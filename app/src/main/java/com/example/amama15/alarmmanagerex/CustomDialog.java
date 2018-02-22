package com.example.amama15.alarmmanagerex;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by amama15 on 10.06.2017.
 */

public class CustomDialog extends Activity{
    public AlertDialog mAlertDialog;
    public SQLiteDatabase database;
    public SQLiteDatabaseHelper dbhelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(view);
        Button btn_ok = (Button) view.findViewById(R.id.button_evet);
        Button btn_cancel = (Button) view.findViewById(R.id.button_hayir);
        btn_ok.setOnClickListener(onClickListener);
        btn_cancel.setOnClickListener(onClickListener);
        builder.setCancelable(false);
        builder.setInverseBackgroundForced(false);///BURAYA DIkKAT
        mAlertDialog = builder.create();
        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mAlertDialog.show();
        dbhelper = new SQLiteDatabaseHelper(getApplicationContext());
    }
    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.button_evet){
                mAlertDialog.cancel();
                Process.killProcess(Process.myPid());
                System.exit(0);
            }
            else if (v.getId() == R.id.button_hayir){
                mAlertDialog.cancel();

                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();
                calSet.set(Calendar.HOUR_OF_DAY, calNow.getTime().getHours());
                calSet.set(Calendar.MINUTE, calNow.getTime().getMinutes() + 5);
                calSet.set(Calendar.SECOND, 0);
                calSet.set(Calendar.MILLISECOND, 0);
                if (calSet.compareTo(calNow) <= 0){
                    calSet.add(Calendar.DATE, 1);
                }
                setAlarm(calSet,getLastId() + 1);
                Process.killProcess(Process.myPid());
                System.exit(0);
            }
        }
    };

    public void setAlarm(Calendar alarmCalender,int alarmId){
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmId, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalender.getTimeInMillis(), pendingIntent);
        Toast.makeText(getApplicationContext(),"Alarm Ayarlandı!",Toast.LENGTH_SHORT).show();
        open();
        database.execSQL("insert into alarm(hour,minute,checked) values("+alarmCalender.getTime().getHours() +
                ","+alarmCalender.getTime().getMinutes()+"," + 0 + ")");
        close();
    }

    public int getLastId(){
        open();
        Cursor cursor = database.rawQuery("SELECT * FROM alarm WHERE id=(SELECT MAX(id) FROM alarm)",null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0)
            return -1;
        else {
            int item = cursor.getInt(cursor.getColumnIndex("id"));
            Log.d("", "getLastId: " + item);
            return item;
        }
    }

    private void open() {
        database = dbhelper.getWritableDatabase();
    }

    private void close(){
        if (database!=null){
            database.close();
        }
    }
}
