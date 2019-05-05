package com.hanista.mobogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
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
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.InputStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.InputStickeredMedia;
import com.hanista.mobogram.tgnet.TLRPC.Photo;
import com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeSticker;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetID;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickeredMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getAttachedStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_installStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_stickerSet;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_stickerSetInstallResultArchive;
import com.hanista.mobogram.tgnet.TLRPC.Vector;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.FeaturedStickerSetInfoCell;
import com.hanista.mobogram.ui.Cells.StickerEmojiCell;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.StickerPreviewViewer;
import java.util.ArrayList;
import java.util.HashMap;

public class StickersAlert extends BottomSheet implements NotificationCenterDelegate {
    private GridAdapter adapter;
    private StickersAlertDelegate delegate;
    private FrameLayout emptyView;
    private RecyclerListView gridView;
    private boolean ignoreLayout;
    private InputStickerSet inputStickerSet;
    private StickersAlertInstallDelegate installDelegate;
    private GridLayoutManager layoutManager;
    private Activity parentActivity;
    private BaseFragment parentFragment;
    private PickerBottomLayout pickerBottomLayout;
    private TextView previewSendButton;
    private View previewSendButtonShadow;
    private int reqId;
    private int scrollOffsetY;
    private Document selectedSticker;
    private View[] shadow;
    private AnimatorSet[] shadowAnimation;
    private Drawable shadowDrawable;
    private boolean showEmoji;
    private TextView stickerEmojiTextView;
    private BackupImageView stickerImageView;
    private FrameLayout stickerPreviewLayout;
    private TL_messages_stickerSet stickerSet;
    private ArrayList<StickerSetCovered> stickerSetCovereds;
    private OnItemClickListener stickersOnItemClickListener;
    private TextView titleTextView;

    public interface StickersAlertInstallDelegate {
        void onStickerSetInstalled();

        void onStickerSetUninstalled();
    }

    public interface StickersAlertDelegate {
        void onStickerSelected(Document document);
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.11 */
    class AnonymousClass11 extends FrameLayout {
        AnonymousClass11(Context context) {
            super(context);
        }

        public void requestLayout() {
            if (!StickersAlert.this.ignoreLayout) {
                super.requestLayout();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.1 */
    class C14831 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.1.1 */
        class C14801 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C14801(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                StickersAlert.this.reqId = 0;
                if (this.val$error == null) {
                    Vector vector = (Vector) this.val$response;
                    if (vector.objects.isEmpty()) {
                        StickersAlert.this.dismiss();
                        return;
                    } else if (vector.objects.size() == 1) {
                        StickerSetCovered stickerSetCovered = (StickerSetCovered) vector.objects.get(0);
                        StickersAlert.this.inputStickerSet = new TL_inputStickerSetID();
                        StickersAlert.this.inputStickerSet.id = stickerSetCovered.set.id;
                        StickersAlert.this.inputStickerSet.access_hash = stickerSetCovered.set.access_hash;
                        StickersAlert.this.loadStickerSet();
                        return;
                    } else {
                        StickersAlert.this.stickerSetCovereds = new ArrayList();
                        for (int i = 0; i < vector.objects.size(); i++) {
                            StickersAlert.this.stickerSetCovereds.add((StickerSetCovered) vector.objects.get(i));
                        }
                        StickersAlert.this.gridView.setLayoutParams(LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                        StickersAlert.this.titleTextView.setVisibility(8);
                        StickersAlert.this.shadow[0].setVisibility(8);
                        StickersAlert.this.adapter.notifyDataSetChanged();
                        return;
                    }
                }
                Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred) + "\n" + this.val$error.text, 0).show();
                StickersAlert.this.dismiss();
            }
        }

