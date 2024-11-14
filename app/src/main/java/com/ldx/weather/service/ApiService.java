package com.ldx.weather.service;
import com.ldx.weather.db.bean.AirResponse;
import com.ldx.weather.db.bean.BingResponse;
import com.ldx.weather.db.bean.DailyResponse;
import com.ldx.weather.db.bean.HourlyResponse;
import com.ldx.weather.db.bean.LifestyleResponse;
import com.ldx.weather.db.bean.NowResponse;
import com.ldx.weather.db.bean.SearchCityResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import static com.ldx.weather.Constant.API_KEY;

//API服务接口
public interface ApiService {

    @GET("/v2/city/lookup?key=" + API_KEY + "&range=cn")//请求中国内的location城市的数据,模糊搜索，返回10条
    Observable<SearchCityResponse> searchCity(@Query("location") String location);

    @GET("/v7/weather/now?key=" + API_KEY)//请返回实况天气数据
    Observable<NowResponse> nowWeather(@Query("location") String location);


    @GET("/v7/weather/7d?key=" + API_KEY)//请求7天数据，免费订阅号最多申请7天
    Observable<DailyResponse> dailyWeather(@Query("location") String location);


    @GET("/v7/indices/1d?key=" + API_KEY)//请求返回生活指数数据
    Observable<LifestyleResponse> lifestyle(@Query("type") String type, @Query("location") String location);


    @GET("/HPImageArchive.aspx?format=js&idx=0&n=1")//必应的 API 来获取每日更新的图片数据
    Observable<BingResponse> bing();

    @GET("/v7/weather/24h?key=" + API_KEY)//未来24小时逐小时天气预报数据
    Observable<HourlyResponse> hourlyWeather(@Query("location") String location);


    @GET("/v7/air/now?key=" + API_KEY)//当天空气质量
    Observable<AirResponse> airWeather(@Query("location") String location);



}
