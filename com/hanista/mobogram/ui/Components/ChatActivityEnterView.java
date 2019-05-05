package com.hanista.mobogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.NotificationsController;
import com.hanista.mobogram.messenger.SendMessagesHelper;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.UdpDataSource;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.query.DraftQuery;
import com.hanista.mobogram.messenger.query.MessagesQuery;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.component.StickerSelectAlert;
import com.hanista.mobogram.mobo.p006g.EmojiView;
import com.hanista.mobogram.mobo.p006g.EmojiViewInf;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.InputStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.KeyboardButton;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.MessageEntity;
import com.hanista.mobogram.tgnet.TLRPC.Peer;
import com.hanista.mobogram.tgnet.TLRPC.StickerSetCovered;
import com.hanista.mobogram.tgnet.TLRPC.TL_document;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetID;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButton;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonCallback;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonRequestPhone;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_message;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityPre;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_replyKeyboardMarkup;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.WebPage;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.RadioButtonCell;
import com.hanista.mobogram.ui.Cells.TextDetailCheckCell;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.BotKeyboardView.BotKeyboardViewDelegate;
import com.hanista.mobogram.ui.Components.EmojiView.Listener;
import com.hanista.mobogram.ui.Components.SeekBar.SeekBarDelegate;
import com.hanista.mobogram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate;
import com.hanista.mobogram.ui.Components.StickersAlert.StickersAlertDelegate;
import com.hanista.mobogram.ui.DialogsActivity;
import com.hanista.mobogram.ui.DialogsActivity.DialogsActivityDelegate;
import com.hanista.mobogram.ui.StickersActivity;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatActivityEnterView extends FrameLayout implements NotificationCenterDelegate, SizeNotifierFrameLayoutDelegate, StickersAlertDelegate {
    private boolean allowGifs;
    private boolean allowShowTopView;
    private boolean allowStickers;
    private LinearLayout attachButton;
    private int audioInterfaceState;
    private ImageView audioSendButton;
    private TL_document audioToSend;
    private MessageObject audioToSendMessageObject;
    private String audioToSendPath;
    private Drawable backgroundDrawable;
    private ImageView botButton;
    private MessageObject botButtonsMessageObject;
    private int botCount;
    private PopupWindow botKeyboardPopup;
    private BotKeyboardView botKeyboardView;
    private MessageObject botMessageObject;
    private TL_replyKeyboardMarkup botReplyMarkup;
    private boolean canWriteToChannel;
    private ImageView cancelBotButton;
    private int currentPopupContentType;
    private AnimatorSet currentTopViewAnimation;
    private ChatActivityEnterViewDelegate delegate;
    private RadioButtonCell detuneRadioCell;
    private long dialog_id;
    private RadioButtonCell disableRadioCell;
    private boolean disableSendSound;
    private float distCanMove;
    private Drawable dotDrawable;
    private boolean editingCaption;
    private MessageObject editingMessageObject;
    private int editingMessageReqId;
    private ImageView emojiButton;
    private int emojiPadding;
    private EmojiViewInf emojiView;
    private boolean forceShowSendButton;
    private boolean hasBotCommands;
    private boolean hasTwoAttach;
    private RadioButtonCell hoarsenessRadioCell;
    private boolean ignoreTextChange;
    private int innerTextChange;
    private boolean isPaused;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private String lastTimeString;
    private long lastTypingTimeSend;
    private WakeLock mWakeLock;
    private EditTextCaption messageEditText;
    private WebPage messageWebPage;
    private boolean messageWebPageSearch;
    private boolean needShowTopView;
    private ImageView notifyButton;
    private Runnable openKeyboardRunnable;
    private Activity parentActivity;
    private ChatActivity parentFragment;
    private KeyboardButton pendingLocationButton;
    private MessageObject pendingMessageObject;
    private CloseProgressDrawable2 progressDrawable;
    private RecordCircle recordCircle;
    private RecordDot recordDot;
    private FrameLayout recordPanel;
    private RadioButtonCell recordSpeedRadioCell;
    private TextView recordTimeText;
    private FrameLayout recordedAudioPanel;
    private ImageView recordedAudioPlayButton;
    private SeekBarWaveformView recordedAudioSeekBar;
    private TextView recordedAudioTimeTextView;
    private boolean recordingAudio;
    private MessageObject replyingMessageObject;
    private RadioButtonCell roboticRadioCell;
    private AnimatorSet runningAnimation;
    private AnimatorSet runningAnimation2;
    private AnimatorSet runningAnimationAudio;
    private int runningAnimationType;
    private ImageView sendButton;
    private FrameLayout sendButtonContainer;
    private boolean sendByEnter;
    private boolean showKeyboardOnResume;
    private boolean silent;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private LinearLayout slideText;
    private float startedDraggingX;
    private LinearLayout textFieldContainer;
    private View topView;
    private boolean topViewShowed;
    private RadioButtonCell transposeRadioCell;
    private boolean waitingForKeyboardOpen;

    public interface ChatActivityEnterViewDelegate {
        void needSendTyping();

        void onAttachButtonHidden();

        void onAttachButtonShow();

        void onMessageEditEnd(boolean z);

        void onMessageSend(CharSequence charSequence);

        void onStickersTab(boolean z);

        void onTextChanged(CharSequence charSequence, boolean z);

        void onWindowSizeChanged(int i);
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.11 */
    class AnonymousClass11 implements OnTouchListener {
        final /* synthetic */ int val$color;

        AnonymousClass11(int i) {
            this.val$color = i;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                if (ChatActivityEnterView.this.parentFragment != null) {
                    if (VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                        String str;
                        if (((int) ChatActivityEnterView.this.dialog_id) < 0) {
                            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) ChatActivityEnterView.this.dialog_id)));
                            str = (chat == null || chat.participants_count <= MessagesController.getInstance().groupBigSize) ? "chat_upload_audio" : "bigchat_upload_audio";
                        } else {
                            str = "pm_upload_audio";
                        }
                        if (!MessagesController.isFeatureEnabled(str, ChatActivityEnterView.this.parentFragment)) {
                            return false;
                        }
                    }
                    ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 3);
                    return false;
                }
                ChatActivityEnterView.this.startedDraggingX = Face.UNCOMPUTED_PROBABILITY;
                MediaController.m71a().m147a(ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
                ChatActivityEnterView.this.updateAudioRecordIntefrace();
                ChatActivityEnterView.this.audioSendButton.getParent().requestDisallowInterceptTouchEvent(true);
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (ThemeUtil.m2490b()) {
                    ChatActivityEnterView.this.audioSendButton.setColorFilter(this.val$color, Mode.SRC_IN);
                }
                ChatActivityEnterView.this.startedDraggingX = Face.UNCOMPUTED_PROBABILITY;
                if (MediaController.m71a().m144F() <= 400 && MoboConstants.aT) {
                    ChatActivityEnterView.this.showVoiceSettingsDialog();
                }
                MediaController.m71a().m171d(MoboConstants.f1353t ? 2 : 1);
                ChatActivityEnterView.this.recordingAudio = false;
                ChatActivityEnterView.this.updateAudioRecordIntefrace();
            } else if (motionEvent.getAction() == 2 && ChatActivityEnterView.this.recordingAudio) {
                float x = motionEvent.getX();
                if (x < (-ChatActivityEnterView.this.distCanMove)) {
                    MediaController.m71a().m171d(0);
                    ChatActivityEnterView.this.recordingAudio = false;
                    ChatActivityEnterView.this.updateAudioRecordIntefrace();
                }
                float x2 = x + ChatActivityEnterView.this.audioSendButton.getX();
                LayoutParams layoutParams = (LayoutParams) ChatActivityEnterView.this.slideText.getLayoutParams();
                if (ChatActivityEnterView.this.startedDraggingX != Face.UNCOMPUTED_PROBABILITY) {
                    float access$3300 = x2 - ChatActivityEnterView.this.startedDraggingX;
                    ChatActivityEnterView.this.recordCircle.setTranslationX(access$3300);
                    layoutParams.leftMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) + ((int) access$3300);
                    ChatActivityEnterView.this.slideText.setLayoutParams(layoutParams);
                    access$3300 = (access$3300 / ChatActivityEnterView.this.distCanMove) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    if (access$3300 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        access$3300 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    } else if (access$3300 < 0.0f) {
                        access$3300 = 0.0f;
                    }
                    ChatActivityEnterView.this.slideText.setAlpha(access$3300);
                }
                if (x2 <= (ChatActivityEnterView.this.slideText.getX() + ((float) ChatActivityEnterView.this.slideText.getWidth())) + ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) && ChatActivityEnterView.this.startedDraggingX == Face.UNCOMPUTED_PROBABILITY) {
                    ChatActivityEnterView.this.startedDraggingX = x2;
                    ChatActivityEnterView.this.distCanMove = ((float) ((ChatActivityEnterView.this.recordPanel.getMeasuredWidth() - ChatActivityEnterView.this.slideText.getMeasuredWidth()) - AndroidUtilities.dp(48.0f))) / 2.0f;
                    if (ChatActivityEnterView.this.distCanMove <= 0.0f) {
                        ChatActivityEnterView.this.distCanMove = (float) AndroidUtilities.dp(80.0f);
                    } else if (ChatActivityEnterView.this.distCanMove > ((float) AndroidUtilities.dp(80.0f))) {
                        ChatActivityEnterView.this.distCanMove = (float) AndroidUtilities.dp(80.0f);
                    }
                }
                if (layoutParams.leftMargin > AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) {
                    layoutParams.leftMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
                    ChatActivityEnterView.this.recordCircle.setTranslationX(0.0f);
                    ChatActivityEnterView.this.slideText.setLayoutParams(layoutParams);
                    ChatActivityEnterView.this.slideText.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    ChatActivityEnterView.this.startedDraggingX = Face.UNCOMPUTED_PROBABILITY;
                }
            }
            view.onTouchEvent(motionEvent);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.14 */
    class AnonymousClass14 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$openKeyboard;

        AnonymousClass14(boolean z) {
            this.val$openKeyboard = z;
        }

        public void onAnimationCancel(Animator animator) {
            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator)) {
                ChatActivityEnterView.this.currentTopViewAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator)) {
                if (ChatActivityEnterView.this.recordedAudioPanel.getVisibility() != 0 && (!ChatActivityEnterView.this.forceShowSendButton || this.val$openKeyboard)) {
                    ChatActivityEnterView.this.openKeyboard();
                }
                ChatActivityEnterView.this.currentTopViewAnimation = null;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.1 */
    class C12981 implements Runnable {
        C12981() {
        }

        public void run() {
            if (ChatActivityEnterView.this.messageEditText != null && ChatActivityEnterView.this.waitingForKeyboardOpen && !ChatActivityEnterView.this.keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow) {
                ChatActivityEnterView.this.messageEditText.requestFocus();
                AndroidUtilities.showKeyboard(ChatActivityEnterView.this.messageEditText);
                AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable, 100);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.25 */
    class AnonymousClass25 implements OnClickListener {
        final /* synthetic */ KeyboardButton val$button;
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass25(MessageObject messageObject, KeyboardButton keyboardButton) {
            this.val$messageObject = messageObject;
            this.val$button = keyboardButton;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                SendMessagesHelper.getInstance().sendCurrentLocation(this.val$messageObject, this.val$button);
                return;
            }
            ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            ChatActivityEnterView.this.pendingMessageObject = this.val$messageObject;
            ChatActivityEnterView.this.pendingLocationButton = this.val$button;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.26 */
    class AnonymousClass26 implements DialogsActivityDelegate {
        final /* synthetic */ KeyboardButton val$button;
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass26(MessageObject messageObject, KeyboardButton keyboardButton) {
            this.val$messageObject = messageObject;
            this.val$button = keyboardButton;
        }

        public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
            int i = this.val$messageObject.messageOwner.from_id;
            if (this.val$messageObject.messageOwner.via_bot_id != 0) {
                i = this.val$messageObject.messageOwner.via_bot_id;
            }
            User user = MessagesController.getInstance().getUser(Integer.valueOf(i));
            if (user == null) {
                dialogsActivity.finishFragment();
                return;
            }
            DraftQuery.saveDraft(j, "@" + user.username + " " + this.val$button.query, null, null, true);
            if (j != ChatActivityEnterView.this.dialog_id) {
                i = (int) j;
                if (i != 0) {
                    Bundle bundle = new Bundle();
                    if (i > 0) {
                        bundle.putInt("user_id", i);
                    } else if (i < 0) {
                        bundle.putInt("chat_id", -i);
                    }
                    if (MessagesController.checkCanOpenChat(bundle, dialogsActivity)) {
                        if (!ChatActivityEnterView.this.parentFragment.presentFragment(new ChatActivity(bundle), true)) {
                            dialogsActivity.finishFragment();
                            return;
                        } else if (!AndroidUtilities.isTablet()) {
                            ChatActivityEnterView.this.parentFragment.removeSelfFromStack();
                            return;
                        } else {
                            return;
                        }
                    }
                    return;
                }
                dialogsActivity.finishFragment();
                return;
            }
            dialogsActivity.finishFragment();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.28 */
    class AnonymousClass28 implements StickersAlertDelegate {
        final /* synthetic */ Document val$doc;

        AnonymousClass28(Document document) {
            this.val$doc = document;
        }

        public void onStickerSelected(Document document) {
            SendMessagesHelper.getInstance().sendSticker(this.val$doc, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
            if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                MessagesController.getInstance().saveGif(this.val$doc);
            }
            if (ChatActivityEnterView.this.delegate != null) {
                ChatActivityEnterView.this.delegate.onMessageSend(null);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.29 */
    class AnonymousClass29 implements View.OnClickListener {
        final /* synthetic */ Editor val$editor;

        AnonymousClass29(Editor editor) {
            this.val$editor = editor;
        }

        public void onClick(View view) {
            this.val$editor.putInt("voice_changer_type", 0);
            this.val$editor.commit();
            MoboConstants.m1379a();
            ChatActivityEnterView.this.updateVoiceChangerInterface();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.2 */
    class C13002 extends ImageView {
        C13002(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if ((ChatActivityEnterView.this.emojiView == null || ChatActivityEnterView.this.emojiView.getVisibility() != 0) && !StickersQuery.getUnreadStickerSets().isEmpty() && ChatActivityEnterView.this.dotDrawable != null && !MoboConstants.aP) {
                int width = (canvas.getWidth() / 2) + AndroidUtilities.dp(4.0f);
                int height = (canvas.getHeight() / 2) - AndroidUtilities.dp(13.0f);
                ChatActivityEnterView.this.dotDrawable.setBounds(width, height, ChatActivityEnterView.this.dotDrawable.getIntrinsicWidth() + width, ChatActivityEnterView.this.dotDrawable.getIntrinsicHeight() + height);
                ChatActivityEnterView.this.dotDrawable.draw(canvas);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.30 */
    class AnonymousClass30 implements View.OnClickListener {
        final /* synthetic */ Editor val$editor;

        /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.30.1 */
        class C13011 implements OnClickListener {
            C13011() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                int i2 = 0;
                switch (i) {
                    case VideoPlayer.TRACK_DEFAULT /*0*/:
                        i2 = 22050;
                        break;
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                        i2 = 33075;
                        break;
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        i2 = 11025;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        i2 = UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS;
                        break;
                }
                AnonymousClass30.this.val$editor.putInt("record_speed", i2);
                AnonymousClass30.this.val$editor.commit();
                MoboConstants.m1379a();
                ChatActivityEnterView.this.updateVoiceChangerInterface();
            }
        }

        AnonymousClass30(Editor editor) {
            this.val$editor = editor;
        }

        public void onClick(View view) {
            this.val$editor.putInt("voice_changer_type", 1);
            this.val$editor.commit();
            MoboConstants.m1379a();
            ChatActivityEnterView.this.updateVoiceChangerInterface();
            Builder builder = new Builder(ChatActivityEnterView.this.parentActivity);
            List arrayList = new ArrayList();
            arrayList.add(LocaleController.getString("VeryLow", C0338R.string.VeryLow));
            arrayList.add(LocaleController.getString("Low", C0338R.string.Low));
            arrayList.add(LocaleController.getString("High", C0338R.string.High));
            arrayList.add(LocaleController.getString("VeryHigh", C0338R.string.VeryHigh));
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new C13011());
            builder.create().show();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.31 */
    class AnonymousClass31 implements View.OnClickListener {
        final /* synthetic */ Editor val$editor;

        AnonymousClass31(Editor editor) {
            this.val$editor = editor;
        }

        public void onClick(View view) {
            this.val$editor.putInt("voice_changer_type", 2);
            this.val$editor.commit();
            MoboConstants.m1379a();
            ChatActivityEnterView.this.updateVoiceChangerInterface();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.32 */
    class AnonymousClass32 implements View.OnClickListener {
        final /* synthetic */ Editor val$editor;

        AnonymousClass32(Editor editor) {
            this.val$editor = editor;
        }

        public void onClick(View view) {
            this.val$editor.putInt("voice_changer_type", 3);
            this.val$editor.commit();
            MoboConstants.m1379a();
            ChatActivityEnterView.this.updateVoiceChangerInterface();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.33 */
    class AnonymousClass33 implements View.OnClickListener {
        final /* synthetic */ Editor val$editor;

        AnonymousClass33(Editor editor) {
            this.val$editor = editor;
        }

        public void onClick(View view) {
            this.val$editor.putInt("voice_changer_type", 4);
            this.val$editor.commit();
            MoboConstants.m1379a();
            ChatActivityEnterView.this.updateVoiceChangerInterface();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.34 */
    class AnonymousClass34 implements View.OnClickListener {
        final /* synthetic */ Editor val$editor;

        /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.34.1 */
        class C13021 implements OnClickListener {
            final /* synthetic */ NumberPicker val$numberPicker;

            C13021(NumberPicker numberPicker) {
                this.val$numberPicker = numberPicker;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                AnonymousClass34.this.val$editor.putInt("transpose_semitone", this.val$numberPicker.getValue());
                AnonymousClass34.this.val$editor.commit();
                MoboConstants.m1379a();
                ChatActivityEnterView.this.updateVoiceChangerInterface();
            }
        }

        AnonymousClass34(Editor editor) {
            this.val$editor = editor;
        }

        public void onClick(View view) {
            this.val$editor.putInt("voice_changer_type", 5);
            this.val$editor.commit();
            MoboConstants.m1379a();
            ChatActivityEnterView.this.updateVoiceChangerInterface();
            Builder builder = new Builder(ChatActivityEnterView.this.parentActivity);
            builder.setTitle(LocaleController.getString("Semitone", C0338R.string.Semitone));
            View numberPicker = new NumberPicker(ChatActivityEnterView.this.parentActivity);
            numberPicker.setMinValue(-5);
            numberPicker.setMaxValue(10);
            numberPicker.setValue(MoboConstants.aS);
            builder.setView(numberPicker);
            builder.setNegativeButton(LocaleController.getString("Done", C0338R.string.Done), new C13021(numberPicker));
            builder.create().show();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.35 */
    class AnonymousClass35 implements View.OnClickListener {
        final /* synthetic */ Editor val$editor;

        AnonymousClass35(Editor editor) {
            this.val$editor = editor;
        }

        public void onClick(View view) {
            boolean z = true;
            boolean z2 = MoboConstants.f1353t;
            this.val$editor.putBoolean("confirm_before_send_voice", !z2);
            this.val$editor.commit();
            MoboConstants.m1379a();
            if (view instanceof TextDetailCheckCell) {
                TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) view;
                if (z2) {
                    z = false;
                }
                textDetailCheckCell.setChecked(z);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.3 */
    class C13033 implements View.OnClickListener {
        C13033() {
        }

        public void onClick(View view) {
            boolean z = true;
            if (ChatActivityEnterView.this.isPopupShowing() && ChatActivityEnterView.this.currentPopupContentType == 0) {
                ChatActivityEnterView.this.openKeyboardInternal();
                ChatActivityEnterView.this.removeGifFromInputField();
                return;
            }
            ChatActivityEnterView.this.showPopup(1, 0);
            EmojiViewInf access$700 = ChatActivityEnterView.this.emojiView;
            if (ChatActivityEnterView.this.messageEditText.length() <= 0) {
                z = false;
            }
            access$700.onOpen(z);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.4 */
    class C13044 implements OnKeyListener {
        boolean ctrlPressed;

        C13044() {
            this.ctrlPressed = false;
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            boolean z = false;
            if (i == 4 && !ChatActivityEnterView.this.keyboardVisible && ChatActivityEnterView.this.isPopupShowing()) {
                if (keyEvent.getAction() != 1) {
                    return true;
                }
                if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                }
                ChatActivityEnterView.this.showPopup(0, 0);
                ChatActivityEnterView.this.removeGifFromInputField();
                return true;
            } else if (i == 66 && ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0 && ChatActivityEnterView.this.editingMessageObject == null)) {
                ChatActivityEnterView.this.sendMessage();
                return true;
            } else if (i != 113 && i != 114) {
                return false;
            } else {
                if (keyEvent.getAction() == 0) {
                    z = true;
                }
                this.ctrlPressed = z;
                return true;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.5 */
    class C13055 implements OnEditorActionListener {
        boolean ctrlPressed;

        C13055() {
            this.ctrlPressed = false;
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            boolean z = false;
            if (i == 4) {
                ChatActivityEnterView.this.sendMessage();
                return true;
            }
            if (keyEvent != null && i == 0) {
                if ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0 && ChatActivityEnterView.this.editingMessageObject == null) {
                    ChatActivityEnterView.this.sendMessage();
                    return true;
                } else if (i == 113 || i == 114) {
                    if (keyEvent.getAction() == 0) {
                        z = true;
                    }
                    this.ctrlPressed = z;
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.6 */
    class C13066 implements TextWatcher {
        boolean processChange;

        C13066() {
            this.processChange = false;
        }

        public void afterTextChanged(Editable editable) {
            if (ChatActivityEnterView.this.innerTextChange == 0) {
                if (ChatActivityEnterView.this.sendByEnter && editable.length() > 0 && editable.charAt(editable.length() - 1) == '\n' && ChatActivityEnterView.this.editingMessageObject == null) {
                    ChatActivityEnterView.this.sendMessage();
                }
                if (this.processChange) {
                    ImageSpan[] imageSpanArr = (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class);
                    for (Object removeSpan : imageSpanArr) {
                        editable.removeSpan(removeSpan);
                    }
                    Emoji.replaceEmoji(editable, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    this.processChange = false;
                }
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (ChatActivityEnterView.this.innerTextChange != 1) {
                ChatActivityEnterView.this.checkSendButton(true);
                CharSequence trimmedString = AndroidUtilities.getTrimmedString(charSequence.toString());
                if (!(ChatActivityEnterView.this.delegate == null || ChatActivityEnterView.this.ignoreTextChange)) {
                    if (i3 > 2 || charSequence == null || charSequence.length() == 0) {
                        ChatActivityEnterView.this.messageWebPageSearch = true;
                    }
                    ChatActivityEnterViewDelegate access$1700 = ChatActivityEnterView.this.delegate;
                    boolean z = i2 > i3 + 1 || i3 - i2 > 2;
                    access$1700.onTextChanged(charSequence, z);
                }
                if (!(ChatActivityEnterView.this.innerTextChange == 2 || i2 == i3 || i3 - i2 <= 1)) {
                    this.processChange = true;
                }
                if (ChatActivityEnterView.this.editingMessageObject == null && !ChatActivityEnterView.this.canWriteToChannel && trimmedString.length() != 0 && ChatActivityEnterView.this.lastTypingTimeSend < System.currentTimeMillis() - HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS && !ChatActivityEnterView.this.ignoreTextChange) {
                    int currentTime = ConnectionsManager.getInstance().getCurrentTime();
                    User user = null;
                    if (((int) ChatActivityEnterView.this.dialog_id) > 0) {
                        user = MessagesController.getInstance().getUser(Integer.valueOf((int) ChatActivityEnterView.this.dialog_id));
                    }
                    if (user != null) {
                        if (user.id == UserConfig.getClientUserId()) {
                            return;
                        }
                        if (!(user.status == null || user.status.expires >= currentTime || MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(user.id)))) {
                            return;
                        }
                    }
                    ChatActivityEnterView.this.lastTypingTimeSend = System.currentTimeMillis();
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.needSendTyping();
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.7 */
    class C13077 implements View.OnClickListener {
        C13077() {
        }

        public void onClick(View view) {
            if (ChatActivityEnterView.this.botReplyMarkup != null) {
                if (ChatActivityEnterView.this.isPopupShowing() && ChatActivityEnterView.this.currentPopupContentType == 1) {
                    if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                    }
                    ChatActivityEnterView.this.openKeyboardInternal();
                    return;
                }
                ChatActivityEnterView.this.showPopup(1, 1);
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("hidekeyboard_" + ChatActivityEnterView.this.dialog_id).commit();
            } else if (ChatActivityEnterView.this.hasBotCommands) {
                ChatActivityEnterView.this.setFieldText("/");
                ChatActivityEnterView.this.openKeyboard();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.8 */
    class C13088 implements View.OnClickListener {
        final /* synthetic */ int val$color;
        private Toast visibleToast;

        C13088(int i) {
            this.val$color = i;
        }

        public void onClick(View view) {
            ChatActivityEnterView.this.silent = !ChatActivityEnterView.this.silent;
            ChatActivityEnterView.this.notifyButton.setImageResource(ChatActivityEnterView.this.silent ? C0338R.drawable.notify_members_off : C0338R.drawable.notify_members_on);
            if (ThemeUtil.m2490b()) {
                ChatActivityEnterView.this.notifyButton.setColorFilter(this.val$color, Mode.SRC_IN);
            }
            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putBoolean("silent_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.silent).commit();
            NotificationsController.updateServerNotificationsSettings(ChatActivityEnterView.this.dialog_id);
            try {
                if (this.visibleToast != null) {
                    this.visibleToast.cancel();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (ChatActivityEnterView.this.silent) {
                this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOff", C0338R.string.ChannelNotifyMembersInfoOff), 0);
            } else {
                this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOn", C0338R.string.ChannelNotifyMembersInfoOn), 0);
            }
            this.visibleToast.show();
            ChatActivityEnterView.this.updateFieldHint();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.9 */
    class C13099 implements View.OnClickListener {
        C13099() {
        }

        public void onClick(View view) {
            MessageObject j = MediaController.m71a().m182j();
            if (j != null && j == ChatActivityEnterView.this.audioToSendMessageObject) {
                MediaController.m71a().m155a(true, true);
            }
            if (ChatActivityEnterView.this.audioToSendPath != null) {
                new File(ChatActivityEnterView.this.audioToSendPath).delete();
            }
            ChatActivityEnterView.this.hideRecordedAudioPanel();
            ChatActivityEnterView.this.checkSendButton(true);
        }
    }

    public static class CustomFrameLayout extends FrameLayout {
        public CustomFrameLayout(Context context) {
            super(context);
            setWillNotDraw(false);
        }
    }

    public class EditTextCaption extends EditText {
        private String caption;
        private StaticLayout captionLayout;
        private Object editor;
        private Field editorField;
        private Drawable[] mCursorDrawable;
        private Field mCursorDrawableField;
        private int triesCount;
        private int userNameLength;
        private int xOffset;
        private int yOffset;

        public EditTextCaption(Context context) {
            super(context);
            this.triesCount = 0;
            try {
                Field declaredField = TextView.class.getDeclaredField("mEditor");
                declaredField.setAccessible(true);
                this.editor = declaredField.get(this);
                Class cls = Class.forName("android.widget.Editor");
                this.editorField = cls.getDeclaredField("mShowCursor");
                this.editorField.setAccessible(true);
                this.mCursorDrawableField = cls.getDeclaredField("mCursorDrawable");
                this.mCursorDrawableField.setAccessible(true);
                this.mCursorDrawable = (Drawable[]) this.mCursorDrawableField.get(this.editor);
            } catch (Throwable th) {
            }
        }

        protected void onDraw(Canvas canvas) {
            try {
                super.onDraw(canvas);
                if (this.captionLayout != null && this.userNameLength == length()) {
                    Paint paint = getPaint();
                    int color = getPaint().getColor();
                    paint.setColor(-5066062);
                    canvas.save();
                    canvas.translate((float) this.xOffset, (float) this.yOffset);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                    paint.setColor(color);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            try {
                if (this.editorField != null && this.mCursorDrawable != null && this.mCursorDrawable[0] != null) {
                    if (((SystemClock.uptimeMillis() - this.editorField.getLong(this.editor)) % 1000 < 500 ? 1 : null) != null) {
                        canvas.save();
                        canvas.translate(0.0f, (float) getPaddingTop());
                        this.mCursorDrawable[0].draw(canvas);
                        canvas.restore();
                    }
                }
            } catch (Throwable th) {
            }
        }

        @SuppressLint({"DrawAllocation"})
        protected void onMeasure(int i, int i2) {
            try {
                super.onMeasure(i, i2);
            } catch (Throwable e) {
                setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(51.0f));
                FileLog.m18e("tmessages", e);
            }
            this.captionLayout = null;
            if (this.caption != null && this.caption.length() > 0) {
                CharSequence text = getText();
                if (text.length() > 1 && text.charAt(0) == '@') {
                    int indexOf = TextUtils.indexOf(text, ' ');
                    if (indexOf != -1) {
                        TextPaint paint = getPaint();
                        int ceil = (int) Math.ceil((double) paint.measureText(text, 0, indexOf + 1));
                        int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                        this.userNameLength = text.subSequence(0, indexOf + 1).length();
                        CharSequence ellipsize = TextUtils.ellipsize(this.caption, paint, (float) (measuredWidth - ceil), TruncateAt.END);
                        this.xOffset = ceil;
                        try {
                            this.captionLayout = new StaticLayout(ellipsize, getPaint(), measuredWidth - ceil, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
                            if (this.captionLayout.getLineCount() > 0) {
                                this.xOffset = (int) (((float) this.xOffset) + (-this.captionLayout.getLineLeft(0)));
                            }
                            this.yOffset = ((getMeasuredHeight() - this.captionLayout.getLineBottom(0)) / 2) + AndroidUtilities.dp(0.5f);
                        } catch (Throwable e2) {
                            FileLog.m18e("tmessages", e2);
                        }
                    }
                }
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean z = false;
            if (ChatActivityEnterView.this.isPopupShowing() && motionEvent.getAction() == 0) {
                ChatActivityEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? z : 2, z);
                ChatActivityEnterView.this.openKeyboardInternal();
            }
            try {
                z = super.onTouchEvent(motionEvent);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            return z;
        }

        public void setCaption(String str) {
            if ((this.caption != null && this.caption.length() != 0) || (str != null && str.length() != 0)) {
                if (this.caption == null || str == null || !this.caption.equals(str)) {
                    this.caption = str;
                    if (this.caption != null) {
                        this.caption = this.caption.replace('\n', ' ');
                    }
                    requestLayout();
                }
            }
        }
    }

    private class RecordCircle extends View {
        private float amplitude;
        private float animateAmplitudeDiff;
        private float animateToAmplitude;
        private long lastUpdateTime;
        private Drawable micDrawable;
        private Paint paint;
        private Paint paintRecord;
        private float scale;

        public RecordCircle(Context context) {
            super(context);
            this.paint = new Paint(1);
            this.paintRecord = new Paint(1);
            this.paint.setColor(-11037236);
            this.paintRecord.setColor(218103808);
            this.micDrawable = getResources().getDrawable(C0338R.drawable.mic_pressed);
            if (ThemeUtil.m2490b()) {
                this.paint.setColor(AdvanceTheme.f2491b);
            }
        }

        public float getScale() {
            return this.scale;
        }

        protected void onDraw(Canvas canvas) {
            float f;
            float f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            int measuredWidth = getMeasuredWidth() / 2;
            int measuredHeight = getMeasuredHeight() / 2;
            if (this.scale <= 0.5f) {
                f2 = this.scale / 0.5f;
                f = f2;
            } else {
                f = this.scale <= AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - (((this.scale - 0.5f) / 0.25f) * 0.1f) : 0.9f + (((this.scale - AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION) / 0.25f) * 0.1f);
            }
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            if (this.animateToAmplitude != this.amplitude) {
                this.amplitude = (((float) currentTimeMillis) * this.animateAmplitudeDiff) + this.amplitude;
                if (this.animateAmplitudeDiff > 0.0f) {
                    if (this.amplitude > this.animateToAmplitude) {
                        this.amplitude = this.animateToAmplitude;
                    }
                } else if (this.amplitude < this.animateToAmplitude) {
                    this.amplitude = this.animateToAmplitude;
                }
                invalidate();
            }
            this.lastUpdateTime = System.currentTimeMillis();
            if (this.amplitude != 0.0f) {
                canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, (((float) AndroidUtilities.dp(42.0f)) + (((float) AndroidUtilities.dp(20.0f)) * this.amplitude)) * this.scale, this.paintRecord);
            }
            canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, f * ((float) AndroidUtilities.dp(42.0f)), this.paint);
            this.micDrawable.setBounds(measuredWidth - (this.micDrawable.getIntrinsicWidth() / 2), measuredHeight - (this.micDrawable.getIntrinsicHeight() / 2), measuredWidth + (this.micDrawable.getIntrinsicWidth() / 2), measuredHeight + (this.micDrawable.getIntrinsicHeight() / 2));
            this.micDrawable.setAlpha((int) (f2 * 255.0f));
            this.micDrawable.draw(canvas);
        }

        public void setAmplitude(double d) {
            this.animateToAmplitude = ((float) Math.min(100.0d, d)) / 100.0f;
            this.animateAmplitudeDiff = (this.animateToAmplitude - this.amplitude) / 150.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            invalidate();
        }

        public void setScale(float f) {
            this.scale = f;
            invalidate();
        }
    }

    private class RecordDot extends View {
        private float alpha;
        private Drawable dotDrawable;
        private boolean isIncr;
        private long lastUpdateTime;

        public RecordDot(Context context) {
            super(context);
            this.dotDrawable = getResources().getDrawable(C0338R.drawable.rec);
        }

        protected void onDraw(Canvas canvas) {
            this.dotDrawable.setBounds(0, 0, AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f));
            this.dotDrawable.setAlpha((int) (255.0f * this.alpha));
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            if (this.isIncr) {
                this.alpha = (((float) currentTimeMillis) / 400.0f) + this.alpha;
                if (this.alpha >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    this.alpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                    this.isIncr = false;
                }
            } else {
                this.alpha -= ((float) currentTimeMillis) / 400.0f;
                if (this.alpha <= 0.0f) {
                    this.alpha = 0.0f;
                    this.isIncr = true;
                }
            }
            this.lastUpdateTime = System.currentTimeMillis();
            this.dotDrawable.draw(canvas);
            invalidate();
        }

        public void resetAlpha() {
            this.alpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            this.lastUpdateTime = System.currentTimeMillis();
            this.isIncr = false;
            invalidate();
        }
    }

    private class SeekBarWaveformView extends View {
        private SeekBarWaveform seekBarWaveform;

        /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.SeekBarWaveformView.1 */
        class C13101 implements SeekBarDelegate {
            final /* synthetic */ ChatActivityEnterView val$this$0;

            C13101(ChatActivityEnterView chatActivityEnterView) {
                this.val$this$0 = chatActivityEnterView;
            }

            public void onSeekBarDrag(float f) {
                if (ChatActivityEnterView.this.audioToSendMessageObject != null) {
                    ChatActivityEnterView.this.audioToSendMessageObject.audioProgress = f;
                    MediaController.m71a().m159a(ChatActivityEnterView.this.audioToSendMessageObject, f);
                }
            }
        }

        public SeekBarWaveformView(Context context) {
            super(context);
            this.seekBarWaveform = new SeekBarWaveform(context);
            this.seekBarWaveform.setColors(-6107400, -1, -6107400);
            this.seekBarWaveform.setDelegate(new C13101(ChatActivityEnterView.this));
        }

        public boolean isDragging() {
            return this.seekBarWaveform.isDragging();
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            this.seekBarWaveform.draw(canvas);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            this.seekBarWaveform.setSize(i3 - i, i4 - i2);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean onTouch = this.seekBarWaveform.onTouch(motionEvent.getAction(), motionEvent.getX(), motionEvent.getY());
            if (onTouch) {
                if (motionEvent.getAction() == 0) {
                    ChatActivityEnterView.this.requestDisallowInterceptTouchEvent(true);
                }
                invalidate();
            }
            return onTouch || super.onTouchEvent(motionEvent);
        }

        public void setProgress(float f) {
            this.seekBarWaveform.setProgress(f);
            invalidate();
        }

        public void setWaveform(byte[] bArr) {
            this.seekBarWaveform.setWaveform(bArr);
            invalidate();
        }
    }

    public ChatActivityEnterView(Activity activity, SizeNotifierFrameLayout sizeNotifierFrameLayout, ChatActivity chatActivity, boolean z) {
        this(activity, sizeNotifierFrameLayout, chatActivity, z, false);
    }

    public ChatActivityEnterView(Activity activity, SizeNotifierFrameLayout sizeNotifierFrameLayout, ChatActivity chatActivity, boolean z, boolean z2) {
        super(activity);
        this.currentPopupContentType = -1;
        this.isPaused = true;
        this.startedDraggingX = Face.UNCOMPUTED_PROBABILITY;
        this.distCanMove = (float) AndroidUtilities.dp(80.0f);
        this.messageWebPageSearch = true;
        this.openKeyboardRunnable = new C12981();
        this.hasTwoAttach = z2;
        this.backgroundDrawable = activity.getResources().getDrawable(C0338R.drawable.compose_panel);
        this.dotDrawable = activity.getResources().getDrawable(C0338R.drawable.bluecircle);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStartError);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStopped);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioRouteChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
        this.parentActivity = activity;
        this.parentFragment = chatActivity;
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        this.sizeNotifierLayout.setDelegate(this);
        this.sendByEnter = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("send_by_enter", false);
        this.textFieldContainer = new LinearLayout(activity);
        this.textFieldContainer.setOrientation(0);
        addView(this.textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 2.0f, 0.0f, 0.0f));
        View frameLayout = new FrameLayout(activity);
        this.textFieldContainer.addView(frameLayout, LayoutHelper.createLinear(0, -2, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.emojiButton = new C13002(activity);
        int i = AdvanceTheme.f2491b;
        int c = AdvanceTheme.m2286c(AdvanceTheme.bG, -5395027);
        if (ThemeUtil.m2490b()) {
            this.emojiButton.setColorFilter(c, Mode.SRC_IN);
        }
        this.emojiButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.emojiButton.setPadding(0, AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), 0, 0);
        if (VERSION.SDK_INT >= 21) {
            this.emojiButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.INPUT_FIELD_SELECTOR_COLOR));
        }
        setEmojiButtonImage();
        frameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48.0f, 83, 3.0f, 0.0f, 0.0f, 0.0f));
        this.emojiButton.setOnClickListener(new C13033());
        this.messageEditText = new EditTextCaption(activity);
        updateFieldHint();
        this.messageEditText.setTypeface(FontUtil.m1176a().m1161d());
        this.messageEditText.setImeOptions(268435456);
        this.messageEditText.setInputType((this.messageEditText.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS) | AccessibilityNodeInfoCompat.ACTION_SET_SELECTION);
        this.messageEditText.setSingleLine(false);
        this.messageEditText.setMaxLines(4);
        this.messageEditText.setTextSize(1, 18.0f);
        this.messageEditText.setGravity(80);
        this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
        this.messageEditText.setBackgroundDrawable(null);
        this.messageEditText.setTextColor(Theme.MSG_TEXT_COLOR);
        this.messageEditText.setHintTextColor(-5066062);
        View view = this.messageEditText;
        float f = z ? z2 ? 98.0f : 50.0f : 2.0f;
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -2.0f, 80, 52.0f, 0.0f, f, 0.0f));
        this.messageEditText.setOnKeyListener(new C13044());
        this.messageEditText.setOnEditorActionListener(new C13055());
        this.messageEditText.addTextChangedListener(new C13066());
        try {
            Field declaredField = TextView.class.getDeclaredField("mCursorDrawableRes");
            declaredField.setAccessible(true);
            declaredField.set(this.messageEditText, Integer.valueOf(C0338R.drawable.field_carret));
        } catch (Exception e) {
        }
        if (z) {
            this.attachButton = new LinearLayout(activity);
            this.attachButton.setOrientation(0);
            this.attachButton.setEnabled(false);
            this.attachButton.setPivotX((float) AndroidUtilities.dp(48.0f));
            frameLayout.addView(this.attachButton, LayoutHelper.createFrame(-2, 48, 85));
            this.botButton = new ImageView(activity);
            this.botButton.setImageResource(C0338R.drawable.bot_keyboard2);
            this.botButton.setScaleType(ScaleType.CENTER);
            this.botButton.setVisibility(8);
            if (VERSION.SDK_INT >= 21) {
                this.botButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.INPUT_FIELD_SELECTOR_COLOR));
            }
            this.attachButton.addView(this.botButton, LayoutHelper.createLinear(48, 48));
            this.botButton.setOnClickListener(new C13077());
            this.notifyButton = new ImageView(activity);
            this.notifyButton.setImageResource(this.silent ? C0338R.drawable.notify_members_off : C0338R.drawable.notify_members_on);
            if (ThemeUtil.m2490b()) {
                this.notifyButton.setColorFilter(c, Mode.SRC_IN);
            }
            this.notifyButton.setScaleType(ScaleType.CENTER);
            this.notifyButton.setVisibility(this.canWriteToChannel ? 0 : 8);
            if (VERSION.SDK_INT >= 21) {
                this.notifyButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.INPUT_FIELD_SELECTOR_COLOR));
            }
            this.attachButton.addView(this.notifyButton, LayoutHelper.createLinear(48, 48));
            this.notifyButton.setOnClickListener(new C13088(c));
        }
        this.recordedAudioPanel = new FrameLayout(activity);
        this.recordedAudioPanel.setVisibility(this.audioToSend == null ? 8 : 0);
        this.recordedAudioPanel.setBackgroundColor(-1);
        this.recordedAudioPanel.setFocusable(true);
        this.recordedAudioPanel.setFocusableInTouchMode(true);
        this.recordedAudioPanel.setClickable(true);
        frameLayout.addView(this.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
        View imageView = new ImageView(activity);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageResource(C0338R.drawable.ic_ab_fwd_delete);
        if (ThemeUtil.m2490b()) {
            imageView.setColorFilter(AdvanceTheme.m2286c(AdvanceTheme.bG, Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR), Mode.SRC_IN);
        }
        this.recordedAudioPanel.addView(imageView, LayoutHelper.createFrame(48, 48.0f));
        imageView.setOnClickListener(new C13099());
        view = new View(activity);
        view.setBackgroundResource(C0338R.drawable.recorded);
        if (ThemeUtil.m2490b()) {
            int i2 = AdvanceTheme.bH;
            i2 = Color.argb(NalUnitUtil.EXTENDED_SAR, Color.red(i2), Color.green(i2), Color.blue(i2));
            view.getBackground().setColorFilter(i2 == -1 ? i : AdvanceTheme.m2283b(i2, 37), Mode.SRC_IN);
        }
        this.recordedAudioPanel.addView(view, LayoutHelper.createFrame(-1, 32.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioSeekBar = new SeekBarWaveformView(activity);
        this.recordedAudioPanel.addView(this.recordedAudioSeekBar, LayoutHelper.createFrame(-1, 32.0f, 19, 92.0f, 0.0f, 52.0f, 0.0f));
        this.recordedAudioPlayButton = new ImageView(activity);
        this.recordedAudioPlayButton.setImageResource(C0338R.drawable.s_player_play_states);
        this.recordedAudioPlayButton.setScaleType(ScaleType.CENTER);
        this.recordedAudioPanel.addView(this.recordedAudioPlayButton, LayoutHelper.createFrame(48, 48.0f, 83, 48.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ChatActivityEnterView.this.audioToSend != null) {
                    if (!MediaController.m71a().m172d(ChatActivityEnterView.this.audioToSendMessageObject) || MediaController.m71a().m191s()) {
                        ChatActivityEnterView.this.recordedAudioPlayButton.setImageResource(C0338R.drawable.s_player_pause_states);
                        MediaController.m71a().m158a(ChatActivityEnterView.this.audioToSendMessageObject);
                        return;
                    }
                    MediaController.m71a().m166b(ChatActivityEnterView.this.audioToSendMessageObject);
                    ChatActivityEnterView.this.recordedAudioPlayButton.setImageResource(C0338R.drawable.s_player_play_states);
                }
            }
        });
        this.recordedAudioTimeTextView = new TextView(activity);
        this.recordedAudioTimeTextView.setTextColor(-1);
        this.recordedAudioTimeTextView.setTextSize(1, 13.0f);
        this.recordedAudioTimeTextView.setText("0:13");
        this.recordedAudioPanel.addView(this.recordedAudioTimeTextView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 13.0f, 0.0f));
        this.recordPanel = new FrameLayout(activity);
        this.recordPanel.setVisibility(8);
        this.recordPanel.setBackgroundColor(-1);
        frameLayout.addView(this.recordPanel, LayoutHelper.createFrame(-1, 48, 80));
        this.slideText = new LinearLayout(activity);
        this.slideText.setOrientation(0);
        this.recordPanel.addView(this.slideText, LayoutHelper.createFrame(-2, -2.0f, 17, BitmapDescriptorFactory.HUE_ORANGE, 0.0f, 0.0f, 0.0f));
        View imageView2 = new ImageView(activity);
        imageView2.setImageResource(C0338R.drawable.slidearrow);
        this.slideText.addView(imageView2, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 0, 0));
        imageView2 = new TextView(activity);
        imageView2.setText(LocaleController.getString("SlideToCancel", C0338R.string.SlideToCancel));
        imageView2.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        imageView2.setTextSize(1, 12.0f);
        imageView2.setTypeface(FontUtil.m1176a().m1161d());
        this.slideText.addView(imageView2, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        imageView2 = new LinearLayout(activity);
        imageView2.setOrientation(0);
        imageView2.setPadding(AndroidUtilities.dp(13.0f), 0, 0, 0);
        imageView2.setBackgroundColor(-1);
        this.recordPanel.addView(imageView2, LayoutHelper.createFrame(-2, -2, 16));
        this.recordDot = new RecordDot(activity);
        imageView2.addView(this.recordDot, LayoutHelper.createLinear(11, 11, 16, 0, 1, 0, 0));
        this.recordTimeText = new TextView(activity);
        this.recordTimeText.setText("00:00");
        this.recordTimeText.setTextColor(-11711413);
        this.recordTimeText.setTextSize(1, 16.0f);
        imageView2.addView(this.recordTimeText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        this.sendButtonContainer = new FrameLayout(activity);
        this.textFieldContainer.addView(this.sendButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        this.audioSendButton = new ImageView(activity);
        this.audioSendButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.audioSendButton.setImageResource(MoboConstants.aQ == 0 ? C0338R.drawable.mic : C0338R.drawable.mic_voicechanger);
        this.audioSendButton.setSoundEffectsEnabled(false);
        if (ThemeUtil.m2490b()) {
            this.audioSendButton.setColorFilter(c, Mode.SRC_IN);
        } else {
            this.audioSendButton.setBackgroundColor(-1);
        }
        this.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        this.sendButtonContainer.addView(this.audioSendButton, LayoutHelper.createFrame(48, 48.0f));
        this.audioSendButton.setOnTouchListener(new AnonymousClass11(c));
        this.recordCircle = new RecordCircle(activity);
        this.recordCircle.setVisibility(8);
        this.sizeNotifierLayout.addView(this.recordCircle, LayoutHelper.createFrame(124, 124.0f, 85, 0.0f, 0.0f, -36.0f, -38.0f));
        this.cancelBotButton = new ImageView(activity);
        this.cancelBotButton.setVisibility(4);
        this.cancelBotButton.setScaleType(ScaleType.CENTER_INSIDE);
        ImageView imageView3 = this.cancelBotButton;
        Drawable closeProgressDrawable2 = new CloseProgressDrawable2();
        this.progressDrawable = closeProgressDrawable2;
        imageView3.setImageDrawable(closeProgressDrawable2);
        this.cancelBotButton.setSoundEffectsEnabled(false);
        this.cancelBotButton.setScaleX(0.1f);
        this.cancelBotButton.setScaleY(0.1f);
        this.cancelBotButton.setAlpha(0.0f);
        this.sendButtonContainer.addView(this.cancelBotButton, LayoutHelper.createFrame(48, 48.0f));
        this.cancelBotButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String obj = ChatActivityEnterView.this.messageEditText.getText().toString();
                int indexOf = obj.indexOf(32);
                if (indexOf == -1 || indexOf == obj.length() - 1) {
                    ChatActivityEnterView.this.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                } else {
                    ChatActivityEnterView.this.setFieldText(obj.substring(0, indexOf + 1));
                }
            }
        });
        this.sendButton = new ImageView(activity);
        this.sendButton.setVisibility(4);
        this.sendButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.sendButton.setImageResource(ThemeUtil.m2485a().m2297k());
        if (ThemeUtil.m2490b()) {
            this.sendButton.setColorFilter(AdvanceTheme.bJ, Mode.SRC_IN);
        }
        this.sendButton.setSoundEffectsEnabled(false);
        this.sendButton.setScaleX(0.1f);
        this.sendButton.setScaleY(0.1f);
        this.sendButton.setAlpha(0.0f);
        this.sendButtonContainer.addView(this.sendButton, LayoutHelper.createFrame(48, 48.0f));
        this.sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ChatActivityEnterView.this.sendMessage();
            }
        });
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
        this.keyboardHeight = sharedPreferences.getInt("kbd_height", AndroidUtilities.dp(200.0f));
        this.keyboardHeightLand = sharedPreferences.getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
        checkSendButton(false);
        initTheme();
    }

    private void createEmojiView() {
        if (this.emojiView == null) {
            if (MoboConstants.aP) {
                this.emojiView = new EmojiView(this.allowStickers, this.allowGifs, this.parentActivity);
            } else {
                this.emojiView = new EmojiView(this.allowStickers, this.allowGifs, this.parentActivity);
            }
            this.emojiView.setVisibility(8);
            this.emojiView.setListener(new Listener() {

                /* renamed from: com.hanista.mobogram.ui.Components.ChatActivityEnterView.27.1 */
                class C12991 implements OnClickListener {
                    C12991() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChatActivityEnterView.this.emojiView.clearRecentEmoji();
                    }
                }

                public boolean onBackspace() {
                    if (ChatActivityEnterView.this.messageEditText.length() == 0) {
                        return false;
                    }
                    ChatActivityEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
                    return true;
                }

                public void onClearEmojiRecent() {
                    if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        Builder builder = new Builder(ChatActivityEnterView.this.parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setMessage(LocaleController.getString("ClearRecentEmoji", C0338R.string.ClearRecentEmoji));
                        builder.setPositiveButton(LocaleController.getString("ClearButton", C0338R.string.ClearButton).toUpperCase(), new C12991());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
                    }
                }

                public void onEmojiSelected(String str) {
                    int selectionEnd = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
                    if (selectionEnd < 0) {
                        selectionEnd = 0;
                    }
                    try {
                        ChatActivityEnterView.this.innerTextChange = 2;
                        CharSequence replaceEmoji = Emoji.replaceEmoji(str, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        ChatActivityEnterView.this.messageEditText.setText(ChatActivityEnterView.this.messageEditText.getText().insert(selectionEnd, replaceEmoji));
                        selectionEnd += replaceEmoji.length();
                        ChatActivityEnterView.this.messageEditText.setSelection(selectionEnd, selectionEnd);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    } finally {
                        ChatActivityEnterView.this.innerTextChange = 0;
                    }
                }

                public void onGifSelected(Document document) {
                    if (MoboConstants.f1352s) {
                        ChatActivityEnterView.this.showStickerSendConfirmDialog(document, true);
                        return;
                    }
                    SendMessagesHelper.getInstance().sendSticker(document, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
                    StickersQuery.addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
                    if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                        MessagesController.getInstance().saveGif(document);
                    }
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onMessageSend(null);
                    }
                }

                public void onGifTab(boolean z) {
                    if (!AndroidUtilities.usingHardwareInput) {
                        if (z) {
                            if (ChatActivityEnterView.this.messageEditText.length() == 0) {
                                ChatActivityEnterView.this.messageEditText.setText("@gif ");
                                ChatActivityEnterView.this.messageEditText.setSelection(ChatActivityEnterView.this.messageEditText.length());
                            }
                        } else if (ChatActivityEnterView.this.messageEditText.getText().toString().equals("@gif ")) {
                            ChatActivityEnterView.this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        }
                    }
                }

                public void onShowStickerSet(StickerSetCovered stickerSetCovered) {
                    if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        InputStickerSet tL_inputStickerSetID = new TL_inputStickerSetID();
                        tL_inputStickerSetID.access_hash = stickerSetCovered.set.access_hash;
                        tL_inputStickerSetID.id = stickerSetCovered.set.id;
                        ChatActivityEnterView.this.parentFragment.showDialog(new StickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, tL_inputStickerSetID, null, ChatActivityEnterView.this));
                    }
                }

                public void onStickerSelected(Document document) {
                    if (MoboConstants.f1352s) {
                        ChatActivityEnterView.this.showStickerSendConfirmDialog(document, false);
                        return;
                    }
                    ChatActivityEnterView.this.onStickerSelected(document);
                    StickersQuery.addRecentSticker(0, document, (int) (System.currentTimeMillis() / 1000));
                    if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                        MessagesController.getInstance().saveGif(document);
                    }
                }

                public void onStickerSetAdd(StickerSetCovered stickerSetCovered) {
                    StickersQuery.removeStickersSet(ChatActivityEnterView.this.parentActivity, stickerSetCovered.set, 2, ChatActivityEnterView.this.parentFragment, false);
                }

                public void onStickerSetRemove(StickerSetCovered stickerSetCovered) {
                    StickersQuery.removeStickersSet(ChatActivityEnterView.this.parentActivity, stickerSetCovered.set, 0, ChatActivityEnterView.this.parentFragment, false);
                }

                public void onStickersSettingsClick() {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(0));
                    }
                }

                public void onStickersTab(boolean z) {
                    ChatActivityEnterView.this.delegate.onStickersTab(z);
                }
            });
            this.emojiView.setVisibility(8);
            this.sizeNotifierLayout.addView(this.emojiView);
        }
    }

    private void hideRecordedAudioPanel() {
        this.audioToSendPath = null;
        this.audioToSend = null;
        this.audioToSendMessageObject = null;
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.recordedAudioPanel, "alpha", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapterProxy() {
            public void onAnimationEnd(Animator animator) {
                ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
            }
        });
        animatorSet.start();
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            this.sendButton.setColorFilter(AdvanceTheme.bJ, Mode.SRC_IN);
            this.messageEditText.setTextColor(AdvanceTheme.m2286c(AdvanceTheme.bI, Theme.MSG_TEXT_COLOR));
            this.messageEditText.setHintTextColor(AdvanceTheme.m2284b(AdvanceTheme.bI, Theme.MSG_TEXT_COLOR, 0.35f));
            this.messageEditText.setTextSize((float) AdvanceTheme.bK);
            int i = AdvanceTheme.bH;
            setBackgroundColor(i);
            this.textFieldContainer.setBackgroundColor(i);
            int i2 = AdvanceTheme.bL;
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
                int i3 = AdvanceTheme.bM;
                Drawable gradientDrawable = new GradientDrawable(orientation, new int[]{i, i3});
                setBackgroundDrawable(gradientDrawable);
                this.textFieldContainer.setBackgroundDrawable(gradientDrawable);
            }
            this.recordedAudioPanel.setBackgroundColor(Color.argb(NalUnitUtil.EXTENDED_SAR, Color.red(i), Color.green(i), Color.blue(i)));
        }
    }

    private void onWindowSizeChanged() {
        int height = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            height -= this.emojiPadding;
        }
        if (this.delegate != null) {
            this.delegate.onWindowSizeChanged(height);
        }
        if (this.topView == null) {
            return;
        }
        if (height < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
            if (this.allowShowTopView) {
                this.allowShowTopView = false;
                if (this.needShowTopView) {
                    this.topView.setVisibility(8);
                    resizeForTopView(false);
                    this.topView.setTranslationY((float) this.topView.getLayoutParams().height);
                }
            }
        } else if (!this.allowShowTopView) {
            this.allowShowTopView = true;
            if (this.needShowTopView) {
                this.topView.setVisibility(0);
                resizeForTopView(true);
                this.topView.setTranslationY(0.0f);
            }
        }
    }

    private void openKeyboardInternal() {
        int i = (AndroidUtilities.usingHardwareInput || this.isPaused) ? 0 : 2;
        showPopup(i, 0);
        this.messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.messageEditText);
        if (this.isPaused) {
            this.showKeyboardOnResume = true;
        } else if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
            this.waitingForKeyboardOpen = true;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
            AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
        }
    }

    private void removeGifFromInputField() {
        if (!AndroidUtilities.usingHardwareInput && this.messageEditText.getText().toString().equals("@gif ")) {
            this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
        }
    }

    private void resizeForTopView(boolean z) {
        LayoutParams layoutParams = (LayoutParams) this.textFieldContainer.getLayoutParams();
        layoutParams.topMargin = (z ? this.topView.getLayoutParams().height : 0) + AndroidUtilities.dp(2.0f);
        this.textFieldContainer.setLayoutParams(layoutParams);
    }

    private void sendMessage() {
        if (this.parentFragment != null) {
            String str;
            if (((int) this.dialog_id) < 0) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) this.dialog_id)));
                str = (chat == null || chat.participants_count <= MessagesController.getInstance().groupBigSize) ? "chat_message" : "bigchat_message";
            } else {
                str = "pm_message";
            }
            if (!MessagesController.isFeatureEnabled(str, this.parentFragment)) {
                return;
            }
        }
        if (this.audioToSend != null) {
            MessageObject j = MediaController.m71a().m182j();
            if (j != null && j == this.audioToSendMessageObject) {
                MediaController.m71a().m155a(true, true);
            }
            SendMessagesHelper.getInstance().sendMessage(this.audioToSend, null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, null, null);
            if (this.delegate != null) {
                this.delegate.onMessageSend(null);
            }
            hideRecordedAudioPanel();
            checkSendButton(true);
            return;
        }
        CharSequence text = this.messageEditText.getText();
        if (processSendingText(text)) {
            this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.lastTypingTimeSend = 0;
            if (this.delegate != null) {
                this.delegate.onMessageSend(text);
            }
        } else if (this.forceShowSendButton && this.delegate != null) {
            this.delegate.onMessageSend(null);
        }
    }

    private void setEmojiButtonImage() {
        int i = this.emojiView == null ? getContext().getSharedPreferences("emoji", 0).getInt("selected_page", 0) : this.emojiView.getCurrentPage();
        if (i == 0 || (!(this.allowStickers || this.allowGifs) || MoboConstants.aP)) {
            this.emojiButton.setImageResource(C0338R.drawable.ic_msg_panel_smiles);
        } else if (i == 1) {
            this.emojiButton.setImageResource(C0338R.drawable.ic_msg_panel_stickers);
        } else if (i == 2) {
            this.emojiButton.setImageResource(C0338R.drawable.ic_msg_panel_gif);
        }
    }

    private void showPopup(int i, int i2) {
        int c = AdvanceTheme.m2286c(AdvanceTheme.bG, -5395027);
        if (i == 1) {
            View view;
            if (i2 == 0 && this.emojiView == null) {
                if (this.parentActivity != null) {
                    createEmojiView();
                } else {
                    return;
                }
            }
            if (i2 == 0) {
                this.emojiView.setVisibility(0);
                if (!(this.botKeyboardView == null || this.botKeyboardView.getVisibility() == 8)) {
                    this.botKeyboardView.setVisibility(8);
                }
                view = this.emojiView;
            } else if (i2 == 1) {
                if (!(this.emojiView == null || this.emojiView.getVisibility() == 8)) {
                    this.emojiView.setVisibility(8);
                }
                this.botKeyboardView.setVisibility(0);
                view = this.botKeyboardView;
            } else {
                view = null;
            }
            this.currentPopupContentType = i2;
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
            }
            int i3 = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            int min = i2 == 1 ? Math.min(this.botKeyboardView.getKeyboardHeight(), i3) : i3;
            if (this.botKeyboardView != null) {
                this.botKeyboardView.setPanelHeight(min);
            }
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.height = min;
            view.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            if (this.sizeNotifierLayout != null) {
                this.emojiPadding = min;
                this.sizeNotifierLayout.requestLayout();
                if (i2 == 0) {
                    this.emojiButton.setImageResource(C0338R.drawable.ic_msg_panel_kb);
                } else if (i2 == 1) {
                    setEmojiButtonImage();
                }
                if (ThemeUtil.m2490b()) {
                    this.emojiButton.setColorFilter(c, Mode.SRC_IN);
                }
                updateBotButton();
                onWindowSizeChanged();
                return;
            }
            return;
        }
        if (this.emojiButton != null) {
            setEmojiButtonImage();
            if (ThemeUtil.m2490b()) {
                this.emojiButton.setColorFilter(c, Mode.SRC_IN);
            }
        }
        this.currentPopupContentType = -1;
        if (this.emojiView != null) {
            this.emojiView.setVisibility(8);
        }
        if (this.botKeyboardView != null) {
            this.botKeyboardView.setVisibility(8);
        }
        if (this.sizeNotifierLayout != null) {
            if (i == 0) {
                this.emojiPadding = 0;
            }
            this.sizeNotifierLayout.requestLayout();
            onWindowSizeChanged();
        }
        updateBotButton();
    }

    private void showStickerSendConfirmDialog(Document document, boolean z) {
        if (this.parentActivity != null && document != null) {
            if (z) {
                new StickerSelectAlert(this.parentActivity, document, new AnonymousClass28(document)).show();
            } else {
                new StickerSelectAlert(this.parentActivity, document, this).show();
            }
        }
    }

    private void showVoiceSettingsDialog() {
        if (this.parentActivity != null && this.parentFragment != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity, false, false);
            builder.setApplyTopPadding(true);
            View scrollView = new ScrollView(this.parentActivity);
            View linearLayout = new LinearLayout(this.parentActivity);
            linearLayout.setOrientation(1);
            View textView = new TextView(this.parentActivity);
            textView.setTypeface(FontUtil.m1176a().m1161d());
            textView.setText(LocaleController.getString("VoiceChanger", C0338R.string.VoiceChanger));
            textView.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(0.0f));
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, 48));
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            this.disableRadioCell = new RadioButtonCell(this.parentActivity);
            this.disableRadioCell.setBackgroundResource(C0338R.drawable.list_selector);
            this.disableRadioCell.setTextAndValue(LocaleController.getString("Disabled", C0338R.string.Disabled), null, MoboConstants.aQ == 0, true);
            this.disableRadioCell.setOnClickListener(new AnonymousClass29(edit));
            linearLayout.addView(this.disableRadioCell, LayoutHelper.createLinear(-1, 48));
            this.recordSpeedRadioCell = new RadioButtonCell(this.parentActivity);
            this.recordSpeedRadioCell.setBackgroundResource(C0338R.drawable.list_selector);
            this.recordSpeedRadioCell.setOnClickListener(new AnonymousClass30(edit));
            linearLayout.addView(this.recordSpeedRadioCell, LayoutHelper.createLinear(-1, 48));
            this.roboticRadioCell = new RadioButtonCell(this.parentActivity);
            this.roboticRadioCell.setBackgroundResource(C0338R.drawable.list_selector);
            this.roboticRadioCell.setTextAndValue(LocaleController.getString("Robotic", C0338R.string.Robotic), null, MoboConstants.aQ == 2, true);
            this.roboticRadioCell.setOnClickListener(new AnonymousClass31(edit));
            linearLayout.addView(this.roboticRadioCell, LayoutHelper.createLinear(-1, 48));
            this.detuneRadioCell = new RadioButtonCell(this.parentActivity);
            this.detuneRadioCell.setBackgroundResource(C0338R.drawable.list_selector);
            this.detuneRadioCell.setTextAndValue(LocaleController.getString("Alien", C0338R.string.Alien), null, MoboConstants.aQ == 3, true);
            this.detuneRadioCell.setOnClickListener(new AnonymousClass32(edit));
            linearLayout.addView(this.detuneRadioCell, LayoutHelper.createLinear(-1, 48));
            this.hoarsenessRadioCell = new RadioButtonCell(this.parentActivity);
            this.hoarsenessRadioCell.setBackgroundResource(C0338R.drawable.list_selector);
            this.hoarsenessRadioCell.setTextAndValue(LocaleController.getString("Hoarseness", C0338R.string.Hoarseness), null, MoboConstants.aQ == 4, true);
            this.hoarsenessRadioCell.setOnClickListener(new AnonymousClass33(edit));
            linearLayout.addView(this.hoarsenessRadioCell, LayoutHelper.createLinear(-1, 48));
            this.transposeRadioCell = new RadioButtonCell(this.parentActivity);
            this.transposeRadioCell.setBackgroundResource(C0338R.drawable.list_selector);
            this.transposeRadioCell.setTextAndValue(LocaleController.getString("Transpose", C0338R.string.Transpose), MoboConstants.aS + TtmlNode.ANONYMOUS_REGION_ID, MoboConstants.aQ == 5, true);
            this.transposeRadioCell.setOnClickListener(new AnonymousClass34(edit));
            linearLayout.addView(this.transposeRadioCell, LayoutHelper.createLinear(-1, 48));
            textView = new TextView(this.parentActivity);
            textView.setTypeface(FontUtil.m1176a().m1161d());
            textView.setText(LocaleController.getString("Settings", C0338R.string.Settings));
            textView.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(15.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(0.0f));
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, 48));
            textView = new TextDetailCheckCell(this.parentActivity);
            textView.setTextAndCheck(LocaleController.getString("ConfirmationBeforSendVoice", C0338R.string.ConfirmationBeforSendVoice), LocaleController.getString("ConfirmationBeforSendVoiceDetail", C0338R.string.ConfirmationBeforSendVoiceDetail), MoboConstants.f1353t, true);
            textView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
            textView.setOnClickListener(new AnonymousClass35(edit));
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, 48));
            textView = new TextView(this.parentActivity);
            textView.setTypeface(FontUtil.m1176a().m1161d());
            textView.setText(LocaleController.getString("VoiceChangerDisableHelp", C0338R.string.VoiceChangerDisableHelp));
            textView.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(0.0f));
            textView.setTextColor(-7697782);
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
            textView = new BottomSheetCell(this.parentActivity, 1);
            textView.setBackgroundResource(C0338R.drawable.list_selector);
            textView.setTextAndIcon(LocaleController.getString("Close", C0338R.string.Close).toUpperCase(), 0);
            textView.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
            textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    try {
                        if (ChatActivityEnterView.this.parentFragment.getVisibleDialog() != null) {
                            ChatActivityEnterView.this.parentFragment.getVisibleDialog().dismiss();
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            });
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, 48));
            scrollView.addView(linearLayout);
            builder.setCustomView(scrollView);
            this.parentFragment.showDialog(builder.create());
            updateVoiceChangerInterface();
        }
    }

    private void updateAudioRecordIntefrace() {
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (!this.recordingAudio) {
            if (this.mWakeLock != null) {
                try {
                    this.mWakeLock.release();
                    this.mWakeLock = null;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
            AndroidUtilities.unlockOrientation(this.parentActivity);
            if (this.audioInterfaceState != 0) {
                this.audioInterfaceState = 0;
                if (this.runningAnimationAudio != null) {
                    this.runningAnimationAudio.cancel();
                }
                this.runningAnimationAudio = new AnimatorSet();
                animatorSet = this.runningAnimationAudio;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[]{(float) AndroidUtilities.displaySize.x});
                animatorArr[1] = ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[]{0.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                animatorSet.playTogether(animatorArr);
                this.runningAnimationAudio.setDuration(300);
                this.runningAnimationAudio.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivityEnterView.this.runningAnimationAudio != null && ChatActivityEnterView.this.runningAnimationAudio.equals(animator)) {
                            LayoutParams layoutParams = (LayoutParams) ChatActivityEnterView.this.slideText.getLayoutParams();
                            layoutParams.leftMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
                            ChatActivityEnterView.this.slideText.setLayoutParams(layoutParams);
                            ChatActivityEnterView.this.slideText.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            ChatActivityEnterView.this.recordPanel.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setVisibility(8);
                            ChatActivityEnterView.this.runningAnimationAudio = null;
                        }
                    }
                });
                this.runningAnimationAudio.setInterpolator(new AccelerateInterpolator());
                this.runningAnimationAudio.start();
            }
        } else if (this.audioInterfaceState != 1) {
            this.audioInterfaceState = 1;
            try {
                if (this.mWakeLock == null) {
                    this.mWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(536870918, "audio record lock");
                    this.mWakeLock.acquire();
                }
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            AndroidUtilities.lockOrientation(this.parentActivity);
            this.recordPanel.setVisibility(0);
            this.recordCircle.setVisibility(0);
            this.recordCircle.setAmplitude(0.0d);
            this.recordTimeText.setText("00:00");
            this.recordDot.resetAlpha();
            this.lastTimeString = null;
            LayoutParams layoutParams = (LayoutParams) this.slideText.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
            this.slideText.setLayoutParams(layoutParams);
            this.slideText.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.recordPanel.setX((float) AndroidUtilities.displaySize.x);
            this.recordCircle.setTranslationX(0.0f);
            if (this.runningAnimationAudio != null) {
                this.runningAnimationAudio.cancel();
            }
            this.runningAnimationAudio = new AnimatorSet();
            animatorSet = this.runningAnimationAudio;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            animatorArr[2] = ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.runningAnimationAudio.setDuration(300);
            this.runningAnimationAudio.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animator) {
                    if (ChatActivityEnterView.this.runningAnimationAudio != null && ChatActivityEnterView.this.runningAnimationAudio.equals(animator)) {
                        ChatActivityEnterView.this.recordPanel.setX(0.0f);
                        ChatActivityEnterView.this.runningAnimationAudio = null;
                    }
                }
            });
            this.runningAnimationAudio.setInterpolator(new DecelerateInterpolator());
            this.runningAnimationAudio.start();
        }
    }

    private void updateBotButton() {
        if (this.botButton != null) {
            if (this.hasBotCommands || this.botReplyMarkup != null) {
                if (this.botButton.getVisibility() != 0) {
                    this.botButton.setVisibility(0);
                }
                int c = AdvanceTheme.m2286c(AdvanceTheme.bG, -5395027);
                if (this.botReplyMarkup == null) {
                    this.botButton.setImageResource(C0338R.drawable.bot_keyboard);
                } else if (isPopupShowing() && this.currentPopupContentType == 1) {
                    this.botButton.setImageResource(C0338R.drawable.ic_msg_panel_kb);
                } else {
                    this.botButton.setImageResource(C0338R.drawable.bot_keyboard2);
                }
                if (ThemeUtil.m2490b()) {
                    this.botButton.setColorFilter(c, Mode.SRC_IN);
                }
            } else {
                this.botButton.setVisibility(8);
            }
            updateFieldRight(2);
            LinearLayout linearLayout = this.attachButton;
            float f = ((this.botButton == null || this.botButton.getVisibility() == 8) && (this.notifyButton == null || this.notifyButton.getVisibility() == 8)) ? 48.0f : 96.0f;
            linearLayout.setPivotX((float) AndroidUtilities.dp(f));
        }
    }

    private void updateFieldHint() {
        Object obj = null;
        if (((int) this.dialog_id) < 0) {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) this.dialog_id)));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                obj = 1;
            }
        }
        if (obj == null) {
            this.messageEditText.setHint(LocaleController.getString("TypeMessage", C0338R.string.TypeMessage));
        } else if (this.editingMessageObject != null) {
            this.messageEditText.setHint(this.editingCaption ? LocaleController.getString("Caption", C0338R.string.Caption) : LocaleController.getString("TypeMessage", C0338R.string.TypeMessage));
        } else if (this.silent) {
            this.messageEditText.setHint(LocaleController.getString("ChannelSilentBroadcast", C0338R.string.ChannelSilentBroadcast));
        } else {
            this.messageEditText.setHint(LocaleController.getString("ChannelBroadcast", C0338R.string.ChannelBroadcast));
        }
    }

    private void updateFieldRight(int i) {
        float f = 148.0f;
        float f2 = 98.0f;
        if (this.messageEditText != null && this.editingMessageObject == null) {
            LayoutParams layoutParams = (LayoutParams) this.messageEditText.getLayoutParams();
            if (i == 1) {
                if ((this.botButton == null || this.botButton.getVisibility() != 0) && (this.notifyButton == null || this.notifyButton.getVisibility() != 0)) {
                    if (!this.hasTwoAttach) {
                        f2 = 48.0f;
                    }
                    layoutParams.rightMargin = AndroidUtilities.dp(f2);
                } else {
                    if (!this.hasTwoAttach) {
                        f = 98.0f;
                    }
                    layoutParams.rightMargin = AndroidUtilities.dp(f);
                }
            } else if (i != 2) {
                layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
            } else if (layoutParams.rightMargin != AndroidUtilities.dp(2.0f)) {
                if ((this.botButton == null || this.botButton.getVisibility() != 0) && (this.notifyButton == null || this.notifyButton.getVisibility() != 0)) {
                    if (!this.hasTwoAttach) {
                        f2 = 48.0f;
                    }
                    layoutParams.rightMargin = AndroidUtilities.dp(f2);
                } else {
                    if (!this.hasTwoAttach) {
                        f = 98.0f;
                    }
                    layoutParams.rightMargin = AndroidUtilities.dp(f);
                }
            }
            this.messageEditText.setLayoutParams(layoutParams);
        }
    }

    private void updateVoiceChangerInterface() {
        boolean z = false;
        this.disableRadioCell.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(0.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(5.0f));
        this.recordSpeedRadioCell.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(5.0f));
        this.roboticRadioCell.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(5.0f));
        this.detuneRadioCell.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(5.0f));
        this.hoarsenessRadioCell.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(5.0f));
        this.transposeRadioCell.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(5.0f));
        this.disableRadioCell.setChecked(false, false);
        this.recordSpeedRadioCell.setChecked(false, false);
        this.roboticRadioCell.setChecked(false, false);
        this.detuneRadioCell.setChecked(false, false);
        this.hoarsenessRadioCell.setChecked(false, false);
        this.transposeRadioCell.setChecked(false, false);
        switch (MoboConstants.aQ) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                this.disableRadioCell.setChecked(true, true);
                break;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                this.recordSpeedRadioCell.setChecked(true, true);
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                this.roboticRadioCell.setChecked(true, true);
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                this.detuneRadioCell.setChecked(true, true);
                break;
            case VideoPlayer.STATE_READY /*4*/:
                this.hoarsenessRadioCell.setChecked(true, true);
                break;
            case VideoPlayer.STATE_ENDED /*5*/:
                this.transposeRadioCell.setChecked(true, true);
                break;
        }
        String str = null;
        switch (MoboConstants.aR) {
            case UdpDataSource.DEAFULT_SOCKET_TIMEOUT_MILLIS /*8000*/:
                str = LocaleController.getString("VeryHigh", C0338R.string.VeryHigh);
                break;
            case 11025:
                str = LocaleController.getString("High", C0338R.string.High);
                break;
            case 22050:
                str = LocaleController.getString("VeryLow", C0338R.string.VeryLow);
                break;
            case 33075:
                str = LocaleController.getString("Low", C0338R.string.Low);
                break;
        }
        this.recordSpeedRadioCell.setTextAndValue(LocaleController.getString("RecordSpeed", C0338R.string.RecordSpeed), str, MoboConstants.aQ == 1, true);
        RadioButtonCell radioButtonCell = this.transposeRadioCell;
        String string = LocaleController.getString("Transpose", C0338R.string.Transpose);
        String str2 = MoboConstants.aS + TtmlNode.ANONYMOUS_REGION_ID;
        if (MoboConstants.aQ == 5) {
            z = true;
        }
        radioButtonCell.setTextAndValue(string, str2, z, true);
        if (this.audioSendButton != null) {
            this.audioSendButton.setImageResource(MoboConstants.aQ == 0 ? C0338R.drawable.mic : C0338R.drawable.mic_voicechanger);
            if (ThemeUtil.m2490b()) {
                this.audioSendButton.setColorFilter(AdvanceTheme.m2286c(AdvanceTheme.bG, -5395027), Mode.SRC_IN);
            }
        }
    }

    public void addRecentGif(Document document) {
        if (this.emojiView != null) {
            this.emojiView.addRecentGif(document);
        }
    }

    public void addStickerToRecent(Document document) {
        createEmojiView();
        this.emojiView.addRecentSticker(document);
    }

    public void addToAttachLayout(View view) {
        if (this.attachButton != null) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            if (VERSION.SDK_INT >= 21) {
                view.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.INPUT_FIELD_SELECTOR_COLOR));
            }
            this.attachButton.addView(view, LayoutHelper.createLinear(48, 48));
        }
    }

    public void addTopView(View view, int i) {
        if (view != null) {
            this.topView = view;
            this.topView.setVisibility(8);
            this.topView.setTranslationY((float) i);
            addView(this.topView, 0, LayoutHelper.createFrame(-1, (float) i, 51, 0.0f, 2.0f, 0.0f, 0.0f));
            this.needShowTopView = false;
        }
    }

    public void checkSendButton(boolean z) {
        if (this.editingMessageObject == null) {
            if (this.isPaused) {
                z = false;
            }
            if (AndroidUtilities.getTrimmedString(this.messageEditText.getText()).length() > 0 || this.forceShowSendButton || this.audioToSend != null) {
                int i = (this.messageEditText.caption == null || this.sendButton.getVisibility() != 0) ? 0 : 1;
                int i2 = (this.messageEditText.caption == null && this.cancelBotButton.getVisibility() == 0) ? 1 : 0;
                if (this.audioSendButton.getVisibility() == 0 || i != 0 || i2 != 0) {
                    if (!z) {
                        this.audioSendButton.setScaleX(0.1f);
                        this.audioSendButton.setScaleY(0.1f);
                        this.audioSendButton.setAlpha(0.0f);
                        if (this.messageEditText.caption != null) {
                            this.sendButton.setScaleX(0.1f);
                            this.sendButton.setScaleY(0.1f);
                            this.sendButton.setAlpha(0.0f);
                            this.cancelBotButton.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            this.cancelBotButton.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            this.cancelBotButton.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            this.cancelBotButton.setVisibility(0);
                            this.sendButton.setVisibility(8);
                        } else {
                            this.cancelBotButton.setScaleX(0.1f);
                            this.cancelBotButton.setScaleY(0.1f);
                            this.cancelBotButton.setAlpha(0.0f);
                            this.sendButton.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            this.sendButton.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            this.sendButton.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            this.sendButton.setVisibility(0);
                            this.cancelBotButton.setVisibility(8);
                        }
                        this.audioSendButton.setVisibility(8);
                        if (this.attachButton != null) {
                            this.attachButton.setVisibility(8);
                            if (this.delegate != null && getVisibility() == 0) {
                                this.delegate.onAttachButtonHidden();
                            }
                            updateFieldRight(0);
                        }
                    } else if (this.runningAnimationType != 1 || this.messageEditText.caption != null) {
                        if (this.runningAnimationType != 3 || this.messageEditText.caption == null) {
                            if (this.runningAnimation != null) {
                                this.runningAnimation.cancel();
                                this.runningAnimation = null;
                            }
                            if (this.runningAnimation2 != null) {
                                this.runningAnimation2.cancel();
                                this.runningAnimation2 = null;
                            }
                            if (this.attachButton != null) {
                                this.runningAnimation2 = new AnimatorSet();
                                AnimatorSet animatorSet = this.runningAnimation2;
                                r5 = new Animator[2];
                                r5[0] = ObjectAnimator.ofFloat(this.attachButton, "alpha", new float[]{0.0f});
                                r5[1] = ObjectAnimator.ofFloat(this.attachButton, "scaleX", new float[]{0.0f});
                                animatorSet.playTogether(r5);
                                this.runningAnimation2.setDuration(100);
                                this.runningAnimation2.addListener(new AnimatorListenerAdapterProxy() {
                                    public void onAnimationCancel(Animator animator) {
                                        if (ChatActivityEnterView.this.runningAnimation2 != null && ChatActivityEnterView.this.runningAnimation2.equals(animator)) {
                                            ChatActivityEnterView.this.runningAnimation2 = null;
                                        }
                                    }

                                    public void onAnimationEnd(Animator animator) {
                                        if (ChatActivityEnterView.this.runningAnimation2 != null && ChatActivityEnterView.this.runningAnimation2.equals(animator)) {
                                            ChatActivityEnterView.this.attachButton.setVisibility(8);
                                        }
                                    }
                                });
                                this.runningAnimation2.start();
                                updateFieldRight(0);
                                if (this.delegate != null && getVisibility() == 0) {
                                    this.delegate.onAttachButtonHidden();
                                }
                            }
                            this.runningAnimation = new AnimatorSet();
                            ArrayList arrayList = new ArrayList();
                            if (this.audioSendButton.getVisibility() == 0) {
                                arrayList.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleX", new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleY", new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[]{0.0f}));
                            }
                            if (i != 0) {
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[]{0.0f}));
                            } else if (i2 != 0) {
                                arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[]{0.1f}));
                                arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[]{0.0f}));
                            }
                            if (this.messageEditText.caption != null) {
                                this.runningAnimationType = 3;
                                arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                                arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                                arrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                                this.cancelBotButton.setVisibility(0);
                            } else {
                                this.runningAnimationType = 1;
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                                arrayList.add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                                this.sendButton.setVisibility(0);
                            }
                            this.runningAnimation.playTogether(arrayList);
                            this.runningAnimation.setDuration(150);
                            this.runningAnimation.addListener(new AnimatorListenerAdapterProxy() {
                                public void onAnimationCancel(Animator animator) {
                                    if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator)) {
                                        ChatActivityEnterView.this.runningAnimation = null;
                                    }
                                }

                                public void onAnimationEnd(Animator animator) {
                                    if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator)) {
                                        if (ChatActivityEnterView.this.messageEditText.caption != null) {
                                            ChatActivityEnterView.this.cancelBotButton.setVisibility(0);
                                            ChatActivityEnterView.this.sendButton.setVisibility(8);
                                        } else {
                                            ChatActivityEnterView.this.sendButton.setVisibility(0);
                                            ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                        }
                                        ChatActivityEnterView.this.audioSendButton.setVisibility(8);
                                        ChatActivityEnterView.this.runningAnimation = null;
                                        ChatActivityEnterView.this.runningAnimationType = 0;
                                    }
                                }
                            });
                            this.runningAnimation.start();
                        }
                    }
                }
            } else if ((this.sendButton.getVisibility() != 0 && this.cancelBotButton.getVisibility() != 0) || this.disableSendSound) {
            } else {
                if (!z) {
                    this.sendButton.setScaleX(0.1f);
                    this.sendButton.setScaleY(0.1f);
                    this.sendButton.setAlpha(0.0f);
                    this.cancelBotButton.setScaleX(0.1f);
                    this.cancelBotButton.setScaleY(0.1f);
                    this.cancelBotButton.setAlpha(0.0f);
                    this.audioSendButton.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    this.audioSendButton.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    this.audioSendButton.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    this.cancelBotButton.setVisibility(8);
                    this.sendButton.setVisibility(8);
                    this.audioSendButton.setVisibility(0);
                    if (this.attachButton != null) {
                        if (getVisibility() == 0) {
                            this.delegate.onAttachButtonShow();
                        }
                        this.attachButton.setVisibility(0);
                        updateFieldRight(1);
                    }
                } else if (this.runningAnimationType != 2) {
                    if (this.runningAnimation != null) {
                        this.runningAnimation.cancel();
                        this.runningAnimation = null;
                    }
                    if (this.runningAnimation2 != null) {
                        this.runningAnimation2.cancel();
                        this.runningAnimation2 = null;
                    }
                    if (this.attachButton != null) {
                        this.attachButton.setVisibility(0);
                        this.runningAnimation2 = new AnimatorSet();
                        AnimatorSet animatorSet2 = this.runningAnimation2;
                        r3 = new Animator[2];
                        r3[0] = ObjectAnimator.ofFloat(this.attachButton, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                        r3[1] = ObjectAnimator.ofFloat(this.attachButton, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                        animatorSet2.playTogether(r3);
                        this.runningAnimation2.setDuration(100);
                        this.runningAnimation2.start();
                        updateFieldRight(1);
                        if (getVisibility() == 0) {
                            this.delegate.onAttachButtonShow();
                        }
                    }
                    this.audioSendButton.setVisibility(0);
                    this.runningAnimation = new AnimatorSet();
                    this.runningAnimationType = 2;
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    arrayList2.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    arrayList2.add(ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    if (this.cancelBotButton.getVisibility() == 0) {
                        arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[]{0.1f}));
                        arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[]{0.1f}));
                        arrayList2.add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[]{0.0f}));
                    } else {
                        arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[]{0.1f}));
                        arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[]{0.1f}));
                        arrayList2.add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[]{0.0f}));
                    }
                    this.runningAnimation.playTogether(arrayList2);
                    this.runningAnimation.setDuration(150);
                    this.runningAnimation.addListener(new AnimatorListenerAdapterProxy() {
                        public void onAnimationCancel(Animator animator) {
                            if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator)) {
                                ChatActivityEnterView.this.runningAnimation = null;
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
                            if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animator)) {
                                ChatActivityEnterView.this.sendButton.setVisibility(8);
                                ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                ChatActivityEnterView.this.audioSendButton.setVisibility(0);
                                ChatActivityEnterView.this.runningAnimation = null;
                                ChatActivityEnterView.this.runningAnimationType = 0;
                            }
                        }
                    });
                    this.runningAnimation.start();
                }
            }
        }
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.messageEditText);
    }

    public void didPressedBotButton(KeyboardButton keyboardButton, MessageObject messageObject, MessageObject messageObject2) {
        if (keyboardButton != null && messageObject2 != null) {
            if (keyboardButton instanceof TL_keyboardButton) {
                SendMessagesHelper.getInstance().sendMessage(keyboardButton.text, this.dialog_id, messageObject, null, false, null, null, null);
            } else if (keyboardButton instanceof TL_keyboardButtonUrl) {
                this.parentFragment.showOpenUrlAlert(keyboardButton.url, true);
            } else if (keyboardButton instanceof TL_keyboardButtonRequestPhone) {
                this.parentFragment.shareMyContact(messageObject2);
            } else if (keyboardButton instanceof TL_keyboardButtonRequestGeoLocation) {
                Builder builder = new Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("ShareYouLocationTitle", C0338R.string.ShareYouLocationTitle));
                builder.setMessage(LocaleController.getString("ShareYouLocationInfo", C0338R.string.ShareYouLocationInfo));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass25(messageObject2, keyboardButton));
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                this.parentFragment.showDialog(builder.create());
            } else if ((keyboardButton instanceof TL_keyboardButtonCallback) || (keyboardButton instanceof TL_keyboardButtonGame)) {
                SendMessagesHelper.getInstance().sendCallback(messageObject2, keyboardButton, this.parentFragment);
            } else if ((keyboardButton instanceof TL_keyboardButtonSwitchInline) && !this.parentFragment.processSwitchButton((TL_keyboardButtonSwitchInline) keyboardButton)) {
                if (keyboardButton.same_peer) {
                    int i = messageObject2.messageOwner.from_id;
                    if (messageObject2.messageOwner.via_bot_id != 0) {
                        i = messageObject2.messageOwner.via_bot_id;
                    }
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(i));
                    if (user != null) {
                        setFieldText("@" + user.username + " " + keyboardButton.query);
                        return;
                    }
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putInt("dialogsType", 1);
                BaseFragment dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(new AnonymousClass26(messageObject2, keyboardButton));
                this.parentFragment.presentFragment(dialogsActivity);
            }
        }
    }

    public void didReceivedNotification(int i, Object... objArr) {
        int i2 = 0;
        if (i == NotificationCenter.emojiDidLoaded) {
            if (this.emojiView != null) {
                this.emojiView.invalidateViews();
            }
            if (this.botKeyboardView != null) {
                this.botKeyboardView.invalidateViews();
            }
        } else if (i == NotificationCenter.recordProgressChanged) {
            long longValue = ((Long) objArr[0]).longValue();
            Long valueOf = Long.valueOf(longValue / 1000);
            int i3 = ((int) (longValue % 1000)) / 10;
            CharSequence format = String.format("%02d:%02d.%02d", new Object[]{Long.valueOf(valueOf.longValue() / 60), Long.valueOf(valueOf.longValue() % 60), Integer.valueOf(i3)});
            if (this.lastTimeString == null || !this.lastTimeString.equals(format)) {
                if (valueOf.longValue() % 5 == 0) {
                    MessagesController.getInstance().sendTyping(this.dialog_id, 1, 0);
                }
                if (this.recordTimeText != null) {
                    this.recordTimeText.setText(format);
                }
            }
            if (this.recordCircle != null) {
                this.recordCircle.setAmplitude(((Double) objArr[1]).doubleValue());
            }
        } else if (i == NotificationCenter.closeChats) {
            if (this.messageEditText != null && this.messageEditText.isFocused()) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
        } else if (i == NotificationCenter.recordStartError || i == NotificationCenter.recordStopped) {
            if (this.recordingAudio) {
                MessagesController.getInstance().sendTyping(this.dialog_id, 2, 0);
                this.recordingAudio = false;
                updateAudioRecordIntefrace();
            }
        } else if (i == NotificationCenter.recordStarted) {
            if (!this.recordingAudio) {
                this.recordingAudio = true;
                updateAudioRecordIntefrace();
            }
        } else if (i == NotificationCenter.audioDidSent) {
            this.audioToSend = (TL_document) objArr[0];
            this.audioToSendPath = (String) objArr[1];
            if (this.audioToSend != null) {
                if (this.recordedAudioPanel != null) {
                    int i4;
                    DocumentAttribute documentAttribute;
                    Message tL_message = new TL_message();
                    tL_message.out = true;
                    tL_message.id = 0;
                    tL_message.to_id = new TL_peerUser();
                    Peer peer = tL_message.to_id;
                    int clientUserId = UserConfig.getClientUserId();
                    tL_message.from_id = clientUserId;
                    peer.user_id = clientUserId;
                    tL_message.date = (int) (System.currentTimeMillis() / 1000);
                    tL_message.message = "-1";
                    tL_message.attachPath = this.audioToSendPath;
                    tL_message.media = new TL_messageMediaDocument();
                    tL_message.media.document = this.audioToSend;
                    tL_message.flags |= 768;
                    this.audioToSendMessageObject = new MessageObject(tL_message, null, false);
                    this.recordedAudioPanel.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    this.recordedAudioPanel.setVisibility(0);
                    for (i4 = 0; i4 < this.audioToSend.attributes.size(); i4++) {
                        documentAttribute = (DocumentAttribute) this.audioToSend.attributes.get(i4);
                        if (documentAttribute instanceof TL_documentAttributeAudio) {
                            i4 = documentAttribute.duration;
                            break;
                        }
                    }
                    i4 = 0;
                    for (clientUserId = 0; clientUserId < this.audioToSend.attributes.size(); clientUserId++) {
                        documentAttribute = (DocumentAttribute) this.audioToSend.attributes.get(clientUserId);
                        if (documentAttribute instanceof TL_documentAttributeAudio) {
                            if (documentAttribute.waveform == null || documentAttribute.waveform.length == 0) {
                                documentAttribute.waveform = MediaController.m71a().getWaveform(this.audioToSendPath);
                            }
                            this.recordedAudioSeekBar.setWaveform(documentAttribute.waveform);
                            this.recordedAudioTimeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(i4 / 60), Integer.valueOf(i4 % 60)}));
                            closeKeyboard();
                            hidePopup(false);
                            checkSendButton(false);
                        }
                    }
                    this.recordedAudioTimeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(i4 / 60), Integer.valueOf(i4 % 60)}));
                    closeKeyboard();
                    hidePopup(false);
                    checkSendButton(false);
                }
            } else if (this.delegate != null) {
                this.delegate.onMessageSend(null);
            }
        } else if (i == NotificationCenter.audioRouteChanged) {
            if (this.parentActivity != null) {
                boolean booleanValue = ((Boolean) objArr[0]).booleanValue();
                Activity activity = this.parentActivity;
                if (!booleanValue) {
                    i2 = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                }
                activity.setVolumeControlStream(i2);
            }
        } else if (i == NotificationCenter.audioDidReset) {
            if (this.audioToSendMessageObject != null && !MediaController.m71a().m172d(this.audioToSendMessageObject)) {
                this.recordedAudioPlayButton.setImageResource(C0338R.drawable.s_player_play_states);
                this.recordedAudioSeekBar.setProgress(0.0f);
            }
        } else if (i == NotificationCenter.audioProgressDidChanged) {
            Integer num = (Integer) objArr[0];
            if (this.audioToSendMessageObject != null && MediaController.m71a().m172d(this.audioToSendMessageObject)) {
                MessageObject j = MediaController.m71a().m182j();
                this.audioToSendMessageObject.audioProgress = j.audioProgress;
                this.audioToSendMessageObject.audioProgressSec = j.audioProgressSec;
                if (!this.recordedAudioSeekBar.isDragging()) {
                    this.recordedAudioSeekBar.setProgress(this.audioToSendMessageObject.audioProgress);
                }
            }
        } else if (i == NotificationCenter.featuredStickersDidLoaded && this.emojiButton != null) {
            this.emojiButton.invalidate();
        }
    }

    public void doneEditingMessage() {
        if (this.editingMessageObject != null) {
            this.delegate.onMessageEditEnd(true);
            CharSequence[] charSequenceArr = new CharSequence[]{this.messageEditText.getText()};
            this.editingMessageReqId = SendMessagesHelper.getInstance().editMessage(this.editingMessageObject, charSequenceArr[0].toString(), this.messageWebPageSearch, this.parentFragment, MessagesQuery.getEntities(charSequenceArr), new Runnable() {
                public void run() {
                    ChatActivityEnterView.this.editingMessageReqId = 0;
                    ChatActivityEnterView.this.setEditingMessageObject(null, false);
                }
            });
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (view == this.topView) {
            canvas.save();
            canvas.clipRect(0, 0, getMeasuredWidth(), view.getLayoutParams().height + AndroidUtilities.dp(2.0f));
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.topView) {
            canvas.restore();
        }
        return drawChild;
    }

    public ImageView getAudioSendButton() {
        return this.audioSendButton;
    }

    public int getCursorPosition() {
        return this.messageEditText == null ? 0 : this.messageEditText.getSelectionStart();
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public CharSequence getFieldText() {
        return (this.messageEditText == null || this.messageEditText.length() <= 0) ? null : this.messageEditText.getText();
    }

    public EditText getMessageEditText() {
        return this.messageEditText;
    }

    public ImageView getSendButton() {
        return this.sendButton;
    }

    public boolean hasAudioToSend() {
        return this.audioToSendMessageObject != null;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean hasText() {
        return this.messageEditText != null && this.messageEditText.length() > 0;
    }

    public void hidePopup(boolean z) {
        if (isPopupShowing()) {
            if (this.currentPopupContentType == 1 && z && this.botButtonsMessageObject != null) {
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
            }
            showPopup(0, 0);
            removeGifFromInputField();
        }
    }

    public void hideTopView(boolean z) {
        if (this.topView != null && this.topViewShowed) {
            this.topViewShowed = false;
            this.needShowTopView = false;
            if (this.allowShowTopView) {
                if (this.currentTopViewAnimation != null) {
                    this.currentTopViewAnimation.cancel();
                    this.currentTopViewAnimation = null;
                }
                if (z) {
                    this.currentTopViewAnimation = new AnimatorSet();
                    AnimatorSet animatorSet = this.currentTopViewAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.topView, "translationY", new float[]{(float) this.topView.getLayoutParams().height});
                    animatorSet.playTogether(animatorArr);
                    this.currentTopViewAnimation.addListener(new AnimatorListenerAdapterProxy() {
                        public void onAnimationCancel(Animator animator) {
                            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator)) {
                                ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
                            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animator)) {
                                ChatActivityEnterView.this.topView.setVisibility(8);
                                ChatActivityEnterView.this.resizeForTopView(false);
                                ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                        }
                    });
                    this.currentTopViewAnimation.setDuration(200);
                    this.currentTopViewAnimation.start();
                    return;
                }
                this.topView.setVisibility(8);
                this.topView.setTranslationY((float) this.topView.getLayoutParams().height);
            }
        }
    }

    public boolean isEditingCaption() {
        return this.editingCaption;
    }

    public boolean isEditingMessage() {
        return this.editingMessageObject != null;
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    public boolean isMessageWebPageSearchEnabled() {
        return this.messageWebPageSearch;
    }

    public boolean isPopupShowing() {
        return (this.emojiView != null && this.emojiView.getVisibility() == 0) || (this.botKeyboardView != null && this.botKeyboardView.getVisibility() == 0);
    }

    public boolean isPopupView(View view) {
        return view == this.botKeyboardView || view == this.emojiView;
    }

    public boolean isRecordCircle(View view) {
        return view == this.recordCircle;
    }

    public boolean isTopViewVisible() {
        return this.topView != null && this.topView.getVisibility() == 0;
    }

    public void onDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStartError);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStopped);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioRouteChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        if (this.emojiView != null) {
            this.emojiView.onDestroy();
        }
        if (this.mWakeLock != null) {
            try {
                this.mWakeLock.release();
                this.mWakeLock = null;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
        if (this.sizeNotifierLayout != null) {
            this.sizeNotifierLayout.setDelegate(null);
        }
    }

    protected void onDraw(Canvas canvas) {
        int translationY = (this.topView == null || this.topView.getVisibility() != 0) ? 0 : (int) this.topView.getTranslationY();
        this.backgroundDrawable.setBounds(0, translationY, getMeasuredWidth(), getMeasuredHeight());
        this.backgroundDrawable.draw(canvas);
    }

    public void onPause() {
        this.isPaused = true;
        closeKeyboard();
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 2 && this.pendingLocationButton != null) {
            if (iArr.length > 0 && iArr[0] == 0) {
                SendMessagesHelper.getInstance().sendCurrentLocation(this.pendingMessageObject, this.pendingLocationButton);
            }
            this.pendingLocationButton = null;
            this.pendingMessageObject = null;
        }
    }

    public void onResume() {
        this.isPaused = false;
        if (this.showKeyboardOnResume) {
            this.showKeyboardOnResume = false;
            this.messageEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.messageEditText);
            if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
                this.waitingForKeyboardOpen = true;
                AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
            }
        }
    }

    public void onSizeChanged(int i, boolean z) {
        if (i > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
            if (z) {
                this.keyboardHeightLand = i;
                ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = i;
                ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (isPopupShowing()) {
            int i2 = z ? this.keyboardHeightLand : this.keyboardHeight;
            int min = (this.currentPopupContentType != 1 || this.botKeyboardView.isFullSize()) ? i2 : Math.min(this.botKeyboardView.getKeyboardHeight(), i2);
            View view = this.currentPopupContentType == 0 ? this.emojiView : this.currentPopupContentType == 1 ? this.botKeyboardView : null;
            if (this.botKeyboardView != null) {
                this.botKeyboardView.setPanelHeight(min);
            }
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (!(layoutParams.width == AndroidUtilities.displaySize.x && layoutParams.height == min)) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = min;
                view.setLayoutParams(layoutParams);
                if (this.sizeNotifierLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    this.sizeNotifierLayout.requestLayout();
                    onWindowSizeChanged();
                }
            }
        }
        if (this.lastSizeChangeValue1 == i && this.lastSizeChangeValue2 == z) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = i;
        this.lastSizeChangeValue2 = z;
        boolean z2 = this.keyboardVisible;
        this.keyboardVisible = i > 0;
        if (this.keyboardVisible && isPopupShowing()) {
            showPopup(0, this.currentPopupContentType);
        }
        if (!(this.emojiPadding == 0 || this.keyboardVisible || this.keyboardVisible == z2 || isPopupShowing())) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        if (this.keyboardVisible && this.waitingForKeyboardOpen) {
            this.waitingForKeyboardOpen = false;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    public void onStickerSelected(Document document) {
        SendMessagesHelper.getInstance().sendSticker(document, this.dialog_id, this.replyingMessageObject);
        if (this.delegate != null) {
            this.delegate.onMessageSend(null);
        }
    }

    public void openKeyboard() {
        AndroidUtilities.showKeyboard(this.messageEditText);
    }

    public boolean processSendingText(CharSequence charSequence) {
        CharSequence trimmedString = AndroidUtilities.getTrimmedString(charSequence);
        if (trimmedString.length() == 0) {
            return false;
        }
        int ceil = (int) Math.ceil((double) (((float) trimmedString.length()) / 4096.0f));
        for (int i = 0; i < ceil; i++) {
            CharSequence[] charSequenceArr = new CharSequence[]{trimmedString.subSequence(i * ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, Math.min((i + 1) * ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, trimmedString.length()))};
            SendMessagesHelper.getInstance().sendMessage(charSequenceArr[0].toString(), this.dialog_id, this.replyingMessageObject, this.messageWebPage, this.messageWebPageSearch, MessagesQuery.getEntities(charSequenceArr), null, null);
        }
        return true;
    }

    public void replaceWithText(int i, int i2, CharSequence charSequence) {
        try {
            CharSequence spannableStringBuilder = new SpannableStringBuilder(this.messageEditText.getText());
            spannableStringBuilder.replace(i, i + i2, charSequence);
            this.messageEditText.setText(spannableStringBuilder);
            this.messageEditText.setSelection(charSequence.length() + i);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void setAllowStickersAndGifs(boolean z, boolean z2) {
        if (!((this.allowStickers == z && this.allowGifs == z2) || this.emojiView == null)) {
            if (this.emojiView.getVisibility() == 0) {
                hidePopup(false);
            }
            this.sizeNotifierLayout.removeView(this.emojiView);
            this.emojiView = null;
        }
        this.allowStickers = z;
        this.allowGifs = z2;
        setEmojiButtonImage();
    }

    public void setBotsCount(int i, boolean z) {
        this.botCount = i;
        if (this.hasBotCommands != z) {
            this.hasBotCommands = z;
            updateBotButton();
        }
    }

    public void setButtons(MessageObject messageObject) {
        setButtons(messageObject, true);
    }

    public void setButtons(MessageObject messageObject, boolean z) {
        TL_replyKeyboardMarkup tL_replyKeyboardMarkup = null;
        if (this.replyingMessageObject != null && this.replyingMessageObject == this.botButtonsMessageObject && this.replyingMessageObject != messageObject) {
            this.botMessageObject = messageObject;
        } else if (this.botButton == null) {
        } else {
            if (this.botButtonsMessageObject != null && this.botButtonsMessageObject == messageObject) {
                return;
            }
            if (this.botButtonsMessageObject != null || messageObject != null) {
                if (this.botKeyboardView == null) {
                    this.botKeyboardView = new BotKeyboardView(this.parentActivity);
                    this.botKeyboardView.setVisibility(8);
                    this.botKeyboardView.setDelegate(new BotKeyboardViewDelegate() {
                        public void didPressedButton(KeyboardButton keyboardButton) {
                            MessageObject access$3400 = ChatActivityEnterView.this.replyingMessageObject != null ? ChatActivityEnterView.this.replyingMessageObject : ((int) ChatActivityEnterView.this.dialog_id) < 0 ? ChatActivityEnterView.this.botButtonsMessageObject : null;
                            ChatActivityEnterView.this.didPressedBotButton(keyboardButton, access$3400, ChatActivityEnterView.this.replyingMessageObject != null ? ChatActivityEnterView.this.replyingMessageObject : ChatActivityEnterView.this.botButtonsMessageObject);
                            if (ChatActivityEnterView.this.replyingMessageObject != null) {
                                ChatActivityEnterView.this.openKeyboardInternal();
                                ChatActivityEnterView.this.setButtons(ChatActivityEnterView.this.botMessageObject, false);
                            } else if (ChatActivityEnterView.this.botButtonsMessageObject.messageOwner.reply_markup.single_use) {
                                ChatActivityEnterView.this.openKeyboardInternal();
                                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("answered_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                            }
                            if (ChatActivityEnterView.this.delegate != null) {
                                ChatActivityEnterView.this.delegate.onMessageSend(null);
                            }
                        }
                    });
                    this.sizeNotifierLayout.addView(this.botKeyboardView);
                }
                this.botButtonsMessageObject = messageObject;
                TL_replyKeyboardMarkup tL_replyKeyboardMarkup2 = (messageObject == null || !(messageObject.messageOwner.reply_markup instanceof TL_replyKeyboardMarkup)) ? null : (TL_replyKeyboardMarkup) messageObject.messageOwner.reply_markup;
                this.botReplyMarkup = tL_replyKeyboardMarkup2;
                this.botKeyboardView.setPanelHeight(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight);
                BotKeyboardView botKeyboardView = this.botKeyboardView;
                if (this.botReplyMarkup != null) {
                    tL_replyKeyboardMarkup = this.botReplyMarkup;
                }
                botKeyboardView.setButtons(tL_replyKeyboardMarkup);
                if (this.botReplyMarkup != null) {
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    int i = sharedPreferences.getInt(new StringBuilder().append("hidekeyboard_").append(this.dialog_id).toString(), 0) == messageObject.getId() ? 1 : 0;
                    if (this.botButtonsMessageObject == this.replyingMessageObject || !this.botReplyMarkup.single_use || sharedPreferences.getInt("answered_" + this.dialog_id, 0) != messageObject.getId()) {
                        if (i == 0 && this.messageEditText.length() == 0 && !isPopupShowing()) {
                            showPopup(1, 1);
                        }
                    } else {
                        return;
                    }
                } else if (isPopupShowing() && this.currentPopupContentType == 1) {
                    if (z) {
                        openKeyboardInternal();
                    } else {
                        showPopup(0, 1);
                    }
                }
                updateBotButton();
            }
        }
    }

    public void setCaption(String str) {
        if (this.messageEditText != null) {
            this.messageEditText.setCaption(str);
            checkSendButton(true);
        }
    }

    public void setCommand(MessageObject messageObject, String str, boolean z, boolean z2) {
        User user = null;
        if (str != null && getVisibility() == 0) {
            if (z) {
                String obj = this.messageEditText.getText().toString();
                if (messageObject != null && ((int) this.dialog_id) < 0) {
                    user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                }
                CharSequence charSequence = ((this.botCount != 1 || z2) && user != null && user.bot && !str.contains("@")) ? String.format(Locale.US, "%s@%s", new Object[]{str, user.username}) + " " + obj.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", TtmlNode.ANONYMOUS_REGION_ID) : str + " " + obj.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", TtmlNode.ANONYMOUS_REGION_ID);
                this.ignoreTextChange = true;
                this.messageEditText.setText(charSequence);
                this.messageEditText.setSelection(this.messageEditText.getText().length());
                this.ignoreTextChange = false;
                if (this.delegate != null) {
                    this.delegate.onTextChanged(this.messageEditText.getText(), true);
                }
                if (!this.keyboardVisible && this.currentPopupContentType == -1) {
                    openKeyboard();
                    return;
                }
                return;
            }
            User user2 = (messageObject == null || ((int) this.dialog_id) >= 0) ? null : MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
            if ((this.botCount != 1 || z2) && user2 != null && user2.bot && !str.contains("@")) {
                SendMessagesHelper.getInstance().sendMessage(String.format(Locale.US, "%s@%s", new Object[]{str, user2.username}), this.dialog_id, null, null, false, null, null, null);
            } else {
                SendMessagesHelper.getInstance().sendMessage(str, this.dialog_id, null, null, false, null, null, null);
            }
        }
    }

    public void setDelegate(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
        this.delegate = chatActivityEnterViewDelegate;
    }

    public void setDialogId(long j) {
        int i = 1;
        this.dialog_id = j;
        if (((int) this.dialog_id) < 0) {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) this.dialog_id)));
            this.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + this.dialog_id, false);
            boolean z = ChatObject.isChannel(chat) && ((chat.creator || chat.editor) && !chat.megagroup);
            this.canWriteToChannel = z;
            if (this.notifyButton != null) {
                this.notifyButton.setVisibility(this.canWriteToChannel ? 0 : 8);
                this.notifyButton.setImageResource(this.silent ? C0338R.drawable.notify_members_off : C0338R.drawable.notify_members_on);
                LinearLayout linearLayout = this.attachButton;
                float f = ((this.botButton == null || this.botButton.getVisibility() == 8) && (this.notifyButton == null || this.notifyButton.getVisibility() == 8)) ? 48.0f : 96.0f;
                linearLayout.setPivotX((float) AndroidUtilities.dp(f));
                if (this.attachButton != null && this.attachButton.getVisibility() == 0 && this.canWriteToChannel) {
                    updateFieldRight(1);
                }
                if (this.attachButton != null) {
                    if (this.attachButton.getVisibility() != 0) {
                        i = 0;
                    }
                    updateFieldRight(i);
                }
            }
        }
        updateFieldHint();
    }

    public void setDisableSendSound(boolean z) {
        this.disableSendSound = z;
    }

    public void setEditingMessageObject(MessageObject messageObject, boolean z) {
        if (this.audioToSend == null && this.editingMessageObject != messageObject) {
            if (this.editingMessageReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.editingMessageReqId, true);
                this.editingMessageReqId = 0;
            }
            this.editingMessageObject = messageObject;
            this.editingCaption = z;
            if (this.editingMessageObject != null) {
                InputFilter[] inputFilterArr = new InputFilter[1];
                if (z) {
                    inputFilterArr[0] = new LengthFilter(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                    if (this.editingMessageObject.caption != null) {
                        setFieldText(Emoji.replaceEmoji(new SpannableStringBuilder(this.editingMessageObject.caption.toString()), this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
                    } else {
                        setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                } else {
                    inputFilterArr[0] = new LengthFilter(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
                    if (this.editingMessageObject.messageText != null) {
                        CharSequence[] charSequenceArr = new CharSequence[]{this.editingMessageObject.messageText};
                        ArrayList arrayList = this.editingMessageObject.messageOwner.entities;
                        CharSequence spannableStringBuilder = new SpannableStringBuilder(charSequenceArr[0]);
                        if (arrayList != null) {
                            int i = 0;
                            int i2 = 0;
                            while (i < arrayList.size()) {
                                int i3;
                                MessageEntity messageEntity = (MessageEntity) arrayList.get(i);
                                if (messageEntity instanceof TL_inputMessageEntityMentionName) {
                                    if ((messageEntity.offset + messageEntity.length) + i2 < spannableStringBuilder.length() && spannableStringBuilder.charAt((messageEntity.offset + messageEntity.length) + i2) == ' ') {
                                        messageEntity.length++;
                                    }
                                    spannableStringBuilder.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + ((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id), messageEntity.offset + i2, (messageEntity.length + messageEntity.offset) + i2, 33);
                                    i3 = i2;
                                } else if (messageEntity instanceof TL_messageEntityCode) {
                                    spannableStringBuilder.insert((messageEntity.offset + messageEntity.length) + i2, "`");
                                    spannableStringBuilder.insert(messageEntity.offset + i2, "`");
                                    i3 = i2 + 2;
                                } else if (messageEntity instanceof TL_messageEntityPre) {
                                    spannableStringBuilder.insert((messageEntity.offset + messageEntity.length) + i2, "```");
                                    spannableStringBuilder.insert(messageEntity.offset + i2, "```");
                                    i3 = i2 + 6;
                                } else {
                                    i3 = i2;
                                }
                                i++;
                                i2 = i3;
                            }
                        }
                        setFieldText(Emoji.replaceEmoji(spannableStringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
                    } else {
                        setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                }
                this.messageEditText.setFilters(inputFilterArr);
                openKeyboard();
                LayoutParams layoutParams = (LayoutParams) this.messageEditText.getLayoutParams();
                layoutParams.rightMargin = AndroidUtilities.dp(4.0f);
                this.messageEditText.setLayoutParams(layoutParams);
                this.sendButton.setVisibility(8);
                this.cancelBotButton.setVisibility(8);
                this.audioSendButton.setVisibility(8);
                this.attachButton.setVisibility(8);
                this.sendButtonContainer.setVisibility(8);
            } else {
                this.messageEditText.setFilters(new InputFilter[0]);
                this.delegate.onMessageEditEnd(false);
                this.audioSendButton.setVisibility(0);
                this.attachButton.setVisibility(0);
                this.sendButtonContainer.setVisibility(0);
                this.attachButton.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.attachButton.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.sendButton.setScaleX(0.1f);
                this.sendButton.setScaleY(0.1f);
                this.sendButton.setAlpha(0.0f);
                this.cancelBotButton.setScaleX(0.1f);
                this.cancelBotButton.setScaleY(0.1f);
                this.cancelBotButton.setAlpha(0.0f);
                this.audioSendButton.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.audioSendButton.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.audioSendButton.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.sendButton.setVisibility(8);
                this.cancelBotButton.setVisibility(8);
                this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                if (getVisibility() == 0) {
                    this.delegate.onAttachButtonShow();
                }
                updateFieldRight(1);
            }
            updateFieldHint();
        }
    }

    public void setFieldFocused() {
        if (this.messageEditText != null) {
            try {
                this.messageEditText.requestFocus();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public void setFieldFocused(boolean z) {
        if (this.messageEditText != null) {
            if (z) {
                if (!this.messageEditText.isFocused()) {
                    this.messageEditText.postDelayed(new Runnable() {
                        public void run() {
                            if (ChatActivityEnterView.this.messageEditText != null) {
                                try {
                                    ChatActivityEnterView.this.messageEditText.requestFocus();
                                } catch (Throwable e) {
                                    FileLog.m18e("tmessages", e);
                                }
                            }
                        }
                    }, 600);
                }
            } else if (this.messageEditText.isFocused() && !this.keyboardVisible) {
                this.messageEditText.clearFocus();
            }
        }
    }

    public void setFieldText(CharSequence charSequence) {
        if (this.messageEditText != null) {
            this.ignoreTextChange = true;
            this.messageEditText.setText(charSequence);
            this.messageEditText.setSelection(this.messageEditText.getText().length());
            this.ignoreTextChange = false;
            if (this.delegate != null) {
                this.delegate.onTextChanged(this.messageEditText.getText(), true);
            }
        }
    }

    public void setForceShowSendButton(boolean z, boolean z2) {
        this.forceShowSendButton = z;
        checkSendButton(z2);
    }

    public void setHasTwoAttach(boolean z) {
        this.hasTwoAttach = z;
    }

    public void setOpenGifsTabFirst() {
        createEmojiView();
        StickersQuery.loadRecents(0, true, true);
        this.emojiView.switchToGifRecent();
    }

    public void setReplyingMessageObject(MessageObject messageObject) {
        if (messageObject != null) {
            if (this.botMessageObject == null && this.botButtonsMessageObject != this.replyingMessageObject) {
                this.botMessageObject = this.botButtonsMessageObject;
            }
            this.replyingMessageObject = messageObject;
            setButtons(this.replyingMessageObject, true);
        } else if (messageObject == null && this.replyingMessageObject == this.botButtonsMessageObject) {
            this.replyingMessageObject = null;
            setButtons(this.botMessageObject, false);
            this.botMessageObject = null;
        } else {
            this.replyingMessageObject = messageObject;
        }
    }

    public void setSelection(int i) {
        if (this.messageEditText != null) {
            this.messageEditText.setSelection(i, this.messageEditText.length());
        }
    }

    public void setWebPage(WebPage webPage, boolean z) {
        this.messageWebPage = webPage;
        this.messageWebPageSearch = z;
    }

    public void showContextProgress(boolean z) {
        if (this.progressDrawable != null) {
            if (z) {
                this.progressDrawable.startAnimation();
            } else {
                this.progressDrawable.stopAnimation();
            }
        }
    }

    public void showTopView(boolean z, boolean z2) {
        if (this.topView != null && !this.topViewShowed && getVisibility() == 0) {
            this.needShowTopView = true;
            this.topViewShowed = true;
            if (this.allowShowTopView) {
                this.topView.setVisibility(0);
                if (this.currentTopViewAnimation != null) {
                    this.currentTopViewAnimation.cancel();
                    this.currentTopViewAnimation = null;
                }
                resizeForTopView(true);
                if (!z) {
                    this.topView.setTranslationY(0.0f);
                } else if (this.keyboardVisible || isPopupShowing()) {
                    this.currentTopViewAnimation = new AnimatorSet();
                    AnimatorSet animatorSet = this.currentTopViewAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.topView, "translationY", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    this.currentTopViewAnimation.addListener(new AnonymousClass14(z2));
                    this.currentTopViewAnimation.setDuration(200);
                    this.currentTopViewAnimation.start();
                } else {
                    this.topView.setTranslationY(0.0f);
                    if (this.recordedAudioPanel.getVisibility() == 0) {
                        return;
                    }
                    if (!this.forceShowSendButton || z2) {
                        openKeyboard();
                    }
                }
            }
        }
    }
}
