package com.hanista.mobogram.ui.Components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Looper;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.FaceDetector.Builder;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.Bitmaps;
import com.hanista.mobogram.messenger.DispatchQueue;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.InputDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeSticker;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_maskCoords;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.ActionBarPopupWindow;
import com.hanista.mobogram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import com.hanista.mobogram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.Paint.Brush;
import com.hanista.mobogram.ui.Components.Paint.Brush.Elliptical;
import com.hanista.mobogram.ui.Components.Paint.Brush.Neon;
import com.hanista.mobogram.ui.Components.Paint.Brush.Radial;
import com.hanista.mobogram.ui.Components.Paint.Painting;
import com.hanista.mobogram.ui.Components.Paint.PhotoFace;
import com.hanista.mobogram.ui.Components.Paint.RenderView;
import com.hanista.mobogram.ui.Components.Paint.RenderView.RenderViewDelegate;
import com.hanista.mobogram.ui.Components.Paint.Swatch;
import com.hanista.mobogram.ui.Components.Paint.UndoStore;
import com.hanista.mobogram.ui.Components.Paint.UndoStore.UndoStoreDelegate;
import com.hanista.mobogram.ui.Components.Paint.Views.ColorPicker;
import com.hanista.mobogram.ui.Components.Paint.Views.ColorPicker.ColorPickerDelegate;
import com.hanista.mobogram.ui.Components.Paint.Views.EditTextOutline;
import com.hanista.mobogram.ui.Components.Paint.Views.EntitiesContainerView;
import com.hanista.mobogram.ui.Components.Paint.Views.EntitiesContainerView.EntitiesContainerViewDelegate;
import com.hanista.mobogram.ui.Components.Paint.Views.EntityView;
import com.hanista.mobogram.ui.Components.Paint.Views.EntityView.EntityViewDelegate;
import com.hanista.mobogram.ui.Components.Paint.Views.StickerView;
import com.hanista.mobogram.ui.Components.Paint.Views.TextPaintView;
import com.hanista.mobogram.ui.Components.StickerMasksView.Listener;
import com.hanista.mobogram.ui.PhotoViewer;
import java.util.ArrayList;

@SuppressLint({"NewApi"})
public class PhotoPaintView extends FrameLayout implements EntityViewDelegate {
    private static final int gallery_menu_done = 1;
    private static final int gallery_menu_undo = 2;
    private ActionBar actionBar;
    private Bitmap bitmapToEdit;
    private Brush[] brushes;
    private TextView cancelTextView;
    private ColorPicker colorPicker;
    private Animator colorPickerAnimator;
    int currentBrush;
    private EntityView currentEntityView;
    private FrameLayout curtainView;
    private FrameLayout dimView;
    private ActionBarMenuItem doneItem;
    private TextView doneTextView;
    private Point editedTextPosition;
    private float editedTextRotation;
    private float editedTextScale;
    private boolean editingText;
    private EntitiesContainerView entitiesView;
    private ArrayList<PhotoFace> faces;
    private String initialText;
    private int orientation;
    private ImageView paintButton;
    private Size paintingSize;
    private boolean pickingSticker;
    private ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    private DispatchQueue queue;
    private RenderView renderView;
    private boolean selectedStroke;
    private FrameLayout selectionContainerView;
    private StickerMasksView stickersView;
    private FrameLayout textDimView;
    private FrameLayout toolsView;
    private ActionBarMenuItem undoItem;
    private UndoStore undoStore;

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.12 */
    class AnonymousClass12 implements OnClickListener {
        final /* synthetic */ Runnable val$okRunnable;

