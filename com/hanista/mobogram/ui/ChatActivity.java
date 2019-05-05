package com.hanista.mobogram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract.Contacts;
import android.support.v4.content.FileProvider;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.Emoji;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.PhotoEntry;
import com.hanista.mobogram.messenger.MediaController.SearchImage;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.NotificationsController;
import com.hanista.mobogram.messenger.SecretChatHelper;
import com.hanista.mobogram.messenger.SendMessagesHelper;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.VideoEditedInfo;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.query.BotQuery;
import com.hanista.mobogram.messenger.query.DraftQuery;
import com.hanista.mobogram.messenger.query.MessagesQuery;
import com.hanista.mobogram.messenger.query.MessagesSearchQuery;
import com.hanista.mobogram.messenger.query.SearchQuery;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemDecoration;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.State;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.component.EditTextCaption;
import com.hanista.mobogram.mobo.component.PowerView;
import com.hanista.mobogram.mobo.component.ProForwardActivity;
import com.hanista.mobogram.mobo.component.StickerSelectAlert;
import com.hanista.mobogram.mobo.download.DownloadMessagesStorage;
import com.hanista.mobogram.mobo.markers.MarkersActivity;
import com.hanista.mobogram.mobo.p000a.Archive;
import com.hanista.mobogram.mobo.p000a.ArchiveMessageInfo;
import com.hanista.mobogram.mobo.p000a.ArchiveSettingsActivity;
import com.hanista.mobogram.mobo.p000a.ArchiveUtil;
import com.hanista.mobogram.mobo.p002c.ChatBarUtil;
import com.hanista.mobogram.mobo.p003d.Glow;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p005f.DialogSettings;
import com.hanista.mobogram.mobo.p005f.DialogSettingsActivity;
import com.hanista.mobogram.mobo.p005f.DialogSettingsUtil;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p010k.MaterialHelperUtil;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p016p.SpecificContactNotifController;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.PersianCalendar;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.BotInfo;
import com.hanista.mobogram.tgnet.TLRPC.BotInlineResult;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.DraftMessage;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputDocument;
import com.hanista.mobogram.tgnet.TLRPC.InputStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.KeyboardButton;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.MessageEntity;
import com.hanista.mobogram.tgnet.TLRPC.MessageMedia;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_botCommand;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelForbidden;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_reportSpam;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatForbidden;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantsForbidden;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.TL_document;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChatRequested;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChatWaiting;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import com.hanista.mobogram.tgnet.TLRPC.TL_game;
import com.hanista.mobogram.tgnet.TLRPC.TL_inlineBotSwitchPM;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetID;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetShortName;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonCallback;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionPinMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityCode;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityMentionName;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityPre;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerNotifySettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSizeEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_replyKeyboardForceReply;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPagePending;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.WebPage;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarLayout;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import com.hanista.mobogram.ui.ActionBar.BackDrawable;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import com.hanista.mobogram.ui.ActionBar.SimpleTextView;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.MentionsAdapter;
import com.hanista.mobogram.ui.Adapters.MentionsAdapter.Holder;
import com.hanista.mobogram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import com.hanista.mobogram.ui.Adapters.StickersAdapter;
import com.hanista.mobogram.ui.Adapters.StickersAdapter.StickersAdapterDelegate;
import com.hanista.mobogram.ui.AudioSelectActivity.AudioSelectActivityDelegate;
import com.hanista.mobogram.ui.Cells.BotHelpCell;
import com.hanista.mobogram.ui.Cells.BotHelpCell.BotHelpCellDelegate;
import com.hanista.mobogram.ui.Cells.ChatActionCell;
import com.hanista.mobogram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import com.hanista.mobogram.ui.Cells.ChatLoadingCell;
import com.hanista.mobogram.ui.Cells.ChatMessageCell;
import com.hanista.mobogram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import com.hanista.mobogram.ui.Cells.ChatUnreadCell;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
import com.hanista.mobogram.ui.Cells.ContextLinkCell;
import com.hanista.mobogram.ui.Components.AlertsCreator;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.ChatActivityEnterView;
import com.hanista.mobogram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import com.hanista.mobogram.ui.Components.ChatAttachAlert;
import com.hanista.mobogram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate;
import com.hanista.mobogram.ui.Components.ChatAvatarContainer;
import com.hanista.mobogram.ui.Components.ChatBigEmptyView;
import com.hanista.mobogram.ui.Components.ContextProgressView;
import com.hanista.mobogram.ui.Components.ExtendedGridLayoutManager;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.NumberTextView;
import com.hanista.mobogram.ui.Components.PlayerView;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnInterceptTouchListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.Components.ShareAlert;
import com.hanista.mobogram.ui.Components.ShareAlert.SendDelegate;
import com.hanista.mobogram.ui.Components.Size;
import com.hanista.mobogram.ui.Components.SizeNotifierFrameLayout;
import com.hanista.mobogram.ui.Components.StickersAlert;
import com.hanista.mobogram.ui.Components.StickersAlert.StickersAlertDelegate;
import com.hanista.mobogram.ui.Components.URLSpanBotCommand;
import com.hanista.mobogram.ui.Components.URLSpanNoUnderline;
import com.hanista.mobogram.ui.Components.URLSpanReplacement;
import com.hanista.mobogram.ui.Components.URLSpanUserMention;
import com.hanista.mobogram.ui.Components.WebFrameLayout;
import com.hanista.mobogram.ui.DialogsActivity.DialogsActivityDelegate;
import com.hanista.mobogram.ui.DocumentSelectActivity.DocumentSelectActivityDelegate;
import com.hanista.mobogram.ui.LocationActivity.LocationActivityDelegate;
import com.hanista.mobogram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import com.hanista.mobogram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import com.hanista.mobogram.ui.VideoEditorActivity.VideoEditorActivityDelegate;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;

public class ChatActivity extends BaseFragment implements NotificationCenterDelegate, DialogsActivityDelegate, PhotoViewerProvider {
    private static final int add_message_to_archive = 243;
    private static final int add_to_download_list = 190;
    private static final int attach_audio = 3;
    private static final int attach_contact = 5;
    private static final int attach_document = 4;
    private static final int attach_gallery = 1;
    private static final int attach_location = 6;
    private static final int attach_photo = 0;
    private static final int attach_video = 2;
    private static final int bot_help = 30;
    private static final int bot_settings = 31;
    private static final int chat_enc_timer = 13;
    private static final int chat_menu_archive = 241;
    private static final int chat_menu_archive_setting = 246;
    private static final int chat_menu_attach = 14;
    private static final int chat_menu_drawing = 231;
    private static final int chat_menu_media = 242;
    private static final int chat_settings = 174;
    private static final int clear_history = 15;
    private static final int copy = 10;
    private static final int delete = 12;
    private static final int delete_chat = 16;
    private static final int delete_multi_message = 171;
    private static final int edit_done = 20;
    private static final int forward = 11;
    public static boolean forwardNoName = false;
    private static final int forward_noname = 111;
    private static final int go_to_marker = 175;
    private static final int id_chat_compose_panel = 1000;
    private static final int jump_to_message = 172;
    private static final int leave_without_delete = 161;
    private static final int mute = 18;
    private static final int remove_message_from_archive = 244;
    private static final int reply = 19;
    private static final int report = 21;
    private static final int search = 40;
    private static final int select_all = 191;
    private static final int share_contact = 17;
    private static final int show_favorite_messages = 176;
    private SimpleTextView actionModeSubTextView;
    private SimpleTextView actionModeTextView;
    private FrameLayout actionModeTitleContainer;
    private ArrayList<View> actionModeViews;
    private TextView addContactItem;
    private TextView addToContactsButton;
    private TextView alertNameTextView;
    private TextView alertTextView;
    private FrameLayout alertView;
    private AnimatorSet alertViewAnimator;
    private boolean allowContextBotPanel;
    private boolean allowContextBotPanelSecond;
    private boolean allowStickersPanel;
    private Archive archive;
    private ActionBarMenuItem archiveItem;
    private ActionBarMenuItem archiveSettingItem;
    private ActionBarMenuItem attachItem;
    private ChatAvatarContainer avatarContainer;
    private ChatBigEmptyView bigEmptyView;
    private MessageObject botButtons;
    private PhotoViewerProvider botContextProvider;
    private ArrayList<Object> botContextResults;
    private HashMap<Integer, BotInfo> botInfo;
    private MessageObject botReplyButtons;
    private String botUser;
    private int botsCount;
    private FrameLayout bottomOverlay;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private TextView bottomOverlayText;
    private boolean[] cacheEndReached;
    private int cantDeleteMessagesCount;
    protected ChatActivityEnterView chatActivityEnterView;
    private ChatActivityAdapter chatAdapter;
    private ChatAttachAlert chatAttachAlert;
    private ChatBarUtil chatBarUtil;
    private long chatEnterTime;
    private LinearLayoutManager chatLayoutManager;
    private long chatLeaveTime;
    private RecyclerListView chatListView;
    private ArrayList<ChatMessageCell> chatMessageCellsCache;
    private Dialog closeChatDialog;
    private BottomSheet copyDialog;
    protected Chat currentChat;
    protected EncryptedChat currentEncryptedChat;
    private ChatMessageCell currentGifCell;
    public CharSequence currentPictureCaption;
    public String currentPicturePath;
    protected User currentUser;
    private TextView dateToastTv;
    private boolean deleteFilesOnDeleteMessage;
    private boolean deleteMutliMessageEnabled;
    private DialogSettings dialogSettings;
    private long dialog_id;
    private ActionBarMenuItem drawingItem;
    private ActionBarMenuItem editDoneItem;
    private AnimatorSet editDoneItemAnimation;
    private ContextProgressView editDoneItemProgress;
    private int editingMessageObjectReqId;
    private View emojiButtonRed;
    private FrameLayout emptyViewContainer;
    private boolean[] endReached;
    private boolean first;
    private boolean firstLoading;
    private int first_unread_id;
    private boolean forceScrollToTop;
    private boolean[] forwardEndReached;
    private ArrayList<MessageObject> forwardingMessages;
    private MessageObject forwaringMessage;
    private ArrayList<CharSequence> foundUrls;
    private WebPage foundWebPage;
    private TextView gifHintTextView;
    private int gotoDateMessage;
    private boolean gotoDateMessageSelected;
    private boolean gotoMessageSelected;
    private boolean hasBotsCommands;
    private ActionBarMenuItem headerItem;
    private Runnable hideAlertViewRunnable;
    private int highlightMessageId;
    protected ChatFull info;
    private long inlineReturn;
    private boolean isBroadcast;
    private int justFromId;
    private int lastLoadIndex;
    private int last_message_id;
    private int linkSearchRequestId;
    private boolean loading;
    private boolean loadingForward;
    private int loadingPinnedMessage;
    private int loadsCount;
    private int[] maxDate;
    private int[] maxMessageId;
    private ActionBarMenuItem mediaItem;
    private FrameLayout mentionContainer;
    private ExtendedGridLayoutManager mentionGridLayoutManager;
    private LinearLayoutManager mentionLayoutManager;
    private AnimatorSet mentionListAnimation;
    private RecyclerListView mentionListView;
    private boolean mentionListViewIgnoreLayout;
    private boolean mentionListViewIsScrolling;
    private int mentionListViewLastViewPosition;
    private int mentionListViewLastViewTop;
    private int mentionListViewScrollOffsetY;
    private MentionsAdapter mentionsAdapter;
    private OnItemClickListener mentionsOnItemClickListener;
    private ActionBarMenuItem menuItem;
    private long mergeDialogId;
    protected ArrayList<MessageObject> messages;
    private HashMap<String, ArrayList<MessageObject>> messagesByDays;
    private HashMap<Integer, MessageObject>[] messagesDict;
    private int[] minDate;
    private int[] minMessageId;
    private TextView muteItem;
    private boolean needSelectFromMessageId;
    private int newUnreadMessageCount;
    OnItemClickListener onItemClickListener;
    OnItemLongClickListener onItemLongClickListener;
    private boolean openAnimationEnded;
    private boolean openSearchKeyboard;
    private Runnable openSecretPhotoRunnable;
    private FrameLayout pagedownButton;
    private ObjectAnimator pagedownButtonAnimation;
    private TextView pagedownButtonCounter;
    private boolean pagedownButtonShowedByScroll;
    private boolean paused;
    private String pendingLinkSearchString;
    private Runnable pendingWebPageTimeoutRunnable;
    private FileLocation pinnedImageLocation;
    private BackupImageView pinnedMessageImageView;
    private SimpleTextView pinnedMessageNameTextView;
    private MessageObject pinnedMessageObject;
    private SimpleTextView pinnedMessageTextView;
    private FrameLayout pinnedMessageView;
    private AnimatorSet pinnedMessageViewAnimator;
    private PlayerView playerView;
    private PowerView powerView;
    private FrameLayout progressView;
    private boolean readWhenResume;
    private int readWithDate;
    private int readWithMid;
    private boolean refreshWallpaper;
    private AnimatorSet replyButtonAnimation;
    private ImageView replyIconImageView;
    private FileLocation replyImageLocation;
    private BackupImageView replyImageView;
    private SimpleTextView replyNameTextView;
    private SimpleTextView replyObjectTextView;
    private MessageObject replyingMessageObject;
    private TextView reportSpamButton;
    private FrameLayout reportSpamContainer;
    private LinearLayout reportSpamView;
    private AnimatorSet reportSpamViewAnimator;
    private int returnToLoadIndex;
    private int returnToMessageId;
    private AnimatorSet runningAnimation;
    private MessageObject scrollToMessage;
    private int scrollToMessagePosition;
    private boolean scrollToTopOnResume;
    private boolean scrollToTopUnReadOnResume;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ImageView searchDownButton;
    private ActionBarMenuItem searchItem;
    private ImageView searchUpButton;
    private List<Integer> selectedFromToIds;
    private HashMap<Integer, MessageObject>[] selectedMessagesCanCopyIds;
    private NumberTextView selectedMessagesCountTextView;
    private HashMap<Integer, MessageObject>[] selectedMessagesIds;
    private MessageObject selectedObject;
    private MessageSelectionDelegate selectionDelegate;
    private boolean selectionMode;
    private int startLoadFromMessageId;
    private String startVideoEdit;
    private float startX;
    private float startY;
    private StickersAdapter stickersAdapter;
    private RecyclerListView stickersListView;
    private OnItemClickListener stickersOnItemClickListener;
    private FrameLayout stickersPanel;
    private View timeItem2;
    private MessageObject unreadMessageObject;
    private int unread_to_load;
    private boolean userBlocked;
    private Runnable waitingForCharaterEnterRunnable;
    private ArrayList<Integer> waitingForLoad;
    private boolean waitingForReplyMessageLoad;
    private boolean wasPaused;

    public interface MessageSelectionDelegate {
        void didSelectMessages(List<Integer> list);
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.101 */
    class AnonymousClass101 implements OnClickListener {
        final /* synthetic */ EditTextCaption val$messageEditText;

        AnonymousClass101(EditTextCaption editTextCaption) {
            this.val$messageEditText = editTextCaption;
        }

        public void onClick(View view) {
            AndroidUtilities.addToClipboard(this.val$messageEditText.getText() + TtmlNode.ANONYMOUS_REGION_ID);
            if (ChatActivity.this.copyDialog != null) {
                ChatActivity.this.copyDialog.dismiss();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.105 */
    class AnonymousClass105 implements OnDateSetListener {
        final /* synthetic */ Calendar val$myCalendar;

        AnonymousClass105(Calendar calendar) {
            this.val$myCalendar = calendar;
        }

        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            this.val$myCalendar.set(ChatActivity.attach_gallery, i);
            this.val$myCalendar.set(ChatActivity.attach_video, i2);
            this.val$myCalendar.set(ChatActivity.attach_contact, i3);
            this.val$myCalendar.set(ChatActivity.forward, ChatActivity.attach_photo);
            this.val$myCalendar.set(ChatActivity.delete, ChatActivity.attach_photo);
            this.val$myCalendar.set(ChatActivity.chat_enc_timer, ChatActivity.attach_photo);
            ChatActivity.this.scrollToDateMessage((int) (this.val$myCalendar.getTimeInMillis() / 1000));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.11 */
    class AnonymousClass11 extends FrameLayout {
        AnonymousClass11(Context context) {
            super(context);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int dp;
            int i5 = i4 - i2;
            if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8) {
                int textHeight = ((i5 / ChatActivity.attach_video) - ChatActivity.this.actionModeTextView.getTextHeight()) / ChatActivity.attach_video;
                float f = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != ChatActivity.attach_video) ? 3.0f : 2.0f;
                dp = AndroidUtilities.dp(f) + textHeight;
            } else {
                dp = (i5 - ChatActivity.this.actionModeTextView.getTextHeight()) / ChatActivity.attach_video;
            }
            ChatActivity.this.actionModeTextView.layout(ChatActivity.attach_photo, dp, ChatActivity.this.actionModeTextView.getMeasuredWidth(), ChatActivity.this.actionModeTextView.getTextHeight() + dp);
            if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8) {
                dp = (i5 / ChatActivity.attach_video) + (((i5 / ChatActivity.attach_video) - ChatActivity.this.actionModeSubTextView.getTextHeight()) / ChatActivity.attach_video);
                if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation == ChatActivity.attach_video) {
                    dp -= AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    ChatActivity.this.actionModeSubTextView.layout(ChatActivity.attach_photo, dp, ChatActivity.this.actionModeSubTextView.getMeasuredWidth(), ChatActivity.this.actionModeSubTextView.getTextHeight() + dp);
                } else {
                    dp -= AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    ChatActivity.this.actionModeSubTextView.layout(ChatActivity.attach_photo, dp, ChatActivity.this.actionModeSubTextView.getMeasuredWidth(), ChatActivity.this.actionModeSubTextView.getTextHeight() + dp);
                }
            }
        }

        protected void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            setMeasuredDimension(size, MeasureSpec.getSize(i2));
            SimpleTextView access$5500 = ChatActivity.this.actionModeTextView;
            int i3 = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != ChatActivity.attach_video) ? ChatActivity.edit_done : ChatActivity.mute;
            access$5500.setTextSize(i3);
            ChatActivity.this.actionModeTextView.measure(MeasureSpec.makeMeasureSpec(size, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), TLRPC.MESSAGE_FLAG_MEGAGROUP));
            if (ChatActivity.this.actionModeSubTextView.getVisibility() != 8) {
                access$5500 = ChatActivity.this.actionModeSubTextView;
                i3 = (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != ChatActivity.attach_video) ? ChatActivity.delete_chat : ChatActivity.chat_menu_attach;
                access$5500.setTextSize(i3);
                ChatActivity.this.actionModeSubTextView.measure(MeasureSpec.makeMeasureSpec(size, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), TLRPC.MESSAGE_FLAG_MEGAGROUP));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.13 */
    class AnonymousClass13 extends SizeNotifierFrameLayout {
        int inputFieldHeight;

        AnonymousClass13(Context context) {
            super(context);
            this.inputFieldHeight = ChatActivity.attach_photo;
        }

        protected boolean drawChild(Canvas canvas, View view, long j) {
            boolean drawChild = super.drawChild(canvas, view, j);
            if (view == ChatActivity.this.actionBar) {
                ChatActivity.this.parentLayout.drawHeaderShadow(canvas, ChatActivity.this.actionBar.getMeasuredHeight());
            }
            return drawChild;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            int emojiPadding = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? ChatActivity.attach_photo : ChatActivity.this.chatActivityEnterView.getEmojiPadding();
            setBottomClip(emojiPadding);
            for (int i5 = ChatActivity.attach_photo; i5 < childCount; i5 += ChatActivity.attach_gallery) {
                View childAt = getChildAt(i5);
                if (childAt.getVisibility() != 8) {
                    int i6;
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    int measuredWidth = childAt.getMeasuredWidth();
                    int measuredHeight = childAt.getMeasuredHeight();
                    int i7 = layoutParams.gravity;
                    if (i7 == -1) {
                        i7 = 51;
                    }
                    int i8 = i7 & 112;
                    switch ((i7 & 7) & 7) {
                        case ChatActivity.attach_gallery /*1*/:
                            i7 = ((((i3 - i) - measuredWidth) / ChatActivity.attach_video) + layoutParams.leftMargin) - layoutParams.rightMargin;
                            break;
                        case ChatActivity.attach_contact /*5*/:
                            i7 = (i3 - measuredWidth) - layoutParams.rightMargin;
                            break;
                        default:
                            i7 = layoutParams.leftMargin;
                            break;
                    }
                    switch (i8) {
                        case ChatActivity.delete_chat /*16*/:
                            i6 = (((((i4 - emojiPadding) - i2) - measuredHeight) / ChatActivity.attach_video) + layoutParams.topMargin) - layoutParams.bottomMargin;
                            break;
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                            i6 = layoutParams.topMargin + getPaddingTop();
                            if (childAt != ChatActivity.this.actionBar) {
                                i6 += ChatActivity.this.actionBar.getMeasuredHeight();
                                break;
                            }
                            break;
                        case 80:
                            i6 = (((i4 - emojiPadding) - i2) - measuredHeight) - layoutParams.bottomMargin;
                            break;
                        default:
                            i6 = layoutParams.topMargin;
                            break;
                    }
                    if (childAt == ChatActivity.this.mentionContainer) {
                        i6 -= ChatActivity.this.chatActivityEnterView.getMeasuredHeight() - AndroidUtilities.dp(2.0f);
                    } else if (childAt == ChatActivity.this.pagedownButton) {
                        i6 -= ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
                    } else if (childAt == ChatActivity.this.emptyViewContainer) {
                        i6 -= (this.inputFieldHeight / ChatActivity.attach_video) - (ChatActivity.this.actionBar.getMeasuredHeight() / ChatActivity.attach_video);
                    } else if (ChatActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                        i6 = AndroidUtilities.isInMultiwindow ? (ChatActivity.this.chatActivityEnterView.getTop() - childAt.getMeasuredHeight()) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) : ChatActivity.this.chatActivityEnterView.getBottom();
                    } else if (childAt == ChatActivity.this.gifHintTextView) {
                        i6 -= this.inputFieldHeight;
                    } else if (childAt == ChatActivity.this.chatListView || childAt == ChatActivity.this.progressView) {
                        if (ChatActivity.this.chatActivityEnterView.isTopViewVisible()) {
                            i6 -= AndroidUtilities.dp(48.0f);
                        }
                    } else if (childAt == ChatActivity.this.actionBar) {
                        i6 -= getPaddingTop();
                    }
                    childAt.layout(i7, i6, measuredWidth + i7, measuredHeight + i6);
                }
            }
            ChatActivity.this.updateMessagesVisisblePart();
            notifyHeightChanged();
        }

        protected void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            int size2 = MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            int paddingTop = size2 - getPaddingTop();
            measureChildWithMargins(ChatActivity.this.actionBar, i, ChatActivity.attach_photo, i2, ChatActivity.attach_photo);
            int measuredHeight = ChatActivity.this.actionBar.getMeasuredHeight();
            size2 = paddingTop - measuredHeight;
            paddingTop = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? size2 : size2 - ChatActivity.this.chatActivityEnterView.getEmojiPadding();
            int childCount = getChildCount();
            measureChildWithMargins(ChatActivity.this.chatActivityEnterView, i, ChatActivity.attach_photo, i2, ChatActivity.attach_photo);
            this.inputFieldHeight = ChatActivity.this.chatActivityEnterView.getMeasuredHeight();
            if (ChatActivity.this.selectionMode) {
                this.inputFieldHeight = ChatActivity.attach_photo;
            }
            for (int i3 = ChatActivity.attach_photo; i3 < childCount; i3 += ChatActivity.attach_gallery) {
                View childAt = getChildAt(i3);
                if (!(childAt == null || childAt.getVisibility() == 8 || childAt == ChatActivity.this.chatActivityEnterView || childAt == ChatActivity.this.actionBar)) {
                    if (childAt == ChatActivity.this.chatListView || childAt == ChatActivity.this.progressView) {
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp((float) ((ChatActivity.this.chatActivityEnterView.isTopViewVisible() ? 48 : ChatActivity.attach_photo) + ChatActivity.attach_video)) + (paddingTop - this.inputFieldHeight)), C0700C.ENCODING_PCM_32BIT));
                    } else if (childAt == ChatActivity.this.emptyViewContainer) {
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(paddingTop, C0700C.ENCODING_PCM_32BIT));
                    } else if (ChatActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                        if (!AndroidUtilities.isInMultiwindow) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, C0700C.ENCODING_PCM_32BIT));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), (((paddingTop - this.inputFieldHeight) + measuredHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), C0700C.ENCODING_PCM_32BIT));
                        } else {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((((paddingTop - this.inputFieldHeight) + measuredHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), C0700C.ENCODING_PCM_32BIT));
                        }
                    } else if (childAt == ChatActivity.this.mentionContainer) {
                        int rowsCount;
                        LayoutParams layoutParams = (LayoutParams) ChatActivity.this.mentionContainer.getLayoutParams();
                        ChatActivity.this.mentionListViewIgnoreLayout = true;
                        int dp;
                        if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                            rowsCount = ChatActivity.this.mentionGridLayoutManager.getRowsCount(size) * 102;
                            if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                                rowsCount += 34;
                            }
                            dp = (rowsCount != 0 ? AndroidUtilities.dp(2.0f) : ChatActivity.attach_photo) + (paddingTop - ChatActivity.this.chatActivityEnterView.getMeasuredHeight());
                            ChatActivity.this.mentionListView.setPadding(ChatActivity.attach_photo, Math.max(ChatActivity.attach_photo, dp - AndroidUtilities.dp(Math.min((float) rowsCount, 122.399994f))), ChatActivity.attach_photo, ChatActivity.attach_photo);
                            rowsCount = dp;
                        } else {
                            dp = ChatActivity.this.mentionsAdapter.getItemCount();
                            rowsCount = ChatActivity.attach_photo;
                            if (ChatActivity.this.mentionsAdapter.isBotContext()) {
                                if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                                    rowsCount = 36;
                                    dp--;
                                }
                                rowsCount += dp * 68;
                            } else {
                                rowsCount = ChatActivity.attach_photo + (dp * 36);
                            }
                            dp = (rowsCount != 0 ? AndroidUtilities.dp(2.0f) : ChatActivity.attach_photo) + (paddingTop - ChatActivity.this.chatActivityEnterView.getMeasuredHeight());
                            ChatActivity.this.mentionListView.setPadding(ChatActivity.attach_photo, Math.max(ChatActivity.attach_photo, dp - AndroidUtilities.dp(Math.min((float) rowsCount, 122.399994f))), ChatActivity.attach_photo, ChatActivity.attach_photo);
                            rowsCount = dp;
                        }
                        layoutParams.height = rowsCount;
                        layoutParams.topMargin = ChatActivity.attach_photo;
                        ChatActivity.this.mentionListViewIgnoreLayout = false;
                        childAt.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(layoutParams.height, C0700C.ENCODING_PCM_32BIT));
                    } else {
                        measureChildWithMargins(childAt, i, ChatActivity.attach_photo, i2, ChatActivity.attach_photo);
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.15 */
    class AnonymousClass15 extends RecyclerListView {
        AnonymousClass15(Context context) {
            super(context);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ChatActivity.this.forceScrollToTop = false;
            if (ChatActivity.this.chatAdapter.isBot) {
                int childCount = getChildCount();
                for (int i5 = ChatActivity.attach_photo; i5 < childCount; i5 += ChatActivity.attach_gallery) {
                    View childAt = getChildAt(i5);
                    if (childAt instanceof BotHelpCell) {
                        i5 = ((i4 - i2) / ChatActivity.attach_video) - (childAt.getMeasuredHeight() / ChatActivity.attach_video);
                        if (childAt.getTop() > i5) {
                            childAt.layout(ChatActivity.attach_photo, i5, i3 - i, childAt.getMeasuredHeight() + i5);
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.16 */
    class AnonymousClass16 extends LinearLayoutManager {
        AnonymousClass16(Context context) {
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.17 */
    class AnonymousClass17 extends OnScrollListener {
        private final int scrollValue;
        private float totalDy;
        final /* synthetic */ int val$hColor;

        AnonymousClass17(int i) {
            this.val$hColor = i;
            this.totalDy = 0.0f;
            this.scrollValue = AndroidUtilities.dp(100.0f);
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == ChatActivity.attach_gallery && ChatActivity.this.highlightMessageId != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                ChatActivity.this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                ChatActivity.this.updateVisibleRows();
            }
            if (ThemeUtil.m2490b()) {
                Glow.m522a(ChatActivity.this.chatListView, this.val$hColor);
            }
            if (i == 0) {
                ChatActivity.this.hideDateTv();
            }
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            ChatActivity.this.checkScrollForLoad(true);
            int findFirstVisibleItemPosition = ChatActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
            int abs = findFirstVisibleItemPosition == -1 ? ChatActivity.attach_photo : Math.abs(ChatActivity.this.chatLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + ChatActivity.attach_gallery;
            if (abs > 0) {
                if (abs + findFirstVisibleItemPosition == ChatActivity.this.chatAdapter.getItemCount() && ChatActivity.this.forwardEndReached[ChatActivity.attach_photo]) {
                    ChatActivity.this.showPagedownButton(false, true);
                } else if (i2 > 0) {
                    if (ChatActivity.this.pagedownButton.getTag() == null) {
                        this.totalDy += (float) i2;
                        if (this.totalDy > ((float) this.scrollValue)) {
                            this.totalDy = 0.0f;
                            ChatActivity.this.showPagedownButton(true, true);
                            ChatActivity.this.pagedownButtonShowedByScroll = true;
                        }
                    }
                } else if (ChatActivity.this.pagedownButtonShowedByScroll && ChatActivity.this.pagedownButton.getTag() != null) {
                    this.totalDy += (float) i2;
                    if (this.totalDy < ((float) (-this.scrollValue))) {
                        ChatActivity.this.showPagedownButton(false, true);
                        this.totalDy = 0.0f;
                    }
                }
            }
            ChatActivity.this.updateMessagesVisisblePart();
            if (i2 != 0) {
                ChatActivity.this.updateDateToast();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.1 */
    class C12461 implements PhotoViewerProvider {
        C12461() {
        }

        public boolean allowCaption() {
            return true;
        }

        public boolean cancelButtonPressed() {
            return false;
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            if (i < 0 || i >= ChatActivity.this.botContextResults.size()) {
                return null;
            }
            int childCount = ChatActivity.this.mentionListView.getChildCount();
            BotInlineResult botInlineResult = ChatActivity.this.botContextResults.get(i);
            int i2 = ChatActivity.attach_photo;
            while (i2 < childCount) {
                ImageReceiver photoImage;
                View childAt = ChatActivity.this.mentionListView.getChildAt(i2);
                if (childAt instanceof ContextLinkCell) {
                    ContextLinkCell contextLinkCell = (ContextLinkCell) childAt;
                    if (contextLinkCell.getResult() == botInlineResult) {
                        photoImage = contextLinkCell.getPhotoImage();
                        if (photoImage == null) {
                            int[] iArr = new int[ChatActivity.attach_video];
                            childAt.getLocationInWindow(iArr);
                            PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                            placeProviderObject.viewX = iArr[ChatActivity.attach_photo];
                            placeProviderObject.viewY = iArr[ChatActivity.attach_gallery] - (VERSION.SDK_INT < ChatActivity.report ? ChatActivity.attach_photo : AndroidUtilities.statusBarHeight);
                            placeProviderObject.parentView = ChatActivity.this.mentionListView;
                            placeProviderObject.imageReceiver = photoImage;
                            placeProviderObject.thumb = photoImage.getBitmap();
                            placeProviderObject.radius = photoImage.getRoundRadius();
                            return placeProviderObject;
                        }
                        i2 += ChatActivity.attach_gallery;
                    }
                }
                photoImage = null;
                if (photoImage == null) {
                    i2 += ChatActivity.attach_gallery;
                } else {
                    int[] iArr2 = new int[ChatActivity.attach_video];
                    childAt.getLocationInWindow(iArr2);
                    PlaceProviderObject placeProviderObject2 = new PlaceProviderObject();
                    placeProviderObject2.viewX = iArr2[ChatActivity.attach_photo];
                    if (VERSION.SDK_INT < ChatActivity.report) {
                    }
                    placeProviderObject2.viewY = iArr2[ChatActivity.attach_gallery] - (VERSION.SDK_INT < ChatActivity.report ? ChatActivity.attach_photo : AndroidUtilities.statusBarHeight);
                    placeProviderObject2.parentView = ChatActivity.this.mentionListView;
                    placeProviderObject2.imageReceiver = photoImage;
                    placeProviderObject2.thumb = photoImage.getBitmap();
                    placeProviderObject2.radius = photoImage.getRoundRadius();
                    return placeProviderObject2;
                }
            }
            return null;
        }

        public int getSelectedCount() {
            return ChatActivity.attach_photo;
        }

        public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            return null;
        }

        public boolean isPhotoChecked(int i) {
            return false;
        }

        public boolean scaleToFill() {
            return false;
        }

        public void sendButtonPressed(int i) {
            if (i >= 0 && i < ChatActivity.this.botContextResults.size()) {
                ChatActivity.this.sendBotInlineResult((BotInlineResult) ChatActivity.this.botContextResults.get(i));
            }
        }

        public void setPhotoChecked(int i) {
        }

        public void updatePhotoAtIndex(int i) {
        }

        public void willHidePhotoViewer() {
        }

        public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.25 */
    class AnonymousClass25 extends FrameLayout {
        private Drawable background;

        AnonymousClass25(Context context) {
            super(context);
        }

        public void onDraw(Canvas canvas) {
            if (ChatActivity.this.mentionListView.getChildCount() > 0) {
                if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout() && ChatActivity.this.mentionsAdapter.getBotContextSwitch() == null) {
                    this.background.setBounds(ChatActivity.attach_photo, ChatActivity.this.mentionListViewScrollOffsetY - AndroidUtilities.dp(4.0f), getMeasuredWidth(), getMeasuredHeight());
                } else {
                    this.background.setBounds(ChatActivity.attach_photo, ChatActivity.this.mentionListViewScrollOffsetY - AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight());
                }
                this.background.draw(canvas);
            }
        }

        public void requestLayout() {
            if (!ChatActivity.this.mentionListViewIgnoreLayout) {
                super.requestLayout();
            }
        }

        public void setBackgroundResource(int i) {
            this.background = getContext().getResources().getDrawable(i);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.26 */
    class AnonymousClass26 extends RecyclerListView {
        private int lastHeight;
        private int lastWidth;

        AnonymousClass26(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (!ChatActivity.this.mentionListViewIsScrolling && ChatActivity.this.mentionListViewScrollOffsetY != 0 && motionEvent.getY() < ((float) ChatActivity.this.mentionListViewScrollOffsetY)) {
                return false;
            }
            return super.onInterceptTouchEvent(motionEvent) || StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, ChatActivity.this.mentionListView, ChatActivity.attach_photo);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            int i6;
            int i7 = i3 - i;
            int i8 = i4 - i2;
            if (ChatActivity.this.mentionListView == null || ChatActivity.this.mentionListViewLastViewPosition < 0 || i7 != this.lastWidth || i8 - this.lastHeight == 0) {
                i5 = ChatActivity.attach_photo;
                i6 = -1;
            } else {
                i6 = ChatActivity.this.mentionListViewLastViewPosition;
                i5 = ((ChatActivity.this.mentionListViewLastViewTop + i8) - this.lastHeight) - getPaddingTop();
            }
            super.onLayout(z, i, i2, i3, i4);
            if (i6 != -1) {
                ChatActivity.this.mentionListViewIgnoreLayout = true;
                if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                    ChatActivity.this.mentionGridLayoutManager.scrollToPositionWithOffset(i6, i5);
                } else {
                    ChatActivity.this.mentionLayoutManager.scrollToPositionWithOffset(i6, i5);
                }
                super.onLayout(false, i, i2, i3, i4);
                ChatActivity.this.mentionListViewIgnoreLayout = false;
            }
            this.lastHeight = i8;
            this.lastWidth = i7;
            ChatActivity.this.mentionListViewUpdateLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return (ChatActivity.this.mentionListViewIsScrolling || ChatActivity.this.mentionListViewScrollOffsetY == 0 || motionEvent.getY() >= ((float) ChatActivity.this.mentionListViewScrollOffsetY)) ? super.onTouchEvent(motionEvent) : false;
        }

        public void requestLayout() {
            if (!ChatActivity.this.mentionListViewIgnoreLayout) {
                super.requestLayout();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.28 */
    class AnonymousClass28 extends LinearLayoutManager {
        AnonymousClass28(Context context) {
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.29 */
    class AnonymousClass29 extends ExtendedGridLayoutManager {
        private Size size;

        AnonymousClass29(Context context, int i) {
            super(context, i);
            this.size = new Size();
        }

        protected int getFlowItemCount() {
            return ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null ? getItemCount() - 1 : super.getFlowItemCount();
        }

        protected Size getSizeForItem(int i) {
            float f = 100.0f;
            if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                i += ChatActivity.attach_gallery;
            }
            Object item = ChatActivity.this.mentionsAdapter.getItem(i);
            if (item instanceof BotInlineResult) {
                BotInlineResult botInlineResult = (BotInlineResult) item;
                if (botInlineResult.document != null) {
                    this.size.width = botInlineResult.document.thumb != null ? (float) botInlineResult.document.thumb.f2664w : 100.0f;
                    Size size = this.size;
                    if (botInlineResult.document.thumb != null) {
                        f = (float) botInlineResult.document.thumb.f2663h;
                    }
                    size.height = f;
                    for (int i2 = ChatActivity.attach_photo; i2 < botInlineResult.document.attributes.size(); i2 += ChatActivity.attach_gallery) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) botInlineResult.document.attributes.get(i2);
                        if ((documentAttribute instanceof TL_documentAttributeImageSize) || (documentAttribute instanceof TL_documentAttributeVideo)) {
                            this.size.width = (float) documentAttribute.f2659w;
                            this.size.height = (float) documentAttribute.f2658h;
                            break;
                        }
                    }
                } else {
                    this.size.width = (float) botInlineResult.f2655w;
                    this.size.height = (float) botInlineResult.f2654h;
                }
            }
            return this.size;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.2 */
    class C12492 implements OnItemLongClickListener {
        C12492() {
        }

        public boolean onItemClick(View view, int i) {
            if (ChatActivity.this.actionBar.isActionModeShowed()) {
                return false;
            }
            ChatActivity.this.createMenu(view, false);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.38 */
    class AnonymousClass38 extends FrameLayout {
        AnonymousClass38(Context context) {
            super(context);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        public void setTranslationY(float f) {
            super.setTranslationY(f);
            if (ChatActivity.this.chatActivityEnterView != null) {
                ChatActivity.this.chatActivityEnterView.invalidate();
            }
            if (getVisibility() != 8) {
                int i = getLayoutParams().height;
                if (ChatActivity.this.chatListView != null) {
                    ChatActivity.this.chatListView.setTranslationY(f);
                }
                if (ChatActivity.this.progressView != null) {
                    ChatActivity.this.progressView.setTranslationY(f);
                }
                if (ChatActivity.this.mentionContainer != null) {
                    ChatActivity.this.mentionContainer.setTranslationY(f);
                }
                if (ChatActivity.this.pagedownButton != null) {
                    ChatActivity.this.pagedownButton.setTranslationY(f);
                }
            }
        }

        public void setVisibility(int i) {
            float f = 0.0f;
            super.setVisibility(i);
            if (i == 8) {
                if (ChatActivity.this.chatListView != null) {
                    ChatActivity.this.chatListView.setTranslationY(0.0f);
                }
                if (ChatActivity.this.progressView != null) {
                    ChatActivity.this.progressView.setTranslationY(0.0f);
                }
                if (ChatActivity.this.mentionContainer != null) {
                    ChatActivity.this.mentionContainer.setTranslationY(0.0f);
                }
                if (ChatActivity.this.pagedownButton != null) {
                    FrameLayout access$7200 = ChatActivity.this.pagedownButton;
                    if (ChatActivity.this.pagedownButton.getTag() == null) {
                        f = (float) AndroidUtilities.dp(100.0f);
                    }
                    access$7200.setTranslationY(f);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.3 */
    class C12543 implements OnItemClickListener {
        C12543() {
        }

        public void onItemClick(View view, int i) {
            if (ChatActivity.this.deleteMutliMessageEnabled) {
                ChatActivity.this.processMutliSelectForDelete(view);
            } else if (ChatActivity.this.actionBar.isActionModeShowed()) {
                ChatActivity.this.processRowSelect(view);
            } else {
                ChatActivity.this.createMenu(view, true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.40 */
    class AnonymousClass40 extends RecyclerListView {
        AnonymousClass40(Context context) {
            super(context);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return super.onInterceptTouchEvent(motionEvent) || StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, ChatActivity.this.stickersListView, ChatActivity.attach_photo);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.48 */
    class AnonymousClass48 implements DialogInterface.OnClickListener {
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass48(MessageObject messageObject) {
            this.val$messageObject = messageObject;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            SendMessagesHelper.getInstance().sendMessage(UserConfig.getCurrentUser(), ChatActivity.this.dialog_id, this.val$messageObject, null, null);
            ChatActivity.this.moveScrollToLastMessage();
            ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.4 */
    class C12614 implements Runnable {
        final /* synthetic */ int val$chatId;
        final /* synthetic */ Semaphore val$semaphore;

        C12614(int i, Semaphore semaphore) {
            this.val$chatId = i;
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChatActivity.this.currentChat = MessagesStorage.getInstance().getChat(this.val$chatId);
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.56 */
    class AnonymousClass56 implements Runnable {
        final /* synthetic */ CharSequence val$charSequence;
        final /* synthetic */ boolean val$force;

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.56.1 */
        class C12621 implements Runnable {
            C12621() {
            }

            public void run() {
                if (ChatActivity.this.foundWebPage != null) {
                    ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                    ChatActivity.this.foundWebPage = null;
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.56.2 */
        class C12632 implements Runnable {
            C12632() {
            }

            public void run() {
                if (ChatActivity.this.foundWebPage != null) {
                    ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                    ChatActivity.this.foundWebPage = null;
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.56.3 */
        class C12653 implements Runnable {

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.56.3.1 */
            class C12641 implements DialogInterface.OnClickListener {
                C12641() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    MessagesController.getInstance().secretWebpagePreview = ChatActivity.attach_gallery;
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", ChatActivity.attach_photo).edit().putInt("secretWebpage2", MessagesController.getInstance().secretWebpagePreview).commit();
                    ChatActivity.this.foundUrls = null;
                    ChatActivity.this.searchLinks(AnonymousClass56.this.val$charSequence, AnonymousClass56.this.val$force);
                }
            }

            C12653() {
            }

            public void run() {
                Builder builder = new Builder(ChatActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C12641());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                builder.setMessage(LocaleController.getString("SecretLinkPreviewAlert", C0338R.string.SecretLinkPreviewAlert));
                ChatActivity.this.showDialog(builder.create());
                MessagesController.getInstance().secretWebpagePreview = ChatActivity.attach_photo;
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", ChatActivity.attach_photo).edit().putInt("secretWebpage2", MessagesController.getInstance().secretWebpagePreview).commit();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.56.4 */
        class C12674 implements RequestDelegate {
            final /* synthetic */ TL_messages_getWebPagePreview val$req;

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.56.4.1 */
            class C12661 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C12661(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    ChatActivity.this.linkSearchRequestId = ChatActivity.attach_photo;
                    if (this.val$error != null) {
                        return;
                    }
                    if (this.val$response instanceof TL_messageMediaWebPage) {
                        ChatActivity.this.foundWebPage = ((TL_messageMediaWebPage) this.val$response).webpage;
                        if ((ChatActivity.this.foundWebPage instanceof TL_webPage) || (ChatActivity.this.foundWebPage instanceof TL_webPagePending)) {
                            if (ChatActivity.this.foundWebPage instanceof TL_webPagePending) {
                                ChatActivity.this.pendingLinkSearchString = C12674.this.val$req.message;
                            }
                            if (ChatActivity.this.currentEncryptedChat != null && (ChatActivity.this.foundWebPage instanceof TL_webPagePending)) {
                                ChatActivity.this.foundWebPage.url = C12674.this.val$req.message;
                            }
                            ChatActivity.this.showReplyPanel(true, null, null, ChatActivity.this.foundWebPage, false, true);
                        } else if (ChatActivity.this.foundWebPage != null) {
                            ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                            ChatActivity.this.foundWebPage = null;
                        }
                    } else if (ChatActivity.this.foundWebPage != null) {
                        ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false, true);
                        ChatActivity.this.foundWebPage = null;
                    }
                }
            }

            C12674(TL_messages_getWebPagePreview tL_messages_getWebPagePreview) {
                this.val$req = tL_messages_getWebPagePreview;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C12661(tL_error, tLObject));
            }
        }

        AnonymousClass56(CharSequence charSequence, boolean z) {
            this.val$charSequence = charSequence;
            this.val$force = z;
        }

        public void run() {
            boolean z = true;
            if (ChatActivity.this.linkSearchRequestId != 0) {
                ConnectionsManager.getInstance().cancelRequest(ChatActivity.this.linkSearchRequestId, true);
                ChatActivity.this.linkSearchRequestId = ChatActivity.attach_photo;
            }
            CharSequence join;
            try {
                Matcher matcher = AndroidUtilities.WEB_URL.matcher(this.val$charSequence);
                Iterable iterable = null;
                while (matcher.find()) {
                    if (matcher.start() <= 0 || this.val$charSequence.charAt(matcher.start() - 1) != '@') {
                        ArrayList arrayList;
                        if (iterable == null) {
                            arrayList = new ArrayList();
                        } else {
                            Iterable iterable2 = iterable;
                        }
                        arrayList.add(this.val$charSequence.subSequence(matcher.start(), matcher.end()));
                        iterable = arrayList;
                    }
                }
                if (!(iterable == null || ChatActivity.this.foundUrls == null || iterable.size() != ChatActivity.this.foundUrls.size())) {
                    int i = ChatActivity.attach_photo;
                    while (i < iterable.size()) {
                        boolean z2 = !TextUtils.equals((CharSequence) iterable.get(i), (CharSequence) ChatActivity.this.foundUrls.get(i)) ? ChatActivity.attach_photo : z;
                        i += ChatActivity.attach_gallery;
                        z = z2;
                    }
                    if (z) {
                        return;
                    }
                }
                ChatActivity.this.foundUrls = iterable;
                if (iterable == null) {
                    AndroidUtilities.runOnUIThread(new C12621());
                    return;
                }
                join = TextUtils.join(" ", iterable);
                if (ChatActivity.this.currentEncryptedChat == null || MessagesController.getInstance().secretWebpagePreview != ChatActivity.attach_video) {
                    TLObject tL_messages_getWebPagePreview = new TL_messages_getWebPagePreview();
                    if (join instanceof String) {
                        tL_messages_getWebPagePreview.message = (String) join;
                    } else {
                        tL_messages_getWebPagePreview.message = join.toString();
                    }
                    ChatActivity.this.linkSearchRequestId = ConnectionsManager.getInstance().sendRequest(tL_messages_getWebPagePreview, new C12674(tL_messages_getWebPagePreview));
                    ConnectionsManager.getInstance().bindRequestToGuid(ChatActivity.this.linkSearchRequestId, ChatActivity.this.classGuid);
                    return;
                }
                AndroidUtilities.runOnUIThread(new C12653());
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                String toLowerCase = this.val$charSequence.toString().toLowerCase();
                if (this.val$charSequence.length() < ChatActivity.chat_enc_timer || !(toLowerCase.contains("http://") || toLowerCase.contains("https://"))) {
                    AndroidUtilities.runOnUIThread(new C12632());
                    return;
                }
                join = this.val$charSequence;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.58 */
    class AnonymousClass58 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$newVisibility;
        final /* synthetic */ ActionBarMenuItem val$replyItem;

        AnonymousClass58(int i, ActionBarMenuItem actionBarMenuItem) {
            this.val$newVisibility = i;
            this.val$replyItem = actionBarMenuItem;
        }

        public void onAnimationCancel(Animator animator) {
            if (ChatActivity.this.replyButtonAnimation != null && ChatActivity.this.replyButtonAnimation.equals(animator)) {
                ChatActivity.this.replyButtonAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (ChatActivity.this.replyButtonAnimation != null && ChatActivity.this.replyButtonAnimation.equals(animator) && this.val$newVisibility == 8) {
                this.val$replyItem.setVisibility(8);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.5 */
    class C12685 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;
        final /* synthetic */ int val$userId;

        C12685(int i, Semaphore semaphore) {
            this.val$userId = i;
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChatActivity.this.currentUser = MessagesStorage.getInstance().getUser(this.val$userId);
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.60 */
    class AnonymousClass60 extends EmptyPhotoViewerProvider {
        final /* synthetic */ ArrayList val$arrayList;
        final /* synthetic */ boolean val$fromMarkers;

        AnonymousClass60(boolean z, ArrayList arrayList) {
            this.val$fromMarkers = z;
            this.val$arrayList = arrayList;
        }

        public void sendButtonPressed(int i) {
            if (this.val$fromMarkers) {
                ChatActivity.this.showSendDrawingOptions((PhotoEntry) this.val$arrayList.get(ChatActivity.attach_photo));
                return;
            }
            ChatActivity.this.sendMedia((PhotoEntry) this.val$arrayList.get(ChatActivity.attach_photo), false);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.62 */
    class AnonymousClass62 implements Runnable {
        final /* synthetic */ int val$last_unread_date_final;
        final /* synthetic */ int val$lastid;
        final /* synthetic */ boolean val$wasUnreadFinal;

        AnonymousClass62(int i, int i2, boolean z) {
            this.val$lastid = i;
            this.val$last_unread_date_final = i2;
            this.val$wasUnreadFinal = z;
        }

        public void run() {
            if (ChatActivity.this.last_message_id != 0) {
                MessagesController.getInstance().markDialogAsRead(ChatActivity.this.dialog_id, this.val$lastid, ChatActivity.this.last_message_id, this.val$last_unread_date_final, this.val$wasUnreadFinal, false);
            } else {
                MessagesController.getInstance().markDialogAsRead(ChatActivity.this.dialog_id, this.val$lastid, ChatActivity.this.minMessageId[ChatActivity.attach_photo], ChatActivity.this.maxDate[ChatActivity.attach_photo], this.val$wasUnreadFinal, false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.63 */
    class AnonymousClass63 implements Runnable {
        final /* synthetic */ Bundle val$bundle;
        final /* synthetic */ int val$channel_id;
        final /* synthetic */ BaseFragment val$lastFragment;

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.63.1 */
        class C12691 implements Runnable {
            C12691() {
            }

            public void run() {
                MessagesController.getInstance().loadFullChat(AnonymousClass63.this.val$channel_id, ChatActivity.attach_photo, true);
            }
        }

        AnonymousClass63(BaseFragment baseFragment, Bundle bundle, int i) {
            this.val$lastFragment = baseFragment;
            this.val$bundle = bundle;
            this.val$channel_id = i;
        }

        public void run() {
            ActionBarLayout access$15100 = ChatActivity.this.parentLayout;
            if (this.val$lastFragment != null) {
                NotificationCenter.getInstance().removeObserver(this.val$lastFragment, NotificationCenter.closeChats);
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[ChatActivity.attach_photo]);
            access$15100.presentFragment(new ChatActivity(this.val$bundle), true);
            AndroidUtilities.runOnUIThread(new C12691(), 1000);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.64 */
    class AnonymousClass64 implements Runnable {
        final /* synthetic */ Bundle val$bundle;
        final /* synthetic */ int val$channel_id;
        final /* synthetic */ BaseFragment val$lastFragment;

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.64.1 */
        class C12701 implements Runnable {
            C12701() {
            }

            public void run() {
                MessagesController.getInstance().loadFullChat(AnonymousClass64.this.val$channel_id, ChatActivity.attach_photo, true);
            }
        }

        AnonymousClass64(BaseFragment baseFragment, Bundle bundle, int i) {
            this.val$lastFragment = baseFragment;
            this.val$bundle = bundle;
            this.val$channel_id = i;
        }

        public void run() {
            ActionBarLayout access$15200 = ChatActivity.this.parentLayout;
            if (this.val$lastFragment != null) {
                NotificationCenter.getInstance().removeObserver(this.val$lastFragment, NotificationCenter.closeChats);
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[ChatActivity.attach_photo]);
            access$15200.presentFragment(new ChatActivity(this.val$bundle), true);
            AndroidUtilities.runOnUIThread(new C12701(), 1000);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.6 */
    class C12726 implements Runnable {
        final /* synthetic */ int val$encId;
        final /* synthetic */ Semaphore val$semaphore;

        C12726(int i, Semaphore semaphore) {
            this.val$encId = i;
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChatActivity.this.currentEncryptedChat = MessagesStorage.getInstance().getEncryptedChat(this.val$encId);
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.75 */
    class AnonymousClass75 implements OnClickListener {
        final /* synthetic */ boolean[] val$checks;

        AnonymousClass75(boolean[] zArr) {
            this.val$checks = zArr;
        }

        public void onClick(View view) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            Integer num = (Integer) checkBoxCell.getTag();
            this.val$checks[num.intValue()] = !this.val$checks[num.intValue()];
            checkBoxCell.setChecked(this.val$checks[num.intValue()], true);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.78 */
    class AnonymousClass78 implements DialogInterface.OnClickListener {
        final /* synthetic */ boolean[] val$checks;
        final /* synthetic */ MessageObject val$finalSelectedObject;
        final /* synthetic */ User val$userFinal;

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.78.1 */
        class C12731 implements RequestDelegate {
            C12731() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
            }
        }

        AnonymousClass78(MessageObject messageObject, User user, boolean[] zArr) {
            this.val$finalSelectedObject = messageObject;
            this.val$userFinal = user;
            this.val$checks = zArr;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ArrayList arrayList = null;
            ArrayList arrayList2;
            if (this.val$finalSelectedObject != null) {
                arrayList = new ArrayList();
                arrayList.add(Integer.valueOf(this.val$finalSelectedObject.getId()));
                arrayList2 = null;
                if (!(ChatActivity.this.currentEncryptedChat == null || this.val$finalSelectedObject.messageOwner.random_id == 0 || this.val$finalSelectedObject.type == ChatActivity.copy)) {
                    arrayList2 = new ArrayList();
                    arrayList2.add(Long.valueOf(this.val$finalSelectedObject.messageOwner.random_id));
                }
                MessagesController.getInstance().deleteMessages(arrayList, arrayList2, ChatActivity.this.currentEncryptedChat, this.val$finalSelectedObject.messageOwner.to_id.channel_id, ChatActivity.this.deleteFilesOnDeleteMessage);
                if (ArchiveUtil.m265a(ChatActivity.this.currentUser)) {
                    ArchiveUtil.m263a((List) arrayList);
                }
            } else {
                for (int i2 = ChatActivity.attach_gallery; i2 >= 0; i2--) {
                    MessageObject messageObject;
                    List arrayList3 = new ArrayList(ChatActivity.this.selectedMessagesIds[i2].keySet());
                    arrayList2 = null;
                    int i3 = ChatActivity.attach_photo;
                    if (!arrayList3.isEmpty()) {
                        messageObject = (MessageObject) ChatActivity.this.selectedMessagesIds[i2].get(arrayList3.get(ChatActivity.attach_photo));
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            i3 = messageObject.messageOwner.to_id.channel_id;
                        }
                    }
                    if (ChatActivity.this.currentEncryptedChat != null) {
                        arrayList2 = new ArrayList();
                        for (Entry value : ChatActivity.this.selectedMessagesIds[i2].entrySet()) {
                            messageObject = (MessageObject) value.getValue();
                            if (!(messageObject.messageOwner.random_id == 0 || messageObject.type == ChatActivity.copy)) {
                                arrayList2.add(Long.valueOf(messageObject.messageOwner.random_id));
                            }
                        }
                    }
                    MessagesController.getInstance().deleteMessages(arrayList3, arrayList2, ChatActivity.this.currentEncryptedChat, i3, ChatActivity.this.deleteFilesOnDeleteMessage);
                    if (ArchiveUtil.m265a(ChatActivity.this.currentUser)) {
                        ArchiveUtil.m263a(arrayList3);
                    }
                }
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
            }
            if (this.val$userFinal != null) {
                if (this.val$checks[ChatActivity.attach_photo]) {
                    MessagesController.getInstance().deleteUserFromChat(ChatActivity.this.currentChat.id, this.val$userFinal, ChatActivity.this.info);
                }
                if (this.val$checks[ChatActivity.attach_gallery]) {
                    TLObject tL_channels_reportSpam = new TL_channels_reportSpam();
                    tL_channels_reportSpam.channel = MessagesController.getInputChannel(ChatActivity.this.currentChat);
                    tL_channels_reportSpam.user_id = MessagesController.getInputUser(this.val$userFinal);
                    tL_channels_reportSpam.id = arrayList;
                    ConnectionsManager.getInstance().sendRequest(tL_channels_reportSpam, new C12731());
                }
                if (this.val$checks[ChatActivity.attach_video]) {
                    MessagesController.getInstance().deleteUserChannelHistory(ChatActivity.this.currentChat, this.val$userFinal, ChatActivity.attach_photo);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.79 */
    class AnonymousClass79 implements DialogInterface.OnClickListener {
        final /* synthetic */ ArrayList val$options;

        AnonymousClass79(ArrayList arrayList) {
            this.val$options = arrayList;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (ChatActivity.this.selectedObject != null && i >= 0 && i < this.val$options.size()) {
                ChatActivity.this.processSelectedOption(((Integer) this.val$options.get(i)).intValue());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.7 */
    class C12747 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;

        C12747(Semaphore semaphore) {
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChatActivity.this.currentUser = MessagesStorage.getInstance().getUser(ChatActivity.this.currentEncryptedChat.user_id);
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.80 */
    class AnonymousClass80 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$show;

        AnonymousClass80(boolean z) {
            this.val$show = z;
        }

        public void onAnimationCancel(Animator animator) {
            if (ChatActivity.this.editDoneItemAnimation != null && ChatActivity.this.editDoneItemAnimation.equals(animator)) {
                ChatActivity.this.editDoneItemAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (ChatActivity.this.editDoneItemAnimation != null && ChatActivity.this.editDoneItemAnimation.equals(animator)) {
                if (this.val$show) {
                    ChatActivity.this.editDoneItem.getImageView().setVisibility(ChatActivity.attach_document);
                } else {
                    ChatActivity.this.editDoneItemProgress.setVisibility(ChatActivity.attach_document);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.82 */
    class AnonymousClass82 implements OnClickListener {
        final /* synthetic */ boolean[] val$checks;

        AnonymousClass82(boolean[] zArr) {
            this.val$checks = zArr;
        }

        public void onClick(View view) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            this.val$checks[ChatActivity.attach_photo] = !this.val$checks[ChatActivity.attach_photo];
            checkBoxCell.setChecked(this.val$checks[ChatActivity.attach_photo], true);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.83 */
    class AnonymousClass83 implements DialogInterface.OnClickListener {
        final /* synthetic */ boolean[] val$checks;
        final /* synthetic */ int val$mid;

        AnonymousClass83(int i, boolean[] zArr) {
            this.val$mid = i;
            this.val$checks = zArr;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            MessagesController.getInstance().pinChannelMessage(ChatActivity.this.currentChat, this.val$mid, this.val$checks[ChatActivity.attach_photo]);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.86 */
    class AnonymousClass86 implements DialogInterface.OnClickListener {
        final /* synthetic */ TL_game val$game;
        final /* synthetic */ MessageObject val$messageObject;
        final /* synthetic */ int val$uid;
        final /* synthetic */ String val$urlStr;

        AnonymousClass86(TL_game tL_game, MessageObject messageObject, String str, int i) {
            this.val$game = tL_game;
            this.val$messageObject = messageObject;
            this.val$urlStr = str;
            this.val$uid = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ChatActivity.this.showOpenGameAlert(this.val$game, this.val$messageObject, this.val$urlStr, false, this.val$uid);
            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", ChatActivity.attach_photo).edit().putBoolean("askgame_" + this.val$uid, false).commit();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.87 */
    class AnonymousClass87 implements DialogInterface.OnClickListener {
        final /* synthetic */ String val$url;

        AnonymousClass87(String str) {
            this.val$url = str;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Browser.openUrl(ChatActivity.this.getParentActivity(), this.val$url, ChatActivity.this.inlineReturn == 0);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.89 */
    class AnonymousClass89 implements StickersAlertDelegate {
        final /* synthetic */ Document val$document;

        AnonymousClass89(Document document) {
            this.val$document = document;
        }

        public void onStickerSelected(Document document) {
            if (this.val$document instanceof TL_document) {
                SendMessagesHelper.getInstance().sendSticker(this.val$document, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                ChatActivity.this.chatActivityEnterView.addStickerToRecent(this.val$document);
            }
            ChatActivity.this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.8 */
    class C12788 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.8.1 */
        class C12751 implements DialogInterface.OnClickListener {
            final /* synthetic */ int val$id;
            final /* synthetic */ boolean val$isChat;

            C12751(int i, boolean z) {
                this.val$id = i;
                this.val$isChat = z;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (this.val$id != ChatActivity.clear_history) {
                    if (!this.val$isChat) {
                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_photo);
                    } else if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_photo);
                    } else {
                        MessagesController.getInstance().deleteUserFromChat((int) (-ChatActivity.this.dialog_id), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                    }
                    ChatActivity.this.finishFragment();
                    return;
                }
                MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_gallery);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.8.2 */
        class C12762 implements DialogInterface.OnClickListener {
            C12762() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance().deleteUserFromChat((int) (-ChatActivity.this.dialog_id), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null, false);
                ChatActivity.this.finishFragment();
            }
        }

        C12788() {
        }

        public void onItemClick(int i) {
            int i2;
            if (i == -1) {
                if (ChatActivity.this.selectionMode) {
                    ChatActivity.this.finishFragment();
                } else if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    for (i2 = ChatActivity.attach_gallery; i2 >= 0; i2--) {
                        ChatActivity.this.selectedMessagesIds[i2].clear();
                        ChatActivity.this.selectedMessagesCanCopyIds[i2].clear();
                    }
                    ChatActivity.this.cantDeleteMessagesCount = ChatActivity.attach_photo;
                    if (ChatActivity.this.chatActivityEnterView.isEditingMessage()) {
                        ChatActivity.this.chatActivityEnterView.setEditingMessageObject(null, false);
                    } else {
                        ChatActivity.this.actionBar.hideActionMode();
                        ChatActivity.this.updatePinnedMessageView(true);
                    }
                    ChatActivity.this.updateVisibleRows();
                } else {
                    ChatActivity.this.finishFragment();
                }
            } else if (i == ChatActivity.copy) {
                CharSequence charSequence = TtmlNode.ANONYMOUS_REGION_ID;
                r1 = ChatActivity.attach_photo;
                int i3 = ChatActivity.attach_gallery;
                while (i3 >= 0) {
                    List arrayList = new ArrayList(ChatActivity.this.selectedMessagesCanCopyIds[i3].keySet());
                    if (ChatActivity.this.currentEncryptedChat == null) {
                        Collections.sort(arrayList);
                    } else {
                        Collections.sort(arrayList, Collections.reverseOrder());
                    }
                    int i4 = r1;
                    String str = charSequence;
                    int i5 = ChatActivity.attach_photo;
                    while (i5 < arrayList.size()) {
                        MessageObject messageObject = (MessageObject) ChatActivity.this.selectedMessagesCanCopyIds[i3].get((Integer) arrayList.get(i5));
                        if (str.length() != 0) {
                            str = str + "\n\n";
                        }
                        String str2 = str + ChatActivity.this.getMessageContent(messageObject, i4, MoboConstants.ac);
                        i5 += ChatActivity.attach_gallery;
                        str = str2;
                        i4 = messageObject.messageOwner.from_id;
                    }
                    i3--;
                    Object obj = str;
                    r1 = i4;
                }
                if (charSequence.length() != 0) {
                    AndroidUtilities.addToClipboard(charSequence);
                }
                for (i2 = ChatActivity.attach_gallery; i2 >= 0; i2--) {
                    ChatActivity.this.selectedMessagesIds[i2].clear();
                    ChatActivity.this.selectedMessagesCanCopyIds[i2].clear();
                }
                ChatActivity.this.cantDeleteMessagesCount = ChatActivity.attach_photo;
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
            } else if (i == ChatActivity.edit_done) {
                if (ChatActivity.this.selectionMode) {
                    if (ChatActivity.this.selectionDelegate != null) {
                        ArrayList access$2000 = ChatActivity.this.getSelectedMessages();
                        List arrayList2 = new ArrayList();
                        Iterator it = access$2000.iterator();
                        while (it.hasNext()) {
                            arrayList2.add(Integer.valueOf(((MessageObject) it.next()).getId()));
                        }
                        ChatActivity.this.selectionDelegate.didSelectMessages(arrayList2);
                    }
                    ChatActivity.this.finishFragment();
                } else if (ChatActivity.this.chatActivityEnterView == null) {
                } else {
                    if (ChatActivity.this.chatActivityEnterView.isEditingCaption() || ChatActivity.this.chatActivityEnterView.hasText()) {
                        ChatActivity.this.chatActivityEnterView.doneEditingMessage();
                    }
                }
            } else if (i == ChatActivity.delete) {
                if (ChatActivity.this.getParentActivity() != null) {
                    ChatActivity.this.createDeleteMessagesAlert(null);
                }
            } else if (i == ChatActivity.forward) {
                ChatActivity.forwardNoName = false;
                if (MoboConstants.f1342i) {
                    ChatActivity.this.multipleForward();
                    return;
                }
                r0 = new Bundle();
                r0.putBoolean("onlySelect", true);
                r0.putInt("dialogsType", ChatActivity.attach_gallery);
                r1 = new DialogsActivity(r0);
                r1.setDelegate(ChatActivity.this);
                ChatActivity.this.presentFragment(r1);
            } else if (i == ChatActivity.forward_noname) {
                ChatActivity.forwardNoName = true;
                if (MoboConstants.f1342i) {
                    ChatActivity.this.multipleForward();
                    return;
                }
                r0 = new Bundle();
                r0.putBoolean("onlySelect", true);
                r0.putInt("dialogsType", ChatActivity.attach_gallery);
                r1 = new DialogsActivity(r0);
                r1.setDelegate(ChatActivity.this);
                ChatActivity.this.presentFragment(r1);
            } else if (i == ChatActivity.chat_enc_timer) {
                if (ChatActivity.this.getParentActivity() != null) {
                    ChatActivity.this.showDialog(AndroidUtilities.buildTTLAlert(ChatActivity.this.getParentActivity(), ChatActivity.this.currentEncryptedChat).create());
                }
            } else if (i == ChatActivity.clear_history || i == ChatActivity.delete_chat) {
                if (ChatActivity.this.getParentActivity() != null) {
                    boolean z = ((int) ChatActivity.this.dialog_id) < 0 && ((int) (ChatActivity.this.dialog_id >> 32)) != ChatActivity.attach_gallery;
                    Builder builder = new Builder(ChatActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    if (i == ChatActivity.clear_history) {
                        builder.setMessage(LocaleController.getString("AreYouSureClearHistory", C0338R.string.AreYouSureClearHistory));
                    } else if (z) {
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0338R.string.AreYouSureDeleteAndExit));
                    } else {
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", C0338R.string.AreYouSureDeleteThisChat));
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C12751(i, z));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    ChatActivity.this.showDialog(builder.create());
                }
            } else if (i == ChatActivity.leave_without_delete) {
                if (ChatActivity.this.getParentActivity() != null) {
                    r0 = new Builder(ChatActivity.this.getParentActivity());
                    r0.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    r0.setMessage(LocaleController.getString("AreYouSureLeaveWithoutDelete", C0338R.string.AreYouSureLeaveWithoutDelete));
                    r0.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C12762());
                    r0.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    ChatActivity.this.showDialog(r0.create());
                }
            } else if (i == ChatActivity.share_contact) {
                if (ChatActivity.this.currentUser != null && ChatActivity.this.getParentActivity() != null) {
                    if (ChatActivity.this.currentUser.phone == null || ChatActivity.this.currentUser.phone.length() == 0) {
                        ChatActivity.this.shareMyContact(ChatActivity.this.replyingMessageObject);
                        return;
                    }
                    r0 = new Bundle();
                    r0.putInt("user_id", ChatActivity.this.currentUser.id);
                    r0.putBoolean("addContact", true);
                    ChatActivity.this.presentFragment(new ContactAddActivity(r0));
                }
            } else if (i == ChatActivity.mute) {
                ChatActivity.this.toggleMute(false);
            } else if (i == ChatActivity.report) {
                ChatActivity.this.showDialog(AlertsCreator.createReportAlert(ChatActivity.this.getParentActivity(), ChatActivity.this.dialog_id, ChatActivity.this));
            } else if (i == ChatActivity.reply) {
                MessageObject messageObject2 = null;
                r1 = ChatActivity.attach_gallery;
                while (r1 >= 0) {
                    if (messageObject2 == null && ChatActivity.this.selectedMessagesIds[r1].size() == ChatActivity.attach_gallery) {
                        messageObject2 = (MessageObject) ChatActivity.this.messagesDict[r1].get(new ArrayList(ChatActivity.this.selectedMessagesIds[r1].keySet()).get(ChatActivity.attach_photo));
                    }
                    ChatActivity.this.selectedMessagesIds[r1].clear();
                    ChatActivity.this.selectedMessagesCanCopyIds[r1].clear();
                    r1--;
                }
                if (messageObject2 != null && (messageObject2.messageOwner.id > 0 || (messageObject2.messageOwner.id < 0 && ChatActivity.this.currentEncryptedChat != null))) {
                    ChatActivity.this.showReplyPanel(true, messageObject2, null, null, false, true);
                }
                ChatActivity.this.cantDeleteMessagesCount = ChatActivity.attach_photo;
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
            } else if (i == ChatActivity.chat_menu_attach) {
                if (ChatActivity.this.getParentActivity() != null) {
                    ChatActivity.this.createChatAttachView();
                    ChatActivity.this.chatAttachAlert.loadGalleryPhotos();
                    if (VERSION.SDK_INT == ChatActivity.report || VERSION.SDK_INT == 22) {
                        ChatActivity.this.chatActivityEnterView.closeKeyboard();
                    }
                    ChatActivity.this.chatAttachAlert.init();
                    ChatActivity.this.showDialog(ChatActivity.this.chatAttachAlert);
                }
            } else if (i == ChatActivity.bot_help) {
                SendMessagesHelper.getInstance().sendMessage("/help", ChatActivity.this.dialog_id, null, null, false, null, null, null);
            } else if (i == ChatActivity.bot_settings) {
                SendMessagesHelper.getInstance().sendMessage("/settings", ChatActivity.this.dialog_id, null, null, false, null, null, null);
            } else if (i == ChatActivity.search) {
                ChatActivity.this.openSearchWithText(null);
            } else if (i == ChatActivity.delete_multi_message) {
                ChatActivity.this.deleteMutliMessageEnabled = true;
                ChatActivity.this.selectedFromToIds.clear();
                r0 = new Builder(ChatActivity.this.getParentActivity());
                r0.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                r0.setMessage(LocaleController.getString("DeleteMultiMessageAlert", C0338R.string.DeleteMultiMessageAlert));
                r0.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                ChatActivity.this.showDialog(r0.create());
            } else if (i == ChatActivity.add_to_download_list) {
                ChatActivity.this.addMessagesToDownloadList();
            } else if (i == ChatActivity.jump_to_message) {
                ChatActivity.this.gotoMessageSelected = true;
                ChatActivity.this.scrollToMessageId(ChatActivity.attach_gallery, ChatActivity.attach_photo, false, ChatActivity.attach_photo);
            } else if (i == ChatActivity.chat_menu_drawing) {
                if (ChatActivity.this.getParentActivity() != null) {
                    Intent intent = new Intent(ChatActivity.this.getParentActivity(), MarkersActivity.class);
                    File generatePictureInCachePath = AndroidUtilities.generatePictureInCachePath();
                    if (generatePictureInCachePath != null) {
                        intent.putExtra("output", generatePictureInCachePath.getPath());
                        ChatActivity.this.currentPicturePath = generatePictureInCachePath.getAbsolutePath();
                    }
                    ChatActivity.this.startActivityForResult(intent, ChatActivity.attach_photo);
                }
            } else if (i == ChatActivity.select_all) {
                ChatActivity.this.doSelectAllMessages();
            } else if (i == ChatActivity.chat_settings) {
                r0 = new Bundle();
                r0.putLong("dialogId", ChatActivity.this.dialog_id);
                ChatActivity.this.presentFragment(new DialogSettingsActivity(r0));
            } else if (i == ChatActivity.go_to_marker) {
                ChatActivity.this.gotoChatMarker();
            } else if (i == ChatActivity.show_favorite_messages) {
                ChatActivity.this.showFavoriteMessages();
            } else if (i == ChatActivity.chat_menu_archive) {
                ChatActivity.this.showArchiveListForSelectDialog();
            } else if (i == ChatActivity.chat_menu_media) {
                ChatActivity.this.showMediaFragment();
            } else if (i == ChatActivity.chat_menu_archive_setting) {
                ChatActivity.this.presentFragment(new ArchiveSettingsActivity());
            } else if (i == ChatActivity.add_message_to_archive) {
                ChatActivity.this.addMessageToArchive();
            } else if (i == ChatActivity.remove_message_from_archive) {
                ChatActivity.this.removeMessageFromArchive();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.91 */
    class AnonymousClass91 implements DialogInterface.OnClickListener {
        final /* synthetic */ MessageObject val$firstMessage;
        final /* synthetic */ ArrayList val$selectedIds;

        AnonymousClass91(ArrayList arrayList, MessageObject messageObject) {
            this.val$selectedIds = arrayList;
            this.val$firstMessage = messageObject;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            List arrayList = new ArrayList(this.val$selectedIds);
            int i2 = ChatActivity.attach_photo;
            if (!(arrayList.isEmpty() || this.val$firstMessage.messageOwner.to_id.channel_id == 0)) {
                i2 = this.val$firstMessage.messageOwner.to_id.channel_id;
            }
            MessagesController.getInstance().deleteMessages(arrayList, null, ChatActivity.this.currentEncryptedChat, i2, ChatActivity.this.deleteFilesOnDeleteMessage);
            if (ArchiveUtil.m265a(ChatActivity.this.currentUser)) {
                ArchiveUtil.m263a(arrayList);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.93 */
    class AnonymousClass93 implements DialogInterface.OnClickListener {
        final /* synthetic */ PhotoEntry val$photoEntry;

        AnonymousClass93(PhotoEntry photoEntry) {
            this.val$photoEntry = photoEntry;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i != 0) {
                ChatActivity.this.multipleForwardDrawing(this.val$photoEntry);
            } else if (this.val$photoEntry.imagePath != null) {
                SendMessagesHelper.prepareSendingPhoto(this.val$photoEntry.imagePath, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, this.val$photoEntry.caption, null);
                ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
            } else if (this.val$photoEntry.path != null) {
                SendMessagesHelper.prepareSendingPhoto(this.val$photoEntry.path, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, this.val$photoEntry.caption, null);
                ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.94 */
    class AnonymousClass94 implements SendDelegate {
        final /* synthetic */ PhotoEntry val$photoEntry;

        AnonymousClass94(PhotoEntry photoEntry) {
            this.val$photoEntry = photoEntry;
        }

        public void send(long j, Long l) {
            if (this.val$photoEntry.imagePath != null) {
                SendMessagesHelper.prepareSendingPhoto(this.val$photoEntry.imagePath, null, j, null, this.val$photoEntry.caption, null);
            } else if (this.val$photoEntry.path != null) {
                SendMessagesHelper.prepareSendingPhoto(this.val$photoEntry.path, null, j, null, this.val$photoEntry.caption, null);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.95 */
    class AnonymousClass95 implements DialogInterface.OnClickListener {
        final /* synthetic */ ArrayList val$options;
        final /* synthetic */ MessageObject val$selectedMessage;

        AnonymousClass95(ArrayList arrayList, MessageObject messageObject) {
            this.val$options = arrayList;
            this.val$selectedMessage = messageObject;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i >= 0 && i < this.val$options.size() && ((Integer) this.val$options.get(i)).intValue() == 0 && ChatActivity.this.currentChat != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("chat_id", ChatActivity.this.currentChat.id);
                bundle.putInt("just_from_id", this.val$selectedMessage.messageOwner.from_id);
                ChatActivity.this.presentFragment(new ChatActivity(bundle));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.96 */
    class AnonymousClass96 implements DialogInterface.OnClickListener {
        final /* synthetic */ List val$archiveList;
        final /* synthetic */ MessageObject val$message;

        AnonymousClass96(List list, MessageObject messageObject) {
            this.val$archiveList = list;
            this.val$message = messageObject;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == this.val$archiveList.size()) {
                ChatActivity.this.showNewArchiveDialog(this.val$message);
            } else if (i == this.val$archiveList.size() + ChatActivity.attach_gallery) {
                ChatActivity.this.addMessageToArchive(Long.valueOf(-1), this.val$message);
            } else {
                ChatActivity.this.addMessageToArchive(((Archive) this.val$archiveList.get(i)).m204a(), this.val$message);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.97 */
    class AnonymousClass97 implements DialogInterface.OnClickListener {
        final /* synthetic */ List val$archiveList;

        AnonymousClass97(List list) {
            this.val$archiveList = list;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ChatActivity.this.archive = (Archive) this.val$archiveList.get(i);
            if (ChatActivity.this.archive.m204a().longValue() == -1) {
                ChatActivity.this.archive.m211e().addAll(ArchiveUtil.f143a);
            }
            ChatActivity.this.resetChat();
            ChatActivity.this.avatarContainer.updateSubtitle();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.98 */
    class AnonymousClass98 implements DialogInterface.OnClickListener {
        final /* synthetic */ EditText val$editTextFinal;
        final /* synthetic */ MessageObject val$message;

        AnonymousClass98(EditText editText, MessageObject messageObject) {
            this.val$editTextFinal = editText;
            this.val$message = messageObject;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            String obj = this.val$editTextFinal.getText().toString();
            if (obj.length() > 0) {
                Archive archive = new Archive();
                archive.m207a(obj);
                ChatActivity.this.addMessageToArchive(ArchiveUtil.m258a(archive), this.val$message);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChatActivity.9 */
    class C12809 extends ActionBarMenuItemSearchListener {

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.9.1 */
        class C12791 implements Runnable {
            C12791() {
            }

            public void run() {
                ChatActivity.this.searchItem.getSearchField().requestFocus();
                AndroidUtilities.showKeyboard(ChatActivity.this.searchItem.getSearchField());
            }
        }

        C12809() {
        }

        public void onSearchCollapse() {
            ChatActivity.this.avatarContainer.setVisibility(ChatActivity.attach_photo);
            if (ChatActivity.this.chatActivityEnterView.hasText()) {
                if (ChatActivity.this.headerItem != null) {
                    ChatActivity.this.headerItem.setVisibility(8);
                }
                if (ChatActivity.this.attachItem != null) {
                    ChatActivity.this.attachItem.setVisibility(ChatActivity.attach_photo);
                }
                if (ChatActivity.this.archiveItem != null) {
                    ChatActivity.this.archiveItem.setVisibility(8);
                }
                if (ChatActivity.this.archiveSettingItem != null) {
                    ChatActivity.this.archiveSettingItem.setVisibility(8);
                }
                if (ChatActivity.this.mediaItem != null) {
                    ChatActivity.this.mediaItem.setVisibility(8);
                }
            } else {
                if (ChatActivity.this.headerItem != null) {
                    ChatActivity.this.headerItem.setVisibility(ChatActivity.attach_photo);
                }
                if (ChatActivity.this.attachItem != null) {
                    ChatActivity.this.attachItem.setVisibility(8);
                }
                if (ChatActivity.this.archiveItem != null) {
                    ChatActivity.this.archiveItem.setVisibility(ChatActivity.attach_photo);
                }
                if (ChatActivity.this.archiveSettingItem != null) {
                    ChatActivity.this.archiveSettingItem.setVisibility(ChatActivity.attach_photo);
                }
                if (ChatActivity.this.mediaItem != null) {
                    ChatActivity.this.mediaItem.setVisibility(ChatActivity.attach_photo);
                }
            }
            ChatActivity.this.searchItem.setVisibility(8);
            ChatActivity.this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            ChatActivity.this.updateVisibleRows();
            ChatActivity.this.scrollToLastMessage(false);
            ChatActivity.this.updateBottomOverlay();
        }

        public void onSearchExpand() {
            if (ChatActivity.this.openSearchKeyboard) {
                AndroidUtilities.runOnUIThread(new C12791(), 300);
            }
        }

        public void onSearchPressed(EditText editText) {
            ChatActivity.this.updateSearchButtons(ChatActivity.attach_photo, ChatActivity.attach_photo, ChatActivity.attach_photo);
            MessagesSearchQuery.searchMessagesInChat(editText.getText().toString(), ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, ChatActivity.attach_photo);
        }
    }

    public class ChatActivityAdapter extends Adapter {
        private int botInfoRow;
        private boolean isBot;
        private int loadingDownRow;
        private int loadingUpRow;
        private Context mContext;
        private int messagesEndRow;
        private int messagesStartRow;
        private int rowCount;

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.ChatActivityAdapter.1 */
        class C12831 implements ChatMessageCellDelegate {

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.ChatActivityAdapter.1.1 */
            class C12811 implements DialogInterface.OnClickListener {
                final /* synthetic */ String val$urlFinal;

                C12811(String str) {
                    this.val$urlFinal = str;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    boolean z = true;
                    if (i == 0) {
                        Context parentActivity = ChatActivity.this.getParentActivity();
                        String str = this.val$urlFinal;
                        if (ChatActivity.this.inlineReturn != 0) {
                            z = false;
                        }
                        Browser.openUrl(parentActivity, str, z);
                    } else if (i == ChatActivity.attach_gallery) {
                        CharSequence charSequence = this.val$urlFinal;
                        if (charSequence.startsWith("mailto:")) {
                            charSequence = charSequence.substring(7);
                        } else if (charSequence.startsWith("tel:")) {
                            charSequence = charSequence.substring(ChatActivity.attach_document);
                        }
                        AndroidUtilities.addToClipboard(charSequence);
                    }
                }
            }

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.ChatActivityAdapter.1.2 */
            class C12822 implements DialogInterface.OnClickListener {
                final /* synthetic */ ChatMessageCell val$cell;

                C12822(ChatMessageCell chatMessageCell) {
                    this.val$cell = chatMessageCell;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        C12831.this.didPressedFavorite(this.val$cell);
                    } else {
                        C12831.this.didPressedShare(this.val$cell);
                    }
                }
            }

            C12831() {
            }

            public boolean canPerformActions() {
                return (ChatActivity.this.actionBar == null || ChatActivity.this.actionBar.isActionModeShowed()) ? false : true;
            }

            public void didLongPressed(ChatMessageCell chatMessageCell) {
                ChatActivity.this.createMenu(chatMessageCell, false);
            }

            public void didLongPressedUserAvatar(ChatMessageCell chatMessageCell, User user) {
                ChatActivity.this.createUserAvatarMenu(chatMessageCell);
            }

            public void didPressedBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                if (ChatActivity.this.getParentActivity() == null) {
                    return;
                }
                if (ChatActivity.this.bottomOverlayChat.getVisibility() != 0 || (keyboardButton instanceof TL_keyboardButtonSwitchInline) || (keyboardButton instanceof TL_keyboardButtonCallback) || (keyboardButton instanceof TL_keyboardButtonGame) || (keyboardButton instanceof TL_keyboardButtonUrl)) {
                    ChatActivity.this.chatActivityEnterView.didPressedBotButton(keyboardButton, chatMessageCell.getMessageObject(), chatMessageCell.getMessageObject());
                }
            }

            public void didPressedCancelSendButton(ChatMessageCell chatMessageCell) {
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (messageObject.messageOwner.send_state != 0) {
                    SendMessagesHelper.getInstance().cancelSendingMessage(messageObject);
                }
            }

            public void didPressedChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i) {
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    ChatActivity.this.processRowSelect(chatMessageCell);
                } else if (chat != null && chat != ChatActivity.this.currentChat) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", chat.id);
                    if (i != 0) {
                        bundle.putInt("message_id", i);
                    }
                    if (MessagesController.checkCanOpenChat(bundle, ChatActivity.this)) {
                        ChatActivity.this.presentFragment(new ChatActivity(bundle), true);
                    }
                }
            }

            public void didPressedFavorite(ChatMessageCell chatMessageCell) {
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (messageObject != null) {
                    if (ArchiveUtil.m264a(messageObject)) {
                        ArchiveUtil.m262a(Long.valueOf(messageObject.getDialogId()), messageObject.getId());
                        ChatActivity.this.updateVisibleRows();
                    } else if (MoboConstants.aI) {
                        ChatActivity.this.showArchiveListForSelectDialog(chatMessageCell.getMessageObject());
                    } else {
                        ChatActivity.this.addMessageToArchive(Long.valueOf(-1), messageObject);
                    }
                }
            }

            public void didPressedImage(ChatMessageCell chatMessageCell) {
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (messageObject.isSendError()) {
                    ChatActivity.this.createMenu(chatMessageCell, false);
                } else if (!messageObject.isSending()) {
                    if (messageObject.type == ChatActivity.chat_enc_timer) {
                        ChatActivity.this.showDialog(new StickersAlert(ChatActivity.this.getParentActivity(), ChatActivity.this, messageObject.getInputStickerSet(), null, ChatActivity.this.bottomOverlayChat.getVisibility() != 0 ? ChatActivity.this.chatActivityEnterView : null));
                    } else if ((VERSION.SDK_INT >= ChatActivity.delete_chat && messageObject.isVideo() && MoboConstants.aD) || messageObject.type == ChatActivity.attach_gallery || ((messageObject.type == 0 && !messageObject.isWebpageDocument()) || messageObject.isGif())) {
                        PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                        if (PhotoViewer.getInstance().openPhoto(messageObject, messageObject.type != 0 ? ChatActivity.this.dialog_id : 0, messageObject.type != 0 ? ChatActivity.this.mergeDialogId : 0, ChatActivity.this)) {
                            PhotoViewer.getInstance().setParentChatActivity(ChatActivity.this);
                        }
                    } else if (messageObject.type == ChatActivity.attach_audio) {
                        ChatActivity.this.sendSecretMessageRead(messageObject);
                        try {
                            File file = (messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0) ? null : new File(messageObject.messageOwner.attachPath);
                            if (file == null || !file.exists()) {
                                file = FileLoader.getPathToMessage(messageObject.messageOwner);
                            }
                            Intent intent = new Intent("android.intent.action.VIEW");
                            if (VERSION.SDK_INT >= 24) {
                                intent.setFlags(ChatActivity.attach_gallery);
                                intent.setDataAndType(FileProvider.getUriForFile(ChatActivity.this.getParentActivity(), "com.hanista.mobogram.provider", file), MimeTypes.VIDEO_MP4);
                            } else {
                                intent.setDataAndType(Uri.fromFile(file), MimeTypes.VIDEO_MP4);
                            }
                            ChatActivity.this.getParentActivity().startActivityForResult(intent, 500);
                        } catch (Exception e) {
                            ChatActivity.this.alertUserOpenError(messageObject);
                        }
                    } else if (messageObject.type == ChatActivity.attach_document) {
                        if (AndroidUtilities.isGoogleMapsInstalled(ChatActivity.this)) {
                            BaseFragment locationActivity = new LocationActivity();
                            locationActivity.setMessageObject(messageObject);
                            ChatActivity.this.presentFragment(locationActivity);
                        }
                    } else if (messageObject.type == 9 || messageObject.type == 0) {
                        try {
                            AndroidUtilities.openForView(messageObject, ChatActivity.this.getParentActivity());
                        } catch (Exception e2) {
                            ChatActivity.this.alertUserOpenError(messageObject);
                        }
                    }
                }
            }

            public void didPressedMenu(ChatMessageCell chatMessageCell) {
                Builder builder = new Builder(ChatActivity.this.getParentActivity());
                ArrayList arrayList = new ArrayList();
                arrayList.add(ArchiveUtil.m264a(chatMessageCell.getMessageObject()) ? LocaleController.getString("RemoveFromFavorites", C0338R.string.RemoveFromFavorites) : LocaleController.getString("AddToFavorites", C0338R.string.AddToFavorites));
                if (chatMessageCell.isChangeShareWithReplyButton()) {
                    arrayList.add(LocaleController.getString("Reply", C0338R.string.Reply));
                } else {
                    arrayList.add(LocaleController.getString("DirectShare", C0338R.string.DirectShare));
                }
                builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new C12822(chatMessageCell));
                ChatActivity.this.showDialog(builder.create());
            }

            public void didPressedOther(ChatMessageCell chatMessageCell) {
                ChatActivity.this.createMenu(chatMessageCell, true);
            }

            public void didPressedReplyMessage(ChatMessageCell chatMessageCell, int i) {
                MessageObject messageObject = chatMessageCell.getMessageObject();
                ChatActivity.this.scrollToMessageId(i, messageObject.getId(), true, messageObject.getDialogId() == ChatActivity.this.mergeDialogId ? ChatActivity.attach_gallery : ChatActivity.attach_photo);
            }

            public void didPressedShare(ChatMessageCell chatMessageCell) {
                if (ChatActivity.this.getParentActivity() != null) {
                    if (ChatActivity.this.chatActivityEnterView != null) {
                        ChatActivity.this.chatActivityEnterView.closeKeyboard();
                    }
                    if (chatMessageCell.isChangeShareWithReplyButton()) {
                        ChatActivity.this.showReplyPanel(true, chatMessageCell.getMessageObject(), null, null, false, true);
                        return;
                    }
                    ChatActivity chatActivity = ChatActivity.this;
                    Context access$17500 = ChatActivityAdapter.this.mContext;
                    MessageObject messageObject = chatMessageCell.getMessageObject();
                    boolean z = ChatObject.isChannel(ChatActivity.this.currentChat) && !ChatActivity.this.currentChat.megagroup && ChatActivity.this.currentChat.username != null && ChatActivity.this.currentChat.username.length() > 0;
                    chatActivity.showDialog(new ShareAlert(access$17500, messageObject, null, z, null));
                }
            }

            public void didPressedUrl(MessageObject messageObject, ClickableSpan clickableSpan, boolean z) {
                boolean z2 = true;
                if (clickableSpan != null) {
                    if (clickableSpan instanceof URLSpanUserMention) {
                        User user = MessagesController.getInstance().getUser(Utilities.parseInt(((URLSpanUserMention) clickableSpan).getURL()));
                        if (user != null) {
                            MessagesController.openChatOrProfileWith(user, null, ChatActivity.this, ChatActivity.attach_photo, false);
                        }
                    } else if (clickableSpan instanceof URLSpanNoUnderline) {
                        r3 = ((URLSpanNoUnderline) clickableSpan).getURL();
                        if (r3.startsWith("@")) {
                            MessagesController.openByUserName(r3.substring(ChatActivity.attach_gallery), ChatActivity.this, ChatActivity.attach_photo);
                        } else if (r3.startsWith("#")) {
                            if (ChatObject.isChannel(ChatActivity.this.currentChat)) {
                                ChatActivity.this.openSearchWithText(r3);
                                return;
                            }
                            BaseFragment dialogsActivity = new DialogsActivity(null);
                            dialogsActivity.setSearchString(r3);
                            ChatActivity.this.presentFragment(dialogsActivity);
                        } else if (r3.startsWith("/") && URLSpanBotCommand.enabled) {
                            ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                            boolean z3 = ChatActivity.this.currentChat != null && ChatActivity.this.currentChat.megagroup;
                            chatActivityEnterView.setCommand(messageObject, r3, z, z3);
                        }
                    } else {
                        r3 = ((URLSpan) clickableSpan).getURL();
                        if (z) {
                            BottomSheet.Builder builder = new BottomSheet.Builder(ChatActivity.this.getParentActivity());
                            builder.setTitle(r3);
                            CharSequence[] charSequenceArr = new CharSequence[ChatActivity.attach_video];
                            charSequenceArr[ChatActivity.attach_photo] = LocaleController.getString("Open", C0338R.string.Open);
                            charSequenceArr[ChatActivity.attach_gallery] = LocaleController.getString("Copy", C0338R.string.Copy);
                            builder.setItems(charSequenceArr, new C12811(r3));
                            ChatActivity.this.showDialog(builder.create());
                        } else if (!((URLSpan) clickableSpan).getURL().contains(TtmlNode.ANONYMOUS_REGION_ID)) {
                        } else {
                            if (clickableSpan instanceof URLSpanReplacement) {
                                ChatActivity.this.showOpenUrlAlert(((URLSpanReplacement) clickableSpan).getURL(), true);
                            } else if (clickableSpan instanceof URLSpan) {
                                Context parentActivity = ChatActivity.this.getParentActivity();
                                if (ChatActivity.this.inlineReturn != 0) {
                                    z2 = false;
                                }
                                Browser.openUrl(parentActivity, r3, z2);
                            } else {
                                clickableSpan.onClick(ChatActivity.this.fragmentView);
                            }
                        }
                    }
                }
            }

            public void didPressedUserAvatar(ChatMessageCell chatMessageCell, User user) {
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    ChatActivity.this.processRowSelect(chatMessageCell);
                } else if (user != null && user.id != UserConfig.getClientUserId()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", user.id);
                    BaseFragment profileActivity = new ProfileActivity(bundle);
                    boolean z = ChatActivity.this.currentUser != null && ChatActivity.this.currentUser.id == user.id;
                    profileActivity.setPlayProfileAnimation(z);
                    ChatActivity.this.presentFragment(profileActivity);
                }
            }

            public void didPressedViaBot(ChatMessageCell chatMessageCell, String str) {
                if (ChatActivity.this.bottomOverlayChat != null && ChatActivity.this.bottomOverlayChat.getVisibility() == 0) {
                    return;
                }
                if ((ChatActivity.this.bottomOverlay == null || ChatActivity.this.bottomOverlay.getVisibility() != 0) && ChatActivity.this.chatActivityEnterView != null && str != null && str.length() > 0) {
                    ChatActivity.this.chatActivityEnterView.setFieldText("@" + str + " ");
                    ChatActivity.this.chatActivityEnterView.openKeyboard();
                }
            }

            public void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                BottomSheet.Builder builder = new BottomSheet.Builder(ChatActivityAdapter.this.mContext);
                builder.setCustomView(new WebFrameLayout(ChatActivityAdapter.this.mContext, builder.create(), str2, str3, str4, str, i, i2));
                builder.setUseFullWidth(true);
                ChatActivity.this.showDialog(builder.create());
            }

            public boolean needPlayAudio(MessageObject messageObject) {
                if (!messageObject.isVoice()) {
                    return messageObject.isMusic() ? MediaController.m71a().m160a(ChatActivity.this.messages, messageObject) : false;
                } else {
                    boolean a = MediaController.m71a().m158a(messageObject);
                    MediaController.m71a().m153a(a ? ChatActivity.this.createVoiceMessagesPlaylist(messageObject, false) : null, false);
                    return a;
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.ChatActivityAdapter.2 */
        class C12842 implements ChatActionCellDelegate {
            C12842() {
            }

            public void didClickedImage(ChatActionCell chatActionCell) {
                MessageObject messageObject = chatActionCell.getMessageObject();
                PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 640);
                if (closestPhotoSizeWithSize != null) {
                    PhotoViewer.getInstance().openPhoto(closestPhotoSizeWithSize.location, ChatActivity.this);
                    return;
                }
                PhotoViewer.getInstance().openPhoto(messageObject, 0, 0, ChatActivity.this);
            }

            public void didLongPressed(ChatActionCell chatActionCell) {
                ChatActivity.this.createMenu(chatActionCell, false);
            }

            public void didPressedBotButton(MessageObject messageObject, KeyboardButton keyboardButton) {
                if (ChatActivity.this.getParentActivity() == null) {
                    return;
                }
                if (ChatActivity.this.bottomOverlayChat.getVisibility() != 0 || (keyboardButton instanceof TL_keyboardButtonSwitchInline) || (keyboardButton instanceof TL_keyboardButtonCallback) || (keyboardButton instanceof TL_keyboardButtonGame) || (keyboardButton instanceof TL_keyboardButtonUrl)) {
                    ChatActivity.this.chatActivityEnterView.didPressedBotButton(keyboardButton, messageObject, messageObject);
                }
            }

            public void didPressedReplyMessage(ChatActionCell chatActionCell, int i) {
                MessageObject messageObject = chatActionCell.getMessageObject();
                ChatActivity.this.scrollToMessageId(i, messageObject.getId(), true, messageObject.getDialogId() == ChatActivity.this.mergeDialogId ? ChatActivity.attach_gallery : ChatActivity.attach_photo);
            }

            public void needOpenUserProfile(int i) {
                boolean z = true;
                Bundle bundle;
                if (i < 0) {
                    bundle = new Bundle();
                    bundle.putInt("chat_id", -i);
                    if (MessagesController.checkCanOpenChat(bundle, ChatActivity.this)) {
                        ChatActivity.this.presentFragment(new ChatActivity(bundle), true);
                    }
                } else if (i != UserConfig.getClientUserId()) {
                    bundle = new Bundle();
                    bundle.putInt("user_id", i);
                    if (ChatActivity.this.currentEncryptedChat != null && i == ChatActivity.this.currentUser.id) {
                        bundle.putLong("dialog_id", ChatActivity.this.dialog_id);
                    }
                    BaseFragment profileActivity = new ProfileActivity(bundle);
                    if (ChatActivity.this.currentUser == null || ChatActivity.this.currentUser.id != i) {
                        z = false;
                    }
                    profileActivity.setPlayProfileAnimation(z);
                    ChatActivity.this.presentFragment(profileActivity);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.ChatActivityAdapter.3 */
        class C12853 implements BotHelpCellDelegate {
            C12853() {
            }

            public void didPressUrl(String str) {
                if (str.startsWith("@")) {
                    MessagesController.openByUserName(str.substring(ChatActivity.attach_gallery), ChatActivity.this, ChatActivity.attach_photo);
                } else if (str.startsWith("#")) {
                    BaseFragment dialogsActivity = new DialogsActivity(null);
                    dialogsActivity.setSearchString(str);
                    ChatActivity.this.presentFragment(dialogsActivity);
                } else if (str.startsWith("/")) {
                    ChatActivity.this.chatActivityEnterView.setCommand(null, str, false, false);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChatActivity.ChatActivityAdapter.4 */
        class C12864 implements OnPreDrawListener {
            final /* synthetic */ ChatMessageCell val$messageCell;

            C12864(ChatMessageCell chatMessageCell) {
                this.val$messageCell = chatMessageCell;
            }

            public boolean onPreDraw() {
                this.val$messageCell.getViewTreeObserver().removeOnPreDrawListener(this);
                int measuredHeight = ChatActivity.this.chatListView.getMeasuredHeight();
                int top = this.val$messageCell.getTop();
                this.val$messageCell.getBottom();
                top = top >= 0 ? ChatActivity.attach_photo : -top;
                int measuredHeight2 = this.val$messageCell.getMeasuredHeight();
                if (measuredHeight2 > measuredHeight) {
                    measuredHeight2 = top + measuredHeight;
                }
                this.val$messageCell.setVisiblePart(top, measuredHeight2 - top);
                return true;
            }
        }

        private class Holder extends ViewHolder {
            public Holder(View view) {
                super(view);
            }
        }

        public ChatActivityAdapter(Context context) {
            this.botInfoRow = -1;
            this.mContext = context;
            boolean z = ChatActivity.this.currentUser != null && ChatActivity.this.currentUser.bot;
            this.isBot = z;
        }

        public int getItemCount() {
            return this.rowCount;
        }

        public long getItemId(int i) {
            return -1;
        }

        public int getItemViewType(int i) {
            return (i < this.messagesStartRow || i >= this.messagesEndRow) ? i == this.botInfoRow ? ChatActivity.attach_audio : ChatActivity.attach_document : ((MessageObject) ChatActivity.this.messages.get((ChatActivity.this.messages.size() - (i - this.messagesStartRow)) - 1)).contentType;
        }

        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemChanged(int i) {
            updateRows();
            try {
                super.notifyItemChanged(i);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemInserted(int i) {
            updateRows();
            try {
                super.notifyItemInserted(i);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemMoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemMoved(i, i2);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemRangeChanged(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeChanged(i, i2);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeInserted(i, i2);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(i, i2);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void notifyItemRemoved(int i) {
            updateRows();
            try {
                super.notifyItemRemoved(i);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = true;
            if (i == this.botInfoRow) {
                ((BotHelpCell) viewHolder.itemView).setText(!ChatActivity.this.botInfo.isEmpty() ? ((BotInfo) ChatActivity.this.botInfo.get(Integer.valueOf(ChatActivity.this.currentUser.id))).description : null);
            } else if (i == this.loadingDownRow || i == this.loadingUpRow) {
                ((ChatLoadingCell) viewHolder.itemView).setProgressVisible(ChatActivity.this.loadsCount > ChatActivity.attach_gallery);
            } else if (i >= this.messagesStartRow && i < this.messagesEndRow) {
                boolean z2;
                int i2;
                MessageObject messageObject = (MessageObject) ChatActivity.this.messages.get((ChatActivity.this.messages.size() - (i - this.messagesStartRow)) - 1);
                View view = viewHolder.itemView;
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    int i3;
                    if ((ChatActivity.this.chatActivityEnterView != null ? ChatActivity.this.chatActivityEnterView.getEditingMessageObject() : null) != messageObject) {
                        if (!ChatActivity.this.selectedMessagesIds[messageObject.getDialogId() == ChatActivity.this.dialog_id ? ChatActivity.attach_photo : ChatActivity.attach_gallery].containsKey(Integer.valueOf(messageObject.getId()))) {
                            view.setBackgroundColor(ChatActivity.attach_photo);
                            i3 = ChatActivity.attach_photo;
                            z2 = true;
                            i2 = i3;
                        }
                    }
                    view.setBackgroundColor(Theme.MSG_SELECTED_BACKGROUND_COLOR);
                    i3 = true;
                    z2 = true;
                    i2 = i3;
                } else {
                    view.setBackgroundColor(ChatActivity.attach_photo);
                    z2 = ChatActivity.attach_photo;
                    i2 = ChatActivity.attach_photo;
                }
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                    chatMessageCell.isChat = ChatActivity.this.currentChat != null;
                    chatMessageCell.setDialogSettings(ChatActivity.this.dialogSettings);
                    chatMessageCell.setMessageObject(messageObject);
                    boolean z3 = !z2;
                    z2 = z2 && i2 != 0;
                    chatMessageCell.setCheckPressed(z3, z2);
                    if ((view instanceof ChatMessageCell) && (MediaController.m71a().m157a((int) ChatActivity.attach_video) || chatMessageCell.isFavoriteAndAutoDownload())) {
                        ((ChatMessageCell) view).downloadAudioIfNeed();
                    }
                    if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID || messageObject.getId() != ChatActivity.this.highlightMessageId) {
                        z = false;
                    }
                    chatMessageCell.setHighlighted(z);
                    if (ChatActivity.this.searchContainer == null || ChatActivity.this.searchContainer.getVisibility() != 0 || MessagesSearchQuery.getLastSearchQuery() == null) {
                        chatMessageCell.setHighlightedText(null);
                    } else {
                        chatMessageCell.setHighlightedText(MessagesSearchQuery.getLastSearchQuery());
                    }
                } else if (view instanceof ChatActionCell) {
                    ((ChatActionCell) view).setMessageObject(messageObject);
                } else if (view instanceof ChatUnreadCell) {
                    ((ChatUnreadCell) view).setText(LocaleController.formatPluralString("NewMessages", ChatActivity.this.unread_to_load));
                }
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            if (i == 0) {
                if (ChatActivity.this.chatMessageCellsCache.isEmpty()) {
                    view = new ChatMessageCell(this.mContext);
                } else {
                    View view2 = (View) ChatActivity.this.chatMessageCellsCache.get(ChatActivity.attach_photo);
                    ChatActivity.this.chatMessageCellsCache.remove(ChatActivity.attach_photo);
                    view = view2;
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setDelegate(new C12831());
                if (ChatActivity.this.currentEncryptedChat == null) {
                    chatMessageCell.setAllowAssistant(true);
                }
            } else if (i == ChatActivity.attach_gallery) {
                view = new ChatActionCell(this.mContext);
                ((ChatActionCell) view).setDelegate(new C12842());
            } else if (i == ChatActivity.attach_video) {
                view = new ChatUnreadCell(this.mContext);
            } else if (i == ChatActivity.attach_audio) {
                view = new BotHelpCell(this.mContext);
                ((BotHelpCell) view).setDelegate(new C12853());
            } else if (i == ChatActivity.attach_document) {
                view = new ChatLoadingCell(this.mContext);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) viewHolder.itemView;
                chatMessageCell.getViewTreeObserver().addOnPreDrawListener(new C12864(chatMessageCell));
                boolean z = ChatActivity.this.highlightMessageId != ConnectionsManager.DEFAULT_DATACENTER_ID && chatMessageCell.getMessageObject().getId() == ChatActivity.this.highlightMessageId;
                chatMessageCell.setHighlighted(z);
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            int indexOf = ChatActivity.this.messages.indexOf(messageObject);
            if (indexOf != -1) {
                notifyItemChanged(((this.messagesStartRow + ChatActivity.this.messages.size()) - indexOf) - 1);
            }
        }

        public void updateRows() {
            this.rowCount = ChatActivity.attach_photo;
            if (ChatActivity.this.currentUser == null || !ChatActivity.this.currentUser.bot) {
                this.botInfoRow = -1;
            } else {
                int i = this.rowCount;
                this.rowCount = i + ChatActivity.attach_gallery;
                this.botInfoRow = i;
            }
            if (ChatActivity.this.messages.isEmpty() && ChatActivity.this.justFromId <= 0 && ChatActivity.this.archive == null) {
                this.loadingUpRow = -1;
                this.loadingDownRow = -1;
                this.messagesStartRow = -1;
                this.messagesEndRow = -1;
                return;
            }
            if (ChatActivity.this.endReached[ChatActivity.attach_photo] && (ChatActivity.this.mergeDialogId == 0 || ChatActivity.this.endReached[ChatActivity.attach_gallery])) {
                this.loadingUpRow = -1;
            } else {
                i = this.rowCount;
                this.rowCount = i + ChatActivity.attach_gallery;
                this.loadingUpRow = i;
            }
            this.messagesStartRow = this.rowCount;
            this.rowCount += ChatActivity.this.messages.size();
            this.messagesEndRow = this.rowCount;
            if (ChatActivity.this.forwardEndReached[ChatActivity.attach_photo] && (ChatActivity.this.mergeDialogId == 0 || ChatActivity.this.forwardEndReached[ChatActivity.attach_gallery])) {
                this.loadingDownRow = -1;
                return;
            }
            i = this.rowCount;
            this.rowCount = i + ChatActivity.attach_gallery;
            this.loadingDownRow = i;
        }
    }

    static {
        forwardNoName = false;
    }

    public ChatActivity(Bundle bundle) {
        super(bundle);
        this.userBlocked = false;
        this.chatMessageCellsCache = new ArrayList();
        this.actionModeViews = new ArrayList();
        this.allowContextBotPanelSecond = true;
        this.paused = true;
        this.wasPaused = false;
        this.readWhenResume = false;
        HashMap[] hashMapArr = new HashMap[attach_video];
        hashMapArr[attach_photo] = new HashMap();
        hashMapArr[attach_gallery] = new HashMap();
        this.selectedMessagesIds = hashMapArr;
        hashMapArr = new HashMap[attach_video];
        hashMapArr[attach_photo] = new HashMap();
        hashMapArr[attach_gallery] = new HashMap();
        this.selectedMessagesCanCopyIds = hashMapArr;
        this.waitingForLoad = new ArrayList();
        hashMapArr = new HashMap[attach_video];
        hashMapArr[attach_photo] = new HashMap();
        hashMapArr[attach_gallery] = new HashMap();
        this.messagesDict = hashMapArr;
        this.messagesByDays = new HashMap();
        this.messages = new ArrayList();
        this.maxMessageId = new int[]{ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID};
        this.minMessageId = new int[]{TLRPC.MESSAGE_FLAG_MEGAGROUP, TLRPC.MESSAGE_FLAG_MEGAGROUP};
        this.maxDate = new int[]{TLRPC.MESSAGE_FLAG_MEGAGROUP, TLRPC.MESSAGE_FLAG_MEGAGROUP};
        this.minDate = new int[attach_video];
        this.endReached = new boolean[attach_video];
        this.cacheEndReached = new boolean[attach_video];
        this.forwardEndReached = new boolean[]{true, true};
        this.firstLoading = true;
        this.last_message_id = attach_photo;
        this.first = true;
        this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
        this.scrollToMessagePosition = -10000;
        this.info = null;
        this.botInfo = new HashMap();
        this.chatEnterTime = 0;
        this.chatLeaveTime = 0;
        this.startVideoEdit = null;
        this.openSecretPhotoRunnable = null;
        this.startX = 0.0f;
        this.startY = 0.0f;
        this.botContextProvider = new C12461();
        this.selectedFromToIds = new ArrayList();
        this.chatBarUtil = new ChatBarUtil();
        this.onItemLongClickListener = new C12492();
        this.onItemClickListener = new C12543();
    }

    private void addMessageToArchive() {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", UserConfig.getClientUserId());
        bundle.putBoolean("selection_mode", true);
        bundle.putLong("archive_id", -1);
        BaseFragment chatActivity = new ChatActivity(bundle);
        chatActivity.setSelectionDelegate(new MessageSelectionDelegate() {
            public void didSelectMessages(List<Integer> list) {
                for (Integer num : list) {
                    ArchiveMessageInfo a = ArchiveUtil.m257a(num.intValue());
                    if (a != null) {
                        a.m236a(ChatActivity.this.archive.m204a());
                        ArchiveUtil.m255a(a);
                    } else {
                        ArchiveUtil.m266b(ChatActivity.this.archive.m204a(), num.intValue());
                    }
                }
                ChatActivity.this.archive = new DataBaseAccess().m904o(ChatActivity.this.archive.m204a());
                ChatActivity.this.resetChat();
            }
        });
        presentFragment(chatActivity);
    }

    private void addMessageToArchive(Long l, MessageObject messageObject) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(messageObject);
        SendMessagesHelper.getInstance().sendMessage(arrayList, (long) UserConfig.getClientUserId(), Long.valueOf(ArchiveUtil.m256a(l, messageObject.getId(), this.dialog_id)));
        updateVisibleRows();
    }

    private void addMessagesToDownloadList() {
        messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = new messages_Messages();
        Iterator it = getSelectedMessages().iterator();
        while (it.hasNext()) {
            com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.add(((MessageObject) it.next()).messageOwner);
        }
        DownloadMessagesStorage.m783a().m811a(com_hanista_mobogram_tgnet_TLRPC_messages_Messages, 1, -1, attach_photo, attach_photo, false);
        Toast.makeText(getParentActivity(), LocaleController.getString("FilesAddedToDownloadList", C0338R.string.FilesAddedToDownloadList), attach_photo).show();
    }

    private void addToSelectedMessages(MessageObject messageObject) {
        int i = 8;
        int i2 = messageObject.getDialogId() == this.dialog_id ? attach_photo : attach_gallery;
        if (this.selectedMessagesIds[i2].containsKey(Integer.valueOf(messageObject.getId()))) {
            this.selectedMessagesIds[i2].remove(Integer.valueOf(messageObject.getId()));
            if (messageObject.type == 0 || messageObject.caption != null) {
                this.selectedMessagesCanCopyIds[i2].remove(Integer.valueOf(messageObject.getId()));
            }
            if (!messageObject.canDeleteMessage(this.currentChat)) {
                this.cantDeleteMessagesCount--;
            }
        } else {
            this.selectedMessagesIds[i2].put(Integer.valueOf(messageObject.getId()), messageObject);
            if (messageObject.type == 0 || messageObject.caption != null) {
                this.selectedMessagesCanCopyIds[i2].put(Integer.valueOf(messageObject.getId()), messageObject);
            }
            if (!messageObject.canDeleteMessage(this.currentChat)) {
                this.cantDeleteMessagesCount += attach_gallery;
            }
        }
        if (this.selectionMode) {
            updateSelectAllListItem();
        } else if (!this.actionBar.isActionModeShowed()) {
        } else {
            if (this.selectedMessagesIds[attach_photo].isEmpty() && this.selectedMessagesIds[attach_gallery].isEmpty()) {
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                return;
            }
            int visibility = this.actionBar.createActionMode().getItem(copy).getVisibility();
            this.actionBar.createActionMode().getItem(copy).setVisibility(this.selectedMessagesCanCopyIds[attach_photo].size() + this.selectedMessagesCanCopyIds[attach_gallery].size() != 0 ? attach_photo : 8);
            int visibility2 = this.actionBar.createActionMode().getItem(copy).getVisibility();
            this.actionBar.createActionMode().getItem(delete).setVisibility(this.cantDeleteMessagesCount == 0 ? attach_photo : 8);
            ActionBarMenuItem item = this.actionBar.createActionMode().getItem(reply);
            if (item != null) {
                boolean z = ((this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46) && !this.isBroadcast && (this.currentChat == null || (!ChatObject.isNotInChat(this.currentChat) && (!ChatObject.isChannel(this.currentChat) || this.currentChat.creator || this.currentChat.editor || this.currentChat.megagroup)))) ? true : attach_photo;
                if (z && this.selectedMessagesIds[attach_photo].size() + this.selectedMessagesIds[attach_gallery].size() == attach_gallery) {
                    i = attach_photo;
                }
                if (item.getVisibility() != i) {
                    if (this.replyButtonAnimation != null) {
                        this.replyButtonAnimation.cancel();
                    }
                    if (visibility != visibility2) {
                        if (i == 0) {
                            item.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            item.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        } else {
                            item.setAlpha(0.0f);
                            item.setScaleX(0.0f);
                        }
                        item.setVisibility(i);
                    } else {
                        this.replyButtonAnimation = new AnimatorSet();
                        item.setPivotX((float) AndroidUtilities.dp(54.0f));
                        AnimatorSet animatorSet;
                        Animator[] animatorArr;
                        float[] fArr;
                        if (i == 0) {
                            item.setVisibility(i);
                            animatorSet = this.replyButtonAnimation;
                            animatorArr = new Animator[attach_video];
                            fArr = new float[attach_gallery];
                            fArr[attach_photo] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                            animatorArr[attach_photo] = ObjectAnimator.ofFloat(item, "alpha", fArr);
                            fArr = new float[attach_gallery];
                            fArr[attach_photo] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                            animatorArr[attach_gallery] = ObjectAnimator.ofFloat(item, "scaleX", fArr);
                            animatorSet.playTogether(animatorArr);
                        } else {
                            animatorSet = this.replyButtonAnimation;
                            animatorArr = new Animator[attach_video];
                            fArr = new float[attach_gallery];
                            fArr[attach_photo] = 0.0f;
                            animatorArr[attach_photo] = ObjectAnimator.ofFloat(item, "alpha", fArr);
                            fArr = new float[attach_gallery];
                            fArr[attach_photo] = 0.0f;
                            animatorArr[attach_gallery] = ObjectAnimator.ofFloat(item, "scaleX", fArr);
                            animatorSet.playTogether(animatorArr);
                        }
                        this.replyButtonAnimation.setDuration(100);
                        this.replyButtonAnimation.addListener(new AnonymousClass58(i, item));
                        this.replyButtonAnimation.start();
                    }
                }
            }
            updateAddToDownloadListItem();
            updateSelectAllListItem();
            showActionModeHelp();
        }
    }

    private void alertUserOpenError(MessageObject messageObject) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            if (messageObject.type == attach_audio) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", C0338R.string.NoPlayerInstalled));
            } else {
                Object[] objArr = new Object[attach_gallery];
                objArr[attach_photo] = messageObject.getDocument().mime_type;
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", C0338R.string.NoHandleAppInstalled, objArr));
            }
            showDialog(builder.create());
        }
    }

    private void applyDraftMaybe(boolean z) {
        if (this.chatActivityEnterView != null) {
            DraftMessage draft = DraftQuery.getDraft(this.dialog_id);
            Message draftMessage = (draft == null || draft.reply_to_msg_id == 0) ? null : DraftQuery.getDraftMessage(this.dialog_id);
            if (this.chatActivityEnterView.getFieldText() == null) {
                if (draft != null) {
                    CharSequence charSequence;
                    this.chatActivityEnterView.setWebPage(null, !draft.no_webpage);
                    if (draft.entities.isEmpty()) {
                        charSequence = draft.message;
                    } else {
                        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(draft.message);
                        MessagesQuery.sortEntities(draft.entities);
                        int i = attach_photo;
                        int i2 = attach_photo;
                        while (i < draft.entities.size()) {
                            int i3;
                            MessageEntity messageEntity = (MessageEntity) draft.entities.get(i);
                            if ((messageEntity instanceof TL_inputMessageEntityMentionName) || (messageEntity instanceof TL_messageEntityMentionName)) {
                                i3 = messageEntity instanceof TL_inputMessageEntityMentionName ? ((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id : ((TL_messageEntityMentionName) messageEntity).user_id;
                                if ((messageEntity.offset + i2) + messageEntity.length < valueOf.length() && valueOf.charAt((messageEntity.offset + i2) + messageEntity.length) == ' ') {
                                    messageEntity.length += attach_gallery;
                                }
                                valueOf.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + i3), messageEntity.offset + i2, messageEntity.length + (messageEntity.offset + i2), 33);
                                i3 = i2;
                            } else if (messageEntity instanceof TL_messageEntityCode) {
                                valueOf.insert((messageEntity.offset + messageEntity.length) + i2, "`");
                                valueOf.insert(messageEntity.offset + i2, "`");
                                i3 = i2 + attach_video;
                            } else if (messageEntity instanceof TL_messageEntityPre) {
                                valueOf.insert((messageEntity.offset + messageEntity.length) + i2, "```");
                                valueOf.insert(messageEntity.offset + i2, "```");
                                i3 = i2 + attach_location;
                            } else {
                                i3 = i2;
                            }
                            i += attach_gallery;
                            i2 = i3;
                        }
                        charSequence = valueOf;
                    }
                    this.chatActivityEnterView.setFieldText(charSequence);
                    if (getArguments().getBoolean("hasUrl", false)) {
                        this.chatActivityEnterView.setSelection(draft.message.indexOf(copy) + attach_gallery);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (ChatActivity.this.chatActivityEnterView != null) {
                                    ChatActivity.this.chatActivityEnterView.setFieldFocused(true);
                                    ChatActivity.this.chatActivityEnterView.openKeyboard();
                                }
                            }
                        }, 700);
                    }
                }
            } else if (z && draft == null) {
                this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                showReplyPanel(false, null, null, null, false, true);
            }
            if (this.replyingMessageObject == null && draftMessage != null) {
                this.replyingMessageObject = new MessageObject(draftMessage, MessagesController.getInstance().getUsers(), false);
                showReplyPanel(true, this.replyingMessageObject, null, null, false, false);
            }
        }
    }

    private void checkActionBarMenu() {
        if ((this.currentEncryptedChat == null || (this.currentEncryptedChat instanceof TL_encryptedChat)) && ((this.currentChat == null || !ChatObject.isNotInChat(this.currentChat)) && (this.currentUser == null || !UserObject.isDeleted(this.currentUser)))) {
            if (this.menuItem != null) {
                this.menuItem.setVisibility(attach_photo);
            }
            if (this.drawingItem != null) {
                this.drawingItem.setVisibility(attach_photo);
            }
            if (this.timeItem2 != null) {
                this.timeItem2.setVisibility(attach_photo);
            }
            if (this.avatarContainer != null) {
                this.avatarContainer.showTimeItem();
            }
        } else {
            if (this.menuItem != null) {
                this.menuItem.setVisibility(8);
            }
            if (this.drawingItem != null) {
                this.drawingItem.setVisibility(8);
            }
            if (this.timeItem2 != null) {
                this.timeItem2.setVisibility(8);
            }
            if (this.avatarContainer != null) {
                this.avatarContainer.hideTimeItem();
            }
        }
        if (!(this.avatarContainer == null || this.currentEncryptedChat == null)) {
            this.avatarContainer.setTime(this.currentEncryptedChat.ttl);
        }
        checkAndUpdateAvatar();
    }

    private void checkAndUpdateAvatar() {
        if (this.currentUser != null) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
            if (user != null) {
                this.currentUser = user;
            } else {
                return;
            }
        } else if (this.currentChat != null) {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
            if (chat != null) {
                this.currentChat = chat;
            } else {
                return;
            }
        }
        if (this.avatarContainer != null) {
            this.avatarContainer.checkAndUpdateAvatar();
        }
    }

    private void checkBotCommands() {
        boolean z = false;
        URLSpanBotCommand.enabled = false;
        if (this.currentUser != null && this.currentUser.bot) {
            URLSpanBotCommand.enabled = true;
        } else if (this.info instanceof TL_chatFull) {
            int i = attach_photo;
            while (i < this.info.participants.participants.size()) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(i)).user_id));
                if (user == null || !user.bot) {
                    i += attach_gallery;
                } else {
                    URLSpanBotCommand.enabled = true;
                    return;
                }
            }
        } else if (this.info instanceof TL_channelFull) {
            if (!this.info.bot_info.isEmpty()) {
                z = true;
            }
            URLSpanBotCommand.enabled = z;
        }
    }

    private void checkContextBotPanel() {
        if (!this.allowStickersPanel || this.mentionsAdapter == null || !this.mentionsAdapter.isBotContext()) {
            return;
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (this.allowContextBotPanel || this.allowContextBotPanelSecond) {
            if (this.mentionContainer.getVisibility() == attach_document || this.mentionContainer.getTag() != null) {
                if (this.mentionListAnimation != null) {
                    this.mentionListAnimation.cancel();
                }
                this.mentionContainer.setTag(null);
                this.mentionContainer.setVisibility(attach_photo);
                this.mentionListAnimation = new AnimatorSet();
                animatorSet = this.mentionListAnimation;
                animatorArr = new Animator[attach_gallery];
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.mentionContainer, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                animatorSet.playTogether(animatorArr);
                this.mentionListAnimation.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationCancel(Animator animator) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }
                });
                this.mentionListAnimation.setDuration(200);
                this.mentionListAnimation.start();
            }
        } else if (this.mentionContainer.getVisibility() == 0 && this.mentionContainer.getTag() == null) {
            if (this.mentionListAnimation != null) {
                this.mentionListAnimation.cancel();
            }
            this.mentionContainer.setTag(Integer.valueOf(attach_gallery));
            this.mentionListAnimation = new AnimatorSet();
            animatorSet = this.mentionListAnimation;
            animatorArr = new Animator[attach_gallery];
            float[] fArr = new float[attach_gallery];
            fArr[attach_photo] = 0.0f;
            animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.mentionContainer, "alpha", fArr);
            animatorSet.playTogether(animatorArr);
            this.mentionListAnimation.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationCancel(Animator animator) {
                    if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator)) {
                        ChatActivity.this.mentionListAnimation = null;
                    }
                }

                public void onAnimationEnd(Animator animator) {
                    if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator)) {
                        ChatActivity.this.mentionContainer.setVisibility(ChatActivity.attach_document);
                        ChatActivity.this.mentionListAnimation = null;
                    }
                }
            });
            this.mentionListAnimation.setDuration(200);
            this.mentionListAnimation.start();
        }
    }

    private void checkEditTimer() {
        if (this.chatActivityEnterView != null) {
            MessageObject editingMessageObject = this.chatActivityEnterView.getEditingMessageObject();
            if (editingMessageObject == null) {
                return;
            }
            if (this.currentUser == null || !this.currentUser.self) {
                int abs = (MessagesController.getInstance().maxEditTime + 300) - Math.abs(ConnectionsManager.getInstance().getCurrentTime() - editingMessageObject.messageOwner.date);
                if (abs > 0) {
                    if (abs <= 300) {
                        if (this.actionModeSubTextView.getVisibility() != 0) {
                            this.actionModeSubTextView.setVisibility(attach_photo);
                        }
                        SimpleTextView simpleTextView = this.actionModeSubTextView;
                        Object[] objArr = new Object[attach_gallery];
                        Object[] objArr2 = new Object[attach_video];
                        objArr2[attach_photo] = Integer.valueOf(abs / 60);
                        objArr2[attach_gallery] = Integer.valueOf(abs % 60);
                        objArr[attach_photo] = String.format("%d:%02d", objArr2);
                        simpleTextView.setText(LocaleController.formatString("TimeToEdit", C0338R.string.TimeToEdit, objArr));
                    } else if (this.actionModeSubTextView.getVisibility() != 8) {
                        this.actionModeSubTextView.setVisibility(8);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ChatActivity.this.checkEditTimer();
                        }
                    }, 1000);
                    return;
                }
                this.editDoneItem.setVisibility(8);
                this.actionModeSubTextView.setText(LocaleController.formatString("TimeToEditExpired", C0338R.string.TimeToEditExpired, new Object[attach_photo]));
            } else if (this.actionModeSubTextView.getVisibility() != 8) {
                this.actionModeSubTextView.setVisibility(8);
            }
        }
    }

    private void checkListViewPaddings() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int i = ChatActivity.attach_photo;
                try {
                    int findLastVisibleItemPosition = ChatActivity.this.chatLayoutManager.findLastVisibleItemPosition();
                    if (findLastVisibleItemPosition != -1) {
                        View findViewByPosition = ChatActivity.this.chatLayoutManager.findViewByPosition(findLastVisibleItemPosition);
                        if (findViewByPosition != null) {
                            i = findViewByPosition.getTop();
                        }
                        i -= ChatActivity.this.chatListView.getPaddingTop();
                    }
                    if (ChatActivity.this.chatListView.getPaddingTop() != AndroidUtilities.dp(52.0f) && ((ChatActivity.this.pinnedMessageView != null && ChatActivity.this.pinnedMessageView.getTag() == null) || (ChatActivity.this.reportSpamView != null && ChatActivity.this.reportSpamView.getTag() == null))) {
                        ChatActivity.this.chatListView.setPadding(ChatActivity.attach_photo, AndroidUtilities.dp(52.0f), ChatActivity.attach_photo, AndroidUtilities.dp(3.0f));
                        ChatActivity.this.chatListView.setTopGlowOffset(AndroidUtilities.dp(48.0f));
                        i -= AndroidUtilities.dp(48.0f);
                    } else if (ChatActivity.this.chatListView.getPaddingTop() == AndroidUtilities.dp(4.0f) || ((ChatActivity.this.pinnedMessageView != null && ChatActivity.this.pinnedMessageView.getTag() == null) || (ChatActivity.this.reportSpamView != null && ChatActivity.this.reportSpamView.getTag() == null))) {
                        findLastVisibleItemPosition = -1;
                    } else {
                        ChatActivity.this.chatListView.setPadding(ChatActivity.attach_photo, AndroidUtilities.dp(4.0f), ChatActivity.attach_photo, AndroidUtilities.dp(3.0f));
                        ChatActivity.this.chatListView.setTopGlowOffset(ChatActivity.attach_photo);
                        i += AndroidUtilities.dp(48.0f);
                    }
                    if (findLastVisibleItemPosition != -1) {
                        ChatActivity.this.chatLayoutManager.scrollToPositionWithOffset(findLastVisibleItemPosition, i);
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        });
    }

    private void checkRaiseSensors() {
        if (ApplicationLoader.mainInterfacePaused || ((this.bottomOverlayChat != null && this.bottomOverlayChat.getVisibility() == 0) || ((this.bottomOverlay != null && this.bottomOverlay.getVisibility() == 0) || (this.searchContainer != null && this.searchContainer.getVisibility() == 0)))) {
            MediaController.m71a().m165b(false);
        } else {
            MediaController.m71a().m165b(true);
        }
    }

    private void checkScrollForLoad(boolean z) {
        if (this.chatLayoutManager != null && !this.paused) {
            int findFirstVisibleItemPosition = this.chatLayoutManager.findFirstVisibleItemPosition();
            int abs = findFirstVisibleItemPosition == -1 ? attach_photo : Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + attach_gallery;
            if (abs > 0) {
                MessagesController instance;
                long j;
                int i;
                boolean z2;
                int i2;
                int i3;
                boolean isChannel;
                int i4;
                int itemCount = this.chatAdapter.getItemCount();
                if (findFirstVisibleItemPosition <= (z ? 25 : attach_contact) && !this.loading) {
                    if (!this.endReached[attach_photo]) {
                        this.loading = true;
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        if (this.messagesByDays.size() != 0) {
                            instance = MessagesController.getInstance();
                            j = this.dialog_id;
                            i = this.maxMessageId[attach_photo];
                            z2 = (this.cacheEndReached[attach_photo] || this.gotoDateMessageSelected) ? false : true;
                            i2 = this.minDate[attach_photo];
                            i3 = this.classGuid;
                            isChannel = ChatObject.isChannel(this.currentChat);
                            i4 = this.lastLoadIndex;
                            this.lastLoadIndex = i4 + attach_gallery;
                            instance.loadMessages(j, 50, i, z2, i2, i3, attach_photo, attach_photo, isChannel, i4);
                        } else {
                            instance = MessagesController.getInstance();
                            j = this.dialog_id;
                            z2 = (this.cacheEndReached[attach_photo] || this.gotoDateMessageSelected) ? false : true;
                            i2 = this.minDate[attach_photo];
                            i3 = this.classGuid;
                            isChannel = ChatObject.isChannel(this.currentChat);
                            i4 = this.lastLoadIndex;
                            this.lastLoadIndex = i4 + attach_gallery;
                            instance.loadMessages(j, 50, attach_photo, z2, i2, i3, attach_photo, attach_photo, isChannel, i4);
                        }
                    } else if (!(this.mergeDialogId == 0 || this.endReached[attach_gallery])) {
                        this.loading = true;
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        instance = MessagesController.getInstance();
                        j = this.mergeDialogId;
                        i = this.maxMessageId[attach_gallery];
                        z2 = (this.cacheEndReached[attach_gallery] || this.gotoDateMessageSelected) ? false : true;
                        i2 = this.minDate[attach_gallery];
                        i3 = this.classGuid;
                        isChannel = ChatObject.isChannel(this.currentChat);
                        i4 = this.lastLoadIndex;
                        this.lastLoadIndex = i4 + attach_gallery;
                        instance.loadMessages(j, 50, i, z2, i2, i3, attach_photo, attach_photo, isChannel, i4);
                    }
                }
                if (!this.loadingForward && findFirstVisibleItemPosition + abs >= itemCount - 10) {
                    if (this.mergeDialogId != 0 && !this.forwardEndReached[attach_gallery]) {
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        instance = MessagesController.getInstance();
                        j = this.mergeDialogId;
                        i = this.minMessageId[attach_gallery];
                        z2 = !this.gotoDateMessageSelected;
                        i2 = this.maxDate[attach_gallery];
                        i3 = this.classGuid;
                        isChannel = ChatObject.isChannel(this.currentChat);
                        i4 = this.lastLoadIndex;
                        this.lastLoadIndex = i4 + attach_gallery;
                        instance.loadMessages(j, 50, i, z2, i2, i3, attach_gallery, attach_photo, isChannel, i4);
                        this.loadingForward = true;
                    } else if (!this.forwardEndReached[attach_photo]) {
                        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                        instance = MessagesController.getInstance();
                        j = this.dialog_id;
                        i = this.minMessageId[attach_photo];
                        z2 = !this.gotoDateMessageSelected;
                        i2 = this.maxDate[attach_photo];
                        i3 = this.classGuid;
                        isChannel = ChatObject.isChannel(this.currentChat);
                        i4 = this.lastLoadIndex;
                        this.lastLoadIndex = i4 + attach_gallery;
                        instance.loadMessages(j, 50, i, z2, i2, i3, attach_gallery, attach_photo, isChannel, i4);
                        this.loadingForward = true;
                    }
                }
            }
        }
    }

    private void clearChatData() {
        this.messages.clear();
        this.messagesByDays.clear();
        this.waitingForLoad.clear();
        this.progressView.setVisibility(this.chatAdapter.botInfoRow == -1 ? attach_photo : attach_document);
        this.chatListView.setEmptyView(null);
        for (int i = attach_photo; i < attach_video; i += attach_gallery) {
            this.messagesDict[i].clear();
            if (this.currentEncryptedChat == null) {
                this.maxMessageId[i] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                this.minMessageId[i] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            } else {
                this.maxMessageId[i] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                this.minMessageId[i] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            this.maxDate[i] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            this.minDate[i] = attach_photo;
            this.endReached[i] = false;
            this.cacheEndReached[i] = false;
            this.forwardEndReached[i] = true;
        }
        this.first = true;
        this.firstLoading = true;
        this.loading = true;
        this.loadingForward = false;
        this.waitingForReplyMessageLoad = false;
        this.startLoadFromMessageId = attach_photo;
        this.last_message_id = attach_photo;
        this.needSelectFromMessageId = false;
        this.chatAdapter.notifyDataSetChanged();
    }

    private void copyPortionOfText() {
        CharSequence messageContent = getMessageContent(this.selectedObject, attach_photo, false);
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity(), false, true);
        View editTextCaption = new EditTextCaption(getParentActivity());
        editTextCaption.setTypeface(FontUtil.m1176a().m1161d());
        editTextCaption.setImeOptions(268435456);
        editTextCaption.setInputType((editTextCaption.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS) | AccessibilityNodeInfoCompat.ACTION_SET_SELECTION);
        editTextCaption.setSingleLine(false);
        editTextCaption.setTextSize(attach_gallery, 18.0f);
        editTextCaption.setGravity(80);
        editTextCaption.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
        editTextCaption.setBackgroundDrawable(null);
        editTextCaption.setTextColor(Theme.MSG_TEXT_COLOR);
        editTextCaption.setHintTextColor(-5066062);
        editTextCaption.setText(messageContent);
        if (ThemeUtil.m2490b()) {
            editTextCaption.setTextColor(AdvanceTheme.m2286c(AdvanceTheme.bI, Theme.MSG_TEXT_COLOR));
            editTextCaption.setHintTextColor(AdvanceTheme.m2284b(AdvanceTheme.bI, Theme.MSG_TEXT_COLOR, 0.35f));
            editTextCaption.setTextSize((float) AdvanceTheme.bK);
            editTextCaption.setBackgroundColor(AdvanceTheme.bH);
        }
        View bottomSheetCell = new BottomSheetCell(getParentActivity(), attach_gallery);
        bottomSheetCell.setBackgroundResource(C0338R.drawable.list_selector);
        bottomSheetCell.setTextAndIcon(LocaleController.getString("CopyAllText", C0338R.string.CopyAllText).toUpperCase(), attach_photo);
        bottomSheetCell.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
        bottomSheetCell.setOnClickListener(new AnonymousClass101(editTextCaption));
        View bottomSheetCell2 = new BottomSheetCell(getParentActivity(), attach_gallery);
        bottomSheetCell2.setBackgroundResource(C0338R.drawable.list_selector);
        bottomSheetCell2.setTextAndIcon(LocaleController.getString("Close", C0338R.string.Close).toUpperCase(), attach_photo);
        bottomSheetCell2.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
        bottomSheetCell2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ChatActivity.this.copyDialog != null) {
                    ChatActivity.this.copyDialog.dismiss();
                }
            }
        });
        View linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(attach_gallery);
        View linearLayout2 = new LinearLayout(getParentActivity());
        linearLayout2.setOrientation(attach_photo);
        if (LocaleController.isRTL) {
            linearLayout2.addView(bottomSheetCell2, LayoutHelper.createLinear(-2, 48, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            linearLayout2.addView(bottomSheetCell, LayoutHelper.createLinear(-2, 48, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } else {
            linearLayout2.addView(bottomSheetCell, LayoutHelper.createLinear(-2, 48, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            linearLayout2.addView(bottomSheetCell2, LayoutHelper.createLinear(-2, 48, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
        linearLayout.addView(editTextCaption, LayoutHelper.createLinear(-1, -2, 3.0f));
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, 48, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        builder.setCustomView(linearLayout);
        builder.setUseFullWidth(true);
        this.copyDialog = builder.show();
    }

    private void createChatAttachView() {
        if (getParentActivity() != null && this.chatAttachAlert == null) {
            this.chatAttachAlert = new ChatAttachAlert(getParentActivity(), this);
            this.chatAttachAlert.setDelegate(new ChatAttachViewDelegate() {
                public void didPressedButton(int i) {
                    if (ChatActivity.this.getParentActivity() != null) {
                        if (i == 7) {
                            ChatActivity.this.chatAttachAlert.dismiss();
                            HashMap selectedPhotos = ChatActivity.this.chatAttachAlert.getSelectedPhotos();
                            if (!selectedPhotos.isEmpty()) {
                                ArrayList arrayList = new ArrayList();
                                ArrayList arrayList2 = new ArrayList();
                                ArrayList arrayList3 = new ArrayList();
                                for (Entry value : selectedPhotos.entrySet()) {
                                    PhotoEntry photoEntry = (PhotoEntry) value.getValue();
                                    if (photoEntry.imagePath != null) {
                                        arrayList.add(photoEntry.imagePath);
                                        arrayList2.add(photoEntry.caption != null ? photoEntry.caption.toString() : null);
                                        arrayList3.add(!photoEntry.stickers.isEmpty() ? new ArrayList(photoEntry.stickers) : null);
                                    } else if (photoEntry.path != null) {
                                        arrayList.add(photoEntry.path);
                                        arrayList2.add(photoEntry.caption != null ? photoEntry.caption.toString() : null);
                                        arrayList3.add(!photoEntry.stickers.isEmpty() ? new ArrayList(photoEntry.stickers) : null);
                                    }
                                    photoEntry.imagePath = null;
                                    photoEntry.thumbPath = null;
                                    photoEntry.caption = null;
                                    photoEntry.stickers.clear();
                                }
                                SendMessagesHelper.prepareSendingPhotos(arrayList, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, arrayList2, arrayList3);
                                ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                                DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                                return;
                            }
                            return;
                        }
                        if (ChatActivity.this.chatAttachAlert != null) {
                            ChatActivity.this.chatAttachAlert.dismissWithButtonClick(i);
                        }
                        ChatActivity.this.processSelectedAttach(i);
                    }
                }

                public void didSelectBot(User user) {
                    if (ChatActivity.this.chatActivityEnterView != null && user.username != null && user.username.length() != 0) {
                        ChatActivity.this.chatActivityEnterView.setFieldText("@" + user.username + " ");
                        ChatActivity.this.chatActivityEnterView.openKeyboard();
                    }
                }

                public View getRevealView() {
                    return ChatActivity.this.menuItem;
                }
            });
        }
    }

    private void createDeleteMessagesAlert(MessageObject messageObject) {
        boolean z;
        MessageObject messageObject2;
        int i;
        MessageObject messageObject3;
        User user;
        Builder builder = new Builder(getParentActivity());
        String str = "AreYouSureDeleteMessages";
        Object[] objArr = new Object[attach_gallery];
        objArr[attach_photo] = LocaleController.formatPluralString("messages", messageObject != null ? attach_gallery : this.selectedMessagesIds[attach_photo].size() + this.selectedMessagesIds[attach_gallery].size());
        builder.setMessage(LocaleController.formatString(str, C0338R.string.AreYouSureDeleteMessages, objArr));
        builder.setTitle(LocaleController.getString("Message", C0338R.string.Message));
        boolean z2 = false;
        if (messageObject != null) {
            messageObject.checkMediaExistance();
            z = messageObject.mediaExists;
            messageObject2 = messageObject;
        } else {
            for (int i2 = attach_gallery; i2 >= 0; i2--) {
                List arrayList = new ArrayList(this.selectedMessagesIds[i2].keySet());
                if (!arrayList.isEmpty()) {
                    messageObject2 = (MessageObject) this.selectedMessagesIds[i2].get(arrayList.get(attach_photo));
                    break;
                }
            }
            messageObject2 = null;
            i = attach_gallery;
            while (i >= 0) {
                boolean z3;
                for (Entry value : this.selectedMessagesIds[i].entrySet()) {
                    messageObject3 = (MessageObject) value.getValue();
                    messageObject3.checkMediaExistance();
                    if (messageObject3.mediaExists) {
                        z3 = true;
                        break;
                    }
                }
                z3 = z2;
                i--;
                z2 = z3;
            }
            z = z2;
        }
        Object obj = (messageObject2 == null || messageObject2.canDeleteMessageOriginal(this.currentChat)) ? attach_gallery : attach_photo;
        boolean[] zArr = new boolean[attach_audio];
        Object obj2;
        if (this.currentChat == null || !this.currentChat.megagroup) {
            obj2 = attach_photo;
            user = null;
        } else {
            User user2;
            if (messageObject != null) {
                if (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty)) {
                    user2 = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                }
                user2 = null;
            } else {
                int i3 = -1;
                for (i = attach_gallery; i >= 0; i--) {
                    int i4 = i3;
                    for (Entry value2 : this.selectedMessagesIds[i].entrySet()) {
                        messageObject3 = (MessageObject) value2.getValue();
                        if (i4 == -1) {
                            i4 = messageObject3.messageOwner.from_id;
                        }
                        if (i4 >= 0) {
                            if (i4 != messageObject3.messageOwner.from_id) {
                            }
                        }
                        i3 = -2;
                        break;
                    }
                    i3 = i4;
                    if (i3 == -2) {
                        break;
                    }
                }
                if (i3 != -1) {
                    user2 = MessagesController.getInstance().getUser(Integer.valueOf(i3));
                }
                user2 = null;
            }
            if (user2 == null || user2.id == UserConfig.getClientUserId()) {
                obj2 = attach_photo;
                user = null;
            } else {
                View frameLayout = new FrameLayout(getParentActivity());
                if (VERSION.SDK_INT >= report) {
                    frameLayout.setPadding(attach_photo, AndroidUtilities.dp(8.0f), attach_photo, attach_photo);
                }
                if (obj != null) {
                    for (int i5 = attach_photo; i5 < attach_audio; i5 += attach_gallery) {
                        View checkBoxCell = new CheckBoxCell(getParentActivity());
                        checkBoxCell.setBackgroundResource(C0338R.drawable.list_selector);
                        checkBoxCell.setTag(Integer.valueOf(i5));
                        if (i5 == 0) {
                            checkBoxCell.setText(LocaleController.getString("DeleteBanUser", C0338R.string.DeleteBanUser), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                        } else if (i5 == attach_gallery) {
                            checkBoxCell.setText(LocaleController.getString("DeleteReportSpam", C0338R.string.DeleteReportSpam), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                        } else if (i5 == attach_video) {
                            Object[] objArr2 = new Object[attach_gallery];
                            objArr2[attach_photo] = ContactsController.formatName(user2.first_name, user2.last_name);
                            checkBoxCell.setText(LocaleController.formatString("DeleteAllFrom", C0338R.string.DeleteAllFrom, objArr2), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                        }
                        checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : attach_photo, attach_photo, LocaleController.isRTL ? attach_photo : AndroidUtilities.dp(8.0f), attach_photo);
                        frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, (float) (i5 * 48), 8.0f, 0.0f));
                        checkBoxCell.setOnClickListener(new AnonymousClass75(zArr));
                    }
                    if (z) {
                        View createDeleteFileCheckBox = AndroidUtilities.createDeleteFileCheckBox(getParentActivity());
                        frameLayout.addView(createDeleteFileCheckBox, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 144.0f, 8.0f, 0.0f));
                        createDeleteFileCheckBox.setOnClickListener(new OnClickListener() {
                            public void onClick(View view) {
                                ChatActivity.this.deleteFilesOnDeleteMessage = !ChatActivity.this.deleteFilesOnDeleteMessage;
                                ((CheckBoxCell) view).setChecked(ChatActivity.this.deleteFilesOnDeleteMessage, true);
                            }
                        });
                        obj2 = attach_gallery;
                        builder.setView(frameLayout);
                        user = user2;
                    }
                }
                obj2 = attach_photo;
                builder.setView(frameLayout);
                user = user2;
            }
        }
        this.deleteFilesOnDeleteMessage = MoboConstants.aE;
        if (z && r2 == null) {
            View frameLayout2 = new FrameLayout(getParentActivity());
            if (VERSION.SDK_INT >= report) {
                frameLayout2.setPadding(attach_photo, AndroidUtilities.dp(8.0f), attach_photo, attach_photo);
            }
            View createDeleteFileCheckBox2 = AndroidUtilities.createDeleteFileCheckBox(getParentActivity());
            frameLayout2.addView(createDeleteFileCheckBox2, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
            createDeleteFileCheckBox2.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ChatActivity.this.deleteFilesOnDeleteMessage = !ChatActivity.this.deleteFilesOnDeleteMessage;
                    ((CheckBoxCell) view).setChecked(ChatActivity.this.deleteFilesOnDeleteMessage, true);
                }
            });
            builder.setView(frameLayout2);
        }
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass78(messageObject, user, zArr));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void createMenu(View view, boolean z) {
        if (!this.actionBar.isActionModeShowed()) {
            MessageObject messageObject = view instanceof ChatMessageCell ? ((ChatMessageCell) view).getMessageObject() : view instanceof ChatActionCell ? ((ChatActionCell) view).getMessageObject() : null;
            if (messageObject != null) {
                int messageType = getMessageType(messageObject);
                if (z && (messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) {
                    scrollToMessageId(messageObject.messageOwner.reply_to_msg_id, attach_photo, true, attach_photo);
                    return;
                }
                this.selectedObject = null;
                this.forwaringMessage = null;
                for (int i = attach_gallery; i >= 0; i--) {
                    this.selectedMessagesCanCopyIds[i].clear();
                    this.selectedMessagesIds[i].clear();
                }
                this.cantDeleteMessagesCount = attach_photo;
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                Object obj = attach_gallery;
                Object obj2 = (messageObject.getDialogId() != this.mergeDialogId && messageObject.getId() > 0 && ChatObject.isChannel(this.currentChat) && this.currentChat.megagroup && ((this.currentChat.creator || this.currentChat.editor) && (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty)))) ? attach_gallery : null;
                Object obj3 = (messageObject.getDialogId() == this.mergeDialogId || this.info == null || this.info.pinned_msg_id != messageObject.getId() || !(this.currentChat.creator || this.currentChat.editor)) ? null : attach_gallery;
                Object obj4 = (!messageObject.canEditMessage(this.currentChat) || this.chatActivityEnterView.hasAudioToSend() || messageObject.getDialogId() == this.mergeDialogId) ? null : attach_gallery;
                if ((this.currentEncryptedChat != null && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46) || ((messageType == attach_gallery && messageObject.getDialogId() == this.mergeDialogId) || ((this.currentEncryptedChat == null && messageObject.getId() < 0) || this.isBroadcast || (this.currentChat != null && (ChatObject.isNotInChat(this.currentChat) || !(!ChatObject.isChannel(this.currentChat) || this.currentChat.creator || this.currentChat.editor || this.currentChat.megagroup)))))) {
                    obj = null;
                }
                if (!z && messageType >= attach_video && messageType != edit_done) {
                    int i2;
                    ActionBarMenu createActionMode = this.actionBar.createActionMode();
                    View item = createActionMode.getItem(forward);
                    if (item != null) {
                        item.setVisibility(attach_photo);
                    }
                    item = createActionMode.getItem(forward_noname);
                    if (item != null) {
                        item.setVisibility(attach_photo);
                    }
                    item = createActionMode.getItem(delete);
                    if (item != null) {
                        item.setVisibility(attach_photo);
                    }
                    if (this.editDoneItem != null) {
                        this.editDoneItem.setVisibility(8);
                    }
                    this.actionBar.showActionMode();
                    updatePinnedMessageView(true);
                    AnimatorSet animatorSet = new AnimatorSet();
                    Collection arrayList = new ArrayList();
                    for (i2 = attach_photo; i2 < this.actionModeViews.size(); i2 += attach_gallery) {
                        item = (View) this.actionModeViews.get(i2);
                        AndroidUtilities.clearDrawableAnimation(item);
                        arrayList.add(ObjectAnimator.ofFloat(item, "scaleY", new float[]{0.1f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    }
                    animatorSet.playTogether(arrayList);
                    animatorSet.setDuration(250);
                    animatorSet.start();
                    addToSelectedMessages(messageObject);
                    this.selectedMessagesCountTextView.setNumber(attach_gallery, false);
                    updateVisibleRows();
                    try {
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getParentActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        i2 = attach_photo;
                        int i3 = attach_photo;
                        while (i3 < createActionMode.getChildCount()) {
                            item = createActionMode.getChildAt(i3);
                            i3 += attach_gallery;
                            i2 = item.getVisibility() == 0 ? item.getLayoutParams().width + i2 : i2;
                        }
                        LayoutParams layoutParams = (LayoutParams) createActionMode.getLayoutParams();
                        layoutParams.height = -1;
                        layoutParams.width = -1;
                        if (((float) i2) > ((float) displayMetrics.widthPixels) / displayMetrics.density) {
                            layoutParams.gravity = attach_audio;
                        } else {
                            layoutParams.gravity = attach_contact;
                        }
                        createActionMode.setLayoutParams(layoutParams);
                    } catch (Exception e) {
                    }
                } else if (messageType >= 0) {
                    this.selectedObject = messageObject;
                    if (getParentActivity() != null) {
                        Builder builder = new Builder(getParentActivity());
                        ArrayList arrayList2 = new ArrayList();
                        ArrayList arrayList3 = new ArrayList();
                        if (this.archive != null) {
                            ArchiveMessageInfo a = ArchiveUtil.m257a(this.selectedObject.getId());
                            if (!(a == null || a.m239d().longValue() == 0 || a.m238c().intValue() == 0)) {
                                arrayList2.add(LocaleController.getString("GoToOriginalMessage", C0338R.string.GoToOriginalMessage));
                                arrayList3.add(Integer.valueOf(132));
                            }
                            if (this.archive.m204a().longValue() > 0) {
                                Object[] objArr = new Object[attach_gallery];
                                objArr[attach_photo] = this.archive.m208b();
                                arrayList2.add(LocaleController.formatString("RemoveMessageFromThisCategory", C0338R.string.RemoveMessageFromThisCategory, objArr));
                                arrayList3.add(Integer.valueOf(133));
                            }
                        }
                        if (!(messageType != attach_video || this.selectedObject.messageOwner.media == null || ((!this.selectedObject.isMusic() && !(this.selectedObject.messageOwner.media instanceof TL_messageMediaDocument) && !(this.selectedObject.messageOwner.media instanceof TL_messageMediaPhoto)) || this.selectedObject.isSticker() || (this.selectedObject.isVoice() && this.selectedObject.mediaExists)))) {
                            arrayList2.add(LocaleController.getString("AddToDownloadList", C0338R.string.AddToDownloadList));
                            arrayList3.add(Integer.valueOf(131));
                        }
                        if (messageType == 0) {
                            arrayList2.add(LocaleController.getString("Retry", C0338R.string.Retry));
                            arrayList3.add(Integer.valueOf(attach_photo));
                            arrayList2.add(LocaleController.getString("Delete", C0338R.string.Delete));
                            arrayList3.add(Integer.valueOf(attach_gallery));
                        } else if (messageType == attach_gallery) {
                            if (this.currentChat == null || this.isBroadcast) {
                                if (z && this.selectedObject.getId() > 0 && obj != null) {
                                    arrayList2.add(LocaleController.getString("Reply", C0338R.string.Reply));
                                    arrayList3.add(Integer.valueOf(8));
                                }
                                if (messageObject.canDeleteMessage(this.currentChat)) {
                                    arrayList2.add(LocaleController.getString("Delete", C0338R.string.Delete));
                                    arrayList3.add(Integer.valueOf(attach_gallery));
                                }
                            } else {
                                if (obj != null) {
                                    arrayList2.add(LocaleController.getString("Reply", C0338R.string.Reply));
                                    arrayList3.add(Integer.valueOf(8));
                                }
                                if (obj3 != null) {
                                    arrayList2.add(LocaleController.getString("UnpinMessage", C0338R.string.UnpinMessage));
                                    arrayList3.add(Integer.valueOf(chat_menu_attach));
                                } else if (obj2 != null) {
                                    arrayList2.add(LocaleController.getString("PinMessage", C0338R.string.PinMessage));
                                    arrayList3.add(Integer.valueOf(chat_enc_timer));
                                }
                                if (obj4 != null) {
                                    arrayList2.add(LocaleController.getString("Edit", C0338R.string.Edit));
                                    arrayList3.add(Integer.valueOf(delete));
                                }
                                if (messageObject.canDeleteMessage(this.currentChat)) {
                                    arrayList2.add(LocaleController.getString("Delete", C0338R.string.Delete));
                                    arrayList3.add(Integer.valueOf(attach_gallery));
                                }
                            }
                        } else if (messageType == edit_done) {
                            arrayList2.add(LocaleController.getString("Retry", C0338R.string.Retry));
                            arrayList3.add(Integer.valueOf(attach_photo));
                            arrayList2.add(LocaleController.getString("Copy", C0338R.string.Copy));
                            arrayList3.add(Integer.valueOf(attach_audio));
                            arrayList2.add(LocaleController.getString("Delete", C0338R.string.Delete));
                            arrayList3.add(Integer.valueOf(attach_gallery));
                        } else if (this.currentEncryptedChat == null) {
                            if (obj != null) {
                                arrayList2.add(LocaleController.getString("Reply", C0338R.string.Reply));
                                arrayList3.add(Integer.valueOf(8));
                            }
                            if (this.selectedObject.type == 0 || this.selectedObject.caption != null) {
                                arrayList2.add(LocaleController.getString("Copy", C0338R.string.Copy));
                                arrayList3.add(Integer.valueOf(attach_audio));
                                arrayList2.add(LocaleController.getString("CopyPortionOfText", C0338R.string.CopyPortionOfText));
                                arrayList3.add(Integer.valueOf(134));
                            }
                            if (messageType == attach_audio) {
                                if ((this.selectedObject.messageOwner.media instanceof TL_messageMediaWebPage) && MessageObject.isNewGifDocument(this.selectedObject.messageOwner.media.webpage.document)) {
                                    arrayList2.add(LocaleController.getString("SaveToGIFs", C0338R.string.SaveToGIFs));
                                    arrayList3.add(Integer.valueOf(forward));
                                    arrayList2.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                    arrayList3.add(Integer.valueOf(attach_document));
                                }
                            } else if (messageType == attach_document) {
                                if (this.selectedObject.isVideo()) {
                                    arrayList2.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                    arrayList3.add(Integer.valueOf(attach_document));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(attach_location));
                                } else if (this.selectedObject.isMusic()) {
                                    arrayList2.add(LocaleController.getString("SaveToMusic", C0338R.string.SaveToMusic));
                                    arrayList3.add(Integer.valueOf(copy));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(attach_location));
                                } else if (this.selectedObject.getDocument() != null) {
                                    if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                                        arrayList2.add(LocaleController.getString("SaveToGIFs", C0338R.string.SaveToGIFs));
                                        arrayList3.add(Integer.valueOf(forward));
                                        arrayList2.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                        arrayList3.add(Integer.valueOf(attach_document));
                                    }
                                    arrayList2.add(LocaleController.getString("SaveToDownloads", C0338R.string.SaveToDownloads));
                                    arrayList3.add(Integer.valueOf(copy));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(attach_location));
                                } else {
                                    arrayList2.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                    arrayList3.add(Integer.valueOf(attach_document));
                                }
                            } else if (messageType == attach_contact) {
                                arrayList2.add(LocaleController.getString("ApplyLocalizationFile", C0338R.string.ApplyLocalizationFile));
                                arrayList3.add(Integer.valueOf(attach_contact));
                                arrayList2.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                arrayList3.add(Integer.valueOf(attach_location));
                            } else if (messageType == attach_location) {
                                arrayList2.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                arrayList3.add(Integer.valueOf(7));
                                arrayList2.add(LocaleController.getString("SaveToDownloads", C0338R.string.SaveToDownloads));
                                arrayList3.add(Integer.valueOf(copy));
                                arrayList2.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                arrayList3.add(Integer.valueOf(attach_location));
                            } else if (messageType == 7) {
                                if (this.selectedObject.isMask()) {
                                    arrayList2.add(LocaleController.getString("AddToMasks", C0338R.string.AddToMasks));
                                } else {
                                    arrayList2.add(LocaleController.getString("AddToStickers", C0338R.string.AddToStickers));
                                }
                                arrayList3.add(Integer.valueOf(9));
                            } else if (messageType == 8) {
                                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
                                if (!(user == null || user.id == UserConfig.getClientUserId() || ContactsController.getInstance().contactsDict.get(user.id) != null)) {
                                    arrayList2.add(LocaleController.getString("AddContactTitle", C0338R.string.AddContactTitle));
                                    arrayList3.add(Integer.valueOf(clear_history));
                                }
                                if (!(this.selectedObject.messageOwner.media.phone_number == null && this.selectedObject.messageOwner.media.phone_number.length() == 0)) {
                                    arrayList2.add(LocaleController.getString("Copy", C0338R.string.Copy));
                                    arrayList3.add(Integer.valueOf(delete_chat));
                                    arrayList2.add(LocaleController.getString("Call", C0338R.string.Call));
                                    arrayList3.add(Integer.valueOf(share_contact));
                                }
                            }
                            arrayList2.add(LocaleController.getString("Forward", C0338R.string.Forward));
                            arrayList3.add(Integer.valueOf(attach_video));
                            arrayList2.add(LocaleController.getString("ForwardNoQuote", C0338R.string.ForwardNoQuote));
                            arrayList3.add(Integer.valueOf(22));
                            if (messageType == attach_audio || !(this.selectedObject.messageOwner == null || this.selectedObject.messageOwner.media == null || (this.selectedObject.messageOwner.media instanceof TL_messageMediaContact))) {
                                arrayList2.add(LocaleController.getString("ProForward", C0338R.string.ProForward));
                                arrayList3.add(Integer.valueOf(26));
                            } else if (!(this.selectedObject.messageOwner.media == null || this.selectedObject.messageOwner.media.document == null || (!this.selectedObject.isNewGif() && !this.selectedObject.isGif()))) {
                                arrayList2.add(LocaleController.getString("ProForward", C0338R.string.ProForward));
                                arrayList3.add(Integer.valueOf(26));
                            }
                            arrayList2.add(LocaleController.getString("MultiForward", C0338R.string.MultiForward));
                            arrayList3.add(Integer.valueOf(23));
                            if (obj3 != null) {
                                arrayList2.add(LocaleController.getString("UnpinMessage", C0338R.string.UnpinMessage));
                                arrayList3.add(Integer.valueOf(chat_menu_attach));
                            } else if (obj2 != null) {
                                arrayList2.add(LocaleController.getString("PinMessage", C0338R.string.PinMessage));
                                arrayList3.add(Integer.valueOf(chat_enc_timer));
                            }
                            if (obj4 != null) {
                                arrayList2.add(LocaleController.getString("Edit", C0338R.string.Edit));
                                arrayList3.add(Integer.valueOf(delete));
                            }
                            if (messageObject.canDeleteMessage(this.currentChat)) {
                                arrayList2.add(LocaleController.getString("Delete", C0338R.string.Delete));
                                arrayList3.add(Integer.valueOf(attach_gallery));
                            }
                            if (this.archive == null) {
                                if (this.selectedObject == null || this.dialogSettings == null || this.dialogSettings.m984g() != this.selectedObject.getId()) {
                                    arrayList2.add(LocaleController.getString("SetChatMarker", C0338R.string.SetChatMarker));
                                } else {
                                    arrayList2.add(LocaleController.getString("DeleteChatMarker", C0338R.string.DeleteChatMarker));
                                }
                                arrayList3.add(Integer.valueOf(25));
                            }
                        } else {
                            if (obj != null) {
                                arrayList2.add(LocaleController.getString("Reply", C0338R.string.Reply));
                                arrayList3.add(Integer.valueOf(8));
                            }
                            if (this.selectedObject.type == 0 || this.selectedObject.caption != null) {
                                arrayList2.add(LocaleController.getString("Copy", C0338R.string.Copy));
                                arrayList3.add(Integer.valueOf(attach_audio));
                                arrayList2.add(LocaleController.getString("CopyPortionOfText", C0338R.string.CopyPortionOfText));
                                arrayList3.add(Integer.valueOf(134));
                            }
                            if (messageType == attach_document) {
                                if (this.selectedObject.isVideo()) {
                                    arrayList2.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                    arrayList3.add(Integer.valueOf(attach_document));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(attach_location));
                                } else if (this.selectedObject.isMusic()) {
                                    arrayList2.add(LocaleController.getString("SaveToMusic", C0338R.string.SaveToMusic));
                                    arrayList3.add(Integer.valueOf(copy));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(attach_location));
                                } else if (this.selectedObject.isVideo() || this.selectedObject.getDocument() == null) {
                                    arrayList2.add(LocaleController.getString("SaveToGallery", C0338R.string.SaveToGallery));
                                    arrayList3.add(Integer.valueOf(attach_document));
                                } else {
                                    arrayList2.add(LocaleController.getString("SaveToDownloads", C0338R.string.SaveToDownloads));
                                    arrayList3.add(Integer.valueOf(copy));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0338R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(attach_location));
                                }
                            } else if (messageType == attach_contact) {
                                arrayList2.add(LocaleController.getString("ApplyLocalizationFile", C0338R.string.ApplyLocalizationFile));
                                arrayList3.add(Integer.valueOf(attach_contact));
                            } else if (messageType == 7) {
                                arrayList2.add(LocaleController.getString("AddToStickers", C0338R.string.AddToStickers));
                                arrayList3.add(Integer.valueOf(9));
                            }
                            arrayList2.add(LocaleController.getString("Delete", C0338R.string.Delete));
                            arrayList3.add(Integer.valueOf(attach_gallery));
                        }
                        if (!arrayList3.isEmpty()) {
                            builder.setItems((CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]), new AnonymousClass79(arrayList3));
                            builder.setTitle(LocaleController.getString("Message", C0338R.string.Message));
                            showDialog(builder.create());
                        }
                    }
                }
            }
        }
    }

    private void createUserAvatarMenu(View view) {
        if (!this.actionBar.isActionModeShowed()) {
            MessageObject messageObject = view instanceof ChatMessageCell ? ((ChatMessageCell) view).getMessageObject() : view instanceof ChatActionCell ? ((ChatActionCell) view).getMessageObject() : null;
            if (messageObject != null) {
                Builder builder = new Builder(getParentActivity());
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                arrayList.add(LocaleController.getString("ShowThisUserMessages", C0338R.string.ShowThisUserMessages));
                arrayList2.add(Integer.valueOf(attach_photo));
                builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new AnonymousClass95(arrayList2, messageObject));
                builder.setTitle(LocaleController.getString("User", C0338R.string.User));
                showDialog(builder.create());
            }
        }
    }

    private ArrayList<MessageObject> createVoiceMessagesPlaylist(MessageObject messageObject, boolean z) {
        ArrayList<MessageObject> arrayList = new ArrayList();
        arrayList.add(messageObject);
        int id = messageObject.getId();
        if (id != 0) {
            for (int size = this.messages.size() - 1; size >= 0; size--) {
                MessageObject messageObject2 = (MessageObject) this.messages.get(size);
                if (((this.currentEncryptedChat == null && messageObject2.getId() > id) || (this.currentEncryptedChat != null && messageObject2.getId() < id)) && messageObject2.isVoice() && (!z || (messageObject2.isContentUnread() && !messageObject2.isOut()))) {
                    arrayList.add(messageObject2);
                }
            }
        }
        return arrayList;
    }

    private void doSelectAllMessages() {
        for (int i = attach_gallery; i >= 0; i--) {
            ArrayList arrayList = new ArrayList(this.selectedMessagesIds[i].keySet());
            Collections.sort(arrayList);
            if (arrayList.size() > attach_gallery) {
                int i2;
                MessageObject messageObject;
                List arrayList2 = new ArrayList();
                for (i2 = attach_photo; i2 < this.messages.size(); i2 += attach_gallery) {
                    messageObject = (MessageObject) this.messages.get(i2);
                    if (messageObject.getId() == ((Integer) arrayList.get(attach_photo)).intValue() || messageObject.getId() == ((Integer) arrayList.get(arrayList.size() - 1)).intValue()) {
                        arrayList2.add(Integer.valueOf(i2));
                        if (arrayList2.size() == attach_video) {
                            break;
                        }
                    }
                }
                if (arrayList2.size() == attach_video) {
                    for (int intValue = ((Integer) arrayList2.get(attach_photo)).intValue() + attach_gallery; intValue < ((Integer) arrayList2.get(attach_gallery)).intValue(); intValue += attach_gallery) {
                        messageObject = (MessageObject) this.messages.get(intValue);
                        if (messageObject.getId() > 0) {
                            i2 = getMessageType(messageObject);
                            if (i2 >= attach_video && i2 != edit_done) {
                                if (!this.selectedMessagesIds[messageObject.getDialogId() == this.dialog_id ? attach_photo : attach_gallery].containsKey(Integer.valueOf(messageObject.getId()))) {
                                    addToSelectedMessages(messageObject);
                                }
                            }
                        }
                    }
                    updateActionModeTitle();
                    updateVisibleRows();
                } else {
                    return;
                }
            }
        }
        ActionBarMenuItem item = this.actionBar.createActionMode().getItem(select_all);
        if (item != null) {
            item.setVisibility(8);
        }
    }

    private void editAndShareMessage() {
        presentFragment(new ProForwardActivity(this.selectedObject));
    }

    private void fixLayout() {
        if (this.avatarContainer != null) {
            this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ChatActivity.this.avatarContainer != null) {
                        ChatActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return ChatActivity.this.fixLayoutInternal();
                }
            });
        }
    }

    private boolean fixLayoutInternal() {
        boolean z = true;
        if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != attach_video) {
            this.selectedMessagesCountTextView.setTextSize(mute);
        } else {
            this.selectedMessagesCountTextView.setTextSize(delete_chat);
        }
        if (!AndroidUtilities.isTablet()) {
            return true;
        }
        int i = AdvanceTheme.bi;
        if (AndroidUtilities.isSmallTablet() && ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == attach_gallery) {
            this.actionBar.setBackButtonDrawable(new BackDrawable(false));
            if (ThemeUtil.m2490b()) {
                Drawable backDrawable = new BackDrawable(false);
                ((BackDrawable) backDrawable).setColor(i);
                this.actionBar.setBackButtonDrawable(backDrawable);
            }
            if (this.playerView != null && this.playerView.getParent() == null) {
                ((ViewGroup) this.fragmentView).addView(this.playerView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            }
            if (this.powerView == null || this.powerView.getParent() != null) {
                return false;
            }
            ((ViewGroup) this.fragmentView).addView(this.powerView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            return false;
        }
        ActionBar actionBar = this.actionBar;
        if (!(this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() || this.parentLayout.fragmentsStack.get(attach_photo) == this || this.parentLayout.fragmentsStack.size() == attach_gallery)) {
            z = false;
        }
        actionBar.setBackButtonDrawable(new BackDrawable(z));
        if (ThemeUtil.m2490b()) {
            Drawable backDrawable2 = new BackDrawable(false);
            ((BackDrawable) backDrawable2).setColor(i);
            this.actionBar.setBackButtonDrawable(backDrawable2);
        }
        if (!(this.playerView == null || this.playerView.getParent() == null)) {
            this.fragmentView.setPadding(attach_photo, attach_photo, attach_photo, attach_photo);
            ((ViewGroup) this.fragmentView).removeView(this.playerView);
        }
        if (this.powerView == null || this.powerView.getParent() == null) {
            return false;
        }
        this.fragmentView.setPadding(attach_photo, attach_photo, attach_photo, attach_photo);
        ((ViewGroup) this.fragmentView).removeView(this.powerView);
        return false;
    }

    private void forwardMessages(ArrayList<MessageObject> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            if (z) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    SendMessagesHelper.getInstance().processForwardFromMyName((MessageObject) it.next(), this.dialog_id);
                }
                return;
            }
            SendMessagesHelper.getInstance().sendMessage(arrayList, this.dialog_id);
        }
    }

    private String getMessageContent(MessageObject messageObject, int i, boolean z) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        if (z && i != messageObject.messageOwner.from_id) {
            if (messageObject.messageOwner.from_id > 0) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if (user != null) {
                    str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                }
            } else if (messageObject.messageOwner.from_id < 0) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-messageObject.messageOwner.from_id));
                if (chat != null) {
                    str = chat.title + ":\n";
                }
            }
        }
        return (messageObject.type != 0 || messageObject.messageOwner.message == null) ? (messageObject.messageOwner.media == null || messageObject.messageOwner.media.caption == null) ? str + messageObject.messageText : str + messageObject.messageOwner.media.caption : str + messageObject.messageOwner.message;
    }

    private int getMessageType(MessageObject messageObject) {
        int i = attach_gallery;
        if (messageObject == null) {
            return -1;
        }
        int i2;
        InputStickerSet inputStickerSet;
        String str;
        if (this.currentEncryptedChat == null) {
            i2 = (this.isBroadcast && messageObject.getId() <= 0 && messageObject.isSendError()) ? attach_gallery : attach_photo;
            if ((!this.isBroadcast && messageObject.getId() <= 0 && messageObject.isOut()) || i2 != 0) {
                return messageObject.isSendError() ? messageObject.isMediaEmpty() ? edit_done : attach_photo : -1;
            } else {
                if (messageObject.type == attach_location) {
                    return -1;
                }
                if (messageObject.type == copy || messageObject.type == forward) {
                    return messageObject.getId() == 0 ? -1 : attach_gallery;
                } else {
                    if (messageObject.isVoice()) {
                        return attach_video;
                    }
                    if (messageObject.isSticker()) {
                        inputStickerSet = messageObject.getInputStickerSet();
                        if (inputStickerSet instanceof TL_inputStickerSetID) {
                            if (!StickersQuery.isStickerPackInstalled(inputStickerSet.id)) {
                                return 7;
                            }
                        } else if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !StickersQuery.isStickerPackInstalled(inputStickerSet.short_name)) {
                            return 7;
                        }
                    } else if ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo()) {
                        i2 = (messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists()) ? attach_photo : attach_gallery;
                        if (!(i2 == 0 && FileLoader.getPathToMessage(messageObject.messageOwner).exists())) {
                            i = i2;
                        }
                        if (i != 0) {
                            if (messageObject.getDocument() != null) {
                                str = messageObject.getDocument().mime_type;
                                if (str != null) {
                                    if (str.endsWith("/xml")) {
                                        return attach_contact;
                                    }
                                    if (str.endsWith("/png") || str.endsWith("/jpg") || str.endsWith("/jpeg")) {
                                        return attach_location;
                                    }
                                }
                            }
                            return attach_document;
                        }
                    } else if (messageObject.type == delete) {
                        return 8;
                    } else {
                        if (messageObject.isMediaEmpty()) {
                            return attach_audio;
                        }
                    }
                    return attach_video;
                }
            }
        } else if (messageObject.isSending()) {
            return -1;
        } else {
            if (messageObject.type == attach_location) {
                return -1;
            }
            if (messageObject.isSendError()) {
                return messageObject.isMediaEmpty() ? edit_done : attach_photo;
            } else {
                if (messageObject.type == copy || messageObject.type == forward) {
                    return (messageObject.getId() == 0 || messageObject.isSending()) ? -1 : attach_gallery;
                } else {
                    if (messageObject.isVoice()) {
                        return attach_video;
                    }
                    if (messageObject.isSticker()) {
                        inputStickerSet = messageObject.getInputStickerSet();
                        if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !StickersQuery.isStickerPackInstalled(inputStickerSet.short_name)) {
                            return 7;
                        }
                    } else if ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo()) {
                        i2 = (messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists()) ? attach_photo : attach_gallery;
                        if (!(i2 == 0 && FileLoader.getPathToMessage(messageObject.messageOwner).exists())) {
                            i = i2;
                        }
                        if (i != 0) {
                            if (messageObject.getDocument() != null) {
                                str = messageObject.getDocument().mime_type;
                                if (str != null && str.endsWith("text/xml")) {
                                    return attach_contact;
                                }
                            }
                            if (messageObject.messageOwner.ttl <= 0) {
                                return attach_document;
                            }
                        }
                    } else if (messageObject.type == delete) {
                        return 8;
                    } else {
                        if (messageObject.isMediaEmpty()) {
                            return attach_audio;
                        }
                    }
                    return attach_video;
                }
            }
        }
    }

    private ArrayList<MessageObject> getSelectedMessages() {
        ArrayList<MessageObject> arrayList = new ArrayList();
        if (this.forwaringMessage != null) {
            arrayList.add(this.forwaringMessage);
            this.forwaringMessage = null;
        } else {
            for (int i = attach_gallery; i >= 0; i--) {
                ArrayList arrayList2 = new ArrayList(this.selectedMessagesIds[i].keySet());
                Collections.sort(arrayList2);
                for (int i2 = attach_photo; i2 < arrayList2.size(); i2 += attach_gallery) {
                    Integer num = (Integer) arrayList2.get(i2);
                    MessageObject messageObject = (MessageObject) this.selectedMessagesIds[i].get(num);
                    if (messageObject != null && num.intValue() > 0) {
                        arrayList.add(messageObject);
                    }
                }
                this.selectedMessagesCanCopyIds[i].clear();
                this.selectedMessagesIds[i].clear();
            }
            this.cantDeleteMessagesCount = attach_photo;
            this.actionBar.hideActionMode();
            updateVisibleRows();
        }
        return arrayList;
    }

    private void gotoChatMarker() {
        if (this.dialogSettings == null || this.dialogSettings.m984g() == 0) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setMessage(LocaleController.getString("GoToChatMarkerEmpty", C0338R.string.GoToChatMarkerEmpty));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            showDialog(builder.create());
            return;
        }
        scrollToMessageId(this.dialogSettings.m984g(), attach_photo, true, attach_photo);
    }

    private void gotoOriginalMessage(ArchiveMessageInfo archiveMessageInfo) {
        long longValue = archiveMessageInfo.m239d().longValue();
        int intValue = archiveMessageInfo.m238c().intValue();
        if (longValue != 0) {
            int i = (int) longValue;
            int i2 = (int) (longValue >> 32);
            Bundle bundle = new Bundle();
            if (i == 0) {
                bundle.putInt("enc_id", i2);
            } else if (i2 == attach_gallery) {
                bundle.putInt("chat_id", i);
            } else if (i > 0) {
                bundle.putInt("user_id", i);
            } else if (i < 0) {
                if (intValue != 0) {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-i));
                    if (!(chat == null || chat.migrated_to == null)) {
                        bundle.putInt("migrated_to", i);
                        i = -chat.migrated_to.channel_id;
                    }
                }
                bundle.putInt("chat_id", -i);
            }
            bundle.putInt("message_id", intValue);
            presentFragment(new ChatActivity(bundle));
        }
    }

    private void hideDateTv() {
        if (MoboConstants.ab && getParentActivity() != null) {
            Animation loadAnimation = AnimationUtils.loadAnimation(getParentActivity(), C0338R.anim.icon_anim_fade_out);
            loadAnimation.setStartOffset(1000);
            loadAnimation.setDuration(1000);
            loadAnimation.setAnimationListener(new AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                    if (ChatActivity.this.dateToastTv != null) {
                        ChatActivity.this.dateToastTv.setVisibility(8);
                    }
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }
            });
            this.dateToastTv.startAnimation(loadAnimation);
        }
    }

    private void hidePinnedMessageView(boolean z) {
        if (this.pinnedMessageView.getTag() == null) {
            this.pinnedMessageView.setTag(Integer.valueOf(attach_gallery));
            if (this.pinnedMessageViewAnimator != null) {
                this.pinnedMessageViewAnimator.cancel();
                this.pinnedMessageViewAnimator = null;
            }
            if (z) {
                this.pinnedMessageViewAnimator = new AnimatorSet();
                AnimatorSet animatorSet = this.pinnedMessageViewAnimator;
                Animator[] animatorArr = new Animator[attach_gallery];
                float[] fArr = new float[attach_gallery];
                fArr[attach_photo] = (float) (-AndroidUtilities.dp(50.0f));
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.pinnedMessageView, "translationY", fArr);
                animatorSet.playTogether(animatorArr);
                this.pinnedMessageViewAnimator.setDuration(200);
                this.pinnedMessageViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationCancel(Animator animator) {
                        if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animator)) {
                            ChatActivity.this.pinnedMessageViewAnimator = null;
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animator)) {
                            ChatActivity.this.pinnedMessageView.setVisibility(8);
                            ChatActivity.this.pinnedMessageViewAnimator = null;
                        }
                    }
                });
                this.pinnedMessageViewAnimator.start();
                return;
            }
            this.pinnedMessageView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
            this.pinnedMessageView.setVisibility(8);
        }
    }

    private void initSelectionMode() {
        if (!this.selectionMode) {
            return;
        }
        if (getParentActivity() == null) {
            this.selectedObject = null;
            return;
        }
        if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
            this.actionBar.closeSearchField();
        }
        this.chatActivityEnterView.setVisibility(8);
        this.mentionsAdapter.setNeedBotContext(false);
        this.chatListView.setOnItemLongClickListener(null);
        this.chatListView.setLongClickable(false);
        this.actionModeTextView.setVisibility(8);
        this.actionModeTitleContainer.setVisibility(attach_photo);
        this.selectedMessagesCountTextView.setVisibility(attach_photo);
        this.chatActivityEnterView.setAllowStickersAndGifs(false, false);
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        createActionMode.getItem(reply).setVisibility(8);
        createActionMode.getItem(copy).setVisibility(8);
        createActionMode.getItem(forward).setVisibility(8);
        createActionMode.getItem(forward_noname).setVisibility(8);
        createActionMode.getItem(add_to_download_list).setVisibility(8);
        createActionMode.getItem(select_all).setVisibility(attach_photo);
        createActionMode.getItem(delete).setVisibility(8);
        if (this.chatBarUtil != null) {
            this.chatBarUtil.m377a();
        }
        if (this.editDoneItemAnimation != null) {
            this.editDoneItemAnimation.cancel();
            this.editDoneItemAnimation = null;
        }
        this.editDoneItem.setVisibility(attach_photo);
        this.editDoneItem.getImageView().setVisibility(attach_photo);
        this.actionBar.showActionMode();
        updatePinnedMessageView(true);
        updateVisibleRows();
    }

    private void initStickers() {
        if (this.chatActivityEnterView != null && getParentActivity() != null && this.stickersAdapter == null) {
            if (this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 23) {
                if (this.stickersAdapter != null) {
                    this.stickersAdapter.onDestroy();
                }
                this.stickersListView.setPadding(AndroidUtilities.dp(18.0f), attach_photo, AndroidUtilities.dp(18.0f), attach_photo);
                RecyclerListView recyclerListView = this.stickersListView;
                Adapter stickersAdapter = new StickersAdapter(getParentActivity(), new StickersAdapterDelegate() {

                    /* renamed from: com.hanista.mobogram.ui.ChatActivity.46.1 */
                    class C12581 extends AnimatorListenerAdapterProxy {
                        final /* synthetic */ boolean val$show;

                        C12581(boolean z) {
                            this.val$show = z;
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (ChatActivity.this.runningAnimation != null && ChatActivity.this.runningAnimation.equals(animator)) {
                                ChatActivity.this.runningAnimation = null;
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
                            if (ChatActivity.this.runningAnimation != null && ChatActivity.this.runningAnimation.equals(animator)) {
                                if (!this.val$show) {
                                    ChatActivity.this.stickersAdapter.clearStickers();
                                    ChatActivity.this.stickersPanel.setVisibility(8);
                                    if (StickerPreviewViewer.getInstance().isVisible()) {
                                        StickerPreviewViewer.getInstance().close();
                                    }
                                    StickerPreviewViewer.getInstance().reset();
                                }
                                ChatActivity.this.runningAnimation = null;
                            }
                        }
                    }

                    public void needChangePanelVisibility(boolean z) {
                        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                        if (!z || ChatActivity.this.stickersPanel.getVisibility() != 0) {
                            if (z || ChatActivity.this.stickersPanel.getVisibility() != 8) {
                                if (z) {
                                    ChatActivity.this.stickersListView.scrollToPosition(ChatActivity.attach_photo);
                                    ChatActivity.this.stickersPanel.setVisibility(ChatActivity.this.allowStickersPanel ? ChatActivity.attach_photo : ChatActivity.attach_document);
                                }
                                if (ChatActivity.this.runningAnimation != null) {
                                    ChatActivity.this.runningAnimation.cancel();
                                    ChatActivity.this.runningAnimation = null;
                                }
                                if (ChatActivity.this.stickersPanel.getVisibility() != ChatActivity.attach_document) {
                                    ChatActivity.this.runningAnimation = new AnimatorSet();
                                    AnimatorSet access$13400 = ChatActivity.this.runningAnimation;
                                    Animator[] animatorArr = new Animator[ChatActivity.attach_gallery];
                                    FrameLayout access$11900 = ChatActivity.this.stickersPanel;
                                    String str = "alpha";
                                    float[] fArr = new float[ChatActivity.attach_video];
                                    fArr[ChatActivity.attach_photo] = z ? ChatActivity.attach_photo : 1.0f;
                                    if (!z) {
                                        f = ChatActivity.attach_photo;
                                    }
                                    fArr[ChatActivity.attach_gallery] = f;
                                    animatorArr[ChatActivity.attach_photo] = ObjectAnimator.ofFloat(access$11900, str, fArr);
                                    access$13400.playTogether(animatorArr);
                                    ChatActivity.this.runningAnimation.setDuration(150);
                                    ChatActivity.this.runningAnimation.addListener(new C12581(z));
                                    ChatActivity.this.runningAnimation.start();
                                } else if (!z) {
                                    ChatActivity.this.stickersPanel.setVisibility(8);
                                }
                            }
                        }
                    }
                });
                this.stickersAdapter = stickersAdapter;
                recyclerListView.setAdapter(stickersAdapter);
                recyclerListView = this.stickersListView;
                OnItemClickListener anonymousClass47 = new OnItemClickListener() {
                    public void onItemClick(View view, int i) {
                        Document item = ChatActivity.this.stickersAdapter.getItem(i);
                        if (MoboConstants.f1352s) {
                            ChatActivity.this.showStickerSendConfirmDialog(item);
                            return;
                        }
                        if (item instanceof TL_document) {
                            SendMessagesHelper.getInstance().sendSticker(item, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                            ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                            ChatActivity.this.chatActivityEnterView.addStickerToRecent(item);
                        }
                        ChatActivity.this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                };
                this.stickersOnItemClickListener = anonymousClass47;
                recyclerListView.setOnItemClickListener(anonymousClass47);
            }
        }
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            try {
                int i;
                int i2 = AdvanceTheme.bg;
                this.actionBar.setBackgroundColor(i2);
                this.searchContainer.setBackgroundDrawable(null);
                this.searchContainer.setBackgroundColor(i2);
                int i3 = AdvanceTheme.bR;
                if (i3 > 0) {
                    Orientation orientation;
                    switch (i3) {
                        case attach_video /*2*/:
                            orientation = Orientation.LEFT_RIGHT;
                            break;
                        case attach_audio /*3*/:
                            orientation = Orientation.TL_BR;
                            break;
                        case attach_document /*4*/:
                            orientation = Orientation.BL_TR;
                            break;
                        default:
                            orientation = Orientation.TOP_BOTTOM;
                            break;
                    }
                    i = AdvanceTheme.bS;
                    int[] iArr = new int[attach_video];
                    iArr[attach_photo] = i2;
                    iArr[attach_gallery] = i;
                    Drawable gradientDrawable = new GradientDrawable(orientation, iArr);
                    this.actionBar.setBackgroundDrawable(gradientDrawable);
                    this.searchContainer.setBackgroundDrawable(gradientDrawable);
                }
                i3 = AdvanceTheme.bh;
                this.avatarContainer.setTitleColor(i3);
                this.searchCountText.setTextColor(i3);
                this.avatarContainer.setTitleSize(AdvanceTheme.bX);
                this.avatarContainer.setSubtitleColor(AdvanceTheme.bT);
                this.avatarContainer.setSubtitleSize(AdvanceTheme.bY);
                i = AdvanceTheme.bi;
                if (getParentActivity() != null) {
                    getParentActivity().getResources().getDrawable(C0338R.drawable.mute_blue).setColorFilter(i, Mode.SRC_IN);
                    getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_other).setColorFilter(i, Mode.MULTIPLY);
                    getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_back).setColorFilter(i, Mode.MULTIPLY);
                    getParentActivity().getResources().getDrawable(C0338R.drawable.search_down).setColorFilter(i, Mode.SRC_IN);
                    getParentActivity().getResources().getDrawable(C0338R.drawable.search_up).setColorFilter(i, Mode.SRC_IN);
                }
                if (this.searchItem != null) {
                    this.searchItem.getSearchField().setTextColor(i);
                }
                this.bottomOverlayChat.setBackgroundColor(i2);
                this.bottomOverlayChatText.setTextColor(i3);
                this.reportSpamView.setBackgroundColor(i2);
                this.reportSpamButton.setTextColor(i3);
                this.addToContactsButton.setTextColor(i3);
                if (this.refreshWallpaper) {
                    ((SizeNotifierFrameLayout) this.fragmentView).setBackgroundImage(ApplicationLoader.getCachedWallpaper());
                    this.refreshWallpaper = false;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    private boolean isMessageDownloadable(MessageObject messageObject) {
        return (messageObject == null || getMessageType(messageObject) != attach_video || messageObject.messageOwner.media == null) ? false : ((messageObject.isMusic() || (messageObject.messageOwner.media instanceof TL_messageMediaDocument) || (messageObject.messageOwner.media instanceof TL_messageMediaPhoto)) && !messageObject.isSticker()) ? (messageObject.isVoice() && messageObject.mediaExists) ? false : true : false;
    }

    private void mentionListViewUpdateLayout() {
        if (this.mentionListView.getChildCount() <= 0) {
            this.mentionListViewScrollOffsetY = attach_photo;
            this.mentionListViewLastViewPosition = -1;
            return;
        }
        View childAt = this.mentionListView.getChildAt(this.mentionListView.getChildCount() - 1);
        Holder holder = (Holder) this.mentionListView.findContainingViewHolder(childAt);
        if (holder != null) {
            this.mentionListViewLastViewPosition = holder.getAdapterPosition();
            this.mentionListViewLastViewTop = childAt.getTop();
        } else {
            this.mentionListViewLastViewPosition = -1;
        }
        childAt = this.mentionListView.getChildAt(attach_photo);
        holder = (Holder) this.mentionListView.findContainingViewHolder(childAt);
        int top = (childAt.getTop() <= 0 || holder == null || holder.getAdapterPosition() != 0) ? attach_photo : childAt.getTop();
        if (this.mentionListViewScrollOffsetY != top) {
            RecyclerListView recyclerListView = this.mentionListView;
            this.mentionListViewScrollOffsetY = top;
            recyclerListView.setTopGlowOffset(top);
            this.mentionListView.invalidate();
            this.mentionContainer.invalidate();
        }
    }

    private void moveScrollToLastMessage() {
        if (this.chatListView != null && !this.messages.isEmpty()) {
            this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
        }
    }

    private void multipleForward() {
        showDialog(new ShareAlert(getParentActivity(), getSelectedMessages(), forwardNoName));
        this.actionBar.hideActionMode();
    }

    private void multipleForwardDrawing(PhotoEntry photoEntry) {
        showDialog(new ShareAlert(getParentActivity(), null, true, true, false, null, new AnonymousClass94(photoEntry)));
    }

    private void openSearchWithText(String str) {
        this.avatarContainer.setVisibility(8);
        this.headerItem.setVisibility(8);
        this.attachItem.setVisibility(8);
        if (this.archiveItem != null) {
            this.archiveItem.setVisibility(8);
        }
        if (this.archiveSettingItem != null) {
            this.archiveSettingItem.setVisibility(8);
        }
        if (this.mediaItem != null) {
            this.mediaItem.setVisibility(8);
        }
        this.searchItem.setVisibility(attach_photo);
        updateSearchButtons(attach_photo, attach_photo, attach_photo);
        updateBottomOverlay();
        this.openSearchKeyboard = str == null;
        this.searchItem.openSearch(this.openSearchKeyboard);
        if (str != null) {
            this.searchItem.getSearchField().setText(str);
            this.searchItem.getSearchField().setSelection(this.searchItem.getSearchField().length());
            MessagesSearchQuery.searchMessagesInChat(str, this.dialog_id, this.mergeDialogId, this.classGuid, attach_photo);
        }
    }

    private void processMutliSelectForDelete(View view) {
        MessageObject messageObject = null;
        if (view instanceof ChatMessageCell) {
            messageObject = ((ChatMessageCell) view).getMessageObject();
        } else if (view instanceof ChatActionCell) {
            messageObject = ((ChatActionCell) view).getMessageObject();
        }
        if (messageObject != null && messageObject.getId() != 0) {
            if (this.selectedFromToIds.isEmpty()) {
                this.selectedFromToIds.add(Integer.valueOf(messageObject.getId()));
                Toast.makeText(view.getContext(), LocaleController.getString("DeleteMultiMessageSelectedOne", C0338R.string.DeleteMultiMessageSelectedOne), attach_gallery).show();
                return;
            }
            this.deleteMutliMessageEnabled = false;
            this.selectedFromToIds.add(Integer.valueOf(messageObject.getId()));
            List arrayList = new ArrayList();
            for (int i = attach_photo; i < this.messages.size(); i += attach_gallery) {
                messageObject = (MessageObject) this.messages.get(i);
                if (messageObject.getId() == ((Integer) this.selectedFromToIds.get(attach_photo)).intValue() || messageObject.getId() == ((Integer) this.selectedFromToIds.get(attach_gallery)).intValue()) {
                    arrayList.add(Integer.valueOf(i));
                    if (arrayList.size() == attach_video) {
                        break;
                    }
                }
            }
            if (arrayList.size() == attach_video) {
                int i2;
                ArrayList arrayList2 = new ArrayList();
                MessageObject messageObject2 = (MessageObject) this.messages.get(((Integer) arrayList.get(attach_photo)).intValue());
                for (int intValue = ((Integer) arrayList.get(attach_photo)).intValue(); intValue <= ((Integer) arrayList.get(attach_gallery)).intValue(); intValue += attach_gallery) {
                    messageObject = (MessageObject) this.messages.get(intValue);
                    if (messageObject.getId() > 0) {
                        arrayList2.add(Integer.valueOf(messageObject.getId()));
                        messageObject.checkMediaExistance();
                        if (messageObject.mediaExists) {
                            i2 = attach_gallery;
                            break;
                        }
                    }
                }
                boolean z = false;
                if (!arrayList2.isEmpty()) {
                    Builder builder = new Builder(getParentActivity());
                    this.deleteFilesOnDeleteMessage = MoboConstants.aE;
                    if (i2 != 0) {
                        View frameLayout = new FrameLayout(getParentActivity());
                        if (VERSION.SDK_INT >= report) {
                            frameLayout.setPadding(attach_photo, AndroidUtilities.dp(8.0f), attach_photo, attach_photo);
                        }
                        View createDeleteFileCheckBox = AndroidUtilities.createDeleteFileCheckBox(getParentActivity());
                        frameLayout.addView(createDeleteFileCheckBox, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
                        createDeleteFileCheckBox.setOnClickListener(new OnClickListener() {
                            public void onClick(View view) {
                                ChatActivity.this.deleteFilesOnDeleteMessage = !ChatActivity.this.deleteFilesOnDeleteMessage;
                                ((CheckBoxCell) view).setChecked(ChatActivity.this.deleteFilesOnDeleteMessage, true);
                            }
                        });
                        builder.setView(frameLayout);
                    }
                    Object[] objArr = new Object[attach_gallery];
                    objArr[attach_photo] = LocaleController.formatPluralString("messages", arrayList2.size());
                    builder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", C0338R.string.AreYouSureDeleteMessages, objArr));
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass91(arrayList2, messageObject2));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    showDialog(builder.create());
                }
            }
        }
    }

    private void processRowSelect(View view) {
        MessageObject messageObject = null;
        if (view instanceof ChatMessageCell) {
            messageObject = ((ChatMessageCell) view).getMessageObject();
        } else if (view instanceof ChatActionCell) {
            messageObject = ((ChatActionCell) view).getMessageObject();
        }
        int messageType = getMessageType(messageObject);
        if (messageType >= attach_video && messageType != edit_done) {
            addToSelectedMessages(messageObject);
            updateActionModeTitle();
            updateVisibleRows();
        }
    }

    private void processSelectedAttach(int i) {
        if (i == 0 || i == attach_gallery || i == attach_document || i == attach_video) {
            String str = this.currentChat != null ? this.currentChat.participants_count > MessagesController.getInstance().groupBigSize ? (i == 0 || i == attach_gallery) ? "bigchat_upload_photo" : "bigchat_upload_document" : (i == 0 || i == attach_gallery) ? "chat_upload_photo" : "chat_upload_document" : (i == 0 || i == attach_gallery) ? "pm_upload_photo" : "pm_upload_document";
            if (!MessagesController.isFeatureEnabled(str, this)) {
                return;
            }
        }
        Intent intent;
        File generatePicturePath;
        Activity parentActivity;
        String[] strArr;
        if (i == 0) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                try {
                    intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    generatePicturePath = AndroidUtilities.generatePicturePath();
                    if (generatePicturePath != null) {
                        if (VERSION.SDK_INT >= 24) {
                            intent.putExtra("output", FileProvider.getUriForFile(getParentActivity(), "com.hanista.mobogram.provider", generatePicturePath));
                            intent.addFlags(attach_video);
                            intent.addFlags(attach_gallery);
                        } else {
                            intent.putExtra("output", Uri.fromFile(generatePicturePath));
                        }
                        this.currentPicturePath = generatePicturePath.getAbsolutePath();
                    }
                    startActivityForResult(intent, attach_photo);
                    return;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    return;
                }
            }
            parentActivity = getParentActivity();
            strArr = new String[attach_gallery];
            strArr[attach_photo] = "android.permission.CAMERA";
            parentActivity.requestPermissions(strArr, reply);
        } else if (i == attach_gallery) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                boolean z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
                BaseFragment photoAlbumPickerActivity = new PhotoAlbumPickerActivity(false, z, true, this);
                photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivityDelegate() {
                    public void didSelectPhotos(ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<ArrayList<InputDocument>> arrayList3, ArrayList<SearchImage> arrayList4) {
                        SendMessagesHelper.prepareSendingPhotos(arrayList, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, arrayList2, arrayList3);
                        SendMessagesHelper.prepareSendingPhotosSearch(arrayList4, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                        ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                    }

                    public boolean didSelectVideo(String str) {
                        if (VERSION.SDK_INT >= ChatActivity.delete_chat) {
                            return !ChatActivity.this.openVideoEditor(str, true, true);
                        } else {
                            SendMessagesHelper.prepareSendingVideo(str, 0, 0, ChatActivity.attach_photo, ChatActivity.attach_photo, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null);
                            ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                            DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                            return true;
                        }
                    }

                    public void startPhotoSelectActivity() {
                        try {
                            Intent intent = new Intent();
                            intent.setType("video/*");
                            intent.setAction("android.intent.action.GET_CONTENT");
                            intent.putExtra("android.intent.extra.sizeLimit", 1610612736);
                            Intent intent2 = new Intent("android.intent.action.PICK");
                            intent2.setType("image/*");
                            intent2 = Intent.createChooser(intent2, null);
                            Parcelable[] parcelableArr = new Intent[ChatActivity.attach_gallery];
                            parcelableArr[ChatActivity.attach_photo] = intent;
                            intent2.putExtra("android.intent.extra.INITIAL_INTENTS", parcelableArr);
                            ChatActivity.this.startActivityForResult(intent2, ChatActivity.attach_gallery);
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                });
                presentFragment(photoAlbumPickerActivity);
                return;
            }
            parentActivity = getParentActivity();
            strArr = new String[attach_gallery];
            strArr[attach_photo] = "android.permission.READ_EXTERNAL_STORAGE";
            parentActivity.requestPermissions(strArr, attach_document);
        } else if (i == attach_video) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                try {
                    intent = new Intent("android.media.action.VIDEO_CAPTURE");
                    generatePicturePath = AndroidUtilities.generateVideoPath();
                    if (generatePicturePath != null) {
                        if (VERSION.SDK_INT >= 24) {
                            intent.putExtra("output", FileProvider.getUriForFile(getParentActivity(), "com.hanista.mobogram.provider", generatePicturePath));
                            intent.addFlags(attach_video);
                            intent.addFlags(attach_gallery);
                        } else if (VERSION.SDK_INT >= mute) {
                            intent.putExtra("output", Uri.fromFile(generatePicturePath));
                        }
                        intent.putExtra("android.intent.extra.sizeLimit", 1610612736);
                        this.currentPicturePath = generatePicturePath.getAbsolutePath();
                    }
                    startActivityForResult(intent, attach_video);
                    return;
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                    return;
                }
            }
            parentActivity = getParentActivity();
            strArr = new String[attach_gallery];
            strArr[attach_photo] = "android.permission.CAMERA";
            parentActivity.requestPermissions(strArr, edit_done);
        } else if (i == attach_location) {
            if (AndroidUtilities.isGoogleMapsInstalled(this)) {
                r0 = new LocationActivity();
                r0.setDelegate(new LocationActivityDelegate() {
                    public void didSelectLocation(MessageMedia messageMedia) {
                        SendMessagesHelper.getInstance().sendMessage(messageMedia, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null, null);
                        ChatActivity.this.moveScrollToLastMessage();
                        ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                        if (ChatActivity.this.paused) {
                            ChatActivity.this.scrollToTopOnResume = true;
                        }
                    }
                });
                presentFragment(r0);
            }
        } else if (i == attach_document) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                r0 = new DocumentSelectActivity();
                r0.setDelegate(new DocumentSelectActivityDelegate() {
                    public void didSelectFiles(DocumentSelectActivity documentSelectActivity, ArrayList<String> arrayList) {
                        documentSelectActivity.finishFragment();
                        SendMessagesHelper.prepareSendingDocuments(arrayList, arrayList, null, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                        ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                    }

                    public void startDocumentSelectActivity() {
                        try {
                            Intent intent = new Intent("android.intent.action.PICK");
                            intent.setType("*/*");
                            ChatActivity.this.startActivityForResult(intent, ChatActivity.report);
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                });
                presentFragment(r0);
                return;
            }
            parentActivity = getParentActivity();
            strArr = new String[attach_gallery];
            strArr[attach_photo] = "android.permission.READ_EXTERNAL_STORAGE";
            parentActivity.requestPermissions(strArr, attach_document);
        } else if (i == attach_audio) {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                r0 = new AudioSelectActivity();
                r0.setDelegate(new AudioSelectActivityDelegate() {
                    public void didSelectAudio(ArrayList<MessageObject> arrayList) {
                        SendMessagesHelper.prepareSendingAudioDocuments(arrayList, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                        ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                        DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
                    }
                });
                presentFragment(r0);
                return;
            }
            parentActivity = getParentActivity();
            strArr = new String[attach_gallery];
            strArr[attach_photo] = "android.permission.READ_EXTERNAL_STORAGE";
            parentActivity.requestPermissions(strArr, attach_document);
        } else if (i != attach_contact) {
        } else {
            if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                try {
                    intent = new Intent("android.intent.action.PICK", Contacts.CONTENT_URI);
                    intent.setType("vnd.android.cursor.dir/phone_v2");
                    startActivityForResult(intent, bot_settings);
                    return;
                } catch (Throwable e22) {
                    FileLog.m18e("tmessages", e22);
                    return;
                }
            }
            parentActivity = getParentActivity();
            strArr = new String[attach_gallery];
            strArr[attach_photo] = "android.permission.READ_CONTACTS";
            parentActivity.requestPermissions(strArr, attach_contact);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void processSelectedOption(int r13) {
        /*
        r12 = this;
        r0 = r12.selectedObject;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        switch(r13) {
            case 0: goto L_0x000c;
            case 1: goto L_0x001d;
            case 2: goto L_0x002d;
            case 3: goto L_0x0053;
            case 4: goto L_0x005f;
            case 5: goto L_0x00f9;
            case 6: goto L_0x018a;
            case 7: goto L_0x01f3;
            case 8: goto L_0x0256;
            case 9: goto L_0x0263;
            case 10: goto L_0x0285;
            case 11: goto L_0x031c;
            case 12: goto L_0x0333;
            case 13: goto L_0x0447;
            case 14: goto L_0x0516;
            case 15: goto L_0x0562;
            case 16: goto L_0x0594;
            case 17: goto L_0x05a1;
            case 22: goto L_0x05e2;
            case 23: goto L_0x0609;
            case 24: goto L_0x0615;
            case 25: goto L_0x0621;
            case 26: goto L_0x064e;
            case 27: goto L_0x0686;
            case 131: goto L_0x0653;
            case 132: goto L_0x06be;
            case 133: goto L_0x06eb;
            case 134: goto L_0x070e;
            default: goto L_0x0008;
        };
    L_0x0008:
        r0 = 0;
        r12.selectedObject = r0;
        goto L_0x0004;
    L_0x000c:
        r0 = com.hanista.mobogram.messenger.SendMessagesHelper.getInstance();
        r1 = r12.selectedObject;
        r2 = 0;
        r0 = r0.retrySendMessage(r1, r2);
        if (r0 == 0) goto L_0x0008;
    L_0x0019:
        r12.moveScrollToLastMessage();
        goto L_0x0008;
    L_0x001d:
        r0 = r12.getParentActivity();
        if (r0 != 0) goto L_0x0027;
    L_0x0023:
        r0 = 0;
        r12.selectedObject = r0;
        goto L_0x0004;
    L_0x0027:
        r0 = r12.selectedObject;
        r12.createDeleteMessagesAlert(r0);
        goto L_0x0008;
    L_0x002d:
        r0 = 0;
        forwardNoName = r0;
        r0 = r12.selectedObject;
        r12.forwaringMessage = r0;
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "onlySelect";
        r2 = 1;
        r0.putBoolean(r1, r2);
        r1 = "dialogsType";
        r2 = 1;
        r0.putInt(r1, r2);
        r1 = new com.hanista.mobogram.ui.DialogsActivity;
        r1.<init>(r0);
        r1.setDelegate(r12);
        r12.presentFragment(r1);
        goto L_0x0008;
    L_0x0053:
        r0 = r12.selectedObject;
        r1 = 0;
        r2 = 0;
        r0 = r12.getMessageContent(r0, r1, r2);
        com.hanista.mobogram.messenger.AndroidUtilities.addToClipboard(r0);
        goto L_0x0008;
    L_0x005f:
        r0 = r12.selectedObject;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x0079;
    L_0x0067:
        r1 = r0.length();
        if (r1 <= 0) goto L_0x0079;
    L_0x006d:
        r1 = new java.io.File;
        r1.<init>(r0);
        r1 = r1.exists();
        if (r1 != 0) goto L_0x0079;
    L_0x0078:
        r0 = 0;
    L_0x0079:
        if (r0 == 0) goto L_0x0081;
    L_0x007b:
        r1 = r0.length();
        if (r1 != 0) goto L_0x008d;
    L_0x0081:
        r0 = r12.selectedObject;
        r0 = r0.messageOwner;
        r0 = com.hanista.mobogram.messenger.FileLoader.getPathToMessage(r0);
        r0 = r0.toString();
    L_0x008d:
        r1 = r12.selectedObject;
        r1 = r1.type;
        r2 = 3;
        if (r1 == r2) goto L_0x00ab;
    L_0x0094:
        r1 = r12.selectedObject;
        r1 = r1.type;
        r2 = 1;
        if (r1 == r2) goto L_0x00ab;
    L_0x009b:
        r1 = r12.selectedObject;
        r1 = r1.isGif();
        if (r1 != 0) goto L_0x00ab;
    L_0x00a3:
        r1 = r12.selectedObject;
        r1 = r1.isNewGif();
        if (r1 == 0) goto L_0x0008;
    L_0x00ab:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 23;
        if (r1 < r2) goto L_0x00d4;
    L_0x00b1:
        r1 = r12.getParentActivity();
        r2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1 = r1.checkSelfPermission(r2);
        if (r1 == 0) goto L_0x00d4;
    L_0x00be:
        r0 = r12.getParentActivity();
        r1 = 1;
        r1 = new java.lang.String[r1];
        r2 = 0;
        r3 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1[r2] = r3;
        r2 = 4;
        r0.requestPermissions(r1, r2);
        r0 = 0;
        r12.selectedObject = r0;
        goto L_0x0004;
    L_0x00d4:
        r2 = r12.getParentActivity();
        r1 = r12.selectedObject;
        r1 = r1.type;
        r3 = 3;
        if (r1 == r3) goto L_0x00ef;
    L_0x00df:
        r1 = r12.selectedObject;
        r1 = r1.isGif();
        if (r1 != 0) goto L_0x00ef;
    L_0x00e7:
        r1 = r12.selectedObject;
        r1 = r1.isNewGif();
        if (r1 == 0) goto L_0x00f7;
    L_0x00ef:
        r1 = 1;
    L_0x00f0:
        r3 = 0;
        r4 = 0;
        com.hanista.mobogram.messenger.MediaController.m84a(r0, r2, r1, r3, r4);
        goto L_0x0008;
    L_0x00f7:
        r1 = 0;
        goto L_0x00f0;
    L_0x00f9:
        r0 = 0;
        r1 = r12.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        if (r1 == 0) goto L_0x0716;
    L_0x0102:
        r1 = r12.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        r1 = r1.length();
        if (r1 == 0) goto L_0x0716;
    L_0x010e:
        r1 = new java.io.File;
        r2 = r12.selectedObject;
        r2 = r2.messageOwner;
        r2 = r2.attachPath;
        r1.<init>(r2);
        r2 = r1.exists();
        if (r2 == 0) goto L_0x0716;
    L_0x011f:
        if (r1 != 0) goto L_0x0713;
    L_0x0121:
        r0 = r12.selectedObject;
        r0 = r0.messageOwner;
        r0 = com.hanista.mobogram.messenger.FileLoader.getPathToMessage(r0);
        r2 = r0.exists();
        if (r2 == 0) goto L_0x0713;
    L_0x012f:
        if (r0 == 0) goto L_0x0008;
    L_0x0131:
        r1 = com.hanista.mobogram.messenger.LocaleController.getInstance();
        r0 = r1.applyLanguageFile(r0);
        if (r0 == 0) goto L_0x0145;
    L_0x013b:
        r0 = new com.hanista.mobogram.ui.LanguageSelectActivity;
        r0.<init>();
        r12.presentFragment(r0);
        goto L_0x0008;
    L_0x0145:
        r0 = r12.getParentActivity();
        if (r0 != 0) goto L_0x0150;
    L_0x014b:
        r0 = 0;
        r12.selectedObject = r0;
        goto L_0x0004;
    L_0x0150:
        r0 = new android.app.AlertDialog$Builder;
        r1 = r12.getParentActivity();
        r0.<init>(r1);
        r1 = "AppName";
        r2 = 2131166486; // 0x7f070516 float:1.7947219E38 double:1.0529361463E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
        r0.setTitle(r1);
        r1 = "IncorrectLocalization";
        r2 = 2131165753; // 0x7f070239 float:1.7945732E38 double:1.052935784E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
        r0.setMessage(r1);
        r1 = "OK";
        r2 = 2131166046; // 0x7f07035e float:1.7946326E38 double:1.052935929E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
        r2 = 0;
        r0.setPositiveButton(r1, r2);
        r0 = r0.create();
        r12.showDialog(r0);
        goto L_0x0008;
    L_0x018a:
        r0 = r12.selectedObject;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x01a4;
    L_0x0192:
        r1 = r0.length();
        if (r1 <= 0) goto L_0x01a4;
    L_0x0198:
        r1 = new java.io.File;
        r1.<init>(r0);
        r1 = r1.exists();
        if (r1 != 0) goto L_0x01a4;
    L_0x01a3:
        r0 = 0;
    L_0x01a4:
        if (r0 == 0) goto L_0x01ac;
    L_0x01a6:
        r1 = r0.length();
        if (r1 != 0) goto L_0x01b8;
    L_0x01ac:
        r0 = r12.selectedObject;
        r0 = r0.messageOwner;
        r0 = com.hanista.mobogram.messenger.FileLoader.getPathToMessage(r0);
        r0 = r0.toString();
    L_0x01b8:
        r1 = new android.content.Intent;
        r2 = "android.intent.action.SEND";
        r1.<init>(r2);
        r2 = r12.selectedObject;
        r2 = r2.getDocument();
        r2 = r2.mime_type;
        r1.setType(r2);
        r2 = "android.intent.extra.STREAM";
        r3 = new java.io.File;
        r3.<init>(r0);
        r0 = android.net.Uri.fromFile(r3);
        r1.putExtra(r2, r0);
        r0 = r12.getParentActivity();
        r2 = "ShareFile";
        r3 = 2131166276; // 0x7f070444 float:1.7946793E38 double:1.0529360425E-314;
        r2 = com.hanista.mobogram.messenger.LocaleController.getString(r2, r3);
        r1 = android.content.Intent.createChooser(r1, r2);
        r2 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r0.startActivityForResult(r1, r2);
        goto L_0x0008;
    L_0x01f3:
        r0 = r12.selectedObject;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x020d;
    L_0x01fb:
        r1 = r0.length();
        if (r1 <= 0) goto L_0x020d;
    L_0x0201:
        r1 = new java.io.File;
        r1.<init>(r0);
        r1 = r1.exists();
        if (r1 != 0) goto L_0x020d;
    L_0x020c:
        r0 = 0;
    L_0x020d:
        if (r0 == 0) goto L_0x0215;
    L_0x020f:
        r1 = r0.length();
        if (r1 != 0) goto L_0x0221;
    L_0x0215:
        r0 = r12.selectedObject;
        r0 = r0.messageOwner;
        r0 = com.hanista.mobogram.messenger.FileLoader.getPathToMessage(r0);
        r0 = r0.toString();
    L_0x0221:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 23;
        if (r1 < r2) goto L_0x024a;
    L_0x0227:
        r1 = r12.getParentActivity();
        r2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1 = r1.checkSelfPermission(r2);
        if (r1 == 0) goto L_0x024a;
    L_0x0234:
        r0 = r12.getParentActivity();
        r1 = 1;
        r1 = new java.lang.String[r1];
        r2 = 0;
        r3 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1[r2] = r3;
        r2 = 4;
        r0.requestPermissions(r1, r2);
        r0 = 0;
        r12.selectedObject = r0;
        goto L_0x0004;
    L_0x024a:
        r1 = r12.getParentActivity();
        r2 = 0;
        r3 = 0;
        r4 = 0;
        com.hanista.mobogram.messenger.MediaController.m84a(r0, r1, r2, r3, r4);
        goto L_0x0008;
    L_0x0256:
        r1 = 1;
        r2 = r12.selectedObject;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 1;
        r0 = r12;
        r0.showReplyPanel(r1, r2, r3, r4, r5, r6);
        goto L_0x0008;
    L_0x0263:
        r0 = new com.hanista.mobogram.ui.Components.StickersAlert;
        r1 = r12.getParentActivity();
        r2 = r12.selectedObject;
        r3 = r2.getInputStickerSet();
        r4 = 0;
        r2 = r12.bottomOverlayChat;
        r2 = r2.getVisibility();
        if (r2 == 0) goto L_0x0283;
    L_0x0278:
        r5 = r12.chatActivityEnterView;
    L_0x027a:
        r2 = r12;
        r0.<init>(r1, r2, r3, r4, r5);
        r12.showDialog(r0);
        goto L_0x0008;
    L_0x0283:
        r5 = 0;
        goto L_0x027a;
    L_0x0285:
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 23;
        if (r0 < r1) goto L_0x02ae;
    L_0x028b:
        r0 = r12.getParentActivity();
        r1 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r0 = r0.checkSelfPermission(r1);
        if (r0 == 0) goto L_0x02ae;
    L_0x0298:
        r0 = r12.getParentActivity();
        r1 = 1;
        r1 = new java.lang.String[r1];
        r2 = 0;
        r3 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1[r2] = r3;
        r2 = 4;
        r0.requestPermissions(r1, r2);
        r0 = 0;
        r12.selectedObject = r0;
        goto L_0x0004;
    L_0x02ae:
        r0 = r12.selectedObject;
        r0 = r0.getDocument();
        r0 = com.hanista.mobogram.messenger.FileLoader.getDocumentFileName(r0);
        if (r0 == 0) goto L_0x02c0;
    L_0x02ba:
        r1 = r0.length();
        if (r1 != 0) goto L_0x02c6;
    L_0x02c0:
        r0 = r12.selectedObject;
        r0 = r0.getFileName();
    L_0x02c6:
        r1 = r12.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        if (r1 == 0) goto L_0x02e0;
    L_0x02ce:
        r2 = r1.length();
        if (r2 <= 0) goto L_0x02e0;
    L_0x02d4:
        r2 = new java.io.File;
        r2.<init>(r1);
        r2 = r2.exists();
        if (r2 != 0) goto L_0x02e0;
    L_0x02df:
        r1 = 0;
    L_0x02e0:
        if (r1 == 0) goto L_0x02e8;
    L_0x02e2:
        r2 = r1.length();
        if (r2 != 0) goto L_0x02f4;
    L_0x02e8:
        r1 = r12.selectedObject;
        r1 = r1.messageOwner;
        r1 = com.hanista.mobogram.messenger.FileLoader.getPathToMessage(r1);
        r1 = r1.toString();
    L_0x02f4:
        r4 = r12.getParentActivity();
        r2 = r12.selectedObject;
        r2 = r2.isMusic();
        if (r2 == 0) goto L_0x0316;
    L_0x0300:
        r2 = 3;
    L_0x0301:
        r3 = r12.selectedObject;
        r3 = r3.getDocument();
        if (r3 == 0) goto L_0x0318;
    L_0x0309:
        r3 = r12.selectedObject;
        r3 = r3.getDocument();
        r3 = r3.mime_type;
    L_0x0311:
        com.hanista.mobogram.messenger.MediaController.m84a(r1, r4, r2, r0, r3);
        goto L_0x0008;
    L_0x0316:
        r2 = 2;
        goto L_0x0301;
    L_0x0318:
        r3 = "";
        goto L_0x0311;
    L_0x031c:
        r0 = r12.selectedObject;
        r0 = r0.getDocument();
        r1 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r1.saveGif(r0);
        r12.showGifHint();
        r1 = r12.chatActivityEnterView;
        r1.addRecentGif(r0);
        goto L_0x0008;
    L_0x0333:
        r0 = r12.getParentActivity();
        if (r0 != 0) goto L_0x033e;
    L_0x0339:
        r0 = 0;
        r12.selectedObject = r0;
        goto L_0x0004;
    L_0x033e:
        r0 = r12.searchItem;
        if (r0 == 0) goto L_0x0354;
    L_0x0342:
        r0 = r12.actionBar;
        r0 = r0.isSearchFieldVisible();
        if (r0 == 0) goto L_0x0354;
    L_0x034a:
        r0 = r12.actionBar;
        r0.closeSearchField();
        r0 = r12.chatActivityEnterView;
        r0.setFieldFocused();
    L_0x0354:
        r0 = r12.mentionsAdapter;
        r1 = 0;
        r0.setNeedBotContext(r1);
        r0 = r12.chatListView;
        r1 = 0;
        r0.setOnItemLongClickListener(r1);
        r0 = r12.chatListView;
        r1 = 0;
        r0.setOnItemClickListener(r1);
        r0 = r12.chatListView;
        r1 = 0;
        r0.setClickable(r1);
        r0 = r12.chatListView;
        r1 = 0;
        r0.setLongClickable(r1);
        r1 = r12.chatActivityEnterView;
        r2 = r12.selectedObject;
        r0 = r12.selectedObject;
        r0 = r0.isMediaEmpty();
        if (r0 != 0) goto L_0x0444;
    L_0x037e:
        r0 = 1;
    L_0x037f:
        r1.setEditingMessageObject(r2, r0);
        r0 = r12.chatActivityEnterView;
        r0 = r0.isEditingCaption();
        if (r0 == 0) goto L_0x0390;
    L_0x038a:
        r0 = r12.mentionsAdapter;
        r1 = 0;
        r0.setAllowNewMentions(r1);
    L_0x0390:
        r0 = r12.actionModeTitleContainer;
        r1 = 0;
        r0.setVisibility(r1);
        r0 = r12.selectedMessagesCountTextView;
        r1 = 8;
        r0.setVisibility(r1);
        r12.checkEditTimer();
        r0 = r12.chatActivityEnterView;
        r1 = 0;
        r2 = 0;
        r0.setAllowStickersAndGifs(r1, r2);
        r0 = r12.actionBar;
        r0 = r0.createActionMode();
        r1 = 19;
        r1 = r0.getItem(r1);
        r2 = 8;
        r1.setVisibility(r2);
        r1 = 10;
        r1 = r0.getItem(r1);
        r2 = 8;
        r1.setVisibility(r2);
        r1 = 11;
        r1 = r0.getItem(r1);
        r2 = 8;
        r1.setVisibility(r2);
        r1 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        r1 = r0.getItem(r1);
        r2 = 8;
        r1.setVisibility(r2);
        r1 = 190; // 0xbe float:2.66E-43 double:9.4E-322;
        r1 = r0.getItem(r1);
        r2 = 8;
        r1.setVisibility(r2);
        r1 = 191; // 0xbf float:2.68E-43 double:9.44E-322;
        r1 = r0.getItem(r1);
        r2 = 8;
        r1.setVisibility(r2);
        r1 = 12;
        r0 = r0.getItem(r1);
        r1 = 8;
        r0.setVisibility(r1);
        r0 = r12.editDoneItemAnimation;
        if (r0 == 0) goto L_0x0406;
    L_0x03fe:
        r0 = r12.editDoneItemAnimation;
        r0.cancel();
        r0 = 0;
        r12.editDoneItemAnimation = r0;
    L_0x0406:
        r0 = r12.editDoneItem;
        r1 = 0;
        r0.setVisibility(r1);
        r0 = 1;
        r1 = 0;
        r12.showEditDoneProgress(r0, r1);
        r0 = r12.actionBar;
        r0.showActionMode();
        r0 = 1;
        r12.updatePinnedMessageView(r0);
        r12.updateVisibleRows();
        r0 = new com.hanista.mobogram.tgnet.TLRPC$TL_messages_getMessageEditData;
        r0.<init>();
        r2 = r12.dialog_id;
        r1 = (int) r2;
        r1 = com.hanista.mobogram.messenger.MessagesController.getInputPeer(r1);
        r0.peer = r1;
        r1 = r12.selectedObject;
        r1 = r1.getId();
        r0.id = r1;
        r1 = com.hanista.mobogram.tgnet.ConnectionsManager.getInstance();
        r2 = new com.hanista.mobogram.ui.ChatActivity$81;
        r2.<init>();
        r0 = r1.sendRequest(r0, r2);
        r12.editingMessageObjectReqId = r0;
        goto L_0x0008;
    L_0x0444:
        r0 = 0;
        goto L_0x037f;
    L_0x0447:
        r0 = r12.selectedObject;
        r7 = r0.getId();
        r8 = new android.app.AlertDialog$Builder;
        r0 = r12.getParentActivity();
        r8.<init>(r0);
        r0 = "PinMessageAlert";
        r1 = 2131166125; // 0x7f0703ad float:1.7946487E38 double:1.052935968E-314;
        r0 = com.hanista.mobogram.messenger.LocaleController.getString(r0, r1);
        r8.setMessage(r0);
        r0 = 1;
        r9 = new boolean[r0];
        r0 = 0;
        r1 = 1;
        r9[r0] = r1;
        r10 = new android.widget.FrameLayout;
        r0 = r12.getParentActivity();
        r10.<init>(r0);
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 21;
        if (r0 < r1) goto L_0x0485;
    L_0x0479:
        r0 = 0;
        r1 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r1 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r1);
        r2 = 0;
        r3 = 0;
        r10.setPadding(r0, r1, r2, r3);
    L_0x0485:
        r11 = new com.hanista.mobogram.ui.Cells.CheckBoxCell;
        r0 = r12.getParentActivity();
        r11.<init>(r0);
        r0 = 2130838069; // 0x7f020235 float:1.728111E38 double:1.0527738867E-314;
        r11.setBackgroundResource(r0);
        r0 = "PinNotify";
        r1 = 2131166126; // 0x7f0703ae float:1.7946489E38 double:1.0529359684E-314;
        r0 = com.hanista.mobogram.messenger.LocaleController.getString(r0, r1);
        r1 = "";
        r2 = 1;
        r3 = 0;
        r11.setText(r0, r1, r2, r3);
        r0 = com.hanista.mobogram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x050d;
    L_0x04aa:
        r0 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r0 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r0);
    L_0x04b0:
        r2 = 0;
        r1 = com.hanista.mobogram.messenger.LocaleController.isRTL;
        if (r1 == 0) goto L_0x050f;
    L_0x04b5:
        r1 = 0;
    L_0x04b6:
        r3 = 0;
        r11.setPadding(r0, r2, r1, r3);
        r0 = -1;
        r1 = 1111490560; // 0x42400000 float:48.0 double:5.491493014E-315;
        r2 = 51;
        r3 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = 0;
        r5 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = 0;
        r0 = com.hanista.mobogram.ui.Components.LayoutHelper.createFrame(r0, r1, r2, r3, r4, r5, r6);
        r10.addView(r11, r0);
        r0 = new com.hanista.mobogram.ui.ChatActivity$82;
        r0.<init>(r9);
        r11.setOnClickListener(r0);
        r8.setView(r10);
        r0 = "OK";
        r1 = 2131166046; // 0x7f07035e float:1.7946326E38 double:1.052935929E-314;
        r0 = com.hanista.mobogram.messenger.LocaleController.getString(r0, r1);
        r1 = new com.hanista.mobogram.ui.ChatActivity$83;
        r1.<init>(r7, r9);
        r8.setPositiveButton(r0, r1);
        r0 = "AppName";
        r1 = 2131166486; // 0x7f070516 float:1.7947219E38 double:1.0529361463E-314;
        r0 = com.hanista.mobogram.messenger.LocaleController.getString(r0, r1);
        r8.setTitle(r0);
        r0 = "Cancel";
        r1 = 2131165385; // 0x7f0700c9 float:1.7944986E38 double:1.0529356023E-314;
        r0 = com.hanista.mobogram.messenger.LocaleController.getString(r0, r1);
        r1 = 0;
        r8.setNegativeButton(r0, r1);
        r0 = r8.create();
        r12.showDialog(r0);
        goto L_0x0008;
    L_0x050d:
        r0 = 0;
        goto L_0x04b0;
    L_0x050f:
        r1 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
        r1 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r1);
        goto L_0x04b6;
    L_0x0516:
        r0 = new android.app.AlertDialog$Builder;
        r1 = r12.getParentActivity();
        r0.<init>(r1);
        r1 = "UnpinMessageAlert";
        r2 = 2131166359; // 0x7f070497 float:1.7946961E38 double:1.0529360836E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
        r0.setMessage(r1);
        r1 = "OK";
        r2 = 2131166046; // 0x7f07035e float:1.7946326E38 double:1.052935929E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
        r2 = new com.hanista.mobogram.ui.ChatActivity$84;
        r2.<init>();
        r0.setPositiveButton(r1, r2);
        r1 = "AppName";
        r2 = 2131166486; // 0x7f070516 float:1.7947219E38 double:1.0529361463E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
        r0.setTitle(r1);
        r1 = "Cancel";
        r2 = 2131165385; // 0x7f0700c9 float:1.7944986E38 double:1.0529356023E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
        r2 = 0;
        r0.setNegativeButton(r1, r2);
        r0 = r0.create();
        r12.showDialog(r0);
        goto L_0x0008;
    L_0x0562:
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "user_id";
        r2 = r12.selectedObject;
        r2 = r2.messageOwner;
        r2 = r2.media;
        r2 = r2.user_id;
        r0.putInt(r1, r2);
        r1 = "phone";
        r2 = r12.selectedObject;
        r2 = r2.messageOwner;
        r2 = r2.media;
        r2 = r2.phone_number;
        r0.putString(r1, r2);
        r1 = "addContact";
        r2 = 1;
        r0.putBoolean(r1, r2);
        r1 = new com.hanista.mobogram.ui.ContactAddActivity;
        r1.<init>(r0);
        r12.presentFragment(r1);
        goto L_0x0008;
    L_0x0594:
        r0 = r12.selectedObject;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.phone_number;
        com.hanista.mobogram.messenger.AndroidUtilities.addToClipboard(r0);
        goto L_0x0008;
    L_0x05a1:
        r0 = new android.content.Intent;	 Catch:{ Exception -> 0x05d9 }
        r1 = "android.intent.action.DIAL";
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x05d9 }
        r2.<init>();	 Catch:{ Exception -> 0x05d9 }
        r3 = "tel:";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x05d9 }
        r3 = r12.selectedObject;	 Catch:{ Exception -> 0x05d9 }
        r3 = r3.messageOwner;	 Catch:{ Exception -> 0x05d9 }
        r3 = r3.media;	 Catch:{ Exception -> 0x05d9 }
        r3 = r3.phone_number;	 Catch:{ Exception -> 0x05d9 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x05d9 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x05d9 }
        r2 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x05d9 }
        r0.<init>(r1, r2);	 Catch:{ Exception -> 0x05d9 }
        r1 = 268435456; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r0.addFlags(r1);	 Catch:{ Exception -> 0x05d9 }
        r1 = r12.getParentActivity();	 Catch:{ Exception -> 0x05d9 }
        r2 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r1.startActivityForResult(r0, r2);	 Catch:{ Exception -> 0x05d9 }
        goto L_0x0008;
    L_0x05d9:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
        goto L_0x0008;
    L_0x05e2:
        r0 = 1;
        forwardNoName = r0;
        r0 = r12.selectedObject;
        r12.forwaringMessage = r0;
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "onlySelect";
        r2 = 1;
        r0.putBoolean(r1, r2);
        r1 = "dialogsType";
        r2 = 1;
        r0.putInt(r1, r2);
        r1 = new com.hanista.mobogram.ui.DialogsActivity;
        r1.<init>(r0);
        r1.setDelegate(r12);
        r12.presentFragment(r1);
        goto L_0x0008;
    L_0x0609:
        r0 = 0;
        forwardNoName = r0;
        r0 = r12.selectedObject;
        r12.forwaringMessage = r0;
        r12.multipleForward();
        goto L_0x0008;
    L_0x0615:
        r0 = 1;
        forwardNoName = r0;
        r0 = r12.selectedObject;
        r12.forwaringMessage = r0;
        r12.multipleForward();
        goto L_0x0008;
    L_0x0621:
        r0 = r12.dialogSettings;
        r0 = r0.m984g();
        r1 = r12.selectedObject;
        r1 = r1.getId();
        if (r0 != r1) goto L_0x0642;
    L_0x062f:
        r0 = r12.dialogSettings;
        r1 = 0;
        r0.m977b(r1);
    L_0x0635:
        r0 = r12.dialogSettings;
        com.hanista.mobogram.mobo.p005f.DialogSettingsUtil.m999a(r0);
        r12.updateVisibleRows();
        r12.showChatMarkerHelpDialog();
        goto L_0x0008;
    L_0x0642:
        r0 = r12.dialogSettings;
        r1 = r12.selectedObject;
        r1 = r1.getId();
        r0.m977b(r1);
        goto L_0x0635;
    L_0x064e:
        r12.editAndShareMessage();
        goto L_0x0008;
    L_0x0653:
        r1 = new com.hanista.mobogram.tgnet.TLRPC$messages_Messages;
        r1.<init>();
        r0 = r1.messages;
        r2 = r12.selectedObject;
        r2 = r2.messageOwner;
        r0.add(r2);
        r0 = com.hanista.mobogram.mobo.download.DownloadMessagesStorage.m783a();
        r2 = 1;
        r4 = -1;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r0.m811a(r1, r2, r4, r5, r6, r7);
        r0 = r12.getParentActivity();
        r1 = "FileAddedToDownloadList";
        r2 = 2131166635; // 0x7f0705ab float:1.794752E38 double:1.05293622E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
        r2 = 0;
        r0 = android.widget.Toast.makeText(r0, r1, r2);
        r0.show();
        goto L_0x0008;
    L_0x0686:
        r0 = r12.selectedObject;
        r0 = r0.isOutOwner();
        if (r0 != 0) goto L_0x06bc;
    L_0x068e:
        r0 = r12.selectedObject;
        r0 = r0.messageOwner;
        r0 = r0.from_id;
        if (r0 <= 0) goto L_0x06bc;
    L_0x0696:
        r0 = r12.selectedObject;
        r0 = r0.messageOwner;
        r0 = r0.post;
        if (r0 == 0) goto L_0x06bc;
    L_0x069e:
        r0 = 1;
    L_0x069f:
        if (r0 == 0) goto L_0x0008;
    L_0x06a1:
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = "user_id";
        r2 = r12.selectedObject;
        r2 = r2.messageOwner;
        r2 = r2.from_id;
        r0.putInt(r1, r2);
        r1 = new com.hanista.mobogram.ui.ProfileActivity;
        r1.<init>(r0);
        r12.presentFragment(r1);
        goto L_0x0008;
    L_0x06bc:
        r0 = 0;
        goto L_0x069f;
    L_0x06be:
        r0 = r12.archive;
        if (r0 == 0) goto L_0x0008;
    L_0x06c2:
        r0 = r12.selectedObject;
        r0 = r0.getId();
        r0 = com.hanista.mobogram.mobo.p000a.ArchiveUtil.m257a(r0);
        if (r0 == 0) goto L_0x0008;
    L_0x06ce:
        r1 = r0.m239d();
        r2 = r1.longValue();
        r4 = 0;
        r1 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r1 == 0) goto L_0x0008;
    L_0x06dc:
        r1 = r0.m238c();
        r1 = r1.intValue();
        if (r1 == 0) goto L_0x0008;
    L_0x06e6:
        r12.gotoOriginalMessage(r0);
        goto L_0x0008;
    L_0x06eb:
        r0 = r12.archive;
        if (r0 == 0) goto L_0x0008;
    L_0x06ef:
        r0 = r12.selectedObject;
        r0 = r0.getId();
        com.hanista.mobogram.mobo.p000a.ArchiveUtil.m268b(r0);
        r0 = new com.hanista.mobogram.mobo.e.a;
        r0.<init>();
        r1 = r12.archive;
        r1 = r1.m204a();
        r0 = r0.m904o(r1);
        r12.archive = r0;
        r12.resetChat();
        goto L_0x0008;
    L_0x070e:
        r12.copyPortionOfText();
        goto L_0x0008;
    L_0x0713:
        r0 = r1;
        goto L_0x012f;
    L_0x0716:
        r1 = r0;
        goto L_0x011f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.ChatActivity.processSelectedOption(int):void");
    }

    private void removeMessageFromArchive() {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", UserConfig.getClientUserId());
        bundle.putBoolean("selection_mode", true);
        bundle.putLong("archive_id", this.archive.m204a().longValue());
        BaseFragment chatActivity = new ChatActivity(bundle);
        chatActivity.setSelectionDelegate(new MessageSelectionDelegate() {
            public void didSelectMessages(List<Integer> list) {
                for (Integer intValue : list) {
                    ArchiveUtil.m268b(intValue.intValue());
                }
                ChatActivity.this.archive = new DataBaseAccess().m904o(ChatActivity.this.archive.m204a());
                ChatActivity.this.resetChat();
            }
        });
        presentFragment(chatActivity);
    }

    private void removeMessageObject(MessageObject messageObject) {
        int indexOf = this.messages.indexOf(messageObject);
        if (indexOf != -1) {
            this.messages.remove(indexOf);
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyItemRemoved(((this.chatAdapter.messagesStartRow + this.messages.size()) - indexOf) - 1);
            }
        }
    }

    private void removeUnreadPlane() {
        if (this.unreadMessageObject != null) {
            boolean[] zArr = this.forwardEndReached;
            this.forwardEndReached[attach_gallery] = true;
            zArr[attach_photo] = true;
            this.first_unread_id = attach_photo;
            this.last_message_id = attach_photo;
            this.unread_to_load = attach_photo;
            removeMessageObject(this.unreadMessageObject);
            this.unreadMessageObject = null;
        }
    }

    private void resetChat() {
        clearChatData();
        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
        MessagesController instance = MessagesController.getInstance();
        long j = this.dialog_id;
        boolean z = !this.gotoDateMessageSelected;
        int i = this.classGuid;
        boolean isChannel = ChatObject.isChannel(this.currentChat);
        int i2 = this.lastLoadIndex;
        this.lastLoadIndex = i2 + attach_gallery;
        instance.loadMessages(j, bot_help, attach_photo, z, attach_photo, i, attach_photo, attach_photo, isChannel, i2);
    }

    private void scrollToDateMessage(int i) {
        this.gotoMessageSelected = true;
        this.gotoDateMessage = i;
        this.gotoDateMessageSelected = true;
        this.waitingForLoad.clear();
        this.waitingForReplyMessageLoad = true;
        this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
        this.scrollToMessagePosition = -10000;
        this.startLoadFromMessageId = attach_photo;
        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
        MessagesController instance = MessagesController.getInstance();
        long j = this.dialog_id;
        int i2 = this.startLoadFromMessageId;
        int i3 = this.classGuid;
        boolean isChannel = ChatObject.isChannel(this.currentChat);
        int i4 = this.lastLoadIndex;
        this.lastLoadIndex = i4 + attach_gallery;
        instance.loadMessages(j, 23, i2, false, i, i3, attach_audio, ConnectionsManager.DEFAULT_DATACENTER_ID, isChannel, i4);
        this.returnToMessageId = attach_photo;
        this.returnToLoadIndex = attach_photo;
        this.needSelectFromMessageId = false;
    }

    private void scrollToLastMessage(boolean z) {
        boolean z2 = true;
        if (!this.forwardEndReached[attach_photo] || this.first_unread_id != 0 || this.startLoadFromMessageId != 0) {
            clearChatData();
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            MessagesController instance = MessagesController.getInstance();
            long j = this.dialog_id;
            if (this.gotoDateMessageSelected) {
                z2 = false;
            }
            int i = this.classGuid;
            boolean isChannel = ChatObject.isChannel(this.currentChat);
            int i2 = this.lastLoadIndex;
            this.lastLoadIndex = i2 + attach_gallery;
            instance.loadMessages(j, bot_help, attach_photo, z2, attach_photo, i, attach_photo, attach_photo, isChannel, i2);
        } else if (z && this.chatLayoutManager.findLastCompletelyVisibleItemPosition() == this.chatAdapter.getItemCount() - 1) {
            showPagedownButton(false, true);
            this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            updateVisibleRows();
        } else {
            this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
        }
    }

    private void scrollToMessageId(int i, int i2, boolean z, int i3) {
        Object obj;
        int childCount;
        int i4;
        MessageObject messageObject = (MessageObject) this.messagesDict[i3].get(Integer.valueOf(i));
        if (messageObject == null) {
            obj = attach_gallery;
        } else if (this.messages.indexOf(messageObject) != -1) {
            if (z) {
                this.highlightMessageId = i;
            } else {
                this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            int max = Math.max(attach_photo, (this.chatListView.getHeight() - messageObject.getApproximateHeight()) / attach_video);
            if (this.messages.get(this.messages.size() - 1) == messageObject) {
                this.chatLayoutManager.scrollToPositionWithOffset(attach_photo, max + ((-this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f)));
            } else {
                this.chatLayoutManager.scrollToPositionWithOffset(((this.chatAdapter.messagesStartRow + this.messages.size()) - this.messages.indexOf(messageObject)) - 1, max + ((-this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f)));
            }
            updateVisibleRows();
            childCount = this.chatListView.getChildCount();
            for (i4 = attach_photo; i4 < childCount; i4 += attach_gallery) {
                View childAt = this.chatListView.getChildAt(i4);
                MessageObject messageObject2;
                if (childAt instanceof ChatMessageCell) {
                    messageObject2 = ((ChatMessageCell) childAt).getMessageObject();
                    if (messageObject2 != null && messageObject2.getId() == messageObject.getId()) {
                        obj = attach_gallery;
                        break;
                    }
                } else if (childAt instanceof ChatActionCell) {
                    messageObject2 = ((ChatActionCell) childAt).getMessageObject();
                    if (messageObject2 != null && messageObject2.getId() == messageObject.getId()) {
                        obj = attach_gallery;
                        break;
                    }
                } else {
                    continue;
                }
            }
            obj = attach_photo;
            if (obj == null) {
                showPagedownButton(true, true);
            }
            obj = attach_photo;
        } else {
            obj = attach_gallery;
        }
        if (obj != null) {
            if (this.currentEncryptedChat == null || MessagesStorage.getInstance().checkMessageId(this.dialog_id, this.startLoadFromMessageId)) {
                this.waitingForLoad.clear();
                this.waitingForReplyMessageLoad = true;
                this.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                this.scrollToMessagePosition = -10000;
                this.startLoadFromMessageId = i;
                this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                MessagesController instance = MessagesController.getInstance();
                long j = i3 == 0 ? this.dialog_id : this.mergeDialogId;
                i4 = AndroidUtilities.isTablet() ? bot_help : edit_done;
                childCount = this.startLoadFromMessageId;
                boolean z2 = !this.gotoDateMessageSelected;
                int i5 = this.classGuid;
                boolean isChannel = ChatObject.isChannel(this.currentChat);
                int i6 = this.lastLoadIndex;
                this.lastLoadIndex = i6 + attach_gallery;
                instance.loadMessages(j, i4, childCount, z2, attach_photo, i5, attach_audio, attach_photo, isChannel, i6);
            } else {
                return;
            }
        }
        this.returnToMessageId = i2;
        this.returnToLoadIndex = i3;
        this.needSelectFromMessageId = z;
    }

    private void searchLinks(CharSequence charSequence, boolean z) {
        if (this.currentEncryptedChat == null || (MessagesController.getInstance().secretWebpagePreview != 0 && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46)) {
            if (z && this.foundWebPage != null) {
                if (this.foundWebPage.url != null) {
                    int indexOf = TextUtils.indexOf(charSequence, this.foundWebPage.url);
                    boolean z2;
                    boolean z3;
                    char charAt;
                    boolean z4;
                    if (indexOf != -1) {
                        z2 = this.foundWebPage.url.length() + indexOf == charSequence.length();
                        z3 = z2;
                        charAt = !z2 ? charSequence.charAt(this.foundWebPage.url.length() + indexOf) : '\u0000';
                        z4 = z3;
                    } else if (this.foundWebPage.display_url != null) {
                        indexOf = TextUtils.indexOf(charSequence, this.foundWebPage.display_url);
                        z2 = indexOf != -1 && this.foundWebPage.display_url.length() + indexOf == charSequence.length();
                        char charAt2 = (indexOf == -1 || z2) ? '\u0000' : charSequence.charAt(this.foundWebPage.display_url.length() + indexOf);
                        z3 = z2;
                        charAt = charAt2;
                        z4 = z3;
                    } else {
                        z4 = false;
                        z2 = false;
                    }
                    if (indexOf != -1 && (r0 || r3 == ' ' || r3 == ',' || r3 == '.' || r3 == '!' || r3 == '/')) {
                        return;
                    }
                }
                this.pendingLinkSearchString = null;
                showReplyPanel(false, null, null, this.foundWebPage, false, true);
            }
            Utilities.searchQueue.postRunnable(new AnonymousClass56(charSequence, z));
        }
    }

    private void sendBotInlineResult(BotInlineResult botInlineResult) {
        int contextBotId = this.mentionsAdapter.getContextBotId();
        HashMap hashMap = new HashMap();
        hashMap.put(TtmlNode.ATTR_ID, botInlineResult.id);
        hashMap.put("query_id", TtmlNode.ANONYMOUS_REGION_ID + botInlineResult.query_id);
        hashMap.put("bot", TtmlNode.ANONYMOUS_REGION_ID + contextBotId);
        hashMap.put("bot_name", this.mentionsAdapter.getContextBotName());
        SendMessagesHelper.prepareSendingBotContextResult(botInlineResult, hashMap, this.dialog_id, this.replyingMessageObject);
        this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
        showReplyPanel(false, null, null, null, false, true);
        SearchQuery.increaseInlineRaiting(contextBotId);
    }

    private boolean sendSecretMessageRead(MessageObject messageObject) {
        if (messageObject == null || messageObject.isOut() || !messageObject.isSecretMedia() || messageObject.messageOwner.destroyTime != 0 || messageObject.messageOwner.ttl <= 0) {
            return false;
        }
        MessagesController.getInstance().markMessageAsRead(this.dialog_id, messageObject.messageOwner.random_id, messageObject.messageOwner.ttl);
        messageObject.messageOwner.destroyTime = messageObject.messageOwner.ttl + ConnectionsManager.getInstance().getCurrentTime();
        return true;
    }

    private void showActionModeHelp() {
        View view = null;
        try {
            if (this.actionBar.isActionModeShowed()) {
                ActionBarMenu createActionMode = this.actionBar.createActionMode();
                Activity parentActivity = getParentActivity();
                View item = (createActionMode.getItem(forward) == null || createActionMode.getItem(forward).getVisibility() != 0) ? null : createActionMode.getItem(forward);
                if (createActionMode.getItem(select_all) != null && createActionMode.getItem(select_all).getVisibility() == 0) {
                    view = createActionMode.getItem(select_all);
                }
                MaterialHelperUtil.m1361a(parentActivity, item, view);
            }
        } catch (Exception e) {
        }
    }

    private void showAttachmentError() {
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", C0338R.string.UnsupportedAttachment), attach_photo).show();
        }
    }

    private void showChatMarkerHelpDialog() {
        if (UserConfig.isClientActivated()) {
            SettingManager settingManager = new SettingManager();
            if (!settingManager.m944b("chatMarkerHelpDisplayed")) {
                settingManager.m943a("chatMarkerHelpDisplayed", true);
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("ChatMarkerHelp", C0338R.string.ChatMarkerHelp));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        }
    }

    private void showDatePickerDialog() {
        Locale locale = LocaleController.getInstance().currentLocale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        if (language.toLowerCase().equals("fa") || language.toLowerCase().equals("ku") || MoboConstants.f1337d) {
            PersianCalendar persianCalendar = new PersianCalendar();
            DatePickerDialog.m2091a(new DatePickerDialog.DatePickerDialog() {
                public void onDateSet(DatePickerDialog datePickerDialog, int i, int i2, int i3) {
                    PersianCalendar persianCalendar = new PersianCalendar();
                    persianCalendar.m2050a(i, i2, i3);
                    persianCalendar.set(ChatActivity.forward, ChatActivity.attach_photo);
                    persianCalendar.set(ChatActivity.delete, ChatActivity.attach_photo);
                    persianCalendar.set(ChatActivity.chat_enc_timer, ChatActivity.attach_photo);
                    ChatActivity.this.scrollToDateMessage((int) (persianCalendar.getTimeInMillis() / 1000));
                }
            }, persianCalendar.m2051b(), persianCalendar.m2052c(), persianCalendar.m2054e()).show(getParentActivity().getFragmentManager(), "Datepickerdialog");
            return;
        }
        Calendar instance = Calendar.getInstance();
        new android.app.DatePickerDialog(getParentActivity(), new AnonymousClass105(instance), instance.get(attach_gallery), instance.get(attach_video), instance.get(attach_contact)).show();
    }

    private void showEditDoneProgress(boolean z, boolean z2) {
        if (this.editDoneItemAnimation != null) {
            this.editDoneItemAnimation.cancel();
        }
        if (z2) {
            this.editDoneItemAnimation = new AnimatorSet();
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            float[] fArr;
            float[] fArr2;
            if (z) {
                this.editDoneItemProgress.setVisibility(attach_photo);
                this.editDoneItem.setEnabled(false);
                animatorSet = this.editDoneItemAnimation;
                animatorArr = new Animator[attach_location];
                fArr = new float[attach_gallery];
                fArr[attach_photo] = 0.1f;
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", fArr);
                fArr = new float[attach_gallery];
                fArr[attach_photo] = 0.1f;
                animatorArr[attach_gallery] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", fArr);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = 0.0f;
                animatorArr[attach_video] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[attach_audio] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[attach_document] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[attach_contact] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", fArr2);
                animatorSet.playTogether(animatorArr);
            } else {
                this.editDoneItem.getImageView().setVisibility(attach_photo);
                this.editDoneItem.setEnabled(true);
                animatorSet = this.editDoneItemAnimation;
                animatorArr = new Animator[attach_location];
                fArr = new float[attach_gallery];
                fArr[attach_photo] = 0.1f;
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", fArr);
                fArr = new float[attach_gallery];
                fArr[attach_photo] = 0.1f;
                animatorArr[attach_gallery] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", fArr);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = 0.0f;
                animatorArr[attach_video] = ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[attach_audio] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[attach_document] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", fArr2);
                fArr2 = new float[attach_gallery];
                fArr2[attach_photo] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                animatorArr[attach_contact] = ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", fArr2);
                animatorSet.playTogether(animatorArr);
            }
            this.editDoneItemAnimation.addListener(new AnonymousClass80(z));
            this.editDoneItemAnimation.setDuration(150);
            this.editDoneItemAnimation.start();
        } else if (z) {
            this.editDoneItem.getImageView().setScaleX(0.1f);
            this.editDoneItem.getImageView().setScaleY(0.1f);
            this.editDoneItem.getImageView().setAlpha(0.0f);
            this.editDoneItemProgress.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItemProgress.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItemProgress.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItem.getImageView().setVisibility(attach_document);
            this.editDoneItemProgress.setVisibility(attach_photo);
            this.editDoneItem.setEnabled(false);
        } else {
            this.editDoneItemProgress.setScaleX(0.1f);
            this.editDoneItemProgress.setScaleY(0.1f);
            this.editDoneItemProgress.setAlpha(0.0f);
            this.editDoneItem.getImageView().setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItem.getImageView().setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItem.getImageView().setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.editDoneItem.getImageView().setVisibility(attach_photo);
            this.editDoneItemProgress.setVisibility(attach_document);
            this.editDoneItem.setEnabled(true);
        }
    }

    private void showFavoriteMessages() {
        Bundle bundle = new Bundle();
        if (this.currentUser != null) {
            bundle.putInt("user_id", this.currentUser.id);
        }
        if (this.currentChat != null) {
            bundle.putInt("chat_id", this.currentChat.id);
        }
        if (this.currentEncryptedChat != null) {
            bundle.putInt("enc_id", this.currentEncryptedChat.id);
        }
        bundle.putBoolean("show_just_favorites", true);
        presentFragment(new ChatActivity(bundle));
    }

    private void showGifHint() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", attach_photo);
        if (!sharedPreferences.getBoolean("gifhint", false)) {
            sharedPreferences.edit().putBoolean("gifhint", true).commit();
            if (getParentActivity() != null && this.fragmentView != null && this.gifHintTextView == null) {
                if (this.allowContextBotPanelSecond) {
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = (SizeNotifierFrameLayout) this.fragmentView;
                    int indexOfChild = sizeNotifierFrameLayout.indexOfChild(this.chatActivityEnterView);
                    if (indexOfChild != -1) {
                        this.chatActivityEnterView.setOpenGifsTabFirst();
                        this.emojiButtonRed = new View(getParentActivity());
                        this.emojiButtonRed.setBackgroundResource(C0338R.drawable.redcircle);
                        sizeNotifierFrameLayout.addView(this.emojiButtonRed, indexOfChild + attach_gallery, LayoutHelper.createFrame(copy, 10.0f, 83, BitmapDescriptorFactory.HUE_ORANGE, 0.0f, 0.0f, 27.0f));
                        this.gifHintTextView = new TextView(getParentActivity());
                        this.gifHintTextView.setBackgroundResource(C0338R.drawable.tooltip);
                        this.gifHintTextView.setTextColor(-1);
                        this.gifHintTextView.setTextSize(attach_gallery, 14.0f);
                        this.gifHintTextView.setPadding(AndroidUtilities.dp(10.0f), attach_photo, AndroidUtilities.dp(10.0f), attach_photo);
                        this.gifHintTextView.setText(LocaleController.getString("TapHereGifs", C0338R.string.TapHereGifs));
                        this.gifHintTextView.setGravity(delete_chat);
                        sizeNotifierFrameLayout.addView(this.gifHintTextView, indexOfChild + attach_gallery, LayoutHelper.createFrame(-2, 32.0f, 83, 5.0f, 0.0f, 0.0f, 3.0f));
                        AnimatorSet animatorSet = new AnimatorSet();
                        Animator[] animatorArr = new Animator[attach_video];
                        animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.gifHintTextView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                        animatorArr[attach_gallery] = ObjectAnimator.ofFloat(this.emojiButtonRed, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                        animatorSet.playTogether(animatorArr);
                        animatorSet.addListener(new AnimatorListenerAdapterProxy() {

                            /* renamed from: com.hanista.mobogram.ui.ChatActivity.49.1 */
                            class C12601 implements Runnable {

                                /* renamed from: com.hanista.mobogram.ui.ChatActivity.49.1.1 */
                                class C12591 extends AnimatorListenerAdapterProxy {
                                    C12591() {
                                    }

                                    public void onAnimationEnd(Animator animator) {
                                        if (ChatActivity.this.gifHintTextView != null) {
                                            ChatActivity.this.gifHintTextView.setVisibility(8);
                                        }
                                    }
                                }

                                C12601() {
                                }

                                public void run() {
                                    if (ChatActivity.this.gifHintTextView != null) {
                                        AnimatorSet animatorSet = new AnimatorSet();
                                        Animator[] animatorArr = new Animator[ChatActivity.attach_gallery];
                                        float[] fArr = new float[ChatActivity.attach_gallery];
                                        fArr[ChatActivity.attach_photo] = 0.0f;
                                        animatorArr[ChatActivity.attach_photo] = ObjectAnimator.ofFloat(ChatActivity.this.gifHintTextView, "alpha", fArr);
                                        animatorSet.playTogether(animatorArr);
                                        animatorSet.addListener(new C12591());
                                        animatorSet.setDuration(300);
                                        animatorSet.start();
                                    }
                                }
                            }

                            public void onAnimationEnd(Animator animator) {
                                AndroidUtilities.runOnUIThread(new C12601(), 2000);
                            }
                        });
                        animatorSet.setDuration(300);
                        animatorSet.start();
                    }
                } else if (this.chatActivityEnterView != null) {
                    this.chatActivityEnterView.setOpenGifsTabFirst();
                }
            }
        }
    }

    private void showHelpDialog() {
        if (!UserConfig.isClientActivated()) {
        }
    }

    private void showJumpToMessageDialog() {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("JumpToMessage", C0338R.string.JumpToMessage));
        List arrayList = new ArrayList();
        arrayList.add(LocaleController.getString("GoToFirstMessage", C0338R.string.GoToFirstMessage));
        arrayList.add(LocaleController.getString("GoToDate", C0338R.string.GoToDate));
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ChatActivity.this.gotoMessageSelected = true;
                    ChatActivity.this.scrollToMessageId(ChatActivity.attach_gallery, ChatActivity.attach_photo, false, ChatActivity.attach_photo);
                } else if (i == ChatActivity.attach_gallery) {
                    ChatActivity.this.showDatePickerDialog();
                }
            }
        });
        showDialog(builder.create());
    }

    private void showMaterialHelp() {
        View view = null;
        try {
            ChatMessageCell chatMessageCell;
            Rect rect;
            Rect rect2;
            Rect rect3;
            if (!(this.chatListView == null || (MaterialHelperUtil.m1359a("replyBtnHelp").booleanValue() && MaterialHelperUtil.m1359a("archiveBtnHelp").booleanValue() && MaterialHelperUtil.m1359a("showUseMessagesHelp").booleanValue() && MaterialHelperUtil.m1359a("messageActionsHelp").booleanValue()))) {
                int childCount = this.chatListView.getChildCount();
                for (int i = attach_photo; i < childCount; i += attach_gallery) {
                    View childAt = this.chatListView.getChildAt(i);
                    if (childAt instanceof ChatMessageCell) {
                        chatMessageCell = (ChatMessageCell) childAt;
                        break;
                    }
                }
            }
            chatMessageCell = null;
            if (chatMessageCell != null) {
                int[] iArr = new int[attach_video];
                chatMessageCell.getLocationOnScreen(iArr);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getParentActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                rect = (!chatMessageCell.drawShareButton || chatMessageCell.drawMenuButton || ((float) (chatMessageCell.shareStartY + iArr[attach_gallery])) >= ((float) displayMetrics.heightPixels) / displayMetrics.density || chatMessageCell.shareStartY + iArr[attach_gallery] <= AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW)) ? null : new Rect(chatMessageCell.shareStartX + iArr[attach_photo], chatMessageCell.shareStartY + iArr[attach_gallery], (chatMessageCell.shareStartX + iArr[attach_photo]) + AndroidUtilities.dp(40.0f), (chatMessageCell.shareStartY + iArr[attach_gallery]) + AndroidUtilities.dp(40.0f));
                rect2 = (!chatMessageCell.drawFavoriteButton || chatMessageCell.drawMenuButton || ((float) (chatMessageCell.favoriteStartY + iArr[attach_gallery])) >= ((float) displayMetrics.heightPixels) / displayMetrics.density || chatMessageCell.favoriteStartY + iArr[attach_gallery] <= AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW)) ? null : new Rect(chatMessageCell.favoriteStartX + iArr[attach_photo], chatMessageCell.favoriteStartY + iArr[attach_gallery], (chatMessageCell.favoriteStartX + iArr[attach_photo]) + AndroidUtilities.dp(40.0f), (chatMessageCell.favoriteStartY + iArr[attach_gallery]) + AndroidUtilities.dp(40.0f));
                rect3 = (!chatMessageCell.isAvatarVisible || chatMessageCell.avatarImage == null || ((float) (chatMessageCell.avatarImage.imageY + iArr[attach_gallery])) >= ((float) displayMetrics.heightPixels) / displayMetrics.density || chatMessageCell.avatarImage.imageY + iArr[attach_gallery] <= AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW)) ? null : new Rect(chatMessageCell.avatarImage.imageX + iArr[attach_photo], chatMessageCell.avatarImage.imageY + iArr[attach_gallery], (chatMessageCell.avatarImage.imageX + iArr[attach_photo]) + chatMessageCell.avatarImage.imageW, chatMessageCell.avatarImage.imageH + (iArr[attach_gallery] + chatMessageCell.avatarImage.imageY));
            } else {
                rect3 = null;
                rect2 = null;
                rect = null;
            }
            Activity parentActivity = getParentActivity();
            View view2 = this.headerItem;
            View view3 = (this.drawingItem == null || this.drawingItem.getVisibility() != 0) ? null : this.drawingItem;
            View audioSendButton = (this.chatActivityEnterView == null || this.chatActivityEnterView.getVisibility() != 0) ? null : this.chatActivityEnterView.getAudioSendButton();
            if (this.chatBarUtil != null) {
                view = this.chatBarUtil.f271a;
            }
            MaterialHelperUtil.m1364a(parentActivity, view2, view3, audioSendButton, view, rect2, rect, rect3);
        } catch (Exception e) {
        }
    }

    private void showMediaFragment() {
        Bundle bundle = new Bundle();
        bundle.putLong("dialog_id", this.dialog_id);
        BaseFragment mediaActivity = new MediaActivity(bundle);
        mediaActivity.setChatInfo(this.info);
        mediaActivity.setArchive(this.archive);
        presentFragment(mediaActivity);
    }

    private void showNewArchiveDialog(MessageObject messageObject) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("NewCategory", C0338R.string.NewCategory));
        View editText = new EditText(getParentActivity());
        if (VERSION.SDK_INT < forward) {
            editText.setBackgroundResource(17301529);
        }
        editText.setTextSize(18.0f);
        editText.setImeOptions(attach_location);
        editText.setSingleLine(true);
        builder.setView(editText);
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass98(editText, messageObject));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
        if (editText != null) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) editText.getLayoutParams();
            if (marginLayoutParams != null) {
                if (marginLayoutParams instanceof LayoutParams) {
                    ((LayoutParams) marginLayoutParams).gravity = attach_gallery;
                }
                int dp = AndroidUtilities.dp(10.0f);
                marginLayoutParams.leftMargin = dp;
                marginLayoutParams.rightMargin = dp;
                dp = AndroidUtilities.dp(10.0f);
                marginLayoutParams.bottomMargin = dp;
                marginLayoutParams.topMargin = dp;
                editText.setLayoutParams(marginLayoutParams);
            }
            editText.setSelection(editText.getText().length());
        }
    }

    private void showPagedownButton(boolean z, boolean z2) {
        if (this.pagedownButton != null) {
            float[] fArr;
            if (z) {
                this.pagedownButtonShowedByScroll = false;
                if (this.pagedownButton.getTag() == null) {
                    if (this.pagedownButtonAnimation != null) {
                        this.pagedownButtonAnimation.cancel();
                        this.pagedownButtonAnimation = null;
                    }
                    if (z2) {
                        if (this.pagedownButton.getTranslationY() == 0.0f) {
                            this.pagedownButton.setTranslationY((float) AndroidUtilities.dp(100.0f));
                        }
                        this.pagedownButton.setVisibility(attach_photo);
                        this.pagedownButton.setTag(Integer.valueOf(attach_gallery));
                        fArr = new float[attach_gallery];
                        fArr[attach_photo] = 0.0f;
                        this.pagedownButtonAnimation = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", fArr).setDuration(200);
                        this.pagedownButtonAnimation.start();
                        return;
                    }
                    this.pagedownButton.setVisibility(attach_photo);
                    return;
                }
                return;
            }
            this.returnToMessageId = attach_photo;
            this.newUnreadMessageCount = attach_photo;
            if (this.pagedownButton.getTag() != null) {
                this.pagedownButton.setTag(null);
                if (this.pagedownButtonAnimation != null) {
                    this.pagedownButtonAnimation.cancel();
                    this.pagedownButtonAnimation = null;
                }
                if (z2) {
                    fArr = new float[attach_gallery];
                    fArr[attach_photo] = (float) AndroidUtilities.dp(100.0f);
                    this.pagedownButtonAnimation = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", fArr).setDuration(200);
                    this.pagedownButtonAnimation.addListener(new AnimatorListenerAdapterProxy() {
                        public void onAnimationEnd(Animator animator) {
                            ChatActivity.this.pagedownButtonCounter.setVisibility(ChatActivity.attach_document);
                            ChatActivity.this.pagedownButton.setVisibility(ChatActivity.attach_document);
                        }
                    });
                    this.pagedownButtonAnimation.start();
                    return;
                }
                this.pagedownButton.setVisibility(attach_document);
            }
        }
    }

    private void showSendDrawingOptions(PhotoEntry photoEntry) {
        if ((this.currentChat == null || (!ChatObject.isNotInChat(this.currentChat) && ChatObject.canWriteToChat(this.currentChat))) && (this.currentUser == null || !(UserObject.isDeleted(this.currentUser) || this.userBlocked))) {
            Builder builder = new Builder(getParentActivity());
            List arrayList = new ArrayList();
            arrayList.add(LocaleController.getString("SendToThisChat", C0338R.string.SendToThisChat));
            arrayList.add(LocaleController.getString("SendToOtherChats", C0338R.string.SendToOtherChats));
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new AnonymousClass93(photoEntry));
            showDialog(builder.create());
            return;
        }
        multipleForwardDrawing(photoEntry);
    }

    private void showStickerSendConfirmDialog(Document document) {
        if (getParentActivity() != null && document != null) {
            showDialog(new StickerSelectAlert(getParentActivity(), document, new AnonymousClass89(document)));
        }
    }

    private void toggleMute(boolean z) {
        Editor edit;
        TL_dialog tL_dialog;
        if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
            edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", attach_photo).edit();
            edit.putInt("notify2_" + this.dialog_id, attach_photo);
            MessagesStorage.getInstance().setDialogFlags(this.dialog_id, 0);
            edit.commit();
            tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
            if (tL_dialog != null) {
                tL_dialog.notify_settings = new TL_peerNotifySettings();
            }
            NotificationsController.updateServerNotificationsSettings(this.dialog_id);
        } else if (z) {
            edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", attach_photo).edit();
            edit.putInt("notify2_" + this.dialog_id, attach_video);
            MessagesStorage.getInstance().setDialogFlags(this.dialog_id, 1);
            edit.commit();
            tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
            if (tL_dialog != null) {
                tL_dialog.notify_settings = new TL_peerNotifySettings();
                tL_dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            NotificationsController.updateServerNotificationsSettings(this.dialog_id);
            NotificationsController.getInstance().removeNotificationsForDialog(this.dialog_id);
        } else {
            showDialog(AlertsCreator.createMuteAlert(getParentActivity(), this.dialog_id));
        }
    }

    private void updateActionModeTitle() {
        if (!this.actionBar.isActionModeShowed()) {
            return;
        }
        if (!this.selectedMessagesIds[attach_photo].isEmpty() || !this.selectedMessagesIds[attach_gallery].isEmpty()) {
            this.selectedMessagesCountTextView.setNumber(this.selectedMessagesIds[attach_photo].size() + this.selectedMessagesIds[attach_gallery].size(), true);
        } else if (this.selectionMode) {
            this.selectedMessagesCountTextView.setNumber(attach_photo, true);
        }
    }

    private void updateAddToDownloadListItem() {
        ActionBarMenuItem item = this.actionBar.createActionMode().getItem(add_to_download_list);
        if (item != null) {
            item.setVisibility(attach_photo);
            for (int i = attach_gallery; i >= 0; i--) {
                ArrayList arrayList = new ArrayList(this.selectedMessagesIds[i].keySet());
                Collections.sort(arrayList);
                int i2 = attach_photo;
                while (i2 < arrayList.size()) {
                    Integer num = (Integer) arrayList.get(i2);
                    MessageObject messageObject = (MessageObject) this.selectedMessagesIds[i].get(num);
                    if (messageObject == null || num.intValue() <= 0 || isMessageDownloadable(messageObject)) {
                        i2 += attach_gallery;
                    } else {
                        item.setVisibility(8);
                        return;
                    }
                }
            }
        }
    }

    private void updateBackground(SizeNotifierFrameLayout sizeNotifierFrameLayout) {
        if (!ThemeUtil.m2490b()) {
            return;
        }
        if (AdvanceTheme.bN) {
            int i = AdvanceTheme.bO;
            int i2 = AdvanceTheme.bP;
            if (i2 == 0) {
                sizeNotifierFrameLayout.setBackgroundDrawable(new ColorDrawable(i));
                return;
            }
            Orientation orientation;
            switch (i2) {
                case attach_video /*2*/:
                    orientation = Orientation.LEFT_RIGHT;
                    break;
                case attach_audio /*3*/:
                    orientation = Orientation.TL_BR;
                    break;
                case attach_document /*4*/:
                    orientation = Orientation.BL_TR;
                    break;
                default:
                    orientation = Orientation.TOP_BOTTOM;
                    break;
            }
            int i3 = AdvanceTheme.bQ;
            int[] iArr = new int[attach_video];
            iArr[attach_photo] = i;
            iArr[attach_gallery] = i3;
            sizeNotifierFrameLayout.setBackgroundDrawable(new GradientDrawable(orientation, iArr));
            return;
        }
        sizeNotifierFrameLayout.setBackgroundImage(ApplicationLoader.getCachedWallpaper());
    }

    private void updateBotButtons() {
        if (this.headerItem != null && this.currentUser != null && this.currentEncryptedChat == null && this.currentUser.bot) {
            Object obj;
            Object obj2;
            if (this.botInfo.isEmpty()) {
                obj = attach_photo;
                obj2 = attach_photo;
            } else {
                obj = attach_photo;
                obj2 = attach_photo;
                for (Entry value : this.botInfo.entrySet()) {
                    Object obj3;
                    BotInfo botInfo = (BotInfo) value.getValue();
                    Object obj4 = obj;
                    Object obj5 = obj2;
                    for (int i = attach_photo; i < botInfo.commands.size(); i += attach_gallery) {
                        TL_botCommand tL_botCommand = (TL_botCommand) botInfo.commands.get(i);
                        if (tL_botCommand.command.toLowerCase().equals("help")) {
                            obj5 = attach_gallery;
                        } else if (tL_botCommand.command.toLowerCase().equals("settings")) {
                            obj4 = attach_gallery;
                        }
                        if (obj4 != null && obj5 != null) {
                            obj3 = obj4;
                            obj = obj5;
                            break;
                        }
                    }
                    obj3 = obj4;
                    obj = obj5;
                    obj2 = obj;
                    obj = obj3;
                }
            }
            if (obj2 != null) {
                this.headerItem.showSubItem(bot_help);
            } else {
                this.headerItem.hideSubItem(bot_help);
            }
            if (obj != null) {
                this.headerItem.showSubItem(bot_settings);
            } else {
                this.headerItem.hideSubItem(bot_settings);
            }
        }
    }

    private void updateBottomOverlay() {
        if (this.bottomOverlayChatText != null) {
            if (this.currentChat != null) {
                if (!ChatObject.isChannel(this.currentChat) || (this.currentChat instanceof TL_channelForbidden)) {
                    if (ChatObject.isKickedFromChat(this.currentChat) || !ChatObject.isLeftFromChat(this.currentChat)) {
                        this.bottomOverlayChatText.setText(LocaleController.getString("DeleteThisGroup", C0338R.string.DeleteThisGroup));
                    } else {
                        this.bottomOverlayChatText.setText(LocaleController.getString("ReturnToGroup", C0338R.string.ReturnToGroup));
                    }
                } else if (ChatObject.isNotInChat(this.currentChat)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelJoin", C0338R.string.ChannelJoin));
                } else if (MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelUnmute", C0338R.string.ChannelUnmute));
                } else {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelMute", C0338R.string.ChannelMute));
                }
            } else if (this.userBlocked) {
                if (this.currentUser.bot) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("BotUnblock", C0338R.string.BotUnblock));
                } else {
                    this.bottomOverlayChatText.setText(LocaleController.getString("Unblock", C0338R.string.Unblock));
                }
                if (this.botButtons != null) {
                    this.botButtons = null;
                    if (this.chatActivityEnterView != null) {
                        if (this.replyingMessageObject != null && this.botReplyButtons == this.replyingMessageObject) {
                            this.botReplyButtons = null;
                            showReplyPanel(false, null, null, null, false, true);
                        }
                        this.chatActivityEnterView.setButtons(this.botButtons, false);
                    }
                }
            } else if (this.botUser == null || !this.currentUser.bot) {
                this.bottomOverlayChatText.setText(LocaleController.getString("DeleteThisChat", C0338R.string.DeleteThisChat));
            } else {
                this.bottomOverlayChatText.setText(LocaleController.getString("BotStart", C0338R.string.BotStart));
                this.chatActivityEnterView.hidePopup(false);
                if (getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }
            if (this.searchItem == null || this.searchItem.getVisibility() != 0) {
                this.searchContainer.setVisibility(attach_document);
                if ((this.currentChat == null || (!ChatObject.isNotInChat(this.currentChat) && ChatObject.canWriteToChat(this.currentChat))) && (this.currentUser == null || !(UserObject.isDeleted(this.currentUser) || this.userBlocked))) {
                    if (this.botUser == null || !this.currentUser.bot) {
                        this.chatActivityEnterView.setVisibility(attach_photo);
                        this.bottomOverlayChat.setVisibility(attach_document);
                    } else {
                        this.bottomOverlayChat.setVisibility(attach_photo);
                        this.chatActivityEnterView.setVisibility(attach_document);
                    }
                    if (this.muteItem != null) {
                        this.muteItem.setVisibility(attach_photo);
                    }
                    if (!(this.archive == null || this.muteItem == null)) {
                        this.muteItem.setVisibility(8);
                    }
                } else {
                    this.bottomOverlayChat.setVisibility(attach_photo);
                    if (this.muteItem != null) {
                        this.muteItem.setVisibility(8);
                    }
                    this.chatActivityEnterView.setFieldFocused(false);
                    this.chatActivityEnterView.setVisibility(attach_document);
                    this.attachItem.setVisibility(8);
                    this.headerItem.setVisibility(attach_photo);
                }
            } else {
                this.searchContainer.setVisibility(attach_photo);
                this.bottomOverlayChat.setVisibility(attach_document);
                this.chatActivityEnterView.setFieldFocused(false);
                this.chatActivityEnterView.setVisibility(attach_document);
            }
            checkRaiseSensors();
        }
    }

    private void updateContactStatus() {
        if (this.addContactItem != null) {
            if (this.currentUser == null) {
                this.addContactItem.setVisibility(8);
            } else {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
                if (user != null) {
                    this.currentUser = user;
                }
                if ((this.currentEncryptedChat != null && !(this.currentEncryptedChat instanceof TL_encryptedChat)) || this.currentUser.id / id_chat_compose_panel == 333 || this.currentUser.id / id_chat_compose_panel == 777 || UserObject.isDeleted(this.currentUser) || ContactsController.getInstance().isLoadingContacts() || (this.currentUser.phone != null && this.currentUser.phone.length() != 0 && ContactsController.getInstance().contactsDict.get(this.currentUser.id) != null && (ContactsController.getInstance().contactsDict.size() != 0 || !ContactsController.getInstance().isLoadingContacts()))) {
                    this.addContactItem.setVisibility(8);
                } else {
                    this.addContactItem.setVisibility(attach_photo);
                    if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                        this.addContactItem.setText(LocaleController.getString("ShareMyContactInfo", C0338R.string.ShareMyContactInfo));
                        this.addToContactsButton.setVisibility(8);
                        this.reportSpamButton.setPadding(AndroidUtilities.dp(50.0f), attach_photo, AndroidUtilities.dp(50.0f), attach_photo);
                        this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 51, attach_photo, attach_photo, attach_photo, AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
                    } else {
                        this.addContactItem.setText(LocaleController.getString("AddToContacts", C0338R.string.AddToContacts));
                        this.reportSpamButton.setPadding(AndroidUtilities.dp(4.0f), attach_photo, AndroidUtilities.dp(50.0f), attach_photo);
                        this.addToContactsButton.setVisibility(attach_photo);
                        this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, 0.5f, 51, attach_photo, attach_photo, attach_photo, AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
                    }
                }
            }
            checkListViewPaddings();
        }
    }

    private void updateDateToast() {
        if (this.chatListView != null && MoboConstants.ab) {
            View childAt = this.chatListView.getChildAt(attach_photo);
            if (childAt instanceof ChatMessageCell) {
                CharSequence formatDateChat = LocaleController.formatDateChat((long) ((ChatMessageCell) childAt).getMessageObject().messageOwner.date);
                if (formatDateChat.length() >= attach_gallery) {
                    this.dateToastTv.setVisibility(attach_photo);
                    this.dateToastTv.setText(formatDateChat);
                }
            }
        }
    }

    private void updateInformationForScreenshotDetector() {
        if (this.currentEncryptedChat != null) {
            ArrayList arrayList = new ArrayList();
            if (this.chatListView != null) {
                int childCount = this.chatListView.getChildCount();
                for (int i = attach_photo; i < childCount; i += attach_gallery) {
                    View childAt = this.chatListView.getChildAt(i);
                    MessageObject messageObject = childAt instanceof ChatMessageCell ? ((ChatMessageCell) childAt).getMessageObject() : null;
                    if (!(messageObject == null || messageObject.getId() >= 0 || messageObject.messageOwner.random_id == 0)) {
                        arrayList.add(Long.valueOf(messageObject.messageOwner.random_id));
                    }
                }
            }
            MediaController.m71a().m146a(this.chatEnterTime, this.chatLeaveTime, this.currentEncryptedChat, arrayList);
        }
    }

    private void updateMessagesVisisblePart() {
        if (this.chatListView != null) {
            int childCount = this.chatListView.getChildCount();
            if (this.chatActivityEnterView.isTopViewVisible()) {
                AndroidUtilities.dp(48.0f);
            }
            int measuredHeight = this.chatListView.getMeasuredHeight();
            for (int i = attach_photo; i < childCount; i += attach_gallery) {
                View childAt = this.chatListView.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    int top = chatMessageCell.getTop();
                    chatMessageCell.getBottom();
                    top = top >= 0 ? attach_photo : -top;
                    int measuredHeight2 = chatMessageCell.getMeasuredHeight();
                    if (measuredHeight2 > measuredHeight) {
                        measuredHeight2 = top + measuredHeight;
                    }
                    chatMessageCell.setVisiblePart(top, measuredHeight2 - top);
                }
            }
        }
    }

    private void updatePinnedMessageView(boolean z) {
        if (this.pinnedMessageView != null) {
            if (this.info != null) {
                if (!(this.pinnedMessageObject == null || this.info.pinned_msg_id == this.pinnedMessageObject.getId())) {
                    this.pinnedMessageObject = null;
                }
                if (this.info.pinned_msg_id != 0 && this.pinnedMessageObject == null) {
                    this.pinnedMessageObject = (MessageObject) this.messagesDict[attach_photo].get(Integer.valueOf(this.info.pinned_msg_id));
                }
            }
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", attach_photo);
            if (this.info == null || this.info.pinned_msg_id == 0 || this.info.pinned_msg_id == sharedPreferences.getInt("pin_" + this.dialog_id, attach_photo) || (this.actionBar != null && this.actionBar.isActionModeShowed())) {
                hidePinnedMessageView(z);
            } else if (this.pinnedMessageObject != null) {
                if (this.pinnedMessageView.getTag() != null) {
                    this.pinnedMessageView.setTag(null);
                    if (this.pinnedMessageViewAnimator != null) {
                        this.pinnedMessageViewAnimator.cancel();
                        this.pinnedMessageViewAnimator = null;
                    }
                    if (z) {
                        this.pinnedMessageView.setVisibility(attach_photo);
                        this.pinnedMessageViewAnimator = new AnimatorSet();
                        AnimatorSet animatorSet = this.pinnedMessageViewAnimator;
                        Animator[] animatorArr = new Animator[attach_gallery];
                        float[] fArr = new float[attach_gallery];
                        fArr[attach_photo] = 0.0f;
                        animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.pinnedMessageView, "translationY", fArr);
                        animatorSet.playTogether(animatorArr);
                        this.pinnedMessageViewAnimator.setDuration(200);
                        this.pinnedMessageViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                            public void onAnimationCancel(Animator animator) {
                                if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animator)) {
                                    ChatActivity.this.pinnedMessageViewAnimator = null;
                                }
                            }

                            public void onAnimationEnd(Animator animator) {
                                if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animator)) {
                                    ChatActivity.this.pinnedMessageViewAnimator = null;
                                }
                            }
                        });
                        this.pinnedMessageViewAnimator.start();
                        this.chatBarUtil.m379b();
                    } else {
                        this.pinnedMessageView.setTranslationY(0.0f);
                        this.pinnedMessageView.setVisibility(attach_photo);
                        this.chatBarUtil.m379b();
                    }
                }
                LayoutParams layoutParams = (LayoutParams) this.pinnedMessageNameTextView.getLayoutParams();
                LayoutParams layoutParams2 = (LayoutParams) this.pinnedMessageTextView.getLayoutParams();
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.pinnedMessageObject.photoThumbs2, AndroidUtilities.dp(50.0f));
                if (closestPhotoSizeWithSize == null) {
                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.pinnedMessageObject.photoThumbs, AndroidUtilities.dp(50.0f));
                }
                int dp;
                if (closestPhotoSizeWithSize == null || (closestPhotoSizeWithSize instanceof TL_photoSizeEmpty) || (closestPhotoSizeWithSize.location instanceof TL_fileLocationUnavailable) || this.pinnedMessageObject.type == chat_enc_timer) {
                    this.pinnedMessageImageView.setImageBitmap(null);
                    this.pinnedImageLocation = null;
                    this.pinnedMessageImageView.setVisibility(attach_document);
                    dp = AndroidUtilities.dp(18.0f);
                    layoutParams2.leftMargin = dp;
                    layoutParams.leftMargin = dp;
                } else {
                    this.pinnedImageLocation = closestPhotoSizeWithSize.location;
                    this.pinnedMessageImageView.setImage(this.pinnedImageLocation, "50_50", (Drawable) attach_photo);
                    this.pinnedMessageImageView.setVisibility(attach_photo);
                    dp = AndroidUtilities.dp(55.0f);
                    layoutParams2.leftMargin = dp;
                    layoutParams.leftMargin = dp;
                }
                this.pinnedMessageNameTextView.setLayoutParams(layoutParams);
                this.pinnedMessageTextView.setLayoutParams(layoutParams2);
                this.pinnedMessageNameTextView.setText(LocaleController.getString("PinnedMessage", C0338R.string.PinnedMessage));
                if (this.pinnedMessageObject.type == chat_menu_attach) {
                    SimpleTextView simpleTextView = this.pinnedMessageTextView;
                    Object[] objArr = new Object[attach_video];
                    objArr[attach_photo] = this.pinnedMessageObject.getMusicAuthor();
                    objArr[attach_gallery] = this.pinnedMessageObject.getMusicTitle();
                    simpleTextView.setText(String.format("%s - %s", objArr));
                } else if (this.pinnedMessageObject.messageOwner.media instanceof TL_messageMediaGame) {
                    this.pinnedMessageTextView.setText(Emoji.replaceEmoji(this.pinnedMessageObject.messageOwner.media.game.title, this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                } else if (this.pinnedMessageObject.messageText != null) {
                    String charSequence = this.pinnedMessageObject.messageText.toString();
                    if (charSequence.length() > 150) {
                        charSequence = charSequence.substring(attach_photo, 150);
                    }
                    this.pinnedMessageTextView.setText(Emoji.replaceEmoji(charSequence.replace('\n', ' '), this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                }
            } else {
                this.pinnedImageLocation = null;
                hidePinnedMessageView(z);
                if (this.loadingPinnedMessage != this.info.pinned_msg_id) {
                    this.loadingPinnedMessage = this.info.pinned_msg_id;
                    MessagesQuery.loadPinnedMessage(this.currentChat.id, this.info.pinned_msg_id, true);
                }
            }
            checkListViewPaddings();
        }
    }

    private void updateSearchButtons(int i, int i2, int i3) {
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        if (this.searchUpButton != null) {
            this.searchUpButton.setEnabled((i & attach_gallery) != 0);
            this.searchDownButton.setEnabled((i & attach_video) != 0);
            this.searchUpButton.setAlpha(this.searchUpButton.isEnabled() ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.5f);
            ImageView imageView = this.searchDownButton;
            if (!this.searchDownButton.isEnabled()) {
                f = 0.5f;
            }
            imageView.setAlpha(f);
            if (i3 == 0) {
                this.searchCountText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                return;
            }
            SimpleTextView simpleTextView = this.searchCountText;
            Object[] objArr = new Object[attach_video];
            objArr[attach_photo] = Integer.valueOf(i2 + attach_gallery);
            objArr[attach_gallery] = Integer.valueOf(i3);
            simpleTextView.setText(LocaleController.formatString("Of", C0338R.string.Of, objArr));
        }
    }

    private void updateSecretStatus() {
        int i = attach_gallery;
        if (this.bottomOverlay != null) {
            if (this.currentEncryptedChat == null || this.bigEmptyView == null) {
                this.bottomOverlay.setVisibility(attach_document);
                return;
            }
            if (this.currentEncryptedChat instanceof TL_encryptedChatRequested) {
                this.bottomOverlayText.setText(LocaleController.getString("EncryptionProcessing", C0338R.string.EncryptionProcessing));
                this.bottomOverlay.setVisibility(attach_photo);
            } else if (this.currentEncryptedChat instanceof TL_encryptedChatWaiting) {
                TextView textView = this.bottomOverlayText;
                Object[] objArr = new Object[attach_gallery];
                objArr[attach_photo] = "<b>" + this.currentUser.first_name + "</b>";
                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AwaitingEncryption", C0338R.string.AwaitingEncryption, objArr)));
                this.bottomOverlay.setVisibility(attach_photo);
            } else if (this.currentEncryptedChat instanceof TL_encryptedChatDiscarded) {
                this.bottomOverlayText.setText(LocaleController.getString("EncryptionRejected", C0338R.string.EncryptionRejected));
                this.bottomOverlay.setVisibility(attach_photo);
                this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                DraftQuery.cleanDraft(this.dialog_id, false);
            } else {
                if (this.currentEncryptedChat instanceof TL_encryptedChat) {
                    this.bottomOverlay.setVisibility(attach_document);
                }
                i = attach_photo;
            }
            checkRaiseSensors();
            if (i != 0) {
                this.chatActivityEnterView.hidePopup(false);
                if (getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }
            checkActionBarMenu();
        }
    }

    private void updateSelectAllListItem() {
        ActionBarMenuItem item = this.actionBar.createActionMode().getItem(select_all);
        if (item != null) {
            item.setVisibility(8);
            for (int i = attach_gallery; i >= 0; i--) {
                if (new ArrayList(this.selectedMessagesIds[i].keySet()).size() > attach_gallery) {
                    item.setVisibility(attach_photo);
                    return;
                }
            }
        }
    }

    private void updateSpamView() {
        if (this.reportSpamView != null) {
            int i;
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            float[] fArr;
            int i2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", attach_photo).getInt(new StringBuilder().append("spam3_").append(this.dialog_id).toString(), attach_photo) == attach_video ? attach_gallery : attach_photo;
            if (i2 != 0) {
                if (this.messages.isEmpty()) {
                    i = attach_photo;
                } else {
                    int size = this.messages.size() - 1;
                    for (int i3 = size; i3 >= Math.max(size - 50, attach_photo); i3--) {
                        if (((MessageObject) this.messages.get(i3)).isOut()) {
                            i = attach_photo;
                            break;
                        }
                    }
                }
                if (i != 0) {
                    if (this.reportSpamView.getTag() == null) {
                        this.reportSpamView.setTag(Integer.valueOf(attach_gallery));
                        if (this.reportSpamViewAnimator != null) {
                            this.reportSpamViewAnimator.cancel();
                        }
                        this.reportSpamViewAnimator = new AnimatorSet();
                        animatorSet = this.reportSpamViewAnimator;
                        animatorArr = new Animator[attach_gallery];
                        fArr = new float[attach_gallery];
                        fArr[attach_photo] = (float) (-AndroidUtilities.dp(50.0f));
                        animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.reportSpamView, "translationY", fArr);
                        animatorSet.playTogether(animatorArr);
                        this.reportSpamViewAnimator.setDuration(200);
                        this.reportSpamViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                            public void onAnimationCancel(Animator animator) {
                                if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator)) {
                                    ChatActivity.this.reportSpamViewAnimator = null;
                                }
                            }

                            public void onAnimationEnd(Animator animator) {
                                if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator)) {
                                    ChatActivity.this.reportSpamView.setVisibility(8);
                                    ChatActivity.this.reportSpamViewAnimator = null;
                                }
                            }
                        });
                        this.reportSpamViewAnimator.start();
                    }
                } else if (this.reportSpamView.getTag() != null) {
                    this.reportSpamView.setTag(null);
                    this.reportSpamView.setVisibility(attach_photo);
                    if (this.reportSpamViewAnimator != null) {
                        this.reportSpamViewAnimator.cancel();
                    }
                    this.reportSpamViewAnimator = new AnimatorSet();
                    animatorSet = this.reportSpamViewAnimator;
                    animatorArr = new Animator[attach_gallery];
                    fArr = new float[attach_gallery];
                    fArr[attach_photo] = 0.0f;
                    animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.reportSpamView, "translationY", fArr);
                    animatorSet.playTogether(animatorArr);
                    this.reportSpamViewAnimator.setDuration(200);
                    this.reportSpamViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                        public void onAnimationCancel(Animator animator) {
                            if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator)) {
                                ChatActivity.this.reportSpamViewAnimator = null;
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
                            if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator)) {
                                ChatActivity.this.reportSpamViewAnimator = null;
                            }
                        }
                    });
                    this.reportSpamViewAnimator.start();
                }
                checkListViewPaddings();
            }
            i = i2;
            if (i != 0) {
                if (this.reportSpamView.getTag() != null) {
                    this.reportSpamView.setTag(null);
                    this.reportSpamView.setVisibility(attach_photo);
                    if (this.reportSpamViewAnimator != null) {
                        this.reportSpamViewAnimator.cancel();
                    }
                    this.reportSpamViewAnimator = new AnimatorSet();
                    animatorSet = this.reportSpamViewAnimator;
                    animatorArr = new Animator[attach_gallery];
                    fArr = new float[attach_gallery];
                    fArr[attach_photo] = 0.0f;
                    animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.reportSpamView, "translationY", fArr);
                    animatorSet.playTogether(animatorArr);
                    this.reportSpamViewAnimator.setDuration(200);
                    this.reportSpamViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                        public void onAnimationCancel(Animator animator) {
                            if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator)) {
                                ChatActivity.this.reportSpamViewAnimator = null;
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
                            if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator)) {
                                ChatActivity.this.reportSpamViewAnimator = null;
                            }
                        }
                    });
                    this.reportSpamViewAnimator.start();
                }
            } else if (this.reportSpamView.getTag() == null) {
                this.reportSpamView.setTag(Integer.valueOf(attach_gallery));
                if (this.reportSpamViewAnimator != null) {
                    this.reportSpamViewAnimator.cancel();
                }
                this.reportSpamViewAnimator = new AnimatorSet();
                animatorSet = this.reportSpamViewAnimator;
                animatorArr = new Animator[attach_gallery];
                fArr = new float[attach_gallery];
                fArr[attach_photo] = (float) (-AndroidUtilities.dp(50.0f));
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.reportSpamView, "translationY", fArr);
                animatorSet.playTogether(animatorArr);
                this.reportSpamViewAnimator.setDuration(200);
                this.reportSpamViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationCancel(Animator animator) {
                        if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator)) {
                            ChatActivity.this.reportSpamViewAnimator = null;
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator)) {
                            ChatActivity.this.reportSpamView.setVisibility(8);
                            ChatActivity.this.reportSpamViewAnimator = null;
                        }
                    }
                });
                this.reportSpamViewAnimator.start();
            }
            checkListViewPaddings();
        }
    }

    private void updateTitle() {
        if (this.avatarContainer != null) {
            if (this.currentChat != null) {
                this.avatarContainer.setTitle(this.currentChat.title);
            } else if (this.currentUser == null) {
            } else {
                if (this.currentUser.self) {
                    this.avatarContainer.setTitle(LocaleController.getString("ChatYourSelfName", C0338R.string.ChatYourSelfName));
                } else if (this.currentUser.id / id_chat_compose_panel == 777 || this.currentUser.id / id_chat_compose_panel == 333 || ContactsController.getInstance().contactsDict.get(this.currentUser.id) != null || (ContactsController.getInstance().contactsDict.size() == 0 && ContactsController.getInstance().isLoadingContacts())) {
                    this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
                } else if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                    this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
                } else {
                    this.avatarContainer.setTitle(PhoneFormat.getInstance().format("+" + this.currentUser.phone));
                }
            }
        }
    }

    private void updateTitleIcons() {
        int i = attach_photo;
        if (this.avatarContainer != null) {
            int i2 = MessagesController.getInstance().isDialogMuted(this.dialog_id) ? C0338R.drawable.mute_fixed : attach_photo;
            ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
            if (this.currentEncryptedChat != null) {
                i = C0338R.drawable.ic_lock_header;
            }
            chatAvatarContainer.setTitleIcons(i, i2);
            if (this.muteItem == null) {
                return;
            }
            if (i2 != 0) {
                this.muteItem.setText(LocaleController.getString("UnmuteNotifications", C0338R.string.UnmuteNotifications));
            } else {
                this.muteItem.setText(LocaleController.getString("MuteNotifications", C0338R.string.MuteNotifications));
            }
        }
    }

    private void updateVisibleRows() {
        if (this.chatListView != null) {
            int childCount = this.chatListView.getChildCount();
            MessageObject editingMessageObject = this.chatActivityEnterView != null ? this.chatActivityEnterView.getEditingMessageObject() : null;
            for (int i = attach_photo; i < childCount; i += attach_gallery) {
                View childAt = this.chatListView.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    Object obj;
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    Object obj2 = null;
                    if (this.actionBar.isActionModeShowed()) {
                        MessageObject messageObject = chatMessageCell.getMessageObject();
                        if (messageObject != editingMessageObject) {
                            if (!this.selectedMessagesIds[messageObject.getDialogId() == this.dialog_id ? attach_photo : attach_gallery].containsKey(Integer.valueOf(messageObject.getId()))) {
                                childAt.setBackgroundColor(attach_photo);
                                obj = attach_photo;
                                obj2 = attach_gallery;
                            }
                        }
                        childAt.setBackgroundColor(Theme.MSG_SELECTED_BACKGROUND_COLOR);
                        obj = attach_gallery;
                        obj2 = attach_gallery;
                    } else {
                        childAt.setBackgroundColor(attach_photo);
                        obj = attach_photo;
                    }
                    chatMessageCell.setDialogSettings(this.dialogSettings);
                    chatMessageCell.setMessageObject(chatMessageCell.getMessageObject());
                    boolean z = obj2 == null;
                    boolean z2 = (obj2 == null || obj == null) ? false : true;
                    chatMessageCell.setCheckPressed(z, z2);
                    z2 = (this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID || chatMessageCell.getMessageObject() == null || chatMessageCell.getMessageObject().getId() != this.highlightMessageId) ? false : true;
                    chatMessageCell.setHighlighted(z2);
                    if (this.searchContainer == null || this.searchContainer.getVisibility() != 0 || MessagesSearchQuery.getLastSearchQuery() == null) {
                        chatMessageCell.setHighlightedText(null);
                    } else {
                        chatMessageCell.setHighlightedText(MessagesSearchQuery.getLastSearchQuery());
                    }
                } else if (childAt instanceof ChatActionCell) {
                    ChatActionCell chatActionCell = (ChatActionCell) childAt;
                    chatActionCell.setMessageObject(chatActionCell.getMessageObject());
                }
            }
        }
    }

    public boolean allowCaption() {
        return true;
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public View createView(Context context) {
        int i;
        View imageView;
        boolean z;
        if (this.chatMessageCellsCache.isEmpty()) {
            for (i = attach_photo; i < 8; i += attach_gallery) {
                this.chatMessageCellsCache.add(new ChatMessageCell(context));
            }
        }
        for (i = attach_gallery; i >= 0; i--) {
            this.selectedMessagesIds[i].clear();
            this.selectedMessagesCanCopyIds[i].clear();
        }
        this.cantDeleteMessagesCount = attach_photo;
        this.hasOwnBackground = true;
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.onDestroy();
            this.chatAttachAlert = null;
        }
        Theme.loadRecources(context);
        Theme.loadChatResources(context);
        this.actionBar.setAddToContainer(false);
        if (ThemeUtil.m2490b()) {
            Drawable backDrawable = new BackDrawable(false);
            ((BackDrawable) backDrawable).setColor(AdvanceTheme.bi);
            this.actionBar.setBackButtonDrawable(backDrawable);
        } else {
            this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C12788());
        this.avatarContainer = new ChatAvatarContainer(context, this, this.currentEncryptedChat != null);
        if (!ThemeUtil.m2490b()) {
            this.avatarContainer.setBackgroundResource(ThemeUtil.m2485a().m2294h());
        }
        if (this.currentUser == null || this.currentUser.id != UserConfig.getClientUserId()) {
            this.actionBar.addView(this.avatarContainer, attach_gallery, LayoutHelper.createFrame(-2, Face.UNCOMPUTED_PROBABILITY, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        } else {
            this.actionBar.addView(this.avatarContainer, attach_gallery, LayoutHelper.createFrame(-2, Face.UNCOMPUTED_PROBABILITY, 51, 56.0f, 0.0f, (float) (MoboConstants.aI ? 144 : 88), 0.0f));
        }
        if (!(this.currentChat == null || ChatObject.isChannel(this.currentChat))) {
            i = this.currentChat.participants_count;
            if (this.info != null) {
                i = this.info.participants.participants.size();
            }
            if (i == 0 || this.currentChat.deactivated || this.currentChat.left || (this.currentChat instanceof TL_chatForbidden) || (this.info != null && (this.info.participants instanceof TL_chatParticipantsForbidden))) {
                this.avatarContainer.setEnabled(false);
            }
        }
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.currentEncryptedChat == null && !this.isBroadcast) {
            this.searchItem = createMenu.addItem((int) attach_photo, (int) C0338R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C12809());
            this.searchItem.getSearchField().setHint(LocaleController.getString("Search", C0338R.string.Search));
            this.searchItem.setVisibility(8);
        }
        if (this.currentUser != null && this.currentUser.id == UserConfig.getClientUserId() && MoboConstants.aF) {
            if (ThemeUtil.m2490b()) {
                backDrawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_media);
                backDrawable.setColorFilter(AdvanceTheme.bi, Mode.MULTIPLY);
                this.mediaItem = createMenu.addItem((int) chat_menu_media, backDrawable);
                if (MoboConstants.aI) {
                    backDrawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_archive);
                    backDrawable.setColorFilter(AdvanceTheme.bi, Mode.MULTIPLY);
                    this.archiveItem = createMenu.addItem((int) chat_menu_archive, backDrawable);
                }
            } else {
                this.mediaItem = createMenu.addItem((int) chat_menu_media, (int) C0338R.drawable.ic_ab_media);
                if (MoboConstants.aI) {
                    this.archiveItem = createMenu.addItem((int) chat_menu_archive, (int) C0338R.drawable.ic_ab_archive);
                }
            }
        }
        if (!(this.currentUser == null || this.currentUser.id != UserConfig.getClientUserId() || (MoboConstants.aF && MoboConstants.aI))) {
            if (ThemeUtil.m2490b()) {
                backDrawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_settings);
                backDrawable.setColorFilter(AdvanceTheme.bi, Mode.MULTIPLY);
                this.archiveSettingItem = createMenu.addItem((int) chat_menu_archive_setting, backDrawable);
            } else {
                this.archiveSettingItem = createMenu.addItem((int) chat_menu_archive_setting, (int) C0338R.drawable.ic_ab_settings);
            }
        }
        if (ThemeUtil.m2490b()) {
            backDrawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_other);
            backDrawable.setColorFilter(AdvanceTheme.bi, Mode.MULTIPLY);
            this.headerItem = createMenu.addItem((int) attach_photo, backDrawable);
        } else {
            this.headerItem = createMenu.addItem((int) attach_photo, (int) C0338R.drawable.ic_ab_other);
        }
        if (this.searchItem != null) {
            this.headerItem.addSubItem(search, LocaleController.getString("Search", C0338R.string.Search), attach_photo);
        }
        if (ChatObject.isChannel(this.currentChat) && !this.currentChat.creator && (!this.currentChat.megagroup || (this.currentChat.username != null && this.currentChat.username.length() > 0))) {
            this.headerItem.addSubItem(report, LocaleController.getString("ReportChat", C0338R.string.ReportChat), attach_photo);
        }
        if (this.currentUser != null) {
            this.addContactItem = this.headerItem.addSubItem(share_contact, TtmlNode.ANONYMOUS_REGION_ID, attach_photo);
        }
        if (this.currentEncryptedChat != null) {
            this.timeItem2 = this.headerItem.addSubItem(chat_enc_timer, LocaleController.getString("SetTimer", C0338R.string.SetTimer), attach_photo);
        }
        if (!ChatObject.isChannel(this.currentChat) && this.archive == null) {
            this.headerItem.addSubItem(clear_history, LocaleController.getString("ClearHistory", C0338R.string.ClearHistory), attach_photo);
            if (this.currentChat == null || this.isBroadcast) {
                this.headerItem.addSubItem(delete_chat, LocaleController.getString("DeleteChatUser", C0338R.string.DeleteChatUser), attach_photo);
            } else {
                this.headerItem.addSubItem(delete_chat, LocaleController.getString("DeleteAndExit", C0338R.string.DeleteAndExit), attach_photo);
                if (!ChatObject.isNotInChat(this.currentChat)) {
                    this.headerItem.addSubItem(leave_without_delete, LocaleController.getString("LeaveWithoutDelete", C0338R.string.LeaveWithoutDelete), attach_photo);
                }
            }
        }
        if (this.currentUser == null || !this.currentUser.self) {
            this.muteItem = this.headerItem.addSubItem(mute, null, attach_photo);
        }
        if (!(this.archive == null || this.muteItem == null)) {
            this.muteItem.setVisibility(8);
        }
        if (this.currentUser != null && this.currentEncryptedChat == null && this.currentUser.bot) {
            this.headerItem.addSubItem(bot_settings, LocaleController.getString("BotSettings", C0338R.string.BotSettings), attach_photo);
            this.headerItem.addSubItem(bot_help, LocaleController.getString("BotHelp", C0338R.string.BotHelp), attach_photo);
            updateBotButtons();
        }
        if (this.currentChat != null && (this.currentChat.creator || (this.currentChat.megagroup && this.currentChat.editor))) {
            this.headerItem.addSubItem(delete_multi_message, LocaleController.getString("DeleteMultiMessage", C0338R.string.DeleteMultiMessage), C0338R.drawable.menu_item_delete);
        }
        if (this.archive == null) {
            this.headerItem.addSubItem(jump_to_message, LocaleController.getString("GoToFirstMessage", C0338R.string.GoToFirstMessage), C0338R.drawable.menu_item_up);
            this.headerItem.addSubItem(go_to_marker, LocaleController.getString("GoToChatMarker", C0338R.string.GoToChatMarker), C0338R.drawable.menu_item_marker);
            this.headerItem.addSubItem(chat_settings, LocaleController.getString("ChatSettings", C0338R.string.ChatSettings), C0338R.drawable.menu_item_settings);
        } else if (this.archive.m204a().longValue() > 0) {
            this.headerItem.addSubItem(add_message_to_archive, LocaleController.getString("AddMessageToCategory", C0338R.string.AddMessageToCategory), C0338R.drawable.menu_item_plus);
            this.headerItem.addSubItem(remove_message_from_archive, LocaleController.getString("RemoveMessageFromCategory", C0338R.string.RemoveMessageFromCategory), C0338R.drawable.menu_item_minus);
        }
        if (MoboConstants.aF && this.archive == null && (this.currentUser == null || this.currentUser.id != UserConfig.getClientUserId())) {
            this.headerItem.addSubItem(show_favorite_messages, LocaleController.getString("ShowFavoriteMessages", C0338R.string.ShowFavoriteMessages), C0338R.drawable.menu_item_archive);
        }
        updateTitle();
        this.avatarContainer.updateOnlineCount();
        this.avatarContainer.updateSubtitle();
        updateTitleIcons();
        this.attachItem = createMenu.addItem((int) chat_menu_attach, (int) C0338R.drawable.ic_ab_other).setOverrideMenuClick(true).setAllowCloseAnimation(false);
        this.attachItem.setVisibility(8);
        if (ThemeUtil.m2490b()) {
            backDrawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_attach_white);
            backDrawable.setColorFilter(AdvanceTheme.m2286c(AdvanceTheme.bG, -5395027), Mode.MULTIPLY);
            this.menuItem = createMenu.addItem((int) chat_menu_attach, backDrawable).setAllowCloseAnimation(false);
            if (MoboConstants.ae) {
                backDrawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_drawing_white);
                backDrawable.setColorFilter(AdvanceTheme.m2286c(AdvanceTheme.bG, -5395027), Mode.MULTIPLY);
                this.drawingItem = createMenu.addItem((int) chat_menu_drawing, backDrawable).setAllowCloseAnimation(false);
            }
        } else {
            this.menuItem = createMenu.addItem((int) chat_menu_attach, (int) C0338R.drawable.ic_ab_attach).setAllowCloseAnimation(false);
            if (MoboConstants.ae) {
                this.drawingItem = createMenu.addItem((int) chat_menu_drawing, (int) C0338R.drawable.ic_ab_drawing).setAllowCloseAnimation(false);
                this.drawingItem.setBackgroundDrawable(null);
            }
        }
        this.menuItem.setBackgroundDrawable(null);
        this.actionModeViews.clear();
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(createActionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(mute);
        this.selectedMessagesCountTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.selectedMessagesCountTextView.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        this.selectedMessagesCountTextView.setMinimumWidth(AndroidUtilities.dp(22.0f));
        createActionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear((int) attach_photo, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 65, (int) attach_photo, (int) attach_photo, (int) attach_photo));
        this.selectedMessagesCountTextView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.actionModeTitleContainer = new AnonymousClass11(context);
        createActionMode.addView(this.actionModeTitleContainer, LayoutHelper.createLinear((int) attach_photo, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 65, (int) attach_photo, (int) attach_photo, (int) attach_photo));
        this.actionModeTitleContainer.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.actionModeTitleContainer.setVisibility(8);
        this.actionModeTextView = new SimpleTextView(context);
        this.actionModeTextView.setTextSize(mute);
        this.actionModeTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.actionModeTextView.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        this.actionModeTextView.setText(LocaleController.getString("Edit", C0338R.string.Edit));
        this.actionModeTitleContainer.addView(this.actionModeTextView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.actionModeViews.add(createActionMode.addItem(select_all, C0338R.drawable.ic_ab_select_all, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.actionModeViews.add(createActionMode.addItem(add_to_download_list, C0338R.drawable.ic_ab_download, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.actionModeSubTextView = new SimpleTextView(context);
        this.actionModeSubTextView.setGravity(attach_audio);
        this.actionModeSubTextView.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        this.actionModeTitleContainer.addView(this.actionModeSubTextView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        if (this.currentEncryptedChat == null) {
            if (!this.isBroadcast) {
                this.actionModeViews.add(createActionMode.addItem(reply, C0338R.drawable.ic_ab_reply, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            }
            this.actionModeViews.add(createActionMode.addItem(copy, C0338R.drawable.ic_ab_fwd_copy, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(createActionMode.addItem(forward, C0338R.drawable.ic_ab_fwd_quoteforward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(createActionMode.addItem(forward_noname, C0338R.drawable.ic_ab_fwd_forward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(createActionMode.addItem(delete, C0338R.drawable.ic_ab_fwd_delete, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            ArrayList arrayList = this.actionModeViews;
            ActionBarMenuItem addItem = createActionMode.addItem(edit_done, C0338R.drawable.check_blue, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f));
            this.editDoneItem = addItem;
            arrayList.add(addItem);
            this.editDoneItem.setVisibility(8);
            this.editDoneItemProgress = new ContextProgressView(context, attach_photo);
            this.editDoneItem.addView(this.editDoneItemProgress, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
            this.editDoneItemProgress.setVisibility(attach_document);
        } else {
            this.actionModeViews.add(createActionMode.addItem(reply, C0338R.drawable.ic_ab_reply, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(createActionMode.addItem(copy, C0338R.drawable.ic_ab_fwd_copy, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(createActionMode.addItem(delete, C0338R.drawable.ic_ab_fwd_delete, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        }
        createActionMode.getItem(copy).setVisibility(this.selectedMessagesCanCopyIds[attach_photo].size() + this.selectedMessagesCanCopyIds[attach_gallery].size() != 0 ? attach_photo : 8);
        createActionMode.getItem(delete).setVisibility(this.cantDeleteMessagesCount == 0 ? attach_photo : 8);
        checkActionBarMenu();
        this.fragmentView = new AnonymousClass13(context);
        SizeNotifierFrameLayout sizeNotifierFrameLayout = (SizeNotifierFrameLayout) this.fragmentView;
        if (ThemeUtil.m2490b()) {
            updateBackground(sizeNotifierFrameLayout);
        } else {
            sizeNotifierFrameLayout.setBackgroundImage(ApplicationLoader.getCachedWallpaper());
        }
        this.emptyViewContainer = new FrameLayout(context);
        this.emptyViewContainer.setVisibility(attach_document);
        sizeNotifierFrameLayout.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, share_contact));
        this.emptyViewContainer.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        i = AdvanceTheme.m2286c(AdvanceTheme.bV, -1);
        int c = AdvanceTheme.m2286c(AdvanceTheme.bn, 1493172224);
        if (this.currentEncryptedChat != null) {
            this.bigEmptyView = new ChatBigEmptyView(context, true);
            ChatBigEmptyView chatBigEmptyView;
            Object[] objArr;
            if (this.currentEncryptedChat.admin_id == UserConfig.getClientUserId()) {
                chatBigEmptyView = this.bigEmptyView;
                objArr = new Object[attach_gallery];
                objArr[attach_photo] = UserObject.getFirstName(this.currentUser);
                chatBigEmptyView.setSecretText(LocaleController.formatString("EncryptedPlaceholderTitleOutgoing", C0338R.string.EncryptedPlaceholderTitleOutgoing, objArr));
            } else {
                chatBigEmptyView = this.bigEmptyView;
                objArr = new Object[attach_gallery];
                objArr[attach_photo] = UserObject.getFirstName(this.currentUser);
                chatBigEmptyView.setSecretText(LocaleController.formatString("EncryptedPlaceholderTitleIncoming", C0338R.string.EncryptedPlaceholderTitleIncoming, objArr));
            }
            this.emptyViewContainer.addView(this.bigEmptyView, new LayoutParams(-2, -2, share_contact));
        } else if (this.currentUser == null || !this.currentUser.self) {
            View textView = new TextView(context);
            textView.setTypeface(FontUtil.m1176a().m1161d());
            if (this.currentUser == null || this.currentUser.id == 777000 || this.currentUser.id == 429000 || !(this.currentUser.id / id_chat_compose_panel == 333 || this.currentUser.id % id_chat_compose_panel == 0)) {
                textView.setText(LocaleController.getString("NoMessages", C0338R.string.NoMessages));
            } else {
                textView.setText(LocaleController.getString("GotAQuestion", C0338R.string.GotAQuestion));
            }
            textView.setTextSize(attach_gallery, 14.0f);
            textView.setGravity(share_contact);
            if (!ThemeUtil.m2490b()) {
                i = -1;
            }
            textView.setTextColor(i);
            textView.setBackgroundResource(C0338R.drawable.system);
            if (ThemeUtil.m2490b()) {
                textView.getBackground().setColorFilter(c, Mode.SRC_IN);
            } else {
                textView.getBackground().setColorFilter(Theme.colorFilter);
            }
            textView.setTypeface(FontUtil.m1176a().m1160c());
            textView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(3.0f));
            this.emptyViewContainer.addView(textView, new LayoutParams(-2, -2, share_contact));
        } else {
            this.bigEmptyView = new ChatBigEmptyView(context, false);
            this.emptyViewContainer.addView(this.bigEmptyView, new LayoutParams(-2, -2, share_contact));
        }
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onDestroy();
        }
        if (this.mentionsAdapter != null) {
            this.mentionsAdapter.onDestroy();
        }
        this.chatListView = new AnonymousClass15(context);
        i = AdvanceTheme.f2508s;
        this.chatListView.setTag(Integer.valueOf(attach_gallery));
        this.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = this.chatListView;
        Adapter chatActivityAdapter = new ChatActivityAdapter(context);
        this.chatAdapter = chatActivityAdapter;
        recyclerListView.setAdapter(chatActivityAdapter);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(attach_photo, AndroidUtilities.dp(4.0f), attach_photo, AndroidUtilities.dp(3.0f));
        this.chatListView.setItemAnimator(null);
        this.chatListView.setLayoutAnimation(null);
        this.chatLayoutManager = new AnonymousClass16(context);
        this.chatLayoutManager.setOrientation(attach_gallery);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        sizeNotifierFrameLayout.addView(this.chatListView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.chatListView.setOnItemLongClickListener(this.onItemLongClickListener);
        this.chatListView.setOnItemClickListener(this.onItemClickListener);
        this.chatListView.setOnScrollListener(new AnonymousClass17(i));
        this.chatListView.setOnTouchListener(new OnTouchListener() {

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.18.1 */
            class C12431 implements Runnable {
                C12431() {
                }

                public void run() {
                    ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                }
            }

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.18.2 */
            class C12442 implements Runnable {
                C12442() {
                }

                public void run() {
                    ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                    ChatActivity.this.chatListView.setLongClickable(true);
                }
            }

            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (ChatActivity.this.openSecretPhotoRunnable != null || SecretPhotoViewer.getInstance().isVisible()) {
                    if (motionEvent.getAction() == ChatActivity.attach_gallery || motionEvent.getAction() == ChatActivity.attach_audio || motionEvent.getAction() == ChatActivity.attach_location) {
                        AndroidUtilities.runOnUIThread(new C12431(), 150);
                        if (ChatActivity.this.openSecretPhotoRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
                            ChatActivity.this.openSecretPhotoRunnable = null;
                            try {
                                Toast.makeText(view.getContext(), LocaleController.getString("PhotoTip", C0338R.string.PhotoTip), ChatActivity.attach_photo).show();
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                            }
                        } else if (SecretPhotoViewer.getInstance().isVisible()) {
                            AndroidUtilities.runOnUIThread(new C12442());
                            SecretPhotoViewer.getInstance().closePhoto();
                        }
                    } else if (motionEvent.getAction() != 0) {
                        if (SecretPhotoViewer.getInstance().isVisible()) {
                            return true;
                        }
                        if (ChatActivity.this.openSecretPhotoRunnable != null) {
                            if (motionEvent.getAction() != ChatActivity.attach_video) {
                                AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
                                ChatActivity.this.openSecretPhotoRunnable = null;
                            } else if (Math.hypot((double) (ChatActivity.this.startX - motionEvent.getX()), (double) (ChatActivity.this.startY - motionEvent.getY())) > ((double) AndroidUtilities.dp(5.0f))) {
                                AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.openSecretPhotoRunnable);
                                ChatActivity.this.openSecretPhotoRunnable = null;
                            }
                            ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                            ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                            ChatActivity.this.chatListView.setLongClickable(true);
                        }
                    }
                }
                return false;
            }
        });
        this.chatListView.setOnInterceptTouchListener(new OnInterceptTouchListener() {

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.19.1 */
            class C12451 implements Runnable {
                final /* synthetic */ ChatMessageCell val$cell;
                final /* synthetic */ MessageObject val$messageObject;

                C12451(MessageObject messageObject, ChatMessageCell chatMessageCell) {
                    this.val$messageObject = messageObject;
                    this.val$cell = chatMessageCell;
                }

                public void run() {
                    if (ChatActivity.this.openSecretPhotoRunnable != null) {
                        ChatActivity.this.chatListView.requestDisallowInterceptTouchEvent(true);
                        ChatActivity.this.chatListView.setOnItemLongClickListener(null);
                        ChatActivity.this.chatListView.setLongClickable(false);
                        ChatActivity.this.openSecretPhotoRunnable = null;
                        if (ChatActivity.this.sendSecretMessageRead(this.val$messageObject)) {
                            this.val$cell.invalidate();
                        }
                        SecretPhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                        SecretPhotoViewer.getInstance().openPhoto(this.val$messageObject);
                    }
                }
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (ChatActivity.this.chatActivityEnterView != null && ChatActivity.this.chatActivityEnterView.isEditingMessage()) {
                    return true;
                }
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    return false;
                }
                if (motionEvent.getAction() == 0) {
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY();
                    int childCount = ChatActivity.this.chatListView.getChildCount();
                    int i = ChatActivity.attach_photo;
                    while (i < childCount) {
                        View childAt = ChatActivity.this.chatListView.getChildAt(i);
                        int top = childAt.getTop();
                        int bottom = childAt.getBottom();
                        if (top > y || bottom < y) {
                            i += ChatActivity.attach_gallery;
                        } else if (childAt instanceof ChatMessageCell) {
                            ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                            MessageObject messageObject = chatMessageCell.getMessageObject();
                            if (messageObject != null && !messageObject.isSending() && messageObject.isSecretPhoto() && chatMessageCell.getPhotoImage().isInsideImage((float) x, (float) (y - top)) && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                                ChatActivity.this.startX = (float) x;
                                ChatActivity.this.startY = (float) y;
                                ChatActivity.this.chatListView.setOnItemClickListener(null);
                                ChatActivity.this.openSecretPhotoRunnable = new C12451(messageObject, chatMessageCell);
                                AndroidUtilities.runOnUIThread(ChatActivity.this.openSecretPhotoRunnable, 100);
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
        this.progressView = new FrameLayout(context);
        this.progressView.setVisibility(attach_document);
        sizeNotifierFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        View view = new View(context);
        view.setBackgroundResource(C0338R.drawable.system_loader);
        view.getBackground().setColorFilter(Theme.colorFilter);
        this.progressView.addView(view, LayoutHelper.createFrame(36, 36, share_contact));
        view = new ProgressBar(context);
        try {
            view.setIndeterminateDrawable(context.getResources().getDrawable(C0338R.drawable.loading_animation));
        } catch (Exception e) {
        }
        view.setIndeterminate(true);
        AndroidUtilities.setProgressBarAnimationDuration(view, ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED);
        this.progressView.addView(view, LayoutHelper.createFrame(32, 32, share_contact));
        if (ChatObject.isChannel(this.currentChat)) {
            int i2 = AdvanceTheme.bh;
            int i3 = AdvanceTheme.bT;
            this.pinnedMessageView = new FrameLayout(context);
            this.pinnedMessageView.setTag(Integer.valueOf(attach_gallery));
            this.pinnedMessageView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
            this.pinnedMessageView.setVisibility(8);
            if (ThemeUtil.m2490b()) {
                this.pinnedMessageView.setBackgroundColor(i);
            } else {
                this.pinnedMessageView.setBackgroundResource(C0338R.drawable.blockpanel);
            }
            sizeNotifierFrameLayout.addView(this.pinnedMessageView, LayoutHelper.createFrame(-1, 50, 51));
            this.pinnedMessageView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ChatActivity.this.scrollToMessageId(ChatActivity.this.info.pinned_msg_id, ChatActivity.attach_photo, true, ChatActivity.attach_photo);
                }
            });
            View view2 = new View(context);
            view2.setBackgroundColor(ThemeUtil.m2490b() ? i2 : -9658414);
            this.pinnedMessageView.addView(view2, LayoutHelper.createFrame(attach_video, 32.0f, 51, 8.0f, 8.0f, 0.0f, 0.0f));
            this.pinnedMessageImageView = new BackupImageView(context);
            this.pinnedMessageView.addView(this.pinnedMessageImageView, LayoutHelper.createFrame(32, 32.0f, 51, 17.0f, 8.0f, 0.0f, 0.0f));
            this.pinnedMessageNameTextView = new SimpleTextView(context);
            this.pinnedMessageNameTextView.setTextSize(chat_menu_attach);
            this.pinnedMessageNameTextView.setTextColor(ThemeUtil.m2490b() ? i2 : Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
            this.pinnedMessageNameTextView.setTypeface(FontUtil.m1176a().m1160c());
            this.pinnedMessageView.addView(this.pinnedMessageNameTextView, LayoutHelper.createFrame(-1, (float) AndroidUtilities.dp(18.0f), 51, 18.0f, 7.3f, 52.0f, 0.0f));
            this.pinnedMessageTextView = new SimpleTextView(context);
            this.pinnedMessageTextView.setTypeface(FontUtil.m1176a().m1161d());
            this.pinnedMessageTextView.setTextSize(chat_menu_attach);
            this.pinnedMessageTextView.setTextColor(ThemeUtil.m2490b() ? i3 : Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
            this.pinnedMessageView.addView(this.pinnedMessageTextView, LayoutHelper.createFrame(-1, (float) AndroidUtilities.dp(18.0f), 51, 18.0f, 25.3f, 52.0f, 0.0f));
            imageView = new ImageView(context);
            imageView.setImageResource(C0338R.drawable.miniplayer_close);
            if (ThemeUtil.m2490b()) {
                imageView.setColorFilter(i2, Mode.SRC_IN);
            }
            imageView.setScaleType(ScaleType.CENTER);
            this.pinnedMessageView.addView(imageView, LayoutHelper.createFrame(48, 48, 53));
            imageView.setOnClickListener(new OnClickListener() {

                /* renamed from: com.hanista.mobogram.ui.ChatActivity.21.1 */
                class C12471 implements DialogInterface.OnClickListener {
                    C12471() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        MessagesController.getInstance().pinChannelMessage(ChatActivity.this.currentChat, ChatActivity.attach_photo, false);
                    }
                }

                public void onClick(View view) {
                    if (ChatActivity.this.getParentActivity() != null) {
                        if (ChatActivity.this.currentChat.creator || ChatActivity.this.currentChat.editor) {
                            Builder builder = new Builder(ChatActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.getString("UnpinMessageAlert", C0338R.string.UnpinMessageAlert));
                            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C12471());
                            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                            ChatActivity.this.showDialog(builder.create());
                            return;
                        }
                        ApplicationLoader.applicationContext.getSharedPreferences("Notifications", ChatActivity.attach_photo).edit().putInt("pin_" + ChatActivity.this.dialog_id, ChatActivity.this.info.pinned_msg_id).commit();
                        ChatActivity.this.updatePinnedMessageView(true);
                    }
                }
            });
        }
        this.reportSpamView = new LinearLayout(context);
        this.reportSpamView.setTag(Integer.valueOf(attach_gallery));
        this.reportSpamView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
        this.reportSpamView.setVisibility(8);
        this.reportSpamView.setBackgroundResource(C0338R.drawable.blockpanel);
        sizeNotifierFrameLayout.addView(this.reportSpamView, LayoutHelper.createFrame(-1, 50, 51));
        this.addToContactsButton = new TextView(context);
        this.addToContactsButton.setTextColor(Theme.CHAT_ADD_CONTACT_TEXT_COLOR);
        this.addToContactsButton.setVisibility(8);
        this.addToContactsButton.setTextSize(attach_gallery, 14.0f);
        this.addToContactsButton.setTypeface(FontUtil.m1176a().m1160c());
        this.addToContactsButton.setSingleLine(true);
        this.addToContactsButton.setMaxLines(attach_gallery);
        this.addToContactsButton.setPadding(AndroidUtilities.dp(4.0f), attach_photo, AndroidUtilities.dp(4.0f), attach_photo);
        this.addToContactsButton.setGravity(share_contact);
        this.addToContactsButton.setText(LocaleController.getString("AddContactChat", C0338R.string.AddContactChat));
        this.reportSpamView.addView(this.addToContactsButton, LayoutHelper.createLinear(-1, -1, 0.5f, 51, attach_photo, attach_photo, attach_photo, AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
        this.addToContactsButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", ChatActivity.this.currentUser.id);
                bundle.putBoolean("addContact", true);
                ChatActivity.this.presentFragment(new ContactAddActivity(bundle));
            }
        });
        this.reportSpamContainer = new FrameLayout(context);
        this.reportSpamView.addView(this.reportSpamContainer, LayoutHelper.createLinear(-1, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 51, attach_photo, attach_photo, attach_photo, AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
        this.reportSpamButton = new TextView(context);
        this.reportSpamButton.setTextColor(Theme.CHAT_REPORT_SPAM_TEXT_COLOR);
        this.reportSpamButton.setTextSize(attach_gallery, 14.0f);
        this.reportSpamButton.setTypeface(FontUtil.m1176a().m1160c());
        this.reportSpamButton.setSingleLine(true);
        this.reportSpamButton.setMaxLines(attach_gallery);
        if (this.currentChat != null) {
            this.reportSpamButton.setText(LocaleController.getString("ReportSpamAndLeave", C0338R.string.ReportSpamAndLeave));
        } else {
            this.reportSpamButton.setText(LocaleController.getString("ReportSpam", C0338R.string.ReportSpam));
        }
        this.reportSpamButton.setGravity(share_contact);
        this.reportSpamButton.setPadding(AndroidUtilities.dp(50.0f), attach_photo, AndroidUtilities.dp(50.0f), attach_photo);
        this.reportSpamContainer.addView(this.reportSpamButton, LayoutHelper.createFrame(-1, -1, 51));
        this.reportSpamButton.setOnClickListener(new OnClickListener() {

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.23.1 */
            class C12481 implements DialogInterface.OnClickListener {
                C12481() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (ChatActivity.this.currentUser != null) {
                        MessagesController.getInstance().blockUser(ChatActivity.this.currentUser.id);
                    }
                    MessagesController.getInstance().reportSpam(ChatActivity.this.dialog_id, ChatActivity.this.currentUser, ChatActivity.this.currentChat);
                    ChatActivity.this.updateSpamView();
                    if (ChatActivity.this.currentChat == null) {
                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_photo);
                    } else if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                        MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_photo);
                    } else {
                        MessagesController.getInstance().deleteUserFromChat((int) (-ChatActivity.this.dialog_id), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                    }
                    ChatActivity.this.finishFragment();
                }
            }

            public void onClick(View view) {
                if (ChatActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(ChatActivity.this.getParentActivity());
                    if (ChatObject.isChannel(ChatActivity.this.currentChat) && !ChatActivity.this.currentChat.megagroup) {
                        builder.setMessage(LocaleController.getString("ReportSpamAlertChannel", C0338R.string.ReportSpamAlertChannel));
                    } else if (ChatActivity.this.currentChat != null) {
                        builder.setMessage(LocaleController.getString("ReportSpamAlertGroup", C0338R.string.ReportSpamAlertGroup));
                    } else {
                        builder.setMessage(LocaleController.getString("ReportSpamAlert", C0338R.string.ReportSpamAlert));
                    }
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C12481());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    ChatActivity.this.showDialog(builder.create());
                }
            }
        });
        imageView = new ImageView(context);
        imageView.setImageResource(C0338R.drawable.miniplayer_close);
        imageView.setScaleType(ScaleType.CENTER);
        this.reportSpamContainer.addView(imageView, LayoutHelper.createFrame(48, 48, 53));
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MessagesController.getInstance().hideReportSpam(ChatActivity.this.dialog_id, ChatActivity.this.currentUser, ChatActivity.this.currentChat);
                ChatActivity.this.updateSpamView();
            }
        });
        this.alertView = new FrameLayout(context);
        this.alertView.setTag(Integer.valueOf(attach_gallery));
        this.alertView.setTranslationY((float) (-AndroidUtilities.dp(50.0f)));
        this.alertView.setVisibility(8);
        this.alertView.setBackgroundResource(C0338R.drawable.blockpanel);
        sizeNotifierFrameLayout.addView(this.alertView, LayoutHelper.createFrame(-1, 50, 51));
        this.alertNameTextView = new TextView(context);
        this.alertNameTextView.setTextSize(attach_gallery, 14.0f);
        this.alertNameTextView.setTextColor(Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
        this.alertNameTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.alertNameTextView.setSingleLine(true);
        this.alertNameTextView.setEllipsize(TruncateAt.END);
        this.alertNameTextView.setMaxLines(attach_gallery);
        this.alertView.addView(this.alertNameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 8.0f, 5.0f, 8.0f, 0.0f));
        this.alertTextView = new TextView(context);
        this.alertTextView.setTextSize(attach_gallery, 14.0f);
        this.alertTextView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        this.alertTextView.setSingleLine(true);
        this.alertTextView.setEllipsize(TruncateAt.END);
        this.alertTextView.setMaxLines(attach_gallery);
        this.alertView.addView(this.alertTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 8.0f, 23.0f, 8.0f, 0.0f));
        if (!this.isBroadcast) {
            this.mentionContainer = new AnonymousClass25(context);
            this.mentionContainer.setBackgroundResource(C0338R.drawable.compose_panel);
            this.mentionContainer.setVisibility(8);
            this.mentionContainer.setWillNotDraw(false);
            sizeNotifierFrameLayout.addView(this.mentionContainer, LayoutHelper.createFrame(-1, 110, 83));
            this.mentionListView = new AnonymousClass26(context);
            this.mentionListView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return StickerPreviewViewer.getInstance().onTouch(motionEvent, ChatActivity.this.mentionListView, ChatActivity.attach_photo, ChatActivity.this.mentionsOnItemClickListener);
                }
            });
            this.mentionListView.setTag(Integer.valueOf(attach_video));
            this.mentionLayoutManager = new AnonymousClass28(context);
            this.mentionLayoutManager.setOrientation(attach_gallery);
            this.mentionGridLayoutManager = new AnonymousClass29(context, 100);
            this.mentionGridLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                public int getSpanSize(int i) {
                    if (ChatActivity.this.mentionsAdapter.getItem(i) instanceof TL_inlineBotSwitchPM) {
                        return 100;
                    }
                    if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() != null) {
                        i--;
                    }
                    return ChatActivity.this.mentionGridLayoutManager.getSpanSizeForItem(i);
                }
            });
            this.mentionListView.addItemDecoration(new ItemDecoration() {
                public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                    rect.left = ChatActivity.attach_photo;
                    rect.right = ChatActivity.attach_photo;
                    rect.top = ChatActivity.attach_photo;
                    rect.bottom = ChatActivity.attach_photo;
                    if (recyclerView.getLayoutManager() == ChatActivity.this.mentionGridLayoutManager) {
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                        if (ChatActivity.this.mentionsAdapter.getBotContextSwitch() == null) {
                            rect.top = AndroidUtilities.dp(2.0f);
                        } else if (childAdapterPosition != 0) {
                            childAdapterPosition--;
                            if (!ChatActivity.this.mentionGridLayoutManager.isFirstRow(childAdapterPosition)) {
                                rect.top = AndroidUtilities.dp(2.0f);
                            }
                        } else {
                            return;
                        }
                        rect.right = ChatActivity.this.mentionGridLayoutManager.isLastInRow(childAdapterPosition) ? ChatActivity.attach_photo : AndroidUtilities.dp(2.0f);
                    }
                }
            });
            this.mentionListView.setItemAnimator(null);
            this.mentionListView.setLayoutAnimation(null);
            this.mentionListView.setClipToPadding(false);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setOverScrollMode(attach_video);
            this.mentionContainer.addView(this.mentionListView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
            RecyclerListView recyclerListView2 = this.mentionListView;
            Adapter mentionsAdapter = new MentionsAdapter(context, false, this.dialog_id, new MentionsAdapterDelegate() {

                /* renamed from: com.hanista.mobogram.ui.ChatActivity.32.1 */
                class C12501 extends AnimatorListenerAdapterProxy {
                    C12501() {
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }
                }

                /* renamed from: com.hanista.mobogram.ui.ChatActivity.32.2 */
                class C12512 extends AnimatorListenerAdapterProxy {
                    C12512() {
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator)) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator)) {
                            ChatActivity.this.mentionContainer.setVisibility(8);
                            ChatActivity.this.mentionContainer.setTag(null);
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }
                }

                public void needChangePanelVisibility(boolean z) {
                    if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                        ChatActivity.this.mentionListView.setLayoutManager(ChatActivity.this.mentionGridLayoutManager);
                    } else {
                        ChatActivity.this.mentionListView.setLayoutManager(ChatActivity.this.mentionLayoutManager);
                    }
                    if (z) {
                        if (ChatActivity.this.mentionListAnimation != null) {
                            ChatActivity.this.mentionListAnimation.cancel();
                            ChatActivity.this.mentionListAnimation = null;
                        }
                        if (ChatActivity.this.mentionContainer.getVisibility() == 0) {
                            ChatActivity.this.mentionContainer.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            return;
                        }
                        if (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) {
                            ChatActivity.this.mentionGridLayoutManager.scrollToPositionWithOffset(ChatActivity.attach_photo, AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS);
                        } else {
                            ChatActivity.this.mentionLayoutManager.scrollToPositionWithOffset(ChatActivity.attach_photo, AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS);
                        }
                        if (ChatActivity.this.allowStickersPanel && (!ChatActivity.this.mentionsAdapter.isBotContext() || ChatActivity.this.allowContextBotPanel || ChatActivity.this.allowContextBotPanelSecond)) {
                            if (ChatActivity.this.currentEncryptedChat != null && ChatActivity.this.mentionsAdapter.isBotContext()) {
                                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", ChatActivity.attach_photo);
                                if (!sharedPreferences.getBoolean("secretbot", false)) {
                                    Builder builder = new Builder(ChatActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                                    builder.setMessage(LocaleController.getString("SecretChatContextBotAlert", C0338R.string.SecretChatContextBotAlert));
                                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                                    ChatActivity.this.showDialog(builder.create());
                                    sharedPreferences.edit().putBoolean("secretbot", true).commit();
                                }
                            }
                            ChatActivity.this.mentionContainer.setVisibility(ChatActivity.attach_photo);
                            ChatActivity.this.mentionContainer.setTag(null);
                            ChatActivity.this.mentionListAnimation = new AnimatorSet();
                            AnimatorSet access$10000 = ChatActivity.this.mentionListAnimation;
                            Animator[] animatorArr = new Animator[ChatActivity.attach_gallery];
                            animatorArr[ChatActivity.attach_photo] = ObjectAnimator.ofFloat(ChatActivity.this.mentionContainer, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                            access$10000.playTogether(animatorArr);
                            ChatActivity.this.mentionListAnimation.addListener(new C12501());
                            ChatActivity.this.mentionListAnimation.setDuration(200);
                            ChatActivity.this.mentionListAnimation.start();
                            return;
                        }
                        ChatActivity.this.mentionContainer.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        ChatActivity.this.mentionContainer.setVisibility(ChatActivity.attach_document);
                        return;
                    }
                    if (ChatActivity.this.mentionListAnimation != null) {
                        ChatActivity.this.mentionListAnimation.cancel();
                        ChatActivity.this.mentionListAnimation = null;
                    }
                    if (ChatActivity.this.mentionContainer.getVisibility() == 8) {
                        return;
                    }
                    if (ChatActivity.this.allowStickersPanel) {
                        ChatActivity.this.mentionListAnimation = new AnimatorSet();
                        access$10000 = ChatActivity.this.mentionListAnimation;
                        animatorArr = new Animator[ChatActivity.attach_gallery];
                        float[] fArr = new float[ChatActivity.attach_gallery];
                        fArr[ChatActivity.attach_photo] = 0.0f;
                        animatorArr[ChatActivity.attach_photo] = ObjectAnimator.ofFloat(ChatActivity.this.mentionContainer, "alpha", fArr);
                        access$10000.playTogether(animatorArr);
                        ChatActivity.this.mentionListAnimation.addListener(new C12512());
                        ChatActivity.this.mentionListAnimation.setDuration(200);
                        ChatActivity.this.mentionListAnimation.start();
                        return;
                    }
                    ChatActivity.this.mentionContainer.setTag(null);
                    ChatActivity.this.mentionContainer.setVisibility(8);
                }

                public void onContextClick(BotInlineResult botInlineResult) {
                    if (ChatActivity.this.getParentActivity() != null && botInlineResult.content_url != null) {
                        if (botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || botInlineResult.type.equals("web_player_video")) {
                            BottomSheet.Builder builder = new BottomSheet.Builder(ChatActivity.this.getParentActivity());
                            builder.setCustomView(new WebFrameLayout(ChatActivity.this.getParentActivity(), builder.create(), botInlineResult.title != null ? botInlineResult.title : TtmlNode.ANONYMOUS_REGION_ID, botInlineResult.description, botInlineResult.content_url, botInlineResult.content_url, botInlineResult.f2655w, botInlineResult.f2654h));
                            builder.setUseFullWidth(true);
                            ChatActivity.this.showDialog(builder.create());
                            return;
                        }
                        Browser.openUrl(ChatActivity.this.getParentActivity(), botInlineResult.content_url);
                    }
                }

                public void onContextSearch(boolean z) {
                    if (ChatActivity.this.chatActivityEnterView != null) {
                        ChatActivity.this.chatActivityEnterView.setCaption(ChatActivity.this.mentionsAdapter.getBotCaption());
                        ChatActivity.this.chatActivityEnterView.showContextProgress(z);
                    }
                }
            });
            this.mentionsAdapter = mentionsAdapter;
            recyclerListView2.setAdapter(mentionsAdapter);
            if (!ChatObject.isChannel(this.currentChat) || (this.currentChat != null && this.currentChat.megagroup)) {
                this.mentionsAdapter.setBotInfo(this.botInfo);
            }
            this.mentionsAdapter.setParentFragment(this);
            this.mentionsAdapter.setChatInfo(this.info);
            this.mentionsAdapter.setNeedUsernames(this.currentChat != null);
            MentionsAdapter mentionsAdapter2 = this.mentionsAdapter;
            z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
            mentionsAdapter2.setNeedBotContext(z);
            this.mentionsAdapter.setBotsCount(this.currentChat != null ? this.botsCount : attach_gallery);
            recyclerListView2 = this.mentionListView;
            OnItemClickListener anonymousClass33 = new OnItemClickListener() {
                public void onItemClick(View view, int i) {
                    Object item = ChatActivity.this.mentionsAdapter.getItem(i);
                    int resultStartPosition = ChatActivity.this.mentionsAdapter.getResultStartPosition();
                    int resultLength = ChatActivity.this.mentionsAdapter.getResultLength();
                    if (item instanceof User) {
                        User user = (User) item;
                        if (user == null) {
                            return;
                        }
                        if (user.username != null) {
                            ChatActivity.this.chatActivityEnterView.replaceWithText(resultStartPosition, resultLength, "@" + user.username + " ");
                            return;
                        }
                        String str = user.first_name;
                        if (str == null || str.length() == 0) {
                            str = user.last_name;
                        }
                        CharSequence spannableString = new SpannableString(str + " ");
                        spannableString.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + user.id), ChatActivity.attach_photo, spannableString.length(), 33);
                        ChatActivity.this.chatActivityEnterView.replaceWithText(resultStartPosition, resultLength, spannableString);
                    } else if (item instanceof String) {
                        if (ChatActivity.this.mentionsAdapter.isBotCommands()) {
                            SendMessagesHelper.getInstance().sendMessage((String) item, ChatActivity.this.dialog_id, null, null, false, null, null, null);
                            ChatActivity.this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                            return;
                        }
                        ChatActivity.this.chatActivityEnterView.replaceWithText(resultStartPosition, resultLength, item + " ");
                    } else if (item instanceof BotInlineResult) {
                        if (ChatActivity.this.chatActivityEnterView.getFieldText() != null) {
                            BotInlineResult botInlineResult = (BotInlineResult) item;
                            if (VERSION.SDK_INT < ChatActivity.delete_chat || ((!botInlineResult.type.equals("photo") || (botInlineResult.photo == null && botInlineResult.content_url == null)) && ((!botInlineResult.type.equals("gif") || (botInlineResult.document == null && botInlineResult.content_url == null)) && (!botInlineResult.type.equals(MimeTypes.BASE_TYPE_VIDEO) || botInlineResult.document == null)))) {
                                ChatActivity.this.sendBotInlineResult(botInlineResult);
                                return;
                            }
                            ArrayList access$002 = ChatActivity.this.botContextResults = new ArrayList(ChatActivity.this.mentionsAdapter.getSearchResultBotContext());
                            PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                            PhotoViewer.getInstance().openPhotoForSelect(access$002, ChatActivity.this.mentionsAdapter.getItemPosition(i), ChatActivity.attach_audio, ChatActivity.this.botContextProvider, null);
                        }
                    } else if (item instanceof TL_inlineBotSwitchPM) {
                        ChatActivity.this.processInlineBotContextPM((TL_inlineBotSwitchPM) item);
                    }
                }
            };
            this.mentionsOnItemClickListener = anonymousClass33;
            recyclerListView2.setOnItemClickListener(anonymousClass33);
            this.mentionListView.setOnItemLongClickListener(new OnItemLongClickListener() {

                /* renamed from: com.hanista.mobogram.ui.ChatActivity.34.1 */
                class C12521 implements DialogInterface.OnClickListener {
                    C12521() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChatActivity.this.mentionsAdapter.clearRecentHashtags();
                    }
                }

                public boolean onItemClick(View view, int i) {
                    boolean z = false;
                    if (ChatActivity.this.getParentActivity() == null || !ChatActivity.this.mentionsAdapter.isLongClickEnabled()) {
                        return false;
                    }
                    Object item = ChatActivity.this.mentionsAdapter.getItem(i);
                    if (!(item instanceof String)) {
                        return false;
                    }
                    if (!ChatActivity.this.mentionsAdapter.isBotCommands()) {
                        Builder builder = new Builder(ChatActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setMessage(LocaleController.getString("ClearSearch", C0338R.string.ClearSearch));
                        builder.setPositiveButton(LocaleController.getString("ClearButton", C0338R.string.ClearButton).toUpperCase(), new C12521());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        ChatActivity.this.showDialog(builder.create());
                        return true;
                    } else if (!URLSpanBotCommand.enabled) {
                        return false;
                    } else {
                        ChatActivity.this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                        ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                        String str = (String) item;
                        if (ChatActivity.this.currentChat != null && ChatActivity.this.currentChat.megagroup) {
                            z = true;
                        }
                        chatActivityEnterView.setCommand(null, str, true, z);
                        return true;
                    }
                }
            });
            this.mentionListView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    boolean z = true;
                    ChatActivity chatActivity = ChatActivity.this;
                    if (i != ChatActivity.attach_gallery) {
                        z = false;
                    }
                    chatActivity.mentionListViewIsScrolling = z;
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    int findLastVisibleItemPosition = (ChatActivity.this.mentionsAdapter.isBotContext() && ChatActivity.this.mentionsAdapter.isMediaLayout()) ? ChatActivity.this.mentionGridLayoutManager.findLastVisibleItemPosition() : ChatActivity.this.mentionLayoutManager.findLastVisibleItemPosition();
                    if ((findLastVisibleItemPosition == -1 ? ChatActivity.attach_photo : findLastVisibleItemPosition) > 0 && findLastVisibleItemPosition > ChatActivity.this.mentionsAdapter.getItemCount() - 5) {
                        ChatActivity.this.mentionsAdapter.searchForContextBotForNextOffset();
                    }
                    ChatActivity.this.mentionListViewUpdateLayout();
                }
            });
        }
        this.pagedownButton = new FrameLayout(context);
        this.pagedownButton.setVisibility(attach_document);
        sizeNotifierFrameLayout.addView(this.pagedownButton, LayoutHelper.createFrame(46, 59.0f, 85, 0.0f, 0.0f, 7.0f, 5.0f));
        this.pagedownButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ChatActivity.this.returnToMessageId > 0) {
                    ChatActivity.this.scrollToMessageId(ChatActivity.this.returnToMessageId, ChatActivity.attach_photo, true, ChatActivity.this.returnToLoadIndex);
                } else {
                    ChatActivity.this.scrollToLastMessage(true);
                }
            }
        });
        imageView = new ImageView(context);
        imageView.setImageResource(C0338R.drawable.pagedown);
        this.pagedownButton.addView(imageView, LayoutHelper.createFrame(46, 46, 83));
        this.pagedownButtonCounter = new TextView(context);
        this.pagedownButtonCounter.setVisibility(attach_document);
        this.pagedownButtonCounter.setTypeface(FontUtil.m1176a().m1160c());
        this.pagedownButtonCounter.setTextSize(attach_gallery, 13.0f);
        this.pagedownButtonCounter.setTextColor(-1);
        this.pagedownButtonCounter.setGravity(share_contact);
        this.pagedownButtonCounter.setBackgroundResource(C0338R.drawable.chat_badge);
        this.pagedownButtonCounter.setMinWidth(AndroidUtilities.dp(23.0f));
        this.pagedownButtonCounter.setPadding(AndroidUtilities.dp(8.0f), attach_photo, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.pagedownButton.addView(this.pagedownButtonCounter, LayoutHelper.createFrame(-2, 23, 49));
        this.chatActivityEnterView = new ChatActivityEnterView(getParentActivity(), sizeNotifierFrameLayout, this, true, MoboConstants.ae);
        this.chatActivityEnterView.setDialogId(this.dialog_id);
        this.chatActivityEnterView.addToAttachLayout(this.menuItem);
        if (MoboConstants.ae) {
            this.chatActivityEnterView.addToAttachLayout(this.drawingItem);
        }
        this.chatActivityEnterView.setId(id_chat_compose_panel);
        this.chatActivityEnterView.setBotsCount(this.botsCount, this.hasBotsCommands);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        z = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 23;
        boolean z2 = this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46;
        chatActivityEnterView.setAllowStickersAndGifs(z, z2);
        sizeNotifierFrameLayout.addView(this.chatActivityEnterView, sizeNotifierFrameLayout.getChildCount() - 1, LayoutHelper.createFrame(-1, -2, 83));
        this.chatActivityEnterView.setDelegate(new ChatActivityEnterViewDelegate() {

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.37.1 */
            class C12531 implements Runnable {
                final /* synthetic */ CharSequence val$text;

                C12531(CharSequence charSequence) {
                    this.val$text = charSequence;
                }

                public void run() {
                    if (this == ChatActivity.this.waitingForCharaterEnterRunnable) {
                        ChatActivity.this.searchLinks(this.val$text, false);
                        ChatActivity.this.waitingForCharaterEnterRunnable = null;
                    }
                }
            }

            public void needSendTyping() {
                MessagesController.getInstance().sendTyping(ChatActivity.this.dialog_id, ChatActivity.attach_photo, ChatActivity.this.classGuid);
            }

            public void onAttachButtonHidden() {
                if (!ChatActivity.this.actionBar.isSearchFieldVisible()) {
                    if (ChatActivity.this.attachItem != null) {
                        ChatActivity.this.attachItem.setVisibility(ChatActivity.attach_photo);
                    }
                    if (ChatActivity.this.archiveItem != null) {
                        ChatActivity.this.archiveItem.setVisibility(8);
                    }
                    if (ChatActivity.this.archiveSettingItem != null) {
                        ChatActivity.this.archiveSettingItem.setVisibility(8);
                    }
                    if (ChatActivity.this.mediaItem != null) {
                        ChatActivity.this.mediaItem.setVisibility(8);
                    }
                    if (ChatActivity.this.headerItem != null) {
                        ChatActivity.this.headerItem.setVisibility(8);
                    }
                }
            }

            public void onAttachButtonShow() {
                if (!ChatActivity.this.actionBar.isSearchFieldVisible()) {
                    if (ChatActivity.this.attachItem != null) {
                        ChatActivity.this.attachItem.setVisibility(8);
                    }
                    if (ChatActivity.this.archiveItem != null) {
                        ChatActivity.this.archiveItem.setVisibility(ChatActivity.attach_photo);
                    }
                    if (ChatActivity.this.archiveSettingItem != null) {
                        ChatActivity.this.archiveSettingItem.setVisibility(ChatActivity.attach_photo);
                    }
                    if (ChatActivity.this.mediaItem != null) {
                        ChatActivity.this.mediaItem.setVisibility(ChatActivity.attach_photo);
                    }
                    if (ChatActivity.this.headerItem != null) {
                        ChatActivity.this.headerItem.setVisibility(ChatActivity.attach_photo);
                    }
                }
            }

            public void onMessageEditEnd(boolean z) {
                if (z) {
                    ChatActivity.this.showEditDoneProgress(true, true);
                    return;
                }
                MentionsAdapter access$6800 = ChatActivity.this.mentionsAdapter;
                boolean z2 = ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 46;
                access$6800.setNeedBotContext(z2);
                ChatActivity.this.chatListView.setOnItemLongClickListener(ChatActivity.this.onItemLongClickListener);
                ChatActivity.this.chatListView.setOnItemClickListener(ChatActivity.this.onItemClickListener);
                ChatActivity.this.chatListView.setClickable(true);
                ChatActivity.this.chatListView.setLongClickable(true);
                ChatActivity.this.mentionsAdapter.setAllowNewMentions(true);
                ChatActivity.this.actionModeTitleContainer.setVisibility(8);
                ChatActivity.this.selectedMessagesCountTextView.setVisibility(ChatActivity.attach_photo);
                ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                z2 = ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 23;
                boolean z3 = ChatActivity.this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(ChatActivity.this.currentEncryptedChat.layer) >= 46;
                chatActivityEnterView.setAllowStickersAndGifs(z2, z3);
                if (ChatActivity.this.editingMessageObjectReqId != 0) {
                    ConnectionsManager.getInstance().cancelRequest(ChatActivity.this.editingMessageObjectReqId, true);
                    ChatActivity.this.editingMessageObjectReqId = ChatActivity.attach_photo;
                }
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
            }

            public void onMessageSend(CharSequence charSequence) {
                ChatActivity.this.moveScrollToLastMessage();
                ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                if (ChatActivity.this.mentionsAdapter != null) {
                    ChatActivity.this.mentionsAdapter.addHashtagsFromMessage(charSequence);
                }
            }

            public void onStickersTab(boolean z) {
                if (ChatActivity.this.emojiButtonRed != null) {
                    ChatActivity.this.emojiButtonRed.setVisibility(8);
                }
                ChatActivity.this.allowContextBotPanelSecond = !z;
                ChatActivity.this.checkContextBotPanel();
            }

            public void onTextChanged(CharSequence charSequence, boolean z) {
                MediaController a = MediaController.m71a();
                boolean z2 = !(charSequence == null || charSequence.length() == 0) || ChatActivity.this.chatActivityEnterView.isEditingMessage();
                a.m154a(z2);
                if (!(ChatActivity.this.stickersAdapter == null || ChatActivity.this.chatActivityEnterView.isEditingMessage())) {
                    ChatActivity.this.stickersAdapter.loadStikersForEmoji(charSequence);
                }
                if (ChatActivity.this.mentionsAdapter != null) {
                    ChatActivity.this.mentionsAdapter.searchUsernameOrHashtag(charSequence.toString(), ChatActivity.this.chatActivityEnterView.getCursorPosition(), ChatActivity.this.messages);
                }
                if (ChatActivity.this.waitingForCharaterEnterRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(ChatActivity.this.waitingForCharaterEnterRunnable);
                    ChatActivity.this.waitingForCharaterEnterRunnable = null;
                }
                if (!ChatActivity.this.chatActivityEnterView.isMessageWebPageSearchEnabled()) {
                    return;
                }
                if (!ChatActivity.this.chatActivityEnterView.isEditingMessage() || !ChatActivity.this.chatActivityEnterView.isEditingCaption()) {
                    if (z) {
                        ChatActivity.this.searchLinks(charSequence, true);
                        return;
                    }
                    ChatActivity.this.waitingForCharaterEnterRunnable = new C12531(charSequence);
                    AndroidUtilities.runOnUIThread(ChatActivity.this.waitingForCharaterEnterRunnable, AndroidUtilities.WEB_URL == null ? 3000 : 1000);
                }
            }

            public void onWindowSizeChanged(int i) {
                boolean z = true;
                if (i < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
                    ChatActivity.this.allowStickersPanel = false;
                    if (ChatActivity.this.stickersPanel.getVisibility() == 0) {
                        ChatActivity.this.stickersPanel.setVisibility(ChatActivity.attach_document);
                    }
                    if (ChatActivity.this.mentionContainer != null && ChatActivity.this.mentionContainer.getVisibility() == 0) {
                        ChatActivity.this.mentionContainer.setVisibility(ChatActivity.attach_document);
                    }
                } else {
                    ChatActivity.this.allowStickersPanel = true;
                    if (ChatActivity.this.stickersPanel.getVisibility() == ChatActivity.attach_document) {
                        ChatActivity.this.stickersPanel.setVisibility(ChatActivity.attach_photo);
                    }
                    if (ChatActivity.this.mentionContainer != null && ChatActivity.this.mentionContainer.getVisibility() == ChatActivity.attach_document && (!ChatActivity.this.mentionsAdapter.isBotContext() || ChatActivity.this.allowContextBotPanel || ChatActivity.this.allowContextBotPanelSecond)) {
                        ChatActivity.this.mentionContainer.setVisibility(ChatActivity.attach_photo);
                        ChatActivity.this.mentionContainer.setTag(null);
                    }
                }
                ChatActivity chatActivity = ChatActivity.this;
                if (ChatActivity.this.chatActivityEnterView.isPopupShowing()) {
                    z = false;
                }
                chatActivity.allowContextBotPanel = z;
                ChatActivity.this.checkContextBotPanel();
            }
        });
        View anonymousClass38 = new AnonymousClass38(context);
        anonymousClass38.setClickable(true);
        this.chatActivityEnterView.addTopView(anonymousClass38, 48);
        view = new View(context);
        i = AdvanceTheme.bH;
        if (ThemeUtil.m2490b()) {
            view.setBackgroundColor(i == -1 ? -1513240 : AdvanceTheme.m2283b(i, report));
        } else {
            view.setBackgroundColor(-1513240);
        }
        anonymousClass38.addView(view, LayoutHelper.createFrame(-1, attach_gallery, 83));
        this.replyIconImageView = new ImageView(context);
        this.replyIconImageView.setScaleType(ScaleType.CENTER);
        anonymousClass38.addView(this.replyIconImageView, LayoutHelper.createFrame(52, 46, 51));
        View imageView2 = new ImageView(context);
        imageView2.setImageResource(C0338R.drawable.delete_reply);
        imageView2.setScaleType(ScaleType.CENTER);
        if (ThemeUtil.m2490b()) {
            i = AdvanceTheme.f2491b;
            c = AdvanceTheme.bH;
            int i4 = AdvanceTheme.bG;
            if (c != -1) {
                i = -5395027;
            }
            imageView2.setColorFilter(AdvanceTheme.m2286c(i4, i), Mode.SRC_IN);
            anonymousClass38.setBackgroundColor(c);
        }
        anonymousClass38.addView(imageView2, LayoutHelper.createFrame(52, 46.0f, 53, 0.0f, 0.5f, 0.0f, 0.0f));
        imageView2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ChatActivity.this.forwardingMessages != null) {
                    ChatActivity.this.forwardingMessages.clear();
                }
                ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, true, true);
            }
        });
        this.replyNameTextView = new SimpleTextView(context);
        this.replyNameTextView.setTextSize(chat_menu_attach);
        this.replyNameTextView.setTextColor(Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
        this.replyNameTextView.setTypeface(FontUtil.m1176a().m1160c());
        anonymousClass38.addView(this.replyNameTextView, LayoutHelper.createFrame(-1, 18.0f, 51, 52.0f, 6.0f, 52.0f, 0.0f));
        this.replyObjectTextView = new SimpleTextView(context);
        this.replyObjectTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.replyObjectTextView.setTextSize(chat_menu_attach);
        this.replyObjectTextView.setTextColor(Theme.REPLY_PANEL_MESSAGE_TEXT_COLOR);
        anonymousClass38.addView(this.replyObjectTextView, LayoutHelper.createFrame(-1, 20.0f, 51, 52.0f, 24.0f, 52.0f, 0.0f));
        this.replyImageView = new BackupImageView(context);
        anonymousClass38.addView(this.replyImageView, LayoutHelper.createFrame(34, 34.0f, 51, 52.0f, 6.0f, 0.0f, 0.0f));
        this.stickersPanel = new FrameLayout(context);
        this.stickersPanel.setVisibility(8);
        sizeNotifierFrameLayout.addView(this.stickersPanel, LayoutHelper.createFrame(-2, 81.5f, 83, 0.0f, 0.0f, 0.0f, 38.0f));
        this.stickersListView = new AnonymousClass40(context);
        this.stickersListView.setTag(Integer.valueOf(attach_audio));
        this.stickersListView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return StickerPreviewViewer.getInstance().onTouch(motionEvent, ChatActivity.this.stickersListView, ChatActivity.attach_photo, ChatActivity.this.stickersOnItemClickListener);
            }
        });
        this.stickersListView.setDisallowInterceptTouchEvents(true);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(attach_photo);
        this.stickersListView.setLayoutManager(linearLayoutManager);
        this.stickersListView.setClipToPadding(false);
        this.stickersListView.setOverScrollMode(attach_video);
        this.stickersPanel.addView(this.stickersListView, LayoutHelper.createFrame(-1, 78.0f));
        initStickers();
        anonymousClass38 = new ImageView(context);
        anonymousClass38.setImageResource(C0338R.drawable.stickers_back_arrow);
        this.stickersPanel.addView(anonymousClass38, LayoutHelper.createFrame(-2, -2.0f, 83, 53.0f, 0.0f, 0.0f, 0.0f));
        this.searchContainer = new FrameLayout(context);
        this.searchContainer.setBackgroundResource(C0338R.drawable.compose_panel);
        this.searchContainer.setVisibility(attach_document);
        this.searchContainer.setFocusable(true);
        this.searchContainer.setFocusableInTouchMode(true);
        this.searchContainer.setClickable(true);
        this.searchContainer.setBackgroundResource(C0338R.drawable.compose_panel);
        this.searchContainer.setPadding(attach_photo, AndroidUtilities.dp(3.0f), attach_photo, attach_photo);
        sizeNotifierFrameLayout.addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
        this.searchUpButton = new ImageView(context);
        this.searchUpButton.setScaleType(ScaleType.CENTER);
        this.searchUpButton.setImageResource(C0338R.drawable.search_up);
        this.searchContainer.addView(this.searchUpButton, LayoutHelper.createFrame(48, 48.0f));
        this.searchUpButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MessagesSearchQuery.searchMessagesInChat(null, ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, ChatActivity.attach_gallery);
            }
        });
        this.searchDownButton = new ImageView(context);
        this.searchDownButton.setScaleType(ScaleType.CENTER);
        this.searchDownButton.setImageResource(C0338R.drawable.search_down);
        this.searchContainer.addView(this.searchDownButton, LayoutHelper.createFrame(48, 48.0f, 51, 48.0f, 0.0f, 0.0f, 0.0f));
        this.searchDownButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MessagesSearchQuery.searchMessagesInChat(null, ChatActivity.this.dialog_id, ChatActivity.this.mergeDialogId, ChatActivity.this.classGuid, ChatActivity.attach_video);
            }
        });
        this.searchCountText = new SimpleTextView(context);
        this.searchCountText.setTextColor(Theme.MSG_IN_VENUE_NAME_TEXT_COLOR);
        this.searchCountText.setTextSize(clear_history);
        this.searchCountText.setTypeface(FontUtil.m1176a().m1160c());
        this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-1, -2.0f, reply, 108.0f, 0.0f, 0.0f, 0.0f));
        this.bottomOverlay = new FrameLayout(context);
        this.bottomOverlay.setVisibility(attach_document);
        this.bottomOverlay.setFocusable(true);
        this.bottomOverlay.setFocusableInTouchMode(true);
        this.bottomOverlay.setClickable(true);
        this.bottomOverlay.setBackgroundResource(C0338R.drawable.compose_panel);
        this.bottomOverlay.setPadding(attach_photo, AndroidUtilities.dp(3.0f), attach_photo, attach_photo);
        sizeNotifierFrameLayout.addView(this.bottomOverlay, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayText = new TextView(context);
        this.bottomOverlayText.setTypeface(FontUtil.m1176a().m1161d());
        this.bottomOverlayText.setTextSize(attach_gallery, 16.0f);
        this.bottomOverlayText.setTextColor(Theme.CHAT_BOTTOM_OVERLAY_TEXT_COLOR);
        this.bottomOverlay.addView(this.bottomOverlayText, LayoutHelper.createFrame(-2, -2, share_contact));
        this.bottomOverlayChat = new FrameLayout(context);
        this.bottomOverlayChat.setBackgroundResource(C0338R.drawable.compose_panel);
        this.bottomOverlayChat.setPadding(attach_photo, AndroidUtilities.dp(3.0f), attach_photo, attach_photo);
        this.bottomOverlayChat.setVisibility(attach_document);
        sizeNotifierFrameLayout.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new OnClickListener() {

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.44.1 */
            class C12551 implements DialogInterface.OnClickListener {
                C12551() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    MessagesController.getInstance().unblockUser(ChatActivity.this.currentUser.id);
                }
            }

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.44.2 */
            class C12562 implements DialogInterface.OnClickListener {
                C12562() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    MessagesController.getInstance().addUserToChat(ChatActivity.this.currentChat.id, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), ChatActivity.this.info, ChatActivity.attach_photo, null, ChatActivity.this);
                    ChatActivity.this.chatActivityEnterView.setVisibility(ChatActivity.attach_photo);
                    ChatActivity.this.bottomOverlayChat.setVisibility(ChatActivity.attach_document);
                }
            }

            /* renamed from: com.hanista.mobogram.ui.ChatActivity.44.3 */
            class C12573 implements DialogInterface.OnClickListener {
                C12573() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    MessagesController.getInstance().deleteDialog(ChatActivity.this.dialog_id, ChatActivity.attach_photo);
                    ChatActivity.this.finishFragment();
                }
            }

            public void onClick(View view) {
                if (ChatActivity.this.getParentActivity() != null) {
                    Builder builder;
                    if (ChatActivity.this.currentUser == null || !ChatActivity.this.userBlocked) {
                        if (ChatActivity.this.currentUser != null && ChatActivity.this.currentUser.bot && ChatActivity.this.botUser != null) {
                            if (ChatActivity.this.botUser.length() != 0) {
                                MessagesController.getInstance().sendBotStart(ChatActivity.this.currentUser, ChatActivity.this.botUser);
                            } else {
                                SendMessagesHelper.getInstance().sendMessage("/start", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                            }
                            ChatActivity.this.botUser = null;
                            ChatActivity.this.updateBottomOverlay();
                            builder = null;
                        } else if (!ChatObject.isChannel(ChatActivity.this.currentChat) || (ChatActivity.this.currentChat instanceof TL_channelForbidden)) {
                            builder = new Builder(ChatActivity.this.getParentActivity());
                            if (ChatObject.isKickedFromChat(ChatActivity.this.currentChat) || !ChatObject.isLeftFromChat(ChatActivity.this.currentChat)) {
                                builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", C0338R.string.AreYouSureDeleteThisChat));
                                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C12573());
                            } else {
                                builder.setMessage(LocaleController.getString("ReturnToGroupWarn", C0338R.string.ReturnToGroupWarn));
                                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C12562());
                            }
                        } else if (ChatObject.isNotInChat(ChatActivity.this.currentChat)) {
                            MessagesController.getInstance().addUserToChat(ChatActivity.this.currentChat.id, UserConfig.getCurrentUser(), null, ChatActivity.attach_photo, null, null);
                            builder = null;
                        } else {
                            ChatActivity.this.toggleMute(true);
                            builder = null;
                        }
                    } else if (ChatActivity.this.currentUser.bot) {
                        String access$12900 = ChatActivity.this.botUser;
                        ChatActivity.this.botUser = null;
                        MessagesController.getInstance().unblockUser(ChatActivity.this.currentUser.id);
                        if (access$12900 == null || access$12900.length() == 0) {
                            SendMessagesHelper.getInstance().sendMessage("/start", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                        } else {
                            MessagesController.getInstance().sendBotStart(ChatActivity.this.currentUser, access$12900);
                        }
                        builder = null;
                    } else {
                        builder = new Builder(ChatActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("AreYouSureUnblockContact", C0338R.string.AreYouSureUnblockContact));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C12551());
                    }
                    if (builder != null) {
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        ChatActivity.this.showDialog(builder.create());
                    }
                }
            }
        });
        this.bottomOverlayChatText = new TextView(context);
        this.bottomOverlayChatText.setTextSize(attach_gallery, 15.0f);
        this.bottomOverlayChatText.setTypeface(FontUtil.m1176a().m1160c());
        this.bottomOverlayChatText.setTextColor(Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, share_contact));
        this.chatAdapter.updateRows();
        if (this.loading && this.messages.isEmpty()) {
            this.progressView.setVisibility(this.chatAdapter.botInfoRow == -1 ? attach_photo : attach_document);
            this.chatListView.setEmptyView(null);
        } else {
            this.progressView.setVisibility(attach_document);
            this.chatListView.setEmptyView(this.emptyViewContainer);
        }
        this.chatActivityEnterView.setButtons(this.userBlocked ? null : this.botButtons);
        this.dateToastTv = new TextView(context);
        this.dateToastTv.setTypeface(FontUtil.m1176a().m1161d());
        this.dateToastTv.setVisibility(8);
        this.dateToastTv.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(3.0f));
        this.dateToastTv.setTextSize(attach_gallery, 14.0f);
        if (ThemeUtil.m2490b()) {
            this.dateToastTv.setTextSize((float) AdvanceTheme.m2286c(AdvanceTheme.bW, delete_chat));
        }
        Drawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius((float) AndroidUtilities.dp(4.0f));
        gradientDrawable.setColor(-1584887671);
        if (ThemeUtil.m2490b()) {
            gradientDrawable.setColor(AdvanceTheme.m2286c(AdvanceTheme.bn, -1584887671));
        }
        this.dateToastTv.setBackgroundDrawable(gradientDrawable);
        this.dateToastTv.setTextColor(-1);
        if (ThemeUtil.m2490b()) {
            this.dateToastTv.setTextColor(AdvanceTheme.m2286c(AdvanceTheme.bV, -1));
        }
        sizeNotifierFrameLayout.addView(this.dateToastTv, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 2.0f, 0.0f, 0.0f));
        this.chatBarUtil = new ChatBarUtil();
        this.chatBarUtil.m378a(context, this, sizeNotifierFrameLayout, this.dateToastTv, this.dialog_id);
        if (!AndroidUtilities.isTablet() || AndroidUtilities.isSmallTablet()) {
            anonymousClass38 = new PlayerView(context, this);
            this.playerView = anonymousClass38;
            sizeNotifierFrameLayout.addView(anonymousClass38, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            anonymousClass38 = new PowerView(context, this);
            this.powerView = anonymousClass38;
            sizeNotifierFrameLayout.addView(anonymousClass38, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        updateContactStatus();
        updateBottomOverlay();
        updateSecretStatus();
        updateSpamView();
        updatePinnedMessageView(true);
        try {
            if (this.currentEncryptedChat != null && VERSION.SDK_INT >= 23) {
                getParentActivity().getWindow().setFlags(MessagesController.UPDATE_MASK_CHANNEL, MessagesController.UPDATE_MASK_CHANNEL);
            }
        } catch (Throwable th) {
            FileLog.m18e("tmessages", th);
        }
        fixLayoutInternal();
        sizeNotifierFrameLayout.addView(this.actionBar);
        showHelpDialog();
        initSelectionMode();
        return this.fragmentView;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r24, java.lang.Object... r25) {
        /*
        r23 = this;
        r4 = com.hanista.mobogram.messenger.NotificationCenter.messagesDidLoaded;
        r0 = r24;
        if (r0 != r4) goto L_0x09d3;
    L_0x0006:
        r4 = 10;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
        r0 = r23;
        r5 = r0.classGuid;
        if (r4 != r5) goto L_0x0053;
    L_0x0016:
        r0 = r23;
        r4 = r0.openAnimationEnded;
        if (r4 != 0) goto L_0x003a;
    L_0x001c:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
        r5 = 4;
        r5 = new int[r5];
        r6 = 0;
        r7 = com.hanista.mobogram.messenger.NotificationCenter.chatInfoDidLoaded;
        r5[r6] = r7;
        r6 = 1;
        r7 = com.hanista.mobogram.messenger.NotificationCenter.dialogsNeedReload;
        r5[r6] = r7;
        r6 = 2;
        r7 = com.hanista.mobogram.messenger.NotificationCenter.closeChats;
        r5[r6] = r7;
        r6 = 3;
        r7 = com.hanista.mobogram.messenger.NotificationCenter.botKeyboardDidLoaded;
        r5[r6] = r7;
        r4.setAllowedNotificationsDutingAnimation(r5);
    L_0x003a:
        r4 = 11;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
        r0 = r23;
        r5 = r0.waitingForLoad;
        r4 = java.lang.Integer.valueOf(r4);
        r4 = r5.indexOf(r4);
        r5 = -1;
        if (r4 != r5) goto L_0x0054;
    L_0x0053:
        return;
    L_0x0054:
        r0 = r23;
        r5 = r0.waitingForLoad;
        r5.remove(r4);
        r4 = 2;
        r4 = r25[r4];
        r4 = (java.util.ArrayList) r4;
        r0 = r23;
        r5 = r0.waitingForReplyMessageLoad;
        if (r5 == 0) goto L_0x00b4;
    L_0x0066:
        r7 = 0;
        r5 = 0;
        r6 = r5;
    L_0x0069:
        r5 = r4.size();
        if (r6 >= r5) goto L_0x2877;
    L_0x006f:
        r5 = r4.get(r6);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        r5 = r5.getId();
        r0 = r23;
        r8 = r0.startLoadFromMessageId;
        if (r5 != r8) goto L_0x01cb;
    L_0x007f:
        r5 = 1;
    L_0x0080:
        if (r5 != 0) goto L_0x00a1;
    L_0x0082:
        r0 = r23;
        r5 = r0.gotoMessageSelected;
        if (r5 == 0) goto L_0x01d0;
    L_0x0088:
        r5 = r4.size();
        r5 = r5 + -1;
        r5 = r4.get(r5);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        r5 = r5.getId();
        r0 = r23;
        r0.startLoadFromMessageId = r5;
        r5 = 0;
        r0 = r23;
        r0.gotoMessageSelected = r5;
    L_0x00a1:
        r0 = r23;
        r5 = r0.startLoadFromMessageId;
        r0 = r23;
        r6 = r0.needSelectFromMessageId;
        r23.clearChatData();
        r0 = r23;
        r0.startLoadFromMessageId = r5;
        r0 = r23;
        r0.needSelectFromMessageId = r6;
    L_0x00b4:
        r0 = r23;
        r5 = r0.loadsCount;
        r5 = r5 + 1;
        r0 = r23;
        r0.loadsCount = r5;
        r5 = 0;
        r5 = r25[r5];
        r5 = (java.lang.Long) r5;
        r6 = r5.longValue();
        r0 = r23;
        r8 = r0.dialog_id;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 != 0) goto L_0x01d7;
    L_0x00cf:
        r5 = 0;
        r7 = r5;
    L_0x00d1:
        r5 = 1;
        r5 = r25[r5];
        r5 = (java.lang.Integer) r5;
        r13 = r5.intValue();
        r5 = 3;
        r5 = r25[r5];
        r5 = (java.lang.Boolean) r5;
        r14 = r5.booleanValue();
        r5 = 4;
        r5 = r25[r5];
        r5 = (java.lang.Integer) r5;
        r6 = r5.intValue();
        r5 = 7;
        r5 = r25[r5];
        r5 = (java.lang.Integer) r5;
        r15 = r5.intValue();
        r5 = 8;
        r5 = r25[r5];
        r5 = (java.lang.Integer) r5;
        r16 = r5.intValue();
        r10 = 0;
        if (r6 == 0) goto L_0x01db;
    L_0x0102:
        r0 = r23;
        r0.first_unread_id = r6;
        r5 = 5;
        r5 = r25[r5];
        r5 = (java.lang.Integer) r5;
        r5 = r5.intValue();
        r0 = r23;
        r0.last_message_id = r5;
        r5 = 6;
        r5 = r25[r5];
        r5 = (java.lang.Integer) r5;
        r5 = r5.intValue();
        r0 = r23;
        r0.unread_to_load = r5;
    L_0x0120:
        r9 = 0;
        r0 = r23;
        r6 = r0.forwardEndReached;
        r0 = r23;
        r5 = r0.startLoadFromMessageId;
        if (r5 != 0) goto L_0x01f5;
    L_0x012b:
        r0 = r23;
        r5 = r0.last_message_id;
        if (r5 != 0) goto L_0x01f5;
    L_0x0131:
        r5 = 1;
    L_0x0132:
        r6[r7] = r5;
        r5 = 1;
        r0 = r16;
        if (r0 == r5) goto L_0x013e;
    L_0x0139:
        r5 = 3;
        r0 = r16;
        if (r0 != r5) goto L_0x0160;
    L_0x013e:
        r5 = 1;
        if (r7 != r5) goto L_0x0160;
    L_0x0141:
        r0 = r23;
        r5 = r0.endReached;
        r6 = 0;
        r0 = r23;
        r8 = r0.cacheEndReached;
        r11 = 0;
        r12 = 1;
        r8[r11] = r12;
        r5[r6] = r12;
        r0 = r23;
        r5 = r0.forwardEndReached;
        r6 = 0;
        r8 = 0;
        r5[r6] = r8;
        r0 = r23;
        r5 = r0.minMessageId;
        r6 = 0;
        r8 = 0;
        r5[r6] = r8;
    L_0x0160:
        r0 = r23;
        r5 = r0.loadsCount;
        r6 = 1;
        if (r5 != r6) goto L_0x0179;
    L_0x0167:
        r5 = r4.size();
        r6 = 20;
        if (r5 <= r6) goto L_0x0179;
    L_0x016f:
        r0 = r23;
        r5 = r0.loadsCount;
        r5 = r5 + 1;
        r0 = r23;
        r0.loadsCount = r5;
    L_0x0179:
        r0 = r23;
        r5 = r0.firstLoading;
        if (r5 == 0) goto L_0x0219;
    L_0x017f:
        r0 = r23;
        r5 = r0.forwardEndReached;
        r5 = r5[r7];
        if (r5 != 0) goto L_0x020a;
    L_0x0187:
        r0 = r23;
        r5 = r0.messages;
        r5.clear();
        r0 = r23;
        r5 = r0.messagesByDays;
        r5.clear();
        r5 = 0;
    L_0x0196:
        r6 = 2;
        if (r5 >= r6) goto L_0x020a;
    L_0x0199:
        r0 = r23;
        r6 = r0.messagesDict;
        r6 = r6[r5];
        r6.clear();
        r0 = r23;
        r6 = r0.currentEncryptedChat;
        if (r6 != 0) goto L_0x01f8;
    L_0x01a8:
        r0 = r23;
        r6 = r0.maxMessageId;
        r8 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r6[r5] = r8;
        r0 = r23;
        r6 = r0.minMessageId;
        r8 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r6[r5] = r8;
    L_0x01b9:
        r0 = r23;
        r6 = r0.maxDate;
        r8 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r6[r5] = r8;
        r0 = r23;
        r6 = r0.minDate;
        r8 = 0;
        r6[r5] = r8;
        r5 = r5 + 1;
        goto L_0x0196;
    L_0x01cb:
        r5 = r6 + 1;
        r6 = r5;
        goto L_0x0069;
    L_0x01d0:
        r4 = 0;
        r0 = r23;
        r0.startLoadFromMessageId = r4;
        goto L_0x0053;
    L_0x01d7:
        r5 = 1;
        r7 = r5;
        goto L_0x00d1;
    L_0x01db:
        r0 = r23;
        r5 = r0.startLoadFromMessageId;
        if (r5 == 0) goto L_0x0120;
    L_0x01e1:
        r5 = 3;
        r0 = r16;
        if (r0 != r5) goto L_0x0120;
    L_0x01e6:
        r5 = 5;
        r5 = r25[r5];
        r5 = (java.lang.Integer) r5;
        r5 = r5.intValue();
        r0 = r23;
        r0.last_message_id = r5;
        goto L_0x0120;
    L_0x01f5:
        r5 = 0;
        goto L_0x0132;
    L_0x01f8:
        r0 = r23;
        r6 = r0.maxMessageId;
        r8 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r6[r5] = r8;
        r0 = r23;
        r6 = r0.minMessageId;
        r8 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r6[r5] = r8;
        goto L_0x01b9;
    L_0x020a:
        r5 = 0;
        r0 = r23;
        r0.firstLoading = r5;
        r5 = new com.hanista.mobogram.ui.ChatActivity$61;
        r0 = r23;
        r5.<init>();
        com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r5);
    L_0x0219:
        r5 = 1;
        r0 = r16;
        if (r0 != r5) goto L_0x0221;
    L_0x021e:
        java.util.Collections.reverse(r4);
    L_0x0221:
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        if (r5 != 0) goto L_0x0232;
    L_0x0227:
        r0 = r23;
        r0 = r0.dialog_id;
        r18 = r0;
        r0 = r18;
        com.hanista.mobogram.messenger.query.MessagesQuery.loadReplyMessagesForMessages(r4, r0);
    L_0x0232:
        r8 = 0;
        r6 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r5 = 0;
        r11 = r9;
        r12 = r10;
        r10 = r8;
        r8 = r5;
    L_0x023b:
        r5 = r4.size();
        if (r8 >= r5) goto L_0x060a;
    L_0x0241:
        r5 = r4.get(r8);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        r0 = r23;
        r9 = r0.gotoDateMessageSelected;
        if (r9 == 0) goto L_0x027e;
    L_0x024d:
        r9 = r4.size();
        r17 = 23;
        r0 = r17;
        if (r9 != r0) goto L_0x027e;
    L_0x0257:
        r9 = r5.messageOwner;
        if (r9 == 0) goto L_0x027e;
    L_0x025b:
        r9 = r5.messageOwner;
        r9 = r9.date;
        r0 = r23;
        r0 = r0.gotoDateMessage;
        r17 = r0;
        r9 = r9 - r17;
        r9 = java.lang.Math.abs(r9);
        if (r9 >= r6) goto L_0x027e;
    L_0x026d:
        r6 = r5.messageOwner;
        r6 = r6.date;
        r0 = r23;
        r9 = r0.gotoDateMessage;
        r6 = r6 - r9;
        r6 = java.lang.Math.abs(r6);
        r0 = r23;
        r0.scrollToMessage = r5;
    L_0x027e:
        r9 = r6;
        r6 = r5.getApproximateHeight();
        r10 = r10 + r6;
        r0 = r23;
        r6 = r0.currentUser;
        if (r6 == 0) goto L_0x02ab;
    L_0x028a:
        r0 = r23;
        r6 = r0.currentUser;
        r6 = r6.self;
        if (r6 == 0) goto L_0x029a;
    L_0x0292:
        r6 = r5.messageOwner;
        r17 = 1;
        r0 = r17;
        r6.out = r0;
    L_0x029a:
        r0 = r23;
        r6 = r0.currentUser;
        r6 = r6.bot;
        if (r6 == 0) goto L_0x02ab;
    L_0x02a2:
        r6 = r5.isOut();
        if (r6 == 0) goto L_0x02ab;
    L_0x02a8:
        r5.setIsRead();
    L_0x02ab:
        r0 = r23;
        r6 = r0.messagesDict;
        r6 = r6[r7];
        r17 = r5.getId();
        r17 = java.lang.Integer.valueOf(r17);
        r0 = r17;
        r6 = r6.containsKey(r0);
        if (r6 == 0) goto L_0x02cb;
    L_0x02c1:
        r6 = r11;
        r11 = r12;
    L_0x02c3:
        r5 = r8 + 1;
        r8 = r5;
        r12 = r11;
        r11 = r6;
        r6 = r9;
        goto L_0x023b;
    L_0x02cb:
        r6 = 1;
        if (r7 != r6) goto L_0x02d1;
    L_0x02ce:
        r5.setIsRead();
    L_0x02d1:
        if (r7 != 0) goto L_0x02f7;
    L_0x02d3:
        r0 = r23;
        r6 = r0.currentChat;
        r6 = com.hanista.mobogram.messenger.ChatObject.isChannel(r6);
        if (r6 == 0) goto L_0x02f7;
    L_0x02dd:
        r6 = r5.getId();
        r17 = 1;
        r0 = r17;
        if (r6 != r0) goto L_0x02f7;
    L_0x02e7:
        r0 = r23;
        r6 = r0.endReached;
        r17 = 1;
        r6[r7] = r17;
        r0 = r23;
        r6 = r0.cacheEndReached;
        r17 = 1;
        r6[r7] = r17;
    L_0x02f7:
        r6 = r5.getId();
        if (r6 <= 0) goto L_0x0386;
    L_0x02fd:
        r0 = r23;
        r6 = r0.maxMessageId;
        r17 = r5.getId();
        r0 = r23;
        r0 = r0.maxMessageId;
        r18 = r0;
        r18 = r18[r7];
        r17 = java.lang.Math.min(r17, r18);
        r6[r7] = r17;
        r0 = r23;
        r6 = r0.minMessageId;
        r17 = r5.getId();
        r0 = r23;
        r0 = r0.minMessageId;
        r18 = r0;
        r18 = r18[r7];
        r17 = java.lang.Math.max(r17, r18);
        r6[r7] = r17;
    L_0x0329:
        r6 = r5.messageOwner;
        r6 = r6.date;
        if (r6 == 0) goto L_0x0373;
    L_0x032f:
        r0 = r23;
        r6 = r0.maxDate;
        r0 = r23;
        r0 = r0.maxDate;
        r17 = r0;
        r17 = r17[r7];
        r0 = r5.messageOwner;
        r18 = r0;
        r0 = r18;
        r0 = r0.date;
        r18 = r0;
        r17 = java.lang.Math.max(r17, r18);
        r6[r7] = r17;
        r0 = r23;
        r6 = r0.minDate;
        r6 = r6[r7];
        if (r6 == 0) goto L_0x0363;
    L_0x0353:
        r6 = r5.messageOwner;
        r6 = r6.date;
        r0 = r23;
        r0 = r0.minDate;
        r17 = r0;
        r17 = r17[r7];
        r0 = r17;
        if (r6 >= r0) goto L_0x0373;
    L_0x0363:
        r0 = r23;
        r6 = r0.minDate;
        r0 = r5.messageOwner;
        r17 = r0;
        r0 = r17;
        r0 = r0.date;
        r17 = r0;
        r6[r7] = r17;
    L_0x0373:
        r6 = r5.type;
        if (r6 < 0) goto L_0x05fe;
    L_0x0377:
        r6 = 1;
        if (r7 != r6) goto L_0x03ba;
    L_0x037a:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r6 == 0) goto L_0x03ba;
    L_0x0382:
        r6 = r11;
        r11 = r12;
        goto L_0x02c3;
    L_0x0386:
        r0 = r23;
        r6 = r0.currentEncryptedChat;
        if (r6 == 0) goto L_0x0329;
    L_0x038c:
        r0 = r23;
        r6 = r0.maxMessageId;
        r17 = r5.getId();
        r0 = r23;
        r0 = r0.maxMessageId;
        r18 = r0;
        r18 = r18[r7];
        r17 = java.lang.Math.max(r17, r18);
        r6[r7] = r17;
        r0 = r23;
        r6 = r0.minMessageId;
        r17 = r5.getId();
        r0 = r23;
        r0 = r0.minMessageId;
        r18 = r0;
        r18 = r18[r7];
        r17 = java.lang.Math.min(r17, r18);
        r6[r7] = r17;
        goto L_0x0329;
    L_0x03ba:
        r6 = r5.isOut();
        if (r6 != 0) goto L_0x03c7;
    L_0x03c0:
        r6 = r5.isUnread();
        if (r6 == 0) goto L_0x03c7;
    L_0x03c6:
        r12 = 1;
    L_0x03c7:
        r6 = r5.messageOwner;
        if (r6 == 0) goto L_0x03e3;
    L_0x03cb:
        r0 = r23;
        r6 = r0.justFromId;
        if (r6 <= 0) goto L_0x03e3;
    L_0x03d1:
        r6 = r5.messageOwner;
        r6 = r6.from_id;
        r0 = r23;
        r0 = r0.justFromId;
        r17 = r0;
        r0 = r17;
        if (r6 == r0) goto L_0x03e3;
    L_0x03df:
        r6 = r11;
        r11 = r12;
        goto L_0x02c3;
    L_0x03e3:
        r6 = r5.messageOwner;
        if (r6 == 0) goto L_0x0445;
    L_0x03e7:
        r0 = r23;
        r6 = r0.archive;
        if (r6 == 0) goto L_0x0445;
    L_0x03ed:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m204a();
        r18 = r6.longValue();
        r20 = -1;
        r6 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
        if (r6 != 0) goto L_0x0417;
    L_0x03ff:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m211e();
        r17 = r5.getId();
        r17 = java.lang.Integer.valueOf(r17);
        r0 = r17;
        r6 = r6.contains(r0);
        if (r6 != 0) goto L_0x05fe;
    L_0x0417:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m204a();
        r18 = r6.longValue();
        r20 = 0;
        r6 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
        if (r6 <= 0) goto L_0x0445;
    L_0x0429:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m211e();
        r17 = r5.getId();
        r17 = java.lang.Integer.valueOf(r17);
        r0 = r17;
        r6 = r6.contains(r0);
        if (r6 != 0) goto L_0x0445;
    L_0x0441:
        r6 = r11;
        r11 = r12;
        goto L_0x02c3;
    L_0x0445:
        r0 = r23;
        r6 = r0.messagesDict;
        r6 = r6[r7];
        r17 = r5.getId();
        r17 = java.lang.Integer.valueOf(r17);
        r0 = r17;
        r6.put(r0, r5);
        r0 = r23;
        r6 = r0.messagesByDays;
        r0 = r5.dateKey;
        r17 = r0;
        r0 = r17;
        r6 = r6.get(r0);
        r6 = (java.util.ArrayList) r6;
        if (r6 != 0) goto L_0x04f2;
    L_0x046a:
        r6 = new java.util.ArrayList;
        r6.<init>();
        r0 = r23;
        r0 = r0.messagesByDays;
        r17 = r0;
        r0 = r5.dateKey;
        r18 = r0;
        r0 = r17;
        r1 = r18;
        r0.put(r1, r6);
        r17 = new com.hanista.mobogram.tgnet.TLRPC$Message;
        r17.<init>();
        r0 = r5.messageOwner;
        r18 = r0;
        r0 = r18;
        r0 = r0.date;
        r18 = r0;
        r0 = r18;
        r0 = (long) r0;
        r18 = r0;
        r18 = com.hanista.mobogram.messenger.LocaleController.formatDateChat(r18);
        r0 = r18;
        r1 = r17;
        r1.message = r0;
        r18 = 0;
        r0 = r18;
        r1 = r17;
        r1.id = r0;
        r0 = r5.messageOwner;
        r18 = r0;
        r0 = r18;
        r0 = r0.date;
        r18 = r0;
        r0 = r18;
        r1 = r17;
        r1.date = r0;
        r18 = new com.hanista.mobogram.messenger.MessageObject;
        r19 = 0;
        r20 = 0;
        r0 = r18;
        r1 = r17;
        r2 = r19;
        r3 = r20;
        r0.<init>(r1, r2, r3);
        r17 = 10;
        r0 = r17;
        r1 = r18;
        r1.type = r0;
        r17 = 1;
        r0 = r17;
        r1 = r18;
        r1.contentType = r0;
        r17 = 1;
        r0 = r16;
        r1 = r17;
        if (r0 != r1) goto L_0x05b5;
    L_0x04df:
        r0 = r23;
        r0 = r0.messages;
        r17 = r0;
        r19 = 0;
        r0 = r17;
        r1 = r19;
        r2 = r18;
        r0.add(r1, r2);
    L_0x04f0:
        r11 = r11 + 1;
    L_0x04f2:
        r11 = r11 + 1;
        r17 = 1;
        r0 = r16;
        r1 = r17;
        if (r0 != r1) goto L_0x050e;
    L_0x04fc:
        r6.add(r5);
        r0 = r23;
        r0 = r0.messages;
        r17 = r0;
        r18 = 0;
        r0 = r17;
        r1 = r18;
        r0.add(r1, r5);
    L_0x050e:
        r17 = 1;
        r0 = r16;
        r1 = r17;
        if (r0 == r1) goto L_0x052e;
    L_0x0516:
        r6.add(r5);
        r0 = r23;
        r6 = r0.messages;
        r0 = r23;
        r0 = r0.messages;
        r17 = r0;
        r17 = r17.size();
        r17 = r17 + -1;
        r0 = r17;
        r6.add(r0, r5);
    L_0x052e:
        r6 = r5.getId();
        r0 = r23;
        r0 = r0.last_message_id;
        r17 = r0;
        r0 = r17;
        if (r6 != r0) goto L_0x0544;
    L_0x053c:
        r0 = r23;
        r6 = r0.forwardEndReached;
        r17 = 1;
        r6[r7] = r17;
    L_0x0544:
        r6 = 2;
        r0 = r16;
        if (r0 != r6) goto L_0x05c0;
    L_0x0549:
        r6 = r5.getId();
        r0 = r23;
        r0 = r0.first_unread_id;
        r17 = r0;
        r0 = r17;
        if (r6 != r0) goto L_0x05c0;
    L_0x0557:
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.displaySize;
        r5 = r5.y;
        r5 = r5 / 2;
        if (r10 > r5) goto L_0x0568;
    L_0x055f:
        r0 = r23;
        r5 = r0.forwardEndReached;
        r6 = 0;
        r5 = r5[r6];
        if (r5 != 0) goto L_0x05fe;
    L_0x0568:
        r5 = new com.hanista.mobogram.tgnet.TLRPC$Message;
        r5.<init>();
        r6 = "";
        r5.message = r6;
        r6 = 0;
        r5.id = r6;
        r6 = new com.hanista.mobogram.messenger.MessageObject;
        r17 = 0;
        r18 = 0;
        r0 = r17;
        r1 = r18;
        r6.<init>(r5, r0, r1);
        r5 = 6;
        r6.type = r5;
        r5 = 2;
        r6.contentType = r5;
        r0 = r23;
        r5 = r0.messages;
        r0 = r23;
        r0 = r0.messages;
        r17 = r0;
        r17 = r17.size();
        r17 = r17 + -1;
        r0 = r17;
        r5.add(r0, r6);
        r0 = r23;
        r0.unreadMessageObject = r6;
        r0 = r23;
        r5 = r0.unreadMessageObject;
        r0 = r23;
        r0.scrollToMessage = r5;
        r5 = -10000; // 0xffffffffffffd8f0 float:NaN double:NaN;
        r0 = r23;
        r0.scrollToMessagePosition = r5;
        r11 = r11 + 1;
        r6 = r11;
        r11 = r12;
        goto L_0x02c3;
    L_0x05b5:
        r0 = r23;
        r0 = r0.messages;
        r17 = r0;
        r17.add(r18);
        goto L_0x04f0;
    L_0x05c0:
        r6 = 3;
        r0 = r16;
        if (r0 != r6) goto L_0x05fe;
    L_0x05c5:
        r6 = r5.getId();
        r0 = r23;
        r0 = r0.startLoadFromMessageId;
        r17 = r0;
        r0 = r17;
        if (r6 != r0) goto L_0x05fe;
    L_0x05d3:
        r0 = r23;
        r6 = r0.needSelectFromMessageId;
        if (r6 == 0) goto L_0x0602;
    L_0x05d9:
        r6 = r5.getId();
        r0 = r23;
        r0.highlightMessageId = r6;
    L_0x05e1:
        r0 = r23;
        r6 = r0.gotoDateMessageSelected;
        if (r6 != 0) goto L_0x05eb;
    L_0x05e7:
        r0 = r23;
        r0.scrollToMessage = r5;
    L_0x05eb:
        r5 = 0;
        r0 = r23;
        r0.startLoadFromMessageId = r5;
        r0 = r23;
        r5 = r0.scrollToMessagePosition;
        r6 = -10000; // 0xffffffffffffd8f0 float:NaN double:NaN;
        if (r5 != r6) goto L_0x05fe;
    L_0x05f8:
        r5 = -9000; // 0xffffffffffffdcd8 float:NaN double:NaN;
        r0 = r23;
        r0.scrollToMessagePosition = r5;
    L_0x05fe:
        r6 = r11;
        r11 = r12;
        goto L_0x02c3;
    L_0x0602:
        r6 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0 = r23;
        r0.highlightMessageId = r6;
        goto L_0x05e1;
    L_0x060a:
        if (r16 != 0) goto L_0x0618;
    L_0x060c:
        if (r11 != 0) goto L_0x0618;
    L_0x060e:
        r0 = r23;
        r5 = r0.loadsCount;
        r5 = r5 + -1;
        r0 = r23;
        r0.loadsCount = r5;
    L_0x0618:
        r0 = r23;
        r5 = r0.forwardEndReached;
        r5 = r5[r7];
        if (r5 == 0) goto L_0x062d;
    L_0x0620:
        r5 = 1;
        if (r7 == r5) goto L_0x062d;
    L_0x0623:
        r5 = 0;
        r0 = r23;
        r0.first_unread_id = r5;
        r5 = 0;
        r0 = r23;
        r0.last_message_id = r5;
    L_0x062d:
        r0 = r23;
        r5 = r0.loadsCount;
        r6 = 2;
        if (r5 > r6) goto L_0x0639;
    L_0x0634:
        if (r14 != 0) goto L_0x0639;
    L_0x0636:
        r23.updateSpamView();
    L_0x0639:
        r5 = 1;
        r0 = r16;
        if (r0 != r5) goto L_0x075f;
    L_0x063e:
        r4 = r4.size();
        if (r4 == r13) goto L_0x0672;
    L_0x0644:
        if (r14 != 0) goto L_0x0672;
    L_0x0646:
        r0 = r23;
        r4 = r0.forwardEndReached;
        r5 = 1;
        r4[r7] = r5;
        r4 = 1;
        if (r7 == r4) goto L_0x066d;
    L_0x0650:
        r4 = 0;
        r0 = r23;
        r0.first_unread_id = r4;
        r4 = 0;
        r0 = r23;
        r0.last_message_id = r4;
        r0 = r23;
        r4 = r0.chatAdapter;
        r0 = r23;
        r5 = r0.chatAdapter;
        r5 = r5.getItemCount();
        r5 = r5 + -1;
        r4.notifyItemRemoved(r5);
        r11 = r11 + -1;
    L_0x066d:
        r4 = 0;
        r0 = r23;
        r0.startLoadFromMessageId = r4;
    L_0x0672:
        if (r11 <= 0) goto L_0x06a5;
    L_0x0674:
        r0 = r23;
        r4 = r0.chatLayoutManager;
        r5 = r4.findLastVisibleItemPosition();
        r4 = 0;
        r0 = r23;
        r6 = r0.chatLayoutManager;
        r6 = r6.getItemCount();
        r6 = r6 + -1;
        if (r5 == r6) goto L_0x0744;
    L_0x0689:
        r5 = -1;
    L_0x068a:
        r0 = r23;
        r6 = r0.chatAdapter;
        r0 = r23;
        r8 = r0.chatAdapter;
        r8 = r8.getItemCount();
        r8 = r8 + -1;
        r6.notifyItemRangeInserted(r8, r11);
        r6 = -1;
        if (r5 == r6) goto L_0x06a5;
    L_0x069e:
        r0 = r23;
        r6 = r0.chatLayoutManager;
        r6.scrollToPositionWithOffset(r5, r4);
    L_0x06a5:
        r4 = 0;
        r0 = r23;
        r0.loadingForward = r4;
    L_0x06aa:
        r0 = r23;
        r4 = r0.first;
        if (r4 == 0) goto L_0x06dc;
    L_0x06b0:
        r0 = r23;
        r4 = r0.messages;
        r4 = r4.size();
        if (r4 <= 0) goto L_0x06dc;
    L_0x06ba:
        if (r7 != 0) goto L_0x06d7;
    L_0x06bc:
        r0 = r23;
        r4 = r0.messages;
        r5 = 0;
        r4 = r4.get(r5);
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        r4 = r4.getId();
        r5 = new com.hanista.mobogram.ui.ChatActivity$62;
        r0 = r23;
        r5.<init>(r4, r15, r12);
        r6 = 700; // 0x2bc float:9.81E-43 double:3.46E-321;
        com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r5, r6);
    L_0x06d7:
        r4 = 0;
        r0 = r23;
        r0.first = r4;
    L_0x06dc:
        r0 = r23;
        r4 = r0.messages;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x070a;
    L_0x06e6:
        r0 = r23;
        r4 = r0.currentEncryptedChat;
        if (r4 != 0) goto L_0x070a;
    L_0x06ec:
        r0 = r23;
        r4 = r0.currentUser;
        if (r4 == 0) goto L_0x070a;
    L_0x06f2:
        r0 = r23;
        r4 = r0.currentUser;
        r4 = r4.bot;
        if (r4 == 0) goto L_0x070a;
    L_0x06fa:
        r0 = r23;
        r4 = r0.botUser;
        if (r4 != 0) goto L_0x070a;
    L_0x0700:
        r4 = "";
        r0 = r23;
        r0.botUser = r4;
        r23.updateBottomOverlay();
    L_0x070a:
        if (r11 != 0) goto L_0x09c3;
    L_0x070c:
        r0 = r23;
        r4 = r0.currentEncryptedChat;
        if (r4 == 0) goto L_0x09c3;
    L_0x0712:
        r0 = r23;
        r4 = r0.endReached;
        r5 = 0;
        r4 = r4[r5];
        if (r4 != 0) goto L_0x09c3;
    L_0x071b:
        r4 = 1;
        r0 = r23;
        r0.first = r4;
        r0 = r23;
        r4 = r0.chatListView;
        if (r4 == 0) goto L_0x072e;
    L_0x0726:
        r0 = r23;
        r4 = r0.chatListView;
        r5 = 0;
        r4.setEmptyView(r5);
    L_0x072e:
        r0 = r23;
        r4 = r0.emptyViewContainer;
        if (r4 == 0) goto L_0x073c;
    L_0x0734:
        r0 = r23;
        r4 = r0.emptyViewContainer;
        r5 = 4;
        r4.setVisibility(r5);
    L_0x073c:
        r4 = 0;
        r0 = r23;
        r0.checkScrollForLoad(r4);
        goto L_0x0053;
    L_0x0744:
        r0 = r23;
        r4 = r0.chatLayoutManager;
        r4 = r4.findViewByPosition(r5);
        if (r4 != 0) goto L_0x075a;
    L_0x074e:
        r4 = 0;
    L_0x074f:
        r0 = r23;
        r6 = r0.chatListView;
        r6 = r6.getPaddingTop();
        r4 = r4 - r6;
        goto L_0x068a;
    L_0x075a:
        r4 = r4.getTop();
        goto L_0x074f;
    L_0x075f:
        r4 = r4.size();
        if (r4 >= r13) goto L_0x078b;
    L_0x0765:
        r4 = 3;
        r0 = r16;
        if (r0 == r4) goto L_0x078b;
    L_0x076a:
        if (r14 == 0) goto L_0x088f;
    L_0x076c:
        r0 = r23;
        r4 = r0.currentEncryptedChat;
        if (r4 != 0) goto L_0x0778;
    L_0x0772:
        r0 = r23;
        r4 = r0.isBroadcast;
        if (r4 == 0) goto L_0x077f;
    L_0x0778:
        r0 = r23;
        r4 = r0.endReached;
        r5 = 1;
        r4[r7] = r5;
    L_0x077f:
        r4 = 2;
        r0 = r16;
        if (r0 == r4) goto L_0x078b;
    L_0x0784:
        r0 = r23;
        r4 = r0.cacheEndReached;
        r5 = 1;
        r4[r7] = r5;
    L_0x078b:
        r4 = 0;
        r0 = r23;
        r0.loading = r4;
        r0 = r23;
        r4 = r0.chatListView;
        if (r4 == 0) goto L_0x09b1;
    L_0x0796:
        r0 = r23;
        r4 = r0.first;
        if (r4 != 0) goto L_0x07a8;
    L_0x079c:
        r0 = r23;
        r4 = r0.scrollToTopOnResume;
        if (r4 != 0) goto L_0x07a8;
    L_0x07a2:
        r0 = r23;
        r4 = r0.forceScrollToTop;
        if (r4 == 0) goto L_0x08f0;
    L_0x07a8:
        r4 = 0;
        r0 = r23;
        r0.forceScrollToTop = r4;
        r0 = r23;
        r4 = r0.chatAdapter;
        r4.notifyDataSetChanged();
        r0 = r23;
        r4 = r0.scrollToMessage;
        if (r4 == 0) goto L_0x08eb;
    L_0x07ba:
        r0 = r23;
        r4 = r0.scrollToMessagePosition;
        r5 = -9000; // 0xffffffffffffdcd8 float:NaN double:NaN;
        if (r4 != r5) goto L_0x089d;
    L_0x07c2:
        r4 = 0;
        r0 = r23;
        r5 = r0.chatListView;
        r5 = r5.getHeight();
        r0 = r23;
        r6 = r0.scrollToMessage;
        r6 = r6.getApproximateHeight();
        r5 = r5 - r6;
        r5 = r5 / 2;
        r4 = java.lang.Math.max(r4, r5);
    L_0x07da:
        r0 = r23;
        r5 = r0.messages;
        r5 = r5.isEmpty();
        if (r5 != 0) goto L_0x0837;
    L_0x07e4:
        r0 = r23;
        r5 = r0.messages;
        r0 = r23;
        r6 = r0.messages;
        r6 = r6.size();
        r6 = r6 + -1;
        r5 = r5.get(r6);
        r0 = r23;
        r6 = r0.scrollToMessage;
        if (r5 == r6) goto L_0x0814;
    L_0x07fc:
        r0 = r23;
        r5 = r0.messages;
        r0 = r23;
        r6 = r0.messages;
        r6 = r6.size();
        r6 = r6 + -2;
        r5 = r5.get(r6);
        r0 = r23;
        r6 = r0.scrollToMessage;
        if (r5 != r6) goto L_0x08b1;
    L_0x0814:
        r0 = r23;
        r6 = r0.chatLayoutManager;
        r0 = r23;
        r5 = r0.chatAdapter;
        r5 = r5.isBot;
        if (r5 == 0) goto L_0x08ae;
    L_0x0822:
        r5 = 1;
    L_0x0823:
        r0 = r23;
        r8 = r0.chatListView;
        r8 = r8.getPaddingTop();
        r8 = -r8;
        r9 = 1088421888; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r9 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r9);
        r8 = r8 - r9;
        r4 = r4 + r8;
        r6.scrollToPositionWithOffset(r5, r4);
    L_0x0837:
        r0 = r23;
        r4 = r0.chatListView;
        r4.invalidate();
        r0 = r23;
        r4 = r0.scrollToMessagePosition;
        r5 = -10000; // 0xffffffffffffd8f0 float:NaN double:NaN;
        if (r4 == r5) goto L_0x084e;
    L_0x0846:
        r0 = r23;
        r4 = r0.scrollToMessagePosition;
        r5 = -9000; // 0xffffffffffffdcd8 float:NaN double:NaN;
        if (r4 != r5) goto L_0x0855;
    L_0x084e:
        r4 = 1;
        r5 = 1;
        r0 = r23;
        r0.showPagedownButton(r4, r5);
    L_0x0855:
        r4 = -10000; // 0xffffffffffffd8f0 float:NaN double:NaN;
        r0 = r23;
        r0.scrollToMessagePosition = r4;
        r4 = 0;
        r0 = r23;
        r0.scrollToMessage = r4;
    L_0x0860:
        r0 = r23;
        r4 = r0.paused;
        if (r4 == 0) goto L_0x0876;
    L_0x0866:
        r4 = 1;
        r0 = r23;
        r0.scrollToTopOnResume = r4;
        r0 = r23;
        r4 = r0.scrollToMessage;
        if (r4 == 0) goto L_0x0876;
    L_0x0871:
        r4 = 1;
        r0 = r23;
        r0.scrollToTopUnReadOnResume = r4;
    L_0x0876:
        r0 = r23;
        r4 = r0.first;
        if (r4 == 0) goto L_0x06aa;
    L_0x087c:
        r0 = r23;
        r4 = r0.chatListView;
        if (r4 == 0) goto L_0x06aa;
    L_0x0882:
        r0 = r23;
        r4 = r0.chatListView;
        r0 = r23;
        r5 = r0.emptyViewContainer;
        r4.setEmptyView(r5);
        goto L_0x06aa;
    L_0x088f:
        r4 = 2;
        r0 = r16;
        if (r0 == r4) goto L_0x078b;
    L_0x0894:
        r0 = r23;
        r4 = r0.endReached;
        r5 = 1;
        r4[r7] = r5;
        goto L_0x078b;
    L_0x089d:
        r0 = r23;
        r4 = r0.scrollToMessagePosition;
        r5 = -10000; // 0xffffffffffffd8f0 float:NaN double:NaN;
        if (r4 != r5) goto L_0x08a8;
    L_0x08a5:
        r4 = 0;
        goto L_0x07da;
    L_0x08a8:
        r0 = r23;
        r4 = r0.scrollToMessagePosition;
        goto L_0x07da;
    L_0x08ae:
        r5 = 0;
        goto L_0x0823;
    L_0x08b1:
        r0 = r23;
        r5 = r0.chatLayoutManager;
        r0 = r23;
        r6 = r0.chatAdapter;
        r6 = r6.messagesStartRow;
        r0 = r23;
        r8 = r0.messages;
        r8 = r8.size();
        r6 = r6 + r8;
        r0 = r23;
        r8 = r0.messages;
        r0 = r23;
        r9 = r0.scrollToMessage;
        r8 = r8.indexOf(r9);
        r6 = r6 - r8;
        r6 = r6 + -1;
        r0 = r23;
        r8 = r0.chatListView;
        r8 = r8.getPaddingTop();
        r8 = -r8;
        r9 = 1088421888; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r9 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r9);
        r8 = r8 - r9;
        r4 = r4 + r8;
        r5.scrollToPositionWithOffset(r6, r4);
        goto L_0x0837;
    L_0x08eb:
        r23.moveScrollToLastMessage();
        goto L_0x0860;
    L_0x08f0:
        if (r11 == 0) goto L_0x0984;
    L_0x08f2:
        r4 = 0;
        r0 = r23;
        r5 = r0.endReached;
        r5 = r5[r7];
        if (r5 == 0) goto L_0x091f;
    L_0x08fb:
        if (r7 != 0) goto L_0x0907;
    L_0x08fd:
        r0 = r23;
        r8 = r0.mergeDialogId;
        r16 = 0;
        r5 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r5 == 0) goto L_0x090a;
    L_0x0907:
        r5 = 1;
        if (r7 != r5) goto L_0x091f;
    L_0x090a:
        r5 = 1;
        r0 = r23;
        r6 = r0.chatAdapter;
        r0 = r23;
        r4 = r0.chatAdapter;
        r4 = r4.isBot;
        if (r4 == 0) goto L_0x0972;
    L_0x0919:
        r4 = 1;
    L_0x091a:
        r8 = 2;
        r6.notifyItemRangeChanged(r4, r8);
        r4 = r5;
    L_0x091f:
        r0 = r23;
        r5 = r0.chatLayoutManager;
        r8 = r5.findLastVisibleItemPosition();
        r0 = r23;
        r5 = r0.chatLayoutManager;
        r5 = r5.findViewByPosition(r8);
        if (r5 != 0) goto L_0x0974;
    L_0x0931:
        r5 = 0;
    L_0x0932:
        r0 = r23;
        r6 = r0.chatListView;
        r6 = r6.getPaddingTop();
        r9 = r5 - r6;
        if (r4 == 0) goto L_0x0979;
    L_0x093e:
        r5 = 1;
    L_0x093f:
        r5 = r11 - r5;
        if (r5 <= 0) goto L_0x095f;
    L_0x0943:
        r0 = r23;
        r10 = r0.chatAdapter;
        r0 = r23;
        r5 = r0.chatAdapter;
        r5 = r5.isBot;
        if (r5 == 0) goto L_0x097b;
    L_0x0951:
        r5 = 2;
        r6 = r5;
    L_0x0953:
        if (r4 == 0) goto L_0x097e;
    L_0x0955:
        r5 = 0;
    L_0x0956:
        r6 = r6 + r5;
        if (r4 == 0) goto L_0x0980;
    L_0x0959:
        r5 = 1;
    L_0x095a:
        r5 = r11 - r5;
        r10.notifyItemRangeInserted(r6, r5);
    L_0x095f:
        r5 = -1;
        if (r8 == r5) goto L_0x0860;
    L_0x0962:
        r0 = r23;
        r5 = r0.chatLayoutManager;
        r6 = r8 + r11;
        if (r4 == 0) goto L_0x0982;
    L_0x096a:
        r4 = 1;
    L_0x096b:
        r4 = r6 - r4;
        r5.scrollToPositionWithOffset(r4, r9);
        goto L_0x0860;
    L_0x0972:
        r4 = 0;
        goto L_0x091a;
    L_0x0974:
        r5 = r5.getTop();
        goto L_0x0932;
    L_0x0979:
        r5 = 0;
        goto L_0x093f;
    L_0x097b:
        r5 = 1;
        r6 = r5;
        goto L_0x0953;
    L_0x097e:
        r5 = 1;
        goto L_0x0956;
    L_0x0980:
        r5 = 0;
        goto L_0x095a;
    L_0x0982:
        r4 = 0;
        goto L_0x096b;
    L_0x0984:
        r0 = r23;
        r4 = r0.endReached;
        r4 = r4[r7];
        if (r4 == 0) goto L_0x0860;
    L_0x098c:
        if (r7 != 0) goto L_0x0998;
    L_0x098e:
        r0 = r23;
        r4 = r0.mergeDialogId;
        r8 = 0;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x099b;
    L_0x0998:
        r4 = 1;
        if (r7 != r4) goto L_0x0860;
    L_0x099b:
        r0 = r23;
        r5 = r0.chatAdapter;
        r0 = r23;
        r4 = r0.chatAdapter;
        r4 = r4.isBot;
        if (r4 == 0) goto L_0x09af;
    L_0x09a9:
        r4 = 1;
    L_0x09aa:
        r5.notifyItemRemoved(r4);
        goto L_0x0860;
    L_0x09af:
        r4 = 0;
        goto L_0x09aa;
    L_0x09b1:
        r4 = 1;
        r0 = r23;
        r0.scrollToTopOnResume = r4;
        r0 = r23;
        r4 = r0.scrollToMessage;
        if (r4 == 0) goto L_0x06aa;
    L_0x09bc:
        r4 = 1;
        r0 = r23;
        r0.scrollToTopUnReadOnResume = r4;
        goto L_0x06aa;
    L_0x09c3:
        r0 = r23;
        r4 = r0.progressView;
        if (r4 == 0) goto L_0x073c;
    L_0x09c9:
        r0 = r23;
        r4 = r0.progressView;
        r5 = 4;
        r4.setVisibility(r5);
        goto L_0x073c;
    L_0x09d3:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.emojiDidLoaded;
        r0 = r24;
        if (r0 != r4) goto L_0x0a1c;
    L_0x09d9:
        r0 = r23;
        r4 = r0.chatListView;
        if (r4 == 0) goto L_0x09e6;
    L_0x09df:
        r0 = r23;
        r4 = r0.chatListView;
        r4.invalidateViews();
    L_0x09e6:
        r0 = r23;
        r4 = r0.replyObjectTextView;
        if (r4 == 0) goto L_0x09f3;
    L_0x09ec:
        r0 = r23;
        r4 = r0.replyObjectTextView;
        r4.invalidate();
    L_0x09f3:
        r0 = r23;
        r4 = r0.alertTextView;
        if (r4 == 0) goto L_0x0a00;
    L_0x09f9:
        r0 = r23;
        r4 = r0.alertTextView;
        r4.invalidate();
    L_0x0a00:
        r0 = r23;
        r4 = r0.pinnedMessageTextView;
        if (r4 == 0) goto L_0x0a0d;
    L_0x0a06:
        r0 = r23;
        r4 = r0.pinnedMessageTextView;
        r4.invalidate();
    L_0x0a0d:
        r0 = r23;
        r4 = r0.mentionListView;
        if (r4 == 0) goto L_0x0053;
    L_0x0a13:
        r0 = r23;
        r4 = r0.mentionListView;
        r4.invalidateViews();
        goto L_0x0053;
    L_0x0a1c:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.updateInterfaces;
        r0 = r24;
        if (r0 != r4) goto L_0x0b0c;
    L_0x0a22:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r5 = r4.intValue();
        r4 = r5 & 1;
        if (r4 != 0) goto L_0x0a33;
    L_0x0a2f:
        r4 = r5 & 16;
        if (r4 == 0) goto L_0x0a54;
    L_0x0a33:
        r0 = r23;
        r4 = r0.currentChat;
        if (r4 == 0) goto L_0x0aec;
    L_0x0a39:
        r4 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r23;
        r6 = r0.currentChat;
        r6 = r6.id;
        r6 = java.lang.Integer.valueOf(r6);
        r4 = r4.getChat(r6);
        if (r4 == 0) goto L_0x0a51;
    L_0x0a4d:
        r0 = r23;
        r0.currentChat = r4;
    L_0x0a51:
        r23.updateTitle();
    L_0x0a54:
        r4 = 0;
        r6 = r5 & 32;
        if (r6 != 0) goto L_0x0a5d;
    L_0x0a59:
        r6 = r5 & 4;
        if (r6 == 0) goto L_0x0a71;
    L_0x0a5d:
        r0 = r23;
        r4 = r0.currentChat;
        if (r4 == 0) goto L_0x0a70;
    L_0x0a63:
        r0 = r23;
        r4 = r0.avatarContainer;
        if (r4 == 0) goto L_0x0a70;
    L_0x0a69:
        r0 = r23;
        r4 = r0.avatarContainer;
        r4.updateOnlineCount();
    L_0x0a70:
        r4 = 1;
    L_0x0a71:
        r6 = r5 & 2;
        if (r6 != 0) goto L_0x0a7d;
    L_0x0a75:
        r6 = r5 & 8;
        if (r6 != 0) goto L_0x0a7d;
    L_0x0a79:
        r6 = r5 & 1;
        if (r6 == 0) goto L_0x0a83;
    L_0x0a7d:
        r23.checkAndUpdateAvatar();
        r23.updateVisibleRows();
    L_0x0a83:
        r6 = r5 & 64;
        if (r6 == 0) goto L_0x0a88;
    L_0x0a87:
        r4 = 1;
    L_0x0a88:
        r6 = r5 & 8192;
        if (r6 == 0) goto L_0x0ac3;
    L_0x0a8c:
        r0 = r23;
        r6 = r0.currentChat;
        r6 = com.hanista.mobogram.messenger.ChatObject.isChannel(r6);
        if (r6 == 0) goto L_0x0ac3;
    L_0x0a96:
        r4 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r23;
        r6 = r0.currentChat;
        r6 = r6.id;
        r6 = java.lang.Integer.valueOf(r6);
        r4 = r4.getChat(r6);
        if (r4 == 0) goto L_0x0053;
    L_0x0aaa:
        r0 = r23;
        r0.currentChat = r4;
        r4 = 1;
        r23.updateBottomOverlay();
        r0 = r23;
        r6 = r0.chatActivityEnterView;
        if (r6 == 0) goto L_0x0ac3;
    L_0x0ab8:
        r0 = r23;
        r6 = r0.chatActivityEnterView;
        r0 = r23;
        r8 = r0.dialog_id;
        r6.setDialogId(r8);
    L_0x0ac3:
        r0 = r23;
        r6 = r0.avatarContainer;
        if (r6 == 0) goto L_0x0ad2;
    L_0x0ac9:
        if (r4 == 0) goto L_0x0ad2;
    L_0x0acb:
        r0 = r23;
        r4 = r0.avatarContainer;
        r4.updateSubtitle();
    L_0x0ad2:
        r4 = r5 & 128;
        if (r4 == 0) goto L_0x0ad9;
    L_0x0ad6:
        r23.updateContactStatus();
    L_0x0ad9:
        r0 = r23;
        r4 = r0.chatBarUtil;
        if (r4 == 0) goto L_0x0053;
    L_0x0adf:
        r4 = r5 & 256;
        if (r4 == 0) goto L_0x0053;
    L_0x0ae3:
        r0 = r23;
        r4 = r0.chatBarUtil;
        r4.m380c();
        goto L_0x0053;
    L_0x0aec:
        r0 = r23;
        r4 = r0.currentUser;
        if (r4 == 0) goto L_0x0a51;
    L_0x0af2:
        r4 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r23;
        r6 = r0.currentUser;
        r6 = r6.id;
        r6 = java.lang.Integer.valueOf(r6);
        r4 = r4.getUser(r6);
        if (r4 == 0) goto L_0x0a51;
    L_0x0b06:
        r0 = r23;
        r0.currentUser = r4;
        goto L_0x0a51;
    L_0x0b0c:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.didReceivedNewMessages;
        r0 = r24;
        if (r0 != r4) goto L_0x151d;
    L_0x0b12:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Long) r4;
        r4 = r4.longValue();
        r0 = r23;
        r6 = r0.dialog_id;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x0053;
    L_0x0b23:
        r11 = 0;
        r13 = 0;
        r4 = 1;
        r4 = r25[r4];
        r4 = (java.util.ArrayList) r4;
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        if (r5 == 0) goto L_0x0bd7;
    L_0x0b30:
        r5 = r4.size();
        r6 = 1;
        if (r5 != r6) goto L_0x0bd7;
    L_0x0b37:
        r5 = 0;
        r5 = r4.get(r5);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        r0 = r23;
        r6 = r0.currentEncryptedChat;
        if (r6 == 0) goto L_0x0bd7;
    L_0x0b44:
        r6 = r5.isOut();
        if (r6 == 0) goto L_0x0bd7;
    L_0x0b4a:
        r6 = r5.messageOwner;
        r6 = r6.action;
        if (r6 == 0) goto L_0x0bd7;
    L_0x0b50:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageEncryptedAction;
        if (r6 == 0) goto L_0x0bd7;
    L_0x0b58:
        r5 = r5.messageOwner;
        r5 = r5.action;
        r5 = r5.encryptedAction;
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
        if (r5 == 0) goto L_0x0bd7;
    L_0x0b62:
        r5 = r23.getParentActivity();
        if (r5 == 0) goto L_0x0bd7;
    L_0x0b68:
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        r5 = r5.layer;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.getPeerLayerVersion(r5);
        r6 = 17;
        if (r5 >= r6) goto L_0x0bd7;
    L_0x0b76:
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        r5 = r5.ttl;
        if (r5 <= 0) goto L_0x0bd7;
    L_0x0b7e:
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        r5 = r5.ttl;
        r6 = 60;
        if (r5 > r6) goto L_0x0bd7;
    L_0x0b88:
        r5 = new android.app.AlertDialog$Builder;
        r6 = r23.getParentActivity();
        r5.<init>(r6);
        r6 = "AppName";
        r7 = 2131166486; // 0x7f070516 float:1.7947219E38 double:1.0529361463E-314;
        r6 = com.hanista.mobogram.messenger.LocaleController.getString(r6, r7);
        r5.setTitle(r6);
        r6 = "OK";
        r7 = 2131166046; // 0x7f07035e float:1.7946326E38 double:1.052935929E-314;
        r6 = com.hanista.mobogram.messenger.LocaleController.getString(r6, r7);
        r7 = 0;
        r5.setPositiveButton(r6, r7);
        r6 = "CompatibilityChat";
        r7 = 2131165517; // 0x7f07014d float:1.7945253E38 double:1.0529356676E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r0 = r23;
        r10 = r0.currentUser;
        r10 = r10.first_name;
        r8[r9] = r10;
        r9 = 1;
        r0 = r23;
        r10 = r0.currentUser;
        r10 = r10.first_name;
        r8[r9] = r10;
        r6 = com.hanista.mobogram.messenger.LocaleController.formatString(r6, r7, r8);
        r5.setMessage(r6);
        r5 = r5.create();
        r0 = r23;
        r0.showDialog(r5);
    L_0x0bd7:
        r0 = r23;
        r5 = r0.currentChat;
        if (r5 != 0) goto L_0x0be7;
    L_0x0bdd:
        r0 = r23;
        r6 = r0.inlineReturn;
        r8 = 0;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 == 0) goto L_0x0d57;
    L_0x0be7:
        r5 = 0;
        r8 = r5;
    L_0x0be9:
        r5 = r4.size();
        if (r8 >= r5) goto L_0x0d57;
    L_0x0bef:
        r5 = r4.get(r8);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        r6 = r5.messageOwner;
        if (r6 == 0) goto L_0x0c0d;
    L_0x0bf9:
        r0 = r23;
        r6 = r0.justFromId;
        if (r6 <= 0) goto L_0x0c0d;
    L_0x0bff:
        r6 = r5.messageOwner;
        r6 = r6.from_id;
        r0 = r23;
        r7 = r0.justFromId;
        if (r6 == r7) goto L_0x0c0d;
    L_0x0c09:
        r5 = r8 + 1;
        r8 = r5;
        goto L_0x0be9;
    L_0x0c0d:
        r6 = r5.messageOwner;
        if (r6 == 0) goto L_0x0c67;
    L_0x0c11:
        r0 = r23;
        r6 = r0.archive;
        if (r6 == 0) goto L_0x0c67;
    L_0x0c17:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m204a();
        r6 = r6.longValue();
        r14 = -1;
        r6 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1));
        if (r6 != 0) goto L_0x0c3f;
    L_0x0c29:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m211e();
        r7 = r5.getId();
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.contains(r7);
        if (r6 != 0) goto L_0x0c09;
    L_0x0c3f:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m204a();
        r6 = r6.longValue();
        r14 = 0;
        r6 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1));
        if (r6 <= 0) goto L_0x0c67;
    L_0x0c51:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m211e();
        r7 = r5.getId();
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.contains(r7);
        if (r6 == 0) goto L_0x0c09;
    L_0x0c67:
        r0 = r23;
        r6 = r0.currentChat;
        if (r6 == 0) goto L_0x0d08;
    L_0x0c6d:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r6 == 0) goto L_0x0c81;
    L_0x0c75:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6.user_id;
        r7 = com.hanista.mobogram.messenger.UserConfig.getClientUserId();
        if (r6 == r7) goto L_0x0c9d;
    L_0x0c81:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r6 == 0) goto L_0x0cca;
    L_0x0c89:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6.users;
        r7 = com.hanista.mobogram.messenger.UserConfig.getClientUserId();
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.contains(r7);
        if (r6 == 0) goto L_0x0cca;
    L_0x0c9d:
        r5 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r23;
        r6 = r0.currentChat;
        r6 = r6.id;
        r6 = java.lang.Integer.valueOf(r6);
        r5 = r5.getChat(r6);
        if (r5 == 0) goto L_0x0c09;
    L_0x0cb1:
        r0 = r23;
        r0.currentChat = r5;
        r23.checkActionBarMenu();
        r23.updateBottomOverlay();
        r0 = r23;
        r5 = r0.avatarContainer;
        if (r5 == 0) goto L_0x0c09;
    L_0x0cc1:
        r0 = r23;
        r5 = r0.avatarContainer;
        r5.updateSubtitle();
        goto L_0x0c09;
    L_0x0cca:
        r6 = r5.messageOwner;
        r6 = r6.reply_to_msg_id;
        if (r6 == 0) goto L_0x0c09;
    L_0x0cd0:
        r6 = r5.replyMessageObject;
        if (r6 != 0) goto L_0x0c09;
    L_0x0cd4:
        r0 = r23;
        r6 = r0.messagesDict;
        r7 = 0;
        r6 = r6[r7];
        r7 = r5.messageOwner;
        r7 = r7.reply_to_msg_id;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.get(r7);
        r6 = (com.hanista.mobogram.messenger.MessageObject) r6;
        r5.replyMessageObject = r6;
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionPinMessage;
        if (r6 == 0) goto L_0x0cfa;
    L_0x0cf3:
        r6 = 0;
        r7 = 0;
        r5.generatePinMessageText(r6, r7);
        goto L_0x0c09;
    L_0x0cfa:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r6 == 0) goto L_0x0c09;
    L_0x0d02:
        r6 = 0;
        r5.generateGameMessageText(r6);
        goto L_0x0c09;
    L_0x0d08:
        r0 = r23;
        r6 = r0.inlineReturn;
        r14 = 0;
        r6 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1));
        if (r6 == 0) goto L_0x0c09;
    L_0x0d12:
        r6 = r5.messageOwner;
        r6 = r6.reply_markup;
        if (r6 == 0) goto L_0x0c09;
    L_0x0d18:
        r6 = 0;
        r9 = r6;
    L_0x0d1a:
        r6 = r5.messageOwner;
        r6 = r6.reply_markup;
        r6 = r6.rows;
        r6 = r6.size();
        if (r9 >= r6) goto L_0x0c09;
    L_0x0d26:
        r6 = r5.messageOwner;
        r6 = r6.reply_markup;
        r6 = r6.rows;
        r6 = r6.get(r9);
        r6 = (com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonRow) r6;
        r7 = 0;
        r10 = r7;
    L_0x0d34:
        r7 = r6.buttons;
        r7 = r7.size();
        if (r10 >= r7) goto L_0x0d4f;
    L_0x0d3c:
        r7 = r6.buttons;
        r7 = r7.get(r10);
        r7 = (com.hanista.mobogram.tgnet.TLRPC.KeyboardButton) r7;
        r12 = r7 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
        if (r12 == 0) goto L_0x0d53;
    L_0x0d48:
        r7 = (com.hanista.mobogram.tgnet.TLRPC.TL_keyboardButtonSwitchInline) r7;
        r0 = r23;
        r0.processSwitchButton(r7);
    L_0x0d4f:
        r6 = r9 + 1;
        r9 = r6;
        goto L_0x0d1a;
    L_0x0d53:
        r7 = r10 + 1;
        r10 = r7;
        goto L_0x0d34;
    L_0x0d57:
        r8 = 0;
        r0 = r23;
        r5 = r0.forwardEndReached;
        r6 = 0;
        r5 = r5[r6];
        if (r5 != 0) goto L_0x0fd9;
    L_0x0d61:
        r10 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r5 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r0 = r23;
        r6 = r0.currentEncryptedChat;
        if (r6 == 0) goto L_0x0d6e;
    L_0x0d6b:
        r5 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x0d6e:
        r7 = 0;
        r6 = 0;
        r9 = r5;
        r13 = r8;
        r14 = r11;
        r8 = r7;
        r7 = r6;
    L_0x0d75:
        r5 = r4.size();
        if (r7 >= r5) goto L_0x0f5c;
    L_0x0d7b:
        r5 = r4.get(r7);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        r6 = r5.messageOwner;
        if (r6 == 0) goto L_0x0d9f;
    L_0x0d85:
        r0 = r23;
        r6 = r0.justFromId;
        if (r6 <= 0) goto L_0x0d9f;
    L_0x0d8b:
        r6 = r5.messageOwner;
        r6 = r6.from_id;
        r0 = r23;
        r11 = r0.justFromId;
        if (r6 == r11) goto L_0x0d9f;
    L_0x0d95:
        r6 = r8;
        r11 = r14;
        r8 = r13;
    L_0x0d98:
        r5 = r7 + 1;
        r7 = r5;
        r13 = r8;
        r14 = r11;
        r8 = r6;
        goto L_0x0d75;
    L_0x0d9f:
        r6 = r5.messageOwner;
        if (r6 == 0) goto L_0x0dfd;
    L_0x0da3:
        r0 = r23;
        r6 = r0.archive;
        if (r6 == 0) goto L_0x0dfd;
    L_0x0da9:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m204a();
        r16 = r6.longValue();
        r18 = -1;
        r6 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1));
        if (r6 != 0) goto L_0x0dd1;
    L_0x0dbb:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m211e();
        r11 = r5.getId();
        r11 = java.lang.Integer.valueOf(r11);
        r6 = r6.contains(r11);
        if (r6 != 0) goto L_0x2872;
    L_0x0dd1:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m204a();
        r16 = r6.longValue();
        r18 = 0;
        r6 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1));
        if (r6 <= 0) goto L_0x0dfd;
    L_0x0de3:
        r0 = r23;
        r6 = r0.archive;
        r6 = r6.m211e();
        r11 = r5.getId();
        r11 = java.lang.Integer.valueOf(r11);
        r6 = r6.contains(r11);
        if (r6 != 0) goto L_0x0dfd;
    L_0x0df9:
        r6 = r8;
        r11 = r14;
        r8 = r13;
        goto L_0x0d98;
    L_0x0dfd:
        r0 = r23;
        r6 = r0.currentUser;
        if (r6 == 0) goto L_0x0e14;
    L_0x0e03:
        r0 = r23;
        r6 = r0.currentUser;
        r6 = r6.bot;
        if (r6 == 0) goto L_0x0e14;
    L_0x0e0b:
        r6 = r5.isOut();
        if (r6 == 0) goto L_0x0e14;
    L_0x0e11:
        r5.setIsRead();
    L_0x0e14:
        r0 = r23;
        r6 = r0.avatarContainer;
        if (r6 == 0) goto L_0x0e49;
    L_0x0e1a:
        r0 = r23;
        r6 = r0.currentEncryptedChat;
        if (r6 == 0) goto L_0x0e49;
    L_0x0e20:
        r6 = r5.messageOwner;
        r6 = r6.action;
        if (r6 == 0) goto L_0x0e49;
    L_0x0e26:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageEncryptedAction;
        if (r6 == 0) goto L_0x0e49;
    L_0x0e2e:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6.encryptedAction;
        r6 = r6 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
        if (r6 == 0) goto L_0x0e49;
    L_0x0e38:
        r0 = r23;
        r11 = r0.avatarContainer;
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6.encryptedAction;
        r6 = (com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL) r6;
        r6 = r6.ttl_seconds;
        r11.setTime(r6);
    L_0x0e49:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r6 == 0) goto L_0x0e9a;
    L_0x0e51:
        r6 = new android.os.Bundle;
        r6.<init>();
        r4 = "chat_id";
        r7 = r5.messageOwner;
        r7 = r7.action;
        r7 = r7.channel_id;
        r6.putInt(r4, r7);
        r0 = r23;
        r4 = r0.parentLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        if (r4 <= 0) goto L_0x0e98;
    L_0x0e6e:
        r0 = r23;
        r4 = r0.parentLayout;
        r4 = r4.fragmentsStack;
        r0 = r23;
        r7 = r0.parentLayout;
        r7 = r7.fragmentsStack;
        r7 = r7.size();
        r7 = r7 + -1;
        r4 = r4.get(r7);
        r4 = (com.hanista.mobogram.ui.ActionBar.BaseFragment) r4;
    L_0x0e86:
        r5 = r5.messageOwner;
        r5 = r5.action;
        r5 = r5.channel_id;
        r7 = new com.hanista.mobogram.ui.ChatActivity$63;
        r0 = r23;
        r7.<init>(r4, r6, r5);
        com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r7);
        goto L_0x0053;
    L_0x0e98:
        r4 = 0;
        goto L_0x0e86;
    L_0x0e9a:
        r0 = r23;
        r6 = r0.currentChat;
        if (r6 == 0) goto L_0x0eb9;
    L_0x0ea0:
        r0 = r23;
        r6 = r0.currentChat;
        r6 = r6.megagroup;
        if (r6 == 0) goto L_0x0eb9;
    L_0x0ea8:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r6 != 0) goto L_0x0eb8;
    L_0x0eb0:
        r6 = r5.messageOwner;
        r6 = r6.action;
        r6 = r6 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r6 == 0) goto L_0x0eb9;
    L_0x0eb8:
        r13 = 1;
    L_0x0eb9:
        r6 = r5.isOut();
        if (r6 == 0) goto L_0x0ecd;
    L_0x0ebf:
        r6 = r5.isSending();
        if (r6 == 0) goto L_0x0ecd;
    L_0x0ec5:
        r4 = 0;
        r0 = r23;
        r0.scrollToLastMessage(r4);
        goto L_0x0053;
    L_0x0ecd:
        r6 = r5.type;
        if (r6 < 0) goto L_0x2872;
    L_0x0ed1:
        r0 = r23;
        r6 = r0.messagesDict;
        r11 = 0;
        r6 = r6[r11];
        r11 = r5.getId();
        r11 = java.lang.Integer.valueOf(r11);
        r6 = r6.containsKey(r11);
        if (r6 == 0) goto L_0x0eeb;
    L_0x0ee6:
        r6 = r8;
        r11 = r14;
        r8 = r13;
        goto L_0x0d98;
    L_0x0eeb:
        r5.checkLayout();
        r6 = r5.messageOwner;
        r6 = r6.date;
        r10 = java.lang.Math.max(r10, r6);
        r6 = r5.getId();
        if (r6 <= 0) goto L_0x0f3d;
    L_0x0efc:
        r6 = r5.getId();
        r9 = java.lang.Math.max(r6, r9);
        r0 = r23;
        r6 = r0.last_message_id;
        r11 = r5.getId();
        r6 = java.lang.Math.max(r6, r11);
        r0 = r23;
        r0.last_message_id = r6;
    L_0x0f14:
        r6 = r5.isOut();
        if (r6 != 0) goto L_0x0f2b;
    L_0x0f1a:
        r6 = r5.isUnread();
        if (r6 == 0) goto L_0x0f2b;
    L_0x0f20:
        r0 = r23;
        r6 = r0.unread_to_load;
        r6 = r6 + 1;
        r0 = r23;
        r0.unread_to_load = r6;
        r8 = 1;
    L_0x0f2b:
        r6 = r5.type;
        r11 = 10;
        if (r6 == r11) goto L_0x0f37;
    L_0x0f31:
        r5 = r5.type;
        r6 = 11;
        if (r5 != r6) goto L_0x2872;
    L_0x0f37:
        r14 = 1;
        r6 = r8;
        r11 = r14;
        r8 = r13;
        goto L_0x0d98;
    L_0x0f3d:
        r0 = r23;
        r6 = r0.currentEncryptedChat;
        if (r6 == 0) goto L_0x0f14;
    L_0x0f43:
        r6 = r5.getId();
        r9 = java.lang.Math.min(r6, r9);
        r0 = r23;
        r6 = r0.last_message_id;
        r11 = r5.getId();
        r6 = java.lang.Math.min(r6, r11);
        r0 = r23;
        r0.last_message_id = r6;
        goto L_0x0f14;
    L_0x0f5c:
        if (r8 == 0) goto L_0x0f71;
    L_0x0f5e:
        r0 = r23;
        r4 = r0.paused;
        if (r4 == 0) goto L_0x0fb2;
    L_0x0f64:
        r4 = 1;
        r0 = r23;
        r0.readWhenResume = r4;
        r0 = r23;
        r0.readWithDate = r10;
        r0 = r23;
        r0.readWithMid = r9;
    L_0x0f71:
        r23.updateVisibleRows();
        r15 = r14;
    L_0x0f75:
        r0 = r23;
        r4 = r0.messages;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x0f97;
    L_0x0f7f:
        r0 = r23;
        r4 = r0.botUser;
        if (r4 == 0) goto L_0x0f97;
    L_0x0f85:
        r0 = r23;
        r4 = r0.botUser;
        r4 = r4.length();
        if (r4 != 0) goto L_0x0f97;
    L_0x0f8f:
        r4 = 0;
        r0 = r23;
        r0.botUser = r4;
        r23.updateBottomOverlay();
    L_0x0f97:
        if (r15 == 0) goto L_0x0f9f;
    L_0x0f99:
        r23.updateTitle();
        r23.checkAndUpdateAvatar();
    L_0x0f9f:
        if (r13 == 0) goto L_0x0053;
    L_0x0fa1:
        r4 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r23;
        r5 = r0.currentChat;
        r5 = r5.id;
        r6 = 0;
        r7 = 1;
        r4.loadFullChat(r5, r6, r7);
        goto L_0x0053;
    L_0x0fb2:
        r0 = r23;
        r4 = r0.messages;
        r4 = r4.size();
        if (r4 <= 0) goto L_0x0f71;
    L_0x0fbc:
        r5 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r23;
        r6 = r0.dialog_id;
        r0 = r23;
        r4 = r0.messages;
        r8 = 0;
        r4 = r4.get(r8);
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        r8 = r4.getId();
        r11 = 1;
        r12 = 0;
        r5.markDialogAsRead(r6, r8, r9, r10, r11, r12);
        goto L_0x0f71;
    L_0x0fd9:
        r14 = 0;
        r12 = 1;
        r0 = r23;
        r5 = r0.messages;
        r17 = r5.size();
        r10 = 0;
        r7 = 0;
        r6 = -1;
        r5 = 0;
        r16 = r5;
        r9 = r13;
        r15 = r11;
        r11 = r6;
        r13 = r8;
    L_0x0fed:
        r5 = r4.size();
        r0 = r16;
        if (r0 >= r5) goto L_0x13f2;
    L_0x0ff5:
        r0 = r16;
        r5 = r4.get(r0);
        r6 = r5;
        r6 = (com.hanista.mobogram.messenger.MessageObject) r6;
        r5 = r6.messageOwner;
        if (r5 == 0) goto L_0x1019;
    L_0x1002:
        r0 = r23;
        r5 = r0.justFromId;
        if (r5 <= 0) goto L_0x1019;
    L_0x1008:
        r5 = r6.messageOwner;
        r5 = r5.from_id;
        r0 = r23;
        r8 = r0.justFromId;
        if (r5 == r8) goto L_0x1019;
    L_0x1012:
        r5 = r15;
    L_0x1013:
        r6 = r16 + 1;
        r16 = r6;
        r15 = r5;
        goto L_0x0fed;
    L_0x1019:
        if (r16 != 0) goto L_0x1022;
    L_0x101b:
        r5 = r6.messageOwner;
        r5 = r5.id;
        if (r5 >= 0) goto L_0x1089;
    L_0x1021:
        r11 = 0;
    L_0x1022:
        r0 = r23;
        r5 = r0.currentUser;
        if (r5 == 0) goto L_0x1039;
    L_0x1028:
        r0 = r23;
        r5 = r0.currentUser;
        r5 = r5.bot;
        if (r5 == 0) goto L_0x1039;
    L_0x1030:
        r5 = r6.isOut();
        if (r5 == 0) goto L_0x1039;
    L_0x1036:
        r6.setIsRead();
    L_0x1039:
        r0 = r23;
        r5 = r0.avatarContainer;
        if (r5 == 0) goto L_0x106e;
    L_0x103f:
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        if (r5 == 0) goto L_0x106e;
    L_0x1045:
        r5 = r6.messageOwner;
        r5 = r5.action;
        if (r5 == 0) goto L_0x106e;
    L_0x104b:
        r5 = r6.messageOwner;
        r5 = r5.action;
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageEncryptedAction;
        if (r5 == 0) goto L_0x106e;
    L_0x1053:
        r5 = r6.messageOwner;
        r5 = r5.action;
        r5 = r5.encryptedAction;
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
        if (r5 == 0) goto L_0x106e;
    L_0x105d:
        r0 = r23;
        r8 = r0.avatarContainer;
        r5 = r6.messageOwner;
        r5 = r5.action;
        r5 = r5.encryptedAction;
        r5 = (com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL) r5;
        r5 = r5.ttl_seconds;
        r8.setTime(r5);
    L_0x106e:
        r5 = r6.type;
        if (r5 < 0) goto L_0x2861;
    L_0x1072:
        r0 = r23;
        r5 = r0.messagesDict;
        r8 = 0;
        r5 = r5[r8];
        r8 = r6.getId();
        r8 = java.lang.Integer.valueOf(r8);
        r5 = r5.containsKey(r8);
        if (r5 == 0) goto L_0x1119;
    L_0x1087:
        r5 = r15;
        goto L_0x1013;
    L_0x1089:
        r0 = r23;
        r5 = r0.messages;
        r5 = r5.isEmpty();
        if (r5 != 0) goto L_0x1116;
    L_0x1093:
        r0 = r23;
        r5 = r0.messages;
        r18 = r5.size();
        r8 = 0;
    L_0x109c:
        r0 = r18;
        if (r8 >= r0) goto L_0x2864;
    L_0x10a0:
        r0 = r23;
        r5 = r0.messages;
        r5 = r5.get(r8);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        r0 = r5.type;
        r19 = r0;
        if (r19 < 0) goto L_0x1113;
    L_0x10b0:
        r0 = r5.messageOwner;
        r19 = r0;
        r0 = r19;
        r0 = r0.date;
        r19 = r0;
        if (r19 <= 0) goto L_0x1113;
    L_0x10bc:
        r0 = r5.messageOwner;
        r19 = r0;
        r0 = r19;
        r0 = r0.id;
        r19 = r0;
        if (r19 <= 0) goto L_0x10ff;
    L_0x10c8:
        r0 = r6.messageOwner;
        r19 = r0;
        r0 = r19;
        r0 = r0.id;
        r19 = r0;
        if (r19 <= 0) goto L_0x10ff;
    L_0x10d4:
        r5 = r5.messageOwner;
        r5 = r5.id;
        r0 = r6.messageOwner;
        r19 = r0;
        r0 = r19;
        r0 = r0.id;
        r19 = r0;
        r0 = r19;
        if (r5 >= r0) goto L_0x1113;
    L_0x10e6:
        r5 = r8;
    L_0x10e7:
        r8 = -1;
        if (r5 == r8) goto L_0x10f4;
    L_0x10ea:
        r0 = r23;
        r8 = r0.messages;
        r8 = r8.size();
        if (r5 <= r8) goto L_0x10fc;
    L_0x10f4:
        r0 = r23;
        r5 = r0.messages;
        r5 = r5.size();
    L_0x10fc:
        r11 = r5;
        goto L_0x1022;
    L_0x10ff:
        r5 = r5.messageOwner;
        r5 = r5.date;
        r0 = r6.messageOwner;
        r19 = r0;
        r0 = r19;
        r0 = r0.date;
        r19 = r0;
        r0 = r19;
        if (r5 >= r0) goto L_0x1113;
    L_0x1111:
        r5 = r8;
        goto L_0x10e7;
    L_0x1113:
        r8 = r8 + 1;
        goto L_0x109c;
    L_0x1116:
        r11 = 0;
        goto L_0x1022;
    L_0x1119:
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        if (r5 == 0) goto L_0x115c;
    L_0x111f:
        r5 = r6.messageOwner;
        r5 = r5.media;
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r5 == 0) goto L_0x115c;
    L_0x1127:
        r5 = r6.messageOwner;
        r5 = r5.media;
        r5 = r5.webpage;
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_webPageUrlPending;
        if (r5 == 0) goto L_0x115c;
    L_0x1131:
        if (r7 != 0) goto L_0x1139;
    L_0x1133:
        r5 = new java.util.HashMap;
        r5.<init>();
        r7 = r5;
    L_0x1139:
        r5 = r6.messageOwner;
        r5 = r5.media;
        r5 = r5.webpage;
        r5 = r5.url;
        r5 = r7.get(r5);
        r5 = (java.util.ArrayList) r5;
        if (r5 != 0) goto L_0x1159;
    L_0x1149:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r8 = r6.messageOwner;
        r8 = r8.media;
        r8 = r8.webpage;
        r8 = r8.url;
        r7.put(r8, r5);
    L_0x1159:
        r5.add(r6);
    L_0x115c:
        r6.checkLayout();
        r5 = r6.messageOwner;
        r5 = r5.action;
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r5 == 0) goto L_0x11b0;
    L_0x1167:
        r5 = new android.os.Bundle;
        r5.<init>();
        r4 = "chat_id";
        r7 = r6.messageOwner;
        r7 = r7.action;
        r7 = r7.channel_id;
        r5.putInt(r4, r7);
        r0 = r23;
        r4 = r0.parentLayout;
        r4 = r4.fragmentsStack;
        r4 = r4.size();
        if (r4 <= 0) goto L_0x11ae;
    L_0x1184:
        r0 = r23;
        r4 = r0.parentLayout;
        r4 = r4.fragmentsStack;
        r0 = r23;
        r7 = r0.parentLayout;
        r7 = r7.fragmentsStack;
        r7 = r7.size();
        r7 = r7 + -1;
        r4 = r4.get(r7);
        r4 = (com.hanista.mobogram.ui.ActionBar.BaseFragment) r4;
    L_0x119c:
        r6 = r6.messageOwner;
        r6 = r6.action;
        r6 = r6.channel_id;
        r7 = new com.hanista.mobogram.ui.ChatActivity$64;
        r0 = r23;
        r7.<init>(r4, r5, r6);
        com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r7);
        goto L_0x0053;
    L_0x11ae:
        r4 = 0;
        goto L_0x119c;
    L_0x11b0:
        r0 = r23;
        r5 = r0.currentChat;
        if (r5 == 0) goto L_0x286f;
    L_0x11b6:
        r0 = r23;
        r5 = r0.currentChat;
        r5 = r5.megagroup;
        if (r5 == 0) goto L_0x286f;
    L_0x11be:
        r5 = r6.messageOwner;
        r5 = r5.action;
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r5 != 0) goto L_0x11ce;
    L_0x11c6:
        r5 = r6.messageOwner;
        r5 = r5.action;
        r5 = r5 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r5 == 0) goto L_0x286f;
    L_0x11ce:
        r5 = 1;
        r8 = r5;
    L_0x11d0:
        r0 = r23;
        r5 = r0.minDate;
        r13 = 0;
        r5 = r5[r13];
        if (r5 == 0) goto L_0x11e7;
    L_0x11d9:
        r5 = r6.messageOwner;
        r5 = r5.date;
        r0 = r23;
        r13 = r0.minDate;
        r18 = 0;
        r13 = r13[r18];
        if (r5 >= r13) goto L_0x11f8;
    L_0x11e7:
        r0 = r23;
        r5 = r0.minDate;
        r13 = 0;
        r0 = r6.messageOwner;
        r18 = r0;
        r0 = r18;
        r0 = r0.date;
        r18 = r0;
        r5[r13] = r18;
    L_0x11f8:
        r5 = r6.isOut();
        if (r5 == 0) goto L_0x1203;
    L_0x11fe:
        r23.removeUnreadPlane();
        r5 = 1;
        r9 = r5;
    L_0x1203:
        r5 = r6.getId();
        if (r5 <= 0) goto L_0x13b8;
    L_0x1209:
        r0 = r23;
        r5 = r0.maxMessageId;
        r13 = 0;
        r18 = r6.getId();
        r0 = r23;
        r0 = r0.maxMessageId;
        r19 = r0;
        r20 = 0;
        r19 = r19[r20];
        r18 = java.lang.Math.min(r18, r19);
        r5[r13] = r18;
        r0 = r23;
        r5 = r0.minMessageId;
        r13 = 0;
        r18 = r6.getId();
        r0 = r23;
        r0 = r0.minMessageId;
        r19 = r0;
        r20 = 0;
        r19 = r19[r20];
        r18 = java.lang.Math.max(r18, r19);
        r5[r13] = r18;
    L_0x123b:
        r0 = r23;
        r5 = r0.maxDate;
        r13 = 0;
        r0 = r23;
        r0 = r0.maxDate;
        r18 = r0;
        r19 = 0;
        r18 = r18[r19];
        r0 = r6.messageOwner;
        r19 = r0;
        r0 = r19;
        r0 = r0.date;
        r19 = r0;
        r18 = java.lang.Math.max(r18, r19);
        r5[r13] = r18;
        r0 = r23;
        r5 = r0.messagesDict;
        r13 = 0;
        r5 = r5[r13];
        r13 = r6.getId();
        r13 = java.lang.Integer.valueOf(r13);
        r5.put(r13, r6);
        r0 = r23;
        r5 = r0.messagesByDays;
        r13 = r6.dateKey;
        r5 = r5.get(r13);
        r5 = (java.util.ArrayList) r5;
        if (r5 != 0) goto L_0x12e1;
    L_0x127a:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r0 = r23;
        r13 = r0.messagesByDays;
        r0 = r6.dateKey;
        r18 = r0;
        r0 = r18;
        r13.put(r0, r5);
        r13 = new com.hanista.mobogram.tgnet.TLRPC$Message;
        r13.<init>();
        r0 = r6.messageOwner;
        r18 = r0;
        r0 = r18;
        r0 = r0.date;
        r18 = r0;
        r0 = r18;
        r0 = (long) r0;
        r18 = r0;
        r18 = com.hanista.mobogram.messenger.LocaleController.formatDateChat(r18);
        r0 = r18;
        r13.message = r0;
        r18 = 0;
        r0 = r18;
        r13.id = r0;
        r0 = r6.messageOwner;
        r18 = r0;
        r0 = r18;
        r0 = r0.date;
        r18 = r0;
        r0 = r18;
        r13.date = r0;
        r18 = new com.hanista.mobogram.messenger.MessageObject;
        r19 = 0;
        r20 = 0;
        r0 = r18;
        r1 = r19;
        r2 = r20;
        r0.<init>(r13, r1, r2);
        r13 = 10;
        r0 = r18;
        r0.type = r13;
        r13 = 1;
        r0 = r18;
        r0.contentType = r13;
        r0 = r23;
        r13 = r0.messages;
        r0 = r18;
        r13.add(r11, r0);
        r10 = r10 + 1;
    L_0x12e1:
        r13 = r6.isOut();
        if (r13 != 0) goto L_0x286c;
    L_0x12e7:
        r0 = r23;
        r13 = r0.paused;
        if (r13 == 0) goto L_0x135d;
    L_0x12ed:
        if (r11 != 0) goto L_0x135d;
    L_0x12ef:
        r0 = r23;
        r13 = r0.scrollToTopUnReadOnResume;
        if (r13 != 0) goto L_0x130d;
    L_0x12f5:
        r0 = r23;
        r13 = r0.unreadMessageObject;
        if (r13 == 0) goto L_0x130d;
    L_0x12fb:
        r0 = r23;
        r13 = r0.unreadMessageObject;
        r0 = r23;
        r0.removeMessageObject(r13);
        if (r11 <= 0) goto L_0x1308;
    L_0x1306:
        r11 = r11 + -1;
    L_0x1308:
        r13 = 0;
        r0 = r23;
        r0.unreadMessageObject = r13;
    L_0x130d:
        r0 = r23;
        r13 = r0.unreadMessageObject;
        if (r13 != 0) goto L_0x135d;
    L_0x1313:
        r12 = new com.hanista.mobogram.tgnet.TLRPC$Message;
        r12.<init>();
        r13 = "";
        r12.message = r13;
        r13 = 0;
        r12.id = r13;
        r13 = new com.hanista.mobogram.messenger.MessageObject;
        r18 = 0;
        r19 = 0;
        r0 = r18;
        r1 = r19;
        r13.<init>(r12, r0, r1);
        r12 = 6;
        r13.type = r12;
        r12 = 2;
        r13.contentType = r12;
        r0 = r23;
        r12 = r0.messages;
        r18 = 0;
        r0 = r18;
        r12.add(r0, r13);
        r0 = r23;
        r0.unreadMessageObject = r13;
        r0 = r23;
        r12 = r0.unreadMessageObject;
        r0 = r23;
        r0.scrollToMessage = r12;
        r12 = -10000; // 0xffffffffffffd8f0 float:NaN double:NaN;
        r0 = r23;
        r0.scrollToMessagePosition = r12;
        r12 = 0;
        r13 = 0;
        r0 = r23;
        r0.unread_to_load = r13;
        r13 = 1;
        r0 = r23;
        r0.scrollToTopUnReadOnResume = r13;
        r10 = r10 + 1;
    L_0x135d:
        r0 = r23;
        r13 = r0.unreadMessageObject;
        if (r13 == 0) goto L_0x136e;
    L_0x1363:
        r0 = r23;
        r12 = r0.unread_to_load;
        r12 = r12 + 1;
        r0 = r23;
        r0.unread_to_load = r12;
        r12 = 1;
    L_0x136e:
        r13 = r6.isUnread();
        if (r13 == 0) goto L_0x286c;
    L_0x1374:
        r0 = r23;
        r13 = r0.paused;
        if (r13 != 0) goto L_0x137d;
    L_0x137a:
        r6.setIsRead();
    L_0x137d:
        r13 = 1;
    L_0x137e:
        r14 = 0;
        r5.add(r14, r6);
        r0 = r23;
        r5 = r0.messages;
        r5 = r5.size();
        if (r11 <= r5) goto L_0x1394;
    L_0x138c:
        r0 = r23;
        r5 = r0.messages;
        r11 = r5.size();
    L_0x1394:
        r0 = r23;
        r5 = r0.messages;
        r5.add(r11, r6);
        r10 = r10 + 1;
        r0 = r23;
        r5 = r0.newUnreadMessageCount;
        r5 = r5 + 1;
        r0 = r23;
        r0.newUnreadMessageCount = r5;
        r5 = r6.type;
        r14 = 10;
        if (r5 == r14) goto L_0x13b3;
    L_0x13ad:
        r5 = r6.type;
        r6 = 11;
        if (r5 != r6) goto L_0x2867;
    L_0x13b3:
        r5 = 1;
        r14 = r13;
        r13 = r8;
        goto L_0x1013;
    L_0x13b8:
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        if (r5 == 0) goto L_0x123b;
    L_0x13be:
        r0 = r23;
        r5 = r0.maxMessageId;
        r13 = 0;
        r18 = r6.getId();
        r0 = r23;
        r0 = r0.maxMessageId;
        r19 = r0;
        r20 = 0;
        r19 = r19[r20];
        r18 = java.lang.Math.max(r18, r19);
        r5[r13] = r18;
        r0 = r23;
        r5 = r0.minMessageId;
        r13 = 0;
        r18 = r6.getId();
        r0 = r23;
        r0 = r0.minMessageId;
        r19 = r0;
        r20 = 0;
        r19 = r19[r20];
        r18 = java.lang.Math.min(r18, r19);
        r5[r13] = r18;
        goto L_0x123b;
    L_0x13f2:
        if (r7 == 0) goto L_0x1403;
    L_0x13f4:
        r4 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r23;
        r0 = r0.dialog_id;
        r18 = r0;
        r0 = r18;
        r4.reloadWebPages(r0, r7);
    L_0x1403:
        r0 = r23;
        r4 = r0.progressView;
        if (r4 == 0) goto L_0x1411;
    L_0x1409:
        r0 = r23;
        r4 = r0.progressView;
        r5 = 4;
        r4.setVisibility(r5);
    L_0x1411:
        r0 = r23;
        r4 = r0.chatAdapter;
        if (r4 == 0) goto L_0x14a4;
    L_0x1417:
        if (r12 == 0) goto L_0x1424;
    L_0x1419:
        r0 = r23;
        r4 = r0.chatAdapter;
        r0 = r23;
        r5 = r0.unreadMessageObject;
        r4.updateRowWithMessageObject(r5);
    L_0x1424:
        if (r10 == 0) goto L_0x1436;
    L_0x1426:
        r0 = r23;
        r4 = r0.chatAdapter;
        r0 = r23;
        r5 = r0.chatAdapter;
        r5 = r5.getItemCount();
        r5 = r5 - r11;
        r4.notifyItemRangeInserted(r5, r10);
    L_0x1436:
        r0 = r23;
        r4 = r0.chatListView;
        if (r4 == 0) goto L_0x14eb;
    L_0x143c:
        r0 = r23;
        r4 = r0.chatAdapter;
        if (r4 == 0) goto L_0x14eb;
    L_0x1442:
        r0 = r23;
        r4 = r0.chatLayoutManager;
        r4 = r4.findLastVisibleItemPosition();
        r5 = -1;
        if (r4 != r5) goto L_0x144e;
    L_0x144d:
        r4 = 0;
    L_0x144e:
        r0 = r23;
        r5 = r0.endReached;
        r6 = 0;
        r5 = r5[r6];
        if (r5 == 0) goto L_0x1459;
    L_0x1457:
        r4 = r4 + 1;
    L_0x1459:
        r0 = r23;
        r5 = r0.chatAdapter;
        r5 = r5.isBot;
        if (r5 == 0) goto L_0x285d;
    L_0x1463:
        r5 = r17 + 1;
    L_0x1465:
        if (r4 >= r5) goto L_0x1469;
    L_0x1467:
        if (r9 == 0) goto L_0x14b3;
    L_0x1469:
        r4 = 0;
        r0 = r23;
        r0.newUnreadMessageCount = r4;
        r0 = r23;
        r4 = r0.firstLoading;
        if (r4 != 0) goto L_0x147f;
    L_0x1474:
        r0 = r23;
        r4 = r0.paused;
        if (r4 == 0) goto L_0x14aa;
    L_0x147a:
        r4 = 1;
        r0 = r23;
        r0.scrollToTopOnResume = r4;
    L_0x147f:
        if (r14 == 0) goto L_0x0f75;
    L_0x1481:
        r0 = r23;
        r4 = r0.paused;
        if (r4 == 0) goto L_0x14f1;
    L_0x1487:
        r4 = 1;
        r0 = r23;
        r0.readWhenResume = r4;
        r0 = r23;
        r4 = r0.maxDate;
        r5 = 0;
        r4 = r4[r5];
        r0 = r23;
        r0.readWithDate = r4;
        r0 = r23;
        r4 = r0.minMessageId;
        r5 = 0;
        r4 = r4[r5];
        r0 = r23;
        r0.readWithMid = r4;
        goto L_0x0f75;
    L_0x14a4:
        r4 = 1;
        r0 = r23;
        r0.scrollToTopOnResume = r4;
        goto L_0x1436;
    L_0x14aa:
        r4 = 1;
        r0 = r23;
        r0.forceScrollToTop = r4;
        r23.moveScrollToLastMessage();
        goto L_0x147f;
    L_0x14b3:
        r0 = r23;
        r4 = r0.newUnreadMessageCount;
        if (r4 == 0) goto L_0x14e3;
    L_0x14b9:
        r0 = r23;
        r4 = r0.pagedownButtonCounter;
        if (r4 == 0) goto L_0x14e3;
    L_0x14bf:
        r0 = r23;
        r4 = r0.pagedownButtonCounter;
        r5 = 0;
        r4.setVisibility(r5);
        r0 = r23;
        r4 = r0.pagedownButtonCounter;
        r5 = "%d";
        r6 = 1;
        r6 = new java.lang.Object[r6];
        r7 = 0;
        r0 = r23;
        r8 = r0.newUnreadMessageCount;
        r8 = java.lang.Integer.valueOf(r8);
        r6[r7] = r8;
        r5 = java.lang.String.format(r5, r6);
        r4.setText(r5);
    L_0x14e3:
        r4 = 1;
        r5 = 1;
        r0 = r23;
        r0.showPagedownButton(r4, r5);
        goto L_0x147f;
    L_0x14eb:
        r4 = 1;
        r0 = r23;
        r0.scrollToTopOnResume = r4;
        goto L_0x147f;
    L_0x14f1:
        r5 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r23;
        r6 = r0.dialog_id;
        r0 = r23;
        r4 = r0.messages;
        r8 = 0;
        r4 = r4.get(r8);
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        r8 = r4.getId();
        r0 = r23;
        r4 = r0.minMessageId;
        r9 = 0;
        r9 = r4[r9];
        r0 = r23;
        r4 = r0.maxDate;
        r10 = 0;
        r10 = r4[r10];
        r11 = 1;
        r12 = 0;
        r5.markDialogAsRead(r6, r8, r9, r10, r11, r12);
        goto L_0x0f75;
    L_0x151d:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.closeChats;
        r0 = r24;
        if (r0 != r4) goto L_0x1545;
    L_0x1523:
        if (r25 == 0) goto L_0x1540;
    L_0x1525:
        r0 = r25;
        r4 = r0.length;
        if (r4 <= 0) goto L_0x1540;
    L_0x152a:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Long) r4;
        r4 = r4.longValue();
        r0 = r23;
        r6 = r0.dialog_id;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x0053;
    L_0x153b:
        r23.finishFragment();
        goto L_0x0053;
    L_0x1540:
        r23.removeSelfFromStack();
        goto L_0x0053;
    L_0x1545:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.messagesRead;
        r0 = r24;
        if (r0 != r4) goto L_0x1612;
    L_0x154b:
        r4 = 0;
        r4 = r25[r4];
        r4 = (android.util.SparseArray) r4;
        r5 = 1;
        r5 = r25[r5];
        r5 = (android.util.SparseArray) r5;
        r8 = 0;
        r6 = 0;
        r7 = r6;
    L_0x1558:
        r6 = r4.size();
        if (r7 >= r6) goto L_0x285a;
    L_0x155e:
        r9 = r4.keyAt(r7);
        r6 = r4.get(r9);
        r6 = (java.lang.Long) r6;
        r10 = r6.longValue();
        r12 = (long) r9;
        r0 = r23;
        r14 = r0.dialog_id;
        r6 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r6 == 0) goto L_0x1579;
    L_0x1575:
        r6 = r7 + 1;
        r7 = r6;
        goto L_0x1558;
    L_0x1579:
        r4 = 0;
        r6 = r4;
        r7 = r8;
    L_0x157c:
        r0 = r23;
        r4 = r0.messages;
        r4 = r4.size();
        if (r6 >= r4) goto L_0x15a9;
    L_0x1586:
        r0 = r23;
        r4 = r0.messages;
        r4 = r4.get(r6);
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        r8 = r4.isOut();
        if (r8 != 0) goto L_0x15d1;
    L_0x1596:
        r8 = r4.getId();
        if (r8 <= 0) goto L_0x15d1;
    L_0x159c:
        r8 = r4.getId();
        r9 = (int) r10;
        if (r8 > r9) goto L_0x15d1;
    L_0x15a3:
        r8 = r4.isUnread();
        if (r8 != 0) goto L_0x15cd;
    L_0x15a9:
        r4 = 0;
        r6 = r4;
    L_0x15ab:
        r4 = r5.size();
        if (r6 >= r4) goto L_0x1603;
    L_0x15b1:
        r8 = r5.keyAt(r6);
        r4 = r5.get(r8);
        r4 = (java.lang.Long) r4;
        r10 = r4.longValue();
        r9 = (int) r10;
        r10 = (long) r8;
        r0 = r23;
        r12 = r0.dialog_id;
        r4 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r4 == 0) goto L_0x15d5;
    L_0x15c9:
        r4 = r6 + 1;
        r6 = r4;
        goto L_0x15ab;
    L_0x15cd:
        r4.setIsRead();
        r7 = 1;
    L_0x15d1:
        r4 = r6 + 1;
        r6 = r4;
        goto L_0x157c;
    L_0x15d5:
        r4 = 0;
        r5 = r4;
    L_0x15d7:
        r0 = r23;
        r4 = r0.messages;
        r4 = r4.size();
        if (r5 >= r4) goto L_0x1603;
    L_0x15e1:
        r0 = r23;
        r4 = r0.messages;
        r4 = r4.get(r5);
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        r6 = r4.isOut();
        if (r6 == 0) goto L_0x160e;
    L_0x15f1:
        r6 = r4.getId();
        if (r6 <= 0) goto L_0x160e;
    L_0x15f7:
        r6 = r4.getId();
        if (r6 > r9) goto L_0x160e;
    L_0x15fd:
        r6 = r4.isUnread();
        if (r6 != 0) goto L_0x160a;
    L_0x1603:
        if (r7 == 0) goto L_0x0053;
    L_0x1605:
        r23.updateVisibleRows();
        goto L_0x0053;
    L_0x160a:
        r4.setIsRead();
        r7 = 1;
    L_0x160e:
        r4 = r5 + 1;
        r5 = r4;
        goto L_0x15d7;
    L_0x1612:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.messagesDeleted;
        r0 = r24;
        if (r0 != r4) goto L_0x183c;
    L_0x1618:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.util.ArrayList) r4;
        r5 = 1;
        r5 = r25[r5];
        r5 = (java.lang.Integer) r5;
        r9 = r5.intValue();
        r5 = 0;
        r0 = r23;
        r6 = r0.currentChat;
        r6 = com.hanista.mobogram.messenger.ChatObject.isChannel(r6);
        if (r6 == 0) goto L_0x16f1;
    L_0x1631:
        if (r9 != 0) goto L_0x16e5;
    L_0x1633:
        r0 = r23;
        r6 = r0.mergeDialogId;
        r10 = 0;
        r5 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r5 == 0) goto L_0x16e5;
    L_0x163d:
        r5 = 1;
        r7 = r5;
    L_0x163f:
        r6 = 0;
        r5 = 0;
        r8 = r5;
        r17 = r6;
    L_0x1644:
        r5 = r4.size();
        if (r8 >= r5) goto L_0x16f6;
    L_0x164a:
        r5 = r4.get(r8);
        r5 = (java.lang.Integer) r5;
        r0 = r23;
        r6 = r0.messagesDict;
        r6 = r6[r7];
        r6 = r6.get(r5);
        r6 = (com.hanista.mobogram.messenger.MessageObject) r6;
        if (r7 != 0) goto L_0x168a;
    L_0x165e:
        r0 = r23;
        r10 = r0.info;
        if (r10 == 0) goto L_0x168a;
    L_0x1664:
        r0 = r23;
        r10 = r0.info;
        r10 = r10.pinned_msg_id;
        r11 = r5.intValue();
        if (r10 != r11) goto L_0x168a;
    L_0x1670:
        r10 = 0;
        r0 = r23;
        r0.pinnedMessageObject = r10;
        r0 = r23;
        r10 = r0.info;
        r11 = 0;
        r10.pinned_msg_id = r11;
        r10 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r11 = 0;
        r10.updateChannelPinnedMessage(r9, r11);
        r10 = 1;
        r0 = r23;
        r0.updatePinnedMessageView(r10);
    L_0x168a:
        if (r6 == 0) goto L_0x2856;
    L_0x168c:
        r0 = r23;
        r10 = r0.messages;
        r10 = r10.indexOf(r6);
        r11 = -1;
        if (r10 == r11) goto L_0x2856;
    L_0x1697:
        r0 = r23;
        r11 = r0.messages;
        r11.remove(r10);
        r0 = r23;
        r11 = r0.messagesDict;
        r11 = r11[r7];
        r11.remove(r5);
        r0 = r23;
        r5 = r0.messagesByDays;
        r11 = r6.dateKey;
        r5 = r5.get(r11);
        r5 = (java.util.ArrayList) r5;
        if (r5 == 0) goto L_0x16da;
    L_0x16b5:
        r5.remove(r6);
        r5 = r5.isEmpty();
        if (r5 == 0) goto L_0x16da;
    L_0x16be:
        r0 = r23;
        r5 = r0.messagesByDays;
        r6 = r6.dateKey;
        r5.remove(r6);
        if (r10 < 0) goto L_0x16da;
    L_0x16c9:
        r0 = r23;
        r5 = r0.messages;
        r5 = r5.size();
        if (r10 >= r5) goto L_0x16da;
    L_0x16d3:
        r0 = r23;
        r5 = r0.messages;
        r5.remove(r10);
    L_0x16da:
        r17 = 1;
        r6 = r17;
    L_0x16de:
        r5 = r8 + 1;
        r8 = r5;
        r17 = r6;
        goto L_0x1644;
    L_0x16e5:
        r0 = r23;
        r5 = r0.currentChat;
        r5 = r5.id;
        if (r9 != r5) goto L_0x0053;
    L_0x16ed:
        r5 = 0;
        r7 = r5;
        goto L_0x163f;
    L_0x16f1:
        if (r9 != 0) goto L_0x0053;
    L_0x16f3:
        r7 = r5;
        goto L_0x163f;
    L_0x16f6:
        r0 = r23;
        r4 = r0.messages;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x17c4;
    L_0x1700:
        r0 = r23;
        r4 = r0.endReached;
        r5 = 0;
        r4 = r4[r5];
        if (r4 != 0) goto L_0x17fd;
    L_0x1709:
        r0 = r23;
        r4 = r0.loading;
        if (r4 != 0) goto L_0x17fd;
    L_0x170f:
        r0 = r23;
        r4 = r0.progressView;
        if (r4 == 0) goto L_0x171d;
    L_0x1715:
        r0 = r23;
        r4 = r0.progressView;
        r5 = 4;
        r4.setVisibility(r5);
    L_0x171d:
        r0 = r23;
        r4 = r0.chatListView;
        if (r4 == 0) goto L_0x172b;
    L_0x1723:
        r0 = r23;
        r4 = r0.chatListView;
        r5 = 0;
        r4.setEmptyView(r5);
    L_0x172b:
        r0 = r23;
        r4 = r0.currentEncryptedChat;
        if (r4 != 0) goto L_0x17d8;
    L_0x1731:
        r0 = r23;
        r4 = r0.maxMessageId;
        r5 = 0;
        r0 = r23;
        r6 = r0.maxMessageId;
        r7 = 1;
        r8 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r6[r7] = r8;
        r4[r5] = r8;
        r0 = r23;
        r4 = r0.minMessageId;
        r5 = 0;
        r0 = r23;
        r6 = r0.minMessageId;
        r7 = 1;
        r8 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r6[r7] = r8;
        r4[r5] = r8;
    L_0x1752:
        r0 = r23;
        r4 = r0.maxDate;
        r5 = 0;
        r0 = r23;
        r6 = r0.maxDate;
        r7 = 1;
        r8 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r6[r7] = r8;
        r4[r5] = r8;
        r0 = r23;
        r4 = r0.minDate;
        r5 = 0;
        r0 = r23;
        r6 = r0.minDate;
        r7 = 1;
        r8 = 0;
        r6[r7] = r8;
        r4[r5] = r8;
        r0 = r23;
        r4 = r0.waitingForLoad;
        r0 = r23;
        r5 = r0.lastLoadIndex;
        r5 = java.lang.Integer.valueOf(r5);
        r4.add(r5);
        r5 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r23;
        r6 = r0.dialog_id;
        r8 = 30;
        r9 = 0;
        r0 = r23;
        r4 = r0.cacheEndReached;
        r10 = 0;
        r4 = r4[r10];
        if (r4 != 0) goto L_0x17fb;
    L_0x1794:
        r0 = r23;
        r4 = r0.gotoDateMessageSelected;
        if (r4 != 0) goto L_0x17fb;
    L_0x179a:
        r10 = 1;
    L_0x179b:
        r0 = r23;
        r4 = r0.minDate;
        r11 = 0;
        r11 = r4[r11];
        r0 = r23;
        r12 = r0.classGuid;
        r13 = 0;
        r14 = 0;
        r0 = r23;
        r4 = r0.currentChat;
        r15 = com.hanista.mobogram.messenger.ChatObject.isChannel(r4);
        r0 = r23;
        r0 = r0.lastLoadIndex;
        r16 = r0;
        r4 = r16 + 1;
        r0 = r23;
        r0.lastLoadIndex = r4;
        r5.loadMessages(r6, r8, r9, r10, r11, r12, r13, r14, r15, r16);
        r4 = 1;
        r0 = r23;
        r0.loading = r4;
    L_0x17c4:
        if (r17 == 0) goto L_0x0053;
    L_0x17c6:
        r0 = r23;
        r4 = r0.chatAdapter;
        if (r4 == 0) goto L_0x0053;
    L_0x17cc:
        r23.removeUnreadPlane();
        r0 = r23;
        r4 = r0.chatAdapter;
        r4.notifyDataSetChanged();
        goto L_0x0053;
    L_0x17d8:
        r0 = r23;
        r4 = r0.maxMessageId;
        r5 = 0;
        r0 = r23;
        r6 = r0.maxMessageId;
        r7 = 1;
        r8 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r6[r7] = r8;
        r4[r5] = r8;
        r0 = r23;
        r4 = r0.minMessageId;
        r5 = 0;
        r0 = r23;
        r6 = r0.minMessageId;
        r7 = 1;
        r8 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r6[r7] = r8;
        r4[r5] = r8;
        goto L_0x1752;
    L_0x17fb:
        r10 = 0;
        goto L_0x179b;
    L_0x17fd:
        r0 = r23;
        r4 = r0.botButtons;
        if (r4 == 0) goto L_0x1817;
    L_0x1803:
        r4 = 0;
        r0 = r23;
        r0.botButtons = r4;
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        if (r4 == 0) goto L_0x1817;
    L_0x180e:
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        r5 = 0;
        r6 = 0;
        r4.setButtons(r5, r6);
    L_0x1817:
        r0 = r23;
        r4 = r0.currentEncryptedChat;
        if (r4 != 0) goto L_0x17c4;
    L_0x181d:
        r0 = r23;
        r4 = r0.currentUser;
        if (r4 == 0) goto L_0x17c4;
    L_0x1823:
        r0 = r23;
        r4 = r0.currentUser;
        r4 = r4.bot;
        if (r4 == 0) goto L_0x17c4;
    L_0x182b:
        r0 = r23;
        r4 = r0.botUser;
        if (r4 != 0) goto L_0x17c4;
    L_0x1831:
        r4 = "";
        r0 = r23;
        r0.botUser = r4;
        r23.updateBottomOverlay();
        goto L_0x17c4;
    L_0x183c:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.messageReceivedByServer;
        r0 = r24;
        if (r0 != r4) goto L_0x19e6;
    L_0x1842:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r0 = r23;
        r5 = r0.messagesDict;
        r6 = 0;
        r5 = r5[r6];
        r5 = r5.get(r4);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        if (r5 == 0) goto L_0x0053;
    L_0x1856:
        r6 = 1;
        r6 = r25[r6];
        r6 = (java.lang.Integer) r6;
        r0 = r23;
        r7 = r0.archive;
        if (r7 == 0) goto L_0x187d;
    L_0x1861:
        r0 = r23;
        r8 = r0.dialog_id;
        r7 = com.hanista.mobogram.messenger.UserConfig.getClientUserId();
        r10 = (long) r7;
        r7 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r7 != 0) goto L_0x187d;
    L_0x186e:
        r0 = r23;
        r7 = r0.archive;
        r7 = r7.m204a();
        r8 = r6.intValue();
        com.hanista.mobogram.mobo.p000a.ArchiveUtil.m266b(r7, r8);
    L_0x187d:
        r7 = r6.equals(r4);
        if (r7 != 0) goto L_0x18ee;
    L_0x1883:
        r0 = r23;
        r7 = r0.messagesDict;
        r8 = 0;
        r7 = r7[r8];
        r7 = r7.containsKey(r6);
        if (r7 == 0) goto L_0x18ee;
    L_0x1890:
        r0 = r23;
        r6 = r0.messagesDict;
        r7 = 0;
        r6 = r6[r7];
        r4 = r6.remove(r4);
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        if (r4 == 0) goto L_0x0053;
    L_0x189f:
        r0 = r23;
        r6 = r0.messages;
        r6 = r6.indexOf(r4);
        r0 = r23;
        r7 = r0.messages;
        r7.remove(r6);
        r0 = r23;
        r7 = r0.messagesByDays;
        r4 = r4.dateKey;
        r4 = r7.get(r4);
        r4 = (java.util.ArrayList) r4;
        r4.remove(r5);
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x18df;
    L_0x18c3:
        r0 = r23;
        r4 = r0.messagesByDays;
        r5 = r5.dateKey;
        r4.remove(r5);
        if (r6 < 0) goto L_0x18df;
    L_0x18ce:
        r0 = r23;
        r4 = r0.messages;
        r4 = r4.size();
        if (r6 >= r4) goto L_0x18df;
    L_0x18d8:
        r0 = r23;
        r4 = r0.messages;
        r4.remove(r6);
    L_0x18df:
        r0 = r23;
        r4 = r0.chatAdapter;
        if (r4 == 0) goto L_0x0053;
    L_0x18e5:
        r0 = r23;
        r4 = r0.chatAdapter;
        r4.notifyDataSetChanged();
        goto L_0x0053;
    L_0x18ee:
        r7 = 2;
        r7 = r25[r7];
        r7 = (com.hanista.mobogram.tgnet.TLRPC.Message) r7;
        r10 = 0;
        r9 = 0;
        if (r7 == 0) goto L_0x2852;
    L_0x18f7:
        r8 = r5.isForwarded();	 Catch:{ Exception -> 0x19d7 }
        if (r8 == 0) goto L_0x19d1;
    L_0x18fd:
        r8 = r5.messageOwner;	 Catch:{ Exception -> 0x19d7 }
        r8 = r8.reply_markup;	 Catch:{ Exception -> 0x19d7 }
        if (r8 != 0) goto L_0x1907;
    L_0x1903:
        r8 = r7.reply_markup;	 Catch:{ Exception -> 0x19d7 }
        if (r8 != 0) goto L_0x1913;
    L_0x1907:
        r8 = r5.messageOwner;	 Catch:{ Exception -> 0x19d7 }
        r8 = r8.message;	 Catch:{ Exception -> 0x19d7 }
        r11 = r7.message;	 Catch:{ Exception -> 0x19d7 }
        r8 = r8.equals(r11);	 Catch:{ Exception -> 0x19d7 }
        if (r8 != 0) goto L_0x19d1;
    L_0x1913:
        r9 = 1;
    L_0x1914:
        if (r9 != 0) goto L_0x1947;
    L_0x1916:
        r8 = r5.messageOwner;	 Catch:{ Exception -> 0x19d7 }
        r8 = r8.params;	 Catch:{ Exception -> 0x19d7 }
        if (r8 == 0) goto L_0x1929;
    L_0x191c:
        r8 = r5.messageOwner;	 Catch:{ Exception -> 0x19d7 }
        r8 = r8.params;	 Catch:{ Exception -> 0x19d7 }
        r11 = "query_id";
        r8 = r8.containsKey(r11);	 Catch:{ Exception -> 0x19d7 }
        if (r8 != 0) goto L_0x1947;
    L_0x1929:
        r8 = r7.media;	 Catch:{ Exception -> 0x19d7 }
        if (r8 == 0) goto L_0x19d4;
    L_0x192d:
        r8 = r5.messageOwner;	 Catch:{ Exception -> 0x19d7 }
        r8 = r8.media;	 Catch:{ Exception -> 0x19d7 }
        if (r8 == 0) goto L_0x19d4;
    L_0x1933:
        r8 = r7.media;	 Catch:{ Exception -> 0x19d7 }
        r8 = r8.getClass();	 Catch:{ Exception -> 0x19d7 }
        r11 = r5.messageOwner;	 Catch:{ Exception -> 0x19d7 }
        r11 = r11.media;	 Catch:{ Exception -> 0x19d7 }
        r11 = r11.getClass();	 Catch:{ Exception -> 0x19d7 }
        r8 = r8.equals(r11);	 Catch:{ Exception -> 0x19d7 }
        if (r8 != 0) goto L_0x19d4;
    L_0x1947:
        r8 = 1;
    L_0x1948:
        r22 = r9;
        r9 = r8;
        r8 = r22;
    L_0x194d:
        r5.messageOwner = r7;
        r10 = 1;
        r5.generateThumbs(r10);
        r5.setType();
        r7 = r7.media;
        r7 = r7 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGame;
        if (r7 == 0) goto L_0x195f;
    L_0x195c:
        r5.applyNewText();
    L_0x195f:
        if (r8 == 0) goto L_0x1964;
    L_0x1961:
        r5.measureInlineBotButtons();
    L_0x1964:
        r0 = r23;
        r7 = r0.messagesDict;
        r8 = 0;
        r7 = r7[r8];
        r7.remove(r4);
        r0 = r23;
        r4 = r0.messagesDict;
        r7 = 0;
        r4 = r4[r7];
        r4.put(r6, r5);
        r4 = r5.messageOwner;
        r6 = r6.intValue();
        r4.id = r6;
        r4 = r5.messageOwner;
        r6 = 0;
        r4.send_state = r6;
        r5.forceUpdate = r9;
        r4 = new java.util.ArrayList;
        r4.<init>();
        r4.add(r5);
        r0 = r23;
        r6 = r0.currentEncryptedChat;
        if (r6 != 0) goto L_0x199c;
    L_0x1995:
        r0 = r23;
        r6 = r0.dialog_id;
        com.hanista.mobogram.messenger.query.MessagesQuery.loadReplyMessagesForMessages(r4, r6);
    L_0x199c:
        r0 = r23;
        r4 = r0.chatAdapter;
        if (r4 == 0) goto L_0x19a9;
    L_0x19a2:
        r0 = r23;
        r4 = r0.chatAdapter;
        r4.updateRowWithMessageObject(r5);
    L_0x19a9:
        r0 = r23;
        r4 = r0.chatLayoutManager;
        if (r4 == 0) goto L_0x19c8;
    L_0x19af:
        if (r9 == 0) goto L_0x19c8;
    L_0x19b1:
        r0 = r23;
        r4 = r0.chatLayoutManager;
        r4 = r4.findLastVisibleItemPosition();
        r0 = r23;
        r5 = r0.messages;
        r5 = r5.size();
        r5 = r5 + -1;
        if (r4 < r5) goto L_0x19c8;
    L_0x19c5:
        r23.moveScrollToLastMessage();
    L_0x19c8:
        r4 = com.hanista.mobogram.messenger.NotificationsController.getInstance();
        r4.playOutChatSound();
        goto L_0x0053;
    L_0x19d1:
        r9 = 0;
        goto L_0x1914;
    L_0x19d4:
        r8 = 0;
        goto L_0x1948;
    L_0x19d7:
        r8 = move-exception;
        r22 = r8;
        r8 = r9;
        r9 = r22;
        r11 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r11, r9);
        r9 = r10;
        goto L_0x194d;
    L_0x19e6:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.messageReceivedByAck;
        r0 = r24;
        if (r0 != r4) goto L_0x1a14;
    L_0x19ec:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r0 = r23;
        r5 = r0.messagesDict;
        r6 = 0;
        r5 = r5[r6];
        r4 = r5.get(r4);
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        if (r4 == 0) goto L_0x0053;
    L_0x1a00:
        r5 = r4.messageOwner;
        r6 = 0;
        r5.send_state = r6;
        r0 = r23;
        r5 = r0.chatAdapter;
        if (r5 == 0) goto L_0x0053;
    L_0x1a0b:
        r0 = r23;
        r5 = r0.chatAdapter;
        r5.updateRowWithMessageObject(r4);
        goto L_0x0053;
    L_0x1a14:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.messageSendError;
        r0 = r24;
        if (r0 != r4) goto L_0x1a38;
    L_0x1a1a:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r0 = r23;
        r5 = r0.messagesDict;
        r6 = 0;
        r5 = r5[r6];
        r4 = r5.get(r4);
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        if (r4 == 0) goto L_0x0053;
    L_0x1a2e:
        r4 = r4.messageOwner;
        r5 = 2;
        r4.send_state = r5;
        r23.updateVisibleRows();
        goto L_0x0053;
    L_0x1a38:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.chatInfoDidLoaded;
        r0 = r24;
        if (r0 != r4) goto L_0x1cb7;
    L_0x1a3e:
        r4 = 0;
        r4 = r25[r4];
        r4 = (com.hanista.mobogram.tgnet.TLRPC.ChatFull) r4;
        r0 = r23;
        r5 = r0.currentChat;
        if (r5 == 0) goto L_0x0053;
    L_0x1a49:
        r5 = r4.id;
        r0 = r23;
        r6 = r0.currentChat;
        r6 = r6.id;
        if (r5 != r6) goto L_0x0053;
    L_0x1a53:
        r5 = r4 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_channelFull;
        if (r5 == 0) goto L_0x1abf;
    L_0x1a57:
        r0 = r23;
        r5 = r0.currentChat;
        r5 = r5.megagroup;
        if (r5 == 0) goto L_0x1aad;
    L_0x1a5f:
        r6 = 0;
        r5 = r4.participants;
        if (r5 == 0) goto L_0x1a85;
    L_0x1a64:
        r5 = 0;
        r7 = r6;
        r6 = r5;
    L_0x1a67:
        r5 = r4.participants;
        r5 = r5.participants;
        r5 = r5.size();
        if (r6 >= r5) goto L_0x1a86;
    L_0x1a71:
        r5 = r4.participants;
        r5 = r5.participants;
        r5 = r5.get(r6);
        r5 = (com.hanista.mobogram.tgnet.TLRPC.ChatParticipant) r5;
        r5 = r5.date;
        r7 = java.lang.Math.max(r5, r7);
        r5 = r6 + 1;
        r6 = r5;
        goto L_0x1a67;
    L_0x1a85:
        r7 = r6;
    L_0x1a86:
        if (r7 == 0) goto L_0x1a9c;
    L_0x1a88:
        r8 = java.lang.System.currentTimeMillis();
        r10 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r8 = r8 / r10;
        r6 = (long) r7;
        r6 = r8 - r6;
        r6 = java.lang.Math.abs(r6);
        r8 = 3600; // 0xe10 float:5.045E-42 double:1.7786E-320;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 <= 0) goto L_0x1aad;
    L_0x1a9c:
        r5 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r23;
        r6 = r0.currentChat;
        r6 = r6.id;
        r6 = java.lang.Integer.valueOf(r6);
        r5.loadChannelParticipants(r6);
    L_0x1aad:
        r5 = r4.participants;
        if (r5 != 0) goto L_0x1abf;
    L_0x1ab1:
        r0 = r23;
        r5 = r0.info;
        if (r5 == 0) goto L_0x1abf;
    L_0x1ab7:
        r0 = r23;
        r5 = r0.info;
        r5 = r5.participants;
        r4.participants = r5;
    L_0x1abf:
        r0 = r23;
        r0.info = r4;
        r0 = r23;
        r4 = r0.mentionsAdapter;
        if (r4 == 0) goto L_0x1ad4;
    L_0x1ac9:
        r0 = r23;
        r4 = r0.mentionsAdapter;
        r0 = r23;
        r5 = r0.info;
        r4.setChatInfo(r5);
    L_0x1ad4:
        r4 = 3;
        r4 = r25[r4];
        r4 = r4 instanceof com.hanista.mobogram.messenger.MessageObject;
        if (r4 == 0) goto L_0x1b78;
    L_0x1adb:
        r4 = 3;
        r4 = r25[r4];
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        r0 = r23;
        r0.pinnedMessageObject = r4;
        r4 = 0;
        r0 = r23;
        r0.updatePinnedMessageView(r4);
    L_0x1aea:
        r0 = r23;
        r4 = r0.avatarContainer;
        if (r4 == 0) goto L_0x1afe;
    L_0x1af0:
        r0 = r23;
        r4 = r0.avatarContainer;
        r4.updateOnlineCount();
        r0 = r23;
        r4 = r0.avatarContainer;
        r4.updateSubtitle();
    L_0x1afe:
        r0 = r23;
        r4 = r0.isBroadcast;
        if (r4 == 0) goto L_0x1b0f;
    L_0x1b04:
        r4 = com.hanista.mobogram.messenger.SendMessagesHelper.getInstance();
        r0 = r23;
        r5 = r0.info;
        r4.setCurrentChatInfo(r5);
    L_0x1b0f:
        r0 = r23;
        r4 = r0.info;
        r4 = r4 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_chatFull;
        if (r4 == 0) goto L_0x1bf7;
    L_0x1b17:
        r4 = 0;
        r0 = r23;
        r0.hasBotsCommands = r4;
        r0 = r23;
        r4 = r0.botInfo;
        r4.clear();
        r4 = 0;
        r0 = r23;
        r0.botsCount = r4;
        r4 = 0;
        com.hanista.mobogram.ui.Components.URLSpanBotCommand.enabled = r4;
        r4 = 0;
        r5 = r4;
    L_0x1b2d:
        r0 = r23;
        r4 = r0.info;
        r4 = r4.participants;
        r4 = r4.participants;
        r4 = r4.size();
        if (r5 >= r4) goto L_0x1b80;
    L_0x1b3b:
        r0 = r23;
        r4 = r0.info;
        r4 = r4.participants;
        r4 = r4.participants;
        r4 = r4.get(r5);
        r4 = (com.hanista.mobogram.tgnet.TLRPC.ChatParticipant) r4;
        r6 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r4 = r4.user_id;
        r4 = java.lang.Integer.valueOf(r4);
        r4 = r6.getUser(r4);
        if (r4 == 0) goto L_0x1b74;
    L_0x1b59:
        r6 = r4.bot;
        if (r6 == 0) goto L_0x1b74;
    L_0x1b5d:
        r6 = 1;
        com.hanista.mobogram.ui.Components.URLSpanBotCommand.enabled = r6;
        r0 = r23;
        r6 = r0.botsCount;
        r6 = r6 + 1;
        r0 = r23;
        r0.botsCount = r6;
        r4 = r4.id;
        r6 = 1;
        r0 = r23;
        r7 = r0.classGuid;
        com.hanista.mobogram.messenger.query.BotQuery.loadBotInfo(r4, r6, r7);
    L_0x1b74:
        r4 = r5 + 1;
        r5 = r4;
        goto L_0x1b2d;
    L_0x1b78:
        r4 = 1;
        r0 = r23;
        r0.updatePinnedMessageView(r4);
        goto L_0x1aea;
    L_0x1b80:
        r0 = r23;
        r4 = r0.chatListView;
        if (r4 == 0) goto L_0x1b8d;
    L_0x1b86:
        r0 = r23;
        r4 = r0.chatListView;
        r4.invalidateViews();
    L_0x1b8d:
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        if (r4 == 0) goto L_0x1ba2;
    L_0x1b93:
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        r0 = r23;
        r5 = r0.botsCount;
        r0 = r23;
        r6 = r0.hasBotsCommands;
        r4.setBotsCount(r5, r6);
    L_0x1ba2:
        r0 = r23;
        r4 = r0.mentionsAdapter;
        if (r4 == 0) goto L_0x1bb3;
    L_0x1ba8:
        r0 = r23;
        r4 = r0.mentionsAdapter;
        r0 = r23;
        r5 = r0.botsCount;
        r4.setBotsCount(r5);
    L_0x1bb3:
        r0 = r23;
        r4 = r0.currentChat;
        r4 = com.hanista.mobogram.messenger.ChatObject.isChannel(r4);
        if (r4 == 0) goto L_0x0053;
    L_0x1bbd:
        r0 = r23;
        r4 = r0.mergeDialogId;
        r6 = 0;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x0053;
    L_0x1bc7:
        r0 = r23;
        r4 = r0.info;
        r4 = r4.migrated_from_chat_id;
        if (r4 == 0) goto L_0x0053;
    L_0x1bcf:
        r0 = r23;
        r4 = r0.info;
        r4 = r4.migrated_from_chat_id;
        r4 = -r4;
        r4 = (long) r4;
        r0 = r23;
        r0.mergeDialogId = r4;
        r0 = r23;
        r4 = r0.maxMessageId;
        r5 = 1;
        r0 = r23;
        r6 = r0.info;
        r6 = r6.migrated_from_max_id;
        r4[r5] = r6;
        r0 = r23;
        r4 = r0.chatAdapter;
        if (r4 == 0) goto L_0x0053;
    L_0x1bee:
        r0 = r23;
        r4 = r0.chatAdapter;
        r4.notifyDataSetChanged();
        goto L_0x0053;
    L_0x1bf7:
        r0 = r23;
        r4 = r0.info;
        r4 = r4 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_channelFull;
        if (r4 == 0) goto L_0x1b8d;
    L_0x1bff:
        r4 = 0;
        r0 = r23;
        r0.hasBotsCommands = r4;
        r0 = r23;
        r4 = r0.botInfo;
        r4.clear();
        r4 = 0;
        r0 = r23;
        r0.botsCount = r4;
        r0 = r23;
        r4 = r0.info;
        r4 = r4.bot_info;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x1c7d;
    L_0x1c1c:
        r4 = 1;
    L_0x1c1d:
        com.hanista.mobogram.ui.Components.URLSpanBotCommand.enabled = r4;
        r0 = r23;
        r4 = r0.info;
        r4 = r4.bot_info;
        r4 = r4.size();
        r0 = r23;
        r0.botsCount = r4;
        r4 = 0;
        r5 = r4;
    L_0x1c2f:
        r0 = r23;
        r4 = r0.info;
        r4 = r4.bot_info;
        r4 = r4.size();
        if (r5 >= r4) goto L_0x1c7f;
    L_0x1c3b:
        r0 = r23;
        r4 = r0.info;
        r4 = r4.bot_info;
        r4 = r4.get(r5);
        r4 = (com.hanista.mobogram.tgnet.TLRPC.BotInfo) r4;
        r6 = r4.commands;
        r6 = r6.isEmpty();
        if (r6 != 0) goto L_0x1c6c;
    L_0x1c4f:
        r0 = r23;
        r6 = r0.currentChat;
        r6 = com.hanista.mobogram.messenger.ChatObject.isChannel(r6);
        if (r6 == 0) goto L_0x1c67;
    L_0x1c59:
        r0 = r23;
        r6 = r0.currentChat;
        if (r6 == 0) goto L_0x1c6c;
    L_0x1c5f:
        r0 = r23;
        r6 = r0.currentChat;
        r6 = r6.megagroup;
        if (r6 == 0) goto L_0x1c6c;
    L_0x1c67:
        r6 = 1;
        r0 = r23;
        r0.hasBotsCommands = r6;
    L_0x1c6c:
        r0 = r23;
        r6 = r0.botInfo;
        r7 = r4.user_id;
        r7 = java.lang.Integer.valueOf(r7);
        r6.put(r7, r4);
        r4 = r5 + 1;
        r5 = r4;
        goto L_0x1c2f;
    L_0x1c7d:
        r4 = 0;
        goto L_0x1c1d;
    L_0x1c7f:
        r0 = r23;
        r4 = r0.chatListView;
        if (r4 == 0) goto L_0x1c8c;
    L_0x1c85:
        r0 = r23;
        r4 = r0.chatListView;
        r4.invalidateViews();
    L_0x1c8c:
        r0 = r23;
        r4 = r0.mentionsAdapter;
        if (r4 == 0) goto L_0x1b8d;
    L_0x1c92:
        r0 = r23;
        r4 = r0.currentChat;
        r4 = com.hanista.mobogram.messenger.ChatObject.isChannel(r4);
        if (r4 == 0) goto L_0x1caa;
    L_0x1c9c:
        r0 = r23;
        r4 = r0.currentChat;
        if (r4 == 0) goto L_0x1b8d;
    L_0x1ca2:
        r0 = r23;
        r4 = r0.currentChat;
        r4 = r4.megagroup;
        if (r4 == 0) goto L_0x1b8d;
    L_0x1caa:
        r0 = r23;
        r4 = r0.mentionsAdapter;
        r0 = r23;
        r5 = r0.botInfo;
        r4.setBotInfo(r5);
        goto L_0x1b8d;
    L_0x1cb7:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.chatInfoCantLoad;
        r0 = r24;
        if (r0 != r4) goto L_0x1d6d;
    L_0x1cbd:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
        r0 = r23;
        r5 = r0.currentChat;
        if (r5 == 0) goto L_0x0053;
    L_0x1ccc:
        r0 = r23;
        r5 = r0.currentChat;
        r5 = r5.id;
        if (r5 != r4) goto L_0x0053;
    L_0x1cd4:
        r4 = 1;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
        r5 = r23.getParentActivity();
        if (r5 == 0) goto L_0x0053;
    L_0x1ce3:
        r0 = r23;
        r5 = r0.closeChatDialog;
        if (r5 != 0) goto L_0x0053;
    L_0x1ce9:
        r5 = new android.app.AlertDialog$Builder;
        r6 = r23.getParentActivity();
        r5.<init>(r6);
        r6 = "AppName";
        r7 = 2131166486; // 0x7f070516 float:1.7947219E38 double:1.0529361463E-314;
        r6 = com.hanista.mobogram.messenger.LocaleController.getString(r6, r7);
        r5.setTitle(r6);
        if (r4 != 0) goto L_0x1d4b;
    L_0x1d01:
        r4 = "ChannelCantOpenPrivate";
        r6 = 2131165417; // 0x7f0700e9 float:1.794505E38 double:1.052935618E-314;
        r4 = com.hanista.mobogram.messenger.LocaleController.getString(r4, r6);
        r5.setMessage(r4);
    L_0x1d0e:
        r4 = "OK";
        r6 = 2131166046; // 0x7f07035e float:1.7946326E38 double:1.052935929E-314;
        r4 = com.hanista.mobogram.messenger.LocaleController.getString(r4, r6);
        r6 = 0;
        r5.setPositiveButton(r4, r6);
        r4 = r5.create();
        r0 = r23;
        r0.closeChatDialog = r4;
        r0 = r23;
        r0.showDialog(r4);
        r4 = 0;
        r0 = r23;
        r0.loading = r4;
        r0 = r23;
        r4 = r0.progressView;
        if (r4 == 0) goto L_0x1d3c;
    L_0x1d34:
        r0 = r23;
        r4 = r0.progressView;
        r5 = 4;
        r4.setVisibility(r5);
    L_0x1d3c:
        r0 = r23;
        r4 = r0.chatAdapter;
        if (r4 == 0) goto L_0x0053;
    L_0x1d42:
        r0 = r23;
        r4 = r0.chatAdapter;
        r4.notifyDataSetChanged();
        goto L_0x0053;
    L_0x1d4b:
        r6 = 1;
        if (r4 != r6) goto L_0x1d5c;
    L_0x1d4e:
        r4 = "ChannelCantOpenNa";
        r6 = 2131165416; // 0x7f0700e8 float:1.7945048E38 double:1.0529356177E-314;
        r4 = com.hanista.mobogram.messenger.LocaleController.getString(r4, r6);
        r5.setMessage(r4);
        goto L_0x1d0e;
    L_0x1d5c:
        r6 = 2;
        if (r4 != r6) goto L_0x1d0e;
    L_0x1d5f:
        r4 = "ChannelCantOpenBanned";
        r6 = 2131165415; // 0x7f0700e7 float:1.7945046E38 double:1.052935617E-314;
        r4 = com.hanista.mobogram.messenger.LocaleController.getString(r4, r6);
        r5.setMessage(r4);
        goto L_0x1d0e;
    L_0x1d6d:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.contactsDidLoaded;
        r0 = r24;
        if (r0 != r4) goto L_0x1d85;
    L_0x1d73:
        r23.updateContactStatus();
        r0 = r23;
        r4 = r0.avatarContainer;
        if (r4 == 0) goto L_0x0053;
    L_0x1d7c:
        r0 = r23;
        r4 = r0.avatarContainer;
        r4.updateSubtitle();
        goto L_0x0053;
    L_0x1d85:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.encryptedChatUpdated;
        r0 = r24;
        if (r0 != r4) goto L_0x1e18;
    L_0x1d8b:
        r4 = 0;
        r4 = r25[r4];
        r4 = (com.hanista.mobogram.tgnet.TLRPC.EncryptedChat) r4;
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        if (r5 == 0) goto L_0x0053;
    L_0x1d96:
        r5 = r4.id;
        r0 = r23;
        r6 = r0.currentEncryptedChat;
        r6 = r6.id;
        if (r5 != r6) goto L_0x0053;
    L_0x1da0:
        r0 = r23;
        r0.currentEncryptedChat = r4;
        r23.updateContactStatus();
        r23.updateSecretStatus();
        r23.initStickers();
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        if (r4 == 0) goto L_0x1de4;
    L_0x1db3:
        r0 = r23;
        r6 = r0.chatActivityEnterView;
        r0 = r23;
        r4 = r0.currentEncryptedChat;
        if (r4 == 0) goto L_0x1dcb;
    L_0x1dbd:
        r0 = r23;
        r4 = r0.currentEncryptedChat;
        r4 = r4.layer;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.getPeerLayerVersion(r4);
        r5 = 23;
        if (r4 < r5) goto L_0x1e12;
    L_0x1dcb:
        r4 = 1;
    L_0x1dcc:
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        if (r5 == 0) goto L_0x1de0;
    L_0x1dd2:
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        r5 = r5.layer;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.getPeerLayerVersion(r5);
        r7 = 46;
        if (r5 < r7) goto L_0x1e14;
    L_0x1de0:
        r5 = 1;
    L_0x1de1:
        r6.setAllowStickersAndGifs(r4, r5);
    L_0x1de4:
        r0 = r23;
        r4 = r0.mentionsAdapter;
        if (r4 == 0) goto L_0x0053;
    L_0x1dea:
        r0 = r23;
        r5 = r0.mentionsAdapter;
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        r4 = r4.isEditingMessage();
        if (r4 != 0) goto L_0x1e16;
    L_0x1df8:
        r0 = r23;
        r4 = r0.currentEncryptedChat;
        if (r4 == 0) goto L_0x1e0c;
    L_0x1dfe:
        r0 = r23;
        r4 = r0.currentEncryptedChat;
        r4 = r4.layer;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.getPeerLayerVersion(r4);
        r6 = 46;
        if (r4 < r6) goto L_0x1e16;
    L_0x1e0c:
        r4 = 1;
    L_0x1e0d:
        r5.setNeedBotContext(r4);
        goto L_0x0053;
    L_0x1e12:
        r4 = 0;
        goto L_0x1dcc;
    L_0x1e14:
        r5 = 0;
        goto L_0x1de1;
    L_0x1e16:
        r4 = 0;
        goto L_0x1e0d;
    L_0x1e18:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.messagesReadEncrypted;
        r0 = r24;
        if (r0 != r4) goto L_0x1e75;
    L_0x1e1e:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        if (r5 == 0) goto L_0x0053;
    L_0x1e2d:
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        r5 = r5.id;
        if (r5 != r4) goto L_0x0053;
    L_0x1e35:
        r4 = 1;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r5 = r4.intValue();
        r0 = r23;
        r4 = r0.messages;
        r6 = r4.iterator();
    L_0x1e46:
        r4 = r6.hasNext();
        if (r4 == 0) goto L_0x1e64;
    L_0x1e4c:
        r4 = r6.next();
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        r7 = r4.isOut();
        if (r7 == 0) goto L_0x1e46;
    L_0x1e58:
        r7 = r4.isOut();
        if (r7 == 0) goto L_0x1e69;
    L_0x1e5e:
        r7 = r4.isUnread();
        if (r7 != 0) goto L_0x1e69;
    L_0x1e64:
        r23.updateVisibleRows();
        goto L_0x0053;
    L_0x1e69:
        r7 = r4.messageOwner;
        r7 = r7.date;
        r7 = r7 + -1;
        if (r7 > r5) goto L_0x1e46;
    L_0x1e71:
        r4.setIsRead();
        goto L_0x1e46;
    L_0x1e75:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.audioDidReset;
        r0 = r24;
        if (r0 == r4) goto L_0x1e81;
    L_0x1e7b:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.audioPlayStateChanged;
        r0 = r24;
        if (r0 != r4) goto L_0x1eef;
    L_0x1e81:
        r0 = r23;
        r4 = r0.chatListView;
        if (r4 == 0) goto L_0x0053;
    L_0x1e87:
        r0 = r23;
        r4 = r0.chatListView;
        r6 = r4.getChildCount();
        r4 = 0;
        r5 = r4;
    L_0x1e91:
        if (r5 >= r6) goto L_0x1ebb;
    L_0x1e93:
        r0 = r23;
        r4 = r0.chatListView;
        r4 = r4.getChildAt(r5);
        r7 = r4 instanceof com.hanista.mobogram.ui.Cells.ChatMessageCell;
        if (r7 == 0) goto L_0x1eb7;
    L_0x1e9f:
        r4 = (com.hanista.mobogram.ui.Cells.ChatMessageCell) r4;
        r7 = r4.getMessageObject();
        if (r7 == 0) goto L_0x1eb7;
    L_0x1ea7:
        r8 = r7.isVoice();
        if (r8 != 0) goto L_0x1eb3;
    L_0x1ead:
        r7 = r7.isMusic();
        if (r7 == 0) goto L_0x1eb7;
    L_0x1eb3:
        r7 = 0;
        r4.updateButtonState(r7);
    L_0x1eb7:
        r4 = r5 + 1;
        r5 = r4;
        goto L_0x1e91;
    L_0x1ebb:
        r0 = r23;
        r4 = r0.mentionListView;
        r6 = r4.getChildCount();
        r4 = 0;
        r5 = r4;
    L_0x1ec5:
        if (r5 >= r6) goto L_0x0053;
    L_0x1ec7:
        r0 = r23;
        r4 = r0.mentionListView;
        r4 = r4.getChildAt(r5);
        r7 = r4 instanceof com.hanista.mobogram.ui.Cells.ContextLinkCell;
        if (r7 == 0) goto L_0x1eeb;
    L_0x1ed3:
        r4 = (com.hanista.mobogram.ui.Cells.ContextLinkCell) r4;
        r7 = r4.getMessageObject();
        if (r7 == 0) goto L_0x1eeb;
    L_0x1edb:
        r8 = r7.isVoice();
        if (r8 != 0) goto L_0x1ee7;
    L_0x1ee1:
        r7 = r7.isMusic();
        if (r7 == 0) goto L_0x1eeb;
    L_0x1ee7:
        r7 = 0;
        r4.updateButtonState(r7);
    L_0x1eeb:
        r4 = r5 + 1;
        r5 = r4;
        goto L_0x1ec5;
    L_0x1eef:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.audioProgressDidChanged;
        r0 = r24;
        if (r0 != r4) goto L_0x1f4d;
    L_0x1ef5:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r0 = r23;
        r5 = r0.chatListView;
        if (r5 == 0) goto L_0x0053;
    L_0x1f00:
        r0 = r23;
        r5 = r0.chatListView;
        r7 = r5.getChildCount();
        r5 = 0;
        r6 = r5;
    L_0x1f0a:
        if (r6 >= r7) goto L_0x0053;
    L_0x1f0c:
        r0 = r23;
        r5 = r0.chatListView;
        r5 = r5.getChildAt(r6);
        r8 = r5 instanceof com.hanista.mobogram.ui.Cells.ChatMessageCell;
        if (r8 == 0) goto L_0x1f49;
    L_0x1f18:
        r5 = (com.hanista.mobogram.ui.Cells.ChatMessageCell) r5;
        r8 = r5.getMessageObject();
        if (r8 == 0) goto L_0x1f49;
    L_0x1f20:
        r8 = r5.getMessageObject();
        r8 = r8.getId();
        r9 = r4.intValue();
        if (r8 != r9) goto L_0x1f49;
    L_0x1f2e:
        r4 = r5.getMessageObject();
        r6 = com.hanista.mobogram.messenger.MediaController.m71a();
        r6 = r6.m182j();
        if (r6 == 0) goto L_0x0053;
    L_0x1f3c:
        r7 = r6.audioProgress;
        r4.audioProgress = r7;
        r6 = r6.audioProgressSec;
        r4.audioProgressSec = r6;
        r5.updateAudioProgress();
        goto L_0x0053;
    L_0x1f49:
        r5 = r6 + 1;
        r6 = r5;
        goto L_0x1f0a;
    L_0x1f4d:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.removeAllMessagesFromDialog;
        r0 = r24;
        if (r0 != r4) goto L_0x20f8;
    L_0x1f53:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Long) r4;
        r4 = r4.longValue();
        r0 = r23;
        r6 = r0.dialog_id;
        r4 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
        if (r4 != 0) goto L_0x0053;
    L_0x1f64:
        r0 = r23;
        r4 = r0.messages;
        r4.clear();
        r0 = r23;
        r4 = r0.waitingForLoad;
        r4.clear();
        r0 = r23;
        r4 = r0.messagesByDays;
        r4.clear();
        r4 = 1;
    L_0x1f7a:
        if (r4 < 0) goto L_0x1fd2;
    L_0x1f7c:
        r0 = r23;
        r5 = r0.messagesDict;
        r5 = r5[r4];
        r5.clear();
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        if (r5 != 0) goto L_0x1fc0;
    L_0x1f8b:
        r0 = r23;
        r5 = r0.maxMessageId;
        r6 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r5[r4] = r6;
        r0 = r23;
        r5 = r0.minMessageId;
        r6 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r5[r4] = r6;
    L_0x1f9c:
        r0 = r23;
        r5 = r0.maxDate;
        r6 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r5[r4] = r6;
        r0 = r23;
        r5 = r0.minDate;
        r6 = 0;
        r5[r4] = r6;
        r0 = r23;
        r5 = r0.selectedMessagesIds;
        r5 = r5[r4];
        r5.clear();
        r0 = r23;
        r5 = r0.selectedMessagesCanCopyIds;
        r5 = r5[r4];
        r5.clear();
        r4 = r4 + -1;
        goto L_0x1f7a;
    L_0x1fc0:
        r0 = r23;
        r5 = r0.maxMessageId;
        r6 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r5[r4] = r6;
        r0 = r23;
        r5 = r0.minMessageId;
        r6 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r5[r4] = r6;
        goto L_0x1f9c;
    L_0x1fd2:
        r4 = 0;
        r0 = r23;
        r0.cantDeleteMessagesCount = r4;
        r0 = r23;
        r4 = r0.actionBar;
        r4.hideActionMode();
        r4 = 1;
        r0 = r23;
        r0.updatePinnedMessageView(r4);
        r0 = r23;
        r4 = r0.botButtons;
        if (r4 == 0) goto L_0x1ffe;
    L_0x1fea:
        r4 = 0;
        r0 = r23;
        r0.botButtons = r4;
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        if (r4 == 0) goto L_0x1ffe;
    L_0x1ff5:
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        r5 = 0;
        r6 = 0;
        r4.setButtons(r5, r6);
    L_0x1ffe:
        r0 = r23;
        r4 = r0.currentEncryptedChat;
        if (r4 != 0) goto L_0x2022;
    L_0x2004:
        r0 = r23;
        r4 = r0.currentUser;
        if (r4 == 0) goto L_0x2022;
    L_0x200a:
        r0 = r23;
        r4 = r0.currentUser;
        r4 = r4.bot;
        if (r4 == 0) goto L_0x2022;
    L_0x2012:
        r0 = r23;
        r4 = r0.botUser;
        if (r4 != 0) goto L_0x2022;
    L_0x2018:
        r4 = "";
        r0 = r23;
        r0.botUser = r4;
        r23.updateBottomOverlay();
    L_0x2022:
        r4 = 1;
        r4 = r25[r4];
        r4 = (java.lang.Boolean) r4;
        r4 = r4.booleanValue();
        if (r4 == 0) goto L_0x20de;
    L_0x202d:
        r0 = r23;
        r4 = r0.chatAdapter;
        if (r4 == 0) goto L_0x204e;
    L_0x2033:
        r0 = r23;
        r5 = r0.progressView;
        r0 = r23;
        r4 = r0.chatAdapter;
        r4 = r4.botInfoRow;
        r6 = -1;
        if (r4 != r6) goto L_0x206a;
    L_0x2042:
        r4 = 0;
    L_0x2043:
        r5.setVisibility(r4);
        r0 = r23;
        r4 = r0.chatListView;
        r5 = 0;
        r4.setEmptyView(r5);
    L_0x204e:
        r4 = 0;
    L_0x204f:
        r5 = 2;
        if (r4 >= r5) goto L_0x206c;
    L_0x2052:
        r0 = r23;
        r5 = r0.endReached;
        r6 = 0;
        r5[r4] = r6;
        r0 = r23;
        r5 = r0.cacheEndReached;
        r6 = 0;
        r5[r4] = r6;
        r0 = r23;
        r5 = r0.forwardEndReached;
        r6 = 1;
        r5[r4] = r6;
        r4 = r4 + 1;
        goto L_0x204f;
    L_0x206a:
        r4 = 4;
        goto L_0x2043;
    L_0x206c:
        r4 = 1;
        r0 = r23;
        r0.first = r4;
        r4 = 1;
        r0 = r23;
        r0.firstLoading = r4;
        r4 = 1;
        r0 = r23;
        r0.loading = r4;
        r4 = 0;
        r0 = r23;
        r0.startLoadFromMessageId = r4;
        r4 = 0;
        r0 = r23;
        r0.needSelectFromMessageId = r4;
        r0 = r23;
        r4 = r0.waitingForLoad;
        r0 = r23;
        r5 = r0.lastLoadIndex;
        r5 = java.lang.Integer.valueOf(r5);
        r4.add(r5);
        r5 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r0 = r23;
        r6 = r0.dialog_id;
        r4 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x20d9;
    L_0x20a2:
        r8 = 30;
    L_0x20a4:
        r9 = 0;
        r0 = r23;
        r4 = r0.gotoDateMessageSelected;
        if (r4 != 0) goto L_0x20dc;
    L_0x20ab:
        r10 = 1;
    L_0x20ac:
        r11 = 0;
        r0 = r23;
        r12 = r0.classGuid;
        r13 = 2;
        r14 = 0;
        r0 = r23;
        r4 = r0.currentChat;
        r15 = com.hanista.mobogram.messenger.ChatObject.isChannel(r4);
        r0 = r23;
        r0 = r0.lastLoadIndex;
        r16 = r0;
        r4 = r16 + 1;
        r0 = r23;
        r0.lastLoadIndex = r4;
        r5.loadMessages(r6, r8, r9, r10, r11, r12, r13, r14, r15, r16);
    L_0x20ca:
        r0 = r23;
        r4 = r0.chatAdapter;
        if (r4 == 0) goto L_0x0053;
    L_0x20d0:
        r0 = r23;
        r4 = r0.chatAdapter;
        r4.notifyDataSetChanged();
        goto L_0x0053;
    L_0x20d9:
        r8 = 20;
        goto L_0x20a4;
    L_0x20dc:
        r10 = 0;
        goto L_0x20ac;
    L_0x20de:
        r0 = r23;
        r4 = r0.progressView;
        if (r4 == 0) goto L_0x20ca;
    L_0x20e4:
        r0 = r23;
        r4 = r0.progressView;
        r5 = 4;
        r4.setVisibility(r5);
        r0 = r23;
        r4 = r0.chatListView;
        r0 = r23;
        r5 = r0.emptyViewContainer;
        r4.setEmptyView(r5);
        goto L_0x20ca;
    L_0x20f8:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.screenshotTook;
        r0 = r24;
        if (r0 != r4) goto L_0x2103;
    L_0x20fe:
        r23.updateInformationForScreenshotDetector();
        goto L_0x0053;
    L_0x2103:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.blockedUsersDidLoaded;
        r0 = r24;
        if (r0 != r4) goto L_0x2136;
    L_0x2109:
        r0 = r23;
        r4 = r0.currentUser;
        if (r4 == 0) goto L_0x0053;
    L_0x210f:
        r0 = r23;
        r4 = r0.userBlocked;
        r5 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r5 = r5.blockedUsers;
        r0 = r23;
        r6 = r0.currentUser;
        r6 = r6.id;
        r6 = java.lang.Integer.valueOf(r6);
        r5 = r5.contains(r6);
        r0 = r23;
        r0.userBlocked = r5;
        r0 = r23;
        r5 = r0.userBlocked;
        if (r4 == r5) goto L_0x0053;
    L_0x2131:
        r23.updateBottomOverlay();
        goto L_0x0053;
    L_0x2136:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.FileNewChunkAvailable;
        r0 = r24;
        if (r0 != r4) goto L_0x2181;
    L_0x213c:
        r4 = 0;
        r4 = r25[r4];
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        r5 = 2;
        r5 = r25[r5];
        r5 = (java.lang.Long) r5;
        r6 = r5.longValue();
        r8 = 0;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 == 0) goto L_0x0053;
    L_0x2150:
        r0 = r23;
        r8 = r0.dialog_id;
        r10 = r4.getDialogId();
        r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r5 != 0) goto L_0x0053;
    L_0x215c:
        r0 = r23;
        r5 = r0.messagesDict;
        r8 = 0;
        r5 = r5[r8];
        r4 = r4.getId();
        r4 = java.lang.Integer.valueOf(r4);
        r4 = r5.get(r4);
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        if (r4 == 0) goto L_0x0053;
    L_0x2173:
        r4 = r4.messageOwner;
        r4 = r4.media;
        r4 = r4.document;
        r5 = (int) r6;
        r4.size = r5;
        r23.updateVisibleRows();
        goto L_0x0053;
    L_0x2181:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.didCreatedNewDeleteTask;
        r0 = r24;
        if (r0 != r4) goto L_0x21d0;
    L_0x2187:
        r4 = 0;
        r4 = r25[r4];
        r4 = (android.util.SparseArray) r4;
        r6 = 0;
        r5 = 0;
        r7 = r6;
        r6 = r5;
    L_0x2190:
        r5 = r4.size();
        if (r6 >= r5) goto L_0x21c9;
    L_0x2196:
        r8 = r4.keyAt(r6);
        r5 = r4.get(r8);
        r5 = (java.util.ArrayList) r5;
        r9 = r5.iterator();
    L_0x21a4:
        r5 = r9.hasNext();
        if (r5 == 0) goto L_0x21c5;
    L_0x21aa:
        r5 = r9.next();
        r5 = (java.lang.Integer) r5;
        r0 = r23;
        r10 = r0.messagesDict;
        r11 = 0;
        r10 = r10[r11];
        r5 = r10.get(r5);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        if (r5 == 0) goto L_0x21a4;
    L_0x21bf:
        r5 = r5.messageOwner;
        r5.destroyTime = r8;
        r7 = 1;
        goto L_0x21a4;
    L_0x21c5:
        r5 = r6 + 1;
        r6 = r5;
        goto L_0x2190;
    L_0x21c9:
        if (r7 == 0) goto L_0x0053;
    L_0x21cb:
        r23.updateVisibleRows();
        goto L_0x0053;
    L_0x21d0:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.audioDidStarted;
        r0 = r24;
        if (r0 != r4) goto L_0x221a;
    L_0x21d6:
        r4 = 0;
        r4 = r25[r4];
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        r0 = r23;
        r0.sendSecretMessageRead(r4);
        r0 = r23;
        r4 = r0.chatListView;
        if (r4 == 0) goto L_0x0053;
    L_0x21e6:
        r0 = r23;
        r4 = r0.chatListView;
        r6 = r4.getChildCount();
        r4 = 0;
        r5 = r4;
    L_0x21f0:
        if (r5 >= r6) goto L_0x0053;
    L_0x21f2:
        r0 = r23;
        r4 = r0.chatListView;
        r4 = r4.getChildAt(r5);
        r7 = r4 instanceof com.hanista.mobogram.ui.Cells.ChatMessageCell;
        if (r7 == 0) goto L_0x2216;
    L_0x21fe:
        r4 = (com.hanista.mobogram.ui.Cells.ChatMessageCell) r4;
        r7 = r4.getMessageObject();
        if (r7 == 0) goto L_0x2216;
    L_0x2206:
        r8 = r7.isVoice();
        if (r8 != 0) goto L_0x2212;
    L_0x220c:
        r7 = r7.isMusic();
        if (r7 == 0) goto L_0x2216;
    L_0x2212:
        r7 = 0;
        r4.updateButtonState(r7);
    L_0x2216:
        r4 = r5 + 1;
        r5 = r4;
        goto L_0x21f0;
    L_0x221a:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.updateMessageMedia;
        r0 = r24;
        if (r0 != r4) goto L_0x2255;
    L_0x2220:
        r4 = 0;
        r4 = r25[r4];
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        r0 = r23;
        r5 = r0.messagesDict;
        r6 = 0;
        r5 = r5[r6];
        r6 = r4.getId();
        r6 = java.lang.Integer.valueOf(r6);
        r5 = r5.get(r6);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        if (r5 == 0) goto L_0x2250;
    L_0x223c:
        r6 = r5.messageOwner;
        r7 = r4.messageOwner;
        r7 = r7.media;
        r6.media = r7;
        r6 = r5.messageOwner;
        r4 = r4.messageOwner;
        r4 = r4.attachPath;
        r6.attachPath = r4;
        r4 = 0;
        r5.generateThumbs(r4);
    L_0x2250:
        r23.updateVisibleRows();
        goto L_0x0053;
    L_0x2255:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.replaceMessagesObjects;
        r0 = r24;
        if (r0 != r4) goto L_0x240a;
    L_0x225b:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Long) r4;
        r4 = r4.longValue();
        r0 = r23;
        r6 = r0.dialog_id;
        r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r6 == 0) goto L_0x2274;
    L_0x226c:
        r0 = r23;
        r6 = r0.mergeDialogId;
        r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r6 != 0) goto L_0x0053;
    L_0x2274:
        r0 = r23;
        r6 = r0.dialog_id;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x2367;
    L_0x227c:
        r4 = 0;
        r8 = r4;
    L_0x227e:
        r7 = 0;
        r6 = 0;
        r4 = 1;
        r4 = r25[r4];
        r4 = (java.util.ArrayList) r4;
        r5 = 0;
        r9 = r5;
        r10 = r7;
        r7 = r6;
    L_0x2289:
        r5 = r4.size();
        if (r9 >= r5) goto L_0x23da;
    L_0x228f:
        r5 = r4.get(r9);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        r0 = r23;
        r6 = r0.messagesDict;
        r6 = r6[r8];
        r11 = r5.getId();
        r11 = java.lang.Integer.valueOf(r11);
        r6 = r6.get(r11);
        r6 = (com.hanista.mobogram.messenger.MessageObject) r6;
        r0 = r23;
        r11 = r0.pinnedMessageObject;
        if (r11 == 0) goto L_0x22c7;
    L_0x22af:
        r0 = r23;
        r11 = r0.pinnedMessageObject;
        r11 = r11.getId();
        r12 = r5.getId();
        if (r11 != r12) goto L_0x22c7;
    L_0x22bd:
        r0 = r23;
        r0.pinnedMessageObject = r5;
        r11 = 1;
        r0 = r23;
        r0.updatePinnedMessageView(r11);
    L_0x22c7:
        if (r6 == 0) goto L_0x284e;
    L_0x22c9:
        r11 = r5.type;
        if (r11 < 0) goto L_0x236b;
    L_0x22cd:
        if (r7 != 0) goto L_0x22d8;
    L_0x22cf:
        r11 = r5.messageOwner;
        r11 = r11.media;
        r11 = r11 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r11 == 0) goto L_0x22d8;
    L_0x22d7:
        r7 = 1;
    L_0x22d8:
        r11 = r6.replyMessageObject;
        if (r11 == 0) goto L_0x22ec;
    L_0x22dc:
        r11 = r6.replyMessageObject;
        r5.replyMessageObject = r11;
        r11 = r5.messageOwner;
        r11 = r11.action;
        r11 = r11 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionGameScore;
        if (r11 == 0) goto L_0x22ec;
    L_0x22e8:
        r11 = 0;
        r5.generateGameMessageText(r11);
    L_0x22ec:
        r11 = r5.messageOwner;
        r12 = r6.messageOwner;
        r12 = r12.attachPath;
        r11.attachPath = r12;
        r11 = r6.attachPathExists;
        r5.attachPathExists = r11;
        r11 = r6.mediaExists;
        r5.mediaExists = r11;
        r0 = r23;
        r11 = r0.messagesDict;
        r11 = r11[r8];
        r12 = r6.getId();
        r12 = java.lang.Integer.valueOf(r12);
        r11.put(r12, r5);
        r11 = r7;
    L_0x230e:
        r0 = r23;
        r7 = r0.messages;
        r12 = r7.indexOf(r6);
        if (r12 < 0) goto L_0x284a;
    L_0x2318:
        r0 = r23;
        r7 = r0.messagesByDays;
        r10 = r6.dateKey;
        r7 = r7.get(r10);
        r7 = (java.util.ArrayList) r7;
        r10 = -1;
        if (r7 == 0) goto L_0x232b;
    L_0x2327:
        r10 = r7.indexOf(r6);
    L_0x232b:
        r13 = r5.type;
        if (r13 < 0) goto L_0x237e;
    L_0x232f:
        r0 = r23;
        r6 = r0.messages;
        r6.set(r12, r5);
        r0 = r23;
        r6 = r0.chatAdapter;
        if (r6 == 0) goto L_0x2358;
    L_0x233c:
        r0 = r23;
        r6 = r0.chatAdapter;
        r0 = r23;
        r13 = r0.chatAdapter;
        r13 = r13.messagesStartRow;
        r0 = r23;
        r14 = r0.messages;
        r14 = r14.size();
        r13 = r13 + r14;
        r12 = r13 - r12;
        r12 = r12 + -1;
        r6.notifyItemChanged(r12);
    L_0x2358:
        if (r10 < 0) goto L_0x235d;
    L_0x235a:
        r7.set(r10, r5);
    L_0x235d:
        r10 = 1;
        r6 = r11;
        r7 = r10;
    L_0x2360:
        r5 = r9 + 1;
        r9 = r5;
        r10 = r7;
        r7 = r6;
        goto L_0x2289;
    L_0x2367:
        r4 = 1;
        r8 = r4;
        goto L_0x227e;
    L_0x236b:
        r0 = r23;
        r11 = r0.messagesDict;
        r11 = r11[r8];
        r12 = r6.getId();
        r12 = java.lang.Integer.valueOf(r12);
        r11.remove(r12);
        r11 = r7;
        goto L_0x230e;
    L_0x237e:
        r0 = r23;
        r5 = r0.messages;
        r5.remove(r12);
        r0 = r23;
        r5 = r0.chatAdapter;
        if (r5 == 0) goto L_0x23a6;
    L_0x238b:
        r0 = r23;
        r5 = r0.chatAdapter;
        r0 = r23;
        r13 = r0.chatAdapter;
        r13 = r13.messagesStartRow;
        r0 = r23;
        r14 = r0.messages;
        r14 = r14.size();
        r13 = r13 + r14;
        r13 = r13 - r12;
        r13 = r13 + -1;
        r5.notifyItemRemoved(r13);
    L_0x23a6:
        if (r10 < 0) goto L_0x235d;
    L_0x23a8:
        r7.remove(r10);
        r5 = r7.isEmpty();
        if (r5 == 0) goto L_0x235d;
    L_0x23b1:
        r0 = r23;
        r5 = r0.messagesByDays;
        r6 = r6.dateKey;
        r5.remove(r6);
        r0 = r23;
        r5 = r0.messages;
        r5.remove(r12);
        r0 = r23;
        r5 = r0.chatAdapter;
        r0 = r23;
        r6 = r0.chatAdapter;
        r6 = r6.messagesStartRow;
        r0 = r23;
        r7 = r0.messages;
        r7 = r7.size();
        r6 = r6 + r7;
        r5.notifyItemRemoved(r6);
        goto L_0x235d;
    L_0x23da:
        if (r10 == 0) goto L_0x0053;
    L_0x23dc:
        r0 = r23;
        r4 = r0.chatLayoutManager;
        if (r4 == 0) goto L_0x0053;
    L_0x23e2:
        if (r7 == 0) goto L_0x0053;
    L_0x23e4:
        r0 = r23;
        r4 = r0.chatLayoutManager;
        r5 = r4.findLastVisibleItemPosition();
        r0 = r23;
        r4 = r0.messages;
        r6 = r4.size();
        r0 = r23;
        r4 = r0.chatAdapter;
        r4 = r4.isBot;
        if (r4 == 0) goto L_0x2408;
    L_0x23fe:
        r4 = 2;
    L_0x23ff:
        r4 = r6 - r4;
        if (r5 < r4) goto L_0x0053;
    L_0x2403:
        r23.moveScrollToLastMessage();
        goto L_0x0053;
    L_0x2408:
        r4 = 1;
        goto L_0x23ff;
    L_0x240a:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.notificationsSettingsUpdated;
        r0 = r24;
        if (r0 != r4) goto L_0x2422;
    L_0x2410:
        r23.updateTitleIcons();
        r0 = r23;
        r4 = r0.currentChat;
        r4 = com.hanista.mobogram.messenger.ChatObject.isChannel(r4);
        if (r4 == 0) goto L_0x0053;
    L_0x241d:
        r23.updateBottomOverlay();
        goto L_0x0053;
    L_0x2422:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.didLoadedReplyMessages;
        r0 = r24;
        if (r0 != r4) goto L_0x243e;
    L_0x2428:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Long) r4;
        r4 = r4.longValue();
        r0 = r23;
        r6 = r0.dialog_id;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x0053;
    L_0x2439:
        r23.updateVisibleRows();
        goto L_0x0053;
    L_0x243e:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.didLoadedPinnedMessage;
        r0 = r24;
        if (r0 != r4) goto L_0x2478;
    L_0x2444:
        r4 = 0;
        r4 = r25[r4];
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
        r6 = r4.getDialogId();
        r0 = r23;
        r8 = r0.dialog_id;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 != 0) goto L_0x0053;
    L_0x2455:
        r0 = r23;
        r5 = r0.info;
        if (r5 == 0) goto L_0x0053;
    L_0x245b:
        r0 = r23;
        r5 = r0.info;
        r5 = r5.pinned_msg_id;
        r6 = r4.getId();
        if (r5 != r6) goto L_0x0053;
    L_0x2467:
        r0 = r23;
        r0.pinnedMessageObject = r4;
        r4 = 0;
        r0 = r23;
        r0.loadingPinnedMessage = r4;
        r4 = 1;
        r0 = r23;
        r0.updatePinnedMessageView(r4);
        goto L_0x0053;
    L_0x2478:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.didReceivedWebpages;
        r0 = r24;
        if (r0 != r4) goto L_0x250a;
    L_0x247e:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.util.ArrayList) r4;
        r6 = 0;
        r5 = 0;
        r7 = r5;
        r8 = r6;
    L_0x2487:
        r5 = r4.size();
        if (r7 >= r5) goto L_0x24e6;
    L_0x248d:
        r5 = r4.get(r7);
        r5 = (com.hanista.mobogram.tgnet.TLRPC.Message) r5;
        r10 = com.hanista.mobogram.messenger.MessageObject.getDialogId(r5);
        r0 = r23;
        r12 = r0.dialog_id;
        r6 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r6 == 0) goto L_0x24ad;
    L_0x249f:
        r0 = r23;
        r12 = r0.mergeDialogId;
        r6 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r6 == 0) goto L_0x24ad;
    L_0x24a7:
        r6 = r8;
    L_0x24a8:
        r5 = r7 + 1;
        r7 = r5;
        r8 = r6;
        goto L_0x2487;
    L_0x24ad:
        r0 = r23;
        r9 = r0.messagesDict;
        r0 = r23;
        r12 = r0.dialog_id;
        r6 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r6 != 0) goto L_0x24e4;
    L_0x24b9:
        r6 = 0;
    L_0x24ba:
        r6 = r9[r6];
        r9 = r5.id;
        r9 = java.lang.Integer.valueOf(r9);
        r6 = r6.get(r9);
        r6 = (com.hanista.mobogram.messenger.MessageObject) r6;
        if (r6 == 0) goto L_0x2847;
    L_0x24ca:
        r8 = r6.messageOwner;
        r9 = new com.hanista.mobogram.tgnet.TLRPC$TL_messageMediaWebPage;
        r9.<init>();
        r8.media = r9;
        r8 = r6.messageOwner;
        r8 = r8.media;
        r5 = r5.media;
        r5 = r5.webpage;
        r8.webpage = r5;
        r5 = 1;
        r6.generateThumbs(r5);
        r8 = 1;
        r6 = r8;
        goto L_0x24a8;
    L_0x24e4:
        r6 = 1;
        goto L_0x24ba;
    L_0x24e6:
        if (r8 == 0) goto L_0x0053;
    L_0x24e8:
        r23.updateVisibleRows();
        r0 = r23;
        r4 = r0.chatLayoutManager;
        if (r4 == 0) goto L_0x0053;
    L_0x24f1:
        r0 = r23;
        r4 = r0.chatLayoutManager;
        r4 = r4.findLastVisibleItemPosition();
        r0 = r23;
        r5 = r0.messages;
        r5 = r5.size();
        r5 = r5 + -1;
        if (r4 < r5) goto L_0x0053;
    L_0x2505:
        r23.moveScrollToLastMessage();
        goto L_0x0053;
    L_0x250a:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.didReceivedWebpagesInUpdates;
        r0 = r24;
        if (r0 != r4) goto L_0x254d;
    L_0x2510:
        r0 = r23;
        r4 = r0.foundWebPage;
        if (r4 == 0) goto L_0x0053;
    L_0x2516:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.util.HashMap) r4;
        r4 = r4.values();
        r4 = r4.iterator();
    L_0x2523:
        r5 = r4.hasNext();
        if (r5 == 0) goto L_0x0053;
    L_0x2529:
        r8 = r4.next();
        r8 = (com.hanista.mobogram.tgnet.TLRPC.WebPage) r8;
        r6 = r8.id;
        r0 = r23;
        r5 = r0.foundWebPage;
        r10 = r5.id;
        r5 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r5 != 0) goto L_0x2523;
    L_0x253b:
        r4 = r8 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_webPageEmpty;
        if (r4 != 0) goto L_0x254b;
    L_0x253f:
        r5 = 1;
    L_0x2540:
        r6 = 0;
        r7 = 0;
        r9 = 0;
        r10 = 1;
        r4 = r23;
        r4.showReplyPanel(r5, r6, r7, r8, r9, r10);
        goto L_0x0053;
    L_0x254b:
        r5 = 0;
        goto L_0x2540;
    L_0x254d:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.messagesReadContent;
        r0 = r24;
        if (r0 != r4) goto L_0x259b;
    L_0x2553:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.util.ArrayList) r4;
        r6 = 0;
        r5 = 0;
        r7 = r6;
        r6 = r5;
    L_0x255c:
        r5 = r4.size();
        if (r6 >= r5) goto L_0x2594;
    L_0x2562:
        r5 = r4.get(r6);
        r5 = (java.lang.Long) r5;
        r8 = r5.longValue();
        r0 = r23;
        r10 = r0.messagesDict;
        r0 = r23;
        r12 = r0.mergeDialogId;
        r14 = 0;
        r5 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r5 != 0) goto L_0x2592;
    L_0x257a:
        r5 = 0;
    L_0x257b:
        r5 = r10[r5];
        r8 = (int) r8;
        r8 = java.lang.Integer.valueOf(r8);
        r5 = r5.get(r8);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        if (r5 == 0) goto L_0x258e;
    L_0x258a:
        r5.setContentIsRead();
        r7 = 1;
    L_0x258e:
        r5 = r6 + 1;
        r6 = r5;
        goto L_0x255c;
    L_0x2592:
        r5 = 1;
        goto L_0x257b;
    L_0x2594:
        if (r7 == 0) goto L_0x0053;
    L_0x2596:
        r23.updateVisibleRows();
        goto L_0x0053;
    L_0x259b:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.botInfoDidLoaded;
        r0 = r24;
        if (r0 != r4) goto L_0x2630;
    L_0x25a1:
        r4 = 1;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
        r0 = r23;
        r5 = r0.classGuid;
        if (r5 != r4) goto L_0x0053;
    L_0x25b0:
        r4 = 0;
        r4 = r25[r4];
        r4 = (com.hanista.mobogram.tgnet.TLRPC.BotInfo) r4;
        r0 = r23;
        r5 = r0.currentEncryptedChat;
        if (r5 != 0) goto L_0x262b;
    L_0x25bb:
        r5 = r4.commands;
        r5 = r5.isEmpty();
        if (r5 != 0) goto L_0x25d2;
    L_0x25c3:
        r0 = r23;
        r5 = r0.currentChat;
        r5 = com.hanista.mobogram.messenger.ChatObject.isChannel(r5);
        if (r5 != 0) goto L_0x25d2;
    L_0x25cd:
        r5 = 1;
        r0 = r23;
        r0.hasBotsCommands = r5;
    L_0x25d2:
        r0 = r23;
        r5 = r0.botInfo;
        r6 = r4.user_id;
        r6 = java.lang.Integer.valueOf(r6);
        r5.put(r6, r4);
        r0 = r23;
        r4 = r0.chatAdapter;
        if (r4 == 0) goto L_0x25ed;
    L_0x25e5:
        r0 = r23;
        r4 = r0.chatAdapter;
        r5 = 0;
        r4.notifyItemChanged(r5);
    L_0x25ed:
        r0 = r23;
        r4 = r0.mentionsAdapter;
        if (r4 == 0) goto L_0x2616;
    L_0x25f3:
        r0 = r23;
        r4 = r0.currentChat;
        r4 = com.hanista.mobogram.messenger.ChatObject.isChannel(r4);
        if (r4 == 0) goto L_0x260b;
    L_0x25fd:
        r0 = r23;
        r4 = r0.currentChat;
        if (r4 == 0) goto L_0x2616;
    L_0x2603:
        r0 = r23;
        r4 = r0.currentChat;
        r4 = r4.megagroup;
        if (r4 == 0) goto L_0x2616;
    L_0x260b:
        r0 = r23;
        r4 = r0.mentionsAdapter;
        r0 = r23;
        r5 = r0.botInfo;
        r4.setBotInfo(r5);
    L_0x2616:
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        if (r4 == 0) goto L_0x262b;
    L_0x261c:
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        r0 = r23;
        r5 = r0.botsCount;
        r0 = r23;
        r6 = r0.hasBotsCommands;
        r4.setBotsCount(r5, r6);
    L_0x262b:
        r23.updateBotButtons();
        goto L_0x0053;
    L_0x2630:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.botKeyboardDidLoaded;
        r0 = r24;
        if (r0 != r4) goto L_0x273a;
    L_0x2636:
        r0 = r23;
        r6 = r0.dialog_id;
        r4 = 1;
        r4 = r25[r4];
        r4 = (java.lang.Long) r4;
        r4 = r4.longValue();
        r4 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
        if (r4 != 0) goto L_0x0053;
    L_0x2647:
        r4 = 0;
        r4 = r25[r4];
        r4 = (com.hanista.mobogram.tgnet.TLRPC.Message) r4;
        if (r4 == 0) goto L_0x2702;
    L_0x264e:
        r0 = r23;
        r5 = r0.userBlocked;
        if (r5 != 0) goto L_0x2702;
    L_0x2654:
        r5 = new com.hanista.mobogram.messenger.MessageObject;
        r6 = 0;
        r7 = 0;
        r5.<init>(r4, r6, r7);
        r0 = r23;
        r0.botButtons = r5;
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        if (r4 == 0) goto L_0x0053;
    L_0x2665:
        r0 = r23;
        r4 = r0.botButtons;
        r4 = r4.messageOwner;
        r4 = r4.reply_markup;
        r4 = r4 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_replyKeyboardForceReply;
        if (r4 == 0) goto L_0x26d5;
    L_0x2671:
        r4 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;
        r5 = "mainconfig";
        r6 = 0;
        r4 = r4.getSharedPreferences(r5, r6);
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "answered_";
        r5 = r5.append(r6);
        r0 = r23;
        r6 = r0.dialog_id;
        r5 = r5.append(r6);
        r5 = r5.toString();
        r6 = 0;
        r4 = r4.getInt(r5, r6);
        r0 = r23;
        r5 = r0.botButtons;
        r5 = r5.getId();
        if (r4 == r5) goto L_0x0053;
    L_0x26a2:
        r0 = r23;
        r4 = r0.replyingMessageObject;
        if (r4 == 0) goto L_0x26b2;
    L_0x26a8:
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        r4 = r4.getFieldText();
        if (r4 != 0) goto L_0x0053;
    L_0x26b2:
        r0 = r23;
        r4 = r0.botButtons;
        r0 = r23;
        r0.botReplyButtons = r4;
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        r0 = r23;
        r5 = r0.botButtons;
        r4.setButtons(r5);
        r5 = 1;
        r0 = r23;
        r6 = r0.botButtons;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 1;
        r4 = r23;
        r4.showReplyPanel(r5, r6, r7, r8, r9, r10);
        goto L_0x0053;
    L_0x26d5:
        r0 = r23;
        r4 = r0.replyingMessageObject;
        if (r4 == 0) goto L_0x26f5;
    L_0x26db:
        r0 = r23;
        r4 = r0.botReplyButtons;
        r0 = r23;
        r5 = r0.replyingMessageObject;
        if (r4 != r5) goto L_0x26f5;
    L_0x26e5:
        r4 = 0;
        r0 = r23;
        r0.botReplyButtons = r4;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 1;
        r4 = r23;
        r4.showReplyPanel(r5, r6, r7, r8, r9, r10);
    L_0x26f5:
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        r0 = r23;
        r5 = r0.botButtons;
        r4.setButtons(r5);
        goto L_0x0053;
    L_0x2702:
        r4 = 0;
        r0 = r23;
        r0.botButtons = r4;
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        if (r4 == 0) goto L_0x0053;
    L_0x270d:
        r0 = r23;
        r4 = r0.replyingMessageObject;
        if (r4 == 0) goto L_0x272d;
    L_0x2713:
        r0 = r23;
        r4 = r0.botReplyButtons;
        r0 = r23;
        r5 = r0.replyingMessageObject;
        if (r4 != r5) goto L_0x272d;
    L_0x271d:
        r4 = 0;
        r0 = r23;
        r0.botReplyButtons = r4;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 1;
        r4 = r23;
        r4.showReplyPanel(r5, r6, r7, r8, r9, r10);
    L_0x272d:
        r0 = r23;
        r4 = r0.chatActivityEnterView;
        r0 = r23;
        r5 = r0.botButtons;
        r4.setButtons(r5);
        goto L_0x0053;
    L_0x273a:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.chatSearchResultsAvailable;
        r0 = r24;
        if (r0 != r4) goto L_0x2797;
    L_0x2740:
        r0 = r23;
        r5 = r0.classGuid;
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
        if (r5 != r4) goto L_0x0053;
    L_0x274f:
        r4 = 1;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r5 = r4.intValue();
        r4 = 3;
        r4 = r25[r4];
        r4 = (java.lang.Long) r4;
        r6 = r4.longValue();
        if (r5 == 0) goto L_0x2773;
    L_0x2763:
        r8 = 0;
        r9 = 1;
        r0 = r23;
        r10 = r0.dialog_id;
        r4 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r4 != 0) goto L_0x2795;
    L_0x276d:
        r4 = 0;
    L_0x276e:
        r0 = r23;
        r0.scrollToMessageId(r5, r8, r9, r4);
    L_0x2773:
        r4 = 2;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r5 = r4.intValue();
        r4 = 4;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r6 = r4.intValue();
        r4 = 5;
        r4 = r25[r4];
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
        r0 = r23;
        r0.updateSearchButtons(r5, r6, r4);
        goto L_0x0053;
    L_0x2795:
        r4 = 1;
        goto L_0x276e;
    L_0x2797:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.didUpdatedMessagesViews;
        r0 = r24;
        if (r0 != r4) goto L_0x27ea;
    L_0x279d:
        r4 = 0;
        r4 = r25[r4];
        r4 = (android.util.SparseArray) r4;
        r0 = r23;
        r6 = r0.dialog_id;
        r5 = (int) r6;
        r4 = r4.get(r5);
        r4 = (android.util.SparseIntArray) r4;
        if (r4 == 0) goto L_0x0053;
    L_0x27af:
        r6 = 0;
        r5 = 0;
        r7 = r6;
        r6 = r5;
    L_0x27b3:
        r5 = r4.size();
        if (r6 >= r5) goto L_0x27e3;
    L_0x27b9:
        r8 = r4.keyAt(r6);
        r0 = r23;
        r5 = r0.messagesDict;
        r9 = 0;
        r5 = r5[r9];
        r9 = java.lang.Integer.valueOf(r8);
        r5 = r5.get(r9);
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;
        if (r5 == 0) goto L_0x27df;
    L_0x27d0:
        r8 = r4.get(r8);
        r9 = r5.messageOwner;
        r9 = r9.views;
        if (r8 <= r9) goto L_0x27df;
    L_0x27da:
        r5 = r5.messageOwner;
        r5.views = r8;
        r7 = 1;
    L_0x27df:
        r5 = r6 + 1;
        r6 = r5;
        goto L_0x27b3;
    L_0x27e3:
        if (r7 == 0) goto L_0x0053;
    L_0x27e5:
        r23.updateVisibleRows();
        goto L_0x0053;
    L_0x27ea:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.peerSettingsDidLoaded;
        r0 = r24;
        if (r0 != r4) goto L_0x2806;
    L_0x27f0:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Long) r4;
        r4 = r4.longValue();
        r0 = r23;
        r6 = r0.dialog_id;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x0053;
    L_0x2801:
        r23.updateSpamView();
        goto L_0x0053;
    L_0x2806:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.newDraftReceived;
        r0 = r24;
        if (r0 != r4) goto L_0x2825;
    L_0x280c:
        r4 = 0;
        r4 = r25[r4];
        r4 = (java.lang.Long) r4;
        r4 = r4.longValue();
        r0 = r23;
        r6 = r0.dialog_id;
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x0053;
    L_0x281d:
        r4 = 1;
        r0 = r23;
        r0.applyDraftMaybe(r4);
        goto L_0x0053;
    L_0x2825:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.dialogsNeedReload;
        r0 = r24;
        if (r0 != r4) goto L_0x283a;
    L_0x282b:
        r0 = r23;
        r4 = r0.chatBarUtil;
        if (r4 == 0) goto L_0x0053;
    L_0x2831:
        r0 = r23;
        r4 = r0.chatBarUtil;
        r4.m380c();
        goto L_0x0053;
    L_0x283a:
        r4 = com.hanista.mobogram.messenger.NotificationCenter.wallpaperChanged;
        r0 = r24;
        if (r0 != r4) goto L_0x0053;
    L_0x2840:
        r4 = 1;
        r0 = r23;
        r0.refreshWallpaper = r4;
        goto L_0x0053;
    L_0x2847:
        r6 = r8;
        goto L_0x24a8;
    L_0x284a:
        r6 = r11;
        r7 = r10;
        goto L_0x2360;
    L_0x284e:
        r6 = r7;
        r7 = r10;
        goto L_0x2360;
    L_0x2852:
        r8 = r9;
        r9 = r10;
        goto L_0x195f;
    L_0x2856:
        r6 = r17;
        goto L_0x16de;
    L_0x285a:
        r7 = r8;
        goto L_0x15a9;
    L_0x285d:
        r5 = r17;
        goto L_0x1465;
    L_0x2861:
        r5 = r15;
        goto L_0x1013;
    L_0x2864:
        r5 = r11;
        goto L_0x10e7;
    L_0x2867:
        r14 = r13;
        r5 = r15;
        r13 = r8;
        goto L_0x1013;
    L_0x286c:
        r13 = r14;
        goto L_0x137e;
    L_0x286f:
        r8 = r13;
        goto L_0x11d0;
    L_0x2872:
        r6 = r8;
        r11 = r14;
        r8 = r13;
        goto L_0x0d98;
    L_0x2877:
        r5 = r7;
        goto L_0x0080;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.ChatActivity.didReceivedNotification(int, java.lang.Object[]):void");
    }

    public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
        if (this.dialog_id == 0) {
            return;
        }
        if (this.forwaringMessage != null || !this.selectedMessagesIds[attach_photo].isEmpty() || !this.selectedMessagesIds[attach_gallery].isEmpty()) {
            ArrayList arrayList = new ArrayList();
            if (this.forwaringMessage != null) {
                arrayList.add(this.forwaringMessage);
                this.forwaringMessage = null;
            } else {
                for (int i = attach_gallery; i >= 0; i--) {
                    ArrayList arrayList2 = new ArrayList(this.selectedMessagesIds[i].keySet());
                    Collections.sort(arrayList2);
                    for (int i2 = attach_photo; i2 < arrayList2.size(); i2 += attach_gallery) {
                        Integer num = (Integer) arrayList2.get(i2);
                        MessageObject messageObject = (MessageObject) this.selectedMessagesIds[i].get(num);
                        if (messageObject != null && num.intValue() > 0) {
                            arrayList.add(messageObject);
                        }
                    }
                    this.selectedMessagesCanCopyIds[i].clear();
                    this.selectedMessagesIds[i].clear();
                }
                this.cantDeleteMessagesCount = attach_photo;
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
            }
            if (j != this.dialog_id) {
                int i3 = (int) j;
                if (i3 != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("scrollToTopOnResume", this.scrollToTopOnResume);
                    if (i3 > 0) {
                        bundle.putInt("user_id", i3);
                    } else if (i3 < 0) {
                        bundle.putInt("chat_id", -i3);
                    }
                    if (MessagesController.checkCanOpenChat(bundle, dialogsActivity)) {
                        BaseFragment chatActivity = new ChatActivity(bundle);
                        if (presentFragment(chatActivity, true)) {
                            chatActivity.showReplyPanel(true, null, arrayList, null, false, false);
                            if (!AndroidUtilities.isTablet()) {
                                removeSelfFromStack();
                                return;
                            }
                            return;
                        }
                        dialogsActivity.finishFragment();
                        return;
                    }
                    return;
                }
                dialogsActivity.finishFragment();
                return;
            }
            dialogsActivity.finishFragment();
            moveScrollToLastMessage();
            showReplyPanel(true, null, arrayList, null, false, AndroidUtilities.isTablet());
            if (AndroidUtilities.isTablet()) {
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
            }
            updateVisibleRows();
        }
    }

    public void dismissCurrentDialig() {
        if (this.chatAttachAlert == null || this.visibleDialog != this.chatAttachAlert) {
            super.dismissCurrentDialig();
            return;
        }
        this.chatAttachAlert.closeCamera(false);
        this.chatAttachAlert.dismissInternal();
        this.chatAttachAlert.hideCamera(true);
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert && super.dismissDialogOnPause(dialog);
    }

    public Archive getArchive() {
        return this.archive;
    }

    public Chat getCurrentChat() {
        return this.currentChat;
    }

    public ChatFull getCurrentChatInfo() {
        return this.info;
    }

    public EncryptedChat getCurrentEncryptedChat() {
        return this.currentEncryptedChat;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public long getDialogId() {
        return this.dialog_id;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        int childCount = this.chatListView.getChildCount();
        for (int i2 = attach_photo; i2 < childCount; i2 += attach_gallery) {
            ImageReceiver imageReceiver = null;
            View childAt = this.chatListView.getChildAt(i2);
            if (!(childAt instanceof ChatMessageCell)) {
                if (childAt instanceof ChatActionCell) {
                    ChatActionCell chatActionCell = (ChatActionCell) childAt;
                    MessageObject messageObject2 = chatActionCell.getMessageObject();
                    if (messageObject2 != null) {
                        if (messageObject == null) {
                            if (!(fileLocation == null || messageObject2.photoThumbs == null)) {
                                for (int i3 = attach_photo; i3 < messageObject2.photoThumbs.size(); i3 += attach_gallery) {
                                    PhotoSize photoSize = (PhotoSize) messageObject2.photoThumbs.get(i3);
                                    if (photoSize.location.volume_id == fileLocation.volume_id && photoSize.location.local_id == fileLocation.local_id) {
                                        imageReceiver = chatActionCell.getPhotoImage();
                                        break;
                                    }
                                }
                            }
                        } else if (messageObject2.getId() == messageObject.getId()) {
                            imageReceiver = chatActionCell.getPhotoImage();
                        }
                    }
                }
            } else if (messageObject != null) {
                ImageReceiver imageReceiver2;
                ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                MessageObject messageObject3 = chatMessageCell.getMessageObject();
                if (messageObject3 == null || messageObject3.getId() != messageObject.getId()) {
                    imageReceiver2 = null;
                } else {
                    ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                    if (messageObject3.isGif() || messageObject3.isNewGif()) {
                        this.currentGifCell = chatMessageCell;
                    }
                    imageReceiver2 = photoImage;
                }
                imageReceiver = imageReceiver2;
            }
            if (imageReceiver != null) {
                int[] iArr = new int[attach_video];
                childAt.getLocationInWindow(iArr);
                PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                placeProviderObject.viewX = iArr[attach_photo];
                placeProviderObject.viewY = iArr[attach_gallery] - (VERSION.SDK_INT >= report ? attach_photo : AndroidUtilities.statusBarHeight);
                placeProviderObject.parentView = this.chatListView;
                placeProviderObject.imageReceiver = imageReceiver;
                placeProviderObject.thumb = imageReceiver.getBitmap();
                placeProviderObject.radius = imageReceiver.getRoundRadius();
                if ((childAt instanceof ChatActionCell) && this.currentChat != null) {
                    placeProviderObject.dialogId = -this.currentChat.id;
                }
                if ((this.pinnedMessageView != null && this.pinnedMessageView.getTag() == null) || (this.reportSpamView != null && this.reportSpamView.getTag() == null)) {
                    placeProviderObject.clipTopAddition = AndroidUtilities.dp(48.0f);
                }
                return placeProviderObject;
            }
        }
        return null;
    }

    public int getSelectedCount() {
        return attach_photo;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        return null;
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    public boolean isSecretChat() {
        return this.currentEncryptedChat != null;
    }

    public boolean needDelayOpenAnimation() {
        return this.firstLoading;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        Throwable th;
        Cursor cursor;
        if (i2 != -1) {
            return;
        }
        if (i == 0) {
            int i3;
            boolean z;
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            ArrayList arrayList = new ArrayList();
            int i4 = attach_photo;
            try {
                switch (new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", attach_gallery)) {
                    case attach_audio /*3*/:
                        i4 = 180;
                        break;
                    case attach_location /*6*/:
                        i4 = 90;
                        break;
                    case TLRPC.USER_FLAG_USERNAME /*8*/:
                        i4 = 270;
                        break;
                }
                i3 = i4;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                i3 = attach_photo;
            }
            PhotoEntry photoEntry = new PhotoEntry(attach_photo, attach_photo, 0, this.currentPicturePath, i3, false);
            photoEntry.caption = this.currentPictureCaption;
            if (intent != null) {
                if (intent.getBooleanExtra("fromMarkers", false)) {
                    z = true;
                    arrayList.add(photoEntry);
                    PhotoViewer.getInstance().openPhotoForSelect(arrayList, attach_photo, attach_video, new AnonymousClass60(z, arrayList), this);
                    AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                    this.currentPicturePath = null;
                }
            }
            z = false;
            arrayList.add(photoEntry);
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, attach_photo, attach_video, new AnonymousClass60(z, arrayList), this);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        } else if (i == attach_gallery) {
            if (intent == null || intent.getData() == null) {
                showAttachmentError();
                return;
            }
            r3 = intent.getData();
            if (r3.toString().contains(MimeTypes.BASE_TYPE_VIDEO)) {
                try {
                    r3 = AndroidUtilities.getPath(r3);
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                    r3 = null;
                }
                if (r3 == null) {
                    showAttachmentError();
                }
                if (VERSION.SDK_INT < delete_chat) {
                    SendMessagesHelper.prepareSendingVideo(r3, 0, 0, attach_photo, attach_photo, null, this.dialog_id, this.replyingMessageObject, null);
                } else if (this.paused) {
                    this.startVideoEdit = r3;
                } else {
                    openVideoEditor(r3, false, false);
                }
            } else {
                SendMessagesHelper.prepareSendingPhoto(null, r3, this.dialog_id, this.replyingMessageObject, null, null);
            }
            showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(this.dialog_id, true);
        } else if (i == attach_video) {
            r2 = null;
            FileLog.m15d("tmessages", "pic path " + this.currentPicturePath);
            if (!(intent == null || this.currentPicturePath == null || !new File(this.currentPicturePath).exists())) {
                intent = null;
            }
            if (intent != null) {
                r2 = intent.getData();
                if (r2 != null) {
                    FileLog.m15d("tmessages", "video record uri " + r2.toString());
                    r2 = AndroidUtilities.getPath(r2);
                    FileLog.m15d("tmessages", "resolved path = " + r2);
                    if (!new File(r2).exists()) {
                        r2 = this.currentPicturePath;
                    }
                } else {
                    r2 = this.currentPicturePath;
                }
                AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                this.currentPicturePath = null;
            }
            if (r2 == null && this.currentPicturePath != null) {
                if (new File(this.currentPicturePath).exists()) {
                    r2 = this.currentPicturePath;
                }
                this.currentPicturePath = null;
            }
            r3 = r2;
            if (VERSION.SDK_INT < delete_chat) {
                SendMessagesHelper.prepareSendingVideo(r3, 0, 0, attach_photo, attach_photo, null, this.dialog_id, this.replyingMessageObject, null);
                showReplyPanel(false, null, null, null, false, true);
                DraftQuery.cleanDraft(this.dialog_id, true);
            } else if (this.paused) {
                this.startVideoEdit = r3;
            } else {
                openVideoEditor(r3, false, false);
            }
        } else if (i == report) {
            if (intent == null || intent.getData() == null) {
                showAttachmentError();
                return;
            }
            r2 = intent.getData();
            r3 = r2.toString();
            if (r3.contains("com.google.android.apps.photos.contentprovider")) {
                try {
                    r3 = r3.split("/1/")[attach_gallery];
                    int indexOf = r3.indexOf("/ACTUAL");
                    if (indexOf != -1) {
                        r2 = Uri.parse(URLDecoder.decode(r3.substring(attach_photo, indexOf), C0700C.UTF8_NAME));
                    }
                } catch (Throwable e22) {
                    FileLog.m18e("tmessages", e22);
                }
            }
            r3 = AndroidUtilities.getPath(r2);
            if (r3 == null) {
                r3 = intent.toString();
                r2 = MediaController.m77a(intent.getData(), "file");
            } else {
                r2 = r3;
            }
            if (r2 == null) {
                showAttachmentError();
                return;
            }
            SendMessagesHelper.prepareSendingDocument(r2, r3, null, null, this.dialog_id, this.replyingMessageObject);
            showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(this.dialog_id, true);
        } else if (i != bot_settings) {
        } else {
            if (intent == null || intent.getData() == null) {
                showAttachmentError();
                return;
            }
            r3 = intent.getData();
            try {
                ContentResolver contentResolver = getParentActivity().getContentResolver();
                String[] strArr = new String[attach_video];
                strArr[attach_photo] = "display_name";
                strArr[attach_gallery] = "data1";
                Cursor query = contentResolver.query(r3, strArr, null, null, null);
                if (query != null) {
                    Object obj = null;
                    while (query.moveToNext()) {
                        try {
                            r2 = query.getString(attach_photo);
                            String string = query.getString(attach_gallery);
                            User user = new User();
                            user.first_name = r2;
                            user.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                            user.phone = string;
                            SendMessagesHelper.getInstance().sendMessage(user, this.dialog_id, this.replyingMessageObject, null, null);
                            obj = attach_gallery;
                        } catch (Throwable th2) {
                            th = th2;
                            cursor = query;
                        }
                    }
                    if (obj != null) {
                        showReplyPanel(false, null, null, null, false, true);
                        DraftQuery.cleanDraft(this.dialog_id, true);
                    }
                }
                if (query != null) {
                    try {
                        if (!query.isClosed()) {
                            query.close();
                        }
                    } catch (Throwable th3) {
                        FileLog.m18e("tmessages", th3);
                    }
                }
            } catch (Throwable th4) {
                th3 = th4;
                cursor = null;
                if (cursor != null) {
                    try {
                        if (!cursor.isClosed()) {
                            cursor.close();
                        }
                    } catch (Throwable e222) {
                        FileLog.m18e("tmessages", e222);
                    }
                }
                throw th3;
            }
        }
    }

    public boolean onBackPressed() {
        if (this.deleteMutliMessageEnabled) {
            this.deleteMutliMessageEnabled = false;
            return true;
        } else if (this.selectionMode) {
            return true;
        } else {
            if (this.actionBar != null && this.actionBar.isActionModeShowed()) {
                for (int i = attach_gallery; i >= 0; i--) {
                    this.selectedMessagesIds[i].clear();
                    this.selectedMessagesCanCopyIds[i].clear();
                }
                this.chatActivityEnterView.setEditingMessageObject(null, false);
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                this.cantDeleteMessagesCount = attach_photo;
                updateVisibleRows();
                return false;
            } else if (this.chatActivityEnterView == null || !this.chatActivityEnterView.isPopupShowing()) {
                return true;
            } else {
                this.chatActivityEnterView.hidePopup(true);
                return false;
            }
        }
    }

    protected void onBecomeFullyVisible() {
        showMaterialHelp();
    }

    public void onConfigurationChanged(Configuration configuration) {
        fixLayout();
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (this.closeChatDialog != null && dialog == this.closeChatDialog) {
            MessagesController.getInstance().deleteDialog(this.dialog_id, attach_photo);
            if (this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() || this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this) {
                finishFragment();
                return;
            }
            BaseFragment baseFragment = (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1);
            removeSelfFromStack();
            baseFragment.finishFragment();
        }
    }

    public boolean onFragmentCreate() {
        Semaphore semaphore;
        int i = this.arguments.getInt("chat_id", attach_photo);
        int i2 = this.arguments.getInt("user_id", attach_photo);
        int i3 = this.arguments.getInt("enc_id", attach_photo);
        this.inlineReturn = this.arguments.getLong("inline_return", 0);
        String string = this.arguments.getString("inline_query");
        this.startLoadFromMessageId = this.arguments.getInt("message_id", attach_photo);
        int i4 = this.arguments.getInt("migrated_to", attach_photo);
        this.scrollToTopOnResume = this.arguments.getBoolean("scrollToTopOnResume", false);
        this.selectionMode = this.arguments.getBoolean("selection_mode", false);
        boolean z = this.arguments.getBoolean("show_just_favorites", false);
        long j = this.arguments.getLong("archive_id", -2);
        if (j == -1) {
            this.archive = new Archive();
            this.archive.m206a(Long.valueOf(-1));
            this.archive.m211e().addAll(ArchiveUtil.f143a);
        } else if (j == 0) {
            this.archive = new Archive();
            this.archive.m206a(Long.valueOf(0));
        } else if (j > 0) {
            this.archive = new DataBaseAccess().m904o(Long.valueOf(j));
        }
        this.justFromId = this.arguments.getInt("just_from_id", attach_photo);
        if (i != 0) {
            this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(i));
            if (this.currentChat == null) {
                Semaphore semaphore2 = new Semaphore(attach_photo);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C12614(i, semaphore2));
                try {
                    semaphore2.acquire();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                if (this.currentChat == null) {
                    return false;
                }
                MessagesController.getInstance().putChat(this.currentChat, true);
            }
            if (i > 0) {
                this.dialog_id = (long) (-i);
            } else {
                this.isBroadcast = true;
                this.dialog_id = AndroidUtilities.makeBroadcastId(i);
            }
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance().startShortPoll(i, false);
            }
        } else if (i2 != 0) {
            this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(i2));
            SpecificContactNotifController.m2023a(i2);
            if (this.currentUser == null) {
                semaphore = new Semaphore(attach_photo);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C12685(i2, semaphore));
                try {
                    semaphore.acquire();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
                if (this.currentUser == null) {
                    return false;
                }
                MessagesController.getInstance().putUser(this.currentUser, true);
            }
            this.dialog_id = (long) i2;
            this.botUser = this.arguments.getString("botUser");
            if (string != null) {
                MessagesController.getInstance().sendBotStart(this.currentUser, string);
            }
        } else if (i3 == 0) {
            return false;
        } else {
            this.currentEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i3));
            if (this.currentEncryptedChat == null) {
                semaphore = new Semaphore(attach_photo);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C12726(i3, semaphore));
                try {
                    semaphore.acquire();
                } catch (Throwable e22) {
                    FileLog.m18e("tmessages", e22);
                }
                if (this.currentEncryptedChat == null) {
                    return false;
                }
                MessagesController.getInstance().putEncryptedChat(this.currentEncryptedChat, true);
            }
            this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentEncryptedChat.user_id));
            if (this.currentUser == null) {
                semaphore = new Semaphore(attach_photo);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C12747(semaphore));
                try {
                    semaphore.acquire();
                } catch (Throwable e222) {
                    FileLog.m18e("tmessages", e222);
                }
                if (this.currentUser == null) {
                    return false;
                }
                MessagesController.getInstance().putUser(this.currentUser, true);
            }
            this.dialog_id = ((long) i3) << 32;
            int[] iArr = this.maxMessageId;
            this.maxMessageId[attach_gallery] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            iArr[attach_photo] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            iArr = this.minMessageId;
            this.minMessageId[attach_gallery] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            iArr[attach_photo] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            MediaController.m71a().m173e();
        }
        if (!HiddenConfig.m1399b(Long.valueOf(this.dialog_id)) || this.arguments.getBoolean("hiddenMode", false) || HiddenConfig.f1402e) {
            if (z) {
                this.archive = new Archive();
                this.archive.m206a(Long.valueOf(1));
                if (ArchiveUtil.f144b.get(Long.valueOf(this.dialog_id)) != null) {
                    this.archive.m211e().addAll((Collection) ArchiveUtil.f144b.get(Long.valueOf(this.dialog_id)));
                }
            }
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesRead);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesReadEncrypted);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.screenshotTook);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.blockedUsersDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didCreatedNewDeleteTask);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateMessageMedia);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.replaceMessagesObjects);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedWebpages);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedWebpagesInUpdates);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesReadContent);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.botInfoDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.botKeyboardDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatSearchResultsAvailable);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdatedMessagesViews);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoCantLoad);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedPinnedMessage);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.peerSettingsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.newDraftReceived);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.wallpaperChanged);
            super.onFragmentCreate();
            if (this.currentEncryptedChat == null && !this.isBroadcast) {
                BotQuery.loadBotKeyboard(this.dialog_id);
            }
            this.loading = true;
            MessagesController.getInstance().loadPeerSettings(this.dialog_id, this.currentUser, this.currentChat);
            MessagesController.getInstance().setLastCreatedDialogId(this.dialog_id, true);
            MessagesController instance;
            long j2;
            int i5;
            int i6;
            boolean isChannel;
            int i7;
            if (this.startLoadFromMessageId != 0) {
                this.needSelectFromMessageId = true;
                this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                int i8;
                if (i4 != 0) {
                    this.mergeDialogId = (long) i4;
                    instance = MessagesController.getInstance();
                    j2 = this.mergeDialogId;
                    i5 = AndroidUtilities.isTablet() ? bot_help : edit_done;
                    i8 = this.startLoadFromMessageId;
                    i6 = this.classGuid;
                    isChannel = ChatObject.isChannel(this.currentChat);
                    i7 = this.lastLoadIndex;
                    this.lastLoadIndex = i7 + attach_gallery;
                    instance.loadMessages(j2, i5, i8, true, attach_photo, i6, attach_audio, attach_photo, isChannel, i7);
                } else {
                    instance = MessagesController.getInstance();
                    j2 = this.dialog_id;
                    i5 = AndroidUtilities.isTablet() ? bot_help : edit_done;
                    i8 = this.startLoadFromMessageId;
                    i6 = this.classGuid;
                    isChannel = ChatObject.isChannel(this.currentChat);
                    i7 = this.lastLoadIndex;
                    this.lastLoadIndex = i7 + attach_gallery;
                    instance.loadMessages(j2, i5, i8, true, attach_photo, i6, attach_audio, attach_photo, isChannel, i7);
                }
            } else {
                this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                instance = MessagesController.getInstance();
                j2 = this.dialog_id;
                i5 = AndroidUtilities.isTablet() ? bot_help : edit_done;
                i6 = this.classGuid;
                isChannel = ChatObject.isChannel(this.currentChat);
                i7 = this.lastLoadIndex;
                this.lastLoadIndex = i7 + attach_gallery;
                instance.loadMessages(j2, i5, attach_photo, true, attach_photo, i6, attach_video, attach_photo, isChannel, i7);
            }
            if (this.currentChat != null) {
                semaphore = null;
                if (this.isBroadcast) {
                    semaphore = new Semaphore(attach_photo);
                }
                MessagesController.getInstance().loadChatInfo(this.currentChat.id, semaphore, ChatObject.isChannel(this.currentChat));
                if (this.isBroadcast && semaphore != null) {
                    try {
                        semaphore.acquire();
                    } catch (Throwable e2222) {
                        FileLog.m18e("tmessages", e2222);
                    }
                }
            }
            if (i2 != 0 && this.currentUser.bot) {
                BotQuery.loadBotInfo(i2, true, this.classGuid);
            } else if (this.info instanceof TL_chatFull) {
                for (i3 = attach_photo; i3 < this.info.participants.participants.size(); i3 += attach_gallery) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(i3)).user_id));
                    if (user != null && user.bot) {
                        BotQuery.loadBotInfo(user.id, true, this.classGuid);
                    }
                }
            }
            if (this.currentUser != null) {
                this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.currentUser.id));
            }
            if (AndroidUtilities.isTablet()) {
                NotificationCenter instance2 = NotificationCenter.getInstance();
                i3 = NotificationCenter.openedChatChanged;
                Object[] objArr = new Object[attach_video];
                objArr[attach_photo] = Long.valueOf(this.dialog_id);
                objArr[attach_gallery] = Boolean.valueOf(false);
                instance2.postNotificationName(i3, objArr);
            }
            if (!(this.currentEncryptedChat == null || AndroidUtilities.getMyLayerVersion(this.currentEncryptedChat.layer) == 46)) {
                SecretChatHelper.getInstance().sendNotifyLayerMessage(this.currentEncryptedChat, null);
            }
            this.dialogSettings = DialogSettingsUtil.m997a(this.dialog_id);
            if (this.dialogSettings == null) {
                this.dialogSettings = new DialogSettings();
                this.dialogSettings.m978b(Long.valueOf(this.dialog_id));
            }
            return true;
        }
        Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("ChatIsHidden", C0338R.string.ChatIsHidden), attach_photo).show();
        return false;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onDestroy();
        }
        if (this.mentionsAdapter != null) {
            this.mentionsAdapter.onDestroy();
        }
        MessagesController.getInstance().setLastCreatedDialogId(this.dialog_id, false);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesRead);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesReadEncrypted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.screenshotTook);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.replaceMessagesObjects);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedWebpages);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedWebpagesInUpdates);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesReadContent);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.botInfoDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.botKeyboardDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatSearchResultsAvailable);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdatedMessagesViews);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoCantLoad);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedPinnedMessage);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.peerSettingsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.newDraftReceived);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.wallpaperChanged);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.openedChatChanged;
            Object[] objArr = new Object[attach_video];
            objArr[attach_photo] = Long.valueOf(this.dialog_id);
            objArr[attach_gallery] = Boolean.valueOf(true);
            instance.postNotificationName(i, objArr);
        }
        if (this.currentEncryptedChat != null) {
            MediaController.m71a().m175f();
            try {
                if (VERSION.SDK_INT >= 23) {
                    getParentActivity().getWindow().clearFlags(MessagesController.UPDATE_MASK_CHANNEL);
                }
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
            }
        }
        if (this.currentUser != null) {
            MessagesController.getInstance().cancelLoadFullUser(this.currentUser.id);
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (this.stickersAdapter != null) {
            this.stickersAdapter.onDestroy();
        }
        if (this.chatAttachAlert != null) {
            this.chatAttachAlert.onDestroy();
        }
        AndroidUtilities.unlockOrientation(getParentActivity());
        if (ChatObject.isChannel(this.currentChat)) {
            MessagesController.getInstance().startShortPoll(this.currentChat.id, true);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPause() {
        /*
        r9 = this;
        r4 = 0;
        r6 = 1;
        r7 = 0;
        super.onPause();
        r0 = com.hanista.mobogram.messenger.MediaController.m71a();
        r0.m164b(r9);
        r0 = r9.menuItem;
        if (r0 == 0) goto L_0x0016;
    L_0x0011:
        r0 = r9.menuItem;
        r0.closeSubMenu();
    L_0x0016:
        r0 = r9.chatAttachAlert;
        if (r0 == 0) goto L_0x001f;
    L_0x001a:
        r0 = r9.chatAttachAlert;
        r0.onPause();
    L_0x001f:
        r9.paused = r6;
        r9.wasPaused = r6;
        r0 = com.hanista.mobogram.messenger.NotificationsController.getInstance();
        r2 = 0;
        r0.setOpenedDialogId(r2);
        r0 = r9.chatActivityEnterView;
        if (r0 == 0) goto L_0x0097;
    L_0x0030:
        r0 = r9.chatActivityEnterView;
        r0.onPause();
        r0 = r9.chatActivityEnterView;
        r0 = r0.isEditingMessage();
        if (r0 != 0) goto L_0x0095;
    L_0x003d:
        r0 = r9.chatActivityEnterView;
        r0 = r0.getFieldText();
        r0 = com.hanista.mobogram.messenger.AndroidUtilities.getTrimmedString(r0);
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 != 0) goto L_0x0095;
    L_0x004d:
        r1 = "@gif";
        r1 = android.text.TextUtils.equals(r0, r1);
        if (r1 != 0) goto L_0x0095;
    L_0x0056:
        r1 = r9.chatActivityEnterView;
        r1 = r1.isMessageWebPageSearchEnabled();
        r2 = r9.chatActivityEnterView;
        r2.setFieldFocused(r7);
        r5 = r1;
    L_0x0062:
        r2 = new java.lang.CharSequence[r6];
        r2[r7] = r0;
        r3 = com.hanista.mobogram.messenger.query.MessagesQuery.getEntities(r2);
        r0 = r9.dialog_id;
        r2 = r2[r7];
        r8 = r9.replyingMessageObject;
        if (r8 == 0) goto L_0x0076;
    L_0x0072:
        r4 = r9.replyingMessageObject;
        r4 = r4.messageOwner;
    L_0x0076:
        if (r5 != 0) goto L_0x0093;
    L_0x0078:
        r5 = r6;
    L_0x0079:
        com.hanista.mobogram.messenger.query.DraftQuery.saveDraft(r0, r2, r3, r4, r5);
        r0 = com.hanista.mobogram.messenger.MessagesController.getInstance();
        r2 = r9.dialog_id;
        r0.cancelTyping(r7, r2);
        r0 = r9.currentEncryptedChat;
        if (r0 == 0) goto L_0x0092;
    L_0x0089:
        r0 = java.lang.System.currentTimeMillis();
        r9.chatLeaveTime = r0;
        r9.updateInformationForScreenshotDetector();
    L_0x0092:
        return;
    L_0x0093:
        r5 = r7;
        goto L_0x0079;
    L_0x0095:
        r0 = r4;
        goto L_0x0056;
    L_0x0097:
        r5 = r6;
        r0 = r4;
        goto L_0x0062;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.ChatActivity.onPause():void");
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        if (this.mentionsAdapter != null) {
            this.mentionsAdapter.onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        if (i == share_contact && this.chatAttachAlert != null) {
            this.chatAttachAlert.checkCamera(false);
        } else if (i == reply && iArr != null && iArr.length > 0 && iArr[attach_photo] == 0) {
            processSelectedAttach(attach_photo);
        } else if (i == edit_done && iArr != null && iArr.length > 0 && iArr[attach_photo] == 0) {
            processSelectedAttach(attach_video);
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        MediaController.m71a().m150a(this);
        checkRaiseSensors();
        checkActionBarMenu();
        if (!(this.replyImageLocation == null || this.replyImageView == null)) {
            this.replyImageView.setImage(this.replyImageLocation, "50_50", (Drawable) null);
        }
        if (!(this.pinnedImageLocation == null || this.pinnedMessageImageView == null)) {
            this.pinnedMessageImageView.setImage(this.pinnedImageLocation, "50_50", (Drawable) null);
        }
        NotificationsController.getInstance().setOpenedDialogId(this.dialog_id);
        if (this.scrollToTopOnResume) {
            if (!this.scrollToTopUnReadOnResume || this.scrollToMessage == null) {
                moveScrollToLastMessage();
            } else if (this.chatListView != null) {
                int max = this.scrollToMessagePosition == -9000 ? Math.max(attach_photo, (this.chatListView.getHeight() - this.scrollToMessage.getApproximateHeight()) / attach_video) : this.scrollToMessagePosition == -10000 ? attach_photo : this.scrollToMessagePosition;
                this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - this.messages.indexOf(this.scrollToMessage), max + ((-this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f)));
            }
            this.scrollToTopUnReadOnResume = false;
            this.scrollToTopOnResume = false;
            this.scrollToMessage = null;
        }
        this.paused = false;
        if (this.readWhenResume && !this.messages.isEmpty()) {
            Iterator it = this.messages.iterator();
            while (it.hasNext()) {
                MessageObject messageObject = (MessageObject) it.next();
                if (!messageObject.isUnread() && !messageObject.isOut()) {
                    break;
                } else if (!messageObject.isOut()) {
                    messageObject.setIsRead();
                }
            }
            this.readWhenResume = false;
            MessagesController.getInstance().markDialogAsRead(this.dialog_id, ((MessageObject) this.messages.get(attach_photo)).getId(), this.readWithMid, this.readWithDate, true, false);
        }
        checkScrollForLoad(false);
        if (this.wasPaused) {
            this.wasPaused = false;
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyDataSetChanged();
            }
        }
        fixLayout();
        applyDraftMaybe(false);
        if (!(this.bottomOverlayChat == null || this.bottomOverlayChat.getVisibility() == 0)) {
            this.chatActivityEnterView.setFieldFocused(true);
        }
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onResume();
        }
        if (this.currentEncryptedChat != null) {
            this.chatEnterTime = System.currentTimeMillis();
            this.chatLeaveTime = 0;
        }
        if (this.startVideoEdit != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    ChatActivity.this.openVideoEditor(ChatActivity.this.startVideoEdit, false, false);
                    ChatActivity.this.startVideoEdit = null;
                }
            });
        }
        if (this.chatListView != null && (this.chatActivityEnterView == null || !this.chatActivityEnterView.isEditingMessage())) {
            this.chatListView.setOnItemLongClickListener(this.onItemLongClickListener);
            this.chatListView.setOnItemClickListener(this.onItemClickListener);
            this.chatListView.setLongClickable(true);
        }
        checkBotCommands();
        initTheme();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        NotificationCenter.getInstance().setAnimationInProgress(false);
        if (z) {
            this.openAnimationEnded = true;
            if (this.currentUser != null) {
                MessagesController.getInstance().loadFullUser(this.currentUser, this.classGuid, false);
            }
            if (VERSION.SDK_INT >= report) {
                createChatAttachView();
            }
        }
    }

    public void onTransitionAnimationStart(boolean z, boolean z2) {
        NotificationCenter instance = NotificationCenter.getInstance();
        int[] iArr = new int[attach_contact];
        iArr[attach_photo] = NotificationCenter.chatInfoDidLoaded;
        iArr[attach_gallery] = NotificationCenter.dialogsNeedReload;
        iArr[attach_video] = NotificationCenter.closeChats;
        iArr[attach_audio] = NotificationCenter.messagesDidLoaded;
        iArr[attach_document] = NotificationCenter.botKeyboardDidLoaded;
        instance.setAllowedNotificationsDutingAnimation(iArr);
        NotificationCenter.getInstance().setAnimationInProgress(true);
        if (z) {
            this.openAnimationEnded = false;
        }
    }

    public boolean openVideoEditor(String str, boolean z, boolean z2) {
        Bundle bundle = new Bundle();
        bundle.putString("videoPath", str);
        BaseFragment videoEditorActivity = new VideoEditorActivity(bundle);
        videoEditorActivity.setDelegate(new VideoEditorActivityDelegate() {
            public void didFinishEditVideo(String str, long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, long j3, long j4, String str2) {
                VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
                videoEditedInfo.startTime = j;
                videoEditedInfo.endTime = j2;
                videoEditedInfo.rotationValue = i3;
                videoEditedInfo.originalWidth = i4;
                videoEditedInfo.originalHeight = i5;
                videoEditedInfo.bitrate = i6;
                videoEditedInfo.resultWidth = i;
                videoEditedInfo.resultHeight = i2;
                videoEditedInfo.originalPath = str;
                SendMessagesHelper.prepareSendingVideo(str, j3, j4, i, i2, videoEditedInfo, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, str2);
                ChatActivity.this.showReplyPanel(false, null, null, null, false, true);
                DraftQuery.cleanDraft(ChatActivity.this.dialog_id, true);
            }
        });
        if (this.parentLayout == null || !videoEditorActivity.onFragmentCreate()) {
            SendMessagesHelper.prepareSendingVideo(str, 0, 0, attach_photo, attach_photo, null, this.dialog_id, this.replyingMessageObject, null);
            showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(this.dialog_id, true);
            return false;
        }
        if (this.parentLayout.presentFragment(videoEditorActivity, z, !z2, true)) {
            videoEditorActivity.setParentChatActivity(this);
        }
        return true;
    }

    public boolean playFirstUnreadVoiceMessage() {
        for (int size = this.messages.size() - 1; size >= 0; size--) {
            MessageObject messageObject = (MessageObject) this.messages.get(size);
            if (messageObject.isVoice() && messageObject.isContentUnread() && !messageObject.isOut() && messageObject.messageOwner.to_id.channel_id == 0) {
                MediaController.m71a().m153a(MediaController.m71a().m158a(messageObject) ? createVoiceMessagesPlaylist(messageObject, true) : null, true);
                return true;
            }
        }
        if (VERSION.SDK_INT < 23 || getParentActivity() == null || getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            return false;
        }
        Activity parentActivity = getParentActivity();
        String[] strArr = new String[attach_gallery];
        strArr[attach_photo] = "android.permission.RECORD_AUDIO";
        parentActivity.requestPermissions(strArr, attach_audio);
        return true;
    }

    public void processInlineBotContextPM(TL_inlineBotSwitchPM tL_inlineBotSwitchPM) {
        if (tL_inlineBotSwitchPM != null) {
            User contextBotUser = this.mentionsAdapter.getContextBotUser();
            if (contextBotUser != null) {
                this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                if (this.dialog_id == ((long) contextBotUser.id)) {
                    this.inlineReturn = this.dialog_id;
                    MessagesController.getInstance().sendBotStart(this.currentUser, tL_inlineBotSwitchPM.start_param);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", contextBotUser.id);
                bundle.putString("inline_query", tL_inlineBotSwitchPM.start_param);
                bundle.putLong("inline_return", this.dialog_id);
                if (MessagesController.checkCanOpenChat(bundle, this)) {
                    presentFragment(new ChatActivity(bundle));
                }
            }
        }
    }

    public boolean processSendingText(String str) {
        return this.chatActivityEnterView.processSendingText(str);
    }

    public boolean processSwitchButton(TL_keyboardButtonSwitchInline tL_keyboardButtonSwitchInline) {
        if (this.inlineReturn == 0 || tL_keyboardButtonSwitchInline.same_peer) {
            return false;
        }
        CharSequence charSequence = "@" + this.currentUser.username + " " + tL_keyboardButtonSwitchInline.query;
        if (this.inlineReturn == this.dialog_id) {
            this.inlineReturn = 0;
            this.chatActivityEnterView.setFieldText(charSequence);
        } else {
            DraftQuery.saveDraft(this.inlineReturn, charSequence, null, null, false);
            if (this.parentLayout.fragmentsStack.size() > attach_gallery) {
                BaseFragment baseFragment = (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2);
                if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).dialog_id == this.inlineReturn) {
                    finishFragment();
                } else {
                    Bundle bundle = new Bundle();
                    int i = (int) this.inlineReturn;
                    int i2 = (int) (this.inlineReturn >> 32);
                    if (i == 0) {
                        bundle.putInt("enc_id", i2);
                    } else if (i > 0) {
                        bundle.putInt("user_id", i);
                    } else if (i < 0) {
                        bundle.putInt("chat_id", -i);
                    }
                    presentFragment(new ChatActivity(bundle), true);
                }
            }
        }
        return true;
    }

    public void restoreSelfArgs(Bundle bundle) {
        this.currentPicturePath = bundle.getString("path");
    }

    public void saveSelfArgs(Bundle bundle) {
        if (this.currentPicturePath != null) {
            bundle.putString("path", this.currentPicturePath);
        }
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
    }

    public void sendMedia(PhotoEntry photoEntry, boolean z) {
        if (photoEntry.isVideo) {
            VideoEditedInfo videoEditedInfo = null;
            long j = 0;
            if (z) {
                videoEditedInfo = new VideoEditedInfo();
                videoEditedInfo.bitrate = -1;
                videoEditedInfo.originalPath = photoEntry.path;
                videoEditedInfo.endTime = -1;
                videoEditedInfo.startTime = -1;
                j = new File(photoEntry.path).length();
            }
            SendMessagesHelper.prepareSendingVideo(photoEntry.path, j, 0, attach_photo, attach_photo, videoEditedInfo, this.dialog_id, this.replyingMessageObject, photoEntry.caption != null ? photoEntry.caption.toString() : null);
        } else if (photoEntry.imagePath != null) {
            SendMessagesHelper.prepareSendingPhoto(photoEntry.imagePath, null, this.dialog_id, this.replyingMessageObject, photoEntry.caption, photoEntry.stickers);
            showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(this.dialog_id, true);
        } else if (photoEntry.path != null) {
            SendMessagesHelper.prepareSendingPhoto(photoEntry.path, null, this.dialog_id, this.replyingMessageObject, photoEntry.caption, photoEntry.stickers);
            showReplyPanel(false, null, null, null, false, true);
            DraftQuery.cleanDraft(this.dialog_id, true);
        }
    }

    public void setBotUser(String str) {
        if (this.inlineReturn != 0) {
            MessagesController.getInstance().sendBotStart(this.currentUser, str);
            return;
        }
        this.botUser = str;
        updateBottomOverlay();
    }

    public void setPhotoChecked(int i) {
    }

    public void setSelectionDelegate(MessageSelectionDelegate messageSelectionDelegate) {
        this.selectionDelegate = messageSelectionDelegate;
    }

    public void shareMyContact(MessageObject messageObject) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("ShareYouPhoneNumberTitle", C0338R.string.ShareYouPhoneNumberTitle));
        if (this.currentUser == null) {
            builder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfo", C0338R.string.AreYouSureShareMyContactInfo));
        } else if (this.currentUser.bot) {
            builder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfoBot", C0338R.string.AreYouSureShareMyContactInfoBot));
        } else {
            Object[] objArr = new Object[attach_video];
            objArr[attach_photo] = PhoneFormat.getInstance().format("+" + UserConfig.getCurrentUser().phone);
            objArr[attach_gallery] = ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name);
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureShareMyContactInfoUser", C0338R.string.AreYouSureShareMyContactInfoUser, objArr)));
        }
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass48(messageObject));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    public void showAlert(String str, String str2) {
        if (this.alertView != null && str != null && str2 != null) {
            if (this.alertView.getTag() != null) {
                this.alertView.setTag(null);
                if (this.alertViewAnimator != null) {
                    this.alertViewAnimator.cancel();
                    this.alertViewAnimator = null;
                }
                this.alertView.setVisibility(attach_photo);
                this.alertViewAnimator = new AnimatorSet();
                AnimatorSet animatorSet = this.alertViewAnimator;
                Animator[] animatorArr = new Animator[attach_gallery];
                float[] fArr = new float[attach_gallery];
                fArr[attach_photo] = 0.0f;
                animatorArr[attach_photo] = ObjectAnimator.ofFloat(this.alertView, "translationY", fArr);
                animatorSet.playTogether(animatorArr);
                this.alertViewAnimator.setDuration(200);
                this.alertViewAnimator.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationCancel(Animator animator) {
                        if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animator)) {
                            ChatActivity.this.alertViewAnimator = null;
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animator)) {
                            ChatActivity.this.alertViewAnimator = null;
                        }
                    }
                });
                this.alertViewAnimator.start();
            }
            this.alertNameTextView.setText(str);
            this.alertTextView.setText(Emoji.replaceEmoji(str2.replace('\n', ' '), this.alertTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
            if (this.hideAlertViewRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.hideAlertViewRunnable);
            }
            Runnable anonymousClass66 = new Runnable() {

                /* renamed from: com.hanista.mobogram.ui.ChatActivity.66.1 */
                class C12711 extends AnimatorListenerAdapterProxy {
                    C12711() {
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animator)) {
                            ChatActivity.this.alertViewAnimator = null;
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animator)) {
                            ChatActivity.this.alertView.setVisibility(8);
                            ChatActivity.this.alertViewAnimator = null;
                        }
                    }
                }

                public void run() {
                    if (ChatActivity.this.hideAlertViewRunnable == this && ChatActivity.this.alertView.getTag() == null) {
                        ChatActivity.this.alertView.setTag(Integer.valueOf(ChatActivity.attach_gallery));
                        if (ChatActivity.this.alertViewAnimator != null) {
                            ChatActivity.this.alertViewAnimator.cancel();
                            ChatActivity.this.alertViewAnimator = null;
                        }
                        ChatActivity.this.alertViewAnimator = new AnimatorSet();
                        AnimatorSet access$15300 = ChatActivity.this.alertViewAnimator;
                        Animator[] animatorArr = new Animator[ChatActivity.attach_gallery];
                        float[] fArr = new float[ChatActivity.attach_gallery];
                        fArr[ChatActivity.attach_photo] = (float) (-AndroidUtilities.dp(50.0f));
                        animatorArr[ChatActivity.attach_photo] = ObjectAnimator.ofFloat(ChatActivity.this.alertView, "translationY", fArr);
                        access$15300.playTogether(animatorArr);
                        ChatActivity.this.alertViewAnimator.setDuration(200);
                        ChatActivity.this.alertViewAnimator.addListener(new C12711());
                        ChatActivity.this.alertViewAnimator.start();
                    }
                }
            };
            this.hideAlertViewRunnable = anonymousClass66;
            AndroidUtilities.runOnUIThread(anonymousClass66, 3000);
        }
    }

    public void showArchiveListForSelectDialog() {
        List<Archive> a = ArchiveUtil.m259a(true);
        Builder builder = new Builder(getParentActivity());
        ArrayList arrayList = new ArrayList();
        for (Archive b : a) {
            arrayList.add(b.m208b());
        }
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new AnonymousClass97(a));
        builder.setTitle(LocaleController.getString("SelectCategory", C0338R.string.SelectCategory));
        showDialog(builder.create());
    }

    public void showArchiveListForSelectDialog(MessageObject messageObject) {
        if (messageObject != null) {
            List<Archive> a = ArchiveUtil.m259a(false);
            if (a.size() == 0) {
                addMessageToArchive(Long.valueOf(-1), messageObject);
                return;
            }
            Builder builder = new Builder(getParentActivity());
            ArrayList arrayList = new ArrayList();
            for (Archive b : a) {
                arrayList.add(b.m208b());
            }
            arrayList.add(LocaleController.getString("NewArchiveCategory", C0338R.string.NewArchiveCategory));
            arrayList.add(LocaleController.getString("NotCategorizedParan", C0338R.string.NotCategorizedParan));
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new AnonymousClass96(a, messageObject));
            builder.setTitle(LocaleController.getString("SelectCategory", C0338R.string.SelectCategory));
            showDialog(builder.create());
        }
    }

    public void showOpenGameAlert(TL_game tL_game, MessageObject messageObject, String str, boolean z, int i) {
        User user = MessagesController.getInstance().getUser(Integer.valueOf(i));
        if (z) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            Object[] objArr = new Object[attach_gallery];
            objArr[attach_photo] = user != null ? ContactsController.formatName(user.first_name, user.last_name) : TtmlNode.ANONYMOUS_REGION_ID;
            builder.setMessage(LocaleController.formatString("BotPermissionGameAlert", C0338R.string.BotPermissionGameAlert, objArr));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new AnonymousClass86(tL_game, messageObject, str, i));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            showDialog(builder.create());
        } else if (VERSION.SDK_INT < report || AndroidUtilities.isTablet() || !WebviewActivity.supportWebview()) {
            Activity parentActivity = getParentActivity();
            String str2 = tL_game.short_name;
            String str3 = (user == null || user.username == null) ? TtmlNode.ANONYMOUS_REGION_ID : user.username;
            WebviewActivity.openGameInBrowser(str, messageObject, parentActivity, str2, str3);
        } else if (this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this) {
            String str4 = (user == null || TextUtils.isEmpty(user.username)) ? TtmlNode.ANONYMOUS_REGION_ID : user.username;
            presentFragment(new WebviewActivity(str, str4, tL_game.title, tL_game.short_name, messageObject));
        }
    }

    public void showOpenUrlAlert(String str, boolean z) {
        boolean z2 = true;
        if (Browser.isInternalUrl(str) || !z) {
            Context parentActivity = getParentActivity();
            if (this.inlineReturn != 0) {
                z2 = false;
            }
            Browser.openUrl(parentActivity, str, z2);
            return;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        Object[] objArr = new Object[attach_gallery];
        objArr[attach_photo] = str;
        builder.setMessage(LocaleController.formatString("OpenUrlAlert", C0338R.string.OpenUrlAlert, objArr));
        builder.setPositiveButton(LocaleController.getString("Open", C0338R.string.Open), new AnonymousClass87(str));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    public void showReplyPanel(boolean z, MessageObject messageObject, ArrayList<MessageObject> arrayList, WebPage webPage, boolean z2, boolean z3) {
        if (this.chatActivityEnterView != null) {
            if (z) {
                if (messageObject != null || arrayList != null || webPage != null) {
                    boolean z4;
                    MessageObject messageObject2;
                    ArrayList arrayList2;
                    int i = AdvanceTheme.f2491b;
                    int i2 = AdvanceTheme.bH;
                    int i3 = AdvanceTheme.bG;
                    if (i2 != -1) {
                        i = -5395027;
                    }
                    int c = AdvanceTheme.m2286c(i3, i);
                    i = AdvanceTheme.m2286c(AdvanceTheme.bI, Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
                    if (ThemeUtil.m2490b()) {
                        this.replyObjectTextView.setTextColor(i);
                    }
                    if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                        this.actionBar.closeSearchField();
                        this.chatActivityEnterView.setFieldFocused();
                    }
                    if (messageObject == null || messageObject.getDialogId() == this.dialog_id) {
                        z4 = false;
                        messageObject2 = messageObject;
                    } else {
                        arrayList2 = new ArrayList();
                        arrayList2.add(messageObject);
                        z4 = true;
                        messageObject2 = null;
                    }
                    User user;
                    CharSequence userName;
                    String charSequence;
                    if (messageObject2 != null) {
                        this.forwardingMessages = null;
                        this.replyingMessageObject = messageObject2;
                        this.chatActivityEnterView.setReplyingMessageObject(messageObject2);
                        if (this.foundWebPage == null) {
                            if (messageObject2.isFromUser()) {
                                user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                                if (user != null) {
                                    userName = UserObject.getUserName(user);
                                } else {
                                    return;
                                }
                            }
                            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(messageObject2.messageOwner.to_id.channel_id));
                            if (chat != null) {
                                userName = chat.title;
                            } else {
                                return;
                            }
                            this.replyIconImageView.setImageResource(C0338R.drawable.reply);
                            this.replyNameTextView.setText(userName);
                            if (ThemeUtil.m2490b()) {
                                this.replyIconImageView.setColorFilter(c, Mode.SRC_IN);
                                this.replyNameTextView.setTextColor(c);
                            }
                            if (messageObject2.messageOwner.media instanceof TL_messageMediaGame) {
                                this.replyObjectTextView.setText(Emoji.replaceEmoji(messageObject2.messageOwner.media.game.title, this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                            } else if (messageObject2.messageText != null) {
                                charSequence = messageObject2.messageText.toString();
                                if (charSequence.length() > 150) {
                                    charSequence = charSequence.substring(attach_photo, 150);
                                }
                                this.replyObjectTextView.setText(Emoji.replaceEmoji(charSequence.replace('\n', ' '), this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                            }
                        } else {
                            return;
                        }
                    } else if (arrayList2 == null) {
                        this.replyIconImageView.setImageResource(C0338R.drawable.link);
                        if (ThemeUtil.m2490b()) {
                            this.replyIconImageView.setColorFilter(c, Mode.SRC_IN);
                            this.replyNameTextView.setTextColor(c);
                        }
                        if (webPage instanceof TL_webPagePending) {
                            this.replyNameTextView.setText(LocaleController.getString("GettingLinkInfo", C0338R.string.GettingLinkInfo));
                            this.replyObjectTextView.setText(this.pendingLinkSearchString);
                        } else {
                            if (webPage.site_name != null) {
                                this.replyNameTextView.setText(webPage.site_name);
                            } else if (webPage.title != null) {
                                this.replyNameTextView.setText(webPage.title);
                            } else {
                                this.replyNameTextView.setText(LocaleController.getString("LinkPreview", C0338R.string.LinkPreview));
                            }
                            if (webPage.description != null) {
                                this.replyObjectTextView.setText(webPage.description);
                            } else if (webPage.title != null && webPage.site_name != null) {
                                this.replyObjectTextView.setText(webPage.title);
                            } else if (webPage.author != null) {
                                this.replyObjectTextView.setText(webPage.author);
                            } else {
                                this.replyObjectTextView.setText(webPage.display_url);
                            }
                            this.chatActivityEnterView.setWebPage(webPage, true);
                        }
                    } else if (!arrayList2.isEmpty()) {
                        this.replyingMessageObject = null;
                        this.chatActivityEnterView.setReplyingMessageObject(null);
                        this.forwardingMessages = arrayList2;
                        if (this.foundWebPage == null) {
                            this.chatActivityEnterView.setForceShowSendButton(true, z3);
                            ArrayList arrayList3 = new ArrayList();
                            this.replyIconImageView.setImageResource(C0338R.drawable.forward_blue);
                            MessageObject messageObject3 = (MessageObject) arrayList2.get(attach_photo);
                            if (messageObject3.isFromUser()) {
                                arrayList3.add(Integer.valueOf(messageObject3.messageOwner.from_id));
                            } else {
                                arrayList3.add(Integer.valueOf(-messageObject3.messageOwner.to_id.channel_id));
                            }
                            if (ThemeUtil.m2490b()) {
                                this.replyIconImageView.setColorFilter(c, Mode.SRC_IN);
                            }
                            i3 = ((MessageObject) arrayList2.get(attach_photo)).type;
                            for (i2 = attach_gallery; i2 < arrayList2.size(); i2 += attach_gallery) {
                                messageObject3 = (MessageObject) arrayList2.get(i2);
                                Object valueOf = messageObject3.isFromUser() ? Integer.valueOf(messageObject3.messageOwner.from_id) : Integer.valueOf(-messageObject3.messageOwner.to_id.channel_id);
                                if (!arrayList3.contains(valueOf)) {
                                    arrayList3.add(valueOf);
                                }
                                if (((MessageObject) arrayList2.get(i2)).type != i3) {
                                    i3 = -1;
                                }
                            }
                            CharSequence stringBuilder = new StringBuilder();
                            for (i2 = attach_photo; i2 < arrayList3.size(); i2 += attach_gallery) {
                                Chat chat2;
                                Integer num = (Integer) arrayList3.get(i2);
                                if (num.intValue() > 0) {
                                    user = MessagesController.getInstance().getUser(num);
                                    chat2 = null;
                                } else {
                                    chat2 = MessagesController.getInstance().getChat(Integer.valueOf(-num.intValue()));
                                    user = null;
                                }
                                if (user != null || chat2 != null) {
                                    if (arrayList3.size() != attach_gallery) {
                                        if (arrayList3.size() != attach_video && stringBuilder.length() != 0) {
                                            stringBuilder.append(" ");
                                            stringBuilder.append(LocaleController.formatPluralString("AndOther", arrayList3.size() - 1));
                                            break;
                                        }
                                        if (stringBuilder.length() > 0) {
                                            stringBuilder.append(", ");
                                        }
                                        if (user == null) {
                                            stringBuilder.append(chat2.title);
                                        } else if (user.first_name != null && user.first_name.length() > 0) {
                                            stringBuilder.append(user.first_name);
                                        } else if (user.last_name == null || user.last_name.length() <= 0) {
                                            stringBuilder.append(" ");
                                        } else {
                                            stringBuilder.append(user.last_name);
                                        }
                                    } else if (user != null) {
                                        stringBuilder.append(UserObject.getUserName(user));
                                    } else {
                                        stringBuilder.append(chat2.title);
                                    }
                                }
                            }
                            this.replyNameTextView.setText(stringBuilder);
                            if (ThemeUtil.m2490b()) {
                                this.replyNameTextView.setTextColor(c);
                            }
                            if (i3 != -1 && i3 != 0 && i3 != copy && i3 != forward) {
                                if (i3 == attach_gallery) {
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedPhoto", arrayList2.size()));
                                    if (arrayList2.size() == attach_gallery) {
                                        messageObject3 = (MessageObject) arrayList2.get(attach_photo);
                                    }
                                } else if (i3 == attach_document) {
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedLocation", arrayList2.size()));
                                    messageObject3 = messageObject2;
                                } else if (i3 == attach_audio) {
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedVideo", arrayList2.size()));
                                    if (arrayList2.size() == attach_gallery) {
                                        messageObject3 = (MessageObject) arrayList2.get(attach_photo);
                                    }
                                } else if (i3 == delete) {
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedContact", arrayList2.size()));
                                    messageObject3 = messageObject2;
                                } else if (i3 == attach_video) {
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedAudio", arrayList2.size()));
                                    messageObject3 = messageObject2;
                                } else if (i3 == chat_menu_attach) {
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMusic", arrayList2.size()));
                                    messageObject3 = messageObject2;
                                } else if (i3 == chat_enc_timer) {
                                    this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedSticker", arrayList2.size()));
                                    messageObject3 = messageObject2;
                                } else if (i3 == 8 || i3 == 9) {
                                    if (arrayList2.size() != attach_gallery) {
                                        this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedFile", arrayList2.size()));
                                    } else if (i3 == 8) {
                                        this.replyObjectTextView.setText(LocaleController.getString("AttachGif", C0338R.string.AttachGif));
                                        messageObject3 = messageObject2;
                                    } else {
                                        userName = FileLoader.getDocumentFileName(((MessageObject) arrayList2.get(attach_photo)).getDocument());
                                        if (userName.length() != 0) {
                                            this.replyObjectTextView.setText(userName);
                                        }
                                        messageObject3 = (MessageObject) arrayList2.get(attach_photo);
                                    }
                                }
                                messageObject3 = messageObject2;
                            } else if (arrayList2.size() != attach_gallery || ((MessageObject) arrayList2.get(attach_photo)).messageText == null) {
                                this.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMessage", arrayList2.size()));
                                messageObject3 = messageObject2;
                            } else {
                                messageObject3 = (MessageObject) arrayList2.get(attach_photo);
                                if (messageObject3.messageOwner.media instanceof TL_messageMediaGame) {
                                    this.replyObjectTextView.setText(Emoji.replaceEmoji(messageObject3.messageOwner.media.game.title, this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                                } else {
                                    charSequence = messageObject3.messageText.toString();
                                    if (charSequence.length() > 150) {
                                        charSequence = charSequence.substring(attach_photo, 150);
                                    }
                                    this.replyObjectTextView.setText(Emoji.replaceEmoji(charSequence.replace('\n', ' '), this.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                                }
                                messageObject3 = messageObject2;
                            }
                            messageObject2 = messageObject3;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                    LayoutParams layoutParams = (LayoutParams) this.replyNameTextView.getLayoutParams();
                    LayoutParams layoutParams2 = (LayoutParams) this.replyObjectTextView.getLayoutParams();
                    PhotoSize photoSize = null;
                    if (messageObject2 != null) {
                        photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs2, 80);
                        if (photoSize == null) {
                            photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 80);
                        }
                    }
                    if (photoSize == null || (photoSize instanceof TL_photoSizeEmpty) || (photoSize.location instanceof TL_fileLocationUnavailable) || messageObject2.type == chat_enc_timer || (messageObject2 != null && messageObject2.isSecretMedia())) {
                        this.replyImageView.setImageBitmap(null);
                        this.replyImageLocation = null;
                        this.replyImageView.setVisibility(attach_document);
                        i3 = AndroidUtilities.dp(52.0f);
                        layoutParams2.leftMargin = i3;
                        layoutParams.leftMargin = i3;
                    } else {
                        this.replyImageLocation = photoSize.location;
                        this.replyImageView.setImage(this.replyImageLocation, "50_50", (Drawable) null);
                        this.replyImageView.setVisibility(attach_photo);
                        i3 = AndroidUtilities.dp(96.0f);
                        layoutParams2.leftMargin = i3;
                        layoutParams.leftMargin = i3;
                    }
                    this.replyNameTextView.setLayoutParams(layoutParams);
                    this.replyObjectTextView.setLayoutParams(layoutParams2);
                    this.chatActivityEnterView.showTopView(z3, z4);
                }
            } else if (this.replyingMessageObject != null || this.forwardingMessages != null || this.foundWebPage != null) {
                if (this.replyingMessageObject != null && (this.replyingMessageObject.messageOwner.reply_markup instanceof TL_replyKeyboardForceReply)) {
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", attach_photo).edit().putInt("answered_" + this.dialog_id, this.replyingMessageObject.getId()).commit();
                }
                if (this.foundWebPage != null) {
                    this.foundWebPage = null;
                    this.chatActivityEnterView.setWebPage(null, !z2);
                    if (!(webPage == null || (this.replyingMessageObject == null && this.forwardingMessages == null))) {
                        showReplyPanel(true, this.replyingMessageObject, this.forwardingMessages, null, false, true);
                        return;
                    }
                }
                if (this.forwardingMessages != null) {
                    forwardMessages(this.forwardingMessages, forwardNoName);
                }
                this.chatActivityEnterView.setForceShowSendButton(false, z3);
                this.chatActivityEnterView.hideTopView(z3);
                this.chatActivityEnterView.setReplyingMessageObject(null);
                this.replyingMessageObject = null;
                this.forwardingMessages = null;
                this.replyImageLocation = null;
            }
        }
    }

    public void updatePhotoAtIndex(int i) {
    }

    public void willHidePhotoViewer() {
        if (this.currentGifCell != null && !MediaController.m71a().m140B() && this.currentGifCell.getPhotoImage() != null) {
            this.currentGifCell.stopGif();
            this.currentGifCell = null;
        }
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}
