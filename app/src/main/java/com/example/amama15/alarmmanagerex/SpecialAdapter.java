package com.example.amama15.alarmmanagerex;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by amama15 on 7.06.2017.
 */

public class SpecialAdapter extends BaseAdapter {
    public LayoutInflater layoutInflater;
    public List<Alarms> alarmsList;
    public Context context;

    public SpecialAdapter(Activity activity, List<Alarms> alarmsList){
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.alarmsList = alarmsList;
        context = activity.getApplicationContext();
    }

    @Override
    public int getCount() {
        return alarmsList.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public SQLiteDatabaseHelper openHelper;
    public SQLiteDatabase database;
    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        rowView = layoutInflater.inflate(R.layout.listviewlayout,null);
        final TextView textView = (TextView) rowView.findViewById(R.id.textView);
        Alarms alarms = alarmsList.get(position);
        textView.setText(alarms.toString());
        return rowView;
    }

    public void cancelAlarm(int alarmId){
        openHelper = new SQLiteDatabaseHelper(context);
        database = openHelper.getWritableDatabase(); // Database open
        Intent intent = new Intent(context,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,alarmId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context, "Alarm iptal edildi !", Toast.LENGTH_SHORT).show();

        database.execSQL("delete from alarm where id=" + alarmId);
        if (database != null){  //Database close
            database.close();
        }
    }
}
