package com.example.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dearjoy.CostumeItem
import com.example.dearjoy.R

class CostumeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk Costume
        val view = inflater.inflate(R.layout.fragment_costume, container, false)

        // Inisialisasi RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Data untuk Costume
        val costumeItems = listOf(
            CostumeItem(R.drawable.ic_daniel, "Daniel", "100"),
            CostumeItem(R.drawable.ic_sara, "Sara", "100"),
            CostumeItem(R.drawable.ic_jacob, "Jacob", "100"),
            CostumeItem(R.drawable.ic_kevin, "Kevin", "100")
        )

        // Set Adapter
        val adapter = CostumeAdapter(costumeItems)
        recyclerView.adapter = adapter

        return view
    }
}
