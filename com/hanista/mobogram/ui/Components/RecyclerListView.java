package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.AdapterDataObserver;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnItemTouchListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import java.lang.reflect.Field;

public class RecyclerListView extends RecyclerView {
    private static int[] attributes;
    private static boolean gotAttributes;
    private Runnable clickRunnable;
    private int currentChildPosition;
    private View currentChildView;
    private boolean disallowInterceptTouchEvents;
    private View emptyView;
    private boolean instantClick;
    private boolean interceptedByChild;
    private GestureDetector mGestureDetector;
    private AdapterDataObserver observer;
    private OnInterceptTouchListener onInterceptTouchListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnScrollListener onScrollListener;
    private Runnable selectChildRunnable;
    private boolean wasPressed;

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(View view, int i);
    }

    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    /* renamed from: com.hanista.mobogram.ui.Components.RecyclerListView.1 */
    class C14451 extends AdapterDataObserver {
        C14451() {
        }

        public void onChanged() {
            RecyclerListView.this.checkIfEmpty();
        }

        public void onItemRangeInserted(int i, int i2) {
            RecyclerListView.this.checkIfEmpty();
        }

        public void onItemRangeRemoved(int i, int i2) {
            RecyclerListView.this.checkIfEmpty();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.RecyclerListView.2 */
    class C14462 extends OnScrollListener {
        C14462() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (!(i == 0 || RecyclerListView.this.currentChildView == null)) {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    RecyclerListView.this.selectChildRunnable = null;
                }
                MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                try {
                    RecyclerListView.this.mGestureDetector.onTouchEvent(obtain);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                RecyclerListView.this.currentChildView.onTouchEvent(obtain);
                obtain.recycle();
                RecyclerListView.this.currentChildView.setPressed(false);
                RecyclerListView.this.currentChildView = null;
                RecyclerListView.this.interceptedByChild = false;
            }
            if (RecyclerListView.this.onScrollListener != null) {
                RecyclerListView.this.onScrollListener.onScrollStateChanged(recyclerView, i);
            }
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (RecyclerListView.this.onScrollListener != null) {
                RecyclerListView.this.onScrollListener.onScrolled(recyclerView, i, i2);
            }
        }
    }

    private class RecyclerListViewItemClickListener implements OnItemTouchListener {

        /* renamed from: com.hanista.mobogram.ui.Components.RecyclerListView.RecyclerListViewItemClickListener.1 */
        class C14481 extends SimpleOnGestureListener {
            final /* synthetic */ RecyclerListView val$this$0;

            /* renamed from: com.hanista.mobogram.ui.Components.RecyclerListView.RecyclerListViewItemClickListener.1.1 */
            class C14471 implements Runnable {
                final /* synthetic */ View val$view;

                C14471(View view) {
                    this.val$view = view;
                }

                public void run() {
                    if (this == RecyclerListView.this.clickRunnable) {
                        RecyclerListView.this.clickRunnable = null;
                    }
                    if (this.val$view != null) {
                        this.val$view.setPressed(false);
                        if (!RecyclerListView.this.instantClick) {
                            this.val$view.playSoundEffect(0);
                            if (RecyclerListView.this.onItemClickListener != null) {
                                RecyclerListView.this.onItemClickListener.onItemClick(this.val$view, RecyclerListView.this.currentChildPosition);
                            }
                        }
                    }
                }
            }

            C14481(RecyclerListView recyclerListView) {
                this.val$this$0 = recyclerListView;
            }

            public void onLongPress(MotionEvent motionEvent) {
                if (RecyclerListView.this.currentChildView != null) {
                    View access$100 = RecyclerListView.this.currentChildView;
                    if (RecyclerListView.this.onItemLongClickListener != null && RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                        access$100.performHapticFeedback(0);
                    }
                }
            }

            public boolean onSingleTapUp(MotionEvent motionEvent) {
                if (!(RecyclerListView.this.currentChildView == null || RecyclerListView.this.onItemClickListener == null)) {
                    RecyclerListView.this.currentChildView.setPressed(true);
                    View access$100 = RecyclerListView.this.currentChildView;
                    if (RecyclerListView.this.instantClick) {
                        access$100.playSoundEffect(0);
                        RecyclerListView.this.onItemClickListener.onItemClick(access$100, RecyclerListView.this.currentChildPosition);
                    }
                    AndroidUtilities.runOnUIThread(RecyclerListView.this.clickRunnable = new C14471(access$100), (long) ViewConfiguration.getPressedStateDuration());
                    if (RecyclerListView.this.selectChildRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                        RecyclerListView.this.selectChildRunnable = null;
                        RecyclerListView.this.currentChildView = null;
                        RecyclerListView.this.interceptedByChild = false;
                    }
                }
                return true;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.Components.RecyclerListView.RecyclerListViewItemClickListener.2 */
        class C14492 implements Runnable {
            C14492() {
            }

            public void run() {
                if (RecyclerListView.this.selectChildRunnable != null && RecyclerListView.this.currentChildView != null) {
                    RecyclerListView.this.currentChildView.setPressed(true);
                    RecyclerListView.this.selectChildRunnable = null;
                }
            }
        }

        public RecyclerListViewItemClickListener(Context context) {
            RecyclerListView.this.mGestureDetector = new GestureDetector(context, new C14481(RecyclerListView.this));
        }

        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            Object obj = RecyclerListView.this.getScrollState() == 0 ? 1 : null;
            if ((actionMasked == 0 || actionMasked == 5) && RecyclerListView.this.currentChildView == null && obj != null) {
                RecyclerListView.this.currentChildView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (RecyclerListView.this.currentChildView instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) RecyclerListView.this.currentChildView;
                    float x = motionEvent.getX() - ((float) RecyclerListView.this.currentChildView.getLeft());
                    float y = motionEvent.getY() - ((float) RecyclerListView.this.currentChildView.getTop());
                    for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                        View childAt = viewGroup.getChildAt(childCount);
                        if (x >= ((float) childAt.getLeft()) && x <= ((float) childAt.getRight()) && y >= ((float) childAt.getTop()) && y <= ((float) childAt.getBottom()) && childAt.isClickable()) {
                            RecyclerListView.this.currentChildView = null;
                            break;
                        }
                    }
                }
                RecyclerListView.this.currentChildPosition = -1;
                if (RecyclerListView.this.currentChildView != null) {
                    RecyclerListView.this.currentChildPosition = recyclerView.getChildPosition(RecyclerListView.this.currentChildView);
                    MotionEvent obtain = MotionEvent.obtain(0, 0, motionEvent.getActionMasked(), motionEvent.getX() - ((float) RecyclerListView.this.currentChildView.getLeft()), motionEvent.getY() - ((float) RecyclerListView.this.currentChildView.getTop()), 0);
                    if (RecyclerListView.this.currentChildView.onTouchEvent(obtain)) {
                        RecyclerListView.this.interceptedByChild = true;
                    }
                    obtain.recycle();
                }
            }
            if (!(RecyclerListView.this.currentChildView == null || RecyclerListView.this.interceptedByChild || motionEvent == null)) {
                try {
                    RecyclerListView.this.mGestureDetector.onTouchEvent(motionEvent);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
            if (actionMasked == 0 || actionMasked == 5) {
                if (!(RecyclerListView.this.interceptedByChild || RecyclerListView.this.currentChildView == null)) {
                    RecyclerListView.this.selectChildRunnable = new C14492();
                    AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, (long) ViewConfiguration.getTapTimeout());
                }
            } else if (RecyclerListView.this.currentChildView != null && (actionMasked == 1 || actionMasked == 6 || actionMasked == 3 || obj == null)) {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    RecyclerListView.this.selectChildRunnable = null;
                }
                RecyclerListView.this.currentChildView.setPressed(false);
                RecyclerListView.this.currentChildView = null;
                RecyclerListView.this.interceptedByChild = false;
            }
            return false;
        }

        public void onRequestDisallowInterceptTouchEvent(boolean z) {
            RecyclerListView.this.cancelClickRunnables(true);
        }

        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        }
    }

    public RecyclerListView(Context context) {
        super(context);
        this.observer = new C14451();
        try {
            if (!gotAttributes) {
                attributes = getResourceDeclareStyleableIntArray("com.android.internal", "View");
                gotAttributes = true;
            }
            TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributes);
            View.class.getDeclaredMethod("initializeScrollbars", new Class[]{TypedArray.class}).invoke(this, new Object[]{obtainStyledAttributes});
            obtainStyledAttributes.recycle();
        } catch (Throwable th) {
            FileLog.m18e("tmessages", th);
        }
        super.setOnScrollListener(new C14462());
        addOnItemTouchListener(new RecyclerListViewItemClickListener(context));
    }

    private void checkIfEmpty() {
        int i = 0;
        if (this.emptyView != null && getAdapter() != null) {
            int i2 = getAdapter().getItemCount() == 0 ? 1 : 0;
            this.emptyView.setVisibility(i2 != 0 ? 0 : 8);
            if (i2 != 0) {
                i = 4;
            }
            setVisibility(i);
        }
    }

    public void cancelClickRunnables(boolean z) {
        if (this.selectChildRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.selectChildRunnable);
            this.selectChildRunnable = null;
        }
        if (this.currentChildView != null) {
            if (z) {
                this.currentChildView.setPressed(false);
            }
            this.currentChildView = null;
        }
        if (this.clickRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.clickRunnable);
            this.clickRunnable = null;
        }
        this.interceptedByChild = false;
    }

    public View getEmptyView() {
        return this.emptyView;
    }

    public int[] getResourceDeclareStyleableIntArray(String str, String str2) {
        try {
            Field field = Class.forName(str + ".R$styleable").getField(str2);
            if (field != null) {
                return (int[]) field.get(null);
            }
        } catch (Throwable th) {
        }
        return null;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void invalidateViews() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).invalidate();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.disallowInterceptTouchEvents) {
            requestDisallowInterceptTouchEvent(true);
        }
        return (this.onInterceptTouchListener != null && this.onInterceptTouchListener.onInterceptTouchEvent(motionEvent)) || super.onInterceptTouchEvent(motionEvent);
    }

    public void setAdapter(Adapter adapter) {
        Adapter adapter2 = getAdapter();
        if (adapter2 != null) {
            adapter2.unregisterAdapterDataObserver(this.observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.observer);
        }
        checkIfEmpty();
    }

    public void setDisallowInterceptTouchEvents(boolean z) {
        this.disallowInterceptTouchEvents = z;
    }

    public void setEmptyView(View view) {
        if (this.emptyView != view) {
            this.emptyView = view;
            checkIfEmpty();
        }
    }

    public void setInstantClick(boolean z) {
        this.instantClick = z;
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener onInterceptTouchListener) {
        this.onInterceptTouchListener = onInterceptTouchListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setVerticalScrollBarEnabled(boolean z) {
        if (attributes != null) {
            super.setVerticalScrollBarEnabled(z);
        }
    }

    public void stopScroll() {
        try {
            super.stopScroll();
        } catch (NullPointerException e) {
        }
    }
}
