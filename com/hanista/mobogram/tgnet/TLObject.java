package com.hanista.mobogram.tgnet;

public class TLObject {
    private static final ThreadLocal<NativeByteBuffer> sizeCalculator;
    public boolean disableFree;

    /* renamed from: com.hanista.mobogram.tgnet.TLObject.1 */
    static class C09461 extends ThreadLocal<NativeByteBuffer> {
        C09461() {
        }

        protected NativeByteBuffer initialValue() {
            return new NativeByteBuffer(true);
        }
    }

    static {
        sizeCalculator = new C09461();
    }

    public TLObject() {
        this.disableFree = false;
    }

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return null;
    }

    public void freeResources() {
    }

    public int getObjectSize() {
        NativeByteBuffer nativeByteBuffer = (NativeByteBuffer) sizeCalculator.get();
        nativeByteBuffer.rewind();
        serializeToStream((AbstractSerializedData) sizeCalculator.get());
        return nativeByteBuffer.length();
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
    }
}
