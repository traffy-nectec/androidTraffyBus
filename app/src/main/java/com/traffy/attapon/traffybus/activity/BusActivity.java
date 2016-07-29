package com.traffy.attapon.traffybus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.traffy.attapon.traffybus.R;
import com.traffy.attapon.traffybus.fragment.BusFragment;


public class BusActivity extends AppCompatActivity {

    private String name;
    private Toolbar toolbar;
    private TextView tv_busStop_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

 /*       toolbar = (Toolbar) findViewById(R.id.toolBarBusStop);
        setSupportActionBar(toolbar);
        tv_busStop_name = (TextView) findViewById(R.id.tv_busStop_name);*/


        if (savedInstanceState == null) {

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                name = bundle.getString("busId");
              //  tv_busStop_name.setText(name);
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.bus_ContentContainer, BusFragment.newInstance(name))
                    .commit();
        }
    }

}
