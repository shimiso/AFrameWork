package com.eshangke.framework.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.eshangke.framework.R;
import com.eshangke.framework.ui.activities.BaseActivity;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 类的说明：图片预览功能
 * 作者：shims
 * 创建时间：2016/2/3 0003 15:28
 */
public class PhotoDetailActivity extends BaseActivity implements PhotoViewAttacher.OnViewTapListener {
	private PhotoView detail_img_srcImage;
	/** 向左的按钮 */
	private ImageButton btn_turn_left;
	/** 总体容器 */
	private RelativeLayout rl_container;
	/** 向右的按钮 */
	private ImageButton btn_turn_right;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_detail);
		//初始化View
		initViews();

		btn_turn_left.setOnClickListener(listener);
		btn_turn_right.setOnClickListener(listener);
		/** 加载中默认不可点击 */
		btn_turn_left.setEnabled(false);
		btn_turn_right.setEnabled(false);

		Intent intent = getIntent();
		String url = intent.getStringExtra("IMAGE_URL");
		detail_img_srcImage.setScaleType(ScaleType.FIT_CENTER);
		detail_img_srcImage.setOnViewTapListener(this);
		if (!TextUtils.isEmpty(url)) {
			Glide.with(context)
					.load(url)
					.asBitmap()
					.placeholder(R.drawable.publicloading)//加载动画
					.error(R.drawable.image_default)//出错默认图
					.fitCenter()
					.into(new BitmapImageViewTarget(detail_img_srcImage){
						@Override
						protected void setResource(Bitmap resource) {
							detail_img_srcImage.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
									RelativeLayout.LayoutParams.MATCH_PARENT));
							view.setImageBitmap(resource);
							btn_turn_left.setEnabled(true);
							btn_turn_right.setEnabled(true);
						}
					});
		}
	}
	void initViews() {
		context=this;
		detail_img_srcImage = (PhotoView) findViewById(R.id.detail_img_srcImage);
		btn_turn_left= (ImageButton) findViewById(R.id.btn_turn_left);
		btn_turn_right= (ImageButton) findViewById(R.id.btn_turn_right);
		rl_container= (RelativeLayout) findViewById(R.id.rl_container);
	}

	/** 向左、向右的单击事件 */
	View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_turn_left:// 向左的按钮
				detail_img_srcImage.setRotationBy(-90);
				break;
			case R.id.btn_turn_right:// 向右的按钮
				detail_img_srcImage.setRotationBy(90);
				break;
			}
		}
	};
	public void onBackPressed() {
		finish();
	}
	/**
	 * 单击屏幕，activity消失
	 */
	@Override
	public void onViewTap(View arg0, float arg1, float arg2) {
		PhotoDetailActivity.this.finish();
	}
}
