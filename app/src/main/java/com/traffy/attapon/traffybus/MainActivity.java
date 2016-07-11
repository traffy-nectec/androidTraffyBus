package com.traffy.attapon.traffybus;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.widget.SwipeRefreshLayout;

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

public class MainActivity extends AppCompatActivity {

    ///////////// Database ////////////////////
    SQLiteDatabase mDb;
    MyDbHelper mHelper;

    private ViewPager viewPager;

    //////////// get gps //////////////
    private String location = null;
    private String locaText;
    private double lat;
    private double lng;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        viewPager = (ViewPager) findViewById(R.id.pager);

        mHelper = new MyDbHelper(this);
        mDb = mHelper.getWritableDatabase();

        PagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        getGPS();

    }

    private void getGPS()
    {
        ///////////////////////////// get current location /////////////////////////////
        //จัดการ Event ต่างๆ ของ Location
        LocationListener locationListener = new LocationListener(){

            //เมื่อหาตำแหน่งได้ จะทำงานที่ function นี้
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub

                //รับค่า Latitude และ Longitude จาก location
                lat = location.getLatitude();
                lng = location.getLongitude();

                locaText = "lat=" + Double.toString(lat) +"&lng=" + Double.toString(lng);
                //    gpsTextView.setText(locationText);

            }

            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub

            }
        };

        //สร้าง locationManager เพื่อใช้ในการรับตำแหน่ง
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //ระบุรูปแบบของตำแหน่งที่เราต้องการ และเชื่อมกับ locationListener
        //LocationManager.GPS_PROVIDER รับตำแหน่งจาก GPS
        //LocationManager.NETWORK_PROVIDER รับตำแหน่งจาก Network พวก Wifi EDGE GPRS 3G
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        //   return locaText;

        ///////////////////////////// get current location /////////////////////////////
    }

    public String txtGPS()
    {
        return locaText;
    }
}
