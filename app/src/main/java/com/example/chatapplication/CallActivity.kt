package com.example.chatapplication

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Dispatcher
import java.util.*


class CallActivity : AppCompatActivity() {

    private lateinit var toggleAudioBtn: ImageView
    private lateinit var toggleVideoBtn: ImageView
    private lateinit var webview: WebView
    private lateinit var callLayout: RelativeLayout
    private lateinit var incomingCallTxt: TextView
    private lateinit var acceptBtn: ImageView
    private lateinit var inputLayout: RelativeLayout
    private lateinit var rejectBtn: ImageView
    private lateinit var callControlLayout: RelativeLayout
    private lateinit var callBtn : ImageView

    var name = ""
    var friendsUserName = ""

    var isPeerConnected = false

    var mDbRef = Firebase.database.getReference("user")

    var isAudio = true
    var isVideo = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        name = intent.getStringExtra("name")!!

        callBtn.setOnClickListener {
            sendCallRequest()

        }

        toggleAudioBtn.setOnClickListener {
            isAudio = !isAudio
            callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
            toggleAudioBtn.setImageResource(if (isAudio) R.drawable.ic_baseline_mic_24 else R.drawable.ic_baseline_mic_off_24)
      }

        toggleVideoBtn.setOnClickListener {
            isVideo = !isVideo
            callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
            toggleVideoBtn.setImageResource(if (isVideo) R.drawable.ic_baseline_videocam_24 else R.drawable.ic_baseline_videocam_off_24)
       }

        setupWebView()

    }

    private fun sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You are not connected. Check your internet", Toast.LENGTH_LONG).show()
            return
        }

       // mDbRef.child()

    }



    // @RequiresApi(Build.VERSION_CODES.O)
    private fun setupWebView() {

       webview = findViewById(R.id.webview)
        webview.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant((request.resources))
            }

        }
       webview = findViewById(R.id.webview)
        webview.settings.javaScriptEnabled = true
       webview.settings.mediaPlaybackRequiresUserGesture = false
       webview.addJavascriptInterface(JavascriptInterface(this),"Android")

       loadVideoCall()

    }

    private fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        webview.loadUrl(filePath)

        webview.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }

    }

    var uniqueId = ""

    private fun initializePeer() {

        uniqueId = getUniqueID()

        callJavascriptFunction("javascript:init(\"${uniqueId}\")")
        mDbRef.child("user").child("incoming").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                onCallRequest(snapshot.value as String?)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun onCallRequest(caller: String?) {
        if (caller == null)return

        callLayout.visibility = View.VISIBLE
        incomingCallTxt.text = "$caller is calling..."

        acceptBtn.setOnClickListener{

            mDbRef.child("user").child("connId").setValue(uniqueId)
            mDbRef.child("user").child("isAvailable").setValue(true)

            callLayout.visibility = View.GONE
            switchToControls()

        }

        rejectBtn.setOnClickListener{
            mDbRef.child("user").child("incoming").setValue(null)
            callLayout.visibility = View.GONE
        }

    }

    private fun switchToControls() {
        callControlLayout.visibility = View.VISIBLE
    }

    private fun getUniqueID(): String {
        return UUID.randomUUID().toString()
    }

    private fun callJavascriptFunction(functionString: String){
        webview.post { webview.evaluateJavascript(functionString,null) }
    }

    fun onPeerConnected() {
        isPeerConnected = true
    }


}





