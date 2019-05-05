package com.hanista.mobogram.ui.ActionBar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.mobo.p003d.ImageListActivity;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import java.lang.reflect.Array;

public class Theme {
    public static final int ACTION_BAR_ACTION_MODE_TEXT_COLOR = -9211021;
    public static final int ACTION_BAR_AUDIO_SELECTOR_COLOR = 788529152;
    public static final int ACTION_BAR_BLUE_SELECTOR_COLOR;
    public static final int ACTION_BAR_CHANNEL_INTRO_COLOR = -1;
    public static final int ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR = 788529152;
    public static final int ACTION_BAR_COLOR;
    public static final int ACTION_BAR_CYAN_SELECTOR_COLOR = -13007715;
    public static final int ACTION_BAR_GREEN_SELECTOR_COLOR = -12020419;
    public static final int ACTION_BAR_MAIN_AVATAR_COLOR;
    public static final int ACTION_BAR_MEDIA_PICKER_COLOR = -13421773;
    public static final int ACTION_BAR_MODE_SELECTOR_COLOR = -986896;
    public static final int ACTION_BAR_ORANGE_SELECTOR_COLOR = -1674199;
    public static final int ACTION_BAR_PHOTO_VIEWER_COLOR = 2130706432;
    public static final int ACTION_BAR_PICKER_SELECTOR_COLOR = -12763843;
    public static final int ACTION_BAR_PINK_SELECTOR_COLOR = -2863493;
    public static final int ACTION_BAR_PLAYER_COLOR = -1;
    public static final int ACTION_BAR_PROFILE_COLOR;
    public static final int ACTION_BAR_PROFILE_SUBTITLE_COLOR = -2626822;
    public static final int ACTION_BAR_RED_SELECTOR_COLOR = -4437183;
    public static final int ACTION_BAR_SELECTOR_COLOR;
    public static final int ACTION_BAR_SUBTITLE_COLOR = -2758409;
    public static final int ACTION_BAR_TITLE_COLOR = -1;
    public static final int ACTION_BAR_VIDEO_EDIT_COLOR = -16777216;
    public static final int ACTION_BAR_VIOLET_SELECTOR_COLOR = -9216066;
    public static final int ACTION_BAR_WHITE_SELECTOR_COLOR = 1090519039;
    public static final int ACTION_BAR_YELLOW_SELECTOR_COLOR = -1073399;
    public static final int ALERT_PANEL_MESSAGE_TEXT_COLOR = -6710887;
    public static final int ALERT_PANEL_NAME_TEXT_COLOR = -12940081;
    public static final int ATTACH_SHEET_TEXT_COLOR = -9079435;
    public static final int AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR;
    public static final int CHAT_ADD_CONTACT_TEXT_COLOR = -11894091;
    public static final int CHAT_BOTTOM_CHAT_OVERLAY_TEXT_COLOR = -12940081;
    public static final int CHAT_BOTTOM_OVERLAY_TEXT_COLOR = -8421505;
    public static final int CHAT_EMPTY_VIEW_TEXT_COLOR = -1;
    public static final int CHAT_GIF_HINT_TEXT_COLOR = -1;
    public static final int CHAT_REPORT_SPAM_TEXT_COLOR = -3188393;
    public static final int CHAT_SEARCH_COUNT_TEXT_COLOR = -11625772;
    public static final int CHAT_UNREAD_TEXT_COLOR = -11102772;
    public static final int DIALOGS_ATTACH_TEXT_COLOR = -11697229;
    public static final int DIALOGS_DRAFT_TEXT_COLOR = -2274503;
    public static final int DIALOGS_MESSAGE_TEXT_COLOR = -7368817;
    public static final int DIALOGS_NAME_TEXT_COLOR = -11697229;
    public static final int DIALOGS_PRINTING_TEXT_COLOR = -11697229;
    public static final int INAPP_PLAYER_BACKGROUND_COLOR = -1;
    public static final int INAPP_PLAYER_PERFORMER_TEXT_COLOR = -13683656;
    public static final int INAPP_PLAYER_TITLE_TEXT_COLOR = -13683656;
    public static final int INPUT_FIELD_SELECTOR_COLOR = -2697514;
    public static final int JOIN_SHEET_COUNT_TEXT_COLOR = -6710887;
    public static final int JOIN_SHEET_NAME_TEXT_COLOR = -14606047;
    public static final int MSG_BOT_BUTTON_TEXT_COLOR = -1;
    public static final int MSG_BOT_PROGRESS_COLOR = -1;
    public static final int MSG_IN_AUDIO_DURATION_SELECTED_TEXT_COLOR = -7752511;
    public static final int MSG_IN_AUDIO_DURATION_TEXT_COLOR = -6182221;
    public static final int MSG_IN_AUDIO_PERFORMER_TEXT_COLOR = -13683656;
    public static final int MSG_IN_AUDIO_PROGRESS_COLOR = -1;
    public static final int MSG_IN_AUDIO_SEEKBAR_COLOR = -1774864;
    public static final int MSG_IN_AUDIO_SEEKBAR_FILL_COLOR = -9259544;
    public static final int MSG_IN_AUDIO_SEEKBAR_SELECTED_COLOR = -4399384;
    public static final int MSG_IN_AUDIO_SELECTED_PROGRESS_COLOR = -1902337;
    public static final int MSG_IN_AUDIO_TITLE_TEXT_COLOR = -11625772;
    public static final int MSG_IN_CONTACT_NAME_TEXT_COLOR = -11625772;
    public static final int MSG_IN_CONTACT_PHONE_TEXT_COLOR = -13683656;
    public static final int MSG_IN_FILE_BACKGROUND_COLOR = -1314571;
    public static final int MSG_IN_FILE_BACKGROUND_SELECTED_COLOR = -3413258;
    public static final int MSG_IN_FILE_INFO_SELECTED_TEXT_COLOR = -7752511;
    public static final int MSG_IN_FILE_INFO_TEXT_COLOR = -6182221;
    public static final int MSG_IN_FILE_NAME_TEXT_COLOR = -11625772;
    public static final int MSG_IN_FILE_PROGRESS_COLOR = -1314571;
    public static final int MSG_IN_FILE_PROGRESS_SELECTED_COLOR = -3413258;
    public static final int MSG_IN_FORDWARDED_NAME_TEXT_COLOR = -13072697;
    public static final int MSG_IN_REPLY_LINE_COLOR = -9390872;
    public static final int MSG_IN_REPLY_MEDIA_MESSAGE_SELETED_TEXT_COLOR = -7752511;
    public static final int MSG_IN_REPLY_MEDIA_MESSAGE_TEXT_COLOR = -6182221;
    public static final int MSG_IN_REPLY_MESSAGE_TEXT_COLOR = -16777216;
    public static final int MSG_IN_REPLY_NAME_TEXT_COLOR = -12940081;
    public static final int MSG_IN_SITE_NAME_TEXT_COLOR = -12940081;
    public static final int MSG_IN_TIME_SELECTED_TEXT_COLOR = -7752511;
    public static final int MSG_IN_TIME_TEXT_COLOR = -6182221;
    public static final int MSG_IN_VENUE_INFO_SELECTED_TEXT_COLOR = -7752511;
    public static final int MSG_IN_VENUE_INFO_TEXT_COLOR = -6182221;
    public static final int MSG_IN_VENUE_NAME_TEXT_COLOR = -11625772;
    public static final int MSG_IN_VIA_BOT_NAME_TEXT_COLOR = -12940081;
    public static final int MSG_IN_VOICE_SEEKBAR_COLOR = -2169365;
    public static final int MSG_IN_VOICE_SEEKBAR_FILL_COLOR = -9259544;
    public static final int MSG_IN_VOICE_SEEKBAR_SELECTED_COLOR = -4399384;
    public static final int MSG_IN_WEB_PREVIEW_LINE_COLOR = -9390872;
    public static final int MSG_LINK_SELECT_BACKGROUND_COLOR = 862104035;
    public static final int MSG_LINK_TEXT_COLOR = -14255946;
    public static final int MSG_MEDIA_INFO_TEXT_COLOR = -1;
    public static final int MSG_MEDIA_PROGRESS_COLOR = -1;
    public static final int MSG_MEDIA_TIME_TEXT_COLOR = -1;
    public static final int MSG_OUT_AUDIO_DURATION_SELECTED_TEXT_COLOR = -10112933;
    public static final int MSG_OUT_AUDIO_DURATION_TEXT_COLOR = -10112933;
    public static final int MSG_OUT_AUDIO_PERFORMER_TEXT_COLOR = -13286860;
    public static final int MSG_OUT_AUDIO_PROGRESS_COLOR = -1048610;
    public static final int MSG_OUT_AUDIO_SEEKBAR_COLOR = -4463700;
    public static final int MSG_OUT_AUDIO_SEEKBAR_FILL_COLOR = -8863118;
    public static final int MSG_OUT_AUDIO_SEEKBAR_SELECTED_COLOR = -5644906;
    public static final int MSG_OUT_AUDIO_SELECTED_PROGRESS_COLOR = -2820676;
    public static final int MSG_OUT_AUDIO_TITLE_TEXT_COLOR = -11162801;
    public static final int MSG_OUT_CONTACT_NAME_TEXT_COLOR = -11162801;
    public static final int MSG_OUT_CONTACT_PHONE_TEXT_COLOR = -13286860;
    public static final int MSG_OUT_FILE_BACKGROUND_COLOR = -2427453;
    public static final int MSG_OUT_FILE_BACKGROUND_SELECTED_COLOR = -3806041;
    public static final int MSG_OUT_FILE_INFO_SELECTED_TEXT_COLOR = -10112933;
    public static final int MSG_OUT_FILE_INFO_TEXT_COLOR = -10112933;
    public static final int MSG_OUT_FILE_NAME_TEXT_COLOR = -11162801;
    public static final int MSG_OUT_FILE_PROGRESS_COLOR = -2427453;
    public static final int MSG_OUT_FILE_PROGRESS_SELECTED_COLOR = -3806041;
    public static final int MSG_OUT_FORDWARDED_NAME_TEXT_COLOR = -11162801;
    public static final int MSG_OUT_REPLY_LINE_COLOR = -7812741;
    public static final int MSG_OUT_REPLY_MEDIA_MESSAGE_SELETED_TEXT_COLOR = -10112933;
    public static final int MSG_OUT_REPLY_MEDIA_MESSAGE_TEXT_COLOR = -10112933;
    public static final int MSG_OUT_REPLY_MESSAGE_TEXT_COLOR = -16777216;
    public static final int MSG_OUT_REPLY_NAME_TEXT_COLOR = -11162801;
    public static final int MSG_OUT_SITE_NAME_TEXT_COLOR = -11162801;
    public static final int MSG_OUT_TIME_SELECTED_TEXT_COLOR = -9391780;
    public static final int MSG_OUT_TIME_TEXT_COLOR = -9391780;
    public static final int MSG_OUT_VENUE_INFO_SELECTED_TEXT_COLOR = -10112933;
    public static final int MSG_OUT_VENUE_INFO_TEXT_COLOR = -10112933;
    public static final int MSG_OUT_VENUE_NAME_TEXT_COLOR = -11162801;
    public static final int MSG_OUT_VIA_BOT_NAME_TEXT_COLOR = -11162801;
    public static final int MSG_OUT_VOICE_SEEKBAR_COLOR = -4463700;
    public static final int MSG_OUT_VOICE_SEEKBAR_FILL_COLOR = -8863118;
    public static final int MSG_OUT_VOICE_SEEKBAR_SELECTED_COLOR = -5644906;
    public static final int MSG_OUT_WEB_PREVIEW_LINE_COLOR = -7812741;
    public static final int MSG_SECRET_TIME_TEXT_COLOR = -1776928;
    public static final int MSG_SELECTED_BACKGROUND_COLOR = 1714664933;
    public static final int MSG_STICKER_NAME_TEXT_COLOR = -1;
    public static final int MSG_STICKER_REPLY_LINE_COLOR = -1;
    public static final int MSG_STICKER_REPLY_MESSAGE_TEXT_COLOR = -1;
    public static final int MSG_STICKER_REPLY_NAME_TEXT_COLOR = -1;
    public static final int MSG_STICKER_VIA_BOT_NAME_TEXT_COLOR = -1;
    public static final int MSG_TEXT_COLOR = -16777216;
    public static final int MSG_TEXT_SELECT_BACKGROUND_COLOR = 1717742051;
    public static final int MSG_WEB_PREVIEW_DURATION_TEXT_COLOR = -1;
    public static final int MSG_WEB_PREVIEW_GAME_TEXT_COLOR = -1;
    public static final int PINNED_PANEL_MESSAGE_TEXT_COLOR = -6710887;
    public static final int PINNED_PANEL_NAME_TEXT_COLOR = -12940081;
    public static final int REPLY_PANEL_MESSAGE_TEXT_COLOR = -14540254;
    public static final int REPLY_PANEL_NAME_TEXT_COLOR = -12940081;
    public static final int SECRET_CHAT_INFO_TEXT_COLOR = -1;
    public static final int SHARE_SHEET_BADGE_TEXT_COLOR = -1;
    public static final int SHARE_SHEET_COPY_TEXT_COLOR = -12940081;
    public static final int SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR = -6842473;
    public static final int SHARE_SHEET_EDIT_TEXT_COLOR = -14606047;
    public static final int SHARE_SHEET_SEND_DISABLED_TEXT_COLOR;
    public static final int SHARE_SHEET_SEND_TEXT_COLOR;
    public static final int STICKERS_SHEET_ADD_TEXT_COLOR = -12940081;
    public static final int STICKERS_SHEET_CLOSE_TEXT_COLOR = -12940081;
    public static final int STICKERS_SHEET_REMOVE_TEXT_COLOR = -3319206;
    public static final int STICKERS_SHEET_SEND_TEXT_COLOR = -12940081;
    public static final int STICKERS_SHEET_TITLE_TEXT_COLOR = -14606047;
    public static Drawable[] attachButtonDrawables;
    public static Drawable backgroundBluePressed;
    public static Drawable backgroundDrawableIn;
    public static Drawable backgroundDrawableInSelected;
    public static Drawable backgroundDrawableOut;
    public static Drawable backgroundDrawableOutSelected;
    public static Drawable backgroundMediaDrawableIn;
    public static Drawable backgroundMediaDrawableInSelected;
    public static Drawable backgroundMediaDrawableOut;
    public static Drawable backgroundMediaDrawableOutSelected;
    public static Drawable botInline;
    public static Drawable botLink;
    public static Drawable broadcastDrawable;
    public static Drawable broadcastMediaDrawable;
    public static Drawable checkDrawable;
    public static Drawable checkMediaDrawable;
    public static Drawable[] clockChannelDrawable;
    public static Drawable clockDrawable;
    public static Drawable clockMediaDrawable;
    public static PorterDuffColorFilter colorFilter;
    public static PorterDuffColorFilter colorPressedFilter;
    public static Drawable[] contactDrawable;
    public static Drawable[] cornerInner;
    public static Drawable[] cornerOuter;
    private static int currentColor;
    public static Drawable[] docMenuDrawable;
    public static Drawable errorDrawable;
    public static Drawable favoriteDoneIconDrawable;
    public static Drawable favoriteDrawable;
    public static Drawable favoriteIconDrawable;
    public static Drawable[][] fileStatesDrawable;
    public static Drawable geoInDrawable;
    public static Drawable geoOutDrawable;
    public static Drawable halfCheckDrawable;
    public static Drawable halfCheckMediaDrawable;
    public static Drawable inlineAudioDrawable;
    public static Drawable inlineDocDrawable;
    public static Drawable inlineLocationDrawable;
    public static Drawable markerDrawable;
    private static Paint maskPaint;
    public static Drawable menuDrawable;
    public static Drawable menuIconDrawable;
    public static Drawable[][] photoStatesDrawables;
    public static Drawable replyIconDrawable;
    public static Drawable shareDrawable;
    public static Drawable shareIconDrawable;
    public static Drawable systemDrawable;
    public static Drawable timeBackgroundDrawable;
    public static Drawable timeStickerBackgroundDrawable;
    public static Drawable[] viewsCountDrawable;
    public static Drawable viewsMediaCountDrawable;
    public static Drawable viewsOutCountDrawable;

