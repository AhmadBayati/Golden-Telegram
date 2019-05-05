package com.hanista.mobogram.ui.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.mobo.p019r.TabsUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.ui.Cells.DialogCell;
import com.hanista.mobogram.ui.Cells.LoadingCell;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.List;

public class DialogsAdapter extends Adapter {
    private long categoryId;
    private int currentCount;
    private ArrayList<TL_dialog> dialogsArray;
    private int dialogsType;
    private boolean hiddenMode;
    private Context mContext;
    private long openedDialogId;
    private List<Long> selectedDialogIds;

    private class Holder extends ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    public DialogsAdapter(Context context, int i) {
        this.selectedDialogIds = null;
        this.mContext = context;
        this.dialogsType = i;
    }

    private void initThemeCell(DialogCell dialogCell) {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2514y;
            if (AdvanceTheme.av <= 0) {
            }
        }
    }

    private void initThemeHolder(ViewGroup viewGroup) {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2514y;
            int i2 = AdvanceTheme.av;
            if (i2 > 0) {
                Orientation orientation;
                switch (i2) {
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                int i3 = AdvanceTheme.aw;
                viewGroup.setBackgroundDrawable(new GradientDrawable(orientation, new int[]{i, i3}));
                return;
            }
            viewGroup.setBackgroundColor(i);
        }
    }

    public ArrayList<TL_dialog> getDialogsArray() {
        if (this.dialogsArray == null) {
            this.dialogsArray = TabsUtil.m2259a(this.dialogsType, this.categoryId, this.hiddenMode);
        }
        return this.dialogsArray;
    }

    public int getDialogsType() {
        return this.dialogsType;
    }

    public TL_dialog getItem(int i) {
        ArrayList dialogsArray = getDialogsArray();
        return (i < 0 || i >= dialogsArray.size()) ? null : (TL_dialog) dialogsArray.get(i);
    }

    public int getItemCount() {
        int size = getDialogsArray().size();
        if (size == 0 && MessagesController.getInstance().loadingDialogs) {
            return 0;
        }
        if (!MessagesController.getInstance().dialogsEndReached) {
            size += 2;
        }
        this.currentCount = size;
        return size;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        return i >= getDialogsArray().size() ? 1 : 0;
    }

    public boolean isDataSetChanged() {
        this.dialogsArray = null;
        int i = this.currentCount;
        return i != getItemCount() || i == 1;
    }

    public void notifyDataSetChanged() {
        this.dialogsArray = null;
        super.notifyDataSetChanged();
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        boolean z = true;
        if (viewHolder.getItemViewType() == 0) {
            DialogCell dialogCell = (DialogCell) viewHolder.itemView;
            dialogCell.useSeparator = i != getItemCount() + -1;
            TL_dialog item = getItem(i);
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                if (item.id != this.openedDialogId) {
                    z = false;
                }
                dialogCell.setDialogSelected(z);
            }
            if (this.selectedDialogIds != null) {
                dialogCell.setDialogSelected(this.selectedDialogIds.contains(Long.valueOf(item.id)));
            }
            initThemeCell(dialogCell);
            dialogCell.setDialog(item, i, this.dialogsType);
            dialogCell.setCategoryId(this.categoryId);
            dialogCell.setHiddenMode(this.hiddenMode);
        } else if (viewHolder.getItemViewType() == 1 && this.dialogsType != 1 && this.dialogsType != 2) {
            viewHolder.itemView.setVisibility(8);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = null;
        if (i == 0) {
            view = new DialogCell(this.mContext);
        } else if (i == 1) {
            view = new LoadingCell(this.mContext);
        }
        view.setLayoutParams(new LayoutParams(-1, -2));
        initThemeHolder(viewGroup);
        return new Holder(view);
    }

    public void onViewAttachedToWindow(ViewHolder viewHolder) {
        if (viewHolder.itemView instanceof DialogCell) {
            ((DialogCell) viewHolder.itemView).checkCurrentDialogIndex();
        }
    }

    public void setCategoryId(long j) {
        this.categoryId = j;
    }

    public void setHiddenMode(boolean z) {
        this.hiddenMode = z;
    }

    public void setOpenedDialogId(long j) {
        this.openedDialogId = j;
    }

    public void setSelectedDialogIds(List<Long> list) {
        this.selectedDialogIds = list;
    }
}
