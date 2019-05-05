package com.hanista.mobogram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.ui.Components.Point;
import com.hanista.mobogram.ui.Components.Rect;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.UUID;

public class EntityView extends FrameLayout {
    private boolean announcedDragging;
    private boolean announcedSelection;
    private EntityViewDelegate delegate;
    private GestureDetector gestureDetector;
    private boolean hasPanned;
    private boolean hasReleased;
    private boolean hasTransformed;
    private int offsetX;
    private int offsetY;
    protected Point position;
    private float previousLocationX;
    private float previousLocationY;
    private boolean recognizedLongPress;
    protected SelectionView selectionView;
    private UUID uuid;

    /* renamed from: com.hanista.mobogram.ui.Components.Paint.Views.EntityView.1 */
    class C13861 extends SimpleOnGestureListener {
        C13861() {
        }

        public void onLongPress(MotionEvent motionEvent) {
            if (!EntityView.this.hasPanned && !EntityView.this.hasTransformed && !EntityView.this.hasReleased) {
                EntityView.this.recognizedLongPress = true;
                if (EntityView.this.delegate != null) {
                    EntityView.this.performHapticFeedback(0);
                    EntityView.this.delegate.onEntityLongClicked(EntityView.this);
                }
            }
        }
    }

    public interface EntityViewDelegate {
        boolean allowInteraction(EntityView entityView);

        void onBeganEntityDragging(EntityView entityView);

        boolean onEntityLongClicked(EntityView entityView);

        boolean onEntitySelected(EntityView entityView);

        void onFinishedEntityDragging(EntityView entityView);
    }

    public class SelectionView extends FrameLayout {
        public static final int SELECTION_LEFT_HANDLE = 1;
        public static final int SELECTION_RIGHT_HANDLE = 2;
        public static final int SELECTION_WHOLE_HANDLE = 3;
        private int currentHandle;
        protected Paint dotPaint;
        protected Paint dotStrokePaint;
        protected Paint paint;

