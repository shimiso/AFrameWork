package com.eshangke.framework.widget.photoselect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.eshangke.framework.R;
import com.eshangke.framework.util.AndroidUtil;
import com.eshangke.framework.widget.photoselect.bean.PhotoInfo;
import com.eshangke.framework.widget.photoselect.imageaware.RotateImageViewAware;
import com.eshangke.framework.widget.photoselect.util.ThumbnailsUtil;
import com.eshangke.framework.widget.photoselect.util.UniversalImageLoadTool;

import java.util.List;


/**
 * 相片适配器
 * @author GuiLin
 */
public class PhotoAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<PhotoInfo> list;
	private ViewHolder viewHolder;
	private GridView gridView;
	private int width = 0;
	int selectPos = -1;

	public PhotoAdapter(Context context, List<PhotoInfo> list, GridView gridView) {
		width= AndroidUtil.getScreenWidth(context)/3;
		mInflater = LayoutInflater.from(context);
		this.list = list;
		this.gridView = gridView;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 刷新view
	 * @param index
	 */
	public void refreshView(int index) {
		selectPos = index;
		notifyDataSetChanged();
//		int visiblePos = gridView.getFirstVisiblePosition();
//		View view = gridView.getChildAt(index-visiblePos);
//		ViewHolder holder = (ViewHolder)view.getTag();
//	
//		if(list.get(index).isChoose()){
//			holder.selectImage.setImageResource(R.drawable.gou_selected);
//		}else{
//			holder.selectImage.setImageResource(R.drawable.gou_normal);
//		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.photoselect_photo_list_item, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView);
			viewHolder.selectImage = (ImageView) convertView.findViewById(R.id.selectImage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (selectPos == position) {
			viewHolder.selectImage.setVisibility(View.VISIBLE);
			viewHolder.selectImage.setImageResource(R.drawable.gou_selected);
		} else {
			viewHolder.selectImage.setVisibility(View.GONE);
//			viewHolder.selectImage.setImageResource(R.drawable.gou_normal);
		}
//		if(list.get(position).isChoose()){
//			viewHolder.selectImage.setImageResource(R.drawable.gou_selected);
//		}else{
//			viewHolder.selectImage.setImageResource(R.drawable.gou_normal);
//		}
		LayoutParams layoutParams = viewHolder.image.getLayoutParams();
		layoutParams.width = width;
		layoutParams.height = width;
		viewHolder.image.setLayoutParams(layoutParams);
		final PhotoInfo photoInfo = list.get(position);
		if (photoInfo != null) {
			UniversalImageLoadTool.disPlay(ThumbnailsUtil.MapgetHashValue(photoInfo.getImage_id(), photoInfo.getPath_file()), new RotateImageViewAware(viewHolder.image, photoInfo.getPath_absolute()), R.drawable.image_default);
//			UniversalImageLoadTool.disPlay(ThumbnailsUtil.MapgetHashValue(photoInfo.getImage_id(),photoInfo.getPath_file()),
//					viewHolder.image, R.drawable.common_defalt_bg);
		}
		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
		public ImageView selectImage;
	}
}
