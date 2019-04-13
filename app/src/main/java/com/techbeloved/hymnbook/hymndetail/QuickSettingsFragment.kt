package com.techbeloved.hymnbook.hymndetail

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.DialogQuickSettingsBinding

class QuickSettingsFragment: RoundedBottomSheetDialogFragment() {

    private lateinit var binding: DialogQuickSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_quick_settings, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.root.clipToOutline = true
        }
        return binding.root
    }

}