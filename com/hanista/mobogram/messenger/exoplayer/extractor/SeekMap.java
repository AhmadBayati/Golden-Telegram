package com.hanista.mobogram.messenger.exoplayer.extractor;

public interface SeekMap {
    public static final SeekMap UNSEEKABLE;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap.1 */
    static class C07361 implements SeekMap {
        C07361() {
        }

        public long getPosition(long j) {
            return 0;
        }

        public boolean isSeekable() {
            return false;
        }
    }

    static {
        UNSEEKABLE = new C07361();
    }

    long getPosition(long j);

    boolean isSeekable();
}