        AnonymousClass12(Runnable runnable) {
            this.val$okRunnable = runnable;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            this.val$okRunnable.run();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.14 */
    class AnonymousClass14 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$visible;

        AnonymousClass14(boolean z) {
            this.val$visible = z;
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.val$visible) {
                PhotoPaintView.this.dimView.setVisibility(8);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.15 */
    class AnonymousClass15 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$visible;

        AnonymousClass15(boolean z) {
            this.val$visible = z;
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.val$visible) {
                PhotoPaintView.this.textDimView.setVisibility(8);
                if (PhotoPaintView.this.textDimView.getParent() != null) {
                    ((EntitiesContainerView) PhotoPaintView.this.textDimView.getParent()).removeView(PhotoPaintView.this.textDimView);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ EntityView val$entityView;

        AnonymousClass18(EntityView entityView) {
            this.val$entityView = entityView;
        }

        public void run() {
            PhotoPaintView.this.removeEntity(this.val$entityView);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.19 */
    class AnonymousClass19 implements Runnable {
        final /* synthetic */ EntityView val$entityView;

        /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.19.1 */
        class C14191 implements View.OnClickListener {
            C14191() {
            }

            public void onClick(View view) {
                PhotoPaintView.this.removeEntity(AnonymousClass19.this.val$entityView);
                if (PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing()) {
                    PhotoPaintView.this.popupWindow.dismiss(true);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.19.2 */
        class C14202 implements View.OnClickListener {
            C14202() {
            }

            public void onClick(View view) {
                PhotoPaintView.this.editSelectedTextEntity();
                if (PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing()) {
                    PhotoPaintView.this.popupWindow.dismiss(true);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.19.3 */
        class C14213 implements View.OnClickListener {
            C14213() {
            }

            public void onClick(View view) {
                PhotoPaintView.this.duplicateSelectedEntity();
                if (PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing()) {
                    PhotoPaintView.this.popupWindow.dismiss(true);
                }
            }
        }

        AnonymousClass19(EntityView entityView) {
            this.val$entityView = entityView;
        }

        public void run() {
            View linearLayout = new LinearLayout(PhotoPaintView.this.getContext());
            linearLayout.setOrientation(0);
            View textView = new TextView(PhotoPaintView.this.getContext());
            textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            textView.setBackgroundResource(C0338R.drawable.list_selector);
            textView.setGravity(16);
            textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(14.0f), 0);
            textView.setTextSize(PhotoPaintView.gallery_menu_done, 18.0f);
            textView.setTag(Integer.valueOf(0));
            textView.setText(LocaleController.getString("PaintDelete", C0338R.string.PaintDelete));
            textView.setOnClickListener(new C14191());
            linearLayout.addView(textView, LayoutHelper.createLinear(-2, 48));
            if (this.val$entityView instanceof TextPaintView) {
                textView = new TextView(PhotoPaintView.this.getContext());
                textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
                textView.setBackgroundResource(C0338R.drawable.list_selector);
                textView.setGravity(16);
                textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
                textView.setTextSize(PhotoPaintView.gallery_menu_done, 18.0f);
                textView.setTag(Integer.valueOf(PhotoPaintView.gallery_menu_done));
                textView.setText(LocaleController.getString("PaintEdit", C0338R.string.PaintEdit));
                textView.setOnClickListener(new C14202());
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, 48));
            }
            textView = new TextView(PhotoPaintView.this.getContext());
            textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            textView.setBackgroundResource(C0338R.drawable.list_selector);
            textView.setGravity(16);
            textView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(16.0f), 0);
            textView.setTextSize(PhotoPaintView.gallery_menu_done, 18.0f);
            textView.setTag(Integer.valueOf(PhotoPaintView.gallery_menu_undo));
            textView.setText(LocaleController.getString("PaintDuplicate", C0338R.string.PaintDuplicate));
            textView.setOnClickListener(new C14213());
            linearLayout.addView(textView, LayoutHelper.createLinear(-2, 48));
            PhotoPaintView.this.popupLayout.addView(linearLayout);
            LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -2;
            linearLayout.setLayoutParams(layoutParams);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.1 */
    class C14221 implements UndoStoreDelegate {
        C14221() {
        }

        public void historyChanged() {
            PhotoPaintView.this.setMenuItemEnabled(PhotoPaintView.this.undoStore.canUndo());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.20 */
    class AnonymousClass20 implements View.OnClickListener {
        final /* synthetic */ int val$brush;

        AnonymousClass20(int i) {
            this.val$brush = i;
        }

        public void onClick(View view) {
            PhotoPaintView.this.setBrush(this.val$brush);
            if (PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing()) {
                PhotoPaintView.this.popupWindow.dismiss(true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.22 */
    class AnonymousClass22 extends FrameLayout {
        AnonymousClass22(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.23 */
    class AnonymousClass23 implements View.OnClickListener {
        final /* synthetic */ boolean val$stroke;

        AnonymousClass23(boolean z) {
            this.val$stroke = z;
        }

        public void onClick(View view) {
            PhotoPaintView.this.setStroke(this.val$stroke);
            if (PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing()) {
                PhotoPaintView.this.popupWindow.dismiss(true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.2 */
    class C14232 implements RenderViewDelegate {
        C14232() {
        }

        public void onBeganDrawing() {
            PhotoPaintView.this.setColorPickerVisibilityFade(false);
            if (PhotoPaintView.this.currentEntityView != null) {
                PhotoPaintView.this.selectEntity(null);
            }
        }

        public void onFinishedDrawing(boolean z) {
            if (z) {
                PhotoPaintView.this.setColorPickerVisibilityFade(true);
            }
            PhotoPaintView.this.setMenuItemEnabled(PhotoPaintView.this.undoStore.canUndo());
        }

        public boolean shouldDraw() {
            boolean z = PhotoPaintView.this.currentEntityView == null;
            if (!z) {
                PhotoPaintView.this.selectEntity(null);
            }
            return z;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.3 */
    class C14243 implements EntitiesContainerViewDelegate {
        C14243() {
        }

        public void onEntityDeselect() {
            PhotoPaintView.this.selectEntity(null);
        }

        public EntityView onSelectedEntityRequest() {
            return PhotoPaintView.this.currentEntityView;
        }

        public boolean shouldReceiveTouches() {
            return PhotoPaintView.this.textDimView.getVisibility() != 0;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.4 */
    class C14254 implements View.OnClickListener {
        C14254() {
        }

        public void onClick(View view) {
            PhotoPaintView.this.closeTextEnter(true);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.5 */
    class C14265 extends FrameLayout {
        C14265(Context context) {
            super(context);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.6 */
    class C14276 implements ColorPickerDelegate {
        C14276() {
        }

        public void onBeganColorPicking() {
            if (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView)) {
                PhotoPaintView.this.setDimVisibility(true);
            }
        }

        public void onColorValueChanged() {
            PhotoPaintView.this.setCurrentSwatch(PhotoPaintView.this.colorPicker.getSwatch(), false);
        }

        public void onFinishedColorPicking() {
            PhotoPaintView.this.setCurrentSwatch(PhotoPaintView.this.colorPicker.getSwatch(), false);
            if (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView)) {
                PhotoPaintView.this.setDimVisibility(false);
            }
        }

        public void onSettingsPressed() {
            if (PhotoPaintView.this.currentEntityView == null) {
                PhotoPaintView.this.showBrushSettings();
            } else if (PhotoPaintView.this.currentEntityView instanceof StickerView) {
                PhotoPaintView.this.mirrorSticker();
            } else if (PhotoPaintView.this.currentEntityView instanceof TextPaintView) {
                PhotoPaintView.this.showTextSettings();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.7 */
    class C14287 implements View.OnClickListener {
        C14287() {
        }

        public void onClick(View view) {
            PhotoPaintView.this.openStickersView();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.8 */
    class C14298 implements View.OnClickListener {
        C14298() {
        }

        public void onClick(View view) {
            PhotoPaintView.this.selectEntity(null);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PhotoPaintView.9 */
    class C14309 implements View.OnClickListener {
        C14309() {
        }

        public void onClick(View view) {
            PhotoPaintView.this.createText();
        }
    }

    private class StickerPosition {
        private float angle;
        private Point position;
        private float scale;

        StickerPosition(Point point, float f, float f2) {
            this.position = point;
            this.scale = f;
            this.angle = f2;
        }
    }

    public PhotoPaintView(Context context, Bitmap bitmap, int i) {
        super(context);
        this.brushes = new Brush[]{new Radial(), new Elliptical(), new Neon()};
        this.selectedStroke = true;
        this.queue = new DispatchQueue("Paint");
        this.bitmapToEdit = bitmap;
        this.orientation = i;
        this.undoStore = new UndoStore();
        this.undoStore.setDelegate(new C14221());
        this.curtainView = new FrameLayout(context);
        this.curtainView.setBackgroundColor(Theme.MSG_TEXT_COLOR);
        this.curtainView.setVisibility(4);
        addView(this.curtainView);
        this.renderView = new RenderView(context, new Painting(getPaintingSize()), bitmap, this.orientation);
        this.renderView.setDelegate(new C14232());
        this.renderView.setUndoStore(this.undoStore);
        this.renderView.setQueue(this.queue);
        this.renderView.setVisibility(4);
        this.renderView.setBrush(this.brushes[0]);
        addView(this.renderView, LayoutHelper.createFrame(-1, -1, 51));
        this.entitiesView = new EntitiesContainerView(context, new C14243());
        this.entitiesView.setPivotX(0.0f);
        this.entitiesView.setPivotY(0.0f);
        addView(this.entitiesView);
        this.dimView = new FrameLayout(context);
        this.dimView.setAlpha(0.0f);
        this.dimView.setBackgroundColor(1711276032);
        this.dimView.setVisibility(8);
        addView(this.dimView);
        this.textDimView = new FrameLayout(context);
        this.textDimView.setAlpha(0.0f);
        this.textDimView.setBackgroundColor(1711276032);
        this.textDimView.setVisibility(8);
        this.textDimView.setOnClickListener(new C14254());
        this.selectionContainerView = new C14265(context);
        addView(this.selectionContainerView);
        this.colorPicker = new ColorPicker(context);
        addView(this.colorPicker);
        this.colorPicker.setDelegate(new C14276());
        this.toolsView = new FrameLayout(context);
        this.toolsView.setBackgroundColor(Theme.MSG_TEXT_COLOR);
        addView(this.toolsView, LayoutHelper.createFrame(-1, 48, 83));
        this.cancelTextView = new TextView(context);
        this.cancelTextView.setTextSize(gallery_menu_done, 14.0f);
        this.cancelTextView.setTextColor(-1);
        this.cancelTextView.setGravity(17);
        this.cancelTextView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false));
        this.cancelTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelTextView.setText(LocaleController.getString("Cancel", C0338R.string.Cancel).toUpperCase());
        this.cancelTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.toolsView.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
        this.doneTextView = new TextView(context);
        this.doneTextView.setTextSize(gallery_menu_done, 14.0f);
        this.doneTextView.setTextColor(-11420173);
        this.doneTextView.setGravity(17);
        this.doneTextView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false));
        this.doneTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneTextView.setText(LocaleController.getString("Done", C0338R.string.Done).toUpperCase());
        this.doneTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.toolsView.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
        View imageView = new ImageView(context);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageResource(C0338R.drawable.photo_sticker);
        imageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(imageView, LayoutHelper.createFrame(54, Face.UNCOMPUTED_PROBABILITY, 17, 0.0f, 0.0f, 56.0f, 0.0f));
        imageView.setOnClickListener(new C14287());
        this.paintButton = new ImageView(context);
        this.paintButton.setScaleType(ScaleType.CENTER);
        this.paintButton.setImageResource(C0338R.drawable.photo_paint);
        this.paintButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(this.paintButton, LayoutHelper.createFrame(54, -1, 17));
        this.paintButton.setOnClickListener(new C14298());
        imageView = new ImageView(context);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageResource(C0338R.drawable.photo_paint_text);
        imageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(imageView, LayoutHelper.createFrame(54, Face.UNCOMPUTED_PROBABILITY, 17, 56.0f, 0.0f, 0.0f, 0.0f));
        imageView.setOnClickListener(new C14309());
        this.actionBar = new ActionBar(context);
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.actionBar.setOccupyStatusBar(VERSION.SDK_INT >= 21);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR);
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("PaintDraw", C0338R.string.PaintDraw));
        addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public boolean canOpenMenu() {
                return false;
            }

            public void onItemClick(int i) {
                if (i == -1) {
                    PhotoPaintView.this.cancelTextView.callOnClick();
                } else if (i == PhotoPaintView.gallery_menu_done) {
                    PhotoPaintView.this.closeTextEnter(true);
                } else if (i == PhotoPaintView.gallery_menu_undo) {
                    PhotoPaintView.this.undoStore.undo();
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        this.undoItem = createMenu.addItem(gallery_menu_undo, C0338R.drawable.photo_undo, AndroidUtilities.dp(56.0f));
        setMenuItemEnabled(false);
        this.doneItem = createMenu.addItemWithWidth((int) gallery_menu_done, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.doneItem.setVisibility(8);
        setCurrentSwatch(this.colorPicker.getSwatch(), false);
        updateSettingsButton();
    }

    private int baseFontSize() {
        return (int) (getPaintingSize().width / 9.0f);
    }

    private Size baseStickerSize() {
        float floor = (float) Math.floor(((double) getPaintingSize().width) * 0.5d);
        return new Size(floor, floor);
    }

    private FrameLayout buttonForBrush(int i, int i2, boolean z) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setBackgroundResource(C0338R.drawable.list_selector);
        frameLayout.setOnClickListener(new AnonymousClass20(i));
        View imageView = new ImageView(getContext());
        imageView.setImageResource(i2);
        frameLayout.addView(imageView, LayoutHelper.createFrame(165, 44.0f, 19, 46.0f, 0.0f, 8.0f, 0.0f));
        if (z) {
            View imageView2 = new ImageView(getContext());
            imageView2.setImageResource(C0338R.drawable.ic_ab_done_gray);
            imageView2.setScaleType(ScaleType.CENTER);
            frameLayout.addView(imageView2, LayoutHelper.createFrame(50, Face.UNCOMPUTED_PROBABILITY));
        }
        return frameLayout;
    }

    private FrameLayout buttonForText(boolean z, String str, boolean z2) {
        int i = Theme.MSG_TEXT_COLOR;
        FrameLayout anonymousClass22 = new AnonymousClass22(getContext());
        anonymousClass22.setBackgroundResource(C0338R.drawable.list_selector);
        anonymousClass22.setOnClickListener(new AnonymousClass23(z));
        View editTextOutline = new EditTextOutline(getContext());
        editTextOutline.setBackgroundColor(0);
        editTextOutline.setEnabled(false);
        editTextOutline.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        editTextOutline.setTextColor(z ? -1 : Theme.MSG_TEXT_COLOR);
        if (!z) {
            i = 0;
        }
        editTextOutline.setStrokeColor(i);
        editTextOutline.setPadding(AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(2.0f), 0);
        editTextOutline.setTextSize(gallery_menu_done, 18.0f);
        editTextOutline.setTypeface(null, gallery_menu_done);
        editTextOutline.setTag(Boolean.valueOf(z));
        editTextOutline.setText(str);
        anonymousClass22.addView(editTextOutline, LayoutHelper.createFrame(-2, -2.0f, 19, 46.0f, 0.0f, 16.0f, 0.0f));
        if (z2) {
            View imageView = new ImageView(getContext());
            imageView.setImageResource(C0338R.drawable.ic_ab_done_gray);
            imageView.setScaleType(ScaleType.CENTER);
            anonymousClass22.addView(imageView, LayoutHelper.createFrame(50, Face.UNCOMPUTED_PROBABILITY));
        }
        return anonymousClass22;
    }

    private StickerPosition calculateStickerPosition(Document document) {
        TL_maskCoords tL_maskCoords;
        for (int i = 0; i < document.attributes.size(); i += gallery_menu_done) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                tL_maskCoords = documentAttribute.mask_coords;
                break;
            }
        }
        tL_maskCoords = null;
        StickerPosition stickerPosition = new StickerPosition(centerPositionForEntity(), AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION, 0.0f);
        if (tL_maskCoords == null || this.faces == null || this.faces.size() == 0) {
            return stickerPosition;
        }
        int i2 = tL_maskCoords.f2667n;
        PhotoFace randomFaceWithVacantAnchor = getRandomFaceWithVacantAnchor(i2, document.id, tL_maskCoords);
        if (randomFaceWithVacantAnchor == null) {
            return stickerPosition;
        }
        Point pointForAnchor = randomFaceWithVacantAnchor.getPointForAnchor(i2);
        float widthForAnchor = randomFaceWithVacantAnchor.getWidthForAnchor(i2);
        float angle = randomFaceWithVacantAnchor.getAngle();
        float toRadians = (float) Math.toRadians((double) angle);
        float cos = (float) ((Math.cos(1.5707963267948966d - ((double) toRadians)) * ((double) widthForAnchor)) * tL_maskCoords.f2668x);
        return new StickerPosition(new Point((pointForAnchor.f2683x + ((float) ((Math.sin(1.5707963267948966d - ((double) toRadians)) * ((double) widthForAnchor)) * tL_maskCoords.f2668x))) + ((float) ((Math.cos(((double) toRadians) + 1.5707963267948966d) * ((double) widthForAnchor)) * tL_maskCoords.f2669y)), (pointForAnchor.f2684y + cos) + ((float) ((Math.sin(((double) toRadians) + 1.5707963267948966d) * ((double) widthForAnchor)) * tL_maskCoords.f2669y))), (float) (((double) (widthForAnchor / baseStickerSize().width)) * tL_maskCoords.zoom), angle);
    }

    private Point centerPositionForEntity() {
        Size paintingSize = getPaintingSize();
        return new Point(paintingSize.width / 2.0f, paintingSize.height / 2.0f);
    }

    private void closeStickersView() {
        if (this.stickersView != null && this.stickersView.getVisibility() == 0) {
            this.pickingSticker = false;
            Animator ofFloat = ObjectAnimator.ofFloat(this.stickersView, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f});
            ofFloat.setDuration(200);
            ofFloat.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animator) {
                    PhotoPaintView.this.stickersView.setVisibility(8);
                }
            });
            ofFloat.start();
            this.undoItem.setVisibility(0);
            this.actionBar.setTitle(LocaleController.getString("PaintDraw", C0338R.string.PaintDraw));
        }
    }

    private void createSticker(Document document) {
        StickerPosition calculateStickerPosition = calculateStickerPosition(document);
        View stickerView = new StickerView(getContext(), calculateStickerPosition.position, calculateStickerPosition.angle, calculateStickerPosition.scale, baseStickerSize(), document);
        stickerView.setDelegate(this);
        this.entitiesView.addView(stickerView);
        registerRemovalUndo(stickerView);
        selectEntity(stickerView);
    }

    private void createText() {
        Swatch swatch = this.colorPicker.getSwatch();
        Swatch swatch2 = new Swatch(-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, swatch.brushWeight);
        Swatch swatch3 = new Swatch(Theme.MSG_TEXT_COLOR, 0.85f, swatch.brushWeight);
        if (!this.selectedStroke) {
            swatch3 = swatch2;
        }
        setCurrentSwatch(swatch3, true);
        View textPaintView = new TextPaintView(getContext(), startPositionRelativeToEntity(null), baseFontSize(), TtmlNode.ANONYMOUS_REGION_ID, this.colorPicker.getSwatch(), this.selectedStroke);
        textPaintView.setDelegate(this);
        textPaintView.setMaxWidth((int) (getPaintingSize().width - 20.0f));
        this.entitiesView.addView(textPaintView, LayoutHelper.createFrame(-2, -2.0f));
        registerRemovalUndo(textPaintView);
        selectEntity(textPaintView);
        editSelectedTextEntity();
    }

    private void detectFaces() {
        this.queue.postRunnable(new Runnable() {
            public void run() {
                try {
                    FaceDetector build = new Builder(PhotoPaintView.this.getContext()).setMode(PhotoPaintView.gallery_menu_done).setLandmarkType(PhotoPaintView.gallery_menu_done).setTrackingEnabled(false).build();
                    if (build.isOperational()) {
                        try {
                            SparseArray detect = build.detect(new Frame.Builder().setBitmap(PhotoPaintView.this.bitmapToEdit).setRotation(PhotoPaintView.this.getFrameRotation()).build());
                            ArrayList arrayList = new ArrayList();
                            Size access$3700 = PhotoPaintView.this.getPaintingSize();
                            for (int i = 0; i < detect.size(); i += PhotoPaintView.gallery_menu_done) {
                                PhotoFace photoFace = new PhotoFace((Face) detect.get(detect.keyAt(i)), PhotoPaintView.this.bitmapToEdit, access$3700, PhotoPaintView.this.isSidewardOrientation());
                                if (photoFace.isSufficient()) {
                                    arrayList.add(photoFace);
                                }
                            }
                            PhotoPaintView.this.faces = arrayList;
                            build.release();
                            return;
                        } catch (Throwable th) {
                            FileLog.m18e("tmessages", th);
                            return;
                        }
                    }
                    FileLog.m16e("tmessages", "face detection is not operational");
                } catch (Throwable th2) {
                    FileLog.m18e("tmessages", th2);
                }
            }
        });
    }

    private void duplicateSelectedEntity() {
        if (this.currentEntityView != null) {
            EntityView entityView = null;
            Point startPositionRelativeToEntity = startPositionRelativeToEntity(this.currentEntityView);
            View stickerView;
            if (this.currentEntityView instanceof StickerView) {
                stickerView = new StickerView(getContext(), (StickerView) this.currentEntityView, startPositionRelativeToEntity);
                stickerView.setDelegate(this);
                this.entitiesView.addView(stickerView);
                entityView = stickerView;
            } else if (this.currentEntityView instanceof TextPaintView) {
                stickerView = new TextPaintView(getContext(), (TextPaintView) this.currentEntityView, startPositionRelativeToEntity);
                stickerView.setDelegate(this);
                stickerView.setMaxWidth((int) (getPaintingSize().width - 20.0f));
                this.entitiesView.addView(stickerView, LayoutHelper.createFrame(-2, -2.0f));
                View view = stickerView;
            }
            registerRemovalUndo(entityView);
            selectEntity(entityView);
            updateSettingsButton();
        }
    }

    private void editSelectedTextEntity() {
        if ((this.currentEntityView instanceof TextPaintView) && !this.editingText) {
            this.curtainView.setVisibility(0);
            TextPaintView textPaintView = (TextPaintView) this.currentEntityView;
            this.initialText = textPaintView.getText();
            this.editingText = true;
            this.editedTextPosition = textPaintView.getPosition();
            this.editedTextRotation = textPaintView.getRotation();
            this.editedTextScale = textPaintView.getScale();
            textPaintView.setPosition(centerPositionForEntity());
            textPaintView.setRotation(0.0f);
            textPaintView.setScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.undoItem.setVisibility(8);
            this.doneItem.setVisibility(0);
            this.actionBar.setTitle(LocaleController.getString("PaintText", C0338R.string.PaintText));
            this.toolsView.setVisibility(8);
            setColorPickerVisibilitySlide(false);
            setTextDimVisibility(true, textPaintView);
            textPaintView.beginEditing();
            ((InputMethodManager) ApplicationLoader.applicationContext.getSystemService("input_method")).toggleSoftInputFromWindow(textPaintView.getFocusedView().getWindowToken(), gallery_menu_undo, 0);
        }
    }

    private int getFrameRotation() {
        switch (this.orientation) {
            case 90:
                return gallery_menu_done;
            case 180:
                return gallery_menu_undo;
            case 270:
                return 3;
            default:
                return 0;
        }
    }

    private Size getPaintingSize() {
        if (this.paintingSize != null) {
            return this.paintingSize;
        }
        float height = isSidewardOrientation() ? (float) this.bitmapToEdit.getHeight() : (float) this.bitmapToEdit.getWidth();
        float width = isSidewardOrientation() ? (float) this.bitmapToEdit.getWidth() : (float) this.bitmapToEdit.getHeight();
        Size size = new Size(height, width);
        size.width = 1280.0f;
        size.height = (float) Math.floor((double) ((size.width * width) / height));
        if (size.height > 1280.0f) {
            size.height = 1280.0f;
            size.width = (float) Math.floor((double) ((height * size.height) / width));
        }
        this.paintingSize = size;
        return size;
    }

    private PhotoFace getRandomFaceWithVacantAnchor(int i, long j, TL_maskCoords tL_maskCoords) {
        if (i < 0 || i > 3 || this.faces.isEmpty()) {
            return null;
        }
        int size = this.faces.size();
        int nextInt = Utilities.random.nextInt(size);
        for (int i2 = size; i2 > 0; i2--) {
            PhotoFace photoFace = (PhotoFace) this.faces.get(nextInt);
            if (!isFaceAnchorOccupied(photoFace, i, j, tL_maskCoords)) {
                return photoFace;
            }
            nextInt = (nextInt + gallery_menu_done) % size;
        }
        return null;
    }

    private boolean hasChanges() {
        return this.undoStore.canUndo() || this.entitiesView.entitiesCount() > 0;
    }

    private boolean isFaceAnchorOccupied(PhotoFace photoFace, int i, long j, TL_maskCoords tL_maskCoords) {
        Point pointForAnchor = photoFace.getPointForAnchor(i);
        if (pointForAnchor == null) {
            return true;
        }
        float widthForAnchor = photoFace.getWidthForAnchor(0) * 1.1f;
        for (int i2 = 0; i2 < this.entitiesView.getChildCount(); i2 += gallery_menu_done) {
            View childAt = this.entitiesView.getChildAt(i2);
            if (childAt instanceof StickerView) {
                StickerView stickerView = (StickerView) childAt;
                if (stickerView.getAnchor() == i) {
                    Point position = stickerView.getPosition();
                    float hypot = (float) Math.hypot((double) (position.f2683x - pointForAnchor.f2683x), (double) (position.f2684y - pointForAnchor.f2684y));
                    if ((j == stickerView.getSticker().id || this.faces.size() > gallery_menu_done) && hypot < widthForAnchor) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    private boolean isSidewardOrientation() {
        return this.orientation % 360 == 90 || this.orientation % 360 == 270;
    }

    private void mirrorSticker() {
        if (this.currentEntityView instanceof StickerView) {
            ((StickerView) this.currentEntityView).mirror();
        }
    }

    private void openStickersView() {
        if (this.stickersView == null || this.stickersView.getVisibility() != 0) {
            this.pickingSticker = true;
            if (this.stickersView == null) {
                this.stickersView = new StickerMasksView(getContext());
                this.stickersView.setListener(new Listener() {
                    public void onStickerSelected(Document document) {
                        PhotoPaintView.this.closeStickersView();
                        PhotoPaintView.this.createSticker(document);
                    }

                    public void onTypeChanged() {
                        PhotoPaintView.this.updateStickersTitle();
                    }
                });
                addView(this.stickersView, LayoutHelper.createFrame(-1, -1, 51));
            }
            this.stickersView.setVisibility(0);
            Animator ofFloat = ObjectAnimator.ofFloat(this.stickersView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            ofFloat.setDuration(200);
            ofFloat.start();
            this.undoItem.setVisibility(8);
            updateStickersTitle();
        }
    }

    private void registerRemovalUndo(EntityView entityView) {
        this.undoStore.registerUndo(entityView.getUUID(), new AnonymousClass18(entityView));
    }

    private void removeEntity(EntityView entityView) {
        if (entityView == this.currentEntityView) {
            this.currentEntityView.deselect();
            this.currentEntityView = null;
            updateSettingsButton();
        }
        this.entitiesView.removeView(entityView);
        this.undoStore.unregisterUndo(entityView.getUUID());
    }

    private boolean selectEntity(EntityView entityView) {
        boolean z = true;
        boolean z2 = false;
        if (this.currentEntityView != null) {
            if (this.currentEntityView == entityView) {
                if (!this.editingText) {
                    showMenuForEntity(this.currentEntityView);
                }
                return z;
            }
            this.currentEntityView.deselect();
            z2 = true;
        }
        this.currentEntityView = entityView;
        if (this.currentEntityView != null) {
            this.currentEntityView.select(this.selectionContainerView);
            this.entitiesView.bringViewToFront(this.currentEntityView);
            if (this.currentEntityView instanceof TextPaintView) {
                setCurrentSwatch(((TextPaintView) this.currentEntityView).getSwatch(), true);
            }
        } else {
            z = z2;
        }
        updateSettingsButton();
        return z;
    }

    private void setBrush(int i) {
        RenderView renderView = this.renderView;
        Brush[] brushArr = this.brushes;
        this.currentBrush = i;
        renderView.setBrush(brushArr[i]);
    }

    private void setColorPickerVisibilityFade(boolean z) {
        if (z) {
            float[] fArr = new float[gallery_menu_undo];
            fArr[0] = this.colorPicker.getAlpha();
            fArr[gallery_menu_done] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            this.colorPickerAnimator = ObjectAnimator.ofFloat(this.colorPicker, "alpha", fArr);
            this.colorPickerAnimator.setStartDelay(200);
            this.colorPickerAnimator.setDuration(200);
            this.colorPickerAnimator.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animator) {
                    if (PhotoPaintView.this.colorPickerAnimator != null) {
                        PhotoPaintView.this.colorPickerAnimator = null;
                    }
                }
            });
            this.colorPickerAnimator.start();
            return;
        }
        if (this.colorPickerAnimator != null) {
            this.colorPickerAnimator.cancel();
            this.colorPickerAnimator = null;
        }
        this.colorPicker.setAlpha(0.0f);
    }

    private void setColorPickerVisibilitySlide(boolean z) {
        float f = 0.0f;
        ColorPicker colorPicker = this.colorPicker;
        String str = "translationX";
        float[] fArr = new float[gallery_menu_undo];
        fArr[0] = z ? (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW) : 0.0f;
        if (!z) {
            f = (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
        }
        fArr[gallery_menu_done] = f;
        Animator ofFloat = ObjectAnimator.ofFloat(colorPicker, str, fArr);
        ofFloat.setDuration(200);
        ofFloat.start();
    }

    private void setCurrentSwatch(Swatch swatch, boolean z) {
        this.renderView.setColor(swatch.color);
        this.renderView.setBrushSize(swatch.brushWeight);
        if (z) {
            this.colorPicker.setSwatch(swatch);
        }
        if (this.currentEntityView instanceof TextPaintView) {
            ((TextPaintView) this.currentEntityView).setSwatch(swatch);
        }
    }

    private void setDimVisibility(boolean z) {
        Animator ofFloat;
        if (z) {
            this.dimView.setVisibility(0);
            ofFloat = ObjectAnimator.ofFloat(this.dimView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
        } else {
            ofFloat = ObjectAnimator.ofFloat(this.dimView, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f});
        }
        ofFloat.addListener(new AnonymousClass14(z));
        ofFloat.setDuration(200);
        ofFloat.start();
    }

    private void setMenuItemEnabled(boolean z) {
        this.undoItem.setAlpha(z ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.3f);
        this.undoItem.setEnabled(z);
    }

    private void setStroke(boolean z) {
        this.selectedStroke = z;
        if (this.currentEntityView instanceof TextPaintView) {
            Swatch swatch = this.colorPicker.getSwatch();
            if (z && swatch.color == -1) {
                setCurrentSwatch(new Swatch(Theme.MSG_TEXT_COLOR, 0.85f, swatch.brushWeight), true);
            } else if (!z && swatch.color == Theme.MSG_TEXT_COLOR) {
                setCurrentSwatch(new Swatch(-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, swatch.brushWeight), true);
            }
            ((TextPaintView) this.currentEntityView).setStroke(z);
        }
    }

    private void setTextDimVisibility(boolean z, EntityView entityView) {
        Animator ofFloat;
        if (z && entityView != null) {
            ViewGroup viewGroup = (ViewGroup) entityView.getParent();
            if (this.textDimView.getParent() != null) {
                ((EntitiesContainerView) this.textDimView.getParent()).removeView(this.textDimView);
            }
            viewGroup.addView(this.textDimView, viewGroup.indexOfChild(entityView));
        }
        entityView.setSelectionVisibility(!z);
        if (z) {
            this.textDimView.setVisibility(0);
            ofFloat = ObjectAnimator.ofFloat(this.textDimView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
        } else {
            ofFloat = ObjectAnimator.ofFloat(this.textDimView, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f});
        }
        ofFloat.addListener(new AnonymousClass15(z));
        ofFloat.setDuration(200);
        ofFloat.start();
    }

    private void showBrushSettings() {
        showPopup(new Runnable() {
            public void run() {
                boolean z = true;
                View access$3000 = PhotoPaintView.this.buttonForBrush(0, C0338R.drawable.paint_radial_preview, PhotoPaintView.this.currentBrush == 0);
                PhotoPaintView.this.popupLayout.addView(access$3000);
                LayoutParams layoutParams = (LayoutParams) access$3000.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = AndroidUtilities.dp(52.0f);
                access$3000.setLayoutParams(layoutParams);
                access$3000 = PhotoPaintView.this.buttonForBrush(PhotoPaintView.gallery_menu_done, C0338R.drawable.paint_elliptical_preview, PhotoPaintView.this.currentBrush == PhotoPaintView.gallery_menu_done);
                PhotoPaintView.this.popupLayout.addView(access$3000);
                layoutParams = (LayoutParams) access$3000.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = AndroidUtilities.dp(52.0f);
                access$3000.setLayoutParams(layoutParams);
                PhotoPaintView photoPaintView = PhotoPaintView.this;
                if (PhotoPaintView.this.currentBrush != PhotoPaintView.gallery_menu_undo) {
                    z = false;
                }
                View access$30002 = photoPaintView.buttonForBrush(PhotoPaintView.gallery_menu_undo, C0338R.drawable.paint_neon_preview, z);
                PhotoPaintView.this.popupLayout.addView(access$30002);
                layoutParams = (LayoutParams) access$30002.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = AndroidUtilities.dp(52.0f);
                access$30002.setLayoutParams(layoutParams);
            }
        }, this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    private void showMenuForEntity(EntityView entityView) {
        showPopup(new AnonymousClass19(entityView), entityView, 17, (int) ((entityView.getPosition().f2683x - ((float) (this.entitiesView.getWidth() / gallery_menu_undo))) * this.entitiesView.getScaleX()), ((int) (((entityView.getPosition().f2684y - ((((float) entityView.getHeight()) * entityView.getScale()) / 2.0f)) - ((float) (this.entitiesView.getHeight() / gallery_menu_undo))) * this.entitiesView.getScaleY())) - AndroidUtilities.dp(32.0f));
    }

    private void showPopup(Runnable runnable, View view, int i, int i2, int i3) {
        if (this.popupWindow == null || !this.popupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                this.popupLayout = new ActionBarPopupWindowLayout(getContext());
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() == 0 && PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing()) {
                            view.getHitRect(PhotoPaintView.this.popupRect);
                            if (!PhotoPaintView.this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                                PhotoPaintView.this.popupWindow.dismiss();
                            }
                        }
                        return false;
                    }
                });
                this.popupLayout.setDispatchKeyEventListener(new OnDispatchKeyEventListener() {
                    public void onDispatchKeyEvent(KeyEvent keyEvent) {
                        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing()) {
                            PhotoPaintView.this.popupWindow.dismiss();
                        }
                    }
                });
                this.popupLayout.setShowedFromBotton(true);
            }
            this.popupLayout.removeInnerViews();
            runnable.run();
            if (this.popupWindow == null) {
                this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                this.popupWindow.setAnimationEnabled(false);
                this.popupWindow.setAnimationStyle(C0338R.style.PopupAnimation);
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                this.popupWindow.setInputMethodMode(gallery_menu_undo);
                this.popupWindow.setSoftInputMode(0);
                this.popupWindow.getContentView().setFocusableInTouchMode(true);
                this.popupWindow.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss() {
                        PhotoPaintView.this.popupLayout.removeInnerViews();
                    }
                });
            }
            this.popupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), TLRPC.MESSAGE_FLAG_MEGAGROUP));
            this.popupWindow.setFocusable(true);
            this.popupWindow.showAtLocation(view, i, i2, i3);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
    }

    private void showTextSettings() {
        showPopup(new Runnable() {
            public void run() {
                View access$3300 = PhotoPaintView.this.buttonForText(true, LocaleController.getString("PaintOutlined", C0338R.string.PaintOutlined), PhotoPaintView.this.selectedStroke);
                PhotoPaintView.this.popupLayout.addView(access$3300);
                LayoutParams layoutParams = (LayoutParams) access$3300.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = AndroidUtilities.dp(48.0f);
                access$3300.setLayoutParams(layoutParams);
                View access$33002 = PhotoPaintView.this.buttonForText(false, LocaleController.getString("PaintRegular", C0338R.string.PaintRegular), !PhotoPaintView.this.selectedStroke);
                PhotoPaintView.this.popupLayout.addView(access$33002);
                layoutParams = (LayoutParams) access$33002.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = AndroidUtilities.dp(48.0f);
                access$33002.setLayoutParams(layoutParams);
            }
        }, this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    private Point startPositionRelativeToEntity(EntityView entityView) {
        if (entityView != null) {
            Point position = entityView.getPosition();
            return new Point(position.f2683x + 200.0f, position.f2684y + 200.0f);
        }
        Point centerPositionForEntity = centerPositionForEntity();
        while (true) {
            Object obj = null;
            for (int i = 0; i < this.entitiesView.getChildCount(); i += gallery_menu_done) {
                View childAt = this.entitiesView.getChildAt(i);
                if (childAt instanceof EntityView) {
                    position = ((EntityView) childAt).getPosition();
                    if (((float) Math.sqrt(Math.pow((double) (position.f2683x - centerPositionForEntity.f2683x), 2.0d) + Math.pow((double) (position.f2684y - centerPositionForEntity.f2684y), 2.0d))) < 100.0f) {
                        obj = gallery_menu_done;
                    }
                }
            }
            if (obj == null) {
                return centerPositionForEntity;
            }
            centerPositionForEntity = new Point(centerPositionForEntity.f2683x + 200.0f, centerPositionForEntity.f2684y + 200.0f);
        }
    }

    private void updateSettingsButton() {
        int i = C0338R.drawable.photo_paint_brush;
        if (this.currentEntityView != null) {
            if (this.currentEntityView instanceof StickerView) {
                i = C0338R.drawable.photo_flip;
            } else if (this.currentEntityView instanceof TextPaintView) {
                i = C0338R.drawable.photo_outline;
            }
            this.paintButton.setImageResource(C0338R.drawable.photo_paint);
        } else {
            this.paintButton.setImageResource(C0338R.drawable.photo_paint2);
        }
        this.colorPicker.setSettingsButtonImage(i);
    }

    private void updateStickersTitle() {
        if (this.stickersView != null && this.stickersView.getVisibility() == 0) {
            switch (this.stickersView.getCurrentType()) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    this.actionBar.setTitle(LocaleController.getString("PaintStickers", C0338R.string.PaintStickers));
                case gallery_menu_done /*1*/:
                    this.actionBar.setTitle(LocaleController.getString("Masks", C0338R.string.Masks));
                default:
            }
        }
    }

    public boolean allowInteraction(EntityView entityView) {
        return !this.editingText;
    }

    public void closeTextEnter(boolean z) {
        if (this.editingText && (this.currentEntityView instanceof TextPaintView)) {
            TextPaintView textPaintView = (TextPaintView) this.currentEntityView;
            this.undoItem.setVisibility(0);
            this.doneItem.setVisibility(8);
            this.actionBar.setTitle(LocaleController.getString("PaintDraw", C0338R.string.PaintDraw));
            this.toolsView.setVisibility(0);
            setColorPickerVisibilitySlide(true);
            AndroidUtilities.hideKeyboard(textPaintView.getFocusedView());
            textPaintView.getFocusedView().clearFocus();
            textPaintView.endEditing();
            if (!z) {
                textPaintView.setText(this.initialText);
            }
            if (textPaintView.getText().trim().length() == 0) {
                this.entitiesView.removeView(textPaintView);
                selectEntity(null);
            } else {
                textPaintView.setPosition(this.editedTextPosition);
                textPaintView.setRotation(this.editedTextRotation);
                textPaintView.setScale(this.editedTextScale);
                this.editedTextPosition = null;
                this.editedTextRotation = 0.0f;
                this.editedTextScale = 0.0f;
            }
            setTextDimVisibility(false, textPaintView);
            this.editingText = false;
            this.initialText = null;
            this.curtainView.setVisibility(8);
        }
    }

    public ActionBar getActionBar() {
        return this.actionBar;
    }

    public Bitmap getBitmap() {
        Bitmap resultBitmap = this.renderView.getResultBitmap();
        if (resultBitmap != null && this.entitiesView.entitiesCount() > 0) {
            Canvas canvas = new Canvas(resultBitmap);
            for (int i = 0; i < this.entitiesView.getChildCount(); i += gallery_menu_done) {
                View childAt = this.entitiesView.getChildAt(i);
                canvas.save();
                if (childAt instanceof EntityView) {
                    EntityView entityView = (EntityView) childAt;
                    canvas.translate(entityView.getPosition().f2683x, entityView.getPosition().f2684y);
                    canvas.scale(childAt.getScaleX(), childAt.getScaleY());
                    canvas.rotate(childAt.getRotation());
                    canvas.translate((float) ((-entityView.getWidth()) / gallery_menu_undo), (float) ((-entityView.getHeight()) / gallery_menu_undo));
                    if (childAt instanceof TextPaintView) {
                        Bitmap createBitmap = Bitmaps.createBitmap(childAt.getWidth(), childAt.getHeight(), Config.ARGB_8888);
                        Canvas canvas2 = new Canvas(createBitmap);
                        childAt.draw(canvas2);
                        canvas.drawBitmap(createBitmap, null, new Rect(0, 0, createBitmap.getWidth(), createBitmap.getHeight()), null);
                        try {
                            canvas2.setBitmap(null);
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                        createBitmap.recycle();
                    } else {
                        childAt.draw(canvas);
                    }
                }
                canvas.restore();
            }
        }
        return resultBitmap;
    }

    public TextView getCancelTextView() {
        return this.cancelTextView;
    }

    public ColorPicker getColorPicker() {
        return this.colorPicker;
    }

    public TextView getDoneTextView() {
        return this.doneTextView;
    }

    public ArrayList<InputDocument> getMasks() {
        ArrayList<InputDocument> arrayList = null;
        int childCount = this.entitiesView.getChildCount();
        int i = 0;
        while (i < childCount) {
            ArrayList<InputDocument> arrayList2;
            View childAt = this.entitiesView.getChildAt(i);
            if (childAt instanceof StickerView) {
                Document sticker = ((StickerView) childAt).getSticker();
                arrayList2 = arrayList == null ? new ArrayList() : arrayList;
                TL_inputDocument tL_inputDocument = new TL_inputDocument();
                tL_inputDocument.id = sticker.id;
                tL_inputDocument.access_hash = sticker.access_hash;
                arrayList2.add(tL_inputDocument);
            } else {
                arrayList2 = arrayList;
            }
            i += gallery_menu_done;
            arrayList = arrayList2;
        }
        return arrayList;
    }

    public FrameLayout getToolsView() {
        return this.toolsView;
    }

    public void init() {
        this.renderView.setVisibility(0);
        detectFaces();
    }

    public void maybeShowDismissalAlert(PhotoViewer photoViewer, Activity activity, Runnable runnable) {
        if (this.editingText) {
            closeTextEnter(false);
        } else if (this.pickingSticker) {
            closeStickersView();
        } else if (!hasChanges()) {
            runnable.run();
        } else if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(LocaleController.getString("DiscardChanges", C0338R.string.DiscardChanges));
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass12(runnable));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            photoViewer.showAlertDialog(builder);
        }
    }

    public void onBeganEntityDragging(EntityView entityView) {
        setColorPickerVisibilityFade(false);
    }

    public boolean onEntityLongClicked(EntityView entityView) {
        showMenuForEntity(entityView);
        return true;
    }

    public boolean onEntitySelected(EntityView entityView) {
        return selectEntity(entityView);
    }

    public void onFinishedEntityDragging(EntityView entityView) {
        setColorPickerVisibilityFade(true);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float height;
        float width;
        int i5 = i3 - i;
        int i6 = i4 - i2;
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
        int measuredHeight = this.actionBar.getMeasuredHeight();
        this.actionBar.layout(0, 0, this.actionBar.getMeasuredWidth(), measuredHeight);
        int dp = (AndroidUtilities.displaySize.y - currentActionBarHeight) - AndroidUtilities.dp(48.0f);
        if (this.bitmapToEdit != null) {
            height = isSidewardOrientation() ? (float) this.bitmapToEdit.getHeight() : (float) this.bitmapToEdit.getWidth();
            width = isSidewardOrientation() ? (float) this.bitmapToEdit.getWidth() : (float) this.bitmapToEdit.getHeight();
        } else {
            height = (float) i5;
            width = (float) ((i6 - currentActionBarHeight) - AndroidUtilities.dp(48.0f));
        }
        float f = (float) i5;
        height = ((float) Math.floor((double) ((f * width) / height))) > ((float) dp) ? (float) Math.floor((double) ((height * ((float) dp)) / width)) : f;
        currentActionBarHeight = (int) Math.ceil((double) ((i5 - this.renderView.getMeasuredWidth()) / gallery_menu_undo));
        int dp2 = ((((i6 - measuredHeight) - AndroidUtilities.dp(48.0f)) - this.renderView.getMeasuredHeight()) / gallery_menu_undo) + measuredHeight;
        this.renderView.layout(currentActionBarHeight, dp2, this.renderView.getMeasuredWidth() + currentActionBarHeight, this.renderView.getMeasuredHeight() + dp2);
        height /= this.paintingSize.width;
        this.entitiesView.setScaleX(height);
        this.entitiesView.setScaleY(height);
        this.entitiesView.layout(currentActionBarHeight, dp2, this.entitiesView.getMeasuredWidth() + currentActionBarHeight, this.entitiesView.getMeasuredHeight() + dp2);
        this.dimView.layout(0, measuredHeight, this.dimView.getMeasuredWidth(), this.dimView.getMeasuredHeight() + measuredHeight);
        this.selectionContainerView.layout(0, measuredHeight, this.selectionContainerView.getMeasuredWidth(), this.selectionContainerView.getMeasuredHeight() + measuredHeight);
        this.colorPicker.layout(0, measuredHeight, this.colorPicker.getMeasuredWidth(), this.colorPicker.getMeasuredHeight() + measuredHeight);
        this.toolsView.layout(0, i6 - this.toolsView.getMeasuredHeight(), this.toolsView.getMeasuredWidth(), i6);
        this.curtainView.layout(0, 0, i5, dp);
        if (this.stickersView != null) {
            this.stickersView.layout(0, measuredHeight, this.stickersView.getMeasuredWidth(), this.stickersView.getMeasuredHeight() + measuredHeight);
        }
        if (this.currentEntityView != null) {
            this.currentEntityView.updateSelectionView();
            this.currentEntityView.setOffset(this.entitiesView.getLeft() - this.selectionContainerView.getLeft(), this.entitiesView.getTop() - this.selectionContainerView.getTop());
        }
    }

    protected void onMeasure(int i, int i2) {
        float height;
        float width;
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        this.actionBar.measure(i, MeasureSpec.makeMeasureSpec(size2, TLRPC.MESSAGE_FLAG_MEGAGROUP));
        int currentActionBarHeight = AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight();
        int dp = currentActionBarHeight - AndroidUtilities.dp(48.0f);
        if (this.bitmapToEdit != null) {
            height = isSidewardOrientation() ? (float) this.bitmapToEdit.getHeight() : (float) this.bitmapToEdit.getWidth();
            width = isSidewardOrientation() ? (float) this.bitmapToEdit.getWidth() : (float) this.bitmapToEdit.getHeight();
        } else {
            height = (float) size;
            width = (float) ((size2 - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f));
        }
        float f = (float) size;
        float floor = (float) Math.floor((double) ((f * width) / height));
        if (floor > ((float) dp)) {
            floor = (float) dp;
            width = (float) Math.floor((double) ((height * floor) / width));
            height = floor;
        } else {
            height = floor;
            width = f;
        }
        this.renderView.measure(MeasureSpec.makeMeasureSpec((int) width, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((int) height, C0700C.ENCODING_PCM_32BIT));
        this.entitiesView.measure(MeasureSpec.makeMeasureSpec((int) this.paintingSize.width, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((int) this.paintingSize.height, C0700C.ENCODING_PCM_32BIT));
        this.dimView.measure(i, MeasureSpec.makeMeasureSpec(dp, TLRPC.MESSAGE_FLAG_MEGAGROUP));
        this.selectionContainerView.measure(i, MeasureSpec.makeMeasureSpec(dp, C0700C.ENCODING_PCM_32BIT));
        this.colorPicker.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(dp, C0700C.ENCODING_PCM_32BIT));
        this.toolsView.measure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C0700C.ENCODING_PCM_32BIT));
        if (this.stickersView != null) {
            this.stickersView.measure(i, MeasureSpec.makeMeasureSpec(currentActionBarHeight, C0700C.ENCODING_PCM_32BIT));
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getY() <= ((float) this.actionBar.getHeight())) {
            return false;
        }
        if (this.currentEntityView == null) {
            return true;
        }
        if (this.editingText) {
            closeTextEnter(true);
            return true;
        }
        selectEntity(null);
        return true;
    }

    public void shutdown() {
        this.renderView.shutdown();
        this.entitiesView.setVisibility(8);
        this.selectionContainerView.setVisibility(8);
        this.queue.postRunnable(new Runnable() {
            public void run() {
                Looper myLooper = Looper.myLooper();
                if (myLooper != null) {
                    myLooper.quit();
                }
            }
        });
    }
}
