package org.zyb.coolweather.util;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zyb.coolweather.Gson.HeWeather;

/**
 * Created by Administrator on 2017/3/7.
 *
 * 这段JSON数据格式真的很奇葩
 * 首先，全部数据是一个对象，这个对象中有一对键值对，键为HeWeather，值为一个数组
 * 他妈的这个数组中只有一个元素，这个元素是一个对象（对应创建的HeWeather类）
 * 这个对象中又包含一系列对象（对应HeWeather类中持有的一系列对象的引用）
 *
 * 所以解析这个json数据，首先用一个JsonObject来对应它的全部，然后使用键HeWeather取出它的值--一个数组
 * 然后再从这个数组中取出它的第0个元素，即为HeWeather类的json内容
 *
 * 需要注意到的问题是：Json中的键是可以对应一个数组的，所以这里才根据一个键取出了一个数组
 */

public class Utility {

    private static final String TAG = "ybz";

    public static HeWeather parseJsonWithGson(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            jsonObject = jsonArray.getJSONObject(0);
            result = jsonObject.toString();
            Gson gson = new Gson();
            HeWeather heWeather = gson.fromJson(result,HeWeather.class);
            return heWeather;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseJsonWithGson: 解析json时出错，请检查服务器返回的json数据");
        }
        return null;
    }
}
