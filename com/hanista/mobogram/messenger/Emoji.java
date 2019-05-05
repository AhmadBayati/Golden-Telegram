package com.hanista.mobogram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Locale;

public class Emoji {
    private static int bigImgSize = 0;
    private static final int[][] cols;
    private static int drawImgSize = 0;
    private static Bitmap[][] emojiBmp = null;
    private static boolean inited = false;
    private static boolean[][] loadingEmoji = null;
    private static Paint placeholderPaint = null;
    private static HashMap<CharSequence, DrawableInfo> rects = null;
    private static final int splitCount = 4;

    /* renamed from: com.hanista.mobogram.messenger.Emoji.1 */
    static class C03851 implements Runnable {
        final /* synthetic */ Bitmap val$finalBitmap;
        final /* synthetic */ int val$page;
        final /* synthetic */ int val$page2;

        C03851(int i, int i2, Bitmap bitmap) {
            this.val$page = i;
            this.val$page2 = i2;
            this.val$finalBitmap = bitmap;
        }

        public void run() {
            Emoji.emojiBmp[this.val$page][this.val$page2] = this.val$finalBitmap;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.emojiDidLoaded, new Object[0]);
        }
    }

    private static class DrawableInfo {
        public int emojiIndex;
        public byte page;
        public byte page2;
        public Rect rect;

        public DrawableInfo(Rect rect, byte b, byte b2, int i) {
            this.rect = rect;
            this.page = b;
            this.page2 = b2;
            this.emojiIndex = i;
        }
    }

    public static class EmojiDrawable extends Drawable {
        private static Paint paint;
        private static Rect rect;
        private static TextPaint textPaint;
        private boolean fullSize;
        private DrawableInfo info;

        /* renamed from: com.hanista.mobogram.messenger.Emoji.EmojiDrawable.1 */
        class C03861 implements Runnable {
            C03861() {
            }

            public void run() {
                Emoji.loadEmoji(EmojiDrawable.this.info.page, EmojiDrawable.this.info.page2);
                Emoji.loadingEmoji[EmojiDrawable.this.info.page][EmojiDrawable.this.info.page2] = false;
            }
        }

        static {
            paint = new Paint(2);
            rect = new Rect();
            textPaint = new TextPaint(1);
        }

        public EmojiDrawable(DrawableInfo drawableInfo) {
            this.fullSize = false;
            this.info = drawableInfo;
        }

        public void draw(Canvas canvas) {
            if (Emoji.emojiBmp[this.info.page][this.info.page2] != null) {
                canvas.drawBitmap(Emoji.emojiBmp[this.info.page][this.info.page2], this.info.rect, this.fullSize ? getDrawRect() : getBounds(), paint);
            } else if (!Emoji.loadingEmoji[this.info.page][this.info.page2]) {
                Emoji.loadingEmoji[this.info.page][this.info.page2] = true;
                Utilities.globalQueue.postRunnable(new C03861());
                canvas.drawRect(getBounds(), Emoji.placeholderPaint);
            }
        }

        public Rect getDrawRect() {
            Rect bounds = getBounds();
            int centerX = bounds.centerX();
            int centerY = bounds.centerY();
            rect.left = centerX - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.right = ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2) + centerX;
            rect.top = centerY - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.bottom = ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2) + centerY;
            return rect;
        }

        public DrawableInfo getDrawableInfo() {
            return this.info;
        }

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    public static class EmojiSpan extends ImageSpan {
        private FontMetricsInt fontMetrics;
        private int size;

        public EmojiSpan(EmojiDrawable emojiDrawable, int i, int i2, FontMetricsInt fontMetricsInt) {
            super(emojiDrawable, i);
            this.fontMetrics = null;
            this.size = AndroidUtilities.dp(20.0f);
            this.fontMetrics = fontMetricsInt;
            if (fontMetricsInt != null) {
                this.size = Math.abs(this.fontMetrics.descent) + Math.abs(this.fontMetrics.ascent);
                if (this.size == 0) {
                    this.size = AndroidUtilities.dp(20.0f);
                }
            }
        }

        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fontMetricsInt) {
            FontMetricsInt fontMetricsInt2 = fontMetricsInt == null ? new FontMetricsInt() : fontMetricsInt;
            if (this.fontMetrics == null) {
                int size = super.getSize(paint, charSequence, i, i2, fontMetricsInt2);
                int dp = AndroidUtilities.dp(8.0f);
                int dp2 = AndroidUtilities.dp(10.0f);
                fontMetricsInt2.top = (-dp2) - dp;
                fontMetricsInt2.bottom = dp2 - dp;
                fontMetricsInt2.ascent = (-dp2) - dp;
                fontMetricsInt2.leading = 0;
                fontMetricsInt2.descent = dp2 - dp;
                return size;
            }
            if (fontMetricsInt2 != null) {
                fontMetricsInt2.ascent = this.fontMetrics.ascent;
                fontMetricsInt2.descent = this.fontMetrics.descent;
                fontMetricsInt2.top = this.fontMetrics.top;
                fontMetricsInt2.bottom = this.fontMetrics.bottom;
            }
            if (getDrawable() != null) {
                getDrawable().setBounds(0, 0, this.size, this.size);
            }
            return this.size;
        }

        public void replaceFontMetrics(FontMetricsInt fontMetricsInt, int i) {
            this.fontMetrics = fontMetricsInt;
            this.size = i;
        }
    }

    static {
        int i;
        rects = new HashMap();
        inited = false;
        emojiBmp = (Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{5, splitCount});
        loadingEmoji = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{5, splitCount});
        cols = new int[][]{new int[]{12, 12, 12, 12}, new int[]{6, 6, 6, 6}, new int[]{9, 9, 9, 9}, new int[]{9, 9, 9, 9}, new int[]{8, 8, 8, 7}};
        int i2 = 2;
        if (AndroidUtilities.density <= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            i = 32;
            i2 = 1;
        } else {
            i = AndroidUtilities.density <= 1.5f ? 64 : AndroidUtilities.density <= 2.0f ? 64 : 64;
        }
        drawImgSize = AndroidUtilities.dp(20.0f);
        bigImgSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        for (int i3 = 0; i3 < EmojiData.data.length; i3++) {
            int ceil = (int) Math.ceil((double) (((float) EmojiData.data[i3].length) / 4.0f));
            for (int i4 = 0; i4 < EmojiData.data[i3].length; i4++) {
                int i5 = i4 / ceil;
                int i6 = i4 - (i5 * ceil);
                int i7 = i6 % cols[i3][i5];
                i6 /= cols[i3][i5];
                rects.put(EmojiData.data[i3][i4], new DrawableInfo(new Rect((i7 * i) + (i7 * i2), (i6 * i) + (i6 * i2), (i7 * i2) + ((i7 + 1) * i), (i6 * i2) + ((i6 + 1) * i)), (byte) i3, (byte) i5, i4));
            }
        }
        placeholderPaint = new Paint();
        placeholderPaint.setColor(0);
    }

    public static String fixEmoji(String str) {
        int length = str.length();
        int i = 0;
        String str2 = str;
        while (i < length) {
            char charAt = str2.charAt(i);
            if (charAt < '\ud83c' || charAt > '\ud83e') {
                if (charAt == '\u20e3') {
                    break;
                } else if (charAt >= '\u203c' && charAt <= '\u3299' && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(charAt))) {
                    str2 = str2.substring(0, i + 1) + "\ufe0f" + str2.substring(i + 1);
                    length++;
                    i++;
                }
            } else if (charAt != '\ud83c' || i >= length - 1) {
                i++;
            } else {
                charAt = str2.charAt(i + 1);
                if (charAt == '\ude2f' || charAt == '\udc04' || charAt == '\ude1a' || charAt == '\udd7f') {
                    str2 = str2.substring(0, i + 2) + "\ufe0f" + str2.substring(i + 2);
                    length++;
                    i += 2;
                } else {
                    i++;
                }
            }
            i++;
        }
        return str2;
    }

    public static Drawable getEmojiBigDrawable(String str) {
        Drawable emojiDrawable = getEmojiDrawable(str);
        if (emojiDrawable == null) {
            return null;
        }
        emojiDrawable.setBounds(0, 0, bigImgSize, bigImgSize);
        emojiDrawable.fullSize = true;
        return emojiDrawable;
    }

    public static EmojiDrawable getEmojiDrawable(CharSequence charSequence) {
        DrawableInfo drawableInfo = (DrawableInfo) rects.get(charSequence);
        if (drawableInfo == null) {
            FileLog.m16e("tmessages", "No drawable for emoji " + charSequence);
            return null;
        }
        EmojiDrawable emojiDrawable = new EmojiDrawable(drawableInfo);
        emojiDrawable.setBounds(0, 0, drawImgSize, drawImgSize);
        return emojiDrawable;
    }

    private static boolean inArray(char c, char[] cArr) {
        for (char c2 : cArr) {
            if (c2 == c) {
                return true;
            }
        }
        return false;
    }

    public static void invalidateAll(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                invalidateAll(viewGroup.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            view.invalidate();
        }
    }

    private static void loadEmoji(int i, int i2) {
        float f;
        Throwable th;
        Bitmap decodeStream;
        int i3 = 2;
        try {
            int i4;
            File fileStreamPath;
            if (AndroidUtilities.density <= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                f = 2.0f;
            } else if (AndroidUtilities.density <= 1.5f) {
                i3 = 1;
                f = 2.0f;
            } else if (AndroidUtilities.density <= 2.0f) {
                i3 = 1;
                f = 2.0f;
            } else {
                i3 = 1;
                f = 2.0f;
            }
            for (i4 = splitCount; i4 < 7; i4++) {
                fileStreamPath = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.jpg", new Object[]{Integer.valueOf(i4), Float.valueOf(f), Integer.valueOf(i)}));
                if (fileStreamPath.exists()) {
                    fileStreamPath.delete();
                }
                fileStreamPath = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_a_%d.jpg", new Object[]{Integer.valueOf(i4), Float.valueOf(f), Integer.valueOf(i)}));
                if (fileStreamPath.exists()) {
                    fileStreamPath.delete();
                }
            }
            for (i4 = 8; i4 < 10; i4++) {
                fileStreamPath = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.png", new Object[]{Integer.valueOf(i4), Float.valueOf(f), Integer.valueOf(i)}));
                if (fileStreamPath.exists()) {
                    fileStreamPath.delete();
                }
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        } catch (Throwable th2) {
            FileLog.m17e("tmessages", "Error loading emoji", th2);
            return;
        }
        try {
            InputStream open = ApplicationLoader.applicationContext.getAssets().open("emoji/" + String.format(Locale.US, "v10_emoji%.01fx_%d_%d.png", new Object[]{Float.valueOf(f), Integer.valueOf(i), Integer.valueOf(i2)}));
            Options options = new Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = i3;
            decodeStream = BitmapFactory.decodeStream(open, null, options);
            try {
                open.close();
            } catch (Throwable th3) {
                th2 = th3;
                FileLog.m18e("tmessages", th2);
                AndroidUtilities.runOnUIThread(new C03851(i, i2, decodeStream));
            }
        } catch (Throwable th4) {
            th2 = th4;
            decodeStream = null;
            FileLog.m18e("tmessages", th2);
            AndroidUtilities.runOnUIThread(new C03851(i, i2, decodeStream));
        }
        AndroidUtilities.runOnUIThread(new C03851(i, i2, decodeStream));
    }

    public static CharSequence replaceEmoji(CharSequence charSequence, FontMetricsInt fontMetricsInt, int i, boolean z) {
        return replaceEmoji(charSequence, fontMetricsInt, i, z, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.CharSequence replaceEmoji(java.lang.CharSequence r21, android.graphics.Paint.FontMetricsInt r22, int r23, boolean r24, int[] r25) {
        /*
        r2 = com.hanista.mobogram.mobo.MoboConstants.aP;
        if (r2 == 0) goto L_0x0009;
    L_0x0004:
        r21 = com.hanista.mobogram.mobo.p006g.Emoji.m1012a(r21, r22, r23, r24);
    L_0x0008:
        return r21;
    L_0x0009:
        r2 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r2 = r2.useSystemEmoji;
        if (r2 != 0) goto L_0x0008;
    L_0x0011:
        if (r21 == 0) goto L_0x0008;
    L_0x0013:
        r2 = r21.length();
        if (r2 == 0) goto L_0x0008;
    L_0x0019:
        if (r24 != 0) goto L_0x00b8;
    L_0x001b:
        r0 = r21;
        r2 = r0 instanceof android.text.Spannable;
        if (r2 == 0) goto L_0x00b8;
    L_0x0021:
        r2 = r21;
        r2 = (android.text.Spannable) r2;
    L_0x0025:
        r8 = 0;
        r10 = 0;
        r6 = -1;
        r4 = 0;
        r5 = 0;
        r13 = new java.lang.StringBuilder;
        r3 = 16;
        r13.<init>(r3);
        r14 = r21.length();
        r3 = 0;
        r12 = 0;
        r7 = r25;
    L_0x003a:
        if (r12 >= r14) goto L_0x0237;
    L_0x003c:
        r0 = r21;
        r11 = r0.charAt(r12);	 Catch:{ Exception -> 0x0247 }
        r15 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        if (r11 < r15) goto L_0x004c;
    L_0x0047:
        r15 = 55358; // 0xd83e float:7.7573E-41 double:2.73505E-319;
        if (r11 <= r15) goto L_0x0075;
    L_0x004c:
        r16 = 0;
        r15 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r15 == 0) goto L_0x00c6;
    L_0x0052:
        r16 = -4294967296; // 0xffffffff00000000 float:0.0 double:NaN;
        r16 = r16 & r8;
        r18 = 0;
        r15 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1));
        if (r15 != 0) goto L_0x00c6;
    L_0x005f:
        r16 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r16 = r16 & r8;
        r18 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        r15 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1));
        if (r15 != 0) goto L_0x00c6;
    L_0x006b:
        r15 = 56806; // 0xdde6 float:7.9602E-41 double:2.8066E-319;
        if (r11 < r15) goto L_0x00c6;
    L_0x0070:
        r15 = 56831; // 0xddff float:7.9637E-41 double:2.8078E-319;
        if (r11 > r15) goto L_0x00c6;
    L_0x0075:
        r5 = -1;
        if (r6 != r5) goto L_0x0079;
    L_0x0078:
        r6 = r12;
    L_0x0079:
        r13.append(r11);	 Catch:{ Exception -> 0x0247 }
        r4 = r4 + 1;
        r5 = 16;
        r8 = r8 << r5;
        r0 = (long) r11;	 Catch:{ Exception -> 0x0247 }
        r16 = r0;
        r8 = r8 | r16;
        r5 = r7;
    L_0x0087:
        r7 = 0;
        r11 = r7;
        r7 = r4;
        r4 = r3;
        r3 = r12;
    L_0x008c:
        r15 = 3;
        if (r11 >= r15) goto L_0x0184;
    L_0x008f:
        r15 = r3 + 1;
        if (r15 >= r14) goto L_0x00b5;
    L_0x0093:
        r15 = r3 + 1;
        r0 = r21;
        r15 = r0.charAt(r15);	 Catch:{ Exception -> 0x0247 }
        r16 = 1;
        r0 = r16;
        if (r11 != r0) goto L_0x0170;
    L_0x00a1:
        r16 = 8205; // 0x200d float:1.1498E-41 double:4.054E-320;
        r0 = r16;
        if (r15 != r0) goto L_0x00b5;
    L_0x00a7:
        r16 = r13.length();	 Catch:{ Exception -> 0x0247 }
        if (r16 <= 0) goto L_0x00b5;
    L_0x00ad:
        r13.append(r15);	 Catch:{ Exception -> 0x0247 }
        r3 = r3 + 1;
        r7 = r7 + 1;
        r4 = 0;
    L_0x00b5:
        r11 = r11 + 1;
        goto L_0x008c;
    L_0x00b8:
        r2 = android.text.Spannable.Factory.getInstance();
        r3 = r21.toString();
        r2 = r2.newSpannable(r3);
        goto L_0x0025;
    L_0x00c6:
        r15 = r13.length();	 Catch:{ Exception -> 0x0247 }
        if (r15 <= 0) goto L_0x00de;
    L_0x00cc:
        r15 = 9792; // 0x2640 float:1.3722E-41 double:4.838E-320;
        if (r11 == r15) goto L_0x00d4;
    L_0x00d0:
        r15 = 9794; // 0x2642 float:1.3724E-41 double:4.839E-320;
        if (r11 != r15) goto L_0x00de;
    L_0x00d4:
        r13.append(r11);	 Catch:{ Exception -> 0x0247 }
        r4 = r4 + 1;
        r8 = 0;
        r3 = 1;
        r5 = r7;
        goto L_0x0087;
    L_0x00de:
        r16 = 0;
        r15 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r15 <= 0) goto L_0x00f9;
    L_0x00e4:
        r15 = 61440; // 0xf000 float:8.6096E-41 double:3.03554E-319;
        r15 = r15 & r11;
        r16 = 53248; // 0xd000 float:7.4616E-41 double:2.6308E-319;
        r0 = r16;
        if (r15 != r0) goto L_0x00f9;
    L_0x00ef:
        r13.append(r11);	 Catch:{ Exception -> 0x0247 }
        r4 = r4 + 1;
        r8 = 0;
        r3 = 1;
        r5 = r7;
        goto L_0x0087;
    L_0x00f9:
        r15 = 8419; // 0x20e3 float:1.1798E-41 double:4.1595E-320;
        if (r11 != r15) goto L_0x012c;
    L_0x00fd:
        if (r12 <= 0) goto L_0x0253;
    L_0x00ff:
        r0 = r21;
        r15 = r0.charAt(r5);	 Catch:{ Exception -> 0x0247 }
        r16 = 48;
        r0 = r16;
        if (r15 < r0) goto L_0x0111;
    L_0x010b:
        r16 = 57;
        r0 = r16;
        if (r15 <= r0) goto L_0x011d;
    L_0x0111:
        r16 = 35;
        r0 = r16;
        if (r15 == r0) goto L_0x011d;
    L_0x0117:
        r16 = 42;
        r0 = r16;
        if (r15 != r0) goto L_0x0256;
    L_0x011d:
        r3 = r12 - r5;
        r4 = r3 + 1;
        r13.append(r15);	 Catch:{ Exception -> 0x0247 }
        r13.append(r11);	 Catch:{ Exception -> 0x0247 }
        r3 = 1;
    L_0x0128:
        r6 = r5;
        r5 = r7;
        goto L_0x0087;
    L_0x012c:
        r5 = 169; // 0xa9 float:2.37E-43 double:8.35E-322;
        if (r11 == r5) goto L_0x013c;
    L_0x0130:
        r5 = 174; // 0xae float:2.44E-43 double:8.6E-322;
        if (r11 == r5) goto L_0x013c;
    L_0x0134:
        r5 = 8252; // 0x203c float:1.1564E-41 double:4.077E-320;
        if (r11 < r5) goto L_0x0155;
    L_0x0138:
        r5 = 12953; // 0x3299 float:1.8151E-41 double:6.3996E-320;
        if (r11 > r5) goto L_0x0155;
    L_0x013c:
        r5 = com.hanista.mobogram.messenger.EmojiData.dataCharsMap;	 Catch:{ Exception -> 0x0247 }
        r15 = java.lang.Character.valueOf(r11);	 Catch:{ Exception -> 0x0247 }
        r5 = r5.containsKey(r15);	 Catch:{ Exception -> 0x0247 }
        if (r5 == 0) goto L_0x0155;
    L_0x0148:
        r3 = -1;
        if (r6 != r3) goto L_0x014c;
    L_0x014b:
        r6 = r12;
    L_0x014c:
        r4 = r4 + 1;
        r13.append(r11);	 Catch:{ Exception -> 0x0247 }
        r3 = 1;
        r5 = r7;
        goto L_0x0087;
    L_0x0155:
        r5 = -1;
        if (r6 == r5) goto L_0x0162;
    L_0x0158:
        r3 = 0;
        r13.setLength(r3);	 Catch:{ Exception -> 0x0247 }
        r6 = -1;
        r4 = 0;
        r3 = 0;
        r5 = r7;
        goto L_0x0087;
    L_0x0162:
        r5 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
        if (r11 == r5) goto L_0x0253;
    L_0x0167:
        if (r7 == 0) goto L_0x0253;
    L_0x0169:
        r5 = 0;
        r11 = 0;
        r7[r5] = r11;	 Catch:{ Exception -> 0x0247 }
        r5 = 0;
        goto L_0x0087;
    L_0x0170:
        r16 = 65024; // 0xfe00 float:9.1118E-41 double:3.2126E-319;
        r0 = r16;
        if (r15 < r0) goto L_0x00b5;
    L_0x0177:
        r16 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
        r0 = r16;
        if (r15 > r0) goto L_0x00b5;
    L_0x017e:
        r3 = r3 + 1;
        r7 = r7 + 1;
        goto L_0x00b5;
    L_0x0184:
        if (r4 == 0) goto L_0x022d;
    L_0x0186:
        if (r5 == 0) goto L_0x018f;
    L_0x0188:
        r4 = 0;
        r11 = r5[r4];	 Catch:{ Exception -> 0x0247 }
        r11 = r11 + 1;
        r5[r4] = r11;	 Catch:{ Exception -> 0x0247 }
    L_0x018f:
        r4 = r3 + 2;
        if (r4 >= r14) goto L_0x0250;
    L_0x0193:
        r4 = r3 + 1;
        r0 = r21;
        r4 = r0.charAt(r4);	 Catch:{ Exception -> 0x0247 }
        r11 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        if (r4 != r11) goto L_0x0250;
    L_0x01a0:
        r4 = r3 + 2;
        r0 = r21;
        r4 = r0.charAt(r4);	 Catch:{ Exception -> 0x0247 }
        r11 = 57339; // 0xdffb float:8.0349E-41 double:2.8329E-319;
        if (r4 < r11) goto L_0x0250;
    L_0x01ad:
        r4 = r3 + 2;
        r0 = r21;
        r4 = r0.charAt(r4);	 Catch:{ Exception -> 0x0247 }
        r11 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        if (r4 > r11) goto L_0x0250;
    L_0x01ba:
        r4 = r3 + 1;
        r11 = r3 + 3;
        r0 = r21;
        r4 = r0.subSequence(r4, r11);	 Catch:{ Exception -> 0x0247 }
        r13.append(r4);	 Catch:{ Exception -> 0x0247 }
        r7 = r7 + 2;
        r3 = r3 + 2;
        r4 = r7;
    L_0x01cc:
        r7 = r3 + 2;
        if (r7 >= r14) goto L_0x0205;
    L_0x01d0:
        r7 = r3 + 1;
        r0 = r21;
        r7 = r0.charAt(r7);	 Catch:{ Exception -> 0x0247 }
        r11 = 8205; // 0x200d float:1.1498E-41 double:4.054E-320;
        if (r7 != r11) goto L_0x0205;
    L_0x01dc:
        r7 = r3 + 2;
        r0 = r21;
        r7 = r0.charAt(r7);	 Catch:{ Exception -> 0x0247 }
        r11 = 9792; // 0x2640 float:1.3722E-41 double:4.838E-320;
        if (r7 == r11) goto L_0x01f4;
    L_0x01e8:
        r7 = r3 + 2;
        r0 = r21;
        r7 = r0.charAt(r7);	 Catch:{ Exception -> 0x0247 }
        r11 = 9794; // 0x2642 float:1.3724E-41 double:4.839E-320;
        if (r7 != r11) goto L_0x0205;
    L_0x01f4:
        r7 = r3 + 1;
        r11 = r3 + 3;
        r0 = r21;
        r7 = r0.subSequence(r7, r11);	 Catch:{ Exception -> 0x0247 }
        r13.append(r7);	 Catch:{ Exception -> 0x0247 }
        r4 = r4 + 2;
        r3 = r3 + 2;
    L_0x0205:
        r7 = 0;
        r11 = r13.length();	 Catch:{ Exception -> 0x0247 }
        r7 = r13.subSequence(r7, r11);	 Catch:{ Exception -> 0x0247 }
        r7 = getEmojiDrawable(r7);	 Catch:{ Exception -> 0x0247 }
        if (r7 == 0) goto L_0x0226;
    L_0x0214:
        r11 = new com.hanista.mobogram.messenger.Emoji$EmojiSpan;	 Catch:{ Exception -> 0x0247 }
        r15 = 0;
        r0 = r23;
        r1 = r22;
        r11.<init>(r7, r15, r0, r1);	 Catch:{ Exception -> 0x0247 }
        r4 = r4 + r6;
        r7 = 33;
        r2.setSpan(r11, r6, r4, r7);	 Catch:{ Exception -> 0x0247 }
        r10 = r10 + 1;
    L_0x0226:
        r7 = 0;
        r6 = -1;
        r4 = 0;
        r13.setLength(r4);	 Catch:{ Exception -> 0x0247 }
        r4 = 0;
    L_0x022d:
        r11 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0247 }
        r15 = 23;
        if (r11 >= r15) goto L_0x023b;
    L_0x0233:
        r11 = 50;
        if (r10 < r11) goto L_0x023b;
    L_0x0237:
        r21 = r2;
        goto L_0x0008;
    L_0x023b:
        r3 = r3 + 1;
        r20 = r3;
        r3 = r4;
        r4 = r7;
        r7 = r5;
        r5 = r12;
        r12 = r20;
        goto L_0x003a;
    L_0x0247:
        r2 = move-exception;
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r2);
        goto L_0x0008;
    L_0x0250:
        r4 = r7;
        goto L_0x01cc;
    L_0x0253:
        r5 = r7;
        goto L_0x0087;
    L_0x0256:
        r5 = r6;
        goto L_0x0128;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.Emoji.replaceEmoji(java.lang.CharSequence, android.graphics.Paint$FontMetricsInt, int, boolean, int[]):java.lang.CharSequence");
    }
}
