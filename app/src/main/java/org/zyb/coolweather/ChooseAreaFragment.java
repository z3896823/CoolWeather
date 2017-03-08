package org.zyb.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.zyb.coolweather.table.City;
import org.zyb.coolweather.table.County;
import org.zyb.coolweather.table.Province;
import org.zyb.coolweather.util.HttpUtil;
import org.zyb.coolweather.util.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/5.
 *
 * 使用queryXxx去寻找数据更新UI，如果找不到就queryFromServer
 * 从网络down下数据后存入数据库，再次执行上一行的操作
 */

public class ChooseAreaFragment  extends Fragment {

    private static final String TAG = "ybz";
    private ProgressDialog progressDialog ;

    private TextView tv_title;
    private Button btn_back;
    private ListView lv_locs;
    private ArrayAdapter<String> adapter;//attention
    private List<String> dataList = new ArrayList<>();

    private static int currentLevel;
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private Province selectedProvince;
    private City selectedCity;

    List<Province> provinceList;
    List<City> cityList;
    List<County> countyList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area_fragment,container,false);
        tv_title = (TextView) view.findViewById(R.id.id_tv_title);
        btn_back = (Button) view.findViewById(R.id.id_btn_back);
        lv_locs = (ListView) view.findViewById(R.id.id_lv_locs);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        lv_locs.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvinces();
        lv_locs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel ==LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY){
                    Toast.makeText(getActivity(),"weather_id is: "+ countyList.get(position).getWeatherId(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("currentLoc",countyList.get(position).getCountyName());
                    intent.putExtra("currentWeatherId",countyList.get(position).getWeatherId());
                    startActivity(intent);
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                } else if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }
            }
        });
    }

    private void queryProvinces(){
        //首先去数据库找，没有就调用queryFromServer
        //最后把数据赋值给dataLiat
        tv_title.setText("中国");
        btn_back.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();//这个方法必须运行在主线程上
            currentLevel = LEVEL_PROVINCE;//列表载入成功后在设置这个参数比较好
        } else {
            queryFromServer("http://guolin.tech/api/china","province");
        }
    }

    private void queryCities(){
        //根据selectProvince这个参数来请求数据
        tv_title.setText(selectedProvince.getProvinceName());
        btn_back.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectedProvince.getProvinceId())).find(City.class);
        if (!cityList.isEmpty()){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lv_locs.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            String address = "http://guolin.tech/api/china/" + selectedProvince.getProvinceId();
            queryFromServer(address,"city");
        }
    }

    private void queryCounties() {
        //根据selectedCity去请求county数据
        tv_title.setText(selectedCity.getCityName());
        countyList = DataSupport.where("cityId = ?",String.valueOf(selectedCity.getCityId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lv_locs.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            String address = "http://guolin.tech/api/china/" + selectedProvince.getProvinceId() + "/" + selectedCity.getCityId();
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(String address, final String requestCode){
        showDialog();
        //调用工具包里的方法来访问服务器
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //该方法运行在子线程中，不能更新UI
                String result = response.body().string();
                switch (requestCode) {
                    case "province":
                        if (JsonParser.parseProvinceResponse(result)) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    queryProvinces();
                                    dismissDialog();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "出错了", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case "city":
                        if (JsonParser.parseCityResponse(result,selectedProvince.getProvinceId())) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    queryCities();
                                    dismissDialog();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "出错了", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case "county":
                        if (JsonParser.parseCountyResponse(result,selectedCity.getCityId())) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    queryCounties();
                                    dismissDialog();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "出错了", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });

    }

    private void showDialog(){
        if (progressDialog ==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("loading..");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void dismissDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
