package com.contplayer.ui;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Const {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RESIZE_MODE_FIT, RESIZE_MODE_FILL})
    public @interface RESIZE_MODE{}
    public static final int RESIZE_MODE_FIT = 0;
    public static final int RESIZE_MODE_FILL = 3;

}
