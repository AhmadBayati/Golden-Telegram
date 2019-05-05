package com.hanista.mobogram.mobo.p020s;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.ui.ActionBar.SimpleTextView;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.LayoutHelper;

/* renamed from: com.hanista.mobogram.mobo.s.p */
public class ThemeCell extends FrameLayout {
    public ThemeCell(Context context, Theme theme) {
        super(context);
        View view = new View(context);
        view.setBackgroundColor(theme.m2289c());
        addView(view, LayoutHelper.createFrame(-1, 50.0f, 48, 0.0f, 10.0f, 0.0f, 0.0f));
        view = new View(context);
        view.setBackgroundColor(theme.m2290d());
        addView(view, LayoutHelper.createFrame(-1, 25.0f, 48, 0.0f, 50.0f, 0.0f, 0.0f));
        view = new View(context);
        view.setBackgroundColor(theme.m2292f());
        addView(view, LayoutHelper.createFrame(-1, BitmapDescriptorFactory.HUE_ORANGE, 0, 0.0f, 25.0f, 0.0f, 0.0f));
        view = new ImageView(context);
        view.setImageResource(C0338R.drawable.menu_themes_telegram);
        addView(view, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) 12, 20.0f, LocaleController.isRTL ? (float) 12 : 0.0f, 0.0f));
        view = new SimpleTextView(context);
        view.setTextColor(-1);
        view.setTextSize(17);
        view.setText(theme.m2288b());
        view.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(view, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) 73, 34.5f, LocaleController.isRTL ? (float) 73 : 28.0f, 0.0f));
        view = new CheckBox(context, C0338R.drawable.round_check2);
        int i = (ThemeUtil.m2490b() || ThemeUtil.m2485a().m2287a() != theme.m2287a()) ? 8 : 0;
        view.setVisibility(i);
        view.setChecked(ThemeUtil.m2485a().m2287a() == theme.m2287a(), false);
        addView(view, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) 42, 38.0f, LocaleController.isRTL ? (float) 42 : 0.0f, 0.0f));
    }
}
