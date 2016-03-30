package com.tt.customcircleview.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * 
 * @author Aige
 * @since 2014/11/17
 */
public final class MeasureUtil {
	public static int[] getScreenSize(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return new int[] { metrics.widthPixels, metrics.heightPixels };
	}
}
