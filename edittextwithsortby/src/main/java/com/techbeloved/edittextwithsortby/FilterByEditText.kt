package com.techbeloved.edittextwithsortby

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat

class FilterByEditText @JvmOverloads constructor (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var sortByImage: Drawable?
    private var sortByClickListener: OnSortByClickListener? = null


    init {
        sortByImage = ResourcesCompat.getDrawable(resources,
                R.drawable.ic_sort_black_24dp, null)

        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                sortByImage, null)

        setOnTouchListener { view, event ->
            view.performClick()
            var isSortButtonClicked: Boolean = false
            if (compoundDrawablesRelative[2] != null) {
                val sortButtonStart: Float  // Used for LTR languages
                val sortButtonEnd: Float // Used for RTL languages

                // Detect touch in RTL or LTR layout direction
                if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    // If RTL, get the end of the button on the left side
                    sortButtonEnd = (sortByImage!!.intrinsicWidth + paddingStart).toFloat()

                    if (event.x < sortButtonEnd) {
                        isSortButtonClicked = true
                    }
                } else {
                    // Layout is LTR.
                    // Get the start of the button on the right side.
                    sortButtonStart = ((width - paddingEnd - sortByImage!!.intrinsicWidth).toFloat())

                    if (event.x > sortButtonStart) {
                        isSortButtonClicked = true
                    }
                }

            }
            if (isSortButtonClicked && sortByClickListener != null) {
                sortByClickListener!!.onClick(view)
            }

            isSortButtonClicked
        }
    }

    fun setOnSortByClickListener(listener: OnSortByClickListener?) {
        sortByClickListener = listener
    }
    interface OnSortByClickListener {
        fun onClick(view: View)
    }
}