package com.eshangke.framework.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.eshangke.framework.util.download.DownloadInfo;
import com.eshangke.framework.util.download.DownloadManager;
import com.eshangke.framework.util.download.DownloadState;
import com.eshangke.framework.util.download.DownloadViewHolder;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件下载下载管理类
 */
public class FileDownloadActivity extends BaseActivity implements View.OnClickListener {
    private ListView lv_downloading;
    private ListView lv_downloaded;
    Button btn_loading;
    Button btn_loaded;
    Button btn_operate_some;
    Button btn_delete;
    Button btn_select_all;

    private DownloadManager downloadManager;
    private DownloadListAdapter downloadedListAdapter;
    private DownloadListAdapter downloadingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_download);

        initView();
        initData();
    }

    private void initData() {
        downloadingListAdapter = new DownloadListAdapter();
        lv_downloading.setAdapter(downloadingListAdapter);
        downloadManager = DownloadManager.getInstance();
        downloadedListAdapter = new DownloadListAdapter();
        lv_downloaded.setAdapter(downloadedListAdapter);

        refreshData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        btn_loaded = (Button) findViewById(R.id.btn_loaded);
        btn_loading = (Button) findViewById(R.id.btn_loading);
        btn_operate_some = (Button) findViewById(R.id.btn_operate_some);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_select_all = (Button) findViewById(R.id.btn_select_all);
        lv_downloading = (ListView) findViewById(R.id.lv_downloading);
        lv_downloaded = (ListView) findViewById(R.id.lv_downloaded);
        lv_downloading.setVisibility(View.VISIBLE);
        lv_downloaded.setVisibility(View.GONE);

        btn_loaded.setOnClickListener(this);
        btn_loading.setOnClickListener(this);
        btn_operate_some.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_select_all.setOnClickListener(this);
    }

    /**
     * 刷新当前任务状态
     */
    private void refreshData() {
        if (downloadManager != null) {
            List loadingData = downloadManager.getLoadingList();
            List loadedData = downloadManager.getLoadedList();
            downloadingListAdapter.setData(loadingData);
            downloadedListAdapter.setData(loadedData);

            downloadingListAdapter.notifyDataSetChanged();
            downloadedListAdapter.notifyDataSetChanged();
        }
    }

    private class DownloadListAdapter extends BaseAdapter {

        private Context mContext;
        private final LayoutInflater mInflater;
        private List data = new ArrayList();
        /**
         * 是否显示选择操作对话框
         */
        private boolean isShowOperaterCheckBox;
        /**
         * 是否全选
         */
        private boolean isSelectedAll;
        private ArrayList<DownloadInfo> selectedInfoList = new ArrayList<DownloadInfo>();

        public boolean isShowOperaterCheckBox() {
            return isShowOperaterCheckBox;
        }

        public void setShowOperaterCheckBox(boolean showOperaterCheckBox) {
            isShowOperaterCheckBox = showOperaterCheckBox;
        }

        private DownloadListAdapter() {
            mContext = getBaseContext();
            mInflater = LayoutInflater.from(mContext);
        }

        public List getData() {
            return data;
        }

        public void setData(List data) {
            this.data = data;
        }

        public ArrayList<DownloadInfo> getSelectedInfoList() {
            return selectedInfoList;
        }

        public void clearSelectedInfoList() {
            this.selectedInfoList.clear();
            isSelectedAll = false;
        }

        public void selectAllData() {
            isSelectedAll = true;
            this.selectedInfoList.clear();
            selectedInfoList.addAll(data);
        }

        @Override
        public int getCount() {
            if (downloadManager == null || data == null) {
                return 0;
            }
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return downloadManager.getDownloadInfo(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            DownloadItemViewHolder holder = null;
            DownloadInfo downloadInfo = (DownloadInfo) data.get(i);
            if (view == null) {
                view = mInflater.inflate(R.layout.download_item, null);
                holder = new DownloadItemViewHolder(view, downloadInfo);
                view.setTag(holder);
                holder.refresh();
            } else {
                holder = (DownloadItemViewHolder) view.getTag();
                holder.update(downloadInfo);
            }

            if (downloadInfo.getState().value() < DownloadState.FINISHED.value()) {
                try {
                    downloadManager.startDownload(
                            downloadInfo.getUrl(),
                            downloadInfo.getLabel(),
                            downloadInfo.getFileSavePath(),
                            downloadInfo.isAutoResume(),
                            downloadInfo.isAutoRename(),
                            holder);
                } catch (DbException ex) {
                    Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
                }
            }

            if (isShowOperaterCheckBox) {
                holder.cb_operate.setVisibility(View.VISIBLE);
            } else {
                holder.cb_operate.setVisibility(View.GONE);
            }

            if (isSelectedAll) {
                holder.cb_operate.setChecked(isSelectedAll);
            }

            holder.cb_operate.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedInfoList.add((DownloadInfo) data.get(i));
                } else {
                    selectedInfoList.remove(data.get(i));
                }
            });


            return view;
        }
    }

    public class DownloadItemViewHolder extends DownloadViewHolder {
        @ViewInject(R.id.download_label)
        TextView label;
        @ViewInject(R.id.download_state)
        TextView state;
        @ViewInject(R.id.download_pb)
        ProgressBar progressBar;
        @ViewInject(R.id.download_stop_btn)
        Button stopBtn;

        CheckBox cb_operate;

        public DownloadItemViewHolder(View view, DownloadInfo downloadInfo) {
            super(view, downloadInfo);
            cb_operate = (CheckBox) view.findViewById(R.id.cb_operate);
            refresh();
        }

        @Event(R.id.download_stop_btn)
        private void toggleEvent(View view) {
            DownloadState state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                case STARTED:
                    downloadManager.stopDownload(downloadInfo);
                    break;
                case ERROR:
                case STOPPED:
                    try {
                        downloadManager.startDownload(
                                downloadInfo.getUrl(),
                                downloadInfo.getLabel(),
                                downloadInfo.getFileSavePath(),
                                downloadInfo.isAutoResume(),
                                downloadInfo.isAutoRename(),
                                this);
                    } catch (DbException ex) {
                        Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
                    }
                    break;
                case FINISHED:
                    Toast.makeText(x.app(), "已经下载完成", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

        @Event(R.id.download_remove_btn)
        private void removeEvent(View view) {
            try {
                downloadManager.removeDownload(downloadInfo);
                refreshData();
            } catch (DbException e) {
                Toast.makeText(x.app(), "移除任务失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void update(DownloadInfo downloadInfo) {
            super.update(downloadInfo);
            refresh();
        }

        @Override
        public void onWaiting() {
            refresh();
        }

        @Override
        public void onStarted() {
            refresh();
        }

        @Override
        public void onLoading(long total, long current) {
            refresh();
        }

        @Override
        public void onSuccess(File result) {
            refresh();
            refreshData();
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            refresh();
        }

        @Override
        public void onCancelled(Callback.CancelledException cex) {
            refresh();
        }

        public void refresh() {
            label.setText(downloadInfo.getLabel());
            state.setText(downloadInfo.getState().toString());
            progressBar.setProgress(downloadInfo.getProgress());

            stopBtn.setVisibility(View.VISIBLE);
            stopBtn.setText(x.app().getString(R.string.stop));
            DownloadState state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                case STARTED:
                    stopBtn.setText(x.app().getString(R.string.stop));
                    break;
                case ERROR:
                case STOPPED:
                    stopBtn.setText(x.app().getString(R.string.start));
                    break;
                case FINISHED:
                    stopBtn.setVisibility(View.INVISIBLE);
                    break;
                default:
                    stopBtn.setText(x.app().getString(R.string.start));
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loaded:// 已下载
                lv_downloading.setVisibility(View.GONE);
                lv_downloaded.setVisibility(View.VISIBLE);

                List loadedList = downloadManager.getLoadedList();
                //Toast.makeText(FileDownloadActivity.this,loadedList.toString(),Toast.LENGTH_LONG).show();
                downloadedListAdapter.setData(loadedList);
                downloadedListAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_loading:// 下载中
                lv_downloading.setVisibility(View.VISIBLE);
                lv_downloaded.setVisibility(View.GONE);
                List loadingList = downloadManager.getLoadingList();
                downloadingListAdapter.setData(loadingList);
                downloadingListAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_select_all:// 全选
                downloadingListAdapter.selectAllData();
                downloadingListAdapter.notifyDataSetChanged();
                downloadedListAdapter.selectAllData();
                downloadedListAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_delete:// 批量删除
                if (downloadManager != null) {

                    if (lv_downloading.getVisibility() == View.VISIBLE) {
                        if (downloadingListAdapter.isShowOperaterCheckBox) {
                            try {
                                downloadManager.removeBatchDownload(downloadingListAdapter.getSelectedInfoList());
                                refreshData();
                            } catch (DbException dbExeption) {

                                dbExeption.printStackTrace();
                            }
                        }

                    }

                    if (lv_downloaded.getVisibility() == View.VISIBLE) {
                        if (downloadedListAdapter.isShowOperaterCheckBox) {
                            try {
                                downloadManager.removeBatchDownload(downloadedListAdapter.getSelectedInfoList());
                                refreshData();
                            } catch (DbException dbExeption) {

                                dbExeption.printStackTrace();
                            }
                        }

                    }
                }
                break;

            case R.id.btn_operate_some:// 批量操作
                if (btn_operate_some.getText().equals("批量操作")) {
                    btn_operate_some.setText("取消操作");
                    downloadingListAdapter.clearSelectedInfoList();
                    downloadedListAdapter.clearSelectedInfoList();
                    btn_delete.setVisibility(View.VISIBLE);
                    btn_select_all.setVisibility(View.VISIBLE);
                    downloadingListAdapter.setShowOperaterCheckBox(true);
                    downloadingListAdapter.notifyDataSetChanged();
                    downloadedListAdapter.setShowOperaterCheckBox(true);
                    downloadedListAdapter.notifyDataSetChanged();
                } else {
                    btn_operate_some.setText("批量操作");
                    btn_delete.setVisibility(View.GONE);
                    btn_select_all.setVisibility(View.GONE);
                    downloadingListAdapter.setShowOperaterCheckBox(false);
                    downloadingListAdapter.notifyDataSetChanged();
                    downloadedListAdapter.setShowOperaterCheckBox(false);
                    downloadedListAdapter.notifyDataSetChanged();
                }

                break;
        }
    }
}
