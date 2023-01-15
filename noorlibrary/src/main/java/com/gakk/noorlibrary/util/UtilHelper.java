package com.gakk.noorlibrary.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;

public class UtilHelper {

    public static Point getScreenSize(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
}
