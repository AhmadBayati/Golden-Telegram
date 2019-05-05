package com.hanista.mobogram.messenger.exoplayer.chunk;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Display.Mode;
import android.view.WindowManager;
import com.hanista.mobogram.messenger.exoplayer.MediaCodecUtil;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import java.util.ArrayList;
import java.util.List;

public final class VideoFormatSelectorUtil {
    private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98f;
    private static final String TAG = "VideoFormatSelectorUtil";

    private VideoFormatSelectorUtil() {
    }

    private static Point getDisplaySize(Context context) {
        if (Util.SDK_INT < 25) {
            if ("Sony".equals(Util.MANUFACTURER) && Util.MODEL != null && Util.MODEL.startsWith("BRAVIA") && context.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd")) {
                return new Point(3840, 2160);
            }
            if ("NVIDIA".equals(Util.MANUFACTURER) && Util.MODEL != null && Util.MODEL.contains("SHIELD")) {
                String str;
                try {
                    Class cls = Class.forName("android.os.SystemProperties");
                    str = (String) cls.getMethod("get", new Class[]{String.class}).invoke(cls, new Object[]{"sys.display-size"});
                } catch (Throwable e) {
                    Log.e(TAG, "Failed to read sys.display-size", e);
                    str = null;
                }
                if (!TextUtils.isEmpty(str)) {
                    try {
                        String[] split = str.trim().split("x");
                        if (split.length == 2) {
                            int parseInt = Integer.parseInt(split[0]);
                            int parseInt2 = Integer.parseInt(split[1]);
                            if (parseInt > 0 && parseInt2 > 0) {
                                return new Point(parseInt, parseInt2);
                            }
                        }
                    } catch (NumberFormatException e2) {
                    }
                    Log.e(TAG, "Invalid sys.display-size: " + str);
                }
            }
        }
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        if (Util.SDK_INT >= 23) {
            getDisplaySizeV23(defaultDisplay, point);
            return point;
        } else if (Util.SDK_INT >= 17) {
            getDisplaySizeV17(defaultDisplay, point);
            return point;
        } else if (Util.SDK_INT >= 16) {
            getDisplaySizeV16(defaultDisplay, point);
            return point;
        } else {
            getDisplaySizeV9(defaultDisplay, point);
            return point;
        }
    }

    @TargetApi(16)
    private static void getDisplaySizeV16(Display display, Point point) {
        display.getSize(point);
    }

    @TargetApi(17)
    private static void getDisplaySizeV17(Display display, Point point) {
        display.getRealSize(point);
    }

    @TargetApi(23)
    private static void getDisplaySizeV23(Display display, Point point) {
        Mode mode = display.getMode();
        point.x = mode.getPhysicalWidth();
        point.y = mode.getPhysicalHeight();
    }

    private static void getDisplaySizeV9(Display display, Point point) {
        point.x = display.getWidth();
        point.y = display.getHeight();
    }

    private static Point getMaxVideoSizeInViewport(boolean z, int i, int i2, int i3, int i4) {
        Object obj = 1;
        if (z) {
            Object obj2 = i3 > i4 ? 1 : null;
            if (i <= i2) {
                obj = null;
            }
            if (obj2 != obj) {
                int i5 = i;
                i = i2;
                i2 = i5;
            }
        }
        return i3 * i2 >= i4 * i ? new Point(i, Util.ceilDivide(i * i4, i3)) : new Point(Util.ceilDivide(i2 * i3, i4), i2);
    }

    private static boolean isFormatPlayable(Format format, String[] strArr, boolean z, boolean z2) {
        if (strArr != null && !Util.contains(strArr, format.mimeType)) {
            return false;
        }
        if (z && (format.width >= 1280 || format.height >= 720)) {
            return false;
        }
        if (format.width > 0 && format.height > 0) {
            if (Util.SDK_INT >= 21) {
                String videoMediaMimeType = MimeTypes.getVideoMediaMimeType(format.codecs);
                if (MimeTypes.VIDEO_UNKNOWN.equals(videoMediaMimeType)) {
                    videoMediaMimeType = MimeTypes.VIDEO_H264;
                }
                if (format.frameRate <= 0.0f) {
                    return MediaCodecUtil.isSizeSupportedV21(videoMediaMimeType, z2, format.width, format.height);
                }
                return MediaCodecUtil.isSizeAndRateSupportedV21(videoMediaMimeType, z2, format.width, format.height, (double) format.frameRate);
            } else if (format.width * format.height > MediaCodecUtil.maxH264DecodableFrameSize()) {
                return false;
            }
        }
        return true;
    }

    public static int[] selectVideoFormats(List<? extends FormatWrapper> list, String[] strArr, boolean z, boolean z2, boolean z3, int i, int i2) {
        int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        List arrayList = new ArrayList();
        int size = list.size();
        int i4 = 0;
        while (i4 < size) {
            int i5;
            Format format = ((FormatWrapper) list.get(i4)).getFormat();
            if (isFormatPlayable(format, strArr, z, z3)) {
                arrayList.add(Integer.valueOf(i4));
                if (format.width > 0 && format.height > 0 && i > 0 && i2 > 0) {
                    Point maxVideoSizeInViewport = getMaxVideoSizeInViewport(z2, i, i2, format.width, format.height);
                    i5 = format.width * format.height;
                    if (format.width >= ((int) (((float) maxVideoSizeInViewport.x) * FRACTION_TO_CONSIDER_FULLSCREEN)) && format.height >= ((int) (((float) maxVideoSizeInViewport.y) * FRACTION_TO_CONSIDER_FULLSCREEN)) && i5 < i3) {
                        i4++;
                        i3 = i5;
                    }
                }
            }
            i5 = i3;
            i4++;
            i3 = i5;
        }
        if (i3 != Integer.MAX_VALUE) {
            for (i4 = arrayList.size() - 1; i4 >= 0; i4--) {
                Format format2 = ((FormatWrapper) list.get(((Integer) arrayList.get(i4)).intValue())).getFormat();
                if (format2.width > 0 && format2.height > 0) {
                    if (format2.height * format2.width > i3) {
                        arrayList.remove(i4);
                    }
                }
            }
        }
        return Util.toArray(arrayList);
    }

    public static int[] selectVideoFormatsForDefaultDisplay(Context context, List<? extends FormatWrapper> list, String[] strArr, boolean z) {
        Point displaySize = getDisplaySize(context);
        return selectVideoFormats(list, strArr, z, true, false, displaySize.x, displaySize.y);
    }
}
