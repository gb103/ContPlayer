package com.contplayer.utility;

import android.os.Build;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ContPlayerUtils {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IN_MOTION_PREV_LEFT, IN_MOTION_NEXT_RIGHT})
    public @interface MOTION_DIRECTION{}
    public static final int IN_MOTION_PREV_LEFT = 0;
    public static final int IN_MOTION_NEXT_RIGHT = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PLAYER_CURRENT, PLAYER_NEXT, PLAYER_PREVIOUS})
    public @interface PLAYER_TYPE{}
    public static final int PLAYER_CURRENT = 0;
    public static final int PLAYER_NEXT = 1;
    public static final int PLAYER_PREVIOUS = 2;



    public static final boolean hasOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

}
