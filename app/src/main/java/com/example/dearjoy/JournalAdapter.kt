package com.example.dearjoy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JournalAdapter(
    private val journalList : List<JournalItem>
) : RecyclerView.Adapter<JournalAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val item = journalList[position]
        holder.textViewMood.text = "Your Feeling: ${convertMoodToText(item.dataMood)}"
        holder.imageMood.setImageResource(convertMoodToImage(item.dataMood))
        holder.textViewStory.text = item.content
        holder.textViewTimestamp.text = "Timestamp: ${item.createdDate}"
    }

    override fun getItemCount(): Int {
        return journalList.size
    }
    class ViewHolderClass(view: View) : RecyclerView.ViewHolder(view) {
        val textViewMood: TextView = view.findViewById(R.id.textViewMood)
        val imageMood: ImageView = view.findViewById(R.id.imageViewIcon)
        val textViewStory: TextView = view.findViewById(R.id.textViewStory)
        val textViewTimestamp: TextView = view.findViewById(R.id.textViewTimestamp)
    }

    private fun convertMoodToText(dataMood: Int?): String {
        return when {
            dataMood == 1 -> "Terrible"
            dataMood == 2 -> "Bad"
            dataMood == 3 -> "Neutral"
            dataMood == 4 -> "Good"
            else -> "Amazing"
        }
    }

    private fun convertMoodToImage(dataMood: Int?): Int {
        return when {
            dataMood == 1 -> R.drawable.mood1
            dataMood == 2 -> R.drawable.mood2
            dataMood == 3 -> R.drawable.mood3
            dataMood == 4 -> R.drawable.mood4
            else -> R.drawable.mood5
        }
    }

}