package com.eshangke.framework.widget.photoselect;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.eshangke.framework.ui.activities.BaseActivity;
import com.eshangke.framework.util.ToastUtil;
import com.eshangke.framework.widget.CropperActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类的说明：选择文件操作类
 * 作者：shims
 * 创建时间：2016/2/2 0002 10:15
 */
public class PhotoSelectDialog extends BaseActivity implements OnClickListener {
	private static final String TAG = "PhotoSelectDialog";

	public final String KEY_UPLOAD_PIC_PATH = "key_upload_pic_path";

	/** 使用照相机拍照获取图片 **/
	public static final int SELECT_PIC_BY_CAMERA_PHOTO = 2001;

	/** 使用相册中的图片 **/
	public static final int SELECT_PIC_BY_LOCAL_PHOTO = 2002;

	/**去剪裁的图片 **/
	public static final int CUP_PIC_CODE = 2003;

	/**从Intent获取图片路径的KEY**/
	public static final String KEY_PHOTO_PATH = "photo_path";

	/**弹出层**/
	private LinearLayout dialogLayout;

	/**拍照\选择图库\取消**/
	private Button cameraPhotoBtn, localPhotoBtn, cancelBtn;

	/**上一个Intent**/
	private Intent lastIntent;

	/**照片存放路径目录**/
	private String cameraTempPathDir = null;

	/**拍照文件绝对路径**/
	private File cameraTempFile = null;

	/**拍照截取文件绝对路径**/
	private File cutTempFile = null;

	/**需要裁剪的图片形状规格,默认按1:1**/
	private int cropMode = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoselect_dialog);
		dialogLayout = (LinearLayout) findViewById(R.id.dialog_layout);
		dialogLayout.setOnClickListener(this);
		cameraPhotoBtn = (Button) findViewById(R.id.btn_camera_photo);
		cameraPhotoBtn.setOnClickListener(this);
		localPhotoBtn = (Button) findViewById(R.id.btn_local_photo);
		localPhotoBtn.setOnClickListener(this);
		cancelBtn = (Button) findViewById(R.id.btn_cancel);
		cancelBtn.setOnClickListener(this);
		lastIntent = getIntent();
		cropMode = lastIntent.getIntExtra(CropperActivity.CROP_MODE,cropMode);
		cameraTempPathDir = spUtil.getCameraTempPath();

		if (savedInstanceState != null) {
			String pathCamera = savedInstanceState.getString("cameraTempPath");
			if (pathCamera != null && !pathCamera.equals("")) {
				cameraTempFile = new File(pathCamera);
			}
			String pathCut = savedInstanceState.getString("cutTempPath");
			if (pathCut != null && !pathCut.equals("")) {
				cutTempFile = new File(pathCut);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (cameraTempFile != null && cameraTempFile.exists())
			outState.putString("cameraTempPath", cameraTempFile.getAbsolutePath());

		if (cutTempFile != null && cutTempFile.exists())
			outState.putString("cutTempPath", cutTempFile.getAbsolutePath());
		super.onSaveInstanceState(outState);

	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_layout:
			setResult(Activity.RESULT_CANCELED, lastIntent);
			finish();
			break;
		case R.id.btn_camera_photo:
			if (androidUtil.checkSDCard()) {
				byCameraPhoto();
			} else {
				ToastUtil.showToast(PhotoSelectDialog.this, "请检查有无可用存储卡", Toast.LENGTH_LONG);
			}
			break;
		case R.id.btn_local_photo:
			if (androidUtil.checkSDCard()) {
				byLocalPhoto();
			} else {
				ToastUtil.showToast(PhotoSelectDialog.this, "请检查有无可用存储卡", Toast.LENGTH_LONG);
			}
			break;
		default:
			setResult(Activity.RESULT_CANCELED, lastIntent);
			finish();
			break;
		}
	}

	/**
	 * 从拍照获取图片
	 * @throws IOException 
	 */
	private void byCameraPhoto() {
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		File dirFile = new File(cameraTempPathDir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		cameraTempFile = new File(cameraTempPathDir + timeStamp + ".jpg");
		if (!cameraTempFile.exists()) {
			try {
				cameraTempFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri outputFileUri = Uri.fromFile(cameraTempFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		startActivityForResult(intent, SELECT_PIC_BY_CAMERA_PHOTO);
	}

	/***
	 * 从本地相册中取图片
	 */
	private void byLocalPhoto() {
		Intent intent = new Intent();
		intent.setClass(PhotoSelectDialog.this, SelectPhotoFolderActivity.class);
		// 跳转到相册
		startActivityForResult(intent, SELECT_PIC_BY_LOCAL_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		dialogLayout.setVisibility(View.GONE);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == CUP_PIC_CODE) {// 剪裁返回
				String cutTempPath = data.getStringExtra(CropperActivity.CUT_TEMP_PATH);// 获取剪裁后的文件路径
				File cutTempFile = new File(cutTempPath);
				if (cutTempFile.exists()) {// 如果剪裁后的文件存在
					lastIntent.putExtra(KEY_UPLOAD_PIC_PATH, cutTempPath);
					setResult(Activity.RESULT_OK, lastIntent);
					finish();
				} else {
					setResult(Activity.RESULT_CANCELED);
					finish();
				}
			} else if (requestCode == SELECT_PIC_BY_LOCAL_PHOTO) {// 选择相册
				String localPhotoPath = data.getStringExtra(SelectPhotoFolderActivity.LOCAL_PHOTO_PATH);// 获取本地相册的图片
				Intent intent = new Intent();
				intent.setClass(PhotoSelectDialog.this, CropperActivity.class);
				intent.putExtra(CropperActivity.FROM_PHOTO_TYPE, CropperActivity.LOCAL_PHOTO_TYPE);
				intent.putExtra(CropperActivity.CROPPER_TEMP_PATH, localPhotoPath);
				intent.putExtra(CropperActivity.CROP_MODE, cropMode);//需要裁剪的图片形状规格
				// 跳转到裁剪页面
				startActivityForResult(intent, CUP_PIC_CODE);
			} else if (requestCode == SELECT_PIC_BY_CAMERA_PHOTO) {// 拍照
				Intent intent = new Intent();
				intent.setClass(PhotoSelectDialog.this, CropperActivity.class);
				intent.putExtra(CropperActivity.CROPPER_TEMP_PATH, cameraTempFile.getAbsolutePath());
				intent.putExtra(CropperActivity.FROM_PHOTO_TYPE, CropperActivity.CAMERA_TYPE);
				intent.putExtra(CropperActivity.CROP_MODE, cropMode);//需要裁剪的图片形状规格
				// 跳转到裁剪页面
				startActivityForResult(intent, CUP_PIC_CODE);
			}
		} else {// 用户取消退出
			setResult(Activity.RESULT_CANCELED, lastIntent);
			finish();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
