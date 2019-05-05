package com.coremedia.iso;

import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.nio.ByteBuffer;

public final class IsoTypeReaderVariable {
    public static long read(ByteBuffer byteBuffer, int i) {
        switch (i) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return (long) IsoTypeReader.readUInt8(byteBuffer);
            case VideoPlayer.STATE_PREPARING /*2*/:
                return (long) IsoTypeReader.readUInt16(byteBuffer);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return (long) IsoTypeReader.readUInt24(byteBuffer);
            case VideoPlayer.STATE_READY /*4*/:
                return IsoTypeReader.readUInt32(byteBuffer);
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                return IsoTypeReader.readUInt64(byteBuffer);
            default:
                throw new RuntimeException("I don't know how to read " + i + " bytes");
        }
    }
}
