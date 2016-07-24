package com.traffy.attapon.traffybus.activity;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.traffy.attapon.traffybus.util.MyDbHelper;
import com.traffy.attapon.traffybus.R;
import com.traffy.attapon.traffybus.util.SharedPre;
import com.traffy.attapon.traffybus.adapter.SimplePagerAdapter;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    ///////////// Database ////////////////////
    SQLiteDatabase mDb;
    MyDbHelper mHelper;

    private ViewPager viewPager;

    //////////// get gps //////////////
    private String locaText = null;
    private SharedPre sharedPre;
    private SwitchCompat swt_noti;
    private GoogleApiClient googleApiClient;
    private LocationAvailability locationAvailability;
    private static final int REQUEST_LOCATION = 0;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        buildGoogleApiClient();

        viewPager = (ViewPager) findViewById(R.id.pager);
        swt_noti = (SwitchCompat) findViewById(R.id.swt_noti);


        mHelper = new MyDbHelper(this);
        mDb = mHelper.getWritableDatabase();

        sharedPre = new SharedPre(getApplication());
        sharedPre.setPage(0);


        PagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        sharedPre.setPage(0);
                        break;
                    case 1:
                        sharedPre.setPage(1);
                        break;
                    default:
                        sharedPre.setPage(0);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private  void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public String txtGPS() {
        return locaText;
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        Log.d("position", "onStop");

    }


    @Override
    protected void onDestroy() {
        Log.d("dd", "onDestroy");
        sharedPre.reSetSharedPreAlertNoti();
        sharedPre.reSetSharedPre();
        super.onDestroy();

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

        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());


        // Log.d("Location", "lat=" + lat + " lng " + lng);
        locaText = "lat=" + lat + "&lng=" + lng;


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
}
