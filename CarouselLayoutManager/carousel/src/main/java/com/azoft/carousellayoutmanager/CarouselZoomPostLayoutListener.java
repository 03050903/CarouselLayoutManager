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
        //此算法想得到一个数学范围内的值而设计的及 (0, 1)
        /**itemPositionToCenterDiff 代表的值的绝对值遇到 离center item的位置越大 scale系数越大
         * atan(x)值的范围是(-Pi/2,Pi/2) x值越大
         * Math.abs(itemPositionToCenterDiff) + 1.0 > 0    +1代表至少是 y = Pi/4 的线
         * StrictMath.atan(Math.abs(itemPositionToCenterDiff) + 1.0) / Math.PI -- (Pi/4,Pi/2)
         *
         * 卧槽 ....还是搞不懂为什么要选择此种算法..
         */
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