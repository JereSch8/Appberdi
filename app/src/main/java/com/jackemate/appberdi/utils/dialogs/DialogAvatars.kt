package com.jackemate.appberdi.utils.dialogs

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jackemate.appberdi.databinding.DialogAvatarsBinding
import com.jackemate.appberdi.entities.Board

class DialogAvatars(activity: Activity): DialogBuilder(activity) {
    override val binding = DialogAvatarsBinding.inflate(inflater)

    init {
        binding.recycler.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recycler.adapter = AvatarListAdapter(
            AvatarViewModel().getListAvatars(),
            ::onSelect
        )
    }

    fun setText(text: String) : DialogAvatars {
        binding.title.text = text
        return this
    }

    private fun onSelect(item: Board) {
        localInfo.setAvatar(item.animation)
        setAnimation(item.animation)
        dismiss()
    }

    override fun setAnimation(rawRes: Int) = this.also { binding.animation.setAnimation(rawRes) }

}