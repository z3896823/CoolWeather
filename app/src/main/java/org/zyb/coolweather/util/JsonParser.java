package org.zyb.coolweather.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.zyb.coolweather.table.City;
import org.zyb.coolweather.table.County;
import org.zyb.coolweather.table.Province;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/4.
 */

public class JsonParser {

    public static boolean parseProvinceResponse(String result) throws IOException {
        if (!TextUtils.isEmpty(result)){
            try{
                JSONArray provinceList = new JSONArray(result);
                for (int i = 0; i<provinceList.length();i++){
                    JSONObject jsonObject = provinceList.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceId(jsonObject.getInt("id"));
                    province.setProvinceName(jsonObject.getString("name"));
                    province.save();
                }
                return true;
            } catch(Exception e){
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean parseCityResponse(String result,int provinceId) throws IOException {
        if (!TextUtils.isEmpty(result)){
            try{
                JSONArray provinceList = new JSONArray(result);
                for (int i = 0; i<provinceList.length();i++){
                    JSONObject jsonObject = provinceList.getJSONObject(i);
                    City city = new City();
                    city.setCityId(jsonObject.getInt("id"));
                    city.setCityName(jsonObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch(Exception e){
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean parseCountyResponse(String result,int cityId) throws IOException {
        if (!TextUtils.isEmpty(result)){
            try{
                JSONArray provinceList = new JSONArray(result);
                for (int i = 0; i<provinceList.length();i++){
                    JSONObject jsonObject = provinceList.getJSONObject(i);
                    County county = new County();
                    county.setCountyId(jsonObject.getInt("id"));
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch(Exception e){
                return false;
            }
        } else {
            return false;
        }
    }
}
