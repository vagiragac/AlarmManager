package com.example.amama15.alarmmanagerex;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public SQLiteDatabaseHelper openHelper;
    public SQLiteDatabase database;
    public ListView listView;
    public List<Alarms> list;
    public SpecialAdapter arrayAdapter;
    public String selectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.openHelper = new SQLiteDatabaseHelper(getApplicationContext());
        listView = (ListView) findViewById(R.id.listview);
        yenile();
        registerForContextMenu(listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.size() != 0) {
                    selectedItem = list.get(position).getId() + "";
                }
                return false;
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,v.getId(),0,"Sil");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Sil"){
            cancelAlarm(Integer.parseInt(selectedItem));
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sett) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_add){
            openTimePickerDialog(false);
        }
        else if (item.getItemId() == R.id.menu_log){
            Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void yenile(){
        list = readAlarms();
        arrayAdapter = new SpecialAdapter(MainActivity.this,list);
        listView.setAdapter(arrayAdapter);
    }

    public void openTimePickerDialog(boolean is24hour){
        TimePickerDialog timePickerDialog;
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(
                MainActivity.this,
                this.onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24hour);
        timePickerDialog.setTitle("Alarm Ayarla");
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            int getId = getLastId() + 1;
            for (int i = 0; i<3;i++){
                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();
                calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calSet.set(Calendar.MINUTE, minute + i);
                calSet.set(Calendar.SECOND, 0);
                calSet.set(Calendar.MILLISECOND, 0);
                if (calSet.compareTo(calNow) <= 0){
                    calSet.add(Calendar.DATE, 1);
                }
                setAlarm(calSet, getId + i);
            }
        }
    };
    public void setAlarm(Calendar alarmCalender, int alarmId){
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmId, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalender.getTimeInMillis(), pendingIntent);
        Toast.makeText(getApplicationContext(),"Alarm Ayarlandı!",Toast.LENGTH_SHORT).show();
        open();
        database.execSQL("insert into alarm(hour,minute,checked) values("+alarmCalender.getTime().getHours() +
                ","+alarmCalender.getTime().getMinutes()+"," + 1 + ")");
        close();
        yenile();
    }
    public void cancelAlarm(int alarmId){
        Intent intent = new Intent(getBaseContext(),AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmId, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(getApplicationContext().ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm iptal edildi !", Toast.LENGTH_SHORT).show();
        open();
        database.execSQL("delete from alarm where id=" + alarmId);
        close();
        yenile();
    }
    public void open(){
        this.database = openHelper.getWritableDatabase();
    }
    public void close(){
        if (database != null){
            database.close();
        }
    }
    public List<Alarms> readAlarms(){
        open();
        List<Alarms> alarmsList = new ArrayList<>();
        Cursor cursor = database.rawQuery("select * from alarm where checked=1",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            alarmsList.add(new Alarms(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3)));
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return alarmsList;
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
}