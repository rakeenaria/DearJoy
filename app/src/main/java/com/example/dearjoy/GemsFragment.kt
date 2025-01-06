package com.example.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dearjoy.R
import com.example.dearjoy.ShopAdapter

class GemsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk Gems
        val view = inflater.inflate(R.layout.fragment_gems, container, false)

        // Inisialisasi RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Data untuk Gems
        val gemsItems = listOf(
            GemsItem(R.drawable.ic_gems, "5 Gems", "Watch Ad"),
            GemsItem(R.drawable.ic_gems, "100 Gems", "Rp21.000,00"),
            GemsItem(R.drawable.ic_packgems, "300 Gems", "Rp56.000,00"),
            GemsItem(R.drawable.ic_bag, "700 Gems", "Rp118.000,00"),
            GemsItem(R.drawable.ic_chest, "1500 Gems", "Rp199.000,00")
        )

        // Set Adapter
        val adapter = ShopAdapter(gemsItems, ShopAdapter.VIEW_TYPE_GEMS)
        recyclerView.adapter = adapter

        return view
    }
}
