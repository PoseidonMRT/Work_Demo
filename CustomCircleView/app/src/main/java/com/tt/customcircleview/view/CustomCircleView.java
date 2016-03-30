package com.tt.customcircleview.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.tt.customcircleview.utils.MeasureUtil;

/**
 * Created by Administrator on 2016/3/29 0029.
 */
public class CustomCircleView extends View implements Runnable{
    private Paint mPaint;
    private Context mContext;
    private int radiu;
    public CustomCircleView(Context context) {
        this(context,null);
    }

    public CustomCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initPaint();
    }

    public void initPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        /*
     * 设置画笔样式为描边，圆环嘛……当然不能填充不然就么意思了
     *
     * 画笔样式分三种：
     * 1.Paint.Style.STROKE：描边
     * 2.Paint.Style.FILL_AND_STROKE：描边并填充
     * 3.Paint.Style.FILL：填充
     */
        mPaint.setStyle(Paint.Style.STROKE);

        // 设置画笔颜色为浅灰色
        mPaint.setColor(Color.LTGRAY);

    /*
     * 设置描边的粗细，单位：像素px
     * 注意：当setStrokeWidth(0)的时候描边宽度并不为0而是只占一个像素
     */
        mPaint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(MeasureUtil.getScreenSize((Activity) mContext)[0] / 2, MeasureUtil.getScreenSize((Activity) mContext)[1] / 2, radiu, mPaint);
    }

    @Override
    public void run() {
        while (true) {
            try {
                /*
                 * 如果半径小于200则自加否则大于200后重置半径值以实现往复
                 */
                if (radiu <= 200) {
                    radiu += 10;

                    // 刷新View
                    postInvalidate();
                } else {
                    radiu = 0;
                }

                // 每执行一次暂停40毫秒
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
