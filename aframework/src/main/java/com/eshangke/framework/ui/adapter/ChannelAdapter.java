/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.eshangke.framework.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eshangke.framework.MainApplication;
import com.eshangke.framework.R;
import com.eshangke.framework.event.ChannelItemMoveEvent;
import com.eshangke.framework.listener.OnItemClickListener;
import com.eshangke.framework.bean.Channel;
import com.eshangke.framework.util.AndroidUtil;
import com.eshangke.framework.util.RxBus;
import com.eshangke.framework.widget.ItemDragHelperCallback;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 类的说明：
 * 作者：shims
 * 创建时间：2016/10/31 0031 16:25
 */
public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
		ItemDragHelperCallback.OnItemMoveListener {
	//固定频道
	private static final int TYPE_CHANNEL_FIXED = 0;
	//非固定频道
	private static final int TYPE_CHANNEL_NO_FIXED = 1;

	private ItemDragHelperCallback mItemDragHelperCallback;

	private OnItemClickListener mOnItemClickListener;

	protected List<Channel> mList;

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	public void setItemDragHelperCallback(ItemDragHelperCallback itemDragHelperCallback) {
		mItemDragHelperCallback = itemDragHelperCallback;
	}

	public ChannelAdapter(List<Channel> list) {
		mList = list;
	}

	public List<Channel> getData() {
		return mList;
	}

	@Override
	public NewsChannelViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_channel, parent, false);
		final NewsChannelViewHolder newsChannelViewHolder = new NewsChannelViewHolder(view);
		handleLongPress(newsChannelViewHolder);
		handleOnClick(newsChannelViewHolder);
		return newsChannelViewHolder;
	}

	private void handleLongPress(final NewsChannelViewHolder newsChannelViewHolder) {
		if (mItemDragHelperCallback != null) {
			newsChannelViewHolder.itemView.setOnTouchListener((v, event) -> {
                Channel channel = mList.get(newsChannelViewHolder.getLayoutPosition());
                boolean isChannelFixed = channel.getChannelFixed();

                //固定频道不能移动
                if (isChannelFixed) {
                    mItemDragHelperCallback.setLongPressEnabled(false);
                } else {
                    mItemDragHelperCallback.setLongPressEnabled(true);
                }
                return false;
            });
		}
	}

	private void handleOnClick(final NewsChannelViewHolder newsChannelViewHolder) {
		if (mOnItemClickListener != null) {
			newsChannelViewHolder.itemView.setOnClickListener(v -> {
                if (!AndroidUtil.isFastDoubleClick()) {
                    mOnItemClickListener.onItemClick(v, newsChannelViewHolder.getLayoutPosition());
                }
            });
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		final Channel channel = mList.get(position);
		String newsChannelName = channel.getChannelName();
		NewsChannelViewHolder viewHolder = (NewsChannelViewHolder) holder;
		viewHolder.mNewsChannelTv.setText(newsChannelName);

		if (channel.getChannelIndex() == 0) {
			viewHolder.mNewsChannelTv.setTextColor(ContextCompat
					.getColor(MainApplication.getAppContext(),  R.color.dark_gray));
		}
	}


	/**
	 * 获取频道类型 固定和非固定
	 * @param position
	 * @return
     */
	@Override
	public int getItemViewType(int position) {
		Boolean isFixed = mList.get(position).getChannelFixed();
		if (isFixed) {
			return TYPE_CHANNEL_FIXED;
		} else {
			return TYPE_CHANNEL_NO_FIXED;
		}
	}

	@Override
	public int getItemCount() {
		if (mList == null) {
			return 0;
		}
		return  mList.size();
	}
	public void add(int position, Channel item) {
		mList.add(position, item);
		notifyItemInserted(position);
	}
	public void delete(int position) {
		mList.remove(position);
		notifyItemRemoved(position);
	}
	/**
	 * 频道移动
	 * @param fromPosition
	 * @param toPosition
     * @return
     */
	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		if (isChannelFixed(fromPosition, toPosition)) {
			return false;
		}
		Collections.swap(mList, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);
		RxBus.getInstance().post(new ChannelItemMoveEvent(fromPosition, toPosition));
		return true;
	}

	/**
	 * 检测移动的两个频道是否为固定
	 * @param fromPosition
	 * @param toPosition
     * @return
     */
	private boolean isChannelFixed(int fromPosition, int toPosition) {
		return mList.get(fromPosition).getChannelFixed() ||
				mList.get(toPosition).getChannelFixed();
	}

	class NewsChannelViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.news_channel_tv)
		TextView mNewsChannelTv;

		public NewsChannelViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
