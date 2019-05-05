package com.hanista.mobogram.mobo.alarmservice;

public class AlarmRequest {
    private String androidId;
    private Integer appVersion;
    private Long lastAlarmId;
    private String packageName;
    private Integer sdkVersion;

    public AlarmRequest(String str, Integer num, Integer num2, String str2, Long l) {
        this.androidId = str;
        this.appVersion = num;
        this.sdkVersion = num2;
        this.packageName = str2;
        this.lastAlarmId = l;
    }

    public String getAndroidId() {
        return this.androidId;
    }

    public Integer getAppVersion() {
        return this.appVersion;
    }

    public Long getLastAlarmId() {
        return this.lastAlarmId;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public Integer getSdkVersion() {
        return this.sdkVersion;
    }

    public void setAndroidId(String str) {
        this.androidId = str;
    }

    public void setAppVersion(Integer num) {
        this.appVersion = num;
    }

    public void setLastAlarmId(Long l) {
        this.lastAlarmId = l;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public void setSdkVersion(Integer num) {
        this.sdkVersion = num;
    }
}
