package com.hanista.mobogram.mobo.p002c;

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
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;

/* renamed from: com.hanista.mobogram.mobo.c.c */
public class RecentDialogCell extends FrameLayout {
    private static Drawable f308d;
    private static Drawable f309e;
    private static TextPaint f310f;
    private BackupImageView f311a;
    private TextView f312b;
    private AvatarDrawable f313c;
    private int f314g;
    private int f315h;
    private StaticLayout f316i;
    private long f317j;
    private int f318k;

    public RecentDialogCell(Context context, int i) {
        super(context);
        this.f313c = new AvatarDrawable();
        setBackgroundResource(C0338R.drawable.list_selector);
        this.f318k = i * 1;
        this.f311a = new BackupImageView(context);
        this.f311a.setRoundRadius(AndroidUtilities.dp(27.0f));
        addView(this.f311a, LayoutHelper.createFrame((int) (((double) this.f318k) / 1.7d), (float) ((int) (((double) this.f318k) / 1.7d)), 49, 0.0f, (float) (this.f318k / 14), 0.0f, 0.0f));
        this.f312b = new TextView(context);
        this.f312b.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.f312b.setTextSize(1, (float) (this.f318k / 9));
        this.f312b.setMaxLines(2);
        this.f312b.setGravity(49);
        this.f312b.setLines(2);
        this.f312b.setEllipsize(TruncateAt.END);
        addView(this.f312b, LayoutHelper.createFrame(-1, -2.0f, 51, (float) (this.f318k / 6), (float) ((int) (((double) this.f318k) / 1.5d)), (float) (this.f318k / 6), 0.0f));
        if (f308d == null) {
            f308d = getResources().getDrawable(C0338R.drawable.dialogs_badge);
            f309e = getResources().getDrawable(C0338R.drawable.dialogs_badge2);
            f310f = new TextPaint(1);
            f310f.setTextSize((float) AndroidUtilities.dp((float) (this.f318k / 8)));
            f310f.setColor(-1);
            f310f.setTypeface(FontUtil.m1176a().m1160c());
        }
        m394a();
    }

    private void m394a() {
        if (ThemeUtil.m2490b()) {
            this.f312b.setTextColor(AdvanceTheme.bf != Theme.ATTACH_SHEET_TEXT_COLOR ? AdvanceTheme.bf : Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        }
    }

    public void m395a(int i) {
        if (i == 0 || (i & TLRPC.USER_FLAG_UNUSED2) != 0 || (i & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0) {
            TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.f317j));
            if (tL_dialog == null || tL_dialog.unread_count == 0) {
                if (this.f316i != null) {
                    if (i != 0) {
                        invalidate();
                    }
                    this.f314g = 0;
                    this.f316i = null;
                }
            } else if (this.f314g != tL_dialog.unread_count) {
                this.f314g = tL_dialog.unread_count;
                CharSequence format = String.format("%d", new Object[]{Integer.valueOf(tL_dialog.unread_count)});
                this.f315h = Math.max(AndroidUtilities.dp((float) (this.f318k / 8)), (int) Math.ceil((double) f310f.measureText(format)));
                this.f316i = new StaticLayout(format, f310f, this.f315h, Alignment.ALIGN_CENTER, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                if (i != 0) {
                    invalidate();
                }
            }
        }
    }

    public void m396a(long j, boolean z, CharSequence charSequence) {
        this.f317j = j;
        int i = (int) j;
        int i2 = (int) (j >> 32);
        TLObject tLObject = null;
        User user;
        if (i == 0) {
            EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i2));
            if (encryptedChat != null) {
                user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat.user_id));
                if (charSequence != null) {
                    this.f312b.setText(charSequence);
                } else if (user != null) {
                    this.f312b.setText(ContactsController.formatName(user.first_name, user.last_name));
                } else {
                    this.f312b.setText(TtmlNode.ANONYMOUS_REGION_ID);
                }
                this.f313c.setInfo(user);
                if (!(user == null || user.photo == null)) {
                    tLObject = user.photo.photo_small;
                }
            }
        } else if (i > 0) {
            user = MessagesController.getInstance().getUser(Integer.valueOf((int) j));
            if (user == null) {
                EncryptedChat encryptedChat2 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) j));
                if (encryptedChat2 != null) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat2.user_id));
                }
            }
            if (charSequence != null) {
                this.f312b.setText(charSequence);
            } else if (user != null) {
                this.f312b.setText(ContactsController.formatName(user.first_name, user.last_name));
            } else {
                this.f312b.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            this.f313c.setInfo(user);
            if (!(user == null || user.photo == null)) {
                tLObject = user.photo.photo_small;
            }
        } else {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) j)));
            if (charSequence != null) {
                this.f312b.setText(charSequence);
            } else if (chat != null) {
                this.f312b.setText(chat.title);
            } else {
                this.f312b.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            this.f313c.setInfo(chat);
            if (!(chat == null || chat.photo == null)) {
                tLObject = chat.photo.photo_small;
            }
        }
        this.f311a.setImage(tLObject, "50_50", this.f313c);
        m395a(0);
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.f311a && this.f316i != null) {
            int dp = AndroidUtilities.dp((float) (this.f318k / 17));
            int dp2 = AndroidUtilities.dp((float) (this.f318k / 2));
            int dp3 = dp2 - AndroidUtilities.dp((float) (this.f318k / 18));
            if (MessagesController.getInstance().isDialogMuted(this.f317j)) {
                f309e.setBounds(dp3, dp, (this.f315h + dp3) + AndroidUtilities.dp((float) (this.f318k / 10)), Math.min(f309e.getIntrinsicHeight(), this.f315h + AndroidUtilities.dp((float) (this.f318k / 10))) + dp);
                f309e.draw(canvas);
            } else {
                f308d.setBounds(dp3, dp, (this.f315h + dp3) + AndroidUtilities.dp((float) (this.f318k / 10)), Math.min(f309e.getIntrinsicHeight(), this.f315h + AndroidUtilities.dp((float) (this.f318k / 10))) + dp);
                f308d.draw(canvas);
            }
            canvas.save();
            canvas.translate((float) dp2, (float) (dp + AndroidUtilities.dp((float) (this.f318k / 25))));
            this.f316i.draw(canvas);
            canvas.restore();
        }
        return drawChild;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.f318k), C0700C.ENCODING_PCM_32BIT));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
            getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return super.onTouchEvent(motionEvent);
    }
}
