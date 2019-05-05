package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class JoinSheetUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView imageView;
    private TextView nameTextView;
    private int[] result;

    public JoinSheetUserCell(Context context) {
        super(context);
        this.avatarDrawable = new AvatarDrawable();
        this.result = new int[1];
        this.imageView = new BackupImageView(context);
        this.imageView.setRoundRadius(AndroidUtilities.dp(27.0f));
        addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 64.0f, 6.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setCount(int i) {
        this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
        this.avatarDrawable.setInfo(0, null, null, false, "+" + LocaleController.formatShortNumber(i, this.result));
        this.imageView.setImage((FileLocation) null, "50_50", this.avatarDrawable);
    }

    public void setUser(User user) {
        this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
        this.avatarDrawable.setInfo(user);
        TLObject tLObject = null;
        if (!(user == null || user.photo == null)) {
            tLObject = user.photo.photo_small;
        }
        this.imageView.setImage(tLObject, "50_50", this.avatarDrawable);
    }
}
