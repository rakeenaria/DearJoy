package com.example.dearjoy

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestAdapter(
    private val questList: List<Quest>
) : RecyclerView.Adapter<QuestAdapter.QuestViewHolder>() {

    class QuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questTitle: TextView = itemView.findViewById(R.id.questTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quest, parent, false)
        return QuestViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        val quest = questList[position]
        holder.questTitle.text = quest.title

        // Perubahan warna berdasarkan status quest
        if (quest.isCompleted) {
            holder.questTitle.setTextColor(Color.parseColor("#518C4E")) // Warna hijau untuk quest selesai
        } else {
            holder.questTitle.setTextColor(Color.parseColor("#9E9E9E"))  // Warna hitam untuk quest belum selesai
        }
    }

    override fun getItemCount(): Int = questList.size
}
