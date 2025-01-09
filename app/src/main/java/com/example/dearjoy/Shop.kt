package com.example.dearjoy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.example.shop.CostumeFragment
import com.example.shop.GemsFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Shop : Fragment() {

    private var gemsCount = 0 // Default gems count
    private lateinit var gemsTextView: TextView

    private lateinit var database: DatabaseReference
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            database = FirebaseDatabase.getInstance().reference
            userRef = database.child("users").child(currentUserId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        // Initialize TextView for Gems
        gemsTextView = view.findViewById(R.id.gemsText)

        // Fetch gems from Firebase and display
        fetchGems()

        // Initialize TabLayout
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)

        // Load initial fragment (Costume)
        loadFragment(CostumeFragment())

        // Listener for tab navigation
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    getString(R.string.avatars) -> loadFragment(CostumeFragment())
                    getString(R.string.gems) -> loadFragment(GemsFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        return view
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    private fun fetchGems() {
        // Fetch gems from Firebase
        userRef.child("gems").get().addOnSuccessListener { snapshot ->
            gemsCount = snapshot.value?.toString()?.toIntOrNull() ?: 0
            updateGemsDisplay()
        }.addOnFailureListener { error ->
            Log.e("Shop", "Failed to fetch gems: ${error.message}")
        }
    }

    private fun updateGemsDisplay() {
        gemsTextView.text = gemsCount.toString()
    }

    fun addGems(amount: Int) {
        gemsCount += amount
        updateGemsDisplay()

        // Update gems in Firebase
        userRef.child("gems").setValue(gemsCount)
    }

    fun deductGems(amount: Int) {
        if (gemsCount >= amount) {
            gemsCount -= amount
            updateGemsDisplay()

            // Update gems in Firebase
            userRef.child("gems").setValue(gemsCount)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Shop().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
