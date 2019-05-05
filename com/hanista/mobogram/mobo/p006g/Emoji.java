package com.hanista.mobogram.mobo.p006g;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.ui.LaunchActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Locale;

/* renamed from: com.hanista.mobogram.mobo.g.a */
public class Emoji {
    private static HashMap<CharSequence, Emoji> f942a;
    private static int f943b;
    private static int f944c;
    private static boolean f945d;
    private static Paint f946e;
    private static Bitmap[][] f947f;
    private static boolean[][] f948g;
    private static final int[][] f949h;

    /* renamed from: com.hanista.mobogram.mobo.g.a.1 */
    static class Emoji implements Runnable {
        final /* synthetic */ int f929a;
        final /* synthetic */ int f930b;
        final /* synthetic */ Bitmap f931c;

        Emoji(int i, int i2, Bitmap bitmap) {
            this.f929a = i;
            this.f930b = i2;
            this.f931c = bitmap;
        }

        public void run() {
            Emoji.f947f[this.f929a][this.f930b] = this.f931c;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.emojiDidLoaded, new Object[0]);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.a.a */
    private static class Emoji {
        public Rect f932a;
        public byte f933b;
        public byte f934c;

        public Emoji(Rect rect, byte b, byte b2) {
            this.f932a = rect;
            this.f933b = b;
            this.f934c = b2;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.a.b */
    public static class Emoji extends Drawable {
        private static Paint f936c;
        private static Rect f937d;
        private Emoji f938a;
        private boolean f939b;

        /* renamed from: com.hanista.mobogram.mobo.g.a.b.1 */
        class Emoji implements Runnable {
            final /* synthetic */ Emoji f935a;

            Emoji(Emoji emoji) {
                this.f935a = emoji;
            }

            public void run() {
                Emoji.m1017b(this.f935a.f938a.f933b, this.f935a.f938a.f934c);
                Emoji.f948g[this.f935a.f938a.f933b][this.f935a.f938a.f934c] = false;
            }
        }

        static {
            f936c = new Paint(2);
            f937d = new Rect();
        }

        public Emoji(Emoji emoji) {
            this.f939b = false;
            this.f938a = emoji;
        }

        public Rect m1010a() {
            Rect bounds = getBounds();
            int centerX = bounds.centerX();
            int centerY = bounds.centerY();
            f937d.left = centerX - ((this.f939b ? Emoji.f944c : Emoji.f943b) / 2);
            f937d.right = ((this.f939b ? Emoji.f944c : Emoji.f943b) / 2) + centerX;
            f937d.top = centerY - ((this.f939b ? Emoji.f944c : Emoji.f943b) / 2);
            f937d.bottom = ((this.f939b ? Emoji.f944c : Emoji.f943b) / 2) + centerY;
            return f937d;
        }

        public void draw(Canvas canvas) {
            if (Emoji.f947f[this.f938a.f933b][this.f938a.f934c] != null) {
                canvas.drawBitmap(Emoji.f947f[this.f938a.f933b][this.f938a.f934c], this.f938a.f932a, this.f939b ? m1010a() : getBounds(), f936c);
            } else if (!Emoji.f948g[this.f938a.f933b][this.f938a.f934c]) {
                Emoji.f948g[this.f938a.f933b][this.f938a.f934c] = true;
                Utilities.globalQueue.postRunnable(new Emoji(this));
                canvas.drawRect(getBounds(), Emoji.f946e);
            }
        }

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.a.c */
    public static class Emoji extends ImageSpan {
        private FontMetricsInt f940a;
        private int f941b;

        public Emoji(Emoji emoji, int i, int i2, FontMetricsInt fontMetricsInt) {
            super(emoji, i);
            this.f940a = null;
            this.f941b = AndroidUtilities.dp(20.0f);
            this.f940a = fontMetricsInt;
            if (fontMetricsInt != null) {
                this.f941b = Math.abs(this.f940a.descent) + Math.abs(this.f940a.ascent);
                if (this.f941b == 0) {
                    this.f941b = AndroidUtilities.dp(20.0f);
                }
            }
        }

        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fontMetricsInt) {
            FontMetricsInt fontMetricsInt2 = fontMetricsInt == null ? new FontMetricsInt() : fontMetricsInt;
            if (this.f940a == null) {
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
                fontMetricsInt2.ascent = this.f940a.ascent;
                fontMetricsInt2.descent = this.f940a.descent;
                fontMetricsInt2.top = this.f940a.top;
                fontMetricsInt2.bottom = this.f940a.bottom;
            }
            if (getDrawable() != null) {
                getDrawable().setBounds(0, 0, this.f941b, this.f941b);
            }
            return this.f941b;
        }
    }

    static {
        f942a = new HashMap();
        f945d = false;
        f947f = (Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{5, 4});
        f948g = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{5, 4});
        f949h = new int[][]{new int[]{11, 11, 11, 11}, new int[]{6, 6, 6, 6}, new int[]{9, 9, 9, 9}, new int[]{9, 9, 9, 9}, new int[]{8, 8, 8, 7}};
        int i = AndroidUtilities.density <= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ? 32 : AndroidUtilities.density <= 1.5f ? 48 : AndroidUtilities.density <= 2.0f ? 64 : 64;
        f943b = AndroidUtilities.dp(20.0f);
        f944c = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        for (int i2 = 0; i2 < EmojiData.f954e.length; i2++) {
            int ceil = (int) Math.ceil((double) (((float) EmojiData.f954e[i2].length) / 4.0f));
            for (int i3 = 0; i3 < EmojiData.f954e[i2].length; i3++) {
                int i4 = i3 / ceil;
                int i5 = i3 - (i4 * ceil);
                f942a.put(EmojiData.f954e[i2][i3], new Emoji(new Rect((i5 % f949h[i2][i4]) * i, (i5 / f949h[i2][i4]) * i, ((i5 % f949h[i2][i4]) + 1) * i, ((i5 / f949h[i2][i4]) + 1) * i), (byte) i2, (byte) i4));
            }
        }
        f946e = new Paint();
        f946e.setColor(0);
    }

