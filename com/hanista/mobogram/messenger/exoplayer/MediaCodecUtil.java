package com.hanista.mobogram.messenger.exoplayer;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.media.MediaCodecList;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.Log;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TargetApi(16)
public final class MediaCodecUtil {
    private static final DecoderInfo PASSTHROUGH_DECODER_INFO;
    private static final String TAG = "MediaCodecUtil";
    private static final Map<CodecKey, List<DecoderInfo>> decoderInfosCache;
    private static int maxH264DecodableFrameSize;

    private static final class CodecKey {
        public final String mimeType;
        public final boolean secure;

        public CodecKey(String str, boolean z) {
            this.mimeType = str;
            this.secure = z;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != CodecKey.class) {
                return false;
            }
            CodecKey codecKey = (CodecKey) obj;
            return TextUtils.equals(this.mimeType, codecKey.mimeType) && this.secure == codecKey.secure;
        }

        public int hashCode() {
            return (this.secure ? 1231 : 1237) + (((this.mimeType == null ? 0 : this.mimeType.hashCode()) + 31) * 31);
        }
    }

    public static class DecoderQueryException extends IOException {
        private DecoderQueryException(Throwable th) {
            super("Failed to query underlying media codecs", th);
        }
    }

    private interface MediaCodecListCompat {
        int getCodecCount();

        MediaCodecInfo getCodecInfoAt(int i);

        boolean isSecurePlaybackSupported(String str, CodecCapabilities codecCapabilities);

        boolean secureDecodersExplicit();
    }

    private static final class MediaCodecListCompatV16 implements MediaCodecListCompat {
        private MediaCodecListCompatV16() {
        }

        public int getCodecCount() {
            return MediaCodecList.getCodecCount();
        }

        public MediaCodecInfo getCodecInfoAt(int i) {
            return MediaCodecList.getCodecInfoAt(i);
        }

        public boolean isSecurePlaybackSupported(String str, CodecCapabilities codecCapabilities) {
            return MimeTypes.VIDEO_H264.equals(str);
        }

        public boolean secureDecodersExplicit() {
            return false;
        }
    }

    @TargetApi(21)
    private static final class MediaCodecListCompatV21 implements MediaCodecListCompat {
        private final int codecKind;
        private MediaCodecInfo[] mediaCodecInfos;

        public MediaCodecListCompatV21(boolean z) {
            this.codecKind = z ? 1 : 0;
        }

        private void ensureMediaCodecInfosInitialized() {
            if (this.mediaCodecInfos == null) {
                this.mediaCodecInfos = new MediaCodecList(this.codecKind).getCodecInfos();
            }
        }

        public int getCodecCount() {
            ensureMediaCodecInfosInitialized();
            return this.mediaCodecInfos.length;
        }

        public MediaCodecInfo getCodecInfoAt(int i) {
            ensureMediaCodecInfosInitialized();
            return this.mediaCodecInfos[i];
        }

        public boolean isSecurePlaybackSupported(String str, CodecCapabilities codecCapabilities) {
            return codecCapabilities.isFeatureSupported("secure-playback");
        }

        public boolean secureDecodersExplicit() {
            return true;
        }
    }

    static {
        PASSTHROUGH_DECODER_INFO = new DecoderInfo("OMX.google.raw.decoder", null);
        decoderInfosCache = new HashMap();
        maxH264DecodableFrameSize = -1;
    }

    private MediaCodecUtil() {
    }

    private static int avcLevelToMaxFrameSize(int i) {
        switch (i) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
            case VideoPlayer.STATE_PREPARING /*2*/:
                return 25344;
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                return 101376;
            case TLRPC.USER_FLAG_PHONE /*16*/:
                return 101376;
            case TLRPC.USER_FLAG_PHOTO /*32*/:
                return 101376;
            case TLRPC.USER_FLAG_STATUS /*64*/:
                return 202752;
            case TLRPC.USER_FLAG_UNUSED /*128*/:
                return 414720;
            case TLRPC.USER_FLAG_UNUSED2 /*256*/:
                return 414720;
            case TLRPC.USER_FLAG_UNUSED3 /*512*/:
                return 921600;
            case TLRPC.MESSAGE_FLAG_HAS_VIEWS /*1024*/:
                return 1310720;
            case TLRPC.MESSAGE_FLAG_HAS_BOT_ID /*2048*/:
                return AccessibilityNodeInfoCompat.ACTION_SET_TEXT;
            case ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT /*4096*/:
                return AccessibilityNodeInfoCompat.ACTION_SET_TEXT;
            case MessagesController.UPDATE_MASK_CHANNEL /*8192*/:
                return 2228224;
            case MessagesController.UPDATE_MASK_CHAT_ADMINS /*16384*/:
                return 5652480;
            case TLRPC.MESSAGE_FLAG_EDITED /*32768*/:
                return 9437184;
            default:
                return -1;
        }
    }

    public static DecoderInfo getDecoderInfo(String str, boolean z) {
        List decoderInfos = getDecoderInfos(str, z);
        return decoderInfos.isEmpty() ? null : (DecoderInfo) decoderInfos.get(0);
    }

    public static synchronized List<DecoderInfo> getDecoderInfos(String str, boolean z) {
        List<DecoderInfo> list;
        synchronized (MediaCodecUtil.class) {
            CodecKey codecKey = new CodecKey(str, z);
            list = (List) decoderInfosCache.get(codecKey);
            if (list == null) {
                List decoderInfosInternal = getDecoderInfosInternal(codecKey, Util.SDK_INT >= 21 ? new MediaCodecListCompatV21(z) : new MediaCodecListCompatV16());
                if (z && decoderInfosInternal.isEmpty() && 21 <= Util.SDK_INT && Util.SDK_INT <= 23) {
                    List decoderInfosInternal2 = getDecoderInfosInternal(codecKey, new MediaCodecListCompatV16());
                    if (!decoderInfosInternal2.isEmpty()) {
                        Log.w(TAG, "MediaCodecList API didn't list secure decoder for: " + str + ". Assuming: " + ((DecoderInfo) decoderInfosInternal2.get(0)).name);
                    }
                    decoderInfosInternal = decoderInfosInternal2;
                }
                list = Collections.unmodifiableList(decoderInfosInternal);
                decoderInfosCache.put(codecKey, list);
            }
        }
        return list;
    }

    private static List<DecoderInfo> getDecoderInfosInternal(CodecKey codecKey, MediaCodecListCompat mediaCodecListCompat) {
        String name;
        try {
            List<DecoderInfo> arrayList = new ArrayList();
            String str = codecKey.mimeType;
            int codecCount = mediaCodecListCompat.getCodecCount();
            boolean secureDecodersExplicit = mediaCodecListCompat.secureDecodersExplicit();
            loop0:
            for (int i = 0; i < codecCount; i++) {
                MediaCodecInfo codecInfoAt = mediaCodecListCompat.getCodecInfoAt(i);
                name = codecInfoAt.getName();
                if (isCodecUsableDecoder(codecInfoAt, name, secureDecodersExplicit)) {
                    for (String str2 : codecInfoAt.getSupportedTypes()) {
                        if (str2.equalsIgnoreCase(str)) {
                            CodecCapabilities capabilitiesForType = codecInfoAt.getCapabilitiesForType(str2);
                            boolean isSecurePlaybackSupported = mediaCodecListCompat.isSecurePlaybackSupported(str, capabilitiesForType);
                            if ((!secureDecodersExplicit || codecKey.secure != isSecurePlaybackSupported) && (secureDecodersExplicit || codecKey.secure)) {
                                if (!secureDecodersExplicit && isSecurePlaybackSupported) {
                                    arrayList.add(new DecoderInfo(name + ".secure", capabilitiesForType));
                                    break loop0;
                                }
                            }
                            arrayList.add(new DecoderInfo(name, capabilitiesForType));
                        }
                    }
                    continue;
                }
            }
            return arrayList;
        } catch (Exception e) {
            if (Util.SDK_INT > 23 || arrayList.isEmpty()) {
                Log.e(TAG, "Failed to query codec " + name + " (" + str2 + ")");
                throw e;
            }
            Log.e(TAG, "Skipping codec " + name + " (failed to query capabilities)");
        } catch (Throwable e2) {
            throw new DecoderQueryException(null);
        }
    }

    public static DecoderInfo getPassthroughDecoderInfo() {
        return PASSTHROUGH_DECODER_INFO;
    }

    @TargetApi(21)
    private static VideoCapabilities getVideoCapabilitiesV21(String str, boolean z) {
        DecoderInfo decoderInfo = getDecoderInfo(str, z);
        return decoderInfo == null ? null : decoderInfo.capabilities.getVideoCapabilities();
    }

    private static boolean isCodecUsableDecoder(MediaCodecInfo mediaCodecInfo, String str, boolean z) {
        return !mediaCodecInfo.isEncoder() ? (z || !str.endsWith(".secure")) ? (Util.SDK_INT >= 21 || !("CIPAACDecoder".equals(str) || "CIPMP3Decoder".equals(str) || "CIPVorbisDecoder".equals(str) || "AACDecoder".equals(str) || "MP3Decoder".equals(str))) ? (Util.SDK_INT >= 18 || !"OMX.SEC.MP3.Decoder".equals(str)) ? (Util.SDK_INT < 18 && "OMX.MTK.AUDIO.DECODER.AAC".equals(str) && "a70".equals(Util.DEVICE)) ? false : (Util.SDK_INT == 16 && Util.DEVICE != null && "OMX.qcom.audio.decoder.mp3".equals(str) && ("dlxu".equals(Util.DEVICE) || "protou".equals(Util.DEVICE) || "ville".equals(Util.DEVICE) || "villeplus".equals(Util.DEVICE) || "villec2".equals(Util.DEVICE) || Util.DEVICE.startsWith("gee") || "C6602".equals(Util.DEVICE) || "C6603".equals(Util.DEVICE) || "C6606".equals(Util.DEVICE) || "C6616".equals(Util.DEVICE) || "L36h".equals(Util.DEVICE) || "SO-02E".equals(Util.DEVICE))) ? false : (Util.SDK_INT == 16 && "OMX.qcom.audio.decoder.aac".equals(str) && ("C1504".equals(Util.DEVICE) || "C1505".equals(Util.DEVICE) || "C1604".equals(Util.DEVICE) || "C1605".equals(Util.DEVICE))) ? false : (Util.SDK_INT > 19 || Util.DEVICE == null || !((Util.DEVICE.startsWith("d2") || Util.DEVICE.startsWith("serrano") || Util.DEVICE.startsWith("jflte") || Util.DEVICE.startsWith("santos")) && "samsung".equals(Util.MANUFACTURER) && str.equals("OMX.SEC.vp8.dec"))) ? Util.SDK_INT > 19 || Util.DEVICE == null || !Util.DEVICE.startsWith("jflte") || !"OMX.qcom.video.decoder.vp8".equals(str) : false : false : false : false : false;
    }

    @Deprecated
    public static boolean isH264ProfileSupported(int i, int i2) {
        DecoderInfo decoderInfo = getDecoderInfo(MimeTypes.VIDEO_H264, false);
        if (decoderInfo == null) {
            return false;
        }
        for (CodecProfileLevel codecProfileLevel : decoderInfo.capabilities.profileLevels) {
            if (codecProfileLevel.profile == i && codecProfileLevel.level >= i2) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(21)
    public static boolean isSizeAndRateSupportedV21(String str, boolean z, int i, int i2, double d) {
        Assertions.checkState(Util.SDK_INT >= 21);
        VideoCapabilities videoCapabilitiesV21 = getVideoCapabilitiesV21(str, z);
        return videoCapabilitiesV21 != null && videoCapabilitiesV21.areSizeAndRateSupported(i, i2, d);
    }

    @TargetApi(21)
    public static boolean isSizeSupportedV21(String str, boolean z, int i, int i2) {
        Assertions.checkState(Util.SDK_INT >= 21);
        VideoCapabilities videoCapabilitiesV21 = getVideoCapabilitiesV21(str, z);
        return videoCapabilitiesV21 != null && videoCapabilitiesV21.isSizeSupported(i, i2);
    }

    public static int maxH264DecodableFrameSize() {
        int i = 0;
        if (maxH264DecodableFrameSize == -1) {
            DecoderInfo decoderInfo = getDecoderInfo(MimeTypes.VIDEO_H264, false);
            if (decoderInfo != null) {
                CodecProfileLevel[] codecProfileLevelArr = decoderInfo.capabilities.profileLevels;
                int length = codecProfileLevelArr.length;
                int i2 = 0;
                while (i < length) {
                    i2 = Math.max(avcLevelToMaxFrameSize(codecProfileLevelArr[i].level), i2);
                    i++;
                }
                i = Math.max(i2, 172800);
            }
            maxH264DecodableFrameSize = i;
        }
        return maxH264DecodableFrameSize;
    }

    public static void warmCodec(String str, boolean z) {
        try {
            getDecoderInfos(str, z);
        } catch (Throwable e) {
            Log.e(TAG, "Codec warming failed", e);
        }
    }
}
