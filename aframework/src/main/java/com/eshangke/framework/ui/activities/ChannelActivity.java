package com.eshangke.framework.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.eshangke.framework.R;
import com.eshangke.framework.event.ChannelChangeEvent;
import com.eshangke.framework.event.ChannelItemMoveEvent;
import com.eshangke.framework.bean.Channel;
import com.eshangke.framework.presenter.ChannelPresenter;
import com.eshangke.framework.ui.adapter.ChannelAdapter;
import com.eshangke.framework.util.RxBus;
import com.eshangke.framework.widget.ItemDragHelperCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * 频道管理
 */
public class ChannelActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.news_channel_mine_rv)
    RecyclerView mNewsChannelMineRv;
    @BindView(R.id.news_channel_more_rv)
    RecyclerView mNewsChannelMoreRv;


    private ChannelAdapter mNewsChannelAdapterMine;
    private ChannelAdapter mNewsChannelAdapterMore;
    ChannelPresenter channelPresenter;
    protected Subscription mSubscription;
    private boolean mIsChannelChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscription = RxBus.getInstance().toObservable(ChannelItemMoveEvent.class)
                .subscribe(channelItemMoveEvent -> {
                    int fromPosition = channelItemMoveEvent.getFromPosition();
                    int toPosition = channelItemMoveEvent.getToPosition();
                    channelPresenter.onItemMove(fromPosition, toPosition);
                    mIsChannelChanged = true;
                }, throwable -> {
                    Log.e("mSubscription", throwable.toString());
                });
        setContentView(R.layout.channel_activity);
        ButterKnife.bind(this);
        channelPresenter = new ChannelPresenter(this);
        initView();
        List<Channel> channelsMine = channelPresenter.getChannelsMine();
        List<Channel> channelsMore = channelPresenter.getChannelsMore();
        initRecyclerView(mNewsChannelMineRv, channelsMine, true);
        initRecyclerView(mNewsChannelMoreRv, channelsMore, false);

    }

    private void initRecyclerView(RecyclerView recyclerView, List<Channel> channels
            , final boolean isChannelMine) {
        // !!!加上这句将不能动态增加列表大小。。。
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (isChannelMine) {
            mNewsChannelAdapterMine = new ChannelAdapter(channels);
            recyclerView.setAdapter(mNewsChannelAdapterMine);
            setChannelMineOnItemClick();

            initItemDragHelper();
        } else {
            mNewsChannelAdapterMore = new ChannelAdapter(channels);
            recyclerView.setAdapter(mNewsChannelAdapterMore);
            setChannelMoreOnItemClick();
        }

    }

    //点击更多频道
    private void setChannelMoreOnItemClick() {
        mNewsChannelAdapterMore.setOnItemClickListener((view, position) -> {
            Channel channel = mNewsChannelAdapterMore.getData().get(position);
            mNewsChannelAdapterMine.add(mNewsChannelAdapterMine.getItemCount(), channel);
            mNewsChannelAdapterMore.delete(position);
            channelPresenter.onItemAddOrRemove(channel, false);
            mIsChannelChanged = true;
        });
    }

    //点击我的频道
    private void setChannelMineOnItemClick() {
        mNewsChannelAdapterMine.setOnItemClickListener((view, position) -> {
            Channel channel = mNewsChannelAdapterMine.getData().get(position);
            boolean isNewsChannelFixed = channel.getChannelFixed();

            //不是固定位置才可以移动
            if (!isNewsChannelFixed) {
                mNewsChannelAdapterMore.add(mNewsChannelAdapterMore.getItemCount(), channel);
                mNewsChannelAdapterMine.delete(position);
                channelPresenter.onItemAddOrRemove(channel, true);
                mIsChannelChanged = true;
            }
        });
    }

    private void initItemDragHelper() {
        ItemDragHelperCallback itemDragHelperCallback = new ItemDragHelperCallback(mNewsChannelAdapterMine);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragHelperCallback);
        itemTouchHelper.attachToRecyclerView(mNewsChannelMineRv);

        mNewsChannelAdapterMine.setItemDragHelperCallback(itemDragHelperCallback);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
            if(mIsChannelChanged){
                RxBus.getInstance().post(new ChannelChangeEvent());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mIsChannelChanged){
            RxBus.getInstance().post(new ChannelChangeEvent());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