        C14831() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C14801(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.21 */
    class AnonymousClass21 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$num;
        final /* synthetic */ boolean val$show;

        AnonymousClass21(int i, boolean z) {
            this.val$num = i;
            this.val$show = z;
        }

        public void onAnimationCancel(Animator animator) {
            if (StickersAlert.this.shadowAnimation[this.val$num] != null && StickersAlert.this.shadowAnimation[this.val$num].equals(animator)) {
                StickersAlert.this.shadowAnimation[this.val$num] = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (StickersAlert.this.shadowAnimation[this.val$num] != null && StickersAlert.this.shadowAnimation[this.val$num].equals(animator)) {
                if (!this.val$show) {
                    StickersAlert.this.shadow[this.val$num].setVisibility(4);
                }
                StickersAlert.this.shadowAnimation[this.val$num] = null;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.2 */
    class C14852 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.2.1 */
        class C14841 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C14841(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                StickersAlert.this.reqId = 0;
                if (this.val$error == null) {
                    StickersAlert.this.stickerSet = (TL_messages_stickerSet) this.val$response;
                    StickersAlert.this.showEmoji = !StickersAlert.this.stickerSet.set.masks;
                    StickersAlert.this.updateSendButton();
                    StickersAlert.this.updateFields();
                    StickersAlert.this.adapter.notifyDataSetChanged();
                    return;
                }
                Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddStickersNotFound", C0338R.string.AddStickersNotFound), 0).show();
                StickersAlert.this.dismiss();
            }
        }

        C14852() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C14841(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.3 */
    class C14863 extends FrameLayout {
        private int lastNotifyWidth;

        C14863(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            StickersAlert.this.shadowDrawable.setBounds(0, StickersAlert.this.scrollOffsetY - StickersAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
            StickersAlert.this.shadowDrawable.draw(canvas);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0 || StickersAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) StickersAlert.this.scrollOffsetY)) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            StickersAlert.this.dismiss();
            return true;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (this.lastNotifyWidth != i3 - i) {
                this.lastNotifyWidth = i3 - i;
                if (!(StickersAlert.this.adapter == null || StickersAlert.this.stickerSetCovereds == null)) {
                    StickersAlert.this.adapter.notifyDataSetChanged();
                }
            }
            super.onLayout(z, i, i2, i3, i4);
            StickersAlert.this.updateLayout();
        }

        protected void onMeasure(int i, int i2) {
            int dp;
            int size = MeasureSpec.getSize(i2);
            if (VERSION.SDK_INT >= 21) {
                size -= AndroidUtilities.statusBarHeight;
            }
            if (StickersAlert.this.stickerSetCovereds != null) {
                dp = (AndroidUtilities.dp(56.0f) + (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW) * StickersAlert.this.stickerSetCovereds.size())) + (StickersAlert.this.adapter.stickersRowCount * AndroidUtilities.dp(82.0f));
            } else {
                dp = ((Math.max(3, StickersAlert.this.stickerSet != null ? (int) Math.ceil((double) (((float) StickersAlert.this.stickerSet.documents.size()) / 5.0f)) : 0) * AndroidUtilities.dp(82.0f)) + AndroidUtilities.dp(96.0f)) + StickersAlert.backgroundPaddingTop;
            }
            int i3 = ((double) dp) < ((double) (size / 5)) * 3.2d ? 0 : (size / 5) * 2;
            if (i3 != 0 && dp < size) {
                i3 -= size - dp;
            }
            if (i3 == 0) {
                i3 = StickersAlert.backgroundPaddingTop;
            }
            if (StickersAlert.this.stickerSetCovereds != null) {
                i3 += AndroidUtilities.dp(8.0f);
            }
            if (StickersAlert.this.gridView.getPaddingTop() != i3) {
                StickersAlert.this.ignoreLayout = true;
                StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0f), i3, AndroidUtilities.dp(10.0f), 0);
                StickersAlert.this.emptyView.setPadding(0, i3, 0, 0);
                StickersAlert.this.ignoreLayout = false;
            }
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(dp, size), C0700C.ENCODING_PCM_32BIT));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return !StickersAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
        }

