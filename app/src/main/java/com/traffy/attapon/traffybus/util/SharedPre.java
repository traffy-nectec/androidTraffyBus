package com.traffy.attapon.traffybus.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 13/7/59.
 */
public class SharedPre {
    Context context;
    SharedPreferences sharedPerfs;
    SharedPreferences.Editor editor;
    static String perfsName = "UserHelper";

    SharedPreferences sharedPerfs2;
    SharedPreferences.Editor editor2;
    static String perfsName2 = "UserHelper";


    public SharedPre(Context context) {
        this.context = context;

        try {
            this.sharedPerfs = this.context.getSharedPreferences(perfsName, 0);
            this.editor = sharedPerfs.edit();
            this.sharedPerfs2 = this.context.getSharedPreferences(perfsName2, 0);
            this.editor2 = sharedPerfs.edit();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public int getPage() {
        return sharedPerfs.getInt("page", 0);
    }

    public void setPage(int page) {

        editor.putInt("page", page);

        editor.commit();
    }

    public void reSetSharedPre() {
        editor.clear();
        editor.commit();
    }

    public void reSetSharedPreAlertNoti() {
        editor2.clear();
        editor2.commit();
    }

    public void setNoti(boolean noTi) {
        editor.putBoolean("noTi", noTi);
        editor.commit();
    }

    public boolean getNoti() {

        return sharedPerfs.getBoolean("noTi", false);
    }

    public void setAlertNoti(String idAlertNoti) {
        editor2.putString(idAlertNoti, idAlertNoti);
        editor2.commit();
    }

    public String getAlertNoti(String idAlertNoti) {

        return sharedPerfs2.getString(idAlertNoti, null);
    }


}
