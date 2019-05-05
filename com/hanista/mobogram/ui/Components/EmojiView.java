package com.hanista.mobogram.ui.Components;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Outline;
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
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
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.EmojiData;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.GridLayoutManager;
import com.hanista.mobogram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemDecoration;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.State;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p006g.EmojiViewInf;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p018q.FavoriteSticker;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.StickerSet;
import com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_stickerSet;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.ContextLinkCell;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.FeaturedStickerSetInfoCell;
import com.hanista.mobogram.ui.Cells.StickerEmojiCell;
import com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.IconTabProvider;
import com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.OnLongClickOnTabListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate;
import com.hanista.mobogram.ui.StickerPreviewViewer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

public class EmojiView extends EmojiViewInf implements NotificationCenterDelegate {
    private static final OnScrollChangedListener NOP;
    private static HashMap<String, String> emojiColor;
    private static final Field superListenerField;
    private ArrayList<EmojiGridAdapter> adapters;
    private ImageView backspaceButton;
    private boolean backspaceOnce;
    private boolean backspacePressed;
    private int currentBackgroundType;
    private int currentPage;
    private Drawable dotDrawable;
    private ArrayList<GridView> emojiGrids;
    private int emojiSize;
    private LinearLayout emojiTab;
    OnLongClickOnTabListener emojiTabLongClickListener;
    private HashMap<String, Integer> emojiUseHistory;
    TL_messages_stickerSet favSet;
    private ArrayList<FavoriteSticker> favoriteEmojiList;
    private FrameLayout favoriteEmojiWrap;
    private ArrayList<String> favoriteEmojis;
    OnItemClickListener favoriteOnItemClickListener;
    private ArrayList<Document> favoriteStickers;
    private boolean favoriteStickersEditMode;
    OnLongClickListener favoriteTabLongClickListener;
    private ExtendedGridLayoutManager flowLayoutManager;
    private int gifTabNum;
    private GifsAdapter gifsAdapter;
    private RecyclerListView gifsGridView;
    OnLongClickListener gifsTabLongClickListener;
    private int[] icons;
    private int[] icons_active;
    private int[] icons_inactive;
    private HashMap<Long, StickerSetCovered> installingStickerSets;
    private boolean isLayout;
    private int lastNotifyWidth;
    private Listener listener;
    private int[] location;
    private int minusDy;
    private int oldWidth;
    private Object outlineProvider;
    private ViewPager pager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private EmojiColorPickerView pickerView;
    private EmojiPopupWindow pickerViewPopup;
    private int popupHeight;
    private int popupWidth;
    private ArrayList<String> recentEmoji;
    private ArrayList<Document> recentGifs;
    private ArrayList<Document> recentStickers;
    OnLongClickListener recentStickersTabLongClickListener;
    private int recentTabBum;
    private HashMap<Long, StickerSetCovered> removingStickerSets;
    private boolean showGifs;
    private ArrayList<TL_messages_stickerSet> stickerSets;
    OnLongClickListener stickerTabLongClickListener;
    private TextView stickersEmptyView;
    private StickersGridAdapter stickersGridAdapter;
    private RecyclerListView stickersGridView;
    private GridLayoutManager stickersLayoutManager;
    private OnItemClickListener stickersOnItemClickListener;
    private ScrollSlidingTabStrip stickersTab;
    private int stickersTabOffset;
    private FrameLayout stickersWrap;
    private boolean switchToGifTab;
    private TrendingGridAdapter trendingGridAdapter;
    private RecyclerListView trendingGridView;
    private GridLayoutManager trendingLayoutManager;
    private boolean trendingLoaded;
    private int trendingTabNum;
    private ArrayList<View> views;

    public interface Listener {
        boolean onBackspace();

        void onClearEmojiRecent();

        void onEmojiSelected(String str);

        void onGifSelected(Document document);

        void onGifTab(boolean z);

        void onShowStickerSet(StickerSetCovered stickerSetCovered);

        void onStickerSelected(Document document);

        void onStickerSetAdd(StickerSetCovered stickerSetCovered);

        void onStickerSetRemove(StickerSetCovered stickerSetCovered);

        void onStickersSettingsClick();

        void onStickersTab(boolean z);
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.11 */
    class AnonymousClass11 extends ExtendedGridLayoutManager {
        private Size size;

        AnonymousClass11(Context context, int i) {
            super(context, i);
            this.size = new Size();
        }

        protected Size getSizeForItem(int i) {
            float f = 100.0f;
            Document document = (Document) EmojiView.this.recentGifs.get(i);
            Size size = this.size;
            float f2 = (document.thumb == null || document.thumb.f2664w == 0) ? 100.0f : (float) document.thumb.f2664w;
            size.width = f2;
            Size size2 = this.size;
            if (!(document.thumb == null || document.thumb.f2663h == 0)) {
                f = (float) document.thumb.f2663h;
            }
            size2.height = f;
            for (int i2 = 0; i2 < document.attributes.size(); i2++) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i2);
                if ((documentAttribute instanceof TL_documentAttributeImageSize) || (documentAttribute instanceof TL_documentAttributeVideo)) {
                    this.size.width = (float) documentAttribute.f2659w;
                    this.size.height = (float) documentAttribute.f2658h;
                    break;
                }
            }
            return this.size;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.17 */
    class AnonymousClass17 extends ScrollSlidingTabStrip {
        boolean first;
        float lastTranslateX;
        float lastX;
        boolean startedScroll;

        AnonymousClass17(Context context) {
            super(context);
            this.first = true;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.first) {
                this.first = false;
                this.lastX = motionEvent.getX();
            }
            float translationX = EmojiView.this.stickersTab.getTranslationX();
            if (EmojiView.this.stickersTab.getScrollX() == 0 && translationX == 0.0f) {
                if (this.startedScroll || this.lastX - motionEvent.getX() >= 0.0f) {
                    if (this.startedScroll && this.lastX - motionEvent.getX() > 0.0f && EmojiView.this.pager.isFakeDragging()) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                } else if (EmojiView.this.pager.beginFakeDrag()) {
                    this.startedScroll = true;
                    this.lastTranslateX = EmojiView.this.stickersTab.getTranslationX();
                }
            }
            if (this.startedScroll) {
                try {
                    EmojiView.this.pager.fakeDragBy((float) ((int) (((motionEvent.getX() - this.lastX) + translationX) - this.lastTranslateX)));
                    this.lastTranslateX = translationX;
                } catch (Throwable e) {
                    try {
                        EmojiView.this.pager.endFakeDrag();
                    } catch (Exception e2) {
                    }
                    this.startedScroll = false;
                    FileLog.m18e("tmessages", e);
                }
            }
            this.lastX = motionEvent.getX();
            if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                this.first = true;
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
            }
            return this.startedScroll || super.onTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.1 */
    static class C13361 implements OnScrollChangedListener {
        C13361() {
        }

