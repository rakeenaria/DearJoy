package com.example.dearjoy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryJournal : Fragment() {

    private var journalList = ArrayList<JournalItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_journal, container, false)

        val selectedDate = arguments?.getString("selectedDate")
        if (selectedDate != null) {
            fetchJournalDataForDate(selectedDate)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun fetchJournalDataForDate(date: String) {
        val userId=getCurrentUserID()
        FirebaseDatabase.getInstance().reference.child("journal")
            .orderByChild("createdDate")
            .startAt("$date 00:00:00")
            .endAt("$date 23:59:59")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        journalList.clear()
                        for (journalSnapshot in snapshot.children) {
                            val journal = journalSnapshot.getValue(JournalItem::class.java)
                            if (journal != null && journal.userID == getCurrentUserID()) {
                                journalList.add(journal)
                                // Fetch mood data for the journal
                                fetchMoodForJournal(journal.journalID)
                            }
                        }
                        displayJournals(journalList)
                    } else {
                        Log.d("JournalData", "No data found for date: $date")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("JournalData", "Error fetching data: ${error.message}")
                }
            })
    }

    private fun fetchMoodForJournal(journalId: String?) {
        if (journalId == null) return

        FirebaseDatabase.getInstance().reference.child("mood")
            .orderByChild("journalID")
            .equalTo(journalId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        return
                    }

                    for (moodSnapshot in snapshot.children) {
                        val moodType = moodSnapshot.child("moodType").getValue(Int::class.java)?.toFloat()
                        if (moodType != null) {
                            // Update journal with mood data
                            Log.d("MoodData", "moodType ${moodType}")
                            updateJournalWithMood(journalId, moodType)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoodData", "Database error: ${error.message}")
                }
            })
    }


    private fun updateJournalWithMood(journalId: String, moodType: Float) {
        // Cari jurnal yang memiliki journalId yang sama dengan journalId
        val updatedJournal = journalList.find { it.journalID == journalId }
        updatedJournal?.dataMood = moodType.toInt()  // Update moodType pada jurnal

        // Cari index item yang sesuai dengan journalId
        val index = journalList.indexOf(updatedJournal)

        // Jika ditemukan, update item dan beri tahu adapter untuk memperbarui item tersebut
        if (index != -1) {
            val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewHistory)
            recyclerView?.adapter?.notifyItemChanged(index)  // Update item yang sesuai
        }
    }


    private fun getCurrentUserID(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid
    }

    private fun displayJournals(journals: List<JournalItem>) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewHistory)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = JournalAdapter(journals)
    }
}


