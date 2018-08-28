package com.sms;

import me.pqpo.librarylog4a.Log4a;

public class Socketth extends Thread {
    Client client = null;
    private String ip_ = "127.0.0.1";
    private int port_ = 0;

    public Socketth(String ip, int port) {
        ip_ = ip;
        port_ = port;
        fun.Log("Socketth - IP", ip_);
        fun.Log("Socketth - Port", port_ + "");

    }

    @Override
    public void run() {
        super.run();
 //       if(fun.client==null)
        client = new Client(ip_, port_);
    }

    public boolean isconn() {
        boolean ret = false;
        try {
            ret =  client.soc.isConnected();

        } catch (Exception e) {
            ret = false;
        }

        return ret;
    }
    public boolean isclose() {
        boolean ret = false;
        try {
            ret =  client.soc.isClosed();

        } catch (Exception e) {
            ret = false;
        }

        return ret;
    }

    public void send(String sdata) {
        if ( client != null) {
            try {
                client.send(sdata);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void sendbyte(byte[] sdata) {
        try {
            client.sendbyte(sdata);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        fun.ConnState = "链接断开";
        if ( client != null){
            client.Close();
        }
    }
    public void close2() {
        fun.ConnState = "链接断开";
        if ( client != null){
            client.Close2();
        }
    }
}
