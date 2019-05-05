package com.hanista.mobogram.mobo.dialogdm;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.ui.Cells.DialogCell;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.dialogdm.c */
public class DialogDmAdapter extends Adapter {
    private Context f603a;
    private List<DialogDm> f604b;

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.c.a */
    private class DialogDmAdapter extends ViewHolder {
        final /* synthetic */ DialogDmAdapter f602a;

        public DialogDmAdapter(DialogDmAdapter dialogDmAdapter, View view) {
            this.f602a = dialogDmAdapter;
            super(view);
        }
    }

    public DialogDmAdapter(Context context, List<DialogDm> list) {
        this.f603a = context;
        this.f604b = list;
    }

    public void m592a(List<DialogDm> list) {
        this.f604b = list;
    }

    public int getItemCount() {
        return this.f604b.size();
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        return 0;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        DialogCell dialogCell = (DialogCell) viewHolder.itemView;
        DialogDm dialogDm = (DialogDm) this.f604b.get(i);
        dialogCell.setDialogDm(dialogDm);
        dialogCell.useSeparator = true;
        dialogCell.setDialog(dialogDm.m567g(), 0, 0);
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new DialogDmAdapter(this, new DialogCell(this.f603a));
    }
}
