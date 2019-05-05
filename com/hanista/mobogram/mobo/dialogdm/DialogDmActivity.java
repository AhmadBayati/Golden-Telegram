package com.hanista.mobogram.mobo.dialogdm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.mobo.DialogSelectActivity.DialogSelectActivity;
import com.hanista.mobogram.mobo.dialogdm.DialogDmDetailActivity.DialogDmDetailActivity;
import com.hanista.mobogram.mobo.dialogdm.b.AnonymousClass11;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.Builder;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.dialogdm.b */
public class DialogDmActivity extends BaseFragment implements NotificationCenterDelegate {
    private static boolean f586g;
    private DialogDmAdapter f587a;
    private TextView f588b;
    private RecyclerListView f589c;
    private DataBaseAccess f590d;
    private List<DialogDm> f591e;
    private LinearLayoutManager f592f;
    private boolean f593h;
    private long f594i;
    private FrameLayout f595j;
    private FrameLayout f596k;
    private TextView f597l;
    private TextView f598m;
    private TextView f599n;
    private TextView f600o;
    private ProgressBar f601p;

    /* compiled from: DialogDmActivity */
    /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.11 */
    class AnonymousClass11 implements OnClickListener {
        final /* synthetic */ int f572a;
        final /* synthetic */ DialogDmActivity f573b;

        /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.11.1 */
        class DialogDmActivity implements DialogDmDetailActivity {
            final /* synthetic */ DialogDm f570a;
            final /* synthetic */ AnonymousClass11 f571b;

            DialogDmActivity(AnonymousClass11 anonymousClass11, DialogDm dialogDm) {
                this.f571b = anonymousClass11;
                this.f570a = dialogDm;
            }

            public void m570a(int i, int i2) {
                this.f570a.m557a(i);
                this.f570a.m562b(i2);
                this.f571b.f573b.f590d.m841a(this.f570a);
                this.f571b.f573b.m584b();
            }
        }

