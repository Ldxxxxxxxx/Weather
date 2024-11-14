package com.ldx.weather.repository;

import android.annotation.SuppressLint;
import android.util.Log;

import com.ldx.weather.Constant;
import com.ldx.weather.db.bean.AirResponse;
import com.ldx.weather.db.bean.DailyResponse;
import com.ldx.weather.db.bean.HourlyResponse;
import com.ldx.weather.db.bean.LifestyleResponse;
import com.ldx.weather.db.bean.NowResponse;
import com.ldx.weather.service.ApiService;
import com.ldx.library.network.ApiType;
import com.ldx.library.network.NetworkApi;
import com.ldx.library.network.observer.BaseObserver;

import androidx.lifecycle.MutableLiveData;

//天气存储库，数据处理(实况天气、7天天气预报)

@SuppressLint("CheckResult")
public class WeatherRepository {

    private static final String TAG = WeatherRepository.class.getSimpleName();

    private static final class WeatherRepositoryHolder {
        private static final WeatherRepository mInstance = new WeatherRepository();
    }

    public static WeatherRepository getInstance() {
        return WeatherRepositoryHolder.mInstance;
    }


    public void nowWeather(MutableLiveData<NowResponse> responseLiveData,
                           MutableLiveData<String> failed, String cityId) {
        String type = "实时天气-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).nowWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(NowResponse nowResponse) {
                        if (nowResponse == null) {
                            failed.postValue("实况天气数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(nowResponse.getCode())) {
                            responseLiveData.postValue(nowResponse);
                        } else {
                            failed.postValue(type + nowResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }


    public void dailyWeather(MutableLiveData<DailyResponse> responseLiveData,
                           MutableLiveData<String> failed, String cityId) {
        String type = "天气预报-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).dailyWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(DailyResponse dailyResponse) {
                        if (dailyResponse == null) {
                            failed.postValue("天气预报数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(dailyResponse.getCode())) {
                            responseLiveData.postValue(dailyResponse);
                        } else {
                            failed.postValue(type + dailyResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }


    public void lifestyle(MutableLiveData<LifestyleResponse> responseLiveData,
                             MutableLiveData<String> failed, String cityId) {
        String type = "生活指数-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).lifestyle("1,2,3,4,5,6,7,8,9", cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(LifestyleResponse lifestyleResponse) {
                        if (lifestyleResponse == null) {
                            failed.postValue("生活指数数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(lifestyleResponse.getCode())) {
                            responseLiveData.postValue(lifestyleResponse);
                        } else {
                            failed.postValue(type + lifestyleResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }


    public void hourlyWeather(MutableLiveData<HourlyResponse> responseLiveData,
                          MutableLiveData<String> failed, String cityId) {
        String type = "逐小时天气预报-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).hourlyWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(HourlyResponse hourlyResponse) {
                        if (hourlyResponse == null) {
                            failed.postValue("逐小时天气预报数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(hourlyResponse.getCode())) {
                            responseLiveData.postValue(hourlyResponse);
                        } else {
                            failed.postValue(type + hourlyResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }


    public void airWeather(MutableLiveData<AirResponse> responseLiveData,
                              MutableLiveData<String> failed, String cityId) {
        String type = "空气质量天气预报-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).airWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(AirResponse airResponse) {
                        if (airResponse == null) {
                            failed.postValue("空气质量预报数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(airResponse.getCode())) {
                            responseLiveData.postValue(airResponse);
                        } else {
                            failed.postValue(type + airResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }
    /*
    实现对空气质量天气预报的请求，并通过 LiveData 返回结果或者失败信息。
    首先，通过 Retrofit 创建了对应的网络请求接口 ApiService，并使用 cityId 参数调用 airWeather 方法来获取空气质量天气预报数据。
    接着，通过 compose() 方法应用了 RxJava 的线程调度器，确保请求在 IO 线程执行，响应在主线程回调。
    然后，定义了一个 BaseObserver 匿名内部类来处理请求的成功和失败情况。
    在成功的情况下，检查返回的 airResponse 对象是否为空，如果为空则返回空数据的提示信息，否则根据返回的状态码判断请求是否成功，
    并通过 LiveData 将结果数据 post 给观察者。在失败的情况下，将失败信息通过 LiveData 返回给观察者，并打印出相应的日志信息。
     */
}
