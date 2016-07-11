package com.traffy.attapon.traffybus;

/**
 * Created by Attapon on 3/5/2559.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MyDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "TRANSIT";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "Stop";
    public static final String COL_STOP_ID = "stopid";


    public MyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME +" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_STOP_ID + " TEXT );");

        db.execSQL("INSERT INTO " + TABLE_NAME + " (" + COL_STOP_ID + ") VALUES ('3140');");

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

public class Database { }
