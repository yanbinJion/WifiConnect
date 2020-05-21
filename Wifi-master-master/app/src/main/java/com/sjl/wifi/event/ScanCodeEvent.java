package com.sjl.wifi.event;

public class ScanCodeEvent {
    public String ssid;

    public ScanCodeEvent(String ssid) {
        this.ssid = ssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    @Override
    public String toString() {
        return "ScanCodeEvent{" +
                "ssid='" + ssid + '\'' +
                '}';
    }
}
