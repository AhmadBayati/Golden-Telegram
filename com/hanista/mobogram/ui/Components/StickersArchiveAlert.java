package com.hanista.mobogram.ui.Components;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.ArchivedStickerSetCell;
import com.hanista.mobogram.ui.StickersActivity;
import java.util.ArrayList;

public class StickersArchiveAlert extends Builder {
    private int currentType;
    private boolean ignoreLayout;
    private BaseFragment parentFragment;
    private int reqId;
    private int scrollOffsetY;
    private ArrayList<StickerSetCovered> stickerSets;

    /* renamed from: com.hanista.mobogram.ui.Components.StickersArchiveAlert.1 */
    class C14941 implements OnClickListener {
        C14941() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.StickersArchiveAlert.2 */
    class C14952 implements OnClickListener {
        C14952() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            StickersArchiveAlert.this.parentFragment.presentFragment(new StickersActivity(StickersArchiveAlert.this.currentType));
            dialogInterface.dismiss();
        }
    }

    private class ListAdapter extends Adapter {
        Context context;

        private class Holder extends ViewHolder {
            public Holder(View view) {
                super(view);
            }
        }

        public ListAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return StickersArchiveAlert.this.stickerSets.size();
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ((ArchivedStickerSetCell) viewHolder.itemView).setStickersSet((StickerSetCovered) StickersArchiveAlert.this.stickerSets.get(i), i != StickersArchiveAlert.this.stickerSets.size() + -1, false);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View archivedStickerSetCell = new ArchivedStickerSetCell(this.context, false);
            archivedStickerSetCell.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(82.0f)));
            return new Holder(archivedStickerSetCell);
        }
    }

    public StickersArchiveAlert(Context context, BaseFragment baseFragment, ArrayList<StickerSetCovered> arrayList) {
        super(context);
        StickerSetCovered stickerSetCovered = (StickerSetCovered) arrayList.get(0);
        if (stickerSetCovered.set.masks) {
            this.currentType = 1;
            setTitle(LocaleController.getString("ArchivedMasksAlertTitle", C0338R.string.ArchivedMasksAlertTitle));
        } else {
            this.currentType = 0;
            setTitle(LocaleController.getString("ArchivedStickersAlertTitle", C0338R.string.ArchivedStickersAlertTitle));
        }
        this.stickerSets = new ArrayList(arrayList);
        this.parentFragment = baseFragment;
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        setView(linearLayout);
        View textView = new TextView(context);
        textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        textView.setTextSize(1, 16.0f);
        textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(23.0f), 0);
        if (stickerSetCovered.set.masks) {
            textView.setText(LocaleController.getString("ArchivedMasksAlertInfo", C0338R.string.ArchivedMasksAlertInfo));
        } else {
            textView.setText(LocaleController.getString("ArchivedStickersAlertInfo", C0338R.string.ArchivedStickersAlertInfo));
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
        View recyclerListView = new RecyclerListView(context);
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        recyclerListView.setAdapter(new ListAdapter(context));
        recyclerListView.setVerticalScrollBarEnabled(false);
        recyclerListView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        recyclerListView.setGlowColor(-657673);
        linearLayout.addView(recyclerListView, LayoutHelper.createLinear(-1, -2, 0.0f, 10.0f, 0.0f, 0.0f));
        setNegativeButton(LocaleController.getString("Close", C0338R.string.Close), new C14941());
        if (this.parentFragment != null) {
            setPositiveButton(LocaleController.getString("Settings", C0338R.string.Settings), new C14952());
        }
    }
}
