package com.example.testrxjava;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class Utils {
    public static View getViewTouchedByEvent(View view, MotionEvent event) {
        if (view == null || event == null) {
            return null;
        }

        if (!(view instanceof ViewGroup)) {
            return isDebugWindowValidTouched(view, event) ? view : null;
        }

        ViewGroup parent = ((ViewGroup) view);
        int childrenCount = parent.getChildCount();
        for (int i = 0; i < childrenCount; i++) {
            View target = getViewTouchedByEvent(parent.getChildAt(i), event);
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    private static boolean isDebugWindowValidTouched(View view, MotionEvent event) {
        if (event == null || view == null) {
            return false;
        }

        if (view.getVisibility() != View.VISIBLE) {
            return false;
        }

        final float eventRawX = event.getRawX();
        final float eventRawY = event.getRawY();

        RectF rect = new RectF();
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        float x = location[0];
        float y = location[1];
        rect.left = x;
        rect.right = x + view.getWidth();
        rect.top = y;
        rect.bottom = y + view.getHeight();

        return rect.contains(eventRawX, eventRawY) ;
    }
}
