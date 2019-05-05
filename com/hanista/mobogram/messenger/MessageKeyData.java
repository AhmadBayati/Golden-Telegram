package com.hanista.mobogram.messenger;

import com.hanista.mobogram.tgnet.SerializedData;

public class MessageKeyData {
    public byte[] aesIv;
    public byte[] aesKey;

    public static MessageKeyData generateMessageKeyData(byte[] bArr, byte[] bArr2, boolean z) {
        MessageKeyData messageKeyData = new MessageKeyData();
        if (bArr == null || bArr.length == 0) {
            messageKeyData.aesIv = null;
            messageKeyData.aesKey = null;
            return messageKeyData;
        }
        int i = z ? 8 : 0;
        SerializedData serializedData = new SerializedData();
        serializedData.writeBytes(bArr2);
        serializedData.writeBytes(bArr, i, 32);
        byte[] computeSHA1 = Utilities.computeSHA1(serializedData.toByteArray());
        serializedData.cleanup();
        serializedData = new SerializedData();
        serializedData.writeBytes(bArr, i + 32, 16);
        serializedData.writeBytes(bArr2);
        serializedData.writeBytes(bArr, i + 48, 16);
        byte[] computeSHA12 = Utilities.computeSHA1(serializedData.toByteArray());
        serializedData.cleanup();
        serializedData = new SerializedData();
        serializedData.writeBytes(bArr, i + 64, 32);
        serializedData.writeBytes(bArr2);
        byte[] computeSHA13 = Utilities.computeSHA1(serializedData.toByteArray());
        serializedData.cleanup();
        serializedData = new SerializedData();
        serializedData.writeBytes(bArr2);
        serializedData.writeBytes(bArr, i + 96, 32);
        byte[] computeSHA14 = Utilities.computeSHA1(serializedData.toByteArray());
        serializedData.cleanup();
        serializedData = new SerializedData();
        serializedData.writeBytes(computeSHA1, 0, 8);
        serializedData.writeBytes(computeSHA12, 8, 12);
        serializedData.writeBytes(computeSHA13, 4, 12);
        messageKeyData.aesKey = serializedData.toByteArray();
        serializedData.cleanup();
        serializedData = new SerializedData();
        serializedData.writeBytes(computeSHA1, 8, 12);
        serializedData.writeBytes(computeSHA12, 0, 8);
        serializedData.writeBytes(computeSHA13, 16, 4);
        serializedData.writeBytes(computeSHA14, 0, 8);
        messageKeyData.aesIv = serializedData.toByteArray();
        serializedData.cleanup();
        return messageKeyData;
    }
}
