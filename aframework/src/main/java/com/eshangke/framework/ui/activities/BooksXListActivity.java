package com.eshangke.framework.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.eshangke.framework.bean.Book;
import com.eshangke.framework.bean.Version;
import com.eshangke.framework.presenter.BookPresenter;
import com.eshangke.framework.ui.adapter.BookListAdapter;
import com.eshangke.framework.ui.adapter.BookListClassifyAdapter;
import com.eshangke.framework.ui.adapter.BookListConstellationAdapter;
import com.eshangke.framework.ui.adapter.BookListGirdDropDownAdapter;
import com.eshangke.framework.util.LoadViewUtil;
import com.eshangke.framework.util.ToastUtil;
import com.eshangke.framework.widget.WrapListView;
import com.eshangke.framework.widget.XlistView.XListView;
import com.yyydjk.library.DropDownMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BooksXListActivity extends BaseActivity {

    private final static int MSG_WHAT_PERFORM_CLICK = 1;

    private Toolbar toolbar;//导航
    private BookListAdapter bookListAdapter;
    private XListView mListView;
    private ImageView loadingImageView;
    private TextView logout_btn;
    private Context context;
    /**
     * View工具类
     **/
    protected LoadViewUtil viewUtil;
    private final int LOAD_FIRST_MODEL = 0;//首次加载
    private final int LOAD_REFRESH_MODEL = 1;//下拉刷新
    private final int LOAD_MORE_MODEL = 2;//上拉加载更多

    private static final int PAGESIZE = 10;//每次请求数据大小
    private int currentPage = 0;//当前页面


    private BookPresenter bookPresenter = new BookPresenter();

    private LinearLayout topHeaderLayout;//顶部选择条的父布局

    private DropDownMenu selectHeaderDropDownMenu;//listview的选择条
    private List<View> selectHeaderPopupViews = new ArrayList<>();//listview选择条弹出框的view列表
    private DropDownMenu mDropDownMenu;//顶部选择条
    private List<View> popupViews = new ArrayList<>();//顶部选择条弹出框的view列表
    private BookListGirdDropDownAdapter cityAdapter;//城市选择的adapter
    private BookListConstellationAdapter constellationAdapter;//星座选择的adapter
    private String classifyPeriod[] = {"不限", "小学", "初中", "高中"};//选择条年级分类的选项列表
    private String classifyPrimary[] = {"一年级", "二年级", "三年级", "四年级", "五年级", "六年级"};//选择条小学分类的选项列表
    private String classifyJunior[] = {"初一", "初二", "初三"};//选择条初中分类的选项列表
    private String classifySenior[] = {"高一", "高二", "高三"};//选择条高中分类的选项列表
    private String headers[] = {"年级", "城市", "星座"};//选择条的选项列表
    private String citys[] = {"不限", "武汉", "北京", "上海", "成都", "广州", "深圳", "重庆", "天津", "西安", "南京", "杭州"};
    private String constellations[] = {"不限", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"};
    private int constellationPosition = 0;//星座Position
    private int currentPeriodPostion = 0;//学段Position
    private int lastPeriodPostion = -1;//上次选择的学段Position
    private int currentGradePostion = -1;//年级Position

    private MyHandler handler = new MyHandler();

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //处理顶部选择条的点击事件
                case MSG_WHAT_PERFORM_CLICK:
                    View view = (View) msg.obj;
                    if (view != null)
                        view.performClick();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_xlist);
        //初始化View
        initViews();

        //设置导航
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        logout_btn.setOnClickListener(v -> {
            /*  版本更新测试*/
            Intent in = new Intent(context, VersionUpdateDialog.class);
            Version bean = new Version();
            bean.setVersionName(androidUtil.getApkVersionName());
            bean.setUpdateInfo("1.更新一些小bug");
            bean.setApkUrl("http://knowapp.b0.upaiyun.com/hz/knowbox_student_2631.apk");
            in.putExtra("bean", bean);
            startActivity(in);
        });

        mListView.setXListViewListener(new XListView.IXListViewListener() {

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
        //listview的第一个header，一般用于放置广告，图片轮播等
        View header = View.inflate(this, R.layout.book_xlist_stick_header, null);//头部内容
        header.setOnClickListener(v -> ToastUtil.showToast(BooksXListActivity.this, "点击了广告位", Toast.LENGTH_SHORT));
        mListView.addHeaderView(header);//添加头部
        //顶部的选择条
        View selectHeaderView = View.inflate(this, R.layout.book_xlist_stick, null);
        selectHeaderDropDownMenu = (DropDownMenu) selectHeaderView.findViewById(R.id.book_xlist_stick_dropDownMenu);
        TextView selectHeader1 = new TextView(this);
        TextView selectHeader2 = new TextView(this);
        TextView selectHeader3 = new TextView(this);
        TextView contentView = new TextView(this);
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        selectHeaderPopupViews.add(selectHeader1);
        selectHeaderPopupViews.add(selectHeader2);
        selectHeaderPopupViews.add(selectHeader3);
        selectHeaderDropDownMenu.setTabClickable(false);
        selectHeaderDropDownMenu.setDropDownMenu(Arrays.asList(headers), selectHeaderPopupViews, contentView);
        //清除listview选择条的内容页
        ViewGroup selectHeaderDropDownMenuContentLayout = (ViewGroup) selectHeaderDropDownMenu.getChildAt(2);
        selectHeaderDropDownMenuContentLayout.removeAllViews();
        //阻断并处理listview的选择条点击事件，使listview选择条位置跳到顶部与顶部选择条重合
        LinearLayout tabMenuView = (LinearLayout) selectHeaderDropDownMenu.getChildAt(0);
        for (int i = 0; i < tabMenuView.getChildCount(); i++) {
            if (tabMenuView.getChildAt(i) instanceof TextView) {
                final TextView tab = (TextView) tabMenuView.getChildAt(i);
                tab.setTag(i);
                tab.setOnClickListener(v -> {
                    //记录点击选项位置
                    currentTabIndex = (int) tab.getTag();
                    mListView.setSelection(2);
                    topHeaderLayout.setVisibility(View.VISIBLE);
                });
            }
        }
        mListView.addHeaderView(selectHeaderView);//ListView条目中的悬浮部分 添加到头部
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= 2) {
                    topHeaderLayout.setVisibility(View.VISIBLE);
                    if (currentTabIndex != -1) {
                        LinearLayout tabMenuView = (LinearLayout) mDropDownMenu.getChildAt(0);
                        Message message = Message.obtain();
                        message.what = MSG_WHAT_PERFORM_CLICK;
                        message.obj = tabMenuView.getChildAt(currentTabIndex);
                        handler.sendMessageDelayed(message, 100);
                        currentTabIndex = -1;
                    }
                } else {
                    topHeaderLayout.setVisibility(View.GONE);
                }
            }
        });
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        bookListAdapter = new BookListAdapter(this);
        //首次加载数据
        loadData(LOAD_FIRST_MODEL);
        mListView.setAdapter(bookListAdapter);
    }

    //在listview的选择条点击的选项位置,-1为未选状态
    private int currentTabIndex = -1;

    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing()) {
            mDropDownMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("InflateParams")
    void initViews() {
        context = this;
        topHeaderLayout = (LinearLayout) findViewById(R.id.invis);
        //头部控件
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        logout_btn = (TextView) findViewById(R.id.logout_btn);
        loadingImageView = (ImageView) findViewById(R.id.loading_image);
        mListView = (XListView) findViewById(R.id.listview);
        this.viewUtil = LoadViewUtil.init(getWindow().getDecorView(), this);
        addDropDownMenus();
    }

    /**
     * 设置顶部选择条
     */
    private void addDropDownMenus() {
        mDropDownMenu = (DropDownMenu) findViewById(R.id.dropDownMenu);
        //init classify menu
        final View classifyLayout = getLayoutInflater().inflate(R.layout.book_list_classify, null);
        WrapListView classifyOneList = (WrapListView) classifyLayout.findViewById(R.id.book_list_classify_one);
        WrapListView classifyTwoList = (WrapListView) classifyLayout.findViewById(R.id.book_list_classify_two);
        final BookListClassifyAdapter classifyOneListAdapter = new BookListClassifyAdapter(BookListClassifyAdapter.TYPE_PERIOD);
        classifyOneListAdapter.setData(Arrays.asList(classifyPeriod));
        classifyOneList.setAdapter(classifyOneListAdapter);
        currentPeriodPostion = 0;
        classifyOneListAdapter.setCheckItem(currentPeriodPostion);
        final BookListClassifyAdapter classifyTwoListAdapter = new BookListClassifyAdapter(BookListClassifyAdapter.TYPE_GRADE);
        classifyTwoList.setAdapter(classifyTwoListAdapter);
        classifyOneList.setOnItemClickListener((parent, view, position, id) -> {
            currentPeriodPostion = position;
            if (position == 0) {
                classifyOneListAdapter.setCheckItem(0);
                currentGradePostion = -1;
                classifyTwoListAdapter.setData(new ArrayList<String>());
                classifyTwoListAdapter.setCheckItem(-1);
                mDropDownMenu.setTabText(headers[0]);
                mDropDownMenu.closeMenu();
                //更新listview内的选择条文字
                LinearLayout tabMenuView = (LinearLayout) selectHeaderDropDownMenu.getChildAt(0);
                for (int i = 0; i < tabMenuView.getChildCount(); i++) {
                    if (tabMenuView.getChildAt(i) instanceof TextView) {
                        TextView tab = (TextView) tabMenuView.getChildAt(i);
                        if (i == 0) {
                            tab.setText(headers[0]);
                        }
                    }
                }
                return;
            } else if (position == 1) {
                classifyTwoListAdapter.setData(Arrays.asList(classifyPrimary));
            } else if (position == 2) {
                classifyTwoListAdapter.setData(Arrays.asList(classifyJunior));
            } else if (position == 3) {
                classifyTwoListAdapter.setData(Arrays.asList(classifySenior));
            }
            if (lastPeriodPostion == currentPeriodPostion) {
                classifyTwoListAdapter.setCheckItem(currentGradePostion);
            } else {
                classifyTwoListAdapter.setCheckItem(-1);
            }
        });
        classifyTwoList.setOnItemClickListener((parent, view, position, id) -> {
            lastPeriodPostion = currentPeriodPostion;
            currentGradePostion = position;
            String tabText = headers[0];
            if (currentPeriodPostion == 1) {
                tabText = classifyPrimary[position];
            } else if (currentPeriodPostion == 2) {
                tabText = classifyJunior[position];
            } else if (currentPeriodPostion == 3) {
                tabText = classifySenior[position];
            }
            classifyOneListAdapter.setCheckItem(currentPeriodPostion);
            classifyTwoListAdapter.setData(new ArrayList<String>());
            classifyTwoListAdapter.setCheckItem(position);
            mDropDownMenu.setTabText(tabText);
            mDropDownMenu.closeMenu();
            //更新listview内的选择条文字
            LinearLayout tabMenuView = (LinearLayout) selectHeaderDropDownMenu.getChildAt(0);
            for (int i = 0; i < tabMenuView.getChildCount(); i++) {
                if (tabMenuView.getChildAt(i) instanceof TextView) {
                    TextView tab = (TextView) tabMenuView.getChildAt(i);
                    if (i == 0) {
                        tab.setText(tabText);
                    }
                }
            }
        });

        //init city menu
        final ListView cityView = new ListView(this);
        cityAdapter = new BookListGirdDropDownAdapter(this, Arrays.asList(citys));
        cityView.setDividerHeight(0);
        cityView.setAdapter(cityAdapter);
        //init constellation
        final View constellationView = getLayoutInflater().inflate(R.layout.book_list_constellations, null);
        GridView constellation = (GridView) constellationView.findViewById(R.id.constellation);
        constellationAdapter = new BookListConstellationAdapter(this, Arrays.asList(constellations));
        constellation.setAdapter(constellationAdapter);
        TextView ok = (TextView) constellationView.findViewById(R.id.ok);
        ok.setOnClickListener(v -> {
            mDropDownMenu.setTabText(constellationPosition == 0 ? headers[2] : constellations[constellationPosition]);
            mDropDownMenu.closeMenu();
            //更新listview内的选择条文字
            LinearLayout tabMenuView = (LinearLayout) selectHeaderDropDownMenu.getChildAt(0);
            for (int i = 0; i < tabMenuView.getChildCount(); i++) {
                if (tabMenuView.getChildAt(i) instanceof TextView) {
                    TextView tab = (TextView) tabMenuView.getChildAt(i);
                    if (i == 4) {
                        tab.setText(constellationPosition == 0 ? headers[2] : constellations[constellationPosition]);
                    }
                }
            }
        });
        //add item click event
        cityView.setOnItemClickListener((parent, view, position, id) -> {
            cityAdapter.setCheckItem(position);
            mDropDownMenu.setTabText(position == 0 ? headers[1] : citys[position]);
            mDropDownMenu.closeMenu();
            //更新listview内的选择条文字
            LinearLayout tabMenuView = (LinearLayout) selectHeaderDropDownMenu.getChildAt(0);
            for (int i = 0; i < tabMenuView.getChildCount(); i++) {
                if (tabMenuView.getChildAt(i) instanceof TextView) {
                    TextView tab = (TextView) tabMenuView.getChildAt(i);
                    if (i == 2) {
                        tab.setText(position == 0 ? headers[1] : citys[position]);
                    }
                }
            }
        });
        constellation.setOnItemClickListener((parent, view, position, id) -> {
            constellationAdapter.setCheckItem(position);
            constellationPosition = position;
        });
        //init popupViews
        popupViews.add(classifyLayout);
        popupViews.add(cityView);
        popupViews.add(constellationView);
        //init dropdownview
        TextView contentView = new TextView(this);
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 加载数据
     *
     * @param model 加载模式
     */
    protected void loadData(final int model) {
        //如果没有网络就直接返回
        if (!androidUtil.hasInternetConnected()) {
            viewUtil.stopLoading();
            if (model == LOAD_FIRST_MODEL) {//如果是首次加载的话就显示错误页面
                viewUtil.showLoadingErrorView(LoadViewUtil.LOADING_NONET_VIEW, () -> {
                    viewUtil.startLoading();
                    loadData(LOAD_FIRST_MODEL);
                });
            }
            return;
        }

        if (model == LOAD_FIRST_MODEL) {//如果是首次加载就启动loadingView
            viewUtil.startLoading();
        }

        bookPresenter.searchBooks("三国", currentPage, PAGESIZE, new BookPresenter.OnSearchListener() {

            @Override
            public void onData(List<Book> data) {
                if (model == LOAD_FIRST_MODEL) {
                    bookListAdapter.onRefresh(data);
                    viewUtil.stopLoading();
                } else if (model == LOAD_REFRESH_MODEL) {
                    bookListAdapter.onRefresh(data);
                    mListView.stopRefresh();
                } else if (model == LOAD_MORE_MODEL) {
                    bookListAdapter.onLoadMore(data);
                    mListView.stopLoadMore();
                    currentPage= currentPage+1;
                }
                mListView.setRefreshTime();

            }

            @Override
            public void onError(Throwable ex) {
                currentPage= currentPage-1;
                viewUtil.stopLoading();
                viewUtil.showLoadingErrorView(LoadViewUtil.LOADING_ERROR_VIEW, () -> {
                    viewUtil.startLoading();
                    loadData(LOAD_FIRST_MODEL);
                });
            }
        });
    }
}
