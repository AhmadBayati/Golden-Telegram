package com.hanista.mobogram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.NotificationsController;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PsExtractor;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.ChatActivityEnterView;
import com.hanista.mobogram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.PopupAudioView;
import com.hanista.mobogram.ui.Components.RecordStatusDrawable;
import com.hanista.mobogram.ui.Components.SizeNotifierFrameLayout;
import com.hanista.mobogram.ui.Components.TypingDotsDrawable;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PopupNotificationActivity extends Activity implements NotificationCenterDelegate {
    private ActionBar actionBar;
    private boolean animationInProgress;
    private long animationStartTime;
    private ArrayList<ViewGroup> audioViews;
    private FrameLayout avatarContainer;
    private BackupImageView avatarImageView;
    private ViewGroup centerView;
    private ChatActivityEnterView chatActivityEnterView;
    private int classGuid;
    private TextView countText;
    private Chat currentChat;
    private int currentMessageNum;
    private MessageObject currentMessageObject;
    private User currentUser;
    private boolean finished;
    private ArrayList<ViewGroup> imageViews;
    private boolean isReply;
    private CharSequence lastPrintString;
    private ViewGroup leftView;
    private ViewGroup messageContainer;
    private float moveStartX;
    private TextView nameTextView;
    private Runnable onAnimationEndRunnable;
    private TextView onlineTextView;
    private RelativeLayout popupContainer;
    private ArrayList<MessageObject> popupMessages;
    private List<MessageObject> readedMessages;
    private RecordStatusDrawable recordStatusDrawable;
    private ViewGroup rightView;
    private boolean startedMoving;
    private ArrayList<ViewGroup> textViews;
    private TypingDotsDrawable typingDotsDrawable;
    private VelocityTracker velocityTracker;
    private WakeLock wakeLock;

    /* renamed from: com.hanista.mobogram.ui.PopupNotificationActivity.1 */
    class C17971 extends SizeNotifierFrameLayout {
        C17971(Context context) {
            super(context);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            int emojiPadding = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding() : 0;
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                if (childAt.getVisibility() != 8) {
                    int measuredHeight;
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    int measuredWidth = childAt.getMeasuredWidth();
                    int measuredHeight2 = childAt.getMeasuredHeight();
                    int i6 = layoutParams.gravity;
                    if (i6 == -1) {
                        i6 = 51;
                    }
                    int i7 = i6 & 7;
                    i6 &= 112;
                    switch (i7 & 7) {
                        case VideoPlayer.TYPE_AUDIO /*1*/:
                            i7 = ((((i3 - i) - measuredWidth) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                            break;
                        case VideoPlayer.STATE_ENDED /*5*/:
                            i7 = (i3 - measuredWidth) - layoutParams.rightMargin;
                            break;
                        default:
                            i7 = layoutParams.leftMargin;
                            break;
                    }
                    switch (i6) {
                        case TLRPC.USER_FLAG_PHONE /*16*/:
                            i6 = (((((i4 - emojiPadding) - i2) - measuredHeight2) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                            break;
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                            i6 = layoutParams.topMargin;
                            break;
                        case 80:
                            i6 = (((i4 - emojiPadding) - i2) - measuredHeight2) - layoutParams.bottomMargin;
                            break;
                        default:
                            i6 = layoutParams.topMargin;
                            break;
                    }
                    if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                        measuredHeight = emojiPadding != 0 ? getMeasuredHeight() - emojiPadding : getMeasuredHeight();
                        i6 = i7;
                    } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(childAt)) {
                        i6 = ((PopupNotificationActivity.this.popupContainer.getLeft() + PopupNotificationActivity.this.popupContainer.getMeasuredWidth()) - childAt.getMeasuredWidth()) - layoutParams.rightMargin;
                        measuredHeight = ((PopupNotificationActivity.this.popupContainer.getTop() + PopupNotificationActivity.this.popupContainer.getMeasuredHeight()) - childAt.getMeasuredHeight()) - layoutParams.bottomMargin;
                    } else {
                        measuredHeight = i6;
                        i6 = i7;
                    }
                    childAt.layout(i6, measuredHeight, i6 + measuredWidth, measuredHeight + measuredHeight2);
                }
            }
            notifyHeightChanged();
        }

        protected void onMeasure(int i, int i2) {
            MeasureSpec.getMode(i);
            MeasureSpec.getMode(i2);
            int size = MeasureSpec.getSize(i);
            int size2 = MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            int emojiPadding = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? size2 - PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding() : size2;
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (childAt.getVisibility() != 8) {
                    if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, C0700C.ENCODING_PCM_32BIT));
                    } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(childAt)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    } else {
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f) + emojiPadding), C0700C.ENCODING_PCM_32BIT));
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PopupNotificationActivity.2 */
    class C17982 implements ChatActivityEnterViewDelegate {
        C17982() {
        }

        public void needSendTyping() {
            if (PopupNotificationActivity.this.currentMessageObject != null) {
                MessagesController.getInstance().sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0, PopupNotificationActivity.this.classGuid);
            }
        }

        public void onAttachButtonHidden() {
        }

        public void onAttachButtonShow() {
        }

        public void onMessageEditEnd(boolean z) {
        }

        public void onMessageSend(CharSequence charSequence) {
            if (PopupNotificationActivity.this.currentMessageObject != null) {
                if (PopupNotificationActivity.this.currentMessageNum >= 0 && PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size()) {
                    PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
                }
                MessagesController.getInstance().markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, true);
                PopupNotificationActivity.this.currentMessageObject = null;
                PopupNotificationActivity.this.getNewMessage();
            }
        }

        public void onStickersTab(boolean z) {
        }

        public void onTextChanged(CharSequence charSequence, boolean z) {
        }

        public void onWindowSizeChanged(int i) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PopupNotificationActivity.3 */
    class C17993 extends ActionBarMenuOnItemClick {
        C17993() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PopupNotificationActivity.this.onFinish();
                PopupNotificationActivity.this.finish();
            } else if (i == 1) {
                PopupNotificationActivity.this.openCurrentMessage();
            } else if (i == 2) {
                PopupNotificationActivity.this.switchToNextMessage();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PopupNotificationActivity.4 */
    class C18004 implements OnClickListener {
        C18004() {
        }

        @TargetApi(9)
        public void onClick(DialogInterface dialogInterface, int i) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                PopupNotificationActivity.this.startActivity(intent);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PopupNotificationActivity.5 */
    class C18015 implements Runnable {
        C18015() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.switchToPreviousMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PopupNotificationActivity.6 */
    class C18026 implements Runnable {
        C18026() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.switchToNextMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PopupNotificationActivity.7 */
    class C18037 implements Runnable {
        C18037() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.applyViewsLayoutParams(0);
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PopupNotificationActivity.8 */
    class C18048 implements View.OnClickListener {
        C18048() {
        }

        public void onClick(View view) {
            PopupNotificationActivity.this.openCurrentMessage();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PopupNotificationActivity.9 */
    class C18059 implements View.OnClickListener {
        C18059() {
        }

        public void onClick(View view) {
            PopupNotificationActivity.this.openCurrentMessage();
        }
    }

    public class FrameLayoutAnimationListener extends FrameLayout {
        public FrameLayoutAnimationListener(Context context) {
            super(context);
        }

        public FrameLayoutAnimationListener(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public FrameLayoutAnimationListener(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
        }

        protected void onAnimationEnd() {
            super.onAnimationEnd();
            if (PopupNotificationActivity.this.onAnimationEndRunnable != null) {
                PopupNotificationActivity.this.onAnimationEndRunnable.run();
                PopupNotificationActivity.this.onAnimationEndRunnable = null;
            }
        }
    }

    private class FrameLayoutTouch extends FrameLayout {
        public FrameLayoutTouch(Context context) {
            super(context);
        }

        public FrameLayoutTouch(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public FrameLayoutTouch(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent);
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            ((PopupNotificationActivity) getContext()).onTouchEventMy(null);
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    public PopupNotificationActivity() {
        this.textViews = new ArrayList();
        this.imageViews = new ArrayList();
        this.audioViews = new ArrayList();
        this.velocityTracker = null;
        this.finished = false;
        this.currentMessageObject = null;
        this.currentMessageNum = 0;
        this.wakeLock = null;
        this.animationInProgress = false;
        this.animationStartTime = 0;
        this.moveStartX = Face.UNCOMPUTED_PROBABILITY;
        this.startedMoving = false;
        this.onAnimationEndRunnable = null;
        this.popupMessages = new ArrayList();
        this.readedMessages = new ArrayList();
    }

    private void applyViewsLayoutParams(int i) {
        int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        if (this.leftView != null) {
            LayoutParams layoutParams = (LayoutParams) this.leftView.getLayoutParams();
            layoutParams.gravity = 51;
            layoutParams.height = -1;
            layoutParams.width = dp;
            layoutParams.leftMargin = (-dp) + i;
            this.leftView.setLayoutParams(layoutParams);
        }
        if (this.centerView != null) {
            layoutParams = (LayoutParams) this.centerView.getLayoutParams();
            layoutParams.gravity = 51;
            layoutParams.height = -1;
            layoutParams.width = dp;
            layoutParams.leftMargin = i;
            this.centerView.setLayoutParams(layoutParams);
        }
        if (this.rightView != null) {
            layoutParams = (LayoutParams) this.rightView.getLayoutParams();
            layoutParams.gravity = 51;
            layoutParams.height = -1;
            layoutParams.width = dp;
            layoutParams.leftMargin = dp + i;
            this.rightView.setLayoutParams(layoutParams);
        }
        this.messageContainer.invalidate();
    }

    private void checkAndUpdateAvatar() {
        TLObject tLObject;
        Drawable drawable = null;
        Drawable avatarDrawable;
        if (this.currentChat != null) {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
            if (chat != null) {
                this.currentChat = chat;
                if (this.currentChat.photo != null) {
                    drawable = this.currentChat.photo.photo_small;
                }
                avatarDrawable = new AvatarDrawable(this.currentChat);
                tLObject = drawable;
                drawable = avatarDrawable;
            } else {
                return;
            }
        } else if (this.currentUser != null) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
            if (user != null) {
                this.currentUser = user;
                if (this.currentUser.photo != null) {
                    drawable = this.currentUser.photo.photo_small;
                }
                avatarDrawable = new AvatarDrawable(this.currentUser);
                Object obj = drawable;
                drawable = avatarDrawable;
            } else {
                return;
            }
        } else {
            tLObject = null;
        }
        if (this.avatarImageView != null) {
            this.avatarImageView.setImage(tLObject, "50_50", drawable);
        }
    }

    private void fixLayout() {
        if (this.avatarContainer != null) {
            this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (PopupNotificationActivity.this.avatarContainer != null) {
                        PopupNotificationActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f)) / 2;
                    PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), currentActionBarHeight, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), currentActionBarHeight);
                    return true;
                }
            });
        }
        if (this.messageContainer != null) {
            this.messageContainer.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (!(PopupNotificationActivity.this.checkTransitionAnimation() || PopupNotificationActivity.this.startedMoving)) {
                        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) PopupNotificationActivity.this.messageContainer.getLayoutParams();
                        marginLayoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                        marginLayoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
                        marginLayoutParams.width = -1;
                        marginLayoutParams.height = -1;
                        PopupNotificationActivity.this.messageContainer.setLayoutParams(marginLayoutParams);
                        PopupNotificationActivity.this.applyViewsLayoutParams(0);
                    }
                    return true;
                }
            });
        }
    }

    private void getNewMessage() {
        if (this.popupMessages.isEmpty()) {
            onFinish();
            finish();
            return;
        }
        int i;
        if ((this.currentMessageNum != 0 || this.chatActivityEnterView.hasText() || this.startedMoving) && this.currentMessageObject != null) {
            for (int i2 = 0; i2 < this.popupMessages.size(); i2++) {
                if (((MessageObject) this.popupMessages.get(i2)).getId() == this.currentMessageObject.getId()) {
                    this.currentMessageNum = i2;
                    i = 1;
                    break;
                }
            }
        }
        i = 0;
        if (i == 0) {
            this.currentMessageNum = 0;
            this.currentMessageObject = (MessageObject) this.popupMessages.get(0);
            updateInterfaceForCurrentMessage(0);
        } else if (this.startedMoving) {
            if (this.currentMessageNum == this.popupMessages.size() - 1) {
                prepareLayouts(3);
            } else if (this.currentMessageNum == 1) {
                prepareLayouts(4);
            }
        }
        this.countText.setText(String.format("%d/%d", new Object[]{Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())}));
    }

    private ViewGroup getViewForMessage(int i, boolean z) {
        if (this.popupMessages.size() == 1 && (i < 0 || i >= this.popupMessages.size())) {
            return null;
        }
        View view;
        if (i == -1) {
            i = this.popupMessages.size() - 1;
        } else if (i == this.popupMessages.size()) {
            i = 0;
        }
        MessageObject messageObject = (MessageObject) this.popupMessages.get(i);
        TextView textView;
        if (messageObject.type == 1 || messageObject.type == 4) {
            View view2;
            if (this.imageViews.size() > 0) {
                view = (ViewGroup) this.imageViews.get(0);
                this.imageViews.remove(0);
                view2 = view;
            } else {
                view = new FrameLayoutAnimationListener(this);
                view.addView(getLayoutInflater().inflate(C0338R.layout.popup_image_layout, null));
                view.setTag(Integer.valueOf(2));
                view.setOnClickListener(new C18048());
                view2 = view;
            }
            textView = (TextView) view2.findViewById(C0338R.id.message_text);
            BackupImageView backupImageView = (BackupImageView) view2.findViewById(C0338R.id.message_image);
            backupImageView.setAspectFit(true);
            if (messageObject.type == 1) {
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100);
                Object obj = null;
                if (closestPhotoSizeWithSize != null) {
                    Object obj2 = 1;
                    if (messageObject.type == 1 && !FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                        obj2 = null;
                    }
                    if (obj2 != null || MediaController.m71a().m157a(1)) {
                        backupImageView.setImage(closestPhotoSizeWithSize.location, "100_100", closestPhotoSizeWithSize2.location, closestPhotoSizeWithSize.size);
                        obj = 1;
                    } else if (closestPhotoSizeWithSize2 != null) {
                        backupImageView.setImage(closestPhotoSizeWithSize2.location, null, (Drawable) null);
                        obj = 1;
                    }
                }
                if (obj == null) {
                    backupImageView.setVisibility(8);
                    textView.setVisibility(0);
                    textView.setTextSize(2, (float) MessagesController.getInstance().fontSize);
                    textView.setText(messageObject.messageText);
                } else {
                    backupImageView.setVisibility(0);
                    textView.setVisibility(8);
                }
            } else if (messageObject.type == 4) {
                textView.setVisibility(8);
                textView.setText(messageObject.messageText);
                backupImageView.setVisibility(0);
                double d = messageObject.messageOwner.media.geo.lat;
                double d2 = messageObject.messageOwner.media.geo._long;
                backupImageView.setImage(String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=13&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:big|%f,%f&sensor=false", new Object[]{Double.valueOf(d), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int) Math.ceil((double) AndroidUtilities.density))), Double.valueOf(d), Double.valueOf(d2)}), null, null);
            }
            view = view2;
        } else if (messageObject.type == 2) {
            PopupAudioView popupAudioView;
            if (this.audioViews.size() > 0) {
                view = (ViewGroup) this.audioViews.get(0);
                this.audioViews.remove(0);
                popupAudioView = (PopupAudioView) view.findViewWithTag(Integer.valueOf(300));
            } else {
                View frameLayoutAnimationListener = new FrameLayoutAnimationListener(this);
                frameLayoutAnimationListener.addView(getLayoutInflater().inflate(C0338R.layout.popup_audio_layout, null));
                frameLayoutAnimationListener.setTag(Integer.valueOf(3));
                frameLayoutAnimationListener.setOnClickListener(new C18059());
                ViewGroup viewGroup = (ViewGroup) frameLayoutAnimationListener.findViewById(C0338R.id.audio_container);
                popupAudioView = new PopupAudioView(this);
                popupAudioView.setTag(Integer.valueOf(300));
                viewGroup.addView(popupAudioView);
                view = frameLayoutAnimationListener;
            }
            popupAudioView.setMessageObject(messageObject);
            if (MediaController.m71a().m157a(2)) {
                popupAudioView.downloadAudioIfNeed();
            }
        } else {
            View view3;
            if (this.textViews.size() > 0) {
                view = (ViewGroup) this.textViews.get(0);
                this.textViews.remove(0);
                view3 = view;
            } else {
                view = new FrameLayoutAnimationListener(this);
                view.addView(getLayoutInflater().inflate(C0338R.layout.popup_text_layout, null));
                view.setTag(Integer.valueOf(1));
                view.findViewById(C0338R.id.text_container).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        PopupNotificationActivity.this.openCurrentMessage();
                    }
                });
                view3 = view;
            }
            textView = (TextView) view3.findViewById(C0338R.id.message_text);
            textView.setTag(Integer.valueOf(301));
            textView.setTextSize(2, (float) MessagesController.getInstance().fontSize);
            textView.setText(messageObject.messageText);
            view = view3;
        }
        if (view.getParent() == null) {
            this.messageContainer.addView(view);
        }
        view.setVisibility(0);
        if (!z) {
            return view;
        }
        int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutParams.gravity = 51;
        layoutParams.height = -1;
        layoutParams.width = dp;
        if (i == this.currentMessageNum) {
            layoutParams.leftMargin = 0;
        } else if (i == this.currentMessageNum - 1) {
            layoutParams.leftMargin = -dp;
        } else if (i == this.currentMessageNum + 1) {
            layoutParams.leftMargin = dp;
        }
        view.setLayoutParams(layoutParams);
        view.invalidate();
        return view;
    }

    private void handleIntent(Intent intent) {
        boolean z = intent != null && intent.getBooleanExtra("force", false);
        this.isReply = z;
        if (this.isReply) {
            this.popupMessages = NotificationsController.getInstance().popupReplyMessages;
        } else {
            this.popupMessages = NotificationsController.getInstance().popupMessages;
        }
        if (((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode() || !ApplicationLoader.isScreenOn) {
            getWindow().addFlags(2623490);
        } else {
            getWindow().addFlags(2623488);
            getWindow().clearFlags(2);
        }
        if (this.currentMessageObject == null) {
            this.currentMessageNum = 0;
        }
        getNewMessage();
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            try {
                this.actionBar.setBackgroundColor(AdvanceTheme.bg);
                int i = AdvanceTheme.bR;
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
                    int i2 = AdvanceTheme.bS;
                    this.actionBar.setBackgroundDrawable(new GradientDrawable(orientation, new int[]{r1, i2}));
                }
                this.nameTextView.setTextColor(AdvanceTheme.bh);
                this.nameTextView.setTextSize((float) AdvanceTheme.bX);
                this.onlineTextView.setTextColor(AdvanceTheme.bT);
                this.onlineTextView.setTextSize((float) AdvanceTheme.bY);
                this.countText.setTextColor(AdvanceTheme.bT);
                i = AdvanceTheme.bi;
                getResources().getDrawable(C0338R.drawable.mute_blue).setColorFilter(i, Mode.SRC_IN);
                getResources().getDrawable(C0338R.drawable.ic_ab_other).setColorFilter(i, Mode.MULTIPLY);
                getResources().getDrawable(C0338R.drawable.ic_ab_back).setColorFilter(i, Mode.MULTIPLY);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    private void openCurrentMessage() {
        if (this.currentMessageObject != null) {
            Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            long dialogId = this.currentMessageObject.getDialogId();
            if (((int) dialogId) != 0) {
                int i = (int) dialogId;
                if (i < 0) {
                    intent.putExtra("chatId", -i);
                } else {
                    intent.putExtra("userId", i);
                }
            } else {
                intent.putExtra("encId", (int) (dialogId >> 32));
            }
            intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
            intent.setFlags(TLRPC.MESSAGE_FLAG_EDITED);
            startActivity(intent);
            onFinish();
            finish();
        }
    }

    private void prepareLayouts(int i) {
        if (i == 0) {
            reuseView(this.centerView);
            reuseView(this.leftView);
            reuseView(this.rightView);
            for (int i2 = this.currentMessageNum - 1; i2 < this.currentMessageNum + 2; i2++) {
                if (i2 == this.currentMessageNum - 1) {
                    this.leftView = getViewForMessage(i2, true);
                } else if (i2 == this.currentMessageNum) {
                    this.centerView = getViewForMessage(i2, true);
                } else if (i2 == this.currentMessageNum + 1) {
                    this.rightView = getViewForMessage(i2, true);
                }
            }
        } else if (i == 1) {
            reuseView(this.rightView);
            this.rightView = this.centerView;
            this.centerView = this.leftView;
            this.leftView = getViewForMessage(this.currentMessageNum - 1, true);
        } else if (i == 2) {
            reuseView(this.leftView);
            this.leftView = this.centerView;
            this.centerView = this.rightView;
            this.rightView = getViewForMessage(this.currentMessageNum + 1, true);
        } else if (i == 3) {
            if (this.rightView != null) {
                r1 = ((LayoutParams) this.rightView.getLayoutParams()).leftMargin;
                reuseView(this.rightView);
                r0 = getViewForMessage(this.currentMessageNum + 1, false);
                this.rightView = r0;
                if (r0 != null) {
                    r2 = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                    r0 = (LayoutParams) this.rightView.getLayoutParams();
                    r0.gravity = 51;
                    r0.height = -1;
                    r0.width = r2;
                    r0.leftMargin = r1;
                    this.rightView.setLayoutParams(r0);
                    this.rightView.invalidate();
                }
            }
        } else if (i == 4 && this.leftView != null) {
            r1 = ((LayoutParams) this.leftView.getLayoutParams()).leftMargin;
            reuseView(this.leftView);
            r0 = getViewForMessage(0, false);
            this.leftView = r0;
            if (r0 != null) {
                r2 = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                r0 = (LayoutParams) this.leftView.getLayoutParams();
                r0.gravity = 51;
                r0.height = -1;
                r0.width = r2;
                r0.leftMargin = r1;
                this.leftView.setLayoutParams(r0);
                this.leftView.invalidate();
            }
        }
    }

    private void reuseView(ViewGroup viewGroup) {
        if (viewGroup != null) {
            int intValue = ((Integer) viewGroup.getTag()).intValue();
            viewGroup.setVisibility(8);
            if (intValue == 1) {
                this.textViews.add(viewGroup);
            } else if (intValue == 2) {
                this.imageViews.add(viewGroup);
            } else if (intValue == 3) {
                this.audioViews.add(viewGroup);
            }
        }
    }

    private void setTypingAnimation(boolean z) {
        if (this.actionBar != null) {
            if (z) {
                try {
                    Integer num = (Integer) MessagesController.getInstance().printingStringsTypes.get(Long.valueOf(this.currentMessageObject.getDialogId()));
                    if (num.intValue() == 0) {
                        this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.typingDotsDrawable, null, null, null);
                        this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                        this.typingDotsDrawable.start();
                        this.recordStatusDrawable.stop();
                        return;
                    } else if (num.intValue() == 1) {
                        this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.recordStatusDrawable, null, null, null);
                        this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                        this.recordStatusDrawable.start();
                        this.typingDotsDrawable.stop();
                        return;
                    } else {
                        return;
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    return;
                }
            }
            this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            this.onlineTextView.setCompoundDrawablePadding(0);
            this.typingDotsDrawable.stop();
            this.recordStatusDrawable.stop();
        }
    }

    private void switchToNextMessage() {
        if (this.popupMessages.size() > 1) {
            if (this.currentMessageNum < this.popupMessages.size() - 1) {
                this.currentMessageNum++;
            } else {
                this.currentMessageNum = 0;
            }
            this.currentMessageObject = (MessageObject) this.popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(2);
            this.countText.setText(String.format("%d/%d", new Object[]{Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())}));
        }
    }

    private void switchToPreviousMessage() {
        if (this.popupMessages.size() > 1) {
            if (this.currentMessageNum > 0) {
                this.currentMessageNum--;
            } else {
                this.currentMessageNum = this.popupMessages.size() - 1;
            }
            this.currentMessageObject = (MessageObject) this.popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(1);
            this.countText.setText(String.format("%d/%d", new Object[]{Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())}));
        }
    }

    private void updateInterfaceForCurrentMessage(int i) {
        if (this.actionBar != null) {
            if (MoboConstants.aN) {
                this.readedMessages.add(this.currentMessageObject);
            }
            this.currentChat = null;
            this.currentUser = null;
            long dialogId = this.currentMessageObject.getDialogId();
            this.chatActivityEnterView.setDialogId(dialogId);
            if (((int) dialogId) != 0) {
                int i2 = (int) dialogId;
                if (i2 > 0) {
                    this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(i2));
                } else {
                    this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-i2));
                    this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
                }
            } else {
                this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (dialogId >> 32))).user_id));
            }
            if (this.currentChat != null && this.currentUser != null) {
                this.nameTextView.setText(this.currentChat.title);
                this.onlineTextView.setText(UserObject.getUserName(this.currentUser));
                this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                this.nameTextView.setCompoundDrawablePadding(0);
            } else if (this.currentUser != null) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
                if (((int) dialogId) == 0) {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(C0338R.drawable.ic_lock_white, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                } else {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(0);
                }
            }
            prepareLayouts(i);
            updateSubtitle();
            checkAndUpdateAvatar();
            applyViewsLayoutParams(0);
        }
    }

    private void updateSubtitle() {
        if (this.actionBar != null && this.currentChat == null && this.currentUser != null) {
            if (this.currentUser.id / PointerIconCompat.TYPE_DEFAULT == 777 || this.currentUser.id / PointerIconCompat.TYPE_DEFAULT == 333 || ContactsController.getInstance().contactsDict.get(this.currentUser.id) != null || (ContactsController.getInstance().contactsDict.size() == 0 && ContactsController.getInstance().isLoadingContacts())) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            } else if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            } else {
                this.nameTextView.setText(PhoneFormat.getInstance().format("+" + this.currentUser.phone));
            }
            CharSequence charSequence = (CharSequence) MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentMessageObject.getDialogId()));
            if (charSequence == null || charSequence.length() == 0) {
                this.lastPrintString = null;
                setTypingAnimation(false);
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
                if (user != null) {
                    this.currentUser = user;
                }
                this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentUser));
                return;
            }
            this.lastPrintString = charSequence;
            this.onlineTextView.setText(charSequence);
            setTypingAnimation(true);
        }
    }

    public boolean checkTransitionAnimation() {
        if (this.animationInProgress && this.animationStartTime < System.currentTimeMillis() - 400) {
            this.animationInProgress = false;
            if (this.onAnimationEndRunnable != null) {
                this.onAnimationEndRunnable.run();
                this.onAnimationEndRunnable = null;
            }
        }
        return this.animationInProgress;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        int i2 = 0;
        if (i == NotificationCenter.appDidLogout) {
            onFinish();
            finish();
        } else if (i == NotificationCenter.pushMessagesUpdated) {
            getNewMessage();
        } else if (i == NotificationCenter.updateInterfaces) {
            if (this.currentMessageObject != null) {
                int intValue = ((Integer) objArr[0]).intValue();
                if (!((intValue & 1) == 0 && (intValue & 4) == 0 && (intValue & 16) == 0 && (intValue & 32) == 0)) {
                    updateSubtitle();
                }
                if (!((intValue & 2) == 0 && (intValue & 8) == 0)) {
                    checkAndUpdateAvatar();
                }
                if ((intValue & 64) != 0) {
                    CharSequence charSequence = (CharSequence) MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentMessageObject.getDialogId()));
                    if ((this.lastPrintString != null && charSequence == null) || ((this.lastPrintString == null && charSequence != null) || (this.lastPrintString != null && charSequence != null && !this.lastPrintString.equals(charSequence)))) {
                        updateSubtitle();
                    }
                }
            }
        } else if (i == NotificationCenter.audioDidReset) {
            r0 = (Integer) objArr[0];
            if (this.messageContainer != null) {
                r3 = this.messageContainer.getChildCount();
                for (r2 = 0; r2 < r3; r2++) {
                    r4 = this.messageContainer.getChildAt(r2);
                    if (((Integer) r4.getTag()).intValue() == 3) {
                        r1 = (PopupAudioView) r4.findViewWithTag(Integer.valueOf(300));
                        if (r1.getMessageObject() != null && r1.getMessageObject().getId() == r0.intValue()) {
                            r1.updateButtonState();
                            return;
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.audioProgressDidChanged) {
            r0 = (Integer) objArr[0];
            if (this.messageContainer != null) {
                r3 = this.messageContainer.getChildCount();
                for (r2 = 0; r2 < r3; r2++) {
                    r4 = this.messageContainer.getChildAt(r2);
                    if (((Integer) r4.getTag()).intValue() == 3) {
                        r1 = (PopupAudioView) r4.findViewWithTag(Integer.valueOf(300));
                        if (r1.getMessageObject() != null && r1.getMessageObject().getId() == r0.intValue()) {
                            r1.updateProgress();
                            return;
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.emojiDidLoaded) {
            if (this.messageContainer != null) {
                r2 = this.messageContainer.getChildCount();
                while (i2 < r2) {
                    View childAt = this.messageContainer.getChildAt(i2);
                    if (((Integer) childAt.getTag()).intValue() == 1) {
                        TextView textView = (TextView) childAt.findViewWithTag(Integer.valueOf(301));
                        if (textView != null) {
                            textView.invalidate();
                        }
                    }
                    i2++;
                }
            }
        } else if (i == NotificationCenter.contactsDidLoaded) {
            updateSubtitle();
        }
    }

    public void onBackPressed() {
        if (this.chatActivityEnterView.isPopupShowing()) {
            this.chatActivityEnterView.hidePopup(true);
        } else {
            super.onBackPressed();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        AndroidUtilities.checkDisplaySize(this, configuration);
        fixLayout();
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Theme.loadRecources(this);
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(identifier);
        }
        this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        this.typingDotsDrawable = new TypingDotsDrawable();
        this.recordStatusDrawable = new RecordStatusDrawable();
        View c17971 = new C17971(this);
        setContentView(c17971);
        c17971.setBackgroundColor(-1728053248);
        View relativeLayout = new RelativeLayout(this);
        c17971.addView(relativeLayout, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.popupContainer = new RelativeLayout(this);
        this.popupContainer.setBackgroundColor(-1);
        relativeLayout.addView(this.popupContainer, LayoutHelper.createRelative(-1, PsExtractor.VIDEO_STREAM_MASK, 12, 0, 12, 0, 13));
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onDestroy();
        }
        this.chatActivityEnterView = new ChatActivityEnterView(this, c17971, null, false);
        this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
        this.chatActivityEnterView.setDelegate(new C17982());
        this.messageContainer = new FrameLayoutTouch(this);
        this.popupContainer.addView(this.messageContainer, 0);
        this.actionBar = new ActionBar(this);
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setBackgroundColor(ThemeUtil.m2485a().m2289c());
        if (ThemeUtil.m2490b()) {
            this.actionBar.setBackgroundResource(C0338R.color.header);
        }
        this.popupContainer.addView(this.actionBar);
        ViewGroup.LayoutParams layoutParams = this.actionBar.getLayoutParams();
        layoutParams.width = -1;
        this.actionBar.setLayoutParams(layoutParams);
        this.countText = (TextView) this.actionBar.createMenu().addItemResource(2, C0338R.layout.popup_count_layout).findViewById(C0338R.id.count_text);
        this.avatarContainer = new FrameLayout(this);
        this.avatarContainer.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.actionBar.addView(this.avatarContainer);
        LayoutParams layoutParams2 = (LayoutParams) this.avatarContainer.getLayoutParams();
        layoutParams2.height = -1;
        layoutParams2.width = -2;
        layoutParams2.rightMargin = AndroidUtilities.dp(48.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
        layoutParams2.gravity = 51;
        this.avatarContainer.setLayoutParams(layoutParams2);
        this.avatarImageView = new BackupImageView(this);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarContainer.addView(this.avatarImageView);
        layoutParams2 = (LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams2.width = AndroidUtilities.dp(42.0f);
        layoutParams2.height = AndroidUtilities.dp(42.0f);
        layoutParams2.topMargin = AndroidUtilities.dp(3.0f);
        this.avatarImageView.setLayoutParams(layoutParams2);
        this.nameTextView = new TextView(this);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.avatarContainer.addView(this.nameTextView);
        LayoutParams layoutParams3 = (LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams3.bottomMargin = AndroidUtilities.dp(22.0f);
        layoutParams3.gravity = 80;
        this.nameTextView.setLayoutParams(layoutParams3);
        this.onlineTextView = new TextView(this);
        this.onlineTextView.setTextColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(3);
        this.avatarContainer.addView(this.onlineTextView);
        layoutParams3 = (LayoutParams) this.onlineTextView.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams3.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams3.gravity = 80;
        this.onlineTextView.setLayoutParams(layoutParams3);
        this.actionBar.setActionBarMenuOnItemClick(new C17993());
        this.wakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(268435462, "screen");
        this.wakeLock.setReferenceCounted(false);
        handleIntent(getIntent());
    }

    protected void onDestroy() {
        super.onDestroy();
        onFinish();
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        if (this.avatarImageView != null) {
            this.avatarImageView.setImageDrawable(null);
        }
    }

    protected void onFinish() {
        if (!this.finished) {
            if (this.readedMessages != null) {
                for (MessageObject messageObject : this.readedMessages) {
                    MessagesController.getInstance().markDialogAsRead(messageObject.getDialogId(), messageObject.getId(), Math.max(0, messageObject.getId()), messageObject.messageOwner.date, true, true);
                }
            }
            this.finished = true;
            if (this.isReply) {
                this.popupMessages.clear();
            }
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            if (this.chatActivityEnterView != null) {
                this.chatActivityEnterView.onDestroy();
            }
            if (this.wakeLock.isHeld()) {
                this.wakeLock.release();
            }
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.hidePopup(false);
            this.chatActivityEnterView.setFieldFocused(false);
        }
        ConnectionsManager.getInstance().setAppPaused(true, false);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 3 && iArr[0] != 0) {
            Builder builder = new Builder(this);
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setMessage(LocaleController.getString("PermissionNoAudio", C0338R.string.PermissionNoAudio));
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", C0338R.string.PermissionOpenSettings), new C18004());
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            builder.show();
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.setFieldFocused(true);
        }
        ConnectionsManager.getInstance().setAppPaused(false, false);
        fixLayout();
        checkAndUpdateAvatar();
        this.wakeLock.acquire(7000);
        initTheme();
    }

    public boolean onTouchEventMy(MotionEvent motionEvent) {
        if (checkTransitionAnimation()) {
            return false;
        }
        if (motionEvent != null && motionEvent.getAction() == 0) {
            this.moveStartX = motionEvent.getX();
        } else if (motionEvent != null && motionEvent.getAction() == 2) {
            float x = motionEvent.getX();
            int i = (int) (x - this.moveStartX);
            if (!(this.moveStartX == Face.UNCOMPUTED_PROBABILITY || this.startedMoving || Math.abs(i) <= AndroidUtilities.dp(10.0f))) {
                this.startedMoving = true;
                this.moveStartX = x;
                AndroidUtilities.lockOrientation(this);
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                    i = 0;
                } else {
                    this.velocityTracker.clear();
                    i = 0;
                }
            }
            if (this.startedMoving) {
                if (this.leftView == null && r0 > 0) {
                    i = 0;
                }
                if (this.rightView == null && r0 < 0) {
                    i = 0;
                }
                if (this.velocityTracker != null) {
                    this.velocityTracker.addMovement(motionEvent);
                }
                applyViewsLayoutParams(i);
            }
        } else if (motionEvent == null || motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent == null || !this.startedMoving) {
                applyViewsLayoutParams(0);
            } else {
                boolean z;
                int i2;
                View view;
                int abs;
                Animation translateAnimation;
                LayoutParams layoutParams = (LayoutParams) this.centerView.getLayoutParams();
                int x2 = (int) (motionEvent.getX() - this.moveStartX);
                int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                if (this.velocityTracker != null) {
                    this.velocityTracker.computeCurrentVelocity(PointerIconCompat.TYPE_DEFAULT);
                    if (this.velocityTracker.getXVelocity() >= 3500.0f) {
                        z = true;
                    } else if (this.velocityTracker.getXVelocity() <= -3500.0f) {
                        z = true;
                    }
                    if ((!z || x2 > dp / 3) && this.leftView != null) {
                        i2 = dp - layoutParams.leftMargin;
                        view = this.leftView;
                        this.onAnimationEndRunnable = new C18015();
                    } else if ((z || x2 < (-dp) / 3) && this.rightView != null) {
                        i2 = (-dp) - layoutParams.leftMargin;
                        view = this.rightView;
                        this.onAnimationEndRunnable = new C18026();
                    } else if (layoutParams.leftMargin != 0) {
                        i2 = -layoutParams.leftMargin;
                        view = x2 > 0 ? this.leftView : this.rightView;
                        this.onAnimationEndRunnable = new C18037();
                    } else {
                        view = null;
                        i2 = 0;
                    }
                    if (i2 != 0) {
                        abs = (int) (Math.abs(((float) i2) / ((float) dp)) * 200.0f);
                        translateAnimation = new TranslateAnimation(0.0f, (float) i2, 0.0f, 0.0f);
                        translateAnimation.setDuration((long) abs);
                        this.centerView.startAnimation(translateAnimation);
                        if (view != null) {
                            translateAnimation = new TranslateAnimation(0.0f, (float) i2, 0.0f, 0.0f);
                            translateAnimation.setDuration((long) abs);
                            view.startAnimation(translateAnimation);
                        }
                        this.animationInProgress = true;
                        this.animationStartTime = System.currentTimeMillis();
                    }
                }
                z = false;
                if (!z) {
                }
                i2 = dp - layoutParams.leftMargin;
                view = this.leftView;
                this.onAnimationEndRunnable = new C18015();
                if (i2 != 0) {
                    abs = (int) (Math.abs(((float) i2) / ((float) dp)) * 200.0f);
                    translateAnimation = new TranslateAnimation(0.0f, (float) i2, 0.0f, 0.0f);
                    translateAnimation.setDuration((long) abs);
                    this.centerView.startAnimation(translateAnimation);
                    if (view != null) {
                        translateAnimation = new TranslateAnimation(0.0f, (float) i2, 0.0f, 0.0f);
                        translateAnimation.setDuration((long) abs);
                        view.startAnimation(translateAnimation);
                    }
                    this.animationInProgress = true;
                    this.animationStartTime = System.currentTimeMillis();
                }
            }
            if (this.velocityTracker != null) {
                this.velocityTracker.recycle();
                this.velocityTracker = null;
            }
            this.startedMoving = false;
            this.moveStartX = Face.UNCOMPUTED_PROBABILITY;
        }
        return this.startedMoving;
    }
}
