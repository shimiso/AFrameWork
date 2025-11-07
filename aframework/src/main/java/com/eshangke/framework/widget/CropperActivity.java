package com.eshangke.framework.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.eshangke.framework.R;
import com.eshangke.framework.ui.activities.BaseActivity;
import com.eshangke.framework.util.BitmapCompressUtil;
import com.eshangke.framework.util.BitmapUtil;
import com.eshangke.framework.util.SharePreferenceUtil;
import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类的说明：图片剪裁
 * 作者：shims
 * 创建时间：2016/2/2 0002 10:34
 */
public class CropperActivity extends BaseActivity {

	//上一个Intent意图
	private Intent lastIntent;

	/**剪裁后的图片**/
	private Bitmap croppedImage;

	/**压缩后的新路径**/
	private String compressPath;
	/**待剪切图片方位**/
	public int cropperTempDigree = 0;
	/**向右/向左旋转按钮**/
	private ImageView rotateButtonLeft, rotateButtonRight;
	/**待剪裁图片**/
	private CropImageView cropImageView;
	/**旋转按钮**/
	private ImageView cropButton;

	/**剪切好的图**/
	public static final String CUT_TEMP_PATH = "CutTempPath";

	/**图片来源类型**/
	public static final String FROM_PHOTO_TYPE ="FromPhotoType";
	/**图片来源类型**/
	private int fromPhotoType;
	/**拍照得到的图**/
	public static final int CAMERA_TYPE = 1;
	/**图库选择的图**/
	public static final int LOCAL_PHOTO_TYPE = 2;

	/**需要裁剪的文件路径**/
	public static final String CROPPER_TEMP_PATH ="CropperTempPath";
	/**需要裁剪的文件路径**/
	private String cropperTempPath;

	/**需要裁剪的图片形状规格**/
	public static final String CROP_MODE = "CropMode";
	/**需要裁剪的图片形状规格,默认按1:1**/
	public static int cropMode = 3;


	// Saves the state upon rotating the screen/restarting the activity
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
	}

	// Restores the state upon rotating the screen/restarting the activity
	@Override
	protected void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_crop);
		lastIntent = getIntent();
		cropperTempPath = lastIntent.getStringExtra(CROPPER_TEMP_PATH);
		fromPhotoType = lastIntent.getIntExtra(FROM_PHOTO_TYPE, LOCAL_PHOTO_TYPE);//默认按图库来源处理
		cropMode = lastIntent.getIntExtra(CROP_MODE, 3);

		// Initialize components of the app
		cropImageView = (CropImageView) findViewById(R.id.CropImageView);
		switch (cropMode){
			case 3:
				//按1:1裁剪
				cropImageView.setCropMode(CropImageView.CropMode.RATIO_1_1);
				break;
			case 6:
				//按任意形状剪裁
				cropImageView.setCropMode(CropImageView.CropMode.RATIO_FREE);
				break;
			case 8:
				//按圆形剪裁
				cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
				break;
		}

		new CompressTask(new File(cropperTempPath), fromPhotoType).execute();
