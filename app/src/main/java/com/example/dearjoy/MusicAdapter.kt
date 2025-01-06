package com.example.dearjoy

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MusicAdapter(
    private val dataList: ArrayList<MusicItem>,
    private val context: Context
) : RecyclerView.Adapter<MusicAdapter.ViewHolderClass>() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvImage.setImageResource(currentItem.dataImage)
        holder.rvTitle.text = currentItem.dataTitle
        holder.rvMusic.text = currentItem.dataMusic
        holder.rvDesc.text = currentItem.dataDesc

        // Klik pada ImageButton untuk memutar audio
        holder.playButton.setOnClickListener {
            mediaPlayer?.release() // Hentikan audio sebelumnya jika ada
            mediaPlayer = MediaPlayer.create(context, currentItem.audioResource)
            mediaPlayer?.start()
        }
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvImage: ImageView = itemView.findViewById(R.id.image)
        val rvTitle: TextView = itemView.findViewById(R.id.title)
        val rvMusic: TextView = itemView.findViewById(R.id.isi_music)
        val rvDesc: TextView = itemView.findViewById(R.id.description)
        val playButton: ImageButton = itemView.findViewById(R.id.button_outside) // Ganti Button dengan ImageButton
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
