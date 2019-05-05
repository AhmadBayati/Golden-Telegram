package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time.a */
public class AmPmCirclesView extends View {
    private final Paint f2283a;
    private int f2284b;
    private int f2285c;
    private int f2286d;
    private int f2287e;
    private int f2288f;
    private int f2289g;
    private float f2290h;
    private float f2291i;
    private String f2292j;
    private String f2293k;
    private boolean f2294l;
    private boolean f2295m;
    private int f2296n;
    private int f2297o;
    private int f2298p;
    private int f2299q;
    private int f2300r;
    private int f2301s;

    public AmPmCirclesView(Context context) {
        super(context);
        this.f2283a = new Paint();
        this.f2294l = false;
    }

    public int m2183a(float f, float f2) {
        if (!this.f2295m) {
            return -1;
        }
        int i = (int) ((f2 - ((float) this.f2299q)) * (f2 - ((float) this.f2299q)));
        if (((int) Math.sqrt((double) (((f - ((float) this.f2297o)) * (f - ((float) this.f2297o))) + ((float) i)))) <= this.f2296n) {
            return 0;
        }
        return ((int) Math.sqrt((double) (((float) i) + ((f - ((float) this.f2298p)) * (f - ((float) this.f2298p)))))) <= this.f2296n ? 1 : -1;
    }

    public void onDraw(Canvas canvas) {
        int i = NalUnitUtil.EXTENDED_SAR;
        if (getWidth() != 0 && this.f2294l) {
            int width;
            int height;
            int min;
            if (!this.f2295m) {
                width = getWidth() / 2;
                height = getHeight() / 2;
                min = (int) (((float) Math.min(width, height)) * this.f2290h);
                this.f2296n = (int) (((float) min) * this.f2291i);
                height = (int) (((double) height) + (((double) this.f2296n) * 0.75d));
                this.f2283a.setTextSize((float) ((this.f2296n * 3) / 4));
                this.f2299q = (height - (this.f2296n / 2)) + min;
                this.f2297o = (width - min) + this.f2296n;
                this.f2298p = (width + min) - this.f2296n;
                this.f2295m = true;
            }
            int i2 = this.f2286d;
            int i3 = this.f2287e;
            height = this.f2286d;
            width = this.f2287e;
            if (this.f2300r == 0) {
                i2 = this.f2289g;
                i3 = this.f2284b;
                min = this.f2288f;
            } else if (this.f2300r == 1) {
                min = this.f2289g;
                height = this.f2284b;
                width = this.f2288f;
                int i4 = height;
                height = min;
                min = i3;
                i3 = NalUnitUtil.EXTENDED_SAR;
                i = i4;
            } else {
                min = i3;
                i3 = NalUnitUtil.EXTENDED_SAR;
            }
            if (this.f2301s == 0) {
                i2 = this.f2285c;
                i3 = this.f2284b;
            } else if (this.f2301s == 1) {
                height = this.f2285c;
                i = this.f2284b;
            }
            this.f2283a.setColor(i2);
            this.f2283a.setAlpha(i3);
            canvas.drawCircle((float) this.f2297o, (float) this.f2299q, (float) this.f2296n, this.f2283a);
            this.f2283a.setColor(height);
            this.f2283a.setAlpha(i);
            canvas.drawCircle((float) this.f2298p, (float) this.f2299q, (float) this.f2296n, this.f2283a);
            this.f2283a.setColor(min);
            i = this.f2299q - (((int) (this.f2283a.descent() + this.f2283a.ascent())) / 2);
            canvas.drawText(this.f2292j, (float) this.f2297o, (float) i, this.f2283a);
            this.f2283a.setColor(width);
            canvas.drawText(this.f2293k, (float) this.f2298p, (float) i, this.f2283a);
        }
    }

    public void setAmOrPm(int i) {
        this.f2300r = i;
    }

    public void setAmOrPmPressed(int i) {
        this.f2301s = i;
    }
}
