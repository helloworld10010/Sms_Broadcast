package com.sms;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;

import me.pqpo.librarylog4a.Log4a;

public class Client {
    Socket soc;
    boolean con = false;// 网络连接情况 true连接 false断开
    public String ip = "127.0.0.1";
    public int port = 9991;
    InetSocketAddress add;

    DataInputStream InputStream;

    public Client(String host, int port_) {
        ip = host;
        port = port_;

        connection(host, port_);
    }

    public void connection(String host, int port_) {
        if (!con) {


            this.ip = host;
            this.port = port_;

            try {
                soc = new Socket();
                add = new InetSocketAddress(ip, port);
                soc.connect(add, 5000);
                soc.setSoTimeout(5000);
                con = soc.isConnected();

                if (con) {
                    fun.receivesms=true;
                    fun.isrun = true;
                    fun.receTime = new Date();
                    fun.ConnState = "链接成功";
                    InputStream = new DataInputStream(soc.getInputStream());
                    new Thread() {
                        @Override
                        public void run() {
                            while (con) {

                                try {
                                    if (!soc.isConnected() || soc.isClosed()) {
                                        Close();
                                        break;
                                    }
                                    sleep(fun.HeartCycle * 1000);
                                    Date ntime = new Date();
                                    long kk = ntime.getTime() - fun.receTime.getTime();
                                    if (kk > (fun.HeartCycle + 20) * 1000) {
                                        fun.Log("Client", "over HeartCycle:");
                                        Close();
                                        break;

                                    }

                                    sendHeart();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    fun.Log("SocketClient - Exception",e.getMessage());
                                    Close();
                                    break;
                                }

                            }
                        }
                    }.start();
                    new Thread() {
                        @Override
                        public void run() {
                            Receive();
                        }
                    }.start();
                    fun.Log("client", "connect ok");
                } else {
                    con = false;
                    fun.Log("client", "connect is fails");
                    Close();
                }
            } catch (IOException e) {
                con = false;
                fun.Log("client connect", e.getMessage());
                e.printStackTrace();
                fun.Log("SocketClient - Exception",e.getMessage());
                Close();
            }


        } else {
            fun.Log("client", "connect is over");
        }
    }


    public void Receive() {


        while (fun.isrun) {
            try {
                int len = 0;
                byte[] sdata = new byte[1024];
                boolean con1 = soc.isConnected();
                boolean clo = soc.isClosed();
                if (!con1 || clo) {
                    Close();
                    break;
                }
                len = InputStream.read(sdata);

                if (len == -1) {
                    Close();
                    break;
                }
                if (len > 0) {
                    fun.receTime = new Date();
                    String id = new String(sdata, 0, len);
                    fun.Log("ret", id);

                }
                sdata = new byte[0];
            } catch (SocketTimeoutException e) {
                fun.Log("SocketClient - Exception","SocketTimeoutException SocketTimeoutException SocketTimeoutException");
                try {
                    Thread.sleep(100);
                    Date ntime = new Date();
                    long kk = ntime.getTime() - fun.receTime.getTime();
                    if (kk > (fun.HeartCycle + 20) * 1000) {
                        fun.Log("Client", "over HeartCycle:");
                        Close();
                        break;

                    }
                    fun.Log("Client", "timeout-sleep:" + kk);
                    InputStream = new DataInputStream(soc.getInputStream());
                } catch (InterruptedException e1) {
                    fun.Log("SocketClient - Exception","");
                    Close();
                    break;
                } catch (IOException e1) {
                    fun.Log("SocketClient - Exception",e1.getMessage());
                    Close();
                    break;
                }
                continue;
            } catch (StringIndexOutOfBoundsException e) {
                fun.Log("SocketClient - Exception",e.getMessage());
                Close();
                break;
            } catch (IOException e) {
                fun.Log("SocketClient - Exception",e.getMessage());
                Close();
                break;
            }
        }
    }


    public void Close() {


        fun.ConnState = "链接断开";
        con = false;
        fun.receivesms=true;
        if (fun.socket != null) {
            try {
                soc.close();
            } catch (IOException e) {
                e.printStackTrace();
                fun.Log("SocketClient - Exception",e.getMessage());
            } finally {
                fun.Log("Client", "disconnect");
                fun.socket = null;
            }
        }


    }
    public void Close2() {


        fun.ConnState = "链接断开";
        con = false;
        if (fun.socket != null) {
            try {
                soc.close();
            } catch (IOException e) {
                e.printStackTrace();
                fun.Log("SocketClient - Exception",e.getMessage());
            } finally {
                fun.Log("Client", "disconnect");
                fun.socket = null;
            }
        }


    }
    public void sendbyte(byte[] data) {
        if (soc.isConnected()) {

            try {
                OutputStream os = soc.getOutputStream();
                //	os.flush();
                os.write(data);
                os.flush(); // 发送图片流，继续等待 结束指令
                fun.Log("Client", "sendbyte");
            } catch (Exception e) {
				e.printStackTrace();
                fun.Log("Client", "SendByte Error:" + e.getMessage());
                fun.Log("SocketClient - Exception - SendByte Error:",e.getMessage());

                Close();

            }

        } else {
            Close();
        }


    }

    public String send(String data) {
        if (con) {
                try {
                    OutputStream os = soc.getOutputStream();
                    os.write(data.getBytes());
                    os.flush(); // 发送图片流，继续等待 结束指令
                fun.Log("send", data);
            } catch (IOException e) {
                e.printStackTrace();
                fun.Log("SocketClient - Exception - SendByte Error:",e.getMessage());
                Close();
            }


        }

        return "OK";
    }

    private void sendHeart() {
        String msgObj = "address:888888888888@body:Heart@date:0000000000000000@id:8888@\n";
        send(msgObj);
    }
}
