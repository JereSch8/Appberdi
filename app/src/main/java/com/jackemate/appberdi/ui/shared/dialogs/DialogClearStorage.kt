package com.jackemate.appberdi.ui.shared.dialogs

import android.app.Activity
import com.jackemate.appberdi.R
import com.jackemate.appberdi.data.CacheRepository
import com.jackemate.appberdi.databinding.DialogClearstorageBinding
import com.jackemate.appberdi.databinding.ItemClearStorageBinding
import com.jackemate.appberdi.entities.Content
import com.jackemate.appberdi.ui.shared.DialogBuilder

class DialogClearStorage(val activity: Activity) : DialogBuilder(activity) {
    override val binding = DialogClearstorageBinding.inflate(inflater)
    private val cacheRepo = CacheRepository(activity)

    class Option(val tag: String, var size: Double = 0.0, var isChecked: Boolean = false)

    private val options = listOf(
        Option(Content.TYPE_AUDIO),
        Option(Content.TYPE_IMG),
        Option(Content.TYPE_GIF)
    )

    init {
        options.forEach { item ->
            item.size = cacheRepo.sizeOf(item.tag)
            binding.options.addView(buildOptionView(item).root)
        }
        initButton()
        updateFinishMessage()
    }

    private fun initButton() {
        binding.button.setOnClickListener {
            options.forEach {
                if (it.isChecked)
                    cacheRepo.clear(it.tag)
            }
            dismiss()
        }
    }

    private fun buildOptionView(item: Option): ItemClearStorageBinding {
        val option = ItemClearStorageBinding.inflate(inflater)
        option.startText.text = item.tag
        option.endText.text = context.getString(R.string.unit_mb, item.size)

        option.root.setOnClickListener {
            item.isChecked = !option.checkbox.isChecked
            option.checkbox.isChecked = item.isChecked
            updateFinishMessage()
        }

        option.checkbox.setOnClickListener {
            item.isChecked = option.checkbox.isChecked
            updateFinishMessage()
        }
        return option
    }

    private fun updateFinishMessage() {
        binding.button.isEnabled = options.any { it.isChecked }

        val size = getSizeToClear()
        binding.finishMessage.text = context.getString(R.string.clear_finish_text, size)
        binding.button.text = context.getString(R.string.clear_button, size)
    }

    private fun getSizeToClear() = options.fold(0.0) { acc, option ->
        if (option.isChecked) acc + option.size else acc
    }

    override fun setAnimation(rawRes: Int) = this.also { binding.animation.setAnimation(rawRes) }
}