//		cropImageView.setImageBitmap(loadBitmap(cropperTempPath, true));

		// 向右旋转
		rotateButtonRight = (ImageView) findViewById(R.id.button_rotate_right);
		rotateButtonRight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rotateButtonRight.setEnabled(false);
				rotateButtonLeft.setEnabled(false);
				cropButton.setEnabled(false);
				cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
				rotateButtonRight.setEnabled(true);
				cropButton.setEnabled(true);
				rotateButtonLeft.setEnabled(true);
			}
		});

		// 向左旋转
		rotateButtonLeft = (ImageView) findViewById(R.id.button_rotate_left);
		rotateButtonLeft.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rotateButtonRight.setEnabled(false);
				rotateButtonLeft.setEnabled(false);
				cropButton.setEnabled(false);
				cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_270D);
				rotateButtonRight.setEnabled(true);
				cropButton.setEnabled(true);
				rotateButtonLeft.setEnabled(true);
			}
		});

		// 裁剪
		cropButton = (ImageView) findViewById(R.id.Button_crop);
		cropButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cropButton.setEnabled(false);
				rotateButtonLeft.setEnabled(false);
				rotateButtonRight.setEnabled(false);
				croppedImage = cropImageView.getCroppedBitmap();
				new AsyncTask<String, Integer, File>() {
					LoadingDialog myDilaDialog = null;

					@Override
					protected void onPreExecute() {
						myDilaDialog = LoadingDialog.createDialog(CropperActivity.this);
						myDilaDialog.setCancelable(false);
						myDilaDialog.setMessage("正在保存中....");
						myDilaDialog.show();
					}
					@Override
					protected File doInBackground(String... arg0) {
						File cutTempFile = BitmapUtil.saveBitmapFile(croppedImage,spUtil.getCameraTempPath());
						return cutTempFile;
					}

					@Override
					protected void onPostExecute(File cutTempFile) {
						try {
							myDilaDialog.dismiss();
							// 拍摄的原图要删除
							if (fromPhotoType == CAMERA_TYPE) {
								File cropperTempFile = new File(cropperTempPath);
								if (cropperTempFile.exists()) {
									cropperTempFile.delete();
								}
							}
							// 压缩后的原图要删除
							File compressFile = new File(compressPath);
							if (compressFile.exists()) {
								compressFile.delete();
							}
							lastIntent.putExtra(CUT_TEMP_PATH, cutTempFile.getAbsolutePath());
							setResult(Activity.RESULT_OK, lastIntent);
							finish();
						} catch (Exception e) {
							return;
						}
					}
				}.execute();
			}
		});
	}

	/**
	 * 对拍好的照片进行压缩.
	 * @author 史明松
	 */
	class CompressTask extends AsyncTask<Void, Integer, Void> {
		private LoadingDialog myDilaDialog = null;

		/**待压缩图片**/
		private File tempFile;

		/**图片来源类型**/
		private int photoType;

		public CompressTask(File tempFile, int photoType) {
			this.tempFile = tempFile;
			this.photoType = photoType;
		}

		@Override
		protected void onPreExecute() {
			myDilaDialog = LoadingDialog.createDialog(CropperActivity.this);
			myDilaDialog.setCancelable(false);
			myDilaDialog.setMessage("正在载入中....");
			myDilaDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// 执行压缩操作
			String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			SharePreferenceUtil spUtil = new SharePreferenceUtil(CropperActivity.this);
			String newTempPath = spUtil.getCameraTempPath() + timeStamp + "_s.jpg";
			//获得图片方位
			cropperTempDigree = BitmapUtil.getBitmapDigree(tempFile.getAbsolutePath());
			compressPath = BitmapCompressUtil.getSmallBitmapAndSave(tempFile.getAbsolutePath(), newTempPath, 100, 60);
			if (null == compressPath) {
				compressPath = tempFile.getAbsolutePath();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			try {
				myDilaDialog.dismiss();
			} catch (Exception e) {
				return;
			}

			if (compressPath != null) {
				cropImageView.setImageBitmap(BitmapUtil.loadMatrixBitmap(compressPath,cropperTempDigree));
				// 压缩完成后将拍摄的原图删除掉
				if (photoType == CAMERA_TYPE) {
					try {
						if (tempFile != null && tempFile.exists()) {
							tempFile.delete();
						}
					} catch (Exception e) {
					}
				}
			} else {// 如果没有压缩成功，就原图返回
				cropImageView.setImageBitmap(BitmapUtil.loadMatrixBitmap(tempFile.getAbsolutePath(), cropperTempDigree));
//				setResult(Activity.RESULT_CANCELED);
//				finish();
			}
		}
	}

	/**
	 * 
	 * 这里关闭.
	 * @param view 
	 * @author 史明松
	 * @update 2014年11月5日 下午3:19:10
	 */
	public void closeButton(View view) {
		setResult(Activity.RESULT_CANCELED, lastIntent);
		finish();
	}
}
