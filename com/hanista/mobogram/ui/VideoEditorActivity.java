package com.hanista.mobogram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.google.android.gms.vision.face.Face;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.BotInlineResult;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.MentionsAdapter;
import com.hanista.mobogram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView;
import com.hanista.mobogram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate;
import com.hanista.mobogram.ui.Components.PickerBottomLayoutViewer;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.Components.SizeNotifierFrameLayoutPhoto;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.Components.VideoSeekBarView;
import com.hanista.mobogram.ui.Components.VideoSeekBarView.SeekBarDelegate;
import com.hanista.mobogram.ui.Components.VideoTimelineView;
import com.hanista.mobogram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate;
import java.io.File;
import java.util.List;

@TargetApi(16)
public class VideoEditorActivity extends BaseFragment implements NotificationCenterDelegate {
    private boolean allowMentions;
    private long audioFramesSize;
    private int bitrate;
    private ActionBarMenuItem captionDoneItem;
    private PhotoViewerCaptionEnterView captionEditText;
    private ImageView captionItem;
    private ImageView compressItem;
    private boolean created;
    private CharSequence currentCaption;
    private VideoEditorActivityDelegate delegate;
    private long endTime;
    private long esimatedDuration;
    private int estimatedSize;
    private float lastProgress;
    private LinearLayoutManager mentionLayoutManager;
    private AnimatorSet mentionListAnimation;
    private RecyclerListView mentionListView;
    private MentionsAdapter mentionsAdapter;
    private ImageView muteItem;
    private boolean muteVideo;
    private boolean needCompressVideo;
    private boolean needSeek;
    private String oldTitle;
    private int originalBitrate;
    private int originalHeight;
    private long originalSize;
    private int originalWidth;
    private ChatActivity parentChatActivity;
    private PickerBottomLayoutViewer pickerView;
    private ImageView playButton;
    private boolean playerPrepared;
    private Runnable progressRunnable;
    private int resultHeight;
    private int resultWidth;
    private int rotationValue;
    private long startTime;
    private final Object sync;
    private TextureView textureView;
    private Thread thread;
    private float videoDuration;
    private long videoFramesSize;
    private String videoPath;
    private MediaPlayer videoPlayer;
    private VideoSeekBarView videoSeekBarView;
    private VideoTimelineView videoTimelineView;

    public interface VideoEditorActivityDelegate {
        void didFinishEditVideo(String str, long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, long j3, long j4, String str2);
    }

    /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.16 */
    class AnonymousClass16 extends LinearLayoutManager {
        AnonymousClass16(Context context) {
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.1 */
    class C19511 implements Runnable {

        /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.1.1 */
        class C19461 implements Runnable {
            C19461() {
            }

            public void run() {
                if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.videoPlayer.isPlaying()) {
                    float leftProgress = VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration;
                    float rightProgress = VideoEditorActivity.this.videoTimelineView.getRightProgress() * VideoEditorActivity.this.videoDuration;
                    if (leftProgress == rightProgress) {
                        leftProgress = rightProgress - 0.01f;
                    }
                    leftProgress = (((((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) - leftProgress) / (rightProgress - leftProgress)) * (VideoEditorActivity.this.videoTimelineView.getRightProgress() - VideoEditorActivity.this.videoTimelineView.getLeftProgress())) + VideoEditorActivity.this.videoTimelineView.getLeftProgress();
                    if (leftProgress > VideoEditorActivity.this.lastProgress) {
                        VideoEditorActivity.this.videoSeekBarView.setProgress(leftProgress);
                        VideoEditorActivity.this.lastProgress = leftProgress;
                    }
                    if (((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) >= rightProgress) {
                        try {
                            VideoEditorActivity.this.videoPlayer.pause();
                            VideoEditorActivity.this.onPlayComplete();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                }
            }
        }

        C19511() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r4 = this;
            r1 = 0;
        L_0x0001:
            r0 = com.hanista.mobogram.ui.VideoEditorActivity.this;
            r2 = r0.sync;
            monitor-enter(r2);
            r0 = com.hanista.mobogram.ui.VideoEditorActivity.this;	 Catch:{ Exception -> 0x0031 }
            r0 = r0.videoPlayer;	 Catch:{ Exception -> 0x0031 }
            if (r0 == 0) goto L_0x002f;
        L_0x0010:
            r0 = com.hanista.mobogram.ui.VideoEditorActivity.this;	 Catch:{ Exception -> 0x0031 }
            r0 = r0.videoPlayer;	 Catch:{ Exception -> 0x0031 }
            r0 = r0.isPlaying();	 Catch:{ Exception -> 0x0031 }
            if (r0 == 0) goto L_0x002f;
        L_0x001c:
            r0 = 1;
        L_0x001d:
            monitor-exit(r2);	 Catch:{ all -> 0x003a }
            if (r0 != 0) goto L_0x003d;
        L_0x0020:
            r0 = com.hanista.mobogram.ui.VideoEditorActivity.this;
            r1 = r0.sync;
            monitor-enter(r1);
            r0 = com.hanista.mobogram.ui.VideoEditorActivity.this;	 Catch:{ all -> 0x0053 }
            r2 = 0;
            r0.thread = r2;	 Catch:{ all -> 0x0053 }
            monitor-exit(r1);	 Catch:{ all -> 0x0053 }
            return;
        L_0x002f:
            r0 = r1;
            goto L_0x001d;
        L_0x0031:
            r0 = move-exception;
            r3 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r3, r0);	 Catch:{ all -> 0x003a }
            r0 = r1;
            goto L_0x001d;
        L_0x003a:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x003a }
            throw r0;
        L_0x003d:
            r0 = new com.hanista.mobogram.ui.VideoEditorActivity$1$1;
            r0.<init>();
            com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r0);
            r2 = 50;
            java.lang.Thread.sleep(r2);	 Catch:{ Exception -> 0x004b }
            goto L_0x0001;
        L_0x004b:
            r0 = move-exception;
            r2 = "tmessages";
            com.hanista.mobogram.messenger.FileLog.m18e(r2, r0);
            goto L_0x0001;
        L_0x0053:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x0053 }
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.VideoEditorActivity.1.run():void");
        }
    }

