package com.traffy.attapon.traffybus.activity;


import android.Manifest;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.traffy.attapon.traffybus.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.traffy.attapon.traffybus.adapter.Bus;
import com.traffy.attapon.traffybus.adapter.routAdapter;
import com.traffy.attapon.traffybus.util.MyDbHelper;
import com.traffy.attapon.traffybus.util.SendUserInfo;
import com.traffy.attapon.traffybus.util.SharedPre;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    ///////////// Database ////////////////////
    SQLiteDatabase mDb;
    MyDbHelper mHelper;
    SwipeRefreshLayout refresh;
    ListView route;
    ImageView traffyLogo;
    //////////// get gps //////////////
    public static String locaText = null;
    private GoogleApiClient googleApiClient;
    private LocationAvailability locationAvailability;
    private static final int REQUEST_LOCATION = 0;
    private LocationRequest locationRequest;
    public static boolean sendInfo = true;
    private long referenceTime = 0;
    private String lat, lng;
    private int sleepTime = 10000;
    Thread t;
    String fakelat = "13.782711";
    String fakelng = "100.615303";
    private SensorManager manager;
    private Sensor accelerometer;
    Accelerometer accel;
    ArrayList<String> buffer;
    ArrayList<String> accelList;
    private int interval = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        SharedPre sharedPre = new SharedPre(this);
        sharedPre.setPage(0);
       initInstance();



}

    private void initInstance() {
        refresh = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);////refresh bar
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fakelat = "13.804464";
                fakelng = "100.576620";
                initInstance();
            }
        });

        if (isOnline()) {
            callFunction();
        } else {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("คุณไม่ได้เปิดอินเทอร์เน็ต?");
            builder.setPositiveButton("เปิด", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                    refresh.setRefreshing(false);
                }
            });
            builder.setNegativeButton("ปิด", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    refresh.setRefreshing(false);

                }
            });
            builder.show();
        }
    }

    private void callFunction() {
        buffer = new ArrayList<>();
        accelList = new ArrayList<>();

        traffyLogo = (ImageView) findViewById(R.id.logo);


        ////////////////////////initialize accel
        manager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accel = new Accelerometer();
        manager.registerListener(accel, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        t = new Thread(new Runnable() {//sendInfo Thread
            @Override
            public void run() {
                int i = 10;
                while (true) {
                    if (i == 10) {
                        Log.d("TimeReference", String.valueOf(System.currentTimeMillis() - referenceTime));
                        referenceTime = System.currentTimeMillis();
                        Log.d("Execute", "sending user info");

                        buffer.addAll(accelList);
                        accelList.clear();
                        SendUserInfo();
                        i = 0;
                    }
                    Log.d("Accel", accel.getAccel());
                    accelList.add(accel.getAccel());
                    try {
                        Thread.sleep(interval);
                        i++;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        buildGoogleApiClient();
        googleApiClient.connect();
        mHelper = new MyDbHelper(this);
        mDb = mHelper.getWritableDatabase();

        route = (ListView) findViewById(R.id.route);

        addBus();///add bus from JSON.tr
        // refresh.setRefreshi ng(true);
        List<Bus> Buslist = new ArrayList<Bus>();

        ListAdapter adapter = new routAdapter(this, Buslist);

        route.setAdapter(adapter);

        route.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MainActivityOld.class);
                Bus bus = (Bus) parent.getItemAtPosition(position);

                intent.putExtra("bus_line", bus.getLine());
                startActivity(intent);
            }
        });
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //sendInfo = true;
        Log.d("position", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
    /*    if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }*/
        Log.d("position", "onStop");
        //   sendInfo = false;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocationConnect();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        lat = String.valueOf(location.getLatitude());
        lng = String.valueOf(location.getLongitude());

        Log.d("Location", "lat=" + lat + " lng " + lng);
        locaText = "lat=" + lat + "&lng=" + lng;
        if (t.getState() == Thread.State.NEW) {
            t.start();
        }

    }

    private void getLocationConnect() {
        //check runtime permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                if
                        (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel("อนุญาตให้ App Traffy Bus เข้าถึงตำแหน่งปัจจุบันของคุณ",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        requestPermissions(new
                                                        String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_LOCATION);
                                    }
                                }
                            });
                    return;
                }
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            locationAvailability =
                    LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);

            locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, this);

            if (Build.VERSION.SDK_INT >= 23) {
                if (!locationAvailability.isLocationAvailable()) {
                    Toast.makeText(MainActivity.this, "คุณไม่ได้เปิด GPS", Toast.LENGTH_SHORT).show();
                }
            } else {
                LocationManager manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(MainActivity.this, "คุณไม่ได้เปิด GPS", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener
            onClickListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("ตกลง", onClickListener)
                .setNegativeButton("ยกเลิก", null)
                .create()
                .show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationConnect();
            } else {
                // Permission was denied or request was cancelled
            }
        }
    }

    public void SendUserInfo() {
        String dateTime, MacAddress;

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);//get MacAddress
        WifiInfo info = wifi.getConnectionInfo();
        MacAddress = info.getMacAddress();

        Date currentDateTime = new Date(); //get date and time2
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateTime = format.format(currentDateTime);//convert login date and time to String

        //Execute POST
        SendUserInfo user = new SendUserInfo(dateTime, MacAddress, lat, lng, this, buffer);
        user.execute("http://cloud.traffy.in.th/attapon/API/get_location/get_user_location.php");

    }

    private void addBus() {

        getBusJSon getBus = new getBusJSon();
        //get Bus line from station lat lng
        // getBus.execute("http://cloud.traffy.in.th/attapon/API/private_apis/get_bus_line_on_service.php?lat="+fakelat+"&lng="+fakelng+"&radius=500");
        getBus.execute("http://cloud.traffy.in.th/attapon/API/private_apis/get_bus_line_on_service.php?lat=" + fakelat + "&lng=" + fakelng + "&radius=100000");

    }

    public void openURLS(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://docs.google.com/forms/d/17UmjxZR-ZORNFjCxAGGLfbInpfG-EfpoehYZomOVY6w/viewform"));
        startActivity(intent);
    }

