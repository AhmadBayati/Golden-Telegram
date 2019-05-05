package com.hanista.mobogram.mobo.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.ui.ActionBar.Theme;

/* renamed from: com.hanista.mobogram.mobo.component.g */
public class RgbSelectorView extends LinearLayout {
    private SeekBar f456a;
    private SeekBar f457b;
    private SeekBar f458c;
    private SeekBar f459d;
    private ImageView f460e;
    private RgbSelectorView f461f;

    /* renamed from: com.hanista.mobogram.mobo.component.g.a */
    public interface RgbSelectorView {
        void m422a(int i);
    }

    /* renamed from: com.hanista.mobogram.mobo.component.g.1 */
    class RgbSelectorView implements OnSeekBarChangeListener {
        final /* synthetic */ RgbSelectorView f455a;

        RgbSelectorView(RgbSelectorView rgbSelectorView) {
            this.f455a = rgbSelectorView;
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            this.f455a.m510b();
            this.f455a.m512c();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    public RgbSelectorView(Context context) {
        super(context);
        m508a();
    }

    private void m508a() {
        View inflate = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C0338R.layout.color_rgbview, null);
        addView(inflate, new LayoutParams(-1, -1));
        OnSeekBarChangeListener rgbSelectorView = new RgbSelectorView(this);
        this.f456a = (SeekBar) inflate.findViewById(C0338R.id.color_rgb_seekRed);
        this.f456a.setOnSeekBarChangeListener(rgbSelectorView);
        this.f457b = (SeekBar) inflate.findViewById(C0338R.id.color_rgb_seekGreen);
        this.f457b.setOnSeekBarChangeListener(rgbSelectorView);
        this.f458c = (SeekBar) inflate.findViewById(C0338R.id.color_rgb_seekBlue);
        this.f458c.setOnSeekBarChangeListener(rgbSelectorView);
        this.f459d = (SeekBar) inflate.findViewById(C0338R.id.color_rgb_seekAlpha);
        this.f459d.setOnSeekBarChangeListener(rgbSelectorView);
        this.f460e = (ImageView) inflate.findViewById(C0338R.id.color_rgb_imgpreview);
        setColor(Theme.MSG_TEXT_COLOR);
    }

    private void m510b() {
        Bitmap createBitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
        createBitmap.setPixel(0, 0, getColor());
        this.f460e.setImageBitmap(createBitmap);
    }

    private void m512c() {
        if (this.f461f != null) {
            this.f461f.m422a(getColor());
        }
    }

    public int getColor() {
        return Color.argb(this.f459d.getProgress(), this.f456a.getProgress(), this.f457b.getProgress(), this.f458c.getProgress());
    }

    public void setColor(int i) {
        this.f459d.setProgress(Color.alpha(i));
        this.f456a.setProgress(Color.red(i));
        this.f457b.setProgress(Color.green(i));
        this.f458c.setProgress(Color.blue(i));
        m510b();
    }

    public void setOnColorChangedListener(RgbSelectorView rgbSelectorView) {
        this.f461f = rgbSelectorView;
    }
}
