package com.hanista.mobogram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PointerIconCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.BuildVars;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.SerializedData;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_help_getSupport;
import com.hanista.mobogram.tgnet.TLRPC.TL_help_support;
import com.hanista.mobogram.tgnet.TLRPC.TL_photos_photo;
import com.hanista.mobogram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_userProfilePhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailSettingsCell;
import com.hanista.mobogram.ui.Cells.TextInfoCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.AvatarUpdater;
import com.hanista.mobogram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.NumberPicker;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SettingsActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private static final int edit_name = 1;
    private static final int logout = 2;
    private int askQuestionRow;
    private int autoplayGifsRow;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int backgroundRow;
    private int cacheRow;
    private int clearLogsRow;
    private int contactsReimportRow;
    private int contactsSectionRow;
    private int contactsSortRow;
    private int customTabsRow;
    private int directShareRow;
    private int emojiRow;
    private int emptyRow;
    private int enableAnimationsRow;
    private int extraHeight;
    private View extraHeightView;
    private int languageRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int mediaDownloadSection;
    private int mediaDownloadSection2;
    private int messagesSectionRow;
    private int messagesSectionRow2;
    private int mobileDownloadRow;
    private TextView nameTextView;
    private int notificationRow;
    private int numberRow;
    private int numberSectionRow;
    private TextView onlineTextView;
    private int overscrollRow;
    private int privacyPolicyRow;
    private int privacyRow;
    private int raiseToSpeakRow;
    private int roamingDownloadRow;
    private int rowCount;
    private int saveToGalleryRow;
    private int sendByEnterRow;
    private int sendLogsRow;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private View shadowView;
    private int stickersRow;
    private int supportSectionRow;
    private int supportSectionRow2;
    private int switchBackendButtonRow;
    private int telegramFaqRow;
    private int textSizeRow;
    private int usernameRow;
    private int versionRow;
    private int wifiDownloadRow;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    /* renamed from: com.hanista.mobogram.ui.SettingsActivity.10 */
    class AnonymousClass10 implements RequestDelegate {
        final /* synthetic */ SharedPreferences val$preferences;
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.10.1 */
        class C18871 implements Runnable {
            final /* synthetic */ TL_help_support val$res;

            C18871(TL_help_support tL_help_support) {
                this.val$res = tL_help_support;
            }

            public void run() {
                Editor edit = AnonymousClass10.this.val$preferences.edit();
                edit.putInt("support_id", this.val$res.user.id);
                AbstractSerializedData serializedData = new SerializedData();
                this.val$res.user.serializeToStream(serializedData);
                edit.putString("support_user", Base64.encodeToString(serializedData.toByteArray(), 0));
                edit.commit();
                serializedData.cleanup();
                try {
                    AnonymousClass10.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                ArrayList arrayList = new ArrayList();
                arrayList.add(this.val$res.user);
                MessagesStorage.getInstance().putUsersAndChats(arrayList, null, true, true);
                MessagesController.getInstance().putUser(this.val$res.user, false);
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", this.val$res.user.id);
                SettingsActivity.this.presentFragment(new ChatActivity(bundle));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.10.2 */
        class C18882 implements Runnable {
            C18882() {
            }

            public void run() {
                try {
                    AnonymousClass10.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        AnonymousClass10(SharedPreferences sharedPreferences, ProgressDialog progressDialog) {
            this.val$preferences = sharedPreferences;
            this.val$progressDialog = progressDialog;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                AndroidUtilities.runOnUIThread(new C18871((TL_help_support) tLObject));
            } else {
                AndroidUtilities.runOnUIThread(new C18882());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SettingsActivity.11 */
    class AnonymousClass11 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$setVisible;

        AnonymousClass11(boolean z) {
            this.val$setVisible = z;
        }

        public void onAnimationEnd(Animator animator) {
            if (SettingsActivity.this.writeButtonAnimation != null && SettingsActivity.this.writeButtonAnimation.equals(animator)) {
                ImageView access$4500 = SettingsActivity.this.writeButton;
                int i = (!this.val$setVisible || UserConfig.isRobot) ? 8 : 0;
                access$4500.setVisibility(i);
                SettingsActivity.this.writeButtonAnimation = null;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SettingsActivity.1 */
    class C18891 implements AvatarUpdaterDelegate {

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.1.1 */
        class C18861 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.SettingsActivity.1.1.1 */
            class C18851 implements Runnable {
                C18851() {
                }

                public void run() {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.updateInterfaces;
                    Object[] objArr = new Object[SettingsActivity.edit_name];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_ALL);
                    instance.postNotificationName(i, objArr);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                    UserConfig.saveConfig(true);
                }
            }

            C18861() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                    if (user == null) {
                        user = UserConfig.getCurrentUser();
                        if (user != null) {
                            MessagesController.getInstance().putUser(user, false);
                        } else {
                            return;
                        }
                    }
                    UserConfig.setCurrentUser(user);
                    TL_photos_photo tL_photos_photo = (TL_photos_photo) tLObject;
                    ArrayList arrayList = tL_photos_photo.photo.sizes;
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 100);
                    PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, PointerIconCompat.TYPE_DEFAULT);
                    user.photo = new TL_userProfilePhoto();
                    user.photo.photo_id = tL_photos_photo.photo.id;
                    if (closestPhotoSizeWithSize != null) {
                        user.photo.photo_small = closestPhotoSizeWithSize.location;
                    }
                    if (closestPhotoSizeWithSize2 != null) {
                        user.photo.photo_big = closestPhotoSizeWithSize2.location;
                    } else if (closestPhotoSizeWithSize != null) {
                        user.photo.photo_small = closestPhotoSizeWithSize.location;
                    }
                    MessagesStorage.getInstance().clearUserPhotos(user.id);
                    arrayList = new ArrayList();
                    arrayList.add(user);
                    MessagesStorage.getInstance().putUsersAndChats(arrayList, null, false, true);
                    AndroidUtilities.runOnUIThread(new C18851());
                }
            }
        }

        C18891() {
        }

        public void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
            TLObject tL_photos_uploadProfilePhoto = new TL_photos_uploadProfilePhoto();
            tL_photos_uploadProfilePhoto.file = inputFile;
            ConnectionsManager.getInstance().sendRequest(tL_photos_uploadProfilePhoto, new C18861());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SettingsActivity.2 */
    class C18912 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.2.1 */
        class C18901 implements OnClickListener {
            C18901() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance().performLogout(true);
            }
        }

        C18912() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                SettingsActivity.this.finishFragment();
            } else if (i == SettingsActivity.edit_name) {
                SettingsActivity.this.presentFragment(new ChangeNameActivity());
            } else if (i == SettingsActivity.logout && SettingsActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("AreYouSureLogout", C0338R.string.AreYouSureLogout));
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C18901());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                SettingsActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SettingsActivity.3 */
    class C18923 extends FrameLayout {
        C18923(Context context) {
            super(context);
        }

        protected boolean drawChild(@NonNull Canvas canvas, @NonNull View view, long j) {
            if (view != SettingsActivity.this.listView) {
                return super.drawChild(canvas, view, j);
            }
            boolean drawChild = super.drawChild(canvas, view, j);
            if (SettingsActivity.this.parentLayout != null) {
                int childCount = getChildCount();
                int i = 0;
                while (i < childCount) {
                    View childAt = getChildAt(i);
                    if (childAt != view && (childAt instanceof ActionBar) && childAt.getVisibility() == 0) {
                        if (((ActionBar) childAt).getCastShadows()) {
                            i = childAt.getMeasuredHeight();
                            SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, i);
                        }
                        i = 0;
                        SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, i);
                    } else {
                        i += SettingsActivity.edit_name;
                    }
                }
                i = 0;
                SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, i);
            }
            return drawChild;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SettingsActivity.4 */
    class C19014 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.4.1 */
        class C18931 implements OnClickListener {
            final /* synthetic */ NumberPicker val$numberPicker;
            final /* synthetic */ int val$position;

            C18931(NumberPicker numberPicker, int i) {
                this.val$numberPicker = numberPicker;
                this.val$position = i;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                edit.putInt("fons_size", this.val$numberPicker.getValue());
                MessagesController.getInstance().fontSize = this.val$numberPicker.getValue();
                edit.commit();
                if (ThemeUtil.m2490b()) {
                    AdvanceTheme.m2280a(this.val$numberPicker.getValue());
                }
                if (SettingsActivity.this.listAdapter != null) {
                    SettingsActivity.this.listAdapter.notifyItemChanged(this.val$position);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.4.2 */
        class C18942 implements OnClickListener {
            C18942() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                SettingsActivity.this.performAskAQuestion();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.4.3 */
        class C18953 implements OnClickListener {
            C18953() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ConnectionsManager.getInstance().switchBackend();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.4.4 */
        class C18964 implements OnClickListener {
            final /* synthetic */ int val$position;

            C18964(int i) {
                this.val$position = i;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                edit.putInt("sortContactsBy", i);
                edit.commit();
                if (SettingsActivity.this.listAdapter != null) {
                    SettingsActivity.this.listAdapter.notifyItemChanged(this.val$position);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.4.5 */
        class C18975 implements View.OnClickListener {
            final /* synthetic */ boolean[] val$maskValues;

            C18975(boolean[] zArr) {
                this.val$maskValues = zArr;
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                this.val$maskValues[intValue] = !this.val$maskValues[intValue];
                checkBoxCell.setChecked(this.val$maskValues[intValue], true);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.4.6 */
        class C18986 implements View.OnClickListener {
            final /* synthetic */ boolean[] val$maskValues;
            final /* synthetic */ int val$position;

            C18986(boolean[] zArr, int i) {
                this.val$maskValues = zArr;
                this.val$position = i;
            }

            public void onClick(View view) {
                try {
                    if (SettingsActivity.this.visibleDialog != null) {
                        SettingsActivity.this.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                int i = 0;
                for (int i2 = 0; i2 < 6; i2 += SettingsActivity.edit_name) {
                    if (this.val$maskValues[i2]) {
                        if (i2 == 0) {
                            i |= SettingsActivity.edit_name;
                        } else if (i2 == SettingsActivity.edit_name) {
                            i |= SettingsActivity.logout;
                        } else if (i2 == SettingsActivity.logout) {
                            i |= 4;
                        } else if (i2 == 3) {
                            i |= 8;
                        } else if (i2 == 4) {
                            i |= 16;
                        } else if (i2 == 5) {
                            i |= 32;
                        }
                    }
                }
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                if (this.val$position == SettingsActivity.this.mobileDownloadRow) {
                    edit.putInt("mobileDataDownloadMask", i);
                    MediaController.m71a().f57b = i;
                } else if (this.val$position == SettingsActivity.this.wifiDownloadRow) {
                    edit.putInt("wifiDownloadMask", i);
                    MediaController.m71a().f58c = i;
                } else if (this.val$position == SettingsActivity.this.roamingDownloadRow) {
                    edit.putInt("roamingDownloadMask", i);
                    MediaController.m71a().f59d = i;
                }
                edit.commit();
                if (SettingsActivity.this.listAdapter != null) {
                    SettingsActivity.this.listAdapter.notifyItemChanged(this.val$position);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.4.7 */
        class C18997 implements View.OnClickListener {
            final /* synthetic */ boolean[] val$maskValues;

            C18997(boolean[] zArr) {
                this.val$maskValues = zArr;
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                this.val$maskValues[intValue] = !this.val$maskValues[intValue];
                checkBoxCell.setChecked(this.val$maskValues[intValue], true);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.4.8 */
        class C19008 implements View.OnClickListener {
            final /* synthetic */ boolean[] val$maskValues;
            final /* synthetic */ int val$position;

            C19008(boolean[] zArr, int i) {
                this.val$maskValues = zArr;
                this.val$position = i;
            }

            public void onClick(View view) {
                try {
                    if (SettingsActivity.this.visibleDialog != null) {
                        SettingsActivity.this.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                MessagesController instance = MessagesController.getInstance();
                boolean z = this.val$maskValues[0];
                instance.allowBigEmoji = z;
                edit.putBoolean("allowBigEmoji", z);
                instance = MessagesController.getInstance();
                z = this.val$maskValues[SettingsActivity.edit_name];
                instance.useSystemEmoji = z;
                edit.putBoolean("useSystemEmoji", z);
                edit.commit();
                if (SettingsActivity.this.listAdapter != null) {
                    SettingsActivity.this.listAdapter.notifyItemChanged(this.val$position);
                }
            }
        }

        C19014() {
        }

        public void onItemClick(View view, int i) {
            Builder builder;
            if (i == SettingsActivity.this.textSizeRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("TextSize", C0338R.string.TextSize));
                    View numberPicker = new NumberPicker(SettingsActivity.this.getParentActivity());
                    numberPicker.setMinValue(12);
                    numberPicker.setMaxValue(30);
                    numberPicker.setValue(MessagesController.getInstance().fontSize);
                    builder.setView(numberPicker);
                    builder.setNegativeButton(LocaleController.getString("Done", C0338R.string.Done), new C18931(numberPicker, i));
                    SettingsActivity.this.showDialog(builder.create());
                }
            } else if (i == SettingsActivity.this.enableAnimationsRow) {
                r0 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                r1 = r0.getBoolean("view_animations", true);
                r2 = r0.edit();
                r2.putBoolean("view_animations", !r1);
                r2.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!r1);
                }
            } else if (i == SettingsActivity.this.notificationRow) {
                SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
            } else if (i == SettingsActivity.this.backgroundRow) {
                SettingsActivity.this.presentFragment(new WallpapersActivity());
            } else if (i == SettingsActivity.this.askQuestionRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    r0 = new TextView(SettingsActivity.this.getParentActivity());
                    r0.setTypeface(FontUtil.m1176a().m1161d());
                    r0.setText(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", C0338R.string.AskAQuestionInfo)));
                    r0.setTextSize(18.0f);
                    r0.setLinkTextColor(Theme.MSG_LINK_TEXT_COLOR);
                    r0.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f));
                    r0.setMovementMethod(new LinkMovementMethodMy());
                    Builder builder2 = new Builder(SettingsActivity.this.getParentActivity());
                    builder2.setView(r0);
                    builder2.setPositiveButton(LocaleController.getString("AskButton", C0338R.string.AskButton), new C18942());
                    builder2.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    SettingsActivity.this.showDialog(builder2.create());
                }
            } else if (i == SettingsActivity.this.sendLogsRow) {
                SettingsActivity.this.sendLogs();
            } else if (i == SettingsActivity.this.clearLogsRow) {
                FileLog.cleanupLogs();
            } else if (i == SettingsActivity.this.sendByEnterRow) {
                r0 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                r1 = r0.getBoolean("send_by_enter", false);
                r2 = r0.edit();
                r2.putBoolean("send_by_enter", !r1);
                r2.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!r1);
                }
            } else if (i == SettingsActivity.this.raiseToSpeakRow) {
                MediaController.m71a().m195w();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(MediaController.m71a().m141C());
                }
            } else if (i == SettingsActivity.this.autoplayGifsRow) {
                MediaController.m71a().m194v();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(MediaController.m71a().m140B());
                }
            } else if (i == SettingsActivity.this.saveToGalleryRow) {
                MediaController.m71a().m193u();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(MediaController.m71a().m139A());
                }
            } else if (i == SettingsActivity.this.customTabsRow) {
                MediaController.m71a().m196x();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(MediaController.m71a().m142D());
                }
            } else if (i == SettingsActivity.this.directShareRow) {
                MediaController.m71a().m197y();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(MediaController.m71a().m143E());
                }
            } else if (i == SettingsActivity.this.privacyRow) {
                SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
            } else if (i == SettingsActivity.this.languageRow) {
                SettingsActivity.this.presentFragment(new LanguageSelectActivity());
            } else if (i == SettingsActivity.this.switchBackendButtonRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSure", C0338R.string.AreYouSure));
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C18953());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    SettingsActivity.this.showDialog(builder.create());
                }
            } else if (i == SettingsActivity.this.telegramFaqRow) {
                Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("TelegramFaqUrl", C0338R.string.TelegramFaqUrl));
            } else if (i == SettingsActivity.this.privacyPolicyRow) {
                Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", C0338R.string.PrivacyPolicyUrl));
            } else if (i == SettingsActivity.this.contactsReimportRow) {
            } else {
                if (i == SettingsActivity.this.contactsSortRow) {
                    if (SettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(SettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("SortBy", C0338R.string.SortBy));
                        builder.setItems(new CharSequence[]{LocaleController.getString("Default", C0338R.string.Default), LocaleController.getString("SortFirstName", C0338R.string.SortFirstName), LocaleController.getString("SortLastName", C0338R.string.SortLastName)}, new C18964(i));
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        SettingsActivity.this.showDialog(builder.create());
                    }
                } else if (i == SettingsActivity.this.wifiDownloadRow || i == SettingsActivity.this.mobileDownloadRow || i == SettingsActivity.this.roamingDownloadRow) {
                    if (SettingsActivity.this.getParentActivity() != null) {
                        boolean[] zArr = new boolean[6];
                        BottomSheet.Builder builder3 = new BottomSheet.Builder(SettingsActivity.this.getParentActivity());
                        r0 = 0;
                        if (i == SettingsActivity.this.mobileDownloadRow) {
                            r0 = MediaController.m71a().f57b;
                        } else if (i == SettingsActivity.this.wifiDownloadRow) {
                            r0 = MediaController.m71a().f58c;
                        } else if (i == SettingsActivity.this.roamingDownloadRow) {
                            r0 = MediaController.m71a().f59d;
                        }
                        builder3.setApplyTopPadding(false);
                        builder3.setApplyBottomPadding(false);
                        r5 = new LinearLayout(SettingsActivity.this.getParentActivity());
                        r5.setOrientation(SettingsActivity.edit_name);
                        for (int i2 = 0; i2 < 6; i2 += SettingsActivity.edit_name) {
                            r1 = null;
                            if (i2 == 0) {
                                zArr[i2] = (r0 & SettingsActivity.edit_name) != 0;
                                r1 = LocaleController.getString("AttachPhoto", C0338R.string.AttachPhoto);
                            } else if (i2 == SettingsActivity.edit_name) {
                                zArr[i2] = (r0 & SettingsActivity.logout) != 0;
                                r1 = LocaleController.getString("AttachAudio", C0338R.string.AttachAudio);
                            } else if (i2 == SettingsActivity.logout) {
                                zArr[i2] = (r0 & 4) != 0;
                                r1 = LocaleController.getString("AttachVideo", C0338R.string.AttachVideo);
                            } else if (i2 == 3) {
                                zArr[i2] = (r0 & 8) != 0;
                                r1 = LocaleController.getString("AttachDocument", C0338R.string.AttachDocument);
                            } else if (i2 == 4) {
                                zArr[i2] = (r0 & 16) != 0;
                                r1 = LocaleController.getString("AttachMusic", C0338R.string.AttachMusic);
                            } else if (i2 == 5) {
                                zArr[i2] = (r0 & 32) != 0;
                                r1 = LocaleController.getString("AttachGif", C0338R.string.AttachGif);
                            }
                            View checkBoxCell = new CheckBoxCell(SettingsActivity.this.getParentActivity());
                            checkBoxCell.setTag(Integer.valueOf(i2));
                            checkBoxCell.setBackgroundResource(C0338R.drawable.list_selector);
                            r5.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                            checkBoxCell.setText(r1, TtmlNode.ANONYMOUS_REGION_ID, zArr[i2], true);
                            checkBoxCell.setOnClickListener(new C18975(zArr));
                        }
                        r0 = new BottomSheetCell(SettingsActivity.this.getParentActivity(), SettingsActivity.edit_name);
                        r0.setBackgroundResource(C0338R.drawable.list_selector);
                        r0.setTextAndIcon(LocaleController.getString("Save", C0338R.string.Save).toUpperCase(), 0);
                        r0.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
                        r0.setOnClickListener(new C18986(zArr, i));
                        r5.addView(r0, LayoutHelper.createLinear(-1, 48));
                        builder3.setCustomView(r5);
                        SettingsActivity.this.showDialog(builder3.create());
                    }
                } else if (i == SettingsActivity.this.usernameRow) {
                    SettingsActivity.this.presentFragment(new ChangeUsernameActivity());
                } else if (i == SettingsActivity.this.numberRow) {
                    SettingsActivity.this.presentFragment(new ChangePhoneHelpActivity());
                } else if (i == SettingsActivity.this.stickersRow) {
                    SettingsActivity.this.presentFragment(new StickersActivity(0));
                } else if (i == SettingsActivity.this.cacheRow) {
                    SettingsActivity.this.presentFragment(new CacheControlActivity());
                } else if (i == SettingsActivity.this.emojiRow && SettingsActivity.this.getParentActivity() != null) {
                    boolean[] zArr2 = new boolean[SettingsActivity.logout];
                    BottomSheet.Builder builder4 = new BottomSheet.Builder(SettingsActivity.this.getParentActivity());
                    builder4.setApplyTopPadding(false);
                    builder4.setApplyBottomPadding(false);
                    View linearLayout = new LinearLayout(SettingsActivity.this.getParentActivity());
                    linearLayout.setOrientation(SettingsActivity.edit_name);
                    r0 = 0;
                    while (true) {
                        if (r0 < (VERSION.SDK_INT >= 19 ? SettingsActivity.logout : SettingsActivity.edit_name)) {
                            r1 = null;
                            if (r0 == 0) {
                                zArr2[r0] = MessagesController.getInstance().allowBigEmoji;
                                r1 = LocaleController.getString("EmojiBigSize", C0338R.string.EmojiBigSize);
                            } else if (r0 == SettingsActivity.edit_name) {
                                zArr2[r0] = MessagesController.getInstance().useSystemEmoji;
                                r1 = LocaleController.getString("EmojiUseDefault", C0338R.string.EmojiUseDefault);
                            }
                            r5 = new CheckBoxCell(SettingsActivity.this.getParentActivity());
                            r5.setTag(Integer.valueOf(r0));
                            r5.setBackgroundResource(C0338R.drawable.list_selector);
                            linearLayout.addView(r5, LayoutHelper.createLinear(-1, 48));
                            r5.setText(r1, TtmlNode.ANONYMOUS_REGION_ID, zArr2[r0], true);
                            r5.setOnClickListener(new C18997(zArr2));
                            r0 += SettingsActivity.edit_name;
                        } else {
                            r0 = new BottomSheetCell(SettingsActivity.this.getParentActivity(), SettingsActivity.edit_name);
                            r0.setBackgroundResource(C0338R.drawable.list_selector);
                            r0.setTextAndIcon(LocaleController.getString("Save", C0338R.string.Save).toUpperCase(), 0);
                            r0.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
                            r0.setOnClickListener(new C19008(zArr2, i));
                            linearLayout.addView(r0, LayoutHelper.createLinear(-1, 48));
                            builder4.setCustomView(linearLayout);
                            SettingsActivity.this.showDialog(builder4.create());
                            return;
                        }
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SettingsActivity.5 */
    class C19025 implements OnItemLongClickListener {
        private int pressCount;

        C19025() {
            this.pressCount = 0;
        }

        public boolean onItemClick(View view, int i) {
            if (i == SettingsActivity.this.versionRow) {
            }
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SettingsActivity.6 */
    class C19036 implements View.OnClickListener {
        C19036() {
        }

        public void onClick(View view) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
            if (user != null && user.photo != null && user.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(SettingsActivity.this.getParentActivity());
                PhotoViewer.getInstance().openPhoto(user.photo.photo_big, SettingsActivity.this);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SettingsActivity.7 */
    class C19047 extends ViewOutlineProvider {
        C19047() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SettingsActivity.8 */
    class C19068 implements View.OnClickListener {

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.8.1 */
        class C19051 implements OnClickListener {
            C19051() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    SettingsActivity.this.avatarUpdater.openCamera();
                } else if (i == SettingsActivity.edit_name) {
                    SettingsActivity.this.avatarUpdater.openGallery();
                } else if (i == SettingsActivity.logout) {
                    MessagesController.getInstance().deleteUserPhoto(null);
                }
            }
        }

        C19068() {
        }

        public void onClick(View view) {
            if (SettingsActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                if (user == null) {
                    user = UserConfig.getCurrentUser();
                }
                if (user != null) {
                    CharSequence[] charSequenceArr;
                    if (user.photo == null || user.photo.photo_big == null || (user.photo instanceof TL_userProfilePhotoEmpty)) {
                        charSequenceArr = new CharSequence[SettingsActivity.logout];
                        charSequenceArr[0] = LocaleController.getString("FromCamera", C0338R.string.FromCamera);
                        charSequenceArr[SettingsActivity.edit_name] = LocaleController.getString("FromGalley", C0338R.string.FromGalley);
                    } else {
                        charSequenceArr = new CharSequence[]{LocaleController.getString("FromCamera", C0338R.string.FromCamera), LocaleController.getString("FromGalley", C0338R.string.FromGalley), LocaleController.getString("DeletePhoto", C0338R.string.DeletePhoto)};
                    }
                    builder.setItems(charSequenceArr, new C19051());
                    SettingsActivity.this.showDialog(builder.create());
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SettingsActivity.9 */
    class C19079 extends OnScrollListener {
        C19079() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            int i3 = 0;
            if (SettingsActivity.this.layoutManager.getItemCount() != 0) {
                View childAt = recyclerView.getChildAt(0);
                if (childAt != null) {
                    if (SettingsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                        int dp = AndroidUtilities.dp(88.0f);
                        if (childAt.getTop() < 0) {
                            i3 = childAt.getTop();
                        }
                        i3 += dp;
                    }
                    if (SettingsActivity.this.extraHeight != i3) {
                        SettingsActivity.this.extraHeight = i3;
                        SettingsActivity.this.needLayout();
                    }
                }
            }
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

    private class ListAdapter extends Adapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.ListAdapter.1 */
        class C19081 extends TextSettingsCell {
            C19081(Context context) {
                super(context);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == SettingsActivity.logout)) {
                    getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.ListAdapter.2 */
        class C19092 extends TextCheckCell {
            C19092(Context context) {
                super(context);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == SettingsActivity.logout)) {
                    getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.ListAdapter.3 */
        class C19103 extends TextInfoCell {
            C19103(Context context) {
                super(context);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == SettingsActivity.logout)) {
                    getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.SettingsActivity.ListAdapter.4 */
        class C19114 extends TextDetailSettingsCell {
            C19114(Context context) {
                super(context);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == SettingsActivity.logout)) {
                    getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        private class Holder extends ViewHolder {
            public Holder(View view) {
                super(view);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return SettingsActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            return (i == SettingsActivity.this.emptyRow || i == SettingsActivity.this.overscrollRow) ? 0 : (i == SettingsActivity.this.settingsSectionRow || i == SettingsActivity.this.supportSectionRow || i == SettingsActivity.this.messagesSectionRow || i == SettingsActivity.this.mediaDownloadSection || i == SettingsActivity.this.contactsSectionRow) ? SettingsActivity.edit_name : (i == SettingsActivity.this.enableAnimationsRow || i == SettingsActivity.this.sendByEnterRow || i == SettingsActivity.this.saveToGalleryRow || i == SettingsActivity.this.autoplayGifsRow || i == SettingsActivity.this.raiseToSpeakRow || i == SettingsActivity.this.customTabsRow || i == SettingsActivity.this.directShareRow) ? 3 : (i == SettingsActivity.this.notificationRow || i == SettingsActivity.this.backgroundRow || i == SettingsActivity.this.askQuestionRow || i == SettingsActivity.this.sendLogsRow || i == SettingsActivity.this.privacyRow || i == SettingsActivity.this.clearLogsRow || i == SettingsActivity.this.switchBackendButtonRow || i == SettingsActivity.this.telegramFaqRow || i == SettingsActivity.this.contactsReimportRow || i == SettingsActivity.this.textSizeRow || i == SettingsActivity.this.languageRow || i == SettingsActivity.this.contactsSortRow || i == SettingsActivity.this.stickersRow || i == SettingsActivity.this.cacheRow || i == SettingsActivity.this.privacyPolicyRow || i == SettingsActivity.this.emojiRow) ? SettingsActivity.logout : i == SettingsActivity.this.versionRow ? 5 : (i == SettingsActivity.this.wifiDownloadRow || i == SettingsActivity.this.mobileDownloadRow || i == SettingsActivity.this.roamingDownloadRow || i == SettingsActivity.this.numberRow || i == SettingsActivity.this.usernameRow) ? 6 : (i == SettingsActivity.this.settingsSectionRow2 || i == SettingsActivity.this.messagesSectionRow2 || i == SettingsActivity.this.supportSectionRow2 || i == SettingsActivity.this.numberSectionRow || i == SettingsActivity.this.mediaDownloadSection2) ? 4 : SettingsActivity.logout;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = true;
            String format;
            switch (viewHolder.getItemViewType()) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    if (i == SettingsActivity.this.overscrollRow) {
                        ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(88.0f));
                    } else {
                        ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(16.0f));
                    }
                    z = false;
                    break;
                case SettingsActivity.logout /*2*/:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    int size;
                    String string;
                    Object[] objArr;
                    if (i != SettingsActivity.this.textSizeRow) {
                        if (i != SettingsActivity.this.languageRow) {
                            if (i != SettingsActivity.this.contactsSortRow) {
                                if (i != SettingsActivity.this.notificationRow) {
                                    if (i != SettingsActivity.this.backgroundRow) {
                                        if (i != SettingsActivity.this.sendLogsRow) {
                                            if (i != SettingsActivity.this.clearLogsRow) {
                                                if (i != SettingsActivity.this.askQuestionRow) {
                                                    if (i != SettingsActivity.this.privacyRow) {
                                                        if (i != SettingsActivity.this.switchBackendButtonRow) {
                                                            if (i != SettingsActivity.this.telegramFaqRow) {
                                                                if (i != SettingsActivity.this.contactsReimportRow) {
                                                                    if (i != SettingsActivity.this.stickersRow) {
                                                                        if (i != SettingsActivity.this.cacheRow) {
                                                                            if (i != SettingsActivity.this.privacyPolicyRow) {
                                                                                if (i == SettingsActivity.this.emojiRow) {
                                                                                    textSettingsCell.setText(LocaleController.getString("Emoji", C0338R.string.Emoji), true);
                                                                                    break;
                                                                                }
                                                                            }
                                                                            textSettingsCell.setText(LocaleController.getString("PrivacyPolicy", C0338R.string.PrivacyPolicy), true);
                                                                            break;
                                                                        }
                                                                        textSettingsCell.setText(LocaleController.getString("CacheSettings", C0338R.string.CacheSettings), true);
                                                                        break;
                                                                    }
                                                                    size = StickersQuery.getUnreadStickerSets().size();
                                                                    string = LocaleController.getString("Stickers", C0338R.string.Stickers);
                                                                    if (size != 0) {
                                                                        objArr = new Object[SettingsActivity.edit_name];
                                                                        objArr[0] = Integer.valueOf(size);
                                                                        format = String.format("%d", objArr);
                                                                    } else {
                                                                        format = TtmlNode.ANONYMOUS_REGION_ID;
                                                                    }
                                                                    textSettingsCell.setTextAndValue(string, format, true);
                                                                    break;
                                                                }
                                                                textSettingsCell.setText(LocaleController.getString("ImportContacts", C0338R.string.ImportContacts), true);
                                                                break;
                                                            }
                                                            textSettingsCell.setText(LocaleController.getString("TelegramFAQ", C0338R.string.TelegramFaq), true);
                                                            break;
                                                        }
                                                        textSettingsCell.setText("Switch Backend", true);
                                                        break;
                                                    }
                                                    textSettingsCell.setText(LocaleController.getString("PrivacySettings", C0338R.string.PrivacySettings), true);
                                                    break;
                                                }
                                                textSettingsCell.setText(LocaleController.getString("AskAQuestion", C0338R.string.AskAQuestion), true);
                                                break;
                                            }
                                            textSettingsCell.setText("Clear Logs", true);
                                            break;
                                        }
                                        textSettingsCell.setText("Send Logs", true);
                                        break;
                                    }
                                    textSettingsCell.setText(LocaleController.getString("ChatBackground", C0338R.string.ChatBackground), true);
                                    break;
                                }
                                textSettingsCell.setText(LocaleController.getString("NotificationsAndSounds", C0338R.string.NotificationsAndSounds), true);
                                break;
                            }
                            size = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("sortContactsBy", 0);
                            format = size == 0 ? LocaleController.getString("Default", C0338R.string.Default) : size == SettingsActivity.edit_name ? LocaleController.getString("FirstName", C0338R.string.SortFirstName) : LocaleController.getString("LastName", C0338R.string.SortLastName);
                            textSettingsCell.setTextAndValue(LocaleController.getString("SortBy", C0338R.string.SortBy), format, true);
                            break;
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Language", C0338R.string.Language), LocaleController.getCurrentLanguageName(), true);
                        break;
                    }
                    size = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
                    if (ThemeUtil.m2490b()) {
                        size = AdvanceTheme.bm;
                    }
                    string = LocaleController.getString("TextSize", C0338R.string.TextSize);
                    objArr = new Object[SettingsActivity.edit_name];
                    objArr[0] = Integer.valueOf(size);
                    textSettingsCell.setTextAndValue(string, String.format("%d", objArr), true);
                    break;
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (i != SettingsActivity.this.enableAnimationsRow) {
                        if (i != SettingsActivity.this.sendByEnterRow) {
                            if (i != SettingsActivity.this.saveToGalleryRow) {
                                if (i != SettingsActivity.this.autoplayGifsRow) {
                                    if (i != SettingsActivity.this.raiseToSpeakRow) {
                                        if (i != SettingsActivity.this.customTabsRow) {
                                            if (i == SettingsActivity.this.directShareRow) {
                                                textCheckCell.setTextAndValueAndCheck(LocaleController.getString("DirectShare", C0338R.string.DirectShare), LocaleController.getString("DirectShareInfo", C0338R.string.DirectShareInfo), MediaController.m71a().m143E(), false, true);
                                                break;
                                            }
                                        }
                                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", C0338R.string.ChromeCustomTabs), LocaleController.getString("ChromeCustomTabsInfo", C0338R.string.ChromeCustomTabsInfo), MediaController.m71a().m142D(), false, true);
                                        break;
                                    }
                                    textCheckCell.setTextAndCheck(LocaleController.getString("RaiseToSpeak", C0338R.string.RaiseToSpeak), MediaController.m71a().m141C(), true);
                                    break;
                                }
                                textCheckCell.setTextAndCheck(LocaleController.getString("AutoplayGifs", C0338R.string.AutoplayGifs), MediaController.m71a().m140B(), true);
                                break;
                            }
                            textCheckCell.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", C0338R.string.SaveToGallerySettings), MediaController.m71a().m139A(), false);
                            break;
                        }
                        textCheckCell.setTextAndCheck(LocaleController.getString("SendByEnter", C0338R.string.SendByEnter), sharedPreferences.getBoolean("send_by_enter", false), false);
                        break;
                    }
                    textCheckCell.setTextAndCheck(LocaleController.getString("EnableAnimations", C0338R.string.EnableAnimations), sharedPreferences.getBoolean("view_animations", true), false);
                    break;
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    if (i != SettingsActivity.this.settingsSectionRow2) {
                        if (i != SettingsActivity.this.supportSectionRow2) {
                            if (i != SettingsActivity.this.messagesSectionRow2) {
                                if (i != SettingsActivity.this.mediaDownloadSection2) {
                                    if (i == SettingsActivity.this.numberSectionRow) {
                                        ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("Info", C0338R.string.Info));
                                        break;
                                    }
                                }
                                ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("AutomaticMediaDownload", C0338R.string.AutomaticMediaDownload));
                                break;
                            }
                            ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("MessagesSettings", C0338R.string.MessagesSettings));
                            break;
                        }
                        ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("Support", C0338R.string.Support));
                        break;
                    }
                    ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("SETTINGS", C0338R.string.SETTINGS));
                    break;
                    break;
                case Method.TRACE /*6*/:
                    TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) viewHolder.itemView;
                    if (i != SettingsActivity.this.mobileDownloadRow && i != SettingsActivity.this.wifiDownloadRow && i != SettingsActivity.this.roamingDownloadRow) {
                        User currentUser;
                        if (i != SettingsActivity.this.numberRow) {
                            if (i == SettingsActivity.this.usernameRow) {
                                currentUser = UserConfig.getCurrentUser();
                                format = (currentUser == null || currentUser.username == null || currentUser.username.length() == 0) ? LocaleController.getString("UsernameEmpty", C0338R.string.UsernameEmpty) : "@" + currentUser.username;
                                textDetailSettingsCell.setTextAndValue(format, LocaleController.getString("Username", C0338R.string.Username), false);
                                break;
                            }
                        }
                        currentUser = UserConfig.getCurrentUser();
                        format = (currentUser == null || currentUser.phone == null || currentUser.phone.length() == 0) ? LocaleController.getString("NumberUnknown", C0338R.string.NumberUnknown) : PhoneFormat.getInstance().format("+" + currentUser.phone);
                        textDetailSettingsCell.setTextAndValue(format, LocaleController.getString("Phone", C0338R.string.Phone), true);
                        break;
                    }
                    int i2;
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (i == SettingsActivity.this.mobileDownloadRow) {
                        format = LocaleController.getString("WhenUsingMobileData", C0338R.string.WhenUsingMobileData);
                        i2 = MediaController.m71a().f57b;
                    } else if (i == SettingsActivity.this.wifiDownloadRow) {
                        format = LocaleController.getString("WhenConnectedOnWiFi", C0338R.string.WhenConnectedOnWiFi);
                        i2 = MediaController.m71a().f58c;
                    } else {
                        format = LocaleController.getString("WhenRoaming", C0338R.string.WhenRoaming);
                        i2 = MediaController.m71a().f59d;
                    }
                    String str = TtmlNode.ANONYMOUS_REGION_ID;
                    if ((i2 & SettingsActivity.edit_name) != 0) {
                        str = str + LocaleController.getString("AttachPhoto", C0338R.string.AttachPhoto);
                    }
                    if ((i2 & SettingsActivity.logout) != 0) {
                        if (str.length() != 0) {
                            str = str + ", ";
                        }
                        str = str + LocaleController.getString("AttachAudio", C0338R.string.AttachAudio);
                    }
                    if ((i2 & 4) != 0) {
                        if (str.length() != 0) {
                            str = str + ", ";
                        }
                        str = str + LocaleController.getString("AttachVideo", C0338R.string.AttachVideo);
                    }
                    if ((i2 & 8) != 0) {
                        if (str.length() != 0) {
                            str = str + ", ";
                        }
                        str = str + LocaleController.getString("AttachDocument", C0338R.string.AttachDocument);
                    }
                    if ((i2 & 16) != 0) {
                        if (str.length() != 0) {
                            str = str + ", ";
                        }
                        str = str + LocaleController.getString("AttachMusic", C0338R.string.AttachMusic);
                    }
                    if ((i2 & 32) != 0) {
                        str = (str.length() != 0 ? str + ", " : str) + LocaleController.getString("AttachGif", C0338R.string.AttachGif);
                    }
                    if (str.length() == 0) {
                        str = LocaleController.getString("NoMediaAutoDownload", C0338R.string.NoMediaAutoDownload);
                    }
                    textDetailSettingsCell.setTextAndValue(format, str, true);
                    break;
                    break;
                default:
                    z = false;
                    break;
            }
            if (!z) {
                return;
            }
            if (i == SettingsActivity.this.textSizeRow || i == SettingsActivity.this.enableAnimationsRow || i == SettingsActivity.this.notificationRow || i == SettingsActivity.this.backgroundRow || i == SettingsActivity.this.numberRow || i == SettingsActivity.this.askQuestionRow || i == SettingsActivity.this.sendLogsRow || i == SettingsActivity.this.sendByEnterRow || i == SettingsActivity.this.autoplayGifsRow || i == SettingsActivity.this.privacyRow || i == SettingsActivity.this.wifiDownloadRow || i == SettingsActivity.this.mobileDownloadRow || i == SettingsActivity.this.clearLogsRow || i == SettingsActivity.this.roamingDownloadRow || i == SettingsActivity.this.languageRow || i == SettingsActivity.this.usernameRow || i == SettingsActivity.this.switchBackendButtonRow || i == SettingsActivity.this.telegramFaqRow || i == SettingsActivity.this.contactsSortRow || i == SettingsActivity.this.contactsReimportRow || i == SettingsActivity.this.saveToGalleryRow || i == SettingsActivity.this.stickersRow || i == SettingsActivity.this.cacheRow || i == SettingsActivity.this.raiseToSpeakRow || i == SettingsActivity.this.privacyPolicyRow || i == SettingsActivity.this.customTabsRow || i == SettingsActivity.this.directShareRow || i == SettingsActivity.this.versionRow || i == SettingsActivity.this.emojiRow) {
                if (viewHolder.itemView.getBackground() == null) {
                    viewHolder.itemView.setBackgroundResource(C0338R.drawable.list_selector);
                }
            } else if (viewHolder.itemView.getBackground() != null) {
                viewHolder.itemView.setBackgroundDrawable(null);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new EmptyCell(this.mContext);
                    break;
                case SettingsActivity.edit_name /*1*/:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case SettingsActivity.logout /*2*/:
                    view = new C19081(this.mContext);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    view = new C19092(this.mContext);
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    view = new HeaderCell(this.mContext);
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    view = new C19103(this.mContext);
                    try {
                        Object obj;
                        PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        int i2 = packageInfo.versionCode / 10;
                        String str = TtmlNode.ANONYMOUS_REGION_ID;
                        String str2;
                        switch (packageInfo.versionCode % 10) {
                            case VideoPlayer.TRACK_DEFAULT /*0*/:
                                str2 = "arm";
                                break;
                            case SettingsActivity.edit_name /*1*/:
                                str2 = "arm-v7a";
                                break;
                            case SettingsActivity.logout /*2*/:
                                str2 = "x86";
                                break;
                            case VideoPlayer.STATE_BUFFERING /*3*/:
                                str2 = "universal";
                                break;
                            default:
                                obj = str;
                                break;
                        }
                        ((TextInfoCell) view).setText(String.format(Locale.US, "Telegram for Android v%s (%d) %s", new Object[]{packageInfo.versionName, Integer.valueOf(i2), obj}));
                        break;
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                        break;
                    }
                case Method.TRACE /*6*/:
                    view = new C19114(this.mContext);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }
    }

    public SettingsActivity() {
        this.avatarUpdater = new AvatarUpdater();
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (SettingsActivity.this.fragmentView != null) {
                        SettingsActivity.this.needLayout();
                        SettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            initThemeActionBar();
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_other).setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
        }
    }

    private void needLayout() {
        int i = 0;
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        if (this.listView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
                this.extraHeightView.setTranslationY((float) currentActionBarHeight);
            }
        }
        if (this.avatarImage != null) {
            float dp = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.extraHeightView.setScaleY(dp);
            this.shadowView.setTranslationY((float) (currentActionBarHeight + this.extraHeight));
            this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.dp(29.5f)));
            boolean z = dp > DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
            if (z != (this.writeButton.getTag() == null ? edit_name : false)) {
                AnimatorSet animatorSet;
                if (z) {
                    this.writeButton.setTag(null);
                    this.writeButton.setVisibility(UserConfig.isRobot ? 8 : 0);
                } else {
                    this.writeButton.setTag(Integer.valueOf(0));
                }
                if (this.writeButtonAnimation != null) {
                    animatorSet = this.writeButtonAnimation;
                    this.writeButtonAnimation = null;
                    animatorSet.cancel();
                }
                this.writeButtonAnimation = new AnimatorSet();
                Animator[] animatorArr;
                float[] fArr;
                float[] fArr2;
                if (z) {
                    this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                    animatorSet = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    fArr = new float[edit_name];
                    fArr[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr);
                    fArr = new float[edit_name];
                    fArr[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    animatorArr[edit_name] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr);
                    fArr2 = new float[edit_name];
                    fArr2[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    animatorArr[logout] = ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr2);
                    animatorSet.playTogether(animatorArr);
                } else {
                    this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                    animatorSet = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    fArr = new float[edit_name];
                    fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr);
                    fArr = new float[edit_name];
                    fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                    animatorArr[edit_name] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr);
                    fArr2 = new float[edit_name];
                    fArr2[0] = 0.0f;
                    animatorArr[logout] = ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr2);
                    animatorSet.playTogether(animatorArr);
                }
                this.writeButtonAnimation.setDuration(150);
                this.writeButtonAnimation.addListener(new AnonymousClass11(z));
                this.writeButtonAnimation.start();
            }
            if (ThemeUtil.m2490b()) {
                int i2 = AdvanceTheme.f2505p;
                this.avatarImage.setScaleX((((float) i2) + (18.0f * dp)) / (((float) i2) * DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                this.avatarImage.setScaleY((((float) i2) + (18.0f * dp)) / (((float) i2) * DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            } else {
                this.avatarImage.setScaleX(((18.0f * dp) + 42.0f) / 42.0f);
                this.avatarImage.setScaleY(((18.0f * dp) + 42.0f) / 42.0f);
            }
            if (this.actionBar.getOccupyStatusBar()) {
                i = AndroidUtilities.statusBarHeight;
            }
            float currentActionBarHeight2 = ((((float) i) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT + dp))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * dp);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * dp);
            this.avatarImage.setTranslationY((float) Math.ceil((double) currentActionBarHeight2));
            this.nameTextView.setTranslationX((-21.0f * AndroidUtilities.density) * dp);
            this.nameTextView.setTranslationY((((float) Math.floor((double) currentActionBarHeight2)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((7.0f * AndroidUtilities.density) * dp))));
            this.onlineTextView.setTranslationX((-21.0f * AndroidUtilities.density) * dp);
            this.onlineTextView.setTranslationY((((float) Math.floor((double) currentActionBarHeight2)) + ((float) AndroidUtilities.dp(22.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * dp));
            this.nameTextView.setScaleX((0.12f * dp) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.nameTextView.setScaleY((0.12f * dp) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
    }

    private void performAskAQuestion() {
        User user;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        int i = sharedPreferences.getInt("support_id", 0);
        if (i != 0) {
            user = MessagesController.getInstance().getUser(Integer.valueOf(i));
            if (user == null) {
                String string = sharedPreferences.getString("support_user", null);
                if (string != null) {
                    try {
                        byte[] decode = Base64.decode(string, 0);
                        if (decode != null) {
                            AbstractSerializedData serializedData = new SerializedData(decode);
                            user = User.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                            if (user != null && user.id == 333000) {
                                user = null;
                            }
                            serializedData.cleanup();
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                        user = null;
                    }
                }
            }
        } else {
            user = null;
        }
        if (user == null) {
            ProgressDialog progressDialog = new ProgressDialog(getParentActivity());
            progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            ConnectionsManager.getInstance().sendRequest(new TL_help_getSupport(), new AnonymousClass10(sharedPreferences, progressDialog));
            return;
        }
        MessagesController.getInstance().putUser(user, true);
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", user.id);
        presentFragment(new ChatActivity(bundle));
    }

    private void sendLogs() {
        try {
            ArrayList arrayList = new ArrayList();
            File[] listFiles = new File(ApplicationLoader.applicationContext.getExternalFilesDir(null).getAbsolutePath() + "/logs").listFiles();
            int length = listFiles.length;
            for (int i = 0; i < length; i += edit_name) {
                arrayList.add(Uri.fromFile(listFiles[i]));
            }
            if (!arrayList.isEmpty()) {
                Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                intent.setType("message/rfc822");
                String[] strArr = new String[edit_name];
                strArr[0] = BuildVars.SEND_LOGS_EMAIL;
                intent.putExtra("android.intent.extra.EMAIL", strArr);
                intent.putExtra("android.intent.extra.SUBJECT", "last logs");
                intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
                getParentActivity().startActivityForResult(Intent.createChooser(intent, "Select email application."), 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUserData() {
        TLObject tLObject;
        FileLocation fileLocation = null;
        boolean z = true;
        User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        if (user.photo != null) {
            tLObject = user.photo.photo_small;
            fileLocation = user.photo.photo_big;
        } else {
            tLObject = null;
        }
        Drawable avatarDrawable = new AvatarDrawable(user, true);
        avatarDrawable.setColor(Theme.ACTION_BAR_MAIN_AVATAR_COLOR);
        if (ThemeUtil.m2490b()) {
            avatarDrawable.setColor(AdvanceTheme.f2503n);
            int dp = AndroidUtilities.dp((float) AdvanceTheme.f2502m);
            this.avatarImage.getImageReceiver().setRoundRadius(dp);
            avatarDrawable.setRadius(dp);
        }
        if (this.avatarImage != null) {
            this.avatarImage.setImage(tLObject, "50_50", avatarDrawable);
            this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.getInstance().isShowingImage(fileLocation), false);
            this.nameTextView.setText(UserObject.getUserName(user));
            if (MoboConstants.f1338e) {
                this.onlineTextView.setText(LocaleController.getString("Hidden", C0338R.string.Hidden));
            } else {
                this.onlineTextView.setText(LocaleController.getString("Online", C0338R.string.Online));
            }
            ImageReceiver imageReceiver = this.avatarImage.getImageReceiver();
            if (PhotoViewer.getInstance().isShowingImage(fileLocation)) {
                z = false;
            }
            imageReceiver.setVisible(z, false);
        }
    }

    public boolean allowCaption() {
        return true;
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(5));
        this.actionBar.setItemsBackgroundColor(ThemeUtil.m2485a().m2291e());
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAddToContainer(false);
        this.extraHeight = 88;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new C18912());
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, (int) C0338R.drawable.ic_ab_other);
        addItem.addSubItem(edit_name, LocaleController.getString("EditName", C0338R.string.EditName), 0);
        addItem.addSubItem(logout, LocaleController.getString("LogOut", C0338R.string.LogOut), 0);
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new C18923(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        int i = AdvanceTheme.f2497h;
        int i2 = AdvanceTheme.f2500k;
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(context, edit_name, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        if (ThemeUtil.m2490b()) {
            this.listView.setBackgroundColor(i);
        }
        this.listView.setGlowColor(AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C19014());
        this.listView.setOnItemLongClickListener(new C19025());
        frameLayout.addView(this.actionBar);
        this.extraHeightView = new View(context);
        this.extraHeightView.setPivotY(0.0f);
        this.extraHeightView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(5));
        if (ThemeUtil.m2490b()) {
            this.extraHeightView.setBackgroundColor(i2);
        }
        frameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        this.shadowView = new View(context);
        this.shadowView.setBackgroundResource(C0338R.drawable.header_shadow);
        frameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        if (ThemeUtil.m2490b()) {
            this.avatarImage.setRoundRadius(AdvanceTheme.f2502m);
            i2 = AdvanceTheme.f2505p;
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(i2, (float) i2, 3, 64.0f, 0.0f, 0.0f, 0.0f));
        } else {
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        }
        this.avatarImage.setOnClickListener(new C19036());
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(-1);
        if (ThemeUtil.m2490b()) {
            this.nameTextView.setTextColor(AdvanceTheme.f2501l);
        }
        this.nameTextView.setTextSize(edit_name, 18.0f);
        this.nameTextView.setLines(edit_name);
        this.nameTextView.setMaxLines(edit_name);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.nameTextView.setPivotX(0.0f);
        this.nameTextView.setPivotY(0.0f);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 48.0f, 0.0f));
        this.onlineTextView = new TextView(context);
        this.onlineTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(5));
        if (ThemeUtil.m2490b()) {
            this.onlineTextView.setTextColor(AdvanceTheme.f2504o);
        }
        this.onlineTextView.setTextSize(edit_name, 14.0f);
        this.onlineTextView.setLines(edit_name);
        this.onlineTextView.setMaxLines(edit_name);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(3);
        this.onlineTextView.setTypeface(FontUtil.m1176a().m1161d());
        frameLayout.addView(this.onlineTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 48.0f, 0.0f));
        this.writeButton = new ImageView(context);
        this.writeButton.setBackgroundResource(C0338R.drawable.floating_user_states);
        if (ThemeUtil.m2490b() && i != -1) {
            Drawable drawable = context.getResources().getDrawable(C0338R.drawable.floating3_profile);
            drawable.setColorFilter(i, Mode.SRC_IN);
            this.writeButton.setBackgroundDrawable(drawable);
        }
        this.writeButton.setImageResource(C0338R.drawable.floating_camera);
        if (ThemeUtil.m2490b()) {
            i2 = AdvanceTheme.f2494e;
            if (i2 != Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR) {
                Drawable drawable2 = context.getResources().getDrawable(C0338R.drawable.floating_camera);
                drawable2.setColorFilter(i2, Mode.SRC_IN);
                this.writeButton.setImageDrawable(drawable2);
            }
        }
        this.writeButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            int[] iArr = new int[edit_name];
            iArr[0] = 16842919;
            float[] fArr = new float[logout];
            fArr[0] = (float) AndroidUtilities.dp(2.0f);
            fArr[edit_name] = (float) AndroidUtilities.dp(4.0f);
            stateListAnimator.addState(iArr, ObjectAnimator.ofFloat(this.writeButton, "translationZ", fArr).setDuration(200));
            iArr = new int[0];
            fArr = new float[logout];
            fArr[0] = (float) AndroidUtilities.dp(4.0f);
            fArr[edit_name] = (float) AndroidUtilities.dp(2.0f);
            stateListAnimator.addState(iArr, ObjectAnimator.ofFloat(this.writeButton, "translationZ", fArr).setDuration(200));
            this.writeButton.setStateListAnimator(stateListAnimator);
            this.writeButton.setOutlineProvider(new C19047());
        }
        frameLayout.addView(this.writeButton, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new C19068());
        if (UserConfig.isRobot) {
            this.writeButton.setVisibility(8);
        }
        needLayout();
        this.listView.setOnScrollListener(new C19079());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if ((intValue & logout) != 0 || (intValue & edit_name) != 0) {
                updateUserData();
            }
        } else if (i == NotificationCenter.featuredStickersDidLoaded && this.listAdapter != null) {
            this.listAdapter.notifyItemChanged(this.stickersRow);
        }
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        int i2 = 0;
        if (fileLocation == null) {
            return null;
        }
        User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
            FileLocation fileLocation2 = user.photo.photo_big;
            if (fileLocation2.local_id == fileLocation.local_id && fileLocation2.volume_id == fileLocation.volume_id && fileLocation2.dc_id == fileLocation.dc_id) {
                int[] iArr = new int[logout];
                this.avatarImage.getLocationInWindow(iArr);
                PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                placeProviderObject.viewX = iArr[0];
                int i3 = iArr[edit_name];
                if (VERSION.SDK_INT < 21) {
                    i2 = AndroidUtilities.statusBarHeight;
                }
                placeProviderObject.viewY = i3 - i2;
                placeProviderObject.parentView = this.avatarImage;
                placeProviderObject.imageReceiver = this.avatarImage.getImageReceiver();
                placeProviderObject.dialogId = UserConfig.getClientUserId();
                placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
                placeProviderObject.size = -1;
                placeProviderObject.radius = this.avatarImage.getImageReceiver().getRoundRadius();
                placeProviderObject.scale = this.avatarImage.getScaleX();
                return placeProviderObject;
            }
        }
        return null;
    }

    public int getSelectedCount() {
        return 0;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        return null;
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.avatarUpdater.onActivityResult(i, i2, intent);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = new C18891();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + edit_name;
        this.overscrollRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.emptyRow = i;
        if (UserConfig.isRobot) {
            this.numberSectionRow = -1;
            this.numberRow = -1;
            this.usernameRow = -1;
        } else {
            i = this.rowCount;
            this.rowCount = i + edit_name;
            this.numberSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + edit_name;
            this.numberRow = i;
            i = this.rowCount;
            this.rowCount = i + edit_name;
            this.usernameRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.settingsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.settingsSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.notificationRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.privacyRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.backgroundRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.languageRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.enableAnimationsRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.mediaDownloadSection = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.mediaDownloadSection2 = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.mobileDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.wifiDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.roamingDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.autoplayGifsRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.saveToGalleryRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.messagesSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.messagesSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.customTabsRow = i;
        if (VERSION.SDK_INT >= 23) {
            i = this.rowCount;
            this.rowCount = i + edit_name;
            this.directShareRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.stickersRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.textSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.cacheRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.raiseToSpeakRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.sendByEnterRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.supportSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.supportSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.askQuestionRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.telegramFaqRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.privacyPolicyRow = i;
        if (BuildVars.DEBUG_VERSION) {
            i = this.rowCount;
            this.rowCount = i + edit_name;
            this.sendLogsRow = i;
            i = this.rowCount;
            this.rowCount = i + edit_name;
            this.clearLogsRow = i;
            i = this.rowCount;
            this.rowCount = i + edit_name;
            this.switchBackendButtonRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.versionRow = i;
        StickersQuery.checkFeaturedStickers();
        MessagesController.getInstance().loadFullUser(UserConfig.getCurrentUser(), this.classGuid, true);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.avatarImage != null) {
            this.avatarImage.setImageDrawable(null);
        }
        MessagesController.getInstance().cancelLoadFullUser(UserConfig.getClientUserId());
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        this.avatarUpdater.clear();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        updateUserData();
        initTheme();
        fixLayout();
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = bundle.getString("path");
        }
    }

    public void saveSelfArgs(Bundle bundle) {
        if (this.avatarUpdater != null && this.avatarUpdater.currentPicturePath != null) {
            bundle.putString("path", this.avatarUpdater.currentPicturePath);
        }
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
    }

    public void setPhotoChecked(int i) {
    }

    public void updatePhotoAtIndex(int i) {
    }

    public void willHidePhotoViewer() {
        this.avatarImage.getImageReceiver().setVisible(true, true);
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}
