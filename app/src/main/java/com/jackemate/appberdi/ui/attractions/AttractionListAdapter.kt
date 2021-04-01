package com.jackemate.appberdi.ui.attractions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jackemate.appberdi.R
import com.jackemate.appberdi.domain.entities.Attraction

class AttractionListAdapter constructor(
    private var list: List<Attraction>,
    private val onClick: (item: Attraction) -> Unit
) : RecyclerView.Adapter<AttractionListAdapter.AttractionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_attraction, parent, false)
        return AttractionViewHolder(v)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(list: List<Attraction>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class AttractionViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bind(item: Attraction) {
            itemView.findViewById<TextView>(R.id.title).text = item.name
            itemView.findViewById<TextView>(R.id.description).text = item.description
            itemView.setOnClickListener { onClick(item) }
        }
    }
}