package com.tt.tradein.utils;

import android.content.Context;
import android.util.DisplayMetrics;
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

    public static int getScreenWidth(Context context){
        DisplayMetrics dm =context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context){
        DisplayMetrics dm =context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
}
