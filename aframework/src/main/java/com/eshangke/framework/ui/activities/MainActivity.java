package com.eshangke.framework.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.eshangke.framework.R;
import com.eshangke.framework.event.ChannelChangeEvent;
import com.eshangke.framework.event.ScrollToTopEvent;
import com.eshangke.framework.jpush.JpushActivity;
import com.eshangke.framework.bean.Channel;
import com.eshangke.framework.presenter.ChannelPresenter;
import com.eshangke.framework.ui.adapter.MainPagerAdapter;
import com.eshangke.framework.ui.fragment.BooksListFragment;
import com.eshangke.framework.util.RxBus;
import com.eshangke.framework.widget.PhotoDetailActivity;
import com.eshangke.framework.widget.photoselect.PhotoSelectDialog;
import com.handmark.pulltorefresh.samples.PullToRefreshLauncherActivity;
import com.umeng.analytics.MobclickAgent;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;

public class MainActivity extends BaseActivity {
    private Context context;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    TabLayout mTabs;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    ActionBarDrawerToggle drawerListener;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    CircleImageView headImageView;

    private List<Fragment> mNewsFragmentList = new ArrayList<>();

    //截图页面
    public static final int SELECT_PIC = 5;

    // 拍照截图后压缩的需要上传的图
    public final String KEY_UPLOAD_PIC_PATH = "key_upload_pic_path";

    private String smallPath;

    //ViewPager当前页索引
    int currentIndex = 2;

    ChannelPresenter channelPresenter;
    protected Subscription mSubscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //友盟统计debug模式开关
        MobclickAgent.setDebugMode(true);
        //禁止友盟自动统计页面
        MobclickAgent.openActivityDurationTrack(false);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        channelPresenter =new ChannelPresenter(this);
        context = this;
        androidUtil.checkStoragePathAndSetBaseApp();
        setSupportActionBar(toolbar);

        drawerListener = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //开
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //关
            }
        };
        //设置drawer的开关监听
        drawerLayout.addDrawerListener(drawerListener);
        //该方法会自动和Toolbar关联, 将开关的图片显示在了Toolbar上，如果不设置，也可以有抽屉的效果，不过是默认的图标
        drawerListener.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;
            switch (id) {
                case R.id.book_xlist_sample://XListView例子
                    intent = new Intent(context, BooksXListActivity.class);//暂时不进入指导页面
                    break;
                case R.id.media_player_sample://视频播放
                    intent = new Intent(context, MediaPlayerActivity.class);

                    break;
                case R.id.file_download_sample://文件下载
                    intent = new Intent(context, HttpSampleActivity.class);
                    break;
                case R.id.save_sample://存储例子
                    intent = new Intent(context, SaveActivity.class);
                    break;
                case R.id.jpush_sample://极光推送
                    intent = new Intent(context, JpushActivity.class);
                    break;
                case R.id.swiperefresh_sample://Swipe刷新
                    intent = new Intent(context, SwipeRefreshActivity.class);
                    break;
                case R.id.image_detail_sample://图片查看例子
                    intent = new Intent(context, PhotoDetailActivity.class);
                    intent.putExtra("IMAGE_URL", "http://res.eshangke.com/newsphoto/2016/01/27/1453878920.jpg");
                    break;
                case R.id.pulltorefresh_sample://PullToRefresh例子
                    intent = new Intent(context, PullToRefreshLauncherActivity.class);
                    break;
                case R.id.bottom_tab_sample:// BottomTab切换例子
                    intent = new Intent(context, BottomTabActivity.class);
                    break;
                case R.id.coordinator_layout_sample:// CoordinatorLayout例子
                    intent = new Intent(context, CoordinatorLayoutActivity.class);
                    break;
                case R.id.ndk_sample:// NDK例子
                    intent = new Intent(context, NDKActivity.class);
                    break;
                case R.id.anim_sample:// 动画例子
                    intent = new Intent(context, AnimationActivity.class);
                    break;
                case R.id.property_anim_sample:// 属性动画例子
                    intent = new Intent(context, PropertyAnimationActivity.class);
                    break;
                case R.id.recorder_sample:// 语音例子
                    intent = new Intent(context, MediaRecorderActivity.class);
                    break;
                case R.id.customerview_sample:// 自定义view例子
                    intent = new Intent(context, CustomerViewActivity.class);
                    break;
                case R.id.rx_android_sample:// 自定义view例子
                    intent = new Intent(context, RxAndroidSampleActivity.class);
                    break;
            }
            if (intent != null)
                startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        View headerView = navigationView.getHeaderView(0);
        headImageView = (CircleImageView) headerView.findViewById(R.id.head_image);
        headImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PhotoSelectDialog.class);
            intent.putExtra("fixAspectRatio", true);
            startActivityForResult(intent, SELECT_PIC);
        });
        mSubscription = RxBus.getInstance().toObservable(ChannelChangeEvent.class)
                .subscribe(channelChangeEvent -> {
                    setViewPager();
                });
        setViewPager();
    }

    @OnClick({R.id.fab, R.id.add_channel_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab://滑动到顶部
                RxBus.getInstance().post(new ScrollToTopEvent());
                break;
            case R.id.add_channel_iv://频道管理
                Intent intent = new Intent(context,ChannelActivity.class);
                startActivityForResult(intent, 1001);
                break;
        }

    }
    /**
     * 设置ViewPager
     */
    private void setViewPager() {
        mNewsFragmentList.clear();
        List<Channel>  channels=channelPresenter.getChannelsMine();
        List<String> channelNames = new ArrayList<>();
        //设置FragmentList
        for (int i = 0; i < channels.size(); i++) {
            Channel channel =channels.get(i);
            channelNames.add(channel.getChannelName());
            mNewsFragmentList.add(BooksListFragment.newInstance(channel));

        }

        //设置FragmentPagerAdapter
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), channelNames, mNewsFragmentList);
        mViewPager.setAdapter(adapter);

        //关联ViewPager和TabLayout
        mTabs.setupWithViewPager(mViewPager);

        //设置ViewPager滑动监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(currentIndex);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PIC:
                    // 设置用户头像
                    smallPath = data.getStringExtra(KEY_UPLOAD_PIC_PATH);
                    Glide.with(context)
                            .load(new File(smallPath))
                            .into(headImageView);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //END即gravity.right 从右向左显示   START即left  从左向右弹出显示
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //友盟事件统计
            MobclickAgent.onEvent(context, "action_settings");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
