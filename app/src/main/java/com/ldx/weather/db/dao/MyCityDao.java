package com.ldx.weather.db.dao;

import com.ldx.weather.db.bean.MyCity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * 城市数据操作接口
 */
@Dao
public interface MyCityDao {

    @Query("SELECT * FROM MyCity")//查询所有城市
    Flowable<List<MyCity>> getAllCity();


    @Insert(onConflict = OnConflictStrategy.REPLACE)//添加城市
    Completable insertCity(MyCity myCity);


    @Delete
    Completable deleteCity(MyCity myCity);//删除城市


    @Query("DELETE  FROM MyCity where cityName=:cityName")//通过城市名称删除
    Completable deleteCity(String cityName);

}
