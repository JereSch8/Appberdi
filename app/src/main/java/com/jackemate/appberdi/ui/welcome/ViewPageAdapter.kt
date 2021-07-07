package com.jackemate.appberdi.ui.welcome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.jackemate.appberdi.R
import com.jackemate.appberdi.entities.Board

class ViewPageAdapter(
    private val boardList: List<Board>,
    private val onItemSelected: OnItemSelected? = null
): RecyclerView.Adapter<ViewPageAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.board_item, parent, false)
        return BoardViewHolder(view, onItemSelected )
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(boardList[position])
    }

    override fun getItemCount(): Int = boardList.size


    inner class  BoardViewHolder(
            itemView: View,
            private val onItemSelected: OnItemSelected? = null
    ): RecyclerView.ViewHolder(itemView){

        private val contenedor = itemView.findViewById<ConstraintLayout>(R.id.container)
        private val animacion = itemView.findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        private val titulo = itemView.findViewById<TextView>(R.id.textView_titulo)
        private val descripcion = itemView.findViewById<TextView>(R.id.textView_descripcion)
        private val boton = itemView.findViewById<AppCompatButton>(R.id.button_siguiente)

        fun bind(board: Board) = with(itemView){
            contenedor.background = ContextCompat.getDrawable(context, board.background)
            animacion.setAnimation(board.animation)
            titulo.text = board.title
            descripcion.text = board.description

            if (adapterPosition == (boardList.size - 1)){
                boton.text = context.getString(R.string.finalizar)
            }

            boton.setOnClickListener { onItemSelected?.onClickListener(adapterPosition) }
        }

    }

    interface OnItemSelected{
        fun onClickListener(position: Int)
    }
}