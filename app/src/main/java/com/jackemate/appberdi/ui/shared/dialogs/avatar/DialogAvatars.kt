package com.jackemate.appberdi.ui.shared.dialogs.avatar

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jackemate.appberdi.R
import com.jackemate.appberdi.databinding.DialogAvatarsBinding
import com.jackemate.appberdi.entities.Board
import com.jackemate.appberdi.ui.shared.DialogBuilder

class DialogAvatars(activity: Activity): DialogBuilder(activity) {
    override val binding = DialogAvatarsBinding.inflate(inflater)

    private val avatars = listOf(
        Board(
            background = R.drawable.background,
            animation = R.raw.astronaut_dog,
            title = context.getString(R.string.avatar_albi_astro),
            description = context.getString(R.string.avatar_albi_astro_desc)
        ),
        Board(
            background = R.drawable.background,
            animation = R.raw.smiling_dog,
            title = context.getString(R.string.avatar_albi_feliz),
            description = context.getString(R.string.avatar_albi_feliz_desc)
        ),
        Board(
            background = R.drawable.background,
            animation = R.raw.unicorn_dog,
            title = context.getString(R.string.avatar_albi_unicornio),
            description = context.getString(R.string.avatar_albi_unicornio_desc)
        ),
        Board(
            background = R.drawable.background,
            animation = R.raw.flirting_dog,
            title = context.getString(R.string.avatar_albi_canchero),
            description = context.getString(R.string.avatar_albi_canchero_desc)
        )
    )

    init {
        binding.recycler.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recycler.adapter = AvatarListAdapter(
            avatars,
            ::onSelect
        )
    }

    fun setText(text: String) : DialogAvatars {
        binding.title.text = text
        return this
    }

    private fun onSelect(item: Board) {
        preferenceRepo.setAvatar(item.animation)
        setAnimation(item.animation)
        dismiss()
    }

    override fun setAnimation(rawRes: Int) = this.also { binding.animation.setAnimation(rawRes) }

}