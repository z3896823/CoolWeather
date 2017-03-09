package org.zyb.coolweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
 */

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "ybz";

    private String currentLoc;
    private String currentWeatherId;

    private DrawerLayout drawerLayout;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        drawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
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

//        currentLoc = getIntent().getStringExtra("currentLoc");
        currentWeatherId = getIntent().getStringExtra("currentWeatherId");
//        tv_currentLoc.setText(currentLoc);

        //先从本地加载数据把空白填上然后再请求新的数据
        SharedPreferences sharedPreferences = getSharedPreferences("historyInfo",MODE_PRIVATE);
        String history = sharedPreferences.getString("originalInfo","");
        if (!history.isEmpty()){
            showWeather(Utility.parseJsonWithGson(history));
        }

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        requestWeather();


    }

    /**
     * request weather info here
     */
    public void requestWeather(){

        String weatherUrl = "http://guolin.tech/api/weather?cityid="+currentWeatherId+"&key=cfde08fa60e34a8da5e959c79fc181b6";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                final HeWeather heWeather = Utility.parseJsonWithGson(result);//get weather object
                if (heWeather != null && heWeather.status.equals("ok")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //把天气数据存储到本地，用sharedPreference
                            SharedPreferences sharedPreferences = getSharedPreferences("historyInfo",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("originalInfo",result);
                            editor.apply();
                            showWeather(heWeather);
                        }
                    });
                } else {
                    Log.d(TAG, "onResponse: heWeather == null");
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * show weather info on activity
     */
    public void showWeather(HeWeather heWeather){

        //header
        tv_currentLoc.setText(heWeather.basic.city);
        tv_updateTime.setText(heWeather.basic.updateTime.time);
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
            tv_aqiIndex.setText("无数据");
            tv_pmIndex.setText("无数据");
        }
        //suggestion
        tv_comfort.setText("舒适度："+heWeather.suggestion.comf.comfortInfo);
        tv_carWash.setText("洗车指数："+heWeather.suggestion.carWash.carWashInfo);
        tv_travel.setText("出行建议："+heWeather.suggestion.travel.travelInfo);
    }
}
