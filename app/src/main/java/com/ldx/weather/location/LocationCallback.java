package com.ldx.weather.location;

import com.baidu.location.BDLocation;

/**
 * 定位接口
 */
public interface LocationCallback {

    void onReceiveLocation(BDLocation bdLocation);//接收定位
}
