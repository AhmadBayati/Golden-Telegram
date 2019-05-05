package com.coremedia.iso;

import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.nio.ByteBuffer;

public final class IsoTypeWriterVariable {
    public static void write(long j, ByteBuffer byteBuffer, int i) {
        switch (i) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                IsoTypeWriter.writeUInt8(byteBuffer, (int) (255 & j));
            case VideoPlayer.STATE_PREPARING /*2*/:
                IsoTypeWriter.writeUInt16(byteBuffer, (int) (65535 & j));
            case VideoPlayer.STATE_BUFFERING /*3*/:
                IsoTypeWriter.writeUInt24(byteBuffer, (int) (16777215 & j));
            case VideoPlayer.STATE_READY /*4*/:
                IsoTypeWriter.writeUInt32(byteBuffer, j);
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                IsoTypeWriter.writeUInt64(byteBuffer, j);
            default:
                throw new RuntimeException("I don't know how to read " + i + " bytes");
        }
    }
}
