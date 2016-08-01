package com.traffy.attapon.traffybus.util;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreNoTiBus {

    //////////////////// var //////////////////////
    Context context;
    SharedPreferences sharedPerfsNoTiBus;
    SharedPreferences.Editor editorNoTiBus;
    static String perfsNameNotiBus = "NotiBus";
    /////////////////////////////////////////////


    public SharedPreNoTiBus(Context context) {
        this.context = context;
        try {
            this.sharedPerfsNoTiBus = this.context.getSharedPreferences(perfsNameNotiBus, 0);
            this.editorNoTiBus = sharedPerfsNoTiBus.edit();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }//End of constructor

    public void resetSharedPreNoTiBus() {
        editorNoTiBus.clear();
        editorNoTiBus.commit();
    }

    public void setNotiBus(String busStopId) {
        editorNoTiBus.putString("busStopId", busStopId);
        editorNoTiBus.commit();
    }

    public String getNotiBus() {
        return sharedPerfsNoTiBus.getString("busStopId", "busStopId");
    }


}//End of SharedPreNotiBus