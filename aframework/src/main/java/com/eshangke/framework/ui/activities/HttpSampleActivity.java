package com.eshangke.framework.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.eshangke.framework.bean.Book;
import com.eshangke.framework.presenter.BookPresenter;
import com.eshangke.framework.util.HttpUtil;
import com.eshangke.framework.util.LoadViewUtil;
import com.eshangke.framework.util.download.DownloadManager;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * 类的说明：网络请求示例
 * 作者：chenlipeng
 * 创建时间：2016/1/29 15:58
 */
public class HttpSampleActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_get;
    private Button btn_post;
    private Button btn_add_down_task;
    private Button btn_download_manager;
    private ImageView loadingImageView;
    private Context context;
    /**
     * View工具类
     **/
    protected LoadViewUtil viewUtil;


    private BookPresenter bookPresenter = new BookPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_sample);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_get = (Button) findViewById(R.id.btn_get);
        btn_post = (Button) findViewById(R.id.btn_post);
        btn_add_down_task = (Button) findViewById(R.id.btn_add_down_task);
        btn_download_manager = (Button) findViewById(R.id.btn_download_manager);
        loadingImageView = (ImageView) findViewById(R.id.loading_image);
        this.viewUtil = LoadViewUtil.init(getWindow().getDecorView(), this);
        viewUtil.stopLoading();

        btn_post.setOnClickListener(this);
        btn_get.setOnClickListener(this);
        btn_add_down_task.setOnClickListener(this);
        btn_download_manager.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                requestSearchBookByGet();
                break;
            case R.id.btn_post:
                requestSearchBookByPost();
                break;
            case R.id.btn_add_down_task:
                addDownloadTast();
                break;
            case R.id.btn_download_manager:
                startActivity(new Intent(HttpSampleActivity.this, FileDownloadActivity.class));
                break;
        }
    }

    /**
     * 添加下载任务
     *
     * @throws DbException
     */
    private void addDownloadTast() {
        for (int i = 0; i < 5; i++) {
            String url = "http://dl.bintray.com/wyouflf/maven/org/xutils/xutils/3.3.8/xutils-3.3.8.aar";
            String label = i + "xUtils_" + System.nanoTime();
            try {
                DownloadManager.getInstance().startDownload(
                        url, label,
                        "/sdcard/xUtils/" + label + ".aar", true, false, null);
            } catch (DbException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 通过  GET 请求获取数据
     * 作者：chenlipeng
     * 创建时间：2016/1/29 16:16
     */
    private void requestSearchBookByGet() {
        viewUtil.startLoading();

        bookPresenter.requestBooksByGet("三国", 2, 20, new BookPresenter.OnSearchListener() {

            @Override
            public void onData(List<Book> data) {
                viewUtil.stopLoading();
                Toast.makeText(HttpSampleActivity.this, "GET data = " + data.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(Throwable ex) {
                viewUtil.stopLoading();
                viewUtil.showLoadingErrorView(LoadViewUtil.LOADING_ERROR_VIEW, () -> viewUtil.startLoading());
            }
        });
    }

    /**
     * 通过 POST 请求获取数据
     * 作者：chenlipeng
     * 创建时间：2016/1/29 16:16
     */
    private void requestSearchBookByPost() {
        viewUtil.startLoading();

        bookPresenter.requestBooksByPost("三国", 2, 20, new BookPresenter.OnSearchListener() {

            @Override
            public void onData(List<Book> data) {
                viewUtil.stopLoading();
                Toast.makeText(HttpSampleActivity.this, "POST data = " + data.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(Throwable ex) {
                viewUtil.stopLoading();
                viewUtil.showLoadingErrorView(LoadViewUtil.LOADING_ERROR_VIEW, () -> viewUtil.startLoading());
            }
        });
    }



    /**
     * 上传文件示例
     */
    public static void uploadFile(String filePath) {// 上传一张头像图片
        RequestParams params = new RequestParams("http://app.mb.eshangke.com/user/updateheadimg");
        // 添加到请求body体的参数, 只有POST, PUT, PATCH, DELETE请求支持.
        // params.addBodyParameter("wd", "xUtils");

        // 使用multipart表单上传文件
        // params.setMultipart(true);
        params.addParameter("scode", "XL8WwhquP444UgTfCbkPwbIGyGApGdYy");
        params.addParameter("version", "2");
        params.addParameter("channel_id", "2");
        params.addParameter("from", "2");
        params.addParameter("plant_id", "2");
        params.addParameter("type_id", "1");
        params.addParameter("userid", "69898");
        params.addBodyParameter(
                "image",
                new File(filePath),// 本地文件路径
                null); // 如果文件没有扩展名, 最好设置contentType参数.
        HttpUtil.getInstance().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

}
