package com.example.dearjoy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class AvatarAdapter(
    private val avatarList: List<String>,              // List avatar yang dimiliki user
    private val onAvatarClick: (String) -> Unit        // Callback ketika avatar dipilih
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    private var selectedAvatar: String? = null         // Avatar yang dipilih

    class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
        val selectionOverlay: View = itemView.findViewById(R.id.selectionOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avatar, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatarName = avatarList[position]

        // Mengatur gambar avatar
        val resId = holder.itemView.context.resources.getIdentifier(avatarName, "drawable", holder.itemView.context.packageName)
        holder.avatarImageView.setImageResource(resId)

        // Mengatur overlay jika avatar dipilih
        if (avatarName == selectedAvatar) {
            holder.selectionOverlay.visibility = View.VISIBLE
        } else {
            holder.selectionOverlay.visibility = View.GONE
        }

        // Ketika avatar dipilih
        holder.itemView.setOnClickListener {
            selectedAvatar = avatarName
            onAvatarClick(avatarName)
            notifyDataSetChanged()  // Refresh tampilan untuk update overlay
        }
    }

    override fun getItemCount(): Int = avatarList.size
}
