package com.jackemate.appberdi.ui.attractions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jackemate.appberdi.databinding.ActivityAttractionsBinding
import com.jackemate.appberdi.entities.Attraction
import com.jackemate.appberdi.ui.attractions.AttractionDetailActivity.Companion.ID_ATTRACTION
import com.jackemate.appberdi.utils.TAG
import com.jackemate.appberdi.utils.observe

class AttractionActivity : AppCompatActivity() {
    private val viewModel: AttractionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAttractionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView(binding)

        observe(viewModel.getAttractions()) {
            Log.i(TAG, "Lista de atracciones: $it")
            (binding.attractionList.adapter as AttractionListAdapter).update(it)
        }
    }

    private fun setupRecyclerView(binding: ActivityAttractionsBinding){
        binding.attractionList.layoutManager = LinearLayoutManager(this)
        binding.attractionList.adapter = AttractionListAdapter(emptyList(), ::onSelect)
    }

    private fun onSelect(item: Attraction) {
        Log.i(TAG, "Seleccionado: $item")
        startActivity(Intent(this, AttractionDetailActivity::class.java).apply {
            putExtra(ID_ATTRACTION, item.id)
        })
    }
}