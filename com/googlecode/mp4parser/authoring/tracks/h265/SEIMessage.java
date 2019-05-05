package com.googlecode.mp4parser.authoring.tracks.h265;

import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;

public class SEIMessage {
    public SEIMessage(BitReaderBuffer bitReaderBuffer) {
        int i = 0;
        while (((long) bitReaderBuffer.readBits(8)) == 255) {
            i += NalUnitUtil.EXTENDED_SAR;
        }
        i += bitReaderBuffer.readBits(8);
        do {
        } while (((long) bitReaderBuffer.readBits(8)) == 255);
        bitReaderBuffer.readBits(8);
        System.err.println("payloadType " + i);
    }
}
