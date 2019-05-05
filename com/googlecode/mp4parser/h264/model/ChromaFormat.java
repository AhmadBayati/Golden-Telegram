package com.googlecode.mp4parser.h264.model;

public class ChromaFormat {
    public static ChromaFormat MONOCHROME;
    public static ChromaFormat YUV_420;
    public static ChromaFormat YUV_422;
    public static ChromaFormat YUV_444;
    private int id;
    private int subHeight;
    private int subWidth;

    static {
        MONOCHROME = new ChromaFormat(0, 0, 0);
        YUV_420 = new ChromaFormat(1, 2, 2);
        YUV_422 = new ChromaFormat(2, 2, 1);
        YUV_444 = new ChromaFormat(3, 1, 1);
    }

    public ChromaFormat(int i, int i2, int i3) {
        this.id = i;
        this.subWidth = i2;
        this.subHeight = i3;
    }

    public static ChromaFormat fromId(int i) {
        return i == MONOCHROME.id ? MONOCHROME : i == YUV_420.id ? YUV_420 : i == YUV_422.id ? YUV_422 : i == YUV_444.id ? YUV_444 : null;
    }

    public int getId() {
        return this.id;
    }

    public int getSubHeight() {
        return this.subHeight;
    }

    public int getSubWidth() {
        return this.subWidth;
    }

    public String toString() {
        return "ChromaFormat{\nid=" + this.id + ",\n" + " subWidth=" + this.subWidth + ",\n" + " subHeight=" + this.subHeight + '}';
    }
}
