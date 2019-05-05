package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings.System;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.NotificationsController;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerNotifySettings;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.TextColorCell;
import com.hanista.mobogram.ui.Cells.TextDetailSettingsCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.ColorPickerView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.NumberPicker;

public class ProfileNotificationsActivity extends BaseFragment implements NotificationCenterDelegate {
    private long dialog_id;
    private ListView listView;
    private int rowCount;
    private int settingsLedRow;
    private int settingsNotificationsRow;
    private int settingsPriorityRow;
    private int settingsSoundRow;
    private int settingsVibrateRow;
    private int smartRow;

    /* renamed from: com.hanista.mobogram.ui.ProfileNotificationsActivity.1 */
    class C18491 extends ActionBarMenuOnItemClick {
        C18491() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ProfileNotificationsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileNotificationsActivity.2 */
    class C18582 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.ProfileNotificationsActivity.2.1 */
        class C18501 implements OnClickListener {
            C18501() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                if (i == 0) {
                    edit.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 2);
                } else if (i == 1) {
                    edit.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                } else if (i == 2) {
                    edit.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 4);
                } else if (i == 3) {
                    edit.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 1);
                } else if (i == 4) {
                    edit.putInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 3);
                }
                edit.commit();
                if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileNotificationsActivity.2.2 */
        class C18512 implements OnClickListener {
            C18512() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                edit.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, i);
                if (i == 2) {
                    NotificationsController.getInstance().removeNotificationsForDialog(ProfileNotificationsActivity.this.dialog_id);
                }
                MessagesStorage.getInstance().setDialogFlags(ProfileNotificationsActivity.this.dialog_id, i == 2 ? 1 : 0);
                edit.commit();
                TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(ProfileNotificationsActivity.this.dialog_id));
                if (tL_dialog != null) {
                    tL_dialog.notify_settings = new TL_peerNotifySettings();
                    if (i == 2) {
                        tL_dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                }
                if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                }
                NotificationsController.updateServerNotificationsSettings(ProfileNotificationsActivity.this.dialog_id);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileNotificationsActivity.2.3 */
        class C18523 implements OnClickListener {
            final /* synthetic */ ColorPickerView val$colorPickerView;

            C18523(ColorPickerView colorPickerView) {
                this.val$colorPickerView = colorPickerView;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                edit.putInt("color_" + ProfileNotificationsActivity.this.dialog_id, this.val$colorPickerView.getColor());
                edit.commit();
                ProfileNotificationsActivity.this.listView.invalidateViews();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileNotificationsActivity.2.4 */
        class C18534 implements OnClickListener {
            C18534() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                edit.putInt("color_" + ProfileNotificationsActivity.this.dialog_id, 0);
                edit.commit();
                ProfileNotificationsActivity.this.listView.invalidateViews();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileNotificationsActivity.2.5 */
        class C18545 implements OnClickListener {
            C18545() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                edit.remove("color_" + ProfileNotificationsActivity.this.dialog_id);
                edit.commit();
                ProfileNotificationsActivity.this.listView.invalidateViews();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileNotificationsActivity.2.6 */
        class C18556 implements OnClickListener {
            C18556() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("priority_" + ProfileNotificationsActivity.this.dialog_id, i == 0 ? 3 : i - 1).commit();
                if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileNotificationsActivity.2.7 */
        class C18567 implements OnClickListener {
            final /* synthetic */ NumberPicker val$numberPickerMinutes;
            final /* synthetic */ NumberPicker val$numberPickerTimes;

            C18567(NumberPicker numberPicker, NumberPicker numberPicker2) {
                this.val$numberPickerTimes = numberPicker;
                this.val$numberPickerMinutes = numberPicker2;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                sharedPreferences.edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, this.val$numberPickerTimes.getValue()).commit();
                sharedPreferences.edit().putInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, this.val$numberPickerMinutes.getValue() * 60).commit();
                if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileNotificationsActivity.2.8 */
        class C18578 implements OnClickListener {
            C18578() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                if (ProfileNotificationsActivity.this.listView != null) {
                    ProfileNotificationsActivity.this.listView.invalidateViews();
                }
            }
        }

        C18582() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            Builder builder;
            if (i == ProfileNotificationsActivity.this.settingsVibrateRow) {
                builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("Vibrate", C0338R.string.Vibrate));
                builder.setItems(new CharSequence[]{LocaleController.getString("VibrationDisabled", C0338R.string.VibrationDisabled), LocaleController.getString("SettingsDefault", C0338R.string.SettingsDefault), LocaleController.getString("SystemDefault", C0338R.string.SystemDefault), LocaleController.getString("Short", C0338R.string.Short), LocaleController.getString("Long", C0338R.string.Long)}, new C18501());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                ProfileNotificationsActivity.this.showDialog(builder.create());
            } else if (i == ProfileNotificationsActivity.this.settingsNotificationsRow) {
                if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                    builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setItems(new CharSequence[]{LocaleController.getString("Default", C0338R.string.Default), LocaleController.getString("Enabled", C0338R.string.Enabled), LocaleController.getString("NotificationsDisabled", C0338R.string.NotificationsDisabled)}, new C18512());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    ProfileNotificationsActivity.this.showDialog(builder.create());
                }
            } else if (i == ProfileNotificationsActivity.this.settingsSoundRow) {
                try {
                    Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                    intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                    intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                    intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    Parcelable parcelable = System.DEFAULT_NOTIFICATION_URI;
                    String path = parcelable != null ? parcelable.getPath() : null;
                    String string = sharedPreferences.getString("sound_path_" + ProfileNotificationsActivity.this.dialog_id, path);
                    if (string == null || string.equals("NoSound")) {
                        parcelable = null;
                    } else if (!string.equals(path)) {
                        parcelable = Uri.parse(string);
                    }
                    intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", parcelable);
                    ProfileNotificationsActivity.this.startActivityForResult(intent, 12);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            } else if (i == ProfileNotificationsActivity.this.settingsLedRow) {
                if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                    View linearLayout = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                    linearLayout.setOrientation(1);
                    r1 = new ColorPickerView(ProfileNotificationsActivity.this.getParentActivity());
                    linearLayout.addView(r1, LayoutHelper.createLinear(-2, -2, 17));
                    r2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    if (r2.contains("color_" + ProfileNotificationsActivity.this.dialog_id)) {
                        r1.setOldCenterColor(r2.getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16711936));
                    } else if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
                        r1.setOldCenterColor(r2.getInt("GroupLed", -16711936));
                    } else {
                        r1.setOldCenterColor(r2.getInt("MessagesLed", -16711936));
                    }
                    builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("LedColor", C0338R.string.LedColor));
                    builder.setView(linearLayout);
                    builder.setPositiveButton(LocaleController.getString("Set", C0338R.string.Set), new C18523(r1));
                    builder.setNeutralButton(LocaleController.getString("LedDisabled", C0338R.string.LedDisabled), new C18534());
                    builder.setNegativeButton(LocaleController.getString("Default", C0338R.string.Default), new C18545());
                    ProfileNotificationsActivity.this.showDialog(builder.create());
                }
            } else if (i == ProfileNotificationsActivity.this.settingsPriorityRow) {
                builder = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("NotificationsPriority", C0338R.string.NotificationsPriority));
                builder.setItems(new CharSequence[]{LocaleController.getString("SettingsDefault", C0338R.string.SettingsDefault), LocaleController.getString("NotificationsPriorityDefault", C0338R.string.NotificationsPriorityDefault), LocaleController.getString("NotificationsPriorityHigh", C0338R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityMax", C0338R.string.NotificationsPriorityMax)}, new C18556());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                ProfileNotificationsActivity.this.showDialog(builder.create());
            } else if (i == ProfileNotificationsActivity.this.smartRow && ProfileNotificationsActivity.this.getParentActivity() != null) {
                r2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                int i2 = r2.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                int i3 = r2.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                if (i2 == 0) {
                    i2 = 2;
                }
                View linearLayout2 = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                linearLayout2.setOrientation(1);
                View linearLayout3 = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                linearLayout3.setOrientation(0);
                linearLayout2.addView(linearLayout3);
                LayoutParams layoutParams = (LayoutParams) linearLayout3.getLayoutParams();
                layoutParams.width = -2;
                layoutParams.height = -2;
                layoutParams.gravity = 49;
                linearLayout3.setLayoutParams(layoutParams);
                View textView = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                textView.setText(LocaleController.getString("SmartNotificationsSoundAtMost", C0338R.string.SmartNotificationsSoundAtMost));
                textView.setTextSize(1, 18.0f);
                linearLayout3.addView(textView);
                layoutParams = (LayoutParams) textView.getLayoutParams();
                layoutParams.width = -2;
                layoutParams.height = -2;
                layoutParams.gravity = 19;
                textView.setLayoutParams(layoutParams);
                textView = new NumberPicker(ProfileNotificationsActivity.this.getParentActivity());
                textView.setMinValue(1);
                textView.setMaxValue(10);
                textView.setValue(i2);
                linearLayout3.addView(textView);
                layoutParams = (LayoutParams) textView.getLayoutParams();
                layoutParams.width = AndroidUtilities.dp(50.0f);
                textView.setLayoutParams(layoutParams);
                r1 = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                r1.setText(LocaleController.getString("SmartNotificationsTimes", C0338R.string.SmartNotificationsTimes));
                r1.setTextSize(1, 18.0f);
                linearLayout3.addView(r1);
                layoutParams = (LayoutParams) r1.getLayoutParams();
                layoutParams.width = -2;
                layoutParams.height = -2;
                layoutParams.gravity = 19;
                r1.setLayoutParams(layoutParams);
                r1 = new LinearLayout(ProfileNotificationsActivity.this.getParentActivity());
                r1.setOrientation(0);
                linearLayout2.addView(r1);
                layoutParams = (LayoutParams) r1.getLayoutParams();
                layoutParams.width = -2;
                layoutParams.height = -2;
                layoutParams.gravity = 49;
                r1.setLayoutParams(layoutParams);
                linearLayout3 = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                linearLayout3.setText(LocaleController.getString("SmartNotificationsWithin", C0338R.string.SmartNotificationsWithin));
                linearLayout3.setTextSize(1, 18.0f);
                r1.addView(linearLayout3);
                layoutParams = (LayoutParams) linearLayout3.getLayoutParams();
                layoutParams.width = -2;
                layoutParams.height = -2;
                layoutParams.gravity = 19;
                linearLayout3.setLayoutParams(layoutParams);
                linearLayout3 = new NumberPicker(ProfileNotificationsActivity.this.getParentActivity());
                linearLayout3.setMinValue(1);
                linearLayout3.setMaxValue(10);
                linearLayout3.setValue(i3 / 60);
                r1.addView(linearLayout3);
                layoutParams = (LayoutParams) linearLayout3.getLayoutParams();
                layoutParams.width = AndroidUtilities.dp(50.0f);
                linearLayout3.setLayoutParams(layoutParams);
                View textView2 = new TextView(ProfileNotificationsActivity.this.getParentActivity());
                textView2.setText(LocaleController.getString("SmartNotificationsMinutes", C0338R.string.SmartNotificationsMinutes));
                textView2.setTextSize(1, 18.0f);
                r1.addView(textView2);
                layoutParams = (LayoutParams) textView2.getLayoutParams();
                layoutParams.width = -2;
                layoutParams.height = -2;
                layoutParams.gravity = 19;
                textView2.setLayoutParams(layoutParams);
                Builder builder2 = new Builder(ProfileNotificationsActivity.this.getParentActivity());
                builder2.setTitle(LocaleController.getString("SmartNotifications", C0338R.string.SmartNotifications));
                builder2.setView(linearLayout2);
                builder2.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C18567(textView, linearLayout3));
                builder2.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", C0338R.string.SmartNotificationsDisabled), new C18578());
                ProfileNotificationsActivity.this.showDialog(builder2.create());
            }
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public int getCount() {
            return ProfileNotificationsActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == ProfileNotificationsActivity.this.settingsNotificationsRow || i == ProfileNotificationsActivity.this.settingsVibrateRow || i == ProfileNotificationsActivity.this.settingsSoundRow || i == ProfileNotificationsActivity.this.settingsPriorityRow || i == ProfileNotificationsActivity.this.smartRow || i != ProfileNotificationsActivity.this.settingsLedRow) ? 0 : 1;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            View textDetailSettingsCell;
            SharedPreferences sharedPreferences;
            if (itemViewType == 0) {
                textDetailSettingsCell = view == null ? new TextDetailSettingsCell(this.mContext) : view;
                TextDetailSettingsCell textDetailSettingsCell2 = (TextDetailSettingsCell) textDetailSettingsCell;
                sharedPreferences = this.mContext.getSharedPreferences("Notifications", 0);
                int i2;
                if (i == ProfileNotificationsActivity.this.settingsVibrateRow) {
                    i2 = sharedPreferences.getInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if (i2 == 0) {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("SettingsDefault", C0338R.string.SettingsDefault), true);
                        return textDetailSettingsCell;
                    } else if (i2 == 1) {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("Short", C0338R.string.Short), true);
                        return textDetailSettingsCell;
                    } else if (i2 == 2) {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("VibrationDisabled", C0338R.string.VibrationDisabled), true);
                        return textDetailSettingsCell;
                    } else if (i2 == 3) {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("Long", C0338R.string.Long), true);
                        return textDetailSettingsCell;
                    } else if (i2 != 4) {
                        return textDetailSettingsCell;
                    } else {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("SystemDefault", C0338R.string.SystemDefault), true);
                        return textDetailSettingsCell;
                    }
                } else if (i == ProfileNotificationsActivity.this.settingsNotificationsRow) {
                    r3 = sharedPreferences.getInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if (r3 == 0) {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("Notifications", C0338R.string.Notifications), LocaleController.getString("Default", C0338R.string.Default), true);
                        return textDetailSettingsCell;
                    } else if (r3 == 1) {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("Notifications", C0338R.string.Notifications), LocaleController.getString("Enabled", C0338R.string.Enabled), true);
                        return textDetailSettingsCell;
                    } else if (r3 == 2) {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("Notifications", C0338R.string.Notifications), LocaleController.getString("NotificationsDisabled", C0338R.string.NotificationsDisabled), true);
                        return textDetailSettingsCell;
                    } else if (r3 != 3) {
                        return textDetailSettingsCell;
                    } else {
                        i2 = sharedPreferences.getInt("notifyuntil_" + ProfileNotificationsActivity.this.dialog_id, 0) - ConnectionsManager.getInstance().getCurrentTime();
                        r2 = i2 <= 0 ? LocaleController.getString("Enabled", C0338R.string.Enabled) : i2 < 3600 ? LocaleController.formatString("WillUnmuteIn", C0338R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", i2 / 60)) : i2 < 86400 ? LocaleController.formatString("WillUnmuteIn", C0338R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) i2) / BitmapDescriptorFactory.HUE_YELLOW) / BitmapDescriptorFactory.HUE_YELLOW)))) : i2 < 31536000 ? LocaleController.formatString("WillUnmuteIn", C0338R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) i2) / BitmapDescriptorFactory.HUE_YELLOW) / BitmapDescriptorFactory.HUE_YELLOW) / 24.0f)))) : null;
                        if (r2 != null) {
                            textDetailSettingsCell2.setTextAndValue(LocaleController.getString("Notifications", C0338R.string.Notifications), r2, true);
                            return textDetailSettingsCell;
                        }
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("Notifications", C0338R.string.Notifications), LocaleController.getString("NotificationsDisabled", C0338R.string.NotificationsDisabled), true);
                        return textDetailSettingsCell;
                    }
                } else if (i == ProfileNotificationsActivity.this.settingsSoundRow) {
                    r2 = sharedPreferences.getString("sound_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("SoundDefault", C0338R.string.SoundDefault));
                    if (r2.equals("NoSound")) {
                        r2 = LocaleController.getString("NoSound", C0338R.string.NoSound);
                    }
                    textDetailSettingsCell2.setTextAndValue(LocaleController.getString("Sound", C0338R.string.Sound), r2, true);
                    return textDetailSettingsCell;
                } else if (i == ProfileNotificationsActivity.this.settingsPriorityRow) {
                    i2 = sharedPreferences.getInt("priority_" + ProfileNotificationsActivity.this.dialog_id, 3);
                    if (i2 == 0) {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("NotificationsPriority", C0338R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityDefault", C0338R.string.NotificationsPriorityDefault), true);
                        return textDetailSettingsCell;
                    } else if (i2 == 1) {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("NotificationsPriority", C0338R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityHigh", C0338R.string.NotificationsPriorityHigh), true);
                        return textDetailSettingsCell;
                    } else if (i2 == 2) {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("NotificationsPriority", C0338R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityMax", C0338R.string.NotificationsPriorityMax), true);
                        return textDetailSettingsCell;
                    } else if (i2 != 3) {
                        return textDetailSettingsCell;
                    } else {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("NotificationsPriority", C0338R.string.NotificationsPriority), LocaleController.getString("SettingsDefault", C0338R.string.SettingsDefault), true);
                        return textDetailSettingsCell;
                    }
                } else if (i != ProfileNotificationsActivity.this.smartRow) {
                    return textDetailSettingsCell;
                } else {
                    r3 = sharedPreferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                    i2 = sharedPreferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                    if (r3 == 0) {
                        textDetailSettingsCell2.setTextAndValue(LocaleController.getString("SmartNotifications", C0338R.string.SmartNotifications), LocaleController.getString("SmartNotificationsDisabled", C0338R.string.SmartNotificationsDisabled), true);
                        return textDetailSettingsCell;
                    }
                    String formatPluralString = LocaleController.formatPluralString("Times", r3);
                    r2 = LocaleController.formatPluralString("Minutes", i2 / 60);
                    textDetailSettingsCell2.setTextAndValue(LocaleController.getString("SmartNotifications", C0338R.string.SmartNotifications), LocaleController.formatString("SmartNotificationsInfo", C0338R.string.SmartNotificationsInfo, formatPluralString, r2), true);
                    return textDetailSettingsCell;
                }
            } else if (itemViewType != 1) {
                return view;
            } else {
                textDetailSettingsCell = view == null ? new TextColorCell(this.mContext) : view;
                TextColorCell textColorCell = (TextColorCell) textDetailSettingsCell;
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (sharedPreferences.contains("color_" + ProfileNotificationsActivity.this.dialog_id)) {
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), sharedPreferences.getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16711936), false);
                    return textDetailSettingsCell;
                } else if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), sharedPreferences.getInt("GroupLed", -16711936), false);
                    return textDetailSettingsCell;
                } else {
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), sharedPreferences.getInt("MessagesLed", -16711936), false);
                    return textDetailSettingsCell;
                }
            }
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean isEnabled(int i) {
            return true;
        }
    }

    public ProfileNotificationsActivity(Bundle bundle) {
        super(bundle);
        this.rowCount = 0;
        this.dialog_id = bundle.getLong("dialog_id");
    }

    private void initThemeListView() {
        if (ThemeUtil.m2490b()) {
            this.listView.setBackgroundColor(AdvanceTheme.f2497h);
            AndroidUtilities.setListViewEdgeEffectColor(this.listView, AdvanceTheme.f2500k);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", C0338R.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new C18491());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new ListView(context);
        initThemeListView();
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.listView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setOnItemClickListener(new C18582());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.listView.invalidateViews();
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1 && intent != null) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String str = null;
            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, uri);
                if (ringtone != null) {
                    str = uri.equals(System.DEFAULT_NOTIFICATION_URI) ? LocaleController.getString("SoundDefault", C0338R.string.SoundDefault) : ringtone.getTitle(getParentActivity());
                    ringtone.stop();
                }
            }
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            if (i == 12) {
                if (str != null) {
                    edit.putString("sound_" + this.dialog_id, str);
                    edit.putString("sound_path_" + this.dialog_id, uri.toString());
                } else {
                    edit.putString("sound_" + this.dialog_id, "NoSound");
                    edit.putString("sound_path_" + this.dialog_id, "NoSound");
                }
            }
            edit.commit();
            this.listView.invalidateViews();
        }
    }

    public boolean onFragmentCreate() {
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsNotificationsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.settingsPriorityRow = i;
        } else {
            this.settingsPriorityRow = -1;
        }
        if (((int) this.dialog_id) < 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.smartRow = i;
        } else {
            this.smartRow = 1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsLedRow = i;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}
