package com.techbeloved.hymnbook.hymndetail;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private OnTapListener onTapListener;

    public GestureListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        // TODO: 8/6/18 Toggle fullscreen here
        onTapListener.onSingleTapConfirmed();
        return true;
    }
}
