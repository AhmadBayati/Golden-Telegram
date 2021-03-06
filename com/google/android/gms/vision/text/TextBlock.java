package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.SparseArray;
import com.google.android.gms.vision.text.internal.client.BoundingBoxParcel;
import com.google.android.gms.vision.text.internal.client.LineBoxParcel;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class TextBlock implements Text {
    private Rect aLA;
    private LineBoxParcel[] aLx;
    private List<Line> aLy;
    private String aLz;
    private Point[] cornerPoints;

    /* renamed from: com.google.android.gms.vision.text.TextBlock.1 */
    class C02611 implements Comparator<Entry<String, Integer>> {
        final /* synthetic */ TextBlock aLB;

        C02611(TextBlock textBlock) {
            this.aLB = textBlock;
        }

        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((Entry) obj, (Entry) obj2);
        }

        public int zza(Entry<String, Integer> entry, Entry<String, Integer> entry2) {
            return ((Integer) entry.getValue()).compareTo((Integer) entry2.getValue());
        }
    }

    TextBlock(SparseArray<LineBoxParcel> sparseArray) {
        this.aLx = new LineBoxParcel[sparseArray.size()];
        for (int i = 0; i < this.aLx.length; i++) {
            this.aLx[i] = (LineBoxParcel) sparseArray.valueAt(i);
        }
    }

    private static Point[] zza(int i, int i2, int i3, int i4, BoundingBoxParcel boundingBoxParcel) {
        int i5 = boundingBoxParcel.left;
        int i6 = boundingBoxParcel.top;
        double sin = Math.sin(Math.toRadians((double) boundingBoxParcel.aLE));
        double cos = Math.cos(Math.toRadians((double) boundingBoxParcel.aLE));
        Point[] pointArr = new Point[]{new Point(i, i2), new Point(i3, i2), new Point(i3, i4), new Point(i, i4)};
        for (int i7 = 0; i7 < 4; i7++) {
            int i8 = (int) ((((double) pointArr[i7].x) * sin) + (((double) pointArr[i7].y) * cos));
            pointArr[i7].x = (int) ((((double) pointArr[i7].x) * cos) - (((double) pointArr[i7].y) * sin));
            pointArr[i7].y = i8;
            pointArr[i7].offset(i5, i6);
        }
        return pointArr;
    }

    private static Point[] zza(BoundingBoxParcel boundingBoxParcel, BoundingBoxParcel boundingBoxParcel2) {
        int i = -boundingBoxParcel2.left;
        int i2 = -boundingBoxParcel2.top;
        double sin = Math.sin(Math.toRadians((double) boundingBoxParcel2.aLE));
        double cos = Math.cos(Math.toRadians((double) boundingBoxParcel2.aLE));
        Point[] pointArr = new Point[4];
        pointArr[0] = new Point(boundingBoxParcel.left, boundingBoxParcel.top);
        pointArr[0].offset(i, i2);
        i = (int) ((((double) pointArr[0].x) * cos) + (((double) pointArr[0].y) * sin));
        i2 = (int) ((sin * ((double) (-pointArr[0].x))) + (cos * ((double) pointArr[0].y)));
        pointArr[0].x = i;
        pointArr[0].y = i2;
        pointArr[1] = new Point(boundingBoxParcel.width + i, i2);
        pointArr[2] = new Point(boundingBoxParcel.width + i, boundingBoxParcel.height + i2);
        pointArr[3] = new Point(i, i2 + boundingBoxParcel.height);
        return pointArr;
    }

    public Rect getBoundingBox() {
        if (this.aLA == null) {
            this.aLA = zza.zza((Text) this);
        }
        return this.aLA;
    }

    public List<? extends Text> getComponents() {
        return zzclw();
    }

    public Point[] getCornerPoints() {
        if (this.cornerPoints == null) {
            zzclv();
        }
        return this.cornerPoints;
    }

    public String getLanguage() {
        if (this.aLz != null) {
            return this.aLz;
        }
        HashMap hashMap = new HashMap();
        for (LineBoxParcel lineBoxParcel : this.aLx) {
            hashMap.put(lineBoxParcel.aLz, Integer.valueOf((hashMap.containsKey(lineBoxParcel.aLz) ? ((Integer) hashMap.get(lineBoxParcel.aLz)).intValue() : 0) + 1));
        }
        this.aLz = (String) ((Entry) Collections.max(hashMap.entrySet(), new C02611(this))).getKey();
        if (this.aLz == null || this.aLz.isEmpty()) {
            this.aLz = "und";
        }
        return this.aLz;
    }

    public String getValue() {
        if (this.aLx.length == 0) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        StringBuilder stringBuilder = new StringBuilder(this.aLx[0].aLJ);
        for (int i = 1; i < this.aLx.length; i++) {
            stringBuilder.append("\n");
            stringBuilder.append(this.aLx[i].aLJ);
        }
        return stringBuilder.toString();
    }

    void zzclv() {
        if (this.aLx.length == 0) {
            this.cornerPoints = new Point[0];
            return;
        }
        int i = ConnectionsManager.DEFAULT_DATACENTER_ID;
        int i2 = TLRPC.MESSAGE_FLAG_MEGAGROUP;
        int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        int i4 = TLRPC.MESSAGE_FLAG_MEGAGROUP;
        for (LineBoxParcel lineBoxParcel : this.aLx) {
            Point[] zza = zza(lineBoxParcel.aLG, this.aLx[0].aLG);
            int i5 = 0;
            while (i5 < 4) {
                Point point = zza[i5];
                int min = Math.min(i3, point.x);
                i3 = Math.max(i2, point.x);
                i2 = Math.min(i, point.y);
                i5++;
                i4 = Math.max(i4, point.y);
                i = i2;
                i2 = i3;
                i3 = min;
            }
        }
        this.cornerPoints = zza(i3, i, i2, i4, this.aLx[0].aLG);
    }

    List<Line> zzclw() {
        if (this.aLx.length == 0) {
            return new ArrayList(0);
        }
        if (this.aLy == null) {
            this.aLy = new ArrayList(this.aLx.length);
            for (LineBoxParcel line : this.aLx) {
                this.aLy.add(new Line(line));
            }
        }
        return this.aLy;
    }
}
