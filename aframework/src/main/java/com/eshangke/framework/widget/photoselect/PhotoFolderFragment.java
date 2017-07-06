package com.eshangke.framework.widget.photoselect;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.eshangke.framework.R;
import com.eshangke.framework.widget.photoselect.adapter.PhotoFolderAdapter;
import com.eshangke.framework.widget.photoselect.bean.AlbumInfo;
import com.eshangke.framework.widget.photoselect.bean.PhotoInfo;
import com.eshangke.framework.widget.photoselect.util.ThumbnailsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 类的说明：相册文件列表
 * 作者：shims
 * 创建时间：2016/2/2 0002 10:04
 */
public class PhotoFolderFragment extends Fragment {

	/**
	 * 类的说明：加载选中的相册
	 * 作者：shims
	 * 创建时间：2016/2/2 0002 10:16
	 */
	public interface OnPageLodingClickListener {
		public void onPageLodingClickListener(List<PhotoInfo> list);

	}

	private OnPageLodingClickListener onPageLodingClickListener;

	private ListView listView;
	private TextView tv_empty;

	private ContentResolver cr;

	private List<AlbumInfo> listImageInfo = new ArrayList<AlbumInfo>();

	private PhotoFolderAdapter listAdapter;

	private LinearLayout loadingLay;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (onPageLodingClickListener == null) {
			onPageLodingClickListener = (OnPageLodingClickListener) activity;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.photoselect_photofolder_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listView = (ListView) getView().findViewById(R.id.listView);
		tv_empty = (TextView) getView().findViewById(R.id.empty_tv);
		listView.setEmptyView(tv_empty);

		loadingLay = (LinearLayout) getView().findViewById(R.id.loadingLay);

		cr = getActivity().getContentResolver();
		listImageInfo.clear();

		new ImageAsyncTask().execute();

		//点击加载选中的相册
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				onPageLodingClickListener.onPageLodingClickListener(listImageInfo.get(arg2).getList());
			}
		});
	}

	/**
	 * 类的说明：遍历获取图片缩略图和原图
	 * 作者：shims
	 * 创建时间：2016/2/2 0002 10:17
	 */
	private class ImageAsyncTask extends AsyncTask<Void, Void, Object> {

		@Override
		protected Object doInBackground(Void... params) {

			// 获取缩略图
			ThumbnailsUtil.clear();
			String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA };
			Cursor cur = null;
			try {
				cur = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
			} catch (Exception e) {
				Log.e("PhotoFolderFragment",e.toString());
			}

			if (cur != null && cur.moveToFirst()) {
				int image_id;
				String image_path;
				int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
				int dataColumn = cur.getColumnIndex(Thumbnails.DATA);
				do {
					image_id = cur.getInt(image_idColumn);
					image_path = cur.getString(dataColumn);
					ThumbnailsUtil.put(image_id, "file://" + image_path);
				} while (cur.moveToNext());
			}
			// 获取原图
			// Cursor cursor =
			// cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,null, null, MediaStore.Images.Media.DATE_MODIFIED);
			Cursor cursor = null;
			try {
				cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
			} catch (Exception e) {
				Log.e("PhotoFolderFragment",e.toString());
			}

			String _path = "_data";
			String _album = "bucket_display_name";

			HashMap<String, AlbumInfo> myhash = new HashMap<String, AlbumInfo>();
			AlbumInfo albumInfo = null;
			PhotoInfo photoInfo = null;
			if (cursor != null && cursor.moveToFirst()) {
				do {
					int index = 0;
					int _id = cursor.getInt(cursor.getColumnIndex("_id"));
					String path = cursor.getString(cursor.getColumnIndex(_path));
					String album = cursor.getString(cursor.getColumnIndex(_album));
					if (!album.contains("cameratemp")) {
						List<PhotoInfo> stringList = new ArrayList<PhotoInfo>();
						photoInfo = new PhotoInfo();
						if (myhash.containsKey(album)) {
							albumInfo = myhash.remove(album);
							if (listImageInfo.contains(albumInfo))
								index = listImageInfo.indexOf(albumInfo);
							photoInfo.setImage_id(_id);
							photoInfo.setPath_file("file://" + path);
							photoInfo.setPath_absolute(path);
							albumInfo.getList().add(photoInfo);
							listImageInfo.set(index, albumInfo);
							myhash.put(album, albumInfo);
						} else {
							albumInfo = new AlbumInfo();
							stringList.clear();
							photoInfo.setImage_id(_id);
							photoInfo.setPath_file("file://" + path);
							photoInfo.setPath_absolute(path);
							stringList.add(photoInfo);
							albumInfo.setImage_id(_id);
							albumInfo.setPath_file("file://" + path);
							albumInfo.setPath_absolute(path);
							albumInfo.setName_album(album);
							albumInfo.setList(stringList);
							listImageInfo.add(albumInfo);
							myhash.put(album, albumInfo);
						}
					}

				} while (cursor.moveToNext());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			loadingLay.setVisibility(View.GONE);
			if (getActivity() != null) {
				listAdapter = new PhotoFolderAdapter(getActivity(), listImageInfo);
				listView.setAdapter(listAdapter);
			}
		}
	}

}
