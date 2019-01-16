package com.jiage.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jiage.weather.gson.Forecast;
import com.jiage.weather.gson.Weather;
import com.jiage.weather.gson.WeatherAqi;
import com.jiage.weather.gson.WeatherStyle;
import com.jiage.weather.gson.Lifestyle;
import com.jiage.weather.service.AutoUpdateService;
import com.jiage.weather.util.HttpUtil;
import com.jiage.weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity  extends AppCompatActivity {
    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView uvText;

    private TextView sportText;

    private ImageView bingPicImg;

    private LinearLayout aqilayout;

    private TextView kqzsText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //状态栏透明效果
        if(Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        uvText = (TextView) findViewById(R.id.uv_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_button);
        aqilayout = (LinearLayout) findViewById(R.id.aqi_layout);
        kqzsText = (TextView) findViewById(R.id.kqzs_text);
        //显示BING的搜索引擎背景图
        loadBingPic();
        //读取缓存里面的历史天气，还需要判断如果缓存是昨天天气。那么就要强制更新
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        final String weatherId;
        if(weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            String weatherStyleString = prefs.getString("weatherStyle",null);
            String weatherAqiString = prefs.getString("weatherAqi",null);
            WeatherAqi weatherAqi = Utility.handleWeatherAQIResponse(weatherAqiString);
            WeatherStyle weatherStyle = Utility.handleWeatherStyleResponse(weatherStyleString);
            showWeatherInfo(weather);
            if(weatherAqi!=null) {
                showWeatherAQI(weatherAqi);
            }else {
                aqilayout.setVisibility(View.GONE);
            }
            if(weatherStyle!=null) {
                showWeatherStyle(weatherStyle);
            }

        }else{
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    /**
     * 根据天气id请求城市天气信息。
     */
    //https://free-api.heweather.com/s6/weather/forecast?key=5c043b56de9f4371b0c7f8bee8f5b75e&location=shishou
    //https://free-api.heweather.net/s6/weather/forecast?key=1dd4d0ebca834cc6a22c41364da7eb7e&location=CN101200804
    public void requestWeather(final String weatherId) {

        String weatherUrl = "https://free-api.heweather.net/s6/weather/forecast?key=1dd4d0ebca834cc6a22c41364da7eb7e&location=" + weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            requestWeatherSytle(weatherId); //查询天气生活指数
                            requestWeatherAQI(weatherId);   //查询PM2.5及API指数
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });


    }

    /**
     * 全国部分城市天气AQI及PM2.5指数显示
     * 例如：https://free-api.heweather.net/s6/air/now?key=1dd4d0ebca834cc6a22c41364da7eb7e&location=yichang
     */
    public void requestWeatherAQI(final String weatherId) {
        String weatherUrl = "https://free-api.heweather.net/s6/air/now?key=1dd4d0ebca834cc6a22c41364da7eb7e&location=" + weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final WeatherAqi weatherAqi = Utility.handleWeatherAQIResponse(responseText);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        if(weatherAqi != null && "ok".equals(weatherAqi.status)){
                            editor.putString("weatherAqi",responseText);
                            aqilayout.setVisibility(View.VISIBLE);
                            showWeatherAQI(weatherAqi);
                        }else{
                            //没查询到AQI值，则数据错误或者该城市没AQI或者PM2.5，隐藏该布局
                            editor.putString("weatherAqi",null);
                            aqilayout.setVisibility(View.GONE);
                        }
                        editor.apply();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * 加载生活指数
     */
    public void requestWeatherSytle(final String weatherId) {
        String weatherUrl = "https://free-api.heweather.net/s6/weather/lifestyle?key=1dd4d0ebca834cc6a22c41364da7eb7e&location=" + weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final WeatherStyle weatherStyle = Utility.handleWeatherStyleResponse(responseText);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        if(weatherStyle != null && "ok".equals(weatherStyle.status)){

                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weatherStyle",responseText);
                            editor.apply();

                            showWeatherStyle(weatherStyle);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather){
        int first = 1;
        String cityName = weather.basic.cityName;
        String updateTime = "天气更新时间 " + weather.update.updatetimelocate.split(" ")[1];
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        forecastLayout.removeAllViews();

        for(Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            if(first == 1){
                //显示今日天气信息
                String weatherInfo = forecast.weatherday;
                String degree =forecast.tmp_min + "/ " + forecast.tmp_max + "℃";
                degreeText.setText(degree);
                weatherInfoText.setText(weatherInfo);
                first = 2;
            }
            dateText.setText(forecast.date);
            infoText.setText(forecast.weatherday);
            maxText.setText(forecast.tmp_max);
            minText.setText(forecast.tmp_min);
            forecastLayout.addView(view);
        }

        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /**
     * 处理并展示WeatherSytle 实体类中的数据--生活指数。
     */
    private void showWeatherStyle(WeatherStyle weatherstyle){

        for(Lifestyle lifestylelist:weatherstyle.Weatherlifestyle)
        {
            if(lifestylelist.type.equals("comf")) {
                comfortText.setText("舒适度：" + lifestylelist.txt);
            }else if(lifestylelist.type.equals("uv")){
                uvText.setText("空气指数：" + lifestylelist.txt);
            }else if(lifestylelist.type.equals("sport")){
                sportText.setText("运动建议：" + lifestylelist.txt);
            }
        }
    }

    /**
     * 处理并展示WeatherAQI  实体类中的数据--PM2.5及 AQI指数。
     */
    private void showWeatherAQI(WeatherAqi weatherAqi){
        //最后更新时间+空气指数
        kqzsText.setText("空气质量：" + weatherAqi.weatheraqi.qlty);
        aqiText.setText(weatherAqi.weatheraqi.aqi);
        pm25Text.setText(weatherAqi.weatheraqi.pm25);
    }
}
