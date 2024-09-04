package com.example.ocr.home.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ocr.R
import com.example.ocr.home.model.RecentCopy



class RecentCopiesAdapter( private val onCopyClick: (RecentCopy) -> Unit ={}) : ListAdapter<RecentCopy, RecentCopiesAdapter.ViewHolder>(
    RecentCopyDiffUti
) {

    object RecentCopyDiffUti : DiffUtil.ItemCallback<RecentCopy>() {
        override fun areItemsTheSame(oldItem: RecentCopy, newItem: RecentCopy): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecentCopy, newItem: RecentCopy): Boolean {
            return oldItem == newItem
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.copyText)
        val timeView: TextView = view.findViewById(R.id.copyTime)
        val copyIcon: ImageView = view.findViewById(R.id.copyIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_copy, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val copy =getItem(position)
        holder.textView.text = copy.text
        holder.timeView.text = copy.time
        holder.copyIcon.setOnClickListener {
            onCopyClick(copy)
        }
    }

}