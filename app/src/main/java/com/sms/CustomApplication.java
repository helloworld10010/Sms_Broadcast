package com.sms;

import android.app.Application;

import me.pqpo.librarylog4a.Log4a;

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化log4a
        LogInit.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log4a.release();
    }
}
