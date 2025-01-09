package com.example.dearjoy

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dearjoy.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Home : Fragment() {
    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userRef: DatabaseReference

    // Preferences
    private lateinit var preferences: Preferences

    // Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var questAdapter: QuestAdapter

    // Quest data
    private val questList = mutableListOf(
        Quest("Daily Login", false),
        Quest("Fill Journal", false)
    )

    // Progress tracking variables
    private var isDailyLogged = false
    private var isJournalFilled = false
    private var gemsEarned = 0

    interface OnFragmentInteractionListener {
        fun onGoToAvatar()
        fun onGoToEscapeRoom()
        fun onGoToJournal()
    }

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // UI Components
        val etWelcome = binding.textView
        val selectedAvatarImageView = binding.selectedAvatar
        val progressBar = binding.progressBar
        recyclerView = binding.recyclerView
        val streakCount = view.findViewById<TextView>(R.id.tvStreak)
        val streakIcon = view.findViewById<ImageView>(R.id.streak)

        // Initialize Preferences
        preferences = Preferences(requireContext())
        val userId = preferences.preflevel

        if (userId != null) {
            userRef = database.child("users").child(userId)

            // Fetch username
            userRef.get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val username = dataSnapshot.child("username").value.toString().trim()
                    etWelcome.text = "Hi, $username"
                    fetchGems()
                }
            }

            // Fetch selected avatar and mood
            userRef.get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val selectedAvatar = dataSnapshot.child("selectedAvatar").value?.toString()?.trim() ?: ""
                    val moodToday = dataSnapshot.child("moodToday").value?.toString()?.trim() ?: "mood3" // Default to mood3

                    if (selectedAvatar.isNotEmpty()) {
                        val resId = resources.getIdentifier("${selectedAvatar}_mood$moodToday", "drawable", requireContext().packageName)
                        if (resId != 0) {
                            selectedAvatarImageView.setImageResource(resId)
                        }
                    }
                }
            }

            //Fetch streak data
            userRef.get().addOnSuccessListener { dataSnapshot ->
                if(dataSnapshot.exists()){
                    val streak = dataSnapshot.child("streak").value?.toString()?.toIntOrNull() ?: 0
                    val lastEntryDate = dataSnapshot.child("lastEntryDate").value?.toString()
                    val today = LocalDate.now()

                    // Check streak status
                    if (lastEntryDate != null) {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val lastDate = LocalDate.parse(lastEntryDate, formatter)

                        if (lastDate == today) {
                            streakIcon.setImageResource(R.drawable.streak_active) // Streak aktif
                        } else {
                            streakIcon.setImageResource(R.drawable.streak) // Streak tidak aktif
                        }
                    } else {
                        streakIcon.setImageResource(R.drawable.streak) // Jika tidak ada lastEntryDate
                    }

                    streakCount.text = streak.toString()
                }
            }
        }

        // Panggil fetchQuestStatus untuk memperbarui quest
        updateDailyLoginStatus()

        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        questAdapter = QuestAdapter(questList)
        recyclerView.adapter = questAdapter

        // Initialize progress bar
        progressBar.max = 100
        updateQuestStatus(progressBar)

        // Daily login quest
        if (!isDailyLogged) {
            isDailyLogged = true
            updateQuestStatus(progressBar)
        }

        // Navigation to Avatar page
        binding.avatar.setOnClickListener {
            listener?.onGoToAvatar()
        }

        // Navigation to Escape Room page
        binding.escape.setOnClickListener {
            listener?.onGoToEscapeRoom()
        }

        // Navigation to Journal page
        binding.journal.setOnClickListener {
            listener?.onGoToJournal()
        }

        return view
    }

    private fun updateQuestStatus(progressBar: ProgressBar) {
        var progress = 0
        if (questList[0].isCompleted) progress += 50 // Daily login contributes 50%
        if (questList[1].isCompleted) progress += 50 // Fill journal contributes 50%

        ObjectAnimator.ofInt(progressBar, "progress", progress)
            .setDuration(1000)
            .start()

        if (progress == 100) {
            awardGems()
        }
    }

    private fun fetchGems() {
        userRef.child("gems").get().addOnSuccessListener { snapshot ->
            val gems = snapshot.value?.toString()?.toIntOrNull() ?: 0
            binding.gemsText.text = gems.toString() // Gunakan binding untuk akses gemsText
        }.addOnFailureListener { error ->
            Log.e("Home", "Failed to fetch gems: ${error.message}")
        }
    }

    private fun awardGems() {
        gemsEarned += 10

        // Update gems in Firebase
        val userId = preferences.preflevel
        if (userId != null) {
            val gemsRef = database.child("users").child(userId).child("gems")
            gemsRef.get().addOnSuccessListener { snapshot ->
                val currentGems = snapshot.value?.toString()?.toIntOrNull() ?: 0
                gemsRef.setValue(currentGems + 10)
            }
        }
    }

    private fun fetchQuestStatus() {
        val userId = preferences.preflevel

        if (userId != null) {
            val questRef = database.child("users").child(userId).child("quests")

            questRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val dailyLoginCompleted = snapshot.child("dailyLogin").getValue(Boolean::class.java) ?: false
                    val fillJournalCompleted = snapshot.child("fillJournal").getValue(Boolean::class.java) ?: false

                    // Perbarui status quest di questList
                    questList[0].isCompleted = dailyLoginCompleted
                    questList[1].isCompleted = fillJournalCompleted

                    // Perbarui RecyclerView
                    questAdapter.notifyDataSetChanged()
                }
            }.addOnFailureListener { error ->
                Log.e("Home", "Failed to fetch quest status: ${error.message}")
            }
        }
    }

    private fun updateDailyLoginStatus() {
        val userId = preferences.preflevel
        if (userId != null) {
            val dailyLoginRef =
                database.child("users").child(userId).child("quests").child("dailyLogin")
            dailyLoginRef.setValue(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Home", "Daily login status updated successfully")
                    fetchQuestStatus() // Past
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
