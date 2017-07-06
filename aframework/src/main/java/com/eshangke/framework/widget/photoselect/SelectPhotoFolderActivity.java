package com.eshangke.framework.widget.photoselect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.eshangke.framework.ui.activities.BaseActivity;
import com.eshangke.framework.util.ToastUtil;
import com.eshangke.framework.widget.photoselect.bean.PhotoInfo;
import com.eshangke.framework.widget.photoselect.bean.PhotoSerializable;
import com.eshangke.framework.widget.photoselect.util.CheckImageLoaderConfiguration;

import java.util.List;


/**
 * 
 * 选择图片文件列表.
 * @author 史明松
 */
public class SelectPhotoFolderActivity extends BaseActivity implements PhotoFolderFragment.OnPageLodingClickListener, PhotoFragment.OnPhotoSelectClickListener {

	private Toolbar toolbar;//导航

	private Context context;

	private PhotoFolderFragment photoFolderFragment;

	private TextView tv_title, tv_finish;

//	private List<PhotoInfo> hasList;

	private FragmentManager manager;
	private int backInt = 0;
	private Intent lastIntent;
	/**
	 * 已选择图片数量
	 */
	private int count;
	/**选择的图片**/
	PhotoInfo mSelectPhoto;

	/**本地的图**/
	public static final String LOCAL_PHOTO_PATH = "LocalPhotoPath";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoselect_photofolder);
		//初始化View
		initViews();
		//设置导航
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		tv_title.setText("请选择相册");
		tv_finish.setVisibility(View.GONE);

		lastIntent = getIntent();
		count = getIntent().getIntExtra("count", 0);
		manager = getSupportFragmentManager();
//		hasList = new ArrayList<PhotoInfo>();

		//点击返回
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (backInt == 0) {//如果没进入过图片列表就直接退出
					finish();
				} else if (backInt == 1) {//如果进入过就跳转到图片文件列表去选择
					backInt--;
					mSelectPhoto = null;
//					hasList.clear();
					tv_finish.setVisibility(View.GONE);
					tv_title.setText("请选择相册");
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.show(photoFolderFragment).commit();
					manager.popBackStack(0, 0);
				}
			}
		});

		//点击完成将选中的图返回出去
		tv_finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSelectPhoto == null) {
					ToastUtil.showToast(SelectPhotoFolderActivity.this, "请选择一张图片", Toast.LENGTH_SHORT);
				} else {
					try {// 验证图片是否存在并完整
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(mSelectPhoto.getPath_absolute(), options);
						if (options.outMimeType == null) {
							ToastUtil.showToast(SelectPhotoFolderActivity.this, "图片不存在或已损坏,请重新选择", Toast.LENGTH_SHORT);
						} else {
							lastIntent.putExtra(LOCAL_PHOTO_PATH, mSelectPhoto.getPath_absolute());
							setResult(Activity.RESULT_OK, lastIntent);
							finish();
						}
					} catch (Exception e) {
						e.printStackTrace();
						ToastUtil.showToast(SelectPhotoFolderActivity.this, "图片不存在或已损坏,请重新选择", Toast.LENGTH_SHORT);
					}
				}

			}
		});

		photoFolderFragment = new PhotoFolderFragment();

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.body, photoFolderFragment);
		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();
	}
	void initViews() {
		context=this;
		//头部控件
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		tv_finish = (TextView) findViewById(R.id.header_tv_right);
		tv_title = (TextView) findViewById(R.id.header_tv_title);

	}
	@Override
	protected void onStart() {
		super.onStart();
		CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
	}

	/**
	 * 加载选中的相册
	 * @param list
     */
	@Override
	public void onPageLodingClickListener(List<PhotoInfo> list) {
		FragmentTransaction transaction = manager.beginTransaction();
		PhotoFragment photoFragment = new PhotoFragment();
		Bundle args = new Bundle();
		PhotoSerializable photoSerializable = new PhotoSerializable();
//		for (PhotoInfo photoInfoBean : list) {
//			photoInfoBean.setChoose(false);
//		}
		photoSerializable.setList(list);
		args.putInt("count", count);
		args.putSerializable("list", photoSerializable);
		photoFragment.setArguments(args);
		transaction = manager.beginTransaction();
		transaction.hide(photoFolderFragment).commit();
		transaction = manager.beginTransaction();
		transaction.add(R.id.body, photoFragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();
		backInt++;
		tv_finish.setVisibility(View.VISIBLE);
		tv_title.setText("请选择图片");
	}

	/**
	 * 点击获得选中某个图片
	 * @param photo
     */
	public void onPhotoSelectClickListener(PhotoInfo photo) {
		mSelectPhoto = photo;
		// title.setText("已选择"); +photo.getPath_file());
//		hasList.clear();
//		for (PhotoInfo photoInfoBean : list) {
//			if(photoInfoBean.isChoose()){
//				hasList.add(photoInfoBean);
//			}
//		}
//		title.setText("已选择"+hasList.size()+"张");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && backInt == 0) {
			finish();
		} else if (keyCode == KeyEvent.KEYCODE_BACK && backInt == 1) {
			backInt--;
//			hasList.clear();
			tv_title.setText("请选择相册");
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.show(photoFolderFragment).commit();
			manager.popBackStack(0, 0);
		}
		return false;
	}
}
