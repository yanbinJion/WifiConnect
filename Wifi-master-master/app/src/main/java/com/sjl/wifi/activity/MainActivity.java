package com.sjl.wifi.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sjl.wifi.R;
import com.sjl.wifi.app.AppConstants;
import com.sjl.wifi.bean.WifiBean;
import com.sjl.wifi.event.ScanCodeEvent;
import com.sjl.wifi.util.CollectionUtils;
import com.sjl.wifi.util.WifiHelper;
import com.sjl.wifi.widget.WifiLinkDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MainActivity.java
 * @time 2019/3/21 13:58
 * @copyright(C) 2019 song
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn1, btn2, btn3;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final String[] NEEDED_PERMISSIONS2 = new String[]{
            Manifest.permission.ACCESS_WIFI_STATE
    };
    //权限请求码
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int PERMISSION_REQUEST_CODE2 =2;
    private boolean mHasPermission;
    private boolean mHasPermission2;
    private WifiHelper mWifiHelper;
    private String scanCodeSSID;
    private TextView scanWifi,tvIp;

    private static final String TAG = "MainActivity";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initView();
        initData();
        initListener();
        scanWifi.setText(null);
    }

    private ScanCodeWifiReceiver wifiReceiver;
    @Override
    public void onResume() {
        super.onResume();
        wifiReceiver = new ScanCodeWifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监听wifi是开关变化的状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//监听wifi连接状态广播,是否连接了一个有效路由
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化（开启一个热点或者关闭一个热点）
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        this.registerReceiver(wifiReceiver, filter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(wifiReceiver);
    }
    private void initView() {
        setContentView(R.layout.main_activity);
        mWifiHelper = new WifiHelper(this);
    }

    private void initData() {
        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        scanWifi = findViewById(R.id.scanWifi);
    }

    private void initListener() {
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                openActivity(WifiListActivity.class);
                break;
            case R.id.btn_2:
                //openActivity(WifiApActivity.class);
                mHasPermission = checkPermission();
                Log.e("YB"," mHasPermission :"+mHasPermission+" wifiEnable : "+mWifiHelper.isWifiEnabled());
                if (!mHasPermission){
                    requestPermission();
                }else if (mHasPermission&&!mWifiHelper.isWifiEnabled()){
                   Toast.makeText(this,"wifi开关未打开",Toast.LENGTH_SHORT).show();
                }else {
                    frist =true;
                    openActivity(ScanCodeConnectWifi.class);
                }
                break;
            case R.id.btn_3:
             /*   mHasPermission2 = checkPermission2();
                Log.e("YB"," mHasPermission2 :"+mHasPermission2+" wifiEnable : "+mWifiHelper.isWifiEnabled());
                if (!mHasPermission2){
                    requestPermission2();
                }else if (mHasPermission2&&!mWifiHelper.isWifiEnabled()){
                     Toast.makeText(this,"wifi开关未打开",Toast.LENGTH_SHORT).show();
                }else {
                   openActivity(GetPhoneIp.class);
                }*/
                openActivity(GetPhoneIp.class);
                break;
            default:
                break;
        }
    }

    private void openActivity(Class<?> clz) {
        startActivity(new Intent(this,clz));
    }

    /**
     * 检查是否已经授予权限
     *
     * @return
     */
    private boolean checkPermission() {
        for (String permission : NEEDED_PERMISSIONS) {
            Log.e("YB"," permission : "+permission);
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                NEEDED_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    /**
     * 申请权限2
     */
    private void requestPermission2() {
        ActivityCompat.requestPermissions(this,
                NEEDED_PERMISSIONS2, PERMISSION_REQUEST_CODE2);
    }
    private boolean checkPermission2() {
        for (String permission : NEEDED_PERMISSIONS2) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllPermission = true;
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermission = false;   //判断用户是否同意获取权限
                    break;
                }
            }

            Log.e("YB"," hasAllPermission: "+hasAllPermission);
            //如果同意权限
            if (hasAllPermission) {
                mHasPermission = true;
                if (mHasPermission && mWifiHelper.isWifiEnabled() ) {  //如果wifi开关是开 并且 已经获取权限
                    frist = true;
                    openActivity(ScanCodeConnectWifi.class);
                } else if (mHasPermission && !mWifiHelper.isWifiEnabled()){
                    Toast.makeText(MainActivity.this, "wifi未开启", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "相机或存储权限获取失败", Toast.LENGTH_SHORT).show();
                }

            } else {  //用户不同意权限
                mHasPermission = false;
                Toast.makeText(MainActivity.this, "相机或存储权限获取失败", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == PERMISSION_REQUEST_CODE2){
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermission = false;   //判断用户是否同意获取权限
                    break;
                }
            }
            //如果同意权限
            if (hasAllPermission) {
                mHasPermission2 = true;
                if (mHasPermission2 && mWifiHelper.isWifiEnabled()) {  //如果wifi开关是开 并且 已经获取权限
                    openActivity(GetPhoneIp.class);
                } else if (mHasPermission2 && ! mWifiHelper.isWifiEnabled()){
                    Toast.makeText(MainActivity.this, "wifi未开启", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "WIFI权限获取失败", Toast.LENGTH_SHORT).show();
                }

            } else {  //用户不同意权限
                mHasPermission2 = false;
                Toast.makeText(MainActivity.this, "WIFI权限获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScanCodeEvent event) {
        /* Do something */
        scanCodeSSID= event.ssid;
        Log.e("YB","onMessageEvent : "+event.ssid);
        handleWifiConnect();
    }

    private void handleWifiConnect() {
        mWifiHelper.startScan();
    }

    public int count = 0;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //扫码设备SN码匹配对应的wifi连接
                    Log.e("YB","已经连接的wifi "+connectedSSID);
                    for (int i =0;i< wifiList.size();i++){
                        Log.e("YB","扫描到的wifi "+wifiList.get(i).getWifiName());
                    }
                    if (wifiList!=null){
                        for (int i = 0;i<wifiList.size();i++){
                            count++;
                            if (scanCodeSSID.equals(wifiList.get(i).getWifiName())){
                                Log.e("YB","已经连接的wifi  00000 "+connectedSSID+" getwifiName :"+wifiList.get(i).getWifiName());
                                if (("\""+wifiList.get(i).getWifiName()+ "\"").equals(connectedSSID)){
                                    scanWifi.setText("wifi已连接:"+mWifiHelper.getConnectionWifiInfo().getSSID());
                                    return;
                                }else {
                                    Log.e("YB","扫码的wifi与连接的不一样");
                                    mWifiHelper.removeWifiBySsid(connectedSSID);
                                    String capabilities = wifiList.get(i).getCapabilities();
                                    if (mWifiHelper.getWifiCipherWay(capabilities) == WifiHelper.WifiCipherType.WIFICIPHER_NOPASS) {//无需密码
                                        mWifiHelper.connectWifi(wifiList.get(i).getWifiName(), null, capabilities);//不需要弹窗
                                    } else {  //需要密码，弹出输入密码dialog
                                        noConfigurationWifi(i);
                                    }
                                }
                            } else {
                                if (count ==wifiList.size()){
                                    Toast.makeText(MainActivity.this,"此条码未开启wifi",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    break;
                case 1:
                    scanWifi.setText(""+msg.obj);
                    break;

            }
        }
    };

    List<ScanResult> scanResults;
    List<WifiBean> wifiList = new ArrayList<>();
    public String connectedSSID;
    public boolean frist = true;
    private class ScanCodeWifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                WifiInfo connectedWifiInfo = mWifiHelper.getConnectionWifiInfo();
                if (NetworkInfo.State.CONNECTED == info.getState()){ //已经连接上wifi
                    connectedSSID =connectedWifiInfo.getSSID();
                     Message msg = new Message();
                     msg.what = 1;
                     msg.obj = "wifi已经连接："+connectedWifiInfo.getSSID();
                     handler.sendMessage(msg);
                }else if (NetworkInfo.State.DISCONNECTED == info.getState()){
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = "wifi已断开：";
                    handler.sendMessage(msg);
                }else if (NetworkInfo.State.CONNECTING == info.getState()){
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = "wifi正在连接："+connectedWifiInfo.getSSID();
                    handler.sendMessage(msg);
                }
            }else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())){ //wifi扫描成功
               scanResults = mWifiHelper.getFilterScanResult();
               if (!CollectionUtils.isNullOrEmpty(scanResults)){
                   for (int i=0;i<scanResults.size();i++){
                       WifiBean wifiBean = new WifiBean();
                       ScanResult scanResult = scanResults.get(i);
                       wifiBean.setWifiName(scanResult.SSID);
                       wifiBean.setState(AppConstants.WIFI_STATE_UNCONNECT);   //只要获取都假设设置成未连接，真正的状态都通过广播来确定
                       wifiBean.setCapabilities(scanResult.capabilities);
                       wifiBean.setLevel(String.valueOf(mWifiHelper.getLevel(scanResult.level)));
                       wifiBean.setScanResult(scanResult);
                       wifiList.add(wifiBean);
                   }
                   Log.e("YB","发消息了哦  111 ----------------- : "+ frist);
                   if (scanCodeSSID!=null&&frist){
                       Log.e("YB","发消息了哦 222 -----------------");
                           Message msg = new Message();
                           msg.what = 0;
                           handler.sendMessage(msg);
                           frist = false;
                   }
               }
           }else if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)){
               int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
               if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                   Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
               }
           }
        }
    }


    /**
     * 之前没有配置wifi， 弹出输入密码界面
     *
     * @param position
     */
    private void noConfigurationWifi(int position) {
        WifiLinkDialog linkDialog = new WifiLinkDialog(this, wifiList.get(position).getWifiName(), wifiList.get(position).getCapabilities());
        if (!linkDialog.isShowing()) {
            linkDialog.show();
        }
    }

}
