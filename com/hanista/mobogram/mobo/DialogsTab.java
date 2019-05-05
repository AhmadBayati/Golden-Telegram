package com.hanista.mobogram.mobo;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.query.DraftQuery;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.DialogSelectActivity.DialogSelectActivity;
import com.hanista.mobogram.mobo.e.AnonymousClass12;
import com.hanista.mobogram.mobo.e.AnonymousClass13;
import com.hanista.mobogram.mobo.p019r.TabData;
import com.hanista.mobogram.mobo.p019r.TabsUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.DraftMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.DialogsAdapter;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.PagerSlidingTabStrip;
import com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.IconTabProvider;
import com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.OnLongClickOnTabListener;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.DialogsActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.e */
public class DialogsTab {
    private DialogsActivity f875a;
    private TabData f876b;
    private SharedPreferences f877c;
    private List<TabData> f878d;
    private ViewPager f879e;
    private LinearLayout f880f;
    private PagerSlidingTabStrip f881g;

    /* compiled from: DialogsTab */
    /* renamed from: com.hanista.mobogram.mobo.e.12 */
    class AnonymousClass12 implements OnClickListener {
        final /* synthetic */ SharedPreferences f851a;
        final /* synthetic */ int f852b;
        final /* synthetic */ int f853c;
        final /* synthetic */ DialogsTab f854d;

