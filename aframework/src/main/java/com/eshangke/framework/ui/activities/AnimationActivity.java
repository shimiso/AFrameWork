package com.eshangke.framework.ui.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.eshangke.framework.R;

/**
 * 类的说明：动画
 * 作者：shims
 * 创建时间：2016/8/31 0031 14:31
 */
public class AnimationActivity extends BaseActivity implements View.OnClickListener{
    private ImageView imgPic1,imgPic2;
    private Button btnAlpha, btnScale, btnTranslate, btnRotate,btnStart,btnStop;
    private Animation myAnimation;
    private Toolbar toolbar;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation);
        initView();

        btnAlpha.setOnClickListener(this);
        btnScale.setOnClickListener(this);
        btnTranslate.setOnClickListener(this);
        btnRotate.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        imgPic1.setImageResource(R.drawable.publicloading);
        //给动画资源赋值
        animationDrawable = (AnimationDrawable) imgPic1.getDrawable();
    }
    /**
     * 初始化组件
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        imgPic1 = (ImageView) findViewById(R.id.imgPic1);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);


        imgPic2 = (ImageView) findViewById(R.id.imgPic2);
        btnAlpha = (Button) findViewById(R.id.btnAlpha);
        btnScale = (Button) findViewById(R.id.btnScale);
        btnTranslate = (Button) findViewById(R.id.btnTranslate);
        btnRotate = (Button) findViewById(R.id.btnRotate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAlpha://渐变透明度动画效果
                myAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
                imgPic2.startAnimation(myAnimation);
                break;

            case R.id.btnScale://渐变尺寸伸缩动画效果
                myAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
                imgPic2.startAnimation(myAnimation);
                break;

            case R.id.btnTranslate://画面转换位置移动动画效果
                myAnimation = AnimationUtils.loadAnimation(this,R.anim.translate_anim);
                imgPic2.startAnimation(myAnimation);
                break;

            case R.id.btnRotate://画面转移旋转动画效果
                myAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
                imgPic2.startAnimation(myAnimation);
                break;
            case R.id.btnStart:
                animationDrawable.start();//开始
                break;

            case R.id.btnStop:
                animationDrawable.stop(); //停止
                break;

        }
    }
}
