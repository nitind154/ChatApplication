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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class  SignUp : AppCompatActivity() {


    private lateinit var edtEmail: EditText
    private lateinit var edtName: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()


        mAuth = FirebaseAuth.getInstance()

        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btn_SignUp)

        btnSignUp.setOnClickListener {
            val userName = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "email is Required", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(applicationContext, "username is Required", Toast.LENGTH_SHORT)
                    .show()
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "password is Required", Toast.LENGTH_SHORT)
                    .show()
            }

            signUp(userName, email, password)

               val intent = Intent(this@SignUp, MainActivity::class.java)
               startActivity(intent)
            //   finish()

        }


    }

    private fun signUp(userName: String, email: String, password: String) {
        // logic for signup and creating new accounts type
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign is success, update UI with the signed-in user's information
                    val user: FirebaseUser? = mAuth.currentUser
                    val userId: String = user!!.uid

                    mDbRef = FirebaseDatabase.getInstance().getReference("user").child(userId)

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("userId", userId)
                    hashMap.put("userName", userName)
                    hashMap.put("profileImage", "")
                    hashMap.put("email", email)

                    // code for jumping to home
                    mDbRef.setValue(hashMap).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            edtName.setText("")
                            edtEmail.setText("")
                            edtPassword.setText("")
//                            val intent = Intent(this@SignUp, MainActivity::class.java)
//                            startActivity(intent)
                        }
                    }


                    }



                }
            }
    }



