package com.example.kevin.jakarta.Utils;

import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by kevin on 16-12-13.
 */

public class AnimationUtil {
    public static void scaleAnimation(float ratio, long duration,float alpha, View view) {
        ViewCompat.animate(view).scaleX(ratio).scaleY(ratio).setDuration(duration).alpha(alpha).start();
    }
}
