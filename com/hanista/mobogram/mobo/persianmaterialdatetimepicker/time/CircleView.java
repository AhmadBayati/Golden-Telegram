package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import com.hanista.mobogram.C0338R;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time.b */
public class CircleView extends View {
    private final Paint f2302a;
    private boolean f2303b;
    private int f2304c;
    private int f2305d;
    private float f2306e;
    private float f2307f;
    private boolean f2308g;
    private boolean f2309h;
    private int f2310i;
    private int f2311j;
    private int f2312k;

    public CircleView(Context context) {
        super(context);
        this.f2302a = new Paint();
        Resources resources = context.getResources();
        this.f2304c = resources.getColor(C0338R.color.mdtp_circle_color);
        this.f2305d = resources.getColor(C0338R.color.mdtp_numbers_text_color);
        this.f2302a.setAntiAlias(true);
        this.f2308g = false;
    }

    public void onDraw(Canvas canvas) {
        if (getWidth() != 0 && this.f2308g) {
            if (!this.f2309h) {
                this.f2310i = getWidth() / 2;
                this.f2311j = getHeight() / 2;
                this.f2312k = (int) (((float) Math.min(this.f2310i, this.f2311j)) * this.f2306e);
                if (!this.f2303b) {
                    this.f2311j = (int) (((double) this.f2311j) - (((double) ((int) (((float) this.f2312k) * this.f2307f))) * 0.75d));
                }
                this.f2309h = true;
            }
            this.f2302a.setColor(this.f2304c);
            canvas.drawCircle((float) this.f2310i, (float) this.f2311j, (float) this.f2312k, this.f2302a);
            this.f2302a.setColor(this.f2305d);
            canvas.drawCircle((float) this.f2310i, (float) this.f2311j, 4.0f, this.f2302a);
        }
    }
}
