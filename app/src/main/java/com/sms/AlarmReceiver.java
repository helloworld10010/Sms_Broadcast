package com.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        fun.Log("AlarmReceiver","AlarmReceiver onReceive ---------");
        if(intent.getAction().equals(fun.RESET_SMS_COUNT)){
            // 重置短信接收数量
            fun.smscount = 0;
            Toast.makeText(context, "自动清空短信统计", Toast.LENGTH_SHORT).show();
        }
    }
}
