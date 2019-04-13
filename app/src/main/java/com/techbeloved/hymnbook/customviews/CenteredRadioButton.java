package com.techbeloved.hymnbook.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;

import com.techbeloved.hymnbook.R;

import androidx.appcompat.widget.AppCompatRadioButton;

public class CenteredRadioButton extends AppCompatRadioButton {

    private Drawable buttonDrawable;

    public CenteredRadioButton(Context context) {
        super(context);
        init(context, null);
    }

    public CenteredRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CenteredRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CenteredRadioButton, 0, 0);
            buttonDrawable = a.getDrawable(R.styleable.CenteredRadioButton_android_button);
            a.recycle();
        }
        setButtonDrawable(android.R.color.transparent);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (buttonDrawable != null) {
           buttonDrawable.setState(getDrawableState());
           final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
           final int height = buttonDrawable.getIntrinsicHeight();

           int y = 0;

           switch (verticalGravity) {
               case Gravity.BOTTOM:
                   y = getHeight() - height;
                   break;
               case Gravity.CENTER_VERTICAL:
                   y = (getHeight() - height) / 2;
                   break;
           }

           int buttonWidth = buttonDrawable.getIntrinsicWidth();
           int buttonLeft = (getWidth() - buttonWidth) / 2;
           buttonDrawable.setBounds(buttonLeft, y, buttonLeft+buttonWidth, y+height);
           buttonDrawable.draw(canvas);
        }
    }
}
