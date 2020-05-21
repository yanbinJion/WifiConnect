package com.sjl.wifi.activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sjl.wifi.R;
import com.sjl.wifi.event.ScanCodeEvent;

import org.greenrobot.eventbus.EventBus;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

public class ScanCodeConnectWifi extends AppCompatActivity implements View.OnClickListener, QRCodeView.Delegate {

    public ZBarView zScanview;
    public ImageView ivBack;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        zScanview =  findViewById(R.id.zScanview);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
        zScanview.setDelegate(this);
        zScanview.changeToScanQRCodeStyle();
        zScanview.setType(BarcodeType.ALL, null);
        zScanview.startSpotAndShowRect();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onStart() {
        super.onStart();
        zScanview.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别

        zScanview.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    public void onStop() {
        zScanview.stopCamera() ;// 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    public void onDestroy() {
        zScanview.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivBack:
                finish();
                break;
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.e("YB","扫码成功 ： "+result);
        vibrate();
        ScanCodeEvent scEvent = new ScanCodeEvent(result);
        EventBus.getDefault().post(scEvent);
        Toast.makeText(this,"扫码结果： "+result,Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = zScanview.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                zScanview.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                zScanview.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }
}
