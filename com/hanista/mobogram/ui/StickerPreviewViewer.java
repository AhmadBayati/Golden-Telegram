package com.hanista.mobogram.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.ui.Cells.ContextLinkCell;
import com.hanista.mobogram.ui.Cells.StickerCell;
import com.hanista.mobogram.ui.Cells.StickerEmojiCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;

public class StickerPreviewViewer {
    private static volatile StickerPreviewViewer Instance;
    private ColorDrawable backgroundDrawable;
    private ImageReceiver centerImage;
    private FrameLayoutDrawer containerView;
    private Document currentSticker;
    private View currentStickerPreviewCell;
    private boolean isVisible;
    private int keyboardHeight;
    private long lastUpdateTime;
    private Runnable openStickerPreviewRunnable;
    private Activity parentActivity;
    private float showProgress;
    private int startX;
    private int startY;
    private LayoutParams windowLayoutParams;
    private FrameLayout windowView;

    /* renamed from: com.hanista.mobogram.ui.StickerPreviewViewer.1 */
    class C19131 implements Runnable {
        final /* synthetic */ View val$listView;
        final /* synthetic */ Object val$listener;

        C19131(View view, Object obj) {
            this.val$listView = view;
            this.val$listener = obj;
        }

        public void run() {
            if (this.val$listView instanceof AbsListView) {
                ((AbsListView) this.val$listView).setOnItemClickListener((OnItemClickListener) this.val$listener);
            } else if (this.val$listView instanceof RecyclerListView) {
                ((RecyclerListView) this.val$listView).setOnItemClickListener((RecyclerListView.OnItemClickListener) this.val$listener);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.StickerPreviewViewer.2 */
    class C19142 implements Runnable {
        final /* synthetic */ int val$height;
        final /* synthetic */ View val$listView;

        C19142(View view, int i) {
            this.val$listView = view;
            this.val$height = i;
        }

        public void run() {
            if (StickerPreviewViewer.this.openStickerPreviewRunnable != null) {
                if (this.val$listView instanceof AbsListView) {
                    ((AbsListView) this.val$listView).setOnItemClickListener(null);
                    ((AbsListView) this.val$listView).requestDisallowInterceptTouchEvent(true);
                } else if (this.val$listView instanceof RecyclerListView) {
                    ((RecyclerListView) this.val$listView).setOnItemClickListener(null);
                    ((RecyclerListView) this.val$listView).requestDisallowInterceptTouchEvent(true);
                }
                StickerPreviewViewer.this.openStickerPreviewRunnable = null;
                StickerPreviewViewer.this.setParentActivity((Activity) this.val$listView.getContext());
                StickerPreviewViewer.this.setKeyboardHeight(this.val$height);
                if (StickerPreviewViewer.this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                    StickerPreviewViewer.this.open(((StickerEmojiCell) StickerPreviewViewer.this.currentStickerPreviewCell).getSticker());
                    ((StickerEmojiCell) StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                } else if (StickerPreviewViewer.this.currentStickerPreviewCell instanceof StickerCell) {
                    StickerPreviewViewer.this.open(((StickerCell) StickerPreviewViewer.this.currentStickerPreviewCell).getSticker());
                    ((StickerCell) StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                } else if (StickerPreviewViewer.this.currentStickerPreviewCell instanceof ContextLinkCell) {
                    StickerPreviewViewer.this.open(((ContextLinkCell) StickerPreviewViewer.this.currentStickerPreviewCell).getDocument());
                    ((ContextLinkCell) StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.StickerPreviewViewer.3 */
    class C19153 implements OnTouchListener {
        C19153() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 6 || motionEvent.getAction() == 3) {
                StickerPreviewViewer.this.close();
            }
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.StickerPreviewViewer.4 */
    class C19164 implements Runnable {
        C19164() {
        }

        public void run() {
            StickerPreviewViewer.this.centerImage.setImageBitmap((Bitmap) null);
        }
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        protected void onDraw(Canvas canvas) {
            StickerPreviewViewer.getInstance().onDraw(canvas);
        }
    }

    static {
        Instance = null;
    }

    public StickerPreviewViewer() {
        this.backgroundDrawable = new ColorDrawable(1895825408);
        this.centerImage = new ImageReceiver();
        this.isVisible = false;
        this.keyboardHeight = AndroidUtilities.dp(200.0f);
        this.currentSticker = null;
    }

    public static StickerPreviewViewer getInstance() {
        StickerPreviewViewer stickerPreviewViewer = Instance;
        if (stickerPreviewViewer == null) {
            synchronized (PhotoViewer.class) {
                stickerPreviewViewer = Instance;
                if (stickerPreviewViewer == null) {
                    stickerPreviewViewer = new StickerPreviewViewer();
                    Instance = stickerPreviewViewer;
                }
            }
        }
        return stickerPreviewViewer;
    }

    private void onDraw(Canvas canvas) {
        if (this.containerView != null && this.backgroundDrawable != null) {
            this.backgroundDrawable.setAlpha((int) (BitmapDescriptorFactory.HUE_CYAN * this.showProgress));
            this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
            this.backgroundDrawable.draw(canvas);
            canvas.save();
            int min = (int) (((float) Math.min(this.containerView.getWidth(), this.containerView.getHeight())) / 1.8f);
            canvas.translate((float) (this.containerView.getWidth() / 2), (float) Math.max((min / 2) + AndroidUtilities.statusBarHeight, (this.containerView.getHeight() - this.keyboardHeight) / 2));
            if (this.centerImage.getBitmap() != null) {
                min = (int) (((float) min) * ((this.showProgress * DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD) / DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD));
                this.centerImage.setAlpha(this.showProgress);
                this.centerImage.setImageCoords((-min) / 2, (-min) / 2, min, min);
                this.centerImage.draw(canvas);
            }
            canvas.restore();
            if (this.showProgress != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                this.lastUpdateTime = currentTimeMillis;
                this.showProgress += ((float) j) / 150.0f;
                this.containerView.invalidate();
                if (this.showProgress > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    this.showProgress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                }
            }
        }
    }

    public void close() {
        if (this.parentActivity != null) {
            this.showProgress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            this.currentSticker = null;
            this.isVisible = false;
            AndroidUtilities.unlockOrientation(this.parentActivity);
            AndroidUtilities.runOnUIThread(new C19164());
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public void destroy() {
        this.isVisible = false;
        this.currentSticker = null;
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

    public boolean isVisible() {
        return this.isVisible;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent, View view, int i) {
        if (motionEvent.getAction() != 0) {
            return false;
        }
        int childCount;
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (view instanceof AbsListView) {
            childCount = ((AbsListView) view).getChildCount();
        } else if (view instanceof RecyclerListView) {
            childCount = ((RecyclerListView) view).getChildCount();
        } else {
            boolean z = false;
        }
        int i2 = 0;
        while (i2 < childCount) {
            View view2 = null;
            if (view instanceof AbsListView) {
                view2 = ((AbsListView) view).getChildAt(i2);
            } else if (view instanceof RecyclerListView) {
                view2 = ((RecyclerListView) view).getChildAt(i2);
            }
            if (view2 == null) {
                return false;
            }
            int top = view2.getTop();
            int bottom = view2.getBottom();
            int left = view2.getLeft();
            int right = view2.getRight();
            if (top > y || bottom < y || left > x || right < x) {
                i2++;
            } else {
                boolean showingBitmap;
                if (view2 instanceof StickerEmojiCell) {
                    showingBitmap = ((StickerEmojiCell) view2).showingBitmap();
                } else if (view2 instanceof StickerCell) {
                    showingBitmap = ((StickerCell) view2).showingBitmap();
                } else if (view2 instanceof ContextLinkCell) {
                    ContextLinkCell contextLinkCell = (ContextLinkCell) view2;
                    showingBitmap = contextLinkCell.isSticker() && contextLinkCell.showingBitmap();
                } else {
                    showingBitmap = false;
                }
                if (!showingBitmap) {
                    return false;
                }
                this.startX = x;
                this.startY = y;
                this.currentStickerPreviewCell = view2;
                this.openStickerPreviewRunnable = new C19142(view, i);
                AndroidUtilities.runOnUIThread(this.openStickerPreviewRunnable, 200);
                return true;
            }
        }
        return false;
    }

    public boolean onTouch(MotionEvent motionEvent, View view, int i, Object obj) {
        if (this.openStickerPreviewRunnable != null || isVisible()) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3 || motionEvent.getAction() == 6) {
                AndroidUtilities.runOnUIThread(new C19131(view, obj), 150);
                if (this.openStickerPreviewRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                    this.openStickerPreviewRunnable = null;
                } else if (isVisible()) {
                    close();
                    if (this.currentStickerPreviewCell != null) {
                        if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                            ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
                        } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                            ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
                        } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                            ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
                        }
                        this.currentStickerPreviewCell = null;
                    }
                }
            } else if (motionEvent.getAction() != 0) {
                if (isVisible()) {
                    if (motionEvent.getAction() == 2) {
                        int x = (int) motionEvent.getX();
                        int y = (int) motionEvent.getY();
                        int childCount = view instanceof AbsListView ? ((AbsListView) view).getChildCount() : view instanceof RecyclerListView ? ((RecyclerListView) view).getChildCount() : 0;
                        int i2 = 0;
                        while (i2 < childCount) {
                            View view2 = null;
                            if (view instanceof AbsListView) {
                                view2 = ((AbsListView) view).getChildAt(i2);
                            } else if (view instanceof RecyclerListView) {
                                view2 = ((RecyclerListView) view).getChildAt(i2);
                            }
                            if (view2 == null) {
                                return false;
                            }
                            int top = view2.getTop();
                            int bottom = view2.getBottom();
                            int left = view2.getLeft();
                            int right = view2.getRight();
                            if (top > y || bottom < y || left > x || right < x) {
                                i2++;
                            } else {
                                boolean z = false;
                                if (view2 instanceof StickerEmojiCell) {
                                    z = true;
                                } else if (view2 instanceof StickerCell) {
                                    z = true;
                                } else if (view2 instanceof ContextLinkCell) {
                                    z = ((ContextLinkCell) view2).isSticker();
                                }
                                if (z && view2 != this.currentStickerPreviewCell) {
                                    if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                                        ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
                                    } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                                        ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
                                    } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                                        ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
                                    }
                                    this.currentStickerPreviewCell = view2;
                                    setKeyboardHeight(i);
                                    if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                                        open(((StickerEmojiCell) this.currentStickerPreviewCell).getSticker());
                                        ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(true);
                                    } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                                        open(((StickerCell) this.currentStickerPreviewCell).getSticker());
                                        ((StickerCell) this.currentStickerPreviewCell).setScaled(true);
                                    } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                                        open(((ContextLinkCell) this.currentStickerPreviewCell).getDocument());
                                        ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(true);
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                    return true;
                } else if (this.openStickerPreviewRunnable != null) {
                    if (motionEvent.getAction() != 2) {
                        AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                        this.openStickerPreviewRunnable = null;
                    } else if (Math.hypot((double) (((float) this.startX) - motionEvent.getX()), (double) (((float) this.startY) - motionEvent.getY())) > ((double) AndroidUtilities.dp(10.0f))) {
                        AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                        this.openStickerPreviewRunnable = null;
                    }
                }
            }
        }
        return false;
    }

    public void open(Document document) {
        if (this.parentActivity != null && document != null) {
            this.centerImage.setImage((TLObject) document, null, document.thumb.location, null, "webp", true);
            this.currentSticker = document;
            this.containerView.invalidate();
            if (!this.isVisible) {
                AndroidUtilities.lockOrientation(this.parentActivity);
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
                this.isVisible = true;
                this.showProgress = 0.0f;
                this.lastUpdateTime = System.currentTimeMillis();
            }
        }
    }

    public void reset() {
        if (this.openStickerPreviewRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
            this.openStickerPreviewRunnable = null;
        }
        if (this.currentStickerPreviewCell != null) {
            if (this.currentStickerPreviewCell instanceof StickerEmojiCell) {
                ((StickerEmojiCell) this.currentStickerPreviewCell).setScaled(false);
            } else if (this.currentStickerPreviewCell instanceof StickerCell) {
                ((StickerCell) this.currentStickerPreviewCell).setScaled(false);
            } else if (this.currentStickerPreviewCell instanceof ContextLinkCell) {
                ((ContextLinkCell) this.currentStickerPreviewCell).setScaled(false);
            }
            this.currentStickerPreviewCell = null;
        }
    }

    public void setKeyboardHeight(int i) {
        this.keyboardHeight = i;
    }

    public void setParentActivity(Activity activity) {
        if (this.parentActivity != activity) {
            this.parentActivity = activity;
            this.windowView = new FrameLayout(activity);
            this.windowView.setFocusable(true);
            this.windowView.setFocusableInTouchMode(true);
            if (VERSION.SDK_INT >= 23) {
                this.windowView.setFitsSystemWindows(true);
            }
            this.containerView = new FrameLayoutDrawer(activity);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            this.containerView.setOnTouchListener(new C19153());
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.gravity = 48;
            this.windowLayoutParams.type = 99;
            if (VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -2147483640;
            } else {
                this.windowLayoutParams.flags = 8;
            }
            this.centerImage.setAspectFit(true);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setParentView(this.containerView);
        }
    }
}
