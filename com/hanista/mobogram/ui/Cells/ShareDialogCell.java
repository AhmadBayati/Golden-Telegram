package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class ShareDialogCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private CheckBox checkBox;
    private BackupImageView imageView;
    private TextView nameTextView;

    public ShareDialogCell(Context context) {
        super(context);
        this.avatarDrawable = new AvatarDrawable();
        setBackgroundResource(C0338R.drawable.list_selector);
        this.imageView = new BackupImageView(context);
        this.imageView.setRoundRadius(AndroidUtilities.dp(27.0f));
        addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(2);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(2);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 64.0f, 6.0f, 0.0f));
        this.checkBox = new CheckBox(context, C0338R.drawable.round_check2);
        this.checkBox.setSize(24);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.checkBox.setVisibility(0);
        if (!ThemeUtil.m2490b()) {
            this.checkBox.setColor(ThemeUtil.m2485a().m2289c());
        }
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 49, 17.0f, 39.0f, 0.0f, 0.0f));
        initTheme();
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            this.nameTextView.setTextColor(AdvanceTheme.bf != Theme.ATTACH_SHEET_TEXT_COLOR ? AdvanceTheme.bf : Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
            getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public void setDialog(int i, boolean z, CharSequence charSequence) {
        TLObject tLObject = null;
        if (i > 0) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(i));
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else if (user != null) {
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
            } else {
                this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            this.avatarDrawable.setInfo(user);
            if (!(user == null || user.photo == null)) {
                tLObject = user.photo.photo_small;
            }
        } else {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-i));
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            this.avatarDrawable.setInfo(chat);
            if (!(chat == null || chat.photo == null)) {
                tLObject = chat.photo.photo_small;
            }
        }
        this.imageView.setImage(tLObject, "50_50", this.avatarDrawable);
        this.checkBox.setChecked(z, false);
    }
}
