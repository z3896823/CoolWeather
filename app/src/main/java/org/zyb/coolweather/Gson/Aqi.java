package org.zyb.coolweather.Gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/6.
 */

public class Aqi {

    @SerializedName("city")
    public AQICity aqiCity;

    public class AQICity{

        @SerializedName("aqi")
        public String aqiIndex;

        @SerializedName("pm25")
        public String pm25Index;
    }
}
