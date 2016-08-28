package com.traffy.attapon.traffybus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.traffy.attapon.traffybus.R;

import java.util.List;

/**
 * Created by chanpc on 7/26/2016.
 */
public class routAdapter extends ArrayAdapter<Bus> {

    TextView busName;
    TextView description;
    public routAdapter(Context context, List<Bus> list)
    {
        super(context, R.layout.rout_list_layout,list);
    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.rout_list_layout,parent,false);

        busName = (TextView)customView.findViewById(R.id.busName);
        busName.setText(getItem(position).getName());
        description = (TextView)customView.findViewById(R.id.bus_line);
        description.setText(getItem(position).getStart()+" - "+getItem(position).getEnd());
        return customView;

    }
}
