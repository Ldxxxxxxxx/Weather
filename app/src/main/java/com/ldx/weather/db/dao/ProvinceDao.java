package com.ldx.weather.db.dao;

import com.ldx.weather.db.bean.Province;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * 行政区数据操作接口
 */
@Dao
public interface ProvinceDao {


    @Query("SELECT * FROM Province")//查询所有省份
    Flowable<List<Province>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)//查询所有行政区数据
    Completable insertAll(Province... provinces);
}
