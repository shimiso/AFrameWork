package com.eshangke.framework.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.eshangke.framework.R;


/**
 * 请在此处简要描述此类所实现的功能。因为这项注释主要是为了在IDE环境中生成tip帮助，务必简明扼要
 * 
 * 请在此处详细描述类的功能、调用方法、注意事项、以及与其它类的关系.
 **/
public class LoadingDialog extends Dialog {

	private Context context = null;
	private static LoadingDialog customProgressDialog = null;

	private LoadingDialog(Context context) {
		super(context);
		this.context = context;
	}

	private LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	public static LoadingDialog createDialog(Context context) {
		customProgressDialog = new LoadingDialog(context, R.style.LodingDialog);
		customProgressDialog.setContentView(R.layout.loading_dialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		customProgressDialog.setCanceledOnTouchOutside(false);
		return customProgressDialog;
	}

	public static LoadingDialog createUploadPicDialog(Context context) {
		customProgressDialog = new LoadingDialog(context, R.style.LodingDialog);
		customProgressDialog.setContentView(R.layout.loading_dialog);
		TextView id_tv_loadingmsg = (TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
		id_tv_loadingmsg.setText("正在上传...");
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return customProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		if (customProgressDialog == null) {
			return;
		}
//		ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
//		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
//		animationDrawable.start();
	}

	/**
	 * 
	 * setMessage 提示内容
	 * 
	 * @param strMessage
	 * 
	 * @return
	 */
	public LoadingDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
			tvMsg.setVisibility(View.GONE);//目前所有的页面只显示加载进度条，不显示相关文字
		}
		return customProgressDialog;
	}

	@Override
	public void show() {
		super.show();
//		ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
//		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
//		animationDrawable.start();
	}

}
