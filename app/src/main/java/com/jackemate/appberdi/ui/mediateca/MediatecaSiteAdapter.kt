package com.jackemate.appberdi.ui.mediateca

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.jackemate.appberdi.R
import com.jackemate.appberdi.entities.Content
import java.lang.Integer.max

class MediatecaSiteAdapter(
    private val context: Context,
    private var listMultimedia: List<Content>,
    private val listener: (Content) -> Unit
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return MultiMediaViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_mediateca_site, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is MultiMediaViewHolder -> holder.bind(listMultimedia[position])
        }
    }

    fun updateItems(list: List<Content>) {
        val temp = listMultimedia
        listMultimedia = list
        notifyItemRangeChanged(0, max(temp.size, list.size))
    }

    override fun getItemCount(): Int {
        return listMultimedia.size
    }

    inner class MultiMediaViewHolder(itemView: View) : BaseViewHolder<Content>(itemView) {
        override fun bind(item: Content) {
            itemView.findViewById<TextView>(R.id.title).text = item.title
            itemView.findViewById<TextView>(R.id.description).text = when (item) {
                is Content.Audio -> item.subtitle
                is Content.Video -> item.description
                is Content.Gif -> item.description
                is Content.Image -> item.description
                is Content.Text -> item.description
                else -> ""
            }

            itemView.findViewById<LottieAnimationView>(R.id.typeData).setAnimation(
                when (item) {
                    is Content.Audio -> R.raw.audio
                    is Content.Video -> R.raw.video
                    is Content.Gif -> R.raw.video
                    is Content.Image -> R.raw.image
                    is Content.Text -> R.raw.text
                    else -> R.raw.problem
                }
            )

            itemView.findViewById<LottieAnimationView>(R.id.typeData).playAnimation()
            itemView.setOnClickListener { listener(item) }
        }

    }

}