    public static Emoji m1011a(CharSequence charSequence) {
        Emoji emoji = (Emoji) f942a.get(charSequence);
        if (emoji == null) {
            FileLog.m16e("tmessages", "No drawable for emoji " + charSequence);
            return null;
        }
        Emoji emoji2 = new Emoji(emoji);
        emoji2.setBounds(0, 0, f943b, f943b);
        return emoji2;
    }

    public static CharSequence m1012a(CharSequence charSequence, FontMetricsInt fontMetricsInt, int i, boolean z) {
        if (charSequence == null || charSequence.length() == 0) {
            return charSequence;
        }
        Spannable newSpannable = (z || !(charSequence instanceof Spannable)) ? Factory.getInstance().newSpannable(charSequence.toString()) : (Spannable) charSequence;
        long j = 0;
        int i2 = 0;
        int i3 = -1;
        int i4 = 0;
        int i5 = 0;
        StringBuilder stringBuilder = new StringBuilder(16);
        int length = charSequence.length();
        Object obj = null;
        int i6 = 0;
        while (i6 < length) {
            char charAt;
            char charAt2 = charSequence.charAt(i6);
            if ((charAt2 >= '\ud83c' && charAt2 <= '\ud83e') || (j != 0 && (-4294967296L & j) == 0 && (65535 & j) == 55356 && charAt2 >= '\udde6' && charAt2 <= '\uddff')) {
                if (i3 == -1) {
                    i3 = i6;
                }
                stringBuilder.append(charAt2);
                i4++;
                j = (j << 16) | ((long) charAt2);
            } else if (j > 0 && (61440 & charAt2) == 53248) {
                try {
                    stringBuilder.append(charAt2);
                    i4++;
                    j = 0;
                    obj = 1;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    return charSequence;
                }
            } else if (charAt2 == '\u20e3') {
                if (i6 > 0) {
                    charAt = charSequence.charAt(i5);
                    if ((charAt >= '0' && charAt <= '9') || charAt == '#' || charAt == '*') {
                        i4 = (i6 - i5) + 1;
                        stringBuilder.append(charAt);
                        stringBuilder.append(charAt2);
                        obj = 1;
                    } else {
                        i5 = i3;
                    }
                    i3 = i5;
                }
            } else if ((charAt2 == '\u00a9' || charAt2 == '\u00ae' || (charAt2 >= '\u203c' && charAt2 <= '\u3299')) && EmojiData.f956g.containsKey(Character.valueOf(charAt2))) {
                if (i3 == -1) {
                    i3 = i6;
                }
                i4++;
                stringBuilder.append(charAt2);
                obj = 1;
            } else if (i3 != -1) {
                stringBuilder.setLength(0);
                i3 = -1;
                i4 = 0;
                obj = null;
            }
            i5 = i4;
            Object obj2 = obj;
            int i7 = i6;
            for (int i8 = 0; i8 < 3; i8++) {
                if (i7 + 1 < length) {
                    charAt = charSequence.charAt(i7 + 1);
                    if (i8 == 1) {
                        if (charAt == '\u200d') {
                            stringBuilder.append(charAt);
                            i7++;
                            i5++;
                            obj2 = null;
                        }
                    } else if (charAt >= '\ufe00' && charAt <= '\ufe0f') {
                        i7++;
                        i5++;
                    }
                }
            }
            if (obj2 != null) {
                if (i7 + 2 < length) {
                    if (charSequence.charAt(i7 + 1) == '\ud83c') {
                        if (charSequence.charAt(i7 + 2) >= '\udffb') {
                            if (charSequence.charAt(i7 + 2) <= '\udfff') {
                                stringBuilder.append(charSequence.subSequence(i7 + 1, i7 + 3));
                                i5 += 2;
                                i7 += 2;
                            }
                        }
                    }
                }
                Emoji a = Emoji.m1011a(stringBuilder.subSequence(0, stringBuilder.length()));
                if (a != null) {
                    newSpannable.setSpan(new Emoji(a, 0, i, fontMetricsInt), i3, i3 + i5, 33);
                    i2++;
                }
                i5 = 0;
                i3 = -1;
                stringBuilder.setLength(0);
                obj2 = null;
            }
            if (i2 >= 50) {
                break;
            }
            int i9 = i7 + 1;
            obj = obj2;
            i4 = i5;
            i5 = i6;
            i6 = i9;
        }
        return newSpannable;
    }

