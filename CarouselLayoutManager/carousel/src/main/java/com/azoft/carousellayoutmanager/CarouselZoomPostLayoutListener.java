package com.azoft.carousellayoutmanager;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Implementation of {@link CarouselLayoutManager.PostLayoutListener} that makes interesting scaling of items. <br />
 * We are trying to make items scaling quicker for closer items for center and slower for when they are far away.<br />
 * Tis implementation uses atan function for this purpose.
 */
public class CarouselZoomPostLayoutListener implements CarouselLayoutManager.PostLayoutListener {

    @Override
    public ItemTransformation transformChild(@NonNull final View child, final float itemPositionToCenterDiff, final int orientation) {
        //首先要明确的是 Math.abs(itemPositionToCenterDiff)的范围是 (0,mMaxVisibleItems + 1)
        //Math.abs(itemPositionToCenterDiff) = 0时 scale值应该为 1 及 不做任何的scale操作
        //Math.abs(itemPositionToCenterDiff) > 0时 scale值应该从 1 -> 0变化 无限趋近于0
        //相当于就是说 Math.abs(itemPositionToCenterDiff)值越大(离center就越远) scale系数就该越小
        //而atan(x)值的范围是(-Pi/2,Pi/2)  x值越大 值越大 想要值越小 就需要在前面加 -x atan(-x) 就表示x越大值越小
        //x 值实际是 1 -> 无穷大 所有 atan(x)/Pi = [1/4,1/2)  但是我们实际是需要的值的范围是 [0,1) 所以才会对该表达式做一些运算操作满足条件
        double v = 2 * -StrictMath.atan(Math.abs(itemPositionToCenterDiff) + 1.0) / Math.PI + 1;
        double v1 = StrictMath.atan(Math.abs(itemPositionToCenterDiff)) / Math.PI;
        final float scale = (float) (2 * v);

        // because scaling will make view smaller in its center, then we should move this item to the top or bottom to make it visible
        final float translateY;
        final float translateX;
        if (CarouselLayoutManager.VERTICAL == orientation) {
            final float translateYGeneral = child.getMeasuredHeight() * (1 - scale) / 2f;
            translateY = Math.signum(itemPositionToCenterDiff) * translateYGeneral;
            translateX = 0;
        } else {
            final float translateXGeneral = child.getMeasuredWidth() * (1 - scale) / 2f;
            translateX = Math.signum(itemPositionToCenterDiff) * translateXGeneral;
            translateY = 0;
        }

        return new ItemTransformation(scale, scale, translateX, translateY);
    }
}