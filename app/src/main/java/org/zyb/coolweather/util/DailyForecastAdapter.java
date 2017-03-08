package org.zyb.coolweather.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.zyb.coolweather.Gson.DailyForecast;
import org.zyb.coolweather.R;

import java.util.List;

/**
 * Created by Administrator on 2017/3/8.
 */

public class DailyForecastAdapter extends ArrayAdapter<DailyForecast> {

    private Context context;
    private int itemId;

    public DailyForecastAdapter(Context context, int itemId, List<DailyForecast> dailyForecastList){
        super(context,itemId,dailyForecastList);
        this.context = context;
        this.itemId = itemId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DailyForecast dailyForecast = getItem(position);
        View itemView = LayoutInflater.from(context).inflate(itemId,parent,false);

        TextView tv_date = (TextView) itemView.findViewById(R.id.id_tv_date);
        TextView tv_condition = (TextView) itemView.findViewById(R.id.id_tv_condition);
        TextView tv_maxTemp = (TextView) itemView.findViewById(R.id.id_tv_maxTemp);
        TextView tv_minTemp = (TextView) itemView.findViewById(R.id.id_tv_minTemp);

        tv_date.setText(dailyForecast.date);
        tv_condition.setText(dailyForecast.condition.condition);
        tv_maxTemp.setText(dailyForecast.temperature.tempMax);
        tv_minTemp.setText(dailyForecast.temperature.tempMin);

        return itemView;
    }
}
