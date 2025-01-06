package com.example.dearjoy

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EscapeRoom : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var dataList: ArrayList<MusicItem>
    lateinit var imageList: Array<Int>
    lateinit var titleList: Array<String>
    lateinit var musicList: Array<String>
    lateinit var descList: Array<String>
    lateinit var audioList: Array<Int>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_escape_room, container, false)

        val backButton: ImageButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        // Initialize data
        imageList = arrayOf(
            R.drawable.calm,
            R.drawable.breathe,
            R.drawable.ocean
        )
        titleList = arrayOf(
            "Calm Beginnings",
            "Breathe & Balance",
            "Ocean of Peace"
        )
        musicList = arrayOf(
            "Gentle instruments, chirping birds, and flowing water",
            "Instrumental piano music with a rhythmic breathing pace",
            "Waves, gentle wind, and positive affirmations"
        )
        descList = arrayOf(
            "A quick meditation to start your day peacefully",
            "A guided breathing session to align your body and mind",
            "A relaxation meditation with ocean sounds and soft narration"
        )
        audioList = arrayOf(
            R.raw.birds_sound,
            R.raw.piano_sound,
            R.raw.ocean_sound
        )

        dataList = arrayListOf()
        getData()

        musicAdapter = MusicAdapter(dataList, requireContext())
        recyclerView.adapter = musicAdapter

        val btnFeelNFill: CardView = view.findViewById(R.id.card_view2)
        btnFeelNFill.setOnClickListener {
            listener?.onGoToFeelNFill()
        }

        val btnPopIt: CardView = view.findViewById(R.id.card_view1)
        btnPopIt.setOnClickListener {
            listener?.onGoToPopIt()
        }

        return view
    }

    private fun getData() {
        for (i in imageList.indices) {
            val dataClass = MusicItem(imageList[i], titleList[i], musicList[i], descList[i], audioList[i])
            dataList.add(dataClass)
        }
        recyclerView.adapter = MusicAdapter(dataList, requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (recyclerView.adapter as? MusicAdapter)?.releaseMediaPlayer()
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onGoToFeelNFill()
        fun onGoToPopIt()
    }
}
