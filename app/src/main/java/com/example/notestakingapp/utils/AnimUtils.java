package com.example.notestakingapp.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class AnimUtils {
    public static AnimatorSet setAnim(View v) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleXDown = ObjectAnimator.ofFloat(v, "scaleX", 0.95f);
        ObjectAnimator scaleYDown = ObjectAnimator.ofFloat(v, "scaleY", 0.95f);
        ObjectAnimator scaleXUp = ObjectAnimator.ofFloat(v, "scaleX", 1f);
        ObjectAnimator scaleYUp = ObjectAnimator.ofFloat(v, "scaleY", 1f);
        ObjectAnimator alphaDown = ObjectAnimator.ofFloat(v, "alpha", 0.8f);
        ObjectAnimator alphaUp = ObjectAnimator.ofFloat(v, "alpha", 1f);
        animatorSet.play(scaleXDown).with(scaleYDown).with(alphaDown);
        animatorSet.play(scaleXUp).with(scaleYUp).with(alphaUp).after(scaleXDown);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(100);
        animatorSet.start();
        return animatorSet;
    }
}
