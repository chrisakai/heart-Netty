package com.wlz.common.entity;

/**
 * @Author Apollo
 * @Date 2023/9/27 13:59
 * @Version 1.0
 */
public class DeviceInfo {
    private int doorSerial;
    private String assetCode;
    private String deviceIMEI;
    private String deviceSn;
    private String deviceIp;
    private int online;
    private String worksite;
    private int battery;

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "doorSerial=" + doorSerial +
                ", assetCode='" + assetCode + '\'' +
                ", deviceIMEI='" + deviceIMEI + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", deviceIp='" + deviceIp + '\'' +
                ", online=" + online +
                ", worksite='" + worksite + '\'' +
                ", battery=" + battery +
                '}';
    }

    public int getDoorSerial() {
        return doorSerial;
    }

    public void setDoorSerial(int doorSerial) {
        this.doorSerial = doorSerial;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getDeviceIMEI() {
        return deviceIMEI;
    }

    public void setDeviceIMEI(String deviceIMEI) {
        this.deviceIMEI = deviceIMEI;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getWorksite() {
        return worksite;
    }

    public void setWorksite(String worksite) {
        this.worksite = worksite;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }
}
