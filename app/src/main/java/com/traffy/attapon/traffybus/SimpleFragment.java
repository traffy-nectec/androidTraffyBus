package com.traffy.attapon.traffybus;

/**
 * Created by Attapon on 2/5/2559.
 */


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SimpleFragment extends Fragment {

    private int position = 0;

    //////////// header //////////////////
    private ListView lvHeader;
    private String[] man = {"กำลังค้นหาป้าย"};
    private TextView tv_con_txt;
    private TextView headerValue;
    private TextView inOut;
    private View header;
    private String route;
    private String stopName;

    ///////////// detect internet //////////////
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    //////////// get json ////////////////
    private String jsonTxt;
    private String jBuslists;
    private String buslistsText;
    private String jsonUrl = "http://cloud.traffy.in.th/attapon/API/private_apis/android_resource/arrival_time_next.php?stop_id=3140";


    ///////////// set json ///////////////
    private JSONObject json = null;
    private ListView lvBuslist;

    //////////// get gps //////////////

    private String locaText;


    ////////////// image from url ///////////////////////
    private String urlImage = "http://cloud.traffy.in.th/attapon/API/private_apis/android_resource/traffylogo.png";

    ///////////// Database ////////////////////
    SQLiteDatabase mDb;
    MyDbHelper mHelper;
    Cursor mCursor;

    //////////// ohter case //////////////
    private ImageView imNetConnect;
    private boolean netImage = true;
    private int timeCircle = 10000;
    private boolean outOfService = true;
    private TextView tv_no_update;
    private TextView tv_bg;
    private Thread t;
    private TextView tv_stop_name;




    //////////// Swipe To Refresh //////////
    SwipeRefreshLayout mSwipeRefreshLayout;
    private SwitchCompat swt_noti;
    private SharedPre sharedPre;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        SharedPre sharedPre = new SharedPre(getActivity());
        sharedPre.reSetSharedPreAlertNoti();
        sharedPre.reSetSharedPre();
        sharedPre = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        position = bundle.getInt(SimplePagerAdapter.ARGS_POSITION);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_main, container, false);

        lvHeader = (ListView) rootView.findViewById(R.id.lv_header);
        lvBuslist = (ListView) rootView.findViewById(R.id.lv_BusList);
        imNetConnect = (ImageView) rootView.findViewById(R.id.im_net_connect);
        tv_con_txt = (TextView) rootView.findViewById(R.id.tv_con_txt);
        tv_no_update = (TextView) rootView.findViewById(R.id.tv_no_update);
        final ImageView imgTraffyLogo = (ImageView) rootView.findViewById(R.id.im_traffylogo);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.main_swipe);
        tv_stop_name = (TextView) rootView.findViewById(R.id.lv_stop_name);
        swt_noti = (SwitchCompat) rootView.findViewById(R.id.swt_noti);
        //   tv_bg = (TextView)rootView.findViewById(R.id.tv_bg);

        header = inflater.inflate(R.layout.header_layout, null, false);
        headerValue = (TextView) header.findViewById(R.id.tv_stop_name);
        inOut = (TextView) header.findViewById(R.id.tv_bus_route);


        cd = new ConnectionDetector(getActivity());



        mHelper = new MyDbHelper(getActivity());
        mDb = mHelper.getWritableDatabase();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.Red, R.color.Green, R.color.Blue);

        tv_no_update.setVisibility(View.INVISIBLE);
        tv_con_txt.setVisibility(View.INVISIBLE);
        imNetConnect.setVisibility(View.INVISIBLE);


        if (position == 1) swt_noti.setVisibility(View.GONE);
        sharedPre = new SharedPre(getActivity());
        sharedPre.setNoti(false);
        swt_noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    sharedPre.setNoti(true);
                    swt_noti.setText("ปิดการแจ้งเตือน");
                } else {
                    sharedPre.setNoti(false);
                    swt_noti.setText("เปิดการแจ้งเตือน");
                }

            }
        });

        // getIntentPage();

        isInternetPresent = cd.isConnectingToInternet();
        //   jHaHa = jsonHaHa();
        //   showToast(jHaHa);
        showHeader();
        findGpsAndShowData();

        ////////////// time circle /////////////////////
        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(timeCircle);
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // update here!
                                    findGpsAndShowData();
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

        t.start();


        ///////////////////////// swip to refresh //////////////////////
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findGpsAndShowData();
                showToast("อัพเดทแล้ว");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        ///////////////// check listview scroll ///////////////////////////////////
        lvBuslist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    mSwipeRefreshLayout.setEnabled(true);
                else
                    mSwipeRefreshLayout.setEnabled(false);
            }
        });

        ////////////////////// logo click ///////////////////////////
        imgTraffyLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openURLS();

            }
        });

        return rootView;
    }

    private void getIntentPage() {
        try {
            SharedPre sharedPre = new SharedPre(getActivity());
            Bundle bundles = getActivity().getIntent().getExtras();
            int pageIntent = bundles.getInt("pageIntent");
            if (pageIntent == 0) {
                sharedPre.setPage(0);
            }
        } catch (NullPointerException e) {

        }

    }

    private class setNormal extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
        }

        protected String doInBackground(String... urls) {
            String result = "";
            try {

                HttpGet httpGet = new HttpGet(urls[0]);
                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(httpGet);

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    InputStream inputStream = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader
                            (new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }
                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return result;
        }

        protected void onPostExecute(String jsonString) {
            // Dismiss ProgressBar
            jsonTxt = jsonString;


            buslistsText = getBuslistText(jsonTxt);
            //   showToast(buslistsText);
            //   jsonTxt = "[]";

            if (jsonTxt == "" || jsonTxt == null) {
                //   showToastShort("รับ json ไม่ได้");
            }

            //else if(jsonTxt.equals("[]")  || buslistsText.equals("[]"))
            else if (jsonTxt == "[]" || buslistsText == "[]") {
                netImage = false;
                imNetConnect.setVisibility(View.INVISIBLE);
                tv_con_txt.setVisibility(View.INVISIBLE);
                //jsonUrl = "http://cloud.traffy.in.th/attapon/API/private_apis/android_resource/arrival_time_next.php?stop_id=3140";
                String lastStop = DB();
                jsonUrl = "http://cloud.traffy.in.th/attapon/API/private_apis/android_resource/arrival_time_next.php?stop_id=" + lastStop;
                new setDefault().execute(jsonUrl);

            } else {
                //   showToastShort("รับ json ได้");
                netImage = false;
                imNetConnect.setVisibility(View.INVISIBLE);
                tv_con_txt.setVisibility(View.INVISIBLE);

                jsonToSetHeader(jsonTxt);
                jsonToBuslist(jsonTxt);
            }

        }
    }

    private class setDefault extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
        }

        protected String doInBackground(String... urls) {
            String result = "";
            try {

                HttpGet httpGet = new HttpGet(urls[0]);
                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(httpGet);

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    InputStream inputStream = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader
                            (new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }
                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return result;
        }

        protected void onPostExecute(String jsonString) {
            // Dismiss ProgressBar
            jsonTxt = jsonString;

            //    showToastShort("รับ json ป้าย Default ได้");

            jsonToSetHeader(jsonTxt);
            jsonToBuslist(jsonTxt);

            if (outOfService) {
                showToast("คุณไม่อยู่ในเขตบริการของสาย 73ก");
                outOfService = false;
            }

            netImage = false;
            imNetConnect.setVisibility(View.INVISIBLE);
            tv_con_txt.setVisibility(View.INVISIBLE);
        }

    }

    private void showToast(String txt) {
        try {
            Toast.makeText(getActivity(), txt, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void showToastShort(String txt) {
        Toast toast = Toast.makeText(getActivity(), txt, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void showHeader() {
        setSizHeader("กำลังค้นหาป้าย", "สาย 73ก สะพานพุทธ-อู่ส่วนสยาม");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.header_layout, R.id.tv_stop_name, man);
        lvHeader.setAdapter(dataAdapter);
    }


    private void jsonToSetHeader(String jsonTxt) {
        ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();

        try {
            JSONArray jArray = new JSONArray(jsonTxt);
            json = jArray.getJSONObject(position);


            HashMap<String, String> map;

            map = new HashMap<String, String>();

            if (json.getString("status").equals("inbound")) {
                route = "สาย 73ก อู่ส่วนสยาม-สะพานพุทธ มุ่งหน้า";
                stopName = json.getString("stop_name");
                map.put("bus_route", "สาย 73ก อู่ส่วนสยาม-สะพานพุทธ มุ่งหน้า");
                map.put("stop_name", json.getString("stop_name"));
                MyArrList.add(map);
            } else if (json.getString("status").equals("outbound")) {
                route = "สาย 73ก สะพานพุทธ-อู่ส่วนสยาม มุ่งหน้า";
                stopName = json.getString("stop_name");
                map.put("bus_route", "สาย 73ก สะพานพุทธ-อู่ส่วนสยาม มุ่งหน้า");
                map.put("stop_name", json.getString("stop_name"));
                MyArrList.add(map);
            } else {
                route = "สาย 73ก สะพานพุทธ-อู่ส่วนสยาม มุ่งหน้า";
                stopName = "asdf";
                map.put("bus_route", "สาย 73ก สะพานพุทธ-อู่ส่วนสยาม มุ่งหน้า");
                map.put("stop_name", "ไม่พบป้ายตรงข้าม");
                MyArrList.add(map);
            }


            try {

                SimpleAdapter sAdap;

                sAdap = new SimpleAdapter(getActivity(), MyArrList, R.layout.header_layout
                        , new String[]{"bus_route", "stop_name"}
                        , new int[]{R.id.tv_bus_route, R.id.tv_stop_name});
                //  setSizHeader("สนามกีฬาแห่งชาติ", route);
                lvHeader.setAdapter(sAdap);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            setSizHeader(stopName, route);


            if (position == 0) {
                updateData(json.getString("stop_id"));
                //  showToastShort("update in db "+position);
            }


        } catch (JSONException e) {
            e.printStackTrace();

            SimpleAdapter sAdap;
            sAdap = new SimpleAdapter(getActivity(), MyArrList, R.layout.header_layout, new String[]{"bus_route", "stop_name"}, new int[]{R.id.tv_bus_route, R.id.tv_stop_name});
            setSizHeader("ไม่พบป้ายตรงข้าม", "สาย 73ก สะพานพุทธ-อู่ส่วนสยาม");
            lvHeader.setAdapter(sAdap);
        }
    }

    private void jsonToBuslist(String jsonTxt) {
        ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
        SharedPre sharedPre = new SharedPre(getActivity());

        try {
            JSONArray jArray = new JSONArray(jsonTxt);
            json = jArray.getJSONObject(position);

            jBuslists = json.getString("buslists");

            JSONArray jArray2 = new JSONArray(jBuslists);


            HashMap<String, String> map;

            for (int i = 0; i < jArray2.length(); i++) {

                JSONObject c = jArray2.getJSONObject(i);
                map = new HashMap<String, String>();
                if (c.getString("predict_time").equals("NA") || c.getString("predict_status").equals("gps_delay")) {

                } else {
                   //  Log.d("position", sharedPre.getPage() + " shared " + swt_noti.isChecked() + " - " + sharedPre.getNoti());
                    if (position == sharedPre.getPage() && sharedPre.getNoti()) {

                      //   Log.d("position ------", "" + sharedPre.getAlertNoti(c.getString("bmta_id")));

                        if (sharedPre.getAlertNoti(c.getString("bmta_id")) == null
                                && Integer.parseInt(c.getString("predict_time")) <= 10) {

                            sharedPre.setAlertNoti(c.getString("bmta_id"));
                            Log.d("position", "" + position + " " + c.getString("bmta_id"));

                            showNotification(c.getString("bmta_id")
                                    , c.getString("predict_time"));
                        }


                    }
                    map.put("bmta_id", c.getString("bmta_id") + " จาก");
                    map.put("predict_time", c.getString("predict_time"));
                    map.put("current_stop_name", c.getString("current_stop_name"));
                    map.put("number_of_nexts", "[" + c.getString("number_of_nexts") + " ป้าย]");

                    MyArrList.add(map);
                }
            }

            if (MyArrList.toString().equals("[]")) {
                tv_no_update.setVisibility(View.VISIBLE);
                lvBuslist.setVisibility(View.INVISIBLE);
                //   showToastShort("no buslist");
            } else {
                tv_no_update.setVisibility(View.INVISIBLE);
                lvBuslist.setVisibility(View.VISIBLE);

                try {
                    SimpleAdapter sAdap;
                    sAdap = new SimpleAdapter(getActivity(), MyArrList, R.layout.listview_layout
                            , new String[]{"bmta_id", "predict_time", "current_stop_name", "number_of_nexts"}
                            , new int[]{R.id.lv_bmta_id, R.id.lv_predict_time, R.id.lv_stop_name, R.id.lv_previous_stop});
                    lvBuslist.setAdapter(sAdap);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();

            tv_no_update.setVisibility(View.INVISIBLE);
            SimpleAdapter sAdap;
            sAdap = new SimpleAdapter(getActivity(), MyArrList, R.layout.listview_layout, new String[]{"bmta_id", "predict_time", "current_stop_name", "number_of_nexts"}, new int[]{R.id.lv_bmta_id, R.id.lv_predict_time, R.id.lv_stop_name, R.id.lv_previous_stop});
            lvBuslist.setAdapter(sAdap);
        }
    }

    private String getBuslistText(String jsonTxt) {
        if (jsonTxt == "" || jsonTxt == null) {

        } else {
            try {
                JSONArray jArray = new JSONArray(jsonTxt);
                json = jArray.getJSONObject(position);
                jBuslists = json.getString("buslists");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jBuslists;
    }

    public Drawable getResource(String url) throws MalformedURLException, IOException {
        return Drawable.createFromStream((InputStream) new URL(url).getContent(), "src");
    }

    private void setImageURLS(ImageView imgShow, String urlImage) {
        try {

            imgShow.setImageDrawable(getResource(urlImage));

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void openURLS() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://docs.google.com/forms/d/17UmjxZR-ZORNFjCxAGGLfbInpfG-EfpoehYZomOVY6w/viewform"));
        startActivity(intent);
    }


    private void findGpsAndShowData() {
        isInternetPresent = cd.isConnectingToInternet();
         MainActivity mainAT = (MainActivity) getActivity();
        locaText = mainAT.txtGPS();
        //    showToastShort(locaText);

        if (isInternetPresent == false) {
            //  showToastShort("no connection");
            if (netImage) {
                imNetConnect.setVisibility(View.VISIBLE);
                netImage = false;
            }

            tv_con_txt.setVisibility(View.VISIBLE);
        } else {
            //   showToastShort("connected");
            if (locaText == null) {
                //timeCircle = 10000;
                //    showToastShort("รับ gps ไม่ได้");
                //  jsonUrl = "http://cloud.traffy.in.th/attapon/API/private_apis/android_resource/arrival_time_next.php?stop_id=3140";
                //   new setDefault().execute();
                String lastStop = DB();
                jsonUrl = "http://cloud.traffy.in.th/attapon/API/private_apis/android_resource/arrival_time_next.php?stop_id=" + lastStop;
                new setDefault().execute(jsonUrl);
            } else {
                // showToastShort("รับ gps ได้");
               // timeCircle = 10000;

Log.d("url", "findGpsAndShowData: "+locaText);
                jsonUrl = "http://cloud.traffy.in.th/attapon/API/private_apis/android_resource/arrival_time_next.php?" + locaText + "&radius=100000";
                locaText = null;
                new setNormal().execute(jsonUrl);
            }
        }

        jsonTxt = "";

    }


    private void setSizHeader(String txt, String route) {

        inOut.setText(route);
        headerValue.setText(txt);
        lvHeader.addHeaderView(header);

    }

    private String DB() {
        mCursor = mDb.rawQuery("SELECT " + MyDbHelper.COL_STOP_ID + " FROM " + MyDbHelper.TABLE_NAME, null);
        ArrayList<String> dirArray = new ArrayList<String>();
        mCursor.moveToFirst();

        //showToastShort(mCursor.getString(mCursor.getColumnIndex(MyDbHelper.COL_NAME)));
        return mCursor.getString(mCursor.getColumnIndex(MyDbHelper.COL_STOP_ID));
    }

    private void updateData(String stopID) {
        mDb.execSQL("update " + MyDbHelper.TABLE_NAME + " set " + MyDbHelper.COL_STOP_ID + " = '" + stopID + "' where _id= '1'");
    }

    public void showNotification(String bmta_id, String predict_time) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int intbmta_id = Integer.parseInt(bmta_id.replace("-", ""));

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("pageIntent", 0);
        PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);



            Notification mNotification = new Notification.Builder(getActivity())
                    .setContentTitle("รถหมายเลข " + bmta_id)
                    .setContentText("จะถึงในอีก " + predict_time + " นาที  ")
                    .setSmallIcon(R.drawable.bus_icon)
                    .setContentIntent(pIntent)
                    .setSound(soundUri)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getActivity()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(intbmta_id, mNotification);





    }


}