    /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.2 */
    class C19532 implements OnCompletionListener {

        /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.2.1 */
        class C19521 implements Runnable {
            C19521() {
            }

            public void run() {
                VideoEditorActivity.this.onPlayComplete();
            }
        }

        C19532() {
        }

        public void onCompletion(MediaPlayer mediaPlayer) {
            AndroidUtilities.runOnUIThread(new C19521());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.3 */
    class C19543 implements OnPreparedListener {
        C19543() {
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
            VideoEditorActivity.this.playerPrepared = true;
            if (VideoEditorActivity.this.videoTimelineView != null && VideoEditorActivity.this.videoPlayer != null) {
                VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.4 */
    class C19554 extends ActionBarMenuOnItemClick {
        C19554() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                if (VideoEditorActivity.this.captionEditText.isPopupShowing() || VideoEditorActivity.this.captionEditText.isKeyboardVisible()) {
                    VideoEditorActivity.this.closeCaptionEnter(false);
                } else {
                    VideoEditorActivity.this.finishFragment();
                }
            } else if (i == 1) {
                VideoEditorActivity.this.closeCaptionEnter(true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.5 */
    class C19565 extends SizeNotifierFrameLayoutPhoto {
        int lastWidth;

        C19565(Context context) {
            super(context);
        }

        private void onLayoutTablet(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            int emojiPadding = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : VideoEditorActivity.this.captionEditText.getEmojiPadding();
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
                            i6 = layoutParams.topMargin;
                            break;
                        case 80:
                            i6 = (((i4 - emojiPadding) - i2) - measuredHeight) - layoutParams.bottomMargin;
                            break;
                        default:
                            i6 = layoutParams.topMargin;
                            break;
                    }
                    if (childAt == VideoEditorActivity.this.mentionListView) {
                        i6 = (VideoEditorActivity.this.captionEditText.isPopupShowing() || VideoEditorActivity.this.captionEditText.isKeyboardVisible() || VideoEditorActivity.this.captionEditText.getEmojiPadding() != 0) ? i6 - VideoEditorActivity.this.captionEditText.getMeasuredHeight() : i6 + AndroidUtilities.dp(400.0f);
                    } else if (childAt == VideoEditorActivity.this.captionEditText) {
                        if (!(VideoEditorActivity.this.captionEditText.isPopupShowing() || VideoEditorActivity.this.captionEditText.isKeyboardVisible() || VideoEditorActivity.this.captionEditText.getEmojiPadding() != 0)) {
                            i6 += AndroidUtilities.dp(400.0f);
                        }
                    } else if (childAt == VideoEditorActivity.this.pickerView) {
                        if (VideoEditorActivity.this.captionEditText.isPopupShowing() || VideoEditorActivity.this.captionEditText.isKeyboardVisible()) {
                            i6 += AndroidUtilities.dp(400.0f);
                        }
                    } else if (VideoEditorActivity.this.captionEditText.isPopupView(childAt)) {
                        i6 = AndroidUtilities.isInMultiwindow ? (VideoEditorActivity.this.captionEditText.getTop() - childAt.getMeasuredHeight()) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) : VideoEditorActivity.this.captionEditText.getBottom();
                    }
                    childAt.layout(i7, i6, measuredWidth + i7, measuredHeight + i6);
                }
            }
            notifyHeightChanged();
        }

        public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
            if (keyEvent == null || keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 1 || (!VideoEditorActivity.this.captionEditText.isPopupShowing() && !VideoEditorActivity.this.captionEditText.isKeyboardVisible())) {
                return super.dispatchKeyEventPreIme(keyEvent);
            }
            VideoEditorActivity.this.closeCaptionEnter(false);
            return false;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (AndroidUtilities.isTablet()) {
                onLayoutTablet(z, i, i2, i3, i4);
                return;
            }
            int childCount = getChildCount();
            int emojiPadding = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : VideoEditorActivity.this.captionEditText.getEmojiPadding();
            int currentActionBarHeight = AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight();
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
                    int i8 = i7 & 7;
                    i7 &= 112;
                    switch (i8 & 7) {
                        case VideoPlayer.TYPE_AUDIO /*1*/:
                            i8 = ((((i3 - i) - measuredWidth) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                            break;
                        case VideoPlayer.STATE_ENDED /*5*/:
                            i8 = (i3 - measuredWidth) - layoutParams.rightMargin;
                            break;
                        default:
                            i8 = layoutParams.leftMargin;
                            break;
                    }
                    switch (i7) {
                        case TLRPC.USER_FLAG_PHONE /*16*/:
                            i7 = (((currentActionBarHeight - measuredHeight) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                            break;
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                            i7 = layoutParams.topMargin;
                            break;
                        case 80:
                            i7 = (currentActionBarHeight - measuredHeight) - layoutParams.bottomMargin;
                            break;
                        default:
                            i7 = layoutParams.topMargin;
                            break;
                    }
                    if (childAt == VideoEditorActivity.this.mentionListView) {
                        i6 = (((i4 - emojiPadding) - i2) - measuredHeight) - layoutParams.bottomMargin;
                        if (VideoEditorActivity.this.captionEditText.isPopupShowing() || VideoEditorActivity.this.captionEditText.isKeyboardVisible() || VideoEditorActivity.this.captionEditText.getEmojiPadding() != 0) {
                            i7 = i6 - VideoEditorActivity.this.captionEditText.getMeasuredHeight();
                            i6 = i8;
                        } else {
                            i7 = AndroidUtilities.dp(400.0f) + i6;
                            i6 = i8;
                        }
                    } else {
                        if (childAt == VideoEditorActivity.this.captionEditText) {
                            i7 = (((i4 - emojiPadding) - i2) - measuredHeight) - layoutParams.bottomMargin;
                            if (!(VideoEditorActivity.this.captionEditText.isPopupShowing() || VideoEditorActivity.this.captionEditText.isKeyboardVisible() || VideoEditorActivity.this.captionEditText.getEmojiPadding() != 0)) {
                                i7 += AndroidUtilities.dp(400.0f);
                                i6 = i8;
                            }
                        } else if (childAt == VideoEditorActivity.this.pickerView) {
                            if (VideoEditorActivity.this.captionEditText.isPopupShowing() || VideoEditorActivity.this.captionEditText.isKeyboardVisible()) {
                                i7 += AndroidUtilities.dp(400.0f);
                                i6 = i8;
                            }
                        } else if (VideoEditorActivity.this.captionEditText.isPopupView(childAt)) {
                            if (AndroidUtilities.isInMultiwindow) {
                                i7 = AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) + (VideoEditorActivity.this.captionEditText.getTop() - childAt.getMeasuredHeight());
                                i6 = i8;
                            } else {
                                i7 = VideoEditorActivity.this.captionEditText.getBottom();
                                i6 = i8;
                            }
                        } else if (childAt == VideoEditorActivity.this.textureView) {
                            i6 = ((i3 - i) - VideoEditorActivity.this.textureView.getMeasuredWidth()) / 2;
                            i7 = AndroidUtilities.dp(14.0f);
                        }
                        i6 = i8;
                    }
                    childAt.layout(i6, i7, i6 + measuredWidth, i7 + measuredHeight);
                }
            }
            notifyHeightChanged();
        }

        protected void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            setMeasuredDimension(size, MeasureSpec.getSize(i2));
            int currentActionBarHeight = AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight();
            measureChildWithMargins(VideoEditorActivity.this.captionEditText, i, 0, i2, 0);
            int measuredHeight = VideoEditorActivity.this.captionEditText.getMeasuredHeight();
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (!(childAt.getVisibility() == 8 || childAt == VideoEditorActivity.this.captionEditText)) {
                    if (VideoEditorActivity.this.captionEditText.isPopupView(childAt)) {
                        if (!AndroidUtilities.isInMultiwindow) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, C0700C.ENCODING_PCM_32BIT));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), (currentActionBarHeight - measuredHeight) - AndroidUtilities.statusBarHeight), C0700C.ENCODING_PCM_32BIT));
                        } else {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((currentActionBarHeight - measuredHeight) - AndroidUtilities.statusBarHeight, C0700C.ENCODING_PCM_32BIT));
                        }
                    } else if (childAt == VideoEditorActivity.this.textureView) {
                        int dp = currentActionBarHeight - AndroidUtilities.dp(166.0f);
                        int access$1300 = (VideoEditorActivity.this.rotationValue == 90 || VideoEditorActivity.this.rotationValue == 270) ? VideoEditorActivity.this.originalHeight : VideoEditorActivity.this.originalWidth;
                        int access$1400 = (VideoEditorActivity.this.rotationValue == 90 || VideoEditorActivity.this.rotationValue == 270) ? VideoEditorActivity.this.originalWidth : VideoEditorActivity.this.originalHeight;
                        float f = ((float) access$1300) / ((float) access$1400);
                        if (((float) size) / ((float) access$1300) > ((float) dp) / ((float) access$1400)) {
                            int i4 = dp;
                            dp = (int) (f * ((float) dp));
                            access$1300 = i4;
                        } else {
                            access$1300 = (int) (((float) size) / f);
                            dp = size;
                        }
                        childAt.measure(MeasureSpec.makeMeasureSpec(dp, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(access$1300, C0700C.ENCODING_PCM_32BIT));
                    } else {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    }
                }
            }
            if (this.lastWidth != size) {
                VideoEditorActivity.this.videoTimelineView.clearFrames();
                this.lastWidth = size;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.6 */
    class C19576 implements OnClickListener {
        C19576() {
        }

        public void onClick(View view) {
            VideoEditorActivity.this.finishFragment();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.7 */
    class C19587 implements OnClickListener {
        C19587() {
        }

        public void onClick(View view) {
            synchronized (VideoEditorActivity.this.sync) {
                if (VideoEditorActivity.this.videoPlayer != null) {
                    try {
                        VideoEditorActivity.this.videoPlayer.stop();
                        VideoEditorActivity.this.videoPlayer.release();
                        VideoEditorActivity.this.videoPlayer = null;
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }
            if (VideoEditorActivity.this.delegate != null) {
                if (VideoEditorActivity.this.compressItem.getVisibility() == 8 || (VideoEditorActivity.this.compressItem.getVisibility() == 0 && !VideoEditorActivity.this.needCompressVideo)) {
                    VideoEditorActivity.this.delegate.didFinishEditVideo(VideoEditorActivity.this.videoPath, VideoEditorActivity.this.startTime, VideoEditorActivity.this.endTime, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.rotationValue, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.muteVideo ? -1 : VideoEditorActivity.this.originalBitrate, (long) VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration, VideoEditorActivity.this.currentCaption != null ? VideoEditorActivity.this.currentCaption.toString() : null);
                } else {
                    VideoEditorActivity.this.delegate.didFinishEditVideo(VideoEditorActivity.this.videoPath, VideoEditorActivity.this.startTime, VideoEditorActivity.this.endTime, VideoEditorActivity.this.resultWidth, VideoEditorActivity.this.resultHeight, VideoEditorActivity.this.rotationValue, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.muteVideo ? -1 : VideoEditorActivity.this.bitrate, (long) VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration, VideoEditorActivity.this.currentCaption != null ? VideoEditorActivity.this.currentCaption.toString() : null);
                }
            }
            VideoEditorActivity.this.finishFragment();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.8 */
    class C19598 implements OnClickListener {
        C19598() {
        }

        public void onClick(View view) {
            VideoEditorActivity.this.captionEditText.setFieldText(VideoEditorActivity.this.currentCaption);
            VideoEditorActivity.this.captionDoneItem.setVisibility(0);
            VideoEditorActivity.this.videoSeekBarView.setVisibility(8);
            VideoEditorActivity.this.videoTimelineView.setVisibility(8);
            VideoEditorActivity.this.pickerView.setVisibility(8);
            LayoutParams layoutParams = (LayoutParams) VideoEditorActivity.this.captionEditText.getLayoutParams();
            layoutParams.bottomMargin = 0;
            VideoEditorActivity.this.captionEditText.setLayoutParams(layoutParams);
            layoutParams = (LayoutParams) VideoEditorActivity.this.mentionListView.getLayoutParams();
            layoutParams.bottomMargin = 0;
            VideoEditorActivity.this.mentionListView.setLayoutParams(layoutParams);
            VideoEditorActivity.this.captionEditText.openKeyboard();
            VideoEditorActivity.this.oldTitle = VideoEditorActivity.this.actionBar.getSubtitle();
            VideoEditorActivity.this.actionBar.setTitle(LocaleController.getString("VideoCaption", C0338R.string.VideoCaption));
            VideoEditorActivity.this.actionBar.setSubtitle(null);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.9 */
    class C19609 implements OnClickListener {
        C19609() {
        }

        public void onClick(View view) {
            VideoEditorActivity.this.needCompressVideo = !VideoEditorActivity.this.needCompressVideo;
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
            edit.putBoolean("compress_video", VideoEditorActivity.this.needCompressVideo);
            edit.commit();
            VideoEditorActivity.this.compressItem.setImageResource(VideoEditorActivity.this.needCompressVideo ? C0338R.drawable.hd_off : C0338R.drawable.hd_on);
            VideoEditorActivity.this.updateVideoInfo();
        }
    }

    public VideoEditorActivity(Bundle bundle) {
        super(bundle);
        this.sync = new Object();
        this.progressRunnable = new C19511();
        this.videoPath = bundle.getString("videoPath");
    }

    private void closeCaptionEnter(boolean z) {
        if (z) {
            this.currentCaption = this.captionEditText.getFieldCharSequence();
        }
        this.actionBar.setSubtitle(this.oldTitle);
        this.captionDoneItem.setVisibility(8);
        this.pickerView.setVisibility(0);
        this.videoSeekBarView.setVisibility(0);
        this.videoTimelineView.setVisibility(0);
        LayoutParams layoutParams = (LayoutParams) this.captionEditText.getLayoutParams();
        layoutParams.bottomMargin = -AndroidUtilities.dp(400.0f);
        this.captionEditText.setLayoutParams(layoutParams);
        layoutParams = (LayoutParams) this.mentionListView.getLayoutParams();
        layoutParams.bottomMargin = -AndroidUtilities.dp(400.0f);
        this.mentionListView.setLayoutParams(layoutParams);
        this.actionBar.setTitle(this.muteVideo ? LocaleController.getString("AttachGif", C0338R.string.AttachGif) : LocaleController.getString("AttachVideo", C0338R.string.AttachVideo));
        this.captionItem.setImageResource(TextUtils.isEmpty(this.currentCaption) ? C0338R.drawable.photo_text : C0338R.drawable.photo_text2);
        if (this.captionEditText.isPopupShowing()) {
            this.captionEditText.hidePopup();
        } else {
            this.captionEditText.closeKeyboard();
        }
    }

    private void onPlayComplete() {
        if (this.playButton != null) {
            this.playButton.setImageResource(C0338R.drawable.video_edit_play);
        }
        if (!(this.videoSeekBarView == null || this.videoTimelineView == null)) {
            this.videoSeekBarView.setProgress(this.videoTimelineView.getLeftProgress());
        }
        try {
            if (this.videoPlayer != null && this.videoTimelineView != null) {
                this.videoPlayer.seekTo((int) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    private boolean processOpenVideo() {
        try {
            this.originalSize = new File(this.videoPath).length();
            Container isoFile = new IsoFile(this.videoPath);
            List<Box> paths = Path.getPaths(isoFile, "/moov/trak/");
            TrackHeaderBox trackHeaderBox = null;
            Object obj = 1;
            if (Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") == null) {
                obj = null;
            }
            if (obj == null) {
                return false;
            }
            Object obj2;
            if (Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/avc1/") == null) {
                obj2 = null;
            } else {
                int i = 1;
            }
            for (Box box : paths) {
                long j;
                TrackBox trackBox = (TrackBox) box;
                long j2 = 0;
                try {
                    MediaBox mediaBox = trackBox.getMediaBox();
                    MediaHeaderBox mediaHeaderBox = mediaBox.getMediaHeaderBox();
                    long[] sampleSizes = mediaBox.getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes();
                    for (long j3 : sampleSizes) {
                        j2 += j3;
                    }
                    this.videoDuration = ((float) mediaHeaderBox.getDuration()) / ((float) mediaHeaderBox.getTimescale());
                    j = (long) ((int) (((float) (8 * j2)) / this.videoDuration));
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    j = 0;
                }
                TrackHeaderBox trackHeaderBox2 = trackBox.getTrackHeaderBox();
                if (trackHeaderBox2.getWidth() == 0.0d || trackHeaderBox2.getHeight() == 0.0d) {
                    this.audioFramesSize += j2;
                    trackHeaderBox2 = trackHeaderBox;
                } else {
                    int i2 = (int) ((j / 100000) * 100000);
                    this.bitrate = i2;
                    this.originalBitrate = i2;
                    if (this.bitrate > 900000) {
                        this.bitrate = 900000;
                    }
                    this.videoFramesSize += j2;
                }
                trackHeaderBox = trackHeaderBox2;
            }
            if (trackHeaderBox == null) {
                return false;
            }
            Matrix matrix = trackHeaderBox.getMatrix();
            if (matrix.equals(Matrix.ROTATE_90)) {
                this.rotationValue = 90;
            } else if (matrix.equals(Matrix.ROTATE_180)) {
                this.rotationValue = 180;
            } else if (matrix.equals(Matrix.ROTATE_270)) {
                this.rotationValue = 270;
            }
            int width = (int) trackHeaderBox.getWidth();
            this.originalWidth = width;
            this.resultWidth = width;
            width = (int) trackHeaderBox.getHeight();
            this.originalHeight = width;
            this.resultHeight = width;
            if (this.resultWidth > 640 || this.resultHeight > 640) {
                float f = this.resultWidth > this.resultHeight ? 640.0f / ((float) this.resultWidth) : 640.0f / ((float) this.resultHeight);
                this.resultWidth = (int) (((float) this.resultWidth) * f);
                this.resultHeight = (int) (((float) this.resultHeight) * f);
                if (this.bitrate != 0) {
                    this.bitrate = (int) (Math.max(0.5f, f) * ((float) this.bitrate));
                    this.videoFramesSize = (long) (((float) (this.bitrate / 8)) * this.videoDuration);
                }
            }
            if (obj2 == null && (this.resultWidth == this.originalWidth || this.resultHeight == this.originalHeight)) {
                return false;
            }
            this.videoDuration *= 1000.0f;
            updateVideoInfo();
            return true;
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
            return false;
        }
    }

    private void updateVideoInfo() {
        if (this.actionBar != null) {
            int i;
            int i2;
            this.esimatedDuration = (long) Math.ceil((double) ((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
            if (this.compressItem.getVisibility() == 8 || (this.compressItem.getVisibility() == 0 && !this.needCompressVideo)) {
                i = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalHeight : this.originalWidth;
                i2 = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalWidth : this.originalHeight;
                this.estimatedSize = (int) (((float) this.originalSize) * (((float) this.esimatedDuration) / this.videoDuration));
            } else {
                i = (this.rotationValue == 90 || this.rotationValue == 270) ? this.resultHeight : this.resultWidth;
                i2 = (this.rotationValue == 90 || this.rotationValue == 270) ? this.resultWidth : this.resultHeight;
                this.estimatedSize = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * (((float) this.esimatedDuration) / this.videoDuration));
                this.estimatedSize += (this.estimatedSize / TLRPC.MESSAGE_FLAG_EDITED) * 16;
            }
            if (this.videoTimelineView.getLeftProgress() == 0.0f) {
                this.startTime = -1;
            } else {
                this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
            }
            if (this.videoTimelineView.getRightProgress() == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                this.endTime = -1;
            } else {
                this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
            }
            String format = String.format("%dx%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
            int ceil = ((int) Math.ceil((double) (this.esimatedDuration / 1000))) - (((int) ((this.esimatedDuration / 1000) / 60)) * 60);
            String format2 = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.esimatedDuration / 1000) / 60)), Integer.valueOf(ceil), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
            this.actionBar.setSubtitle(String.format("%s, %s", new Object[]{format, format2}));
        }
    }

    public View createView(Context context) {
        this.needCompressVideo = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("compress_video", true);
        this.actionBar.setBackgroundColor(Theme.MSG_TEXT_COLOR);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR);
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("AttachVideo", C0338R.string.AttachVideo));
        this.actionBar.setSubtitleColor(-1);
        this.actionBar.setActionBarMenuOnItemClick(new C19554());
        this.captionDoneItem = this.actionBar.createMenu().addItemWithWidth(1, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.captionDoneItem.setVisibility(8);
        this.fragmentView = new C19565(context);
        this.fragmentView.setBackgroundColor(Theme.MSG_TEXT_COLOR);
        SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto = (SizeNotifierFrameLayoutPhoto) this.fragmentView;
        sizeNotifierFrameLayoutPhoto.setWithoutWindow(true);
        this.pickerView = new PickerBottomLayoutViewer(context);
        this.pickerView.setBackgroundColor(0);
        this.pickerView.updateSelectedCount(0, false);
        sizeNotifierFrameLayoutPhoto.addView(this.pickerView, LayoutHelper.createFrame(-1, 48, 83));
        this.pickerView.cancelButton.setOnClickListener(new C19576());
        this.pickerView.doneButton.setOnClickListener(new C19587());
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        this.pickerView.addView(linearLayout, LayoutHelper.createFrame(-2, 48, 49));
        this.captionItem = new ImageView(context);
        this.captionItem.setScaleType(ScaleType.CENTER);
        this.captionItem.setImageResource(TextUtils.isEmpty(this.currentCaption) ? C0338R.drawable.photo_text : C0338R.drawable.photo_text2);
        this.captionItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        linearLayout.addView(this.captionItem, LayoutHelper.createLinear(56, 48));
        this.captionItem.setOnClickListener(new C19598());
        this.compressItem = new ImageView(context);
        this.compressItem.setScaleType(ScaleType.CENTER);
        this.compressItem.setImageResource(this.needCompressVideo ? C0338R.drawable.hd_off : C0338R.drawable.hd_on);
        this.compressItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        ImageView imageView = this.compressItem;
        int i = (this.originalHeight == this.resultHeight && this.originalWidth == this.resultWidth) ? 8 : 0;
        imageView.setVisibility(i);
        linearLayout.addView(this.compressItem, LayoutHelper.createLinear(56, 48));
        this.compressItem.setOnClickListener(new C19609());
        if (VERSION.SDK_INT < 18) {
            try {
                MediaCodecInfo a = MediaController.m68a(MimeTypes.VIDEO_H264);
                if (a == null) {
                    this.compressItem.setVisibility(8);
                } else {
                    String name = a.getName();
                    if (name.equals("OMX.google.h264.encoder") || name.equals("OMX.ST.VFM.H264Enc") || name.equals("OMX.Exynos.avc.enc") || name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") || name.equals("OMX.MARVELL.VIDEO.H264ENCODER") || name.equals("OMX.k3.video.encoder.avc") || name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                        this.compressItem.setVisibility(8);
                    } else if (MediaController.m59a(a, MimeTypes.VIDEO_H264) == 0) {
                        this.compressItem.setVisibility(8);
                    }
                }
            } catch (Throwable e) {
                this.compressItem.setVisibility(8);
                FileLog.m18e("tmessages", e);
            }
        }
        this.muteItem = new ImageView(context);
        this.muteItem.setScaleType(ScaleType.CENTER);
        this.muteItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        linearLayout.addView(this.muteItem, LayoutHelper.createLinear(56, 48));
        this.muteItem.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VideoEditorActivity.this.muteVideo = !VideoEditorActivity.this.muteVideo;
                VideoEditorActivity.this.updateMuteButton();
            }
        });
        this.videoTimelineView = new VideoTimelineView(context);
        this.videoTimelineView.setVideoPath(this.videoPath);
        this.videoTimelineView.setDelegate(new VideoTimelineViewDelegate() {
            public void onLeftProgressChanged(float f) {
                if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared) {
                    try {
                        if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                            VideoEditorActivity.this.videoPlayer.pause();
                            VideoEditorActivity.this.playButton.setImageResource(C0338R.drawable.video_edit_play);
                        }
                        VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
                        VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * f));
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                    VideoEditorActivity.this.needSeek = true;
                    VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
                    VideoEditorActivity.this.updateVideoInfo();
                }
            }

            public void onRifhtProgressChanged(float f) {
                if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared) {
                    try {
                        if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                            VideoEditorActivity.this.videoPlayer.pause();
                            VideoEditorActivity.this.playButton.setImageResource(C0338R.drawable.video_edit_play);
                        }
                        VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
                        VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * f));
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                    VideoEditorActivity.this.needSeek = true;
                    VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
                    VideoEditorActivity.this.updateVideoInfo();
                }
            }
        });
        sizeNotifierFrameLayoutPhoto.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, 44.0f, 83, 0.0f, 0.0f, 0.0f, 67.0f));
        this.videoSeekBarView = new VideoSeekBarView(context);
        this.videoSeekBarView.setDelegate(new SeekBarDelegate() {
            public void onSeekBarDrag(float f) {
                if (f < VideoEditorActivity.this.videoTimelineView.getLeftProgress()) {
                    f = VideoEditorActivity.this.videoTimelineView.getLeftProgress();
                    VideoEditorActivity.this.videoSeekBarView.setProgress(f);
                } else if (f > VideoEditorActivity.this.videoTimelineView.getRightProgress()) {
                    f = VideoEditorActivity.this.videoTimelineView.getRightProgress();
                    VideoEditorActivity.this.videoSeekBarView.setProgress(f);
                }
                if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared) {
                    if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                        try {
                            VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * f));
                            VideoEditorActivity.this.lastProgress = f;
                            return;
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                            return;
                        }
                    }
                    VideoEditorActivity.this.lastProgress = f;
                    VideoEditorActivity.this.needSeek = true;
                }
            }
        });
        sizeNotifierFrameLayoutPhoto.addView(this.videoSeekBarView, LayoutHelper.createFrame(-1, 40.0f, 83, 11.0f, 0.0f, 11.0f, 112.0f));
        this.textureView = new TextureView(context);
        this.textureView.setSurfaceTextureListener(new SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                if (VideoEditorActivity.this.textureView != null && VideoEditorActivity.this.textureView.isAvailable() && VideoEditorActivity.this.videoPlayer != null) {
                    try {
                        VideoEditorActivity.this.videoPlayer.setSurface(new Surface(VideoEditorActivity.this.textureView.getSurfaceTexture()));
                        if (VideoEditorActivity.this.playerPrepared) {
                            VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration));
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (VideoEditorActivity.this.videoPlayer != null) {
                    VideoEditorActivity.this.videoPlayer.setDisplay(null);
                }
                return true;
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });
        sizeNotifierFrameLayoutPhoto.addView(this.textureView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 14.0f, 0.0f, 140.0f));
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.playButton.setImageResource(C0338R.drawable.video_edit_play);
        this.playButton.setOnClickListener(new OnClickListener() {

            /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.14.1 */
            class C19471 implements OnSeekCompleteListener {
                C19471() {
                }

                public void onSeekComplete(MediaPlayer mediaPlayer) {
                    float leftProgress = VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration;
                    float rightProgress = VideoEditorActivity.this.videoTimelineView.getRightProgress() * VideoEditorActivity.this.videoDuration;
                    if (leftProgress == rightProgress) {
                        leftProgress = rightProgress - 0.01f;
                    }
                    VideoEditorActivity.this.lastProgress = (((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) - leftProgress) / (rightProgress - leftProgress);
                    leftProgress = VideoEditorActivity.this.videoTimelineView.getRightProgress() - VideoEditorActivity.this.videoTimelineView.getLeftProgress();
                    VideoEditorActivity.this.lastProgress = (leftProgress * VideoEditorActivity.this.lastProgress) + VideoEditorActivity.this.videoTimelineView.getLeftProgress();
                    VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.lastProgress);
                }
            }

            public void onClick(View view) {
                if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared) {
                    if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                        VideoEditorActivity.this.videoPlayer.pause();
                        VideoEditorActivity.this.playButton.setImageResource(C0338R.drawable.video_edit_play);
                        return;
                    }
                    try {
                        VideoEditorActivity.this.playButton.setImageDrawable(null);
                        VideoEditorActivity.this.lastProgress = 0.0f;
                        if (VideoEditorActivity.this.needSeek) {
                            VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * VideoEditorActivity.this.videoSeekBarView.getProgress()));
                            VideoEditorActivity.this.needSeek = false;
                        }
                        VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(new C19471());
                        VideoEditorActivity.this.videoPlayer.start();
                        synchronized (VideoEditorActivity.this.sync) {
                            if (VideoEditorActivity.this.thread == null) {
                                VideoEditorActivity.this.thread = new Thread(VideoEditorActivity.this.progressRunnable);
                                VideoEditorActivity.this.thread.start();
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }
        });
        sizeNotifierFrameLayoutPhoto.addView(this.playButton, LayoutHelper.createFrame(100, 100.0f, 17, 0.0f, 0.0f, 0.0f, 70.0f));
        if (this.captionEditText != null) {
            this.captionEditText.onDestroy();
        }
        this.captionEditText = new PhotoViewerCaptionEnterView(context, sizeNotifierFrameLayoutPhoto, null);
        this.captionEditText.setDelegate(new PhotoViewerCaptionEnterViewDelegate() {
            public void onCaptionEnter() {
                VideoEditorActivity.this.closeCaptionEnter(true);
            }

            public void onTextChanged(CharSequence charSequence) {
                if (VideoEditorActivity.this.mentionsAdapter != null && VideoEditorActivity.this.captionEditText != null && VideoEditorActivity.this.parentChatActivity != null && charSequence != null) {
                    VideoEditorActivity.this.mentionsAdapter.searchUsernameOrHashtag(charSequence.toString(), VideoEditorActivity.this.captionEditText.getCursorPosition(), VideoEditorActivity.this.parentChatActivity.messages);
                }
            }

            public void onWindowSizeChanged(int i) {
                if (i - (ActionBar.getCurrentActionBarHeight() * 2) < AndroidUtilities.dp((float) ((VideoEditorActivity.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0) + (Math.min(3, VideoEditorActivity.this.mentionsAdapter.getItemCount()) * 36)))) {
                    VideoEditorActivity.this.allowMentions = false;
                    if (VideoEditorActivity.this.mentionListView != null && VideoEditorActivity.this.mentionListView.getVisibility() == 0) {
                        VideoEditorActivity.this.mentionListView.setVisibility(4);
                    }
                } else {
                    VideoEditorActivity.this.allowMentions = true;
                    if (VideoEditorActivity.this.mentionListView != null && VideoEditorActivity.this.mentionListView.getVisibility() == 4) {
                        VideoEditorActivity.this.mentionListView.setVisibility(0);
                    }
                }
                VideoEditorActivity.this.fragmentView.requestLayout();
            }
        });
        sizeNotifierFrameLayoutPhoto.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, -400.0f));
        this.captionEditText.onCreate();
        this.mentionListView = new RecyclerListView(context);
        this.mentionListView.setTag(Integer.valueOf(5));
        this.mentionLayoutManager = new AnonymousClass16(context);
        this.mentionLayoutManager.setOrientation(1);
        this.mentionListView.setLayoutManager(this.mentionLayoutManager);
        this.mentionListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.mentionListView.setVisibility(8);
        this.mentionListView.setClipToPadding(true);
        this.mentionListView.setOverScrollMode(2);
        sizeNotifierFrameLayoutPhoto.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
        RecyclerListView recyclerListView = this.mentionListView;
        Adapter mentionsAdapter = new MentionsAdapter(context, true, 0, new MentionsAdapterDelegate() {

            /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.17.1 */
            class C19481 extends AnimatorListenerAdapterProxy {
                C19481() {
                }

                public void onAnimationEnd(Animator animator) {
                    if (VideoEditorActivity.this.mentionListAnimation != null && VideoEditorActivity.this.mentionListAnimation.equals(animator)) {
                        VideoEditorActivity.this.mentionListAnimation = null;
                    }
                }
            }

            /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.17.2 */
            class C19492 extends AnimatorListenerAdapterProxy {
                C19492() {
                }

                public void onAnimationEnd(Animator animator) {
                    if (VideoEditorActivity.this.mentionListAnimation != null && VideoEditorActivity.this.mentionListAnimation.equals(animator)) {
                        VideoEditorActivity.this.mentionListView.setVisibility(8);
                        VideoEditorActivity.this.mentionListAnimation = null;
                    }
                }
            }

            public void needChangePanelVisibility(boolean z) {
                if (z) {
                    LayoutParams layoutParams = (LayoutParams) VideoEditorActivity.this.mentionListView.getLayoutParams();
                    int min = (VideoEditorActivity.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0) + (Math.min(3, VideoEditorActivity.this.mentionsAdapter.getItemCount()) * 36);
                    layoutParams.height = AndroidUtilities.dp((float) min);
                    layoutParams.topMargin = -AndroidUtilities.dp((float) min);
                    VideoEditorActivity.this.mentionListView.setLayoutParams(layoutParams);
                    if (VideoEditorActivity.this.mentionListAnimation != null) {
                        VideoEditorActivity.this.mentionListAnimation.cancel();
                        VideoEditorActivity.this.mentionListAnimation = null;
                    }
                    if (VideoEditorActivity.this.mentionListView.getVisibility() == 0) {
                        VideoEditorActivity.this.mentionListView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        return;
                    }
                    VideoEditorActivity.this.mentionLayoutManager.scrollToPositionWithOffset(0, AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS);
                    if (VideoEditorActivity.this.allowMentions) {
                        VideoEditorActivity.this.mentionListView.setVisibility(0);
                        VideoEditorActivity.this.mentionListAnimation = new AnimatorSet();
                        VideoEditorActivity.this.mentionListAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(VideoEditorActivity.this.mentionListView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
                        VideoEditorActivity.this.mentionListAnimation.addListener(new C19481());
                        VideoEditorActivity.this.mentionListAnimation.setDuration(200);
                        VideoEditorActivity.this.mentionListAnimation.start();
                        return;
                    }
                    VideoEditorActivity.this.mentionListView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    VideoEditorActivity.this.mentionListView.setVisibility(4);
                    return;
                }
                if (VideoEditorActivity.this.mentionListAnimation != null) {
                    VideoEditorActivity.this.mentionListAnimation.cancel();
                    VideoEditorActivity.this.mentionListAnimation = null;
                }
                if (VideoEditorActivity.this.mentionListView.getVisibility() == 8) {
                    return;
                }
                if (VideoEditorActivity.this.allowMentions) {
                    VideoEditorActivity.this.mentionListAnimation = new AnimatorSet();
                    AnimatorSet access$4400 = VideoEditorActivity.this.mentionListAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(VideoEditorActivity.this.mentionListView, "alpha", new float[]{0.0f});
                    access$4400.playTogether(animatorArr);
                    VideoEditorActivity.this.mentionListAnimation.addListener(new C19492());
                    VideoEditorActivity.this.mentionListAnimation.setDuration(200);
                    VideoEditorActivity.this.mentionListAnimation.start();
                    return;
                }
                VideoEditorActivity.this.mentionListView.setVisibility(8);
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
                Object item = VideoEditorActivity.this.mentionsAdapter.getItem(i);
                int resultStartPosition = VideoEditorActivity.this.mentionsAdapter.getResultStartPosition();
                int resultLength = VideoEditorActivity.this.mentionsAdapter.getResultLength();
                if (item instanceof User) {
                    User user = (User) item;
                    if (user != null) {
                        VideoEditorActivity.this.captionEditText.replaceWithText(resultStartPosition, resultLength, "@" + user.username + " ");
                    }
                } else if (item instanceof String) {
                    VideoEditorActivity.this.captionEditText.replaceWithText(resultStartPosition, resultLength, item + " ");
                }
            }
        });
        this.mentionListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            /* renamed from: com.hanista.mobogram.ui.VideoEditorActivity.19.1 */
            class C19501 implements DialogInterface.OnClickListener {
                C19501() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    VideoEditorActivity.this.mentionsAdapter.clearRecentHashtags();
                }
            }

            public boolean onItemClick(View view, int i) {
                if (VideoEditorActivity.this.getParentActivity() == null || !(VideoEditorActivity.this.mentionsAdapter.getItem(i) instanceof String)) {
                    return false;
                }
                Builder builder = new Builder(VideoEditorActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setMessage(LocaleController.getString("ClearSearch", C0338R.string.ClearSearch));
                builder.setPositiveButton(LocaleController.getString("ClearButton", C0338R.string.ClearButton).toUpperCase(), new C19501());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                VideoEditorActivity.this.showDialog(builder.create());
                return true;
            }
        });
        updateVideoInfo();
        updateMuteButton();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public boolean onFragmentCreate() {
        if (this.created) {
            return true;
        }
        if (this.videoPath == null || !processOpenVideo()) {
            return false;
        }
        this.videoPlayer = new MediaPlayer();
        this.videoPlayer.setOnCompletionListener(new C19532());
        this.videoPlayer.setOnPreparedListener(new C19543());
        try {
            this.videoPlayer.setDataSource(this.videoPath);
            this.videoPlayer.prepareAsync();
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
            this.created = true;
            return super.onFragmentCreate();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return false;
        }
    }

    public void onFragmentDestroy() {
        if (this.videoTimelineView != null) {
            this.videoTimelineView.destroy();
        }
        if (this.videoPlayer != null) {
            try {
                this.videoPlayer.stop();
                this.videoPlayer.release();
                this.videoPlayer = null;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        if (this.captionEditText != null) {
            this.captionEditText.onDestroy();
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        super.onFragmentDestroy();
    }

    public void onPause() {
        super.onPause();
        if (this.captionDoneItem.getVisibility() != 8) {
            closeCaptionEnter(true);
        }
    }

    public void setDelegate(VideoEditorActivityDelegate videoEditorActivityDelegate) {
        this.delegate = videoEditorActivityDelegate;
    }

    public void setParentChatActivity(ChatActivity chatActivity) {
        this.parentChatActivity = chatActivity;
    }

    public void updateMuteButton() {
        if (this.videoPlayer != null) {
            float f = this.muteVideo ? 0.0f : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            if (this.videoPlayer != null) {
                this.videoPlayer.setVolume(f, f);
            }
        }
        if (this.muteVideo) {
            this.actionBar.setTitle(LocaleController.getString("AttachGif", C0338R.string.AttachGif));
            this.muteItem.setImageResource(C0338R.drawable.volume_off);
            if (this.captionItem.getVisibility() == 0) {
                this.needCompressVideo = true;
                this.compressItem.setImageResource(C0338R.drawable.hd_off);
                this.compressItem.setClickable(false);
                this.compressItem.setAlpha(DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD);
                this.compressItem.setEnabled(false);
                return;
            }
            return;
        }
        this.actionBar.setTitle(LocaleController.getString("AttachVideo", C0338R.string.AttachVideo));
        this.muteItem.setImageResource(C0338R.drawable.volume_on);
        if (this.captionItem.getVisibility() == 0) {
            this.compressItem.setClickable(true);
            this.compressItem.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.compressItem.setEnabled(true);
        }
    }
}
