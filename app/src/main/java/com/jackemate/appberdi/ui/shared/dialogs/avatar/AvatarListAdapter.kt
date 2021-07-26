package com.jackemate.appberdi.ui.shared.dialogs.avatar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jackemate.appberdi.databinding.ItemAvatarBinding
import com.jackemate.appberdi.entities.Board
import com.jackemate.appberdi.utils.visible

class AvatarListAdapter constructor(
    private var list: List<Board>,
    private val onClick: (item: Board) -> Unit
) : RecyclerView.Adapter<AvatarListAdapter.AvatarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val v = ItemAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvatarViewHolder(v)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int { return list.size }

    inner class AvatarViewHolder(private val v: ItemAvatarBinding) :
        RecyclerView.ViewHolder(v.root) {
        fun bind(item: Board) {
            v.title.text = item.title
            v.description.visible(item.description.isNotEmpty())
            v.description.text = item.description
            v.img.setAnimation(item.animation)

            v.root.setOnClickListener { onClick(item) }
        }
    }
}