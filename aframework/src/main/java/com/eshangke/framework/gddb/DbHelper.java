package com.eshangke.framework.gddb;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.eshangke.framework.gen.DaoMaster;
import com.eshangke.framework.gen.DaoSession;
import com.eshangke.framework.gen.HobbyDao;
import com.eshangke.framework.gen.UserDao;


/**
 * Created by huochangsheng on 2018/6/20.
 */
public class DbHelper {
    private final DaoMaster daoMaster;
    private final DaoSession daoSession;

    //定义单例模式
    private static volatile DbHelper instance;

    public DbHelper(Context context) {

        //初始化数据库的一些配置        第一个参数上下文， 二 ：数据库名
        DaoMaster.DevOpenHelper user = new DaoMaster.DevOpenHelper(context, "user", null);
//        //获取数据库操作对象
        SQLiteDatabase db = user.getWritableDatabase();
//        //获取DaoMaster对象
        daoMaster = new DaoMaster(db);
//       //获取DaoSession对象
        daoSession = daoMaster.newSession();
    }
    public static DbHelper getInstance(Context context){
        if (null==instance){
            synchronized (DbHelper.class){
                if (instance==null){
                    instance=new DbHelper(context);
                }
            }
        }
        return instance;
    }
    //对外定义方法
    public UserDao getUserDao(){
        return daoSession.getUserDao();
    }

    public HobbyDao getHobbyDao(){
        return daoSession.getHobbyDao();
    }

}
