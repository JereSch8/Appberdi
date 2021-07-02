package com.jackemate.appberdi.utils.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.DialogAvatarsBinding
import com.jackemate.appberdi.domain.entities.Board
import com.jackemate.appberdi.utils.LocalInfo

class DialogAvatars(val activity: Activity) {
    private val binding: DialogAvatarsBinding
    private val dialog: Dialog = Dialog(activity)

    private val localInfo: LocalInfo = LocalInfo(activity.baseContext)

    init {
        val inflater: LayoutInflater = LayoutInflater.from(activity)
        binding = DialogAvatarsBinding.inflate(inflater)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        if (localInfo.getAvatar() != -8)
            setAnimation(localInfo.getAvatar())
        else
            setAnimation(R.raw.astronaut_dog)

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

    fun show() {
        dialog.setContentView(binding.root)
        dialog.show()
    }

    fun setText(text: String) : DialogAvatars {
        binding.title.text = text
        return this
    }

    fun setOnDismiss(listener: () -> Unit ) : DialogAvatars{
        dialog.setOnDismissListener { listener() }
        return this
    }

    private fun onSelect(item: Board) {
        localInfo.setAvatar(item.animation)
        setAnimation(item.animation)
        dialog.dismiss()
    }

    private fun setAnimation(rawRes: Int) = binding.animation.setAnimation(rawRes)



}