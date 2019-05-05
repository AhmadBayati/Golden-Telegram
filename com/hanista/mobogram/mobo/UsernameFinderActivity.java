package com.hanista.mobogram.mobo;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_checkUsername;
import com.hanista.mobogram.tgnet.TLRPC.TL_boolTrue;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.VideoPlayer;

/* renamed from: com.hanista.mobogram.mobo.s */
public class UsernameFinderActivity extends BaseFragment {
    private EditText f2521a;
    private View f2522b;
    private TextView f2523c;
    private TextView f2524d;
    private int f2525e;
    private String f2526f;
    private Runnable f2527g;
    private boolean f2528h;

    /* renamed from: com.hanista.mobogram.mobo.s.1 */
    class UsernameFinderActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ UsernameFinderActivity f2454a;

        UsernameFinderActivity(UsernameFinderActivity usernameFinderActivity) {
            this.f2454a = usernameFinderActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f2454a.finishFragment();
            } else if (i == 1) {
                MessagesController.openByUserName(this.f2454a.f2521a.getText().toString(), this.f2454a, 0);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.s.2 */
    class UsernameFinderActivity implements OnTouchListener {
        final /* synthetic */ UsernameFinderActivity f2455a;

        UsernameFinderActivity(UsernameFinderActivity usernameFinderActivity) {
            this.f2455a = usernameFinderActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.s.3 */
    class UsernameFinderActivity implements OnEditorActionListener {
        final /* synthetic */ UsernameFinderActivity f2456a;

        UsernameFinderActivity(UsernameFinderActivity usernameFinderActivity) {
            this.f2456a = usernameFinderActivity;
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || this.f2456a.f2522b == null) {
                return false;
            }
            this.f2456a.f2522b.performClick();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.s.4 */
    class UsernameFinderActivity implements TextWatcher {
        final /* synthetic */ UsernameFinderActivity f2457a;

        UsernameFinderActivity(UsernameFinderActivity usernameFinderActivity) {
            this.f2457a = usernameFinderActivity;
        }

        public void afterTextChanged(Editable editable) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            this.f2457a.m2502a(this.f2457a.f2521a.getText().toString(), false);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.s.5 */
    class UsernameFinderActivity implements Runnable {
        final /* synthetic */ String f2462a;
        final /* synthetic */ UsernameFinderActivity f2463b;

        /* renamed from: com.hanista.mobogram.mobo.s.5.1 */
        class UsernameFinderActivity implements RequestDelegate {
            final /* synthetic */ UsernameFinderActivity f2461a;

            /* renamed from: com.hanista.mobogram.mobo.s.5.1.1 */
            class UsernameFinderActivity implements Runnable {
                final /* synthetic */ TL_error f2458a;
                final /* synthetic */ TLObject f2459b;
                final /* synthetic */ UsernameFinderActivity f2460c;

                UsernameFinderActivity(UsernameFinderActivity usernameFinderActivity, TL_error tL_error, TLObject tLObject) {
                    this.f2460c = usernameFinderActivity;
                    this.f2458a = tL_error;
                    this.f2459b = tLObject;
                }

                public void run() {
                    this.f2460c.f2461a.f2463b.f2525e = 0;
                    if (this.f2460c.f2461a.f2463b.f2526f != null && this.f2460c.f2461a.f2463b.f2526f.equals(this.f2460c.f2461a.f2462a)) {
                        if (this.f2458a == null && (this.f2459b instanceof TL_boolTrue)) {
                            this.f2460c.f2461a.f2463b.f2523c.setText(LocaleController.getString("UsernameUnavailable", C0338R.string.UsernameUnavailable));
                            this.f2460c.f2461a.f2463b.f2523c.setTextColor(-3198928);
                            this.f2460c.f2461a.f2463b.f2528h = true;
                            return;
                        }
                        this.f2460c.f2461a.f2463b.f2523c.setText(LocaleController.formatString("UsernameIsAvailable", C0338R.string.UsernameIsAvailable, this.f2460c.f2461a.f2462a));
                        this.f2460c.f2461a.f2463b.f2523c.setTextColor(-14248148);
                        this.f2460c.f2461a.f2463b.f2528h = false;
                    }
                }
            }

            UsernameFinderActivity(UsernameFinderActivity usernameFinderActivity) {
                this.f2461a = usernameFinderActivity;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new UsernameFinderActivity(this, tL_error, tLObject));
            }
        }

        UsernameFinderActivity(UsernameFinderActivity usernameFinderActivity, String str) {
            this.f2463b = usernameFinderActivity;
            this.f2462a = str;
        }

        public void run() {
            TLObject tL_account_checkUsername = new TL_account_checkUsername();
            tL_account_checkUsername.username = this.f2462a;
            this.f2463b.f2525e = ConnectionsManager.getInstance().sendRequest(tL_account_checkUsername, new UsernameFinderActivity(this), 2);
        }
    }

    public UsernameFinderActivity() {
        this.f2525e = 0;
        this.f2526f = null;
        this.f2527g = null;
        this.f2528h = false;
    }

    private void m2498a() {
        if (ThemeUtil.m2490b()) {
            if (this.fragmentView != null) {
                this.fragmentView.setBackgroundColor(AdvanceTheme.f2497h);
            }
            if (this.f2521a != null) {
                if (this.f2521a.getBackground() != null) {
                    this.f2521a.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.f2521a.setHintTextColor(AdvanceTheme.f2495f);
                this.f2521a.setTextColor(AdvanceTheme.f2494e);
            }
            int i = AdvanceTheme.f2495f;
            int i2 = AdvanceTheme.f2496g;
            if (i2 != Theme.ACTION_BAR_MODE_SELECTOR_COLOR) {
                this.f2524d.setBackgroundColor(i2);
            } else {
                this.f2524d.setBackgroundResource(C0338R.drawable.greydivider);
            }
            this.f2524d.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
            this.f2524d.setTextColor(i);
            this.f2523c.setTextColor(AdvanceTheme.f2499j);
        }
    }

    private void m2499a(String str) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            Object obj = -1;
            switch (str.hashCode()) {
                case -141887186:
                    if (str.equals("USERNAMES_UNAVAILABLE")) {
                        obj = 2;
                        break;
                    }
                    break;
                case 288843630:
                    if (str.equals("USERNAME_INVALID")) {
                        obj = null;
                        break;
                    }
                    break;
                case 533175271:
                    if (str.equals("USERNAME_OCCUPIED")) {
                        obj = 1;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    builder.setMessage(LocaleController.getString("UsernameInvalid", C0338R.string.UsernameInvalid));
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    builder.setMessage(LocaleController.getString("UsernameInUse", C0338R.string.UsernameInUse));
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    builder.setMessage(LocaleController.getString("FeatureUnavailable", C0338R.string.FeatureUnavailable));
                    break;
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred));
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            showDialog(builder.create());
        }
    }

    private boolean m2502a(String str, boolean z) {
        if (str == null || str.length() <= 0) {
            this.f2523c.setVisibility(8);
        } else {
            this.f2523c.setVisibility(0);
        }
        if (z && str.length() == 0) {
            return true;
        }
        if (this.f2527g != null) {
            AndroidUtilities.cancelRunOnUIThread(this.f2527g);
            this.f2527g = null;
            this.f2526f = null;
            if (this.f2525e != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.f2525e, true);
            }
        }
        this.f2528h = false;
        if (str != null) {
            if (str.startsWith("_") || str.endsWith("_")) {
                this.f2523c.setText(LocaleController.getString("UsernameInvalid", C0338R.string.UsernameInvalid));
                this.f2523c.setTextColor(-3198928);
                return false;
            }
            int i = 0;
            while (i < str.length()) {
                char charAt = str.charAt(i);
                if (i != 0 || charAt < '0' || charAt > '9') {
                    if ((charAt >= '0' && charAt <= '9') || ((charAt >= 'a' && charAt <= 'z') || ((charAt >= 'A' && charAt <= 'Z') || charAt == '_'))) {
                        i++;
                    } else if (z) {
                        m2499a(LocaleController.getString("UsernameInvalid", C0338R.string.UsernameInvalid));
                        return false;
                    } else {
                        this.f2523c.setText(LocaleController.getString("UsernameInvalid", C0338R.string.UsernameInvalid));
                        this.f2523c.setTextColor(-3198928);
                        return false;
                    }
                } else if (z) {
                    m2499a(LocaleController.getString("UsernameInvalidStartNumber", C0338R.string.UsernameInvalidStartNumber));
                    return false;
                } else {
                    this.f2523c.setText(LocaleController.getString("UsernameInvalidStartNumber", C0338R.string.UsernameInvalidStartNumber));
                    this.f2523c.setTextColor(-3198928);
                    return false;
                }
            }
        }
        if (str == null || str.length() < 5) {
            if (z) {
                m2499a(LocaleController.getString("UsernameInvalidShort", C0338R.string.UsernameInvalidShort));
                return false;
            }
            this.f2523c.setText(LocaleController.getString("UsernameInvalidShort", C0338R.string.UsernameInvalidShort));
            this.f2523c.setTextColor(-3198928);
            return false;
        } else if (str.length() <= 32) {
            if (!z) {
                Object obj = UserConfig.getCurrentUser().username;
                if (obj == null) {
                    obj = TtmlNode.ANONYMOUS_REGION_ID;
                }
                if (str.equals(obj)) {
                    this.f2523c.setText(LocaleController.formatString("UsernameAvailable", C0338R.string.UsernameAvailable, str));
                    this.f2523c.setTextColor(-14248148);
                    return true;
                }
                this.f2523c.setText(LocaleController.getString("UsernameChecking", C0338R.string.UsernameChecking));
                this.f2523c.setTextColor(-9605774);
                this.f2526f = str;
                this.f2527g = new UsernameFinderActivity(this, str);
                AndroidUtilities.runOnUIThread(this.f2527g, 300);
            }
            return true;
        } else if (z) {
            m2499a(LocaleController.getString("UsernameInvalidLong", C0338R.string.UsernameInvalidLong));
            return false;
        } else {
            this.f2523c.setText(LocaleController.getString("UsernameInvalidLong", C0338R.string.UsernameInvalidLong));
            this.f2523c.setTextColor(-3198928);
            return false;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("UsernameFinder", C0338R.string.UsernameFinder));
        this.actionBar.setActionBarMenuOnItemClick(new UsernameFinderActivity(this));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            this.f2522b = createMenu.addItemWithWidth(1, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.f2522b = createMenu.addItemWithWidth(1, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        if (MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())) == null) {
            UserConfig.getCurrentUser();
        }
        this.fragmentView = new LinearLayout(context);
        initThemeBackground(this.fragmentView);
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener(new UsernameFinderActivity(this));
        this.f2521a = new EditText(context);
        this.f2521a.setTextSize(1, 18.0f);
        this.f2521a.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.f2521a.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.f2521a.setMaxLines(1);
        this.f2521a.setLines(1);
        this.f2521a.setPadding(0, 0, 0, 0);
        this.f2521a.setSingleLine(true);
        this.f2521a.setGravity(LocaleController.isRTL ? 5 : 3);
        this.f2521a.setInputType(180224);
        this.f2521a.setImeOptions(6);
        this.f2521a.setHint(LocaleController.getString("UsernameHint", C0338R.string.UsernameHint));
        AndroidUtilities.clearCursorDrawable(this.f2521a);
        this.f2521a.setOnEditorActionListener(new UsernameFinderActivity(this));
        ((LinearLayout) this.fragmentView).addView(this.f2521a, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.f2523c = new TextView(context);
        this.f2523c.setTextSize(1, 15.0f);
        this.f2523c.setGravity(LocaleController.isRTL ? 5 : 3);
        this.f2523c.setTypeface(FontUtil.m1176a().m1161d());
        ((LinearLayout) this.fragmentView).addView(this.f2523c, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 12, 24, 0));
        this.f2524d = new TextView(context);
        this.f2524d.setTextSize(1, 15.0f);
        this.f2524d.setTextColor(-9605774);
        this.f2524d.setGravity(LocaleController.isRTL ? 5 : 3);
        this.f2524d.setTypeface(FontUtil.m1176a().m1161d());
        this.f2524d.setText(AndroidUtilities.replaceTags(LocaleController.getString("UsernameFinderHelp", C0338R.string.UsernameFinderHelp)));
        ((LinearLayout) this.fragmentView).addView(this.f2524d, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        this.f2521a.addTextChangedListener(new UsernameFinderActivity(this));
        this.f2523c.setVisibility(8);
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).getBoolean("view_animations", true)) {
            this.f2521a.requestFocus();
            AndroidUtilities.showKeyboard(this.f2521a);
        }
        initThemeActionBar();
        m2498a();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.f2521a.requestFocus();
            AndroidUtilities.showKeyboard(this.f2521a);
        }
    }
}
