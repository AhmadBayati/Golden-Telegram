package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class DrawerProfileCell extends FrameLayout {
    private BackupImageView avatarImageView;
    private CloudView cloudView;
    private int currentColor;
    private Rect destRect;
    private TextView nameTextView;
    private Paint paint;
    private TextView phoneTextView;
    private ImageView shadowView;
    private Rect srcRect;

    private class CloudView extends View {
        private Drawable cloudDrawable;
        private Paint paint;

        public CloudView(Context context) {
            super(context);
            this.paint = new Paint(1);
            this.cloudDrawable = getResources().getDrawable(C0338R.drawable.cloud);
        }

        protected void onDraw(Canvas canvas) {
            if (!ApplicationLoader.isCustomTheme() || ApplicationLoader.getCachedWallpaper() == null) {
                this.paint.setColor(-12420183);
            } else {
                this.paint.setColor(ApplicationLoader.getServiceMessageColor());
            }
            canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) AndroidUtilities.dp(34.0f)) / 2.0f, this.paint);
            int measuredWidth = (getMeasuredWidth() - AndroidUtilities.dp(33.0f)) / 2;
            int measuredHeight = (getMeasuredHeight() - AndroidUtilities.dp(33.0f)) / 2;
            this.cloudDrawable.setBounds(measuredWidth, measuredHeight, AndroidUtilities.dp(33.0f) + measuredWidth, AndroidUtilities.dp(33.0f) + measuredHeight);
            this.cloudDrawable.draw(canvas);
        }
    }

    public DrawerProfileCell(Context context) {
        super(context);
        this.srcRect = new Rect();
        this.destRect = new Rect();
        this.paint = new Paint();
        this.shadowView = new ImageView(context);
        this.shadowView.setVisibility(4);
        this.shadowView.setScaleType(ScaleType.FIT_XY);
        this.shadowView.setImageResource(C0338R.drawable.bottom_shadow);
        addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0f));
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2471H;
            if (AdvanceTheme.f2472I) {
                addView(this.avatarImageView, LayoutHelper.createFrame(i, (float) i, 81, 0.0f, 0.0f, 0.0f, 67.0f));
            } else {
                addView(this.avatarImageView, LayoutHelper.createFrame(i, (float) i, 83, 16.0f, 0.0f, 0.0f, 67.0f));
            }
        } else {
            addView(this.avatarImageView, LayoutHelper.createFrame(64, 64.0f, 83, 16.0f, 0.0f, 0.0f, 67.0f));
            setBackgroundColor(ThemeUtil.m2485a().m2292f());
        }
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(1, 15.0f);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(3);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 28.0f));
        this.phoneTextView = new TextView(context);
        this.phoneTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.phoneTextView.setTextColor(-4004353);
        this.phoneTextView.setTextSize(1, 13.0f);
        this.phoneTextView.setLines(1);
        this.phoneTextView.setMaxLines(1);
        this.phoneTextView.setSingleLine(true);
        this.phoneTextView.setGravity(3);
        addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 9.0f));
        this.cloudView = new CloudView(context);
        if (!UserConfig.isRobot) {
            addView(this.cloudView, LayoutHelper.createFrame(61, 61, 85));
        }
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            setBackgroundColor(AdvanceTheme.f2474K);
            int i = AdvanceTheme.f2475L;
            if (i > 0) {
                Orientation orientation;
                switch (i) {
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
                int i2 = AdvanceTheme.f2476M;
                setBackgroundDrawable(new GradientDrawable(orientation, new int[]{r1, i2}));
            }
            this.nameTextView.setTextColor(AdvanceTheme.f2477N);
            this.nameTextView.setTextSize(1, (float) AdvanceTheme.f2478O);
            this.phoneTextView.setTextColor(AdvanceTheme.f2479P);
            this.phoneTextView.setTextSize(1, (float) AdvanceTheme.f2480Q);
            User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
            TLObject tLObject = null;
            if (!(user == null || user.photo == null || user.photo.photo_small == null)) {
                tLObject = user.photo.photo_small;
            }
            Drawable avatarDrawable = new AvatarDrawable(user);
            avatarDrawable.setColor(AdvanceTheme.f2481R);
            int dp = AndroidUtilities.dp((float) AdvanceTheme.f2482S);
            avatarDrawable.setRadius(dp);
            this.avatarImageView.getImageReceiver().setRoundRadius(dp);
            this.avatarImageView.setImage(tLObject, "50_50", avatarDrawable);
        }
    }

    public void invalidate() {
        super.invalidate();
        this.cloudView.invalidate();
    }

    protected void onDraw(Canvas canvas) {
        int i = 4;
        Drawable cachedWallpaper = ApplicationLoader.getCachedWallpaper();
        int serviceMessageColor = ApplicationLoader.getServiceMessageColor();
        if (this.currentColor != serviceMessageColor) {
            this.currentColor = serviceMessageColor;
            this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(serviceMessageColor | Theme.MSG_TEXT_COLOR, Mode.MULTIPLY));
        }
        if (!ApplicationLoader.isCustomTheme() || cachedWallpaper == null) {
            this.shadowView.setVisibility(4);
            this.phoneTextView.setTextColor(-4004353);
            super.onDraw(canvas);
        } else {
            this.phoneTextView.setTextColor(-1);
            this.shadowView.setVisibility(0);
            if (ThemeUtil.m2490b()) {
                if (!AdvanceTheme.f2473J) {
                    i = 0;
                }
                this.shadowView.setVisibility(i);
            }
            if (cachedWallpaper instanceof ColorDrawable) {
                cachedWallpaper.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                cachedWallpaper.draw(canvas);
            } else if (cachedWallpaper instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) cachedWallpaper).getBitmap();
                float measuredWidth = ((float) getMeasuredWidth()) / ((float) bitmap.getWidth());
                float measuredHeight = ((float) getMeasuredHeight()) / ((float) bitmap.getHeight());
                if (measuredWidth >= measuredHeight) {
                    measuredHeight = measuredWidth;
                }
                i = (int) (((float) getMeasuredWidth()) / measuredHeight);
                int measuredHeight2 = (int) (((float) getMeasuredHeight()) / measuredHeight);
                int width = (bitmap.getWidth() - i) / 2;
                int height = (bitmap.getHeight() - measuredHeight2) / 2;
                this.srcRect.set(width, height, i + width, measuredHeight2 + height);
                this.destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                canvas.drawBitmap(bitmap, this.srcRect, this.destRect, this.paint);
            }
        }
        initTheme();
    }

    protected void onMeasure(int i, int i2) {
        if (VERSION.SDK_INT >= 21) {
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f) + AndroidUtilities.statusBarHeight, C0700C.ENCODING_PCM_32BIT));
            return;
        }
        try {
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), C0700C.ENCODING_PCM_32BIT));
        } catch (Throwable e) {
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(148.0f));
            FileLog.m18e("tmessages", e);
        }
    }

    public void refreshAvatar(int i, int i2) {
        if (ThemeUtil.m2490b()) {
            removeView(this.avatarImageView);
            removeView(this.nameTextView);
            removeView(this.phoneTextView);
            this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp((float) i2));
            if (AdvanceTheme.f2472I) {
                addView(this.avatarImageView, LayoutHelper.createFrame(i, (float) i, 81, 0.0f, 0.0f, 0.0f, 67.0f));
                this.nameTextView.setGravity(17);
                addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 28.0f));
                this.phoneTextView.setGravity(17);
                addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 9.0f));
                return;
            }
            addView(this.avatarImageView, LayoutHelper.createFrame(i, (float) i, 83, 16.0f, 0.0f, 0.0f, 67.0f));
            this.nameTextView.setGravity(3);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 16.0f, 28.0f));
            this.phoneTextView.setGravity(3);
            addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 16.0f, 9.0f));
        }
    }

    public void setUser(User user) {
        if (user != null) {
            TLObject tLObject = null;
            if (user.photo != null) {
                tLObject = user.photo.photo_small;
            }
            this.nameTextView.setText(UserObject.getUserName(user));
            this.phoneTextView.setText(PhoneFormat.getInstance().format("+" + user.phone));
            Drawable avatarDrawable = new AvatarDrawable(user);
            avatarDrawable.setColor(Theme.ACTION_BAR_MAIN_AVATAR_COLOR);
            this.avatarImageView.setImage(tLObject, "50_50", avatarDrawable);
            if (MoboConstants.ag || UserConfig.isRobot) {
                this.phoneTextView.setVisibility(8);
            } else {
                this.phoneTextView.setVisibility(0);
            }
            initTheme();
        }
    }
}