        public void requestLayout() {
            if (!StickersAlert.this.ignoreLayout) {
                super.requestLayout();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.4 */
    class C14874 implements OnTouchListener {
        C14874() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.5 */
    class C14885 extends RecyclerListView {
        C14885(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return super.onInterceptTouchEvent(motionEvent) || StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, StickersAlert.this.gridView, 0);
        }

        public void requestLayout() {
            if (!StickersAlert.this.ignoreLayout) {
                super.requestLayout();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.6 */
    class C14896 extends SpanSizeLookup {
        C14896() {
        }

        public int getSpanSize(int i) {
            return ((StickersAlert.this.stickerSetCovereds == null || !(StickersAlert.this.adapter.cache.get(Integer.valueOf(i)) instanceof Integer)) && i != StickersAlert.this.adapter.totalItems) ? 1 : StickersAlert.this.adapter.stickersPerRow;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.7 */
    class C14907 extends ItemDecoration {
        C14907() {
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
            rect.left = 0;
            rect.right = 0;
            rect.bottom = 0;
            rect.top = 0;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.8 */
    class C14918 implements OnTouchListener {
        C14918() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return StickerPreviewViewer.getInstance().onTouch(motionEvent, StickersAlert.this.gridView, 0, StickersAlert.this.stickersOnItemClickListener);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.9 */
    class C14929 extends OnScrollListener {
        C14929() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            StickersAlert.this.updateLayout();
        }
    }

    private class GridAdapter extends Adapter {
        private HashMap<Integer, Object> cache;
        private Context context;
        private HashMap<Integer, StickerSetCovered> positionsToSets;
        private int stickersPerRow;
        private int stickersRowCount;
        private int totalItems;

        /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.GridAdapter.1 */
        class C14931 extends StickerEmojiCell {
            C14931(Context context) {
                super(context);
            }

            public void onMeasure(int i, int i2) {
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), C0700C.ENCODING_PCM_32BIT));
            }
        }

        private class Holder extends ViewHolder {
            public Holder(View view) {
                super(view);
            }
        }

        public GridAdapter(Context context) {
            this.cache = new HashMap();
            this.positionsToSets = new HashMap();
            this.context = context;
        }

        public int getItemCount() {
            return this.totalItems;
        }

        public int getItemViewType(int i) {
            if (StickersAlert.this.stickerSetCovereds == null) {
                return 0;
            }
            Object obj = this.cache.get(Integer.valueOf(i));
            return obj != null ? obj instanceof Document ? 0 : 2 : 1;
        }

        public void notifyDataSetChanged() {
            int i = 0;
            if (StickersAlert.this.stickerSetCovereds != null) {
                int measuredWidth = StickersAlert.this.gridView.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
                StickersAlert.this.layoutManager.setSpanCount(this.stickersPerRow);
                this.cache.clear();
                this.positionsToSets.clear();
                this.totalItems = 0;
                this.stickersRowCount = 0;
                for (int i2 = 0; i2 < StickersAlert.this.stickerSetCovereds.size(); i2++) {
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(i2);
                    if (!stickerSetCovered.covers.isEmpty() || stickerSetCovered.cover != null) {
                        this.stickersRowCount = (int) (((double) this.stickersRowCount) + Math.ceil((double) (((float) StickersAlert.this.stickerSetCovereds.size()) / ((float) this.stickersPerRow))));
                        this.positionsToSets.put(Integer.valueOf(this.totalItems), stickerSetCovered);
                        HashMap hashMap = this.cache;
                        int i3 = this.totalItems;
                        this.totalItems = i3 + 1;
                        hashMap.put(Integer.valueOf(i3), Integer.valueOf(i2));
                        int i4 = this.totalItems / this.stickersPerRow;
                        if (stickerSetCovered.covers.isEmpty()) {
                            i4 = 1;
                            this.cache.put(Integer.valueOf(this.totalItems), stickerSetCovered.cover);
                        } else {
                            i3 = (int) Math.ceil((double) (((float) stickerSetCovered.covers.size()) / ((float) this.stickersPerRow)));
                            for (i4 = 0; i4 < stickerSetCovered.covers.size(); i4++) {
                                this.cache.put(Integer.valueOf(this.totalItems + i4), stickerSetCovered.covers.get(i4));
                            }
                            i4 = i3;
                        }
                        for (i3 = 0; i3 < this.stickersPerRow * i4; i3++) {
                            this.positionsToSets.put(Integer.valueOf(this.totalItems + i3), stickerSetCovered);
                        }
                        this.totalItems += i4 * this.stickersPerRow;
                    }
                }
            } else {
                if (StickersAlert.this.stickerSet != null) {
                    i = StickersAlert.this.stickerSet.documents.size();
                }
                this.totalItems = i;
            }
            super.notifyDataSetChanged();
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                switch (viewHolder.getItemViewType()) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) this.cache.get(Integer.valueOf(i)), false);
                        return;
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                        ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                        return;
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        ((FeaturedStickerSetInfoCell) viewHolder.itemView).setStickerSet((StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(((Integer) this.cache.get(Integer.valueOf(i))).intValue()), false);
                        return;
                    default:
                        return;
                }
            }
            ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) StickersAlert.this.stickerSet.documents.get(i), StickersAlert.this.showEmoji);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new C14931(this.context);
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    view = new EmptyCell(this.context);
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    view = new FeaturedStickerSetInfoCell(this.context, 8);
                    break;
            }
            return new Holder(view);
        }
    }

