package com.hanista.mobogram.mobo.p013m;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.query.SharedMediaQuery;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.component.PowerView;
import com.hanista.mobogram.mobo.download.DownloadMessagesStorage;
import com.hanista.mobogram.mobo.download.DownloadUtil;
import com.hanista.mobogram.mobo.p000a.ArchiveUtil;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p009j.SharedGifCell.SharedGifCell;
import com.hanista.mobogram.mobo.p010k.MaterialHelperUtil;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p013m.AllSharedMediaQuery;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_search;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPageEmpty;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.WebPage;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import com.hanista.mobogram.ui.ActionBar.BackDrawable;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Adapters.BaseSectionsAdapter;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
import com.hanista.mobogram.ui.Cells.GreySectionCell;
import com.hanista.mobogram.ui.Cells.LoadingCell;
import com.hanista.mobogram.ui.Cells.SharedDocumentCell;
import com.hanista.mobogram.ui.Cells.SharedLinkCell;
import com.hanista.mobogram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate;
import com.hanista.mobogram.ui.Cells.SharedMediaSectionCell;
import com.hanista.mobogram.ui.Cells.SharedPhotoVideoCell;
import com.hanista.mobogram.ui.Cells.SharedPhotoVideoCell.SharedPhotoVideoCellDelegate;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.NumberTextView;
import com.hanista.mobogram.ui.Components.PlayerView;
import com.hanista.mobogram.ui.Components.SectionsListView;
import com.hanista.mobogram.ui.Components.ShareAlert;
import com.hanista.mobogram.ui.Components.WebFrameLayout;
import com.hanista.mobogram.ui.DialogsActivity;
import com.hanista.mobogram.ui.DialogsActivity.DialogsActivityDelegate;
import com.hanista.mobogram.ui.PhotoViewer;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

