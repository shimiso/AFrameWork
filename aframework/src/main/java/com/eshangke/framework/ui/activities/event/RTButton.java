package com.eshangke.framework.ui.activities.event;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

public class RTButton extends Button {
	public RTButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 事件的分发
	 * @param ev
	 * @return
	 * 返回true只执行当前的方法
	 * 返回false执行上一层的 onTouchEvent
	 * 返回super.dispatchTouchEvent(event) 执行当前的onTouchEvent
     */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.e("eventTest", "RTButton | dispatchTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 事件的处理
	 * @param ev
	 * @return 返回true执行当前方法 返回false执行上一层的执行当前的onTouchEvent
     */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.e("eventTest", "RTButton | dispatchTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		return super.onTouchEvent(ev);
	}
}