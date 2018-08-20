package com.sms;

import android.content.Context;

public class smsloop extends Thread {
    Context context_;

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    boolean run=false;

    public smsloop(Context context) {
        this.context_ = context;
        fun.sms = new SMS(context);
    }

    @Override
    public void run() {
        try {
            run=true;
            sleep(2000);

            fun.sms.getSmsFromPhone();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

}
