package com.ldx.weather;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.baidu.location.LocationClient;
import com.ldx.weather.db.AppDatabase;
import com.ldx.weather.service.MusicService;
import com.ldx.weather.utils.MVUtils;
import com.ldx.library.base.BaseApplication;
import com.ldx.library.network.NetworkApi;
import com.tencent.mmkv.MMKV;
    /**
     *该活动作为应用程序的自定义 Application 类，它的 onCreate() 方法会在应用程序启动时首先被调用，因此可以在这里进行一些初始化操作
     * 进行应用程序的初始化设置，包括网络框架、数据库、服务等的初始化，并提供了一些必要的全局信息和服务。
     **/
public class WeatherApp extends BaseApplication {

    //数据库
    private static AppDatabase db;
    private static MusicService musicService;
    @Override
    public void onCreate() {
        super.onCreate();
        //使用定位需要同意隐私合规政策
        LocationClient.setAgreePrivacy(true);
        //初始化网络框架
        NetworkApi.init(new NetworkRequiredInfo(this));
        //MMKV初始化
        MMKV.initialize(this);
        //工具类初始化
        MVUtils.getInstance();
        //初始化Room数据库
        db = AppDatabase.getInstance(this);
        bindService(new Intent(this,MusicService.class),serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public static AppDatabase getDb() {
        return db;
    }
    public static MusicService getService(){
        return musicService;
    }
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service != null) {
                musicService = ((MusicService.MusicBinder) service).getService();
            } else {
                musicService = null;
                // 处理绑定失败的情况
                Log.e("MusicService", "Failed to bind MusicService");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };
}
