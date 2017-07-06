package com.eshangke.framework.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * 类的说明:item可自动收缩的listview
 * 作者: caoyulong
 * 创建时间: 2016/2/18 18:24
 */
public class WrapListView extends ListView {
    public WrapListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public WrapListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        int width = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }else {
            if(widthMode == MeasureSpec.AT_MOST) {
                final int childCount = getChildCount();
                for(int i=0;i<childCount;i++) {
                    View view = getChildAt(i);
                    measureChild(view, widthMeasureSpec, heightMeasureSpec);
                    width = Math.max(width, view.getMeasuredWidth());
                }
            }
        }

        setMeasuredDimension(width, height);
    }
}
