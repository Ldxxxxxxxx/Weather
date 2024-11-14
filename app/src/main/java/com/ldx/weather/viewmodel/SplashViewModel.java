package com.ldx.weather.viewmodel;


import androidx.lifecycle.MutableLiveData;

import com.ldx.weather.db.bean.BingResponse;
import com.ldx.weather.db.bean.Province;
import com.ldx.weather.repository.BingRepository;
import com.ldx.weather.repository.CityRepository;
import com.ldx.library.base.BaseViewModel;

import java.util.List;

/**
 * 启动页ViewModel
 * {@link com.ldx.weather.ui.SplashActivity}
 */
public class SplashViewModel extends BaseViewModel {

    public MutableLiveData<List<Province>> listMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<BingResponse> bingResponseMutableLiveData = new MutableLiveData<>();

    /**
     * 添加城市数据
     */
    public void addCityData(List<Province> provinceList) {
        CityRepository.getInstance().addCityData(provinceList);
    }

    /**
     * 获取所有城市数据
     */
    public void getAllCityData() {
        CityRepository.getInstance().getCityData(listMutableLiveData);
    }

    /**
     * 必应壁纸
     */
    public void bing() {
        BingRepository.getInstance().bing(bingResponseMutableLiveData, failed);
    }
}