    public static String m1013a(String str) {
        int length = str.length();
        int i = 0;
        String str2 = str;
        while (i < length) {
            char charAt = str2.charAt(i);
            if (charAt < '\ud83c' || charAt > '\ud83e') {
                if (charAt == '\u20e3') {
                    break;
                } else if (charAt >= '\u203c' && charAt <= '\u3299' && EmojiData.f955f.containsKey(Character.valueOf(charAt))) {
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

    public static void m1014a() {
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
        edit.putBoolean("use_old_emojis", false);
        edit.commit();
        MoboConstants.m1379a();
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 1234567, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }

    public static Drawable m1016b(String str) {
        Drawable a = Emoji.m1011a((CharSequence) str);
        if (a == null) {
            return null;
        }
        a.setBounds(0, 0, f944c, f944c);
        a.f939b = true;
        return a;
    }

    private static void m1017b(int i, int i2) {
        float f;
        File fileStreamPath;
        int i3 = 2;
        try {
            if (AndroidUtilities.density <= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                f = 2.0f;
            } else if (AndroidUtilities.density <= 1.5f) {
                f = 3.0f;
            } else if (AndroidUtilities.density <= 2.0f) {
                i3 = 1;
                f = 2.0f;
            } else {
                i3 = 1;
                f = 2.0f;
            }
            for (int i4 = 4; i4 < 6; i4++) {
                fileStreamPath = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.jpg", new Object[]{Integer.valueOf(i4), Float.valueOf(f), Integer.valueOf(i)}));
                if (fileStreamPath.exists()) {
                    fileStreamPath.delete();
                }
                fileStreamPath = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_a_%d.jpg", new Object[]{Integer.valueOf(i4), Float.valueOf(f), Integer.valueOf(i)}));
                if (fileStreamPath.exists()) {
                    fileStreamPath.delete();
                }
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        } catch (Throwable e2) {
            FileLog.m17e("tmessages", "Error loading emoji", e2);
            Emoji.m1014a();
            return;
        }
        String format = String.format(Locale.US, "v7_emoji%.01fx_%d_%d.jpg", new Object[]{Float.valueOf(f), Integer.valueOf(i), Integer.valueOf(i2)});
        File fileStreamPath2 = ApplicationLoader.applicationContext.getFileStreamPath(format);
        if (!fileStreamPath2.exists()) {
            fileStreamPath = new File(Environment.getExternalStorageDirectory(), "MoboPlus");
            if (!fileStreamPath.exists()) {
                Emoji.m1014a();
            }
            File file = new File(fileStreamPath, "emoji");
            if (!file.exists()) {
                Emoji.m1014a();
            }
            fileStreamPath = new File(file, format);
            if (!fileStreamPath.exists()) {
                Emoji.m1014a();
            }
            InputStream fileInputStream = new FileInputStream(fileStreamPath);
            AndroidUtilities.copyFile(fileInputStream, fileStreamPath2);
            fileInputStream.close();
        }
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileStreamPath2.getAbsolutePath(), options);
        int i5 = options.outWidth / i3;
        int i6 = options.outHeight / i3;
        int i7 = i5 * 4;
        Bitmap createBitmap = Bitmap.createBitmap(i5, i6, Config.ARGB_8888);
        Utilities.loadBitmap(fileStreamPath2.getAbsolutePath(), createBitmap, i3, i5, i6, i7);
        format = String.format(Locale.US, "v7_emoji%.01fx_a_%d_%d.jpg", new Object[]{Float.valueOf(f), Integer.valueOf(i), Integer.valueOf(i2)});
        File fileStreamPath3 = ApplicationLoader.applicationContext.getFileStreamPath(format);
        if (!fileStreamPath3.exists()) {
            fileStreamPath2 = new File(Environment.getExternalStorageDirectory(), "MoboPlus");
            if (!fileStreamPath2.exists()) {
                Emoji.m1014a();
            }
            File file2 = new File(fileStreamPath2, "emoji");
            if (!file2.exists()) {
                Emoji.m1014a();
            }
            fileStreamPath2 = new File(file2, format);
            if (!fileStreamPath2.exists()) {
                Emoji.m1014a();
            }
            fileInputStream = new FileInputStream(fileStreamPath2);
            AndroidUtilities.copyFile(fileInputStream, fileStreamPath3);
            fileInputStream.close();
        }
        Utilities.loadBitmap(fileStreamPath3.getAbsolutePath(), createBitmap, i3, i5, i6, i7);
        AndroidUtilities.runOnUIThread(new Emoji(i, i2, createBitmap));
    }
}
