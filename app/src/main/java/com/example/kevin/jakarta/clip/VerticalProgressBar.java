package com.example.kevin.jakarta.clip;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ants on 09/12/2016.
 */

public class VerticalProgressBar extends View {
    @Nullable private Drawable foreground;
    private float progress;


    public VerticalProgressBar(Context context) {
        super(context);
    }

    public VerticalProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public VerticalProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setForeground(@Nullable Drawable foreground) {
        this.foreground = foreground;
        postInvalidate();
    }

    public void setProgress(float progress) {
        this.progress = progress;
        postInvalidate();
    }

    @Override protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Drawable foreground = this.foreground;
        if (null != foreground) {
            final int parentWidth = getWidth();
            final int parentHeight = getHeight();
            int width = parentWidth;
            int height = (int) (parentHeight * progress);
            foreground.setBounds(0, parentHeight - height, width, parentHeight);
            foreground.draw(canvas);
        }
    }

    @BindingAdapter("android:progress_foreground")
    public static void setVerticalProgressBarForeground(@NonNull VerticalProgressBar progressBar, int color) {
        progressBar.setForeground(new ColorDrawable(color));
    }

    @BindingAdapter("android:progress")
    public static void setVerticalProgressBarProgress(@NonNull VerticalProgressBar progressBar, float progress) {
        progressBar.setProgress(progress);
    }
}
