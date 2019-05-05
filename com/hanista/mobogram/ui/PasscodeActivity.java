package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.lock.LockActivity;
import com.hanista.mobogram.mobo.lock.LockPreferences;
import com.hanista.mobogram.mobo.lock.PrefUtils;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailCheckCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.NumberPicker;
import com.hanista.mobogram.ui.Components.NumberPicker.Formatter;

public class PasscodeActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private static final int password_item = 3;
    private static final int pattern_item = 4;
    private static final int pin_item = 2;
    private int autoLockDetailRow;
    private int autoLockRow;
    private int changePasscodeRow;
    private int currentPasswordType;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private int fingerprintRow;
    private String firstPassword;
    private ListAdapter listAdapter;
    private ListView listView;
    private int passcodeDetailRow;
    private int passcodeRow;
    private int passcodeSetStep;
    private EditText passwordEditText;
    private int patternHideWrongRow;
    private int patternLockRow;
    private int patternSectionRow;
    private int patternSectionRow2;
    private int patternSilentModeRow;
    private int patternSizeRow;
    private int patternVibrateRow;
    private int rowCount;
    private TextView titleTextView;
    private int type;

    /* renamed from: com.hanista.mobogram.ui.PasscodeActivity.1 */
    class C17401 extends ActionBarMenuOnItemClick {
        C17401() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PasscodeActivity.this.finishFragment();
            } else if (i == PasscodeActivity.done_button) {
                if (PasscodeActivity.this.passcodeSetStep == 0) {
                    PasscodeActivity.this.processNext();
                } else if (PasscodeActivity.this.passcodeSetStep == PasscodeActivity.done_button) {
                    PasscodeActivity.this.processDone();
                }
            } else if (i == PasscodeActivity.pin_item) {
                PasscodeActivity.this.currentPasswordType = 0;
                PasscodeActivity.this.updateDropDownTextView();
            } else if (i == PasscodeActivity.password_item) {
                PasscodeActivity.this.currentPasswordType = PasscodeActivity.done_button;
                PasscodeActivity.this.updateDropDownTextView();
            } else if (i == PasscodeActivity.pattern_item) {
                PasscodeActivity.this.showCreatePattern();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PasscodeActivity.2 */
    class C17412 implements OnEditorActionListener {
        C17412() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (PasscodeActivity.this.passcodeSetStep == 0) {
                PasscodeActivity.this.processNext();
                return true;
            } else if (PasscodeActivity.this.passcodeSetStep != PasscodeActivity.done_button) {
                return false;
            } else {
                PasscodeActivity.this.processDone();
                return true;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PasscodeActivity.3 */
    class C17423 implements TextWatcher {
        C17423() {
        }

        public void afterTextChanged(Editable editable) {
            if (PasscodeActivity.this.passwordEditText.length() != PasscodeActivity.pattern_item) {
                return;
            }
            if (PasscodeActivity.this.type == PasscodeActivity.pin_item && UserConfig.passcodeType == 0) {
                PasscodeActivity.this.processDone();
            } else if (PasscodeActivity.this.type != PasscodeActivity.done_button || PasscodeActivity.this.currentPasswordType != 0) {
            } else {
                if (PasscodeActivity.this.passcodeSetStep == 0) {
                    PasscodeActivity.this.processNext();
                } else if (PasscodeActivity.this.passcodeSetStep == PasscodeActivity.done_button) {
                    PasscodeActivity.this.processDone();
                }
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PasscodeActivity.4 */
    class C17434 implements Callback {
        C17434() {
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PasscodeActivity.5 */
    class C17445 implements OnClickListener {
        C17445() {
        }

        public void onClick(View view) {
            PasscodeActivity.this.dropDownContainer.toggleSubMenu();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PasscodeActivity.6 */
    class C17486 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.PasscodeActivity.6.1 */
        class C17451 implements Formatter {
            C17451() {
            }

            public String format(int i) {
                if (i == 0) {
                    return LocaleController.getString("AutoLockDisabled", C0338R.string.AutoLockDisabled);
                }
                Object[] objArr;
                if (i == PasscodeActivity.done_button) {
                    objArr = new Object[PasscodeActivity.done_button];
                    objArr[0] = LocaleController.formatPluralString("Minutes", PasscodeActivity.done_button);
                    return LocaleController.formatString("AutoLockInTime", C0338R.string.AutoLockInTime, objArr);
                } else if (i == PasscodeActivity.pin_item) {
                    objArr = new Object[PasscodeActivity.done_button];
                    objArr[0] = LocaleController.formatPluralString("Minutes", 5);
                    return LocaleController.formatString("AutoLockInTime", C0338R.string.AutoLockInTime, objArr);
                } else if (i == PasscodeActivity.password_item) {
                    objArr = new Object[PasscodeActivity.done_button];
                    objArr[0] = LocaleController.formatPluralString("Hours", PasscodeActivity.done_button);
                    return LocaleController.formatString("AutoLockInTime", C0338R.string.AutoLockInTime, objArr);
                } else if (i != PasscodeActivity.pattern_item) {
                    return TtmlNode.ANONYMOUS_REGION_ID;
                } else {
                    objArr = new Object[PasscodeActivity.done_button];
                    objArr[0] = LocaleController.formatPluralString("Hours", 5);
                    return LocaleController.formatString("AutoLockInTime", C0338R.string.AutoLockInTime, objArr);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.PasscodeActivity.6.2 */
        class C17462 implements DialogInterface.OnClickListener {
            final /* synthetic */ NumberPicker val$numberPicker;

            C17462(NumberPicker numberPicker) {
                this.val$numberPicker = numberPicker;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                int value = this.val$numberPicker.getValue();
                if (value == 0) {
                    UserConfig.autoLockIn = 0;
                } else if (value == PasscodeActivity.done_button) {
                    UserConfig.autoLockIn = 60;
                } else if (value == PasscodeActivity.pin_item) {
                    UserConfig.autoLockIn = 300;
                } else if (value == PasscodeActivity.password_item) {
                    UserConfig.autoLockIn = 3600;
                } else if (value == PasscodeActivity.pattern_item) {
                    UserConfig.autoLockIn = 18000;
                }
                PasscodeActivity.this.listView.invalidateViews();
                UserConfig.saveConfig(false);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.PasscodeActivity.6.3 */
        class C17473 implements DialogInterface.OnClickListener {
            final /* synthetic */ NumberPicker val$numberPicker;

            C17473(NumberPicker numberPicker) {
                this.val$numberPicker = numberPicker;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (UserConfig.passcodeHash.length() <= 0 || UserConfig.passcodeType != PasscodeActivity.pin_item) {
                    PrefUtils prefUtils = new PrefUtils(PasscodeActivity.this.getParentActivity());
                    prefUtils.m1579a((int) C0338R.string.pref_key_pattern_size, String.valueOf(this.val$numberPicker.getValue()));
                    prefUtils.m1583b();
                    if (PasscodeActivity.this.listView != null) {
                        PasscodeActivity.this.listView.invalidateViews();
                        return;
                    }
                    return;
                }
                PasscodeActivity.this.presentFragment(new LockActivity(PasscodeActivity.done_button, this.val$numberPicker.getValue()));
            }
        }

        C17486() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            if (i == PasscodeActivity.this.changePasscodeRow) {
                PasscodeActivity.this.presentFragment(new PasscodeActivity(PasscodeActivity.done_button));
            } else if (i == PasscodeActivity.this.passcodeRow) {
                r10 = (TextCheckCell) view;
                if (UserConfig.passcodeHash.length() != 0) {
                    UserConfig.passcodeHash = TtmlNode.ANONYMOUS_REGION_ID;
                    UserConfig.appLocked = false;
                    UserConfig.saveConfig(false);
                    r4 = PasscodeActivity.this.listView.getChildCount();
                    for (r3 = 0; r3 < r4; r3 += PasscodeActivity.done_button) {
                        r0 = PasscodeActivity.this.listView.getChildAt(r3);
                        if (r0 instanceof TextSettingsCell) {
                            ((TextSettingsCell) r0).setTextColor(-3750202);
                            break;
                        }
                    }
                    r10.setChecked(UserConfig.passcodeHash.length() != 0);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                    return;
                }
                PasscodeActivity.this.presentFragment(new PasscodeActivity(PasscodeActivity.done_button));
            } else if (i == PasscodeActivity.this.patternLockRow) {
                r10 = (TextCheckCell) view;
                if (UserConfig.passcodeHash.length() == 0 || UserConfig.passcodeType != PasscodeActivity.pin_item) {
                    PasscodeActivity.this.presentFragment(new LockActivity(PasscodeActivity.done_button));
                    return;
                }
                UserConfig.passcodeHash = TtmlNode.ANONYMOUS_REGION_ID;
                UserConfig.appLocked = false;
                UserConfig.saveConfig(false);
                r4 = PasscodeActivity.this.listView.getChildCount();
                for (r3 = 0; r3 < r4; r3 += PasscodeActivity.done_button) {
                    r0 = PasscodeActivity.this.listView.getChildAt(r3);
                    if (r0 instanceof TextSettingsCell) {
                        ((TextSettingsCell) r0).setTextColor(-3750202);
                        break;
                    }
                }
                if (UserConfig.passcodeHash.length() == 0) {
                    z = false;
                }
                r10.setChecked(z);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
            } else if (i == PasscodeActivity.this.autoLockRow) {
                if (PasscodeActivity.this.getParentActivity() != null) {
                    r0 = new Builder(PasscodeActivity.this.getParentActivity());
                    r0.setTitle(LocaleController.getString("AutoLock", C0338R.string.AutoLock));
                    View numberPicker = new NumberPicker(PasscodeActivity.this.getParentActivity());
                    numberPicker.setMinValue(0);
                    numberPicker.setMaxValue(PasscodeActivity.pattern_item);
                    if (UserConfig.autoLockIn == 0) {
                        numberPicker.setValue(0);
                    } else if (UserConfig.autoLockIn == 60) {
                        numberPicker.setValue(PasscodeActivity.done_button);
                    } else if (UserConfig.autoLockIn == 300) {
                        numberPicker.setValue(PasscodeActivity.pin_item);
                    } else if (UserConfig.autoLockIn == 3600) {
                        numberPicker.setValue(PasscodeActivity.password_item);
                    } else if (UserConfig.autoLockIn == 18000) {
                        numberPicker.setValue(PasscodeActivity.pattern_item);
                    }
                    numberPicker.setFormatter(new C17451());
                    r0.setView(numberPicker);
                    r0.setNegativeButton(LocaleController.getString("Done", C0338R.string.Done), new C17462(numberPicker));
                    PasscodeActivity.this.showDialog(r0.create());
                }
            } else if (i == PasscodeActivity.this.fingerprintRow) {
                if (UserConfig.useFingerprint) {
                    z = false;
                }
                UserConfig.useFingerprint = z;
                UserConfig.saveConfig(false);
                ((TextCheckCell) view).setChecked(UserConfig.useFingerprint);
            } else if (i == PasscodeActivity.this.patternVibrateRow) {
                r3 = new LockPreferences(PasscodeActivity.this.getParentActivity());
                r4 = new PrefUtils(PasscodeActivity.this.getParentActivity());
                r4.m1579a((int) C0338R.string.pref_key_vibrate, Boolean.valueOf(!r3.f1543c.booleanValue()));
                r4.m1583b();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (r3.f1543c.booleanValue()) {
                        z = false;
                    }
                    r10.setChecked(z);
                }
            } else if (i == PasscodeActivity.this.patternSilentModeRow) {
                r3 = new LockPreferences(PasscodeActivity.this.getParentActivity());
                r4 = new PrefUtils(PasscodeActivity.this.getParentActivity());
                r4.m1579a((int) C0338R.string.pref_key_pattern_stealth, Boolean.valueOf(!r3.f1554n));
                r4.m1583b();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (r3.f1554n) {
                        z = false;
                    }
                    r10.setChecked(z);
                }
            } else if (i == PasscodeActivity.this.patternHideWrongRow) {
                r3 = new LockPreferences(PasscodeActivity.this.getParentActivity());
                r4 = new PrefUtils(PasscodeActivity.this.getParentActivity());
                r4.m1579a((int) C0338R.string.pref_key_pattern_hide_error, Boolean.valueOf(!r3.f1555o));
                r4.m1583b();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (r3.f1555o) {
                        z = false;
                    }
                    r10.setChecked(z);
                }
            } else if (i == PasscodeActivity.this.patternSizeRow) {
                r0 = new Builder(PasscodeActivity.this.getParentActivity());
                r0.setTitle(LocaleController.getString("PatternSize", C0338R.string.PatternSize));
                View numberPicker2 = new NumberPicker(PasscodeActivity.this.getParentActivity());
                numberPicker2.setMinValue(PasscodeActivity.pin_item);
                numberPicker2.setMaxValue(10);
                numberPicker2.setValue(new LockPreferences(PasscodeActivity.this.getParentActivity()).f1545e);
                r0.setView(numberPicker2);
                r0.setNegativeButton(LocaleController.getString("Done", C0338R.string.Done), new C17473(numberPicker2));
                PasscodeActivity.this.showDialog(r0.create());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PasscodeActivity.7 */
    class C17497 implements Runnable {
        C17497() {
        }

        public void run() {
            if (PasscodeActivity.this.passwordEditText != null) {
                PasscodeActivity.this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(PasscodeActivity.this.passwordEditText);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PasscodeActivity.8 */
    class C17508 implements OnPreDrawListener {
        C17508() {
        }

        public boolean onPreDraw() {
            PasscodeActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            PasscodeActivity.this.fixLayoutInternal();
            return true;
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return PasscodeActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == PasscodeActivity.this.passcodeRow || i == PasscodeActivity.this.fingerprintRow || i == PasscodeActivity.this.patternLockRow) ? 0 : (i == PasscodeActivity.this.changePasscodeRow || i == PasscodeActivity.this.autoLockRow || i == PasscodeActivity.this.patternSizeRow) ? PasscodeActivity.done_button : (i == PasscodeActivity.this.passcodeDetailRow || i == PasscodeActivity.this.autoLockDetailRow) ? PasscodeActivity.pin_item : i == PasscodeActivity.this.patternSectionRow2 ? PasscodeActivity.pattern_item : i == PasscodeActivity.this.patternSectionRow ? 7 : (i == PasscodeActivity.this.patternVibrateRow || i == PasscodeActivity.this.patternSilentModeRow || i == PasscodeActivity.this.patternHideWrongRow) ? 8 : 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = false;
            int itemViewType = getItemViewType(i);
            View textCheckCell;
            if (itemViewType == 0) {
                if (view == null) {
                    textCheckCell = new TextCheckCell(this.mContext);
                    textCheckCell.setBackgroundColor(-1);
                } else {
                    textCheckCell = view;
                }
                TextCheckCell textCheckCell2 = (TextCheckCell) textCheckCell;
                String string;
                if (i == PasscodeActivity.this.passcodeRow) {
                    string = LocaleController.getString("PasscodeOrPatternLock", C0338R.string.PasscodeOrPatternLock);
                    if (UserConfig.passcodeHash.length() > 0) {
                        z = true;
                    }
                    textCheckCell2.setTextAndCheck(string, z, true);
                    return textCheckCell;
                } else if (i == PasscodeActivity.this.fingerprintRow) {
                    textCheckCell2.setTextAndCheck(LocaleController.getString("UnlockFingerprint", C0338R.string.UnlockFingerprint), UserConfig.useFingerprint, true);
                    return textCheckCell;
                } else if (i != PasscodeActivity.this.patternLockRow) {
                    return textCheckCell;
                } else {
                    string = LocaleController.getString("PatternLock", C0338R.string.PatternLock);
                    if (UserConfig.passcodeHash.length() > 0 && UserConfig.passcodeType == PasscodeActivity.pin_item) {
                        z = true;
                    }
                    textCheckCell2.setTextAndCheck(string, z, true);
                    return textCheckCell;
                }
            } else if (itemViewType == PasscodeActivity.done_button) {
                if (view == null) {
                    textCheckCell = new TextSettingsCell(this.mContext);
                    textCheckCell.setBackgroundColor(-1);
                } else {
                    textCheckCell = view;
                }
                TextSettingsCell textSettingsCell = (TextSettingsCell) textCheckCell;
                if (i == PasscodeActivity.this.changePasscodeRow) {
                    textSettingsCell.setText(LocaleController.getString("ChangePasscode", C0338R.string.ChangePasscode), false);
                    textSettingsCell.setTextColor(UserConfig.passcodeHash.length() == 0 ? -3750202 : Theme.MSG_TEXT_COLOR);
                    return textCheckCell;
                } else if (i == PasscodeActivity.this.autoLockRow) {
                    String formatString;
                    if (UserConfig.autoLockIn == 0) {
                        formatString = LocaleController.formatString("AutoLockDisabled", C0338R.string.AutoLockDisabled, new Object[0]);
                    } else if (UserConfig.autoLockIn < 3600) {
                        r7 = new Object[PasscodeActivity.done_button];
                        r7[0] = LocaleController.formatPluralString("Minutes", UserConfig.autoLockIn / 60);
                        formatString = LocaleController.formatString("AutoLockInTime", C0338R.string.AutoLockInTime, r7);
                    } else if (UserConfig.autoLockIn < 86400) {
                        r7 = new Object[PasscodeActivity.done_button];
                        r7[0] = LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) UserConfig.autoLockIn) / BitmapDescriptorFactory.HUE_YELLOW) / BitmapDescriptorFactory.HUE_YELLOW)));
                        formatString = LocaleController.formatString("AutoLockInTime", C0338R.string.AutoLockInTime, r7);
                    } else {
                        r7 = new Object[PasscodeActivity.done_button];
                        r7[0] = LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) UserConfig.autoLockIn) / BitmapDescriptorFactory.HUE_YELLOW) / BitmapDescriptorFactory.HUE_YELLOW) / 24.0f)));
                        formatString = LocaleController.formatString("AutoLockInTime", C0338R.string.AutoLockInTime, r7);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("AutoLock", C0338R.string.AutoLock), formatString, true);
                    textSettingsCell.setTextColor(Theme.MSG_TEXT_COLOR);
                    return textCheckCell;
                } else if (i != PasscodeActivity.this.patternSizeRow) {
                    return textCheckCell;
                } else {
                    textSettingsCell.setTextAndValue(LocaleController.getString("PatternSize", C0338R.string.PatternSize), new LockPreferences(PasscodeActivity.this.getParentActivity()).f1545e + TtmlNode.ANONYMOUS_REGION_ID, true);
                    textSettingsCell.setTextColor(Theme.MSG_TEXT_COLOR);
                    return textCheckCell;
                }
            } else if (itemViewType == PasscodeActivity.pin_item) {
                textCheckCell = view == null ? new TextInfoPrivacyCell(this.mContext) : view;
                if (i == PasscodeActivity.this.passcodeDetailRow) {
                    ((TextInfoPrivacyCell) textCheckCell).setText(LocaleController.getString("ChangePasscodeInfo", C0338R.string.ChangePasscodeInfo));
                    if (PasscodeActivity.this.autoLockDetailRow != -1) {
                        textCheckCell.setBackgroundResource(C0338R.drawable.greydivider);
                        return textCheckCell;
                    }
                    textCheckCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textCheckCell;
                } else if (i != PasscodeActivity.this.autoLockDetailRow) {
                    return textCheckCell;
                } else {
                    ((TextInfoPrivacyCell) textCheckCell).setText(LocaleController.getString("AutoLockInfo", C0338R.string.AutoLockInfo));
                    textCheckCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textCheckCell;
                }
            } else if (itemViewType == PasscodeActivity.pattern_item) {
                if (view == null) {
                    textCheckCell = new HeaderCell(this.mContext);
                    textCheckCell.setBackgroundColor(-1);
                } else {
                    textCheckCell = view;
                }
                if (i != PasscodeActivity.this.patternSectionRow2) {
                    return textCheckCell;
                }
                ((HeaderCell) textCheckCell).setText(LocaleController.getString("PatternPassword", C0338R.string.PatternPassword));
                return textCheckCell;
            } else if (itemViewType != 8) {
                return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.mContext) : view;
            } else {
                if (view == null) {
                    textCheckCell = new TextDetailCheckCell(this.mContext);
                    textCheckCell.setBackgroundColor(-1);
                } else {
                    textCheckCell = view;
                }
                TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textCheckCell;
                LockPreferences lockPreferences = new LockPreferences(PasscodeActivity.this.getParentActivity());
                if (i == PasscodeActivity.this.patternVibrateRow) {
                    textDetailCheckCell.setTextAndCheck(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("VibrateDetail", C0338R.string.VibrateDetail), lockPreferences.f1543c.booleanValue(), true);
                    return textCheckCell;
                } else if (i == PasscodeActivity.this.patternSilentModeRow) {
                    textDetailCheckCell.setTextAndCheck(LocaleController.getString("HiddenMode", C0338R.string.HiddenMode), LocaleController.getString("HiddenModeDetail", C0338R.string.HiddenModeDetail), lockPreferences.f1554n, true);
                    return textCheckCell;
                } else if (i != PasscodeActivity.this.patternHideWrongRow) {
                    return textCheckCell;
                } else {
                    textDetailCheckCell.setTextAndCheck(LocaleController.getString("HideWrongPattern", C0338R.string.HideWrongPattern), LocaleController.getString("HideWrongPatternDetail", C0338R.string.HideWrongPatternDetail), lockPreferences.f1555o, true);
                    return textCheckCell;
                }
            }
        }

        public int getViewTypeCount() {
            return 9;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == PasscodeActivity.this.passcodeRow || i == PasscodeActivity.this.fingerprintRow || i == PasscodeActivity.this.autoLockRow || ((UserConfig.passcodeHash.length() != 0 && i == PasscodeActivity.this.changePasscodeRow) || i == PasscodeActivity.this.patternVibrateRow || i == PasscodeActivity.this.patternSilentModeRow || i == PasscodeActivity.this.patternHideWrongRow || i == PasscodeActivity.this.patternSizeRow || i == PasscodeActivity.this.patternLockRow);
        }
    }

    public PasscodeActivity(int i) {
        this.currentPasswordType = 0;
        this.passcodeSetStep = 0;
        this.type = i;
    }

    private void fixLayoutInternal() {
        if (this.dropDownContainer != null) {
            if (!AndroidUtilities.isTablet()) {
                LayoutParams layoutParams = (LayoutParams) this.dropDownContainer.getLayoutParams();
                layoutParams.topMargin = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                this.dropDownContainer.setLayoutParams(layoutParams);
            }
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != pin_item) {
                this.dropDown.setTextSize(20.0f);
            } else {
                this.dropDown.setTextSize(18.0f);
            }
        }
    }

    private void onPasscodeError() {
        if (getParentActivity() != null) {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
        }
    }

    private void processDone() {
        if (this.passwordEditText.getText().length() == 0) {
            onPasscodeError();
        } else if (this.type == done_button) {
            if (this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                try {
                    UserConfig.passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(UserConfig.passcodeSalt);
                    Object bytes = this.firstPassword.getBytes(C0700C.UTF8_NAME);
                    Object obj = new byte[(bytes.length + 32)];
                    System.arraycopy(UserConfig.passcodeSalt, 0, obj, 0, 16);
                    System.arraycopy(bytes, 0, obj, 16, bytes.length);
                    System.arraycopy(UserConfig.passcodeSalt, 0, obj, bytes.length + 16, 16);
                    UserConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(obj, 0, obj.length));
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                UserConfig.passcodeType = this.currentPasswordType;
                UserConfig.saveConfig(false);
                finishFragment();
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                return;
            }
            try {
                Toast.makeText(getParentActivity(), LocaleController.getString("PasscodeDoNotMatch", C0338R.string.PasscodeDoNotMatch), 0).show();
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
            this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
        } else if (this.type != pin_item) {
        } else {
            if (UserConfig.checkPasscode(this.passwordEditText.getText().toString())) {
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                presentFragment(new PasscodeActivity(0), true);
                return;
            }
            this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            onPasscodeError();
        }
    }

    private void processNext() {
        if (this.passwordEditText.getText().length() == 0 || (this.currentPasswordType == 0 && this.passwordEditText.getText().length() != pattern_item)) {
            onPasscodeError();
            return;
        }
        if (this.currentPasswordType == 0) {
            this.actionBar.setTitle(LocaleController.getString("PasscodePIN", C0338R.string.PasscodePIN));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PasscodePassword", C0338R.string.PasscodePassword));
        }
        this.dropDownContainer.setVisibility(8);
        this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", C0338R.string.ReEnterYourPasscode));
        this.firstPassword = this.passwordEditText.getText().toString();
        this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
        this.passcodeSetStep = done_button;
    }

    private void showCreatePattern() {
        presentFragment(new LockActivity(done_button), true);
    }

    private void updateDropDownTextView() {
        if (this.dropDown != null) {
            if (this.currentPasswordType == 0) {
                this.dropDown.setText(LocaleController.getString("PasscodePIN", C0338R.string.PasscodePIN));
            } else if (this.currentPasswordType == done_button) {
                this.dropDown.setText(LocaleController.getString("PasscodePassword", C0338R.string.PasscodePassword));
            } else if (this.currentPasswordType == pin_item) {
                this.dropDown.setText(LocaleController.getString("PatternPassword", C0338R.string.PatternPassword));
            }
        }
        if ((this.type == done_button && this.currentPasswordType == 0) || (this.type == pin_item && UserConfig.passcodeType == 0)) {
            InputFilter[] inputFilterArr = new InputFilter[done_button];
            inputFilterArr[0] = new LengthFilter(pattern_item);
            this.passwordEditText.setFilters(inputFilterArr);
            this.passwordEditText.setInputType(password_item);
            this.passwordEditText.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        } else if ((this.type == done_button && this.currentPasswordType == done_button) || (this.type == pin_item && UserConfig.passcodeType == done_button)) {
            this.passwordEditText.setFilters(new InputFilter[0]);
            this.passwordEditText.setKeyListener(null);
            this.passwordEditText.setInputType(129);
        }
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + done_button;
        this.passcodeRow = i;
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.changePasscodeRow = i;
        if (UserConfig.passcodeHash.length() > 0) {
            try {
                if (VERSION.SDK_INT >= 23 && FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected()) {
                    i = this.rowCount;
                    this.rowCount = i + done_button;
                    this.fingerprintRow = i;
                }
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
            }
            i = this.rowCount;
            this.rowCount = i + done_button;
            this.autoLockRow = i;
            i = this.rowCount;
            this.rowCount = i + done_button;
            this.autoLockDetailRow = i;
        } else {
            this.fingerprintRow = -1;
            this.autoLockRow = -1;
            this.autoLockDetailRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.patternSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.patternSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.patternLockRow = i;
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.patternVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.patternSilentModeRow = i;
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.patternHideWrongRow = i;
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.patternSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + done_button;
        this.passcodeDetailRow = i;
    }

    public View createView(Context context) {
        if (this.type != password_item) {
            this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        }
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new C17401());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        if (this.type == pin_item && UserConfig.passcodeType == pin_item) {
            presentFragment(new LockActivity(pin_item), true);
            return this.fragmentView;
        }
        LayoutParams layoutParams;
        if (this.type != 0) {
            ActionBarMenu createMenu = this.actionBar.createMenu();
            if (ThemeUtil.m2490b()) {
                Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
                drawable.setColorFilter(AdvanceTheme.f2492c, Mode.SRC_IN);
                createMenu.addItemWithWidth((int) done_button, drawable, AndroidUtilities.dp(56.0f));
            } else {
                createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
            }
            this.titleTextView = new TextView(context);
            this.titleTextView.setTypeface(FontUtil.m1176a().m1161d());
            this.titleTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            if (this.type != done_button) {
                this.titleTextView.setText(LocaleController.getString("EnterCurrentPasscode", C0338R.string.EnterCurrentPasscode));
            } else if (UserConfig.passcodeHash.length() != 0) {
                this.titleTextView.setText(LocaleController.getString("EnterNewPasscode", C0338R.string.EnterNewPasscode));
            } else {
                this.titleTextView.setText(LocaleController.getString("EnterNewFirstPasscode", C0338R.string.EnterNewFirstPasscode));
            }
            this.titleTextView.setTextSize(done_button, 18.0f);
            this.titleTextView.setGravity(done_button);
            frameLayout.addView(this.titleTextView);
            LayoutParams layoutParams2 = (LayoutParams) this.titleTextView.getLayoutParams();
            layoutParams2.width = -2;
            layoutParams2.height = -2;
            layoutParams2.gravity = done_button;
            layoutParams2.topMargin = AndroidUtilities.dp(38.0f);
            this.titleTextView.setLayoutParams(layoutParams2);
            this.passwordEditText = new EditText(context);
            this.passwordEditText.setTextSize(done_button, 20.0f);
            this.passwordEditText.setTextColor(Theme.MSG_TEXT_COLOR);
            this.passwordEditText.setMaxLines(done_button);
            this.passwordEditText.setLines(done_button);
            this.passwordEditText.setGravity(done_button);
            this.passwordEditText.setSingleLine(true);
            if (this.type == done_button) {
                this.passcodeSetStep = 0;
                this.passwordEditText.setImeOptions(5);
            } else {
                this.passcodeSetStep = done_button;
                this.passwordEditText.setImeOptions(6);
            }
            this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.passwordEditText.setTypeface(Typeface.DEFAULT);
            AndroidUtilities.clearCursorDrawable(this.passwordEditText);
            frameLayout.addView(this.passwordEditText);
            layoutParams = (LayoutParams) this.passwordEditText.getLayoutParams();
            layoutParams.topMargin = AndroidUtilities.dp(90.0f);
            layoutParams.height = AndroidUtilities.dp(36.0f);
            layoutParams.leftMargin = AndroidUtilities.dp(40.0f);
            layoutParams.gravity = 51;
            layoutParams.rightMargin = AndroidUtilities.dp(40.0f);
            layoutParams.width = -1;
            this.passwordEditText.setLayoutParams(layoutParams);
            this.passwordEditText.setOnEditorActionListener(new C17412());
            this.passwordEditText.addTextChangedListener(new C17423());
            this.passwordEditText.setCustomSelectionActionModeCallback(new C17434());
            if (this.type == done_button) {
                this.dropDownContainer = new ActionBarMenuItem(context, createMenu, 0);
                this.dropDownContainer.setSubMenuOpenSide(done_button);
                this.dropDownContainer.addSubItem(pin_item, LocaleController.getString("PasscodePIN", C0338R.string.PasscodePIN), 0);
                this.dropDownContainer.addSubItem(password_item, LocaleController.getString("PasscodePassword", C0338R.string.PasscodePassword), 0);
                this.dropDownContainer.addSubItem(pattern_item, LocaleController.getString("PatternPassword", C0338R.string.PatternPassword), 0);
                this.actionBar.addView(this.dropDownContainer);
                layoutParams = (LayoutParams) this.dropDownContainer.getLayoutParams();
                layoutParams.height = -1;
                layoutParams.width = -2;
                layoutParams.rightMargin = AndroidUtilities.dp(40.0f);
                layoutParams.leftMargin = AndroidUtilities.isTablet() ? AndroidUtilities.dp(64.0f) : AndroidUtilities.dp(56.0f);
                layoutParams.gravity = 51;
                this.dropDownContainer.setLayoutParams(layoutParams);
                this.dropDownContainer.setOnClickListener(new C17445());
                this.dropDown = new TextView(context);
                this.dropDown.setGravity(password_item);
                this.dropDown.setSingleLine(true);
                this.dropDown.setLines(done_button);
                this.dropDown.setMaxLines(done_button);
                this.dropDown.setEllipsize(TruncateAt.END);
                this.dropDown.setTextColor(-1);
                this.dropDown.setTypeface(FontUtil.m1176a().m1160c());
                this.dropDown.setCompoundDrawablesWithIntrinsicBounds(0, 0, C0338R.drawable.ic_arrow_drop_down, 0);
                this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
                this.dropDownContainer.addView(this.dropDown);
                layoutParams = (LayoutParams) this.dropDown.getLayoutParams();
                layoutParams.width = -2;
                layoutParams.height = -2;
                layoutParams.leftMargin = AndroidUtilities.dp(16.0f);
                layoutParams.gravity = 16;
                layoutParams.bottomMargin = AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.dropDown.setLayoutParams(layoutParams);
            } else {
                this.actionBar.setTitle(LocaleController.getString("PasscodeOrPatternLock", C0338R.string.PasscodeOrPatternLock));
            }
            updateDropDownTextView();
        } else {
            this.actionBar.setTitle(LocaleController.getString("PasscodeOrPatternLock", C0338R.string.PasscodeOrPatternLock));
            frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
            this.listView = new ListView(context);
            initThemeBackground(this.listView);
            this.listView.setDivider(null);
            this.listView.setDividerHeight(0);
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setDrawSelectorOnTop(true);
            frameLayout.addView(this.listView);
            layoutParams = (LayoutParams) this.listView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.gravity = 48;
            this.listView.setLayoutParams(layoutParams);
            ListView listView = this.listView;
            android.widget.ListAdapter listAdapter = new ListAdapter(context);
            this.listAdapter = listAdapter;
            listView.setAdapter(listAdapter);
            this.listView.setOnItemClickListener(new C17486());
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.didSetPasscode && this.type == 0) {
            updateRows();
            if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new C17508());
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type == 0) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.type != 0) {
            AndroidUtilities.runOnUIThread(new C17497(), 200);
        }
        fixLayoutInternal();
        initThemeActionBar();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.type != 0) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }
}
