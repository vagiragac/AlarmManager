package com.example.amama15.alarmmanagerex;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ListView listView = (ListView) findViewById(R.id.listview2);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String data = preferences.getString("notification_text","");
            List<String> datas = new ArrayList<>();
            for (String d : data.split("\n")){
                datas.add(d);
            }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>
                 (this, android.R.layout.simple_selectable_list_item, android.R.id.text1, datas);
        listView.setAdapter(arrayAdapter);
    }
}
