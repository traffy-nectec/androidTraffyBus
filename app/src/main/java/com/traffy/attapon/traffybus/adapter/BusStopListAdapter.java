package com.traffy.attapon.traffybus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.traffy.attapon.traffybus.DAO.BusStopItemCollectionDao;
import com.traffy.attapon.traffybus.R;

import java.util.ArrayList;
import java.util.List;


public class BusStopListAdapter extends BaseAdapter {
    private Holder holder;
    private List<BusStopItemCollectionDao> data;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_busstop, null);
            holder = new Holder();
            holder.lv_stop_name = (TextView) convertView.findViewById(R.id.lv_stop_name);
            holder.lv_predict_time = (TextView) convertView.findViewById(R.id.lv_predict_time);
            holder.lv_previous_stop = (TextView) convertView.findViewById(R.id.lv_previous_stop);
            holder.lv_bmta_id = (TextView) convertView.findViewById(R.id.lv_bmta_id);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.lv_stop_name.setText(data.get(position).getStopName());
        holder.lv_predict_time.setText(""+Math.abs(data.get(position).getPredictTime()));
        holder.lv_previous_stop.setText("["+data.get(position).getNumberOfNexts().toString()+"ป้าย]");
        holder.lv_bmta_id.setText(data.get(position).getStopId().toString());

        return convertView;

    }

    private class Holder {
        TextView lv_previous_stop;
        TextView lv_predict_time;
        TextView lv_stop_name;
        TextView lv_bmta_id;

    }
}
