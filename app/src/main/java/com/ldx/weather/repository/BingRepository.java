package com.ldx.weather.repository;

import android.annotation.SuppressLint;
import android.util.Log;

import com.ldx.weather.db.bean.BingResponse;
import com.ldx.weather.service.ApiService;
import com.ldx.library.network.ApiType;
import com.ldx.library.network.NetworkApi;
import com.ldx.library.network.observer.BaseObserver;

import androidx.lifecycle.MutableLiveData;


 //必应壁纸存储库，用于获取壁纸数据

@SuppressLint("CheckResult")
public class BingRepository {
    private static final String TAG = WeatherRepository.class.getSimpleName();

    private static final class BingRepositoryHolder {
        private static final BingRepository mInstance = new BingRepository();
    }

    public static BingRepository getInstance() {
        return BingRepositoryHolder.mInstance;
    }


    public void bing(MutableLiveData<BingResponse> responseLiveData, MutableLiveData<String> failed) {
        //responseLiveData 成功数据，failed 误信息
        String type = "必应壁纸-->";
        NetworkApi.createService(ApiService.class, ApiType.BING).bing()
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(BingResponse bingResponse) {
                        if (bingResponse == null) {
                            failed.postValue("必应壁纸数据为null。");
                            return;
                        }
                        responseLiveData.postValue(bingResponse);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }
}
