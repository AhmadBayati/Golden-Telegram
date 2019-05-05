package com.hanista.mobogram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager.TaskDescription;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.BuildVars;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.DialogObject;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.query.SearchQuery;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.DialogsTab;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.alarmservice.AlarmUtil;
import com.hanista.mobogram.mobo.component.PowerView;
import com.hanista.mobogram.mobo.p001b.Category;
import com.hanista.mobogram.mobo.p001b.CategoryUtil.CategoryUtil;
import com.hanista.mobogram.mobo.p003d.Glow;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p007h.FavoriteUtil;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p010k.MaterialHelperUtil;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p015o.MenuData;
import com.hanista.mobogram.mobo.p015o.MenuUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.MenuDrawable;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.DialogsAdapter;
import com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter;
import com.hanista.mobogram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate;
import com.hanista.mobogram.ui.Cells.DialogCell;
import com.hanista.mobogram.ui.Cells.HintDialogCell;
import com.hanista.mobogram.ui.Cells.ProfileSearchCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.EmptyTextProgressView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.PlayerView;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import java.util.ArrayList;
import java.util.List;

public class DialogsActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private static final int category = 4;
    public static boolean dialogsLoaded = false;
    private static final int ghostMode = 3;
    private static final int lastSeen = 2;
    private String addToGroupAlertString;
    private boolean cantSendToChannels;
    private long categoryId;
    private ActionBarMenuItem categoryItem;
    private int chat_id;
    private boolean checkPermission;
    private DialogsActivityDelegate delegate;
    private DialogCell dialogCell;
    public DialogsAdapter dialogsAdapter;
    private DialogsSearchAdapter dialogsSearchAdapter;
    private DialogsTab dialogsTab;
    private int dialogsType;
    public LinearLayout emptyView;
    private boolean enterHiddenPassMode;
    private ImageView floatingButton;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator;
    private ActionBarMenuItem ghostItem;
    private boolean hiddenMode;
    private boolean justSelect;
    private LinearLayoutManager layoutManager;
    public RecyclerListView listView;
    private DialogTouchListener onTouchListener;
    private boolean onlySelect;
    private long openedDialogId;
    private ActionBarMenuItem passcodeItem;
    private AlertDialog permissionDialog;
    private int prevPosition;
    private int prevTop;
    public ProgressBar progressView;
    private boolean scrollUpdated;
    private EmptyTextProgressView searchEmptyView;
    private ActionBarMenuItem searchFiledItem;
    private String searchString;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString;
    private String selectAlertStringGroup;
    private long selectedDialog;
    private boolean touchedAvatar;
    private int user_id;

    public interface DialogsActivityDelegate {
        void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z);
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.13 */
    class AnonymousClass13 implements OnClickListener {
        final /* synthetic */ long val$dialog_id;

        AnonymousClass13(long j) {
            this.val$dialog_id = j;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            DialogsActivity.this.didSelectResult(this.val$dialog_id, false, false);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.1 */
    class C15331 implements Runnable {
        final /* synthetic */ Context val$context;

        C15331(Context context) {
            this.val$context = context;
        }

        public void run() {
            Theme.loadRecources(this.val$context);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.22 */
    class AnonymousClass22 implements CategoryUtil {
        final /* synthetic */ DataBaseAccess val$dataBaseAccess;
        final /* synthetic */ TL_dialog val$dialog;

        AnonymousClass22(DataBaseAccess dataBaseAccess, TL_dialog tL_dialog) {
            this.val$dataBaseAccess = dataBaseAccess;
            this.val$dialog = tL_dialog;
        }

        public void didSelectCategory(Category category) {
            this.val$dataBaseAccess.m848a(category.m276a(), Long.valueOf(this.val$dialog.id));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.27 */
    class AnonymousClass27 implements OnItemLongClickListener {
        final /* synthetic */ LaunchActivity val$launchActivity;

        AnonymousClass27(LaunchActivity launchActivity) {
            this.val$launchActivity = launchActivity;
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            List a = MenuUtil.m1994a(true);
            if (i >= a.size()) {
                return false;
            }
            if (MoboConstants.am == ((MenuData) a.get(i)).m1973b()) {
                DialogsActivity.this.gotoHiddenMode();
                if (this.val$launchActivity.drawerLayoutContainer != null) {
                    this.val$launchActivity.drawerLayoutContainer.closeDrawer(false);
                }
            }
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.2 */
    class C15342 extends ActionBarMenuItemSearchListener {
        C15342() {
        }

        public boolean canCollapseSearch() {
            if (DialogsActivity.this.searchString == null) {
                return true;
            }
            DialogsActivity.this.finishFragment();
            return false;
        }

        public void onSearchCollapse() {
            int i = 8;
            if (HiddenConfig.m1398b() && DialogsActivity.this.searchFiledItem != null && DialogsActivity.this.enterHiddenPassMode) {
                DialogsActivity.this.searchFiledItem.getSearchField().setInputType(524289);
                DialogsActivity.this.searchFiledItem.getSearchField().setTransformationMethod(null);
                DialogsActivity.this.searchFiledItem.getSearchField().setHint(LocaleController.getString("Search", C0338R.string.Search));
            }
            DialogsActivity.this.enterHiddenPassMode = false;
            DialogsActivity.this.searching = false;
            DialogsActivity.this.searchWas = false;
            DialogsActivity.this.dialogsTab.m970c();
            if (DialogsActivity.this.listView != null) {
                DialogsActivity.this.searchEmptyView.setVisibility(8);
                if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                    DialogsActivity.this.emptyView.setVisibility(8);
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.progressView);
                } else {
                    DialogsActivity.this.progressView.setVisibility(8);
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.emptyView);
                }
                if (!DialogsActivity.this.onlySelect) {
                    ImageView access$500 = DialogsActivity.this.floatingButton;
                    if (!UserConfig.isRobot) {
                        i = 0;
                    }
                    access$500.setVisibility(i);
                    DialogsActivity.this.floatingHidden = true;
                    DialogsActivity.this.floatingButton.setTranslationY((float) AndroidUtilities.dp(100.0f));
                    DialogsActivity.this.hideFloatingButton(false);
                }
                if (DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsAdapter) {
                    DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsAdapter);
                    DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                }
            }
            if (DialogsActivity.this.dialogsSearchAdapter != null) {
                DialogsActivity.this.dialogsSearchAdapter.searchDialogs(null);
            }
            DialogsActivity.this.updatePasscodeButton();
            DialogsActivity.this.updateCategoryState();
        }

        public void onSearchExpand() {
            DialogsActivity.this.searching = true;
            DialogsActivity.this.dialogsTab.m969b();
            if (DialogsActivity.this.listView != null) {
                if (DialogsActivity.this.searchString != null) {
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
                    DialogsActivity.this.progressView.setVisibility(8);
                    DialogsActivity.this.emptyView.setVisibility(8);
                }
                if (!DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.floatingButton.setVisibility(8);
                }
            }
            DialogsActivity.this.updatePasscodeButton();
            DialogsActivity.this.updateCategoryState();
        }

        public void onTextChanged(EditText editText) {
            String obj = editText.getText().toString();
            if (!DialogsActivity.this.enterHiddenPassMode) {
                if (obj.length() != 0 || (DialogsActivity.this.dialogsSearchAdapter != null && DialogsActivity.this.dialogsSearchAdapter.hasRecentRearch())) {
                    DialogsActivity.this.searchWas = true;
                    if (!(DialogsActivity.this.dialogsSearchAdapter == null || DialogsActivity.this.listView.getAdapter() == DialogsActivity.this.dialogsSearchAdapter)) {
                        DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsSearchAdapter);
                        DialogsActivity.this.dialogsSearchAdapter.notifyDataSetChanged();
                    }
                    if (!(DialogsActivity.this.searchEmptyView == null || DialogsActivity.this.listView.getEmptyView() == DialogsActivity.this.searchEmptyView)) {
                        DialogsActivity.this.emptyView.setVisibility(8);
                        DialogsActivity.this.progressView.setVisibility(8);
                        DialogsActivity.this.searchEmptyView.showTextView();
                        DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
                    }
                }
                if (DialogsActivity.this.dialogsSearchAdapter != null) {
                    DialogsActivity.this.dialogsSearchAdapter.searchDialogs(obj);
                }
                DialogsActivity.this.updateListBG();
            } else if (HiddenConfig.m1396a(MoboUtils.m1713c(obj))) {
                editText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                if (DialogsActivity.this.actionBar != null && DialogsActivity.this.actionBar.isSearchFieldVisible()) {
                    DialogsActivity.this.actionBar.closeSearchField();
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("hiddenMode", true);
                Toast.makeText(DialogsActivity.this.getParentActivity(), LocaleController.getString("HiddenChats", C0338R.string.HiddenChats), 0).show();
                DialogsActivity.this.presentFragment(new DialogsActivity(bundle));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.3 */
    class C15353 extends ActionBarMenuOnItemClick {
        C15353() {
        }

        public void onItemClick(int i) {
            boolean z = true;
            if (i == -1) {
                if (DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.finishFragment();
                } else if (DialogsActivity.this.parentLayout != null) {
                    DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
                }
            } else if (i == 1) {
                if (UserConfig.appLocked) {
                    z = false;
                }
                UserConfig.appLocked = z;
                UserConfig.saveConfig(false);
                DialogsActivity.this.updatePasscodeButton();
            } else if (i == DialogsActivity.lastSeen) {
                DialogsActivity.this.showUserLastSeenDialog();
            } else if (i == DialogsActivity.ghostMode) {
                DialogsActivity.this.changeGhostModeState();
            } else if (i == DialogsActivity.category) {
                DialogsActivity.this.showSelectCategory();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.4 */
    class C15364 extends LinearLayoutManager {
        C15364(Context context) {
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.5 */
    class C15375 implements OnItemClickListener {
        C15375() {
        }

        public void onItemClick(View view, int i) {
            if (DialogsActivity.this.listView != null && DialogsActivity.this.listView.getAdapter() != null) {
                long j;
                int i2;
                int id;
                Adapter adapter = DialogsActivity.this.listView.getAdapter();
                if (adapter == DialogsActivity.this.dialogsAdapter) {
                    TL_dialog item = DialogsActivity.this.dialogsAdapter.getItem(i);
                    if (item != null) {
                        j = item.id;
                        i2 = 0;
                    } else {
                        return;
                    }
                }
                if (adapter == DialogsActivity.this.dialogsSearchAdapter) {
                    Object item2 = DialogsActivity.this.dialogsSearchAdapter.getItem(i);
                    if (item2 instanceof User) {
                        j = (long) ((User) item2).id;
                        if (DialogsActivity.this.dialogsSearchAdapter.isGlobalSearch(i)) {
                            ArrayList arrayList = new ArrayList();
                            arrayList.add((User) item2);
                            MessagesController.getInstance().putUsers(arrayList, false);
                            MessagesStorage.getInstance().putUsersAndChats(arrayList, null, false, true);
                        }
                        if (!DialogsActivity.this.onlySelect) {
                            DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(j, (User) item2);
                            i2 = 0;
                        }
                    } else if (item2 instanceof Chat) {
                        if (DialogsActivity.this.dialogsSearchAdapter.isGlobalSearch(i)) {
                            ArrayList arrayList2 = new ArrayList();
                            arrayList2.add((Chat) item2);
                            MessagesController.getInstance().putChats(arrayList2, false);
                            MessagesStorage.getInstance().putUsersAndChats(null, arrayList2, false, true);
                        }
                        j = ((Chat) item2).id > 0 ? (long) (-((Chat) item2).id) : AndroidUtilities.makeBroadcastId(((Chat) item2).id);
                        if (!DialogsActivity.this.onlySelect) {
                            DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(j, (Chat) item2);
                            i2 = 0;
                        }
                    } else if (item2 instanceof EncryptedChat) {
                        j = ((long) ((EncryptedChat) item2).id) << 32;
                        if (!DialogsActivity.this.onlySelect) {
                            DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(j, (EncryptedChat) item2);
                            i2 = 0;
                        }
                    } else if (item2 instanceof MessageObject) {
                        MessageObject messageObject = (MessageObject) item2;
                        j = messageObject.getDialogId();
                        id = messageObject.getId();
                        DialogsActivity.this.dialogsSearchAdapter.addHashtagsFromMessage(DialogsActivity.this.dialogsSearchAdapter.getLastSearchString());
                        i2 = id;
                    } else if (item2 instanceof String) {
                        DialogsActivity.this.actionBar.openSearchField((String) item2);
                    }
                    i2 = 0;
                }
                i2 = 0;
                j = 0;
                if (j != 0 && !DialogsActivity.this.isTouchedAvatarAndAct(view, j)) {
                    if (DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.didSelectResult(j, true, false);
                        return;
                    }
                    Bundle bundle = new Bundle();
                    id = (int) j;
                    int i3 = (int) (j >> 32);
                    if (id == 0) {
                        bundle.putInt("enc_id", i3);
                    } else if (i3 == 1) {
                        bundle.putInt("chat_id", id);
                    } else if (id > 0) {
                        bundle.putInt("user_id", id);
                    } else if (id < 0) {
                        if (i2 != 0) {
                            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-id));
                            if (!(chat == null || chat.migrated_to == null)) {
                                bundle.putInt("migrated_to", id);
                                id = -chat.migrated_to.channel_id;
                            }
                        }
                        bundle.putInt("chat_id", -id);
                    }
                    if (i2 != 0) {
                        bundle.putInt("message_id", i2);
                    } else if (DialogsActivity.this.actionBar != null) {
                        DialogsActivity.this.actionBar.closeSearchField();
                    }
                    if (AndroidUtilities.isTablet()) {
                        if (DialogsActivity.this.openedDialogId == j && adapter != DialogsActivity.this.dialogsSearchAdapter) {
                            return;
                        }
                        if (DialogsActivity.this.dialogsAdapter != null) {
                            DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = j);
                            DialogsActivity.this.updateVisibleRows(TLRPC.USER_FLAG_UNUSED3);
                        }
                    }
                    bundle.putBoolean("hiddenMode", DialogsActivity.this.isHiddenMode());
                    if (DialogsActivity.this.searchString != null) {
                        if (MessagesController.checkCanOpenChat(bundle, DialogsActivity.this)) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            DialogsActivity.this.presentFragment(new ChatActivity(bundle));
                        }
                    } else if (MessagesController.checkCanOpenChat(bundle, DialogsActivity.this)) {
                        DialogsActivity.this.presentFragment(new ChatActivity(bundle));
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.6 */
    class C15446 implements RecyclerListView.OnItemLongClickListener {

        /* renamed from: com.hanista.mobogram.ui.DialogsActivity.6.1 */
        class C15381 implements OnClickListener {
            C15381() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                } else {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.DialogsActivity.6.2 */
        class C15412 implements OnClickListener {
            final /* synthetic */ Chat val$chat;
            final /* synthetic */ TL_dialog val$dialog;

            /* renamed from: com.hanista.mobogram.ui.DialogsActivity.6.2.1 */
            class C15391 implements OnClickListener {
                C15391() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, DialogsActivity.lastSeen);
                }
            }

            /* renamed from: com.hanista.mobogram.ui.DialogsActivity.6.2.2 */
            class C15402 implements OnClickListener {
                C15402() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    MessagesController.getInstance().deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), UserConfig.getCurrentUser(), null);
                    if (AndroidUtilities.isTablet()) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                    }
                }
            }

            C15412(TL_dialog tL_dialog, Chat chat) {
                this.val$dialog = tL_dialog;
                this.val$chat = chat;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    DialogsActivity.this.addOrDeleteFavorite(this.val$dialog);
                } else if (i == 1) {
                    DialogsActivity.this.addOrDeleteFromCategory(this.val$dialog);
                } else if (i == DialogsActivity.lastSeen && HiddenConfig.m1398b()) {
                    DialogsActivity.this.addOrDeleteHidden(this.val$dialog);
                } else {
                    Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    if (!(HiddenConfig.m1398b() && i == DialogsActivity.ghostMode) && (HiddenConfig.m1398b() || i != DialogsActivity.lastSeen)) {
                        if (this.val$chat == null || !this.val$chat.megagroup) {
                            if (this.val$chat == null || !this.val$chat.creator) {
                                builder.setMessage(LocaleController.getString("ChannelLeaveAlert", C0338R.string.ChannelLeaveAlert));
                            } else {
                                builder.setMessage(LocaleController.getString("ChannelDeleteAlert", C0338R.string.ChannelDeleteAlert));
                            }
                        } else if (this.val$chat.creator) {
                            builder.setMessage(LocaleController.getString("MegaDeleteAlert", C0338R.string.MegaDeleteAlert));
                        } else {
                            builder.setMessage(LocaleController.getString("MegaLeaveAlert", C0338R.string.MegaLeaveAlert));
                        }
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C15402());
                    } else {
                        if (this.val$chat == null || !this.val$chat.megagroup) {
                            builder.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", C0338R.string.AreYouSureClearHistoryChannel));
                        } else {
                            builder.setMessage(LocaleController.getString("AreYouSureClearHistorySuper", C0338R.string.AreYouSureClearHistorySuper));
                        }
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C15391());
                    }
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    DialogsActivity.this.showDialog(builder.create());
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.DialogsActivity.6.3 */
        class C15433 implements OnClickListener {
            final /* synthetic */ TL_dialog val$dialog;
            final /* synthetic */ boolean val$isBot;
            final /* synthetic */ boolean val$isChat;

            /* renamed from: com.hanista.mobogram.ui.DialogsActivity.6.3.1 */
            class C15421 implements OnClickListener {
                final /* synthetic */ int val$which;

                C15421(int i) {
                    this.val$which = i;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if ((!HiddenConfig.m1398b() || this.val$which == DialogsActivity.ghostMode) && (HiddenConfig.m1398b() || this.val$which == DialogsActivity.lastSeen)) {
                        MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 1);
                        return;
                    }
                    if (C15433.this.val$isChat) {
                        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf((int) (-DialogsActivity.this.selectedDialog)));
                        if (chat == null || !ChatObject.isNotInChat(chat)) {
                            MessagesController.getInstance().deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                        } else {
                            MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                        }
                    } else {
                        MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                        DialogsActivity.this.showDeleteMultipleChatHelp();
                    }
                    if (C15433.this.val$isBot) {
                        MessagesController.getInstance().blockUser((int) DialogsActivity.this.selectedDialog);
                    }
                    if (AndroidUtilities.isTablet()) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                    }
                }
            }

            C15433(TL_dialog tL_dialog, boolean z, boolean z2) {
                this.val$dialog = tL_dialog;
                this.val$isChat = z;
                this.val$isBot = z2;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    DialogsActivity.this.addOrDeleteFavorite(this.val$dialog);
                } else if (i == 1) {
                    DialogsActivity.this.addOrDeleteFromCategory(this.val$dialog);
                } else if (i == DialogsActivity.lastSeen && HiddenConfig.m1398b()) {
                    DialogsActivity.this.addOrDeleteHidden(this.val$dialog);
                } else {
                    Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    if ((HiddenConfig.m1398b() && i == DialogsActivity.ghostMode) || (!HiddenConfig.m1398b() && i == DialogsActivity.lastSeen)) {
                        builder.setMessage(LocaleController.getString("AreYouSureClearHistory", C0338R.string.AreYouSureClearHistory));
                    } else if (this.val$isChat) {
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0338R.string.AreYouSureDeleteAndExit));
                    } else {
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", C0338R.string.AreYouSureDeleteThisChat));
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C15421(i));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    DialogsActivity.this.showDialog(builder.create());
                }
            }
        }

        C15446() {
        }

        public boolean onItemClick(View view, int i) {
            if (!DialogsActivity.this.onlySelect && ((!DialogsActivity.this.searching || !DialogsActivity.this.searchWas) && DialogsActivity.this.getParentActivity() != null)) {
                ArrayList access$2800 = DialogsActivity.this.getDialogsArray();
                if (i < 0 || i >= access$2800.size()) {
                    return false;
                }
                TL_dialog tL_dialog = (TL_dialog) access$2800.get(i);
                DialogsActivity.this.selectedDialog = tL_dialog.id;
                BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                int access$2900 = (int) DialogsActivity.this.selectedDialog;
                int access$29002 = (int) (DialogsActivity.this.selectedDialog >> 32);
                CharSequence[] charSequenceArr;
                if (DialogObject.isChannel(tL_dialog)) {
                    CharSequence[] charSequenceArr2;
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-access$2900));
                    if (chat != null) {
                        builder.setTitle(chat.title);
                    }
                    String string;
                    if (chat == null || !chat.megagroup) {
                        if (HiddenConfig.m1398b()) {
                            charSequenceArr = new CharSequence[5];
                            charSequenceArr[0] = FavoriteUtil.m1140a(Long.valueOf(DialogsActivity.this.selectedDialog));
                            charSequenceArr[1] = com.hanista.mobogram.mobo.p001b.CategoryUtil.m350b(Long.valueOf(DialogsActivity.this.selectedDialog));
                            charSequenceArr[DialogsActivity.lastSeen] = HiddenConfig.m1392a(Long.valueOf(DialogsActivity.this.selectedDialog));
                            charSequenceArr[DialogsActivity.ghostMode] = LocaleController.getString("ClearHistoryCache", C0338R.string.ClearHistoryCache);
                            string = (chat == null || !chat.creator) ? LocaleController.getString("LeaveChannelMenu", C0338R.string.LeaveChannelMenu) : LocaleController.getString("ChannelDeleteMenu", C0338R.string.ChannelDeleteMenu);
                            charSequenceArr[DialogsActivity.category] = string;
                            charSequenceArr2 = charSequenceArr;
                        } else {
                            charSequenceArr = new CharSequence[DialogsActivity.category];
                            charSequenceArr[0] = FavoriteUtil.m1140a(Long.valueOf(DialogsActivity.this.selectedDialog));
                            charSequenceArr[1] = com.hanista.mobogram.mobo.p001b.CategoryUtil.m350b(Long.valueOf(DialogsActivity.this.selectedDialog));
                            charSequenceArr[DialogsActivity.lastSeen] = LocaleController.getString("ClearHistoryCache", C0338R.string.ClearHistoryCache);
                            string = (chat == null || !chat.creator) ? LocaleController.getString("LeaveChannelMenu", C0338R.string.LeaveChannelMenu) : LocaleController.getString("ChannelDeleteMenu", C0338R.string.ChannelDeleteMenu);
                            charSequenceArr[DialogsActivity.ghostMode] = string;
                            charSequenceArr2 = charSequenceArr;
                        }
                    } else if (HiddenConfig.m1398b()) {
                        charSequenceArr = new CharSequence[5];
                        charSequenceArr[0] = FavoriteUtil.m1140a(Long.valueOf(DialogsActivity.this.selectedDialog));
                        charSequenceArr[1] = com.hanista.mobogram.mobo.p001b.CategoryUtil.m350b(Long.valueOf(DialogsActivity.this.selectedDialog));
                        charSequenceArr[DialogsActivity.lastSeen] = HiddenConfig.m1392a(Long.valueOf(DialogsActivity.this.selectedDialog));
                        charSequenceArr[DialogsActivity.ghostMode] = LocaleController.getString("ClearHistoryCache", C0338R.string.ClearHistoryCache);
                        string = (chat == null || !chat.creator) ? LocaleController.getString("LeaveMegaMenu", C0338R.string.LeaveMegaMenu) : LocaleController.getString("DeleteMegaMenu", C0338R.string.DeleteMegaMenu);
                        charSequenceArr[DialogsActivity.category] = string;
                        charSequenceArr2 = charSequenceArr;
                    } else {
                        charSequenceArr = new CharSequence[DialogsActivity.category];
                        charSequenceArr[0] = FavoriteUtil.m1140a(Long.valueOf(DialogsActivity.this.selectedDialog));
                        charSequenceArr[1] = com.hanista.mobogram.mobo.p001b.CategoryUtil.m350b(Long.valueOf(DialogsActivity.this.selectedDialog));
                        charSequenceArr[DialogsActivity.lastSeen] = LocaleController.getString("ClearHistoryCache", C0338R.string.ClearHistoryCache);
                        string = (chat == null || !chat.creator) ? LocaleController.getString("LeaveMegaMenu", C0338R.string.LeaveMegaMenu) : LocaleController.getString("DeleteMegaMenu", C0338R.string.DeleteMegaMenu);
                        charSequenceArr[DialogsActivity.ghostMode] = string;
                        charSequenceArr2 = charSequenceArr;
                    }
                    builder.setItems(charSequenceArr2, new C15412(tL_dialog, chat));
                    DialogsActivity.this.showDialog(builder.create());
                    return true;
                }
                boolean z = access$2900 < 0 && access$29002 != 1;
                User user = null;
                if (!(z || access$2900 <= 0 || access$29002 == 1)) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(access$2900));
                }
                if (user != null) {
                    builder.setTitle(UserObject.getUserName(user));
                }
                boolean z2 = user != null && user.bot;
                CharSequence[] charSequenceArr3;
                String string2;
                if (HiddenConfig.m1398b()) {
                    charSequenceArr3 = new CharSequence[5];
                    charSequenceArr3[0] = FavoriteUtil.m1140a(Long.valueOf(DialogsActivity.this.selectedDialog));
                    charSequenceArr3[1] = com.hanista.mobogram.mobo.p001b.CategoryUtil.m350b(Long.valueOf(DialogsActivity.this.selectedDialog));
                    charSequenceArr3[DialogsActivity.lastSeen] = HiddenConfig.m1392a(Long.valueOf(DialogsActivity.this.selectedDialog));
                    charSequenceArr3[DialogsActivity.ghostMode] = LocaleController.getString("ClearHistory", C0338R.string.ClearHistory);
                    string2 = z ? LocaleController.getString("DeleteChat", C0338R.string.DeleteChat) : z2 ? LocaleController.getString("DeleteAndStop", C0338R.string.DeleteAndStop) : LocaleController.getString("Delete", C0338R.string.Delete);
                    charSequenceArr3[DialogsActivity.category] = string2;
                    charSequenceArr = charSequenceArr3;
                } else {
                    charSequenceArr3 = new CharSequence[DialogsActivity.category];
                    charSequenceArr3[0] = FavoriteUtil.m1140a(Long.valueOf(DialogsActivity.this.selectedDialog));
                    charSequenceArr3[1] = com.hanista.mobogram.mobo.p001b.CategoryUtil.m350b(Long.valueOf(DialogsActivity.this.selectedDialog));
                    charSequenceArr3[DialogsActivity.lastSeen] = LocaleController.getString("ClearHistory", C0338R.string.ClearHistory);
                    string2 = z ? LocaleController.getString("DeleteChat", C0338R.string.DeleteChat) : z2 ? LocaleController.getString("DeleteAndStop", C0338R.string.DeleteAndStop) : LocaleController.getString("Delete", C0338R.string.Delete);
                    charSequenceArr3[DialogsActivity.ghostMode] = string2;
                    charSequenceArr = charSequenceArr3;
                }
                builder.setItems(charSequenceArr, new C15433(tL_dialog, z, z2));
                DialogsActivity.this.showDialog(builder.create());
                return true;
            } else if (((!DialogsActivity.this.searchWas || !DialogsActivity.this.searching) && !DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) || DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsSearchAdapter || (!(DialogsActivity.this.dialogsSearchAdapter.getItem(i) instanceof String) && !DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed())) {
                return false;
            } else {
                Builder builder2 = new Builder(DialogsActivity.this.getParentActivity());
                builder2.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder2.setMessage(LocaleController.getString("ClearSearch", C0338R.string.ClearSearch));
                builder2.setPositiveButton(LocaleController.getString("ClearButton", C0338R.string.ClearButton).toUpperCase(), new C15381());
                builder2.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                DialogsActivity.this.showDialog(builder2.create());
                return true;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.7 */
    class C15457 implements OnTouchListener {
        C15457() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.8 */
    class C15468 extends ViewOutlineProvider {
        C15468() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DialogsActivity.9 */
    class C15479 implements View.OnClickListener {
        C15479() {
        }

        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("destroyAfterSelect", true);
            DialogsActivity.this.presentFragment(new ContactsActivity(bundle));
        }
    }

    public class DialogTouchListener implements OnTouchListener {
        private int displayWidth;
        private float vDPI;

        public DialogTouchListener(Context context) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            DialogsActivity.this.getParentActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            this.displayWidth = (int) (((float) displayMetrics.widthPixels) / displayMetrics.density);
            this.vDPI = displayMetrics.xdpi / 160.0f;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean z = true;
            int[] iArr = new int[DialogsActivity.lastSeen];
            DialogsActivity.this.listView.getLocationOnScreen(iArr);
            int x = ((int) motionEvent.getX()) - iArr[0];
            int y = ((int) motionEvent.getY()) - iArr[1];
            DialogsActivity dialogsActivity;
            if (LocaleController.isRTL) {
                dialogsActivity = DialogsActivity.this;
                if (Math.round(((float) x) / this.vDPI) <= Math.round(((float) DialogsActivity.this.listView.getMeasuredWidth()) / this.vDPI) - 65) {
                    z = false;
                }
                dialogsActivity.touchedAvatar = z;
            } else {
                dialogsActivity = DialogsActivity.this;
                if (Math.round(((float) x) / this.vDPI) >= 65) {
                    z = false;
                }
                dialogsActivity.touchedAvatar = z;
            }
            return false;
        }
    }

    public DialogsActivity(Bundle bundle) {
        super(bundle);
        this.floatingInterpolator = new AccelerateDecelerateInterpolator();
        this.checkPermission = true;
        this.onTouchListener = null;
    }

    private void addOrDeleteFavorite(TL_dialog tL_dialog) {
        if (FavoriteUtil.m1142b(Long.valueOf(tL_dialog.id))) {
            FavoriteUtil.m1144d(Long.valueOf(tL_dialog.id));
            MessagesController.getInstance().dialogsFavoriteOnly.remove(tL_dialog);
        } else {
            FavoriteUtil.m1143c(Long.valueOf(tL_dialog.id));
            MessagesController.getInstance().dialogsFavoriteOnly.add(tL_dialog);
        }
        if (this.dialogsAdapter != null && this.dialogsAdapter.getDialogsType() == 6) {
            this.dialogsAdapter.notifyDataSetChanged();
        }
        if (this.dialogsTab != null) {
            this.dialogsTab.m967a(this.categoryId, this.hiddenMode);
        }
    }

    private void addOrDeleteFromCategory(TL_dialog tL_dialog) {
        DataBaseAccess dataBaseAccess = new DataBaseAccess();
        if (dataBaseAccess.m884g(Long.valueOf(tL_dialog.id))) {
            dataBaseAccess.m880f(Long.valueOf(tL_dialog.id));
            if (this.dialogsAdapter != null) {
                this.dialogsAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        com.hanista.mobogram.mobo.p001b.CategoryUtil.m347a(this, LocaleController.getString("SelectCategory", C0338R.string.SelectCategory), false, true, new AnonymousClass22(dataBaseAccess, tL_dialog));
    }

    private void addOrDeleteHidden(TL_dialog tL_dialog) {
        if (HiddenConfig.m1399b(Long.valueOf(tL_dialog.id))) {
            HiddenConfig.m1397b(tL_dialog);
        } else {
            HiddenConfig.m1395a(tL_dialog);
        }
        if (this.dialogsAdapter != null) {
            this.dialogsAdapter.notifyDataSetChanged();
        }
        if (this.dialogsTab != null) {
            this.dialogsTab.m967a(this.categoryId, this.hiddenMode);
        }
        SearchQuery.loaded = false;
        SearchQuery.loadHints(true);
        if (HiddenConfig.m1399b(Long.valueOf(tL_dialog.id))) {
            SettingManager settingManager = new SettingManager();
            if (!settingManager.m944b("hiddenChatsHelpDisplayed")) {
                String formatString;
                settingManager.m943a("hiddenChatsHelpDisplayed", true);
                Builder builder = new Builder(getParentActivity());
                String str = TtmlNode.ANONYMOUS_REGION_ID;
                int i = MoboConstants.am;
                if (i == 100) {
                    formatString = LocaleController.formatString("LongPressOnSomething", C0338R.string.LongPressOnSomething, LocaleController.getString("SearchButton", C0338R.string.SearchButton));
                } else if (i == 101) {
                    formatString = LocaleController.formatString("LongPressOnSomething", C0338R.string.LongPressOnSomething, LocaleController.getString("FloatingButton", C0338R.string.FloatingButton));
                } else {
                    for (MenuData b : MenuUtil.m1994a(false)) {
                        if (b.m1973b() == i) {
                            formatString = LocaleController.formatString("LongPressOnItem", C0338R.string.LongPressOnItem, ((MenuData) r4.next()).m1976e());
                            break;
                        }
                    }
                    formatString = str;
                }
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.formatString("AccessToHiddenChatsHelp", C0338R.string.AccessToHiddenChatsHelp, formatString));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        }
    }

    @TargetApi(23)
    private void askForPermissons() {
        Activity parentActivity = getParentActivity();
        if (parentActivity != null) {
            ArrayList arrayList = new ArrayList();
            if (parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                arrayList.add("android.permission.READ_CONTACTS");
                arrayList.add("android.permission.WRITE_CONTACTS");
                arrayList.add("android.permission.GET_ACCOUNTS");
            }
            if (parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                arrayList.add("android.permission.READ_EXTERNAL_STORAGE");
                arrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
            parentActivity.requestPermissions((String[]) arrayList.toArray(new String[arrayList.size()]), 1);
        }
    }

    private void clearCacheFile() {
    }

    private void didSelectResult(long j, boolean z, boolean z2) {
        Builder builder;
        if (this.addToGroupAlertString == null && !this.justSelect && ((int) j) < 0 && ChatObject.isChannel(-((int) j)) && (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(-((int) j)))) {
            builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setMessage(LocaleController.getString("ChannelCantSendMessage", C0338R.string.ChannelCantSendMessage));
            builder.setNegativeButton(LocaleController.getString("OK", C0338R.string.OK), null);
            showDialog(builder.create());
        } else if (this.justSelect || !z || ((this.selectAlertString == null || this.selectAlertStringGroup == null) && this.addToGroupAlertString == null)) {
            if (this.delegate != null) {
                this.delegate.didSelectDialog(this, j, z2);
                this.delegate = null;
                return;
            }
            finishFragment();
        } else if (getParentActivity() != null) {
            builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            int i = (int) j;
            int i2 = (int) (j >> 32);
            if (i == 0) {
                if (MessagesController.getInstance().getUser(Integer.valueOf(MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i2)).user_id)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(r1)));
                } else {
                    return;
                }
            } else if (i2 == 1) {
                if (MessagesController.getInstance().getChat(Integer.valueOf(i)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, r1.title));
                } else {
                    return;
                }
            } else if (i > 0) {
                if (MessagesController.getInstance().getUser(Integer.valueOf(i)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(r1)));
                } else {
                    return;
                }
            } else if (i < 0) {
                if (MessagesController.getInstance().getChat(Integer.valueOf(-i)) == null) {
                    return;
                }
                if (this.addToGroupAlertString != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.addToGroupAlertString, r1.title));
                } else {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, r1.title));
                }
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass13(j));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    private ArrayList<TL_dialog> getDialogsArray() {
        return this.dialogsAdapter.getDialogsArray();
    }

    private void gotoHiddenMode() {
        if (HiddenConfig.m1398b()) {
            this.searchFiledItem.openSearch(true);
            this.searchFiledItem.getSearchField().setHint(LocaleController.getString("PasscodePassword", C0338R.string.PasscodePassword));
            if (HiddenConfig.f1401d == 0) {
                this.searchFiledItem.getSearchField().setInputType(ghostMode);
            } else {
                this.searchFiledItem.getSearchField().setInputType(129);
            }
            this.searchFiledItem.getSearchField().setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.enterHiddenPassMode = true;
        }
    }

    private void hideFloatingButton(boolean z) {
        if (this.floatingHidden != z) {
            this.floatingHidden = z;
            ImageView imageView = this.floatingButton;
            String str = "translationY";
            float[] fArr = new float[1];
            fArr[0] = this.floatingHidden ? (float) AndroidUtilities.dp(140.0f) : 0.0f;
            ObjectAnimator duration = ObjectAnimator.ofFloat(imageView, str, fArr).setDuration(300);
            duration.setInterpolator(this.floatingInterpolator);
            this.floatingButton.setClickable(!z);
            duration.start();
        }
    }

    private void initHiddenModeEnteringMethods() {
        if (!isHiddenMode()) {
            this.searchFiledItem.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    if (MoboConstants.am == 100) {
                        DialogsActivity.this.gotoHiddenMode();
                    }
                    return true;
                }
            });
            this.floatingButton.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    if (MoboConstants.am == 101) {
                        DialogsActivity.this.gotoHiddenMode();
                    }
                    return true;
                }
            });
            if (getParentActivity() != null && (getParentActivity() instanceof LaunchActivity)) {
                LaunchActivity launchActivity = (LaunchActivity) getParentActivity();
                if (launchActivity.listView != null && launchActivity.listView.getOnItemLongClickListener() == null) {
                    launchActivity.listView.setOnItemLongClickListener(new AnonymousClass27(launchActivity));
                }
            }
        }
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            Drawable drawable;
            paintHeader();
            if (this.emptyView != null) {
                this.emptyView.setBackgroundColor(AdvanceTheme.f2514y);
                if (this.emptyView.getChildCount() > 0) {
                    TextView textView = (TextView) this.emptyView.getChildAt(0);
                    if (textView != null) {
                        textView.setTextColor(AdvanceTheme.m2286c(AdvanceTheme.f2489Z, Theme.STICKERS_SHEET_TITLE_TEXT_COLOR));
                    }
                }
            }
            int i = AdvanceTheme.f2507r;
            try {
                int i2 = AdvanceTheme.f2508s;
                if (VERSION.SDK_INT >= 21) {
                    Bitmap decodeResource = BitmapFactory.decodeResource(getParentActivity().getResources(), C0338R.drawable.ic_launcher);
                    getParentActivity().setTaskDescription(new TaskDescription(LocaleController.getString("AppName", C0338R.string.AppName), decodeResource, i2));
                    decodeResource.recycle();
                }
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.floating_white);
                if (drawable != null) {
                    drawable.setColorFilter(AdvanceTheme.f2464A, Mode.MULTIPLY);
                }
                this.floatingButton.setBackgroundDrawable(drawable);
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.floating_pencil);
                if (drawable != null) {
                    drawable.setColorFilter(AdvanceTheme.f2465B, Mode.MULTIPLY);
                }
                this.floatingButton.setImageDrawable(drawable);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            try {
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_search);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.lock_close);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.lock_open);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_close_white);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ghost);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ghost_disable);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_mobo_seen);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_close_category);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_category);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            paintHeader();
        }
    }

    private boolean isTouchedAvatarAndAct(View view, long j) {
        if (this.touchedAvatar) {
            this.user_id = 0;
            this.chat_id = 0;
            int i = (int) j;
            int i2 = (int) (j >> 32);
            if (view instanceof DialogCell) {
                this.dialogCell = (DialogCell) view;
            }
            if (i == 0) {
                this.user_id = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i2)).user_id;
            } else if (i2 == 1) {
                this.chat_id = i;
            } else if (i > 0) {
                this.user_id = i;
            } else if (i < 0) {
                this.chat_id = -i;
            }
            Bundle bundle;
            if (this.user_id != 0) {
                i = MoboConstants.aB;
                if (i == 1) {
                    bundle = new Bundle();
                    bundle.putInt("user_id", this.user_id);
                    presentFragment(new ProfileActivity(bundle));
                    return true;
                } else if (i == lastSeen) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
                    if (!(user.photo == null || user.photo.photo_big == null)) {
                        PhotoViewer.getInstance().setParentActivity(getParentActivity());
                        PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this);
                        return true;
                    }
                }
            } else if (this.chat_id != 0) {
                i = MoboConstants.aC;
                if (i == 1) {
                    MessagesController.getInstance().loadChatInfo(this.chat_id, null, false);
                    bundle = new Bundle();
                    bundle.putInt("chat_id", this.chat_id);
                    presentFragment(new ProfileActivity(bundle));
                    return true;
                } else if (i == lastSeen) {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                    if (!(chat.photo == null || chat.photo.photo_big == null)) {
                        PhotoViewer.getInstance().setParentActivity(getParentActivity());
                        PhotoViewer.getInstance().openPhoto(chat.photo.photo_big, this);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void paintHeader() {
        if (ThemeUtil.m2490b()) {
            this.actionBar.setTitleColor(AdvanceTheme.f2513x);
            int i = AdvanceTheme.f2508s;
            this.actionBar.setBackgroundColor(i);
            int i2 = AdvanceTheme.f2509t;
            if (i2 > 0) {
                Orientation orientation;
                switch (i2) {
                    case lastSeen /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case ghostMode /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case category /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                int i3 = AdvanceTheme.f2510u;
                int[] iArr = new int[lastSeen];
                iArr[0] = i;
                iArr[1] = i3;
                this.actionBar.setBackgroundDrawable(new GradientDrawable(orientation, iArr));
            }
        }
    }

    private void showDeleteMultipleChatHelp() {
        if (UserConfig.isClientActivated()) {
            SettingManager settingManager = new SettingManager();
            if (!settingManager.m944b("deleteMultiChatHelpDisplayed")) {
                settingManager.m943a("deleteMultiChatHelpDisplayed", true);
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("Help", C0338R.string.Help)).setMessage(LocaleController.getString("DeleteMultiChatHelp", C0338R.string.DeleteMultiChatHelp));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        }
    }

    private void showMaterialHelp() {
        View view = null;
        try {
            Activity parentActivity = getParentActivity();
            View d = this.dialogsTab != null ? this.dialogsTab.m971d() : null;
            View view2 = this.ghostItem;
            View childAt = this.listView.getChildCount() > 0 ? this.listView.getChildAt(0) : null;
            if (this.actionBar != null) {
                view = this.actionBar.getBackButtonImageView();
            }
            MaterialHelperUtil.m1363a(parentActivity, d, view2, childAt, view);
        } catch (Exception e) {
        }
    }

    private void showSelectCategory() {
        com.hanista.mobogram.mobo.p001b.CategoryUtil.m348a(this, true, new CategoryUtil() {
            public void didSelectCategory(Category category) {
                if (category == null) {
                    DialogsActivity.this.categoryId = 0;
                } else {
                    DialogsActivity.this.categoryId = category.m276a().longValue();
                }
                if (DialogsActivity.this.dialogsAdapter != null) {
                    DialogsActivity.this.dialogsAdapter.setCategoryId(DialogsActivity.this.categoryId);
                    DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                    DialogsActivity.this.updateCategoryState();
                }
            }
        });
    }

    private void showUserLastSeenDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", UserConfig.getCurrentUser().id);
        presentFragment(new ChatActivity(bundle));
    }

    private void showWelcomeDialog() {
        if (UserConfig.isClientActivated()) {
            SettingManager settingManager = new SettingManager();
            Builder builder;
            if (MoboUtils.m1727j(getParentActivity()) || MoboUtils.m1728k(getParentActivity())) {
                if (!settingManager.m944b("autoSyncHelpDisplayed")) {
                    settingManager.m943a("autoSyncHelpDisplayed", true);
                    builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("AutoSyncHelp", C0338R.string.AutoSyncHelp));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                } else if (!settingManager.m944b("newFeaturesForVersionDisplayed" + MoboUtils.m1692a(getParentActivity()))) {
                    settingManager.m943a("newFeaturesForVersionDisplayed" + MoboUtils.m1692a(getParentActivity()), true);
                    builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("NewFeatures", C0338R.string.NewFeatures)).setMessage(LocaleController.getString("updateMoboText", C0338R.string.updateMoboText));
                    builder.setPositiveButton(LocaleController.getString("HanistaChannel", C0338R.string.HanistaChannel), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            if (!ChatObject.isLeftFromChat(MessagesController.getInstance().getChat(Integer.valueOf(1009604744)))) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("chat_id", 1009604744);
                                DialogsActivity.this.presentFragment(new ChatActivity(bundle));
                            } else if (DialogsActivity.this.getParentActivity() == null || !(DialogsActivity.this.getParentActivity() instanceof LaunchActivity)) {
                                Intent intent = new Intent(DialogsActivity.this.getParentActivity(), LaunchActivity.class);
                                intent.setAction("android.intent.action.VIEW");
                                intent.setData(Uri.parse("https://telegram.me/joinchat/BlusLzwtWIiyvfw4Vguksg"));
                                DialogsActivity.this.getParentActivity().startActivity(intent);
                            } else {
                                ((LaunchActivity) DialogsActivity.this.getParentActivity()).runLinkRequest(null, "BlusLzwtWIiyvfw4Vguksg", null, null, null, null, false, null, null, 1);
                            }
                        }
                    });
                    builder.create().show();
                }
                AlarmUtil.m275a(getParentActivity());
                return;
            }
            if (!settingManager.m944b("helpDisplayed")) {
                settingManager.m943a("helpDisplayed", true);
                builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("WelcomeMessage", C0338R.string.WelcomeMessage));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            } else if (!settingManager.m944b("newFeaturesForVersionDisplayed" + MoboUtils.m1692a(getParentActivity()))) {
                settingManager.m943a("newFeaturesForVersionDisplayed" + MoboUtils.m1692a(getParentActivity()), true);
                builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("NewFeatures", C0338R.string.NewFeatures)).setMessage(LocaleController.getString("updateMoboText", C0338R.string.updateMoboText));
                builder.setPositiveButton(LocaleController.getString("HanistaChannel", C0338R.string.HanistaChannel), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (!ChatObject.isLeftFromChat(MessagesController.getInstance().getChat(Integer.valueOf(1009604744)))) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("chat_id", 1009604744);
                            DialogsActivity.this.presentFragment(new ChatActivity(bundle));
                        } else if (DialogsActivity.this.getParentActivity() == null || !(DialogsActivity.this.getParentActivity() instanceof LaunchActivity)) {
                            Intent intent = new Intent(DialogsActivity.this.getParentActivity(), LaunchActivity.class);
                            intent.setAction("android.intent.action.VIEW");
                            intent.setData(Uri.parse("https://telegram.me/joinchat/BlusLzwtWIiyvfw4Vguksg"));
                            DialogsActivity.this.getParentActivity().startActivity(intent);
                        } else {
                            ((LaunchActivity) DialogsActivity.this.getParentActivity()).runLinkRequest(null, "BlusLzwtWIiyvfw4Vguksg", null, null, null, null, false, null, null, 1);
                        }
                    }
                });
                builder.create().show();
            } else if (!settingManager.m944b("copyrightDisplayed")) {
                settingManager.m943a("copyrightDisplayed", true);
                builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage("\u06a9\u0627\u0631\u0628\u0631 \u0639\u0632\u06cc\u0632\u060c\n\n\u0628\u0627 \u062a\u0634\u06a9\u0631 \u0627\u0632 \u062d\u0633\u0646 \u0627\u0646\u062a\u062e\u0627\u0628 \u0634\u0645\u0627\u060c \u062a\u0648\u062c\u0647 \u0634\u0645\u0627 \u0631\u0627 \u0628\u0647 \u0646\u06a9\u0627\u062a \u0632\u06cc\u0631 \u062c\u0644\u0628 \u0645\u06cc \u0646\u0645\u0627\u06cc\u06cc\u0645.\n\n1- \u0628\u0631\u0627\u06cc \u0627\u06cc\u0646 \u0628\u0631\u0646\u0627\u0645\u0647 \u0632\u062d\u0645\u062a \u0632\u06cc\u0627\u062f\u06cc \u06a9\u0634\u06cc\u062f\u0647 \u0634\u062f\u0647 \u0627\u0633\u062a \u0648 \u0645\u0627 \u0647\u0645\u0686\u0646\u0627\u0646 \u062f\u0631 \u062a\u0644\u0627\u0634 \u0628\u0631\u0627\u06cc \u0627\u0631\u0627\u0626\u0647 \u0646\u0633\u062e\u0647 \u0647\u0627\u06cc \u06a9\u0627\u0645\u0644\u062a\u0631 \u0648 \u067e\u06cc\u0634\u0631\u0641\u062a\u0647 \u062a\u0631 \u0628\u0647 \u0634\u0645\u0627 \u0639\u0632\u06cc\u0632\u0627\u0646 \u0647\u0633\u062a\u06cc\u0645.\n\n2- \u0628\u0631\u0627\u06cc \u062c\u0628\u0631\u0627\u0646 \u0647\u0632\u06cc\u0646\u0647 \u0647\u0627\u06cc \u062a\u0648\u0644\u06cc\u062f\u060c \u0627\u06cc\u0646 \u0628\u0631\u0646\u0627\u0645\u0647 \u0628\u0647 \u0635\u0648\u0631\u062a \u067e\u0648\u0644\u06cc \u062f\u0631 \u0645\u0627\u0631\u06a9\u062a \u06a9\u0627\u0641\u0647 \u0628\u0627\u0632\u0627\u0631 \u0645\u0646\u062a\u0634\u0631 \u0634\u062f\u0647 \u0627\u0633\u062a. (\u0647\u0631\u06af\u0648\u0646\u0647 \u0627\u0633\u062a\u0641\u0627\u062f\u0647 \u0631\u0627\u06cc\u06af\u0627\u0646 \u0627\u0632 \u0627\u06cc\u0646 \u0628\u0631\u0646\u0627\u0645\u0647 \u0628\u0631\u062e\u0644\u0627\u0641 \u0631\u0636\u0627\u06cc\u062a \u0645\u0627 \u0645\u06cc \u0628\u0627\u0634\u062f.)\n-\u0644\u0637\u0641\u0627 \u062f\u0631 \u0635\u0648\u0631\u062a\u06cc \u06a9\u0647 \u0627\u06cc\u0646 \u0628\u0631\u0646\u0627\u0645\u0647 \u0631\u0627 \u0631\u0627\u06cc\u06af\u0627\u0646 \u062f\u0627\u0646\u0644\u0648\u062f \u06a9\u0631\u062f\u0647 \u0627\u06cc\u062f \u0648 \u062a\u0645\u0627\u06cc\u0644 \u0628\u0647 \u0627\u0633\u062a\u0641\u0627\u062f\u0647 \u0627\u0632 \u0622\u0646 \u0631\u0627 \u062f\u0627\u0631\u06cc\u062f\u060c \u062d\u062a\u0645\u0627 \u0646\u0633\u062e\u0647 \u0628\u0639\u062f\u06cc \u0622\u0646 \u0631\u0627 \u0627\u0632 \u0645\u0627\u0631\u06a9\u062a \u06a9\u0627\u0641\u0647 \u0628\u0627\u0632\u0627\u0631 \u062e\u0631\u06cc\u062f\u0627\u0631\u06cc \u06a9\u0646\u06cc\u062f.\n- \u0628\u0627 \u06cc\u06a9\u0628\u0627\u0631 \u062e\u0631\u06cc\u062f\u060c \u062a\u0645\u0627\u0645\u06cc \u0646\u0633\u062e\u0647 \u0647\u0627\u06cc \u0628\u0639\u062f\u06cc \u0622\u0646 \u0631\u0627 \u0645\u06cc \u062a\u0648\u0627\u0646\u06cc\u062f \u0631\u0627\u06cc\u06af\u0627\u0646 \u062f\u0631\u06cc\u0627\u0641\u062a \u06a9\u0646\u06cc\u062f.\n\n\u0628\u0627 \u062a\u0634\u06a9\u0631\n\u06af\u0631\u0648\u0647 \u0628\u0631\u0646\u0627\u0645\u0647 \u0646\u0648\u06cc\u0633\u06cc \u0647\u0627\u0646\u06cc\u0633\u062a\u0627");
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            } else if (!settingManager.m944b("ghostUnreadHelpDisplayed")) {
                settingManager.m943a("ghostUnreadHelpDisplayed", true);
                builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("GhostModeUnreadHelp", C0338R.string.GhostModeUnreadHelp));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            } else if (!settingManager.m944b("channelJoinDisplayed")) {
                settingManager.m943a("channelJoinDisplayed", true);
                builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("ChannelMessage", C0338R.string.ChannelMessage));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(DialogsActivity.this.getParentActivity(), LaunchActivity.class);
                        intent.setAction("android.intent.action.VIEW");
                        intent.setData(Uri.parse("https://telegram.me/joinchat/BlusLzwtWIiyvfw4Vguksg"));
                        DialogsActivity.this.getParentActivity().startActivity(intent);
                    }
                });
                builder.create().show();
            }
            showMaterialHelp();
            AlarmUtil.m275a(getParentActivity());
        }
    }

    private void updateListBG() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2514y;
            int i2 = AdvanceTheme.av;
            if (i2 > 0) {
                Orientation orientation;
                switch (i2) {
                    case lastSeen /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case ghostMode /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case category /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                int i3 = AdvanceTheme.aw;
                int[] iArr = new int[lastSeen];
                iArr[0] = i;
                iArr[1] = i3;
                this.listView.setBackgroundDrawable(new GradientDrawable(orientation, iArr));
                return;
            }
            this.listView.setBackgroundColor(i);
        }
    }

    private void updatePasscodeButton() {
        if (this.passcodeItem != null) {
            if (UserConfig.passcodeHash.length() == 0 || this.searching) {
                this.passcodeItem.setVisibility(8);
                return;
            }
            this.passcodeItem.setVisibility(0);
            int i = AdvanceTheme.f2507r;
            Drawable drawable;
            if (UserConfig.appLocked) {
                this.passcodeItem.setIcon((int) C0338R.drawable.lock_close);
                if (ThemeUtil.m2490b()) {
                    drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.lock_close);
                    if (drawable != null) {
                        drawable.setColorFilter(i, Mode.MULTIPLY);
                    }
                    this.passcodeItem.setIcon(drawable);
                    return;
                }
                return;
            }
            this.passcodeItem.setIcon((int) C0338R.drawable.lock_open);
            if (ThemeUtil.m2490b()) {
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.lock_open);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                this.passcodeItem.setIcon(drawable);
            }
        }
    }

    private void updateVisibleRows(int i) {
        if (this.listView != null) {
            int childCount = this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof DialogCell) {
                    if (this.listView.getAdapter() != this.dialogsSearchAdapter) {
                        DialogCell dialogCell = (DialogCell) childAt;
                        if ((i & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                            dialogCell.checkCurrentDialogIndex();
                            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                                dialogCell.setDialogSelected(dialogCell.getDialogId() == this.openedDialogId);
                            }
                        } else if ((i & TLRPC.USER_FLAG_UNUSED3) == 0) {
                            dialogCell.update(i);
                        } else if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                            dialogCell.setDialogSelected(dialogCell.getDialogId() == this.openedDialogId);
                        }
                    }
                } else if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                } else if (childAt instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) childAt).update(i);
                } else if (childAt instanceof RecyclerListView) {
                    RecyclerListView recyclerListView = (RecyclerListView) childAt;
                    int childCount2 = recyclerListView.getChildCount();
                    for (int i3 = 0; i3 < childCount2; i3++) {
                        View childAt2 = recyclerListView.getChildAt(i3);
                        if (childAt2 instanceof HintDialogCell) {
                            ((HintDialogCell) childAt2).checkUnreadCounter(i);
                        }
                    }
                }
            }
            if (!(this.dialogsTab == null || (i & TLRPC.USER_FLAG_UNUSED2) == 0)) {
                this.dialogsTab.m967a(this.categoryId, this.hiddenMode);
            }
            if (!(this.dialogsAdapter == null || this.dialogsTab == null || this.dialogsTab.m966a() == null || this.dialogsTab.m966a().m2225a() != 10)) {
                this.dialogsAdapter.notifyDataSetChanged();
            }
            updateListBG();
        }
    }

    public boolean allowCaption() {
        return true;
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public void changeGhostModeState() {
        boolean z = true;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
        Editor edit = sharedPreferences.edit();
        boolean z2 = sharedPreferences.getBoolean("ghost_mode", false);
        edit.putBoolean("ghost_mode", !z2);
        edit.putBoolean("not_send_read_state", !z2);
        edit.commit();
        if (z2) {
            z = false;
        }
        MoboConstants.f1338e = z;
        MoboConstants.m1379a();
        this.actionBar.changeGhostModeVisibility();
        MessagesController.getInstance().reRunUpdateTimerProc();
        if (this.ghostItem != null) {
            this.ghostItem.setIcon(MoboConstants.f1338e ? C0338R.drawable.ic_ghost : C0338R.drawable.ic_ghost_disable);
            if (ThemeUtil.m2490b()) {
                int i = AdvanceTheme.f2507r;
                Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ghost);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ghost_disable);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
            }
        }
        if (this.hiddenMode && this.parentLayout != null) {
            this.parentLayout.rebuildAllFragmentViews(false);
        }
        if (getParentActivity() != null) {
            PhotoViewer.getInstance().destroyPhotoViewer();
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
        }
    }

    public View createView(Context context) {
        Drawable drawable;
        this.searching = false;
        this.searchWas = false;
        AndroidUtilities.runOnUIThread(new C15331(context));
        int i = AdvanceTheme.f2507r;
        int i2 = AdvanceTheme.f2513x;
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.categoryId > 0 && new DataBaseAccess().m835a(Long.valueOf(this.categoryId), false) == null) {
            this.categoryId = 0;
        }
        if (!this.onlySelect && this.searchString == null && com.hanista.mobogram.mobo.p001b.CategoryUtil.m351b()) {
            this.categoryItem = createMenu.addItem((int) category, this.categoryId == 0 ? C0338R.drawable.ic_close_category : C0338R.drawable.ic_category);
        }
        if (!(this.onlySelect || this.searchString != null || this.hiddenMode)) {
            if (ThemeUtil.m2490b()) {
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.lock_close);
                drawable.setColorFilter(i, Mode.MULTIPLY);
                this.passcodeItem = createMenu.addItem(1, drawable);
            } else {
                this.passcodeItem = createMenu.addItem(1, (int) C0338R.drawable.lock_close);
            }
            updatePasscodeButton();
        }
        if (MoboConstants.f1348o) {
            this.ghostItem = createMenu.addItem((int) ghostMode, MoboConstants.f1338e ? C0338R.drawable.ic_ghost : C0338R.drawable.ic_ghost_disable);
        }
        if (MoboConstants.f1347n) {
            createMenu.addItem((int) lastSeen, (int) C0338R.drawable.ic_mobo_seen);
        }
        this.searchFiledItem = createMenu.addItem(0, (int) C0338R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C15342());
        this.searchFiledItem.getSearchField().setHint(LocaleController.getString("Search", C0338R.string.Search));
        if (ThemeUtil.m2490b()) {
            if (i2 != -1) {
                this.searchFiledItem.getSearchField().setTextColor(i2);
                this.searchFiledItem.getSearchField().setHintTextColor(AdvanceTheme.m2275a(AdvanceTheme.f2513x, 0.5f));
            }
            drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_close_white);
            if (drawable != null) {
                drawable.setColorFilter(i, Mode.MULTIPLY);
            }
            this.searchFiledItem.getClearButton().setImageDrawable(drawable);
        }
        if (this.onlySelect) {
            if (ThemeUtil.m2490b()) {
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_back);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                this.actionBar.setBackButtonDrawable(drawable);
            } else {
                this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
            }
            this.actionBar.setTitle(LocaleController.getString("SelectChat", C0338R.string.SelectChat));
        } else {
            if (this.searchString != null) {
                this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
            } else {
                this.actionBar.setBackButtonDrawable(new MenuDrawable());
            }
            if (BuildVars.DEBUG_VERSION) {
                this.actionBar.setTitle(LocaleController.getString("AppNameBeta", C0338R.string.AppNameBeta));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            }
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C15353());
        paintHeader();
        View frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        initThemeBackground(this.fragmentView);
        this.dialogsTab = new DialogsTab(this);
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setItemAnimator(null);
        this.listView.setInstantClick(true);
        this.listView.setLayoutAnimation(null);
        this.listView.setTag(Integer.valueOf(category));
        this.layoutManager = new C15364(context);
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : lastSeen);
        Object obj = (MoboConstants.f1334a && MoboConstants.f1344k != 0 && this.searchString == null) ? 1 : null;
        if ((this.dialogsType == 0 || this.dialogsType == 1) && obj != null) {
            frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 48, 0.0f, MoboConstants.f1331X ? 0.0f : 40.0f, 0.0f, MoboConstants.f1331X ? 40.0f : 0.0f));
        } else {
            frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        }
        this.onTouchListener = new DialogTouchListener(context);
        this.listView.setOnTouchListener(this.onTouchListener);
        this.listView.setOnItemClickListener(new C15375());
        this.listView.setOnItemLongClickListener(new C15446());
        this.searchEmptyView = new EmptyTextProgressView(context);
        this.searchEmptyView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
        frameLayout.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.emptyView = new LinearLayout(context);
        this.emptyView.setOrientation(1);
        this.emptyView.setVisibility(8);
        this.emptyView.setGravity(17);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.emptyView.setOnTouchListener(new C15457());
        View textView = new TextView(context);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(LocaleController.getString("NoChats", C0338R.string.NoChats));
        textView.setTextColor(-6974059);
        textView.setGravity(17);
        textView.setTextSize(1, 20.0f);
        this.emptyView.addView(textView, LayoutHelper.createLinear(-2, -2));
        View textView2 = new TextView(context);
        textView2.setTypeface(FontUtil.m1176a().m1161d());
        CharSequence string = LocaleController.getString("NoChatsHelp", C0338R.string.NoChatsHelp);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            string = string.replace('\n', ' ');
        }
        textView2.setText(string);
        textView2.setTextColor(-6974059);
        textView2.setTextSize(1, 15.0f);
        textView2.setGravity(17);
        textView2.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), 0);
        textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.emptyView.addView(textView2, LayoutHelper.createLinear(-2, -2));
        this.progressView = new ProgressBar(context);
        this.progressView.setVisibility(8);
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
        this.floatingButton = new ImageView(context);
        ImageView imageView = this.floatingButton;
        int i3 = (this.onlySelect || UserConfig.isRobot) ? 8 : 0;
        imageView.setVisibility(i3);
        this.floatingButton.setScaleType(ScaleType.CENTER);
        drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.floating_white);
        if (drawable != null) {
            drawable.setColorFilter(ThemeUtil.m2485a().m2289c(), Mode.MULTIPLY);
        }
        this.floatingButton.setBackgroundDrawable(drawable);
        this.floatingButton.setImageResource(C0338R.drawable.floating_pencil);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            int[] iArr = new int[]{16842919};
            float[] fArr = new float[lastSeen];
            fArr[0] = (float) AndroidUtilities.dp(2.0f);
            fArr[1] = (float) AndroidUtilities.dp(4.0f);
            stateListAnimator.addState(iArr, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", fArr).setDuration(200));
            iArr = new int[0];
            fArr = new float[lastSeen];
            fArr[0] = (float) AndroidUtilities.dp(4.0f);
            fArr[1] = (float) AndroidUtilities.dp(2.0f);
            stateListAnimator.addState(iArr, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", fArr).setDuration(200));
            this.floatingButton.setStateListAnimator(stateListAnimator);
            this.floatingButton.setOutlineProvider(new C15468());
        }
        View view = this.floatingButton;
        i2 = (LocaleController.isRTL ? ghostMode : 5) | 80;
        float f = LocaleController.isRTL ? 14.0f : 0.0f;
        float f2 = LocaleController.isRTL ? 0.0f : 14.0f;
        float f3 = (!MoboConstants.f1331X || obj == null) ? 14.0f : 54.0f;
        frameLayout.addView(view, LayoutHelper.createFrame(-2, -2.0f, i2, f, 0.0f, f2, f3));
        this.floatingButton.setOnClickListener(new C15479());
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && DialogsActivity.this.searching && DialogsActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(DialogsActivity.this.getParentActivity().getCurrentFocus());
                }
                if (ThemeUtil.m2490b()) {
                    int i2 = AdvanceTheme.f2491b;
                    Glow.m522a(DialogsActivity.this.listView, AdvanceTheme.f2508s);
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                boolean z = false;
                int findFirstVisibleItemPosition = DialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
                int abs = Math.abs(DialogsActivity.this.layoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                int itemCount = recyclerView.getAdapter().getItemCount();
                if (!DialogsActivity.this.searching || !DialogsActivity.this.searchWas) {
                    if (abs > 0 && DialogsActivity.this.layoutManager.findLastVisibleItemPosition() >= DialogsActivity.this.getDialogsArray().size() - 10 && !MessagesController.getInstance().dialogsEndReached) {
                        MessagesController.getInstance().loadDialogs(-1, 100, !MessagesController.getInstance().dialogsEndReached);
                    }
                    if (DialogsActivity.this.floatingButton.getVisibility() != 8) {
                        boolean z2;
                        int i3;
                        View childAt = recyclerView.getChildAt(0);
                        abs = childAt != null ? childAt.getTop() : 0;
                        if (DialogsActivity.this.prevPosition == findFirstVisibleItemPosition) {
                            int access$3600 = DialogsActivity.this.prevTop - abs;
                            z2 = abs < DialogsActivity.this.prevTop;
                            if (Math.abs(access$3600) > 1) {
                                i3 = 1;
                            }
                        } else {
                            if (findFirstVisibleItemPosition > DialogsActivity.this.prevPosition) {
                                z = true;
                            }
                            z2 = z;
                            i3 = 1;
                        }
                        if (i3 != 0 && DialogsActivity.this.scrollUpdated) {
                            DialogsActivity.this.hideFloatingButton(z2);
                        }
                        DialogsActivity.this.prevPosition = findFirstVisibleItemPosition;
                        DialogsActivity.this.prevTop = abs;
                        DialogsActivity.this.scrollUpdated = true;
                    }
                } else if (abs > 0 && DialogsActivity.this.layoutManager.findLastVisibleItemPosition() == itemCount - 1 && !DialogsActivity.this.dialogsSearchAdapter.isMessagesSearchEndReached()) {
                    DialogsActivity.this.dialogsSearchAdapter.loadMoreSearchMessages();
                }
            }
        });
        if (this.searchString == null) {
            i3 = ((this.dialogsType == 0 || this.dialogsType == 1) && obj != null) ? MoboConstants.f1329V : this.dialogsType;
            this.dialogsAdapter = new DialogsAdapter(context, i3);
            this.dialogsAdapter.setHiddenMode(this.hiddenMode);
            this.dialogsAdapter.setCategoryId(this.categoryId);
            if (AndroidUtilities.isTablet() && this.openedDialogId != 0) {
                this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
            }
            this.listView.setAdapter(this.dialogsAdapter);
        }
        i3 = 0;
        if (this.searchString != null) {
            i3 = lastSeen;
        } else if (!this.onlySelect) {
            i3 = 1;
        }
        this.dialogsSearchAdapter = new DialogsSearchAdapter(context, i3, this.dialogsType);
        this.dialogsSearchAdapter.setDelegate(new DialogsSearchAdapterDelegate() {

            /* renamed from: com.hanista.mobogram.ui.DialogsActivity.11.1 */
            class C15321 implements OnClickListener {
                final /* synthetic */ int val$did;

                C15321(int i) {
                    this.val$did = i;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    SearchQuery.removePeer(this.val$did);
                }
            }

            public void didPressedOnSubDialog(int i) {
                if (DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.didSelectResult((long) i, true, false);
                    return;
                }
                Bundle bundle = new Bundle();
                if (i > 0) {
                    bundle.putInt("user_id", i);
                } else {
                    bundle.putInt("chat_id", -i);
                }
                if (DialogsActivity.this.actionBar != null) {
                    DialogsActivity.this.actionBar.closeSearchField();
                }
                if (AndroidUtilities.isTablet() && DialogsActivity.this.dialogsAdapter != null) {
                    DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = (long) i);
                    DialogsActivity.this.updateVisibleRows(TLRPC.USER_FLAG_UNUSED3);
                }
                if (DialogsActivity.this.searchString != null) {
                    if (MessagesController.checkCanOpenChat(bundle, DialogsActivity.this)) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        DialogsActivity.this.presentFragment(new ChatActivity(bundle));
                    }
                } else if (MessagesController.checkCanOpenChat(bundle, DialogsActivity.this)) {
                    DialogsActivity.this.presentFragment(new ChatActivity(bundle));
                }
            }

            public void needRemoveHint(int i) {
                if (DialogsActivity.this.getParentActivity() != null && MessagesController.getInstance().getUser(Integer.valueOf(i)) != null) {
                    Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setMessage(LocaleController.formatString("ChatHintsDelete", C0338R.string.ChatHintsDelete, ContactsController.formatName(r0.first_name, r0.last_name)));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C15321(i));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    DialogsActivity.this.showDialog(builder.create());
                }
            }

            public void searchStateChanged(boolean z) {
                if (!DialogsActivity.this.searching || !DialogsActivity.this.searchWas || DialogsActivity.this.searchEmptyView == null) {
                    return;
                }
                if (z) {
                    DialogsActivity.this.searchEmptyView.showProgress();
                } else {
                    DialogsActivity.this.searchEmptyView.showTextView();
                }
            }
        });
        if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
            this.searchEmptyView.setVisibility(8);
            this.emptyView.setVisibility(8);
            this.listView.setEmptyView(this.progressView);
        } else {
            this.searchEmptyView.setVisibility(8);
            this.progressView.setVisibility(8);
            this.listView.setEmptyView(this.emptyView);
        }
        if (this.searchString != null) {
            this.actionBar.openSearchField(this.searchString);
        }
        if (!this.onlySelect && this.dialogsType == 0) {
            frameLayout.addView(new PlayerView(context, this), LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            frameLayout.addView(new PowerView(context, this), LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        if ((this.dialogsType == 0 || this.dialogsType == 1) && obj != null) {
            this.dialogsTab.m968a(getParentActivity(), frameLayout, this.listView, this.onTouchListener);
            this.dialogsTab.m967a(this.categoryId, this.hiddenMode);
        }
        updateCategoryState();
        showWelcomeDialog();
        initHiddenModeEnteringMethods();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.dialogsNeedReload) {
            if (this.dialogsAdapter != null) {
                if (this.dialogsAdapter.isDataSetChanged()) {
                    this.dialogsAdapter.notifyDataSetChanged();
                } else {
                    updateVisibleRows(TLRPC.MESSAGE_FLAG_HAS_BOT_ID);
                }
            }
            if (this.dialogsSearchAdapter != null) {
                this.dialogsSearchAdapter.notifyDataSetChanged();
            }
            if (this.listView != null) {
                if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                    this.searchEmptyView.setVisibility(8);
                    this.emptyView.setVisibility(8);
                    this.listView.setEmptyView(this.progressView);
                } else {
                    try {
                        this.progressView.setVisibility(8);
                        if (this.searching && this.searchWas) {
                            this.emptyView.setVisibility(8);
                            this.listView.setEmptyView(this.searchEmptyView);
                        } else {
                            this.searchEmptyView.setVisibility(8);
                            this.listView.setEmptyView(this.emptyView);
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }
        } else if (i == NotificationCenter.emojiDidLoaded) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.updateInterfaces) {
            if (this.dialogsAdapter != null && this.dialogsAdapter.getDialogsType() == 8 && this.dialogsAdapter.isDataSetChanged()) {
                this.dialogsAdapter.notifyDataSetChanged();
            }
            updateVisibleRows(((Integer) objArr[0]).intValue());
        } else if (i == NotificationCenter.appDidLogout) {
            dialogsLoaded = false;
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.contactsDidLoaded) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.openedChatChanged) {
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                boolean booleanValue = ((Boolean) objArr[1]).booleanValue();
                long longValue = ((Long) objArr[0]).longValue();
                if (!booleanValue) {
                    this.openedDialogId = longValue;
                } else if (longValue == this.openedDialogId) {
                    this.openedDialogId = 0;
                }
                if (this.dialogsAdapter != null) {
                    this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
                }
                updateVisibleRows(TLRPC.USER_FLAG_UNUSED3);
            }
        } else if (i == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.messageReceivedByAck || i == NotificationCenter.messageReceivedByServer || i == NotificationCenter.messageSendError) {
            updateVisibleRows(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
        } else if (i == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        }
        if (i == NotificationCenter.needReloadRecentDialogsSearch) {
            if (this.dialogsSearchAdapter != null) {
                this.dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (i == NotificationCenter.didLoadedReplyMessages) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.reloadHints && this.dialogsSearchAdapter != null) {
            this.dialogsSearchAdapter.notifyDataSetChanged();
        }
    }

    public long getCategoryId() {
        return this.categoryId;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        if (fileLocation == null || this.dialogCell == null) {
            return null;
        }
        FileLocation fileLocation2;
        if (this.user_id != 0) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
            if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                fileLocation2 = user.photo.photo_big;
            }
            fileLocation2 = null;
        } else {
            if (this.chat_id != 0) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                if (!(chat == null || chat.photo == null || chat.photo.photo_big == null)) {
                    fileLocation2 = chat.photo.photo_big;
                }
            }
            fileLocation2 = null;
        }
        if (fileLocation2 == null || fileLocation2.local_id != fileLocation.local_id || fileLocation2.volume_id != fileLocation.volume_id || fileLocation2.dc_id != fileLocation.dc_id) {
            return null;
        }
        int[] iArr = new int[lastSeen];
        this.dialogCell.getLocationInWindow(iArr);
        PlaceProviderObject placeProviderObject = new PlaceProviderObject();
        placeProviderObject.viewX = iArr[0];
        placeProviderObject.viewY = iArr[1] - AndroidUtilities.statusBarHeight;
        placeProviderObject.parentView = this.dialogCell;
        placeProviderObject.imageReceiver = this.dialogCell.getAvatarImage();
        placeProviderObject.dialogId = this.user_id;
        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
        placeProviderObject.size = -1;
        placeProviderObject.radius = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
        return placeProviderObject;
    }

    public int getSelectedCount() {
        return 0;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        return null;
    }

    public boolean isHiddenMode() {
        return this.hiddenMode;
    }

    public boolean isMainDialogList() {
        return this.delegate == null && this.searchString == null;
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    protected void onBecomeFullyVisible() {
        showMaterialHelp();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (!this.onlySelect && this.floatingButton != null) {
            this.floatingButton.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    DialogsActivity.this.floatingButton.setTranslationY(DialogsActivity.this.floatingHidden ? (float) AndroidUtilities.dp(140.0f) : 0.0f);
                    DialogsActivity.this.floatingButton.setClickable(!DialogsActivity.this.floatingHidden);
                    if (DialogsActivity.this.floatingButton == null) {
                        return;
                    }
                    if (VERSION.SDK_INT < 16) {
                        DialogsActivity.this.floatingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        DialogsActivity.this.floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (this.permissionDialog != null && dialog == this.permissionDialog && getParentActivity() != null) {
            askForPermissons();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.swipeBackEnabled = false;
        if (getArguments() != null) {
            this.onlySelect = this.arguments.getBoolean("onlySelect", false);
            this.cantSendToChannels = this.arguments.getBoolean("cantSendToChannels", false);
            this.dialogsType = this.arguments.getInt("dialogsType", 0);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.selectAlertStringGroup = this.arguments.getString("selectAlertStringGroup");
            this.addToGroupAlertString = this.arguments.getString("addToGroupAlertString");
            this.justSelect = this.arguments.getBoolean("justSelect", false);
            this.hiddenMode = this.arguments.getBoolean("hiddenMode", false);
            this.categoryId = this.arguments.getLong("categoryId", 0);
        }
        if (this.searchString == null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadHints);
        }
        if (UserConfig.isRobot) {
            dialogsLoaded = true;
        }
        if (!dialogsLoaded) {
            MessagesController.getInstance().loadDialogs(0, 100, true);
            ContactsController.getInstance().checkInviteText();
            StickersQuery.checkFeaturedStickers();
            dialogsLoaded = true;
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.searchString == null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadHints);
        }
        this.delegate = null;
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 1) {
            int i2 = 0;
            while (i2 < strArr.length) {
                if (iArr.length > i2 && iArr[i2] == 0) {
                    String str = strArr[i2];
                    Object obj = -1;
                    switch (str.hashCode()) {
                        case 1365911975:
                            if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                                int i3 = 1;
                                break;
                            }
                            break;
                        case 1977429404:
                            if (str.equals("android.permission.READ_CONTACTS")) {
                                obj = null;
                                break;
                            }
                            break;
                    }
                    switch (obj) {
                        case VideoPlayer.TRACK_DEFAULT /*0*/:
                            ContactsController.getInstance().readContacts();
                            break;
                        case VideoPlayer.TYPE_AUDIO /*1*/:
                            ImageLoader.getInstance().checkMediaPaths();
                            break;
                        default:
                            break;
                    }
                }
                i2++;
            }
        }
    }

    public void onResume() {
        super.onResume();
        HiddenConfig.f1402e = this.hiddenMode;
        if (this.dialogsAdapter != null) {
            this.dialogsAdapter.notifyDataSetChanged();
        }
        if (this.dialogsSearchAdapter != null) {
            this.dialogsSearchAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && !this.onlySelect && VERSION.SDK_INT >= 23) {
            Context parentActivity = getParentActivity();
            if (parentActivity != null) {
                this.checkPermission = false;
                if (!(parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") == 0 && parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0)) {
                    Builder builder;
                    Dialog create;
                    if (parentActivity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                        builder = new Builder(parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionContacts", C0338R.string.PermissionContacts));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                        create = builder.create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else if (parentActivity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        builder = new Builder(parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionStorage", C0338R.string.PermissionStorage));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                        create = builder.create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else {
                        askForPermissons();
                    }
                }
            }
        }
        this.actionBar.changeGhostModeVisibility();
        if (this.dialogsTab != null) {
            this.dialogsTab.m967a(this.categoryId, this.hiddenMode);
        }
        initTheme();
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
    }

    public void setDelegate(DialogsActivityDelegate dialogsActivityDelegate) {
        this.delegate = dialogsActivityDelegate;
    }

    public void setPhotoChecked(int i) {
    }

    public void setSearchString(String str) {
        this.searchString = str;
    }

    public void updateCategoryState() {
        if (this.categoryItem != null) {
            this.categoryItem.setIcon(this.categoryId == 0 ? C0338R.drawable.ic_close_category : C0338R.drawable.ic_category);
            if (this.searching) {
                this.categoryItem.setVisibility(8);
            } else {
                this.categoryItem.setVisibility(0);
            }
            if (ThemeUtil.m2490b()) {
                int i = AdvanceTheme.f2507r;
                Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_category);
                if (drawable != null) {
                    drawable.setColorFilter(i, Mode.MULTIPLY);
                }
                Drawable drawable2 = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_close_category);
                if (drawable2 != null) {
                    drawable2.setColorFilter(i, Mode.MULTIPLY);
                }
            }
        }
        if (this.onlySelect) {
            this.actionBar.setTitle(LocaleController.getString("SelectChat", C0338R.string.SelectChat));
        } else if (this.categoryId > 0) {
            Category a = new DataBaseAccess().m835a(Long.valueOf(this.categoryId), false);
            this.actionBar.setTitle(a == null ? LocaleController.getString("AppName", C0338R.string.AppName) : a.m281b());
        } else {
            this.actionBar.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        }
    }

    public void updatePhotoAtIndex(int i) {
    }

    public void willHidePhotoViewer() {
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}
