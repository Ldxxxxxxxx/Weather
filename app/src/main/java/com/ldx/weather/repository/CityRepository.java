package com.ldx.weather.repository;

import android.util.Log;

import com.ldx.weather.WeatherApp;
import com.ldx.weather.db.bean.MyCity;
import com.ldx.weather.db.bean.Province;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Completable;
import io.reactivex.Flowable;

//用于管理城市数据的存储库，主要负责与城市数据库进行交互，并提供所需的城市数据。

public class CityRepository {

    private static final String TAG = CityRepository.class.getSimpleName();

    private static final class CityRepositoryHolder {
        private static final CityRepository mInstance = new CityRepository();
    }

    public static CityRepository getInstance() {
        return CityRepository.CityRepositoryHolder.mInstance;
    }

    /**
     * 添加城市数据
     */
    public void addCityData(List<Province> cityList) {
        Province[] provinceArray = cityList.toArray(new Province[0]);
        Completable insertAll = WeatherApp.getDb().provinceDao().insertAll(provinceArray);
        CustomDisposable.addDisposable(insertAll, () -> Log.d(TAG, "addCityData: 插入数据成功。"));
    }

    /**
     * 获取城市数据
     */
    public void getCityData(MutableLiveData<List<Province>> listMutableLiveData) {
        Flowable<List<Province>> listFlowable = WeatherApp.getDb().provinceDao().getAll();
        CustomDisposable.addDisposable(listFlowable, listMutableLiveData::postValue);
    }

    /**
     * 获取我的城市所有数据
     */
    public void getMyCityData(MutableLiveData<List<MyCity>> listMutableLiveData) {
        CustomDisposable.addDisposable(WeatherApp.getDb().myCityDao().getAllCity(), listMutableLiveData::postValue);
    }

    /**
     * 添加我的城市数据
     */
    public void addMyCityData(MyCity myCity) {
        CustomDisposable.addDisposable(WeatherApp.getDb().myCityDao().insertCity(myCity), () -> Log.d(TAG, "addMyCityData: 插入数据成功。"));
    }

    /**
     * 删除我的城市数据
     */
    public void deleteMyCityData(String cityName) {
        CustomDisposable.addDisposable(WeatherApp.getDb().myCityDao().deleteCity(cityName), () -> Log.d(TAG, "deleteMyCityData: 删除数据成功"));
    }

    /**
     * 删除我的城市数据
     */
    public void deleteMyCityData(MyCity myCity) {
        CustomDisposable.addDisposable(WeatherApp.getDb().myCityDao().deleteCity(myCity), () -> Log.d(TAG, "deleteMyCityData: 删除数据成功"));
    }
}