        AnonymousClass11(DialogDmActivity dialogDmActivity, int i) {
            this.f573b = dialogDmActivity;
            this.f572a = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            DialogDm dialogDm = (DialogDm) this.f573b.f591e.get(this.f572a);
            if (i == 0) {
                Bundle bundle = new Bundle();
                bundle.putInt("docType", dialogDm.m563c());
                bundle.putInt("messageCount", dialogDm.m564d());
                BaseFragment dialogDmDetailActivity = new DialogDmDetailActivity(bundle);
                dialogDmDetailActivity.m606a(new com.hanista.mobogram.mobo.dialogdm.DialogDmActivity.11.DialogDmActivity(this, dialogDm));
                this.f573b.presentFragment(dialogDmDetailActivity);
            } else if (i == 1) {
                if (this.f572a != 0) {
                    dialogDm = (DialogDm) this.f573b.f591e.get(this.f572a);
                    r1 = (DialogDm) this.f573b.f591e.get(this.f572a - 1);
                    r2 = r1.m565e();
                    r1.m559a(dialogDm.m565e());
                    dialogDm.m559a(r2);
                    this.f573b.f590d.m841a(dialogDm);
                    this.f573b.f590d.m841a(r1);
                    this.f573b.m584b();
                }
            } else if (i == 2) {
                if (this.f572a != this.f573b.f591e.size() - 1) {
                    dialogDm = (DialogDm) this.f573b.f591e.get(this.f572a + 1);
                    r1 = (DialogDm) this.f573b.f591e.get(this.f572a);
                    r2 = dialogDm.m565e();
                    dialogDm.m559a(r1.m565e());
                    r1.m559a(r2);
                    this.f573b.f590d.m841a(r1);
                    this.f573b.f590d.m841a(dialogDm);
                    this.f573b.m584b();
                }
            } else if (i == 3) {
                this.f573b.m578a((DialogDm) this.f573b.f591e.get(this.f572a));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.1 */
    class DialogDmActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ DialogDmActivity f574a;

        /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.1.1 */
        class DialogDmActivity implements DialogSelectActivity {
            final /* synthetic */ DialogDmActivity f568a;

            DialogDmActivity(DialogDmActivity dialogDmActivity) {
                this.f568a = dialogDmActivity;
            }

            public void m568a(List<Long> list) {
                if (list != null && list.size() != 0) {
                    this.f568a.f574a.parentLayout.rebuildAllFragmentViews(false);
                    this.f568a.f574a.m576a(((Long) list.get(0)).longValue());
                }
            }
        }

        DialogDmActivity(DialogDmActivity dialogDmActivity) {
            this.f574a = dialogDmActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f574a.finishFragment();
            } else if (i == 2) {
                Bundle bundle = new Bundle();
                bundle.putInt("dialogsType", 0);
                bundle.putBoolean("multiSelect", false);
                BaseFragment dialogSelectActivity = new com.hanista.mobogram.mobo.DialogSelectActivity(bundle);
                dialogSelectActivity.m536a(new DialogDmActivity(this));
                this.f574a.presentFragment(dialogSelectActivity);
            } else if (i == 3) {
                this.f574a.presentFragment(new DialogDmSettingsActivity());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.2 */
    class DialogDmActivity implements Comparator<DialogDm> {
        final /* synthetic */ DialogDmActivity f575a;

        DialogDmActivity(DialogDmActivity dialogDmActivity) {
            this.f575a = dialogDmActivity;
        }

        public int m571a(DialogDm dialogDm, DialogDm dialogDm2) {
            return (dialogDm.m565e() == null || dialogDm2.m565e() == null) ? 0 : dialogDm.m565e().intValue() < dialogDm2.m565e().intValue() ? -1 : dialogDm.m565e().intValue() > dialogDm2.m565e().intValue() ? 1 : 0;
        }

        public /* synthetic */ int compare(Object obj, Object obj2) {
            return m571a((DialogDm) obj, (DialogDm) obj2);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.3 */
    class DialogDmActivity implements OnClickListener {
        final /* synthetic */ DialogDm f576a;
        final /* synthetic */ DialogDmActivity f577b;

        DialogDmActivity(DialogDmActivity dialogDmActivity, DialogDm dialogDm) {
            this.f577b = dialogDmActivity;
            this.f576a = dialogDm;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            this.f577b.f590d.m901m(this.f576a.m556a());
            this.f577b.m584b();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.4 */
    class DialogDmActivity implements OnTouchListener {
        final /* synthetic */ DialogDmActivity f578a;

        DialogDmActivity(DialogDmActivity dialogDmActivity) {
            this.f578a = dialogDmActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.5 */
    class DialogDmActivity extends LinearLayoutManager {
        final /* synthetic */ DialogDmActivity f579a;

        DialogDmActivity(DialogDmActivity dialogDmActivity, Context context) {
            this.f579a = dialogDmActivity;
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.6 */
    class DialogDmActivity implements OnItemClickListener {
        final /* synthetic */ DialogDmActivity f580a;

        DialogDmActivity(DialogDmActivity dialogDmActivity) {
            this.f580a = dialogDmActivity;
        }

        public void onItemClick(View view, int i) {
            DialogDm dialogDm = (DialogDm) this.f580a.f591e.get(i);
            Bundle bundle = new Bundle();
            int b = (int) dialogDm.m561b();
            int b2 = (int) (dialogDm.m561b() >> 32);
            if (b != 0) {
                if (b2 == 1) {
                    bundle.putInt("chat_id", b);
                } else if (b > 0) {
                    bundle.putInt("user_id", b);
                } else if (b < 0) {
                    bundle.putInt("chat_id", -b);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                this.f580a.presentFragment(new ChatActivity(bundle));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.7 */
    class DialogDmActivity implements OnItemLongClickListener {
        final /* synthetic */ DialogDmActivity f581a;

        DialogDmActivity(DialogDmActivity dialogDmActivity) {
            this.f581a = dialogDmActivity;
        }

        public boolean onItemClick(View view, int i) {
            if (((DialogDm) this.f581a.f591e.get(i)).m556a() != null) {
                this.f581a.m575a(i);
            }
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.8 */
    class DialogDmActivity implements OnClickListener {
        final /* synthetic */ long f582a;
        final /* synthetic */ DialogDmActivity f583b;

        DialogDmActivity(DialogDmActivity dialogDmActivity, long j) {
            this.f583b = dialogDmActivity;
            this.f582a = j;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            this.f583b.m576a(this.f582a);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.b.9 */
    class DialogDmActivity implements DialogDmDetailActivity {
        final /* synthetic */ long f584a;
        final /* synthetic */ DialogDmActivity f585b;

        DialogDmActivity(DialogDmActivity dialogDmActivity, long j) {
            this.f585b = dialogDmActivity;
            this.f584a = j;
        }

        public void m572a(int i, int i2) {
            if (MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (this.f584a >> 32))) != null) {
                Toast.makeText(this.f585b.getParentActivity(), LocaleController.getString("AddSecretChatToDialogDmAlert", C0338R.string.AddSecretChatToDialogDmAlert), 1).show();
            } else if (this.f585b.m585b(this.f584a)) {
                Toast.makeText(this.f585b.getParentActivity(), LocaleController.getString("SelectedDialogAlreadyAdded", C0338R.string.SelectedDialogAlreadyAdded), 1).show();
            } else {
                DialogDm dialogDm = new DialogDm();
                dialogDm.m560a(Long.valueOf(this.f584a));
                dialogDm.m557a(i);
                dialogDm.m562b(i2);
                this.f585b.f590d.m841a(dialogDm);
                this.f585b.m584b();
            }
        }
    }

    public DialogDmActivity(Bundle bundle) {
        super(bundle);
        this.f591e = new ArrayList();
    }

    private void m574a() {
        m582a(null, 0, null);
    }

    private void m575a(int i) {
        Builder builder = new Builder(getParentActivity());
        List arrayList = new ArrayList();
        arrayList.add(LocaleController.getString("Edit", C0338R.string.Edit));
        arrayList.add(LocaleController.getString("HigherPriority", C0338R.string.HigherPriority));
        arrayList.add(LocaleController.getString("LowerPriority", C0338R.string.LowerPriority));
        arrayList.add(LocaleController.getString("Delete", C0338R.string.Delete));
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new AnonymousClass11(this, i));
        showDialog(builder.create());
    }

    private void m576a(long j) {
        BaseFragment dialogDmDetailActivity = new DialogDmDetailActivity(new Bundle());
        dialogDmDetailActivity.m606a(new DialogDmActivity(this, j));
        presentFragment(dialogDmDetailActivity, false, false);
    }

    private void m577a(Context context, FrameLayout frameLayout) {
        this.f595j = new FrameLayout(context);
        this.f595j.setBackgroundColor(-1);
        this.f595j.setVisibility(4);
        this.f595j.setFocusable(true);
        this.f595j.setFocusableInTouchMode(true);
        this.f595j.setClickable(true);
        frameLayout.addView(this.f595j, LayoutHelper.createFrame(-1, 58, 80));
        this.f597l = new TextView(context);
        this.f597l.setTypeface(FontUtil.m1176a().m1161d());
        this.f597l.setTextSize(1, 16.0f);
        this.f597l.setTextColor(Theme.CHAT_BOTTOM_OVERLAY_TEXT_COLOR);
        this.f595j.addView(this.f597l, LayoutHelper.createFrame(-2, -2, 17));
        this.f596k = new FrameLayout(context);
        this.f596k.setBackgroundColor(ThemeUtil.m2485a().m2289c());
        this.f596k.setVisibility(4);
        frameLayout.addView(this.f596k, LayoutHelper.createFrame(-1, 58, 80));
        this.f596k.setOnClickListener(new View.OnClickListener() {
            final /* synthetic */ DialogDmActivity f569a;

            {
                this.f569a = r1;
            }

            public void onClick(View view) {
                if (this.f569a.getParentActivity() != null) {
                    Intent intent;
                    if (DialogDmService.f529b) {
                        intent = new Intent(this.f569a.getParentActivity(), DialogDmService.class);
                        intent.setAction("com.hanista.mobogram.dialogdm.stop");
                        this.f569a.getParentActivity().startService(intent);
                        this.f569a.m574a();
                        return;
                    }
                    intent = new Intent(this.f569a.getParentActivity(), DialogDmService.class);
                    intent.setAction("com.hanista.mobogram.dialogdm.start");
                    this.f569a.getParentActivity().startService(intent);
                    this.f569a.m574a();
                }
            }
        });
        this.f598m = new TextView(context);
        this.f598m.setTypeface(FontUtil.m1176a().m1161d());
        this.f598m.setTextSize(1, 18.0f);
        this.f598m.setTextColor(-1);
        this.f596k.addView(this.f598m, LayoutHelper.createFrame(-2, -2, 17));
        this.f599n = new TextView(context);
        this.f599n.setTypeface(FontUtil.m1176a().m1161d());
        this.f599n.setTextSize(1, 18.0f);
        this.f599n.setTextColor(-13866845);
        this.f596k.addView(this.f599n, LayoutHelper.createFrame(-2, -2.0f, 19, 10.0f, 0.0f, 0.0f, 0.0f));
        this.f600o = new TextView(context);
        this.f600o.setTypeface(FontUtil.m1176a().m1161d());
        this.f600o.setTextSize(1, 18.0f);
        this.f600o.setTextColor(-13866845);
        this.f596k.addView(this.f600o, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 10.0f, 0.0f));
        this.f601p = new ProgressBar(context, null, 16842872);
        this.f601p.setVisibility(8);
        this.f596k.addView(this.f601p, LayoutHelper.createFrame(-1, 15, 81));
    }

    private void m578a(DialogDm dialogDm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.formatString("AreYouSureDeleteDialogDm", C0338R.string.AreYouSureDeleteDialogDm, new Object[0]));
        builder.setTitle(LocaleController.getString("Delete", C0338R.string.Delete));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new DialogDmActivity(this, dialogDm));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void m582a(String str, int i, String str2) {
        if (this.f598m != null) {
            this.f596k.setVisibility(0);
            this.f600o.setText(str2);
            if (i > 0) {
                this.f599n.setText("%" + i);
                this.f601p.setIndeterminate(false);
                this.f601p.setProgress(i);
                this.f601p.setVisibility(0);
            } else {
                this.f601p.setVisibility(8);
                this.f599n.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            if (DialogDmService.f529b) {
                this.f601p.setIndeterminate(true);
                this.f601p.setVisibility(0);
                this.f598m.setText(LocaleController.getString("StopDownloadService", C0338R.string.StopDownloadService));
                return;
            }
            this.f601p.setIndeterminate(true);
            this.f601p.setVisibility(8);
            this.f598m.setText(LocaleController.getString("StartDownloadService", C0338R.string.StartDownloadService));
        }
    }

    private void m584b() {
        this.f591e.clear();
        List<DialogDm> s = this.f590d.m912s();
        Iterator it = MessagesController.getInstance().dialogs.iterator();
        while (it.hasNext()) {
            TL_dialog tL_dialog = (TL_dialog) it.next();
            if (HiddenConfig.m1399b(Long.valueOf(tL_dialog.id)) && !HiddenConfig.f1402e) {
                break;
            }
            for (DialogDm dialogDm : s) {
                if (dialogDm.m561b() == tL_dialog.id) {
                    dialogDm.m558a(tL_dialog);
                    this.f591e.add(dialogDm);
                }
            }
        }
        Collections.sort(this.f591e, new DialogDmActivity(this));
        this.f587a.m592a(this.f591e);
        this.f587a.notifyDataSetChanged();
    }

    private boolean m585b(long j) {
        for (DialogDm b : this.f591e) {
            if (b.m561b() == j) {
                return true;
            }
        }
        return false;
    }

    private void m588c() {
        if (!UserConfig.isClientActivated()) {
        }
    }

    private void m589d() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.bc;
            this.actionBar.setBackgroundColor(i);
            this.f596k.setBackgroundColor(i);
            int i2 = AdvanceTheme.bd;
            if (i2 > 0) {
                Orientation orientation;
                switch (i2) {
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
                int i3 = AdvanceTheme.be;
                Drawable gradientDrawable = new GradientDrawable(orientation, new int[]{i, i3});
                this.actionBar.setBackgroundDrawable(gradientDrawable);
                this.f596k.setBackgroundDrawable(gradientDrawable);
            }
            this.actionBar.setTitleColor(AdvanceTheme.bb);
            this.f598m.setTextColor(AdvanceTheme.bb);
            getParentActivity().getResources().getDrawable(C0338R.drawable.plus).setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_settings).setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("ChatDownloadManager", C0338R.string.ChatDownloadManager));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new DialogDmActivity(this));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.addItem(2, (int) C0338R.drawable.plus);
        createMenu.addItem(3, (int) C0338R.drawable.ic_ab_settings);
        this.f590d = new DataBaseAccess();
        this.f587a = new DialogDmAdapter(context, this.f591e);
        this.fragmentView = new FrameLayout(context);
        View linearLayout = new LinearLayout(context);
        linearLayout.setVisibility(4);
        linearLayout.setOrientation(1);
        ((FrameLayout) this.fragmentView).addView(linearLayout);
        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOnTouchListener(new DialogDmActivity(this));
        this.f588b = new TextView(context);
        this.f588b.setTypeface(FontUtil.m1176a().m1161d());
        this.f588b.setTextColor(-8355712);
        this.f588b.setTextSize(1, 18.0f);
        this.f588b.setGravity(17);
        this.f588b.setText(LocaleController.getString("NoDialogDmHelp", C0338R.string.NoDialogDmHelp));
        linearLayout.addView(this.f588b);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.f588b.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        this.f588b.setLayoutParams(layoutParams2);
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams2);
        this.f589c = new RecyclerListView(context);
        initThemeBackground(this.f589c);
        this.f589c.setEmptyView(linearLayout);
        this.f589c.setVerticalScrollBarEnabled(false);
        this.f589c.setScrollBarStyle(33554432);
        this.f589c.setAdapter(this.f587a);
        ((FrameLayout) this.fragmentView).addView(this.f589c);
        layoutParams = (LayoutParams) this.f589c.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.bottomMargin = AndroidUtilities.dp(58.0f);
        this.f589c.setLayoutParams(layoutParams);
        this.f592f = new DialogDmActivity(this, context);
        this.f592f.setOrientation(1);
        this.f589c.setLayoutManager(this.f592f);
        this.f589c.setOnItemClickListener(new DialogDmActivity(this));
        this.f589c.setOnItemLongClickListener(new DialogDmActivity(this));
        m577a(context, (FrameLayout) this.fragmentView);
        m574a();
        if (this.f594i != 0) {
            long j = this.f594i;
            this.f594i = 0;
            if (m585b(this.f594i)) {
                Toast.makeText(getParentActivity(), LocaleController.getString("SelectedDialogAlreadyAdded", C0338R.string.SelectedDialogAlreadyAdded), 1).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("AreYouSureToAddDialogToList", C0338R.string.AreYouSureToAddDialogToList));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new DialogDmActivity(this, j));
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                showDialog(builder.create());
            }
        } else {
            m588c();
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i != NotificationCenter.dialogsNeedReload) {
            if (i == NotificationCenter.dialogDmServiceStarted || i == NotificationCenter.dialogDmServiceStoped) {
                m574a();
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogDmServiceStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogDmServiceStoped);
        if (getArguments() != null) {
            this.f593h = this.arguments.getBoolean("fromMenu", false);
            this.f594i = this.arguments.getLong("dialogId", 0);
        }
        if (!(f586g || this.f593h)) {
            MessagesController.getInstance().loadDialogs(0, 100, true);
            f586g = true;
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.categoriesInfoDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogDmServiceStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogDmServiceStoped);
    }

    public void onResume() {
        super.onResume();
        if (getParentActivity() != null) {
            m584b();
        }
        m589d();
    }
}
