package com.example.dearjoy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shop.GemsItem

class ShopAdapter<T>(
    private val items: List<T>,
    private val viewType: Int
) : RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_COSTUME = 1
        const val VIEW_TYPE_GEMS = 2
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val priceTextView: TextView? = itemView.findViewById(R.id.priceTextView) // Untuk Costume
        val actionButton: TextView? = itemView.findViewById(R.id.actionButton) // Untuk Gems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = if (viewType == VIEW_TYPE_COSTUME) {
            R.layout.item_costume
        } else {
            R.layout.item_gems
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        when (viewType) {
            VIEW_TYPE_COSTUME -> {
                val costumeItem = item as CostumeItem
                holder.iconImageView.setImageResource(costumeItem.iconResId)
                holder.titleTextView.text = costumeItem.title
                holder.priceTextView?.text = costumeItem.price
            }

            VIEW_TYPE_GEMS -> {
                val gemsItem = item as GemsItem
                holder.iconImageView.setImageResource(gemsItem.iconResId)
                holder.titleTextView.text = gemsItem.title
                holder.actionButton?.text = gemsItem.actionText
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = viewType
}
