package com.hanista.mobogram.mobo.download;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.FileDownloadProgressListener;
import com.hanista.mobogram.messenger.MediaController.PhotoEntry;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.NotificationsController;
import com.hanista.mobogram.messenger.SendMessagesHelper;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.VideoEditedInfo;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.query.BotQuery;
import com.hanista.mobogram.messenger.query.MessagesQuery;
import com.hanista.mobogram.messenger.query.MessagesSearchQuery;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.component.PowerView;
import com.hanista.mobogram.mobo.component.ProForwardActivity;
import com.hanista.mobogram.mobo.download.a.AnonymousClass13;
import com.hanista.mobogram.mobo.download.a.AnonymousClass14;
import com.hanista.mobogram.mobo.download.a.AnonymousClass20;
import com.hanista.mobogram.mobo.download.a.AnonymousClass23;
import com.hanista.mobogram.mobo.download.a.AnonymousClass24;
import com.hanista.mobogram.mobo.download.a.AnonymousClass28;
import com.hanista.mobogram.mobo.download.a.AnonymousClass31;
import com.hanista.mobogram.mobo.download.a.AnonymousClass33;
import com.hanista.mobogram.mobo.p003d.Glow;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p010k.MaterialHelperUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.SerializedData;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.KeyboardButton;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelForbidden;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatForbidden;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantsForbidden;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetID;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetShortName;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonCallback;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionPinMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import com.hanista.mobogram.tgnet.TLRPC.TL_replyKeyboardForceReply;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPagePending;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.WebPage;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BackDrawable;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.Builder;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.MentionsAdapter;
import com.hanista.mobogram.ui.Cells.BotHelpCell;
import com.hanista.mobogram.ui.Cells.BotHelpCell.BotHelpCellDelegate;
import com.hanista.mobogram.ui.Cells.ChatActionCell;
import com.hanista.mobogram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import com.hanista.mobogram.ui.Cells.ChatLoadingCell;
import com.hanista.mobogram.ui.Cells.ChatMessageCell;
import com.hanista.mobogram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import com.hanista.mobogram.ui.Cells.ChatUnreadCell;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.ChatActivityEnterView;
import com.hanista.mobogram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.NumberTextView;
import com.hanista.mobogram.ui.Components.PlayerView;
import com.hanista.mobogram.ui.Components.RadioButton;
import com.hanista.mobogram.ui.Components.RecordStatusDrawable;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnInterceptTouchListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.Components.SendingFileExDrawable;
import com.hanista.mobogram.ui.Components.ShareAlert;
import com.hanista.mobogram.ui.Components.SizeNotifierFrameLayout;
import com.hanista.mobogram.ui.Components.StickersAlert;
import com.hanista.mobogram.ui.Components.TypingDotsDrawable;
import com.hanista.mobogram.ui.Components.URLSpanBotCommand;
import com.hanista.mobogram.ui.Components.URLSpanNoUnderline;
import com.hanista.mobogram.ui.Components.URLSpanReplacement;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.Components.WebFrameLayout;
import com.hanista.mobogram.ui.DialogsActivity;
import com.hanista.mobogram.ui.DialogsActivity.DialogsActivityDelegate;
import com.hanista.mobogram.ui.LocationActivity;
import com.hanista.mobogram.ui.PhotoViewer;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import com.hanista.mobogram.ui.ProfileActivity;
import com.hanista.mobogram.ui.SecretPhotoViewer;
import com.hanista.mobogram.ui.StickerPreviewViewer;
import com.hanista.mobogram.ui.VideoEditorActivity;
import com.hanista.mobogram.ui.VideoEditorActivity.VideoEditorActivityDelegate;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;

/* renamed from: com.hanista.mobogram.mobo.download.a */
public class DownloadManagerActivity extends BaseFragment implements FileDownloadProgressListener, NotificationCenterDelegate, DialogsActivityDelegate, PhotoViewerProvider {
    public static boolean f721e;
    private TextView f722A;
    private RadioButton f723B;
    private FrameLayout f724C;
    private TextView f725D;
    private NumberTextView f726E;
    private RecyclerListView f727F;
    private OnItemClickListener f728G;
    private FrameLayout f729H;
    private ImageView f730I;
    private BackupImageView f731J;
    private TextView f732K;
    private TextView f733L;
    private ImageView f734M;
    private MentionsAdapter f735N;
    private PlayerView f736O;
    private PowerView f737P;
    private TextView f738Q;
    private View f739R;
    private ObjectAnimator f740S;
    private int f741T;
    private boolean f742U;
    private boolean f743V;
    private boolean f744W;
    private boolean f745X;
    private MessageObject f746Y;
    private ArrayList<MessageObject> f747Z;
    protected Chat f748a;
    private int[] aA;
    private int[] aB;
    private int[] aC;
    private boolean[] aD;
    private boolean[] aE;
    private boolean[] aF;
    private boolean aG;
    private boolean aH;
    private int aI;
    private int aJ;
    private long aK;
    private int aL;
    private boolean aM;
    private int aN;
    private boolean aO;
    private int aP;
    private int aQ;
    private boolean aR;
    private MessageObject aS;
    private MessageObject aT;
    private int aU;
    private int aV;
    private String aW;
    private Rect aX;
    private int aY;
    private CharSequence aZ;
    private MessageObject aa;
    private MessageObject ab;
    private boolean ac;
    private boolean ad;
    private boolean ae;
    private FileLocation af;
    private int ag;
    private WebPage ah;
    private ArrayList<CharSequence> ai;
    private String aj;
    private Runnable ak;
    private boolean al;
    private int am;
    private int an;
    private boolean ao;
    private boolean ap;
    private long aq;
    private int ar;
    private boolean as;
    private HashMap<Integer, MessageObject>[] at;
    private HashMap<Integer, MessageObject>[] au;
    private int av;
    private ArrayList<Integer> aw;
    private HashMap<Integer, MessageObject>[] ax;
    private HashMap<String, ArrayList<MessageObject>> ay;
    private int[] az;
    protected ChatActivityEnterView f749b;
    private String ba;
    private Runnable bb;
    private float bc;
    private float bd;
    private View be;
    private boolean bf;
    private ChatMessageCell bg;
    private boolean bh;
    protected ArrayList<MessageObject> f750c;
    protected ChatFull f751d;
    OnItemLongClickListener f752f;
    OnItemClickListener f753g;
    private ArrayList<ChatMessageCell> f754h;
    private FrameLayout f755i;
    private FrameLayout f756j;
    private ActionBarMenuItem f757k;
    private RecyclerListView f758l;
    private LinearLayoutManager f759m;
    private DownloadManagerActivity f760n;
    private ImageView f761o;
    private TextView f762p;
    private TextView f763q;
    private TextView f764r;
    private ProgressBar f765s;
    private FrameLayout f766t;
    private TypingDotsDrawable f767u;
    private RecordStatusDrawable f768v;
    private SendingFileExDrawable f769w;
    private FrameLayout f770x;
    private ArrayList<View> f771y;
    private TextView f772z;

    /* compiled from: DownloadManagerActivity */
    /* renamed from: com.hanista.mobogram.mobo.download.a.14 */
    class AnonymousClass14 implements Runnable {
        final /* synthetic */ CharSequence f661a;
        final /* synthetic */ DownloadManagerActivity f662b;

        /* renamed from: com.hanista.mobogram.mobo.download.a.14.1 */
        class DownloadManagerActivity implements Runnable {
            final /* synthetic */ AnonymousClass14 f654a;

            DownloadManagerActivity(AnonymousClass14 anonymousClass14) {
                this.f654a = anonymousClass14;
            }

