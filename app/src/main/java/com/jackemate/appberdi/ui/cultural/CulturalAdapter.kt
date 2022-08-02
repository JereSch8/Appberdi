package com.jackemate.appberdi.ui.cultural

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jackemate.appberdi.entities.CulturalActivity

class CulturalAdapter constructor(
    private var list: List<CulturalActivity>
) : RecyclerView.Adapter<CulturalAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(CulturalItem(parent.context))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(private val v: CulturalItem) : RecyclerView.ViewHolder(v) {

        fun bind(item: CulturalActivity) {
            v.set(item)
        }

    }
}