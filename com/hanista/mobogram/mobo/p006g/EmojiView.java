package com.hanista.mobogram.mobo.p006g;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build.VERSION;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController.SearchImage;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemDecoration;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.State;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.g.c.AnonymousClass10;
import com.hanista.mobogram.mobo.g.c.AnonymousClass12;
import com.hanista.mobogram.mobo.g.c.AnonymousClass13;
import com.hanista.mobogram.mobo.g.c.AnonymousClass15;
import com.hanista.mobogram.mobo.g.c.AnonymousClass16;
import com.hanista.mobogram.mobo.g.c.AnonymousClass25;
import com.hanista.mobogram.mobo.g.c.AnonymousClass26;
import com.hanista.mobogram.mobo.g.c.AnonymousClass31;
import com.hanista.mobogram.mobo.g.c.AnonymousClass35;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p018q.FavoriteSticker;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.StickerSet;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getSavedGifs;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_savedGifs;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_stickerSet;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.ContextLinkCell;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.StickerEmojiCell;
import com.hanista.mobogram.ui.Components.EmojiView.Listener;
import com.hanista.mobogram.ui.Components.ExtendedGridLayoutManager;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.PagerSlidingTabStrip;
import com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.IconTabProvider;
import com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.OnLongClickOnTabListener;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.Components.ScrollSlidingTabStrip;
import com.hanista.mobogram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate;
import com.hanista.mobogram.ui.Components.Size;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.StickerPreviewViewer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

/* renamed from: com.hanista.mobogram.mobo.g.c */
public class EmojiView extends EmojiViewInf implements NotificationCenterDelegate {
    private static final Field f1042h;
    private static final OnScrollChangedListener f1043i;
    private static HashMap<String, String> f1044l;
    private LinearLayout f1045A;
    private ScrollSlidingTabStrip f1046B;
    private GridView f1047C;
    private TextView f1048D;
    private RecyclerListView f1049E;
    private ExtendedGridLayoutManager f1050F;
    private EmojiView f1051G;
    private OnItemClickListener f1052H;
    private EmojiView f1053I;
    private EmojiView f1054J;
    private int f1055K;
    private int f1056L;
    private int f1057M;
    private int[] f1058N;
    private int f1059O;
    private int f1060P;
    private int f1061Q;
    private boolean f1062R;
    private int f1063S;
    private int f1064T;
    private boolean f1065U;
    private boolean f1066V;
    private boolean f1067W;
    TL_messages_stickerSet f1068a;
    private boolean aa;
    private boolean ab;
    private long ac;
    private ArrayList<String> ad;
    private FrameLayout ae;
    private ArrayList<FavoriteSticker> af;
    private ArrayList<Document> ag;
    private boolean ah;
    private int[] ai;
    private int[] aj;
    OnLongClickListener f1069b;
    OnLongClickListener f1070c;
    OnLongClickListener f1071d;
    OnLongClickListener f1072e;
    OnLongClickOnTabListener f1073f;
    OnItemClickListener f1074g;
    private ArrayList<EmojiView> f1075j;
    private HashMap<String, Integer> f1076k;
    private ArrayList<String> f1077m;
    private ArrayList<Long> f1078n;
    private ArrayList<Document> f1079o;
    private ArrayList<TL_messages_stickerSet> f1080p;
    private ArrayList<SearchImage> f1081q;
    private boolean f1082r;
    private int[] f1083s;
    private Listener f1084t;
    private ViewPager f1085u;
    private FrameLayout f1086v;
    private FrameLayout f1087w;
    private ArrayList<GridView> f1088x;
    private ImageView f1089y;
    private EmojiView f1090z;

    /* compiled from: EmojiView */
    /* renamed from: com.hanista.mobogram.mobo.g.c.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ int f958a;
        final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f959b;

        AnonymousClass10(com.hanista.mobogram.mobo.p006g.EmojiView emojiView, int i) {
            this.f959b = emojiView;
            this.f958a = i;
        }

        public void run() {
            if (this.f959b.f1065U) {
                if (this.f959b.f1084t != null && this.f959b.f1084t.onBackspace()) {
                    this.f959b.f1089y.performHapticFeedback(3);
                }
                this.f959b.f1066V = true;
                this.f959b.m1083b(Math.max(50, this.f958a - 100));
            }
        }
    }

    /* compiled from: EmojiView */
    /* renamed from: com.hanista.mobogram.mobo.g.c.12 */
    class AnonymousClass12 extends GridView {
        final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f961a;

        AnonymousClass12(com.hanista.mobogram.mobo.p006g.EmojiView emojiView, Context context) {
            this.f961a = emojiView;
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return super.onInterceptTouchEvent(motionEvent) || StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, this.f961a.f1047C, this.f961a.getMeasuredHeight());
        }

