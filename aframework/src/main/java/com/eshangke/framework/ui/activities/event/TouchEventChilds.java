package com.eshangke.framework.ui.activities.event;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class TouchEventChilds extends LinearLayout {

	public TouchEventChilds(Context context) {
		super(context);
	}

	public TouchEventChilds(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	/**
	 * 事件的分发
	 * @param ev
	 * @return
	 * 返回true只执行当前的onTouchEvent
	 * 返回false则执行Acitity中的onTouchEvent
	 * 返回super.dispatchTouchEvent(ev)传递到下一层
	 */
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.e("eventTest", "Childs | dispatchTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 事件拦截
	 * ViewGroup中才有的方法，View中没有
	 * @param ev
	 * @return
	 * 返回true的时候表示拦截当前事件，不继续往下分发，交给自身的onTouchEvent进行处理。
	 * 返回false则不拦截，继续传给下一层的dispatchTouchEvent方法
	 */
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.i("eventTest", "Childs | onInterceptTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * 事件的处理
	 * @param ev
	 * @return 返回true表示消费处理当前事件，返回false则不处理，交给子控件进行继续分发。
     */
	public boolean onTouchEvent(MotionEvent ev) {
		Log.d("eventTest", "Childs | onTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		return super.onTouchEvent(ev);
	}

}
