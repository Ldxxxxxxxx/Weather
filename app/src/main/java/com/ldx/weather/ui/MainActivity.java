package com.ldx.weather.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import com.baidu.location.BDLocation;
import com.ldx.weather.Constant;
import com.ldx.weather.R;
import com.ldx.weather.databinding.ActivityMainBinding;
import com.ldx.weather.databinding.DialogDailyDetailBinding;
import com.ldx.weather.databinding.DialogHourlyDetailBinding;
import com.ldx.weather.db.bean.AirResponse;
import com.ldx.weather.db.bean.DailyResponse;
import com.ldx.weather.db.bean.HourlyResponse;
import com.ldx.weather.db.bean.LifestyleResponse;
import com.ldx.weather.db.bean.NowResponse;
import com.ldx.weather.db.bean.SearchCityResponse;
import com.ldx.weather.location.GoodLocation;
import com.ldx.weather.location.LocationCallback;
import com.ldx.weather.manage.DailyManager;
import com.ldx.weather.manage.ManagerActivity;
import com.ldx.weather.ui.adapter.DailyAdapter;
import com.ldx.weather.ui.adapter.HourlyAdapter;
import com.ldx.weather.ui.adapter.LifestyleAdapter;
import com.ldx.weather.utils.CityDialog;
import com.ldx.weather.utils.EasyDate;
import com.ldx.weather.utils.GlideUtils;
import com.ldx.weather.utils.MVUtils;
import com.ldx.weather.utils.WeatherUtil;
import com.ldx.weather.viewmodel.MainViewModel;
import com.ldx.library.base.NetworkActivity;
import java.util.ArrayList;
import java.util.List;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends NetworkActivity<ActivityMainBinding> implements LocationCallback, CityDialog.SelectedCityCallback {

    //权限数组
    private final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求权限意图
    private ActivityResultLauncher<String[]> requestPermissionIntent;
    //跳转页面Intent
    private ActivityResultLauncher<Intent> jumpActivityIntent;

    private MainViewModel viewModel;

    //天气预报数据和适配器
    private final List<DailyResponse.DailyBean> dailyBeanList = new ArrayList<>();
    private final DailyAdapter dailyAdapter = new DailyAdapter(dailyBeanList);
    //生活指数数据和适配器
    private final List<LifestyleResponse.DailyBean> lifestyleList = new ArrayList<>();
    private final LifestyleAdapter lifestyleAdapter = new LifestyleAdapter(lifestyleList);
    //逐小时天气预报数据和适配器
    private final List<HourlyResponse.HourlyBean> hourlyBeanList = new ArrayList<>();
    private final HourlyAdapter hourlyAdapter = new HourlyAdapter(hourlyBeanList);
    //城市弹窗
    private CityDialog cityDialog;
    //定位
    private GoodLocation goodLocation;
    //菜单
    private Menu mMenu;
    //城市信息来源标识  0: 定位， 1: 切换城市
    private int cityFlag = 0;
    //城市名称，定位和切换城市都会重新赋值。
    private String mCityName;
    //是否正在刷新
    private boolean isRefresh;
    //用户是否保持登录
    private boolean isChecked;
    /**
     * 注册意图
     */
    @Override
    public void onRegister() {
        //请求权限意图
        requestPermissionIntent = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineLocation = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
            boolean writeStorage = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
            if (fineLocation && writeStorage) {
                startLocation();//权限已经获取到，开始定位
            }
        });
        //城市管理页面返回数据
        jumpActivityIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                //获取上个页面返回的数据
                String city = result.getData().getStringExtra(Constant.CITY_RESULT);
                //检查返回的城市 , 如果返回的城市是当前定位城市，并且当前定位标志为0，则不需要请求
                if (city.equals(MVUtils.getString(Constant.LOCATION_CITY)) && cityFlag == 0) {
                    Log.d("TAG", "onRegister: 管理城市页面返回不需要进行天气查询");
                    return;
                }
                //反之就直接调用选中城市的方法进行城市天气搜索
                Log.d("TAG", "onRegister: 管理城市页面返回进行天气查询");
                selectedCity(city);
            }
        });
    }

    /**
     * 初始化
     */
    @Override
    protected void onCreate() {
        //沉浸式
        setFullScreenImmersion();
        //初始化定位
        initLocation();
        //请求权限
        requestPermission();
        //初始化视图
        initView();
        //绑定ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        //获取城市数据
        viewModel.getAllCity();
        Intent intent = getIntent();
        isChecked = intent.getBooleanExtra("isChecked", false); // 获取布尔类型的值，如果没有传递或者出错，使用默认值 false
    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新壁纸
        updateBgImage(MVUtils.getBoolean(Constant.USED_BING), MVUtils.getString(Constant.BING_URL));
    }

    /**
     * 初始化页面视图
     */
    private void initView() {
        //自定义Toolbar图标
        setToolbarMoreIconCustom(binding.materialToolbar);
        //天气预报列表
        binding.rvDaily.setLayoutManager(new LinearLayoutManager(this));
        dailyAdapter.setOnClickItemCallback(position -> showDailyDetailDialog(dailyBeanList.get(position)));
        binding.rvDaily.setAdapter(dailyAdapter);
        //生活指数列表
        binding.rvLifestyle.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLifestyle.setAdapter(lifestyleAdapter);
        //逐小时天气预报列表
        LinearLayoutManager hourlyLayoutManager = new LinearLayoutManager(this);
        hourlyLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvHourly.setLayoutManager(hourlyLayoutManager);
        hourlyAdapter.setOnClickItemCallback(position -> showHourlyDetailDialog(hourlyBeanList.get(position)));
        binding.rvHourly.setAdapter(hourlyAdapter);
        //下拉刷新监听
        binding.layRefresh.setOnRefreshListener(() -> {
            if (mCityName == null) {
                binding.layRefresh.setRefreshing(false);
                return;
            }
            //设置正在刷新
            isRefresh = true;
            //搜索城市
            viewModel.searchCity(mCityName);
        });
        //滑动监听
        binding.layScroll.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                if (scrollY > binding.layScrollHeight.getMeasuredHeight()) {
                    binding.tvTitle.setText((mCityName == null ? "城市天气" : mCityName));
                }
            } else if (scrollY < oldScrollY) {
                if (scrollY < binding.layScrollHeight.getMeasuredHeight()) {
                    binding.tvTitle.setText("城市天气");
                }
            }
        });
    }

    /**
     * 显示天气预报详情弹窗
     */
    private void showDailyDetailDialog(DailyResponse.DailyBean dailyBean) {
            // 创建Dialog对象
            Dialog dialog = new Dialog(MainActivity.this);

            // 设置内容视图
            DialogDailyDetailBinding detailBinding = DialogDailyDetailBinding.inflate(LayoutInflater.from(MainActivity.this), null, false);
            dialog.setContentView(detailBinding.getRoot());


            // 关闭弹窗
            detailBinding.ivClose.setOnClickListener(v -> dialog.dismiss());

            // 设置数据显示
            detailBinding.toolbarDaily.setTitle(String.format("%s   %s", dailyBean.getFxDate(), EasyDate.getWeek(dailyBean.getFxDate())));
            detailBinding.toolbarDaily.setSubtitle("详细天气");
            WeatherUtil.changeIcon(detailBinding.icon, Integer.parseInt(dailyBean.getIconDay()));
            detailBinding.toolbarDaily.setSubtitleTextColor(getResources().getColor(android.R.color.black));
            detailBinding.tvTmpMax.setText(String.format("%s℃", dailyBean.getTempMax()));
            detailBinding.tvTmpMin.setText(String.format("%s℃", dailyBean.getTempMin()));
            detailBinding.tvUvIndex.setText(dailyBean.getUvIndex());
            detailBinding.tvCondTxtD.setText(dailyBean.getTextDay());
            detailBinding.tvCondTxtN.setText(dailyBean.getTextNight());
            detailBinding.tvWindDeg.setText(String.format("%s°", dailyBean.getWind360Day()));
            detailBinding.tvWindDir.setText(dailyBean.getWindDirDay());
            detailBinding.tvWindSc.setText(String.format("%s级", dailyBean.getWindScaleDay()));
            detailBinding.tvWindSpd.setText(String.format("%s公里/小时", dailyBean.getWindSpeedDay()));
            detailBinding.tvCloud.setText(String.format("%s%%", dailyBean.getCloud()));
            detailBinding.tvHum.setText(String.format("%s%%", dailyBean.getHumidity()));
            detailBinding.tvPres.setText(String.format("%shPa", dailyBean.getPressure()));
            detailBinding.tvPcpn.setText(String.format("%smm", dailyBean.getPrecip()));
            detailBinding.tvVis.setText(String.format("%skm", dailyBean.getVis()));

            // 显示Dialog
            dialog.show();
        }




    /**
     * 显示逐小时预报详情弹窗
     */
    private void showHourlyDetailDialog(HourlyResponse.HourlyBean hourlyBean) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
        DialogHourlyDetailBinding detailBinding = DialogHourlyDetailBinding.inflate(LayoutInflater.from(MainActivity.this), null, false);
        dialog.setContentView(detailBinding.getRoot()); // 使用您设置的布局
          // 关闭弹窗
        detailBinding.ivClose.setOnClickListener(v -> dialog.dismiss());
        // 设置标题和副标题
        String time = EasyDate.updateTime(hourlyBean.getFxTime());
        detailBinding.toolbarHourly.setTitle(EasyDate.showTimeInfo(time) + time);
        detailBinding.toolbarHourly.setSubtitle("逐小时预报");
        WeatherUtil.changeIcon(detailBinding.hourIcon, Integer.parseInt(hourlyBean.getIcon()));
        detailBinding.toolbarHourly.setTitleTextColor(getResources().getColor(android.R.color.black));
        detailBinding.toolbarHourly.setSubtitleTextColor(getResources().getColor(android.R.color.black));
        detailBinding.tvTmp.setText(String.format("%s℃", hourlyBean.getTemp()));
        detailBinding.tvCondTxt.setText(hourlyBean.getText());
        detailBinding.tvWindDeg.setText(String.format("%s°", hourlyBean.getWind360()));
        detailBinding.tvWindDir.setText(hourlyBean.getWindDir());
        detailBinding.tvWindSc.setText(String.format("%s级", hourlyBean.getWindScale()));
        detailBinding.tvWindSpd.setText(String.format("公里/小时%s", hourlyBean.getWindSpeed()));
        detailBinding.tvHum.setText(String.format("%s%%", hourlyBean.getHumidity()));
        detailBinding.tvPres.setText(String.format("%shPa", hourlyBean.getPressure()));
        detailBinding.tvPop.setText(String.format("%s%%", hourlyBean.getPop()));
        detailBinding.tvDew.setText(String.format("%s℃", hourlyBean.getDew()));
        detailBinding.tvCloud.setText(String.format("%s%%", hourlyBean.getCloud()));
        dialog.show();

    }


    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        //根据cityFlag设置重新定位菜单项是否显示
        mMenu.findItem(R.id.item_relocation).setVisible(cityFlag == 1);
        //根据使用必应壁纸的状态，设置item项是否选中
        mMenu.findItem(R.id.item_bing).setChecked(MVUtils.getBoolean(Constant.USED_BING));
        return true;
    }

    /**
     * 菜单选项选中
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_relocation:              //重新定位
                startLocation();//点击重新定位item时，再次定位一下。
                break;
            case R.id.item_bing:                    //是否使用壁纸
                item.setChecked(!item.isChecked());
                MVUtils.put(Constant.USED_BING, item.isChecked());
                String bingUrl = MVUtils.getString(Constant.BING_URL);
                //更新壁纸
                updateBgImage(item.isChecked(), bingUrl);
                break;
            case R.id.item_manage_city:             //管理城市
                jumpActivityIntent.launch(new Intent(mContext, ManageCityActivity.class));
                break;
            case R.id.item_schedule_center:         //日程中心
                Intent intent1;
                if (isChecked) {
                    // 如果 isChecked 是 true，跳转到 DailyManager
                    intent1 = new Intent(this, DailyManager.class);
                } else {
                    // 如果 isChecked 是 false，跳转到 ManagerActivity
                    intent1 = new Intent(this, ManagerActivity.class);
                }
                startActivity(intent1);
                break;
            case R.id.item_music_player:            //音乐之家
                Intent intent2 = new Intent(this, MusicListActivity.class);
                startActivity(intent2);
                break;
        }
        return true;
    }

    /**
     * 更新壁纸
     */
    private void updateBgImage(boolean usedBing, String bingUrl) {
        if (usedBing && !bingUrl.isEmpty()) {
            GlideUtils.loadImg(this, bingUrl, binding.layRoot);
        } else {
            binding.layRoot.setBackground(ContextCompat.getDrawable(this, R.drawable.main_bg));
        }
    }

    private void startLocation() {
        cityFlag = 0;
        goodLocation.startLocation();
    }

    /**
     * 自定义Toolbar的图标
     */
    public void setToolbarMoreIconCustom(Toolbar toolbar) {
        if (toolbar == null) return;
        toolbar.setTitle("");
        Drawable moreIcon = ContextCompat.getDrawable(toolbar.getContext(), R.drawable.ic_round_add_32);
        if (moreIcon != null) toolbar.setOverflowIcon(moreIcon);
        setSupportActionBar(toolbar);
    }

    /**
     * 数据观察
     */
    @Override
    protected void onObserveData() {
        if (viewModel != null) {
            //城市数据返回
            viewModel.searchCityResponseMutableLiveData.observe(this, searchCityResponse -> {
                List<SearchCityResponse.LocationBean> location = searchCityResponse.getLocation();
                if (location != null && location.size() > 0) {
                    String id = location.get(0).getId();
                    //根据cityFlag设置重新定位菜单项是否显示
                    mMenu.findItem(R.id.item_relocation).setVisible(cityFlag == 1);
                    //检查到正在刷新
                    if (isRefresh) {
                        showMsg("刷新完成");
                        binding.layRefresh.setRefreshing(false);
                        isRefresh = false;
                    }
                    //获取到城市的ID
                    if (id != null) {
                        //通过城市ID查询城市实时天气
                        viewModel.nowWeather(id);
                        //通过城市ID查询天气预报
                        viewModel.dailyWeather(id);
                        //通过城市ID查询生活指数
                        viewModel.lifestyle(id);
                        //通过城市ID查询逐小时天气预报
                        viewModel.hourlyWeather(id);
                        //通过城市ID查询空气质量
                        viewModel.airWeather(id);
                    }
                }
            });
            //实况天气返回
            viewModel.nowResponseMutableLiveData.observe(this, nowResponse -> {
                NowResponse.NowBean now = nowResponse.getNow();
                if (now != null) {
                    binding.tvWeek.setText(EasyDate.getTodayOfWeek());//星期
                    binding.tvWeatherInfo.setText(now.getText());
                    binding.tvTemp.setText(now.getTemp());
                    //精简更新时间
                    String time = EasyDate.updateTime(nowResponse.getUpdateTime());
                    binding.tvUpdateTime.setText(String.format("最近更新时间：%s%s", EasyDate.showTimeInfo(time), time));

                    binding.tvWindDirection.setText(String.format("风向     %s", now.getWindDir()));//风向
                    binding.tvWindPower.setText(String.format("风力     %s级", now.getWindScale()));//风力
                    binding.wwBig.startRotate();//大风车开始转动
                    binding.wwSmall.startRotate();//小风车开始转动
                }
            });
            //天气预报返回
            viewModel.dailyResponseMutableLiveData.observe(this, dailyResponse -> {
                List<DailyResponse.DailyBean> daily = dailyResponse.getDaily();
                if (daily != null) {
                    if (dailyBeanList.size() > 0) {
                        dailyBeanList.clear();
                    }
                    dailyBeanList.addAll(daily);
                    dailyAdapter.notifyDataSetChanged();
                    //设置当天最高温和最低温
                    binding.tvHeight.setText(String.format("%s℃", daily.get(0).getTempMax()));
                    binding.tvLow.setText(String.format(" / %s℃", daily.get(0).getTempMin()));
                }
            });
            //生活指数返回
            viewModel.lifestyleResponseMutableLiveData.observe(this, lifestyleResponse -> {
                List<LifestyleResponse.DailyBean> daily = lifestyleResponse.getDaily();
                if (daily != null) {
                    if (lifestyleList.size() > 0) {
                        lifestyleList.clear();
                    }
                    lifestyleList.addAll(daily);
                    lifestyleAdapter.notifyDataSetChanged();
                }
            });
            //获取本地城市数据返回
            viewModel.cityMutableLiveData.observe(this, provinces -> {
                //城市弹窗初始化
                cityDialog = CityDialog.getInstance(MainActivity.this, provinces);
                cityDialog.setSelectedCityCallback(this);
            });
            //逐小时天气预报
            viewModel.hourlyResponseMutableLiveData.observe(this, hourlyResponse -> {
                List<HourlyResponse.HourlyBean> hourly = hourlyResponse.getHourly();
                if (hourly != null) {
                    if (hourlyBeanList.size() > 0) {
                        hourlyBeanList.clear();
                    }
                    hourlyBeanList.addAll(hourly);
                    hourlyAdapter.notifyDataSetChanged();
                }
            });
            //空气质量返回
            viewModel.airResponseMutableLiveData.observe(this, airResponse -> {
                AirResponse.NowBean now = airResponse.getNow();
                if (now == null) return;
                binding.rpbAqi.setMaxProgress(300);//最大进度，用于计算
                binding.rpbAqi.setMinText("0");//设置显示最小值
                binding.rpbAqi.setMinTextSize(32f);
                binding.rpbAqi.setMaxText("300");//设置显示最大值
                binding.rpbAqi.setMaxTextSize(32f);
                binding.rpbAqi.setProgress(Float.parseFloat(now.getAqi()));//当前进度
                binding.rpbAqi.setArcBgColor(getColor(R.color.arc_bg_color));//圆弧的颜色
                binding.rpbAqi.setProgressColor(getColor(R.color.arc_progress_color));//进度圆弧的颜色
                binding.rpbAqi.setFirstText(now.getCategory());//空气质量描述 取值范围：优，良，轻度污染，中度污染，重度污染，严重污染
                binding.rpbAqi.setFirstTextSize(44f);//第一行文本的字体大小
                binding.rpbAqi.setSecondText(now.getAqi());//空气质量值
                binding.rpbAqi.setSecondTextSize(64f);//第二行文本的字体大小
                binding.rpbAqi.setMinText("0");
                binding.rpbAqi.setMinTextColor(getColor(R.color.arc_progress_color));

                binding.tvAirInfo.setText(String.format("空气%s", now.getCategory()));

                binding.tvPm10.setText(now.getPm10());//PM10
                binding.tvPm25.setText(now.getPm2p5());//PM2.5
                binding.tvNo2.setText(now.getNo2());//二氧化氮
                binding.tvSo2.setText(now.getSo2());//二氧化硫
                binding.tvO3.setText(now.getO3());//臭氧
                binding.tvCo.setText(now.getCo());//一氧化碳
            });
            //错误信息返回
            viewModel.failed.observe(this, this::showLongMsg);
        }
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        //动态请求危险权限，只需要判断权限是否拥有即可
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //开始权限请求
            requestPermissionIntent.launch(permissions);
            return;
        }
        startLocation();//拥有权限，开始定位
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        goodLocation = GoodLocation.getInstance(this);
        goodLocation.setCallback(this);
    }

    /**
     * 接收定位信息
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        String district = bdLocation.getDistrict();     //获取区县
        if (viewModel != null && district != null) {
            mCityName = district; //定位后重新赋值
            //保存定位城市
            MVUtils.put(Constant.LOCATION_CITY, district);
            //保存到我的城市数据表中
            viewModel.addMyCityData(district);
            //显示当前定位城市
            binding.tvCity.setText(district);
            //搜索城市
            viewModel.searchCity(district);
        } else {
            Log.e("TAG", "district: " + district);
        }
    }

    /**
     * 选中城市
     */
    @Override
    public void selectedCity(String cityName) {
        cityFlag = 1;//切换城市
        mCityName = cityName;//切换城市后赋值
        //搜索城市
        viewModel.searchCity(cityName);
        //显示所选城市
        binding.tvCity.setText(cityName);
    }
}