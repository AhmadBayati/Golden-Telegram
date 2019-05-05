package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.Adapters.BaseSectionsAdapter;
import java.util.ArrayList;
import java.util.Iterator;

public class LetterSectionsListView extends ListView implements OnScrollListener {
    private int currentFirst;
    private int currentVisible;
    private ArrayList<View> headers;
    private ArrayList<View> headersCache;
    private BaseSectionsAdapter mAdapter;
    private OnScrollListener mOnScrollListener;
    private int sectionsCount;
    private int startSection;

    public LetterSectionsListView(Context context) {
        super(context);
        this.headers = new ArrayList();
        this.headersCache = new ArrayList();
        this.currentFirst = -1;
        this.currentVisible = -1;
        super.setOnScrollListener(this);
    }

    public LetterSectionsListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.headers = new ArrayList();
        this.headersCache = new ArrayList();
        this.currentFirst = -1;
        this.currentVisible = -1;
        super.setOnScrollListener(this);
    }

    public LetterSectionsListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.headers = new ArrayList();
        this.headersCache = new ArrayList();
        this.currentFirst = -1;
        this.currentVisible = -1;
        super.setOnScrollListener(this);
    }

    private void ensurePinnedHeaderLayout(View view, boolean z) {
        if (view.isLayoutRequested() || z) {
            LayoutParams layoutParams = view.getLayoutParams();
            try {
                view.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(layoutParams.height, C0700C.ENCODING_PCM_32BIT));
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
        if (this.mAdapter != null && !this.headers.isEmpty()) {
            Iterator it = this.headers.iterator();
            while (it.hasNext()) {
                View view = (View) it.next();
                int save = canvas.save();
                int intValue = ((Integer) view.getTag()).intValue();
                canvas.translate(LocaleController.isRTL ? (float) (getWidth() - view.getWidth()) : 0.0f, (float) intValue);
                canvas.clipRect(0, 0, getWidth(), view.getMeasuredHeight());
                if (intValue < 0) {
                    canvas.saveLayerAlpha(0.0f, (float) intValue, (float) view.getWidth(), (float) (canvas.getHeight() + intValue), (int) (255.0f * ((((float) intValue) / ((float) view.getMeasuredHeight())) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), 4);
                }
                view.draw(canvas);
                canvas.restoreToCount(save);
            }
        }
    }

    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScroll(absListView, i, i2, i3);
        }
        if (this.mAdapter != null) {
            this.headersCache.addAll(this.headers);
            this.headers.clear();
            if (this.mAdapter.getCount() != 0) {
                int countForSection;
                if (!(this.currentFirst == i && this.currentVisible == i2)) {
                    this.currentFirst = i;
                    this.currentVisible = i2;
                    this.sectionsCount = 1;
                    this.startSection = this.mAdapter.getSectionForPosition(i);
                    countForSection = (this.mAdapter.getCountForSection(this.startSection) + i) - this.mAdapter.getPositionInSectionForPosition(i);
                    while (countForSection < i + i2) {
                        countForSection += this.mAdapter.getCountForSection(this.startSection + this.sectionsCount);
                        this.sectionsCount++;
                    }
                }
                int i4 = i;
                for (int i5 = this.startSection; i5 < this.startSection + this.sectionsCount; i5++) {
                    View view = null;
                    if (!this.headersCache.isEmpty()) {
                        view = (View) this.headersCache.get(0);
                        this.headersCache.remove(0);
                    }
                    View sectionHeaderView = getSectionHeaderView(i5, view);
                    this.headers.add(sectionHeaderView);
                    int countForSection2 = this.mAdapter.getCountForSection(i5);
                    if (i5 == this.startSection) {
                        countForSection = this.mAdapter.getPositionInSectionForPosition(i4);
                        if (countForSection == countForSection2 - 1) {
                            sectionHeaderView.setTag(Integer.valueOf(-sectionHeaderView.getHeight()));
                        } else if (countForSection == countForSection2 - 2) {
                            view = getChildAt(i4 - i);
                            countForSection = view != null ? view.getTop() : -AndroidUtilities.dp(100.0f);
                            if (countForSection < 0) {
                                sectionHeaderView.setTag(Integer.valueOf(countForSection));
                            } else {
                                sectionHeaderView.setTag(Integer.valueOf(0));
                            }
                        } else {
                            sectionHeaderView.setTag(Integer.valueOf(0));
                        }
                        i4 += countForSection2 - this.mAdapter.getPositionInSectionForPosition(i);
                    } else {
                        view = getChildAt(i4 - i);
                        if (view != null) {
                            sectionHeaderView.setTag(Integer.valueOf(view.getTop()));
                        } else {
                            sectionHeaderView.setTag(Integer.valueOf(-AndroidUtilities.dp(100.0f)));
                        }
                        i4 += countForSection2;
                    }
                }
            }
        }
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScrollStateChanged(absListView, i);
        }
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (this.mAdapter != null && !this.headers.isEmpty()) {
            Iterator it = this.headers.iterator();
            while (it.hasNext()) {
                ensurePinnedHeaderLayout((View) it.next(), true);
            }
        }
    }

    public void setAdapter(ListAdapter listAdapter) {
        if (this.mAdapter != listAdapter) {
            this.headers.clear();
            this.headersCache.clear();
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
