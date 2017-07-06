package com.eshangke.framework.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eshangke.framework.R;
import com.eshangke.framework.ui.adapter.ToolbarAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 类的说明：CoordinatorLayout
 * 作者：shims
 * 创建时间：2016/2/2 0002 18:06
 */
public class CoordinatorLayoutActivity extends BaseActivity {
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout = null;

    @BindView(R.id.toolbar1)
    View mToolbar1 = null;

    @BindView(R.id.toolbar2)
    View mToolbar2 = null;

    @BindView(R.id.myRecyclerView)
    RecyclerView myRecyclerView;

    @BindView(R.id.img_zhangdan)
    ImageView mZhangdan = null;

    @BindView(R.id.img_zhangdan_txt)
    TextView mZhangdan_txt = null;

    @BindView(R.id.tongxunlu)
    ImageView mTongxunlu = null;

    @BindView(R.id.jiahao)
    ImageView mJiahao=null;

    @BindView(R.id.img_shaomiao)
    ImageView mZhangdan2 = null;

    @BindView(R.id.img_fukuang)
    ImageView mShaoyishao = null;

    @BindView(R.id.img_search)
    ImageView mSearch = null;

    @BindView(R.id.img_zhaoxiang)
    ImageView mZhaoxiang = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coordinator_layout);
        ButterKnife.bind(this);

        myRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myRecyclerView.setAdapter(new ToolbarAdapter(this));

        /**
         * 设置AppBarLayout 的监听器addOnOffsetChangedListener来进行效果的处理。
         * 当verticalOffset=0的时候即使整个展开的是时候要做的就是显示要显示的，隐藏要隐藏的设置，
         * 在设置透明度，同理当verticalOffset等于appBarLayout.getTotalScrollRange()即等于最大值的时候，就是关闭的时候，处理的展开相反。
         * 当他在中间值的时候，通过`int alpha=255-Math.abs(verticalOffset)-150;得到要设置的透明度，减去150是为了让效果更明显。
         * 当alpha小于0的时候是执行展开的toolbar的透明度效果，反之大于0的时候是闭合时toolbar的透明图效果。
         */
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    //张开
                    mToolbar1.setVisibility(View.VISIBLE);
                    mToolbar2.setVisibility(View.GONE);
                    setToolbar1Alpha(255);
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    //收缩
                    mToolbar1.setVisibility(View.GONE);
                    mToolbar2.setVisibility(View.VISIBLE);
                    setToolbar2Alpha(255);
                } else {
                    int alpha = 255 - Math.abs(verticalOffset) - 150;
                    if (alpha <= 0) {
                        //收缩toolbar
                        mToolbar1.setVisibility(View.GONE);
                        mToolbar2.setVisibility(View.VISIBLE);
                        setToolbar2Alpha(Math.abs(verticalOffset));
                    } else {
                        //张开toolbar
                        mToolbar1.setVisibility(View.VISIBLE);
                        mToolbar2.setVisibility(View.GONE);
                        setToolbar1Alpha(alpha);
                    }
                }
            }
        });
    }

    //设置展开时各控件的透明度
    public void setToolbar1Alpha(int alpha){
        mZhangdan.getDrawable().setAlpha(alpha);
        mZhangdan_txt.setTextColor(Color.argb(alpha,255,255,255));
        mTongxunlu.getDrawable().setAlpha(alpha);
        mJiahao.getDrawable().setAlpha(alpha);
    }

    //设置闭合时各控件的透明度
    public void setToolbar2Alpha(int alpha){
        mZhangdan2.getDrawable().setAlpha(alpha);
        mShaoyishao.getDrawable().setAlpha(alpha);
        mSearch.getDrawable().setAlpha(alpha);
        mZhaoxiang.getDrawable().setAlpha(alpha);
    }
}
