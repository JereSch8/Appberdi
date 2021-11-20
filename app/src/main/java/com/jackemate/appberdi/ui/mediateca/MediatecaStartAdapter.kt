package com.jackemate.appberdi.ui.mediateca

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ItemGvMediatecaBinding
import com.jackemate.appberdi.entities.ContentMediateca

class MediatecaStartAdapter(
    private val contents: List<ContentMediateca>,
    val onClick: (ContentMediateca) -> Unit
) :
    RecyclerView.Adapter<MediatecaStartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemGvMediatecaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contents[position])
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    inner class ViewHolder(private val binding: ItemGvMediatecaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ContentMediateca) {
            Glide.with(binding.root)
                .load(item.href)
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .transform(CenterCrop())
                .into(binding.img)

            binding.title.text = item.title
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}