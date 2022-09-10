package com.example.chatapplication

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.gms.dynamite.DynamiteModule.load
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import io.grpc.InternalServiceProviders.load
import java.io.IOException
import java.lang.System.load
import java.util.*
import java.util.ServiceLoader.load
import kotlin.collections.HashMap

class Profile : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var mStrRef: StorageReference
    private lateinit var progressBar:ProgressBar
    private lateinit var mAuth:FirebaseAuth
    private lateinit var etuserName : TextView

    private lateinit var imgBackBtn:ImageView
    private lateinit var imgUserProfile: ImageView
    private lateinit var btnSave:Button

    private var filePath:Uri? = null

    private val PICK_IMAGE_REQUEST:Int =2020
    //private lateinit var imgProfile:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        btnSave = findViewById(R.id.btnSave)
        progressBar = findViewById(R.id.progressBar)

        mAuth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        etuserName = findViewById(R.id.etuserName)
        imgUserProfile = findViewById(R.id.imgUserProfile)
        storage = FirebaseStorage.getInstance()
        mStrRef = storage.reference
        mDbRef = FirebaseDatabase.getInstance().getReference("user").child(firebaseUser.uid)

        mDbRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (firebaseUser.uid  != user?.uid)
                {
                    etuserName.text = user?.userName
                }
                  //  userList.add(currentUser)


                if (firebaseUser.uid != user?.profileImage){
                    imgUserProfile.setImageResource(R.mipmap.ic_launcher)
                    }else{
                        Glide.with(this@Profile).load(user.profileImage).into(imgUserProfile)
                    }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
            }

        })

        imgUserProfile =findViewById(R.id.imgUserProfile)
        imgUserProfile.setOnClickListener {
        val intent =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            Intent(this@Profile,Profile::class.java)
         chooseImage()
            finish()
  }

    imgBackBtn =findViewById(R.id.imgBackBtn)
    imgBackBtn.setOnClickListener {
        val intent =Intent(this@Profile,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

        btnSave.setOnClickListener {
            uploadImage()
            progressBar.visibility = View.VISIBLE

        }

    }

    // intent gallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    private fun chooseImage(){
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        @Suppress("DEPRECATION")
        startActivityForResult(Intent.createChooser(intent,"Select Image"),PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST){
            filePath = data!!.data
            try {
                @Suppress("DEPRECATION") val bitmap:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                imgUserProfile.setImageBitmap(bitmap)
                btnSave.visibility = View.VISIBLE
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        if (filePath != null){
            val ref:StorageReference = mStrRef.child("image/"+ UUID.randomUUID().toString())
            ref.putFile(filePath!!).addOnSuccessListener {

                val hashMap: HashMap<String, String> = HashMap()
                hashMap["profileImage"] = ""
                hashMap["userName"] = etuserName.text.toString()
                mDbRef.updateChildren(hashMap as Map<String, Any>)

                progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext,"Uploaded",Toast.LENGTH_SHORT).show()
                    btnSave.visibility = View.GONE


            }
                    .addOnFailureListener{
                            progressBar.visibility = View.GONE
                            Toast.makeText(applicationContext,"Failed"+it.message,Toast.LENGTH_SHORT).show()
                    }



        }
    }

}

