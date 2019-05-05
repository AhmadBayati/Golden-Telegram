package com.hanista.mobogram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.FileDownloadProgressListener;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.audioinfo.AudioInfo;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.LineProgressView;
import java.io.File;

public class AudioPlayerActivity extends BaseFragment implements FileDownloadProgressListener, NotificationCenterDelegate {
    private int TAG;
    private ImageView[] buttons;
    private TextView durationTextView;
    private MessageObject lastMessageObject;
    private String lastTimeString;
    private ImageView nextButton;
    private ImageView placeholder;
    private ImageView playButton;
    private ImageView prevButton;
    private LineProgressView progressView;
    private ImageView repeatButton;
    private SeekBarView seekBarView;
    private ImageView shuffleButton;
    private TextView timeTextView;

    /* renamed from: com.hanista.mobogram.ui.AudioPlayerActivity.1 */
    class C10501 implements OnTouchListener {
        C10501() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.AudioPlayerActivity.2 */
    class C10512 extends ActionBarMenuOnItemClick {
        C10512() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                AudioPlayerActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.AudioPlayerActivity.3 */
    class C10523 extends FrameLayout {
        C10523(Context context) {
            super(context);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int dp = ((i3 - i) - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET)) / 4;
            for (int i5 = 0; i5 < 5; i5++) {
                int dp2 = AndroidUtilities.dp((float) ((i5 * 48) + 15)) + (dp * i5);
                int dp3 = AndroidUtilities.dp(9.0f);
                AudioPlayerActivity.this.buttons[i5].layout(dp2, dp3, AudioPlayerActivity.this.buttons[i5].getMeasuredWidth() + dp2, AudioPlayerActivity.this.buttons[i5].getMeasuredHeight() + dp3);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.AudioPlayerActivity.4 */
    class C10534 implements OnClickListener {
        C10534() {
        }

        public void onClick(View view) {
            MediaController.m71a().m190r();
            AudioPlayerActivity.this.updateRepeatButton();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.AudioPlayerActivity.5 */
    class C10545 implements OnClickListener {
        C10545() {
        }

        public void onClick(View view) {
            MediaController.m71a().m185m();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.AudioPlayerActivity.6 */
    class C10556 implements OnClickListener {
        C10556() {
        }

        public void onClick(View view) {
            if (!MediaController.m71a().m192t()) {
                if (MediaController.m71a().m191s()) {
                    MediaController.m71a().m158a(MediaController.m71a().m182j());
                } else {
                    MediaController.m71a().m166b(MediaController.m71a().m182j());
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.AudioPlayerActivity.7 */
    class C10567 implements OnClickListener {
        C10567() {
        }

        public void onClick(View view) {
            MediaController.m71a().m184l();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.AudioPlayerActivity.8 */
    class C10578 implements OnClickListener {
        C10578() {
        }

        public void onClick(View view) {
            MediaController.m71a().m189q();
            AudioPlayerActivity.this.updateShuffleButton();
        }
    }

    private class SeekBarView extends FrameLayout {
        private Paint innerPaint1;
        private Paint outerPaint1;
        private boolean pressed;
        public int thumbDX;
        private int thumbHeight;
        private int thumbWidth;
        public int thumbX;

        public SeekBarView(Context context) {
            super(context);
            this.thumbX = 0;
            this.thumbDX = 0;
            this.pressed = false;
            setWillNotDraw(false);
            this.innerPaint1 = new Paint(1);
            this.innerPaint1.setColor(419430400);
            this.outerPaint1 = new Paint(1);
            this.outerPaint1.setColor(-14438417);
            this.thumbWidth = AndroidUtilities.dp(24.0f);
            this.thumbHeight = AndroidUtilities.dp(24.0f);
        }

        public boolean isDragging() {
            return this.pressed;
        }

        protected void onDraw(Canvas canvas) {
            int measuredHeight = (getMeasuredHeight() - this.thumbHeight) / 2;
            canvas.drawRect((float) (this.thumbWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), (float) (getMeasuredWidth() - (this.thumbWidth / 2)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), this.innerPaint1);
            canvas.drawRect((float) (this.thumbWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), (float) ((this.thumbWidth / 2) + this.thumbX), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), this.outerPaint1);
            canvas.drawCircle((float) (this.thumbX + (this.thumbWidth / 2)), (float) ((this.thumbHeight / 2) + measuredHeight), (float) AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f), this.outerPaint1);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return onTouch(motionEvent);
        }

        boolean onTouch(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                int measuredHeight = (getMeasuredHeight() - this.thumbWidth) / 2;
                if (((float) (this.thumbX - measuredHeight)) <= motionEvent.getX() && motionEvent.getX() <= ((float) (measuredHeight + (this.thumbX + this.thumbWidth))) && motionEvent.getY() >= 0.0f && motionEvent.getY() <= ((float) getMeasuredHeight())) {
                    this.pressed = true;
                    this.thumbDX = (int) (motionEvent.getX() - ((float) this.thumbX));
                    invalidate();
                    return true;
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (this.pressed) {
                    if (motionEvent.getAction() == 1) {
                        AudioPlayerActivity.this.onSeekBarDrag(((float) this.thumbX) / ((float) (getMeasuredWidth() - this.thumbWidth)));
                    }
                    this.pressed = false;
                    invalidate();
                    return true;
                }
            } else if (motionEvent.getAction() == 2 && this.pressed) {
                this.thumbX = (int) (motionEvent.getX() - ((float) this.thumbDX));
                if (this.thumbX < 0) {
                    this.thumbX = 0;
                } else if (this.thumbX > getMeasuredWidth() - this.thumbWidth) {
                    this.thumbX = getMeasuredWidth() - this.thumbWidth;
                }
                invalidate();
                return true;
            }
            return false;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return onTouch(motionEvent);
        }

        public void setProgress(float f) {
            int ceil = (int) Math.ceil((double) (((float) (getMeasuredWidth() - this.thumbWidth)) * f));
            if (this.thumbX != ceil) {
                this.thumbX = ceil;
                if (this.thumbX < 0) {
                    this.thumbX = 0;
                } else if (this.thumbX > getMeasuredWidth() - this.thumbWidth) {
                    this.thumbX = getMeasuredWidth() - this.thumbWidth;
                }
                invalidate();
            }
        }
    }

    public AudioPlayerActivity() {
        this.buttons = new ImageView[5];
    }

    private void checkIfMusicDownloaded(MessageObject messageObject) {
        File file = null;
        if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
            File file2 = new File(messageObject.messageOwner.attachPath);
            if (file2.exists()) {
                file = file2;
            }
        }
        if (file == null) {
            file = FileLoader.getPathToMessage(messageObject.messageOwner);
        }
        if (file.exists()) {
            MediaController.m71a().m149a((FileDownloadProgressListener) this);
            this.progressView.setVisibility(4);
            this.seekBarView.setVisibility(0);
            this.playButton.setEnabled(true);
            return;
        }
        String fileName = messageObject.getFileName();
        MediaController.m71a().m151a(fileName, (FileDownloadProgressListener) this);
        Float fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
        this.progressView.setProgress(fileProgress != null ? fileProgress.floatValue() : 0.0f, false);
        this.progressView.setVisibility(0);
        this.seekBarView.setVisibility(4);
        this.playButton.setEnabled(false);
    }

    private void onSeekBarDrag(float f) {
        MediaController.m71a().m159a(MediaController.m71a().m182j(), f);
    }

    private void updateProgress(MessageObject messageObject) {
        if (this.seekBarView != null) {
            if (!this.seekBarView.isDragging()) {
                this.seekBarView.setProgress(messageObject.audioProgress);
            }
            CharSequence format = String.format("%d:%02d", new Object[]{Integer.valueOf(messageObject.audioProgressSec / 60), Integer.valueOf(messageObject.audioProgressSec % 60)});
            if (this.lastTimeString == null || !(this.lastTimeString == null || this.lastTimeString.equals(format))) {
                this.lastTimeString = format;
                this.timeTextView.setText(format);
            }
        }
    }

    private void updateRepeatButton() {
        int p = MediaController.m71a().m188p();
        if (p == 0) {
            this.repeatButton.setImageResource(C0338R.drawable.pl_repeat);
        } else if (p == 1) {
            this.repeatButton.setImageResource(C0338R.drawable.pl_repeat_active);
        } else if (p == 2) {
            this.repeatButton.setImageResource(C0338R.drawable.pl_repeat1_active);
        }
    }

    private void updateShuffleButton() {
        if (MediaController.m71a().m187o()) {
            this.shuffleButton.setImageResource(C0338R.drawable.pl_shuffle_active);
        } else {
            this.shuffleButton.setImageResource(C0338R.drawable.pl_shuffle);
        }
    }

    private void updateTitle(boolean z) {
        int i = -1;
        MessageObject j = MediaController.m71a().m182j();
        if (!(j == null && z) && (j == null || j.isMusic())) {
            if (j != null) {
                checkIfMusicDownloaded(j);
                updateProgress(j);
                if (MediaController.m71a().m191s()) {
                    this.playButton.setImageResource(C0338R.drawable.player_play_states);
                } else {
                    this.playButton.setImageResource(C0338R.drawable.player_pause_states);
                }
                if (this.actionBar != null) {
                    this.actionBar.setTitle(j.getMusicTitle());
                    this.actionBar.setSubtitle(j.getMusicAuthor());
                    if (ThemeUtil.m2490b()) {
                        if (AdvanceTheme.f2491b == -1) {
                            i = Theme.STICKERS_SHEET_TITLE_TEXT_COLOR;
                        }
                        this.actionBar.getTitleTextView().setTextColor(i);
                    } else {
                        this.actionBar.getTitleTextView().setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
                        this.actionBar.getSubtitleTextView().setTextColor(-7697782);
                    }
                }
                AudioInfo n = MediaController.m71a().m186n();
                if (n == null || n.getCover() == null) {
                    this.placeholder.setImageResource(C0338R.drawable.nocover);
                    this.placeholder.setPadding(0, 0, 0, AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
                    this.placeholder.setScaleType(ScaleType.CENTER);
                } else {
                    this.placeholder.setImageBitmap(n.getCover());
                    this.placeholder.setPadding(0, 0, 0, 0);
                    this.placeholder.setScaleType(ScaleType.CENTER_CROP);
                }
                if (this.durationTextView != null) {
                    CharSequence format;
                    Document document = j.getDocument();
                    if (document != null) {
                        for (int i2 = 0; i2 < document.attributes.size(); i2++) {
                            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i2);
                            if (documentAttribute instanceof TL_documentAttributeAudio) {
                                i = documentAttribute.duration;
                                break;
                            }
                        }
                    }
                    i = 0;
                    TextView textView = this.durationTextView;
                    if (i != 0) {
                        format = String.format("%d:%02d", new Object[]{Integer.valueOf(i / 60), Integer.valueOf(i % 60)});
                    } else {
                        format = "-:--";
                    }
                    textView.setText(format);
                }
            }
        } else if (this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() || this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) != this) {
            removeSelfFromStack();
        } else {
            finishFragment();
        }
    }

    public View createView(Context context) {
        View frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        frameLayout.setOnTouchListener(new C10501());
        this.fragmentView = frameLayout;
        this.actionBar.setBackgroundColor(-1);
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2491b != -1 ? -1 : Theme.STICKERS_SHEET_TITLE_TEXT_COLOR;
            Drawable drawable = context.getResources().getDrawable(C0338R.drawable.pl_back);
            drawable.setColorFilter(i, Mode.SRC_IN);
            this.actionBar.setBackButtonDrawable(drawable);
        } else {
            this.actionBar.setBackButtonImage(C0338R.drawable.pl_back);
        }
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new C10512());
        this.placeholder = new ImageView(context);
        frameLayout.addView(this.placeholder, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 0.0f, 0.0f, 66.0f));
        View view = new View(context);
        view.setBackgroundResource(C0338R.drawable.header_shadow_reverse);
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 96.0f));
        view = new FrameLayout(context);
        view.setBackgroundColor(-436207617);
        frameLayout.addView(view, LayoutHelper.createFrame(-1, BitmapDescriptorFactory.HUE_ORANGE, 83, 0.0f, 0.0f, 0.0f, 66.0f));
        this.timeTextView = new TextView(context);
        this.timeTextView.setTextSize(1, 12.0f);
        this.timeTextView.setTextColor(-15095832);
        this.timeTextView.setGravity(17);
        this.timeTextView.setText("0:00");
        this.timeTextView.setTypeface(FontUtil.m1176a().m1161d());
        view.addView(this.timeTextView, LayoutHelper.createFrame(44, -1, 51));
        this.durationTextView = new TextView(context);
        this.durationTextView.setTextSize(1, 12.0f);
        this.durationTextView.setTextColor(-7697782);
        this.durationTextView.setGravity(17);
        this.durationTextView.setText("3:00");
        this.durationTextView.setTypeface(FontUtil.m1176a().m1161d());
        view.addView(this.durationTextView, LayoutHelper.createFrame(44, -1, 53));
        this.seekBarView = new SeekBarView(context);
        view.addView(this.seekBarView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 32.0f, 0.0f, 32.0f, 0.0f));
        this.progressView = new LineProgressView(context);
        this.progressView.setVisibility(4);
        this.progressView.setBackgroundColor(419430400);
        this.progressView.setProgressColor(-14438417);
        view.addView(this.progressView, LayoutHelper.createFrame(-1, 2.0f, 19, 44.0f, 0.0f, 44.0f, 0.0f));
        View c10523 = new C10523(context);
        c10523.setBackgroundColor(-1);
        frameLayout.addView(c10523, LayoutHelper.createFrame(-1, 66, 83));
        ImageView[] imageViewArr = this.buttons;
        ImageView imageView = new ImageView(context);
        this.repeatButton = imageView;
        imageViewArr[0] = imageView;
        this.repeatButton.setScaleType(ScaleType.CENTER);
        c10523.addView(this.repeatButton, LayoutHelper.createFrame(48, 48, 51));
        this.repeatButton.setOnClickListener(new C10534());
        imageViewArr = this.buttons;
        imageView = new ImageView(context);
        this.prevButton = imageView;
        imageViewArr[1] = imageView;
        this.prevButton.setScaleType(ScaleType.CENTER);
        this.prevButton.setImageResource(C0338R.drawable.player_prev_states);
        c10523.addView(this.prevButton, LayoutHelper.createFrame(48, 48, 51));
        this.prevButton.setOnClickListener(new C10545());
        imageViewArr = this.buttons;
        imageView = new ImageView(context);
        this.playButton = imageView;
        imageViewArr[2] = imageView;
        this.playButton.setScaleType(ScaleType.CENTER);
        this.playButton.setImageResource(C0338R.drawable.player_play_states);
        c10523.addView(this.playButton, LayoutHelper.createFrame(48, 48, 51));
        this.playButton.setOnClickListener(new C10556());
        imageViewArr = this.buttons;
        imageView = new ImageView(context);
        this.nextButton = imageView;
        imageViewArr[3] = imageView;
        this.nextButton.setScaleType(ScaleType.CENTER);
        this.nextButton.setImageResource(C0338R.drawable.player_next_states);
        c10523.addView(this.nextButton, LayoutHelper.createFrame(48, 48, 51));
        this.nextButton.setOnClickListener(new C10567());
        imageViewArr = this.buttons;
        imageView = new ImageView(context);
        this.shuffleButton = imageView;
        imageViewArr[4] = imageView;
        this.shuffleButton.setScaleType(ScaleType.CENTER);
        c10523.addView(this.shuffleButton, LayoutHelper.createFrame(48, 48, 51));
        this.shuffleButton.setOnClickListener(new C10578());
        updateTitle(false);
        updateRepeatButton();
        updateShuffleButton();
        return frameLayout;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.audioDidStarted || i == NotificationCenter.audioPlayStateChanged || i == NotificationCenter.audioDidReset) {
            boolean z = i == NotificationCenter.audioDidReset && ((Boolean) objArr[1]).booleanValue();
            updateTitle(z);
        } else if (i == NotificationCenter.audioProgressDidChanged) {
            MessageObject j = MediaController.m71a().m182j();
            if (j != null && j.isMusic()) {
                updateProgress(j);
            }
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onFailedDownload(String str) {
    }

    public boolean onFragmentCreate() {
        this.TAG = MediaController.m71a().m178g();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
        MediaController.m71a().m149a((FileDownloadProgressListener) this);
        super.onFragmentDestroy();
    }

    public void onProgressDownload(String str, float f) {
        this.progressView.setProgress(f, true);
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public void onSuccessDownload(String str) {
    }
}
