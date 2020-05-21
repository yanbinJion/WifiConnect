package com.sjl.wifi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.sjl.wifi.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class GetPhoneIp extends AppCompatActivity {

    public TextView tvIp;
    public static final String TAG = "GetPhoneIp";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getphone);
        initView();
        initData();
    }

    private void initData() {
        String ip = getIPAddress(this);
        tvIp.setText("3.获取手机的IP地址："+ip);
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
    public String getIPAddress(Context context) {
        ConnectivityManager conMann = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobileNetworkInfo.isConnected()||wifiNetworkInfo.isConnected()) {
            if (mobileNetworkInfo.isConnected()) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    Log.e(TAG," inetAddress 是 手机网络 1111111： "+NetworkInterface.getNetworkInterfaces().hasMoreElements());
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        Log.e(TAG," inetAddress 是 手机网络 222222： "+intf.getInetAddresses());
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            Log.e(TAG," inetAddress  3333333333 : "+ inetAddress.isLoopbackAddress()+" is IVP4 :"+(inetAddress instanceof Inet4Address)+" 主机地址 ："+inetAddress.getHostAddress());
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (wifiNetworkInfo.isConnected()) {//当前使用无线网络
                Log.e(TAG," inetAddress 是 wifi 网络 0000000： ");
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            Log.e(TAG," inetAddress 是 无网络： ");
            Toast.makeText(this,"当前无网络",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