    public StickersAlert(Context context, Photo photo) {
        super(context, false);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.parentActivity = (Activity) context;
        TLObject tL_messages_getAttachedStickers = new TL_messages_getAttachedStickers();
        InputStickeredMedia tL_inputStickeredMediaPhoto = new TL_inputStickeredMediaPhoto();
        tL_inputStickeredMediaPhoto.id = new TL_inputPhoto();
        tL_inputStickeredMediaPhoto.id.id = photo.id;
        tL_inputStickeredMediaPhoto.id.access_hash = photo.access_hash;
        tL_messages_getAttachedStickers.media = tL_inputStickeredMediaPhoto;
        this.reqId = ConnectionsManager.getInstance().sendRequest(tL_messages_getAttachedStickers, new C14831());
        init(context);
    }

    public StickersAlert(Context context, BaseFragment baseFragment, InputStickerSet inputStickerSet, TL_messages_stickerSet tL_messages_stickerSet, StickersAlertDelegate stickersAlertDelegate) {
        super(context, false);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.delegate = stickersAlertDelegate;
        this.inputStickerSet = inputStickerSet;
        this.stickerSet = tL_messages_stickerSet;
        this.parentFragment = baseFragment;
        loadStickerSet();
        init(context);
    }

    private void hidePreview() {
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.stickerPreviewLayout, "alpha", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapterProxy() {
            public void onAnimationEnd(Animator animator) {
                StickersAlert.this.stickerPreviewLayout.setVisibility(8);
            }
        });
        animatorSet.start();
    }

    private void init(Context context) {
        this.shadowDrawable = context.getResources().getDrawable(C0338R.drawable.sheet_shadow);
        this.containerView = new C14863(context);
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.titleTextView = new TextView(context);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setEllipsize(TruncateAt.MIDDLE);
        this.titleTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.containerView.addView(this.titleTextView, LayoutHelper.createLinear(-1, 48));
        this.titleTextView.setOnTouchListener(new C14874());
        this.shadow[0] = new View(context);
        this.shadow[0].setBackgroundResource(C0338R.drawable.header_shadow);
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setVisibility(4);
        this.shadow[0].setTag(Integer.valueOf(1));
        this.containerView.addView(this.shadow[0], LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.gridView = new C14885(context);
        this.gridView.setTag(Integer.valueOf(14));
        RecyclerListView recyclerListView = this.gridView;
        LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        this.layoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.layoutManager.setSpanSizeLookup(new C14896());
        recyclerListView = this.gridView;
        Adapter gridAdapter = new GridAdapter(context);
        this.adapter = gridAdapter;
        recyclerListView.setAdapter(gridAdapter);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new C14907());
        this.gridView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.gridView.setClipToPadding(false);
        this.gridView.setEnabled(true);
        this.gridView.setGlowColor(-657673);
        this.gridView.setOnTouchListener(new C14918());
        this.gridView.setOnScrollListener(new C14929());
        this.stickersOnItemClickListener = new OnItemClickListener() {
            public void onItemClick(View view, int i) {
                if (StickersAlert.this.stickerSetCovereds != null) {
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) StickersAlert.this.adapter.positionsToSets.get(Integer.valueOf(i));
                    if (stickerSetCovered != null) {
                        StickersAlert.this.dismiss();
                        InputStickerSet tL_inputStickerSetID = new TL_inputStickerSetID();
                        tL_inputStickerSetID.access_hash = stickerSetCovered.set.access_hash;
                        tL_inputStickerSetID.id = stickerSetCovered.set.id;
                        new StickersAlert(StickersAlert.this.parentActivity, StickersAlert.this.parentFragment, tL_inputStickerSetID, null, null).show();
                    }
                } else if (StickersAlert.this.stickerSet != null && i >= 0 && i < StickersAlert.this.stickerSet.documents.size()) {
                    boolean z;
                    LayoutParams layoutParams;
                    AnimatorSet animatorSet;
                    StickersAlert.this.selectedSticker = (Document) StickersAlert.this.stickerSet.documents.get(i);
                    for (int i2 = 0; i2 < StickersAlert.this.selectedSticker.attributes.size(); i2++) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) StickersAlert.this.selectedSticker.attributes.get(i2);
                        if (documentAttribute instanceof TL_documentAttributeSticker) {
                            if (documentAttribute.alt != null && documentAttribute.alt.length() > 0) {
                                StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(documentAttribute.alt, StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), false));
                                z = true;
                                if (!z) {
                                    StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(StickersAlert.this.selectedSticker.id), StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), false));
                                }
                                StickersAlert.this.stickerImageView.getImageReceiver().setImage(StickersAlert.this.selectedSticker, null, StickersAlert.this.selectedSticker.thumb.location, null, "webp", true);
                                layoutParams = (LayoutParams) StickersAlert.this.stickerPreviewLayout.getLayoutParams();
                                layoutParams.topMargin = StickersAlert.this.scrollOffsetY;
                                StickersAlert.this.stickerPreviewLayout.setLayoutParams(layoutParams);
                                StickersAlert.this.stickerPreviewLayout.setVisibility(0);
                                animatorSet = new AnimatorSet();
                                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(StickersAlert.this.stickerPreviewLayout, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
                                animatorSet.setDuration(200);
                                animatorSet.start();
                            }
                            z = false;
                            if (z) {
                                StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(StickersAlert.this.selectedSticker.id), StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), false));
                            }
                            StickersAlert.this.stickerImageView.getImageReceiver().setImage(StickersAlert.this.selectedSticker, null, StickersAlert.this.selectedSticker.thumb.location, null, "webp", true);
                            layoutParams = (LayoutParams) StickersAlert.this.stickerPreviewLayout.getLayoutParams();
                            layoutParams.topMargin = StickersAlert.this.scrollOffsetY;
                            StickersAlert.this.stickerPreviewLayout.setLayoutParams(layoutParams);
                            StickersAlert.this.stickerPreviewLayout.setVisibility(0);
                            animatorSet = new AnimatorSet();
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(StickersAlert.this.stickerPreviewLayout, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
                            animatorSet.setDuration(200);
                            animatorSet.start();
                        }
                    }
                    z = false;
                    if (z) {
                        StickersAlert.this.stickerEmojiTextView.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(StickersAlert.this.selectedSticker.id), StickersAlert.this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), false));
                    }
                    StickersAlert.this.stickerImageView.getImageReceiver().setImage(StickersAlert.this.selectedSticker, null, StickersAlert.this.selectedSticker.thumb.location, null, "webp", true);
                    layoutParams = (LayoutParams) StickersAlert.this.stickerPreviewLayout.getLayoutParams();
                    layoutParams.topMargin = StickersAlert.this.scrollOffsetY;
                    StickersAlert.this.stickerPreviewLayout.setLayoutParams(layoutParams);
                    StickersAlert.this.stickerPreviewLayout.setVisibility(0);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(StickersAlert.this.stickerPreviewLayout, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
                    animatorSet.setDuration(200);
                    animatorSet.start();
                }
            }
        };
        this.gridView.setOnItemClickListener(this.stickersOnItemClickListener);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 48.0f, 0.0f, 48.0f));
        this.emptyView = new AnonymousClass11(context);
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.gridView.setEmptyView(this.emptyView);
        this.emptyView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.emptyView.addView(new ProgressBar(context), LayoutHelper.createFrame(-2, -2, 17));
        this.shadow[1] = new View(context);
        this.shadow[1].setBackgroundResource(C0338R.drawable.header_shadow_reverse);
        this.containerView.addView(this.shadow[1], LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.pickerBottomLayout = new PickerBottomLayout(context, false);
        this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        this.pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.cancelButton.setTextColor(Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
        this.pickerBottomLayout.cancelButton.setText(LocaleController.getString("Close", C0338R.string.Close).toUpperCase());
        this.pickerBottomLayout.cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StickersAlert.this.dismiss();
            }
        });
        this.pickerBottomLayout.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.doneButtonBadgeTextView.setBackgroundResource(C0338R.drawable.stickercounter);
        this.stickerPreviewLayout = new FrameLayout(context);
        this.stickerPreviewLayout.setBackgroundColor(-536870913);
        this.stickerPreviewLayout.setVisibility(8);
        this.stickerPreviewLayout.setSoundEffectsEnabled(false);
        this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.stickerPreviewLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StickersAlert.this.hidePreview();
            }
        });
        View imageView = new ImageView(context);
        imageView.setImageResource(C0338R.drawable.delete_reply);
        imageView.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            imageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.INPUT_FIELD_SELECTOR_COLOR));
        }
        this.stickerPreviewLayout.addView(imageView, LayoutHelper.createFrame(48, 48, 53));
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StickersAlert.this.hidePreview();
            }
        });
        this.stickerImageView = new BackupImageView(context);
        this.stickerImageView.setAspectFit(true);
        this.stickerPreviewLayout.addView(this.stickerImageView);
        this.stickerEmojiTextView = new TextView(context);
        this.stickerEmojiTextView.setTextSize(1, BitmapDescriptorFactory.HUE_ORANGE);
        this.stickerEmojiTextView.setGravity(85);
        this.stickerPreviewLayout.addView(this.stickerEmojiTextView);
        this.previewSendButton = new TextView(context);
        this.previewSendButton.setTextSize(1, 14.0f);
        this.previewSendButton.setTextColor(Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
        this.previewSendButton.setGravity(17);
        this.previewSendButton.setBackgroundColor(-1);
        this.previewSendButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.previewSendButton.setTypeface(FontUtil.m1176a().m1160c());
        this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
        this.previewSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StickersAlert.this.delegate.onStickerSelected(StickersAlert.this.selectedSticker);
                StickersAlert.this.dismiss();
            }
        });
        this.previewSendButtonShadow = new View(context);
        this.previewSendButtonShadow.setBackgroundResource(C0338R.drawable.header_shadow_reverse);
        this.stickerPreviewLayout.addView(this.previewSendButtonShadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        updateFields();
        updateSendButton();
        this.adapter.notifyDataSetChanged();
    }

    private void loadStickerSet() {
        if (this.inputStickerSet != null) {
            if (this.stickerSet == null && this.inputStickerSet.short_name != null) {
                this.stickerSet = StickersQuery.getStickerSetByName(this.inputStickerSet.short_name);
            }
            if (this.stickerSet == null) {
                this.stickerSet = StickersQuery.getStickerSetById(Long.valueOf(this.inputStickerSet.id));
            }
            if (this.stickerSet == null) {
                TLObject tL_messages_getStickerSet = new TL_messages_getStickerSet();
                tL_messages_getStickerSet.stickerset = this.inputStickerSet;
                ConnectionsManager.getInstance().sendRequest(tL_messages_getStickerSet, new C14852());
            } else if (this.adapter != null) {
                updateSendButton();
                updateFields();
                this.adapter.notifyDataSetChanged();
            }
        }
        if (this.stickerSet != null) {
            this.showEmoji = !this.stickerSet.set.masks;
        }
    }

    private void runShadowAnimation(int i, boolean z) {
        if (this.stickerSetCovereds == null) {
            if ((z && this.shadow[i].getTag() != null) || (!z && this.shadow[i].getTag() == null)) {
                this.shadow[i].setTag(z ? null : Integer.valueOf(1));
                if (z) {
                    this.shadow[i].setVisibility(0);
                }
                if (this.shadowAnimation[i] != null) {
                    this.shadowAnimation[i].cancel();
                }
                this.shadowAnimation[i] = new AnimatorSet();
                AnimatorSet animatorSet = this.shadowAnimation[i];
                Animator[] animatorArr = new Animator[1];
                Object obj = this.shadow[i];
                String str = "alpha";
                float[] fArr = new float[1];
                fArr[0] = z ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(obj, str, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[i].setDuration(150);
                this.shadowAnimation[i].addListener(new AnonymousClass21(i, z));
                this.shadowAnimation[i].start();
            }
        }
    }

    private void setRightButton(OnClickListener onClickListener, String str, int i, boolean z) {
        if (str == null) {
            this.pickerBottomLayout.doneButton.setVisibility(8);
            return;
        }
        this.pickerBottomLayout.doneButton.setVisibility(0);
        if (z) {
            this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(0);
            this.pickerBottomLayout.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.stickerSet.documents.size())}));
        } else {
            this.pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        }
        this.pickerBottomLayout.doneButtonTextView.setTextColor(i);
        this.pickerBottomLayout.doneButtonTextView.setText(str.toUpperCase());
        this.pickerBottomLayout.doneButton.setOnClickListener(onClickListener);
    }

    private void updateFields() {
        if (this.titleTextView != null) {
            if (this.stickerSet != null) {
                this.titleTextView.setText(this.stickerSet.set.title);
                if (this.stickerSet.set == null || !StickersQuery.isStickerPackInstalled(this.stickerSet.set.id)) {
                    OnClickListener anonymousClass17 = new OnClickListener() {

                        /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.17.1 */
                        class C14821 implements RequestDelegate {

                            /* renamed from: com.hanista.mobogram.ui.Components.StickersAlert.17.1.1 */
                            class C14811 implements Runnable {
                                final /* synthetic */ TL_error val$error;
                                final /* synthetic */ TLObject val$response;

                                C14811(TL_error tL_error, TLObject tLObject) {
                                    this.val$error = tL_error;
                                    this.val$response = tLObject;
                                }

                                public void run() {
                                    try {
                                        if (this.val$error == null) {
                                            if (StickersAlert.this.stickerSet.set.masks) {
                                                Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddMasksInstalled", C0338R.string.AddMasksInstalled), 0).show();
                                            } else {
                                                Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("AddStickersInstalled", C0338R.string.AddStickersInstalled), 0).show();
                                            }
                                            if (this.val$response instanceof TL_messages_stickerSetInstallResultArchive) {
                                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadArchivedStickers, new Object[0]);
                                                if (!(StickersAlert.this.parentFragment == null || StickersAlert.this.parentFragment.getParentActivity() == null)) {
                                                    StickersAlert.this.parentFragment.showDialog(new StickersArchiveAlert(StickersAlert.this.parentFragment.getParentActivity(), StickersAlert.this.parentFragment, ((TL_messages_stickerSetInstallResultArchive) this.val$response).sets).create());
                                                }
                                            }
                                            StickersQuery.loadStickers(StickersAlert.this.stickerSet.set.masks ? 1 : 0, false, true);
                                        }
                                        Toast.makeText(StickersAlert.this.getContext(), LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred), 0).show();
                                        if (StickersAlert.this.stickerSet.set.masks) {
                                        }
                                        StickersQuery.loadStickers(StickersAlert.this.stickerSet.set.masks ? 1 : 0, false, true);
                                    } catch (Throwable e) {
                                        FileLog.m18e("tmessages", e);
                                    }
                                }
                            }

                            C14821() {
                            }

                            public void run(TLObject tLObject, TL_error tL_error) {
                                AndroidUtilities.runOnUIThread(new C14811(tL_error, tLObject));
                            }
                        }

                        public void onClick(View view) {
                            StickersAlert.this.dismiss();
                            if (StickersAlert.this.installDelegate != null) {
                                StickersAlert.this.installDelegate.onStickerSetInstalled();
                            }
                            TLObject tL_messages_installStickerSet = new TL_messages_installStickerSet();
                            tL_messages_installStickerSet.stickerset = StickersAlert.this.inputStickerSet;
                            ConnectionsManager.getInstance().sendRequest(tL_messages_installStickerSet, new C14821());
                        }
                    };
                    String string = (this.stickerSet == null || !this.stickerSet.set.masks) ? LocaleController.getString("AddStickers", C0338R.string.AddStickers) : LocaleController.getString("AddMasks", C0338R.string.AddMasks);
                    setRightButton(anonymousClass17, string, Theme.STICKERS_SHEET_SEND_TEXT_COLOR, true);
                } else if (this.stickerSet.set.official) {
                    setRightButton(new OnClickListener() {
                        public void onClick(View view) {
                            if (StickersAlert.this.installDelegate != null) {
                                StickersAlert.this.installDelegate.onStickerSetUninstalled();
                            }
                            StickersAlert.this.dismiss();
                            StickersQuery.removeStickersSet(StickersAlert.this.getContext(), StickersAlert.this.stickerSet.set, 1, StickersAlert.this.parentFragment, true);
                        }
                    }, LocaleController.getString("StickersRemove", C0338R.string.StickersHide), Theme.STICKERS_SHEET_REMOVE_TEXT_COLOR, false);
                } else {
                    setRightButton(new OnClickListener() {
                        public void onClick(View view) {
                            if (StickersAlert.this.installDelegate != null) {
                                StickersAlert.this.installDelegate.onStickerSetUninstalled();
                            }
                            StickersAlert.this.dismiss();
                            StickersQuery.removeStickersSet(StickersAlert.this.getContext(), StickersAlert.this.stickerSet.set, 0, StickersAlert.this.parentFragment, true);
                        }
                    }, LocaleController.getString("StickersRemove", C0338R.string.StickersRemove), Theme.STICKERS_SHEET_REMOVE_TEXT_COLOR, false);
                }
                this.adapter.notifyDataSetChanged();
                return;
            }
            setRightButton(null, null, Theme.STICKERS_SHEET_REMOVE_TEXT_COLOR, false);
        }
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = this.gridView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            if (this.stickerSetCovereds == null) {
                this.titleTextView.setTranslationY((float) this.scrollOffsetY);
                this.shadow[0].setTranslationY((float) this.scrollOffsetY);
            }
            this.containerView.invalidate();
            return;
        }
        int i;
        View childAt = this.gridView.getChildAt(0);
        Holder holder = (Holder) this.gridView.findContainingViewHolder(childAt);
        paddingTop = childAt.getTop();
        if (paddingTop < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(0, true);
            i = 0;
        } else {
            runShadowAnimation(0, false);
            i = paddingTop;
        }
        if (this.scrollOffsetY != i) {
            RecyclerListView recyclerListView2 = this.gridView;
            this.scrollOffsetY = i;
            recyclerListView2.setTopGlowOffset(i);
            if (this.stickerSetCovereds == null) {
                this.titleTextView.setTranslationY((float) this.scrollOffsetY);
                this.shadow[0].setTranslationY((float) this.scrollOffsetY);
            }
            this.containerView.invalidate();
        }
    }

    private void updateSendButton() {
        int min = (int) (((float) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2)) / AndroidUtilities.density);
        if (this.delegate == null || (this.stickerSet != null && this.stickerSet.set.masks)) {
            this.previewSendButton.setText(LocaleController.getString("Close", C0338R.string.Close).toUpperCase());
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, min, 17));
            this.previewSendButton.setVisibility(8);
            this.previewSendButtonShadow.setVisibility(8);
            return;
        }
        this.previewSendButton.setText(LocaleController.getString("SendSticker", C0338R.string.SendSticker).toUpperCase());
        this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(min, (float) min, 17, 0.0f, 0.0f, 0.0f, BitmapDescriptorFactory.HUE_ORANGE));
        this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(min, (float) min, 17, 0.0f, 0.0f, 0.0f, BitmapDescriptorFactory.HUE_ORANGE));
        this.previewSendButton.setVisibility(0);
        this.previewSendButtonShadow.setVisibility(0);
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.emojiDidLoaded) {
            if (this.gridView != null) {
                int childCount = this.gridView.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    this.gridView.getChildAt(i2).invalidate();
                }
            }
            if (StickerPreviewViewer.getInstance().isVisible()) {
                StickerPreviewViewer.getInstance().close();
            }
            StickerPreviewViewer.getInstance().reset();
        }
    }

    public void dismiss() {
        super.dismiss();
        if (this.reqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    public void setInstallDelegate(StickersAlertInstallDelegate stickersAlertInstallDelegate) {
        this.installDelegate = stickersAlertInstallDelegate;
    }
}
