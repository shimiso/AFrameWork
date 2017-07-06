package com.eshangke.framework.ui.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.eshangke.framework.R;
import com.eshangke.framework.util.RxSampleUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 类的说明：RxAndroid例子
 * 作者：shims
 * 创建时间：2016/11/18 0018 10:06
 */
public class RxAndroidSampleActivity extends BaseActivity {
    private final static String TAG = RxAndroidSampleActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar = null;
    @BindView(R.id.imageView)
    ImageView imageView;
    private String PATH = "http://pic32.nipic.com/20130829/12906030_124355855000_2.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rx_android_sample);
        ButterKnife.bind(this);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });
    }

    @OnClick({R.id.button, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                RxSampleUtils.createObserable();
                break;
            case R.id.button2:
                RxSampleUtils.createPrint();
                break;
            case R.id.button3:
                RxSampleUtils.from();
                break;
            case R.id.button4:
                RxSampleUtils.just();
                break;
            case R.id.button5:
                RxSampleUtils.filter();
                break;

            case R.id.button6:
                RxSampleUtils utils = new RxSampleUtils();
                //使用HTTP协议获取数据
                Observable<byte[]> observable=utils.downLoadImage(PATH);
                //subscribeOn()主要改变的是订阅的线程，即call()执行的线程;
                observable.subscribeOn(Schedulers.io());
                //observeOn()主要改变的是发送的线程，即onNext()执行的线程。
//                observable.observeOn(AndroidSchedulers.mainThread());
                observable.subscribe(new Subscriber<byte[]>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");//对话框消失
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    }
                });
                break;
        }
    }
}
