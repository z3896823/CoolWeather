package org.zyb.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 这个Activity只是个空壳，其内部的实现全都在Fragment中
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("launchInfo",MODE_PRIVATE);
        Boolean isFirstLaunch = sp.getBoolean("isFirstLaunch",true);

        if (!isFirstLaunch){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
