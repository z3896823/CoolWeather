package org.zyb.coolweather.Gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/3/7.
 */

public class HeWeather {

    public String status;

    public Basic basic;
    public Aqi aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<DailyForecast> dailyForecastList;
}
