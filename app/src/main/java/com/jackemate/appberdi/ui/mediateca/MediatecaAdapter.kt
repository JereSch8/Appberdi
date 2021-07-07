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

class MediatecaAdapter(private val context: Context, private val listMultimedia: List<Any>,
                       private val itemClickListener: OnMultimediaClickListener) :
    RecyclerView.Adapter<BaseViewHolder<*>>()  {

    interface OnMultimediaClickListener{ fun onMultimediaClick(multimedia: Any) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return MultiMediaViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler,parent,false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is MultiMediaViewHolder -> holder.bind(listMultimedia[position])
        }
    }

    override fun getItemCount(): Int {
        return listMultimedia.size
    }

    inner class MultiMediaViewHolder(itemView: View): BaseViewHolder<Any>(itemView){
        override fun bind(item: Any) {
            when(item){
                is Content.Audio -> {
                    itemView.findViewById<TextView>(R.id.title).text = item.title
                    itemView.findViewById<LottieAnimationView>(R.id.typeData).setAnimation(R.raw.audio)
                    itemView.findViewById<TextView>(R.id.description).text = item.subtitle
                }
                is Content.Video -> {
                    itemView.findViewById<TextView>(R.id.title).text = item.title
                    itemView.findViewById<LottieAnimationView>(R.id.typeData).setAnimation(R.raw.video)
                    itemView.findViewById<TextView>(R.id.description).text = item.description
                }
                is Content.Gif -> {
                    itemView.findViewById<TextView>(R.id.title).text = item.title
                    itemView.findViewById<LottieAnimationView>(R.id.typeData).setAnimation(R.raw.video)
                    itemView.findViewById<TextView>(R.id.description).text = item.description
                }
                is Content.Image -> {
                    itemView.findViewById<TextView>(R.id.title).text = item.site
                    itemView.findViewById<LottieAnimationView>(R.id.typeData).setAnimation(R.raw.image)
                    itemView.findViewById<TextView>(R.id.description).text = item.description
                }
                is Content.Text -> {
                    itemView.findViewById<TextView>(R.id.title).text = item.site
                    itemView.findViewById<LottieAnimationView>(R.id.typeData).setAnimation(R.raw.text)
                    itemView.findViewById<TextView>(R.id.description).text = item.description
                }
            }

            itemView.setOnClickListener{itemClickListener.onMultimediaClick(item)}
        }

    }

}