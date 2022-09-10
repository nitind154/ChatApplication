package com.example.chatapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var tvUserName: TextView
    private lateinit var imgBack: ImageView
    private lateinit var imgUserProfile: ImageView
    private var topic =""
    //var userName = intent.getStringExtra("userName")

    private var firebaseUser: FirebaseUser? = null
    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    private val requestcode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intent = intent
        val userId = intent.getStringExtra("userId")


        firebaseUser = FirebaseAuth.getInstance().currentUser
        mDbRef = FirebaseDatabase.getInstance().getReference("user").child(userId!!)

//        val receiverUid = intent.getStringExtra("userId")
//        val senderUid = FirebaseAuth.getInstance().currentUser!!.uid
//        mDbRef = FirebaseDatabase.getInstance().getReference("user").child(receiverUid!!)



        // setting reciever name as title

        tvUserName = findViewById(R.id.tvUserName)
        imgUserProfile = findViewById(R.id.imgUserProfile)
        imgUserProfile.setOnClickListener {
            val intent =Intent(this@ChatActivity,Profile::class.java)
            startActivity(intent)
            finish()
        }

        mDbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = snapshot.getValue(User::class.java)
                tvUserName.text = currentUser!!.userName

                    if (currentUser.profileImage.equals("")){
                    imgUserProfile.setImageResource(R.mipmap.ic_launcher)
                    }else{
                    Glide.with(this@ChatActivity).load(currentUser.profileImage).into(imgUserProfile)
                            }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
//
//        senderRoom = receiverUid + senderUid
//        receiverRoom = senderUid + receiverUid

        // customizing toolbar
       // supportActionBar?.title = name

        //val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
       // setSupportActionBar(toolbar)
       // supportActionBar!!.setDisplayShowHomeEnabled(true)
        //supportActionBar!!.setLogo(R.drawable.logo)
       // supportActionBar!!.setDisplayUseLogoEnabled(true)
        //actionBar.setDisplayShowCustomEnabled(true)
        //videocall = findViewById(R)

        //toolbarusername = findViewById(R.id.toolbarusername)
       // toolbarusername.add(name)

        if (!isPermissionsGranted()){
            askPermission()
        }

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        //logic for adding data recyclerView

//
//        mDbRef = FirebaseDatabase.getInstance().getReference("chats")
//        mDbRef.child("chat").child(senderRoom!!).child("messages")
//            .addValueEventListener(object: ValueEventListener{
//                @SuppressLint("NotifyDataSetChanged")
//                override fun onDataChange(snapshot: DataSnapshot) {
//
//                    messageList.clear()
//
//                    for (postSnapshot in snapshot.children){
//
//                        val message = postSnapshot.getValue(Message::class.java)
//                        messageList.add(message!!)
//                    }
//                    messageAdapter.notifyDataSetChanged()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//
//            })

        imgBack =findViewById(R.id.imgBack)
        imgBack.setOnClickListener {
            val intent =Intent(this@ChatActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //adding the message to database
        sendButton.setOnClickListener{

            val message:String = messageBox.text.toString()

            if (message.isEmpty()){
                Toast.makeText(applicationContext,"message is empty",Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(firebaseUser!!.uid,userId,message)
//            val messageObject = Message(message,senderUid,receiverUid)
//
//            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
//                .setValue(messageObject).addOnSuccessListener {
//                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
//                        .setValue(messageObject)
 //               }
            messageBox.setText("")
            topic = "/topics/$userId"
            PushNotification(NotificationData("bbm",message),
                topic).also{
                    sendNotification(it)
            }
            }
        }

        readMessage(firebaseUser!!.uid,userId)


      //  Firebase.initialize(this)

//        iconvideocall = findViewById(R.id.iconvideocall)
//        iconvideocall.setOnClickListener{
//            val username = name
//            val intent = Intent(this, CallActivity::class.java)
//            intent.putExtra("name", name)
//            startActivity(intent)
//        }

    }

    private fun isPermissionsGranted(): Boolean {

        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(this,it) != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
                this,
                permissions,
                requestcode
        )
    }

    private fun sendMessage(senderId:String,receiverId:String,message:String){
        val mDbRef:DatabaseReference = FirebaseDatabase.getInstance().reference

        val hashMap:HashMap<String,String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message

        mDbRef.child("Chat").push().setValue(hashMap)

    }

    fun readMessage(senderId: String,receiverId: String){
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

            databaseReference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()
                    for(dataSnapshot in snapshot.children){

                        val chat = dataSnapshot.getValue(Message::class.java)
                        //  if (!currentUser!!.uid.equals(mAuth.uid))
                        if (chat!!.senderId.equals(senderId) && chat.receiverId.equals(receiverId) ||
                                chat.senderId.equals(receiverId) && chat.receiverId.equals(senderId)){
                            messageList.add(chat)
                        }

                    }

                    val messageAdapter = MessageAdapter(this@ChatActivity,messageList)
                    chatRecyclerView.adapter = messageAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch{
        try{
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful){
                Toast.makeText(this@ChatActivity,"Response ${Gson().toJson(response)}",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@ChatActivity, response.errorBody().toString(),Toast.LENGTH_SHORT).show()

            }
        }catch (e:Exception){
//            Toast.makeText(this@ChatActivity,e.message,Toast.LENGTH_SHORT).show()
        }
    }


}