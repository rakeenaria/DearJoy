package com.example.dearjoy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    // Inisialisasi FirebaseAuth
    private lateinit var auth: FirebaseAuth
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()
        preferences = Preferences(this)

        // Inisialisasi elemen layout
        val btnSignIn = findViewById<Button>(R.id.bSignIn)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        val etEmail = findViewById<EditText>(R.id.etEmailIn)
        val etPass = findViewById<EditText>(R.id.etPassIn)

        // Tombol Sign In
        btnSignIn.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPass.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                signInUser(email, password)
            }
        }

        // Tombol Sign Up
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if(userId != null){
                        preferences.prefStatus = true
                        preferences.preflevel = userId
                    }

                    // Berhasil login
                    Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Gagal login
                    val error = task.exception?.message
                    Toast.makeText(this, "Sign In Failed: $error", Toast.LENGTH_SHORT).show()
                    Log.e("SignInActivity", "Error: $error")
                }
            }
    }
}