private class getBusJSon extends AsyncTask<String, Void, String> {

    List<Bus> bus;

    HttpURLConnection connection;

    @Override
    protected String doInBackground(String... params) {
        try {
            Log.d("Updateing...", "start update");
            bus = new ArrayList<Bus>();
            URL u = new URL(params[0]);
            connection = (HttpURLConnection) u.openConnection();
            connection.getDoInput();
            StringBuffer buff = new StringBuffer();
            String JSontxt = "";
            connection.connect();
            int responseCode = connection.getResponseCode();//connect to web
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((JSontxt = reader.readLine()) != null) {
                    buff.append(JSontxt);
                }
                reader.close();
            }
            JSONArray JArray = new JSONArray(buff.toString());
            Log.d("JSONCheck", JArray.toString());
            for (int i = 0; i < JArray.length(); i++)///get JSON data
            {
                JSONObject JObject = JArray.getJSONObject(i);
                String Bus_Line = JObject.getString("bus_line");
                String Bus_Name = JObject.getString("bus_name");
                String start = JObject.getString("start");
                String end = JObject.getString("end");
                bus.add(new Bus(Bus_Name, Bus_Line, start, end));
            }
            final ListAdapter adapter = new routAdapter(MainActivity.this, bus);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    route.setAdapter(adapter);
                    if (refresh.isRefreshing())//stop refreshing animtion
                    {
                        refresh.setRefreshing(false);
                        Toast.makeText(MainActivity.this, "อัพเดทแล้ว", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "ติดต่อระบบไม่ได้", Toast.LENGTH_SHORT);
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
            Log.d("Updateing...", "finish update");
        }
        return null;
    }

}

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
