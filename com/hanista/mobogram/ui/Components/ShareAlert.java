package com.hanista.mobogram.ui.Components;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.DialogObject;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.SendMessagesHelper;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.support.widget.GridLayoutManager;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemDecoration;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.State;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p007h.FavoriteUtil;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p010k.MaterialHelperUtil;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p019r.TabData;
import com.hanista.mobogram.mobo.p019r.TabsUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_exportMessageLink;
import com.hanista.mobogram.tgnet.TLRPC.TL_contact;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_exportedMessageLink;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.OnFinishOpenAnimationListener;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.DialogCell;
import com.hanista.mobogram.ui.Cells.ShareDialogCell;
import com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.IconTabProvider;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.DialogsActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class ShareAlert extends BottomSheet implements NotificationCenterDelegate {
    private Switch captionCheckBox;
    private TextView captionTextView;
    private boolean copyLinkOnEnd;
    private TabData currentTab;
    private LinearLayout doneButton;
    private TextView doneButtonBadgeTextView;
    private TextView doneButtonTextView;
    private TL_exportedMessageLink exportedMessageLink;
    private boolean forwardNoName;
    private FrameLayout frameLayout;
    private RecyclerListView gridView;
    private boolean isPublicChannel;
    private GridLayoutManager layoutManager;
    private LinearLayoutManager linearLayoutManager;
    private String linkToCopy;
    private ShareDialogsAdapter listAdapter;
    private boolean loadingLink;
    private EditText nameTextView;
    private ViewPager pager;
    private LinearLayout pagerSlidingTabStripContainer;
    private boolean proForward;
    private Switch quoteCheckBox;
    private TextView quoteTextView;
    private int scrollOffsetY;
    private ShareSearchAdapter searchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private CheckBoxSquare selectAllcheckBox;
    private HashMap<Long, TL_dialog> selectedDialogs;
    private ArrayList<MessageObject> sendingMessageObjects;
    private String sendingText;
    private View shadow;
    private Drawable shadowDrawable;
    private List<TabData> tabs;
    private int topBeforeSwitch;
    private boolean withCaption;

    public interface OnDoneListener {
        void onDone();
    }

    public interface SendDelegate {
        void send(long j, Long l);
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.10 */
    class AnonymousClass10 extends RecyclerListView {
        AnonymousClass10(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (MoboConstants.ah && MoboConstants.f1344k != 0 && motionEvent.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.14 */
    class AnonymousClass14 extends ViewPager {
        AnonymousClass14(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.15 */
    class AnonymousClass15 extends LinearLayout {
        AnonymousClass15(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.1 */
    class C14551 implements RequestDelegate {
        final /* synthetic */ Context val$context;

        /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.1.1 */
        class C14541 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C14541(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$response != null) {
                    ShareAlert.this.exportedMessageLink = (TL_exportedMessageLink) this.val$response;
                    if (ShareAlert.this.copyLinkOnEnd) {
                        ShareAlert.this.copyLink(C14551.this.val$context);
                    }
                }
                ShareAlert.this.loadingLink = false;
            }
        }

        C14551(Context context) {
            this.val$context = context;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C14541(tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.2 */
    class C14562 extends FrameLayout {
        private boolean ignoreLayout;

        C14562(Context context) {
            super(context);
            this.ignoreLayout = false;
        }

        protected void onDraw(Canvas canvas) {
            ShareAlert.this.shadowDrawable.setBounds(0, ShareAlert.this.scrollOffsetY - ShareAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
            ShareAlert.this.shadowDrawable.draw(canvas);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0 || ShareAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) ShareAlert.this.scrollOffsetY)) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            ShareAlert.this.dismiss();
            return true;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ShareAlert.this.updateLayout();
        }

        protected void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i2);
            if (VERSION.SDK_INT >= 21) {
                size -= AndroidUtilities.statusBarHeight;
            }
            int access$700 = ShareAlert.backgroundPaddingTop + ((Math.max(3, (int) Math.ceil((double) (((float) Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount())) / 4.0f))) * AndroidUtilities.dp(100.0f)) + AndroidUtilities.dp(48.0f));
            int dp = access$700 < size ? 0 : (size - ((size / 5) * 3)) + AndroidUtilities.dp(8.0f);
            if (ShareAlert.this.gridView.getPaddingTop() != dp) {
                this.ignoreLayout = true;
                ShareAlert.this.gridView.setPadding(0, dp, 0, AndroidUtilities.dp(8.0f));
                this.ignoreLayout = false;
            }
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(access$700, size), C0700C.ENCODING_PCM_32BIT));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return !ShareAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.3 */
    class C14573 implements OnTouchListener {
        C14573() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.4 */
    class C14584 implements OnClickListener {
        final /* synthetic */ ArrayList val$messageObjects;
        final /* synthetic */ OnDoneListener val$onDoneListener;
        final /* synthetic */ boolean val$proForward;
        final /* synthetic */ SendDelegate val$sendDelegate;

        C14584(SendDelegate sendDelegate, boolean z, ArrayList arrayList, OnDoneListener onDoneListener) {
            this.val$sendDelegate = sendDelegate;
            this.val$proForward = z;
            this.val$messageObjects = arrayList;
            this.val$onDoneListener = onDoneListener;
        }

        public void onClick(View view) {
            if (!ShareAlert.this.selectedDialogs.isEmpty() || (!ShareAlert.this.isPublicChannel && ShareAlert.this.linkToCopy == null)) {
                if (ShareAlert.this.sendingMessageObjects != null && !ShareAlert.this.sendingMessageObjects.isEmpty()) {
                    for (Entry entry : ShareAlert.this.selectedDialogs.entrySet()) {
                        if (this.val$sendDelegate != null) {
                            this.val$sendDelegate.send(((Long) entry.getKey()).longValue(), (Long) entry.getKey());
                        } else if (this.val$proForward) {
                            r4 = this.val$messageObjects.iterator();
                            while (r4.hasNext()) {
                                SendMessagesHelper.getInstance().processProForward((MessageObject) r4.next(), ((Long) entry.getKey()).longValue(), ShareAlert.this.withCaption);
                            }
                        } else if (ShareAlert.this.forwardNoName) {
                            r4 = this.val$messageObjects.iterator();
                            while (r4.hasNext()) {
                                SendMessagesHelper.getInstance().processForwardFromMyName((MessageObject) r4.next(), ((Long) entry.getKey()).longValue(), ShareAlert.this.withCaption);
                            }
                        } else {
                            SendMessagesHelper.getInstance().sendMessage(ShareAlert.this.sendingMessageObjects, ((Long) entry.getKey()).longValue());
                        }
                    }
                } else if (ShareAlert.this.sendingText != null) {
                    for (Entry key : ShareAlert.this.selectedDialogs.entrySet()) {
                        SendMessagesHelper.getInstance().sendMessage(ShareAlert.this.sendingText, ((Long) key.getKey()).longValue(), null, null, true, null, null, null);
                    }
                } else if (this.val$sendDelegate != null) {
                    for (Entry entry2 : ShareAlert.this.selectedDialogs.entrySet()) {
                        this.val$sendDelegate.send(((Long) entry2.getKey()).longValue(), (Long) entry2.getKey());
                    }
                }
                ShareAlert.this.dismiss();
                if (this.val$onDoneListener != null) {
                    this.val$onDoneListener.onDone();
                    return;
                }
                return;
            }
            if (ShareAlert.this.linkToCopy == null && ShareAlert.this.loadingLink) {
                ShareAlert.this.copyLinkOnEnd = true;
                Toast.makeText(ShareAlert.this.getContext(), LocaleController.getString("Loading", C0338R.string.Loading), 0).show();
            } else {
                ShareAlert.this.copyLink(ShareAlert.this.getContext());
            }
            ShareAlert.this.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.5 */
    class C14595 implements OnClickListener {
        C14595() {
        }

        public void onClick(View view) {
            int i = 0;
            if (ShareAlert.this.selectAllcheckBox.isChecked()) {
                ShareAlert.this.selectedDialogs.clear();
                ShareAlert.this.selectAllcheckBox.setChecked(false, true);
            } else {
                while (i < ShareAlert.this.gridView.getAdapter().getItemCount()) {
                    TL_dialog item = ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter ? ShareAlert.this.listAdapter.getItem(i) : ShareAlert.this.searchAdapter.getItem(i);
                    ShareAlert.this.selectedDialogs.put(Long.valueOf(item.id), item);
                    i++;
                }
                ShareAlert.this.selectAllcheckBox.setChecked(true, true);
            }
            if (ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter) {
                ShareAlert.this.listAdapter.notifyDataSetChanged();
            } else {
                ShareAlert.this.searchAdapter.notifyDataSetChanged();
            }
            ShareAlert.this.updateSelectedCount();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.6 */
    class C14606 implements OnCheckedChangeListener {
        C14606() {
        }

        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            ShareAlert.this.withCaption = z;
            if (!ShareAlert.this.withCaption && !ShareAlert.this.forwardNoName) {
                ShareAlert.this.setQuoteCheck(false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.7 */
    class C14617 implements OnClickListener {
        final /* synthetic */ Context val$context;

        C14617(Context context) {
            this.val$context = context;
        }

        public void onClick(View view) {
            ((Switch) view).setChecked(false);
            Toast.makeText(this.val$context, LocaleController.getString("ProForwardQuoteNotAvailable", C0338R.string.ProForwardQuoteNotAvailable), 0).show();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.8 */
    class C14628 implements OnCheckedChangeListener {
        C14628() {
        }

        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            ShareAlert.this.forwardNoName = !z;
            if (!(ShareAlert.this.withCaption || ShareAlert.this.forwardNoName)) {
                ShareAlert.this.captionCheckBox.setChecked(true);
            }
            ShareAlert.this.setCheckColor();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.9 */
    class C14639 implements TextWatcher {
        C14639() {
        }

        public void afterTextChanged(Editable editable) {
            String obj = ShareAlert.this.nameTextView.getText().toString();
            if (obj.length() != 0) {
                if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                    ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                    ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
                    ShareAlert.this.searchAdapter.notifyDataSetChanged();
                }
                if (ShareAlert.this.searchEmptyView != null) {
                    ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
                }
            } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                int access$2400 = ShareAlert.this.getCurrentTop();
                ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", C0338R.string.NoChats));
                ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
                ShareAlert.this.listAdapter.notifyDataSetChanged();
                if (access$2400 > 0) {
                    ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -access$2400);
                }
            }
            if (ShareAlert.this.searchAdapter != null) {
                ShareAlert.this.searchAdapter.searchDialogs(obj);
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    private class Holder extends ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    private class ShareDialogsAdapter extends Adapter {
        private Context context;
        private int currentCount;
        private ArrayList<TL_dialog> dialogs;

        public ShareDialogsAdapter(Context context) {
            this.dialogs = new ArrayList();
            this.context = context;
            fetchDialogs();
        }

        private ArrayList<TL_dialog> getDialogs() {
            if (!MoboConstants.ah) {
                return (HiddenConfig.f1402e || MoboConstants.f1312E) ? MessagesController.getInstance().dialogsServerOnly : HiddenConfig.m1393a(MessagesController.getInstance().dialogsServerOnly, false);
            } else {
                ArrayList<TL_dialog> arrayList = new ArrayList();
                ShareAlert shareAlert = ShareAlert.this;
                TabData tabData = (ShareAlert.this.tabs == null || ShareAlert.this.pager == null) ? null : (TabData) ShareAlert.this.tabs.get(ShareAlert.this.pager.getCurrentItem());
                shareAlert.currentTab = tabData;
                if (ShareAlert.this.currentTab == null) {
                    return arrayList;
                }
                if (ShareAlert.this.currentTab.m2225a() == 13) {
                    Iterator it = ContactsController.getInstance().contacts.iterator();
                    while (it.hasNext()) {
                        TL_contact tL_contact = (TL_contact) it.next();
                        TL_dialog tL_dialog = new TL_dialog();
                        tL_dialog.id = (long) tL_contact.user_id;
                        arrayList.add(tL_dialog);
                    }
                    return arrayList;
                }
                for (int i = 0; i < MessagesController.getInstance().dialogsServerOnly.size(); i++) {
                    TL_dialog tL_dialog2 = (TL_dialog) MessagesController.getInstance().dialogsServerOnly.get(i);
                    if (ShareAlert.this.currentTab.m2225a() == 0) {
                        arrayList.add(tL_dialog2);
                    } else if (ShareAlert.this.currentTab.m2225a() == 6) {
                        if (FavoriteUtil.m1142b(Long.valueOf(tL_dialog2.id))) {
                            arrayList.add(tL_dialog2);
                        }
                    } else if (ShareAlert.this.currentTab.m2225a() == 4) {
                        r4 = MessagesController.getInstance().getUser(Integer.valueOf((int) tL_dialog2.id));
                        if (!(r4 == null || r4.bot)) {
                            arrayList.add(tL_dialog2);
                        }
                    } else if (ShareAlert.this.currentTab.m2225a() == 7) {
                        if ((tL_dialog2 instanceof TL_dialog) && tL_dialog2.id < 0 && !DialogObject.isChannel(tL_dialog2)) {
                            arrayList.add(tL_dialog2);
                        }
                    } else if (ShareAlert.this.currentTab.m2225a() == 9) {
                        r4 = MessagesController.getInstance().getChat(Integer.valueOf(-((int) tL_dialog2.id)));
                        if (r4 != null && ChatObject.isChannel(r4) && r4.megagroup) {
                            arrayList.add(tL_dialog2);
                        }
                    } else if (ShareAlert.this.currentTab.m2225a() == 3) {
                        r4 = MessagesController.getInstance().getChat(Integer.valueOf(-((int) tL_dialog2.id)));
                        if (!(r4 == null || !ChatObject.isChannel(r4) || r4.megagroup)) {
                            arrayList.add(tL_dialog2);
                        }
                    } else if (ShareAlert.this.currentTab.m2225a() == 5) {
                        r4 = MessagesController.getInstance().getUser(Integer.valueOf((int) tL_dialog2.id));
                        if (r4 != null && r4.bot) {
                            arrayList.add(tL_dialog2);
                        }
                    } else if (ShareAlert.this.currentTab.m2225a() == 11) {
                        if (!(tL_dialog2 instanceof TL_dialog) || tL_dialog2.id >= 0 || DialogObject.isChannel(tL_dialog2)) {
                            r4 = MessagesController.getInstance().getChat(Integer.valueOf(-((int) tL_dialog2.id)));
                            if (r4 != null && ChatObject.isChannel(r4) && r4.megagroup) {
                                arrayList.add(tL_dialog2);
                            }
                        } else {
                            arrayList.add(tL_dialog2);
                        }
                    } else if (ShareAlert.this.currentTab.m2225a() == 12) {
                        r4 = MessagesController.getInstance().getChat(Integer.valueOf(-((int) tL_dialog2.id)));
                        if (r4 != null && (r4.creator || (MoboConstants.f1328U && r4.editor))) {
                            arrayList.add(tL_dialog2);
                        }
                    }
                }
                return (HiddenConfig.f1402e || MoboConstants.f1312E) ? arrayList : HiddenConfig.m1393a(arrayList, false);
            }
        }

        public void fetchDialogs() {
            this.dialogs.clear();
            ArrayList dialogs = getDialogs();
            for (int i = 0; i < dialogs.size(); i++) {
                TL_dialog tL_dialog = (TL_dialog) dialogs.get(i);
                int i2 = (int) tL_dialog.id;
                int i3 = (int) (tL_dialog.id >> 32);
                if (!(i2 == 0 || i3 == 1)) {
                    if (i2 > 0) {
                        this.dialogs.add(tL_dialog);
                    } else {
                        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-i2));
                        if (!(chat == null || ChatObject.isNotInChat(chat) || (ChatObject.isChannel(chat) && !chat.creator && !chat.editor && !chat.megagroup))) {
                            this.dialogs.add(tL_dialog);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

        public TL_dialog getItem(int i) {
            return (i < 0 || i >= this.dialogs.size()) ? null : (TL_dialog) this.dialogs.get(i);
        }

        public int getItemCount() {
            return this.dialogs.size();
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            if (MoboConstants.aj) {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                dialogCell.useSeparator = i != getItemCount() + -1;
                TL_dialog item = getItem(i);
                if (ShareAlert.this.selectedDialogs != null) {
                    dialogCell.setDialogSelected(ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(item.id)));
                }
                if (ShareAlert.this.currentTab != null && ShareAlert.this.currentTab.m2225a() == 13) {
                    dialogCell.clear();
                }
                dialogCell.setDialog(item, i, ShareAlert.this.currentTab == null ? 1 : ShareAlert.this.currentTab.m2225a());
                if (HiddenConfig.f1402e || MoboConstants.f1312E) {
                    z = true;
                }
                dialogCell.setHiddenMode(z);
                return;
            }
            ShareDialogCell shareDialogCell = (ShareDialogCell) viewHolder.itemView;
            TL_dialog item2 = getItem(i);
            shareDialogCell.setDialog((int) item2.id, ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(item2.id)), null);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View dialogCell = MoboConstants.aj ? new DialogCell(this.context) : new ShareDialogCell(this.context);
            dialogCell.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            return new Holder(dialogCell);
        }
    }

    public class ShareSearchAdapter extends Adapter {
        private Context context;
        private int lastReqId;
        private int lastSearchId;
        private String lastSearchText;
        private int reqId;
        private ArrayList<DialogSearchResult> searchResult;
        private Timer searchTimer;

        /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.ShareSearchAdapter.1 */
        class C14651 implements Runnable {
            final /* synthetic */ String val$query;
            final /* synthetic */ int val$searchId;

            /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.ShareSearchAdapter.1.1 */
            class C14641 implements Comparator<DialogSearchResult> {
                C14641() {
                }

                public int compare(DialogSearchResult dialogSearchResult, DialogSearchResult dialogSearchResult2) {
                    return dialogSearchResult.date < dialogSearchResult2.date ? 1 : dialogSearchResult.date > dialogSearchResult2.date ? -1 : 0;
                }
            }

            C14651(String str, int i) {
                this.val$query = str;
                this.val$searchId = i;
            }

            public void run() {
                try {
                    String toLowerCase = this.val$query.trim().toLowerCase();
                    if (toLowerCase.length() == 0) {
                        ShareSearchAdapter.this.lastSearchId = -1;
                        ShareSearchAdapter.this.updateSearchResults(new ArrayList(), ShareSearchAdapter.this.lastSearchId);
                        return;
                    }
                    int i;
                    String stringValue;
                    String str;
                    int lastIndexOf;
                    String substring;
                    int i2;
                    AbstractSerializedData byteBufferValue;
                    DialogSearchResult dialogSearchResult;
                    String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
                    String str2 = (toLowerCase.equals(translitString) || translitString.length() == 0) ? null : translitString;
                    String[] strArr = new String[((str2 != null ? 1 : 0) + 1)];
                    strArr[0] = toLowerCase;
                    if (str2 != null) {
                        strArr[1] = str2;
                    }
                    Iterable arrayList = new ArrayList();
                    Iterable arrayList2 = new ArrayList();
                    int i3 = 0;
                    HashMap hashMap = new HashMap();
                    SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400", new Object[0]);
                    while (queryFinalized.next()) {
                        long longValue = queryFinalized.longValue(0);
                        DialogSearchResult dialogSearchResult2 = new DialogSearchResult(null);
                        dialogSearchResult2.date = queryFinalized.intValue(1);
                        hashMap.put(Long.valueOf(longValue), dialogSearchResult2);
                        i = (int) longValue;
                        int i4 = (int) (longValue >> 32);
                        if (!(i == 0 || i4 == 1)) {
                            if (i > 0) {
                                if (!arrayList.contains(Integer.valueOf(i))) {
                                    arrayList.add(Integer.valueOf(i));
                                }
                            } else if (!arrayList2.contains(Integer.valueOf(-i))) {
                                arrayList2.add(Integer.valueOf(-i));
                            }
                        }
                    }
                    queryFinalized.dispose();
                    if (!arrayList.isEmpty()) {
                        SQLiteCursor queryFinalized2 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
                        while (queryFinalized2.next()) {
                            stringValue = queryFinalized2.stringValue(2);
                            translitString = LocaleController.getInstance().getTranslitString(stringValue);
                            str = stringValue.equals(translitString) ? null : translitString;
                            lastIndexOf = stringValue.lastIndexOf(";;;");
                            substring = lastIndexOf != -1 ? stringValue.substring(lastIndexOf + 3) : null;
                            int length = strArr.length;
                            i = 0;
                            i2 = 0;
                            while (i < length) {
                                String str3 = strArr[i];
                                lastIndexOf = (stringValue.startsWith(str3) || stringValue.contains(" " + str3) || (str != null && (str.startsWith(str3) || str.contains(" " + str3)))) ? 1 : (substring == null || !substring.startsWith(str3)) ? i2 : 2;
                                if (lastIndexOf != 0) {
                                    byteBufferValue = queryFinalized2.byteBufferValue(0);
                                    if (byteBufferValue != null) {
                                        TLObject TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                        byteBufferValue.reuse();
                                        dialogSearchResult = (DialogSearchResult) hashMap.get(Long.valueOf((long) TLdeserialize.id));
                                        if (TLdeserialize.status != null) {
                                            TLdeserialize.status.expires = queryFinalized2.intValue(1);
                                        }
                                        if (lastIndexOf == 1) {
                                            dialogSearchResult.name = AndroidUtilities.generateSearchName(TLdeserialize.first_name, TLdeserialize.last_name, str3);
                                        } else {
                                            dialogSearchResult.name = AndroidUtilities.generateSearchName("@" + TLdeserialize.username, null, "@" + str3);
                                        }
                                        dialogSearchResult.object = TLdeserialize;
                                        dialogSearchResult.dialog.id = (long) TLdeserialize.id;
                                        i2 = i3 + 1;
                                        i3 = i2;
                                    }
                                    i2 = i3;
                                    i3 = i2;
                                } else {
                                    i++;
                                    i2 = lastIndexOf;
                                }
                            }
                            i2 = i3;
                            i3 = i2;
                        }
                        queryFinalized2.dispose();
                    }
                    if (!arrayList2.isEmpty()) {
                        SQLiteCursor queryFinalized3 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[]{TextUtils.join(",", arrayList2)}), new Object[0]);
                        while (queryFinalized3.next()) {
                            substring = queryFinalized3.stringValue(1);
                            translitString = LocaleController.getInstance().getTranslitString(substring);
                            if (substring.equals(translitString)) {
                                translitString = null;
                            }
                            lastIndexOf = 0;
                            while (lastIndexOf < strArr.length) {
                                str = strArr[lastIndexOf];
                                if (substring.startsWith(str) || substring.contains(" " + str) || (r0 != null && (r0.startsWith(str) || r0.contains(" " + str)))) {
                                    byteBufferValue = queryFinalized3.byteBufferValue(0);
                                    if (byteBufferValue != null) {
                                        Chat TLdeserialize2 = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                        byteBufferValue.reuse();
                                        if (TLdeserialize2 == null || ChatObject.isNotInChat(TLdeserialize2) || !(!ChatObject.isChannel(TLdeserialize2) || TLdeserialize2.creator || TLdeserialize2.editor || TLdeserialize2.megagroup)) {
                                            i2 = i3;
                                        } else {
                                            dialogSearchResult = (DialogSearchResult) hashMap.get(Long.valueOf(-((long) TLdeserialize2.id)));
                                            dialogSearchResult.name = AndroidUtilities.generateSearchName(TLdeserialize2.title, null, str);
                                            dialogSearchResult.object = TLdeserialize2;
                                            dialogSearchResult.dialog.id = (long) (-TLdeserialize2.id);
                                            i2 = i3 + 1;
                                        }
                                        i3 = i2;
                                    }
                                } else {
                                    lastIndexOf++;
                                }
                            }
                        }
                        queryFinalized3.dispose();
                    }
                    Object arrayList3 = new ArrayList(i3);
                    for (DialogSearchResult dialogSearchResult3 : hashMap.values()) {
                        if (!(dialogSearchResult3.object == null || dialogSearchResult3.name == null)) {
                            arrayList3.add(dialogSearchResult3);
                        }
                    }
                    SQLiteCursor queryFinalized4 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
                    while (queryFinalized4.next()) {
                        if (!hashMap.containsKey(Long.valueOf((long) queryFinalized4.intValue(3)))) {
                            String stringValue2 = queryFinalized4.stringValue(2);
                            translitString = LocaleController.getInstance().getTranslitString(stringValue2);
                            String str4 = stringValue2.equals(translitString) ? null : translitString;
                            i3 = stringValue2.lastIndexOf(";;;");
                            toLowerCase = i3 != -1 ? stringValue2.substring(i3 + 3) : null;
                            int length2 = strArr.length;
                            Object obj = null;
                            i3 = 0;
                            while (i3 < length2) {
                                stringValue = strArr[i3];
                                if (stringValue2.startsWith(stringValue) || stringValue2.contains(" " + stringValue) || (str4 != null && (str4.startsWith(stringValue) || str4.contains(" " + stringValue)))) {
                                    obj = 1;
                                } else if (toLowerCase != null && toLowerCase.startsWith(stringValue)) {
                                    obj = 2;
                                }
                                if (i2 != null) {
                                    AbstractSerializedData byteBufferValue2 = queryFinalized4.byteBufferValue(0);
                                    if (byteBufferValue2 != null) {
                                        TLObject TLdeserialize3 = User.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                                        byteBufferValue2.reuse();
                                        DialogSearchResult dialogSearchResult4 = new DialogSearchResult(null);
                                        if (TLdeserialize3.status != null) {
                                            TLdeserialize3.status.expires = queryFinalized4.intValue(1);
                                        }
                                        dialogSearchResult4.dialog.id = (long) TLdeserialize3.id;
                                        dialogSearchResult4.object = TLdeserialize3;
                                        if (i2 == 1) {
                                            dialogSearchResult4.name = AndroidUtilities.generateSearchName(TLdeserialize3.first_name, TLdeserialize3.last_name, stringValue);
                                        } else {
                                            dialogSearchResult4.name = AndroidUtilities.generateSearchName("@" + TLdeserialize3.username, null, "@" + stringValue);
                                        }
                                        arrayList3.add(dialogSearchResult4);
                                    }
                                } else {
                                    i3++;
                                }
                            }
                        }
                    }
                    queryFinalized4.dispose();
                    Collections.sort(arrayList3, new C14641());
                    ShareSearchAdapter.this.updateSearchResults(arrayList3, this.val$searchId);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.ShareSearchAdapter.2 */
        class C14662 implements Runnable {
            final /* synthetic */ ArrayList val$result;
            final /* synthetic */ int val$searchId;

            C14662(int i, ArrayList arrayList) {
                this.val$searchId = i;
                this.val$result = arrayList;
            }

            public void run() {
                boolean z = true;
                if (this.val$searchId == ShareSearchAdapter.this.lastSearchId) {
                    for (int i = 0; i < this.val$result.size(); i++) {
                        DialogSearchResult dialogSearchResult = (DialogSearchResult) this.val$result.get(i);
                        if (dialogSearchResult.object instanceof User) {
                            MessagesController.getInstance().putUser((User) dialogSearchResult.object, true);
                        } else if (dialogSearchResult.object instanceof Chat) {
                            MessagesController.getInstance().putChat((Chat) dialogSearchResult.object, true);
                        }
                    }
                    boolean z2 = !ShareSearchAdapter.this.searchResult.isEmpty() && this.val$result.isEmpty();
                    if (!(ShareSearchAdapter.this.searchResult.isEmpty() && this.val$result.isEmpty())) {
                        z = false;
                    }
                    if (z2) {
                        ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                    }
                    ShareSearchAdapter.this.searchResult = this.val$result;
                    ShareSearchAdapter.this.notifyDataSetChanged();
                    if (!z && !z2 && ShareAlert.this.topBeforeSwitch > 0) {
                        if (MoboConstants.aj) {
                            ShareAlert.this.linearLayoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
                        } else {
                            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
                        }
                        ShareAlert.this.topBeforeSwitch = NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
                    }
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.ShareAlert.ShareSearchAdapter.3 */
        class C14673 extends TimerTask {
            final /* synthetic */ String val$query;
            final /* synthetic */ int val$searchId;

            C14673(String str, int i) {
                this.val$query = str;
                this.val$searchId = i;
            }

            public void run() {
                try {
                    cancel();
                    ShareSearchAdapter.this.searchTimer.cancel();
                    ShareSearchAdapter.this.searchTimer = null;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                ShareSearchAdapter.this.searchDialogsInternal(this.val$query, this.val$searchId);
            }
        }

        private class DialogSearchResult {
            public int date;
            public TL_dialog dialog;
            public CharSequence name;
            public TLObject object;

            private DialogSearchResult() {
                this.dialog = new TL_dialog();
            }
        }

        public ShareSearchAdapter(Context context) {
            this.searchResult = new ArrayList();
            this.reqId = 0;
            this.lastSearchId = 0;
            this.context = context;
        }

        private void searchDialogsInternal(String str, int i) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C14651(str, i));
        }

        private void updateSearchResults(ArrayList<DialogSearchResult> arrayList, int i) {
            AndroidUtilities.runOnUIThread(new C14662(i, arrayList));
        }

        public TL_dialog getItem(int i) {
            return ((DialogSearchResult) this.searchResult.get(i)).dialog;
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            DialogSearchResult dialogSearchResult = (DialogSearchResult) this.searchResult.get(i);
            ((ShareDialogCell) viewHolder.itemView).setDialog((int) dialogSearchResult.dialog.id, ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(dialogSearchResult.dialog.id)), dialogSearchResult.name);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View shareDialogCell = new ShareDialogCell(this.context);
            shareDialogCell.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            return new Holder(shareDialogCell);
        }

        public void searchDialogs(String str) {
            if (str == null || this.lastSearchText == null || !str.equals(this.lastSearchText)) {
                this.lastSearchText = str;
                try {
                    if (this.searchTimer != null) {
                        this.searchTimer.cancel();
                        this.searchTimer = null;
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                if (str == null || str.length() == 0) {
                    this.searchResult.clear();
                    ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                    notifyDataSetChanged();
                    return;
                }
                int i = this.lastSearchId + 1;
                this.lastSearchId = i;
                this.searchTimer = new Timer();
                this.searchTimer.schedule(new C14673(str, i), 200, 300);
            }
        }
    }

    private class TabsPagesAdapter extends PagerAdapter implements IconTabProvider {
        private TabsPagesAdapter() {
        }

        public void customOnDraw(Canvas canvas, int i) {
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public int getCount() {
            return ShareAlert.this.tabs.size();
        }

        public int getPageIconResId(int i) {
            return ((TabData) ShareAlert.this.tabs.get(i)).m2236f();
        }

        public int getPageIconSelectedResId(int i) {
            return ((TabData) ShareAlert.this.tabs.get(i)).m2240j();
        }

        public int getPageIconUnSelectedResId(int i) {
            return ((TabData) ShareAlert.this.tabs.get(i)).m2241k();
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View view = new View(ShareAlert.this.getContext());
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

    public ShareAlert(Context context, MessageObject messageObject, String str, boolean z, String str2) {
        this(context, new ArrayList(Arrays.asList(new MessageObject[]{messageObject})), false, false, z, null, null, str, str2);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> arrayList) {
        this(context, arrayList, false, false, false, null);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> arrayList, boolean z) {
        this(context, arrayList, z, false, false, null);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> arrayList, boolean z, boolean z2, boolean z3, OnDoneListener onDoneListener) {
        this(context, arrayList, z, z2, z3, onDoneListener, null, null, null);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> arrayList, boolean z, boolean z2, boolean z3, OnDoneListener onDoneListener, SendDelegate sendDelegate) {
        this(context, arrayList, z, z2, z3, onDoneListener, sendDelegate, null, null);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> arrayList, boolean z, boolean z2, boolean z3, OnDoneListener onDoneListener, SendDelegate sendDelegate, String str, String str2) {
        super(context, true);
        this.selectedDialogs = new HashMap();
        this.tabs = new ArrayList();
        this.shadowDrawable = context.getResources().getDrawable(C0338R.drawable.sheet_shadow);
        this.linkToCopy = str2;
        this.sendingMessageObjects = arrayList;
        this.searchAdapter = new ShareSearchAdapter(context);
        this.isPublicChannel = z3;
        this.sendingText = str;
        this.proForward = z2;
        this.forwardNoName = z;
        if (z3) {
            this.loadingLink = true;
            TLObject tL_channels_exportMessageLink = new TL_channels_exportMessageLink();
            tL_channels_exportMessageLink.id = ((MessageObject) arrayList.get(0)).getId();
            tL_channels_exportMessageLink.channel = MessagesController.getInputChannel(((MessageObject) arrayList.get(0)).messageOwner.to_id.channel_id);
            ConnectionsManager.getInstance().sendRequest(tL_channels_exportMessageLink, new C14551(context));
        }
        this.containerView = new C14562(context);
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.frameLayout = new FrameLayout(context);
        this.frameLayout.setBackgroundColor(-1);
        this.frameLayout.setOnTouchListener(new C14573());
        this.doneButton = new LinearLayout(context);
        this.doneButton.setOrientation(0);
        this.doneButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR, false));
        this.doneButton.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.frameLayout.addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        this.doneButton.setOnClickListener(new C14584(sendDelegate, z2, arrayList, onDoneListener));
        this.doneButtonBadgeTextView = new TextView(context);
        this.doneButtonBadgeTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        this.doneButtonBadgeTextView.setTextColor(-1);
        this.doneButtonBadgeTextView.setGravity(17);
        this.doneButtonBadgeTextView.setBackgroundResource(C0338R.drawable.bluecounter);
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
        this.doneButtonTextView = new TextView(context);
        this.doneButtonTextView.setTextSize(1, 14.0f);
        this.doneButtonTextView.setGravity(17);
        this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.doneButtonTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
        this.selectAllcheckBox = new CheckBoxSquare(context);
        this.selectAllcheckBox.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        this.selectAllcheckBox.setOnClickListener(new C14595());
        this.frameLayout.addView(this.selectAllcheckBox, LayoutHelper.createFrame(18, 18.0f, 19, 10.0f, 0.0f, 0.0f, 0.0f));
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        this.quoteTextView = new TextView(context);
        this.quoteTextView.setTextSize(1, 12.0f);
        this.quoteTextView.setTextColor(ThemeUtil.m2485a().m2289c());
        this.quoteTextView.setGravity(17);
        this.quoteTextView.setText(LocaleController.getString("Quote", C0338R.string.Quote));
        this.quoteTextView.setSingleLine(true);
        this.quoteTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.quoteTextView.setTypeface(FontUtil.m1176a().m1160c());
        linearLayout.addView(this.quoteTextView, LayoutHelper.createLinear(-1, -2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 16));
        this.quoteCheckBox = new Switch(context);
        this.quoteCheckBox.setDuplicateParentStateEnabled(false);
        this.quoteCheckBox.setFocusable(false);
        this.quoteCheckBox.setFocusableInTouchMode(false);
        this.quoteCheckBox.setClickable(true);
        linearLayout.addView(this.quoteCheckBox, LayoutHelper.createLinear(-1, -2, 1));
        this.frameLayout.addView(linearLayout, LayoutHelper.createFrame(48, 48.0f, 19, 33.0f, 0.0f, 0.0f, 0.0f));
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        this.captionTextView = new TextView(context);
        this.captionTextView.setTextSize(1, 12.0f);
        this.captionTextView.setTextColor(ThemeUtil.m2485a().m2289c());
        this.captionTextView.setGravity(17);
        this.captionTextView.setText(LocaleController.getString("Caption", C0338R.string.Caption));
        this.captionTextView.setSingleLine(true);
        this.captionTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.captionTextView.setTypeface(FontUtil.m1176a().m1160c());
        setQuoteCheck(!this.forwardNoName);
        linearLayout.addView(this.captionTextView, LayoutHelper.createLinear(-1, -2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 16));
        this.captionCheckBox = new Switch(context);
        this.captionCheckBox.setDuplicateParentStateEnabled(false);
        this.captionCheckBox.setFocusable(false);
        this.captionCheckBox.setFocusableInTouchMode(false);
        this.captionCheckBox.setClickable(true);
        this.captionCheckBox.setOnCheckedChangeListener(new C14606());
        Switch switchR = this.captionCheckBox;
        boolean z4 = (MoboConstants.f1343j && this.forwardNoName) ? false : true;
        switchR.setChecked(z4);
        linearLayout.addView(this.captionCheckBox, LayoutHelper.createLinear(-1, -2, 1));
        this.frameLayout.addView(linearLayout, LayoutHelper.createFrame(48, 48.0f, 19, 81.0f, 0.0f, 0.0f, 0.0f));
        if (z2) {
            setQuoteCheck(false);
            this.quoteCheckBox.setOnClickListener(new C14617(context));
        } else {
            this.quoteCheckBox.setOnCheckedChangeListener(new C14628());
        }
        linearLayout = new ImageView(context);
        linearLayout.setImageResource(C0338R.drawable.search_share);
        linearLayout.setScaleType(ScaleType.CENTER);
        linearLayout.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        this.frameLayout.addView(linearLayout, LayoutHelper.createFrame(48, 48.0f, 19, 130.0f, 2.0f, 0.0f, 0.0f));
        this.nameTextView = new EditText(context);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.nameTextView.setHint(LocaleController.getString("ShareSendTo", C0338R.string.ShareSendTo));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(19);
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setBackgroundDrawable(null);
        this.nameTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.nameTextView.setImeOptions(268435456);
        this.nameTextView.setInputType(16385);
        AndroidUtilities.clearCursorDrawable(this.nameTextView);
        this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.bf;
            EditText editText = this.nameTextView;
            if (i == Theme.ATTACH_SHEET_TEXT_COLOR) {
                i = Theme.STICKERS_SHEET_TITLE_TEXT_COLOR;
            }
            editText.setTextColor(i);
        }
        this.frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 179.0f, 2.0f, 96.0f, 0.0f));
        this.nameTextView.addTextChangedListener(new C14639());
        this.gridView = new AnonymousClass10(context);
        this.gridView.setTag(Integer.valueOf(13));
        this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        this.gridView.setClipToPadding(false);
        this.layoutManager = new GridLayoutManager(getContext(), 4);
        this.linearLayoutManager = new LinearLayoutManager(getContext());
        if (MoboConstants.aj) {
            this.gridView.setLayoutManager(this.linearLayoutManager);
            this.layoutManager.setOrientation(1);
        } else {
            this.gridView.setLayoutManager(this.layoutManager);
        }
        this.gridView.setHorizontalScrollBarEnabled(false);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new ItemDecoration() {
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                int i = 0;
                Holder holder = (Holder) recyclerView.getChildViewHolder(view);
                if (holder != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    rect.left = adapterPosition % 4 == 0 ? 0 : AndroidUtilities.dp(4.0f);
                    if (adapterPosition % 4 != 3) {
                        i = AndroidUtilities.dp(4.0f);
                    }
                    rect.right = i;
                    return;
                }
                rect.left = AndroidUtilities.dp(4.0f);
                rect.right = AndroidUtilities.dp(4.0f);
            }
        });
        ViewGroup viewGroup = this.containerView;
        View view = this.gridView;
        float f = (!MoboConstants.ah || MoboConstants.f1344k == 0) ? 48.0f : 96.0f;
        viewGroup.addView(view, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, f, 0.0f, 0.0f));
        RecyclerListView recyclerListView = this.gridView;
        Adapter shareDialogsAdapter = new ShareDialogsAdapter(context);
        this.listAdapter = shareDialogsAdapter;
        recyclerListView.setAdapter(shareDialogsAdapter);
        this.gridView.setGlowColor(-657673);
        if (ThemeUtil.m2490b()) {
            int i2 = AdvanceTheme.bj;
            this.gridView.setGlowColor(i2 != -1 ? i2 : -657673);
            this.frameLayout.setBackgroundColor(i2);
            this.shadowDrawable.setColorFilter(i2, Mode.SRC_IN);
        }
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int i) {
                if (i >= 0) {
                    TL_dialog item = ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter ? ShareAlert.this.listAdapter.getItem(i) : ShareAlert.this.searchAdapter.getItem(i);
                    if (item != null) {
                        if (view instanceof ShareDialogCell) {
                            ShareDialogCell shareDialogCell = (ShareDialogCell) view;
                            if (ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(item.id))) {
                                ShareAlert.this.selectedDialogs.remove(Long.valueOf(item.id));
                                shareDialogCell.setChecked(false, true);
                            } else {
                                ShareAlert.this.selectedDialogs.put(Long.valueOf(item.id), item);
                                shareDialogCell.setChecked(true, true);
                            }
                        } else if (view instanceof DialogCell) {
                            DialogCell dialogCell = (DialogCell) view;
                            if (ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(item.id))) {
                                ShareAlert.this.selectedDialogs.remove(Long.valueOf(item.id));
                                dialogCell.setDialogSelected(false);
                            } else {
                                ShareAlert.this.selectedDialogs.put(Long.valueOf(item.id), item);
                                dialogCell.setDialogSelected(true);
                            }
                        }
                        ShareAlert.this.updateSelectedCount();
                    }
                }
            }
        });
        this.gridView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ShareAlert.this.updateLayout();
            }
        });
        this.searchEmptyView = new EmptyTextProgressView(context);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.showTextView();
        this.searchEmptyView.setText(LocaleController.getString("NoChats", C0338R.string.NoChats));
        this.gridView.setEmptyView(this.searchEmptyView);
        this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 48, 51));
        this.shadow = new View(context);
        this.shadow.setBackgroundResource(C0338R.drawable.header_shadow);
        this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        updateSelectedCount();
        if (!DialogsActivity.dialogsLoaded) {
            MessagesController.getInstance().loadDialogs(0, 100, true);
            ContactsController.getInstance().checkInviteText();
            DialogsActivity.dialogsLoaded = true;
        }
        if (this.listAdapter.dialogs.isEmpty()) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
        }
        initTabs(context);
        showMaterialHelp(context);
    }

    private void copyLink(Context context) {
        if (this.exportedMessageLink != null || this.linkToCopy != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.linkToCopy != null ? this.linkToCopy : this.exportedMessageLink.link));
                Toast.makeText(context, LocaleController.getString("LinkCopied", C0338R.string.LinkCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    private int getCurrentTop() {
        if (this.gridView.getChildCount() != 0) {
            View childAt = this.gridView.getChildAt(0);
            Holder holder = (Holder) this.gridView.findContainingViewHolder(childAt);
            if (holder != null) {
                int paddingTop = this.gridView.getPaddingTop();
                int top = (holder.getAdapterPosition() != 0 || childAt.getTop() < 0) ? 0 : childAt.getTop();
                return paddingTop - top;
            }
        }
        return NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
    }

    private void initTabs(Context context) {
        if (MoboConstants.ah && MoboConstants.f1344k != 0) {
            int i;
            this.tabs.clear();
            this.tabs.addAll(TabsUtil.m2260a(true, true));
            Collections.reverse(this.tabs);
            int i2 = MoboConstants.ak;
            for (TabData tabData : this.tabs) {
                if (tabData.m2225a() == i2) {
                    this.currentTab = tabData;
                    break;
                }
            }
            if (this.currentTab == null) {
                this.currentTab = (TabData) this.tabs.get(this.tabs.size() - 1);
            }
            this.pager = new AnonymousClass14(context);
            this.pager.setAdapter(new TabsPagesAdapter());
            this.pagerSlidingTabStripContainer = new AnonymousClass15(context);
            this.pagerSlidingTabStripContainer.setOrientation(0);
            this.pagerSlidingTabStripContainer.setBackgroundColor(-657673);
            initThemeTab();
            this.containerView.addView(this.pagerSlidingTabStripContainer, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
            View pagerSlidingTabStrip = new PagerSlidingTabStrip(context);
            pagerSlidingTabStrip.setViewPager(this.pager);
            pagerSlidingTabStrip.setShouldExpand(true);
            pagerSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(2.0f));
            pagerSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            pagerSlidingTabStrip.setIndicatorColor(-13920542);
            pagerSlidingTabStrip.setUnderlineColor(-1907225);
            if (ThemeUtil.m2490b()) {
                i = AdvanceTheme.ch;
                int i3 = AdvanceTheme.ci;
                i = i == -657673 ? -1907225 : AdvanceTheme.m2283b(i, 16);
                pagerSlidingTabStrip.setIndicatorColor(i3);
                pagerSlidingTabStrip.setUnderlineColor(i);
            }
            pagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
                public void onPageScrollStateChanged(int i) {
                }

                public void onPageScrolled(int i, float f, int i2) {
                }

                public void onPageSelected(int i) {
                    ShareAlert shareAlert = ShareAlert.this;
                    TabData tabData = (ShareAlert.this.tabs == null || ShareAlert.this.pager == null) ? null : (TabData) ShareAlert.this.tabs.get(ShareAlert.this.pager.getCurrentItem());
                    shareAlert.currentTab = tabData;
                    if (MoboConstants.al && ShareAlert.this.currentTab != null) {
                        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                        edit.putInt("multi_forward_default_tab", ShareAlert.this.currentTab.m2225a());
                        edit.commit();
                        MoboConstants.ak = ShareAlert.this.currentTab.m2225a();
                    }
                    ShareAlert.this.listAdapter.fetchDialogs();
                    ShareAlert.this.listAdapter.notifyDataSetChanged();
                    ShareAlert.this.selectAllcheckBox.setChecked(false, true);
                }
            });
            this.pagerSlidingTabStripContainer.addView(pagerSlidingTabStrip, LayoutHelper.createLinear(-1, -1));
            this.pagerSlidingTabStripContainer.addView(new FrameLayout(context), LayoutHelper.createLinear(52, 48));
            this.containerView.addView(this.pager, LayoutHelper.createFrame(-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.gridView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ShareAlert.this.pager.onTouchEvent(motionEvent);
                    return false;
                }
            });
            for (i = 0; i < this.tabs.size(); i++) {
                if (((TabData) this.tabs.get(i)).m2225a() == this.currentTab.m2225a()) {
                    this.pager.setCurrentItem(i);
                    this.listAdapter.fetchDialogs();
                    this.listAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    private void initThemeTab() {
        if (ThemeUtil.m2490b()) {
            this.pagerSlidingTabStripContainer.setBackgroundColor(AdvanceTheme.ch);
        }
    }

    private void setCheckColor() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.bf;
            this.quoteTextView.setTextColor(i != Theme.ATTACH_SHEET_TEXT_COLOR ? i : Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            TextView textView = this.captionTextView;
            if (i == Theme.ATTACH_SHEET_TEXT_COLOR) {
                i = Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR;
            }
            textView.setTextColor(i);
        }
    }

    private void showMaterialHelp(Context context) {
        setOnFinishOpenAnimation(new OnFinishOpenAnimationListener() {
            public void onFinishOpenAnimation() {
                try {
                    MaterialHelperUtil.m1369a(ShareAlert.this, ShareAlert.this.quoteCheckBox, ShareAlert.this.selectAllcheckBox, ShareAlert.this.captionCheckBox, ShareAlert.this.pagerSlidingTabStripContainer, ShareAlert.this.pagerSlidingTabStripContainer);
                } catch (Exception e) {
                }
            }
        });
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        if (this.gridView.getChildCount() > 0) {
            View childAt = this.gridView.getChildAt(0);
            Holder holder = (Holder) this.gridView.findContainingViewHolder(childAt);
            int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
            int i = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
            if (this.scrollOffsetY != i) {
                RecyclerListView recyclerListView = this.gridView;
                this.scrollOffsetY = i;
                recyclerListView.setTopGlowOffset(i);
                this.frameLayout.setTranslationY((float) this.scrollOffsetY);
                this.shadow.setTranslationY((float) this.scrollOffsetY);
                this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
                if (this.pagerSlidingTabStripContainer != null) {
                    this.pagerSlidingTabStripContainer.setTranslationY((float) this.scrollOffsetY);
                }
                this.containerView.invalidate();
            }
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.dialogsNeedReload) {
            if (this.listAdapter != null) {
                this.listAdapter.fetchDialogs();
            }
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
        }
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
    }

    public void setQuoteCheck(boolean z) {
        this.quoteCheckBox.setChecked(z);
        setCheckColor();
    }

    public void updateSelectedCount() {
        if (this.selectedDialogs.isEmpty()) {
            this.doneButtonBadgeTextView.setVisibility(8);
            if (this.isPublicChannel || this.linkToCopy != null) {
                this.doneButtonTextView.setTextColor(Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
                this.doneButton.setEnabled(true);
                this.doneButtonTextView.setText(LocaleController.getString("CopyLink", C0338R.string.CopyLink).toUpperCase());
            } else {
                this.doneButtonTextView.setTextColor(Theme.SHARE_SHEET_SEND_DISABLED_TEXT_COLOR);
                this.doneButton.setEnabled(false);
                this.doneButtonTextView.setText(LocaleController.getString("Send", C0338R.string.Send).toUpperCase());
            }
        } else {
            this.doneButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            this.doneButtonBadgeTextView.setVisibility(0);
            this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.selectedDialogs.size())}));
            this.doneButtonTextView.setTextColor(Theme.SHARE_SHEET_SEND_TEXT_COLOR);
            this.doneButton.setEnabled(true);
            this.doneButtonTextView.setText(LocaleController.getString("Send", C0338R.string.Send).toUpperCase());
        }
        if (ThemeUtil.m2490b()) {
            this.doneButtonTextView.setTextColor(AdvanceTheme.bf);
        }
    }
}
