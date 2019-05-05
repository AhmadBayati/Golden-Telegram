package com.hanista.mobogram.mobo.alarmservice;

public class AlarmResponse {
    private Integer displayCount;
    private Boolean exitOnDismiss;
    private Long id;
    private String imageUrl;
    private String message;
    private String negativeBtnAction;
    private String negativeBtnText;
    private String negativeBtnUrl;
    private String positiveBtnAction;
    private String positiveBtnText;
    private String positiveBtnUrl;
    private Integer showCount;
    private Integer targetNetwork;
    private Integer targetVersion;
    private String title;

    public AlarmResponse(Long l, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, Integer num, Boolean bool, Integer num2, Integer num3, Integer num4) {
        this.id = l;
        this.title = str;
        this.message = str2;
        this.imageUrl = str3;
        this.positiveBtnText = str4;
        this.positiveBtnAction = str5;
        this.positiveBtnUrl = str6;
        this.negativeBtnText = str7;
        this.negativeBtnAction = str8;
        this.negativeBtnUrl = str9;
        this.showCount = num;
        this.exitOnDismiss = bool;
        this.targetNetwork = num2;
        this.displayCount = num3;
        this.targetVersion = num4;
    }

    public Integer getDisplayCount() {
        return this.displayCount;
    }

    public Boolean getExitOnDismiss() {
        return this.exitOnDismiss;
    }

    public Long getId() {
        return this.id;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getMessage() {
        return this.message;
    }

    public String getNegativeBtnAction() {
        return this.negativeBtnAction;
    }

    public String getNegativeBtnText() {
        return this.negativeBtnText;
    }

    public String getNegativeBtnUrl() {
        return this.negativeBtnUrl;
    }

    public String getPositiveBtnAction() {
        return this.positiveBtnAction;
    }

    public String getPositiveBtnText() {
        return this.positiveBtnText;
    }

    public String getPositiveBtnUrl() {
        return this.positiveBtnUrl;
    }

    public Integer getShowCount() {
        return this.showCount;
    }

    public Integer getTargetNetwork() {
        return this.targetNetwork;
    }

    public Integer getTargetVersion() {
        return this.targetVersion;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDisplayCount(Integer num) {
        this.displayCount = num;
    }

    public void setExitOnDismiss(Boolean bool) {
        this.exitOnDismiss = bool;
    }

    public void setId(Long l) {
        this.id = l;
    }

    public void setImageUrl(String str) {
        this.imageUrl = str;
    }

    public void setMessage(String str) {
        this.message = str;
    }

    public void setNegativeBtnAction(String str) {
        this.negativeBtnAction = str;
    }

    public void setNegativeBtnText(String str) {
        this.negativeBtnText = str;
    }

    public void setNegativeBtnUrl(String str) {
        this.negativeBtnUrl = str;
    }

    public void setPositiveBtnAction(String str) {
        this.positiveBtnAction = str;
    }

    public void setPositiveBtnText(String str) {
        this.positiveBtnText = str;
    }

    public void setPositiveBtnUrl(String str) {
        this.positiveBtnUrl = str;
    }

    public void setShowCount(Integer num) {
        this.showCount = num;
    }

    public void setTargetNetwork(Integer num) {
        this.targetNetwork = num;
    }

    public void setTargetVersion(Integer num) {
        this.targetVersion = num;
    }

    public void setTitle(String str) {
        this.title = str;
    }
}
