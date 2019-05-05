package com.hanista.mobogram.mobo;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailSettingsCell;
import com.hanista.mobogram.ui.Cells.TextInfoCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.util.Locale;

/* renamed from: com.hanista.mobogram.mobo.f */
public class EmojiSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListView f914a;
    private EmojiSettingsActivity f915b;
    private int f916c;
    private int f917d;
    private int f918e;
    private int f919f;
    private int f920g;
    private int f921h;

    /* renamed from: com.hanista.mobogram.mobo.f.1 */
    class EmojiSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ EmojiSettingsActivity f882a;

        EmojiSettingsActivity(EmojiSettingsActivity emojiSettingsActivity) {
            this.f882a = emojiSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f882a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.f.2 */
    class EmojiSettingsActivity implements OnItemClickListener {
        final /* synthetic */ EmojiSettingsActivity f885a;

        /* renamed from: com.hanista.mobogram.mobo.f.2.1 */
        class EmojiSettingsActivity implements OnClickListener {
            final /* synthetic */ EmojiSettingsActivity f883a;

            EmojiSettingsActivity(EmojiSettingsActivity emojiSettingsActivity) {
                this.f883a = emojiSettingsActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("https://cafebazaar.ir/app/com.hanista.moboplus/?l=fa"));
                this.f883a.f885a.getParentActivity().startActivity(intent);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.f.2.2 */
        class EmojiSettingsActivity implements OnClickListener {
            final /* synthetic */ EmojiSettingsActivity f884a;

            EmojiSettingsActivity(EmojiSettingsActivity emojiSettingsActivity) {
                this.f884a = emojiSettingsActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                try {
                    String str = "com.hanista.moboplus";
                    Intent launchIntentForPackage = this.f884a.f885a.getParentActivity().getPackageManager().getLaunchIntentForPackage(str);
                    if (launchIntentForPackage == null) {
                        launchIntentForPackage = new Intent("android.intent.action.VIEW", Uri.parse("bazaar://details?id=" + str));
                    }
                    this.f884a.f885a.startActivityForResult(launchIntentForPackage, 503);
                } catch (Throwable e) {
                    this.f884a.f885a.startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("https://cafebazaar.ir/app/com.hanista.moboplus")), 503);
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        EmojiSettingsActivity(EmojiSettingsActivity emojiSettingsActivity) {
            this.f885a = emojiSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            boolean z2;
            TextDetailCheckCell textDetailCheckCell;
            if (i == this.f885a.f916c) {
                z2 = MoboConstants.f1310C;
                edit.putBoolean("favorite_emojis", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
            } else if (i == this.f885a.f917d) {
                z2 = MoboConstants.f1309B;
                edit.putBoolean("favorite_stickers", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
            } else if (i == this.f885a.f918e) {
                z2 = MoboConstants.f1352s;
                edit.putBoolean("confirm_before_send_sticker", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
            } else if (i == this.f885a.f920g) {
                z2 = MoboConstants.ad;
                edit.putBoolean("small_stickers", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
            } else if (i == this.f885a.f919f) {
                z2 = MoboConstants.aP;
                if (z2 || MoboUtils.m1716d()) {
                    edit.putBoolean("use_old_emojis", !z2);
                    edit.commit();
                    if (view instanceof TextDetailCheckCell) {
                        textDetailCheckCell = (TextDetailCheckCell) view;
                        if (z2) {
                            z = false;
                        }
                        textDetailCheckCell.setChecked(z);
                    }
                } else if (!MoboUtils.m1702a(this.f885a.getParentActivity(), "com.hanista.moboplus") || MoboUtils.m1705b(this.f885a.getParentActivity(), "com.hanista.moboplus") < 11) {
                    Builder builder = new Builder(this.f885a.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setMessage(LocaleController.getString("UseOldEmojisInstallAlert", C0338R.string.UseOldEmojisInstallAlert));
                    builder.setPositiveButton(!MoboUtils.m1702a(this.f885a.getParentActivity(), "com.hanista.moboplus") ? LocaleController.getString("DownloadMoboplus", C0338R.string.DownloadMoboplus) : LocaleController.getString("UpdateMoboplus", C0338R.string.UpdateMoboplus), new EmojiSettingsActivity(this));
                    this.f885a.showDialog(builder.create());
                    return;
                } else {
                    Builder builder2 = new Builder(this.f885a.getParentActivity());
                    builder2.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder2.setMessage(LocaleController.getString("UseOldEmojisEnableAlert", C0338R.string.UseOldEmojisEnableAlert));
                    builder2.setPositiveButton(LocaleController.getString("OpenMoboplus", C0338R.string.OpenMoboplus), new EmojiSettingsActivity(this));
                    this.f885a.showDialog(builder2.create());
                    return;
                }
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.f.a */
    private class EmojiSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ EmojiSettingsActivity f886a;
        private Context f887b;

        public EmojiSettingsActivity(EmojiSettingsActivity emojiSettingsActivity, Context context) {
            this.f886a = emojiSettingsActivity;
            this.f887b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f886a.f921h;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == this.f886a.f918e || i == this.f886a.f916c || i == this.f886a.f920g || i == this.f886a.f917d || i == this.f886a.f919f) ? 8 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f887b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f887b) : view;
                } else {
                    View textSettingsCell;
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f887b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f887b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f887b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                        if (i == this.f886a.f916c) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("FavoriteEmojis", C0338R.string.FavoriteEmojis), LocaleController.getString("FavoriteEmojisDetail", C0338R.string.FavoriteEmojisDetail), MoboConstants.f1310C, true);
                        } else if (i == this.f886a.f917d) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("FavoriteStickers", C0338R.string.FavoriteStickers), LocaleController.getString("FavoriteStickersDetail", C0338R.string.FavoriteStickersDetail), MoboConstants.f1309B, true);
                        } else if (i == this.f886a.f918e) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ConfirmationBeforSendSticker", C0338R.string.ConfirmationBeforSendSticker), LocaleController.getString("ConfirmationBeforSendStickerDetail", C0338R.string.ConfirmationBeforSendStickerDetail), sharedPreferences.getBoolean("confirm_before_send_sticker", false), true);
                        } else if (i == this.f886a.f920g) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowStickersSmaller", C0338R.string.ShowStickersSmaller), LocaleController.getString("ShowStickersSmallerDetail", C0338R.string.ShowStickersSmallerDetail), MoboConstants.ad, true);
                        } else if (i == this.f886a.f919f) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("UseOldEmojis", C0338R.string.UseOldEmojis), LocaleController.getString("UseOldEmojisDetail", C0338R.string.UseOldEmojisDetail), MoboConstants.aP, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f887b) : view;
                    } else {
                        if (itemViewType == 5) {
                            if (view != null) {
                                return view;
                            }
                            textSettingsCell = new TextInfoCell(this.f887b);
                            try {
                                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                                ((TextInfoCell) textSettingsCell).setText(String.format(Locale.US, "Mobogram for Android v%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)}));
                                return textSettingsCell;
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                                return textSettingsCell;
                            }
                        } else if (itemViewType != 6) {
                            return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f887b) : view;
                        } else {
                            textSettingsCell = view == null ? new TextDetailSettingsCell(this.f887b) : view;
                            TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                            return textSettingsCell;
                        }
                    }
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
            return i == this.f886a.f918e || i == this.f886a.f920g || i == this.f886a.f916c || i == this.f886a.f917d || i == this.f886a.f919f;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EmojiSettings", C0338R.string.EmojiSettings));
        this.actionBar.setActionBarMenuOnItemClick(new EmojiSettingsActivity(this));
        this.f915b = new EmojiSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f914a = new ListView(context);
        initThemeBackground(this.f914a);
        this.f914a.setDivider(null);
        this.f914a.setDividerHeight(0);
        this.f914a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f914a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f914a, LayoutHelper.createFrame(-1, -1, 51));
        this.f914a.setAdapter(this.f915b);
        this.f914a.setOnItemClickListener(new EmojiSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.f914a.invalidateViews();
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        this.f921h = 0;
        int i = this.f921h;
        this.f921h = i + 1;
        this.f916c = i;
        i = this.f921h;
        this.f921h = i + 1;
        this.f917d = i;
        i = this.f921h;
        this.f921h = i + 1;
        this.f918e = i;
        i = this.f921h;
        this.f921h = i + 1;
        this.f919f = i;
        i = this.f921h;
        this.f921h = i + 1;
        this.f920g = i;
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}
