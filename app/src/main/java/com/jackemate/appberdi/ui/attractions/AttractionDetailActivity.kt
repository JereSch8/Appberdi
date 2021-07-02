package com.jackemate.appberdi.ui.attractions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.jackemate.appberdi.databinding.ActivityAttractionsDetailBinding
import com.jackemate.appberdi.entities.BusinessHours
import com.jackemate.appberdi.utils.dialogs.DialogMap
import com.jackemate.appberdi.utils.observe


class AttractionDetailActivity : AppCompatActivity() {
    private lateinit var id: String
    private val viewModel: AttractionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAttractionsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra(ID_ATTRACTION) ?: throw Exception("No id attraction")

        observe(viewModel.getOneAttraction(id)) {
            binding.title.text = it.name
            binding.description.text = it.description

            Glide.with(binding.root)
                .load(it.coverUrl)
                .placeholder(ColorDrawable(Color.parseColor("#12000000")))
                .into(binding.img)

            val h = it.horarios ?: BusinessHours() // Empty
            binding.horarios.text = h.toString()

            for (social in it.links) {
                val b = ItemSocial(this)
                b.set(social)
                binding.socialList.addView(b)
            }

            if(it.pos != null) {
                binding.howToGet.setOnClickListener { _ ->
                    DialogMap(this,this, it).show()
                }
            }
            else
                binding.howToGet.visibility = View.GONE
        }
    }

    companion object {
        const val ID_ATTRACTION = "id_attraction"
    }
}