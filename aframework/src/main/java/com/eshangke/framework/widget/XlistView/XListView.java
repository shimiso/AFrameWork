/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.eshangke.framework.widget.XlistView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.eshangke.framework.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class XListView extends ListView implements OnScrollListener {

    //分页请求，每次请求的条数
    public static final int EVERY_PAGE_COUNT_TWENTY = 20;//分页请求，每次请求数据条数

    private float mLastY = -1; // save event y
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    private IXListViewListener mListViewListener;

    // -- header view
    private XListViewHeader mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private RelativeLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    private int mHeaderViewHeight; // header view's height
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false; // is refreashing.

    // -- footer view
    private XListViewFooter mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private boolean mIsFooterReady = false;

    // total list items, used to detect is at the bottom of listview.
    private int mTotalItemCount;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400; // scroll back duration
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
    // at bottom, trigger
    // load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
    // feature.
    private Context context;

    /**
     * @param context
     */
    public XListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public XListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public XListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        this.context = context;
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);

        // init header view
        mHeaderView = new XListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView
                .findViewById(R.id.xlistview_header_content);
        mHeaderTimeView = (TextView) mHeaderView
                .findViewById(R.id.xlistview_header_time);
        addHeaderView(mHeaderView);

        // init footer view
        mFooterView = new XListViewFooter(context);

        // init header height
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mHeaderViewHeight = mHeaderViewContent.getHeight();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // make sure XListViewFooter is the last footer view, and only add once.
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
            //make sure "pull up" don't show a line in bottom when listview with one page
            setFooterDividersEnabled(false);
        } else {
            mPullLoading = false;
            mFooterView.show();
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
            //make sure "pull up" don't show a line in bottom when listview with one page
            setFooterDividersEnabled(false);//底部分割线不显示
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        setRefreshTime();
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
        }
    }

    /**
     * 方法说明：检查是否允许上拉刷新，adapter中的数据条数少于20条时禁止上拉加载,不显示“查看更多”按钮
     * 作者：chenlipeng
     * 创建时间：2016/3/31 17:41
     */
    public void checkIsNeedForbidPullLoadMore() {
        ListAdapter adapter = getAdapter();

        if (adapter != null) {
            int adapterSize = adapter.getCount();
            adapterSize = adapterSize - getHeaderViewsCount() - getFooterViewsCount();//实际条目需去除头部和底部的footer条目数
            if (adapterSize < EVERY_PAGE_COUNT_TWENTY) {//少于20条时禁止上拉加载,不显示“查看更多”按钮
                setPullLoadEnable(false);
            } else if (adapterSize == EVERY_PAGE_COUNT_TWENTY) {//当前总条数正好等于20条，需要重新设置允许上拉加载
                mEnablePullLoad = true;
                mPullLoading = false;
                mFooterView.show();
//                mFooterView.setState(XListViewFooter.STATE_NORMAL);//不设置底部文字，避免“已加载所有内容”被覆盖
                setFooterDividersEnabled(false);//底部分割线不显示
                // both "pull up" and "click" will invoke load more.
                mFooterView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLoadMore();
                    }
                });
            }
        }
    }

    /**
     * 方法说明：检查是否允许上拉刷新，adapter中的数据条数少于20条时禁止上拉加载,不显示“查看更多”按钮
     * <p/>
     * 作者：gaoxuan
     * 创建时间：2016/04/21 14:00
     *
     * @param listSize 列表里的item数量
     */
    public void checkIsNeedForbidPullLoadMore(int listSize) {
        if (listSize < EVERY_PAGE_COUNT_TWENTY) {//少于20条时禁止上拉加载,不显示“查看更多”按钮
            setPullLoadEnable(false);
        } else if (listSize == EVERY_PAGE_COUNT_TWENTY) {//当前总条数正好等于20条，需要重新设置允许上拉加载
            mPullLoading = false;
            mFooterView.show();
//                mFooterView.setState(XListViewFooter.STATE_NORMAL);//不设置底部文字，避免“已加载所有内容”被覆盖
            setFooterDividersEnabled(false);//底部分割线不显示
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * 设置成“已加载完所有内容”的状态，底部显示，不可点击，允许上拉继续请求数据
     * 修改人：chenlipeng
     * 修改时间：2016/3/1 14:23
     */
    public void setHasLoadedAllText() {
        setFooterText(this.getResources().getString(R.string.xlistview_footer_hint_loaded_all));
    }

    /**
     * 说明：设置底部文字为“查看更多”
     * 作者：chenlipeng
     * 创建时间：2016/3/1 19:54
     */
    public void setFooterTextNormal() {
        setFooterText(this.getResources().getString(R.string.xlistview_footer_hint_normal));
    }

    /**
     * 设置底部文字
     *
     * @param hintText
     */
    private void setFooterText(String hintText) {
        mFooterView.setFooterHintText(hintText);
    }

    /**
     * set last refresh time
     */
    public void setRefreshTime() {
        mHeaderTimeView.setText(new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date()));
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta
                + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(XListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(XListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                // more.
                mFooterView.setState(XListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(XListViewFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);

//		setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(XListViewFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
            if(!hasInternetConnected(false)){// 请求更多数据时没有网络，需停止动画
                stopLoadMore();
            }
        }
    }

    /**
     * 检测是否存在网络
     *
     * @return
     * @param isShowToastWithoutNet 无网络时是否显示 吐司 提示
     */
    public boolean hasInternetConnected(boolean isShowToastWithoutNet) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo network = manager.getActiveNetworkInfo();
            if (network != null && network.isConnectedOrConnecting()) {
                return true;
            }
        }
        if (isShowToastWithoutNet) {//需要提示时提示
            ToastUtil.showToast(context, R.string.check_connection, Toast.LENGTH_SHORT);
        }
        Log.i("XListView","XListView 无网络停止动画");
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0
                        && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1
                        && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (mEnablePullRefresh
                            && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        mPullRefreshing = true;
                        mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                            if(!hasInternetConnected(false)){//下拉刷新没有网络，需停止动画
                                stopRefresh();
                            }
                        }
                    }
                    resetHeaderHeight();
                }
                //此处不用else连接避免数据很少时，满足上方 if 条件，以下代码执行不到，导致松手后一直显示“松开加载更多”
                if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // invoke load more.
                    if (mEnablePullLoad
                            && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
                            && !mPullLoading) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    public void setXListViewListener(IXListViewListener l) {
        mListViewListener = l;
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {
        public void onXScrolling(View view);
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface IXListViewListener {
        public void onRefresh();

        public void onLoadMore();
    }


    /**
     * 是否可下拉刷新
     * 作者：chenlipeng
     * 创建时间：2016/3/7 14:29
     */
    public boolean isEnablePullRefresh() {
        return mEnablePullRefresh;
    }

    /**
     * 是否可上拉加载
     * 作者：chenlipeng
     * 创建时间：2016/3/7 14:30
     */
    public boolean isEnablePullLoad() {
        return mEnablePullLoad;
    }
}
