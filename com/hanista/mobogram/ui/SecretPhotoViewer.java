package com.hanista.mobogram.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.PointerIconCompat;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.ui.ActionBar.Theme;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class SecretPhotoViewer implements NotificationCenterDelegate {
    private static volatile SecretPhotoViewer Instance;
    private ImageReceiver centerImage;
    private FrameLayoutDrawer containerView;
    private MessageObject currentMessageObject;
    private boolean isVisible;
    private Activity parentActivity;
    private SecretDeleteTimer secretDeleteTimer;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;

    /* renamed from: com.hanista.mobogram.ui.SecretPhotoViewer.1 */
    class C18641 implements OnTouchListener {
        C18641() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 6 || motionEvent.getAction() == 3) {
                SecretPhotoViewer.this.closePhoto();
            }
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.SecretPhotoViewer.2 */
    class C18652 implements Runnable {
        C18652() {
        }

        public void run() {
            SecretPhotoViewer.this.centerImage.setImageBitmap((Bitmap) null);
        }
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        protected void onDraw(Canvas canvas) {
            SecretPhotoViewer.getInstance().onDraw(canvas);
        }
    }

    private class SecretDeleteTimer extends FrameLayout {
        private String currentInfoString;
        private Paint deleteProgressPaint;
        private RectF deleteProgressRect;
        private Drawable drawable;
        private StaticLayout infoLayout;
        private TextPaint infoPaint;
        private int infoWidth;

        public SecretDeleteTimer(Context context) {
            super(context);
            this.infoPaint = null;
            this.infoLayout = null;
            this.deleteProgressRect = new RectF();
            this.drawable = null;
            setWillNotDraw(false);
            this.infoPaint = new TextPaint(1);
            this.infoPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            this.infoPaint.setColor(-1);
            this.deleteProgressPaint = new Paint(1);
            this.deleteProgressPaint.setColor(-1644826);
            this.drawable = getResources().getDrawable(C0338R.drawable.circle1);
        }

        private void updateSecretTimeText() {
            if (SecretPhotoViewer.this.currentMessageObject != null) {
                String secretTimeString = SecretPhotoViewer.this.currentMessageObject.getSecretTimeString();
                if (secretTimeString == null) {
                    return;
                }
                if (this.currentInfoString == null || !this.currentInfoString.equals(secretTimeString)) {
                    this.currentInfoString = secretTimeString;
                    this.infoWidth = (int) Math.ceil((double) this.infoPaint.measureText(this.currentInfoString));
                    this.infoLayout = new StaticLayout(TextUtils.ellipsize(this.currentInfoString, this.infoPaint, (float) this.infoWidth, TruncateAt.END), this.infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                    invalidate();
                }
            }
        }

        protected void onDraw(Canvas canvas) {
            if (SecretPhotoViewer.this.currentMessageObject != null && SecretPhotoViewer.this.currentMessageObject.messageOwner.destroyTime != 0) {
                if (this.drawable != null) {
                    this.drawable.setBounds(getMeasuredWidth() - AndroidUtilities.dp(32.0f), 0, getMeasuredWidth(), AndroidUtilities.dp(32.0f));
                    this.drawable.draw(canvas);
                }
                float max = ((float) Math.max(0, (((long) SecretPhotoViewer.this.currentMessageObject.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance().getTimeDifference() * PointerIconCompat.TYPE_DEFAULT))))) / (((float) SecretPhotoViewer.this.currentMessageObject.messageOwner.ttl) * 1000.0f);
                canvas.drawArc(this.deleteProgressRect, -90.0f, -360.0f * max, true, this.deleteProgressPaint);
                if (max != 0.0f) {
                    int dp = AndroidUtilities.dp(2.0f);
                    invalidate(((int) this.deleteProgressRect.left) - dp, ((int) this.deleteProgressRect.top) - dp, ((int) this.deleteProgressRect.right) + (dp * 2), (dp * 2) + ((int) this.deleteProgressRect.bottom));
                }
                updateSecretTimeText();
                if (this.infoLayout != null) {
                    canvas.save();
                    canvas.translate((float) ((getMeasuredWidth() - AndroidUtilities.dp(38.0f)) - this.infoWidth), (float) AndroidUtilities.dp(7.0f));
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            this.deleteProgressRect.set((float) (getMeasuredWidth() - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)), (float) AndroidUtilities.dp(2.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
        }
    }

    static {
        Instance = null;
    }

    public SecretPhotoViewer() {
        this.centerImage = new ImageReceiver();
        this.isVisible = false;
        this.currentMessageObject = null;
    }

    public static SecretPhotoViewer getInstance() {
        SecretPhotoViewer secretPhotoViewer = Instance;
        if (secretPhotoViewer == null) {
            synchronized (PhotoViewer.class) {
                secretPhotoViewer = Instance;
                if (secretPhotoViewer == null) {
                    secretPhotoViewer = new SecretPhotoViewer();
                    Instance = secretPhotoViewer;
                }
            }
        }
        return secretPhotoViewer;
    }

    private void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate((float) (this.containerView.getWidth() / 2), (float) (this.containerView.getHeight() / 2));
        Bitmap bitmap = this.centerImage.getBitmap();
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float width2 = ((float) this.containerView.getWidth()) / ((float) width);
            float height2 = ((float) this.containerView.getHeight()) / ((float) height);
            if (width2 <= height2) {
                height2 = width2;
            }
            int i = (int) (((float) width) * height2);
            int i2 = (int) (height2 * ((float) height));
            this.centerImage.setImageCoords((-i) / 2, (-i2) / 2, i, i2);
            this.centerImage.draw(canvas);
        }
        canvas.restore();
    }

    public void closePhoto() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        if (this.parentActivity != null) {
            this.currentMessageObject = null;
            this.isVisible = false;
            AndroidUtilities.unlockOrientation(this.parentActivity);
            AndroidUtilities.runOnUIThread(new C18652());
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public void destroyPhotoViewer() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        this.isVisible = false;
        this.currentMessageObject = null;
        if (this.parentActivity != null && this.windowView != null) {
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            Instance = null;
        }
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.messagesDeleted) {
            if (this.currentMessageObject != null && ((Integer) objArr[1]).intValue() == 0 && ((ArrayList) objArr[0]).contains(Integer.valueOf(this.currentMessageObject.getId()))) {
                closePhoto();
            }
        } else if (i == NotificationCenter.didCreatedNewDeleteTask && this.currentMessageObject != null && this.secretDeleteTimer != null) {
            SparseArray sparseArray = (SparseArray) objArr[0];
            for (int i2 = 0; i2 < sparseArray.size(); i2++) {
                int keyAt = sparseArray.keyAt(i2);
                Iterator it = ((ArrayList) sparseArray.get(keyAt)).iterator();
                while (it.hasNext()) {
                    if (this.currentMessageObject.getId() == ((Integer) it.next()).intValue()) {
                        this.currentMessageObject.messageOwner.destroyTime = keyAt;
                        this.secretDeleteTimer.invalidate();
                        return;
                    }
                }
            }
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void openPhoto(MessageObject messageObject) {
        if (this.parentActivity != null && messageObject != null && messageObject.messageOwner.media != null && messageObject.messageOwner.media.photo != null) {
            Drawable bitmapDrawable;
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didCreatedNewDeleteTask);
            TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
            int i = closestPhotoSizeWithSize.size;
            if (i == 0) {
                i = -1;
            }
            Drawable imageFromMemory = ImageLoader.getInstance().getImageFromMemory(closestPhotoSizeWithSize.location, null, null);
            if (imageFromMemory == null) {
                Options options;
                Bitmap decodeFile;
                File pathToAttach = FileLoader.getPathToAttach(closestPhotoSizeWithSize);
                if (VERSION.SDK_INT < 21) {
                    options = new Options();
                    options.inDither = true;
                    options.inPreferredConfig = Config.ARGB_8888;
                    options.inPurgeable = true;
                    options.inSampleSize = 1;
                    options.inMutable = true;
                } else {
                    options = null;
                }
                try {
                    decodeFile = BitmapFactory.decodeFile(pathToAttach.getAbsolutePath(), options);
                } catch (Throwable th) {
                    FileLog.m18e("tmessages", th);
                    decodeFile = null;
                }
                if (decodeFile != null) {
                    bitmapDrawable = new BitmapDrawable(decodeFile);
                    ImageLoader.getInstance().putImageToCache(bitmapDrawable, closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id);
                    if (bitmapDrawable == null) {
                        this.centerImage.setImageBitmap(bitmapDrawable);
                    } else {
                        this.centerImage.setImage(closestPhotoSizeWithSize.location, null, null, i, null, false);
                    }
                    this.currentMessageObject = messageObject;
                    AndroidUtilities.lockOrientation(this.parentActivity);
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                    }
                    ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
                    this.secretDeleteTimer.invalidate();
                    this.isVisible = true;
                }
            }
            bitmapDrawable = imageFromMemory;
            if (bitmapDrawable == null) {
                this.centerImage.setImage(closestPhotoSizeWithSize.location, null, null, i, null, false);
            } else {
                this.centerImage.setImageBitmap(bitmapDrawable);
            }
            this.currentMessageObject = messageObject;
            AndroidUtilities.lockOrientation(this.parentActivity);
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                }
            } catch (Throwable th2) {
                FileLog.m18e("tmessages", th2);
            }
            ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
            this.secretDeleteTimer.invalidate();
            this.isVisible = true;
        }
    }

    public void setParentActivity(Activity activity) {
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.windowView = new FrameLayout(activity);
            this.windowView.setBackgroundColor(Theme.MSG_TEXT_COLOR);
            this.windowView.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            if (VERSION.SDK_INT >= 23) {
                this.windowView.setFitsSystemWindows(true);
            }
            this.containerView = new FrameLayoutDrawer(activity);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.containerView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.gravity = 51;
            this.containerView.setLayoutParams(layoutParams);
            this.containerView.setOnTouchListener(new C18641());
            this.secretDeleteTimer = new SecretDeleteTimer(activity);
            this.containerView.addView(this.secretDeleteTimer);
            layoutParams = (FrameLayout.LayoutParams) this.secretDeleteTimer.getLayoutParams();
            layoutParams.gravity = 53;
            layoutParams.width = AndroidUtilities.dp(100.0f);
            layoutParams.height = AndroidUtilities.dp(32.0f);
            layoutParams.rightMargin = AndroidUtilities.dp(19.0f);
            layoutParams.topMargin = AndroidUtilities.dp(19.0f);
            this.secretDeleteTimer.setLayoutParams(layoutParams);
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 48;
            this.windowLayoutParams.type = 99;
            this.windowLayoutParams.flags = 8;
            this.centerImage.setParentView(this.containerView);
        }
    }
}