        public void onScrollChanged() {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.20 */
    class AnonymousClass20 extends ViewPager {
        AnonymousClass20(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.21 */
    class AnonymousClass21 extends LinearLayout {
        AnonymousClass21(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.23 */
    class AnonymousClass23 extends ImageView {
        AnonymousClass23(Context context) {
            super(context);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                EmojiView.this.backspacePressed = true;
                EmojiView.this.backspaceOnce = false;
                EmojiView.this.postBackspaceRunnable(350);
            } else if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                EmojiView.this.backspacePressed = false;
                if (!(EmojiView.this.backspaceOnce || EmojiView.this.listener == null || !EmojiView.this.listener.onBackspace())) {
                    EmojiView.this.backspaceButton.performHapticFeedback(3);
                }
            }
            super.onTouchEvent(motionEvent);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.25 */
    class AnonymousClass25 implements Runnable {
        final /* synthetic */ int val$time;

        AnonymousClass25(int i) {
            this.val$time = i;
        }

        public void run() {
            if (EmojiView.this.backspacePressed) {
                if (EmojiView.this.listener != null && EmojiView.this.listener.onBackspace()) {
                    EmojiView.this.backspaceButton.performHapticFeedback(3);
                }
                EmojiView.this.backspaceOnce = true;
                EmojiView.this.postBackspaceRunnable(Math.max(50, this.val$time - 100));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.28 */
    class AnonymousClass28 implements OnClickListener {
        final /* synthetic */ int[] val$options;
        final /* synthetic */ TL_messages_stickerSet val$stickerSet;

        AnonymousClass28(int[] iArr, TL_messages_stickerSet tL_messages_stickerSet) {
            this.val$options = iArr;
            this.val$stickerSet = tL_messages_stickerSet;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            EmojiView.this.processSelectionOption(this.val$options[i], this.val$stickerSet);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.2 */
    class C13372 extends ViewOutlineProvider {
        C13372() {
        }

        @TargetApi(21)
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(view.getPaddingLeft(), view.getPaddingTop(), view.getMeasuredWidth() - view.getPaddingRight(), view.getMeasuredHeight() - view.getPaddingBottom(), (float) AndroidUtilities.dp(6.0f));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.36 */
    class AnonymousClass36 implements OnClickListener {
        final /* synthetic */ Long val$docId;

        AnonymousClass36(Long l) {
            this.val$docId = l;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            new DataBaseAccess().m897k(this.val$docId);
            EmojiView.this.loadFavoriteStickers();
            EmojiView.this.reloadStickersAdapter();
            Toast.makeText(EmojiView.this.getContext(), LocaleController.getString("StickerRemovedFromFavorites", C0338R.string.StickerRemovedFromFavorites), 0).show();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.37 */
    class AnonymousClass37 implements OnClickListener {
        final /* synthetic */ Long val$id;

        AnonymousClass37(Long l) {
            this.val$id = l;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            new DataBaseAccess().m894j(this.val$id);
            EmojiView.this.loadFavoriteEmojis();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.3 */
    class C13383 extends RecyclerListView {
        C13383(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return super.onInterceptTouchEvent(motionEvent) || StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight());
        }

        public void setVisibility(int i) {
            if ((EmojiView.this.gifsGridView == null || EmojiView.this.gifsGridView.getVisibility() != 0) && (EmojiView.this.trendingGridView == null || EmojiView.this.trendingGridView.getVisibility() != 0)) {
                super.setVisibility(i);
            } else {
                super.setVisibility(8);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.4 */
    class C13394 extends SpanSizeLookup {
        C13394() {
        }

        public int getSpanSize(int i) {
            return i == EmojiView.this.stickersGridAdapter.totalItems ? EmojiView.this.stickersGridAdapter.stickersPerRow : 1;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.5 */
    class C13405 implements OnTouchListener {
        C13405() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return StickerPreviewViewer.getInstance().onTouch(motionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.favoriteStickersEditMode ? EmojiView.this.favoriteOnItemClickListener : EmojiView.this.stickersOnItemClickListener);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.6 */
    class C13416 implements OnItemClickListener {
        C13416() {
        }

        public void onItemClick(View view, int i) {
            if (view instanceof StickerEmojiCell) {
                StickerPreviewViewer.getInstance().reset();
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
                if (!stickerEmojiCell.isDisabled()) {
                    stickerEmojiCell.disable();
                    EmojiView.this.listener.onStickerSelected(stickerEmojiCell.getSticker());
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.7 */
    class C13427 extends GridLayoutManager {
        C13427(Context context, int i) {
            super(context, i);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.8 */
    class C13438 extends SpanSizeLookup {
        C13438() {
        }

        public int getSpanSize(int i) {
            return ((EmojiView.this.trendingGridAdapter.cache.get(Integer.valueOf(i)) instanceof Integer) || i == EmojiView.this.trendingGridAdapter.totalItems) ? EmojiView.this.trendingGridAdapter.stickersPerRow : 1;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.9 */
    class C13449 extends OnScrollListener {
        C13449() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            EmojiView.this.checkStickersTabY(recyclerView, i2);
        }
    }

    private class EmojiColorPickerView extends View {
        private Drawable arrowDrawable;
        private int arrowX;
        private Drawable backgroundDrawable;
        private String currentEmoji;
        private RectF rect;
        private Paint rectPaint;
        private int selection;

        public EmojiColorPickerView(Context context) {
            super(context);
            this.rectPaint = new Paint(1);
            this.rect = new RectF();
            this.backgroundDrawable = getResources().getDrawable(C0338R.drawable.stickers_back_all);
            this.arrowDrawable = getResources().getDrawable(C0338R.drawable.stickers_back_arrow);
        }

        public String getEmoji() {
            return this.currentEmoji;
        }

        public int getSelection() {
            return this.selection;
        }

        protected void onDraw(Canvas canvas) {
            float f = 55.5f;
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(AndroidUtilities.isTablet() ? BitmapDescriptorFactory.HUE_YELLOW : 52.0f));
            this.backgroundDrawable.draw(canvas);
            Drawable drawable = this.arrowDrawable;
            int dp = this.arrowX - AndroidUtilities.dp(9.0f);
            int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 55.5f : 47.5f);
            int dp3 = this.arrowX + AndroidUtilities.dp(9.0f);
            if (!AndroidUtilities.isTablet()) {
                f = 47.5f;
            }
            drawable.setBounds(dp, dp2, dp3, AndroidUtilities.dp(f + 8.0f));
            this.arrowDrawable.draw(canvas);
            if (this.currentEmoji != null) {
                dp2 = 0;
                while (true) {
                    if (dp2 < (MoboConstants.f1310C ? 7 : 6)) {
                        int access$900 = (EmojiView.this.emojiSize * dp2) + AndroidUtilities.dp((float) ((dp2 * 4) + 5));
                        int dp4 = AndroidUtilities.dp(9.0f);
                        if (this.selection == dp2) {
                            this.rect.set((float) access$900, (float) (dp4 - ((int) AndroidUtilities.dpf2(3.5f))), (float) (EmojiView.this.emojiSize + access$900), (float) ((EmojiView.this.emojiSize + dp4) + AndroidUtilities.dp(3.0f)));
                            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.rectPaint);
                        }
                        String str = this.currentEmoji;
                        if (dp2 != 0) {
                            String str2;
                            switch (dp2) {
                                case VideoPlayer.TYPE_AUDIO /*1*/:
                                    str2 = "\ud83c\udffb";
                                    break;
                                case VideoPlayer.STATE_PREPARING /*2*/:
                                    str2 = "\ud83c\udffc";
                                    break;
                                case VideoPlayer.STATE_BUFFERING /*3*/:
                                    str2 = "\ud83c\udffd";
                                    break;
                                case VideoPlayer.STATE_READY /*4*/:
                                    str2 = "\ud83c\udffe";
                                    break;
                                case VideoPlayer.STATE_ENDED /*5*/:
                                    str2 = "\ud83c\udfff";
                                    break;
                                case Method.TRACE /*6*/:
                                    str2 = TtmlNode.ANONYMOUS_REGION_ID;
                                    str = "\u2764";
                                    break;
                                default:
                                    str2 = TtmlNode.ANONYMOUS_REGION_ID;
                                    break;
                            }
                            str = EmojiView.addColorToCode(str, str2);
                        }
                        Drawable emojiBigDrawable = Emoji.getEmojiBigDrawable(str);
                        if (emojiBigDrawable != null) {
                            emojiBigDrawable.setBounds(access$900, dp4, EmojiView.this.emojiSize + access$900, EmojiView.this.emojiSize + dp4);
                            emojiBigDrawable.draw(canvas);
                        }
                        dp2++;
                    } else {
                        return;
                    }
                }
            }
        }

        public void setEmoji(String str, int i) {
            this.currentEmoji = str;
            this.arrowX = i;
            this.rectPaint.setColor(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR);
            invalidate();
        }

        public void setSelection(int i) {
            if (this.selection != i) {
                this.selection = i;
                invalidate();
            }
        }
    }

    private class EmojiGridAdapter extends BaseAdapter {
        private int emojiPage;

        public EmojiGridAdapter(int i) {
            this.emojiPage = i;
        }

        public int getCount() {
            return this.emojiPage == -1 ? EmojiView.this.recentEmoji.size() : (this.emojiPage == 0 && MoboConstants.f1310C) ? EmojiView.this.favoriteEmojis.size() : MoboConstants.f1310C ? EmojiData.dataColored[this.emojiPage - 1].length : EmojiData.dataColored[this.emojiPage].length;
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
            view = (ImageViewEmoji) view;
            if (view == null) {
                view = new ImageViewEmoji(EmojiView.this.getContext());
            }
            if (this.emojiPage == -1) {
                str = (String) EmojiView.this.recentEmoji.get(i);
                obj = str;
            } else if (this.emojiPage == 0 && MoboConstants.f1310C) {
                str = (String) EmojiView.this.favoriteEmojis.get(i);
                r2 = str;
            } else {
                String str2;
                if (MoboConstants.f1310C) {
                    str = EmojiData.dataColored[this.emojiPage - 1][i];
                    str2 = str;
                    obj = str;
                } else {
                    str = EmojiData.dataColored[this.emojiPage][i];
                    str2 = str;
                    r2 = str;
                }
                str = (String) EmojiView.emojiColor.get(obj);
                str = str != null ? EmojiView.addColorToCode(str2, str) : str2;
            }
            view.setImageDrawable(Emoji.getEmojiBigDrawable(str));
            view.setTag(obj);
            return view;
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (dataSetObserver != null) {
                super.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    private class EmojiPagesAdapter extends PagerAdapter implements IconTabProvider {
        private EmojiPagesAdapter() {
        }

        public void customOnDraw(Canvas canvas, int i) {
            if (((i == 6 && !MoboConstants.f1310C) || (i == 7 && MoboConstants.f1310C)) && !StickersQuery.getUnreadStickerSets().isEmpty() && EmojiView.this.dotDrawable != null) {
                int width = (canvas.getWidth() / 2) + AndroidUtilities.dp(4.0f);
                int height = (canvas.getHeight() / 2) - AndroidUtilities.dp(13.0f);
                EmojiView.this.dotDrawable.setBounds(width, height, EmojiView.this.dotDrawable.getIntrinsicWidth() + width, EmojiView.this.dotDrawable.getIntrinsicHeight() + height);
                EmojiView.this.dotDrawable.draw(canvas);
            }
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            View access$6900 = (i == 1 && MoboConstants.f1310C) ? EmojiView.this.favoriteEmojiWrap : ((i != 6 || MoboConstants.f1310C) && !(i == 7 && MoboConstants.f1310C)) ? (View) EmojiView.this.views.get(i) : EmojiView.this.stickersWrap;
            viewGroup.removeView(access$6900);
        }

        public int getCount() {
            return EmojiView.this.views.size();
        }

        public int getPageIconResId(int i) {
            if (i == 1 && MoboConstants.f1310C) {
                return C0338R.drawable.ic_tab_favorite_stickers;
            }
            if (!MoboConstants.f1310C) {
                return EmojiView.this.icons[i];
            }
            return EmojiView.this.icons[i == 0 ? 0 : i - 1];
        }

        public int getPageIconSelectedResId(int i) {
            if (i == 1 && MoboConstants.f1310C) {
                return C0338R.drawable.ic_tab_favorite_blue;
            }
            if (!MoboConstants.f1310C) {
                return EmojiView.this.icons_active[i];
            }
            return EmojiView.this.icons_active[i == 0 ? 0 : i - 1];
        }

        public int getPageIconUnSelectedResId(int i) {
            if (i == 1 && MoboConstants.f1310C) {
                return C0338R.drawable.ic_tab_favorite_stickers;
            }
            if (!MoboConstants.f1310C) {
                return EmojiView.this.icons_inactive[i];
            }
            return EmojiView.this.icons_inactive[i == 0 ? 0 : i - 1];
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View access$6900 = (i == 1 && MoboConstants.f1310C) ? EmojiView.this.favoriteEmojiWrap : ((i != 6 || MoboConstants.f1310C) && !(i == 7 && MoboConstants.f1310C)) ? (View) EmojiView.this.views.get(i) : EmojiView.this.stickersWrap;
            viewGroup.addView(access$6900);
            return access$6900;
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

    private class EmojiPopupWindow extends PopupWindow {
        private OnScrollChangedListener mSuperScrollListener;
        private ViewTreeObserver mViewTreeObserver;

        public EmojiPopupWindow() {
            init();
        }

        public EmojiPopupWindow(int i, int i2) {
            super(i, i2);
            init();
        }

        public EmojiPopupWindow(Context context) {
            super(context);
            init();
        }

        public EmojiPopupWindow(View view) {
            super(view);
            init();
        }

        public EmojiPopupWindow(View view, int i, int i2) {
            super(view, i, i2);
            init();
        }

        public EmojiPopupWindow(View view, int i, int i2, boolean z) {
            super(view, i, i2, z);
            init();
        }

        private void init() {
            if (EmojiView.superListenerField != null) {
                try {
                    this.mSuperScrollListener = (OnScrollChangedListener) EmojiView.superListenerField.get(this);
                    EmojiView.superListenerField.set(this, EmojiView.NOP);
                } catch (Exception e) {
                    this.mSuperScrollListener = null;
                }
            }
        }

        private void registerListener(View view) {
            if (this.mSuperScrollListener != null) {
                ViewTreeObserver viewTreeObserver = view.getWindowToken() != null ? view.getViewTreeObserver() : null;
                if (viewTreeObserver != this.mViewTreeObserver) {
                    if (this.mViewTreeObserver != null && this.mViewTreeObserver.isAlive()) {
                        this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                    }
                    this.mViewTreeObserver = viewTreeObserver;
                    if (viewTreeObserver != null) {
                        viewTreeObserver.addOnScrollChangedListener(this.mSuperScrollListener);
                    }
                }
            }
        }

        private void unregisterListener() {
            if (this.mSuperScrollListener != null && this.mViewTreeObserver != null) {
                if (this.mViewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = null;
            }
        }

        public void dismiss() {
            setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception e) {
            }
            unregisterListener();
        }

        public void showAsDropDown(View view, int i, int i2) {
            try {
                super.showAsDropDown(view, i, i2);
                registerListener(view);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void showAtLocation(View view, int i, int i2, int i3) {
            super.showAtLocation(view, i, i2, i3);
            unregisterListener();
        }

        public void update(View view, int i, int i2) {
            super.update(view, i, i2);
            registerListener(view);
        }

        public void update(View view, int i, int i2, int i3, int i4) {
            super.update(view, i, i2, i3, i4);
            registerListener(view);
        }
    }

    private class GifsAdapter extends Adapter {
        private Context mContext;

        public GifsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return EmojiView.this.recentGifs.size();
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Document document = (Document) EmojiView.this.recentGifs.get(i);
            if (document != null) {
                ((ContextLinkCell) viewHolder.itemView).setGif(document, false);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new ContextLinkCell(this.mContext));
        }
    }

    private class Holder extends ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    private class ImageViewEmoji extends ImageView {
        private float lastX;
        private float lastY;
        private boolean touched;
        private float touchedX;
        private float touchedY;

        /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.1 */
        class C13451 implements View.OnClickListener {
            final /* synthetic */ EmojiView val$this$0;

            C13451(EmojiView emojiView) {
                this.val$this$0 = emojiView;
            }

            public void onClick(View view) {
                ImageViewEmoji.this.sendEmoji(null);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.2 */
        class C13462 implements OnLongClickListener {
            final /* synthetic */ EmojiView val$this$0;

            C13462(EmojiView emojiView) {
                this.val$this$0 = emojiView;
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
                r1 = com.hanista.mobogram.messenger.EmojiData.emojiColoredMap;
                r1 = r1.containsKey(r0);
                if (r1 == 0) goto L_0x01e5;
            L_0x0014:
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1.touched = r2;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r4 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r4 = r4.lastX;
                r1.touchedX = r4;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r4 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r4 = r4.lastY;
                r1.touchedY = r4;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.emojiColor;
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
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r1 = r1.location;
                r10.getLocationOnScreen(r1);
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r1 = r1.emojiSize;
                r4 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r4 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r4 = r4.pickerView;
                r4 = r4.getSelection();
                r4 = r4 * r1;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r1 = r1.pickerView;
                r1 = r1.getSelection();
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
                r4 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r4 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r4 = r4.location;
                r4 = r4[r3];
                r4 = r4 - r1;
                r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r8);
                if (r4 >= r5) goto L_0x019e;
            L_0x0095:
                r4 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r4 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r4 = r4.location;
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
                r3 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r3 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r5 = r3.pickerView;
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
                r5.setEmoji(r0, r3);
                r0 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r0 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r0 = r0.pickerViewPopup;
                r0.setFocusable(r2);
                r0 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r0 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r0 = r0.pickerViewPopup;
                r3 = r10.getMeasuredHeight();
                r3 = -r3;
                r5 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r5 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r5 = r5.popupHeight;
                r3 = r3 - r5;
                r5 = r10.getMeasuredHeight();
                r6 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r6 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r6 = r6.emojiSize;
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
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r1 = r1.pickerView;
                r1.setSelection(r2);
                goto L_0x0047;
            L_0x0158:
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r1 = r1.pickerView;
                r1.setSelection(r5);
                goto L_0x0047;
            L_0x0165:
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r1 = r1.pickerView;
                r1.setSelection(r6);
                goto L_0x0047;
            L_0x0172:
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r1 = r1.pickerView;
                r4 = 4;
                r1.setSelection(r4);
                goto L_0x0047;
            L_0x0180:
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r1 = r1.pickerView;
                r4 = 5;
                r1.setSelection(r4);
                goto L_0x0047;
            L_0x018e:
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r1 = r1.pickerView;
                r1.setSelection(r3);
                goto L_0x0047;
            L_0x019b:
                r1 = r2;
                goto L_0x007c;
            L_0x019e:
                r4 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r4 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r4 = r4.location;
                r4 = r4[r3];
                r4 = r4 - r1;
                r5 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r5 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r5 = r5.popupWidth;
                r4 = r4 + r5;
                r5 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
                r5 = r5.x;
                r6 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r8);
                r5 = r5 - r6;
                if (r4 <= r5) goto L_0x00a6;
            L_0x01bd:
                r4 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r4 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r4 = r4.location;
                r4 = r4[r3];
                r4 = r4 - r1;
                r5 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r5 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r5 = r5.popupWidth;
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
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r1 = r1.pager;
                r1 = r1.getCurrentItem();
                if (r1 != 0) goto L_0x0205;
            L_0x01f3:
                r1 = com.hanista.mobogram.mobo.MoboConstants.f1310C;
                if (r1 != 0) goto L_0x0205;
            L_0x01f7:
                r0 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r0 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r0 = r0.listener;
                r0.onClearEmojiRecent();
            L_0x0202:
                r2 = r3;
                goto L_0x010e;
            L_0x0205:
                r1 = com.hanista.mobogram.mobo.MoboConstants.f1310C;
                if (r1 == 0) goto L_0x0202;
            L_0x0209:
                r1 = com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.this;
                r1 = com.hanista.mobogram.ui.Components.EmojiView.this;
                r1.addOrDeleteFavoriteEmoji(r0);
                goto L_0x010e;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.Components.EmojiView.ImageViewEmoji.2.onLongClick(android.view.View):boolean");
            }
        }

        public ImageViewEmoji(Context context) {
            super(context);
            setOnClickListener(new C13451(EmojiView.this));
            setOnLongClickListener(new C13462(EmojiView.this));
            setBackgroundResource(C0338R.drawable.list_selector);
            setScaleType(ScaleType.CENTER);
        }

        private void sendEmoji(String str) {
            String str2 = str != null ? str : (String) getTag();
            if (str == null) {
                if (EmojiView.this.pager.getCurrentItem() != 0) {
                    String str3 = (String) EmojiView.emojiColor.get(str2);
                    if (str3 != null) {
                        str2 = EmojiView.addColorToCode(str2, str3);
                    }
                }
                Integer num = (Integer) EmojiView.this.emojiUseHistory.get(str2);
                Integer valueOf = num == null ? Integer.valueOf(0) : num;
                if (valueOf.intValue() == 0 && EmojiView.this.emojiUseHistory.size() > 50) {
                    for (int size = EmojiView.this.recentEmoji.size() - 1; size >= 0; size--) {
                        EmojiView.this.emojiUseHistory.remove((String) EmojiView.this.recentEmoji.get(size));
                        EmojiView.this.recentEmoji.remove(size);
                        if (EmojiView.this.emojiUseHistory.size() <= 50) {
                            break;
                        }
                    }
                }
                EmojiView.this.emojiUseHistory.put(str2, Integer.valueOf(valueOf.intValue() + 1));
                if (EmojiView.this.pager.getCurrentItem() != 0) {
                    EmojiView.this.sortEmoji();
                }
                EmojiView.this.saveRecentEmoji();
                ((EmojiGridAdapter) EmojiView.this.adapters.get(0)).notifyDataSetChanged();
                if (EmojiView.this.listener != null) {
                    EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(str2));
                }
            } else if (EmojiView.this.listener != null) {
                EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(str));
            }
        }

        public void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int i = 5;
            boolean z = true;
            if (this.touched) {
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    if (EmojiView.this.pickerViewPopup != null && EmojiView.this.pickerViewPopup.isShowing()) {
                        String str;
                        EmojiView.this.pickerViewPopup.dismiss();
                        switch (EmojiView.this.pickerView.getSelection()) {
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
                                EmojiView.this.addOrDeleteFavoriteEmoji((String) getTag());
                                return true;
                            default:
                                str = null;
                                break;
                        }
                        String str2 = (String) getTag();
                        if (EmojiView.this.pager.getCurrentItem() != 0) {
                            if (str != null) {
                                EmojiView.emojiColor.put(str2, str);
                                str2 = EmojiView.addColorToCode(str2, str);
                            } else {
                                EmojiView.emojiColor.remove(str2);
                            }
                            setImageDrawable(Emoji.getEmojiBigDrawable(str2));
                            sendEmoji(null);
                            EmojiView.this.saveEmojiColors();
                        } else {
                            StringBuilder append = new StringBuilder().append(str2);
                            if (str == null) {
                                str = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                            sendEmoji(append.append(str).toString());
                        }
                    }
                    this.touched = false;
                    this.touchedX = -10000.0f;
                    this.touchedY = -10000.0f;
                } else if (motionEvent.getAction() == 2) {
                    if (this.touchedX == -10000.0f) {
                        z = false;
                    } else if (Math.abs(this.touchedX - motionEvent.getX()) > AndroidUtilities.getPixelsInCM(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD, true) || Math.abs(this.touchedY - motionEvent.getY()) > AndroidUtilities.getPixelsInCM(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD, false)) {
                        this.touchedX = -10000.0f;
                        this.touchedY = -10000.0f;
                        z = false;
                    }
                    if (!z) {
                        getLocationOnScreen(EmojiView.this.location);
                        float x = ((float) EmojiView.this.location[0]) + motionEvent.getX();
                        EmojiView.this.pickerView.getLocationOnScreen(EmojiView.this.location);
                        int dp = (int) ((x - ((float) (EmojiView.this.location[0] + AndroidUtilities.dp(3.0f)))) / ((float) (EmojiView.this.emojiSize + AndroidUtilities.dp(4.0f))));
                        if (dp < 0) {
                            i = 0;
                        } else {
                            if (dp <= (MoboConstants.f1310C ? 6 : 5)) {
                                i = dp;
                            } else if (MoboConstants.f1310C) {
                                i = 6;
                            }
                        }
                        EmojiView.this.pickerView.setSelection(i);
                    }
                }
            }
            this.lastX = motionEvent.getX();
            this.lastY = motionEvent.getY();
            return super.onTouchEvent(motionEvent);
        }
    }

    private class StickersGridAdapter extends Adapter {
        private HashMap<Integer, Document> cache;
        private Context context;
        private HashMap<TL_messages_stickerSet, Integer> packStartRow;
        private HashMap<Integer, TL_messages_stickerSet> rowStartPack;
        private int stickersPerRow;
        private int totalItems;

        /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.StickersGridAdapter.1 */
        class C13471 extends StickerEmojiCell {
            C13471(Context context) {
                super(context);
            }

            public void onMeasure(int i, int i2) {
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), C0700C.ENCODING_PCM_32BIT));
            }
        }

        public StickersGridAdapter(Context context) {
            this.rowStartPack = new HashMap();
            this.packStartRow = new HashMap();
            this.cache = new HashMap();
            this.context = context;
        }

        public Object getItem(int i) {
            return this.cache.get(Integer.valueOf(i));
        }

        public int getItemCount() {
            return this.totalItems != 0 ? this.totalItems + 1 : 0;
        }

        public int getItemViewType(int i) {
            return this.cache.get(Integer.valueOf(i)) != null ? 0 : 1;
        }

        public int getPositionForPack(TL_messages_stickerSet tL_messages_stickerSet) {
            return ((Integer) this.packStartRow.get(tL_messages_stickerSet)).intValue() * this.stickersPerRow;
        }

        public int getTabForPosition(int i) {
            if (this.stickersPerRow == 0) {
                int measuredWidth = EmojiView.this.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            }
            TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.rowStartPack.get(Integer.valueOf(i / this.stickersPerRow));
            return tL_messages_stickerSet == null ? EmojiView.this.recentTabBum : EmojiView.this.stickerSets.indexOf(tL_messages_stickerSet) + EmojiView.this.stickersTabOffset;
        }

        public void notifyDataSetChanged() {
            int measuredWidth = EmojiView.this.getMeasuredWidth();
            if (measuredWidth == 0) {
                measuredWidth = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            EmojiView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
            this.rowStartPack.clear();
            this.packStartRow.clear();
            this.cache.clear();
            this.totalItems = 0;
            ArrayList access$5400 = EmojiView.this.stickerSets;
            for (int i = -1; i < access$5400.size(); i++) {
                ArrayList access$6700;
                Object obj = null;
                int i2 = this.totalItems / this.stickersPerRow;
                if (i == -1) {
                    access$6700 = EmojiView.this.recentStickers;
                } else {
                    TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) access$5400.get(i);
                    access$6700 = tL_messages_stickerSet.documents;
                    this.packStartRow.put(tL_messages_stickerSet, Integer.valueOf(i2));
                }
                if (!access$6700.isEmpty()) {
                    int ceil = (int) Math.ceil((double) (((float) access$6700.size()) / ((float) this.stickersPerRow)));
                    for (int i3 = 0; i3 < access$6700.size(); i3++) {
                        this.cache.put(Integer.valueOf(this.totalItems + i3), access$6700.get(i3));
                    }
                    this.totalItems += this.stickersPerRow * ceil;
                    for (int i4 = 0; i4 < ceil; i4++) {
                        this.rowStartPack.put(Integer.valueOf(i2 + i4), obj);
                    }
                }
            }
            super.notifyDataSetChanged();
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            switch (viewHolder.getItemViewType()) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) this.cache.get(Integer.valueOf(i)), false);
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    if (i == this.totalItems) {
                        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.rowStartPack.get(Integer.valueOf((i - 1) / this.stickersPerRow));
                        if (tL_messages_stickerSet == null) {
                            ((EmptyCell) viewHolder.itemView).setHeight(1);
                            return;
                        }
                        int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil((double) (((float) tL_messages_stickerSet.documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                        EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                        if (height <= 0) {
                            height = 1;
                        }
                        emptyCell.setHeight(height);
                        return;
                    }
                    ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                default:
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new C13471(this.context);
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    view = new EmptyCell(this.context);
                    break;
            }
            return new Holder(view);
        }
    }

    private class TrendingGridAdapter extends Adapter {
        private HashMap<Integer, Object> cache;
        private Context context;
        private HashMap<Integer, StickerSetCovered> positionsToSets;
        private ArrayList<StickerSetCovered> sets;
        private int stickersPerRow;
        private int totalItems;

        /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.TrendingGridAdapter.1 */
        class C13481 extends StickerEmojiCell {
            C13481(Context context) {
                super(context);
            }

            public void onMeasure(int i, int i2) {
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), C0700C.ENCODING_PCM_32BIT));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.TrendingGridAdapter.2 */
        class C13492 implements View.OnClickListener {
            C13492() {
            }

            public void onClick(View view) {
                FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
                StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                if (!EmojiView.this.installingStickerSets.containsKey(Long.valueOf(stickerSet.set.id)) && !EmojiView.this.removingStickerSets.containsKey(Long.valueOf(stickerSet.set.id))) {
                    if (featuredStickerSetInfoCell.isInstalled()) {
                        EmojiView.this.removingStickerSets.put(Long.valueOf(stickerSet.set.id), stickerSet);
                        EmojiView.this.listener.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                    } else {
                        EmojiView.this.installingStickerSets.put(Long.valueOf(stickerSet.set.id), stickerSet);
                        EmojiView.this.listener.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
                    }
                    featuredStickerSetInfoCell.setDrawProgress(true);
                }
            }
        }

        public TrendingGridAdapter(Context context) {
            this.cache = new HashMap();
            this.sets = new ArrayList();
            this.positionsToSets = new HashMap();
            this.context = context;
        }

        public Object getItem(int i) {
            return this.cache.get(Integer.valueOf(i));
        }

        public int getItemCount() {
            return this.totalItems;
        }

        public int getItemViewType(int i) {
            Object obj = this.cache.get(Integer.valueOf(i));
            return obj != null ? obj instanceof Document ? 0 : 2 : 1;
        }

        public void notifyDataSetChanged() {
            if (!EmojiView.this.trendingLoaded) {
                int measuredWidth = EmojiView.this.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
                EmojiView.this.trendingLayoutManager.setSpanCount(this.stickersPerRow);
                this.cache.clear();
                this.positionsToSets.clear();
                this.sets.clear();
                this.totalItems = 0;
                ArrayList featuredStickerSets = StickersQuery.getFeaturedStickerSets();
                int i = 0;
                for (int i2 = 0; i2 < featuredStickerSets.size(); i2++) {
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) featuredStickerSets.get(i2);
                    if (!(StickersQuery.isStickerPackInstalled(stickerSetCovered.set.id) || (stickerSetCovered.covers.isEmpty() && stickerSetCovered.cover == null))) {
                        int ceil;
                        this.sets.add(stickerSetCovered);
                        this.positionsToSets.put(Integer.valueOf(this.totalItems), stickerSetCovered);
                        HashMap hashMap = this.cache;
                        int i3 = this.totalItems;
                        this.totalItems = i3 + 1;
                        Integer valueOf = Integer.valueOf(i3);
                        i3 = i + 1;
                        hashMap.put(valueOf, Integer.valueOf(i));
                        i = this.totalItems / this.stickersPerRow;
                        if (stickerSetCovered.covers.isEmpty()) {
                            this.cache.put(Integer.valueOf(this.totalItems), stickerSetCovered.cover);
                            i = 1;
                        } else {
                            ceil = (int) Math.ceil((double) (((float) stickerSetCovered.covers.size()) / ((float) this.stickersPerRow)));
                            for (i = 0; i < stickerSetCovered.covers.size(); i++) {
                                this.cache.put(Integer.valueOf(this.totalItems + i), stickerSetCovered.covers.get(i));
                            }
                            i = ceil;
                        }
                        for (ceil = 0; ceil < this.stickersPerRow * i; ceil++) {
                            this.positionsToSets.put(Integer.valueOf(this.totalItems + ceil), stickerSetCovered);
                        }
                        this.totalItems += i * this.stickersPerRow;
                        i = i3;
                    }
                }
                if (this.totalItems != 0) {
                    EmojiView.this.trendingLoaded = true;
                }
                super.notifyDataSetChanged();
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) this.cache.get(Integer.valueOf(i)), false);
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                case VideoPlayer.STATE_PREPARING /*2*/:
                    boolean z2;
                    ArrayList unreadStickerSets = StickersQuery.getUnreadStickerSets();
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) this.sets.get(((Integer) this.cache.get(Integer.valueOf(i))).intValue());
                    boolean z3 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSetCovered.set.id));
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) viewHolder.itemView;
                    featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, z3);
                    if (z3) {
                        StickersQuery.markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                    }
                    boolean containsKey = EmojiView.this.installingStickerSets.containsKey(Long.valueOf(stickerSetCovered.set.id));
                    z3 = EmojiView.this.removingStickerSets.containsKey(Long.valueOf(stickerSetCovered.set.id));
                    if (containsKey || z3) {
                        if (containsKey && featuredStickerSetInfoCell.isInstalled()) {
                            EmojiView.this.installingStickerSets.remove(Long.valueOf(stickerSetCovered.set.id));
                            z2 = z3;
                            z3 = false;
                            z = true;
                            featuredStickerSetInfoCell.setDrawProgress(z);
                        } else if (z3 && !featuredStickerSetInfoCell.isInstalled()) {
                            EmojiView.this.removingStickerSets.remove(Long.valueOf(stickerSetCovered.set.id));
                            z2 = false;
                            z3 = containsKey;
                            if (z3 || r0) {
                                z = true;
                            }
                            featuredStickerSetInfoCell.setDrawProgress(z);
                        }
                    }
                    z2 = z3;
                    z3 = containsKey;
                    z = true;
                    featuredStickerSetInfoCell.setDrawProgress(z);
                default:
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new C13481(this.context);
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    view = new EmptyCell(this.context);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    view = new FeaturedStickerSetInfoCell(this.context, 17);
                    ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new C13492());
                    break;
            }
            return new Holder(view);
        }
    }

    static {
        Field field = null;
        try {
            field = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        superListenerField = field;
        NOP = new C13361();
        emojiColor = new HashMap();
    }

    public EmojiView(boolean z, boolean z2, Context context) {
        int i;
        super(context);
        this.adapters = new ArrayList();
        this.emojiUseHistory = new HashMap();
        this.recentEmoji = new ArrayList();
        this.stickerSets = new ArrayList();
        this.recentGifs = new ArrayList();
        this.recentStickers = new ArrayList();
        this.icons = new int[]{C0338R.drawable.ic_emoji_recent, C0338R.drawable.ic_emoji_smile, C0338R.drawable.ic_emoji_flower, C0338R.drawable.ic_emoji_bell, C0338R.drawable.ic_emoji_car, C0338R.drawable.ic_emoji_symbol, C0338R.drawable.ic_smiles2_stickers};
        this.views = new ArrayList();
        this.emojiGrids = new ArrayList();
        this.installingStickerSets = new HashMap();
        this.removingStickerSets = new HashMap();
        this.location = new int[2];
        this.recentTabBum = -2;
        this.gifTabNum = -2;
        this.trendingTabNum = -2;
        this.currentBackgroundType = -1;
        this.favoriteEmojis = new ArrayList();
        this.favoriteEmojiList = new ArrayList();
        this.favoriteStickers = new ArrayList();
        this.favoriteTabLongClickListener = new OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (EmojiView.this.favoriteStickersEditMode) {
                    EmojiView.this.stickersGridView.setOnItemClickListener(EmojiView.this.stickersOnItemClickListener);
                    EmojiView.this.favoriteStickersEditMode = false;
                    Toast.makeText(EmojiView.this.getContext(), LocaleController.getString("FavoriteStickersEditModeExitAlert", C0338R.string.FavoriteStickersEditModeExitAlert), 0).show();
                } else {
                    EmojiView.this.favoriteStickersEditMode = true;
                    EmojiView.this.stickersGridView.setOnItemClickListener(EmojiView.this.favoriteOnItemClickListener);
                    EmojiView.this.showFavoriteEditModeDialog();
                }
                return true;
            }
        };
        this.stickerTabLongClickListener = new OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (view.getTag() != null) {
                    EmojiView.this.showStickerSetOptionsDialog(((Integer) view.getTag()).intValue());
                }
                return true;
            }
        };
        this.recentStickersTabLongClickListener = new OnLongClickListener() {
            public boolean onLongClick(View view) {
                EmojiView.this.showDeleteAllRecentStickersConfirmation();
                return true;
            }
        };
        this.gifsTabLongClickListener = new OnLongClickListener() {
            public boolean onLongClick(View view) {
                EmojiView.this.showDeleteAllGifsConfirmation();
                return true;
            }
        };
        this.emojiTabLongClickListener = new OnLongClickOnTabListener() {
            public void onLongClick(int i) {
                if (i == 0) {
                    EmojiView.this.listener.onClearEmojiRecent();
                } else if (i == 1 && MoboConstants.f1310C) {
                    EmojiView.this.showDeleteAllFavoriteEmojisConfirmation();
                }
            }
        };
        this.favoriteOnItemClickListener = new OnItemClickListener() {
            public void onItemClick(View view, int i) {
                Document sticker = ((StickerEmojiCell) view).getSticker();
                FavoriteSticker favoriteSticker = new FavoriteSticker();
                favoriteSticker.m2190a(Long.valueOf(sticker.id));
                if (EmojiView.this.favoriteStickers.contains(sticker)) {
                    EmojiView.this.showDeleteStickerFromFavoritesConfirmation(Long.valueOf(sticker.id));
                    return;
                }
                new DataBaseAccess().m846a(favoriteSticker);
                EmojiView.this.loadFavoriteStickers();
                EmojiView.this.reloadStickersAdapter();
                Toast.makeText(EmojiView.this.getContext(), LocaleController.getString("StickerAddedToFavorites", C0338R.string.StickerAddedToFavorites), 0).show();
            }
        };
        this.icons_inactive = new int[]{C0338R.drawable.ic_smiles2_recent, C0338R.drawable.ic_smiles2_smile, C0338R.drawable.ic_smiles2_nature, C0338R.drawable.ic_smiles2_food, C0338R.drawable.ic_smiles2_car, C0338R.drawable.ic_smiles2_objects, C0338R.drawable.ic_smiles2_stickers};
        this.icons_active = new int[]{C0338R.drawable.ic_smiles2_recent_active, C0338R.drawable.ic_smiles2_smile_active, C0338R.drawable.ic_smiles2_nature_active, C0338R.drawable.ic_smiles2_food_active, C0338R.drawable.ic_smiles2_car_active, C0338R.drawable.ic_smiles2_objects_active, C0338R.drawable.ic_smiles2_stickers_active};
        this.showGifs = z2;
        int i2 = AdvanceTheme.ch;
        int i3 = AdvanceTheme.ci;
        int b = i2 == -657673 ? -1907225 : AdvanceTheme.m2283b(i2, 16);
        this.dotDrawable = context.getResources().getDrawable(C0338R.drawable.bluecircle);
        if (VERSION.SDK_INT >= 21) {
            this.outlineProvider = new C13372();
        }
        int i4 = 0;
        while (true) {
            if (i4 >= (MoboConstants.f1310C ? 2 : 1) + EmojiData.dataColored.length) {
                break;
            }
            View gridView = new GridView(context);
            if (AndroidUtilities.isTablet()) {
                gridView.setColumnWidth(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW));
            } else {
                gridView.setColumnWidth(AndroidUtilities.dp(45.0f));
            }
            gridView.setNumColumns(-1);
            ListAdapter emojiGridAdapter = new EmojiGridAdapter(i4 - 1);
            AndroidUtilities.setListViewEdgeEffectColor(gridView, ThemeUtil.m2490b() ? i2 : -657673);
            gridView.setAdapter(emojiGridAdapter);
            this.adapters.add(emojiGridAdapter);
            this.emojiGrids.add(gridView);
            FrameLayout frameLayout = new FrameLayout(context);
            if (!MoboConstants.f1310C || i4 != 1) {
                frameLayout.addView(gridView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 48.0f, 0.0f, 0.0f));
            }
            this.views.add(frameLayout);
            i4++;
        }
        if (z) {
            this.stickersWrap = new FrameLayout(context);
            StickersQuery.checkStickers(0);
            StickersQuery.checkFeaturedStickers();
            this.stickersGridView = new C13383(context);
            RecyclerListView recyclerListView = this.stickersGridView;
            LayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
            this.stickersLayoutManager = gridLayoutManager;
            recyclerListView.setLayoutManager(gridLayoutManager);
            this.stickersLayoutManager.setSpanSizeLookup(new C13394());
            this.stickersGridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
            this.stickersGridView.setClipToPadding(false);
            this.views.add(this.stickersWrap);
            recyclerListView = this.stickersGridView;
            Adapter stickersGridAdapter = new StickersGridAdapter(context);
            this.stickersGridAdapter = stickersGridAdapter;
            recyclerListView.setAdapter(stickersGridAdapter);
            this.stickersGridView.setOnTouchListener(new C13405());
            this.stickersOnItemClickListener = new C13416();
            this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
            this.stickersGridView.setGlowColor(ThemeUtil.m2490b() ? AdvanceTheme.ch : -657673);
            this.stickersWrap.addView(this.stickersGridView);
            this.trendingGridView = new RecyclerListView(context);
            this.trendingGridView.setItemAnimator(null);
            this.trendingGridView.setLayoutAnimation(null);
            recyclerListView = this.trendingGridView;
            gridLayoutManager = new C13427(context, 5);
            this.trendingLayoutManager = gridLayoutManager;
            recyclerListView.setLayoutManager(gridLayoutManager);
            this.trendingLayoutManager.setSpanSizeLookup(new C13438());
            this.trendingGridView.setOnScrollListener(new C13449());
            this.trendingGridView.setClipToPadding(false);
            this.trendingGridView.setPadding(0, AndroidUtilities.dp(48.0f), 0, 0);
            recyclerListView = this.trendingGridView;
            stickersGridAdapter = new TrendingGridAdapter(context);
            this.trendingGridAdapter = stickersGridAdapter;
            recyclerListView.setAdapter(stickersGridAdapter);
            this.trendingGridView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int i) {
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) EmojiView.this.trendingGridAdapter.positionsToSets.get(Integer.valueOf(i));
                    if (stickerSetCovered != null) {
                        EmojiView.this.listener.onShowStickerSet(stickerSetCovered);
                    }
                }
            });
            this.trendingGridAdapter.notifyDataSetChanged();
            this.trendingGridView.setGlowColor(ThemeUtil.m2490b() ? AdvanceTheme.ch : -657673);
            this.trendingGridView.setVisibility(8);
            this.stickersWrap.addView(this.trendingGridView);
            if (z2) {
                this.gifsGridView = new RecyclerListView(context);
                this.gifsGridView.setClipToPadding(false);
                this.gifsGridView.setPadding(0, AndroidUtilities.dp(48.0f), 0, 0);
                recyclerListView = this.gifsGridView;
                gridLayoutManager = new AnonymousClass11(context, 100);
                this.flowLayoutManager = gridLayoutManager;
                recyclerListView.setLayoutManager(gridLayoutManager);
                this.flowLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                    public int getSpanSize(int i) {
                        return EmojiView.this.flowLayoutManager.getSpanSizeForItem(i);
                    }
                });
                this.gifsGridView.addItemDecoration(new ItemDecoration() {
                    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                        int i = 0;
                        rect.left = 0;
                        rect.top = 0;
                        rect.bottom = 0;
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                        if (!EmojiView.this.flowLayoutManager.isFirstRow(childAdapterPosition)) {
                            rect.top = AndroidUtilities.dp(2.0f);
                        }
                        if (!EmojiView.this.flowLayoutManager.isLastInRow(childAdapterPosition)) {
                            i = AndroidUtilities.dp(2.0f);
                        }
                        rect.right = i;
                    }
                });
                this.gifsGridView.setOverScrollMode(2);
                recyclerListView = this.gifsGridView;
                stickersGridAdapter = new GifsAdapter(context);
                this.gifsAdapter = stickersGridAdapter;
                recyclerListView.setAdapter(stickersGridAdapter);
                this.gifsGridView.setOnScrollListener(new OnScrollListener() {
                    public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                        EmojiView.this.checkStickersTabY(recyclerView, i2);
                    }
                });
                this.gifsGridView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(View view, int i) {
                        if (i >= 0 && i < EmojiView.this.recentGifs.size() && EmojiView.this.listener != null) {
                            EmojiView.this.listener.onGifSelected((Document) EmojiView.this.recentGifs.get(i));
                        }
                    }
                });
                this.gifsGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

                    /* renamed from: com.hanista.mobogram.ui.Components.EmojiView.16.1 */
                    class C13351 implements OnClickListener {
                        final /* synthetic */ Document val$searchImage;

                        C13351(Document document) {
                            this.val$searchImage = document;
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            StickersQuery.removeRecentGif(this.val$searchImage);
                            EmojiView.this.recentGifs = StickersQuery.getRecentGifs();
                            if (EmojiView.this.gifsAdapter != null) {
                                EmojiView.this.gifsAdapter.notifyDataSetChanged();
                            }
                            if (EmojiView.this.recentGifs.isEmpty()) {
                                EmojiView.this.updateStickerTabs();
                            }
                        }
                    }

                    public boolean onItemClick(View view, int i) {
                        if (i < 0 || i >= EmojiView.this.recentGifs.size()) {
                            return false;
                        }
                        Document document = (Document) EmojiView.this.recentGifs.get(i);
                        Builder builder = new Builder(view.getContext());
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setMessage(LocaleController.getString("DeleteGif", C0338R.string.DeleteGif));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK).toUpperCase(), new C13351(document));
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        builder.show().setCanceledOnTouchOutside(true);
                        return true;
                    }
                });
                this.gifsGridView.setVisibility(8);
                this.stickersWrap.addView(this.gifsGridView);
            }
            this.stickersEmptyView = new TextView(context);
            this.stickersEmptyView.setText(LocaleController.getString("NoStickers", C0338R.string.NoStickers));
            this.stickersEmptyView.setTextSize(1, 18.0f);
            this.stickersEmptyView.setTextColor(-7829368);
            this.stickersWrap.addView(this.stickersEmptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 48.0f, 0.0f, 0.0f));
            this.stickersGridView.setEmptyView(this.stickersEmptyView);
            this.stickersTab = new AnonymousClass17(context);
            this.stickersTab.setUnderlineHeight(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.stickersTab.setIndicatorColor(-1907225);
            this.stickersTab.setUnderlineColor(-1907225);
            this.stickersTab.setBackgroundColor(-657673);
            if (ThemeUtil.m2490b()) {
                this.stickersTab.setIndicatorColor(i3);
                this.stickersTab.setUnderlineColor(b);
                this.stickersTab.setBackgroundColor(i2);
            }
            this.stickersTab.setVisibility(4);
            addView(this.stickersTab, LayoutHelper.createFrame(-1, 48, 51));
            this.stickersTab.setTranslationX((float) AndroidUtilities.displaySize.x);
            updateStickerTabs();
            this.stickersTab.setDelegate(new ScrollSlidingTabStripDelegate() {
                public void onPageSelected(int i) {
                    int i2 = 8;
                    if (EmojiView.this.gifsGridView != null) {
                        if (i == EmojiView.this.gifTabNum + 1) {
                            if (EmojiView.this.gifsGridView.getVisibility() != 0) {
                                EmojiView.this.listener.onGifTab(true);
                                EmojiView.this.showGifTab();
                            }
                        } else if (i == EmojiView.this.trendingTabNum + 1) {
                            if (EmojiView.this.trendingGridView.getVisibility() != 0) {
                                EmojiView.this.showTrendingTab();
                            }
                        } else if (EmojiView.this.gifsGridView.getVisibility() == 0) {
                            EmojiView.this.listener.onGifTab(false);
                            EmojiView.this.gifsGridView.setVisibility(8);
                            EmojiView.this.stickersGridView.setVisibility(0);
                            r2 = EmojiView.this.stickersEmptyView;
                            if (EmojiView.this.stickersGridAdapter.getItemCount() == 0) {
                                i2 = 0;
                            }
                            r2.setVisibility(i2);
                            EmojiView.this.saveNewPage();
                        } else if (EmojiView.this.trendingGridView.getVisibility() == 0) {
                            EmojiView.this.trendingGridView.setVisibility(8);
                            EmojiView.this.stickersGridView.setVisibility(0);
                            r2 = EmojiView.this.stickersEmptyView;
                            if (EmojiView.this.stickersGridAdapter.getItemCount() == 0) {
                                i2 = 0;
                            }
                            r2.setVisibility(i2);
                            EmojiView.this.saveNewPage();
                        }
                    }
                    if (i == 0) {
                        EmojiView.this.pager.setCurrentItem(0);
                    } else if (i != EmojiView.this.gifTabNum + 1 && i != EmojiView.this.trendingTabNum + 1) {
                        if (i == EmojiView.this.recentTabBum + 1) {
                            EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
                            EmojiView.this.checkStickersTabY(null, 0);
                            EmojiView.this.stickersTab.onPageScrolled(EmojiView.this.recentTabBum + 1, (EmojiView.this.recentTabBum > 0 ? EmojiView.this.recentTabBum : EmojiView.this.stickersTabOffset) + 1);
                            return;
                        }
                        i2 = (i - 1) - EmojiView.this.stickersTabOffset;
                        if (i2 == 0 && MoboConstants.f1309B) {
                            EmojiView.this.showFavoriteStickerTabHelp();
                        }
                        if (i2 < EmojiView.this.stickerSets.size()) {
                            if (i2 >= EmojiView.this.stickerSets.size()) {
                                i2 = EmojiView.this.stickerSets.size() - 1;
                            }
                            EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(EmojiView.this.stickersGridAdapter.getPositionForPack((TL_messages_stickerSet) EmojiView.this.stickerSets.get(i2)), 0);
                            EmojiView.this.checkStickersTabY(null, 0);
                            EmojiView.this.checkScroll();
                        } else if (EmojiView.this.listener != null) {
                            EmojiView.this.listener.onStickersSettingsClick();
                        }
                    }
                }
            });
            this.stickersGridView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    EmojiView.this.checkScroll();
                    EmojiView.this.checkStickersTabY(recyclerView, i2);
                }
            });
        }
        this.pager = new AnonymousClass20(context);
        this.pager.setAdapter(new EmojiPagesAdapter());
        this.emojiTab = new AnonymousClass21(context);
        this.emojiTab.setOrientation(0);
        addView(this.emojiTab, LayoutHelper.createFrame(-1, 48.0f));
        this.pagerSlidingTabStrip = new PagerSlidingTabStrip(context);
        this.pagerSlidingTabStrip.setViewPager(this.pager);
        this.pagerSlidingTabStrip.setShouldExpand(true);
        this.pagerSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(2.0f));
        this.pagerSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.pagerSlidingTabStrip.setIndicatorColor(-13920542);
        this.pagerSlidingTabStrip.setUnderlineColor(-1907225);
        if (ThemeUtil.m2490b()) {
            this.pagerSlidingTabStrip.setIndicatorColor(i3);
            this.pagerSlidingTabStrip.setUnderlineColor(b);
        }
        this.pagerSlidingTabStrip.setOnLongClickOnTabListener(this.emojiTabLongClickListener);
        this.emojiTab.addView(this.pagerSlidingTabStrip, LayoutHelper.createLinear(0, 48, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.pagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
                EmojiView.this.onPageScrolled(i, (EmojiView.this.getMeasuredWidth() - EmojiView.this.getPaddingLeft()) - EmojiView.this.getPaddingRight(), i2);
            }

            public void onPageSelected(int i) {
                EmojiView.this.saveNewPage();
            }
        });
        View frameLayout2 = new FrameLayout(context);
        this.emojiTab.addView(frameLayout2, LayoutHelper.createLinear(52, 48));
        this.backspaceButton = new AnonymousClass23(context);
        this.backspaceButton.setImageResource(C0338R.drawable.ic_smiles_backspace);
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getResources().getDrawable(C0338R.drawable.ic_smiles_backspace);
            drawable.setColorFilter(AdvanceTheme.cj, Mode.SRC_IN);
            this.backspaceButton.setImageDrawable(drawable);
        }
        this.backspaceButton.setBackgroundResource(C0338R.drawable.ic_emoji_backspace);
        this.backspaceButton.setScaleType(ScaleType.CENTER);
        frameLayout2.addView(this.backspaceButton, LayoutHelper.createFrame(52, 48.0f));
        View view = new View(context);
        view.setBackgroundColor(-1907225);
        if (ThemeUtil.m2490b()) {
            view.setBackgroundColor(b);
            i = AdvanceTheme.cf;
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
                int i5 = AdvanceTheme.cg;
                setBackgroundDrawable(new GradientDrawable(orientation, new int[]{i2, i5}));
                this.emojiTab.setBackgroundColor(0);
                if (this.stickersTab != null) {
                    this.stickersTab.setUnderlineColor(0);
                }
                this.pagerSlidingTabStrip.setUnderlineColor(0);
                view.setBackgroundColor(0);
            }
        }
        frameLayout2.addView(view, LayoutHelper.createFrame(52, 1, 83));
        View textView = new TextView(context);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(LocaleController.getString("NoRecent", C0338R.string.NoRecent));
        textView.setTextSize(18.0f);
        textView.setTextColor(-7829368);
        textView.setGravity(17);
        textView.setClickable(false);
        textView.setFocusable(false);
        ((FrameLayout) this.views.get(0)).addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 48.0f, 0.0f, 0.0f));
        ((GridView) this.emojiGrids.get(0)).setEmptyView(textView);
        addView(this.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
        this.emojiSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        this.pickerView = new EmojiColorPickerView(context);
        View view2 = this.pickerView;
        int dp = AndroidUtilities.dp((float) (((MoboConstants.f1310C ? 6 : 5) * 4) + (((AndroidUtilities.isTablet() ? 40 : 32) * (MoboConstants.f1310C ? 7 : 6)) + 10)));
        this.popupWidth = dp;
        i = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 64.0f : 56.0f);
        this.popupHeight = i;
        this.pickerViewPopup = new EmojiPopupWindow(view2, dp, i);
        this.pickerViewPopup.setOutsideTouchable(true);
        this.pickerViewPopup.setClippingEnabled(true);
        this.pickerViewPopup.setInputMethodMode(2);
        this.pickerViewPopup.setSoftInputMode(0);
        this.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
        this.pickerViewPopup.getContentView().setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || EmojiView.this.pickerViewPopup == null || !EmojiView.this.pickerViewPopup.isShowing()) {
                    return false;
                }
                EmojiView.this.pickerViewPopup.dismiss();
                return true;
            }
        });
        this.currentPage = getContext().getSharedPreferences("emoji", 0).getInt("selected_page", 0);
        if (MoboConstants.f1309B) {
            loadFavoriteStickers();
        }
        if (MoboConstants.f1310C) {
            loadFavoriteEmojis();
        }
        loadRecents();
    }

    private static String addColorToCode(String str, String str2) {
        String str3 = null;
        if (str.endsWith("\u200d\u2640") || str.endsWith("\u200d\u2642")) {
            str3 = str.substring(str.length() - 2);
            str = str.substring(0, str.length() - 2);
        }
        String str4 = str + str2;
        return str3 != null ? str4 + str3 : str4;
    }

    private void addOrDeleteFavoriteEmoji(String str) {
        FavoriteSticker favoriteSticker;
        Iterator it = this.favoriteEmojiList.iterator();
        while (it.hasNext()) {
            favoriteSticker = (FavoriteSticker) it.next();
            if (favoriteSticker.m2194d() != null && favoriteSticker.m2194d().equals(str)) {
                showDeleteEmojiFromFavoritesConfirmation(favoriteSticker.m2189a());
                return;
            }
        }
        favoriteSticker = new FavoriteSticker();
        favoriteSticker.m2191a(str);
        new DataBaseAccess().m846a(favoriteSticker);
        loadFavoriteEmojis();
        Toast.makeText(getContext(), LocaleController.getString("EmojiAddedToFavorites", C0338R.string.EmojiAddedToFavorites), 0).show();
    }

    private void checkDocuments(boolean z) {
        int size;
        if (z) {
            size = this.recentGifs.size();
            this.recentGifs = StickersQuery.getRecentGifs();
            if (this.gifsAdapter != null) {
                this.gifsAdapter.notifyDataSetChanged();
            }
            if (size != this.recentGifs.size()) {
                updateStickerTabs();
                return;
            }
            return;
        }
        size = this.recentStickers.size();
        this.recentStickers = StickersQuery.getRecentStickers(0);
        if (this.stickersGridAdapter != null) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        if (size != this.recentStickers.size()) {
            updateStickerTabs();
        }
    }

    private void checkPanels() {
        int i = 8;
        if (this.stickersTab != null) {
            if (this.trendingTabNum == -2 && this.trendingGridView != null && this.trendingGridView.getVisibility() == 0) {
                this.gifsGridView.setVisibility(8);
                this.trendingGridView.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                this.stickersEmptyView.setVisibility(this.stickersGridAdapter.getItemCount() != 0 ? 8 : 0);
            }
            if (this.gifTabNum == -2 && this.gifsGridView != null && this.gifsGridView.getVisibility() == 0) {
                this.listener.onGifTab(false);
                this.gifsGridView.setVisibility(8);
                this.trendingGridView.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                TextView textView = this.stickersEmptyView;
                if (this.stickersGridAdapter.getItemCount() == 0) {
                    i = 0;
                }
                textView.setVisibility(i);
            } else if (this.gifTabNum == -2) {
            } else {
                if (this.gifsGridView != null && this.gifsGridView.getVisibility() == 0) {
                    this.stickersTab.onPageScrolled(this.gifTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                } else if (this.trendingGridView == null || this.trendingGridView.getVisibility() != 0) {
                    int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
                    if (findFirstVisibleItemPosition != -1) {
                        this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                    }
                } else {
                    this.stickersTab.onPageScrolled(this.trendingTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                }
            }
        }
    }

    private void checkScroll() {
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition != -1) {
            checkStickersScroll(findFirstVisibleItemPosition);
        }
    }

    private void checkStickersScroll(int i) {
        if (this.stickersGridView != null) {
            if (this.stickersGridView.getVisibility() != 0) {
                if (!(this.gifsGridView == null || this.gifsGridView.getVisibility() == 0)) {
                    this.gifsGridView.setVisibility(0);
                }
                if (this.stickersEmptyView != null && this.stickersEmptyView.getVisibility() == 0) {
                    this.stickersEmptyView.setVisibility(8);
                }
                this.stickersTab.onPageScrolled(this.gifTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                return;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(i) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        }
    }

    private void checkStickersTabY(View view, int i) {
        if (view == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            this.minusDy = 0;
            scrollSlidingTabStrip.setTranslationY((float) null);
        } else if (view.getVisibility() == 0) {
        }
    }

    private String convert(long j) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        for (int i = 0; i < 4; i++) {
            int i2 = (int) (65535 & (j >> ((3 - i) * 16)));
            if (i2 != 0) {
                str = str + ((char) i2);
            }
        }
        return str;
    }

    private TL_messages_stickerSet getFavoriteStickerSet(ArrayList<TL_messages_stickerSet> arrayList) {
        if (this.favSet == null) {
            this.favSet = new TL_messages_stickerSet();
            this.favSet.documents = this.favoriteStickers;
        }
        return this.favSet;
    }

    private void loadFavoriteEmojis() {
        if (this.favoriteEmojiWrap == null) {
            this.favoriteEmojiWrap = new FrameLayout(getContext());
            this.favoriteEmojiWrap.addView((View) this.emojiGrids.get(1), LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 48.0f, 0.0f, 0.0f));
            View textView = new TextView(getContext());
            textView.setTypeface(FontUtil.m1176a().m1161d());
            textView.setText(LocaleController.getString("NoFavoriteEmoji", C0338R.string.NoFavoriteEmoji));
            textView.setTextSize(16.0f);
            textView.setTextColor(-7829368);
            textView.setGravity(17);
            this.favoriteEmojiWrap.addView(textView);
            ((GridView) this.emojiGrids.get(1)).setEmptyView(textView);
        }
        this.favoriteEmojis.clear();
        this.favoriteEmojiList.clear();
        for (FavoriteSticker favoriteSticker : new DataBaseAccess().m900m()) {
            if (favoriteSticker.m2194d() != null) {
                this.favoriteEmojis.add(favoriteSticker.m2194d());
                this.favoriteEmojiList.add(favoriteSticker);
            }
        }
        ListAdapter emojiGridAdapter = new EmojiGridAdapter(0);
        ((GridView) this.emojiGrids.get(1)).setAdapter(emojiGridAdapter);
        this.adapters.set(1, emojiGridAdapter);
    }

    private void loadFavoriteStickers() {
        this.favoriteStickers.clear();
        for (FavoriteSticker c : new DataBaseAccess().m898l()) {
            Document stickerById = StickersQuery.getStickerById(c.m2193c().longValue());
            if (stickerById != null) {
                this.favoriteStickers.add(stickerById);
            }
        }
    }

    private void onPageScrolled(int i, int i2, int i3) {
        int i4 = 6;
        boolean z = true;
        int i5 = 0;
        if (this.stickersTab != null) {
            int i6;
            if (i2 == 0) {
                i2 = AndroidUtilities.displaySize.x;
            }
            if (i == (MoboConstants.f1310C ? 6 : 5)) {
                i4 = -i3;
                if (this.listener != null) {
                    Listener listener = this.listener;
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
                    if (this.listener != null) {
                        this.listener.onStickersTab(true);
                        i6 = i4;
                    }
                    i6 = i4;
                } else {
                    if (this.listener != null) {
                        this.listener.onStickersTab(false);
                    }
                    i6 = 0;
                }
            }
            if (this.emojiTab.getTranslationX() != ((float) i6)) {
                this.emojiTab.setTranslationX((float) i6);
                this.stickersTab.setTranslationX((float) (i2 + i6));
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
                if (i6 >= 0) {
                    i5 = 4;
                }
                scrollSlidingTabStrip.setVisibility(i5);
            }
        }
    }

    private void postBackspaceRunnable(int i) {
        AndroidUtilities.runOnUIThread(new AnonymousClass25(i), (long) i);
    }

    private void processSelectionOption(int i, TL_messages_stickerSet tL_messages_stickerSet) {
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
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(Locale.US, "https://telegram.me/addstickers/%s", new Object[]{tL_messages_stickerSet.set.short_name})));
                Toast.makeText(getContext(), LocaleController.getString("LinkCopied", C0338R.string.LinkCopied), 0).show();
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
        }
    }

    private void reloadStickersAdapter() {
        if (this.stickersGridAdapter != null) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        if (this.trendingGridAdapter != null) {
            this.trendingGridAdapter.notifyDataSetChanged();
        }
        if (StickerPreviewViewer.getInstance().isVisible()) {
            StickerPreviewViewer.getInstance().close();
        }
        StickerPreviewViewer.getInstance().reset();
    }

    private void saveEmojiColors() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("emoji", 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry entry : emojiColor.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append((String) entry.getValue());
        }
        sharedPreferences.edit().putString(TtmlNode.ATTR_TTS_COLOR, stringBuilder.toString()).commit();
    }

    private void saveNewPage() {
        int i = ((this.pager.getCurrentItem() != 6 || MoboConstants.f1310C) && !(MoboConstants.f1310C && this.pager.getCurrentItem() == 7)) ? 0 : (this.gifsGridView == null || this.gifsGridView.getVisibility() != 0) ? 1 : 2;
        if (this.currentPage != i) {
            this.currentPage = i;
            getContext().getSharedPreferences("emoji", 0).edit().putInt("selected_page", i).commit();
        }
    }

    private void saveRecentEmoji() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("emoji", 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry entry : this.emojiUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        sharedPreferences.edit().putString("emojis2", stringBuilder.toString()).commit();
    }

    private void showDeleteAllFavoriteEmojisConfirmation() {
        Builder builder = new Builder(getContext());
        builder.setMessage(LocaleController.formatString("DeleteAllFavoriteEmojis", C0338R.string.DeleteAllFavoriteEmojis, new Object[0]));
        builder.setTitle(LocaleController.getString("FavoriteEmojis", C0338R.string.FavoriteEmojis));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                new DataBaseAccess().m902n();
                EmojiView.this.loadFavoriteEmojis();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        builder.create().show();
    }

    private void showDeleteAllGifsConfirmation() {
        Builder builder = new Builder(getContext());
        builder.setMessage(LocaleController.formatString("DeleteAllGifs", C0338R.string.DeleteAllGifs, new Object[0]));
        builder.setTitle(LocaleController.getString("Delete", C0338R.string.Delete));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Iterator it = EmojiView.this.recentGifs.iterator();
                while (it.hasNext()) {
                    StickersQuery.removeRecentGif((Document) it.next());
                }
                EmojiView.this.recentGifs = StickersQuery.getRecentGifs();
                if (EmojiView.this.gifsAdapter != null) {
                    EmojiView.this.gifsAdapter.notifyDataSetChanged();
                }
                if (EmojiView.this.recentGifs.isEmpty()) {
                    EmojiView.this.updateStickerTabs();
                }
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        builder.create().show();
    }

    private void showDeleteAllRecentStickersConfirmation() {
        Builder builder = new Builder(getContext());
        builder.setMessage(LocaleController.formatString("DeleteAllRecentStickers", C0338R.string.DeleteAllRecentStickers, new Object[0]));
        builder.setTitle(LocaleController.getString("Delete", C0338R.string.Delete));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Iterator it = EmojiView.this.recentStickers.iterator();
                while (it.hasNext()) {
                    StickersQuery.removeRecentSticker((Document) it.next());
                }
                EmojiView.this.recentStickers = StickersQuery.getRecentStickers(0);
                EmojiView.this.reloadStickersAdapter();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        builder.create().show();
    }

    private void showDeleteEmojiFromFavoritesConfirmation(Long l) {
        Builder builder = new Builder(getContext());
        builder.setMessage(LocaleController.formatString("EmojiRemoveFromFavoritesAlert", C0338R.string.EmojiRemoveFromFavoritesAlert, new Object[0]));
        builder.setTitle(LocaleController.getString("FavoriteEmojis", C0338R.string.FavoriteEmojis));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass37(l));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        builder.create().show();
    }

    private void showDeleteStickerFromFavoritesConfirmation(Long l) {
        Builder builder = new Builder(getContext());
        builder.setMessage(LocaleController.formatString("StickerRemoveFromFavoritesAlert", C0338R.string.StickerRemoveFromFavoritesAlert, new Object[0]));
        builder.setTitle(LocaleController.getString("FavoriteStickers", C0338R.string.FavoriteStickers));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass36(l));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        builder.create().show();
    }

    private void showFavoriteEditModeDialog() {
        Builder builder = new Builder(getContext());
        builder.setTitle(LocaleController.getString("FavoriteStickers", C0338R.string.FavoriteStickers)).setMessage(LocaleController.getString("FavoriteStickersEditModeAlert", C0338R.string.FavoriteStickersEditModeAlert));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
        builder.create().show();
    }

    private void showFavoriteStickerTabHelp() {
        SettingManager settingManager = new SettingManager();
        if (!settingManager.m944b("favStickerHelpDisplayed")) {
            settingManager.m943a("favStickerHelpDisplayed", true);
            Builder builder = new Builder(getContext());
            builder.setTitle(LocaleController.getString("Help", C0338R.string.Help)).setMessage(LocaleController.getString("FavoriteStickersTabHelp", C0338R.string.FavoriteStickersTabHelp));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private void showGifTab() {
        this.gifsGridView.setVisibility(0);
        this.stickersGridView.setVisibility(8);
        this.stickersEmptyView.setVisibility(8);
        this.trendingGridView.setVisibility(8);
        this.stickersTab.onPageScrolled(this.gifTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        saveNewPage();
    }

    private void showStickerSetOptionsDialog(int i) {
        int[] iArr;
        CharSequence[] charSequenceArr;
        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.stickerSets.get(i);
        Builder builder = new Builder(getContext());
        builder.setTitle(tL_messages_stickerSet.set.title);
        if (tL_messages_stickerSet.set.official) {
            iArr = new int[]{0};
            charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", C0338R.string.StickersHide)};
        } else {
            iArr = new int[]{0, 1, 2, 3};
            charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", C0338R.string.StickersHide), LocaleController.getString("StickersRemove", C0338R.string.StickersRemove), LocaleController.getString("StickersShare", C0338R.string.StickersShare), LocaleController.getString("StickersCopy", C0338R.string.StickersCopy)};
        }
        builder.setItems(charSequenceArr, new AnonymousClass28(iArr, tL_messages_stickerSet));
        builder.show();
    }

    private void showTrendingTab() {
        this.trendingGridView.setVisibility(0);
        this.stickersGridView.setVisibility(8);
        this.stickersEmptyView.setVisibility(8);
        this.gifsGridView.setVisibility(8);
        this.stickersTab.onPageScrolled(this.trendingTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        saveNewPage();
    }

    private void sortEmoji() {
        this.recentEmoji.clear();
        for (Entry key : this.emojiUseHistory.entrySet()) {
            this.recentEmoji.add(key.getKey());
        }
        Collections.sort(this.recentEmoji, new Comparator<String>() {
            public int compare(String str, String str2) {
                Integer num = (Integer) EmojiView.this.emojiUseHistory.get(str);
                Integer num2 = (Integer) EmojiView.this.emojiUseHistory.get(str2);
                if (num == null) {
                    num = Integer.valueOf(0);
                }
                if (num2 == null) {
                    num2 = Integer.valueOf(0);
                }
                return num.intValue() > num2.intValue() ? -1 : num.intValue() < num2.intValue() ? 1 : 0;
            }
        });
        while (this.recentEmoji.size() > 50) {
            this.recentEmoji.remove(this.recentEmoji.size() - 1);
        }
    }

    private void updateStickerTabs() {
        if (this.stickersTab != null) {
            int i;
            this.recentTabBum = -2;
            this.gifTabNum = -2;
            this.trendingTabNum = -2;
            this.stickersTabOffset = 0;
            int currentPosition = this.stickersTab.getCurrentPosition();
            this.stickersTab.removeTabs();
            this.stickersTab.addIconTab(C0338R.drawable.ic_smiles2_smile);
            if (this.showGifs && !this.recentGifs.isEmpty()) {
                this.stickersTab.addIconTab(C0338R.drawable.ic_smiles_gif, this.gifsTabLongClickListener);
                this.gifTabNum = this.stickersTabOffset;
                this.stickersTabOffset++;
            }
            ArrayList unreadStickerSets = StickersQuery.getUnreadStickerSets();
            if (!(this.trendingGridAdapter == null || this.trendingGridAdapter.getItemCount() == 0 || unreadStickerSets.isEmpty())) {
                TextView addIconTabWithCounter = this.stickersTab.addIconTabWithCounter(C0338R.drawable.ic_smiles_trend);
                this.trendingTabNum = this.stickersTabOffset;
                this.stickersTabOffset++;
                addIconTabWithCounter.setText(String.format("%d", new Object[]{Integer.valueOf(unreadStickerSets.size())}));
            }
            if (!this.recentStickers.isEmpty()) {
                this.recentTabBum = this.stickersTabOffset;
                this.stickersTabOffset++;
                this.stickersTab.addIconTab(C0338R.drawable.ic_smiles2_recent, this.recentStickersTabLongClickListener);
            }
            this.stickerSets.clear();
            ArrayList stickerSets = StickersQuery.getStickerSets(0);
            if (MoboConstants.f1309B) {
                this.stickerSets.add(getFavoriteStickerSet(stickerSets));
            }
            for (i = 0; i < stickerSets.size(); i++) {
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(i);
                if (!(tL_messages_stickerSet.set.archived || tL_messages_stickerSet.documents == null || tL_messages_stickerSet.documents.isEmpty())) {
                    this.stickerSets.add(tL_messages_stickerSet);
                }
            }
            for (i = 0; i < this.stickerSets.size(); i++) {
                if (i == 0 && MoboConstants.f1309B) {
                    this.stickersTab.addIconTab(C0338R.drawable.ic_tab_favorite_stickers, this.favoriteTabLongClickListener);
                } else {
                    this.stickersTab.addStickerTab((Document) ((TL_messages_stickerSet) this.stickerSets.get(i)).documents.get(0), Integer.valueOf(i), this.stickerTabLongClickListener);
                }
            }
            if (!(this.trendingGridAdapter == null || this.trendingGridAdapter.getItemCount() == 0 || !unreadStickerSets.isEmpty())) {
                this.trendingTabNum = this.stickersTabOffset + this.stickerSets.size();
                this.stickersTab.addIconTab(C0338R.drawable.ic_smiles_trend);
            }
            this.stickersTab.addIconTab(C0338R.drawable.ic_smiles_settings);
            this.stickersTab.updateTabStyles();
            if (currentPosition != 0) {
                this.stickersTab.onPageScrolled(currentPosition, currentPosition);
            }
            if (this.switchToGifTab && this.gifTabNum >= 0 && this.gifsGridView.getVisibility() != 0) {
                showGifTab();
                this.switchToGifTab = false;
            }
            checkPanels();
        }
    }

    private void updateVisibleTrendingSets() {
        int findFirstVisibleItemPosition = this.trendingLayoutManager.findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition != -1) {
            int findLastVisibleItemPosition = this.trendingLayoutManager.findLastVisibleItemPosition();
            if (findLastVisibleItemPosition != -1) {
                this.trendingGridAdapter.notifyItemRangeChanged(findFirstVisibleItemPosition, (findLastVisibleItemPosition - findFirstVisibleItemPosition) + 1);
            }
        }
    }

    public void addRecentGif(Document document) {
        if (document != null) {
            StickersQuery.addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
            boolean isEmpty = this.recentGifs.isEmpty();
            this.recentGifs = StickersQuery.getRecentGifs();
            if (this.gifsAdapter != null) {
                this.gifsAdapter.notifyDataSetChanged();
            }
            if (isEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void addRecentSticker(Document document) {
        if (document != null) {
            StickersQuery.addRecentSticker(0, document, (int) (System.currentTimeMillis() / 1000));
            boolean isEmpty = this.recentStickers.isEmpty();
            this.recentStickers = StickersQuery.getRecentStickers(0);
            if (this.stickersGridAdapter != null) {
                this.stickersGridAdapter.notifyDataSetChanged();
            }
            if (isEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void clearRecentEmoji() {
        getContext().getSharedPreferences("emoji", 0).edit().putBoolean("filled_default", true).commit();
        this.emojiUseHistory.clear();
        this.recentEmoji.clear();
        saveRecentEmoji();
        ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
    }

    public void didReceivedNotification(int i, Object... objArr) {
        int i2 = 0;
        if (i == NotificationCenter.stickersDidLoaded) {
            if (((Integer) objArr[0]).intValue() == 0) {
                if (this.trendingGridAdapter != null) {
                    if (this.trendingLoaded) {
                        updateVisibleTrendingSets();
                    } else {
                        this.trendingGridAdapter.notifyDataSetChanged();
                    }
                }
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoaded) {
            boolean booleanValue = ((Boolean) objArr[0]).booleanValue();
            if (booleanValue || ((Integer) objArr[1]).intValue() == 0) {
                checkDocuments(booleanValue);
            }
        } else if (i == NotificationCenter.featuredStickersDidLoaded) {
            if (this.trendingGridAdapter != null) {
                if (this.trendingLoaded) {
                    updateVisibleTrendingSets();
                } else {
                    this.trendingGridAdapter.notifyDataSetChanged();
                }
            }
            if (this.pagerSlidingTabStrip != null) {
                int childCount = this.pagerSlidingTabStrip.getChildCount();
                while (i2 < childCount) {
                    this.pagerSlidingTabStrip.getChildAt(i2).invalidate();
                    i2++;
                }
            }
            updateStickerTabs();
        }
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void invalidateViews() {
        for (int i = 0; i < this.emojiGrids.size(); i++) {
            ((GridView) this.emojiGrids.get(i)).invalidateViews();
        }
    }

    public void loadRecents() {
        String string;
        int i;
        String[] strArr;
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("emoji", 0);
        try {
            this.emojiUseHistory.clear();
            if (sharedPreferences.contains("emojis")) {
                string = sharedPreferences.getString("emojis", TtmlNode.ANONYMOUS_REGION_ID);
                if (string != null && string.length() > 0) {
                    for (String string2 : string2.split(",")) {
                        String[] split = string2.split("=");
                        long longValue = Utilities.parseLong(split[0]).longValue();
                        string2 = TtmlNode.ANONYMOUS_REGION_ID;
                        for (int i2 = 0; i2 < 4; i2++) {
                            string2 = String.valueOf((char) ((int) longValue)) + string2;
                            longValue >>= 16;
                            if (longValue == 0) {
                                break;
                            }
                        }
                        if (string2.length() > 0) {
                            this.emojiUseHistory.put(string2, Utilities.parseInt(split[1]));
                        }
                    }
                }
                sharedPreferences.edit().remove("emojis").commit();
                saveRecentEmoji();
            } else {
                string2 = sharedPreferences.getString("emojis2", TtmlNode.ANONYMOUS_REGION_ID);
                if (string2 != null && string2.length() > 0) {
                    for (String split2 : string2.split(",")) {
                        String[] split3 = split2.split("=");
                        this.emojiUseHistory.put(split3[0], Utilities.parseInt(split3[1]));
                    }
                }
            }
            if (this.emojiUseHistory.isEmpty() && !sharedPreferences.getBoolean("filled_default", false)) {
                strArr = new String[]{"\ud83d\ude02", "\ud83d\ude18", "\u2764", "\ud83d\ude0d", "\ud83d\ude0a", "\ud83d\ude01", "\ud83d\udc4d", "\u263a", "\ud83d\ude14", "\ud83d\ude04", "\ud83d\ude2d", "\ud83d\udc8b", "\ud83d\ude12", "\ud83d\ude33", "\ud83d\ude1c", "\ud83d\ude48", "\ud83d\ude09", "\ud83d\ude03", "\ud83d\ude22", "\ud83d\ude1d", "\ud83d\ude31", "\ud83d\ude21", "\ud83d\ude0f", "\ud83d\ude1e", "\ud83d\ude05", "\ud83d\ude1a", "\ud83d\ude4a", "\ud83d\ude0c", "\ud83d\ude00", "\ud83d\ude0b", "\ud83d\ude06", "\ud83d\udc4c", "\ud83d\ude10", "\ud83d\ude15"};
                for (i = 0; i < strArr.length; i++) {
                    this.emojiUseHistory.put(strArr[i], Integer.valueOf(strArr.length - i));
                }
                sharedPreferences.edit().putBoolean("filled_default", true).commit();
                saveRecentEmoji();
            }
            sortEmoji();
            ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        try {
            string2 = sharedPreferences.getString(TtmlNode.ATTR_TTS_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
            if (string2 != null && string2.length() > 0) {
                strArr = string2.split(",");
                for (String split4 : strArr) {
                    String[] split5 = split4.split("=");
                    emojiColor.put(split5[0], split5[1]);
                }
            }
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    EmojiView.this.updateStickerTabs();
                    EmojiView.this.reloadStickersAdapter();
                }
            });
        }
    }

    public void onDestroy() {
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentDocumentsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.pickerViewPopup != null && this.pickerViewPopup.isShowing()) {
            this.pickerViewPopup.dismiss();
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.lastNotifyWidth != i3 - i) {
            this.lastNotifyWidth = i3 - i;
            reloadStickersAdapter();
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    public void onMeasure(int i, int i2) {
        LayoutParams layoutParams;
        int i3 = -657673;
        this.isLayout = true;
        if (AndroidUtilities.isInMultiwindow) {
            if (this.currentBackgroundType != 1) {
                if (VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation((float) AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(C0338R.drawable.smiles_popup);
                this.emojiTab.setBackgroundDrawable(null);
                this.currentBackgroundType = 1;
            }
        } else if (this.currentBackgroundType != 0) {
            if (VERSION.SDK_INT >= 21) {
                setOutlineProvider(null);
                setClipToOutline(false);
                setElevation(0.0f);
            }
            setBackgroundColor(ThemeUtil.m2490b() ? AdvanceTheme.ch : -657673);
            LinearLayout linearLayout = this.emojiTab;
            if (ThemeUtil.m2490b()) {
                i3 = AdvanceTheme.ch;
            }
            linearLayout.setBackgroundColor(i3);
            this.currentBackgroundType = 0;
        }
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.emojiTab.getLayoutParams();
        layoutParams2.width = MeasureSpec.getSize(i);
        if (this.stickersTab != null) {
            layoutParams = (FrameLayout.LayoutParams) this.stickersTab.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = layoutParams2.width;
            }
        } else {
            layoutParams = null;
        }
        if (layoutParams2.width != this.oldWidth) {
            if (!(this.stickersTab == null || layoutParams == null)) {
                onPageScrolled(this.pager.getCurrentItem(), (layoutParams2.width - getPaddingLeft()) - getPaddingRight(), 0);
                this.stickersTab.setLayoutParams(layoutParams);
            }
            this.emojiTab.setLayoutParams(layoutParams2);
            this.oldWidth = layoutParams2.width;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(layoutParams2.width, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i2), C0700C.ENCODING_PCM_32BIT));
        this.isLayout = false;
    }

    public void onOpen(boolean z) {
        boolean z2 = true;
        int i = 7;
        if (this.stickersTab == null) {
            return;
        }
        if (this.currentPage == 0 || z) {
            if ((this.pager.getCurrentItem() == 6 && !MoboConstants.f1310C) || (this.pager.getCurrentItem() == 7 && MoboConstants.f1310C)) {
                ViewPager viewPager = this.pager;
                if (z) {
                    z2 = false;
                }
                viewPager.setCurrentItem(0, z2);
            }
        } else if (this.currentPage == 1) {
            if (!(this.pager.getCurrentItem() == 6 || MoboConstants.f1310C) || (this.pager.getCurrentItem() != 7 && MoboConstants.f1310C)) {
                this.pager.setCurrentItem(MoboConstants.f1310C ? 7 : 6);
            }
            if (this.stickersTab.getCurrentPosition() != this.gifTabNum + 1) {
                return;
            }
            if (this.recentTabBum >= 0) {
                this.stickersTab.selectTab(this.recentTabBum + 1);
            } else if (this.gifTabNum >= 0) {
                this.stickersTab.selectTab(this.gifTabNum + 2);
            } else {
                this.stickersTab.selectTab(1);
            }
        } else if (this.currentPage == 2) {
            if (!(this.pager.getCurrentItem() == 6 || MoboConstants.f1310C) || (this.pager.getCurrentItem() != 7 && MoboConstants.f1310C)) {
                ViewPager viewPager2 = this.pager;
                if (!MoboConstants.f1310C) {
                    i = 6;
                }
                viewPager2.setCurrentItem(i);
            }
            if (this.stickersTab.getCurrentPosition() == this.gifTabNum + 1) {
                return;
            }
            if (this.gifTabNum < 0 || this.recentGifs.isEmpty()) {
                this.switchToGifTab = true;
            } else {
                this.stickersTab.selectTab(this.gifTabNum + 1);
            }
        }
    }

    public void requestLayout() {
        if (!this.isLayout) {
            super.requestLayout();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 8) {
            sortEmoji();
            ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
            if (this.stickersGridAdapter != null) {
                NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
                NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentDocumentsDidLoaded);
                updateStickerTabs();
                reloadStickersAdapter();
                if (!(this.gifsGridView == null || this.gifsGridView.getVisibility() != 0 || this.listener == null)) {
                    Listener listener = this.listener;
                    boolean z = this.pager != null && this.pager.getCurrentItem() >= 6;
                    listener.onGifTab(z);
                }
            }
            if (this.trendingGridAdapter != null) {
                this.trendingLoaded = false;
                this.trendingGridAdapter.notifyDataSetChanged();
            }
            checkDocuments(true);
            checkDocuments(false);
            StickersQuery.loadRecents(0, true, true);
            StickersQuery.loadRecents(0, false, true);
        }
    }

    public void switchToGifRecent() {
        if (this.gifTabNum < 0 || this.recentGifs.isEmpty()) {
            this.switchToGifTab = true;
        } else {
            this.stickersTab.selectTab(this.gifTabNum + 1);
        }
        this.pager.setCurrentItem(MoboConstants.f1310C ? 7 : 6);
    }
}
