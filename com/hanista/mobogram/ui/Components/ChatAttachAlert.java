package com.hanista.mobogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.ExifInterface;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.PhotoEntry;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.camera.CameraController;
import com.hanista.mobogram.messenger.camera.CameraController.VideoTakeCallback;
import com.hanista.mobogram.messenger.camera.CameraView;
import com.hanista.mobogram.messenger.camera.CameraView.CameraViewDelegate;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.query.SearchQuery;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemDecoration;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_topPeer;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.PhotoAttachCameraCell;
import com.hanista.mobogram.ui.Cells.PhotoAttachPhotoCell;
import com.hanista.mobogram.ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.ShutterButton.ShutterButtonDelegate;
import com.hanista.mobogram.ui.Components.ShutterButton.State;
import com.hanista.mobogram.ui.PhotoViewer;
import com.hanista.mobogram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

public class ChatAttachAlert extends BottomSheet implements NotificationCenterDelegate, BottomSheetDelegateInterface, PhotoViewerProvider {
    private ListAdapter adapter;
    private int[] animateCameraValues;
    private LinearLayoutManager attachPhotoLayoutManager;
    private RecyclerListView attachPhotoRecyclerView;
    private ViewGroup attachView;
    private ChatActivity baseFragment;
    private boolean cameraAnimationInProgress;
    private File cameraFile;
    private FrameLayout cameraIcon;
    private boolean cameraInitied;
    private float cameraOpenProgress;
    private boolean cameraOpened;
    private FrameLayout cameraPanel;
    private ArrayList<Object> cameraPhoto;
    private CameraView cameraView;
    private int[] cameraViewLocation;
    private int cameraViewOffsetX;
    private int cameraViewOffsetY;
    private AnimatorSet currentHintAnimation;
    private DecelerateInterpolator decelerateInterpolator;
    private ChatAttachViewDelegate delegate;
    private boolean deviceHasGoodCamera;
    private boolean flashAnimationInProgress;
    private ImageView[] flashModeButton;
    private Runnable hideHintRunnable;
    private boolean hintShowed;
    private TextView hintTextView;
    private boolean ignoreLayout;
    private ArrayList<InnerAnimator> innerAnimators;
    private DecelerateInterpolator interpolator;
    private float lastY;
    private LinearLayoutManager layoutManager;
    private View lineView;
    private RecyclerListView listView;
    private boolean loading;
    private boolean maybeStartDraging;
    private PhotoAttachAdapter photoAttachAdapter;
    private boolean pressed;
    private EmptyTextProgressView progressView;
    private TextView recordTime;
    private boolean revealAnimationInProgress;
    private float revealRadius;
    private int revealX;
    private int revealY;
    private int scrollOffsetY;
    private AttachButton sendPhotosButton;
    private Drawable shadowDrawable;
    private ShutterButton shutterButton;
    private ImageView switchCameraButton;
    private boolean takingPhoto;
    private boolean useRevealAnimation;
    private Runnable videoRecordRunnable;
    private int videoRecordTime;
    private View[] views;
    private ArrayList<Holder> viewsCache;

    public interface ChatAttachViewDelegate {
        void didPressedButton(int i);

        void didSelectBot(User user);

