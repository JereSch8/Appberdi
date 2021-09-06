package com.jackemate.appberdi.ui.mediateca

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ItemGvMediatecaBinding
import com.jackemate.appberdi.entities.ContentMediateca

class GVAdapter(var context: Context, var listContents: List<ContentMediateca>) : BaseAdapter(){
    override fun getCount(): Int {
        return listContents.size
    }

    override fun getItem(position: Int): ContentMediateca {
        return listContents[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ItemGvMediatecaBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        val view : View = binding.root
        val title = binding.title
        val item = getItem(position)

        Glide.with(view)
            .load(item.href)
            .placeholder(R.drawable.loading)
            .error(R.drawable.no_image)
            .transform(CenterCrop())
            .into(binding.img)

        title.text = item.title

        return view;
    }
}