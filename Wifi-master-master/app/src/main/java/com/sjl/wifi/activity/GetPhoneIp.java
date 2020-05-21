package com.sjl.wifi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.sjl.wifi.R;

public class GetPhoneIp extends AppCompatActivity {

    public TextView tvIp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getphone);
        initView();
        initData();
    }

    private void initData() {
        String ip = getPhoneIp();
        tvIp.setText("3.获取手机DHCP的IP地址："+ip);
    }

    private void initView() {
        tvIp = findViewById(R.id.tvIP);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mReceive,intentFilter);
    }

    BroadcastReceiver mReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(GetPhoneIp.this," 收到USB广播 ："+intent.getAction(),Toast.LENGTH_SHORT).show();
            Log.e("YB","收到的广播 ："+intent.getAction());
        }
    };

    private String getPhoneIp() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        //WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return  android.text.format.Formatter.formatIpAddress(dhcpInfo.ipAddress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceive);
    }
}
