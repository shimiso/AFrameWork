package com.eshangke.framework.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Window;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.base.Request;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）
 * 版    本：1.0
 * 创建日期：2016/4/8
 * 描    述：我的Github地址  https://github.com/jeasonlzy0216
 * 修订历史：
 * ================================================
 */
public abstract class StringDialogCallback extends StringCallback {

    private ProgressDialog dialog;

    public StringDialogCallback(Activity activity) {
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("请求网络中...");
    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {
        super.onStart(request);
        //网络请求前显示对话框
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
