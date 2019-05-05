package com.hanista.mobogram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.ViewStructure;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.ImageReceiver.ImageReceiverDelegate;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.FileDownloadProgressListener;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessageObject.TextLayoutBlock;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.SendMessagesHelper;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p000a.ArchiveUtil;
import com.hanista.mobogram.mobo.p005f.DialogSettings;
import com.hanista.mobogram.mobo.p005f.DialogSettingsUtil;
import com.hanista.mobogram.mobo.p007h.FavoriteUtil;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.KeyboardButton;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonCallback;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPage;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.WebPage;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.LinkPath;
import com.hanista.mobogram.ui.Components.RadialProgress;
import com.hanista.mobogram.ui.Components.SeekBar;
import com.hanista.mobogram.ui.Components.SeekBar.SeekBarDelegate;
import com.hanista.mobogram.ui.Components.SeekBarWaveform;
import com.hanista.mobogram.ui.Components.StaticLayoutEx;
import com.hanista.mobogram.ui.Components.TypefaceSpan;
import com.hanista.mobogram.ui.Components.URLSpanBotCommand;
import com.hanista.mobogram.ui.Components.URLSpanNoUnderline;
import com.hanista.mobogram.ui.PhotoViewer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ChatMessageCell extends BaseCell implements ImageReceiverDelegate, FileDownloadProgressListener, SeekBarDelegate {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static TextPaint audioPerformerPaint;
    private static TextPaint audioTimePaint;
    private static TextPaint audioTitlePaint;
    private static TextPaint botButtonPaint;
    private static Paint botProgressPaint;
    private static TextPaint contactNamePaint;
    private static TextPaint contactPhonePaint;
    private static Paint deleteProgressPaint;
    private static Paint docBackPaint;
    private static TextPaint docNamePaint;
    private static TextPaint durationPaint;
    private static TextPaint forwardNamePaint;
    private static TextPaint gamePaint;
    private static TextPaint infoPaint;
    private static TextPaint locationAddressPaint;
    private static TextPaint locationTitlePaint;
    private static TextPaint namePaint;
    private static Paint replyLinePaint;
    private static TextPaint replyNamePaint;
    private static TextPaint replyTextPaint;
    private static TextPaint timePaint;
    private static Paint urlPaint;
    private static Paint urlSelectionPaint;
    private int TAG;
    private boolean allowAssistant;
    private StaticLayout authorLayout;
    private int authorX;
    private int availableTimeWidth;
    private AvatarDrawable avatarDrawable;
    public ImageReceiver avatarImage;
    private boolean avatarPressed;
    private int backgroundDrawableLeft;
    private int backgroundWidth;
    private ArrayList<BotButton> botButtons;
    private HashMap<String, BotButton> botButtonsByData;
    private int buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private boolean cancelLoading;
    private int captionHeight;
    private StaticLayout captionLayout;
    private int captionX;
    private int captionY;
    private boolean changeShareWithReplyButton;
    private AvatarDrawable contactAvatarDrawable;
    private Drawable currentBackgroundDrawable;
    private Chat currentChat;
    private Chat currentForwardChannel;
    private String currentForwardNameString;
    private User currentForwardUser;
    private MessageObject currentMessageObject;
    private String currentNameString;
    private FileLocation currentPhoto;
    public String currentPhotoFilter;
    private String currentPhotoFilterThumb;
    public PhotoSize currentPhotoObject;
    public PhotoSize currentPhotoObjectThumb;
    private FileLocation currentReplyPhoto;
    private String currentTimeString;
    private String currentUrl;
    private User currentUser;
    private User currentViaBotUser;
    private String currentViewsString;
    private ChatMessageCellDelegate delegate;
    private RectF deleteProgressRect;
    private StaticLayout descriptionLayout;
    private int descriptionX;
    private int descriptionY;
    private DialogSettings dialogSettings;
    private boolean disallowLongPress;
    private StaticLayout docTitleLayout;
    private int docTitleOffsetX;
    private Document documentAttach;
    private int documentAttachType;
    private boolean drawBackground;
    public boolean drawFavoriteButton;
    private boolean drawForwardedName;
    private boolean drawImageButton;
    private boolean drawMarker;
    public boolean drawMenuButton;
    private boolean drawName;
    private boolean drawNameLayout;
    private boolean drawPhotoImage;
    public boolean drawShareButton;
    private boolean drawStatus;
    private boolean drawTime;
    private StaticLayout durationLayout;
    private int durationWidth;
    private int extraHeight;
    private boolean favoritePressed;
    public int favoriteStartX;
    public int favoriteStartY;
    private int firstVisibleBlockNum;
    private boolean forwardBotPressed;
    private boolean forwardName;
    private float[] forwardNameOffsetX;
    private boolean forwardNamePressed;
    private int forwardNameX;
    private int forwardNameY;
    private StaticLayout[] forwardedNameLayout;
    private int forwardedNameWidth;
    private boolean gamePreviewPressed;
    private boolean hasGamePreview;
    private boolean hasLinkPreview;
    private boolean imagePressed;
    private boolean inLayout;
    private StaticLayout infoLayout;
    private int infoWidth;
    public boolean isAvatarVisible;
    public boolean isChat;
    private boolean isCheckPressed;
    private boolean isHighlighted;
    private boolean isPressed;
    private boolean isSmallImage;
    private int keyboardHeight;
    private int lastDeleteDate;
    private int lastSendState;
    private String lastStatus;
    private String lastTimeString;
    private int lastViewsCount;
    private int lastVisibleBlockNum;
    private int layoutHeight;
    private int layoutWidth;
    private int linkBlockNum;
    private int linkPreviewHeight;
    private boolean linkPreviewPressed;
    private int linkSelectionBlockNum;
    private boolean mediaBackground;
    private int mediaOffsetY;
    private boolean menuPressed;
    private int menuStartX;
    private int menuStartY;
    private StaticLayout nameLayout;
    private float nameOffsetX;
    private int nameWidth;
    private float nameX;
    private float nameY;
    private int namesOffset;
    private boolean needNewVisiblePart;
    private boolean needReplyImage;
    private boolean otherPressed;
    private int otherX;
    private int otherY;
    private StaticLayout performerLayout;
    private int performerX;
    public ImageReceiver photoImage;
    private boolean photoNotSet;
    private int pressedBotButton;
    private ClickableSpan pressedLink;
    private int pressedLinkType;
    private RadialProgress radialProgress;
    private RectF rect;
    private ImageReceiver replyImageReceiver;
    private StaticLayout replyNameLayout;
    private float replyNameOffset;
    private int replyNameWidth;
    private boolean replyPressed;
    private int replyStartX;
    private int replyStartY;
    private StaticLayout replyTextLayout;
    private float replyTextOffset;
    private int replyTextWidth;
    private Rect scrollRect;
    private SeekBar seekBar;
    private SeekBarWaveform seekBarWaveform;
    private int seekBarX;
    private int seekBarY;
    private boolean sharePressed;
    public int shareStartX;
    public int shareStartY;
    private StaticLayout siteNameLayout;
    private StaticLayout songLayout;
    private int songX;
    private GradientDrawable statusBG;
    private int substractBackgroundHeight;
    private int textX;
    private int textY;
    private int timeAudioX;
    private StaticLayout timeLayout;
    private int timeTextWidth;
    private int timeWidth;
    private int timeWidthAudio;
    private int timeX;
    private StaticLayout titleLayout;
    private int titleX;
    private int totalHeight;
    private int totalVisibleBlocksCount;
    private ArrayList<LinkPath> urlPath;
    private ArrayList<LinkPath> urlPathCache;
    private ArrayList<LinkPath> urlPathSelection;
    private boolean useSeekBarWaweform;
    private int viaNameWidth;
    private int viaWidth;
    private StaticLayout videoInfoLayout;
    private StaticLayout viewsLayout;
    private int viewsTextWidth;
    private boolean wasLayout;
    private int widthForButtons;

    public interface ChatMessageCellDelegate {
        boolean canPerformActions();

        void didLongPressed(ChatMessageCell chatMessageCell);

        void didLongPressedUserAvatar(ChatMessageCell chatMessageCell, User user);

        void didPressedBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton);

        void didPressedCancelSendButton(ChatMessageCell chatMessageCell);

        void didPressedChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i);

        void didPressedFavorite(ChatMessageCell chatMessageCell);

        void didPressedImage(ChatMessageCell chatMessageCell);

        void didPressedMenu(ChatMessageCell chatMessageCell);

        void didPressedOther(ChatMessageCell chatMessageCell);

        void didPressedReplyMessage(ChatMessageCell chatMessageCell, int i);

        void didPressedShare(ChatMessageCell chatMessageCell);

        void didPressedUrl(MessageObject messageObject, ClickableSpan clickableSpan, boolean z);

        void didPressedUserAvatar(ChatMessageCell chatMessageCell, User user);

        void didPressedViaBot(ChatMessageCell chatMessageCell, String str);

        void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2);

        boolean needPlayAudio(MessageObject messageObject);
    }

    private class BotButton {
        private int angle;
        private KeyboardButton button;
        private int height;
        private long lastUpdateTime;
        private float progressAlpha;
        private StaticLayout title;
        private int width;
        private int f2677x;
        private int f2678y;

        private BotButton() {
        }
    }

    public ChatMessageCell(Context context) {
        super(context);
        this.scrollRect = new Rect();
        this.deleteProgressRect = new RectF();
        this.rect = new RectF();
        this.urlPathCache = new ArrayList();
        this.urlPath = new ArrayList();
        this.urlPathSelection = new ArrayList();
        this.botButtons = new ArrayList();
        this.botButtonsByData = new HashMap();
        this.isCheckPressed = true;
        this.drawBackground = true;
        this.backgroundWidth = 100;
        this.forwardedNameLayout = new StaticLayout[DOCUMENT_ATTACH_TYPE_GIF];
        this.forwardNameOffsetX = new float[DOCUMENT_ATTACH_TYPE_GIF];
        this.drawTime = true;
        this.lastStatus = TtmlNode.ANONYMOUS_REGION_ID;
        if (infoPaint == null) {
            infoPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            infoPaint.setTypeface(FontUtil.m1176a().m1161d());
            docNamePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            docNamePaint.setTypeface(FontUtil.m1176a().m1160c());
            docBackPaint = new Paint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            docBackPaint.setTypeface(FontUtil.m1176a().m1161d());
            deleteProgressPaint = new Paint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            deleteProgressPaint.setColor(Theme.MSG_SECRET_TIME_TEXT_COLOR);
            botProgressPaint = new Paint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            botProgressPaint.setColor(-1);
            botProgressPaint.setStrokeCap(Cap.ROUND);
            botProgressPaint.setStyle(Style.STROKE);
            locationTitlePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            locationTitlePaint.setTypeface(FontUtil.m1176a().m1160c());
            locationAddressPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            urlPaint = new Paint();
            urlPaint.setColor(Theme.MSG_LINK_SELECT_BACKGROUND_COLOR);
            urlSelectionPaint = new Paint();
            urlSelectionPaint.setColor(Theme.MSG_TEXT_SELECT_BACKGROUND_COLOR);
            audioTimePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            audioTimePaint.setTypeface(FontUtil.m1176a().m1161d());
            audioTitlePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            audioTitlePaint.setTypeface(FontUtil.m1176a().m1160c());
            audioPerformerPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            audioPerformerPaint.setTypeface(FontUtil.m1176a().m1161d());
            botButtonPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            botButtonPaint.setColor(-1);
            botButtonPaint.setTypeface(FontUtil.m1176a().m1160c());
            contactNamePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            contactNamePaint.setTypeface(FontUtil.m1176a().m1160c());
            contactPhonePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            contactPhonePaint.setTypeface(FontUtil.m1176a().m1161d());
            durationPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            durationPaint.setTypeface(FontUtil.m1176a().m1161d());
            durationPaint.setColor(-1);
            gamePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            gamePaint.setColor(-1);
            gamePaint.setTypeface(FontUtil.m1176a().m1160c());
            timePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            timePaint.setTypeface(FontUtil.m1176a().m1161d());
            namePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            namePaint.setTypeface(FontUtil.m1176a().m1160c());
            forwardNamePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            forwardNamePaint.setTypeface(FontUtil.m1176a().m1161d());
            replyNamePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            replyNamePaint.setTypeface(FontUtil.m1176a().m1160c());
            replyTextPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            replyTextPaint.setTypeface(FontUtil.m1176a().m1161d());
            replyTextPaint.linkColor = Theme.MSG_LINK_TEXT_COLOR;
            replyLinePaint = new Paint();
        }
        botProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        infoPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        docNamePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        locationTitlePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        locationAddressPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        audioTimePaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        audioTitlePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        audioPerformerPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        botButtonPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        contactNamePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        contactPhonePaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        durationPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        timePaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        namePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        forwardNamePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        replyNamePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        replyTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        gamePaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.avatarImage = new ImageReceiver(this);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        this.replyImageReceiver = new ImageReceiver(this);
        this.TAG = MediaController.m71a().m178g();
        this.contactAvatarDrawable = new AvatarDrawable();
        this.photoImage = new ImageReceiver(this);
        this.photoImage.setDelegate(this);
        this.radialProgress = new RadialProgress(this);
        this.seekBar = new SeekBar(context);
        this.seekBar.setDelegate(this);
        this.seekBarWaveform = new SeekBarWaveform(context);
        this.seekBarWaveform.setDelegate(this);
        this.seekBarWaveform.setParentView(this);
        this.radialProgress = new RadialProgress(this);
        this.statusBG = new GradientDrawable();
        this.statusBG.setColor(-7829368);
        this.statusBG.setCornerRadius((float) AndroidUtilities.dp(13.0f));
        this.statusBG.setStroke(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), -1);
        initTheme1();
    }

    private void calcBackgroundWidth(int i, int i2, int i3) {
        if (this.hasLinkPreview || this.hasGamePreview || i - this.currentMessageObject.lastLineWidth < i2) {
            this.totalHeight += AndroidUtilities.dp(14.0f);
            this.backgroundWidth = Math.max(i3, this.currentMessageObject.lastLineWidth) + AndroidUtilities.dp(31.0f);
            this.backgroundWidth = Math.max(this.backgroundWidth, this.timeWidth + AndroidUtilities.dp(31.0f));
            return;
        }
        int i4 = i3 - this.currentMessageObject.lastLineWidth;
        if (i4 < 0 || i4 > i2) {
            this.backgroundWidth = Math.max(i3, this.currentMessageObject.lastLineWidth + i2) + AndroidUtilities.dp(31.0f);
        } else {
            this.backgroundWidth = ((i3 + i2) - i4) + AndroidUtilities.dp(31.0f);
        }
    }

    private boolean checkAudioMotionEvent(MotionEvent motionEvent) {
        boolean z = true;
        if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO && this.documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        boolean onTouch = this.useSeekBarWaweform ? this.seekBarWaveform.onTouch(motionEvent.getAction(), (motionEvent.getX() - ((float) this.seekBarX)) - ((float) AndroidUtilities.dp(13.0f)), motionEvent.getY() - ((float) this.seekBarY)) : this.seekBar.onTouch(motionEvent.getAction(), motionEvent.getX() - ((float) this.seekBarX), motionEvent.getY() - ((float) this.seekBarY));
        if (onTouch) {
            if (!this.useSeekBarWaweform && motionEvent.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (this.useSeekBarWaweform && !this.seekBarWaveform.isStartDraging() && motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                didPressedButton(true);
            }
            this.disallowLongPress = true;
            invalidate();
            z = onTouch;
        } else {
            int dp = AndroidUtilities.dp(36.0f);
            boolean z2 = (this.buttonState == 0 || this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.buttonState == DOCUMENT_ATTACH_TYPE_GIF) ? x >= this.buttonX - AndroidUtilities.dp(12.0f) && x <= (this.buttonX - AndroidUtilities.dp(12.0f)) + this.backgroundWidth && y >= this.namesOffset + this.mediaOffsetY && y <= this.layoutHeight : x >= this.buttonX && x <= this.buttonX + dp && y >= this.buttonY && y <= this.buttonY + dp;
            if (motionEvent.getAction() == 0) {
                if (z2) {
                    this.buttonPressed = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                    invalidate();
                    this.radialProgress.swapBackground(getDrawableForCurrentState());
                }
            } else if (this.buttonPressed != 0) {
                if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    didPressedButton(true);
                    invalidate();
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
                    invalidate();
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_GIF && !z2) {
                    this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
                    invalidate();
                }
                this.radialProgress.swapBackground(getDrawableForCurrentState());
            }
            z = onTouch;
        }
        return z;
    }

    private boolean checkBotButtonMotionEvent(MotionEvent motionEvent) {
        if (this.botButtons.isEmpty()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            int measuredWidth;
            if (this.currentMessageObject.isOutOwner()) {
                measuredWidth = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                measuredWidth = AndroidUtilities.dp(this.mediaBackground ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 7.0f) + this.backgroundDrawableLeft;
            }
            int i = DOCUMENT_ATTACH_TYPE_NONE;
            while (i < this.botButtons.size()) {
                BotButton botButton = (BotButton) this.botButtons.get(i);
                int access$100 = (botButton.f2678y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                if (x < botButton.f2677x + measuredWidth || x > (botButton.f2677x + measuredWidth) + botButton.width || y < access$100 || y > botButton.height + access$100) {
                    i += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                } else {
                    this.pressedBotButton = i;
                    invalidate();
                    return true;
                }
            }
            return false;
        } else if (motionEvent.getAction() != DOCUMENT_ATTACH_TYPE_DOCUMENT || this.pressedBotButton == -1) {
            return false;
        } else {
            playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
            this.delegate.didPressedBotButton(this, ((BotButton) this.botButtons.get(this.pressedBotButton)).button);
            this.pressedBotButton = -1;
            invalidate();
            return false;
        }
    }

    private boolean checkCaptionMotionEvent(MotionEvent motionEvent) {
        if (!(this.currentMessageObject.caption instanceof Spannable) || this.captionLayout == null) {
            return false;
        }
        if (motionEvent.getAction() == 0 || ((this.linkPreviewPressed || this.pressedLink != null) && motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT)) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (x < this.captionX || x > this.captionX + this.backgroundWidth || y < this.captionY || y > this.captionY + this.captionHeight) {
                resetPressedLink(DOCUMENT_ATTACH_TYPE_AUDIO);
            } else if (motionEvent.getAction() == 0) {
                try {
                    x -= this.captionX;
                    y = this.captionLayout.getLineForVertical(y - this.captionY);
                    int offsetForHorizontal = this.captionLayout.getOffsetForHorizontal(y, (float) x);
                    float lineLeft = this.captionLayout.getLineLeft(y);
                    if (lineLeft <= ((float) x) && this.captionLayout.getLineWidth(y) + lineLeft >= ((float) x)) {
                        Spannable spannable = (Spannable) this.currentMessageObject.caption;
                        ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                        boolean z = (clickableSpanArr.length == 0 || !(clickableSpanArr.length == 0 || !(clickableSpanArr[DOCUMENT_ATTACH_TYPE_NONE] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled)) ? DOCUMENT_ATTACH_TYPE_DOCUMENT : false;
                        if (!z) {
                            this.pressedLink = clickableSpanArr[DOCUMENT_ATTACH_TYPE_NONE];
                            this.pressedLinkType = DOCUMENT_ATTACH_TYPE_AUDIO;
                            resetUrlPaths(false);
                            try {
                                Path obtainNewUrlPath = obtainNewUrlPath(false);
                                offsetForHorizontal = spannable.getSpanStart(this.pressedLink);
                                obtainNewUrlPath.setCurrentLayout(this.captionLayout, offsetForHorizontal, 0.0f);
                                this.captionLayout.getSelectionPath(offsetForHorizontal, spannable.getSpanEnd(this.pressedLink), obtainNewUrlPath);
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                            }
                            invalidate();
                            return true;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            } else if (this.pressedLinkType == DOCUMENT_ATTACH_TYPE_AUDIO) {
                this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
                resetPressedLink(DOCUMENT_ATTACH_TYPE_AUDIO);
                return true;
            }
        }
        return false;
    }

    private boolean checkGameMotionEvent(MotionEvent motionEvent) {
        if (!this.hasGamePreview) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (this.drawPhotoImage && this.photoImage.isInsideImage((float) x, (float) y)) {
                this.gamePreviewPressed = true;
                return true;
            } else if (this.descriptionLayout == null || y < this.descriptionY) {
                return false;
            } else {
                try {
                    x -= (this.textX + AndroidUtilities.dp(10.0f)) + this.descriptionX;
                    y = this.descriptionLayout.getLineForVertical(y - this.descriptionY);
                    int offsetForHorizontal = this.descriptionLayout.getOffsetForHorizontal(y, (float) x);
                    float lineLeft = this.descriptionLayout.getLineLeft(y);
                    if (lineLeft > ((float) x) || this.descriptionLayout.getLineWidth(y) + lineLeft < ((float) x)) {
                        return false;
                    }
                    Spannable spannable = (Spannable) this.currentMessageObject.linkDescription;
                    ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                    boolean z = clickableSpanArr.length == 0 || !(clickableSpanArr.length == 0 || !(clickableSpanArr[DOCUMENT_ATTACH_TYPE_NONE] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled);
                    if (z) {
                        return false;
                    }
                    this.pressedLink = clickableSpanArr[DOCUMENT_ATTACH_TYPE_NONE];
                    this.linkBlockNum = -10;
                    this.pressedLinkType = DOCUMENT_ATTACH_TYPE_GIF;
                    resetUrlPaths(false);
                    try {
                        Path obtainNewUrlPath = obtainNewUrlPath(false);
                        offsetForHorizontal = spannable.getSpanStart(this.pressedLink);
                        obtainNewUrlPath.setCurrentLayout(this.descriptionLayout, offsetForHorizontal, 0.0f);
                        this.descriptionLayout.getSelectionPath(offsetForHorizontal, spannable.getSpanEnd(this.pressedLink), obtainNewUrlPath);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                    invalidate();
                    return true;
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                    return false;
                }
            }
        } else if (motionEvent.getAction() != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            return false;
        } else {
            if (this.pressedLinkType != DOCUMENT_ATTACH_TYPE_GIF && !this.gamePreviewPressed) {
                resetPressedLink(DOCUMENT_ATTACH_TYPE_GIF);
                return false;
            } else if (this.pressedLink != null) {
                if (this.pressedLink instanceof URLSpan) {
                    Browser.openUrl(getContext(), ((URLSpan) this.pressedLink).getURL());
                } else {
                    this.pressedLink.onClick(this);
                }
                resetPressedLink(DOCUMENT_ATTACH_TYPE_GIF);
                return false;
            } else {
                this.gamePreviewPressed = false;
                for (y = DOCUMENT_ATTACH_TYPE_NONE; y < this.botButtons.size(); y += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    BotButton botButton = (BotButton) this.botButtons.get(y);
                    if (botButton.button instanceof TL_keyboardButtonGame) {
                        playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                        this.delegate.didPressedBotButton(this, botButton.button);
                        invalidate();
                        break;
                    }
                }
                resetPressedLink(DOCUMENT_ATTACH_TYPE_GIF);
                return true;
            }
        }
    }

    private boolean checkLinkPreviewMotionEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject.type != 0 || !this.hasLinkPreview) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (x >= this.textX && x <= this.textX + this.backgroundWidth && y >= this.textY + this.currentMessageObject.textHeight && y <= ((this.textY + this.currentMessageObject.textHeight) + this.linkPreviewHeight) + AndroidUtilities.dp(8.0f)) {
            if (motionEvent.getAction() == 0) {
                if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT && this.drawPhotoImage && this.photoImage.isInsideImage((float) x, (float) y)) {
                    if (!this.drawImageButton || this.buttonState == -1 || x < this.buttonX || x > this.buttonX + AndroidUtilities.dp(48.0f) || y < this.buttonY || y > this.buttonY + AndroidUtilities.dp(48.0f)) {
                        this.linkPreviewPressed = true;
                        WebPage webPage = this.currentMessageObject.messageOwner.media.webpage;
                        if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF || this.buttonState != -1 || !MediaController.m71a().m140B() || (this.photoImage.getAnimation() != null && TextUtils.isEmpty(webPage.embed_url))) {
                            return true;
                        }
                        this.linkPreviewPressed = false;
                        return false;
                    }
                    this.buttonPressed = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                    return true;
                } else if (this.descriptionLayout != null && y >= this.descriptionY) {
                    try {
                        x -= (this.textX + AndroidUtilities.dp(10.0f)) + this.descriptionX;
                        y = this.descriptionLayout.getLineForVertical(y - this.descriptionY);
                        int offsetForHorizontal = this.descriptionLayout.getOffsetForHorizontal(y, (float) x);
                        float lineLeft = this.descriptionLayout.getLineLeft(y);
                        if (lineLeft <= ((float) x) && this.descriptionLayout.getLineWidth(y) + lineLeft >= ((float) x)) {
                            Spannable spannable = (Spannable) this.currentMessageObject.linkDescription;
                            ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                            boolean z = clickableSpanArr.length == 0 || !(clickableSpanArr.length == 0 || !(clickableSpanArr[DOCUMENT_ATTACH_TYPE_NONE] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled);
                            if (!z) {
                                this.pressedLink = clickableSpanArr[DOCUMENT_ATTACH_TYPE_NONE];
                                this.linkBlockNum = -10;
                                this.pressedLinkType = DOCUMENT_ATTACH_TYPE_GIF;
                                resetUrlPaths(false);
                                try {
                                    Path obtainNewUrlPath = obtainNewUrlPath(false);
                                    offsetForHorizontal = spannable.getSpanStart(this.pressedLink);
                                    obtainNewUrlPath.setCurrentLayout(this.descriptionLayout, offsetForHorizontal, 0.0f);
                                    this.descriptionLayout.getSelectionPath(offsetForHorizontal, spannable.getSpanEnd(this.pressedLink), obtainNewUrlPath);
                                } catch (Throwable e) {
                                    FileLog.m18e("tmessages", e);
                                }
                                invalidate();
                                return true;
                            }
                        }
                    } catch (Throwable e2) {
                        FileLog.m18e("tmessages", e2);
                    }
                }
            } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                if (this.pressedLinkType != DOCUMENT_ATTACH_TYPE_GIF && this.buttonPressed == 0 && !this.linkPreviewPressed) {
                    resetPressedLink(DOCUMENT_ATTACH_TYPE_GIF);
                } else if (this.buttonPressed != 0) {
                    if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
                        playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                        didPressedButton(false);
                        invalidate();
                    }
                } else if (this.pressedLink != null) {
                    if (this.pressedLink instanceof URLSpan) {
                        Browser.openUrl(getContext(), ((URLSpan) this.pressedLink).getURL());
                    } else {
                        this.pressedLink.onClick(this);
                    }
                    resetPressedLink(DOCUMENT_ATTACH_TYPE_GIF);
                } else {
                    if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF || !this.drawImageButton) {
                        WebPage webPage2 = this.currentMessageObject.messageOwner.media.webpage;
                        if (webPage2 != null && VERSION.SDK_INT >= 16 && !TextUtils.isEmpty(webPage2.embed_url)) {
                            this.delegate.needOpenWebView(webPage2.embed_url, webPage2.site_name, webPage2.description, webPage2.url, webPage2.embed_width, webPage2.embed_height);
                        } else if (this.buttonState == -1) {
                            this.delegate.didPressedImage(this);
                            playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                        } else if (webPage2 != null) {
                            Browser.openUrl(getContext(), webPage2.url);
                        }
                    } else if (this.buttonState == -1) {
                        if (MediaController.m71a().m140B()) {
                            this.delegate.didPressedImage(this);
                        } else {
                            this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                            this.currentMessageObject.audioProgress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                            this.photoImage.setAllowStartAnimation(false);
                            this.photoImage.stopAnimation();
                            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                            invalidate();
                            playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                        }
                    } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_GIF || this.buttonState == 0) {
                        didPressedButton(false);
                        playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    }
                    resetPressedLink(DOCUMENT_ATTACH_TYPE_GIF);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkNeedDrawFavoriteButton(MessageObject messageObject) {
        boolean z = true;
        if (messageObject.getDialogId() == ((long) UserConfig.getClientUserId()) || !MoboConstants.aF) {
            return false;
        }
        if (MoboConstants.aH && messageObject.isOutOwner()) {
            return true;
        }
        if (!messageObject.isFromUser()) {
            return ((messageObject.messageOwner.from_id < 0 || messageObject.messageOwner.post) && messageObject.messageOwner.to_id.channel_id != 0) ? ((messageObject.messageOwner.via_bot_id == 0 && messageObject.messageOwner.reply_to_msg_id == 0) || messageObject.type != 13) && (MoboConstants.aG & 8) != 0 : false;
        } else {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
            if ((user == null || !user.bot || messageObject.type == 13 || (messageObject.messageOwner.media instanceof TL_messageMediaEmpty) || messageObject.messageOwner.media == null || ((messageObject.messageOwner.media instanceof TL_messageMediaWebPage) && !(messageObject.messageOwner.media.webpage instanceof TL_webPage))) && (messageObject.isOut() || messageObject.type == 13 || ((user == null || user.bot || this.isChat || (MoboConstants.aG & DOCUMENT_ATTACH_TYPE_DOCUMENT) == 0) && ((user == null || !user.bot || (MoboConstants.aG & 16) == 0) && ((!messageObject.isMegagroup() || (MoboConstants.aG & DOCUMENT_ATTACH_TYPE_VIDEO) == 0) && (messageObject.messageOwner.to_id.chat_id == 0 || (MoboConstants.aG & DOCUMENT_ATTACH_TYPE_GIF) == 0)))))) {
                z = false;
            }
            return z;
        }
    }

    private boolean checkNeedDrawShareButton(MessageObject messageObject) {
        if (messageObject.isFromUser()) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
            if (user != null && user.bot && messageObject.type != 13 && !(messageObject.messageOwner.media instanceof TL_messageMediaEmpty) && messageObject.messageOwner.media != null && (!(messageObject.messageOwner.media instanceof TL_messageMediaWebPage) || (messageObject.messageOwner.media.webpage instanceof TL_webPage))) {
                return true;
            }
            if (!(messageObject.isOut() || messageObject.type == 13)) {
                if (user == null || user.bot || this.isChat || ((MoboConstants.f1356w & DOCUMENT_ATTACH_TYPE_DOCUMENT) == 0 && (MoboConstants.f1357x & DOCUMENT_ATTACH_TYPE_DOCUMENT) == 0)) {
                    if (user == null || !user.bot || ((MoboConstants.f1356w & 16) == 0 && (MoboConstants.f1357x & 16) == 0)) {
                        if (!messageObject.isMegagroup() || ((MoboConstants.f1356w & DOCUMENT_ATTACH_TYPE_VIDEO) == 0 && (MoboConstants.f1357x & DOCUMENT_ATTACH_TYPE_VIDEO) == 0)) {
                            if (!(messageObject.messageOwner.to_id.chat_id == 0 || ((MoboConstants.f1356w & DOCUMENT_ATTACH_TYPE_GIF) == 0 && (MoboConstants.f1357x & DOCUMENT_ATTACH_TYPE_GIF) == 0))) {
                                if ((MoboConstants.f1357x & DOCUMENT_ATTACH_TYPE_GIF) == 0) {
                                    return true;
                                }
                                this.changeShareWithReplyButton = true;
                                return true;
                            }
                        } else if ((MoboConstants.f1357x & DOCUMENT_ATTACH_TYPE_VIDEO) == 0) {
                            return true;
                        } else {
                            this.changeShareWithReplyButton = true;
                            return true;
                        }
                    } else if ((MoboConstants.f1357x & 16) == 0) {
                        return true;
                    } else {
                        this.changeShareWithReplyButton = true;
                        return true;
                    }
                } else if ((MoboConstants.f1357x & DOCUMENT_ATTACH_TYPE_DOCUMENT) == 0) {
                    return true;
                } else {
                    this.changeShareWithReplyButton = true;
                    return true;
                }
            }
        } else if ((messageObject.messageOwner.from_id < 0 || messageObject.messageOwner.post) && messageObject.messageOwner.to_id.channel_id != 0 && (((messageObject.messageOwner.via_bot_id == 0 && messageObject.messageOwner.reply_to_msg_id == 0) || messageObject.type != 13) && (MoboConstants.f1356w & 8) != 0)) {
            return true;
        }
        return false;
    }

    private boolean checkOtherButtonMotionEvent(MotionEvent motionEvent) {
        if ((this.documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT && this.currentMessageObject.type != 12 && this.documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC && this.documentAttachType != DOCUMENT_ATTACH_TYPE_VIDEO && this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF && this.currentMessageObject.type != 8) || this.hasGamePreview) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (x >= this.otherX - AndroidUtilities.dp(20.0f) && x <= this.otherX + AndroidUtilities.dp(20.0f) && y >= this.otherY - AndroidUtilities.dp(4.0f) && y <= this.otherY + AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) {
                this.otherPressed = true;
                return true;
            }
        } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT && this.otherPressed) {
            this.otherPressed = false;
            playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
            this.delegate.didPressedOther(this);
        }
        return false;
    }

    private boolean checkPhotoImageMotionEvent(MotionEvent motionEvent) {
        boolean z = true;
        if (!this.drawPhotoImage && this.documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (this.buttonState == -1 || x < this.buttonX || x > this.buttonX + AndroidUtilities.dp(48.0f) || y < this.buttonY || y > this.buttonY + AndroidUtilities.dp(48.0f)) {
                if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    if (x >= this.photoImage.getImageX() && x <= (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(50.0f) && y >= this.photoImage.getImageY() && y <= this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
                        this.imagePressed = true;
                    }
                } else if (!(this.currentMessageObject.type == 13 && this.currentMessageObject.getInputStickerSet() == null)) {
                    if (x < this.photoImage.getImageX() || x > this.photoImage.getImageX() + this.backgroundWidth || y < this.photoImage.getImageY() || y > this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
                        z = false;
                    } else {
                        this.imagePressed = true;
                    }
                    if (this.currentMessageObject.type == 12 && MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)) == null) {
                        this.imagePressed = false;
                        z = false;
                    }
                }
                z = false;
            } else {
                this.buttonPressed = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                invalidate();
            }
            if (this.imagePressed) {
                if (this.currentMessageObject.isSecretPhoto()) {
                    this.imagePressed = false;
                } else if (this.currentMessageObject.isSendError()) {
                    this.imagePressed = false;
                    z = false;
                } else if (this.currentMessageObject.type == 8 && this.buttonState == -1 && MediaController.m71a().m140B() && this.photoImage.getAnimation() == null && !MoboConstants.af) {
                    this.imagePressed = false;
                    z = false;
                }
            }
        } else {
            if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                if (this.buttonPressed == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    didPressedButton(false);
                    this.radialProgress.swapBackground(getDrawableForCurrentState());
                    invalidate();
                    z = false;
                } else if (this.imagePressed) {
                    this.imagePressed = false;
                    if (this.buttonState == -1 || this.buttonState == DOCUMENT_ATTACH_TYPE_GIF || this.buttonState == DOCUMENT_ATTACH_TYPE_AUDIO) {
                        playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                        didClickedImage();
                    } else if (this.buttonState == 0 && this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                        didPressedButton(false);
                    }
                    invalidate();
                }
            }
            z = false;
        }
        return z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkTextBlockMotionEvent(android.view.MotionEvent r15) {
        /*
        r14 = this;
        r6 = 1;
        r4 = 0;
        r0 = r14.currentMessageObject;
        r0 = r0.type;
        if (r0 != 0) goto L_0x0020;
    L_0x0008:
        r0 = r14.currentMessageObject;
        r0 = r0.textLayoutBlocks;
        if (r0 == 0) goto L_0x0020;
    L_0x000e:
        r0 = r14.currentMessageObject;
        r0 = r0.textLayoutBlocks;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0020;
    L_0x0018:
        r0 = r14.currentMessageObject;
        r0 = r0.messageText;
        r0 = r0 instanceof android.text.Spannable;
        if (r0 != 0) goto L_0x0021;
    L_0x0020:
        return r4;
    L_0x0021:
        r0 = r15.getAction();
        if (r0 == 0) goto L_0x0031;
    L_0x0027:
        r0 = r15.getAction();
        if (r0 != r6) goto L_0x0020;
    L_0x002d:
        r0 = r14.pressedLinkType;
        if (r0 != r6) goto L_0x0020;
    L_0x0031:
        r0 = r15.getX();
        r2 = (int) r0;
        r0 = r15.getY();
        r0 = (int) r0;
        r1 = r14.textX;
        if (r2 < r1) goto L_0x021a;
    L_0x003f:
        r1 = r14.textY;
        if (r0 < r1) goto L_0x021a;
    L_0x0043:
        r1 = r14.textX;
        r3 = r14.currentMessageObject;
        r3 = r3.textWidth;
        r1 = r1 + r3;
        if (r2 > r1) goto L_0x021a;
    L_0x004c:
        r1 = r14.textY;
        r3 = r14.currentMessageObject;
        r3 = r3.textHeight;
        r1 = r1 + r3;
        if (r0 > r1) goto L_0x021a;
    L_0x0055:
        r1 = r14.textY;
        r3 = r0 - r1;
        r1 = r4;
        r5 = r4;
    L_0x005b:
        r0 = r14.currentMessageObject;
        r0 = r0.textLayoutBlocks;
        r0 = r0.size();
        if (r1 >= r0) goto L_0x0076;
    L_0x0065:
        r0 = r14.currentMessageObject;
        r0 = r0.textLayoutBlocks;
        r0 = r0.get(r1);
        r0 = (com.hanista.mobogram.messenger.MessageObject.TextLayoutBlock) r0;
        r0 = r0.textYOffset;
        r7 = (float) r3;
        r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1));
        if (r0 <= 0) goto L_0x019a;
    L_0x0076:
        r0 = r14.currentMessageObject;	 Catch:{ Exception -> 0x01f9 }
        r0 = r0.textLayoutBlocks;	 Catch:{ Exception -> 0x01f9 }
        r0 = r0.get(r5);	 Catch:{ Exception -> 0x01f9 }
        r0 = (com.hanista.mobogram.messenger.MessageObject.TextLayoutBlock) r0;	 Catch:{ Exception -> 0x01f9 }
        r1 = r14.textX;	 Catch:{ Exception -> 0x01f9 }
        r7 = r0.textXOffset;	 Catch:{ Exception -> 0x01f9 }
        r8 = (double) r7;	 Catch:{ Exception -> 0x01f9 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x01f9 }
        r7 = (int) r8;	 Catch:{ Exception -> 0x01f9 }
        r1 = r1 - r7;
        r1 = r2 - r1;
        r2 = (float) r3;	 Catch:{ Exception -> 0x01f9 }
        r3 = r0.textYOffset;	 Catch:{ Exception -> 0x01f9 }
        r2 = r2 - r3;
        r2 = (int) r2;	 Catch:{ Exception -> 0x01f9 }
        r3 = r0.textLayout;	 Catch:{ Exception -> 0x01f9 }
        r2 = r3.getLineForVertical(r2);	 Catch:{ Exception -> 0x01f9 }
        r3 = r0.textLayout;	 Catch:{ Exception -> 0x01f9 }
        r7 = (float) r1;	 Catch:{ Exception -> 0x01f9 }
        r3 = r3.getOffsetForHorizontal(r2, r7);	 Catch:{ Exception -> 0x01f9 }
        r7 = r0.charactersOffset;	 Catch:{ Exception -> 0x01f9 }
        r3 = r3 + r7;
        r7 = r0.textLayout;	 Catch:{ Exception -> 0x01f9 }
        r7 = r7.getLineLeft(r2);	 Catch:{ Exception -> 0x01f9 }
        r8 = (float) r1;	 Catch:{ Exception -> 0x01f9 }
        r8 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1));
        if (r8 > 0) goto L_0x0020;
    L_0x00ad:
        r8 = r0.textLayout;	 Catch:{ Exception -> 0x01f9 }
        r2 = r8.getLineWidth(r2);	 Catch:{ Exception -> 0x01f9 }
        r2 = r2 + r7;
        r1 = (float) r1;	 Catch:{ Exception -> 0x01f9 }
        r1 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1));
        if (r1 < 0) goto L_0x0020;
    L_0x00b9:
        r1 = r14.currentMessageObject;	 Catch:{ Exception -> 0x01f9 }
        r1 = r1.messageText;	 Catch:{ Exception -> 0x01f9 }
        r1 = (android.text.Spannable) r1;	 Catch:{ Exception -> 0x01f9 }
        r2 = android.text.style.ClickableSpan.class;
        r2 = r1.getSpans(r3, r3, r2);	 Catch:{ Exception -> 0x01f9 }
        r2 = (android.text.style.ClickableSpan[]) r2;	 Catch:{ Exception -> 0x01f9 }
        r3 = r2.length;	 Catch:{ Exception -> 0x01f9 }
        if (r3 == 0) goto L_0x00d8;
    L_0x00ca:
        r3 = r2.length;	 Catch:{ Exception -> 0x01f9 }
        if (r3 == 0) goto L_0x021f;
    L_0x00cd:
        r3 = 0;
        r3 = r2[r3];	 Catch:{ Exception -> 0x01f9 }
        r3 = r3 instanceof com.hanista.mobogram.ui.Components.URLSpanBotCommand;	 Catch:{ Exception -> 0x01f9 }
        if (r3 == 0) goto L_0x021f;
    L_0x00d4:
        r3 = com.hanista.mobogram.ui.Components.URLSpanBotCommand.enabled;	 Catch:{ Exception -> 0x01f9 }
        if (r3 != 0) goto L_0x021f;
    L_0x00d8:
        r3 = r6;
    L_0x00d9:
        if (r3 != 0) goto L_0x0020;
    L_0x00db:
        r3 = r15.getAction();	 Catch:{ Exception -> 0x01f9 }
        if (r3 != 0) goto L_0x0202;
    L_0x00e1:
        r3 = 0;
        r2 = r2[r3];	 Catch:{ Exception -> 0x01f9 }
        r14.pressedLink = r2;	 Catch:{ Exception -> 0x01f9 }
        r14.linkBlockNum = r5;	 Catch:{ Exception -> 0x01f9 }
        r2 = 1;
        r14.pressedLinkType = r2;	 Catch:{ Exception -> 0x01f9 }
        r2 = 0;
        r14.resetUrlPaths(r2);	 Catch:{ Exception -> 0x01f9 }
        r2 = 0;
        r2 = r14.obtainNewUrlPath(r2);	 Catch:{ Exception -> 0x01f1 }
        r3 = r14.pressedLink;	 Catch:{ Exception -> 0x01f1 }
        r3 = r1.getSpanStart(r3);	 Catch:{ Exception -> 0x01f1 }
        r7 = r0.charactersOffset;	 Catch:{ Exception -> 0x01f1 }
        r8 = r3 - r7;
        r3 = r14.pressedLink;	 Catch:{ Exception -> 0x01f1 }
        r9 = r1.getSpanEnd(r3);	 Catch:{ Exception -> 0x01f1 }
        r3 = r0.textLayout;	 Catch:{ Exception -> 0x01f1 }
        r3 = r3.getText();	 Catch:{ Exception -> 0x01f1 }
        r3 = r3.length();	 Catch:{ Exception -> 0x01f1 }
        r7 = r0.textLayout;	 Catch:{ Exception -> 0x01f1 }
        r10 = 0;
        r2.setCurrentLayout(r7, r8, r10);	 Catch:{ Exception -> 0x01f1 }
        r7 = r0.textLayout;	 Catch:{ Exception -> 0x01f1 }
        r10 = r0.charactersOffset;	 Catch:{ Exception -> 0x01f1 }
        r10 = r9 - r10;
        r7.getSelectionPath(r8, r10, r2);	 Catch:{ Exception -> 0x01f1 }
        r2 = r0.charactersOffset;	 Catch:{ Exception -> 0x01f1 }
        r2 = r2 + r3;
        if (r9 < r2) goto L_0x015b;
    L_0x0122:
        r2 = r5 + 1;
        r7 = r2;
    L_0x0125:
        r2 = r14.currentMessageObject;	 Catch:{ Exception -> 0x01f1 }
        r2 = r2.textLayoutBlocks;	 Catch:{ Exception -> 0x01f1 }
        r2 = r2.size();	 Catch:{ Exception -> 0x01f1 }
        if (r7 >= r2) goto L_0x015b;
    L_0x012f:
        r2 = r14.currentMessageObject;	 Catch:{ Exception -> 0x01f1 }
        r2 = r2.textLayoutBlocks;	 Catch:{ Exception -> 0x01f1 }
        r2 = r2.get(r7);	 Catch:{ Exception -> 0x01f1 }
        r2 = (com.hanista.mobogram.messenger.MessageObject.TextLayoutBlock) r2;	 Catch:{ Exception -> 0x01f1 }
        r3 = r2.textLayout;	 Catch:{ Exception -> 0x01f1 }
        r3 = r3.getText();	 Catch:{ Exception -> 0x01f1 }
        r10 = r3.length();	 Catch:{ Exception -> 0x01f1 }
        r3 = r2.charactersOffset;	 Catch:{ Exception -> 0x01f1 }
        r11 = r2.charactersOffset;	 Catch:{ Exception -> 0x01f1 }
        r12 = android.text.style.ClickableSpan.class;
        r3 = r1.getSpans(r3, r11, r12);	 Catch:{ Exception -> 0x01f1 }
        r3 = (android.text.style.ClickableSpan[]) r3;	 Catch:{ Exception -> 0x01f1 }
        if (r3 == 0) goto L_0x015b;
    L_0x0151:
        r11 = r3.length;	 Catch:{ Exception -> 0x01f1 }
        if (r11 == 0) goto L_0x015b;
    L_0x0154:
        r11 = 0;
        r3 = r3[r11];	 Catch:{ Exception -> 0x01f1 }
        r11 = r14.pressedLink;	 Catch:{ Exception -> 0x01f1 }
        if (r3 == r11) goto L_0x01a0;
    L_0x015b:
        if (r8 >= 0) goto L_0x0194;
    L_0x015d:
        r0 = r5 + -1;
        r3 = r0;
    L_0x0160:
        if (r3 < 0) goto L_0x0194;
    L_0x0162:
        r0 = r14.currentMessageObject;	 Catch:{ Exception -> 0x01f1 }
        r0 = r0.textLayoutBlocks;	 Catch:{ Exception -> 0x01f1 }
        r0 = r0.get(r3);	 Catch:{ Exception -> 0x01f1 }
        r0 = (com.hanista.mobogram.messenger.MessageObject.TextLayoutBlock) r0;	 Catch:{ Exception -> 0x01f1 }
        r2 = r0.textLayout;	 Catch:{ Exception -> 0x01f1 }
        r2 = r2.getText();	 Catch:{ Exception -> 0x01f1 }
        r2 = r2.length();	 Catch:{ Exception -> 0x01f1 }
        r5 = r0.charactersOffset;	 Catch:{ Exception -> 0x01f1 }
        r5 = r5 + r2;
        r5 = r5 + -1;
        r7 = r0.charactersOffset;	 Catch:{ Exception -> 0x01f1 }
        r2 = r2 + r7;
        r2 = r2 + -1;
        r7 = android.text.style.ClickableSpan.class;
        r2 = r1.getSpans(r5, r2, r7);	 Catch:{ Exception -> 0x01f1 }
        r2 = (android.text.style.ClickableSpan[]) r2;	 Catch:{ Exception -> 0x01f1 }
        if (r2 == 0) goto L_0x0194;
    L_0x018a:
        r5 = r2.length;	 Catch:{ Exception -> 0x01f1 }
        if (r5 == 0) goto L_0x0194;
    L_0x018d:
        r5 = 0;
        r2 = r2[r5];	 Catch:{ Exception -> 0x01f1 }
        r5 = r14.pressedLink;	 Catch:{ Exception -> 0x01f1 }
        if (r2 == r5) goto L_0x01c4;
    L_0x0194:
        r14.invalidate();	 Catch:{ Exception -> 0x01f9 }
        r4 = r6;
        goto L_0x0020;
    L_0x019a:
        r0 = r1 + 1;
        r5 = r1;
        r1 = r0;
        goto L_0x005b;
    L_0x01a0:
        r3 = 0;
        r3 = r14.obtainNewUrlPath(r3);	 Catch:{ Exception -> 0x01f1 }
        r11 = r2.textLayout;	 Catch:{ Exception -> 0x01f1 }
        r12 = 0;
        r13 = r2.height;	 Catch:{ Exception -> 0x01f1 }
        r13 = (float) r13;	 Catch:{ Exception -> 0x01f1 }
        r3.setCurrentLayout(r11, r12, r13);	 Catch:{ Exception -> 0x01f1 }
        r11 = r2.textLayout;	 Catch:{ Exception -> 0x01f1 }
        r12 = 0;
        r2 = r2.charactersOffset;	 Catch:{ Exception -> 0x01f1 }
        r2 = r9 - r2;
        r11.getSelectionPath(r12, r2, r3);	 Catch:{ Exception -> 0x01f1 }
        r2 = r0.charactersOffset;	 Catch:{ Exception -> 0x01f1 }
        r2 = r2 + r10;
        r2 = r2 + -1;
        if (r9 < r2) goto L_0x015b;
    L_0x01bf:
        r2 = r7 + 1;
        r7 = r2;
        goto L_0x0125;
    L_0x01c4:
        r2 = 0;
        r2 = r14.obtainNewUrlPath(r2);	 Catch:{ Exception -> 0x01f1 }
        r5 = r14.pressedLink;	 Catch:{ Exception -> 0x01f1 }
        r5 = r1.getSpanStart(r5);	 Catch:{ Exception -> 0x01f1 }
        r7 = r0.charactersOffset;	 Catch:{ Exception -> 0x01f1 }
        r5 = r5 - r7;
        r7 = r0.textLayout;	 Catch:{ Exception -> 0x01f1 }
        r8 = r0.height;	 Catch:{ Exception -> 0x01f1 }
        r8 = -r8;
        r8 = (float) r8;	 Catch:{ Exception -> 0x01f1 }
        r2.setCurrentLayout(r7, r5, r8);	 Catch:{ Exception -> 0x01f1 }
        r7 = r0.textLayout;	 Catch:{ Exception -> 0x01f1 }
        r8 = r14.pressedLink;	 Catch:{ Exception -> 0x01f1 }
        r8 = r1.getSpanEnd(r8);	 Catch:{ Exception -> 0x01f1 }
        r0 = r0.charactersOffset;	 Catch:{ Exception -> 0x01f1 }
        r0 = r8 - r0;
        r7.getSelectionPath(r5, r0, r2);	 Catch:{ Exception -> 0x01f1 }
        if (r5 >= 0) goto L_0x0194;
    L_0x01ec:
        r0 = r3 + -1;
        r3 = r0;
        goto L_0x0160;
    L_0x01f1:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);	 Catch:{ Exception -> 0x01f9 }
        goto L_0x0194;
    L_0x01f9:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
        goto L_0x0020;
    L_0x0202:
        r0 = 0;
        r0 = r2[r0];	 Catch:{ Exception -> 0x01f9 }
        r1 = r14.pressedLink;	 Catch:{ Exception -> 0x01f9 }
        if (r0 != r1) goto L_0x0020;
    L_0x0209:
        r0 = r14.delegate;	 Catch:{ Exception -> 0x01f9 }
        r1 = r14.currentMessageObject;	 Catch:{ Exception -> 0x01f9 }
        r2 = r14.pressedLink;	 Catch:{ Exception -> 0x01f9 }
        r3 = 0;
        r0.didPressedUrl(r1, r2, r3);	 Catch:{ Exception -> 0x01f9 }
        r0 = 1;
        r14.resetPressedLink(r0);	 Catch:{ Exception -> 0x01f9 }
        r4 = r6;
        goto L_0x0020;
    L_0x021a:
        r14.resetPressedLink(r6);
        goto L_0x0020;
    L_0x021f:
        r3 = r4;
        goto L_0x00d9;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.Cells.ChatMessageCell.checkTextBlockMotionEvent(android.view.MotionEvent):boolean");
    }

    private int createDocumentLayout(int i, MessageObject messageObject) {
        this.radialProgress.setSizeAndType(0, DOCUMENT_ATTACH_TYPE_NONE);
        this.radialProgress.setShowSize(false);
        if (messageObject.type == 0) {
            this.documentAttach = messageObject.messageOwner.media.webpage.document;
        } else {
            this.documentAttach = messageObject.messageOwner.media.document;
        }
        if (this.documentAttach == null) {
            return DOCUMENT_ATTACH_TYPE_NONE;
        }
        int i2;
        DocumentAttribute documentAttribute;
        int i3;
        if (MessageObject.isVoiceDocument(this.documentAttach)) {
            this.documentAttachType = DOCUMENT_ATTACH_TYPE_AUDIO;
            for (i2 = DOCUMENT_ATTACH_TYPE_NONE; i2 < this.documentAttach.attributes.size(); i2 += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                documentAttribute = (DocumentAttribute) this.documentAttach.attributes.get(i2);
                if (documentAttribute instanceof TL_documentAttributeAudio) {
                    i3 = documentAttribute.duration;
                    break;
                }
            }
            i3 = DOCUMENT_ATTACH_TYPE_NONE;
            this.availableTimeWidth = (i - AndroidUtilities.dp(94.0f)) - ((int) Math.ceil((double) audioTimePaint.measureText("00:00")));
            measureTime(messageObject);
            i2 = AndroidUtilities.dp(174.0f) + this.timeWidth;
            if (!this.hasLinkPreview) {
                this.backgroundWidth = Math.min(i, (i3 * AndroidUtilities.dp(10.0f)) + i2);
            }
            if (messageObject.isOutOwner()) {
                this.seekBarWaveform.setColors(Theme.MSG_OUT_VOICE_SEEKBAR_COLOR, Theme.MSG_OUT_VOICE_SEEKBAR_FILL_COLOR, Theme.MSG_OUT_VOICE_SEEKBAR_SELECTED_COLOR);
                this.seekBar.setColors(Theme.MSG_OUT_VOICE_SEEKBAR_COLOR, Theme.MSG_OUT_VOICE_SEEKBAR_FILL_COLOR, Theme.MSG_OUT_VOICE_SEEKBAR_SELECTED_COLOR);
            } else {
                this.seekBarWaveform.setColors(Theme.MSG_IN_VOICE_SEEKBAR_COLOR, Theme.MSG_IN_VOICE_SEEKBAR_FILL_COLOR, Theme.MSG_IN_VOICE_SEEKBAR_SELECTED_COLOR);
                this.seekBar.setColors(Theme.MSG_IN_AUDIO_SEEKBAR_COLOR, Theme.MSG_IN_VOICE_SEEKBAR_FILL_COLOR, Theme.MSG_IN_VOICE_SEEKBAR_SELECTED_COLOR);
            }
            this.seekBarWaveform.setMessageObject(messageObject);
            this.radialProgress.setShowSize(!messageObject.mediaExists);
            this.radialProgress.setSizeAndType((long) this.documentAttach.size, messageObject.type);
            return DOCUMENT_ATTACH_TYPE_NONE;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = DOCUMENT_ATTACH_TYPE_MUSIC;
            if (messageObject.isOutOwner()) {
                this.seekBar.setColors(Theme.MSG_OUT_VOICE_SEEKBAR_COLOR, Theme.MSG_OUT_VOICE_SEEKBAR_FILL_COLOR, Theme.MSG_OUT_VOICE_SEEKBAR_SELECTED_COLOR);
            } else {
                this.seekBar.setColors(Theme.MSG_IN_AUDIO_SEEKBAR_COLOR, Theme.MSG_IN_VOICE_SEEKBAR_FILL_COLOR, Theme.MSG_IN_VOICE_SEEKBAR_SELECTED_COLOR);
            }
            int dp = i - AndroidUtilities.dp(86.0f);
            this.songLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicTitle().replace('\n', ' '), audioTitlePaint, (float) (dp - AndroidUtilities.dp(12.0f)), TruncateAt.END), audioTitlePaint, dp, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            if (this.songLayout.getLineCount() > 0) {
                this.songX = -((int) Math.ceil((double) this.songLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE)));
            }
            this.performerLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicAuthor().replace('\n', ' '), audioPerformerPaint, (float) dp, TruncateAt.END), audioPerformerPaint, dp, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            if (this.performerLayout.getLineCount() > 0) {
                this.performerX = -((int) Math.ceil((double) this.performerLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE)));
            }
            this.radialProgress.setShowSize(!messageObject.mediaExists);
            this.radialProgress.setSizeAndType((long) this.documentAttach.size, messageObject.type);
            for (i2 = DOCUMENT_ATTACH_TYPE_NONE; i2 < this.documentAttach.attributes.size(); i2 += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                documentAttribute = (DocumentAttribute) this.documentAttach.attributes.get(i2);
                if (documentAttribute instanceof TL_documentAttributeAudio) {
                    i3 = documentAttribute.duration;
                    break;
                }
            }
            i3 = DOCUMENT_ATTACH_TYPE_NONE;
            TextPaint textPaint = audioTimePaint;
            r3 = new Object[DOCUMENT_ATTACH_TYPE_VIDEO];
            r3[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(i3 / 60);
            r3[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(i3 % 60);
            r3[DOCUMENT_ATTACH_TYPE_GIF] = Integer.valueOf(i3 / 60);
            r3[DOCUMENT_ATTACH_TYPE_AUDIO] = Integer.valueOf(i3 % 60);
            r10 = (int) Math.ceil((double) textPaint.measureText(String.format("%d:%02d / %d:%02d", r3)));
            this.availableTimeWidth = (this.backgroundWidth - AndroidUtilities.dp(94.0f)) - r10;
            return r10;
        } else if (MessageObject.isVideoDocument(this.documentAttach)) {
            this.documentAttachType = DOCUMENT_ATTACH_TYPE_VIDEO;
            for (i2 = DOCUMENT_ATTACH_TYPE_NONE; i2 < this.documentAttach.attributes.size(); i2 += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                documentAttribute = (DocumentAttribute) this.documentAttach.attributes.get(i2);
                if (documentAttribute instanceof TL_documentAttributeVideo) {
                    i3 = documentAttribute.duration;
                    break;
                }
            }
            i3 = DOCUMENT_ATTACH_TYPE_NONE;
            i2 = i3 / 60;
            i3 -= i2 * 60;
            r3 = new Object[DOCUMENT_ATTACH_TYPE_AUDIO];
            r3[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(i2);
            r3[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(i3);
            r3[DOCUMENT_ATTACH_TYPE_GIF] = AndroidUtilities.formatFileSize((long) this.documentAttach.size);
            r1 = String.format("%d:%02d, %s", r3);
            this.infoWidth = (int) Math.ceil((double) infoPaint.measureText(r1));
            this.infoLayout = new StaticLayout(r1, infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            this.radialProgress.setSizeAndType((long) this.documentAttach.size, messageObject.type);
            return DOCUMENT_ATTACH_TYPE_NONE;
        } else {
            boolean z = (this.documentAttach.mime_type != null && this.documentAttach.mime_type.toLowerCase().startsWith("image/")) || ((this.documentAttach.thumb instanceof TL_photoSize) && !(this.documentAttach.thumb.location instanceof TL_fileLocationUnavailable));
            this.drawPhotoImage = z;
            int dp2 = !this.drawPhotoImage ? i + AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) : i;
            this.documentAttachType = DOCUMENT_ATTACH_TYPE_DOCUMENT;
            CharSequence documentFileName = FileLoader.getDocumentFileName(this.documentAttach);
            if (documentFileName == null || documentFileName.length() == 0) {
                documentFileName = LocaleController.getString("AttachDocument", C0338R.string.AttachDocument);
            }
            this.docTitleLayout = StaticLayoutEx.createStaticLayout(documentFileName, docNamePaint, dp2, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false, TruncateAt.MIDDLE, dp2, this.drawPhotoImage ? DOCUMENT_ATTACH_TYPE_GIF : DOCUMENT_ATTACH_TYPE_DOCUMENT);
            this.docTitleOffsetX = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            if (this.docTitleLayout == null || this.docTitleLayout.getLineCount() <= 0) {
                this.docTitleOffsetX = DOCUMENT_ATTACH_TYPE_NONE;
                r10 = dp2;
            } else {
                i2 = DOCUMENT_ATTACH_TYPE_NONE;
                for (i3 = DOCUMENT_ATTACH_TYPE_NONE; i3 < this.docTitleLayout.getLineCount(); i3 += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    i2 = Math.max(i2, (int) Math.ceil((double) this.docTitleLayout.getLineWidth(i3)));
                    this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int) Math.ceil((double) (-this.docTitleLayout.getLineLeft(i3))));
                }
                r10 = Math.min(dp2, i2);
            }
            this.radialProgress.setSizeAndType((long) this.documentAttach.size, messageObject.type);
            documentFileName = AndroidUtilities.formatFileSize((long) this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach);
            this.infoWidth = Math.min(dp2 - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), (int) Math.ceil((double) infoPaint.measureText(documentFileName)));
            r1 = TextUtils.ellipsize(documentFileName, infoPaint, (float) this.infoWidth, TruncateAt.END);
            try {
                if (this.infoWidth < 0) {
                    this.infoWidth = AndroidUtilities.dp(10.0f);
                }
                this.infoLayout = new StaticLayout(r1, infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (!this.drawPhotoImage) {
                return r10;
            }
            this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
            this.photoImage.setNeedsQualityThumb(true);
            this.photoImage.setShouldGenerateQualityThumb(true);
            this.photoImage.setParentMessageObject(messageObject);
            if (this.currentPhotoObject != null) {
                this.currentPhotoFilter = "86_86_b";
                this.photoImage.setImage(null, null, null, null, this.currentPhotoObject.location, this.currentPhotoFilter, DOCUMENT_ATTACH_TYPE_NONE, null, true);
                return r10;
            }
            this.photoImage.setImageBitmap((BitmapDrawable) null);
            return r10;
        }
    }

    private void didClickedImage() {
        if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.currentMessageObject.type == 13) {
            if (this.buttonState == -1) {
                this.delegate.didPressedImage(this);
            } else if (this.buttonState == 0) {
                didPressedButton(false);
            }
        } else if (this.currentMessageObject.type == 12) {
            this.delegate.didPressedUserAvatar(this, MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)));
        } else if (this.currentMessageObject.type == 8) {
            if (this.buttonState == -1) {
                if (MediaController.m71a().m140B()) {
                    this.delegate.didPressedImage(this);
                    return;
                }
                this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                this.currentMessageObject.audioProgress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                this.photoImage.setAllowStartAnimation(false);
                this.photoImage.stopAnimation();
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_GIF || this.buttonState == 0) {
                didPressedButton(false);
            }
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (this.buttonState == 0 || this.buttonState == DOCUMENT_ATTACH_TYPE_AUDIO) {
                didPressedButton(false);
            }
        } else if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_VIDEO) {
            this.delegate.didPressedImage(this);
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            if (this.buttonState == -1) {
                this.delegate.didPressedImage(this);
            }
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF && this.buttonState == -1) {
            WebPage webPage = this.currentMessageObject.messageOwner.media.webpage;
            if (webPage == null) {
                return;
            }
            if (VERSION.SDK_INT < 16 || webPage.embed_url == null || webPage.embed_url.length() == 0) {
                Browser.openUrl(getContext(), webPage.url);
            } else {
                this.delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.description, webPage.url, webPage.embed_width, webPage.embed_height);
            }
        }
    }

    private void didPressedButton(boolean z) {
        if (this.buttonState == 0) {
            if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO && this.documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC) {
                this.cancelLoading = false;
                this.radialProgress.setProgress(0.0f, false);
                if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb != null ? this.currentPhotoObjectThumb.location : null, this.currentPhotoFilter, this.currentPhotoObject.size, null, false);
                } else if (this.currentMessageObject.type == 8) {
                    this.currentMessageObject.audioProgress = 2.0f;
                    this.photoImage.setImage(this.currentMessageObject.messageOwner.media.document, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilter, this.currentMessageObject.messageOwner.media.document.size, null, false);
                } else if (this.currentMessageObject.type == 9) {
                    FileLoader.getInstance().loadFile(this.currentMessageObject.messageOwner.media.document, false, false);
                } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
                    FileLoader.getInstance().loadFile(this.documentAttach, true, false);
                } else if (this.currentMessageObject.type != 0 || this.documentAttachType == 0) {
                    this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb != null ? this.currentPhotoObjectThumb.location : null, this.currentPhotoFilterThumb, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF) {
                    this.photoImage.setImage(this.currentMessageObject.messageOwner.media.webpage.document, null, this.currentPhotoObject.location, this.currentPhotoFilter, this.currentMessageObject.messageOwner.media.webpage.document.size, null, false);
                    this.currentMessageObject.audioProgress = 2.0f;
                } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    FileLoader.getInstance().loadFile(this.currentMessageObject.messageOwner.media.webpage.document, false, false);
                }
                if (this.buttonState == 0) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                    invalidate();
                }
            } else if (this.delegate.needPlayAudio(this.currentMessageObject)) {
                this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                if (MediaController.m71a().m166b(this.currentMessageObject)) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                }
            } else if (this.currentMessageObject.isOut() && this.currentMessageObject.isSending()) {
                this.delegate.didPressedCancelSendButton(this);
            } else {
                this.cancelLoading = true;
                if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    FileLoader.getInstance().cancelLoadFile(this.documentAttach);
                } else if (this.currentMessageObject.type == 0 || this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.currentMessageObject.type == 8) {
                    this.photoImage.cancelLoadImage();
                } else if (this.currentMessageObject.type == 9) {
                    FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.messageOwner.media.document);
                }
                this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                invalidate();
            }
        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_GIF) {
            if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                this.radialProgress.setProgress(0.0f, false);
                FileLoader.getInstance().loadFile(this.documentAttach, true, false);
                this.buttonState = DOCUMENT_ATTACH_TYPE_VIDEO;
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
                invalidate();
            } else if (!MoboConstants.af || this.currentMessageObject.messageOwner.media == null || this.currentMessageObject.messageOwner.media.document == null) {
                this.photoImage.setAllowStartAnimation(true);
                this.photoImage.startAnimation();
                this.currentMessageObject.audioProgress = 0.0f;
                this.buttonState = -1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
            } else {
                this.photoImage.setAllowStartAnimation(true);
                this.photoImage.startAnimation();
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                this.delegate.didPressedImage(this);
            }
        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_AUDIO) {
            this.delegate.didPressedImage(this);
        } else if (this.buttonState != DOCUMENT_ATTACH_TYPE_VIDEO) {
        } else {
            if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO && this.documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC) {
                return;
            }
            if ((!this.currentMessageObject.isOut() || !this.currentMessageObject.isSending()) && !this.currentMessageObject.isSendError()) {
                FileLoader.getInstance().cancelLoadFile(this.documentAttach);
                this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            } else if (this.delegate != null) {
                this.delegate.didPressedCancelSendButton(this);
            }
        }
    }

    private void drawContent(Canvas canvas) {
        int i;
        int i2;
        int dp;
        int i3;
        int i4;
        int dp2;
        Drawable drawable;
        if (this.needNewVisiblePart && this.currentMessageObject.type == 0) {
            getLocalVisibleRect(this.scrollRect);
            setVisiblePart(this.scrollRect.top, this.scrollRect.bottom - this.scrollRect.top);
            this.needNewVisiblePart = false;
        }
        this.photoImage.setPressed(isDrawSelectedBackground());
        this.photoImage.setVisible(!PhotoViewer.getInstance().isShowingImage(this.currentMessageObject), false);
        this.radialProgress.setHideCurrentDrawable(false);
        this.radialProgress.setProgressColor(-1);
        boolean z = false;
        boolean z2;
        if (this.currentMessageObject.type == 0) {
            if (this.currentMessageObject.isOutOwner()) {
                this.textX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f);
            } else {
                this.textX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0f);
            }
            if (this.hasGamePreview) {
                this.textX += AndroidUtilities.dp(11.0f);
                this.textY = AndroidUtilities.dp(14.0f) + this.namesOffset;
                if (this.siteNameLayout != null) {
                    this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                }
            } else {
                this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
            }
            if (!(this.currentMessageObject.textLayoutBlocks == null || this.currentMessageObject.textLayoutBlocks.isEmpty() || this.firstVisibleBlockNum < 0)) {
                i = this.firstVisibleBlockNum;
                while (i <= this.lastVisibleBlockNum && i < this.currentMessageObject.textLayoutBlocks.size()) {
                    TextLayoutBlock textLayoutBlock = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i);
                    canvas.save();
                    canvas.translate((float) (this.textX - ((int) Math.ceil((double) textLayoutBlock.textXOffset))), ((float) this.textY) + textLayoutBlock.textYOffset);
                    if (this.pressedLink != null && i == this.linkBlockNum) {
                        for (i2 = DOCUMENT_ATTACH_TYPE_NONE; i2 < this.urlPath.size(); i2 += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                            canvas.drawPath((Path) this.urlPath.get(i2), urlPaint);
                        }
                    }
                    if (i == this.linkSelectionBlockNum && !this.urlPathSelection.isEmpty()) {
                        for (i2 = DOCUMENT_ATTACH_TYPE_NONE; i2 < this.urlPathSelection.size(); i2 += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                            canvas.drawPath((Path) this.urlPathSelection.get(i2), urlSelectionPaint);
                        }
                    }
                    try {
                        textLayoutBlock.textLayout.draw(canvas);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                    canvas.restore();
                    i += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                }
            }
            if (this.hasLinkPreview || this.hasGamePreview) {
                int dp3;
                TextPaint textPaint;
                int i5;
                if (this.hasGamePreview) {
                    dp3 = this.textX - AndroidUtilities.dp(10.0f);
                    dp = this.namesOffset + AndroidUtilities.dp(14.0f);
                } else {
                    dp3 = this.textX + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    dp = AndroidUtilities.dp(8.0f) + (this.textY + this.currentMessageObject.textHeight);
                }
                replyLinePaint.setColor(this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_WEB_PREVIEW_LINE_COLOR : Theme.MSG_IN_WEB_PREVIEW_LINE_COLOR);
                i3 = AdvanceTheme.f2491b;
                i4 = AdvanceTheme.bv;
                int i6 = AdvanceTheme.br;
                int i7 = AdvanceTheme.bt;
                int i8 = AdvanceTheme.bp;
                if (ThemeUtil.m2490b()) {
                    replyLinePaint.setColor(this.currentMessageObject.isOutOwner() ? i4 : i6);
                }
                canvas.drawRect((float) dp3, (float) (dp - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + dp3), (float) ((this.linkPreviewHeight + dp) + AndroidUtilities.dp(3.0f)), replyLinePaint);
                if (this.siteNameLayout != null) {
                    replyNamePaint.setColor(this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_VIA_BOT_NAME_TEXT_COLOR : Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
                    if (ThemeUtil.m2490b()) {
                        textPaint = replyNamePaint;
                        if (!this.currentMessageObject.isOutOwner()) {
                            i4 = i6;
                        }
                        textPaint.setColor(i4);
                    }
                    canvas.save();
                    canvas.translate((float) (AndroidUtilities.dp(10.0f) + dp3), (float) (dp - AndroidUtilities.dp(3.0f)));
                    this.siteNameLayout.draw(canvas);
                    canvas.restore();
                    i3 = this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1) + dp;
                } else {
                    i3 = dp;
                }
                if (this.hasGamePreview && this.currentMessageObject.textHeight != 0) {
                    dp += this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0f);
                    i3 += this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0f);
                }
                replyNamePaint.setColor(Theme.MSG_TEXT_COLOR);
                replyTextPaint.setColor(Theme.MSG_TEXT_COLOR);
                if (this.titleLayout != null) {
                    if (i3 != dp) {
                        i3 += AndroidUtilities.dp(2.0f);
                    }
                    if (ThemeUtil.m2490b()) {
                        replyNamePaint.setColor(this.currentMessageObject.isOutOwner() ? i7 : i8);
                    }
                    dp2 = i3 - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    canvas.save();
                    canvas.translate((float) ((AndroidUtilities.dp(10.0f) + dp3) + this.titleX), (float) (i3 - AndroidUtilities.dp(3.0f)));
                    this.titleLayout.draw(canvas);
                    canvas.restore();
                    i5 = dp2;
                    dp2 = i3 + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
                    i3 = i5;
                } else {
                    dp2 = i3;
                    i3 = DOCUMENT_ATTACH_TYPE_NONE;
                }
                if (this.authorLayout != null) {
                    i = dp2 != dp ? dp2 + AndroidUtilities.dp(2.0f) : dp2;
                    if (i3 == 0) {
                        i3 = i - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    }
                    if (ThemeUtil.m2490b()) {
                        replyNamePaint.setColor(this.currentMessageObject.isOutOwner() ? i7 : i8);
                    }
                    canvas.save();
                    canvas.translate((float) ((AndroidUtilities.dp(10.0f) + dp3) + this.authorX), (float) (i - AndroidUtilities.dp(3.0f)));
                    this.authorLayout.draw(canvas);
                    canvas.restore();
                    dp2 = this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1) + i;
                }
                if (this.descriptionLayout != null) {
                    i2 = dp2 != dp ? dp2 + AndroidUtilities.dp(2.0f) : dp2;
                    dp2 = i3 == 0 ? i2 - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) : i3;
                    if (ThemeUtil.m2490b()) {
                        textPaint = replyTextPaint;
                        if (!this.currentMessageObject.isOutOwner()) {
                            i7 = i8;
                        }
                        textPaint.setColor(i7);
                    }
                    this.descriptionY = i2 - AndroidUtilities.dp(3.0f);
                    canvas.save();
                    canvas.translate((float) ((AndroidUtilities.dp(10.0f) + dp3) + this.descriptionX), (float) this.descriptionY);
                    if (this.pressedLink != null && this.linkBlockNum == -10) {
                        for (i = DOCUMENT_ATTACH_TYPE_NONE; i < this.urlPath.size(); i += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                            canvas.drawPath((Path) this.urlPath.get(i), urlPaint);
                        }
                    }
                    this.descriptionLayout.draw(canvas);
                    canvas.restore();
                    i3 = this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1) + i2;
                } else {
                    i5 = i3;
                    i3 = dp2;
                    dp2 = i5;
                }
                if (this.drawPhotoImage) {
                    if (i3 != dp) {
                        i3 += AndroidUtilities.dp(2.0f);
                    }
                    if (this.isSmallImage) {
                        this.photoImage.setImageCoords((this.backgroundWidth + dp3) - AndroidUtilities.dp(81.0f), dp2, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    } else {
                        this.photoImage.setImageCoords(AndroidUtilities.dp(10.0f) + dp3, i3, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                        if (this.drawImageButton) {
                            i3 = AndroidUtilities.dp(48.0f);
                            this.buttonX = (int) (((float) this.photoImage.getImageX()) + (((float) (this.photoImage.getImageWidth() - i3)) / 2.0f));
                            this.buttonY = (int) ((((float) (this.photoImage.getImageHeight() - i3)) / 2.0f) + ((float) this.photoImage.getImageY()));
                            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0f), this.buttonY + AndroidUtilities.dp(48.0f));
                        }
                    }
                    boolean draw = this.photoImage.draw(canvas);
                    if (this.videoInfoLayout != null) {
                        if (this.hasGamePreview) {
                            dp2 = AndroidUtilities.dp(8.5f) + this.photoImage.getImageX();
                            i3 = this.photoImage.getImageY() + AndroidUtilities.dp(6.0f);
                            Theme.timeBackgroundDrawable.setBounds(dp2 - AndroidUtilities.dp(4.0f), i3 - AndroidUtilities.dp(1.5f), (this.durationWidth + dp2) + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(16.5f) + i3);
                            Theme.timeBackgroundDrawable.draw(canvas);
                        } else {
                            dp2 = ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.durationWidth;
                            i3 = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f);
                            Theme.timeBackgroundDrawable.setBounds(dp2 - AndroidUtilities.dp(4.0f), i3 - AndroidUtilities.dp(1.5f), (this.durationWidth + dp2) + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(14.5f) + i3);
                            Theme.timeBackgroundDrawable.draw(canvas);
                        }
                        canvas.save();
                        canvas.translate((float) dp2, (float) i3);
                        this.videoInfoLayout.draw(canvas);
                        canvas.restore();
                    }
                    z2 = draw;
                    this.drawTime = true;
                    z = z2;
                }
            }
            z2 = DOCUMENT_ATTACH_TYPE_NONE;
            this.drawTime = true;
            z = z2;
        } else if (this.drawPhotoImage) {
            z2 = this.photoImage.draw(canvas);
            this.drawTime = this.photoImage.getVisible();
            z = z2;
        }
        if (this.buttonState == -1 && this.currentMessageObject.isSecretPhoto()) {
            i3 = DOCUMENT_ATTACH_TYPE_VIDEO;
            if (this.currentMessageObject.messageOwner.destroyTime != 0) {
                i3 = this.currentMessageObject.isOutOwner() ? DOCUMENT_ATTACH_TYPE_STICKER : DOCUMENT_ATTACH_TYPE_MUSIC;
            }
            setDrawableBounds(Theme.photoStatesDrawables[i3][this.buttonPressed], this.buttonX, this.buttonY);
            Theme.photoStatesDrawables[i3][this.buttonPressed].setAlpha((int) (255.0f * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.radialProgress.getAlpha())));
            Theme.photoStatesDrawables[i3][this.buttonPressed].draw(canvas);
            if (!(this.currentMessageObject.isOutOwner() || this.currentMessageObject.messageOwner.destroyTime == 0)) {
                float max = ((float) Math.max(0, (((long) this.currentMessageObject.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance().getTimeDifference() * PointerIconCompat.TYPE_DEFAULT))))) / (((float) this.currentMessageObject.messageOwner.ttl) * 1000.0f);
                canvas.drawArc(this.deleteProgressRect, -90.0f, -360.0f * max, true, deleteProgressPaint);
                if (max != 0.0f) {
                    i3 = AndroidUtilities.dp(2.0f);
                    invalidate(((int) this.deleteProgressRect.left) - i3, ((int) this.deleteProgressRect.top) - i3, ((int) this.deleteProgressRect.right) + (i3 * DOCUMENT_ATTACH_TYPE_GIF), (i3 * DOCUMENT_ATTACH_TYPE_GIF) + ((int) this.deleteProgressRect.bottom));
                }
                updateSecretTimeText(this.currentMessageObject);
            }
        }
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF || this.currentMessageObject.type == 8) {
            if (this.photoImage.getVisible() && !this.hasGamePreview) {
                drawable = Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_AUDIO];
                dp2 = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                this.otherX = dp2;
                i = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                this.otherY = i;
                setDrawableBounds(drawable, dp2, i);
                Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_AUDIO].draw(canvas);
            }
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            dp2 = AdvanceTheme.m2286c(AdvanceTheme.bu, Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            i = AdvanceTheme.bE;
            if (this.currentMessageObject.isOutOwner()) {
                audioTitlePaint.setColor(Theme.MSG_OUT_VIA_BOT_NAME_TEXT_COLOR);
                audioPerformerPaint.setColor(Theme.MSG_OUT_CONTACT_PHONE_TEXT_COLOR);
                audioTimePaint.setColor(Theme.MSG_OUT_VENUE_INFO_TEXT_COLOR);
                RadialProgress radialProgress = this.radialProgress;
                i3 = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.MSG_OUT_AUDIO_SELECTED_PROGRESS_COLOR : Theme.MSG_OUT_AUDIO_PROGRESS_COLOR;
                radialProgress.setProgressColor(i3);
                if (ThemeUtil.m2490b()) {
                    audioTitlePaint.setColor(dp2);
                    audioPerformerPaint.setColor(dp2);
                    audioTimePaint.setColor(i);
                }
            } else {
                audioTitlePaint.setColor(Theme.MSG_IN_VENUE_NAME_TEXT_COLOR);
                audioPerformerPaint.setColor(Theme.MSG_IN_CONTACT_PHONE_TEXT_COLOR);
                audioTimePaint.setColor(Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
                r3 = this.radialProgress;
                i3 = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.MSG_IN_AUDIO_SELECTED_PROGRESS_COLOR : -1;
                r3.setProgressColor(i3);
                if (ThemeUtil.m2490b()) {
                    i3 = AdvanceTheme.m2286c(AdvanceTheme.bq, Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
                    dp2 = AdvanceTheme.m2286c(AdvanceTheme.bF, Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
                    audioTitlePaint.setColor(i3);
                    audioPerformerPaint.setColor(i3);
                    audioTimePaint.setColor(dp2);
                }
            }
            this.radialProgress.draw(canvas);
            canvas.save();
            canvas.translate((float) (this.timeAudioX + this.songX), (float) ((AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY));
            this.songLayout.draw(canvas);
            canvas.restore();
            canvas.save();
            if (MediaController.m71a().m172d(this.currentMessageObject)) {
                canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas);
            } else {
                canvas.translate((float) (this.timeAudioX + this.performerX), (float) ((AndroidUtilities.dp(35.0f) + this.namesOffset) + this.mediaOffsetY));
                this.performerLayout.draw(canvas);
            }
            canvas.restore();
            canvas.save();
            canvas.translate((float) this.timeAudioX, (float) ((AndroidUtilities.dp(57.0f) + this.namesOffset) + this.mediaOffsetY));
            this.durationLayout.draw(canvas);
            canvas.restore();
            if (this.currentMessageObject.isOutOwner()) {
                drawable = Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_DOCUMENT];
            } else {
                drawable = Theme.docMenuDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_GIF : DOCUMENT_ATTACH_TYPE_NONE];
            }
            dp2 = (this.backgroundWidth + this.buttonX) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
            this.otherX = dp2;
            i = this.buttonY - AndroidUtilities.dp(5.0f);
            this.otherY = i;
            setDrawableBounds(drawable, dp2, i);
            drawable.draw(canvas);
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
            if (this.currentMessageObject.isOutOwner()) {
                audioTimePaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_VENUE_INFO_TEXT_COLOR : Theme.MSG_OUT_VENUE_INFO_TEXT_COLOR);
                r3 = this.radialProgress;
                i3 = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.MSG_OUT_AUDIO_SELECTED_PROGRESS_COLOR : Theme.MSG_OUT_AUDIO_PROGRESS_COLOR;
                r3.setProgressColor(i3);
                if (ThemeUtil.m2490b()) {
                    audioTimePaint.setColor(AdvanceTheme.bE);
                }
            } else {
                audioTimePaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_VENUE_INFO_SELECTED_TEXT_COLOR : Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
                r3 = this.radialProgress;
                i3 = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.MSG_IN_AUDIO_SELECTED_PROGRESS_COLOR : -1;
                r3.setProgressColor(i3);
                if (ThemeUtil.m2490b()) {
                    audioTimePaint.setColor(AdvanceTheme.m2286c(AdvanceTheme.bF, Theme.MSG_IN_VENUE_INFO_TEXT_COLOR));
                }
            }
            this.radialProgress.draw(canvas);
            canvas.save();
            if (this.useSeekBarWaweform) {
                canvas.translate((float) (this.seekBarX + AndroidUtilities.dp(13.0f)), (float) this.seekBarY);
                this.seekBarWaveform.draw(canvas);
            } else {
                canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas);
            }
            canvas.restore();
            canvas.save();
            canvas.translate((float) this.timeAudioX, (float) ((AndroidUtilities.dp(44.0f) + this.namesOffset) + this.mediaOffsetY));
            this.durationLayout.draw(canvas);
            canvas.restore();
            if (this.currentMessageObject.type != 0 && this.currentMessageObject.messageOwner.to_id.channel_id == 0 && this.currentMessageObject.isContentUnread()) {
                docBackPaint.setColor(this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_VOICE_SEEKBAR_FILL_COLOR : Theme.MSG_IN_VOICE_SEEKBAR_FILL_COLOR);
                canvas.drawCircle((float) ((this.timeAudioX + this.timeWidthAudio) + AndroidUtilities.dp(6.0f)), (float) ((AndroidUtilities.dp(51.0f) + this.namesOffset) + this.mediaOffsetY), (float) AndroidUtilities.dp(3.0f), docBackPaint);
            }
        }
        if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (this.photoImage.getVisible()) {
                if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
                    drawable = Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_AUDIO];
                    dp2 = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                    this.otherX = dp2;
                    i = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                    this.otherY = i;
                    setDrawableBounds(drawable, dp2, i);
                    Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_AUDIO].draw(canvas);
                }
                if (this.infoLayout != null && (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.buttonState == 0 || this.buttonState == DOCUMENT_ATTACH_TYPE_AUDIO || this.currentMessageObject.isSecretPhoto())) {
                    infoPaint.setColor(-1);
                    setDrawableBounds(Theme.timeBackgroundDrawable, AndroidUtilities.dp(4.0f) + this.photoImage.getImageX(), AndroidUtilities.dp(4.0f) + this.photoImage.getImageY(), AndroidUtilities.dp(8.0f) + this.infoWidth, AndroidUtilities.dp(16.5f));
                    Theme.timeBackgroundDrawable.draw(canvas);
                    canvas.save();
                    canvas.translate((float) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f)), (float) (this.photoImage.getImageY() + AndroidUtilities.dp(5.5f)));
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                }
            }
        } else if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (this.docTitleLayout != null) {
                dp2 = AdvanceTheme.m2286c(AdvanceTheme.bu, Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
                i = AdvanceTheme.bE;
                if (this.currentMessageObject.isOutOwner()) {
                    locationTitlePaint.setColor(Theme.MSG_OUT_VIA_BOT_NAME_TEXT_COLOR);
                    locationAddressPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_VENUE_INFO_TEXT_COLOR : Theme.MSG_OUT_VENUE_INFO_TEXT_COLOR);
                    if (ThemeUtil.m2490b()) {
                        locationTitlePaint.setColor(dp2);
                        locationAddressPaint.setColor(i);
                    }
                } else {
                    locationTitlePaint.setColor(Theme.MSG_IN_VENUE_NAME_TEXT_COLOR);
                    locationAddressPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_VENUE_INFO_SELECTED_TEXT_COLOR : Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
                    if (ThemeUtil.m2490b()) {
                        i3 = AdvanceTheme.m2286c(AdvanceTheme.bq, Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
                        dp2 = AdvanceTheme.m2286c(AdvanceTheme.bF, Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
                        locationTitlePaint.setColor(i3);
                        locationAddressPaint.setColor(dp2);
                    }
                }
                canvas.save();
                canvas.translate((float) (((this.docTitleOffsetX + this.photoImage.getImageX()) + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f)));
                this.docTitleLayout.draw(canvas);
                canvas.restore();
                if (this.infoLayout != null) {
                    canvas.save();
                    canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) ((this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f)));
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                }
            }
        } else if (this.currentMessageObject.type == 12) {
            contactNamePaint.setColor(this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_VIA_BOT_NAME_TEXT_COLOR : Theme.MSG_IN_VENUE_NAME_TEXT_COLOR);
            contactPhonePaint.setColor(this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_CONTACT_PHONE_TEXT_COLOR : Theme.MSG_IN_CONTACT_PHONE_TEXT_COLOR);
            i3 = AdvanceTheme.f2491b;
            dp2 = AdvanceTheme.bA;
            if (ThemeUtil.m2490b()) {
                if (this.currentMessageObject.messageOwner.media.user_id == 0 || dp2 != i3) {
                    contactNamePaint.setColor(dp2);
                }
                i3 = AdvanceTheme.m2286c(AdvanceTheme.bq, Theme.MSG_IN_CONTACT_PHONE_TEXT_COLOR);
                if (this.currentMessageObject.isOutOwner()) {
                    i3 = AdvanceTheme.m2286c(AdvanceTheme.bu, Theme.MSG_OUT_CONTACT_PHONE_TEXT_COLOR);
                }
                contactPhonePaint.setColor(i3);
            }
            if (this.titleLayout != null) {
                canvas.save();
                canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(16.0f) + this.namesOffset));
                this.titleLayout.draw(canvas);
                canvas.restore();
            }
            if (this.docTitleLayout != null) {
                canvas.save();
                canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(39.0f) + this.namesOffset));
                this.docTitleLayout.draw(canvas);
                canvas.restore();
            }
            if (this.currentMessageObject.isOutOwner()) {
                drawable = Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_DOCUMENT];
            } else {
                drawable = Theme.docMenuDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_GIF : DOCUMENT_ATTACH_TYPE_NONE];
            }
            dp2 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(48.0f);
            this.otherX = dp2;
            i = this.photoImage.getImageY() - AndroidUtilities.dp(5.0f);
            this.otherY = i;
            setDrawableBounds(drawable, dp2, i);
            drawable.draw(canvas);
        } else if (this.currentMessageObject.type == 8 && this.infoLayout != null && (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.buttonState == 0 || this.buttonState == DOCUMENT_ATTACH_TYPE_AUDIO || this.currentMessageObject.isSecretPhoto())) {
            infoPaint.setColor(-1);
            setDrawableBounds(Theme.timeBackgroundDrawable, AndroidUtilities.dp(4.0f) + this.photoImage.getImageX(), AndroidUtilities.dp(4.0f) + this.photoImage.getImageY(), AndroidUtilities.dp(8.0f) + this.infoWidth, AndroidUtilities.dp(16.5f));
            Theme.timeBackgroundDrawable.draw(canvas);
            canvas.save();
            canvas.translate((float) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f)), (float) (this.photoImage.getImageY() + AndroidUtilities.dp(5.5f)));
            this.infoLayout.draw(canvas);
            canvas.restore();
        }
        if (this.captionLayout != null) {
            canvas.save();
            float f;
            if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO || this.currentMessageObject.type == 8) {
                i3 = this.photoImage.getImageX() + AndroidUtilities.dp(5.0f);
                this.captionX = i3;
                f = (float) i3;
                dp2 = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) + AndroidUtilities.dp(6.0f);
                this.captionY = dp2;
                canvas.translate(f, (float) dp2);
            } else {
                i3 = AndroidUtilities.dp(this.currentMessageObject.isOutOwner() ? 11.0f : 17.0f) + this.backgroundDrawableLeft;
                this.captionX = i3;
                f = (float) i3;
                dp2 = ((this.totalHeight - this.captionHeight) - AndroidUtilities.dp(10.0f)) + this.extraHeight;
                this.captionY = dp2;
                canvas.translate(f, (float) dp2);
            }
            if (this.pressedLink != null) {
                for (dp2 = DOCUMENT_ATTACH_TYPE_NONE; dp2 < this.urlPath.size(); dp2 += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    canvas.drawPath((Path) this.urlPath.get(dp2), urlPaint);
                }
            }
            try {
                this.captionLayout.draw(canvas);
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            canvas.restore();
        }
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            dp2 = AdvanceTheme.bt;
            if (this.currentMessageObject.isOutOwner()) {
                docNamePaint.setColor(Theme.MSG_OUT_VIA_BOT_NAME_TEXT_COLOR);
                infoPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_VENUE_INFO_TEXT_COLOR : Theme.MSG_OUT_VENUE_INFO_TEXT_COLOR);
                docBackPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_FILE_PROGRESS_SELECTED_COLOR : Theme.MSG_OUT_FILE_PROGRESS_COLOR);
                drawable = Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_DOCUMENT];
                if (ThemeUtil.m2490b()) {
                    docNamePaint.setColor(dp2);
                    infoPaint.setColor(dp2);
                    drawable.setColorFilter(AdvanceTheme.bE, Mode.SRC_IN);
                }
            } else {
                docNamePaint.setColor(Theme.MSG_IN_VENUE_NAME_TEXT_COLOR);
                infoPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_VENUE_INFO_SELECTED_TEXT_COLOR : Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
                docBackPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_FILE_PROGRESS_SELECTED_COLOR : Theme.MSG_IN_FILE_PROGRESS_COLOR);
                drawable = Theme.docMenuDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_GIF : DOCUMENT_ATTACH_TYPE_NONE];
                if (ThemeUtil.m2490b()) {
                    dp2 = AdvanceTheme.bp;
                    docNamePaint.setColor(dp2);
                    infoPaint.setColor(dp2);
                    drawable.setColorFilter(AdvanceTheme.m2286c(AdvanceTheme.bF, -6181445), Mode.SRC_IN);
                }
            }
            int imageX;
            RadialProgress radialProgress2;
            if (this.drawPhotoImage) {
                if (this.currentMessageObject.type == 0) {
                    dp2 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(56.0f);
                    this.otherX = dp2;
                    i = this.photoImage.getImageY() + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    this.otherY = i;
                    setDrawableBounds(drawable, dp2, i);
                } else {
                    dp2 = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(40.0f);
                    this.otherX = dp2;
                    i = this.photoImage.getImageY() + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    this.otherY = i;
                    setDrawableBounds(drawable, dp2, i);
                }
                imageX = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f);
                i2 = this.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                i = AndroidUtilities.dp(13.0f) + (this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1));
                if (this.buttonState >= 0 && this.buttonState < DOCUMENT_ATTACH_TYPE_VIDEO) {
                    if (z) {
                        this.radialProgress.swapBackground(Theme.photoStatesDrawables[this.buttonState][this.buttonPressed]);
                    } else {
                        dp2 = this.buttonState;
                        if (this.buttonState == 0) {
                            dp2 = this.currentMessageObject.isOutOwner() ? 7 : 10;
                        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                            dp2 = this.currentMessageObject.isOutOwner() ? 8 : 11;
                        }
                        radialProgress2 = this.radialProgress;
                        Drawable[] drawableArr = Theme.photoStatesDrawables[dp2];
                        dp2 = (isDrawSelectedBackground() || this.buttonPressed != 0) ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE;
                        radialProgress2.swapBackground(drawableArr[dp2]);
                    }
                }
                if (z) {
                    if (this.buttonState == -1) {
                        this.radialProgress.setHideCurrentDrawable(true);
                    }
                    this.radialProgress.setProgressColor(-1);
                    dp2 = i;
                    i = i2;
                    i2 = imageX;
                } else {
                    this.rect.set((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) (this.photoImage.getImageX() + this.photoImage.getImageWidth()), (float) (this.photoImage.getImageY() + this.photoImage.getImageHeight()));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), docBackPaint);
                    if (this.currentMessageObject.isOutOwner()) {
                        this.radialProgress.setProgressColor(isDrawSelectedBackground() ? Theme.MSG_OUT_FILE_PROGRESS_SELECTED_COLOR : Theme.MSG_OUT_FILE_PROGRESS_COLOR);
                        dp2 = i;
                        i = i2;
                        i2 = imageX;
                    } else {
                        this.radialProgress.setProgressColor(isDrawSelectedBackground() ? Theme.MSG_IN_FILE_PROGRESS_SELECTED_COLOR : Theme.MSG_IN_FILE_PROGRESS_COLOR);
                        dp2 = i;
                        i = i2;
                        i2 = imageX;
                    }
                }
            } else {
                dp2 = (this.backgroundWidth + this.buttonX) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
                this.otherX = dp2;
                i = this.buttonY - AndroidUtilities.dp(5.0f);
                this.otherY = i;
                setDrawableBounds(drawable, dp2, i);
                imageX = this.buttonX + AndroidUtilities.dp(53.0f);
                i2 = this.buttonY + AndroidUtilities.dp(4.0f);
                i = AndroidUtilities.dp(27.0f) + this.buttonY;
                if (this.currentMessageObject.isOutOwner()) {
                    radialProgress2 = this.radialProgress;
                    dp2 = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.MSG_OUT_AUDIO_SELECTED_PROGRESS_COLOR : Theme.MSG_OUT_AUDIO_PROGRESS_COLOR;
                    radialProgress2.setProgressColor(dp2);
                    dp2 = i;
                    i = i2;
                    i2 = imageX;
                } else {
                    radialProgress2 = this.radialProgress;
                    dp2 = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.MSG_IN_AUDIO_SELECTED_PROGRESS_COLOR : -1;
                    radialProgress2.setProgressColor(dp2);
                    dp2 = i;
                    i = i2;
                    i2 = imageX;
                }
            }
            drawable.draw(canvas);
            try {
                if (this.docTitleLayout != null) {
                    canvas.save();
                    canvas.translate((float) (this.docTitleOffsetX + i2), (float) i);
                    this.docTitleLayout.draw(canvas);
                    canvas.restore();
                }
            } catch (Throwable e22) {
                FileLog.m18e("tmessages", e22);
            }
            try {
                if (this.infoLayout != null) {
                    canvas.save();
                    canvas.translate((float) i2, (float) dp2);
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                }
            } catch (Throwable e222) {
                FileLog.m18e("tmessages", e222);
            }
        }
        if (this.drawImageButton && this.photoImage.getVisible()) {
            this.radialProgress.draw(canvas);
        }
        if (!this.botButtons.isEmpty()) {
            if (this.currentMessageObject.isOutOwner()) {
                dp = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                dp = AndroidUtilities.dp(this.mediaBackground ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 7.0f) + this.backgroundDrawableLeft;
            }
            i4 = DOCUMENT_ATTACH_TYPE_NONE;
            while (i4 < this.botButtons.size()) {
                BotButton botButton = (BotButton) this.botButtons.get(i4);
                dp2 = (botButton.f2678y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                Theme.systemDrawable.setColorFilter(i4 == this.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                Theme.systemDrawable.setBounds(botButton.f2677x + dp, dp2, (botButton.f2677x + dp) + botButton.width, botButton.height + dp2);
                Theme.systemDrawable.draw(canvas);
                canvas.save();
                canvas.translate((float) ((botButton.f2677x + dp) + AndroidUtilities.dp(5.0f)), (float) (((AndroidUtilities.dp(44.0f) - botButton.title.getLineBottom(botButton.title.getLineCount() - 1)) / DOCUMENT_ATTACH_TYPE_GIF) + dp2));
                botButton.title.draw(canvas);
                canvas.restore();
                if (botButton.button instanceof TL_keyboardButtonUrl) {
                    setDrawableBounds(Theme.botLink, (((botButton.f2677x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.botLink.getIntrinsicWidth()) + dp, dp2 + AndroidUtilities.dp(3.0f));
                    Theme.botLink.draw(canvas);
                } else if (botButton.button instanceof TL_keyboardButtonSwitchInline) {
                    setDrawableBounds(Theme.botInline, (((botButton.f2677x + botButton.width) - AndroidUtilities.dp(3.0f)) - Theme.botInline.getIntrinsicWidth()) + dp, dp2 + AndroidUtilities.dp(3.0f));
                    Theme.botInline.draw(canvas);
                } else if ((botButton.button instanceof TL_keyboardButtonCallback) || (botButton.button instanceof TL_keyboardButtonRequestGeoLocation) || (botButton.button instanceof TL_keyboardButtonGame)) {
                    Object obj = ((((botButton.button instanceof TL_keyboardButtonCallback) || (botButton.button instanceof TL_keyboardButtonGame)) && SendMessagesHelper.getInstance().isSendingCallback(this.currentMessageObject, botButton.button)) || ((botButton.button instanceof TL_keyboardButtonRequestGeoLocation) && SendMessagesHelper.getInstance().isSendingCurrentLocation(this.currentMessageObject, botButton.button))) ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE;
                    if (obj != null || (obj == null && botButton.progressAlpha != 0.0f)) {
                        botProgressPaint.setAlpha(Math.min(NalUnitUtil.EXTENDED_SAR, (int) (botButton.progressAlpha * 255.0f)));
                        i3 = ((botButton.f2677x + botButton.width) - AndroidUtilities.dp(12.0f)) + dp;
                        this.rect.set((float) i3, (float) (AndroidUtilities.dp(4.0f) + dp2), (float) (i3 + AndroidUtilities.dp(8.0f)), (float) (dp2 + AndroidUtilities.dp(12.0f)));
                        canvas.drawArc(this.rect, (float) botButton.angle, 220.0f, false, botProgressPaint);
                        invalidate(((int) this.rect.left) - AndroidUtilities.dp(2.0f), ((int) this.rect.top) - AndroidUtilities.dp(2.0f), ((int) this.rect.right) + AndroidUtilities.dp(2.0f), ((int) this.rect.bottom) + AndroidUtilities.dp(2.0f));
                        long currentTimeMillis = System.currentTimeMillis();
                        if (Math.abs(botButton.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                            long access$800 = currentTimeMillis - botButton.lastUpdateTime;
                            botButton.angle = (int) ((((float) (360 * access$800)) / 2000.0f) + ((float) botButton.angle));
                            botButton.angle = botButton.angle - ((botButton.angle / 360) * 360);
                            if (obj != null) {
                                if (botButton.progressAlpha < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                    botButton.progressAlpha = (((float) access$800) / 200.0f) + botButton.progressAlpha;
                                    if (botButton.progressAlpha > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                        botButton.progressAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                                    }
                                }
                            } else if (botButton.progressAlpha > 0.0f) {
                                botButton.progressAlpha = botButton.progressAlpha - (((float) access$800) / 200.0f);
                                if (botButton.progressAlpha < 0.0f) {
                                    botButton.progressAlpha = 0.0f;
                                }
                            }
                        }
                        botButton.lastUpdateTime = currentTimeMillis;
                    }
                }
                i4 += DOCUMENT_ATTACH_TYPE_DOCUMENT;
            }
        }
    }

    public static StaticLayout generateStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, int i2, int i3, int i4) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        StaticLayout staticLayout = new StaticLayout(charSequence, textPaint, i2, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        int i5 = DOCUMENT_ATTACH_TYPE_NONE;
        int i6 = DOCUMENT_ATTACH_TYPE_NONE;
        int i7 = i;
        while (i5 < i3) {
            staticLayout.getLineDirections(i5);
            if (staticLayout.getLineLeft(i5) != 0.0f || staticLayout.isRtlCharAt(staticLayout.getLineStart(i5)) || staticLayout.isRtlCharAt(staticLayout.getLineEnd(i5))) {
                i7 = i2;
            }
            int lineEnd = staticLayout.getLineEnd(i5);
            if (lineEnd != charSequence.length()) {
                lineEnd--;
                if (spannableStringBuilder.charAt(lineEnd + i6) == ' ') {
                    spannableStringBuilder.replace(lineEnd + i6, (lineEnd + i6) + DOCUMENT_ATTACH_TYPE_DOCUMENT, "\n");
                } else if (spannableStringBuilder.charAt(lineEnd + i6) != '\n') {
                    spannableStringBuilder.insert(lineEnd + i6, "\n");
                    i6 += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                }
                if (i5 == staticLayout.getLineCount() - 1) {
                    break;
                } else if (i5 == i4 - 1) {
                    i6 = i7;
                    break;
                } else {
                    i5 += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                }
            } else {
                i6 = i7;
                break;
            }
        }
        i6 = i7;
        return StaticLayoutEx.createStaticLayout(spannableStringBuilder, textPaint, i6, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, (float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), false, TruncateAt.END, i6, i4);
    }

    private Drawable getDrawableForCurrentState() {
        int i = DOCUMENT_ATTACH_TYPE_AUDIO;
        int i2 = DOCUMENT_ATTACH_TYPE_NONE;
        int i3 = DOCUMENT_ATTACH_TYPE_DOCUMENT;
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (!(this.radialProgress == null || this.currentMessageObject == null)) {
                this.radialProgress.setShowSize(!this.currentMessageObject.mediaExists);
            }
            if (this.buttonState == -1) {
                return null;
            }
            this.radialProgress.setAlphaForPrevious(false);
            Drawable[] drawableArr = Theme.fileStatesDrawable[this.currentMessageObject.isOutOwner() ? this.buttonState : this.buttonState + DOCUMENT_ATTACH_TYPE_MUSIC];
            if (isDrawSelectedBackground() || this.buttonPressed != 0) {
                i2 = DOCUMENT_ATTACH_TYPE_DOCUMENT;
            }
            return drawableArr[i2];
        }
        if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT || this.drawPhotoImage) {
            this.radialProgress.setAlphaForPrevious(true);
            if (this.buttonState < 0 || this.buttonState >= DOCUMENT_ATTACH_TYPE_VIDEO) {
                if (this.buttonState == -1 && this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    drawableArr = Theme.photoStatesDrawables[this.currentMessageObject.isOutOwner() ? 9 : 12];
                    if (!isDrawSelectedBackground()) {
                        i3 = DOCUMENT_ATTACH_TYPE_NONE;
                    }
                    return drawableArr[i3];
                }
            } else if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                return Theme.photoStatesDrawables[this.buttonState][this.buttonPressed];
            } else {
                i = this.buttonState;
                if (this.buttonState == 0) {
                    i = this.currentMessageObject.isOutOwner() ? 7 : 10;
                } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    i = this.currentMessageObject.isOutOwner() ? 8 : 11;
                }
                drawableArr = Theme.photoStatesDrawables[i];
                if (isDrawSelectedBackground() || this.buttonPressed != 0) {
                    i2 = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                }
                return drawableArr[i2];
            }
        }
        this.radialProgress.setAlphaForPrevious(false);
        if (this.buttonState == -1) {
            Drawable[][] drawableArr2 = Theme.fileStatesDrawable;
            if (!this.currentMessageObject.isOutOwner()) {
                i = 8;
            }
            drawableArr = drawableArr2[i];
            if (!isDrawSelectedBackground()) {
                i3 = DOCUMENT_ATTACH_TYPE_NONE;
            }
            return drawableArr[i3];
        } else if (this.buttonState == 0) {
            drawableArr = Theme.fileStatesDrawable[this.currentMessageObject.isOutOwner() ? DOCUMENT_ATTACH_TYPE_GIF : 7];
            if (!isDrawSelectedBackground()) {
                i3 = DOCUMENT_ATTACH_TYPE_NONE;
            }
            return drawableArr[i3];
        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            drawableArr = Theme.fileStatesDrawable[this.currentMessageObject.isOutOwner() ? DOCUMENT_ATTACH_TYPE_VIDEO : 9];
            if (!isDrawSelectedBackground()) {
                i3 = DOCUMENT_ATTACH_TYPE_NONE;
            }
            return drawableArr[i3];
        }
        return null;
    }

    private int getMaxNameWidth() {
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_STICKER) {
            int minTabletSide = AndroidUtilities.isTablet() ? (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.isFromUser()) ? AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(42.0f) : AndroidUtilities.getMinTabletSide() : (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.isFromUser()) ? Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(42.0f) : Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            return (minTabletSide - this.backgroundWidth) - AndroidUtilities.dp(57.0f);
        }
        return this.backgroundWidth - AndroidUtilities.dp(this.mediaBackground ? 22.0f : 31.0f);
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            try {
                int i = AdvanceTheme.bz;
                int b = AdvanceTheme.m2283b(i, 21);
                int i2 = AdvanceTheme.bo;
                int b2 = AdvanceTheme.m2283b(i2, 21);
                int i3 = AdvanceTheme.br;
                if (this.currentMessageObject.isOut()) {
                    i3 = AdvanceTheme.bv;
                }
                replyTextPaint.linkColor = i3;
                Theme.backgroundDrawableOut.setColorFilter(i, Mode.SRC_IN);
                Theme.backgroundMediaDrawableOut.setColorFilter(i, Mode.SRC_IN);
                Theme.backgroundDrawableOutSelected.setColorFilter(b, Mode.SRC_IN);
                Theme.backgroundMediaDrawableOutSelected.setColorFilter(b, Mode.SRC_IN);
                Theme.backgroundDrawableIn.setColorFilter(i2, Mode.SRC_IN);
                Theme.backgroundMediaDrawableIn.setColorFilter(i2, Mode.SRC_IN);
                Theme.backgroundDrawableInSelected.setColorFilter(b2, Mode.SRC_IN);
                Theme.backgroundMediaDrawableInSelected.setColorFilter(b2, Mode.SRC_IN);
                i3 = AdvanceTheme.bC;
                Theme.checkDrawable.setColorFilter(i3, Mode.SRC_IN);
                Theme.halfCheckDrawable.setColorFilter(i3, Mode.SRC_IN);
                Theme.clockDrawable.setColorFilter(i3, Mode.SRC_IN);
                Theme.checkMediaDrawable.setColorFilter(i3, Mode.MULTIPLY);
                Theme.halfCheckMediaDrawable.setColorFilter(i3, Mode.MULTIPLY);
                Theme.halfCheckMediaDrawable.setColorFilter(i3, Mode.MULTIPLY);
                Theme.markerDrawable.setColorFilter(i3, Mode.SRC_IN);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    private void initTheme1() {
        if (ThemeUtil.m2490b()) {
            int dp = AndroidUtilities.dp((float) AdvanceTheme.by);
            this.avatarImage.setRoundRadius(dp);
            this.avatarDrawable.setRadius(dp);
        }
    }

    private void initThemeTimePaint() {
        if (ThemeUtil.m2490b()) {
            int c = this.currentMessageObject.isOutOwner() ? AdvanceTheme.bE : isDrawSelectedBackground() ? Theme.MSG_IN_VENUE_INFO_SELECTED_TEXT_COLOR : AdvanceTheme.m2286c(AdvanceTheme.bF, Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
            timePaint.setColor(c);
            initTheme();
        }
    }

    private boolean intersect(float f, float f2, float f3, float f4) {
        return f <= f3 ? f2 >= f3 : f <= f4;
    }

    private boolean isDrawSelectedBackground() {
        return (isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted);
    }

    private boolean isPhotoDataChanged(MessageObject messageObject) {
        if (messageObject.type == 0 || messageObject.type == 14) {
            return false;
        }
        if (messageObject.type == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (this.currentUrl == null) {
                return true;
            }
            double d = messageObject.messageOwner.media.geo.lat;
            double d2 = messageObject.messageOwner.media.geo._long;
            Object[] objArr = new Object[DOCUMENT_ATTACH_TYPE_MUSIC];
            objArr[DOCUMENT_ATTACH_TYPE_NONE] = Double.valueOf(d);
            objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Double.valueOf(d2);
            objArr[DOCUMENT_ATTACH_TYPE_GIF] = Integer.valueOf(Math.min(DOCUMENT_ATTACH_TYPE_GIF, (int) Math.ceil((double) AndroidUtilities.density)));
            objArr[DOCUMENT_ATTACH_TYPE_AUDIO] = Double.valueOf(d);
            objArr[DOCUMENT_ATTACH_TYPE_VIDEO] = Double.valueOf(d2);
            if (!String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", objArr).equals(this.currentUrl)) {
                return true;
            }
        } else if (this.currentPhotoObject == null || (this.currentPhotoObject.location instanceof TL_fileLocationUnavailable)) {
            return true;
        } else {
            if (this.currentMessageObject != null && this.photoNotSet && FileLoader.getPathToMessage(this.currentMessageObject.messageOwner).exists()) {
                return true;
            }
        }
        return false;
    }

    private boolean isUserDataChanged() {
        Object obj = null;
        if (this.currentMessageObject != null && !this.hasLinkPreview && this.currentMessageObject.messageOwner.media != null && (this.currentMessageObject.messageOwner.media.webpage instanceof TL_webPage)) {
            return true;
        }
        if (this.currentMessageObject == null || (this.currentUser == null && this.currentChat == null)) {
            return false;
        }
        if (this.lastSendState != this.currentMessageObject.messageOwner.send_state || this.lastDeleteDate != this.currentMessageObject.messageOwner.destroyTime || this.lastViewsCount != this.currentMessageObject.messageOwner.views) {
            return true;
        }
        User user;
        Chat chat;
        FileLocation fileLocation;
        PhotoSize closestPhotoSizeWithSize;
        String forwardedName;
        boolean z;
        if (this.currentMessageObject.isFromUser()) {
            User user2 = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
            setStatusColor(user2);
            user = user2;
            chat = null;
        } else if (this.currentMessageObject.messageOwner.from_id < 0) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(-this.currentMessageObject.messageOwner.from_id));
            user = null;
        } else if (this.currentMessageObject.messageOwner.post) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
            user = null;
        } else {
            chat = null;
            user = null;
        }
        if (this.isAvatarVisible) {
            if (user != null && user.photo != null) {
                fileLocation = user.photo.photo_small;
                if (this.replyTextLayout != null) {
                }
                if (this.currentPhoto != null) {
                }
                if (this.currentPhoto == null) {
                }
                if (this.currentPhoto == null) {
                }
                if (this.currentMessageObject.replyMessageObject != null) {
                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
                    fileLocation = closestPhotoSizeWithSize.location;
                    if (this.currentReplyPhoto != null) {
                    }
                    if (user != null) {
                        obj = UserObject.getUserName(user);
                    } else if (chat != null) {
                        obj = chat.title;
                    }
                    if (this.currentNameString != null) {
                    }
                    if (this.currentNameString == null) {
                    }
                    if (this.currentNameString == null) {
                    }
                    if (this.drawForwardedName) {
                        return false;
                    }
                    forwardedName = this.currentMessageObject.getForwardedName();
                    if (this.currentForwardNameString == null) {
                    }
                    return z;
                }
                fileLocation = null;
                if (this.currentReplyPhoto != null) {
                }
                if (user != null) {
                    obj = UserObject.getUserName(user);
                } else if (chat != null) {
                    obj = chat.title;
                }
                if (this.currentNameString != null) {
                }
                if (this.currentNameString == null) {
                }
                if (this.currentNameString == null) {
                }
                if (this.drawForwardedName) {
                    return false;
                }
                forwardedName = this.currentMessageObject.getForwardedName();
                if (this.currentForwardNameString == null) {
                }
                return z;
            } else if (!(chat == null || chat.photo == null)) {
                fileLocation = chat.photo.photo_small;
                if (this.replyTextLayout != null && this.currentMessageObject.replyMessageObject != null) {
                    return true;
                }
                if (this.currentPhoto != null && r3 != null) {
                    return true;
                }
                if (this.currentPhoto == null && r3 == null) {
                    return true;
                }
                if (this.currentPhoto == null && r3 != null && (this.currentPhoto.local_id != r3.local_id || this.currentPhoto.volume_id != r3.volume_id)) {
                    return true;
                }
                if (this.currentMessageObject.replyMessageObject != null) {
                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
                    if (!(closestPhotoSizeWithSize == null || this.currentMessageObject.replyMessageObject.type == 13)) {
                        fileLocation = closestPhotoSizeWithSize.location;
                        if (this.currentReplyPhoto != null && r3 != null) {
                            return true;
                        }
                        if (this.drawName && this.isChat && !this.currentMessageObject.isOutOwner()) {
                            if (user != null) {
                                obj = UserObject.getUserName(user);
                            } else if (chat != null) {
                                obj = chat.title;
                            }
                        }
                        if (this.currentNameString != null && r1 != null) {
                            return true;
                        }
                        if (this.currentNameString == null && r1 == null) {
                            return true;
                        }
                        if (this.currentNameString == null && r1 != null && !this.currentNameString.equals(r1)) {
                            return true;
                        }
                        if (this.drawForwardedName) {
                            return false;
                        }
                        forwardedName = this.currentMessageObject.getForwardedName();
                        z = ((this.currentForwardNameString == null || forwardedName == null) && ((this.currentForwardNameString == null || forwardedName != null) && (this.currentForwardNameString == null || forwardedName == null || this.currentForwardNameString.equals(forwardedName)))) ? DOCUMENT_ATTACH_TYPE_NONE : true;
                        return z;
                    }
                }
                fileLocation = null;
                if (this.currentReplyPhoto != null) {
                }
                if (user != null) {
                    obj = UserObject.getUserName(user);
                } else if (chat != null) {
                    obj = chat.title;
                }
                if (this.currentNameString != null) {
                }
                if (this.currentNameString == null) {
                }
                if (this.currentNameString == null) {
                }
                if (this.drawForwardedName) {
                    return false;
                }
                forwardedName = this.currentMessageObject.getForwardedName();
                if (this.currentForwardNameString == null) {
                }
                return z;
            }
        }
        fileLocation = null;
        if (this.replyTextLayout != null) {
        }
        if (this.currentPhoto != null) {
        }
        if (this.currentPhoto == null) {
        }
        if (this.currentPhoto == null) {
        }
        if (this.currentMessageObject.replyMessageObject != null) {
            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
            fileLocation = closestPhotoSizeWithSize.location;
            if (this.currentReplyPhoto != null) {
            }
            if (user != null) {
                obj = UserObject.getUserName(user);
            } else if (chat != null) {
                obj = chat.title;
            }
            if (this.currentNameString != null) {
            }
            if (this.currentNameString == null) {
            }
            if (this.currentNameString == null) {
            }
            if (this.drawForwardedName) {
                return false;
            }
            forwardedName = this.currentMessageObject.getForwardedName();
            if (this.currentForwardNameString == null) {
            }
            return z;
        }
        fileLocation = null;
        if (this.currentReplyPhoto != null) {
        }
        if (user != null) {
            obj = UserObject.getUserName(user);
        } else if (chat != null) {
            obj = chat.title;
        }
        if (this.currentNameString != null) {
        }
        if (this.currentNameString == null) {
        }
        if (this.currentNameString == null) {
        }
        if (this.drawForwardedName) {
            return false;
        }
        forwardedName = this.currentMessageObject.getForwardedName();
        if (this.currentForwardNameString == null) {
        }
        return z;
    }

    private void measureTime(MessageObject messageObject) {
        int i = (messageObject.isOutOwner() || messageObject.messageOwner.from_id <= 0 || !messageObject.messageOwner.post) ? DOCUMENT_ATTACH_TYPE_NONE : DOCUMENT_ATTACH_TYPE_DOCUMENT;
        User user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
        if (i != 0 && user == null) {
            i = DOCUMENT_ATTACH_TYPE_NONE;
        }
        User user2 = this.currentMessageObject.isFromUser() ? MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id)) : null;
        String format = (messageObject.messageOwner.via_bot_id == 0 && messageObject.messageOwner.via_bot_name == null && ((user2 == null || !user2.bot) && (messageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_EDITED) != 0)) ? LocaleController.getString("EditedMessage", C0338R.string.EditedMessage) + " " + LocaleController.getInstance().formatterEnDay.format(((long) messageObject.messageOwner.date) * 1000) : LocaleController.getInstance().formatterEnDay.format(((long) messageObject.messageOwner.date) * 1000);
        if (i != 0) {
            this.currentTimeString = ", " + format;
        } else {
            this.currentTimeString = format;
        }
        int ceil = (int) Math.ceil((double) timePaint.measureText(this.currentTimeString));
        this.timeWidth = ceil;
        this.timeTextWidth = ceil;
        if ((messageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
            Object[] objArr = new Object[DOCUMENT_ATTACH_TYPE_DOCUMENT];
            objArr[DOCUMENT_ATTACH_TYPE_NONE] = LocaleController.formatShortNumber(Math.max(DOCUMENT_ATTACH_TYPE_DOCUMENT, messageObject.messageOwner.views), null);
            this.currentViewsString = String.format("%s", objArr);
            this.viewsTextWidth = (int) Math.ceil((double) timePaint.measureText(this.currentViewsString));
            this.timeWidth += (this.viewsTextWidth + Theme.viewsCountDrawable[DOCUMENT_ATTACH_TYPE_NONE].getIntrinsicWidth()) + AndroidUtilities.dp(10.0f);
        }
        if (i != 0) {
            Object ellipsize;
            if (this.availableTimeWidth == 0) {
                this.availableTimeWidth = AndroidUtilities.dp(1000.0f);
            }
            CharSequence replace = ContactsController.formatName(user.first_name, user.last_name).replace('\n', ' ');
            i = this.availableTimeWidth - this.timeWidth;
            int ceil2 = (int) Math.ceil((double) timePaint.measureText(replace, DOCUMENT_ATTACH_TYPE_NONE, replace.length()));
            if (ceil2 > i) {
                ellipsize = TextUtils.ellipsize(replace, timePaint, (float) i, TruncateAt.END);
            } else {
                i = ceil2;
                CharSequence charSequence = replace;
            }
            this.currentTimeString = ellipsize + this.currentTimeString;
            this.timeTextWidth += i;
            this.timeWidth = i + this.timeWidth;
        }
    }

    private LinkPath obtainNewUrlPath(boolean z) {
        LinkPath linkPath;
        if (this.urlPathCache.isEmpty()) {
            linkPath = new LinkPath();
        } else {
            linkPath = (LinkPath) this.urlPathCache.get(DOCUMENT_ATTACH_TYPE_NONE);
            this.urlPathCache.remove(DOCUMENT_ATTACH_TYPE_NONE);
        }
        if (z) {
            this.urlPathSelection.add(linkPath);
        } else {
            this.urlPath.add(linkPath);
        }
        return linkPath;
    }

    private void resetPressedLink(int i) {
        if (this.pressedLink == null) {
            return;
        }
        if (this.pressedLinkType == i || i == -1) {
            resetUrlPaths(false);
            this.pressedLink = null;
            this.pressedLinkType = -1;
            invalidate();
        }
    }

    private void resetUrlPaths(boolean z) {
        if (z) {
            if (!this.urlPathSelection.isEmpty()) {
                this.urlPathCache.addAll(this.urlPathSelection);
                this.urlPathSelection.clear();
            }
        } else if (!this.urlPath.isEmpty()) {
            this.urlPathCache.addAll(this.urlPath);
            this.urlPath.clear();
        }
    }

    private void setMessageObjectInternal(MessageObject messageObject) {
        CharSequence charSequence;
        Object obj;
        String str;
        int i;
        CharSequence ellipsize;
        if ((messageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
            if (this.currentMessageObject.isContentUnread() && !this.currentMessageObject.isOut()) {
                MessagesController.getInstance().addToViewsQueue(this.currentMessageObject.messageOwner, false);
                this.currentMessageObject.setContentIsRead();
            } else if (!this.currentMessageObject.viewsReloaded) {
                MessagesController.getInstance().addToViewsQueue(this.currentMessageObject.messageOwner, true);
                this.currentMessageObject.viewsReloaded = true;
            }
        }
        if (this.currentMessageObject.isFromUser()) {
            this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
        } else if (this.currentMessageObject.messageOwner.from_id < 0) {
            this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-this.currentMessageObject.messageOwner.from_id));
        } else if (this.currentMessageObject.messageOwner.post) {
            this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
        }
        if (this.isChat && !messageObject.isOutOwner() && messageObject.isFromUser()) {
            this.isAvatarVisible = true;
            if (this.currentUser != null) {
                if (this.currentUser.photo != null) {
                    this.currentPhoto = this.currentUser.photo.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                this.avatarDrawable.setInfo(this.currentUser);
                this.drawStatus = MoboConstants.f1351r;
                setStatusColor(this.currentUser);
            } else if (this.currentChat != null) {
                if (this.currentChat.photo != null) {
                    this.currentPhoto = this.currentChat.photo.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                this.avatarDrawable.setInfo(this.currentChat);
            } else {
                this.currentPhoto = null;
                this.avatarDrawable.setInfo(messageObject.messageOwner.from_id, null, null, false);
            }
            this.avatarImage.setImage(this.currentPhoto, "50_50", this.avatarDrawable, null, false);
        }
        measureTime(messageObject);
        this.namesOffset = DOCUMENT_ATTACH_TYPE_NONE;
        String str2 = null;
        CharSequence charSequence2 = null;
        if (messageObject.messageOwner.via_bot_id != 0) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.via_bot_id));
            if (!(user == null || user.username == null || user.username.length() <= 0)) {
                str2 = "@" + user.username;
                Object[] objArr = new Object[DOCUMENT_ATTACH_TYPE_DOCUMENT];
                objArr[DOCUMENT_ATTACH_TYPE_NONE] = str2;
                charSequence2 = AndroidUtilities.replaceTags(String.format(" via <b>%s</b>", objArr));
                this.viaWidth = (int) Math.ceil((double) replyNamePaint.measureText(charSequence2, DOCUMENT_ATTACH_TYPE_NONE, charSequence2.length()));
                this.currentViaBotUser = user;
            }
            charSequence = charSequence2;
            obj = str2;
        } else if (messageObject.messageOwner.via_bot_name == null || messageObject.messageOwner.via_bot_name.length() <= 0) {
            charSequence = null;
            r9 = null;
        } else {
            str2 = "@" + messageObject.messageOwner.via_bot_name;
            Object[] objArr2 = new Object[DOCUMENT_ATTACH_TYPE_DOCUMENT];
            objArr2[DOCUMENT_ATTACH_TYPE_NONE] = str2;
            charSequence2 = AndroidUtilities.replaceTags(String.format(" via <b>%s</b>", objArr2));
            this.viaWidth = (int) Math.ceil((double) replyNamePaint.measureText(charSequence2, DOCUMENT_ATTACH_TYPE_NONE, charSequence2.length()));
            charSequence = charSequence2;
            r9 = str2;
        }
        Object obj2 = (this.drawName && this.isChat && !this.currentMessageObject.isOutOwner()) ? DOCUMENT_ATTACH_TYPE_DOCUMENT : null;
        Object obj3 = ((messageObject.messageOwner.fwd_from == null || messageObject.type == 14) && obj != null) ? DOCUMENT_ATTACH_TYPE_DOCUMENT : null;
        if (obj2 == null && obj3 == null) {
            this.currentNameString = null;
            this.nameLayout = null;
            this.nameWidth = DOCUMENT_ATTACH_TYPE_NONE;
        } else {
            this.drawNameLayout = true;
            this.nameWidth = getMaxNameWidth();
            if (this.nameWidth < 0) {
                this.nameWidth = AndroidUtilities.dp(100.0f);
            }
            if (obj2 == null) {
                this.currentNameString = TtmlNode.ANONYMOUS_REGION_ID;
            } else if (this.currentUser != null) {
                this.currentNameString = UserObject.getUserName(this.currentUser);
                if (ThemeUtil.m2490b()) {
                    str = this.currentUser.username;
                    if (str != null && AdvanceTheme.bZ) {
                        this.currentNameString = this.currentNameString.replaceAll("\\p{C}", " ");
                        this.currentNameString = this.currentNameString.trim().replaceAll(" +", " ") + " [@" + str + "]";
                    }
                }
            } else if (this.currentChat != null) {
                this.currentNameString = this.currentChat.title;
            } else {
                this.currentNameString = "DELETED";
            }
            CharSequence ellipsize2 = TextUtils.ellipsize(this.currentNameString.replace('\n', ' '), namePaint, (float) (this.nameWidth - (obj3 != null ? this.viaWidth : DOCUMENT_ATTACH_TYPE_NONE)), TruncateAt.END);
            if (obj3 != null) {
                this.viaNameWidth = (int) Math.ceil((double) namePaint.measureText(ellipsize2, DOCUMENT_ATTACH_TYPE_NONE, ellipsize2.length()));
                if (this.viaNameWidth != 0) {
                    this.viaNameWidth += AndroidUtilities.dp(4.0f);
                }
                i = this.currentMessageObject.type == 13 ? -1 : this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_VIA_BOT_NAME_TEXT_COLOR : Theme.STICKERS_SHEET_SEND_TEXT_COLOR;
                if (ThemeUtil.m2490b()) {
                    i = this.currentMessageObject.isOutOwner() ? AdvanceTheme.bw : AdvanceTheme.bx;
                }
                SpannableStringBuilder spannableStringBuilder;
                if (this.currentNameString.length() > 0) {
                    Object[] objArr3 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                    objArr3[DOCUMENT_ATTACH_TYPE_NONE] = ellipsize2;
                    objArr3[DOCUMENT_ATTACH_TYPE_DOCUMENT] = obj;
                    spannableStringBuilder = new SpannableStringBuilder(String.format("%s via %s", objArr3));
                    spannableStringBuilder.setSpan(new TypefaceSpan(Typeface.DEFAULT, DOCUMENT_ATTACH_TYPE_NONE, i), ellipsize2.length() + DOCUMENT_ATTACH_TYPE_DOCUMENT, ellipsize2.length() + DOCUMENT_ATTACH_TYPE_VIDEO, 33);
                    spannableStringBuilder.setSpan(new TypefaceSpan(FontUtil.m1176a().m1160c(), DOCUMENT_ATTACH_TYPE_NONE, i), ellipsize2.length() + DOCUMENT_ATTACH_TYPE_MUSIC, spannableStringBuilder.length(), 33);
                    charSequence2 = spannableStringBuilder;
                } else {
                    objArr = new Object[DOCUMENT_ATTACH_TYPE_DOCUMENT];
                    objArr[DOCUMENT_ATTACH_TYPE_NONE] = obj;
                    spannableStringBuilder = new SpannableStringBuilder(String.format("via %s", objArr));
                    spannableStringBuilder.setSpan(new TypefaceSpan(Typeface.DEFAULT, DOCUMENT_ATTACH_TYPE_NONE, i), DOCUMENT_ATTACH_TYPE_NONE, DOCUMENT_ATTACH_TYPE_VIDEO, 33);
                    spannableStringBuilder.setSpan(new TypefaceSpan(FontUtil.m1176a().m1160c(), DOCUMENT_ATTACH_TYPE_NONE, i), DOCUMENT_ATTACH_TYPE_VIDEO, spannableStringBuilder.length(), 33);
                    obj2 = spannableStringBuilder;
                }
                ellipsize = TextUtils.ellipsize(charSequence2, namePaint, (float) this.nameWidth, TruncateAt.END);
            } else {
                ellipsize = ellipsize2;
            }
            try {
                this.nameLayout = new StaticLayout(ellipsize, namePaint, this.nameWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                if (this.nameLayout == null || this.nameLayout.getLineCount() <= 0) {
                    this.nameWidth = DOCUMENT_ATTACH_TYPE_NONE;
                    if (this.currentNameString.length() == 0) {
                        this.currentNameString = null;
                    }
                } else {
                    this.nameWidth = (int) Math.ceil((double) this.nameLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE));
                    if (messageObject.type != 13) {
                        this.namesOffset += AndroidUtilities.dp(19.0f);
                    }
                    this.nameOffsetX = this.nameLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.currentNameString.length() == 0) {
                        this.currentNameString = null;
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        this.currentForwardUser = null;
        this.currentForwardNameString = null;
        this.currentForwardChannel = null;
        this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] = null;
        this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_DOCUMENT] = null;
        this.forwardedNameWidth = DOCUMENT_ATTACH_TYPE_NONE;
        if (this.drawForwardedName && messageObject.isForwarded()) {
            if (messageObject.messageOwner.fwd_from.channel_id != 0) {
                this.currentForwardChannel = MessagesController.getInstance().getChat(Integer.valueOf(messageObject.messageOwner.fwd_from.channel_id));
            }
            if (messageObject.messageOwner.fwd_from.from_id != 0) {
                this.currentForwardUser = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.fwd_from.from_id));
            }
            if (!(this.currentForwardUser == null && this.currentForwardChannel == null)) {
                if (this.currentForwardChannel != null) {
                    if (this.currentForwardUser != null) {
                        Object[] objArr4 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                        objArr4[DOCUMENT_ATTACH_TYPE_NONE] = this.currentForwardChannel.title;
                        objArr4[DOCUMENT_ATTACH_TYPE_DOCUMENT] = UserObject.getUserName(this.currentForwardUser);
                        this.currentForwardNameString = String.format("%s (%s)", objArr4);
                    } else {
                        this.currentForwardNameString = this.currentForwardChannel.title;
                    }
                } else if (this.currentForwardUser != null) {
                    this.currentForwardNameString = UserObject.getUserName(this.currentForwardUser);
                }
                this.forwardedNameWidth = getMaxNameWidth();
                if (ThemeUtil.m2490b()) {
                    if (this.currentMessageObject.isOutOwner()) {
                        forwardNamePaint.setColor(AdvanceTheme.bw);
                    } else {
                        forwardNamePaint.setColor(AdvanceTheme.bx);
                    }
                }
                charSequence2 = TextUtils.ellipsize(this.currentForwardNameString.replace('\n', ' '), replyNamePaint, (float) ((this.forwardedNameWidth - ((int) Math.ceil((double) forwardNamePaint.measureText(LocaleController.getString("From", C0338R.string.From) + " ")))) - this.viaWidth), TruncateAt.END);
                if (charSequence != null) {
                    this.viaNameWidth = (int) Math.ceil((double) forwardNamePaint.measureText(LocaleController.getString("From", C0338R.string.From) + " " + charSequence2));
                    objArr2 = new Object[DOCUMENT_ATTACH_TYPE_AUDIO];
                    objArr2[DOCUMENT_ATTACH_TYPE_NONE] = LocaleController.getString("From", C0338R.string.From);
                    objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = charSequence2;
                    objArr2[DOCUMENT_ATTACH_TYPE_GIF] = obj;
                    charSequence2 = AndroidUtilities.replaceTags(String.format("%s <b>%s</b> via <b>%s</b>", objArr2));
                } else {
                    objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                    objArr2[DOCUMENT_ATTACH_TYPE_NONE] = LocaleController.getString("From", C0338R.string.From);
                    objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = charSequence2;
                    charSequence2 = AndroidUtilities.replaceTags(String.format("%s <b>%s</b>", objArr2));
                }
                try {
                    this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_DOCUMENT] = new StaticLayout(TextUtils.ellipsize(charSequence2, forwardNamePaint, (float) this.forwardedNameWidth, TruncateAt.END), forwardNamePaint, this.forwardedNameWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                    this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] = new StaticLayout(TextUtils.ellipsize(AndroidUtilities.replaceTags(LocaleController.getString("ForwardedMessage", C0338R.string.ForwardedMessage)), forwardNamePaint, (float) this.forwardedNameWidth, TruncateAt.END), forwardNamePaint, this.forwardedNameWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                    this.forwardedNameWidth = Math.max((int) Math.ceil((double) this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE].getLineWidth(DOCUMENT_ATTACH_TYPE_NONE)), (int) Math.ceil((double) this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_DOCUMENT].getLineWidth(DOCUMENT_ATTACH_TYPE_NONE)));
                    this.forwardNameOffsetX[DOCUMENT_ATTACH_TYPE_NONE] = this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE].getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                    this.forwardNameOffsetX[DOCUMENT_ATTACH_TYPE_DOCUMENT] = this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_DOCUMENT].getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                    this.namesOffset += AndroidUtilities.dp(36.0f);
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
        }
        if (messageObject.isReply()) {
            int i2;
            this.namesOffset += AndroidUtilities.dp(42.0f);
            if (messageObject.type != 0) {
                if (messageObject.type == 13) {
                    this.namesOffset -= AndroidUtilities.dp(42.0f);
                } else {
                    this.namesOffset += AndroidUtilities.dp(5.0f);
                }
            }
            i = getMaxNameWidth();
            int dp = messageObject.type != 13 ? i - AndroidUtilities.dp(10.0f) : i;
            charSequence = null;
            if (messageObject.replyMessageObject != null) {
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.replyMessageObject.photoThumbs2, 80);
                PhotoSize closestPhotoSizeWithSize2 = closestPhotoSizeWithSize == null ? FileLoader.getClosestPhotoSizeWithSize(messageObject.replyMessageObject.photoThumbs, 80) : closestPhotoSizeWithSize;
                if (closestPhotoSizeWithSize2 == null || messageObject.replyMessageObject.type == 13 || ((messageObject.type == 13 && !AndroidUtilities.isTablet()) || messageObject.replyMessageObject.isSecretMedia())) {
                    this.replyImageReceiver.setImageBitmap((Drawable) null);
                    this.needReplyImage = false;
                } else {
                    this.currentReplyPhoto = closestPhotoSizeWithSize2.location;
                    this.replyImageReceiver.setImage(closestPhotoSizeWithSize2.location, "50_50", null, null, true);
                    this.needReplyImage = true;
                    dp -= AndroidUtilities.dp(44.0f);
                }
                str = null;
                if (messageObject.replyMessageObject.isFromUser()) {
                    User user2 = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.replyMessageObject.messageOwner.from_id));
                    if (user2 != null) {
                        str = UserObject.getUserName(user2);
                    }
                } else if (messageObject.replyMessageObject.messageOwner.from_id < 0) {
                    r1 = MessagesController.getInstance().getChat(Integer.valueOf(-messageObject.replyMessageObject.messageOwner.from_id));
                    if (r1 != null) {
                        str = r1.title;
                    }
                } else {
                    r1 = MessagesController.getInstance().getChat(Integer.valueOf(messageObject.replyMessageObject.messageOwner.to_id.channel_id));
                    if (r1 != null) {
                        str = r1.title;
                    }
                }
                charSequence2 = str != null ? TextUtils.ellipsize(str.replace('\n', ' '), replyNamePaint, (float) dp, TruncateAt.END) : null;
                if (messageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) {
                    charSequence = TextUtils.ellipsize(Emoji.replaceEmoji(messageObject.replyMessageObject.messageOwner.media.game.title, replyTextPaint.getFontMetricsInt(), AndroidUtilities.dp(14.0f), false), replyTextPaint, (float) dp, TruncateAt.END);
                    i2 = dp;
                    ellipsize = charSequence2;
                } else if (messageObject.replyMessageObject.messageText == null || messageObject.replyMessageObject.messageText.length() <= 0) {
                    ellipsize = charSequence2;
                    i2 = dp;
                } else {
                    str2 = messageObject.replyMessageObject.messageText.toString();
                    if (str2.length() > 150) {
                        str2 = str2.substring(DOCUMENT_ATTACH_TYPE_NONE, 150);
                    }
                    charSequence = TextUtils.ellipsize(Emoji.replaceEmoji(str2.replace('\n', ' '), replyTextPaint.getFontMetricsInt(), AndroidUtilities.dp(14.0f), false), replyTextPaint, (float) dp, TruncateAt.END);
                    i2 = dp;
                    ellipsize = charSequence2;
                }
            } else {
                ellipsize = null;
                i2 = dp;
            }
            if (ellipsize == null) {
                ellipsize = LocaleController.getString("Loading", C0338R.string.Loading);
            }
            try {
                this.replyNameLayout = new StaticLayout(ellipsize, replyNamePaint, AndroidUtilities.dp(6.0f) + i2, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                if (this.replyNameLayout.getLineCount() > 0) {
                    this.replyNameWidth = AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : DOCUMENT_ATTACH_TYPE_NONE) + 12)) + ((int) Math.ceil((double) this.replyNameLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE)));
                    this.replyNameOffset = this.replyNameLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                }
            } catch (Throwable e22) {
                FileLog.m18e("tmessages", e22);
            }
            if (charSequence != null) {
                try {
                    this.replyTextLayout = new StaticLayout(charSequence, replyTextPaint, i2 + AndroidUtilities.dp(6.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                    if (this.replyTextLayout.getLineCount() > 0) {
                        this.replyTextWidth = AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : DOCUMENT_ATTACH_TYPE_NONE) + 12)) + ((int) Math.ceil((double) this.replyTextLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE)));
                        this.replyTextOffset = this.replyTextLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                    }
                } catch (Throwable e222) {
                    FileLog.m18e("tmessages", e222);
                }
            }
        }
        requestLayout();
    }

    private boolean setStatusColor(User user) {
        if (user == null || !this.drawStatus) {
            return false;
        }
        String formatUserStatus = LocaleController.formatUserStatus(user);
        if (formatUserStatus == null || formatUserStatus.equals(this.lastStatus)) {
            return false;
        }
        this.lastStatus = formatUserStatus;
        if (formatUserStatus.equals(LocaleController.getString("ALongTimeAgo", C0338R.string.ALongTimeAgo))) {
            this.statusBG.setColor(Theme.MSG_TEXT_COLOR);
        } else if (formatUserStatus.equals(LocaleController.getString("Online", C0338R.string.Online))) {
            this.statusBG.setColor(-16718218);
        } else if (formatUserStatus.equals(LocaleController.getString("Lately", C0338R.string.Lately))) {
            this.statusBG.setColor(-3355444);
        } else {
            this.statusBG.setColor(-7829368);
        }
        return true;
    }

    private void updateSecretTimeText(MessageObject messageObject) {
        if (messageObject != null && !messageObject.isOut()) {
            CharSequence secretTimeString = messageObject.getSecretTimeString();
            if (secretTimeString != null) {
                this.infoWidth = (int) Math.ceil((double) infoPaint.measureText(secretTimeString));
                this.infoLayout = new StaticLayout(TextUtils.ellipsize(secretTimeString, infoPaint, (float) this.infoWidth, TruncateAt.END), infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                invalidate();
            }
        }
    }

    private void updateWaveform() {
        boolean z = false;
        if (this.currentMessageObject != null && this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
            for (int i = DOCUMENT_ATTACH_TYPE_NONE; i < this.documentAttach.attributes.size(); i += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                DocumentAttribute documentAttribute = (DocumentAttribute) this.documentAttach.attributes.get(i);
                if (documentAttribute instanceof TL_documentAttributeAudio) {
                    if (documentAttribute.waveform == null || documentAttribute.waveform.length == 0) {
                        MediaController.m71a().m174e(this.currentMessageObject);
                    }
                    if (documentAttribute.waveform != null) {
                        z = true;
                    }
                    this.useSeekBarWaweform = z;
                    this.seekBarWaveform.setWaveform(documentAttribute.waveform);
                    return;
                }
            }
        }
    }

    public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
        if (this.currentMessageObject != null && z && !z2 && !this.currentMessageObject.mediaExists && !this.currentMessageObject.attachPathExists) {
            this.currentMessageObject.mediaExists = true;
            updateButtonState(true);
        }
    }

    public void downloadAudioIfNeed() {
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO && this.documentAttach.size < AccessibilityNodeInfoCompat.ACTION_DISMISS && this.buttonState == DOCUMENT_ATTACH_TYPE_GIF) {
            FileLoader.getInstance().loadFile(this.documentAttach, true, false);
            this.buttonState = DOCUMENT_ATTACH_TYPE_VIDEO;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
        }
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public ImageReceiver getPhotoImage() {
        return this.photoImage;
    }

    public boolean isChangeShareWithReplyButton() {
        return this.changeShareWithReplyButton;
    }

    public boolean isFavoriteAndAutoDownload() {
        boolean z = false;
        if (this.currentMessageObject == null) {
            return false;
        }
        int i = FavoriteUtil.m1142b(Long.valueOf(this.currentMessageObject.getDialogId())) ? MoboConstants.f1346m : DOCUMENT_ATTACH_TYPE_NONE;
        DialogSettings a = DialogSettingsUtil.m997a(this.currentMessageObject.getDialogId());
        if (a != null) {
            i |= a.m980c();
        }
        if (i > 0) {
            switch (this.currentMessageObject.type) {
                case DOCUMENT_ATTACH_TYPE_DOCUMENT /*1*/:
                    if ((i & DOCUMENT_ATTACH_TYPE_DOCUMENT) != 0) {
                        z = true;
                    }
                    return z;
                case DOCUMENT_ATTACH_TYPE_GIF /*2*/:
                    return (i & DOCUMENT_ATTACH_TYPE_GIF) != 0;
                case DOCUMENT_ATTACH_TYPE_AUDIO /*3*/:
                    return (i & DOCUMENT_ATTACH_TYPE_VIDEO) != 0;
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    return (i & 32) != 0;
                case C0338R.styleable.PromptView_iconTint /*9*/:
                case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                    return (i & 8) != 0;
                case C0338R.styleable.PromptView_primaryTextFontFamily /*14*/:
                    return (i & 16) != 0;
            }
        }
        return false;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
        this.replyImageReceiver.onAttachedToWindow();
        if (!this.drawPhotoImage) {
            updateButtonState(false);
        } else if (this.photoImage.onAttachedToWindow()) {
            updateButtonState(false);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
        this.replyImageReceiver.onDetachedFromWindow();
        this.photoImage.onDetachedFromWindow();
        MediaController.m71a().m149a((FileDownloadProgressListener) this);
    }

    protected void onDraw(Canvas canvas) {
        boolean z = true;
        if (this.currentMessageObject != null) {
            if (this.wasLayout) {
                Drawable drawable;
                int dp;
                int i;
                if (this.isAvatarVisible) {
                    this.avatarImage.draw(canvas);
                    if (this.drawStatus) {
                        setDrawableBounds(this.statusBG, AndroidUtilities.dp(35.0f), this.layoutHeight - AndroidUtilities.dp(15.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f));
                        this.statusBG.draw(canvas);
                    }
                }
                if (this.mediaBackground) {
                    timePaint.setColor(-1);
                } else if (this.currentMessageObject.isOutOwner()) {
                    timePaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_TIME_TEXT_COLOR : Theme.MSG_OUT_TIME_TEXT_COLOR);
                } else {
                    timePaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_VENUE_INFO_SELECTED_TEXT_COLOR : Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
                }
                initThemeTimePaint();
                if (this.currentMessageObject.isOutOwner()) {
                    if (isDrawSelectedBackground()) {
                        if (this.mediaBackground) {
                            this.currentBackgroundDrawable = Theme.backgroundMediaDrawableOutSelected;
                        } else {
                            this.currentBackgroundDrawable = Theme.backgroundDrawableOutSelected;
                        }
                    } else if (this.mediaBackground) {
                        this.currentBackgroundDrawable = Theme.backgroundMediaDrawableOut;
                    } else {
                        this.currentBackgroundDrawable = Theme.backgroundDrawableOut;
                    }
                    drawable = this.currentBackgroundDrawable;
                    dp = (this.layoutWidth - this.backgroundWidth) - (!this.mediaBackground ? DOCUMENT_ATTACH_TYPE_NONE : AndroidUtilities.dp(9.0f));
                    this.backgroundDrawableLeft = dp;
                    setDrawableBounds(drawable, dp, AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), this.backgroundWidth - (this.mediaBackground ? DOCUMENT_ATTACH_TYPE_NONE : AndroidUtilities.dp(3.0f)), this.layoutHeight - AndroidUtilities.dp(2.0f));
                } else {
                    if (isDrawSelectedBackground()) {
                        if (this.mediaBackground) {
                            this.currentBackgroundDrawable = Theme.backgroundMediaDrawableInSelected;
                        } else {
                            this.currentBackgroundDrawable = Theme.backgroundDrawableInSelected;
                        }
                    } else if (this.mediaBackground) {
                        this.currentBackgroundDrawable = Theme.backgroundMediaDrawableIn;
                    } else {
                        this.currentBackgroundDrawable = Theme.backgroundDrawableIn;
                    }
                    if (this.isChat && this.currentMessageObject.isFromUser()) {
                        drawable = this.currentBackgroundDrawable;
                        dp = AndroidUtilities.dp((float) ((!this.mediaBackground ? DOCUMENT_ATTACH_TYPE_AUDIO : 9) + 48));
                        this.backgroundDrawableLeft = dp;
                        setDrawableBounds(drawable, dp, AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), this.backgroundWidth - (this.mediaBackground ? DOCUMENT_ATTACH_TYPE_NONE : AndroidUtilities.dp(3.0f)), this.layoutHeight - AndroidUtilities.dp(2.0f));
                    } else {
                        drawable = this.currentBackgroundDrawable;
                        dp = !this.mediaBackground ? AndroidUtilities.dp(3.0f) : AndroidUtilities.dp(9.0f);
                        this.backgroundDrawableLeft = dp;
                        setDrawableBounds(drawable, dp, AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), this.backgroundWidth - (this.mediaBackground ? DOCUMENT_ATTACH_TYPE_NONE : AndroidUtilities.dp(3.0f)), this.layoutHeight - AndroidUtilities.dp(2.0f));
                    }
                }
                if (this.drawBackground && this.currentBackgroundDrawable != null) {
                    this.currentBackgroundDrawable.draw(canvas);
                }
                drawContent(canvas);
                if (this.layoutHeight - AndroidUtilities.dp(76.0f) < DOCUMENT_ATTACH_TYPE_MUSIC && this.drawShareButton && this.drawFavoriteButton) {
                    this.drawMenuButton = true;
                } else {
                    this.drawMenuButton = false;
                }
                if (this.drawShareButton && !this.drawMenuButton) {
                    Theme.shareDrawable.setColorFilter(this.sharePressed ? Theme.colorPressedFilter : Theme.colorFilter);
                    if (this.currentMessageObject.isOutOwner()) {
                        this.shareStartX = (this.currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(8.0f)) - Theme.shareDrawable.getIntrinsicWidth();
                    } else {
                        this.shareStartX = this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(8.0f);
                    }
                    this.shareStartY = this.layoutHeight - AndroidUtilities.dp(41.0f);
                    if (this.shareStartY < 0) {
                        this.shareStartY = DOCUMENT_ATTACH_TYPE_NONE;
                    }
                    setDrawableBounds(Theme.shareDrawable, this.shareStartX, this.shareStartY);
                    Theme.shareDrawable.draw(canvas);
                    if (this.changeShareWithReplyButton) {
                        setDrawableBounds(Theme.replyIconDrawable, this.shareStartX + AndroidUtilities.dp(9.0f), this.shareStartY + AndroidUtilities.dp(9.0f));
                        Theme.replyIconDrawable.draw(canvas);
                    } else {
                        setDrawableBounds(Theme.shareIconDrawable, this.shareStartX + AndroidUtilities.dp(9.0f), this.shareStartY + AndroidUtilities.dp(9.0f));
                        Theme.shareIconDrawable.draw(canvas);
                    }
                }
                if (this.drawFavoriteButton && !this.drawMenuButton) {
                    Theme.favoriteDrawable.setColorFilter(this.favoritePressed ? Theme.colorPressedFilter : Theme.colorFilter);
                    if (this.currentMessageObject.isOutOwner()) {
                        this.favoriteStartX = (this.currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(8.0f)) - Theme.favoriteDrawable.getIntrinsicWidth();
                    } else {
                        this.favoriteStartX = this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(8.0f);
                    }
                    this.favoriteStartY = this.drawShareButton ? this.layoutHeight - AndroidUtilities.dp(76.0f) : this.layoutHeight - AndroidUtilities.dp(41.0f);
                    if (this.favoriteStartY < 0) {
                        this.favoriteStartY = DOCUMENT_ATTACH_TYPE_NONE;
                    }
                    setDrawableBounds(Theme.favoriteDrawable, this.favoriteStartX, this.favoriteStartY);
                    Theme.favoriteDrawable.draw(canvas);
                    if (ArchiveUtil.m264a(this.currentMessageObject)) {
                        setDrawableBounds(Theme.favoriteDoneIconDrawable, this.favoriteStartX + AndroidUtilities.dp(9.0f), this.favoriteStartY + AndroidUtilities.dp(9.0f));
                        Theme.favoriteDoneIconDrawable.draw(canvas);
                    } else {
                        setDrawableBounds(Theme.favoriteIconDrawable, this.favoriteStartX + AndroidUtilities.dp(9.0f), this.favoriteStartY + AndroidUtilities.dp(9.0f));
                        Theme.favoriteIconDrawable.draw(canvas);
                    }
                }
                if (this.drawMenuButton) {
                    Theme.menuDrawable.setColorFilter(this.menuPressed ? Theme.colorPressedFilter : Theme.colorFilter);
                    if (this.currentMessageObject.isOutOwner()) {
                        this.menuStartX = (this.currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(8.0f)) - Theme.menuDrawable.getIntrinsicWidth();
                    } else {
                        this.menuStartX = this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(8.0f);
                    }
                    this.menuStartY = this.layoutHeight - AndroidUtilities.dp(41.0f);
                    if (this.menuStartY < 0) {
                        this.menuStartY = DOCUMENT_ATTACH_TYPE_NONE;
                    }
                    setDrawableBounds(Theme.menuDrawable, this.menuStartX, this.menuStartY);
                    Theme.menuDrawable.draw(canvas);
                    setDrawableBounds(Theme.menuIconDrawable, this.menuStartX + AndroidUtilities.dp(9.0f), this.menuStartY + AndroidUtilities.dp(9.0f));
                    Theme.menuIconDrawable.draw(canvas);
                }
                boolean z2 = AdvanceTheme.ca && ThemeUtil.m2490b();
                int i2 = AdvanceTheme.bB;
                if (this.drawNameLayout && this.nameLayout != null) {
                    canvas.save();
                    if (this.currentMessageObject.type == 13) {
                        namePaint.setColor(-1);
                        if (this.currentMessageObject.isOutOwner()) {
                            this.nameX = (float) AndroidUtilities.dp(28.0f);
                        } else {
                            this.nameX = (float) (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(22.0f));
                        }
                        this.nameY = (float) (this.layoutHeight - AndroidUtilities.dp(38.0f));
                        Theme.systemDrawable.setColorFilter(Theme.colorFilter);
                        Theme.systemDrawable.setBounds(((int) this.nameX) - AndroidUtilities.dp(12.0f), ((int) this.nameY) - AndroidUtilities.dp(5.0f), (((int) this.nameX) + AndroidUtilities.dp(12.0f)) + this.nameWidth, ((int) this.nameY) + AndroidUtilities.dp(22.0f));
                        Theme.systemDrawable.draw(canvas);
                    } else {
                        if (this.mediaBackground || this.currentMessageObject.isOutOwner()) {
                            this.nameX = ((float) (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f))) - this.nameOffsetX;
                        } else {
                            this.nameX = ((float) (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0f))) - this.nameOffsetX;
                        }
                        if (this.currentUser != null) {
                            namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentUser.id));
                            if (z2) {
                                namePaint.setColor(i2);
                            }
                        } else if (this.currentChat != null) {
                            namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentChat.id));
                            if (z2) {
                                namePaint.setColor(i2);
                            }
                        } else {
                            namePaint.setColor(AvatarDrawable.getNameColorForId(DOCUMENT_ATTACH_TYPE_NONE));
                            if (z2) {
                                namePaint.setColor(i2);
                            }
                        }
                        this.nameY = (float) AndroidUtilities.dp(10.0f);
                    }
                    canvas.translate(this.nameX, this.nameY);
                    this.nameLayout.draw(canvas);
                    canvas.restore();
                }
                if (!(!this.drawForwardedName || this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] == null || this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_DOCUMENT] == null)) {
                    this.forwardNameY = AndroidUtilities.dp((float) ((this.drawNameLayout ? 19 : DOCUMENT_ATTACH_TYPE_NONE) + 10));
                    i = AdvanceTheme.f2491b;
                    if (this.currentMessageObject.isOutOwner()) {
                        forwardNamePaint.setColor(Theme.MSG_OUT_VIA_BOT_NAME_TEXT_COLOR);
                        this.forwardNameX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f);
                        if (ThemeUtil.m2490b()) {
                            forwardNamePaint.setColor(AdvanceTheme.bw);
                        }
                    } else {
                        forwardNamePaint.setColor(Theme.MSG_IN_FORDWARDED_NAME_TEXT_COLOR);
                        if (this.mediaBackground) {
                            this.forwardNameX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f);
                        } else {
                            this.forwardNameX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0f);
                        }
                        if (ThemeUtil.m2490b()) {
                            forwardNamePaint.setColor(AdvanceTheme.bx);
                        }
                    }
                    for (i = DOCUMENT_ATTACH_TYPE_NONE; i < DOCUMENT_ATTACH_TYPE_GIF; i += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        canvas.save();
                        canvas.translate(((float) this.forwardNameX) - this.forwardNameOffsetX[i], (float) (this.forwardNameY + (AndroidUtilities.dp(16.0f) * i)));
                        this.forwardedNameLayout[i].draw(canvas);
                        canvas.restore();
                    }
                }
                if (this.currentMessageObject.isReply()) {
                    i2 = AdvanceTheme.bw;
                    dp = AdvanceTheme.bx;
                    i = AdvanceTheme.bt;
                    int i3 = AdvanceTheme.bp;
                    if (this.currentMessageObject.type == 13) {
                        replyLinePaint.setColor(-1);
                        replyNamePaint.setColor(-1);
                        replyTextPaint.setColor(-1);
                        if (this.currentMessageObject.isOutOwner()) {
                            this.replyStartX = AndroidUtilities.dp(23.0f);
                            if (ThemeUtil.m2490b()) {
                                replyLinePaint.setColor(i2);
                                replyNamePaint.setColor(i2);
                                replyTextPaint.setColor(i);
                            }
                        } else {
                            this.replyStartX = this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(17.0f);
                            if (ThemeUtil.m2490b()) {
                                replyLinePaint.setColor(dp);
                                replyNamePaint.setColor(dp);
                                replyTextPaint.setColor(i3);
                            }
                        }
                        this.replyStartY = this.layoutHeight - AndroidUtilities.dp(58.0f);
                        if (this.nameLayout != null) {
                            this.replyStartY -= AndroidUtilities.dp(31.0f);
                        }
                        i = AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : DOCUMENT_ATTACH_TYPE_NONE) + 14)) + Math.max(this.replyNameWidth, this.replyTextWidth);
                        Theme.systemDrawable.setColorFilter(Theme.colorFilter);
                        Theme.systemDrawable.setBounds(this.replyStartX - AndroidUtilities.dp(7.0f), this.replyStartY - AndroidUtilities.dp(6.0f), i + (this.replyStartX - AndroidUtilities.dp(7.0f)), this.replyStartY + AndroidUtilities.dp(41.0f));
                        Theme.systemDrawable.draw(canvas);
                    } else {
                        if (this.currentMessageObject.isOutOwner()) {
                            replyLinePaint.setColor(Theme.MSG_OUT_WEB_PREVIEW_LINE_COLOR);
                            replyNamePaint.setColor(Theme.MSG_OUT_VIA_BOT_NAME_TEXT_COLOR);
                            if (ThemeUtil.m2490b()) {
                                replyLinePaint.setColor(i2);
                                replyNamePaint.setColor(i2);
                            }
                            if (this.currentMessageObject.replyMessageObject == null || this.currentMessageObject.replyMessageObject.type != 0 || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame)) {
                                replyTextPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_VENUE_INFO_TEXT_COLOR : Theme.MSG_OUT_VENUE_INFO_TEXT_COLOR);
                                if (ThemeUtil.m2490b()) {
                                    replyTextPaint.setColor(i2);
                                }
                            } else {
                                replyTextPaint.setColor(Theme.MSG_TEXT_COLOR);
                                if (ThemeUtil.m2490b()) {
                                    replyTextPaint.setColor(i);
                                }
                            }
                            this.replyStartX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12.0f);
                        } else {
                            replyLinePaint.setColor(Theme.MSG_IN_WEB_PREVIEW_LINE_COLOR);
                            replyNamePaint.setColor(Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
                            if (ThemeUtil.m2490b()) {
                                replyLinePaint.setColor(dp);
                                replyNamePaint.setColor(dp);
                            }
                            if (this.currentMessageObject.replyMessageObject == null || this.currentMessageObject.replyMessageObject.type != 0 || (this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame)) {
                                replyTextPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_VENUE_INFO_SELECTED_TEXT_COLOR : Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
                                if (ThemeUtil.m2490b()) {
                                    replyTextPaint.setColor(dp);
                                }
                            } else {
                                replyTextPaint.setColor(Theme.MSG_TEXT_COLOR);
                                if (ThemeUtil.m2490b()) {
                                    replyTextPaint.setColor(i3);
                                }
                            }
                            if (this.mediaBackground) {
                                this.replyStartX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12.0f);
                            } else {
                                this.replyStartX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(18.0f);
                            }
                        }
                        i = (!this.drawForwardedName || this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] == null) ? DOCUMENT_ATTACH_TYPE_NONE : 36;
                        i2 = i + 12;
                        i = (!this.drawNameLayout || this.nameLayout == null) ? DOCUMENT_ATTACH_TYPE_NONE : 20;
                        this.replyStartY = AndroidUtilities.dp((float) (i + i2));
                    }
                    canvas.drawRect((float) this.replyStartX, (float) this.replyStartY, (float) (this.replyStartX + AndroidUtilities.dp(2.0f)), (float) (this.replyStartY + AndroidUtilities.dp(35.0f)), replyLinePaint);
                    if (this.needReplyImage) {
                        this.replyImageReceiver.setImageCoords(this.replyStartX + AndroidUtilities.dp(10.0f), this.replyStartY, AndroidUtilities.dp(35.0f), AndroidUtilities.dp(35.0f));
                        this.replyImageReceiver.draw(canvas);
                    }
                    if (this.replyNameLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : DOCUMENT_ATTACH_TYPE_NONE) + 10))) + (((float) this.replyStartX) - this.replyNameOffset), (float) this.replyStartY);
                        this.replyNameLayout.draw(canvas);
                        canvas.restore();
                    }
                    if (this.replyTextLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : DOCUMENT_ATTACH_TYPE_NONE) + 10))) + (((float) this.replyStartX) - this.replyTextOffset), (float) (this.replyStartY + AndroidUtilities.dp(19.0f)));
                        this.replyTextLayout.draw(canvas);
                        canvas.restore();
                    }
                }
                if (this.drawTime || !this.mediaBackground) {
                    if (this.mediaBackground) {
                        drawable = this.currentMessageObject.type == 13 ? Theme.timeStickerBackgroundDrawable : Theme.timeBackgroundDrawable;
                        setDrawableBounds(drawable, this.timeX - AndroidUtilities.dp(4.0f), this.layoutHeight - AndroidUtilities.dp(27.0f), this.timeWidth + AndroidUtilities.dp((float) ((this.currentMessageObject.isOutOwner() ? 20 : DOCUMENT_ATTACH_TYPE_NONE) + 8)), AndroidUtilities.dp(17.0f));
                        drawable.draw(canvas);
                        if ((this.currentMessageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                            i = (int) (((float) this.timeWidth) - this.timeLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE));
                            if (this.currentMessageObject.isSending()) {
                                if (!this.currentMessageObject.isOutOwner()) {
                                    setDrawableBounds(Theme.clockMediaDrawable, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(13.0f)) - Theme.clockMediaDrawable.getIntrinsicHeight());
                                    Theme.clockMediaDrawable.draw(canvas);
                                }
                            } else if (!this.currentMessageObject.isSendError()) {
                                drawable = Theme.viewsMediaCountDrawable;
                                setDrawableBounds(drawable, this.timeX, (this.layoutHeight - AndroidUtilities.dp(9.5f)) - this.timeLayout.getHeight());
                                drawable.draw(canvas);
                                if (this.viewsLayout != null) {
                                    canvas.save();
                                    canvas.translate((float) ((drawable.getIntrinsicWidth() + this.timeX) + AndroidUtilities.dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.dp(11.3f)) - this.timeLayout.getHeight()));
                                    this.viewsLayout.draw(canvas);
                                    canvas.restore();
                                }
                            } else if (!this.currentMessageObject.isOutOwner()) {
                                setDrawableBounds(Theme.errorDrawable, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.errorDrawable.getIntrinsicHeight());
                                Theme.errorDrawable.draw(canvas);
                            }
                        } else {
                            i = DOCUMENT_ATTACH_TYPE_NONE;
                        }
                        canvas.save();
                        canvas.translate((float) (i + this.timeX), (float) ((this.layoutHeight - AndroidUtilities.dp(11.3f)) - this.timeLayout.getHeight()));
                        this.timeLayout.draw(canvas);
                        canvas.restore();
                    } else {
                        if ((this.currentMessageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                            i2 = (int) (((float) this.timeWidth) - this.timeLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE));
                            if (this.currentMessageObject.isSending()) {
                                if (!this.currentMessageObject.isOutOwner()) {
                                    Drawable drawable2 = Theme.clockChannelDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE];
                                    setDrawableBounds(drawable2, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(8.5f)) - drawable2.getIntrinsicHeight());
                                    drawable2.draw(canvas);
                                    i = i2;
                                }
                            } else if (!this.currentMessageObject.isSendError()) {
                                if (this.currentMessageObject.isOutOwner()) {
                                    setDrawableBounds(Theme.viewsOutCountDrawable, this.timeX, (this.layoutHeight - AndroidUtilities.dp(4.5f)) - this.timeLayout.getHeight());
                                    Theme.viewsOutCountDrawable.draw(canvas);
                                } else {
                                    setDrawableBounds(Theme.viewsCountDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE], this.timeX, (this.layoutHeight - AndroidUtilities.dp(4.5f)) - this.timeLayout.getHeight());
                                    Theme.viewsCountDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE].draw(canvas);
                                }
                                if (this.viewsLayout != null) {
                                    canvas.save();
                                    canvas.translate((float) ((this.timeX + Theme.viewsOutCountDrawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.dp(6.5f)) - this.timeLayout.getHeight()));
                                    this.viewsLayout.draw(canvas);
                                    canvas.restore();
                                }
                            } else if (!this.currentMessageObject.isOutOwner()) {
                                setDrawableBounds(Theme.errorDrawable, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(6.5f)) - Theme.errorDrawable.getIntrinsicHeight());
                                Theme.errorDrawable.draw(canvas);
                                i = i2;
                            }
                            i = i2;
                        } else {
                            i = DOCUMENT_ATTACH_TYPE_NONE;
                        }
                        canvas.save();
                        canvas.translate((float) (i + this.timeX), (float) ((this.layoutHeight - AndroidUtilities.dp(6.5f)) - this.timeLayout.getHeight()));
                        this.timeLayout.draw(canvas);
                        canvas.restore();
                    }
                    if (this.currentMessageObject.isOutOwner()) {
                        boolean z3;
                        boolean z4;
                        boolean z5;
                        z2 = ((int) (this.currentMessageObject.getDialogId() >> 32)) == DOCUMENT_ATTACH_TYPE_DOCUMENT;
                        if (this.currentMessageObject.isSending()) {
                            z3 = true;
                            z4 = false;
                            z5 = false;
                            z = false;
                        } else if (this.currentMessageObject.isSendError()) {
                            z3 = false;
                            z4 = false;
                            z5 = false;
                        } else if (this.currentMessageObject.isSent()) {
                            z4 = true;
                            z5 = !this.currentMessageObject.isUnread();
                            z = false;
                            z3 = false;
                        } else {
                            z = false;
                            z3 = false;
                            z4 = false;
                            z5 = false;
                        }
                        if (z3) {
                            if (this.mediaBackground) {
                                setDrawableBounds(Theme.clockMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(22.0f)) - Theme.clockMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.clockMediaDrawable.getIntrinsicHeight());
                                Theme.clockMediaDrawable.draw(canvas);
                            } else {
                                setDrawableBounds(Theme.clockDrawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - Theme.clockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.5f)) - Theme.clockDrawable.getIntrinsicHeight());
                                Theme.clockDrawable.draw(canvas);
                            }
                        }
                        if (!z2) {
                            if (z4) {
                                if (this.mediaBackground) {
                                    if (z5) {
                                        setDrawableBounds(Theme.checkMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.checkMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.checkMediaDrawable.getIntrinsicHeight());
                                    } else {
                                        setDrawableBounds(Theme.checkMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.checkMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.checkMediaDrawable.getIntrinsicHeight());
                                    }
                                    Theme.checkMediaDrawable.draw(canvas);
                                } else {
                                    if (z5) {
                                        setDrawableBounds(Theme.checkDrawable, (this.layoutWidth - AndroidUtilities.dp(22.5f)) - Theme.checkDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.checkDrawable.getIntrinsicHeight());
                                    } else {
                                        setDrawableBounds(Theme.checkDrawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - Theme.checkDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.checkDrawable.getIntrinsicHeight());
                                    }
                                    Theme.checkDrawable.draw(canvas);
                                }
                            }
                            if (z5) {
                                if (this.mediaBackground) {
                                    setDrawableBounds(Theme.halfCheckMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.halfCheckMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.halfCheckMediaDrawable.getIntrinsicHeight());
                                    Theme.halfCheckMediaDrawable.draw(canvas);
                                } else {
                                    setDrawableBounds(Theme.halfCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(18.0f)) - Theme.halfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.halfCheckDrawable.getIntrinsicHeight());
                                    Theme.halfCheckDrawable.draw(canvas);
                                }
                            }
                        } else if (z5 || z4) {
                            if (this.mediaBackground) {
                                setDrawableBounds(Theme.broadcastMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(24.0f)) - Theme.broadcastMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.0f)) - Theme.broadcastMediaDrawable.getIntrinsicHeight());
                                Theme.broadcastMediaDrawable.draw(canvas);
                            } else {
                                setDrawableBounds(Theme.broadcastDrawable, (this.layoutWidth - AndroidUtilities.dp(20.5f)) - Theme.broadcastDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.broadcastDrawable.getIntrinsicHeight());
                                Theme.broadcastDrawable.draw(canvas);
                            }
                        }
                        if (z) {
                            if (this.mediaBackground) {
                                setDrawableBounds(Theme.errorDrawable, (this.layoutWidth - AndroidUtilities.dp(20.5f)) - Theme.errorDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(11.5f)) - Theme.errorDrawable.getIntrinsicHeight());
                                Theme.errorDrawable.draw(canvas);
                            } else {
                                setDrawableBounds(Theme.errorDrawable, (this.layoutWidth - AndroidUtilities.dp(18.0f)) - Theme.errorDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(7.0f)) - Theme.errorDrawable.getIntrinsicHeight());
                                Theme.errorDrawable.draw(canvas);
                            }
                        }
                    }
                }
                if (this.drawMarker) {
                    setDrawableBounds(Theme.markerDrawable, this.currentMessageObject.isOutOwner() ? this.currentBackgroundDrawable.getBounds().left + (Theme.markerDrawable.getIntrinsicWidth() / DOCUMENT_ATTACH_TYPE_GIF) : this.currentBackgroundDrawable.getBounds().right - (Theme.markerDrawable.getIntrinsicWidth() + (Theme.markerDrawable.getIntrinsicWidth() / DOCUMENT_ATTACH_TYPE_GIF)), (int) DOCUMENT_ATTACH_TYPE_NONE);
                    Theme.markerDrawable.draw(canvas);
                    return;
                }
                return;
            }
            requestLayout();
        }
    }

    public void onFailedDownload(String str) {
        boolean z = this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC;
        updateButtonState(z);
    }

    @SuppressLint({"DrawAllocation"})
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.currentMessageObject == null) {
            super.onLayout(z, i, i2, i3, i4);
            return;
        }
        int dp;
        if (z || !this.wasLayout) {
            this.layoutWidth = getMeasuredWidth();
            this.layoutHeight = getMeasuredHeight() - this.substractBackgroundHeight;
            if (this.timeTextWidth < 0) {
                this.timeTextWidth = AndroidUtilities.dp(10.0f);
            }
            this.timeLayout = new StaticLayout(this.currentTimeString, timePaint, this.timeTextWidth + AndroidUtilities.dp(6.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            int dp2;
            if (this.mediaBackground) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(42.0f);
                } else {
                    dp2 = (this.backgroundWidth - AndroidUtilities.dp(4.0f)) - this.timeWidth;
                    dp = (this.isChat && this.currentMessageObject.isFromUser()) ? AndroidUtilities.dp(48.0f) : DOCUMENT_ATTACH_TYPE_NONE;
                    this.timeX = dp + dp2;
                }
            } else if (this.currentMessageObject.isOutOwner()) {
                this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(38.5f);
            } else {
                dp2 = (this.backgroundWidth - AndroidUtilities.dp(9.0f)) - this.timeWidth;
                dp = (this.isChat && this.currentMessageObject.isFromUser()) ? AndroidUtilities.dp(48.0f) : DOCUMENT_ATTACH_TYPE_NONE;
                this.timeX = dp + dp2;
            }
            if ((this.currentMessageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0) {
                this.viewsLayout = new StaticLayout(this.currentViewsString, timePaint, this.viewsTextWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
            } else {
                this.viewsLayout = null;
            }
            if (this.isAvatarVisible) {
                this.avatarImage.setImageCoords(AndroidUtilities.dp(6.0f), this.layoutHeight - AndroidUtilities.dp(44.0f), AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            }
            this.wasLayout = true;
        }
        if (this.currentMessageObject.type == 0) {
            this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
        }
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
            if (this.currentMessageObject.isOutOwner()) {
                this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(57.0f);
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
            } else if (this.isChat && this.currentMessageObject.isFromUser()) {
                this.seekBarX = AndroidUtilities.dp(114.0f);
                this.buttonX = AndroidUtilities.dp(71.0f);
                this.timeAudioX = AndroidUtilities.dp(124.0f);
            } else {
                this.seekBarX = AndroidUtilities.dp(66.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            this.seekBarWaveform.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : DOCUMENT_ATTACH_TYPE_NONE) + 92)), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
            this.seekBar.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : DOCUMENT_ATTACH_TYPE_NONE) + 72)), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
            this.seekBarY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
            updateAudioProgress();
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (this.currentMessageObject.isOutOwner()) {
                this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(56.0f);
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
            } else if (this.isChat && this.currentMessageObject.isFromUser()) {
                this.seekBarX = AndroidUtilities.dp(113.0f);
                this.buttonX = AndroidUtilities.dp(71.0f);
                this.timeAudioX = AndroidUtilities.dp(124.0f);
            } else {
                this.seekBarX = AndroidUtilities.dp(65.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            this.seekBar.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : DOCUMENT_ATTACH_TYPE_NONE) + 65)), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
            this.seekBarY = (AndroidUtilities.dp(29.0f) + this.namesOffset) + this.mediaOffsetY;
            this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
            updateAudioProgress();
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT && !this.drawPhotoImage) {
            if (this.currentMessageObject.isOutOwner()) {
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
            } else if (this.isChat && this.currentMessageObject.isFromUser()) {
                this.buttonX = AndroidUtilities.dp(71.0f);
            } else {
                this.buttonX = AndroidUtilities.dp(23.0f);
            }
            if (this.hasLinkPreview) {
                this.buttonX += AndroidUtilities.dp(10.0f);
            }
            this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
            this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.dp(10.0f), this.buttonY - AndroidUtilities.dp(10.0f), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        } else if (this.currentMessageObject.type == 12) {
            dp = this.currentMessageObject.isOutOwner() ? (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f) : (this.isChat && this.currentMessageObject.isFromUser()) ? AndroidUtilities.dp(72.0f) : AndroidUtilities.dp(23.0f);
            this.photoImage.setImageCoords(dp, AndroidUtilities.dp(13.0f) + this.namesOffset, AndroidUtilities.dp(44.0f), AndroidUtilities.dp(44.0f));
        } else {
            dp = this.currentMessageObject.isOutOwner() ? this.mediaBackground ? (this.layoutWidth - this.backgroundWidth) - AndroidUtilities.dp(3.0f) : (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(6.0f) : (this.isChat && this.currentMessageObject.isFromUser()) ? AndroidUtilities.dp(63.0f) : AndroidUtilities.dp(15.0f);
            this.photoImage.setImageCoords(dp, this.photoImage.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
            this.buttonX = (int) (((float) dp) + (((float) (this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f))) / 2.0f));
            this.buttonY = ((int) (((float) AndroidUtilities.dp(7.0f)) + (((float) (this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f))) / 2.0f))) + this.namesOffset;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0f), this.buttonY + AndroidUtilities.dp(48.0f));
            this.deleteProgressRect.set((float) (this.buttonX + AndroidUtilities.dp(3.0f)), (float) (this.buttonY + AndroidUtilities.dp(3.0f)), (float) (this.buttonX + AndroidUtilities.dp(45.0f)), (float) (this.buttonY + AndroidUtilities.dp(45.0f)));
        }
    }

    protected void onLongPress() {
        if (this.pressedLink instanceof URLSpanNoUnderline) {
            if (((URLSpanNoUnderline) this.pressedLink).getURL().startsWith("/")) {
                this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
                return;
            }
        } else if (this.pressedLink instanceof URLSpan) {
            this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
            return;
        }
        resetPressedLink(-1);
        if (!(this.buttonPressed == 0 && this.pressedBotButton == -1)) {
            this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
            this.pressedBotButton = -1;
            invalidate();
        }
        if (!(this.delegate == null || this.avatarPressed)) {
            this.delegate.didLongPressed(this);
        }
        if (this.delegate != null && this.avatarPressed) {
            this.delegate.didLongPressedUserAvatar(this, this.currentUser);
        }
    }

    protected void onMeasure(int i, int i2) {
        if (this.currentMessageObject != null && this.currentMessageObject.checkLayout()) {
            this.inLayout = true;
            MessageObject messageObject = this.currentMessageObject;
            this.currentMessageObject = null;
            setMessageObject(messageObject);
            this.inLayout = false;
        }
        this.extraHeight = DOCUMENT_ATTACH_TYPE_NONE;
        if (!this.mediaBackground && (this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC || this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT || MessageObject.isVoiceDocument(this.documentAttach))) {
            this.extraHeight = 20;
        }
        setMeasuredDimension(MeasureSpec.getSize(i), (this.totalHeight + this.keyboardHeight) + this.extraHeight);
    }

    public void onProgressDownload(String str, float f) {
        this.radialProgress.setProgress(f, true);
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (this.buttonState != DOCUMENT_ATTACH_TYPE_VIDEO) {
                updateButtonState(false);
            }
        } else if (this.buttonState != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            updateButtonState(false);
        }
    }

    public void onProgressUpload(String str, float f, boolean z) {
        this.radialProgress.setProgress(f, true);
    }

    public void onProvideStructure(ViewStructure viewStructure) {
        super.onProvideStructure(viewStructure);
        if (this.allowAssistant && VERSION.SDK_INT >= 23) {
            if (this.currentMessageObject.messageText != null && this.currentMessageObject.messageText.length() > 0) {
                viewStructure.setText(this.currentMessageObject.messageText);
            } else if (this.currentMessageObject.caption != null && this.currentMessageObject.caption.length() > 0) {
                viewStructure.setText(this.currentMessageObject.caption);
            }
        }
    }

    public void onSeekBarDrag(float f) {
        if (this.currentMessageObject != null) {
            this.currentMessageObject.audioProgress = f;
            MediaController.m71a().m159a(this.currentMessageObject, f);
        }
    }

    public void onSuccessDownload(String str) {
        if (this.radialProgress != null) {
            this.radialProgress.setSizeAndType(0, DOCUMENT_ATTACH_TYPE_NONE);
        }
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            updateButtonState(true);
            updateWaveform();
            return;
        }
        this.radialProgress.setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, true);
        if (this.currentMessageObject.type != 0) {
            if (!this.photoNotSet || (this.currentMessageObject.type == 8 && this.currentMessageObject.audioProgress != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) {
                if (this.currentMessageObject.type != 8 || this.currentMessageObject.audioProgress == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    updateButtonState(true);
                } else {
                    this.photoNotSet = false;
                    this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                    if (MoboConstants.af && MediaController.m71a().m140B()) {
                        this.photoImage.setAllowStartAnimation(true);
                        this.photoImage.startAnimation();
                        this.currentMessageObject.audioProgress = 0.0f;
                        this.buttonState = -1;
                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, true);
                    } else if (MoboConstants.af) {
                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, true);
                    } else {
                        didPressedButton(true);
                    }
                }
            }
            if (this.photoNotSet) {
                setMessageObject(this.currentMessageObject);
            }
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF && this.currentMessageObject.audioProgress != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
            didPressedButton(true);
        } else if (this.photoNotSet) {
            setMessageObject(this.currentMessageObject);
        } else {
            updateButtonState(true);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject == null || !this.delegate.canPerformActions()) {
            return super.onTouchEvent(motionEvent);
        }
        this.disallowLongPress = false;
        boolean checkTextBlockMotionEvent = checkTextBlockMotionEvent(motionEvent);
        if (!checkTextBlockMotionEvent) {
            checkTextBlockMotionEvent = checkOtherButtonMotionEvent(motionEvent);
        }
        if (!checkTextBlockMotionEvent) {
            checkTextBlockMotionEvent = checkLinkPreviewMotionEvent(motionEvent);
        }
        if (!checkTextBlockMotionEvent) {
            checkTextBlockMotionEvent = checkGameMotionEvent(motionEvent);
        }
        if (!checkTextBlockMotionEvent) {
            checkTextBlockMotionEvent = checkCaptionMotionEvent(motionEvent);
        }
        if (!checkTextBlockMotionEvent) {
            checkTextBlockMotionEvent = checkAudioMotionEvent(motionEvent);
        }
        if (!checkTextBlockMotionEvent) {
            checkTextBlockMotionEvent = checkPhotoImageMotionEvent(motionEvent);
        }
        if (!checkTextBlockMotionEvent) {
            checkTextBlockMotionEvent = checkBotButtonMotionEvent(motionEvent);
        }
        if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
            this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
            this.pressedBotButton = -1;
            this.linkPreviewPressed = false;
            this.otherPressed = false;
            this.imagePressed = false;
            resetPressedLink(-1);
            checkTextBlockMotionEvent = false;
        }
        if (!this.disallowLongPress && checkTextBlockMotionEvent && motionEvent.getAction() == 0) {
            startCheckLongPress();
        }
        if (!(motionEvent.getAction() == 0 || motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_GIF)) {
            cancelCheckLongPress();
        }
        if (checkTextBlockMotionEvent) {
            return checkTextBlockMotionEvent;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (motionEvent.getAction() != 0) {
            if (motionEvent.getAction() != DOCUMENT_ATTACH_TYPE_GIF) {
                cancelCheckLongPress();
            }
            if (this.avatarPressed) {
                if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.avatarPressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate == null) {
                        return checkTextBlockMotionEvent;
                    }
                    if (this.currentUser != null) {
                        this.delegate.didPressedUserAvatar(this, this.currentUser);
                        return checkTextBlockMotionEvent;
                    } else if (this.currentChat == null) {
                        return checkTextBlockMotionEvent;
                    } else {
                        this.delegate.didPressedChannelAvatar(this, this.currentChat, DOCUMENT_ATTACH_TYPE_NONE);
                        return checkTextBlockMotionEvent;
                    }
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.avatarPressed = false;
                    return checkTextBlockMotionEvent;
                } else if (motionEvent.getAction() != DOCUMENT_ATTACH_TYPE_GIF || !this.isAvatarVisible || this.avatarImage.isInsideImage(x, y)) {
                    return checkTextBlockMotionEvent;
                } else {
                    this.avatarPressed = false;
                    return checkTextBlockMotionEvent;
                }
            } else if (this.forwardNamePressed) {
                if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.forwardNamePressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate == null) {
                        return checkTextBlockMotionEvent;
                    }
                    if (this.currentForwardChannel != null) {
                        this.delegate.didPressedChannelAvatar(this, this.currentForwardChannel, this.currentMessageObject.messageOwner.fwd_from.channel_post);
                        return checkTextBlockMotionEvent;
                    } else if (this.currentForwardUser == null) {
                        return checkTextBlockMotionEvent;
                    } else {
                        this.delegate.didPressedUserAvatar(this, this.currentForwardUser);
                        return checkTextBlockMotionEvent;
                    }
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.forwardNamePressed = false;
                    return checkTextBlockMotionEvent;
                } else if (motionEvent.getAction() != DOCUMENT_ATTACH_TYPE_GIF) {
                    return checkTextBlockMotionEvent;
                } else {
                    if (x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.dp(32.0f)))) {
                        return checkTextBlockMotionEvent;
                    }
                    this.forwardNamePressed = false;
                    return checkTextBlockMotionEvent;
                }
            } else if (this.forwardBotPressed) {
                if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.forwardBotPressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate == null) {
                        return checkTextBlockMotionEvent;
                    }
                    this.delegate.didPressedViaBot(this, this.currentViaBotUser != null ? this.currentViaBotUser.username : this.currentMessageObject.messageOwner.via_bot_name);
                    return checkTextBlockMotionEvent;
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.forwardBotPressed = false;
                    return checkTextBlockMotionEvent;
                } else if (motionEvent.getAction() != DOCUMENT_ATTACH_TYPE_GIF) {
                    return checkTextBlockMotionEvent;
                } else {
                    if (!this.drawForwardedName || this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] == null) {
                        if (x >= this.nameX + ((float) this.viaNameWidth) && x <= (this.nameX + ((float) this.viaNameWidth)) + ((float) this.viaWidth) && y >= this.nameY - ((float) AndroidUtilities.dp(4.0f)) && y <= this.nameY + ((float) AndroidUtilities.dp(20.0f))) {
                            return checkTextBlockMotionEvent;
                        }
                        this.forwardBotPressed = false;
                        return checkTextBlockMotionEvent;
                    } else if (x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.dp(32.0f)))) {
                        return checkTextBlockMotionEvent;
                    } else {
                        this.forwardBotPressed = false;
                        return checkTextBlockMotionEvent;
                    }
                }
            } else if (this.replyPressed) {
                if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.replyPressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate == null) {
                        return checkTextBlockMotionEvent;
                    }
                    this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
                    return checkTextBlockMotionEvent;
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.replyPressed = false;
                    return checkTextBlockMotionEvent;
                } else if (motionEvent.getAction() != DOCUMENT_ATTACH_TYPE_GIF) {
                    return checkTextBlockMotionEvent;
                } else {
                    if (x >= ((float) this.replyStartX) && x <= ((float) (this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth))) && y >= ((float) this.replyStartY) && y <= ((float) (this.replyStartY + AndroidUtilities.dp(35.0f)))) {
                        return checkTextBlockMotionEvent;
                    }
                    this.replyPressed = false;
                    return checkTextBlockMotionEvent;
                }
            } else if (this.sharePressed) {
                if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.sharePressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate != null) {
                        this.delegate.didPressedShare(this);
                    }
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.sharePressed = false;
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_GIF && (x < ((float) this.shareStartX) || x > ((float) (this.shareStartX + AndroidUtilities.dp(40.0f))) || y < ((float) this.shareStartY) || y > ((float) (this.shareStartY + AndroidUtilities.dp(32.0f))))) {
                    this.sharePressed = false;
                }
                invalidate();
                return checkTextBlockMotionEvent;
            } else if (this.favoritePressed) {
                if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.favoritePressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate != null) {
                        this.delegate.didPressedFavorite(this);
                    }
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.favoritePressed = false;
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_GIF && (x < ((float) this.favoriteStartX) || x > ((float) (this.favoriteStartX + AndroidUtilities.dp(40.0f))) || y < ((float) this.favoriteStartY) || y > ((float) (this.favoriteStartY + AndroidUtilities.dp(32.0f))))) {
                    this.favoritePressed = false;
                }
                invalidate();
                return checkTextBlockMotionEvent;
            } else if (!this.menuPressed) {
                return checkTextBlockMotionEvent;
            } else {
                if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.menuPressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate != null) {
                        this.delegate.didPressedMenu(this);
                    }
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.menuPressed = false;
                } else if (motionEvent.getAction() == DOCUMENT_ATTACH_TYPE_GIF && (x < ((float) this.menuStartX) || x > ((float) (this.menuStartX + AndroidUtilities.dp(40.0f))) || y < ((float) this.menuStartY) || y > ((float) (this.menuStartY + AndroidUtilities.dp(32.0f))))) {
                    this.menuPressed = false;
                }
                invalidate();
                return checkTextBlockMotionEvent;
            }
        } else if (this.delegate != null && !this.delegate.canPerformActions()) {
            return checkTextBlockMotionEvent;
        } else {
            if (this.isAvatarVisible && this.avatarImage.isInsideImage(x, y)) {
                this.avatarPressed = true;
                checkTextBlockMotionEvent = true;
            } else if (this.drawForwardedName && this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] != null && x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.dp(32.0f)))) {
                if (this.viaWidth == 0 || x < ((float) ((this.forwardNameX + this.viaNameWidth) + AndroidUtilities.dp(4.0f)))) {
                    this.forwardNamePressed = true;
                } else {
                    this.forwardBotPressed = true;
                }
                checkTextBlockMotionEvent = true;
            } else if (this.drawNameLayout && this.nameLayout != null && this.viaWidth != 0 && x >= this.nameX + ((float) this.viaNameWidth) && x <= (this.nameX + ((float) this.viaNameWidth)) + ((float) this.viaWidth) && y >= this.nameY - ((float) AndroidUtilities.dp(4.0f)) && y <= this.nameY + ((float) AndroidUtilities.dp(20.0f))) {
                this.forwardBotPressed = true;
                checkTextBlockMotionEvent = true;
            } else if (this.currentMessageObject.isReply() && x >= ((float) this.replyStartX) && x <= ((float) (this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth))) && y >= ((float) this.replyStartY) && y <= ((float) (this.replyStartY + AndroidUtilities.dp(35.0f)))) {
                this.replyPressed = true;
                checkTextBlockMotionEvent = true;
            } else if (this.drawShareButton && x >= ((float) this.shareStartX) && x <= ((float) (this.shareStartX + AndroidUtilities.dp(40.0f))) && y >= ((float) this.shareStartY) && y <= ((float) (this.shareStartY + AndroidUtilities.dp(32.0f)))) {
                this.sharePressed = true;
                invalidate();
                checkTextBlockMotionEvent = true;
            } else if (this.drawFavoriteButton && x >= ((float) this.favoriteStartX) && x <= ((float) (this.favoriteStartX + AndroidUtilities.dp(40.0f))) && y >= ((float) this.favoriteStartY) && y <= ((float) (this.favoriteStartY + AndroidUtilities.dp(32.0f)))) {
                this.favoritePressed = true;
                invalidate();
                checkTextBlockMotionEvent = true;
            } else if (this.drawMenuButton && x >= ((float) this.menuStartX) && x <= ((float) (this.menuStartX + AndroidUtilities.dp(40.0f))) && y >= ((float) this.menuStartY) && y <= ((float) (this.menuStartY + AndroidUtilities.dp(32.0f)))) {
                this.menuPressed = true;
                invalidate();
                checkTextBlockMotionEvent = true;
            }
            if (!checkTextBlockMotionEvent) {
                return checkTextBlockMotionEvent;
            }
            startCheckLongPress();
            return checkTextBlockMotionEvent;
        }
    }

    public void requestLayout() {
        if (!this.inLayout) {
            super.requestLayout();
        }
    }

    public void setAllowAssistant(boolean z) {
        this.allowAssistant = z;
    }

    public void setCheckPressed(boolean z, boolean z2) {
        this.isCheckPressed = z;
        this.isPressed = z2;
        this.radialProgress.swapBackground(getDrawableForCurrentState());
        if (this.useSeekBarWaweform) {
            this.seekBarWaveform.setSelected(isDrawSelectedBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectedBackground());
        }
        invalidate();
    }

    public void setDelegate(ChatMessageCellDelegate chatMessageCellDelegate) {
        this.delegate = chatMessageCellDelegate;
    }

    public void setDialogSettings(DialogSettings dialogSettings) {
        this.dialogSettings = dialogSettings;
    }

    public void setHighlighted(boolean z) {
        if (this.isHighlighted != z) {
            this.isHighlighted = z;
            this.radialProgress.swapBackground(getDrawableForCurrentState());
            if (this.useSeekBarWaweform) {
                this.seekBarWaveform.setSelected(isDrawSelectedBackground());
            } else {
                this.seekBar.setSelected(isDrawSelectedBackground());
            }
            invalidate();
        }
    }

    public void setHighlightedText(String str) {
        if (this.currentMessageObject.messageOwner.message != null && this.currentMessageObject != null && this.currentMessageObject.type == 0 && !TextUtils.isEmpty(this.currentMessageObject.messageText) && str != null) {
            int indexOf = TextUtils.indexOf(this.currentMessageObject.messageOwner.message.toLowerCase(), str.toLowerCase());
            if (indexOf != -1) {
                int length = indexOf + str.length();
                int i = DOCUMENT_ATTACH_TYPE_NONE;
                while (i < this.currentMessageObject.textLayoutBlocks.size()) {
                    TextLayoutBlock textLayoutBlock = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i);
                    if (indexOf < textLayoutBlock.charactersOffset || indexOf >= textLayoutBlock.charactersOffset + textLayoutBlock.textLayout.getText().length()) {
                        i += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                    } else {
                        this.linkSelectionBlockNum = i;
                        resetUrlPaths(true);
                        try {
                            Path obtainNewUrlPath = obtainNewUrlPath(true);
                            int length2 = textLayoutBlock.textLayout.getText().length();
                            obtainNewUrlPath.setCurrentLayout(textLayoutBlock.textLayout, indexOf, 0.0f);
                            textLayoutBlock.textLayout.getSelectionPath(indexOf, length - textLayoutBlock.charactersOffset, obtainNewUrlPath);
                            if (length >= textLayoutBlock.charactersOffset + length2) {
                                for (indexOf = i + DOCUMENT_ATTACH_TYPE_DOCUMENT; indexOf < this.currentMessageObject.textLayoutBlocks.size(); indexOf += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                    TextLayoutBlock textLayoutBlock2 = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(indexOf);
                                    int length3 = textLayoutBlock2.textLayout.getText().length();
                                    Path obtainNewUrlPath2 = obtainNewUrlPath(true);
                                    obtainNewUrlPath2.setCurrentLayout(textLayoutBlock2.textLayout, DOCUMENT_ATTACH_TYPE_NONE, (float) textLayoutBlock2.height);
                                    textLayoutBlock2.textLayout.getSelectionPath(DOCUMENT_ATTACH_TYPE_NONE, length - textLayoutBlock2.charactersOffset, obtainNewUrlPath2);
                                    if (length < (textLayoutBlock.charactersOffset + length3) - 1) {
                                        break;
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                        invalidate();
                        return;
                    }
                }
            } else if (!this.urlPathSelection.isEmpty()) {
                this.linkSelectionBlockNum = -1;
                resetUrlPaths(true);
                invalidate();
            }
        } else if (!this.urlPathSelection.isEmpty()) {
            this.linkSelectionBlockNum = -1;
            resetUrlPaths(true);
            invalidate();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setMessageObject(com.hanista.mobogram.messenger.MessageObject r34) {
        /*
        r33 = this;
        r0 = r33;
        r2 = r0.radialProgress;
        if (r2 == 0) goto L_0x000e;
    L_0x0006:
        r0 = r33;
        r2 = r0.radialProgress;
        r3 = 0;
        r2.setShowSize(r3);
    L_0x000e:
        r0 = r33;
        r2 = r0.dialogSettings;
        if (r2 == 0) goto L_0x057c;
    L_0x0014:
        r0 = r33;
        r2 = r0.dialogSettings;
        r2 = r2.m984g();
        r3 = r34.getId();
        if (r2 != r3) goto L_0x057c;
    L_0x0022:
        r2 = 1;
    L_0x0023:
        r0 = r33;
        r0.drawMarker = r2;
        r2 = r34.checkLayout();
        if (r2 == 0) goto L_0x0032;
    L_0x002d:
        r2 = 0;
        r0 = r33;
        r0.currentMessageObject = r2;
    L_0x0032:
        r0 = r33;
        r2 = r0.currentMessageObject;
        if (r2 == 0) goto L_0x0046;
    L_0x0038:
        r0 = r33;
        r2 = r0.currentMessageObject;
        r2 = r2.getId();
        r3 = r34.getId();
        if (r2 == r3) goto L_0x057f;
    L_0x0046:
        r2 = 1;
        r14 = r2;
    L_0x0048:
        r0 = r33;
        r2 = r0.currentMessageObject;
        r0 = r34;
        if (r2 != r0) goto L_0x0056;
    L_0x0050:
        r0 = r34;
        r2 = r0.forceUpdate;
        if (r2 == 0) goto L_0x0583;
    L_0x0056:
        r2 = 1;
    L_0x0057:
        r0 = r33;
        r3 = r0.currentMessageObject;
        r0 = r34;
        if (r3 != r0) goto L_0x0586;
    L_0x005f:
        r3 = r33.isUserDataChanged();
        if (r3 != 0) goto L_0x006b;
    L_0x0065:
        r0 = r33;
        r3 = r0.photoNotSet;
        if (r3 == 0) goto L_0x0586;
    L_0x006b:
        r3 = 1;
        r30 = r3;
    L_0x006e:
        if (r2 != 0) goto L_0x0078;
    L_0x0070:
        if (r30 != 0) goto L_0x0078;
    L_0x0072:
        r3 = r33.isPhotoDataChanged(r34);
        if (r3 == 0) goto L_0x23ac;
    L_0x0078:
        r0 = r34;
        r1 = r33;
        r1.currentMessageObject = r0;
        r0 = r34;
        r3 = r0.messageOwner;
        r3 = r3.send_state;
        r0 = r33;
        r0.lastSendState = r3;
        r0 = r34;
        r3 = r0.messageOwner;
        r3 = r3.destroyTime;
        r0 = r33;
        r0.lastDeleteDate = r3;
        r0 = r34;
        r3 = r0.messageOwner;
        r3 = r3.views;
        r0 = r33;
        r0.lastViewsCount = r3;
        r3 = 0;
        r0 = r33;
        r0.isPressed = r3;
        r3 = 1;
        r0 = r33;
        r0.isCheckPressed = r3;
        r3 = 0;
        r0 = r33;
        r0.isAvatarVisible = r3;
        r3 = 0;
        r0 = r33;
        r0.wasLayout = r3;
        r3 = r33.checkNeedDrawShareButton(r34);
        r0 = r33;
        r0.drawShareButton = r3;
        r3 = r33.checkNeedDrawFavoriteButton(r34);
        r0 = r33;
        r0.drawFavoriteButton = r3;
        r0 = r33;
        r3 = r0.drawShareButton;
        if (r3 == 0) goto L_0x058b;
    L_0x00c6:
        r0 = r33;
        r3 = r0.drawFavoriteButton;
        if (r3 == 0) goto L_0x058b;
    L_0x00cc:
        r3 = 1;
    L_0x00cd:
        r0 = r33;
        r0.drawMenuButton = r3;
        r3 = 0;
        r0 = r33;
        r0.replyNameLayout = r3;
        r3 = 0;
        r0 = r33;
        r0.replyTextLayout = r3;
        r3 = 0;
        r0 = r33;
        r0.replyNameWidth = r3;
        r3 = 0;
        r0 = r33;
        r0.replyTextWidth = r3;
        r3 = 0;
        r0 = r33;
        r0.viaWidth = r3;
        r3 = 0;
        r0 = r33;
        r0.viaNameWidth = r3;
        r3 = 0;
        r0 = r33;
        r0.currentReplyPhoto = r3;
        r3 = 0;
        r0 = r33;
        r0.currentUser = r3;
        r3 = 0;
        r0 = r33;
        r0.currentChat = r3;
        r3 = 0;
        r0 = r33;
        r0.currentViaBotUser = r3;
        r3 = 0;
        r0 = r33;
        r0.drawNameLayout = r3;
        r3 = 0;
        r0 = r33;
        r0.drawStatus = r3;
        r3 = -1;
        r0 = r33;
        r0.resetPressedLink(r3);
        r3 = 0;
        r0 = r34;
        r0.forceUpdate = r3;
        r3 = 0;
        r0 = r33;
        r0.drawPhotoImage = r3;
        r3 = 0;
        r0 = r33;
        r0.hasLinkPreview = r3;
        r3 = 0;
        r0 = r33;
        r0.hasGamePreview = r3;
        r3 = 0;
        r0 = r33;
        r0.linkPreviewPressed = r3;
        r3 = 0;
        r0 = r33;
        r0.buttonPressed = r3;
        r3 = -1;
        r0 = r33;
        r0.pressedBotButton = r3;
        r3 = 0;
        r0 = r33;
        r0.linkPreviewHeight = r3;
        r3 = 0;
        r0 = r33;
        r0.mediaOffsetY = r3;
        r3 = 0;
        r0 = r33;
        r0.documentAttachType = r3;
        r3 = 0;
        r0 = r33;
        r0.documentAttach = r3;
        r3 = 0;
        r0 = r33;
        r0.descriptionLayout = r3;
        r3 = 0;
        r0 = r33;
        r0.titleLayout = r3;
        r3 = 0;
        r0 = r33;
        r0.videoInfoLayout = r3;
        r3 = 0;
        r0 = r33;
        r0.siteNameLayout = r3;
        r3 = 0;
        r0 = r33;
        r0.authorLayout = r3;
        r3 = 0;
        r0 = r33;
        r0.captionLayout = r3;
        r3 = 0;
        r0 = r33;
        r0.docTitleLayout = r3;
        r3 = 0;
        r0 = r33;
        r0.drawImageButton = r3;
        r3 = 0;
        r0 = r33;
        r0.currentPhotoObject = r3;
        r3 = 0;
        r0 = r33;
        r0.currentPhotoObjectThumb = r3;
        r3 = 0;
        r0 = r33;
        r0.currentPhotoFilter = r3;
        r3 = 0;
        r0 = r33;
        r0.infoLayout = r3;
        r3 = 0;
        r0 = r33;
        r0.cancelLoading = r3;
        r3 = -1;
        r0 = r33;
        r0.buttonState = r3;
        r3 = 0;
        r0 = r33;
        r0.currentUrl = r3;
        r3 = 0;
        r0 = r33;
        r0.photoNotSet = r3;
        r3 = 1;
        r0 = r33;
        r0.drawBackground = r3;
        r3 = 0;
        r0 = r33;
        r0.drawName = r3;
        r3 = 0;
        r0 = r33;
        r0.useSeekBarWaweform = r3;
        r3 = 0;
        r0 = r33;
        r0.drawForwardedName = r3;
        r3 = 0;
        r0 = r33;
        r0.mediaBackground = r3;
        r3 = 0;
        r0 = r33;
        r0.availableTimeWidth = r3;
        r0 = r33;
        r3 = r0.photoImage;
        r4 = 0;
        r3.setNeedsQualityThumb(r4);
        r0 = r33;
        r3 = r0.photoImage;
        r4 = 0;
        r3.setShouldGenerateQualityThumb(r4);
        r0 = r33;
        r3 = r0.photoImage;
        r4 = 0;
        r3.setParentMessageObject(r4);
        r0 = r33;
        r3 = r0.photoImage;
        r4 = 1077936128; // 0x40400000 float:3.0 double:5.325712093E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r3.setRoundRadius(r4);
        if (r2 == 0) goto L_0x01ee;
    L_0x01df:
        r2 = 0;
        r0 = r33;
        r0.firstVisibleBlockNum = r2;
        r2 = 0;
        r0 = r33;
        r0.lastVisibleBlockNum = r2;
        r2 = 1;
        r0 = r33;
        r0.needNewVisiblePart = r2;
    L_0x01ee:
        r0 = r34;
        r2 = r0.type;
        if (r2 != 0) goto L_0x13b5;
    L_0x01f4:
        r2 = 1;
        r0 = r33;
        r0.drawForwardedName = r2;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x05b3;
    L_0x01ff:
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x058e;
    L_0x0205:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x058e;
    L_0x020b:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x058e;
    L_0x0211:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r3 = 1123287040; // 0x42f40000 float:122.0 double:5.54977537E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = 1;
        r0 = r33;
        r0.drawName = r3;
        r15 = r2;
    L_0x0222:
        r0 = r33;
        r0.availableTimeWidth = r15;
        r33.measureTime(r34);
        r0 = r33;
        r2 = r0.timeWidth;
        r3 = 1086324736; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r3 = r34.isOutOwner();
        if (r3 == 0) goto L_0x2455;
    L_0x023a:
        r3 = 1101266944; // 0x41a40000 float:20.5 double:5.44098164E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r16 = r2;
    L_0x0243:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGame;
        if (r2 == 0) goto L_0x060e;
    L_0x024d:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.game;
        r2 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_game;
        if (r2 == 0) goto L_0x060e;
    L_0x0259:
        r2 = 1;
    L_0x025a:
        r0 = r33;
        r0.hasGamePreview = r2;
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r2 == 0) goto L_0x0611;
    L_0x0268:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.webpage;
        r2 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_webPage;
        if (r2 == 0) goto L_0x0611;
    L_0x0274:
        r2 = 1;
    L_0x0275:
        r0 = r33;
        r0.hasLinkPreview = r2;
        r0 = r33;
        r0.backgroundWidth = r15;
        r0 = r33;
        r2 = r0.hasLinkPreview;
        if (r2 != 0) goto L_0x0293;
    L_0x0283:
        r0 = r33;
        r2 = r0.hasGamePreview;
        if (r2 != 0) goto L_0x0293;
    L_0x0289:
        r0 = r34;
        r2 = r0.lastLineWidth;
        r2 = r15 - r2;
        r0 = r16;
        if (r2 >= r0) goto L_0x0614;
    L_0x0293:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r0 = r34;
        r3 = r0.lastLineWidth;
        r2 = java.lang.Math.max(r2, r3);
        r3 = 1106771968; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.backgroundWidth = r2;
        r0 = r33;
        r2 = r0.backgroundWidth;
        r0 = r33;
        r3 = r0.timeWidth;
        r4 = 1106771968; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 + r4;
        r2 = java.lang.Math.max(r2, r3);
        r0 = r33;
        r0.backgroundWidth = r2;
    L_0x02c1:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r3 = 1106771968; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r0 = r33;
        r0.availableTimeWidth = r2;
        r33.setMessageObjectInternal(r34);
        r0 = r34;
        r3 = r0.textWidth;
        r0 = r33;
        r2 = r0.hasGamePreview;
        if (r2 == 0) goto L_0x0653;
    L_0x02dd:
        r2 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
    L_0x02e3:
        r2 = r2 + r3;
        r0 = r33;
        r0.backgroundWidth = r2;
        r0 = r34;
        r2 = r0.textHeight;
        r3 = 1100742656; // 0x419c0000 float:19.5 double:5.43839131E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r3 = r0.namesOffset;
        r2 = r2 + r3;
        r0 = r33;
        r0.totalHeight = r2;
        r0 = r33;
        r2 = r0.backgroundWidth;
        r0 = r33;
        r3 = r0.nameWidth;
        r2 = java.lang.Math.max(r2, r3);
        r0 = r33;
        r3 = r0.forwardedNameWidth;
        r2 = java.lang.Math.max(r2, r3);
        r0 = r33;
        r3 = r0.replyNameWidth;
        r2 = java.lang.Math.max(r2, r3);
        r0 = r33;
        r3 = r0.replyTextWidth;
        r13 = java.lang.Math.max(r2, r3);
        r26 = 0;
        r0 = r33;
        r2 = r0.hasLinkPreview;
        if (r2 != 0) goto L_0x032e;
    L_0x0328:
        r0 = r33;
        r2 = r0.hasGamePreview;
        if (r2 == 0) goto L_0x13a2;
    L_0x032e:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x0663;
    L_0x0334:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x0656;
    L_0x033a:
        r0 = r33;
        r2 = r0.currentMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.to_id;
        r2 = r2.channel_id;
        if (r2 != 0) goto L_0x0352;
    L_0x0346:
        r0 = r33;
        r2 = r0.currentMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.to_id;
        r2 = r2.chat_id;
        if (r2 == 0) goto L_0x0656;
    L_0x0352:
        r0 = r33;
        r2 = r0.currentMessageObject;
        r2 = r2.isOut();
        if (r2 != 0) goto L_0x0656;
    L_0x035c:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r3 = 1123287040; // 0x42f40000 float:122.0 double:5.54977537E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
    L_0x0367:
        r0 = r33;
        r3 = r0.drawShareButton;
        if (r3 != 0) goto L_0x0379;
    L_0x036d:
        r0 = r33;
        r3 = r0.drawFavoriteButton;
        if (r3 != 0) goto L_0x0379;
    L_0x0373:
        r0 = r33;
        r3 = r0.drawMenuButton;
        if (r3 == 0) goto L_0x2452;
    L_0x0379:
        r3 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = r2;
    L_0x0381:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.webpage;
        if (r2 == 0) goto L_0x06bb;
    L_0x038b:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.webpage;
        r2 = (com.hanista.mobogram.tgnet.TLRPC.TL_webPage) r2;
        r11 = r2.site_name;
        r10 = r2.title;
        r9 = r2.author;
        r8 = r2.description;
        r7 = r2.photo;
        r6 = r2.document;
        r4 = r2.type;
        r5 = r2.duration;
        if (r11 == 0) goto L_0x244f;
    L_0x03a7:
        if (r7 == 0) goto L_0x244f;
    L_0x03a9:
        r2 = r11.toLowerCase();
        r12 = "instagram";
        r2 = r2.equals(r12);
        if (r2 == 0) goto L_0x244f;
    L_0x03b6:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r2 = r2.y;
        r2 = r2 / 3;
        r0 = r33;
        r3 = r0.currentMessageObject;
        r3 = r3.textWidth;
        r3 = java.lang.Math.max(r2, r3);
        r12 = r3;
    L_0x03c7:
        if (r4 == 0) goto L_0x06b5;
    L_0x03c9:
        r2 = "app";
        r2 = r4.equals(r2);
        if (r2 != 0) goto L_0x03e4;
    L_0x03d2:
        r2 = "profile";
        r2 = r4.equals(r2);
        if (r2 != 0) goto L_0x03e4;
    L_0x03db:
        r2 = "article";
        r2 = r4.equals(r2);
        if (r2 == 0) goto L_0x06b5;
    L_0x03e4:
        r3 = 1;
    L_0x03e5:
        if (r8 == 0) goto L_0x06b8;
    L_0x03e7:
        if (r4 == 0) goto L_0x06b8;
    L_0x03e9:
        r2 = "app";
        r2 = r4.equals(r2);
        if (r2 != 0) goto L_0x0404;
    L_0x03f2:
        r2 = "profile";
        r2 = r4.equals(r2);
        if (r2 != 0) goto L_0x0404;
    L_0x03fb:
        r2 = "article";
        r2 = r4.equals(r2);
        if (r2 == 0) goto L_0x06b8;
    L_0x0404:
        r0 = r33;
        r2 = r0.currentMessageObject;
        r2 = r2.photoThumbs;
        if (r2 == 0) goto L_0x06b8;
    L_0x040c:
        r2 = 1;
    L_0x040d:
        r0 = r33;
        r0.isSmallImage = r2;
        r17 = r4;
        r18 = r3;
        r19 = r5;
        r20 = r6;
        r21 = r7;
        r22 = r8;
        r23 = r9;
        r2 = r12;
        r3 = r11;
        r12 = r10;
    L_0x0422:
        r4 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r31 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r25 = 3;
        r10 = 0;
        r28 = r2 - r31;
        r0 = r33;
        r2 = r0.currentMessageObject;
        r2 = r2.photoThumbs;
        if (r2 != 0) goto L_0x043f;
    L_0x0435:
        if (r21 == 0) goto L_0x043f;
    L_0x0437:
        r0 = r33;
        r2 = r0.currentMessageObject;
        r4 = 1;
        r2.generateThumbs(r4);
    L_0x043f:
        if (r3 == 0) goto L_0x2449;
    L_0x0441:
        r2 = replyNamePaint;	 Catch:{ Exception -> 0x06f6 }
        r2 = r2.measureText(r3);	 Catch:{ Exception -> 0x06f6 }
        r4 = (double) r2;	 Catch:{ Exception -> 0x06f6 }
        r4 = java.lang.Math.ceil(r4);	 Catch:{ Exception -> 0x06f6 }
        r5 = (int) r4;	 Catch:{ Exception -> 0x06f6 }
        r2 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x06f6 }
        r4 = replyNamePaint;	 Catch:{ Exception -> 0x06f6 }
        r0 = r28;
        r5 = java.lang.Math.min(r5, r0);	 Catch:{ Exception -> 0x06f6 }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x06f6 }
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x06f6 }
        r0 = r33;
        r0.siteNameLayout = r2;	 Catch:{ Exception -> 0x06f6 }
        r0 = r33;
        r2 = r0.siteNameLayout;	 Catch:{ Exception -> 0x06f6 }
        r0 = r33;
        r4 = r0.siteNameLayout;	 Catch:{ Exception -> 0x06f6 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x06f6 }
        r4 = r4 + -1;
        r2 = r2.getLineBottom(r4);	 Catch:{ Exception -> 0x06f6 }
        r0 = r33;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x06f6 }
        r4 = r4 + r2;
        r0 = r33;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x06f6 }
        r0 = r33;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x06f6 }
        r4 = r4 + r2;
        r0 = r33;
        r0.totalHeight = r4;	 Catch:{ Exception -> 0x06f6 }
        r4 = r10 + r2;
        r0 = r33;
        r2 = r0.siteNameLayout;	 Catch:{ Exception -> 0x23eb }
        r2 = r2.getWidth();	 Catch:{ Exception -> 0x23eb }
        r5 = r2 + r31;
        r5 = java.lang.Math.max(r13, r5);	 Catch:{ Exception -> 0x23eb }
        r2 = r2 + r31;
        r0 = r26;
        r26 = java.lang.Math.max(r0, r2);	 Catch:{ Exception -> 0x23ef }
        r29 = r4;
        r27 = r5;
    L_0x04a4:
        r24 = 0;
        if (r12 == 0) goto L_0x243f;
    L_0x04a8:
        r2 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0 = r33;
        r0.titleX = r2;	 Catch:{ Exception -> 0x23d7 }
        r0 = r33;
        r2 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x23d7 }
        if (r2 == 0) goto L_0x04d3;
    L_0x04b5:
        r0 = r33;
        r2 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x23d7 }
        r4 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x23d7 }
        r2 = r2 + r4;
        r0 = r33;
        r0.linkPreviewHeight = r2;	 Catch:{ Exception -> 0x23d7 }
        r0 = r33;
        r2 = r0.totalHeight;	 Catch:{ Exception -> 0x23d7 }
        r4 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x23d7 }
        r2 = r2 + r4;
        r0 = r33;
        r0.totalHeight = r2;	 Catch:{ Exception -> 0x23d7 }
    L_0x04d3:
        r2 = 0;
        r0 = r33;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x23d7 }
        if (r4 == 0) goto L_0x04dc;
    L_0x04da:
        if (r22 != 0) goto L_0x0705;
    L_0x04dc:
        r5 = replyNamePaint;	 Catch:{ Exception -> 0x23d7 }
        r7 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x23d7 }
        r8 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x23d7 }
        r9 = (float) r4;	 Catch:{ Exception -> 0x23d7 }
        r10 = 0;
        r11 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x23d7 }
        r13 = 4;
        r4 = r12;
        r6 = r28;
        r12 = r28;
        r4 = com.hanista.mobogram.ui.Components.StaticLayoutEx.createStaticLayout(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x23d7 }
        r0 = r33;
        r0.titleLayout = r4;	 Catch:{ Exception -> 0x23d7 }
        r5 = r25;
        r25 = r2;
    L_0x04fe:
        r0 = r33;
        r2 = r0.titleLayout;	 Catch:{ Exception -> 0x23e2 }
        r0 = r33;
        r4 = r0.titleLayout;	 Catch:{ Exception -> 0x23e2 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x23e2 }
        r4 = r4 + -1;
        r2 = r2.getLineBottom(r4);	 Catch:{ Exception -> 0x23e2 }
        r0 = r33;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x23e2 }
        r4 = r4 + r2;
        r0 = r33;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x23e2 }
        r0 = r33;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x23e2 }
        r2 = r2 + r4;
        r0 = r33;
        r0.totalHeight = r2;	 Catch:{ Exception -> 0x23e2 }
        r2 = 0;
        r8 = r2;
        r4 = r24;
        r6 = r26;
        r7 = r27;
    L_0x052a:
        r0 = r33;
        r2 = r0.titleLayout;	 Catch:{ Exception -> 0x0738 }
        r2 = r2.getLineCount();	 Catch:{ Exception -> 0x0738 }
        if (r8 >= r2) goto L_0x08a5;
    L_0x0534:
        r0 = r33;
        r2 = r0.titleLayout;	 Catch:{ Exception -> 0x0738 }
        r2 = r2.getLineLeft(r8);	 Catch:{ Exception -> 0x0738 }
        r9 = (int) r2;	 Catch:{ Exception -> 0x0738 }
        if (r9 == 0) goto L_0x0540;
    L_0x053f:
        r4 = 1;
    L_0x0540:
        r0 = r33;
        r2 = r0.titleX;	 Catch:{ Exception -> 0x0738 }
        r10 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r2 != r10) goto L_0x0729;
    L_0x0549:
        r2 = -r9;
        r0 = r33;
        r0.titleX = r2;	 Catch:{ Exception -> 0x0738 }
    L_0x054e:
        if (r9 == 0) goto L_0x0895;
    L_0x0550:
        r0 = r33;
        r2 = r0.titleLayout;	 Catch:{ Exception -> 0x0738 }
        r2 = r2.getWidth();	 Catch:{ Exception -> 0x0738 }
        r2 = r2 - r9;
    L_0x0559:
        r0 = r25;
        if (r8 < r0) goto L_0x0565;
    L_0x055d:
        if (r9 == 0) goto L_0x056c;
    L_0x055f:
        r0 = r33;
        r9 = r0.isSmallImage;	 Catch:{ Exception -> 0x0738 }
        if (r9 == 0) goto L_0x056c;
    L_0x0565:
        r9 = 1112539136; // 0x42500000 float:52.0 double:5.496673668E-315;
        r9 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Exception -> 0x0738 }
        r2 = r2 + r9;
    L_0x056c:
        r9 = r2 + r31;
        r7 = java.lang.Math.max(r7, r9);	 Catch:{ Exception -> 0x0738 }
        r2 = r2 + r31;
        r6 = java.lang.Math.max(r6, r2);	 Catch:{ Exception -> 0x0738 }
        r2 = r8 + 1;
        r8 = r2;
        goto L_0x052a;
    L_0x057c:
        r2 = 0;
        goto L_0x0023;
    L_0x057f:
        r2 = 0;
        r14 = r2;
        goto L_0x0048;
    L_0x0583:
        r2 = 0;
        goto L_0x0057;
    L_0x0586:
        r3 = 0;
        r30 = r3;
        goto L_0x006e;
    L_0x058b:
        r3 = 0;
        goto L_0x00cd;
    L_0x058e:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.to_id;
        r2 = r2.channel_id;
        if (r2 == 0) goto L_0x05b1;
    L_0x0598:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x05b1;
    L_0x059e:
        r2 = 1;
    L_0x059f:
        r0 = r33;
        r0.drawName = r2;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r3 = 1117782016; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r15 = r2;
        goto L_0x0222;
    L_0x05b1:
        r2 = 0;
        goto L_0x059f;
    L_0x05b3:
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x05e0;
    L_0x05b9:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x05e0;
    L_0x05bf:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x05e0;
    L_0x05c5:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r2 = r2.x;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r3.y;
        r2 = java.lang.Math.min(r2, r3);
        r3 = 1123287040; // 0x42f40000 float:122.0 double:5.54977537E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = 1;
        r0 = r33;
        r0.drawName = r3;
        r15 = r2;
        goto L_0x0222;
    L_0x05e0:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r2 = r2.x;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r3.y;
        r2 = java.lang.Math.min(r2, r3);
        r3 = 1117782016; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r3 = r2 - r3;
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.to_id;
        r2 = r2.channel_id;
        if (r2 == 0) goto L_0x060c;
    L_0x05fe:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x060c;
    L_0x0604:
        r2 = 1;
    L_0x0605:
        r0 = r33;
        r0.drawName = r2;
        r15 = r3;
        goto L_0x0222;
    L_0x060c:
        r2 = 0;
        goto L_0x0605;
    L_0x060e:
        r2 = 0;
        goto L_0x025a;
    L_0x0611:
        r2 = 0;
        goto L_0x0275;
    L_0x0614:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r0 = r34;
        r3 = r0.lastLineWidth;
        r2 = r2 - r3;
        if (r2 < 0) goto L_0x0638;
    L_0x061f:
        r0 = r16;
        if (r2 > r0) goto L_0x0638;
    L_0x0623:
        r0 = r33;
        r3 = r0.backgroundWidth;
        r3 = r3 + r16;
        r2 = r3 - r2;
        r3 = 1106771968; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.backgroundWidth = r2;
        goto L_0x02c1;
    L_0x0638:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r0 = r34;
        r3 = r0.lastLineWidth;
        r3 = r3 + r16;
        r2 = java.lang.Math.max(r2, r3);
        r3 = 1106771968; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.backgroundWidth = r2;
        goto L_0x02c1;
    L_0x0653:
        r2 = 0;
        goto L_0x02e3;
    L_0x0656:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r3 = 1117782016; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        goto L_0x0367;
    L_0x0663:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x06a0;
    L_0x0669:
        r0 = r33;
        r2 = r0.currentMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.to_id;
        r2 = r2.channel_id;
        if (r2 != 0) goto L_0x0681;
    L_0x0675:
        r0 = r33;
        r2 = r0.currentMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.to_id;
        r2 = r2.chat_id;
        if (r2 == 0) goto L_0x06a0;
    L_0x0681:
        r0 = r33;
        r2 = r0.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 != 0) goto L_0x06a0;
    L_0x068b:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r2 = r2.x;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r3.y;
        r2 = java.lang.Math.min(r2, r3);
        r3 = 1123287040; // 0x42f40000 float:122.0 double:5.54977537E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        goto L_0x0367;
    L_0x06a0:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r2 = r2.x;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r3.y;
        r2 = java.lang.Math.min(r2, r3);
        r3 = 1117782016; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        goto L_0x0367;
    L_0x06b5:
        r3 = 0;
        goto L_0x03e5;
    L_0x06b8:
        r2 = 0;
        goto L_0x040d;
    L_0x06bb:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r4 = r2.game;
        r11 = r4.title;
        r10 = 0;
        r0 = r34;
        r2 = r0.messageText;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 == 0) goto L_0x06f4;
    L_0x06d0:
        r2 = r4.description;
    L_0x06d2:
        r8 = r4.photo;
        r9 = 0;
        r7 = r4.document;
        r6 = 0;
        r4 = "game";
        r5 = 0;
        r0 = r33;
        r0.isSmallImage = r5;
        r5 = 0;
        r17 = r4;
        r18 = r5;
        r19 = r6;
        r20 = r7;
        r21 = r8;
        r22 = r2;
        r23 = r9;
        r12 = r10;
        r2 = r3;
        r3 = r11;
        goto L_0x0422;
    L_0x06f4:
        r2 = 0;
        goto L_0x06d2;
    L_0x06f6:
        r2 = move-exception;
        r4 = r10;
        r5 = r13;
    L_0x06f9:
        r6 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r6, r2);
        r29 = r4;
        r27 = r5;
        goto L_0x04a4;
    L_0x0705:
        r5 = replyNamePaint;	 Catch:{ Exception -> 0x23d7 }
        r2 = 1112539136; // 0x42500000 float:52.0 double:5.496673668E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x23d7 }
        r7 = r28 - r2;
        r9 = 4;
        r4 = r12;
        r6 = r28;
        r8 = r25;
        r2 = generateStaticLayout(r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x23d7 }
        r0 = r33;
        r0.titleLayout = r2;	 Catch:{ Exception -> 0x23d7 }
        r0 = r33;
        r2 = r0.titleLayout;	 Catch:{ Exception -> 0x23d7 }
        r2 = r2.getLineCount();	 Catch:{ Exception -> 0x23d7 }
        r5 = r25 - r2;
        goto L_0x04fe;
    L_0x0729:
        r0 = r33;
        r2 = r0.titleX;	 Catch:{ Exception -> 0x0738 }
        r10 = -r9;
        r2 = java.lang.Math.max(r2, r10);	 Catch:{ Exception -> 0x0738 }
        r0 = r33;
        r0.titleX = r2;	 Catch:{ Exception -> 0x0738 }
        goto L_0x054e;
    L_0x0738:
        r2 = move-exception;
    L_0x0739:
        r8 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r8, r2);
        r26 = r4;
        r13 = r5;
        r25 = r6;
        r24 = r7;
    L_0x0746:
        r12 = 0;
        if (r23 == 0) goto L_0x2436;
    L_0x0749:
        r0 = r33;
        r2 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x08e4 }
        if (r2 == 0) goto L_0x076d;
    L_0x074f:
        r0 = r33;
        r2 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x08e4 }
        r4 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x08e4 }
        r2 = r2 + r4;
        r0 = r33;
        r0.linkPreviewHeight = r2;	 Catch:{ Exception -> 0x08e4 }
        r0 = r33;
        r2 = r0.totalHeight;	 Catch:{ Exception -> 0x08e4 }
        r4 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x08e4 }
        r2 = r2 + r4;
        r0 = r33;
        r0.totalHeight = r2;	 Catch:{ Exception -> 0x08e4 }
    L_0x076d:
        r2 = 3;
        if (r13 != r2) goto L_0x08ae;
    L_0x0770:
        r0 = r33;
        r2 = r0.isSmallImage;	 Catch:{ Exception -> 0x08e4 }
        if (r2 == 0) goto L_0x0778;
    L_0x0776:
        if (r22 != 0) goto L_0x08ae;
    L_0x0778:
        r4 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x08e4 }
        r6 = replyNamePaint;	 Catch:{ Exception -> 0x08e4 }
        r8 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x08e4 }
        r9 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r10 = 0;
        r11 = 0;
        r5 = r23;
        r7 = r28;
        r4.<init>(r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x08e4 }
        r0 = r33;
        r0.authorLayout = r4;	 Catch:{ Exception -> 0x08e4 }
        r5 = r13;
    L_0x078e:
        r0 = r33;
        r2 = r0.authorLayout;	 Catch:{ Exception -> 0x23c9 }
        r0 = r33;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x23c9 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x23c9 }
        r4 = r4 + -1;
        r2 = r2.getLineBottom(r4);	 Catch:{ Exception -> 0x23c9 }
        r0 = r33;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x23c9 }
        r4 = r4 + r2;
        r0 = r33;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x23c9 }
        r0 = r33;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x23c9 }
        r2 = r2 + r4;
        r0 = r33;
        r0.totalHeight = r2;	 Catch:{ Exception -> 0x23c9 }
        r0 = r33;
        r2 = r0.authorLayout;	 Catch:{ Exception -> 0x23c9 }
        r4 = 0;
        r2 = r2.getLineLeft(r4);	 Catch:{ Exception -> 0x23c9 }
        r2 = (int) r2;	 Catch:{ Exception -> 0x23c9 }
        r4 = -r2;
        r0 = r33;
        r0.authorX = r4;	 Catch:{ Exception -> 0x23c9 }
        if (r2 == 0) goto L_0x08d2;
    L_0x07c3:
        r0 = r33;
        r4 = r0.authorLayout;	 Catch:{ Exception -> 0x23c9 }
        r4 = r4.getWidth();	 Catch:{ Exception -> 0x23c9 }
        r2 = r4 - r2;
        r4 = 1;
    L_0x07ce:
        r6 = r2 + r31;
        r0 = r24;
        r6 = java.lang.Math.max(r0, r6);	 Catch:{ Exception -> 0x23cf }
        r2 = r2 + r31;
        r0 = r25;
        r24 = java.lang.Math.max(r0, r2);	 Catch:{ Exception -> 0x23d4 }
        r25 = r4;
        r8 = r5;
        r23 = r6;
    L_0x07e3:
        if (r22 == 0) goto L_0x2432;
    L_0x07e5:
        r2 = 0;
        r0 = r33;
        r0.descriptionX = r2;	 Catch:{ Exception -> 0x0923 }
        r0 = r33;
        r2 = r0.currentMessageObject;	 Catch:{ Exception -> 0x0923 }
        r2.generateLinkDescription();	 Catch:{ Exception -> 0x0923 }
        r0 = r33;
        r2 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0923 }
        if (r2 == 0) goto L_0x0815;
    L_0x07f7:
        r0 = r33;
        r2 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0923 }
        r4 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0923 }
        r2 = r2 + r4;
        r0 = r33;
        r0.linkPreviewHeight = r2;	 Catch:{ Exception -> 0x0923 }
        r0 = r33;
        r2 = r0.totalHeight;	 Catch:{ Exception -> 0x0923 }
        r4 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0923 }
        r2 = r2 + r4;
        r0 = r33;
        r0.totalHeight = r2;	 Catch:{ Exception -> 0x0923 }
    L_0x0815:
        r2 = 0;
        r4 = 3;
        if (r8 != r4) goto L_0x08f8;
    L_0x0819:
        r0 = r33;
        r4 = r0.isSmallImage;	 Catch:{ Exception -> 0x0923 }
        if (r4 != 0) goto L_0x08f8;
    L_0x081f:
        r0 = r34;
        r4 = r0.linkDescription;	 Catch:{ Exception -> 0x0923 }
        r5 = replyTextPaint;	 Catch:{ Exception -> 0x0923 }
        r7 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0923 }
        r8 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0923 }
        r9 = (float) r6;	 Catch:{ Exception -> 0x0923 }
        r10 = 0;
        r11 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0923 }
        r13 = 6;
        r6 = r28;
        r12 = r28;
        r4 = com.hanista.mobogram.ui.Components.StaticLayoutEx.createStaticLayout(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0923 }
        r0 = r33;
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x0923 }
        r9 = r2;
    L_0x0841:
        r0 = r33;
        r2 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0923 }
        r0 = r33;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0923 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x0923 }
        r4 = r4 + -1;
        r2 = r2.getLineBottom(r4);	 Catch:{ Exception -> 0x0923 }
        r0 = r33;
        r4 = r0.linkPreviewHeight;	 Catch:{ Exception -> 0x0923 }
        r4 = r4 + r2;
        r0 = r33;
        r0.linkPreviewHeight = r4;	 Catch:{ Exception -> 0x0923 }
        r0 = r33;
        r4 = r0.totalHeight;	 Catch:{ Exception -> 0x0923 }
        r2 = r2 + r4;
        r0 = r33;
        r0.totalHeight = r2;	 Catch:{ Exception -> 0x0923 }
        r4 = 0;
        r2 = 0;
        r32 = r2;
        r2 = r4;
        r4 = r32;
    L_0x086c:
        r0 = r33;
        r5 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0923 }
        r5 = r5.getLineCount();	 Catch:{ Exception -> 0x0923 }
        if (r4 >= r5) goto L_0x0d8f;
    L_0x0876:
        r0 = r33;
        r5 = r0.descriptionLayout;	 Catch:{ Exception -> 0x0923 }
        r5 = r5.getLineLeft(r4);	 Catch:{ Exception -> 0x0923 }
        r6 = (double) r5;	 Catch:{ Exception -> 0x0923 }
        r6 = java.lang.Math.ceil(r6);	 Catch:{ Exception -> 0x0923 }
        r5 = (int) r6;	 Catch:{ Exception -> 0x0923 }
        if (r5 == 0) goto L_0x0892;
    L_0x0886:
        r2 = 1;
        r0 = r33;
        r6 = r0.descriptionX;	 Catch:{ Exception -> 0x0923 }
        if (r6 != 0) goto L_0x0914;
    L_0x088d:
        r5 = -r5;
        r0 = r33;
        r0.descriptionX = r5;	 Catch:{ Exception -> 0x0923 }
    L_0x0892:
        r4 = r4 + 1;
        goto L_0x086c;
    L_0x0895:
        r0 = r33;
        r2 = r0.titleLayout;	 Catch:{ Exception -> 0x0738 }
        r2 = r2.getLineWidth(r8);	 Catch:{ Exception -> 0x0738 }
        r10 = (double) r2;	 Catch:{ Exception -> 0x0738 }
        r10 = java.lang.Math.ceil(r10);	 Catch:{ Exception -> 0x0738 }
        r2 = (int) r10;
        goto L_0x0559;
    L_0x08a5:
        r26 = r4;
        r13 = r5;
        r25 = r6;
        r24 = r7;
        goto L_0x0746;
    L_0x08ae:
        r5 = replyNamePaint;	 Catch:{ Exception -> 0x08e4 }
        r2 = 1112539136; // 0x42500000 float:52.0 double:5.496673668E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x08e4 }
        r7 = r28 - r2;
        r9 = 1;
        r4 = r23;
        r6 = r28;
        r8 = r13;
        r2 = generateStaticLayout(r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x08e4 }
        r0 = r33;
        r0.authorLayout = r2;	 Catch:{ Exception -> 0x08e4 }
        r0 = r33;
        r2 = r0.authorLayout;	 Catch:{ Exception -> 0x08e4 }
        r2 = r2.getLineCount();	 Catch:{ Exception -> 0x08e4 }
        r5 = r13 - r2;
        goto L_0x078e;
    L_0x08d2:
        r0 = r33;
        r2 = r0.authorLayout;	 Catch:{ Exception -> 0x23c9 }
        r4 = 0;
        r2 = r2.getLineWidth(r4);	 Catch:{ Exception -> 0x23c9 }
        r6 = (double) r2;	 Catch:{ Exception -> 0x23c9 }
        r6 = java.lang.Math.ceil(r6);	 Catch:{ Exception -> 0x23c9 }
        r2 = (int) r6;
        r4 = r12;
        goto L_0x07ce;
    L_0x08e4:
        r2 = move-exception;
        r4 = r12;
        r5 = r13;
        r6 = r24;
    L_0x08e9:
        r7 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r7, r2);
        r8 = r5;
        r24 = r25;
        r23 = r6;
        r25 = r4;
        goto L_0x07e3;
    L_0x08f8:
        r0 = r34;
        r4 = r0.linkDescription;	 Catch:{ Exception -> 0x0923 }
        r5 = replyTextPaint;	 Catch:{ Exception -> 0x0923 }
        r2 = 1112539136; // 0x42500000 float:52.0 double:5.496673668E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x0923 }
        r7 = r28 - r2;
        r9 = 6;
        r6 = r28;
        r2 = generateStaticLayout(r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x0923 }
        r0 = r33;
        r0.descriptionLayout = r2;	 Catch:{ Exception -> 0x0923 }
        r9 = r8;
        goto L_0x0841;
    L_0x0914:
        r0 = r33;
        r6 = r0.descriptionX;	 Catch:{ Exception -> 0x0923 }
        r5 = -r5;
        r5 = java.lang.Math.max(r6, r5);	 Catch:{ Exception -> 0x0923 }
        r0 = r33;
        r0.descriptionX = r5;	 Catch:{ Exception -> 0x0923 }
        goto L_0x0892;
    L_0x0923:
        r2 = move-exception;
        r4 = r23;
    L_0x0926:
        r5 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r5, r2);
        r6 = r4;
    L_0x092d:
        if (r18 == 0) goto L_0x094d;
    L_0x092f:
        r0 = r33;
        r2 = r0.descriptionLayout;
        if (r2 == 0) goto L_0x0946;
    L_0x0935:
        r0 = r33;
        r2 = r0.descriptionLayout;
        if (r2 == 0) goto L_0x094d;
    L_0x093b:
        r0 = r33;
        r2 = r0.descriptionLayout;
        r2 = r2.getLineCount();
        r4 = 1;
        if (r2 != r4) goto L_0x094d;
    L_0x0946:
        r18 = 0;
        r2 = 0;
        r0 = r33;
        r0.isSmallImage = r2;
    L_0x094d:
        if (r18 == 0) goto L_0x0e26;
    L_0x094f:
        r2 = 1111490560; // 0x42400000 float:48.0 double:5.491493014E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
    L_0x0955:
        if (r20 == 0) goto L_0x1125;
    L_0x0957:
        r2 = com.hanista.mobogram.messenger.MessageObject.isGifDocument(r20);
        if (r2 == 0) goto L_0x0e32;
    L_0x095d:
        r2 = com.hanista.mobogram.messenger.MediaController.m71a();
        r2 = r2.m140B();
        if (r2 != 0) goto L_0x096d;
    L_0x0967:
        r2 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r34;
        r0.audioProgress = r2;
    L_0x096d:
        r0 = r33;
        r4 = r0.photoImage;
        r0 = r34;
        r2 = r0.audioProgress;
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
        if (r2 == 0) goto L_0x0e2a;
    L_0x097b:
        r2 = 1;
    L_0x097c:
        r4.setAllowStartAnimation(r2);
        r0 = r20;
        r2 = r0.thumb;
        r0 = r33;
        r0.currentPhotoObject = r2;
        r0 = r33;
        r2 = r0.currentPhotoObject;
        if (r2 == 0) goto L_0x09ed;
    L_0x098d:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2664w;
        if (r2 == 0) goto L_0x099d;
    L_0x0995:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2663h;
        if (r2 != 0) goto L_0x09ed;
    L_0x099d:
        r2 = 0;
        r4 = r2;
    L_0x099f:
        r0 = r20;
        r2 = r0.attributes;
        r2 = r2.size();
        if (r4 >= r2) goto L_0x09cb;
    L_0x09a9:
        r0 = r20;
        r2 = r0.attributes;
        r2 = r2.get(r4);
        r2 = (com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute) r2;
        r7 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r7 != 0) goto L_0x09bb;
    L_0x09b7:
        r7 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r7 == 0) goto L_0x0e2d;
    L_0x09bb:
        r0 = r33;
        r4 = r0.currentPhotoObject;
        r7 = r2.f2659w;
        r4.f2664w = r7;
        r0 = r33;
        r4 = r0.currentPhotoObject;
        r2 = r2.f2658h;
        r4.f2663h = r2;
    L_0x09cb:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2664w;
        if (r2 == 0) goto L_0x09db;
    L_0x09d3:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2663h;
        if (r2 != 0) goto L_0x09ed;
    L_0x09db:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r0 = r33;
        r4 = r0.currentPhotoObject;
        r7 = 1125515264; // 0x43160000 float:150.0 double:5.56078426E-315;
        r7 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r7);
        r4.f2663h = r7;
        r2.f2664w = r7;
    L_0x09ed:
        r2 = 2;
        r0 = r33;
        r0.documentAttachType = r2;
        r4 = r6;
    L_0x09f3:
        r0 = r33;
        r2 = r0.documentAttachType;
        r6 = 5;
        if (r2 == r6) goto L_0x0bec;
    L_0x09fa:
        r0 = r33;
        r2 = r0.documentAttachType;
        r6 = 3;
        if (r2 == r6) goto L_0x0bec;
    L_0x0a01:
        r0 = r33;
        r2 = r0.documentAttachType;
        r6 = 1;
        if (r2 == r6) goto L_0x0bec;
    L_0x0a08:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        if (r2 == 0) goto L_0x1378;
    L_0x0a0e:
        if (r17 == 0) goto L_0x117c;
    L_0x0a10:
        r2 = "photo";
        r0 = r17;
        r2 = r0.equals(r2);
        if (r2 != 0) goto L_0x0a3f;
    L_0x0a1b:
        r2 = "document";
        r0 = r17;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0a2d;
    L_0x0a26:
        r0 = r33;
        r2 = r0.documentAttachType;
        r6 = 6;
        if (r2 != r6) goto L_0x0a3f;
    L_0x0a2d:
        r2 = "gif";
        r0 = r17;
        r2 = r0.equals(r2);
        if (r2 != 0) goto L_0x0a3f;
    L_0x0a38:
        r0 = r33;
        r2 = r0.documentAttachType;
        r6 = 4;
        if (r2 != r6) goto L_0x117c;
    L_0x0a3f:
        r2 = 1;
    L_0x0a40:
        r0 = r33;
        r0.drawImageButton = r2;
        r0 = r33;
        r2 = r0.linkPreviewHeight;
        if (r2 == 0) goto L_0x0a68;
    L_0x0a4a:
        r0 = r33;
        r2 = r0.linkPreviewHeight;
        r6 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r6);
        r2 = r2 + r6;
        r0 = r33;
        r0.linkPreviewHeight = r2;
        r0 = r33;
        r2 = r0.totalHeight;
        r6 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r6);
        r2 = r2 + r6;
        r0 = r33;
        r0.totalHeight = r2;
    L_0x0a68:
        r0 = r33;
        r2 = r0.documentAttachType;
        r6 = 6;
        if (r2 != r6) goto L_0x0a7e;
    L_0x0a6f:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x117f;
    L_0x0a75:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r2 = (float) r2;
        r5 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r2 = r2 * r5;
        r5 = (int) r2;
    L_0x0a7e:
        r2 = r5 + r31;
        r12 = java.lang.Math.max(r4, r2);
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r4 = -1;
        r2.size = r4;
        r0 = r33;
        r2 = r0.currentPhotoObjectThumb;
        if (r2 == 0) goto L_0x0a98;
    L_0x0a91:
        r0 = r33;
        r2 = r0.currentPhotoObjectThumb;
        r4 = -1;
        r2.size = r4;
    L_0x0a98:
        if (r18 == 0) goto L_0x118a;
    L_0x0a9a:
        r6 = r5;
        r7 = r5;
    L_0x0a9c:
        r0 = r33;
        r2 = r0.isSmallImage;
        if (r2 == 0) goto L_0x11ee;
    L_0x0aa2:
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r29;
        r0 = r33;
        r3 = r0.linkPreviewHeight;
        if (r2 <= r3) goto L_0x0ad9;
    L_0x0ab0:
        r0 = r33;
        r2 = r0.totalHeight;
        r3 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r29;
        r0 = r33;
        r4 = r0.linkPreviewHeight;
        r3 = r3 - r4;
        r4 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 + r4;
        r2 = r2 + r3;
        r0 = r33;
        r0.totalHeight = r2;
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r29;
        r0 = r33;
        r0.linkPreviewHeight = r2;
    L_0x0ad9:
        r0 = r33;
        r2 = r0.linkPreviewHeight;
        r3 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r0 = r33;
        r0.linkPreviewHeight = r2;
    L_0x0ae8:
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 0;
        r4 = 0;
        r2.setImageCoords(r3, r4, r7, r6);
        r2 = java.util.Locale.US;
        r3 = "%d_%d";
        r4 = 2;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r8 = java.lang.Integer.valueOf(r7);
        r4[r5] = r8;
        r5 = 1;
        r8 = java.lang.Integer.valueOf(r6);
        r4[r5] = r8;
        r2 = java.lang.String.format(r2, r3, r4);
        r0 = r33;
        r0.currentPhotoFilter = r2;
        r2 = java.util.Locale.US;
        r3 = "%d_%d_b";
        r4 = 2;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r8 = java.lang.Integer.valueOf(r7);
        r4[r5] = r8;
        r5 = 1;
        r8 = java.lang.Integer.valueOf(r6);
        r4[r5] = r8;
        r2 = java.lang.String.format(r2, r3, r4);
        r0 = r33;
        r0.currentPhotoFilterThumb = r2;
        r0 = r33;
        r2 = r0.documentAttachType;
        r3 = 6;
        if (r2 != r3) goto L_0x120c;
    L_0x0b34:
        r0 = r33;
        r2 = r0.photoImage;
        r0 = r33;
        r3 = r0.documentAttach;
        r4 = 0;
        r0 = r33;
        r5 = r0.currentPhotoFilter;
        r6 = 0;
        r0 = r33;
        r7 = r0.currentPhotoObject;
        if (r7 == 0) goto L_0x1209;
    L_0x0b48:
        r0 = r33;
        r7 = r0.currentPhotoObject;
        r7 = r7.location;
    L_0x0b4e:
        r8 = "b1";
        r0 = r33;
        r9 = r0.documentAttach;
        r9 = r9.size;
        r10 = "webp";
        r11 = 1;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11);
    L_0x0b5e:
        r2 = 1;
        r0 = r33;
        r0.drawPhotoImage = r2;
        if (r17 == 0) goto L_0x133d;
    L_0x0b65:
        r2 = "video";
        r0 = r17;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x133d;
    L_0x0b70:
        if (r19 == 0) goto L_0x133d;
    L_0x0b72:
        r2 = r19 / 60;
        r3 = r2 * 60;
        r3 = r19 - r3;
        r4 = "%d:%02d";
        r5 = 2;
        r5 = new java.lang.Object[r5];
        r6 = 0;
        r2 = java.lang.Integer.valueOf(r2);
        r5[r6] = r2;
        r2 = 1;
        r3 = java.lang.Integer.valueOf(r3);
        r5[r2] = r3;
        r3 = java.lang.String.format(r4, r5);
        r2 = durationPaint;
        r2 = r2.measureText(r3);
        r4 = (double) r2;
        r4 = java.lang.Math.ceil(r4);
        r2 = (int) r4;
        r0 = r33;
        r0.durationWidth = r2;
        r2 = new android.text.StaticLayout;
        r4 = durationPaint;
        r0 = r33;
        r5 = r0.durationWidth;
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);
        r0 = r33;
        r0.videoInfoLayout = r2;
    L_0x0bb5:
        r4 = r12;
    L_0x0bb6:
        r0 = r33;
        r2 = r0.hasGamePreview;
        if (r2 == 0) goto L_0x0be5;
    L_0x0bbc:
        r0 = r34;
        r2 = r0.textHeight;
        if (r2 == 0) goto L_0x0be5;
    L_0x0bc2:
        r0 = r33;
        r2 = r0.linkPreviewHeight;
        r0 = r34;
        r3 = r0.textHeight;
        r5 = 1086324736; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);
        r3 = r3 + r5;
        r2 = r2 + r3;
        r0 = r33;
        r0.linkPreviewHeight = r2;
        r0 = r33;
        r2 = r0.totalHeight;
        r3 = 1082130432; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.totalHeight = r2;
    L_0x0be5:
        r0 = r33;
        r1 = r16;
        r0.calcBackgroundWidth(r15, r1, r4);
    L_0x0bec:
        r0 = r33;
        r2 = r0.captionLayout;
        if (r2 != 0) goto L_0x0ce6;
    L_0x0bf2:
        r0 = r34;
        r2 = r0.caption;
        if (r2 == 0) goto L_0x0ce6;
    L_0x0bf8:
        r0 = r34;
        r2 = r0.type;
        r3 = 13;
        if (r2 == r3) goto L_0x0ce6;
    L_0x0c00:
        r0 = r33;
        r2 = r0.backgroundWidth;	 Catch:{ Exception -> 0x225c }
        r3 = 1106771968; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x225c }
        r10 = r2 - r3;
        r2 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x225c }
        r0 = r34;
        r3 = r0.caption;	 Catch:{ Exception -> 0x225c }
        r4 = com.hanista.mobogram.messenger.MessageObject.getTextPaint();	 Catch:{ Exception -> 0x225c }
        r5 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x225c }
        r5 = r10 - r5;
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x225c }
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x225c }
        r0 = r33;
        r0.captionLayout = r2;	 Catch:{ Exception -> 0x225c }
        r2 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();	 Catch:{ Exception -> 0x225c }
        if (r2 == 0) goto L_0x0c58;
    L_0x0c31:
        r2 = r34.isOutOwner();	 Catch:{ Exception -> 0x225c }
        if (r2 == 0) goto L_0x2256;
    L_0x0c37:
        r2 = com.hanista.mobogram.messenger.MessageObject.textPaintRight;	 Catch:{ Exception -> 0x225c }
        com.hanista.mobogram.messenger.MessageObject.textPaint = r2;	 Catch:{ Exception -> 0x225c }
    L_0x0c3b:
        r2 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x225c }
        r0 = r34;
        r3 = r0.caption;	 Catch:{ Exception -> 0x225c }
        r4 = com.hanista.mobogram.messenger.MessageObject.textPaint;	 Catch:{ Exception -> 0x225c }
        r5 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x225c }
        r5 = r10 - r5;
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x225c }
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x225c }
        r0 = r33;
        r0.captionLayout = r2;	 Catch:{ Exception -> 0x225c }
    L_0x0c58:
        r0 = r33;
        r2 = r0.captionLayout;	 Catch:{ Exception -> 0x225c }
        r2 = r2.getLineCount();	 Catch:{ Exception -> 0x225c }
        if (r2 <= 0) goto L_0x0ce6;
    L_0x0c62:
        r0 = r33;
        r3 = r0.timeWidth;	 Catch:{ Exception -> 0x225c }
        r2 = r34.isOutOwner();	 Catch:{ Exception -> 0x225c }
        if (r2 == 0) goto L_0x2265;
    L_0x0c6c:
        r2 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x225c }
    L_0x0c72:
        r2 = r2 + r3;
        r0 = r33;
        r3 = r0.captionLayout;	 Catch:{ Exception -> 0x225c }
        r3 = r3.getHeight();	 Catch:{ Exception -> 0x225c }
        r0 = r33;
        r0.captionHeight = r3;	 Catch:{ Exception -> 0x225c }
        r0 = r33;
        r3 = r0.totalHeight;	 Catch:{ Exception -> 0x225c }
        r0 = r33;
        r4 = r0.captionHeight;	 Catch:{ Exception -> 0x225c }
        r5 = 1091567616; // 0x41100000 float:9.0 double:5.39306059E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x225c }
        r4 = r4 + r5;
        r3 = r3 + r4;
        r0 = r33;
        r0.totalHeight = r3;	 Catch:{ Exception -> 0x225c }
        r0 = r33;
        r3 = r0.captionLayout;	 Catch:{ Exception -> 0x225c }
        r0 = r33;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x225c }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x225c }
        r4 = r4 + -1;
        r3 = r3.getLineWidth(r4);	 Catch:{ Exception -> 0x225c }
        r0 = r33;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x225c }
        r0 = r33;
        r5 = r0.captionLayout;	 Catch:{ Exception -> 0x225c }
        r5 = r5.getLineCount();	 Catch:{ Exception -> 0x225c }
        r5 = r5 + -1;
        r4 = r4.getLineLeft(r5);	 Catch:{ Exception -> 0x225c }
        r3 = r3 + r4;
        r4 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x225c }
        r4 = r10 - r4;
        r4 = (float) r4;	 Catch:{ Exception -> 0x225c }
        r3 = r4 - r3;
        r2 = (float) r2;	 Catch:{ Exception -> 0x225c }
        r2 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1));
        if (r2 >= 0) goto L_0x0ce6;
    L_0x0cc8:
        r0 = r33;
        r2 = r0.totalHeight;	 Catch:{ Exception -> 0x225c }
        r3 = 1096810496; // 0x41600000 float:14.0 double:5.41896386E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x225c }
        r2 = r2 + r3;
        r0 = r33;
        r0.totalHeight = r2;	 Catch:{ Exception -> 0x225c }
        r0 = r33;
        r2 = r0.captionHeight;	 Catch:{ Exception -> 0x225c }
        r3 = 1096810496; // 0x41600000 float:14.0 double:5.41896386E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x225c }
        r2 = r2 + r3;
        r0 = r33;
        r0.captionHeight = r2;	 Catch:{ Exception -> 0x225c }
    L_0x0ce6:
        r0 = r33;
        r2 = r0.botButtons;
        r2.clear();
        if (r14 == 0) goto L_0x0cf6;
    L_0x0cef:
        r0 = r33;
        r2 = r0.botButtonsByData;
        r2.clear();
    L_0x0cf6:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.reply_markup;
        r2 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_replyInlineMarkup;
        if (r2 == 0) goto L_0x23b7;
    L_0x0d00:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.reply_markup;
        r2 = r2.rows;
        r15 = r2.size();
        r2 = 1111490560; // 0x42400000 float:48.0 double:5.491493014E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 * r15;
        r3 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.keyboardHeight = r2;
        r0 = r33;
        r0.substractBackgroundHeight = r2;
        r0 = r33;
        r2 = r0.backgroundWidth;
        r0 = r33;
        r0.widthForButtons = r2;
        r2 = 0;
        r0 = r34;
        r3 = r0.wantedBotKeyboardWidth;
        r0 = r33;
        r4 = r0.widthForButtons;
        if (r3 <= r4) goto L_0x23f7;
    L_0x0d35:
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x2268;
    L_0x0d3b:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x2268;
    L_0x0d41:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x2268;
    L_0x0d47:
        r2 = 1115160576; // 0x42780000 float:62.0 double:5.5096253E-315;
    L_0x0d49:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = -r2;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r3 == 0) goto L_0x226c;
    L_0x0d54:
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r2 = r2 + r3;
    L_0x0d59:
        r0 = r33;
        r3 = r0.backgroundWidth;
        r0 = r34;
        r4 = r0.wantedBotKeyboardWidth;
        r2 = java.lang.Math.min(r4, r2);
        r2 = java.lang.Math.max(r3, r2);
        r0 = r33;
        r0.widthForButtons = r2;
        r2 = 1;
        r11 = r2;
    L_0x0d6f:
        r3 = 0;
        r2 = 0;
        r14 = r2;
    L_0x0d72:
        if (r14 >= r15) goto L_0x23a8;
    L_0x0d74:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.reply_markup;
        r2 = r2.rows;
        r2 = r2.get(r14);
        r10 = r2;
        r10 = (com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonRow) r10;
        r2 = r10.buttons;
        r4 = r2.size();
        if (r4 != 0) goto L_0x227b;
    L_0x0d8b:
        r2 = r14 + 1;
        r14 = r2;
        goto L_0x0d72;
    L_0x0d8f:
        r4 = 0;
        r8 = r4;
        r6 = r24;
        r5 = r23;
    L_0x0d95:
        r0 = r33;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x23c5 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x23c5 }
        if (r8 >= r4) goto L_0x0e23;
    L_0x0d9f:
        r0 = r33;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x23c5 }
        r4 = r4.getLineLeft(r8);	 Catch:{ Exception -> 0x23c5 }
        r10 = (double) r4;	 Catch:{ Exception -> 0x23c5 }
        r10 = java.lang.Math.ceil(r10);	 Catch:{ Exception -> 0x23c5 }
        r7 = (int) r10;	 Catch:{ Exception -> 0x23c5 }
        if (r7 != 0) goto L_0x0dba;
    L_0x0daf:
        r0 = r33;
        r4 = r0.descriptionX;	 Catch:{ Exception -> 0x23c5 }
        if (r4 == 0) goto L_0x0dba;
    L_0x0db5:
        r4 = 0;
        r0 = r33;
        r0.descriptionX = r4;	 Catch:{ Exception -> 0x23c5 }
    L_0x0dba:
        if (r7 == 0) goto L_0x0e09;
    L_0x0dbc:
        r0 = r33;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x23c5 }
        r4 = r4.getWidth();	 Catch:{ Exception -> 0x23c5 }
        r4 = r4 - r7;
    L_0x0dc5:
        if (r8 < r9) goto L_0x0dd1;
    L_0x0dc7:
        if (r9 == 0) goto L_0x242f;
    L_0x0dc9:
        if (r7 == 0) goto L_0x242f;
    L_0x0dcb:
        r0 = r33;
        r7 = r0.isSmallImage;	 Catch:{ Exception -> 0x23c5 }
        if (r7 == 0) goto L_0x242f;
    L_0x0dd1:
        r7 = 1112539136; // 0x42500000 float:52.0 double:5.496673668E-315;
        r7 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r7);	 Catch:{ Exception -> 0x23c5 }
        r4 = r4 + r7;
        r7 = r4;
    L_0x0dd9:
        r4 = r7 + r31;
        if (r6 >= r4) goto L_0x242c;
    L_0x0ddd:
        if (r26 == 0) goto L_0x0deb;
    L_0x0ddf:
        r0 = r33;
        r4 = r0.titleX;	 Catch:{ Exception -> 0x23c5 }
        r10 = r7 + r31;
        r10 = r10 - r6;
        r4 = r4 + r10;
        r0 = r33;
        r0.titleX = r4;	 Catch:{ Exception -> 0x23c5 }
    L_0x0deb:
        if (r25 == 0) goto L_0x0dfa;
    L_0x0ded:
        r0 = r33;
        r4 = r0.authorX;	 Catch:{ Exception -> 0x23c5 }
        r10 = r7 + r31;
        r6 = r10 - r6;
        r4 = r4 + r6;
        r0 = r33;
        r0.authorX = r4;	 Catch:{ Exception -> 0x23c5 }
    L_0x0dfa:
        r4 = r7 + r31;
    L_0x0dfc:
        r6 = r7 + r31;
        r23 = java.lang.Math.max(r5, r6);	 Catch:{ Exception -> 0x23c5 }
        r5 = r8 + 1;
        r8 = r5;
        r6 = r4;
        r5 = r23;
        goto L_0x0d95;
    L_0x0e09:
        if (r2 == 0) goto L_0x0e14;
    L_0x0e0b:
        r0 = r33;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x23c5 }
        r4 = r4.getWidth();	 Catch:{ Exception -> 0x23c5 }
        goto L_0x0dc5;
    L_0x0e14:
        r0 = r33;
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x23c5 }
        r4 = r4.getLineWidth(r8);	 Catch:{ Exception -> 0x23c5 }
        r10 = (double) r4;	 Catch:{ Exception -> 0x23c5 }
        r10 = java.lang.Math.ceil(r10);	 Catch:{ Exception -> 0x23c5 }
        r4 = (int) r10;
        goto L_0x0dc5;
    L_0x0e23:
        r6 = r5;
        goto L_0x092d;
    L_0x0e26:
        r5 = r28;
        goto L_0x0955;
    L_0x0e2a:
        r2 = 0;
        goto L_0x097c;
    L_0x0e2d:
        r2 = r4 + 1;
        r4 = r2;
        goto L_0x099f;
    L_0x0e32:
        r2 = com.hanista.mobogram.messenger.MessageObject.isVideoDocument(r20);
        if (r2 == 0) goto L_0x0eb1;
    L_0x0e38:
        r0 = r20;
        r2 = r0.thumb;
        r0 = r33;
        r0.currentPhotoObject = r2;
        r0 = r33;
        r2 = r0.currentPhotoObject;
        if (r2 == 0) goto L_0x0ea2;
    L_0x0e46:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2664w;
        if (r2 == 0) goto L_0x0e56;
    L_0x0e4e:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2663h;
        if (r2 != 0) goto L_0x0ea2;
    L_0x0e56:
        r2 = 0;
        r4 = r2;
    L_0x0e58:
        r0 = r20;
        r2 = r0.attributes;
        r2 = r2.size();
        if (r4 >= r2) goto L_0x0e80;
    L_0x0e62:
        r0 = r20;
        r2 = r0.attributes;
        r2 = r2.get(r4);
        r2 = (com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute) r2;
        r7 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r7 == 0) goto L_0x0ead;
    L_0x0e70:
        r0 = r33;
        r4 = r0.currentPhotoObject;
        r7 = r2.f2659w;
        r4.f2664w = r7;
        r0 = r33;
        r4 = r0.currentPhotoObject;
        r2 = r2.f2658h;
        r4.f2663h = r2;
    L_0x0e80:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2664w;
        if (r2 == 0) goto L_0x0e90;
    L_0x0e88:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2663h;
        if (r2 != 0) goto L_0x0ea2;
    L_0x0e90:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r0 = r33;
        r4 = r0.currentPhotoObject;
        r7 = 1125515264; // 0x43160000 float:150.0 double:5.56078426E-315;
        r7 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r7);
        r4.f2663h = r7;
        r2.f2664w = r7;
    L_0x0ea2:
        r2 = 0;
        r0 = r33;
        r1 = r34;
        r0.createDocumentLayout(r2, r1);
        r4 = r6;
        goto L_0x09f3;
    L_0x0ead:
        r2 = r4 + 1;
        r4 = r2;
        goto L_0x0e58;
    L_0x0eb1:
        r2 = com.hanista.mobogram.messenger.MessageObject.isStickerDocument(r20);
        if (r2 == 0) goto L_0x0f33;
    L_0x0eb7:
        r0 = r20;
        r2 = r0.thumb;
        r0 = r33;
        r0.currentPhotoObject = r2;
        r0 = r33;
        r2 = r0.currentPhotoObject;
        if (r2 == 0) goto L_0x0f21;
    L_0x0ec5:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2664w;
        if (r2 == 0) goto L_0x0ed5;
    L_0x0ecd:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2663h;
        if (r2 != 0) goto L_0x0f21;
    L_0x0ed5:
        r2 = 0;
        r4 = r2;
    L_0x0ed7:
        r0 = r20;
        r2 = r0.attributes;
        r2 = r2.size();
        if (r4 >= r2) goto L_0x0eff;
    L_0x0ee1:
        r0 = r20;
        r2 = r0.attributes;
        r2 = r2.get(r4);
        r2 = (com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute) r2;
        r7 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r7 == 0) goto L_0x0f2f;
    L_0x0eef:
        r0 = r33;
        r4 = r0.currentPhotoObject;
        r7 = r2.f2659w;
        r4.f2664w = r7;
        r0 = r33;
        r4 = r0.currentPhotoObject;
        r2 = r2.f2658h;
        r4.f2663h = r2;
    L_0x0eff:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2664w;
        if (r2 == 0) goto L_0x0f0f;
    L_0x0f07:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2663h;
        if (r2 != 0) goto L_0x0f21;
    L_0x0f0f:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r0 = r33;
        r4 = r0.currentPhotoObject;
        r7 = 1125515264; // 0x43160000 float:150.0 double:5.56078426E-315;
        r7 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r7);
        r4.f2663h = r7;
        r2.f2664w = r7;
    L_0x0f21:
        r0 = r20;
        r1 = r33;
        r1.documentAttach = r0;
        r2 = 6;
        r0 = r33;
        r0.documentAttachType = r2;
        r4 = r6;
        goto L_0x09f3;
    L_0x0f2f:
        r2 = r4 + 1;
        r4 = r2;
        goto L_0x0ed7;
    L_0x0f33:
        r0 = r33;
        r1 = r16;
        r0.calcBackgroundWidth(r15, r1, r6);
        r2 = com.hanista.mobogram.messenger.MessageObject.isStickerDocument(r20);
        if (r2 != 0) goto L_0x1173;
    L_0x0f40:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r4 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r15;
        if (r2 >= r4) goto L_0x0f58;
    L_0x0f4d:
        r2 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r15;
        r0 = r33;
        r0.backgroundWidth = r2;
    L_0x0f58:
        r2 = com.hanista.mobogram.messenger.MessageObject.isVoiceDocument(r20);
        if (r2 == 0) goto L_0x0fae;
    L_0x0f5e:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r4 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 - r4;
        r0 = r33;
        r1 = r34;
        r0.createDocumentLayout(r2, r1);
        r0 = r33;
        r2 = r0.currentMessageObject;
        r2 = r2.textHeight;
        r4 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r0 = r33;
        r4 = r0.linkPreviewHeight;
        r2 = r2 + r4;
        r0 = r33;
        r0.mediaOffsetY = r2;
        r0 = r33;
        r2 = r0.totalHeight;
        r4 = 1110441984; // 0x42300000 float:44.0 double:5.48631236E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r0 = r33;
        r0.totalHeight = r2;
        r0 = r33;
        r2 = r0.linkPreviewHeight;
        r4 = 1110441984; // 0x42300000 float:44.0 double:5.48631236E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r0 = r33;
        r0.linkPreviewHeight = r2;
        r0 = r33;
        r1 = r16;
        r0.calcBackgroundWidth(r15, r1, r6);
        r4 = r6;
        goto L_0x09f3;
    L_0x0fae:
        r2 = com.hanista.mobogram.messenger.MessageObject.isMusicDocument(r20);
        if (r2 == 0) goto L_0x106f;
    L_0x0fb4:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r4 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 - r4;
        r0 = r33;
        r1 = r34;
        r2 = r0.createDocumentLayout(r2, r1);
        r0 = r33;
        r4 = r0.currentMessageObject;
        r4 = r4.textHeight;
        r7 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r7 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 + r7;
        r0 = r33;
        r7 = r0.linkPreviewHeight;
        r4 = r4 + r7;
        r0 = r33;
        r0.mediaOffsetY = r4;
        r0 = r33;
        r4 = r0.totalHeight;
        r7 = 1113587712; // 0x42600000 float:56.0 double:5.50185432E-315;
        r7 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 + r7;
        r0 = r33;
        r0.totalHeight = r4;
        r0 = r33;
        r4 = r0.linkPreviewHeight;
        r7 = 1113587712; // 0x42600000 float:56.0 double:5.50185432E-315;
        r7 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 + r7;
        r0 = r33;
        r0.linkPreviewHeight = r4;
        r4 = 1118568448; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r15 = r15 - r4;
        r2 = r2 + r31;
        r4 = 1119617024; // 0x42bc0000 float:94.0 double:5.53164308E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r2 = java.lang.Math.max(r6, r2);
        r0 = r33;
        r4 = r0.songLayout;
        if (r4 == 0) goto L_0x103a;
    L_0x1015:
        r0 = r33;
        r4 = r0.songLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x103a;
    L_0x101f:
        r2 = (float) r2;
        r0 = r33;
        r4 = r0.songLayout;
        r6 = 0;
        r4 = r4.getLineWidth(r6);
        r0 = r31;
        r6 = (float) r0;
        r4 = r4 + r6;
        r6 = 1118568448; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r6 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r4 = r4 + r6;
        r2 = java.lang.Math.max(r2, r4);
        r2 = (int) r2;
    L_0x103a:
        r0 = r33;
        r4 = r0.performerLayout;
        if (r4 == 0) goto L_0x1065;
    L_0x1040:
        r0 = r33;
        r4 = r0.performerLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x1065;
    L_0x104a:
        r2 = (float) r2;
        r0 = r33;
        r4 = r0.performerLayout;
        r6 = 0;
        r4 = r4.getLineWidth(r6);
        r0 = r31;
        r6 = (float) r0;
        r4 = r4 + r6;
        r6 = 1118568448; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r6 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r4 = r4 + r6;
        r2 = java.lang.Math.max(r2, r4);
        r2 = (int) r2;
    L_0x1065:
        r0 = r33;
        r1 = r16;
        r0.calcBackgroundWidth(r15, r1, r2);
        r4 = r2;
        goto L_0x09f3;
    L_0x106f:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r4 = 1126694912; // 0x43280000 float:168.0 double:5.566612494E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 - r4;
        r0 = r33;
        r1 = r34;
        r0.createDocumentLayout(r2, r1);
        r2 = 1;
        r0 = r33;
        r0.drawImageButton = r2;
        r0 = r33;
        r2 = r0.drawPhotoImage;
        if (r2 == 0) goto L_0x10ca;
    L_0x108c:
        r0 = r33;
        r2 = r0.totalHeight;
        r4 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r0 = r33;
        r0.totalHeight = r2;
        r0 = r33;
        r2 = r0.linkPreviewHeight;
        r4 = 1118568448; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r0 = r33;
        r0.linkPreviewHeight = r2;
        r0 = r33;
        r2 = r0.photoImage;
        r4 = 0;
        r0 = r33;
        r7 = r0.totalHeight;
        r0 = r33;
        r8 = r0.namesOffset;
        r7 = r7 + r8;
        r8 = 1118568448; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r8 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r8);
        r9 = 1118568448; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r9 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r9);
        r2.setImageCoords(r4, r7, r8, r9);
        r4 = r6;
        goto L_0x09f3;
    L_0x10ca:
        r0 = r33;
        r2 = r0.currentMessageObject;
        r2 = r2.textHeight;
        r4 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r0 = r33;
        r4 = r0.linkPreviewHeight;
        r2 = r2 + r4;
        r0 = r33;
        r0.mediaOffsetY = r2;
        r0 = r33;
        r2 = r0.photoImage;
        r4 = 0;
        r0 = r33;
        r7 = r0.totalHeight;
        r0 = r33;
        r8 = r0.namesOffset;
        r7 = r7 + r8;
        r8 = 1096810496; // 0x41600000 float:14.0 double:5.41896386E-315;
        r8 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r8);
        r7 = r7 - r8;
        r8 = 1113587712; // 0x42600000 float:56.0 double:5.50185432E-315;
        r8 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r8);
        r9 = 1113587712; // 0x42600000 float:56.0 double:5.50185432E-315;
        r9 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r9);
        r2.setImageCoords(r4, r7, r8, r9);
        r0 = r33;
        r2 = r0.totalHeight;
        r4 = 1115684864; // 0x42800000 float:64.0 double:5.51221563E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r0 = r33;
        r0.totalHeight = r2;
        r0 = r33;
        r2 = r0.linkPreviewHeight;
        r4 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r0 = r33;
        r0.linkPreviewHeight = r2;
        r4 = r6;
        goto L_0x09f3;
    L_0x1125:
        if (r21 == 0) goto L_0x1173;
    L_0x1127:
        if (r17 == 0) goto L_0x1176;
    L_0x1129:
        r2 = "photo";
        r0 = r17;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x1176;
    L_0x1134:
        r2 = 1;
    L_0x1135:
        r0 = r33;
        r0.drawImageButton = r2;
        r0 = r34;
        r7 = r0.photoThumbs;
        r0 = r33;
        r2 = r0.drawImageButton;
        if (r2 == 0) goto L_0x1178;
    L_0x1143:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getPhotoSize();
    L_0x1147:
        r0 = r33;
        r4 = r0.drawImageButton;
        if (r4 != 0) goto L_0x117a;
    L_0x114d:
        r4 = 1;
    L_0x114e:
        r2 = com.hanista.mobogram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r2, r4);
        r0 = r33;
        r0.currentPhotoObject = r2;
        r0 = r34;
        r2 = r0.photoThumbs;
        r4 = 80;
        r2 = com.hanista.mobogram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r4);
        r0 = r33;
        r0.currentPhotoObjectThumb = r2;
        r0 = r33;
        r2 = r0.currentPhotoObjectThumb;
        r0 = r33;
        r4 = r0.currentPhotoObject;
        if (r2 != r4) goto L_0x1173;
    L_0x116e:
        r2 = 0;
        r0 = r33;
        r0.currentPhotoObjectThumb = r2;
    L_0x1173:
        r4 = r6;
        goto L_0x09f3;
    L_0x1176:
        r2 = 0;
        goto L_0x1135;
    L_0x1178:
        r2 = r5;
        goto L_0x1147;
    L_0x117a:
        r4 = 0;
        goto L_0x114e;
    L_0x117c:
        r2 = 0;
        goto L_0x0a40;
    L_0x117f:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r2 = r2.x;
        r2 = (float) r2;
        r5 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r2 = r2 * r5;
        r5 = (int) r2;
        goto L_0x0a7e;
    L_0x118a:
        r0 = r33;
        r2 = r0.hasGamePreview;
        if (r2 == 0) goto L_0x11a8;
    L_0x1190:
        r2 = 640; // 0x280 float:8.97E-43 double:3.16E-321;
        r3 = 360; // 0x168 float:5.04E-43 double:1.78E-321;
        r4 = (float) r2;
        r6 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r5 = (float) r5;
        r4 = r4 / r5;
        r2 = (float) r2;
        r2 = r2 / r4;
        r2 = (int) r2;
        r3 = (float) r3;
        r3 = r3 / r4;
        r5 = (int) r3;
        r6 = r5;
        r7 = r2;
        goto L_0x0a9c;
    L_0x11a8:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2664w;
        r0 = r33;
        r4 = r0.currentPhotoObject;
        r4 = r4.f2663h;
        r6 = (float) r2;
        r7 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r7 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r7);
        r5 = r5 - r7;
        r5 = (float) r5;
        r5 = r6 / r5;
        r2 = (float) r2;
        r2 = r2 / r5;
        r2 = (int) r2;
        r4 = (float) r4;
        r4 = r4 / r5;
        r5 = (int) r4;
        if (r3 == 0) goto L_0x11dc;
    L_0x11c7:
        if (r3 == 0) goto L_0x2428;
    L_0x11c9:
        r3 = r3.toLowerCase();
        r4 = "instagram";
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x2428;
    L_0x11d6:
        r0 = r33;
        r3 = r0.documentAttachType;
        if (r3 != 0) goto L_0x2428;
    L_0x11dc:
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r3.y;
        r3 = r3 / 3;
        if (r5 <= r3) goto L_0x2428;
    L_0x11e4:
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r3.y;
        r5 = r3 / 3;
        r6 = r5;
        r7 = r2;
        goto L_0x0a9c;
    L_0x11ee:
        r0 = r33;
        r2 = r0.totalHeight;
        r3 = 1094713344; // 0x41400000 float:12.0 double:5.408602553E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r6;
        r2 = r2 + r3;
        r0 = r33;
        r0.totalHeight = r2;
        r0 = r33;
        r2 = r0.linkPreviewHeight;
        r2 = r2 + r6;
        r0 = r33;
        r0.linkPreviewHeight = r2;
        goto L_0x0ae8;
    L_0x1209:
        r7 = 0;
        goto L_0x0b4e;
    L_0x120c:
        r0 = r33;
        r2 = r0.documentAttachType;
        r3 = 4;
        if (r2 != r3) goto L_0x122b;
    L_0x1213:
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 0;
        r4 = 0;
        r0 = r33;
        r5 = r0.currentPhotoObject;
        r5 = r5.location;
        r0 = r33;
        r6 = r0.currentPhotoFilter;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x0b5e;
    L_0x122b:
        r0 = r33;
        r2 = r0.documentAttachType;
        r3 = 2;
        if (r2 != r3) goto L_0x129c;
    L_0x1232:
        r0 = r34;
        r2 = r0.mediaExists;
        r3 = com.hanista.mobogram.messenger.FileLoader.getAttachFileName(r20);
        r0 = r33;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x125e;
    L_0x1240:
        if (r2 != 0) goto L_0x125e;
    L_0x1242:
        r2 = com.hanista.mobogram.messenger.MediaController.m71a();
        r4 = 32;
        r2 = r2.m157a(r4);
        if (r2 != 0) goto L_0x125e;
    L_0x124e:
        r2 = com.hanista.mobogram.messenger.FileLoader.getInstance();
        r2 = r2.isLoadingFile(r3);
        if (r2 != 0) goto L_0x125e;
    L_0x1258:
        r2 = r33.isFavoriteAndAutoDownload();
        if (r2 == 0) goto L_0x127f;
    L_0x125e:
        r2 = 0;
        r0 = r33;
        r0.photoNotSet = r2;
        r0 = r33;
        r2 = r0.photoImage;
        r4 = 0;
        r0 = r33;
        r3 = r0.currentPhotoObject;
        r5 = r3.location;
        r0 = r33;
        r6 = r0.currentPhotoFilter;
        r0 = r20;
        r7 = r0.size;
        r8 = 0;
        r9 = 0;
        r3 = r20;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x0b5e;
    L_0x127f:
        r2 = 1;
        r0 = r33;
        r0.photoNotSet = r2;
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 0;
        r4 = 0;
        r0 = r33;
        r5 = r0.currentPhotoObject;
        r5 = r5.location;
        r0 = r33;
        r6 = r0.currentPhotoFilter;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x0b5e;
    L_0x129c:
        r0 = r34;
        r2 = r0.mediaExists;
        r0 = r33;
        r3 = r0.currentPhotoObject;
        r3 = com.hanista.mobogram.messenger.FileLoader.getAttachFileName(r3);
        r0 = r33;
        r4 = r0.hasGamePreview;
        if (r4 != 0) goto L_0x12cb;
    L_0x12ae:
        if (r2 != 0) goto L_0x12cb;
    L_0x12b0:
        r2 = com.hanista.mobogram.messenger.MediaController.m71a();
        r4 = 1;
        r2 = r2.m157a(r4);
        if (r2 != 0) goto L_0x12cb;
    L_0x12bb:
        r2 = com.hanista.mobogram.messenger.FileLoader.getInstance();
        r2 = r2.isLoadingFile(r3);
        if (r2 != 0) goto L_0x12cb;
    L_0x12c5:
        r2 = r33.isFavoriteAndAutoDownload();
        if (r2 == 0) goto L_0x12f8;
    L_0x12cb:
        r2 = 0;
        r0 = r33;
        r0.photoNotSet = r2;
        r0 = r33;
        r2 = r0.photoImage;
        r0 = r33;
        r3 = r0.currentPhotoObject;
        r3 = r3.location;
        r0 = r33;
        r4 = r0.currentPhotoFilter;
        r0 = r33;
        r5 = r0.currentPhotoObjectThumb;
        if (r5 == 0) goto L_0x12f6;
    L_0x12e4:
        r0 = r33;
        r5 = r0.currentPhotoObjectThumb;
        r5 = r5.location;
    L_0x12ea:
        r0 = r33;
        r6 = r0.currentPhotoFilterThumb;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x0b5e;
    L_0x12f6:
        r5 = 0;
        goto L_0x12ea;
    L_0x12f8:
        r2 = 1;
        r0 = r33;
        r0.photoNotSet = r2;
        r0 = r33;
        r2 = r0.currentPhotoObjectThumb;
        if (r2 == 0) goto L_0x1331;
    L_0x1303:
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 0;
        r4 = 0;
        r0 = r33;
        r5 = r0.currentPhotoObjectThumb;
        r5 = r5.location;
        r8 = java.util.Locale.US;
        r9 = "%d_%d_b";
        r10 = 2;
        r10 = new java.lang.Object[r10];
        r11 = 0;
        r7 = java.lang.Integer.valueOf(r7);
        r10[r11] = r7;
        r7 = 1;
        r6 = java.lang.Integer.valueOf(r6);
        r10[r7] = r6;
        r6 = java.lang.String.format(r8, r9, r10);
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x0b5e;
    L_0x1331:
        r0 = r33;
        r3 = r0.photoImage;
        r2 = 0;
        r2 = (android.graphics.drawable.Drawable) r2;
        r3.setImageBitmap(r2);
        goto L_0x0b5e;
    L_0x133d:
        r0 = r33;
        r2 = r0.hasGamePreview;
        if (r2 == 0) goto L_0x0bb5;
    L_0x1343:
        r2 = "AttachGame";
        r3 = 2131165338; // 0x7f07009a float:1.794489E38 double:1.052935579E-314;
        r2 = com.hanista.mobogram.messenger.LocaleController.getString(r2, r3);
        r3 = r2.toUpperCase();
        r2 = gamePaint;
        r2 = r2.measureText(r3);
        r4 = (double) r2;
        r4 = java.lang.Math.ceil(r4);
        r2 = (int) r4;
        r0 = r33;
        r0.durationWidth = r2;
        r2 = new android.text.StaticLayout;
        r4 = gamePaint;
        r0 = r33;
        r5 = r0.durationWidth;
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);
        r0 = r33;
        r0.videoInfoLayout = r2;
        goto L_0x0bb5;
    L_0x1378:
        r0 = r33;
        r3 = r0.photoImage;
        r2 = 0;
        r2 = (android.graphics.drawable.Drawable) r2;
        r3.setImageBitmap(r2);
        r0 = r33;
        r2 = r0.linkPreviewHeight;
        r3 = 1086324736; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r0 = r33;
        r0.linkPreviewHeight = r2;
        r0 = r33;
        r2 = r0.totalHeight;
        r3 = 1082130432; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.totalHeight = r2;
        goto L_0x0bb6;
    L_0x13a2:
        r0 = r33;
        r3 = r0.photoImage;
        r2 = 0;
        r2 = (android.graphics.drawable.Drawable) r2;
        r3.setImageBitmap(r2);
        r0 = r33;
        r1 = r16;
        r0.calcBackgroundWidth(r15, r1, r13);
        goto L_0x0bec;
    L_0x13b5:
        r0 = r34;
        r2 = r0.type;
        r3 = 12;
        if (r2 != r3) goto L_0x15d7;
    L_0x13bd:
        r2 = 0;
        r0 = r33;
        r0.drawName = r2;
        r2 = 1;
        r0 = r33;
        r0.drawForwardedName = r2;
        r2 = 1;
        r0 = r33;
        r0.drawPhotoImage = r2;
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 1102053376; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2.setRoundRadius(r3);
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x156b;
    L_0x13df:
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x1567;
    L_0x13e9:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x1567;
    L_0x13ef:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x1567;
    L_0x13f5:
        r2 = 1120665600; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x13f7:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r3 - r2;
        r3 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = java.lang.Math.min(r2, r3);
        r0 = r33;
        r0.backgroundWidth = r2;
    L_0x140b:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r3 = 1106771968; // 0x41f80000 float:31.0 double:5.46818007E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r0 = r33;
        r0.availableTimeWidth = r2;
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r4 = r2.user_id;
        r2 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r3 = java.lang.Integer.valueOf(r4);
        r5 = r2.getUser(r3);
        r2 = r33.getMaxNameWidth();
        r3 = 1121714176; // 0x42dc0000 float:110.0 double:5.54200439E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        if (r2 >= 0) goto L_0x2425;
    L_0x143b:
        r2 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r11 = r2;
    L_0x1442:
        r2 = 0;
        if (r5 == 0) goto L_0x2422;
    L_0x1445:
        r3 = r5.photo;
        if (r3 == 0) goto L_0x144d;
    L_0x1449:
        r2 = r5.photo;
        r2 = r2.photo_small;
    L_0x144d:
        r0 = r33;
        r3 = r0.contactAvatarDrawable;
        r3.setInfo(r5);
        r3 = r2;
    L_0x1455:
        if (r4 != 0) goto L_0x1466;
    L_0x1457:
        r2 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
        if (r2 == 0) goto L_0x1466;
    L_0x145d:
        r0 = r33;
        r2 = r0.avatarDrawable;
        r4 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.bA;
        r2.setColor(r4);
    L_0x1466:
        r0 = r33;
        r2 = r0.photoImage;
        r4 = "50_50";
        if (r5 == 0) goto L_0x159c;
    L_0x146f:
        r0 = r33;
        r5 = r0.contactAvatarDrawable;
    L_0x1473:
        r6 = 0;
        r7 = 0;
        r2.setImage(r3, r4, r5, r6, r7);
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.phone_number;
        if (r2 == 0) goto L_0x15ab;
    L_0x1482:
        r3 = r2.length();
        if (r3 == 0) goto L_0x15ab;
    L_0x1488:
        r3 = com.hanista.mobogram.PhoneFormat.PhoneFormat.getInstance();
        r2 = r3.format(r2);
        r10 = r2;
    L_0x1491:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.first_name;
        r0 = r34;
        r3 = r0.messageOwner;
        r3 = r3.media;
        r3 = r3.last_name;
        r2 = com.hanista.mobogram.messenger.ContactsController.formatName(r2, r3);
        r3 = 10;
        r4 = 32;
        r2 = r2.replace(r3, r4);
        r3 = r2.length();
        if (r3 != 0) goto L_0x241f;
    L_0x14b3:
        r3 = r10;
    L_0x14b4:
        r2 = new android.text.StaticLayout;
        r4 = contactNamePaint;
        r5 = (float) r11;
        r6 = android.text.TextUtils.TruncateAt.END;
        r3 = android.text.TextUtils.ellipsize(r3, r4, r5, r6);
        r4 = contactNamePaint;
        r5 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);
        r5 = r5 + r11;
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);
        r0 = r33;
        r0.titleLayout = r2;
        r2 = new android.text.StaticLayout;
        r3 = 10;
        r4 = 32;
        r3 = r10.replace(r3, r4);
        r4 = contactPhonePaint;
        r5 = (float) r11;
        r6 = android.text.TextUtils.TruncateAt.END;
        r3 = android.text.TextUtils.ellipsize(r3, r4, r5, r6);
        r4 = contactPhonePaint;
        r5 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);
        r5 = r5 + r11;
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);
        r0 = r33;
        r0.docTitleLayout = r2;
        r33.setMessageObjectInternal(r34);
        r0 = r33;
        r2 = r0.drawForwardedName;
        if (r2 == 0) goto L_0x15b8;
    L_0x1507:
        r2 = r34.isForwarded();
        if (r2 == 0) goto L_0x15b8;
    L_0x150d:
        r0 = r33;
        r2 = r0.namesOffset;
        r3 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.namesOffset = r2;
    L_0x151c:
        r2 = 1116471296; // 0x428c0000 float:70.0 double:5.51610112E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r0 = r33;
        r3 = r0.namesOffset;
        r2 = r2 + r3;
        r0 = r33;
        r0.totalHeight = r2;
        r0 = r33;
        r2 = r0.docTitleLayout;
        r2 = r2.getLineCount();
        if (r2 <= 0) goto L_0x0bec;
    L_0x1535:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r3 = 1121714176; // 0x42dc0000 float:110.0 double:5.54200439E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r0 = r33;
        r3 = r0.docTitleLayout;
        r4 = 0;
        r3 = r3.getLineWidth(r4);
        r4 = (double) r3;
        r4 = java.lang.Math.ceil(r4);
        r3 = (int) r4;
        r2 = r2 - r3;
        r0 = r33;
        r3 = r0.timeWidth;
        if (r2 >= r3) goto L_0x0bec;
    L_0x1556:
        r0 = r33;
        r2 = r0.totalHeight;
        r3 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.totalHeight = r2;
        goto L_0x0bec;
    L_0x1567:
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x13f7;
    L_0x156b:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r2.x;
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x1599;
    L_0x1575:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x1599;
    L_0x157b:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x1599;
    L_0x1581:
        r2 = 1120665600; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x1583:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r3 - r2;
        r3 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = java.lang.Math.min(r2, r3);
        r0 = r33;
        r0.backgroundWidth = r2;
        goto L_0x140b;
    L_0x1599:
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x1583;
    L_0x159c:
        r6 = com.hanista.mobogram.ui.ActionBar.Theme.contactDrawable;
        r5 = r34.isOutOwner();
        if (r5 == 0) goto L_0x15a9;
    L_0x15a4:
        r5 = 1;
    L_0x15a5:
        r5 = r6[r5];
        goto L_0x1473;
    L_0x15a9:
        r5 = 0;
        goto L_0x15a5;
    L_0x15ab:
        r2 = "NumberUnknown";
        r3 = 2131166045; // 0x7f07035d float:1.7946324E38 double:1.0529359284E-314;
        r2 = com.hanista.mobogram.messenger.LocaleController.getString(r2, r3);
        r10 = r2;
        goto L_0x1491;
    L_0x15b8:
        r0 = r33;
        r2 = r0.drawNameLayout;
        if (r2 == 0) goto L_0x151c;
    L_0x15be:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.reply_to_msg_id;
        if (r2 != 0) goto L_0x151c;
    L_0x15c6:
        r0 = r33;
        r2 = r0.namesOffset;
        r3 = 1088421888; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.namesOffset = r2;
        goto L_0x151c;
    L_0x15d7:
        r0 = r34;
        r2 = r0.type;
        r3 = 2;
        if (r2 != r3) goto L_0x1667;
    L_0x15de:
        r2 = 1;
        r0 = r33;
        r0.drawForwardedName = r2;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x1637;
    L_0x15e9:
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x1634;
    L_0x15f3:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x1634;
    L_0x15f9:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x1634;
    L_0x15ff:
        r2 = 1120665600; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x1601:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r3 - r2;
        r3 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = java.lang.Math.min(r2, r3);
        r0 = r33;
        r0.backgroundWidth = r2;
    L_0x1615:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r0 = r33;
        r1 = r34;
        r0.createDocumentLayout(r2, r1);
        r33.setMessageObjectInternal(r34);
        r2 = 1116471296; // 0x428c0000 float:70.0 double:5.51610112E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r0 = r33;
        r3 = r0.namesOffset;
        r2 = r2 + r3;
        r0 = r33;
        r0.totalHeight = r2;
        goto L_0x0bec;
    L_0x1634:
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x1601;
    L_0x1637:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r2.x;
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x1664;
    L_0x1641:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x1664;
    L_0x1647:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x1664;
    L_0x164d:
        r2 = 1120665600; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x164f:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r3 - r2;
        r3 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = java.lang.Math.min(r2, r3);
        r0 = r33;
        r0.backgroundWidth = r2;
        goto L_0x1615;
    L_0x1664:
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x164f;
    L_0x1667:
        r0 = r34;
        r2 = r0.type;
        r3 = 14;
        if (r2 != r3) goto L_0x16f3;
    L_0x166f:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x16c3;
    L_0x1675:
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x16c0;
    L_0x167f:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x16c0;
    L_0x1685:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x16c0;
    L_0x168b:
        r2 = 1120665600; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x168d:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r3 - r2;
        r3 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = java.lang.Math.min(r2, r3);
        r0 = r33;
        r0.backgroundWidth = r2;
    L_0x16a1:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r0 = r33;
        r1 = r34;
        r0.createDocumentLayout(r2, r1);
        r33.setMessageObjectInternal(r34);
        r2 = 1118044160; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r0 = r33;
        r3 = r0.namesOffset;
        r2 = r2 + r3;
        r0 = r33;
        r0.totalHeight = r2;
        goto L_0x0bec;
    L_0x16c0:
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x168d;
    L_0x16c3:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r2.x;
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x16f0;
    L_0x16cd:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x16f0;
    L_0x16d3:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x16f0;
    L_0x16d9:
        r2 = 1120665600; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x16db:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r3 - r2;
        r3 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = java.lang.Math.min(r2, r3);
        r0 = r33;
        r0.backgroundWidth = r2;
        goto L_0x16a1;
    L_0x16f0:
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x16db;
    L_0x16f3:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.fwd_from;
        if (r2 == 0) goto L_0x1870;
    L_0x16fb:
        r0 = r34;
        r2 = r0.type;
        r3 = 13;
        if (r2 == r3) goto L_0x1870;
    L_0x1703:
        r2 = 1;
    L_0x1704:
        r0 = r33;
        r0.drawForwardedName = r2;
        r0 = r34;
        r2 = r0.type;
        r3 = 9;
        if (r2 == r3) goto L_0x1873;
    L_0x1710:
        r2 = 1;
    L_0x1711:
        r0 = r33;
        r0.mediaBackground = r2;
        r2 = 1;
        r0 = r33;
        r0.drawImageButton = r2;
        r2 = 1;
        r0 = r33;
        r0.drawPhotoImage = r2;
        r5 = 0;
        r4 = 0;
        r15 = 0;
        r0 = r34;
        r2 = r0.audioProgress;
        r3 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 == 0) goto L_0x1744;
    L_0x172c:
        r2 = com.hanista.mobogram.messenger.MediaController.m71a();
        r2 = r2.m140B();
        if (r2 != 0) goto L_0x1744;
    L_0x1736:
        r0 = r34;
        r2 = r0.type;
        r3 = 8;
        if (r2 != r3) goto L_0x1744;
    L_0x173e:
        r2 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r34;
        r0.audioProgress = r2;
    L_0x1744:
        r0 = r33;
        r3 = r0.photoImage;
        r0 = r34;
        r2 = r0.audioProgress;
        r6 = 0;
        r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r2 != 0) goto L_0x1876;
    L_0x1751:
        r2 = 1;
    L_0x1752:
        r3.setAllowStartAnimation(r2);
        r0 = r33;
        r2 = r0.photoImage;
        r3 = r34.isSecretPhoto();
        r2.setForcePreview(r3);
        r0 = r34;
        r2 = r0.type;
        r3 = 9;
        if (r2 != r3) goto L_0x18d2;
    L_0x1768:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x187d;
    L_0x176e:
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x1879;
    L_0x1778:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x1879;
    L_0x177e:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x1879;
    L_0x1784:
        r2 = 1120665600; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x1786:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r3 - r2;
        r3 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = java.lang.Math.min(r2, r3);
        r0 = r33;
        r0.backgroundWidth = r2;
    L_0x179a:
        r2 = r33.checkNeedDrawShareButton(r34);
        if (r2 == 0) goto L_0x17af;
    L_0x17a0:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r3 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r0 = r33;
        r0.backgroundWidth = r2;
    L_0x17af:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r3 = 1124728832; // 0x430a0000 float:138.0 double:5.55689877E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r0 = r33;
        r1 = r34;
        r0.createDocumentLayout(r2, r1);
        r0 = r34;
        r3 = r0.caption;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x17d2;
    L_0x17cb:
        r3 = 1118568448; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
    L_0x17d2:
        r0 = r33;
        r3 = r0.drawPhotoImage;
        if (r3 == 0) goto L_0x18ae;
    L_0x17d8:
        r3 = 1118568448; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r3 = 1118568448; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
    L_0x17e4:
        r0 = r33;
        r0.availableTimeWidth = r2;
        r0 = r33;
        r2 = r0.drawPhotoImage;
        if (r2 != 0) goto L_0x182d;
    L_0x17ee:
        r0 = r34;
        r2 = r0.caption;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 == 0) goto L_0x182d;
    L_0x17f8:
        r0 = r33;
        r2 = r0.infoLayout;
        r2 = r2.getLineCount();
        if (r2 <= 0) goto L_0x182d;
    L_0x1802:
        r33.measureTime(r34);
        r0 = r33;
        r2 = r0.backgroundWidth;
        r5 = 1123287040; // 0x42f40000 float:122.0 double:5.54977537E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 - r5;
        r0 = r33;
        r5 = r0.infoLayout;
        r6 = 0;
        r5 = r5.getLineWidth(r6);
        r6 = (double) r5;
        r6 = java.lang.Math.ceil(r6);
        r5 = (int) r6;
        r2 = r2 - r5;
        r0 = r33;
        r5 = r0.timeWidth;
        if (r2 >= r5) goto L_0x182d;
    L_0x1826:
        r2 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r3 = r3 + r2;
    L_0x182d:
        r12 = r3;
        r13 = r4;
    L_0x182f:
        r33.setMessageObjectInternal(r34);
        r0 = r33;
        r2 = r0.drawForwardedName;
        if (r2 == 0) goto L_0x2237;
    L_0x1838:
        r0 = r33;
        r2 = r0.namesOffset;
        r3 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.namesOffset = r2;
    L_0x1847:
        r33.invalidate();
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 0;
        r4 = 1088421888; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r0 = r33;
        r5 = r0.namesOffset;
        r4 = r4 + r5;
        r2.setImageCoords(r3, r4, r13, r12);
        r2 = 1096810496; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r12;
        r0 = r33;
        r3 = r0.namesOffset;
        r2 = r2 + r3;
        r2 = r2 + r15;
        r0 = r33;
        r0.totalHeight = r2;
        goto L_0x0bec;
    L_0x1870:
        r2 = 0;
        goto L_0x1704;
    L_0x1873:
        r2 = 0;
        goto L_0x1711;
    L_0x1876:
        r2 = 0;
        goto L_0x1752;
    L_0x1879:
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x1786;
    L_0x187d:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r2.x;
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x18ab;
    L_0x1887:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x18ab;
    L_0x188d:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x18ab;
    L_0x1893:
        r2 = 1120665600; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x1895:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r3 - r2;
        r3 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = java.lang.Math.min(r2, r3);
        r0 = r33;
        r0.backgroundWidth = r2;
        goto L_0x179a;
    L_0x18ab:
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x1895;
    L_0x18ae:
        r3 = 1113587712; // 0x42600000 float:56.0 double:5.50185432E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r3 = 1113587712; // 0x42600000 float:56.0 double:5.50185432E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r0 = r34;
        r3 = r0.caption;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 == 0) goto L_0x18cf;
    L_0x18c4:
        r3 = 1112276992; // 0x424c0000 float:51.0 double:5.495378504E-315;
    L_0x18c6:
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r3 = r4;
        r4 = r5;
        goto L_0x17e4;
    L_0x18cf:
        r3 = 1101529088; // 0x41a80000 float:21.0 double:5.442276803E-315;
        goto L_0x18c6;
    L_0x18d2:
        r0 = r34;
        r2 = r0.type;
        r3 = 4;
        if (r2 != r3) goto L_0x1ace;
    L_0x18d9:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.geo;
        r12 = r2.lat;
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.geo;
        r0 = r2._long;
        r16 = r0;
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.title;
        if (r2 == 0) goto L_0x1a65;
    L_0x18f9:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.title;
        r2 = r2.length();
        if (r2 <= 0) goto L_0x1a65;
    L_0x1907:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x1a2d;
    L_0x190d:
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x1a29;
    L_0x1917:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x1a29;
    L_0x191d:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x1a29;
    L_0x1923:
        r2 = 1120665600; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x1925:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r3 - r2;
        r3 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = java.lang.Math.min(r2, r3);
        r0 = r33;
        r0.backgroundWidth = r2;
    L_0x1939:
        r2 = r33.checkNeedDrawShareButton(r34);
        if (r2 == 0) goto L_0x194e;
    L_0x193f:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r3 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r0 = r33;
        r0.backgroundWidth = r2;
    L_0x194e:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r3 = 1123418112; // 0x42f60000 float:123.0 double:5.55042295E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r4 = r2 - r3;
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.title;
        r3 = locationTitlePaint;
        r5 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r6 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r7 = 0;
        r8 = 0;
        r9 = android.text.TextUtils.TruncateAt.END;
        r11 = 2;
        r10 = r4;
        r2 = com.hanista.mobogram.ui.Components.StaticLayoutEx.createStaticLayout(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        r0 = r33;
        r0.docTitleLayout = r2;
        r0 = r33;
        r2 = r0.docTitleLayout;
        r10 = r2.getLineCount();
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.address;
        if (r2 == 0) goto L_0x1a5e;
    L_0x1988:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.address;
        r2 = r2.length();
        if (r2 <= 0) goto L_0x1a5e;
    L_0x1996:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.address;
        r3 = locationAddressPaint;
        r5 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r6 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r7 = 0;
        r8 = 0;
        r9 = android.text.TextUtils.TruncateAt.END;
        r11 = 3;
        r10 = 3 - r10;
        r11 = java.lang.Math.min(r11, r10);
        r10 = r4;
        r2 = com.hanista.mobogram.ui.Components.StaticLayoutEx.createStaticLayout(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        r0 = r33;
        r0.infoLayout = r2;
    L_0x19b8:
        r2 = 0;
        r0 = r33;
        r0.mediaBackground = r2;
        r0 = r33;
        r0.availableTimeWidth = r4;
        r2 = 1118568448; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = 1118568448; // 0x42ac0000 float:86.0 double:5.526462427E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r4 = java.util.Locale.US;
        r5 = "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false";
        r6 = 5;
        r6 = new java.lang.Object[r6];
        r7 = 0;
        r8 = java.lang.Double.valueOf(r12);
        r6[r7] = r8;
        r7 = 1;
        r8 = java.lang.Double.valueOf(r16);
        r6[r7] = r8;
        r7 = 2;
        r8 = 2;
        r9 = com.hanista.mobogram.messenger.AndroidUtilities.density;
        r10 = (double) r9;
        r10 = java.lang.Math.ceil(r10);
        r9 = (int) r10;
        r8 = java.lang.Math.min(r8, r9);
        r8 = java.lang.Integer.valueOf(r8);
        r6[r7] = r8;
        r7 = 3;
        r8 = java.lang.Double.valueOf(r12);
        r6[r7] = r8;
        r7 = 4;
        r8 = java.lang.Double.valueOf(r16);
        r6[r7] = r8;
        r4 = java.lang.String.format(r4, r5, r6);
        r0 = r33;
        r0.currentUrl = r4;
        r8 = r2;
        r9 = r3;
    L_0x1a0f:
        r0 = r33;
        r2 = r0.photoImage;
        r0 = r33;
        r3 = r0.currentUrl;
        r4 = 0;
        r5 = r34.isOutOwner();
        if (r5 == 0) goto L_0x1aca;
    L_0x1a1e:
        r5 = com.hanista.mobogram.ui.ActionBar.Theme.geoOutDrawable;
    L_0x1a20:
        r6 = 0;
        r7 = 0;
        r2.setImage(r3, r4, r5, r6, r7);
        r12 = r8;
        r13 = r9;
        goto L_0x182f;
    L_0x1a29:
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x1925;
    L_0x1a2d:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r2.x;
        r0 = r33;
        r2 = r0.isChat;
        if (r2 == 0) goto L_0x1a5b;
    L_0x1a37:
        r2 = r34.isFromUser();
        if (r2 == 0) goto L_0x1a5b;
    L_0x1a3d:
        r2 = r34.isOutOwner();
        if (r2 != 0) goto L_0x1a5b;
    L_0x1a43:
        r2 = 1120665600; // 0x42cc0000 float:102.0 double:5.536823734E-315;
    L_0x1a45:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r3 - r2;
        r3 = 1132920832; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = java.lang.Math.min(r2, r3);
        r0 = r33;
        r0.backgroundWidth = r2;
        goto L_0x1939;
    L_0x1a5b:
        r2 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        goto L_0x1a45;
    L_0x1a5e:
        r2 = 0;
        r0 = r33;
        r0.infoLayout = r2;
        goto L_0x19b8;
    L_0x1a65:
        r2 = 1127874560; // 0x433a0000 float:186.0 double:5.57244073E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r0 = r33;
        r0.availableTimeWidth = r2;
        r2 = 1128792064; // 0x43480000 float:200.0 double:5.5769738E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r4 = 1094713344; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r3;
        r0 = r33;
        r0.backgroundWidth = r4;
        r4 = java.util.Locale.US;
        r5 = "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=200x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false";
        r6 = 5;
        r6 = new java.lang.Object[r6];
        r7 = 0;
        r8 = java.lang.Double.valueOf(r12);
        r6[r7] = r8;
        r7 = 1;
        r8 = java.lang.Double.valueOf(r16);
        r6[r7] = r8;
        r7 = 2;
        r8 = 2;
        r9 = com.hanista.mobogram.messenger.AndroidUtilities.density;
        r10 = (double) r9;
        r10 = java.lang.Math.ceil(r10);
        r9 = (int) r10;
        r8 = java.lang.Math.min(r8, r9);
        r8 = java.lang.Integer.valueOf(r8);
        r6[r7] = r8;
        r7 = 3;
        r8 = java.lang.Double.valueOf(r12);
        r6[r7] = r8;
        r7 = 4;
        r8 = java.lang.Double.valueOf(r16);
        r6[r7] = r8;
        r4 = java.lang.String.format(r4, r5, r6);
        r0 = r33;
        r0.currentUrl = r4;
        r8 = r2;
        r9 = r3;
        goto L_0x1a0f;
    L_0x1aca:
        r5 = com.hanista.mobogram.ui.ActionBar.Theme.geoInDrawable;
        goto L_0x1a20;
    L_0x1ace:
        r0 = r34;
        r2 = r0.type;
        r3 = 13;
        if (r2 != r3) goto L_0x1c36;
    L_0x1ad6:
        r2 = 0;
        r0 = r33;
        r0.drawBackground = r2;
        r2 = 0;
        r3 = r2;
    L_0x1add:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.size();
        if (r3 >= r2) goto L_0x241b;
    L_0x1aed:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.get(r3);
        r2 = (com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute) r2;
        r6 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r6 == 0) goto L_0x1bc0;
    L_0x1b01:
        r3 = r2.f2659w;
        r2 = r2.f2658h;
    L_0x1b05:
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x1bc5;
    L_0x1b0b:
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = (float) r4;
        r5 = 1053609165; // 0x3ecccccd float:0.4 double:5.205520926E-315;
        r4 = r4 * r5;
        r5 = r4;
    L_0x1b15:
        if (r3 != 0) goto L_0x1b1f;
    L_0x1b17:
        r2 = (int) r5;
        r3 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r2;
    L_0x1b1f:
        r6 = com.hanista.mobogram.mobo.MoboConstants.ad;
        if (r6 != 0) goto L_0x1b2a;
    L_0x1b23:
        r2 = (float) r2;
        r3 = (float) r3;
        r3 = r4 / r3;
        r2 = r2 * r3;
        r2 = (int) r2;
        r3 = (int) r4;
    L_0x1b2a:
        r6 = (float) r2;
        r6 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1));
        if (r6 <= 0) goto L_0x1b36;
    L_0x1b2f:
        r3 = (float) r3;
        r2 = (float) r2;
        r2 = r5 / r2;
        r2 = r2 * r3;
        r3 = (int) r2;
        r2 = (int) r5;
    L_0x1b36:
        r5 = com.hanista.mobogram.mobo.MoboConstants.ad;
        if (r5 == 0) goto L_0x2417;
    L_0x1b3a:
        r5 = (float) r3;
        r5 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1));
        if (r5 <= 0) goto L_0x2417;
    L_0x1b3f:
        r2 = (float) r2;
        r3 = (float) r3;
        r3 = r4 / r3;
        r2 = r2 * r3;
        r2 = (int) r2;
        r3 = (int) r4;
        r12 = r2;
        r13 = r3;
    L_0x1b48:
        r2 = 6;
        r0 = r33;
        r0.documentAttachType = r2;
        r2 = 1096810496; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r13 - r2;
        r0 = r33;
        r0.availableTimeWidth = r2;
        r2 = 1094713344; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r13;
        r0 = r33;
        r0.backgroundWidth = r2;
        r0 = r34;
        r2 = r0.photoThumbs;
        r3 = 80;
        r2 = com.hanista.mobogram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3);
        r0 = r33;
        r0.currentPhotoObjectThumb = r2;
        r0 = r34;
        r2 = r0.attachPathExists;
        if (r2 == 0) goto L_0x1bda;
    L_0x1b78:
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 0;
        r0 = r34;
        r4 = r0.messageOwner;
        r4 = r4.attachPath;
        r5 = java.util.Locale.US;
        r6 = "%d_%d";
        r7 = 2;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r9 = java.lang.Integer.valueOf(r13);
        r7[r8] = r9;
        r8 = 1;
        r9 = java.lang.Integer.valueOf(r12);
        r7[r8] = r9;
        r5 = java.lang.String.format(r5, r6, r7);
        r6 = 0;
        r0 = r33;
        r7 = r0.currentPhotoObjectThumb;
        if (r7 == 0) goto L_0x1bd8;
    L_0x1ba4:
        r0 = r33;
        r7 = r0.currentPhotoObjectThumb;
        r7 = r7.location;
    L_0x1baa:
        r8 = "b1";
        r0 = r34;
        r9 = r0.messageOwner;
        r9 = r9.media;
        r9 = r9.document;
        r9 = r9.size;
        r10 = "webp";
        r11 = 1;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x182f;
    L_0x1bc0:
        r2 = r3 + 1;
        r3 = r2;
        goto L_0x1add;
    L_0x1bc5:
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r5 = r5.y;
        r4 = java.lang.Math.min(r4, r5);
        r4 = (float) r4;
        r5 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r4 = r4 * r5;
        r5 = r4;
        goto L_0x1b15;
    L_0x1bd8:
        r7 = 0;
        goto L_0x1baa;
    L_0x1bda:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.document;
        r2 = r2.id;
        r4 = 0;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 == 0) goto L_0x182f;
    L_0x1bea:
        r0 = r33;
        r2 = r0.photoImage;
        r0 = r34;
        r3 = r0.messageOwner;
        r3 = r3.media;
        r3 = r3.document;
        r4 = 0;
        r5 = java.util.Locale.US;
        r6 = "%d_%d";
        r7 = 2;
        r7 = new java.lang.Object[r7];
        r8 = 0;
        r9 = java.lang.Integer.valueOf(r13);
        r7[r8] = r9;
        r8 = 1;
        r9 = java.lang.Integer.valueOf(r12);
        r7[r8] = r9;
        r5 = java.lang.String.format(r5, r6, r7);
        r6 = 0;
        r0 = r33;
        r7 = r0.currentPhotoObjectThumb;
        if (r7 == 0) goto L_0x1c34;
    L_0x1c18:
        r0 = r33;
        r7 = r0.currentPhotoObjectThumb;
        r7 = r7.location;
    L_0x1c1e:
        r8 = "b1";
        r0 = r34;
        r9 = r0.messageOwner;
        r9 = r9.media;
        r9 = r9.document;
        r9 = r9.size;
        r10 = "webp";
        r11 = 1;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x182f;
    L_0x1c34:
        r7 = 0;
        goto L_0x1c1e;
    L_0x1c36:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x1f86;
    L_0x1c3c:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r2 = (float) r2;
        r3 = 1060320051; // 0x3f333333 float:0.7 double:5.23867711E-315;
        r2 = r2 * r3;
        r2 = (int) r2;
        r3 = r2;
    L_0x1c47:
        r4 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r3;
        r5 = r33.checkNeedDrawShareButton(r34);
        if (r5 == 0) goto L_0x2414;
    L_0x1c54:
        r5 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 - r5;
        r5 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);
        r3 = r3 - r5;
        r10 = r2;
    L_0x1c63:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getPhotoSize();
        if (r3 <= r2) goto L_0x2411;
    L_0x1c69:
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.getPhotoSize();
        r11 = r3;
    L_0x1c6e:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getPhotoSize();
        if (r4 <= r2) goto L_0x240e;
    L_0x1c74:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getPhotoSize();
        r12 = r2;
    L_0x1c79:
        r0 = r34;
        r2 = r0.type;
        r3 = 1;
        if (r2 != r3) goto L_0x1f9b;
    L_0x1c80:
        r33.updateSecretTimeText(r34);
        r0 = r34;
        r2 = r0.photoThumbs;
        r3 = 80;
        r2 = com.hanista.mobogram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3);
        r0 = r33;
        r0.currentPhotoObjectThumb = r2;
    L_0x1c91:
        r0 = r34;
        r2 = r0.caption;
        if (r2 == 0) goto L_0x1c9c;
    L_0x1c97:
        r2 = 0;
        r0 = r33;
        r0.mediaBackground = r2;
    L_0x1c9c:
        r0 = r34;
        r2 = r0.photoThumbs;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.getPhotoSize();
        r2 = com.hanista.mobogram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3);
        r0 = r33;
        r0.currentPhotoObject = r2;
        r5 = 0;
        r4 = 0;
        r0 = r33;
        r2 = r0.currentPhotoObject;
        if (r2 == 0) goto L_0x1cc3;
    L_0x1cb4:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r0 = r33;
        r3 = r0.currentPhotoObjectThumb;
        if (r2 != r3) goto L_0x1cc3;
    L_0x1cbe:
        r2 = 0;
        r0 = r33;
        r0.currentPhotoObjectThumb = r2;
    L_0x1cc3:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        if (r2 == 0) goto L_0x1cff;
    L_0x1cc9:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2664w;
        r2 = (float) r2;
        r3 = (float) r11;
        r2 = r2 / r3;
        r0 = r33;
        r3 = r0.currentPhotoObject;
        r3 = r3.f2664w;
        r3 = (float) r3;
        r3 = r3 / r2;
        r5 = (int) r3;
        r0 = r33;
        r3 = r0.currentPhotoObject;
        r3 = r3.f2663h;
        r3 = (float) r3;
        r2 = r3 / r2;
        r4 = (int) r2;
        if (r5 != 0) goto L_0x1ced;
    L_0x1ce7:
        r2 = 1125515264; // 0x43160000 float:150.0 double:5.56078426E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
    L_0x1ced:
        if (r4 != 0) goto L_0x1cf5;
    L_0x1cef:
        r2 = 1125515264; // 0x43160000 float:150.0 double:5.56078426E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
    L_0x1cf5:
        if (r4 <= r12) goto L_0x2046;
    L_0x1cf7:
        r2 = (float) r4;
        r3 = (float) r12;
        r2 = r2 / r3;
        r3 = (float) r5;
        r2 = r3 / r2;
        r5 = (int) r2;
        r4 = r12;
    L_0x1cff:
        if (r5 == 0) goto L_0x1d03;
    L_0x1d01:
        if (r4 != 0) goto L_0x240a;
    L_0x1d03:
        r0 = r34;
        r2 = r0.type;
        r3 = 8;
        if (r2 != r3) goto L_0x240a;
    L_0x1d0b:
        r2 = 0;
        r3 = r2;
    L_0x1d0d:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.size();
        if (r3 >= r2) goto L_0x240a;
    L_0x1d1d:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.get(r3);
        r2 = (com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute) r2;
        r6 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r6 != 0) goto L_0x1d35;
    L_0x1d31:
        r6 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r6 == 0) goto L_0x209b;
    L_0x1d35:
        r3 = r2.f2659w;
        r3 = (float) r3;
        r4 = (float) r11;
        r4 = r3 / r4;
        r3 = r2.f2659w;
        r3 = (float) r3;
        r3 = r3 / r4;
        r3 = (int) r3;
        r5 = r2.f2658h;
        r5 = (float) r5;
        r4 = r5 / r4;
        r4 = (int) r4;
        if (r4 <= r12) goto L_0x2076;
    L_0x1d48:
        r2 = (float) r4;
        r4 = (float) r12;
        r2 = r2 / r4;
        r3 = (float) r3;
        r2 = r3 / r2;
        r3 = (int) r2;
        r2 = r12;
    L_0x1d50:
        if (r3 == 0) goto L_0x1d54;
    L_0x1d52:
        if (r2 != 0) goto L_0x1d5b;
    L_0x1d54:
        r2 = 1125515264; // 0x43160000 float:150.0 double:5.56078426E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r3 = r2;
    L_0x1d5b:
        r0 = r34;
        r4 = r0.type;
        r5 = 3;
        if (r4 != r5) goto L_0x1d7a;
    L_0x1d62:
        r0 = r33;
        r4 = r0.infoWidth;
        r5 = 1109393408; // 0x42200000 float:40.0 double:5.481131706E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 + r5;
        if (r3 >= r4) goto L_0x1d7a;
    L_0x1d6f:
        r0 = r33;
        r3 = r0.infoWidth;
        r4 = 1109393408; // 0x42200000 float:40.0 double:5.481131706E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 + r4;
    L_0x1d7a:
        r4 = 1096810496; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r4 = r10 - r4;
        r0 = r33;
        r0.availableTimeWidth = r4;
        r33.measureTime(r34);
        r0 = r33;
        r5 = r0.timeWidth;
        r4 = r34.isOutOwner();
        if (r4 == 0) goto L_0x20a0;
    L_0x1d93:
        r4 = 20;
    L_0x1d95:
        r4 = r4 + 14;
        r4 = (float) r4;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r10 = r5 + r4;
        if (r3 >= r10) goto L_0x1da1;
    L_0x1da0:
        r3 = r10;
    L_0x1da1:
        r4 = r34.isSecretPhoto();
        if (r4 == 0) goto L_0x2400;
    L_0x1da7:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x20a3;
    L_0x1dad:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.getMinTabletSide();
        r2 = (float) r2;
        r3 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r2 = r2 * r3;
        r2 = (int) r2;
        r12 = r2;
        r13 = r2;
    L_0x1db8:
        r2 = 1094713344; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r13;
        r0 = r33;
        r0.backgroundWidth = r2;
        r0 = r33;
        r2 = r0.mediaBackground;
        if (r2 != 0) goto L_0x1dd8;
    L_0x1dc9:
        r0 = r33;
        r2 = r0.backgroundWidth;
        r3 = 1091567616; // 0x41100000 float:9.0 double:5.39306059E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.backgroundWidth = r2;
    L_0x1dd8:
        r0 = r34;
        r2 = r0.caption;
        if (r2 == 0) goto L_0x1e89;
    L_0x1dde:
        r2 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x20be }
        r0 = r34;
        r3 = r0.caption;	 Catch:{ Exception -> 0x20be }
        r4 = com.hanista.mobogram.messenger.MessageObject.getTextPaint();	 Catch:{ Exception -> 0x20be }
        r5 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x20be }
        r5 = r13 - r5;
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x20be }
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x20be }
        r0 = r33;
        r0.captionLayout = r2;	 Catch:{ Exception -> 0x20be }
        r2 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();	 Catch:{ Exception -> 0x20be }
        if (r2 == 0) goto L_0x1e2a;
    L_0x1e03:
        r2 = r34.isOutOwner();	 Catch:{ Exception -> 0x20be }
        if (r2 == 0) goto L_0x20b8;
    L_0x1e09:
        r2 = com.hanista.mobogram.messenger.MessageObject.textPaintRight;	 Catch:{ Exception -> 0x20be }
        com.hanista.mobogram.messenger.MessageObject.textPaint = r2;	 Catch:{ Exception -> 0x20be }
    L_0x1e0d:
        r2 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x20be }
        r0 = r34;
        r3 = r0.caption;	 Catch:{ Exception -> 0x20be }
        r4 = com.hanista.mobogram.messenger.MessageObject.textPaint;	 Catch:{ Exception -> 0x20be }
        r5 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x20be }
        r5 = r13 - r5;
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x20be }
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x20be }
        r0 = r33;
        r0.captionLayout = r2;	 Catch:{ Exception -> 0x20be }
    L_0x1e2a:
        r0 = r33;
        r2 = r0.captionLayout;	 Catch:{ Exception -> 0x20be }
        r2 = r2.getLineCount();	 Catch:{ Exception -> 0x20be }
        if (r2 <= 0) goto L_0x23fd;
    L_0x1e34:
        r0 = r33;
        r2 = r0.captionLayout;	 Catch:{ Exception -> 0x20be }
        r2 = r2.getHeight();	 Catch:{ Exception -> 0x20be }
        r0 = r33;
        r0.captionHeight = r2;	 Catch:{ Exception -> 0x20be }
        r0 = r33;
        r2 = r0.captionHeight;	 Catch:{ Exception -> 0x20be }
        r3 = 1091567616; // 0x41100000 float:9.0 double:5.39306059E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x20be }
        r2 = r2 + r3;
        r2 = r2 + r15;
        r0 = r33;
        r3 = r0.captionLayout;	 Catch:{ Exception -> 0x23c2 }
        r0 = r33;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x23c2 }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x23c2 }
        r4 = r4 + -1;
        r3 = r3.getLineWidth(r4);	 Catch:{ Exception -> 0x23c2 }
        r0 = r33;
        r4 = r0.captionLayout;	 Catch:{ Exception -> 0x23c2 }
        r0 = r33;
        r5 = r0.captionLayout;	 Catch:{ Exception -> 0x23c2 }
        r5 = r5.getLineCount();	 Catch:{ Exception -> 0x23c2 }
        r5 = r5 + -1;
        r4 = r4.getLineLeft(r5);	 Catch:{ Exception -> 0x23c2 }
        r3 = r3 + r4;
        r4 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x23c2 }
        r4 = r13 - r4;
        r4 = (float) r4;	 Catch:{ Exception -> 0x23c2 }
        r3 = r4 - r3;
        r4 = (float) r10;	 Catch:{ Exception -> 0x23c2 }
        r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
        if (r3 >= 0) goto L_0x1e88;
    L_0x1e81:
        r3 = 1096810496; // 0x41600000 float:14.0 double:5.41896386E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x23c2 }
        r2 = r2 + r3;
    L_0x1e88:
        r15 = r2;
    L_0x1e89:
        r2 = java.util.Locale.US;
        r3 = "%d_%d";
        r4 = 2;
        r4 = new java.lang.Object[r4];
        r5 = 0;
        r6 = (float) r13;
        r7 = com.hanista.mobogram.messenger.AndroidUtilities.density;
        r6 = r6 / r7;
        r6 = (int) r6;
        r6 = java.lang.Integer.valueOf(r6);
        r4[r5] = r6;
        r5 = 1;
        r6 = (float) r12;
        r7 = com.hanista.mobogram.messenger.AndroidUtilities.density;
        r6 = r6 / r7;
        r6 = (int) r6;
        r6 = java.lang.Integer.valueOf(r6);
        r4[r5] = r6;
        r2 = java.lang.String.format(r2, r3, r4);
        r0 = r33;
        r0.currentPhotoFilter = r2;
        r0 = r34;
        r2 = r0.photoThumbs;
        if (r2 == 0) goto L_0x1ec2;
    L_0x1eb7:
        r0 = r34;
        r2 = r0.photoThumbs;
        r2 = r2.size();
        r3 = 1;
        if (r2 > r3) goto L_0x1ed1;
    L_0x1ec2:
        r0 = r34;
        r2 = r0.type;
        r3 = 3;
        if (r2 == r3) goto L_0x1ed1;
    L_0x1ec9:
        r0 = r34;
        r2 = r0.type;
        r3 = 8;
        if (r2 != r3) goto L_0x1ef3;
    L_0x1ed1:
        r2 = r34.isSecretPhoto();
        if (r2 == 0) goto L_0x20ca;
    L_0x1ed7:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r33;
        r3 = r0.currentPhotoFilter;
        r2 = r2.append(r3);
        r3 = "_b2";
        r2 = r2.append(r3);
        r2 = r2.toString();
        r0 = r33;
        r0.currentPhotoFilter = r2;
    L_0x1ef3:
        r2 = 0;
        r0 = r34;
        r3 = r0.type;
        r4 = 3;
        if (r3 == r4) goto L_0x1f03;
    L_0x1efb:
        r0 = r34;
        r3 = r0.type;
        r4 = 8;
        if (r3 != r4) goto L_0x23fa;
    L_0x1f03:
        r2 = 1;
        r7 = r2;
    L_0x1f05:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        if (r2 == 0) goto L_0x1f1c;
    L_0x1f0b:
        if (r7 != 0) goto L_0x1f1c;
    L_0x1f0d:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.size;
        if (r2 != 0) goto L_0x1f1c;
    L_0x1f15:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r3 = -1;
        r2.size = r3;
    L_0x1f1c:
        r0 = r34;
        r2 = r0.type;
        r3 = 1;
        if (r2 != r3) goto L_0x2131;
    L_0x1f23:
        r0 = r33;
        r2 = r0.currentPhotoObject;
        if (r2 == 0) goto L_0x2125;
    L_0x1f29:
        r2 = 1;
        r0 = r33;
        r3 = r0.currentPhotoObject;
        r3 = com.hanista.mobogram.messenger.FileLoader.getAttachFileName(r3);
        r0 = r34;
        r4 = r0.mediaExists;
        if (r4 == 0) goto L_0x20e8;
    L_0x1f38:
        r4 = com.hanista.mobogram.messenger.MediaController.m71a();
        r0 = r33;
        r4.m149a(r0);
    L_0x1f41:
        if (r2 != 0) goto L_0x1f5e;
    L_0x1f43:
        r2 = com.hanista.mobogram.messenger.MediaController.m71a();
        r4 = 1;
        r2 = r2.m157a(r4);
        if (r2 != 0) goto L_0x1f5e;
    L_0x1f4e:
        r2 = com.hanista.mobogram.messenger.FileLoader.getInstance();
        r2 = r2.isLoadingFile(r3);
        if (r2 != 0) goto L_0x1f5e;
    L_0x1f58:
        r2 = r33.isFavoriteAndAutoDownload();
        if (r2 == 0) goto L_0x20f6;
    L_0x1f5e:
        r0 = r33;
        r2 = r0.photoImage;
        r0 = r33;
        r3 = r0.currentPhotoObject;
        r3 = r3.location;
        r0 = r33;
        r4 = r0.currentPhotoFilter;
        r0 = r33;
        r5 = r0.currentPhotoObjectThumb;
        if (r5 == 0) goto L_0x20eb;
    L_0x1f72:
        r0 = r33;
        r5 = r0.currentPhotoObjectThumb;
        r5 = r5.location;
    L_0x1f78:
        r0 = r33;
        r6 = r0.currentPhotoFilter;
        if (r7 == 0) goto L_0x20ee;
    L_0x1f7e:
        r7 = 0;
    L_0x1f7f:
        r8 = 0;
        r9 = 0;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x182f;
    L_0x1f86:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r2 = r2.x;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r3.y;
        r2 = java.lang.Math.min(r2, r3);
        r2 = (float) r2;
        r3 = 1060320051; // 0x3f333333 float:0.7 double:5.23867711E-315;
        r2 = r2 * r3;
        r2 = (int) r2;
        r3 = r2;
        goto L_0x1c47;
    L_0x1f9b:
        r0 = r34;
        r2 = r0.type;
        r3 = 3;
        if (r2 != r3) goto L_0x1fc5;
    L_0x1fa2:
        r2 = 0;
        r0 = r33;
        r1 = r34;
        r0.createDocumentLayout(r2, r1);
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 1;
        r2.setNeedsQualityThumb(r3);
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 1;
        r2.setShouldGenerateQualityThumb(r3);
        r0 = r33;
        r2 = r0.photoImage;
        r0 = r34;
        r2.setParentMessageObject(r0);
        goto L_0x1c91;
    L_0x1fc5:
        r0 = r34;
        r2 = r0.type;
        r3 = 8;
        if (r2 != r3) goto L_0x1c91;
    L_0x1fcd:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "GIF, ";
        r2 = r2.append(r3);
        r0 = r34;
        r3 = r0.messageOwner;
        r3 = r3.media;
        r3 = r3.document;
        r3 = r3.size;
        r4 = (long) r3;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.formatFileSize(r4);
        r2 = r2.append(r3);
        r3 = r2.toString();
        r2 = infoPaint;
        r2 = r2.measureText(r3);
        r4 = (double) r2;
        r4 = java.lang.Math.ceil(r4);
        r2 = (int) r4;
        r0 = r33;
        r0.infoWidth = r2;
        r2 = new android.text.StaticLayout;
        r4 = infoPaint;
        r0 = r33;
        r5 = r0.infoWidth;
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);
        r0 = r33;
        r0.infoLayout = r2;
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 1;
        r2.setNeedsQualityThumb(r3);
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 1;
        r2.setShouldGenerateQualityThumb(r3);
        r0 = r33;
        r2 = r0.photoImage;
        r0 = r34;
        r2.setParentMessageObject(r0);
        r0 = r33;
        r2 = r0.radialProgress;
        r0 = r34;
        r3 = r0.messageOwner;
        r3 = r3.media;
        r3 = r3.document;
        r3 = r3.size;
        r4 = (long) r3;
        r0 = r34;
        r3 = r0.type;
        r2.setSizeAndType(r4, r3);
        goto L_0x1c91;
    L_0x2046:
        r2 = 1123024896; // 0x42f00000 float:120.0 double:5.548480205E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        if (r4 >= r2) goto L_0x1cff;
    L_0x204e:
        r2 = 1123024896; // 0x42f00000 float:120.0 double:5.548480205E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r0 = r33;
        r2 = r0.currentPhotoObject;
        r2 = r2.f2663h;
        r2 = (float) r2;
        r3 = (float) r4;
        r2 = r2 / r3;
        r0 = r33;
        r3 = r0.currentPhotoObject;
        r3 = r3.f2664w;
        r3 = (float) r3;
        r3 = r3 / r2;
        r6 = (float) r11;
        r3 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1));
        if (r3 >= 0) goto L_0x1cff;
    L_0x206a:
        r0 = r33;
        r3 = r0.currentPhotoObject;
        r3 = r3.f2664w;
        r3 = (float) r3;
        r2 = r3 / r2;
        r5 = (int) r2;
        goto L_0x1cff;
    L_0x2076:
        r5 = 1123024896; // 0x42f00000 float:120.0 double:5.548480205E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);
        if (r4 >= r5) goto L_0x2407;
    L_0x207e:
        r4 = 1123024896; // 0x42f00000 float:120.0 double:5.548480205E-315;
        r12 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r4 = r2.f2658h;
        r4 = (float) r4;
        r5 = (float) r12;
        r4 = r4 / r5;
        r5 = r2.f2659w;
        r5 = (float) r5;
        r5 = r5 / r4;
        r6 = (float) r11;
        r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
        if (r5 >= 0) goto L_0x2404;
    L_0x2092:
        r2 = r2.f2659w;
        r2 = (float) r2;
        r2 = r2 / r4;
        r2 = (int) r2;
    L_0x2097:
        r3 = r2;
        r2 = r12;
        goto L_0x1d50;
    L_0x209b:
        r2 = r3 + 1;
        r3 = r2;
        goto L_0x1d0d;
    L_0x20a0:
        r4 = 0;
        goto L_0x1d95;
    L_0x20a3:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r2 = r2.x;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r3.y;
        r2 = java.lang.Math.min(r2, r3);
        r2 = (float) r2;
        r3 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r2 = r2 * r3;
        r2 = (int) r2;
        r12 = r2;
        r13 = r2;
        goto L_0x1db8;
    L_0x20b8:
        r2 = com.hanista.mobogram.messenger.MessageObject.textPaintLeft;	 Catch:{ Exception -> 0x20be }
        com.hanista.mobogram.messenger.MessageObject.textPaint = r2;	 Catch:{ Exception -> 0x20be }
        goto L_0x1e0d;
    L_0x20be:
        r2 = move-exception;
        r3 = r2;
        r2 = r15;
    L_0x20c1:
        r4 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r4, r3);
        r15 = r2;
        goto L_0x1e89;
    L_0x20ca:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r33;
        r3 = r0.currentPhotoFilter;
        r2 = r2.append(r3);
        r3 = "_b";
        r2 = r2.append(r3);
        r2 = r2.toString();
        r0 = r33;
        r0.currentPhotoFilter = r2;
        goto L_0x1ef3;
    L_0x20e8:
        r2 = 0;
        goto L_0x1f41;
    L_0x20eb:
        r5 = 0;
        goto L_0x1f78;
    L_0x20ee:
        r0 = r33;
        r7 = r0.currentPhotoObject;
        r7 = r7.size;
        goto L_0x1f7f;
    L_0x20f6:
        r2 = 1;
        r0 = r33;
        r0.photoNotSet = r2;
        r0 = r33;
        r2 = r0.currentPhotoObjectThumb;
        if (r2 == 0) goto L_0x2119;
    L_0x2101:
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 0;
        r4 = 0;
        r0 = r33;
        r5 = r0.currentPhotoObjectThumb;
        r5 = r5.location;
        r0 = r33;
        r6 = r0.currentPhotoFilter;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x182f;
    L_0x2119:
        r0 = r33;
        r3 = r0.photoImage;
        r2 = 0;
        r2 = (android.graphics.drawable.Drawable) r2;
        r3.setImageBitmap(r2);
        goto L_0x182f;
    L_0x2125:
        r0 = r33;
        r3 = r0.photoImage;
        r2 = 0;
        r2 = (android.graphics.drawable.BitmapDrawable) r2;
        r3.setImageBitmap(r2);
        goto L_0x182f;
    L_0x2131:
        r0 = r34;
        r2 = r0.type;
        r3 = 8;
        if (r2 != r3) goto L_0x2217;
    L_0x2139:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.media;
        r2 = r2.document;
        r3 = com.hanista.mobogram.messenger.FileLoader.getAttachFileName(r2);
        r2 = 0;
        r0 = r34;
        r4 = r0.attachPathExists;
        if (r4 == 0) goto L_0x21b1;
    L_0x214c:
        r2 = com.hanista.mobogram.messenger.MediaController.m71a();
        r0 = r33;
        r2.m149a(r0);
        r2 = 1;
    L_0x2156:
        r4 = r34.isSending();
        if (r4 != 0) goto L_0x21f2;
    L_0x215c:
        if (r2 != 0) goto L_0x2188;
    L_0x215e:
        r4 = com.hanista.mobogram.messenger.MediaController.m71a();
        r5 = 32;
        r4 = r4.m157a(r5);
        if (r4 != 0) goto L_0x2170;
    L_0x216a:
        r4 = r33.isFavoriteAndAutoDownload();
        if (r4 == 0) goto L_0x217e;
    L_0x2170:
        r0 = r34;
        r4 = r0.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r4 = com.hanista.mobogram.messenger.MessageObject.isNewGifDocument(r4);
        if (r4 != 0) goto L_0x2188;
    L_0x217e:
        r4 = com.hanista.mobogram.messenger.FileLoader.getInstance();
        r3 = r4.isLoadingFile(r3);
        if (r3 == 0) goto L_0x21f2;
    L_0x2188:
        r3 = 1;
        if (r2 != r3) goto L_0x21c2;
    L_0x218b:
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 0;
        r4 = r34.isSendError();
        if (r4 == 0) goto L_0x21b9;
    L_0x2196:
        r4 = 0;
    L_0x2197:
        r5 = 0;
        r6 = 0;
        r0 = r33;
        r7 = r0.currentPhotoObject;
        if (r7 == 0) goto L_0x21c0;
    L_0x219f:
        r0 = r33;
        r7 = r0.currentPhotoObject;
        r7 = r7.location;
    L_0x21a5:
        r0 = r33;
        r8 = r0.currentPhotoFilter;
        r9 = 0;
        r10 = 0;
        r11 = 0;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x182f;
    L_0x21b1:
        r0 = r34;
        r4 = r0.mediaExists;
        if (r4 == 0) goto L_0x2156;
    L_0x21b7:
        r2 = 2;
        goto L_0x2156;
    L_0x21b9:
        r0 = r34;
        r4 = r0.messageOwner;
        r4 = r4.attachPath;
        goto L_0x2197;
    L_0x21c0:
        r7 = 0;
        goto L_0x21a5;
    L_0x21c2:
        r0 = r33;
        r2 = r0.photoImage;
        r0 = r34;
        r3 = r0.messageOwner;
        r3 = r3.media;
        r3 = r3.document;
        r4 = 0;
        r0 = r33;
        r5 = r0.currentPhotoObject;
        if (r5 == 0) goto L_0x21f0;
    L_0x21d5:
        r0 = r33;
        r5 = r0.currentPhotoObject;
        r5 = r5.location;
    L_0x21db:
        r0 = r33;
        r6 = r0.currentPhotoFilter;
        r0 = r34;
        r7 = r0.messageOwner;
        r7 = r7.media;
        r7 = r7.document;
        r7 = r7.size;
        r8 = 0;
        r9 = 0;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x182f;
    L_0x21f0:
        r5 = 0;
        goto L_0x21db;
    L_0x21f2:
        r2 = 1;
        r0 = r33;
        r0.photoNotSet = r2;
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 0;
        r4 = 0;
        r0 = r33;
        r5 = r0.currentPhotoObject;
        if (r5 == 0) goto L_0x2215;
    L_0x2203:
        r0 = r33;
        r5 = r0.currentPhotoObject;
        r5 = r5.location;
    L_0x2209:
        r0 = r33;
        r6 = r0.currentPhotoFilter;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x182f;
    L_0x2215:
        r5 = 0;
        goto L_0x2209;
    L_0x2217:
        r0 = r33;
        r2 = r0.photoImage;
        r3 = 0;
        r4 = 0;
        r0 = r33;
        r5 = r0.currentPhotoObject;
        if (r5 == 0) goto L_0x2235;
    L_0x2223:
        r0 = r33;
        r5 = r0.currentPhotoObject;
        r5 = r5.location;
    L_0x2229:
        r0 = r33;
        r6 = r0.currentPhotoFilter;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9);
        goto L_0x182f;
    L_0x2235:
        r5 = 0;
        goto L_0x2229;
    L_0x2237:
        r0 = r33;
        r2 = r0.drawNameLayout;
        if (r2 == 0) goto L_0x1847;
    L_0x223d:
        r0 = r34;
        r2 = r0.messageOwner;
        r2 = r2.reply_to_msg_id;
        if (r2 != 0) goto L_0x1847;
    L_0x2245:
        r0 = r33;
        r2 = r0.namesOffset;
        r3 = 1088421888; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r33;
        r0.namesOffset = r2;
        goto L_0x1847;
    L_0x2256:
        r2 = com.hanista.mobogram.messenger.MessageObject.textPaintLeft;	 Catch:{ Exception -> 0x225c }
        com.hanista.mobogram.messenger.MessageObject.textPaint = r2;	 Catch:{ Exception -> 0x225c }
        goto L_0x0c3b;
    L_0x225c:
        r2 = move-exception;
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r2);
        goto L_0x0ce6;
    L_0x2265:
        r2 = 0;
        goto L_0x0c72;
    L_0x2268:
        r2 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        goto L_0x0d49;
    L_0x226c:
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r3 = r3.x;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r3 = java.lang.Math.min(r3, r4);
        r2 = r2 + r3;
        goto L_0x0d59;
    L_0x227b:
        r0 = r33;
        r2 = r0.widthForButtons;
        r5 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);
        r6 = r4 + -1;
        r5 = r5 * r6;
        r5 = r2 - r5;
        if (r11 != 0) goto L_0x2399;
    L_0x228c:
        r0 = r33;
        r2 = r0.mediaBackground;
        if (r2 == 0) goto L_0x2399;
    L_0x2292:
        r2 = 0;
    L_0x2293:
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r5 - r2;
        r5 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 - r5;
        r16 = r2 / r4;
        r2 = 0;
        r12 = r2;
        r13 = r3;
    L_0x22a5:
        r2 = r10.buttons;
        r2 = r2.size();
        if (r12 >= r2) goto L_0x23f4;
    L_0x22ad:
        r17 = new com.hanista.mobogram.ui.Cells.ChatMessageCell$BotButton;
        r2 = 0;
        r0 = r17;
        r1 = r33;
        r0.<init>(r2);
        r2 = r10.buttons;
        r2 = r2.get(r12);
        r2 = (com.hanista.mobogram.tgnet.TLRPC.KeyboardButton) r2;
        r0 = r17;
        r0.button = r2;
        r2 = r17.button;
        r2 = r2.data;
        r3 = com.hanista.mobogram.messenger.Utilities.bytesToHex(r2);
        r0 = r33;
        r2 = r0.botButtonsByData;
        r2 = r2.get(r3);
        r2 = (com.hanista.mobogram.ui.Cells.ChatMessageCell.BotButton) r2;
        if (r2 == 0) goto L_0x239d;
    L_0x22da:
        r4 = r2.progressAlpha;
        r0 = r17;
        r0.progressAlpha = r4;
        r4 = r2.angle;
        r0 = r17;
        r0.angle = r4;
        r4 = r2.lastUpdateTime;
        r0 = r17;
        r0.lastUpdateTime = r4;
    L_0x22f5:
        r0 = r33;
        r2 = r0.botButtonsByData;
        r0 = r17;
        r2.put(r3, r0);
        r2 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r16;
        r2 = r2 * r12;
        r0 = r17;
        r0.f2677x = r2;
        r2 = 1111490560; // 0x42400000 float:48.0 double:5.491493014E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 * r14;
        r3 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r3 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r0 = r17;
        r0.f2678y = r2;
        r0 = r17;
        r1 = r16;
        r0.width = r1;
        r2 = 1110441984; // 0x42300000 float:44.0 double:5.48631236E-315;
        r2 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r2);
        r0 = r17;
        r0.height = r2;
        r2 = r17.button;
        r2 = r2.text;
        r3 = botButtonPaint;
        r3 = r3.getFontMetricsInt();
        r4 = 1097859072; // 0x41700000 float:15.0 double:5.424144515E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r5 = 0;
        r2 = com.hanista.mobogram.messenger.Emoji.replaceEmoji(r2, r3, r4, r5);
        r3 = botButtonPaint;
        r4 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r4);
        r4 = r16 - r4;
        r4 = (float) r4;
        r5 = android.text.TextUtils.TruncateAt.END;
        r3 = android.text.TextUtils.ellipsize(r2, r3, r4, r5);
        r2 = new android.text.StaticLayout;
        r4 = botButtonPaint;
        r5 = 1092616192; // 0x41200000 float:10.0 double:5.398241246E-315;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r5);
        r5 = r16 - r5;
        r6 = android.text.Layout.Alignment.ALIGN_CENTER;
        r7 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);
        r0 = r17;
        r0.title = r2;
        r0 = r33;
        r2 = r0.botButtons;
        r0 = r17;
        r2.add(r0);
        r2 = r10.buttons;
        r2 = r2.size();
        r2 = r2 + -1;
        if (r12 != r2) goto L_0x23f2;
    L_0x2386:
        r2 = r17.f2677x;
        r3 = r17.width;
        r2 = r2 + r3;
        r3 = java.lang.Math.max(r13, r2);
    L_0x2393:
        r2 = r12 + 1;
        r12 = r2;
        r13 = r3;
        goto L_0x22a5;
    L_0x2399:
        r2 = 1091567616; // 0x41100000 float:9.0 double:5.39306059E-315;
        goto L_0x2293;
    L_0x239d:
        r4 = java.lang.System.currentTimeMillis();
        r0 = r17;
        r0.lastUpdateTime = r4;
        goto L_0x22f5;
    L_0x23a8:
        r0 = r33;
        r0.widthForButtons = r3;
    L_0x23ac:
        r33.updateWaveform();
        r0 = r33;
        r1 = r30;
        r0.updateButtonState(r1);
        return;
    L_0x23b7:
        r2 = 0;
        r0 = r33;
        r0.substractBackgroundHeight = r2;
        r2 = 0;
        r0 = r33;
        r0.keyboardHeight = r2;
        goto L_0x23ac;
    L_0x23c2:
        r3 = move-exception;
        goto L_0x20c1;
    L_0x23c5:
        r2 = move-exception;
        r4 = r5;
        goto L_0x0926;
    L_0x23c9:
        r2 = move-exception;
        r4 = r12;
        r6 = r24;
        goto L_0x08e9;
    L_0x23cf:
        r2 = move-exception;
        r6 = r24;
        goto L_0x08e9;
    L_0x23d4:
        r2 = move-exception;
        goto L_0x08e9;
    L_0x23d7:
        r2 = move-exception;
        r4 = r24;
        r5 = r25;
        r6 = r26;
        r7 = r27;
        goto L_0x0739;
    L_0x23e2:
        r2 = move-exception;
        r4 = r24;
        r6 = r26;
        r7 = r27;
        goto L_0x0739;
    L_0x23eb:
        r2 = move-exception;
        r5 = r13;
        goto L_0x06f9;
    L_0x23ef:
        r2 = move-exception;
        goto L_0x06f9;
    L_0x23f2:
        r3 = r13;
        goto L_0x2393;
    L_0x23f4:
        r3 = r13;
        goto L_0x0d8b;
    L_0x23f7:
        r11 = r2;
        goto L_0x0d6f;
    L_0x23fa:
        r7 = r2;
        goto L_0x1f05;
    L_0x23fd:
        r2 = r15;
        goto L_0x1e88;
    L_0x2400:
        r12 = r2;
        r13 = r3;
        goto L_0x1db8;
    L_0x2404:
        r2 = r3;
        goto L_0x2097;
    L_0x2407:
        r2 = r4;
        goto L_0x1d50;
    L_0x240a:
        r2 = r4;
        r3 = r5;
        goto L_0x1d50;
    L_0x240e:
        r12 = r4;
        goto L_0x1c79;
    L_0x2411:
        r11 = r3;
        goto L_0x1c6e;
    L_0x2414:
        r10 = r2;
        goto L_0x1c63;
    L_0x2417:
        r12 = r2;
        r13 = r3;
        goto L_0x1b48;
    L_0x241b:
        r2 = r4;
        r3 = r5;
        goto L_0x1b05;
    L_0x241f:
        r3 = r2;
        goto L_0x14b4;
    L_0x2422:
        r3 = r2;
        goto L_0x1455;
    L_0x2425:
        r11 = r2;
        goto L_0x1442;
    L_0x2428:
        r6 = r5;
        r7 = r2;
        goto L_0x0a9c;
    L_0x242c:
        r4 = r6;
        goto L_0x0dfc;
    L_0x242f:
        r7 = r4;
        goto L_0x0dd9;
    L_0x2432:
        r6 = r23;
        goto L_0x092d;
    L_0x2436:
        r8 = r13;
        r23 = r24;
        r24 = r25;
        r25 = r12;
        goto L_0x07e3;
    L_0x243f:
        r13 = r25;
        r25 = r26;
        r26 = r24;
        r24 = r27;
        goto L_0x0746;
    L_0x2449:
        r29 = r10;
        r27 = r13;
        goto L_0x04a4;
    L_0x244f:
        r12 = r3;
        goto L_0x03c7;
    L_0x2452:
        r3 = r2;
        goto L_0x0381;
    L_0x2455:
        r16 = r2;
        goto L_0x0243;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.Cells.ChatMessageCell.setMessageObject(com.hanista.mobogram.messenger.MessageObject):void");
    }

    public void setPressed(boolean z) {
        super.setPressed(z);
        this.radialProgress.swapBackground(getDrawableForCurrentState());
        if (this.useSeekBarWaweform) {
            this.seekBarWaveform.setSelected(isDrawSelectedBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectedBackground());
        }
        invalidate();
    }

    public void setVisiblePart(int i, int i2) {
        int i3 = DOCUMENT_ATTACH_TYPE_NONE;
        if (this.currentMessageObject != null && this.currentMessageObject.textLayoutBlocks != null) {
            int i4 = i - this.textY;
            int i5 = DOCUMENT_ATTACH_TYPE_NONE;
            int i6 = DOCUMENT_ATTACH_TYPE_NONE;
            while (i5 < this.currentMessageObject.textLayoutBlocks.size() && ((TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i5)).textYOffset <= ((float) i4)) {
                i6 = i5;
                i5 += DOCUMENT_ATTACH_TYPE_DOCUMENT;
            }
            i5 = -1;
            int i7 = -1;
            while (i6 < this.currentMessageObject.textLayoutBlocks.size()) {
                int i8;
                TextLayoutBlock textLayoutBlock = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i6);
                float f = textLayoutBlock.textYOffset;
                if (intersect(f, ((float) textLayoutBlock.height) + f, (float) i4, (float) (i4 + i2))) {
                    if (i7 == -1) {
                        i7 = i6;
                    }
                    i3 += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                    i8 = i6;
                    i5 = i7;
                } else if (f > ((float) i4)) {
                    break;
                } else {
                    i8 = i5;
                    i5 = i7;
                }
                i6 += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                i7 = i5;
                i5 = i8;
            }
            if (this.lastVisibleBlockNum != i5 || this.firstVisibleBlockNum != i7 || this.totalVisibleBlocksCount != i3) {
                this.lastVisibleBlockNum = i5;
                this.firstVisibleBlockNum = i7;
                this.totalVisibleBlocksCount = i3;
                invalidate();
            }
        }
    }

    public void stopGif() {
        if (this.photoImage != null) {
            this.photoImage.setAllowStartAnimation(false);
            this.photoImage.stopAnimation();
        }
        if (this.radialProgress != null) {
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
        }
    }

    public void updateAudioProgress() {
        if (this.currentMessageObject != null && this.documentAttach != null) {
            if (this.useSeekBarWaweform) {
                if (!this.seekBarWaveform.isDragging()) {
                    this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress);
                }
            } else if (!this.seekBar.isDragging()) {
                this.seekBar.setProgress(this.currentMessageObject.audioProgress);
            }
            int i;
            int i2;
            DocumentAttribute documentAttribute;
            CharSequence format;
            if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
                if (MediaController.m71a().m172d(this.currentMessageObject)) {
                    i = this.currentMessageObject.audioProgressSec;
                } else {
                    for (i2 = DOCUMENT_ATTACH_TYPE_NONE; i2 < this.documentAttach.attributes.size(); i2 += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        documentAttribute = (DocumentAttribute) this.documentAttach.attributes.get(i2);
                        if (documentAttribute instanceof TL_documentAttributeAudio) {
                            i = documentAttribute.duration;
                            break;
                        }
                    }
                    i = DOCUMENT_ATTACH_TYPE_NONE;
                }
                Object[] objArr = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                objArr[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(i / 60);
                objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(i % 60);
                format = String.format("%02d:%02d", objArr);
                if (this.lastTimeString == null || !(this.lastTimeString == null || this.lastTimeString.equals(format))) {
                    this.lastTimeString = format;
                    this.timeWidthAudio = (int) Math.ceil((double) audioTimePaint.measureText(format));
                    this.durationLayout = new StaticLayout(format, audioTimePaint, this.timeWidthAudio, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                }
            } else {
                for (i2 = DOCUMENT_ATTACH_TYPE_NONE; i2 < this.documentAttach.attributes.size(); i2 += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    documentAttribute = (DocumentAttribute) this.documentAttach.attributes.get(i2);
                    if (documentAttribute instanceof TL_documentAttributeAudio) {
                        i = documentAttribute.duration;
                        break;
                    }
                }
                i = DOCUMENT_ATTACH_TYPE_NONE;
                i2 = MediaController.m71a().m172d(this.currentMessageObject) ? this.currentMessageObject.audioProgressSec : DOCUMENT_ATTACH_TYPE_NONE;
                Object[] objArr2 = new Object[DOCUMENT_ATTACH_TYPE_VIDEO];
                objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(i2 / 60);
                objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(i2 % 60);
                objArr2[DOCUMENT_ATTACH_TYPE_GIF] = Integer.valueOf(i / 60);
                objArr2[DOCUMENT_ATTACH_TYPE_AUDIO] = Integer.valueOf(i % 60);
                format = String.format("%d:%02d / %d:%02d", objArr2);
                if (this.lastTimeString == null || !(this.lastTimeString == null || this.lastTimeString.equals(format))) {
                    this.lastTimeString = format;
                    this.durationLayout = new StaticLayout(format, audioTimePaint, (int) Math.ceil((double) audioTimePaint.measureText(format)), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                }
            }
            invalidate();
        }
    }

    public void updateButtonState(boolean z) {
        String str;
        boolean z2;
        float f = 0.0f;
        boolean z3 = true;
        if (this.currentMessageObject.type != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            if (this.currentMessageObject.type == 8 || this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO || this.currentMessageObject.type == 9 || this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                if (this.currentMessageObject.attachPathExists) {
                    String str2 = this.currentMessageObject.messageOwner.attachPath;
                    if (str2 != null && str2.length() == 0 && this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
                        str2 = this.currentMessageObject.getFileName();
                    }
                    str = str2;
                    z2 = true;
                } else if (!this.currentMessageObject.isSendError() || this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                    str = this.currentMessageObject.getFileName();
                    z2 = this.currentMessageObject.mediaExists;
                }
            } else if (this.documentAttachType != 0) {
                str = FileLoader.getAttachFileName(this.documentAttach);
                z2 = this.currentMessageObject.mediaExists;
            } else if (this.currentPhotoObject != null) {
                str = FileLoader.getAttachFileName(this.currentPhotoObject);
                z2 = this.currentMessageObject.mediaExists;
            }
            str = null;
            z2 = false;
        } else if (this.currentPhotoObject != null) {
            str = FileLoader.getAttachFileName(this.currentPhotoObject);
            z2 = this.currentMessageObject.mediaExists;
        } else {
            return;
        }
        if (TextUtils.isEmpty(str)) {
            this.radialProgress.setBackground(null, false, false);
            return;
        }
        boolean z4 = this.currentMessageObject.messageOwner.params != null && this.currentMessageObject.messageOwner.params.containsKey("query_id");
        Float fileProgress;
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if ((this.currentMessageObject.isOut() && this.currentMessageObject.isSending()) || (this.currentMessageObject.isSendError() && z4)) {
                MediaController.m71a().m152a(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, (FileDownloadProgressListener) this);
                this.buttonState = DOCUMENT_ATTACH_TYPE_VIDEO;
                RadialProgress radialProgress = this.radialProgress;
                Drawable drawableForCurrentState = getDrawableForCurrentState();
                if (z4) {
                    z3 = false;
                }
                radialProgress.setBackground(drawableForCurrentState, z3, z);
                if (z4) {
                    this.radialProgress.setProgress(0.0f, false);
                } else {
                    fileProgress = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
                    if (fileProgress == null && SendMessagesHelper.getInstance().isSendingMessage(this.currentMessageObject.getId())) {
                        fileProgress = Float.valueOf(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    }
                    this.radialProgress.setProgress(fileProgress != null ? fileProgress.floatValue() : 0.0f, false);
                }
            } else if (z2) {
                MediaController.m71a().m149a((FileDownloadProgressListener) this);
                z2 = MediaController.m71a().m172d(this.currentMessageObject);
                if (!z2 || (z2 && MediaController.m71a().m191s())) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
                } else {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
            } else {
                MediaController.m71a().m152a(str, this.currentMessageObject, (FileDownloadProgressListener) this);
                if (FileLoader.getInstance().isLoadingFile(str)) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_VIDEO;
                    fileProgress = ImageLoader.getInstance().getFileProgress(str);
                    if (fileProgress != null) {
                        this.radialProgress.setProgress(fileProgress.floatValue(), z);
                    } else {
                        this.radialProgress.setProgress(0.0f, z);
                    }
                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                } else {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                    this.radialProgress.setProgress(0.0f, z);
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                }
            }
            updateAudioProgress();
        } else if (this.currentMessageObject.type != 0 || this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (!this.currentMessageObject.isOut() || !this.currentMessageObject.isSending()) {
                if (!(this.currentMessageObject.messageOwner.attachPath == null || this.currentMessageObject.messageOwner.attachPath.length() == 0)) {
                    MediaController.m71a().m149a((FileDownloadProgressListener) this);
                }
                if (z2) {
                    MediaController.m71a().m149a((FileDownloadProgressListener) this);
                    if (this.currentMessageObject.type == 8 && !this.photoImage.isAllowStartAnimation()) {
                        this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                    } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
                        this.buttonState = DOCUMENT_ATTACH_TYPE_AUDIO;
                    } else {
                        this.buttonState = -1;
                    }
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                    if (this.photoNotSet) {
                        setMessageObject(this.currentMessageObject);
                    }
                    invalidate();
                    return;
                }
                MediaController.m71a().m152a(str, this.currentMessageObject, (FileDownloadProgressListener) this);
                if (FileLoader.getInstance().isLoadingFile(str)) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                    fileProgress = ImageLoader.getInstance().getFileProgress(str);
                    if (fileProgress != null) {
                        f = fileProgress.floatValue();
                    }
                } else if (this.cancelLoading || !((this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT && (MediaController.m71a().m157a((int) DOCUMENT_ATTACH_TYPE_DOCUMENT) || isFavoriteAndAutoDownload())) || (this.currentMessageObject.type == 8 && ((MediaController.m71a().m157a(32) || isFavoriteAndAutoDownload()) && MessageObject.isNewGifDocument(this.currentMessageObject.messageOwner.media.document))))) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
                    z3 = false;
                } else {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), z3, z);
                this.radialProgress.setProgress(f, false);
                invalidate();
            } else if (this.currentMessageObject.messageOwner.attachPath != null && this.currentMessageObject.messageOwner.attachPath.length() > 0) {
                MediaController.m71a().m152a(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, (FileDownloadProgressListener) this);
                z2 = this.currentMessageObject.messageOwner.attachPath == null || !this.currentMessageObject.messageOwner.attachPath.startsWith("http");
                HashMap hashMap = this.currentMessageObject.messageOwner.params;
                if (this.currentMessageObject.messageOwner.message == null || hashMap == null || !(hashMap.containsKey("url") || hashMap.containsKey("bot"))) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                } else {
                    this.buttonState = -1;
                    z2 = false;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), z2, z);
                if (z2) {
                    fileProgress = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
                    if (fileProgress == null && SendMessagesHelper.getInstance().isSendingMessage(this.currentMessageObject.getId())) {
                        fileProgress = Float.valueOf(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    }
                    RadialProgress radialProgress2 = this.radialProgress;
                    if (fileProgress != null) {
                        f = fileProgress.floatValue();
                    }
                    radialProgress2.setProgress(f, false);
                } else {
                    this.radialProgress.setProgress(0.0f, false);
                }
                invalidate();
            }
        } else if (this.currentPhotoObject != null && this.drawImageButton) {
            if (z2) {
                MediaController.m71a().m149a((FileDownloadProgressListener) this);
                if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF || this.photoImage.isAllowStartAnimation()) {
                    this.buttonState = -1;
                } else {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                invalidate();
                return;
            }
            MediaController.m71a().m152a(str, this.currentMessageObject, (FileDownloadProgressListener) this);
            if (FileLoader.getInstance().isLoadingFile(str)) {
                this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                fileProgress = ImageLoader.getInstance().getFileProgress(str);
                if (fileProgress != null) {
                    f = fileProgress.floatValue();
                }
            } else if (this.cancelLoading || !((this.documentAttachType == 0 && (MediaController.m71a().m157a((int) DOCUMENT_ATTACH_TYPE_DOCUMENT) || isFavoriteAndAutoDownload())) || (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF && (MediaController.m71a().m157a(32) || isFavoriteAndAutoDownload())))) {
                this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
                z3 = false;
            } else {
                this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
            }
            this.radialProgress.setProgress(f, false);
            this.radialProgress.setBackground(getDrawableForCurrentState(), z3, z);
            invalidate();
        }
    }
}