            public void run() {
                if (this.f654a.f662b.ah != null) {
                    this.f654a.f662b.m770a(false, null, null, this.f654a.f662b.ah, false, true);
                    this.f654a.f662b.ah = null;
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.download.a.14.2 */
        class DownloadManagerActivity implements Runnable {
            final /* synthetic */ AnonymousClass14 f655a;

            DownloadManagerActivity(AnonymousClass14 anonymousClass14) {
                this.f655a = anonymousClass14;
            }

            public void run() {
                if (this.f655a.f662b.ah != null) {
                    this.f655a.f662b.m770a(false, null, null, this.f655a.f662b.ah, false, true);
                    this.f655a.f662b.ah = null;
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.download.a.14.3 */
        class DownloadManagerActivity implements RequestDelegate {
            final /* synthetic */ TL_messages_getWebPagePreview f659a;
            final /* synthetic */ AnonymousClass14 f660b;

            /* renamed from: com.hanista.mobogram.mobo.download.a.14.3.1 */
            class DownloadManagerActivity implements Runnable {
                final /* synthetic */ TL_error f656a;
                final /* synthetic */ TLObject f657b;
                final /* synthetic */ DownloadManagerActivity f658c;

                DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity, TL_error tL_error, TLObject tLObject) {
                    this.f658c = downloadManagerActivity;
                    this.f656a = tL_error;
                    this.f657b = tLObject;
                }

                public void run() {
                    this.f658c.f660b.f662b.ag = 0;
                    if (this.f656a != null) {
                        return;
                    }
                    if (this.f657b instanceof TL_messageMediaWebPage) {
                        this.f658c.f660b.f662b.ah = ((TL_messageMediaWebPage) this.f657b).webpage;
                        if ((this.f658c.f660b.f662b.ah instanceof TL_webPage) || (this.f658c.f660b.f662b.ah instanceof TL_webPagePending)) {
                            if (this.f658c.f660b.f662b.ah instanceof TL_webPagePending) {
                                this.f658c.f660b.f662b.aj = this.f658c.f659a.message;
                            }
                            this.f658c.f660b.f662b.m770a(true, null, null, this.f658c.f660b.f662b.ah, false, true);
                        } else if (this.f658c.f660b.f662b.ah != null) {
                            this.f658c.f660b.f662b.m770a(false, null, null, this.f658c.f660b.f662b.ah, false, true);
                            this.f658c.f660b.f662b.ah = null;
                        }
                    } else if (this.f658c.f660b.f662b.ah != null) {
                        this.f658c.f660b.f662b.m770a(false, null, null, this.f658c.f660b.f662b.ah, false, true);
                        this.f658c.f660b.f662b.ah = null;
                    }
                }
            }

            DownloadManagerActivity(AnonymousClass14 anonymousClass14, TL_messages_getWebPagePreview tL_messages_getWebPagePreview) {
                this.f660b = anonymousClass14;
                this.f659a = tL_messages_getWebPagePreview;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new DownloadManagerActivity(this, tL_error, tLObject));
            }
        }

        AnonymousClass14(DownloadManagerActivity downloadManagerActivity, CharSequence charSequence) {
            this.f662b = downloadManagerActivity;
            this.f661a = charSequence;
        }

        public void run() {
            boolean z = true;
            if (this.f662b.ag != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.f662b.ag, true);
                this.f662b.ag = 0;
            }
            CharSequence join;
            try {
                Matcher matcher = AndroidUtilities.WEB_URL.matcher(this.f661a);
                Iterable iterable = null;
                while (matcher.find()) {
                    if (matcher.start() <= 0 || this.f661a.charAt(matcher.start() - 1) != '@') {
                        ArrayList arrayList = iterable == null ? new ArrayList() : iterable;
                        arrayList.add(this.f661a.subSequence(matcher.start(), matcher.end()));
                        iterable = arrayList;
                    }
                }
                if (!(iterable == null || this.f662b.ai == null || iterable.size() != this.f662b.ai.size())) {
                    int i = 0;
                    while (i < iterable.size()) {
                        boolean z2 = !TextUtils.equals((CharSequence) iterable.get(i), (CharSequence) this.f662b.ai.get(i)) ? false : z;
                        i++;
                        z = z2;
                    }
                    if (z) {
                        return;
                    }
                }
                this.f662b.ai = iterable;
                if (iterable == null) {
                    AndroidUtilities.runOnUIThread(new com.hanista.mobogram.mobo.download.DownloadManagerActivity.14.DownloadManagerActivity(this));
                    return;
                }
                join = TextUtils.join(" ", iterable);
                TLObject tL_messages_getWebPagePreview = new TL_messages_getWebPagePreview();
                if (join instanceof String) {
                    tL_messages_getWebPagePreview.message = (String) join;
                } else {
                    tL_messages_getWebPagePreview.message = join.toString();
                }
                this.f662b.ag = ConnectionsManager.getInstance().sendRequest(tL_messages_getWebPagePreview, new com.hanista.mobogram.mobo.download.DownloadManagerActivity.14.DownloadManagerActivity(this, tL_messages_getWebPagePreview));
                ConnectionsManager.getInstance().bindRequestToGuid(this.f662b.ag, this.f662b.classGuid);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                String toLowerCase = this.f661a.toString().toLowerCase();
                if (this.f661a.length() < 13 || !(toLowerCase.contains("http://") || toLowerCase.contains("https://"))) {
                    AndroidUtilities.runOnUIThread(new com.hanista.mobogram.mobo.download.DownloadManagerActivity.14.DownloadManagerActivity(this));
                    return;
                }
                join = this.f661a;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.a.1 */
    class DownloadManagerActivity implements OnItemLongClickListener {
        final /* synthetic */ DownloadManagerActivity f668a;

        DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
            this.f668a = downloadManagerActivity;
        }

        public boolean onItemClick(View view, int i) {
            if (this.f668a.actionBar.isActionModeShowed()) {
                return false;
            }
            this.f668a.m687a(view, false);
            return true;
        }
    }

    /* compiled from: DownloadManagerActivity */
    /* renamed from: com.hanista.mobogram.mobo.download.a.20 */
    class AnonymousClass20 implements OnClickListener {
        final /* synthetic */ ArrayList f669a;
        final /* synthetic */ DownloadManagerActivity f670b;

        AnonymousClass20(DownloadManagerActivity downloadManagerActivity, ArrayList arrayList) {
            this.f670b = downloadManagerActivity;
            this.f669a = arrayList;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (this.f670b.f746Y != null && i >= 0 && i < this.f669a.size()) {
                this.f670b.m683a(((Integer) this.f669a.get(i)).intValue());
            }
        }
    }

    /* compiled from: DownloadManagerActivity */
    /* renamed from: com.hanista.mobogram.mobo.download.a.22 */
    class AnonymousClass22 implements OnClickListener {
        final /* synthetic */ MessageObject f672a;
        final /* synthetic */ DownloadManagerActivity f673b;

        AnonymousClass22(DownloadManagerActivity downloadManagerActivity, MessageObject messageObject) {
            this.f673b = downloadManagerActivity;
            this.f672a = messageObject;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            this.f673b.m746o();
            this.f673b.m684a(this.f672a.getId(), this.f672a.messageOwner.to_id.channel_id);
        }
    }

    /* compiled from: DownloadManagerActivity */
    /* renamed from: com.hanista.mobogram.mobo.download.a.23 */
    class AnonymousClass23 implements Runnable {
        final /* synthetic */ Semaphore f674a;
        final /* synthetic */ DownloadManagerActivity f675b;

        AnonymousClass23(DownloadManagerActivity downloadManagerActivity, Semaphore semaphore) {
            this.f675b = downloadManagerActivity;
            this.f674a = semaphore;
        }

        public void run() {
            this.f675b.f748a = DownloadMessagesStorage.m783a().m819b(1);
            this.f674a.release();
        }
    }

    /* compiled from: DownloadManagerActivity */
    /* renamed from: com.hanista.mobogram.mobo.download.a.24 */
    class AnonymousClass24 implements OnClickListener {
        final /* synthetic */ String f676a;
        final /* synthetic */ DownloadManagerActivity f677b;

        AnonymousClass24(DownloadManagerActivity downloadManagerActivity, String str) {
            this.f677b = downloadManagerActivity;
            this.f676a = str;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Browser.openUrl(this.f677b.getParentActivity(), this.f676a, true);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.a.2 */
    class DownloadManagerActivity extends LinearLayoutManager {
        final /* synthetic */ DownloadManagerActivity f685a;

        DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity, Context context) {
            this.f685a = downloadManagerActivity;
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* compiled from: DownloadManagerActivity */
    /* renamed from: com.hanista.mobogram.mobo.download.a.31 */
    class AnonymousClass31 extends SizeNotifierFrameLayout {
        int f687a;
        final /* synthetic */ DownloadManagerActivity f688b;

        AnonymousClass31(DownloadManagerActivity downloadManagerActivity, Context context) {
            this.f688b = downloadManagerActivity;
            super(context);
            this.f687a = 0;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            int emojiPadding = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? this.f688b.f749b.getEmojiPadding() : 0;
            setBottomClip(emojiPadding);
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                if (childAt.getVisibility() != 8) {
                    int i6;
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    int measuredWidth = childAt.getMeasuredWidth();
                    int measuredHeight = childAt.getMeasuredHeight();
                    int i7 = layoutParams.gravity;
                    if (i7 == -1) {
                        i7 = 51;
                    }
                    int i8 = i7 & 112;
                    switch ((i7 & 7) & 7) {
                        case VideoPlayer.TYPE_AUDIO /*1*/:
                            i7 = ((((i3 - i) - measuredWidth) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                            break;
                        case VideoPlayer.STATE_ENDED /*5*/:
                            i7 = (i3 - measuredWidth) - layoutParams.rightMargin;
                            break;
                        default:
                            i7 = layoutParams.leftMargin;
                            break;
                    }
                    switch (i8) {
                        case TLRPC.USER_FLAG_PHONE /*16*/:
                            i6 = (((((i4 - emojiPadding) - i2) - measuredHeight) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                            break;
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                            i6 = layoutParams.topMargin + getPaddingTop();
                            break;
                        case 80:
                            i6 = (((i4 - emojiPadding) - i2) - measuredHeight) - layoutParams.bottomMargin;
                            break;
                        default:
                            i6 = layoutParams.topMargin;
                            break;
                    }
                    if (childAt == this.f688b.f730I) {
                        i6 -= this.f688b.f749b.getMeasuredHeight();
                    } else if (childAt == this.f688b.f770x) {
                        i6 -= this.f687a / 2;
                    } else if (this.f688b.f749b.isPopupView(childAt)) {
                        i6 = this.f688b.f749b.getBottom();
                    } else if (childAt == this.f688b.f738Q) {
                        i6 -= this.f687a;
                    }
                    childAt.layout(i7, i6, measuredWidth + i7, measuredHeight + i6);
                }
            }
            notifyHeightChanged();
        }

        protected void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            int size2 = MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            size2 -= getPaddingTop();
            int emojiPadding = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? size2 - this.f688b.f749b.getEmojiPadding() : size2;
            int childCount = getChildCount();
            measureChildWithMargins(this.f688b.f749b, i, 0, i2, 0);
            this.f687a = this.f688b.f749b.getMeasuredHeight();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (!(childAt == null || childAt.getVisibility() == 8 || childAt == this.f688b.f749b)) {
                    try {
                        if (childAt == this.f688b.f758l || childAt == this.f688b.f755i) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (emojiPadding - this.f687a) + AndroidUtilities.dp(2.0f)), C0700C.ENCODING_PCM_32BIT));
                        } else if (childAt == this.f688b.f770x) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(emojiPadding, C0700C.ENCODING_PCM_32BIT));
                        } else if (this.f688b.f749b.isPopupView(childAt)) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, C0700C.ENCODING_PCM_32BIT));
                        } else {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }
        }
    }

    /* compiled from: DownloadManagerActivity */
    /* renamed from: com.hanista.mobogram.mobo.download.a.33 */
    class AnonymousClass33 extends RecyclerListView {
        final /* synthetic */ DownloadManagerActivity f690a;

        AnonymousClass33(DownloadManagerActivity downloadManagerActivity, Context context) {
            this.f690a = downloadManagerActivity;
            super(context);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.a.3 */
    class DownloadManagerActivity extends OnScrollListener {
        final /* synthetic */ int f691a;
        final /* synthetic */ DownloadManagerActivity f692b;

        DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity, int i) {
            this.f692b = downloadManagerActivity;
            this.f691a = i;
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1 && this.f692b.aU != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                this.f692b.aU = ConnectionsManager.DEFAULT_DATACENTER_ID;
                this.f692b.m749q();
            }
            if (ThemeUtil.m2490b()) {
                Glow.m522a(this.f692b.f758l, this.f691a);
            }
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            this.f692b.m715c();
            int findFirstVisibleItemPosition = this.f692b.f759m.findFirstVisibleItemPosition();
            int abs = findFirstVisibleItemPosition == -1 ? 0 : Math.abs(this.f692b.f759m.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
            if (abs > 0 && abs + findFirstVisibleItemPosition == this.f692b.f760n.getItemCount() && this.f692b.aF[0]) {
                this.f692b.m701a(false, true);
            }
            this.f692b.m728f();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.a.4 */
    class DownloadManagerActivity implements OnTouchListener {
        final /* synthetic */ DownloadManagerActivity f695a;

        /* renamed from: com.hanista.mobogram.mobo.download.a.4.1 */
        class DownloadManagerActivity implements Runnable {
            final /* synthetic */ DownloadManagerActivity f693a;

            DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
                this.f693a = downloadManagerActivity;
            }

            public void run() {
                this.f693a.f695a.f758l.setOnItemClickListener(this.f693a.f695a.f753g);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.download.a.4.2 */
        class DownloadManagerActivity implements Runnable {
            final /* synthetic */ DownloadManagerActivity f694a;

            DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
                this.f694a = downloadManagerActivity;
            }

            public void run() {
                this.f694a.f695a.f758l.setOnItemLongClickListener(this.f694a.f695a.f752f);
                this.f694a.f695a.f758l.setLongClickable(true);
            }
        }

        DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
            this.f695a = downloadManagerActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (this.f695a.bb != null || SecretPhotoViewer.getInstance().isVisible()) {
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3 || motionEvent.getAction() == 6) {
                    AndroidUtilities.runOnUIThread(new DownloadManagerActivity(this), 150);
                    if (this.f695a.bb != null) {
                        AndroidUtilities.cancelRunOnUIThread(this.f695a.bb);
                        this.f695a.bb = null;
                        try {
                            Toast.makeText(view.getContext(), LocaleController.getString("PhotoTip", C0338R.string.PhotoTip), 0).show();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    } else if (SecretPhotoViewer.getInstance().isVisible()) {
                        AndroidUtilities.runOnUIThread(new DownloadManagerActivity(this));
                        SecretPhotoViewer.getInstance().closePhoto();
                    }
                } else if (motionEvent.getAction() != 0) {
                    if (SecretPhotoViewer.getInstance().isVisible()) {
                        return true;
                    }
                    if (this.f695a.bb != null) {
                        if (motionEvent.getAction() != 2) {
                            AndroidUtilities.cancelRunOnUIThread(this.f695a.bb);
                            this.f695a.bb = null;
                        } else if (Math.hypot((double) (this.f695a.bc - motionEvent.getX()), (double) (this.f695a.bd - motionEvent.getY())) > ((double) AndroidUtilities.dp(5.0f))) {
                            AndroidUtilities.cancelRunOnUIThread(this.f695a.bb);
                            this.f695a.bb = null;
                        }
                        this.f695a.f758l.setOnItemClickListener(this.f695a.f753g);
                        this.f695a.f758l.setOnItemLongClickListener(this.f695a.f752f);
                        this.f695a.f758l.setLongClickable(true);
                    }
                }
            }
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.a.5 */
    class DownloadManagerActivity implements OnInterceptTouchListener {
        final /* synthetic */ DownloadManagerActivity f698a;

        /* renamed from: com.hanista.mobogram.mobo.download.a.5.1 */
        class DownloadManagerActivity implements Runnable {
            final /* synthetic */ MessageObject f696a;
            final /* synthetic */ DownloadManagerActivity f697b;

            DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity, MessageObject messageObject) {
                this.f697b = downloadManagerActivity;
                this.f696a = messageObject;
            }

            public void run() {
                if (this.f697b.f698a.bb != null) {
                    this.f697b.f698a.f758l.requestDisallowInterceptTouchEvent(true);
                    this.f697b.f698a.f758l.setOnItemLongClickListener(null);
                    this.f697b.f698a.f758l.setLongClickable(false);
                    this.f697b.f698a.bb = null;
                    SecretPhotoViewer.getInstance().setParentActivity(this.f697b.f698a.getParentActivity());
                    SecretPhotoViewer.getInstance().openPhoto(this.f696a);
                }
            }
        }

        DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
            this.f698a = downloadManagerActivity;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (this.f698a.actionBar.isActionModeShowed()) {
                return false;
            }
            if (motionEvent.getAction() == 0) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                int childCount = this.f698a.f758l.getChildCount();
                int i = 0;
                while (i < childCount) {
                    View childAt = this.f698a.f758l.getChildAt(i);
                    int top = childAt.getTop();
                    int bottom = childAt.getBottom();
                    if (top > y || bottom < y) {
                        i++;
                    } else if (childAt instanceof ChatMessageCell) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                        MessageObject messageObject = chatMessageCell.getMessageObject();
                        if (messageObject != null && !messageObject.isSending() && messageObject.isSecretPhoto() && chatMessageCell.getPhotoImage().isInsideImage((float) x, (float) (y - top)) && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                            this.f698a.bc = (float) x;
                            this.f698a.bd = (float) y;
                            this.f698a.f758l.setOnItemClickListener(null);
                            this.f698a.bb = new DownloadManagerActivity(this, messageObject);
                            AndroidUtilities.runOnUIThread(this.f698a.bb, 100);
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.a.6 */
    class DownloadManagerActivity implements View.OnClickListener {
        final /* synthetic */ DownloadManagerActivity f699a;

        DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
            this.f699a = downloadManagerActivity;
        }

        public void onClick(View view) {
            if (this.f699a.aN > 0) {
                this.f699a.m685a(this.f699a.aN, 0, true, 0);
            } else {
                this.f699a.m700a(true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.a.7 */
    class DownloadManagerActivity implements ChatActivityEnterViewDelegate {
        final /* synthetic */ DownloadManagerActivity f702a;

        /* renamed from: com.hanista.mobogram.mobo.download.a.7.1 */
        class DownloadManagerActivity implements Runnable {
            final /* synthetic */ CharSequence f700a;
            final /* synthetic */ DownloadManagerActivity f701b;

            DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity, CharSequence charSequence) {
                this.f701b = downloadManagerActivity;
                this.f700a = charSequence;
            }

            public void run() {
                if (this == this.f701b.f702a.ak) {
                    this.f701b.f702a.m696a(this.f700a, false);
                    this.f701b.f702a.ak = null;
                }
            }
        }

        DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
            this.f702a = downloadManagerActivity;
        }

        public void needSendTyping() {
        }

        public void onAttachButtonHidden() {
            if (!this.f702a.actionBar.isSearchFieldVisible() && this.f702a.f757k != null) {
                this.f702a.f757k.setVisibility(8);
            }
        }

        public void onAttachButtonShow() {
            if (!this.f702a.actionBar.isSearchFieldVisible() && this.f702a.f757k != null) {
                this.f702a.f757k.setVisibility(0);
            }
        }

        public void onMessageEditEnd(boolean z) {
        }

        public void onMessageSend(CharSequence charSequence) {
            this.f702a.m718d();
            this.f702a.m770a(false, null, null, null, false, true);
            if (this.f702a.f735N != null) {
                this.f702a.f735N.addHashtagsFromMessage(charSequence);
            }
        }

        public void onStickersTab(boolean z) {
            if (this.f702a.f739R != null) {
                this.f702a.f739R.setVisibility(8);
            }
            this.f702a.f745X = !z;
        }

        public void onTextChanged(CharSequence charSequence, boolean z) {
            if (this.f702a.f735N != null) {
                this.f702a.f735N.searchUsernameOrHashtag(charSequence.toString(), this.f702a.f749b.getCursorPosition(), this.f702a.f750c);
            }
            if (this.f702a.ak != null) {
                AndroidUtilities.cancelRunOnUIThread(this.f702a.ak);
                this.f702a.ak = null;
            }
            if (!this.f702a.f749b.isMessageWebPageSearchEnabled()) {
                return;
            }
            if (z) {
                this.f702a.m696a(charSequence, true);
                return;
            }
            this.f702a.ak = new DownloadManagerActivity(this, charSequence);
            AndroidUtilities.runOnUIThread(this.f702a.ak, AndroidUtilities.WEB_URL == null ? 3000 : 1000);
        }

        public void onWindowSizeChanged(int i) {
            boolean z = true;
            if (i < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
                this.f702a.f743V = false;
                if (this.f702a.f729H.getVisibility() == 0) {
                    this.f702a.f729H.clearAnimation();
                    this.f702a.f729H.setVisibility(4);
                }
            } else {
                this.f702a.f743V = true;
                if (this.f702a.f729H.getVisibility() == 4) {
                    this.f702a.f729H.clearAnimation();
                    this.f702a.f729H.setVisibility(0);
                }
            }
            DownloadManagerActivity downloadManagerActivity = this.f702a;
            if (this.f702a.f749b.isPopupShowing()) {
                z = false;
            }
            downloadManagerActivity.f744W = z;
            this.f702a.m728f();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.a.8 */
    class DownloadManagerActivity implements View.OnClickListener {
        final /* synthetic */ DownloadManagerActivity f703a;

        DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
            this.f703a = downloadManagerActivity;
        }

        public void onClick(View view) {
            if (this.f703a.f747Z != null) {
                this.f703a.f747Z.clear();
            }
            this.f703a.m770a(false, null, null, this.f703a.ah, true, true);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.a.9 */
    class DownloadManagerActivity extends RecyclerListView {
        final /* synthetic */ DownloadManagerActivity f704a;

        DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity, Context context) {
            this.f704a = downloadManagerActivity;
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return super.onInterceptTouchEvent(motionEvent) || StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, this.f704a.f727F, 0);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.a.a */
    public class DownloadManagerActivity extends Adapter {
        final /* synthetic */ DownloadManagerActivity f713a;
        private Context f714b;
        private int f715c;
        private int f716d;
        private int f717e;
        private int f718f;
        private int f719g;
        private int f720h;

        /* renamed from: com.hanista.mobogram.mobo.download.a.a.1 */
        class DownloadManagerActivity implements ChatMessageCellDelegate {
            final /* synthetic */ DownloadManagerActivity f707a;

            /* renamed from: com.hanista.mobogram.mobo.download.a.a.1.1 */
            class DownloadManagerActivity implements OnClickListener {
                final /* synthetic */ String f705a;
                final /* synthetic */ DownloadManagerActivity f706b;

                DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity, String str) {
                    this.f706b = downloadManagerActivity;
                    this.f705a = str;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        Browser.openUrl(this.f706b.f707a.f713a.getParentActivity(), this.f705a, false);
                    } else if (i == 1) {
                        try {
                            if (VERSION.SDK_INT < 11) {
                                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setText(this.f705a);
                            } else {
                                ((android.content.ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.f705a));
                            }
                            Toast.makeText(this.f706b.f707a.f713a.getParentActivity(), LocaleController.getString("LinkCopied", C0338R.string.LinkCopied), 0).show();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                }
            }

            DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
                this.f707a = downloadManagerActivity;
            }

            public boolean canPerformActions() {
                return (this.f707a.f713a.actionBar == null || this.f707a.f713a.actionBar.isActionModeShowed()) ? false : true;
            }

            public void didLongPressed(ChatMessageCell chatMessageCell) {
                this.f707a.f713a.m687a((View) chatMessageCell, false);
            }

            public void didLongPressedUserAvatar(ChatMessageCell chatMessageCell, User user) {
            }

            public void didPressedBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                if (this.f707a.f713a.getParentActivity() == null) {
                    return;
                }
                if (this.f707a.f713a.f766t.getVisibility() != 0 || (keyboardButton instanceof TL_keyboardButtonCallback) || (keyboardButton instanceof TL_keyboardButtonUrl)) {
                    this.f707a.f713a.f749b.didPressedBotButton(keyboardButton, chatMessageCell.getMessageObject(), chatMessageCell.getMessageObject());
                }
            }

            public void didPressedCancelSendButton(ChatMessageCell chatMessageCell) {
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (messageObject.messageOwner.send_state != 0) {
                    SendMessagesHelper.getInstance().cancelSendingMessage(messageObject);
                }
            }

            public void didPressedChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i) {
                if (this.f707a.f713a.actionBar.isActionModeShowed()) {
                    this.f707a.f713a.m686a((View) chatMessageCell);
                } else if (chat != null && chat != this.f707a.f713a.f748a) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", chat.id);
                    if (i != 0) {
                        bundle.putInt("message_id", i);
                    }
                    if (MessagesController.checkCanOpenChat(bundle, this.f707a.f713a)) {
                        this.f707a.f713a.presentFragment(new ChatActivity(bundle), true);
                    }
                }
            }

            public void didPressedFavorite(ChatMessageCell chatMessageCell) {
            }

            public void didPressedImage(ChatMessageCell chatMessageCell) {
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (messageObject.isSendError()) {
                    this.f707a.f713a.m687a((View) chatMessageCell, false);
                } else if (!messageObject.isSending()) {
                    if (messageObject.type == 13) {
                        this.f707a.f713a.showDialog(new StickersAlert(this.f707a.f713a.getParentActivity(), this.f707a.f713a, messageObject.getInputStickerSet(), null, this.f707a.f713a.f766t.getVisibility() != 0 ? this.f707a.f713a.f749b : null));
                    } else if ((VERSION.SDK_INT >= 16 && messageObject.isVideo() && MoboConstants.aD) || messageObject.type == 1 || ((messageObject.type == 0 && !messageObject.isWebpageDocument()) || messageObject.isGif())) {
                        PhotoViewer.getInstance().setParentActivity(this.f707a.f713a.getParentActivity());
                        PhotoViewer.getInstance().openPhoto(messageObject, messageObject.type != 0 ? this.f707a.f713a.aq : 0, messageObject.type != 0 ? this.f707a.f713a.aK : 0, this.f707a.f713a);
                    } else if (messageObject.type == 3) {
                        try {
                            File file = (messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0) ? null : new File(messageObject.messageOwner.attachPath);
                            if (file == null || !file.exists()) {
                                file = FileLoader.getPathToMessage(messageObject.messageOwner);
                            }
                            Intent intent = new Intent("android.intent.action.VIEW");
                            intent.setDataAndType(Uri.fromFile(file), MimeTypes.VIDEO_MP4);
                            this.f707a.f713a.getParentActivity().startActivityForResult(intent, 500);
                        } catch (Exception e) {
                            this.f707a.f713a.m719d(messageObject);
                        }
                    } else if (messageObject.type == 4) {
                        if (AndroidUtilities.isGoogleMapsInstalled(this.f707a.f713a)) {
                            BaseFragment locationActivity = new LocationActivity();
                            locationActivity.setMessageObject(messageObject);
                            this.f707a.f713a.presentFragment(locationActivity);
                        }
                    } else if (messageObject.type == 9 || messageObject.type == 0) {
                        try {
                            AndroidUtilities.openForView(messageObject, this.f707a.f713a.getParentActivity());
                        } catch (Exception e2) {
                            this.f707a.f713a.m719d(messageObject);
                        }
                    }
                }
            }

            public void didPressedMenu(ChatMessageCell chatMessageCell) {
            }

            public void didPressedOther(ChatMessageCell chatMessageCell) {
                this.f707a.f713a.m687a((View) chatMessageCell, true);
            }

            public void didPressedReplyMessage(ChatMessageCell chatMessageCell, int i) {
            }

            public void didPressedShare(ChatMessageCell chatMessageCell) {
                if (this.f707a.f713a.getParentActivity() != null) {
                    if (this.f707a.f713a.f749b != null) {
                        this.f707a.f713a.f749b.closeKeyboard();
                    }
                    DownloadManagerActivity downloadManagerActivity = this.f707a.f713a;
                    Context c = this.f707a.f714b;
                    MessageObject messageObject = chatMessageCell.getMessageObject();
                    boolean z = ChatObject.isChannel(this.f707a.f713a.f748a) && !this.f707a.f713a.f748a.megagroup && this.f707a.f713a.f748a.username != null && this.f707a.f713a.f748a.username.length() > 0;
                    downloadManagerActivity.showDialog(new ShareAlert(c, messageObject, null, z, null));
                }
            }

            public void didPressedUrl(MessageObject messageObject, ClickableSpan clickableSpan, boolean z) {
                if (clickableSpan != null) {
                    if (clickableSpan instanceof URLSpanNoUnderline) {
                        String url = ((URLSpanNoUnderline) clickableSpan).getURL();
                        if (url.startsWith("@")) {
                            MessagesController.openByUserName(url.substring(1), this.f707a.f713a, 0);
                            return;
                        } else if (url.startsWith("#")) {
                            if (!ChatObject.isChannel(this.f707a.f713a.f748a)) {
                                BaseFragment dialogsActivity = new DialogsActivity(null);
                                dialogsActivity.setSearchString(url);
                                this.f707a.f713a.presentFragment(dialogsActivity);
                                return;
                            }
                            return;
                        } else if (url.startsWith("/") && URLSpanBotCommand.enabled) {
                            ChatActivityEnterView chatActivityEnterView = this.f707a.f713a.f749b;
                            boolean z2 = this.f707a.f713a.f748a != null && this.f707a.f713a.f748a.megagroup;
                            chatActivityEnterView.setCommand(messageObject, url, z, z2);
                            return;
                        } else {
                            return;
                        }
                    }
                    String url2 = ((URLSpan) clickableSpan).getURL();
                    if (z) {
                        Builder builder = new Builder(this.f707a.f713a.getParentActivity());
                        builder.setTitle(url2);
                        builder.setItems(new CharSequence[]{LocaleController.getString("Open", C0338R.string.Open), LocaleController.getString("Copy", C0338R.string.Copy)}, new DownloadManagerActivity(this, url2));
                        this.f707a.f713a.showDialog(builder.create());
                    } else if (clickableSpan instanceof URLSpanReplacement) {
                        this.f707a.f713a.m769a(((URLSpanReplacement) clickableSpan).getURL());
                    } else if (clickableSpan instanceof URLSpan) {
                        Browser.openUrl(this.f707a.f713a.getParentActivity(), url2, false);
                    } else {
                        clickableSpan.onClick(this.f707a.f713a.fragmentView);
                    }
                }
            }

            public void didPressedUserAvatar(ChatMessageCell chatMessageCell, User user) {
                if (this.f707a.f713a.actionBar.isActionModeShowed()) {
                    this.f707a.f713a.m686a((View) chatMessageCell);
                } else if (user != null && user.id != UserConfig.getClientUserId()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", user.id);
                    BaseFragment profileActivity = new ProfileActivity(bundle);
                    profileActivity.setPlayProfileAnimation(false);
                    this.f707a.f713a.presentFragment(profileActivity);
                }
            }

            public void didPressedViaBot(ChatMessageCell chatMessageCell, String str) {
                if (this.f707a.f713a.f766t != null && this.f707a.f713a.f766t.getVisibility() == 0) {
                    return;
                }
                if ((this.f707a.f713a.f756j == null || this.f707a.f713a.f756j.getVisibility() != 0) && this.f707a.f713a.f749b != null && str != null && str.length() > 0) {
                    this.f707a.f713a.f749b.setFieldText("@" + str + " ");
                    this.f707a.f713a.f749b.openKeyboard();
                }
            }

            public void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                Builder builder = new Builder(this.f707a.f714b);
                builder.setCustomView(new WebFrameLayout(this.f707a.f714b, builder.create(), str2, str3, str4, str, i, i2));
                builder.setUseFullWidth(true);
                this.f707a.f713a.showDialog(builder.create());
            }

            public boolean needPlayAudio(MessageObject messageObject) {
                if (!messageObject.isVoice()) {
                    return messageObject.isMusic() ? MediaController.m71a().m160a(this.f707a.f713a.f750c, messageObject) : false;
                } else {
                    boolean a = MediaController.m71a().m158a(messageObject);
                    MediaController.m71a().m153a(a ? this.f707a.f713a.m679a(messageObject, false) : null, false);
                    return a;
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.download.a.a.2 */
        class DownloadManagerActivity implements ChatActionCellDelegate {
            final /* synthetic */ DownloadManagerActivity f708a;

            DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
                this.f708a = downloadManagerActivity;
            }

            public void didClickedImage(ChatActionCell chatActionCell) {
                MessageObject messageObject = chatActionCell.getMessageObject();
                PhotoViewer.getInstance().setParentActivity(this.f708a.f713a.getParentActivity());
                PhotoViewer.getInstance().openPhoto(messageObject, 0, 0, this.f708a.f713a);
            }

            public void didLongPressed(ChatActionCell chatActionCell) {
                this.f708a.f713a.m687a((View) chatActionCell, false);
            }

            public void didPressedBotButton(MessageObject messageObject, KeyboardButton keyboardButton) {
            }

            public void didPressedReplyMessage(ChatActionCell chatActionCell, int i) {
            }

            public void needOpenUserProfile(int i) {
                Bundle bundle;
                if (i < 0) {
                    bundle = new Bundle();
                    bundle.putInt("chat_id", -i);
                    if (MessagesController.checkCanOpenChat(bundle, this.f708a.f713a)) {
                        this.f708a.f713a.presentFragment(new ChatActivity(bundle), true);
                    }
                } else if (i != UserConfig.getClientUserId()) {
                    bundle = new Bundle();
                    bundle.putInt("user_id", i);
                    BaseFragment profileActivity = new ProfileActivity(bundle);
                    profileActivity.setPlayProfileAnimation(false);
                    this.f708a.f713a.presentFragment(profileActivity);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.download.a.a.3 */
        class DownloadManagerActivity implements BotHelpCellDelegate {
            final /* synthetic */ DownloadManagerActivity f709a;

            DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
                this.f709a = downloadManagerActivity;
            }

            public void didPressUrl(String str) {
                if (str.startsWith("@")) {
                    MessagesController.openByUserName(str.substring(1), this.f709a.f713a, 0);
                } else if (str.startsWith("#")) {
                    BaseFragment dialogsActivity = new DialogsActivity(null);
                    dialogsActivity.setSearchString(str);
                    this.f709a.f713a.presentFragment(dialogsActivity);
                } else if (str.startsWith("/")) {
                    this.f709a.f713a.f749b.setCommand(null, str, false, false);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.download.a.a.4 */
        class DownloadManagerActivity implements OnPreDrawListener {
            final /* synthetic */ ChatMessageCell f710a;
            final /* synthetic */ DownloadManagerActivity f711b;

            DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity, ChatMessageCell chatMessageCell) {
                this.f711b = downloadManagerActivity;
                this.f710a = chatMessageCell;
            }

            public boolean onPreDraw() {
                this.f710a.getViewTreeObserver().removeOnPreDrawListener(this);
                this.f710a.getLocalVisibleRect(this.f711b.f713a.aX);
                this.f710a.setVisiblePart(this.f711b.f713a.aX.top, this.f711b.f713a.aX.bottom - this.f711b.f713a.aX.top);
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.download.a.a.a */
        private class DownloadManagerActivity extends ViewHolder {
            final /* synthetic */ DownloadManagerActivity f712a;

            public DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity, View view) {
                this.f712a = downloadManagerActivity;
                super(view);
            }
        }

        public DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity, Context context) {
            this.f713a = downloadManagerActivity;
            this.f716d = -1;
            this.f714b = context;
        }

        public void m639a() {
            this.f715c = 0;
            this.f716d = -1;
            if (this.f713a.f750c.isEmpty()) {
                this.f717e = -1;
                this.f718f = -1;
                this.f719g = -1;
                this.f720h = -1;
                return;
            }
            if (this.f713a.aD[0] && (this.f713a.aK == 0 || this.f713a.aD[1])) {
                this.f717e = -1;
            } else {
                int i = this.f715c;
                this.f715c = i + 1;
                this.f717e = i;
            }
            this.f719g = this.f715c;
            this.f715c += this.f713a.f750c.size();
            this.f720h = this.f715c;
            if (this.f713a.aF[0] && (this.f713a.aK == 0 || this.f713a.aF[1])) {
                this.f718f = -1;
                return;
            }
            i = this.f715c;
            this.f715c = i + 1;
            this.f718f = i;
        }

        public void m640a(MessageObject messageObject) {
            int indexOf = this.f713a.f750c.indexOf(messageObject);
            if (indexOf != -1) {
                this.f713a.f750c.remove(indexOf);
                notifyItemRemoved(((this.f719g + this.f713a.f750c.size()) - indexOf) - 1);
            }
        }

        public int getItemCount() {
            return this.f715c;
        }

        public long getItemId(int i) {
            return -1;
        }

        public int getItemViewType(int i) {
            return (i < this.f719g || i >= this.f720h) ? i == this.f716d ? 3 : 4 : ((MessageObject) this.f713a.f750c.get((this.f713a.f750c.size() - (i - this.f719g)) - 1)).contentType;
        }

        public void notifyDataSetChanged() {
            m639a();
            try {
                super.notifyDataSetChanged();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemChanged(int i) {
            m639a();
            try {
                super.notifyItemChanged(i);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemInserted(int i) {
            m639a();
            try {
                super.notifyItemInserted(i);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemMoved(int i, int i2) {
            m639a();
            try {
                super.notifyItemMoved(i, i2);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemRangeChanged(int i, int i2) {
            m639a();
            try {
                super.notifyItemRangeChanged(i, i2);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemRangeInserted(int i, int i2) {
            m639a();
            try {
                super.notifyItemRangeInserted(i, i2);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            m639a();
            try {
                super.notifyItemRangeRemoved(i, i2);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemRemoved(int i) {
            m639a();
            try {
                super.notifyItemRemoved(i);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = true;
            if (i == this.f718f || i == this.f717e) {
                ((ChatLoadingCell) viewHolder.itemView).setVisibility(8);
            } else if (i >= this.f719g && i < this.f720h) {
                boolean z2;
                boolean z3;
                MessageObject messageObject = (MessageObject) this.f713a.f750c.get((this.f713a.f750c.size() - (i - this.f719g)) - 1);
                View view = viewHolder.itemView;
                if (this.f713a.actionBar.isActionModeShowed()) {
                    boolean z4;
                    if (this.f713a.at[messageObject.getDialogId() == this.f713a.aq ? 0 : 1].containsKey(Integer.valueOf(messageObject.getId()))) {
                        view.setBackgroundColor(Theme.MSG_SELECTED_BACKGROUND_COLOR);
                        z4 = true;
                    } else {
                        view.setBackgroundColor(0);
                        z4 = false;
                    }
                    z2 = true;
                    z3 = z4;
                } else {
                    view.setBackgroundColor(0);
                    z2 = false;
                    z3 = false;
                }
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                    chatMessageCell.isChat = this.f713a.f748a != null;
                    chatMessageCell.setMessageObject(messageObject);
                    boolean z5 = !z2;
                    z2 = z2 && z3;
                    chatMessageCell.setCheckPressed(z5, z2);
                    if ((view instanceof ChatMessageCell) && (MediaController.m71a().m157a(2) || chatMessageCell.isFavoriteAndAutoDownload())) {
                        ((ChatMessageCell) view).downloadAudioIfNeed();
                    }
                    if (this.f713a.aU == ConnectionsManager.DEFAULT_DATACENTER_ID || messageObject.getId() != this.f713a.aU) {
                        z = false;
                    }
                    chatMessageCell.setHighlighted(z);
                } else if (view instanceof ChatActionCell) {
                    ((ChatActionCell) view).setMessageObject(messageObject);
                } else if (view instanceof ChatUnreadCell) {
                    ((ChatUnreadCell) view).setText(LocaleController.formatPluralString("NewMessages", this.f713a.aP));
                }
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            if (i == 0) {
                if (this.f713a.f754h.isEmpty()) {
                    view = new ChatMessageCell(this.f714b);
                } else {
                    View view2 = (View) this.f713a.f754h.get(0);
                    this.f713a.f754h.remove(0);
                    view = view2;
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setDelegate(new DownloadManagerActivity(this));
                chatMessageCell.setAllowAssistant(true);
            } else if (i == 1) {
                view = new ChatActionCell(this.f714b);
                ((ChatActionCell) view).setDelegate(new DownloadManagerActivity(this));
            } else if (i == 2) {
                view = new ChatUnreadCell(this.f714b);
            } else if (i == 3) {
                view = new BotHelpCell(this.f714b);
                ((BotHelpCell) view).setDelegate(new DownloadManagerActivity(this));
            } else if (i == 4) {
                view = new ChatLoadingCell(this.f714b);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new DownloadManagerActivity(this, view);
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) viewHolder.itemView;
                chatMessageCell.getViewTreeObserver().addOnPreDrawListener(new DownloadManagerActivity(this, chatMessageCell));
                boolean z = this.f713a.aU != ConnectionsManager.DEFAULT_DATACENTER_ID && chatMessageCell.getMessageObject().getId() == this.f713a.aU;
                chatMessageCell.setHighlighted(z);
            }
        }
    }

    static {
        f721e = false;
    }

    public DownloadManagerActivity(Bundle bundle) {
        super(bundle);
        this.f754h = new ArrayList();
        this.f771y = new ArrayList();
        this.f745X = true;
        this.ac = true;
        this.ad = false;
        this.ae = false;
        this.at = new HashMap[]{new HashMap(), new HashMap()};
        this.au = new HashMap[]{new HashMap(), new HashMap()};
        this.aw = new ArrayList();
        this.ax = new HashMap[]{new HashMap(), new HashMap()};
        this.ay = new HashMap();
        this.f750c = new ArrayList();
        this.az = new int[]{ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID};
        this.aA = new int[]{TLRPC.MESSAGE_FLAG_MEGAGROUP, TLRPC.MESSAGE_FLAG_MEGAGROUP};
        this.aB = new int[]{TLRPC.MESSAGE_FLAG_MEGAGROUP, TLRPC.MESSAGE_FLAG_MEGAGROUP};
        this.aC = new int[2];
        this.aD = new boolean[2];
        this.aE = new boolean[2];
        this.aF = new boolean[]{true, true};
        this.aH = true;
        this.aJ = 0;
        this.aO = true;
        this.aU = ConnectionsManager.DEFAULT_DATACENTER_ID;
        this.aV = -10000;
        this.aX = new Rect();
        this.f751d = null;
        this.aY = -1;
        this.ba = null;
        this.bb = null;
        this.bc = 0.0f;
        this.bd = 0.0f;
        this.f752f = new DownloadManagerActivity(this);
        this.f753g = new OnItemClickListener() {
            final /* synthetic */ DownloadManagerActivity f650a;

            {
                this.f650a = r1;
            }

            public void onItemClick(View view, int i) {
                if (this.f650a.actionBar.isActionModeShowed()) {
                    this.f650a.m686a(view);
                } else {
                    this.f650a.m687a(view, true);
                }
            }
        };
    }

    private void m642A() {
        this.bh = false;
        Iterator it = new ArrayList(this.f750c).iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            if (FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                m684a(messageObject.getId(), messageObject.messageOwner.to_id.channel_id);
            }
        }
    }

    private void m644B() {
        SettingManager settingManager = new SettingManager();
        if (!settingManager.m944b("downloadingHelpDisplayed")) {
            settingManager.m943a("downloadingHelpDisplayed", true);
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("Help", C0338R.string.Help)).setMessage(LocaleController.getString("DownloadManagerDownloadHelp", C0338R.string.DownloadManagerDownloadHelp));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                final /* synthetic */ DownloadManagerActivity f679a;

                {
                    this.f679a = r1;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }
        if (!settingManager.m944b("downloadingSwitchBtnHelpDisplayed")) {
            settingManager.m943a("downloadingSwitchBtnHelpDisplayed", true);
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
            builder2.setTitle(LocaleController.getString("Help", C0338R.string.Help)).setMessage(LocaleController.getString("DownloadManagerDownloadSwithBtnHelp", C0338R.string.DownloadManagerDownloadSwithBtnHelp));
            builder2.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                final /* synthetic */ DownloadManagerActivity f680a;

                {
                    this.f680a = r1;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder2.create().show();
        }
    }

    private void m645C() {
        ActionBarMenuItem item = this.actionBar.createActionMode().getItem(191);
        if (item != null) {
            item.setVisibility(8);
            for (int i = 1; i >= 0; i--) {
                if (new ArrayList(this.at[i].keySet()).size() > 1) {
                    item.setVisibility(0);
                    return;
                }
            }
        }
    }

    private void m648D() {
        for (int i = 1; i >= 0; i--) {
            ArrayList arrayList = new ArrayList(this.at[i].keySet());
            Collections.sort(arrayList);
            if (arrayList.size() > 1) {
                int i2;
                MessageObject messageObject;
                List arrayList2 = new ArrayList();
                for (i2 = 0; i2 < this.f750c.size(); i2++) {
                    messageObject = (MessageObject) this.f750c.get(i2);
                    if (messageObject.getId() == ((Integer) arrayList.get(0)).intValue() || messageObject.getId() == ((Integer) arrayList.get(arrayList.size() - 1)).intValue()) {
                        arrayList2.add(Integer.valueOf(i2));
                        if (arrayList2.size() == 2) {
                            break;
                        }
                    }
                }
                if (arrayList2.size() == 2) {
                    for (int intValue = ((Integer) arrayList2.get(0)).intValue() + 1; intValue < ((Integer) arrayList2.get(1)).intValue(); intValue++) {
                        messageObject = (MessageObject) this.f750c.get(intValue);
                        if (messageObject.getId() > 0) {
                            i2 = m673a(messageObject);
                            if (i2 >= 2 && i2 != 20) {
                                if (!this.at[messageObject.getDialogId() == this.aq ? 0 : 1].containsKey(Integer.valueOf(messageObject.getId()))) {
                                    m709b(messageObject);
                                }
                            }
                        }
                    }
                    m733i();
                    m749q();
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

    private void m650E() {
        if (ThemeUtil.m2490b()) {
            try {
                int i = AdvanceTheme.bg;
                this.actionBar.setBackgroundColor(i);
                int i2 = AdvanceTheme.bR;
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
                    int i3 = AdvanceTheme.bS;
                    this.actionBar.setBackgroundDrawable(new GradientDrawable(orientation, new int[]{i, i3}));
                }
                this.f772z.setTextColor(AdvanceTheme.bh);
                this.f722A.setTextColor(AdvanceTheme.bT);
                i2 = AdvanceTheme.bi;
                if (getParentActivity() != null) {
                    getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_other).setColorFilter(i2, Mode.MULTIPLY);
                    getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_back).setColorFilter(i2, Mode.MULTIPLY);
                    getParentActivity().getResources().getDrawable(C0338R.drawable.ic_download_avatar).setColorFilter(i2, Mode.MULTIPLY);
                    this.f761o.setColorFilter(i2, Mode.SRC_IN);
                }
                this.f766t.setBackgroundColor(i);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    private int m673a(MessageObject messageObject) {
        int i = 1;
        if (messageObject == null) {
            return -1;
        }
        int i2 = (this.as && messageObject.getId() <= 0 && messageObject.isSendError()) ? 1 : 0;
        if ((!this.as && messageObject.getId() <= 0 && messageObject.isOut()) || i2 != 0) {
            return messageObject.isSendError() ? messageObject.isMediaEmpty() ? 20 : 0 : -1;
        } else {
            if (messageObject.type == 6) {
                return -1;
            }
            if (messageObject.type == 10 || messageObject.type == 11) {
                return messageObject.getId() == 0 ? -1 : 1;
            } else {
                if (messageObject.isMediaEmpty()) {
                    return 3;
                }
                if (messageObject.isVoice()) {
                    return 2;
                }
                if (messageObject.isSticker()) {
                    InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                    if (inputStickerSet instanceof TL_inputStickerSetID) {
                        if (!StickersQuery.isStickerPackInstalled(inputStickerSet.id)) {
                            return 7;
                        }
                    } else if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !StickersQuery.isStickerPackInstalled(inputStickerSet.short_name)) {
                        return 7;
                    }
                } else if ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || (messageObject.messageOwner.media instanceof TL_messageMediaDocument)) {
                    i2 = (messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists()) ? 0 : 1;
                    if (!(i2 == 0 && FileLoader.getPathToMessage(messageObject.messageOwner).exists())) {
                        i = i2;
                    }
                    if (i != 0) {
                        if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                            String str = messageObject.messageOwner.media.document.mime_type;
                            if (str != null) {
                                if (str.endsWith("/xml")) {
                                    return 5;
                                }
                                if (str.endsWith("/png") || str.endsWith("/jpg") || str.endsWith("/jpeg")) {
                                    return 6;
                                }
                            }
                        }
                        return 4;
                    }
                }
                return 2;
            }
        }
    }

    private ArrayList<MessageObject> m679a(MessageObject messageObject, boolean z) {
        ArrayList<MessageObject> arrayList = new ArrayList();
        arrayList.add(messageObject);
        if (messageObject.getId() != 0) {
            for (int size = this.f750c.size() - 1; size >= 0; size--) {
                arrayList.add((MessageObject) this.f750c.get(size));
            }
        }
        return arrayList;
    }

    private void m682a() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        builder.setMessage(LocaleController.getString("DownloadCountExceedMessage", C0338R.string.DownloadCountExceedMessage));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
        showDialog(builder.create());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void m683a(int r15) {
        /*
        r14 = this;
        r4 = 4;
        r2 = 3;
        r8 = 1;
        r7 = 0;
        r9 = 0;
        r0 = r14.f746Y;
        if (r0 != 0) goto L_0x000a;
    L_0x0009:
        return;
    L_0x000a:
        if (r15 != 0) goto L_0x001e;
    L_0x000c:
        r0 = com.hanista.mobogram.messenger.SendMessagesHelper.getInstance();
        r1 = r14.f746Y;
        r0 = r0.retrySendMessage(r1, r7);
        if (r0 == 0) goto L_0x001b;
    L_0x0018:
        r14.m718d();
    L_0x001b:
        r14.f746Y = r9;
        goto L_0x0009;
    L_0x001e:
        if (r15 != r8) goto L_0x00cb;
    L_0x0020:
        r0 = r14.getParentActivity();
        if (r0 != 0) goto L_0x0029;
    L_0x0026:
        r14.f746Y = r9;
        goto L_0x0009;
    L_0x0029:
        r10 = r14.f746Y;
        if (r10 == 0) goto L_0x0438;
    L_0x002d:
        r10.checkMediaExistance();
        r0 = r10.mediaExists;
    L_0x0032:
        r11 = new android.app.AlertDialog$Builder;
        r1 = r14.getParentActivity();
        r11.<init>(r1);
        r1 = com.hanista.mobogram.mobo.MoboConstants.aE;
        r14.bh = r1;
        if (r0 == 0) goto L_0x007e;
    L_0x0041:
        r12 = new android.widget.FrameLayout;
        r0 = r14.getParentActivity();
        r12.<init>(r0);
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 21;
        if (r0 < r1) goto L_0x0059;
    L_0x0050:
        r0 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r0 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r0);
        r12.setPadding(r7, r0, r7, r7);
    L_0x0059:
        r0 = r14.getParentActivity();
        r13 = com.hanista.mobogram.messenger.AndroidUtilities.createDeleteFileCheckBox(r0);
        r0 = -1;
        r1 = 1111490560; // 0x42400000 float:48.0 double:5.491493014E-315;
        r2 = 51;
        r3 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = 0;
        r5 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = 0;
        r0 = com.hanista.mobogram.ui.Components.LayoutHelper.createFrame(r0, r1, r2, r3, r4, r5, r6);
        r12.addView(r13, r0);
        r0 = new com.hanista.mobogram.mobo.download.a$21;
        r0.<init>(r14);
        r13.setOnClickListener(r0);
        r11.setView(r12);
    L_0x007e:
        r0 = "AreYouSureDeleteMessages";
        r1 = 2131165319; // 0x7f070087 float:1.7944852E38 double:1.0529355697E-314;
        r2 = new java.lang.Object[r8];
        r3 = "messages";
        r3 = com.hanista.mobogram.messenger.LocaleController.formatPluralString(r3, r8);
        r2[r7] = r3;
        r0 = com.hanista.mobogram.messenger.LocaleController.formatString(r0, r1, r2);
        r11.setMessage(r0);
        r0 = "AppName";
        r1 = 2131166486; // 0x7f070516 float:1.7947219E38 double:1.0529361463E-314;
        r0 = com.hanista.mobogram.messenger.LocaleController.getString(r0, r1);
        r11.setTitle(r0);
        r0 = "OK";
        r1 = 2131166046; // 0x7f07035e float:1.7946326E38 double:1.052935929E-314;
        r0 = com.hanista.mobogram.messenger.LocaleController.getString(r0, r1);
        r1 = new com.hanista.mobogram.mobo.download.a$22;
        r1.<init>(r14, r10);
        r11.setPositiveButton(r0, r1);
        r0 = "Cancel";
        r1 = 2131165385; // 0x7f0700c9 float:1.7944986E38 double:1.0529356023E-314;
        r0 = com.hanista.mobogram.messenger.LocaleController.getString(r0, r1);
        r11.setNegativeButton(r0, r9);
        r0 = r11.create();
        r14.showDialog(r0);
        goto L_0x001b;
    L_0x00cb:
        r0 = 2;
        if (r15 != r0) goto L_0x00d9;
    L_0x00ce:
        f721e = r7;
        r0 = r14.f746Y;
        r14.aa = r0;
        r14.m751r();
        goto L_0x001b;
    L_0x00d9:
        r0 = 22;
        if (r15 != r0) goto L_0x00e8;
    L_0x00dd:
        f721e = r8;
        r0 = r14.f746Y;
        r14.aa = r0;
        r14.m751r();
        goto L_0x001b;
    L_0x00e8:
        r0 = 23;
        if (r15 != r0) goto L_0x00f7;
    L_0x00ec:
        f721e = r7;
        r0 = r14.f746Y;
        r14.aa = r0;
        r14.m751r();
        goto L_0x001b;
    L_0x00f7:
        r0 = 25;
        if (r15 != r0) goto L_0x0141;
    L_0x00fb:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0119 }
        r1 = 11;
        if (r0 >= r1) goto L_0x0122;
    L_0x0101:
        r0 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0119 }
        r1 = "clipboard";
        r0 = r0.getSystemService(r1);	 Catch:{ Exception -> 0x0119 }
        r0 = (android.text.ClipboardManager) r0;	 Catch:{ Exception -> 0x0119 }
        r1 = r14.f746Y;	 Catch:{ Exception -> 0x0119 }
        r1 = r1.messageOwner;	 Catch:{ Exception -> 0x0119 }
        r1 = r1.media;	 Catch:{ Exception -> 0x0119 }
        r1 = r1.caption;	 Catch:{ Exception -> 0x0119 }
        r0.setText(r1);	 Catch:{ Exception -> 0x0119 }
        goto L_0x001b;
    L_0x0119:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
        goto L_0x001b;
    L_0x0122:
        r0 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0119 }
        r1 = "clipboard";
        r0 = r0.getSystemService(r1);	 Catch:{ Exception -> 0x0119 }
        r0 = (android.content.ClipboardManager) r0;	 Catch:{ Exception -> 0x0119 }
        r1 = "label";
        r2 = r14.f746Y;	 Catch:{ Exception -> 0x0119 }
        r2 = r2.messageOwner;	 Catch:{ Exception -> 0x0119 }
        r2 = r2.media;	 Catch:{ Exception -> 0x0119 }
        r2 = r2.caption;	 Catch:{ Exception -> 0x0119 }
        r1 = android.content.ClipData.newPlainText(r1, r2);	 Catch:{ Exception -> 0x0119 }
        r0.setPrimaryClip(r1);	 Catch:{ Exception -> 0x0119 }
        goto L_0x001b;
    L_0x0141:
        r0 = 26;
        if (r15 != r0) goto L_0x014a;
    L_0x0145:
        r14.m760v();
        goto L_0x001b;
    L_0x014a:
        if (r15 != r2) goto L_0x018a;
    L_0x014c:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0166 }
        r1 = 11;
        if (r0 >= r1) goto L_0x016f;
    L_0x0152:
        r0 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0166 }
        r1 = "clipboard";
        r0 = r0.getSystemService(r1);	 Catch:{ Exception -> 0x0166 }
        r0 = (android.text.ClipboardManager) r0;	 Catch:{ Exception -> 0x0166 }
        r1 = r14.f746Y;	 Catch:{ Exception -> 0x0166 }
        r1 = r1.messageText;	 Catch:{ Exception -> 0x0166 }
        r0.setText(r1);	 Catch:{ Exception -> 0x0166 }
        goto L_0x001b;
    L_0x0166:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
        goto L_0x001b;
    L_0x016f:
        r0 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0166 }
        r1 = "clipboard";
        r0 = r0.getSystemService(r1);	 Catch:{ Exception -> 0x0166 }
        r0 = (android.content.ClipboardManager) r0;	 Catch:{ Exception -> 0x0166 }
        r1 = "label";
        r2 = r14.f746Y;	 Catch:{ Exception -> 0x0166 }
        r2 = r2.messageText;	 Catch:{ Exception -> 0x0166 }
        r1 = android.content.ClipData.newPlainText(r1, r2);	 Catch:{ Exception -> 0x0166 }
        r0.setPrimaryClip(r1);	 Catch:{ Exception -> 0x0166 }
        goto L_0x001b;
    L_0x018a:
        if (r15 != r4) goto L_0x01fb;
    L_0x018c:
        r0 = r14.f746Y;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x01a6;
    L_0x0194:
        r1 = r0.length();
        if (r1 <= 0) goto L_0x01a6;
    L_0x019a:
        r1 = new java.io.File;
        r1.<init>(r0);
        r1 = r1.exists();
        if (r1 != 0) goto L_0x01a6;
    L_0x01a5:
        r0 = r9;
    L_0x01a6:
        if (r0 == 0) goto L_0x01ae;
    L_0x01a8:
        r1 = r0.length();
        if (r1 != 0) goto L_0x01ba;
    L_0x01ae:
        r0 = r14.f746Y;
        r0 = r0.messageOwner;
        r0 = com.hanista.mobogram.messenger.FileLoader.getPathToMessage(r0);
        r0 = r0.toString();
    L_0x01ba:
        r1 = r14.f746Y;
        r1 = r1.type;
        if (r1 == r2) goto L_0x01c6;
    L_0x01c0:
        r1 = r14.f746Y;
        r1 = r1.type;
        if (r1 != r8) goto L_0x001b;
    L_0x01c6:
        r1 = android.os.Build.VERSION.SDK_INT;
        r3 = 23;
        if (r1 < r3) goto L_0x01eb;
    L_0x01cc:
        r1 = r14.getParentActivity();
        r3 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1 = r1.checkSelfPermission(r3);
        if (r1 == 0) goto L_0x01eb;
    L_0x01d9:
        r0 = r14.getParentActivity();
        r1 = new java.lang.String[r8];
        r2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1[r7] = r2;
        r0.requestPermissions(r1, r4);
        r14.f746Y = r9;
        goto L_0x0009;
    L_0x01eb:
        r1 = r14.getParentActivity();
        r3 = r14.f746Y;
        r3 = r3.type;
        if (r3 != r2) goto L_0x01f6;
    L_0x01f5:
        r7 = r8;
    L_0x01f6:
        com.hanista.mobogram.messenger.MediaController.m84a(r0, r1, r7, r9, r9);
        goto L_0x001b;
    L_0x01fb:
        r0 = 5;
        if (r15 != r0) goto L_0x028c;
    L_0x01fe:
        r0 = r14.f746Y;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x0435;
    L_0x0206:
        r0 = r14.f746Y;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        r0 = r0.length();
        if (r0 == 0) goto L_0x0435;
    L_0x0212:
        r1 = new java.io.File;
        r0 = r14.f746Y;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        r1.<init>(r0);
        r0 = r1.exists();
        if (r0 == 0) goto L_0x0435;
    L_0x0223:
        if (r1 != 0) goto L_0x0432;
    L_0x0225:
        r0 = r14.f746Y;
        r0 = r0.messageOwner;
        r0 = com.hanista.mobogram.messenger.FileLoader.getPathToMessage(r0);
        r2 = r0.exists();
        if (r2 == 0) goto L_0x0432;
    L_0x0233:
        if (r0 == 0) goto L_0x001b;
    L_0x0235:
        r1 = com.hanista.mobogram.messenger.LocaleController.getInstance();
        r0 = r1.applyLanguageFile(r0);
        if (r0 == 0) goto L_0x0249;
    L_0x023f:
        r0 = new com.hanista.mobogram.ui.LanguageSelectActivity;
        r0.<init>();
        r14.presentFragment(r0);
        goto L_0x001b;
    L_0x0249:
        r0 = r14.getParentActivity();
        if (r0 != 0) goto L_0x0253;
    L_0x024f:
        r14.f746Y = r9;
        goto L_0x0009;
    L_0x0253:
        r0 = new android.app.AlertDialog$Builder;
        r1 = r14.getParentActivity();
        r0.<init>(r1);
        r1 = "AppName";
        r2 = 2131166486; // 0x7f070516 float:1.7947219E38 double:1.0529361463E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
        r0.setTitle(r1);
        r1 = "IncorrectLocalization";
        r2 = 2131165753; // 0x7f070239 float:1.7945732E38 double:1.052935784E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
        r0.setMessage(r1);
        r1 = "OK";
        r2 = 2131166046; // 0x7f07035e float:1.7946326E38 double:1.052935929E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
        r0.setPositiveButton(r1, r9);
        r0 = r0.create();
        r14.showDialog(r0);
        goto L_0x001b;
    L_0x028c:
        r0 = 6;
        if (r15 != r0) goto L_0x02fa;
    L_0x028f:
        r0 = r14.f746Y;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x02a9;
    L_0x0297:
        r1 = r0.length();
        if (r1 <= 0) goto L_0x02a9;
    L_0x029d:
        r1 = new java.io.File;
        r1.<init>(r0);
        r1 = r1.exists();
        if (r1 != 0) goto L_0x02a9;
    L_0x02a8:
        r0 = r9;
    L_0x02a9:
        if (r0 == 0) goto L_0x02b1;
    L_0x02ab:
        r1 = r0.length();
        if (r1 != 0) goto L_0x02bd;
    L_0x02b1:
        r0 = r14.f746Y;
        r0 = r0.messageOwner;
        r0 = com.hanista.mobogram.messenger.FileLoader.getPathToMessage(r0);
        r0 = r0.toString();
    L_0x02bd:
        r1 = new android.content.Intent;
        r2 = "android.intent.action.SEND";
        r1.<init>(r2);
        r2 = r14.f746Y;
        r2 = r2.messageOwner;
        r2 = r2.media;
        r2 = r2.document;
        r2 = r2.mime_type;
        r1.setType(r2);
        r2 = "android.intent.extra.STREAM";
        r3 = new java.io.File;
        r3.<init>(r0);
        r0 = android.net.Uri.fromFile(r3);
        r1.putExtra(r2, r0);
        r0 = r14.getParentActivity();
        r2 = "ShareFile";
        r3 = 2131166276; // 0x7f070444 float:1.7946793E38 double:1.0529360425E-314;
        r2 = com.hanista.mobogram.messenger.LocaleController.getString(r2, r3);
        r1 = android.content.Intent.createChooser(r1, r2);
        r2 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r0.startActivityForResult(r1, r2);
        goto L_0x001b;
    L_0x02fa:
        r0 = 7;
        if (r15 != r0) goto L_0x0359;
    L_0x02fd:
        r0 = r14.f746Y;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x0317;
    L_0x0305:
        r1 = r0.length();
        if (r1 <= 0) goto L_0x0317;
    L_0x030b:
        r1 = new java.io.File;
        r1.<init>(r0);
        r1 = r1.exists();
        if (r1 != 0) goto L_0x0317;
    L_0x0316:
        r0 = r9;
    L_0x0317:
        if (r0 == 0) goto L_0x031f;
    L_0x0319:
        r1 = r0.length();
        if (r1 != 0) goto L_0x032b;
    L_0x031f:
        r0 = r14.f746Y;
        r0 = r0.messageOwner;
        r0 = com.hanista.mobogram.messenger.FileLoader.getPathToMessage(r0);
        r0 = r0.toString();
    L_0x032b:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 23;
        if (r1 < r2) goto L_0x0350;
    L_0x0331:
        r1 = r14.getParentActivity();
        r2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1 = r1.checkSelfPermission(r2);
        if (r1 == 0) goto L_0x0350;
    L_0x033e:
        r0 = r14.getParentActivity();
        r1 = new java.lang.String[r8];
        r2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1[r7] = r2;
        r0.requestPermissions(r1, r4);
        r14.f746Y = r9;
        goto L_0x0009;
    L_0x0350:
        r1 = r14.getParentActivity();
        com.hanista.mobogram.messenger.MediaController.m84a(r0, r1, r7, r9, r9);
        goto L_0x001b;
    L_0x0359:
        r0 = 9;
        if (r15 != r0) goto L_0x037f;
    L_0x035d:
        r0 = new com.hanista.mobogram.ui.Components.StickersAlert;
        r1 = r14.getParentActivity();
        r2 = r14.f746Y;
        r3 = r2.getInputStickerSet();
        r2 = r14.f766t;
        r2 = r2.getVisibility();
        if (r2 == 0) goto L_0x037d;
    L_0x0371:
        r5 = r14.f749b;
    L_0x0373:
        r2 = r14;
        r4 = r9;
        r0.<init>(r1, r2, r3, r4, r5);
        r14.showDialog(r0);
        goto L_0x001b;
    L_0x037d:
        r5 = r9;
        goto L_0x0373;
    L_0x037f:
        r0 = 10;
        if (r15 != r0) goto L_0x0417;
    L_0x0383:
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 23;
        if (r0 < r1) goto L_0x03a8;
    L_0x0389:
        r0 = r14.getParentActivity();
        r1 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r0 = r0.checkSelfPermission(r1);
        if (r0 == 0) goto L_0x03a8;
    L_0x0396:
        r0 = r14.getParentActivity();
        r1 = new java.lang.String[r8];
        r2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1[r7] = r2;
        r0.requestPermissions(r1, r4);
        r14.f746Y = r9;
        goto L_0x0009;
    L_0x03a8:
        r0 = r14.f746Y;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.document;
        r0 = com.hanista.mobogram.messenger.FileLoader.getDocumentFileName(r0);
        if (r0 == 0) goto L_0x03bc;
    L_0x03b6:
        r1 = r0.length();
        if (r1 != 0) goto L_0x03c2;
    L_0x03bc:
        r0 = r14.f746Y;
        r0 = r0.getFileName();
    L_0x03c2:
        r1 = r14.f746Y;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        if (r1 == 0) goto L_0x03dc;
    L_0x03ca:
        r3 = r1.length();
        if (r3 <= 0) goto L_0x03dc;
    L_0x03d0:
        r3 = new java.io.File;
        r3.<init>(r1);
        r3 = r3.exists();
        if (r3 != 0) goto L_0x03dc;
    L_0x03db:
        r1 = r9;
    L_0x03dc:
        if (r1 == 0) goto L_0x03e4;
    L_0x03de:
        r3 = r1.length();
        if (r3 != 0) goto L_0x03f0;
    L_0x03e4:
        r1 = r14.f746Y;
        r1 = r1.messageOwner;
        r1 = com.hanista.mobogram.messenger.FileLoader.getPathToMessage(r1);
        r1 = r1.toString();
    L_0x03f0:
        r4 = r14.getParentActivity();
        r3 = r14.f746Y;
        r3 = r3.isMusic();
        if (r3 == 0) goto L_0x0411;
    L_0x03fc:
        r3 = r14.f746Y;
        r3 = r3.getDocument();
        if (r3 == 0) goto L_0x0413;
    L_0x0404:
        r3 = r14.f746Y;
        r3 = r3.getDocument();
        r3 = r3.mime_type;
    L_0x040c:
        com.hanista.mobogram.messenger.MediaController.m84a(r1, r4, r2, r0, r3);
        goto L_0x001b;
    L_0x0411:
        r2 = 2;
        goto L_0x03fc;
    L_0x0413:
        r3 = "";
        goto L_0x040c;
    L_0x0417:
        r0 = 11;
        if (r15 != r0) goto L_0x001b;
    L_0x041b:
        r0 = r14.f746Y;
        r0 = r0.getDocument();
        r1 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r1.saveGif(r0);
        r14.m708b();
        r1 = r14.f749b;
        r1.addRecentGif(r0);
        goto L_0x001b;
    L_0x0432:
        r0 = r1;
        goto L_0x0233;
    L_0x0435:
        r1 = r9;
        goto L_0x0223;
    L_0x0438:
        r0 = r7;
        goto L_0x0032;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.mobo.download.a.a(int):void");
    }

    private void m684a(int i, int i2) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(i));
        DownloadMessagesStorage.m783a().m816a(arrayList, true, i2, this.bh);
        DownloadMessagesStorage.m783a().m815a(arrayList, true, i2);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(i2));
    }

    private void m685a(int i, int i2, boolean z, int i3) {
        MessageObject messageObject = (MessageObject) this.ax[i3].get(Integer.valueOf(i));
        boolean z2;
        if (messageObject == null) {
            z2 = true;
        } else if (this.f750c.indexOf(messageObject) != -1) {
            if (z) {
                this.aU = i;
            } else {
                this.aU = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            int max = Math.max(0, (this.f758l.getHeight() - messageObject.getApproximateHeight()) / 2);
            if (this.f750c.get(this.f750c.size() - 1) == messageObject) {
                this.f759m.scrollToPositionWithOffset(0, max + ((-this.f758l.getPaddingTop()) - AndroidUtilities.dp(7.0f)));
            } else {
                this.f759m.scrollToPositionWithOffset(((this.f760n.f719g + this.f750c.size()) - this.f750c.indexOf(messageObject)) - 1, max + ((-this.f758l.getPaddingTop()) - AndroidUtilities.dp(7.0f)));
            }
            m749q();
            int childCount = this.f758l.getChildCount();
            for (int i4 = 0; i4 < childCount; i4++) {
                View childAt = this.f758l.getChildAt(i4);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    if (chatMessageCell.getMessageObject() != null && chatMessageCell.getMessageObject().getId() == messageObject.getId()) {
                        z2 = true;
                        break;
                    }
                } else if (childAt instanceof ChatActionCell) {
                    ChatActionCell chatActionCell = (ChatActionCell) childAt;
                    if (chatActionCell.getMessageObject() != null && chatActionCell.getMessageObject().getId() == messageObject.getId()) {
                        z2 = true;
                        break;
                    }
                } else {
                    continue;
                }
            }
            z2 = false;
            if (!z2) {
                m701a(true, true);
            }
            int i5 = 0;
        } else {
            z2 = true;
        }
        if (i5 != 0) {
            this.aU = ConnectionsManager.DEFAULT_DATACENTER_ID;
            this.aV = -10000;
            this.aL = i;
            this.aw.add(Integer.valueOf(this.ar));
            DownloadMessagesStorage a = DownloadMessagesStorage.m783a();
            long j = i3 == 0 ? this.aq : this.aK;
            int i6 = this.aL;
            int i7 = this.classGuid;
            int i8 = i3 == 0 ? this.f741T : 0;
            int i9 = this.ar;
            this.ar = i9 + 1;
            a.m810a(j, 300, i6, 0, i7, 3, i8, i9);
            this.f770x.setVisibility(4);
        }
        this.aN = i2;
        this.aM = z;
    }

    private void m686a(View view) {
        MessageObject messageObject = null;
        if (view instanceof ChatMessageCell) {
            messageObject = ((ChatMessageCell) view).getMessageObject();
        } else if (view instanceof ChatActionCell) {
            messageObject = ((ChatActionCell) view).getMessageObject();
        }
        int a = m673a(messageObject);
        if (a >= 2 && a != 20) {
            m709b(messageObject);
            m733i();
            m749q();
        }
    }

    private void m687a(View view, boolean z) {
        if (!this.actionBar.isActionModeShowed()) {
            MessageObject messageObject = view instanceof ChatMessageCell ? ((ChatMessageCell) view).getMessageObject() : view instanceof ChatActionCell ? ((ChatActionCell) view).getMessageObject() : null;
            if (messageObject != null) {
                this.be = view;
                int a = m673a(messageObject);
                if (z && (messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) {
                    m685a(messageObject.messageOwner.reply_to_msg_id, 0, true, 0);
                    return;
                }
                this.f746Y = null;
                this.aa = null;
                for (int i = 1; i >= 0; i--) {
                    this.au[i].clear();
                    this.at[i].clear();
                }
                this.av = 0;
                this.actionBar.hideActionMode();
                AlertDialog.Builder builder;
                ArrayList arrayList;
                ArrayList arrayList2;
                if ((a == 1 && messageObject.getDialogId() == this.aK) || messageObject.getId() < 0 || this.as || this.f748a == null || ChatObject.isNotInChat(this.f748a) || !ChatObject.isChannel(this.f748a) || this.f748a.creator || this.f748a.editor || this.f748a.megagroup) {
                    if (!!z) {
                    }
                    if (a >= 0) {
                        this.f746Y = messageObject;
                        if (getParentActivity() != null) {
                            builder = new AlertDialog.Builder(getParentActivity());
                            arrayList = new ArrayList();
                            arrayList2 = new ArrayList();
                            if (a == 0) {
                                arrayList.add(LocaleController.getString("Retry", C0338R.string.Retry));
                                arrayList2.add(Integer.valueOf(0));
                                arrayList.add(LocaleController.getString("Delete", C0338R.string.Delete));
                                arrayList2.add(Integer.valueOf(1));
                            } else if (a == 1) {
                                if (this.f748a != null) {
                                }
                                if (messageObject.canDeleteMessage(this.f748a)) {
                                    arrayList.add(LocaleController.getString("Delete", C0338R.string.Delete));
                                    arrayList2.add(Integer.valueOf(1));
                                }
                            } else if (a != 20) {
                                if (a == 3) {
                                    arrayList.add(LocaleController.getString("Copy", C0338R.string.Copy));
                                    arrayList2.add(Integer.valueOf(3));
                                    arrayList.add(LocaleController.getString("SaveToGIFs", C0338R.string.SaveToGIFs));
                                    arrayList2.add(Integer.valueOf(11));
                                } else if (a != 4) {
                                    if (a == 5) {
                                        arrayList.add(LocaleController.getString("ApplyLocalizationFile", C0338R.string.ApplyLocalizationFile));
                                        arrayList2.add(Integer.valueOf(5));
                                        arrayList.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                        arrayList2.add(Integer.valueOf(6));
                                    } else if (a == 6) {
                                        arrayList.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                        arrayList2.add(Integer.valueOf(7));
                                        if (this.f746Y.isMusic()) {
                                        }
                                        arrayList.add(this.f746Y.isMusic() ? LocaleController.getString("SaveToMusic", C0338R.string.SaveToMusic) : LocaleController.getString("SaveToDownloads", C0338R.string.SaveToDownloads));
                                        arrayList2.add(Integer.valueOf(10));
                                        arrayList.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                        arrayList2.add(Integer.valueOf(6));
                                    } else if (a == 7) {
                                        arrayList.add(LocaleController.getString("AddToStickers", C0338R.string.AddToStickers));
                                        arrayList2.add(Integer.valueOf(9));
                                    }
                                } else if (this.f746Y.isVideo()) {
                                    arrayList.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                    arrayList2.add(Integer.valueOf(4));
                                    arrayList.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                    arrayList2.add(Integer.valueOf(6));
                                } else if (this.f746Y.messageOwner.media instanceof TL_messageMediaDocument) {
                                    if (MessageObject.isNewGifDocument(this.f746Y.messageOwner.media.document)) {
                                        arrayList.add(LocaleController.getString("SaveToGIFs", C0338R.string.SaveToGIFs));
                                        arrayList2.add(Integer.valueOf(11));
                                    }
                                    if (this.f746Y.isMusic()) {
                                    }
                                    arrayList.add(this.f746Y.isMusic() ? LocaleController.getString("SaveToMusic", C0338R.string.SaveToMusic) : LocaleController.getString("SaveToDownloads", C0338R.string.SaveToDownloads));
                                    arrayList2.add(Integer.valueOf(10));
                                    arrayList.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                    arrayList2.add(Integer.valueOf(6));
                                } else {
                                    arrayList.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                    arrayList2.add(Integer.valueOf(4));
                                }
                                arrayList.add(LocaleController.getString("CopyCaption", C0338R.string.CopyCaption));
                                arrayList2.add(Integer.valueOf(25));
                                if (a != 3) {
                                }
                                arrayList.add(LocaleController.getString("ProForward", C0338R.string.ProForward));
                                arrayList2.add(Integer.valueOf(26));
                                arrayList.add(LocaleController.getString("MultiForward", C0338R.string.MultiForward));
                                arrayList2.add(Integer.valueOf(23));
                                if (messageObject.canDeleteMessage(this.f748a)) {
                                    arrayList.add(LocaleController.getString("Delete", C0338R.string.Delete));
                                    arrayList2.add(Integer.valueOf(1));
                                }
                            } else {
                                arrayList.add(LocaleController.getString("Retry", C0338R.string.Retry));
                                arrayList2.add(Integer.valueOf(0));
                                arrayList.add(LocaleController.getString("Copy", C0338R.string.Copy));
                                arrayList2.add(Integer.valueOf(3));
                                arrayList.add(LocaleController.getString("Delete", C0338R.string.Delete));
                                arrayList2.add(Integer.valueOf(1));
                            }
                            if (!arrayList2.isEmpty()) {
                                builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new AnonymousClass20(this, arrayList2));
                                builder.setTitle(LocaleController.getString("Message", C0338R.string.Message));
                                showDialog(builder.create());
                            }
                        }
                    }
                } else if (!z && a >= 2 && a != 20) {
                    ActionBarMenu createActionMode = this.actionBar.createActionMode();
                    View item = createActionMode.getItem(11);
                    if (item != null) {
                        item.setVisibility(0);
                    }
                    View item2 = createActionMode.getItem(12);
                    if (item2 != null) {
                        item2.setVisibility(0);
                    }
                    this.actionBar.showActionMode();
                    AnimatorSet animatorSet = new AnimatorSet();
                    Collection arrayList3 = new ArrayList();
                    for (a = 0; a < this.f771y.size(); a++) {
                        item2 = (View) this.f771y.get(a);
                        AndroidUtilities.clearDrawableAnimation(item2);
                        arrayList3.add(ObjectAnimator.ofFloat(item2, "scaleY", new float[]{0.1f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    }
                    animatorSet.playTogether(arrayList3);
                    animatorSet.setDuration(250);
                    animatorSet.start();
                    m709b(messageObject);
                    this.f726E.setNumber(1, false);
                    m749q();
                } else if (a >= 0) {
                    this.f746Y = messageObject;
                    if (getParentActivity() != null) {
                        builder = new AlertDialog.Builder(getParentActivity());
                        arrayList = new ArrayList();
                        arrayList2 = new ArrayList();
                        if (a == 0) {
                            arrayList.add(LocaleController.getString("Retry", C0338R.string.Retry));
                            arrayList2.add(Integer.valueOf(0));
                            arrayList.add(LocaleController.getString("Delete", C0338R.string.Delete));
                            arrayList2.add(Integer.valueOf(1));
                        } else if (a == 1) {
                            if (this.f748a != null || this.as) {
                                if (messageObject.canDeleteMessage(this.f748a)) {
                                    arrayList.add(LocaleController.getString("Delete", C0338R.string.Delete));
                                    arrayList2.add(Integer.valueOf(1));
                                }
                            } else if (messageObject.canDeleteMessage(this.f748a)) {
                                arrayList.add(LocaleController.getString("Delete", C0338R.string.Delete));
                                arrayList2.add(Integer.valueOf(1));
                            }
                        } else if (a != 20) {
                            arrayList.add(LocaleController.getString("Retry", C0338R.string.Retry));
                            arrayList2.add(Integer.valueOf(0));
                            arrayList.add(LocaleController.getString("Copy", C0338R.string.Copy));
                            arrayList2.add(Integer.valueOf(3));
                            arrayList.add(LocaleController.getString("Delete", C0338R.string.Delete));
                            arrayList2.add(Integer.valueOf(1));
                        } else {
                            if (a == 3) {
                                arrayList.add(LocaleController.getString("Copy", C0338R.string.Copy));
                                arrayList2.add(Integer.valueOf(3));
                                if ((this.f746Y.messageOwner.media instanceof TL_messageMediaWebPage) && MessageObject.isNewGifDocument(this.f746Y.messageOwner.media.webpage.document)) {
                                    arrayList.add(LocaleController.getString("SaveToGIFs", C0338R.string.SaveToGIFs));
                                    arrayList2.add(Integer.valueOf(11));
                                }
                            } else if (a != 4) {
                                if (this.f746Y.isVideo()) {
                                    arrayList.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                    arrayList2.add(Integer.valueOf(4));
                                    arrayList.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                    arrayList2.add(Integer.valueOf(6));
                                } else if (this.f746Y.messageOwner.media instanceof TL_messageMediaDocument) {
                                    if (MessageObject.isNewGifDocument(this.f746Y.messageOwner.media.document)) {
                                        arrayList.add(LocaleController.getString("SaveToGIFs", C0338R.string.SaveToGIFs));
                                        arrayList2.add(Integer.valueOf(11));
                                    }
                                    arrayList.add(this.f746Y.isMusic() ? LocaleController.getString("SaveToMusic", C0338R.string.SaveToMusic) : LocaleController.getString("SaveToDownloads", C0338R.string.SaveToDownloads));
                                    arrayList2.add(Integer.valueOf(10));
                                    arrayList.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                    arrayList2.add(Integer.valueOf(6));
                                } else {
                                    arrayList.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                    arrayList2.add(Integer.valueOf(4));
                                }
                            } else if (a == 5) {
                                arrayList.add(LocaleController.getString("ApplyLocalizationFile", C0338R.string.ApplyLocalizationFile));
                                arrayList2.add(Integer.valueOf(5));
                                arrayList.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                arrayList2.add(Integer.valueOf(6));
                            } else if (a == 6) {
                                arrayList.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                arrayList2.add(Integer.valueOf(7));
                                arrayList.add(this.f746Y.isMusic() ? LocaleController.getString("SaveToMusic", C0338R.string.SaveToMusic) : LocaleController.getString("SaveToDownloads", C0338R.string.SaveToDownloads));
                                arrayList2.add(Integer.valueOf(10));
                                arrayList.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                arrayList2.add(Integer.valueOf(6));
                            } else if (a == 7) {
                                arrayList.add(LocaleController.getString("AddToStickers", C0338R.string.AddToStickers));
                                arrayList2.add(Integer.valueOf(9));
                            }
                            if (!(this.f746Y.messageOwner == null || this.f746Y.messageOwner.media == null || this.f746Y.messageOwner.media.caption == null || this.f746Y.messageOwner.media.caption.length() <= 0)) {
                                arrayList.add(LocaleController.getString("CopyCaption", C0338R.string.CopyCaption));
                                arrayList2.add(Integer.valueOf(25));
                            }
                            if (a != 3 || (this.f746Y.messageOwner != null && this.f746Y.messageOwner.media != null)) {
                                arrayList.add(LocaleController.getString("ProForward", C0338R.string.ProForward));
                                arrayList2.add(Integer.valueOf(26));
                            } else if (!(this.f746Y.messageOwner.media == null || this.f746Y.messageOwner.media.document == null || !MessageObject.isNewGifDocument(this.f746Y.messageOwner.media.document))) {
                                arrayList.add(LocaleController.getString("ProForward", C0338R.string.ProForward));
                                arrayList2.add(Integer.valueOf(26));
                            }
                            arrayList.add(LocaleController.getString("MultiForward", C0338R.string.MultiForward));
                            arrayList2.add(Integer.valueOf(23));
                            if (messageObject.canDeleteMessage(this.f748a)) {
                                arrayList.add(LocaleController.getString("Delete", C0338R.string.Delete));
                                arrayList2.add(Integer.valueOf(1));
                            }
                        }
                        if (!arrayList2.isEmpty()) {
                            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new AnonymousClass20(this, arrayList2));
                            builder.setTitle(LocaleController.getString("Message", C0338R.string.Message));
                            showDialog(builder.create());
                        }
                    }
                }
            }
        }
    }

    private void m695a(SizeNotifierFrameLayout sizeNotifierFrameLayout) {
        if (!ThemeUtil.m2490b()) {
            return;
        }
        if (AdvanceTheme.bN) {
            int i = AdvanceTheme.bO;
            int i2 = AdvanceTheme.bP;
            if (i2 == 0) {
                sizeNotifierFrameLayout.setBackgroundDrawable(new ColorDrawable(i));
                return;
            }
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
            int i3 = AdvanceTheme.bQ;
            sizeNotifierFrameLayout.setBackgroundDrawable(new GradientDrawable(orientation, new int[]{i, i3}));
            return;
        }
        sizeNotifierFrameLayout.setBackgroundImage(ApplicationLoader.getCachedWallpaper());
    }

    private void m696a(CharSequence charSequence, boolean z) {
        if (z && this.ah != null) {
            if (this.ah.url != null) {
                int indexOf = TextUtils.indexOf(charSequence, this.ah.url);
                boolean z2;
                boolean z3;
                char charAt;
                boolean z4;
                if (indexOf != -1) {
                    z2 = this.ah.url.length() + indexOf == charSequence.length();
                    z3 = z2;
                    charAt = !z2 ? charSequence.charAt(this.ah.url.length() + indexOf) : '\u0000';
                    z4 = z3;
                } else if (this.ah.display_url != null) {
                    indexOf = TextUtils.indexOf(charSequence, this.ah.display_url);
                    z2 = indexOf != -1 && this.ah.display_url.length() + indexOf == charSequence.length();
                    char charAt2 = (indexOf == -1 || z2) ? '\u0000' : charSequence.charAt(this.ah.display_url.length() + indexOf);
                    z3 = z2;
                    charAt = charAt2;
                    z4 = z3;
                } else {
                    z4 = false;
                    z2 = false;
                }
                if (indexOf != -1 && (r0 || r3 == ' ' || r3 == ',' || r3 == '.' || r3 == '!' || r3 == '/')) {
                    return;
                }
            }
            this.aj = null;
            m770a(false, null, null, this.ah, false, true);
        }
        Utilities.searchQueue.postRunnable(new AnonymousClass14(this, charSequence));
    }

    private void m697a(String str, int i, String str2) {
        if (this.f762p != null) {
            this.f766t.setVisibility(0);
            this.f749b.setFieldFocused(false);
            this.f749b.setVisibility(4);
            if (str != null) {
                if (this.bf) {
                    this.f762p.setText(str);
                }
            } else if (this.bf) {
                this.f762p.setText(LocaleController.getString("Stop", C0338R.string.Stop));
            } else {
                this.f762p.setText(LocaleController.getString("StartDownloadService", C0338R.string.StartDownloadService));
            }
            this.f764r.setText(str2);
            if (i > 0) {
                this.f763q.setText("%" + i);
                this.f765s.setIndeterminate(false);
                this.f765s.setProgress(i);
                this.f765s.setVisibility(0);
            } else {
                this.f765s.setVisibility(8);
                this.f763q.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            if (DownloadManagerService.f640b) {
                this.f765s.setIndeterminate(true);
                this.f765s.setVisibility(0);
                this.f762p.setText(LocaleController.getString("StopDownloadService", C0338R.string.StopDownloadService));
            }
        }
    }

    private void m698a(ArrayList<MessageObject> arrayList) {
        Collections.sort(arrayList, new Comparator<MessageObject>() {
            final /* synthetic */ DownloadManagerActivity f678a;

            {
                this.f678a = r1;
            }

            public int m635a(MessageObject messageObject, MessageObject messageObject2) {
                return (messageObject == null || messageObject.messageOwner == null) ? -1 : (messageObject2 == null || messageObject2.messageOwner == null) ? 1 : messageObject.messageOwner.date != messageObject2.messageOwner.date ? messageObject.messageOwner.date < messageObject2.messageOwner.date ? 1 : messageObject.messageOwner.date > messageObject2.messageOwner.date ? -1 : 0 : 0;
            }

            public /* synthetic */ int compare(Object obj, Object obj2) {
                return m635a((MessageObject) obj, (MessageObject) obj2);
            }
        });
    }

    private void m699a(ArrayList<MessageObject> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            if (z) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    SendMessagesHelper.getInstance().processForwardFromMyName((MessageObject) it.next(), this.aq);
                }
                return;
            }
            SendMessagesHelper.getInstance().sendMessage(arrayList, this.aq);
        }
    }

    private void m700a(boolean z) {
        if (!this.aF[0] || this.aQ != 0 || this.aL != 0) {
            m723e();
            this.aw.add(Integer.valueOf(this.ar));
            DownloadMessagesStorage a = DownloadMessagesStorage.m783a();
            long j = this.aq;
            int i = this.classGuid;
            int i2 = this.f741T;
            int i3 = this.ar;
            this.ar = i3 + 1;
            a.m810a(j, 300, 0, 0, i, 0, i2, i3);
        } else if (z && this.f759m.findLastCompletelyVisibleItemPosition() == this.f760n.getItemCount() - 1) {
            m701a(false, true);
            this.aU = ConnectionsManager.DEFAULT_DATACENTER_ID;
            m749q();
        } else {
            this.f759m.scrollToPositionWithOffset(this.f750c.size() - 1, -100000 - this.f758l.getPaddingTop());
        }
    }

    private void m701a(boolean z, boolean z2) {
        if (this.f730I != null) {
            if (!z) {
                this.aN = 0;
                if (this.f730I.getTag() != null) {
                    this.f730I.setTag(null);
                    if (this.f740S != null) {
                        this.f740S.cancel();
                        this.f740S = null;
                    }
                    if (z2) {
                        this.f740S = ObjectAnimator.ofFloat(this.f730I, "translationY", new float[]{(float) AndroidUtilities.dp(100.0f)}).setDuration(200);
                        this.f740S.addListener(new AnimatorListenerAdapterProxy() {
                            final /* synthetic */ DownloadManagerActivity f663a;

                            {
                                this.f663a = r1;
                            }

                            public void onAnimationEnd(Animator animator) {
                                this.f663a.f730I.setVisibility(4);
                            }
                        });
                        this.f740S.start();
                        return;
                    }
                    this.f730I.clearAnimation();
                    this.f730I.setVisibility(4);
                }
            } else if (this.f730I.getTag() == null) {
                if (this.f740S != null) {
                    this.f740S.cancel();
                    this.f740S = null;
                }
                if (z2) {
                    if (this.f730I.getTranslationY() == 0.0f) {
                        this.f730I.setTranslationY((float) AndroidUtilities.dp(100.0f));
                    }
                    this.f730I.setVisibility(0);
                    this.f730I.setTag(Integer.valueOf(1));
                    this.f740S = ObjectAnimator.ofFloat(this.f730I, "translationY", new float[]{0.0f}).setDuration(200);
                    this.f740S.start();
                    return;
                }
                this.f730I.setVisibility(0);
            }
        }
    }

    private void m708b() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (!sharedPreferences.getBoolean("gifhint", false)) {
            sharedPreferences.edit().putBoolean("gifhint", true).commit();
            if (getParentActivity() != null && this.fragmentView != null && this.f738Q == null) {
                if (this.f745X) {
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = (SizeNotifierFrameLayout) this.fragmentView;
                    int indexOfChild = sizeNotifierFrameLayout.indexOfChild(this.f749b);
                    if (indexOfChild != -1) {
                        this.f749b.setOpenGifsTabFirst();
                        this.f739R = new View(getParentActivity());
                        this.f739R.setBackgroundResource(C0338R.drawable.redcircle);
                        sizeNotifierFrameLayout.addView(this.f739R, indexOfChild + 1, LayoutHelper.createFrame(10, 10.0f, 83, BitmapDescriptorFactory.HUE_ORANGE, 0.0f, 0.0f, 27.0f));
                        this.f738Q = new TextView(getParentActivity());
                        this.f738Q.setBackgroundResource(C0338R.drawable.tooltip);
                        this.f738Q.setTextColor(-1);
                        this.f738Q.setTextSize(1, 14.0f);
                        this.f738Q.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                        this.f738Q.setText(LocaleController.getString("TapHereGifs", C0338R.string.TapHereGifs));
                        this.f738Q.setGravity(16);
                        sizeNotifierFrameLayout.addView(this.f738Q, indexOfChild + 1, LayoutHelper.createFrame(-2, 32.0f, 83, 5.0f, 0.0f, 0.0f, 3.0f));
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.f738Q, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(this.f739R, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
                        animatorSet.addListener(new AnimatorListenerAdapterProxy() {
                            final /* synthetic */ DownloadManagerActivity f653a;

                            /* renamed from: com.hanista.mobogram.mobo.download.a.13.1 */
                            class DownloadManagerActivity implements Runnable {
                                final /* synthetic */ AnonymousClass13 f652a;

                                /* renamed from: com.hanista.mobogram.mobo.download.a.13.1.1 */
                                class DownloadManagerActivity extends AnimatorListenerAdapterProxy {
                                    final /* synthetic */ DownloadManagerActivity f651a;

                                    DownloadManagerActivity(DownloadManagerActivity downloadManagerActivity) {
                                        this.f651a = downloadManagerActivity;
                                    }

                                    public void onAnimationEnd(Animator animator) {
                                        if (this.f651a.f652a.f653a.f738Q != null) {
                                            this.f651a.f652a.f653a.f738Q.setVisibility(8);
                                        }
                                    }
                                }

                                DownloadManagerActivity(AnonymousClass13 anonymousClass13) {
                                    this.f652a = anonymousClass13;
                                }

                                public void run() {
                                    if (this.f652a.f653a.f738Q != null) {
                                        AnimatorSet animatorSet = new AnimatorSet();
                                        Animator[] animatorArr = new Animator[1];
                                        animatorArr[0] = ObjectAnimator.ofFloat(this.f652a.f653a.f738Q, "alpha", new float[]{0.0f});
                                        animatorSet.playTogether(animatorArr);
                                        animatorSet.addListener(new DownloadManagerActivity(this));
                                        animatorSet.setDuration(300);
                                        animatorSet.start();
                                    }
                                }
                            }

                            {
                                this.f653a = r1;
                            }

                            public void onAnimationEnd(Animator animator) {
                                AndroidUtilities.runOnUIThread(new com.hanista.mobogram.mobo.download.DownloadManagerActivity.13.DownloadManagerActivity(this), 2000);
                            }
                        });
                        animatorSet.setDuration(300);
                        animatorSet.start();
                    }
                } else if (this.f749b != null) {
                    this.f749b.setOpenGifsTabFirst();
                }
            }
        }
    }

    private void m709b(MessageObject messageObject) {
        int i = 0;
        int i2 = messageObject.getDialogId() == this.aq ? 0 : 1;
        if (this.at[i2].containsKey(Integer.valueOf(messageObject.getId()))) {
            this.at[i2].remove(Integer.valueOf(messageObject.getId()));
            if (messageObject.type == 0) {
                this.au[i2].remove(Integer.valueOf(messageObject.getId()));
            }
            if (!messageObject.canDeleteMessage(this.f748a)) {
                this.av--;
            }
        } else {
            this.at[i2].put(Integer.valueOf(messageObject.getId()), messageObject);
            if (messageObject.type == 0) {
                this.au[i2].put(Integer.valueOf(messageObject.getId()), messageObject);
            }
            if (!messageObject.canDeleteMessage(this.f748a)) {
                this.av++;
            }
        }
        if (!this.actionBar.isActionModeShowed()) {
            return;
        }
        if (this.at[0].isEmpty() && this.at[1].isEmpty()) {
            this.actionBar.hideActionMode();
            return;
        }
        this.actionBar.createActionMode().getItem(10).setVisibility(this.au[0].size() + this.au[1].size() != 0 ? 0 : 8);
        ActionBarMenuItem item = this.actionBar.createActionMode().getItem(12);
        if (this.av != 0) {
            i = 8;
        }
        item.setVisibility(i);
        m645C();
    }

    private void m712b(boolean z) {
        if (this.f724C != null) {
            this.f724C.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                final /* synthetic */ DownloadManagerActivity f667a;

                {
                    this.f667a = r1;
                }

                public boolean onPreDraw() {
                    if (this.f667a.f724C != null) {
                        this.f667a.f724C.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                        this.f667a.f726E.setTextSize(20);
                    } else {
                        this.f667a.f726E.setTextSize(18);
                    }
                    int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f)) / 2;
                    if (this.f667a.f724C.getPaddingTop() != currentActionBarHeight) {
                        this.f667a.f724C.setPadding(this.f667a.f724C.getPaddingLeft(), currentActionBarHeight, this.f667a.f724C.getPaddingRight(), currentActionBarHeight);
                    }
                    LayoutParams layoutParams = (LayoutParams) this.f667a.f724C.getLayoutParams();
                    if (layoutParams.topMargin != (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) {
                        layoutParams.topMargin = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                        this.f667a.f724C.setLayoutParams(layoutParams);
                    }
                    if (!AndroidUtilities.isTablet()) {
                        return true;
                    }
                    int i = AdvanceTheme.bi;
                    if (AndroidUtilities.isSmallTablet() && ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 1) {
                        this.f667a.actionBar.setBackButtonDrawable(new BackDrawable(false));
                        if (ThemeUtil.m2490b()) {
                            Drawable backDrawable = new BackDrawable(false);
                            ((BackDrawable) backDrawable).setColor(i);
                            this.f667a.actionBar.setBackButtonDrawable(backDrawable);
                        }
                        if (this.f667a.f736O != null && this.f667a.f736O.getParent() == null) {
                            ((ViewGroup) this.f667a.fragmentView).addView(this.f667a.f736O, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
                        }
                        if (this.f667a.f737P == null || this.f667a.f737P.getParent() != null) {
                            return false;
                        }
                        ((ViewGroup) this.f667a.fragmentView).addView(this.f667a.f737P, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
                        return false;
                    }
                    this.f667a.actionBar.setBackButtonDrawable(new BackDrawable(true));
                    if (ThemeUtil.m2490b()) {
                        Drawable backDrawable2 = new BackDrawable(false);
                        ((BackDrawable) backDrawable2).setColor(i);
                        this.f667a.actionBar.setBackButtonDrawable(backDrawable2);
                    }
                    if (!(this.f667a.f736O == null || this.f667a.f736O.getParent() == null)) {
                        this.f667a.fragmentView.setPadding(0, 0, 0, 0);
                        ((ViewGroup) this.f667a.fragmentView).removeView(this.f667a.f736O);
                    }
                    if (this.f667a.f737P == null || this.f667a.f737P.getParent() == null) {
                        return false;
                    }
                    this.f667a.fragmentView.setPadding(0, 0, 0, 0);
                    ((ViewGroup) this.f667a.fragmentView).removeView(this.f667a.f737P);
                    return false;
                }
            });
        }
    }

    private void m715c() {
        if (this.f759m != null && !this.ac) {
            int findFirstVisibleItemPosition = this.f759m.findFirstVisibleItemPosition();
            int abs = findFirstVisibleItemPosition == -1 ? 0 : Math.abs(this.f759m.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
            if (abs > 0) {
                DownloadMessagesStorage a;
                long j;
                int i;
                int i2;
                int i3;
                int i4;
                int i5;
                int itemCount = this.f760n.getItemCount();
                if (findFirstVisibleItemPosition <= 25 && !this.aG) {
                    if (!this.aD[0]) {
                        this.aG = true;
                        this.aw.add(Integer.valueOf(this.ar));
                        if (this.ay.size() != 0) {
                            a = DownloadMessagesStorage.m783a();
                            j = this.aq;
                            i = this.az[0];
                            i2 = this.aC[0];
                            i3 = this.classGuid;
                            i4 = this.f741T;
                            i5 = this.ar;
                            this.ar = i5 + 1;
                            a.m810a(j, 300, i, i2, i3, 0, i4, i5);
                        } else {
                            a = DownloadMessagesStorage.m783a();
                            j = this.aq;
                            i2 = this.aC[0];
                            i3 = this.classGuid;
                            i4 = this.f741T;
                            i5 = this.ar;
                            this.ar = i5 + 1;
                            a.m810a(j, 300, 0, i2, i3, 0, i4, i5);
                        }
                    } else if (!(this.aK == 0 || this.aD[1])) {
                        this.aG = true;
                        this.aw.add(Integer.valueOf(this.ar));
                        a = DownloadMessagesStorage.m783a();
                        j = this.aK;
                        i2 = this.aC[0];
                        i3 = this.classGuid;
                        i4 = this.f741T;
                        i5 = this.ar;
                        this.ar = i5 + 1;
                        a.m810a(j, 300, 0, i2, i3, 0, i4, i5);
                    }
                }
                if (!this.aR && abs + findFirstVisibleItemPosition >= itemCount - 10) {
                    if (this.aK != 0 && !this.aF[1]) {
                        this.aw.add(Integer.valueOf(this.ar));
                        a = DownloadMessagesStorage.m783a();
                        j = this.aK;
                        i = this.aA[1];
                        i2 = this.aB[1];
                        i3 = this.classGuid;
                        i5 = this.ar;
                        this.ar = i5 + 1;
                        a.m810a(j, 300, i, i2, i3, 1, 0, i5);
                        this.aR = true;
                    } else if (!this.aF[0]) {
                        this.aw.add(Integer.valueOf(this.ar));
                        a = DownloadMessagesStorage.m783a();
                        j = this.aq;
                        i = this.aA[0];
                        i2 = this.aB[0];
                        i3 = this.classGuid;
                        i4 = this.f741T;
                        i5 = this.ar;
                        this.ar = i5 + 1;
                        a.m810a(j, 300, i, i2, i3, 1, i4, i5);
                        this.aR = true;
                    }
                }
            }
        }
    }

    private void m716c(MessageObject messageObject) {
        int i;
        MessageObject messageObject2 = null;
        int childCount = this.f758l.getChildCount();
        if (messageObject == null) {
            i = 0;
            while (i <= childCount) {
                View childAt = this.f758l.getChildAt(i);
                MessageObject messageObject3 = childAt instanceof ChatMessageCell ? ((ChatMessageCell) childAt).getMessageObject() : childAt instanceof ChatActionCell ? ((ChatActionCell) childAt).getMessageObject() : messageObject2;
                i = (messageObject3 == null || messageObject3.getId() > 0) ? i + 1 : i + 1;
            }
        }
        if (messageObject2 == null) {
            int indexOf = this.f750c.indexOf(messageObject);
            if (indexOf >= 0) {
                for (i = indexOf + 1; i < this.f750c.size(); i++) {
                    messageObject3 = (MessageObject) this.f750c.get(i);
                }
            }
        }
        if (messageObject2 != null) {
            this.aV = -10000;
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt2 = this.f758l.getChildAt(i2);
                messageObject3 = childAt2 instanceof ChatMessageCell ? ((ChatMessageCell) childAt2).getMessageObject() : childAt2 instanceof ChatActionCell ? ((ChatActionCell) childAt2).getMessageObject() : messageObject2;
                if (messageObject3 == null) {
                    this.aV = childAt2.getTop() + AndroidUtilities.dp(7.0f);
                    break;
                }
            }
            if (this.aV == -10000) {
                this.aV = this.f758l.getPaddingTop();
            }
        }
        this.f723B.setChecked(!this.f723B.isChecked(), true);
        this.f741T = this.f723B.isChecked() ? 1 : 2;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("important_" + this.aq, this.f741T).commit();
        this.f742U = true;
        this.aw.add(Integer.valueOf(this.ar));
        if (messageObject2 != null) {
            this.aL = messageObject2.getId();
        }
    }

    private void m718d() {
        if (this.f758l != null) {
            this.f759m.scrollToPositionWithOffset(this.f750c.size() - 1, -100000 - this.f758l.getPaddingTop());
        }
    }

    private void m719d(MessageObject messageObject) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            if (messageObject.type == 3) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", C0338R.string.NoPlayerInstalled));
            } else {
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", C0338R.string.NoHandleAppInstalled, messageObject.messageOwner.media.document.mime_type));
            }
            showDialog(builder.create());
        }
    }

    private void m723e() {
        this.f750c.clear();
        this.ay.clear();
        this.aw.clear();
        this.f755i.setVisibility(this.f760n.f716d == -1 ? 0 : 4);
        this.f758l.setEmptyView(null);
        for (int i = 0; i < 2; i++) {
            this.ax[i].clear();
            this.az[i] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            this.aA[i] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            this.aB[i] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            this.aC[i] = 0;
            this.aD[i] = false;
            this.aE[i] = false;
            this.aF[i] = true;
        }
        this.aO = true;
        this.aH = true;
        this.aG = true;
        this.f742U = false;
        this.aL = 0;
        this.aJ = 0;
        this.aM = false;
        this.f760n.notifyDataSetChanged();
    }

    private boolean m724e(MessageObject messageObject) {
        if (messageObject.type == 9) {
            FileLoader.getInstance().loadFile(messageObject.messageOwner.media.document, false, false);
        } else if (messageObject.type == 3) {
            FileLoader.getInstance().loadFile(messageObject.messageOwner.media.document, true, false);
        } else if (messageObject.type == 14) {
            FileLoader.getInstance().loadFile(messageObject.messageOwner.media.document, true, false);
        } else if (messageObject.type == 8) {
            FileLoader.getInstance().loadFile(messageObject.messageOwner.media.document, true, false);
        } else if (messageObject.isVoice()) {
            FileLoader.getInstance().loadFile(messageObject.messageOwner.media.document, true, false);
        } else if (!(messageObject.messageOwner.media instanceof TL_messageMediaPhoto)) {
            return false;
        } else {
            ChatMessageCell chatMessageCell = new ChatMessageCell(getParentActivity());
            chatMessageCell.setMessageObject(messageObject);
            chatMessageCell.photoImage.setImage(chatMessageCell.currentPhotoObject.location, chatMessageCell.currentPhotoFilter, chatMessageCell.currentPhotoObjectThumb != null ? chatMessageCell.currentPhotoObjectThumb.location : null, chatMessageCell.currentPhotoFilter, chatMessageCell.currentPhotoObject.size, null, false);
        }
        return true;
    }

    private void m728f() {
        if (this.f758l != null) {
            int childCount = this.f758l.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.f758l.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    chatMessageCell.getLocalVisibleRect(this.aX);
                    chatMessageCell.setVisiblePart(this.aX.top, this.aX.bottom - this.aX.top);
                }
            }
        }
    }

    private void m729g() {
        if (this.f756j != null) {
            this.f756j.setVisibility(4);
        }
    }

    private void m732h() {
        m742m();
    }

    private void m733i() {
        if (!this.actionBar.isActionModeShowed()) {
            return;
        }
        if (!this.at[0].isEmpty() || !this.at[1].isEmpty()) {
            this.f726E.setNumber(this.at[0].size() + this.at[1].size(), true);
        }
    }

    private void m736j() {
        if (this.f772z != null) {
            this.f772z.setText(LocaleController.getString("DownloadManager", C0338R.string.DownloadManager));
        }
    }

    private void m737k() {
        if (this.f772z != null) {
        }
    }

    private void m739l() {
        Iterator it = this.f750c.iterator();
        int i = 0;
        while (it.hasNext()) {
            i = ((MessageObject) it.next()).messageOwner.media != null ? i + 1 : i;
        }
        this.f722A.setText(LocaleController.formatString("DownloadedCount", C0338R.string.DownloadedCount, Integer.valueOf(i), Integer.valueOf(i - m755t())));
    }

    private void m742m() {
        if (this.f748a != null) {
            Chat b = DownloadMessagesStorage.m783a().m819b(this.f748a.id);
            if (b != null) {
                this.f748a = b;
            } else {
                return;
            }
        }
        if (this.f761o != null) {
            this.f761o.setImageResource(C0338R.drawable.ic_download_avatar);
        }
    }

    private void m744n() {
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", C0338R.string.UnsupportedAttachment), 0).show();
        }
    }

    private void m746o() {
        if (this.aS != null) {
            boolean[] zArr = this.aF;
            this.aF[1] = true;
            zArr[0] = true;
            this.aQ = 0;
            this.aJ = 0;
            this.aP = 0;
            if (this.f760n != null) {
                this.f760n.m640a(this.aS);
            } else {
                this.f750c.remove(this.aS);
            }
            this.aS = null;
        }
    }

    private void m748p() {
        m697a(null, 0, null);
    }

    private void m749q() {
        if (this.f758l != null) {
            int childCount = this.f758l.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.f758l.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    int i2;
                    int i3;
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    if (this.actionBar.isActionModeShowed()) {
                        MessageObject messageObject = chatMessageCell.getMessageObject();
                        if (this.at[messageObject.getDialogId() == this.aq ? 0 : 1].containsKey(Integer.valueOf(messageObject.getId()))) {
                            childAt.setBackgroundColor(Theme.MSG_SELECTED_BACKGROUND_COLOR);
                            i2 = 1;
                        } else {
                            childAt.setBackgroundColor(0);
                            i2 = 0;
                        }
                        i3 = 1;
                    } else {
                        childAt.setBackgroundColor(0);
                        i2 = 0;
                        i3 = 0;
                    }
                    chatMessageCell.setMessageObject(chatMessageCell.getMessageObject());
                    boolean z = i3 == 0;
                    boolean z2 = (i3 == 0 || i2 == 0) ? false : true;
                    chatMessageCell.setCheckPressed(z, z2);
                    z2 = (this.aU == ConnectionsManager.DEFAULT_DATACENTER_ID || chatMessageCell.getMessageObject() == null || chatMessageCell.getMessageObject().getId() != this.aU) ? false : true;
                    chatMessageCell.setHighlighted(z2);
                }
            }
        }
    }

    private void m751r() {
        showDialog(new ShareAlert(getParentActivity(), m753s(), f721e));
        this.actionBar.hideActionMode();
    }

    private ArrayList<MessageObject> m753s() {
        ArrayList<MessageObject> arrayList = new ArrayList();
        if (this.aa != null) {
            arrayList.add(this.aa);
            this.aa = null;
        } else {
            for (int i = 1; i >= 0; i--) {
                ArrayList arrayList2 = new ArrayList(this.at[i].keySet());
                Collections.sort(arrayList2);
                for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                    Integer num = (Integer) arrayList2.get(i2);
                    MessageObject messageObject = (MessageObject) this.at[i].get(num);
                    if (messageObject != null && num.intValue() > 0) {
                        arrayList.add(messageObject);
                    }
                }
                this.au[i].clear();
                this.at[i].clear();
            }
            this.av = 0;
            this.actionBar.hideActionMode();
            m749q();
        }
        return arrayList;
    }

    private int m755t() {
        Iterator it = this.f750c.iterator();
        int i = 0;
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            File pathToMessage = FileLoader.getPathToMessage(messageObject.messageOwner);
            int i2 = (pathToMessage == null || pathToMessage.exists() || messageObject.messageOwner.media == null) ? i : i + 1;
            i = i2;
        }
        return i;
    }

    private void m757u() {
        try {
            MaterialHelperUtil.m1372b(getParentActivity(), this.f757k, this.f762p);
        } catch (Exception e) {
        }
    }

    private void m760v() {
        presentFragment(new ProForwardActivity(this.f746Y));
    }

    private void m762w() {
        Iterator it = this.f750c.iterator();
        while (it.hasNext()) {
            m724e((MessageObject) it.next());
        }
        if (this.f760n != null) {
            this.f760n.notifyDataSetChanged();
        }
        m748p();
    }

    private void m764x() {
        this.bf = false;
        Iterator it = this.f750c.iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            if (messageObject.messageOwner.media != null) {
                FileLoader.getInstance().cancelLoadFile(messageObject.messageOwner.media.document);
            }
        }
        if (this.f760n != null) {
            this.f760n.notifyDataSetChanged();
        }
        m748p();
    }

    private void m766y() {
        m644B();
        this.bf = false;
        Iterator it = this.f750c.iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            File pathToMessage = FileLoader.getPathToMessage(messageObject.messageOwner);
            if (pathToMessage != null && !pathToMessage.exists() && m724e(messageObject)) {
                String attachFileName;
                String attachFileName2 = FileLoader.getAttachFileName(messageObject.messageOwner.media.document);
                if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                    ChatMessageCell chatMessageCell = new ChatMessageCell(getParentActivity());
                    chatMessageCell.setMessageObject(messageObject);
                    attachFileName = FileLoader.getAttachFileName(chatMessageCell.currentPhotoObject);
                } else {
                    attachFileName = attachFileName2;
                }
                MediaController.m71a().m151a(attachFileName, (FileDownloadProgressListener) this);
                this.bf = true;
                if (this.f760n != null) {
                    this.f760n.notifyDataSetChanged();
                }
                m748p();
            }
        }
        if (this.f760n != null) {
            this.f760n.notifyDataSetChanged();
        }
        m748p();
    }

    private void m768z() {
        this.bh = false;
        Iterator it = new ArrayList(this.f750c).iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            if (messageObject.messageOwner.media != null) {
                m684a(messageObject.getId(), messageObject.messageOwner.to_id.channel_id);
            }
        }
        DownloadMessagesStorage.m783a().m823e();
    }

    public void m769a(String str) {
        if (Browser.isInternalUrl(str)) {
            Browser.openUrl(getParentActivity(), str, true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.formatString("OpenUrlAlert", C0338R.string.OpenUrlAlert, str));
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("Open", C0338R.string.Open), new AnonymousClass24(this, str));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    public void m770a(boolean z, MessageObject messageObject, ArrayList<MessageObject> arrayList, WebPage webPage, boolean z2, boolean z3) {
        if (this.f749b != null) {
            if (z) {
                if (messageObject != null || arrayList != null || webPage != null) {
                    boolean z4;
                    MessageObject messageObject2;
                    ArrayList arrayList2;
                    int i;
                    if (messageObject == null || messageObject.getDialogId() == this.aq) {
                        z4 = false;
                        messageObject2 = messageObject;
                    } else {
                        arrayList2 = new ArrayList();
                        arrayList2.add(messageObject);
                        z4 = true;
                        messageObject2 = null;
                    }
                    User user;
                    CharSequence userName;
                    String charSequence;
                    if (messageObject2 != null) {
                        if (messageObject2.isFromUser()) {
                            user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                            if (user != null) {
                                userName = UserObject.getUserName(user);
                            } else {
                                return;
                            }
                        }
                        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(messageObject2.messageOwner.to_id.channel_id));
                        if (chat != null) {
                            userName = chat.title;
                        } else {
                            return;
                        }
                        this.f747Z = null;
                        this.ab = messageObject2;
                        this.f749b.setReplyingMessageObject(messageObject2);
                        if (this.ah == null) {
                            this.f734M.setImageResource(C0338R.drawable.reply);
                            this.f732K.setText(userName);
                            if (messageObject2.messageText != null) {
                                charSequence = messageObject2.messageText.toString();
                                if (charSequence.length() > 150) {
                                    charSequence = charSequence.substring(0, 150);
                                }
                                this.f733L.setText(Emoji.replaceEmoji(charSequence.replace("\n", " "), this.f733L.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                            }
                        } else {
                            return;
                        }
                    } else if (arrayList2 == null) {
                        this.f734M.setImageResource(C0338R.drawable.link);
                        if (webPage instanceof TL_webPagePending) {
                            this.f732K.setText(LocaleController.getString("GettingLinkInfo", C0338R.string.GettingLinkInfo));
                            this.f733L.setText(this.aj);
                        } else {
                            if (webPage.site_name != null) {
                                this.f732K.setText(webPage.site_name);
                            } else if (webPage.title != null) {
                                this.f732K.setText(webPage.title);
                            } else {
                                this.f732K.setText(LocaleController.getString("LinkPreview", C0338R.string.LinkPreview));
                            }
                            if (webPage.description != null) {
                                this.f733L.setText(webPage.description);
                            } else if (webPage.title != null && webPage.site_name != null) {
                                this.f733L.setText(webPage.title);
                            } else if (webPage.author != null) {
                                this.f733L.setText(webPage.author);
                            } else {
                                this.f733L.setText(webPage.display_url);
                            }
                            this.f749b.setWebPage(webPage, true);
                        }
                    } else if (!arrayList2.isEmpty()) {
                        this.ab = null;
                        this.f749b.setReplyingMessageObject(null);
                        this.f747Z = arrayList2;
                        if (this.ah == null) {
                            int i2;
                            this.f749b.setForceShowSendButton(true, z3);
                            ArrayList arrayList3 = new ArrayList();
                            this.f734M.setImageResource(C0338R.drawable.forward_blue);
                            MessageObject messageObject3 = (MessageObject) arrayList2.get(0);
                            if (messageObject3.isFromUser()) {
                                arrayList3.add(Integer.valueOf(messageObject3.messageOwner.from_id));
                            } else {
                                arrayList3.add(Integer.valueOf(-messageObject3.messageOwner.to_id.channel_id));
                            }
                            i = ((MessageObject) arrayList2.get(0)).type;
                            for (i2 = 1; i2 < arrayList2.size(); i2++) {
                                messageObject3 = (MessageObject) arrayList2.get(i2);
                                Object valueOf = messageObject3.isFromUser() ? Integer.valueOf(messageObject3.messageOwner.from_id) : Integer.valueOf(-messageObject3.messageOwner.to_id.channel_id);
                                if (!arrayList3.contains(valueOf)) {
                                    arrayList3.add(valueOf);
                                }
                                if (((MessageObject) arrayList2.get(i2)).type != i) {
                                    i = -1;
                                }
                            }
                            CharSequence stringBuilder = new StringBuilder();
                            for (i2 = 0; i2 < arrayList3.size(); i2++) {
                                Chat chat2;
                                Integer num = (Integer) arrayList3.get(i2);
                                if (num.intValue() > 0) {
                                    user = MessagesController.getInstance().getUser(num);
                                    chat2 = null;
                                } else {
                                    chat2 = MessagesController.getInstance().getChat(Integer.valueOf(-num.intValue()));
                                    user = null;
                                }
                                if (user != null || chat2 != null) {
                                    if (arrayList3.size() != 1) {
                                        if (arrayList3.size() != 2 && stringBuilder.length() != 0) {
                                            stringBuilder.append(" ");
                                            stringBuilder.append(LocaleController.formatPluralString("AndOther", arrayList3.size() - 1));
                                            break;
                                        }
                                        if (stringBuilder.length() > 0) {
                                            stringBuilder.append(", ");
                                        }
                                        if (user == null) {
                                            stringBuilder.append(chat2.title);
                                        } else if (user.first_name != null && user.first_name.length() > 0) {
                                            stringBuilder.append(user.first_name);
                                        } else if (user.last_name == null || user.last_name.length() <= 0) {
                                            stringBuilder.append(" ");
                                        } else {
                                            stringBuilder.append(user.last_name);
                                        }
                                    } else if (user != null) {
                                        stringBuilder.append(UserObject.getUserName(user));
                                    } else {
                                        stringBuilder.append(chat2.title);
                                    }
                                }
                            }
                            this.f732K.setText(stringBuilder);
                            if (i != -1 && i != 0 && i != 10 && i != 11) {
                                if (i == 1) {
                                    this.f733L.setText(LocaleController.formatPluralString("ForwardedPhoto", arrayList2.size()));
                                    if (arrayList2.size() == 1) {
                                        messageObject3 = (MessageObject) arrayList2.get(0);
                                    }
                                } else if (i == 4) {
                                    this.f733L.setText(LocaleController.formatPluralString("ForwardedLocation", arrayList2.size()));
                                    messageObject3 = messageObject2;
                                } else if (i == 3) {
                                    this.f733L.setText(LocaleController.formatPluralString("ForwardedVideo", arrayList2.size()));
                                    if (arrayList2.size() == 1) {
                                        messageObject3 = (MessageObject) arrayList2.get(0);
                                    }
                                } else if (i == 12) {
                                    this.f733L.setText(LocaleController.formatPluralString("ForwardedContact", arrayList2.size()));
                                    messageObject3 = messageObject2;
                                } else if (i == 2) {
                                    this.f733L.setText(LocaleController.formatPluralString("ForwardedAudio", arrayList2.size()));
                                    messageObject3 = messageObject2;
                                } else if (i == 14) {
                                    this.f733L.setText(LocaleController.formatPluralString("ForwardedMusic", arrayList2.size()));
                                    messageObject3 = messageObject2;
                                } else if (i == 13) {
                                    this.f733L.setText(LocaleController.formatPluralString("ForwardedSticker", arrayList2.size()));
                                    messageObject3 = messageObject2;
                                } else if (i == 8 || i == 9) {
                                    if (arrayList2.size() != 1) {
                                        this.f733L.setText(LocaleController.formatPluralString("ForwardedFile", arrayList2.size()));
                                    } else if (i == 8) {
                                        this.f733L.setText(LocaleController.getString("AttachGif", C0338R.string.AttachGif));
                                        messageObject3 = messageObject2;
                                    } else {
                                        userName = FileLoader.getDocumentFileName(((MessageObject) arrayList2.get(0)).messageOwner.media.document);
                                        if (userName.length() != 0) {
                                            this.f733L.setText(userName);
                                        }
                                        messageObject3 = (MessageObject) arrayList2.get(0);
                                    }
                                }
                                messageObject3 = messageObject2;
                            } else if (arrayList2.size() != 1 || ((MessageObject) arrayList2.get(0)).messageText == null) {
                                this.f733L.setText(LocaleController.formatPluralString("ForwardedMessage", arrayList2.size()));
                                messageObject3 = messageObject2;
                            } else {
                                charSequence = ((MessageObject) arrayList2.get(0)).messageText.toString();
                                if (charSequence.length() > 150) {
                                    charSequence = charSequence.substring(0, 150);
                                }
                                this.f733L.setText(Emoji.replaceEmoji(charSequence.replace("\n", " "), this.f733L.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                                messageObject3 = messageObject2;
                            }
                            messageObject2 = messageObject3;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                    LayoutParams layoutParams = (LayoutParams) this.f732K.getLayoutParams();
                    LayoutParams layoutParams2 = (LayoutParams) this.f733L.getLayoutParams();
                    PhotoSize closestPhotoSizeWithSize = messageObject2 != null ? FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 80) : null;
                    if (closestPhotoSizeWithSize == null || messageObject2.type == 13 || (messageObject2 != null && messageObject2.isSecretMedia())) {
                        this.f731J.setImageBitmap(null);
                        this.af = null;
                        this.f731J.setVisibility(4);
                        i = AndroidUtilities.dp(52.0f);
                        layoutParams2.leftMargin = i;
                        layoutParams.leftMargin = i;
                    } else {
                        this.af = closestPhotoSizeWithSize.location;
                        this.f731J.setImage(this.af, "50_50", (Drawable) null);
                        this.f731J.setVisibility(0);
                        i = AndroidUtilities.dp(96.0f);
                        layoutParams2.leftMargin = i;
                        layoutParams.leftMargin = i;
                    }
                    this.f732K.setLayoutParams(layoutParams);
                    this.f733L.setLayoutParams(layoutParams2);
                    this.f749b.showTopView(z3, z4);
                }
            } else if (this.ab != null || this.f747Z != null || this.ah != null) {
                if (this.ab != null && (this.ab.messageOwner.reply_markup instanceof TL_replyKeyboardForceReply)) {
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("answered_" + this.aq, this.ab.getId()).commit();
                }
                if (this.ah != null) {
                    this.ah = null;
                    this.f749b.setWebPage(null, !z2);
                    if (!(webPage == null || (this.ab == null && this.f747Z == null))) {
                        m770a(true, this.ab, this.f747Z, null, false, true);
                        return;
                    }
                }
                if (this.f747Z != null) {
                    m699a(this.f747Z, f721e);
                }
                this.f749b.setForceShowSendButton(false, z3);
                this.f749b.hideTopView(z3);
                this.f749b.setReplyingMessageObject(null);
                this.ab = null;
                this.f747Z = null;
                this.af = null;
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("reply_" + this.aq).commit();
            }
        }
    }

    public boolean m771a(String str, boolean z, boolean z2) {
        Bundle bundle = new Bundle();
        bundle.putString("videoPath", str);
        BaseFragment videoEditorActivity = new VideoEditorActivity(bundle);
        videoEditorActivity.setDelegate(new VideoEditorActivityDelegate() {
            final /* synthetic */ DownloadManagerActivity f664a;

            {
                this.f664a = r1;
            }

            public void didFinishEditVideo(String str, long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, long j3, long j4, String str2) {
                VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
                videoEditedInfo.startTime = j;
                videoEditedInfo.endTime = j2;
                videoEditedInfo.rotationValue = i3;
                videoEditedInfo.originalWidth = i4;
                videoEditedInfo.originalHeight = i5;
                videoEditedInfo.bitrate = i6;
                videoEditedInfo.resultWidth = i;
                videoEditedInfo.resultHeight = i2;
                videoEditedInfo.originalPath = str;
                SendMessagesHelper.prepareSendingVideo(str, j3, j4, i, i2, videoEditedInfo, this.f664a.aq, this.f664a.ab, str2);
                this.f664a.m770a(false, null, null, null, false, true);
            }
        });
        if (this.parentLayout == null || !videoEditorActivity.onFragmentCreate()) {
            SendMessagesHelper.prepareSendingVideo(str, 0, 0, 0, 0, null, this.aq, this.ab, null);
            m770a(false, null, null, null, false, true);
            return false;
        }
        this.parentLayout.presentFragment(videoEditorActivity, z, !z2, true);
        return true;
    }

    public boolean allowCaption() {
        return true;
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public View createView(Context context) {
        int i;
        if (this.f754h.isEmpty()) {
            for (i = 0; i < 8; i++) {
                this.f754h.add(new ChatMessageCell(context));
            }
        }
        for (i = 1; i >= 0; i--) {
            this.at[i].clear();
            this.au[i].clear();
        }
        this.av = 0;
        this.aZ = null;
        this.hasOwnBackground = true;
        Theme.loadRecources(context);
        if (ThemeUtil.m2490b()) {
            Drawable backDrawable = new BackDrawable(false);
            ((BackDrawable) backDrawable).setColor(AdvanceTheme.bi);
            this.actionBar.setBackButtonDrawable(backDrawable);
        } else {
            this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            final /* synthetic */ DownloadManagerActivity f683a;

            /* renamed from: com.hanista.mobogram.mobo.download.a.28.1 */
            class DownloadManagerActivity implements View.OnClickListener {
                final /* synthetic */ AnonymousClass28 f681a;

                DownloadManagerActivity(AnonymousClass28 anonymousClass28) {
                    this.f681a = anonymousClass28;
                }

                public void onClick(View view) {
                    this.f681a.f683a.bh = !this.f681a.f683a.bh;
                    ((CheckBoxCell) view).setChecked(this.f681a.f683a.bh, true);
                }
            }

            /* renamed from: com.hanista.mobogram.mobo.download.a.28.2 */
            class DownloadManagerActivity implements OnClickListener {
                final /* synthetic */ AnonymousClass28 f682a;

                DownloadManagerActivity(AnonymousClass28 anonymousClass28) {
                    this.f682a = anonymousClass28;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    for (int i2 = 1; i2 >= 0; i2--) {
                        ArrayList arrayList = new ArrayList(this.f682a.f683a.at[i2].keySet());
                        for (int i3 = 0; i3 < arrayList.size(); i3++) {
                            int i4;
                            Integer num = (Integer) arrayList.get(i3);
                            if (!arrayList.isEmpty()) {
                                MessageObject messageObject = (MessageObject) this.f682a.f683a.at[i2].get(arrayList.get(i3));
                                if (messageObject.messageOwner.to_id.channel_id != 0) {
                                    i4 = messageObject.messageOwner.to_id.channel_id;
                                    this.f682a.f683a.m684a(num.intValue(), i4);
                                }
                            }
                            i4 = 0;
                            this.f682a.f683a.m684a(num.intValue(), i4);
                        }
                    }
                    this.f682a.f683a.actionBar.hideActionMode();
                }
            }

            {
                this.f683a = r1;
            }

            public void onItemClick(int i) {
                int i2 = 1;
                if (i == -1) {
                    if (this.f683a.actionBar.isActionModeShowed()) {
                        while (i2 >= 0) {
                            this.f683a.at[i2].clear();
                            this.f683a.au[i2].clear();
                            i2--;
                        }
                        this.f683a.av = 0;
                        this.f683a.actionBar.hideActionMode();
                        this.f683a.m749q();
                        return;
                    }
                    this.f683a.finishFragment();
                } else if (i == 10) {
                    String str = TtmlNode.ANONYMOUS_REGION_ID;
                    for (int i3 = 1; i3 >= 0; i3--) {
                        ArrayList arrayList = new ArrayList(this.f683a.au[i3].keySet());
                        Collections.sort(arrayList);
                        int i4 = 0;
                        while (i4 < arrayList.size()) {
                            r0 = (MessageObject) this.f683a.au[i3].get((Integer) arrayList.get(i4));
                            if (str.length() != 0) {
                                str = str + "\n";
                            }
                            i4++;
                            str = r0.messageOwner.message != null ? str + r0.messageOwner.message : str + r0.messageText;
                        }
                    }
                    if (str.length() != 0) {
                        try {
                            if (VERSION.SDK_INT < 11) {
                                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setText(str);
                            } else {
                                ((android.content.ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", str));
                            }
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                    while (i2 >= 0) {
                        this.f683a.at[i2].clear();
                        this.f683a.au[i2].clear();
                        i2--;
                    }
                    this.f683a.av = 0;
                    this.f683a.actionBar.hideActionMode();
                    this.f683a.m749q();
                } else if (i == 12) {
                    if (this.f683a.getParentActivity() != null) {
                        int i5 = 1;
                        boolean z = false;
                        while (i5 >= 0) {
                            boolean z2;
                            ArrayList arrayList2 = new ArrayList(this.f683a.at[i5].keySet());
                            for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                                if (!arrayList2.isEmpty()) {
                                    r0 = (MessageObject) this.f683a.at[i5].get(arrayList2.get(i6));
                                    r0.checkMediaExistance();
                                    if (r0.mediaExists) {
                                        z2 = true;
                                        break;
                                    }
                                }
                            }
                            z2 = z;
                            i5--;
                            z = z2;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(this.f683a.getParentActivity());
                        this.f683a.bh = MoboConstants.aE;
                        if (z) {
                            View frameLayout = new FrameLayout(this.f683a.getParentActivity());
                            if (VERSION.SDK_INT >= 21) {
                                frameLayout.setPadding(0, AndroidUtilities.dp(8.0f), 0, 0);
                            }
                            View createDeleteFileCheckBox = AndroidUtilities.createDeleteFileCheckBox(this.f683a.getParentActivity());
                            frameLayout.addView(createDeleteFileCheckBox, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
                            createDeleteFileCheckBox.setOnClickListener(new com.hanista.mobogram.mobo.download.DownloadManagerActivity.28.DownloadManagerActivity(this));
                            builder.setView(frameLayout);
                        }
                        builder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", C0338R.string.AreYouSureDeleteMessages, LocaleController.formatPluralString("messages", this.f683a.at[0].size() + this.f683a.at[1].size())));
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new com.hanista.mobogram.mobo.download.DownloadManagerActivity.28.DownloadManagerActivity(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        this.f683a.showDialog(builder.create());
                    }
                } else if (i == 11) {
                    DownloadManagerActivity.f721e = false;
                    this.f683a.m751r();
                } else if (i == 111) {
                    DownloadManagerActivity.f721e = true;
                    this.f683a.m751r();
                } else if (i == 191) {
                    this.f683a.m648D();
                } else if (i == 41) {
                    MessagesSearchQuery.searchMessagesInChat(null, this.f683a.aq, this.f683a.aK, this.f683a.classGuid, 1);
                } else if (i == 42) {
                    MessagesSearchQuery.searchMessagesInChat(null, this.f683a.aq, this.f683a.aK, this.f683a.classGuid, 2);
                } else if (i == 50) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", this.f683a.f748a.id);
                    BaseFragment profileActivity = new ProfileActivity(bundle);
                    profileActivity.setChatInfo(this.f683a.f751d);
                    profileActivity.setPlayProfileAnimation(true);
                    this.f683a.presentFragment(profileActivity);
                } else if (i == 43) {
                    this.f683a.m762w();
                } else if (i == 44) {
                    this.f683a.m764x();
                } else if (i == 45) {
                    this.f683a.m642A();
                } else if (i == 46) {
                    this.f683a.m768z();
                } else if (i == 53) {
                    this.f683a.presentFragment(new DownloadManagerSettingsActivity());
                } else if (i == 47) {
                    this.f683a.m766y();
                }
            }
        });
        this.f724C = new FrameLayout(context);
        this.f724C.setBackgroundResource(ThemeUtil.m2485a().m2294h());
        this.f724C.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.actionBar.addView(this.f724C, 1, LayoutHelper.createFrame(-2, Face.UNCOMPUTED_PROBABILITY, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        this.f724C.setOnClickListener(new View.OnClickListener() {
            final /* synthetic */ DownloadManagerActivity f684a;

            {
                this.f684a = r1;
            }

            public void onClick(View view) {
                if (this.f684a.f723B != null && this.f684a.f723B.getVisibility() == 0) {
                    this.f684a.m716c(null);
                } else if (this.f684a.f748a != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", this.f684a.f748a.id);
                    BaseFragment profileActivity = new ProfileActivity(bundle);
                    profileActivity.setChatInfo(this.f684a.f751d);
                    profileActivity.setPlayProfileAnimation(true);
                    this.f684a.presentFragment(profileActivity);
                }
            }
        });
        if (!(this.f748a == null || ChatObject.isChannel(this.f748a))) {
            i = this.f748a.participants_count;
            if (this.f751d != null) {
                i = this.f751d.participants.participants.size();
            }
            if (i == 0 || this.f748a.deactivated || this.f748a.left || (this.f748a instanceof TL_chatForbidden) || (this.f751d != null && (this.f751d.participants instanceof TL_chatParticipantsForbidden))) {
                this.f724C.setEnabled(false);
            }
        }
        this.f761o = new ImageView(context);
        this.f724C.addView(this.f761o, LayoutHelper.createFrame(42, 42.0f, 51, 0.0f, 3.0f, 0.0f, 0.0f));
        this.f772z = new TextView(context);
        this.f772z.setTextColor(-1);
        this.f772z.setTextSize(1, 18.0f);
        this.f772z.setLines(1);
        this.f772z.setMaxLines(1);
        this.f772z.setSingleLine(true);
        this.f772z.setEllipsize(TruncateAt.END);
        this.f772z.setGravity(3);
        this.f772z.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.f772z.setTypeface(FontUtil.m1176a().m1160c());
        this.f724C.addView(this.f772z, LayoutHelper.createFrame(-2, -2.0f, 83, 45.0f, 0.0f, 0.0f, 22.0f));
        this.f722A = new TextView(context);
        this.f722A.setTypeface(FontUtil.m1176a().m1161d());
        this.f722A.setTextColor(-2627337);
        this.f722A.setTextSize(1, 14.0f);
        this.f722A.setLines(1);
        this.f722A.setMaxLines(1);
        this.f722A.setSingleLine(true);
        this.f722A.setEllipsize(TruncateAt.END);
        this.f722A.setGravity(3);
        if (!ChatObject.isChannel(this.f748a) || this.f748a.megagroup || (this.f748a instanceof TL_channelForbidden)) {
            this.f724C.addView(this.f722A, LayoutHelper.createFrame(-2, -2.0f, 83, 45.0f, 0.0f, 0.0f, 0.0f));
        } else {
            this.f723B = new RadioButton(context);
            this.f723B.setChecked(this.f741T == 1, false);
            this.f723B.setVisibility(8);
            this.f724C.addView(this.f723B, LayoutHelper.createFrame(24, 24.0f, 83, 50.0f, 0.0f, 0.0f, 0.0f));
            this.f724C.addView(this.f722A, LayoutHelper.createFrame(-2, -2.0f, 83, 45.0f, 0.0f, 0.0f, 0.0f));
        }
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            backDrawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_other);
            backDrawable.setColorFilter(AdvanceTheme.bi, Mode.MULTIPLY);
            this.f757k = createMenu.addItem(0, backDrawable);
        } else {
            this.f757k = createMenu.addItem(0, (int) C0338R.drawable.ic_ab_other);
        }
        this.f757k.addSubItem(47, LocaleController.getString("StartDownloading", C0338R.string.StartDownloading), 0);
        this.f757k.addSubItem(53, LocaleController.getString("ScheduleDownloads", C0338R.string.ScheduleDownloads), 0);
        this.f757k.addSubItem(43, LocaleController.getString("StartAllDownloads", C0338R.string.StartAllDownloads), 0);
        this.f757k.addSubItem(44, LocaleController.getString("StopAllDownloads", C0338R.string.StopAllDownloads), 0);
        this.f757k.addSubItem(45, LocaleController.getString("DeleteCompletedDownloads", C0338R.string.DeleteCompletedDownloads), 0);
        this.f757k.addSubItem(46, LocaleController.getString("DeleteAllDownloads", C0338R.string.DeleteAllDownloads), 0);
        m736j();
        m739l();
        m737k();
        this.f771y.clear();
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        this.f726E = new NumberTextView(createActionMode.getContext());
        this.f726E.setTextSize(18);
        this.f726E.setTypeface(FontUtil.m1176a().m1160c());
        this.f726E.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        createActionMode.addView(this.f726E, LayoutHelper.createLinear(0, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 65, 0, 0, 0));
        this.f726E.setOnTouchListener(new OnTouchListener() {
            final /* synthetic */ DownloadManagerActivity f686a;

            {
                this.f686a = r1;
            }

            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.f771y.add(createActionMode.addItem(191, C0338R.drawable.ic_ab_select_all, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.f771y.add(createActionMode.addItem(10, C0338R.drawable.ic_ab_fwd_copy, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.f771y.add(createActionMode.addItem(11, C0338R.drawable.ic_ab_fwd_quoteforward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.f771y.add(createActionMode.addItem(111, C0338R.drawable.ic_ab_fwd_forward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.f771y.add(createActionMode.addItem(12, C0338R.drawable.ic_ab_fwd_delete, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        createActionMode.getItem(10).setVisibility(this.au[0].size() + this.au[1].size() != 0 ? 0 : 8);
        createActionMode.getItem(12).setVisibility(this.av == 0 ? 0 : 8);
        m732h();
        this.fragmentView = new AnonymousClass31(this, context);
        SizeNotifierFrameLayout sizeNotifierFrameLayout = (SizeNotifierFrameLayout) this.fragmentView;
        if (ThemeUtil.m2490b()) {
            m695a(sizeNotifierFrameLayout);
        } else {
            sizeNotifierFrameLayout.setBackgroundImage(ApplicationLoader.getCachedWallpaper());
        }
        this.f770x = new FrameLayout(context);
        this.f770x.setVisibility(4);
        sizeNotifierFrameLayout.addView(this.f770x, LayoutHelper.createFrame(-1, -2, 17));
        this.f770x.setOnTouchListener(new OnTouchListener() {
            final /* synthetic */ DownloadManagerActivity f689a;

            {
                this.f689a = r1;
            }

            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        i = AdvanceTheme.m2286c(AdvanceTheme.bV, -1);
        int c = AdvanceTheme.m2286c(AdvanceTheme.bn, 1493172224);
        View textView = new TextView(context);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(LocaleController.getString("DownloadListEmpty", C0338R.string.DownloadListEmpty));
        textView.setTextSize(1, 16.0f);
        textView.setGravity(17);
        if (!ThemeUtil.m2490b()) {
            i = -1;
        }
        textView.setTextColor(i);
        textView.setBackgroundResource(C0338R.drawable.system);
        if (ThemeUtil.m2490b()) {
            textView.getBackground().setColorFilter(c, Mode.SRC_IN);
        } else {
            textView.getBackground().setColorFilter(Theme.colorFilter);
        }
        textView.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.f770x.addView(textView, new LayoutParams(-2, -2, 17));
        if (this.f749b != null) {
            this.f749b.onDestroy();
        }
        this.f758l = new AnonymousClass33(this, context);
        i = AdvanceTheme.f2491b;
        i = AdvanceTheme.f2508s;
        this.f758l.setTag(Integer.valueOf(1));
        this.f758l.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = this.f758l;
        Adapter downloadManagerActivity = new DownloadManagerActivity(this, context);
        this.f760n = downloadManagerActivity;
        recyclerListView.setAdapter(downloadManagerActivity);
        this.f758l.setClipToPadding(false);
        this.f758l.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(15.0f));
        this.f758l.setItemAnimator(null);
        this.f758l.setLayoutAnimation(null);
        this.f759m = new DownloadManagerActivity(this, context);
        this.f759m.setOrientation(1);
        this.f759m.setStackFromEnd(true);
        this.f758l.setLayoutManager(this.f759m);
        sizeNotifierFrameLayout.addView(this.f758l, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.f758l.setOnItemLongClickListener(this.f752f);
        this.f758l.setOnItemClickListener(this.f753g);
        this.f758l.setOnScrollListener(new DownloadManagerActivity(this, i));
        this.f758l.setOnTouchListener(new DownloadManagerActivity(this));
        this.f758l.setOnInterceptTouchListener(new DownloadManagerActivity(this));
        this.f755i = new FrameLayout(context);
        this.f755i.setVisibility(4);
        sizeNotifierFrameLayout.addView(this.f755i, LayoutHelper.createFrame(-1, -1, 51));
        View view = new View(context);
        view.setBackgroundResource(C0338R.drawable.system_loader);
        this.f755i.addView(view, LayoutHelper.createFrame(36, 36, 17));
        view = new ProgressBar(context);
        try {
            view.setIndeterminateDrawable(context.getResources().getDrawable(C0338R.drawable.loading_animation));
        } catch (Exception e) {
        }
        view.setIndeterminate(true);
        AndroidUtilities.setProgressBarAnimationDuration(view, ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED);
        this.f755i.addView(view, LayoutHelper.createFrame(32, 32, 17));
        this.f730I = new ImageView(context);
        this.f730I.setVisibility(4);
        this.f730I.setImageResource(C0338R.drawable.pagedown);
        sizeNotifierFrameLayout.addView(this.f730I, LayoutHelper.createFrame(-2, -2.0f, 85, 0.0f, 0.0f, 6.0f, 4.0f));
        this.f730I.setOnClickListener(new DownloadManagerActivity(this));
        this.f749b = new ChatActivityEnterView(getParentActivity(), sizeNotifierFrameLayout, null, false);
        this.f749b.setDialogId(this.aq);
        sizeNotifierFrameLayout.addView(this.f749b, sizeNotifierFrameLayout.getChildCount() - 1, LayoutHelper.createFrame(-1, -2, 83));
        this.f749b.setDelegate(new DownloadManagerActivity(this));
        View frameLayout = new FrameLayout(context);
        frameLayout.setClickable(true);
        this.f749b.addTopView(frameLayout, 48);
        view = new View(context);
        view.setBackgroundColor(-1513240);
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 1, 83));
        this.f734M = new ImageView(context);
        this.f734M.setScaleType(ScaleType.CENTER);
        frameLayout.addView(this.f734M, LayoutHelper.createFrame(52, 46, 51));
        View imageView = new ImageView(context);
        imageView.setImageResource(C0338R.drawable.delete_reply);
        imageView.setScaleType(ScaleType.CENTER);
        frameLayout.addView(imageView, LayoutHelper.createFrame(52, 46.0f, 53, 0.0f, 0.5f, 0.0f, 0.0f));
        imageView.setOnClickListener(new DownloadManagerActivity(this));
        this.f732K = new TextView(context);
        this.f732K.setTextSize(1, 14.0f);
        this.f732K.setTextColor(-13141330);
        this.f732K.setTypeface(FontUtil.m1176a().m1160c());
        this.f732K.setSingleLine(true);
        this.f732K.setEllipsize(TruncateAt.END);
        this.f732K.setMaxLines(1);
        frameLayout.addView(this.f732K, LayoutHelper.createFrame(-2, -2.0f, 51, 52.0f, 4.0f, 52.0f, 0.0f));
        this.f733L = new TextView(context);
        this.f733L.setTypeface(FontUtil.m1176a().m1161d());
        this.f733L.setTextSize(1, 14.0f);
        this.f733L.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        this.f733L.setSingleLine(true);
        this.f733L.setEllipsize(TruncateAt.END);
        this.f733L.setMaxLines(1);
        frameLayout.addView(this.f733L, LayoutHelper.createFrame(-2, -2.0f, 51, 52.0f, 22.0f, 52.0f, 0.0f));
        this.f731J = new BackupImageView(context);
        frameLayout.addView(this.f731J, LayoutHelper.createFrame(34, 34.0f, 51, 52.0f, 6.0f, 0.0f, 0.0f));
        this.f729H = new FrameLayout(context);
        this.f729H.setVisibility(8);
        sizeNotifierFrameLayout.addView(this.f729H, LayoutHelper.createFrame(-2, 81.5f, 83, 0.0f, 0.0f, 0.0f, 38.0f));
        this.f727F = new DownloadManagerActivity(this, context);
        this.f727F.setOnTouchListener(new OnTouchListener() {
            final /* synthetic */ DownloadManagerActivity f648a;

            {
                this.f648a = r1;
            }

            public boolean onTouch(View view, MotionEvent motionEvent) {
                return StickerPreviewViewer.getInstance().onTouch(motionEvent, this.f648a.f727F, 0, this.f648a.f728G);
            }
        });
        this.f727F.setDisallowInterceptTouchEvents(true);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(0);
        this.f727F.setLayoutManager(linearLayoutManager);
        this.f727F.setClipToPadding(false);
        if (VERSION.SDK_INT >= 9) {
            this.f727F.setOverScrollMode(2);
        }
        this.f729H.addView(this.f727F, LayoutHelper.createFrame(-1, 78.0f));
        frameLayout = new ImageView(context);
        frameLayout.setImageResource(C0338R.drawable.stickers_back_arrow);
        this.f729H.addView(frameLayout, LayoutHelper.createFrame(-2, -2.0f, 83, 53.0f, 0.0f, 0.0f, 0.0f));
        this.f756j = new FrameLayout(context);
        this.f756j.setBackgroundColor(-1);
        this.f756j.setVisibility(4);
        this.f756j.setFocusable(true);
        this.f756j.setFocusableInTouchMode(true);
        this.f756j.setClickable(true);
        sizeNotifierFrameLayout.addView(this.f756j, LayoutHelper.createFrame(-1, 58, 80));
        this.f725D = new TextView(context);
        this.f725D.setTypeface(FontUtil.m1176a().m1161d());
        this.f725D.setTextSize(1, 16.0f);
        this.f725D.setTextColor(Theme.CHAT_BOTTOM_OVERLAY_TEXT_COLOR);
        this.f756j.addView(this.f725D, LayoutHelper.createFrame(-2, -2, 17));
        this.f766t = new FrameLayout(context);
        this.f766t.setBackgroundColor(-262915);
        this.f766t.setVisibility(4);
        sizeNotifierFrameLayout.addView(this.f766t, LayoutHelper.createFrame(-1, 58, 80));
        this.f766t.setOnClickListener(new View.OnClickListener() {
            final /* synthetic */ DownloadManagerActivity f649a;

            {
                this.f649a = r1;
            }

            public void onClick(View view) {
                if (this.f649a.getParentActivity() != null) {
                    Intent intent;
                    if (DownloadManagerService.f640b) {
                        intent = new Intent(this.f649a.getParentActivity(), DownloadManagerService.class);
                        intent.setAction("com.hanista.mobogram.download.stop");
                        this.f649a.getParentActivity().startService(intent);
                        this.f649a.m748p();
                    } else if (this.f649a.bf) {
                        this.f649a.m764x();
                    } else if (this.f649a.f750c.size() >= 150) {
                        this.f649a.m682a();
                    } else {
                        this.f649a.m764x();
                        intent = new Intent(this.f649a.getParentActivity(), DownloadManagerService.class);
                        intent.setAction("com.hanista.mobogram.download.start");
                        this.f649a.getParentActivity().startService(intent);
                    }
                }
            }
        });
        this.f762p = new TextView(context);
        this.f762p.setTypeface(FontUtil.m1176a().m1161d());
        this.f762p.setTextSize(1, 18.0f);
        this.f762p.setTextColor(-12685407);
        if (ThemeUtil.m2490b()) {
            this.f762p.setTextColor(AdvanceTheme.bi);
        }
        this.f766t.addView(this.f762p, LayoutHelper.createFrame(-2, -2, 17));
        this.f763q = new TextView(context);
        this.f763q.setTypeface(FontUtil.m1176a().m1161d());
        this.f763q.setTextSize(1, 18.0f);
        this.f763q.setTextColor(-13866845);
        this.f766t.addView(this.f763q, LayoutHelper.createFrame(-2, -2.0f, 19, 10.0f, 0.0f, 0.0f, 0.0f));
        this.f764r = new TextView(context);
        this.f764r.setTypeface(FontUtil.m1176a().m1161d());
        this.f764r.setTextSize(1, 18.0f);
        this.f764r.setTextColor(-13866845);
        this.f766t.addView(this.f764r, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 10.0f, 0.0f));
        this.f765s = new ProgressBar(context, null, 16842872);
        this.f765s.setVisibility(8);
        this.f766t.addView(this.f765s, LayoutHelper.createFrame(-1, 15, 81));
        this.f760n.m639a();
        if (this.aG && this.f750c.isEmpty()) {
            this.f755i.setVisibility(this.f760n.f716d == -1 ? 0 : 4);
            this.f758l.setEmptyView(null);
        } else {
            this.f755i.setVisibility(4);
            this.f758l.setEmptyView(this.f770x);
        }
        if (!AndroidUtilities.isTablet() || AndroidUtilities.isSmallTablet()) {
            frameLayout = new PlayerView(context, this);
            this.f736O = frameLayout;
            sizeNotifierFrameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            frameLayout = new PowerView(context, this);
            this.f737P = frameLayout;
            sizeNotifierFrameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        m748p();
        m729g();
        if (this.f749b != null) {
            this.f749b.setAllowStickersAndGifs(true, true);
        }
        if (this.f735N != null) {
            this.f735N.setNeedBotContext(!this.f749b.isEditingMessage());
        }
        m757u();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        int indexOf;
        int i2;
        int intValue;
        int intValue2;
        ArrayList arrayList;
        int i3;
        int i4;
        MessageObject messageObject;
        int i5;
        View childAt;
        View childAt2;
        if (i == NotificationCenter.messagesDidLoaded) {
            if (((Integer) objArr[10]).intValue() == this.classGuid) {
                indexOf = this.aw.indexOf(Integer.valueOf(((Integer) objArr[11]).intValue()));
                if (indexOf != -1) {
                    this.aw.remove(indexOf);
                    if (this.f742U) {
                        indexOf = this.aL;
                        m723e();
                        this.aL = indexOf;
                    }
                    this.aI++;
                    i2 = ((Long) objArr[0]).longValue() == this.aq ? 0 : 1;
                    intValue = ((Integer) objArr[1]).intValue();
                    boolean booleanValue = ((Boolean) objArr[3]).booleanValue();
                    intValue2 = ((Integer) objArr[4]).intValue();
                    ((Integer) objArr[7]).intValue();
                    int intValue3 = ((Integer) objArr[8]).intValue();
                    if (intValue2 != 0) {
                        this.aQ = intValue2;
                        this.aJ = ((Integer) objArr[5]).intValue();
                        this.aP = ((Integer) objArr[6]).intValue();
                    } else if (this.aL != 0 && intValue3 == 3) {
                        this.aJ = ((Integer) objArr[5]).intValue();
                    }
                    arrayList = (ArrayList) objArr[2];
                    boolean[] zArr = this.aF;
                    boolean z = this.aL == 0 && this.aJ == 0;
                    zArr[i2] = z;
                    if ((intValue3 == 1 || intValue3 == 3) && i2 == 1) {
                        boolean[] zArr2 = this.aD;
                        this.aE[0] = true;
                        zArr2[0] = true;
                        this.aF[0] = false;
                        this.aA[0] = 0;
                    }
                    if (this.aI == 1 && arrayList.size() > 20) {
                        this.aI++;
                    }
                    if (this.aH) {
                        if (!this.aF[i2]) {
                            this.f750c.clear();
                            this.ay.clear();
                            for (intValue2 = 0; intValue2 < 2; intValue2++) {
                                this.ax[intValue2].clear();
                                this.az[intValue2] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                this.aA[intValue2] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                                this.aB[intValue2] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                                this.aC[intValue2] = 0;
                            }
                        }
                        this.aH = false;
                    }
                    if (intValue3 == 1) {
                        Collections.reverse(arrayList);
                    }
                    m698a(arrayList);
                    if (arrayList.size() >= 98) {
                        m682a();
                    }
                    MessagesQuery.loadReplyMessagesForMessages(arrayList, this.aq);
                    i3 = 0;
                    i4 = 0;
                    while (i3 < arrayList.size()) {
                        messageObject = (MessageObject) arrayList.get(i3);
                        if (this.ax[i2].containsKey(Integer.valueOf(messageObject.getId()))) {
                            i5 = i4;
                        } else {
                            if (i2 == 1) {
                                messageObject.setIsRead();
                            }
                            if (i2 == 0 && this.f741T != 0 && messageObject.getId() == 1) {
                                this.aD[i2] = true;
                                this.aE[i2] = true;
                            }
                            if (messageObject.getId() > 0) {
                                this.az[i2] = Math.min(messageObject.getId(), this.az[i2]);
                                this.aA[i2] = Math.max(messageObject.getId(), this.aA[i2]);
                            } else {
                                this.az[i2] = Math.max(messageObject.getId(), this.az[i2]);
                                this.aA[i2] = Math.min(messageObject.getId(), this.aA[i2]);
                            }
                            if (messageObject.messageOwner.date != 0) {
                                this.aB[i2] = Math.max(this.aB[i2], messageObject.messageOwner.date);
                                if (this.aC[i2] == 0 || messageObject.messageOwner.date < this.aC[i2]) {
                                    this.aC[i2] = messageObject.messageOwner.date;
                                }
                            }
                            if (messageObject.type < 0) {
                                i5 = i4;
                            } else if (i2 == 1 && (messageObject.messageOwner.action instanceof TL_messageActionChatMigrateTo)) {
                                i5 = i4;
                            } else {
                                ArrayList arrayList2;
                                ArrayList arrayList3;
                                if (messageObject.isOut() || messageObject.isUnread()) {
                                    this.ax[i2].put(Integer.valueOf(messageObject.getId()), messageObject);
                                    arrayList2 = (ArrayList) this.ay.get(messageObject.dateKey);
                                } else {
                                    this.ax[i2].put(Integer.valueOf(messageObject.getId()), messageObject);
                                    arrayList2 = (ArrayList) this.ay.get(messageObject.dateKey);
                                }
                                ArrayList arrayList4;
                                if (arrayList2 == null) {
                                    arrayList2 = new ArrayList();
                                    this.ay.put(messageObject.dateKey, arrayList2);
                                    Message message = new Message();
                                    message.message = LocaleController.formatDateChat((long) messageObject.messageOwner.date);
                                    message.id = 0;
                                    MessageObject messageObject2 = new MessageObject(message, null, false);
                                    messageObject2.type = 10;
                                    messageObject2.contentType = 1;
                                    if (intValue3 == 1) {
                                        this.f750c.add(0, messageObject2);
                                    } else {
                                        this.f750c.add(messageObject2);
                                    }
                                    arrayList4 = arrayList2;
                                    i5 = i4 + 1;
                                    arrayList3 = arrayList4;
                                } else {
                                    arrayList4 = arrayList2;
                                    i5 = i4;
                                    arrayList3 = arrayList4;
                                }
                                i5++;
                                if (intValue3 == 1) {
                                    arrayList3.add(messageObject);
                                    this.f750c.add(0, messageObject);
                                }
                                if (intValue3 != 1) {
                                    arrayList3.add(messageObject);
                                    this.f750c.add(this.f750c.size() - 1, messageObject);
                                }
                                if (intValue3 == 2 && messageObject.getId() == this.aQ) {
                                    Message message2 = new Message();
                                    message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                                    message2.id = 0;
                                    MessageObject messageObject3 = new MessageObject(message2, null, false);
                                    messageObject3.type = 6;
                                    messageObject3.contentType = 6;
                                    this.f750c.add(this.f750c.size() - 1, messageObject3);
                                    this.aS = messageObject3;
                                    this.aT = this.aS;
                                    this.aV = -10000;
                                    i5++;
                                } else if (intValue3 == 3 && messageObject.getId() == this.aL) {
                                    if (this.aM) {
                                        this.aU = messageObject.getId();
                                    } else {
                                        this.aU = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    }
                                    this.aT = messageObject;
                                    this.aL = 0;
                                    if (this.aV == -10000) {
                                        this.aV = -9000;
                                    }
                                }
                                if (messageObject.getId() == this.aJ) {
                                    this.aF[i2] = true;
                                }
                            }
                        }
                        i3++;
                        i4 = i5;
                    }
                    if (this.aF[i2] && i2 != 1) {
                        this.aQ = 0;
                        this.aJ = 0;
                    }
                    if (intValue3 == 1) {
                        if (!(arrayList.size() == intValue || booleanValue)) {
                            this.aF[i2] = true;
                            if (i2 != 1) {
                                this.aQ = 0;
                                this.aJ = 0;
                                this.f760n.notifyItemRemoved(this.f760n.getItemCount() - 1);
                                i4--;
                            }
                            this.aL = 0;
                        }
                        if (i4 != 0) {
                            intValue2 = this.f759m.findLastVisibleItemPosition();
                            indexOf = 0;
                            if (intValue2 != this.f759m.getItemCount() - 1) {
                                intValue2 = -1;
                            } else {
                                childAt = this.f758l.getChildAt(this.f758l.getChildCount() - 1);
                                indexOf = (childAt == null ? 0 : childAt.getTop()) - this.f758l.getPaddingTop();
                            }
                            this.f760n.notifyItemRangeInserted(this.f760n.getItemCount() - 1, i4);
                            if (intValue2 != -1) {
                                this.f759m.scrollToPositionWithOffset(intValue2, indexOf);
                            }
                        }
                        this.aR = false;
                    } else {
                        if (arrayList.size() < intValue && intValue3 != 3) {
                            if (booleanValue) {
                                this.aD[i2] = true;
                                this.aE[i2] = true;
                            } else if (intValue3 != 2) {
                                this.aD[i2] = true;
                            }
                        }
                        this.aG = false;
                        if (this.f758l != null) {
                            if (this.aO || this.ao) {
                                this.f760n.notifyDataSetChanged();
                                if (this.aT != null) {
                                    indexOf = this.aV == -9000 ? Math.max(0, (this.f758l.getHeight() - this.aT.getApproximateHeight()) / 2) : this.aV == -10000 ? 0 : this.aV;
                                    if (!this.f750c.isEmpty()) {
                                        if (this.f750c.get(this.f750c.size() - 1) == this.aT || this.f750c.get(this.f750c.size() - 2) == this.aT) {
                                            this.f759m.scrollToPositionWithOffset(0, indexOf + ((-this.f758l.getPaddingTop()) - AndroidUtilities.dp(7.0f)));
                                        } else {
                                            this.f759m.scrollToPositionWithOffset(((this.f760n.f719g + this.f750c.size()) - this.f750c.indexOf(this.aT)) - 1, indexOf + ((-this.f758l.getPaddingTop()) - AndroidUtilities.dp(7.0f)));
                                        }
                                    }
                                    this.f758l.invalidate();
                                    if (this.aV == -10000 || this.aV == -9000) {
                                        m701a(true, true);
                                    }
                                    this.aV = -10000;
                                    this.aT = null;
                                } else {
                                    m718d();
                                }
                            } else if (i4 != 0) {
                                Object obj = null;
                                if (this.aD[i2] && ((i2 == 0 && this.aK == 0) || i2 == 1)) {
                                    obj = 1;
                                    this.f760n.notifyItemRangeChanged(0, 2);
                                }
                                i5 = this.f759m.findLastVisibleItemPosition();
                                childAt2 = this.f758l.getChildAt(this.f758l.getChildCount() - 1);
                                i3 = (childAt2 == null ? 0 : childAt2.getTop()) - this.f758l.getPaddingTop();
                                if (i4 - (obj != null ? 1 : 0) > 0) {
                                    this.f760n.notifyItemRangeInserted((obj != null ? 0 : 1) + 1, i4 - (obj != null ? 1 : 0));
                                }
                                if (i5 != -1) {
                                    this.f759m.scrollToPositionWithOffset((i5 + i4) - (obj != null ? 1 : 0), i3);
                                }
                            } else if (this.aD[i2] && ((i2 == 0 && this.aK == 0) || i2 == 1)) {
                                this.f760n.notifyItemRemoved(0);
                            }
                            if (this.ac) {
                                this.ao = true;
                                if (this.aT != null) {
                                    this.ap = true;
                                }
                            }
                            if (this.aO && this.f758l != null) {
                                this.f758l.setEmptyView(this.f770x);
                            }
                        } else {
                            this.ao = true;
                            if (this.aT != null) {
                                this.ap = true;
                            }
                        }
                    }
                    if (this.aO && this.f750c.size() > 0) {
                        if (i2 == 0) {
                            this.aO = false;
                        } else {
                            this.aO = false;
                        }
                    }
                    if (i4 == 0) {
                    }
                    if (this.f755i != null) {
                        this.f755i.setVisibility(4);
                    }
                    m715c();
                } else {
                    return;
                }
            }
            m739l();
        } else if (i == NotificationCenter.closeChats) {
            if (objArr == null || objArr.length <= 0) {
                removeSelfFromStack();
            } else if (((Long) objArr[0]).longValue() == this.aq) {
                finishFragment();
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            arrayList = (ArrayList) objArr[0];
            i5 = ((Integer) objArr[1]).intValue();
            if (!ChatObject.isChannel(this.f748a)) {
                i2 = 0;
            } else if (i5 == 0 && this.aK != 0) {
                i2 = 1;
            } else if (i5 == this.f748a.id) {
                i2 = 0;
            } else {
                return;
            }
            i3 = 0;
            Object obj2 = null;
            while (i3 < arrayList.size()) {
                Integer num = (Integer) arrayList.get(i3);
                r4 = (MessageObject) this.ax[i2].get(num);
                if (r4 != null) {
                    i4 = this.f750c.indexOf(r4);
                    if (i4 != -1) {
                        this.f750c.remove(i4);
                        this.ax[i2].remove(num);
                        ArrayList arrayList5 = (ArrayList) this.ay.get(r4.dateKey);
                        if (arrayList5 != null) {
                            arrayList5.remove(r4);
                            if (arrayList5.isEmpty()) {
                                this.ay.remove(r4.dateKey);
                                if (i4 >= 0 && i4 < this.f750c.size()) {
                                    this.f750c.remove(i4);
                                }
                            }
                            r4 = 1;
                            i3++;
                            obj2 = r4;
                        }
                    }
                }
                r4 = obj2;
                i3++;
                obj2 = r4;
            }
            if (!(!this.f750c.isEmpty() || this.aD[0] || this.aG)) {
                if (this.f755i != null) {
                    this.f755i.setVisibility(4);
                }
                if (this.f758l != null) {
                    this.f758l.setEmptyView(null);
                }
                int[] iArr = this.az;
                this.az[1] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                iArr[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                iArr = this.aA;
                this.aA[1] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                iArr[0] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                iArr = this.aB;
                this.aB[1] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                iArr[0] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                iArr = this.aC;
                this.aC[1] = 0;
                iArr[0] = 0;
                this.aw.add(Integer.valueOf(this.ar));
                r3 = DownloadMessagesStorage.m783a();
                r4 = this.aq;
                intValue = this.aC[0];
                r9 = this.classGuid;
                r11 = this.f741T;
                r12 = this.ar;
                this.ar = r12 + 1;
                r3.m810a(r4, 300, 0, intValue, r9, 0, r11, r12);
                this.aG = true;
            }
            if (!(obj2 == null || this.f760n == null)) {
                m746o();
                this.f760n.notifyDataSetChanged();
            }
            m739l();
        } else if (i == NotificationCenter.chatInfoCantLoad) {
            indexOf = ((Integer) objArr[0]).intValue();
            if (this.f748a != null && this.f748a.id == indexOf && getParentActivity() != null) {
                this.aG = false;
                if (this.f755i != null) {
                    this.f755i.setVisibility(4);
                }
                if (this.f760n != null) {
                    this.f760n.notifyDataSetChanged();
                }
            }
        } else if (i == NotificationCenter.audioDidReset || i == NotificationCenter.audioPlayStateChanged) {
            if (this.f758l != null) {
                i5 = this.f758l.getChildCount();
                for (intValue2 = 0; intValue2 < i5; intValue2++) {
                    childAt = this.f758l.getChildAt(intValue2);
                    if (childAt instanceof ChatMessageCell) {
                        r2 = (ChatMessageCell) childAt;
                        r5 = r2.getMessageObject();
                        if (r5 != null && (r5.isVoice() || r5.isMusic())) {
                            r2.updateButtonState(false);
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.audioProgressDidChanged) {
            Integer num2 = (Integer) objArr[0];
            if (this.f758l != null) {
                i2 = this.f758l.getChildCount();
                for (i5 = 0; i5 < i2; i5++) {
                    childAt2 = this.f758l.getChildAt(i5);
                    if (childAt2 instanceof ChatMessageCell) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt2;
                        if (chatMessageCell.getMessageObject() != null && chatMessageCell.getMessageObject().getId() == num2.intValue()) {
                            r2 = chatMessageCell.getMessageObject();
                            r4 = MediaController.m71a().m182j();
                            if (r4 != null) {
                                r2.audioProgress = r4.audioProgress;
                                r2.audioProgressSec = r4.audioProgressSec;
                                chatMessageCell.updateAudioProgress();
                                return;
                            }
                            return;
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.removeAllMessagesFromDialog) {
            if (this.aq == ((Long) objArr[0]).longValue()) {
                this.f750c.clear();
                this.aw.clear();
                this.ay.clear();
                for (indexOf = 1; indexOf >= 0; indexOf--) {
                    this.ax[indexOf].clear();
                    this.az[indexOf] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    this.aA[indexOf] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                    this.aB[indexOf] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                    this.aC[indexOf] = 0;
                    this.at[indexOf].clear();
                    this.au[indexOf].clear();
                }
                this.av = 0;
                this.actionBar.hideActionMode();
                if (((Boolean) objArr[1]).booleanValue()) {
                    if (this.f760n != null) {
                        this.f755i.setVisibility(this.f760n.f716d == -1 ? 0 : 4);
                        this.f758l.setEmptyView(null);
                    }
                    for (indexOf = 0; indexOf < 2; indexOf++) {
                        this.aD[indexOf] = false;
                        this.aE[indexOf] = false;
                        this.aF[indexOf] = true;
                    }
                    this.aO = true;
                    this.aH = true;
                    this.aG = true;
                    this.f742U = false;
                    this.aL = 0;
                    this.aM = false;
                    this.aw.add(Integer.valueOf(this.ar));
                    r3 = DownloadMessagesStorage.m783a();
                    r4 = this.aq;
                    r9 = this.classGuid;
                    r11 = this.f741T;
                    r12 = this.ar;
                    this.ar = r12 + 1;
                    r3.m810a(r4, 300, 0, 0, r9, 2, r11, r12);
                } else if (this.f755i != null) {
                    this.f755i.setVisibility(4);
                    this.f758l.setEmptyView(this.f770x);
                }
                if (this.f760n != null) {
                    this.f760n.notifyDataSetChanged();
                }
            }
            m739l();
        } else if (i == NotificationCenter.FileNewChunkAvailable) {
            r2 = (MessageObject) objArr[0];
            r4 = ((Long) objArr[2]).longValue();
            if (r4 != 0 && this.aq == r2.getDialogId()) {
                r2 = (MessageObject) this.ax[0].get(Integer.valueOf(r2.getId()));
                if (r2 != null) {
                    r2.messageOwner.media.document.size = (int) r4;
                    m749q();
                }
            }
        } else if (i == NotificationCenter.didCreatedNewDeleteTask) {
            SparseArray sparseArray = (SparseArray) objArr[0];
            r5 = null;
            for (i5 = 0; i5 < sparseArray.size(); i5++) {
                i3 = sparseArray.keyAt(i5);
                Iterator it = ((ArrayList) sparseArray.get(i3)).iterator();
                while (it.hasNext()) {
                    messageObject = (MessageObject) this.ax[0].get((Integer) it.next());
                    if (messageObject != null) {
                        messageObject.messageOwner.destroyTime = i3;
                        r5 = 1;
                    }
                }
            }
            if (r5 != null) {
                m749q();
            }
        } else if (i == NotificationCenter.audioDidStarted) {
            if (this.f758l != null) {
                i5 = this.f758l.getChildCount();
                for (intValue2 = 0; intValue2 < i5; intValue2++) {
                    childAt = this.f758l.getChildAt(intValue2);
                    if (childAt instanceof ChatMessageCell) {
                        r2 = (ChatMessageCell) childAt;
                        r5 = r2.getMessageObject();
                        if (r5 != null && (r5.isVoice() || r5.isMusic())) {
                            r2.updateButtonState(false);
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.updateMessageMedia) {
            r2 = (MessageObject) objArr[0];
            messageObject = (MessageObject) this.ax[0].get(Integer.valueOf(r2.getId()));
            if (messageObject != null) {
                messageObject.messageOwner.media = r2.messageOwner.media;
                messageObject.messageOwner.attachPath = r2.messageOwner.attachPath;
                messageObject.generateThumbs(false);
            }
            m749q();
        } else if (i == NotificationCenter.replaceMessagesObjects) {
            long longValue = ((Long) objArr[0]).longValue();
            if (longValue == this.aq || longValue == this.aK) {
                i2 = longValue == this.aq ? 0 : 1;
                arrayList = (ArrayList) objArr[1];
                Object obj3 = null;
                Object obj4 = null;
                i3 = 0;
                while (i3 < arrayList.size()) {
                    messageObject = (MessageObject) arrayList.get(i3);
                    r4 = (MessageObject) this.ax[i2].get(Integer.valueOf(messageObject.getId()));
                    if (r4 != null) {
                        if (obj3 == null && (messageObject.messageOwner.media instanceof TL_messageMediaWebPage)) {
                            obj3 = 1;
                        }
                        if (r4.replyMessageObject != null) {
                            messageObject.replyMessageObject = r4.replyMessageObject;
                        }
                        this.ax[i2].put(Integer.valueOf(r4.getId()), messageObject);
                        i5 = this.f750c.indexOf(r4);
                        if (i5 >= 0) {
                            this.f750c.set(i5, messageObject);
                            if (this.f760n != null) {
                                this.f760n.notifyItemChanged(((this.f760n.f719g + this.f750c.size()) - i5) - 1);
                            }
                            r4 = obj3;
                            obj3 = 1;
                            i3++;
                            obj4 = obj3;
                            obj3 = r4;
                        }
                    }
                    r4 = obj3;
                    obj3 = obj4;
                    i3++;
                    obj4 = obj3;
                    obj3 = r4;
                }
                if (obj4 != null && this.f759m != null && obj3 != null && this.f759m.findLastVisibleItemPosition() >= this.f750c.size() - 1) {
                    m718d();
                }
            }
        } else if (i == NotificationCenter.didUpdatedMessagesViews) {
            SparseIntArray sparseIntArray = (SparseIntArray) ((SparseArray) objArr[0]).get((int) this.aq);
            if (sparseIntArray != null) {
                r5 = null;
                for (i5 = 0; i5 < sparseIntArray.size(); i5++) {
                    i3 = sparseIntArray.keyAt(i5);
                    messageObject = (MessageObject) this.ax[0].get(Integer.valueOf(i3));
                    if (messageObject != null) {
                        i3 = sparseIntArray.get(i3);
                        if (i3 > messageObject.messageOwner.views) {
                            messageObject.messageOwner.views = i3;
                            r5 = 1;
                        }
                    }
                }
                if (r5 != null) {
                    m749q();
                }
            }
        } else if (i == NotificationCenter.downloadServiceStarted || i == NotificationCenter.downloadServiceStoped) {
            m748p();
            if (this.f760n != null) {
                this.f760n.notifyDataSetChanged();
            }
        }
    }

    public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
        if (this.aq == 0) {
            return;
        }
        if (this.aa != null || !this.at[0].isEmpty() || !this.at[1].isEmpty()) {
            ArrayList arrayList = new ArrayList();
            if (this.aa != null) {
                arrayList.add(this.aa);
                this.aa = null;
            } else {
                for (int i = 1; i >= 0; i--) {
                    ArrayList arrayList2 = new ArrayList(this.at[i].keySet());
                    Collections.sort(arrayList2);
                    for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                        Integer num = (Integer) arrayList2.get(i2);
                        MessageObject messageObject = (MessageObject) this.at[i].get(num);
                        if (messageObject != null && num.intValue() > 0) {
                            arrayList.add(messageObject);
                        }
                    }
                    this.au[i].clear();
                    this.at[i].clear();
                }
                this.av = 0;
                this.actionBar.hideActionMode();
            }
            if (j != this.aq) {
                int i3 = (int) j;
                if (i3 != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("scrollToTopOnResume", this.ao);
                    if (i3 > 0) {
                        bundle.putInt("user_id", i3);
                    } else if (i3 < 0) {
                        bundle.putInt("chat_id", -i3);
                    }
                    BaseFragment chatActivity = new ChatActivity(bundle);
                    if (presentFragment(chatActivity, true)) {
                        chatActivity.showReplyPanel(true, null, arrayList, null, false, false);
                        if (!AndroidUtilities.isTablet()) {
                            removeSelfFromStack();
                            return;
                        }
                        return;
                    }
                    dialogsActivity.finishFragment();
                    return;
                }
                dialogsActivity.finishFragment();
                return;
            }
            dialogsActivity.finishFragment();
            m718d();
            m770a(true, null, arrayList, null, false, AndroidUtilities.isTablet());
            if (AndroidUtilities.isTablet()) {
                this.actionBar.hideActionMode();
            }
            m749q();
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return super.dismissDialogOnPause(dialog);
    }

    public int getObserverTag() {
        return 0;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        if (messageObject == null) {
            return null;
        }
        int childCount = this.f758l.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            ImageReceiver imageReceiver;
            MessageObject messageObject2;
            View childAt = this.f758l.getChildAt(i2);
            if (childAt instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                MessageObject messageObject3 = chatMessageCell.getMessageObject();
                if (messageObject3 != null && messageObject3.getId() == messageObject.getId()) {
                    ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                    if (messageObject3.isGif() || messageObject3.isNewGif()) {
                        this.bg = chatMessageCell;
                    }
                    imageReceiver = photoImage;
                    messageObject2 = messageObject3;
                }
                imageReceiver = null;
                messageObject2 = null;
            } else {
                if (childAt instanceof ChatActionCell) {
                    ChatActionCell chatActionCell = (ChatActionCell) childAt;
                    messageObject2 = chatActionCell.getMessageObject();
                    if (messageObject2 != null && messageObject2.getId() == messageObject.getId()) {
                        imageReceiver = chatActionCell.getPhotoImage();
                    }
                }
                imageReceiver = null;
                messageObject2 = null;
            }
            if (messageObject2 != null) {
                int[] iArr = new int[2];
                childAt.getLocationInWindow(iArr);
                PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                placeProviderObject.viewX = iArr[0];
                placeProviderObject.viewY = iArr[1] - AndroidUtilities.statusBarHeight;
                placeProviderObject.parentView = this.f758l;
                placeProviderObject.imageReceiver = imageReceiver;
                placeProviderObject.thumb = imageReceiver.getBitmap();
                placeProviderObject.radius = imageReceiver.getRoundRadius();
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
        Throwable th;
        Cursor cursor;
        if (i2 != -1) {
            return;
        }
        int i3;
        if (i == 0) {
            int i4;
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            ArrayList arrayList = new ArrayList();
            i3 = 0;
            try {
                switch (new ExifInterface(this.aW).getAttributeInt("Orientation", 1)) {
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        i3 = 180;
                        break;
                    case Method.TRACE /*6*/:
                        i3 = 90;
                        break;
                    case TLRPC.USER_FLAG_USERNAME /*8*/:
                        i3 = 270;
                        break;
                }
                i4 = i3;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                i4 = 0;
            }
            arrayList.add(new PhotoEntry(0, 0, 0, this.aW, i4, false));
            AndroidUtilities.addMediaToGallery(this.aW);
            this.aW = null;
        } else if (i == 1) {
            if (intent == null || intent.getData() == null) {
                m744n();
                return;
            }
            Uri data = intent.getData();
            if (data.toString().contains(MimeTypes.BASE_TYPE_VIDEO)) {
                try {
                    r1 = AndroidUtilities.getPath(data);
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                    r1 = null;
                }
                if (r1 == null) {
                    m744n();
                }
                if (VERSION.SDK_INT < 16) {
                    SendMessagesHelper.prepareSendingVideo(r1, 0, 0, 0, 0, null, this.aq, this.ab, null);
                    m770a(false, null, null, null, false, true);
                } else if (this.ac) {
                    this.ba = r1;
                } else {
                    m771a(r1, false, false);
                }
            } else {
                SendMessagesHelper.prepareSendingPhoto(null, data, this.aq, this.ab, null, null);
            }
            m770a(false, null, null, null, false, true);
        } else if (i == 2) {
            r0 = null;
            if (intent != null) {
                r0 = intent.getData();
                r0 = r0 != null ? r0.getPath() : this.aW;
                AndroidUtilities.addMediaToGallery(this.aW);
                this.aW = null;
            }
            if (r0 == null && this.aW != null) {
                if (new File(this.aW).exists()) {
                    r0 = this.aW;
                }
                this.aW = null;
            }
            r1 = r0;
            if (VERSION.SDK_INT < 16) {
                SendMessagesHelper.prepareSendingVideo(r1, 0, 0, 0, 0, null, this.aq, this.ab, null);
                m770a(false, null, null, null, false, true);
            } else if (this.ac) {
                this.ba = r1;
            } else {
                m771a(r1, false, false);
            }
        } else if (i == 21) {
            if (intent == null || intent.getData() == null) {
                m744n();
                return;
            }
            r0 = intent.getData();
            r1 = r0.toString();
            if (r1.contains("com.google.android.apps.photos.contentprovider")) {
                try {
                    r1 = r1.split("/1/")[1];
                    int indexOf = r1.indexOf("/ACTUAL");
                    if (indexOf != -1) {
                        r0 = Uri.parse(URLDecoder.decode(r1.substring(0, indexOf), C0700C.UTF8_NAME));
                    }
                } catch (Throwable e22) {
                    FileLog.m18e("tmessages", e22);
                }
            }
            r1 = AndroidUtilities.getPath(r0);
            if (r1 == null) {
                r1 = intent.toString();
                r0 = MediaController.m77a(intent.getData(), "file");
            } else {
                r0 = r1;
            }
            if (r0 == null) {
                m744n();
                return;
            }
            SendMessagesHelper.prepareSendingDocument(r0, r1, null, null, this.aq, this.ab);
            m770a(false, null, null, null, false, true);
        } else if (i != 31) {
        } else {
            if (intent == null || intent.getData() == null) {
                m744n();
                return;
            }
            try {
                Cursor query = getParentActivity().getContentResolver().query(intent.getData(), new String[]{"display_name", "data1"}, null, null, null);
                if (query != null) {
                    Object obj = null;
                    while (query.moveToNext()) {
                        try {
                            r0 = query.getString(0);
                            String string = query.getString(1);
                            User user = new User();
                            user.first_name = r0;
                            user.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                            user.phone = string;
                            SendMessagesHelper.getInstance().sendMessage(user, this.aq, this.ab, null, null);
                            i3 = 1;
                        } catch (Throwable th2) {
                            th = th2;
                            cursor = query;
                        }
                    }
                    if (obj != null) {
                        m770a(false, null, null, null, false, true);
                    }
                }
                if (query != null) {
                    try {
                        if (!query.isClosed()) {
                            query.close();
                        }
                    } catch (Throwable th3) {
                        FileLog.m18e("tmessages", th3);
                    }
                }
            } catch (Throwable th4) {
                th3 = th4;
                cursor = null;
                if (cursor != null) {
                    try {
                        if (!cursor.isClosed()) {
                            cursor.close();
                        }
                    } catch (Throwable e222) {
                        FileLog.m18e("tmessages", e222);
                    }
                }
                throw th3;
            }
        }
    }

    public boolean onBackPressed() {
        int i = 1;
        if (this.actionBar.isActionModeShowed()) {
            while (i >= 0) {
                this.at[i].clear();
                this.au[i].clear();
                i--;
            }
            this.actionBar.hideActionMode();
            this.av = 0;
            m749q();
            return false;
        } else if (!this.f749b.isPopupShowing()) {
            return true;
        } else {
            this.f749b.hidePopup(true);
            return false;
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        m712b(false);
    }

    public void onFailedDownload(String str) {
        if (this.bf) {
            m766y();
        }
    }

    public boolean onFragmentCreate() {
        Semaphore semaphore;
        boolean z = false;
        this.aL = 0;
        this.ao = false;
        if (this.f748a == null) {
            semaphore = new Semaphore(0);
            DownloadMessagesStorage.m783a().m818b().postRunnable(new AnonymousClass23(this, semaphore));
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        this.aq = 1;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.screenshotTook);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdatedMessagesViews);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoCantLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.downloadServiceStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.downloadServiceStoped);
        super.onFragmentCreate();
        this.aG = true;
        DownloadMessagesStorage a;
        long j;
        int i;
        int i2;
        int i3;
        if (this.aL != 0) {
            this.aM = true;
            this.aw.add(Integer.valueOf(this.ar));
            a = DownloadMessagesStorage.m783a();
            j = this.aq;
            int i4 = this.aL;
            i = this.classGuid;
            i2 = this.f741T;
            i3 = this.ar;
            this.ar = i3 + 1;
            a.m810a(j, 300, i4, 0, i, 3, i2, i3);
        } else {
            this.aw.add(Integer.valueOf(this.ar));
            a = DownloadMessagesStorage.m783a();
            j = this.aq;
            i = this.classGuid;
            i2 = this.f741T;
            i3 = this.ar;
            this.ar = i3 + 1;
            a.m810a(j, 300, 0, 0, i, 3, i2, i3);
        }
        if (this.f748a != null) {
            semaphore = null;
            if (this.as) {
                semaphore = new Semaphore(0);
            }
            DownloadMessagesStorage.m783a().m807a(this.f748a.id, semaphore, ChatObject.isChannel(this.f748a), false);
            if (this.as && semaphore != null) {
                try {
                    semaphore.acquire();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
        }
        URLSpanBotCommand.enabled = false;
        if (this.f751d instanceof TL_chatFull) {
            for (int i5 = 0; i5 < this.f751d.participants.participants.size(); i5++) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.f751d.participants.participants.get(i5)).user_id));
                if (user != null && user.bot) {
                    BotQuery.loadBotInfo(user.id, true, this.classGuid);
                    URLSpanBotCommand.enabled = true;
                }
            }
        }
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.openedChatChanged, Long.valueOf(this.aq), Boolean.valueOf(false));
        }
        this.f767u = new TypingDotsDrawable();
        this.f767u.setIsChat(this.f748a != null);
        this.f768v = new RecordStatusDrawable();
        this.f768v.setIsChat(this.f748a != null);
        this.f769w = new SendingFileExDrawable();
        SendingFileExDrawable sendingFileExDrawable = this.f769w;
        if (this.f748a != null) {
            z = true;
        }
        sendingFileExDrawable.setIsChat(z);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.f749b != null) {
            this.f749b.onDestroy();
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.screenshotTook);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdatedMessagesViews);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoCantLoad);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.downloadServiceStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.downloadServiceStoped);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.openedChatChanged, Long.valueOf(this.aq), Boolean.valueOf(true));
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.unlockOrientation(getParentActivity());
    }

    public void onPause() {
        super.onPause();
        this.ac = true;
        this.ad = true;
        NotificationsController.getInstance().setOpenedDialogId(0);
        if (this.f749b != null) {
            this.f749b.onPause();
            this.f749b.setFieldFocused(false);
        }
        if (this.ab != null) {
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
            try {
                AbstractSerializedData serializedData = new SerializedData();
                this.ab.messageOwner.serializeToStream(serializedData);
                String encodeToString = Base64.encodeToString(serializedData.toByteArray(), 0);
                if (encodeToString.length() != 0) {
                    edit.putString("reply_" + this.aq, encodeToString);
                }
            } catch (Throwable e) {
                edit.remove("reply_" + this.aq);
                FileLog.m18e("tmessages", e);
            }
            edit.commit();
        }
        MessagesController.getInstance().cancelTyping(0, this.aq);
    }

    public void onProgressDownload(String str, float f) {
        m697a(null, (int) (100.0f * f), m755t() + TtmlNode.ANONYMOUS_REGION_ID);
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        m732h();
        if (!(this.af == null || this.f731J == null)) {
            this.f731J.setImage(this.af, "50_50", (Drawable) null);
        }
        NotificationsController.getInstance().setOpenedDialogId(this.aq);
        if (this.ao) {
            if (!this.ap || this.aT == null) {
                m718d();
            } else if (this.f758l != null) {
                int max = this.aV == -9000 ? Math.max(0, (this.f758l.getHeight() - this.aT.getApproximateHeight()) / 2) : this.aV == -10000 ? 0 : this.aV;
                this.f759m.scrollToPositionWithOffset(this.f750c.size() - this.f750c.indexOf(this.aT), max + ((-this.f758l.getPaddingTop()) - AndroidUtilities.dp(7.0f)));
            }
            this.ap = false;
            this.ao = false;
            this.aT = null;
        }
        this.ac = false;
        if (this.ae && !this.f750c.isEmpty()) {
            Iterator it = this.f750c.iterator();
            while (it.hasNext()) {
                MessageObject messageObject = (MessageObject) it.next();
                if (!messageObject.isUnread() && !messageObject.isOut()) {
                    break;
                } else if (!messageObject.isOut()) {
                    messageObject.setIsRead();
                }
            }
            this.ae = false;
            MessagesController.getInstance().markDialogAsRead(this.aq, ((MessageObject) this.f750c.get(0)).getId(), this.an, this.am, true, false);
        }
        m715c();
        if (this.ad) {
            this.ad = false;
            if (this.f760n != null) {
                this.f760n.notifyDataSetChanged();
            }
        }
        m712b(true);
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (this.f749b.getFieldText() == null) {
            Object string = sharedPreferences.getString("dialog_" + this.aq, null);
            if (string != null) {
                sharedPreferences.edit().remove("dialog_" + this.aq).commit();
                this.f749b.setFieldText(string);
                if (getArguments().getBoolean("hasUrl", false)) {
                    this.f749b.setSelection(string.indexOf(10) + 1);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        final /* synthetic */ DownloadManagerActivity f665a;

                        {
                            this.f665a = r1;
                        }

                        public void run() {
                            if (this.f665a.f749b != null) {
                                this.f665a.f749b.setFieldFocused(true);
                                this.f665a.f749b.openKeyboard();
                            }
                        }
                    }, 700);
                }
            }
        } else {
            sharedPreferences.edit().remove("dialog_" + this.aq).commit();
        }
        if (this.ab == null) {
            String string2 = sharedPreferences.getString("reply_" + this.aq, null);
            if (!(string2 == null || string2.length() == 0)) {
                sharedPreferences.edit().remove("reply_" + this.aq).commit();
                try {
                    byte[] decode = Base64.decode(string2, 0);
                    if (decode != null) {
                        AbstractSerializedData serializedData = new SerializedData(decode);
                        Message TLdeserialize = Message.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                        if (TLdeserialize != null) {
                            this.ab = new MessageObject(TLdeserialize, MessagesController.getInstance().getUsers(), false);
                            m770a(true, this.ab, null, null, false, false);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        } else {
            sharedPreferences.edit().remove("reply_" + this.aq).commit();
        }
        if (this.f766t.getVisibility() != 0) {
            this.f749b.setFieldFocused(true);
        }
        this.f749b.onResume();
        if (this.ba != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                final /* synthetic */ DownloadManagerActivity f666a;

                {
                    this.f666a = r1;
                }

                public void run() {
                    this.f666a.m771a(this.f666a.ba, false, false);
                    this.f666a.ba = null;
                }
            });
        }
        if (this.f749b == null || !this.f749b.isEditingMessage()) {
            this.f758l.setOnItemLongClickListener(this.f752f);
            this.f758l.setOnItemClickListener(this.f753g);
            this.f758l.setLongClickable(true);
        }
        m650E();
    }

    public void onSuccessDownload(String str) {
        if (this.bf) {
            m766y();
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        int i = 0;
        if (z) {
            NotificationCenter.getInstance().setAnimationInProgress(false);
            this.al = true;
            int childCount = this.f758l.getChildCount();
            while (i < childCount) {
                i = this.f758l.getChildAt(i) instanceof ChatMessageCell ? i + 1 : i + 1;
            }
        }
    }

    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (z) {
            NotificationCenter.getInstance().setAnimationInProgress(true);
            this.al = false;
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        this.aW = bundle.getString("path");
    }

    public void saveSelfArgs(Bundle bundle) {
        if (this.aW != null) {
            bundle.putString("path", this.aW);
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
        if (this.bg != null && !MediaController.m71a().m140B() && this.bg.getPhotoImage() != null) {
            this.bg.stopGif();
            this.bg = null;
        }
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}
