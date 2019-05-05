package com.hanista.mobogram.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.IdenticonDrawable;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.URLSpanReplacement;

public class IdenticonActivity extends BaseFragment {
    private int chat_id;

    /* renamed from: com.hanista.mobogram.ui.IdenticonActivity.1 */
    class C15811 extends ActionBarMenuOnItemClick {
        C15811() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                IdenticonActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.IdenticonActivity.2 */
    class C15822 implements OnTouchListener {
        C15822() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.IdenticonActivity.3 */
    class C15833 implements OnPreDrawListener {
        C15833() {
        }

        public boolean onPreDraw() {
            if (IdenticonActivity.this.fragmentView != null) {
                IdenticonActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                LinearLayout linearLayout = (LinearLayout) IdenticonActivity.this.fragmentView;
                int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                if (rotation == 3 || rotation == 1) {
                    linearLayout.setOrientation(0);
                } else {
                    linearLayout.setOrientation(1);
                }
                IdenticonActivity.this.fragmentView.setPadding(IdenticonActivity.this.fragmentView.getPaddingLeft(), 0, IdenticonActivity.this.fragmentView.getPaddingRight(), IdenticonActivity.this.fragmentView.getPaddingBottom());
            }
            return true;
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(@NonNull TextView textView, @NonNull Spannable spannable, @NonNull MotionEvent motionEvent) {
            try {
                return super.onTouchEvent(textView, spannable, motionEvent);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                return false;
            }
        }
    }

    public IdenticonActivity(Bundle bundle) {
        super(bundle);
    }

    private void fixLayout() {
        this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new C15833());
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EncryptionKey", C0338R.string.EncryptionKey));
        this.actionBar.setActionBarMenuOnItemClick(new C15811());
        this.fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        linearLayout.setWeightSum(100.0f);
        linearLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        this.fragmentView.setOnTouchListener(new C15822());
        View frameLayout = new FrameLayout(context);
        frameLayout.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -1, 50.0f));
        View imageView = new ImageView(context);
        imageView.setScaleType(ScaleType.FIT_XY);
        frameLayout.addView(imageView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(-1);
        frameLayout.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -1, 50.0f));
        View textView = new TextView(context);
        textView.setTextColor(Theme.CHAT_BOTTOM_OVERLAY_TEXT_COLOR);
        textView.setTextSize(1, 16.0f);
        textView.setLinksClickable(true);
        textView.setClickable(true);
        textView.setMovementMethod(new LinkMovementMethodMy());
        textView.setLinkTextColor(Theme.MSG_LINK_TEXT_COLOR);
        textView.setGravity(17);
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(this.chat_id));
        if (encryptedChat != null) {
            int i;
            Drawable identiconDrawable = new IdenticonDrawable();
            imageView.setImageDrawable(identiconDrawable);
            identiconDrawable.setEncryptedChat(encryptedChat);
            User user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat.user_id));
            CharSequence spannableStringBuilder = new SpannableStringBuilder();
            if (encryptedChat.key_hash.length > 16) {
                String bytesToHex = Utilities.bytesToHex(encryptedChat.key_hash);
                for (i = 0; i < 32; i++) {
                    if (i != 0) {
                        if (i % 8 == 0) {
                            spannableStringBuilder.append('\n');
                        } else if (i % 4 == 0) {
                            spannableStringBuilder.append(' ');
                        }
                    }
                    spannableStringBuilder.append(bytesToHex.substring(i * 2, (i * 2) + 2));
                    spannableStringBuilder.append(' ');
                }
                spannableStringBuilder.append("\n\n");
            }
            spannableStringBuilder.append(AndroidUtilities.replaceTags(LocaleController.formatString("EncryptionKeyDescription", C0338R.string.EncryptionKeyDescription, user.first_name, user.first_name)));
            String str = "telegram.org";
            i = spannableStringBuilder.toString().indexOf("telegram.org");
            if (i != -1) {
                spannableStringBuilder.setSpan(new URLSpanReplacement(LocaleController.getString("EncryptionKeyLink", C0338R.string.EncryptionKeyLink)), i, "telegram.org".length() + i, 33);
            }
            textView.setText(spannableStringBuilder);
        }
        return this.fragmentView;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public boolean onFragmentCreate() {
        this.chat_id = getArguments().getInt("chat_id");
        return super.onFragmentCreate();
    }

    public void onResume() {
        super.onResume();
        fixLayout();
    }
}
