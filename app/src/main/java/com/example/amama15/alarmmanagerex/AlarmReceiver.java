package com.example.amama15.alarmmanagerex;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by amama15 on 30.05.2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean state_alarm = preferences.getBoolean("switch_alarm",true);
        Boolean alarm_sessiz = preferences.getBoolean("switch_sessiz",false);

        if (state_alarm) {
            Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context,RingtoneManager.TYPE_ALARM);
            final MediaPlayer mediaPlayer = MediaPlayer.create(context,uri);
            final AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (alarm_sessiz){
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            }else{
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamVolume(AudioManager.STREAM_MUSIC), 0);
            }
            mediaPlayer.start();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mediaPlayer.stop();
                    Log.e("Point_1", "Volume_after " + audio.getStreamVolume(AudioManager.STREAM_ALARM));
                }
            }, 10000);
        }else {
            Calendar c = Calendar.getInstance();
            String time = c.getTime().getHours() + " : " + c.getTime().getMinutes();
            Intent i = new Intent(context, Main2Activity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), i, 0);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle("Alarm Manager EX");
            mBuilder.setContentText("Merhaba saat " + time);
            mBuilder.setContentIntent(pIntent);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setAutoCancel(true).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0,mBuilder.build());

            String get = preferences.getString("notification_text","");
            SharedPreferences.Editor editor = preferences.edit();

            if (get == ""){
                get = "Merhaba saat " + time;
                editor.putString("notification_text",get);
            }
            else {
                get = get + "\nMerhaba saat " + time;
                editor.putString("notification_text",get);
            }
            editor.commit();
        }
        //Intent alarmIntent = new Intent("android.intent.action.MAIN");
        Intent alarmIntent = new Intent();
        alarmIntent.setClass(context, CustomDialog.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }
}
