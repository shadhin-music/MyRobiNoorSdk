package com.gakk.noorlibrary.extralib.cardstackview.internal;

import android.view.animation.Interpolator;

import com.gakk.noorlibrary.extralib.cardstackview.Direction;


public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
