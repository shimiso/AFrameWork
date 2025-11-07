package com.eshangke.framework.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.eshangke.framework.MainApplication;
import com.eshangke.framework.R;
import com.eshangke.framework.widget.ConfirmDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Android的工具类
 *
 * Created by shims on 2016/1/25 0025.
 */
public class AndroidUtil {
    public final static int TIPS_ERROR = 0;
    public final static int TIPS_SUCCESS = 1;
    public Context context;
    public SharePreferenceUtil spUtil;
    public MainApplication mainApplication;
    public static AndroidUtil androidUtils;

    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static final String KEY_APP_KEY = "JPUSH_APPKEY";

    private AndroidUtil(Context context, SharePreferenceUtil spUtil, MainApplication mainApplication) {
        this.context = context;
        this.spUtil = spUtil;
        this.mainApplication = mainApplication;
    }
    private AndroidUtil(Context context) {
        this.context = context;
    }

    public static AndroidUtil init(Context context, SharePreferenceUtil spUtil, MainApplication mainApplication) {
        androidUtils = new AndroidUtil(context, spUtil, mainApplication);
        return androidUtils;
    }

    public static AndroidUtil init(Context context) {
        androidUtils = new AndroidUtil(context);
        return androidUtils;
    }
    private static long mLastClickTime = 0;
    private static final int SPACE_TIME = 500;

    public static boolean isFastDoubleClick() {
        long time = SystemClock.elapsedRealtime();
        if (time - mLastClickTime <= SPACE_TIME) {
            return true;
        } else {
            mLastClickTime = time;
            return false;
        }
    }
    /**
     * 检测并设置可用的存储路径
     *
     * @return
     */
    public boolean checkStoragePathAndSetBaseApp() {
        String storagePath = null;
        List<Long> memorySize = new ArrayList<Long>();
        Map<Long, String> storageMap = new HashMap<Long, String>();

        // 如果可以检测到SD卡返回SD卡路径，否则就反射得到最大可以用的机身存储
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) == true) {
            storagePath = android.os.Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            StorageManager sm = (StorageManager) context
                    .getSystemService(Context.STORAGE_SERVICE);
            String[] paths;
            try {
                paths = (String[]) sm.getClass()
                        .getMethod("getVolumePaths", (Class) null).invoke(sm, (Object) null);
                for (String path : paths) {
                    StatFs stat = new StatFs(path);
                    long blockSize = stat.getBlockSize();
                    long availableBlocks = stat.getAvailableBlocks();
                    long storageSize = availableBlocks * blockSize;
                    if (storageSize > 0) {
                        memorySize.add(storageSize);
                        storageMap.put(storageSize, path);
                    }
                }
                if (memorySize != null)
                    storagePath = storageMap.get(Collections.max(memorySize));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (storagePath == null) {
            storagePath = context.getFilesDir().getAbsolutePath();

        }

        if (storagePath != null) {
            spUtil.setStoragePath(storagePath);
            return true;
        } else {
            ConfirmDialog dialog = ConfirmDialog.createDialog(context);
            dialog.setDialogTitle("提示");
            dialog.setCancelable(false);
            dialog.setDialogMessage("请检查有无可用存储卡");
            dialog.setOnButton1ClickListener("取消", null,
                    new ConfirmDialog.OnButton1ClickListener() {
                        @Override
                        public void onClick(View view, DialogInterface dialog) {
                            dialog.cancel();
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            context.startActivity(intent);
                        }
                    });
            dialog.setOnButton2ClickListener("退出", null,
                    new ConfirmDialog.OnButton2ClickListener() {
                        @Override
                        public void onClick(View view, DialogInterface dialog) {
                            dialog.cancel();
                            mainApplication.exit();
                        }
                    });
            dialog.show();
            return false;
        }
    }

    /**
     * 检测是否存在网络
     *
     * @return
     */
    public boolean hasInternetConnected() {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo network = manager.getActiveNetworkInfo();
            if (network != null && network.isConnectedOrConnecting()) {
                return true;
            }
        }
        Toast.makeText(context, R.string.check_connection, Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 验证网络 如果没有网直接打开网络设置页面
     *
     * @return
     */
    public boolean validateInternet() {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            openWirelessSet();
            return false;
        } else {
            NetworkInfo[] info = manager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        openWirelessSet();
        return false;
    }

    /**
     * 打开网络设置
     */
    public void openWirelessSet() {
        ConfirmDialog dialog = ConfirmDialog.createDialog(context);
        dialog.setDialogTitle(R.string.prompt);
        dialog.setDialogMessage(R.string.check_connection);
        dialog.setCancelable(false);
        dialog.setOnButton1ClickListener(R.string.settings, null,
                new ConfirmDialog.OnButton1ClickListener() {
                    @Override
                    public void onClick(View view, DialogInterface dialog) {
                        dialog.cancel();
                        Intent intent = null;
                        try {
                            intent = new Intent(Settings.ACTION_SETTINGS);
                            context.startActivity(intent);
                        } catch (Exception e) {
                        }

                    }
                });
        dialog.setOnButton2ClickListener(R.string.exit, null,
                new ConfirmDialog.OnButton2ClickListener() {
                    @Override
                    public void onClick(View view, DialogInterface dialog) {
                        dialog.cancel();
                        mainApplication.exit();
                    }
                });
        dialog.show();
    }

    /**
     * 功能: 获取应用的版本VersionName
     *
     * @return
     */
    public String getApkVersionName() {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 功能: 获取应用的版本Code
     *
     * @return
     */
    public int getApkVersionCode() {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {

        }
        return 0;
    }

    /**
     * 弹出Snackbar提示
     *
     * @param v
     * @param text
     * @param tipModel TIPS_ERROR-错误 TIPS_SUCCESS-成功
     */
    public void showTips(View v, String text, Integer tipModel) {
        Snackbar snackbar = Snackbar.make(v, text, Snackbar.LENGTH_SHORT);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout ve = (Snackbar.SnackbarLayout) snackbar.getView();
        switch (tipModel) {
            case TIPS_ERROR:
                ve.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                break;
            case TIPS_SUCCESS:
                ve.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                break;

        }

        ve.setAlpha(0.5f);
        snackbar.show();
    }

    /**
     * dp转Px
     * @param dp
     * @return
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * 获取屏幕的高度
     * @param c
     * @return
     */
    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    /**
     * 获取屏幕宽度
     * @param c
     * @return
     */
    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    /**
     * 是否是android5.0版本
     * @return
     */
    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    /**
     * 取得AppKey
     * @return
     */
    public String getAppKey() {
        Bundle metaData = null;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai)
                metaData = ai.metaData;
            if (null != metaData) {
                appKey = metaData.getString(KEY_APP_KEY);
                if ((null == appKey) || appKey.length() != 24) {
                    appKey = null;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return appKey;
    }


    /**
     * 验证SD卡是够可用
     * @return
     */
    public boolean checkSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
}
