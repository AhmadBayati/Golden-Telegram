package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class HintDialogCell extends FrameLayout {
    private static Drawable countDrawable;
    private static Drawable countDrawableGrey;
    private static TextPaint countPaint;
    private AvatarDrawable avatarDrawable;
    private StaticLayout countLayout;
    private int countWidth;
    private long dialog_id;
    private BackupImageView imageView;
    private int lastUnreadCount;
    private TextView nameTextView;

    public HintDialogCell(Context context) {
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
        if (countDrawable == null) {
            countDrawable = getResources().getDrawable(C0338R.drawable.dialogs_badge);
            countDrawableGrey = getResources().getDrawable(C0338R.drawable.dialogs_badge2);
            countPaint = new TextPaint(1);
            countPaint.setColor(-1);
            countPaint.setTypeface(FontUtil.m1176a().m1160c());
        }
        countPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        initTheme();
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.bf;
            TextView textView = this.nameTextView;
            if (i == Theme.ATTACH_SHEET_TEXT_COLOR) {
                i = Theme.STICKERS_SHEET_TITLE_TEXT_COLOR;
            }
            textView.setTextColor(i);
        }
    }

    public void checkUnreadCounter(int i) {
        if (i == 0 || (i & TLRPC.USER_FLAG_UNUSED2) != 0 || (i & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
            TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
            if (tL_dialog == null || tL_dialog.unread_count == 0) {
                if (this.countLayout != null) {
                    if (i != 0) {
                        invalidate();
                    }
                    this.lastUnreadCount = 0;
                    this.countLayout = null;
                }
            } else if (this.lastUnreadCount != tL_dialog.unread_count) {
                this.lastUnreadCount = tL_dialog.unread_count;
                CharSequence format = String.format("%d", new Object[]{Integer.valueOf(tL_dialog.unread_count)});
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) countPaint.measureText(format)));
                this.countLayout = new StaticLayout(format, countPaint, this.countWidth, Alignment.ALIGN_CENTER, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                if (i != 0) {
                    invalidate();
                }
            }
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && this.countLayout != null) {
            int dp = AndroidUtilities.dp(6.0f);
            int dp2 = AndroidUtilities.dp(54.0f);
            int dp3 = dp2 - AndroidUtilities.dp(5.5f);
            if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
                countDrawableGrey.setBounds(dp3, dp, (this.countWidth + dp3) + AndroidUtilities.dp(11.0f), countDrawableGrey.getIntrinsicHeight() + dp);
                countDrawableGrey.draw(canvas);
            } else {
                countDrawable.setBounds(dp3, dp, (this.countWidth + dp3) + AndroidUtilities.dp(11.0f), countDrawable.getIntrinsicHeight() + dp);
                countDrawable.draw(canvas);
            }
            canvas.save();
            canvas.translate((float) dp2, (float) (dp + AndroidUtilities.dp(4.0f)));
            this.countLayout.draw(canvas);
            canvas.restore();
        }
        return drawChild;
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

    public void setDialog(int i, boolean z, CharSequence charSequence) {
        TLObject tLObject;
        this.dialog_id = (long) i;
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
            tLObject = null;
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
            tLObject = null;
        }
        this.imageView.setImage(tLObject, "50_50", this.avatarDrawable);
        if (z) {
            checkUnreadCounter(0);
        } else {
            this.countLayout = null;
        }
    }
}
