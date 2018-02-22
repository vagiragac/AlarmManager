package com.example.amama15.alarmmanagerex;

/**
 * Created by amama15 on 30.05.2017.
 */

public class Alarms {

    int id;
    int hour;
    int minute;
    int checked;

    public Alarms(int id, int hour, int minute, int checked) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "\n" + getId() + "\t\t\t\t\t" + getHour() + "  :  " + getMinute();
    }
}
