package com.eshangke.framework.ui.activities;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.eshangke.framework.R;

/**
 * 类的说明：属性动画
 * 作者：shims
 * 创建时间：2016/8/31 0031 14:31
 */
public class PropertyAnimationActivity extends BaseActivity implements View.OnClickListener{
    private ImageView imgPic;
    private Button btnAlpha, btnScale, btnTranslate, btnRotate;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_animation);
        initView();

        btnAlpha.setOnClickListener(this);
        btnScale.setOnClickListener(this);
        btnTranslate.setOnClickListener(this);
        btnRotate.setOnClickListener(this);

        imgPic.setImageResource(R.drawable.publicloading);
    }
    /**
     * 初始化组件
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        imgPic = (ImageView) findViewById(R.id.imgPic);
        btnAlpha = (Button) findViewById(R.id.btnAlpha);
        btnScale = (Button) findViewById(R.id.btnScale);
        btnTranslate = (Button) findViewById(R.id.btnTranslate);
        btnRotate = (Button) findViewById(R.id.btnRotate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAlpha://渐变透明度动画效果
                break;

            case R.id.btnScale://渐变尺寸伸缩动画效果
                break;

            case R.id.btnTranslate://画面转换位置移动动画效果
                ObjectAnimator.ofFloat(imgPic,"rotation",0,360F).setDuration(1000).start();
                break;

            case R.id.btnRotate://画面转移旋转动画效果
                ObjectAnimator.ofFloat(imgPic,"rotation",0,360F).setDuration(1000).start();
                break;

        }
    }
}
