package com.hanista.mobogram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.PointerIconCompat;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.ImageReceiver.ImageReceiverDelegate;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.PhotoEntry;
import com.hanista.mobogram.messenger.MediaController.SearchImage;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.AspectRatioFrameLayout;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.PlayerControl;
import com.hanista.mobogram.messenger.query.SharedMediaQuery;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.component.ProForwardActivity;
import com.hanista.mobogram.mobo.markers.MarkersActivity;
import com.hanista.mobogram.mobo.p000a.ArchiveUtil;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.BotInlineResult;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputPhoto;
import com.hanista.mobogram.tgnet.TLRPC.Photo;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageService;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.DrawerLayoutContainer;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.MentionsAdapter;
import com.hanista.mobogram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
import com.hanista.mobogram.ui.Components.AnimatedFileDrawable;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.ClippingImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.PhotoCropView;
import com.hanista.mobogram.ui.Components.PhotoCropView.PhotoCropViewDelegate;
import com.hanista.mobogram.ui.Components.PhotoFilterView;
import com.hanista.mobogram.ui.Components.PhotoPaintView;
import com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView;
import com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate;
import com.hanista.mobogram.ui.Components.PickerBottomLayoutViewer;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.Components.SeekBar;
import com.hanista.mobogram.ui.Components.SeekBar.SeekBarDelegate;
import com.hanista.mobogram.ui.Components.SizeNotifierFrameLayoutPhoto;
import com.hanista.mobogram.ui.Components.StickersAlert;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.Components.VideoPlayer.ExtractorRendererBuilder;
import com.hanista.mobogram.ui.Components.VideoPlayer.Listener;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class PhotoViewer implements OnDoubleTapListener, OnGestureListener, NotificationCenterDelegate {
    private static volatile PhotoViewer Instance = null;
    private static DecelerateInterpolator decelerateInterpolator = null;
    private static final int gallery_menu_caption_done = 9;
    private static final int gallery_menu_delete = 6;
    private static final int gallery_menu_drawing = 112;
    private static final int gallery_menu_goto_message = 113;
    private static final int gallery_menu_masks = 13;
    private static final int gallery_menu_mute = 12;
    private static final int gallery_menu_openin = 11;
    private static final int gallery_menu_proforward = 111;
    private static final int gallery_menu_rotate = 114;
    private static final int gallery_menu_save = 1;
    private static final int gallery_menu_send = 3;
    private static final int gallery_menu_share = 10;
    private static final int gallery_menu_showall = 2;
    private static Drawable[] progressDrawables;
    private static Paint progressPaint;
    private ActionBar actionBar;
    private Context actvityContext;
    private boolean allowMentions;
    private boolean allowShare;
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private ClippingImageView animatingImageView;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private float[][] animationValues;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private boolean attachedToWindow;
    private ArrayList<Photo> avatarsArr;
    private int avatarsDialogId;
    private BackgroundDrawable backgroundDrawable;
    private Paint blackPaint;
    private FrameLayout bottomLayout;
    private boolean canDragDown;
    private boolean canShowBottom;
    private boolean canZoom;
    private ActionBarMenuItem captionDoneItem;
    private PhotoViewerCaptionEnterView captionEditText;
    private TextView captionTextView;
    private TextView captionTextViewNew;
    private TextView captionTextViewOld;
    public ImageReceiver centerImage;
    private AnimatorSet changeModeAnimation;
    private boolean changingPage;
    private CheckBox checkImageView;
    private int classGuid;
    private FrameLayoutDrawer containerView;
    private ImageView cropItem;
    private AnimatorSet currentActionBarAnimation;
    private AnimatedFileDrawable currentAnimation;
    private BotInlineResult currentBotInlineResult;
    private long currentDialogId;
    private int currentEditMode;
    private FileLocation currentFileLocation;
    private String[] currentFileNames;
    private int currentIndex;
    private MessageObject currentMessageObject;
    private String currentPathObject;
    private PlaceProviderObject currentPlaceObject;
    private int currentRotation;
    private Bitmap currentThumb;
    private FileLocation currentUserAvatarLocation;
    private TextView dateTextView;
    private boolean deleteFilesOnDeleteMessage;
    private boolean disableShowCheck;
    private boolean discardTap;
    private boolean dontResetZoomOnFirstLayout;
    private boolean doubleTap;
    private float dragY;
    private boolean draggingDown;
    private ActionBarMenuItem drawingItem;
    private PickerBottomLayoutViewer editorDoneLayout;
    private boolean[] endReached;
    private GestureDetector gestureDetector;
    private PlaceProviderObject hideAfterAnimation;
    private AnimatorSet imageMoveAnimation;
    private ArrayList<MessageObject> imagesArr;
    private ArrayList<Object> imagesArrLocals;
    private ArrayList<FileLocation> imagesArrLocations;
    private ArrayList<Integer> imagesArrLocationsSizes;
    private ArrayList<MessageObject> imagesArrTemp;
    private HashMap<Integer, MessageObject>[] imagesByIds;
    private HashMap<Integer, MessageObject>[] imagesByIdsTemp;
    private DecelerateInterpolator interpolator;
    private boolean invalidCoords;
    private boolean isActionBarVisible;
    private boolean isFirstLoading;
    private boolean isGif;
    private boolean isPlaying;
    private boolean isVisible;
    private Object lastInsets;
    private String lastTitle;
    private ImageReceiver leftImage;
    private boolean loadingMoreImages;
    protected WakeLock mWakeLock;
    private ActionBarMenuItem masksItem;
    private float maxX;
    private float maxY;
    private LinearLayoutManager mentionLayoutManager;
    private AnimatorSet mentionListAnimation;
    private RecyclerListView mentionListView;
    private MentionsAdapter mentionsAdapter;
    private ActionBarMenuItem menuItem;
    private long mergeDialogId;
    private float minX;
    private float minY;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private ActionBarMenuItem muteItem;
    private boolean muteItemAvailable;
    private boolean muteVideo;
    private TextView nameTextView;
    private boolean needCaptionLayout;
    private boolean needSearchImageInArr;
    private boolean opennedFromMedia;
    private int originalOrientation;
    private ImageView paintItem;
    private Activity parentActivity;
    private ChatActivity parentChatActivity;
    private PhotoCropView photoCropView;
    private PhotoFilterView photoFilterView;
    private PhotoPaintView photoPaintView;
    private PickerBottomLayoutViewer pickerView;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale;
    private float pinchStartX;
    private float pinchStartY;
    private PhotoViewerProvider placeProvider;
    private boolean playerNeedsPrepare;
    private RadialProgressView[] radialProgressViews;
    private ImageReceiver rightImage;
    private ActionBarMenuItem rotateItem;
    private float scale;
    private Scroller scroller;
    private int sendPhotoType;
    private ImageView shareButton;
    private PlaceProviderObject showAfterAnimation;
    private int switchImageAfterAnimation;
    private boolean textureUploaded;
    private int totalImagesCount;
    private int totalImagesCountMerge;
    private long transitionAnimationStartTime;
    private float translationX;
    private float translationY;
    private ImageView tuneItem;
    private Runnable updateProgressRunnable;
    private VelocityTracker velocityTracker;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private ImageView videoPlayButton;
    private VideoPlayer videoPlayer;
    private FrameLayout videoPlayerControlFrameLayout;
    private SeekBar videoPlayerSeekbar;
    private TextView videoPlayerTime;
    private TextureView videoTextureView;
    private AlertDialog visibleDialog;
    private boolean wasLayout;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;
    private boolean zoomAnimation;
    private boolean zooming;

    public interface PhotoViewerProvider {
        boolean allowCaption();

        boolean cancelButtonPressed();

        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i);

        int getSelectedCount();

        Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i);

        boolean isPhotoChecked(int i);

        boolean scaleToFill();

        void sendButtonPressed(int i);

        void setPhotoChecked(int i);

        void updatePhotoAtIndex(int i);

        void willHidePhotoViewer();

        void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i);
    }

    public static class EmptyPhotoViewerProvider implements PhotoViewerProvider {
        public boolean allowCaption() {
            return true;
        }

        public boolean cancelButtonPressed() {
            return true;
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
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

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.1 */
    class C17741 implements Runnable {
        C17741() {
        }

        public void run() {
            if (!(PhotoViewer.this.videoPlayer == null || PhotoViewer.this.videoPlayerSeekbar == null || PhotoViewer.this.videoPlayerSeekbar.isDragging())) {
                PlayerControl playerControl = PhotoViewer.this.videoPlayer.getPlayerControl();
                PhotoViewer.this.videoPlayerSeekbar.setProgress(((float) playerControl.getCurrentPosition()) / ((float) playerControl.getDuration()));
                PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                PhotoViewer.this.updateVideoPlayerTime();
            }
            if (PhotoViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 100);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.22 */
    class AnonymousClass22 extends LinearLayoutManager {
        AnonymousClass22(Context context) {
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.29 */
    class AnonymousClass29 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$mode;

        /* renamed from: com.hanista.mobogram.ui.PhotoViewer.29.1 */
        class C17791 extends AnimatorListenerAdapterProxy {
            C17791() {
            }

            public void onAnimationStart(Animator animator) {
                PhotoViewer.this.pickerView.setVisibility(0);
                PhotoViewer.this.actionBar.setVisibility(0);
                if (PhotoViewer.this.needCaptionLayout) {
                    PhotoViewer.this.captionTextView.setVisibility(PhotoViewer.this.captionTextView.getTag() != null ? 0 : 4);
                }
                if (PhotoViewer.this.sendPhotoType == 0) {
                    PhotoViewer.this.checkImageView.setVisibility(0);
                }
            }
        }

        AnonymousClass29(int i) {
            this.val$mode = i;
        }

        public void onAnimationEnd(Animator animator) {
            if (PhotoViewer.this.currentEditMode == PhotoViewer.gallery_menu_save) {
                PhotoViewer.this.editorDoneLayout.setVisibility(8);
                PhotoViewer.this.photoCropView.setVisibility(8);
            } else if (PhotoViewer.this.currentEditMode == PhotoViewer.gallery_menu_showall) {
                PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoFilterView);
                PhotoViewer.this.photoFilterView = null;
            } else if (PhotoViewer.this.currentEditMode == PhotoViewer.gallery_menu_send) {
                PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoPaintView);
                PhotoViewer.this.photoPaintView = null;
            }
            PhotoViewer.this.imageMoveAnimation = null;
            PhotoViewer.this.currentEditMode = this.val$mode;
            PhotoViewer.this.animateToScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            PhotoViewer.this.animateToX = 0.0f;
            PhotoViewer.this.animateToY = 0.0f;
            PhotoViewer.this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
            PhotoViewer.this.containerView.invalidate();
            AnimatorSet animatorSet = new AnimatorSet();
            Collection arrayList = new ArrayList();
            float[] fArr = new float[PhotoViewer.gallery_menu_save];
            fArr[0] = 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, "translationY", fArr));
            fArr = new float[PhotoViewer.gallery_menu_save];
            fArr[0] = 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, "translationY", fArr));
            if (PhotoViewer.this.needCaptionLayout) {
                fArr = new float[PhotoViewer.gallery_menu_save];
                fArr[0] = 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, "translationY", fArr));
            }
            if (PhotoViewer.this.sendPhotoType == 0) {
                fArr = new float[PhotoViewer.gallery_menu_save];
                fArr[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.checkImageView, "alpha", fArr));
            }
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(200);
            animatorSet.addListener(new C17791());
            animatorSet.start();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.2 */
    class C17802 extends FrameLayout {
        private Runnable attachRunnable;

        /* renamed from: com.hanista.mobogram.ui.PhotoViewer.2.1 */
        class C17751 implements Runnable {
            C17751() {
            }

            public void run() {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.checkImageView.getLayoutParams();
                int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                float f = (rotation == PhotoViewer.gallery_menu_send || rotation == PhotoViewer.gallery_menu_save) ? 58.0f : 68.0f;
                layoutParams.topMargin = (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(f);
                PhotoViewer.this.checkImageView.setLayoutParams(layoutParams);
            }
        }

        C17802(Context context) {
            super(context);
        }

        public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
            if (keyEvent == null || keyEvent.getKeyCode() != 4 || keyEvent.getAction() != PhotoViewer.gallery_menu_save) {
                return super.dispatchKeyEventPreIme(keyEvent);
            }
            if (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible()) {
                PhotoViewer.this.closeCaptionEnter(false);
                return false;
            }
            PhotoViewer.getInstance().closePhoto(true, false);
            return true;
        }

        protected boolean drawChild(Canvas canvas, View view, long j) {
            boolean drawChild = super.drawChild(canvas, view, j);
            if (VERSION.SDK_INT >= 21 && view == PhotoViewer.this.animatingImageView && PhotoViewer.this.lastInsets != null) {
                WindowInsets windowInsets = (WindowInsets) PhotoViewer.this.lastInsets;
                canvas.drawRect(0.0f, (float) getMeasuredHeight(), (float) getMeasuredWidth(), (float) (windowInsets.getSystemWindowInsetBottom() + getMeasuredHeight()), PhotoViewer.this.blackPaint);
            }
            return drawChild;
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            PhotoViewer.this.attachedToWindow = true;
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            PhotoViewer.this.attachedToWindow = false;
            PhotoViewer.this.wasLayout = false;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return PhotoViewer.this.isVisible && super.onInterceptTouchEvent(motionEvent);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            PhotoViewer.this.animatingImageView.layout(0, 0, PhotoViewer.this.animatingImageView.getMeasuredWidth(), PhotoViewer.this.animatingImageView.getMeasuredHeight());
            PhotoViewer.this.containerView.layout(0, 0, PhotoViewer.this.containerView.getMeasuredWidth(), PhotoViewer.this.containerView.getMeasuredHeight());
            PhotoViewer.this.wasLayout = true;
            if (z) {
                if (!PhotoViewer.this.dontResetZoomOnFirstLayout) {
                    PhotoViewer.this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    PhotoViewer.this.translationX = 0.0f;
                    PhotoViewer.this.translationY = 0.0f;
                    PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                }
                if (PhotoViewer.this.checkImageView != null) {
                    PhotoViewer.this.checkImageView.post(new C17751());
                }
            }
            if (PhotoViewer.this.dontResetZoomOnFirstLayout) {
                PhotoViewer.this.setScaleToFill();
                PhotoViewer.this.dontResetZoomOnFirstLayout = false;
            }
        }

        protected void onMeasure(int i, int i2) {
            int systemWindowInsetRight;
            int size = MeasureSpec.getSize(i);
            int size2 = MeasureSpec.getSize(i2);
            if (VERSION.SDK_INT >= 21 && PhotoViewer.this.lastInsets != null) {
                WindowInsets windowInsets = (WindowInsets) PhotoViewer.this.lastInsets;
                if (AndroidUtilities.incorrectDisplaySizeFix) {
                    if (size2 > AndroidUtilities.displaySize.y) {
                        size2 = AndroidUtilities.displaySize.y;
                    }
                    size2 += AndroidUtilities.statusBarHeight;
                }
                size2 -= windowInsets.getSystemWindowInsetBottom();
                systemWindowInsetRight = size - windowInsets.getSystemWindowInsetRight();
            } else if (size2 > AndroidUtilities.displaySize.y) {
                size2 = AndroidUtilities.displaySize.y;
                systemWindowInsetRight = size;
            } else {
                systemWindowInsetRight = size;
            }
            setMeasuredDimension(systemWindowInsetRight, size2);
            ViewGroup.LayoutParams layoutParams = PhotoViewer.this.animatingImageView.getLayoutParams();
            PhotoViewer.this.animatingImageView.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(layoutParams.height, TLRPC.MESSAGE_FLAG_MEGAGROUP));
            PhotoViewer.this.containerView.measure(MeasureSpec.makeMeasureSpec(systemWindowInsetRight, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(size2, C0700C.ENCODING_PCM_32BIT));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return PhotoViewer.this.isVisible && PhotoViewer.this.onTouchEvent(motionEvent);
        }

        public ActionMode startActionModeForChild(View view, Callback callback, int i) {
            if (VERSION.SDK_INT >= 23) {
                View findViewById = PhotoViewer.this.parentActivity.findViewById(16908290);
                if (findViewById instanceof ViewGroup) {
                    try {
                        return ((ViewGroup) findViewById).startActionModeForChild(view, callback, i);
                    } catch (Throwable th) {
                        FileLog.m18e("tmessages", th);
                    }
                }
            }
            return super.startActionModeForChild(view, callback, i);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.31 */
    class AnonymousClass31 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$mode;

        /* renamed from: com.hanista.mobogram.ui.PhotoViewer.31.1 */
        class C17811 extends AnimatorListenerAdapterProxy {
            C17811() {
            }

            public void onAnimationEnd(Animator animator) {
                PhotoViewer.this.imageMoveAnimation = null;
                PhotoViewer.this.currentEditMode = AnonymousClass31.this.val$mode;
                PhotoViewer.this.animateToScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                PhotoViewer.this.animateToX = 0.0f;
                PhotoViewer.this.animateToY = 0.0f;
                PhotoViewer.this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                PhotoViewer.this.containerView.invalidate();
            }

            public void onAnimationStart(Animator animator) {
                PhotoViewer.this.editorDoneLayout.setVisibility(0);
                PhotoViewer.this.photoCropView.setVisibility(0);
            }
        }

        AnonymousClass31(int i) {
            this.val$mode = i;
        }

        public void onAnimationEnd(Animator animator) {
            PhotoViewer.this.changeModeAnimation = null;
            PhotoViewer.this.pickerView.setVisibility(8);
            if (PhotoViewer.this.needCaptionLayout) {
                PhotoViewer.this.captionTextView.setVisibility(4);
            }
            if (PhotoViewer.this.sendPhotoType == 0) {
                PhotoViewer.this.checkImageView.setVisibility(8);
            }
            Bitmap bitmap = PhotoViewer.this.centerImage.getBitmap();
            if (bitmap != null) {
                PhotoViewer.this.photoCropView.setBitmap(bitmap, PhotoViewer.this.centerImage.getOrientation(), PhotoViewer.this.sendPhotoType != PhotoViewer.gallery_menu_save);
                int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                float access$1000 = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                float access$1100 = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                float access$9000 = ((float) PhotoViewer.this.getContainerViewWidth(PhotoViewer.gallery_menu_save)) / ((float) bitmapWidth);
                float access$9100 = ((float) PhotoViewer.this.getContainerViewHeight(PhotoViewer.gallery_menu_save)) / ((float) bitmapHeight);
                if (access$1000 <= access$1100) {
                    access$1100 = access$1000;
                }
                if (access$9000 <= access$9100) {
                    access$9100 = access$9000;
                }
                PhotoViewer.this.animateToScale = access$9100 / access$1100;
                PhotoViewer.this.animateToX = 0.0f;
                PhotoViewer.this.animateToY = (float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / PhotoViewer.gallery_menu_showall : 0) + (-AndroidUtilities.dp(24.0f)));
                PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                PhotoViewer.this.zoomAnimation = true;
            }
            PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
            AnimatorSet access$6700 = PhotoViewer.this.imageMoveAnimation;
            Animator[] animatorArr = new Animator[PhotoViewer.gallery_menu_send];
            float[] fArr = new float[PhotoViewer.gallery_menu_showall];
            fArr[0] = (float) AndroidUtilities.dp(48.0f);
            fArr[PhotoViewer.gallery_menu_save] = 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this.editorDoneLayout, "translationY", fArr);
            animatorArr[PhotoViewer.gallery_menu_save] = ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            animatorArr[PhotoViewer.gallery_menu_showall] = ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            access$6700.playTogether(animatorArr);
            PhotoViewer.this.imageMoveAnimation.setDuration(200);
            PhotoViewer.this.imageMoveAnimation.addListener(new C17811());
            PhotoViewer.this.imageMoveAnimation.start();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.34 */
    class AnonymousClass34 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$mode;

        /* renamed from: com.hanista.mobogram.ui.PhotoViewer.34.1 */
        class C17831 extends AnimatorListenerAdapterProxy {
            C17831() {
            }

            public void onAnimationEnd(Animator animator) {
                PhotoViewer.this.photoFilterView.init();
                PhotoViewer.this.imageMoveAnimation = null;
                PhotoViewer.this.currentEditMode = AnonymousClass34.this.val$mode;
                PhotoViewer.this.animateToScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                PhotoViewer.this.animateToX = 0.0f;
                PhotoViewer.this.animateToY = 0.0f;
                PhotoViewer.this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                PhotoViewer.this.containerView.invalidate();
            }

            public void onAnimationStart(Animator animator) {
            }
        }

        AnonymousClass34(int i) {
            this.val$mode = i;
        }

        public void onAnimationEnd(Animator animator) {
            PhotoViewer.this.changeModeAnimation = null;
            PhotoViewer.this.pickerView.setVisibility(8);
            PhotoViewer.this.actionBar.setVisibility(8);
            if (PhotoViewer.this.needCaptionLayout) {
                PhotoViewer.this.captionTextView.setVisibility(4);
            }
            if (PhotoViewer.this.sendPhotoType == 0) {
                PhotoViewer.this.checkImageView.setVisibility(8);
            }
            if (PhotoViewer.this.centerImage.getBitmap() != null) {
                int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                float access$1000 = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                float access$1100 = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                float access$9000 = ((float) PhotoViewer.this.getContainerViewWidth(PhotoViewer.gallery_menu_showall)) / ((float) bitmapWidth);
                float access$9100 = ((float) PhotoViewer.this.getContainerViewHeight(PhotoViewer.gallery_menu_showall)) / ((float) bitmapHeight);
                if (access$1000 <= access$1100) {
                    access$1100 = access$1000;
                }
                if (access$9000 <= access$9100) {
                    access$9100 = access$9000;
                }
                PhotoViewer.this.animateToScale = access$9100 / access$1100;
                PhotoViewer.this.animateToX = 0.0f;
                PhotoViewer.this.animateToY = (float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / PhotoViewer.gallery_menu_showall : 0) + (-AndroidUtilities.dp(62.0f)));
                PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                PhotoViewer.this.zoomAnimation = true;
            }
            PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
            AnimatorSet access$6700 = PhotoViewer.this.imageMoveAnimation;
            Animator[] animatorArr = new Animator[PhotoViewer.gallery_menu_showall];
            animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this, "animationValue", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            float[] fArr = new float[PhotoViewer.gallery_menu_showall];
            fArr[0] = (float) AndroidUtilities.dp(126.0f);
            fArr[PhotoViewer.gallery_menu_save] = 0.0f;
            animatorArr[PhotoViewer.gallery_menu_save] = ObjectAnimator.ofFloat(PhotoViewer.this.photoFilterView.getToolsView(), "translationY", fArr);
            access$6700.playTogether(animatorArr);
            PhotoViewer.this.imageMoveAnimation.setDuration(200);
            PhotoViewer.this.imageMoveAnimation.addListener(new C17831());
            PhotoViewer.this.imageMoveAnimation.start();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.37 */
    class AnonymousClass37 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$mode;

        /* renamed from: com.hanista.mobogram.ui.PhotoViewer.37.1 */
        class C17851 extends AnimatorListenerAdapterProxy {
            C17851() {
            }

            public void onAnimationEnd(Animator animator) {
                PhotoViewer.this.photoPaintView.init();
                PhotoViewer.this.imageMoveAnimation = null;
                PhotoViewer.this.currentEditMode = AnonymousClass37.this.val$mode;
                PhotoViewer.this.animateToScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                PhotoViewer.this.animateToX = 0.0f;
                PhotoViewer.this.animateToY = 0.0f;
                PhotoViewer.this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                PhotoViewer.this.updateMinMax(PhotoViewer.this.scale);
                PhotoViewer.this.containerView.invalidate();
            }

            public void onAnimationStart(Animator animator) {
            }
        }

        AnonymousClass37(int i) {
            this.val$mode = i;
        }

        public void onAnimationEnd(Animator animator) {
            PhotoViewer.this.changeModeAnimation = null;
            PhotoViewer.this.pickerView.setVisibility(8);
            if (PhotoViewer.this.needCaptionLayout) {
                PhotoViewer.this.captionTextView.setVisibility(4);
            }
            if (PhotoViewer.this.sendPhotoType == 0) {
                PhotoViewer.this.checkImageView.setVisibility(8);
            }
            if (PhotoViewer.this.centerImage.getBitmap() != null) {
                int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                float access$1000 = ((float) PhotoViewer.this.getContainerViewWidth()) / ((float) bitmapWidth);
                float access$1100 = ((float) PhotoViewer.this.getContainerViewHeight()) / ((float) bitmapHeight);
                float access$9000 = ((float) PhotoViewer.this.getContainerViewWidth(PhotoViewer.gallery_menu_send)) / ((float) bitmapWidth);
                float access$9100 = ((float) PhotoViewer.this.getContainerViewHeight(PhotoViewer.gallery_menu_send)) / ((float) bitmapHeight);
                if (access$1000 <= access$1100) {
                    access$1100 = access$1000;
                }
                if (access$9000 <= access$9100) {
                    access$9100 = access$9000;
                }
                PhotoViewer.this.animateToScale = access$9100 / access$1100;
                PhotoViewer.this.animateToX = 0.0f;
                PhotoViewer.this.animateToY = (float) (((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f))) / PhotoViewer.gallery_menu_showall);
                PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                PhotoViewer.this.zoomAnimation = true;
            }
            PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
            AnimatorSet access$6700 = PhotoViewer.this.imageMoveAnimation;
            r3 = new Animator[4];
            float[] fArr = new float[PhotoViewer.gallery_menu_showall];
            fArr[0] = (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
            fArr[PhotoViewer.gallery_menu_save] = 0.0f;
            r3[PhotoViewer.gallery_menu_save] = ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getColorPicker(), "translationX", fArr);
            fArr = new float[PhotoViewer.gallery_menu_showall];
            fArr[0] = (float) AndroidUtilities.dp(126.0f);
            fArr[PhotoViewer.gallery_menu_save] = 0.0f;
            r3[PhotoViewer.gallery_menu_showall] = ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getToolsView(), "translationY", fArr);
            ActionBar actionBar = PhotoViewer.this.photoPaintView.getActionBar();
            String str = "translationY";
            float[] fArr2 = new float[PhotoViewer.gallery_menu_showall];
            fArr2[0] = (float) ((-ActionBar.getCurrentActionBarHeight()) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
            fArr2[PhotoViewer.gallery_menu_save] = 0.0f;
            r3[PhotoViewer.gallery_menu_send] = ObjectAnimator.ofFloat(actionBar, str, fArr2);
            access$6700.playTogether(r3);
            PhotoViewer.this.imageMoveAnimation.setDuration(200);
            PhotoViewer.this.imageMoveAnimation.addListener(new C17851());
            PhotoViewer.this.imageMoveAnimation.start();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.3 */
    class C17863 implements OnApplyWindowInsetsListener {
        C17863() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            PhotoViewer.this.lastInsets = windowInsets;
            PhotoViewer.this.windowView.requestLayout();
            return windowInsets.consumeSystemWindowInsets();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.40 */
    class AnonymousClass40 implements Runnable {
        final /* synthetic */ ArrayList val$photos;

        AnonymousClass40(ArrayList arrayList) {
            this.val$photos = arrayList;
        }

        public void run() {
            if (PhotoViewer.this.containerView != null && PhotoViewer.this.windowView != null) {
                if (VERSION.SDK_INT >= 18) {
                    PhotoViewer.this.containerView.setLayerType(0, null);
                }
                PhotoViewer.this.animationInProgress = 0;
                PhotoViewer.this.transitionAnimationStartTime = 0;
                PhotoViewer.this.setImages();
                PhotoViewer.this.containerView.invalidate();
                PhotoViewer.this.animatingImageView.setVisibility(8);
                if (PhotoViewer.this.showAfterAnimation != null) {
                    PhotoViewer.this.showAfterAnimation.imageReceiver.setVisible(true, true);
                }
                if (PhotoViewer.this.hideAfterAnimation != null) {
                    PhotoViewer.this.hideAfterAnimation.imageReceiver.setVisible(false, true);
                }
                if (this.val$photos != null && PhotoViewer.this.sendPhotoType != PhotoViewer.gallery_menu_send) {
                    if (VERSION.SDK_INT >= 21) {
                        PhotoViewer.this.windowLayoutParams.flags = -2147417856;
                    } else {
                        PhotoViewer.this.windowLayoutParams.flags = 0;
                    }
                    PhotoViewer.this.windowLayoutParams.softInputMode = 272;
                    ((WindowManager) PhotoViewer.this.parentActivity.getSystemService("window")).updateViewLayout(PhotoViewer.this.windowView, PhotoViewer.this.windowLayoutParams);
                    PhotoViewer.this.windowView.setFocusable(true);
                    PhotoViewer.this.containerView.setFocusable(true);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.42 */
    class AnonymousClass42 implements Runnable {
        final /* synthetic */ AnimatorSet val$animatorSet;

        AnonymousClass42(AnimatorSet animatorSet) {
            this.val$animatorSet = animatorSet;
        }

        public void run() {
            NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded, NotificationCenter.mediaDidLoaded, NotificationCenter.dialogPhotosLoaded});
            NotificationCenter.getInstance().setAnimationInProgress(true);
            this.val$animatorSet.start();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.43 */
    class AnonymousClass43 implements Runnable {
        final /* synthetic */ PlaceProviderObject val$object;

        AnonymousClass43(PlaceProviderObject placeProviderObject) {
            this.val$object = placeProviderObject;
        }

        public void run() {
            PhotoViewer.this.disableShowCheck = false;
            this.val$object.imageReceiver.setVisible(false, true);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.45 */
    class AnonymousClass45 implements Runnable {
        final /* synthetic */ PlaceProviderObject val$object;

        AnonymousClass45(PlaceProviderObject placeProviderObject) {
            this.val$object = placeProviderObject;
        }

        public void run() {
            if (VERSION.SDK_INT >= 18) {
                PhotoViewer.this.containerView.setLayerType(0, null);
            }
            PhotoViewer.this.animationInProgress = 0;
            PhotoViewer.this.onPhotoClosed(this.val$object);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.47 */
    class AnonymousClass47 implements Runnable {
        final /* synthetic */ PlaceProviderObject val$object;

        AnonymousClass47(PlaceProviderObject placeProviderObject) {
            this.val$object = placeProviderObject;
        }

        public void run() {
            if (PhotoViewer.this.containerView != null) {
                if (VERSION.SDK_INT >= 18) {
                    PhotoViewer.this.containerView.setLayerType(0, null);
                }
                PhotoViewer.this.animationInProgress = 0;
                PhotoViewer.this.onPhotoClosed(this.val$object);
                PhotoViewer.this.containerView.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                PhotoViewer.this.containerView.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.4 */
    class C17914 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.PhotoViewer.4.1 */
        class C17871 implements OnClickListener {
            C17871() {
            }

            public void onClick(View view) {
                PhotoViewer.this.deleteFilesOnDeleteMessage = !PhotoViewer.this.deleteFilesOnDeleteMessage;
                ((CheckBoxCell) view).setChecked(PhotoViewer.this.deleteFilesOnDeleteMessage, true);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.PhotoViewer.4.2 */
        class C17882 implements DialogInterface.OnClickListener {
            C17882() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                EncryptedChat encryptedChat = null;
                if (PhotoViewer.this.imagesArr.isEmpty()) {
                    if (!PhotoViewer.this.avatarsArr.isEmpty() && PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.avatarsArr.size()) {
                        boolean z;
                        InputPhoto tL_inputPhoto;
                        int access$4500;
                        Photo photo = (Photo) PhotoViewer.this.avatarsArr.get(PhotoViewer.this.currentIndex);
                        FileLocation fileLocation = (FileLocation) PhotoViewer.this.imagesArrLocations.get(PhotoViewer.this.currentIndex);
                        Photo photo2 = photo instanceof TL_photoEmpty ? null : photo;
                        if (PhotoViewer.this.currentUserAvatarLocation != null) {
                            if (photo2 != null) {
                                Iterator it = photo2.sizes.iterator();
                                while (it.hasNext()) {
                                    PhotoSize photoSize = (PhotoSize) it.next();
                                    if (photoSize.location.local_id == PhotoViewer.this.currentUserAvatarLocation.local_id && photoSize.location.volume_id == PhotoViewer.this.currentUserAvatarLocation.volume_id) {
                                        z = true;
                                        break;
                                    }
                                }
                            } else if (fileLocation.local_id == PhotoViewer.this.currentUserAvatarLocation.local_id && fileLocation.volume_id == PhotoViewer.this.currentUserAvatarLocation.volume_id) {
                                z = true;
                                if (z) {
                                    MessagesController.getInstance().deleteUserPhoto(null);
                                    PhotoViewer.this.closePhoto(false, false);
                                } else if (photo2 != null) {
                                    tL_inputPhoto = new TL_inputPhoto();
                                    tL_inputPhoto.id = photo2.id;
                                    tL_inputPhoto.access_hash = photo2.access_hash;
                                    MessagesController.getInstance().deleteUserPhoto(tL_inputPhoto);
                                    MessagesStorage.getInstance().clearUserPhoto(PhotoViewer.this.avatarsDialogId, photo2.id);
                                    PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                                    PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                                    PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                                    if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
                                        access$4500 = PhotoViewer.this.currentIndex;
                                        if (access$4500 >= PhotoViewer.this.avatarsArr.size()) {
                                            access$4500 = PhotoViewer.this.avatarsArr.size() - 1;
                                        }
                                        PhotoViewer.this.currentIndex = -1;
                                        PhotoViewer.this.setImageIndex(access$4500, true);
                                        return;
                                    }
                                    PhotoViewer.this.closePhoto(false, false);
                                }
                            }
                        }
                        z = false;
                        if (z) {
                            MessagesController.getInstance().deleteUserPhoto(null);
                            PhotoViewer.this.closePhoto(false, false);
                        } else if (photo2 != null) {
                            tL_inputPhoto = new TL_inputPhoto();
                            tL_inputPhoto.id = photo2.id;
                            tL_inputPhoto.access_hash = photo2.access_hash;
                            MessagesController.getInstance().deleteUserPhoto(tL_inputPhoto);
                            MessagesStorage.getInstance().clearUserPhoto(PhotoViewer.this.avatarsDialogId, photo2.id);
                            PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                            PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                            PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                            if (PhotoViewer.this.imagesArrLocations.isEmpty()) {
                                access$4500 = PhotoViewer.this.currentIndex;
                                if (access$4500 >= PhotoViewer.this.avatarsArr.size()) {
                                    access$4500 = PhotoViewer.this.avatarsArr.size() - 1;
                                }
                                PhotoViewer.this.currentIndex = -1;
                                PhotoViewer.this.setImageIndex(access$4500, true);
                                return;
                            }
                            PhotoViewer.this.closePhoto(false, false);
                        }
                    }
                } else if (PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size()) {
                    MessageObject messageObject = (MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                    if (messageObject.isSent()) {
                        ArrayList arrayList;
                        PhotoViewer.this.closePhoto(false, false);
                        List arrayList2 = new ArrayList();
                        arrayList2.add(Integer.valueOf(messageObject.getId()));
                        if (((int) messageObject.getDialogId()) != 0 || messageObject.messageOwner.random_id == 0) {
                            arrayList = null;
                        } else {
                            arrayList = new ArrayList();
                            arrayList.add(Long.valueOf(messageObject.messageOwner.random_id));
                            encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
                        }
                        MessagesController.getInstance().deleteMessages(arrayList2, arrayList, encryptedChat, messageObject.messageOwner.to_id.channel_id, PhotoViewer.this.deleteFilesOnDeleteMessage);
                        if (messageObject.getDialogId() == ((long) UserConfig.getClientUserId())) {
                            ArchiveUtil.m263a(arrayList2);
                        }
                    }
                }
            }
        }

        C17914() {
        }

        public boolean canOpenMenu() {
            if (PhotoViewer.this.currentMessageObject != null) {
                if (FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner).exists()) {
                    return true;
                }
            } else if (PhotoViewer.this.currentFileLocation != null) {
                if (FileLoader.getPathToAttach(PhotoViewer.this.currentFileLocation, PhotoViewer.this.avatarsDialogId != 0).exists()) {
                    return true;
                }
            }
            return false;
        }

        public void onItemClick(int i) {
            boolean z = true;
            if (i == -1) {
                if (PhotoViewer.this.needCaptionLayout && (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible())) {
                    PhotoViewer.this.closeCaptionEnter(false);
                } else {
                    PhotoViewer.this.closePhoto(true, false);
                }
            } else if (i == PhotoViewer.gallery_menu_save) {
                if (VERSION.SDK_INT < 23 || PhotoViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                    if (PhotoViewer.this.currentMessageObject != null) {
                        r0 = FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
                    } else if (PhotoViewer.this.currentFileLocation != null) {
                        r0 = FileLoader.getPathToAttach(PhotoViewer.this.currentFileLocation, PhotoViewer.this.avatarsDialogId != 0);
                    } else {
                        r0 = null;
                    }
                    if (r0 == null || !r0.exists()) {
                        Builder builder = new Builder(PhotoViewer.this.parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                        builder.setMessage(LocaleController.getString("PleaseDownload", C0338R.string.PleaseDownload));
                        PhotoViewer.this.showAlertDialog(builder);
                        return;
                    }
                    int i2;
                    String file = r0.toString();
                    Context access$600 = PhotoViewer.this.parentActivity;
                    if (PhotoViewer.this.currentMessageObject == null || !(PhotoViewer.this.currentMessageObject.isVideo() || PhotoViewer.this.currentMessageObject.isGif() || PhotoViewer.this.currentMessageObject.isNewGif())) {
                        i2 = 0;
                    }
                    MediaController.m84a(file, access$600, i2, null, null);
                    return;
                }
                Activity access$6002 = PhotoViewer.this.parentActivity;
                String[] strArr = new String[PhotoViewer.gallery_menu_save];
                strArr[0] = "android.permission.WRITE_EXTERNAL_STORAGE";
                access$6002.requestPermissions(strArr, 4);
            } else if (i == PhotoViewer.gallery_menu_showall) {
                if (PhotoViewer.this.opennedFromMedia) {
                    PhotoViewer.this.closePhoto(true, false);
                } else if (PhotoViewer.this.currentDialogId != 0) {
                    PhotoViewer.this.disableShowCheck = true;
                    Bundle bundle = new Bundle();
                    bundle.putLong("dialog_id", PhotoViewer.this.currentDialogId);
                    BaseFragment mediaActivity = new MediaActivity(bundle);
                    if (PhotoViewer.this.parentChatActivity != null) {
                        mediaActivity.setChatInfo(PhotoViewer.this.parentChatActivity.getCurrentChatInfo());
                    }
                    PhotoViewer.this.closePhoto(false, false);
                    ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(mediaActivity, false, true);
                }
            } else if (i == PhotoViewer.gallery_menu_send) {
            } else {
                if (i == PhotoViewer.gallery_menu_delete) {
                    if (PhotoViewer.this.parentActivity != null) {
                        Builder builder2 = new Builder(PhotoViewer.this.parentActivity);
                        if (PhotoViewer.this.currentMessageObject != null && PhotoViewer.this.currentMessageObject.isVideo()) {
                            builder2.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", C0338R.string.AreYouSureDeleteVideo, new Object[0]));
                        } else if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isGif()) {
                            builder2.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", C0338R.string.AreYouSureDeletePhoto, new Object[0]));
                        } else {
                            builder2.setMessage(LocaleController.formatString("AreYouSure", C0338R.string.AreYouSure, new Object[0]));
                        }
                        if (!PhotoViewer.this.imagesArr.isEmpty()) {
                            if (PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size()) {
                                MessageObject messageObject = (MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                                messageObject.checkMediaExistance();
                                boolean z2 = messageObject.mediaExists;
                                PhotoViewer.this.deleteFilesOnDeleteMessage = MoboConstants.aE;
                                if (z2) {
                                    View frameLayout = new FrameLayout(PhotoViewer.this.getParentActivity());
                                    if (VERSION.SDK_INT >= 21) {
                                        frameLayout.setPadding(0, AndroidUtilities.dp(8.0f), 0, 0);
                                    }
                                    View createDeleteFileCheckBox = AndroidUtilities.createDeleteFileCheckBox(PhotoViewer.this.getParentActivity());
                                    frameLayout.addView(createDeleteFileCheckBox, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
                                    createDeleteFileCheckBox.setOnClickListener(new C17871());
                                    builder2.setView(frameLayout);
                                }
                            } else {
                                return;
                            }
                        }
                        builder2.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder2.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C17882());
                        builder2.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        PhotoViewer.this.showAlertDialog(builder2);
                    }
                } else if (i == PhotoViewer.gallery_menu_caption_done) {
                    PhotoViewer.this.closeCaptionEnter(true);
                } else if (i == PhotoViewer.gallery_menu_share) {
                    PhotoViewer.this.onSharePressed();
                } else if (i == PhotoViewer.gallery_menu_openin) {
                    try {
                        AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                        PhotoViewer.this.closePhoto(false, false);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                } else if (i == PhotoViewer.gallery_menu_mute) {
                    PhotoViewer photoViewer = PhotoViewer.this;
                    if (PhotoViewer.this.muteVideo) {
                        z = false;
                    }
                    photoViewer.muteVideo = z;
                    if (PhotoViewer.this.videoPlayer != null) {
                        PhotoViewer.this.videoPlayer.setMute(PhotoViewer.this.muteVideo);
                    }
                    if (PhotoViewer.this.muteVideo) {
                        PhotoViewer.this.actionBar.setTitle(LocaleController.getString("AttachGif", C0338R.string.AttachGif));
                        PhotoViewer.this.muteItem.setIcon((int) C0338R.drawable.volume_off);
                        return;
                    }
                    PhotoViewer.this.actionBar.setTitle(LocaleController.getString("AttachVideo", C0338R.string.AttachVideo));
                    PhotoViewer.this.muteItem.setIcon((int) C0338R.drawable.volume_on);
                } else if (i == PhotoViewer.gallery_menu_masks) {
                    if (PhotoViewer.this.parentActivity != null && PhotoViewer.this.currentMessageObject != null && PhotoViewer.this.currentMessageObject.messageOwner.media != null && PhotoViewer.this.currentMessageObject.messageOwner.media.photo != null) {
                        new StickersAlert(PhotoViewer.this.parentActivity, PhotoViewer.this.currentMessageObject.messageOwner.media.photo).show();
                    }
                } else if (i == PhotoViewer.gallery_menu_proforward) {
                    if (!PhotoViewer.this.imagesArr.isEmpty() && PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size()) {
                        PhotoViewer.this.editAndShareMessage((MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex));
                    }
                } else if (i == PhotoViewer.gallery_menu_drawing) {
                    if (PhotoViewer.this.parentChatActivity != null || (PhotoViewer.this.placeProvider != null && (PhotoViewer.this.placeProvider instanceof ChatActivity))) {
                        if (PhotoViewer.this.currentMessageObject != null) {
                            r0 = FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
                        } else if (PhotoViewer.this.currentFileLocation != null) {
                            r0 = FileLoader.getPathToAttach(PhotoViewer.this.currentFileLocation, PhotoViewer.this.avatarsDialogId != 0);
                        } else {
                            if (PhotoViewer.this.imagesArrLocals != null && PhotoViewer.this.imagesArrLocals.size() > PhotoViewer.this.currentIndex) {
                                Object obj = PhotoViewer.this.imagesArrLocals.get(PhotoViewer.this.currentIndex);
                                if (obj instanceof PhotoEntry) {
                                    r0 = new File(((PhotoEntry) obj).path);
                                } else if (obj instanceof SearchImage) {
                                    r0 = new File(((SearchImage) obj).localUrl);
                                }
                            }
                            r0 = null;
                        }
                        Intent intent = new Intent(PhotoViewer.this.parentActivity, MarkersActivity.class);
                        File generatePictureInCachePath = AndroidUtilities.generatePictureInCachePath();
                        if (r0 != null && generatePictureInCachePath != null) {
                            intent.putExtra("output", generatePictureInCachePath.getPath());
                            intent.putExtra("backgroundImage", Uri.fromFile(r0));
                            if (PhotoViewer.this.parentChatActivity != null) {
                                PhotoViewer.this.parentChatActivity.currentPicturePath = generatePictureInCachePath.getAbsolutePath();
                                if (PhotoViewer.this.currentMessageObject != null) {
                                    PhotoViewer.this.parentChatActivity.currentPictureCaption = PhotoViewer.this.currentMessageObject.caption;
                                }
                            } else if (PhotoViewer.this.placeProvider instanceof ChatActivity) {
                                ((ChatActivity) PhotoViewer.this.placeProvider).currentPicturePath = generatePictureInCachePath.getAbsolutePath();
                                if (PhotoViewer.this.currentMessageObject != null) {
                                    ((ChatActivity) PhotoViewer.this.placeProvider).currentPictureCaption = PhotoViewer.this.currentMessageObject.caption;
                                }
                            }
                            if (PhotoViewer.this.placeProvider != null && (PhotoViewer.this.placeProvider instanceof PhotoPickerActivity)) {
                                ((PhotoPickerActivity) PhotoViewer.this.placeProvider).getDelegate().actionButtonPressed(true);
                                ((PhotoPickerActivity) PhotoViewer.this.placeProvider).finishFragment();
                            }
                            PhotoViewer.this.parentActivity.startActivityForResult(intent, 0);
                            PhotoViewer.this.closePhoto(true, false);
                        }
                    }
                } else if (i == PhotoViewer.gallery_menu_goto_message) {
                    if (!PhotoViewer.this.imagesArr.isEmpty() && PhotoViewer.this.currentIndex >= 0 && PhotoViewer.this.currentIndex < PhotoViewer.this.imagesArr.size()) {
                        PhotoViewer.this.gotoMessage((MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex));
                    }
                } else if (i == PhotoViewer.gallery_menu_rotate) {
                    PhotoViewer.this.rotate();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.50 */
    class AnonymousClass50 implements Runnable {
        final /* synthetic */ int val$count;

        AnonymousClass50(int i) {
            this.val$count = i;
        }

        public void run() {
            PhotoViewer.this.redraw(this.val$count + PhotoViewer.gallery_menu_save);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.5 */
    class C17925 implements OnClickListener {
        C17925() {
        }

        public void onClick(View view) {
            if (PhotoViewer.this.cropItem.getVisibility() == 0) {
                PhotoViewer.this.openCaptionEnter();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.6 */
    class C17936 implements OnClickListener {
        C17936() {
        }

        public void onClick(View view) {
            if (PhotoViewer.this.cropItem.getVisibility() == 0) {
                PhotoViewer.this.openCaptionEnter();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.7 */
    class C17947 implements OnClickListener {
        C17947() {
        }

        public void onClick(View view) {
            PhotoViewer.this.onSharePressed();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.8 */
    class C17958 implements SeekBarDelegate {
        C17958() {
        }

        public void onSeekBarDrag(float f) {
            if (PhotoViewer.this.videoPlayer != null) {
                PhotoViewer.this.videoPlayer.getPlayerControl().seekTo((int) (((float) PhotoViewer.this.videoPlayer.getDuration()) * f));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.9 */
    class C17969 extends FrameLayout {
        C17969(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(48.0f), 0.0f);
            PhotoViewer.this.videoPlayerSeekbar.draw(canvas);
            canvas.restore();
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            float f = 0.0f;
            if (PhotoViewer.this.videoPlayer != null) {
                PlayerControl playerControl = PhotoViewer.this.videoPlayer.getPlayerControl();
                f = ((float) playerControl.getCurrentPosition()) / ((float) playerControl.getDuration());
            }
            PhotoViewer.this.videoPlayerSeekbar.setProgress(f);
        }

        protected void onMeasure(int i, int i2) {
            long j = 0;
            super.onMeasure(i, i2);
            if (PhotoViewer.this.videoPlayer != null) {
                long duration = PhotoViewer.this.videoPlayer.getDuration();
                if (duration != -1) {
                    j = duration;
                }
            }
            j /= 1000;
            PhotoViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) - ((int) Math.ceil((double) PhotoViewer.this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(j / 60), Long.valueOf(j % 60), Long.valueOf(j / 60), Long.valueOf(j % 60)})))), getMeasuredHeight());
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int x = (int) motionEvent.getX();
            x = (int) motionEvent.getY();
            if (!PhotoViewer.this.videoPlayerSeekbar.onTouch(motionEvent.getAction(), motionEvent.getX() - ((float) AndroidUtilities.dp(48.0f)), motionEvent.getY())) {
                return super.onTouchEvent(motionEvent);
            }
            getParent().requestDisallowInterceptTouchEvent(true);
            invalidate();
            return true;
        }
    }

    private class BackgroundDrawable extends ColorDrawable {
        private Runnable drawRunnable;

        public BackgroundDrawable(int i) {
            super(i);
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (getAlpha() != 0 && this.drawRunnable != null) {
                this.drawRunnable.run();
                this.drawRunnable = null;
            }
        }

        public void setAlpha(int i) {
            if (PhotoViewer.this.parentActivity instanceof LaunchActivity) {
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) PhotoViewer.this.parentActivity).drawerLayoutContainer;
                boolean z = (PhotoViewer.this.isVisible && i == NalUnitUtil.EXTENDED_SAR) ? false : true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            super.setAlpha(i);
        }
    }

    private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {
        private Paint paint;

        public FrameLayoutDrawer(Context context) {
            super(context);
            this.paint = new Paint();
            setWillNotDraw(false);
            this.paint.setColor(855638016);
        }

        protected boolean drawChild(Canvas canvas, View view, long j) {
            return view != PhotoViewer.this.aspectRatioFrameLayout && super.drawChild(canvas, view, j);
        }

        protected void onDraw(Canvas canvas) {
            PhotoViewer.getInstance().onDraw(canvas);
            if (VERSION.SDK_INT >= 21 && AndroidUtilities.statusBarHeight != 0) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.statusBarHeight, this.paint);
            }
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            int emojiPadding = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
            for (int i5 = 0; i5 < childCount; i5 += PhotoViewer.gallery_menu_save) {
                View childAt = getChildAt(i5);
                if (childAt.getVisibility() != 8) {
                    int i6;
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                    int measuredWidth = childAt.getMeasuredWidth();
                    int measuredHeight = childAt.getMeasuredHeight();
                    int i7 = layoutParams.gravity;
                    if (i7 == -1) {
                        i7 = 51;
                    }
                    int i8 = i7 & PhotoViewer.gallery_menu_drawing;
                    switch ((i7 & 7) & 7) {
                        case PhotoViewer.gallery_menu_save /*1*/:
                            i7 = ((((i3 - i) - measuredWidth) / PhotoViewer.gallery_menu_showall) + layoutParams.leftMargin) - layoutParams.rightMargin;
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
                            i6 = (((((i4 - emojiPadding) - i2) - measuredHeight) / PhotoViewer.gallery_menu_showall) + layoutParams.topMargin) - layoutParams.bottomMargin;
                            break;
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                            i6 = layoutParams.topMargin;
                            break;
                        case 80:
                            i6 = (((i4 - emojiPadding) - i2) - measuredHeight) - layoutParams.bottomMargin;
                            break;
                        default:
                            i6 = layoutParams.topMargin;
                            break;
                    }
                    if (childAt == PhotoViewer.this.mentionListView) {
                        i6 = (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible() || PhotoViewer.this.captionEditText.getEmojiPadding() != 0) ? i6 - PhotoViewer.this.captionEditText.getMeasuredHeight() : i6 + AndroidUtilities.dp(400.0f);
                    } else if (childAt == PhotoViewer.this.captionEditText) {
                        if (!(PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible() || PhotoViewer.this.captionEditText.getEmojiPadding() != 0)) {
                            i6 += AndroidUtilities.dp(400.0f);
                        }
                    } else if (childAt == PhotoViewer.this.pickerView || childAt == PhotoViewer.this.captionTextViewNew || childAt == PhotoViewer.this.captionTextViewOld) {
                        if (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible()) {
                            i6 += AndroidUtilities.dp(400.0f);
                        }
                    } else if (PhotoViewer.this.captionEditText.isPopupView(childAt)) {
                        i6 = AndroidUtilities.isInMultiwindow ? (PhotoViewer.this.captionEditText.getTop() - childAt.getMeasuredHeight()) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) : PhotoViewer.this.captionEditText.getBottom();
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
            measureChildWithMargins(PhotoViewer.this.captionEditText, i, 0, i2, 0);
            int measuredHeight = PhotoViewer.this.captionEditText.getMeasuredHeight();
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3 += PhotoViewer.gallery_menu_save) {
                View childAt = getChildAt(i3);
                if (!(childAt.getVisibility() == 8 || childAt == PhotoViewer.this.captionEditText)) {
                    if (!PhotoViewer.this.captionEditText.isPopupView(childAt)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    } else if (!AndroidUtilities.isInMultiwindow) {
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, C0700C.ENCODING_PCM_32BIT));
                    } else if (AndroidUtilities.isTablet()) {
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), (size2 - measuredHeight) - AndroidUtilities.statusBarHeight), C0700C.ENCODING_PCM_32BIT));
                    } else {
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((size2 - measuredHeight) - AndroidUtilities.statusBarHeight, C0700C.ENCODING_PCM_32BIT));
                    }
                }
            }
        }
    }

    public static class PlaceProviderObject {
        public int clipBottomAddition;
        public int clipTopAddition;
        public int dialogId;
        public ImageReceiver imageReceiver;
        public int index;
        public View parentView;
        public int radius;
        public float scale;
        public int size;
        public Bitmap thumb;
        public int viewX;
        public int viewY;

        public PlaceProviderObject() {
            this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
    }

    private class RadialProgressView {
        private float alpha;
        private float animatedAlphaValue;
        private float animatedProgressValue;
        private float animationProgressStart;
        private int backgroundState;
        private float currentProgress;
        private long currentProgressTime;
        private long lastUpdateTime;
        private View parent;
        private int previousBackgroundState;
        private RectF progressRect;
        private float radOffset;
        private float scale;
        private int size;

        public RadialProgressView(Context context, View view) {
            this.lastUpdateTime = 0;
            this.radOffset = 0.0f;
            this.currentProgress = 0.0f;
            this.animationProgressStart = 0.0f;
            this.currentProgressTime = 0;
            this.animatedProgressValue = 0.0f;
            this.progressRect = new RectF();
            this.backgroundState = -1;
            this.parent = null;
            this.size = AndroidUtilities.dp(64.0f);
            this.previousBackgroundState = -2;
            this.animatedAlphaValue = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            this.alpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            if (PhotoViewer.decelerateInterpolator == null) {
                PhotoViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                PhotoViewer.progressPaint = new Paint(PhotoViewer.gallery_menu_save);
                PhotoViewer.progressPaint.setStyle(Style.STROKE);
                PhotoViewer.progressPaint.setStrokeCap(Cap.ROUND);
                PhotoViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
                PhotoViewer.progressPaint.setColor(-1);
            }
            this.parent = view;
        }

        private void updateAnimation() {
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (this.animatedProgressValue != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                this.radOffset += ((float) (360 * j)) / 3000.0f;
                float f = this.currentProgress - this.animationProgressStart;
                if (f > 0.0f) {
                    this.currentProgressTime += j;
                    if (this.currentProgressTime >= 300) {
                        this.animatedProgressValue = this.currentProgress;
                        this.animationProgressStart = this.currentProgress;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = (f * PhotoViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / BitmapDescriptorFactory.HUE_MAGENTA)) + this.animationProgressStart;
                    }
                }
                this.parent.invalidate();
            }
            if (this.animatedProgressValue >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.previousBackgroundState != -2) {
                this.animatedAlphaValue -= ((float) j) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousBackgroundState = -2;
                }
                this.parent.invalidate();
            }
        }

        public void onDraw(Canvas canvas) {
            Drawable drawable;
            int i = (int) (((float) this.size) * this.scale);
            int access$1000 = (PhotoViewer.this.getContainerViewWidth() - i) / PhotoViewer.gallery_menu_showall;
            int access$1100 = (PhotoViewer.this.getContainerViewHeight() - i) / PhotoViewer.gallery_menu_showall;
            if (this.previousBackgroundState >= 0 && this.previousBackgroundState < 4) {
                drawable = PhotoViewer.progressDrawables[this.previousBackgroundState];
                if (drawable != null) {
                    drawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                    drawable.setBounds(access$1000, access$1100, access$1000 + i, access$1100 + i);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState >= 0 && this.backgroundState < 4) {
                drawable = PhotoViewer.progressDrawables[this.backgroundState];
                if (drawable != null) {
                    if (this.previousBackgroundState != -2) {
                        drawable.setAlpha((int) (((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.animatedAlphaValue) * 255.0f) * this.alpha));
                    } else {
                        drawable.setAlpha((int) (this.alpha * 255.0f));
                    }
                    drawable.setBounds(access$1000, access$1100, access$1000 + i, access$1100 + i);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState == 0 || this.backgroundState == PhotoViewer.gallery_menu_save || this.previousBackgroundState == 0 || this.previousBackgroundState == PhotoViewer.gallery_menu_save) {
                int dp = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    PhotoViewer.progressPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                } else {
                    PhotoViewer.progressPaint.setAlpha((int) (this.alpha * 255.0f));
                }
                this.progressRect.set((float) (access$1000 + dp), (float) (access$1100 + dp), (float) ((access$1000 + i) - dp), (float) ((i + access$1100) - dp));
                canvas.drawArc(this.progressRect, this.radOffset - 0.049804688f, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, PhotoViewer.progressPaint);
                updateAnimation();
            }
        }

        public void setAlpha(float f) {
            this.alpha = f;
        }

        public void setBackgroundState(int i, boolean z) {
            this.lastUpdateTime = System.currentTimeMillis();
            if (!z || this.backgroundState == i) {
                this.previousBackgroundState = -2;
            } else {
                this.previousBackgroundState = this.backgroundState;
                this.animatedAlphaValue = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            }
            this.backgroundState = i;
            this.parent.invalidate();
        }

        public void setProgress(float f, boolean z) {
            if (z) {
                this.animationProgressStart = this.animatedProgressValue;
            } else {
                this.animatedProgressValue = f;
                this.animationProgressStart = f;
            }
            this.currentProgress = f;
            this.currentProgressTime = 0;
        }

        public void setScale(float f) {
            this.scale = f;
        }
    }

    static {
        decelerateInterpolator = null;
        progressPaint = null;
        Instance = null;
    }

    public PhotoViewer() {
        this.isActionBarVisible = true;
        this.backgroundDrawable = new BackgroundDrawable(Theme.MSG_TEXT_COLOR);
        this.blackPaint = new Paint();
        this.radialProgressViews = new RadialProgressView[gallery_menu_send];
        this.canShowBottom = true;
        this.updateProgressRunnable = new C17741();
        this.animationValues = (float[][]) Array.newInstance(Float.TYPE, new int[]{gallery_menu_showall, 8});
        this.animationInProgress = 0;
        this.transitionAnimationStartTime = 0;
        this.animationEndRunnable = null;
        this.disableShowCheck = false;
        this.leftImage = new ImageReceiver();
        this.centerImage = new ImageReceiver();
        this.rightImage = new ImageReceiver();
        this.currentFileNames = new String[gallery_menu_send];
        this.currentThumb = null;
        this.endReached = new boolean[]{false, true};
        this.draggingDown = false;
        this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.interpolator = new DecelerateInterpolator(1.5f);
        this.pinchStartScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.canZoom = true;
        this.changingPage = false;
        this.zooming = false;
        this.moving = false;
        this.doubleTap = false;
        this.invalidCoords = false;
        this.canDragDown = true;
        this.zoomAnimation = false;
        this.discardTap = false;
        this.switchImageAfterAnimation = 0;
        this.velocityTracker = null;
        this.scroller = null;
        this.imagesArrTemp = new ArrayList();
        HashMap[] hashMapArr = new HashMap[gallery_menu_showall];
        hashMapArr[0] = new HashMap();
        hashMapArr[gallery_menu_save] = new HashMap();
        this.imagesByIdsTemp = hashMapArr;
        this.imagesArr = new ArrayList();
        hashMapArr = new HashMap[gallery_menu_showall];
        hashMapArr[0] = new HashMap();
        hashMapArr[gallery_menu_save] = new HashMap();
        this.imagesByIds = hashMapArr;
        this.imagesArrLocations = new ArrayList();
        this.avatarsArr = new ArrayList();
        this.imagesArrLocationsSizes = new ArrayList();
        this.imagesArrLocals = new ArrayList();
        this.currentUserAvatarLocation = null;
        this.originalOrientation = -100;
        this.blackPaint.setColor(Theme.MSG_TEXT_COLOR);
    }

    private void animateTo(float f, float f2, float f3, boolean z) {
        animateTo(f, f2, f3, z, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    }

    private void animateTo(float f, float f2, float f3, boolean z, int i) {
        if (this.scale != f || this.translationX != f2 || this.translationY != f3) {
            this.zoomAnimation = z;
            this.animateToScale = f;
            this.animateToX = f2;
            this.animateToY = f3;
            this.animationStartTime = System.currentTimeMillis();
            this.imageMoveAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.imageMoveAnimation;
            Animator[] animatorArr = new Animator[gallery_menu_save];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            animatorSet.playTogether(animatorArr);
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) i);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animator) {
                    PhotoViewer.this.imageMoveAnimation = null;
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    private void applyCurrentEditMode() {
        Bitmap bitmap;
        Collection collection;
        if (this.currentEditMode == gallery_menu_save) {
            bitmap = this.photoCropView.getBitmap();
            collection = null;
        } else if (this.currentEditMode == gallery_menu_showall) {
            bitmap = this.photoFilterView.getBitmap();
            collection = null;
        } else if (this.currentEditMode == gallery_menu_send) {
            Bitmap bitmap2 = this.photoPaintView.getBitmap();
            Object masks = this.photoPaintView.getMasks();
            bitmap = bitmap2;
        } else {
            collection = null;
            bitmap = null;
        }
        if (bitmap != null) {
            TLObject scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
            if (scaleAndSaveImage != null) {
                Object obj = this.imagesArrLocals.get(this.currentIndex);
                TLObject scaleAndSaveImage2;
                if (obj instanceof PhotoEntry) {
                    PhotoEntry photoEntry = (PhotoEntry) obj;
                    photoEntry.imagePath = FileLoader.getPathToAttach(scaleAndSaveImage, true).toString();
                    scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), 70, false, 101, 101);
                    if (scaleAndSaveImage2 != null) {
                        photoEntry.thumbPath = FileLoader.getPathToAttach(scaleAndSaveImage2, true).toString();
                    }
                    if (collection != null) {
                        photoEntry.stickers.addAll(collection);
                    }
                } else if (obj instanceof SearchImage) {
                    SearchImage searchImage = (SearchImage) obj;
                    searchImage.imagePath = FileLoader.getPathToAttach(scaleAndSaveImage, true).toString();
                    scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN), 70, false, 101, 101);
                    if (scaleAndSaveImage2 != null) {
                        searchImage.thumbPath = FileLoader.getPathToAttach(scaleAndSaveImage2, true).toString();
                    }
                    if (collection != null) {
                        searchImage.stickers.addAll(collection);
                    }
                }
                if (this.sendPhotoType == 0 && this.placeProvider != null) {
                    this.placeProvider.updatePhotoAtIndex(this.currentIndex);
                    if (!this.placeProvider.isPhotoChecked(this.currentIndex)) {
                        this.placeProvider.setPhotoChecked(this.currentIndex);
                        this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), true);
                        updateSelectedCount();
                    }
                }
                if (this.currentEditMode == gallery_menu_save) {
                    float rectSizeX = this.photoCropView.getRectSizeX() / ((float) getContainerViewWidth());
                    float rectSizeY = this.photoCropView.getRectSizeY() / ((float) getContainerViewHeight());
                    if (rectSizeX <= rectSizeY) {
                        rectSizeX = rectSizeY;
                    }
                    this.scale = rectSizeX;
                    this.translationX = (this.photoCropView.getRectX() + (this.photoCropView.getRectSizeX() / 2.0f)) - ((float) (getContainerViewWidth() / gallery_menu_showall));
                    this.translationY = (this.photoCropView.getRectY() + (this.photoCropView.getRectSizeY() / 2.0f)) - ((float) (getContainerViewHeight() / gallery_menu_showall));
                    this.zoomAnimation = true;
                }
                this.centerImage.setParentView(null);
                this.centerImage.setOrientation(0, true);
                this.centerImage.setImageBitmap(bitmap);
                this.centerImage.setParentView(this.containerView);
            }
        }
    }

    private boolean checkAnimation() {
        if (this.animationInProgress != 0 && Math.abs(this.transitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            if (this.animationEndRunnable != null) {
                this.animationEndRunnable.run();
                this.animationEndRunnable = null;
            }
            this.animationInProgress = 0;
        }
        return this.animationInProgress != 0;
    }

    private void checkMinMax(boolean z) {
        float f = this.translationX;
        float f2 = this.translationY;
        updateMinMax(this.scale);
        if (this.translationX < this.minX) {
            f = this.minX;
        } else if (this.translationX > this.maxX) {
            f = this.maxX;
        }
        if (this.translationY < this.minY) {
            f2 = this.minY;
        } else if (this.translationY > this.maxY) {
            f2 = this.maxY;
        }
        animateTo(this.scale, f, f2, z);
    }

    private void checkProgress(int i, boolean z) {
        if (this.currentFileNames[i] != null) {
            boolean z2;
            int i2 = this.currentIndex;
            if (i == gallery_menu_save) {
                i2 += gallery_menu_save;
            } else if (i == gallery_menu_showall) {
                i2--;
            }
            File file = null;
            if (this.currentMessageObject != null) {
                MessageObject messageObject = (MessageObject) this.imagesArr.get(i2);
                if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                    file = new File(messageObject.messageOwner.attachPath);
                    if (!file.exists()) {
                        file = null;
                    }
                }
                if (file == null) {
                    file = FileLoader.getPathToMessage(messageObject.messageOwner);
                }
                z2 = messageObject.isVideo() || isGifToPlay(messageObject);
            } else if (this.currentBotInlineResult != null) {
                File pathToAttach;
                boolean z3;
                BotInlineResult botInlineResult = (BotInlineResult) this.imagesArrLocals.get(i2);
                if (botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(botInlineResult.document)) {
                    pathToAttach = botInlineResult.document != null ? FileLoader.getPathToAttach(botInlineResult.document) : botInlineResult.content_url != null ? new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(botInlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content_url, "mp4")) : null;
                    z3 = true;
                } else if (botInlineResult.document != null) {
                    pathToAttach = new File(FileLoader.getInstance().getDirectory(gallery_menu_send), this.currentFileNames[i]);
                    z3 = false;
                } else if (botInlineResult.photo != null) {
                    pathToAttach = new File(FileLoader.getInstance().getDirectory(0), this.currentFileNames[i]);
                    z3 = false;
                } else {
                    pathToAttach = null;
                    z3 = false;
                }
                if (pathToAttach == null || !pathToAttach.exists()) {
                    pathToAttach = new File(FileLoader.getInstance().getDirectory(4), this.currentFileNames[i]);
                }
                boolean z4 = z3;
                file = pathToAttach;
                z2 = z4;
            } else if (this.currentFileLocation != null) {
                file = FileLoader.getPathToAttach((FileLocation) this.imagesArrLocations.get(i2), this.avatarsDialogId != 0);
                z2 = false;
            } else {
                if (this.currentPathObject != null) {
                    file = new File(FileLoader.getInstance().getDirectory(gallery_menu_send), this.currentFileNames[i]);
                    if (!file.exists()) {
                        file = new File(FileLoader.getInstance().getDirectory(4), this.currentFileNames[i]);
                        z2 = false;
                    }
                }
                z2 = false;
            }
            if (file == null || !file.exists()) {
                if (!z2) {
                    this.radialProgressViews[i].setBackgroundState(0, z);
                } else if (FileLoader.getInstance().isLoadingFile(this.currentFileNames[i])) {
                    this.radialProgressViews[i].setBackgroundState(gallery_menu_save, false);
                } else {
                    this.radialProgressViews[i].setBackgroundState(gallery_menu_showall, false);
                }
                Float fileProgress = ImageLoader.getInstance().getFileProgress(this.currentFileNames[i]);
                if (fileProgress == null) {
                    fileProgress = Float.valueOf(0.0f);
                }
                this.radialProgressViews[i].setProgress(fileProgress.floatValue(), false);
            } else if (z2) {
                this.radialProgressViews[i].setBackgroundState(gallery_menu_send, z);
            } else {
                this.radialProgressViews[i].setBackgroundState(-1, z);
            }
            if (i == 0) {
                if (!this.imagesArrLocals.isEmpty() || (!(this.currentFileNames[0] == null || this.radialProgressViews[0].backgroundState == 0) || isGifToPlay())) {
                    this.canZoom = true;
                } else {
                    this.canZoom = true;
                }
                return;
            }
            return;
        }
        this.radialProgressViews[i].setBackgroundState(-1, z);
    }

    private void closeCaptionEnter(boolean z) {
        if (this.currentIndex >= 0 && this.currentIndex < this.imagesArrLocals.size()) {
            Object obj = this.imagesArrLocals.get(this.currentIndex);
            if (z) {
                if (obj instanceof PhotoEntry) {
                    ((PhotoEntry) obj).caption = this.captionEditText.getFieldCharSequence();
                } else if (obj instanceof SearchImage) {
                    ((SearchImage) obj).caption = this.captionEditText.getFieldCharSequence();
                }
                if (!(this.captionEditText.getFieldCharSequence().length() == 0 || this.placeProvider.isPhotoChecked(this.currentIndex))) {
                    this.placeProvider.setPhotoChecked(this.currentIndex);
                    this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), true);
                    updateSelectedCount();
                }
            }
            this.cropItem.setVisibility(0);
            setDrawingItemVisibility(0);
            if (VERSION.SDK_INT >= 16) {
                this.paintItem.setVisibility(0);
                this.tuneItem.setVisibility(0);
            }
            if (this.sendPhotoType == 0) {
                this.checkImageView.setVisibility(0);
            }
            this.captionDoneItem.setVisibility(8);
            this.pickerView.setVisibility(0);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.captionEditText.getLayoutParams();
            layoutParams.bottomMargin = -AndroidUtilities.dp(400.0f);
            this.captionEditText.setLayoutParams(layoutParams);
            layoutParams = (FrameLayout.LayoutParams) this.mentionListView.getLayoutParams();
            layoutParams.bottomMargin = -AndroidUtilities.dp(400.0f);
            this.mentionListView.setLayoutParams(layoutParams);
            if (this.lastTitle != null) {
                this.actionBar.setTitle(this.lastTitle);
                this.lastTitle = null;
            }
            updateCaptionTextForCurrentPhoto(obj);
            setCurrentCaption(this.captionEditText.getFieldCharSequence());
            if (this.captionEditText.isPopupShowing()) {
                this.captionEditText.hidePopup();
            } else {
                this.captionEditText.closeKeyboard();
            }
        }
    }

    private void editAndShareMessage(MessageObject messageObject) {
        messageObject.generateThumbs(false);
        BaseFragment proForwardActivity = new ProForwardActivity(messageObject);
        closePhoto(true, false);
        ((LaunchActivity) this.parentActivity).presentFragment(proForwardActivity);
    }

    private int getAdditionX() {
        return (this.currentEditMode == 0 || this.currentEditMode == gallery_menu_send) ? 0 : AndroidUtilities.dp(14.0f);
    }

    private int getAdditionY() {
        int i = 0;
        int currentActionBarHeight;
        if (this.currentEditMode == gallery_menu_send) {
            currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
            if (VERSION.SDK_INT >= 21) {
                i = AndroidUtilities.statusBarHeight;
            }
            return i + currentActionBarHeight;
        } else if (this.currentEditMode == 0) {
            return 0;
        } else {
            currentActionBarHeight = AndroidUtilities.dp(14.0f);
            if (VERSION.SDK_INT >= 21) {
                i = AndroidUtilities.statusBarHeight;
            }
            return i + currentActionBarHeight;
        }
    }

    private int getContainerViewHeight() {
        return getContainerViewHeight(this.currentEditMode);
    }

    private int getContainerViewHeight(int i) {
        int i2 = AndroidUtilities.displaySize.y;
        if (i == 0 && VERSION.SDK_INT >= 21) {
            i2 += AndroidUtilities.statusBarHeight;
        }
        return i == gallery_menu_save ? i2 - AndroidUtilities.dp(76.0f) : i == gallery_menu_showall ? i2 - AndroidUtilities.dp(154.0f) : i == gallery_menu_send ? i2 - (AndroidUtilities.dp(48.0f) + ActionBar.getCurrentActionBarHeight()) : i2;
    }

    private int getContainerViewWidth() {
        return getContainerViewWidth(this.currentEditMode);
    }

    private int getContainerViewWidth(int i) {
        int width = this.containerView.getWidth();
        return (i == 0 || i == gallery_menu_send) ? width : width - AndroidUtilities.dp(28.0f);
    }

    private FileLocation getFileLocation(int i, int[] iArr) {
        if (i < 0) {
            return null;
        }
        if (this.imagesArrLocations.isEmpty()) {
            if (!this.imagesArr.isEmpty()) {
                if (i >= this.imagesArr.size()) {
                    return null;
                }
                MessageObject messageObject = (MessageObject) this.imagesArr.get(i);
                PhotoSize closestPhotoSizeWithSize;
                if (messageObject.messageOwner instanceof TL_messageService) {
                    if (messageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                        return messageObject.messageOwner.action.newUserPhoto.photo_big;
                    }
                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        iArr[0] = closestPhotoSizeWithSize.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                        return closestPhotoSizeWithSize.location;
                    }
                    iArr[0] = -1;
                } else if (((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) && messageObject.messageOwner.media.photo != null) || ((messageObject.messageOwner.media instanceof TL_messageMediaWebPage) && messageObject.messageOwner.media.webpage != null)) {
                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        iArr[0] = closestPhotoSizeWithSize.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                        return closestPhotoSizeWithSize.location;
                    }
                    iArr[0] = -1;
                } else if (!(messageObject.getDocument() == null || messageObject.getDocument().thumb == null)) {
                    iArr[0] = messageObject.getDocument().thumb.size;
                    if (iArr[0] == 0) {
                        iArr[0] = -1;
                    }
                    return messageObject.getDocument().thumb.location;
                }
            }
            return null;
        } else if (i >= this.imagesArrLocations.size()) {
            return null;
        } else {
            iArr[0] = ((Integer) this.imagesArrLocationsSizes.get(i)).intValue();
            return (FileLocation) this.imagesArrLocations.get(i);
        }
    }

    private String getFileName(int i) {
        if (i < 0) {
            return null;
        }
        if (this.imagesArrLocations.isEmpty() && this.imagesArr.isEmpty()) {
            if (!this.imagesArrLocals.isEmpty()) {
                if (i >= this.imagesArrLocals.size()) {
                    return null;
                }
                Object obj = this.imagesArrLocals.get(i);
                if (obj instanceof SearchImage) {
                    SearchImage searchImage = (SearchImage) obj;
                    if (searchImage.document != null) {
                        return FileLoader.getAttachFileName(searchImage.document);
                    }
                    if (!(searchImage.type == gallery_menu_save || searchImage.localUrl == null || searchImage.localUrl.length() <= 0)) {
                        File file = new File(searchImage.localUrl);
                        if (file.exists()) {
                            return file.getName();
                        }
                        searchImage.localUrl = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    return Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg");
                } else if (obj instanceof BotInlineResult) {
                    BotInlineResult botInlineResult = (BotInlineResult) obj;
                    if (botInlineResult.document != null) {
                        return FileLoader.getAttachFileName(botInlineResult.document);
                    }
                    if (botInlineResult.photo != null) {
                        return FileLoader.getAttachFileName(FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize()));
                    }
                    if (botInlineResult.content_url != null) {
                        return Utilities.MD5(botInlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content_url, "jpg");
                    }
                }
            }
        } else if (this.imagesArrLocations.isEmpty()) {
            if (!this.imagesArr.isEmpty()) {
                return i >= this.imagesArr.size() ? null : FileLoader.getMessageFileName(((MessageObject) this.imagesArr.get(i)).messageOwner);
            }
        } else if (i >= this.imagesArrLocations.size()) {
            return null;
        } else {
            FileLocation fileLocation = (FileLocation) this.imagesArrLocations.get(i);
            return fileLocation.volume_id + "_" + fileLocation.local_id + ".jpg";
        }
        return null;
    }

    public static PhotoViewer getInstance() {
        PhotoViewer photoViewer = Instance;
        if (photoViewer == null) {
            synchronized (PhotoViewer.class) {
                photoViewer = Instance;
                if (photoViewer == null) {
                    photoViewer = new PhotoViewer();
                    Instance = photoViewer;
                }
            }
        }
        return photoViewer;
    }

    private void goToNext() {
        float f = 0.0f;
        if (this.scale != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            f = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / gallery_menu_showall)) * this.scale;
        }
        this.switchImageAfterAnimation = gallery_menu_save;
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - f) - ((float) (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / gallery_menu_showall)), this.translationY, false);
    }

    private void goToPrev() {
        float f = 0.0f;
        if (this.scale != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            f = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / gallery_menu_showall)) * this.scale;
        }
        this.switchImageAfterAnimation = gallery_menu_showall;
        animateTo(this.scale, (f + (this.maxX + ((float) getContainerViewWidth()))) + ((float) (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / gallery_menu_showall)), this.translationY, false);
    }

    private void gotoMessage(MessageObject messageObject) {
        long dialogId = messageObject.getDialogId();
        int id = messageObject.getId();
        if (dialogId != 0) {
            int i = (int) dialogId;
            int i2 = (int) (dialogId >> 32);
            Bundle bundle = new Bundle();
            if (i == 0) {
                bundle.putInt("enc_id", i2);
            } else if (i2 == gallery_menu_save) {
                bundle.putInt("chat_id", i);
            } else if (i > 0) {
                bundle.putInt("user_id", i);
            } else if (i < 0) {
                if (id != 0) {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-i));
                    if (!(chat == null || chat.migrated_to == null)) {
                        bundle.putInt("migrated_to", i);
                        i = -chat.migrated_to.channel_id;
                    }
                }
                bundle.putInt("chat_id", -i);
            }
            bundle.putInt("message_id", id);
            if (MessagesController.checkCanOpenChat(bundle, this.parentChatActivity)) {
                closePhoto(true, false);
                ((LaunchActivity) this.parentActivity).presentFragment(new ChatActivity(bundle));
            }
        }
    }

    private void initThemeCaptionItem() {
        if (!ThemeUtil.m2490b()) {
        }
    }

    private void initThemeItems() {
        if (!ThemeUtil.m2490b()) {
        }
    }

    private boolean isGifToPlay() {
        return isGifToPlay(this.currentMessageObject);
    }

    private boolean isGifToPlay(MessageObject messageObject) {
        return (messageObject == null || !MoboConstants.aU) ? false : (messageObject.isGif() || messageObject.isNewGif()) && VERSION.SDK_INT >= 16;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onActionClick(boolean r9) {
        /*
        r8 = this;
        r7 = 1;
        r0 = 0;
        r6 = 0;
        r1 = r8.currentMessageObject;
        if (r1 != 0) goto L_0x000b;
    L_0x0007:
        r1 = r8.currentBotInlineResult;
        if (r1 == 0) goto L_0x0011;
    L_0x000b:
        r1 = r8.currentFileNames;
        r1 = r1[r6];
        if (r1 != 0) goto L_0x0012;
    L_0x0011:
        return;
    L_0x0012:
        r1 = r8.currentMessageObject;
        if (r1 == 0) goto L_0x0070;
    L_0x0016:
        r1 = r8.currentMessageObject;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        if (r1 == 0) goto L_0x0179;
    L_0x001e:
        r1 = r8.currentMessageObject;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        r1 = r1.length();
        if (r1 == 0) goto L_0x0179;
    L_0x002a:
        r1 = new java.io.File;
        r2 = r8.currentMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.attachPath;
        r1.<init>(r2);
        r2 = r1.exists();
        if (r2 != 0) goto L_0x003c;
    L_0x003b:
        r1 = r0;
    L_0x003c:
        if (r1 != 0) goto L_0x0176;
    L_0x003e:
        r1 = r8.currentMessageObject;
        r1 = r1.messageOwner;
        r1 = com.hanista.mobogram.messenger.FileLoader.getPathToMessage(r1);
        r2 = r1.exists();
        if (r2 != 0) goto L_0x0088;
    L_0x004c:
        if (r0 != 0) goto L_0x0137;
    L_0x004e:
        if (r9 == 0) goto L_0x0011;
    L_0x0050:
        r0 = r8.currentMessageObject;
        if (r0 == 0) goto L_0x00da;
    L_0x0054:
        r0 = com.hanista.mobogram.messenger.FileLoader.getInstance();
        r1 = r8.currentFileNames;
        r1 = r1[r6];
        r0 = r0.isLoadingFile(r1);
        if (r0 != 0) goto L_0x00cb;
    L_0x0062:
        r0 = com.hanista.mobogram.messenger.FileLoader.getInstance();
        r1 = r8.currentMessageObject;
        r1 = r1.getDocument();
        r0.loadFile(r1, r7, r6);
        goto L_0x0011;
    L_0x0070:
        r1 = r8.currentBotInlineResult;
        if (r1 == 0) goto L_0x004c;
    L_0x0074:
        r1 = r8.currentBotInlineResult;
        r1 = r1.document;
        if (r1 == 0) goto L_0x008a;
    L_0x007a:
        r1 = r8.currentBotInlineResult;
        r1 = r1.document;
        r1 = com.hanista.mobogram.messenger.FileLoader.getPathToAttach(r1);
        r2 = r1.exists();
        if (r2 == 0) goto L_0x004c;
    L_0x0088:
        r0 = r1;
        goto L_0x004c;
    L_0x008a:
        r1 = new java.io.File;
        r2 = com.hanista.mobogram.messenger.FileLoader.getInstance();
        r3 = 4;
        r2 = r2.getDirectory(r3);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = r8.currentBotInlineResult;
        r4 = r4.content_url;
        r4 = com.hanista.mobogram.messenger.Utilities.MD5(r4);
        r3 = r3.append(r4);
        r4 = ".";
        r3 = r3.append(r4);
        r4 = r8.currentBotInlineResult;
        r4 = r4.content_url;
        r5 = "mp4";
        r4 = com.hanista.mobogram.messenger.ImageLoader.getHttpUrlExtension(r4, r5);
        r3 = r3.append(r4);
        r3 = r3.toString();
        r1.<init>(r2, r3);
        r2 = r1.exists();
        if (r2 == 0) goto L_0x004c;
    L_0x00c9:
        r0 = r1;
        goto L_0x004c;
    L_0x00cb:
        r0 = com.hanista.mobogram.messenger.FileLoader.getInstance();
        r1 = r8.currentMessageObject;
        r1 = r1.getDocument();
        r0.cancelLoadFile(r1);
        goto L_0x0011;
    L_0x00da:
        r0 = r8.currentBotInlineResult;
        if (r0 == 0) goto L_0x0011;
    L_0x00de:
        r0 = r8.currentBotInlineResult;
        r0 = r0.document;
        if (r0 == 0) goto L_0x010c;
    L_0x00e4:
        r0 = com.hanista.mobogram.messenger.FileLoader.getInstance();
        r1 = r8.currentFileNames;
        r1 = r1[r6];
        r0 = r0.isLoadingFile(r1);
        if (r0 != 0) goto L_0x00ff;
    L_0x00f2:
        r0 = com.hanista.mobogram.messenger.FileLoader.getInstance();
        r1 = r8.currentBotInlineResult;
        r1 = r1.document;
        r0.loadFile(r1, r7, r6);
        goto L_0x0011;
    L_0x00ff:
        r0 = com.hanista.mobogram.messenger.FileLoader.getInstance();
        r1 = r8.currentBotInlineResult;
        r1 = r1.document;
        r0.cancelLoadFile(r1);
        goto L_0x0011;
    L_0x010c:
        r0 = com.hanista.mobogram.messenger.ImageLoader.getInstance();
        r1 = r8.currentBotInlineResult;
        r1 = r1.content_url;
        r0 = r0.isLoadingHttpFile(r1);
        if (r0 != 0) goto L_0x012a;
    L_0x011a:
        r0 = com.hanista.mobogram.messenger.ImageLoader.getInstance();
        r1 = r8.currentBotInlineResult;
        r1 = r1.content_url;
        r2 = "mp4";
        r0.loadHttpFile(r1, r2);
        goto L_0x0011;
    L_0x012a:
        r0 = com.hanista.mobogram.messenger.ImageLoader.getInstance();
        r1 = r8.currentBotInlineResult;
        r1 = r1.content_url;
        r0.cancelLoadHttpFile(r1);
        goto L_0x0011;
    L_0x0137:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 16;
        if (r1 < r2) goto L_0x0142;
    L_0x013d:
        r8.preparePlayer(r0, r7);
        goto L_0x0011;
    L_0x0142:
        r1 = new android.content.Intent;
        r2 = "android.intent.action.VIEW";
        r1.<init>(r2);
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 24;
        if (r2 < r3) goto L_0x016b;
    L_0x0150:
        r1.setFlags(r7);
        r2 = r8.parentActivity;
        r3 = "com.hanista.mobogram.provider";
        r0 = android.support.v4.content.FileProvider.getUriForFile(r2, r3, r0);
        r2 = "video/mp4";
        r1.setDataAndType(r0, r2);
    L_0x0162:
        r0 = r8.parentActivity;
        r2 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r0.startActivityForResult(r1, r2);
        goto L_0x0011;
    L_0x016b:
        r0 = android.net.Uri.fromFile(r0);
        r2 = "video/mp4";
        r1.setDataAndType(r0, r2);
        goto L_0x0162;
    L_0x0176:
        r0 = r1;
        goto L_0x004c;
    L_0x0179:
        r1 = r0;
        goto L_0x003c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.PhotoViewer.onActionClick(boolean):void");
    }

    @SuppressLint({"NewApi"})
    private void onDraw(Canvas canvas) {
        if (this.animationInProgress == gallery_menu_save) {
            return;
        }
        if (this.isVisible || this.animationInProgress == gallery_menu_showall) {
            float f;
            float f2;
            float f3;
            float containerViewHeight;
            float f4;
            float f5;
            int bitmapWidth;
            int i;
            int i2;
            float f6 = Face.UNCOMPUTED_PROBABILITY;
            if (this.imageMoveAnimation != null) {
                if (!this.scroller.isFinished()) {
                    this.scroller.abortAnimation();
                }
                f = ((this.animateToScale - this.scale) * this.animationValue) + this.scale;
                f2 = ((this.animateToX - this.translationX) * this.animationValue) + this.translationX;
                f3 = this.translationY + ((this.animateToY - this.translationY) * this.animationValue);
                if (this.currentEditMode == gallery_menu_save) {
                    this.photoCropView.setAnimationProgress(this.animationValue);
                }
                if (this.animateToScale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.translationX == 0.0f) {
                    f6 = f3;
                }
                this.containerView.invalidate();
                float f7 = f;
                f = f2;
                f2 = f3;
                f3 = f7;
            } else {
                if (this.animationStartTime != 0) {
                    this.translationX = this.animateToX;
                    this.translationY = this.animateToY;
                    this.scale = this.animateToScale;
                    this.animationStartTime = 0;
                    if (this.currentEditMode == gallery_menu_save) {
                        this.photoCropView.setAnimationProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    }
                    updateMinMax(this.scale);
                    this.zoomAnimation = false;
                }
                if (!this.scroller.isFinished() && this.scroller.computeScrollOffset()) {
                    if (((float) this.scroller.getStartX()) < this.maxX && ((float) this.scroller.getStartX()) > this.minX) {
                        this.translationX = (float) this.scroller.getCurrX();
                    }
                    if (((float) this.scroller.getStartY()) < this.maxY && ((float) this.scroller.getStartY()) > this.minY) {
                        this.translationY = (float) this.scroller.getCurrY();
                    }
                    this.containerView.invalidate();
                }
                if (this.switchImageAfterAnimation != 0) {
                    if (this.switchImageAfterAnimation == gallery_menu_save) {
                        setImageIndex(this.currentIndex + gallery_menu_save, false);
                    } else if (this.switchImageAfterAnimation == gallery_menu_showall) {
                        setImageIndex(this.currentIndex - 1, false);
                    }
                    this.switchImageAfterAnimation = 0;
                }
                f3 = this.scale;
                f2 = this.translationY;
                f = this.translationX;
                if (!this.moving) {
                    f6 = this.translationY;
                }
            }
            if (this.currentEditMode != 0 || this.scale != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || f6 == Face.UNCOMPUTED_PROBABILITY || this.zoomAnimation) {
                this.backgroundDrawable.setAlpha(NalUnitUtil.EXTENDED_SAR);
            } else {
                containerViewHeight = ((float) getContainerViewHeight()) / 4.0f;
                this.backgroundDrawable.setAlpha((int) Math.max(127.0f, (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - (Math.min(Math.abs(f6), containerViewHeight) / containerViewHeight)) * 255.0f));
            }
            ImageReceiver imageReceiver = null;
            if (this.currentEditMode == 0) {
                ImageReceiver imageReceiver2;
                if (!(this.scale < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || this.zoomAnimation || this.zooming)) {
                    if (f > this.maxX + ((float) AndroidUtilities.dp(5.0f))) {
                        imageReceiver2 = this.leftImage;
                    } else if (f < this.minX - ((float) AndroidUtilities.dp(5.0f))) {
                        imageReceiver2 = this.rightImage;
                    }
                    this.changingPage = imageReceiver2 == null;
                    imageReceiver = imageReceiver2;
                }
                imageReceiver2 = null;
                if (imageReceiver2 == null) {
                }
                this.changingPage = imageReceiver2 == null;
                imageReceiver = imageReceiver2;
            }
            if (imageReceiver == this.rightImage) {
                f4 = 0.0f;
                containerViewHeight = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                if (this.zoomAnimation || f >= this.minX) {
                    f5 = f;
                } else {
                    containerViewHeight = Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, (this.minX - f) / ((float) canvas.getWidth()));
                    f4 = (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - containerViewHeight) * 0.3f;
                    f5 = (float) ((-canvas.getWidth()) - (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / gallery_menu_showall));
                }
                if (imageReceiver.hasBitmapImage()) {
                    canvas.save();
                    canvas.translate((float) (getContainerViewWidth() / gallery_menu_showall), (float) (getContainerViewHeight() / gallery_menu_showall));
                    canvas.translate(((float) (canvas.getWidth() + (AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) / gallery_menu_showall))) + f5, 0.0f);
                    canvas.scale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f4, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f4);
                    bitmapWidth = imageReceiver.getBitmapWidth();
                    int bitmapHeight = imageReceiver.getBitmapHeight();
                    float containerViewWidth = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                    float containerViewHeight2 = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                    if (containerViewWidth <= containerViewHeight2) {
                        containerViewHeight2 = containerViewWidth;
                    }
                    i = (int) (((float) bitmapWidth) * containerViewHeight2);
                    i2 = (int) (containerViewHeight2 * ((float) bitmapHeight));
                    imageReceiver.setAlpha(containerViewHeight);
                    imageReceiver.setImageCoords((-i) / gallery_menu_showall, (-i2) / gallery_menu_showall, i, i2);
                    imageReceiver.draw(canvas);
                    canvas.restore();
                }
                canvas.save();
                canvas.translate(f5, f2 / f3);
                canvas.translate(((((float) canvas.getWidth()) * (this.scale + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) + ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE))) / 2.0f, (-f2) / f3);
                this.radialProgressViews[gallery_menu_save].setScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f4);
                this.radialProgressViews[gallery_menu_save].setAlpha(containerViewHeight);
                this.radialProgressViews[gallery_menu_save].onDraw(canvas);
                canvas.restore();
            }
            f4 = 0.0f;
            containerViewHeight = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            if (this.zoomAnimation || f <= this.maxX || this.currentEditMode != 0) {
                f5 = f;
            } else {
                containerViewHeight = Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, (f - this.maxX) / ((float) canvas.getWidth()));
                f4 = 0.3f * containerViewHeight;
                containerViewHeight = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - containerViewHeight;
                f5 = this.maxX;
            }
            Object obj = (VERSION.SDK_INT < 16 || this.aspectRatioFrameLayout == null || this.aspectRatioFrameLayout.getVisibility() != 0) ? null : gallery_menu_save;
            if (this.centerImage.hasBitmapImage()) {
                canvas.save();
                canvas.translate((float) ((getContainerViewWidth() / gallery_menu_showall) + getAdditionX()), (float) ((getContainerViewHeight() / gallery_menu_showall) + getAdditionY()));
                canvas.translate(f5, f2);
                canvas.scale(f3 - f4, f3 - f4);
                if (this.currentEditMode == gallery_menu_save) {
                    this.photoCropView.setBitmapParams(f3, f5, f2);
                }
                bitmapWidth = this.centerImage.getBitmapWidth();
                i = this.centerImage.getBitmapHeight();
                if (obj != null && this.textureUploaded && Math.abs((((float) bitmapWidth) / ((float) i)) - (((float) this.videoTextureView.getMeasuredWidth()) / ((float) this.videoTextureView.getMeasuredHeight()))) > 0.01f) {
                    bitmapWidth = this.videoTextureView.getMeasuredWidth();
                    i = this.videoTextureView.getMeasuredHeight();
                }
                float containerViewWidth2 = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                float containerViewHeight3 = ((float) getContainerViewHeight()) / ((float) i);
                if (containerViewWidth2 <= containerViewHeight3) {
                    containerViewHeight3 = containerViewWidth2;
                }
                bitmapWidth = (int) (((float) bitmapWidth) * containerViewHeight3);
                i = (int) (((float) i) * containerViewHeight3);
                if (!(obj != null && this.textureUploaded && this.videoCrossfadeStarted && this.videoCrossfadeAlpha == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) {
                    this.centerImage.setAlpha(containerViewHeight);
                    this.centerImage.setImageCoords((-bitmapWidth) / gallery_menu_showall, (-i) / gallery_menu_showall, bitmapWidth, i);
                    this.centerImage.draw(canvas);
                }
                if (obj != null) {
                    if (!this.videoCrossfadeStarted && this.textureUploaded) {
                        this.videoCrossfadeStarted = true;
                        this.videoCrossfadeAlpha = 0.0f;
                        this.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                    }
                    canvas.translate((float) ((-bitmapWidth) / gallery_menu_showall), (float) ((-i) / gallery_menu_showall));
                    this.videoTextureView.setAlpha(this.videoCrossfadeAlpha * containerViewHeight);
                    this.aspectRatioFrameLayout.draw(canvas);
                    if (this.videoCrossfadeStarted && this.videoCrossfadeAlpha < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        long currentTimeMillis = System.currentTimeMillis();
                        long j = currentTimeMillis - this.videoCrossfadeAlphaLastTime;
                        this.videoCrossfadeAlphaLastTime = currentTimeMillis;
                        this.videoCrossfadeAlpha += ((float) j) / BitmapDescriptorFactory.HUE_MAGENTA;
                        this.containerView.invalidate();
                        if (this.videoCrossfadeAlpha > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                            this.videoCrossfadeAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                        }
                    }
                }
                canvas.restore();
            }
            if (obj == null && (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0)) {
                canvas.save();
                canvas.translate(f5, f2 / f3);
                this.radialProgressViews[0].setScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f4);
                this.radialProgressViews[0].setAlpha(containerViewHeight);
                this.radialProgressViews[0].onDraw(canvas);
                canvas.restore();
            }
            if (imageReceiver == this.leftImage) {
                if (imageReceiver.hasBitmapImage()) {
                    canvas.save();
                    canvas.translate((float) (getContainerViewWidth() / gallery_menu_showall), (float) (getContainerViewHeight() / gallery_menu_showall));
                    canvas.translate(((-((((float) canvas.getWidth()) * (this.scale + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) + ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)))) / 2.0f) + f, 0.0f);
                    int bitmapWidth2 = imageReceiver.getBitmapWidth();
                    i2 = imageReceiver.getBitmapHeight();
                    f4 = ((float) getContainerViewWidth()) / ((float) bitmapWidth2);
                    containerViewHeight = ((float) getContainerViewHeight()) / ((float) i2);
                    if (f4 <= containerViewHeight) {
                        containerViewHeight = f4;
                    }
                    int i3 = (int) (((float) bitmapWidth2) * containerViewHeight);
                    int i4 = (int) (containerViewHeight * ((float) i2));
                    imageReceiver.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    imageReceiver.setImageCoords((-i3) / gallery_menu_showall, (-i4) / gallery_menu_showall, i3, i4);
                    imageReceiver.draw(canvas);
                    canvas.restore();
                }
                canvas.save();
                canvas.translate(f, f2 / f3);
                canvas.translate((-((((float) canvas.getWidth()) * (this.scale + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) + ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)))) / 2.0f, (-f2) / f3);
                this.radialProgressViews[gallery_menu_showall].setScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.radialProgressViews[gallery_menu_showall].setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.radialProgressViews[gallery_menu_showall].onDraw(canvas);
                canvas.restore();
            }
        }
    }

    private void onPhotoClosed(PlaceProviderObject placeProviderObject) {
        this.isVisible = false;
        this.disableShowCheck = true;
        this.currentMessageObject = null;
        this.currentBotInlineResult = null;
        this.currentFileLocation = null;
        this.currentPathObject = null;
        this.currentThumb = null;
        if (this.currentAnimation != null) {
            this.currentAnimation.setSecondParentView(null);
            this.currentAnimation = null;
        }
        for (int i = 0; i < gallery_menu_send; i += gallery_menu_save) {
            if (this.radialProgressViews[i] != null) {
                this.radialProgressViews[i].setBackgroundState(-1, false);
            }
        }
        this.centerImage.setImageBitmap((Bitmap) null);
        this.leftImage.setImageBitmap((Bitmap) null);
        this.rightImage.setImageBitmap((Bitmap) null);
        this.containerView.post(new Runnable() {
            public void run() {
                PhotoViewer.this.animatingImageView.setImageBitmap(null);
                try {
                    if (PhotoViewer.this.windowView.getParent() != null) {
                        ((WindowManager) PhotoViewer.this.parentActivity.getSystemService("window")).removeView(PhotoViewer.this.windowView);
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        });
        if (this.placeProvider != null) {
            this.placeProvider.willHidePhotoViewer();
        }
        this.placeProvider = null;
        this.disableShowCheck = false;
        if (placeProviderObject != null) {
            placeProviderObject.imageReceiver.setVisible(true, true);
        }
        rollbackOrientation();
    }

    private void onPhotoShow(MessageObject messageObject, FileLocation fileLocation, ArrayList<MessageObject> arrayList, ArrayList<Object> arrayList2, int i, PlaceProviderObject placeProviderObject) {
        int i2;
        Object obj;
        this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
        this.currentMessageObject = null;
        this.currentFileLocation = null;
        this.currentPathObject = null;
        this.currentBotInlineResult = null;
        this.currentIndex = -1;
        this.currentFileNames[0] = null;
        this.currentFileNames[gallery_menu_save] = null;
        this.currentFileNames[gallery_menu_showall] = null;
        this.avatarsDialogId = 0;
        this.totalImagesCount = 0;
        this.totalImagesCountMerge = 0;
        this.currentEditMode = 0;
        this.isFirstLoading = true;
        this.needSearchImageInArr = false;
        this.loadingMoreImages = false;
        this.endReached[0] = false;
        this.endReached[gallery_menu_save] = this.mergeDialogId == 0;
        this.opennedFromMedia = false;
        this.needCaptionLayout = false;
        this.canShowBottom = true;
        this.imagesArr.clear();
        this.imagesArrLocations.clear();
        this.imagesArrLocationsSizes.clear();
        this.avatarsArr.clear();
        this.imagesArrLocals.clear();
        for (i2 = 0; i2 < gallery_menu_showall; i2 += gallery_menu_save) {
            this.imagesByIds[i2].clear();
            this.imagesByIdsTemp[i2].clear();
        }
        this.imagesArrTemp.clear();
        this.currentUserAvatarLocation = null;
        this.containerView.setPadding(0, 0, 0, 0);
        this.currentThumb = placeProviderObject != null ? placeProviderObject.thumb : null;
        this.menuItem.setVisibility(0);
        setDrawingItemVisibility(0);
        this.bottomLayout.setVisibility(0);
        this.bottomLayout.setTranslationY(0.0f);
        this.shareButton.setVisibility(8);
        this.allowShare = false;
        this.menuItem.hideSubItem(gallery_menu_showall);
        this.menuItem.hideSubItem(gallery_menu_share);
        this.menuItem.hideSubItem(gallery_menu_openin);
        this.menuItem.hideSubItem(gallery_menu_proforward);
        this.menuItem.hideSubItem(gallery_menu_goto_message);
        this.actionBar.setTranslationY(0.0f);
        this.pickerView.setTranslationY(0.0f);
        this.checkImageView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.pickerView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.checkImageView.setVisibility(8);
        this.pickerView.setVisibility(8);
        this.paintItem.setVisibility(8);
        this.cropItem.setVisibility(8);
        this.tuneItem.setVisibility(8);
        this.captionDoneItem.setVisibility(8);
        this.captionEditText.setVisibility(8);
        this.mentionListView.setVisibility(8);
        this.muteItem.setVisibility(8);
        this.masksItem.setVisibility(8);
        this.muteItemAvailable = false;
        this.muteVideo = false;
        this.muteItem.setIcon((int) C0338R.drawable.volume_on);
        this.editorDoneLayout.setVisibility(8);
        this.captionTextView.setTag(null);
        this.captionTextView.setVisibility(4);
        if (this.photoCropView != null) {
            this.photoCropView.setVisibility(8);
        }
        if (this.photoFilterView != null) {
            this.photoFilterView.setVisibility(8);
        }
        for (i2 = 0; i2 < gallery_menu_send; i2 += gallery_menu_save) {
            if (this.radialProgressViews[i2] != null) {
                this.radialProgressViews[i2].setBackgroundState(-1, false);
            }
        }
        if (messageObject != null && arrayList == null) {
            this.imagesArr.add(messageObject);
            if (this.currentAnimation != null) {
                this.needSearchImageInArr = false;
                this.menuItem.showSubItem(gallery_menu_proforward);
                this.menuItem.showSubItem(gallery_menu_goto_message);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaWebPage) || !(messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty))) {
                this.menuItem.hideSubItem(gallery_menu_proforward);
                this.menuItem.hideSubItem(gallery_menu_goto_message);
            } else {
                this.needSearchImageInArr = true;
                this.imagesByIds[0].put(Integer.valueOf(messageObject.getId()), messageObject);
                this.menuItem.showSubItem(gallery_menu_showall);
                this.menuItem.showSubItem(gallery_menu_proforward);
                this.menuItem.showSubItem(gallery_menu_goto_message);
            }
            setImageIndex(0, true);
        } else if (fileLocation != null) {
            this.avatarsDialogId = placeProviderObject.dialogId;
            this.imagesArrLocations.add(fileLocation);
            this.imagesArrLocationsSizes.add(Integer.valueOf(placeProviderObject.size));
            this.avatarsArr.add(new TL_photoEmpty());
            r1 = this.shareButton;
            i2 = (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0) ? 0 : 8;
            r1.setVisibility(i2);
            this.allowShare = true;
            this.menuItem.hideSubItem(gallery_menu_showall);
            this.menuItem.hideSubItem(gallery_menu_proforward);
            this.menuItem.hideSubItem(gallery_menu_goto_message);
            if (this.shareButton.getVisibility() == 0) {
                this.menuItem.hideSubItem(gallery_menu_share);
            } else {
                this.menuItem.showSubItem(gallery_menu_share);
            }
            setImageIndex(0, true);
            this.currentUserAvatarLocation = fileLocation;
        } else if (arrayList != null) {
            this.menuItem.showSubItem(gallery_menu_showall);
            this.menuItem.showSubItem(gallery_menu_proforward);
            this.menuItem.showSubItem(gallery_menu_goto_message);
            this.opennedFromMedia = true;
            this.imagesArr.addAll(arrayList);
            if (!this.opennedFromMedia) {
                Collections.reverse(this.imagesArr);
                i = (this.imagesArr.size() - i) - 1;
            }
            for (int i3 = 0; i3 < this.imagesArr.size(); i3 += gallery_menu_save) {
                MessageObject messageObject2 = (MessageObject) this.imagesArr.get(i3);
                this.imagesByIds[messageObject2.getDialogId() == this.currentDialogId ? 0 : gallery_menu_save].put(Integer.valueOf(messageObject2.getId()), messageObject2);
            }
            setImageIndex(i, true);
        } else if (arrayList2 != null) {
            if (this.sendPhotoType == 0) {
                this.checkImageView.setVisibility(0);
            }
            this.menuItem.setVisibility(8);
            setDrawingItemVisibility(8);
            this.imagesArrLocals.addAll(arrayList2);
            setImageIndex(i, true);
            this.pickerView.setVisibility(0);
            this.bottomLayout.setVisibility(8);
            this.canShowBottom = false;
            obj = this.imagesArrLocals.get(i);
            if (obj instanceof PhotoEntry) {
                if (((PhotoEntry) obj).isVideo) {
                    this.cropItem.setVisibility(8);
                    this.bottomLayout.setVisibility(0);
                    this.bottomLayout.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                } else {
                    this.cropItem.setVisibility(0);
                }
            } else if (obj instanceof BotInlineResult) {
                this.cropItem.setVisibility(8);
            } else {
                r1 = this.cropItem;
                i2 = ((obj instanceof SearchImage) && ((SearchImage) obj).type == 0) ? 0 : 8;
                r1.setVisibility(i2);
            }
            if (this.parentChatActivity != null && (this.parentChatActivity.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.parentChatActivity.currentEncryptedChat.layer) >= 46)) {
                this.mentionsAdapter.setChatInfo(this.parentChatActivity.info);
                this.mentionsAdapter.setNeedUsernames(this.parentChatActivity.currentChat != null);
                this.mentionsAdapter.setNeedBotContext(false);
                boolean z = this.cropItem.getVisibility() == 0 && (this.placeProvider == null || (this.placeProvider != null && this.placeProvider.allowCaption()));
                this.needCaptionLayout = z;
                this.captionEditText.setVisibility(this.needCaptionLayout ? 0 : 8);
                setDrawingItemVisibility(this.cropItem.getVisibility());
                if (this.captionTextView.getTag() == null && this.needCaptionLayout) {
                    this.captionTextView.setText(LocaleController.getString("AddCaption", C0338R.string.AddCaption));
                    this.captionTextView.setTag("empty");
                    this.captionTextView.setTextColor(-1291845633);
                    this.captionTextView.setVisibility(0);
                } else {
                    this.captionTextView.setTextColor(-1);
                }
                if (this.needCaptionLayout) {
                    this.captionEditText.onCreate();
                }
            }
            if (VERSION.SDK_INT >= 16) {
                this.paintItem.setVisibility(this.cropItem.getVisibility());
                this.tuneItem.setVisibility(this.cropItem.getVisibility());
            }
            updateSelectedCount();
        }
        if (this.currentAnimation == null) {
            if (this.currentDialogId != 0 && this.totalImagesCount == 0) {
                SharedMediaQuery.getMediaCount(this.currentDialogId, 0, this.classGuid, true);
                if (this.mergeDialogId != 0) {
                    SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                }
            } else if (this.avatarsDialogId != 0) {
                MessagesController.getInstance().loadDialogPhotos(this.avatarsDialogId, 0, 80, 0, true, this.classGuid);
            }
        }
        if ((this.currentMessageObject != null && (this.currentMessageObject.isVideo() || isGifToPlay())) || (this.currentBotInlineResult != null && (this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(this.currentBotInlineResult.document)))) {
            onActionClick(false);
        } else if (!this.imagesArrLocals.isEmpty()) {
            obj = this.imagesArrLocals.get(i);
            if (obj instanceof PhotoEntry) {
                PhotoEntry photoEntry = (PhotoEntry) obj;
                if (photoEntry.isVideo) {
                    preparePlayer(new File(photoEntry.path), false);
                }
            }
        }
        setDrawingItemVisibility(this.drawingItem.getVisibility());
    }

    private void onSharePressed() {
        File file = null;
        Object obj = null;
        if (this.parentActivity != null && this.allowShare) {
            try {
                if (this.currentMessageObject != null) {
                    if (this.currentMessageObject.isVideo() || isGifToPlay()) {
                        obj = gallery_menu_save;
                    }
                    file = FileLoader.getPathToMessage(this.currentMessageObject.messageOwner);
                } else if (this.currentFileLocation != null) {
                    file = FileLoader.getPathToAttach(this.currentFileLocation, this.avatarsDialogId != 0);
                }
                if (file.exists()) {
                    Intent intent = new Intent("android.intent.action.SEND");
                    if (obj != null) {
                        intent.setType(MimeTypes.VIDEO_MP4);
                    } else {
                        intent.setType("image/jpeg");
                    }
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
                    this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", C0338R.string.ShareFile)), 500);
                    return;
                }
                Builder builder = new Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                builder.setMessage(LocaleController.getString("PleaseDownload", C0338R.string.PleaseDownload));
                showAlertDialog(builder);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    private boolean onTouchEvent(MotionEvent motionEvent) {
        float f = 0.0f;
        if (this.animationInProgress != 0 || this.animationStartTime != 0) {
            return false;
        }
        if (this.currentEditMode == gallery_menu_showall) {
            this.photoFilterView.onTouch(motionEvent);
            return true;
        }
        if (this.currentEditMode == gallery_menu_save) {
            if (motionEvent.getPointerCount() != gallery_menu_save) {
                this.photoCropView.onTouch(null);
            } else if (this.photoCropView.onTouch(motionEvent)) {
                updateMinMax(this.scale);
                return true;
            }
        }
        if (this.captionEditText.isPopupShowing() || this.captionEditText.isKeyboardVisible()) {
            if (motionEvent.getAction() == gallery_menu_save) {
                closeCaptionEnter(true);
            }
            return true;
        } else if (this.currentEditMode == 0 && motionEvent.getPointerCount() == gallery_menu_save && this.gestureDetector.onTouchEvent(motionEvent) && this.doubleTap) {
            this.doubleTap = false;
            this.moving = false;
            this.zooming = false;
            checkMinMax(false);
            return true;
        } else {
            float y;
            if (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5) {
                if (this.currentEditMode == gallery_menu_save) {
                    this.photoCropView.cancelAnimationRunnable();
                }
                this.discardTap = false;
                if (!this.scroller.isFinished()) {
                    this.scroller.abortAnimation();
                }
                if (!(this.draggingDown || this.changingPage)) {
                    if (this.canZoom && motionEvent.getPointerCount() == gallery_menu_showall) {
                        this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(gallery_menu_save) - motionEvent.getX(0)), (double) (motionEvent.getY(gallery_menu_save) - motionEvent.getY(0)));
                        this.pinchStartScale = this.scale;
                        this.pinchCenterX = (motionEvent.getX(0) + motionEvent.getX(gallery_menu_save)) / 2.0f;
                        this.pinchCenterY = (motionEvent.getY(0) + motionEvent.getY(gallery_menu_save)) / 2.0f;
                        this.pinchStartX = this.translationX;
                        this.pinchStartY = this.translationY;
                        this.zooming = true;
                        this.moving = false;
                        if (this.velocityTracker != null) {
                            this.velocityTracker.clear();
                        }
                    } else if (motionEvent.getPointerCount() == gallery_menu_save) {
                        this.moveStartX = motionEvent.getX();
                        y = motionEvent.getY();
                        this.moveStartY = y;
                        this.dragY = y;
                        this.draggingDown = false;
                        this.canDragDown = true;
                        if (this.velocityTracker != null) {
                            this.velocityTracker.clear();
                        }
                    }
                }
            } else if (motionEvent.getActionMasked() == gallery_menu_showall) {
                if (this.currentEditMode == gallery_menu_save) {
                    this.photoCropView.cancelAnimationRunnable();
                }
                if (this.canZoom && motionEvent.getPointerCount() == gallery_menu_showall && !this.draggingDown && this.zooming && !this.changingPage) {
                    this.discardTap = true;
                    this.scale = (((float) Math.hypot((double) (motionEvent.getX(gallery_menu_save) - motionEvent.getX(0)), (double) (motionEvent.getY(gallery_menu_save) - motionEvent.getY(0)))) / this.pinchStartDistance) * this.pinchStartScale;
                    this.translationX = (this.pinchCenterX - ((float) (getContainerViewWidth() / gallery_menu_showall))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / gallery_menu_showall))) - this.pinchStartX) * (this.scale / this.pinchStartScale));
                    this.translationY = (this.pinchCenterY - ((float) (getContainerViewHeight() / gallery_menu_showall))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / gallery_menu_showall))) - this.pinchStartY) * (this.scale / this.pinchStartScale));
                    updateMinMax(this.scale);
                    this.containerView.invalidate();
                } else if (motionEvent.getPointerCount() == gallery_menu_save) {
                    if (this.velocityTracker != null) {
                        this.velocityTracker.addMovement(motionEvent);
                    }
                    y = Math.abs(motionEvent.getX() - this.moveStartX);
                    r2 = Math.abs(motionEvent.getY() - this.dragY);
                    if (y > ((float) AndroidUtilities.dp(3.0f)) || r2 > ((float) AndroidUtilities.dp(3.0f))) {
                        this.discardTap = true;
                    }
                    if (!(this.placeProvider instanceof EmptyPhotoViewerProvider) && this.currentEditMode == 0 && this.canDragDown && !this.draggingDown && this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && r2 >= ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) && r2 / 2.0f > y) {
                        this.draggingDown = true;
                        this.moving = false;
                        this.dragY = motionEvent.getY();
                        if (this.isActionBarVisible && this.canShowBottom) {
                            toggleActionBar(false, true);
                        } else if (this.pickerView.getVisibility() == 0) {
                            toggleActionBar(false, true);
                            toggleCheckImageView(false);
                        }
                        return true;
                    } else if (this.draggingDown) {
                        this.translationY = motionEvent.getY() - this.dragY;
                        this.containerView.invalidate();
                    } else if (this.invalidCoords || this.animationStartTime != 0) {
                        this.invalidCoords = false;
                        this.moveStartX = motionEvent.getX();
                        this.moveStartY = motionEvent.getY();
                    } else {
                        r2 = this.moveStartX - motionEvent.getX();
                        y = this.moveStartY - motionEvent.getY();
                        if (this.moving || this.currentEditMode != 0 || ((this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && Math.abs(y) + ((float) AndroidUtilities.dp(12.0f)) < Math.abs(r2)) || this.scale != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) {
                            if (!this.moving) {
                                this.moving = true;
                                this.canDragDown = false;
                                y = 0.0f;
                                r2 = 0.0f;
                            }
                            this.moveStartX = motionEvent.getX();
                            this.moveStartY = motionEvent.getY();
                            updateMinMax(this.scale);
                            if ((this.translationX < this.minX && !(this.currentEditMode == 0 && this.rightImage.hasImage())) || (this.translationX > this.maxX && !(this.currentEditMode == 0 && this.leftImage.hasImage()))) {
                                r2 /= 3.0f;
                            }
                            if (this.maxY != 0.0f || this.minY != 0.0f || this.currentEditMode != 0) {
                                if (this.translationY < this.minY || this.translationY > this.maxY) {
                                    f = y / 3.0f;
                                }
                                f = y;
                            } else if (this.translationY - y < this.minY) {
                                this.translationY = this.minY;
                            } else {
                                if (this.translationY - y > this.maxY) {
                                    this.translationY = this.maxY;
                                }
                                f = y;
                            }
                            this.translationX -= r2;
                            if (!(this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.currentEditMode == 0)) {
                                this.translationY -= f;
                            }
                            this.containerView.invalidate();
                        }
                    }
                }
            } else if (motionEvent.getActionMasked() == gallery_menu_send || motionEvent.getActionMasked() == gallery_menu_save || motionEvent.getActionMasked() == gallery_menu_delete) {
                if (this.currentEditMode == gallery_menu_save) {
                    this.photoCropView.startAnimationRunnable();
                }
                if (this.zooming) {
                    this.invalidCoords = true;
                    if (this.scale < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        updateMinMax(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        animateTo(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, 0.0f, true);
                    } else if (this.scale > 3.0f) {
                        y = (this.pinchCenterX - ((float) (getContainerViewWidth() / gallery_menu_showall))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / gallery_menu_showall))) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                        f = (this.pinchCenterY - ((float) (getContainerViewHeight() / gallery_menu_showall))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / gallery_menu_showall))) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                        updateMinMax(3.0f);
                        if (y < this.minX) {
                            y = this.minX;
                        } else if (y > this.maxX) {
                            y = this.maxX;
                        }
                        if (f < this.minY) {
                            f = this.minY;
                        } else if (f > this.maxY) {
                            f = this.maxY;
                        }
                        animateTo(3.0f, y, f, true);
                    } else {
                        checkMinMax(true);
                    }
                    this.zooming = false;
                } else if (this.draggingDown) {
                    if (Math.abs(this.dragY - motionEvent.getY()) > ((float) getContainerViewHeight()) / 6.0f) {
                        closePhoto(true, false);
                    } else {
                        if (this.pickerView.getVisibility() == 0) {
                            toggleActionBar(true, true);
                            toggleCheckImageView(true);
                        }
                        animateTo(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, 0.0f, false);
                    }
                    this.draggingDown = false;
                } else if (this.moving) {
                    y = this.translationX;
                    r2 = this.translationY;
                    updateMinMax(this.scale);
                    this.moving = false;
                    this.canDragDown = true;
                    if (this.velocityTracker != null && this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        this.velocityTracker.computeCurrentVelocity(PointerIconCompat.TYPE_DEFAULT);
                        f = this.velocityTracker.getXVelocity();
                    }
                    if (this.currentEditMode == 0) {
                        if ((this.translationX < this.minX - ((float) (getContainerViewWidth() / gallery_menu_send)) || r1 < ((float) (-AndroidUtilities.dp(650.0f)))) && this.rightImage.hasImage()) {
                            goToNext();
                            return true;
                        } else if ((this.translationX > this.maxX + ((float) (getContainerViewWidth() / gallery_menu_send)) || r1 > ((float) AndroidUtilities.dp(650.0f))) && this.leftImage.hasImage()) {
                            goToPrev();
                            return true;
                        }
                    }
                    if (this.translationX < this.minX) {
                        y = this.minX;
                    } else if (this.translationX > this.maxX) {
                        y = this.maxX;
                    }
                    f = this.translationY < this.minY ? this.minY : this.translationY > this.maxY ? this.maxY : r2;
                    animateTo(this.scale, y, f, false);
                }
            }
            return false;
        }
    }

    private void openCaptionEnter() {
        if (this.imageMoveAnimation == null && this.changeModeAnimation == null) {
            this.paintItem.setVisibility(8);
            this.cropItem.setVisibility(8);
            this.tuneItem.setVisibility(8);
            this.checkImageView.setVisibility(8);
            this.captionDoneItem.setVisibility(0);
            this.pickerView.setVisibility(8);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.captionEditText.getLayoutParams();
            layoutParams.bottomMargin = 0;
            this.captionEditText.setLayoutParams(layoutParams);
            layoutParams = (FrameLayout.LayoutParams) this.mentionListView.getLayoutParams();
            layoutParams.bottomMargin = 0;
            this.mentionListView.setLayoutParams(layoutParams);
            this.captionTextView.setVisibility(4);
            this.captionEditText.openKeyboard();
            this.lastTitle = this.actionBar.getTitle();
            this.actionBar.setTitle(LocaleController.getString("PhotoCaption", C0338R.string.PhotoCaption));
        }
    }

    @SuppressLint({"NewApi"})
    private void preparePlayer(File file, boolean z) {
        if (this.parentActivity != null) {
            releasePlayer();
            if (this.videoTextureView == null) {
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity);
                this.aspectRatioFrameLayout.setVisibility(4);
                this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                this.videoTextureView = new TextureView(this.parentActivity);
                this.videoTextureView.setOpaque(false);
                this.videoTextureView.setSurfaceTextureListener(new SurfaceTextureListener() {
                    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            PhotoViewer.this.videoPlayer.setSurface(new Surface(PhotoViewer.this.videoTextureView.getSurfaceTexture()));
                        }
                    }

                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            PhotoViewer.this.videoPlayer.blockingClearSurface();
                        }
                        return true;
                    }

                    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                        if (!PhotoViewer.this.textureUploaded) {
                            PhotoViewer.this.textureUploaded = true;
                            PhotoViewer.this.containerView.invalidate();
                        }
                    }
                });
                this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
            }
            this.textureUploaded = false;
            this.videoCrossfadeStarted = false;
            TextureView textureView = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView.setAlpha(0.0f);
            this.videoPlayButton.setImageResource(C0338R.drawable.inline_video_play);
            if (this.videoPlayer == null) {
                long duration;
                this.videoPlayer = new VideoPlayer(new ExtractorRendererBuilder(this.parentActivity, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36", Uri.fromFile(file)));
                this.videoPlayer.addListener(new Listener() {
                    public void onError(Exception exception) {
                        FileLog.m18e("tmessages", (Throwable) exception);
                    }

                    public void onStateChanged(boolean z, int i) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            if (i == 5 || i == PhotoViewer.gallery_menu_save) {
                                try {
                                    PhotoViewer.this.parentActivity.getWindow().clearFlags(TLRPC.USER_FLAG_UNUSED);
                                } catch (Throwable e) {
                                    FileLog.m18e("tmessages", e);
                                }
                            } else {
                                try {
                                    PhotoViewer.this.parentActivity.getWindow().addFlags(TLRPC.USER_FLAG_UNUSED);
                                } catch (Throwable e2) {
                                    FileLog.m18e("tmessages", e2);
                                }
                            }
                            if (i == 4 && PhotoViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                PhotoViewer.this.aspectRatioFrameLayout.setVisibility(0);
                            }
                            if (!PhotoViewer.this.videoPlayer.getPlayerControl().isPlaying() || i == 5) {
                                if (PhotoViewer.this.isPlaying) {
                                    PhotoViewer.this.isPlaying = false;
                                    PhotoViewer.this.videoPlayButton.setImageResource(C0338R.drawable.inline_video_play);
                                    AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.updateProgressRunnable);
                                    if (i == 5) {
                                        if (!PhotoViewer.this.videoPlayerSeekbar.isDragging()) {
                                            PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                                            PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                                            PhotoViewer.this.videoPlayer.seekTo(0);
                                            PhotoViewer.this.videoPlayer.getPlayerControl().pause();
                                        }
                                        if (PhotoViewer.this.isGifToPlay()) {
                                            PhotoViewer.this.videoPlayer.getPlayerControl().start();
                                        }
                                    }
                                }
                            } else if (!PhotoViewer.this.isPlaying) {
                                PhotoViewer.this.isPlaying = true;
                                PhotoViewer.this.videoPlayButton.setImageResource(C0338R.drawable.inline_video_pause);
                                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable);
                            }
                            PhotoViewer.this.updateVideoPlayerTime();
                        }
                    }

                    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                        if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                            if (i3 == 90 || i3 == 270) {
                                int i4 = i;
                                i = i2;
                                i2 = i4;
                            }
                            PhotoViewer.this.aspectRatioFrameLayout.setAspectRatio(i2 == 0 ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : (((float) i) * f) / ((float) i2), i3);
                        }
                    }
                });
                if (this.videoPlayer != null) {
                    duration = this.videoPlayer.getDuration();
                    if (duration == -1) {
                        duration = 0;
                    }
                } else {
                    duration = 0;
                }
                duration /= 1000;
                int ceil = (int) Math.ceil((double) this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60), Long.valueOf(duration / 60), Long.valueOf(duration % 60)})));
                this.playerNeedsPrepare = true;
            }
            if (this.playerNeedsPrepare) {
                this.videoPlayer.prepare();
                this.playerNeedsPrepare = false;
            }
            if (this.videoPlayerControlFrameLayout != null) {
                if (this.currentBotInlineResult != null && (this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                    this.bottomLayout.setVisibility(0);
                    this.bottomLayout.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                }
                this.videoPlayerControlFrameLayout.setVisibility(0);
                this.dateTextView.setVisibility(8);
                this.nameTextView.setVisibility(8);
                if (this.allowShare) {
                    this.shareButton.setVisibility(8);
                    this.menuItem.showSubItem(gallery_menu_share);
                }
            }
            if (this.videoTextureView.getSurfaceTexture() != null) {
                this.videoPlayer.setSurface(new Surface(this.videoTextureView.getSurfaceTexture()));
            }
            this.videoPlayer.setPlayWhenReady(z);
        }
    }

    private void redraw(int i) {
        if (i < gallery_menu_delete && this.containerView != null) {
            this.containerView.invalidate();
            AndroidUtilities.runOnUIThread(new AnonymousClass50(i), 100);
        }
    }

    private void releasePlayer() {
        if (this.videoPlayer != null) {
            this.videoPlayer.release();
            this.videoPlayer = null;
        }
        try {
            this.parentActivity.getWindow().clearFlags(TLRPC.USER_FLAG_UNUSED);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        if (this.aspectRatioFrameLayout != null) {
            this.containerView.removeView(this.aspectRatioFrameLayout);
            this.aspectRatioFrameLayout = null;
        }
        if (this.videoTextureView != null) {
            this.videoTextureView = null;
        }
        if (this.isPlaying) {
            this.isPlaying = false;
            this.videoPlayButton.setImageResource(C0338R.drawable.inline_video_play);
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
        }
        if (this.videoPlayerControlFrameLayout != null) {
            this.videoPlayerControlFrameLayout.setVisibility(8);
            this.dateTextView.setVisibility(0);
            this.nameTextView.setVisibility(0);
            if (this.allowShare) {
                this.shareButton.setVisibility(0);
                this.menuItem.hideSubItem(gallery_menu_share);
            }
        }
    }

    private void rollbackOrientation() {
        if (getParentActivity() != null) {
            if (this.originalOrientation != -100) {
                getParentActivity().setRequestedOrientation(this.originalOrientation);
            }
            this.originalOrientation = -100;
        }
    }

    private void rotate() {
        if (getParentActivity() != null) {
            if (this.originalOrientation == -100) {
                this.originalOrientation = getParentActivity().getRequestedOrientation();
            }
            if (getParentActivity().getResources().getConfiguration().orientation == gallery_menu_showall) {
                getParentActivity().setRequestedOrientation(gallery_menu_save);
            } else {
                getParentActivity().setRequestedOrientation(0);
            }
        }
    }

    private void setCurrentCaption(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() <= 0) {
            if (this.needCaptionLayout) {
                this.captionTextView.setText(LocaleController.getString("AddCaption", C0338R.string.AddCaption));
                this.captionTextView.setTag("empty");
                this.captionTextView.setVisibility(0);
                this.captionTextView.setTextColor(-1291845633);
            } else {
                this.captionTextView.setTextColor(-1);
                this.captionTextView.setTag(null);
                this.captionTextView.setVisibility(4);
            }
            initThemeCaptionItem();
            return;
        }
        this.captionTextView = this.captionTextViewOld;
        this.captionTextViewOld = this.captionTextViewNew;
        this.captionTextViewNew = this.captionTextView;
        CharSequence replaceEmoji = Emoji.replaceEmoji(new SpannableStringBuilder(charSequence.toString()), MessageObject.getTextPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        this.captionTextView.setTag(replaceEmoji);
        this.captionTextView.setText(replaceEmoji);
        this.captionTextView.setTextColor(-1);
        TextView textView = this.captionTextView;
        float f = (this.bottomLayout.getVisibility() == 0 || this.pickerView.getVisibility() == 0) ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
        textView.setAlpha(f);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int i = 4;
                PhotoViewer.this.captionTextViewOld.setTag(null);
                PhotoViewer.this.captionTextViewOld.setVisibility(4);
                TextView access$1600 = PhotoViewer.this.captionTextViewNew;
                if (PhotoViewer.this.bottomLayout.getVisibility() == 0 || PhotoViewer.this.pickerView.getVisibility() == 0) {
                    i = 0;
                }
                access$1600.setVisibility(i);
            }
        });
    }

    private void setDrawingItemVisibility(int i) {
        if ((this.parentChatActivity != null || (this.placeProvider != null && (this.placeProvider instanceof ChatActivity))) && this.currentAnimation == null && !isGifToPlay() && (this.currentMessageObject == null || !this.currentMessageObject.isVideo())) {
            this.drawingItem.setVisibility(i);
        } else {
            this.drawingItem.setVisibility(8);
        }
    }

    private void setImageIndex(int i, boolean z) {
        if (this.currentIndex != i && this.placeProvider != null) {
            boolean z2;
            int i2;
            if (!z) {
                this.currentThumb = null;
            }
            this.currentFileNames[0] = getFileName(i);
            this.currentFileNames[gallery_menu_save] = getFileName(i + gallery_menu_save);
            this.currentFileNames[gallery_menu_showall] = getFileName(i - 1);
            this.placeProvider.willSwitchFromPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
            int i3 = this.currentIndex;
            this.currentIndex = i;
            Object obj = null;
            Object obj2;
            int i4;
            if (this.imagesArr.isEmpty()) {
                if (!this.imagesArrLocations.isEmpty()) {
                    this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                    this.dateTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                    if (this.avatarsDialogId != UserConfig.getClientUserId() || this.avatarsArr.isEmpty()) {
                        this.menuItem.hideSubItem(gallery_menu_delete);
                    } else {
                        this.menuItem.showSubItem(gallery_menu_delete);
                    }
                    FileLocation fileLocation = this.currentFileLocation;
                    if (i < 0 || i >= this.imagesArrLocations.size()) {
                        closePhoto(false, false);
                        return;
                    }
                    this.currentFileLocation = (FileLocation) this.imagesArrLocations.get(i);
                    obj2 = (fileLocation == null || this.currentFileLocation == null || fileLocation.local_id != this.currentFileLocation.local_id || fileLocation.volume_id != this.currentFileLocation.volume_id) ? null : gallery_menu_save;
                    ActionBar actionBar = this.actionBar;
                    Object[] objArr = new Object[gallery_menu_showall];
                    objArr[0] = Integer.valueOf(this.currentIndex + gallery_menu_save);
                    objArr[gallery_menu_save] = Integer.valueOf(this.imagesArrLocations.size());
                    actionBar.setTitle(LocaleController.formatString("Of", C0338R.string.Of, objArr));
                    this.menuItem.showSubItem(gallery_menu_save);
                    this.allowShare = true;
                    ImageView imageView = this.shareButton;
                    i4 = (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0) ? 0 : 8;
                    imageView.setVisibility(i4);
                    if (this.shareButton.getVisibility() == 0) {
                        this.menuItem.hideSubItem(gallery_menu_share);
                    } else {
                        this.menuItem.showSubItem(gallery_menu_share);
                    }
                    obj = obj2;
                } else if (!this.imagesArrLocals.isEmpty()) {
                    if (i < 0 || i >= this.imagesArrLocals.size()) {
                        closePhoto(false, false);
                        return;
                    }
                    Object obj3;
                    boolean z3;
                    CharSequence charSequence;
                    Object obj4 = this.imagesArrLocals.get(i);
                    if (obj4 instanceof PhotoEntry) {
                        PhotoEntry photoEntry = (PhotoEntry) obj4;
                        this.currentPathObject = photoEntry.path;
                        obj3 = (photoEntry.bucketId == 0 && photoEntry.dateTaken == 0 && this.imagesArrLocals.size() == gallery_menu_save) ? gallery_menu_save : null;
                        CharSequence charSequence2 = photoEntry.caption;
                        z3 = photoEntry.isVideo;
                        charSequence = charSequence2;
                    } else if (obj4 instanceof BotInlineResult) {
                        BotInlineResult botInlineResult = (BotInlineResult) obj4;
                        this.currentBotInlineResult = botInlineResult;
                        if (botInlineResult.document != null) {
                            boolean isVideoDocument = MessageObject.isVideoDocument(botInlineResult.document);
                            this.currentPathObject = FileLoader.getPathToAttach(botInlineResult.document).getAbsolutePath();
                            z2 = isVideoDocument;
                        } else if (botInlineResult.photo != null) {
                            this.currentPathObject = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize())).getAbsolutePath();
                            z2 = false;
                        } else if (botInlineResult.content_url != null) {
                            this.currentPathObject = botInlineResult.content_url;
                            z2 = botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO);
                        } else {
                            z2 = false;
                        }
                        obj3 = null;
                        z3 = z2;
                        charSequence = null;
                    } else if (obj4 instanceof SearchImage) {
                        SearchImage searchImage = (SearchImage) obj4;
                        if (searchImage.document != null) {
                            this.currentPathObject = FileLoader.getPathToAttach(searchImage.document, true).getAbsolutePath();
                        } else {
                            this.currentPathObject = searchImage.imageUrl;
                        }
                        charSequence = searchImage.caption;
                        z3 = false;
                        obj3 = null;
                    } else {
                        charSequence = null;
                        z3 = false;
                        obj3 = null;
                    }
                    if (obj3 == null) {
                        ActionBar actionBar2 = this.actionBar;
                        Object[] objArr2 = new Object[gallery_menu_showall];
                        objArr2[0] = Integer.valueOf(this.currentIndex + gallery_menu_save);
                        objArr2[gallery_menu_save] = Integer.valueOf(this.imagesArrLocals.size());
                        actionBar2.setTitle(LocaleController.formatString("Of", C0338R.string.Of, objArr2));
                    } else if (z3) {
                        this.muteItemAvailable = true;
                        this.actionBar.setTitle(LocaleController.getString("AttachVideo", C0338R.string.AttachVideo));
                    } else {
                        this.actionBar.setTitle(LocaleController.getString("AttachPhoto", C0338R.string.AttachPhoto));
                    }
                    if (this.sendPhotoType == 0) {
                        this.checkImageView.setChecked(this.placeProvider.isPhotoChecked(this.currentIndex), false);
                    }
                    setCurrentCaption(charSequence);
                    updateCaptionTextForCurrentPhoto(obj4);
                }
            } else if (this.currentIndex < 0 || this.currentIndex >= this.imagesArr.size()) {
                closePhoto(false, false);
                return;
            } else {
                Object[] objArr3;
                MessageObject messageObject = (MessageObject) this.imagesArr.get(this.currentIndex);
                Object obj5 = (this.currentMessageObject == null || this.currentMessageObject.getId() != messageObject.getId()) ? null : gallery_menu_save;
                this.currentMessageObject = messageObject;
                obj2 = (this.currentMessageObject.isVideo() || isGifToPlay()) ? gallery_menu_save : null;
                ActionBarMenuItem actionBarMenuItem = this.masksItem;
                i4 = (!this.currentMessageObject.hasPhotoStickers() || ((int) this.currentMessageObject.getDialogId()) == 0) ? 4 : 0;
                actionBarMenuItem.setVisibility(i4);
                if (this.currentMessageObject.canDeleteMessage(null)) {
                    this.menuItem.showSubItem(gallery_menu_delete);
                } else {
                    this.menuItem.hideSubItem(gallery_menu_delete);
                }
                if (obj2 == null || VERSION.SDK_INT < 16) {
                    this.menuItem.hideSubItem(gallery_menu_openin);
                } else {
                    this.menuItem.showSubItem(gallery_menu_openin);
                }
                if (this.currentMessageObject.isFromUser()) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
                    if (user != null) {
                        this.nameTextView.setText(UserObject.getUserName(user));
                    } else {
                        this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                } else {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
                    if (chat != null) {
                        this.nameTextView.setText(chat.title);
                    } else {
                        this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                }
                long j = (long) this.currentMessageObject.messageOwner.date;
                Object[] objArr4 = new Object[gallery_menu_showall];
                objArr4[0] = LocaleController.formatYear(j);
                objArr4[gallery_menu_save] = LocaleController.getInstance().formatterDay.format(new Date(j * 1000));
                CharSequence formatString = LocaleController.formatString("formatDateAtTime", C0338R.string.formatDateAtTime, objArr4);
                if (this.currentFileNames[0] == null || obj2 == null) {
                    this.dateTextView.setText(formatString);
                } else {
                    TextView textView = this.dateTextView;
                    objArr3 = new Object[gallery_menu_showall];
                    objArr3[0] = formatString;
                    objArr3[gallery_menu_save] = AndroidUtilities.formatFileSize((long) this.currentMessageObject.getDocument().size);
                    textView.setText(String.format("%s (%s)", objArr3));
                }
                setCurrentCaption(this.currentMessageObject.caption);
                if (this.currentAnimation != null) {
                    this.menuItem.hideSubItem(gallery_menu_share);
                    if (!this.currentMessageObject.canDeleteMessage(null)) {
                        this.menuItem.setVisibility(8);
                    }
                    this.allowShare = true;
                    this.shareButton.setVisibility(0);
                    this.actionBar.setTitle(LocaleController.getString("AttachGif", C0338R.string.AttachGif));
                } else {
                    if (this.totalImagesCount + this.totalImagesCountMerge == 0 || this.needSearchImageInArr) {
                        if (this.currentMessageObject.messageOwner.media instanceof TL_messageMediaWebPage) {
                            if (this.currentMessageObject.isVideo()) {
                                this.actionBar.setTitle(LocaleController.getString("AttachVideo", C0338R.string.AttachVideo));
                            } else {
                                this.actionBar.setTitle(LocaleController.getString("AttachPhoto", C0338R.string.AttachPhoto));
                            }
                        }
                    } else if (this.opennedFromMedia) {
                        if (this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge && !this.loadingMoreImages && this.currentIndex > this.imagesArr.size() - 5) {
                            i4 = this.imagesArr.isEmpty() ? 0 : ((MessageObject) this.imagesArr.get(this.imagesArr.size() - 1)).getId();
                            i2 = 0;
                            if (!this.endReached[0] || this.mergeDialogId == 0) {
                                r6 = i4;
                            } else if (this.imagesArr.isEmpty() || ((MessageObject) this.imagesArr.get(this.imagesArr.size() - 1)).getDialogId() == this.mergeDialogId) {
                                i2 = gallery_menu_save;
                                r6 = i4;
                            } else {
                                i2 = gallery_menu_save;
                                r6 = 0;
                            }
                            SharedMediaQuery.loadMedia(i2 == 0 ? this.currentDialogId : this.mergeDialogId, 0, 80, r6, 0, true, this.classGuid);
                            this.loadingMoreImages = true;
                        }
                        r2 = this.actionBar;
                        objArr3 = new Object[gallery_menu_showall];
                        objArr3[0] = Integer.valueOf(this.currentIndex + gallery_menu_save);
                        objArr3[gallery_menu_save] = Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge);
                        r2.setTitle(LocaleController.formatString("Of", C0338R.string.Of, objArr3));
                    } else {
                        if (this.imagesArr.size() < this.totalImagesCount + this.totalImagesCountMerge && !this.loadingMoreImages && this.currentIndex < 5) {
                            i4 = this.imagesArr.isEmpty() ? 0 : ((MessageObject) this.imagesArr.get(0)).getId();
                            i2 = 0;
                            if (!this.endReached[0] || this.mergeDialogId == 0) {
                                r6 = i4;
                            } else if (this.imagesArr.isEmpty() || ((MessageObject) this.imagesArr.get(0)).getDialogId() == this.mergeDialogId) {
                                i2 = gallery_menu_save;
                                r6 = i4;
                            } else {
                                i2 = gallery_menu_save;
                                r6 = 0;
                            }
                            SharedMediaQuery.loadMedia(i2 == 0 ? this.currentDialogId : this.mergeDialogId, 0, 80, r6, 0, true, this.classGuid);
                            this.loadingMoreImages = true;
                        }
                        r2 = this.actionBar;
                        objArr3 = new Object[gallery_menu_showall];
                        objArr3[0] = Integer.valueOf((((this.totalImagesCount + this.totalImagesCountMerge) - this.imagesArr.size()) + this.currentIndex) + gallery_menu_save);
                        objArr3[gallery_menu_save] = Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge);
                        r2.setTitle(LocaleController.formatString("Of", C0338R.string.Of, objArr3));
                    }
                    if (this.currentMessageObject.messageOwner.ttl != 0) {
                        this.allowShare = false;
                        this.menuItem.hideSubItem(gallery_menu_save);
                        this.shareButton.setVisibility(8);
                        this.menuItem.hideSubItem(gallery_menu_share);
                    } else {
                        this.allowShare = true;
                        this.menuItem.showSubItem(gallery_menu_save);
                        ImageView imageView2 = this.shareButton;
                        i2 = (this.videoPlayerControlFrameLayout == null || this.videoPlayerControlFrameLayout.getVisibility() != 0) ? 0 : 8;
                        imageView2.setVisibility(i2);
                        if (this.shareButton.getVisibility() == 0) {
                            this.menuItem.hideSubItem(gallery_menu_share);
                        } else {
                            this.menuItem.showSubItem(gallery_menu_share);
                        }
                    }
                    if (isGifToPlay()) {
                        this.actionBar.setTitle(LocaleController.getString("AttachGif", C0338R.string.AttachGif));
                    }
                }
                obj = obj5;
            }
            if (this.currentPlaceObject != null) {
                if (this.animationInProgress == 0) {
                    this.currentPlaceObject.imageReceiver.setVisible(true, true);
                } else {
                    this.showAfterAnimation = this.currentPlaceObject;
                }
            }
            this.currentPlaceObject = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
            if (this.currentPlaceObject != null) {
                if (this.animationInProgress == 0) {
                    this.currentPlaceObject.imageReceiver.setVisible(false, true);
                } else {
                    this.hideAfterAnimation = this.currentPlaceObject;
                }
            }
            if (obj == null) {
                this.draggingDown = false;
                this.translationX = 0.0f;
                this.translationY = 0.0f;
                this.scale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                this.animateToX = 0.0f;
                this.animateToY = 0.0f;
                this.animateToScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                this.animationStartTime = 0;
                this.imageMoveAnimation = null;
                this.changeModeAnimation = null;
                if (this.aspectRatioFrameLayout != null) {
                    this.aspectRatioFrameLayout.setVisibility(4);
                }
                releasePlayer();
                this.pinchStartDistance = 0.0f;
                this.pinchStartScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                this.pinchCenterX = 0.0f;
                this.pinchCenterY = 0.0f;
                this.pinchStartX = 0.0f;
                this.pinchStartY = 0.0f;
                this.moveStartX = 0.0f;
                this.moveStartY = 0.0f;
                this.zooming = false;
                this.moving = false;
                this.doubleTap = false;
                this.invalidCoords = false;
                this.canDragDown = true;
                this.changingPage = false;
                this.switchImageAfterAnimation = 0;
                z2 = !this.imagesArrLocals.isEmpty() || (!(this.currentFileNames[0] == null || this.radialProgressViews[0].backgroundState == 0) || isGifToPlay());
                this.canZoom = z2;
                updateMinMax(this.scale);
            }
            if (i3 == -1) {
                setImages();
                for (i2 = 0; i2 < gallery_menu_send; i2 += gallery_menu_save) {
                    checkProgress(i2, false);
                }
                return;
            }
            checkProgress(0, false);
            ImageReceiver imageReceiver;
            RadialProgressView radialProgressView;
            if (i3 > this.currentIndex) {
                imageReceiver = this.rightImage;
                this.rightImage = this.centerImage;
                this.centerImage = this.leftImage;
                this.leftImage = imageReceiver;
                radialProgressView = this.radialProgressViews[0];
                this.radialProgressViews[0] = this.radialProgressViews[gallery_menu_showall];
                this.radialProgressViews[gallery_menu_showall] = radialProgressView;
                setIndexToImage(this.leftImage, this.currentIndex - 1);
                checkProgress(gallery_menu_save, false);
                checkProgress(gallery_menu_showall, false);
            } else if (i3 < this.currentIndex) {
                imageReceiver = this.leftImage;
                this.leftImage = this.centerImage;
                this.centerImage = this.rightImage;
                this.rightImage = imageReceiver;
                radialProgressView = this.radialProgressViews[0];
                this.radialProgressViews[0] = this.radialProgressViews[gallery_menu_save];
                this.radialProgressViews[gallery_menu_save] = radialProgressView;
                setIndexToImage(this.rightImage, this.currentIndex + gallery_menu_save);
                checkProgress(gallery_menu_save, false);
                checkProgress(gallery_menu_showall, false);
            }
        }
    }

    private void setImages() {
        if (this.animationInProgress == 0 && !this.isGif) {
            setIndexToImage(this.centerImage, this.currentIndex);
            setIndexToImage(this.rightImage, this.currentIndex + gallery_menu_save);
            setIndexToImage(this.leftImage, this.currentIndex - 1);
        }
    }

    private void setIndexToImage(ImageReceiver imageReceiver, int i) {
        imageReceiver.setOrientation(0, false);
        TLObject fileLocation;
        if (this.imagesArrLocals.isEmpty()) {
            int[] iArr = new int[gallery_menu_save];
            fileLocation = getFileLocation(i, iArr);
            if (fileLocation != null) {
                MessageObject messageObject = null;
                if (!this.imagesArr.isEmpty()) {
                    messageObject = (MessageObject) this.imagesArr.get(i);
                }
                imageReceiver.setParentMessageObject(messageObject);
                if (messageObject != null) {
                    imageReceiver.setShouldGenerateQualityThumb(true);
                }
                Bitmap bitmap;
                if (messageObject != null && messageObject.isVideo()) {
                    imageReceiver.setNeedsQualityThumb(true);
                    if (messageObject.photoThumbs == null || messageObject.photoThumbs.isEmpty()) {
                        imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(C0338R.drawable.photoview_placeholder));
                        return;
                    }
                    bitmap = (this.currentThumb == null || imageReceiver != this.centerImage) ? null : this.currentThumb;
                    imageReceiver.setImage(null, null, null, bitmap != null ? new BitmapDrawable(null, bitmap) : null, FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100).location, "b", 0, null, true);
                    return;
                } else if (messageObject == null || this.currentAnimation == null) {
                    imageReceiver.setNeedsQualityThumb(false);
                    bitmap = (this.currentThumb == null || imageReceiver != this.centerImage) ? null : this.currentThumb;
                    if (iArr[0] == 0) {
                        iArr[0] = -1;
                    }
                    PhotoSize closestPhotoSizeWithSize = messageObject != null ? FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100) : null;
                    imageReceiver.setImage(fileLocation, null, null, bitmap != null ? new BitmapDrawable(null, bitmap) : null, closestPhotoSizeWithSize != null ? closestPhotoSizeWithSize.location : null, "b", iArr[0], null, this.avatarsDialogId != 0);
                    return;
                } else {
                    imageReceiver.setImageBitmap(this.currentAnimation);
                    this.currentAnimation.setSecondParentView(this.containerView);
                    return;
                }
            }
            imageReceiver.setNeedsQualityThumb(false);
            imageReceiver.setParentMessageObject(null);
            if (iArr[0] == 0) {
                imageReceiver.setImageBitmap((Bitmap) null);
                return;
            } else {
                imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(C0338R.drawable.photoview_placeholder));
                return;
            }
        }
        imageReceiver.setParentMessageObject(null);
        if (i < 0 || i >= this.imagesArrLocals.size()) {
            imageReceiver.setImageBitmap((Bitmap) null);
            return;
        }
        int i2;
        TLObject tLObject;
        String str;
        Object obj = this.imagesArrLocals.get(i);
        int photoSize = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
        Bitmap bitmap2 = null;
        if (this.currentThumb != null && imageReceiver == this.centerImage) {
            bitmap2 = this.currentThumb;
        }
        Bitmap thumbForPhoto = bitmap2 == null ? this.placeProvider.getThumbForPhoto(null, null, i) : bitmap2;
        String str2 = null;
        TLObject tLObject2 = null;
        int i3 = 0;
        String str3 = null;
        Object[] objArr;
        if (obj instanceof PhotoEntry) {
            String str4;
            PhotoEntry photoEntry = (PhotoEntry) obj;
            if (photoEntry.isVideo) {
                str4 = null;
                str3 = null;
            } else {
                if (photoEntry.imagePath != null) {
                    str4 = photoEntry.imagePath;
                } else {
                    imageReceiver.setOrientation(photoEntry.orientation, false);
                    str4 = photoEntry.path;
                }
                objArr = new Object[gallery_menu_showall];
                objArr[0] = Integer.valueOf(photoSize);
                objArr[gallery_menu_save] = Integer.valueOf(photoSize);
                str3 = str4;
                str4 = String.format(Locale.US, "%d_%d", objArr);
            }
            i2 = 0;
            tLObject = null;
            str2 = str3;
            fileLocation = null;
            str = str4;
        } else if (obj instanceof BotInlineResult) {
            String str5;
            TLObject tLObject3;
            int i4;
            TLObject tLObject4;
            BotInlineResult botInlineResult = (BotInlineResult) obj;
            if (botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(botInlineResult.document)) {
                if (botInlineResult.document != null) {
                    str5 = null;
                    tLObject3 = botInlineResult.document.thumb.location;
                    i4 = 0;
                    tLObject4 = null;
                } else {
                    str2 = botInlineResult.thumb_url;
                    i4 = 0;
                    tLObject4 = null;
                    str5 = str2;
                    tLObject3 = null;
                }
            } else if (botInlineResult.type.equals("gif") && botInlineResult.document != null) {
                tLObject4 = botInlineResult.document;
                i4 = botInlineResult.document.size;
                str3 = "d";
                str5 = null;
                tLObject3 = null;
            } else if (botInlineResult.photo != null) {
                PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, AndroidUtilities.getPhotoSize());
                tLObject4 = closestPhotoSizeWithSize2.location;
                i4 = closestPhotoSizeWithSize2.size;
                objArr = new Object[gallery_menu_showall];
                objArr[0] = Integer.valueOf(photoSize);
                objArr[gallery_menu_save] = Integer.valueOf(photoSize);
                str3 = String.format(Locale.US, "%d_%d", objArr);
                str5 = null;
                tLObject3 = tLObject4;
                tLObject4 = null;
            } else if (botInlineResult.content_url != null) {
                if (botInlineResult.type.equals("gif")) {
                    str3 = "d";
                } else {
                    objArr = new Object[gallery_menu_showall];
                    objArr[0] = Integer.valueOf(photoSize);
                    objArr[gallery_menu_save] = Integer.valueOf(photoSize);
                    str3 = String.format(Locale.US, "%d_%d", objArr);
                }
                str2 = botInlineResult.content_url;
                i4 = 0;
                tLObject4 = null;
                str5 = str2;
                tLObject3 = null;
            } else {
                i4 = 0;
                tLObject4 = null;
                str5 = null;
                tLObject3 = null;
            }
            i2 = i4;
            tLObject = tLObject3;
            str2 = str5;
            TLObject tLObject5 = tLObject4;
            str = str3;
            fileLocation = tLObject5;
        } else if (obj instanceof SearchImage) {
            SearchImage searchImage = (SearchImage) obj;
            if (searchImage.imagePath != null) {
                str2 = searchImage.imagePath;
            } else if (searchImage.document != null) {
                tLObject2 = searchImage.document;
                i3 = searchImage.document.size;
            } else {
                str2 = searchImage.imageUrl;
                i3 = searchImage.size;
            }
            i2 = i3;
            tLObject = null;
            str = "d";
            fileLocation = tLObject2;
        } else {
            i2 = 0;
            tLObject = null;
            str = null;
            fileLocation = null;
        }
        Drawable bitmapDrawable;
        if (fileLocation != null) {
            str = "d";
            bitmapDrawable = thumbForPhoto != null ? new BitmapDrawable(null, thumbForPhoto) : null;
            FileLocation fileLocation2 = thumbForPhoto == null ? fileLocation.thumb.location : null;
            Object[] objArr2 = new Object[gallery_menu_showall];
            objArr2[0] = Integer.valueOf(photoSize);
            objArr2[gallery_menu_save] = Integer.valueOf(photoSize);
            imageReceiver.setImage(fileLocation, null, str, bitmapDrawable, fileLocation2, String.format(Locale.US, "%d_%d", objArr2), i2, null, false);
        } else if (tLObject != null) {
            bitmapDrawable = thumbForPhoto != null ? new BitmapDrawable(null, thumbForPhoto) : null;
            Object[] objArr3 = new Object[gallery_menu_showall];
            objArr3[0] = Integer.valueOf(photoSize);
            objArr3[gallery_menu_save] = Integer.valueOf(photoSize);
            imageReceiver.setImage(tLObject, null, str, bitmapDrawable, null, String.format(Locale.US, "%d_%d", objArr3), i2, null, false);
        } else {
            imageReceiver.setImage(str2, str, thumbForPhoto != null ? new BitmapDrawable(null, thumbForPhoto) : null, null, i2);
        }
    }

    private void setScaleToFill() {
        float bitmapWidth = (float) this.centerImage.getBitmapWidth();
        float containerViewWidth = (float) getContainerViewWidth();
        float bitmapHeight = (float) this.centerImage.getBitmapHeight();
        float containerViewHeight = (float) getContainerViewHeight();
        float min = Math.min(containerViewHeight / bitmapHeight, containerViewWidth / bitmapWidth);
        this.scale = Math.max(containerViewWidth / ((float) ((int) (bitmapWidth * min))), containerViewHeight / ((float) ((int) (bitmapHeight * min))));
        updateMinMax(this.scale);
    }

    private void switchToEditMode(int i) {
        if (this.currentEditMode == i || this.centerImage.getBitmap() == null || this.changeModeAnimation != null || this.imageMoveAnimation != null || this.radialProgressViews[0].backgroundState != -1) {
            return;
        }
        float[] fArr;
        if (i == 0) {
            if (this.currentEditMode != gallery_menu_showall || this.photoFilterView.getToolsView().getVisibility() == 0) {
                if (this.centerImage.getBitmap() != null) {
                    int bitmapWidth = this.centerImage.getBitmapWidth();
                    int bitmapHeight = this.centerImage.getBitmapHeight();
                    float containerViewWidth = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                    float containerViewHeight = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                    float containerViewWidth2 = ((float) getContainerViewWidth(0)) / ((float) bitmapWidth);
                    float containerViewHeight2 = ((float) getContainerViewHeight(0)) / ((float) bitmapHeight);
                    if (containerViewWidth <= containerViewHeight) {
                        containerViewHeight = containerViewWidth;
                    }
                    if (containerViewWidth2 <= containerViewHeight2) {
                        containerViewHeight2 = containerViewWidth2;
                    }
                    this.animateToScale = containerViewHeight2 / containerViewHeight;
                    this.animateToX = 0.0f;
                    if (this.currentEditMode == gallery_menu_save) {
                        this.animateToY = (float) AndroidUtilities.dp(24.0f);
                    } else if (this.currentEditMode == gallery_menu_showall) {
                        this.animateToY = (float) AndroidUtilities.dp(62.0f);
                    } else if (this.currentEditMode == gallery_menu_send) {
                        this.animateToY = (float) ((AndroidUtilities.dp(48.0f) - ActionBar.getCurrentActionBarHeight()) / gallery_menu_showall);
                    }
                    if (VERSION.SDK_INT >= 21) {
                        this.animateToY -= (float) (AndroidUtilities.statusBarHeight / gallery_menu_showall);
                    }
                    this.animationStartTime = System.currentTimeMillis();
                    this.zoomAnimation = true;
                }
                this.imageMoveAnimation = new AnimatorSet();
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                float[] fArr2;
                if (this.currentEditMode == gallery_menu_save) {
                    animatorSet = this.imageMoveAnimation;
                    animatorArr = new Animator[gallery_menu_send];
                    fArr2 = new float[gallery_menu_save];
                    fArr2[0] = (float) AndroidUtilities.dp(48.0f);
                    animatorArr[0] = ObjectAnimator.ofFloat(this.editorDoneLayout, "translationY", fArr2);
                    animatorArr[gallery_menu_save] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                    fArr2 = new float[gallery_menu_save];
                    fArr2[0] = 0.0f;
                    animatorArr[gallery_menu_showall] = ObjectAnimator.ofFloat(this.photoCropView, "alpha", fArr2);
                    animatorSet.playTogether(animatorArr);
                } else if (this.currentEditMode == gallery_menu_showall) {
                    this.photoFilterView.shutdown();
                    animatorSet = this.imageMoveAnimation;
                    animatorArr = new Animator[gallery_menu_showall];
                    fArr = new float[gallery_menu_save];
                    fArr[0] = (float) AndroidUtilities.dp(126.0f);
                    animatorArr[0] = ObjectAnimator.ofFloat(this.photoFilterView.getToolsView(), "translationY", fArr);
                    animatorArr[gallery_menu_save] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                    animatorSet.playTogether(animatorArr);
                } else if (this.currentEditMode == gallery_menu_send) {
                    this.photoPaintView.shutdown();
                    AnimatorSet animatorSet2 = this.imageMoveAnimation;
                    Animator[] animatorArr2 = new Animator[4];
                    fArr = new float[gallery_menu_save];
                    fArr[0] = (float) AndroidUtilities.dp(126.0f);
                    animatorArr2[0] = ObjectAnimator.ofFloat(this.photoPaintView.getToolsView(), "translationY", fArr);
                    fArr = new float[gallery_menu_save];
                    fArr[0] = (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
                    animatorArr2[gallery_menu_save] = ObjectAnimator.ofFloat(this.photoPaintView.getColorPicker(), "translationX", fArr);
                    ActionBar actionBar = this.photoPaintView.getActionBar();
                    String str = "translationY";
                    fArr2 = new float[gallery_menu_save];
                    fArr2[0] = (float) ((-ActionBar.getCurrentActionBarHeight()) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
                    animatorArr2[gallery_menu_showall] = ObjectAnimator.ofFloat(actionBar, str, fArr2);
                    animatorArr2[gallery_menu_send] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                    animatorSet2.playTogether(animatorArr2);
                }
                this.imageMoveAnimation.setDuration(200);
                this.imageMoveAnimation.addListener(new AnonymousClass29(i));
                this.imageMoveAnimation.start();
                return;
            }
            this.photoFilterView.switchToOrFromEditMode();
        } else if (i == gallery_menu_save) {
            if (this.photoCropView == null) {
                this.photoCropView = new PhotoCropView(this.actvityContext);
                this.photoCropView.setVisibility(8);
                this.containerView.addView(this.photoCropView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 0.0f, 0.0f, 48.0f));
                this.photoCropView.setDelegate(new PhotoCropViewDelegate() {
                    public Bitmap getBitmap() {
                        return PhotoViewer.this.centerImage.getBitmap();
                    }

                    public void needMoveImageTo(float f, float f2, float f3, boolean z) {
                        if (z) {
                            PhotoViewer.this.animateTo(f3, f, f2, true);
                            return;
                        }
                        PhotoViewer.this.translationX = f;
                        PhotoViewer.this.translationY = f2;
                        PhotoViewer.this.scale = f3;
                        PhotoViewer.this.containerView.invalidate();
                    }
                });
            }
            this.editorDoneLayout.doneButton.setText(LocaleController.getString("Crop", C0338R.string.Crop));
            this.changeModeAnimation = new AnimatorSet();
            r0 = new ArrayList();
            fArr = new float[gallery_menu_showall];
            fArr[0] = 0.0f;
            fArr[gallery_menu_save] = (float) AndroidUtilities.dp(96.0f);
            r0.add(ObjectAnimator.ofFloat(this.pickerView, "translationY", fArr));
            fArr = new float[gallery_menu_showall];
            fArr[0] = 0.0f;
            fArr[gallery_menu_save] = (float) (-this.actionBar.getHeight());
            r0.add(ObjectAnimator.ofFloat(this.actionBar, "translationY", fArr));
            if (this.needCaptionLayout) {
                fArr = new float[gallery_menu_showall];
                fArr[0] = 0.0f;
                fArr[gallery_menu_save] = (float) AndroidUtilities.dp(96.0f);
                r0.add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", fArr));
            }
            if (this.sendPhotoType == 0) {
                r0.add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f}));
            }
            this.changeModeAnimation.playTogether(r0);
            this.changeModeAnimation.setDuration(200);
            this.changeModeAnimation.addListener(new AnonymousClass31(i));
            this.changeModeAnimation.start();
        } else if (i == gallery_menu_showall) {
            if (this.photoFilterView == null) {
                this.photoFilterView = new PhotoFilterView(this.parentActivity, this.centerImage.getBitmap(), this.centerImage.getOrientation());
                this.containerView.addView(this.photoFilterView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
                this.photoFilterView.getDoneTextView().setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        PhotoViewer.this.applyCurrentEditMode();
                        PhotoViewer.this.switchToEditMode(0);
                    }
                });
                this.photoFilterView.getCancelTextView().setOnClickListener(new OnClickListener() {

                    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.33.1 */
                    class C17821 implements DialogInterface.OnClickListener {
                        C17821() {
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            PhotoViewer.this.switchToEditMode(0);
                        }
                    }

                    public void onClick(View view) {
                        if (!PhotoViewer.this.photoFilterView.hasChanges()) {
                            PhotoViewer.this.switchToEditMode(0);
                        } else if (PhotoViewer.this.parentActivity != null) {
                            Builder builder = new Builder(PhotoViewer.this.parentActivity);
                            builder.setMessage(LocaleController.getString("DiscardChanges", C0338R.string.DiscardChanges));
                            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C17821());
                            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                            PhotoViewer.this.showAlertDialog(builder);
                        }
                    }
                });
                this.photoFilterView.getToolsView().setTranslationY((float) AndroidUtilities.dp(126.0f));
            }
            this.changeModeAnimation = new AnimatorSet();
            r0 = new ArrayList();
            fArr = new float[gallery_menu_showall];
            fArr[0] = 0.0f;
            fArr[gallery_menu_save] = (float) AndroidUtilities.dp(96.0f);
            r0.add(ObjectAnimator.ofFloat(this.pickerView, "translationY", fArr));
            fArr = new float[gallery_menu_showall];
            fArr[0] = 0.0f;
            fArr[gallery_menu_save] = (float) (-this.actionBar.getHeight());
            r0.add(ObjectAnimator.ofFloat(this.actionBar, "translationY", fArr));
            if (this.needCaptionLayout) {
                fArr = new float[gallery_menu_showall];
                fArr[0] = 0.0f;
                fArr[gallery_menu_save] = (float) AndroidUtilities.dp(96.0f);
                r0.add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", fArr));
            }
            if (this.sendPhotoType == 0) {
                r0.add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f}));
            }
            this.changeModeAnimation.playTogether(r0);
            this.changeModeAnimation.setDuration(200);
            this.changeModeAnimation.addListener(new AnonymousClass34(i));
            this.changeModeAnimation.start();
        } else if (i == gallery_menu_send) {
            if (this.photoPaintView == null) {
                this.photoPaintView = new PhotoPaintView(this.parentActivity, this.centerImage.getBitmap(), this.centerImage.getOrientation());
                this.containerView.addView(this.photoPaintView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
                this.photoPaintView.getDoneTextView().setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        PhotoViewer.this.applyCurrentEditMode();
                        PhotoViewer.this.switchToEditMode(0);
                    }
                });
                this.photoPaintView.getCancelTextView().setOnClickListener(new OnClickListener() {

                    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.36.1 */
                    class C17841 implements Runnable {
                        C17841() {
                        }

                        public void run() {
                            PhotoViewer.this.switchToEditMode(0);
                        }
                    }

                    public void onClick(View view) {
                        PhotoViewer.this.photoPaintView.maybeShowDismissalAlert(PhotoViewer.this, PhotoViewer.this.parentActivity, new C17841());
                    }
                });
                this.photoPaintView.getColorPicker().setTranslationX((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW));
                this.photoPaintView.getToolsView().setTranslationY((float) AndroidUtilities.dp(126.0f));
                this.photoPaintView.getActionBar().setTranslationY((float) ((-ActionBar.getCurrentActionBarHeight()) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }
            this.changeModeAnimation = new AnimatorSet();
            r0 = new ArrayList();
            fArr = new float[gallery_menu_showall];
            fArr[0] = 0.0f;
            fArr[gallery_menu_save] = (float) AndroidUtilities.dp(96.0f);
            r0.add(ObjectAnimator.ofFloat(this.pickerView, "translationY", fArr));
            fArr = new float[gallery_menu_showall];
            fArr[0] = 0.0f;
            fArr[gallery_menu_save] = (float) (-this.actionBar.getHeight());
            r0.add(ObjectAnimator.ofFloat(this.actionBar, "translationY", fArr));
            if (this.needCaptionLayout) {
                fArr = new float[gallery_menu_showall];
                fArr[0] = 0.0f;
                fArr[gallery_menu_save] = (float) AndroidUtilities.dp(96.0f);
                r0.add(ObjectAnimator.ofFloat(this.captionTextView, "translationY", fArr));
            }
            if (this.sendPhotoType == 0) {
                r0.add(ObjectAnimator.ofFloat(this.checkImageView, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f}));
            }
            this.changeModeAnimation.playTogether(r0);
            this.changeModeAnimation.setDuration(200);
            this.changeModeAnimation.addListener(new AnonymousClass37(i));
            this.changeModeAnimation.start();
        }
    }

    private void toggleActionBar(boolean z, boolean z2) {
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        if (z) {
            this.actionBar.setVisibility(0);
            if (this.canShowBottom) {
                this.bottomLayout.setVisibility(0);
                if (this.captionTextView.getTag() != null) {
                    this.captionTextView.setVisibility(0);
                }
            }
        }
        this.isActionBarVisible = z;
        this.actionBar.setEnabled(z);
        this.bottomLayout.setEnabled(z);
        if (z2) {
            Collection arrayList = new ArrayList();
            ActionBar actionBar = this.actionBar;
            String str = "alpha";
            float[] fArr = new float[gallery_menu_save];
            fArr[0] = z ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, str, fArr));
            FrameLayout frameLayout = this.bottomLayout;
            str = "alpha";
            fArr = new float[gallery_menu_save];
            fArr[0] = z ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, str, fArr));
            if (this.captionTextView.getTag() != null) {
                TextView textView = this.captionTextView;
                String str2 = "alpha";
                float[] fArr2 = new float[gallery_menu_save];
                if (!z) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(textView, str2, fArr2));
            }
            this.currentActionBarAnimation = new AnimatorSet();
            this.currentActionBarAnimation.playTogether(arrayList);
            if (!z) {
                this.currentActionBarAnimation.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animator) {
                        if (PhotoViewer.this.currentActionBarAnimation != null && PhotoViewer.this.currentActionBarAnimation.equals(animator)) {
                            PhotoViewer.this.actionBar.setVisibility(8);
                            if (PhotoViewer.this.canShowBottom) {
                                PhotoViewer.this.bottomLayout.setVisibility(8);
                                if (PhotoViewer.this.captionTextView.getTag() != null) {
                                    PhotoViewer.this.captionTextView.setVisibility(4);
                                }
                            }
                            PhotoViewer.this.currentActionBarAnimation = null;
                        }
                    }
                });
            }
            this.currentActionBarAnimation.setDuration(200);
            this.currentActionBarAnimation.start();
            return;
        }
        this.actionBar.setAlpha(z ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f);
        this.bottomLayout.setAlpha(z ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f);
        if (this.captionTextView.getTag() != null) {
            textView = this.captionTextView;
            if (!z) {
                f = 0.0f;
            }
            textView.setAlpha(f);
        }
        if (!z) {
            this.actionBar.setVisibility(8);
            if (this.canShowBottom) {
                this.bottomLayout.setVisibility(8);
                if (this.captionTextView.getTag() != null) {
                    this.captionTextView.setVisibility(4);
                }
            }
        }
    }

    private void toggleCheckImageView(boolean z) {
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        AnimatorSet animatorSet = new AnimatorSet();
        Collection arrayList = new ArrayList();
        PickerBottomLayoutViewer pickerBottomLayoutViewer = this.pickerView;
        String str = "alpha";
        float[] fArr = new float[gallery_menu_save];
        fArr[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(pickerBottomLayoutViewer, str, fArr));
        if (this.needCaptionLayout) {
            TextView textView = this.captionTextView;
            str = "alpha";
            fArr = new float[gallery_menu_save];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(textView, str, fArr));
        }
        if (this.sendPhotoType == 0) {
            CheckBox checkBox = this.checkImageView;
            String str2 = "alpha";
            float[] fArr2 = new float[gallery_menu_save];
            if (!z) {
                f = 0.0f;
            }
            fArr2[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(checkBox, str2, fArr2));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void updateCaptionTextForCurrentPhoto(Object obj) {
        CharSequence charSequence = null;
        if (obj instanceof PhotoEntry) {
            charSequence = ((PhotoEntry) obj).caption;
        } else if (!(obj instanceof BotInlineResult) && (obj instanceof SearchImage)) {
            charSequence = ((SearchImage) obj).caption;
        }
        if (charSequence == null || charSequence.length() == 0) {
            this.captionEditText.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
        } else {
            this.captionEditText.setFieldText(charSequence);
        }
    }

    private void updateMinMax(float f) {
        int imageWidth = ((int) ((((float) this.centerImage.getImageWidth()) * f) - ((float) getContainerViewWidth()))) / gallery_menu_showall;
        int imageHeight = ((int) ((((float) this.centerImage.getImageHeight()) * f) - ((float) getContainerViewHeight()))) / gallery_menu_showall;
        if (imageWidth > 0) {
            this.minX = (float) (-imageWidth);
            this.maxX = (float) imageWidth;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (imageHeight > 0) {
            this.minY = (float) (-imageHeight);
            this.maxY = (float) imageHeight;
        } else {
            this.maxY = 0.0f;
            this.minY = 0.0f;
        }
        if (this.currentEditMode == gallery_menu_save) {
            this.maxX += this.photoCropView.getLimitX();
            this.maxY += this.photoCropView.getLimitY();
            this.minX -= this.photoCropView.getLimitWidth();
            this.minY -= this.photoCropView.getLimitHeight();
        }
    }

    private void updateSelectedCount() {
        if (this.placeProvider != null) {
            this.pickerView.updateSelectedCount(this.placeProvider.getSelectedCount(), false);
        }
    }

    private void updateVideoPlayerTime() {
        CharSequence charSequence;
        if (this.videoPlayer == null) {
            charSequence = "00:00 / 00:00";
        } else {
            long currentPosition = this.videoPlayer.getCurrentPosition() / 1000;
            long duration = this.videoPlayer.getDuration();
            if (this.muteItemAvailable) {
                if (duration >= 30000) {
                    if (this.muteItem.getVisibility() == 0) {
                        this.muteItem.setVisibility(8);
                    }
                } else if (this.muteItem.getVisibility() != 0) {
                    this.muteItem.setVisibility(0);
                }
            }
            if (duration / 1000 == -1 || currentPosition == -1) {
                charSequence = "00:00 / 00:00";
            } else {
                charSequence = String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(currentPosition / 60), Long.valueOf(currentPosition % 60), Long.valueOf(duration / 60), Long.valueOf(duration % 60)});
            }
        }
        if (!TextUtils.equals(this.videoPlayerTime.getText(), charSequence)) {
            this.videoPlayerTime.setText(charSequence);
        }
    }

    public void closePhoto(boolean z, boolean z2) {
        if (z2 || this.currentEditMode == 0) {
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (this.currentEditMode != 0) {
                if (this.currentEditMode == gallery_menu_showall) {
                    this.photoFilterView.shutdown();
                    this.containerView.removeView(this.photoFilterView);
                    this.photoFilterView = null;
                } else if (this.currentEditMode == gallery_menu_save) {
                    this.editorDoneLayout.setVisibility(8);
                    this.photoCropView.setVisibility(8);
                }
                this.currentEditMode = 0;
            }
            if (this.parentActivity != null && this.isVisible && !checkAnimation() && this.placeProvider != null) {
                if (!this.captionEditText.hideActionMode() || z2) {
                    releasePlayer();
                    this.captionEditText.onDestroy();
                    this.parentChatActivity = null;
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileLoadProgressChanged);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaCountDidLoaded);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaDidLoaded);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogPhotosLoaded);
                    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
                    ConnectionsManager.getInstance().cancelRequestsForGuid(this.classGuid);
                    this.isActionBarVisible = false;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                    ConnectionsManager.getInstance().cancelRequestsForGuid(this.classGuid);
                    PlaceProviderObject placeForPhoto = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, this.currentFileLocation, this.currentIndex);
                    int[] iArr;
                    float[] fArr;
                    Animator[] animatorArr;
                    if (z) {
                        Rect drawRegion;
                        this.animationInProgress = gallery_menu_save;
                        this.animatingImageView.setVisibility(0);
                        this.containerView.invalidate();
                        AnimatorSet animatorSet = new AnimatorSet();
                        ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
                        int orientation = this.centerImage.getOrientation();
                        int i = 0;
                        if (!(placeForPhoto == null || placeForPhoto.imageReceiver == null)) {
                            i = placeForPhoto.imageReceiver.getAnimatedOrientation();
                        }
                        if (i == 0) {
                            i = orientation;
                        }
                        this.animatingImageView.setOrientation(i);
                        if (placeForPhoto != null) {
                            this.animatingImageView.setNeedRadius(placeForPhoto.radius != 0);
                            drawRegion = placeForPhoto.imageReceiver.getDrawRegion();
                            layoutParams.width = drawRegion.right - drawRegion.left;
                            layoutParams.height = drawRegion.bottom - drawRegion.top;
                            this.animatingImageView.setImageBitmap(placeForPhoto.thumb);
                        } else {
                            this.animatingImageView.setNeedRadius(false);
                            layoutParams.width = this.centerImage.getImageWidth();
                            layoutParams.height = this.centerImage.getImageHeight();
                            this.animatingImageView.setImageBitmap(this.centerImage.getBitmap());
                            drawRegion = null;
                        }
                        this.animatingImageView.setLayoutParams(layoutParams);
                        float f = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                        float f2 = ((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) / ((float) layoutParams.height);
                        if (f <= f2) {
                            f2 = f;
                        }
                        f = (((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) - ((((float) layoutParams.height) * this.scale) * f2)) / 2.0f;
                        this.animatingImageView.setTranslationX(((((float) AndroidUtilities.displaySize.x) - ((((float) layoutParams.width) * this.scale) * f2)) / 2.0f) + this.translationX);
                        this.animatingImageView.setTranslationY(f + this.translationY);
                        this.animatingImageView.setScaleX(this.scale * f2);
                        this.animatingImageView.setScaleY(f2 * this.scale);
                        if (placeForPhoto != null) {
                            placeForPhoto.imageReceiver.setVisible(false, true);
                            int abs = Math.abs(drawRegion.left - placeForPhoto.imageReceiver.getImageX());
                            int abs2 = Math.abs(drawRegion.top - placeForPhoto.imageReceiver.getImageY());
                            int[] iArr2 = new int[gallery_menu_showall];
                            placeForPhoto.parentView.getLocationInWindow(iArr2);
                            orientation = ((iArr2[gallery_menu_save] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (placeForPhoto.viewY + drawRegion.top)) + placeForPhoto.clipTopAddition;
                            if (orientation < 0) {
                                orientation = 0;
                            }
                            int height = (((placeForPhoto.viewY + drawRegion.top) + (drawRegion.bottom - drawRegion.top)) - ((placeForPhoto.parentView.getHeight() + iArr2[gallery_menu_save]) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) + placeForPhoto.clipBottomAddition;
                            if (height < 0) {
                                height = 0;
                            }
                            orientation = Math.max(orientation, abs2);
                            height = Math.max(height, abs2);
                            this.animationValues[0][0] = this.animatingImageView.getScaleX();
                            this.animationValues[0][gallery_menu_save] = this.animatingImageView.getScaleY();
                            this.animationValues[0][gallery_menu_showall] = this.animatingImageView.getTranslationX();
                            this.animationValues[0][gallery_menu_send] = this.animatingImageView.getTranslationY();
                            this.animationValues[0][4] = 0.0f;
                            this.animationValues[0][5] = 0.0f;
                            this.animationValues[0][gallery_menu_delete] = 0.0f;
                            this.animationValues[0][7] = 0.0f;
                            this.animationValues[gallery_menu_save][0] = placeForPhoto.scale;
                            this.animationValues[gallery_menu_save][gallery_menu_save] = placeForPhoto.scale;
                            this.animationValues[gallery_menu_save][gallery_menu_showall] = ((float) placeForPhoto.viewX) + (((float) drawRegion.left) * placeForPhoto.scale);
                            this.animationValues[gallery_menu_save][gallery_menu_send] = (((float) drawRegion.top) * placeForPhoto.scale) + ((float) placeForPhoto.viewY);
                            this.animationValues[gallery_menu_save][4] = ((float) abs) * placeForPhoto.scale;
                            this.animationValues[gallery_menu_save][5] = ((float) orientation) * placeForPhoto.scale;
                            this.animationValues[gallery_menu_save][gallery_menu_delete] = ((float) height) * placeForPhoto.scale;
                            this.animationValues[gallery_menu_save][7] = (float) placeForPhoto.radius;
                            Animator[] animatorArr2 = new Animator[gallery_menu_send];
                            animatorArr2[0] = ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                            iArr = new int[gallery_menu_save];
                            iArr[0] = 0;
                            animatorArr2[gallery_menu_save] = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", iArr);
                            fArr = new float[gallery_menu_save];
                            fArr[0] = 0.0f;
                            animatorArr2[gallery_menu_showall] = ObjectAnimator.ofFloat(this.containerView, "alpha", fArr);
                            animatorSet.playTogether(animatorArr2);
                        } else {
                            i = (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y;
                            animatorArr = new Animator[4];
                            int[] iArr3 = new int[gallery_menu_save];
                            iArr3[0] = 0;
                            animatorArr[0] = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", iArr3);
                            float[] fArr2 = new float[gallery_menu_save];
                            fArr2[0] = 0.0f;
                            animatorArr[gallery_menu_save] = ObjectAnimator.ofFloat(this.animatingImageView, "alpha", fArr2);
                            ClippingImageView clippingImageView = this.animatingImageView;
                            String str = "translationY";
                            fArr2 = new float[gallery_menu_save];
                            fArr2[0] = this.translationY >= 0.0f ? (float) i : (float) (-i);
                            animatorArr[gallery_menu_showall] = ObjectAnimator.ofFloat(clippingImageView, str, fArr2);
                            fArr = new float[gallery_menu_save];
                            fArr[0] = 0.0f;
                            animatorArr[gallery_menu_send] = ObjectAnimator.ofFloat(this.containerView, "alpha", fArr);
                            animatorSet.playTogether(animatorArr);
                        }
                        this.animationEndRunnable = new AnonymousClass45(placeForPhoto);
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapterProxy() {

                            /* renamed from: com.hanista.mobogram.ui.PhotoViewer.46.1 */
                            class C17901 implements Runnable {
                                C17901() {
                                }

                                public void run() {
                                    if (PhotoViewer.this.animationEndRunnable != null) {
                                        PhotoViewer.this.animationEndRunnable.run();
                                        PhotoViewer.this.animationEndRunnable = null;
                                    }
                                }
                            }

                            public void onAnimationEnd(Animator animator) {
                                AndroidUtilities.runOnUIThread(new C17901());
                            }
                        });
                        this.transitionAnimationStartTime = System.currentTimeMillis();
                        if (VERSION.SDK_INT >= 18) {
                            this.containerView.setLayerType(gallery_menu_showall, null);
                        }
                        animatorSet.start();
                    } else {
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        animatorArr = new Animator[4];
                        fArr = new float[gallery_menu_save];
                        fArr[0] = 0.9f;
                        animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "scaleX", fArr);
                        fArr = new float[gallery_menu_save];
                        fArr[0] = 0.9f;
                        animatorArr[gallery_menu_save] = ObjectAnimator.ofFloat(this.containerView, "scaleY", fArr);
                        iArr = new int[gallery_menu_save];
                        iArr[0] = 0;
                        animatorArr[gallery_menu_showall] = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", iArr);
                        fArr = new float[gallery_menu_save];
                        fArr[0] = 0.0f;
                        animatorArr[gallery_menu_send] = ObjectAnimator.ofFloat(this.containerView, "alpha", fArr);
                        animatorSet2.playTogether(animatorArr);
                        this.animationInProgress = gallery_menu_showall;
                        this.animationEndRunnable = new AnonymousClass47(placeForPhoto);
                        animatorSet2.setDuration(200);
                        animatorSet2.addListener(new AnimatorListenerAdapterProxy() {
                            public void onAnimationEnd(Animator animator) {
                                if (PhotoViewer.this.animationEndRunnable != null) {
                                    PhotoViewer.this.animationEndRunnable.run();
                                    PhotoViewer.this.animationEndRunnable = null;
                                }
                            }
                        });
                        this.transitionAnimationStartTime = System.currentTimeMillis();
                        if (VERSION.SDK_INT >= 18) {
                            this.containerView.setLayerType(gallery_menu_showall, null);
                        }
                        animatorSet2.start();
                    }
                    if (this.currentAnimation != null) {
                        this.currentAnimation.setSecondParentView(null);
                        this.currentAnimation = null;
                        this.centerImage.setImageBitmap((Drawable) null);
                    }
                    if (this.placeProvider instanceof EmptyPhotoViewerProvider) {
                        this.placeProvider.cancelButtonPressed();
                    }
                    if (this.mWakeLock != null) {
                        this.mWakeLock.release();
                        this.mWakeLock = null;
                        if (this.parentActivity != null) {
                            this.parentActivity.getWindow().clearFlags(TLRPC.USER_FLAG_UNUSED);
                        }
                    }
                }
            }
        } else if (this.currentEditMode != gallery_menu_send || this.photoPaintView == null) {
            if (this.currentEditMode == gallery_menu_save) {
                this.photoCropView.cancelAnimationRunnable();
            }
            switchToEditMode(0);
        } else {
            this.photoPaintView.maybeShowDismissalAlert(this, this.parentActivity, new Runnable() {
                public void run() {
                    PhotoViewer.this.switchToEditMode(0);
                }
            });
        }
    }

    public void destroyPhotoViewer() {
        if (this.parentActivity != null && this.windowView != null) {
            releasePlayer();
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (this.captionEditText != null) {
                this.captionEditText.onDestroy();
            }
            Instance = null;
        }
    }

    public void didReceivedNotification(int i, Object... objArr) {
        String str;
        int i2;
        if (i == NotificationCenter.FileDidFailedLoad) {
            str = (String) objArr[0];
            i2 = 0;
            while (i2 < gallery_menu_send) {
                if (this.currentFileNames[i2] == null || !this.currentFileNames[i2].equals(str)) {
                    i2 += gallery_menu_save;
                } else {
                    this.radialProgressViews[i2].setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, true);
                    checkProgress(i2, true);
                    return;
                }
            }
        } else if (i == NotificationCenter.FileDidLoaded) {
            str = (String) objArr[0];
            i2 = 0;
            while (i2 < gallery_menu_send) {
                if (this.currentFileNames[i2] == null || !this.currentFileNames[i2].equals(str)) {
                    i2 += gallery_menu_save;
                } else {
                    this.radialProgressViews[i2].setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, true);
                    checkProgress(i2, true);
                    if (VERSION.SDK_INT >= 16 && i2 == 0) {
                        if (this.currentMessageObject == null || !this.currentMessageObject.isVideo()) {
                            if (this.currentBotInlineResult == null) {
                                return;
                            }
                            if (!(this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                                return;
                            }
                        }
                        onActionClick(false);
                        return;
                    }
                    return;
                }
            }
        } else if (i == NotificationCenter.FileLoadProgressChanged) {
            str = (String) objArr[0];
            r2 = 0;
            while (r2 < gallery_menu_send) {
                if (this.currentFileNames[r2] != null && this.currentFileNames[r2].equals(str)) {
                    this.radialProgressViews[r2].setProgress(((Float) objArr[gallery_menu_save]).floatValue(), true);
                }
                r2 += gallery_menu_save;
            }
        } else if (i == NotificationCenter.dialogPhotosLoaded) {
            i2 = ((Integer) objArr[4]).intValue();
            if (this.avatarsDialogId == ((Integer) objArr[0]).intValue() && this.classGuid == i2) {
                boolean booleanValue = ((Boolean) objArr[gallery_menu_send]).booleanValue();
                r0 = (ArrayList) objArr[5];
                if (!r0.isEmpty()) {
                    this.imagesArrLocations.clear();
                    this.imagesArrLocationsSizes.clear();
                    this.avatarsArr.clear();
                    r3 = 0;
                    r4 = -1;
                    while (r3 < r0.size()) {
                        Photo photo = (Photo) r0.get(r3);
                        if (!(photo == null || (photo instanceof TL_photoEmpty))) {
                            if (photo.sizes == null) {
                                r2 = r4;
                                r3 += gallery_menu_save;
                                r4 = r2;
                            } else {
                                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 640);
                                if (closestPhotoSizeWithSize != null) {
                                    if (r4 == -1 && this.currentFileLocation != null) {
                                        for (r5 = 0; r5 < photo.sizes.size(); r5 += gallery_menu_save) {
                                            PhotoSize photoSize = (PhotoSize) photo.sizes.get(r5);
                                            if (photoSize.location.local_id == this.currentFileLocation.local_id && photoSize.location.volume_id == this.currentFileLocation.volume_id) {
                                                r4 = this.imagesArrLocations.size();
                                                break;
                                            }
                                        }
                                    }
                                    this.imagesArrLocations.add(closestPhotoSizeWithSize.location);
                                    this.imagesArrLocationsSizes.add(Integer.valueOf(closestPhotoSizeWithSize.size));
                                    this.avatarsArr.add(photo);
                                }
                            }
                        }
                        r2 = r4;
                        r3 += gallery_menu_save;
                        r4 = r2;
                    }
                    if (this.avatarsArr.isEmpty()) {
                        this.menuItem.hideSubItem(gallery_menu_delete);
                    } else {
                        this.menuItem.showSubItem(gallery_menu_delete);
                    }
                    this.needSearchImageInArr = false;
                    this.currentIndex = -1;
                    if (r4 != -1) {
                        setImageIndex(r4, true);
                    } else {
                        this.avatarsArr.add(0, new TL_photoEmpty());
                        this.imagesArrLocations.add(0, this.currentFileLocation);
                        this.imagesArrLocationsSizes.add(0, Integer.valueOf(0));
                        setImageIndex(0, true);
                    }
                    if (booleanValue) {
                        MessagesController.getInstance().loadDialogPhotos(this.avatarsDialogId, 0, 80, 0, false, this.classGuid);
                    }
                }
            }
        } else if (i == NotificationCenter.mediaCountDidLoaded) {
            long longValue = ((Long) objArr[0]).longValue();
            if (longValue == this.currentDialogId || longValue == this.mergeDialogId) {
                if (longValue == this.currentDialogId) {
                    this.totalImagesCount = ((Integer) objArr[gallery_menu_save]).intValue();
                } else if (longValue == this.mergeDialogId) {
                    this.totalImagesCountMerge = ((Integer) objArr[gallery_menu_save]).intValue();
                }
                if (this.needSearchImageInArr && this.isFirstLoading) {
                    this.isFirstLoading = false;
                    this.loadingMoreImages = true;
                    SharedMediaQuery.loadMedia(this.currentDialogId, 0, 80, 0, 0, true, this.classGuid);
                } else if (!this.imagesArr.isEmpty()) {
                    ActionBar actionBar;
                    Object[] objArr2;
                    if (this.opennedFromMedia) {
                        actionBar = this.actionBar;
                        objArr2 = new Object[gallery_menu_showall];
                        objArr2[0] = Integer.valueOf(this.currentIndex + gallery_menu_save);
                        objArr2[gallery_menu_save] = Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge);
                        actionBar.setTitle(LocaleController.formatString("Of", C0338R.string.Of, objArr2));
                        return;
                    }
                    actionBar = this.actionBar;
                    objArr2 = new Object[gallery_menu_showall];
                    objArr2[0] = Integer.valueOf((((this.totalImagesCount + this.totalImagesCountMerge) - this.imagesArr.size()) + this.currentIndex) + gallery_menu_save);
                    objArr2[gallery_menu_save] = Integer.valueOf(this.totalImagesCount + this.totalImagesCountMerge);
                    actionBar.setTitle(LocaleController.formatString("Of", C0338R.string.Of, objArr2));
                }
            }
        } else if (i == NotificationCenter.mediaDidLoaded) {
            long longValue2 = ((Long) objArr[0]).longValue();
            int intValue = ((Integer) objArr[gallery_menu_send]).intValue();
            if ((longValue2 == this.currentDialogId || longValue2 == this.mergeDialogId) && intValue == this.classGuid) {
                this.loadingMoreImages = false;
                r3 = longValue2 == this.currentDialogId ? 0 : gallery_menu_save;
                r0 = (ArrayList) objArr[gallery_menu_showall];
                this.endReached[r3] = ((Boolean) objArr[5]).booleanValue();
                if (!this.needSearchImageInArr) {
                    i2 = 0;
                    Iterator it = r0.iterator();
                    while (it.hasNext()) {
                        MessageObject messageObject = (MessageObject) it.next();
                        if (!this.imagesByIds[r3].containsKey(Integer.valueOf(messageObject.getId()))) {
                            i2 += gallery_menu_save;
                            if (this.opennedFromMedia) {
                                this.imagesArr.add(messageObject);
                            } else {
                                this.imagesArr.add(0, messageObject);
                            }
                            this.imagesByIds[r3].put(Integer.valueOf(messageObject.getId()), messageObject);
                        }
                        i2 = i2;
                    }
                    if (this.opennedFromMedia) {
                        if (i2 == 0) {
                            this.totalImagesCount = this.imagesArr.size();
                            this.totalImagesCountMerge = 0;
                        }
                    } else if (i2 != 0) {
                        intValue = this.currentIndex;
                        this.currentIndex = -1;
                        setImageIndex(intValue + i2, true);
                    } else {
                        this.totalImagesCount = this.imagesArr.size();
                        this.totalImagesCountMerge = 0;
                    }
                } else if (!r0.isEmpty() || (r3 == 0 && this.mergeDialogId != 0)) {
                    MessageObject messageObject2 = (MessageObject) this.imagesArr.get(this.currentIndex);
                    int i3 = -1;
                    r5 = 0;
                    for (r4 = 0; r4 < r0.size(); r4 += gallery_menu_save) {
                        MessageObject messageObject3 = (MessageObject) r0.get(r4);
                        if (!this.imagesByIdsTemp[r3].containsKey(Integer.valueOf(messageObject3.getId()))) {
                            this.imagesByIdsTemp[r3].put(Integer.valueOf(messageObject3.getId()), messageObject3);
                            if (this.opennedFromMedia) {
                                this.imagesArrTemp.add(messageObject3);
                                if (messageObject3.getId() == messageObject2.getId()) {
                                    i3 = r5;
                                }
                                r5 += gallery_menu_save;
                            } else {
                                r5 += gallery_menu_save;
                                this.imagesArrTemp.add(0, messageObject3);
                                if (messageObject3.getId() == messageObject2.getId()) {
                                    i3 = r0.size() - r5;
                                }
                            }
                        }
                    }
                    if (r5 == 0 && (r3 != 0 || this.mergeDialogId == 0)) {
                        this.totalImagesCount = this.imagesArr.size();
                        this.totalImagesCountMerge = 0;
                    }
                    if (i3 != -1) {
                        this.imagesArr.clear();
                        this.imagesArr.addAll(this.imagesArrTemp);
                        for (intValue = 0; intValue < gallery_menu_showall; intValue += gallery_menu_save) {
                            this.imagesByIds[intValue].clear();
                            this.imagesByIds[intValue].putAll(this.imagesByIdsTemp[intValue]);
                            this.imagesByIdsTemp[intValue].clear();
                        }
                        this.imagesArrTemp.clear();
                        this.needSearchImageInArr = false;
                        this.currentIndex = -1;
                        if (i3 >= this.imagesArr.size()) {
                            i3 = this.imagesArr.size() - 1;
                        }
                        setImageIndex(i3, true);
                        return;
                    }
                    if (this.opennedFromMedia) {
                        r4 = this.imagesArrTemp.isEmpty() ? 0 : ((MessageObject) this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getId();
                        if (r3 == 0 && this.endReached[r3] && this.mergeDialogId != 0) {
                            r3 = gallery_menu_save;
                            if (!(this.imagesArrTemp.isEmpty() || ((MessageObject) this.imagesArrTemp.get(this.imagesArrTemp.size() - 1)).getDialogId() == this.mergeDialogId)) {
                                r4 = 0;
                            }
                        }
                    } else {
                        r4 = this.imagesArrTemp.isEmpty() ? 0 : ((MessageObject) this.imagesArrTemp.get(0)).getId();
                        if (r3 == 0 && this.endReached[r3] && this.mergeDialogId != 0) {
                            r3 = gallery_menu_save;
                            if (!(this.imagesArrTemp.isEmpty() || ((MessageObject) this.imagesArrTemp.get(0)).getDialogId() == this.mergeDialogId)) {
                                r4 = 0;
                            }
                        }
                    }
                    if (!this.endReached[r3]) {
                        this.loadingMoreImages = true;
                        if (this.opennedFromMedia) {
                            SharedMediaQuery.loadMedia(r3 == 0 ? this.currentDialogId : this.mergeDialogId, 0, 80, r4, 0, true, this.classGuid);
                        } else {
                            SharedMediaQuery.loadMedia(r3 == 0 ? this.currentDialogId : this.mergeDialogId, 0, 80, r4, 0, true, this.classGuid);
                        }
                    }
                } else {
                    this.needSearchImageInArr = false;
                }
            }
        } else if (i == NotificationCenter.emojiDidLoaded && this.captionTextView != null) {
            this.captionTextView.invalidate();
        }
    }

    public float getAnimationValue() {
        return this.animationValue;
    }

    public Activity getParentActivity() {
        return this.parentActivity;
    }

    public boolean isMuteVideo() {
        return this.muteVideo;
    }

    public boolean isShowingImage(MessageObject messageObject) {
        return (!this.isVisible || this.disableShowCheck || messageObject == null || this.currentMessageObject == null || this.currentMessageObject.getId() != messageObject.getId()) ? false : true;
    }

    public boolean isShowingImage(FileLocation fileLocation) {
        return this.isVisible && !this.disableShowCheck && fileLocation != null && this.currentFileLocation != null && fileLocation.local_id == this.currentFileLocation.local_id && fileLocation.volume_id == this.currentFileLocation.volume_id && fileLocation.dc_id == this.currentFileLocation.dc_id;
    }

    public boolean isShowingImage(String str) {
        return (!this.isVisible || this.disableShowCheck || str == null || this.currentPathObject == null || !str.equals(this.currentPathObject)) ? false : true;
    }

    public boolean isVisible() {
        return this.isVisible && this.placeProvider != null;
    }

    public boolean onDoubleTap(MotionEvent motionEvent) {
        if (!this.canZoom) {
            return false;
        }
        if ((this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && (this.translationY != 0.0f || this.translationX != 0.0f)) || this.animationStartTime != 0 || this.animationInProgress != 0) {
            return false;
        }
        if (this.scale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            float x = (motionEvent.getX() - ((float) (getContainerViewWidth() / gallery_menu_showall))) - (((motionEvent.getX() - ((float) (getContainerViewWidth() / gallery_menu_showall))) - this.translationX) * (3.0f / this.scale));
            float y = (motionEvent.getY() - ((float) (getContainerViewHeight() / gallery_menu_showall))) - (((motionEvent.getY() - ((float) (getContainerViewHeight() / gallery_menu_showall))) - this.translationY) * (3.0f / this.scale));
            updateMinMax(3.0f);
            if (x < this.minX) {
                x = this.minX;
            } else if (x > this.maxX) {
                x = this.maxX;
            }
            if (y < this.minY) {
                y = this.minY;
            } else if (y > this.maxY) {
                y = this.maxY;
            }
            animateTo(3.0f, x, y, true);
        } else {
            animateTo(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, 0.0f, true);
        }
        this.doubleTap = true;
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.scale != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(f), Math.round(f2), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.containerView.postInvalidate();
        }
        return false;
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false, false);
        } else if (this.captionDoneItem.getVisibility() != 8) {
            closeCaptionEnter(true);
        }
    }

    public void onResume() {
        redraw(0);
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        boolean z = false;
        if (this.discardTap) {
            return false;
        }
        int access$7900;
        float x;
        float y;
        if (this.canShowBottom) {
            boolean z2 = VERSION.SDK_INT >= 16 && this.aspectRatioFrameLayout != null && this.aspectRatioFrameLayout.getVisibility() == 0;
            if (!(this.radialProgressViews[0] == null || this.containerView == null || z2)) {
                access$7900 = this.radialProgressViews[0].backgroundState;
                if (access$7900 > 0 && access$7900 <= gallery_menu_send) {
                    x = motionEvent.getX();
                    y = motionEvent.getY();
                    if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                        onActionClick(true);
                        checkProgress(0, true);
                        return true;
                    }
                }
            }
            if (!this.isActionBarVisible) {
                z = true;
            }
            toggleActionBar(z, true);
            return true;
        } else if (this.sendPhotoType == 0) {
            this.checkImageView.performClick();
            return true;
        } else if (this.currentBotInlineResult == null) {
            return true;
        } else {
            if (!this.currentBotInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) && !MessageObject.isVideoDocument(this.currentBotInlineResult.document)) {
                return true;
            }
            access$7900 = this.radialProgressViews[0].backgroundState;
            if (access$7900 <= 0 || access$7900 > gallery_menu_send) {
                return true;
            }
            x = motionEvent.getX();
            y = motionEvent.getY();
            if (x < ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f || x > ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f || y < ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f || y > ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                return true;
            }
            onActionClick(true);
            checkProgress(0, true);
            return true;
        }
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public void openGif(MessageObject messageObject, PhotoSize photoSize, String str) {
        this.isGif = true;
        if (this.drawingItem != null) {
            setDrawingItemVisibility(8);
        }
        this.centerImage.setImage(messageObject.messageOwner.media.document, null, photoSize != null ? photoSize.location : null, str, messageObject.messageOwner.media.document.size, null, false);
        this.centerImage.setAllowStartAnimation(true);
        this.centerImage.startAnimation();
    }

    public boolean openPhoto(MessageObject messageObject, long j, long j2, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(messageObject, null, null, null, 0, photoViewerProvider, null, j, j2);
    }

    public boolean openPhoto(MessageObject messageObject, FileLocation fileLocation, ArrayList<MessageObject> arrayList, ArrayList<Object> arrayList2, int i, PhotoViewerProvider photoViewerProvider, ChatActivity chatActivity, long j, long j2) {
        this.isGif = false;
        if (this.parentActivity == null || this.isVisible || ((photoViewerProvider == null && checkAnimation()) || (messageObject == null && fileLocation == null && arrayList == null && arrayList2 == null))) {
            return false;
        }
        PlaceProviderObject placeForPhoto = photoViewerProvider.getPlaceForPhoto(messageObject, fileLocation, i);
        if (placeForPhoto == null && arrayList2 == null) {
            return false;
        }
        WindowManager windowManager = (WindowManager) this.parentActivity.getSystemService("window");
        if (this.attachedToWindow) {
            try {
                windowManager.removeView(this.windowView);
            } catch (Exception e) {
            }
        }
        try {
            this.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -2147417848;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.windowLayoutParams.softInputMode = 272;
            this.windowView.setFocusable(false);
            this.containerView.setFocusable(false);
            windowManager.addView(this.windowView, this.windowLayoutParams);
            this.parentChatActivity = chatActivity;
            ActionBar actionBar = this.actionBar;
            Object[] objArr = new Object[gallery_menu_showall];
            objArr[0] = Integer.valueOf(gallery_menu_save);
            objArr[gallery_menu_save] = Integer.valueOf(gallery_menu_save);
            actionBar.setTitle(LocaleController.formatString("Of", C0338R.string.Of, objArr));
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileLoadProgressChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaCountDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogPhotosLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            this.placeProvider = photoViewerProvider;
            this.mergeDialogId = j2;
            this.currentDialogId = j;
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.isVisible = true;
            toggleActionBar(true, false);
            if (placeForPhoto != null) {
                this.disableShowCheck = true;
                this.animationInProgress = gallery_menu_save;
                if (!(messageObject == null || isGifToPlay(messageObject))) {
                    this.currentAnimation = placeForPhoto.imageReceiver.getAnimation();
                }
                onPhotoShow(messageObject, fileLocation, arrayList, arrayList2, i, placeForPhoto);
                Rect drawRegion = placeForPhoto.imageReceiver.getDrawRegion();
                int orientation = placeForPhoto.imageReceiver.getOrientation();
                int animatedOrientation = placeForPhoto.imageReceiver.getAnimatedOrientation();
                if (animatedOrientation == 0) {
                    animatedOrientation = orientation;
                }
                this.animatingImageView.setVisibility(0);
                this.animatingImageView.setRadius(placeForPhoto.radius);
                this.animatingImageView.setOrientation(animatedOrientation);
                this.animatingImageView.setNeedRadius(placeForPhoto.radius != 0);
                this.animatingImageView.setImageBitmap(placeForPhoto.thumb);
                this.animatingImageView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.animatingImageView.setPivotX(0.0f);
                this.animatingImageView.setPivotY(0.0f);
                this.animatingImageView.setScaleX(placeForPhoto.scale);
                this.animatingImageView.setScaleY(placeForPhoto.scale);
                this.animatingImageView.setTranslationX(((float) placeForPhoto.viewX) + (((float) drawRegion.left) * placeForPhoto.scale));
                this.animatingImageView.setTranslationY(((float) placeForPhoto.viewY) + (((float) drawRegion.top) * placeForPhoto.scale));
                ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
                layoutParams.width = drawRegion.right - drawRegion.left;
                layoutParams.height = drawRegion.bottom - drawRegion.top;
                this.animatingImageView.setLayoutParams(layoutParams);
                float f = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                float f2 = ((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) / ((float) layoutParams.height);
                if (f <= f2) {
                    f2 = f;
                }
                float f3 = (((float) AndroidUtilities.displaySize.x) - (((float) layoutParams.width) * f2)) / 2.0f;
                float f4 = (((float) ((VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.displaySize.y)) - (((float) layoutParams.height) * f2)) / 2.0f;
                int abs = Math.abs(drawRegion.left - placeForPhoto.imageReceiver.getImageX());
                int abs2 = Math.abs(drawRegion.top - placeForPhoto.imageReceiver.getImageY());
                int[] iArr = new int[gallery_menu_showall];
                placeForPhoto.parentView.getLocationInWindow(iArr);
                orientation = ((iArr[gallery_menu_save] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)) - (placeForPhoto.viewY + drawRegion.top)) + placeForPhoto.clipTopAddition;
                if (orientation < 0) {
                    orientation = 0;
                }
                int height = ((layoutParams.height + (drawRegion.top + placeForPhoto.viewY)) - ((placeForPhoto.parentView.getHeight() + iArr[gallery_menu_save]) - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) + placeForPhoto.clipBottomAddition;
                if (height < 0) {
                    height = 0;
                }
                orientation = Math.max(orientation, abs2);
                height = Math.max(height, abs2);
                this.animationValues[0][0] = this.animatingImageView.getScaleX();
                this.animationValues[0][gallery_menu_save] = this.animatingImageView.getScaleY();
                this.animationValues[0][gallery_menu_showall] = this.animatingImageView.getTranslationX();
                this.animationValues[0][gallery_menu_send] = this.animatingImageView.getTranslationY();
                this.animationValues[0][4] = ((float) abs) * placeForPhoto.scale;
                this.animationValues[0][5] = ((float) orientation) * placeForPhoto.scale;
                this.animationValues[0][gallery_menu_delete] = ((float) height) * placeForPhoto.scale;
                this.animationValues[0][7] = (float) this.animatingImageView.getRadius();
                this.animationValues[gallery_menu_save][0] = f2;
                this.animationValues[gallery_menu_save][gallery_menu_save] = f2;
                this.animationValues[gallery_menu_save][gallery_menu_showall] = f3;
                this.animationValues[gallery_menu_save][gallery_menu_send] = f4;
                this.animationValues[gallery_menu_save][4] = 0.0f;
                this.animationValues[gallery_menu_save][5] = 0.0f;
                this.animationValues[gallery_menu_save][gallery_menu_delete] = 0.0f;
                this.animationValues[gallery_menu_save][7] = 0.0f;
                this.animatingImageView.setAnimationProgress(0.0f);
                this.backgroundDrawable.setAlpha(0);
                this.containerView.setAlpha(0.0f);
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[gallery_menu_send];
                animatorArr[0] = ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                animatorArr[gallery_menu_save] = ObjectAnimator.ofInt(this.backgroundDrawable, "alpha", new int[]{0, NalUnitUtil.EXTENDED_SAR});
                animatorArr[gallery_menu_showall] = ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                animatorSet.playTogether(animatorArr);
                this.animationEndRunnable = new AnonymousClass40(arrayList2);
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapterProxy() {

                    /* renamed from: com.hanista.mobogram.ui.PhotoViewer.41.1 */
                    class C17891 implements Runnable {
                        C17891() {
                        }

                        public void run() {
                            NotificationCenter.getInstance().setAnimationInProgress(false);
                            if (PhotoViewer.this.animationEndRunnable != null) {
                                PhotoViewer.this.animationEndRunnable.run();
                                PhotoViewer.this.animationEndRunnable = null;
                            }
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        AndroidUtilities.runOnUIThread(new C17891());
                    }
                });
                this.transitionAnimationStartTime = System.currentTimeMillis();
                AndroidUtilities.runOnUIThread(new AnonymousClass42(animatorSet));
                if (VERSION.SDK_INT >= 18) {
                    this.containerView.setLayerType(gallery_menu_showall, null);
                }
                this.backgroundDrawable.drawRunnable = new AnonymousClass43(placeForPhoto);
            } else {
                if (!(arrayList2 == null || this.sendPhotoType == gallery_menu_send)) {
                    if (VERSION.SDK_INT >= 21) {
                        this.windowLayoutParams.flags = -2147417856;
                    } else {
                        this.windowLayoutParams.flags = 0;
                    }
                    this.windowLayoutParams.softInputMode = 272;
                    windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
                    this.windowView.setFocusable(true);
                    this.containerView.setFocusable(true);
                }
                this.backgroundDrawable.setAlpha(NalUnitUtil.EXTENDED_SAR);
                this.containerView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                onPhotoShow(messageObject, fileLocation, arrayList, arrayList2, i, placeForPhoto);
            }
            if ((this.currentMessageObject != null && this.currentMessageObject.isVideo()) || this.currentAnimation != null) {
                this.mWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(gallery_menu_delete, "MobogramPlayer");
                this.mWakeLock.acquire();
                if (this.parentActivity != null) {
                    this.parentActivity.getWindow().addFlags(TLRPC.USER_FLAG_UNUSED);
                }
            }
            return true;
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
            return false;
        }
    }

    public boolean openPhoto(FileLocation fileLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, fileLocation, null, null, 0, photoViewerProvider, null, 0, 0);
    }

    public boolean openPhoto(ArrayList<MessageObject> arrayList, int i, long j, long j2, PhotoViewerProvider photoViewerProvider) {
        return openPhoto((MessageObject) arrayList.get(i), null, arrayList, null, i, photoViewerProvider, null, j, j2);
    }

    public boolean openPhotoForSelect(ArrayList<Object> arrayList, int i, int i2, PhotoViewerProvider photoViewerProvider, ChatActivity chatActivity) {
        this.isGif = false;
        this.sendPhotoType = i2;
        if (this.pickerView != null) {
            this.pickerView.doneButton.setText(this.sendPhotoType == gallery_menu_save ? LocaleController.getString("Set", C0338R.string.Set).toUpperCase() : LocaleController.getString("Send", C0338R.string.Send).toUpperCase());
        }
        return openPhoto(null, null, null, arrayList, i, photoViewerProvider, chatActivity, 0, 0);
    }

    public void setAnimationValue(float f) {
        this.animationValue = f;
        this.containerView.invalidate();
    }

    public void setParentActivity(Activity activity) {
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.actvityContext = new ContextThemeWrapper(this.parentActivity, C0338R.style.Theme_TMessages);
            if (progressDrawables == null) {
                progressDrawables = new Drawable[4];
                progressDrawables[0] = this.parentActivity.getResources().getDrawable(C0338R.drawable.circle_big);
                progressDrawables[gallery_menu_save] = this.parentActivity.getResources().getDrawable(C0338R.drawable.cancel_big);
                progressDrawables[gallery_menu_showall] = this.parentActivity.getResources().getDrawable(C0338R.drawable.load_big);
                progressDrawables[gallery_menu_send] = this.parentActivity.getResources().getDrawable(C0338R.drawable.play_big);
            }
            this.scroller = new Scroller(activity);
            this.windowView = new C17802(activity);
            this.windowView.setBackgroundDrawable(this.backgroundDrawable);
            this.windowView.setClipChildren(true);
            this.windowView.setFocusable(false);
            this.animatingImageView = new ClippingImageView(activity);
            this.animatingImageView.setAnimationValues(this.animationValues);
            this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
            this.containerView = new FrameLayoutDrawer(activity);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            if (VERSION.SDK_INT >= 21) {
                this.containerView.setOnApplyWindowInsetsListener(new C17863());
            }
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -2147417848;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.actionBar = new ActionBar(activity);
            this.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.actionBar.setOccupyStatusBar(VERSION.SDK_INT >= 21);
            this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR);
            this.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
            ActionBar actionBar;
            Object[] objArr;
            ActionBarMenu createMenu;
            TextView textView;
            if (ThemeUtil.m2490b()) {
                actionBar = this.actionBar;
                objArr = new Object[gallery_menu_showall];
                objArr[0] = Integer.valueOf(gallery_menu_save);
                objArr[gallery_menu_save] = Integer.valueOf(gallery_menu_save);
                actionBar.setTitle(LocaleController.formatString("Of", C0338R.string.Of, objArr));
                this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
                this.actionBar.setActionBarMenuOnItemClick(new C17914());
                createMenu = this.actionBar.createMenu();
                this.masksItem = createMenu.addItem((int) gallery_menu_masks, (int) C0338R.drawable.ic_masks_msk1);
                this.muteItem = createMenu.addItem((int) gallery_menu_mute, (int) C0338R.drawable.volume_on);
                this.rotateItem = createMenu.addItem((int) gallery_menu_rotate, (int) C0338R.drawable.ic_rotation);
                this.drawingItem = createMenu.addItem((int) gallery_menu_drawing, (int) C0338R.drawable.ic_ab_brush);
                this.menuItem = createMenu.addItem(0, (int) C0338R.drawable.ic_ab_other);
                this.menuItem.addSubItem(gallery_menu_openin, LocaleController.getString("OpenInExternalApp", C0338R.string.OpenInExternalApp), 0);
                this.menuItem.addSubItem(gallery_menu_showall, LocaleController.getString("ShowAllMedia", C0338R.string.ShowAllMedia), 0);
                this.menuItem.addSubItem(gallery_menu_share, LocaleController.getString("ShareFile", C0338R.string.ShareFile), 0);
                this.menuItem.addSubItem(gallery_menu_save, LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery), 0);
                this.menuItem.addSubItem(gallery_menu_delete, LocaleController.getString("Delete", C0338R.string.Delete), 0);
                this.menuItem.addSubItem(gallery_menu_proforward, LocaleController.getString("ProForward", C0338R.string.ProForward), 0);
                this.menuItem.addSubItem(gallery_menu_goto_message, LocaleController.getString("GoToMessage", C0338R.string.GoToMessage), 0);
                this.captionDoneItem = createMenu.addItemWithWidth((int) gallery_menu_caption_done, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
                initThemeItems();
                this.bottomLayout = new FrameLayout(this.actvityContext);
                this.bottomLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
                this.captionTextViewOld = new TextView(this.actvityContext);
                this.captionTextViewOld.setTypeface(FontUtil.m1176a().m1161d());
                this.captionTextViewOld.setMaxLines(gallery_menu_share);
                this.captionTextViewOld.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                this.captionTextViewOld.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
                this.captionTextViewOld.setLinkTextColor(-1);
                this.captionTextViewOld.setTextColor(-1);
                this.captionTextViewOld.setGravity(19);
                this.captionTextViewOld.setTextSize(gallery_menu_save, 16.0f);
                this.captionTextViewOld.setVisibility(4);
                this.containerView.addView(this.captionTextViewOld, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
                this.captionTextViewOld.setOnClickListener(new C17925());
                textView = new TextView(this.actvityContext);
                this.captionTextViewNew = textView;
                this.captionTextView = textView;
                this.captionTextViewNew.setTypeface(FontUtil.m1176a().m1161d());
                this.captionTextViewNew.setMaxLines(gallery_menu_share);
                this.captionTextViewNew.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                this.captionTextViewNew.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
                this.captionTextViewNew.setLinkTextColor(-1);
                this.captionTextViewNew.setTextColor(-1);
                this.captionTextViewNew.setGravity(19);
                this.captionTextViewNew.setTextSize(gallery_menu_save, 16.0f);
                this.captionTextViewNew.setVisibility(4);
                this.containerView.addView(this.captionTextViewNew, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
                this.captionTextViewNew.setOnClickListener(new C17936());
                this.radialProgressViews[0] = new RadialProgressView(this.containerView.getContext(), this.containerView);
                this.radialProgressViews[0].setBackgroundState(0, false);
                this.radialProgressViews[gallery_menu_save] = new RadialProgressView(this.containerView.getContext(), this.containerView);
                this.radialProgressViews[gallery_menu_save].setBackgroundState(0, false);
                this.radialProgressViews[gallery_menu_showall] = new RadialProgressView(this.containerView.getContext(), this.containerView);
                this.radialProgressViews[gallery_menu_showall].setBackgroundState(0, false);
                this.shareButton = new ImageView(this.containerView.getContext());
                this.shareButton.setImageResource(C0338R.drawable.share);
                this.shareButton.setScaleType(ScaleType.CENTER);
                this.shareButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
                this.bottomLayout.addView(this.shareButton, LayoutHelper.createFrame(50, -1, 53));
                this.shareButton.setOnClickListener(new C17947());
                this.nameTextView = new TextView(this.containerView.getContext());
                this.nameTextView.setTextSize(gallery_menu_save, 14.0f);
                this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
                this.nameTextView.setSingleLine(true);
                this.nameTextView.setMaxLines(gallery_menu_save);
                this.nameTextView.setEllipsize(TruncateAt.END);
                this.nameTextView.setTextColor(-1);
                this.nameTextView.setGravity(gallery_menu_send);
                this.bottomLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 5.0f, BitmapDescriptorFactory.HUE_YELLOW, 0.0f));
                this.dateTextView = new TextView(this.containerView.getContext());
                this.dateTextView.setTextSize(gallery_menu_save, 13.0f);
                this.dateTextView.setSingleLine(true);
                this.dateTextView.setMaxLines(gallery_menu_save);
                this.dateTextView.setEllipsize(TruncateAt.END);
                this.dateTextView.setTextColor(-1);
                this.dateTextView.setTypeface(FontUtil.m1176a().m1160c());
                this.dateTextView.setGravity(gallery_menu_send);
                this.bottomLayout.addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 25.0f, 50.0f, 0.0f));
            } else {
                actionBar = this.actionBar;
                objArr = new Object[gallery_menu_showall];
                objArr[0] = Integer.valueOf(gallery_menu_save);
                objArr[gallery_menu_save] = Integer.valueOf(gallery_menu_save);
                actionBar.setTitle(LocaleController.formatString("Of", C0338R.string.Of, objArr));
                this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
                this.actionBar.setActionBarMenuOnItemClick(new C17914());
                createMenu = this.actionBar.createMenu();
                this.masksItem = createMenu.addItem((int) gallery_menu_masks, (int) C0338R.drawable.ic_masks_msk1);
                this.muteItem = createMenu.addItem((int) gallery_menu_mute, (int) C0338R.drawable.volume_on);
                this.rotateItem = createMenu.addItem((int) gallery_menu_rotate, (int) C0338R.drawable.ic_rotation);
                this.drawingItem = createMenu.addItem((int) gallery_menu_drawing, (int) C0338R.drawable.ic_ab_brush);
                this.menuItem = createMenu.addItem(0, (int) C0338R.drawable.ic_ab_other);
                this.menuItem.addSubItem(gallery_menu_openin, LocaleController.getString("OpenInExternalApp", C0338R.string.OpenInExternalApp), 0);
                this.menuItem.addSubItem(gallery_menu_showall, LocaleController.getString("ShowAllMedia", C0338R.string.ShowAllMedia), 0);
                this.menuItem.addSubItem(gallery_menu_share, LocaleController.getString("ShareFile", C0338R.string.ShareFile), 0);
                this.menuItem.addSubItem(gallery_menu_save, LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery), 0);
                this.menuItem.addSubItem(gallery_menu_delete, LocaleController.getString("Delete", C0338R.string.Delete), 0);
                this.menuItem.addSubItem(gallery_menu_proforward, LocaleController.getString("ProForward", C0338R.string.ProForward), 0);
                this.menuItem.addSubItem(gallery_menu_goto_message, LocaleController.getString("GoToMessage", C0338R.string.GoToMessage), 0);
                this.captionDoneItem = createMenu.addItemWithWidth((int) gallery_menu_caption_done, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
                initThemeItems();
                this.bottomLayout = new FrameLayout(this.actvityContext);
                this.bottomLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
                this.captionTextViewOld = new TextView(this.actvityContext);
                this.captionTextViewOld.setTypeface(FontUtil.m1176a().m1161d());
                this.captionTextViewOld.setMaxLines(gallery_menu_share);
                this.captionTextViewOld.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                this.captionTextViewOld.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
                this.captionTextViewOld.setLinkTextColor(-1);
                this.captionTextViewOld.setTextColor(-1);
                this.captionTextViewOld.setGravity(19);
                this.captionTextViewOld.setTextSize(gallery_menu_save, 16.0f);
                this.captionTextViewOld.setVisibility(4);
                this.containerView.addView(this.captionTextViewOld, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
                this.captionTextViewOld.setOnClickListener(new C17925());
                textView = new TextView(this.actvityContext);
                this.captionTextViewNew = textView;
                this.captionTextView = textView;
                this.captionTextViewNew.setTypeface(FontUtil.m1176a().m1161d());
                this.captionTextViewNew.setMaxLines(gallery_menu_share);
                this.captionTextViewNew.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                this.captionTextViewNew.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
                this.captionTextViewNew.setLinkTextColor(-1);
                this.captionTextViewNew.setTextColor(-1);
                this.captionTextViewNew.setGravity(19);
                this.captionTextViewNew.setTextSize(gallery_menu_save, 16.0f);
                this.captionTextViewNew.setVisibility(4);
                this.containerView.addView(this.captionTextViewNew, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
                this.captionTextViewNew.setOnClickListener(new C17936());
                this.radialProgressViews[0] = new RadialProgressView(this.containerView.getContext(), this.containerView);
                this.radialProgressViews[0].setBackgroundState(0, false);
                this.radialProgressViews[gallery_menu_save] = new RadialProgressView(this.containerView.getContext(), this.containerView);
                this.radialProgressViews[gallery_menu_save].setBackgroundState(0, false);
                this.radialProgressViews[gallery_menu_showall] = new RadialProgressView(this.containerView.getContext(), this.containerView);
                this.radialProgressViews[gallery_menu_showall].setBackgroundState(0, false);
                this.shareButton = new ImageView(this.containerView.getContext());
                this.shareButton.setImageResource(C0338R.drawable.share);
                this.shareButton.setScaleType(ScaleType.CENTER);
                this.shareButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
                this.bottomLayout.addView(this.shareButton, LayoutHelper.createFrame(50, -1, 53));
                this.shareButton.setOnClickListener(new C17947());
                this.nameTextView = new TextView(this.containerView.getContext());
                this.nameTextView.setTextSize(gallery_menu_save, 14.0f);
                this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
                this.nameTextView.setSingleLine(true);
                this.nameTextView.setMaxLines(gallery_menu_save);
                this.nameTextView.setEllipsize(TruncateAt.END);
                this.nameTextView.setTextColor(-1);
                this.nameTextView.setGravity(gallery_menu_send);
                this.bottomLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 5.0f, BitmapDescriptorFactory.HUE_YELLOW, 0.0f));
                this.dateTextView = new TextView(this.containerView.getContext());
                this.dateTextView.setTextSize(gallery_menu_save, 13.0f);
                this.dateTextView.setSingleLine(true);
                this.dateTextView.setMaxLines(gallery_menu_save);
                this.dateTextView.setEllipsize(TruncateAt.END);
                this.dateTextView.setTextColor(-1);
                this.dateTextView.setTypeface(FontUtil.m1176a().m1160c());
                this.dateTextView.setGravity(gallery_menu_send);
                this.bottomLayout.addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 25.0f, 50.0f, 0.0f));
            }
            if (VERSION.SDK_INT >= 16) {
                this.videoPlayerSeekbar = new SeekBar(this.containerView.getContext());
                this.videoPlayerSeekbar.setColors(1728053247, -1, -1);
                this.videoPlayerSeekbar.setDelegate(new C17958());
                this.videoPlayerControlFrameLayout = new C17969(this.containerView.getContext());
                this.videoPlayerControlFrameLayout.setWillNotDraw(false);
                this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
                this.videoPlayButton = new ImageView(this.containerView.getContext());
                this.videoPlayButton.setScaleType(ScaleType.CENTER);
                this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48, 51));
                this.videoPlayButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (PhotoViewer.this.videoPlayer == null) {
                            return;
                        }
                        if (PhotoViewer.this.isPlaying) {
                            PhotoViewer.this.videoPlayer.getPlayerControl().pause();
                        } else {
                            PhotoViewer.this.videoPlayer.getPlayerControl().start();
                        }
                    }
                });
                this.videoPlayerTime = new TextView(this.containerView.getContext());
                this.videoPlayerTime.setTextColor(-1);
                this.videoPlayerTime.setGravity(16);
                this.videoPlayerTime.setTextSize(gallery_menu_save, 13.0f);
                this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, Face.UNCOMPUTED_PROBABILITY, 53, 0.0f, 0.0f, 8.0f, 0.0f));
            }
            this.pickerView = new PickerBottomLayoutViewer(this.actvityContext);
            this.pickerView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.containerView.addView(this.pickerView, LayoutHelper.createFrame(-1, 48, 83));
            this.pickerView.cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.placeProvider instanceof EmptyPhotoViewerProvider) {
                        PhotoViewer.this.closePhoto(false, false);
                    } else if (PhotoViewer.this.placeProvider != null) {
                        PhotoViewer.this.closePhoto(!PhotoViewer.this.placeProvider.cancelButtonPressed(), false);
                    }
                }
            });
            this.pickerView.doneButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.placeProvider != null) {
                        PhotoViewer.this.placeProvider.sendButtonPressed(PhotoViewer.this.currentIndex);
                        PhotoViewer.this.closePhoto(false, false);
                    }
                }
            });
            View linearLayout = new LinearLayout(this.parentActivity);
            linearLayout.setOrientation(0);
            this.pickerView.addView(linearLayout, LayoutHelper.createFrame(-2, 48, 49));
            this.tuneItem = new ImageView(this.parentActivity);
            this.tuneItem.setScaleType(ScaleType.CENTER);
            this.tuneItem.setImageResource(C0338R.drawable.photo_tools);
            this.tuneItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            linearLayout.addView(this.tuneItem, LayoutHelper.createLinear(56, 48));
            this.tuneItem.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    PhotoViewer.this.switchToEditMode(PhotoViewer.gallery_menu_showall);
                }
            });
            this.paintItem = new ImageView(this.parentActivity);
            this.paintItem.setScaleType(ScaleType.CENTER);
            this.paintItem.setImageResource(C0338R.drawable.photo_paint);
            this.paintItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            linearLayout.addView(this.paintItem, LayoutHelper.createLinear(56, 48));
            this.paintItem.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    PhotoViewer.this.switchToEditMode(PhotoViewer.gallery_menu_send);
                }
            });
            this.cropItem = new ImageView(this.parentActivity);
            this.cropItem.setScaleType(ScaleType.CENTER);
            this.cropItem.setImageResource(C0338R.drawable.photo_crop);
            this.cropItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            linearLayout.addView(this.cropItem, LayoutHelper.createLinear(56, 48));
            this.cropItem.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    PhotoViewer.this.switchToEditMode(PhotoViewer.gallery_menu_save);
                }
            });
            this.editorDoneLayout = new PickerBottomLayoutViewer(this.actvityContext);
            this.editorDoneLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.editorDoneLayout.updateSelectedCount(0, false);
            this.editorDoneLayout.setVisibility(8);
            this.containerView.addView(this.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.editorDoneLayout.cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.currentEditMode == PhotoViewer.gallery_menu_save) {
                        PhotoViewer.this.photoCropView.cancelAnimationRunnable();
                    }
                    PhotoViewer.this.switchToEditMode(0);
                }
            });
            this.editorDoneLayout.doneButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.currentEditMode == PhotoViewer.gallery_menu_save) {
                        PhotoViewer.this.photoCropView.cancelAnimationRunnable();
                        if (PhotoViewer.this.imageMoveAnimation != null) {
                            return;
                        }
                    }
                    PhotoViewer.this.applyCurrentEditMode();
                    PhotoViewer.this.switchToEditMode(0);
                }
            });
            linearLayout = new ImageView(this.actvityContext);
            linearLayout.setScaleType(ScaleType.CENTER);
            linearLayout.setImageResource(C0338R.drawable.tool_rotate);
            linearLayout.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
            this.editorDoneLayout.addView(linearLayout, LayoutHelper.createFrame(48, 48, 17));
            linearLayout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    PhotoViewer.this.centerImage.setOrientation(PhotoViewer.this.centerImage.getOrientation() - 90, false);
                    PhotoViewer.this.photoCropView.setOrientation(PhotoViewer.this.centerImage.getOrientation());
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            this.gestureDetector = new GestureDetector(this.containerView.getContext(), this);
            this.gestureDetector.setOnDoubleTapListener(this);
            ImageReceiverDelegate anonymousClass19 = new ImageReceiverDelegate() {
                public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
                    if (imageReceiver != PhotoViewer.this.centerImage || !z || PhotoViewer.this.placeProvider == null || !PhotoViewer.this.placeProvider.scaleToFill()) {
                        return;
                    }
                    if (PhotoViewer.this.wasLayout) {
                        PhotoViewer.this.setScaleToFill();
                    } else {
                        PhotoViewer.this.dontResetZoomOnFirstLayout = true;
                    }
                }
            };
            this.centerImage.setParentView(this.containerView);
            this.centerImage.setCrossfadeAlpha((byte) 2);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setDelegate(anonymousClass19);
            this.leftImage.setParentView(this.containerView);
            this.leftImage.setCrossfadeAlpha((byte) 2);
            this.leftImage.setInvalidateAll(true);
            this.leftImage.setDelegate(anonymousClass19);
            this.rightImage.setParentView(this.containerView);
            this.rightImage.setCrossfadeAlpha((byte) 2);
            this.rightImage.setInvalidateAll(true);
            this.rightImage.setDelegate(anonymousClass19);
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.checkImageView = new CheckBox(this.containerView.getContext(), C0338R.drawable.selectphoto_large);
            this.checkImageView.setDrawBackground(true);
            this.checkImageView.setSize(45);
            this.checkImageView.setCheckOffset(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.checkImageView.setColor(-12793105);
            this.checkImageView.setVisibility(8);
            FrameLayoutDrawer frameLayoutDrawer = this.containerView;
            View view = this.checkImageView;
            float f = (rotation == gallery_menu_send || rotation == gallery_menu_save) ? 58.0f : 68.0f;
            frameLayoutDrawer.addView(view, LayoutHelper.createFrame(45, 45.0f, 53, 0.0f, f, 10.0f, 0.0f));
            if (VERSION.SDK_INT >= 21) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.checkImageView.getLayoutParams();
                layoutParams.topMargin += AndroidUtilities.statusBarHeight;
            }
            this.checkImageView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoViewer.this.placeProvider != null) {
                        PhotoViewer.this.placeProvider.setPhotoChecked(PhotoViewer.this.currentIndex);
                        PhotoViewer.this.checkImageView.setChecked(PhotoViewer.this.placeProvider.isPhotoChecked(PhotoViewer.this.currentIndex), true);
                        PhotoViewer.this.updateSelectedCount();
                    }
                }
            });
            this.captionEditText = new PhotoViewerCaptionEnterView(this.actvityContext, this.containerView, this.windowView);
            this.captionEditText.setDelegate(new PhotoViewerCaptionEnterViewDelegate() {
                public void onCaptionEnter() {
                    PhotoViewer.this.closeCaptionEnter(true);
                }

                public void onTextChanged(CharSequence charSequence) {
                    if (PhotoViewer.this.mentionsAdapter != null && PhotoViewer.this.captionEditText != null && PhotoViewer.this.parentChatActivity != null && charSequence != null) {
                        PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(charSequence.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages);
                    }
                }

                public void onWindowSizeChanged(int i) {
                    if (i - (ActionBar.getCurrentActionBarHeight() * PhotoViewer.gallery_menu_showall) < AndroidUtilities.dp((float) ((PhotoViewer.this.mentionsAdapter.getItemCount() > PhotoViewer.gallery_menu_send ? 18 : 0) + (Math.min(PhotoViewer.gallery_menu_send, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36)))) {
                        PhotoViewer.this.allowMentions = false;
                        if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setVisibility(4);
                            return;
                        }
                        return;
                    }
                    PhotoViewer.this.allowMentions = true;
                    if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == 4) {
                        PhotoViewer.this.mentionListView.setVisibility(0);
                    }
                }
            });
            this.containerView.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, -400.0f));
            this.mentionListView = new RecyclerListView(this.actvityContext);
            this.mentionListView.setTag(Integer.valueOf(5));
            this.mentionLayoutManager = new AnonymousClass22(this.actvityContext);
            this.mentionLayoutManager.setOrientation(gallery_menu_save);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
            this.mentionListView.setVisibility(8);
            this.mentionListView.setClipToPadding(true);
            this.mentionListView.setOverScrollMode(gallery_menu_showall);
            this.containerView.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
            RecyclerListView recyclerListView = this.mentionListView;
            Adapter mentionsAdapter = new MentionsAdapter(this.actvityContext, true, 0, new MentionsAdapterDelegate() {

                /* renamed from: com.hanista.mobogram.ui.PhotoViewer.23.1 */
                class C17761 extends AnimatorListenerAdapterProxy {
                    C17761() {
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animator)) {
                            PhotoViewer.this.mentionListAnimation = null;
                        }
                    }
                }

                /* renamed from: com.hanista.mobogram.ui.PhotoViewer.23.2 */
                class C17772 extends AnimatorListenerAdapterProxy {
                    C17772() {
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animator)) {
                            PhotoViewer.this.mentionListView.setVisibility(8);
                            PhotoViewer.this.mentionListAnimation = null;
                        }
                    }
                }

                public void needChangePanelVisibility(boolean z) {
                    if (z) {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.mentionListView.getLayoutParams();
                        int min = (PhotoViewer.this.mentionsAdapter.getItemCount() > PhotoViewer.gallery_menu_send ? 18 : 0) + (Math.min(PhotoViewer.gallery_menu_send, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36);
                        layoutParams.height = AndroidUtilities.dp((float) min);
                        layoutParams.topMargin = -AndroidUtilities.dp((float) min);
                        PhotoViewer.this.mentionListView.setLayoutParams(layoutParams);
                        if (PhotoViewer.this.mentionListAnimation != null) {
                            PhotoViewer.this.mentionListAnimation.cancel();
                            PhotoViewer.this.mentionListAnimation = null;
                        }
                        if (PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            return;
                        }
                        PhotoViewer.this.mentionLayoutManager.scrollToPositionWithOffset(0, AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS);
                        if (PhotoViewer.this.allowMentions) {
                            PhotoViewer.this.mentionListView.setVisibility(0);
                            PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                            AnimatorSet access$7200 = PhotoViewer.this.mentionListAnimation;
                            Animator[] animatorArr = new Animator[PhotoViewer.gallery_menu_save];
                            animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                            access$7200.playTogether(animatorArr);
                            PhotoViewer.this.mentionListAnimation.addListener(new C17761());
                            PhotoViewer.this.mentionListAnimation.setDuration(200);
                            PhotoViewer.this.mentionListAnimation.start();
                            return;
                        }
                        PhotoViewer.this.mentionListView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        PhotoViewer.this.mentionListView.setVisibility(4);
                        return;
                    }
                    if (PhotoViewer.this.mentionListAnimation != null) {
                        PhotoViewer.this.mentionListAnimation.cancel();
                        PhotoViewer.this.mentionListAnimation = null;
                    }
                    if (PhotoViewer.this.mentionListView.getVisibility() == 8) {
                        return;
                    }
                    if (PhotoViewer.this.allowMentions) {
                        PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                        access$7200 = PhotoViewer.this.mentionListAnimation;
                        animatorArr = new Animator[PhotoViewer.gallery_menu_save];
                        float[] fArr = new float[PhotoViewer.gallery_menu_save];
                        fArr[0] = 0.0f;
                        animatorArr[0] = ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, "alpha", fArr);
                        access$7200.playTogether(animatorArr);
                        PhotoViewer.this.mentionListAnimation.addListener(new C17772());
                        PhotoViewer.this.mentionListAnimation.setDuration(200);
                        PhotoViewer.this.mentionListAnimation.start();
                        return;
                    }
                    PhotoViewer.this.mentionListView.setVisibility(8);
                }

                public void onContextClick(BotInlineResult botInlineResult) {
                }

                public void onContextSearch(boolean z) {
                }
            });
            this.mentionsAdapter = mentionsAdapter;
            recyclerListView.setAdapter(mentionsAdapter);
            this.mentionsAdapter.setAllowNewMentions(false);
            this.mentionListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int i) {
                    Object item = PhotoViewer.this.mentionsAdapter.getItem(i);
                    int resultStartPosition = PhotoViewer.this.mentionsAdapter.getResultStartPosition();
                    int resultLength = PhotoViewer.this.mentionsAdapter.getResultLength();
                    if (item instanceof User) {
                        User user = (User) item;
                        if (user != null) {
                            PhotoViewer.this.captionEditText.replaceWithText(resultStartPosition, resultLength, "@" + user.username + " ");
                        }
                    } else if (item instanceof String) {
                        PhotoViewer.this.captionEditText.replaceWithText(resultStartPosition, resultLength, item + " ");
                    }
                }
            });
            this.mentionListView.setOnItemLongClickListener(new OnItemLongClickListener() {

                /* renamed from: com.hanista.mobogram.ui.PhotoViewer.25.1 */
                class C17781 implements DialogInterface.OnClickListener {
                    C17781() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        PhotoViewer.this.mentionsAdapter.clearRecentHashtags();
                    }
                }

                public boolean onItemClick(View view, int i) {
                    if (!(PhotoViewer.this.mentionsAdapter.getItem(i) instanceof String)) {
                        return false;
                    }
                    Builder builder = new Builder(PhotoViewer.this.parentActivity);
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setMessage(LocaleController.getString("ClearSearch", C0338R.string.ClearSearch));
                    builder.setPositiveButton(LocaleController.getString("ClearButton", C0338R.string.ClearButton).toUpperCase(), new C17781());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    PhotoViewer.this.showAlertDialog(builder);
                    return true;
                }
            });
        }
    }

    public void setParentChatActivity(ChatActivity chatActivity) {
        this.parentChatActivity = chatActivity;
    }

    public void showAlertDialog(Builder builder) {
        if (this.parentActivity != null) {
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            try {
                this.visibleDialog = builder.show();
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss(DialogInterface dialogInterface) {
                        PhotoViewer.this.visibleDialog = null;
                    }
                });
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
        }
    }
}
