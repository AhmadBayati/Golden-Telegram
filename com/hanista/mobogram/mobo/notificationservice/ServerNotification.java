package com.hanista.mobogram.mobo.notificationservice;

public class ServerNotification {
    private String action;
    private String bigPictureUrl;
    private String btnText;
    private Integer iconType;
    private Long id;
    private String imageUrl;
    private Boolean ongoing;
    private String pictureUrl;
    private String targetPackage;
    private String text;
    private String text2;
    private String text3;
    private String title;
    private Integer type;
    private String url;
    private String value;

    public String getAction() {
        return this.action;
    }

    public String getBigPictureUrl() {
        return this.bigPictureUrl;
    }

    public String getBtnText() {
        return this.btnText;
    }

    public Integer getIconType() {
        return this.iconType;
    }

    public Long getId() {
        return this.id;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getPictureUrl() {
        return this.pictureUrl;
    }

    public String getTargetPackage() {
        return this.targetPackage;
    }

    public String getText() {
        return this.text;
    }

    public String getText2() {
        return this.text2;
    }

    public String getText3() {
        return this.text3;
    }

    public String getTitle() {
        return this.title;
    }

    public Integer getType() {
        return this.type;
    }

    public String getUrl() {
        return this.url;
    }

    public String getValue() {
        return this.value;
    }

    public Boolean isOngoing() {
        return Boolean.valueOf(this.ongoing == null ? false : this.ongoing.booleanValue());
    }

    public void setAction(String str) {
        this.action = str;
    }

    public void setBigPictureUrl(String str) {
        this.bigPictureUrl = str;
    }

    public void setBtnText(String str) {
        this.btnText = str;
    }

    public void setIconType(Integer num) {
        this.iconType = num;
    }

    public void setId(Long l) {
        this.id = l;
    }

    public void setImageUrl(String str) {
        this.imageUrl = str;
    }

    public void setOngoing(Boolean bool) {
        this.ongoing = bool;
    }

    public void setPictureUrl(String str) {
        this.pictureUrl = str;
    }

    public void setTargetPackage(String str) {
        this.targetPackage = str;
    }

    public void setText(String str) {
        this.text = str;
    }

    public void setText2(String str) {
        this.text2 = str;
    }

    public void setText3(String str) {
        this.text3 = str;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public void setType(Integer num) {
        this.type = num;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public void setValue(String str) {
        this.value = str;
    }
}
