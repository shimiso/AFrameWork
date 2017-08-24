package com.eshangke.framework;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shims on 2016/1/20 0020.
 */
public class MainApplication extends MultiDexApplication {
    private static MainApplication mainApplication = null;
    public List<Activity> activityList = new LinkedList<Activity>();
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);// 初始化 xutils
        mainApplication = this;
        sAppContext = this;
        //JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        //JPushInterface.init(this);            // 初始化 JPush
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static MainApplication getInstance() {
        return mainApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    /**
     * 添加Activity到容器中.
     *
     * @param activity
     * @author 史明松
     * @update 2014年6月26日 上午10:55:40
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }


    /**
     * 遍历所有Activity并finish.
     *
     * @author 史明松
     * @update 2014年6月26日 上午10:55:28
     */
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }


    public String getAPKVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // can't
            return "";
        }

    }

    public String getAPKVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            return pi.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // can't
            return "";
        }
    }
}
