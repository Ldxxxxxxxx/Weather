package com.ldx.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//个人日程数据库帮助类
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "MySchedule"; // 数据库名
    private static final int VERSION = 2; // 数据库版本

    public MySQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    // 该方法会自动调用，首先系统会检查该程序中是否存在数据库名为‘MySchedule’的数据库
    // 如果存在则不会执行该方法，如果不存在则会执行该方法。

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table schedules(" +
                "id Integer primary key autoincrement," +
                "title varchar(50)," +    // 日程标题
                "place varchar(50)," +    // 地点
                "notition integer," +     // 是否需要通知，0表示不需要，1表示需要
                "priority varchar(20)," + // 优先级，可以是字符串形式如 "重要", "普通", "忽略"
                "description varchar(255)," + // 描述
                "time varchar(30)," +
                "UserName varchar(50)" +
                ")";
        db.execSQL(sql);
    }

  //当数据库版本号发生变化时，系统会调用这个方法来执行相应的升级操作。
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE schedules ADD COLUMN description varchar(255)");
        }
    }

}
