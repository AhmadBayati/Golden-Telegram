package com.hanista.mobogram.ui.Components;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.ActionBar.Theme;
import java.util.ArrayList;
import java.util.Iterator;

@TargetApi(10)
public class VideoTimelineView extends View {
    private static final Object sync;
    private AsyncTask<Integer, Integer, Bitmap> currentTask;
    private VideoTimelineViewDelegate delegate;
    private int frameHeight;
    private long frameTimeOffset;
    private int frameWidth;
    private ArrayList<Bitmap> frames;
    private int framesToLoad;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private Paint paint;
    private Paint paint2;
    private float pressDx;
    private boolean pressedLeft;
    private boolean pressedRight;
    private float progressLeft;
    private float progressRight;
    private long videoLength;

    /* renamed from: com.hanista.mobogram.ui.Components.VideoTimelineView.1 */
    class C14981 extends AsyncTask<Integer, Integer, Bitmap> {
        private int frameNum;

        C14981() {
            this.frameNum = 0;
        }

        protected Bitmap doInBackground(Integer... numArr) {
            Throwable th;
            Bitmap bitmap = null;
            this.frameNum = numArr[0].intValue();
            if (isCancelled()) {
                return null;
            }
            try {
                Bitmap frameAtTime = VideoTimelineView.this.mediaMetadataRetriever.getFrameAtTime((VideoTimelineView.this.frameTimeOffset * ((long) this.frameNum)) * 1000);
                try {
                    if (isCancelled()) {
                        return null;
                    }
                    if (frameAtTime == null) {
                        return frameAtTime;
                    }
                    bitmap = Bitmap.createBitmap(VideoTimelineView.this.frameWidth, VideoTimelineView.this.frameHeight, frameAtTime.getConfig());
                    Canvas canvas = new Canvas(bitmap);
                    float access$200 = ((float) VideoTimelineView.this.frameWidth) / ((float) frameAtTime.getWidth());
                    float access$300 = ((float) VideoTimelineView.this.frameHeight) / ((float) frameAtTime.getHeight());
                    if (access$200 <= access$300) {
                        access$200 = access$300;
                    }
                    int width = (int) (((float) frameAtTime.getWidth()) * access$200);
                    int height = (int) (access$200 * ((float) frameAtTime.getHeight()));
                    canvas.drawBitmap(frameAtTime, new Rect(0, 0, frameAtTime.getWidth(), frameAtTime.getHeight()), new Rect((VideoTimelineView.this.frameWidth - width) / 2, (VideoTimelineView.this.frameHeight - height) / 2, width, height), null);
                    frameAtTime.recycle();
                    return bitmap;
                } catch (Throwable e) {
                    Throwable th2 = e;
                    bitmap = frameAtTime;
                    th = th2;
                    FileLog.m18e("tmessages", th);
                    return bitmap;
                }
            } catch (Exception e2) {
                th = e2;
                FileLog.m18e("tmessages", th);
                return bitmap;
            }
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (!isCancelled()) {
                VideoTimelineView.this.frames.add(bitmap);
                VideoTimelineView.this.invalidate();
                if (this.frameNum < VideoTimelineView.this.framesToLoad) {
                    VideoTimelineView.this.reloadFrames(this.frameNum + 1);
                }
            }
        }
    }

    public interface VideoTimelineViewDelegate {
        void onLeftProgressChanged(float f);

        void onRifhtProgressChanged(float f);
    }

    static {
        sync = new Object();
    }

    public VideoTimelineView(Context context) {
        super(context);
        this.progressRight = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.frames = new ArrayList();
        this.paint = new Paint(1);
        this.paint.setColor(-1);
        this.paint2 = new Paint();
        this.paint2.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
    }

