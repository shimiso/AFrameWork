package com.eshangke.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.eshangke.framework.R;

import java.util.Random;

/**
 * 类描述: 自定义view
 * 创建人: shims
 * 创建时间: 16/9/6 16:03
 */
public class CustomerView extends View {

    Paint paint = new Paint();

    RectF rectCircle = new RectF(350, 300, 600, 550);

    private Bitmap bitmap;

    private MyThread myThread = null;

    private float rx;//x坐标

    private float sweepAngle;//区间角度

    private String textView;

    public CustomerView(Context context) {
        super(context);
    }

    public CustomerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.CustomerView);
        textView=ta.getString(R.styleable.CustomerView_textValue);
        ta.recycle();
    }

    public CustomerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Canvas 可以用来绘制文字,几何图形,bitmap
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);


        paint.setTextSize(60);
        paint.setColor(0xffff0000);//a,r,g,b (透明度,红,绿,蓝)
        paint.setStyle(Paint.Style.FILL);//FILL实心 STROKE空心
        //绘制文字
        //文字,x坐标,y坐标
        canvas.drawText(textView, rx, 200, paint);
        if (myThread == null) {
            myThread = new MyThread();
            myThread.start();
        }

        //绘制直线
        //起始x坐标 起始y坐标 终止x坐标 终止y坐标
        canvas.drawLine(10, 250, 300, 250, paint);

        //绘制矩形
        //左x坐标 上y起始坐标 右x的终止坐标 下y的终止坐标

        RectF rect = new RectF(10, 300, 300, 550);
//      canvas.drawRect(0,300,300,550,paint);
//      canvas.drawRect(rect,paint);

        //绘制圆角矩形
        //x弧度 y弧度
        canvas.drawRoundRect(rect, 20, 20, paint);

        //绘制原型
        //x,y原型坐标,半径
//        canvas.drawCircle(600, 450, 100, paint);
        //起始角度,区间角度
        canvas.drawArc(rectCircle, 0, sweepAngle, true, paint);


        //绘制图片
        //左x坐标 上y起始坐标
        canvas.drawBitmap(bitmap, 10, 600, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        running = false;
        super.onDetachedFromWindow();
    }

    private boolean running = true;

    class MyThread extends Thread {
        Random random = new Random();

        @Override
        public void run() {
            super.run();
            while (running) {
                rx = rx + 10;
                if (rx > getWidth()) {
                    rx = 0 - paint.measureText(textView);
                }

                sweepAngle = sweepAngle + 10;
                if (sweepAngle > 360) {
                    sweepAngle = 0;
                }

//                int r = random.nextInt(255);
//                int g = random.nextInt(255);
//                int b = random.nextInt(255);
//
//                paint.setARGB(255, r, g, b);
                postInvalidate();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
