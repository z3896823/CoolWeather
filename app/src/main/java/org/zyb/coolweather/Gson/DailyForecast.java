package org.zyb.coolweather.Gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/7.
 */

public class DailyForecast {

    @SerializedName("date")
    public String date;

    @SerializedName("hum")
    public String humidity;

    @SerializedName("uv")
    public String uv;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public Condition condition;

    public class Temperature{

        @SerializedName("max")
        public String tempMax;

        @SerializedName("min")
        public String tempMin;
    }

    public class Condition{

        @SerializedName("txt_d")
        public String condition;
    }

}
