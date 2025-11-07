package com.eshangke.framework.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JpushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[JpushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
	}

	/**
	 * 打印所有的 intent extra 数据
	 * @param bundle
	 * @return
     */
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}

	/**
	 * send msg to JpushActivity
	 * @param context
	 * @param bundle
     */
	private void processCustomMessage(Context context, Bundle bundle) {
	}
}
