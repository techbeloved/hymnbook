package com.techbeloved.edittextwithsortby;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

public class SortByEditText extends AppCompatEditText {

    public static final String TAG = "SortByEditText";
    private Drawable sortByImage;
    private OnSortByClickListener mSortByClickListener;

    public SortByEditText(Context context) {
        super(context);
        init();
    }

    public SortByEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SortByEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        sortByImage = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_sort_black_24dp, null);

        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, sortByImage, null);

        setOnTouchListener((view, event) -> {
            view.performClick();

            if (getCompoundDrawablesRelative()[2] != null) {

                boolean isSortButtonClicked = false;
                float sortButtonStart;  // Used for LTR languages
                float sortButtonEnd; // Used for RTL languages

                // Detect touch in RTL or LTR layout direction
                if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    // If RTL, get the end of the button on the left side
                    sortButtonEnd = (sortByImage.getIntrinsicWidth() + getPaddingStart());

                    if (event.getX() < sortButtonEnd) {
                        isSortButtonClicked = true;
                    }
                } else {
                    // Layout is LTR.
                    // Get the start of the button on the right side.
                    sortButtonStart = (getWidth() - getPaddingEnd() - sortByImage.getIntrinsicWidth());

                    if (event.getX() > sortButtonStart) {
                        isSortButtonClicked = true;
                    }
                }
                if (isSortButtonClicked) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // Maybe change the drawable
                        return true;
                    }

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Log.i(TAG, "actionUp: clicked sort icon");
                        if (mSortByClickListener != null) {
                            mSortByClickListener.onClick(view);
                        }
                        return true;
                    }

                }

            }

            return false;
        });
    }

    public void setSortByClickListener(OnSortByClickListener sortByClickListener) {
        mSortByClickListener = sortByClickListener;
    }

    public interface OnSortByClickListener {
        void onClick(View view);
    }
}
