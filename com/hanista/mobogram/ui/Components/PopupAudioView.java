package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.FileDownloadProgressListener;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.BaseCell;
import com.hanista.mobogram.ui.Components.SeekBar.SeekBarDelegate;
import java.lang.reflect.Array;

public class PopupAudioView extends BaseCell implements FileDownloadProgressListener, SeekBarDelegate {
    private static Drawable backgroundMediaDrawableIn;
    private static Drawable[][] statesDrawable;
    private static TextPaint timePaint;
    private int TAG;
    private int buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    protected MessageObject currentMessageObject;
    private String lastTimeString;
    private ProgressView progressView;
    private SeekBar seekBar;
    private int seekBarX;
    private int seekBarY;
    private StaticLayout timeLayout;
    int timeWidth;
    private int timeX;
    private boolean wasLayout;

    static {
        statesDrawable = (Drawable[][]) Array.newInstance(Drawable.class, new int[]{8, 2});
    }

    public PopupAudioView(Context context) {
        super(context);
        this.wasLayout = false;
        this.buttonState = 0;
        this.buttonPressed = 0;
        this.timeWidth = 0;
        this.lastTimeString = null;
        if (backgroundMediaDrawableIn == null) {
            backgroundMediaDrawableIn = getResources().getDrawable(C0338R.drawable.msg_in_photo);
            statesDrawable[0][0] = getResources().getDrawable(C0338R.drawable.play_g);
            statesDrawable[0][1] = getResources().getDrawable(C0338R.drawable.play_g_s);
            statesDrawable[1][0] = getResources().getDrawable(C0338R.drawable.pause_g);
            statesDrawable[1][1] = getResources().getDrawable(C0338R.drawable.pause_g_s);
            statesDrawable[2][0] = getResources().getDrawable(C0338R.drawable.file_g_load);
            statesDrawable[2][1] = getResources().getDrawable(C0338R.drawable.file_g_load_s);
            statesDrawable[3][0] = getResources().getDrawable(C0338R.drawable.file_g_cancel);
            statesDrawable[3][1] = getResources().getDrawable(C0338R.drawable.file_g_cancel_s);
            statesDrawable[4][0] = getResources().getDrawable(C0338R.drawable.play_b);
            statesDrawable[4][1] = getResources().getDrawable(C0338R.drawable.play_b_s);
            statesDrawable[5][0] = getResources().getDrawable(C0338R.drawable.pause_b);
            statesDrawable[5][1] = getResources().getDrawable(C0338R.drawable.pause_b_s);
            statesDrawable[6][0] = getResources().getDrawable(C0338R.drawable.file_b_load);
            statesDrawable[6][1] = getResources().getDrawable(C0338R.drawable.file_b_load_s);
            statesDrawable[7][0] = getResources().getDrawable(C0338R.drawable.file_b_cancel);
            statesDrawable[7][1] = getResources().getDrawable(C0338R.drawable.file_b_cancel_s);
            timePaint = new TextPaint(1);
        }
        timePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.TAG = MediaController.m71a().m178g();
        this.seekBar = new SeekBar(getContext());
        this.seekBar.setDelegate(this);
        this.progressView = new ProgressView();
    }

