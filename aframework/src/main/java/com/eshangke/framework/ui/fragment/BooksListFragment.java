package com.eshangke.framework.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eshangke.framework.R;
import com.eshangke.framework.event.ScrollToTopEvent;
import com.eshangke.framework.bean.Channel;
import com.eshangke.framework.ui.activities.BookDetailActivity;
import com.eshangke.framework.ui.adapter.BooksRecyclerAdapter;
import com.eshangke.framework.bean.Book;
import com.eshangke.framework.presenter.BookPresenter;
import com.eshangke.framework.util.LoadViewUtil;
import com.eshangke.framework.util.RxBus;
import com.eshangke.framework.view.BookListView;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

import rx.Subscription;

/**
 * 图书列表BooksRecycler例子
 */
public class BooksListFragment extends BaseFragment implements BookListView{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    //首次加载
    private final int LOAD_FIRST_MODEL = 0;
    //下拉刷新
    private final int LOAD_REFRESH_MODEL = 1;
    //上拉加载更多
    private final int LOAD_MORE_MODEL = 2;
    //每次请求数据大小
    private static final int PAGESIZE = 10;
    //当前页面
    private int currentPage=0;
    /** LoadView工具类 **/
    protected LoadViewUtil viewUtil;

    private XRecyclerView xRecyclerView;
    private BooksRecyclerAdapter booksAdapter;

    private BookPresenter bookPresenter=new BookPresenter(this);
    protected Subscription mSubscription;
    public BooksListFragment() {
        // Required empty public constructor
    }

    public static BooksListFragment newInstance(Channel channel) {
        BooksListFragment fragment = new BooksListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "");
        args.putString(ARG_PARAM2, "");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.book_recycler, container,false);
        this.viewUtil= LoadViewUtil.init(view,getActivity());

        xRecyclerView = (XRecyclerView) view.findViewById(R.id.recyclerView);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        xRecyclerView.setHasFixedSize(true);
        //创建默认的线性LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        xRecyclerView.setLayoutManager(layoutManager);
        xRecyclerView.setItemAnimator(new DefaultItemAnimator());


        xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        xRecyclerView.setArrowImageView(R.drawable.xlistview_arrow);

        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener(){

            @Override
            public void onRefresh() {
                //刷新数据
                loadData(LOAD_REFRESH_MODEL);
            }

            @Override
            public void onLoadMore() {
                //加载更多数据
                loadData(LOAD_MORE_MODEL);
            }
        });
        booksAdapter=new BooksRecyclerAdapter(getActivity());
        booksAdapter.setOnItemClickListener((view1, position) -> {
            Book book = booksAdapter.getBook(position);

            Intent intent = new Intent();
            intent.setClass(getActivity(), BookDetailActivity.class);
            intent.putExtra("book", book);

            ActivityOptionsCompat options =  ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            view1.findViewById(R.id.ivBook),"transition_book_img");
            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        });
        xRecyclerView.setAdapter(booksAdapter);

        //首次加载数据
        loadData(LOAD_FIRST_MODEL);
        registerScrollToTopEvent();
        return view;
    }
    private void registerScrollToTopEvent() {
        mSubscription = RxBus.getInstance().toObservable(ScrollToTopEvent.class)
                .subscribe(scrollToTopEvent -> {
                    xRecyclerView.getLayoutManager().scrollToPosition(0);
                });
    }

    /**
     * 加载数据
     * @param model 加载模式
     */
    protected void loadData(final int model) {
        //如果没有网络就直接返回
        if (!androidUtil.hasInternetConnected()) {
            viewUtil.stopLoading();
            if(model==LOAD_FIRST_MODEL){//如果是首次加载的话就显示错误页面
                viewUtil.showLoadingErrorView( viewUtil.LOADING_NONET_VIEW, () -> {
                    viewUtil.startLoading();
                    loadData(LOAD_FIRST_MODEL);
                });
            }
            return;
        }

        if(model==LOAD_FIRST_MODEL) {//如果是首次加载就启动loadingView
            viewUtil.startLoading();
        }
        bookPresenter.getBookList(model,currentPage,PAGESIZE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void loadMore(List<Book> books) {
        booksAdapter.onLoadMore(books,true);
        xRecyclerView.loadMoreComplete();
        currentPage=currentPage+1;
    }

    @Override
    public void loadRefresh(List<Book> books) {
        booksAdapter.onRefresh(books,true);
        xRecyclerView.refreshComplete();
    }

    @Override
    public void loadFirst(List<Book> books) {
        booksAdapter.onRefresh(books,true);
        viewUtil.stopLoading();
    }

    @Override
    public void loadError(Throwable e) {
        currentPage=currentPage-1;
        viewUtil.stopLoading();
        viewUtil.showLoadingErrorView(viewUtil.LOADING_ERROR_VIEW, () -> {
            viewUtil.startLoading();
            loadData(LOAD_FIRST_MODEL);
        });
    }
}
