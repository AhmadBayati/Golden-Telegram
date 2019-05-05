package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.KeyboardButton;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonRow;
import com.hanista.mobogram.tgnet.TLRPC.TL_replyKeyboardMarkup;
import java.util.ArrayList;

public class BotKeyboardView extends LinearLayout {
    private TL_replyKeyboardMarkup botButtons;
    private int buttonHeight;
    private ArrayList<TextView> buttonViews;
    private LinearLayout container;
    private BotKeyboardViewDelegate delegate;
    private boolean isFullSize;
    private int panelHeight;

    /* renamed from: com.hanista.mobogram.ui.Components.BotKeyboardView.1 */
    class C12971 implements OnClickListener {
        C12971() {
        }

        public void onClick(View view) {
            BotKeyboardView.this.delegate.didPressedButton((KeyboardButton) view.getTag());
        }
    }

    public interface BotKeyboardViewDelegate {
        void didPressedButton(KeyboardButton keyboardButton);
    }

    public BotKeyboardView(Context context) {
        super(context);
        this.buttonViews = new ArrayList();
        setOrientation(1);
        View scrollView = new ScrollView(context);
        addView(scrollView);
        this.container = new LinearLayout(context);
        this.container.setOrientation(1);
        scrollView.addView(this.container);
        setBackgroundColor(-657673);
    }

    public int getKeyboardHeight() {
        return this.isFullSize ? this.panelHeight : ((this.botButtons.rows.size() * AndroidUtilities.dp((float) this.buttonHeight)) + AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f));
    }

    public void invalidateViews() {
        for (int i = 0; i < this.buttonViews.size(); i++) {
            ((TextView) this.buttonViews.get(i)).invalidate();
        }
    }

    public boolean isFullSize() {
        return this.isFullSize;
    }

    public void setButtons(TL_replyKeyboardMarkup tL_replyKeyboardMarkup) {
        this.botButtons = tL_replyKeyboardMarkup;
        this.container.removeAllViews();
        this.buttonViews.clear();
        if (ThemeUtil.m2490b()) {
            setBackgroundColor(AdvanceTheme.ch);
        }
        if (tL_replyKeyboardMarkup != null && this.botButtons.rows.size() != 0) {
            this.isFullSize = !tL_replyKeyboardMarkup.resize;
            this.buttonHeight = !this.isFullSize ? 42 : (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            int i = 0;
            while (i < tL_replyKeyboardMarkup.rows.size()) {
                TL_keyboardButtonRow tL_keyboardButtonRow = (TL_keyboardButtonRow) tL_replyKeyboardMarkup.rows.get(i);
                View linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(0);
                this.container.addView(linearLayout, LayoutHelper.createLinear(-1, this.buttonHeight, 15.0f, i == 0 ? 15.0f : 10.0f, 15.0f, i == tL_replyKeyboardMarkup.rows.size() + -1 ? 15.0f : 0.0f));
                float size = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ((float) tL_keyboardButtonRow.buttons.size());
                int i2 = 0;
                while (i2 < tL_keyboardButtonRow.buttons.size()) {
                    KeyboardButton keyboardButton = (KeyboardButton) tL_keyboardButtonRow.buttons.get(i2);
                    View textView = new TextView(getContext());
                    textView.setTypeface(FontUtil.m1176a().m1161d());
                    textView.setTag(keyboardButton);
                    textView.setTextColor(-13220017);
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity(17);
                    textView.setBackgroundResource(C0338R.drawable.bot_keyboard_states);
                    textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                    textView.setText(Emoji.replaceEmoji(keyboardButton.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                    linearLayout.addView(textView, LayoutHelper.createLinear(0, -1, size, 0, 0, i2 != tL_keyboardButtonRow.buttons.size() + -1 ? 10 : 0, 0));
                    textView.setOnClickListener(new C12971());
                    this.buttonViews.add(textView);
                    i2++;
                }
                i++;
            }
        }
    }

    public void setDelegate(BotKeyboardViewDelegate botKeyboardViewDelegate) {
        this.delegate = botKeyboardViewDelegate;
    }

    public void setPanelHeight(int i) {
        this.panelHeight = i;
        if (this.isFullSize && this.botButtons != null && this.botButtons.rows.size() != 0) {
            this.buttonHeight = !this.isFullSize ? 42 : (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            int childCount = this.container.getChildCount();
            int dp = AndroidUtilities.dp((float) this.buttonHeight);
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.container.getChildAt(i2);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.height != dp) {
                    layoutParams.height = dp;
                    childAt.setLayoutParams(layoutParams);
                }
            }
        }
    }
}
