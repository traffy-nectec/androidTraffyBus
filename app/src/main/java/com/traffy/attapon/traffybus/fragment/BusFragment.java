package com.traffy.attapon.traffybus.fragment;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.traffy.attapon.traffybus.DAO.BusStopItemCollectionDao;
import com.traffy.attapon.traffybus.R;
import com.traffy.attapon.traffybus.activity.BusActivity;
import com.traffy.attapon.traffybus.adapter.BusStopListAdapter;
import com.traffy.attapon.traffybus.manager.HttpManager;
import com.traffy.attapon.traffybus.util.SharedPreNoTiBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BusFragment extends Fragment {


    private String busId;
    private ListView lv_BusStop;
    private BusStopListAdapter busStopListAdapter;
    private SwipeRefreshLayout swipe_BusStopList;
    private TextView tv_busStop_name;
    private TextView tv_con_txt;
    private TextView tv_bus_route;
    private SharedPreNoTiBus sharedPreNoTiBus;
    private List<BusStopItemCollectionDao> dao;
    private final int MOD_NOTI_CANCEL = 0;
    private final int MOD_NOTI_TIME10 = 1;
    private final int MOD_NOTI_NEXT_TO = 2;
    private int notificationMod = 0;


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

        tv_busStop_name = (TextView) rootView.findViewById(R.id.tv_busStop_name);
        lv_BusStop = (ListView) rootView.findViewById(R.id.lv_BusStop);
        swipe_BusStopList = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_BusStopList);
        tv_con_txt = (TextView) rootView.findViewById(R.id.tv_con_txt);
        tv_bus_route = (TextView) rootView.findViewById(R.id.tv_bus_route);

        busStopListAdapter = new BusStopListAdapter(getContext());
        sharedPreNoTiBus = new SharedPreNoTiBus(getContext());

        lv_BusStop.setAdapter(busStopListAdapter);


        initInstances(rootView);

        return rootView;
    }

    private void initInstances(final View rootView) {
        CallConApiBusStop();
        lv_BusStop.setOnScrollListener(new lvBusStopOnScrollListener());
        swipe_BusStopList.setOnRefreshListener(new BusStopOnRefreshListener());
        lv_BusStop.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_BusStopId = (TextView) view.findViewById(R.id.lv_bmta_id);
                String busStopId = tv_BusStopId.getText().toString();
                if (busStopId == null)
                    busStopId = "";
                final CharSequence[] items = {"แจ้งเตือนก่อนถึงป้าย 10 นาที", "แจ้งเตือนก่อนถึงหนึ่งป้าย", "ยกเลิก"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        switch (position) {
                            case 0:
                                notificationMod = MOD_NOTI_TIME10;
                                break;
                            case 1:
                                notificationMod = MOD_NOTI_NEXT_TO;
                                break;
                            case 2:
                                notificationMod = MOD_NOTI_CANCEL;
                                sharedPreNoTiBus.resetSharedPreNoTiBus();
                                busStopListAdapter.notifyDataSetChanged();
                                break;
                            default:
                                notificationMod = MOD_NOTI_CANCEL;
                                sharedPreNoTiBus.resetSharedPreNoTiBus();
                                busStopListAdapter.notifyDataSetChanged();
                        }

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                showToast("stopid = " + busStopId);
                sharedPreNoTiBus.resetSharedPreNoTiBus();
                sharedPreNoTiBus.setNotiBus(busStopId);

                busStopListAdapter.notifyDataSetChanged();
                //lv_BusStop.setAdapter(busStopListAdapter);

            }
        });

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
                        }
                    }
                } catch (InterruptedException e) {
                }
            }


        };

        threadCallApi.start();


    }//End of initInstances

    ///////////////////////////////////////////////////
    /////////////////// method ///////////////////////
    //////////////////////////////////////////////////

    private void CallConApiBusStop() {
        Call<List<BusStopItemCollectionDao>> call = HttpManager.getInstance().getService().loadBusStopList(busId);
        call.enqueue(new ListCallback());
    }

    private void showToast(String str) {
        Toast.makeText(getContext(), "" + str, Toast.LENGTH_SHORT).show();
    }

    public void showNotification(String bmta_id, String stopName, Integer predict_time) {

        Log.d("dd", "time ---" + predict_time);
        Log.d("dd", "bmta_id ---" + bmta_id);
        Log.d("dd-", "shared id ---" + sharedPreNoTiBus.getNotiBus());

        String busStopId = sharedPreNoTiBus.getNotiBus();

        if (notificationMod == MOD_NOTI_TIME10) {
            if (busStopId.equals(bmta_id) && predict_time <= 10)
                NotificationBus(bmta_id,stopName, predict_time);
        } else if (notificationMod == MOD_NOTI_NEXT_TO) {
            if (busStopId.equals(bmta_id))
                NotificationBus(bmta_id,stopName ,predict_time);
        } else {

        }

    }

    private void NotificationBus(String bmta_id,String stopName, Integer predict_time) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int intbmta_id = Integer.parseInt(bmta_id.replace("-", ""));

        Intent intent = new Intent(getContext(), BusActivity.class);
        intent.putExtra("busId", busId);
        PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        sharedPreNoTiBus.resetSharedPreNoTiBus();
        Notification mNotification = new Notification.Builder(getContext())
                .setContentTitle(stopName)
                .setContentText("ถึงในอีก " + predict_time + " นาที  ")
                .setSmallIcon(R.drawable.bus_icon)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .build();
        NotificationManager notificationManager = (NotificationManager) getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(intbmta_id, mNotification);
    }

    ///////////////////////////////////////////////////
    /////////////////// Listener //////////////////////
    //////////////////////////////////////////////////

    private class BusStopOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            CallConApiBusStop();
        }
    }//End of BusStopOnRefreshListener


    private class lvBusStopOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            swipe_BusStopList.setEnabled(firstVisibleItem == 0);
        }
    }

    ///////////////////////////////////////////////////
    /////////////////// inner Class //////////////////
    //////////////////////////////////////////////////
    private class ListCallback implements Callback<List<BusStopItemCollectionDao>> {
        @Override
        public void onResponse(Call<List<BusStopItemCollectionDao>> call, Response<List<BusStopItemCollectionDao>> response) {
            if (response.isSuccessful()) {
                tv_con_txt.setVisibility(View.GONE);
                swipe_BusStopList.setRefreshing(false);
                dao = response.body();
                tv_bus_route.setText(dao.get(0).getBusLine() + " หมายเลขรถที่ " + busId);
                tv_busStop_name.setText(dao.get(0).getStopName());
                busStopListAdapter.setData(dao);
                lv_BusStop.setAdapter(busStopListAdapter);

                showNotification(dao.get(0).getStopId().toString()
                        ,dao.get(0).getStopName()
                        , dao.get(0).getPredictTime());

            } else {

                tv_busStop_name.setText("ติดต่อข้อมูลไม่ได้");
                swipe_BusStopList.setRefreshing(false);
            }

        }

        @Override
        public void onFailure(Call<List<BusStopItemCollectionDao>> call, Throwable t) {
            swipe_BusStopList.setRefreshing(false);
            tv_busStop_name.setText("ติดต่อข้อมูลไม่ได้");
            tv_con_txt.setVisibility(View.VISIBLE);
        }
    }//End of ListCallback


}// End of BusFragment
