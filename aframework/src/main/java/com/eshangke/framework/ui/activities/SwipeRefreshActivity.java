package com.eshangke.framework.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.eshangke.framework.bean.Book;
import com.eshangke.framework.presenter.BookPresenter;
import com.eshangke.framework.ui.adapter.BooksRecyclerAdapter;
import com.eshangke.framework.util.ToastUtil;
import com.eshangke.framework.widget.ProgressView;
import com.eshangke.framework.widget.haorefresh.HaoRecyclerView;

import java.util.List;

public class SwipeRefreshActivity extends BaseActivity {

    private SwipeRefreshLayout swiperefresh;
    private HaoRecyclerView recycleview;
    private Context context;
    private BooksRecyclerAdapter booksAdapter;
    private BookPresenter bookPresenter = new BookPresenter();
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_refresh);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        context = this;
        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swiperefresh.setColorSchemeResources(R.color.blue, R.color.blue, R.color.blue,
                R.color.blue);
        swiperefresh.setOnRefreshListener(() -> {
            ToastUtil.showToast(context, "刷新了", Toast.LENGTH_LONG);
            onRefreshData();
        });


        recycleview = (HaoRecyclerView) findViewById(R.id.recycleview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleview.setLayoutManager(layoutManager);
        //设置自定义加载中和到底的效果
        ProgressView progressView = new ProgressView(this);
        progressView.setIndicatorId(ProgressView.BallGridPulse);
        progressView.setIndicatorColor(0xff69b3e0);
        recycleview.setFootLoadingView(progressView);
        recycleview.setLoadMoreListener(() -> {
            page = page + 1;
            onLoadMoreData();
        });
        booksAdapter = new BooksRecyclerAdapter(context);
        booksAdapter.setOnItemClickListener((view, position) -> {
            Book book = booksAdapter.getBook(position);

            Intent intent = new Intent();
            intent.setClass(context, BookDetailActivity.class);
            intent.putExtra("book", book);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SwipeRefreshActivity.this,
                    view.findViewById(R.id.ivBook), "transition_book_img");
            ActivityCompat.startActivity(SwipeRefreshActivity.this, intent, options.toBundle());
        });
        recycleview.setAdapter(booksAdapter);
        //首次加载数据
        onRefreshData();
    }


    protected void onRefreshData() {
        bookPresenter.searchBooks("三国", 1, 5, new BookPresenter.OnSearchListener() {

            @Override
            public void onData(List<Book> data) {
                booksAdapter.onRefresh(data, true);
                swiperefresh.setRefreshing(false);
            }

            @Override
            public void onError(Throwable ex) {
                ToastUtil.showToast(context, "出错拉", Toast.LENGTH_LONG);
            }
        });
    }

    protected void onLoadMoreData() {
        bookPresenter.searchBooks("三国", page, 5, new BookPresenter.OnSearchListener() {

            @Override
            public void onData(List<Book> data) {
                booksAdapter.onLoadMore(data, true);
                recycleview.loadMoreComplete();
            }

            @Override
            public void onError(Throwable ex) {
                page = page - 1;
                ToastUtil.showToast(context, "出错拉", Toast.LENGTH_LONG);
            }
        });
    }
}