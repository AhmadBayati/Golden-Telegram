package com.googlecode.mp4parser.h264.model;

import android.support.v4.view.InputDeviceCompat;
import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.h264.write.CAVLCWriter;
import com.hanista.mobogram.tgnet.TLRPC;

public class ScalingList {
    public int[] scalingList;
    public boolean useDefaultScalingMatrixFlag;

    public static ScalingList read(CAVLCReader cAVLCReader, int i) {
        int i2 = 8;
        ScalingList scalingList = new ScalingList();
        scalingList.scalingList = new int[i];
        int i3 = 0;
        int i4 = 8;
        while (i3 < i) {
            if (i2 != 0) {
                int readSE = ((cAVLCReader.readSE("deltaScale") + i4) + TLRPC.USER_FLAG_UNUSED2) % TLRPC.USER_FLAG_UNUSED2;
                boolean z = i3 == 0 && readSE == 0;
                scalingList.useDefaultScalingMatrixFlag = z;
                i2 = readSE;
            }
            scalingList.scalingList[i3] = i2 == 0 ? i4 : i2;
            i4 = scalingList.scalingList[i3];
            i3++;
        }
        return scalingList;
    }

    public String toString() {
        return "ScalingList{scalingList=" + this.scalingList + ", useDefaultScalingMatrixFlag=" + this.useDefaultScalingMatrixFlag + '}';
    }

    public void write(CAVLCWriter cAVLCWriter) {
        int i = 0;
        if (this.useDefaultScalingMatrixFlag) {
            cAVLCWriter.writeSE(0, "SPS: ");
            return;
        }
        int i2 = 8;
        while (i < this.scalingList.length) {
            cAVLCWriter.writeSE((this.scalingList[i] - i2) + InputDeviceCompat.SOURCE_ANY, "SPS: ");
            i2 = this.scalingList[i];
            i++;
        }
    }
}