        public SelectionView(Context context) {
            super(context);
            this.paint = new Paint(SELECTION_LEFT_HANDLE);
            this.dotPaint = new Paint(SELECTION_LEFT_HANDLE);
            this.dotStrokePaint = new Paint(SELECTION_LEFT_HANDLE);
            setWillNotDraw(false);
            this.paint.setColor(-1);
            this.dotPaint.setColor(-12793105);
            this.dotStrokePaint.setColor(-1);
            this.dotStrokePaint.setStyle(Style.STROKE);
            this.dotStrokePaint.setStrokeWidth((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean z = false;
            switch (motionEvent.getActionMasked()) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                case VideoPlayer.STATE_ENDED /*5*/:
                    int pointInsideHandle = pointInsideHandle(motionEvent.getX(), motionEvent.getY());
                    if (pointInsideHandle != 0) {
                        this.currentHandle = pointInsideHandle;
                        EntityView.this.previousLocationX = motionEvent.getRawX();
                        EntityView.this.previousLocationY = motionEvent.getRawY();
                        EntityView.this.hasReleased = false;
                        z = true;
                        break;
                    }
                    break;
                case SELECTION_LEFT_HANDLE /*1*/:
                case SELECTION_WHOLE_HANDLE /*3*/:
                case Method.TRACE /*6*/:
                    EntityView.this.onTouchUp();
                    this.currentHandle = 0;
                    z = true;
                    break;
                case SELECTION_RIGHT_HANDLE /*2*/:
                    if (this.currentHandle != SELECTION_WHOLE_HANDLE) {
                        if (this.currentHandle != 0) {
                            EntityView.this.hasTransformed = true;
                            Point point = new Point(motionEvent.getRawX() - EntityView.this.previousLocationX, motionEvent.getRawY() - EntityView.this.previousLocationY);
                            float toRadians = (float) Math.toRadians((double) getRotation());
                            float sin = (float) ((Math.sin((double) toRadians) * ((double) point.f2684y)) + (((double) point.f2683x) * Math.cos((double) toRadians)));
                            if (this.currentHandle == SELECTION_LEFT_HANDLE) {
                                sin *= Face.UNCOMPUTED_PROBABILITY;
                            }
                            EntityView.this.scale(((sin * 2.0f) / ((float) getWidth())) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            toRadians = (float) (getLeft() + (getWidth() / SELECTION_RIGHT_HANDLE));
                            float top = (float) (getTop() + (getHeight() / SELECTION_RIGHT_HANDLE));
                            float rawX = motionEvent.getRawX() - ((float) ((View) getParent()).getLeft());
                            float rawY = (motionEvent.getRawY() - ((float) ((View) getParent()).getTop())) - ((float) AndroidUtilities.statusBarHeight);
                            sin = 0.0f;
                            if (this.currentHandle == SELECTION_LEFT_HANDLE) {
                                sin = (float) Math.atan2((double) (top - rawY), (double) (toRadians - rawX));
                            } else if (this.currentHandle == SELECTION_RIGHT_HANDLE) {
                                sin = (float) Math.atan2((double) (rawY - top), (double) (rawX - toRadians));
                            }
                            EntityView.this.rotate((float) Math.toDegrees((double) sin));
                            EntityView.this.previousLocationX = motionEvent.getRawX();
                            EntityView.this.previousLocationY = motionEvent.getRawY();
                            if (!EntityView.this.announcedDragging) {
                                EntityView.this.announcedDragging = true;
                                if (EntityView.this.delegate != null) {
                                    EntityView.this.delegate.onBeganEntityDragging(EntityView.this);
                                }
                            }
                            z = true;
                            break;
                        }
                    }
                    z = EntityView.this.onTouchMove(motionEvent.getRawX(), motionEvent.getRawY());
                    break;
                    break;
            }
            if (this.currentHandle == SELECTION_WHOLE_HANDLE) {
                EntityView.this.gestureDetector.onTouchEvent(motionEvent);
            }
            return z;
        }

        protected int pointInsideHandle(float f, float f2) {
            return 0;
        }

        protected void updatePosition() {
            Rect selectionBounds = EntityView.this.getSelectionBounds();
            LayoutParams layoutParams = (LayoutParams) getLayoutParams();
            layoutParams.leftMargin = ((int) selectionBounds.f2685x) + EntityView.this.offsetX;
            layoutParams.topMargin = ((int) selectionBounds.f2686y) + EntityView.this.offsetY;
            layoutParams.width = (int) selectionBounds.width;
            layoutParams.height = (int) selectionBounds.height;
            setLayoutParams(layoutParams);
            setRotation(EntityView.this.getRotation());
        }
    }

    public EntityView(Context context, Point point) {
        super(context);
        this.hasPanned = false;
        this.hasReleased = false;
        this.hasTransformed = false;
        this.announcedSelection = false;
        this.announcedDragging = false;
        this.recognizedLongPress = false;
        this.position = new Point();
        this.uuid = UUID.randomUUID();
        this.position = point;
        this.gestureDetector = new GestureDetector(context, new C13861());
    }

    private boolean onTouchMove(float f, float f2) {
        float scaleX = ((View) getParent()).getScaleX();
        Point point = new Point((f - this.previousLocationX) / scaleX, (f2 - this.previousLocationY) / scaleX);
        if (((float) Math.hypot((double) point.f2683x, (double) point.f2684y)) <= (this.hasPanned ? 6.0f : 16.0f)) {
            return false;
        }
        pan(point);
        this.previousLocationX = f;
        this.previousLocationY = f2;
        if (!this.announcedDragging) {
            this.announcedDragging = true;
            if (this.delegate != null) {
                this.delegate.onBeganEntityDragging(this);
            }
        }
        this.hasPanned = true;
        return true;
    }

    private void onTouchUp() {
        if (!(this.recognizedLongPress || this.hasPanned || this.hasTransformed || this.announcedSelection || this.delegate == null)) {
            this.delegate.onEntitySelected(this);
        }
        if (this.announcedDragging && this.delegate != null) {
            this.delegate.onFinishedEntityDragging(this);
        }
        this.recognizedLongPress = false;
        this.hasPanned = false;
        this.hasTransformed = false;
        this.hasReleased = true;
        this.announcedSelection = false;
        this.announcedDragging = false;
    }

    protected SelectionView createSelectionView() {
        return null;
    }

    public void deselect() {
        if (this.selectionView != null) {
            if (this.selectionView.getParent() != null) {
                ((ViewGroup) this.selectionView.getParent()).removeView(this.selectionView);
            }
            this.selectionView = null;
        }
    }

    public Point getPosition() {
        return this.position;
    }

    public float getScale() {
        return getScaleX();
    }

    protected Rect getSelectionBounds() {
        return new Rect(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public boolean isSelected() {
        return this.selectionView != null;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.delegate.allowInteraction(this);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = true;
        if (motionEvent.getPointerCount() > 1 || !this.delegate.allowInteraction(this)) {
            return false;
        }
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        switch (motionEvent.getActionMasked()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
            case VideoPlayer.STATE_ENDED /*5*/:
                if (!(isSelected() || this.delegate == null)) {
                    this.delegate.onEntitySelected(this);
                    this.announcedSelection = true;
                }
                this.previousLocationX = rawX;
                this.previousLocationY = rawY;
                this.hasReleased = false;
                break;
            case VideoPlayer.TYPE_AUDIO /*1*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case Method.TRACE /*6*/:
                onTouchUp();
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                z = onTouchMove(rawX, rawY);
                break;
            default:
                z = false;
                break;
        }
        this.gestureDetector.onTouchEvent(motionEvent);
        return z;
    }

    public void pan(Point point) {
        Point point2 = this.position;
        point2.f2683x += point.f2683x;
        point2 = this.position;
        point2.f2684y += point.f2684y;
        updatePosition();
    }

    public void rotate(float f) {
        setRotation(f);
        updateSelectionView();
    }

    public void scale(float f) {
        setScale(Math.max(getScale() * f, 0.1f));
        updateSelectionView();
    }

    public void select(ViewGroup viewGroup) {
        View createSelectionView = createSelectionView();
        this.selectionView = createSelectionView;
        viewGroup.addView(createSelectionView);
        createSelectionView.updatePosition();
    }

    public void setDelegate(EntityViewDelegate entityViewDelegate) {
        this.delegate = entityViewDelegate;
    }

    public void setOffset(int i, int i2) {
        this.offsetX = i;
        this.offsetY = i2;
    }

    public void setPosition(Point point) {
        this.position = point;
        updatePosition();
    }

    public void setScale(float f) {
        setScaleX(f);
        setScaleY(f);
    }

    public void setSelectionVisibility(boolean z) {
        if (this.selectionView != null) {
            this.selectionView.setVisibility(z ? 0 : 8);
        }
    }

    protected void updatePosition() {
        float height = ((float) getHeight()) / 2.0f;
        setX(this.position.f2683x - (((float) getWidth()) / 2.0f));
        setY(this.position.f2684y - height);
        updateSelectionView();
    }

    public void updateSelectionView() {
        if (this.selectionView != null) {
            this.selectionView.updatePosition();
        }
    }
}
