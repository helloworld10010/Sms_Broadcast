package com.sms;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.TimeZone;

import me.pqpo.librarylog4a.Log4a;

public class CustomApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,2018);
        calendar.set(Calendar.MONTH,Calendar.AUGUST);
        calendar.set(Calendar.DAY_OF_MONTH,29);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.setTimeZone(TimeZone.getDefault());
        setAlarm(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,1,fun.RESET_SMS_COUNT);

    }

    private void setAlarm(int type, long triggerAtMillis, long intervalMillis, int requestCode, String action) {
            AlarmManager  alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(type, triggerAtMillis, intervalMillis, PendingIntent.getBroadcast(this,
                    requestCode,new Intent(action), PendingIntent.FLAG_UPDATE_CURRENT));
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log4a.release();
    }
}
