package org.zyb.coolweather.util;

import android.util.Log;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/4.
 */

public class HttpUtil {

    private static final String TAG = "ybz";
    //传入不同的address，获得不同的结果
    public static void sendOkHttpRequest(String address, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);//子线程在enqueue方法中开启
    }
}
