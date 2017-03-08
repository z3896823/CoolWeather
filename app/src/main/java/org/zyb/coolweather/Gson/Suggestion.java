package org.zyb.coolweather.Gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/6.
 */

public class Suggestion {

    @SerializedName("air")
    public Air air;

    @SerializedName("comf")
    public Comfort comf;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("trav")
    public Travel travel;

    public class Air{

        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String airInfo;
    }

    public class Comfort {

        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String comfortInfo;
    }

    public class CarWash{

        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String carWashInfo;
    }

    public class Travel{

        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String travelInfo;
    }
}
