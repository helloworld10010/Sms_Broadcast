package com.sms;

public class SocketStart extends Thread {

    @Override
    public void run() {
        super.run();

            if (fun.isrun) {

                if (!fun.IP.equals("") && !fun.Port.equals("")) {
                    if (fun.socket == null) {
                        fun.socket = new Socketth(fun.IP, Integer.valueOf(fun.Port));
                        fun.socket.start();
                    }
                }
            }

    }


}
