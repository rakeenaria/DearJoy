package com.example.dearjoy

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        preferences = Preferences(this)

        val btnSignUp = findViewById<Button>(R.id.bSignUp)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPass)
        val etPass2 = findViewById<EditText>(R.id.etPass2)

        btnSignUp.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPass.text.toString().trim()
            val confirmPassword = etPass2.text.toString().trim()

            if (validateInput(username, email, password, confirmPassword)) {
                registerUser(username, email, password)
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun validateInput(username: String, email: String, password: String, confirmPassword: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun registerUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val database = FirebaseDatabase.getInstance()
                    val usersRef = database.getReference("users")
                    val userMap = mapOf(
                        "username" to username,
                        "email" to email,
                        "password" to password
                    )
                    if (userId != null) {
                        usersRef.child(userId).setValue(userMap)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Failed to save data: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    // Log error for debugging
                    val error = task.exception
                    Toast.makeText(this, "Sign Up Failed: ${error?.message}", Toast.LENGTH_SHORT).show()
                    error?.printStackTrace()
                }
            }
    }
}