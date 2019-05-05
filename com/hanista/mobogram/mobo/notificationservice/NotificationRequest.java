package com.hanista.mobogram.mobo.notificationservice;

import java.util.List;

public class NotificationRequest {
    private String androidId;
    private Integer appVersion;
    private List<Long> notificationIds;
    private Integer sdkVersion;

    public NotificationRequest(List<Long> list, String str, Integer num, Integer num2) {
        this.notificationIds = list;
        this.androidId = str;
        this.appVersion = num;
        this.sdkVersion = num2;
    }

    public String getAndroidId() {
        return this.androidId;
    }

    public Integer getAppVersion() {
        return this.appVersion;
    }

    public List<Long> getNotificationIds() {
        return this.notificationIds;
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

    public void setNotificationIds(List<Long> list) {
        this.notificationIds = list;
    }

    public void setSdkVersion(Integer num) {
        this.sdkVersion = num;
    }
}
