package com.eshangke.framework.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * SQLite数据库管理类
 * 
 * 主要负责数据库资源的初始化,开启,关闭,以及获得DatabaseHelper帮助类操作
 * 
 * @author 史明松
 * 
 */
public class DBManager {
	private static DataBaseHelper dataBaseHelper = null;

	private static DBManager dBManager = null;
	private static DBConfig dBConfig;

	/**
	 * 构造函数
	 *
	 */
	private DBManager(DBConfig DBConfig) {
		super();
	}

	private synchronized static DBManager getInstance(DBConfig dBConfig) {
		if (null == dBManager) {
			dBManager = new DBManager(dBConfig);
		}
		DBManager.dataBaseHelper = new DataBaseHelper(dBConfig.getContext(), dBConfig.getDbName(), null, dBConfig.getDbVersion());
		return dBManager;
	}

	public static DBManager create(Context context) {
		DBConfig dBConfig = new DBConfig(context);
		return getInstance(dBConfig);
	}

	public static DBManager create(Context context, String dbName) {
		DBConfig dBConfig = new DBConfig(context);
		dBConfig.setDbName(dbName);
		return getInstance(dBConfig);
	}

	public static DBManager create(Context context, String dbName, int dbVersion) {
		DBConfig dBConfig = new DBConfig(context);
		dBConfig.setDbName(dbName);
		dBConfig.setDbVersion(dbVersion);
		return getInstance(dBConfig);
	}

	/**
	 * 关闭数据库 注意:当事务成功或者一次性操作完毕时候再关闭
	 */
	public void closeDatabase(SQLiteDatabase dataBase, Cursor cursor) {
		if (null != dataBase) {
			dataBase.close();
		}
		if (null != cursor) {
			cursor.close();
		}
	}

	/**
	 * 打开数据库 注:SQLiteDatabase资源一旦被关闭,该底层会重新产生一个新的SQLiteDatabase
	 */
	public SQLiteDatabase openReadableDatabase() {
		return getDatabaseHelper().getReadableDatabase();
	}

	/**
	 * 打开数据库 注:SQLiteDatabase资源一旦被关闭,该底层会重新产生一个新的SQLiteDatabase
	 */
	public SQLiteDatabase openWritableDatabase() {
		return getDatabaseHelper().getWritableDatabase();
	}

	/**
	 * 获取DataBaseHelper
	 * 
	 * @return
	 */
	public DataBaseHelper getDatabaseHelper() {
		if (dataBaseHelper == null) {
			dataBaseHelper = new DataBaseHelper(dBConfig.getContext(), dBConfig.getDbName(), null, dBConfig.getDbVersion());
		}
		return dataBaseHelper;
	}

	public DBConfig getDBConfig() {
		return dBConfig;
	}

	public static class DBConfig {
		private Context context;
		/** 默认数据库名称 **/
		private String dbName = "framework.db";
		/** 数据库版本 **/
		private int dbVersion = 1;

		public DBConfig(Context context) {
			this.context = context;
		}

		public Context getContext() {
			return context;
		}

		public String getDbName() {
			return dbName;
		}

		public void setDbName(String dbName) {
			if (!TextUtils.isEmpty(dbName)) {
				this.dbName = dbName;
			}
		}

		public int getDbVersion() {
			return dbVersion;
		}

		public void setDbVersion(int dbVersion) {
			this.dbVersion = dbVersion;
		}
	}
}
