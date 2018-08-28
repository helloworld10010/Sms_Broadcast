package com.sms;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.pqpo.librarylog4a.Log4a;
/*
增加日志收集,使用线程池
17125048412
 */
public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;
    String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS};
    List<String> mPermissionList = new ArrayList<>();
    TextView txtmsg, txtsmscount;
    EditText edtip;
    Button btnconn, btndisconn,btnreset;
    PowerManager.WakeLock mWl;
    PowerManager mPm;
    CheckBox checkBox;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        fun.imei = fun.getPhoneIMEI(this);
        setContentView(R.layout.main);

        edtip =  findViewById(R.id.edtIP);
        txtmsg =  findViewById(R.id.txtmsg);
        btnreset = findViewById(R.id.btnReset);
        btnconn = findViewById(R.id.btnConn);
        btndisconn = findViewById(R.id.btnDisconn);

        checkBox = findViewById(R.id.logSwitch);
        SharedPreferences sharedPreferences = getSharedPreferences("sms", Context.MODE_PRIVATE);
        fun.openLog = sharedPreferences.getBoolean("open_log",true);
        checkBox.setChecked(fun.openLog);

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                Toast.makeText(MainActivity.this,"打开日志记录",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this,"关闭日志记录",Toast.LENGTH_SHORT).show();
            }
            editor.putBoolean("open_log", isChecked);
            fun.openLog = isChecked;
            editor.commit();
        });
        if(fun.socket!=null){
            if(fun.socket.isconn()){
                txtmsg.setText(fun.ConnState);
                btnconn.setEnabled(false);
            }
        }
        mPm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWl = mPm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myservice");
        mWl.acquire(5000);

        btnconn.setOnClickListener(view -> {
            fun.IP = ((EditText) findViewById(R.id.edtIP)).getText().toString().trim();
            fun.Port = ((EditText) findViewById(R.id.edtPort)).getText().toString().trim();
//            if(fun.loop==null)
//                fun.loop=new smsloop(this);
            if (!fun.IP.equals("") && !fun.Port.equals("")) {
                txtmsg.setText("链接中...");
                fun.ConnState = "";
                btnconn.setEnabled(false);
                showstate();
                Intent innerIntent = new Intent(MainActivity.this, SNSService.class);
                startService(innerIntent);
                fun.isrun = true;

            } else {
                showToast("IP地址与端口号必填");
            }
        });

        btndisconn.setOnClickListener(view -> {
            txtmsg.setText("断开中...");
            fun.isrun = false;
            fun.ConnState = "";
            btnconn.setEnabled(true);
            fun.receivesms = false;
            if (fun.socket != null) {
                fun.socket.close();
                fun.socket=null;
            }
            else{
                fun.ConnState = "链接断开";
            }

            showstate();
        });

        btnreset.setOnClickListener(v -> {
            fun.smscount = 0;
            txtsmscount.setText("0");
            Toast.makeText(this, "清除统计", Toast.LENGTH_SHORT).show();
        });
        showsmscount();
    }

    private void showsmscount() {
        // 轮询显示 短信接收个数 连接状态
        new Thread() {

            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(() -> {
                        txtsmscount = findViewById(R.id.txtsmscount);
                        txtsmscount.setText(String.valueOf(fun.smscount));
                     //   fun.Log("sms","ui");
                        if(fun.socket!=null){
                            if(fun.socket.isconn()){
                                txtmsg.setText(fun.ConnState);
                                btnconn.setEnabled(fun.isrun==false);
                            }
                        }
                    });
                }
            }
        }.start();
    }

    private void showstate() {

        new Thread() {
            int index = 0;

            public void run() {
                while (true) {

                    if (index >= 50) {
                        break;
                    }
                    if (!txtmsg.getText().toString().trim().equals(fun.ConnState)) {

                        runOnUiThread(() -> {

                            if (!fun.ConnState.equals("")) {
                                txtmsg.setText(fun.ConnState);
                                index = 50;
                                if (fun.ConnState.equals("链接成功")) {
                                    btnconn.setEnabled(false);
                                } else {
                                    btnconn.setEnabled(true);
                                }

                            }
                        });

                    } else {
                        try {
                            sleep(100);
                            index++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }.start();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();
        fun.Log("MainActivity","onStart-----------");
        try {
            // 设置默认应用
            final String myPackageName = getPackageName();
            if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);

                startActivity(intent);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            Toast.makeText(MainActivity.this, "已经授权", Toast.LENGTH_LONG).show();


        } else {//请求权限方法
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(MainActivity.this, permissions, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("权限已申请");
            } else {
                showToast("权限已拒绝");
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_CALL_CAMERA) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[i]);
                    if (showRequestPermission) {
                        showToast("权限未申请");
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showToast(String string) {
        Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
    }

    public void onBackPressed() {
        super.onBackPressed();
        Log4a.flush();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWl.isHeld()) {
            mWl.release();
        }
        fun.Log("sms", "Destroy");
        fun.Log("MainActivity","onDestroy-----------");
        Log4a.release();
    }
}
