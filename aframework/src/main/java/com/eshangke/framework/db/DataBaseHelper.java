package com.eshangke.framework.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite数据库的帮助类
 * 
 * 该类属于扩展类,主要承担数据库初始化和版本升级使用,其他核心全由核心父类完成
 * 
 * @author 史明松
 * 
 */
public class DataBaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "DataBaseHelper";

	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS [userinfo] (" +
				"[_id] INTEGER PRIMARY KEY AUTOINCREMENT," +
				"[userId] VARCHAR(20)UNIQUE ," +
				"[easyId] VARCHAR(20)," +
				"[mobile] VARCHAR(30)," +
				"[password] VARCHAR(20)," +
				"[isCurrent] INTEGER," +
				"[userName] VARCHAR(20)," +
				"[portrait] VARCHAR(30)," +
				"[logintime] VARCHAR(30)," +
				"[token] VARCHAR(30)," +
				"[wxUnionId] VARCHAR(30)," +
				"[qqOpenId] VARCHAR(30) );");

		db.execSQL("CREATE TABLE IF NOT EXISTS [BOOK] (" +
				"[_ID] integer primary key autoincrement," +
				"[TITLE] varchar,price varchar);");

		db.execSQL("CREATE TABLE IF NOT EXISTS [CHANNEL](" +
				"[_ID] INTEGER PRIMARY KEY AUTOINCREMENT," +
				"[CHANNEL_NAME] TEXT NOT NULL ," +
				"[CHANNEL_TYPE] TEXT NOT NULL ," +
				"[CHANNEL_SELECT] INTEGER NOT NULL ," +
				"[CHANNEL_INDEX] INTEGER NOT NULL ," +
				"[CHANNEL_FIXED] INTEGER);");

		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('推荐',0,0,1,0);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('热点',0,0,2,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('法制',0,0,3,1);");

		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('政治',0,1,1,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('经济',0,1,2,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('军事',0,1,3,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('娱乐',0,1,4,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('科技',0,1,5,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('体育',0,1,6,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('健康',0,1,7,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('国际',0,1,8,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('视频',0,1,9,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('养身',0,1,10,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('图片',0,1,11,1);");
		db.execSQL("INSERT INTO CHANNEL (CHANNEL_NAME,CHANNEL_TYPE,CHANNEL_SELECT,CHANNEL_INDEX,CHANNEL_FIXED) VALUES('社会',0,1,12,1);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	/*	if (oldVersion != newVersion) {
			db.execSQL("DELETE FROM " + TABLE_OBJECTIVE);

			//更新
			boolean b = checkColumnExist(db, TABLE_OBJECTIVE, "userId");
			if (!b) {
				db.execSQL("ALTER TABLE " + TABLE_OBJECTIVE + " ADD COLUMN userId VARCHAR;");
			}

		}*/
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	/**
	 * 检查某表列是否存在
	 * 
	 * @param db
	 * @param tableName 表名
	 * @param columnName  列名
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean checkColumnExist(SQLiteDatabase db, String tableName, String columnName) {
		boolean result = false;
		Cursor cursor = null;
		try {
			// 查询一行
			cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
			result = cursor != null && cursor.getColumnIndex(columnName) != -1;
		} catch (Exception e) {
			// LogUtil.e(TAG, "checkColumnExists1..." + e.getMessage());
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return result;
	}
}