    private void didPressedButton() {
        if (this.buttonState == 0) {
            boolean a = MediaController.m71a().m158a(this.currentMessageObject);
            if (!this.currentMessageObject.isOut() && this.currentMessageObject.isContentUnread() && this.currentMessageObject.messageOwner.to_id.channel_id == 0) {
                MessagesController.getInstance().markMessageContentAsRead(this.currentMessageObject);
                this.currentMessageObject.setContentIsRead();
            }
            if (a) {
                this.buttonState = 1;
                invalidate();
            }
        } else if (this.buttonState == 1) {
            if (MediaController.m71a().m166b(this.currentMessageObject)) {
                this.buttonState = 0;
                invalidate();
            }
        } else if (this.buttonState == 2) {
            FileLoader.getInstance().loadFile(this.currentMessageObject.getDocument(), true, false);
            this.buttonState = 3;
            invalidate();
        } else if (this.buttonState == 3) {
            FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.getDocument());
            this.buttonState = 2;
            invalidate();
        }
    }

    public void downloadAudioIfNeed() {
        if (this.buttonState == 2) {
            FileLoader.getInstance().loadFile(this.currentMessageObject.getDocument(), true, false);
            this.buttonState = 3;
            invalidate();
        }
    }

    public final MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public int getObserverTag() {
        return this.TAG;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MediaController.m71a().m149a((FileDownloadProgressListener) this);
    }

    protected void onDraw(Canvas canvas) {
        if (this.currentMessageObject != null) {
            if (this.wasLayout) {
                setDrawableBounds(backgroundMediaDrawableIn, 0, 0, getMeasuredWidth(), getMeasuredHeight());
                backgroundMediaDrawableIn.draw(canvas);
                if (this.currentMessageObject != null) {
                    canvas.save();
                    if (this.buttonState == 0 || this.buttonState == 1) {
                        canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                        this.seekBar.draw(canvas);
                    } else {
                        canvas.translate((float) (this.seekBarX + AndroidUtilities.dp(12.0f)), (float) this.seekBarY);
                        this.progressView.draw(canvas);
                    }
                    canvas.restore();
                    int i = this.buttonState + 4;
                    timePaint.setColor(Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
                    Drawable drawable = statesDrawable[i][this.buttonPressed];
                    int dp = AndroidUtilities.dp(36.0f);
                    setDrawableBounds(drawable, ((dp - drawable.getIntrinsicWidth()) / 2) + this.buttonX, ((dp - drawable.getIntrinsicHeight()) / 2) + this.buttonY);
                    drawable.draw(canvas);
                    canvas.save();
                    canvas.translate((float) this.timeX, (float) AndroidUtilities.dp(18.0f));
                    this.timeLayout.draw(canvas);
                    canvas.restore();
                    return;
                }
                return;
            }
            requestLayout();
        }
    }

    public void onFailedDownload(String str) {
        updateButtonState();
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.currentMessageObject == null) {
            super.onLayout(z, i, i2, i3, i4);
            return;
        }
        this.seekBarX = AndroidUtilities.dp(54.0f);
        this.buttonX = AndroidUtilities.dp(10.0f);
        this.timeX = (getMeasuredWidth() - this.timeWidth) - AndroidUtilities.dp(16.0f);
        this.seekBar.setSize((getMeasuredWidth() - AndroidUtilities.dp(70.0f)) - this.timeWidth, AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
        this.progressView.width = (getMeasuredWidth() - AndroidUtilities.dp(94.0f)) - this.timeWidth;
        this.progressView.height = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
        this.seekBarY = AndroidUtilities.dp(13.0f);
        this.buttonY = AndroidUtilities.dp(10.0f);
        updateProgress();
        if (z || !this.wasLayout) {
            this.wasLayout = true;
        }
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(56.0f));
    }

    public void onProgressDownload(String str, float f) {
        this.progressView.setProgress(f);
        if (this.buttonState != 3) {
            updateButtonState();
        }
        invalidate();
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public void onSeekBarDrag(float f) {
        if (this.currentMessageObject != null) {
            this.currentMessageObject.audioProgress = f;
            MediaController.m71a().m159a(this.currentMessageObject, f);
        }
    }

    public void onSuccessDownload(String str) {
        updateButtonState();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        boolean onTouch = this.seekBar.onTouch(motionEvent.getAction(), motionEvent.getX() - ((float) this.seekBarX), motionEvent.getY() - ((float) this.seekBarY));
        if (onTouch) {
            if (motionEvent.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            invalidate();
            return onTouch;
        }
        int dp = AndroidUtilities.dp(36.0f);
        if (motionEvent.getAction() == 0) {
            if (x >= ((float) this.buttonX) && x <= ((float) (this.buttonX + dp)) && y >= ((float) this.buttonY) && y <= ((float) (this.buttonY + dp))) {
                this.buttonPressed = 1;
                invalidate();
                onTouch = true;
            }
        } else if (this.buttonPressed == 1) {
            if (motionEvent.getAction() == 1) {
                this.buttonPressed = 0;
                playSoundEffect(0);
                didPressedButton();
                invalidate();
            } else if (motionEvent.getAction() == 3) {
                this.buttonPressed = 0;
                invalidate();
            } else if (motionEvent.getAction() == 2 && (x < ((float) this.buttonX) || x > ((float) (this.buttonX + dp)) || y < ((float) this.buttonY) || y > ((float) (this.buttonY + dp)))) {
                this.buttonPressed = 0;
                invalidate();
            }
        }
        return !onTouch ? super.onTouchEvent(motionEvent) : onTouch;
    }

    public void setMessageObject(MessageObject messageObject) {
        if (this.currentMessageObject != messageObject) {
            this.seekBar.setColors(Theme.MSG_IN_AUDIO_SEEKBAR_COLOR, Theme.MSG_IN_VOICE_SEEKBAR_FILL_COLOR, Theme.MSG_IN_VOICE_SEEKBAR_SELECTED_COLOR);
            this.progressView.setProgressColors(-2497813, -7944712);
            this.currentMessageObject = messageObject;
            this.wasLayout = false;
            requestLayout();
        }
        updateButtonState();
    }

    public void updateButtonState() {
        String fileName = this.currentMessageObject.getFileName();
        if (FileLoader.getPathToMessage(this.currentMessageObject.messageOwner).exists()) {
            MediaController.m71a().m149a((FileDownloadProgressListener) this);
            boolean d = MediaController.m71a().m172d(this.currentMessageObject);
            if (!d || (d && MediaController.m71a().m191s())) {
                this.buttonState = 0;
            } else {
                this.buttonState = 1;
            }
            this.progressView.setProgress(0.0f);
        } else {
            MediaController.m71a().m151a(fileName, (FileDownloadProgressListener) this);
            if (FileLoader.getInstance().isLoadingFile(fileName)) {
                this.buttonState = 3;
                Float fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
                if (fileProgress != null) {
                    this.progressView.setProgress(fileProgress.floatValue());
                } else {
                    this.progressView.setProgress(0.0f);
                }
            } else {
                this.buttonState = 2;
                this.progressView.setProgress(0.0f);
            }
        }
        updateProgress();
    }

    public void updateProgress() {
        if (this.currentMessageObject != null) {
            int i;
            if (!this.seekBar.isDragging()) {
                this.seekBar.setProgress(this.currentMessageObject.audioProgress);
            }
            if (MediaController.m71a().m172d(this.currentMessageObject)) {
                i = this.currentMessageObject.audioProgressSec;
            } else {
                for (int i2 = 0; i2 < this.currentMessageObject.getDocument().attributes.size(); i2++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) this.currentMessageObject.getDocument().attributes.get(i2);
                    if (documentAttribute instanceof TL_documentAttributeAudio) {
                        i = documentAttribute.duration;
                        break;
                    }
                }
                i = 0;
            }
            CharSequence format = String.format("%02d:%02d", new Object[]{Integer.valueOf(i / 60), Integer.valueOf(i % 60)});
            if (this.lastTimeString == null || !(this.lastTimeString == null || this.lastTimeString.equals(format))) {
                this.timeWidth = (int) Math.ceil((double) timePaint.measureText(format));
                this.timeLayout = new StaticLayout(format, timePaint, this.timeWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            }
            invalidate();
        }
    }
}
