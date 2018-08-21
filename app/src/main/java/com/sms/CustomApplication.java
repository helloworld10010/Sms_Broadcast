package com.sms;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import me.pqpo.librarylog4a.Log4a;

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化静态变量
        SharedPreferences sharedPreferences = getSharedPreferences("sms", Context.MODE_PRIVATE);
        fun.openLog = sharedPreferences.getBoolean("open_log",true);
        // 初始化log4a
        LogInit.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log4a.release();
    }
}