    private void reloadFrames(int i) {
        if (this.mediaMetadataRetriever != null) {
            if (i == 0) {
                this.frameHeight = AndroidUtilities.dp(40.0f);
                this.framesToLoad = (getMeasuredWidth() - AndroidUtilities.dp(16.0f)) / this.frameHeight;
                this.frameWidth = (int) Math.ceil((double) (((float) (getMeasuredWidth() - AndroidUtilities.dp(16.0f))) / ((float) this.framesToLoad)));
                this.frameTimeOffset = this.videoLength / ((long) this.framesToLoad);
            }
            this.currentTask = new C14981();
            this.currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Integer[]{Integer.valueOf(i), null, null});
        }
    }

    public void clearFrames() {
        Iterator it = this.frames.iterator();
        while (it.hasNext()) {
            Bitmap bitmap = (Bitmap) it.next();
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.frames.clear();
        if (this.currentTask != null) {
            this.currentTask.cancel(true);
            this.currentTask = null;
        }
        invalidate();
    }

    public void destroy() {
        synchronized (sync) {
            try {
                if (this.mediaMetadataRetriever != null) {
                    this.mediaMetadataRetriever.release();
                    this.mediaMetadataRetriever = null;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        Iterator it = this.frames.iterator();
        while (it.hasNext()) {
            Bitmap bitmap = (Bitmap) it.next();
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.frames.clear();
        if (this.currentTask != null) {
            this.currentTask.cancel(true);
            this.currentTask = null;
        }
    }

    public float getLeftProgress() {
        return this.progressLeft;
    }

    public float getRightProgress() {
        return this.progressRight;
    }

    protected void onDraw(Canvas canvas) {
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(36.0f);
        int dp = ((int) (((float) measuredWidth) * this.progressLeft)) + AndroidUtilities.dp(16.0f);
        int dp2 = ((int) (((float) measuredWidth) * this.progressRight)) + AndroidUtilities.dp(16.0f);
        canvas.save();
        canvas.clipRect(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(20.0f) + measuredWidth, AndroidUtilities.dp(44.0f));
        if (this.frames.isEmpty() && this.currentTask == null) {
            reloadFrames(0);
        } else {
            int i = 0;
            for (int i2 = 0; i2 < this.frames.size(); i2++) {
                Bitmap bitmap = (Bitmap) this.frames.get(i2);
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, (float) (AndroidUtilities.dp(16.0f) + (this.frameWidth * i)), (float) AndroidUtilities.dp(2.0f), null);
                }
                i++;
            }
        }
        canvas.drawRect((float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(2.0f), (float) dp, (float) AndroidUtilities.dp(42.0f), this.paint2);
        canvas.drawRect((float) (AndroidUtilities.dp(4.0f) + dp2), (float) AndroidUtilities.dp(2.0f), (float) ((AndroidUtilities.dp(16.0f) + measuredWidth) + AndroidUtilities.dp(4.0f)), (float) AndroidUtilities.dp(42.0f), this.paint2);
        canvas.drawRect((float) dp, 0.0f, (float) (AndroidUtilities.dp(2.0f) + dp), (float) AndroidUtilities.dp(44.0f), this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + dp2), 0.0f, (float) (AndroidUtilities.dp(4.0f) + dp2), (float) AndroidUtilities.dp(44.0f), this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + dp), 0.0f, (float) (AndroidUtilities.dp(4.0f) + dp2), (float) AndroidUtilities.dp(2.0f), this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + dp), (float) AndroidUtilities.dp(42.0f), (float) (AndroidUtilities.dp(4.0f) + dp2), (float) AndroidUtilities.dp(44.0f), this.paint);
        canvas.restore();
        canvas.drawCircle((float) dp, (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(7.0f), this.paint);
        canvas.drawCircle((float) (AndroidUtilities.dp(4.0f) + dp2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(7.0f), this.paint);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent == null) {
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(32.0f);
        int dp = AndroidUtilities.dp(16.0f) + ((int) (((float) measuredWidth) * this.progressLeft));
        int dp2 = ((int) (((float) measuredWidth) * this.progressRight)) + AndroidUtilities.dp(16.0f);
        if (motionEvent.getAction() == 0) {
            measuredWidth = AndroidUtilities.dp(12.0f);
            if (((float) (dp - measuredWidth)) <= x && x <= ((float) (dp + measuredWidth)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                this.pressedLeft = true;
                this.pressDx = (float) ((int) (x - ((float) dp)));
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            } else if (((float) (dp2 - measuredWidth)) <= x && x <= ((float) (dp2 + measuredWidth)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                this.pressedRight = true;
                this.pressDx = (float) ((int) (x - ((float) dp2)));
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (this.pressedLeft) {
                this.pressedLeft = false;
                return true;
            } else if (this.pressedRight) {
                this.pressedRight = false;
                return true;
            }
        } else if (motionEvent.getAction() == 2) {
            if (this.pressedLeft) {
                dp = (int) (x - this.pressDx);
                if (dp < AndroidUtilities.dp(16.0f)) {
                    dp2 = AndroidUtilities.dp(16.0f);
                } else if (dp <= dp2) {
                    dp2 = dp;
                }
                this.progressLeft = ((float) (dp2 - AndroidUtilities.dp(16.0f))) / ((float) measuredWidth);
                if (this.delegate != null) {
                    this.delegate.onLeftProgressChanged(this.progressLeft);
                }
                invalidate();
                return true;
            } else if (this.pressedRight) {
                dp2 = (int) (x - this.pressDx);
                if (dp2 < dp) {
                    dp2 = dp;
                } else if (dp2 > AndroidUtilities.dp(16.0f) + measuredWidth) {
                    dp2 = AndroidUtilities.dp(16.0f) + measuredWidth;
                }
                this.progressRight = ((float) (dp2 - AndroidUtilities.dp(16.0f))) / ((float) measuredWidth);
                if (this.delegate != null) {
                    this.delegate.onRifhtProgressChanged(this.progressRight);
                }
                invalidate();
                return true;
            }
        }
        return false;
    }

    public void setDelegate(VideoTimelineViewDelegate videoTimelineViewDelegate) {
        this.delegate = videoTimelineViewDelegate;
    }

    public void setVideoPath(String str) {
        this.mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            this.mediaMetadataRetriever.setDataSource(str);
            this.videoLength = Long.parseLong(this.mediaMetadataRetriever.extractMetadata(9));
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }
}
