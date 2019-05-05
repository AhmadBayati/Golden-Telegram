package com.hanista.mobogram.messenger.support.widget.helper;

import android.graphics.Canvas;
import android.view.View;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;

public interface ItemTouchUIUtil {
    void clearView(View view);

    void onDraw(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z);

    void onDrawOver(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z);

    void onSelected(View view);
}
