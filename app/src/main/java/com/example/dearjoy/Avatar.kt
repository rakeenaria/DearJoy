package com.example.dearjoy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Avatar : Fragment() {

    // Firebase Database
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userRef: DatabaseReference

    // UI Components
    private lateinit var selectedAvatarImageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var avatarAdapter: AvatarAdapter

    // Data
    private val avatarList = mutableListOf<String>()
    private var selectedAvatar: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        database = FirebaseDatabase.getInstance().reference
        userRef = database.child("users").child(userId ?: "")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_avatar, container, false)

        // Tombol kembali
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack() // Kembali ke fragment sebelumnya
        }

        // Initialize UI components
        selectedAvatarImageView = view.findViewById(R.id.selectedAvatar)
        recyclerView = view.findViewById(R.id.avatarRecyclerView)

        // Set up RecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        avatarAdapter = AvatarAdapter(avatarList) { avatar ->
            updateSelectedAvatar(avatar)
        }
        recyclerView.adapter = avatarAdapter

        // Fetch avatar list and selected avatar
        fetchAvatarsAndSelectedAvatar()

        return view
    }

    private fun fetchAvatarsAndSelectedAvatar() {
        // Fetch avatar list
        database.child("avatar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                avatarList.clear() // Clear list before adding new data
                for (childSnapshot in snapshot.children) {
                    val avatar = childSnapshot.getValue(String::class.java)
                    avatar?.let { avatarList.add(it) }
                }
                avatarAdapter.notifyDataSetChanged() // Notify adapter to update RecyclerView
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Avatar", "Failed to fetch avatars: ${error.message}")
                Toast.makeText(requireContext(), "Failed to load avatars", Toast.LENGTH_SHORT).show()
            }
        })

        // Fetch selected avatar
        userRef.child("selectedAvatar").get().addOnSuccessListener { snapshot ->
            val avatar = snapshot.value?.toString()?.trim() ?: ""
            if (avatar.isNotEmpty()) {
                selectedAvatar = avatar
                updateSelectedAvatarImage(avatar)
            }
        }.addOnFailureListener { error ->
            Log.e("Avatar", "Failed to fetch selected avatar: ${error.message}")
        }
    }

    private fun updateSelectedAvatar(avatar: String) {
        selectedAvatar = avatar
        updateSelectedAvatarImage(avatar)

        // Update selected avatar in Firebase
        userRef.child("selectedAvatar").setValue(avatar).addOnSuccessListener {
            Toast.makeText(requireContext(), "Avatar Selected: $avatar", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { error ->
            Log.e("Avatar", "Failed to update selected avatar: ${error.message}")
        }
    }

    private fun updateSelectedAvatarImage(avatar: String) {
        val resId = resources.getIdentifier(avatar, "drawable", requireContext().packageName)
        if (resId != 0) {
            selectedAvatarImageView.setImageResource(resId)
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Avatar().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
