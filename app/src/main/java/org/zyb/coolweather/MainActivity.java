package org.zyb.coolweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 这个Activity只是个空壳，其内部的实现全都在Fragment中
 */
public class MainActivity extends AppCompatActivity {

    private  static boolean isFirstLaunch  = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
