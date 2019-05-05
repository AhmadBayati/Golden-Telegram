package com.hanista.mobogram.mobo.p016p;

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
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
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

/* renamed from: com.hanista.mobogram.mobo.p.g */
public class SpecificContactSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListView f2097a;
    private int f2098b;
    private int f2099c;
    private int f2100d;
    private int f2101e;
    private int f2102f;
    private int f2103g;

    /* renamed from: com.hanista.mobogram.mobo.p.g.1 */
    class SpecificContactSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ SpecificContactSettingsActivity f2087a;

        SpecificContactSettingsActivity(SpecificContactSettingsActivity specificContactSettingsActivity) {
            this.f2087a = specificContactSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f2087a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.g.2 */
    class SpecificContactSettingsActivity implements OnItemClickListener {
        final /* synthetic */ SpecificContactSettingsActivity f2094a;

        /* renamed from: com.hanista.mobogram.mobo.p.g.2.1 */
        class SpecificContactSettingsActivity implements OnClickListener {
            final /* synthetic */ View f2088a;
            final /* synthetic */ ColorPickerView f2089b;
            final /* synthetic */ SpecificContactSettingsActivity f2090c;

            SpecificContactSettingsActivity(SpecificContactSettingsActivity specificContactSettingsActivity, View view, ColorPickerView colorPickerView) {
                this.f2090c = specificContactSettingsActivity;
                this.f2088a = view;
                this.f2089b = colorPickerView;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                TextColorCell textColorCell = (TextColorCell) this.f2088a;
                edit.putInt("MessagesLed", this.f2089b.getColor());
                textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), this.f2089b.getColor(), true);
                edit.commit();
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.p.g.2.2 */
        class SpecificContactSettingsActivity implements OnClickListener {
            final /* synthetic */ View f2091a;
            final /* synthetic */ SpecificContactSettingsActivity f2092b;

            SpecificContactSettingsActivity(SpecificContactSettingsActivity specificContactSettingsActivity, View view) {
                this.f2092b = specificContactSettingsActivity;
                this.f2091a = view;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                TextColorCell textColorCell = (TextColorCell) this.f2091a;
                edit.putInt("MessagesLed", 0);
                textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), 0, true);
                edit.commit();
                this.f2092b.f2094a.f2097a.invalidateViews();
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.p.g.2.3 */
        class SpecificContactSettingsActivity implements OnClickListener {
            final /* synthetic */ SpecificContactSettingsActivity f2093a;

            SpecificContactSettingsActivity(SpecificContactSettingsActivity specificContactSettingsActivity) {
                this.f2093a = specificContactSettingsActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                String str = "vibrate_messages";
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
                if (this.f2093a.f2094a.f2097a != null) {
                    this.f2093a.f2094a.f2097a.invalidateViews();
                }
            }
        }

        SpecificContactSettingsActivity(SpecificContactSettingsActivity specificContactSettingsActivity) {
            this.f2094a = specificContactSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (i == this.f2094a.f2101e) {
                try {
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                    Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                    intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                    intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                    intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                    Parcelable parcelable = System.DEFAULT_NOTIFICATION_URI;
                    String path = parcelable != null ? parcelable.getPath() : null;
                    String string = sharedPreferences.getString("GlobalSoundPath", path);
                    if (string == null || string.equals("NoSound")) {
                        parcelable = null;
                    } else if (!string.equals(path)) {
                        parcelable = Uri.parse(string);
                    }
                    intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", parcelable);
                    this.f2094a.startActivityForResult(intent, i);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            } else if (i == this.f2094a.f2102f) {
                if (this.f2094a.getParentActivity() != null) {
                    View linearLayout = new LinearLayout(this.f2094a.getParentActivity());
                    linearLayout.setOrientation(1);
                    View colorPickerView = new ColorPickerView(this.f2094a.getParentActivity());
                    linearLayout.addView(colorPickerView, LayoutHelper.createLinear(-2, -2, 17));
                    colorPickerView.setOldCenterColor(ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).getInt("MessagesLed", -16711936));
                    Builder builder = new Builder(this.f2094a.getParentActivity());
                    builder.setTitle(LocaleController.getString("LedColor", C0338R.string.LedColor));
                    builder.setView(linearLayout);
                    builder.setPositiveButton(LocaleController.getString("Set", C0338R.string.Set), new SpecificContactSettingsActivity(this, view, colorPickerView));
                    builder.setNeutralButton(LocaleController.getString("LedDisabled", C0338R.string.LedDisabled), new SpecificContactSettingsActivity(this, view));
                    this.f2094a.showDialog(builder.create());
                } else {
                    return;
                }
            } else if (i == this.f2094a.f2100d) {
                Builder builder2 = new Builder(this.f2094a.getParentActivity());
                builder2.setTitle(LocaleController.getString("Vibrate", C0338R.string.Vibrate));
                builder2.setItems(new CharSequence[]{LocaleController.getString("VibrationDisabled", C0338R.string.VibrationDisabled), LocaleController.getString("VibrationDefault", C0338R.string.VibrationDefault), LocaleController.getString("Short", C0338R.string.Short), LocaleController.getString("Long", C0338R.string.Long), LocaleController.getString("OnlyIfSilent", C0338R.string.OnlyIfSilent)}, new SpecificContactSettingsActivity(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                this.f2094a.showDialog(builder2.create());
            }
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.g.a */
    private class SpecificContactSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ SpecificContactSettingsActivity f2095a;
        private Context f2096b;

        public SpecificContactSettingsActivity(SpecificContactSettingsActivity specificContactSettingsActivity, Context context) {
            this.f2095a = specificContactSettingsActivity;
            this.f2096b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f2095a.f2103g;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == this.f2095a.f2099c ? 0 : i == this.f2095a.f2102f ? 3 : i == this.f2095a.f2098b ? 4 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View headerCell;
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                headerCell = view == null ? new HeaderCell(this.f2096b) : view;
                if (i == this.f2095a.f2099c) {
                    ((HeaderCell) headerCell).setText(LocaleController.getString("SpecificContactNotification", C0338R.string.SpecificContactNotification));
                }
            } else {
                headerCell = view;
            }
            if (itemViewType == 1) {
                if (headerCell == null) {
                    headerCell = new TextCheckCell(this.f2096b);
                }
                TextCheckCell textCheckCell = (TextCheckCell) headerCell;
                return headerCell;
            } else if (itemViewType == 2) {
                if (headerCell == null) {
                    headerCell = new TextDetailSettingsCell(this.f2096b);
                }
                TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) headerCell;
                r2 = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                if (i == this.f2095a.f2101e) {
                    textDetailSettingsCell.setMultilineDetail(false);
                    String string = r2.getString("GlobalSound", LocaleController.getString("SoundDefault", C0338R.string.SoundDefault));
                    if (string.equals("NoSound")) {
                        string = LocaleController.getString("NoSound", C0338R.string.NoSound);
                    }
                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("Sound", C0338R.string.Sound), string, true);
                    return headerCell;
                } else if (i != this.f2095a.f2100d) {
                    return headerCell;
                } else {
                    textDetailSettingsCell.setMultilineDetail(false);
                    itemViewType = r2.getInt("vibrate_messages", 0);
                    if (itemViewType == 0) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("VibrationDefault", C0338R.string.VibrationDefault), true);
                        return headerCell;
                    } else if (itemViewType == 1) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("Short", C0338R.string.Short), true);
                        return headerCell;
                    } else if (itemViewType == 2) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("VibrationDisabled", C0338R.string.VibrationDisabled), true);
                        return headerCell;
                    } else if (itemViewType == 3) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("Long", C0338R.string.Long), true);
                        return headerCell;
                    } else if (itemViewType != 4) {
                        return headerCell;
                    } else {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0338R.string.Vibrate), LocaleController.getString("OnlyIfSilent", C0338R.string.OnlyIfSilent), true);
                        return headerCell;
                    }
                }
            } else if (itemViewType != 3) {
                return (itemViewType == 4 && headerCell == null) ? new ShadowSectionCell(this.f2096b) : headerCell;
            } else {
                if (headerCell == null) {
                    headerCell = new TextColorCell(this.f2096b);
                }
                TextColorCell textColorCell = (TextColorCell) headerCell;
                r2 = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                if (i != this.f2095a.f2102f) {
                    return headerCell;
                }
                textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0338R.string.LedColor), r2.getInt("MessagesLed", -16711936), true);
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
            return (i == this.f2095a.f2099c || i == this.f2095a.f2098b) ? false : true;
        }
    }

    public SpecificContactSettingsActivity() {
        this.f2103g = 0;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", C0338R.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new SpecificContactSettingsActivity(this));
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f2097a = new ListView(context);
        initThemeBackground(this.f2097a);
        this.f2097a.setDivider(null);
        this.f2097a.setDividerHeight(0);
        this.f2097a.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.f2097a);
        LayoutParams layoutParams = (LayoutParams) this.f2097a.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.f2097a.setLayoutParams(layoutParams);
        this.f2097a.setAdapter(new SpecificContactSettingsActivity(this, context));
        this.f2097a.setOnItemClickListener(new SpecificContactSettingsActivity(this));
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.f2097a.invalidateViews();
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
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            if (i == this.f2101e) {
                if (str == null || uri == null) {
                    edit.putString("GlobalSound", "NoSound");
                    edit.putString("GlobalSoundPath", "NoSound");
                } else {
                    edit.putString("GlobalSound", str);
                    edit.putString("GlobalSoundPath", uri.toString());
                }
            }
            edit.commit();
            this.f2097a.invalidateViews();
        }
    }

    public boolean onFragmentCreate() {
        int i = this.f2103g;
        this.f2103g = i + 1;
        this.f2098b = i;
        i = this.f2103g;
        this.f2103g = i + 1;
        this.f2099c = i;
        i = this.f2103g;
        this.f2103g = i + 1;
        this.f2102f = i;
        i = this.f2103g;
        this.f2103g = i + 1;
        this.f2100d = i;
        i = this.f2103g;
        this.f2103g = i + 1;
        this.f2101e = i;
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
