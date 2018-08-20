package com.sms;

import android.content.Context;

import java.io.File;

public class FileUtils {

    public static File getLogDir(Context context) {
        //    /storage/emulated/0/Android/data/me.pqpo.log4a/files/log
        // 如果File对象不存在，先new，然后创建对应的文件夹
        File log = context.getExternalFilesDir("logs");
        if (log == null) {
            log = new File(context.getFilesDir(), "logs");
        }
        if (!log.exists()) {
            log.mkdir();
        }
        return log;
    }

}