        View getRevealView();
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.10 */
    class AnonymousClass10 extends FrameLayout {
        AnonymousClass10(Context context) {
            super(context);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int measuredWidth;
            int i5;
            int measuredWidth2 = getMeasuredWidth() / 2;
            int measuredHeight = getMeasuredHeight() / 2;
            ChatAttachAlert.this.shutterButton.layout(measuredWidth2 - (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2), measuredHeight - (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2), (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2) + measuredWidth2, (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2) + measuredHeight);
            if (getMeasuredWidth() == AndroidUtilities.dp(100.0f)) {
                measuredWidth = getMeasuredWidth() / 2;
                measuredWidth2 = ((measuredHeight / 2) + measuredHeight) + AndroidUtilities.dp(17.0f);
                measuredHeight = (measuredHeight / 2) - AndroidUtilities.dp(17.0f);
                i5 = measuredWidth;
            } else {
                measuredWidth = ((measuredWidth2 / 2) + measuredWidth2) + AndroidUtilities.dp(17.0f);
                measuredHeight = (measuredWidth2 / 2) - AndroidUtilities.dp(17.0f);
                measuredWidth2 = getMeasuredHeight() / 2;
                i5 = measuredHeight;
                measuredHeight = measuredWidth2;
            }
            ChatAttachAlert.this.switchCameraButton.layout(measuredWidth - (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2), measuredWidth2 - (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2), measuredWidth + (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2), measuredWidth2 + (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2));
            for (measuredWidth2 = 0; measuredWidth2 < 2; measuredWidth2++) {
                ChatAttachAlert.this.flashModeButton[measuredWidth2].layout(i5 - (ChatAttachAlert.this.flashModeButton[measuredWidth2].getMeasuredWidth() / 2), measuredHeight - (ChatAttachAlert.this.flashModeButton[measuredWidth2].getMeasuredHeight() / 2), (ChatAttachAlert.this.flashModeButton[measuredWidth2].getMeasuredWidth() / 2) + i5, (ChatAttachAlert.this.flashModeButton[measuredWidth2].getMeasuredHeight() / 2) + measuredHeight);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.11 */
    class AnonymousClass11 implements ShutterButtonDelegate {
        final /* synthetic */ ChatActivity val$parentFragment;

        /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.11.1 */
        class C13111 implements Runnable {
            C13111() {
            }

            public void run() {
                if (ChatAttachAlert.this.videoRecordRunnable != null) {
                    ChatAttachAlert.this.videoRecordTime = ChatAttachAlert.this.videoRecordTime + 1;
                    ChatAttachAlert.this.recordTime.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(ChatAttachAlert.this.videoRecordTime / 60), Integer.valueOf(ChatAttachAlert.this.videoRecordTime % 60)}));
                    AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.11.2 */
        class C13142 implements VideoTakeCallback {

            /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.11.2.1 */
            class C13131 extends EmptyPhotoViewerProvider {
                final /* synthetic */ Bitmap val$thumb;

                /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.11.2.1.1 */
                class C13121 implements Runnable {
                    C13121() {
                    }

                    public void run() {
                        if (ChatAttachAlert.this.cameraView != null && !ChatAttachAlert.this.isDismissed() && VERSION.SDK_INT >= 21) {
                            ChatAttachAlert.this.cameraView.setSystemUiVisibility(1028);
                        }
                    }
                }

                C13131(Bitmap bitmap) {
                    this.val$thumb = bitmap;
                }

                @TargetApi(16)
                public boolean cancelButtonPressed() {
                    if (!(!ChatAttachAlert.this.cameraOpened || ChatAttachAlert.this.cameraView == null || ChatAttachAlert.this.cameraFile == null)) {
                        ChatAttachAlert.this.cameraFile.delete();
                        AndroidUtilities.runOnUIThread(new C13121(), 1000);
                        CameraController.getInstance().startPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                        ChatAttachAlert.this.cameraFile = null;
                    }
                    return true;
                }

                public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
                    return this.val$thumb;
                }

                public void sendButtonPressed(int i) {
                    if (ChatAttachAlert.this.cameraFile != null) {
                        AndroidUtilities.addMediaToGallery(ChatAttachAlert.this.cameraFile.getAbsolutePath());
                        ChatAttachAlert.this.baseFragment.sendMedia((PhotoEntry) ChatAttachAlert.this.cameraPhoto.get(0), PhotoViewer.getInstance().isMuteVideo());
                        ChatAttachAlert.this.closeCamera(false);
                        ChatAttachAlert.this.dismiss();
                        ChatAttachAlert.this.cameraFile = null;
                    }
                }
            }

            C13142() {
            }

            public void onFinishVideoRecording(Bitmap bitmap) {
                if (ChatAttachAlert.this.cameraFile != null && ChatAttachAlert.this.baseFragment != null) {
                    PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
                    ChatAttachAlert.this.cameraPhoto = new ArrayList();
                    ChatAttachAlert.this.cameraPhoto.add(new PhotoEntry(0, 0, 0, ChatAttachAlert.this.cameraFile.getAbsolutePath(), 0, true));
                    PhotoViewer.getInstance().openPhotoForSelect(ChatAttachAlert.this.cameraPhoto, 0, 2, new C13131(bitmap), ChatAttachAlert.this.baseFragment);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.11.3 */
        class C13173 implements Runnable {

            /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.11.3.1 */
            class C13161 extends EmptyPhotoViewerProvider {

                /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.11.3.1.1 */
                class C13151 implements Runnable {
                    C13151() {
                    }

                    public void run() {
                        if (ChatAttachAlert.this.cameraView != null && !ChatAttachAlert.this.isDismissed() && VERSION.SDK_INT >= 21) {
                            ChatAttachAlert.this.cameraView.setSystemUiVisibility(1028);
                        }
                    }
                }

                C13161() {
                }

                @TargetApi(16)
                public boolean cancelButtonPressed() {
                    if (!(!ChatAttachAlert.this.cameraOpened || ChatAttachAlert.this.cameraView == null || ChatAttachAlert.this.cameraFile == null)) {
                        ChatAttachAlert.this.cameraFile.delete();
                        AndroidUtilities.runOnUIThread(new C13151(), 1000);
                        CameraController.getInstance().startPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                        ChatAttachAlert.this.cameraFile = null;
                    }
                    return true;
                }

                public boolean scaleToFill() {
                    return true;
                }

                public void sendButtonPressed(int i) {
                    if (ChatAttachAlert.this.cameraFile != null) {
                        AndroidUtilities.addMediaToGallery(ChatAttachAlert.this.cameraFile.getAbsolutePath());
                        ChatAttachAlert.this.baseFragment.sendMedia((PhotoEntry) ChatAttachAlert.this.cameraPhoto.get(0), false);
                        ChatAttachAlert.this.closeCamera(false);
                        ChatAttachAlert.this.dismiss();
                        ChatAttachAlert.this.cameraFile = null;
                    }
                }
            }

            C13173() {
            }

            public void run() {
                ChatAttachAlert.this.takingPhoto = false;
                if (ChatAttachAlert.this.cameraFile != null && ChatAttachAlert.this.baseFragment != null) {
                    int i;
                    PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
                    ChatAttachAlert.this.cameraPhoto = new ArrayList();
                    try {
                        boolean z;
                        switch (new ExifInterface(ChatAttachAlert.this.cameraFile.getAbsolutePath()).getAttributeInt("Orientation", 1)) {
                            case VideoPlayer.STATE_BUFFERING /*3*/:
                                z = true;
                                break;
                            case Method.TRACE /*6*/:
                                z = true;
                                break;
                            case TLRPC.USER_FLAG_USERNAME /*8*/:
                                z = true;
                                break;
                            default:
                                z = false;
                                break;
                        }
                        i = z;
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                        i = 0;
                    }
                    ChatAttachAlert.this.cameraPhoto.add(new PhotoEntry(0, 0, 0, ChatAttachAlert.this.cameraFile.getAbsolutePath(), i, false));
                    PhotoViewer.getInstance().openPhotoForSelect(ChatAttachAlert.this.cameraPhoto, 0, 2, new C13161(), ChatAttachAlert.this.baseFragment);
                }
            }
        }

        AnonymousClass11(ChatActivity chatActivity) {
            this.val$parentFragment = chatActivity;
        }

        public void shutterCancel() {
            ChatAttachAlert.this.cameraFile.delete();
            ChatAttachAlert.this.resetRecordState();
            CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), true);
        }

        public void shutterLongPressed() {
            if (!ChatAttachAlert.this.takingPhoto && ChatAttachAlert.this.baseFragment != null && ChatAttachAlert.this.baseFragment.getParentActivity() != null) {
                if (VERSION.SDK_INT < 23 || ChatAttachAlert.this.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                    for (int i = 0; i < 2; i++) {
                        ChatAttachAlert.this.flashModeButton[i].setAlpha(0.0f);
                    }
                    ChatAttachAlert.this.switchCameraButton.setAlpha(0.0f);
                    ChatAttachAlert.this.cameraFile = AndroidUtilities.generateVideoPath();
                    ChatAttachAlert.this.recordTime.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    ChatAttachAlert.this.recordTime.setText("00:00");
                    ChatAttachAlert.this.videoRecordTime = 0;
                    ChatAttachAlert.this.videoRecordRunnable = new C13111();
                    AndroidUtilities.lockOrientation(this.val$parentFragment.getParentActivity());
                    CameraController.getInstance().recordVideo(ChatAttachAlert.this.cameraView.getCameraSession(), ChatAttachAlert.this.cameraFile, new C13142());
                    AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000);
                    ChatAttachAlert.this.shutterButton.setState(State.RECORDING, true);
                    return;
                }
                ChatAttachAlert.this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 21);
            }
        }

        public void shutterReleased() {
            if (!ChatAttachAlert.this.takingPhoto) {
                if (ChatAttachAlert.this.shutterButton.getState() == State.RECORDING) {
                    ChatAttachAlert.this.resetRecordState();
                    CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), false);
                    ChatAttachAlert.this.shutterButton.setState(State.DEFAULT, true);
                    return;
                }
                ChatAttachAlert.this.cameraFile = AndroidUtilities.generatePicturePath();
                ChatAttachAlert.this.takingPhoto = CameraController.getInstance().takePicture(ChatAttachAlert.this.cameraFile, ChatAttachAlert.this.cameraView.getCameraSession(), new C13173());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.17 */
    class AnonymousClass17 implements Runnable {
        final /* synthetic */ LayoutParams val$layoutParamsFinal;

        AnonymousClass17(LayoutParams layoutParams) {
            this.val$layoutParamsFinal = layoutParams;
        }

        public void run() {
            if (ChatAttachAlert.this.cameraView != null) {
                ChatAttachAlert.this.cameraView.setLayoutParams(this.val$layoutParamsFinal);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ LayoutParams val$layoutParamsFinal;

        AnonymousClass18(LayoutParams layoutParams) {
            this.val$layoutParamsFinal = layoutParams;
        }

        public void run() {
            if (ChatAttachAlert.this.cameraIcon != null) {
                ChatAttachAlert.this.cameraIcon.setLayoutParams(this.val$layoutParamsFinal);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.1 */
    class C13201 extends RecyclerListView {
        private int lastHeight;
        private int lastWidth;

        C13201(Context context) {
            super(context);
        }

        public void onDraw(Canvas canvas) {
            if (!ChatAttachAlert.this.useRevealAnimation || VERSION.SDK_INT > 19) {
                ChatAttachAlert.this.shadowDrawable.setBounds(0, ChatAttachAlert.this.scrollOffsetY - ChatAttachAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                ChatAttachAlert.this.shadowDrawable.draw(canvas);
                return;
            }
            canvas.save();
            canvas.clipRect(ChatAttachAlert.backgroundPaddingLeft, ChatAttachAlert.this.scrollOffsetY, getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft, getMeasuredHeight());
            if (ChatAttachAlert.this.revealAnimationInProgress) {
                canvas.drawCircle((float) ChatAttachAlert.this.revealX, (float) ChatAttachAlert.this.revealY, ChatAttachAlert.this.revealRadius, ChatAttachAlert.this.ciclePaint);
            } else {
                canvas.drawRect((float) ChatAttachAlert.backgroundPaddingLeft, (float) ChatAttachAlert.this.scrollOffsetY, (float) (getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft), (float) getMeasuredHeight(), ChatAttachAlert.this.ciclePaint);
            }
            canvas.restore();
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (ChatAttachAlert.this.cameraAnimationInProgress) {
                return true;
            }
            if (ChatAttachAlert.this.cameraOpened) {
                return ChatAttachAlert.this.processTouchEvent(motionEvent);
            }
            if (motionEvent.getAction() != 0 || ChatAttachAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) ChatAttachAlert.this.scrollOffsetY)) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            ChatAttachAlert.this.dismiss();
            return true;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int adapterPosition;
            int top;
            int i5 = i3 - i;
            int i6 = i4 - i2;
            if (ChatAttachAlert.this.listView.getChildCount() > 0) {
                View childAt = ChatAttachAlert.this.listView.getChildAt(ChatAttachAlert.this.listView.getChildCount() - 1);
                Holder holder = (Holder) ChatAttachAlert.this.listView.findContainingViewHolder(childAt);
                if (holder != null) {
                    adapterPosition = holder.getAdapterPosition();
                    top = childAt.getTop();
                    if (adapterPosition >= 0 || i6 - this.lastHeight == 0) {
                        top = 0;
                        adapterPosition = -1;
                    } else {
                        top = ((top + i6) - this.lastHeight) - getPaddingTop();
                    }
                    super.onLayout(z, i, i2, i3, i4);
                    if (adapterPosition != -1) {
                        ChatAttachAlert.this.ignoreLayout = true;
                        ChatAttachAlert.this.layoutManager.scrollToPositionWithOffset(adapterPosition, top);
                        super.onLayout(false, i, i2, i3, i4);
                        ChatAttachAlert.this.ignoreLayout = false;
                    }
                    this.lastHeight = i6;
                    this.lastWidth = i5;
                    ChatAttachAlert.this.updateLayout();
                    ChatAttachAlert.this.checkCameraViewPosition();
                }
            }
            top = 0;
            adapterPosition = -1;
            if (adapterPosition >= 0) {
            }
            top = 0;
            adapterPosition = -1;
            super.onLayout(z, i, i2, i3, i4);
            if (adapterPosition != -1) {
                ChatAttachAlert.this.ignoreLayout = true;
                ChatAttachAlert.this.layoutManager.scrollToPositionWithOffset(adapterPosition, top);
                super.onLayout(false, i, i2, i3, i4);
                ChatAttachAlert.this.ignoreLayout = false;
            }
            this.lastHeight = i6;
            this.lastWidth = i5;
            ChatAttachAlert.this.updateLayout();
            ChatAttachAlert.this.checkCameraViewPosition();
        }

        protected void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i2);
            if (VERSION.SDK_INT >= 21) {
                size -= AndroidUtilities.statusBarHeight;
            }
            int dp = (AndroidUtilities.dp(294.0f) + ChatAttachAlert.backgroundPaddingTop) + (SearchQuery.inlineBots.isEmpty() ? 0 : (((int) Math.ceil((double) (((float) SearchQuery.inlineBots.size()) / 4.0f))) * AndroidUtilities.dp(100.0f)) + AndroidUtilities.dp(12.0f));
            int max = dp == AndroidUtilities.dp(294.0f) ? 0 : Math.max(0, size - AndroidUtilities.dp(294.0f));
            if (max != 0 && dp < size) {
                max -= size - dp;
            }
            if (max == 0) {
                max = ChatAttachAlert.backgroundPaddingTop;
            }
            if (getPaddingTop() != max) {
                ChatAttachAlert.this.ignoreLayout = true;
                setPadding(ChatAttachAlert.backgroundPaddingLeft, max, ChatAttachAlert.backgroundPaddingLeft, 0);
                ChatAttachAlert.this.ignoreLayout = false;
            }
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(dp, size), C0700C.ENCODING_PCM_32BIT));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ChatAttachAlert.this.cameraAnimationInProgress ? true : ChatAttachAlert.this.cameraOpened ? ChatAttachAlert.this.processTouchEvent(motionEvent) : !ChatAttachAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
        }

        public void requestLayout() {
            if (!ChatAttachAlert.this.ignoreLayout) {
                super.requestLayout();
            }
        }

        public void setTranslationY(float f) {
            super.setTranslationY(f);
            ChatAttachAlert.this.checkCameraViewPosition();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.21 */
    class AnonymousClass21 extends AnimatorListenerAdapter {
        final /* synthetic */ AnimatorSet val$animatorSet;
        final /* synthetic */ boolean val$open;

        AnonymousClass21(boolean z, AnimatorSet animatorSet) {
            this.val$open = z;
            this.val$animatorSet = animatorSet;
        }

        public void onAnimationCancel(Animator animator) {
            if (ChatAttachAlert.this.currentSheetAnimation != null && this.val$animatorSet.equals(animator)) {
                ChatAttachAlert.this.currentSheetAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (ChatAttachAlert.this.currentSheetAnimation != null && ChatAttachAlert.this.currentSheetAnimation.equals(animator)) {
                ChatAttachAlert.this.currentSheetAnimation = null;
                ChatAttachAlert.this.onRevealAnimationEnd(this.val$open);
                ChatAttachAlert.this.containerView.invalidate();
                ChatAttachAlert.this.containerView.setLayerType(0, null);
                if (!this.val$open) {
                    try {
                        ChatAttachAlert.this.dismissInternal();
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.22 */
    class AnonymousClass22 extends AnimatorListenerAdapter {
        final /* synthetic */ AnimatorSet val$animatorSetInner;

        AnonymousClass22(AnimatorSet animatorSet) {
            this.val$animatorSetInner = animatorSet;
        }

        public void onAnimationEnd(Animator animator) {
            if (this.val$animatorSetInner != null) {
                this.val$animatorSetInner.start();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.2 */
    class C13222 extends ItemDecoration {
        C13222() {
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            rect.left = 0;
            rect.right = 0;
            rect.top = 0;
            rect.bottom = 0;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.3 */
    class C13233 extends OnScrollListener {
        C13233() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (ChatAttachAlert.this.listView.getChildCount() > 0) {
                if (ChatAttachAlert.this.hintShowed && ChatAttachAlert.this.layoutManager.findLastVisibleItemPosition() > 1) {
                    ChatAttachAlert.this.hideHint();
                    ChatAttachAlert.this.hintShowed = false;
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("bothint", true).commit();
                }
                ChatAttachAlert.this.updateLayout();
                ChatAttachAlert.this.checkCameraViewPosition();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.4 */
    class C13244 extends FrameLayout {
        C13244(Context context) {
            super(context);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5 = 0;
            int i6 = i3 - i;
            int i7 = i4 - i2;
            int dp = AndroidUtilities.dp(8.0f);
            ChatAttachAlert.this.attachPhotoRecyclerView.layout(0, dp, i6, ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() + dp);
            ChatAttachAlert.this.progressView.layout(0, dp, i6, ChatAttachAlert.this.progressView.getMeasuredHeight() + dp);
            ChatAttachAlert.this.lineView.layout(0, AndroidUtilities.dp(96.0f), i6, AndroidUtilities.dp(96.0f) + ChatAttachAlert.this.lineView.getMeasuredHeight());
            ChatAttachAlert.this.hintTextView.layout((i6 - ChatAttachAlert.this.hintTextView.getMeasuredWidth()) - AndroidUtilities.dp(5.0f), (i7 - ChatAttachAlert.this.hintTextView.getMeasuredHeight()) - AndroidUtilities.dp(5.0f), i6 - AndroidUtilities.dp(5.0f), i7 - AndroidUtilities.dp(5.0f));
            i6 = (i6 - AndroidUtilities.dp(360.0f)) / 3;
            while (i5 < 8) {
                i7 = AndroidUtilities.dp((float) (((i5 / 4) * 95) + 105));
                dp = AndroidUtilities.dp(10.0f) + ((i5 % 4) * (AndroidUtilities.dp(85.0f) + i6));
                ChatAttachAlert.this.views[i5].layout(dp, i7, ChatAttachAlert.this.views[i5].getMeasuredWidth() + dp, ChatAttachAlert.this.views[i5].getMeasuredHeight() + i7);
                i5++;
            }
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(294.0f), C0700C.ENCODING_PCM_32BIT));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.5 */
    class C13255 extends LinearLayoutManager {
        C13255(Context context) {
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.6 */
    class C13266 implements OnItemClickListener {
        C13266() {
        }

        public void onItemClick(View view, int i) {
            if (ChatAttachAlert.this.baseFragment != null && ChatAttachAlert.this.baseFragment.getParentActivity() != null) {
                if (ChatAttachAlert.this.deviceHasGoodCamera && i == 0) {
                    ChatAttachAlert.this.openCamera();
                    return;
                }
                int i2 = ChatAttachAlert.this.deviceHasGoodCamera ? i - 1 : i;
                if (MediaController.f28e != null) {
                    ArrayList arrayList = MediaController.f28e.photos;
                    if (i2 >= 0 && i2 < arrayList.size()) {
                        PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
                        PhotoViewer.getInstance().openPhotoForSelect(arrayList, i2, 0, ChatAttachAlert.this, ChatAttachAlert.this.baseFragment);
                        AndroidUtilities.hideKeyboard(ChatAttachAlert.this.baseFragment.getFragmentView().findFocus());
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.7 */
    class C13277 extends OnScrollListener {
        C13277() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            ChatAttachAlert.this.checkCameraViewPosition();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.8 */
    class C13288 extends View {
        C13288(Context context) {
            super(context);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.9 */
    class C13299 implements OnClickListener {
        C13299() {
        }

        public void onClick(View view) {
            ChatAttachAlert.this.delegate.didPressedButton(((Integer) view.getTag()).intValue());
        }
    }

    private class AttachBotButton extends FrameLayout {
        private AvatarDrawable avatarDrawable;
        private boolean checkingForLongPress;
        private User currentUser;
        private BackupImageView imageView;
        private TextView nameTextView;
        private CheckForLongPress pendingCheckForLongPress;
        private CheckForTap pendingCheckForTap;
        private int pressCount;
        private boolean pressed;

        /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.AttachBotButton.1 */
        class C13301 implements DialogInterface.OnClickListener {
            C13301() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                SearchQuery.removeInline(AttachBotButton.this.currentUser.id);
            }
        }

        class CheckForLongPress implements Runnable {
            public int currentPressCount;

            CheckForLongPress() {
            }

            public void run() {
                if (AttachBotButton.this.checkingForLongPress && AttachBotButton.this.getParent() != null && this.currentPressCount == AttachBotButton.this.pressCount) {
                    AttachBotButton.this.checkingForLongPress = false;
                    AttachBotButton.this.performHapticFeedback(0);
                    AttachBotButton.this.onLongPress();
                    MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                    AttachBotButton.this.onTouchEvent(obtain);
                    obtain.recycle();
                }
            }
        }

        private final class CheckForTap implements Runnable {
            private CheckForTap() {
            }

            public void run() {
                if (AttachBotButton.this.pendingCheckForLongPress == null) {
                    AttachBotButton.this.pendingCheckForLongPress = new CheckForLongPress();
                }
                AttachBotButton.this.pendingCheckForLongPress.currentPressCount = AttachBotButton.access$104(AttachBotButton.this);
                AttachBotButton.this.postDelayed(AttachBotButton.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
            }
        }

        public AttachBotButton(Context context) {
            super(context);
            this.avatarDrawable = new AvatarDrawable();
            this.checkingForLongPress = false;
            this.pendingCheckForLongPress = null;
            this.pressCount = 0;
            this.pendingCheckForTap = null;
            this.imageView = new BackupImageView(context);
            this.imageView.setRoundRadius(AndroidUtilities.dp(27.0f));
            addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
            this.nameTextView = new TextView(context);
            this.nameTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.nameTextView.setTextSize(1, 12.0f);
            this.nameTextView.setMaxLines(2);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(2);
            this.nameTextView.setEllipsize(TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 65.0f, 6.0f, 0.0f));
            if (ThemeUtil.m2490b()) {
                this.nameTextView.setTextColor(AdvanceTheme.bf);
            }
        }

        static /* synthetic */ int access$104(AttachBotButton attachBotButton) {
            int i = attachBotButton.pressCount + 1;
            attachBotButton.pressCount = i;
            return i;
        }

        private void onLongPress() {
            if (ChatAttachAlert.this.baseFragment != null && this.currentUser != null) {
                Builder builder = new Builder(getContext());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", C0338R.string.ChatHintsDelete, ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C13301());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                builder.show();
            }
        }

        protected void cancelCheckLongPress() {
            this.checkingForLongPress = false;
            if (this.pendingCheckForLongPress != null) {
                removeCallbacks(this.pendingCheckForLongPress);
            }
            if (this.pendingCheckForTap != null) {
                removeCallbacks(this.pendingCheckForTap);
            }
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), C0700C.ENCODING_PCM_32BIT));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean z;
            if (motionEvent.getAction() == 0) {
                this.pressed = true;
                invalidate();
                z = true;
            } else {
                if (this.pressed) {
                    if (motionEvent.getAction() == 1) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        this.pressed = false;
                        playSoundEffect(0);
                        ChatAttachAlert.this.delegate.didSelectBot(MessagesController.getInstance().getUser(Integer.valueOf(((TL_topPeer) SearchQuery.inlineBots.get(((Integer) getTag()).intValue())).peer.user_id)));
                        ChatAttachAlert.this.setUseRevealAnimation(false);
                        ChatAttachAlert.this.dismiss();
                        ChatAttachAlert.this.setUseRevealAnimation(true);
                        invalidate();
                        z = false;
                    } else if (motionEvent.getAction() == 3) {
                        this.pressed = false;
                        invalidate();
                    }
                }
                z = false;
            }
            if (!z) {
                z = super.onTouchEvent(motionEvent);
            } else if (motionEvent.getAction() == 0) {
                startCheckLongPress();
            }
            if (!(motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
                cancelCheckLongPress();
            }
            return z;
        }

        public void setUser(User user) {
            if (user != null) {
                this.currentUser = user;
                TLObject tLObject = null;
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarDrawable.setInfo(user);
                if (!(user == null || user.photo == null)) {
                    tLObject = user.photo.photo_small;
                }
                this.imageView.setImage(tLObject, "50_50", this.avatarDrawable);
                requestLayout();
            }
        }

        protected void startCheckLongPress() {
            if (!this.checkingForLongPress) {
                this.checkingForLongPress = true;
                if (this.pendingCheckForTap == null) {
                    this.pendingCheckForTap = new CheckForTap();
                }
                postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
            }
        }
    }

    private class AttachButton extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public AttachButton(Context context) {
            super(context);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(64, 64, 49));
            this.textView = new TextView(context);
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            this.textView.setTextSize(1, 12.0f);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 64.0f, 0.0f, 0.0f));
            if (ThemeUtil.m2490b()) {
                this.textView.setTextColor(AdvanceTheme.bf);
            }
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f), C0700C.ENCODING_PCM_32BIT));
        }

        public void setTextAndIcon(CharSequence charSequence, Drawable drawable) {
            this.textView.setText(charSequence);
            this.imageView.setBackgroundDrawable(drawable);
        }
    }

    private class Holder extends ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    private class InnerAnimator {
        private AnimatorSet animatorSet;
        private float startRadius;

        private InnerAnimator() {
        }
    }

    private class ListAdapter extends Adapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.ListAdapter.1 */
        class C13311 extends FrameLayout {
            C13311(Context context) {
                super(context);
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int dp = ((i3 - i) - AndroidUtilities.dp(360.0f)) / 3;
                for (int i5 = 0; i5 < 4; i5++) {
                    int dp2 = AndroidUtilities.dp(10.0f) + ((i5 % 4) * (AndroidUtilities.dp(85.0f) + dp));
                    View childAt = getChildAt(i5);
                    childAt.layout(dp2, 0, childAt.getMeasuredWidth() + dp2, childAt.getMeasuredHeight());
                }
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return (!SearchQuery.inlineBots.isEmpty() ? ((int) Math.ceil((double) (((float) SearchQuery.inlineBots.size()) / 4.0f))) + 1 : 0) + 1;
        }

        public int getItemViewType(int i) {
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    return 0;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    return 1;
                default:
                    return 2;
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (i > 1) {
                int i2 = (i - 2) * 4;
                FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
                for (int i3 = 0; i3 < 4; i3++) {
                    AttachBotButton attachBotButton = (AttachBotButton) frameLayout.getChildAt(i3);
                    if (i2 + i3 >= SearchQuery.inlineBots.size()) {
                        attachBotButton.setVisibility(4);
                    } else {
                        attachBotButton.setVisibility(0);
                        attachBotButton.setTag(Integer.valueOf(i2 + i3));
                        attachBotButton.setUser(MessagesController.getInstance().getUser(Integer.valueOf(((TL_topPeer) SearchQuery.inlineBots.get(i2 + i3)).peer.user_id)));
                    }
                }
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View access$6400;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    access$6400 = ChatAttachAlert.this.attachView;
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    access$6400 = new ShadowSectionCell(this.mContext);
                    break;
                default:
                    access$6400 = new C13311(this.mContext);
                    for (int i2 = 0; i2 < 4; i2++) {
                        access$6400.addView(new AttachBotButton(this.mContext));
                    }
                    ChatAttachAlert.this.initThemeBg(access$6400);
                    access$6400.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
                    break;
            }
            return new Holder(access$6400);
        }
    }

    private class PhotoAttachAdapter extends Adapter {
        private Context mContext;
        private HashMap<Integer, PhotoEntry> selectedPhotos;

        /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.PhotoAttachAdapter.1 */
        class C13321 implements PhotoAttachPhotoCellDelegate {
            C13321() {
            }

            public void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell) {
                PhotoEntry photoEntry = photoAttachPhotoCell.getPhotoEntry();
                if (PhotoAttachAdapter.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId))) {
                    PhotoAttachAdapter.this.selectedPhotos.remove(Integer.valueOf(photoEntry.imageId));
                    photoAttachPhotoCell.setChecked(false, true);
                    photoEntry.imagePath = null;
                    photoEntry.thumbPath = null;
                    photoEntry.stickers.clear();
                    photoAttachPhotoCell.setPhotoEntry(photoEntry, ((Integer) photoAttachPhotoCell.getTag()).intValue() == MediaController.f28e.photos.size() + -1);
                } else {
                    PhotoAttachAdapter.this.selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
                    photoAttachPhotoCell.setChecked(true, true);
                }
                ChatAttachAlert.this.updatePhotosButton();
            }
        }

        public PhotoAttachAdapter(Context context) {
            this.selectedPhotos = new HashMap();
            this.mContext = context;
        }

        public void clearSelectedPhotos() {
            if (!this.selectedPhotos.isEmpty()) {
                for (Entry value : this.selectedPhotos.entrySet()) {
                    PhotoEntry photoEntry = (PhotoEntry) value.getValue();
                    photoEntry.imagePath = null;
                    photoEntry.thumbPath = null;
                    photoEntry.caption = null;
                    photoEntry.stickers.clear();
                }
                this.selectedPhotos.clear();
                ChatAttachAlert.this.updatePhotosButton();
                notifyDataSetChanged();
            }
        }

        public Holder createHolder() {
            View photoAttachPhotoCell = new PhotoAttachPhotoCell(this.mContext);
            photoAttachPhotoCell.setDelegate(new C13321());
            return new Holder(photoAttachPhotoCell);
        }

        public int getItemCount() {
            int i = 0;
            if (ChatAttachAlert.this.deviceHasGoodCamera) {
                i = 1;
            }
            return MediaController.f28e != null ? i + MediaController.f28e.photos.size() : i;
        }

        public int getItemViewType(int i) {
            return (ChatAttachAlert.this.deviceHasGoodCamera && i == 0) ? 1 : 0;
        }

        public HashMap<Integer, PhotoEntry> getSelectedPhotos() {
            return this.selectedPhotos;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (!ChatAttachAlert.this.deviceHasGoodCamera || i != 0) {
                if (ChatAttachAlert.this.deviceHasGoodCamera) {
                    i--;
                }
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
                PhotoEntry photoEntry = (PhotoEntry) MediaController.f28e.photos.get(i);
                photoAttachPhotoCell.setPhotoEntry(photoEntry, i == MediaController.f28e.photos.size() + -1);
                photoAttachPhotoCell.setChecked(this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                photoAttachPhotoCell.getImageView().setTag(Integer.valueOf(i));
                photoAttachPhotoCell.setTag(Integer.valueOf(i));
            } else if (!ChatAttachAlert.this.deviceHasGoodCamera || i != 0) {
            } else {
                if (ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isInitied()) {
                    viewHolder.itemView.setVisibility(0);
                } else {
                    viewHolder.itemView.setVisibility(4);
                }
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    return new Holder(new PhotoAttachCameraCell(this.mContext));
                default:
                    if (ChatAttachAlert.this.viewsCache.isEmpty()) {
                        return createHolder();
                    }
                    Holder holder = (Holder) ChatAttachAlert.this.viewsCache.get(0);
                    ChatAttachAlert.this.viewsCache.remove(0);
                    return holder;
            }
        }
    }

    public ChatAttachAlert(Context context, ChatActivity chatActivity) {
        int i;
        super(context, false);
        this.views = new View[20];
        this.viewsCache = new ArrayList(8);
        this.innerAnimators = new ArrayList();
        this.flashModeButton = new ImageView[2];
        this.cameraViewLocation = new int[2];
        this.animateCameraValues = new int[5];
        this.interpolator = new DecelerateInterpolator(1.5f);
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.loading = true;
        this.baseFragment = chatActivity;
        setDelegate(this);
        setUseRevealAnimation(true);
        checkCamera(false);
        if (this.deviceHasGoodCamera) {
            CameraController.getInstance().initCamera();
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.albumsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.cameraInitied);
        this.shadowDrawable = context.getResources().getDrawable(C0338R.drawable.sheet_shadow);
        ViewGroup c13201 = new C13201(context);
        this.listView = c13201;
        this.containerView = c13201;
        this.listView.setWillNotDraw(false);
        this.listView.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.layoutManager.setOrientation(1);
        recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(-657673);
        this.listView.addItemDecoration(new C13222());
        this.listView.setOnScrollListener(new C13233());
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.attachView = new C13244(context);
        View[] viewArr = this.views;
        RecyclerListView recyclerListView2 = new RecyclerListView(context);
        this.attachPhotoRecyclerView = recyclerListView2;
        viewArr[8] = recyclerListView2;
        this.attachPhotoRecyclerView.setVerticalScrollBarEnabled(true);
        recyclerListView = this.attachPhotoRecyclerView;
        listAdapter = new PhotoAttachAdapter(context);
        this.photoAttachAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.attachPhotoRecyclerView.setClipToPadding(false);
        this.attachPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.attachPhotoRecyclerView.setItemAnimator(null);
        this.attachPhotoRecyclerView.setLayoutAnimation(null);
        this.attachPhotoRecyclerView.setOverScrollMode(2);
        this.attachView.addView(this.attachPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
        this.attachPhotoLayoutManager = new C13255(context);
        this.attachPhotoLayoutManager.setOrientation(0);
        this.attachPhotoRecyclerView.setLayoutManager(this.attachPhotoLayoutManager);
        this.attachPhotoRecyclerView.setOnItemClickListener(new C13266());
        this.attachPhotoRecyclerView.setOnScrollListener(new C13277());
        viewArr = this.views;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.progressView = emptyTextProgressView;
        viewArr[9] = emptyTextProgressView;
        if (VERSION.SDK_INT < 23 || getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            this.progressView.setText(LocaleController.getString("NoPhotos", C0338R.string.NoPhotos));
            this.progressView.setTextSize(20);
        } else {
            this.progressView.setText(LocaleController.getString("PermissionStorage", C0338R.string.PermissionStorage));
            this.progressView.setTextSize(16);
        }
        this.attachView.addView(this.progressView, LayoutHelper.createFrame(-1, 80.0f));
        this.attachPhotoRecyclerView.setEmptyView(this.progressView);
        initThemeBg(this.attachView);
        viewArr = this.views;
        View c13288 = new C13288(getContext());
        this.lineView = c13288;
        viewArr[10] = c13288;
        this.lineView.setBackgroundColor(-2960686);
        this.attachView.addView(this.lineView, new LayoutParams(-1, 1, 51));
        CharSequence[] charSequenceArr = new CharSequence[]{LocaleController.getString("ChatCamera", C0338R.string.ChatCamera), LocaleController.getString("ChatGallery", C0338R.string.ChatGallery), LocaleController.getString("ChatVideo", C0338R.string.ChatVideo), LocaleController.getString("AttachMusic", C0338R.string.AttachMusic), LocaleController.getString("ChatDocument", C0338R.string.ChatDocument), LocaleController.getString("AttachContact", C0338R.string.AttachContact), LocaleController.getString("ChatLocation", C0338R.string.ChatLocation), TtmlNode.ANONYMOUS_REGION_ID};
        for (i = 0; i < 8; i++) {
            c13288 = new AttachButton(context);
            c13288.setTextAndIcon(charSequenceArr[i], Theme.attachButtonDrawables[i]);
            this.attachView.addView(c13288, LayoutHelper.createFrame(85, 90, 51));
            c13288.setTag(Integer.valueOf(i));
            this.views[i] = c13288;
            if (i == 7) {
                this.sendPhotosButton = c13288;
                this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            }
            c13288.setOnClickListener(new C13299());
        }
        this.hintTextView = new TextView(context);
        this.hintTextView.setBackgroundResource(C0338R.drawable.tooltip);
        this.hintTextView.setTextColor(-1);
        this.hintTextView.setTextSize(1, 14.0f);
        this.hintTextView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.hintTextView.setText(LocaleController.getString("AttachBotsHelp", C0338R.string.AttachBotsHelp));
        this.hintTextView.setGravity(16);
        this.hintTextView.setVisibility(4);
        this.hintTextView.setCompoundDrawablesWithIntrinsicBounds(C0338R.drawable.scroll_tip, 0, 0, 0);
        this.hintTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.attachView.addView(this.hintTextView, LayoutHelper.createFrame(-2, 32.0f, 85, 5.0f, 0.0f, 5.0f, 5.0f));
        for (i = 0; i < 8; i++) {
            this.viewsCache.add(this.photoAttachAdapter.createHolder());
        }
        if (this.loading) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        if (VERSION.SDK_INT >= 16) {
            this.recordTime = new TextView(context);
            this.recordTime.setBackgroundResource(C0338R.drawable.system);
            this.recordTime.getBackground().setColorFilter(new PorterDuffColorFilter(1711276032, Mode.MULTIPLY));
            this.recordTime.setText("00:00");
            this.recordTime.setTextSize(1, 15.0f);
            this.recordTime.setTypeface(FontUtil.m1176a().m1160c());
            this.recordTime.setAlpha(0.0f);
            this.recordTime.setTextColor(-1);
            this.recordTime.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f));
            this.container.addView(this.recordTime, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 16.0f, 0.0f, 0.0f));
            this.cameraPanel = new AnonymousClass10(context);
            this.cameraPanel.setVisibility(8);
            this.cameraPanel.setAlpha(0.0f);
            this.container.addView(this.cameraPanel, LayoutHelper.createFrame(-1, 100, 83));
            this.shutterButton = new ShutterButton(context);
            this.cameraPanel.addView(this.shutterButton, LayoutHelper.createFrame(84, 84, 17));
            this.shutterButton.setDelegate(new AnonymousClass11(chatActivity));
            this.switchCameraButton = new ImageView(context);
            this.switchCameraButton.setScaleType(ScaleType.CENTER);
            this.cameraPanel.addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48, 21));
            this.switchCameraButton.setOnClickListener(new OnClickListener() {

                /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.12.1 */
                class C13181 extends AnimatorListenerAdapterProxy {
                    C13181() {
                    }

                    public void onAnimationEnd(Animator animator) {
                        ChatAttachAlert.this.switchCameraButton.setImageResource(ChatAttachAlert.this.cameraView.isFrontface() ? C0338R.drawable.camera_revert1 : C0338R.drawable.camera_revert2);
                        ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}).setDuration(100).start();
                    }
                }

                public void onClick(View view) {
                    if (!ChatAttachAlert.this.takingPhoto && ChatAttachAlert.this.cameraView != null && ChatAttachAlert.this.cameraView.isInitied()) {
                        ChatAttachAlert.this.cameraInitied = false;
                        ChatAttachAlert.this.cameraView.switchCamera();
                        ObjectAnimator duration = ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, "scaleX", new float[]{0.0f}).setDuration(100);
                        duration.addListener(new C13181());
                        duration.start();
                    }
                }
            });
            for (i = 0; i < 2; i++) {
                this.flashModeButton[i] = new ImageView(context);
                this.flashModeButton[i].setScaleType(ScaleType.CENTER);
                this.flashModeButton[i].setVisibility(4);
                this.cameraPanel.addView(this.flashModeButton[i], LayoutHelper.createFrame(48, 48, 51));
                this.flashModeButton[i].setOnClickListener(new OnClickListener() {

                    /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.13.1 */
                    class C13191 extends AnimatorListenerAdapterProxy {
                        final /* synthetic */ View val$currentImage;

                        C13191(View view) {
                            this.val$currentImage = view;
                        }

                        public void onAnimationEnd(Animator animator) {
                            ChatAttachAlert.this.flashAnimationInProgress = false;
                            this.val$currentImage.setVisibility(4);
                        }
                    }

                    public void onClick(View view) {
                        if (!ChatAttachAlert.this.flashAnimationInProgress && ChatAttachAlert.this.cameraView != null && ChatAttachAlert.this.cameraView.isInitied() && ChatAttachAlert.this.cameraOpened) {
                            String currentFlashMode = ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode();
                            String nextFlashMode = ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode();
                            if (!currentFlashMode.equals(nextFlashMode)) {
                                ChatAttachAlert.this.cameraView.getCameraSession().setCurrentFlashMode(nextFlashMode);
                                ChatAttachAlert.this.flashAnimationInProgress = true;
                                ImageView imageView = ChatAttachAlert.this.flashModeButton[0] == view ? ChatAttachAlert.this.flashModeButton[1] : ChatAttachAlert.this.flashModeButton[0];
                                imageView.setVisibility(0);
                                ChatAttachAlert.this.setCameraFlashModeIcon(imageView, nextFlashMode);
                                AnimatorSet animatorSet = new AnimatorSet();
                                r2 = new Animator[4];
                                r2[0] = ObjectAnimator.ofFloat(view, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(48.0f)});
                                r2[1] = ObjectAnimator.ofFloat(imageView, "translationY", new float[]{(float) (-AndroidUtilities.dp(48.0f)), 0.0f});
                                r2[2] = ObjectAnimator.ofFloat(view, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f});
                                r2[3] = ObjectAnimator.ofFloat(imageView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                                animatorSet.playTogether(r2);
                                animatorSet.setDuration(200);
                                animatorSet.addListener(new C13191(view));
                                animatorSet.start();
                            }
                        }
                    }
                });
            }
        }
    }

    private void applyCameraViewPosition() {
        if (this.cameraView != null) {
            LayoutParams layoutParams;
            if (!this.cameraOpened) {
                this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
            }
            this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
            int dp = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetX;
            int dp2 = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetY;
            if (!this.cameraOpened) {
                this.cameraView.setClipLeft(this.cameraViewOffsetX);
                this.cameraView.setClipTop(this.cameraViewOffsetY);
                layoutParams = (LayoutParams) this.cameraView.getLayoutParams();
                if (!(layoutParams.height == dp2 && layoutParams.width == dp)) {
                    layoutParams.width = dp;
                    layoutParams.height = dp2;
                    this.cameraView.setLayoutParams(layoutParams);
                    AndroidUtilities.runOnUIThread(new AnonymousClass17(layoutParams));
                }
            }
            layoutParams = (LayoutParams) this.cameraIcon.getLayoutParams();
            if (layoutParams.height != dp2 || layoutParams.width != dp) {
                layoutParams.width = dp;
                layoutParams.height = dp2;
                this.cameraIcon.setLayoutParams(layoutParams);
                AndroidUtilities.runOnUIThread(new AnonymousClass18(layoutParams));
            }
        }
    }

    private void checkCameraViewPosition() {
        if (this.deviceHasGoodCamera) {
            int childCount = this.attachPhotoRecyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.attachPhotoRecyclerView.getChildAt(i);
                if (childAt instanceof PhotoAttachCameraCell) {
                    if (VERSION.SDK_INT < 19 || childAt.isAttachedToWindow()) {
                        int[] iArr;
                        childAt.getLocationInWindow(this.cameraViewLocation);
                        float x = this.listView.getX() + ((float) backgroundPaddingLeft);
                        if (((float) this.cameraViewLocation[0]) < x) {
                            this.cameraViewOffsetX = (int) (x - ((float) this.cameraViewLocation[0]));
                            if (this.cameraViewOffsetX >= AndroidUtilities.dp(80.0f)) {
                                this.cameraViewOffsetX = 0;
                                this.cameraViewLocation[0] = AndroidUtilities.dp(-100.0f);
                                this.cameraViewLocation[1] = 0;
                            } else {
                                iArr = this.cameraViewLocation;
                                iArr[0] = iArr[0] + this.cameraViewOffsetX;
                            }
                        } else {
                            this.cameraViewOffsetX = 0;
                        }
                        if (VERSION.SDK_INT < 21 || this.cameraViewLocation[1] >= AndroidUtilities.statusBarHeight) {
                            this.cameraViewOffsetY = 0;
                        } else {
                            this.cameraViewOffsetY = AndroidUtilities.statusBarHeight - this.cameraViewLocation[1];
                            if (this.cameraViewOffsetY >= AndroidUtilities.dp(80.0f)) {
                                this.cameraViewOffsetY = 0;
                                this.cameraViewLocation[0] = AndroidUtilities.dp(-100.0f);
                                this.cameraViewLocation[1] = 0;
                            } else {
                                iArr = this.cameraViewLocation;
                                iArr[1] = iArr[1] + this.cameraViewOffsetY;
                            }
                        }
                        applyCameraViewPosition();
                        return;
                    }
                    this.cameraViewOffsetX = 0;
                    this.cameraViewOffsetY = 0;
                    this.cameraViewLocation[0] = AndroidUtilities.dp(-100.0f);
                    this.cameraViewLocation[1] = 0;
                    applyCameraViewPosition();
                }
            }
            this.cameraViewOffsetX = 0;
            this.cameraViewOffsetY = 0;
            this.cameraViewLocation[0] = AndroidUtilities.dp(-100.0f);
            this.cameraViewLocation[1] = 0;
            applyCameraViewPosition();
        }
    }

    private PhotoAttachPhotoCell getCellForIndex(int i) {
        if (MediaController.f28e == null) {
            return null;
        }
        int childCount = this.attachPhotoRecyclerView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.attachPhotoRecyclerView.getChildAt(i2);
            if (childAt instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                int intValue = ((Integer) photoAttachPhotoCell.getImageView().getTag()).intValue();
                if (intValue >= 0 && intValue < MediaController.f28e.photos.size() && intValue == i) {
                    return photoAttachPhotoCell;
                }
            }
        }
        return null;
    }

    private void hideHint() {
        if (this.hideHintRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.hideHintRunnable);
            this.hideHintRunnable = null;
        }
        if (this.hintTextView != null) {
            this.currentHintAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.currentHintAnimation;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
            this.currentHintAnimation.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationCancel(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animator)) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                    }
                }

                public void onAnimationEnd(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animator)) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                        if (ChatAttachAlert.this.hintTextView != null) {
                            ChatAttachAlert.this.hintTextView.setVisibility(4);
                        }
                    }
                }
            });
            this.currentHintAnimation.setDuration(300);
            this.currentHintAnimation.start();
        }
    }

    private void initThemeBg(View view) {
        if (ThemeUtil.m2490b()) {
            view.setBackgroundColor(AdvanceTheme.bj);
            int i = AdvanceTheme.bk;
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
                int i2 = AdvanceTheme.bl;
                view.setBackgroundDrawable(new GradientDrawable(orientation, new int[]{r1, i2}));
            }
        }
    }

    private void initThemeSendPhotosButton() {
        if (ThemeUtil.m2490b()) {
            ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.attach_send1).setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
            this.sendPhotosButton.imageView.setBackgroundDrawable(ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.attach_send_states));
        }
    }

    private void onRevealAnimationEnd(boolean z) {
        NotificationCenter.getInstance().setAnimationInProgress(false);
        this.revealAnimationInProgress = false;
        if (z && VERSION.SDK_INT <= 19 && MediaController.f28e == null) {
            MediaController.m111e(0);
        }
        if (z) {
            checkCamera(true);
            showHint();
        }
    }

    @TargetApi(16)
    private void openCamera() {
        if (this.cameraView != null) {
            this.animateCameraValues[0] = 0;
            this.animateCameraValues[1] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetX;
            this.animateCameraValues[2] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetY;
            this.cameraAnimationInProgress = true;
            this.cameraPanel.setVisibility(0);
            this.cameraPanel.setTag(null);
            Collection arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
            arrayList.add(ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
            for (int i = 0; i < 2; i++) {
                if (this.flashModeButton[i].getVisibility() == 0) {
                    arrayList.add(ObjectAnimator.ofFloat(this.flashModeButton[i], "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    break;
                }
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(200);
            animatorSet.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlert.this.cameraAnimationInProgress = false;
                }
            });
            animatorSet.start();
            if (VERSION.SDK_INT >= 21) {
                this.cameraView.setSystemUiVisibility(1028);
            }
            this.cameraOpened = true;
        }
    }

    private boolean processTouchEvent(MotionEvent motionEvent) {
        if ((this.pressed || motionEvent.getActionMasked() != 0) && motionEvent.getActionMasked() != 5) {
            if (this.pressed) {
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (motionEvent.getActionMasked() == 2) {
                    float y = motionEvent.getY();
                    float f = y - this.lastY;
                    if (!this.maybeStartDraging) {
                        this.cameraView.setTranslationY(f + this.cameraView.getTranslationY());
                        this.lastY = y;
                        if (this.cameraPanel.getTag() == null) {
                            this.cameraPanel.setTag(Integer.valueOf(1));
                            animatorSet = new AnimatorSet();
                            animatorArr = new Animator[3];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[]{0.0f});
                            animatorArr[2] = ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                            animatorSet.setDuration(200);
                            animatorSet.start();
                        }
                    } else if (Math.abs(f) > AndroidUtilities.getPixelsInCM(0.4f, false)) {
                        this.maybeStartDraging = false;
                    }
                } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
                    this.pressed = false;
                    if (Math.abs(this.cameraView.getTranslationY()) > ((float) this.cameraView.getMeasuredHeight()) / 6.0f) {
                        closeCamera(true);
                    } else {
                        animatorSet = new AnimatorSet();
                        animatorArr = new Animator[4];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.cameraView, "translationY", new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                        animatorArr[2] = ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                        animatorArr[3] = ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                        animatorSet.playTogether(animatorArr);
                        animatorSet.setDuration(250);
                        animatorSet.setInterpolator(this.interpolator);
                        animatorSet.start();
                        this.cameraPanel.setTag(null);
                    }
                }
            }
        } else if (!this.takingPhoto) {
            this.pressed = true;
            this.maybeStartDraging = true;
            this.lastY = motionEvent.getY();
        }
        return true;
    }

    private void resetRecordState() {
        if (this.baseFragment != null) {
            for (int i = 0; i < 2; i++) {
                this.flashModeButton[i].setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            }
            this.switchCameraButton.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.recordTime.setAlpha(0.0f);
            AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
            this.videoRecordRunnable = null;
            AndroidUtilities.unlockOrientation(this.baseFragment.getParentActivity());
        }
    }

    private void setCameraFlashModeIcon(ImageView imageView, String str) {
        Object obj = -1;
        switch (str.hashCode()) {
            case 3551:
                if (str.equals("on")) {
                    obj = 1;
                    break;
                }
                break;
            case 109935:
                if (str.equals("off")) {
                    obj = null;
                    break;
                }
                break;
            case 3005871:
                if (str.equals("auto")) {
                    obj = 2;
                    break;
                }
                break;
        }
        switch (obj) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                imageView.setImageResource(C0338R.drawable.flash_off);
            case VideoPlayer.TYPE_AUDIO /*1*/:
                imageView.setImageResource(C0338R.drawable.flash_on);
            case VideoPlayer.STATE_PREPARING /*2*/:
                imageView.setImageResource(C0338R.drawable.flash_auto);
            default:
        }
    }

    private void setUseRevealAnimation(boolean z) {
        if (!z || (z && VERSION.SDK_INT >= 18 && !AndroidUtilities.isTablet())) {
            this.useRevealAnimation = z;
        }
    }

    private void showHint() {
        if (!SearchQuery.inlineBots.isEmpty() && !ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("bothint", false)) {
            this.hintShowed = true;
            this.hintTextView.setVisibility(0);
            this.currentHintAnimation = new AnimatorSet();
            this.currentHintAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
            this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
            this.currentHintAnimation.addListener(new AnimatorListenerAdapterProxy() {

                /* renamed from: com.hanista.mobogram.ui.Components.ChatAttachAlert.20.1 */
                class C13211 implements Runnable {
                    C13211() {
                    }

                    public void run() {
                        if (ChatAttachAlert.this.hideHintRunnable == this) {
                            ChatAttachAlert.this.hideHintRunnable = null;
                            ChatAttachAlert.this.hideHint();
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animator)) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                    }
                }

                public void onAnimationEnd(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animator)) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                        AndroidUtilities.runOnUIThread(ChatAttachAlert.this.hideHintRunnable = new C13211(), 2000);
                    }
                }
            });
            this.currentHintAnimation.setDuration(300);
            this.currentHintAnimation.start();
        }
    }

    @SuppressLint({"NewApi"})
    private void startRevealAnimation(boolean z) {
        this.containerView.setTranslationY(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        View revealView = this.delegate.getRevealView();
        if (revealView.getVisibility() == 0 && ((ViewGroup) revealView.getParent()).getVisibility() == 0) {
            int[] iArr = new int[2];
            revealView.getLocationInWindow(iArr);
            float measuredHeight = VERSION.SDK_INT <= 19 ? (float) ((AndroidUtilities.displaySize.y - this.containerView.getMeasuredHeight()) - AndroidUtilities.statusBarHeight) : this.containerView.getY();
            this.revealX = iArr[0] + (revealView.getMeasuredWidth() / 2);
            this.revealY = (int) (((float) ((revealView.getMeasuredHeight() / 2) + iArr[1])) - measuredHeight);
            if (VERSION.SDK_INT <= 19) {
                this.revealY -= AndroidUtilities.statusBarHeight;
            }
        } else {
            this.revealX = (AndroidUtilities.displaySize.x / 2) + backgroundPaddingLeft;
            this.revealY = (int) (((float) AndroidUtilities.displaySize.y) - this.containerView.getY());
        }
        r2 = new int[4][];
        r2[1] = new int[]{0, AndroidUtilities.dp(304.0f)};
        r2[2] = new int[]{this.containerView.getMeasuredWidth(), 0};
        r2[3] = new int[]{this.containerView.getMeasuredWidth(), AndroidUtilities.dp(304.0f)};
        int i = (this.revealY - this.scrollOffsetY) + backgroundPaddingTop;
        int i2 = 0;
        int i3 = 0;
        while (i2 < 4) {
            i2++;
            i3 = Math.max(i3, (int) Math.ceil(Math.sqrt((double) (((this.revealX - r2[i2][0]) * (this.revealX - r2[i2][0])) + ((i - r2[i2][1]) * (i - r2[i2][1]))))));
        }
        i2 = this.revealX <= this.containerView.getMeasuredWidth() ? this.revealX : this.containerView.getMeasuredWidth();
        ArrayList arrayList = new ArrayList(3);
        String str = "revealRadius";
        float[] fArr = new float[2];
        fArr[0] = z ? 0.0f : (float) i3;
        fArr[1] = z ? (float) i3 : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(this, str, fArr));
        ColorDrawable colorDrawable = this.backDrawable;
        String str2 = "alpha";
        int[] iArr2 = new int[1];
        iArr2[0] = z ? 51 : 0;
        arrayList.add(ObjectAnimator.ofInt(colorDrawable, str2, iArr2));
        if (VERSION.SDK_INT >= 21) {
            try {
                arrayList.add(ViewAnimationUtils.createCircularReveal(this.containerView, i2, this.revealY, z ? 0.0f : (float) i3, z ? (float) i3 : 0.0f));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            animatorSet.setDuration(320);
        } else if (z) {
            animatorSet.setDuration(250);
            this.containerView.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.containerView.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.containerView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            if (VERSION.SDK_INT <= 19) {
                animatorSet.setStartDelay(20);
            }
        } else {
            animatorSet.setDuration(200);
            this.containerView.setPivotX(this.revealX <= this.containerView.getMeasuredWidth() ? (float) this.revealX : (float) this.containerView.getMeasuredWidth());
            this.containerView.setPivotY((float) this.revealY);
            arrayList.add(ObjectAnimator.ofFloat(this.containerView, "scaleX", new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.containerView, "scaleY", new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f}));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.addListener(new AnonymousClass21(z, animatorSet));
        if (z) {
            this.innerAnimators.clear();
            NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload});
            NotificationCenter.getInstance().setAnimationInProgress(true);
            this.revealAnimationInProgress = true;
            i2 = VERSION.SDK_INT <= 19 ? 11 : 8;
            for (int i4 = 0; i4 < i2; i4++) {
                AnimatorSet animatorSet2;
                if (VERSION.SDK_INT <= 19) {
                    if (i4 < 8) {
                        this.views[i4].setScaleX(0.1f);
                        this.views[i4].setScaleY(0.1f);
                    }
                    this.views[i4].setAlpha(0.0f);
                } else {
                    this.views[i4].setScaleX(0.7f);
                    this.views[i4].setScaleY(0.7f);
                }
                InnerAnimator innerAnimator = new InnerAnimator();
                int left = this.views[i4].getLeft() + (this.views[i4].getMeasuredWidth() / 2);
                i = (this.views[i4].getTop() + this.attachView.getTop()) + (this.views[i4].getMeasuredHeight() / 2);
                float sqrt = (float) Math.sqrt((double) (((this.revealX - left) * (this.revealX - left)) + ((this.revealY - i) * (this.revealY - i))));
                float f = ((float) (this.revealY - i)) / sqrt;
                this.views[i4].setPivotX(((((float) (this.revealX - left)) / sqrt) * ((float) AndroidUtilities.dp(20.0f))) + ((float) (this.views[i4].getMeasuredWidth() / 2)));
                this.views[i4].setPivotY((f * ((float) AndroidUtilities.dp(20.0f))) + ((float) (this.views[i4].getMeasuredHeight() / 2)));
                innerAnimator.startRadius = sqrt - ((float) AndroidUtilities.dp(81.0f));
                this.views[i4].setTag(C0338R.string.AppName, Integer.valueOf(1));
                Collection arrayList2 = new ArrayList();
                if (i4 < 8) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.views[i4], "scaleX", new float[]{0.7f, 1.05f}));
                    arrayList2.add(ObjectAnimator.ofFloat(this.views[i4], "scaleY", new float[]{0.7f, 1.05f}));
                    animatorSet2 = new AnimatorSet();
                    r6 = new Animator[2];
                    r6[0] = ObjectAnimator.ofFloat(this.views[i4], "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                    r6[1] = ObjectAnimator.ofFloat(this.views[i4], "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                    animatorSet2.playTogether(r6);
                    animatorSet2.setDuration(100);
                    animatorSet2.setInterpolator(this.decelerateInterpolator);
                } else {
                    animatorSet2 = null;
                }
                if (VERSION.SDK_INT <= 19) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.views[i4], "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                }
                innerAnimator.animatorSet = new AnimatorSet();
                innerAnimator.animatorSet.playTogether(arrayList2);
                innerAnimator.animatorSet.setDuration(150);
                innerAnimator.animatorSet.setInterpolator(this.decelerateInterpolator);
                innerAnimator.animatorSet.addListener(new AnonymousClass22(animatorSet2));
                this.innerAnimators.add(innerAnimator);
            }
        }
        this.currentSheetAnimation = animatorSet;
        animatorSet.start();
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = this.listView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.listView.invalidate();
            return;
        }
        View childAt = this.listView.getChildAt(0);
        Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
        paddingTop = childAt.getTop();
        int i = (paddingTop < 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : paddingTop;
        if (this.scrollOffsetY != i) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = i;
            recyclerListView2.setTopGlowOffset(i);
            this.listView.invalidate();
        }
    }

    public boolean allowCaption() {
        return true;
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    protected boolean canDismissWithTouchOutside() {
        return !this.cameraOpened;
    }

    public boolean cancelButtonPressed() {
        return false;
    }

    public void checkCamera(boolean z) {
        boolean z2 = true;
        if (this.baseFragment != null) {
            boolean z3 = this.deviceHasGoodCamera;
            if (VERSION.SDK_INT >= 23) {
                if (this.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                    if (z) {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 17);
                    }
                    this.deviceHasGoodCamera = false;
                } else {
                    CameraController.getInstance().initCamera();
                    if (!CameraController.getInstance().isCameraInitied() || MoboConstants.f1330W) {
                        z2 = false;
                    }
                    this.deviceHasGoodCamera = z2;
                }
            } else if (VERSION.SDK_INT >= 16) {
                CameraController.getInstance().initCamera();
                if (!CameraController.getInstance().isCameraInitied() || MoboConstants.f1330W) {
                    z2 = false;
                }
                this.deviceHasGoodCamera = z2;
            }
            if (!(z3 == this.deviceHasGoodCamera || this.photoAttachAdapter == null)) {
                this.photoAttachAdapter.notifyDataSetChanged();
            }
            if (isShowing() && this.deviceHasGoodCamera && this.baseFragment != null && !this.revealAnimationInProgress) {
                showCamera();
            }
        }
    }

    @TargetApi(16)
    public void closeCamera(boolean z) {
        if (!this.takingPhoto && this.cameraView != null) {
            this.animateCameraValues[1] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetX;
            this.animateCameraValues[2] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetY;
            int i;
            if (z) {
                LayoutParams layoutParams = (LayoutParams) this.cameraView.getLayoutParams();
                int[] iArr = this.animateCameraValues;
                int translationY = (int) this.cameraView.getTranslationY();
                layoutParams.topMargin = translationY;
                iArr[0] = translationY;
                this.cameraView.setLayoutParams(layoutParams);
                this.cameraView.setTranslationY(0.0f);
                this.cameraAnimationInProgress = true;
                Collection arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{0.0f}));
                for (i = 0; i < 2; i++) {
                    if (this.flashModeButton[i].getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(this.flashModeButton[i], "alpha", new float[]{0.0f}));
                        break;
                    }
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animator) {
                        ChatAttachAlert.this.cameraAnimationInProgress = false;
                        ChatAttachAlert.this.cameraPanel.setVisibility(8);
                        ChatAttachAlert.this.cameraOpened = false;
                        if (VERSION.SDK_INT >= 21) {
                            ChatAttachAlert.this.cameraView.setSystemUiVisibility(TLRPC.MESSAGE_FLAG_HAS_VIEWS);
                        }
                    }
                });
                animatorSet.start();
                return;
            }
            this.animateCameraValues[0] = 0;
            setCameraOpenProgress(0.0f);
            this.cameraPanel.setAlpha(0.0f);
            this.cameraPanel.setVisibility(8);
            for (i = 0; i < 2; i++) {
                if (this.flashModeButton[i].getVisibility() == 0) {
                    this.flashModeButton[i].setAlpha(0.0f);
                    break;
                }
            }
            this.cameraOpened = false;
            if (VERSION.SDK_INT >= 21) {
                this.cameraView.setSystemUiVisibility(TLRPC.MESSAGE_FLAG_HAS_VIEWS);
            }
        }
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.albumsDidLoaded) {
            if (this.photoAttachAdapter != null) {
                this.loading = false;
                this.progressView.showTextView();
                this.photoAttachAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.reloadInlineHints) {
            if (this.adapter != null) {
                this.adapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.cameraInitied) {
            checkCamera(false);
        }
    }

    public void dismiss() {
        if (!this.cameraAnimationInProgress) {
            if (this.cameraOpened) {
                closeCamera(true);
                return;
            }
            hideCamera(true);
            super.dismiss();
        }
    }

    public void dismissInternal() {
        if (this.containerView != null) {
            this.containerView.setVisibility(4);
        }
        super.dismissInternal();
    }

    public void dismissWithButtonClick(int i) {
        super.dismissWithButtonClick(i);
        boolean z = (i == 0 || i == 2) ? false : true;
        hideCamera(z);
    }

    public float getCameraOpenProgress() {
        return this.cameraOpenProgress;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        PhotoAttachPhotoCell cellForIndex = getCellForIndex(i);
        if (cellForIndex == null) {
            return null;
        }
        int[] iArr = new int[2];
        cellForIndex.getImageView().getLocationInWindow(iArr);
        PlaceProviderObject placeProviderObject = new PlaceProviderObject();
        placeProviderObject.viewX = iArr[0];
        placeProviderObject.viewY = iArr[1];
        placeProviderObject.parentView = this.attachPhotoRecyclerView;
        placeProviderObject.imageReceiver = cellForIndex.getImageView().getImageReceiver();
        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
        placeProviderObject.scale = cellForIndex.getImageView().getScaleX();
        cellForIndex.getCheckBox().setVisibility(8);
        return placeProviderObject;
    }

    protected float getRevealRadius() {
        return this.revealRadius;
    }

    public int getSelectedCount() {
        return this.photoAttachAdapter.getSelectedPhotos().size();
    }

    public HashMap<Integer, PhotoEntry> getSelectedPhotos() {
        return this.photoAttachAdapter.getSelectedPhotos();
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        PhotoAttachPhotoCell cellForIndex = getCellForIndex(i);
        return cellForIndex != null ? cellForIndex.getImageView().getImageReceiver().getBitmap() : null;
    }

    public void hideCamera(boolean z) {
        if (this.deviceHasGoodCamera && this.cameraView != null) {
            this.cameraView.destroy(z);
            this.container.removeView(this.cameraView);
            this.container.removeView(this.cameraIcon);
            this.cameraView = null;
            this.cameraIcon = null;
            int childCount = this.attachPhotoRecyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.attachPhotoRecyclerView.getChildAt(i);
                if (childAt instanceof PhotoAttachCameraCell) {
                    childAt.setVisibility(0);
                    return;
                }
            }
        }
    }

    public void init() {
        if (MediaController.f28e != null) {
            for (int i = 0; i < Math.min(100, MediaController.f28e.photos.size()); i++) {
                PhotoEntry photoEntry = (PhotoEntry) MediaController.f28e.photos.get(i);
                photoEntry.caption = null;
                photoEntry.imagePath = null;
                photoEntry.thumbPath = null;
                photoEntry.stickers.clear();
            }
        }
        if (this.currentHintAnimation != null) {
            this.currentHintAnimation.cancel();
            this.currentHintAnimation = null;
        }
        this.hintTextView.setAlpha(0.0f);
        this.hintTextView.setVisibility(4);
        this.attachPhotoLayoutManager.scrollToPositionWithOffset(0, 1000000);
        this.photoAttachAdapter.clearSelectedPhotos();
        this.layoutManager.scrollToPositionWithOffset(0, 1000000);
        updatePhotosButton();
    }

    public boolean isPhotoChecked(int i) {
        return i >= 0 && i < MediaController.f28e.photos.size() && this.photoAttachAdapter.getSelectedPhotos().containsKey(Integer.valueOf(((PhotoEntry) MediaController.f28e.photos.get(i)).imageId));
    }

    public void loadGalleryPhotos() {
        if (MediaController.f28e == null && VERSION.SDK_INT >= 21) {
            MediaController.m111e(0);
        }
    }

    protected boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return this.cameraOpened && processTouchEvent(motionEvent);
    }

    protected boolean onCustomCloseAnimation() {
        if (!this.useRevealAnimation) {
            return false;
        }
        this.backDrawable.setAlpha(51);
        startRevealAnimation(false);
        return true;
    }

    protected boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        int i5 = 0;
        int i6 = i3 - i;
        int i7 = i4 - i2;
        boolean z = i6 < i7;
        if (view == this.cameraPanel) {
            if (z) {
                this.cameraPanel.layout(0, i4 - AndroidUtilities.dp(100.0f), i6, i4);
                return true;
            }
            this.cameraPanel.layout(i3 - AndroidUtilities.dp(100.0f), 0, i3, i7);
            return true;
        } else if (view != this.flashModeButton[0] && view != this.flashModeButton[1]) {
            return false;
        } else {
            i6 = VERSION.SDK_INT >= 21 ? AndroidUtilities.dp(10.0f) : 0;
            if (VERSION.SDK_INT >= 21) {
                i5 = AndroidUtilities.dp(8.0f);
            }
            if (z) {
                view.layout((i3 - view.getMeasuredWidth()) - i5, i6, i3 - i5, view.getMeasuredHeight() + i6);
                return true;
            }
            view.layout(i5, i6, view.getMeasuredWidth() + i5, view.getMeasuredHeight() + i6);
            return true;
        }
    }

    protected boolean onCustomMeasure(View view, int i, int i2) {
        boolean z = i < i2;
        if (view == this.cameraView) {
            if (this.cameraOpened && !this.cameraAnimationInProgress) {
                this.cameraView.measure(MeasureSpec.makeMeasureSpec(i, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(i2, C0700C.ENCODING_PCM_32BIT));
                return true;
            }
        } else if (view == this.cameraPanel) {
            if (z) {
                this.cameraPanel.measure(MeasureSpec.makeMeasureSpec(i, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), C0700C.ENCODING_PCM_32BIT));
                return true;
            }
            this.cameraPanel.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(i2, C0700C.ENCODING_PCM_32BIT));
            return true;
        }
        return false;
    }

    protected boolean onCustomOpenAnimation() {
        if (!this.useRevealAnimation) {
            return false;
        }
        startRevealAnimation(true);
        return true;
    }

    public void onDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.albumsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.cameraInitied);
        this.baseFragment = null;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (!this.cameraOpened || (i != 24 && i != 25)) {
            return super.onKeyDown(i, keyEvent);
        }
        this.shutterButton.getDelegate().shutterReleased();
        return true;
    }

    public void onOpenAnimationEnd() {
        onRevealAnimationEnd(true);
    }

    public void onOpenAnimationStart() {
    }

    public void onPause() {
        if (this.cameraOpened && this.shutterButton.getState() == State.RECORDING) {
            resetRecordState();
            CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
            this.shutterButton.setState(State.DEFAULT, true);
        }
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
        if (this.photoAttachAdapter.getSelectedPhotos().isEmpty()) {
            if (i >= 0 && i < MediaController.f28e.photos.size()) {
                PhotoEntry photoEntry = (PhotoEntry) MediaController.f28e.photos.get(i);
                this.photoAttachAdapter.getSelectedPhotos().put(Integer.valueOf(photoEntry.imageId), photoEntry);
            } else {
                return;
            }
        }
        this.delegate.didPressedButton(7);
    }

    public void setCameraOpenProgress(float f) {
        if (this.cameraView != null) {
            float width;
            float height;
            this.cameraOpenProgress = f;
            float f2 = (float) this.animateCameraValues[1];
            float f3 = (float) this.animateCameraValues[2];
            if ((AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y ? 1 : 0) != 0) {
                width = (float) this.container.getWidth();
                height = (float) this.container.getHeight();
            } else {
                width = (float) this.container.getWidth();
                height = (float) this.container.getHeight();
            }
            if (f == 0.0f) {
                this.cameraView.setClipLeft(this.cameraViewOffsetX);
                this.cameraView.setClipTop(this.cameraViewOffsetY);
                this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
                this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
            } else if (!(this.cameraView.getTranslationX() == 0.0f && this.cameraView.getTranslationY() == 0.0f)) {
                this.cameraView.setTranslationX(0.0f);
                this.cameraView.setTranslationY(0.0f);
            }
            LayoutParams layoutParams = (LayoutParams) this.cameraView.getLayoutParams();
            layoutParams.width = (int) (((width - f2) * f) + f2);
            layoutParams.height = (int) (((height - f3) * f) + f3);
            if (f != 0.0f) {
                this.cameraView.setClipLeft((int) (((float) this.cameraViewOffsetX) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f)));
                this.cameraView.setClipTop((int) (((float) this.cameraViewOffsetY) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f)));
                layoutParams.leftMargin = (int) (((float) this.cameraViewLocation[0]) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f));
                layoutParams.topMargin = (int) (((float) this.animateCameraValues[0]) + (((float) (this.cameraViewLocation[1] - this.animateCameraValues[0])) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f)));
            } else {
                layoutParams.leftMargin = 0;
                layoutParams.topMargin = 0;
            }
            this.cameraView.setLayoutParams(layoutParams);
            if (f <= 0.5f) {
                this.cameraIcon.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - (f / 0.5f));
            } else {
                this.cameraIcon.setAlpha(0.0f);
            }
        }
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void setPhotoChecked(int i) {
        if (i >= 0 && i < MediaController.f28e.photos.size()) {
            boolean z;
            PhotoEntry photoEntry = (PhotoEntry) MediaController.f28e.photos.get(i);
            if (this.photoAttachAdapter.getSelectedPhotos().containsKey(Integer.valueOf(photoEntry.imageId))) {
                this.photoAttachAdapter.getSelectedPhotos().remove(Integer.valueOf(photoEntry.imageId));
                z = false;
            } else {
                this.photoAttachAdapter.getSelectedPhotos().put(Integer.valueOf(photoEntry.imageId), photoEntry);
                z = true;
            }
            int childCount = this.attachPhotoRecyclerView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.attachPhotoRecyclerView.getChildAt(i2);
                if ((childAt instanceof PhotoAttachPhotoCell) && ((Integer) childAt.getTag()).intValue() == i) {
                    ((PhotoAttachPhotoCell) childAt).setChecked(z, false);
                    break;
                }
            }
            updatePhotosButton();
        }
    }

    @SuppressLint({"NewApi"})
    protected void setRevealRadius(float f) {
        this.revealRadius = f;
        if (VERSION.SDK_INT <= 19) {
            this.listView.invalidate();
        }
        if (!isDismissed()) {
            int i = 0;
            while (i < this.innerAnimators.size()) {
                InnerAnimator innerAnimator = (InnerAnimator) this.innerAnimators.get(i);
                if (innerAnimator.startRadius <= f) {
                    innerAnimator.animatorSet.start();
                    this.innerAnimators.remove(i);
                    i--;
                }
                i++;
            }
        }
    }

    @TargetApi(16)
    public void showCamera() {
        if (this.cameraView == null) {
            this.cameraView = new CameraView(this.baseFragment.getParentActivity());
            this.container.addView(this.cameraView, 1, LayoutHelper.createFrame(80, 80.0f));
            this.cameraView.setDelegate(new CameraViewDelegate() {
                public void onCameraInit() {
                    int i;
                    int i2 = 0;
                    int childCount = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
                    for (i = 0; i < childCount; i++) {
                        View childAt = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(i);
                        if (childAt instanceof PhotoAttachCameraCell) {
                            childAt.setVisibility(4);
                            break;
                        }
                    }
                    if (ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode())) {
                        for (i = 0; i < 2; i++) {
                            ChatAttachAlert.this.flashModeButton[i].setVisibility(4);
                            ChatAttachAlert.this.flashModeButton[i].setAlpha(0.0f);
                            ChatAttachAlert.this.flashModeButton[i].setTranslationY(0.0f);
                        }
                    } else {
                        ChatAttachAlert.this.setCameraFlashModeIcon(ChatAttachAlert.this.flashModeButton[0], ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode());
                        childCount = 0;
                        while (childCount < 2) {
                            ChatAttachAlert.this.flashModeButton[childCount].setVisibility(childCount == 0 ? 0 : 4);
                            ImageView imageView = ChatAttachAlert.this.flashModeButton[childCount];
                            float f = (childCount == 0 && ChatAttachAlert.this.cameraOpened) ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
                            imageView.setAlpha(f);
                            ChatAttachAlert.this.flashModeButton[childCount].setTranslationY(0.0f);
                            childCount++;
                        }
                    }
                    ChatAttachAlert.this.switchCameraButton.setImageResource(ChatAttachAlert.this.cameraView.isFrontface() ? C0338R.drawable.camera_revert1 : C0338R.drawable.camera_revert2);
                    ImageView access$4600 = ChatAttachAlert.this.switchCameraButton;
                    if (!ChatAttachAlert.this.cameraView.hasFrontFaceCamera()) {
                        i2 = 4;
                    }
                    access$4600.setVisibility(i2);
                }
            });
            this.cameraIcon = new FrameLayout(this.baseFragment.getParentActivity());
            this.container.addView(this.cameraIcon, 2, LayoutHelper.createFrame(80, 80.0f));
            View imageView = new ImageView(this.baseFragment.getParentActivity());
            imageView.setScaleType(ScaleType.CENTER);
            imageView.setImageResource(C0338R.drawable.instant_camera);
            this.cameraIcon.addView(imageView, LayoutHelper.createFrame(80, 80, 85));
        }
        this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
        this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
        this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
        this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
    }

    public void updatePhotoAtIndex(int i) {
        PhotoAttachPhotoCell cellForIndex = getCellForIndex(i);
        if (cellForIndex != null) {
            cellForIndex.getImageView().setOrientation(0, true);
            PhotoEntry photoEntry = (PhotoEntry) MediaController.f28e.photos.get(i);
            if (photoEntry.thumbPath != null) {
                cellForIndex.getImageView().setImage(photoEntry.thumbPath, null, cellForIndex.getContext().getResources().getDrawable(C0338R.drawable.nophotos));
            } else if (photoEntry.path != null) {
                cellForIndex.getImageView().setOrientation(photoEntry.orientation, true);
                cellForIndex.getImageView().setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, cellForIndex.getContext().getResources().getDrawable(C0338R.drawable.nophotos));
            } else {
                cellForIndex.getImageView().setImageResource(C0338R.drawable.nophotos);
            }
        }
    }

    public void updatePhotosButton() {
        if (this.photoAttachAdapter.getSelectedPhotos().size() == 0) {
            this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            this.sendPhotosButton.imageView.setBackgroundResource(C0338R.drawable.attach_hide_states);
            this.sendPhotosButton.imageView.setImageResource(C0338R.drawable.attach_hide2);
            this.sendPhotosButton.textView.setText(TtmlNode.ANONYMOUS_REGION_ID);
        } else {
            this.sendPhotosButton.imageView.setPadding(AndroidUtilities.dp(2.0f), 0, 0, 0);
            this.sendPhotosButton.imageView.setBackgroundResource(C0338R.drawable.attach_send_states);
            initThemeSendPhotosButton();
            this.sendPhotosButton.imageView.setImageResource(C0338R.drawable.attach_send2);
            TextView access$6300 = this.sendPhotosButton.textView;
            Object[] objArr = new Object[1];
            objArr[0] = String.format("(%d)", new Object[]{Integer.valueOf(r0)});
            access$6300.setText(LocaleController.formatString("SendItems", C0338R.string.SendItems, objArr));
        }
        if (VERSION.SDK_INT < 23 || getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            this.progressView.setText(LocaleController.getString("NoPhotos", C0338R.string.NoPhotos));
            this.progressView.setTextSize(20);
            return;
        }
        this.progressView.setText(LocaleController.getString("PermissionStorage", C0338R.string.PermissionStorage));
        this.progressView.setTextSize(16);
    }

    public void willHidePhotoViewer() {
        int childCount = this.attachPhotoRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.attachPhotoRecyclerView.getChildAt(i);
            if (childAt instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                if (photoAttachPhotoCell.getCheckBox().getVisibility() != 0) {
                    photoAttachPhotoCell.getCheckBox().setVisibility(0);
                }
            }
        }
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        PhotoAttachPhotoCell cellForIndex = getCellForIndex(i);
        if (cellForIndex != null) {
            cellForIndex.getCheckBox().setVisibility(0);
        }
    }
}
