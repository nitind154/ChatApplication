package com.example.chatapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.firebase.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class MainActivity : AppCompatActivity() {

    private  lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mStrRef: StorageReference
    private lateinit var imgBack1: ImageView
    private lateinit var imgMenu : ImageView


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            
            FirebaseService.token = it.token
            Log.e("MainActivity" , it.token)
        }


        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        mStrRef = FirebaseStorage.getInstance().reference

        imgMenu = findViewById(R.id.imgMenu)
        imgMenu.setOnClickListener {

        }

        imgBack1 =findViewById(R.id.imgBack1)
        imgBack1.setOnClickListener {
            val intent =Intent(this@MainActivity,LogIn::class.java)
            startActivity(intent)
            finish()
        }


        userList = ArrayList()
        adapter = UserAdapter(this,userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)
        userRecyclerView.adapter = adapter

        mDbRef.child("user").addValueEventListener(object: ValueEventListener{

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()

            }


            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                  //  if (!currentUser!!.uid.equals(mAuth.uid))
                   if (mAuth.currentUser?.uid != currentUser!!.userId)
                    {
                        val userid = mAuth.uid
                        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")
                        userList.add(currentUser)
                    }
                }
                adapter.notifyDataSetChanged()

//                val user = snapshot.getValue(User::class.java)
//
//                    if (user!!.profileImage?.equals("") == true){
//                    imgUserProfile.setImageResource(R.mipmap.ic_launcher)
//                    }else{
//                    Glide.with(this@MainActivity).load(user.profileImage).into(imgUserProfile)
//                            }

            }

        })
    }

//    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.menu,menu)
//        return super.onCreateOptionsMenu(menu)
//    }

}







