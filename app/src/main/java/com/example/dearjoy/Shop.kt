package com.example.dearjoy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.example.shop.CostumeFragment
import com.example.shop.GemsFragment
import com.google.android.material.tabs.TabLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Shop.newInstance] factory method to
 * create an instance of this fragment.
 */
class Shop : Fragment() {

    private var gemsCount = 10
    private lateinit var gemsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        // Inisialisasi TextView untuk jumlah Gems
        gemsTextView = view.findViewById(R.id.gemsText)
        updateGemsDisplay()

        // Inisialisasi TabLayout
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)

        // Muat halaman awal (Costume)
        loadFragment(CostumeFragment())

        // Listener untuk navigasi antar-tab
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

    private fun updateGemsDisplay() {
        gemsTextView.text = gemsCount.toString()
    }

    fun addGems(amount: Int) {
        gemsCount += amount
        updateGemsDisplay()
    }

    fun deductGems(amount: Int) {
        if (gemsCount >= amount) {
            gemsCount -= amount
            updateGemsDisplay()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Shop.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Shop().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}