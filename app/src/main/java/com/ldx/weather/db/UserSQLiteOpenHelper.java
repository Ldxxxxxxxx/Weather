package com.ldx.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "UserDatabase"; // 数据库名
    private static final int VERSION = 1; // 数据库版本

    public UserSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    // 该方法会自动调用，首先系统会检查该程序中是否存在数据库名为‘UserDatabase’的数据库
    // 如果存在则不会执行该方法，如果不存在则会执行该方法。
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table users(" +
                "UserId INTEGER primary key autoincrement," + // UserId自增主键
                "UserName varchar(50)," +
                "PassWord varchar(50)" +
                ")";
        db.execSQL(sql);
    }

    // 数据库版本更新时执行该方法，如果表已存在则先删除再调用onCreate重新创建
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists users");
        onCreate(db);
    }
}