    /* renamed from: com.hanista.mobogram.ui.ActionBar.Theme.1 */
    static class C09891 extends Drawable {
        C09891() {
        }

        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) AndroidUtilities.dp(18.0f), Theme.maskPaint);
        }

        public int getOpacity() {
            return Theme.SHARE_SHEET_SEND_TEXT_COLOR;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    static {
        ACTION_BAR_COLOR = ThemeUtil.m2485a().m2289c();
        ACTION_BAR_PROFILE_COLOR = ThemeUtil.m2485a().m2292f();
        ACTION_BAR_MAIN_AVATAR_COLOR = ThemeUtil.m2485a().m2289c();
        ACTION_BAR_SELECTOR_COLOR = ThemeUtil.m2485a().m2292f();
        ACTION_BAR_BLUE_SELECTOR_COLOR = ThemeUtil.m2485a().m2292f();
        AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR = ThemeUtil.m2485a().m2289c();
        SHARE_SHEET_SEND_TEXT_COLOR = ThemeUtil.m2485a().m2289c();
        SHARE_SHEET_SEND_DISABLED_TEXT_COLOR = ThemeUtil.m2485a().m2289c();
        clockChannelDrawable = new Drawable[2];
        cornerOuter = new Drawable[4];
        cornerInner = new Drawable[4];
        viewsCountDrawable = new Drawable[2];
        contactDrawable = new Drawable[2];
        fileStatesDrawable = (Drawable[][]) Array.newInstance(Drawable.class, new int[]{10, 2});
        photoStatesDrawables = (Drawable[][]) Array.newInstance(Drawable.class, new int[]{13, 2});
        docMenuDrawable = new Drawable[4];
        attachButtonDrawables = new Drawable[8];
        maskPaint = new Paint(1);
    }

    public static Drawable createBarSelectorDrawable(int i) {
        return createBarSelectorDrawable(i, true);
    }

    public static Drawable createBarSelectorDrawable(int i, boolean z) {
        Drawable c09891;
        if (VERSION.SDK_INT >= 21) {
            if (z) {
                maskPaint.setColor(SHARE_SHEET_BADGE_TEXT_COLOR);
                c09891 = new C09891();
            } else {
                c09891 = null;
            }
            return new RippleDrawable(new ColorStateList(new int[][]{new int[SHARE_SHEET_SEND_TEXT_COLOR]}, new int[]{i}), null, c09891);
        }
        c09891 = new StateListDrawable();
        c09891.addState(new int[]{16842919}, new ColorDrawable(i));
        c09891.addState(new int[]{16842908}, new ColorDrawable(i));
        c09891.addState(new int[]{16842913}, new ColorDrawable(i));
        c09891.addState(new int[]{16843518}, new ColorDrawable(i));
        c09891.addState(new int[SHARE_SHEET_SEND_TEXT_COLOR], new ColorDrawable(SHARE_SHEET_SEND_TEXT_COLOR));
        return c09891;
    }

    public static void loadChatResources(Context context) {
        if (attachButtonDrawables[SHARE_SHEET_SEND_TEXT_COLOR] == null) {
            attachButtonDrawables[SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.attach_camera_states);
            attachButtonDrawables[1] = context.getResources().getDrawable(C0338R.drawable.attach_gallery_states);
            attachButtonDrawables[2] = context.getResources().getDrawable(C0338R.drawable.attach_video_states);
            attachButtonDrawables[3] = context.getResources().getDrawable(C0338R.drawable.attach_audio_states);
            attachButtonDrawables[4] = context.getResources().getDrawable(C0338R.drawable.attach_file_states);
            attachButtonDrawables[5] = context.getResources().getDrawable(C0338R.drawable.attach_contact_states);
            attachButtonDrawables[6] = context.getResources().getDrawable(C0338R.drawable.attach_location_states);
            attachButtonDrawables[7] = context.getResources().getDrawable(C0338R.drawable.attach_hide_states);
        }
    }

    public static void loadRecources(Context context) {
        int i = SHARE_SHEET_SEND_TEXT_COLOR;
        if (backgroundDrawableIn == null) {
            backgroundDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in);
            backgroundDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_selected);
            backgroundDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out);
            backgroundDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_selected);
            backgroundMediaDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_photo);
            backgroundMediaDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_photo_selected);
            backgroundMediaDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_photo);
            backgroundMediaDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_photo_selected);
            checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check);
            halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck);
            clockDrawable = context.getResources().getDrawable(C0338R.drawable.msg_clock);
            checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w);
            halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w);
            clockMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_clock_photo);
            clockChannelDrawable[SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.msg_clock2);
            clockChannelDrawable[1] = context.getResources().getDrawable(C0338R.drawable.msg_clock2_s);
            errorDrawable = context.getResources().getDrawable(C0338R.drawable.msg_warning);
            timeBackgroundDrawable = context.getResources().getDrawable(C0338R.drawable.phototime2_b);
            timeStickerBackgroundDrawable = context.getResources().getDrawable(C0338R.drawable.phototime2);
            broadcastDrawable = context.getResources().getDrawable(C0338R.drawable.broadcast3);
            broadcastMediaDrawable = context.getResources().getDrawable(C0338R.drawable.broadcast4);
            systemDrawable = context.getResources().getDrawable(C0338R.drawable.system);
            botLink = context.getResources().getDrawable(C0338R.drawable.bot_link);
            botInline = context.getResources().getDrawable(C0338R.drawable.bot_lines);
            viewsCountDrawable[SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.post_views);
            viewsCountDrawable[1] = context.getResources().getDrawable(C0338R.drawable.post_views_s);
            viewsOutCountDrawable = context.getResources().getDrawable(C0338R.drawable.post_viewsg);
            viewsMediaCountDrawable = context.getResources().getDrawable(C0338R.drawable.post_views_w);
            fileStatesDrawable[SHARE_SHEET_SEND_TEXT_COLOR][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.play_g);
            fileStatesDrawable[SHARE_SHEET_SEND_TEXT_COLOR][1] = context.getResources().getDrawable(C0338R.drawable.play_g_s);
            fileStatesDrawable[1][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.pause_g);
            fileStatesDrawable[1][1] = context.getResources().getDrawable(C0338R.drawable.pause_g_s);
            fileStatesDrawable[2][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.file_g_load);
            fileStatesDrawable[2][1] = context.getResources().getDrawable(C0338R.drawable.file_g_load_s);
            fileStatesDrawable[3][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.file_g);
            fileStatesDrawable[3][1] = context.getResources().getDrawable(C0338R.drawable.file_g_s);
            fileStatesDrawable[4][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.file_g_cancel);
            fileStatesDrawable[4][1] = context.getResources().getDrawable(C0338R.drawable.file_g_cancel_s);
            fileStatesDrawable[5][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.play_b);
            fileStatesDrawable[5][1] = context.getResources().getDrawable(C0338R.drawable.play_b_s);
            fileStatesDrawable[6][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.pause_b);
            fileStatesDrawable[6][1] = context.getResources().getDrawable(C0338R.drawable.pause_b_s);
            fileStatesDrawable[7][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.file_b_load);
            fileStatesDrawable[7][1] = context.getResources().getDrawable(C0338R.drawable.file_b_load_s);
            fileStatesDrawable[8][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.file_b);
            fileStatesDrawable[8][1] = context.getResources().getDrawable(C0338R.drawable.file_b_s);
            fileStatesDrawable[9][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.file_b_cancel);
            fileStatesDrawable[9][1] = context.getResources().getDrawable(C0338R.drawable.file_b_cancel_s);
            photoStatesDrawables[SHARE_SHEET_SEND_TEXT_COLOR][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.photoload);
            photoStatesDrawables[SHARE_SHEET_SEND_TEXT_COLOR][1] = context.getResources().getDrawable(C0338R.drawable.photoload_pressed);
            photoStatesDrawables[1][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.photocancel);
            photoStatesDrawables[1][1] = context.getResources().getDrawable(C0338R.drawable.photocancel_pressed);
            photoStatesDrawables[2][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.photogif);
            photoStatesDrawables[2][1] = context.getResources().getDrawable(C0338R.drawable.photogif_pressed);
            photoStatesDrawables[3][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.playvideo);
            photoStatesDrawables[3][1] = context.getResources().getDrawable(C0338R.drawable.playvideo_pressed);
            Drawable[] drawableArr = photoStatesDrawables[4];
            Drawable[] drawableArr2 = photoStatesDrawables[4];
            Drawable drawable = context.getResources().getDrawable(C0338R.drawable.burn);
            drawableArr2[1] = drawable;
            drawableArr[SHARE_SHEET_SEND_TEXT_COLOR] = drawable;
            drawableArr = photoStatesDrawables[5];
            drawableArr2 = photoStatesDrawables[5];
            drawable = context.getResources().getDrawable(C0338R.drawable.circle);
            drawableArr2[1] = drawable;
            drawableArr[SHARE_SHEET_SEND_TEXT_COLOR] = drawable;
            drawableArr = photoStatesDrawables[6];
            drawableArr2 = photoStatesDrawables[6];
            drawable = context.getResources().getDrawable(C0338R.drawable.photocheck);
            drawableArr2[1] = drawable;
            drawableArr[SHARE_SHEET_SEND_TEXT_COLOR] = drawable;
            photoStatesDrawables[7][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.photoload_g);
            photoStatesDrawables[7][1] = context.getResources().getDrawable(C0338R.drawable.photoload_g_s);
            photoStatesDrawables[8][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.photocancel_g);
            photoStatesDrawables[8][1] = context.getResources().getDrawable(C0338R.drawable.photocancel_g_s);
            photoStatesDrawables[9][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.doc_green);
            photoStatesDrawables[9][1] = context.getResources().getDrawable(C0338R.drawable.doc_green);
            photoStatesDrawables[10][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.photoload_b);
            photoStatesDrawables[10][1] = context.getResources().getDrawable(C0338R.drawable.photoload_b_s);
            photoStatesDrawables[11][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.photocancel_b);
            photoStatesDrawables[11][1] = context.getResources().getDrawable(C0338R.drawable.photocancel_b_s);
            photoStatesDrawables[12][SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.doc_blue);
            photoStatesDrawables[12][1] = context.getResources().getDrawable(C0338R.drawable.doc_blue_s);
            docMenuDrawable[SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.doc_actions_b);
            docMenuDrawable[1] = context.getResources().getDrawable(C0338R.drawable.doc_actions_g);
            docMenuDrawable[2] = context.getResources().getDrawable(C0338R.drawable.doc_actions_b_s);
            docMenuDrawable[3] = context.getResources().getDrawable(C0338R.drawable.video_actions);
            contactDrawable[SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.contact_blue);
            contactDrawable[1] = context.getResources().getDrawable(C0338R.drawable.contact_green);
            shareDrawable = context.getResources().getDrawable(C0338R.drawable.share_round);
            shareIconDrawable = context.getResources().getDrawable(C0338R.drawable.share_arrow);
            geoInDrawable = context.getResources().getDrawable(C0338R.drawable.location_b);
            geoOutDrawable = context.getResources().getDrawable(C0338R.drawable.location_g);
            cornerOuter[SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.corner_out_tl);
            cornerOuter[1] = context.getResources().getDrawable(C0338R.drawable.corner_out_tr);
            cornerOuter[2] = context.getResources().getDrawable(C0338R.drawable.corner_out_br);
            cornerOuter[3] = context.getResources().getDrawable(C0338R.drawable.corner_out_bl);
            cornerInner[SHARE_SHEET_SEND_TEXT_COLOR] = context.getResources().getDrawable(C0338R.drawable.corner_in_tr);
            cornerInner[1] = context.getResources().getDrawable(C0338R.drawable.corner_in_tl);
            cornerInner[2] = context.getResources().getDrawable(C0338R.drawable.corner_in_br);
            cornerInner[3] = context.getResources().getDrawable(C0338R.drawable.corner_in_bl);
            inlineDocDrawable = context.getResources().getDrawable(C0338R.drawable.bot_file);
            inlineAudioDrawable = context.getResources().getDrawable(C0338R.drawable.bot_music);
            inlineLocationDrawable = context.getResources().getDrawable(C0338R.drawable.bot_location);
            markerDrawable = context.getResources().getDrawable(C0338R.drawable.marker);
            favoriteDrawable = context.getResources().getDrawable(C0338R.drawable.share_round);
            favoriteIconDrawable = context.getResources().getDrawable(C0338R.drawable.fav_ic);
            favoriteDoneIconDrawable = context.getResources().getDrawable(C0338R.drawable.fav_done_ic);
            menuDrawable = context.getResources().getDrawable(C0338R.drawable.share_round);
            menuIconDrawable = context.getResources().getDrawable(C0338R.drawable.menu_ic);
            replyIconDrawable = context.getResources().getDrawable(C0338R.drawable.reply_arrow);
            setBubbles(context);
            setChecks(context);
        }
        int serviceMessageColor = ApplicationLoader.getServiceMessageColor();
        if (currentColor != serviceMessageColor) {
            colorFilter = new PorterDuffColorFilter(serviceMessageColor, Mode.MULTIPLY);
            colorPressedFilter = new PorterDuffColorFilter(ApplicationLoader.getServiceSelectedMessageColor(), Mode.MULTIPLY);
            currentColor = serviceMessageColor;
            while (i < 4) {
                cornerOuter[i].setColorFilter(colorFilter);
                cornerInner[i].setColorFilter(colorFilter);
                i++;
            }
            timeStickerBackgroundDrawable.setColorFilter(colorFilter);
        }
    }

    public static void setBubbles(Context context) {
        if (ThemeUtil.m2490b()) {
            String str = AdvanceTheme.cc;
            if (str.equals(ImageListActivity.m525a((int) SHARE_SHEET_SEND_TEXT_COLOR))) {
                backgroundDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in);
                backgroundDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_selected);
                backgroundDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out);
                backgroundDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_selected);
                backgroundMediaDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_photo);
                backgroundMediaDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_photo_selected);
                backgroundMediaDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_photo);
                backgroundMediaDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_photo_selected);
            } else if (str.equals(ImageListActivity.m525a(1))) {
                backgroundDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_2);
                backgroundDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_2_selected);
                backgroundDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_2);
                backgroundDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_2_selected);
                backgroundMediaDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_2_photo);
                backgroundMediaDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_2_photo_selected);
                backgroundMediaDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_2_photo);
                backgroundMediaDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_2_photo_selected);
            } else if (str.equals(ImageListActivity.m525a(2))) {
                backgroundDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_3);
                backgroundDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_3_selected);
                backgroundDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_3);
                backgroundDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_3_selected);
                backgroundMediaDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_3_photo);
                backgroundMediaDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_3_photo_selected);
                backgroundMediaDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_3_photo);
                backgroundMediaDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_3_photo_selected);
            } else if (str.equals(ImageListActivity.m525a(3))) {
                backgroundDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_4);
                backgroundDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_4_selected);
                backgroundDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_4);
                backgroundDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_4_selected);
                backgroundMediaDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_4_photo);
                backgroundMediaDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_4_photo_selected);
                backgroundMediaDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_4_photo);
                backgroundMediaDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_4_photo_selected);
            } else if (str.equals(ImageListActivity.m525a(4))) {
                backgroundDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_5);
                backgroundDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_5_selected);
                backgroundDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_5);
                backgroundDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_5_selected);
                backgroundMediaDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_5_photo);
                backgroundMediaDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_5_photo_selected);
                backgroundMediaDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_5_photo);
                backgroundMediaDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_5_photo_selected);
            } else if (str.equals(ImageListActivity.m525a(5))) {
                backgroundDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_6);
                backgroundDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_6_selected);
                backgroundDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_6);
                backgroundDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_6_selected);
                backgroundMediaDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_6_photo);
                backgroundMediaDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_6_photo_selected);
                backgroundMediaDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_6_photo);
                backgroundMediaDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_6_photo_selected);
            } else if (str.equals(ImageListActivity.m525a(6))) {
                backgroundDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_7);
                backgroundDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_7_selected);
                backgroundDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_7);
                backgroundDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_7_selected);
                backgroundMediaDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_7_photo);
                backgroundMediaDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_7_photo_selected);
                backgroundMediaDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_7_photo);
                backgroundMediaDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_7_photo_selected);
            } else if (str.equals(ImageListActivity.m525a(7))) {
                backgroundDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_8);
                backgroundDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_8_selected);
                backgroundDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_8);
                backgroundDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_8_selected);
                backgroundMediaDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_8_photo);
                backgroundMediaDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_8_photo_selected);
                backgroundMediaDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_8_photo);
                backgroundMediaDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_8_photo_selected);
            } else {
                backgroundDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in);
                backgroundDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_selected);
                backgroundDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out);
                backgroundDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_selected);
                backgroundMediaDrawableIn = context.getResources().getDrawable(C0338R.drawable.msg_in_photo);
                backgroundMediaDrawableInSelected = context.getResources().getDrawable(C0338R.drawable.msg_in_photo_selected);
                backgroundMediaDrawableOut = context.getResources().getDrawable(C0338R.drawable.msg_out_photo);
                backgroundMediaDrawableOutSelected = context.getResources().getDrawable(C0338R.drawable.msg_out_photo_selected);
            }
        }
    }

    public static void setChecks(Context context) {
        if (ThemeUtil.m2490b()) {
            String str = AdvanceTheme.bD;
            if (str.equals(ImageListActivity.m528b(1))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_2);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_2);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w_2);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w_2);
            } else if (str.equals(ImageListActivity.m528b(2))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_3);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_3);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w_3);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w_3);
            } else if (str.equals(ImageListActivity.m528b(3))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_4);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_4);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w_4);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w_4);
            } else if (str.equals(ImageListActivity.m528b(4))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_5);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_5);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w_5);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w_5);
            } else if (str.equals(ImageListActivity.m528b(5))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_6);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_6);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w_6);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w_6);
            } else if (str.equals(ImageListActivity.m528b(6))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_7);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_7);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w_7);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w_7);
            } else if (str.equals(ImageListActivity.m528b(7))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_8);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_8);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w_8);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w_8);
            } else if (str.equals(ImageListActivity.m528b(8))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_9);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_9);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w_9);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w_9);
            } else if (str.equals(ImageListActivity.m528b(9))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_10);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_10);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w_10);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w_10);
            } else if (str.equals(ImageListActivity.m528b(10))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_11);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_11);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w_11);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w_11);
            } else if (str.equals(ImageListActivity.m528b(11))) {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_12);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_12);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w_12);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w_12);
            } else {
                checkDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check);
                halfCheckDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck);
                checkMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_check_w);
                halfCheckMediaDrawable = context.getResources().getDrawable(C0338R.drawable.msg_halfcheck_w);
            }
        }
    }
}
