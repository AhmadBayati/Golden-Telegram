package com.googlecode.mp4parser.authoring;

import com.googlecode.mp4parser.util.Matrix;
import java.util.Date;

public class TrackMetaData implements Cloneable {
    private Date creationTime;
    private int group;
    private double height;
    private String language;
    int layer;
    private Matrix matrix;
    private Date modificationTime;
    private long timescale;
    private long trackId;
    private float volume;
    private double width;

    public TrackMetaData() {
        this.language = "eng";
        this.modificationTime = new Date();
        this.creationTime = new Date();
        this.matrix = Matrix.ROTATE_0;
        this.trackId = 1;
        this.group = 0;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Date getCreationTime() {
        return this.creationTime;
    }

    public int getGroup() {
        return this.group;
    }

    public double getHeight() {
        return this.height;
    }

    public String getLanguage() {
        return this.language;
    }

    public int getLayer() {
        return this.layer;
    }

    public Matrix getMatrix() {
        return this.matrix;
    }

    public Date getModificationTime() {
        return this.modificationTime;
    }

    public long getTimescale() {
        return this.timescale;
    }

    public long getTrackId() {
        return this.trackId;
    }

    public float getVolume() {
        return this.volume;
    }

    public double getWidth() {
        return this.width;
    }

    public void setCreationTime(Date date) {
        this.creationTime = date;
    }

    public void setGroup(int i) {
        this.group = i;
    }

    public void setHeight(double d) {
        this.height = d;
    }

    public void setLanguage(String str) {
        this.language = str;
    }

    public void setLayer(int i) {
        this.layer = i;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public void setModificationTime(Date date) {
        this.modificationTime = date;
    }

    public void setTimescale(long j) {
        this.timescale = j;
    }

    public void setTrackId(long j) {
        this.trackId = j;
    }

    public void setVolume(float f) {
        this.volume = f;
    }

    public void setWidth(double d) {
        this.width = d;
    }
}
