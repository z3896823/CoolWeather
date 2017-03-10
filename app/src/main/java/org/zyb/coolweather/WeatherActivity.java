package org.zyb.coolweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.zyb.coolweather.Gson.DailyForecast;
import org.zyb.coolweather.Gson.HeWeather;
import org.zyb.coolweather.util.DailyForecastAdapter;
import org.zyb.coolweather.util.HttpUtil;
import org.zyb.coolweather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by Administrator on 2017/3/6.
 *
 * SharedPreferences设计：
 * name：historyInfo
 * keys: weatherInfo,weatherId,backgroundImageUrl
 *
 * 天气数据和背景图片的加载原则：
 * 尽量不要让用户看到空的layout，有历史数据的先加载上再说，后面再去服务器查找是否有更新
 */

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "ybz-WeatherActivity";

    public SwipeRefreshLayout swipeRefreshLayout;

    private String backgroundImageUrl;

    public String currentWeatherId;//注意这个修饰符不能用private

    public DrawerLayout drawerLayout;

    private ImageView iv_backgroundPic;

    //header
    private Button btn_menu;
    private TextView tv_currentLoc;
    private TextView tv_updateTime;

    //temperature
    private TextView tv_temperature;
    private TextView tv_condition;

    //forecast
    private ListView lv_forecast;

    //aqi
    private TextView tv_aqiIndex;
    private TextView tv_pmIndex;

    //suggestion
    private TextView tv_comfort;
    private TextView tv_carWash;
    private TextView tv_travel;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        sp =  getSharedPreferences("historyInfo",MODE_PRIVATE);
        editor = sp.edit();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipeRefresh);

        drawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
        iv_backgroundPic = (ImageView) findViewById(R.id.id_iv_backgroundPic);

        //header
        btn_menu = (Button) findViewById(R.id.id_btn_menu);
        tv_currentLoc = (TextView) findViewById(R.id.id_tv_currentLoc);
        tv_updateTime = (TextView) findViewById(R.id.id_tv_updateTime);
        //temperature
        tv_temperature = (TextView) findViewById(R.id.id_tv_temperature);
        tv_condition = (TextView) findViewById(R.id.id_tv_condition);
        //forecast
        lv_forecast = (ListView) findViewById(R.id.id_lv_forecast);
        //aqi
        tv_aqiIndex = (TextView) findViewById(R.id.id_tv_aqiIndex);
        tv_pmIndex = (TextView) findViewById(R.id.id_tv_pmIndex);
        //suggestion
        tv_comfort = (TextView) findViewById(R.id.id_tv_comfort);
        tv_carWash = (TextView) findViewById(R.id.id_tv_carWash);
        tv_travel = (TextView) findViewById(R.id.id_tv_travel);

        currentWeatherId = getIntent().getStringExtra("currentWeatherId");//如果intent中并无该key所对应的value则返回空

        if (currentWeatherId == null){
            currentWeatherId = sp.getString("weatherId",null);
        }//这个if非常有必要：如果是从选择页面过来的，就不要读历史的weatherId了，但是如果是二次进入，就必须读历史weatherId

        String historicalWeatherInfo = sp.getString("weatherInfo",null);
        if (historicalWeatherInfo != null){
            HeWeather weather = Utility.parseJsonWithGson(historicalWeatherInfo);
            showWeather(weather);
//            swipeRefreshLayout.setRefreshing(true);//先载入历史数据，然后自动刷新
        } else{
            requestWeather();
        }

        backgroundImageUrl = sp.getString("backgroundImageUrl",null);
        if (backgroundImageUrl != null){
            Glide.with(this).load(backgroundImageUrl).into(iv_backgroundPic);
        }

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather();
            }
        });
    }

    /**
     * request weather info here
     */
    public void requestWeather(){

        String weatherUrl;
        //如果既不是从城市选择活动跳过来的，而且本地没有历史数据，则return掉requestWeather防止出错
        if (currentWeatherId != null){
            weatherUrl = "http://guolin.tech/api/weather?cityid="+currentWeatherId+"&key=cfde08fa60e34a8da5e959c79fc181b6";
        } else {
            Toast.makeText(this, "历史数据异常，请选择城市", Toast.LENGTH_SHORT).show();
            return;
        }
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                Log.d(TAG, result);
                final HeWeather heWeather = Utility.parseJsonWithGson(result);//get weather object
                if (heWeather != null && heWeather.status.equals("ok")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //把天气数据存储到本地，用sharedPreference
                            editor.putString("weatherInfo",result);
                            editor.putString("weatherId",currentWeatherId);
                            editor.apply();
                            showWeather(heWeather);
                            swipeRefreshLayout.setRefreshing(false);//隐藏进度条
                        }
                    });
                } else {
                    Log.d(TAG, "onResponse: heWeather == null or status != ok");
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "requestWeather--onFailure: 获取天气信息失败");
                        swipeRefreshLayout.setRefreshing(false);//获取失败的情况下也要隐藏进度条
                    }
                });
            }
        });
    }

    /**
     * show weatherInfo on activity
     */
    public void showWeather(HeWeather heWeather){

        //header
        tv_currentLoc.setText(heWeather.basic.city);
        tv_updateTime.setText(heWeather.basic.updateTime.time.split(" ")[1]);//正则表达式，看一下split方法的源码即明白
        //temperature
        tv_temperature.setText(heWeather.now.temperature + "°C");
        tv_condition.setText(heWeather.now.condition.condition);
        //forecastList
        List<DailyForecast> dailyForecastList = heWeather.dailyForecastList;
        DailyForecastAdapter adapter = new DailyForecastAdapter(this,R.layout.daily_forecast_item,dailyForecastList);
        lv_forecast.setAdapter(adapter);
        //aqi
        if (heWeather.aqi != null){
            tv_aqiIndex.setText(heWeather.aqi.aqiCity.aqiIndex);
            tv_pmIndex.setText(heWeather.aqi.aqiCity.pm25Index);
        } else{
            tv_aqiIndex.setText("无");
            tv_pmIndex.setText("无");
        }
        //suggestion
        tv_comfort.setText("舒适度："+heWeather.suggestion.comf.comfortInfo);
        tv_carWash.setText("洗车指数："+heWeather.suggestion.carWash.carWashInfo);
        tv_travel.setText("出行建议："+heWeather.suggestion.travel.travelInfo);
        //backgroundImage
        getBackgroundPic();
    }

    /**
     * load background image
     */
    public void getBackgroundPic(){

        //把图片地址存到本地，每次打开时先加载本地图片，并获取新的图片地址与本地进行比较，如果相同，不下载图片，如果不同，联网下载新的图片
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String picUrl = response.body().string();//服务器上的最新图片url
                //第一次载入时本地的backgroundImageUrl是null，在这里会导致空指针，所以加一个非空判断
                if (backgroundImageUrl != null && backgroundImageUrl.equals(picUrl)){
                    //空方法体，直接执行完，效果相当于return
                } else {
                    //更新本地的图片url
                    editor.putString("backgroundImageUrl",picUrl);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(picUrl).into(iv_backgroundPic);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "load background picture failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
