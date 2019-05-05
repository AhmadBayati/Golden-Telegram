package com.hanista.mobogram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class ChannelIntroActivity extends BaseFragment {
    private TextView createChannelText;
    private TextView descriptionText;
    private ImageView imageView;
    private TextView whatIsChannelText;

    /* renamed from: com.hanista.mobogram.ui.ChannelIntroActivity.1 */
    class C12221 extends ActionBarMenuOnItemClick {
        C12221() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChannelIntroActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelIntroActivity.2 */
    class C12232 extends ViewGroup {
        C12232(Context context) {
            super(context);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5 = i3 - i;
            int i6 = i4 - i2;
            if (i3 > i4) {
                int i7 = (int) (((float) i6) * 0.05f);
                ChannelIntroActivity.this.imageView.layout(0, i7, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + i7);
                i7 = (int) (((float) i5) * 0.4f);
                int i8 = (int) (((float) i6) * 0.14f);
                ChannelIntroActivity.this.whatIsChannelText.layout(i7, i8, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth() + i7, ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + i8);
                i8 = (int) (((float) i6) * 0.61f);
                ChannelIntroActivity.this.createChannelText.layout(i7, i8, ChannelIntroActivity.this.createChannelText.getMeasuredWidth() + i7, ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + i8);
                i5 = (int) (((float) i5) * 0.45f);
                i6 = (int) (((float) i6) * 0.31f);
                ChannelIntroActivity.this.descriptionText.layout(i5, i6, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + i5, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + i6);
                return;
            }
            i7 = (int) (((float) i6) * 0.05f);
            ChannelIntroActivity.this.imageView.layout(0, i7, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + i7);
            i7 = (int) (((float) i6) * 0.59f);
            ChannelIntroActivity.this.whatIsChannelText.layout(0, i7, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth(), ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + i7);
            i7 = (int) (((float) i6) * 0.68f);
            i5 = (int) (((float) i5) * 0.05f);
            ChannelIntroActivity.this.descriptionText.layout(i5, i7, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + i5, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + i7);
            i5 = (int) (((float) i6) * 0.86f);
            ChannelIntroActivity.this.createChannelText.layout(0, i5, ChannelIntroActivity.this.createChannelText.getMeasuredWidth(), ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + i5);
        }

        protected void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            int size2 = MeasureSpec.getSize(i2);
            if (size > size2) {
                ChannelIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.45f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.78f), C0700C.ENCODING_PCM_32BIT));
                ChannelIntroActivity.this.whatIsChannelText.measure(MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.6f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(size2, 0));
                ChannelIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.5f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(size2, 0));
                ChannelIntroActivity.this.createChannelText.measure(MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.6f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), C0700C.ENCODING_PCM_32BIT));
            } else {
                ChannelIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.44f), C0700C.ENCODING_PCM_32BIT));
                ChannelIntroActivity.this.whatIsChannelText.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(size2, 0));
                ChannelIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.9f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(size2, 0));
                ChannelIntroActivity.this.createChannelText.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), C0700C.ENCODING_PCM_32BIT));
            }
            setMeasuredDimension(size, size2);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelIntroActivity.3 */
    class C12243 implements OnTouchListener {
        C12243() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelIntroActivity.4 */
    class C12254 implements OnClickListener {
        C12254() {
        }

        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putInt("step", 0);
            ChannelIntroActivity.this.presentFragment(new ChannelCreateActivity(bundle), true);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(-1);
        this.actionBar.setBackButtonImage(C0338R.drawable.pl_back);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR);
        this.actionBar.setCastShadows(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new C12221());
        this.fragmentView = new C12232(context);
        this.fragmentView.setBackgroundColor(-1);
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        viewGroup.setOnTouchListener(new C12243());
        this.imageView = new ImageView(context);
        this.imageView.setImageResource(C0338R.drawable.channelintro);
        this.imageView.setScaleType(ScaleType.FIT_CENTER);
        viewGroup.addView(this.imageView);
        this.whatIsChannelText = new TextView(context);
        this.whatIsChannelText.setTypeface(FontUtil.m1176a().m1161d());
        this.whatIsChannelText.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.whatIsChannelText.setGravity(1);
        this.whatIsChannelText.setTextSize(1, 24.0f);
        this.whatIsChannelText.setText(LocaleController.getString("ChannelAlertTitle", C0338R.string.ChannelAlertTitle));
        viewGroup.addView(this.whatIsChannelText);
        this.descriptionText = new TextView(context);
        this.descriptionText.setTypeface(FontUtil.m1176a().m1161d());
        this.descriptionText.setTextColor(-8882056);
        this.descriptionText.setGravity(1);
        this.descriptionText.setTextSize(1, 16.0f);
        this.descriptionText.setText(LocaleController.getString("ChannelAlertText", C0338R.string.ChannelAlertText));
        viewGroup.addView(this.descriptionText);
        this.createChannelText = new TextView(context);
        this.createChannelText.setTextColor(-11759926);
        this.createChannelText.setGravity(17);
        this.createChannelText.setTextSize(1, 16.0f);
        this.createChannelText.setTypeface(FontUtil.m1176a().m1160c());
        this.createChannelText.setText(LocaleController.getString("ChannelAlertCreate", C0338R.string.ChannelAlertCreate));
        viewGroup.addView(this.createChannelText);
        this.createChannelText.setOnClickListener(new C12254());
        return this.fragmentView;
    }
}