        public void setVisibility(int i) {
            if (this.f961a.f1049E == null || this.f961a.f1049E.getVisibility() != 0) {
                super.setVisibility(i);
            } else {
                super.setVisibility(8);
            }
        }
    }

    /* compiled from: EmojiView */
    /* renamed from: com.hanista.mobogram.mobo.g.c.13 */
    class AnonymousClass13 implements Comparator<Long> {
        final /* synthetic */ HashMap f962a;
        final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f963b;

        AnonymousClass13(com.hanista.mobogram.mobo.p006g.EmojiView emojiView, HashMap hashMap) {
            this.f963b = emojiView;
            this.f962a = hashMap;
        }

        public int m1024a(Long l, Long l2) {
            Integer num = (Integer) this.f962a.get(l);
            Integer num2 = (Integer) this.f962a.get(l2);
            if (num == null) {
                num = Integer.valueOf(0);
            }
            if (num2 == null) {
                num2 = Integer.valueOf(0);
            }
            return num.intValue() > num2.intValue() ? -1 : num.intValue() < num2.intValue() ? 1 : 0;
        }

        public /* synthetic */ int compare(Object obj, Object obj2) {
            return m1024a((Long) obj, (Long) obj2);
        }
    }

    /* compiled from: EmojiView */
    /* renamed from: com.hanista.mobogram.mobo.g.c.16 */
    class AnonymousClass16 implements OnClickListener {
        final /* synthetic */ int[] f968a;
        final /* synthetic */ TL_messages_stickerSet f969b;
        final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f970c;

        AnonymousClass16(com.hanista.mobogram.mobo.p006g.EmojiView emojiView, int[] iArr, TL_messages_stickerSet tL_messages_stickerSet) {
            this.f970c = emojiView;
            this.f968a = iArr;
            this.f969b = tL_messages_stickerSet;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            this.f970c.m1072a(this.f968a[i], this.f969b);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.1 */
    static class EmojiView implements OnScrollChangedListener {
        EmojiView() {
        }

        public void onScrollChanged() {
        }
    }

    /* compiled from: EmojiView */
    /* renamed from: com.hanista.mobogram.mobo.g.c.25 */
    class AnonymousClass25 implements OnClickListener {
        final /* synthetic */ Long f979a;
        final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f980b;

        AnonymousClass25(com.hanista.mobogram.mobo.p006g.EmojiView emojiView, Long l) {
            this.f980b = emojiView;
            this.f979a = l;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            new DataBaseAccess().m897k(this.f979a);
            this.f980b.m1116o();
            this.f980b.m1113n();
            Toast.makeText(this.f980b.getContext(), LocaleController.getString("StickerRemovedFromFavorites", C0338R.string.StickerRemovedFromFavorites), 0).show();
        }
    }

    /* compiled from: EmojiView */
    /* renamed from: com.hanista.mobogram.mobo.g.c.26 */
    class AnonymousClass26 implements OnClickListener {
        final /* synthetic */ Long f981a;
        final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f982b;

        AnonymousClass26(com.hanista.mobogram.mobo.p006g.EmojiView emojiView, Long l) {
            this.f982b = emojiView;
            this.f981a = l;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            new DataBaseAccess().m894j(this.f981a);
            this.f982b.m1118p();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.2 */
    class EmojiView extends ScrollSlidingTabStrip {
        boolean f986a;
        float f987b;
        float f988c;
        boolean f989d;
        final /* synthetic */ EmojiView f990e;

        EmojiView(EmojiView emojiView, Context context) {
            this.f990e = emojiView;
            super(context);
            this.f989d = true;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.f989d) {
                this.f989d = false;
                this.f987b = motionEvent.getX();
            }
            float translationX = this.f990e.f1046B.getTranslationX();
            if (this.f990e.f1046B.getScrollX() == 0 && translationX == 0.0f) {
                if (this.f986a || this.f987b - motionEvent.getX() >= 0.0f) {
                    if (this.f986a && this.f987b - motionEvent.getX() > 0.0f && this.f990e.f1085u.isFakeDragging()) {
                        this.f990e.f1085u.endFakeDrag();
                        this.f986a = false;
                    }
                } else if (this.f990e.f1085u.beginFakeDrag()) {
                    this.f986a = true;
                    this.f988c = this.f990e.f1046B.getTranslationX();
                }
            }
            if (this.f986a) {
                try {
                    this.f990e.f1085u.fakeDragBy((float) ((int) (((motionEvent.getX() - this.f987b) + translationX) - this.f988c)));
                    this.f988c = translationX;
                } catch (Throwable e) {
                    try {
                        this.f990e.f1085u.endFakeDrag();
                    } catch (Exception e2) {
                    }
                    this.f986a = false;
                    FileLog.m18e("tmessages", e);
                }
            }
            this.f987b = motionEvent.getX();
            if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                this.f989d = true;
                if (this.f986a) {
                    this.f990e.f1085u.endFakeDrag();
                    this.f986a = false;
                }
            }
            return this.f986a || super.onTouchEvent(motionEvent);
        }
    }

    /* compiled from: EmojiView */
    /* renamed from: com.hanista.mobogram.mobo.g.c.31 */
    class AnonymousClass31 extends ExtendedGridLayoutManager {
        final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f992a;
        private Size f993b;

        AnonymousClass31(com.hanista.mobogram.mobo.p006g.EmojiView emojiView, Context context, int i) {
            this.f992a = emojiView;
            super(context, i);
            this.f993b = new Size();
        }

        protected Size getSizeForItem(int i) {
            float f = 100.0f;
            Document document = ((SearchImage) this.f992a.f1081q.get(i)).document;
            Size size = this.f993b;
            float f2 = (document.thumb == null || document.thumb.f2664w == 0) ? 100.0f : (float) document.thumb.f2664w;
            size.width = f2;
            Size size2 = this.f993b;
            if (!(document.thumb == null || document.thumb.f2663h == 0)) {
                f = (float) document.thumb.f2663h;
            }
            size2.height = f;
            for (int i2 = 0; i2 < document.attributes.size(); i2++) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i2);
                if ((documentAttribute instanceof TL_documentAttributeImageSize) || (documentAttribute instanceof TL_documentAttributeVideo)) {
                    this.f993b.width = (float) documentAttribute.f2659w;
                    this.f993b.height = (float) documentAttribute.f2658h;
                    break;
                }
            }
            return this.f993b;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.3 */
    class EmojiView implements ScrollSlidingTabStripDelegate {
        final /* synthetic */ EmojiView f1000a;

        EmojiView(EmojiView emojiView) {
            this.f1000a = emojiView;
        }

        public void onPageSelected(int i) {
            int i2 = 8;
            int i3 = 7;
            if (this.f1000a.f1049E != null) {
                if (i == this.f1000a.f1061Q + 1) {
                    if (this.f1000a.f1049E.getVisibility() != 0) {
                        this.f1000a.f1084t.onGifTab(true);
                        this.f1000a.m1100g();
                    }
                } else if (this.f1000a.f1049E.getVisibility() == 0) {
                    this.f1000a.f1084t.onGifTab(false);
                    this.f1000a.f1049E.setVisibility(8);
                    this.f1000a.f1047C.setVisibility(0);
                    TextView A = this.f1000a.f1048D;
                    if (this.f1000a.f1090z.getCount() == 0) {
                        i2 = 0;
                    }
                    A.setVisibility(i2);
                }
            }
            if (i == 0) {
                this.f1000a.f1085u.setCurrentItem(0);
            } else if (i == this.f1000a.f1061Q + 1) {
            } else {
                if (i == this.f1000a.f1060P + 1) {
                    ((GridView) this.f1000a.f1088x.get(MoboConstants.f1310C ? 7 : 6)).setSelection(0);
                    return;
                }
                i2 = (i - 1) - this.f1000a.f1059O;
                if (i2 == 0 && MoboConstants.f1309B) {
                    this.f1000a.m1119q();
                }
                if (i2 != this.f1000a.f1080p.size()) {
                    int size = i2 >= this.f1000a.f1080p.size() ? this.f1000a.f1080p.size() - 1 : i2;
                    ArrayList C = this.f1000a.f1088x;
                    if (!MoboConstants.f1310C) {
                        i3 = 6;
                    }
                    ((GridView) C.get(i3)).setSelection(this.f1000a.f1090z.m1039a((TL_messages_stickerSet) this.f1000a.f1080p.get(size)));
                } else if (this.f1000a.f1084t != null) {
                    this.f1000a.f1084t.onStickersSettingsClick();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.4 */
    class EmojiView implements OnScrollListener {
        final /* synthetic */ EmojiView f1001a;

        EmojiView(EmojiView emojiView) {
            this.f1001a = emojiView;
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            this.f1001a.m1070a(i);
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.5 */
    class EmojiView extends ViewPager {
        final /* synthetic */ EmojiView f1002a;

        EmojiView(EmojiView emojiView, Context context) {
            this.f1002a = emojiView;
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.6 */
    class EmojiView extends LinearLayout {
        final /* synthetic */ EmojiView f1003a;

        EmojiView(EmojiView emojiView, Context context) {
            this.f1003a = emojiView;
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.7 */
    class EmojiView implements OnPageChangeListener {
        final /* synthetic */ EmojiView f1004a;

        EmojiView(EmojiView emojiView) {
            this.f1004a = emojiView;
        }

        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
            this.f1004a.m1071a(i, this.f1004a.getMeasuredWidth(), i2);
        }

        public void onPageSelected(int i) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.8 */
    class EmojiView extends ImageView {
        final /* synthetic */ EmojiView f1005a;

        EmojiView(EmojiView emojiView, Context context) {
            this.f1005a = emojiView;
            super(context);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                this.f1005a.f1065U = true;
                this.f1005a.f1066V = false;
                this.f1005a.m1083b(350);
            } else if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                this.f1005a.f1065U = false;
                if (!(this.f1005a.f1066V || this.f1005a.f1084t == null || !this.f1005a.f1084t.onBackspace())) {
                    this.f1005a.f1089y.performHapticFeedback(3);
                }
            }
            super.onTouchEvent(motionEvent);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.9 */
    class EmojiView implements OnKeyListener {
        final /* synthetic */ EmojiView f1006a;

        EmojiView(EmojiView emojiView) {
            this.f1006a = emojiView;
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || this.f1006a.f1054J == null || !this.f1006a.f1054J.isShowing()) {
                return false;
            }
            this.f1006a.f1054J.dismiss();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.a */
    private class EmojiView extends View {
        final /* synthetic */ EmojiView f1007a;
        private Drawable f1008b;
        private Drawable f1009c;
        private String f1010d;
        private int f1011e;
        private int f1012f;
        private Paint f1013g;
        private RectF f1014h;

        public EmojiView(EmojiView emojiView, Context context) {
            this.f1007a = emojiView;
            super(context);
            this.f1013g = new Paint(1);
            this.f1014h = new RectF();
            this.f1008b = getResources().getDrawable(C0338R.drawable.stickers_back_all);
            this.f1009c = getResources().getDrawable(C0338R.drawable.stickers_back_arrow);
        }

        public int m1025a() {
            return this.f1012f;
        }

        public void m1026a(int i) {
            if (this.f1012f != i) {
                this.f1012f = i;
                invalidate();
            }
        }

        public void m1027a(String str, int i) {
            this.f1010d = str;
            this.f1011e = i;
            this.f1013g.setColor(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR);
            invalidate();
        }

        protected void onDraw(Canvas canvas) {
            float f = 55.5f;
            this.f1008b.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(AndroidUtilities.isTablet() ? BitmapDescriptorFactory.HUE_YELLOW : 52.0f));
            this.f1008b.draw(canvas);
            Drawable drawable = this.f1009c;
            int dp = this.f1011e - AndroidUtilities.dp(9.0f);
            int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 55.5f : 47.5f);
            int dp3 = this.f1011e + AndroidUtilities.dp(9.0f);
            if (!AndroidUtilities.isTablet()) {
                f = 47.5f;
            }
            drawable.setBounds(dp, dp2, dp3, AndroidUtilities.dp(f + 8.0f));
            this.f1009c.draw(canvas);
            if (this.f1010d != null) {
                dp2 = 0;
                while (true) {
                    if (dp2 < (MoboConstants.f1310C ? 7 : 6)) {
                        int dp4 = AndroidUtilities.dp((float) ((dp2 * 4) + 5)) + (this.f1007a.f1057M * dp2);
                        int dp5 = AndroidUtilities.dp(9.0f);
                        if (this.f1012f == dp2) {
                            this.f1014h.set((float) dp4, (float) (dp5 - ((int) AndroidUtilities.dpf2(3.5f))), (float) (this.f1007a.f1057M + dp4), (float) ((this.f1007a.f1057M + dp5) + AndroidUtilities.dp(3.0f)));
                            canvas.drawRoundRect(this.f1014h, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.f1013g);
                        }
                        String str = this.f1010d;
                        if (dp2 != 0) {
                            str = str + "\ud83c";
                            switch (dp2) {
                                case VideoPlayer.TYPE_AUDIO /*1*/:
                                    str = str + "\udffb";
                                    break;
                                case VideoPlayer.STATE_PREPARING /*2*/:
                                    str = str + "\udffc";
                                    break;
                                case VideoPlayer.STATE_BUFFERING /*3*/:
                                    str = str + "\udffd";
                                    break;
                                case VideoPlayer.STATE_READY /*4*/:
                                    str = str + "\udffe";
                                    break;
                                case VideoPlayer.STATE_ENDED /*5*/:
                                    str = str + "\udfff";
                                    break;
                                case Method.TRACE /*6*/:
                                    str = "\u2764";
                                    break;
                            }
                        }
                        Drawable b = Emoji.m1016b(str);
                        if (b != null) {
                            b.setBounds(dp4, dp5, this.f1007a.f1057M + dp4, this.f1007a.f1057M + dp5);
                            b.draw(canvas);
                        }
                        dp2++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.b */
    private class EmojiView extends BaseAdapter {
        final /* synthetic */ EmojiView f1015a;
        private int f1016b;

        public EmojiView(EmojiView emojiView, int i) {
            this.f1015a = emojiView;
            this.f1016b = i;
        }

        public int getCount() {
            return this.f1016b == -1 ? this.f1015a.f1077m.size() : (this.f1016b == 0 && MoboConstants.f1310C) ? this.f1015a.ad.size() : MoboConstants.f1310C ? EmojiData.f953d[this.f1016b - 1].length : EmojiData.f953d[this.f1016b].length;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            String str;
            Object obj;
            view = (EmojiView) view;
            if (view == null) {
                view = new EmojiView(this.f1015a, this.f1015a.getContext());
            }
            if (this.f1016b == -1) {
                str = (String) this.f1015a.f1077m.get(i);
                obj = str;
            } else if (this.f1016b == 0 && MoboConstants.f1310C) {
                str = (String) this.f1015a.ad.get(i);
                r2 = str;
            } else {
                String str2;
                if (MoboConstants.f1310C) {
                    str = EmojiData.f953d[this.f1016b - 1][i];
                    str2 = str;
                    obj = str;
                } else {
                    str = EmojiData.f953d[this.f1016b][i];
                    str2 = str;
                    r2 = str;
                }
                str = (String) EmojiView.f1044l.get(obj);
                str = str != null ? str2 + str : str2;
            }
            view.setImageDrawable(Emoji.m1016b(str));
            view.setTag(obj);
            return view;
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (dataSetObserver != null) {
                super.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.c */
    private class EmojiView extends PagerAdapter implements IconTabProvider {
        final /* synthetic */ EmojiView f1017a;

        private EmojiView(EmojiView emojiView) {
            this.f1017a = emojiView;
        }

        public void customOnDraw(Canvas canvas, int i) {
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            View N = i == 0 ? this.f1017a.f1086v : (i == 1 && MoboConstants.f1310C) ? this.f1017a.ae : ((i != 6 || MoboConstants.f1310C) && !(i == 7 && MoboConstants.f1310C)) ? (View) this.f1017a.f1088x.get(i) : this.f1017a.f1087w;
            viewGroup.removeView(N);
        }

        public int getCount() {
            return this.f1017a.f1088x.size();
        }

        public int getPageIconResId(int i) {
            if (i == 1 && MoboConstants.f1310C) {
                return C0338R.drawable.ic_tab_favorite_stickers;
            }
            if (!MoboConstants.f1310C) {
                return this.f1017a.f1083s[i];
            }
            return this.f1017a.f1083s[i == 0 ? 0 : i - 1];
        }

        public int getPageIconSelectedResId(int i) {
            if (i == 1 && MoboConstants.f1310C) {
                return C0338R.drawable.ic_tab_favorite_blue;
            }
            if (!MoboConstants.f1310C) {
                return this.f1017a.aj[i];
            }
            return this.f1017a.aj[i == 0 ? 0 : i - 1];
        }

        public int getPageIconUnSelectedResId(int i) {
            if (i == 1 && MoboConstants.f1310C) {
                return C0338R.drawable.ic_tab_favorite_stickers;
            }
            if (!MoboConstants.f1310C) {
                return this.f1017a.ai[i];
            }
            return this.f1017a.ai[i == 0 ? 0 : i - 1];
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View N = i == 0 ? this.f1017a.f1086v : (i == 1 && MoboConstants.f1310C) ? this.f1017a.ae : ((i != 6 || MoboConstants.f1310C) && !(i == 7 && MoboConstants.f1310C)) ? (View) this.f1017a.f1088x.get(i) : this.f1017a.f1087w;
            viewGroup.addView(N);
            return N;
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

    /* renamed from: com.hanista.mobogram.mobo.g.c.d */
    private class EmojiView extends PopupWindow {
        final /* synthetic */ EmojiView f1018a;
        private OnScrollChangedListener f1019b;
        private ViewTreeObserver f1020c;

        public EmojiView(EmojiView emojiView, View view, int i, int i2) {
            this.f1018a = emojiView;
            super(view, i, i2);
            m1028a();
        }

        private void m1028a() {
            if (EmojiView.f1042h != null) {
                try {
                    this.f1019b = (OnScrollChangedListener) EmojiView.f1042h.get(this);
                    EmojiView.f1042h.set(this, EmojiView.f1043i);
                } catch (Exception e) {
                    this.f1019b = null;
                }
            }
        }

        private void m1029a(View view) {
            if (this.f1019b != null) {
                ViewTreeObserver viewTreeObserver = view.getWindowToken() != null ? view.getViewTreeObserver() : null;
                if (viewTreeObserver != this.f1020c) {
                    if (this.f1020c != null && this.f1020c.isAlive()) {
                        this.f1020c.removeOnScrollChangedListener(this.f1019b);
                    }
                    this.f1020c = viewTreeObserver;
                    if (viewTreeObserver != null) {
                        viewTreeObserver.addOnScrollChangedListener(this.f1019b);
                    }
                }
            }
        }

        private void m1030b() {
            if (this.f1019b != null && this.f1020c != null) {
                if (this.f1020c.isAlive()) {
                    this.f1020c.removeOnScrollChangedListener(this.f1019b);
                }
                this.f1020c = null;
            }
        }

        public void dismiss() {
            setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception e) {
            }
            m1030b();
        }

        public void showAsDropDown(View view, int i, int i2) {
            try {
                super.showAsDropDown(view, i, i2);
                m1029a(view);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void showAtLocation(View view, int i, int i2, int i3) {
            super.showAtLocation(view, i, i2, i3);
            m1030b();
        }

        public void update(View view, int i, int i2) {
            super.update(view, i, i2);
            m1029a(view);
        }

        public void update(View view, int i, int i2, int i3, int i4) {
            super.update(view, i, i2, i3, i4);
            m1029a(view);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.e */
    private class EmojiView extends Adapter {
        final /* synthetic */ EmojiView f1022a;
        private Context f1023b;

        /* renamed from: com.hanista.mobogram.mobo.g.c.e.a */
        private class EmojiView extends ViewHolder {
            final /* synthetic */ EmojiView f1021a;

            public EmojiView(EmojiView emojiView, View view) {
                this.f1021a = emojiView;
                super(view);
            }
        }

        public EmojiView(EmojiView emojiView, Context context) {
            this.f1022a = emojiView;
            this.f1023b = context;
        }

        public int getItemCount() {
            return this.f1022a.f1081q.size();
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            SearchImage searchImage = (SearchImage) this.f1022a.f1081q.get(i);
            if (searchImage.document != null) {
                ((ContextLinkCell) viewHolder.itemView).setGif(searchImage.document, false);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new EmojiView(this, new ContextLinkCell(this.f1023b));
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.f */
    private class EmojiView extends ImageView {
        final /* synthetic */ EmojiView f1028a;
        private boolean f1029b;
        private float f1030c;
        private float f1031d;
        private float f1032e;
        private float f1033f;

        /* renamed from: com.hanista.mobogram.mobo.g.c.f.1 */
        class EmojiView implements View.OnClickListener {
            final /* synthetic */ EmojiView f1024a;
            final /* synthetic */ EmojiView f1025b;

            EmojiView(EmojiView emojiView, EmojiView emojiView2) {
                this.f1025b = emojiView;
                this.f1024a = emojiView2;
            }

            public void onClick(View view) {
                this.f1025b.m1034a(null);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.g.c.f.2 */
        class EmojiView implements OnLongClickListener {
            final /* synthetic */ EmojiView f1026a;
            final /* synthetic */ EmojiView f1027b;

            EmojiView(EmojiView emojiView, EmojiView emojiView2) {
                this.f1027b = emojiView;
                this.f1026a = emojiView2;
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onLongClick(android.view.View r10) {
                /*
                r9 = this;
                r6 = 3;
                r5 = 2;
                r8 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
                r2 = 1;
                r3 = 0;
                r0 = r10.getTag();
                r0 = (java.lang.String) r0;
                r1 = com.hanista.mobogram.mobo.p006g.EmojiData.f957h;
                r1 = r1.containsKey(r0);
                if (r1 == 0) goto L_0x01e5;
            L_0x0014:
                r1 = r9.f1027b;
                r1.f1029b = r2;
                r1 = r9.f1027b;
                r4 = r9.f1027b;
                r4 = r4.f1030c;
                r1.f1032e = r4;
                r1 = r9.f1027b;
                r4 = r9.f1027b;
                r4 = r4.f1031d;
                r1.f1033f = r4;
                r1 = com.hanista.mobogram.mobo.p006g.EmojiView.f1044l;
                r1 = r1.get(r0);
                r1 = (java.lang.String) r1;
                if (r1 == 0) goto L_0x018e;
            L_0x003b:
                r4 = -1;
                r7 = r1.hashCode();
                switch(r7) {
                    case 1773375: goto L_0x010f;
                    case 1773376: goto L_0x011b;
                    case 1773377: goto L_0x0127;
                    case 1773378: goto L_0x0133;
                    case 1773379: goto L_0x013f;
                    default: goto L_0x0043;
                };
            L_0x0043:
                r1 = r4;
            L_0x0044:
                switch(r1) {
                    case 0: goto L_0x014b;
                    case 1: goto L_0x0158;
                    case 2: goto L_0x0165;
                    case 3: goto L_0x0172;
                    case 4: goto L_0x0180;
                    default: goto L_0x0047;
                };
            L_0x0047:
                r1 = r9.f1027b;
                r1 = r1.f1028a;
                r1 = r1.f1058N;
                r10.getLocationOnScreen(r1);
                r1 = r9.f1027b;
                r1 = r1.f1028a;
                r1 = r1.f1057M;
                r4 = r9.f1027b;
                r4 = r4.f1028a;
                r4 = r4.f1053I;
                r4 = r4.m1025a();
                r4 = r4 * r1;
                r1 = r9.f1027b;
                r1 = r1.f1028a;
                r1 = r1.f1053I;
                r1 = r1.m1025a();
                r5 = r1 * 4;
                r1 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
                if (r1 == 0) goto L_0x019b;
            L_0x007b:
                r1 = 5;
            L_0x007c:
                r1 = r5 - r1;
                r1 = (float) r1;
                r1 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r1);
                r1 = r1 + r4;
                r4 = r9.f1027b;
                r4 = r4.f1028a;
                r4 = r4.f1058N;
                r4 = r4[r3];
                r4 = r4 - r1;
                r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r8);
                if (r4 >= r5) goto L_0x019e;
            L_0x0095:
                r4 = r9.f1027b;
                r4 = r4.f1028a;
                r4 = r4.f1058N;
                r4 = r4[r3];
                r4 = r4 - r1;
                r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r8);
                r4 = r4 - r5;
                r1 = r1 + r4;
            L_0x00a6:
                r4 = -r1;
                r1 = r10.getTop();
                if (r1 >= 0) goto L_0x01de;
            L_0x00ad:
                r1 = r10.getTop();
            L_0x00b1:
                r3 = r9.f1027b;
                r3 = r3.f1028a;
                r5 = r3.f1053I;
                r3 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
                if (r3 == 0) goto L_0x01e1;
            L_0x00bf:
                r3 = 1106247680; // 0x41f00000 float:30.0 double:5.465589745E-315;
            L_0x00c1:
                r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
                r3 = r3 - r4;
                r6 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
                r6 = com.hanista.mobogram.messenger.AndroidUtilities.dpf2(r6);
                r6 = (int) r6;
                r3 = r3 + r6;
                r5.m1027a(r0, r3);
                r0 = r9.f1027b;
                r0 = r0.f1028a;
                r0 = r0.f1054J;
                r0.setFocusable(r2);
                r0 = r9.f1027b;
                r0 = r0.f1028a;
                r0 = r0.f1054J;
                r3 = r10.getMeasuredHeight();
                r3 = -r3;
                r5 = r9.f1027b;
                r5 = r5.f1028a;
                r5 = r5.f1056L;
                r3 = r3 - r5;
                r5 = r10.getMeasuredHeight();
                r6 = r9.f1027b;
                r6 = r6.f1028a;
                r6 = r6.f1057M;
                r5 = r5 - r6;
                r5 = r5 / 2;
                r3 = r3 + r5;
                r1 = r3 - r1;
                r0.showAsDropDown(r10, r4, r1);
                r0 = r10.getParent();
                r0.requestDisallowInterceptTouchEvent(r2);
            L_0x010e:
                return r2;
            L_0x010f:
                r7 = "\ud83c\udffb";
                r1 = r1.equals(r7);
                if (r1 == 0) goto L_0x0043;
            L_0x0118:
                r1 = r3;
                goto L_0x0044;
            L_0x011b:
                r7 = "\ud83c\udffc";
                r1 = r1.equals(r7);
                if (r1 == 0) goto L_0x0043;
            L_0x0124:
                r1 = r2;
                goto L_0x0044;
            L_0x0127:
                r7 = "\ud83c\udffd";
                r1 = r1.equals(r7);
                if (r1 == 0) goto L_0x0043;
            L_0x0130:
                r1 = r5;
                goto L_0x0044;
            L_0x0133:
                r7 = "\ud83c\udffe";
                r1 = r1.equals(r7);
                if (r1 == 0) goto L_0x0043;
            L_0x013c:
                r1 = r6;
                goto L_0x0044;
            L_0x013f:
                r7 = "\ud83c\udfff";
                r1 = r1.equals(r7);
                if (r1 == 0) goto L_0x0043;
            L_0x0148:
                r1 = 4;
                goto L_0x0044;
            L_0x014b:
                r1 = r9.f1027b;
                r1 = r1.f1028a;
                r1 = r1.f1053I;
                r1.m1026a(r2);
                goto L_0x0047;
            L_0x0158:
                r1 = r9.f1027b;
                r1 = r1.f1028a;
                r1 = r1.f1053I;
                r1.m1026a(r5);
                goto L_0x0047;
            L_0x0165:
                r1 = r9.f1027b;
                r1 = r1.f1028a;
                r1 = r1.f1053I;
                r1.m1026a(r6);
                goto L_0x0047;
            L_0x0172:
                r1 = r9.f1027b;
                r1 = r1.f1028a;
                r1 = r1.f1053I;
                r4 = 4;
                r1.m1026a(r4);
                goto L_0x0047;
            L_0x0180:
                r1 = r9.f1027b;
                r1 = r1.f1028a;
                r1 = r1.f1053I;
                r4 = 5;
                r1.m1026a(r4);
                goto L_0x0047;
            L_0x018e:
                r1 = r9.f1027b;
                r1 = r1.f1028a;
                r1 = r1.f1053I;
                r1.m1026a(r3);
                goto L_0x0047;
            L_0x019b:
                r1 = r2;
                goto L_0x007c;
            L_0x019e:
                r4 = r9.f1027b;
                r4 = r4.f1028a;
                r4 = r4.f1058N;
                r4 = r4[r3];
                r4 = r4 - r1;
                r5 = r9.f1027b;
                r5 = r5.f1028a;
                r5 = r5.f1055K;
                r4 = r4 + r5;
                r5 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
                r5 = r5.x;
                r6 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r8);
                r5 = r5 - r6;
                if (r4 <= r5) goto L_0x00a6;
            L_0x01bd:
                r4 = r9.f1027b;
                r4 = r4.f1028a;
                r4 = r4.f1058N;
                r4 = r4[r3];
                r4 = r4 - r1;
                r5 = r9.f1027b;
                r5 = r5.f1028a;
                r5 = r5.f1055K;
                r4 = r4 + r5;
                r5 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
                r5 = r5.x;
                r6 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r8);
                r5 = r5 - r6;
                r4 = r4 - r5;
                r1 = r1 + r4;
                goto L_0x00a6;
            L_0x01de:
                r1 = r3;
                goto L_0x00b1;
            L_0x01e1:
                r3 = 1102053376; // 0x41b00000 float:22.0 double:5.44486713E-315;
                goto L_0x00c1;
            L_0x01e5:
                r1 = r9.f1027b;
                r1 = r1.f1028a;
                r1 = r1.f1085u;
                r1 = r1.getCurrentItem();
                if (r1 != 0) goto L_0x0205;
            L_0x01f3:
                r1 = com.hanista.mobogram.mobo.MoboConstants.f1310C;
                if (r1 != 0) goto L_0x0205;
            L_0x01f7:
                r0 = r9.f1027b;
                r0 = r0.f1028a;
                r0 = r0.f1084t;
                r0.onClearEmojiRecent();
            L_0x0202:
                r2 = r3;
                goto L_0x010e;
            L_0x0205:
                r1 = com.hanista.mobogram.mobo.MoboConstants.f1310C;
                if (r1 == 0) goto L_0x0202;
            L_0x0209:
                r1 = r9.f1027b;
                r1 = r1.f1028a;
                r1.m1079a(r0);
                goto L_0x010e;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.mobo.g.c.f.2.onLongClick(android.view.View):boolean");
            }
        }

        public EmojiView(EmojiView emojiView, Context context) {
            this.f1028a = emojiView;
            super(context);
            setOnClickListener(new EmojiView(this, emojiView));
            setOnLongClickListener(new EmojiView(this, emojiView));
            setBackgroundResource(C0338R.drawable.list_selector);
            setScaleType(ScaleType.CENTER);
        }

        private void m1034a(String str) {
            String str2 = str != null ? str : (String) getTag();
            if (str == null) {
                if (this.f1028a.f1085u.getCurrentItem() != 0) {
                    String str3 = (String) EmojiView.f1044l.get(str2);
                    if (str3 != null) {
                        str2 = str2 + str3;
                    }
                }
                Integer num = (Integer) this.f1028a.f1076k.get(str2);
                Integer valueOf = num == null ? Integer.valueOf(0) : num;
                if (valueOf.intValue() == 0 && this.f1028a.f1076k.size() > 50) {
                    for (int size = this.f1028a.f1077m.size() - 1; size >= 0; size--) {
                        this.f1028a.f1076k.remove((String) this.f1028a.f1077m.get(size));
                        this.f1028a.f1077m.remove(size);
                        if (this.f1028a.f1076k.size() <= 50) {
                            break;
                        }
                    }
                }
                this.f1028a.f1076k.put(str2, Integer.valueOf(valueOf.intValue() + 1));
                if (this.f1028a.f1085u.getCurrentItem() != 0) {
                    this.f1028a.m1107k();
                }
                this.f1028a.m1102h();
                ((EmojiView) this.f1028a.f1075j.get(0)).notifyDataSetChanged();
                if (this.f1028a.f1084t != null) {
                    this.f1028a.f1084t.onEmojiSelected(Emoji.m1013a(str2));
                }
            } else if (this.f1028a.f1084t != null) {
                this.f1028a.f1084t.onEmojiSelected(Emoji.m1013a(str));
            }
        }

        public void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int i = 5;
            boolean z = true;
            if (this.f1029b) {
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    if (this.f1028a.f1054J != null && this.f1028a.f1054J.isShowing()) {
                        String str;
                        this.f1028a.f1054J.dismiss();
                        switch (this.f1028a.f1053I.m1025a()) {
                            case VideoPlayer.TYPE_AUDIO /*1*/:
                                str = "\ud83c\udffb";
                                break;
                            case VideoPlayer.STATE_PREPARING /*2*/:
                                str = "\ud83c\udffc";
                                break;
                            case VideoPlayer.STATE_BUFFERING /*3*/:
                                str = "\ud83c\udffd";
                                break;
                            case VideoPlayer.STATE_READY /*4*/:
                                str = "\ud83c\udffe";
                                break;
                            case VideoPlayer.STATE_ENDED /*5*/:
                                str = "\ud83c\udfff";
                                break;
                            case Method.TRACE /*6*/:
                                this.f1028a.m1079a((String) getTag());
                                return true;
                            default:
                                str = null;
                                break;
                        }
                        String str2 = (String) getTag();
                        if (this.f1028a.f1085u.getCurrentItem() != 0) {
                            if (str != null) {
                                EmojiView.f1044l.put(str2, str);
                                str2 = str2 + str;
                            } else {
                                EmojiView.f1044l.remove(str2);
                            }
                            setImageDrawable(Emoji.m1016b(str2));
                            m1034a(null);
                            this.f1028a.m1104i();
                        } else {
                            StringBuilder append = new StringBuilder().append(str2);
                            if (str == null) {
                                str = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                            m1034a(append.append(str).toString());
                        }
                    }
                    this.f1029b = false;
                    this.f1032e = -10000.0f;
                    this.f1033f = -10000.0f;
                } else if (motionEvent.getAction() == 2) {
                    if (this.f1032e == -10000.0f) {
                        z = false;
                    } else if (Math.abs(this.f1032e - motionEvent.getX()) > AndroidUtilities.getPixelsInCM(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD, true) || Math.abs(this.f1033f - motionEvent.getY()) > AndroidUtilities.getPixelsInCM(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD, false)) {
                        this.f1032e = -10000.0f;
                        this.f1033f = -10000.0f;
                        z = false;
                    }
                    if (!z) {
                        getLocationOnScreen(this.f1028a.f1058N);
                        float x = ((float) this.f1028a.f1058N[0]) + motionEvent.getX();
                        this.f1028a.f1053I.getLocationOnScreen(this.f1028a.f1058N);
                        int dp = (int) ((x - ((float) (this.f1028a.f1058N[0] + AndroidUtilities.dp(3.0f)))) / ((float) (this.f1028a.f1057M + AndroidUtilities.dp(4.0f))));
                        if (dp < 0) {
                            i = 0;
                        } else {
                            if (dp <= (MoboConstants.f1310C ? 6 : 5)) {
                                i = dp;
                            } else if (MoboConstants.f1310C) {
                                i = 6;
                            }
                        }
                        this.f1028a.f1053I.m1026a(i);
                    }
                }
            }
            this.f1030c = motionEvent.getX();
            this.f1031d = motionEvent.getY();
            return super.onTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.c.g */
    private class EmojiView extends BaseAdapter {
        final /* synthetic */ EmojiView f1035a;
        private Context f1036b;
        private int f1037c;
        private HashMap<Integer, TL_messages_stickerSet> f1038d;
        private HashMap<TL_messages_stickerSet, Integer> f1039e;
        private HashMap<Integer, Document> f1040f;
        private int f1041g;

        /* renamed from: com.hanista.mobogram.mobo.g.c.g.1 */
        class EmojiView extends StickerEmojiCell {
            final /* synthetic */ EmojiView f1034a;

            EmojiView(EmojiView emojiView, Context context) {
                this.f1034a = emojiView;
                super(context);
            }

            public void onMeasure(int i, int i2) {
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), C0700C.ENCODING_PCM_32BIT));
            }
        }

        public EmojiView(EmojiView emojiView, Context context) {
            this.f1035a = emojiView;
            this.f1038d = new HashMap();
            this.f1039e = new HashMap();
            this.f1040f = new HashMap();
            this.f1036b = context;
        }

        public int m1038a(int i) {
            if (this.f1037c == 0) {
                int measuredWidth = this.f1035a.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.f1037c = measuredWidth / AndroidUtilities.dp(72.0f);
            }
            TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.f1038d.get(Integer.valueOf(i / this.f1037c));
            return tL_messages_stickerSet == null ? this.f1035a.f1060P : this.f1035a.f1080p.indexOf(tL_messages_stickerSet) + this.f1035a.f1059O;
        }

        public int m1039a(TL_messages_stickerSet tL_messages_stickerSet) {
            return ((Integer) this.f1039e.get(tL_messages_stickerSet)).intValue() * this.f1037c;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f1041g != 0 ? this.f1041g + 1 : 0;
        }

        public Object getItem(int i) {
            return this.f1040f.get(Integer.valueOf(i));
        }

        public long getItemId(int i) {
            return -1;
        }

        public int getItemViewType(int i) {
            return this.f1040f.get(Integer.valueOf(i)) != null ? 0 : 1;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            Document document = (Document) this.f1040f.get(Integer.valueOf(i));
            if (document != null) {
                View emojiView = view == null ? new EmojiView(this, this.f1036b) : view;
                ((StickerEmojiCell) emojiView).setSticker(document, false);
                return emojiView;
            }
            View emptyCell = view == null ? new EmptyCell(this.f1036b) : view;
            if (i == this.f1041g) {
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.f1038d.get(Integer.valueOf((i - 1) / this.f1037c));
                if (tL_messages_stickerSet == null) {
                    ((EmptyCell) emptyCell).setHeight(1);
                    return emptyCell;
                }
                int height = this.f1035a.f1085u.getHeight() - (((int) Math.ceil((double) (((float) tL_messages_stickerSet.documents.size()) / ((float) this.f1037c)))) * AndroidUtilities.dp(82.0f));
                EmptyCell emptyCell2 = (EmptyCell) emptyCell;
                if (height <= 0) {
                    height = 1;
                }
                emptyCell2.setHeight(height);
                return emptyCell;
            }
            ((EmptyCell) emptyCell).setHeight(AndroidUtilities.dp(82.0f));
            return emptyCell;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean isEnabled(int i) {
            return this.f1040f.get(Integer.valueOf(i)) != null;
        }

        public void notifyDataSetChanged() {
            int measuredWidth = this.f1035a.getMeasuredWidth();
            if (measuredWidth == 0) {
                measuredWidth = AndroidUtilities.displaySize.x;
            }
            this.f1037c = measuredWidth / AndroidUtilities.dp(72.0f);
            this.f1038d.clear();
            this.f1039e.clear();
            this.f1040f.clear();
            this.f1041g = 0;
            ArrayList F = this.f1035a.f1080p;
            for (int i = -1; i < F.size(); i++) {
                ArrayList L;
                Object obj = null;
                int i2 = this.f1041g / this.f1037c;
                if (i == -1) {
                    L = this.f1035a.f1079o;
                } else {
                    TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) F.get(i);
                    L = tL_messages_stickerSet.documents;
                    this.f1039e.put(tL_messages_stickerSet, Integer.valueOf(i2));
                }
                if (!L.isEmpty()) {
                    int ceil = (int) Math.ceil((double) (((float) L.size()) / ((float) this.f1037c)));
                    for (int i3 = 0; i3 < L.size(); i3++) {
                        this.f1040f.put(Integer.valueOf(this.f1041g + i3), L.get(i3));
                    }
                    this.f1041g += this.f1037c * ceil;
                    for (int i4 = 0; i4 < ceil; i4++) {
                        this.f1038d.put(Integer.valueOf(i2 + i4), obj);
                    }
                }
            }
            super.notifyDataSetChanged();
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (dataSetObserver != null) {
                super.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    static {
        Field field = null;
        try {
            field = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        f1042h = field;
        f1043i = new EmojiView();
        f1044l = new HashMap();
    }

    public EmojiView(boolean z, boolean z2, Context context) {
        super(context);
        this.f1075j = new ArrayList();
        this.f1076k = new HashMap();
        this.f1077m = new ArrayList();
        this.f1078n = new ArrayList();
        this.f1079o = new ArrayList();
        this.f1080p = new ArrayList();
        this.f1083s = new int[]{C0338R.drawable.ic_emoji_recent, C0338R.drawable.ic_emoji_smile, C0338R.drawable.ic_emoji_flower, C0338R.drawable.ic_emoji_bell, C0338R.drawable.ic_emoji_car, C0338R.drawable.ic_emoji_symbol, C0338R.drawable.ic_smiles2_stickers};
        this.f1088x = new ArrayList();
        this.f1058N = new int[2];
        this.f1060P = -2;
        this.f1061Q = -2;
        this.ad = new ArrayList();
        this.af = new ArrayList();
        this.ag = new ArrayList();
        this.f1069b = new OnLongClickListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f971a;

            {
                this.f971a = r1;
            }

            public boolean onLongClick(View view) {
                if (this.f971a.ah) {
                    this.f971a.f1047C.setOnItemClickListener(this.f971a.f1052H);
                    this.f971a.ah = false;
                    Toast.makeText(this.f971a.getContext(), LocaleController.getString("FavoriteStickersEditModeExitAlert", C0338R.string.FavoriteStickersEditModeExitAlert), 0).show();
                } else {
                    this.f971a.ah = true;
                    this.f971a.f1047C.setOnItemClickListener(this.f971a.f1074g);
                    this.f971a.m1122r();
                }
                return true;
            }
        };
        this.f1070c = new OnLongClickListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f972a;

            {
                this.f972a = r1;
            }

            public boolean onLongClick(View view) {
                if (view.getTag() != null) {
                    this.f972a.m1089c(((Integer) view.getTag()).intValue());
                }
                return true;
            }
        };
        this.f1071d = new OnLongClickListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f973a;

            {
                this.f973a = r1;
            }

            public boolean onLongClick(View view) {
                this.f973a.m1128u();
                return true;
            }
        };
        this.f1072e = new OnLongClickListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f974a;

            {
                this.f974a = r1;
            }

            public boolean onLongClick(View view) {
                this.f974a.m1126t();
                return true;
            }
        };
        this.f1073f = new OnLongClickOnTabListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f975a;

            {
                this.f975a = r1;
            }

            public void onLongClick(int i) {
                if (i == 0) {
                    this.f975a.f1084t.onClearEmojiRecent();
                } else if (i == 1 && MoboConstants.f1310C) {
                    this.f975a.m1124s();
                }
            }
        };
        this.f1074g = new OnItemClickListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f976a;

            {
                this.f976a = r1;
            }

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Document sticker = ((StickerEmojiCell) view).getSticker();
                FavoriteSticker favoriteSticker = new FavoriteSticker();
                favoriteSticker.m2190a(Long.valueOf(sticker.id));
                if (this.f976a.ag.contains(sticker)) {
                    this.f976a.m1078a(Long.valueOf(sticker.id));
                    return;
                }
                new DataBaseAccess().m846a(favoriteSticker);
                this.f976a.m1116o();
                this.f976a.m1113n();
                Toast.makeText(this.f976a.getContext(), LocaleController.getString("StickerAddedToFavorites", C0338R.string.StickerAddedToFavorites), 0).show();
            }
        };
        this.ai = new int[]{C0338R.drawable.ic_smiles2_recent, C0338R.drawable.ic_smiles2_smile, C0338R.drawable.ic_smiles2_nature, C0338R.drawable.ic_smiles2_food, C0338R.drawable.ic_smiles2_car, C0338R.drawable.ic_smiles2_objects, C0338R.drawable.ic_smiles2_stickers};
        this.aj = new int[]{C0338R.drawable.ic_smiles2_recent_active, C0338R.drawable.ic_smiles2_smile_active, C0338R.drawable.ic_smiles2_nature_active, C0338R.drawable.ic_smiles2_food_active, C0338R.drawable.ic_smiles2_car_active, C0338R.drawable.ic_smiles2_objects_active, C0338R.drawable.ic_smiles2_stickers_active};
        this.f1067W = z;
        this.aa = z2;
        int i = AdvanceTheme.ch;
        int i2 = AdvanceTheme.ci;
        int b = i == -657673 ? -1907225 : AdvanceTheme.m2283b(i, 16);
        int i3 = 0;
        while (true) {
            if (i3 >= (MoboConstants.f1310C ? 2 : 1) + EmojiData.f953d.length) {
                break;
            }
            AbsListView gridView = new GridView(context);
            if (AndroidUtilities.isTablet()) {
                gridView.setColumnWidth(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW));
            } else {
                gridView.setColumnWidth(AndroidUtilities.dp(45.0f));
            }
            gridView.setNumColumns(-1);
            this.f1088x.add(gridView);
            ListAdapter emojiView = new EmojiView(this, i3 - 1);
            gridView.setAdapter(emojiView);
            AndroidUtilities.setListViewEdgeEffectColor(gridView, ThemeUtil.m2490b() ? i : -657673);
            this.f1075j.add(emojiView);
            i3++;
        }
        if (this.f1067W) {
            StickersQuery.checkStickers(0);
            this.f1047C = new AnonymousClass12(this, context);
            this.f1047C.setSelector(C0338R.drawable.transparent);
            this.f1047C.setColumnWidth(AndroidUtilities.dp(72.0f));
            this.f1047C.setNumColumns(-1);
            this.f1047C.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            this.f1047C.setClipToPadding(false);
            this.f1088x.add(this.f1047C);
            this.f1090z = new EmojiView(this, context);
            this.f1047C.setAdapter(this.f1090z);
            this.f1047C.setOnTouchListener(new OnTouchListener() {
                final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f977a;

                {
                    this.f977a = r1;
                }

                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return StickerPreviewViewer.getInstance().onTouch(motionEvent, this.f977a.f1047C, this.f977a.getMeasuredHeight(), this.f977a.ah ? this.f977a.f1074g : this.f977a.f1052H);
                }
            });
            this.f1052H = new OnItemClickListener() {
                final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f991a;

                {
                    this.f991a = r1;
                }

                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    if (view instanceof StickerEmojiCell) {
                        StickerPreviewViewer.getInstance().reset();
                        StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
                        if (!stickerEmojiCell.isDisabled()) {
                            stickerEmojiCell.disable();
                            Document sticker = stickerEmojiCell.getSticker();
                            this.f991a.addRecentSticker(sticker);
                            if (this.f991a.f1084t != null) {
                                this.f991a.f1084t.onStickerSelected(sticker);
                            }
                        }
                    }
                }
            };
            this.f1047C.setOnItemClickListener(this.f1052H);
            AndroidUtilities.setListViewEdgeEffectColor(this.f1047C, -657673);
            this.f1087w = new FrameLayout(context);
            this.f1087w.addView(this.f1047C);
            if (z2) {
                this.f1049E = new RecyclerListView(context);
                this.f1049E.setTag(Integer.valueOf(11));
                RecyclerListView recyclerListView = this.f1049E;
                LayoutManager anonymousClass31 = new AnonymousClass31(this, context, 100);
                this.f1050F = anonymousClass31;
                recyclerListView.setLayoutManager(anonymousClass31);
                this.f1050F.setSpanSizeLookup(new SpanSizeLookup() {
                    final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f994a;

                    {
                        this.f994a = r1;
                    }

                    public int getSpanSize(int i) {
                        return this.f994a.f1050F.getSpanSizeForItem(i);
                    }
                });
                this.f1049E.addItemDecoration(new ItemDecoration() {
                    final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f995a;

                    {
                        this.f995a = r1;
                    }

                    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                        int i = 0;
                        rect.left = 0;
                        rect.top = 0;
                        rect.bottom = 0;
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                        if (!this.f995a.f1050F.isFirstRow(childAdapterPosition)) {
                            rect.top = AndroidUtilities.dp(2.0f);
                        }
                        if (!this.f995a.f1050F.isLastInRow(childAdapterPosition)) {
                            i = AndroidUtilities.dp(2.0f);
                        }
                        rect.right = i;
                    }
                });
                this.f1049E.setOverScrollMode(2);
                recyclerListView = this.f1049E;
                Adapter emojiView2 = new EmojiView(this, context);
                this.f1051G = emojiView2;
                recyclerListView.setAdapter(emojiView2);
                this.f1049E.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                    final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f996a;

                    {
                        this.f996a = r1;
                    }

                    public void onItemClick(View view, int i) {
                        if (i >= 0 && i < this.f996a.f1081q.size() && this.f996a.f1084t != null) {
                            this.f996a.f1084t.onGifSelected(((SearchImage) this.f996a.f1081q.get(i)).document);
                        }
                    }
                });
                this.f1049E.setOnItemLongClickListener(new OnItemLongClickListener() {
                    final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f999a;

                    /* renamed from: com.hanista.mobogram.mobo.g.c.35.1 */
                    class EmojiView implements OnClickListener {
                        final /* synthetic */ SearchImage f997a;
                        final /* synthetic */ AnonymousClass35 f998b;

                        EmojiView(AnonymousClass35 anonymousClass35, SearchImage searchImage) {
                            this.f998b = anonymousClass35;
                            this.f997a = searchImage;
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            this.f998b.f999a.f1081q.remove(this.f997a);
                            StickersQuery.removeRecentGif(this.f997a.document);
                            if (this.f998b.f999a.f1051G != null) {
                                this.f998b.f999a.f1051G.notifyDataSetChanged();
                            }
                            if (this.f998b.f999a.f1081q.isEmpty()) {
                                this.f998b.f999a.m1112m();
                            }
                        }
                    }

                    {
                        this.f999a = r1;
                    }

                    public boolean onItemClick(View view, int i) {
                        if (i < 0 || i >= this.f999a.f1081q.size()) {
                            return false;
                        }
                        SearchImage searchImage = (SearchImage) this.f999a.f1081q.get(i);
                        Builder builder = new Builder(view.getContext());
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setMessage(LocaleController.getString("DeleteGif", C0338R.string.DeleteGif));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK).toUpperCase(), new com.hanista.mobogram.mobo.p006g.EmojiView.35.EmojiView(this, searchImage));
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        builder.show().setCanceledOnTouchOutside(true);
                        return true;
                    }
                });
                this.f1049E.setVisibility(8);
                this.f1087w.addView(this.f1049E);
            }
            this.f1048D = new TextView(context);
            this.f1048D.setText(LocaleController.getString("NoStickers", C0338R.string.NoStickers));
            this.f1048D.setTextSize(1, 18.0f);
            this.f1048D.setTextColor(-7829368);
            this.f1087w.addView(this.f1048D, LayoutHelper.createFrame(-2, -2, 17));
            this.f1047C.setEmptyView(this.f1048D);
            this.f1046B = new EmojiView(this, context);
            this.f1046B.setUnderlineHeight(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.f1046B.setIndicatorColor(-1907225);
            this.f1046B.setUnderlineColor(-1907225);
            if (ThemeUtil.m2490b()) {
                this.f1046B.setIndicatorColor(i2);
                this.f1046B.setUnderlineColor(b);
            }
            this.f1046B.setVisibility(4);
            addView(this.f1046B, LayoutHelper.createFrame(-1, 48, 51));
            this.f1046B.setTranslationX((float) AndroidUtilities.displaySize.x);
            m1112m();
            this.f1046B.setDelegate(new EmojiView(this));
            this.f1047C.setOnScrollListener(new EmojiView(this));
        }
        setBackgroundColor(ThemeUtil.m2490b() ? i : -657673);
        this.f1085u = new EmojiView(this, context);
        this.f1085u.setAdapter(new EmojiView());
        this.f1045A = new EmojiView(this, context);
        this.f1045A.setOrientation(0);
        this.f1045A.setBackgroundColor(ThemeUtil.m2490b() ? i : -657673);
        addView(this.f1045A, LayoutHelper.createFrame(-1, 48.0f));
        View pagerSlidingTabStrip = new PagerSlidingTabStrip(context);
        pagerSlidingTabStrip.setViewPager(this.f1085u);
        pagerSlidingTabStrip.setShouldExpand(true);
        pagerSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(2.0f));
        pagerSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        pagerSlidingTabStrip.setIndicatorColor(-13920542);
        pagerSlidingTabStrip.setUnderlineColor(-1907225);
        if (ThemeUtil.m2490b()) {
            pagerSlidingTabStrip.setIndicatorColor(i2);
            pagerSlidingTabStrip.setUnderlineColor(b);
        }
        pagerSlidingTabStrip.setOnLongClickOnTabListener(this.f1073f);
        this.f1045A.addView(pagerSlidingTabStrip, LayoutHelper.createLinear(0, 48, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        pagerSlidingTabStrip.setOnPageChangeListener(new EmojiView(this));
        View frameLayout = new FrameLayout(context);
        this.f1045A.addView(frameLayout, LayoutHelper.createLinear(52, 48));
        this.f1089y = new EmojiView(this, context);
        this.f1089y.setImageResource(C0338R.drawable.ic_smiles_backspace);
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getResources().getDrawable(C0338R.drawable.ic_smiles_backspace);
            drawable.setColorFilter(AdvanceTheme.cj, Mode.SRC_IN);
            this.f1089y.setImageDrawable(drawable);
        }
        this.f1089y.setBackgroundResource(C0338R.drawable.ic_emoji_backspace);
        this.f1089y.setScaleType(ScaleType.CENTER);
        frameLayout.addView(this.f1089y, LayoutHelper.createFrame(52, 48.0f));
        View view = new View(context);
        view.setBackgroundColor(-1907225);
        if (ThemeUtil.m2490b()) {
            view.setBackgroundColor(b);
            b = AdvanceTheme.cf;
            if (b > 0) {
                Orientation orientation;
                switch (b) {
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
                int i4 = AdvanceTheme.cg;
                setBackgroundDrawable(new GradientDrawable(orientation, new int[]{i, i4}));
                this.f1045A.setBackgroundColor(0);
                if (this.f1046B != null) {
                    this.f1046B.setUnderlineColor(0);
                }
                pagerSlidingTabStrip.setUnderlineColor(0);
                view.setBackgroundColor(0);
            }
        }
        frameLayout.addView(view, LayoutHelper.createFrame(52, 1, 83));
        this.f1086v = new FrameLayout(context);
        this.f1086v.addView((View) this.f1088x.get(0));
        pagerSlidingTabStrip = new TextView(context);
        pagerSlidingTabStrip.setTypeface(FontUtil.m1176a().m1161d());
        pagerSlidingTabStrip.setText(LocaleController.getString("NoRecent", C0338R.string.NoRecent));
        pagerSlidingTabStrip.setTextSize(18.0f);
        pagerSlidingTabStrip.setTextColor(-7829368);
        pagerSlidingTabStrip.setGravity(17);
        this.f1086v.addView(pagerSlidingTabStrip);
        ((GridView) this.f1088x.get(0)).setEmptyView(pagerSlidingTabStrip);
        addView(this.f1085u, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.f1057M = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        this.f1053I = new EmojiView(this, context);
        View view2 = this.f1053I;
        i3 = AndroidUtilities.dp((float) (((MoboConstants.f1310C ? 6 : 5) * 4) + (((AndroidUtilities.isTablet() ? 40 : 32) * (MoboConstants.f1310C ? 7 : 6)) + 10)));
        this.f1055K = i3;
        b = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 64.0f : 56.0f);
        this.f1056L = b;
        this.f1054J = new EmojiView(this, view2, i3, b);
        this.f1054J.setOutsideTouchable(true);
        this.f1054J.setClippingEnabled(true);
        this.f1054J.setInputMethodMode(2);
        this.f1054J.setSoftInputMode(0);
        this.f1054J.getContentView().setFocusableInTouchMode(true);
        this.f1054J.getContentView().setOnKeyListener(new EmojiView(this));
        if (MoboConstants.f1309B) {
            m1116o();
        }
        if (MoboConstants.f1310C) {
            m1118p();
        }
        m1134a();
    }

    private int m1066a(ArrayList<SearchImage> arrayList) {
        if (arrayList == null) {
            return 0;
        }
        long j = 0;
        for (int i = 0; i < Math.min(Callback.DEFAULT_DRAG_ANIMATION_DURATION, arrayList.size()); i++) {
            SearchImage searchImage = (SearchImage) arrayList.get(i);
            if (searchImage.document != null) {
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (searchImage.document.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) searchImage.document.id))) % 2147483648L;
            }
        }
        return (int) j;
    }

    private void m1070a(int i) {
        if (this.f1047C != null) {
            if (this.f1047C.getVisibility() != 0) {
                this.f1046B.onPageScrolled(this.f1061Q + 1, (this.f1060P > 0 ? this.f1060P : this.f1059O) + 1);
                return;
            }
            int childCount = this.f1047C.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.f1047C.getChildAt(i2);
                if (childAt.getTop() + childAt.getHeight() >= AndroidUtilities.dp(5.0f)) {
                    break;
                }
                i++;
            }
            this.f1046B.onPageScrolled(this.f1090z.m1038a(i) + 1, (this.f1060P > 0 ? this.f1060P : this.f1059O) + 1);
        }
    }

    private void m1071a(int i, int i2, int i3) {
        int i4 = 6;
        boolean z = true;
        int i5 = 0;
        if (this.f1046B != null) {
            int i6;
            if (i2 == 0) {
                i2 = AndroidUtilities.displaySize.x;
            }
            if (i == (MoboConstants.f1310C ? 6 : 5)) {
                i4 = -i3;
                if (this.f1084t != null) {
                    Listener listener = this.f1084t;
                    if (i3 == 0) {
                        z = false;
                    }
                    listener.onStickersTab(z);
                    i6 = i4;
                }
                i6 = i4;
            } else {
                if (MoboConstants.f1310C) {
                    i4 = 7;
                }
                if (i == i4) {
                    i4 = -i2;
                    if (this.f1084t != null) {
                        this.f1084t.onStickersTab(true);
                        i6 = i4;
                    }
                    i6 = i4;
                } else {
                    if (this.f1084t != null) {
                        this.f1084t.onStickersTab(false);
                    }
                    i6 = 0;
                }
            }
            if (this.f1045A.getTranslationX() != ((float) i6)) {
                this.f1045A.setTranslationX((float) i6);
                this.f1046B.setTranslationX((float) (i2 + i6));
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.f1046B;
                if (i6 >= 0) {
                    i5 = 4;
                }
                scrollSlidingTabStrip.setVisibility(i5);
            }
        }
    }

    private void m1072a(int i, TL_messages_stickerSet tL_messages_stickerSet) {
        int i2 = 2;
        if (i == 0) {
            Context context = getContext();
            StickerSet stickerSet = tL_messages_stickerSet.set;
            if (!tL_messages_stickerSet.set.archived) {
                i2 = 1;
            }
            StickersQuery.removeStickersSet(context, stickerSet, i2, null, true);
        } else if (i == 1) {
            StickersQuery.removeStickersSet(getContext(), tL_messages_stickerSet.set, 0, null, true);
        } else if (i == 2) {
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TEXT", String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[]{tL_messages_stickerSet.set.short_name}));
                ((Activity) getContext()).startActivityForResult(Intent.createChooser(intent, LocaleController.getString("StickersShare", C0338R.string.StickersShare)), 500);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        } else if (i == 3) {
            try {
                if (VERSION.SDK_INT < 11) {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setText(String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[]{tL_messages_stickerSet.set.short_name}));
                } else {
                    ((android.content.ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[]{tL_messages_stickerSet.set.short_name})));
                }
                Toast.makeText(getContext(), LocaleController.getString("LinkCopied", C0338R.string.LinkCopied), 0).show();
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
        }
    }

    private void m1078a(Long l) {
        Builder builder = new Builder(getContext());
        builder.setMessage(LocaleController.formatString("StickerRemoveFromFavoritesAlert", C0338R.string.StickerRemoveFromFavoritesAlert, new Object[0]));
        builder.setTitle(LocaleController.getString("FavoriteStickers", C0338R.string.FavoriteStickers));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass25(this, l));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        builder.create().show();
    }

    private void m1079a(String str) {
        FavoriteSticker favoriteSticker;
        Iterator it = this.af.iterator();
        while (it.hasNext()) {
            favoriteSticker = (FavoriteSticker) it.next();
            if (favoriteSticker.m2194d() != null && favoriteSticker.m2194d().equals(str)) {
                m1085b(favoriteSticker.m2189a());
                return;
            }
        }
        favoriteSticker = new FavoriteSticker();
        favoriteSticker.m2191a(str);
        new DataBaseAccess().m846a(favoriteSticker);
        m1118p();
        Toast.makeText(getContext(), LocaleController.getString("EmojiAddedToFavorites", C0338R.string.EmojiAddedToFavorites), 0).show();
    }

    private TL_messages_stickerSet m1081b(ArrayList<TL_messages_stickerSet> arrayList) {
        if (this.f1068a == null) {
            this.f1068a = new TL_messages_stickerSet();
            this.f1068a.documents = this.ag;
        }
        return this.f1068a;
    }

    private void m1083b(int i) {
        AndroidUtilities.runOnUIThread(new AnonymousClass10(this, i), (long) i);
    }

    private void m1085b(Long l) {
        Builder builder = new Builder(getContext());
        builder.setMessage(LocaleController.formatString("EmojiRemoveFromFavoritesAlert", C0338R.string.EmojiRemoveFromFavoritesAlert, new Object[0]));
        builder.setTitle(LocaleController.getString("FavoriteEmojis", C0338R.string.FavoriteEmojis));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass26(this, l));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        builder.create().show();
    }

    private void m1089c(int i) {
        int[] iArr;
        CharSequence[] charSequenceArr;
        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.f1080p.get(i);
        Builder builder = new Builder(getContext());
        builder.setTitle(tL_messages_stickerSet.set.title);
        if (tL_messages_stickerSet.set.official) {
            iArr = new int[]{0};
            charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", C0338R.string.StickersHide)};
        } else {
            iArr = new int[]{0, 1, 2, 3};
            charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", C0338R.string.StickersHide), LocaleController.getString("StickersRemove", C0338R.string.StickersRemove), LocaleController.getString("StickersShare", C0338R.string.StickersShare), LocaleController.getString("StickersCopy", C0338R.string.StickersCopy)};
        }
        builder.setItems(charSequenceArr, new AnonymousClass16(this, iArr, tL_messages_stickerSet));
        builder.show();
    }

    private void m1100g() {
        this.f1049E.setVisibility(0);
        this.f1047C.setVisibility(8);
        this.f1048D.setVisibility(8);
        this.f1046B.onPageScrolled(this.f1061Q + 1, (this.f1060P > 0 ? this.f1060P : this.f1059O) + 1);
    }

    private void m1102h() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("emoji", 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry entry : this.f1076k.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        sharedPreferences.edit().putString("emojis2", stringBuilder.toString()).commit();
    }

    private void m1104i() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("emoji", 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry entry : f1044l.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append((String) entry.getValue());
        }
        sharedPreferences.edit().putString(TtmlNode.ATTR_TTS_COLOR, stringBuilder.toString()).commit();
    }

    private void m1106j() {
        int i = 0;
        Editor edit = getContext().getSharedPreferences("emoji", 0).edit();
        StringBuilder stringBuilder = new StringBuilder();
        while (i < this.f1078n.size()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(this.f1078n.get(i));
            i++;
        }
        edit.putString("stickers2", stringBuilder.toString());
        edit.commit();
    }

    private void m1107k() {
        this.f1077m.clear();
        for (Entry key : this.f1076k.entrySet()) {
            this.f1077m.add(key.getKey());
        }
        Collections.sort(this.f1077m, new Comparator<String>() {
            final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f960a;

            {
                this.f960a = r1;
            }

            public int m1023a(String str, String str2) {
                Integer num = (Integer) this.f960a.f1076k.get(str);
                Integer num2 = (Integer) this.f960a.f1076k.get(str2);
                if (num == null) {
                    num = Integer.valueOf(0);
                }
                if (num2 == null) {
                    num2 = Integer.valueOf(0);
                }
                return num.intValue() > num2.intValue() ? -1 : num.intValue() < num2.intValue() ? 1 : 0;
            }

            public /* synthetic */ int compare(Object obj, Object obj2) {
                return m1023a((String) obj, (String) obj2);
            }
        });
        while (this.f1077m.size() > 50) {
            this.f1077m.remove(this.f1077m.size() - 1);
        }
    }

    private void m1109l() {
        int i = 0;
        if (StickersQuery.getStickerSets(0).isEmpty()) {
            this.f1079o.clear();
            return;
        }
        this.f1079o.clear();
        for (int i2 = 0; i2 < this.f1078n.size(); i2++) {
            Document stickerById = StickersQuery.getStickerById(((Long) this.f1078n.get(i2)).longValue());
            if (stickerById != null) {
                this.f1079o.add(stickerById);
            }
        }
        while (this.f1079o.size() > 20) {
            this.f1079o.remove(this.f1079o.size() - 1);
        }
        if (this.f1078n.size() != this.f1079o.size()) {
            this.f1078n.clear();
            while (i < this.f1079o.size()) {
                this.f1078n.add(Long.valueOf(((Document) this.f1079o.get(i)).id));
                i++;
            }
            m1106j();
        }
    }

    private void m1112m() {
        int i = 0;
        if (this.f1046B != null) {
            int i2;
            this.f1060P = -2;
            this.f1061Q = -2;
            this.f1059O = 0;
            int currentPosition = this.f1046B.getCurrentPosition();
            this.f1046B.removeTabs();
            this.f1046B.addIconTab(C0338R.drawable.ic_smiles2_smile);
            if (!(!this.aa || this.f1081q == null || this.f1081q.isEmpty())) {
                this.f1046B.addIconTab(C0338R.drawable.ic_smiles_gif, this.f1072e);
                this.f1061Q = this.f1059O;
                this.f1059O++;
            }
            if (!this.f1079o.isEmpty()) {
                this.f1060P = this.f1059O;
                this.f1059O++;
                this.f1046B.addIconTab(C0338R.drawable.ic_smiles2_recent, this.f1071d);
            }
            this.f1080p.clear();
            ArrayList stickerSets = StickersQuery.getStickerSets(0);
            if (MoboConstants.f1309B) {
                this.f1080p.add(m1081b(stickerSets));
            }
            for (i2 = 0; i2 < stickerSets.size(); i2++) {
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(i2);
                if (!(tL_messages_stickerSet.set.archived || tL_messages_stickerSet.documents == null || tL_messages_stickerSet.documents.isEmpty())) {
                    this.f1080p.add(tL_messages_stickerSet);
                }
            }
            for (i2 = 0; i2 < this.f1080p.size(); i2++) {
                if (i2 == 0 && MoboConstants.f1309B) {
                    this.f1046B.addIconTab(C0338R.drawable.ic_tab_favorite_stickers, this.f1069b);
                } else {
                    this.f1046B.addStickerTab((Document) ((TL_messages_stickerSet) this.f1080p.get(i2)).documents.get(0), Integer.valueOf(i2), this.f1070c);
                }
            }
            this.f1046B.addIconTab(C0338R.drawable.ic_ab_settings);
            this.f1046B.updateTabStyles();
            if (currentPosition != 0) {
                this.f1046B.onPageScrolled(currentPosition, currentPosition);
            }
            if (this.f1062R && this.f1061Q >= 0 && this.f1049E.getVisibility() != 0) {
                m1100g();
                this.f1062R = false;
            }
            if (this.f1061Q == -2 && this.f1049E != null && this.f1049E.getVisibility() == 0) {
                this.f1084t.onGifTab(false);
                this.f1049E.setVisibility(8);
                this.f1047C.setVisibility(0);
                TextView textView = this.f1048D;
                if (this.f1090z.getCount() != 0) {
                    i = 8;
                }
                textView.setVisibility(i);
            } else if (this.f1061Q == -2) {
            } else {
                if (this.f1049E == null || this.f1049E.getVisibility() != 0) {
                    this.f1046B.onPageScrolled(this.f1090z.m1038a(this.f1047C.getFirstVisiblePosition()) + 1, (this.f1060P > 0 ? this.f1060P : this.f1059O) + 1);
                } else {
                    this.f1046B.onPageScrolled(this.f1061Q + 1, (this.f1060P > 0 ? this.f1060P : this.f1059O) + 1);
                }
            }
        }
    }

    private void m1113n() {
        if (this.f1090z != null) {
            this.f1090z.notifyDataSetChanged();
        }
        if (StickerPreviewViewer.getInstance().isVisible()) {
            StickerPreviewViewer.getInstance().close();
        }
        StickerPreviewViewer.getInstance().reset();
    }

    private void m1116o() {
        this.ag.clear();
        for (FavoriteSticker c : new DataBaseAccess().m898l()) {
            Document stickerById = StickersQuery.getStickerById(c.m2193c().longValue());
            if (stickerById != null) {
                this.ag.add(stickerById);
            }
        }
    }

    private void m1118p() {
        if (this.ae == null) {
            this.ae = new FrameLayout(getContext());
            this.ae.addView((View) this.f1088x.get(1));
            View textView = new TextView(getContext());
            textView.setTypeface(FontUtil.m1176a().m1161d());
            textView.setText(LocaleController.getString("NoFavoriteEmoji", C0338R.string.NoFavoriteEmoji));
            textView.setTextSize(16.0f);
            textView.setTextColor(-7829368);
            textView.setGravity(17);
            this.ae.addView(textView);
            ((GridView) this.f1088x.get(1)).setEmptyView(textView);
        }
        this.ad.clear();
        this.af.clear();
        for (FavoriteSticker favoriteSticker : new DataBaseAccess().m900m()) {
            if (favoriteSticker.m2194d() != null) {
                this.ad.add(favoriteSticker.m2194d());
                this.af.add(favoriteSticker);
            }
        }
        ListAdapter emojiView = new EmojiView(this, 0);
        ((GridView) this.f1088x.get(1)).setAdapter(emojiView);
        this.f1075j.set(1, emojiView);
    }

    private void m1119q() {
        SettingManager settingManager = new SettingManager();
        if (!settingManager.m944b("favStickerHelpDisplayed")) {
            settingManager.m943a("favStickerHelpDisplayed", true);
            Builder builder = new Builder(getContext());
            builder.setTitle(LocaleController.getString("Help", C0338R.string.Help)).setMessage(LocaleController.getString("FavoriteStickersTabHelp", C0338R.string.FavoriteStickersTabHelp));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f978a;

                {
                    this.f978a = r1;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private void m1122r() {
        Builder builder = new Builder(getContext());
        builder.setTitle(LocaleController.getString("FavoriteStickers", C0338R.string.FavoriteStickers)).setMessage(LocaleController.getString("FavoriteStickersEditModeAlert", C0338R.string.FavoriteStickersEditModeAlert));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
        builder.create().show();
    }

    private void m1124s() {
        Builder builder = new Builder(getContext());
        builder.setMessage(LocaleController.formatString("DeleteAllFavoriteEmojis", C0338R.string.DeleteAllFavoriteEmojis, new Object[0]));
        builder.setTitle(LocaleController.getString("FavoriteEmojis", C0338R.string.FavoriteEmojis));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f983a;

            {
                this.f983a = r1;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                new DataBaseAccess().m902n();
                this.f983a.m1118p();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        builder.create().show();
    }

    private void m1126t() {
        Builder builder = new Builder(getContext());
        builder.setMessage(LocaleController.formatString("DeleteAllGifs", C0338R.string.DeleteAllGifs, new Object[0]));
        builder.setTitle(LocaleController.getString("Delete", C0338R.string.Delete));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f984a;

            {
                this.f984a = r1;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Iterator it = this.f984a.f1081q.iterator();
                while (it.hasNext()) {
                    StickersQuery.removeRecentGif(((SearchImage) it.next()).document);
                }
                this.f984a.f1081q.clear();
                if (this.f984a.f1051G != null) {
                    this.f984a.f1051G.notifyDataSetChanged();
                }
                if (this.f984a.f1081q.isEmpty()) {
                    this.f984a.m1112m();
                }
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        builder.create().show();
    }

    private void m1128u() {
        Builder builder = new Builder(getContext());
        builder.setMessage(LocaleController.formatString("DeleteAllRecentStickers", C0338R.string.DeleteAllRecentStickers, new Object[0]));
        builder.setTitle(LocaleController.getString("Delete", C0338R.string.Delete));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f985a;

            {
                this.f985a = r1;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Iterator it = this.f985a.f1079o.iterator();
                while (it.hasNext()) {
                    StickersQuery.removeRecentSticker((Document) it.next());
                }
                StickersQuery.getRecentStickersNoCopy(0).clear();
                this.f985a.f1079o = StickersQuery.getRecentStickers(0);
                this.f985a.m1113n();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        builder.create().show();
    }

    public void m1134a() {
        String string;
        long longValue;
        int i;
        String[] strArr;
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("emoji", 0);
        this.ac = sharedPreferences.getLong("lastGifLoadTime", 0);
        try {
            this.f1076k.clear();
            if (sharedPreferences.contains("emojis")) {
                string = sharedPreferences.getString("emojis", TtmlNode.ANONYMOUS_REGION_ID);
                if (string != null && string.length() > 0) {
                    for (String string2 : string2.split(",")) {
                        String[] split = string2.split("=");
                        longValue = Utilities.parseLong(split[0]).longValue();
                        string2 = TtmlNode.ANONYMOUS_REGION_ID;
                        for (int i2 = 0; i2 < 4; i2++) {
                            string2 = String.valueOf((char) ((int) longValue)) + string2;
                            longValue >>= 16;
                            if (longValue == 0) {
                                break;
                            }
                        }
                        if (string2.length() > 0) {
                            this.f1076k.put(string2, Utilities.parseInt(split[1]));
                        }
                    }
                }
                sharedPreferences.edit().remove("emojis").commit();
                m1102h();
            } else {
                string2 = sharedPreferences.getString("emojis2", TtmlNode.ANONYMOUS_REGION_ID);
                if (string2 != null && string2.length() > 0) {
                    for (String split2 : string2.split(",")) {
                        String[] split3 = split2.split("=");
                        this.f1076k.put(split3[0], Utilities.parseInt(split3[1]));
                    }
                }
            }
            if (this.f1076k.isEmpty() && !sharedPreferences.getBoolean("filled_default", false)) {
                strArr = new String[]{"\ud83d\ude02", "\ud83d\ude18", "\u2764", "\ud83d\ude0d", "\ud83d\ude0a", "\ud83d\ude01", "\ud83d\udc4d", "\u263a", "\ud83d\ude14", "\ud83d\ude04", "\ud83d\ude2d", "\ud83d\udc8b", "\ud83d\ude12", "\ud83d\ude33", "\ud83d\ude1c", "\ud83d\ude48", "\ud83d\ude09", "\ud83d\ude03", "\ud83d\ude22", "\ud83d\ude1d", "\ud83d\ude31", "\ud83d\ude21", "\ud83d\ude0f", "\ud83d\ude1e", "\ud83d\ude05", "\ud83d\ude1a", "\ud83d\ude4a", "\ud83d\ude0c", "\ud83d\ude00", "\ud83d\ude0b", "\ud83d\ude06", "\ud83d\udc4c", "\ud83d\ude10", "\ud83d\ude15"};
                for (i = 0; i < strArr.length; i++) {
                    this.f1076k.put(strArr[i], Integer.valueOf(strArr.length - i));
                }
                sharedPreferences.edit().putBoolean("filled_default", true).commit();
                m1102h();
            }
            m1107k();
            ((EmojiView) this.f1075j.get(0)).notifyDataSetChanged();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        try {
            string2 = sharedPreferences.getString(TtmlNode.ATTR_TTS_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
            if (string2 != null && string2.length() > 0) {
                strArr = string2.split(",");
                for (String split4 : strArr) {
                    String[] split5 = split4.split("=");
                    f1044l.put(split5[0], split5[1]);
                }
            }
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
        if (this.f1067W) {
            try {
                this.f1078n.clear();
                string2 = sharedPreferences.getString("stickers", TtmlNode.ANONYMOUS_REGION_ID);
                if (string2 == null || string2.length() <= 0) {
                    strArr = sharedPreferences.getString("stickers2", TtmlNode.ANONYMOUS_REGION_ID).split(",");
                    for (i = 0; i < strArr.length; i++) {
                        if (strArr[i].length() != 0) {
                            longValue = Utilities.parseLong(strArr[i]).longValue();
                            if (longValue != 0) {
                                this.f1078n.add(Long.valueOf(longValue));
                            }
                        }
                    }
                } else {
                    strArr = string2.split(",");
                    HashMap hashMap = new HashMap();
                    for (String split22 : strArr) {
                        split3 = split22.split("=");
                        Long parseLong = Utilities.parseLong(split3[0]);
                        hashMap.put(parseLong, Utilities.parseInt(split3[1]));
                        this.f1078n.add(parseLong);
                    }
                    Collections.sort(this.f1078n, new AnonymousClass13(this, hashMap));
                    sharedPreferences.edit().remove("stickers").commit();
                    m1106j();
                }
                m1109l();
                m1112m();
            } catch (Throwable e22) {
                FileLog.m18e("tmessages", e22);
            }
        }
    }

    public void addRecentGif(Document document) {
        if (document != null) {
            StickersQuery.addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
            boolean isEmpty = this.f1081q.isEmpty();
            if (this.f1051G != null) {
                this.f1051G.notifyDataSetChanged();
            }
            if (isEmpty) {
                m1112m();
            }
        }
    }

    public void addRecentSticker(Document document) {
        if (document != null) {
            int indexOf = this.f1078n.indexOf(Long.valueOf(document.id));
            if (indexOf == -1) {
                this.f1078n.add(0, Long.valueOf(document.id));
                if (this.f1078n.size() > 20) {
                    this.f1078n.remove(this.f1078n.size() - 1);
                }
            } else if (indexOf != 0) {
                this.f1078n.remove(indexOf);
                this.f1078n.add(0, Long.valueOf(document.id));
            }
            m1106j();
        }
    }

    public void m1135b() {
        if (this.aa && this.f1051G != null && !this.f1082r) {
            MessagesStorage.getInstance().loadWebRecent(2);
            this.f1082r = true;
        }
    }

    public void m1136c() {
        if (!this.ab && Math.abs(System.currentTimeMillis() - this.ac) >= 3600000) {
            this.ab = true;
            TLObject tL_messages_getSavedGifs = new TL_messages_getSavedGifs();
            tL_messages_getSavedGifs.hash = m1066a(this.f1081q);
            ConnectionsManager.getInstance().sendRequest(tL_messages_getSavedGifs, new RequestDelegate() {
                final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f967a;

                /* renamed from: com.hanista.mobogram.mobo.g.c.15.1 */
                class EmojiView implements Runnable {
                    final /* synthetic */ ArrayList f965a;
                    final /* synthetic */ AnonymousClass15 f966b;

                    EmojiView(AnonymousClass15 anonymousClass15, ArrayList arrayList) {
                        this.f966b = anonymousClass15;
                        this.f965a = arrayList;
                    }

                    public void run() {
                        if (this.f965a != null) {
                            boolean isEmpty = this.f966b.f967a.f1081q.isEmpty();
                            this.f966b.f967a.f1081q = this.f965a;
                            if (this.f966b.f967a.f1051G != null) {
                                this.f966b.f967a.f1051G.notifyDataSetChanged();
                            }
                            this.f966b.f967a.ac = System.currentTimeMillis();
                            this.f966b.f967a.getContext().getSharedPreferences("emoji", 0).edit().putLong("lastGifLoadTime", this.f966b.f967a.ac).commit();
                            if (isEmpty && !this.f966b.f967a.f1081q.isEmpty()) {
                                this.f966b.f967a.m1112m();
                            }
                        }
                        this.f966b.f967a.ab = false;
                    }
                }

                {
                    this.f967a = r1;
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    ArrayList arrayList = null;
                    if (tLObject instanceof TL_messages_savedGifs) {
                        ArrayList arrayList2 = new ArrayList();
                        TL_messages_savedGifs tL_messages_savedGifs = (TL_messages_savedGifs) tLObject;
                        int size = tL_messages_savedGifs.gifs.size();
                        for (int i = 0; i < size; i++) {
                            SearchImage searchImage = new SearchImage();
                            searchImage.type = 2;
                            searchImage.document = (Document) tL_messages_savedGifs.gifs.get(i);
                            searchImage.date = size - i;
                            searchImage.id = TtmlNode.ANONYMOUS_REGION_ID + searchImage.document.id;
                            arrayList2.add(searchImage);
                            MessagesStorage.getInstance().putWebRecent(arrayList2);
                        }
                        arrayList = arrayList2;
                    }
                    AndroidUtilities.runOnUIThread(new com.hanista.mobogram.mobo.p006g.EmojiView.15.EmojiView(this, arrayList));
                }
            });
        }
    }

    public void clearRecentEmoji() {
        getContext().getSharedPreferences("emoji", 0).edit().putBoolean("filled_default", true).commit();
        this.f1076k.clear();
        this.f1077m.clear();
        m1102h();
        ((EmojiView) this.f1075j.get(0)).notifyDataSetChanged();
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoaded) {
            m1112m();
            m1113n();
        } else if (i == NotificationCenter.recentImagesDidLoaded && ((Integer) objArr[0]).intValue() == 2) {
            int size;
            if (this.f1081q != null) {
                size = this.f1081q.size();
            } else {
                boolean z = false;
            }
            this.f1081q = (ArrayList) objArr[1];
            this.f1082r = false;
            if (this.f1051G != null) {
                this.f1051G.notifyDataSetChanged();
            }
            if (size != this.f1081q.size()) {
                m1112m();
            }
            m1136c();
        }
    }

    public int getCurrentPage() {
        return 0;
    }

    public void invalidateViews() {
        Iterator it = this.f1088x.iterator();
        while (it.hasNext()) {
            GridView gridView = (GridView) it.next();
            if (gridView != null) {
                gridView.invalidateViews();
            }
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.f1090z != null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
            AndroidUtilities.runOnUIThread(new Runnable() {
                final /* synthetic */ com.hanista.mobogram.mobo.p006g.EmojiView f964a;

                {
                    this.f964a = r1;
                }

                public void run() {
                    this.f964a.m1112m();
                    this.f964a.m1113n();
                }
            });
        }
    }

    public void onDestroy() {
        if (this.f1090z != null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentImagesDidLoaded);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.f1054J != null && this.f1054J.isShowing()) {
            this.f1054J.dismiss();
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.f1064T != i3 - i) {
            this.f1064T = i3 - i;
            m1113n();
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    public void onMeasure(int i, int i2) {
        LayoutParams layoutParams = (LayoutParams) this.f1045A.getLayoutParams();
        ViewGroup.LayoutParams layoutParams2 = null;
        layoutParams.width = MeasureSpec.getSize(i);
        if (this.f1046B != null) {
            layoutParams2 = (LayoutParams) this.f1046B.getLayoutParams();
            if (layoutParams2 != null) {
                layoutParams2.width = layoutParams.width;
            }
        }
        if (layoutParams.width != this.f1063S) {
            if (!(this.f1046B == null || layoutParams2 == null)) {
                m1071a(this.f1085u.getCurrentItem(), layoutParams.width, 0);
                this.f1046B.setLayoutParams(layoutParams2);
            }
            this.f1045A.setLayoutParams(layoutParams);
            this.f1063S = layoutParams.width;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(layoutParams.width, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i2), C0700C.ENCODING_PCM_32BIT));
    }

    public void onOpen(boolean z) {
    }

    public void setListener(Listener listener) {
        this.f1084t = listener;
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 8) {
            m1107k();
            ((EmojiView) this.f1075j.get(0)).notifyDataSetChanged();
            if (this.f1090z != null) {
                NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
                NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
                m1109l();
                m1112m();
                m1113n();
                if (!(this.f1049E == null || this.f1049E.getVisibility() != 0 || this.f1084t == null)) {
                    this.f1084t.onGifTab(true);
                }
            }
            m1135b();
        }
    }

    public void switchToGifRecent() {
        this.f1085u.setCurrentItem(MoboConstants.f1310C ? 7 : 6);
        if (this.f1061Q < 0 || this.f1081q.isEmpty()) {
            this.f1062R = true;
        } else {
            this.f1046B.selectTab(this.f1061Q + 1);
        }
    }
}
