package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class ChatBigEmptyView extends LinearLayout {
    private TextView secretViewStatusTextView;

    public ChatBigEmptyView(Context context, boolean z) {
        View imageView;
        super(context);
        setBackgroundResource(C0338R.drawable.system);
        getBackground().setColorFilter(Theme.colorFilter);
        setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        setOrientation(1);
        if (z) {
            this.secretViewStatusTextView = new TextView(context);
            this.secretViewStatusTextView.setTextSize(1, 15.0f);
            this.secretViewStatusTextView.setTextColor(-1);
            this.secretViewStatusTextView.setGravity(1);
            this.secretViewStatusTextView.setMaxWidth(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_AZURE));
            addView(this.secretViewStatusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else {
            imageView = new ImageView(context);
            imageView.setImageResource(C0338R.drawable.cloud_big);
            addView(imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 2, 0, 0));
        }
        imageView = new TextView(context);
        if (z) {
            imageView.setText(LocaleController.getString("EncryptedDescriptionTitle", C0338R.string.EncryptedDescriptionTitle));
            imageView.setTextSize(1, 15.0f);
        } else {
            imageView.setText(LocaleController.getString("ChatYourSelfTitle", C0338R.string.ChatYourSelfTitle));
            imageView.setTextSize(1, 16.0f);
            imageView.setTypeface(FontUtil.m1176a().m1160c());
            imageView.setGravity(1);
        }
        imageView.setTextColor(-1);
        imageView.setMaxWidth(AndroidUtilities.dp(260.0f));
        int i = z ? LocaleController.isRTL ? 5 : 3 : 1;
        addView(imageView, LayoutHelper.createLinear(-2, -2, i | 48, 0, 8, 0, z ? 0 : 8));
        for (int i2 = 0; i2 < 4; i2++) {
            View linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
            View imageView2 = new ImageView(context);
            imageView2.setImageResource(z ? C0338R.drawable.ic_lock_white : C0338R.drawable.list_circle);
            View textView = new TextView(context);
            textView.setTextSize(1, 15.0f);
            textView.setTextColor(-1);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView.setMaxWidth(AndroidUtilities.dp(260.0f));
            switch (i2) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    if (!z) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription1", C0338R.string.ChatYourSelfDescription1));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription1", C0338R.string.EncryptedDescription1));
                        break;
                    }
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    if (!z) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription2", C0338R.string.ChatYourSelfDescription2));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription2", C0338R.string.EncryptedDescription2));
                        break;
                    }
                case VideoPlayer.STATE_PREPARING /*2*/:
                    if (!z) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription3", C0338R.string.ChatYourSelfDescription3));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription3", C0338R.string.EncryptedDescription3));
                        break;
                    }
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    if (!z) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription4", C0338R.string.ChatYourSelfDescription4));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription4", C0338R.string.EncryptedDescription4));
                        break;
                    }
            }
            if (LocaleController.isRTL) {
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
                if (z) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                }
            } else {
                if (z) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                }
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
            }
        }
    }

    public void setSecretText(String str) {
        this.secretViewStatusTextView.setText(str);
    }
}
