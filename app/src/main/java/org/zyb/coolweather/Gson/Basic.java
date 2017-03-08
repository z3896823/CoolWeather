package org.zyb.coolweather.Gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/7.
 */

public class Basic {

    @SerializedName("update")
    public UpdateTime updateTime;

    public class UpdateTime{
        @SerializedName("loc")
        public String time;
    }
}
