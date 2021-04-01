package com.jackemate.appberdi.ui.attractions

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jackemate.appberdi.databinding.ActivityAttractionsBinding
import com.jackemate.appberdi.domain.entities.Attraction
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
            (binding.attractionList.adapter as AttractionListAdapter).update(it)
            Log.i(TAG, "Lista de atracciones: $it")
        }
    }

    private fun setupRecyclerView(binding: ActivityAttractionsBinding){
        binding.attractionList.layoutManager = LinearLayoutManager(this)
        binding.attractionList.adapter = AttractionListAdapter(emptyList(), ::onSelect)
    }

    private fun onSelect(item: Attraction) {
        Log.i(TAG, "Seleccionado: $item")
    }
}