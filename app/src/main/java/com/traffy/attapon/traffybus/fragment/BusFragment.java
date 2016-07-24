package com.traffy.attapon.traffybus.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.traffy.attapon.traffybus.R;
import com.traffy.attapon.traffybus.adapter.BusStopListAdapter;

import java.util.ArrayList;


public class BusFragment extends Fragment {


    private String busId;
    private ListView lv_BusStop;
    private BusStopListAdapter busStopListAdapter;
    private ArrayList<String> data;


    public static BusFragment newInstance(String busId) {
        BusFragment fragment = new BusFragment();
        Bundle args = new Bundle();
        args.putString("busId", busId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        busId = getArguments().getString("busId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bus, container, false);
        initInstances(rootView);
        Toast.makeText(getContext(), "" + busId, Toast.LENGTH_SHORT).show();

        return rootView;
    }

    private void initInstances(View rootView) {

        data = new ArrayList<String>();

        for (int i = 0; i < 799; i++) {
            data.add("data" + (i + 1));
        }


        lv_BusStop = (ListView) rootView.findViewById(R.id.lv_BusStop);
        busStopListAdapter = new BusStopListAdapter();
        busStopListAdapter.setData(data);
        lv_BusStop.setAdapter(busStopListAdapter);


    }//End of initInstances


}
