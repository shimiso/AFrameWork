package com.eshangke.framework.widget.photoselect;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.eshangke.framework.R;
import com.eshangke.framework.widget.photoselect.adapter.PhotoAdapter;
import com.eshangke.framework.widget.photoselect.bean.PhotoInfo;
import com.eshangke.framework.widget.photoselect.bean.PhotoSerializable;
import com.eshangke.framework.widget.photoselect.util.UniversalImageLoadTool;

/**
 * 类的说明：图片列表
 * 作者：shims
 * 创建时间：2016/2/2 0002 10:15
 */
public class PhotoFragment extends Fragment {

	/**
	 * 类的说明：选中某个图片
	 * 作者：shims
	 * 创建时间：2016/2/2 0002 10:18
	 */
	public interface OnPhotoSelectClickListener {
		public void onPhotoSelectClickListener(PhotoInfo photo);

	}

	private OnPhotoSelectClickListener onPhotoSelectClickListener;

	private GridView gridView;
	private PhotoAdapter photoAdapter;

	private List<PhotoInfo> list;

	private int hasSelect = 1;

	private int count;
	int selectPos;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (onPhotoSelectClickListener == null) {
			onPhotoSelectClickListener = (OnPhotoSelectClickListener) activity;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.photoselect_photo_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		gridView = (GridView) getView().findViewById(R.id.gridview);

		Bundle args = getArguments();

		PhotoSerializable photoSerializable = (PhotoSerializable) args.getSerializable("list");
		list = new ArrayList<PhotoInfo>();
		list.addAll(photoSerializable.getList());
		hasSelect += count;

		photoAdapter = new PhotoAdapter(getActivity(), list, gridView);
		gridView.setAdapter(photoAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				选择多张图片
// 				if(list.get(position).isChoose()&&hasSelect>1){
//					list.get(position).setChoose(false);
//					hasSelect--;
//				}else if(hasSelect<10){
//					list.get(position).setChoose(true);
//					hasSelect++;
//				}else{
//					Toast.makeText(getActivity(), "最多选择9张图片！", Toast.LENGTH_SHORT).show();
//				}
				selectPos = position;
				photoAdapter.refreshView(position);
				//将选中的图通过回调告诉SelectPhotoFolderActivity
				onPhotoSelectClickListener.onPhotoSelectClickListener(list.get(selectPos));
			}
		});

		gridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == 0) {
					UniversalImageLoadTool.resume();
				} else {
					UniversalImageLoadTool.pause();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
	}
}
