package com.traffy.attapon.traffybus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.traffy.attapon.traffybus.R;
import com.traffy.attapon.traffybus.fragment.BusFragment;


public class BusActivity extends AppCompatActivity {

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);


        if (savedInstanceState == null) {

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                 name = bundle.getString("busId");

            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.bus_ContentContainer, BusFragment.newInstance(name))
                    .commit();
        }
    }
}
