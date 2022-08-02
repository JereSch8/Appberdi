package com.jackemate.appberdi.ui.welcome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.BoardItemBinding
import com.jackemate.appberdi.entities.Board

class ViewPageAdapter(
    private val boardList: List<Board>,
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<ViewPageAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = BoardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(boardList[position])
    }

    override fun getItemCount(): Int = boardList.size


    inner class BoardViewHolder(val binding: BoardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(board: Board) = with(itemView) {
            binding.lottieAnimationView.setAnimation(board.animation)
            binding.textViewTitulo.text = board.title
            binding.textViewDescripcion.text = board.description

            if (adapterPosition == (boardList.size - 1)) {
                binding.buttonSiguiente.text = context.getString(R.string.finalizar)
            }

            binding.buttonSiguiente.setOnClickListener {
                onItemSelected(adapterPosition)
            }
        }

    }
}