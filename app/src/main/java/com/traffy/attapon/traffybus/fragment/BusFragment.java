package com.traffy.attapon.traffybus.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.traffy.attapon.traffybus.DAO.BusStopItemCollectionDao;
import com.traffy.attapon.traffybus.R;
import com.traffy.attapon.traffybus.adapter.BusStopListAdapter;
import com.traffy.attapon.traffybus.manager.HttpManager;
import com.traffy.attapon.traffybus.manager.http.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BusFragment extends Fragment {


    private String busId;
    private ListView lv_BusStop;
    private BusStopListAdapter busStopListAdapter;
    private SwipeRefreshLayout swipe_BusStopList;


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

        View rootView = inflater.inflate(R.layout.fragment_bus, container, false);

        lv_BusStop = (ListView) rootView.findViewById(R.id.lv_BusStop);
        swipe_BusStopList = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_BusStopList);

        busStopListAdapter = new BusStopListAdapter();
        lv_BusStop.setAdapter(busStopListAdapter);

        initInstances(rootView);

        return rootView;
    }

    private void initInstances(View rootView) {
        swipe_BusStopList.setOnRefreshListener(new BusStopOnRefreshListener());
        CallConApiBusStop();
        Thread threadCallApi = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10000);
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // update here!
                                    CallConApiBusStop();
                                }
                            });
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                }
            }


        };

        threadCallApi.start();


    }//End of initInstances

    private void CallConApiBusStop() {
        Call<List<BusStopItemCollectionDao>> call = HttpManager.getInstance().getService().loadBusStopList(busId);
        call.enqueue(new ListCallback());
    }

    private void showToast(String str) {
        Toast.makeText(getContext(), "" + str, Toast.LENGTH_SHORT).show();
    }

    private class ListCallback implements Callback<List<BusStopItemCollectionDao>> {
        @Override
        public void onResponse(Call<List<BusStopItemCollectionDao>> call, Response<List<BusStopItemCollectionDao>> response) {
            if (response.isSuccessful()) {
                
                swipe_BusStopList.setRefreshing(false);
                List<BusStopItemCollectionDao> dao = response.body();
                busStopListAdapter.setData(dao);
                busStopListAdapter.notifyDataSetChanged();

            } else {
                showToast("ติดต่อข้อมูลไม่ได้");
                swipe_BusStopList.setRefreshing(false);
            }

        }

        @Override
        public void onFailure(Call<List<BusStopItemCollectionDao>> call, Throwable t) {
            swipe_BusStopList.setRefreshing(false);
            showToast("ติดต่อข้อมูลไม่ได้");
        }
    }//End of ListCallback


    private class BusStopOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            CallConApiBusStop();
        }
    }//End of BusStopOnRefreshListener


}// End of BusFragment