/* renamed from: com.hanista.mobogram.mobo.m.a */
public class AllMediaActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private HashMap<Integer, MessageObject>[] f1667A;
    private int f1668B;
    private ArrayList<View> f1669C;
    private boolean f1670D;
    private long f1671E;
    private long f1672F;
    private int f1673G;
    private int f1674H;
    private int f1675I;
    private boolean f1676J;
    private ActionBarMenuItem f1677K;
    private AllMediaActivity[] f1678L;
    private int f1679M;
    protected ChatFull f1680a;
    private AllMediaActivity f1681b;
    private AllMediaActivity f1682c;
    private AllMediaActivity f1683d;
    private AllMediaActivity f1684e;
    private AllMediaActivity f1685f;
    private AllMediaActivity f1686g;
    private AllMediaActivity f1687h;
    private AllMediaActivity f1688i;
    private AllMediaActivity f1689j;
    private AllMediaActivity f1690k;
    private SectionsListView f1691l;
    private LinearLayout f1692m;
    private TextView f1693n;
    private ImageView f1694o;
    private LinearLayout f1695p;
    private TextView f1696q;
    private ActionBarMenuItem f1697r;
    private ActionBarMenuItem f1698s;
    private NumberTextView f1699t;
    private ArrayList<SharedPhotoVideoCell> f1700u;
    private FrameLayout f1701v;
    private TextView f1702w;
    private TextView f1703x;
    private boolean f1704y;
    private boolean f1705z;

    /* renamed from: com.hanista.mobogram.mobo.m.a.1 */
    class AllMediaActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ AllMediaActivity f1614a;

        /* renamed from: com.hanista.mobogram.mobo.m.a.1.1 */
        class AllMediaActivity implements OnClickListener {
            final /* synthetic */ AllMediaActivity f1606a;

            AllMediaActivity(AllMediaActivity allMediaActivity) {
                this.f1606a = allMediaActivity;
            }

            public void onClick(View view) {
                this.f1606a.f1614a.f1676J = !this.f1606a.f1614a.f1676J;
                ((CheckBoxCell) view).setChecked(this.f1606a.f1614a.f1676J, true);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.m.a.1.2 */
        class AllMediaActivity implements DialogInterface.OnClickListener {
            final /* synthetic */ AllMediaActivity f1607a;

            AllMediaActivity(AllMediaActivity allMediaActivity) {
                this.f1607a = allMediaActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                for (int i2 = 1; i2 >= 0; i2--) {
                    MessageObject messageObject;
                    ArrayList arrayList = new ArrayList(this.f1607a.f1614a.f1667A[i2].keySet());
                    ArrayList arrayList2 = null;
                    EncryptedChat encryptedChat = null;
                    int i3 = 0;
                    if (!arrayList.isEmpty()) {
                        messageObject = (MessageObject) this.f1607a.f1614a.f1667A[i2].get(arrayList.get(0));
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            i3 = messageObject.messageOwner.to_id.channel_id;
                        }
                    }
                    if (((int) this.f1607a.f1614a.f1672F) == 0) {
                        encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (this.f1607a.f1614a.f1672F >> 32)));
                    }
                    if (encryptedChat != null) {
                        arrayList2 = new ArrayList();
                        for (Entry value : this.f1607a.f1614a.f1667A[i2].entrySet()) {
                            messageObject = (MessageObject) value.getValue();
                            if (!(messageObject.messageOwner.random_id == 0 || messageObject.type == 10)) {
                                arrayList2.add(Long.valueOf(messageObject.messageOwner.random_id));
                            }
                        }
                    }
                    MessagesController.getInstance().deleteMessages(arrayList, arrayList2, encryptedChat, i3, this.f1607a.f1614a.f1676J);
                    for (Entry value2 : this.f1607a.f1614a.f1667A[i2].entrySet()) {
                        messageObject = (MessageObject) value2.getValue();
                        if (messageObject.getDialogId() == ((long) UserConfig.getClientUserId())) {
                            List arrayList3 = new ArrayList();
                            arrayList3.add(Integer.valueOf(messageObject.getId()));
                            ArchiveUtil.m263a(arrayList3);
                        }
                    }
                    this.f1607a.f1614a.f1667A[i2].clear();
                }
                this.f1607a.f1614a.actionBar.hideActionMode();
                this.f1607a.f1614a.actionBar.closeSearchField();
                this.f1607a.f1614a.f1668B = 0;
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.m.a.1.3 */
        class AllMediaActivity implements DialogsActivityDelegate {
            final /* synthetic */ int f1608a;
            final /* synthetic */ AllMediaActivity f1609b;

            AllMediaActivity(AllMediaActivity allMediaActivity, int i) {
                this.f1609b = allMediaActivity;
                this.f1608a = i;
            }

            public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
                int i = (int) j;
                if (i != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("scrollToTopOnResume", true);
                    if (i > 0) {
                        bundle.putInt("user_id", i);
                    } else if (i < 0) {
                        bundle.putInt("chat_id", -i);
                    }
                    ArrayList arrayList = new ArrayList();
                    for (int i2 = 1; i2 >= 0; i2--) {
                        Object arrayList2 = new ArrayList(this.f1609b.f1614a.f1667A[i2].keySet());
                        Collections.sort(arrayList2);
                        Iterator it = arrayList2.iterator();
                        while (it.hasNext()) {
                            Integer num = (Integer) it.next();
                            if (num.intValue() > 0) {
                                arrayList.add(this.f1609b.f1614a.f1667A[i2].get(num));
                            }
                        }
                        this.f1609b.f1614a.f1667A[i2].clear();
                    }
                    this.f1609b.f1614a.f1668B = 0;
                    this.f1609b.f1614a.actionBar.hideActionMode();
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    BaseFragment chatActivity = new ChatActivity(bundle);
                    ChatActivity.forwardNoName = this.f1608a == 111;
                    this.f1609b.f1614a.presentFragment(chatActivity, true);
                    chatActivity.showReplyPanel(true, null, arrayList, null, false, false);
                    if (!AndroidUtilities.isTablet()) {
                        this.f1609b.f1614a.removeSelfFromStack();
                        return;
                    }
                    return;
                }
                dialogsActivity.finishFragment();
            }
        }

        AllMediaActivity(AllMediaActivity allMediaActivity) {
            this.f1614a = allMediaActivity;
        }

        public void onItemClick(int i) {
            int i2 = 1;
            if (i == -1) {
                if (this.f1614a.actionBar.isActionModeShowed()) {
                    while (i2 >= 0) {
                        this.f1614a.f1667A[i2].clear();
                        i2--;
                    }
                    this.f1614a.f1668B = 0;
                    this.f1614a.actionBar.hideActionMode();
                    this.f1614a.f1691l.invalidateViews();
                    return;
                }
                this.f1614a.finishFragment();
            } else if (i == 51) {
                this.f1614a.m1633a();
            } else if (i == 52) {
                this.f1614a.m1643b();
            } else if (i == 1) {
                if (this.f1614a.f1674H != 0 || this.f1614a.f1679M != 0) {
                    this.f1614a.f1674H = 0;
                    this.f1614a.f1679M = 0;
                    this.f1614a.m1654e();
                    this.f1614a.m1647c();
                }
            } else if (i == 2) {
                if (this.f1614a.f1674H != 0 || this.f1614a.f1679M != 1) {
                    this.f1614a.f1674H = 0;
                    this.f1614a.f1679M = 1;
                    this.f1614a.m1654e();
                    this.f1614a.m1647c();
                }
            } else if (i == 83) {
                if (this.f1614a.f1674H != 5) {
                    this.f1614a.f1674H = 5;
                    this.f1614a.f1679M = 3;
                    this.f1614a.m1654e();
                }
            } else if (i == 3) {
                if (this.f1614a.f1674H != 0 || this.f1614a.f1679M != 2) {
                    this.f1614a.f1674H = 0;
                    this.f1614a.f1679M = 2;
                    this.f1614a.m1654e();
                    this.f1614a.m1647c();
                }
            } else if (i == 4) {
                if (this.f1614a.f1674H != 1) {
                    this.f1614a.f1674H = 1;
                    this.f1614a.m1654e();
                }
            } else if (i == 5) {
                if (this.f1614a.f1674H != 3) {
                    this.f1614a.f1674H = 3;
                    this.f1614a.m1654e();
                }
            } else if (i == 6) {
                if (this.f1614a.f1674H != 4) {
                    this.f1614a.f1674H = 4;
                    this.f1614a.m1654e();
                }
            } else if (i == 191) {
                this.f1614a.m1665k();
            } else if (i == 8) {
                if (this.f1614a.getParentActivity() != null) {
                    int i3 = 1;
                    int i4 = 0;
                    while (i3 >= 0) {
                        int i5;
                        for (Entry value : this.f1614a.f1667A[i3].entrySet()) {
                            MessageObject messageObject = (MessageObject) value.getValue();
                            messageObject.checkMediaExistance();
                            if (messageObject.mediaExists) {
                                i5 = 1;
                                break;
                            }
                        }
                        i5 = i4;
                        i3--;
                        i4 = i5;
                    }
                    Builder builder = new Builder(this.f1614a.getParentActivity());
                    this.f1614a.f1676J = MoboConstants.aE;
                    if (i4 != 0) {
                        View frameLayout = new FrameLayout(this.f1614a.getParentActivity());
                        if (VERSION.SDK_INT >= 21) {
                            frameLayout.setPadding(0, AndroidUtilities.dp(8.0f), 0, 0);
                        }
                        View createDeleteFileCheckBox = AndroidUtilities.createDeleteFileCheckBox(this.f1614a.getParentActivity());
                        frameLayout.addView(createDeleteFileCheckBox, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
                        createDeleteFileCheckBox.setOnClickListener(new AllMediaActivity(this));
                        builder.setView(frameLayout);
                    }
                    builder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", C0338R.string.AreYouSureDeleteMessages, LocaleController.formatPluralString("items", this.f1614a.f1667A[0].size() + this.f1614a.f1667A[1].size())));
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AllMediaActivity(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    this.f1614a.showDialog(builder.create());
                }
            } else if (i == 7 || i == 111) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putInt("dialogsType", 1);
                BaseFragment dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(new AllMediaActivity(this, i));
                this.f1614a.presentFragment(dialogsActivity);
            } else if (i == 112) {
                this.f1614a.m1658g();
            } else if (i == 190) {
                this.f1614a.m1661i();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.2 */
    class AllMediaActivity implements OnTouchListener {
        final /* synthetic */ AllMediaActivity f1615a;

        AllMediaActivity(AllMediaActivity allMediaActivity) {
            this.f1615a = allMediaActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.3 */
    class AllMediaActivity implements DialogInterface.OnClickListener {
        final /* synthetic */ AllMediaActivity f1617a;

        /* renamed from: com.hanista.mobogram.mobo.m.a.3.1 */
        class AllMediaActivity implements DialogsActivityDelegate {
            final /* synthetic */ AllMediaActivity f1616a;

            AllMediaActivity(AllMediaActivity allMediaActivity) {
                this.f1616a = allMediaActivity;
            }

            public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
                this.f1616a.f1617a.f1672F = j;
                this.f1616a.f1617a.m1647c();
                dialogsActivity.finishFragment();
            }
        }

        AllMediaActivity(AllMediaActivity allMediaActivity) {
            this.f1617a = allMediaActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 0) {
                this.f1617a.f1672F = 0;
                this.f1617a.m1647c();
            } else if (i == 1) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putBoolean("justSelect", true);
                bundle.putInt("dialogsType", 1);
                BaseFragment dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(new AllMediaActivity(this));
                this.f1617a.presentFragment(dialogsActivity);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.4 */
    class AllMediaActivity implements DialogInterface.OnClickListener {
        final /* synthetic */ AllMediaActivity f1618a;

        AllMediaActivity(AllMediaActivity allMediaActivity) {
            this.f1618a = allMediaActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 0) {
                this.f1618a.f1673G = 0;
            } else if (i == 1) {
                this.f1618a.f1673G = 1;
            } else if (i == 2) {
                this.f1618a.f1673G = 2;
            }
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            edit.putInt("all_shared_media_dl_type", this.f1618a.f1673G);
            edit.commit();
            this.f1618a.m1647c();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.5 */
    class AllMediaActivity implements OnPreDrawListener {
        final /* synthetic */ AllMediaActivity f1619a;

        AllMediaActivity(AllMediaActivity allMediaActivity) {
            this.f1619a = allMediaActivity;
        }

        public boolean onPreDraw() {
            this.f1619a.f1691l.getViewTreeObserver().removeOnPreDrawListener(this);
            this.f1619a.m1655f();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.6 */
    class AllMediaActivity extends ActionBarMenuItemSearchListener {
        final /* synthetic */ AllMediaActivity f1620a;

        AllMediaActivity(AllMediaActivity allMediaActivity) {
            this.f1620a = allMediaActivity;
        }

        public void onSearchCollapse() {
            this.f1620a.f1697r.setVisibility(0);
            if (this.f1620a.f1674H == 1) {
                this.f1620a.f1688i.m1599a(null);
            } else if (this.f1620a.f1674H == 3) {
                this.f1620a.f1690k.m1599a(null);
            } else if (this.f1620a.f1674H == 4) {
                this.f1620a.f1689j.m1599a(null);
            }
            this.f1620a.f1705z = false;
            this.f1620a.f1704y = false;
            this.f1620a.m1654e();
        }

        public void onSearchExpand() {
            this.f1620a.f1697r.setVisibility(8);
            this.f1620a.f1705z = true;
        }

        public void onTextChanged(EditText editText) {
            String obj = editText.getText().toString();
            if (obj.length() != 0) {
                this.f1620a.f1704y = true;
                this.f1620a.m1654e();
            }
            if (this.f1620a.f1674H == 1) {
                if (this.f1620a.f1688i != null) {
                    this.f1620a.f1688i.m1599a(obj);
                }
            } else if (this.f1620a.f1674H == 3) {
                if (this.f1620a.f1690k != null) {
                    this.f1620a.f1690k.m1599a(obj);
                }
            } else if (this.f1620a.f1674H == 4 && this.f1620a.f1689j != null) {
                this.f1620a.f1689j.m1599a(obj);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.7 */
    class AllMediaActivity implements OnClickListener {
        final /* synthetic */ AllMediaActivity f1621a;

        AllMediaActivity(AllMediaActivity allMediaActivity) {
            this.f1621a = allMediaActivity;
        }

        public void onClick(View view) {
            this.f1621a.f1697r.toggleSubMenu();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.8 */
    class AllMediaActivity implements OnTouchListener {
        final /* synthetic */ AllMediaActivity f1622a;

        AllMediaActivity(AllMediaActivity allMediaActivity) {
            this.f1622a = allMediaActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.9 */
    class AllMediaActivity implements OnClickListener {
        final /* synthetic */ AllMediaActivity f1623a;

        AllMediaActivity(AllMediaActivity allMediaActivity) {
            this.f1623a = allMediaActivity;
        }

        public void onClick(View view) {
            this.f1623a.m1633a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.a */
    public class AllMediaActivity extends BaseFragmentAdapter {
        protected ArrayList<MessageObject> f1638a;
        final /* synthetic */ AllMediaActivity f1639b;
        private Context f1640c;
        private ArrayList<MessageObject> f1641d;
        private Timer f1642e;
        private int f1643f;
        private int f1644g;
        private int f1645h;

        /* renamed from: com.hanista.mobogram.mobo.m.a.a.1 */
        class AllMediaActivity implements RequestDelegate {
            final /* synthetic */ int f1626a;
            final /* synthetic */ int f1627b;
            final /* synthetic */ AllMediaActivity f1628c;

            /* renamed from: com.hanista.mobogram.mobo.m.a.a.1.1 */
            class AllMediaActivity implements Runnable {
                final /* synthetic */ ArrayList f1624a;
                final /* synthetic */ AllMediaActivity f1625b;

                AllMediaActivity(AllMediaActivity allMediaActivity, ArrayList arrayList) {
                    this.f1625b = allMediaActivity;
                    this.f1624a = arrayList;
                }

                public void run() {
                    if (this.f1625b.f1627b == this.f1625b.f1628c.f1644g) {
                        this.f1625b.f1628c.f1638a = this.f1624a;
                        this.f1625b.f1628c.notifyDataSetChanged();
                    }
                    this.f1625b.f1628c.f1643f = 0;
                }
            }

            AllMediaActivity(AllMediaActivity allMediaActivity, int i, int i2) {
                this.f1628c = allMediaActivity;
                this.f1626a = i;
                this.f1627b = i2;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                ArrayList arrayList = new ArrayList();
                if (tL_error == null) {
                    messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                    for (int i = 0; i < com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size(); i++) {
                        Message message = (Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(i);
                        if (this.f1626a == 0 || message.id <= this.f1626a) {
                            arrayList.add(new MessageObject(message, null, false));
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new AllMediaActivity(this, arrayList));
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.m.a.a.2 */
        class AllMediaActivity extends TimerTask {
            final /* synthetic */ String f1629a;
            final /* synthetic */ AllMediaActivity f1630b;

            AllMediaActivity(AllMediaActivity allMediaActivity, String str) {
                this.f1630b = allMediaActivity;
                this.f1629a = str;
            }

            public void run() {
                try {
                    this.f1630b.f1642e.cancel();
                    this.f1630b.f1642e = null;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                this.f1630b.m1596b(this.f1629a);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.m.a.a.3 */
        class AllMediaActivity implements Runnable {
            final /* synthetic */ String f1633a;
            final /* synthetic */ AllMediaActivity f1634b;

            /* renamed from: com.hanista.mobogram.mobo.m.a.a.3.1 */
            class AllMediaActivity implements Runnable {
                final /* synthetic */ ArrayList f1631a;
                final /* synthetic */ AllMediaActivity f1632b;

                AllMediaActivity(AllMediaActivity allMediaActivity, ArrayList arrayList) {
                    this.f1632b = allMediaActivity;
                    this.f1631a = arrayList;
                }

                public void run() {
                    String toLowerCase = this.f1632b.f1633a.trim().toLowerCase();
                    if (toLowerCase.length() == 0) {
                        this.f1632b.f1634b.m1593a(new ArrayList());
                        return;
                    }
                    String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
                    String str = (toLowerCase.equals(translitString) || translitString.length() == 0) ? null : translitString;
                    String[] strArr = new String[((str != null ? 1 : 0) + 1)];
                    strArr[0] = toLowerCase;
                    if (str != null) {
                        strArr[1] = str;
                    }
                    ArrayList arrayList = new ArrayList();
                    for (int i = 0; i < this.f1631a.size(); i++) {
                        MessageObject messageObject = (MessageObject) this.f1631a.get(i);
                        for (CharSequence charSequence : strArr) {
                            String documentName = messageObject.getDocumentName();
                            if (!(documentName == null || documentName.length() == 0)) {
                                if (!documentName.toLowerCase().contains(charSequence)) {
                                    if (this.f1632b.f1634b.f1645h == 4) {
                                        boolean contains;
                                        Document document = messageObject.type == 0 ? messageObject.messageOwner.media.webpage.document : messageObject.messageOwner.media.document;
                                        int i2 = 0;
                                        while (i2 < document.attributes.size()) {
                                            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i2);
                                            if (documentAttribute instanceof TL_documentAttributeAudio) {
                                                boolean contains2 = documentAttribute.performer != null ? documentAttribute.performer.toLowerCase().contains(charSequence) : false;
                                                contains = (contains2 || documentAttribute.title == null) ? contains2 : documentAttribute.title.toLowerCase().contains(charSequence);
                                                if (contains) {
                                                    arrayList.add(messageObject);
                                                    break;
                                                }
                                            } else {
                                                i2++;
                                            }
                                        }
                                        contains = false;
                                        if (contains) {
                                            arrayList.add(messageObject);
                                            break;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    arrayList.add(messageObject);
                                    break;
                                }
                            }
                        }
                    }
                    this.f1632b.f1634b.m1593a(arrayList);
                }
            }

            AllMediaActivity(AllMediaActivity allMediaActivity, String str) {
                this.f1634b = allMediaActivity;
                this.f1633a = str;
            }

            public void run() {
                if (!this.f1634b.f1639b.f1678L[this.f1634b.f1645h].f1656b.isEmpty()) {
                    if (this.f1634b.f1645h == 1 || this.f1634b.f1645h == 4) {
                        MessageObject messageObject = (MessageObject) this.f1634b.f1639b.f1678L[this.f1634b.f1645h].f1656b.get(this.f1634b.f1639b.f1678L[this.f1634b.f1645h].f1656b.size() - 1);
                        this.f1634b.m1600a(this.f1633a, messageObject.getId(), messageObject.getDialogId());
                    } else if (this.f1634b.f1645h == 3) {
                        this.f1634b.m1600a(this.f1633a, 0, this.f1634b.f1639b.f1672F);
                    }
                }
                if (this.f1634b.f1645h == 1 || this.f1634b.f1645h == 4) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.addAll(this.f1634b.f1639b.f1678L[this.f1634b.f1645h].f1656b);
                    Utilities.searchQueue.postRunnable(new AllMediaActivity(this, arrayList));
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.m.a.a.4 */
        class AllMediaActivity implements Runnable {
            final /* synthetic */ ArrayList f1635a;
            final /* synthetic */ AllMediaActivity f1636b;

            AllMediaActivity(AllMediaActivity allMediaActivity, ArrayList arrayList) {
                this.f1636b = allMediaActivity;
                this.f1635a = arrayList;
            }

            public void run() {
                this.f1636b.f1641d = this.f1635a;
                this.f1636b.notifyDataSetChanged();
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.m.a.a.5 */
        class AllMediaActivity implements SharedLinkCellDelegate {
            final /* synthetic */ AllMediaActivity f1637a;

            AllMediaActivity(AllMediaActivity allMediaActivity) {
                this.f1637a = allMediaActivity;
            }

            public boolean canPerformActions() {
                return !this.f1637a.f1639b.actionBar.isActionModeShowed();
            }

            public void needOpenWebView(WebPage webPage) {
                this.f1637a.f1639b.m1637a(webPage);
            }
        }

        public AllMediaActivity(AllMediaActivity allMediaActivity, Context context, int i) {
            this.f1639b = allMediaActivity;
            this.f1641d = new ArrayList();
            this.f1638a = new ArrayList();
            this.f1643f = 0;
            this.f1640c = context;
            this.f1645h = i;
        }

        private void m1593a(ArrayList<MessageObject> arrayList) {
            AndroidUtilities.runOnUIThread(new AllMediaActivity(this, arrayList));
        }

        private void m1596b(String str) {
            AndroidUtilities.runOnUIThread(new AllMediaActivity(this, str));
        }

        public MessageObject m1598a(int i) {
            return i < this.f1641d.size() ? (MessageObject) this.f1641d.get(i) : (MessageObject) this.f1638a.get(i - this.f1641d.size());
        }

        public void m1599a(String str) {
            try {
                if (this.f1642e != null) {
                    this.f1642e.cancel();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (str == null) {
                this.f1641d.clear();
                notifyDataSetChanged();
                return;
            }
            this.f1642e = new Timer();
            this.f1642e.schedule(new AllMediaActivity(this, str), 200, 300);
        }

        public void m1600a(String str, int i, long j) {
            int i2 = (int) j;
            if (i2 != 0) {
                if (this.f1643f != 0) {
                    ConnectionsManager.getInstance().cancelRequest(this.f1643f, true);
                    this.f1643f = 0;
                }
                if (str == null || str.length() == 0) {
                    this.f1638a.clear();
                    this.f1644g = 0;
                    notifyDataSetChanged();
                    return;
                }
                TLObject tL_messages_search = new TL_messages_search();
                tL_messages_search.offset = 0;
                tL_messages_search.limit = 50;
                tL_messages_search.max_id = i;
                if (this.f1645h == 1) {
                    tL_messages_search.filter = new TL_inputMessagesFilterDocument();
                } else if (this.f1645h == 3) {
                    tL_messages_search.filter = new TL_inputMessagesFilterUrl();
                } else if (this.f1645h == 4) {
                    tL_messages_search.filter = new TL_inputMessagesFilterMusic();
                }
                tL_messages_search.f2672q = str;
                tL_messages_search.peer = MessagesController.getInputPeer(i2);
                if (tL_messages_search.peer != null) {
                    i2 = this.f1644g + 1;
                    this.f1644g = i2;
                    this.f1643f = ConnectionsManager.getInstance().sendRequest(tL_messages_search, new AllMediaActivity(this, i, i2), 2);
                    ConnectionsManager.getInstance().bindRequestToGuid(this.f1643f, this.f1639b.classGuid);
                }
            }
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            int size = this.f1641d.size();
            int size2 = this.f1638a.size();
            return size2 != 0 ? size + size2 : size;
        }

        public /* synthetic */ Object getItem(int i) {
            return m1598a(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = true;
            View sharedDocumentCell;
            MessageObject a;
            boolean containsKey;
            if (this.f1645h == 1 || this.f1645h == 4) {
                sharedDocumentCell = view == null ? new SharedDocumentCell(this.f1640c) : view;
                SharedDocumentCell sharedDocumentCell2 = (SharedDocumentCell) sharedDocumentCell;
                a = m1598a(i);
                sharedDocumentCell2.setDocument(a, i != getCount() + -1);
                if (this.f1639b.actionBar.isActionModeShowed()) {
                    containsKey = this.f1639b.f1667A[a.getDialogId() == this.f1639b.f1672F ? 0 : 1].containsKey(Integer.valueOf(a.getId()));
                    if (this.f1639b.f1670D) {
                        z = false;
                    }
                    sharedDocumentCell2.setChecked(containsKey, z);
                    return sharedDocumentCell;
                }
                if (this.f1639b.f1670D) {
                    z = false;
                }
                sharedDocumentCell2.setChecked(false, z);
                return sharedDocumentCell;
            } else if (this.f1645h != 3) {
                return view;
            } else {
                if (view == null) {
                    sharedDocumentCell = new SharedLinkCell(this.f1640c);
                    ((SharedLinkCell) sharedDocumentCell).setDelegate(new AllMediaActivity(this));
                } else {
                    sharedDocumentCell = view;
                }
                SharedLinkCell sharedLinkCell = (SharedLinkCell) sharedDocumentCell;
                a = m1598a(i);
                sharedLinkCell.setLink(a, i != getCount() + -1);
                if (this.f1639b.actionBar.isActionModeShowed()) {
                    containsKey = this.f1639b.f1667A[a.getDialogId() == this.f1639b.f1672F ? 0 : 1].containsKey(Integer.valueOf(a.getId()));
                    if (this.f1639b.f1670D) {
                        z = false;
                    }
                    sharedLinkCell.setChecked(containsKey, z);
                    return sharedDocumentCell;
                }
                if (this.f1639b.f1670D) {
                    z = false;
                }
                sharedLinkCell.setChecked(false, z);
                return sharedDocumentCell;
            }
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isEmpty() {
            return this.f1641d.isEmpty() && this.f1638a.isEmpty();
        }

        public boolean isEnabled(int i) {
            return i != this.f1641d.size() + this.f1638a.size();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.b */
    private class AllMediaActivity extends BaseSectionsAdapter {
        final /* synthetic */ AllMediaActivity f1646a;
        private Context f1647b;
        private int f1648c;

        public AllMediaActivity(AllMediaActivity allMediaActivity, Context context, int i) {
            this.f1646a = allMediaActivity;
            this.f1647b = context;
            this.f1648c = i;
        }

        public int getCountForSection(int i) {
            return i < this.f1646a.f1678L[this.f1648c].f1658d.size() ? ((ArrayList) this.f1646a.f1678L[this.f1648c].f1659e.get(this.f1646a.f1678L[this.f1648c].f1658d.get(i))).size() + 1 : 1;
        }

        public Object getItem(int i, int i2) {
            return null;
        }

        public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
            if (i >= this.f1646a.f1678L[this.f1648c].f1658d.size()) {
                return view == null ? new LoadingCell(this.f1647b) : view;
            } else {
                ArrayList arrayList = (ArrayList) this.f1646a.f1678L[this.f1648c].f1659e.get((String) this.f1646a.f1678L[this.f1648c].f1658d.get(i));
                View greySectionCell;
                if (i2 == 0) {
                    greySectionCell = view == null ? new GreySectionCell(this.f1647b) : view;
                    ((GreySectionCell) greySectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) arrayList.get(0)).messageOwner.date).toUpperCase());
                    return greySectionCell;
                }
                greySectionCell = view == null ? new SharedDocumentCell(this.f1647b) : view;
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) greySectionCell;
                MessageObject messageObject = (MessageObject) arrayList.get(i2 - 1);
                boolean z = i2 != arrayList.size() || (i == this.f1646a.f1678L[this.f1648c].f1658d.size() - 1 && this.f1646a.f1678L[this.f1648c].f1661g);
                sharedDocumentCell.setDocument(messageObject, z);
                if (this.f1646a.actionBar.isActionModeShowed()) {
                    sharedDocumentCell.setChecked(this.f1646a.f1667A[messageObject.getDialogId() == this.f1646a.f1672F ? 0 : 1].containsKey(Integer.valueOf(messageObject.getId())), !this.f1646a.f1670D);
                    return greySectionCell;
                }
                sharedDocumentCell.setChecked(false, !this.f1646a.f1670D);
                return greySectionCell;
            }
        }

        public int getItemViewType(int i, int i2) {
            return i < this.f1646a.f1678L[this.f1648c].f1658d.size() ? i2 == 0 ? 0 : 1 : 2;
        }

        public int getSectionCount() {
            int i = 1;
            int size = this.f1646a.f1678L[this.f1648c].f1658d.size();
            if (this.f1646a.f1678L[this.f1648c].f1658d.isEmpty() || (this.f1646a.f1678L[this.f1648c].f1662h[0] && this.f1646a.f1678L[this.f1648c].f1662h[1])) {
                i = 0;
            }
            return i + size;
        }

        public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
            View greySectionCell = view == null ? new GreySectionCell(this.f1647b) : view;
            if (i < this.f1646a.f1678L[this.f1648c].f1658d.size()) {
                ((GreySectionCell) greySectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) ((ArrayList) this.f1646a.f1678L[this.f1648c].f1659e.get((String) this.f1646a.f1678L[this.f1648c].f1658d.get(i))).get(0)).messageOwner.date).toUpperCase());
            }
            return greySectionCell;
        }

        public int getViewTypeCount() {
            return 3;
        }

        public boolean isRowEnabled(int i, int i2) {
            return i2 != 0;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.c */
    private class AllMediaActivity extends BaseSectionsAdapter {
        final /* synthetic */ AllMediaActivity f1650a;
        private Context f1651b;

        /* renamed from: com.hanista.mobogram.mobo.m.a.c.1 */
        class AllMediaActivity implements SharedGifCell {
            final /* synthetic */ AllMediaActivity f1649a;

            AllMediaActivity(AllMediaActivity allMediaActivity) {
                this.f1649a = allMediaActivity;
            }

            public void didClickItem(com.hanista.mobogram.mobo.p009j.SharedGifCell sharedGifCell, int i, MessageObject messageObject, int i2) {
                this.f1649a.f1650a.m1634a(i, (View) sharedGifCell, messageObject, i2);
            }

            public boolean didLongClickItem(com.hanista.mobogram.mobo.p009j.SharedGifCell sharedGifCell, int i, MessageObject messageObject, int i2) {
                return this.f1649a.f1650a.m1638a(messageObject, sharedGifCell, i2);
            }
        }

        public AllMediaActivity(AllMediaActivity allMediaActivity, Context context) {
            this.f1650a = allMediaActivity;
            this.f1651b = context;
        }

        public int getCountForSection(int i) {
            return i < this.f1650a.f1678L[5].f1658d.size() ? ((int) Math.ceil((double) (((float) ((ArrayList) this.f1650a.f1678L[5].f1659e.get(this.f1650a.f1678L[5].f1658d.get(i))).size()) / ((float) this.f1650a.f1675I)))) + 1 : 1;
        }

        public Object getItem(int i, int i2) {
            return null;
        }

        public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
            if (i >= this.f1650a.f1678L[5].f1658d.size()) {
                return view == null ? new LoadingCell(this.f1651b) : view;
            } else {
                ArrayList arrayList = (ArrayList) this.f1650a.f1678L[5].f1659e.get((String) this.f1650a.f1678L[5].f1658d.get(i));
                View sharedMediaSectionCell;
                if (i2 == 0) {
                    sharedMediaSectionCell = view == null ? new SharedMediaSectionCell(this.f1651b) : view;
                    ((SharedMediaSectionCell) sharedMediaSectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) arrayList.get(0)).messageOwner.date).toUpperCase());
                    return sharedMediaSectionCell;
                }
                com.hanista.mobogram.mobo.p009j.SharedGifCell sharedGifCell;
                if (view == null) {
                    sharedMediaSectionCell = new com.hanista.mobogram.mobo.p009j.SharedGifCell(this.f1651b);
                    com.hanista.mobogram.mobo.p009j.SharedGifCell sharedGifCell2 = (com.hanista.mobogram.mobo.p009j.SharedGifCell) sharedMediaSectionCell;
                    sharedGifCell2.setDelegate(new AllMediaActivity(this));
                    view = sharedMediaSectionCell;
                    sharedGifCell = sharedGifCell2;
                } else {
                    sharedGifCell = (com.hanista.mobogram.mobo.p009j.SharedGifCell) view;
                }
                int K = this.f1650a.f1675I / 2;
                sharedGifCell.setItemsCount(K);
                for (int i3 = 0; i3 < K; i3++) {
                    int i4 = ((i2 - 1) * K) + i3;
                    if (i4 < arrayList.size()) {
                        MessageObject messageObject = (MessageObject) arrayList.get(i4);
                        sharedGifCell.setIsFirst(i2 == 1);
                        sharedGifCell.m1236a(i3, this.f1650a.f1678L[5].f1656b.indexOf(messageObject), messageObject);
                        if (this.f1650a.actionBar.isActionModeShowed()) {
                            sharedGifCell.m1237a(i3, this.f1650a.f1667A[messageObject.getDialogId() == this.f1650a.f1672F ? 0 : 1].containsKey(Integer.valueOf(messageObject.getId())), !this.f1650a.f1670D);
                        } else {
                            sharedGifCell.m1237a(i3, false, !this.f1650a.f1670D);
                        }
                    } else {
                        sharedGifCell.m1236a(i3, i4, null);
                    }
                }
                sharedGifCell.requestLayout();
                return view;
            }
        }

        public int getItemViewType(int i, int i2) {
            return i < this.f1650a.f1678L[5].f1658d.size() ? i2 == 0 ? 0 : 1 : 2;
        }

        public int getSectionCount() {
            int i = 1;
            int size = this.f1650a.f1678L[5].f1658d.size();
            if (this.f1650a.f1678L[5].f1658d.isEmpty() || (this.f1650a.f1678L[5].f1662h[0] && this.f1650a.f1678L[5].f1662h[1])) {
                i = 0;
            }
            return i + size;
        }

        public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
            View sharedMediaSectionCell;
            if (view == null) {
                sharedMediaSectionCell = new SharedMediaSectionCell(this.f1651b);
                sharedMediaSectionCell.setBackgroundColor(-1);
            } else {
                sharedMediaSectionCell = view;
            }
            if (i < this.f1650a.f1678L[5].f1658d.size()) {
                ((SharedMediaSectionCell) sharedMediaSectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) ((ArrayList) this.f1650a.f1678L[5].f1659e.get((String) this.f1650a.f1678L[5].f1658d.get(i))).get(0)).messageOwner.date).toUpperCase());
            }
            return sharedMediaSectionCell;
        }

        public int getViewTypeCount() {
            return 3;
        }

        public boolean isRowEnabled(int i, int i2) {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.d */
    private class AllMediaActivity extends BaseSectionsAdapter {
        final /* synthetic */ AllMediaActivity f1653a;
        private Context f1654b;

        /* renamed from: com.hanista.mobogram.mobo.m.a.d.1 */
        class AllMediaActivity implements SharedLinkCellDelegate {
            final /* synthetic */ AllMediaActivity f1652a;

            AllMediaActivity(AllMediaActivity allMediaActivity) {
                this.f1652a = allMediaActivity;
            }

            public boolean canPerformActions() {
                return !this.f1652a.f1653a.actionBar.isActionModeShowed();
            }

            public void needOpenWebView(WebPage webPage) {
                this.f1652a.f1653a.m1637a(webPage);
            }
        }

        public AllMediaActivity(AllMediaActivity allMediaActivity, Context context) {
            this.f1653a = allMediaActivity;
            this.f1654b = context;
        }

        public int getCountForSection(int i) {
            return i < this.f1653a.f1678L[3].f1658d.size() ? ((ArrayList) this.f1653a.f1678L[3].f1659e.get(this.f1653a.f1678L[3].f1658d.get(i))).size() + 1 : 1;
        }

        public Object getItem(int i, int i2) {
            return null;
        }

        public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
            if (i >= this.f1653a.f1678L[3].f1658d.size()) {
                return view == null ? new LoadingCell(this.f1654b) : view;
            } else {
                ArrayList arrayList = (ArrayList) this.f1653a.f1678L[3].f1659e.get((String) this.f1653a.f1678L[3].f1658d.get(i));
                View greySectionCell;
                if (i2 == 0) {
                    greySectionCell = view == null ? new GreySectionCell(this.f1654b) : view;
                    ((GreySectionCell) greySectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) arrayList.get(0)).messageOwner.date).toUpperCase());
                    return greySectionCell;
                }
                if (view == null) {
                    greySectionCell = new SharedLinkCell(this.f1654b);
                    ((SharedLinkCell) greySectionCell).setDelegate(new AllMediaActivity(this));
                } else {
                    greySectionCell = view;
                }
                SharedLinkCell sharedLinkCell = (SharedLinkCell) greySectionCell;
                MessageObject messageObject = (MessageObject) arrayList.get(i2 - 1);
                boolean z = i2 != arrayList.size() || (i == this.f1653a.f1678L[3].f1658d.size() - 1 && this.f1653a.f1678L[3].f1661g);
                sharedLinkCell.setLink(messageObject, z);
                if (this.f1653a.actionBar.isActionModeShowed()) {
                    sharedLinkCell.setChecked(this.f1653a.f1667A[messageObject.getDialogId() == this.f1653a.f1672F ? 0 : 1].containsKey(Integer.valueOf(messageObject.getId())), !this.f1653a.f1670D);
                    return greySectionCell;
                }
                sharedLinkCell.setChecked(false, !this.f1653a.f1670D);
                return greySectionCell;
            }
        }

        public int getItemViewType(int i, int i2) {
            return i < this.f1653a.f1678L[3].f1658d.size() ? i2 == 0 ? 0 : 1 : 2;
        }

        public int getSectionCount() {
            int i = 1;
            int size = this.f1653a.f1678L[3].f1658d.size();
            if (this.f1653a.f1678L[3].f1658d.isEmpty() || (this.f1653a.f1678L[3].f1662h[0] && this.f1653a.f1678L[3].f1662h[1])) {
                i = 0;
            }
            return i + size;
        }

        public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
            View greySectionCell = view == null ? new GreySectionCell(this.f1654b) : view;
            if (i < this.f1653a.f1678L[3].f1658d.size()) {
                ((GreySectionCell) greySectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) ((ArrayList) this.f1653a.f1678L[3].f1659e.get((String) this.f1653a.f1678L[3].f1658d.get(i))).get(0)).messageOwner.date).toUpperCase());
            }
            return greySectionCell;
        }

        public int getViewTypeCount() {
            return 3;
        }

        public boolean isRowEnabled(int i, int i2) {
            return i2 != 0;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.e */
    private class AllMediaActivity {
        final /* synthetic */ AllMediaActivity f1655a;
        private ArrayList<MessageObject> f1656b;
        private HashMap<Integer, MessageObject>[] f1657c;
        private ArrayList<String> f1658d;
        private HashMap<String, ArrayList<MessageObject>> f1659e;
        private int f1660f;
        private boolean f1661g;
        private boolean[] f1662h;
        private int[] f1663i;

        private AllMediaActivity(AllMediaActivity allMediaActivity) {
            this.f1655a = allMediaActivity;
            this.f1656b = new ArrayList();
            this.f1657c = new HashMap[]{new HashMap(), new HashMap()};
            this.f1658d = new ArrayList();
            this.f1659e = new HashMap();
            this.f1662h = new boolean[]{false, true};
            this.f1663i = new int[]{0, 0};
        }

        public boolean m1609a(int i, int i2) {
            MessageObject messageObject = (MessageObject) this.f1657c[i2].get(Integer.valueOf(i));
            if (messageObject == null) {
                return false;
            }
            String formatYearMonth = LocaleController.formatYearMonth((long) messageObject.messageOwner.date);
            ArrayList arrayList = (ArrayList) this.f1659e.get(formatYearMonth);
            if (arrayList == null) {
                return false;
            }
            arrayList.remove(messageObject);
            this.f1656b.remove(messageObject);
            this.f1657c[i2].remove(Integer.valueOf(messageObject.getId()));
            if (arrayList.isEmpty()) {
                this.f1659e.remove(formatYearMonth);
                this.f1658d.remove(formatYearMonth);
            }
            this.f1660f--;
            return true;
        }

        public boolean m1610a(MessageObject messageObject, boolean z, boolean z2) {
            if (this.f1657c[0].containsKey(Integer.valueOf(messageObject.getId()))) {
                return false;
            }
            if (z2) {
                this.f1663i[0] = Math.max(messageObject.getId(), this.f1663i[0]);
            } else if (messageObject.getId() > 0) {
                if (this.f1655a.f1672F != 0) {
                    this.f1663i[0] = Math.min(messageObject.getId(), this.f1663i[0]);
                } else {
                    this.f1663i[0] = Math.min(messageObject.messageOwner.date, this.f1663i[0]);
                }
            }
            if (!HiddenConfig.f1402e && HiddenConfig.m1399b(Long.valueOf(messageObject.messageOwner.dialog_id))) {
                return false;
            }
            if (this.f1655a.f1674H == 0 && this.f1655a.f1679M == 2 && !messageObject.isVideo()) {
                return false;
            }
            if (this.f1655a.f1674H == 0 && this.f1655a.f1679M == 1 && messageObject.isVideo()) {
                return false;
            }
            if (this.f1655a.f1674H == 5 && !messageObject.isGif() && !messageObject.isNewGif()) {
                return false;
            }
            if (this.f1655a.f1674H != 3) {
                if (this.f1655a.f1673G == 1) {
                    if (!FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                        return false;
                    }
                } else if (this.f1655a.f1673G == 2 && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                    return false;
                }
            }
            String formatYearMonth = LocaleController.formatYearMonth((long) messageObject.messageOwner.date);
            ArrayList arrayList = (ArrayList) this.f1659e.get(formatYearMonth);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.f1659e.put(formatYearMonth, arrayList);
                if (z) {
                    this.f1658d.add(0, formatYearMonth);
                } else {
                    this.f1658d.add(formatYearMonth);
                }
            }
            if (z) {
                arrayList.add(0, messageObject);
                this.f1656b.add(0, messageObject);
            } else {
                arrayList.add(messageObject);
                this.f1656b.add(messageObject);
            }
            this.f1657c[0].put(Integer.valueOf(messageObject.getId()), messageObject);
            return true;
        }

        public void m1611b(int i, int i2) {
            MessageObject messageObject = (MessageObject) this.f1657c[0].get(Integer.valueOf(i));
            if (messageObject != null) {
                this.f1657c[0].remove(Integer.valueOf(i));
                this.f1657c[0].put(Integer.valueOf(i2), messageObject);
                messageObject.messageOwner.id = i2;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.m.a.f */
    private class AllMediaActivity extends BaseSectionsAdapter {
        final /* synthetic */ AllMediaActivity f1665a;
        private Context f1666b;

        /* renamed from: com.hanista.mobogram.mobo.m.a.f.1 */
        class AllMediaActivity implements SharedPhotoVideoCellDelegate {
            final /* synthetic */ AllMediaActivity f1664a;

            AllMediaActivity(AllMediaActivity allMediaActivity) {
                this.f1664a = allMediaActivity;
            }

            public void didClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2) {
                this.f1664a.f1665a.m1634a(i, (View) sharedPhotoVideoCell, messageObject, i2);
            }

            public boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2) {
                return this.f1664a.f1665a.m1638a(messageObject, sharedPhotoVideoCell, i2);
            }
        }

        public AllMediaActivity(AllMediaActivity allMediaActivity, Context context) {
            this.f1665a = allMediaActivity;
            this.f1666b = context;
        }

        public int getCountForSection(int i) {
            return i < this.f1665a.f1678L[0].f1658d.size() ? ((int) Math.ceil((double) (((float) ((ArrayList) this.f1665a.f1678L[0].f1659e.get(this.f1665a.f1678L[0].f1658d.get(i))).size()) / ((float) this.f1665a.f1675I)))) + 1 : 1;
        }

        public Object getItem(int i, int i2) {
            return null;
        }

        public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
            if (i >= this.f1665a.f1678L[0].f1658d.size()) {
                return view == null ? new LoadingCell(this.f1666b) : view;
            } else {
                ArrayList arrayList = (ArrayList) this.f1665a.f1678L[0].f1659e.get((String) this.f1665a.f1678L[0].f1658d.get(i));
                View sharedMediaSectionCell;
                if (i2 == 0) {
                    sharedMediaSectionCell = view == null ? new SharedMediaSectionCell(this.f1666b) : view;
                    ((SharedMediaSectionCell) sharedMediaSectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) arrayList.get(0)).messageOwner.date).toUpperCase());
                    return sharedMediaSectionCell;
                }
                SharedPhotoVideoCell sharedPhotoVideoCell;
                if (view == null) {
                    if (this.f1665a.f1700u.isEmpty()) {
                        sharedMediaSectionCell = new SharedPhotoVideoCell(this.f1666b);
                    } else {
                        View view2 = (View) this.f1665a.f1700u.get(0);
                        this.f1665a.f1700u.remove(0);
                        sharedMediaSectionCell = view2;
                    }
                    SharedPhotoVideoCell sharedPhotoVideoCell2 = (SharedPhotoVideoCell) sharedMediaSectionCell;
                    sharedPhotoVideoCell2.setDelegate(new AllMediaActivity(this));
                    view = sharedMediaSectionCell;
                    sharedPhotoVideoCell = sharedPhotoVideoCell2;
                } else {
                    sharedPhotoVideoCell = (SharedPhotoVideoCell) view;
                }
                sharedPhotoVideoCell.setItemsCount(this.f1665a.f1675I);
                for (int i3 = 0; i3 < this.f1665a.f1675I; i3++) {
                    int K = ((i2 - 1) * this.f1665a.f1675I) + i3;
                    if (K < arrayList.size()) {
                        MessageObject messageObject = (MessageObject) arrayList.get(K);
                        sharedPhotoVideoCell.setIsFirst(i2 == 1);
                        sharedPhotoVideoCell.setItem(i3, this.f1665a.f1678L[0].f1656b.indexOf(messageObject), messageObject);
                        if (this.f1665a.actionBar.isActionModeShowed()) {
                            sharedPhotoVideoCell.setChecked(i3, this.f1665a.f1667A[messageObject.getDialogId() == this.f1665a.f1672F ? 0 : 1].containsKey(Integer.valueOf(messageObject.getId())), !this.f1665a.f1670D);
                        } else {
                            sharedPhotoVideoCell.setChecked(i3, false, !this.f1665a.f1670D);
                        }
                    } else {
                        sharedPhotoVideoCell.setItem(i3, K, null);
                    }
                }
                sharedPhotoVideoCell.requestLayout();
                return view;
            }
        }

        public int getItemViewType(int i, int i2) {
            return i < this.f1665a.f1678L[0].f1658d.size() ? i2 == 0 ? 0 : 1 : 2;
        }

        public int getSectionCount() {
            int i = 1;
            int size = this.f1665a.f1678L[0].f1658d.size();
            if (this.f1665a.f1678L[0].f1658d.isEmpty() || (this.f1665a.f1678L[0].f1662h[0] && this.f1665a.f1678L[0].f1662h[1])) {
                i = 0;
            }
            return i + size;
        }

        public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
            View sharedMediaSectionCell;
            if (view == null) {
                sharedMediaSectionCell = new SharedMediaSectionCell(this.f1666b);
                sharedMediaSectionCell.setBackgroundColor(-1);
            } else {
                sharedMediaSectionCell = view;
            }
            if (i < this.f1665a.f1678L[0].f1658d.size()) {
                ((SharedMediaSectionCell) sharedMediaSectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) ((ArrayList) this.f1665a.f1678L[0].f1659e.get((String) this.f1665a.f1678L[0].f1658d.get(i))).get(0)).messageOwner.date).toUpperCase());
            }
            return sharedMediaSectionCell;
        }

        public int getViewTypeCount() {
            return 3;
        }

        public boolean isRowEnabled(int i, int i2) {
            return false;
        }
    }

    public AllMediaActivity(Bundle bundle) {
        super(bundle);
        this.f1700u = new ArrayList(6);
        this.f1667A = new HashMap[]{new HashMap(), new HashMap()};
        this.f1669C = new ArrayList();
        this.f1680a = null;
        this.f1675I = 4;
        this.f1678L = new AllMediaActivity[6];
    }

    private void m1633a() {
        this.actionBar.hideActionMode();
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setItems(new CharSequence[]{LocaleController.getString("AllChats", C0338R.string.AllChats), LocaleController.getString("SelectChat", C0338R.string.SelectChat)}, new AllMediaActivity(this));
        showDialog(builder.create());
    }

    private void m1634a(int i, View view, MessageObject messageObject, int i2) {
        long j = 0;
        if (messageObject != null) {
            if (this.actionBar.isActionModeShowed()) {
                int i3 = messageObject.getDialogId() == this.f1672F ? 0 : 1;
                if (this.f1667A[i3].containsKey(Integer.valueOf(messageObject.getId()))) {
                    this.f1667A[i3].remove(Integer.valueOf(messageObject.getId()));
                    if (!messageObject.canDeleteMessage(null)) {
                        this.f1668B--;
                    }
                } else {
                    this.f1667A[i3].put(Integer.valueOf(messageObject.getId()), messageObject);
                    if (!messageObject.canDeleteMessage(null)) {
                        this.f1668B++;
                    }
                }
                if (this.f1667A[0].isEmpty() && this.f1667A[1].isEmpty()) {
                    this.actionBar.hideActionMode();
                } else {
                    this.f1699t.setNumber(this.f1667A[0].size() + this.f1667A[1].size(), true);
                }
                this.actionBar.createActionMode().getItem(8).setVisibility(this.f1668B == 0 ? 0 : 8);
                this.f1670D = false;
                if (view instanceof SharedDocumentCell) {
                    ((SharedDocumentCell) view).setChecked(this.f1667A[i3].containsKey(Integer.valueOf(messageObject.getId())), true);
                } else if (view instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) view).setChecked(i2, this.f1667A[i3].containsKey(Integer.valueOf(messageObject.getId())), true);
                } else if (view instanceof com.hanista.mobogram.mobo.p009j.SharedGifCell) {
                    ((com.hanista.mobogram.mobo.p009j.SharedGifCell) view).m1237a(i2, this.f1667A[i3].containsKey(Integer.valueOf(messageObject.getId())), true);
                } else if (view instanceof SharedLinkCell) {
                    ((SharedLinkCell) view).setChecked(this.f1667A[i3].containsKey(Integer.valueOf(messageObject.getId())), true);
                }
                m1663j();
                m1667l();
                m1669m();
            } else if (this.f1674H == 0) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().openPhoto(this.f1678L[this.f1674H].f1656b, i, this.f1672F, this.f1671E, this);
            } else if (this.f1674H == 1 || this.f1674H == 4) {
                if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
                    if (sharedDocumentCell.isLoaded()) {
                        if (!messageObject.isMusic() || !MediaController.m71a().m160a(this.f1678L[this.f1674H].f1656b, messageObject)) {
                            r0 = messageObject.messageOwner.media != null ? FileLoader.getAttachFileName(messageObject.getDocument()) : TtmlNode.ANONYMOUS_REGION_ID;
                            File file = (messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0) ? null : new File(messageObject.messageOwner.attachPath);
                            File pathToMessage = (file == null || !(file == null || file.exists())) ? FileLoader.getPathToMessage(messageObject.messageOwner) : file;
                            if (pathToMessage != null && pathToMessage.exists()) {
                                try {
                                    String mimeTypeFromExtension;
                                    Intent intent = new Intent("android.intent.action.VIEW");
                                    intent.setFlags(1);
                                    MimeTypeMap singleton = MimeTypeMap.getSingleton();
                                    int lastIndexOf = r0.lastIndexOf(46);
                                    if (lastIndexOf != -1) {
                                        mimeTypeFromExtension = singleton.getMimeTypeFromExtension(r0.substring(lastIndexOf + 1).toLowerCase());
                                        if (mimeTypeFromExtension == null) {
                                            mimeTypeFromExtension = messageObject.getDocument().mime_type;
                                            if (mimeTypeFromExtension == null || mimeTypeFromExtension.length() == 0) {
                                                mimeTypeFromExtension = null;
                                            }
                                        }
                                    } else {
                                        mimeTypeFromExtension = null;
                                    }
                                    if (VERSION.SDK_INT >= 24) {
                                        intent.setDataAndType(FileProvider.getUriForFile(getParentActivity(), "com.hanista.mobogram.provider", pathToMessage), mimeTypeFromExtension != null ? mimeTypeFromExtension : "text/plain");
                                    } else {
                                        intent.setDataAndType(Uri.fromFile(pathToMessage), mimeTypeFromExtension != null ? mimeTypeFromExtension : "text/plain");
                                    }
                                    if (mimeTypeFromExtension != null) {
                                        try {
                                            getParentActivity().startActivityForResult(intent, 500);
                                            return;
                                        } catch (Exception e) {
                                            if (VERSION.SDK_INT >= 24) {
                                                intent.setDataAndType(FileProvider.getUriForFile(getParentActivity(), "com.hanista.mobogram.provider", pathToMessage), "text/plain");
                                            } else {
                                                intent.setDataAndType(Uri.fromFile(pathToMessage), "text/plain");
                                            }
                                            getParentActivity().startActivityForResult(intent, 500);
                                            return;
                                        }
                                    }
                                    getParentActivity().startActivityForResult(intent, 500);
                                } catch (Exception e2) {
                                    if (getParentActivity() != null) {
                                        Builder builder = new Builder(getParentActivity());
                                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                                        builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", C0338R.string.NoHandleAppInstalled, messageObject.getDocument().mime_type));
                                        showDialog(builder.create());
                                    }
                                }
                            }
                        }
                    } else if (sharedDocumentCell.isLoading()) {
                        FileLoader.getInstance().cancelLoadFile(sharedDocumentCell.getMessage().getDocument());
                        sharedDocumentCell.updateFileExistIcon();
                    } else {
                        FileLoader.getInstance().loadFile(sharedDocumentCell.getMessage().getDocument(), false, false);
                        sharedDocumentCell.updateFileExistIcon();
                    }
                }
            } else if (this.f1674H == 3) {
                try {
                    WebPage webPage = messageObject.messageOwner.media.webpage;
                    if (webPage == null || (webPage instanceof TL_webPageEmpty)) {
                        r0 = null;
                    } else if (VERSION.SDK_INT < 16 || webPage.embed_url == null || webPage.embed_url.length() == 0) {
                        r0 = webPage.url;
                    } else {
                        m1637a(webPage);
                        return;
                    }
                    if (r0 == null) {
                        r0 = ((SharedLinkCell) view).getLink(0);
                    }
                    if (r0 != null) {
                        Browser.openUrl(getParentActivity(), r0);
                    }
                } catch (Throwable e3) {
                    FileLog.m18e("tmessages", e3);
                }
            } else if (this.f1674H == 5) {
                messageObject.checkMediaExistance();
                PhotoViewer instance;
                long j2;
                if (messageObject.mediaExists) {
                    instance = PhotoViewer.getInstance();
                    j2 = messageObject.type != 0 ? this.f1672F : 0;
                    if (messageObject.type != 0) {
                        j = this.f1671E;
                    }
                    instance.openPhoto(messageObject, j2, j, this);
                    return;
                }
                ((com.hanista.mobogram.mobo.p009j.SharedGifCell) view).m1239b(i2, i, messageObject);
                instance = PhotoViewer.getInstance();
                j2 = messageObject.type != 0 ? this.f1672F : 0;
                if (messageObject.type != 0) {
                    j = this.f1671E;
                }
                instance.openPhoto(messageObject, j2, j, this);
                PhotoViewer.getInstance().openGif(messageObject, FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80), null);
            }
        }
    }

    private void m1637a(WebPage webPage) {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setCustomView(new WebFrameLayout(getParentActivity(), builder.create(), webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height));
        builder.setUseFullWidth(true);
        showDialog(builder.create());
    }

    private boolean m1638a(MessageObject messageObject, View view, int i) {
        if (this.actionBar.isActionModeShowed()) {
            return false;
        }
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.f1667A[messageObject.getDialogId() == this.f1672F ? 0 : 1].put(Integer.valueOf(messageObject.getId()), messageObject);
        if (!messageObject.canDeleteMessage(null)) {
            this.f1668B++;
        }
        this.actionBar.createActionMode().getItem(8).setVisibility(this.f1668B == 0 ? 0 : 8);
        this.f1699t.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        Collection arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.f1669C.size(); i2++) {
            View view2 = (View) this.f1669C.get(i2);
            AndroidUtilities.clearDrawableAnimation(view2);
            arrayList.add(ObjectAnimator.ofFloat(view2, "scaleY", new float[]{0.1f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(250);
        animatorSet.start();
        this.f1670D = false;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell) {
            ((SharedPhotoVideoCell) view).setChecked(i, true, true);
        } else if (view instanceof SharedLinkCell) {
            ((SharedLinkCell) view).setChecked(true, true);
        } else if (view instanceof com.hanista.mobogram.mobo.p009j.SharedGifCell) {
            ((com.hanista.mobogram.mobo.p009j.SharedGifCell) view).m1237a(i, true, true);
        }
        this.actionBar.showActionMode();
        m1663j();
        m1667l();
        m1669m();
        return true;
    }

    private void m1643b() {
        this.actionBar.hideActionMode();
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setItems(new CharSequence[]{LocaleController.getString("All", C0338R.string.All), LocaleController.getString("Downloaded", C0338R.string.Downloaded), LocaleController.getString("NotDownloaded", C0338R.string.NotDownloaded)}, new AllMediaActivity(this));
        showDialog(builder.create());
    }

    private void m1647c() {
        this.f1678L = new AllMediaActivity[6];
        for (int i = 0; i < this.f1678L.length; i++) {
            this.f1678L[i] = new AllMediaActivity();
            this.f1678L[i].f1663i[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            if (!(this.f1671E == 0 || this.f1680a == null)) {
                this.f1678L[i].f1663i[1] = this.f1680a.migrated_from_max_id;
                this.f1678L[i].f1662h[1] = false;
            }
        }
        this.f1678L[0].f1661g = true;
        this.f1681b = new AllMediaActivity(this, getParentActivity());
        this.f1682c = new AllMediaActivity(this, getParentActivity());
        this.f1684e = new AllMediaActivity(this, getParentActivity());
        this.f1683d = new AllMediaActivity(this, getParentActivity());
        this.f1686g = new AllMediaActivity(this, getParentActivity(), 1);
        this.f1687h = new AllMediaActivity(this, getParentActivity(), 4);
        this.f1688i = new AllMediaActivity(this, getParentActivity(), 1);
        this.f1689j = new AllMediaActivity(this, getParentActivity(), 4);
        this.f1690k = new AllMediaActivity(this, getParentActivity(), 3);
        this.f1685f = new AllMediaActivity(this, getParentActivity());
        m1654e();
        if (this.f1674H != 5) {
            if (this.f1672F != 0) {
                SharedMediaQuery.loadMedia(this.f1672F, 0, 50, 0, 0, true, this.classGuid);
            } else {
                AllSharedMediaQuery.m1688a(0, 50, ConnectionsManager.DEFAULT_DATACENTER_ID, 0, true, this.classGuid);
            }
        }
        m1660h();
    }

    private void m1651d() {
        if (this.f1672F == 0) {
            this.f1702w.setText(LocaleController.getString("AllChats", C0338R.string.AllChats));
        } else if (this.f1672F < 0) {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) this.f1672F)));
            if (chat != null) {
                this.f1702w.setText(chat.title);
            }
        } else {
            User user = MessagesController.getInstance().getUser(Integer.valueOf((int) this.f1672F));
            if (user != null) {
                this.f1702w.setText(UserObject.getUserName(user));
            }
        }
        if (this.f1673G == 1) {
            this.f1703x.setText(LocaleController.getString("Downloaded", C0338R.string.Downloaded));
        } else if (this.f1673G == 2) {
            this.f1703x.setText(LocaleController.getString("NotDownloaded", C0338R.string.NotDownloaded));
        } else {
            this.f1703x.setText(LocaleController.getString("All", C0338R.string.All));
        }
        if (this.f1674H == 3) {
            this.f1703x.setVisibility(8);
            this.f1677K.setVisibility(8);
            return;
        }
        this.f1703x.setVisibility(0);
        this.f1677K.setVisibility(0);
    }

    private void m1654e() {
        if (this.f1705z && this.f1704y) {
            if (this.f1691l != null) {
                if (this.f1674H == 1) {
                    this.f1691l.setAdapter(this.f1688i);
                    this.f1688i.notifyDataSetChanged();
                } else if (this.f1674H == 3) {
                    this.f1691l.setAdapter(this.f1690k);
                    this.f1690k.notifyDataSetChanged();
                } else if (this.f1674H == 4) {
                    this.f1691l.setAdapter(this.f1689j);
                    this.f1689j.notifyDataSetChanged();
                }
            }
            if (this.f1693n != null) {
                this.f1693n.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
                this.f1693n.setTextSize(1, 20.0f);
                this.f1694o.setVisibility(8);
            }
        } else {
            this.f1693n.setTextSize(1, 17.0f);
            this.f1694o.setVisibility(0);
            if (this.f1674H == 0) {
                if (this.f1679M == 0) {
                    this.f1691l.setAdapter(this.f1681b);
                    this.f1696q.setText(LocaleController.getString("Medias", C0338R.string.Medias));
                } else if (this.f1679M == 1) {
                    this.f1691l.setAdapter(this.f1682c);
                    this.f1696q.setText(LocaleController.getString("Photos", C0338R.string.Photos));
                } else if (this.f1679M == 3) {
                    this.f1691l.setAdapter(this.f1684e);
                    this.f1696q.setText(LocaleController.getString("Gifs", C0338R.string.Gifs));
                } else {
                    this.f1691l.setAdapter(this.f1683d);
                    this.f1696q.setText(LocaleController.getString("Videos", C0338R.string.Videos));
                }
                this.f1694o.setImageResource(C0338R.drawable.tip1);
                this.f1693n.setText(LocaleController.getString("NoMedia", C0338R.string.NoMedia));
                this.f1698s.setVisibility(8);
                if (this.f1678L[this.f1674H].f1661g && this.f1678L[this.f1674H].f1656b.isEmpty()) {
                    this.f1692m.setVisibility(0);
                    this.f1691l.setEmptyView(null);
                    this.f1695p.setVisibility(8);
                } else {
                    this.f1692m.setVisibility(8);
                    this.f1691l.setEmptyView(this.f1695p);
                }
                this.f1691l.setVisibility(0);
                this.f1691l.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
            } else if (this.f1674H == 1 || this.f1674H == 4) {
                if (this.f1674H == 1) {
                    this.f1691l.setAdapter(this.f1686g);
                    this.f1696q.setText(LocaleController.getString("Files", C0338R.string.Files));
                    this.f1694o.setImageResource(C0338R.drawable.tip2);
                    this.f1693n.setText(LocaleController.getString("NoSharedFiles", C0338R.string.NoSharedFiles));
                } else if (this.f1674H == 4) {
                    this.f1691l.setAdapter(this.f1687h);
                    this.f1696q.setText(LocaleController.getString("Musics", C0338R.string.Musics));
                    this.f1694o.setImageResource(C0338R.drawable.tip4);
                    this.f1693n.setText(LocaleController.getString("NoSharedAudio", C0338R.string.NoSharedAudio));
                }
                this.f1698s.setVisibility(!this.f1678L[this.f1674H].f1656b.isEmpty() ? 0 : 8);
                if (!(this.f1678L[this.f1674H].f1661g || this.f1678L[this.f1674H].f1662h[0] || !this.f1678L[this.f1674H].f1656b.isEmpty())) {
                    this.f1678L[this.f1674H].f1661g = true;
                    if (this.f1672F != 0) {
                        SharedMediaQuery.loadMedia(this.f1672F, 0, 50, 0, this.f1674H == 1 ? 1 : 4, true, this.classGuid);
                    } else {
                        AllSharedMediaQuery.m1688a(0, 50, ConnectionsManager.DEFAULT_DATACENTER_ID, this.f1674H == 1 ? 1 : 4, true, this.classGuid);
                    }
                }
                this.f1691l.setVisibility(0);
                if (this.f1678L[this.f1674H].f1661g && this.f1678L[this.f1674H].f1656b.isEmpty()) {
                    this.f1692m.setVisibility(0);
                    this.f1691l.setEmptyView(null);
                    this.f1695p.setVisibility(8);
                } else {
                    this.f1692m.setVisibility(8);
                    this.f1691l.setEmptyView(this.f1695p);
                }
                this.f1691l.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
            } else if (this.f1674H == 3) {
                this.f1691l.setAdapter(this.f1685f);
                this.f1696q.setText(LocaleController.getString("Links", C0338R.string.Links));
                this.f1694o.setImageResource(C0338R.drawable.tip3);
                this.f1693n.setText(LocaleController.getString("NoSharedLinks", C0338R.string.NoSharedLinks));
                this.f1698s.setVisibility(!this.f1678L[3].f1656b.isEmpty() ? 0 : 8);
                if (!(this.f1678L[this.f1674H].f1661g || this.f1678L[this.f1674H].f1662h[0] || !this.f1678L[this.f1674H].f1656b.isEmpty())) {
                    this.f1678L[this.f1674H].f1661g = true;
                    if (this.f1672F != 0) {
                        SharedMediaQuery.loadMedia(this.f1672F, 0, 50, 0, 3, true, this.classGuid);
                    } else {
                        AllSharedMediaQuery.m1688a(0, 50, ConnectionsManager.DEFAULT_DATACENTER_ID, 3, true, this.classGuid);
                    }
                }
                this.f1691l.setVisibility(0);
                if (this.f1678L[this.f1674H].f1661g && this.f1678L[this.f1674H].f1656b.isEmpty()) {
                    this.f1692m.setVisibility(0);
                    this.f1691l.setEmptyView(null);
                    this.f1695p.setVisibility(8);
                } else {
                    this.f1692m.setVisibility(8);
                    this.f1691l.setEmptyView(this.f1695p);
                }
                this.f1691l.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
            } else if (this.f1674H == 5) {
                this.f1691l.setAdapter(this.f1684e);
                this.f1696q.setText(LocaleController.getString("Gifs", C0338R.string.Gifs));
                this.f1694o.setImageResource(C0338R.drawable.tip1);
                if (((int) this.f1672F) == 0) {
                    this.f1693n.setText(LocaleController.getString("NoMediaSecret", C0338R.string.NoMediaSecret));
                } else {
                    this.f1693n.setText(LocaleController.getString("NoSharedGif", C0338R.string.NoSharedGif));
                }
                this.f1698s.setVisibility(8);
                if (!(this.f1678L[this.f1674H].f1661g || this.f1678L[this.f1674H].f1662h[0] || !this.f1678L[this.f1674H].f1656b.isEmpty())) {
                    this.f1678L[this.f1674H].f1661g = true;
                    if (this.f1672F != 0) {
                        SharedMediaQuery.loadMedia(this.f1672F, 0, 50, 0, 5, true, this.classGuid);
                    } else {
                        AllSharedMediaQuery.m1688a(0, 50, ConnectionsManager.DEFAULT_DATACENTER_ID, 5, true, this.classGuid);
                    }
                }
                if (this.f1678L[this.f1674H].f1661g && this.f1678L[this.f1674H].f1656b.isEmpty()) {
                    this.f1692m.setVisibility(0);
                    this.f1691l.setEmptyView(null);
                    this.f1695p.setVisibility(8);
                } else {
                    this.f1692m.setVisibility(8);
                    this.f1691l.setEmptyView(this.f1695p);
                }
                this.f1691l.setVisibility(0);
                this.f1691l.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
            }
        }
        m1651d();
    }

    private void m1655f() {
        int i = 0;
        if (this.f1691l != null) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.f1699t.setTextSize(18);
            } else {
                this.f1699t.setTextSize(16);
            }
            if (AndroidUtilities.isTablet()) {
                this.f1675I = 4;
                this.f1693n.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            } else if (rotation == 3 || rotation == 1) {
                this.f1675I = 6;
                this.f1693n.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
            } else {
                this.f1675I = 4;
                this.f1693n.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            }
            this.f1681b.notifyDataSetChanged();
            this.f1682c.notifyDataSetChanged();
            this.f1684e.notifyDataSetChanged();
            this.f1683d.notifyDataSetChanged();
            if (this.f1697r != null) {
                if (!AndroidUtilities.isTablet()) {
                    LayoutParams layoutParams = (LayoutParams) this.f1697r.getLayoutParams();
                    if (VERSION.SDK_INT >= 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    layoutParams.topMargin = i;
                    this.f1697r.setLayoutParams(layoutParams);
                }
                if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                    this.f1696q.setTextSize(20.0f);
                } else {
                    this.f1696q.setTextSize(18.0f);
                }
            }
        }
    }

    private void m1658g() {
        ArrayList arrayList = new ArrayList();
        for (int i = 1; i >= 0; i--) {
            Object arrayList2 = new ArrayList(this.f1667A[i].keySet());
            Collections.sort(arrayList2);
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                Integer num = (Integer) it.next();
                if (num.intValue() > 0) {
                    arrayList.add(this.f1667A[i].get(num));
                }
            }
            this.f1667A[i].clear();
        }
        this.f1668B = 0;
        this.actionBar.hideActionMode();
        showDialog(new ShareAlert(getParentActivity(), arrayList));
        m1660h();
    }

    private void m1660h() {
        if (this.f1681b != null) {
            this.f1681b.notifyDataSetChanged();
        }
        if (this.f1682c != null) {
            this.f1682c.notifyDataSetChanged();
        }
        if (this.f1684e != null) {
            this.f1684e.notifyDataSetChanged();
        }
        if (this.f1683d != null) {
            this.f1683d.notifyDataSetChanged();
        }
        if (this.f1686g != null) {
            this.f1686g.notifyDataSetChanged();
        }
        if (this.f1685f != null) {
            this.f1685f.notifyDataSetChanged();
        }
        if (this.f1687h != null) {
            this.f1687h.notifyDataSetChanged();
        }
        m1651d();
    }

    private void m1661i() {
        ArrayList arrayList = new ArrayList();
        for (int i = 1; i >= 0; i--) {
            Object arrayList2 = new ArrayList(this.f1667A[i].keySet());
            Collections.sort(arrayList2);
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                Integer num = (Integer) it.next();
                if (num.intValue() > 0) {
                    arrayList.add(this.f1667A[i].get(num));
                }
            }
            this.f1667A[i].clear();
        }
        messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = new messages_Messages();
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.add(((MessageObject) it2.next()).messageOwner);
        }
        DownloadMessagesStorage.m783a().m811a(com_hanista_mobogram_tgnet_TLRPC_messages_Messages, 1, -1, 0, 0, false);
        Toast.makeText(getParentActivity(), LocaleController.getString("FilesAddedToDownloadList", C0338R.string.FilesAddedToDownloadList), 0).show();
        this.f1668B = 0;
        this.actionBar.hideActionMode();
        m1660h();
    }

    private void m1663j() {
        ActionBarMenuItem item = this.actionBar.createActionMode().getItem(190);
        if (item != null) {
            item.setVisibility(0);
            for (int i = 1; i >= 0; i--) {
                Object arrayList = new ArrayList(this.f1667A[i].keySet());
                Collections.sort(arrayList);
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    Integer num = (Integer) it.next();
                    if (num.intValue() > 0) {
                        MessageObject messageObject = (MessageObject) this.f1667A[i].get(num);
                        if (!(messageObject == null || num.intValue() <= 0 || DownloadUtil.m824a(messageObject))) {
                            item.setVisibility(8);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void m1665k() {
        for (int i = 1; i >= 0; i--) {
            ArrayList arrayList = new ArrayList(this.f1667A[i].keySet());
            Collections.sort(arrayList);
            if (arrayList.size() > 1) {
                int i2;
                MessageObject messageObject;
                List arrayList2 = new ArrayList();
                for (i2 = 0; i2 < this.f1678L[this.f1674H].f1656b.size(); i2++) {
                    messageObject = (MessageObject) this.f1678L[this.f1674H].f1656b.get(i2);
                    if (messageObject.getId() == ((Integer) arrayList.get(0)).intValue() || messageObject.getId() == ((Integer) arrayList.get(arrayList.size() - 1)).intValue()) {
                        arrayList2.add(Integer.valueOf(i2));
                        if (arrayList2.size() == 2) {
                            break;
                        }
                    }
                }
                if (arrayList2.size() == 2) {
                    for (int intValue = ((Integer) arrayList2.get(0)).intValue() + 1; intValue < ((Integer) arrayList2.get(1)).intValue(); intValue++) {
                        messageObject = (MessageObject) this.f1678L[this.f1674H].f1656b.get(intValue);
                        if (messageObject.getId() > 0) {
                            if (!this.f1667A[messageObject.getDialogId() == this.f1672F ? 0 : 1].containsKey(Integer.valueOf(messageObject.getId()))) {
                                i2 = messageObject.getDialogId() == this.f1672F ? 0 : 1;
                                if (this.f1667A[i2].containsKey(Integer.valueOf(messageObject.getId()))) {
                                    this.f1667A[i2].remove(Integer.valueOf(messageObject.getId()));
                                    if (!messageObject.canDeleteMessage(null)) {
                                        this.f1668B--;
                                    }
                                } else {
                                    this.f1667A[i2].put(Integer.valueOf(messageObject.getId()), messageObject);
                                    if (!messageObject.canDeleteMessage(null)) {
                                        this.f1668B++;
                                    }
                                }
                                if (this.f1667A[0].isEmpty() && this.f1667A[1].isEmpty()) {
                                    this.actionBar.hideActionMode();
                                } else {
                                    this.f1699t.setNumber(this.f1667A[0].size() + this.f1667A[1].size(), true);
                                }
                                this.actionBar.createActionMode().getItem(8).setVisibility(this.f1668B == 0 ? 0 : 8);
                            }
                        }
                    }
                    m1660h();
                } else {
                    return;
                }
            }
        }
        ActionBarMenuItem item = this.actionBar.createActionMode().getItem(191);
        if (item != null) {
            item.setVisibility(8);
        }
    }

    private void m1667l() {
        ActionBarMenuItem item = this.actionBar.createActionMode().getItem(191);
        if (item != null) {
            item.setVisibility(8);
            for (int i = 1; i >= 0; i--) {
                if (new ArrayList(this.f1667A[i].keySet()).size() > 1) {
                    item.setVisibility(0);
                    return;
                }
            }
        }
    }

    private void m1669m() {
        try {
            ActionBarMenu createActionMode = this.actionBar.createActionMode();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getParentActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int i = 0;
            int i2 = 0;
            while (i < createActionMode.getChildCount()) {
                View childAt = createActionMode.getChildAt(i);
                i++;
                i2 = childAt.getVisibility() == 0 ? childAt.getLayoutParams().width + i2 : i2;
            }
            LayoutParams layoutParams = (LayoutParams) createActionMode.getLayoutParams();
            layoutParams.height = -1;
            layoutParams.width = -1;
            if (((float) i2) > ((float) displayMetrics.widthPixels) / displayMetrics.density) {
                layoutParams.gravity = 3;
            } else {
                layoutParams.gravity = 5;
            }
            createActionMode.setLayoutParams(layoutParams);
        } catch (Exception e) {
        }
    }

    private void m1671n() {
        try {
            MaterialHelperUtil.m1362a(getParentActivity(), this.f1697r, this.f1677K, this.actionBar.createMenu().getItem(51));
        } catch (Exception e) {
        }
    }

    private void m1674o() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2507r;
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_menu_download);
            if (drawable != null) {
                drawable.setColorFilter(i, Mode.MULTIPLY);
            }
            this.f1703x.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_dialog);
            if (drawable != null) {
                drawable.setColorFilter(i, Mode.MULTIPLY);
            }
            this.f1702w.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            this.f1701v.setBackgroundColor(AdvanceTheme.m2276a(AdvanceTheme.f2491b, -16));
            this.f1702w.setTextColor(i);
            this.f1703x.setTextColor(i);
        }
    }

    public boolean allowCaption() {
        return true;
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public View createView(Context context) {
        int i;
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setTitle(TtmlNode.ANONYMOUS_REGION_ID);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new AllMediaActivity(this));
        for (i = 1; i >= 0; i--) {
            this.f1667A[i].clear();
        }
        this.f1668B = 0;
        this.f1669C.clear();
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_menu_download);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            this.f1677K = createMenu.addItemWithWidth(52, drawable, AndroidUtilities.dp(56.0f));
            drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_dialog);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            createMenu.addItemWithWidth(51, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.f1677K = createMenu.addItem(52, C0338R.drawable.ic_menu_download, AndroidUtilities.dp(56.0f));
            createMenu.addItem(51, C0338R.drawable.ic_dialog, AndroidUtilities.dp(56.0f));
        }
        this.f1698s = createMenu.addItem(0, (int) C0338R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new AllMediaActivity(this));
        this.f1698s.getSearchField().setHint(LocaleController.getString("Search", C0338R.string.Search));
        this.f1698s.setVisibility(8);
        this.f1697r = new ActionBarMenuItem(context, createMenu, ThemeUtil.m2485a().m2294h());
        this.f1697r.setSubMenuOpenSide(1);
        this.f1697r.addSubItem(1, LocaleController.getString("Medias", C0338R.string.Medias), 0);
        this.f1697r.addSubItem(2, LocaleController.getString("Photos", C0338R.string.Photos), 0);
        this.f1697r.addSubItem(3, LocaleController.getString("Videos", C0338R.string.Videos), 0);
        this.f1697r.addSubItem(83, LocaleController.getString("Gifs", C0338R.string.Gifs), 0);
        this.f1697r.addSubItem(4, LocaleController.getString("Files", C0338R.string.Files), 0);
        this.f1697r.addSubItem(5, LocaleController.getString("Links", C0338R.string.Links), 0);
        this.f1697r.addSubItem(6, LocaleController.getString("Musics", C0338R.string.Musics), 0);
        this.actionBar.addView(this.f1697r, 1, LayoutHelper.createFrame(-2, Face.UNCOMPUTED_PROBABILITY, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
        this.f1697r.setOnClickListener(new AllMediaActivity(this));
        this.f1696q = new TextView(context);
        this.f1696q.setGravity(3);
        this.f1696q.setSingleLine(true);
        this.f1696q.setLines(1);
        this.f1696q.setMaxLines(1);
        this.f1696q.setEllipsize(TruncateAt.END);
        this.f1696q.setTextColor(-1);
        this.f1696q.setTypeface(FontUtil.m1176a().m1160c());
        this.f1696q.setCompoundDrawablesWithIntrinsicBounds(0, 0, C0338R.drawable.ic_arrow_drop_down, 0);
        this.f1696q.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.f1696q.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
        this.f1697r.addView(this.f1696q, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        this.f1699t = new NumberTextView(createActionMode.getContext());
        this.f1699t.setTextSize(18);
        this.f1699t.setTypeface(FontUtil.m1176a().m1160c());
        this.f1699t.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        this.f1699t.setMinimumWidth(AndroidUtilities.dp(22.0f));
        this.f1699t.setOnTouchListener(new AllMediaActivity(this));
        createActionMode.addView(this.f1699t, LayoutHelper.createLinear(0, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 65, 0, 0, 0));
        this.f1669C.add(createActionMode.addItem(191, C0338R.drawable.ic_ab_select_all, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.f1669C.add(createActionMode.addItem(190, C0338R.drawable.ic_ab_download, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.f1669C.add(createActionMode.addItem(112, C0338R.drawable.ic_ab_fwd_multiforward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.f1669C.add(createActionMode.addItem(7, C0338R.drawable.ic_ab_fwd_quoteforward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.f1669C.add(createActionMode.addItem(111, C0338R.drawable.ic_ab_fwd_forward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.f1669C.add(createActionMode.addItem(8, C0338R.drawable.ic_ab_fwd_delete, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.f1681b = new AllMediaActivity(this, context);
        this.f1682c = new AllMediaActivity(this, context);
        this.f1684e = new AllMediaActivity(this, context);
        this.f1683d = new AllMediaActivity(this, context);
        this.f1686g = new AllMediaActivity(this, context, 1);
        this.f1687h = new AllMediaActivity(this, context, 4);
        this.f1688i = new AllMediaActivity(this, context, 1);
        this.f1689j = new AllMediaActivity(this, context, 4);
        this.f1690k = new AllMediaActivity(this, context, 3);
        this.f1685f = new AllMediaActivity(this, context);
        View frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.f1701v = new FrameLayout(context);
        this.f1701v.setBackgroundColor(ThemeUtil.m2485a().m2290d());
        frameLayout.addView(this.f1701v, LayoutHelper.createFrame(-1, 50.0f));
        this.f1702w = new TextView(context);
        this.f1702w.setTextColor(Theme.MSG_TEXT_COLOR);
        this.f1702w.setGravity(17);
        this.f1702w.setTextSize(1, 17.0f);
        this.f1702w.setCompoundDrawablesWithIntrinsicBounds(0, 0, C0338R.drawable.ic_dialog_black, 0);
        this.f1702w.setCompoundDrawablePadding(AndroidUtilities.dp(10.0f));
        this.f1702w.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        this.f1702w.setTypeface(FontUtil.m1176a().m1161d());
        this.f1702w.setOnClickListener(new AllMediaActivity(this));
        this.f1701v.addView(this.f1702w, LayoutHelper.createFrame(-2, -2.0f, 5, 0.0f, 0.0f, 0.0f, 0.0f));
        this.f1703x = new TextView(context);
        this.f1703x.setTextColor(Theme.MSG_TEXT_COLOR);
        this.f1703x.setGravity(17);
        this.f1703x.setTextSize(1, 17.0f);
        this.f1703x.setCompoundDrawablesWithIntrinsicBounds(C0338R.drawable.ic_menu_download_black, 0, 0, 0);
        this.f1703x.setCompoundDrawablePadding(AndroidUtilities.dp(10.0f));
        this.f1703x.setTypeface(FontUtil.m1176a().m1161d());
        this.f1703x.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        this.f1703x.setOnClickListener(new OnClickListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p013m.AllMediaActivity f1610a;

            {
                this.f1610a = r1;
            }

            public void onClick(View view) {
                this.f1610a.m1643b();
            }
        });
        this.f1701v.addView(this.f1703x, LayoutHelper.createFrame(-2, -2.0f, 3, 0.0f, 0.0f, 0.0f, 0.0f));
        this.f1691l = new SectionsListView(context);
        this.f1691l.setDivider(null);
        this.f1691l.setDividerHeight(0);
        this.f1691l.setDrawSelectorOnTop(true);
        this.f1691l.setClipToPadding(false);
        frameLayout.addView(this.f1691l, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 48, 0.0f, 50.0f, 0.0f, 0.0f));
        this.f1691l.setOnItemClickListener(new OnItemClickListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p013m.AllMediaActivity f1611a;

            {
                this.f1611a = r1;
            }

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if ((this.f1611a.f1674H == 1 || this.f1611a.f1674H == 4) && (view instanceof SharedDocumentCell)) {
                    this.f1611a.m1634a(i, view, ((SharedDocumentCell) view).getMessage(), 0);
                } else if (this.f1611a.f1674H == 3 && (view instanceof SharedLinkCell)) {
                    this.f1611a.m1634a(i, view, ((SharedLinkCell) view).getMessage(), 0);
                }
            }
        });
        this.f1691l.setOnScrollListener(new OnScrollListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p013m.AllMediaActivity f1612a;

            {
                this.f1612a = r1;
            }

            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                int i4 = 2;
                if ((!this.f1612a.f1705z || !this.f1612a.f1704y) && i2 != 0 && i + i2 > i3 - 2 && !this.f1612a.f1678L[this.f1612a.f1674H].f1661g) {
                    if (this.f1612a.f1674H == 0) {
                        i4 = 0;
                    } else if (this.f1612a.f1674H == 1) {
                        i4 = 1;
                    } else if (this.f1612a.f1674H != 2) {
                        i4 = this.f1612a.f1674H == 4 ? 4 : this.f1612a.f1674H == 5 ? 5 : 3;
                    }
                    if (!this.f1612a.f1678L[this.f1612a.f1674H].f1662h[0]) {
                        this.f1612a.f1678L[this.f1612a.f1674H].f1661g = true;
                        if (this.f1612a.f1672F != 0) {
                            SharedMediaQuery.loadMedia(this.f1612a.f1672F, 0, 50, this.f1612a.f1678L[this.f1612a.f1674H].f1663i[0], i4, true, this.f1612a.classGuid);
                        } else {
                            AllSharedMediaQuery.m1688a(0, 50, this.f1612a.f1678L[this.f1612a.f1674H].f1663i[0], i4, true, this.f1612a.classGuid);
                        }
                    } else if (this.f1612a.f1671E != 0 && !this.f1612a.f1678L[this.f1612a.f1674H].f1662h[1]) {
                        this.f1612a.f1678L[this.f1612a.f1674H].f1661g = true;
                        if (this.f1612a.f1672F != 0) {
                            SharedMediaQuery.loadMedia(this.f1612a.f1671E, 0, 50, this.f1612a.f1678L[this.f1612a.f1674H].f1663i[1], i4, true, this.f1612a.classGuid);
                        } else {
                            AllSharedMediaQuery.m1688a(0, 50, this.f1612a.f1678L[this.f1612a.f1674H].f1663i[1], i4, true, this.f1612a.classGuid);
                        }
                    }
                }
            }

            public void onScrollStateChanged(AbsListView absListView, int i) {
                boolean z = true;
                if (i == 1 && this.f1612a.f1705z && this.f1612a.f1704y) {
                    AndroidUtilities.hideKeyboard(this.f1612a.getParentActivity().getCurrentFocus());
                }
                com.hanista.mobogram.mobo.p013m.AllMediaActivity allMediaActivity = this.f1612a;
                if (i == 0) {
                    z = false;
                }
                allMediaActivity.f1670D = z;
            }
        });
        this.f1691l.setOnItemLongClickListener(new OnItemLongClickListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p013m.AllMediaActivity f1613a;

            {
                this.f1613a = r1;
            }

            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                if ((this.f1613a.f1674H == 1 || this.f1613a.f1674H == 4) && (view instanceof SharedDocumentCell)) {
                    return this.f1613a.m1638a(((SharedDocumentCell) view).getMessage(), view, 0);
                } else if (this.f1613a.f1674H != 3 || !(view instanceof SharedLinkCell)) {
                    return false;
                } else {
                    return this.f1613a.m1638a(((SharedLinkCell) view).getMessage(), view, 0);
                }
            }
        });
        for (i = 0; i < 6; i++) {
            this.f1700u.add(new SharedPhotoVideoCell(context));
        }
        this.f1695p = new LinearLayout(context);
        this.f1695p.setOrientation(1);
        this.f1695p.setGravity(17);
        this.f1695p.setVisibility(8);
        this.f1695p.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        frameLayout.addView(this.f1695p, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 17, 0.0f, 50.0f, 0.0f, 0.0f));
        this.f1695p.setOnTouchListener(new AllMediaActivity(this));
        this.f1694o = new ImageView(context);
        this.f1695p.addView(this.f1694o, LayoutHelper.createLinear(-2, -2));
        this.f1693n = new TextView(context);
        this.f1693n.setTextColor(-7697782);
        this.f1693n.setGravity(17);
        this.f1693n.setTextSize(1, 17.0f);
        this.f1693n.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        this.f1695p.addView(this.f1693n, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
        this.f1692m = new LinearLayout(context);
        this.f1692m.setGravity(17);
        this.f1692m.setOrientation(1);
        this.f1692m.setVisibility(8);
        this.f1692m.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        frameLayout.addView(this.f1692m, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.f1692m.addView(new ProgressBar(context), LayoutHelper.createLinear(-2, -2));
        m1654e();
        if (!AndroidUtilities.isTablet()) {
            frameLayout.addView(new PlayerView(context, this), LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            frameLayout.addView(new PowerView(context, this), LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        m1651d();
        m1671n();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        ArrayList arrayList;
        int i2;
        ActionBarMenuItem actionBarMenuItem;
        int i3;
        if (i == NotificationCenter.mediaDidLoaded) {
            if (((Integer) objArr[3]).intValue() == this.classGuid) {
                int intValue = ((Integer) objArr[4]).intValue();
                this.f1678L[intValue].f1661g = false;
                this.f1678L[intValue].f1660f = ((Integer) objArr[1]).intValue();
                arrayList = (ArrayList) objArr[2];
                for (i2 = 0; i2 < arrayList.size(); i2++) {
                    this.f1678L[intValue].m1610a((MessageObject) arrayList.get(i2), false, false);
                }
                this.f1678L[intValue].f1662h[0] = ((Boolean) objArr[5]).booleanValue();
                if (this.f1678L[intValue].f1656b.size() == 0 && arrayList.size() > 0) {
                    this.f1678L[this.f1674H].f1661g = true;
                    if (this.f1672F != 0) {
                        SharedMediaQuery.loadMedia(this.f1672F, 0, 50, this.f1678L[this.f1674H].f1663i[0], intValue, true, this.classGuid);
                    } else {
                        AllSharedMediaQuery.m1688a(0, 50, this.f1678L[this.f1674H].f1663i[0], intValue, true, this.classGuid);
                    }
                }
                if (this.f1678L[this.f1674H].f1656b.isEmpty() && this.f1671E != 0) {
                    this.f1678L[this.f1674H].f1661g = true;
                    if (this.f1672F != 0) {
                        SharedMediaQuery.loadMedia(this.f1671E, 0, 50, this.f1678L[this.f1674H].f1663i[1], intValue, true, this.classGuid);
                    } else {
                        AllSharedMediaQuery.m1688a(0, 50, this.f1678L[this.f1674H].f1663i[1], intValue, true, this.classGuid);
                    }
                }
                if (!this.f1678L[this.f1674H].f1661g) {
                    if (this.f1692m != null) {
                        this.f1692m.setVisibility(8);
                    }
                    if (this.f1674H == intValue && this.f1691l != null && this.f1691l.getEmptyView() == null) {
                        this.f1691l.setEmptyView(this.f1695p);
                    }
                }
                this.f1670D = true;
                if (this.f1674H == 0 && intValue == 0) {
                    if (this.f1681b != null) {
                        this.f1681b.notifyDataSetChanged();
                    }
                    if (this.f1682c != null) {
                        this.f1682c.notifyDataSetChanged();
                    }
                    if (this.f1683d != null) {
                        this.f1683d.notifyDataSetChanged();
                    }
                } else if (this.f1674H == 1 && intValue == 1) {
                    if (this.f1686g != null) {
                        this.f1686g.notifyDataSetChanged();
                    }
                } else if (this.f1674H == 3 && intValue == 3) {
                    if (this.f1685f != null) {
                        this.f1685f.notifyDataSetChanged();
                    }
                } else if (this.f1674H == 4 && intValue == 4) {
                    if (this.f1687h != null) {
                        this.f1687h.notifyDataSetChanged();
                    }
                } else if (this.f1674H == 5 && this.f1684e != null) {
                    this.f1684e.notifyDataSetChanged();
                }
                if (this.f1674H == 1 || this.f1674H == 3 || this.f1674H == 4) {
                    actionBarMenuItem = this.f1698s;
                    i3 = (this.f1678L[this.f1674H].f1656b.isEmpty() || this.f1705z) ? 8 : 0;
                    actionBarMenuItem.setVisibility(i3);
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            Object obj = null;
            Iterator it = ((ArrayList) objArr[0]).iterator();
            while (it.hasNext()) {
                r0 = (Integer) it.next();
                for (AllMediaActivity a : this.f1678L) {
                    if (a.m1609a(r0.intValue(), 0)) {
                        obj = 1;
                    }
                }
            }
            if (obj != null) {
                this.f1670D = true;
                if (this.f1681b != null) {
                    this.f1681b.notifyDataSetChanged();
                }
                if (this.f1682c != null) {
                    this.f1682c.notifyDataSetChanged();
                }
                if (this.f1684e != null) {
                    this.f1684e.notifyDataSetChanged();
                }
                if (this.f1683d != null) {
                    this.f1683d.notifyDataSetChanged();
                }
                if (this.f1686g != null) {
                    this.f1686g.notifyDataSetChanged();
                }
                if (this.f1685f != null) {
                    this.f1685f.notifyDataSetChanged();
                }
                if (this.f1687h != null) {
                    this.f1687h.notifyDataSetChanged();
                }
                if (this.f1674H == 1 || this.f1674H == 3 || this.f1674H == 4) {
                    actionBarMenuItem = this.f1698s;
                    i3 = (this.f1678L[this.f1674H].f1656b.isEmpty() || this.f1705z) ? 8 : 0;
                    actionBarMenuItem.setVisibility(i3);
                }
            }
        } else if (i == NotificationCenter.didReceivedNewMessages) {
            if (((Long) objArr[0]).longValue() == this.f1672F) {
                arrayList = (ArrayList) objArr[1];
                boolean z = ((int) this.f1672F) == 0;
                Object obj2 = null;
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    MessageObject messageObject = (MessageObject) it2.next();
                    if (messageObject.messageOwner.media != null) {
                        r4 = AllSharedMediaQuery.m1686a(messageObject.messageOwner);
                        if (r4 != -1) {
                            obj2 = this.f1678L[r4].m1610a(messageObject, true, z) ? 1 : obj2;
                        } else {
                            return;
                        }
                    }
                }
                if (obj2 != null) {
                    this.f1670D = true;
                    if (this.f1681b != null) {
                        this.f1681b.notifyDataSetChanged();
                    }
                    if (this.f1682c != null) {
                        this.f1682c.notifyDataSetChanged();
                    }
                    if (this.f1684e != null) {
                        this.f1684e.notifyDataSetChanged();
                    }
                    if (this.f1683d != null) {
                        this.f1683d.notifyDataSetChanged();
                    }
                    if (this.f1686g != null) {
                        this.f1686g.notifyDataSetChanged();
                    }
                    if (this.f1685f != null) {
                        this.f1685f.notifyDataSetChanged();
                    }
                    if (this.f1687h != null) {
                        this.f1687h.notifyDataSetChanged();
                    }
                    if (this.f1674H == 1 || this.f1674H == 3 || this.f1674H == 4) {
                        actionBarMenuItem = this.f1698s;
                        i3 = (this.f1678L[this.f1674H].f1656b.isEmpty() || this.f1705z) ? 8 : 0;
                        actionBarMenuItem.setVisibility(i3);
                    }
                }
            }
        } else if (i == NotificationCenter.messageReceivedByServer) {
            r0 = (Integer) objArr[0];
            Integer num = (Integer) objArr[1];
            for (AllMediaActivity b : this.f1678L) {
                b.m1611b(r0.intValue(), num.intValue());
            }
        }
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        int i2 = 0;
        if (messageObject == null || this.f1691l == null || (this.f1674H != 0 && this.f1674H != 5)) {
            return null;
        }
        int childCount = this.f1691l.getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = this.f1691l.getChildAt(i3);
            int i4;
            MessageObject messageObject2;
            BackupImageView imageView;
            if (childAt instanceof SharedPhotoVideoCell) {
                SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) childAt;
                i4 = 0;
                while (i4 < 6) {
                    messageObject2 = sharedPhotoVideoCell.getMessageObject(i4);
                    if (messageObject2 == null) {
                        break;
                        continue;
                    } else {
                        imageView = sharedPhotoVideoCell.getImageView(i4);
                        if (messageObject2.getId() == messageObject.getId()) {
                            int[] iArr = new int[2];
                            imageView.getLocationInWindow(iArr);
                            PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                            placeProviderObject.viewX = iArr[0];
                            placeProviderObject.viewY = iArr[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                            placeProviderObject.parentView = this.f1691l;
                            placeProviderObject.imageReceiver = imageView.getImageReceiver();
                            placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
                            placeProviderObject.parentView.getLocationInWindow(iArr);
                            placeProviderObject.clipTopAddition = AndroidUtilities.dp(40.0f);
                            return placeProviderObject;
                        }
                        i4++;
                    }
                }
                continue;
            } else if (childAt instanceof com.hanista.mobogram.mobo.p009j.SharedGifCell) {
                com.hanista.mobogram.mobo.p009j.SharedGifCell sharedGifCell = (com.hanista.mobogram.mobo.p009j.SharedGifCell) childAt;
                i4 = 0;
                while (i4 < 6) {
                    messageObject2 = sharedGifCell.m1238b(i4);
                    if (messageObject2 != null) {
                        imageView = sharedGifCell.m1235a(i4);
                        if (messageObject2.getId() == messageObject.getId()) {
                            int[] iArr2 = new int[2];
                            imageView.getLocationInWindow(iArr2);
                            PlaceProviderObject placeProviderObject2 = new PlaceProviderObject();
                            placeProviderObject2.viewX = iArr2[0];
                            i3 = iArr2[1];
                            if (VERSION.SDK_INT < 21) {
                                i2 = AndroidUtilities.statusBarHeight;
                            }
                            placeProviderObject2.viewY = i3 - i2;
                            placeProviderObject2.parentView = this.f1691l;
                            placeProviderObject2.imageReceiver = imageView.getImageReceiver();
                            placeProviderObject2.thumb = placeProviderObject2.imageReceiver.getBitmap();
                            placeProviderObject2.parentView.getLocationInWindow(iArr2);
                            placeProviderObject2.clipTopAddition = AndroidUtilities.dp(40.0f);
                            return placeProviderObject2;
                        }
                        i4++;
                    } else {
                        break;
                        continue;
                    }
                }
                continue;
            } else {
                continue;
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

    public void initThemeActionBar() {
        if (ThemeUtil.m2490b()) {
            Drawable backDrawable = new BackDrawable(false);
            ((BackDrawable) backDrawable).setColor(AdvanceTheme.bi);
            this.actionBar.setBackButtonDrawable(backDrawable);
            this.actionBar.setBackgroundColor(AdvanceTheme.f2491b);
        }
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    public boolean onBackPressed() {
        int i = 1;
        if (this.actionBar == null || !this.actionBar.isActionModeShowed()) {
            return true;
        }
        while (i >= 0) {
            this.f1667A[i].clear();
            i--;
        }
        this.actionBar.hideActionMode();
        this.f1668B = 0;
        m1660h();
        return false;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.f1691l != null) {
            this.f1691l.getViewTreeObserver().addOnPreDrawListener(new AllMediaActivity(this));
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
        for (int i = 0; i < this.f1678L.length; i++) {
            this.f1678L[i] = new AllMediaActivity();
            this.f1678L[i].f1663i[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            if (!(this.f1671E == 0 || this.f1680a == null)) {
                this.f1678L[i].f1663i[1] = this.f1680a.migrated_from_max_id;
                this.f1678L[i].f1662h[1] = false;
            }
        }
        this.f1678L[0].f1661g = true;
        this.f1673G = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).getInt("all_shared_media_dl_type", 0);
        if (this.f1672F != 0) {
            SharedMediaQuery.loadMedia(this.f1672F, 0, 50, 0, 0, true, this.classGuid);
        } else {
            AllSharedMediaQuery.m1688a(0, 50, ConnectionsManager.DEFAULT_DATACENTER_ID, 0, true, this.classGuid);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
    }

    public void onPause() {
        super.onPause();
        if (this.f1697r != null) {
            this.f1697r.closeSubMenu();
        }
    }

    public void onResume() {
        super.onResume();
        this.f1670D = true;
        if (this.f1681b != null) {
            this.f1681b.notifyDataSetChanged();
        }
        if (this.f1682c != null) {
            this.f1682c.notifyDataSetChanged();
        }
        if (this.f1684e != null) {
            this.f1684e.notifyDataSetChanged();
        }
        if (this.f1683d != null) {
            this.f1683d.notifyDataSetChanged();
        }
        if (this.f1686g != null) {
            this.f1686g.notifyDataSetChanged();
        }
        if (this.f1685f != null) {
            this.f1685f.notifyDataSetChanged();
        }
        m1655f();
        initThemeActionBar();
        m1674o();
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
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}
