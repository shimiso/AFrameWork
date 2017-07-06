package com.eshangke.framework.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

/**
 * 友盟分享界面
 */
public class UmengShareActivity extends BaseActivity {
    private CheckBox cb;

    public void onClick(View view) {

        UMImage image = new UMImage(UmengShareActivity.this, "http://www.umeng.com/images/pic/social/integrated_3.png");
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.com_facebook_button_icon);
//        UMImage image = new UMImage(UmengShareActivity.this, bitmap);
        UMusic music = new UMusic("http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
        music.setTitle("sdasdasd");
        music.setThumb(new UMImage(UmengShareActivity.this, "http://www.umeng.com/images/pic/social/chart_1.png"));
        UMVideo video = new UMVideo("http://video.sina.com.cn/p/sports/cba/v/2013-10-22/144463050817.html");

        switch (view.getId()) {

            case R.id.app_open_share:
                ShareContent content = new ShareContent();
                content.mText = "分享内容";
                content.mTitle = "分享标题";
                content.mTargetUrl = "http://www.baidu.com";
                /**shareboard  need the platform all you want and callbacklistener,then open it**/
                new ShareAction(this).setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setContentList(new ShareContent(), content)
                        .withMedia(image)
                        .setListenerList(umShareListener, umShareListener)
                        .open();

            case R.id.app_share_sina:
                //新浪，当同时传递URL参数和图片时，注意确保图片不能超过32K，否则无法分享，不传递URL参数时图片不受32K限制
                /** shareaction need setplatform , callbacklistener,and content(text,image).then share it **/
                new ShareAction(this).setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener)
                        .withText("hello umeng video")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_douban:
                new ShareAction(this).setPlatform(SHARE_MEDIA.DOUBAN).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;

            case R.id.app_share_email:
                new ShareAction(this).setPlatform(SHARE_MEDIA.EMAIL).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(music)
                        .withTitle("dddddd")
                        .share();
                break;
            case R.id.app_share_wx:
                music.setTargetUrl("http://www.baidu.com");
                new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener)
                        .withMedia(image)
                        .withText("hello umeng")
                                //.withMedia(new UMEmoji(UmengShareActivity.this,"http://img.newyx.net/news_img/201306/20/1371714170_1812223777.gif"))
                        .share();
                break;
            case R.id.app_share_wx_circle:
                new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(music)
                        .share();
                break;
            case R.id.app_share_sms:
                new ShareAction(this).setPlatform(SHARE_MEDIA.SMS).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_qq:
                new ShareAction(this).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(music)
                        .withTitle("qqshare")
                        .withTargetUrl("http://dev.umeng.com")
                        .share();
                break;
            case R.id.app_share_qzone:
                new ShareAction(this).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_yixin:
                new ShareAction(this).setPlatform(SHARE_MEDIA.YIXIN).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_yixin_circle:
                new ShareAction(this).setPlatform(SHARE_MEDIA.YIXIN_CIRCLE).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_ynote:
                new ShareAction(this).setPlatform(SHARE_MEDIA.YNOTE).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_evernote:
                new ShareAction(this).setPlatform(SHARE_MEDIA.EVERNOTE).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_facebook:
                new ShareAction(this).setPlatform(SHARE_MEDIA.FACEBOOK).setCallback(umShareListener)
                        .withTargetUrl("http://www.tuicool.com/articles/rABJBz")
                        .share();
                break;
            case R.id.app_share_laiwang:
                new ShareAction(this).setPlatform(SHARE_MEDIA.LAIWANG).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_line:
                new ShareAction(this).setPlatform(SHARE_MEDIA.LINE).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_linkedin:
                new ShareAction(this).setPlatform(SHARE_MEDIA.LINKEDIN).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withTitle("this is cool")
                        .withTargetUrl("https://www.americanexpress.com/us/small-business/openforum/programhub/managing-your-money/?pillar=critical-numbers")
                        .share();
                break;
            case R.id.app_share_twitter:
                new ShareAction(this).setPlatform(SHARE_MEDIA.TWITTER).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_tencent:
                new ShareAction(this).setPlatform(SHARE_MEDIA.TENCENT).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_kakao:
                new ShareAction(this).setPlatform(SHARE_MEDIA.KAKAO).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;
            case R.id.app_share_alipay:
                new ShareAction(this).setPlatform(SHARE_MEDIA.ALIPAY).setCallback(umShareListener)
                        .withText("hello umeng")
                        .withMedia(image)
                        .share();
                break;


        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umeng_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        /** need not init ,but must config App.java**/
        cb = (CheckBox) findViewById(R.id.checkBox_close_editor);
        cb.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                Config.OpenEditor = true;
            } else {
                Config.OpenEditor = false;//open editpage
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //友盟页面路径统计
        MobclickAgent.onPageStart("友盟分享界面");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟页面路径统计
        MobclickAgent.onPageEnd("友盟分享界面");
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(UmengShareActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(UmengShareActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(UmengShareActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    private ShareBoardlistener shareBoardlistener = (snsPlatform, share_media) -> new ShareAction(UmengShareActivity.this).setPlatform(share_media).setCallback(umShareListener)
            .withText("多平台分享")
            .share();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}
