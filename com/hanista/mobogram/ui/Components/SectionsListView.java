package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.ui.Adapters.BaseSectionsAdapter;

public class SectionsListView extends ListView implements OnScrollListener {
    private int currentStartSection;
    private BaseSectionsAdapter mAdapter;
    private OnScrollListener mOnScrollListener;
    private View pinnedHeader;

    public SectionsListView(Context context) {
        super(context);
        this.currentStartSection = -1;
        super.setOnScrollListener(this);
    }

    public SectionsListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.currentStartSection = -1;
        super.setOnScrollListener(this);
    }

    public SectionsListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.currentStartSection = -1;
        super.setOnScrollListener(this);
    }

    private void ensurePinnedHeaderLayout(View view, boolean z) {
        if (view.isLayoutRequested() || z) {
            try {
                view.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(0, 0));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
    }

    private View getSectionHeaderView(int i, View view) {
        boolean z = view == null;
        View sectionHeaderView = this.mAdapter.getSectionHeaderView(i, view, this);
        if (z) {
            ensurePinnedHeaderLayout(sectionHeaderView, false);
        }
        return sectionHeaderView;
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mAdapter != null && this.pinnedHeader != null) {
            int save = canvas.save();
            canvas.translate(LocaleController.isRTL ? (float) (getWidth() - this.pinnedHeader.getWidth()) : 0.0f, (float) ((Integer) this.pinnedHeader.getTag()).intValue());
            canvas.clipRect(0, 0, getWidth(), this.pinnedHeader.getMeasuredHeight());
            this.pinnedHeader.draw(canvas);
            canvas.restoreToCount(save);
        }
    }

    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScroll(absListView, i, i2, i3);
        }
        if (this.mAdapter != null && this.mAdapter.getCount() != 0) {
            int sectionForPosition = this.mAdapter.getSectionForPosition(i);
            if (this.currentStartSection != sectionForPosition || this.pinnedHeader == null) {
                this.pinnedHeader = getSectionHeaderView(sectionForPosition, this.pinnedHeader);
                this.currentStartSection = sectionForPosition;
            }
            if (this.mAdapter.getPositionInSectionForPosition(i) == this.mAdapter.getCountForSection(sectionForPosition) - 1) {
                View childAt = getChildAt(0);
                int height = this.pinnedHeader.getHeight();
                if (childAt != null) {
                    sectionForPosition = childAt.getHeight() + childAt.getTop();
                    sectionForPosition = sectionForPosition < height ? sectionForPosition - height : 0;
                } else {
                    sectionForPosition = -AndroidUtilities.dp(100.0f);
                }
                if (sectionForPosition < 0) {
                    this.pinnedHeader.setTag(Integer.valueOf(sectionForPosition));
                } else {
                    this.pinnedHeader.setTag(Integer.valueOf(0));
                }
            } else {
                this.pinnedHeader.setTag(Integer.valueOf(0));
            }
            invalidate();
        }
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScrollStateChanged(absListView, i);
        }
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (this.mAdapter != null && this.pinnedHeader != null) {
            ensurePinnedHeaderLayout(this.pinnedHeader, true);
        }
    }

    public void setAdapter(ListAdapter listAdapter) {
        if (this.mAdapter != listAdapter) {
            this.pinnedHeader = null;
            if (listAdapter instanceof BaseSectionsAdapter) {
                this.mAdapter = (BaseSectionsAdapter) listAdapter;
            } else {
                this.mAdapter = null;
            }
            super.setAdapter(listAdapter);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        super.setOnItemClickListener(onItemClickListener);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }
}
