package com.example.chatapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LogIn : AppCompatActivity() {

        private lateinit var edtEmail : EditText
        private lateinit var edtPassword : EditText
        private lateinit var btnLogin : Button
        private lateinit var btnSignUp : Button
        private lateinit var mAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
         supportActionBar?.show()

        mAuth = FirebaseAuth.getInstance()
        val firebaseUser = mAuth.currentUser

        //check if the user is log in then navigate to user screen
        if (firebaseUser != null) {
            val intent = Intent(this@LogIn,
                MainActivity::class.java
            )
            startActivity(intent)
        }


        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btn_SignUp)

        btnSignUp.setOnClickListener { 
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext,"email and password are required",Toast.LENGTH_SHORT).show()
            }else{

                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful){
                            edtEmail.setText("")
                            edtPassword.setText("")
                                val intent = Intent(this@LogIn,MainActivity::class.java)
                                startActivity(intent)

                            } else  {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(this@LogIn, "email or password invalid",
                                    Toast.LENGTH_SHORT).show()

                        }
                        }


            }

            //either u can call function
           // login(email,password)
        }

    }
   // private fun login(email: String, password:String){
       // login for loggin in user
     //   mAuth.signInWithEmailAndPassword(email, password)
       //     .addOnCompleteListener(this) { task ->
         //       if (task.isSuccessful) {
           //         val intent = Intent(this@LogIn, MainActivity::class.java)
                    //finish()
             //       startActivity(intent)

//                } else {
  //                  // If sign in fails, display a message to the user.
//
  //                  Toast.makeText(this@LogIn, "User does not exist",
    //                    Toast.LENGTH_SHORT).show()

        //        }
      //      }
   // }
}