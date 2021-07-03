package com.jackemate.appberdi.ui.attractions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.jackemate.appberdi.databinding.ItemAttractionBinding
import com.jackemate.appberdi.entities.Attraction
import com.jackemate.appberdi.utils.Hmm
import com.jackemate.appberdi.utils.eeeeHmm
import com.jackemate.appberdi.utils.upper
import com.jackemate.appberdi.utils.visible
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

class AttractionListAdapter constructor(
    private var list: List<Attraction>,
    private val onClick: (item: Attraction) -> Unit
) : RecyclerView.Adapter<AttractionListAdapter.AttractionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val v = ItemAttractionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttractionViewHolder(v)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int { return list.size }

    fun update(list: List<Attraction>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class AttractionViewHolder(private val v: ItemAttractionBinding) :
        RecyclerView.ViewHolder(v.root) {
        fun bind(item: Attraction) {
            v.title.text = item.name

            v.description.visible(item.description.isNotEmpty())
            v.description.text = item.description
            v.btnAccessible.visible(item.accessible)

            Glide.with(v.root)
                .load(item.coverUrl)
                .transform(CenterCrop())
                .placeholder(ColorDrawable(Color.parseColor("#12000000")))
                .into(v.img)

            val horario = item.getProxHorario()
            v.horario.visible(horario != null)
            horario?.let {
                val now = LocalDateTime.now()
                val next = now.with(TemporalAdjusters.nextOrSame(it.day))

                if (it.isNowOpen()) {
                    v.horario.text = (SpannableString("Ahora mismo".upper()).nowStyle())
                    v.horario.append(" • Cierra a las ${next.with(it.close).Hmm()}".upper())
                } else {
                    v.horario.text = ("Abre el ${next.with(it.open).eeeeHmm()}".upper())
                }
            }

            v.root.setOnClickListener { onClick(item) }
        }

        private fun SpannableString.nowStyle(): SpannableString {
            this.setSpan(
                ForegroundColorSpan(Color.RED),
                0,
                this.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return this
        }
    }
}