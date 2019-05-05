package com.hanista.mobogram.messenger.exoplayer;

public interface MediaCodecSelector {
    public static final MediaCodecSelector DEFAULT;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.MediaCodecSelector.1 */
    static class C07061 implements MediaCodecSelector {
        C07061() {
        }

        public DecoderInfo getDecoderInfo(String str, boolean z) {
            return MediaCodecUtil.getDecoderInfo(str, z);
        }

        public DecoderInfo getPassthroughDecoderInfo() {
            return MediaCodecUtil.getPassthroughDecoderInfo();
        }
    }

    static {
        DEFAULT = new C07061();
    }

    DecoderInfo getDecoderInfo(String str, boolean z);

    DecoderInfo getPassthroughDecoderInfo();
}
