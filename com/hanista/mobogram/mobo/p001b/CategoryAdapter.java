package com.hanista.mobogram.mobo.p001b;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.b.c */
public class CategoryAdapter extends Adapter {
    private Context f183a;
    private List<Category> f184b;

    /* renamed from: com.hanista.mobogram.mobo.b.c.a */
    private class CategoryAdapter extends ViewHolder {
        final /* synthetic */ CategoryAdapter f182a;

        public CategoryAdapter(CategoryAdapter categoryAdapter, View view) {
            this.f182a = categoryAdapter;
            super(view);
        }
    }

    public CategoryAdapter(Context context, List<Category> list) {
        this.f183a = context;
        this.f184b = list;
    }

    public void m312a(List<Category> list) {
        this.f184b = list;
    }

    public int getItemCount() {
        return this.f184b.size();
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        return 0;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ((CategoryInfoCell) viewHolder.itemView).setCategory((Category) this.f184b.get(i));
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new CategoryAdapter(this, new CategoryInfoCell(this.f183a));
    }
}
