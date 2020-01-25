package androidx.viewpager.widget;

import android.view.View;

/**
 * Created by gaurav.bansal1 on 10/03/19.
 */

public class ViewPagerUtils {

    public static View getCurrentView(ViewPager viewPager) {
        final int currentItem = viewPager.getCurrentItem();
        for (int i = 0; i < viewPager.getChildCount(); i++) {
            final View child = viewPager.getChildAt(i);
            final ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) child.getLayoutParams();
            if (!layoutParams.isDecor && currentItem == layoutParams.position) {
                return child;
            }
        }
        return null;
    }

    public static View getNextView(ViewPager viewPager) {
        final int currentItem = viewPager.getCurrentItem();
        for (int i = 0; i < viewPager.getChildCount(); i++) {
            final View child = viewPager.getChildAt(i);
            final ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) child.getLayoutParams();
            if (!layoutParams.isDecor && layoutParams.position == (currentItem + 1)) {
                return child;
            }
        }
        return null;
    }

    public static View getPreviousView(ViewPager viewPager) {
        final int currentItem = viewPager.getCurrentItem();
        for (int i = 0; i < viewPager.getChildCount(); i++) {
            final View child = viewPager.getChildAt(i);
            final ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) child.getLayoutParams();
            if (!layoutParams.isDecor && layoutParams.position == (currentItem - 1)) {
                return child;
            }
        }
        return null;
    }



}
