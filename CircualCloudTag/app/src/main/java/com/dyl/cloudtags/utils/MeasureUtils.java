package com.dyl.cloudtags.utils;

import android.graphics.Point;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/3/29 0029.
 */
public class MeasureUtils {

    public static float getTextViewCenterX(TextView textView){
        float x = (textView.getLeft()+textView.getRight())/2;
        return x;
    }

    public static float getTextViewCenterY(TextView textView){
        float y = (textView.getTop()+textView.getBottom())/2;
        return y;
    }
}
