package com.hanista.mobogram.mobo;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.DialogsAdapter;
import com.hanista.mobogram.ui.Cells.DialogCell;
import com.hanista.mobogram.ui.Cells.ProfileSearchCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.d */
public class DialogSelectActivity extends BaseFragment implements NotificationCenterDelegate {
    private static boolean f511h;
    public RecyclerListView f512a;
    public DialogsAdapter f513b;
    public ProgressBar f514c;
    ProgressDialog f515d;
    private LinearLayoutManager f516e;
    private LinearLayout f517f;
    private int f518g;
    private List<Long> f519i;
    private DialogSelectActivity f520j;
    private long f521k;
    private boolean f522l;

    /* renamed from: com.hanista.mobogram.mobo.d.a */
    public interface DialogSelectActivity {
        void m289a(List<Long> list);
    }

    /* renamed from: com.hanista.mobogram.mobo.d.1 */
    class DialogSelectActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ DialogSelectActivity f477a;

        DialogSelectActivity(DialogSelectActivity dialogSelectActivity) {
            this.f477a = dialogSelectActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f477a.finishFragment();
            } else if (i == 1) {
                if (this.f477a.f520j != null) {
                    this.f477a.f520j.m289a(this.f477a.f519i);
                }
                this.f477a.finishFragment();
            } else if (i == 2) {
                this.f477a.m531a();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.d.2 */
    class DialogSelectActivity extends LinearLayoutManager {
        final /* synthetic */ DialogSelectActivity f478a;

        DialogSelectActivity(DialogSelectActivity dialogSelectActivity, Context context) {
            this.f478a = dialogSelectActivity;
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.d.3 */
    class DialogSelectActivity implements OnItemClickListener {
        final /* synthetic */ DialogSelectActivity f479a;

        DialogSelectActivity(DialogSelectActivity dialogSelectActivity) {
            this.f479a = dialogSelectActivity;
        }

        public void onItemClick(View view, int i) {
            if (this.f479a.f512a != null && this.f479a.f512a.getAdapter() != null && this.f479a.f512a.getAdapter() == this.f479a.f513b) {
                TL_dialog item = this.f479a.f513b.getItem(i);
                if (item != null) {
                    long j = item.id;
                    if (this.f479a.f522l) {
                        if (this.f479a.f519i.contains(Long.valueOf(j))) {
                            this.f479a.f519i.remove(Long.valueOf(j));
                        } else {
                            this.f479a.f519i.add(Long.valueOf(j));
                        }
                        this.f479a.f513b.notifyDataSetChanged();
                        return;
                    }
                    this.f479a.f519i.clear();
                    this.f479a.f519i.add(Long.valueOf(j));
                    if (this.f479a.f520j != null) {
                        this.f479a.f520j.m289a(this.f479a.f519i);
                    }
                    this.f479a.removeSelfFromStack();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.d.4 */
    class DialogSelectActivity implements OnTouchListener {
        final /* synthetic */ DialogSelectActivity f480a;

        DialogSelectActivity(DialogSelectActivity dialogSelectActivity) {
            this.f480a = dialogSelectActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.d.5 */
    class DialogSelectActivity implements Runnable {
        final /* synthetic */ DialogSelectActivity f481a;

        DialogSelectActivity(DialogSelectActivity dialogSelectActivity) {
            this.f481a = dialogSelectActivity;
        }

        public void run() {
            if (this.f481a.f512a.getAdapter() == this.f481a.f513b) {
                Iterator it = this.f481a.f513b.getDialogsArray().iterator();
                while (it.hasNext()) {
                    long j = ((TL_dialog) it.next()).id;
                    if (!this.f481a.f519i.contains(Long.valueOf(j))) {
                        this.f481a.f519i.add(Long.valueOf(j));
                    }
                }
                this.f481a.f513b.notifyDataSetChanged();
            }
            this.f481a.f515d.dismiss();
            Toast.makeText(this.f481a.getParentActivity(), LocaleController.getString("AllChatsSelected", C0338R.string.AllChatsSelected), 0).show();
        }
    }

    public DialogSelectActivity(Bundle bundle) {
        super(bundle);
        this.f519i = new ArrayList();
    }

    private void m531a() {
        this.f515d = new ProgressDialog(getParentActivity());
        this.f515d.setMessage(LocaleController.getString("PleaseWait", C0338R.string.PleaseWait));
        this.f515d.setCanceledOnTouchOutside(false);
        this.f515d.setCancelable(false);
        this.f515d.show();
        new Handler().postDelayed(new DialogSelectActivity(this), 500);
    }

    private void m532a(int i) {
        if (this.f512a != null) {
            int childCount = this.f512a.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.f512a.getChildAt(i2);
                if (childAt instanceof DialogCell) {
                    DialogCell dialogCell = (DialogCell) childAt;
                    if ((i & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
                        dialogCell.checkCurrentDialogIndex();
                    } else if ((i & TLRPC.USER_FLAG_UNUSED3) == 0) {
                        dialogCell.update(i);
                    }
                } else if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                } else if (childAt instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) childAt).update(i);
                }
            }
        }
    }

    public void m536a(DialogSelectActivity dialogSelectActivity) {
        this.f520j = dialogSelectActivity;
    }

    public View createView(Context context) {
        int i = 2;
        Theme.loadRecources(context);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.f522l) {
            if (ThemeUtil.m2490b()) {
                Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
                drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
                createMenu.addItemWithWidth(1, drawable, AndroidUtilities.dp(56.0f));
                drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_select_all);
                drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
                createMenu.addItemWithWidth(2, drawable, AndroidUtilities.dp(56.0f));
            } else {
                createMenu.addItemWithWidth(1, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
                createMenu.addItemWithWidth(2, (int) C0338R.drawable.ic_select_all, AndroidUtilities.dp(56.0f));
            }
        }
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        if (this.f522l) {
            this.actionBar.setTitle(LocaleController.getString("SelectChats", C0338R.string.SelectChats));
        } else {
            this.actionBar.setTitle(LocaleController.getString("SelectChat", C0338R.string.SelectChat));
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new DialogSelectActivity(this));
        View frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.f512a = new RecyclerListView(context);
        initThemeBackground(this.f512a);
        this.f512a.setVerticalScrollBarEnabled(true);
        this.f512a.setItemAnimator(null);
        this.f512a.setInstantClick(true);
        this.f512a.setLayoutAnimation(null);
        this.f516e = new DialogSelectActivity(this, context);
        this.f516e.setOrientation(1);
        this.f512a.setLayoutManager(this.f516e);
        if (VERSION.SDK_INT >= 11) {
            RecyclerListView recyclerListView = this.f512a;
            if (LocaleController.isRTL) {
                i = 1;
            }
            recyclerListView.setVerticalScrollbarPosition(i);
        }
        frameLayout.addView(this.f512a, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.f512a.setOnItemClickListener(new DialogSelectActivity(this));
        this.f517f = new LinearLayout(context);
        this.f517f.setOrientation(1);
        this.f517f.setVisibility(8);
        this.f517f.setGravity(17);
        frameLayout.addView(this.f517f, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.f517f.setOnTouchListener(new DialogSelectActivity(this));
        View textView = new TextView(context);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(LocaleController.getString("NoChats", C0338R.string.NoChats));
        textView.setTextColor(-6974059);
        textView.setGravity(17);
        textView.setTextSize(1, 20.0f);
        this.f517f.addView(textView, LayoutHelper.createLinear(-2, -2));
        this.f514c = new ProgressBar(context);
        this.f514c.setVisibility(8);
        frameLayout.addView(this.f514c, LayoutHelper.createFrame(-2, -2, 17));
        this.f513b = new DialogsAdapter(context, this.f518g);
        this.f513b.setSelectedDialogIds(this.f519i);
        this.f513b.setCategoryId(this.f521k);
        this.f512a.setAdapter(this.f513b);
        if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
            this.f517f.setVisibility(8);
            this.f512a.setEmptyView(this.f514c);
        } else {
            this.f514c.setVisibility(8);
            this.f512a.setEmptyView(this.f517f);
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.dialogsNeedReload) {
            if (this.f513b != null) {
                if (this.f513b.isDataSetChanged()) {
                    this.f513b.notifyDataSetChanged();
                } else {
                    m532a((int) TLRPC.MESSAGE_FLAG_HAS_BOT_ID);
                }
            }
            if (this.f512a != null) {
                try {
                    if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                        this.f517f.setVisibility(8);
                        this.f512a.setEmptyView(this.f514c);
                        return;
                    }
                    this.f514c.setVisibility(8);
                    this.f512a.setEmptyView(this.f517f);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        } else if (i == NotificationCenter.emojiDidLoaded) {
            if (this.f512a != null) {
                m532a(0);
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            if (this.f513b != null && this.f513b.getDialogsType() == 8 && this.f513b.isDataSetChanged()) {
                this.f513b.notifyDataSetChanged();
            }
            m532a(((Integer) objArr[0]).intValue());
        } else if (i == NotificationCenter.appDidLogout) {
            f511h = false;
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            m532a(0);
        } else if (i == NotificationCenter.contactsDidLoaded) {
            m532a(0);
        } else if (i == NotificationCenter.notificationsSettingsUpdated) {
            m532a(0);
        } else if (i == NotificationCenter.messageReceivedByAck || i == NotificationCenter.messageReceivedByServer || i == NotificationCenter.messageSendError) {
            m532a((int) ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (getArguments() != null) {
            this.f518g = this.arguments.getInt("dialogsType", 0);
            this.f521k = this.arguments.getLong("categoryId", 0);
            this.f522l = this.arguments.getBoolean("multiSelect", true);
        }
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
        if (!f511h) {
            MessagesController.getInstance().loadDialogs(0, 100, true);
            ContactsController.getInstance().checkInviteText();
            f511h = true;
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
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
                            ImageLoader.getInstance().createMediaPaths();
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
        if (this.f513b != null) {
            this.f513b.notifyDataSetChanged();
        }
        this.actionBar.changeGhostModeVisibility();
        initThemeActionBar();
    }
}
