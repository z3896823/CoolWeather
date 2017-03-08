package org.zyb.coolweather.Gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/6.
 */

public class Now {

    @SerializedName("hum")
    public String humidity;


    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public Condition condition;

    public class Condition{

        @SerializedName("txt")
        public String condition;
    }

    public class Wind{

        @SerializedName("deg")
        public String windDeg;

        @SerializedName("dir")
        public String windDir;

        @SerializedName("spd")
        public String windSpeed;
    }

}
