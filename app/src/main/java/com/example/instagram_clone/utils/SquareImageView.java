package com.example.instagram_clone.utils;

import android.content.Context;
import android.util.AttributeSet;

public class SquareImageView extends androidx.appcompat.widget.AppCompatImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // second parameter widthMeasureSpec should be height, but we pass
        // width, making image squared
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
