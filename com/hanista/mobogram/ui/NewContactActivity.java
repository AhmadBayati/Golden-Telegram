package com.hanista.mobogram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_importContacts;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_importedContacts;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPhoneContact;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.ContextProgressView;
import com.hanista.mobogram.ui.Components.HintEditText;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.CountrySelectActivity.CountrySelectActivityDelegate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class NewContactActivity extends BaseFragment implements OnItemSelectedListener {
    private static final int done_button = 1;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private EditText codeField;
    private HashMap<String, String> codesMap;
    private ArrayList<String> countriesArray;
    private HashMap<String, String> countriesMap;
    private TextView countryButton;
    private int countryState;
    private boolean donePressed;
    private ActionBarMenuItem editDoneItem;
    private AnimatorSet editDoneItemAnimation;
    private ContextProgressView editDoneItemProgress;
    private EditText firstNameField;
    private boolean ignoreOnPhoneChange;
    private boolean ignoreOnTextChange;
    private boolean ignoreSelection;
    private EditText lastNameField;
    private HintEditText phoneField;
    private HashMap<String, String> phoneFormatMap;

    /* renamed from: com.hanista.mobogram.ui.NewContactActivity.13 */
    class AnonymousClass13 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$show;

        AnonymousClass13(boolean z) {
            this.val$show = z;
        }

        public void onAnimationCancel(Animator animator) {
            if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animator)) {
                NewContactActivity.this.editDoneItemAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animator)) {
                if (this.val$show) {
                    NewContactActivity.this.editDoneItem.getImageView().setVisibility(4);
                } else {
                    NewContactActivity.this.editDoneItemProgress.setVisibility(4);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.NewContactActivity.1 */
    class C17191 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.NewContactActivity.1.1 */
        class C17181 implements RequestDelegate {
            final /* synthetic */ TL_inputPhoneContact val$inputPhoneContact;

            /* renamed from: com.hanista.mobogram.ui.NewContactActivity.1.1.1 */
            class C17171 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TL_contacts_importedContacts val$res;

                /* renamed from: com.hanista.mobogram.ui.NewContactActivity.1.1.1.1 */
                class C17161 implements OnClickListener {
                    C17161() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", C17181.this.val$inputPhoneContact.phone, null));
                            intent.putExtra("sms_body", LocaleController.getString("InviteText", C0338R.string.InviteText));
                            NewContactActivity.this.getParentActivity().startActivityForResult(intent, 500);
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                }

                C17171(TL_contacts_importedContacts tL_contacts_importedContacts, TL_error tL_error) {
                    this.val$res = tL_contacts_importedContacts;
                    this.val$error = tL_error;
                }

                public void run() {
                    NewContactActivity.this.donePressed = false;
                    if (this.val$res == null) {
                        NewContactActivity.this.showEditDoneProgress(false, true);
                        if (this.val$error == null || this.val$error.text.startsWith("FLOOD_WAIT")) {
                            NewContactActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else {
                            NewContactActivity.this.needShowAlert(LocaleController.getString("AppName", C0338R.string.AppName), LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred) + "\n" + this.val$error.text);
                        }
                    } else if (!this.val$res.users.isEmpty()) {
                        MessagesController.getInstance().putUsers(this.val$res.users, false);
                        MessagesController.openChatOrProfileWith((User) this.val$res.users.get(0), null, NewContactActivity.this, NewContactActivity.done_button, true);
                    } else if (NewContactActivity.this.getParentActivity() != null) {
                        NewContactActivity.this.showEditDoneProgress(false, true);
                        Builder builder = new Builder(NewContactActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        Object[] objArr = new Object[NewContactActivity.done_button];
                        objArr[0] = ContactsController.formatName(C17181.this.val$inputPhoneContact.first_name, C17181.this.val$inputPhoneContact.last_name);
                        builder.setMessage(LocaleController.formatString("ContactNotRegistered", C0338R.string.ContactNotRegistered, objArr));
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        builder.setPositiveButton(LocaleController.getString("Invite", C0338R.string.Invite), new C17161());
                        NewContactActivity.this.showDialog(builder.create());
                    }
                }
            }

            C17181(TL_inputPhoneContact tL_inputPhoneContact) {
                this.val$inputPhoneContact = tL_inputPhoneContact;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C17171((TL_contacts_importedContacts) tLObject, tL_error));
            }
        }

        C17191() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                NewContactActivity.this.finishFragment();
            } else if (i == NewContactActivity.done_button && !NewContactActivity.this.donePressed) {
                Vibrator vibrator;
                if (NewContactActivity.this.firstNameField.length() == 0) {
                    vibrator = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                    AndroidUtilities.shakeView(NewContactActivity.this.firstNameField, 2.0f, 0);
                } else if (NewContactActivity.this.codeField.length() == 0) {
                    vibrator = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                    AndroidUtilities.shakeView(NewContactActivity.this.codeField, 2.0f, 0);
                } else if (NewContactActivity.this.phoneField.length() == 0) {
                    vibrator = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                    AndroidUtilities.shakeView(NewContactActivity.this.phoneField, 2.0f, 0);
                } else {
                    NewContactActivity.this.donePressed = true;
                    NewContactActivity.this.showEditDoneProgress(true, true);
                    TLObject tL_contacts_importContacts = new TL_contacts_importContacts();
                    TL_inputPhoneContact tL_inputPhoneContact = new TL_inputPhoneContact();
                    tL_inputPhoneContact.first_name = NewContactActivity.this.firstNameField.getText().toString();
                    tL_inputPhoneContact.last_name = NewContactActivity.this.lastNameField.getText().toString();
                    tL_inputPhoneContact.phone = "+" + NewContactActivity.this.codeField.getText().toString() + NewContactActivity.this.phoneField.getText().toString();
                    tL_contacts_importContacts.contacts.add(tL_inputPhoneContact);
                    ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_contacts_importContacts, new C17181(tL_inputPhoneContact), 2), NewContactActivity.this.classGuid);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.NewContactActivity.2 */
    class C17202 implements OnTouchListener {
        C17202() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.NewContactActivity.3 */
    class C17213 implements OnEditorActionListener {
        C17213() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            NewContactActivity.this.lastNameField.requestFocus();
            NewContactActivity.this.lastNameField.setSelection(NewContactActivity.this.lastNameField.length());
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.NewContactActivity.4 */
    class C17224 implements TextWatcher {
        C17224() {
        }

        public void afterTextChanged(Editable editable) {
            NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString(), false);
            NewContactActivity.this.avatarImage.invalidate();
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.NewContactActivity.5 */
    class C17235 implements OnEditorActionListener {
        C17235() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            NewContactActivity.this.phoneField.requestFocus();
            NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.NewContactActivity.6 */
    class C17246 implements TextWatcher {
        C17246() {
        }

        public void afterTextChanged(Editable editable) {
            NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString(), false);
            NewContactActivity.this.avatarImage.invalidate();
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.NewContactActivity.7 */
    class C17277 implements View.OnClickListener {

        /* renamed from: com.hanista.mobogram.ui.NewContactActivity.7.1 */
        class C17261 implements CountrySelectActivityDelegate {

            /* renamed from: com.hanista.mobogram.ui.NewContactActivity.7.1.1 */
            class C17251 implements Runnable {
                C17251() {
                }

                public void run() {
                    AndroidUtilities.showKeyboard(NewContactActivity.this.phoneField);
                }
            }

            C17261() {
            }

            public void didSelectCountry(String str) {
                NewContactActivity.this.selectCountry(str);
                AndroidUtilities.runOnUIThread(new C17251(), 300);
                NewContactActivity.this.phoneField.requestFocus();
                NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
            }
        }

        C17277() {
        }

        public void onClick(View view) {
            BaseFragment countrySelectActivity = new CountrySelectActivity();
            countrySelectActivity.setCountrySelectActivityDelegate(new C17261());
            NewContactActivity.this.presentFragment(countrySelectActivity);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.NewContactActivity.8 */
    class C17288 implements TextWatcher {
        C17288() {
        }

        public void afterTextChanged(Editable editable) {
            String str = null;
            if (!NewContactActivity.this.ignoreOnTextChange) {
                NewContactActivity.this.ignoreOnTextChange = true;
                String stripExceptNumbers = PhoneFormat.stripExceptNumbers(NewContactActivity.this.codeField.getText().toString());
                NewContactActivity.this.codeField.setText(stripExceptNumbers);
                if (stripExceptNumbers.length() == 0) {
                    NewContactActivity.this.countryButton.setText(LocaleController.getString("ChooseCountry", C0338R.string.ChooseCountry));
                    NewContactActivity.this.phoneField.setHintText(null);
                    NewContactActivity.this.countryState = NewContactActivity.done_button;
                } else {
                    String str2;
                    Object obj;
                    boolean z;
                    CharSequence charSequence;
                    if (stripExceptNumbers.length() > 4) {
                        String substring;
                        boolean z2;
                        NewContactActivity.this.ignoreOnTextChange = true;
                        for (int i = 4; i >= NewContactActivity.done_button; i--) {
                            substring = stripExceptNumbers.substring(0, i);
                            if (((String) NewContactActivity.this.codesMap.get(substring)) != null) {
                                str2 = stripExceptNumbers.substring(i, stripExceptNumbers.length()) + NewContactActivity.this.phoneField.getText().toString();
                                NewContactActivity.this.codeField.setText(substring);
                                z2 = true;
                                break;
                            }
                        }
                        str2 = null;
                        substring = stripExceptNumbers;
                        z2 = false;
                        if (!z2) {
                            NewContactActivity.this.ignoreOnTextChange = true;
                            str2 = substring.substring(NewContactActivity.done_button, substring.length()) + NewContactActivity.this.phoneField.getText().toString();
                            EditText access$200 = NewContactActivity.this.codeField;
                            substring = substring.substring(0, NewContactActivity.done_button);
                            access$200.setText(substring);
                        }
                        obj = substring;
                        z = z2;
                        charSequence = str2;
                    } else {
                        z = false;
                        String str3 = stripExceptNumbers;
                        charSequence = null;
                    }
                    str2 = (String) NewContactActivity.this.codesMap.get(obj);
                    if (str2 != null) {
                        int indexOf = NewContactActivity.this.countriesArray.indexOf(str2);
                        if (indexOf != -1) {
                            NewContactActivity.this.ignoreSelection = true;
                            NewContactActivity.this.countryButton.setText((CharSequence) NewContactActivity.this.countriesArray.get(indexOf));
                            str2 = (String) NewContactActivity.this.phoneFormatMap.get(obj);
                            HintEditText access$300 = NewContactActivity.this.phoneField;
                            if (str2 != null) {
                                str = str2.replace('X', '\u2013');
                            }
                            access$300.setHintText(str);
                            NewContactActivity.this.countryState = 0;
                        } else {
                            NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", C0338R.string.WrongCountry));
                            NewContactActivity.this.phoneField.setHintText(null);
                            NewContactActivity.this.countryState = 2;
                        }
                    } else {
                        NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", C0338R.string.WrongCountry));
                        NewContactActivity.this.phoneField.setHintText(null);
                        NewContactActivity.this.countryState = 2;
                    }
                    if (!z) {
                        NewContactActivity.this.codeField.setSelection(NewContactActivity.this.codeField.getText().length());
                    }
                    if (charSequence != null) {
                        NewContactActivity.this.phoneField.requestFocus();
                        NewContactActivity.this.phoneField.setText(charSequence);
                        NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
                    }
                }
                NewContactActivity.this.ignoreOnTextChange = false;
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.NewContactActivity.9 */
    class C17299 implements OnEditorActionListener {
        C17299() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            NewContactActivity.this.phoneField.requestFocus();
            NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
            return true;
        }
    }

    public NewContactActivity() {
        this.countriesArray = new ArrayList();
        this.countriesMap = new HashMap();
        this.codesMap = new HashMap();
        this.phoneFormatMap = new HashMap();
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            if (this.fragmentView != null) {
                this.fragmentView.setBackgroundColor(AdvanceTheme.f2497h);
            }
            if (this.avatarImage != null) {
                this.avatarImage.setRoundRadius(AndroidUtilities.dp((float) AdvanceTheme.f2502m));
            }
            if (this.firstNameField != null) {
                if (this.firstNameField.getBackground() != null) {
                    this.firstNameField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.firstNameField.setHintTextColor(AdvanceTheme.f2495f);
                this.firstNameField.setTextColor(AdvanceTheme.f2494e);
            }
            if (this.lastNameField != null) {
                if (this.lastNameField.getBackground() != null) {
                    this.lastNameField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.lastNameField.setHintTextColor(AdvanceTheme.f2495f);
                this.lastNameField.setTextColor(AdvanceTheme.f2494e);
            }
            if (this.countryButton != null) {
                if (this.countryButton.getBackground() != null) {
                    this.countryButton.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.countryButton.setTextColor(AdvanceTheme.aJ);
            }
            if (this.codeField != null) {
                if (this.codeField.getBackground() != null) {
                    this.codeField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.codeField.setHintTextColor(AdvanceTheme.f2495f);
                this.codeField.setTextColor(AdvanceTheme.f2494e);
            }
            if (this.phoneField != null) {
                if (this.phoneField.getBackground() != null) {
                    this.phoneField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.phoneField.setHintTextColor(AdvanceTheme.f2495f);
                this.phoneField.setTextColor(AdvanceTheme.f2494e);
            }
        }
    }

    private void needShowAlert(String str, String str2) {
        if (str2 != null && getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(str);
            builder.setMessage(str2);
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            showDialog(builder.create());
        }
    }

    private void showEditDoneProgress(boolean z, boolean z2) {
        if (this.editDoneItemAnimation != null) {
            this.editDoneItemAnimation.cancel();
        }
        if (z2) {
            this.editDoneItemAnimation = new AnimatorSet();
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            float[] fArr;
            float[] fArr2;
            if (z) {
                this.editDoneItemProgress.setVisibility(0);
                this.editDoneItem.setEnabled(false);
                animatorSet = this.editDoneItemAnimation;
                animatorArr = new Animator[6];
                fArr = new float[done_button];
                fArr[0] = 0.1f;
                animatorArr[0] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", fArr);
                fArr = new float[done_button];
                fArr[0] = 0.1f;
                animatorArr[done_button] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", fArr);
                fArr2 = new float[done_button];
                fArr2[0] = 0.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", fArr2);
                fArr2 = new float[done_button];
                fArr2[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[3] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", fArr2);
                fArr2 = new float[done_button];
                fArr2[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[4] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", fArr2);
                fArr2 = new float[done_button];
                fArr2[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[5] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", fArr2);
                animatorSet.playTogether(animatorArr);
            } else {
                this.editDoneItem.getImageView().setVisibility(0);
                this.editDoneItem.setEnabled(true);
                animatorSet = this.editDoneItemAnimation;
                animatorArr = new Animator[6];
                fArr = new float[done_button];
                fArr[0] = 0.1f;
                animatorArr[0] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", fArr);
                fArr = new float[done_button];
                fArr[0] = 0.1f;
                animatorArr[done_button] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", fArr);
                fArr2 = new float[done_button];
                fArr2[0] = 0.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", fArr2);
                fArr2 = new float[done_button];
                fArr2[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[3] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", fArr2);
                fArr2 = new float[done_button];
                fArr2[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[4] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", fArr2);
                fArr2 = new float[done_button];
                fArr2[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[5] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", fArr2);
                animatorSet.playTogether(animatorArr);
            }
            this.editDoneItemAnimation.addListener(new AnonymousClass13(z));
            this.editDoneItemAnimation.setDuration(150);
            this.editDoneItemAnimation.start();
        } else if (z) {
            this.editDoneItem.getImageView().setScaleX(0.1f);
            this.editDoneItem.getImageView().setScaleY(0.1f);
            this.editDoneItem.getImageView().setAlpha(0.0f);
            this.editDoneItemProgress.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItemProgress.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItemProgress.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItem.getImageView().setVisibility(4);
            this.editDoneItemProgress.setVisibility(0);
            this.editDoneItem.setEnabled(false);
        } else {
            this.editDoneItemProgress.setScaleX(0.1f);
            this.editDoneItemProgress.setScaleY(0.1f);
            this.editDoneItemProgress.setAlpha(0.0f);
            this.editDoneItem.getImageView().setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItem.getImageView().setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItem.getImageView().setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItem.getImageView().setVisibility(0);
            this.editDoneItemProgress.setVisibility(4);
            this.editDoneItem.setEnabled(true);
        }
    }

    public View createView(Context context) {
        Object toUpperCase;
        String str;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AddContactTitle", C0338R.string.AddContactTitle));
        this.actionBar.setActionBarMenuOnItemClick(new C17191());
        this.avatarDrawable = new AvatarDrawable();
        this.avatarDrawable.setInfo(5, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, false);
        this.editDoneItem = this.actionBar.createMenu().addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.editDoneItemProgress = new ContextProgressView(context, done_button);
        this.editDoneItem.addView(this.editDoneItemProgress, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.editDoneItemProgress.setVisibility(4);
        this.fragmentView = new ScrollView(context);
        View linearLayout = new LinearLayout(context);
        linearLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        linearLayout.setOrientation(done_button);
        ((ScrollView) this.fragmentView).addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        linearLayout.setOnTouchListener(new C17202());
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 24.0f, 0.0f, 0.0f));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setImageDrawable(this.avatarDrawable);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(60, BitmapDescriptorFactory.HUE_YELLOW, 51, 0.0f, 9.0f, 0.0f, 0.0f));
        this.firstNameField = new EditText(context);
        this.firstNameField.setTextSize(done_button, 18.0f);
        this.firstNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.firstNameField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.firstNameField.setMaxLines(done_button);
        this.firstNameField.setLines(done_button);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", C0338R.string.FirstName));
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        frameLayout.addView(this.firstNameField, LayoutHelper.createFrame(-1, 34.0f, 51, 84.0f, 0.0f, 0.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new C17213());
        this.firstNameField.addTextChangedListener(new C17224());
        this.lastNameField = new EditText(context);
        this.lastNameField.setTextSize(done_button, 18.0f);
        this.lastNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.lastNameField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.lastNameField.setMaxLines(done_button);
        this.lastNameField.setLines(done_button);
        this.lastNameField.setSingleLine(true);
        this.lastNameField.setGravity(3);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(5);
        this.lastNameField.setHint(LocaleController.getString("LastName", C0338R.string.LastName));
        AndroidUtilities.clearCursorDrawable(this.lastNameField);
        frameLayout.addView(this.lastNameField, LayoutHelper.createFrame(-1, 34.0f, 51, 84.0f, 44.0f, 0.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new C17235());
        this.lastNameField.addTextChangedListener(new C17246());
        this.countryButton = new TextView(context);
        this.countryButton.setTextSize(done_button, 18.0f);
        this.countryButton.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(6.0f), 0);
        this.countryButton.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.countryButton.setMaxLines(done_button);
        this.countryButton.setSingleLine(true);
        this.countryButton.setEllipsize(TruncateAt.END);
        this.countryButton.setGravity(3);
        this.countryButton.setBackgroundResource(C0338R.drawable.spinner_states);
        linearLayout.addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 24.0f, 0.0f, 14.0f));
        this.countryButton.setOnClickListener(new C17277());
        View view = new View(context);
        view.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        view.setBackgroundColor(-2368549);
        linearLayout.addView(view, LayoutHelper.createLinear(-1, done_button, 0.0f, -17.5f, 0.0f, 0.0f));
        view = new LinearLayout(context);
        view.setOrientation(0);
        linearLayout.addView(view, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
        View textView = new TextView(context);
        textView.setText("+");
        textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        textView.setTextSize(done_button, 18.0f);
        if (ThemeUtil.m2490b()) {
            textView.setTextColor(AdvanceTheme.f2494e);
        }
        view.addView(textView, LayoutHelper.createLinear(-2, -2));
        this.codeField = new EditText(context);
        this.codeField.setInputType(3);
        this.codeField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        AndroidUtilities.clearCursorDrawable(this.codeField);
        this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
        this.codeField.setTextSize(done_button, 18.0f);
        this.codeField.setMaxLines(done_button);
        this.codeField.setGravity(19);
        this.codeField.setImeOptions(268435461);
        InputFilter[] inputFilterArr = new InputFilter[done_button];
        inputFilterArr[0] = new LengthFilter(5);
        this.codeField.setFilters(inputFilterArr);
        view.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
        this.codeField.addTextChangedListener(new C17288());
        this.codeField.setOnEditorActionListener(new C17299());
        this.phoneField = new HintEditText(context);
        this.phoneField.setInputType(3);
        this.phoneField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.phoneField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.phoneField.setPadding(0, 0, 0, 0);
        AndroidUtilities.clearCursorDrawable(this.phoneField);
        this.phoneField.setTextSize(done_button, 18.0f);
        this.phoneField.setMaxLines(done_button);
        this.phoneField.setGravity(19);
        this.phoneField.setImeOptions(268435462);
        view.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
        this.phoneField.addTextChangedListener(new TextWatcher() {
            private int actionPosition;
            private int characterAction;

            {
                this.characterAction = -1;
            }

            public void afterTextChanged(Editable editable) {
                if (!NewContactActivity.this.ignoreOnPhoneChange) {
                    int selectionStart = NewContactActivity.this.phoneField.getSelectionStart();
                    String str = "0123456789";
                    String obj = NewContactActivity.this.phoneField.getText().toString();
                    if (this.characterAction == 3) {
                        obj = obj.substring(0, this.actionPosition) + obj.substring(this.actionPosition + NewContactActivity.done_button, obj.length());
                        selectionStart--;
                    }
                    CharSequence stringBuilder = new StringBuilder(obj.length());
                    for (int i = 0; i < obj.length(); i += NewContactActivity.done_button) {
                        Object substring = obj.substring(i, i + NewContactActivity.done_button);
                        if (str.contains(substring)) {
                            stringBuilder.append(substring);
                        }
                    }
                    NewContactActivity.this.ignoreOnPhoneChange = true;
                    String hintText = NewContactActivity.this.phoneField.getHintText();
                    if (hintText != null) {
                        int i2 = 0;
                        while (i2 < stringBuilder.length()) {
                            if (i2 < hintText.length()) {
                                if (hintText.charAt(i2) == ' ') {
                                    stringBuilder.insert(i2, ' ');
                                    i2 += NewContactActivity.done_button;
                                    if (!(selectionStart != i2 || this.characterAction == 2 || this.characterAction == 3)) {
                                        selectionStart += NewContactActivity.done_button;
                                    }
                                }
                                i2 += NewContactActivity.done_button;
                            } else {
                                stringBuilder.insert(i2, ' ');
                                if (!(selectionStart != i2 + NewContactActivity.done_button || this.characterAction == 2 || this.characterAction == 3)) {
                                    selectionStart += NewContactActivity.done_button;
                                }
                            }
                        }
                    }
                    NewContactActivity.this.phoneField.setText(stringBuilder);
                    if (selectionStart >= 0) {
                        HintEditText access$300 = NewContactActivity.this.phoneField;
                        if (selectionStart > NewContactActivity.this.phoneField.length()) {
                            selectionStart = NewContactActivity.this.phoneField.length();
                        }
                        access$300.setSelection(selectionStart);
                    }
                    NewContactActivity.this.phoneField.onTextChange();
                    NewContactActivity.this.ignoreOnPhoneChange = false;
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (i2 == 0 && i3 == NewContactActivity.done_button) {
                    this.characterAction = NewContactActivity.done_button;
                } else if (i2 != NewContactActivity.done_button || i3 != 0) {
                    this.characterAction = -1;
                } else if (charSequence.charAt(i) != ' ' || i <= 0) {
                    this.characterAction = 2;
                } else {
                    this.characterAction = 3;
                    this.actionPosition = i - 1;
                }
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        this.phoneField.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                NewContactActivity.this.editDoneItem.performClick();
                return true;
            }
        });
        HashMap hashMap = new HashMap();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String[] split = readLine.split(";");
                this.countriesArray.add(0, split[2]);
                this.countriesMap.put(split[2], split[0]);
                this.codesMap.put(split[0], split[2]);
                if (split.length > 3) {
                    this.phoneFormatMap.put(split[0], split[3]);
                }
                hashMap.put(split[done_button], split[2]);
            }
            bufferedReader.close();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        Collections.sort(this.countriesArray, new Comparator<String>() {
            public int compare(String str, String str2) {
                return str.compareTo(str2);
            }
        });
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                toUpperCase = telephonyManager.getSimCountryIso().toUpperCase();
                if (toUpperCase != null) {
                    str = (String) hashMap.get(toUpperCase);
                    if (!(str == null || this.countriesArray.indexOf(str) == -1)) {
                        this.codeField.setText((CharSequence) this.countriesMap.get(str));
                        this.countryState = 0;
                    }
                }
                if (this.codeField.length() == 0) {
                    this.countryButton.setText(LocaleController.getString("ChooseCountry", C0338R.string.ChooseCountry));
                    this.phoneField.setHintText(null);
                    this.countryState = done_button;
                }
                return this.fragmentView;
            }
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
        toUpperCase = null;
        if (toUpperCase != null) {
            str = (String) hashMap.get(toUpperCase);
            this.codeField.setText((CharSequence) this.countriesMap.get(str));
            this.countryState = 0;
        }
        if (this.codeField.length() == 0) {
            this.countryButton.setText(LocaleController.getString("ChooseCountry", C0338R.string.ChooseCountry));
            this.phoneField.setHintText(null);
            this.countryState = done_button;
        }
        return this.fragmentView;
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.ignoreSelection) {
            this.ignoreSelection = false;
            return;
        }
        this.ignoreOnTextChange = true;
        this.codeField.setText((CharSequence) this.countriesMap.get((String) this.countriesArray.get(i)));
        this.ignoreOnTextChange = false;
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void onResume() {
        super.onResume();
        if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
        initThemeActionBar();
        initTheme();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void selectCountry(String str) {
        if (this.countriesArray.indexOf(str) != -1) {
            this.ignoreOnTextChange = true;
            String str2 = (String) this.countriesMap.get(str);
            this.codeField.setText(str2);
            this.countryButton.setText(str);
            str2 = (String) this.phoneFormatMap.get(str2);
            this.phoneField.setHintText(str2 != null ? str2.replace('X', '\u2013') : null);
            this.countryState = 0;
            this.ignoreOnTextChange = false;
        }
    }
}
