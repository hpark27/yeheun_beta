package com.beta.yeheun

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdapter(private var list: MutableList<Int>) : RecyclerView.Adapter<ViewPagerAdapter.InfiniteViewHolder>() {

    inner class InfiniteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imageViewBanner = itemView.findViewById<ImageView>(R.id.banner_item)

        fun onBind(res: Int) {
            imageViewBanner.setImageResource(res)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfiniteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.banner, parent, false)
        return InfiniteViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfiniteViewHolder, position: Int) {
        // use the value that current position is divided by 5 (hard coding)
        // since 5 is the maximum number for this project
        holder.onBind(list[position % 5])
    }

    // Increase the item size to a randomly large number
    override fun getItemCount(): Int = Int.MAX_VALUE
}