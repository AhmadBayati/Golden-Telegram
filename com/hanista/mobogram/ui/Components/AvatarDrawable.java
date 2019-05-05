package com.hanista.mobogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class AvatarDrawable extends Drawable {
    private static int[] arrColors;
    private static int[] arrColorsButtons;
    private static int[] arrColorsNames;
    private static int[] arrColorsProfiles;
    private static int[] arrColorsProfilesBack;
    private static int[] arrColorsProfilesText;
    private static Drawable broadcastDrawable;
    private static TextPaint namePaint;
    private static TextPaint namePaintSmall;
    private static Paint paint;
    private static Drawable photoDrawable;
    private int color;
    private boolean drawBrodcast;
    private boolean drawPhoto;
    private boolean isProfile;
    private int radius;
    private boolean smallStyle;
    private StringBuilder stringBuilder;
    private float textHeight;
    private StaticLayout textLayout;
    private float textLeft;
    private float textWidth;

    static {
        paint = new Paint(1);
        arrColors = new int[]{-1743531, -881592, -7436818, -8992691, -10502443, -11232035, -7436818, -887654};
        arrColorsProfiles = new int[]{-2592923, -615071, -7570990, -9981091, -11099461, Theme.ACTION_BAR_MAIN_AVATAR_COLOR, -7570990, -819290};
        arrColorsProfilesBack = new int[]{-3514282, -947900, -8557884, -11099828, -12283220, Theme.ACTION_BAR_PROFILE_COLOR, -8557884, -11762506};
        arrColorsProfilesText = new int[]{-406587, -139832, -3291923, -4133446, -4660496, Theme.ACTION_BAR_PROFILE_SUBTITLE_COLOR, -3291923, -4990985};
        arrColorsNames = new int[]{-3516848, -2589911, -11627828, -11488718, -12406360, -11627828, -11627828, -11627828};
        arrColorsButtons = new int[]{Theme.ACTION_BAR_RED_SELECTOR_COLOR, Theme.ACTION_BAR_ORANGE_SELECTOR_COLOR, Theme.ACTION_BAR_VIOLET_SELECTOR_COLOR, Theme.ACTION_BAR_GREEN_SELECTOR_COLOR, Theme.ACTION_BAR_CYAN_SELECTOR_COLOR, Theme.ACTION_BAR_BLUE_SELECTOR_COLOR, Theme.ACTION_BAR_VIOLET_SELECTOR_COLOR, Theme.ACTION_BAR_BLUE_SELECTOR_COLOR};
    }

    public AvatarDrawable() {
        this.stringBuilder = new StringBuilder(5);
        if (namePaint == null) {
            namePaint = new TextPaint(1);
            namePaint.setColor(-1);
            namePaintSmall = new TextPaint(1);
            namePaintSmall.setColor(-1);
            broadcastDrawable = ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.broadcast_w);
        }
        namePaint.setTextSize((float) AndroidUtilities.dp(20.0f));
        namePaintSmall.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.radius = 32;
    }

    public AvatarDrawable(Chat chat) {
        this(chat, false);
    }

    public AvatarDrawable(Chat chat, boolean z) {
        this();
        this.isProfile = z;
        if (chat != null) {
            setInfo(chat.id, chat.title, null, chat.id < 0, null);
        }
    }

    public AvatarDrawable(User user) {
        this(user, false);
    }

    public AvatarDrawable(User user, boolean z) {
        this();
        this.isProfile = z;
        if (user != null) {
            setInfo(user.id, user.first_name, user.last_name, false, null);
        }
    }

    public static int getButtonColorForId(int i) {
        return arrColorsButtons[getColorIndex(i)];
    }

    public static int getColorForId(int i) {
        return arrColors[getColorIndex(i)];
    }

    public static int getColorIndex(int i) {
        return (i < 0 || i >= 8) ? Math.abs(i % arrColors.length) : i;
    }

    public static int getNameColorForId(int i) {
        return arrColorsNames[getColorIndex(i)];
    }

    public static int getProfileBackColorForId(int i) {
        return arrColorsProfilesBack[getColorIndex(i)];
    }

    public static int getProfileColorForId(int i) {
        return arrColorsProfiles[getColorIndex(i)];
    }

    public static int getProfileTextColorForId(int i) {
        return arrColorsProfilesText[getColorIndex(i)];
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds != null) {
            int radius;
            int width = bounds.width();
            paint.setColor(this.color);
            canvas.save();
            canvas.translate((float) bounds.left, (float) bounds.top);
            if (ThemeUtil.m2490b()) {
                RectF rectF = new RectF(new Rect(0, 0, width, width));
                radius = getRadius();
                canvas.drawRoundRect(rectF, (float) radius, (float) radius, paint);
            } else {
                canvas.drawCircle((float) (width / 2), (float) (width / 2), (float) (width / 2), paint);
            }
            if (this.drawBrodcast && broadcastDrawable != null) {
                radius = (width - broadcastDrawable.getIntrinsicWidth()) / 2;
                width = (width - broadcastDrawable.getIntrinsicHeight()) / 2;
                broadcastDrawable.setBounds(radius, width, broadcastDrawable.getIntrinsicWidth() + radius, broadcastDrawable.getIntrinsicHeight() + width);
                broadcastDrawable.draw(canvas);
            } else if (this.textLayout != null) {
                canvas.translate(((((float) width) - this.textWidth) / 2.0f) - this.textLeft, (((float) width) - this.textHeight) / 2.0f);
                this.textLayout.draw(canvas);
            } else if (this.drawPhoto && photoDrawable != null) {
                radius = (width - photoDrawable.getIntrinsicWidth()) / 2;
                width = (width - photoDrawable.getIntrinsicHeight()) / 2;
                photoDrawable.setBounds(radius, width, photoDrawable.getIntrinsicWidth() + radius, photoDrawable.getIntrinsicHeight() + width);
                photoDrawable.draw(canvas);
            }
            canvas.restore();
        }
    }

    public int getIntrinsicHeight() {
        return 0;
    }

    public int getIntrinsicWidth() {
        return 0;
    }

    public int getOpacity() {
        return -2;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setAlpha(int i) {
    }

    public void setColor(int i) {
        this.color = i;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setDrawPhoto(boolean z) {
        if (z && photoDrawable == null) {
            photoDrawable = ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.photo_w);
        }
        this.drawPhoto = z;
    }

    public void setInfo(int i, String str, String str2, boolean z) {
        setInfo(i, str, str2, z, null);
    }

    public void setInfo(int i, String str, String str2, boolean z, String str3) {
        if (this.isProfile) {
            this.color = arrColorsProfiles[getColorIndex(i)];
        } else {
            this.color = arrColors[getColorIndex(i)];
        }
        this.drawBrodcast = z;
        if (str == null || str.length() == 0) {
            str = str2;
            str2 = null;
        }
        this.stringBuilder.setLength(0);
        if (str3 != null) {
            this.stringBuilder.append(str3);
        } else {
            if (str != null && str.length() > 0) {
                this.stringBuilder.append(str.substring(0, 1));
            }
            int length;
            if (str2 != null && str2.length() > 0) {
                length = str2.length() - 1;
                String str4 = null;
                while (length >= 0 && (str4 == null || str2.charAt(length) != ' ')) {
                    str4 = str2.substring(length, length + 1);
                    length--;
                }
                if (VERSION.SDK_INT >= 16) {
                    this.stringBuilder.append("\u200c");
                }
                this.stringBuilder.append(str4);
            } else if (str != null && str.length() > 0) {
                length = str.length() - 1;
                while (length >= 0) {
                    if (str.charAt(length) != ' ' || length == str.length() - 1 || str.charAt(length + 1) == ' ') {
                        length--;
                    } else {
                        if (VERSION.SDK_INT >= 16) {
                            this.stringBuilder.append("\u200c");
                        }
                        this.stringBuilder.append(str.substring(length + 1, length + 2));
                    }
                }
            }
        }
        if (this.stringBuilder.length() > 0) {
            try {
                this.textLayout = new StaticLayout(this.stringBuilder.toString().toUpperCase(), this.smallStyle ? namePaintSmall : namePaint, AndroidUtilities.dp(100.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                if (this.textLayout.getLineCount() > 0) {
                    this.textLeft = this.textLayout.getLineLeft(0);
                    this.textWidth = this.textLayout.getLineWidth(0);
                    this.textHeight = (float) this.textLayout.getLineBottom(0);
                    return;
                }
                return;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                return;
            }
        }
        this.textLayout = null;
    }

    public void setInfo(Chat chat) {
        if (chat != null) {
            setInfo(chat.id, chat.title, null, chat.id < 0, null);
        }
    }

    public void setInfo(User user) {
        if (user != null) {
            setInfo(user.id, user.first_name, user.last_name, false, null);
        }
    }

    public void setProfile(boolean z) {
        this.isProfile = z;
    }

    public void setRadius(int i) {
        this.radius = i;
    }

    public void setSmallStyle(boolean z) {
        this.smallStyle = z;
    }
}
