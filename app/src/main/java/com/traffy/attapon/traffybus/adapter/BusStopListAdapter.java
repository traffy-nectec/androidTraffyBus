package com.traffy.attapon.traffybus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.traffy.attapon.traffybus.R;

import java.util.ArrayList;


public class BusStopListAdapter extends BaseAdapter{
    private Holder holder;
    private ArrayList<String> data;

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return 10;
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
          /*  holder = new Holder();
            holder.title = (TextView) convertView.findViewById(R.id.tile1);
            convertView.setTag(holder);*/
        } else {

            holder = (Holder)convertView.getTag();
        }

      //  holder.title.setText(data.get(position));

        return convertView;

    }
    private class Holder {
        TextView title;

    }
}
