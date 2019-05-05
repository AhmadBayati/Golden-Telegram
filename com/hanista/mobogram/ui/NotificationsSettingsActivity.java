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
import android.os.Parcelable;
import android.provider.Settings.System;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.NotificationsController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PsExtractor;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_resetNotifySettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextColorCell;
import com.hanista.mobogram.ui.Cells.TextDetailSettingsCell;
import com.hanista.mobogram.ui.Components.ColorPickerView;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class NotificationsSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int androidAutoAlertRow;
    private int badgeNumberRow;
    private int contactJoinedRow;
    private int eventsSectionRow;
    private int eventsSectionRow2;
    private int groupAlertRow;
    private int groupLedRow;
    private int groupPopupNotificationRow;
    private int groupPreviewRow;
    private int groupPriorityRow;
    private int groupSectionRow;
    private int groupSectionRow2;
    private int groupSoundRow;
    private int groupVibrateRow;
    private int inappPreviewRow;
    private int inappPriorityRow;
    private int inappSectionRow;
    private int inappSectionRow2;
    private int inappSoundRow;
    private int inappVibrateRow;
    private int inchatSoundRow;
    private ListView listView;
    private int messageAlertRow;
    private int messageLedRow;
    private int messagePopupNotificationRow;
    private int messagePreviewRow;
    private int messagePriorityRow;
    private int messageSectionRow;
    private int messageSoundRow;
    private int messageVibrateRow;
    private int notificationsServiceConnectionRow;
    private int notificationsServiceRow;
    private int otherSectionRow;
    private int otherSectionRow2;
    private int pinnedMessageRow;
    private int repeatRow;
    private int resetNotificationsRow;
    private int resetSectionRow;
    private int resetSectionRow2;
    private boolean reseting;
    private int rowCount;

    /* renamed from: com.hanista.mobogram.ui.NotificationsSettingsActivity.1 */
    class C17301 extends ActionBarMenuOnItemClick {
        C17301() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                NotificationsSettingsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.NotificationsSettingsActivity.2 */
    class C17392 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.NotificationsSettingsActivity.2.1 */
        class C17321 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.NotificationsSettingsActivity.2.1.1 */
            class C17311 implements Runnable {
                C17311() {
                }

                public void run() {
                    MessagesController.getInstance().enableJoined = true;
                    NotificationsSettingsActivity.this.reseting = false;
                    Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                    edit.clear();
                    edit.commit();
                    if (NotificationsSettingsActivity.this.listView != null) {
                        NotificationsSettingsActivity.this.listView.invalidateViews();
                    }
                    if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                        Toast.makeText(NotificationsSettingsActivity.this.getParentActivity(), LocaleController.getString("ResetNotificationsText", C0338R.string.ResetNotificationsText), 0).show();
                    }
                }
            }

            C17321() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C17311());
            }
        }

        /* renamed from: com.hanista.mobogram.ui.NotificationsSettingsActivity.2.2 */
        class C17332 implements OnClickListener {
            final /* synthetic */ ColorPickerView val$colorPickerView;
            final /* synthetic */ int val$i;
            final /* synthetic */ View val$view;

            C17332(View view, int i, ColorPickerView colorPickerView) {
                this.val$view = view;
                this.val$i = i;
                this.val$colorPickerView = colorPickerView;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                TextColorCell textColorCell = (TextColorCell) this.val$view;
                if (this.val$i == NotificationsSettingsActivity.this.messageLedRow) {
                    edit.putInt("MessagesLed", this.val$colorPickerView.getColor());
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), this.val$colorPickerView.getColor(), true);
                } else if (this.val$i == NotificationsSettingsActivity.this.groupLedRow) {
                    edit.putInt("GroupLed", this.val$colorPickerView.getColor());
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), this.val$colorPickerView.getColor(), true);
                }
                edit.commit();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.NotificationsSettingsActivity.2.3 */
        class C17343 implements OnClickListener {
            final /* synthetic */ int val$i;
            final /* synthetic */ View val$view;

            C17343(View view, int i) {
                this.val$view = view;
                this.val$i = i;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                TextColorCell textColorCell = (TextColorCell) this.val$view;
                if (this.val$i == NotificationsSettingsActivity.this.messageLedRow) {
                    edit.putInt("MessagesLed", 0);
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), 0, true);
                } else if (this.val$i == NotificationsSettingsActivity.this.groupLedRow) {
                    edit.putInt("GroupLed", 0);
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), 0, true);
                }
                edit.commit();
                NotificationsSettingsActivity.this.listView.invalidateViews();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.NotificationsSettingsActivity.2.4 */
        class C17354 implements OnClickListener {
            final /* synthetic */ int val$i;

            C17354(int i) {
                this.val$i = i;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                if (this.val$i == NotificationsSettingsActivity.this.messagePopupNotificationRow) {
                    edit.putInt("popupAll", i);
                } else if (this.val$i == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                    edit.putInt("popupGroup", i);
                }
                edit.commit();
                if (NotificationsSettingsActivity.this.listView != null) {
                    NotificationsSettingsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.NotificationsSettingsActivity.2.5 */
        class C17365 implements OnClickListener {
            final /* synthetic */ int val$i;

            C17365(int i) {
                this.val$i = i;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                String str = "vibrate_messages";
                if (this.val$i == NotificationsSettingsActivity.this.groupVibrateRow) {
                    str = "vibrate_group";
                }
                if (i == 0) {
                    edit.putInt(str, 2);
                } else if (i == 1) {
                    edit.putInt(str, 0);
                } else if (i == 2) {
                    edit.putInt(str, 1);
                } else if (i == 3) {
                    edit.putInt(str, 3);
                } else if (i == 4) {
                    edit.putInt(str, 4);
                }
                edit.commit();
                if (NotificationsSettingsActivity.this.listView != null) {
                    NotificationsSettingsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.NotificationsSettingsActivity.2.6 */
        class C17376 implements OnClickListener {
            final /* synthetic */ int val$i;

            C17376(int i) {
                this.val$i = i;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (this.val$i == NotificationsSettingsActivity.this.messagePriorityRow) {
                    sharedPreferences.edit().putInt("priority_messages", i).commit();
                } else if (this.val$i == NotificationsSettingsActivity.this.groupPriorityRow) {
                    sharedPreferences.edit().putInt("priority_group", i).commit();
                }
                if (NotificationsSettingsActivity.this.listView != null) {
                    NotificationsSettingsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.NotificationsSettingsActivity.2.7 */
        class C17387 implements OnClickListener {
            C17387() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                int i2 = 5;
                if (i != 1) {
                    i2 = i == 2 ? 10 : i == 3 ? 30 : i == 4 ? 60 : i == 5 ? 120 : i == 6 ? PsExtractor.VIDEO_STREAM_MASK : 0;
                }
                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("repeat_messages", i2).commit();
                if (NotificationsSettingsActivity.this.listView != null) {
                    NotificationsSettingsActivity.this.listView.invalidateViews();
                }
            }
        }

        C17392() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z;
            Parcelable parcelable = null;
            boolean z2 = true;
            SharedPreferences sharedPreferences;
            Editor edit;
            boolean z3;
            if (i == NotificationsSettingsActivity.this.messageAlertRow || i == NotificationsSettingsActivity.this.groupAlertRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                edit = sharedPreferences.edit();
                if (i == NotificationsSettingsActivity.this.messageAlertRow) {
                    z3 = sharedPreferences.getBoolean("EnableAll", true);
                    edit.putBoolean("EnableAll", !z3);
                    z = z3;
                } else if (i == NotificationsSettingsActivity.this.groupAlertRow) {
                    z3 = sharedPreferences.getBoolean("EnableGroup", true);
                    edit.putBoolean("EnableGroup", !z3);
                    z = z3;
                } else {
                    z = false;
                }
                edit.commit();
                NotificationsSettingsActivity.this.updateServerNotificationsSettings(i == NotificationsSettingsActivity.this.groupAlertRow);
            } else if (i == NotificationsSettingsActivity.this.messagePreviewRow || i == NotificationsSettingsActivity.this.groupPreviewRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                edit = sharedPreferences.edit();
                if (i == NotificationsSettingsActivity.this.messagePreviewRow) {
                    z3 = sharedPreferences.getBoolean("EnablePreviewAll", true);
                    edit.putBoolean("EnablePreviewAll", !z3);
                    z = z3;
                } else if (i == NotificationsSettingsActivity.this.groupPreviewRow) {
                    z3 = sharedPreferences.getBoolean("EnablePreviewGroup", true);
                    edit.putBoolean("EnablePreviewGroup", !z3);
                    z = z3;
                } else {
                    z = false;
                }
                edit.commit();
                NotificationsSettingsActivity.this.updateServerNotificationsSettings(i == NotificationsSettingsActivity.this.groupPreviewRow);
            } else if (i == NotificationsSettingsActivity.this.messageSoundRow || i == NotificationsSettingsActivity.this.groupSoundRow) {
                try {
                    SharedPreferences sharedPreferences2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                    intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                    intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                    intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                    Uri uri = System.DEFAULT_NOTIFICATION_URI;
                    String path = uri != null ? uri.getPath() : null;
                    String string;
                    if (i == NotificationsSettingsActivity.this.messageSoundRow) {
                        string = sharedPreferences2.getString("GlobalSoundPath", path);
                        if (string == null || string.equals("NoSound")) {
                            uri = null;
                        } else if (!string.equals(path)) {
                            uri = Uri.parse(string);
                        }
                        parcelable = uri;
                    } else if (i == NotificationsSettingsActivity.this.groupSoundRow) {
                        string = sharedPreferences2.getString("GroupSoundPath", path);
                        if (!(string == null || string.equals("NoSound"))) {
                            if (string.equals(path)) {
                                Object obj = uri;
                            } else {
                                parcelable = Uri.parse(string);
                            }
                        }
                    }
                    intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", parcelable);
                    NotificationsSettingsActivity.this.startActivityForResult(intent, i);
                    z = false;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    z = false;
                }
            } else if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                if (!NotificationsSettingsActivity.this.reseting) {
                    NotificationsSettingsActivity.this.reseting = true;
                    ConnectionsManager.getInstance().sendRequest(new TL_account_resetNotifySettings(), new C17321());
                    z = false;
                } else {
                    return;
                }
            } else if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                edit = sharedPreferences.edit();
                z3 = sharedPreferences.getBoolean("EnableInAppSounds", true);
                edit.putBoolean("EnableInAppSounds", !z3);
                edit.commit();
                z = z3;
            } else if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                edit = sharedPreferences.edit();
                z3 = sharedPreferences.getBoolean("EnableInAppVibrate", true);
                edit.putBoolean("EnableInAppVibrate", !z3);
                edit.commit();
                z = z3;
            } else if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                edit = sharedPreferences.edit();
                z3 = sharedPreferences.getBoolean("EnableInAppPreview", true);
                edit.putBoolean("EnableInAppPreview", !z3);
                edit.commit();
                z = z3;
            } else if (i == NotificationsSettingsActivity.this.inchatSoundRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                edit = sharedPreferences.edit();
                z3 = sharedPreferences.getBoolean("EnableInChatSound", true);
                edit.putBoolean("EnableInChatSound", !z3);
                edit.commit();
                NotificationsController.getInstance().setInChatSoundEnabled(!z3);
                z = z3;
            } else if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                edit = sharedPreferences.edit();
                z3 = sharedPreferences.getBoolean("EnableInAppPriority", false);
                edit.putBoolean("EnableInAppPriority", !z3);
                edit.commit();
                z = z3;
            } else if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                edit = sharedPreferences.edit();
                z3 = sharedPreferences.getBoolean("EnableContactJoined", true);
                MessagesController.getInstance().enableJoined = !z3;
                edit.putBoolean("EnableContactJoined", !z3);
                edit.commit();
                z = z3;
            } else if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                edit = sharedPreferences.edit();
                z3 = sharedPreferences.getBoolean("PinnedMessages", true);
                edit.putBoolean("PinnedMessages", !z3);
                edit.commit();
                z = z3;
            } else if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                edit = sharedPreferences.edit();
                z3 = sharedPreferences.getBoolean("EnableAutoNotifications", false);
                edit.putBoolean("EnableAutoNotifications", !z3);
                edit.commit();
                z = z3;
            } else if (i == NotificationsSettingsActivity.this.badgeNumberRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                edit = sharedPreferences.edit();
                z3 = sharedPreferences.getBoolean("badgeNumber", true);
                edit.putBoolean("badgeNumber", !z3);
                edit.commit();
                NotificationsController.getInstance().setBadgeEnabled(!z3);
                z = z3;
            } else if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                z3 = sharedPreferences.getBoolean("pushConnection", true);
                edit = sharedPreferences.edit();
                edit.putBoolean("pushConnection", !z3);
                edit.commit();
                if (z3) {
                    ConnectionsManager.getInstance().setPushConnectionEnabled(false);
                } else {
                    ConnectionsManager.getInstance().setPushConnectionEnabled(true);
                }
                z = z3;
            } else if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                z3 = sharedPreferences.getBoolean("pushService", true);
                edit = sharedPreferences.edit();
                edit.putBoolean("pushService", !z3);
                edit.commit();
                if (z3) {
                    ApplicationLoader.stopPushService();
                } else {
                    ApplicationLoader.startPushService();
                }
                z = z3;
            } else if (i == NotificationsSettingsActivity.this.messageLedRow || i == NotificationsSettingsActivity.this.groupLedRow) {
                if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                    View linearLayout = new LinearLayout(NotificationsSettingsActivity.this.getParentActivity());
                    linearLayout.setOrientation(1);
                    View colorPickerView = new ColorPickerView(NotificationsSettingsActivity.this.getParentActivity());
                    linearLayout.addView(colorPickerView, LayoutHelper.createLinear(-2, -2, 17));
                    SharedPreferences sharedPreferences3 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                    if (i == NotificationsSettingsActivity.this.messageLedRow) {
                        colorPickerView.setOldCenterColor(sharedPreferences3.getInt("MessagesLed", -16711936));
                    } else if (i == NotificationsSettingsActivity.this.groupLedRow) {
                        colorPickerView.setOldCenterColor(sharedPreferences3.getInt("GroupLed", -16711936));
                    }
                    Builder builder = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("LedColor", C0338R.string.LedColor));
                    builder.setView(linearLayout);
                    builder.setPositiveButton(LocaleController.getString("Set", C0338R.string.Set), new C17332(view, i, colorPickerView));
                    builder.setNeutralButton(LocaleController.getString("LedDisabled", C0338R.string.LedDisabled), new C17343(view, i));
                    NotificationsSettingsActivity.this.showDialog(builder.create());
                    z = false;
                } else {
                    return;
                }
            } else if (i == NotificationsSettingsActivity.this.messagePopupNotificationRow || i == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                r0 = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                r0.setTitle(LocaleController.getString("PopupNotification", C0338R.string.PopupNotification));
                r0.setItems(new CharSequence[]{LocaleController.getString("NoPopup", C0338R.string.NoPopup), LocaleController.getString("OnlyWhenScreenOn", C0338R.string.OnlyWhenScreenOn), LocaleController.getString("OnlyWhenScreenOff", C0338R.string.OnlyWhenScreenOff), LocaleController.getString("AlwaysShowPopup", C0338R.string.AlwaysShowPopup)}, new C17354(i));
                r0.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                NotificationsSettingsActivity.this.showDialog(r0.create());
                z = false;
            } else if (i == NotificationsSettingsActivity.this.messageVibrateRow || i == NotificationsSettingsActivity.this.groupVibrateRow) {
                r0 = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                r0.setTitle(LocaleController.getString("Vibrate", C0338R.string.Vibrate));
                r0.setItems(new CharSequence[]{LocaleController.getString("VibrationDisabled", C0338R.string.VibrationDisabled), LocaleController.getString("VibrationDefault", C0338R.string.VibrationDefault), LocaleController.getString("Short", C0338R.string.Short), LocaleController.getString("Long", C0338R.string.Long), LocaleController.getString("OnlyIfSilent", C0338R.string.OnlyIfSilent)}, new C17365(i));
                r0.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                NotificationsSettingsActivity.this.showDialog(r0.create());
                z = false;
            } else if (i == NotificationsSettingsActivity.this.messagePriorityRow || i == NotificationsSettingsActivity.this.groupPriorityRow) {
                r0 = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                r0.setTitle(LocaleController.getString("NotificationsPriority", C0338R.string.NotificationsPriority));
                r0.setItems(new CharSequence[]{LocaleController.getString("NotificationsPriorityDefault", C0338R.string.NotificationsPriorityDefault), LocaleController.getString("NotificationsPriorityHigh", C0338R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityMax", C0338R.string.NotificationsPriorityMax)}, new C17376(i));
                r0.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                NotificationsSettingsActivity.this.showDialog(r0.create());
                z = false;
            } else {
                if (i == NotificationsSettingsActivity.this.repeatRow) {
                    r0 = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                    r0.setTitle(LocaleController.getString("RepeatNotifications", C0338R.string.RepeatNotifications));
                    r0.setItems(new CharSequence[]{LocaleController.getString("RepeatDisabled", C0338R.string.RepeatDisabled), LocaleController.formatPluralString("Minutes", 5), LocaleController.formatPluralString("Minutes", 10), LocaleController.formatPluralString("Minutes", 30), LocaleController.formatPluralString("Hours", 1), LocaleController.formatPluralString("Hours", 2), LocaleController.formatPluralString("Hours", 4)}, new C17387());
                    r0.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    NotificationsSettingsActivity.this.showDialog(r0.create());
                }
                z = false;
            }
            if (view instanceof TextCheckCell) {
                TextCheckCell textCheckCell = (TextCheckCell) view;
                if (z) {
                    z2 = false;
                }
                textCheckCell.setChecked(z2);
            }
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
            return NotificationsSettingsActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == NotificationsSettingsActivity.this.messageSectionRow || i == NotificationsSettingsActivity.this.groupSectionRow || i == NotificationsSettingsActivity.this.inappSectionRow || i == NotificationsSettingsActivity.this.eventsSectionRow || i == NotificationsSettingsActivity.this.otherSectionRow || i == NotificationsSettingsActivity.this.resetSectionRow) ? 0 : (i == NotificationsSettingsActivity.this.messageAlertRow || i == NotificationsSettingsActivity.this.messagePreviewRow || i == NotificationsSettingsActivity.this.groupAlertRow || i == NotificationsSettingsActivity.this.groupPreviewRow || i == NotificationsSettingsActivity.this.inappSoundRow || i == NotificationsSettingsActivity.this.inappVibrateRow || i == NotificationsSettingsActivity.this.inappPreviewRow || i == NotificationsSettingsActivity.this.contactJoinedRow || i == NotificationsSettingsActivity.this.pinnedMessageRow || i == NotificationsSettingsActivity.this.notificationsServiceRow || i == NotificationsSettingsActivity.this.badgeNumberRow || i == NotificationsSettingsActivity.this.inappPriorityRow || i == NotificationsSettingsActivity.this.inchatSoundRow || i == NotificationsSettingsActivity.this.androidAutoAlertRow || i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) ? 1 : (i == NotificationsSettingsActivity.this.messageLedRow || i == NotificationsSettingsActivity.this.groupLedRow) ? 3 : (i == NotificationsSettingsActivity.this.eventsSectionRow2 || i == NotificationsSettingsActivity.this.groupSectionRow2 || i == NotificationsSettingsActivity.this.inappSectionRow2 || i == NotificationsSettingsActivity.this.otherSectionRow2 || i == NotificationsSettingsActivity.this.resetSectionRow2) ? 4 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View headerCell;
            View view2;
            int i2 = 0;
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                headerCell = view == null ? new HeaderCell(this.mContext) : view;
                if (i == NotificationsSettingsActivity.this.messageSectionRow) {
                    ((HeaderCell) headerCell).setText(LocaleController.getString("MessageNotifications", C0338R.string.MessageNotifications));
                    view2 = headerCell;
                } else if (i == NotificationsSettingsActivity.this.groupSectionRow) {
                    ((HeaderCell) headerCell).setText(LocaleController.getString("GroupNotifications", C0338R.string.GroupNotifications));
                    view2 = headerCell;
                } else if (i == NotificationsSettingsActivity.this.inappSectionRow) {
                    ((HeaderCell) headerCell).setText(LocaleController.getString("InAppNotifications", C0338R.string.InAppNotifications));
                    view2 = headerCell;
                } else if (i == NotificationsSettingsActivity.this.eventsSectionRow) {
                    ((HeaderCell) headerCell).setText(LocaleController.getString("Events", C0338R.string.Events));
                    view2 = headerCell;
                } else if (i == NotificationsSettingsActivity.this.otherSectionRow) {
                    ((HeaderCell) headerCell).setText(LocaleController.getString("NotificationsOther", C0338R.string.NotificationsOther));
                    view2 = headerCell;
                } else {
                    if (i == NotificationsSettingsActivity.this.resetSectionRow) {
                        ((HeaderCell) headerCell).setText(LocaleController.getString("Reset", C0338R.string.Reset));
                    }
                    view2 = headerCell;
                }
            } else {
                view2 = view;
            }
            SharedPreferences sharedPreferences;
            if (itemViewType == 1) {
                if (view2 == null) {
                    view2 = new TextCheckCell(this.mContext);
                }
                TextCheckCell textCheckCell = (TextCheckCell) view2;
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (i == NotificationsSettingsActivity.this.messageAlertRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("Alert", C0338R.string.Alert), sharedPreferences.getBoolean("EnableAll", true), true);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.groupAlertRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("Alert", C0338R.string.Alert), sharedPreferences.getBoolean("EnableGroup", true), true);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.messagePreviewRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("MessagePreview", C0338R.string.MessagePreview), sharedPreferences.getBoolean("EnablePreviewAll", true), true);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.groupPreviewRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("MessagePreview", C0338R.string.MessagePreview), sharedPreferences.getBoolean("EnablePreviewGroup", true), true);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("InAppSounds", C0338R.string.InAppSounds), sharedPreferences.getBoolean("EnableInAppSounds", true), true);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("InAppVibrate", C0338R.string.InAppVibrate), sharedPreferences.getBoolean("EnableInAppVibrate", true), true);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("InAppPreview", C0338R.string.InAppPreview), sharedPreferences.getBoolean("EnableInAppPreview", true), true);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("NotificationsPriority", C0338R.string.NotificationsPriority), sharedPreferences.getBoolean("EnableInAppPriority", false), false);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("ContactJoined", C0338R.string.ContactJoined), sharedPreferences.getBoolean("EnableContactJoined", true), true);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("PinnedMessages", C0338R.string.PinnedMessages), sharedPreferences.getBoolean("PinnedMessages", true), false);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                    textCheckCell.setTextAndCheck("Android Auto", sharedPreferences.getBoolean("EnableAutoNotifications", false), true);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                    textCheckCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", C0338R.string.NotificationsService), LocaleController.getString("NotificationsServiceInfo", C0338R.string.NotificationsServiceInfo), sharedPreferences.getBoolean("pushService", true), true, true);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                    textCheckCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", C0338R.string.NotificationsServiceConnection), LocaleController.getString("NotificationsServiceConnectionInfo", C0338R.string.NotificationsServiceConnectionInfo), sharedPreferences.getBoolean("pushConnection", true), true, true);
                    return view2;
                } else if (i == NotificationsSettingsActivity.this.badgeNumberRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("BadgeNumber", C0338R.string.BadgeNumber), sharedPreferences.getBoolean("badgeNumber", true), true);
                    return view2;
                } else if (i != NotificationsSettingsActivity.this.inchatSoundRow) {
                    return view2;
                } else {
                    textCheckCell.setTextAndCheck(LocaleController.getString("InChatSound", C0338R.string.InChatSound), sharedPreferences.getBoolean("EnableInChatSound", true), true);
                    return view2;
                }
            } else if (itemViewType == 2) {
                headerCell = view2 == null ? new TextDetailSettingsCell(this.mContext) : view2;
                TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) headerCell;
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                String str;
                if (i == NotificationsSettingsActivity.this.messageSoundRow || i == NotificationsSettingsActivity.this.groupSoundRow) {
                    textDetailSettingsCell.setMultilineDetail(false);
                    str = null;
                    if (i == NotificationsSettingsActivity.this.messageSoundRow) {
                        str = sharedPreferences.getString("GlobalSound", LocaleController.getString("SoundDefault", C0338R.string.SoundDefault));
                    } else if (i == NotificationsSettingsActivity.this.groupSoundRow) {
                        str = sharedPreferences.getString("GroupSound", LocaleController.getString("SoundDefault", C0338R.string.SoundDefault));
                    }
                    if (str.equals("NoSound")) {
                        str = LocaleController.getString("NoSound", C0338R.string.NoSound);
                    }
                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("Sound", C0338R.string.Sound), str, true);
                } else if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                    textDetailSettingsCell.setMultilineDetail(true);
                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("ResetAllNotifications", C0338R.string.ResetAllNotifications), LocaleController.getString("UndoAllCustom", C0338R.string.UndoAllCustom), false);
                } else if (i == NotificationsSettingsActivity.this.messagePopupNotificationRow || i == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                    textDetailSettingsCell.setMultilineDetail(false);
                    if (i == NotificationsSettingsActivity.this.messagePopupNotificationRow) {
                        i2 = sharedPreferences.getInt("popupAll", 0);
                    } else if (i == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                        i2 = sharedPreferences.getInt("popupGroup", 0);
                    }
                    str = i2 == 0 ? LocaleController.getString("NoPopup", C0338R.string.NoPopup) : i2 == 1 ? LocaleController.getString("OnlyWhenScreenOn", C0338R.string.OnlyWhenScreenOn) : i2 == 2 ? LocaleController.getString("OnlyWhenScreenOff", C0338R.string.OnlyWhenScreenOff) : LocaleController.getString("AlwaysShowPopup", C0338R.string.AlwaysShowPopup);
                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("PopupNotification", C0338R.string.PopupNotification), str, true);
                } else if (i == NotificationsSettingsActivity.this.messageVibrateRow || i == NotificationsSettingsActivity.this.groupVibrateRow) {
                    textDetailSettingsCell.setMultilineDetail(false);
                    if (i == NotificationsSettingsActivity.this.messageVibrateRow) {
                        i2 = sharedPreferences.getInt("vibrate_messages", 0);
                    } else if (i == NotificationsSettingsActivity.this.groupVibrateRow) {
                        i2 = sharedPreferences.getInt("vibrate_group", 0);
                    }
                    if (i2 == 0) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("VibrationDefault", C0338R.string.VibrationDefault), true);
                    } else if (i2 == 1) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("Short", C0338R.string.Short), true);
                    } else if (i2 == 2) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("VibrationDisabled", C0338R.string.VibrationDisabled), true);
                    } else if (i2 == 3) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("Long", C0338R.string.Long), true);
                    } else if (i2 == 4) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("OnlyIfSilent", C0338R.string.OnlyIfSilent), true);
                    }
                } else if (i == NotificationsSettingsActivity.this.repeatRow) {
                    textDetailSettingsCell.setMultilineDetail(false);
                    itemViewType = sharedPreferences.getInt("repeat_messages", 60);
                    str = itemViewType == 0 ? LocaleController.getString("RepeatNotificationsNever", C0338R.string.RepeatNotificationsNever) : itemViewType < 60 ? LocaleController.formatPluralString("Minutes", itemViewType) : LocaleController.formatPluralString("Hours", itemViewType / 60);
                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("RepeatNotifications", C0338R.string.RepeatNotifications), str, false);
                } else if (i == NotificationsSettingsActivity.this.messagePriorityRow || i == NotificationsSettingsActivity.this.groupPriorityRow) {
                    textDetailSettingsCell.setMultilineDetail(false);
                    itemViewType = i == NotificationsSettingsActivity.this.messagePriorityRow ? sharedPreferences.getInt("priority_messages", 1) : i == NotificationsSettingsActivity.this.groupPriorityRow ? sharedPreferences.getInt("priority_group", 1) : 0;
                    if (itemViewType == 0) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("NotificationsPriority", C0338R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityDefault", C0338R.string.NotificationsPriorityDefault), false);
                    } else if (itemViewType == 1) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("NotificationsPriority", C0338R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityHigh", C0338R.string.NotificationsPriorityHigh), false);
                    } else if (itemViewType == 2) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("NotificationsPriority", C0338R.string.NotificationsPriority), LocaleController.getString("NotificationsPriorityMax", C0338R.string.NotificationsPriorityMax), false);
                    }
                }
                return headerCell;
            } else if (itemViewType != 3) {
                return (itemViewType == 4 && view2 == null) ? new ShadowSectionCell(this.mContext) : view2;
            } else {
                headerCell = view2 == null ? new TextColorCell(this.mContext) : view2;
                TextColorCell textColorCell = (TextColorCell) headerCell;
                SharedPreferences sharedPreferences2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (i == NotificationsSettingsActivity.this.messageLedRow) {
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), sharedPreferences2.getInt("MessagesLed", -16711936), true);
                } else if (i == NotificationsSettingsActivity.this.groupLedRow) {
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), sharedPreferences2.getInt("GroupLed", -16711936), true);
                }
                return headerCell;
            }
        }

        public int getViewTypeCount() {
            return 5;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean isEnabled(int i) {
            return (i == NotificationsSettingsActivity.this.messageSectionRow || i == NotificationsSettingsActivity.this.groupSectionRow || i == NotificationsSettingsActivity.this.inappSectionRow || i == NotificationsSettingsActivity.this.eventsSectionRow || i == NotificationsSettingsActivity.this.otherSectionRow || i == NotificationsSettingsActivity.this.resetSectionRow || i == NotificationsSettingsActivity.this.eventsSectionRow2 || i == NotificationsSettingsActivity.this.groupSectionRow2 || i == NotificationsSettingsActivity.this.inappSectionRow2 || i == NotificationsSettingsActivity.this.otherSectionRow2 || i == NotificationsSettingsActivity.this.resetSectionRow2) ? false : true;
        }
    }

    public NotificationsSettingsActivity() {
        this.reseting = false;
        this.rowCount = 0;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", C0338R.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new C17301());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new ListView(context);
        initThemeBackground(this.listView);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView);
        LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setOnItemClickListener(new C17392());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.listView.invalidateViews();
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String str = null;
            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(getParentActivity(), uri);
                if (ringtone != null) {
                    str = uri.equals(System.DEFAULT_NOTIFICATION_URI) ? LocaleController.getString("SoundDefault", C0338R.string.SoundDefault) : ringtone.getTitle(getParentActivity());
                    ringtone.stop();
                }
            }
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            if (i == this.messageSoundRow) {
                if (str == null || uri == null) {
                    edit.putString("GlobalSound", "NoSound");
                    edit.putString("GlobalSoundPath", "NoSound");
                } else {
                    edit.putString("GlobalSound", str);
                    edit.putString("GlobalSoundPath", uri.toString());
                }
            } else if (i == this.groupSoundRow) {
                if (str == null || uri == null) {
                    edit.putString("GroupSound", "NoSound");
                    edit.putString("GroupSoundPath", "NoSound");
                } else {
                    edit.putString("GroupSound", str);
                    edit.putString("GroupSoundPath", uri.toString());
                }
            }
            edit.commit();
            this.listView.invalidateViews();
        }
    }

    public boolean onFragmentCreate() {
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.messageSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageAlertRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagePreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageLedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagePopupNotificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.messagePriorityRow = i;
        } else {
            this.messagePriorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupAlertRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupPreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupLedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupPopupNotificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.groupPriorityRow = i;
        } else {
            this.groupPriorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSoundRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappPreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inchatSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.inappPriorityRow = i;
        } else {
            this.inappPriorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.eventsSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.eventsSectionRow = i;
        if (UserConfig.isRobot) {
            this.contactJoinedRow = -1;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.contactJoinedRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.pinnedMessageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsServiceRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsServiceConnectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.badgeNumberRow = i;
        this.androidAutoAlertRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.repeatRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetNotificationsRow = i;
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

    public void updateServerNotificationsSettings(boolean z) {
    }
}