        AnonymousClass12(DialogsTab dialogsTab, SharedPreferences sharedPreferences, int i, int i2) {
            this.f854d = dialogsTab;
            this.f851a = sharedPreferences;
            this.f852b = i;
            this.f853c = i2;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            int i2 = 1;
            if (i == 0) {
                Editor edit = this.f851a.edit();
                String a = DialogsTab.m946a(this.f852b);
                if (this.f853c != 0) {
                    i2 = 0;
                }
                edit.putInt(a, i2);
                edit.commit();
                if (this.f854d.f875a != null && this.f854d.f875a.dialogsAdapter != null) {
                    this.f854d.f875a.dialogsAdapter.notifyDataSetChanged();
                }
            } else if (i == 1) {
                this.f854d.m958g();
            } else if (i == 2 && this.f854d.f876b.m2225a() == 4) {
                this.f854d.m960h();
            } else if (i == 2 && this.f854d.f876b.m2225a() == 3) {
                this.f854d.m962i();
            } else if (i != 2) {
            } else {
                if (this.f854d.f876b.m2225a() == 7 || this.f854d.f876b.m2225a() == 9 || this.f854d.f876b.m2225a() == 11) {
                    this.f854d.m964j();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.e.1 */
    class DialogsTab extends ViewPager {
        final /* synthetic */ DialogsTab f858a;

        DialogsTab(DialogsTab dialogsTab, Context context) {
            this.f858a = dialogsTab;
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.e.2 */
    class DialogsTab implements DialogSelectActivity {
        final /* synthetic */ DialogsTab f861a;

        /* renamed from: com.hanista.mobogram.mobo.e.2.1 */
        class DialogsTab implements OnClickListener {
            final /* synthetic */ List f859a;
            final /* synthetic */ DialogsTab f860b;

            DialogsTab(DialogsTab dialogsTab, List list) {
                this.f860b = dialogsTab;
                this.f859a = list;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                for (Long longValue : this.f859a) {
                    long longValue2 = longValue.longValue();
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) longValue2)));
                    if (chat == null || !chat.creator) {
                        MessagesController.getInstance().deleteUserFromChat((int) (-longValue2), UserConfig.getCurrentUser(), null);
                    }
                }
                if (AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
            }
        }

        DialogsTab(DialogsTab dialogsTab) {
            this.f861a = dialogsTab;
        }

        public void m827a(List<Long> list) {
            Builder builder = new Builder(this.f861a.f875a.getParentActivity());
            builder.setMessage(LocaleController.getString("SelectedChannelsLeaveAlert", C0338R.string.SelectedChannelsLeaveAlert));
            builder.setTitle(LocaleController.getString("LeaveChannel", C0338R.string.LeaveChannel));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new DialogsTab(this, list));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            this.f861a.f875a.showDialog(builder.create());
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.e.3 */
    class DialogsTab implements DialogSelectActivity {
        final /* synthetic */ DialogsTab f864a;

        /* renamed from: com.hanista.mobogram.mobo.e.3.1 */
        class DialogsTab implements OnClickListener {
            final /* synthetic */ List f862a;
            final /* synthetic */ DialogsTab f863b;

            DialogsTab(DialogsTab dialogsTab, List list) {
                this.f863b = dialogsTab;
                this.f862a = list;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                for (Long longValue : this.f862a) {
                    long longValue2 = longValue.longValue();
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) longValue2)));
                    if (chat != null) {
                        if (chat.megagroup) {
                            MessagesController.getInstance().deleteUserFromChat((int) (-longValue2), UserConfig.getCurrentUser(), null);
                        } else if (ChatObject.isNotInChat(chat)) {
                            MessagesController.getInstance().deleteDialog(longValue2, 0);
                        } else {
                            MessagesController.getInstance().deleteUserFromChat((int) (-longValue2), UserConfig.getCurrentUser(), null);
                        }
                    }
                }
                if (AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
            }
        }

        DialogsTab(DialogsTab dialogsTab) {
            this.f864a = dialogsTab;
        }

        public void m828a(List<Long> list) {
            Builder builder = new Builder(this.f864a.f875a.getParentActivity());
            builder.setMessage(LocaleController.getString("DeleteAndLeaveSomeGroupsAlert", C0338R.string.DeleteAndLeaveSomeGroupsAlert));
            builder.setTitle(LocaleController.getString("Delete", C0338R.string.Delete));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new DialogsTab(this, list));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            this.f864a.f875a.showDialog(builder.create());
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.e.4 */
    static class DialogsTab implements Comparator<TL_dialog> {
        DialogsTab() {
        }

        public int m829a(TL_dialog tL_dialog, TL_dialog tL_dialog2) {
            DraftMessage draft = DraftQuery.getDraft(tL_dialog.id);
            int i = (draft == null || draft.date < tL_dialog.last_message_date) ? tL_dialog.last_message_date : draft.date;
            DraftMessage draft2 = DraftQuery.getDraft(tL_dialog2.id);
            int i2 = (draft2 == null || draft2.date < tL_dialog2.last_message_date) ? tL_dialog2.last_message_date : draft2.date;
            return i < i2 ? 1 : i > i2 ? -1 : 0;
        }

        public /* synthetic */ int compare(Object obj, Object obj2) {
            return m829a((TL_dialog) obj, (TL_dialog) obj2);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.e.5 */
    static class DialogsTab implements Comparator<TL_dialog> {
        DialogsTab() {
        }

        public int m830a(TL_dialog tL_dialog, TL_dialog tL_dialog2) {
            return tL_dialog.unread_count == tL_dialog2.unread_count ? 0 : tL_dialog.unread_count < tL_dialog2.unread_count ? 1 : -1;
        }

        public /* synthetic */ int compare(Object obj, Object obj2) {
            return m830a((TL_dialog) obj, (TL_dialog) obj2);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.e.6 */
    class DialogsTab extends LinearLayout {
        final /* synthetic */ DialogsTab f865a;

        DialogsTab(DialogsTab dialogsTab, Context context) {
            this.f865a = dialogsTab;
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.e.7 */
    class DialogsTab implements OnLongClickOnTabListener {
        final /* synthetic */ DialogsTab f866a;

        DialogsTab(DialogsTab dialogsTab) {
            this.f866a = dialogsTab;
        }

        public void onLongClick(int i) {
            if (((TabData) this.f866a.f878d.get(i)).m2225a() == this.f866a.f876b.m2225a()) {
                this.f866a.m951b(this.f866a.f876b.m2225a());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.e.8 */
    class DialogsTab implements OnPageChangeListener {
        boolean f867a;
        int f868b;
        final /* synthetic */ DialogsTab f869c;

        DialogsTab(DialogsTab dialogsTab) {
            this.f869c = dialogsTab;
            this.f867a = false;
            this.f868b = 0;
        }

        public void onPageScrollStateChanged(int i) {
            if (i != 0 || !this.f867a) {
                return;
            }
            if (this.f869c.f879e.getCurrentItem() == this.f869c.f879e.getAdapter().getCount() - 1) {
                this.f869c.f879e.setCurrentItem(0);
            } else if (this.f869c.f879e.getCurrentItem() == 0) {
                this.f869c.f879e.setCurrentItem(this.f869c.f879e.getAdapter().getCount() - 1);
            }
        }

        public void onPageScrolled(int i, float f, int i2) {
            if (i2 == 0) {
                this.f868b++;
                if (this.f868b >= 5) {
                    this.f867a = true;
                    return;
                }
                return;
            }
            this.f868b = 0;
            this.f867a = false;
        }

        public void onPageSelected(int i) {
            this.f868b = 0;
            this.f867a = false;
            this.f869c.f876b = (TabData) this.f869c.f878d.get(i);
            this.f869c.m955e();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.e.9 */
    class DialogsTab implements OnTouchListener {
        final /* synthetic */ OnTouchListener f870a;
        final /* synthetic */ DialogsTab f871b;

        DialogsTab(DialogsTab dialogsTab, OnTouchListener onTouchListener) {
            this.f871b = dialogsTab;
            this.f870a = onTouchListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getX() > 150.0f) {
                this.f871b.f879e.onTouchEvent(motionEvent);
            }
            if (this.f870a != null) {
                this.f870a.onTouch(view, motionEvent);
            }
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.e.a */
    private class DialogsTab extends PagerAdapter implements IconTabProvider {
        final /* synthetic */ DialogsTab f872a;

        private DialogsTab(DialogsTab dialogsTab) {
            this.f872a = dialogsTab;
        }

        public void customOnDraw(Canvas canvas, int i) {
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public int getCount() {
            return this.f872a.f878d.size();
        }

        public int getPageIconResId(int i) {
            return ((TabData) this.f872a.f878d.get(i)).m2228b();
        }

        public int getPageIconSelectedResId(int i) {
            return ((TabData) this.f872a.f878d.get(i)).m2240j();
        }

        public int getPageIconUnSelectedResId(int i) {
            return ((TabData) this.f872a.f878d.get(i)).m2241k();
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View view = new View(this.f872a.f875a.getParentActivity());
            viewGroup.addView(view);
            return view;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (dataSetObserver != null) {
                super.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    public DialogsTab(DialogsActivity dialogsActivity) {
        this.f878d = new ArrayList();
        this.f875a = dialogsActivity;
        this.f877c = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
    }

    public static String m946a(int i) {
        return "tab_" + i + "_sort_type";
    }

    public static ArrayList<TL_dialog> m947a(int i, ArrayList<TL_dialog> arrayList) {
        if (ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).getInt(DialogsTab.m946a(i), 0) == 0) {
            Collections.sort(arrayList, new DialogsTab());
        } else {
            Collections.sort(arrayList, new DialogsTab());
        }
        return arrayList;
    }

    private void m951b(int i) {
        Builder builder = new Builder(this.f875a.getParentActivity());
        builder.setTitle(TabsUtil.m2258a(i));
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
        int i2 = sharedPreferences.getInt(DialogsTab.m946a(i), 0);
        List arrayList = new ArrayList();
        arrayList.add(i2 == 0 ? LocaleController.getString("SortByUnreadMessages", C0338R.string.SortByUnreadMessages) : LocaleController.getString("SortByLastMessage", C0338R.string.SortByLastMessage));
        arrayList.add(LocaleController.getString("MarkAllDialogsAsRead", C0338R.string.MarkAllDialogsAsRead));
        if (this.f876b.m2225a() == 4) {
            arrayList.add(LocaleController.getString("DeleteSelectiveContactChats", C0338R.string.DeleteSelectiveContactChats));
        } else if (this.f876b.m2225a() == 3) {
            arrayList.add(LocaleController.getString("LeaveSomeChannels", C0338R.string.LeaveSomeChannels));
        } else if (this.f876b.m2225a() == 7 || this.f876b.m2225a() == 9 || this.f876b.m2225a() == 11) {
            arrayList.add(LocaleController.getString("DeleteAndLeaveSomeGroups", C0338R.string.DeleteAndLeaveSomeGroups));
        }
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new AnonymousClass12(this, sharedPreferences, i, i2));
        this.f875a.showDialog(builder.create());
    }

    private void m955e() {
        this.f875a.dialogsAdapter = new DialogsAdapter(this.f875a.getParentActivity(), this.f876b.m2225a());
        this.f875a.dialogsAdapter.setHiddenMode(this.f875a.isHiddenMode());
        this.f875a.dialogsAdapter.setCategoryId(this.f875a.getCategoryId());
        this.f875a.listView.setAdapter(this.f875a.dialogsAdapter);
        if (this.f877c.getBoolean("last_selected_tab", false)) {
            Editor edit = this.f877c.edit();
            edit.putInt("default_tab", this.f876b.m2225a());
            edit.commit();
            MoboConstants.f1329V = this.f876b.m2225a();
        }
        TextView f = m956f();
        CharSequence string;
        switch (this.f876b.m2225a()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                string = LocaleController.getString("NoChatsHelp", C0338R.string.NoChatsHelp);
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                    string = string.replace("\n", " ");
                }
                f.setText(string);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                f.setText(LocaleController.getString("NoChannelsHelp", C0338R.string.NoChannelsHelp));
            case VideoPlayer.STATE_READY /*4*/:
                string = LocaleController.getString("NoChatsHelp", C0338R.string.NoChatsHelp);
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                    string = string.replace("\n", " ");
                }
                f.setText(string);
            case VideoPlayer.STATE_ENDED /*5*/:
                f.setText(LocaleController.getString("NoBotsHelp", C0338R.string.NoBotsHelp));
            case Method.TRACE /*6*/:
                f.setText(LocaleController.getString("NoFavoriteHelp", C0338R.string.NoFavoriteHelp));
            case Method.PATCH /*7*/:
                f.setText(LocaleController.getString("NoGroupsHelp", C0338R.string.NoGroupsHelp));
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                f.setText(LocaleController.getString("NoUnreadHelp", C0338R.string.NoUnreadHelp));
            case C0338R.styleable.PromptView_iconTint /*9*/:
                f.setText(LocaleController.getString("NoGroupsHelp", C0338R.string.NoGroupsHelp));
            case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                f.setText(LocaleController.getString("NoUnreadHelp", C0338R.string.NoUnreadHelp));
            case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                f.setText(LocaleController.getString("NoGroupsHelp", C0338R.string.NoGroupsHelp));
            case Atom.FULL_HEADER_SIZE /*12*/:
                if (MoboConstants.f1328U) {
                    f.setText(LocaleController.getString("NoCreatorAdminHelp", C0338R.string.NoCreatorAdminHelp));
                } else {
                    f.setText(LocaleController.getString("NoCreatorHelp", C0338R.string.NoCreatorHelp));
                }
            default:
        }
    }

    private TextView m956f() {
        TextView textView = new TextView(this.f875a.getParentActivity());
        View emptyView = this.f875a.listView.getEmptyView();
        if (emptyView != null && (emptyView instanceof ViewGroup)) {
            ViewGroup viewGroup = (ViewGroup) emptyView;
            if (viewGroup.getChildCount() > 1 && viewGroup.getChildAt(1) != null && (viewGroup.getChildAt(1) instanceof TextView)) {
                return (TextView) viewGroup.getChildAt(1);
            }
        }
        return textView;
    }

    private void m958g() {
        if (this.f875a != null && this.f875a.dialogsAdapter != null) {
            Builder builder = new Builder(this.f875a.getParentActivity());
            CharSequence string = LocaleController.getString("MarkAllDialogsAsReadAlert", C0338R.string.MarkAllDialogsAsReadAlert);
            if (MoboConstants.f1338e) {
                string = string + "\n\n" + LocaleController.getString("MarkAllDialogsAsReadGhostModeAlert", C0338R.string.MarkAllDialogsAsReadGhostModeAlert);
            }
            builder.setMessage(string);
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                final /* synthetic */ DialogsTab f850a;

                {
                    this.f850a = r1;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (MoboConstants.f1338e) {
                        this.f850a.f875a.changeGhostModeState();
                    }
                    Iterator it = this.f850a.f875a.dialogsAdapter.getDialogsArray().iterator();
                    while (it.hasNext()) {
                        TL_dialog tL_dialog = (TL_dialog) it.next();
                        if (tL_dialog.unread_count > 0) {
                            MessagesController.getInstance().markDialogAsRead(tL_dialog.id, ConnectionsManager.DEFAULT_DATACENTER_ID, Math.max(0, tL_dialog.top_message), tL_dialog.last_message_date, true, false);
                        }
                    }
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            this.f875a.showDialog(builder.create());
        }
    }

    private void m960h() {
        if (this.f875a != null && this.f875a.dialogsAdapter != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("dialogsType", 4);
            BaseFragment dialogSelectActivity = new DialogSelectActivity(bundle);
            dialogSelectActivity.m536a(new DialogSelectActivity() {
                final /* synthetic */ DialogsTab f857a;

                /* renamed from: com.hanista.mobogram.mobo.e.13.1 */
                class DialogsTab implements OnClickListener {
                    final /* synthetic */ List f855a;
                    final /* synthetic */ AnonymousClass13 f856b;

                    DialogsTab(AnonymousClass13 anonymousClass13, List list) {
                        this.f856b = anonymousClass13;
                        this.f855a = list;
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (Long longValue : this.f855a) {
                            MessagesController.getInstance().deleteDialog(longValue.longValue(), 0);
                        }
                        if (AndroidUtilities.isTablet()) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        }
                    }
                }

                {
                    this.f857a = r1;
                }

                public void m826a(List<Long> list) {
                    Builder builder = new Builder(this.f857a.f875a.getParentActivity());
                    builder.setMessage(LocaleController.getString("DeleteSelectedContactChatsAlert", C0338R.string.DeleteSelectedContactChatsAlert));
                    builder.setTitle(LocaleController.getString("Delete", C0338R.string.Delete));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new com.hanista.mobogram.mobo.DialogsTab.13.DialogsTab(this, list));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    this.f857a.f875a.showDialog(builder.create());
                }
            });
            this.f875a.presentFragment(dialogSelectActivity);
        }
    }

    private void m962i() {
        if (this.f875a != null && this.f875a.dialogsAdapter != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("dialogsType", 3);
            BaseFragment dialogSelectActivity = new DialogSelectActivity(bundle);
            dialogSelectActivity.m536a(new DialogsTab(this));
            this.f875a.presentFragment(dialogSelectActivity);
        }
    }

    private void m964j() {
        if (this.f875a != null && this.f875a.dialogsAdapter != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("dialogsType", this.f876b.m2225a());
            BaseFragment dialogSelectActivity = new DialogSelectActivity(bundle);
            dialogSelectActivity.m536a(new DialogsTab(this));
            this.f875a.presentFragment(dialogSelectActivity);
        }
    }

    private void m965k() {
        if (ThemeUtil.m2490b()) {
            this.f880f.setBackgroundColor(AdvanceTheme.f2508s);
            int i = AdvanceTheme.f2509t;
            if (i > 0) {
                Orientation orientation;
                switch (i) {
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                int i2 = AdvanceTheme.f2510u;
                this.f880f.setBackgroundDrawable(new GradientDrawable(orientation, new int[]{r1, i2}));
            }
        }
    }

    public TabData m966a() {
        return this.f876b;
    }

    public void m967a(long j, boolean z) {
        if (this.f881g != null && MoboConstants.f1332Y && MoboConstants.f1334a && MoboConstants.f1344k != 0) {
            int i;
            for (TabData tabData : this.f878d) {
                if (tabData.m2231c()) {
                    tabData.m2230c(0);
                    tabData.m2229b(0);
                    Iterator it = TabsUtil.m2259a(tabData.m2225a(), j, z).iterator();
                    while (it.hasNext()) {
                        TL_dialog tL_dialog = (TL_dialog) it.next();
                        i = tL_dialog.unread_count;
                        if (MessagesController.getInstance().isDialogMuted(tL_dialog.id)) {
                            if (MoboConstants.f1333Z) {
                                if (MoboConstants.aa) {
                                    tabData.m2233d(i > 0 ? 1 : 0);
                                } else {
                                    tabData.m2233d(i);
                                }
                            }
                        } else if (MoboConstants.aa) {
                            tabData.m2235e(i > 0 ? 1 : 0);
                        } else {
                            tabData.m2235e(i);
                        }
                        if (tabData.m2242l() > 999 && tabData.m2238h() > 0) {
                            break;
                        }
                    }
                }
            }
            LinearLayout tabsContainer = this.f881g.getTabsContainer();
            if (tabsContainer != null) {
                for (int i2 = 0; i2 < tabsContainer.getChildCount(); i2++) {
                    if (tabsContainer.getChildAt(i2) instanceof FrameLayout) {
                        FrameLayout frameLayout = (FrameLayout) tabsContainer.getChildAt(i2);
                        for (i = 0; i < frameLayout.getChildCount(); i++) {
                            if (frameLayout.getChildAt(i) instanceof TextView) {
                                TextView textView = (TextView) frameLayout.getChildAt(i);
                                for (TabData tabData2 : this.f878d) {
                                    if (tabData2.m2228b() == Integer.parseInt(textView.getTag() + TtmlNode.ANONYMOUS_REGION_ID)) {
                                        int l = tabData2.m2242l();
                                        if (l > 999) {
                                            textView.setText("+999");
                                        } else {
                                            textView.setText(l + TtmlNode.ANONYMOUS_REGION_ID);
                                        }
                                        Drawable gradientDrawable = new GradientDrawable();
                                        gradientDrawable.setShape(0);
                                        gradientDrawable.setCornerRadius((float) AndroidUtilities.dp(10.0f));
                                        if (tabData2.m2238h() > 0 && tabData2.m2239i() == 0) {
                                            gradientDrawable.setColor(-11613090);
                                        } else if (tabData2.m2238h() <= 0 || tabData2.m2239i() <= 0) {
                                            gradientDrawable.setColor(-3684409);
                                        } else {
                                            gradientDrawable.setColor(-10054549);
                                        }
                                        textView.setBackgroundDrawable(gradientDrawable);
                                        textView.setTextColor(-1);
                                        if (ThemeUtil.m2490b()) {
                                            int i3 = AdvanceTheme.f2466C;
                                            textView.setTextSize(1, (float) i3);
                                            textView.setPadding(AndroidUtilities.dp(i3 > 10 ? (float) (i3 - 7) : 4.0f), 0, AndroidUtilities.dp(i3 > 10 ? (float) (i3 - 7) : 4.0f), 0);
                                            int i4 = AdvanceTheme.f2467D;
                                            Drawable gradientDrawable2 = new GradientDrawable();
                                            gradientDrawable2.setShape(0);
                                            gradientDrawable2.setCornerRadius((float) AndroidUtilities.dp(32.0f));
                                            textView.setBackgroundDrawable(gradientDrawable2);
                                            textView.setTextColor(i4);
                                            if (tabData2.m2238h() > 0 && tabData2.m2239i() == 0) {
                                                textView.getBackground().setColorFilter(AdvanceTheme.f2468E, Mode.SRC_IN);
                                            } else if (tabData2.m2238h() <= 0 || tabData2.m2239i() <= 0) {
                                                textView.getBackground().setColorFilter(AdvanceTheme.f2470G, Mode.SRC_IN);
                                            } else {
                                                textView.getBackground().setColorFilter(AdvanceTheme.f2469F, Mode.SRC_IN);
                                            }
                                        }
                                        if (l > 0) {
                                            textView.setVisibility(0);
                                        } else {
                                            textView.setVisibility(8);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void m968a(Context context, FrameLayout frameLayout, RecyclerListView recyclerListView, OnTouchListener onTouchListener) {
        if (MoboConstants.f1334a) {
            this.f878d.clear();
            this.f878d.addAll(TabsUtil.m2260a(true, false));
            Collections.reverse(this.f878d);
            int i = MoboConstants.f1329V;
            for (TabData tabData : this.f878d) {
                if (tabData.m2225a() == i) {
                    this.f876b = tabData;
                    break;
                }
            }
            if (this.f876b == null) {
                this.f876b = (TabData) this.f878d.get(this.f878d.size() - 1);
            }
            this.f879e = new DialogsTab(this, context);
            this.f879e.setAdapter(new DialogsTab());
            this.f880f = new DialogsTab(this, context);
            this.f880f.setOrientation(0);
            this.f880f.setBackgroundColor(ThemeUtil.m2485a().m2289c());
            m965k();
            frameLayout.addView(this.f880f, LayoutHelper.createFrame(-1, 40.0f, MoboConstants.f1331X ? 80 : 48, 0.0f, 0.0f, 0.0f, 0.0f));
            this.f881g = new PagerSlidingTabStrip(context);
            this.f881g.setAddCounterToTabs(MoboConstants.f1332Y);
            this.f881g.setIsMainScreen(true);
            this.f881g.setShouldExpand(true);
            this.f881g.setIndicatorHeight(AndroidUtilities.dp(4.0f));
            this.f881g.setUnderlineHeight(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.f881g.setIndicatorColor(-1);
            this.f881g.setShowIndicatorAtTop(MoboConstants.f1331X);
            this.f881g.setUnderlineColor(ThemeUtil.m2485a().m2289c());
            if (ThemeUtil.m2490b()) {
                this.f881g.setUnderlineColor(AdvanceTheme.f2491b);
                this.f881g.setIndicatorColor(AdvanceTheme.f2511v);
            }
            this.f881g.setViewPager(this.f879e);
            this.f881g.setOnLongClickOnTabListener(new DialogsTab(this));
            this.f881g.setOnPageChangeListener(new DialogsTab(this));
            this.f880f.addView(this.f881g, LayoutHelper.createLinear(-1, -1));
            this.f880f.addView(new FrameLayout(context), LayoutHelper.createLinear(52, 48));
            frameLayout.addView(this.f879e, LayoutHelper.createFrame(-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            if (MoboConstants.f1335b) {
                recyclerListView.setOnTouchListener(new DialogsTab(this, onTouchListener));
                if (this.f875a.emptyView != null) {
                    this.f875a.emptyView.setOnTouchListener(new OnTouchListener() {
                        final /* synthetic */ DialogsTab f849a;

                        {
                            this.f849a = r1;
                        }

                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getX() > 150.0f) {
                                this.f849a.f879e.onTouchEvent(motionEvent);
                            }
                            return true;
                        }
                    });
                }
            }
            for (int i2 = 0; i2 < this.f878d.size(); i2++) {
                if (((TabData) this.f878d.get(i2)).m2225a() == this.f876b.m2225a()) {
                    this.f879e.setCurrentItem(i2);
                    return;
                }
            }
        }
    }

    public void m969b() {
        if (this.f880f != null) {
            this.f880f.setVisibility(8);
        }
    }

    public void m970c() {
        if (this.f880f != null) {
            this.f880f.setVisibility(0);
        }
    }

    public PagerSlidingTabStrip m971d() {
        return this.f881g;
    }
}
