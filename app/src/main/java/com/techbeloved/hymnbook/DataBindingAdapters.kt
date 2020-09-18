package com.techbeloved.hymnbook

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("app:visibleGone")
fun showOrHideView(view: View, show: Boolean) {
    view.isVisible = show
}