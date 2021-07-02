package com.jackemate.appberdi.ui.attractions

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jackemate.appberdi.databinding.ActivityAttractionsBinding
import com.jackemate.appberdi.entities.Attraction
import com.jackemate.appberdi.ui.attractions.AttractionDetailActivity.Companion.ID_ATTRACTION
import com.jackemate.appberdi.utils.observe
import com.jackemate.appberdi.utils.upper

class AttractionActivity : AppCompatActivity() {
    private val viewModel: AttractionViewModel by viewModels()
    private lateinit var binding: ActivityAttractionsBinding
    private lateinit var listAttraction : List<Attraction>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttractionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView(binding)
        onCreateOptionsMenu(binding.searchView)

        observe(viewModel.getAttractions()) {
            listAttraction = it
            (binding.attractionList.adapter as AttractionListAdapter).update(it)
        }
    }

    private fun setupRecyclerView(binding: ActivityAttractionsBinding){
        binding.attractionList.layoutManager = LinearLayoutManager(this)
        binding.attractionList.adapter = AttractionListAdapter(emptyList(), ::onSelect)
    }

    private fun onSelect(item: Attraction) {
        startActivity(Intent(this, AttractionDetailActivity::class.java).apply {
            putExtra(ID_ATTRACTION, item.id)
        })
    }

    private fun onCreateOptionsMenu(searchView: SearchView){
        searchView.queryHint = "Ingrese título o descripción"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean { return false }

            override fun onQueryTextChange(newText: String?): Boolean {
                (binding.attractionList.adapter as AttractionListAdapter).update(
                    listAttraction.filter { at ->
                        at.description.upper().contains(newText!!.upper()) ||
                                at.name.upper().contains(newText.upper())})
                return true
            }
        })
    }
}