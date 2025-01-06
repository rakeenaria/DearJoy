package com.example.dearjoy

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class Settings : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var preferences: Preferences
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is com.example.dearjoy.Settings.OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val etUsername = view.findViewById<EditText>(R.id.tvUsername)
        val etEmail = view.findViewById<EditText>(R.id.tvEmail)
        val etPassword = view.findViewById<EditText>(R.id.tvPass)

        preferences = Preferences(requireContext())

        val userId = preferences.preflevel

        if(userId != null){
            val userRef = database.getReference("users").child(userId)
            userRef.get().addOnSuccessListener { dataSnapshot ->
                if(dataSnapshot.exists()){
                    val username = dataSnapshot.child("username").value.toString().trim()
                    val email = dataSnapshot.child("email").value.toString().trim()
                    val password = dataSnapshot.child("password").value.toString().trim()

                    etUsername.setText(username)
                    etEmail.setText(email)
                    etPassword.setText(password)
                }
            }
        }

        val btnSignOut: CardView = view.findViewById(R.id.signout)
        btnSignOut.setOnClickListener {
            listener?.onGoToSignIn()
        }
        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onGoToSignIn()
    }
}