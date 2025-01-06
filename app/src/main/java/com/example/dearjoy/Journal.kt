package com.example.dearjoy

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class Journal : Fragment() {
    private lateinit var database: FirebaseDatabase

    interface OnFragmentInteractionListener {
        fun onGoToHome()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_journal, container, false)

        // Tombol kembali
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            // Kembali ke fragmen sebelumnya di back stack
            parentFragmentManager.popBackStack()
        }

        val btnJournal: Button = view.findViewById(R.id.bSubmit)
        btnJournal.setOnClickListener {
            saveJournal(view)
        }

        val moodButtons = listOf(
            Pair(view.findViewById<ImageButton>(R.id.mood1), 1),
            Pair(view.findViewById<ImageButton>(R.id.mood2), 2),
            Pair(view.findViewById<ImageButton>(R.id.mood3), 3),
            Pair(view.findViewById<ImageButton>(R.id.mood4), 4),
            Pair(view.findViewById<ImageButton>(R.id.mood5), 5)
        )

        // Set click listeners to mood buttons
        moodButtons.forEach { (button, _) ->
            button.setOnClickListener {
                updateMoodSelection(button, moodButtons.map { it.first })
            }
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveJournal(view: View) {
        val journalContent = view.findViewById<EditText>(R.id.etJornal)?.text.toString()
        val moodType = getSelectedMoodType(view) // Mendapatkan mood dari tombol yang dipilih
        val userId = FirebaseAuth.getInstance().currentUser?.uid // ID user yang sedang login

        if (journalContent.isNotEmpty() && moodType != null && userId != null) {
            val journalRef = database.getReference("journal").push() // Membuat ID unik untuk journal
            val journalId = journalRef.key ?: return

            val moodRef = database.getReference("mood").push() // Membuat ID unik untuk mood
            val moodId = moodRef.key ?: return
            val moodNowRef = database.getReference("users").child(userId).child("moodToday")

            val userRef = database.getReference("users").child(userId) // Referensi ke data pengguna

            // Format tanggal
            val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            userRef.get().addOnSuccessListener { snapshot ->
                var streak = snapshot.child("streak").getValue(Int::class.java) ?: 1
                val lastEntryDate = snapshot.child("lastEntryDate").getValue(String::class.java)

                // Hitung streak
                if (lastEntryDate != null) {
                    val lastDate = LocalDate.parse(lastEntryDate)
                    val daysDifference = ChronoUnit.DAYS.between(lastDate, LocalDate.now())

                    streak = when {
                        daysDifference == 1L -> streak + 1  // Tambah streak
                        daysDifference > 1L -> 1           // Reset streak
                        else -> streak                     // Hari ini sudah tercatat
                    }
                } else {
                    1
                }

                // Data journal
                val journalData = mapOf(
                    "createdDate" to currentDateTime, // Menggunakan format yang lebih mudah dibaca
                    "content" to journalContent,
                    "userID" to userId
                )

                // Data mood
                val moodData = mapOf(
                    "moodType" to moodType,
                    "journalID" to journalId
                )

                // Simpan ke Firebase
                journalRef.setValue(journalData).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        userRef.updateChildren(
                            mapOf(
                                "streak" to streak,
                                "lastEntryDate" to LocalDate.now().toString()
                            )
                        ).addOnCompleteListener { streakTask ->
                            if (streakTask.isSuccessful){
                                moodNowRef.setValue(moodType).addOnCompleteListener { moodTask ->
                                    if (moodTask.isSuccessful) {
                                        moodRef.setValue(moodData)
                                            .addOnCompleteListener { finalMoodTask ->
                                                if (finalMoodTask.isSuccessful) {
                                                    showMessage("Journal and mood saved successfully!")
                                                    resetForm(view)
                                                    navigateToHome()
                                                } else {
                                                    showMessage("Failed to save mood: ${finalMoodTask.exception?.message}")
                                                }
                                            }
                                    } else {
                                        showMessage("Failed to save mood: ${moodTask.exception?.message}")
                                    }

                                }
                            }
                        }
                    } else {
                        showMessage("Failed to save journal: ${task.exception?.message}")
                    }
                }
            }
        } else {
            showMessage("Please fill in all fields.")
        }
    }

    private fun navigateToHome() {
        val activity = requireActivity()
        if (activity is MainActivity) {
            activity.onGoToHome()
        }
    }

    // Fungsi untuk mendapatkan mood dari tombol yang dipilih
    private fun getSelectedMoodType(view: View): Int? {
        val moodButtons = listOf(
            Pair(view.findViewById<ImageButton>(R.id.mood1), 1),
            Pair(view.findViewById<ImageButton>(R.id.mood2), 2),
            Pair(view.findViewById<ImageButton>(R.id.mood3), 3),
            Pair(view.findViewById<ImageButton>(R.id.mood4), 4),
            Pair(view.findViewById<ImageButton>(R.id.mood5), 5)
        )

        return moodButtons.firstOrNull { it.first.isSelected }?.second
    }

    // Fungsi untuk menampilkan pesan
    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk memperbarui status pilihan mood
    private fun updateMoodSelection(selectedButton: ImageButton, allButtons: List<ImageButton>) {
        // Reset semua tombol mood
        allButtons.forEach { button ->
            button.isSelected = false
            button.setBackgroundColor(resources.getColor(R.color.white)) // Atur ulang background ke default
        }

        // Set tombol yang dipilih
        selectedButton.isSelected = true
        selectedButton.setBackgroundColor(resources.getColor(R.color.green)) // Ganti background untuk tombol yang dipilih
    }

    private fun resetForm(view: View) {
        // Reset EditText
        val journalEditText: EditText = view.findViewById(R.id.etJornal)
        journalEditText.text.clear()

        // Reset semua tombol mood
        val moodButtons = listOf(
            view.findViewById<ImageButton>(R.id.mood1),
            view.findViewById<ImageButton>(R.id.mood2),
            view.findViewById<ImageButton>(R.id.mood3),
            view.findViewById<ImageButton>(R.id.mood4),
            view.findViewById<ImageButton>(R.id.mood5)
        )

        moodButtons.forEach { button ->
            button.isSelected = false
            button.setBackgroundColor(resources.getColor(R.color.white)) // Reset warna ke default
        }
    }
}
