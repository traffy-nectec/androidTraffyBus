package com.traffy.attapon.traffybus.adapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.traffy.attapon.traffybus.DAO.BusStopItemCollectionDao;
import com.traffy.attapon.traffybus.R;
import com.traffy.attapon.traffybus.util.SharedPreNoTiBus;

import java.util.ArrayList;
import java.util.List;


public class BusStopListAdapter extends BaseAdapter {

    private final SharedPreNoTiBus sharedPreNoTiBus;
    private final Context context;
    private Holder holder;
    private List<BusStopItemCollectionDao> data;

    public BusStopListAdapter(Context context) {
        this.context = context;
        sharedPreNoTiBus = new SharedPreNoTiBus(context);
    }

    public void setData(List<BusStopItemCollectionDao> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        } else {
            return data.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (sharedPreNoTiBus.getNotiBus().equals(data.get(position).getStopId())) {
            return 1;
        } else {
            return 0;
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_busstop, null);
            holder = new Holder();

            holder.lv_stop_name = (TextView) convertView.findViewById(R.id.lv_stop_name);
            holder.lv_predict_time = (TextView) convertView.findViewById(R.id.lv_predict_time);
            holder.lv_previous_stop = (TextView) convertView.findViewById(R.id.lv_previous_stop);
            holder.img_ic_alert = (ImageView) convertView.findViewById(R.id.img_ic_alert);

            convertView.setTag(holder);
        } else {

            holder = (Holder) convertView.getTag();
        }

        holder.lv_stop_name.setText(data.get(position).getStopName());
        holder.lv_predict_time.setText("" + Math.abs(data.get(position).getPredictTime()));
        holder.lv_previous_stop.setText("[" + data.get(position).getNumberOfNexts().toString() + "ป้าย]");


        if (getItemViewType(position) == 1) {
            holder.img_ic_alert.setVisibility(View.VISIBLE);
        }else{
            holder.img_ic_alert.setVisibility(View.INVISIBLE);
        }


        return convertView;

    }


    private class Holder {
        TextView lv_previous_stop;
        TextView lv_predict_time;
        TextView lv_stop_name;
        ImageView img_ic_alert;
    }

}
