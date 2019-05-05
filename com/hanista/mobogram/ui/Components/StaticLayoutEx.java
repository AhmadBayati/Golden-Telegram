package com.hanista.mobogram.ui.Components;

import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import com.hanista.mobogram.messenger.FileLog;
import java.lang.reflect.Constructor;

public class StaticLayoutEx {
    private static final String TEXT_DIRS_CLASS = "android.text.TextDirectionHeuristics";
    private static final String TEXT_DIR_CLASS = "android.text.TextDirectionHeuristic";
    private static final String TEXT_DIR_FIRSTSTRONG_LTR = "FIRSTSTRONG_LTR";
    private static boolean initialized;
    private static Constructor<StaticLayout> sConstructor;
    private static Object[] sConstructorArgs;
    private static Object sTextDirection;

    public static StaticLayout createStaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i4, int i5) {
        if (i5 == 1) {
            try {
                CharSequence ellipsize = TextUtils.ellipsize(charSequence, textPaint, (float) i4, TruncateAt.END);
                return new StaticLayout(ellipsize, 0, ellipsize.length(), textPaint, i3, alignment, f, f2, z);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                return null;
            }
        }
        StaticLayout staticLayout = new StaticLayout(charSequence, textPaint, i3, alignment, f, f2, z);
        if (staticLayout.getLineCount() <= i5) {
            return staticLayout;
        }
        int offsetForHorizontal;
        float lineLeft = staticLayout.getLineLeft(i5 - 1);
        if (lineLeft != 0.0f) {
            offsetForHorizontal = staticLayout.getOffsetForHorizontal(i5 - 1, lineLeft);
        } else {
            offsetForHorizontal = staticLayout.getOffsetForHorizontal(i5 - 1, staticLayout.getLineWidth(i5 - 1));
        }
        ellipsize = new SpannableStringBuilder(charSequence.subSequence(0, Math.max(0, offsetForHorizontal - 1)));
        ellipsize.append("\u2026");
        return new StaticLayout(ellipsize, textPaint, i3, alignment, f, f2, z);
    }

    public static StaticLayout createStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i2, int i3) {
        return createStaticLayout(charSequence, 0, charSequence.length(), textPaint, i, alignment, f, f2, z, truncateAt, i2, i3);
    }

    public static void init() {
        if (!initialized) {
            try {
                Class cls;
                if (VERSION.SDK_INT >= 18) {
                    cls = TextDirectionHeuristic.class;
                    sTextDirection = TextDirectionHeuristics.FIRSTSTRONG_LTR;
                } else {
                    ClassLoader classLoader = StaticLayoutEx.class.getClassLoader();
                    cls = classLoader.loadClass(TEXT_DIR_CLASS);
                    Class loadClass = classLoader.loadClass(TEXT_DIRS_CLASS);
                    sTextDirection = loadClass.getField(TEXT_DIR_FIRSTSTRONG_LTR).get(loadClass);
                }
                Class[] clsArr = new Class[]{CharSequence.class, Integer.TYPE, Integer.TYPE, TextPaint.class, Integer.TYPE, Alignment.class, cls, Float.TYPE, Float.TYPE, Boolean.TYPE, TruncateAt.class, Integer.TYPE, Integer.TYPE};
                sConstructor = StaticLayout.class.getDeclaredConstructor(clsArr);
                sConstructor.setAccessible(true);
                sConstructorArgs = new Object[clsArr.length];
                initialized = true;
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
            }
        }
    }
}
