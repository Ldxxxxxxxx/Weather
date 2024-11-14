package com.ldx.weather.db;

import android.content.Context;

import com.ldx.weather.db.bean.MyCity;
import com.ldx.weather.db.bean.Province;
import com.ldx.weather.db.dao.MyCityDao;
import com.ldx.weather.db.dao.ProvinceDao;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Room数据库类,用于管理应用程序中的本地数据库
 * 该数据库包含了两个实体类：Province 和 MyCity
 */
@Database(entities = {Province.class, MyCity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "NancyWeather";
    private static volatile AppDatabase mInstance;

    public abstract ProvinceDao provinceDao();

    public abstract MyCityDao myCityDao();

    /**
     * 单例模式
     */
    public static AppDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AppDatabase.class) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return mInstance;
    }

    /**
     * 版本升级迁移到2 新增我的城市表
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `myCity` " +
                    "(cityName TEXT NOT NULL, " +
                    "PRIMARY KEY(`cityName`))");
        }
    };
}
