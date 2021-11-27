package com.jackemate.appberdi.ui.mediateca

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.ItemMediatecaSiteBinding
import com.jackemate.appberdi.entities.Content
import java.lang.Integer.max

class MediatecaSiteAdapter(
    private val context: Context,
    private var listMultimedia: List<Content>,
    private val listener: (Content) -> Unit
) : RecyclerView.Adapter<MediatecaSiteAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemMediatecaSiteBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(listMultimedia[position])
    }

    fun updateItems(list: List<Content>) {
        val temp = listMultimedia
        listMultimedia = list
        notifyItemRangeChanged(0, max(temp.size, list.size))
    }

    override fun getItemCount(): Int {
        return listMultimedia.size
    }

    inner class Holder(val binding: ItemMediatecaSiteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Content) {
            binding.title.text = item.title
            binding.description.text = when (item) {
                is Content.Audio -> item.subtitle
                is Content.Video -> item.description
                is Content.Gif -> item.description
                is Content.Image -> item.description
                is Content.Text -> item.description
                else -> ""
            }
            binding.typeData.cancelAnimation()
            binding.typeData.clearAnimation()
            binding.typeData.setAnimation(
                when (item) {
                    is Content.Audio -> R.raw.audio
                    is Content.Video -> R.raw.video
                    is Content.Gif -> R.raw.video
                    is Content.Image -> R.raw.image
                    is Content.Text -> R.raw.text
                    else -> R.raw.problem
                }
            )

            /* White color
            binding.typeData.addValueCallback(
                KeyPath("**"),
                LottieProperty.COLOR,
                { ContextCompat.getColor(context, R.color.white) }
            )
            binding.typeData.addValueCallback(
                KeyPath("**"),
                LottieProperty.STROKE_COLOR,
                { ContextCompat.getColor(context, R.color.white) }
            )*/

            // Play animation on startup
            binding.typeData.playAnimation()

            itemView.setOnTouchListener { view, event ->
                return@setOnTouchListener when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Play animation on touch
                        binding.typeData.playAnimation()
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        true
                    }
                    else -> false
                }
            }
            itemView.setOnClickListener { listener(item) }
        }

    }

}