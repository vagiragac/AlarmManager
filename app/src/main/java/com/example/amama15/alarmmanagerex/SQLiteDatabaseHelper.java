package com.example.amama15.alarmmanagerex;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by amama15 on 2.06.2017.
 */

public class SQLiteDatabaseHelper extends SQLiteAssetHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "alarms.db";
    public Context mContext;

    public SQLiteDatabaseHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        this.mContext = context;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,DB_VERSION,newVersion);
    }
}
