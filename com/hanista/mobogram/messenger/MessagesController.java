package com.hanista.mobogram.messenger;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.PointerIconCompat;
import android.util.Base64;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLiteDatabase;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.exoplayer.ExoPlayer.Factory;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.messenger.query.BotQuery;
import com.hanista.mobogram.messenger.query.DraftQuery;
import com.hanista.mobogram.messenger.query.MessagesQuery;
import com.hanista.mobogram.messenger.query.SearchQuery;
import com.hanista.mobogram.messenger.query.StickersQuery;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p000a.ArchiveUtil;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p007h.FavoriteUtil;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p016p.SpecificContactNotifController;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.telegraph.biz.UpdateBiz;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.NativeByteBuffer;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.SerializedData;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.BotInfo;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipants;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DraftMessage;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedMessage;
import com.hanista.mobogram.tgnet.TLRPC.ExportedChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.InputChannel;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.InputPeer;
import com.hanista.mobogram.tgnet.TLRPC.InputPhoto;
import com.hanista.mobogram.tgnet.TLRPC.InputUser;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.MessageEntity;
import com.hanista.mobogram.tgnet.TLRPC.Peer;
import com.hanista.mobogram.tgnet.TLRPC.PeerNotifySettings;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.SendMessageAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_registerDevice;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_unregisterDevice;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_updateStatus;
import com.hanista.mobogram.tgnet.TLRPC.TL_auth_logOut;
import com.hanista.mobogram.tgnet.TLRPC.TL_boolTrue;
import com.hanista.mobogram.tgnet.TLRPC.TL_botInfo;
import com.hanista.mobogram.tgnet.TLRPC.TL_channel;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelForbidden;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelMessagesFilterEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantSelf;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantsRecent;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelRoleEditor;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_channelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_channelParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_createChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_deleteChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_deleteMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_deleteUserHistory;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_editAbout;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_editAdmin;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_editPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_editTitle;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_getFullChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_getMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_getParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_getParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_inviteToChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_joinChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_kickFromChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_leaveChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_readHistory;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_toggleInvites;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_toggleSignatures;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_updatePinnedMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_updateUsername;
import com.hanista.mobogram.tgnet.TLRPC.TL_chat;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatInviteEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatPhotoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_config;
import com.hanista.mobogram.tgnet.TLRPC.TL_contactBlocked;
import com.hanista.mobogram.tgnet.TLRPC.TL_contactLinkContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_block;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_getBlocked;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_resolveUsername;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_unblock;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.TL_disabledFeature;
import com.hanista.mobogram.tgnet.TLRPC.TL_draftMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_help_appChangelog;
import com.hanista.mobogram.tgnet.TLRPC.TL_help_appChangelogEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_help_getAppChangelog;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputChannelEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputChatPhotoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputChatUploadedPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputEncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterChatPhotos;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPeerChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPeerChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPeerEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPeerUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPhotoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputUserEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputUserSelf;
import com.hanista.mobogram.tgnet.TLRPC.TL_message;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChannelCreate;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatAddUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionHistoryClear;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionUserJoined;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityMentionName;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaUnsupported;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageService;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_addChatUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_affectedHistory;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_affectedMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_channelMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_chatFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_createChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_deleteChatUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_deleteHistory;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_deleteMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_dialogs;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_editChatAdmin;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_editChatPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_editChatTitle;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getDialogs;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getFullChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getHistory;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getMessagesViews;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getPeerDialogs;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getPeerSettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_hideReportSpam;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_messages;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_migrateChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_peerDialogs;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_readEncryptedHistory;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_readHistory;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_readMessageContents;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_reportSpam;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_saveGif;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_saveRecentSticker;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_search;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_setEncryptedTyping;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_setTyping;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_startBot;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_toggleChatAdmins;
import com.hanista.mobogram.tgnet.TLRPC.TL_notifyPeer;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerNotifySettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerSettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_photos_deletePhotos;
import com.hanista.mobogram.tgnet.TLRPC.TL_photos_getUserPhotos;
import com.hanista.mobogram.tgnet.TLRPC.TL_photos_photo;
import com.hanista.mobogram.tgnet.TLRPC.TL_photos_photos;
import com.hanista.mobogram.tgnet.TLRPC.TL_photos_updateProfilePhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_privacyKeyChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.TL_privacyKeyStatusTimestamp;
import com.hanista.mobogram.tgnet.TLRPC.TL_replyKeyboardHide;
import com.hanista.mobogram.tgnet.TLRPC.TL_sendMessageCancelAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_sendMessageGamePlayAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_sendMessageRecordAudioAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_sendMessageRecordVideoAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_sendMessageTypingAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_sendMessageUploadAudioAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_sendMessageUploadDocumentAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_sendMessageUploadPhotoAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_sendMessageUploadVideoAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateChannelMessageViews;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateChannelPinnedMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateChannelTooLong;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateChatAdmins;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateChatParticipantAdd;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateChatParticipantAdmin;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateChatParticipantDelete;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateChatParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateChatUserTyping;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateContactLink;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateContactRegistered;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateDcOptions;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateDeleteChannelMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateDeleteMessages;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateDraftMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateEditChannelMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateEditMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateEncryptedChatTyping;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateEncryptedMessagesRead;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateEncryption;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateMessageID;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateNewAuthorization;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateNewChannelMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateNewEncryptedMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateNewGeoChatMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateNewMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateNewStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateNotifySettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_updatePrivacy;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateReadChannelInbox;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateReadChannelOutbox;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateReadFeaturedStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateReadHistoryInbox;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateReadHistoryOutbox;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateReadMessagesContents;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateRecentStickers;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateSavedGifs;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateServiceNotification;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateStickerSets;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateStickerSetsOrder;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserBlocked;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserName;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserPhone;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserStatus;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateUserTyping;
import com.hanista.mobogram.tgnet.TLRPC.TL_updateWebPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_updatesCombined;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates_channelDifference;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates_channelDifferenceEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates_difference;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates_differenceEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates_differenceSlice;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates_getChannelDifference;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates_getDifference;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates_getState;
import com.hanista.mobogram.tgnet.TLRPC.TL_updates_state;
import com.hanista.mobogram.tgnet.TLRPC.TL_userForeign_old2;
import com.hanista.mobogram.tgnet.TLRPC.TL_userFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_userProfilePhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusLastMonth;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusLastWeek;
import com.hanista.mobogram.tgnet.TLRPC.TL_userStatusRecently;
import com.hanista.mobogram.tgnet.TLRPC.TL_users_getFullUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPage;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPageEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPagePending;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPageUrlPending;
import com.hanista.mobogram.tgnet.TLRPC.Update;
import com.hanista.mobogram.tgnet.TLRPC.Updates;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.tgnet.TLRPC.UserProfilePhoto;
import com.hanista.mobogram.tgnet.TLRPC.Vector;
import com.hanista.mobogram.tgnet.TLRPC.WebPage;
import com.hanista.mobogram.tgnet.TLRPC.contacts_Blocked;
import com.hanista.mobogram.tgnet.TLRPC.messages_Dialogs;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import com.hanista.mobogram.tgnet.TLRPC.photos_Photos;
import com.hanista.mobogram.tgnet.TLRPC.updates_ChannelDifference;
import com.hanista.mobogram.tgnet.TLRPC.updates_Difference;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.AlertsCreator;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.ProfileActivity;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class MessagesController implements NotificationCenterDelegate {
    private static volatile MessagesController Instance = null;
    public static final int UPDATE_MASK_ALL = 1535;
    public static final int UPDATE_MASK_AVATAR = 2;
    public static final int UPDATE_MASK_CHANNEL = 8192;
    public static final int UPDATE_MASK_CHAT_ADMINS = 16384;
    public static final int UPDATE_MASK_CHAT_AVATAR = 8;
    public static final int UPDATE_MASK_CHAT_MEMBERS = 32;
    public static final int UPDATE_MASK_CHAT_NAME = 16;
    public static final int UPDATE_MASK_NAME = 1;
    public static final int UPDATE_MASK_NEW_MESSAGE = 2048;
    public static final int UPDATE_MASK_PHONE = 1024;
    public static final int UPDATE_MASK_READ_DIALOG_MESSAGE = 256;
    public static final int UPDATE_MASK_SELECT_DIALOG = 512;
    public static final int UPDATE_MASK_SEND_STATE = 4096;
    public static final int UPDATE_MASK_STATUS = 4;
    public static final int UPDATE_MASK_USER_PHONE = 128;
    public static final int UPDATE_MASK_USER_PRINT = 64;
    public boolean allowBigEmoji;
    public ArrayList<Integer> blockedUsers;
    private SparseArray<ArrayList<Integer>> channelViewsToReload;
    private SparseArray<ArrayList<Integer>> channelViewsToSend;
    private HashMap<Integer, Integer> channelsPts;
    private ConcurrentHashMap<Integer, Chat> chats;
    private HashMap<Integer, Boolean> checkingLastMessagesDialogs;
    private ArrayList<Long> createdDialogIds;
    private Runnable currentDeleteTaskRunnable;
    private ArrayList<Integer> currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    private final Comparator<TL_dialog> dialogComparator;
    public HashMap<Long, MessageObject> dialogMessage;
    public HashMap<Integer, MessageObject> dialogMessagesByIds;
    public HashMap<Long, MessageObject> dialogMessagesByRandomIds;
    public ArrayList<TL_dialog> dialogs;
    public ArrayList<TL_dialog> dialogsBotOnly;
    public ArrayList<TL_dialog> dialogsChannelOnly;
    public ArrayList<TL_dialog> dialogsContactOnly;
    public boolean dialogsEndReached;
    public ArrayList<TL_dialog> dialogsFavoriteOnly;
    public ArrayList<TL_dialog> dialogsGroupsOnly;
    public ArrayList<TL_dialog> dialogsHiddenOnly;
    public ArrayList<TL_dialog> dialogsJustGroupsOnly;
    public ArrayList<TL_dialog> dialogsServerOnly;
    public ArrayList<TL_dialog> dialogsSuperGroupsOnly;
    public ArrayList<TL_dialog> dialogsUnreadOnly;
    public ConcurrentHashMap<Long, TL_dialog> dialogs_dict;
    public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max;
    public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max;
    private ArrayList<TL_disabledFeature> disabledFeatures;
    public boolean enableJoined;
    private ConcurrentHashMap<Integer, EncryptedChat> encryptedChats;
    private HashMap<Integer, ExportedChatInvite> exportedChats;
    public boolean firstGettingTask;
    public int fontSize;
    private HashMap<Integer, String> fullUsersAbout;
    public boolean gettingDifference;
    private HashMap<Integer, Boolean> gettingDifferenceChannels;
    private boolean gettingNewDeleteTask;
    private HashMap<Integer, Boolean> gettingUnknownChannels;
    public int groupBigSize;
    private ArrayList<Integer> joiningToChannels;
    private int lastPrintingStringCount;
    private long lastStatusUpdateTime;
    private long lastViewsCheckTime;
    private ArrayList<Integer> loadedFullChats;
    private ArrayList<Integer> loadedFullParticipants;
    private ArrayList<Integer> loadedFullUsers;
    public boolean loadingBlockedUsers;
    public boolean loadingDialogs;
    private ArrayList<Integer> loadingFullChats;
    private ArrayList<Integer> loadingFullParticipants;
    private ArrayList<Integer> loadingFullUsers;
    private HashMap<Long, Boolean> loadingPeerSettings;
    public int maxBroadcastCount;
    public int maxEditTime;
    public int maxGroupCount;
    public int maxMegagroupCount;
    public int maxRecentGifsCount;
    public int maxRecentStickersCount;
    private boolean migratingDialogs;
    public int minGroupConvertSize;
    private SparseIntArray needShortPollChannels;
    public int nextDialogsCacheOffset;
    private boolean offlineSent;
    public ConcurrentHashMap<Integer, Integer> onlinePrivacy;
    private TL_dialog ownDialog;
    public HashMap<Long, CharSequence> printingStrings;
    public HashMap<Long, Integer> printingStringsTypes;
    public ConcurrentHashMap<Long, ArrayList<PrintingUser>> printingUsers;
    public int ratingDecay;
    public boolean registeringForPush;
    private HashMap<Long, ArrayList<Integer>> reloadingMessages;
    private HashMap<String, ArrayList<MessageObject>> reloadingWebpages;
    private HashMap<Long, ArrayList<MessageObject>> reloadingWebpagesPending;
    public int secretWebpagePreview;
    public HashMap<Integer, HashMap<Long, Boolean>> sendingTypings;
    private SparseIntArray shortPollChannels;
    private int statusRequest;
    private int statusSettingState;
    private final Comparator<Update> updatesComparator;
    private HashMap<Integer, ArrayList<Updates>> updatesQueueChannels;
    private ArrayList<Updates> updatesQueuePts;
    private ArrayList<Updates> updatesQueueQts;
    private ArrayList<Updates> updatesQueueSeq;
    private HashMap<Integer, Long> updatesStartWaitTimeChannels;
    private long updatesStartWaitTimePts;
    private long updatesStartWaitTimeQts;
    private long updatesStartWaitTimeSeq;
    public boolean updatingState;
    private String uploadingAvatar;
    public boolean useSystemEmoji;
    private ConcurrentHashMap<Integer, User> users;
    private ConcurrentHashMap<String, User> usersByUsernames;

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.100 */
    class AnonymousClass100 implements Runnable {
        final /* synthetic */ ArrayList val$objArr;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.100.1 */
        class C04881 implements Runnable {
            C04881() {
            }

            public void run() {
                NotificationsController.getInstance().processNewMessages(AnonymousClass100.this.val$objArr, true);
            }
        }

        AnonymousClass100(ArrayList arrayList) {
            this.val$objArr = arrayList;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C04881());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.103 */
    class AnonymousClass103 implements Runnable {
        final /* synthetic */ ArrayList val$chatsArr;
        final /* synthetic */ ArrayList val$usersArr;

        AnonymousClass103(ArrayList arrayList, ArrayList arrayList2) {
            this.val$usersArr = arrayList;
            this.val$chatsArr = arrayList2;
        }

        public void run() {
            MessagesController.this.putUsers(this.val$usersArr, false);
            MessagesController.this.putChats(this.val$chatsArr, false);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.104 */
    class AnonymousClass104 implements Runnable {
        final /* synthetic */ ArrayList val$chatsArr;
        final /* synthetic */ ArrayList val$usersArr;

        AnonymousClass104(ArrayList arrayList, ArrayList arrayList2) {
            this.val$usersArr = arrayList;
            this.val$chatsArr = arrayList2;
        }

        public void run() {
            MessagesController.this.putUsers(this.val$usersArr, false);
            MessagesController.this.putChats(this.val$chatsArr, false);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.106 */
    class AnonymousClass106 implements Runnable {
        final /* synthetic */ TL_updateUserBlocked val$finalUpdate;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.106.1 */
        class C04891 implements Runnable {
            C04891() {
            }

            public void run() {
                if (!AnonymousClass106.this.val$finalUpdate.blocked) {
                    MessagesController.this.blockedUsers.remove(Integer.valueOf(AnonymousClass106.this.val$finalUpdate.user_id));
                } else if (!MessagesController.this.blockedUsers.contains(Integer.valueOf(AnonymousClass106.this.val$finalUpdate.user_id))) {
                    MessagesController.this.blockedUsers.add(Integer.valueOf(AnonymousClass106.this.val$finalUpdate.user_id));
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            }
        }

        AnonymousClass106(TL_updateUserBlocked tL_updateUserBlocked) {
            this.val$finalUpdate = tL_updateUserBlocked;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C04891());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.107 */
    class AnonymousClass107 implements Runnable {
        final /* synthetic */ ArrayList val$pushMessages;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.107.1 */
        class C04901 implements Runnable {
            C04901() {
            }

            public void run() {
                NotificationsController.getInstance().processNewMessages(AnonymousClass107.this.val$pushMessages, true);
            }
        }

        AnonymousClass107(ArrayList arrayList) {
            this.val$pushMessages = arrayList;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C04901());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.108 */
    class AnonymousClass108 implements Runnable {
        final /* synthetic */ SparseArray val$channelViews;
        final /* synthetic */ ArrayList val$chatInfoToUpdate;
        final /* synthetic */ ArrayList val$contactsIds;
        final /* synthetic */ HashMap val$editingMessages;
        final /* synthetic */ int val$interfaceUpdateMaskFinal;
        final /* synthetic */ HashMap val$messages;
        final /* synthetic */ boolean val$printChangedArg;
        final /* synthetic */ ArrayList val$updatesOnMainThread;
        final /* synthetic */ HashMap val$webPages;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.108.1 */
        class C04911 implements Runnable {
            final /* synthetic */ User val$currentUser;

            C04911(User user) {
                this.val$currentUser = user;
            }

            public void run() {
                ContactsController.getInstance().addContactToPhoneBook(this.val$currentUser, true);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.108.2 */
        class C04922 implements Runnable {
            final /* synthetic */ Update val$update;

            C04922(Update update) {
                this.val$update = update;
            }

            public void run() {
                MessagesController.this.getChannelDifference(this.val$update.channel_id, MessagesController.UPDATE_MASK_NAME, 0);
            }
        }

        AnonymousClass108(int i, ArrayList arrayList, HashMap hashMap, HashMap hashMap2, HashMap hashMap3, boolean z, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray) {
            this.val$interfaceUpdateMaskFinal = i;
            this.val$updatesOnMainThread = arrayList;
            this.val$webPages = hashMap;
            this.val$messages = hashMap2;
            this.val$editingMessages = hashMap3;
            this.val$printChangedArg = z;
            this.val$contactsIds = arrayList2;
            this.val$chatInfoToUpdate = arrayList3;
            this.val$channelViews = sparseArray;
        }

        public void run() {
            int i;
            Object obj;
            int i2;
            long j;
            int i3;
            NotificationCenter instance;
            Object[] objArr;
            Object obj2;
            int i4 = this.val$interfaceUpdateMaskFinal;
            Object obj3 = null;
            if (!this.val$updatesOnMainThread.isEmpty()) {
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                Editor editor = null;
                i = 0;
                Object obj4 = null;
                while (i < this.val$updatesOnMainThread.size()) {
                    Update update = (Update) this.val$updatesOnMainThread.get(i);
                    User user = new User();
                    user.id = update.user_id;
                    User user2 = MessagesController.this.getUser(Integer.valueOf(update.user_id));
                    Object obj5 = (obj4 != null || new UpdateBiz().insertUpdate(update)) ? MessagesController.UPDATE_MASK_NAME : null;
                    SpecificContactNotifController.m2024a(update);
                    if (update instanceof TL_updatePrivacy) {
                        if (update.key instanceof TL_privacyKeyStatusTimestamp) {
                            ContactsController.getInstance().setPrivacyRules(update.rules, false);
                            obj = obj3;
                            i2 = i4;
                        } else {
                            if (update.key instanceof TL_privacyKeyChatInvite) {
                                ContactsController.getInstance().setPrivacyRules(update.rules, true);
                                obj = obj3;
                                i2 = i4;
                            }
                            obj = obj3;
                            i2 = i4;
                        }
                    } else if (update instanceof TL_updateUserStatus) {
                        if (update.status instanceof TL_userStatusRecently) {
                            update.status.expires = -100;
                        } else if (update.status instanceof TL_userStatusLastWeek) {
                            update.status.expires = -101;
                        } else if (update.status instanceof TL_userStatusLastMonth) {
                            update.status.expires = -102;
                        }
                        if (user2 != null) {
                            user2.id = update.user_id;
                            user2.status = update.status;
                        }
                        user.status = update.status;
                        arrayList2.add(user);
                        if (update.user_id == UserConfig.getClientUserId()) {
                            NotificationsController.getInstance().setLastOnlineFromOtherDevice(update.status.expires);
                            obj = obj3;
                            i2 = i4;
                        }
                        obj = obj3;
                        i2 = i4;
                    } else if (update instanceof TL_updateUserName) {
                        if (user2 != null) {
                            if (!UserObject.isContact(user2)) {
                                user2.first_name = update.first_name;
                                user2.last_name = update.last_name;
                            }
                            if (user2.username != null && user2.username.length() > 0) {
                                MessagesController.this.usersByUsernames.remove(user2.username);
                            }
                            if (update.username != null && update.username.length() > 0) {
                                MessagesController.this.usersByUsernames.put(update.username, user2);
                            }
                            user2.username = update.username;
                        }
                        user.first_name = update.first_name;
                        user.last_name = update.last_name;
                        user.username = update.username;
                        arrayList.add(user);
                        obj = obj3;
                        i2 = i4;
                    } else if (update instanceof TL_updateUserPhoto) {
                        if (user2 != null) {
                            user2.photo = update.photo;
                        }
                        user.photo = update.photo;
                        arrayList.add(user);
                        obj = obj3;
                        i2 = i4;
                    } else if (update instanceof TL_updateUserPhone) {
                        if (user2 != null) {
                            user2.phone = update.phone;
                            Utilities.phoneBookQueue.postRunnable(new C04911(user2));
                        }
                        user.phone = update.phone;
                        arrayList.add(user);
                        obj = obj3;
                        i2 = i4;
                    } else if (update instanceof TL_updateNotifySettings) {
                        TL_updateNotifySettings tL_updateNotifySettings = (TL_updateNotifySettings) update;
                        if ((update.notify_settings instanceof TL_peerNotifySettings) && (tL_updateNotifySettings.peer instanceof TL_notifyPeer)) {
                            if (editor == null) {
                                editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                            }
                            j = tL_updateNotifySettings.peer.peer.user_id != 0 ? (long) tL_updateNotifySettings.peer.peer.user_id : tL_updateNotifySettings.peer.peer.chat_id != 0 ? (long) (-tL_updateNotifySettings.peer.peer.chat_id) : (long) (-tL_updateNotifySettings.peer.peer.channel_id);
                            r2 = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(j));
                            if (r2 != null) {
                                r2.notify_settings = update.notify_settings;
                            }
                            editor.putBoolean("silent_" + j, update.notify_settings.silent);
                            if (update.notify_settings.mute_until > ConnectionsManager.getInstance().getCurrentTime()) {
                                i3 = 0;
                                if (update.notify_settings.mute_until > ConnectionsManager.getInstance().getCurrentTime() + 31536000) {
                                    editor.putInt("notify2_" + j, MessagesController.UPDATE_MASK_AVATAR);
                                    if (r2 != null) {
                                        r2.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                        r2 = 0;
                                    }
                                    r2 = i3;
                                } else {
                                    i3 = update.notify_settings.mute_until;
                                    editor.putInt("notify2_" + j, 3);
                                    editor.putInt("notifyuntil_" + j, update.notify_settings.mute_until);
                                    if (r2 != null) {
                                        r2.notify_settings.mute_until = i3;
                                    }
                                    r2 = i3;
                                }
                                MessagesStorage.getInstance().setDialogFlags(j, (((long) r2) << MessagesController.UPDATE_MASK_CHAT_MEMBERS) | 1);
                                NotificationsController.getInstance().removeNotificationsForDialog(j);
                            } else {
                                if (r2 != null) {
                                    r2.notify_settings.mute_until = 0;
                                }
                                editor.remove("notify2_" + j);
                                MessagesStorage.getInstance().setDialogFlags(j, 0);
                            }
                        }
                        obj = obj3;
                        i2 = i4;
                    } else if (update instanceof TL_updateChannel) {
                        r2 = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(-((long) update.channel_id)));
                        Chat chat = MessagesController.this.getChat(Integer.valueOf(update.channel_id));
                        if (chat != null) {
                            if (r2 == null && (chat instanceof TL_channel) && !chat.left) {
                                Utilities.stageQueue.postRunnable(new C04922(update));
                            } else if (chat.left && r2 != null) {
                                MessagesController.this.deleteDialog(r2.id, 0);
                            }
                        }
                        r2 = i4 | MessagesController.UPDATE_MASK_CHANNEL;
                        MessagesController.this.loadFullChat(update.channel_id, 0, true);
                        i2 = r2;
                        obj = obj3;
                    } else if (update instanceof TL_updateChatAdmins) {
                        i2 = i4 | MessagesController.UPDATE_MASK_CHAT_ADMINS;
                        obj = obj3;
                    } else if (update instanceof TL_updateStickerSets) {
                        StickersQuery.loadStickers(update.masks ? MessagesController.UPDATE_MASK_NAME : 0, false, true);
                        obj = obj3;
                        i2 = i4;
                    } else if (update instanceof TL_updateStickerSetsOrder) {
                        StickersQuery.reorderStickers(update.masks ? MessagesController.UPDATE_MASK_NAME : 0, update.order);
                        obj = obj3;
                        i2 = i4;
                    } else if (update instanceof TL_updateNewStickerSet) {
                        StickersQuery.addNewStickerSet(update.stickerset);
                        obj = obj3;
                        i2 = i4;
                    } else if (update instanceof TL_updateSavedGifs) {
                        ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putLong("lastGifLoadTime", 0).commit();
                        obj = obj3;
                        i2 = i4;
                    } else if (update instanceof TL_updateRecentStickers) {
                        ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putLong("lastStickersLoadTime", 0).commit();
                        obj = obj3;
                        i2 = i4;
                    } else if (update instanceof TL_updateDraftMessage) {
                        Peer peer = ((TL_updateDraftMessage) update).peer;
                        j = peer.user_id != 0 ? (long) peer.user_id : peer.channel_id != 0 ? (long) (-peer.channel_id) : (long) (-peer.chat_id);
                        DraftQuery.saveDraft(j, update.draft, null, true);
                        obj = MessagesController.UPDATE_MASK_NAME;
                        i2 = i4;
                    } else {
                        if (update instanceof TL_updateReadFeaturedStickers) {
                            StickersQuery.markFaturedStickersAsRead(false);
                        }
                        obj = obj3;
                        i2 = i4;
                    }
                    i += MessagesController.UPDATE_MASK_NAME;
                    obj3 = obj;
                    i4 = i2;
                    obj4 = obj5;
                }
                if (obj4 != null) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                }
                if (editor != null) {
                    editor.commit();
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                }
                MessagesStorage.getInstance().updateUsers(arrayList2, true, true, true);
                MessagesStorage.getInstance().updateUsers(arrayList, false, true, true);
            }
            i = i4;
            Object obj6 = obj3;
            if (!this.val$webPages.isEmpty()) {
                instance = NotificationCenter.getInstance();
                i2 = NotificationCenter.didReceivedWebpagesInUpdates;
                objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = this.val$webPages;
                instance.postNotificationName(i2, objArr);
                for (Entry entry : this.val$webPages.entrySet()) {
                    ArrayList arrayList3 = (ArrayList) MessagesController.this.reloadingWebpagesPending.remove(entry.getKey());
                    if (arrayList3 != null) {
                        long j2;
                        WebPage webPage = (WebPage) entry.getValue();
                        ArrayList arrayList4 = new ArrayList();
                        j = 0;
                        if ((webPage instanceof TL_webPage) || (webPage instanceof TL_webPageEmpty)) {
                            for (i3 = 0; i3 < arrayList3.size(); i3 += MessagesController.UPDATE_MASK_NAME) {
                                ((MessageObject) arrayList3.get(i3)).messageOwner.media.webpage = webPage;
                                if (i3 == 0) {
                                    j = ((MessageObject) arrayList3.get(i3)).getDialogId();
                                    ImageLoader.saveMessageThumbs(((MessageObject) arrayList3.get(i3)).messageOwner);
                                }
                                arrayList4.add(((MessageObject) arrayList3.get(i3)).messageOwner);
                            }
                            j2 = j;
                        } else {
                            MessagesController.this.reloadingWebpagesPending.put(Long.valueOf(webPage.id), arrayList3);
                            j2 = 0;
                        }
                        if (!arrayList4.isEmpty()) {
                            MessagesStorage.getInstance().putMessages(arrayList4, true, true, false, MediaController.m71a().m167c());
                            instance = NotificationCenter.getInstance();
                            i2 = NotificationCenter.replaceMessagesObjects;
                            objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                            objArr[0] = Long.valueOf(j2);
                            objArr[MessagesController.UPDATE_MASK_NAME] = arrayList3;
                            instance.postNotificationName(i2, objArr);
                        }
                    }
                }
            }
            obj = null;
            if (!this.val$messages.isEmpty()) {
                for (Entry entry2 : this.val$messages.entrySet()) {
                    MessagesController.this.updateInterfaceWithMessages(((Long) entry2.getKey()).longValue(), (ArrayList) entry2.getValue());
                }
                obj = MessagesController.UPDATE_MASK_NAME;
            } else if (obj6 != null) {
                MessagesController.this.sortDialogs(null);
                obj = MessagesController.UPDATE_MASK_NAME;
            }
            if (this.val$editingMessages.isEmpty()) {
                obj2 = obj;
            } else {
                obj2 = obj;
                for (Entry entry22 : this.val$editingMessages.entrySet()) {
                    Object obj7;
                    NotificationCenter instance2;
                    int i5;
                    Object[] objArr2;
                    Long l = (Long) entry22.getKey();
                    ArrayList arrayList5 = (ArrayList) entry22.getValue();
                    MessageObject messageObject = (MessageObject) MessagesController.this.dialogMessage.get(l);
                    if (messageObject != null) {
                        int i6 = 0;
                        while (i6 < arrayList5.size()) {
                            MessageObject messageObject2 = (MessageObject) arrayList5.get(i6);
                            if (messageObject.getId() == messageObject2.getId()) {
                                MessagesController.this.dialogMessage.put(l, messageObject2);
                                if (messageObject2.messageOwner.to_id != null && messageObject2.messageOwner.to_id.channel_id == 0) {
                                    MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject2.getId()), messageObject2);
                                }
                                obj7 = MessagesController.UPDATE_MASK_NAME;
                                MessagesQuery.loadReplyMessagesForMessages(arrayList5, l.longValue());
                                instance2 = NotificationCenter.getInstance();
                                i5 = NotificationCenter.replaceMessagesObjects;
                                objArr2 = new Object[MessagesController.UPDATE_MASK_AVATAR];
                                objArr2[0] = l;
                                objArr2[MessagesController.UPDATE_MASK_NAME] = arrayList5;
                                instance2.postNotificationName(i5, objArr2);
                                obj2 = obj7;
                            } else {
                                i6 += MessagesController.UPDATE_MASK_NAME;
                            }
                        }
                    }
                    obj7 = obj2;
                    MessagesQuery.loadReplyMessagesForMessages(arrayList5, l.longValue());
                    instance2 = NotificationCenter.getInstance();
                    i5 = NotificationCenter.replaceMessagesObjects;
                    objArr2 = new Object[MessagesController.UPDATE_MASK_AVATAR];
                    objArr2[0] = l;
                    objArr2[MessagesController.UPDATE_MASK_NAME] = arrayList5;
                    instance2.postNotificationName(i5, objArr2);
                    obj2 = obj7;
                }
            }
            if (obj2 != null) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            if (this.val$printChangedArg) {
                i |= MessagesController.UPDATE_MASK_USER_PRINT;
            }
            if (!this.val$contactsIds.isEmpty()) {
                i = (i | MessagesController.UPDATE_MASK_NAME) | MessagesController.UPDATE_MASK_USER_PHONE;
            }
            if (!this.val$chatInfoToUpdate.isEmpty()) {
                for (i2 = 0; i2 < this.val$chatInfoToUpdate.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                    MessagesStorage.getInstance().updateChatParticipants((ChatParticipants) this.val$chatInfoToUpdate.get(i2));
                }
            }
            if (this.val$channelViews.size() != 0) {
                instance = NotificationCenter.getInstance();
                i2 = NotificationCenter.didUpdatedMessagesViews;
                objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = this.val$channelViews;
                instance.postNotificationName(i2, objArr);
            }
            if (i != 0) {
                instance = NotificationCenter.getInstance();
                i2 = NotificationCenter.updateInterfaces;
                objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(i);
                instance.postNotificationName(i2, objArr);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.109 */
    class AnonymousClass109 implements Runnable {
        final /* synthetic */ SparseArray val$deletedMessages;
        final /* synthetic */ HashMap val$markAsReadEncrypted;
        final /* synthetic */ ArrayList val$markAsReadMessages;
        final /* synthetic */ SparseArray val$markAsReadMessagesInbox;
        final /* synthetic */ SparseArray val$markAsReadMessagesOutbox;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.109.1 */
        class C04931 implements Runnable {
            C04931() {
            }

            public void run() {
                int i;
                int i2;
                int keyAt;
                int longValue;
                MessageObject messageObject;
                if (AnonymousClass109.this.val$markAsReadMessagesInbox.size() == 0 && AnonymousClass109.this.val$markAsReadMessagesOutbox.size() == 0) {
                    i = 0;
                } else {
                    TL_dialog tL_dialog;
                    MessageObject messageObject2;
                    NotificationCenter instance = NotificationCenter.getInstance();
                    i2 = NotificationCenter.messagesRead;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                    objArr[0] = AnonymousClass109.this.val$markAsReadMessagesInbox;
                    objArr[MessagesController.UPDATE_MASK_NAME] = AnonymousClass109.this.val$markAsReadMessagesOutbox;
                    instance.postNotificationName(i2, objArr);
                    NotificationsController.getInstance().processReadMessages(AnonymousClass109.this.val$markAsReadMessagesInbox, 0, 0, 0, false);
                    i = 0;
                    for (i2 = 0; i2 < AnonymousClass109.this.val$markAsReadMessagesInbox.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                        keyAt = AnonymousClass109.this.val$markAsReadMessagesInbox.keyAt(i2);
                        longValue = (int) ((Long) AnonymousClass109.this.val$markAsReadMessagesInbox.get(keyAt)).longValue();
                        tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf((long) keyAt));
                        if (tL_dialog != null && tL_dialog.top_message <= longValue) {
                            messageObject2 = (MessageObject) MessagesController.this.dialogMessage.get(Long.valueOf(tL_dialog.id));
                            if (!(messageObject2 == null || messageObject2.isOut())) {
                                messageObject2.setIsRead();
                                i |= MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
                            }
                        }
                    }
                    for (i2 = 0; i2 < AnonymousClass109.this.val$markAsReadMessagesOutbox.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                        keyAt = AnonymousClass109.this.val$markAsReadMessagesOutbox.keyAt(i2);
                        longValue = (int) ((Long) AnonymousClass109.this.val$markAsReadMessagesOutbox.get(keyAt)).longValue();
                        tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf((long) keyAt));
                        if (tL_dialog != null && tL_dialog.top_message <= longValue) {
                            messageObject2 = (MessageObject) MessagesController.this.dialogMessage.get(Long.valueOf(tL_dialog.id));
                            if (messageObject2 != null && messageObject2.isOut()) {
                                messageObject2.setIsRead();
                                i |= MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
                            }
                        }
                    }
                }
                if (!AnonymousClass109.this.val$markAsReadEncrypted.isEmpty()) {
                    for (Entry entry : AnonymousClass109.this.val$markAsReadEncrypted.entrySet()) {
                        NotificationCenter instance2 = NotificationCenter.getInstance();
                        longValue = NotificationCenter.messagesReadEncrypted;
                        Object[] objArr2 = new Object[MessagesController.UPDATE_MASK_AVATAR];
                        objArr2[0] = entry.getKey();
                        objArr2[MessagesController.UPDATE_MASK_NAME] = entry.getValue();
                        instance2.postNotificationName(longValue, objArr2);
                        long intValue = ((long) ((Integer) entry.getKey()).intValue()) << MessagesController.UPDATE_MASK_CHAT_MEMBERS;
                        if (((TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(intValue))) != null) {
                            messageObject = (MessageObject) MessagesController.this.dialogMessage.get(Long.valueOf(intValue));
                            if (messageObject != null && messageObject.messageOwner.date <= ((Integer) entry.getValue()).intValue()) {
                                messageObject.setIsRead();
                                i |= MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
                            }
                        }
                    }
                }
                keyAt = i;
                if (!AnonymousClass109.this.val$markAsReadMessages.isEmpty()) {
                    instance = NotificationCenter.getInstance();
                    i2 = NotificationCenter.messagesReadContent;
                    objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = AnonymousClass109.this.val$markAsReadMessages;
                    instance.postNotificationName(i2, objArr);
                }
                if (AnonymousClass109.this.val$deletedMessages.size() != 0) {
                    for (longValue = 0; longValue < AnonymousClass109.this.val$deletedMessages.size(); longValue += MessagesController.UPDATE_MASK_NAME) {
                        i2 = AnonymousClass109.this.val$deletedMessages.keyAt(longValue);
                        ArrayList arrayList = (ArrayList) AnonymousClass109.this.val$deletedMessages.get(i2);
                        if (arrayList != null) {
                            NotificationCenter instance3 = NotificationCenter.getInstance();
                            int i3 = NotificationCenter.messagesDeleted;
                            Object[] objArr3 = new Object[MessagesController.UPDATE_MASK_AVATAR];
                            objArr3[0] = arrayList;
                            objArr3[MessagesController.UPDATE_MASK_NAME] = Integer.valueOf(i2);
                            instance3.postNotificationName(i3, objArr3);
                            if (i2 == 0) {
                                for (i = 0; i < arrayList.size(); i += MessagesController.UPDATE_MASK_NAME) {
                                    messageObject = (MessageObject) MessagesController.this.dialogMessagesByIds.get((Integer) arrayList.get(i));
                                    if (messageObject != null) {
                                        messageObject.deleted = true;
                                    }
                                }
                            } else {
                                messageObject = (MessageObject) MessagesController.this.dialogMessage.get(Long.valueOf((long) (-i2)));
                                if (messageObject != null) {
                                    for (i3 = 0; i3 < arrayList.size(); i3 += MessagesController.UPDATE_MASK_NAME) {
                                        if (messageObject.getId() == ((Integer) arrayList.get(i3)).intValue()) {
                                            messageObject.deleted = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    NotificationsController.getInstance().removeDeletedMessagesFromNotifications(AnonymousClass109.this.val$deletedMessages);
                }
                if (keyAt != 0) {
                    instance = NotificationCenter.getInstance();
                    i2 = NotificationCenter.updateInterfaces;
                    objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(keyAt);
                    instance.postNotificationName(i2, objArr);
                }
            }
        }

        AnonymousClass109(SparseArray sparseArray, SparseArray sparseArray2, HashMap hashMap, ArrayList arrayList, SparseArray sparseArray3) {
            this.val$markAsReadMessagesInbox = sparseArray;
            this.val$markAsReadMessagesOutbox = sparseArray2;
            this.val$markAsReadEncrypted = hashMap;
            this.val$markAsReadMessages = arrayList;
            this.val$deletedMessages = sparseArray3;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C04931());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.10 */
    class AnonymousClass10 implements RequestDelegate {
        final /* synthetic */ Chat val$chat;
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ int val$classGuid;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.10.1 */
        class C04861 implements Runnable {
            final /* synthetic */ TL_messages_chatFull val$res;

            C04861(TL_messages_chatFull tL_messages_chatFull) {
                this.val$res = tL_messages_chatFull;
            }

            public void run() {
                int i;
                MessagesController.this.applyDialogNotificationsSettings((long) (-AnonymousClass10.this.val$chat_id), this.val$res.full_chat.notify_settings);
                for (i = 0; i < this.val$res.full_chat.bot_info.size(); i += MessagesController.UPDATE_MASK_NAME) {
                    BotQuery.putBotInfo((BotInfo) this.val$res.full_chat.bot_info.get(i));
                }
                MessagesController.this.exportedChats.put(Integer.valueOf(AnonymousClass10.this.val$chat_id), this.val$res.full_chat.exported_invite);
                MessagesController.this.loadingFullChats.remove(Integer.valueOf(AnonymousClass10.this.val$chat_id));
                MessagesController.this.loadedFullChats.add(Integer.valueOf(AnonymousClass10.this.val$chat_id));
                if (!this.val$res.chats.isEmpty()) {
                    ((Chat) this.val$res.chats.get(0)).address = this.val$res.full_chat.about;
                }
                MessagesController.this.putUsers(this.val$res.users, false);
                MessagesController.this.putChats(this.val$res.chats, false);
                NotificationCenter instance = NotificationCenter.getInstance();
                i = NotificationCenter.chatInfoDidLoaded;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_STATUS];
                objArr[0] = this.val$res.full_chat;
                objArr[MessagesController.UPDATE_MASK_NAME] = Integer.valueOf(AnonymousClass10.this.val$classGuid);
                objArr[MessagesController.UPDATE_MASK_AVATAR] = Boolean.valueOf(false);
                objArr[3] = null;
                instance.postNotificationName(i, objArr);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.10.2 */
        class C04872 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C04872(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                MessagesController.this.checkChannelError(this.val$error.text, AnonymousClass10.this.val$chat_id);
                MessagesController.this.loadingFullChats.remove(Integer.valueOf(AnonymousClass10.this.val$chat_id));
            }
        }

        AnonymousClass10(Chat chat, int i, int i2) {
            this.val$chat = chat;
            this.val$chat_id = i;
            this.val$classGuid = i2;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                TL_messages_chatFull tL_messages_chatFull = (TL_messages_chatFull) tLObject;
                MessagesStorage.getInstance().putUsersAndChats(tL_messages_chatFull.users, tL_messages_chatFull.chats, true, true);
                MessagesStorage.getInstance().updateChatInfo(tL_messages_chatFull.full_chat, false);
                if (ChatObject.isChannel(this.val$chat)) {
                    ArrayList arrayList;
                    long j = (long) (-this.val$chat_id);
                    Integer num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j));
                    if (num == null) {
                        num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, j));
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_inbox_max_id, num.intValue())));
                    if (num.intValue() == 0) {
                        arrayList = new ArrayList();
                        TL_updateReadChannelInbox tL_updateReadChannelInbox = new TL_updateReadChannelInbox();
                        tL_updateReadChannelInbox.channel_id = this.val$chat_id;
                        tL_updateReadChannelInbox.max_id = tL_messages_chatFull.full_chat.read_inbox_max_id;
                        arrayList.add(tL_updateReadChannelInbox);
                        MessagesController.this.processUpdateArray(arrayList, null, null, false);
                    }
                    num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j));
                    if (num == null) {
                        num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, j));
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(tL_messages_chatFull.full_chat.read_outbox_max_id, num.intValue())));
                    if (num.intValue() == 0) {
                        arrayList = new ArrayList();
                        TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TL_updateReadChannelOutbox();
                        tL_updateReadChannelOutbox.channel_id = this.val$chat_id;
                        tL_updateReadChannelOutbox.max_id = tL_messages_chatFull.full_chat.read_outbox_max_id;
                        arrayList.add(tL_updateReadChannelOutbox);
                        MessagesController.this.processUpdateArray(arrayList, null, null, false);
                    }
                }
                AndroidUtilities.runOnUIThread(new C04861(tL_messages_chatFull));
                return;
            }
            AndroidUtilities.runOnUIThread(new C04872(tL_error));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.110 */
    static class AnonymousClass110 implements RequestDelegate {
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ ProgressDialog val$progressDialog;
        final /* synthetic */ int val$type;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.110.1 */
        class C04961 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C04961(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                try {
                    AnonymousClass110.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                AnonymousClass110.this.val$fragment.setVisibleDialog(null);
                if (this.val$error == null) {
                    TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) this.val$response;
                    MessagesController.getInstance().putUsers(tL_contacts_resolvedPeer.users, false);
                    MessagesController.getInstance().putChats(tL_contacts_resolvedPeer.chats, false);
                    MessagesStorage.getInstance().putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, false, true);
                    if (!tL_contacts_resolvedPeer.chats.isEmpty()) {
                        MessagesController.openChatOrProfileWith(null, (Chat) tL_contacts_resolvedPeer.chats.get(0), AnonymousClass110.this.val$fragment, MessagesController.UPDATE_MASK_NAME, false);
                    } else if (!tL_contacts_resolvedPeer.users.isEmpty()) {
                        MessagesController.openChatOrProfileWith((User) tL_contacts_resolvedPeer.users.get(0), null, AnonymousClass110.this.val$fragment, AnonymousClass110.this.val$type, false);
                    }
                } else if (AnonymousClass110.this.val$fragment != null && AnonymousClass110.this.val$fragment.getParentActivity() != null) {
                    try {
                        Toast.makeText(AnonymousClass110.this.val$fragment.getParentActivity(), LocaleController.getString("NoUsernameFound", C0338R.string.NoUsernameFound), 0).show();
                    } catch (Throwable e2) {
                        FileLog.m18e("tmessages", e2);
                    }
                }
            }
        }

        AnonymousClass110(ProgressDialog progressDialog, BaseFragment baseFragment, int i) {
            this.val$progressDialog = progressDialog;
            this.val$fragment = baseFragment;
            this.val$type = i;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C04961(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.111 */
    static class AnonymousClass111 implements OnClickListener {
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ int val$reqId;

        AnonymousClass111(int i, BaseFragment baseFragment) {
            this.val$reqId = i;
            this.val$fragment = baseFragment;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
            try {
                dialogInterface.dismiss();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (this.val$fragment != null) {
                this.val$fragment.setVisibleDialog(null);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.11 */
    class AnonymousClass11 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ User val$user;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.11.1 */
        class C04941 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C04941(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                TL_userFull tL_userFull = (TL_userFull) this.val$response;
                MessagesController.this.applyDialogNotificationsSettings((long) AnonymousClass11.this.val$user.id, tL_userFull.notify_settings);
                if (tL_userFull.bot_info instanceof TL_botInfo) {
                    BotQuery.putBotInfo(tL_userFull.bot_info);
                }
                if (tL_userFull.about == null || tL_userFull.about.length() <= 0) {
                    MessagesController.this.fullUsersAbout.remove(Integer.valueOf(AnonymousClass11.this.val$user.id));
                } else {
                    MessagesController.this.fullUsersAbout.put(Integer.valueOf(AnonymousClass11.this.val$user.id), tL_userFull.about);
                }
                MessagesController.this.loadingFullUsers.remove(Integer.valueOf(AnonymousClass11.this.val$user.id));
                MessagesController.this.loadedFullUsers.add(Integer.valueOf(AnonymousClass11.this.val$user.id));
                String str = AnonymousClass11.this.val$user.first_name + AnonymousClass11.this.val$user.last_name + AnonymousClass11.this.val$user.username;
                ArrayList arrayList = new ArrayList();
                arrayList.add(tL_userFull.user);
                MessagesController.this.putUsers(arrayList, false);
                MessagesStorage.getInstance().putUsersAndChats(arrayList, null, false, true);
                if (!(str == null || str.equals(tL_userFull.user.first_name + tL_userFull.user.last_name + tL_userFull.user.username))) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.updateInterfaces;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_NAME);
                    instance.postNotificationName(i, objArr);
                }
                if (tL_userFull.bot_info instanceof TL_botInfo) {
                    instance = NotificationCenter.getInstance();
                    i = NotificationCenter.botInfoDidLoaded;
                    objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                    objArr[0] = tL_userFull.bot_info;
                    objArr[MessagesController.UPDATE_MASK_NAME] = Integer.valueOf(AnonymousClass11.this.val$classGuid);
                    instance.postNotificationName(i, objArr);
                }
                NotificationCenter instance2 = NotificationCenter.getInstance();
                int i2 = NotificationCenter.userInfoDidLoaded;
                Object[] objArr2 = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr2[0] = Integer.valueOf(AnonymousClass11.this.val$user.id);
                instance2.postNotificationName(i2, objArr2);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.11.2 */
        class C04952 implements Runnable {
            C04952() {
            }

            public void run() {
                MessagesController.this.loadingFullUsers.remove(Integer.valueOf(AnonymousClass11.this.val$user.id));
            }
        }

        AnonymousClass11(User user, int i) {
            this.val$user = user;
            this.val$classGuid = i;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                AndroidUtilities.runOnUIThread(new C04941(tLObject));
            } else {
                AndroidUtilities.runOnUIThread(new C04952());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.12 */
    class AnonymousClass12 implements RequestDelegate {
        final /* synthetic */ Chat val$chat;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ ArrayList val$result;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.12.1 */
        class C04971 implements Runnable {
            final /* synthetic */ ArrayList val$objects;

            C04971(ArrayList arrayList) {
                this.val$objects = arrayList;
            }

            public void run() {
                ArrayList arrayList = (ArrayList) MessagesController.this.reloadingMessages.get(Long.valueOf(AnonymousClass12.this.val$dialog_id));
                if (arrayList != null) {
                    arrayList.removeAll(AnonymousClass12.this.val$result);
                    if (arrayList.isEmpty()) {
                        MessagesController.this.reloadingMessages.remove(Long.valueOf(AnonymousClass12.this.val$dialog_id));
                    }
                }
                MessageObject messageObject = (MessageObject) MessagesController.this.dialogMessage.get(Long.valueOf(AnonymousClass12.this.val$dialog_id));
                if (messageObject != null) {
                    int i = 0;
                    while (i < this.val$objects.size()) {
                        MessageObject messageObject2 = (MessageObject) this.val$objects.get(i);
                        if (messageObject == null || messageObject.getId() != messageObject2.getId()) {
                            i += MessagesController.UPDATE_MASK_NAME;
                        } else {
                            MessagesController.this.dialogMessage.put(Long.valueOf(AnonymousClass12.this.val$dialog_id), messageObject2);
                            if (messageObject2.messageOwner.to_id.channel_id == 0) {
                                messageObject = (MessageObject) MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(messageObject2.getId()));
                                if (messageObject != null) {
                                    MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject.getId()), messageObject);
                                }
                            }
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        }
                    }
                }
                NotificationCenter instance = NotificationCenter.getInstance();
                int i2 = NotificationCenter.replaceMessagesObjects;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                objArr[0] = Long.valueOf(AnonymousClass12.this.val$dialog_id);
                objArr[MessagesController.UPDATE_MASK_NAME] = this.val$objects;
                instance.postNotificationName(i2, objArr);
            }
        }

        AnonymousClass12(long j, Chat chat, ArrayList arrayList) {
            this.val$dialog_id = j;
            this.val$chat = chat;
            this.val$result = arrayList;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                int i;
                messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                AbstractMap hashMap = new HashMap();
                for (i = 0; i < com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users.size(); i += MessagesController.UPDATE_MASK_NAME) {
                    User user = (User) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users.get(i);
                    hashMap.put(Integer.valueOf(user.id), user);
                }
                AbstractMap hashMap2 = new HashMap();
                for (i = 0; i < com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats.size(); i += MessagesController.UPDATE_MASK_NAME) {
                    Chat chat = (Chat) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats.get(i);
                    hashMap2.put(Integer.valueOf(chat.id), chat);
                }
                Integer num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(this.val$dialog_id));
                if (num == null) {
                    num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$dialog_id));
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(this.val$dialog_id), num);
                }
                Integer num2 = num;
                num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(this.val$dialog_id));
                if (num == null) {
                    num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$dialog_id));
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(this.val$dialog_id), num);
                }
                Integer num3 = num;
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                    Message message = (Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(i2);
                    if (this.val$chat != null && this.val$chat.megagroup) {
                        message.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                    }
                    message.dialog_id = this.val$dialog_id;
                    message.unread = (message.out ? num3 : num2).intValue() < message.id;
                    arrayList.add(new MessageObject(message, hashMap, hashMap2, true));
                }
                ImageLoader.saveMessagesThumbs(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages);
                MessagesStorage.getInstance().putMessages(com_hanista_mobogram_tgnet_TLRPC_messages_Messages, this.val$dialog_id, -1, 0, false);
                AndroidUtilities.runOnUIThread(new C04971(arrayList));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.15 */
    class AnonymousClass15 implements RequestDelegate {
        final /* synthetic */ long val$dialogId;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.15.1 */
        class C04981 implements Runnable {
            C04981() {
            }

            public void run() {
                MessagesController.this.loadingPeerSettings.remove(Long.valueOf(AnonymousClass15.this.val$dialogId));
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                edit.remove("spam_" + AnonymousClass15.this.val$dialogId);
                edit.putInt("spam3_" + AnonymousClass15.this.val$dialogId, MessagesController.UPDATE_MASK_NAME);
                edit.commit();
            }
        }

        AnonymousClass15(long j) {
            this.val$dialogId = j;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C04981());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.16 */
    class AnonymousClass16 implements RequestDelegate {
        final /* synthetic */ long val$dialogId;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.16.1 */
        class C04991 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C04991(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                MessagesController.this.loadingPeerSettings.remove(Long.valueOf(AnonymousClass16.this.val$dialogId));
                if (this.val$response != null) {
                    TL_peerSettings tL_peerSettings = (TL_peerSettings) this.val$response;
                    Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                    if (tL_peerSettings.report_spam) {
                        edit.putInt("spam3_" + AnonymousClass16.this.val$dialogId, MessagesController.UPDATE_MASK_AVATAR);
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i = NotificationCenter.peerSettingsDidLoaded;
                        Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                        objArr[0] = Long.valueOf(AnonymousClass16.this.val$dialogId);
                        instance.postNotificationName(i, objArr);
                    } else {
                        edit.putInt("spam3_" + AnonymousClass16.this.val$dialogId, MessagesController.UPDATE_MASK_NAME);
                    }
                    edit.commit();
                }
            }
        }

        AnonymousClass16(long j) {
            this.val$dialogId = j;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C04991(tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.17 */
    class AnonymousClass17 implements Runnable {
        final /* synthetic */ int val$minDate;

        AnonymousClass17(int i) {
            this.val$minDate = i;
        }

        public void run() {
            if ((MessagesController.this.currentDeletingTaskMids == null && !MessagesController.this.gettingNewDeleteTask) || (MessagesController.this.currentDeletingTaskTime != 0 && this.val$minDate < MessagesController.this.currentDeletingTaskTime)) {
                MessagesController.this.getNewDeleteTask(null);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ SparseArray val$mids;

        AnonymousClass18(SparseArray sparseArray) {
            this.val$mids = sparseArray;
        }

        public void run() {
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.didCreatedNewDeleteTask;
            Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
            objArr[0] = this.val$mids;
            instance.postNotificationName(i, objArr);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.19 */
    class AnonymousClass19 implements Runnable {
        final /* synthetic */ ArrayList val$oldTask;

        AnonymousClass19(ArrayList arrayList) {
            this.val$oldTask = arrayList;
        }

        public void run() {
            MessagesController.this.gettingNewDeleteTask = true;
            MessagesStorage.getInstance().getNewTask(this.val$oldTask);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.1 */
    class C05001 implements Comparator<TL_dialog> {
        C05001() {
        }

        public int compare(TL_dialog tL_dialog, TL_dialog tL_dialog2) {
            DraftMessage draft = DraftQuery.getDraft(tL_dialog.id);
            int i = (draft == null || draft.date < tL_dialog.last_message_date) ? tL_dialog.last_message_date : draft.date;
            DraftMessage draft2 = DraftQuery.getDraft(tL_dialog2.id);
            int i2 = (draft2 == null || draft2.date < tL_dialog2.last_message_date) ? tL_dialog2.last_message_date : draft2.date;
            return i < i2 ? MessagesController.UPDATE_MASK_NAME : i > i2 ? -1 : 0;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.21 */
    class AnonymousClass21 implements Runnable {
        final /* synthetic */ ArrayList val$messages;
        final /* synthetic */ int val$taskTime;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.21.1 */
        class C05021 implements Runnable {
            C05021() {
            }

            public void run() {
                MessagesController.this.checkDeletingTask(true);
            }
        }

        AnonymousClass21(ArrayList arrayList, int i) {
            this.val$messages = arrayList;
            this.val$taskTime = i;
        }

        public void run() {
            MessagesController.this.gettingNewDeleteTask = false;
            if (this.val$messages != null) {
                MessagesController.this.currentDeletingTaskTime = this.val$taskTime;
                MessagesController.this.currentDeletingTaskMids = this.val$messages;
                if (MessagesController.this.currentDeleteTaskRunnable != null) {
                    Utilities.stageQueue.cancelRunnable(MessagesController.this.currentDeleteTaskRunnable);
                    MessagesController.this.currentDeleteTaskRunnable = null;
                }
                if (!MessagesController.this.checkDeletingTask(false)) {
                    MessagesController.this.currentDeleteTaskRunnable = new C05021();
                    Utilities.stageQueue.postRunnable(MessagesController.this.currentDeleteTaskRunnable, ((long) Math.abs(ConnectionsManager.getInstance().getCurrentTime() - MessagesController.this.currentDeletingTaskTime)) * 1000);
                    return;
                }
                return;
            }
            MessagesController.this.currentDeletingTaskTime = 0;
            MessagesController.this.currentDeletingTaskMids = null;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.22 */
    class AnonymousClass22 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ int val$did;
        final /* synthetic */ long val$max_id;
        final /* synthetic */ int val$offset;

        AnonymousClass22(int i, int i2, int i3, long j, int i4) {
            this.val$did = i;
            this.val$offset = i2;
            this.val$count = i3;
            this.val$max_id = j;
            this.val$classGuid = i4;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                MessagesController.this.processLoadedUserPhotos((photos_Photos) tLObject, this.val$did, this.val$offset, this.val$count, this.val$max_id, false, this.val$classGuid);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.23 */
    class AnonymousClass23 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ int val$did;
        final /* synthetic */ long val$max_id;
        final /* synthetic */ int val$offset;

        AnonymousClass23(int i, int i2, int i3, long j, int i4) {
            this.val$did = i;
            this.val$offset = i2;
            this.val$count = i3;
            this.val$max_id = j;
            this.val$classGuid = i4;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                photos_Photos tL_photos_photos = new TL_photos_photos();
                tL_photos_photos.count = com_hanista_mobogram_tgnet_TLRPC_messages_Messages.count;
                tL_photos_photos.users.addAll(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users);
                for (int i = 0; i < com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size(); i += MessagesController.UPDATE_MASK_NAME) {
                    Message message = (Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(i);
                    if (!(message.action == null || message.action.photo == null)) {
                        tL_photos_photos.photos.add(message.action.photo);
                    }
                }
                MessagesController.this.processLoadedUserPhotos(tL_photos_photos, this.val$did, this.val$offset, this.val$count, this.val$max_id, false, this.val$classGuid);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.24 */
    class AnonymousClass24 implements RequestDelegate {
        final /* synthetic */ User val$user;

        AnonymousClass24(User user) {
            this.val$user = user;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(Integer.valueOf(this.val$user.id));
                MessagesStorage.getInstance().putBlockedUsers(arrayList, false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.25 */
    class AnonymousClass25 implements RequestDelegate {
        final /* synthetic */ User val$user;

        AnonymousClass25(User user) {
            this.val$user = user;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            MessagesStorage.getInstance().deleteBlockedUser(this.val$user.id);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.27 */
    class AnonymousClass27 implements Runnable {
        final /* synthetic */ boolean val$cache;
        final /* synthetic */ ArrayList val$ids;
        final /* synthetic */ ArrayList val$users;

        AnonymousClass27(ArrayList arrayList, boolean z, ArrayList arrayList2) {
            this.val$users = arrayList;
            this.val$cache = z;
            this.val$ids = arrayList2;
        }

        public void run() {
            if (this.val$users != null) {
                MessagesController.this.putUsers(this.val$users, this.val$cache);
            }
            MessagesController.this.loadingBlockedUsers = false;
            if (this.val$ids.isEmpty() && this.val$cache && !UserConfig.blockedUsersLoaded) {
                MessagesController.this.getBlockedUsers(false);
                return;
            }
            if (!this.val$cache) {
                UserConfig.blockedUsersLoaded = true;
                UserConfig.saveConfig(false);
            }
            MessagesController.this.blockedUsers = this.val$ids;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.2 */
    class C05042 implements Comparator<Update> {
        C05042() {
        }

        public int compare(Update update, Update update2) {
            int access$000 = MessagesController.this.getUpdateType(update);
            int access$0002 = MessagesController.this.getUpdateType(update2);
            if (access$000 != access$0002) {
                return AndroidUtilities.compare(access$000, access$0002);
            }
            if (access$000 == 0) {
                return AndroidUtilities.compare(update.pts, update2.pts);
            }
            if (access$000 == MessagesController.UPDATE_MASK_NAME) {
                return AndroidUtilities.compare(update.qts, update2.qts);
            }
            if (access$000 != MessagesController.UPDATE_MASK_AVATAR) {
                return 0;
            }
            access$000 = MessagesController.this.getUpdateChannelId(update);
            access$0002 = MessagesController.this.getUpdateChannelId(update2);
            return access$000 == access$0002 ? AndroidUtilities.compare(update.pts, update2.pts) : AndroidUtilities.compare(access$000, access$0002);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.30 */
    class AnonymousClass30 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ int val$did;
        final /* synthetic */ boolean val$fromCache;
        final /* synthetic */ int val$offset;
        final /* synthetic */ photos_Photos val$res;

        AnonymousClass30(photos_Photos com_hanista_mobogram_tgnet_TLRPC_photos_Photos, boolean z, int i, int i2, int i3, int i4) {
            this.val$res = com_hanista_mobogram_tgnet_TLRPC_photos_Photos;
            this.val$fromCache = z;
            this.val$did = i;
            this.val$offset = i2;
            this.val$count = i3;
            this.val$classGuid = i4;
        }

        public void run() {
            MessagesController.this.putUsers(this.val$res.users, this.val$fromCache);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogPhotosLoaded, Integer.valueOf(this.val$did), Integer.valueOf(this.val$offset), Integer.valueOf(this.val$count), Boolean.valueOf(this.val$fromCache), Integer.valueOf(this.val$classGuid), this.val$res.photos);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.31 */
    class AnonymousClass31 implements RequestDelegate {
        final /* synthetic */ int val$channelId;

        AnonymousClass31(int i) {
            this.val$channelId = i;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
                MessagesController.this.processNewChannelDifferenceParams(tL_messages_affectedMessages.pts, tL_messages_affectedMessages.pts_count, this.val$channelId);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.34 */
    class AnonymousClass34 implements RequestDelegate {
        final /* synthetic */ Chat val$chat;
        final /* synthetic */ User val$user;

        AnonymousClass34(Chat chat, User user) {
            this.val$chat = chat;
            this.val$user = user;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                TL_messages_affectedHistory tL_messages_affectedHistory = (TL_messages_affectedHistory) tLObject;
                if (tL_messages_affectedHistory.offset > 0) {
                    MessagesController.this.deleteUserChannelHistory(this.val$chat, this.val$user, tL_messages_affectedHistory.offset);
                }
                MessagesController.this.processNewChannelDifferenceParams(tL_messages_affectedHistory.pts, tL_messages_affectedHistory.pts_count, this.val$chat.id);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.35 */
    class AnonymousClass35 implements Runnable {
        final /* synthetic */ long val$did;

        AnonymousClass35(long j) {
            this.val$did = j;
        }

        public void run() {
            MessagesController.this.channelsPts.remove(Integer.valueOf(-((int) this.val$did)));
            MessagesController.this.shortPollChannels.delete(-((int) this.val$did));
            MessagesController.this.needShortPollChannels.delete(-((int) this.val$did));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.36 */
    class AnonymousClass36 implements Runnable {
        final /* synthetic */ long val$did;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.36.1 */
        class C05051 implements Runnable {
            C05051() {
            }

            public void run() {
                NotificationsController.getInstance().removeNotificationsForDialog(AnonymousClass36.this.val$did);
            }
        }

        AnonymousClass36(long j) {
            this.val$did = j;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05051());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.37 */
    class AnonymousClass37 implements RequestDelegate {
        final /* synthetic */ long val$did;
        final /* synthetic */ int val$max_id_delete_final;
        final /* synthetic */ int val$onlyHistory;

        AnonymousClass37(long j, int i, int i2) {
            this.val$did = j;
            this.val$onlyHistory = i;
            this.val$max_id_delete_final = i2;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                TL_messages_affectedHistory tL_messages_affectedHistory = (TL_messages_affectedHistory) tLObject;
                if (tL_messages_affectedHistory.offset > 0) {
                    MessagesController.this.deleteDialog(this.val$did, false, this.val$onlyHistory, this.val$max_id_delete_final);
                }
                MessagesController.this.processNewDifferenceParams(-1, tL_messages_affectedHistory.pts, -1, tL_messages_affectedHistory.pts_count);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.3 */
    class C05063 implements Runnable {
        final /* synthetic */ TL_config val$config;

        C05063(TL_config tL_config) {
            this.val$config = tL_config;
        }

        public void run() {
            MessagesController.this.maxMegagroupCount = this.val$config.megagroup_size_max;
            MessagesController.this.maxGroupCount = this.val$config.chat_size_max;
            MessagesController.this.groupBigSize = this.val$config.chat_big_size;
            MessagesController.this.disabledFeatures = this.val$config.disabled_features;
            MessagesController.this.maxEditTime = this.val$config.edit_time_limit;
            MessagesController.this.ratingDecay = this.val$config.rating_e_decay;
            MessagesController.this.maxRecentGifsCount = this.val$config.saved_gifs_limit;
            MessagesController.this.maxRecentStickersCount = this.val$config.stickers_recent_limit;
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
            edit.putInt("maxGroupCount", MessagesController.this.maxGroupCount);
            edit.putInt("maxMegagroupCount", MessagesController.this.maxMegagroupCount);
            edit.putInt("groupBigSize", MessagesController.this.groupBigSize);
            edit.putInt("maxEditTime", MessagesController.this.maxEditTime);
            edit.putInt("ratingDecay", MessagesController.this.ratingDecay);
            edit.putInt("maxRecentGifsCount", MessagesController.this.maxRecentGifsCount);
            edit.putInt("maxRecentStickersCount", MessagesController.this.maxRecentStickersCount);
            try {
                AbstractSerializedData serializedData = new SerializedData();
                serializedData.writeInt32(MessagesController.this.disabledFeatures.size());
                Iterator it = MessagesController.this.disabledFeatures.iterator();
                while (it.hasNext()) {
                    ((TL_disabledFeature) it.next()).serializeToStream(serializedData);
                }
                String encodeToString = Base64.encodeToString(serializedData.toByteArray(), 0);
                if (encodeToString.length() != 0) {
                    edit.putString("disabledFeatures", encodeToString);
                }
            } catch (Throwable e) {
                edit.remove("disabledFeatures");
                FileLog.m18e("tmessages", e);
            }
            edit.commit();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.40 */
    class AnonymousClass40 implements RequestDelegate {
        final /* synthetic */ Integer val$chat_id;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.40.1 */
        class C05081 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C05081(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$error == null) {
                    TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) this.val$response;
                    MessagesController.this.putUsers(tL_channels_channelParticipants.users, false);
                    MessagesStorage.getInstance().putUsersAndChats(tL_channels_channelParticipants.users, null, true, true);
                    MessagesStorage.getInstance().updateChannelUsers(AnonymousClass40.this.val$chat_id.intValue(), tL_channels_channelParticipants.participants);
                    MessagesController.this.loadedFullParticipants.add(AnonymousClass40.this.val$chat_id);
                }
                MessagesController.this.loadingFullParticipants.remove(AnonymousClass40.this.val$chat_id);
            }
        }

        AnonymousClass40(Integer num) {
            this.val$chat_id = num;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C05081(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.41 */
    class AnonymousClass41 implements Runnable {
        final /* synthetic */ boolean val$byChannelUsers;
        final /* synthetic */ boolean val$fromCache;
        final /* synthetic */ ChatFull val$info;
        final /* synthetic */ MessageObject val$pinnedMessageObject;
        final /* synthetic */ ArrayList val$usersArr;

        AnonymousClass41(ArrayList arrayList, boolean z, ChatFull chatFull, boolean z2, MessageObject messageObject) {
            this.val$usersArr = arrayList;
            this.val$fromCache = z;
            this.val$info = chatFull;
            this.val$byChannelUsers = z2;
            this.val$pinnedMessageObject = messageObject;
        }

        public void run() {
            MessagesController.this.putUsers(this.val$usersArr, this.val$fromCache);
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.chatInfoDidLoaded;
            Object[] objArr = new Object[MessagesController.UPDATE_MASK_STATUS];
            objArr[0] = this.val$info;
            objArr[MessagesController.UPDATE_MASK_NAME] = Integer.valueOf(0);
            objArr[MessagesController.UPDATE_MASK_AVATAR] = Boolean.valueOf(this.val$byChannelUsers);
            objArr[3] = this.val$pinnedMessageObject;
            instance.postNotificationName(i, objArr);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.45 */
    class AnonymousClass45 implements RequestDelegate {
        final /* synthetic */ int val$key;
        final /* synthetic */ TL_messages_getMessagesViews val$req;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.45.1 */
        class C05091 implements Runnable {
            final /* synthetic */ SparseArray val$channelViews;

            C05091(SparseArray sparseArray) {
                this.val$channelViews = sparseArray;
            }

            public void run() {
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.didUpdatedMessagesViews;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = this.val$channelViews;
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass45(int i, TL_messages_getMessagesViews tL_messages_getMessagesViews) {
            this.val$key = i;
            this.val$req = tL_messages_getMessagesViews;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                SparseIntArray sparseIntArray;
                Vector vector = (Vector) tLObject;
                SparseArray sparseArray = new SparseArray();
                SparseIntArray sparseIntArray2 = (SparseIntArray) sparseArray.get(this.val$key);
                if (sparseIntArray2 == null) {
                    sparseIntArray2 = new SparseIntArray();
                    sparseArray.put(this.val$key, sparseIntArray2);
                    sparseIntArray = sparseIntArray2;
                } else {
                    sparseIntArray = sparseIntArray2;
                }
                int i = 0;
                while (i < this.val$req.id.size() && i < vector.objects.size()) {
                    sparseIntArray.put(((Integer) this.val$req.id.get(i)).intValue(), ((Integer) vector.objects.get(i)).intValue());
                    i += MessagesController.UPDATE_MASK_NAME;
                }
                MessagesStorage.getInstance().putChannelViews(sparseArray, this.val$req.peer instanceof TL_inputPeerChannel);
                AndroidUtilities.runOnUIThread(new C05091(sparseArray));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.48 */
    class AnonymousClass48 implements Runnable {
        final /* synthetic */ HashMap val$newPrintingStrings;
        final /* synthetic */ HashMap val$newPrintingStringsTypes;

        AnonymousClass48(HashMap hashMap, HashMap hashMap2) {
            this.val$newPrintingStrings = hashMap;
            this.val$newPrintingStringsTypes = hashMap2;
        }

        public void run() {
            MessagesController.this.printingStrings = this.val$newPrintingStrings;
            MessagesController.this.printingStringsTypes = this.val$newPrintingStringsTypes;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.49 */
    class AnonymousClass49 implements RequestDelegate {
        final /* synthetic */ int val$action;
        final /* synthetic */ long val$dialog_id;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.49.1 */
        class C05101 implements Runnable {
            C05101() {
            }

            public void run() {
                HashMap hashMap = (HashMap) MessagesController.this.sendingTypings.get(Integer.valueOf(AnonymousClass49.this.val$action));
                if (hashMap != null) {
                    hashMap.remove(Long.valueOf(AnonymousClass49.this.val$dialog_id));
                }
            }
        }

        AnonymousClass49(int i, long j) {
            this.val$action = i;
            this.val$dialog_id = j;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C05101());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.4 */
    class C05114 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.4.1 */
        class C05071 implements Runnable {
            C05071() {
            }

            public void run() {
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_AVATAR);
                instance.postNotificationName(i, objArr);
                UserConfig.saveConfig(true);
            }
        }

        C05114() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                User user = MessagesController.this.getUser(Integer.valueOf(UserConfig.getClientUserId()));
                if (user == null) {
                    user = UserConfig.getCurrentUser();
                    MessagesController.this.putUser(user, true);
                } else {
                    UserConfig.setCurrentUser(user);
                }
                if (user != null) {
                    TL_photos_photo tL_photos_photo = (TL_photos_photo) tLObject;
                    ArrayList arrayList = tL_photos_photo.photo.sizes;
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 100);
                    PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, PointerIconCompat.TYPE_DEFAULT);
                    user.photo = new TL_userProfilePhoto();
                    user.photo.photo_id = tL_photos_photo.photo.id;
                    if (closestPhotoSizeWithSize != null) {
                        user.photo.photo_small = closestPhotoSizeWithSize.location;
                    }
                    if (closestPhotoSizeWithSize2 != null) {
                        user.photo.photo_big = closestPhotoSizeWithSize2.location;
                    } else if (closestPhotoSizeWithSize != null) {
                        user.photo.photo_small = closestPhotoSizeWithSize.location;
                    }
                    MessagesStorage.getInstance().clearUserPhotos(user.id);
                    arrayList = new ArrayList();
                    arrayList.add(user);
                    MessagesStorage.getInstance().putUsersAndChats(arrayList, null, false, true);
                    AndroidUtilities.runOnUIThread(new C05071());
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.50 */
    class AnonymousClass50 implements RequestDelegate {
        final /* synthetic */ int val$action;
        final /* synthetic */ long val$dialog_id;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.50.1 */
        class C05121 implements Runnable {
            C05121() {
            }

            public void run() {
                HashMap hashMap = (HashMap) MessagesController.this.sendingTypings.get(Integer.valueOf(AnonymousClass50.this.val$action));
                if (hashMap != null) {
                    hashMap.remove(Long.valueOf(AnonymousClass50.this.val$dialog_id));
                }
            }
        }

        AnonymousClass50(int i, long j) {
            this.val$action = i;
            this.val$dialog_id = j;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C05121());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.51 */
    class AnonymousClass51 implements RequestDelegate {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$first_unread;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ int val$last_date;
        final /* synthetic */ int val$last_message_id;
        final /* synthetic */ int val$loadIndex;
        final /* synthetic */ int val$load_type;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ boolean val$queryFromServer;
        final /* synthetic */ int val$unread_count;

        AnonymousClass51(int i, long j, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z, int i9, boolean z2) {
            this.val$count = i;
            this.val$dialog_id = j;
            this.val$max_id = i2;
            this.val$classGuid = i3;
            this.val$first_unread = i4;
            this.val$last_message_id = i5;
            this.val$unread_count = i6;
            this.val$last_date = i7;
            this.val$load_type = i8;
            this.val$isChannel = z;
            this.val$loadIndex = i9;
            this.val$queryFromServer = z2;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject != null) {
                messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                if (com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size() > this.val$count) {
                    com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.remove(0);
                }
                MessagesController.this.processLoadedMessages(com_hanista_mobogram_tgnet_TLRPC_messages_Messages, this.val$dialog_id, this.val$count, this.val$max_id, false, this.val$classGuid, this.val$first_unread, this.val$last_message_id, this.val$unread_count, this.val$last_date, this.val$load_type, this.val$isChannel, false, this.val$loadIndex, this.val$queryFromServer);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.52 */
    class AnonymousClass52 implements RequestDelegate {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ String val$url;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.52.1 */
        class C05131 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C05131(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                ArrayList arrayList = (ArrayList) MessagesController.this.reloadingWebpages.remove(AnonymousClass52.this.val$url);
                if (arrayList != null) {
                    messages_Messages tL_messages_messages = new TL_messages_messages();
                    if (this.val$response instanceof TL_messageMediaWebPage) {
                        TL_messageMediaWebPage tL_messageMediaWebPage = (TL_messageMediaWebPage) this.val$response;
                        if ((tL_messageMediaWebPage.webpage instanceof TL_webPage) || (tL_messageMediaWebPage.webpage instanceof TL_webPageEmpty)) {
                            for (int i = 0; i < arrayList.size(); i += MessagesController.UPDATE_MASK_NAME) {
                                ((MessageObject) arrayList.get(i)).messageOwner.media.webpage = tL_messageMediaWebPage.webpage;
                                if (i == 0) {
                                    ImageLoader.saveMessageThumbs(((MessageObject) arrayList.get(i)).messageOwner);
                                }
                                tL_messages_messages.messages.add(((MessageObject) arrayList.get(i)).messageOwner);
                            }
                        } else {
                            MessagesController.this.reloadingWebpagesPending.put(Long.valueOf(tL_messageMediaWebPage.webpage.id), arrayList);
                        }
                    } else {
                        for (int i2 = 0; i2 < arrayList.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                            ((MessageObject) arrayList.get(i2)).messageOwner.media.webpage = new TL_webPageEmpty();
                            tL_messages_messages.messages.add(((MessageObject) arrayList.get(i2)).messageOwner);
                        }
                    }
                    if (!tL_messages_messages.messages.isEmpty()) {
                        MessagesStorage.getInstance().putMessages(tL_messages_messages, AnonymousClass52.this.val$dialog_id, -2, 0, false);
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i3 = NotificationCenter.replaceMessagesObjects;
                        Object[] objArr = new Object[MessagesController.UPDATE_MASK_AVATAR];
                        objArr[0] = Long.valueOf(AnonymousClass52.this.val$dialog_id);
                        objArr[MessagesController.UPDATE_MASK_NAME] = arrayList;
                        instance.postNotificationName(i3, objArr);
                    }
                }
            }
        }

        AnonymousClass52(String str, long j) {
            this.val$url = str;
            this.val$dialog_id = j;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C05131(tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.53 */
    class AnonymousClass53 implements Runnable {
        final /* synthetic */ int val$classGuid;
        final /* synthetic */ int val$count;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$first_unread;
        final /* synthetic */ boolean val$isCache;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ boolean val$isEnd;
        final /* synthetic */ int val$last_date;
        final /* synthetic */ int val$last_message_id;
        final /* synthetic */ int val$loadIndex;
        final /* synthetic */ int val$load_type;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ messages_Messages val$messagesRes;
        final /* synthetic */ boolean val$queryFromServer;
        final /* synthetic */ int val$unread_count;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.53.1 */
        class C05141 implements Runnable {
            C05141() {
            }

            public void run() {
                MessagesController messagesController = MessagesController.this;
                long j = AnonymousClass53.this.val$dialog_id;
                int i = AnonymousClass53.this.val$count;
                int i2 = (AnonymousClass53.this.val$load_type == MessagesController.UPDATE_MASK_AVATAR && AnonymousClass53.this.val$queryFromServer) ? AnonymousClass53.this.val$first_unread : AnonymousClass53.this.val$max_id;
                messagesController.loadMessages(j, i, i2, false, 0, AnonymousClass53.this.val$classGuid, AnonymousClass53.this.val$load_type, AnonymousClass53.this.val$last_message_id, AnonymousClass53.this.val$isChannel, AnonymousClass53.this.val$loadIndex, AnonymousClass53.this.val$first_unread, AnonymousClass53.this.val$unread_count, AnonymousClass53.this.val$last_date, AnonymousClass53.this.val$queryFromServer);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.53.2 */
        class C05152 implements Runnable {
            final /* synthetic */ ArrayList val$messagesToReload;
            final /* synthetic */ ArrayList val$objects;
            final /* synthetic */ HashMap val$webpagesToReload;

            C05152(ArrayList arrayList, ArrayList arrayList2, HashMap hashMap) {
                this.val$objects = arrayList;
                this.val$messagesToReload = arrayList2;
                this.val$webpagesToReload = hashMap;
            }

            public void run() {
                int i;
                MessagesController.this.putUsers(AnonymousClass53.this.val$messagesRes.users, AnonymousClass53.this.val$isCache);
                MessagesController.this.putChats(AnonymousClass53.this.val$messagesRes.chats, AnonymousClass53.this.val$isCache);
                if (AnonymousClass53.this.val$queryFromServer && AnonymousClass53.this.val$load_type == MessagesController.UPDATE_MASK_AVATAR) {
                    int i2 = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    for (int i3 = 0; i3 < AnonymousClass53.this.val$messagesRes.messages.size(); i3 += MessagesController.UPDATE_MASK_NAME) {
                        Message message = (Message) AnonymousClass53.this.val$messagesRes.messages.get(i3);
                        if (!message.out && message.id > AnonymousClass53.this.val$first_unread && message.id < i2) {
                            i2 = message.id;
                        }
                    }
                    i = i2;
                } else {
                    i = ConnectionsManager.DEFAULT_DATACENTER_ID;
                }
                if (i == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                    i = AnonymousClass53.this.val$first_unread;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDidLoaded, Long.valueOf(AnonymousClass53.this.val$dialog_id), Integer.valueOf(AnonymousClass53.this.val$count), this.val$objects, Boolean.valueOf(AnonymousClass53.this.val$isCache), Integer.valueOf(i), Integer.valueOf(AnonymousClass53.this.val$last_message_id), Integer.valueOf(AnonymousClass53.this.val$unread_count), Integer.valueOf(AnonymousClass53.this.val$last_date), Integer.valueOf(AnonymousClass53.this.val$load_type), Boolean.valueOf(AnonymousClass53.this.val$isEnd), Integer.valueOf(AnonymousClass53.this.val$classGuid), Integer.valueOf(AnonymousClass53.this.val$loadIndex));
                if (!this.val$messagesToReload.isEmpty()) {
                    MessagesController.this.reloadMessages(this.val$messagesToReload, AnonymousClass53.this.val$dialog_id);
                }
                if (!this.val$webpagesToReload.isEmpty()) {
                    MessagesController.this.reloadWebPages(AnonymousClass53.this.val$dialog_id, this.val$webpagesToReload);
                }
            }
        }

        AnonymousClass53(messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages, long j, boolean z, int i, int i2, boolean z2, int i3, int i4, int i5, int i6, boolean z3, int i7, int i8, int i9, boolean z4) {
            this.val$messagesRes = com_hanista_mobogram_tgnet_TLRPC_messages_Messages;
            this.val$dialog_id = j;
            this.val$isCache = z;
            this.val$count = i;
            this.val$load_type = i2;
            this.val$queryFromServer = z2;
            this.val$first_unread = i3;
            this.val$max_id = i4;
            this.val$classGuid = i5;
            this.val$last_message_id = i6;
            this.val$isChannel = z3;
            this.val$loadIndex = i7;
            this.val$unread_count = i8;
            this.val$last_date = i9;
            this.val$isEnd = z4;
        }

        public void run() {
            int i;
            Chat chat;
            boolean z;
            boolean z2;
            if (this.val$messagesRes instanceof TL_messages_channelMessages) {
                boolean z3;
                int i2 = -((int) this.val$dialog_id);
                if (((Integer) MessagesController.this.channelsPts.get(Integer.valueOf(i2))) == null && Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(i2)).intValue() == 0) {
                    MessagesController.this.channelsPts.put(Integer.valueOf(i2), Integer.valueOf(this.val$messagesRes.pts));
                    if (MessagesController.this.needShortPollChannels.indexOfKey(i2) < 0 || MessagesController.this.shortPollChannels.indexOfKey(i2) >= 0) {
                        MessagesController.this.getChannelDifference(i2);
                        z3 = true;
                    } else {
                        MessagesController.this.getChannelDifference(i2, MessagesController.UPDATE_MASK_AVATAR, 0);
                        z3 = true;
                    }
                } else {
                    z3 = false;
                }
                for (i = 0; i < this.val$messagesRes.chats.size(); i += MessagesController.UPDATE_MASK_NAME) {
                    chat = (Chat) this.val$messagesRes.chats.get(i);
                    if (chat.id == i2) {
                        z = chat.megagroup;
                        z2 = z3;
                        break;
                    }
                }
                z = false;
                z2 = z3;
            } else {
                z = false;
                z2 = false;
            }
            int i3 = (int) this.val$dialog_id;
            int i4 = (int) (this.val$dialog_id >> MessagesController.UPDATE_MASK_CHAT_MEMBERS);
            if (!this.val$isCache) {
                ImageLoader.saveMessagesThumbs(this.val$messagesRes.messages);
            }
            if (i4 == MessagesController.UPDATE_MASK_NAME || i3 == 0 || !this.val$isCache || this.val$messagesRes.messages.size() != 0) {
                Message message;
                AbstractMap hashMap = new HashMap();
                AbstractMap hashMap2 = new HashMap();
                for (i4 = 0; i4 < this.val$messagesRes.users.size(); i4 += MessagesController.UPDATE_MASK_NAME) {
                    User user = (User) this.val$messagesRes.users.get(i4);
                    hashMap.put(Integer.valueOf(user.id), user);
                }
                for (i4 = 0; i4 < this.val$messagesRes.chats.size(); i4 += MessagesController.UPDATE_MASK_NAME) {
                    chat = (Chat) this.val$messagesRes.chats.get(i4);
                    hashMap2.put(Integer.valueOf(chat.id), chat);
                }
                int size = this.val$messagesRes.messages.size();
                if (!this.val$isCache) {
                    Integer num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(this.val$dialog_id));
                    if (num == null) {
                        num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$dialog_id));
                        MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(this.val$dialog_id), num);
                    }
                    Integer num2 = num;
                    num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(this.val$dialog_id));
                    if (num == null) {
                        num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$dialog_id));
                        MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(this.val$dialog_id), num);
                    }
                    Integer num3 = num;
                    for (int i5 = 0; i5 < size; i5 += MessagesController.UPDATE_MASK_NAME) {
                        message = (Message) this.val$messagesRes.messages.get(i5);
                        if (!(this.val$isCache || !message.post || message.out)) {
                            message.media_unread = true;
                        }
                        if (z) {
                            message.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                        }
                        if (message.action instanceof TL_messageActionChatDeleteUser) {
                            User user2 = (User) hashMap.get(Integer.valueOf(message.action.user_id));
                            if (user2 != null && user2.bot) {
                                message.reply_markup = new TL_replyKeyboardHide();
                            }
                        }
                        if ((message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate)) {
                            message.unread = false;
                            message.media_unread = false;
                        } else {
                            message.unread = (message.out ? num3 : num2).intValue() < message.id;
                        }
                    }
                    MessagesStorage.getInstance().putMessages(this.val$messagesRes, this.val$dialog_id, this.val$load_type, this.val$max_id, z2);
                }
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                HashMap hashMap3 = new HashMap();
                for (i = 0; i < size; i += MessagesController.UPDATE_MASK_NAME) {
                    message = (Message) this.val$messagesRes.messages.get(i);
                    message.dialog_id = this.val$dialog_id;
                    MessageObject messageObject = new MessageObject(message, hashMap, hashMap2, true);
                    arrayList.add(messageObject);
                    if (this.val$isCache) {
                        if (message.media instanceof TL_messageMediaUnsupported) {
                            if (message.media.bytes != null && (message.media.bytes.length == 0 || (message.media.bytes.length == MessagesController.UPDATE_MASK_NAME && message.media.bytes[0] < 57))) {
                                arrayList2.add(Integer.valueOf(message.id));
                            }
                        } else if (message.media instanceof TL_messageMediaWebPage) {
                            if ((message.media.webpage instanceof TL_webPagePending) && message.media.webpage.date <= ConnectionsManager.getInstance().getCurrentTime()) {
                                arrayList2.add(Integer.valueOf(message.id));
                            } else if (message.media.webpage instanceof TL_webPageUrlPending) {
                                ArrayList arrayList3 = (ArrayList) hashMap3.get(message.media.webpage.url);
                                if (arrayList3 == null) {
                                    arrayList3 = new ArrayList();
                                    hashMap3.put(message.media.webpage.url, arrayList3);
                                }
                                arrayList3.add(messageObject);
                            }
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new C05152(arrayList, arrayList2, hashMap3));
                return;
            }
            AndroidUtilities.runOnUIThread(new C05141());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.54 */
    class AnonymousClass54 implements RequestDelegate {
        final /* synthetic */ int val$count;

        AnonymousClass54(int i) {
            this.val$count = i;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                MessagesController.this.processLoadedDialogs((messages_Dialogs) tLObject, null, 0, this.val$count, 0, false, false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.56 */
    class AnonymousClass56 implements Runnable {
        final /* synthetic */ int val$count;
        final /* synthetic */ messages_Dialogs val$dialogsRes;
        final /* synthetic */ ArrayList val$encChats;
        final /* synthetic */ int val$loadType;
        final /* synthetic */ boolean val$migrate;
        final /* synthetic */ int val$offset;
        final /* synthetic */ boolean val$resetEnd;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.56.1 */
        class C05191 implements Runnable {
            C05191() {
            }

            public void run() {
                MessagesController.this.putUsers(AnonymousClass56.this.val$dialogsRes.users, true);
                MessagesController.this.loadingDialogs = false;
                if (AnonymousClass56.this.val$resetEnd) {
                    MessagesController.this.dialogsEndReached = false;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                MessagesController.this.loadDialogs(0, AnonymousClass56.this.val$count, false);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.56.2 */
        class C05202 implements Runnable {
            final /* synthetic */ HashMap val$chatsDict;
            final /* synthetic */ ArrayList val$dialogsToReload;
            final /* synthetic */ HashMap val$new_dialogMessage;
            final /* synthetic */ HashMap val$new_dialogs_dict;

            C05202(HashMap hashMap, HashMap hashMap2, HashMap hashMap3, ArrayList arrayList) {
                this.val$new_dialogs_dict = hashMap;
                this.val$new_dialogMessage = hashMap2;
                this.val$chatsDict = hashMap3;
                this.val$dialogsToReload = arrayList;
            }

            public void run() {
                if (AnonymousClass56.this.val$loadType != MessagesController.UPDATE_MASK_NAME) {
                    MessagesController.this.applyDialogsNotificationsSettings(AnonymousClass56.this.val$dialogsRes.dialogs);
                    if (!UserConfig.draftsLoaded) {
                        DraftQuery.loadDrafts();
                    }
                }
                MessagesController.this.putUsers(AnonymousClass56.this.val$dialogsRes.users, AnonymousClass56.this.val$loadType == MessagesController.UPDATE_MASK_NAME);
                MessagesController.this.putChats(AnonymousClass56.this.val$dialogsRes.chats, AnonymousClass56.this.val$loadType == MessagesController.UPDATE_MASK_NAME);
                if (AnonymousClass56.this.val$encChats != null) {
                    for (int i = 0; i < AnonymousClass56.this.val$encChats.size(); i += MessagesController.UPDATE_MASK_NAME) {
                        EncryptedChat encryptedChat = (EncryptedChat) AnonymousClass56.this.val$encChats.get(i);
                        if ((encryptedChat instanceof TL_encryptedChat) && AndroidUtilities.getMyLayerVersion(encryptedChat.layer) < 46) {
                            SecretChatHelper.getInstance().sendNotifyLayerMessage(encryptedChat, null);
                        }
                        MessagesController.this.putEncryptedChat(encryptedChat, true);
                    }
                }
                if (!AnonymousClass56.this.val$migrate) {
                    MessagesController.this.loadingDialogs = false;
                }
                if (!AnonymousClass56.this.val$migrate || MessagesController.this.dialogs.isEmpty()) {
                    boolean z = false;
                } else {
                    int i2 = ((TL_dialog) MessagesController.this.dialogs.get(MessagesController.this.dialogs.size() - 1)).last_message_date;
                }
                boolean z2 = false;
                for (Entry entry : this.val$new_dialogs_dict.entrySet()) {
                    boolean z3;
                    Long l = (Long) entry.getKey();
                    TL_dialog tL_dialog = (TL_dialog) entry.getValue();
                    if (!AnonymousClass56.this.val$migrate || r6 == 0 || tL_dialog.last_message_date >= r6) {
                        TL_dialog tL_dialog2 = (TL_dialog) MessagesController.this.dialogs_dict.get(l);
                        if (AnonymousClass56.this.val$loadType != MessagesController.UPDATE_MASK_NAME && (tL_dialog.draft instanceof TL_draftMessage)) {
                            DraftQuery.saveDraft(tL_dialog.id, tL_dialog.draft, null, false);
                        }
                        MessageObject messageObject;
                        if (tL_dialog2 == null) {
                            MessagesController.this.dialogs_dict.put(l, tL_dialog);
                            messageObject = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(tL_dialog.id));
                            MessagesController.this.dialogMessage.put(l, messageObject);
                            if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject.getId()), messageObject);
                                if (messageObject.messageOwner.random_id != 0) {
                                    MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject.messageOwner.random_id), messageObject);
                                }
                            }
                            z3 = true;
                        } else {
                            if (AnonymousClass56.this.val$loadType != MessagesController.UPDATE_MASK_NAME) {
                                tL_dialog2.notify_settings = tL_dialog.notify_settings;
                            }
                            MessageObject messageObject2 = (MessageObject) MessagesController.this.dialogMessage.get(l);
                            if ((messageObject2 == null || !messageObject2.deleted) && messageObject2 != null && tL_dialog2.top_message <= 0) {
                                MessageObject messageObject3 = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(tL_dialog.id));
                                if (messageObject2.deleted || messageObject3 == null || messageObject3.messageOwner.date > messageObject2.messageOwner.date) {
                                    MessagesController.this.dialogs_dict.put(l, tL_dialog);
                                    MessagesController.this.dialogMessage.put(l, messageObject3);
                                    if (messageObject3 != null && messageObject3.messageOwner.to_id.channel_id == 0) {
                                        MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject3.getId()), messageObject3);
                                        if (!(messageObject3 == null || messageObject3.messageOwner.random_id == 0)) {
                                            MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject3.messageOwner.random_id), messageObject3);
                                        }
                                    }
                                    MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(messageObject2.getId()));
                                    if (messageObject2.messageOwner.random_id != 0) {
                                        MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(messageObject2.messageOwner.random_id));
                                    }
                                }
                            } else if (tL_dialog.top_message >= tL_dialog2.top_message) {
                                MessagesController.this.dialogs_dict.put(l, tL_dialog);
                                messageObject = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(tL_dialog.id));
                                MessagesController.this.dialogMessage.put(l, messageObject);
                                if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                    MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject.getId()), messageObject);
                                    if (!(messageObject == null || messageObject.messageOwner.random_id == 0)) {
                                        MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject.messageOwner.random_id), messageObject);
                                    }
                                }
                                if (messageObject2 != null) {
                                    MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(messageObject2.getId()));
                                    if (messageObject2.messageOwner.random_id != 0) {
                                        MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(messageObject2.messageOwner.random_id));
                                    }
                                }
                                z3 = z2;
                            }
                            z3 = z2;
                        }
                        z2 = z3;
                    }
                }
                MessagesController.this.dialogs.clear();
                MessagesController.this.dialogsServerOnly.clear();
                MessagesController.this.dialogsUnreadOnly.clear();
                MessagesController.this.dialogsGroupsOnly.clear();
                MessagesController.this.dialogsJustGroupsOnly.clear();
                MessagesController.this.dialogsSuperGroupsOnly.clear();
                MessagesController.this.dialogsChannelOnly.clear();
                MessagesController.this.dialogsContactOnly.clear();
                MessagesController.this.dialogsFavoriteOnly.clear();
                MessagesController.this.dialogsHiddenOnly.clear();
                MessagesController.this.dialogsBotOnly.clear();
                MessagesController.this.dialogs.addAll(MessagesController.this.dialogs_dict.values());
                MessagesController.this.sortDialogs(AnonymousClass56.this.val$migrate ? this.val$chatsDict : null);
                if (!(AnonymousClass56.this.val$loadType == MessagesController.UPDATE_MASK_AVATAR || AnonymousClass56.this.val$migrate)) {
                    MessagesController messagesController = MessagesController.this;
                    z3 = (AnonymousClass56.this.val$dialogsRes.dialogs.size() == 0 || AnonymousClass56.this.val$dialogsRes.dialogs.size() != AnonymousClass56.this.val$count) && AnonymousClass56.this.val$loadType == 0;
                    messagesController.dialogsEndReached = z3;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                if (AnonymousClass56.this.val$migrate) {
                    UserConfig.migrateOffsetId = AnonymousClass56.this.val$offset;
                    UserConfig.saveConfig(false);
                    MessagesController.this.migratingDialogs = false;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                } else {
                    MessagesController.this.generateUpdateMessage();
                    if (!(z2 || AnonymousClass56.this.val$loadType != MessagesController.UPDATE_MASK_NAME || UserConfig.isRobot)) {
                        MessagesController.this.loadDialogs(0, AnonymousClass56.this.val$count, false);
                    }
                }
                MessagesController.this.migrateDialogs(UserConfig.migrateOffsetId, UserConfig.migrateOffsetDate, UserConfig.migrateOffsetUserId, UserConfig.migrateOffsetChatId, UserConfig.migrateOffsetChannelId, UserConfig.migrateOffsetAccess);
                if (!this.val$dialogsToReload.isEmpty()) {
                    MessagesController.this.reloadDialogsReadValue(this.val$dialogsToReload, 0);
                }
            }
        }

        AnonymousClass56(int i, messages_Dialogs com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs, boolean z, int i2, int i3, ArrayList arrayList, boolean z2) {
            this.val$loadType = i;
            this.val$dialogsRes = com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs;
            this.val$resetEnd = z;
            this.val$count = i2;
            this.val$offset = i3;
            this.val$encChats = arrayList;
            this.val$migrate = z2;
        }

        public void run() {
            FileLog.m16e("tmessages", "loaded loadType " + this.val$loadType + " count " + this.val$dialogsRes.dialogs.size());
            if (this.val$loadType == MessagesController.UPDATE_MASK_NAME && this.val$dialogsRes.dialogs.size() == 0) {
                AndroidUtilities.runOnUIThread(new C05191());
                return;
            }
            int i;
            Chat chat;
            int i2;
            Integer num;
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();
            AbstractMap hashMap3 = new HashMap();
            AbstractMap hashMap4 = new HashMap();
            for (i = 0; i < this.val$dialogsRes.users.size(); i += MessagesController.UPDATE_MASK_NAME) {
                User user = (User) this.val$dialogsRes.users.get(i);
                hashMap3.put(Integer.valueOf(user.id), user);
            }
            for (i = 0; i < this.val$dialogsRes.chats.size(); i += MessagesController.UPDATE_MASK_NAME) {
                Chat chat2 = (Chat) this.val$dialogsRes.chats.get(i);
                hashMap4.put(Integer.valueOf(chat2.id), chat2);
            }
            if (this.val$loadType == MessagesController.UPDATE_MASK_NAME) {
                MessagesController.this.nextDialogsCacheOffset = this.val$offset + this.val$count;
            }
            for (int i3 = 0; i3 < this.val$dialogsRes.messages.size(); i3 += MessagesController.UPDATE_MASK_NAME) {
                MessageObject messageObject;
                Message message = (Message) this.val$dialogsRes.messages.get(i3);
                if (message.to_id.channel_id != 0) {
                    chat = (Chat) hashMap4.get(Integer.valueOf(message.to_id.channel_id));
                    if (chat == null || !chat.left) {
                        if (chat != null && chat.megagroup) {
                            message.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                        }
                        if (!(this.val$loadType == MessagesController.UPDATE_MASK_NAME || !message.post || message.out)) {
                            message.media_unread = true;
                        }
                        messageObject = new MessageObject(message, hashMap3, hashMap4, false);
                        hashMap2.put(Long.valueOf(messageObject.getDialogId()), messageObject);
                    }
                } else {
                    if (message.to_id.chat_id != 0) {
                        chat = (Chat) hashMap4.get(Integer.valueOf(message.to_id.chat_id));
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    message.media_unread = true;
                    messageObject = new MessageObject(message, hashMap3, hashMap4, false);
                    hashMap2.put(Long.valueOf(messageObject.getDialogId()), messageObject);
                }
            }
            ArrayList arrayList = new ArrayList();
            for (i2 = 0; i2 < this.val$dialogsRes.dialogs.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                TL_dialog tL_dialog = (TL_dialog) this.val$dialogsRes.dialogs.get(i2);
                if (tL_dialog.id == 0 && tL_dialog.peer != null) {
                    if (tL_dialog.peer.user_id != 0) {
                        tL_dialog.id = (long) tL_dialog.peer.user_id;
                    } else if (tL_dialog.peer.chat_id != 0) {
                        tL_dialog.id = (long) (-tL_dialog.peer.chat_id);
                    } else if (tL_dialog.peer.channel_id != 0) {
                        tL_dialog.id = (long) (-tL_dialog.peer.channel_id);
                    }
                }
                if (tL_dialog.id != 0) {
                    if (tL_dialog.last_message_date == 0) {
                        messageObject = (MessageObject) hashMap2.get(Long.valueOf(tL_dialog.id));
                        if (messageObject != null) {
                            tL_dialog.last_message_date = messageObject.messageOwner.date;
                        }
                    }
                    Object obj = MessagesController.UPDATE_MASK_NAME;
                    if (DialogObject.isChannel(tL_dialog)) {
                        chat = (Chat) hashMap4.get(Integer.valueOf(-((int) tL_dialog.id)));
                        if (chat != null) {
                            if (!chat.megagroup) {
                                obj = null;
                            }
                            if (chat.left) {
                            }
                        }
                        MessagesController.this.channelsPts.put(Integer.valueOf(-((int) tL_dialog.id)), Integer.valueOf(tL_dialog.pts));
                    } else if (((int) tL_dialog.id) < 0) {
                        chat = (Chat) hashMap4.get(Integer.valueOf(-((int) tL_dialog.id)));
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    hashMap.put(Long.valueOf(tL_dialog.id), tL_dialog);
                    if (obj != null && this.val$loadType == MessagesController.UPDATE_MASK_NAME && ((tL_dialog.read_outbox_max_id == 0 || tL_dialog.read_inbox_max_id == 0) && tL_dialog.top_message != 0)) {
                        arrayList.add(tL_dialog);
                    }
                    num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_inbox_max_id)));
                    num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_outbox_max_id)));
                }
            }
            if (this.val$loadType != MessagesController.UPDATE_MASK_NAME) {
                ImageLoader.saveMessagesThumbs(this.val$dialogsRes.messages);
                for (i2 = 0; i2 < this.val$dialogsRes.messages.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                    message = (Message) this.val$dialogsRes.messages.get(i2);
                    if (message.action instanceof TL_messageActionChatDeleteUser) {
                        User user2 = (User) hashMap3.get(Integer.valueOf(message.action.user_id));
                        if (user2 != null && user2.bot) {
                            message.reply_markup = new TL_replyKeyboardHide();
                        }
                    }
                    if ((message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate)) {
                        message.unread = false;
                        message.media_unread = false;
                    } else {
                        ConcurrentHashMap concurrentHashMap = message.out ? MessagesController.this.dialogs_read_outbox_max : MessagesController.this.dialogs_read_inbox_max;
                        num = (Integer) concurrentHashMap.get(Long.valueOf(message.dialog_id));
                        if (num == null) {
                            num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message.out, message.dialog_id));
                            concurrentHashMap.put(Long.valueOf(message.dialog_id), num);
                        }
                        message.unread = num.intValue() < message.id;
                    }
                }
                MessagesStorage.getInstance().putDialogs(this.val$dialogsRes);
            }
            if (this.val$loadType == MessagesController.UPDATE_MASK_AVATAR) {
                chat2 = (Chat) this.val$dialogsRes.chats.get(0);
                MessagesController.this.getChannelDifference(chat2.id);
                MessagesController.this.checkChannelInviter(chat2.id);
            }
            AndroidUtilities.runOnUIThread(new C05202(hashMap, hashMap2, hashMap4, arrayList));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.57 */
    class AnonymousClass57 implements Runnable {
        final /* synthetic */ HashMap val$dialogsToUpdate;

        AnonymousClass57(HashMap hashMap) {
            this.val$dialogsToUpdate = hashMap;
        }

        public void run() {
            for (Entry entry : this.val$dialogsToUpdate.entrySet()) {
                TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(entry.getKey());
                if (tL_dialog != null) {
                    tL_dialog.unread_count = ((Integer) entry.getValue()).intValue();
                    if (MoboConstants.f1334a && (MoboConstants.f1344k & MessagesController.UPDATE_MASK_CHAT_MEMBERS) != 0) {
                        if (tL_dialog.unread_count == 0) {
                            MessagesController.getInstance().dialogsUnreadOnly.remove(tL_dialog);
                        } else if (!MessagesController.getInstance().dialogsUnreadOnly.contains(tL_dialog)) {
                            MessagesController.getInstance().dialogsUnreadOnly.add(tL_dialog);
                        }
                    }
                }
            }
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.updateInterfaces;
            Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
            objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
            instance.postNotificationName(i, objArr);
            NotificationsController.getInstance().processDialogsUpdateRead(this.val$dialogsToUpdate);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.58 */
    class AnonymousClass58 implements RequestDelegate {
        final /* synthetic */ TL_dialog val$dialog;
        final /* synthetic */ int val$lower_id;
        final /* synthetic */ long val$newTaskId;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.58.1 */
        class C05211 implements Runnable {
            C05211() {
            }

            public void run() {
                TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(AnonymousClass58.this.val$dialog.id));
                if (tL_dialog != null && tL_dialog.top_message == 0) {
                    MessagesController.this.deleteDialog(AnonymousClass58.this.val$dialog.id, 3);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.58.2 */
        class C05222 implements Runnable {
            C05222() {
            }

            public void run() {
                MessagesController.this.checkingLastMessagesDialogs.remove(Integer.valueOf(AnonymousClass58.this.val$lower_id));
            }
        }

        AnonymousClass58(TL_dialog tL_dialog, long j, int i) {
            this.val$dialog = tL_dialog;
            this.val$newTaskId = j;
            this.val$lower_id = i;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject != null) {
                messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                if (com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C05211());
                } else {
                    messages_Dialogs tL_messages_dialogs = new TL_messages_dialogs();
                    Message message = (Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(0);
                    TL_dialog tL_dialog = new TL_dialog();
                    tL_dialog.flags = this.val$dialog.flags;
                    tL_dialog.top_message = message.id;
                    tL_dialog.last_message_date = message.date;
                    tL_dialog.notify_settings = this.val$dialog.notify_settings;
                    tL_dialog.pts = this.val$dialog.pts;
                    tL_dialog.unread_count = this.val$dialog.unread_count;
                    tL_dialog.read_inbox_max_id = this.val$dialog.read_inbox_max_id;
                    tL_dialog.read_outbox_max_id = this.val$dialog.read_outbox_max_id;
                    long j = this.val$dialog.id;
                    tL_dialog.id = j;
                    message.dialog_id = j;
                    tL_messages_dialogs.users.addAll(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.users);
                    tL_messages_dialogs.chats.addAll(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.chats);
                    tL_messages_dialogs.dialogs.add(tL_dialog);
                    tL_messages_dialogs.messages.addAll(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages);
                    tL_messages_dialogs.count = MessagesController.UPDATE_MASK_NAME;
                    MessagesController.this.processDialogsUpdate(tL_messages_dialogs, null);
                    MessagesStorage.getInstance().putMessages(com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages, true, true, false, MediaController.m71a().m167c(), true);
                }
            }
            if (this.val$newTaskId != 0) {
                MessagesStorage.getInstance().removePendingTask(this.val$newTaskId);
            }
            AndroidUtilities.runOnUIThread(new C05222());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.59 */
    class AnonymousClass59 implements Runnable {
        final /* synthetic */ messages_Dialogs val$dialogsRes;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.59.1 */
        class C05231 implements Runnable {
            final /* synthetic */ HashMap val$dialogsToUpdate;
            final /* synthetic */ HashMap val$new_dialogMessage;
            final /* synthetic */ HashMap val$new_dialogs_dict;

            C05231(HashMap hashMap, HashMap hashMap2, HashMap hashMap3) {
                this.val$new_dialogs_dict = hashMap;
                this.val$new_dialogMessage = hashMap2;
                this.val$dialogsToUpdate = hashMap3;
            }

            public void run() {
                MessagesController.this.putUsers(AnonymousClass59.this.val$dialogsRes.users, true);
                MessagesController.this.putChats(AnonymousClass59.this.val$dialogsRes.chats, true);
                for (Entry entry : this.val$new_dialogs_dict.entrySet()) {
                    Long l = (Long) entry.getKey();
                    TL_dialog tL_dialog = (TL_dialog) entry.getValue();
                    TL_dialog tL_dialog2 = (TL_dialog) MessagesController.this.dialogs_dict.get(l);
                    if (tL_dialog2 == null) {
                        MessagesController messagesController = MessagesController.this;
                        messagesController.nextDialogsCacheOffset += MessagesController.UPDATE_MASK_NAME;
                        MessagesController.this.dialogs_dict.put(l, tL_dialog);
                        MessageObject messageObject = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(tL_dialog.id));
                        MessagesController.this.dialogMessage.put(l, messageObject);
                        if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                            MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject.getId()), messageObject);
                            if (messageObject.messageOwner.random_id != 0) {
                                MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject.messageOwner.random_id), messageObject);
                            }
                        }
                    } else {
                        tL_dialog2.unread_count = tL_dialog.unread_count;
                        MessageObject messageObject2 = (MessageObject) MessagesController.this.dialogMessage.get(l);
                        MessageObject messageObject3;
                        if (messageObject2 != null && tL_dialog2.top_message <= 0) {
                            messageObject3 = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(tL_dialog.id));
                            if (messageObject2.deleted || messageObject3 == null || messageObject3.messageOwner.date > messageObject2.messageOwner.date) {
                                MessagesController.this.dialogs_dict.put(l, tL_dialog);
                                MessagesController.this.dialogMessage.put(l, messageObject3);
                                if (messageObject3 != null && messageObject3.messageOwner.to_id.channel_id == 0) {
                                    MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject3.getId()), messageObject3);
                                    if (messageObject3.messageOwner.random_id != 0) {
                                        MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject3.messageOwner.random_id), messageObject3);
                                    }
                                }
                                MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(messageObject2.getId()));
                                if (messageObject2.messageOwner.random_id != 0) {
                                    MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(messageObject2.messageOwner.random_id));
                                }
                            }
                        } else if ((messageObject2 != null && messageObject2.deleted) || tL_dialog.top_message > tL_dialog2.top_message) {
                            MessagesController.this.dialogs_dict.put(l, tL_dialog);
                            messageObject3 = (MessageObject) this.val$new_dialogMessage.get(Long.valueOf(tL_dialog.id));
                            MessagesController.this.dialogMessage.put(l, messageObject3);
                            if (messageObject3 != null && messageObject3.messageOwner.to_id.channel_id == 0) {
                                MessagesController.this.dialogMessagesByIds.put(Integer.valueOf(messageObject3.getId()), messageObject3);
                                if (messageObject3.messageOwner.random_id != 0) {
                                    MessagesController.this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject3.messageOwner.random_id), messageObject3);
                                }
                            }
                            if (messageObject2 != null) {
                                MessagesController.this.dialogMessagesByIds.remove(Integer.valueOf(messageObject2.getId()));
                                if (messageObject2.messageOwner.random_id != 0) {
                                    MessagesController.this.dialogMessagesByRandomIds.remove(Long.valueOf(messageObject2.messageOwner.random_id));
                                }
                            }
                            if (messageObject3 == null) {
                                MessagesController.this.checkLastDialogMessage(tL_dialog, null, 0);
                            }
                        }
                    }
                }
                MessagesController.this.dialogs.clear();
                MessagesController.this.dialogsServerOnly.clear();
                MessagesController.this.dialogsGroupsOnly.clear();
                MessagesController.this.dialogsUnreadOnly.clear();
                MessagesController.this.dialogsJustGroupsOnly.clear();
                MessagesController.this.dialogsSuperGroupsOnly.clear();
                MessagesController.this.dialogsChannelOnly.clear();
                MessagesController.this.dialogsContactOnly.clear();
                MessagesController.this.dialogsFavoriteOnly.clear();
                MessagesController.this.dialogsHiddenOnly.clear();
                MessagesController.this.dialogsBotOnly.clear();
                MessagesController.this.dialogs.addAll(MessagesController.this.dialogs_dict.values());
                MessagesController.this.sortDialogs(null);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                NotificationsController.getInstance().processDialogsUpdateRead(this.val$dialogsToUpdate);
            }
        }

        AnonymousClass59(messages_Dialogs com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs) {
            this.val$dialogsRes = com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs;
        }

        public void run() {
            int i;
            int i2;
            Chat chat;
            MessageObject messageObject;
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();
            AbstractMap hashMap3 = new HashMap();
            AbstractMap hashMap4 = new HashMap();
            HashMap hashMap5 = new HashMap();
            for (i = 0; i < this.val$dialogsRes.users.size(); i += MessagesController.UPDATE_MASK_NAME) {
                User user = (User) this.val$dialogsRes.users.get(i);
                hashMap3.put(Integer.valueOf(user.id), user);
            }
            for (i = 0; i < this.val$dialogsRes.chats.size(); i += MessagesController.UPDATE_MASK_NAME) {
                Chat chat2 = (Chat) this.val$dialogsRes.chats.get(i);
                hashMap4.put(Integer.valueOf(chat2.id), chat2);
            }
            for (i2 = 0; i2 < this.val$dialogsRes.messages.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                Message message = (Message) this.val$dialogsRes.messages.get(i2);
                if (message.to_id.channel_id != 0) {
                    chat = (Chat) hashMap4.get(Integer.valueOf(message.to_id.channel_id));
                    if (chat != null && chat.left) {
                    }
                    messageObject = new MessageObject(message, hashMap3, hashMap4, false);
                    hashMap2.put(Long.valueOf(messageObject.getDialogId()), messageObject);
                } else {
                    if (message.to_id.chat_id != 0) {
                        chat = (Chat) hashMap4.get(Integer.valueOf(message.to_id.chat_id));
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    messageObject = new MessageObject(message, hashMap3, hashMap4, false);
                    hashMap2.put(Long.valueOf(messageObject.getDialogId()), messageObject);
                }
            }
            for (i2 = 0; i2 < this.val$dialogsRes.dialogs.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                TL_dialog tL_dialog = (TL_dialog) this.val$dialogsRes.dialogs.get(i2);
                if (tL_dialog.id == 0) {
                    if (tL_dialog.peer.user_id != 0) {
                        tL_dialog.id = (long) tL_dialog.peer.user_id;
                    } else if (tL_dialog.peer.chat_id != 0) {
                        tL_dialog.id = (long) (-tL_dialog.peer.chat_id);
                    } else if (tL_dialog.peer.channel_id != 0) {
                        tL_dialog.id = (long) (-tL_dialog.peer.channel_id);
                    }
                }
                Integer num;
                if (DialogObject.isChannel(tL_dialog)) {
                    chat = (Chat) hashMap4.get(Integer.valueOf(-((int) tL_dialog.id)));
                    if (chat != null && chat.left) {
                    }
                    if (tL_dialog.last_message_date == 0) {
                        messageObject = (MessageObject) hashMap2.get(Long.valueOf(tL_dialog.id));
                        if (messageObject != null) {
                            tL_dialog.last_message_date = messageObject.messageOwner.date;
                        }
                    }
                    hashMap.put(Long.valueOf(tL_dialog.id), tL_dialog);
                    hashMap5.put(Long.valueOf(tL_dialog.id), Integer.valueOf(tL_dialog.unread_count));
                    num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_inbox_max_id)));
                    num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_outbox_max_id)));
                } else {
                    if (((int) tL_dialog.id) < 0) {
                        chat = (Chat) hashMap4.get(Integer.valueOf(-((int) tL_dialog.id)));
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    if (tL_dialog.last_message_date == 0) {
                        messageObject = (MessageObject) hashMap2.get(Long.valueOf(tL_dialog.id));
                        if (messageObject != null) {
                            tL_dialog.last_message_date = messageObject.messageOwner.date;
                        }
                    }
                    hashMap.put(Long.valueOf(tL_dialog.id), tL_dialog);
                    hashMap5.put(Long.valueOf(tL_dialog.id), Integer.valueOf(tL_dialog.unread_count));
                    num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_inbox_max_id)));
                    num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(num.intValue(), tL_dialog.read_outbox_max_id)));
                }
            }
            AndroidUtilities.runOnUIThread(new C05231(hashMap, hashMap2, hashMap5));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.5 */
    class C05245 implements Runnable {
        C05245() {
        }

        public void run() {
            MessagesController.this.updatesQueueSeq.clear();
            MessagesController.this.updatesQueuePts.clear();
            MessagesController.this.updatesQueueQts.clear();
            MessagesController.this.gettingUnknownChannels.clear();
            MessagesController.this.updatesStartWaitTimeSeq = 0;
            MessagesController.this.updatesStartWaitTimePts = 0;
            MessagesController.this.updatesStartWaitTimeQts = 0;
            MessagesController.this.createdDialogIds.clear();
            MessagesController.this.gettingDifference = false;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.60 */
    class AnonymousClass60 implements Runnable {
        final /* synthetic */ Message val$message;

        AnonymousClass60(Message message) {
            this.val$message = message;
        }

        public void run() {
            SparseArray access$5000 = MessagesController.this.channelViewsToSend;
            int i = this.val$message.to_id.channel_id != 0 ? -this.val$message.to_id.channel_id : this.val$message.to_id.chat_id != 0 ? -this.val$message.to_id.chat_id : this.val$message.to_id.user_id;
            ArrayList arrayList = (ArrayList) access$5000.get(i);
            if (arrayList == null) {
                arrayList = new ArrayList();
                access$5000.put(i, arrayList);
            }
            if (!arrayList.contains(Integer.valueOf(this.val$message.id))) {
                arrayList.add(Integer.valueOf(this.val$message.id));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.62 */
    class AnonymousClass62 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$max_positive_id;
        final /* synthetic */ boolean val$popup;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.62.1 */
        class C05251 implements Runnable {
            C05251() {
            }

            public void run() {
                TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(AnonymousClass62.this.val$dialog_id));
                if (tL_dialog != null) {
                    tL_dialog.unread_count = 0;
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.updateInterfaces;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                    instance.postNotificationName(i, objArr);
                }
                if (AnonymousClass62.this.val$popup) {
                    NotificationsController.getInstance().processReadMessages(null, AnonymousClass62.this.val$dialog_id, 0, AnonymousClass62.this.val$max_positive_id, true);
                    HashMap hashMap = new HashMap();
                    hashMap.put(Long.valueOf(AnonymousClass62.this.val$dialog_id), Integer.valueOf(-1));
                    NotificationsController.getInstance().processDialogsUpdateRead(hashMap);
                    return;
                }
                NotificationsController.getInstance().processReadMessages(null, AnonymousClass62.this.val$dialog_id, 0, AnonymousClass62.this.val$max_positive_id, false);
                hashMap = new HashMap();
                hashMap.put(Long.valueOf(AnonymousClass62.this.val$dialog_id), Integer.valueOf(0));
                NotificationsController.getInstance().processDialogsUpdateRead(hashMap);
            }
        }

        AnonymousClass62(long j, boolean z, int i) {
            this.val$dialog_id = j;
            this.val$popup = z;
            this.val$max_positive_id = i;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05251());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.65 */
    class AnonymousClass65 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$max_date;
        final /* synthetic */ boolean val$popup;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.65.1 */
        class C05261 implements Runnable {
            C05261() {
            }

            public void run() {
                NotificationsController.getInstance().processReadMessages(null, AnonymousClass65.this.val$dialog_id, AnonymousClass65.this.val$max_date, 0, AnonymousClass65.this.val$popup);
                TL_dialog tL_dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(Long.valueOf(AnonymousClass65.this.val$dialog_id));
                if (tL_dialog != null) {
                    tL_dialog.unread_count = 0;
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.updateInterfaces;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                    instance.postNotificationName(i, objArr);
                }
                HashMap hashMap = new HashMap();
                hashMap.put(Long.valueOf(AnonymousClass65.this.val$dialog_id), Integer.valueOf(0));
                NotificationsController.getInstance().processDialogsUpdateRead(hashMap);
            }
        }

        AnonymousClass65(long j, int i, boolean z) {
            this.val$dialog_id = j;
            this.val$max_date = i;
            this.val$popup = z;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05261());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.66 */
    class AnonymousClass66 implements RequestDelegate {
        final /* synthetic */ BaseFragment val$fragment;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.66.1 */
        class C05271 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C05271(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                    AlertsCreator.showFloodWaitAlert(this.val$error.text, AnonymousClass66.this.val$fragment);
                } else {
                    AlertsCreator.showAddUserAlert(this.val$error.text, AnonymousClass66.this.val$fragment, false);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.66.2 */
        class C05282 implements Runnable {
            final /* synthetic */ Updates val$updates;

            C05282(Updates updates) {
                this.val$updates = updates;
            }

            public void run() {
                MessagesController.this.putUsers(this.val$updates.users, false);
                MessagesController.this.putChats(this.val$updates.chats, false);
                if (this.val$updates.chats == null || this.val$updates.chats.isEmpty()) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                    return;
                }
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.chatDidCreated;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(((Chat) this.val$updates.chats.get(0)).id);
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass66(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error != null) {
                AndroidUtilities.runOnUIThread(new C05271(tL_error));
                return;
            }
            Updates updates = (Updates) tLObject;
            MessagesController.this.processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new C05282(updates));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.67 */
    class AnonymousClass67 implements RequestDelegate {
        final /* synthetic */ BaseFragment val$fragment;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.67.1 */
        class C05291 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C05291(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                    AlertsCreator.showFloodWaitAlert(this.val$error.text, AnonymousClass67.this.val$fragment);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.67.2 */
        class C05302 implements Runnable {
            final /* synthetic */ Updates val$updates;

            C05302(Updates updates) {
                this.val$updates = updates;
            }

            public void run() {
                MessagesController.this.putUsers(this.val$updates.users, false);
                MessagesController.this.putChats(this.val$updates.chats, false);
                if (this.val$updates.chats == null || this.val$updates.chats.isEmpty()) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                    return;
                }
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.chatDidCreated;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(((Chat) this.val$updates.chats.get(0)).id);
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass67(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error != null) {
                AndroidUtilities.runOnUIThread(new C05291(tL_error));
                return;
            }
            Updates updates = (Updates) tLObject;
            MessagesController.this.processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new C05302(updates));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.68 */
    class AnonymousClass68 implements RequestDelegate {
        final /* synthetic */ Context val$context;
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.68.1 */
        class C05311 implements Runnable {
            C05311() {
            }

            public void run() {
                if (!((Activity) AnonymousClass68.this.val$context).isFinishing()) {
                    try {
                        AnonymousClass68.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.68.2 */
        class C05322 implements Runnable {
            C05322() {
            }

            public void run() {
                if (!((Activity) AnonymousClass68.this.val$context).isFinishing()) {
                    try {
                        AnonymousClass68.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                    Builder builder = new Builder(AnonymousClass68.this.val$context);
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setMessage(LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                    builder.show().setCanceledOnTouchOutside(true);
                }
            }
        }

        AnonymousClass68(Context context, ProgressDialog progressDialog) {
            this.val$context = context;
            this.val$progressDialog = progressDialog;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                AndroidUtilities.runOnUIThread(new C05311());
                Updates updates = (Updates) tLObject;
                MessagesController.this.processUpdates((Updates) tLObject, false);
                return;
            }
            AndroidUtilities.runOnUIThread(new C05322());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.69 */
    class AnonymousClass69 implements OnClickListener {
        final /* synthetic */ int val$reqId;

        AnonymousClass69(int i) {
            this.val$reqId = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
            try {
                dialogInterface.dismiss();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.6 */
    class C05336 implements Runnable {
        C05336() {
        }

        public void run() {
            ConnectionsManager.getInstance().setIsUpdating(false);
            MessagesController.this.updatesQueueChannels.clear();
            MessagesController.this.updatesStartWaitTimeChannels.clear();
            MessagesController.this.gettingDifferenceChannels.clear();
            MessagesController.this.channelsPts.clear();
            MessagesController.this.shortPollChannels.clear();
            MessagesController.this.needShortPollChannels.clear();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.70 */
    class AnonymousClass70 implements RequestDelegate {
        final /* synthetic */ BaseFragment val$fragment;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.70.1 */
        class C05341 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C05341(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                if (AnonymousClass70.this.val$fragment != null) {
                    AlertsCreator.showAddUserAlert(this.val$error.text, AnonymousClass70.this.val$fragment, true);
                } else if (this.val$error.text.equals("PEER_FLOOD")) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.needShowAlert;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_NAME);
                    instance.postNotificationName(i, objArr);
                }
            }
        }

        AnonymousClass70(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error != null) {
                AndroidUtilities.runOnUIThread(new C05341(tL_error));
            } else {
                MessagesController.this.processUpdates((Updates) tLObject, false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.73 */
    class AnonymousClass73 implements RequestDelegate {
        final /* synthetic */ String val$about;
        final /* synthetic */ ChatFull val$info;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.73.1 */
        class C05361 implements Runnable {
            C05361() {
            }

            public void run() {
                AnonymousClass73.this.val$info.about = AnonymousClass73.this.val$about;
                MessagesStorage.getInstance().updateChatInfo(AnonymousClass73.this.val$info, false);
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.chatInfoDidLoaded;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_STATUS];
                objArr[0] = AnonymousClass73.this.val$info;
                objArr[MessagesController.UPDATE_MASK_NAME] = Integer.valueOf(0);
                objArr[MessagesController.UPDATE_MASK_AVATAR] = Boolean.valueOf(false);
                objArr[3] = null;
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass73(ChatFull chatFull, String str) {
            this.val$info = chatFull;
            this.val$about = str;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject instanceof TL_boolTrue) {
                AndroidUtilities.runOnUIThread(new C05361());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.74 */
    class AnonymousClass74 implements RequestDelegate {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ String val$userName;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.74.1 */
        class C05371 implements Runnable {
            C05371() {
            }

            public void run() {
                Chat chat = MessagesController.this.getChat(Integer.valueOf(AnonymousClass74.this.val$chat_id));
                if (AnonymousClass74.this.val$userName.length() != 0) {
                    chat.flags |= MessagesController.UPDATE_MASK_USER_PRINT;
                } else {
                    chat.flags &= -65;
                }
                chat.username = AnonymousClass74.this.val$userName;
                ArrayList arrayList = new ArrayList();
                arrayList.add(chat);
                MessagesStorage.getInstance().putUsersAndChats(null, arrayList, true, true);
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL);
                instance.postNotificationName(i, objArr);
            }
        }

        AnonymousClass74(int i, String str) {
            this.val$chat_id = i;
            this.val$userName = str;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject instanceof TL_boolTrue) {
                AndroidUtilities.runOnUIThread(new C05371());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.76 */
    class AnonymousClass76 implements RequestDelegate {
        final /* synthetic */ int val$chat_id;

        AnonymousClass76(int i) {
            this.val$chat_id = i;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                MessagesController.this.processUpdates((Updates) tLObject, false);
                MessagesController.this.loadFullChat(this.val$chat_id, 0, true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.78 */
    class AnonymousClass78 implements RequestDelegate {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ InputUser val$inputUser;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ boolean val$isMegagroup;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.78.1 */
        class C05381 implements Runnable {
            C05381() {
            }

            public void run() {
                MessagesController.this.joiningToChannels.remove(Integer.valueOf(AnonymousClass78.this.val$chat_id));
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.78.2 */
        class C05392 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C05392(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                boolean z = true;
                if (AnonymousClass78.this.val$fragment != null) {
                    String str = this.val$error.text;
                    BaseFragment baseFragment = AnonymousClass78.this.val$fragment;
                    if (!AnonymousClass78.this.val$isChannel || AnonymousClass78.this.val$isMegagroup) {
                        z = false;
                    }
                    AlertsCreator.showAddUserAlert(str, baseFragment, z);
                } else if (this.val$error.text.equals("PEER_FLOOD")) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.needShowAlert;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_NAME);
                    instance.postNotificationName(i, objArr);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.78.3 */
        class C05403 implements Runnable {
            C05403() {
            }

            public void run() {
                MessagesController.this.loadFullChat(AnonymousClass78.this.val$chat_id, 0, true);
            }
        }

        AnonymousClass78(boolean z, InputUser inputUser, int i, BaseFragment baseFragment, boolean z2) {
            this.val$isChannel = z;
            this.val$inputUser = inputUser;
            this.val$chat_id = i;
            this.val$fragment = baseFragment;
            this.val$isMegagroup = z2;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (this.val$isChannel && (this.val$inputUser instanceof TL_inputUserSelf)) {
                AndroidUtilities.runOnUIThread(new C05381());
            }
            if (tL_error != null) {
                AndroidUtilities.runOnUIThread(new C05392(tL_error));
                return;
            }
            boolean z;
            Updates updates = (Updates) tLObject;
            for (int i = 0; i < updates.updates.size(); i += MessagesController.UPDATE_MASK_NAME) {
                Update update = (Update) updates.updates.get(i);
                if ((update instanceof TL_updateNewChannelMessage) && (((TL_updateNewChannelMessage) update).message.action instanceof TL_messageActionChatAddUser)) {
                    z = true;
                    break;
                }
            }
            z = false;
            MessagesController.this.processUpdates(updates, false);
            if (this.val$isChannel) {
                if (!z && (this.val$inputUser instanceof TL_inputUserSelf)) {
                    MessagesController.this.generateJoinMessage(this.val$chat_id, true);
                }
                AndroidUtilities.runOnUIThread(new C05403(), 1000);
            }
            if (this.val$isChannel && (this.val$inputUser instanceof TL_inputUserSelf)) {
                MessagesStorage.getInstance().updateDialogsWithDeletedMessages(new ArrayList(), true, this.val$chat_id);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.79 */
    class AnonymousClass79 implements RequestDelegate {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ boolean val$deleteDialog;
        final /* synthetic */ InputUser val$inputUser;
        final /* synthetic */ boolean val$isChannel;
        final /* synthetic */ User val$user;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.79.1 */
        class C05411 implements Runnable {
            C05411() {
            }

            public void run() {
                if (AnonymousClass79.this.val$deleteDialog) {
                    MessagesController.this.deleteDialog((long) (-AnonymousClass79.this.val$chat_id), 0);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.79.2 */
        class C05422 implements Runnable {
            C05422() {
            }

            public void run() {
                MessagesController.this.loadFullChat(AnonymousClass79.this.val$chat_id, 0, true);
            }
        }

        AnonymousClass79(User user, boolean z, int i, boolean z2, InputUser inputUser) {
            this.val$user = user;
            this.val$deleteDialog = z;
            this.val$chat_id = i;
            this.val$isChannel = z2;
            this.val$inputUser = inputUser;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (this.val$user.id == UserConfig.getClientUserId()) {
                AndroidUtilities.runOnUIThread(new C05411());
            }
            if (tL_error == null) {
                MessagesController.this.processUpdates((Updates) tLObject, false);
                if (this.val$isChannel && !(this.val$inputUser instanceof TL_inputUserSelf)) {
                    AndroidUtilities.runOnUIThread(new C05422(), 1000);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.7 */
    class C05437 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ boolean val$set;

        C05437(boolean z, long j) {
            this.val$set = z;
            this.val$dialog_id = j;
        }

        public void run() {
            if (this.val$set) {
                MessagesController.this.createdDialogIds.add(Long.valueOf(this.val$dialog_id));
            } else {
                MessagesController.this.createdDialogIds.remove(Long.valueOf(this.val$dialog_id));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.85 */
    class AnonymousClass85 implements RequestDelegate {
        final /* synthetic */ String val$regid;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.85.1 */
        class C05441 implements Runnable {
            C05441() {
            }

            public void run() {
                MessagesController.this.registeringForPush = false;
            }
        }

        AnonymousClass85(String str) {
            this.val$regid = str;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject instanceof TL_boolTrue) {
                FileLog.m16e("tmessages", "registered for push");
                UserConfig.registeredForPush = true;
                UserConfig.pushString = this.val$regid;
                UserConfig.saveConfig(false);
            }
            AndroidUtilities.runOnUIThread(new C05441());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.8 */
    class C05458 implements Runnable {
        C05458() {
        }

        public void run() {
            NotificationCenter instance = NotificationCenter.getInstance();
            int i = NotificationCenter.updateInterfaces;
            Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
            objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_STATUS);
            instance.postNotificationName(i, objArr);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.91 */
    class AnonymousClass91 implements RequestDelegate {
        final /* synthetic */ Chat val$channel;
        final /* synthetic */ long val$newTaskId;

        AnonymousClass91(long j, Chat chat) {
            this.val$newTaskId = j;
            this.val$channel = chat;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject != null) {
                TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
                if (!(tL_messages_peerDialogs.dialogs.isEmpty() || tL_messages_peerDialogs.chats.isEmpty())) {
                    messages_Dialogs tL_messages_dialogs = new TL_messages_dialogs();
                    tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
                    tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
                    tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
                    tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
                    MessagesController.this.processLoadedDialogs(tL_messages_dialogs, null, 0, MessagesController.UPDATE_MASK_NAME, MessagesController.UPDATE_MASK_AVATAR, false, false);
                }
            }
            if (this.val$newTaskId != 0) {
                MessagesStorage.getInstance().removePendingTask(this.val$newTaskId);
            }
            MessagesController.this.gettingUnknownChannels.remove(Integer.valueOf(this.val$channel.id));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.92 */
    class AnonymousClass92 implements Runnable {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ boolean val$stop;

        AnonymousClass92(boolean z, int i) {
            this.val$stop = z;
            this.val$channelId = i;
        }

        public void run() {
            if (this.val$stop) {
                MessagesController.this.needShortPollChannels.delete(this.val$channelId);
                return;
            }
            MessagesController.this.needShortPollChannels.put(this.val$channelId, 0);
            if (MessagesController.this.shortPollChannels.indexOfKey(this.val$channelId) < 0) {
                MessagesController.this.getChannelDifference(this.val$channelId, MessagesController.UPDATE_MASK_AVATAR, 0);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.93 */
    class AnonymousClass93 implements RequestDelegate {
        final /* synthetic */ int val$channelId;
        final /* synthetic */ int val$newDialogType;
        final /* synthetic */ long val$newTaskId;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.93.1 */
        class C05461 implements Runnable {
            final /* synthetic */ updates_ChannelDifference val$res;

            C05461(updates_ChannelDifference com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference) {
                this.val$res = com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference;
            }

            public void run() {
                MessagesController.this.putUsers(this.val$res.users, false);
                MessagesController.this.putChats(this.val$res.chats, false);
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.93.2 */
        class C05522 implements Runnable {
            final /* synthetic */ Chat val$channelFinal;
            final /* synthetic */ ArrayList val$msgUpdates;
            final /* synthetic */ updates_ChannelDifference val$res;
            final /* synthetic */ HashMap val$usersDict;

            /* renamed from: com.hanista.mobogram.messenger.MessagesController.93.2.1 */
            class C05471 implements Runnable {
                final /* synthetic */ HashMap val$corrected;

                C05471(HashMap hashMap) {
                    this.val$corrected = hashMap;
                }

                public void run() {
                    for (Entry entry : this.val$corrected.entrySet()) {
                        Integer num = (Integer) entry.getKey();
                        long[] jArr = (long[]) entry.getValue();
                        Integer valueOf = Integer.valueOf((int) jArr[MessagesController.UPDATE_MASK_NAME]);
                        SendMessagesHelper.getInstance().processSentMessage(valueOf.intValue());
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i = NotificationCenter.messageReceivedByServer;
                        Object[] objArr = new Object[MessagesController.UPDATE_MASK_STATUS];
                        objArr[0] = valueOf;
                        objArr[MessagesController.UPDATE_MASK_NAME] = num;
                        objArr[MessagesController.UPDATE_MASK_AVATAR] = null;
                        objArr[3] = Long.valueOf(jArr[0]);
                        instance.postNotificationName(i, objArr);
                    }
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.MessagesController.93.2.2 */
            class C05512 implements Runnable {

                /* renamed from: com.hanista.mobogram.messenger.MessagesController.93.2.2.1 */
                class C05481 implements Runnable {
                    final /* synthetic */ HashMap val$messages;

                    C05481(HashMap hashMap) {
                        this.val$messages = hashMap;
                    }

                    public void run() {
                        for (Entry entry : this.val$messages.entrySet()) {
                            Long l = (Long) entry.getKey();
                            MessagesController.this.updateInterfaceWithMessages(l.longValue(), (ArrayList) entry.getValue());
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    }
                }

                /* renamed from: com.hanista.mobogram.messenger.MessagesController.93.2.2.2 */
                class C05502 implements Runnable {
                    final /* synthetic */ ArrayList val$pushMessages;

                    /* renamed from: com.hanista.mobogram.messenger.MessagesController.93.2.2.2.1 */
                    class C05491 implements Runnable {
                        C05491() {
                        }

                        public void run() {
                            NotificationsController.getInstance().processNewMessages(C05502.this.val$pushMessages, true);
                        }
                    }

                    C05502(ArrayList arrayList) {
                        this.val$pushMessages = arrayList;
                    }

                    public void run() {
                        if (!this.val$pushMessages.isEmpty()) {
                            AndroidUtilities.runOnUIThread(new C05491());
                        }
                        MessagesStorage.getInstance().putMessages(C05522.this.val$res.new_messages, true, false, false, MediaController.m71a().m167c());
                    }
                }

                C05512() {
                }

                public void run() {
                    Integer num;
                    Integer num2;
                    Integer num3;
                    int i;
                    Message message;
                    boolean z;
                    if ((C05522.this.val$res instanceof TL_updates_channelDifference) || (C05522.this.val$res instanceof TL_updates_channelDifferenceEmpty)) {
                        if (!C05522.this.val$res.new_messages.isEmpty()) {
                            HashMap hashMap = new HashMap();
                            ImageLoader.saveMessagesThumbs(C05522.this.val$res.new_messages);
                            ArrayList arrayList = new ArrayList();
                            long j = (long) (-AnonymousClass93.this.val$channelId);
                            num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j));
                            if (num == null) {
                                num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, j));
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j), num);
                            }
                            num2 = num;
                            num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j));
                            if (num == null) {
                                num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, j));
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j), num);
                            }
                            num3 = num;
                            for (i = 0; i < C05522.this.val$res.new_messages.size(); i += MessagesController.UPDATE_MASK_NAME) {
                                MessageObject messageObject;
                                long j2;
                                ArrayList arrayList2;
                                message = (Message) C05522.this.val$res.new_messages.get(i);
                                if (C05522.this.val$channelFinal == null || !C05522.this.val$channelFinal.left) {
                                    if ((message.out ? num3 : num2).intValue() < message.id && !(message.action instanceof TL_messageActionChannelCreate)) {
                                        z = true;
                                        message.unread = z;
                                        if (C05522.this.val$channelFinal != null && C05522.this.val$channelFinal.megagroup) {
                                            message.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                                        }
                                        messageObject = new MessageObject(message, C05522.this.val$usersDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(j)));
                                        if (!messageObject.isOut() && messageObject.isUnread()) {
                                            arrayList.add(messageObject);
                                        }
                                        j2 = (long) (-AnonymousClass93.this.val$channelId);
                                        arrayList2 = (ArrayList) hashMap.get(Long.valueOf(j2));
                                        if (arrayList2 == null) {
                                            arrayList2 = new ArrayList();
                                            hashMap.put(Long.valueOf(j2), arrayList2);
                                        }
                                        arrayList2.add(messageObject);
                                    }
                                }
                                z = false;
                                message.unread = z;
                                message.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                                messageObject = new MessageObject(message, C05522.this.val$usersDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(j)));
                                arrayList.add(messageObject);
                                j2 = (long) (-AnonymousClass93.this.val$channelId);
                                arrayList2 = (ArrayList) hashMap.get(Long.valueOf(j2));
                                if (arrayList2 == null) {
                                    arrayList2 = new ArrayList();
                                    hashMap.put(Long.valueOf(j2), arrayList2);
                                }
                                arrayList2.add(messageObject);
                            }
                            AndroidUtilities.runOnUIThread(new C05481(hashMap));
                            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C05502(arrayList));
                        }
                        if (!C05522.this.val$res.other_updates.isEmpty()) {
                            MessagesController.this.processUpdateArray(C05522.this.val$res.other_updates, C05522.this.val$res.users, C05522.this.val$res.chats, true);
                        }
                        MessagesController.this.processChannelsUpdatesQueue(AnonymousClass93.this.val$channelId, MessagesController.UPDATE_MASK_NAME);
                        MessagesStorage.getInstance().saveChannelPts(AnonymousClass93.this.val$channelId, C05522.this.val$res.pts);
                    } else if (C05522.this.val$res instanceof TL_updates_channelDifferenceTooLong) {
                        long j3 = (long) (-AnonymousClass93.this.val$channelId);
                        num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j3));
                        if (num == null) {
                            num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, j3));
                            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j3), num);
                        }
                        num2 = num;
                        num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j3));
                        if (num == null) {
                            num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, j3));
                            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j3), num);
                        }
                        num3 = num;
                        for (i = 0; i < C05522.this.val$res.messages.size(); i += MessagesController.UPDATE_MASK_NAME) {
                            message = (Message) C05522.this.val$res.messages.get(i);
                            message.dialog_id = (long) (-AnonymousClass93.this.val$channelId);
                            if (!(message.action instanceof TL_messageActionChannelCreate) && (C05522.this.val$channelFinal == null || !C05522.this.val$channelFinal.left)) {
                                if ((message.out ? num3 : num2).intValue() < message.id) {
                                    z = true;
                                    message.unread = z;
                                    if (C05522.this.val$channelFinal != null && C05522.this.val$channelFinal.megagroup) {
                                        message.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                                    }
                                }
                            }
                            z = false;
                            message.unread = z;
                            message.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                        }
                        MessagesStorage.getInstance().overwriteChannel(AnonymousClass93.this.val$channelId, (TL_updates_channelDifferenceTooLong) C05522.this.val$res, AnonymousClass93.this.val$newDialogType);
                    }
                    MessagesController.this.gettingDifferenceChannels.remove(Integer.valueOf(AnonymousClass93.this.val$channelId));
                    MessagesController.this.channelsPts.put(Integer.valueOf(AnonymousClass93.this.val$channelId), Integer.valueOf(C05522.this.val$res.pts));
                    if ((C05522.this.val$res.flags & MessagesController.UPDATE_MASK_AVATAR) != 0) {
                        MessagesController.this.shortPollChannels.put(AnonymousClass93.this.val$channelId, ((int) (System.currentTimeMillis() / 1000)) + C05522.this.val$res.timeout);
                    }
                    if (!C05522.this.val$res.isFinal) {
                        MessagesController.this.getChannelDifference(AnonymousClass93.this.val$channelId);
                    }
                    FileLog.m16e("tmessages", "received channel difference with pts = " + C05522.this.val$res.pts + " channelId = " + AnonymousClass93.this.val$channelId);
                    FileLog.m16e("tmessages", "new_messages = " + C05522.this.val$res.new_messages.size() + " messages = " + C05522.this.val$res.messages.size() + " users = " + C05522.this.val$res.users.size() + " chats = " + C05522.this.val$res.chats.size() + " other updates = " + C05522.this.val$res.other_updates.size());
                    if (AnonymousClass93.this.val$newTaskId != 0) {
                        MessagesStorage.getInstance().removePendingTask(AnonymousClass93.this.val$newTaskId);
                    }
                }
            }

            C05522(ArrayList arrayList, updates_ChannelDifference com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference, Chat chat, HashMap hashMap) {
                this.val$msgUpdates = arrayList;
                this.val$res = com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference;
                this.val$channelFinal = chat;
                this.val$usersDict = hashMap;
            }

            public void run() {
                if (!this.val$msgUpdates.isEmpty()) {
                    HashMap hashMap = new HashMap();
                    Iterator it = this.val$msgUpdates.iterator();
                    while (it.hasNext()) {
                        TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) it.next();
                        Object updateMessageStateAndId = MessagesStorage.getInstance().updateMessageStateAndId(tL_updateMessageID.random_id, null, tL_updateMessageID.id, 0, false, AnonymousClass93.this.val$channelId);
                        if (updateMessageStateAndId != null) {
                            hashMap.put(Integer.valueOf(tL_updateMessageID.id), updateMessageStateAndId);
                        }
                    }
                    if (!hashMap.isEmpty()) {
                        AndroidUtilities.runOnUIThread(new C05471(hashMap));
                    }
                }
                Utilities.stageQueue.postRunnable(new C05512());
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.93.3 */
        class C05533 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C05533(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                MessagesController.this.checkChannelError(this.val$error.text, AnonymousClass93.this.val$channelId);
            }
        }

        AnonymousClass93(int i, int i2, long j) {
            this.val$channelId = i;
            this.val$newDialogType = i2;
            this.val$newTaskId = j;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            int i = 0;
            if (tL_error == null) {
                int i2;
                updates_ChannelDifference com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference = (updates_ChannelDifference) tLObject;
                HashMap hashMap = new HashMap();
                for (i2 = 0; i2 < com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference.users.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                    User user = (User) com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference.users.get(i2);
                    hashMap.put(Integer.valueOf(user.id), user);
                }
                Chat chat = null;
                for (i2 = 0; i2 < com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference.chats.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                    Chat chat2 = (Chat) com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference.chats.get(i2);
                    if (chat2.id == this.val$channelId) {
                        chat = chat2;
                        break;
                    }
                }
                ArrayList arrayList = new ArrayList();
                if (!com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference.other_updates.isEmpty()) {
                    while (i < com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference.other_updates.size()) {
                        Update update = (Update) com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference.other_updates.get(i);
                        if (update instanceof TL_updateMessageID) {
                            arrayList.add((TL_updateMessageID) update);
                            com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference.other_updates.remove(i);
                            i--;
                        }
                        i += MessagesController.UPDATE_MASK_NAME;
                    }
                }
                MessagesStorage.getInstance().putUsersAndChats(com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference.users, com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference.chats, true, true);
                AndroidUtilities.runOnUIThread(new C05461(com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference));
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C05522(arrayList, com_hanista_mobogram_tgnet_TLRPC_updates_ChannelDifference, chat, hashMap));
                return;
            }
            AndroidUtilities.runOnUIThread(new C05533(tL_error));
            MessagesController.this.gettingDifferenceChannels.remove(Integer.valueOf(this.val$channelId));
            if (this.val$newTaskId != 0) {
                MessagesStorage.getInstance().removePendingTask(this.val$newTaskId);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.95 */
    class AnonymousClass95 implements Runnable {
        final /* synthetic */ ArrayList val$pushMessages;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.95.1 */
        class C05611 implements Runnable {
            C05611() {
            }

            public void run() {
                NotificationsController.getInstance().processNewMessages(AnonymousClass95.this.val$pushMessages, true);
            }
        }

        AnonymousClass95(ArrayList arrayList) {
            this.val$pushMessages = arrayList;
        }

        public void run() {
            AndroidUtilities.runOnUIThread(new C05611());
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.96 */
    class AnonymousClass96 implements Runnable {
        final /* synthetic */ int val$chat_id;
        final /* synthetic */ ArrayList val$pushMessages;

        AnonymousClass96(int i, ArrayList arrayList) {
            this.val$chat_id = i;
            this.val$pushMessages = arrayList;
        }

        public void run() {
            MessagesController.this.updateInterfaceWithMessages((long) (-this.val$chat_id), this.val$pushMessages);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.97 */
    class AnonymousClass97 implements Runnable {
        final /* synthetic */ int val$chat_id;

        /* renamed from: com.hanista.mobogram.messenger.MessagesController.97.1 */
        class C05661 implements RequestDelegate {
            final /* synthetic */ Chat val$chat;

            /* renamed from: com.hanista.mobogram.messenger.MessagesController.97.1.1 */
            class C05621 implements Runnable {
                final /* synthetic */ TL_channels_channelParticipant val$res;

                C05621(TL_channels_channelParticipant tL_channels_channelParticipant) {
                    this.val$res = tL_channels_channelParticipant;
                }

                public void run() {
                    MessagesController.this.putUsers(this.val$res.users, false);
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.MessagesController.97.1.2 */
            class C05642 implements Runnable {
                final /* synthetic */ ArrayList val$pushMessages;

                /* renamed from: com.hanista.mobogram.messenger.MessagesController.97.1.2.1 */
                class C05631 implements Runnable {
                    C05631() {
                    }

                    public void run() {
                        NotificationsController.getInstance().processNewMessages(C05642.this.val$pushMessages, true);
                    }
                }

                C05642(ArrayList arrayList) {
                    this.val$pushMessages = arrayList;
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C05631());
                }
            }

            /* renamed from: com.hanista.mobogram.messenger.MessagesController.97.1.3 */
            class C05653 implements Runnable {
                final /* synthetic */ ArrayList val$pushMessages;

                C05653(ArrayList arrayList) {
                    this.val$pushMessages = arrayList;
                }

                public void run() {
                    MessagesController.this.updateInterfaceWithMessages((long) (-AnonymousClass97.this.val$chat_id), this.val$pushMessages);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }

            C05661(Chat chat) {
                this.val$chat = chat;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                TL_channels_channelParticipant tL_channels_channelParticipant = (TL_channels_channelParticipant) tLObject;
                if (tL_channels_channelParticipant != null && (tL_channels_channelParticipant.participant instanceof TL_channelParticipantSelf) && tL_channels_channelParticipant.participant.inviter_id != UserConfig.getClientUserId()) {
                    if (!this.val$chat.megagroup || !MessagesStorage.getInstance().isMigratedChat(this.val$chat.id)) {
                        AndroidUtilities.runOnUIThread(new C05621(tL_channels_channelParticipant));
                        MessagesStorage.getInstance().putUsersAndChats(tL_channels_channelParticipant.users, null, true, true);
                        Message tL_messageService = new TL_messageService();
                        tL_messageService.media_unread = true;
                        tL_messageService.unread = true;
                        tL_messageService.flags = MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE;
                        tL_messageService.post = true;
                        if (this.val$chat.megagroup) {
                            tL_messageService.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                        }
                        int newMessageId = UserConfig.getNewMessageId();
                        tL_messageService.id = newMessageId;
                        tL_messageService.local_id = newMessageId;
                        tL_messageService.date = tL_channels_channelParticipant.participant.date;
                        tL_messageService.action = new TL_messageActionChatAddUser();
                        tL_messageService.from_id = tL_channels_channelParticipant.participant.inviter_id;
                        tL_messageService.action.users.add(Integer.valueOf(UserConfig.getClientUserId()));
                        tL_messageService.to_id = new TL_peerChannel();
                        tL_messageService.to_id.channel_id = AnonymousClass97.this.val$chat_id;
                        tL_messageService.dialog_id = (long) (-AnonymousClass97.this.val$chat_id);
                        UserConfig.saveConfig(false);
                        ArrayList arrayList = new ArrayList();
                        ArrayList arrayList2 = new ArrayList();
                        AbstractMap concurrentHashMap = new ConcurrentHashMap();
                        for (int i = 0; i < tL_channels_channelParticipant.users.size(); i += MessagesController.UPDATE_MASK_NAME) {
                            User user = (User) tL_channels_channelParticipant.users.get(i);
                            concurrentHashMap.put(Integer.valueOf(user.id), user);
                        }
                        arrayList2.add(tL_messageService);
                        arrayList.add(new MessageObject(tL_messageService, concurrentHashMap, true));
                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C05642(arrayList));
                        MessagesStorage.getInstance().putMessages(arrayList2, true, true, false, MediaController.m71a().m167c());
                        AndroidUtilities.runOnUIThread(new C05653(arrayList));
                    }
                }
            }
        }

        AnonymousClass97(int i) {
            this.val$chat_id = i;
        }

        public void run() {
            Chat chat = MessagesController.this.getChat(Integer.valueOf(this.val$chat_id));
            if (chat != null && ChatObject.isChannel(this.val$chat_id) && !chat.creator) {
                TLObject tL_channels_getParticipant = new TL_channels_getParticipant();
                tL_channels_getParticipant.channel = MessagesController.getInputChannel(this.val$chat_id);
                tL_channels_getParticipant.user_id = new TL_inputUserSelf();
                ConnectionsManager.getInstance().sendRequest(tL_channels_getParticipant, new C05661(chat));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.98 */
    class AnonymousClass98 implements Runnable {
        final /* synthetic */ ArrayList val$objArr;
        final /* synthetic */ boolean val$printUpdate;
        final /* synthetic */ int val$user_id;

        AnonymousClass98(boolean z, int i, ArrayList arrayList) {
            this.val$printUpdate = z;
            this.val$user_id = i;
            this.val$objArr = arrayList;
        }

        public void run() {
            if (this.val$printUpdate) {
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_USER_PRINT);
                instance.postNotificationName(i, objArr);
            }
            MessagesController.this.updateInterfaceWithMessages((long) this.val$user_id, this.val$objArr);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.99 */
    class AnonymousClass99 implements Runnable {
        final /* synthetic */ ArrayList val$objArr;
        final /* synthetic */ boolean val$printUpdate;
        final /* synthetic */ Updates val$updates;

        AnonymousClass99(boolean z, Updates updates, ArrayList arrayList) {
            this.val$printUpdate = z;
            this.val$updates = updates;
            this.val$objArr = arrayList;
        }

        public void run() {
            if (this.val$printUpdate) {
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_USER_PRINT);
                instance.postNotificationName(i, objArr);
            }
            MessagesController.this.updateInterfaceWithMessages((long) (-this.val$updates.chat_id), this.val$objArr);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.MessagesController.9 */
    class C05679 implements RequestDelegate {
        C05679() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject != null) {
                TL_messages_peerDialogs tL_messages_peerDialogs = (TL_messages_peerDialogs) tLObject;
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < tL_messages_peerDialogs.dialogs.size(); i += MessagesController.UPDATE_MASK_NAME) {
                    TL_dialog tL_dialog = (TL_dialog) tL_messages_peerDialogs.dialogs.get(i);
                    if (tL_dialog.read_inbox_max_id == 0) {
                        tL_dialog.read_inbox_max_id = MessagesController.UPDATE_MASK_NAME;
                    }
                    if (tL_dialog.read_outbox_max_id == 0) {
                        tL_dialog.read_outbox_max_id = MessagesController.UPDATE_MASK_NAME;
                    }
                    if (tL_dialog.id == 0 && tL_dialog.peer != null) {
                        if (tL_dialog.peer.user_id != 0) {
                            tL_dialog.id = (long) tL_dialog.peer.user_id;
                        } else if (tL_dialog.peer.chat_id != 0) {
                            tL_dialog.id = (long) (-tL_dialog.peer.chat_id);
                        } else if (tL_dialog.peer.channel_id != 0) {
                            tL_dialog.id = (long) (-tL_dialog.peer.channel_id);
                        }
                    }
                    Integer num = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(tL_dialog.read_inbox_max_id, num.intValue())));
                    if (num.intValue() == 0) {
                        if (tL_dialog.peer.channel_id != 0) {
                            TL_updateReadChannelInbox tL_updateReadChannelInbox = new TL_updateReadChannelInbox();
                            tL_updateReadChannelInbox.channel_id = tL_dialog.peer.channel_id;
                            tL_updateReadChannelInbox.max_id = tL_dialog.read_inbox_max_id;
                            arrayList.add(tL_updateReadChannelInbox);
                        } else {
                            TL_updateReadHistoryInbox tL_updateReadHistoryInbox = new TL_updateReadHistoryInbox();
                            tL_updateReadHistoryInbox.peer = tL_dialog.peer;
                            tL_updateReadHistoryInbox.max_id = tL_dialog.read_inbox_max_id;
                            arrayList.add(tL_updateReadHistoryInbox);
                        }
                    }
                    num = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(tL_dialog.id));
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(tL_dialog.id), Integer.valueOf(Math.max(tL_dialog.read_outbox_max_id, num.intValue())));
                    if (num.intValue() == 0) {
                        if (tL_dialog.peer.channel_id != 0) {
                            TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TL_updateReadChannelOutbox();
                            tL_updateReadChannelOutbox.channel_id = tL_dialog.peer.channel_id;
                            tL_updateReadChannelOutbox.max_id = tL_dialog.read_outbox_max_id;
                            arrayList.add(tL_updateReadChannelOutbox);
                        } else {
                            TL_updateReadHistoryOutbox tL_updateReadHistoryOutbox = new TL_updateReadHistoryOutbox();
                            tL_updateReadHistoryOutbox.peer = tL_dialog.peer;
                            tL_updateReadHistoryOutbox.max_id = tL_dialog.read_outbox_max_id;
                            arrayList.add(tL_updateReadHistoryOutbox);
                        }
                    }
                }
                if (!arrayList.isEmpty()) {
                    MessagesController.this.processUpdateArray(arrayList, null, null, false);
                }
            }
        }
    }

    public static class PrintingUser {
        public SendMessageAction action;
        public long lastTime;
        public int userId;
    }

    private class UserActionUpdatesPts extends Updates {
        private UserActionUpdatesPts() {
        }
    }

    private class UserActionUpdatesSeq extends Updates {
        private UserActionUpdatesSeq() {
        }
    }

    static {
        Instance = null;
    }

    public MessagesController() {
        this.chats = new ConcurrentHashMap(100, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, UPDATE_MASK_AVATAR);
        this.encryptedChats = new ConcurrentHashMap(10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, UPDATE_MASK_AVATAR);
        this.users = new ConcurrentHashMap(100, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, UPDATE_MASK_AVATAR);
        this.usersByUsernames = new ConcurrentHashMap(100, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, UPDATE_MASK_AVATAR);
        this.joiningToChannels = new ArrayList();
        this.exportedChats = new HashMap();
        this.dialogs = new ArrayList();
        this.dialogsServerOnly = new ArrayList();
        this.dialogsGroupsOnly = new ArrayList();
        this.dialogsUnreadOnly = new ArrayList();
        this.dialogsJustGroupsOnly = new ArrayList();
        this.dialogsSuperGroupsOnly = new ArrayList();
        this.dialogsChannelOnly = new ArrayList();
        this.dialogsContactOnly = new ArrayList();
        this.dialogsFavoriteOnly = new ArrayList();
        this.dialogsHiddenOnly = new ArrayList();
        this.dialogsBotOnly = new ArrayList();
        this.dialogs_read_inbox_max = new ConcurrentHashMap(100, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, UPDATE_MASK_AVATAR);
        this.dialogs_read_outbox_max = new ConcurrentHashMap(100, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, UPDATE_MASK_AVATAR);
        this.dialogs_dict = new ConcurrentHashMap(100, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, UPDATE_MASK_AVATAR);
        this.dialogMessage = new HashMap();
        this.dialogMessagesByRandomIds = new HashMap();
        this.dialogMessagesByIds = new HashMap();
        this.printingUsers = new ConcurrentHashMap(20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, UPDATE_MASK_AVATAR);
        this.printingStrings = new HashMap();
        this.printingStringsTypes = new HashMap();
        this.sendingTypings = new HashMap();
        this.onlinePrivacy = new ConcurrentHashMap(20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, UPDATE_MASK_AVATAR);
        this.lastPrintingStringCount = 0;
        this.loadingPeerSettings = new HashMap();
        this.createdDialogIds = new ArrayList();
        this.shortPollChannels = new SparseIntArray();
        this.needShortPollChannels = new SparseIntArray();
        this.loadingBlockedUsers = false;
        this.blockedUsers = new ArrayList();
        this.channelViewsToSend = new SparseArray();
        this.channelViewsToReload = new SparseArray();
        this.updatesQueueChannels = new HashMap();
        this.updatesStartWaitTimeChannels = new HashMap();
        this.channelsPts = new HashMap();
        this.gettingDifferenceChannels = new HashMap();
        this.gettingUnknownChannels = new HashMap();
        this.checkingLastMessagesDialogs = new HashMap();
        this.updatesQueueSeq = new ArrayList();
        this.updatesQueuePts = new ArrayList();
        this.updatesQueueQts = new ArrayList();
        this.updatesStartWaitTimeSeq = 0;
        this.updatesStartWaitTimePts = 0;
        this.updatesStartWaitTimeQts = 0;
        this.fullUsersAbout = new HashMap();
        this.loadingFullUsers = new ArrayList();
        this.loadedFullUsers = new ArrayList();
        this.loadingFullChats = new ArrayList();
        this.loadingFullParticipants = new ArrayList();
        this.loadedFullParticipants = new ArrayList();
        this.loadedFullChats = new ArrayList();
        this.reloadingWebpages = new HashMap();
        this.reloadingWebpagesPending = new HashMap();
        this.reloadingMessages = new HashMap();
        this.gettingNewDeleteTask = false;
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeleteTaskRunnable = null;
        this.loadingDialogs = false;
        this.migratingDialogs = false;
        this.dialogsEndReached = false;
        this.gettingDifference = false;
        this.updatingState = false;
        this.firstGettingTask = false;
        this.registeringForPush = false;
        this.secretWebpagePreview = UPDATE_MASK_AVATAR;
        this.lastStatusUpdateTime = 0;
        this.statusRequest = 0;
        this.statusSettingState = 0;
        this.offlineSent = false;
        this.uploadingAvatar = null;
        this.enableJoined = true;
        this.allowBigEmoji = false;
        this.useSystemEmoji = false;
        this.fontSize = AndroidUtilities.dp(16.0f);
        this.maxGroupCount = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        this.maxBroadcastCount = 100;
        this.maxMegagroupCount = Factory.DEFAULT_MIN_REBUFFER_MS;
        this.minGroupConvertSize = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        this.maxEditTime = 172800;
        this.maxRecentStickersCount = 30;
        this.maxRecentGifsCount = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        this.disabledFeatures = new ArrayList();
        this.dialogComparator = new C05001();
        this.updatesComparator = new C05042();
        ImageLoader.getInstance();
        MessagesStorage.getInstance();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
        addSupportUser();
        this.enableJoined = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnableContactJoined", true);
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        this.secretWebpagePreview = sharedPreferences.getInt("secretWebpage2", UPDATE_MASK_AVATAR);
        this.maxGroupCount = sharedPreferences.getInt("maxGroupCount", Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        this.maxMegagroupCount = sharedPreferences.getInt("maxMegagroupCount", PointerIconCompat.TYPE_DEFAULT);
        this.maxRecentGifsCount = sharedPreferences.getInt("maxRecentGifsCount", Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        this.maxRecentStickersCount = sharedPreferences.getInt("maxRecentStickersCount", 30);
        this.maxEditTime = sharedPreferences.getInt("maxEditTime", 3600);
        this.groupBigSize = sharedPreferences.getInt("groupBigSize", 10);
        this.ratingDecay = sharedPreferences.getInt("ratingDecay", 2419200);
        this.fontSize = sharedPreferences.getInt("fons_size", AndroidUtilities.isTablet() ? 18 : UPDATE_MASK_CHAT_NAME);
        if (ThemeUtil.m2490b()) {
            this.fontSize = AdvanceTheme.bm;
        }
        this.allowBigEmoji = sharedPreferences.getBoolean("allowBigEmoji", false);
        this.useSystemEmoji = sharedPreferences.getBoolean("useSystemEmoji", false);
        String string = sharedPreferences.getString("disabledFeatures", null);
        if (string != null && string.length() != 0) {
            try {
                byte[] decode = Base64.decode(string, 0);
                if (decode != null) {
                    AbstractSerializedData serializedData = new SerializedData(decode);
                    int readInt32 = serializedData.readInt32(false);
                    for (int i = 0; i < readInt32; i += UPDATE_MASK_NAME) {
                        TL_disabledFeature TLdeserialize = TL_disabledFeature.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                        if (!(TLdeserialize == null || TLdeserialize.feature == null || TLdeserialize.description == null)) {
                            this.disabledFeatures.add(TLdeserialize);
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    private void applyDialogNotificationsSettings(long j, PeerNotifySettings peerNotifySettings) {
        int i = UPDATE_MASK_NAME;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        int i2 = sharedPreferences.getInt("notify2_" + j, 0);
        int i3 = sharedPreferences.getInt("notifyuntil_" + j, 0);
        Editor edit = sharedPreferences.edit();
        TL_dialog tL_dialog = (TL_dialog) this.dialogs_dict.get(Long.valueOf(j));
        if (tL_dialog != null) {
            tL_dialog.notify_settings = peerNotifySettings;
        }
        edit.putBoolean("silent_" + j, peerNotifySettings.silent);
        if (peerNotifySettings.mute_until > ConnectionsManager.getInstance().getCurrentTime()) {
            int i4;
            if (peerNotifySettings.mute_until > ConnectionsManager.getInstance().getCurrentTime() + 31536000) {
                if (i2 != UPDATE_MASK_AVATAR) {
                    edit.putInt("notify2_" + j, UPDATE_MASK_AVATAR);
                    if (tL_dialog != null) {
                        tL_dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        i4 = 0;
                        i2 = UPDATE_MASK_NAME;
                    } else {
                        i4 = 0;
                        i2 = UPDATE_MASK_NAME;
                    }
                }
                i4 = 0;
                i2 = 0;
            } else {
                if (!(i2 == 3 && i3 == peerNotifySettings.mute_until)) {
                    i2 = peerNotifySettings.mute_until;
                    edit.putInt("notify2_" + j, 3);
                    edit.putInt("notifyuntil_" + j, peerNotifySettings.mute_until);
                    if (tL_dialog != null) {
                        tL_dialog.notify_settings.mute_until = i2;
                    }
                    i4 = i2;
                    i2 = UPDATE_MASK_NAME;
                }
                i4 = 0;
                i2 = 0;
            }
            MessagesStorage.getInstance().setDialogFlags(j, (((long) i4) << UPDATE_MASK_CHAT_MEMBERS) | 1);
            NotificationsController.getInstance().removeNotificationsForDialog(j);
        } else {
            if (i2 == 0 || i2 == UPDATE_MASK_NAME) {
                i = 0;
            } else {
                if (tL_dialog != null) {
                    tL_dialog.notify_settings.mute_until = 0;
                }
                edit.remove("notify2_" + j);
            }
            MessagesStorage.getInstance().setDialogFlags(j, 0);
            i2 = i;
        }
        edit.commit();
        if (i2 != 0) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
    }

    private void applyDialogsNotificationsSettings(ArrayList<TL_dialog> arrayList) {
        Editor editor = null;
        for (int i = 0; i < arrayList.size(); i += UPDATE_MASK_NAME) {
            TL_dialog tL_dialog = (TL_dialog) arrayList.get(i);
            if (tL_dialog.peer != null && (tL_dialog.notify_settings instanceof TL_peerNotifySettings)) {
                if (editor == null) {
                    editor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                }
                int i2 = tL_dialog.peer.user_id != 0 ? tL_dialog.peer.user_id : tL_dialog.peer.chat_id != 0 ? -tL_dialog.peer.chat_id : -tL_dialog.peer.channel_id;
                editor.putBoolean("silent_" + i2, tL_dialog.notify_settings.silent);
                if (tL_dialog.notify_settings.mute_until == 0) {
                    editor.remove("notify2_" + i2);
                } else if (tL_dialog.notify_settings.mute_until > ConnectionsManager.getInstance().getCurrentTime() + 31536000) {
                    editor.putInt("notify2_" + i2, UPDATE_MASK_AVATAR);
                    tL_dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                } else {
                    editor.putInt("notify2_" + i2, 3);
                    editor.putInt("notifyuntil_" + i2, tL_dialog.notify_settings.mute_until);
                }
            }
        }
        if (editor != null) {
            editor.commit();
        }
    }

    public static boolean checkCanOpenChat(Bundle bundle, BaseFragment baseFragment) {
        String str = null;
        if (bundle == null || baseFragment == null) {
            return true;
        }
        User user;
        Chat chat;
        int i = bundle.getInt("user_id", 0);
        int i2 = bundle.getInt("chat_id", 0);
        if (i != 0) {
            user = getInstance().getUser(Integer.valueOf(i));
            chat = null;
        } else if (i2 != 0) {
            chat = getInstance().getChat(Integer.valueOf(i2));
            user = null;
        } else {
            chat = null;
            user = null;
        }
        if (user == null && chat == null) {
            return true;
        }
        if (chat != null) {
            str = getRestrictionReason(chat.restriction_reason);
        } else if (user != null) {
            str = getRestrictionReason(user.restriction_reason);
        }
        if (str == null) {
            return true;
        }
        showCantOpenAlert(baseFragment, str);
        return false;
    }

    private void checkChannelError(String str, int i) {
        int i2 = -1;
        switch (str.hashCode()) {
            case -1809401834:
                if (str.equals("USER_BANNED_IN_CHANNEL")) {
                    i2 = UPDATE_MASK_AVATAR;
                    break;
                }
                break;
            case -795226617:
                if (str.equals("CHANNEL_PRIVATE")) {
                    i2 = 0;
                    break;
                }
                break;
            case -471086771:
                if (str.equals("CHANNEL_PUBLIC_GROUP_NA")) {
                    i2 = UPDATE_MASK_NAME;
                    break;
                }
                break;
        }
        NotificationCenter instance;
        int i3;
        Object[] objArr;
        switch (i2) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                instance = NotificationCenter.getInstance();
                i3 = NotificationCenter.chatInfoCantLoad;
                objArr = new Object[UPDATE_MASK_AVATAR];
                objArr[0] = Integer.valueOf(i);
                objArr[UPDATE_MASK_NAME] = Integer.valueOf(0);
                instance.postNotificationName(i3, objArr);
            case UPDATE_MASK_NAME /*1*/:
                instance = NotificationCenter.getInstance();
                i3 = NotificationCenter.chatInfoCantLoad;
                objArr = new Object[UPDATE_MASK_AVATAR];
                objArr[0] = Integer.valueOf(i);
                objArr[UPDATE_MASK_NAME] = Integer.valueOf(UPDATE_MASK_NAME);
                instance.postNotificationName(i3, objArr);
            case UPDATE_MASK_AVATAR /*2*/:
                instance = NotificationCenter.getInstance();
                i3 = NotificationCenter.chatInfoCantLoad;
                Object[] objArr2 = new Object[UPDATE_MASK_AVATAR];
                objArr2[0] = Integer.valueOf(i);
                objArr2[UPDATE_MASK_NAME] = Integer.valueOf(UPDATE_MASK_AVATAR);
                instance.postNotificationName(i3, objArr2);
            default:
        }
    }

    private boolean checkDeletingTask(boolean z) {
        int currentTime = ConnectionsManager.getInstance().getCurrentTime();
        if (this.currentDeletingTaskMids == null) {
            return false;
        }
        if (!z && (this.currentDeletingTaskTime == 0 || this.currentDeletingTaskTime > currentTime)) {
            return false;
        }
        this.currentDeletingTaskTime = 0;
        if (!(this.currentDeleteTaskRunnable == null || z)) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
        }
        this.currentDeleteTaskRunnable = null;
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: com.hanista.mobogram.messenger.MessagesController.20.1 */
            class C05011 implements Runnable {
                C05011() {
                }

                public void run() {
                    MessagesController.this.getNewDeleteTask(MessagesController.this.currentDeletingTaskMids);
                    MessagesController.this.currentDeletingTaskTime = 0;
                    MessagesController.this.currentDeletingTaskMids = null;
                }
            }

            public void run() {
                MessagesController.this.deleteMessages(MessagesController.this.currentDeletingTaskMids, null, null, 0);
                Utilities.stageQueue.postRunnable(new C05011());
            }
        });
        return true;
    }

    private void deleteDialog(long j, boolean z, int i, int i2) {
        if (j == ((long) UserConfig.getClientUserId())) {
            ArchiveUtil.m260a();
        }
        int i3 = (int) j;
        int i4 = (int) (j >> UPDATE_MASK_CHAT_MEMBERS);
        if (i == UPDATE_MASK_AVATAR) {
            MessagesStorage.getInstance().deleteDialog(j, i);
            return;
        }
        if (i == 0 || i == 3) {
            AndroidUtilities.uninstallShortcut(j);
        }
        if (z) {
            MessagesStorage.getInstance().deleteDialog(j, i);
            TL_dialog tL_dialog = (TL_dialog) this.dialogs_dict.get(Long.valueOf(j));
            if (tL_dialog != null) {
                if (i2 == 0) {
                    i2 = Math.max(0, tL_dialog.top_message);
                }
                if (i == 0 || i == 3) {
                    this.dialogs.remove(tL_dialog);
                    if (this.dialogsServerOnly.remove(tL_dialog) && DialogObject.isChannel(tL_dialog)) {
                        Utilities.stageQueue.postRunnable(new AnonymousClass35(j));
                    }
                    this.dialogsUnreadOnly.remove(tL_dialog);
                    this.dialogsGroupsOnly.remove(tL_dialog);
                    this.dialogsJustGroupsOnly.remove(tL_dialog);
                    this.dialogsSuperGroupsOnly.remove(tL_dialog);
                    this.dialogsChannelOnly.remove(tL_dialog);
                    this.dialogsContactOnly.remove(tL_dialog);
                    this.dialogsFavoriteOnly.remove(tL_dialog);
                    this.dialogsHiddenOnly.remove(tL_dialog);
                    HiddenConfig.m1397b(tL_dialog);
                    this.dialogsBotOnly.remove(tL_dialog);
                    this.dialogs_dict.remove(Long.valueOf(j));
                    this.dialogs_read_inbox_max.remove(Long.valueOf(j));
                    this.dialogs_read_outbox_max.remove(Long.valueOf(j));
                    this.nextDialogsCacheOffset--;
                } else {
                    tL_dialog.unread_count = 0;
                }
                MessageObject messageObject = (MessageObject) this.dialogMessage.remove(Long.valueOf(tL_dialog.id));
                int id;
                if (messageObject != null) {
                    id = messageObject.getId();
                    this.dialogMessagesByIds.remove(Integer.valueOf(messageObject.getId()));
                } else {
                    id = tL_dialog.top_message;
                    messageObject = (MessageObject) this.dialogMessagesByIds.remove(Integer.valueOf(tL_dialog.top_message));
                }
                if (!(messageObject == null || messageObject.messageOwner.random_id == 0)) {
                    this.dialogMessagesByRandomIds.remove(Long.valueOf(messageObject.messageOwner.random_id));
                }
                if (i != UPDATE_MASK_NAME || i3 == 0 || r2 <= 0) {
                    tL_dialog.top_message = 0;
                } else {
                    Message tL_messageService = new TL_messageService();
                    tL_messageService.id = tL_dialog.top_message;
                    tL_messageService.out = false;
                    tL_messageService.from_id = UserConfig.getClientUserId();
                    tL_messageService.flags |= UPDATE_MASK_READ_DIALOG_MESSAGE;
                    tL_messageService.action = new TL_messageActionHistoryClear();
                    tL_messageService.date = tL_dialog.last_message_date;
                    if (i3 > 0) {
                        tL_messageService.to_id = new TL_peerUser();
                        tL_messageService.to_id.user_id = i3;
                    } else if (ChatObject.isChannel(getChat(Integer.valueOf(-i3)))) {
                        tL_messageService.to_id = new TL_peerChannel();
                        tL_messageService.to_id.channel_id = -i3;
                    } else {
                        tL_messageService.to_id = new TL_peerChat();
                        tL_messageService.to_id.chat_id = -i3;
                    }
                    MessageObject messageObject2 = new MessageObject(tL_messageService, null, this.createdDialogIds.contains(Long.valueOf(tL_messageService.dialog_id)));
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(messageObject2);
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(tL_messageService);
                    updateInterfaceWithMessages(j, arrayList);
                    MessagesStorage.getInstance().putMessages(arrayList2, false, true, false, 0);
                }
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter instance = NotificationCenter.getInstance();
            int i5 = NotificationCenter.removeAllMessagesFromDialog;
            Object[] objArr = new Object[UPDATE_MASK_AVATAR];
            objArr[0] = Long.valueOf(j);
            objArr[UPDATE_MASK_NAME] = Boolean.valueOf(false);
            instance.postNotificationName(i5, objArr);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass36(j));
        }
        int i6 = i2;
        if (i4 != UPDATE_MASK_NAME && i != 3) {
            if (i3 != 0) {
                InputPeer inputPeer = getInputPeer(i3);
                if (inputPeer != null && !(inputPeer instanceof TL_inputPeerChannel)) {
                    TLObject tL_messages_deleteHistory = new TL_messages_deleteHistory();
                    tL_messages_deleteHistory.peer = inputPeer;
                    tL_messages_deleteHistory.max_id = i == 0 ? ConnectionsManager.DEFAULT_DATACENTER_ID : i6;
                    tL_messages_deleteHistory.just_clear = i != 0;
                    ConnectionsManager.getInstance().sendRequest(tL_messages_deleteHistory, new AnonymousClass37(j, i, i6), UPDATE_MASK_USER_PRINT);
                }
            } else if (i == UPDATE_MASK_NAME) {
                SecretChatHelper.getInstance().sendClearHistoryMessage(getEncryptedChat(Integer.valueOf(i4)), null);
            } else {
                SecretChatHelper.getInstance().declineSecretChat(i4);
            }
        }
    }

    private void getChannelDifference(int i) {
        getChannelDifference(i, 0, 0);
    }

    public static InputChannel getInputChannel(int i) {
        return getInputChannel(getInstance().getChat(Integer.valueOf(i)));
    }

    public static InputChannel getInputChannel(Chat chat) {
        if (!(chat instanceof TL_channel) && !(chat instanceof TL_channelForbidden)) {
            return new TL_inputChannelEmpty();
        }
        InputChannel tL_inputChannel = new TL_inputChannel();
        tL_inputChannel.channel_id = chat.id;
        tL_inputChannel.access_hash = chat.access_hash;
        return tL_inputChannel;
    }

    public static InputPeer getInputPeer(int i) {
        InputPeer tL_inputPeerChannel;
        if (i < 0) {
            Chat chat = getInstance().getChat(Integer.valueOf(-i));
            if (ChatObject.isChannel(chat)) {
                tL_inputPeerChannel = new TL_inputPeerChannel();
                tL_inputPeerChannel.channel_id = -i;
                tL_inputPeerChannel.access_hash = chat.access_hash;
                return tL_inputPeerChannel;
            }
            tL_inputPeerChannel = new TL_inputPeerChat();
            tL_inputPeerChannel.chat_id = -i;
            return tL_inputPeerChannel;
        }
        User user = getInstance().getUser(Integer.valueOf(i));
        tL_inputPeerChannel = new TL_inputPeerUser();
        tL_inputPeerChannel.user_id = i;
        if (user == null) {
            return tL_inputPeerChannel;
        }
        tL_inputPeerChannel.access_hash = user.access_hash;
        return tL_inputPeerChannel;
    }

    public static InputUser getInputUser(int i) {
        return getInputUser(getInstance().getUser(Integer.valueOf(i)));
    }

    public static InputUser getInputUser(User user) {
        if (user == null) {
            return new TL_inputUserEmpty();
        }
        if (user.id == UserConfig.getClientUserId()) {
            return new TL_inputUserSelf();
        }
        InputUser tL_inputUser = new TL_inputUser();
        tL_inputUser.user_id = user.id;
        tL_inputUser.access_hash = user.access_hash;
        return tL_inputUser;
    }

    public static MessagesController getInstance() {
        MessagesController messagesController = Instance;
        if (messagesController == null) {
            synchronized (MessagesController.class) {
                messagesController = Instance;
                if (messagesController == null) {
                    messagesController = new MessagesController();
                    Instance = messagesController;
                }
            }
        }
        return messagesController;
    }

    public static Peer getPeer(int i) {
        Peer tL_peerChannel;
        if (i < 0) {
            Chat chat = getInstance().getChat(Integer.valueOf(-i));
            if ((chat instanceof TL_channel) || (chat instanceof TL_channelForbidden)) {
                tL_peerChannel = new TL_peerChannel();
                tL_peerChannel.channel_id = -i;
                return tL_peerChannel;
            }
            tL_peerChannel = new TL_peerChat();
            tL_peerChannel.chat_id = -i;
            return tL_peerChannel;
        }
        getInstance().getUser(Integer.valueOf(i));
        tL_peerChannel = new TL_peerUser();
        tL_peerChannel.user_id = i;
        return tL_peerChannel;
    }

    private static String getRestrictionReason(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        int indexOf = str.indexOf(": ");
        if (indexOf <= 0) {
            return null;
        }
        String substring = str.substring(0, indexOf);
        return (substring.contains("-all") || substring.contains("-android")) ? str.substring(indexOf + UPDATE_MASK_AVATAR) : null;
    }

    private int getUpdateChannelId(Update update) {
        return update instanceof TL_updateNewChannelMessage ? ((TL_updateNewChannelMessage) update).message.to_id.channel_id : update instanceof TL_updateEditChannelMessage ? ((TL_updateEditChannelMessage) update).message.to_id.channel_id : update.channel_id;
    }

    private int getUpdateSeq(Updates updates) {
        return updates instanceof TL_updatesCombined ? updates.seq_start : updates.seq;
    }

    private int getUpdateType(Update update) {
        return ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateReadMessagesContents) || (update instanceof TL_updateReadHistoryInbox) || (update instanceof TL_updateReadHistoryOutbox) || (update instanceof TL_updateDeleteMessages) || (update instanceof TL_updateWebPage) || (update instanceof TL_updateEditMessage)) ? 0 : update instanceof TL_updateNewEncryptedMessage ? UPDATE_MASK_NAME : ((update instanceof TL_updateNewChannelMessage) || (update instanceof TL_updateDeleteChannelMessages) || (update instanceof TL_updateEditChannelMessage)) ? UPDATE_MASK_AVATAR : 3;
    }

    private String getUserNameForTyping(User user) {
        return user == null ? TtmlNode.ANONYMOUS_REGION_ID : (user.first_name == null || user.first_name.length() <= 0) ? (user.last_name == null || user.last_name.length() <= 0) ? TtmlNode.ANONYMOUS_REGION_ID : user.last_name : user.first_name;
    }

    public static boolean isFeatureEnabled(String str, BaseFragment baseFragment) {
        if (str == null || str.length() == 0 || getInstance().disabledFeatures.isEmpty() || baseFragment == null) {
            return true;
        }
        Iterator it = getInstance().disabledFeatures.iterator();
        while (it.hasNext()) {
            TL_disabledFeature tL_disabledFeature = (TL_disabledFeature) it.next();
            if (tL_disabledFeature.feature.equals(str)) {
                if (baseFragment.getParentActivity() != null) {
                    Builder builder = new Builder(baseFragment.getParentActivity());
                    builder.setTitle("Oops!");
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                    builder.setMessage(tL_disabledFeature.description);
                    baseFragment.showDialog(builder.create());
                }
                return false;
            }
        }
        return true;
    }

    private boolean isNotifySettingsMuted(PeerNotifySettings peerNotifySettings) {
        return (peerNotifySettings instanceof TL_peerNotifySettings) && peerNotifySettings.mute_until > ConnectionsManager.getInstance().getCurrentTime();
    }

    private int isValidUpdate(Updates updates, int i) {
        if (i != 0) {
            return i == UPDATE_MASK_NAME ? updates.pts <= MessagesStorage.lastPtsValue ? UPDATE_MASK_AVATAR : MessagesStorage.lastPtsValue + updates.pts_count == updates.pts ? 0 : UPDATE_MASK_NAME : i == UPDATE_MASK_AVATAR ? updates.pts <= MessagesStorage.lastQtsValue ? UPDATE_MASK_AVATAR : MessagesStorage.lastQtsValue + updates.updates.size() == updates.pts ? 0 : UPDATE_MASK_NAME : 0;
        } else {
            int updateSeq = getUpdateSeq(updates);
            return (MessagesStorage.lastSeqValue + UPDATE_MASK_NAME == updateSeq || MessagesStorage.lastSeqValue == updateSeq) ? 0 : MessagesStorage.lastSeqValue >= updateSeq ? UPDATE_MASK_AVATAR : UPDATE_MASK_NAME;
        }
    }

    private void migrateDialogs(int i, int i2, int i3, int i4, int i5, long j) {
        if (!this.migratingDialogs && i != -1) {
            if (UserConfig.isRobot) {
                FileLog.m16e("tmessages", "Abort loading");
                this.loadingDialogs = false;
                return;
            }
            this.migratingDialogs = true;
            TLObject tL_messages_getDialogs = new TL_messages_getDialogs();
            tL_messages_getDialogs.limit = 100;
            tL_messages_getDialogs.offset_id = i;
            tL_messages_getDialogs.offset_date = i2;
            if (i == 0) {
                tL_messages_getDialogs.offset_peer = new TL_inputPeerEmpty();
            } else {
                if (i5 != 0) {
                    tL_messages_getDialogs.offset_peer = new TL_inputPeerChannel();
                    tL_messages_getDialogs.offset_peer.channel_id = i5;
                } else if (i3 != 0) {
                    tL_messages_getDialogs.offset_peer = new TL_inputPeerUser();
                    tL_messages_getDialogs.offset_peer.user_id = i3;
                } else {
                    tL_messages_getDialogs.offset_peer = new TL_inputPeerChat();
                    tL_messages_getDialogs.offset_peer.chat_id = i4;
                }
                tL_messages_getDialogs.offset_peer.access_hash = j;
            }
            ConnectionsManager.getInstance().sendRequest(tL_messages_getDialogs, new RequestDelegate() {

                /* renamed from: com.hanista.mobogram.messenger.MessagesController.55.1 */
                class C05171 implements Runnable {
                    final /* synthetic */ messages_Dialogs val$dialogsRes;

                    /* renamed from: com.hanista.mobogram.messenger.MessagesController.55.1.1 */
                    class C05161 implements Runnable {
                        C05161() {
                        }

                        public void run() {
                            MessagesController.this.migratingDialogs = false;
                        }
                    }

                    C05171(messages_Dialogs com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs) {
                        this.val$dialogsRes = com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs;
                    }

                    public void run() {
                        Message message = null;
                        try {
                            int i;
                            Message message2;
                            int i2;
                            int i3;
                            TL_dialog tL_dialog;
                            int i4;
                            if (this.val$dialogsRes.dialogs.size() == 100) {
                                i = 0;
                                while (i < this.val$dialogsRes.messages.size()) {
                                    message2 = (Message) this.val$dialogsRes.messages.get(i);
                                    if (message != null && message2.date >= message.date) {
                                        message2 = message;
                                    }
                                    i += MessagesController.UPDATE_MASK_NAME;
                                    message = message2;
                                }
                                i2 = message.id;
                                UserConfig.migrateOffsetDate = message.date;
                                Chat chat;
                                if (message.to_id.channel_id != 0) {
                                    UserConfig.migrateOffsetChannelId = message.to_id.channel_id;
                                    UserConfig.migrateOffsetChatId = 0;
                                    UserConfig.migrateOffsetUserId = 0;
                                    for (i3 = 0; i3 < this.val$dialogsRes.chats.size(); i3 += MessagesController.UPDATE_MASK_NAME) {
                                        chat = (Chat) this.val$dialogsRes.chats.get(i3);
                                        if (chat.id == UserConfig.migrateOffsetChannelId) {
                                            UserConfig.migrateOffsetAccess = chat.access_hash;
                                            break;
                                        }
                                    }
                                } else if (message.to_id.chat_id != 0) {
                                    UserConfig.migrateOffsetChatId = message.to_id.chat_id;
                                    UserConfig.migrateOffsetChannelId = 0;
                                    UserConfig.migrateOffsetUserId = 0;
                                    for (i3 = 0; i3 < this.val$dialogsRes.chats.size(); i3 += MessagesController.UPDATE_MASK_NAME) {
                                        chat = (Chat) this.val$dialogsRes.chats.get(i3);
                                        if (chat.id == UserConfig.migrateOffsetChatId) {
                                            UserConfig.migrateOffsetAccess = chat.access_hash;
                                            break;
                                        }
                                    }
                                } else if (message.to_id.user_id != 0) {
                                    UserConfig.migrateOffsetUserId = message.to_id.user_id;
                                    UserConfig.migrateOffsetChatId = 0;
                                    UserConfig.migrateOffsetChannelId = 0;
                                    for (i3 = 0; i3 < this.val$dialogsRes.users.size(); i3 += MessagesController.UPDATE_MASK_NAME) {
                                        User user = (User) this.val$dialogsRes.users.get(i3);
                                        if (user.id == UserConfig.migrateOffsetUserId) {
                                            UserConfig.migrateOffsetAccess = user.access_hash;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                i2 = -1;
                            }
                            StringBuilder stringBuilder = new StringBuilder(this.val$dialogsRes.dialogs.size() * 12);
                            HashMap hashMap = new HashMap();
                            for (i3 = 0; i3 < this.val$dialogsRes.dialogs.size(); i3 += MessagesController.UPDATE_MASK_NAME) {
                                tL_dialog = (TL_dialog) this.val$dialogsRes.dialogs.get(i3);
                                if (tL_dialog.peer.channel_id != 0) {
                                    tL_dialog.id = (long) (-tL_dialog.peer.channel_id);
                                } else if (tL_dialog.peer.chat_id != 0) {
                                    tL_dialog.id = (long) (-tL_dialog.peer.chat_id);
                                } else {
                                    tL_dialog.id = (long) tL_dialog.peer.user_id;
                                }
                                if (stringBuilder.length() > 0) {
                                    stringBuilder.append(",");
                                }
                                stringBuilder.append(tL_dialog.id);
                                hashMap.put(Long.valueOf(tL_dialog.id), tL_dialog);
                            }
                            SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                            Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                            objArr[0] = stringBuilder.toString();
                            SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE did IN (%s)", objArr), new Object[0]);
                            while (queryFinalized.next()) {
                                long longValue = queryFinalized.longValue(0);
                                tL_dialog = (TL_dialog) hashMap.remove(Long.valueOf(longValue));
                                if (tL_dialog != null) {
                                    this.val$dialogsRes.dialogs.remove(tL_dialog);
                                    i = 0;
                                    while (i < this.val$dialogsRes.messages.size()) {
                                        message = (Message) this.val$dialogsRes.messages.get(i);
                                        if (MessageObject.getDialogId(message) == longValue) {
                                            this.val$dialogsRes.messages.remove(i);
                                            i--;
                                            if (message.id == tL_dialog.top_message) {
                                                tL_dialog.top_message = 0;
                                            }
                                            if (tL_dialog.top_message == 0) {
                                                break;
                                            }
                                            i3 = i;
                                        } else {
                                            i3 = i;
                                        }
                                        i = i3 + MessagesController.UPDATE_MASK_NAME;
                                    }
                                }
                            }
                            queryFinalized.dispose();
                            queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
                            if (queryFinalized.next()) {
                                int max = Math.max(1441062000, queryFinalized.intValue(0));
                                i3 = 0;
                                i = i2;
                                while (i3 < this.val$dialogsRes.messages.size()) {
                                    int i5;
                                    message2 = (Message) this.val$dialogsRes.messages.get(i3);
                                    if (message2.date < max) {
                                        this.val$dialogsRes.messages.remove(i3);
                                        i3--;
                                        tL_dialog = (TL_dialog) hashMap.remove(Long.valueOf(MessageObject.getDialogId(message2)));
                                        if (tL_dialog != null) {
                                            this.val$dialogsRes.dialogs.remove(tL_dialog);
                                        }
                                        i5 = i3;
                                        i3 = -1;
                                    } else {
                                        i5 = i3;
                                        i3 = i;
                                    }
                                    i = i3;
                                    i3 = i5 + MessagesController.UPDATE_MASK_NAME;
                                }
                                i4 = i;
                            } else {
                                i4 = i2;
                            }
                            queryFinalized.dispose();
                            MessagesController.this.processLoadedDialogs(this.val$dialogsRes, null, i4, 0, 0, false, true);
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                            AndroidUtilities.runOnUIThread(new C05161());
                        }
                    }
                }

                /* renamed from: com.hanista.mobogram.messenger.MessagesController.55.2 */
                class C05182 implements Runnable {
                    C05182() {
                    }

                    public void run() {
                        MessagesController.this.migratingDialogs = false;
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C05171((messages_Dialogs) tLObject));
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new C05182());
                }
            });
        }
    }

    public static void openByUserName(String str, BaseFragment baseFragment, int i) {
        if (str != null && baseFragment != null) {
            User user = getInstance().getUser(str);
            if (user != null) {
                openChatOrProfileWith(user, null, baseFragment, i, false);
            } else if (baseFragment.getParentActivity() != null) {
                Dialog progressDialog = new ProgressDialog(baseFragment.getParentActivity());
                progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                TLObject tL_contacts_resolveUsername = new TL_contacts_resolveUsername();
                tL_contacts_resolveUsername.username = str;
                progressDialog.setButton(-2, LocaleController.getString("Cancel", C0338R.string.Cancel), new AnonymousClass111(ConnectionsManager.getInstance().sendRequest(tL_contacts_resolveUsername, new AnonymousClass110(progressDialog, baseFragment, i)), baseFragment));
                baseFragment.setVisibleDialog(progressDialog);
                progressDialog.show();
            }
        }
    }

    public static void openChatOrProfileWith(User user, Chat chat, BaseFragment baseFragment, int i, boolean z) {
        if ((user != null || chat != null) && baseFragment != null) {
            String str = null;
            if (chat != null) {
                str = getRestrictionReason(chat.restriction_reason);
            } else if (user != null) {
                str = getRestrictionReason(user.restriction_reason);
                if (user.bot) {
                    z = true;
                    i = UPDATE_MASK_NAME;
                }
            }
            if (str != null) {
                showCantOpenAlert(baseFragment, str);
                return;
            }
            Bundle bundle = new Bundle();
            if (chat != null) {
                bundle.putInt("chat_id", chat.id);
            } else {
                bundle.putInt("user_id", user.id);
            }
            if (i == 0) {
                baseFragment.presentFragment(new ProfileActivity(bundle));
            } else {
                baseFragment.presentFragment(new ChatActivity(bundle), z);
            }
        }
    }

    private void processChannelsUpdatesQueue(int i, int i2) {
        ArrayList arrayList = (ArrayList) this.updatesQueueChannels.get(Integer.valueOf(i));
        if (arrayList != null) {
            Integer num = (Integer) this.channelsPts.get(Integer.valueOf(i));
            if (arrayList.isEmpty() || num == null) {
                this.updatesQueueChannels.remove(Integer.valueOf(i));
                return;
            }
            Collections.sort(arrayList, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
            if (i2 == UPDATE_MASK_AVATAR) {
                this.channelsPts.put(Integer.valueOf(i), Integer.valueOf(((Updates) arrayList.get(0)).pts));
            }
            boolean z = false;
            while (arrayList.size() > 0) {
                int i3;
                boolean z2;
                Updates updates = (Updates) arrayList.get(0);
                if (updates.pts <= num.intValue()) {
                    i3 = UPDATE_MASK_AVATAR;
                } else if (num.intValue() + updates.pts_count == updates.pts) {
                    i3 = 0;
                } else {
                    boolean z3 = true;
                }
                if (i3 == 0) {
                    processUpdates(updates, true);
                    arrayList.remove(0);
                    z2 = true;
                } else if (i3 == UPDATE_MASK_NAME) {
                    Long l = (Long) this.updatesStartWaitTimeChannels.get(Integer.valueOf(i));
                    if (l == null || (!z && Math.abs(System.currentTimeMillis() - l.longValue()) > 1500)) {
                        FileLog.m16e("tmessages", "HOLE IN CHANNEL " + i + " UPDATES QUEUE - getChannelDifference ");
                        this.updatesStartWaitTimeChannels.remove(Integer.valueOf(i));
                        this.updatesQueueChannels.remove(Integer.valueOf(i));
                        getChannelDifference(i);
                        return;
                    }
                    FileLog.m16e("tmessages", "HOLE IN CHANNEL " + i + " UPDATES QUEUE - will wait more time");
                    if (z) {
                        this.updatesStartWaitTimeChannels.put(Integer.valueOf(i), Long.valueOf(System.currentTimeMillis()));
                        return;
                    }
                    return;
                } else {
                    arrayList.remove(0);
                    z2 = z;
                }
                z = z2;
            }
            this.updatesQueueChannels.remove(Integer.valueOf(i));
            this.updatesStartWaitTimeChannels.remove(Integer.valueOf(i));
            FileLog.m16e("tmessages", "UPDATES CHANNEL " + i + " QUEUE PROCEED - OK");
        }
    }

    private void processUpdatesQueue(int i, int i2) {
        ArrayList arrayList;
        List list;
        if (i == 0) {
            list = this.updatesQueueSeq;
            Collections.sort(list, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(MessagesController.this.getUpdateSeq(updates), MessagesController.this.getUpdateSeq(updates2));
                }
            });
            arrayList = list;
        } else if (i == UPDATE_MASK_NAME) {
            list = this.updatesQueuePts;
            Collections.sort(list, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
            r4 = list;
        } else if (i == UPDATE_MASK_AVATAR) {
            list = this.updatesQueueQts;
            Collections.sort(list, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
            r4 = list;
        } else {
            arrayList = null;
        }
        if (!(arrayList == null || arrayList.isEmpty())) {
            Updates updates;
            if (i2 == UPDATE_MASK_AVATAR) {
                updates = (Updates) arrayList.get(0);
                if (i == 0) {
                    MessagesStorage.lastSeqValue = getUpdateSeq(updates);
                } else if (i == UPDATE_MASK_NAME) {
                    MessagesStorage.lastPtsValue = updates.pts;
                } else {
                    MessagesStorage.lastQtsValue = updates.pts;
                }
            }
            boolean z = false;
            while (arrayList.size() > 0) {
                boolean z2;
                updates = (Updates) arrayList.get(0);
                int isValidUpdate = isValidUpdate(updates, i);
                if (isValidUpdate == 0) {
                    processUpdates(updates, true);
                    arrayList.remove(0);
                    z2 = true;
                } else if (isValidUpdate != UPDATE_MASK_NAME) {
                    arrayList.remove(0);
                    z2 = z;
                } else if (getUpdatesStartTime(i) == 0 || (!z && Math.abs(System.currentTimeMillis() - getUpdatesStartTime(i)) > 1500)) {
                    FileLog.m16e("tmessages", "HOLE IN UPDATES QUEUE - getDifference");
                    setUpdatesStartTime(i, 0);
                    arrayList.clear();
                    getDifference();
                    return;
                } else {
                    FileLog.m16e("tmessages", "HOLE IN UPDATES QUEUE - will wait more time");
                    if (z) {
                        setUpdatesStartTime(i, System.currentTimeMillis());
                        return;
                    }
                    return;
                }
                z = z2;
            }
            arrayList.clear();
            FileLog.m16e("tmessages", "UPDATES QUEUE PROCEED - OK");
        }
        setUpdatesStartTime(i, 0);
    }

    private void reloadDialogsReadValue(ArrayList<TL_dialog> arrayList, long j) {
        if (!arrayList.isEmpty() && !UserConfig.isRobot) {
            TLObject tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i += UPDATE_MASK_NAME) {
                    tL_messages_getPeerDialogs.peers.add(getInputPeer((int) ((TL_dialog) arrayList.get(i)).id));
                }
            } else {
                tL_messages_getPeerDialogs.peers.add(getInputPeer((int) j));
            }
            ConnectionsManager.getInstance().sendRequest(tL_messages_getPeerDialogs, new C05679());
        }
    }

    private void reloadMessages(ArrayList<Integer> arrayList, long j) {
        if (!arrayList.isEmpty()) {
            TLObject tLObject;
            ArrayList arrayList2 = new ArrayList();
            Chat chatByDialog = ChatObject.getChatByDialog(j);
            TLObject tL_channels_getMessages;
            if (ChatObject.isChannel(chatByDialog)) {
                tL_channels_getMessages = new TL_channels_getMessages();
                tL_channels_getMessages.channel = getInputChannel(chatByDialog);
                tL_channels_getMessages.id = arrayList2;
                tLObject = tL_channels_getMessages;
            } else {
                tL_channels_getMessages = new TL_messages_getMessages();
                tL_channels_getMessages.id = arrayList2;
                tLObject = tL_channels_getMessages;
            }
            ArrayList arrayList3 = (ArrayList) this.reloadingMessages.get(Long.valueOf(j));
            for (int i = 0; i < arrayList.size(); i += UPDATE_MASK_NAME) {
                Integer num = (Integer) arrayList.get(i);
                if (arrayList3 == null || !arrayList3.contains(num)) {
                    arrayList2.add(num);
                }
            }
            if (!arrayList2.isEmpty()) {
                if (arrayList3 == null) {
                    arrayList3 = new ArrayList();
                    this.reloadingMessages.put(Long.valueOf(j), arrayList3);
                }
                arrayList3.addAll(arrayList2);
                ConnectionsManager.getInstance().sendRequest(tLObject, new AnonymousClass12(j, chatByDialog, arrayList2));
            }
        }
    }

    private void setUpdatesStartTime(int i, long j) {
        if (i == 0) {
            this.updatesStartWaitTimeSeq = j;
        } else if (i == UPDATE_MASK_NAME) {
            this.updatesStartWaitTimePts = j;
        } else if (i == UPDATE_MASK_AVATAR) {
            this.updatesStartWaitTimeQts = j;
        }
    }

    private static void showCantOpenAlert(BaseFragment baseFragment, String str) {
        Builder builder = new Builder(baseFragment.getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
        builder.setMessage(str);
        baseFragment.showDialog(builder.create());
    }

    private void updatePrintingStrings() {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        ArrayList arrayList = new ArrayList(this.printingUsers.keySet());
        for (Entry entry : this.printingUsers.entrySet()) {
            long longValue = ((Long) entry.getKey()).longValue();
            arrayList = (ArrayList) entry.getValue();
            int i = (int) longValue;
            Long valueOf;
            Object[] objArr;
            if (i > 0 || i == 0 || arrayList.size() == UPDATE_MASK_NAME) {
                PrintingUser printingUser = (PrintingUser) arrayList.get(0);
                User user = getUser(Integer.valueOf(printingUser.userId));
                if (user != null) {
                    if (printingUser.action instanceof TL_sendMessageRecordAudioAction) {
                        if (i < 0) {
                            valueOf = Long.valueOf(longValue);
                            objArr = new Object[UPDATE_MASK_NAME];
                            objArr[0] = getUserNameForTyping(user);
                            hashMap.put(valueOf, LocaleController.formatString("IsRecordingAudio", C0338R.string.IsRecordingAudio, objArr));
                        } else {
                            hashMap.put(Long.valueOf(longValue), LocaleController.getString("RecordingAudio", C0338R.string.RecordingAudio));
                        }
                        hashMap2.put(Long.valueOf(longValue), Integer.valueOf(UPDATE_MASK_NAME));
                    } else if (printingUser.action instanceof TL_sendMessageUploadAudioAction) {
                        if (i < 0) {
                            valueOf = Long.valueOf(longValue);
                            objArr = new Object[UPDATE_MASK_NAME];
                            objArr[0] = getUserNameForTyping(user);
                            hashMap.put(valueOf, LocaleController.formatString("IsSendingAudio", C0338R.string.IsSendingAudio, objArr));
                        } else {
                            hashMap.put(Long.valueOf(longValue), LocaleController.getString("SendingAudio", C0338R.string.SendingAudio));
                        }
                        hashMap2.put(Long.valueOf(longValue), Integer.valueOf(UPDATE_MASK_AVATAR));
                    } else if ((printingUser.action instanceof TL_sendMessageUploadVideoAction) || (printingUser.action instanceof TL_sendMessageRecordVideoAction)) {
                        if (i < 0) {
                            valueOf = Long.valueOf(longValue);
                            objArr = new Object[UPDATE_MASK_NAME];
                            objArr[0] = getUserNameForTyping(user);
                            hashMap.put(valueOf, LocaleController.formatString("IsSendingVideo", C0338R.string.IsSendingVideo, objArr));
                        } else {
                            hashMap.put(Long.valueOf(longValue), LocaleController.getString("SendingVideoStatus", C0338R.string.SendingVideoStatus));
                        }
                        hashMap2.put(Long.valueOf(longValue), Integer.valueOf(UPDATE_MASK_AVATAR));
                    } else if (printingUser.action instanceof TL_sendMessageUploadDocumentAction) {
                        if (i < 0) {
                            valueOf = Long.valueOf(longValue);
                            objArr = new Object[UPDATE_MASK_NAME];
                            objArr[0] = getUserNameForTyping(user);
                            hashMap.put(valueOf, LocaleController.formatString("IsSendingFile", C0338R.string.IsSendingFile, objArr));
                        } else {
                            hashMap.put(Long.valueOf(longValue), LocaleController.getString("SendingFile", C0338R.string.SendingFile));
                        }
                        hashMap2.put(Long.valueOf(longValue), Integer.valueOf(UPDATE_MASK_AVATAR));
                    } else if (printingUser.action instanceof TL_sendMessageUploadPhotoAction) {
                        if (i < 0) {
                            valueOf = Long.valueOf(longValue);
                            objArr = new Object[UPDATE_MASK_NAME];
                            objArr[0] = getUserNameForTyping(user);
                            hashMap.put(valueOf, LocaleController.formatString("IsSendingPhoto", C0338R.string.IsSendingPhoto, objArr));
                        } else {
                            hashMap.put(Long.valueOf(longValue), LocaleController.getString("SendingPhoto", C0338R.string.SendingPhoto));
                        }
                        hashMap2.put(Long.valueOf(longValue), Integer.valueOf(UPDATE_MASK_AVATAR));
                    } else if (!(printingUser.action instanceof TL_sendMessageGamePlayAction)) {
                        if (i < 0) {
                            valueOf = Long.valueOf(longValue);
                            Object[] objArr2 = new Object[UPDATE_MASK_AVATAR];
                            objArr2[0] = getUserNameForTyping(user);
                            objArr2[UPDATE_MASK_NAME] = LocaleController.getString("IsTyping", C0338R.string.IsTyping);
                            hashMap.put(valueOf, String.format("%s %s", objArr2));
                        } else {
                            hashMap.put(Long.valueOf(longValue), LocaleController.getString("Typing", C0338R.string.Typing));
                        }
                        hashMap2.put(Long.valueOf(longValue), Integer.valueOf(0));
                    }
                } else {
                    return;
                }
            }
            String str = TtmlNode.ANONYMOUS_REGION_ID;
            Iterator it = arrayList.iterator();
            int i2 = 0;
            while (it.hasNext()) {
                User user2 = getUser(Integer.valueOf(((PrintingUser) it.next()).userId));
                if (user2 != null) {
                    str = (str.length() != 0 ? str + ", " : str) + getUserNameForTyping(user2);
                    i = i2 + UPDATE_MASK_NAME;
                } else {
                    i = i2;
                }
                if (i == UPDATE_MASK_AVATAR) {
                    break;
                }
                i2 = i;
            }
            i = i2;
            if (str.length() != 0) {
                if (i == UPDATE_MASK_NAME) {
                    valueOf = Long.valueOf(longValue);
                    objArr2 = new Object[UPDATE_MASK_AVATAR];
                    objArr2[0] = str;
                    objArr2[UPDATE_MASK_NAME] = LocaleController.getString("IsTyping", C0338R.string.IsTyping);
                    hashMap.put(valueOf, String.format("%s %s", objArr2));
                } else if (arrayList.size() > UPDATE_MASK_AVATAR) {
                    Long valueOf2 = Long.valueOf(longValue);
                    objArr = new Object[UPDATE_MASK_AVATAR];
                    objArr[0] = str;
                    objArr[UPDATE_MASK_NAME] = LocaleController.formatPluralString("AndMoreTyping", arrayList.size() - 2);
                    hashMap.put(valueOf2, String.format("%s %s", objArr));
                } else {
                    valueOf = Long.valueOf(longValue);
                    objArr2 = new Object[UPDATE_MASK_AVATAR];
                    objArr2[0] = str;
                    objArr2[UPDATE_MASK_NAME] = LocaleController.getString("AreTyping", C0338R.string.AreTyping);
                    hashMap.put(valueOf, String.format("%s %s", objArr2));
                }
                hashMap2.put(Long.valueOf(longValue), Integer.valueOf(0));
            }
        }
        this.lastPrintingStringCount = hashMap.size();
        AndroidUtilities.runOnUIThread(new AnonymousClass48(hashMap, hashMap2));
    }

    private boolean updatePrintingUsersWithNewMessages(long j, ArrayList<MessageObject> arrayList) {
        if (j > 0) {
            if (((ArrayList) this.printingUsers.get(Long.valueOf(j))) != null) {
                this.printingUsers.remove(Long.valueOf(j));
                return true;
            }
        } else if (j < 0) {
            boolean z;
            ArrayList arrayList2 = new ArrayList();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                MessageObject messageObject = (MessageObject) it.next();
                if (!arrayList2.contains(Integer.valueOf(messageObject.messageOwner.from_id))) {
                    arrayList2.add(Integer.valueOf(messageObject.messageOwner.from_id));
                }
            }
            ArrayList arrayList3 = (ArrayList) this.printingUsers.get(Long.valueOf(j));
            if (arrayList3 != null) {
                int i = 0;
                z = false;
                while (i < arrayList3.size()) {
                    int i2;
                    boolean z2;
                    if (arrayList2.contains(Integer.valueOf(((PrintingUser) arrayList3.get(i)).userId))) {
                        arrayList3.remove(i);
                        i--;
                        if (arrayList3.isEmpty()) {
                            this.printingUsers.remove(Long.valueOf(j));
                        }
                        i2 = i;
                        z2 = true;
                    } else {
                        i2 = i;
                        z2 = z;
                    }
                    z = z2;
                    i = i2 + UPDATE_MASK_NAME;
                }
            } else {
                z = false;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    public void addSupportUser() {
        User tL_userForeign_old2 = new TL_userForeign_old2();
        tL_userForeign_old2.phone = "333";
        tL_userForeign_old2.id = 333000;
        tL_userForeign_old2.first_name = "Telegram";
        tL_userForeign_old2.last_name = TtmlNode.ANONYMOUS_REGION_ID;
        tL_userForeign_old2.status = null;
        tL_userForeign_old2.photo = new TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old2, true);
        tL_userForeign_old2 = new TL_userForeign_old2();
        tL_userForeign_old2.phone = "42777";
        tL_userForeign_old2.id = 777000;
        tL_userForeign_old2.first_name = "Telegram";
        tL_userForeign_old2.last_name = "Notifications";
        tL_userForeign_old2.status = null;
        tL_userForeign_old2.photo = new TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old2, true);
    }

    public void addToViewsQueue(Message message, boolean z) {
        ArrayList arrayList = new ArrayList();
        long j = (long) message.id;
        if (message.to_id.channel_id != 0) {
            j |= ((long) message.to_id.channel_id) << UPDATE_MASK_CHAT_MEMBERS;
        }
        arrayList.add(Long.valueOf(j));
        MessagesStorage.getInstance().markMessagesContentAsRead(arrayList);
        Utilities.stageQueue.postRunnable(new AnonymousClass60(message));
    }

    public void addUserToChat(int i, User user, ChatFull chatFull, int i2, String str, BaseFragment baseFragment) {
        boolean z = true;
        if (user != null) {
            if (i > 0) {
                TLObject tLObject;
                boolean isChannel = ChatObject.isChannel(i);
                if (!(isChannel && getChat(Integer.valueOf(i)).megagroup)) {
                    z = false;
                }
                InputUser inputUser = getInputUser(user);
                TLObject tL_messages_startBot;
                if (str != null && (!isChannel || z)) {
                    tL_messages_startBot = new TL_messages_startBot();
                    tL_messages_startBot.bot = inputUser;
                    if (isChannel) {
                        tL_messages_startBot.peer = getInputPeer(-i);
                    } else {
                        tL_messages_startBot.peer = new TL_inputPeerChat();
                        tL_messages_startBot.peer.chat_id = i;
                    }
                    tL_messages_startBot.start_param = str;
                    tL_messages_startBot.random_id = Utilities.random.nextLong();
                    tLObject = tL_messages_startBot;
                } else if (!isChannel) {
                    tL_messages_startBot = new TL_messages_addChatUser();
                    tL_messages_startBot.chat_id = i;
                    tL_messages_startBot.fwd_limit = i2;
                    tL_messages_startBot.user_id = inputUser;
                    tLObject = tL_messages_startBot;
                } else if (inputUser instanceof TL_inputUserSelf) {
                    if (!this.joiningToChannels.contains(Integer.valueOf(i))) {
                        tL_messages_startBot = new TL_channels_joinChannel();
                        tL_messages_startBot.channel = getInputChannel(i);
                        this.joiningToChannels.add(Integer.valueOf(i));
                        tLObject = tL_messages_startBot;
                    } else {
                        return;
                    }
                } else if (!user.bot || z) {
                    tL_messages_startBot = new TL_channels_inviteToChannel();
                    tL_messages_startBot.channel = getInputChannel(i);
                    tL_messages_startBot.users.add(inputUser);
                    tLObject = tL_messages_startBot;
                } else {
                    tL_messages_startBot = new TL_channels_editAdmin();
                    tL_messages_startBot.channel = getInputChannel(i);
                    tL_messages_startBot.user_id = getInputUser(user);
                    tL_messages_startBot.role = new TL_channelRoleEditor();
                    tLObject = tL_messages_startBot;
                }
                ConnectionsManager.getInstance().sendRequest(tLObject, new AnonymousClass78(isChannel, inputUser, i, baseFragment, z));
            } else if (chatFull instanceof TL_chatFull) {
                int i3 = 0;
                while (i3 < chatFull.participants.participants.size()) {
                    if (((ChatParticipant) chatFull.participants.participants.get(i3)).user_id != user.id) {
                        i3 += UPDATE_MASK_NAME;
                    } else {
                        return;
                    }
                }
                Chat chat = getChat(Integer.valueOf(i));
                chat.participants_count += UPDATE_MASK_NAME;
                ArrayList arrayList = new ArrayList();
                arrayList.add(chat);
                MessagesStorage.getInstance().putUsersAndChats(null, arrayList, true, true);
                TL_chatParticipant tL_chatParticipant = new TL_chatParticipant();
                tL_chatParticipant.user_id = user.id;
                tL_chatParticipant.inviter_id = UserConfig.getClientUserId();
                tL_chatParticipant.date = ConnectionsManager.getInstance().getCurrentTime();
                chatFull.participants.participants.add(0, tL_chatParticipant);
                MessagesStorage.getInstance().updateChatInfo(chatFull, true);
                NotificationCenter instance = NotificationCenter.getInstance();
                i3 = NotificationCenter.chatInfoDidLoaded;
                Object[] objArr = new Object[UPDATE_MASK_STATUS];
                objArr[0] = chatFull;
                objArr[UPDATE_MASK_NAME] = Integer.valueOf(0);
                objArr[UPDATE_MASK_AVATAR] = Boolean.valueOf(false);
                objArr[3] = null;
                instance.postNotificationName(i3, objArr);
                instance = NotificationCenter.getInstance();
                i3 = NotificationCenter.updateInterfaces;
                objArr = new Object[UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(UPDATE_MASK_CHAT_MEMBERS);
                instance.postNotificationName(i3, objArr);
            }
        }
    }

    public void addUsersToChannel(int i, ArrayList<InputUser> arrayList, BaseFragment baseFragment) {
        if (arrayList != null && !arrayList.isEmpty()) {
            TLObject tL_channels_inviteToChannel = new TL_channels_inviteToChannel();
            tL_channels_inviteToChannel.channel = getInputChannel(i);
            tL_channels_inviteToChannel.users = arrayList;
            ConnectionsManager.getInstance().sendRequest(tL_channels_inviteToChannel, new AnonymousClass70(baseFragment));
        }
    }

    public void blockUser(int i) {
        User user = getUser(Integer.valueOf(i));
        if (user != null && !this.blockedUsers.contains(Integer.valueOf(i))) {
            this.blockedUsers.add(Integer.valueOf(i));
            if (user.bot) {
                SearchQuery.removeInline(i);
            } else {
                SearchQuery.removePeer(i);
            }
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            TLObject tL_contacts_block = new TL_contacts_block();
            tL_contacts_block.id = getInputUser(user);
            ConnectionsManager.getInstance().sendRequest(tL_contacts_block, new AnonymousClass24(user));
        }
    }

    public void cancelLoadFullChat(int i) {
        this.loadingFullChats.remove(Integer.valueOf(i));
    }

    public void cancelLoadFullUser(int i) {
        this.loadingFullUsers.remove(Integer.valueOf(i));
    }

    public void cancelTyping(int i, long j) {
        HashMap hashMap = (HashMap) this.sendingTypings.get(Integer.valueOf(i));
        if (hashMap != null) {
            hashMap.remove(Long.valueOf(j));
        }
    }

    public void changeChatAvatar(int i, InputFile inputFile) {
        TLObject tL_channels_editPhoto;
        if (ChatObject.isChannel(i)) {
            tL_channels_editPhoto = new TL_channels_editPhoto();
            tL_channels_editPhoto.channel = getInputChannel(i);
            if (inputFile != null) {
                tL_channels_editPhoto.photo = new TL_inputChatUploadedPhoto();
                tL_channels_editPhoto.photo.file = inputFile;
            } else {
                tL_channels_editPhoto.photo = new TL_inputChatPhotoEmpty();
            }
        } else {
            tL_channels_editPhoto = new TL_messages_editChatPhoto();
            tL_channels_editPhoto.chat_id = i;
            if (inputFile != null) {
                tL_channels_editPhoto.photo = new TL_inputChatUploadedPhoto();
                tL_channels_editPhoto.photo.file = inputFile;
            } else {
                tL_channels_editPhoto.photo = new TL_inputChatPhotoEmpty();
            }
        }
        ConnectionsManager.getInstance().sendRequest(tL_channels_editPhoto, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    MessagesController.this.processUpdates((Updates) tLObject, false);
                }
            }
        }, UPDATE_MASK_USER_PRINT);
    }

    public void changeChatTitle(int i, String str) {
        if (i > 0) {
            TLObject tL_channels_editTitle;
            if (ChatObject.isChannel(i)) {
                tL_channels_editTitle = new TL_channels_editTitle();
                tL_channels_editTitle.channel = getInputChannel(i);
                tL_channels_editTitle.title = str;
            } else {
                tL_channels_editTitle = new TL_messages_editChatTitle();
                tL_channels_editTitle.chat_id = i;
                tL_channels_editTitle.title = str;
            }
            ConnectionsManager.getInstance().sendRequest(tL_channels_editTitle, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        MessagesController.this.processUpdates((Updates) tLObject, false);
                    }
                }
            }, UPDATE_MASK_USER_PRINT);
            return;
        }
        Chat chat = getChat(Integer.valueOf(i));
        chat.title = str;
        ArrayList arrayList = new ArrayList();
        arrayList.add(chat);
        MessagesStorage.getInstance().putUsersAndChats(null, arrayList, true, true);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        NotificationCenter instance = NotificationCenter.getInstance();
        int i2 = NotificationCenter.updateInterfaces;
        Object[] objArr = new Object[UPDATE_MASK_NAME];
        objArr[0] = Integer.valueOf(UPDATE_MASK_CHAT_NAME);
        instance.postNotificationName(i2, objArr);
    }

    public void checkChannelInviter(int i) {
        AndroidUtilities.runOnUIThread(new AnonymousClass97(i));
    }

    protected void checkLastDialogMessage(TL_dialog tL_dialog, InputPeer inputPeer, long j) {
        Throwable e;
        long createPendingTask;
        int i = (int) tL_dialog.id;
        if (i != 0 && !this.checkingLastMessagesDialogs.containsKey(Integer.valueOf(i))) {
            if (UserConfig.isRobot) {
                this.checkingLastMessagesDialogs.remove(Integer.valueOf(i));
                return;
            }
            TLObject tL_messages_getHistory = new TL_messages_getHistory();
            tL_messages_getHistory.peer = inputPeer == null ? getInputPeer(i) : inputPeer;
            if (tL_messages_getHistory.peer != null) {
                tL_messages_getHistory.limit = UPDATE_MASK_NAME;
                this.checkingLastMessagesDialogs.put(Integer.valueOf(i), Boolean.valueOf(true));
                if (j == 0) {
                    NativeByteBuffer nativeByteBuffer;
                    try {
                        nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + 40);
                        try {
                            nativeByteBuffer.writeInt32(UPDATE_MASK_AVATAR);
                            nativeByteBuffer.writeInt64(tL_dialog.id);
                            nativeByteBuffer.writeInt32(tL_dialog.top_message);
                            nativeByteBuffer.writeInt32(tL_dialog.read_inbox_max_id);
                            nativeByteBuffer.writeInt32(tL_dialog.read_outbox_max_id);
                            nativeByteBuffer.writeInt32(tL_dialog.unread_count);
                            nativeByteBuffer.writeInt32(tL_dialog.last_message_date);
                            nativeByteBuffer.writeInt32(tL_dialog.pts);
                            nativeByteBuffer.writeInt32(tL_dialog.flags);
                            inputPeer.serializeToStream(nativeByteBuffer);
                        } catch (Exception e2) {
                            e = e2;
                            FileLog.m18e("tmessages", e);
                            createPendingTask = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
                            ConnectionsManager.getInstance().sendRequest(tL_messages_getHistory, new AnonymousClass58(tL_dialog, createPendingTask, i));
                        }
                    } catch (Throwable e3) {
                        Throwable th = e3;
                        nativeByteBuffer = null;
                        e = th;
                        FileLog.m18e("tmessages", e);
                        createPendingTask = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
                        ConnectionsManager.getInstance().sendRequest(tL_messages_getHistory, new AnonymousClass58(tL_dialog, createPendingTask, i));
                    }
                    createPendingTask = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
                } else {
                    createPendingTask = j;
                }
                ConnectionsManager.getInstance().sendRequest(tL_messages_getHistory, new AnonymousClass58(tL_dialog, createPendingTask, i));
            }
        }
    }

    public void cleanup() {
        ContactsController.getInstance().cleanup();
        MediaController.m71a().m162b();
        NotificationsController.getInstance().cleanup();
        SendMessagesHelper.getInstance().cleanup();
        SecretChatHelper.getInstance().cleanup();
        StickersQuery.cleanup();
        SearchQuery.cleanup();
        DraftQuery.cleanup();
        this.reloadingWebpages.clear();
        this.reloadingWebpagesPending.clear();
        this.dialogs_dict.clear();
        this.dialogs_read_inbox_max.clear();
        this.dialogs_read_outbox_max.clear();
        this.exportedChats.clear();
        this.fullUsersAbout.clear();
        this.dialogs.clear();
        this.joiningToChannels.clear();
        this.channelViewsToSend.clear();
        this.channelViewsToReload.clear();
        this.dialogsServerOnly.clear();
        this.dialogsUnreadOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogsJustGroupsOnly.clear();
        this.dialogsSuperGroupsOnly.clear();
        this.dialogsChannelOnly.clear();
        this.dialogsContactOnly.clear();
        this.dialogsFavoriteOnly.clear();
        this.dialogsHiddenOnly.clear();
        this.dialogsBotOnly.clear();
        this.dialogMessagesByIds.clear();
        this.dialogMessagesByRandomIds.clear();
        this.users.clear();
        this.usersByUsernames.clear();
        this.chats.clear();
        this.dialogMessage.clear();
        this.printingUsers.clear();
        this.printingStrings.clear();
        this.printingStringsTypes.clear();
        this.onlinePrivacy.clear();
        this.loadingPeerSettings.clear();
        this.lastPrintingStringCount = 0;
        this.nextDialogsCacheOffset = 0;
        Utilities.stageQueue.postRunnable(new C05245());
        this.blockedUsers.clear();
        this.sendingTypings.clear();
        this.loadingFullUsers.clear();
        this.loadedFullUsers.clear();
        this.reloadingMessages.clear();
        this.loadingFullChats.clear();
        this.loadingFullParticipants.clear();
        this.loadedFullParticipants.clear();
        this.loadedFullChats.clear();
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.gettingNewDeleteTask = false;
        this.loadingDialogs = false;
        this.dialogsEndReached = false;
        this.loadingBlockedUsers = false;
        this.firstGettingTask = false;
        this.updatingState = false;
        this.lastStatusUpdateTime = 0;
        this.offlineSent = false;
        this.registeringForPush = false;
        this.uploadingAvatar = null;
        this.statusRequest = 0;
        this.statusSettingState = 0;
        Utilities.stageQueue.postRunnable(new C05336());
        if (this.currentDeleteTaskRunnable != null) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            this.currentDeleteTaskRunnable = null;
        }
        addSupportUser();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    protected void clearFullUsers() {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
    }

    public void convertGroup() {
    }

    public void convertToMegaGroup(Context context, int i) {
        TLObject tL_messages_migrateChat = new TL_messages_migrateChat();
        tL_messages_migrateChat.chat_id = i;
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setButton(-2, LocaleController.getString("Cancel", C0338R.string.Cancel), new AnonymousClass69(ConnectionsManager.getInstance().sendRequest(tL_messages_migrateChat, new AnonymousClass68(context, progressDialog))));
        try {
            progressDialog.show();
        } catch (Exception e) {
        }
    }

    public int createChat(String str, ArrayList<Integer> arrayList, String str2, int i, BaseFragment baseFragment) {
        int i2;
        if (i == UPDATE_MASK_NAME) {
            Chat tL_chat = new TL_chat();
            tL_chat.id = UserConfig.lastBroadcastId;
            tL_chat.title = str;
            tL_chat.photo = new TL_chatPhotoEmpty();
            tL_chat.participants_count = arrayList.size();
            tL_chat.date = (int) (System.currentTimeMillis() / 1000);
            tL_chat.version = UPDATE_MASK_NAME;
            UserConfig.lastBroadcastId--;
            putChat(tL_chat, false);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(tL_chat);
            MessagesStorage.getInstance().putUsersAndChats(null, arrayList2, true, true);
            ChatFull tL_chatFull = new TL_chatFull();
            tL_chatFull.id = tL_chat.id;
            tL_chatFull.chat_photo = new TL_photoEmpty();
            tL_chatFull.notify_settings = new TL_peerNotifySettingsEmpty();
            tL_chatFull.exported_invite = new TL_chatInviteEmpty();
            tL_chatFull.participants = new TL_chatParticipants();
            tL_chatFull.participants.chat_id = tL_chat.id;
            tL_chatFull.participants.admin_id = UserConfig.getClientUserId();
            tL_chatFull.participants.version = UPDATE_MASK_NAME;
            for (i2 = 0; i2 < arrayList.size(); i2 += UPDATE_MASK_NAME) {
                TL_chatParticipant tL_chatParticipant = new TL_chatParticipant();
                tL_chatParticipant.user_id = ((Integer) arrayList.get(i2)).intValue();
                tL_chatParticipant.inviter_id = UserConfig.getClientUserId();
                tL_chatParticipant.date = (int) (System.currentTimeMillis() / 1000);
                tL_chatFull.participants.participants.add(tL_chatParticipant);
            }
            MessagesStorage.getInstance().updateChatInfo(tL_chatFull, false);
            Message tL_messageService = new TL_messageService();
            tL_messageService.action = new TL_messageActionCreatedBroadcastList();
            int newMessageId = UserConfig.getNewMessageId();
            tL_messageService.id = newMessageId;
            tL_messageService.local_id = newMessageId;
            tL_messageService.from_id = UserConfig.getClientUserId();
            tL_messageService.dialog_id = AndroidUtilities.makeBroadcastId(tL_chat.id);
            tL_messageService.to_id = new TL_peerChat();
            tL_messageService.to_id.chat_id = tL_chat.id;
            tL_messageService.date = ConnectionsManager.getInstance().getCurrentTime();
            tL_messageService.random_id = 0;
            tL_messageService.flags |= UPDATE_MASK_READ_DIALOG_MESSAGE;
            UserConfig.saveConfig(false);
            MessageObject messageObject = new MessageObject(tL_messageService, this.users, true);
            messageObject.messageOwner.send_state = 0;
            ArrayList arrayList3 = new ArrayList();
            arrayList3.add(messageObject);
            ArrayList arrayList4 = new ArrayList();
            arrayList4.add(tL_messageService);
            MessagesStorage.getInstance().putMessages(arrayList4, false, true, false, 0);
            updateInterfaceWithMessages(tL_messageService.dialog_id, arrayList3);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter instance = NotificationCenter.getInstance();
            i2 = NotificationCenter.chatDidCreated;
            Object[] objArr = new Object[UPDATE_MASK_NAME];
            objArr[0] = Integer.valueOf(tL_chat.id);
            instance.postNotificationName(i2, objArr);
            return 0;
        } else if (i == 0) {
            TLObject tL_messages_createChat = new TL_messages_createChat();
            tL_messages_createChat.title = str;
            for (i2 = 0; i2 < arrayList.size(); i2 += UPDATE_MASK_NAME) {
                User user = getUser((Integer) arrayList.get(i2));
                if (user != null) {
                    tL_messages_createChat.users.add(getInputUser(user));
                }
            }
            return ConnectionsManager.getInstance().sendRequest(tL_messages_createChat, new AnonymousClass66(baseFragment), UPDATE_MASK_AVATAR);
        } else if (i != UPDATE_MASK_AVATAR && i != UPDATE_MASK_STATUS) {
            return 0;
        } else {
            TLObject tL_channels_createChannel = new TL_channels_createChannel();
            tL_channels_createChannel.title = str;
            tL_channels_createChannel.about = str2;
            if (i == UPDATE_MASK_STATUS) {
                tL_channels_createChannel.megagroup = true;
            } else {
                tL_channels_createChannel.broadcast = true;
            }
            return ConnectionsManager.getInstance().sendRequest(tL_channels_createChannel, new AnonymousClass67(baseFragment), UPDATE_MASK_AVATAR);
        }
    }

    public void deleteDialog(long j, int i) {
        deleteDialog(j, true, i, 0);
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, EncryptedChat encryptedChat, int i) {
        deleteMessages(arrayList, arrayList2, encryptedChat, i, false);
    }

    public void deleteMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, EncryptedChat encryptedChat, int i, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int i2;
            if (i == 0) {
                for (i2 = 0; i2 < arrayList.size(); i2 += UPDATE_MASK_NAME) {
                    MessageObject messageObject = (MessageObject) this.dialogMessagesByIds.get((Integer) arrayList.get(i2));
                    if (messageObject != null) {
                        messageObject.deleted = true;
                    }
                }
            } else {
                markChannelDialogMessageAsDeleted(arrayList, i);
            }
            ArrayList arrayList3 = new ArrayList();
            for (i2 = 0; i2 < arrayList.size(); i2 += UPDATE_MASK_NAME) {
                Integer num = (Integer) arrayList.get(i2);
                if (num.intValue() > 0) {
                    arrayList3.add(num);
                }
            }
            MessagesStorage.getInstance().markMessagesAsDeleted(arrayList, true, i, z);
            MessagesStorage.getInstance().updateDialogsWithDeletedMessages(arrayList, true, i);
            NotificationCenter instance = NotificationCenter.getInstance();
            i2 = NotificationCenter.messagesDeleted;
            Object[] objArr = new Object[UPDATE_MASK_AVATAR];
            objArr[0] = arrayList;
            objArr[UPDATE_MASK_NAME] = Integer.valueOf(i);
            instance.postNotificationName(i2, objArr);
            TLObject tL_channels_deleteMessages;
            if (i != 0) {
                tL_channels_deleteMessages = new TL_channels_deleteMessages();
                tL_channels_deleteMessages.id = arrayList3;
                tL_channels_deleteMessages.channel = getInputChannel(i);
                ConnectionsManager.getInstance().sendRequest(tL_channels_deleteMessages, new AnonymousClass31(i));
                return;
            }
            if (!(arrayList2 == null || encryptedChat == null || arrayList2.isEmpty())) {
                SecretChatHelper.getInstance().sendMessagesDeleteMessage(encryptedChat, arrayList2, null);
            }
            tL_channels_deleteMessages = new TL_messages_deleteMessages();
            tL_channels_deleteMessages.id = arrayList3;
            ConnectionsManager.getInstance().sendRequest(tL_channels_deleteMessages, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
                        MessagesController.this.processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
                    }
                }
            });
        }
    }

    public void deleteUserChannelHistory(Chat chat, User user, int i) {
        if (i == 0) {
            MessagesStorage.getInstance().deleteUserChannelHistory(chat.id, user.id);
        }
        TLObject tL_channels_deleteUserHistory = new TL_channels_deleteUserHistory();
        tL_channels_deleteUserHistory.channel = getInputChannel(chat);
        tL_channels_deleteUserHistory.user_id = getInputUser(user);
        ConnectionsManager.getInstance().sendRequest(tL_channels_deleteUserHistory, new AnonymousClass34(chat, user));
    }

    public void deleteUserFromChat(int i, User user, ChatFull chatFull) {
        deleteUserFromChat(i, user, chatFull, true);
    }

    public void deleteUserFromChat(int i, User user, ChatFull chatFull, boolean z) {
        new DataBaseAccess().m860b((long) (-i));
        MoboConstants.f1341h = z;
        if (user != null) {
            if (i > 0) {
                TLObject tLObject;
                InputUser inputUser = getInputUser(user);
                Chat chat = getChat(Integer.valueOf(i));
                boolean isChannel = ChatObject.isChannel(chat);
                TLObject tL_messages_deleteChatUser;
                if (!isChannel) {
                    tL_messages_deleteChatUser = new TL_messages_deleteChatUser();
                    tL_messages_deleteChatUser.chat_id = i;
                    tL_messages_deleteChatUser.user_id = getInputUser(user);
                    tLObject = tL_messages_deleteChatUser;
                } else if (!(inputUser instanceof TL_inputUserSelf)) {
                    tL_messages_deleteChatUser = new TL_channels_kickFromChannel();
                    tL_messages_deleteChatUser.channel = getInputChannel(chat);
                    tL_messages_deleteChatUser.user_id = inputUser;
                    tL_messages_deleteChatUser.kicked = true;
                    tLObject = tL_messages_deleteChatUser;
                } else if (chat.creator) {
                    tL_messages_deleteChatUser = new TL_channels_deleteChannel();
                    tL_messages_deleteChatUser.channel = getInputChannel(chat);
                    tLObject = tL_messages_deleteChatUser;
                } else {
                    tL_messages_deleteChatUser = new TL_channels_leaveChannel();
                    tL_messages_deleteChatUser.channel = getInputChannel(chat);
                    tLObject = tL_messages_deleteChatUser;
                }
                ConnectionsManager.getInstance().sendRequest(tLObject, new AnonymousClass79(user, z, i, isChannel, inputUser), UPDATE_MASK_USER_PRINT);
            } else if (chatFull instanceof TL_chatFull) {
                int i2;
                boolean z2;
                NotificationCenter instance;
                Chat chat2 = getChat(Integer.valueOf(i));
                chat2.participants_count--;
                ArrayList arrayList = new ArrayList();
                arrayList.add(chat2);
                MessagesStorage.getInstance().putUsersAndChats(null, arrayList, true, true);
                for (i2 = 0; i2 < chatFull.participants.participants.size(); i2 += UPDATE_MASK_NAME) {
                    if (((ChatParticipant) chatFull.participants.participants.get(i2)).user_id == user.id) {
                        chatFull.participants.participants.remove(i2);
                        z2 = true;
                        break;
                    }
                }
                z2 = false;
                if (z2) {
                    MessagesStorage.getInstance().updateChatInfo(chatFull, true);
                    instance = NotificationCenter.getInstance();
                    i2 = NotificationCenter.chatInfoDidLoaded;
                    Object[] objArr = new Object[UPDATE_MASK_STATUS];
                    objArr[0] = chatFull;
                    objArr[UPDATE_MASK_NAME] = Integer.valueOf(0);
                    objArr[UPDATE_MASK_AVATAR] = Boolean.valueOf(false);
                    objArr[3] = null;
                    instance.postNotificationName(i2, objArr);
                }
                instance = NotificationCenter.getInstance();
                i2 = NotificationCenter.updateInterfaces;
                Object[] objArr2 = new Object[UPDATE_MASK_NAME];
                objArr2[0] = Integer.valueOf(UPDATE_MASK_CHAT_MEMBERS);
                instance.postNotificationName(i2, objArr2);
            }
        }
    }

    public void deleteUserPhoto(InputPhoto inputPhoto) {
        if (inputPhoto == null) {
            TLObject tL_photos_updateProfilePhoto = new TL_photos_updateProfilePhoto();
            tL_photos_updateProfilePhoto.id = new TL_inputPhotoEmpty();
            UserConfig.getCurrentUser().photo = new TL_userProfilePhotoEmpty();
            User user = getUser(Integer.valueOf(UserConfig.getClientUserId()));
            if (user == null) {
                user = UserConfig.getCurrentUser();
            }
            if (user != null) {
                user.photo = UserConfig.getCurrentUser().photo;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[UPDATE_MASK_NAME];
                objArr[0] = Integer.valueOf(UPDATE_MASK_ALL);
                instance.postNotificationName(i, objArr);
                ConnectionsManager.getInstance().sendRequest(tL_photos_updateProfilePhoto, new RequestDelegate() {

                    /* renamed from: com.hanista.mobogram.messenger.MessagesController.28.1 */
                    class C05031 implements Runnable {
                        C05031() {
                        }

                        public void run() {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                            NotificationCenter instance = NotificationCenter.getInstance();
                            int i = NotificationCenter.updateInterfaces;
                            Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                            objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_ALL);
                            instance.postNotificationName(i, objArr);
                            UserConfig.saveConfig(true);
                        }
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            User user = MessagesController.this.getUser(Integer.valueOf(UserConfig.getClientUserId()));
                            if (user == null) {
                                user = UserConfig.getCurrentUser();
                                MessagesController.this.putUser(user, false);
                            } else {
                                UserConfig.setCurrentUser(user);
                            }
                            if (user != null) {
                                MessagesStorage.getInstance().clearUserPhotos(user.id);
                                ArrayList arrayList = new ArrayList();
                                arrayList.add(user);
                                MessagesStorage.getInstance().putUsersAndChats(arrayList, null, false, true);
                                user.photo = (UserProfilePhoto) tLObject;
                                AndroidUtilities.runOnUIThread(new C05031());
                            }
                        }
                    }
                });
                return;
            }
            return;
        }
        TLObject tL_photos_deletePhotos = new TL_photos_deletePhotos();
        tL_photos_deletePhotos.id.add(inputPhoto);
        ConnectionsManager.getInstance().sendRequest(tL_photos_deletePhotos, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
            }
        });
    }

    public void didAddedNewTask(int i, SparseArray<ArrayList<Integer>> sparseArray) {
        Utilities.stageQueue.postRunnable(new AnonymousClass17(i));
        AndroidUtilities.runOnUIThread(new AnonymousClass18(sparseArray));
    }

    public void didReceivedNotification(int i, Object... objArr) {
        String str;
        if (i == NotificationCenter.FileDidUpload) {
            str = (String) objArr[0];
            InputFile inputFile = (InputFile) objArr[UPDATE_MASK_NAME];
            if (this.uploadingAvatar != null && this.uploadingAvatar.equals(str)) {
                TLObject tL_photos_uploadProfilePhoto = new TL_photos_uploadProfilePhoto();
                tL_photos_uploadProfilePhoto.file = inputFile;
                ConnectionsManager.getInstance().sendRequest(tL_photos_uploadProfilePhoto, new C05114());
            }
        } else if (i == NotificationCenter.FileDidFailUpload) {
            str = (String) objArr[0];
            if (this.uploadingAvatar != null && this.uploadingAvatar.equals(str)) {
                this.uploadingAvatar = null;
            }
        } else if (i == NotificationCenter.messageReceivedByServer) {
            Integer num = (Integer) objArr[0];
            Integer num2 = (Integer) objArr[UPDATE_MASK_NAME];
            Long l = (Long) objArr[3];
            MessageObject messageObject = (MessageObject) this.dialogMessage.get(l);
            if (messageObject != null && messageObject.getId() == num.intValue()) {
                messageObject.messageOwner.id = num2.intValue();
                messageObject.messageOwner.send_state = 0;
                TL_dialog tL_dialog = (TL_dialog) this.dialogs_dict.get(l);
                if (tL_dialog != null && tL_dialog.top_message == num.intValue()) {
                    tL_dialog.top_message = num2.intValue();
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            MessageObject messageObject2 = (MessageObject) this.dialogMessagesByIds.remove(num);
            if (messageObject2 != null) {
                this.dialogMessagesByIds.put(num2, messageObject2);
            }
        }
    }

    public void generateJoinMessage(int i, boolean z) {
        Chat chat = getChat(Integer.valueOf(i));
        if (chat != null && ChatObject.isChannel(i)) {
            if ((!chat.left && !chat.kicked) || z) {
                Message tL_messageService = new TL_messageService();
                tL_messageService.flags = UPDATE_MASK_READ_DIALOG_MESSAGE;
                int newMessageId = UserConfig.getNewMessageId();
                tL_messageService.id = newMessageId;
                tL_messageService.local_id = newMessageId;
                tL_messageService.date = ConnectionsManager.getInstance().getCurrentTime();
                tL_messageService.from_id = UserConfig.getClientUserId();
                tL_messageService.to_id = new TL_peerChannel();
                tL_messageService.to_id.channel_id = i;
                tL_messageService.dialog_id = (long) (-i);
                tL_messageService.post = true;
                tL_messageService.action = new TL_messageActionChatAddUser();
                tL_messageService.action.users.add(Integer.valueOf(UserConfig.getClientUserId()));
                if (chat.megagroup) {
                    tL_messageService.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                }
                UserConfig.saveConfig(false);
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(tL_messageService);
                arrayList.add(new MessageObject(tL_messageService, null, true));
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass95(arrayList));
                MessagesStorage.getInstance().putMessages(arrayList2, true, true, false, MediaController.m71a().m167c());
                AndroidUtilities.runOnUIThread(new AnonymousClass96(i, arrayList));
            }
        }
    }

    public void generateUpdateMessage() {
        if (!BuildVars.DEBUG_VERSION && UserConfig.lastUpdateVersion != null && !UserConfig.lastUpdateVersion.equals(BuildVars.BUILD_VERSION_STRING)) {
            ConnectionsManager.getInstance().sendRequest(new TL_help_getAppChangelog(), new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        UserConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
                        UserConfig.saveConfig(false);
                    }
                    if ((tLObject instanceof TL_help_appChangelog) || (tLObject instanceof TL_help_appChangelogEmpty)) {
                        TL_updateServiceNotification tL_updateServiceNotification = new TL_updateServiceNotification();
                        tL_updateServiceNotification.message = LocaleController.getString("updateMoboText", C0338R.string.updateMoboText);
                        tL_updateServiceNotification.media = new TL_messageMediaEmpty();
                        tL_updateServiceNotification.type = "update";
                        tL_updateServiceNotification.popup = false;
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(tL_updateServiceNotification);
                        MessagesController.this.processUpdateArray(arrayList, null, null, false);
                    }
                }
            });
        }
    }

    public void getBlockedUsers(boolean z) {
        if (UserConfig.isClientActivated() && !this.loadingBlockedUsers) {
            this.loadingBlockedUsers = true;
            if (z) {
                MessagesStorage.getInstance().getBlockedUsers();
            } else if (UserConfig.isRobot) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            } else {
                TLObject tL_contacts_getBlocked = new TL_contacts_getBlocked();
                tL_contacts_getBlocked.offset = 0;
                tL_contacts_getBlocked.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                ConnectionsManager.getInstance().sendRequest(tL_contacts_getBlocked, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        ArrayList arrayList;
                        ArrayList arrayList2 = new ArrayList();
                        if (tL_error == null) {
                            contacts_Blocked com_hanista_mobogram_tgnet_TLRPC_contacts_Blocked = (contacts_Blocked) tLObject;
                            Iterator it = com_hanista_mobogram_tgnet_TLRPC_contacts_Blocked.blocked.iterator();
                            while (it.hasNext()) {
                                arrayList2.add(Integer.valueOf(((TL_contactBlocked) it.next()).user_id));
                            }
                            arrayList = com_hanista_mobogram_tgnet_TLRPC_contacts_Blocked.users;
                            MessagesStorage.getInstance().putUsersAndChats(com_hanista_mobogram_tgnet_TLRPC_contacts_Blocked.users, null, true, true);
                            MessagesStorage.getInstance().putBlockedUsers(arrayList2, true);
                        } else {
                            arrayList = null;
                        }
                        MessagesController.this.processLoadedBlockedUsers(arrayList2, arrayList, false);
                    }
                });
            }
        }
    }

    protected void getChannelDifference(int i, int i2, long j) {
        Integer num;
        int i3;
        NativeByteBuffer nativeByteBuffer;
        Throwable e;
        long createPendingTask;
        TLObject tL_updates_getChannelDifference;
        Boolean bool = (Boolean) this.gettingDifferenceChannels.get(Integer.valueOf(i));
        if (bool == null) {
            bool = Boolean.valueOf(false);
        }
        if (!bool.booleanValue()) {
            if (i2 != UPDATE_MASK_NAME) {
                Integer num2 = (Integer) this.channelsPts.get(Integer.valueOf(i));
                if (num2 == null) {
                    num2 = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(i));
                    if (num2.intValue() != 0) {
                        this.channelsPts.put(Integer.valueOf(i), num2);
                    }
                    if (num2.intValue() == 0 && i2 == UPDATE_MASK_AVATAR) {
                        return;
                    }
                }
                if (num2.intValue() != 0) {
                    num = num2;
                    i3 = 100;
                } else {
                    return;
                }
            } else if (((Integer) this.channelsPts.get(Integer.valueOf(i))) == null) {
                num = Integer.valueOf(UPDATE_MASK_NAME);
                i3 = UPDATE_MASK_NAME;
            } else {
                return;
            }
            if (j == 0) {
                try {
                    nativeByteBuffer = new NativeByteBuffer(12);
                    try {
                        nativeByteBuffer.writeInt32(UPDATE_MASK_NAME);
                        nativeByteBuffer.writeInt32(i);
                        nativeByteBuffer.writeInt32(i2);
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.m18e("tmessages", e);
                        createPendingTask = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
                        this.gettingDifferenceChannels.put(Integer.valueOf(i), Boolean.valueOf(true));
                        tL_updates_getChannelDifference = new TL_updates_getChannelDifference();
                        tL_updates_getChannelDifference.channel = getInputChannel(i);
                        tL_updates_getChannelDifference.filter = new TL_channelMessagesFilterEmpty();
                        tL_updates_getChannelDifference.pts = num.intValue();
                        tL_updates_getChannelDifference.limit = i3;
                        FileLog.m16e("tmessages", "start getChannelDifference with pts = " + num + " channelId = " + i);
                        ConnectionsManager.getInstance().sendRequest(tL_updates_getChannelDifference, new AnonymousClass93(i, i2, createPendingTask));
                    }
                } catch (Throwable e3) {
                    Throwable th = e3;
                    nativeByteBuffer = null;
                    e = th;
                    FileLog.m18e("tmessages", e);
                    createPendingTask = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
                    this.gettingDifferenceChannels.put(Integer.valueOf(i), Boolean.valueOf(true));
                    tL_updates_getChannelDifference = new TL_updates_getChannelDifference();
                    tL_updates_getChannelDifference.channel = getInputChannel(i);
                    tL_updates_getChannelDifference.filter = new TL_channelMessagesFilterEmpty();
                    tL_updates_getChannelDifference.pts = num.intValue();
                    tL_updates_getChannelDifference.limit = i3;
                    FileLog.m16e("tmessages", "start getChannelDifference with pts = " + num + " channelId = " + i);
                    ConnectionsManager.getInstance().sendRequest(tL_updates_getChannelDifference, new AnonymousClass93(i, i2, createPendingTask));
                }
                createPendingTask = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
            } else {
                createPendingTask = j;
            }
            this.gettingDifferenceChannels.put(Integer.valueOf(i), Boolean.valueOf(true));
            tL_updates_getChannelDifference = new TL_updates_getChannelDifference();
            tL_updates_getChannelDifference.channel = getInputChannel(i);
            tL_updates_getChannelDifference.filter = new TL_channelMessagesFilterEmpty();
            tL_updates_getChannelDifference.pts = num.intValue();
            tL_updates_getChannelDifference.limit = i3;
            FileLog.m16e("tmessages", "start getChannelDifference with pts = " + num + " channelId = " + i);
            ConnectionsManager.getInstance().sendRequest(tL_updates_getChannelDifference, new AnonymousClass93(i, i2, createPendingTask));
        }
    }

    public Chat getChat(Integer num) {
        return (Chat) this.chats.get(num);
    }

    public void getDifference() {
        getDifference(MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue, false);
    }

    public void getDifference(int i, int i2, int i3, boolean z) {
        registerForPush(UserConfig.pushString);
        if (MessagesStorage.lastPtsValue == 0) {
            loadCurrentState();
        } else if (z || !this.gettingDifference) {
            if (!this.firstGettingTask) {
                getNewDeleteTask(null);
                this.firstGettingTask = true;
            }
            this.gettingDifference = true;
            TLObject tL_updates_getDifference = new TL_updates_getDifference();
            tL_updates_getDifference.pts = i;
            tL_updates_getDifference.date = i2;
            tL_updates_getDifference.qts = i3;
            if (tL_updates_getDifference.date == 0) {
                tL_updates_getDifference.date = ConnectionsManager.getInstance().getCurrentTime();
            }
            FileLog.m16e("tmessages", "start getDifference with date = " + MessagesStorage.lastDateValue + " pts = " + MessagesStorage.lastPtsValue + " seq = " + MessagesStorage.lastSeqValue);
            ConnectionsManager.getInstance().setIsUpdating(true);
            ConnectionsManager.getInstance().sendRequest(tL_updates_getDifference, new RequestDelegate() {

                /* renamed from: com.hanista.mobogram.messenger.MessagesController.94.1 */
                class C05541 implements Runnable {
                    final /* synthetic */ updates_Difference val$res;

                    C05541(updates_Difference com_hanista_mobogram_tgnet_TLRPC_updates_Difference) {
                        this.val$res = com_hanista_mobogram_tgnet_TLRPC_updates_Difference;
                    }

                    public void run() {
                        MessagesController.this.putUsers(this.val$res.users, false);
                        MessagesController.this.putChats(this.val$res.chats, false);
                    }
                }

                /* renamed from: com.hanista.mobogram.messenger.MessagesController.94.2 */
                class C05602 implements Runnable {
                    final /* synthetic */ HashMap val$chatsDict;
                    final /* synthetic */ ArrayList val$msgUpdates;
                    final /* synthetic */ updates_Difference val$res;
                    final /* synthetic */ HashMap val$usersDict;

                    /* renamed from: com.hanista.mobogram.messenger.MessagesController.94.2.1 */
                    class C05551 implements Runnable {
                        final /* synthetic */ HashMap val$corrected;

                        C05551(HashMap hashMap) {
                            this.val$corrected = hashMap;
                        }

                        public void run() {
                            for (Entry entry : this.val$corrected.entrySet()) {
                                Integer num = (Integer) entry.getKey();
                                long[] jArr = (long[]) entry.getValue();
                                Integer valueOf = Integer.valueOf((int) jArr[MessagesController.UPDATE_MASK_NAME]);
                                SendMessagesHelper.getInstance().processSentMessage(valueOf.intValue());
                                NotificationCenter instance = NotificationCenter.getInstance();
                                int i = NotificationCenter.messageReceivedByServer;
                                Object[] objArr = new Object[MessagesController.UPDATE_MASK_STATUS];
                                objArr[0] = valueOf;
                                objArr[MessagesController.UPDATE_MASK_NAME] = num;
                                objArr[MessagesController.UPDATE_MASK_AVATAR] = null;
                                objArr[3] = Long.valueOf(jArr[0]);
                                instance.postNotificationName(i, objArr);
                            }
                        }
                    }

                    /* renamed from: com.hanista.mobogram.messenger.MessagesController.94.2.2 */
                    class C05592 implements Runnable {

                        /* renamed from: com.hanista.mobogram.messenger.MessagesController.94.2.2.1 */
                        class C05561 implements Runnable {
                            final /* synthetic */ HashMap val$messages;

                            C05561(HashMap hashMap) {
                                this.val$messages = hashMap;
                            }

                            public void run() {
                                for (Entry entry : this.val$messages.entrySet()) {
                                    Long l = (Long) entry.getKey();
                                    MessagesController.this.updateInterfaceWithMessages(l.longValue(), (ArrayList) entry.getValue());
                                }
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            }
                        }

                        /* renamed from: com.hanista.mobogram.messenger.MessagesController.94.2.2.2 */
                        class C05582 implements Runnable {
                            final /* synthetic */ ArrayList val$pushMessages;

                            /* renamed from: com.hanista.mobogram.messenger.MessagesController.94.2.2.2.1 */
                            class C05571 implements Runnable {
                                C05571() {
                                }

                                public void run() {
                                    NotificationsController.getInstance().processNewMessages(C05582.this.val$pushMessages, !(C05602.this.val$res instanceof TL_updates_differenceSlice));
                                }
                            }

                            C05582(ArrayList arrayList) {
                                this.val$pushMessages = arrayList;
                            }

                            public void run() {
                                if (!this.val$pushMessages.isEmpty()) {
                                    AndroidUtilities.runOnUIThread(new C05571());
                                }
                                MessagesStorage.getInstance().putMessages(C05602.this.val$res.new_messages, true, false, false, MediaController.m71a().m167c());
                            }
                        }

                        C05592() {
                        }

                        public void run() {
                            int i = 0;
                            if (!(C05602.this.val$res.new_messages.isEmpty() && C05602.this.val$res.new_encrypted_messages.isEmpty())) {
                                int i2;
                                HashMap hashMap = new HashMap();
                                for (int i3 = 0; i3 < C05602.this.val$res.new_encrypted_messages.size(); i3 += MessagesController.UPDATE_MASK_NAME) {
                                    ArrayList decryptMessage = SecretChatHelper.getInstance().decryptMessage((EncryptedMessage) C05602.this.val$res.new_encrypted_messages.get(i3));
                                    if (!(decryptMessage == null || decryptMessage.isEmpty())) {
                                        for (i2 = 0; i2 < decryptMessage.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                                            C05602.this.val$res.new_messages.add((Message) decryptMessage.get(i2));
                                        }
                                    }
                                }
                                ImageLoader.saveMessagesThumbs(C05602.this.val$res.new_messages);
                                ArrayList arrayList = new ArrayList();
                                int clientUserId = UserConfig.getClientUserId();
                                for (i2 = 0; i2 < C05602.this.val$res.new_messages.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                                    Message message = (Message) C05602.this.val$res.new_messages.get(i2);
                                    if (message.dialog_id == 0) {
                                        if (message.to_id.chat_id != 0) {
                                            message.dialog_id = (long) (-message.to_id.chat_id);
                                        } else {
                                            if (message.to_id.user_id == UserConfig.getClientUserId()) {
                                                message.to_id.user_id = message.from_id;
                                            }
                                            message.dialog_id = (long) message.to_id.user_id;
                                        }
                                    }
                                    if (((int) message.dialog_id) != 0) {
                                        if (message.action instanceof TL_messageActionChatDeleteUser) {
                                            User user = (User) C05602.this.val$usersDict.get(Integer.valueOf(message.action.user_id));
                                            if (user != null && user.bot) {
                                                message.reply_markup = new TL_replyKeyboardHide();
                                            }
                                        }
                                        if ((message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate)) {
                                            message.unread = false;
                                            message.media_unread = false;
                                        } else {
                                            ConcurrentHashMap concurrentHashMap = message.out ? MessagesController.this.dialogs_read_outbox_max : MessagesController.this.dialogs_read_inbox_max;
                                            Integer num = (Integer) concurrentHashMap.get(Long.valueOf(message.dialog_id));
                                            if (num == null) {
                                                num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message.out, message.dialog_id));
                                                concurrentHashMap.put(Long.valueOf(message.dialog_id), num);
                                            }
                                            message.unread = num.intValue() < message.id;
                                        }
                                    }
                                    if (message.dialog_id == ((long) clientUserId)) {
                                        message.unread = false;
                                        message.media_unread = false;
                                        message.out = true;
                                    }
                                    MessageObject messageObject = new MessageObject(message, C05602.this.val$usersDict, C05602.this.val$chatsDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                                    if (!messageObject.isOut() && messageObject.isUnread()) {
                                        arrayList.add(messageObject);
                                    }
                                    ArrayList arrayList2 = (ArrayList) hashMap.get(Long.valueOf(message.dialog_id));
                                    if (arrayList2 == null) {
                                        arrayList2 = new ArrayList();
                                        hashMap.put(Long.valueOf(message.dialog_id), arrayList2);
                                    }
                                    arrayList2.add(messageObject);
                                }
                                AndroidUtilities.runOnUIThread(new C05561(hashMap));
                                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C05582(arrayList));
                                SecretChatHelper.getInstance().processPendingEncMessages();
                            }
                            if (!C05602.this.val$res.other_updates.isEmpty()) {
                                MessagesController.this.processUpdateArray(C05602.this.val$res.other_updates, C05602.this.val$res.users, C05602.this.val$res.chats, true);
                            }
                            if (C05602.this.val$res instanceof TL_updates_difference) {
                                MessagesController.this.gettingDifference = false;
                                MessagesStorage.lastSeqValue = C05602.this.val$res.state.seq;
                                MessagesStorage.lastDateValue = C05602.this.val$res.state.date;
                                MessagesStorage.lastPtsValue = C05602.this.val$res.state.pts;
                                MessagesStorage.lastQtsValue = C05602.this.val$res.state.qts;
                                ConnectionsManager.getInstance().setIsUpdating(false);
                                while (i < 3) {
                                    MessagesController.this.processUpdatesQueue(i, MessagesController.UPDATE_MASK_NAME);
                                    i += MessagesController.UPDATE_MASK_NAME;
                                }
                            } else if (C05602.this.val$res instanceof TL_updates_differenceSlice) {
                                MessagesStorage.lastDateValue = C05602.this.val$res.intermediate_state.date;
                                MessagesStorage.lastPtsValue = C05602.this.val$res.intermediate_state.pts;
                                MessagesStorage.lastQtsValue = C05602.this.val$res.intermediate_state.qts;
                            } else if (C05602.this.val$res instanceof TL_updates_differenceEmpty) {
                                MessagesController.this.gettingDifference = false;
                                MessagesStorage.lastSeqValue = C05602.this.val$res.seq;
                                MessagesStorage.lastDateValue = C05602.this.val$res.date;
                                ConnectionsManager.getInstance().setIsUpdating(false);
                                while (i < 3) {
                                    MessagesController.this.processUpdatesQueue(i, MessagesController.UPDATE_MASK_NAME);
                                    i += MessagesController.UPDATE_MASK_NAME;
                                }
                            }
                            MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
                            FileLog.m16e("tmessages", "received difference with date = " + MessagesStorage.lastDateValue + " pts = " + MessagesStorage.lastPtsValue + " seq = " + MessagesStorage.lastSeqValue + " messages = " + C05602.this.val$res.new_messages.size() + " users = " + C05602.this.val$res.users.size() + " chats = " + C05602.this.val$res.chats.size() + " other updates = " + C05602.this.val$res.other_updates.size());
                        }
                    }

                    C05602(updates_Difference com_hanista_mobogram_tgnet_TLRPC_updates_Difference, ArrayList arrayList, HashMap hashMap, HashMap hashMap2) {
                        this.val$res = com_hanista_mobogram_tgnet_TLRPC_updates_Difference;
                        this.val$msgUpdates = arrayList;
                        this.val$usersDict = hashMap;
                        this.val$chatsDict = hashMap2;
                    }

                    public void run() {
                        MessagesStorage.getInstance().putUsersAndChats(this.val$res.users, this.val$res.chats, true, false);
                        if (!this.val$msgUpdates.isEmpty()) {
                            HashMap hashMap = new HashMap();
                            for (int i = 0; i < this.val$msgUpdates.size(); i += MessagesController.UPDATE_MASK_NAME) {
                                TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) this.val$msgUpdates.get(i);
                                Object updateMessageStateAndId = MessagesStorage.getInstance().updateMessageStateAndId(tL_updateMessageID.random_id, null, tL_updateMessageID.id, 0, false, 0);
                                if (updateMessageStateAndId != null) {
                                    hashMap.put(Integer.valueOf(tL_updateMessageID.id), updateMessageStateAndId);
                                }
                            }
                            if (!hashMap.isEmpty()) {
                                AndroidUtilities.runOnUIThread(new C05551(hashMap));
                            }
                        }
                        Utilities.stageQueue.postRunnable(new C05592());
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    int i = 0;
                    if (tL_error == null) {
                        int i2;
                        updates_Difference com_hanista_mobogram_tgnet_TLRPC_updates_Difference = (updates_Difference) tLObject;
                        if (com_hanista_mobogram_tgnet_TLRPC_updates_Difference instanceof TL_updates_differenceSlice) {
                            MessagesController.this.getDifference(com_hanista_mobogram_tgnet_TLRPC_updates_Difference.intermediate_state.pts, com_hanista_mobogram_tgnet_TLRPC_updates_Difference.intermediate_state.date, com_hanista_mobogram_tgnet_TLRPC_updates_Difference.intermediate_state.qts, true);
                        }
                        HashMap hashMap = new HashMap();
                        HashMap hashMap2 = new HashMap();
                        for (i2 = 0; i2 < com_hanista_mobogram_tgnet_TLRPC_updates_Difference.users.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                            User user = (User) com_hanista_mobogram_tgnet_TLRPC_updates_Difference.users.get(i2);
                            hashMap.put(Integer.valueOf(user.id), user);
                        }
                        for (i2 = 0; i2 < com_hanista_mobogram_tgnet_TLRPC_updates_Difference.chats.size(); i2 += MessagesController.UPDATE_MASK_NAME) {
                            Chat chat = (Chat) com_hanista_mobogram_tgnet_TLRPC_updates_Difference.chats.get(i2);
                            hashMap2.put(Integer.valueOf(chat.id), chat);
                        }
                        ArrayList arrayList = new ArrayList();
                        if (!com_hanista_mobogram_tgnet_TLRPC_updates_Difference.other_updates.isEmpty()) {
                            while (i < com_hanista_mobogram_tgnet_TLRPC_updates_Difference.other_updates.size()) {
                                Update update = (Update) com_hanista_mobogram_tgnet_TLRPC_updates_Difference.other_updates.get(i);
                                if (update instanceof TL_updateMessageID) {
                                    arrayList.add((TL_updateMessageID) update);
                                    com_hanista_mobogram_tgnet_TLRPC_updates_Difference.other_updates.remove(i);
                                    i--;
                                }
                                i += MessagesController.UPDATE_MASK_NAME;
                            }
                        }
                        AndroidUtilities.runOnUIThread(new C05541(com_hanista_mobogram_tgnet_TLRPC_updates_Difference));
                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C05602(com_hanista_mobogram_tgnet_TLRPC_updates_Difference, arrayList, hashMap, hashMap2));
                        return;
                    }
                    MessagesController.this.gettingDifference = false;
                    ConnectionsManager.getInstance().setIsUpdating(false);
                }
            });
        }
    }

    public EncryptedChat getEncryptedChat(Integer num) {
        return (EncryptedChat) this.encryptedChats.get(num);
    }

    public EncryptedChat getEncryptedChatDB(int i) {
        EncryptedChat encryptedChat = (EncryptedChat) this.encryptedChats.get(Integer.valueOf(i));
        if (encryptedChat != null && encryptedChat.auth_key != null) {
            return encryptedChat;
        }
        Semaphore semaphore = new Semaphore(0);
        ArrayList arrayList = new ArrayList();
        MessagesStorage.getInstance().getEncryptedChat(i, semaphore, arrayList);
        try {
            semaphore.acquire();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        if (arrayList.size() != UPDATE_MASK_AVATAR) {
            return encryptedChat;
        }
        encryptedChat = (EncryptedChat) arrayList.get(0);
        User user = (User) arrayList.get(UPDATE_MASK_NAME);
        putEncryptedChat(encryptedChat, false);
        putUser(user, true);
        return encryptedChat;
    }

    public ExportedChatInvite getExportedInvite(int i) {
        return (ExportedChatInvite) this.exportedChats.get(Integer.valueOf(i));
    }

    public void getNewDeleteTask(ArrayList<Integer> arrayList) {
        Utilities.stageQueue.postRunnable(new AnonymousClass19(arrayList));
    }

    public TL_dialog getOwnDialog() {
        if (this.ownDialog == null) {
            Iterator it = this.dialogs.iterator();
            while (it.hasNext()) {
                TL_dialog tL_dialog = (TL_dialog) it.next();
                if (tL_dialog.id == ((long) UserConfig.getClientUserId())) {
                    this.ownDialog = tL_dialog;
                    break;
                }
            }
        }
        return this.ownDialog;
    }

    public long getUpdatesStartTime(int i) {
        return i == 0 ? this.updatesStartWaitTimeSeq : i == UPDATE_MASK_NAME ? this.updatesStartWaitTimePts : i == UPDATE_MASK_AVATAR ? this.updatesStartWaitTimeQts : 0;
    }

    public User getUser(Integer num) {
        return (User) this.users.get(num);
    }

    public User getUser(String str) {
        return (str == null || str.length() == 0) ? null : (User) this.usersByUsernames.get(str.toLowerCase());
    }

    public String getUserAbout(int i) {
        return (String) this.fullUsersAbout.get(Integer.valueOf(i));
    }

    public ConcurrentHashMap<Integer, User> getUsers() {
        return this.users;
    }

    public void hideReportSpam(long j, User user, Chat chat) {
        if (user != null || chat != null) {
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            edit.putInt("spam3_" + j, UPDATE_MASK_NAME);
            edit.commit();
            TLObject tL_messages_hideReportSpam = new TL_messages_hideReportSpam();
            if (user != null) {
                tL_messages_hideReportSpam.peer = getInputPeer(user.id);
            } else if (chat != null) {
                tL_messages_hideReportSpam.peer = getInputPeer(-chat.id);
            }
            ConnectionsManager.getInstance().sendRequest(tL_messages_hideReportSpam, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                }
            });
        }
    }

    public boolean isDialogMuted(long j) {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        int i = sharedPreferences.getInt("notify2_" + j, 0);
        return i == UPDATE_MASK_AVATAR ? true : i == 3 && sharedPreferences.getInt("notifyuntil_" + j, 0) >= ConnectionsManager.getInstance().getCurrentTime();
    }

    public void loadChannelParticipants(Integer num) {
        if (!this.loadingFullParticipants.contains(num) && !this.loadedFullParticipants.contains(num)) {
            this.loadingFullParticipants.add(num);
            TLObject tL_channels_getParticipants = new TL_channels_getParticipants();
            tL_channels_getParticipants.channel = getInputChannel(num.intValue());
            tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
            tL_channels_getParticipants.offset = 0;
            tL_channels_getParticipants.limit = UPDATE_MASK_CHAT_MEMBERS;
            ConnectionsManager.getInstance().sendRequest(tL_channels_getParticipants, new AnonymousClass40(num));
        }
    }

    public void loadChatInfo(int i, Semaphore semaphore, boolean z) {
        MessagesStorage.getInstance().loadChatInfo(i, semaphore, z, false);
    }

    public void loadCurrentState() {
        if (!this.updatingState) {
            this.updatingState = true;
            ConnectionsManager.getInstance().sendRequest(new TL_updates_getState(), new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    int i = 0;
                    MessagesController.this.updatingState = false;
                    if (tL_error == null) {
                        TL_updates_state tL_updates_state = (TL_updates_state) tLObject;
                        MessagesStorage.lastDateValue = tL_updates_state.date;
                        MessagesStorage.lastPtsValue = tL_updates_state.pts;
                        MessagesStorage.lastSeqValue = tL_updates_state.seq;
                        MessagesStorage.lastQtsValue = tL_updates_state.qts;
                        while (i < 3) {
                            MessagesController.this.processUpdatesQueue(i, MessagesController.UPDATE_MASK_AVATAR);
                            i += MessagesController.UPDATE_MASK_NAME;
                        }
                        MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
                    } else if (tL_error.code != 401) {
                        MessagesController.this.loadCurrentState();
                    }
                }
            });
        }
    }

    public void loadDialogPhotos(int i, int i2, int i3, long j, boolean z, int i4) {
        if (z) {
            MessagesStorage.getInstance().getDialogPhotos(i, i2, i3, j, i4);
        } else if (i > 0) {
            User user = getUser(Integer.valueOf(i));
            if (user != null) {
                TLObject tL_photos_getUserPhotos = new TL_photos_getUserPhotos();
                tL_photos_getUserPhotos.limit = i3;
                tL_photos_getUserPhotos.offset = i2;
                tL_photos_getUserPhotos.max_id = (long) ((int) j);
                tL_photos_getUserPhotos.user_id = getInputUser(user);
                ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_photos_getUserPhotos, new AnonymousClass22(i, i2, i3, j, i4)), i4);
            }
        } else if (i < 0) {
            TLObject tL_messages_search = new TL_messages_search();
            tL_messages_search.filter = new TL_inputMessagesFilterChatPhotos();
            tL_messages_search.limit = i3;
            tL_messages_search.offset = i2;
            tL_messages_search.max_id = (int) j;
            tL_messages_search.f2672q = TtmlNode.ANONYMOUS_REGION_ID;
            tL_messages_search.peer = getInputPeer(i);
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_messages_search, new AnonymousClass23(i, i2, i3, j, i4)), i4);
        }
    }

    public void loadDialogs(int i, int i2, boolean z) {
        boolean z2 = false;
        if (!this.loadingDialogs) {
            this.loadingDialogs = true;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            FileLog.m16e("tmessages", "load cacheOffset = " + i + " count = " + i2 + " cache = " + z);
            if (z) {
                MessagesStorage.getInstance().getDialogs(i == 0 ? 0 : this.nextDialogsCacheOffset, i2);
            } else if (UserConfig.isRobot) {
                this.loadingDialogs = false;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            } else {
                TLObject tL_messages_getDialogs = new TL_messages_getDialogs();
                tL_messages_getDialogs.limit = i2;
                for (int size = this.dialogs.size() - 1; size >= 0; size--) {
                    TL_dialog tL_dialog = (TL_dialog) this.dialogs.get(size);
                    int i3 = (int) (tL_dialog.id >> UPDATE_MASK_CHAT_MEMBERS);
                    if (!(((int) tL_dialog.id) == 0 || i3 == UPDATE_MASK_NAME || tL_dialog.top_message <= 0)) {
                        MessageObject messageObject = (MessageObject) this.dialogMessage.get(Long.valueOf(tL_dialog.id));
                        if (messageObject != null && messageObject.getId() > 0) {
                            tL_messages_getDialogs.offset_date = messageObject.messageOwner.date;
                            tL_messages_getDialogs.offset_id = messageObject.messageOwner.id;
                            int i4 = messageObject.messageOwner.to_id.channel_id != 0 ? -messageObject.messageOwner.to_id.channel_id : messageObject.messageOwner.to_id.chat_id != 0 ? -messageObject.messageOwner.to_id.chat_id : messageObject.messageOwner.to_id.user_id;
                            tL_messages_getDialogs.offset_peer = getInputPeer(i4);
                            z2 = true;
                            if (!z2) {
                                tL_messages_getDialogs.offset_peer = new TL_inputPeerEmpty();
                            }
                            ConnectionsManager.getInstance().sendRequest(tL_messages_getDialogs, new AnonymousClass54(i2));
                        }
                    }
                }
                if (z2) {
                    tL_messages_getDialogs.offset_peer = new TL_inputPeerEmpty();
                }
                ConnectionsManager.getInstance().sendRequest(tL_messages_getDialogs, new AnonymousClass54(i2));
            }
        }
    }

    public void loadFullChat(int i, int i2, boolean z) {
        if (!this.loadingFullChats.contains(Integer.valueOf(i))) {
            if (z || !this.loadedFullChats.contains(Integer.valueOf(i))) {
                TLObject tL_channels_getFullChannel;
                this.loadingFullChats.add(Integer.valueOf(i));
                Chat chat = getChat(Integer.valueOf(i));
                if (ChatObject.isChannel(i)) {
                    tL_channels_getFullChannel = new TL_channels_getFullChannel();
                    tL_channels_getFullChannel.channel = getInputChannel(i);
                } else {
                    tL_channels_getFullChannel = new TL_messages_getFullChat();
                    tL_channels_getFullChannel.chat_id = i;
                }
                int sendRequest = ConnectionsManager.getInstance().sendRequest(tL_channels_getFullChannel, new AnonymousClass10(chat, i, i2));
                if (i2 != 0) {
                    ConnectionsManager.getInstance().bindRequestToGuid(sendRequest, i2);
                }
            }
        }
    }

    public void loadFullUser(User user, int i, boolean z) {
        if (user != null && !this.loadingFullUsers.contains(Integer.valueOf(user.id))) {
            if (z || !this.loadedFullUsers.contains(Integer.valueOf(user.id))) {
                this.loadingFullUsers.add(Integer.valueOf(user.id));
                TLObject tL_users_getFullUser = new TL_users_getFullUser();
                tL_users_getFullUser.id = getInputUser(user);
                ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_users_getFullUser, new AnonymousClass11(user, i)), i);
            }
        }
    }

    public void loadMessages(long j, int i, int i2, boolean z, int i3, int i4, int i5, int i6, boolean z2, int i7) {
        loadMessages(j, i, i2, z, i3, i4, i5, i6, z2, i7, 0, 0, 0, false);
    }

    public void loadMessages(long j, int i, int i2, boolean z, int i3, int i4, int i5, int i6, boolean z2, int i7, int i8, int i9, int i10, boolean z3) {
        FileLog.m16e("tmessages", "load messages in chat " + j + " count " + i + " max_id " + i2 + " cache " + z + " mindate = " + i3 + " guid " + i4 + " load_type " + i5 + " last_message_id " + i6 + " index " + i7 + " firstUnread " + i8 + " underad count " + i9 + " last_date " + i10 + " queryFromServer " + z3);
        int i11 = (int) j;
        if (z || i11 == 0) {
            MessagesStorage.getInstance().getMessages(j, i, i2, i3, i4, i5, z2, i7);
        } else if (!UserConfig.isRobot) {
            TLObject tL_messages_getHistory = new TL_messages_getHistory();
            tL_messages_getHistory.peer = getInputPeer(i11);
            if (i5 == 3) {
                tL_messages_getHistory.add_offset = (-i) / UPDATE_MASK_AVATAR;
            } else if (i5 == UPDATE_MASK_NAME) {
                tL_messages_getHistory.add_offset = (-i) - 1;
            } else if (i5 == UPDATE_MASK_AVATAR && i2 != 0) {
                tL_messages_getHistory.add_offset = (-i) + 6;
            } else if (i11 < 0 && i2 != 0 && ChatObject.isChannel(getChat(Integer.valueOf(-i11)))) {
                tL_messages_getHistory.add_offset = -1;
                tL_messages_getHistory.limit += UPDATE_MASK_NAME;
            }
            tL_messages_getHistory.limit = i;
            tL_messages_getHistory.offset_id = i2;
            if (i == 23) {
                tL_messages_getHistory.offset_date = i3;
            }
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_messages_getHistory, new AnonymousClass51(i, j, i2, i4, i8, i6, i9, i10, i5, z2, i7, z3)), i4);
        }
    }

    public void loadPeerSettings(long j, User user, Chat chat) {
        if (!this.loadingPeerSettings.containsKey(Long.valueOf(j))) {
            if (user != null || chat != null) {
                this.loadingPeerSettings.put(Long.valueOf(j), Boolean.valueOf(true));
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                if (sharedPreferences.getInt("spam3_" + j, 0) == UPDATE_MASK_NAME) {
                    return;
                }
                TLObject tL_messages_hideReportSpam;
                if (sharedPreferences.getBoolean("spam_" + j, false)) {
                    tL_messages_hideReportSpam = new TL_messages_hideReportSpam();
                    if (user != null) {
                        tL_messages_hideReportSpam.peer = getInputPeer(user.id);
                    } else if (chat != null) {
                        tL_messages_hideReportSpam.peer = getInputPeer(-chat.id);
                    }
                    ConnectionsManager.getInstance().sendRequest(tL_messages_hideReportSpam, new AnonymousClass15(j));
                } else if (!UserConfig.isRobot) {
                    tL_messages_hideReportSpam = new TL_messages_getPeerSettings();
                    if (user != null) {
                        tL_messages_hideReportSpam.peer = getInputPeer(user.id);
                    } else if (chat != null) {
                        tL_messages_hideReportSpam.peer = getInputPeer(-chat.id);
                    }
                    ConnectionsManager.getInstance().sendRequest(tL_messages_hideReportSpam, new AnonymousClass16(j));
                }
            }
        }
    }

    protected void loadUnknownChannel(Chat chat, long j) {
        NativeByteBuffer nativeByteBuffer;
        Throwable e;
        if ((chat instanceof TL_channel) && !this.gettingUnknownChannels.containsKey(Integer.valueOf(chat.id)) && !UserConfig.isRobot) {
            this.gettingUnknownChannels.put(Integer.valueOf(chat.id), Boolean.valueOf(true));
            TL_inputPeerChannel tL_inputPeerChannel = new TL_inputPeerChannel();
            tL_inputPeerChannel.channel_id = chat.id;
            tL_inputPeerChannel.access_hash = chat.access_hash;
            TLObject tL_messages_getPeerDialogs = new TL_messages_getPeerDialogs();
            tL_messages_getPeerDialogs.peers.add(tL_inputPeerChannel);
            if (j == 0) {
                try {
                    nativeByteBuffer = new NativeByteBuffer(chat.getObjectSize() + UPDATE_MASK_STATUS);
                    try {
                        nativeByteBuffer.writeInt32(0);
                        chat.serializeToStream(nativeByteBuffer);
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.m18e("tmessages", e);
                        j = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
                        ConnectionsManager.getInstance().sendRequest(tL_messages_getPeerDialogs, new AnonymousClass91(j, chat));
                    }
                } catch (Throwable e3) {
                    Throwable th = e3;
                    nativeByteBuffer = null;
                    e = th;
                    FileLog.m18e("tmessages", e);
                    j = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
                    ConnectionsManager.getInstance().sendRequest(tL_messages_getPeerDialogs, new AnonymousClass91(j, chat));
                }
                j = MessagesStorage.getInstance().createPendingTask(nativeByteBuffer);
            }
            ConnectionsManager.getInstance().sendRequest(tL_messages_getPeerDialogs, new AnonymousClass91(j, chat));
        }
    }

    public void markChannelDialogMessageAsDeleted(ArrayList<Integer> arrayList, int i) {
        MessageObject messageObject = (MessageObject) this.dialogMessage.get(Long.valueOf((long) (-i)));
        if (messageObject != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2 += UPDATE_MASK_NAME) {
                if (messageObject.getId() == ((Integer) arrayList.get(i2)).intValue()) {
                    messageObject.deleted = true;
                    return;
                }
            }
        }
    }

    public void markDialogAsRead(long j, int i, int i2, int i3, boolean z, boolean z2) {
        int i4 = (int) j;
        int i5 = (int) (j >> UPDATE_MASK_CHAT_MEMBERS);
        TLObject tL_channels_readHistory;
        if (i4 != 0) {
            if (i2 != 0 && i5 != UPDATE_MASK_NAME) {
                TLObject tLObject;
                InputPeer inputPeer = getInputPeer(i4);
                long j2 = (long) i2;
                if (inputPeer instanceof TL_inputPeerChannel) {
                    tL_channels_readHistory = new TL_channels_readHistory();
                    tL_channels_readHistory.channel = getInputChannel(-i4);
                    tL_channels_readHistory.max_id = i2;
                    j2 |= ((long) (-i4)) << UPDATE_MASK_CHAT_MEMBERS;
                    tLObject = tL_channels_readHistory;
                } else {
                    tL_channels_readHistory = new TL_messages_readHistory();
                    tL_channels_readHistory.peer = inputPeer;
                    tL_channels_readHistory.max_id = i2;
                    tLObject = tL_channels_readHistory;
                }
                Integer num = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(j));
                if (num == null) {
                    num = Integer.valueOf(0);
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(j), Integer.valueOf(Math.max(num.intValue(), i2)));
                MessagesStorage.getInstance().processPendingRead(j, j2, i3);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass62(j, z2, i2));
                if (i2 != ConnectionsManager.DEFAULT_DATACENTER_ID && !UserConfig.isRobot) {
                    ConnectionsManager.getInstance().sendRequest(tLObject, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tL_error == null && (tLObject instanceof TL_messages_affectedMessages)) {
                                TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
                                MessagesController.this.processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
                            }
                        }
                    });
                }
            }
        } else if (i3 != 0) {
            EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(i5));
            if (encryptedChat.auth_key != null && encryptedChat.auth_key.length > UPDATE_MASK_NAME && (encryptedChat instanceof TL_encryptedChat)) {
                tL_channels_readHistory = new TL_messages_readEncryptedHistory();
                tL_channels_readHistory.peer = new TL_inputEncryptedChat();
                tL_channels_readHistory.peer.chat_id = encryptedChat.id;
                tL_channels_readHistory.peer.access_hash = encryptedChat.access_hash;
                tL_channels_readHistory.max_date = i3;
                ConnectionsManager.getInstance().sendRequest(tL_channels_readHistory, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }
                });
            }
            MessagesStorage.getInstance().processPendingRead(j, (long) i, i3);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass65(j, i3, z2));
            if (encryptedChat.ttl > 0 && z) {
                int max = Math.max(ConnectionsManager.getInstance().getCurrentTime(), i3);
                MessagesStorage.getInstance().createTaskForSecretChat(encryptedChat.id, max, max, 0, null);
            }
        }
    }

    public void markMessageAsRead(long j, long j2, int i) {
        if (j2 != 0 && j != 0) {
            if (i > 0 || i == TLRPC.MESSAGE_FLAG_MEGAGROUP) {
                int i2 = (int) (j >> UPDATE_MASK_CHAT_MEMBERS);
                if (((int) j) == 0) {
                    EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(i2));
                    if (encryptedChat != null) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(Long.valueOf(j2));
                        SecretChatHelper.getInstance().sendMessagesReadMessage(encryptedChat, arrayList, null);
                        if (i > 0) {
                            int currentTime = ConnectionsManager.getInstance().getCurrentTime();
                            MessagesStorage.getInstance().createTaskForSecretChat(encryptedChat.id, currentTime, currentTime, 0, arrayList);
                        }
                    }
                }
            }
        }
    }

    public void markMessageContentAsRead(MessageObject messageObject) {
        ArrayList arrayList = new ArrayList();
        long id = (long) messageObject.getId();
        if (messageObject.messageOwner.to_id.channel_id != 0) {
            id |= ((long) messageObject.messageOwner.to_id.channel_id) << UPDATE_MASK_CHAT_MEMBERS;
        }
        arrayList.add(Long.valueOf(id));
        MessagesStorage.getInstance().markMessagesContentAsRead(arrayList);
        NotificationCenter instance = NotificationCenter.getInstance();
        int i = NotificationCenter.messagesReadContent;
        Object[] objArr = new Object[UPDATE_MASK_NAME];
        objArr[0] = arrayList;
        instance.postNotificationName(i, objArr);
        if (messageObject.getId() < 0) {
            markMessageAsRead(messageObject.getDialogId(), messageObject.messageOwner.random_id, TLRPC.MESSAGE_FLAG_MEGAGROUP);
            return;
        }
        TLObject tL_messages_readMessageContents = new TL_messages_readMessageContents();
        tL_messages_readMessageContents.id.add(Integer.valueOf(messageObject.getId()));
        tL_messages_readMessageContents.dialogId = messageObject.getDialogId();
        ConnectionsManager.getInstance().sendRequest(tL_messages_readMessageContents, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
                    MessagesController.this.processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
                }
            }
        });
    }

    public void performLogout(boolean z) {
        ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().clear().commit();
        ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putLong("lastGifLoadTime", 0).putLong("lastStickersLoadTime", 0).commit();
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("gifhint").commit();
        if (z) {
            unregistedPush();
            ConnectionsManager.getInstance().sendRequest(new TL_auth_logOut(), new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    ConnectionsManager.getInstance().cleanup();
                }
            });
        } else {
            ConnectionsManager.getInstance().cleanup();
        }
        UserConfig.clearConfig();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
        MessagesStorage.getInstance().cleanup(false);
        cleanup();
        ContactsController.getInstance().deleteAllAppAccounts();
    }

    public void pinChannelMessage(Chat chat, int i, boolean z) {
        TLObject tL_channels_updatePinnedMessage = new TL_channels_updatePinnedMessage();
        tL_channels_updatePinnedMessage.channel = getInputChannel(chat);
        tL_channels_updatePinnedMessage.id = i;
        tL_channels_updatePinnedMessage.silent = !z;
        ConnectionsManager.getInstance().sendRequest(tL_channels_updatePinnedMessage, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    MessagesController.this.processUpdates((Updates) tLObject, false);
                }
            }
        });
    }

    public void processChatInfo(int i, ChatFull chatFull, ArrayList<User> arrayList, boolean z, boolean z2, boolean z3, MessageObject messageObject) {
        if (z && i > 0 && !z3) {
            loadFullChat(i, 0, z2);
        }
        if (chatFull != null) {
            AndroidUtilities.runOnUIThread(new AnonymousClass41(arrayList, z, chatFull, z3, messageObject));
        }
    }

    public void processDialogsUpdate(messages_Dialogs com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs, ArrayList<EncryptedChat> arrayList) {
        Utilities.stageQueue.postRunnable(new AnonymousClass59(com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs));
    }

    public void processDialogsUpdateRead(HashMap<Long, Integer> hashMap) {
        AndroidUtilities.runOnUIThread(new AnonymousClass57(hashMap));
    }

    public void processLoadedBlockedUsers(ArrayList<Integer> arrayList, ArrayList<User> arrayList2, boolean z) {
        AndroidUtilities.runOnUIThread(new AnonymousClass27(arrayList2, z, arrayList));
    }

    public void processLoadedDeleteTask(int i, ArrayList<Integer> arrayList) {
        Utilities.stageQueue.postRunnable(new AnonymousClass21(arrayList, i));
    }

    public void processLoadedDialogs(messages_Dialogs com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs, ArrayList<EncryptedChat> arrayList, int i, int i2, int i3, boolean z, boolean z2) {
        Utilities.stageQueue.postRunnable(new AnonymousClass56(i3, com_hanista_mobogram_tgnet_TLRPC_messages_Dialogs, z, i2, i, arrayList, z2));
    }

    public void processLoadedMessages(messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages, long j, int i, int i2, boolean z, int i3, int i4, int i5, int i6, int i7, int i8, boolean z2, boolean z3, int i9, boolean z4) {
        FileLog.m16e("tmessages", "processLoadedMessages size " + com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size() + " in chat " + j + " count " + i + " max_id " + i2 + " cache " + z + " guid " + i3 + " load_type " + i8 + " last_message_id " + i5 + " isChannel " + z2 + " index " + i9 + " firstUnread " + i4 + " underad count " + i6 + " last_date " + i7 + " queryFromServer " + z4);
        Utilities.stageQueue.postRunnable(new AnonymousClass53(com_hanista_mobogram_tgnet_TLRPC_messages_Messages, j, z, i, i8, z4, i4, i2, i3, i5, z2, i9, i6, i7, z3));
    }

    public void processLoadedUserPhotos(photos_Photos com_hanista_mobogram_tgnet_TLRPC_photos_Photos, int i, int i2, int i3, long j, boolean z, int i4) {
        if (!z) {
            MessagesStorage.getInstance().putUsersAndChats(com_hanista_mobogram_tgnet_TLRPC_photos_Photos.users, null, true, true);
            MessagesStorage.getInstance().putDialogPhotos(i, com_hanista_mobogram_tgnet_TLRPC_photos_Photos);
        } else if (com_hanista_mobogram_tgnet_TLRPC_photos_Photos == null || com_hanista_mobogram_tgnet_TLRPC_photos_Photos.photos.isEmpty()) {
            loadDialogPhotos(i, i2, i3, j, false, i4);
            return;
        }
        AndroidUtilities.runOnUIThread(new AnonymousClass30(com_hanista_mobogram_tgnet_TLRPC_photos_Photos, z, i, i2, i3, i4));
    }

    protected void processNewChannelDifferenceParams(int i, int i2, int i3) {
        FileLog.m16e("tmessages", "processNewChannelDifferenceParams pts = " + i + " pts_count = " + i2 + " channeldId = " + i3);
        if (DialogObject.isChannel((TL_dialog) this.dialogs_dict.get(Long.valueOf((long) (-i3))))) {
            Integer num = (Integer) this.channelsPts.get(Integer.valueOf(i3));
            if (num == null) {
                num = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(i3));
                if (num.intValue() == 0) {
                    num = Integer.valueOf(UPDATE_MASK_NAME);
                }
                this.channelsPts.put(Integer.valueOf(i3), num);
            }
            if (num.intValue() + i2 == i) {
                FileLog.m16e("tmessages", "APPLY CHANNEL PTS");
                this.channelsPts.put(Integer.valueOf(i3), Integer.valueOf(i));
                MessagesStorage.getInstance().saveChannelPts(i3, i);
            } else if (num.intValue() != i) {
                Long l = (Long) this.updatesStartWaitTimeChannels.get(Integer.valueOf(i3));
                Boolean bool = (Boolean) this.gettingDifferenceChannels.get(Integer.valueOf(i3));
                if (bool == null) {
                    bool = Boolean.valueOf(false);
                }
                if (bool.booleanValue() || l == null || Math.abs(System.currentTimeMillis() - l.longValue()) <= 1500) {
                    FileLog.m16e("tmessages", "ADD CHANNEL UPDATE TO QUEUE pts = " + i + " pts_count = " + i2);
                    if (l == null) {
                        this.updatesStartWaitTimeChannels.put(Integer.valueOf(i3), Long.valueOf(System.currentTimeMillis()));
                    }
                    UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts();
                    userActionUpdatesPts.pts = i;
                    userActionUpdatesPts.pts_count = i2;
                    userActionUpdatesPts.chat_id = i3;
                    ArrayList arrayList = (ArrayList) this.updatesQueueChannels.get(Integer.valueOf(i3));
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        this.updatesQueueChannels.put(Integer.valueOf(i3), arrayList);
                    }
                    arrayList.add(userActionUpdatesPts);
                    return;
                }
                getChannelDifference(i3);
            }
        }
    }

    protected void processNewDifferenceParams(int i, int i2, int i3, int i4) {
        FileLog.m16e("tmessages", "processNewDifferenceParams seq = " + i + " pts = " + i2 + " date = " + i3 + " pts_count = " + i4);
        if (i2 != -1) {
            if (MessagesStorage.lastPtsValue + i4 == i2) {
                FileLog.m16e("tmessages", "APPLY PTS");
                MessagesStorage.lastPtsValue = i2;
                MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
            } else if (MessagesStorage.lastPtsValue != i2) {
                if (this.gettingDifference || this.updatesStartWaitTimePts == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500) {
                    FileLog.m16e("tmessages", "ADD UPDATE TO QUEUE pts = " + i2 + " pts_count = " + i4);
                    if (this.updatesStartWaitTimePts == 0) {
                        this.updatesStartWaitTimePts = System.currentTimeMillis();
                    }
                    UserActionUpdatesPts userActionUpdatesPts = new UserActionUpdatesPts();
                    userActionUpdatesPts.pts = i2;
                    userActionUpdatesPts.pts_count = i4;
                    this.updatesQueuePts.add(userActionUpdatesPts);
                } else {
                    getDifference();
                }
            }
        }
        if (i == -1) {
            return;
        }
        if (MessagesStorage.lastSeqValue + UPDATE_MASK_NAME == i) {
            FileLog.m16e("tmessages", "APPLY SEQ");
            MessagesStorage.lastSeqValue = i;
            if (i3 != -1) {
                MessagesStorage.lastDateValue = i3;
            }
            MessagesStorage.getInstance().saveDiffParams(MessagesStorage.lastSeqValue, MessagesStorage.lastPtsValue, MessagesStorage.lastDateValue, MessagesStorage.lastQtsValue);
        } else if (MessagesStorage.lastSeqValue == i) {
        } else {
            if (this.gettingDifference || this.updatesStartWaitTimeSeq == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) <= 1500) {
                FileLog.m16e("tmessages", "ADD UPDATE TO QUEUE seq = " + i);
                if (this.updatesStartWaitTimeSeq == 0) {
                    this.updatesStartWaitTimeSeq = System.currentTimeMillis();
                }
                UserActionUpdatesSeq userActionUpdatesSeq = new UserActionUpdatesSeq();
                userActionUpdatesSeq.seq = i;
                this.updatesQueueSeq.add(userActionUpdatesSeq);
                return;
            }
            getDifference();
        }
    }

    public boolean processUpdateArray(ArrayList<Update> arrayList, ArrayList<User> arrayList2, ArrayList<Chat> arrayList3, boolean z) {
        if (arrayList.isEmpty()) {
            if (!(arrayList2 == null && arrayList3 == null)) {
                AndroidUtilities.runOnUIThread(new AnonymousClass103(arrayList2, arrayList3));
            }
            return true;
        }
        AbstractMap concurrentHashMap;
        int i;
        AbstractMap abstractMap;
        AbstractMap abstractMap2;
        Object obj;
        long currentTimeMillis = System.currentTimeMillis();
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        HashMap hashMap3 = new HashMap();
        SparseArray sparseArray = new SparseArray();
        SparseArray sparseArray2 = new SparseArray();
        SparseArray sparseArray3 = new SparseArray();
        ArrayList arrayList6 = new ArrayList();
        HashMap hashMap4 = new HashMap();
        SparseArray sparseArray4 = new SparseArray();
        ArrayList arrayList7 = new ArrayList();
        ArrayList arrayList8 = new ArrayList();
        ArrayList arrayList9 = new ArrayList();
        ArrayList arrayList10 = new ArrayList();
        Object obj2 = UPDATE_MASK_NAME;
        if (arrayList2 != null) {
            concurrentHashMap = new ConcurrentHashMap();
            for (i = 0; i < arrayList2.size(); i += UPDATE_MASK_NAME) {
                User user = (User) arrayList2.get(i);
                concurrentHashMap.put(Integer.valueOf(user.id), user);
            }
            abstractMap = concurrentHashMap;
        } else {
            obj2 = null;
            abstractMap = this.users;
        }
        if (arrayList3 != null) {
            concurrentHashMap = new ConcurrentHashMap();
            for (i = 0; i < arrayList3.size(); i += UPDATE_MASK_NAME) {
                Chat chat = (Chat) arrayList3.get(i);
                concurrentHashMap.put(Integer.valueOf(chat.id), chat);
            }
            abstractMap2 = concurrentHashMap;
            obj = obj2;
        } else {
            abstractMap2 = this.chats;
            obj = null;
        }
        Object obj3 = z ? null : obj;
        if (!(arrayList2 == null && arrayList3 == null)) {
            AndroidUtilities.runOnUIThread(new AnonymousClass104(arrayList2, arrayList3));
        }
        int i2 = 0;
        int i3 = 0;
        boolean z2 = false;
        while (i2 < arrayList.size()) {
            int i4;
            int i5;
            boolean z3;
            ArrayList arrayList11;
            Update update = (Update) arrayList.get(i2);
            FileLog.m15d("tmessages", "process update " + update);
            Message message;
            Message message2;
            int i6;
            int i7;
            int i8;
            MessageEntity messageEntity;
            ConcurrentHashMap concurrentHashMap2;
            Integer num;
            if ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateNewChannelMessage)) {
                Chat chat2;
                if (update instanceof TL_updateNewMessage) {
                    message = ((TL_updateNewMessage) update).message;
                } else {
                    message2 = ((TL_updateNewChannelMessage) update).message;
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.m15d("tmessages", update + " channelId = " + message2.to_id.channel_id);
                    }
                    if (!message2.out && message2.from_id == UserConfig.getClientUserId()) {
                        message2.out = true;
                    }
                    message = message2;
                }
                i4 = 0;
                i5 = 0;
                if (message.to_id.channel_id != 0) {
                    i4 = message.to_id.channel_id;
                } else if (message.to_id.chat_id != 0) {
                    i4 = message.to_id.chat_id;
                } else if (message.to_id.user_id != 0) {
                    i5 = message.to_id.user_id;
                }
                if (i4 != 0) {
                    chat = (Chat) abstractMap2.get(Integer.valueOf(i4));
                    if (chat == null) {
                        chat = getChat(Integer.valueOf(i4));
                    }
                    if (chat == null) {
                        chat = MessagesStorage.getInstance().getChatSync(i4);
                        putChat(chat, true);
                    }
                    chat2 = chat;
                } else {
                    chat2 = null;
                }
                if (obj3 == null) {
                    i6 = i3;
                } else if (i4 == 0 || chat2 != null) {
                    int size = message.entities.size() + 3;
                    i7 = 0;
                    i4 = i3;
                    i8 = i5;
                    while (i7 < size) {
                        Object obj4 = null;
                        if (i7 == 0) {
                            i5 = i8;
                        } else if (i7 == UPDATE_MASK_NAME) {
                            i5 = message.from_id;
                            if (message.post) {
                                obj4 = UPDATE_MASK_NAME;
                            }
                        } else if (i7 == UPDATE_MASK_AVATAR) {
                            i5 = message.fwd_from != null ? message.fwd_from.from_id : 0;
                        } else {
                            messageEntity = (MessageEntity) message.entities.get(i7 - 3);
                            i5 = messageEntity instanceof TL_messageEntityMentionName ? ((TL_messageEntityMentionName) messageEntity).user_id : 0;
                        }
                        if (i5 > 0) {
                            user = (User) abstractMap.get(Integer.valueOf(i5));
                            if (user == null || (r7 == null && user.min)) {
                                user = getUser(Integer.valueOf(i5));
                            }
                            if (user == null || (r7 == null && user.min)) {
                                user = MessagesStorage.getInstance().getUserSync(i5);
                                if (user != null && r7 == null && user.min) {
                                    user = null;
                                }
                                putUser(user, true);
                            }
                            if (user == null) {
                                FileLog.m15d("tmessages", "not found user " + i5);
                                return false;
                            } else if (i7 == UPDATE_MASK_NAME && user.status != null && user.status.expires <= 0) {
                                this.onlinePrivacy.put(Integer.valueOf(i5), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                                i8 = i4 | UPDATE_MASK_STATUS;
                                i7 += UPDATE_MASK_NAME;
                                i4 = i8;
                                i8 = i5;
                            }
                        }
                        i8 = i4;
                        i7 += UPDATE_MASK_NAME;
                        i4 = i8;
                        i8 = i5;
                    }
                    i6 = i4;
                } else {
                    FileLog.m15d("tmessages", "not found chat " + i4);
                    return false;
                }
                if (chat2 != null && chat2.megagroup) {
                    message.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                }
                if (message.action instanceof TL_messageActionChatDeleteUser) {
                    user = (User) abstractMap.get(Integer.valueOf(message.action.user_id));
                    if (user != null && user.bot) {
                        message.reply_markup = new TL_replyKeyboardHide();
                    } else if (message.from_id == UserConfig.getClientUserId() && message.action.user_id == UserConfig.getClientUserId() && MoboConstants.f1341h) {
                        i = i6;
                        z3 = z2;
                    }
                }
                arrayList5.add(message);
                ImageLoader.saveMessageThumbs(message);
                i5 = UserConfig.getClientUserId();
                if (message.to_id.chat_id != 0) {
                    message.dialog_id = (long) (-message.to_id.chat_id);
                } else if (message.to_id.channel_id != 0) {
                    message.dialog_id = (long) (-message.to_id.channel_id);
                } else {
                    if (message.to_id.user_id == i5) {
                        message.to_id.user_id = message.from_id;
                    }
                    message.dialog_id = (long) message.to_id.user_id;
                }
                concurrentHashMap2 = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                num = (Integer) concurrentHashMap2.get(Long.valueOf(message.dialog_id));
                if (num == null) {
                    num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message.out, message.dialog_id));
                    concurrentHashMap2.put(Long.valueOf(message.dialog_id), num);
                }
                boolean z4 = num.intValue() < message.id && !((chat2 != null && ChatObject.isNotInChat(chat2)) || (message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate));
                message.unread = z4;
                if (message.dialog_id == ((long) i5)) {
                    message.unread = false;
                    message.media_unread = false;
                    message.out = true;
                }
                MessageObject messageObject = new MessageObject(message, abstractMap, abstractMap2, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                i4 = messageObject.type == 11 ? i6 | UPDATE_MASK_CHAT_AVATAR : messageObject.type == 10 ? i6 | UPDATE_MASK_CHAT_NAME : i6;
                arrayList11 = (ArrayList) hashMap.get(Long.valueOf(message.dialog_id));
                if (arrayList11 == null) {
                    arrayList11 = new ArrayList();
                    hashMap.put(Long.valueOf(message.dialog_id), arrayList11);
                }
                arrayList11.add(messageObject);
                if (!messageObject.isOut() && messageObject.isUnread()) {
                    arrayList4.add(messageObject);
                }
                i = i4;
                z3 = z2;
            } else if (update instanceof TL_updateReadMessagesContents) {
                for (i = 0; i < update.messages.size(); i += UPDATE_MASK_NAME) {
                    arrayList6.add(Long.valueOf((long) ((Integer) update.messages.get(i)).intValue()));
                }
                i = i3;
                z3 = z2;
            } else if ((update instanceof TL_updateReadHistoryInbox) || (update instanceof TL_updateReadHistoryOutbox)) {
                Peer peer;
                if (update instanceof TL_updateReadHistoryInbox) {
                    peer = ((TL_updateReadHistoryInbox) update).peer;
                    if (peer.chat_id != 0) {
                        sparseArray2.put(-peer.chat_id, Long.valueOf((long) update.max_id));
                        r4 = (long) (-peer.chat_id);
                    } else {
                        sparseArray2.put(peer.user_id, Long.valueOf((long) update.max_id));
                        r4 = (long) peer.user_id;
                    }
                    r6 = r4;
                    r5 = this.dialogs_read_inbox_max;
                } else {
                    peer = ((TL_updateReadHistoryOutbox) update).peer;
                    if (peer.chat_id != 0) {
                        sparseArray3.put(-peer.chat_id, Long.valueOf((long) update.max_id));
                        r4 = (long) (-peer.chat_id);
                    } else {
                        sparseArray3.put(peer.user_id, Long.valueOf((long) update.max_id));
                        r4 = (long) peer.user_id;
                    }
                    r6 = r4;
                    r5 = this.dialogs_read_outbox_max;
                }
                num = (Integer) r5.get(Long.valueOf(r6));
                if (num == null) {
                    num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(update instanceof TL_updateReadHistoryOutbox, r6));
                }
                r5.put(Long.valueOf(r6), Integer.valueOf(Math.max(num.intValue(), update.max_id)));
                i = i3;
                z3 = z2;
            } else if (update instanceof TL_updateDeleteMessages) {
                arrayList11 = (ArrayList) sparseArray4.get(0);
                if (arrayList11 == null) {
                    arrayList11 = new ArrayList();
                    sparseArray4.put(0, arrayList11);
                }
                arrayList11.addAll(update.messages);
                i = i3;
                z3 = z2;
            } else {
                ArrayList arrayList12;
                Iterator it;
                PrintingUser printingUser;
                int i9;
                if ((update instanceof TL_updateUserTyping) || (update instanceof TL_updateChatUserTyping)) {
                    if (update.user_id != UserConfig.getClientUserId()) {
                        r4 = (long) (-update.chat_id);
                        r6 = r4 == 0 ? (long) update.user_id : r4;
                        arrayList11 = (ArrayList) this.printingUsers.get(Long.valueOf(r6));
                        if (!(update.action instanceof TL_sendMessageCancelAction)) {
                            if (arrayList11 == null) {
                                arrayList11 = new ArrayList();
                                this.printingUsers.put(Long.valueOf(r6), arrayList11);
                                arrayList12 = arrayList11;
                            } else {
                                arrayList12 = arrayList11;
                            }
                            it = arrayList12.iterator();
                            while (it.hasNext()) {
                                printingUser = (PrintingUser) it.next();
                                i5 = printingUser.userId;
                                i9 = update.user_id;
                                if (i5 == r0) {
                                    printingUser.lastTime = currentTimeMillis;
                                    if (printingUser.action.getClass() != update.action.getClass()) {
                                        z2 = true;
                                    }
                                    printingUser.action = update.action;
                                    obj = UPDATE_MASK_NAME;
                                    if (obj == null) {
                                        printingUser = new PrintingUser();
                                        printingUser.userId = update.user_id;
                                        printingUser.lastTime = currentTimeMillis;
                                        printingUser.action = update.action;
                                        arrayList12.add(printingUser);
                                        z2 = true;
                                    }
                                }
                            }
                            obj = null;
                            if (obj == null) {
                                printingUser = new PrintingUser();
                                printingUser.userId = update.user_id;
                                printingUser.lastTime = currentTimeMillis;
                                printingUser.action = update.action;
                                arrayList12.add(printingUser);
                                z2 = true;
                            }
                        } else if (arrayList11 != null) {
                            for (i5 = 0; i5 < arrayList11.size(); i5 += UPDATE_MASK_NAME) {
                                i = ((PrintingUser) arrayList11.get(i5)).userId;
                                i9 = update.user_id;
                                if (i == r0) {
                                    arrayList11.remove(i5);
                                    z2 = true;
                                    break;
                                }
                            }
                            if (arrayList11.isEmpty()) {
                                this.printingUsers.remove(Long.valueOf(r6));
                            }
                        }
                        this.onlinePrivacy.put(Integer.valueOf(update.user_id), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                        i = i3;
                        z3 = z2;
                    }
                } else if (update instanceof TL_updateChatParticipants) {
                    i3 |= UPDATE_MASK_CHAT_MEMBERS;
                    arrayList7.add(update.participants);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateUserStatus) {
                    i3 |= UPDATE_MASK_STATUS;
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateUserName) {
                    i3 |= UPDATE_MASK_NAME;
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateUserPhoto) {
                    i3 |= UPDATE_MASK_AVATAR;
                    MessagesStorage.getInstance().clearUserPhotos(update.user_id);
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateUserPhone) {
                    i3 |= UPDATE_MASK_PHONE;
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateContactRegistered) {
                    if (this.enableJoined) {
                        if (abstractMap.containsKey(Integer.valueOf(update.user_id)) && !MessagesStorage.getInstance().isDialogHasMessages((long) update.user_id)) {
                            message = new TL_messageService();
                            message.action = new TL_messageActionUserJoined();
                            i8 = UserConfig.getNewMessageId();
                            message.id = i8;
                            message.local_id = i8;
                            UserConfig.saveConfig(false);
                            message.unread = false;
                            message.flags = UPDATE_MASK_READ_DIALOG_MESSAGE;
                            message.date = update.date;
                            message.from_id = update.user_id;
                            message.to_id = new TL_peerUser();
                            message.to_id.user_id = UserConfig.getClientUserId();
                            message.dialog_id = (long) update.user_id;
                            arrayList5.add(message);
                            r6 = new MessageObject(message, abstractMap, abstractMap2, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                            arrayList11 = (ArrayList) hashMap.get(Long.valueOf(message.dialog_id));
                            if (arrayList11 == null) {
                                arrayList11 = new ArrayList();
                                hashMap.put(Long.valueOf(message.dialog_id), arrayList11);
                            }
                            arrayList11.add(r6);
                            i = i3;
                            z3 = z2;
                        }
                    }
                } else if (update instanceof TL_updateContactLink) {
                    if (update.my_link instanceof TL_contactLinkContact) {
                        i8 = arrayList10.indexOf(Integer.valueOf(-update.user_id));
                        if (i8 != -1) {
                            arrayList10.remove(i8);
                        }
                        if (!arrayList10.contains(Integer.valueOf(update.user_id))) {
                            arrayList10.add(Integer.valueOf(update.user_id));
                        }
                    } else {
                        i8 = arrayList10.indexOf(Integer.valueOf(update.user_id));
                        if (i8 != -1) {
                            arrayList10.remove(i8);
                        }
                        if (!arrayList10.contains(Integer.valueOf(update.user_id))) {
                            arrayList10.add(Integer.valueOf(-update.user_id));
                        }
                    }
                    new UpdateBiz().insertUpdate(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateNewAuthorization) {
                    if (!MessagesStorage.getInstance().hasAuthMessage(update.date)) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.newSessionReceived, new Object[0]);
                            }
                        });
                        message = new TL_messageService();
                        message.action = new TL_messageActionLoginUnknownLocation();
                        message.action.title = update.device;
                        message.action.address = update.location;
                        i8 = UserConfig.getNewMessageId();
                        message.id = i8;
                        message.local_id = i8;
                        UserConfig.saveConfig(false);
                        message.unread = true;
                        message.flags = UPDATE_MASK_READ_DIALOG_MESSAGE;
                        message.date = update.date;
                        message.from_id = 777000;
                        message.to_id = new TL_peerUser();
                        message.to_id.user_id = UserConfig.getClientUserId();
                        message.dialog_id = 777000;
                        arrayList5.add(message);
                        r6 = new MessageObject(message, abstractMap, abstractMap2, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                        arrayList11 = (ArrayList) hashMap.get(Long.valueOf(message.dialog_id));
                        if (arrayList11 == null) {
                            arrayList11 = new ArrayList();
                            hashMap.put(Long.valueOf(message.dialog_id), arrayList11);
                        }
                        arrayList11.add(r6);
                        arrayList4.add(r6);
                        i = i3;
                        z3 = z2;
                    }
                } else if (update instanceof TL_updateNewGeoChatMessage) {
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateNewEncryptedMessage) {
                    ArrayList decryptMessage = SecretChatHelper.getInstance().decryptMessage(((TL_updateNewEncryptedMessage) update).message);
                    if (!(decryptMessage == null || decryptMessage.isEmpty())) {
                        long j = ((long) ((TL_updateNewEncryptedMessage) update).message.chat_id) << UPDATE_MASK_CHAT_MEMBERS;
                        arrayList11 = (ArrayList) hashMap.get(Long.valueOf(j));
                        if (arrayList11 == null) {
                            arrayList11 = new ArrayList();
                            hashMap.put(Long.valueOf(j), arrayList11);
                            arrayList12 = arrayList11;
                        } else {
                            arrayList12 = arrayList11;
                        }
                        for (i4 = 0; i4 < decryptMessage.size(); i4 += UPDATE_MASK_NAME) {
                            message2 = (Message) decryptMessage.get(i4);
                            ImageLoader.saveMessageThumbs(message2);
                            arrayList5.add(message2);
                            MessageObject messageObject2 = new MessageObject(message2, abstractMap, abstractMap2, this.createdDialogIds.contains(Long.valueOf(j)));
                            arrayList12.add(messageObject2);
                            arrayList4.add(messageObject2);
                        }
                    }
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateEncryptedChatTyping) {
                    EncryptedChat encryptedChatDB = getEncryptedChatDB(update.chat_id);
                    if (encryptedChatDB != null) {
                        update.user_id = encryptedChatDB.user_id;
                        r6 = ((long) update.chat_id) << UPDATE_MASK_CHAT_MEMBERS;
                        arrayList11 = (ArrayList) this.printingUsers.get(Long.valueOf(r6));
                        if (arrayList11 == null) {
                            arrayList11 = new ArrayList();
                            this.printingUsers.put(Long.valueOf(r6), arrayList11);
                            arrayList12 = arrayList11;
                        } else {
                            arrayList12 = arrayList11;
                        }
                        it = arrayList12.iterator();
                        while (it.hasNext()) {
                            printingUser = (PrintingUser) it.next();
                            i5 = printingUser.userId;
                            i9 = update.user_id;
                            if (i5 == r0) {
                                printingUser.lastTime = currentTimeMillis;
                                printingUser.action = new TL_sendMessageTypingAction();
                                obj = UPDATE_MASK_NAME;
                                break;
                            }
                        }
                        obj = null;
                        if (obj == null) {
                            printingUser = new PrintingUser();
                            printingUser.userId = update.user_id;
                            printingUser.lastTime = currentTimeMillis;
                            printingUser.action = new TL_sendMessageTypingAction();
                            arrayList12.add(printingUser);
                            z2 = true;
                        }
                        this.onlinePrivacy.put(Integer.valueOf(update.user_id), Integer.valueOf(ConnectionsManager.getInstance().getCurrentTime()));
                    }
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateEncryptedMessagesRead) {
                    hashMap4.put(Integer.valueOf(update.chat_id), Integer.valueOf(Math.max(update.max_date, update.date)));
                    arrayList9.add((TL_updateEncryptedMessagesRead) update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateChatParticipantAdd) {
                    MessagesStorage.getInstance().updateChatInfo(update.chat_id, update.user_id, 0, update.inviter_id, update.version);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateChatParticipantDelete) {
                    MessagesStorage.getInstance().updateChatInfo(update.chat_id, update.user_id, UPDATE_MASK_NAME, 0, update.version);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateDcOptions) {
                    ConnectionsManager.getInstance().updateDcSettings();
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateEncryption) {
                    SecretChatHelper.getInstance().processUpdateEncryption((TL_updateEncryption) update, abstractMap);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateUserBlocked) {
                    TL_updateUserBlocked tL_updateUserBlocked = (TL_updateUserBlocked) update;
                    if (tL_updateUserBlocked.blocked) {
                        arrayList11 = new ArrayList();
                        arrayList11.add(Integer.valueOf(tL_updateUserBlocked.user_id));
                        MessagesStorage.getInstance().putBlockedUsers(arrayList11, false);
                    } else {
                        MessagesStorage.getInstance().deleteBlockedUser(tL_updateUserBlocked.user_id);
                    }
                    MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass106(tL_updateUserBlocked));
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateNotifySettings) {
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateServiceNotification) {
                    TL_updateServiceNotification tL_updateServiceNotification = (TL_updateServiceNotification) update;
                    if (tL_updateServiceNotification.popup && tL_updateServiceNotification.message != null && tL_updateServiceNotification.message.length() > 0) {
                        NotificationCenter instance = NotificationCenter.getInstance();
                        i4 = NotificationCenter.needShowAlert;
                        Object[] objArr = new Object[UPDATE_MASK_AVATAR];
                        objArr[0] = Integer.valueOf(UPDATE_MASK_AVATAR);
                        objArr[UPDATE_MASK_NAME] = tL_updateServiceNotification.message;
                        instance.postNotificationName(i4, objArr);
                    }
                    message = new TL_message();
                    i4 = UserConfig.getNewMessageId();
                    message.id = i4;
                    message.local_id = i4;
                    UserConfig.saveConfig(false);
                    message.unread = true;
                    message.flags = UPDATE_MASK_READ_DIALOG_MESSAGE;
                    message.date = ConnectionsManager.getInstance().getCurrentTime();
                    message.from_id = 777000;
                    message.to_id = new TL_peerUser();
                    message.to_id.user_id = UserConfig.getClientUserId();
                    message.dialog_id = 777000;
                    message.media = update.media;
                    message.flags |= UPDATE_MASK_SELECT_DIALOG;
                    message.message = tL_updateServiceNotification.message;
                    arrayList5.add(message);
                    r6 = new MessageObject(message, abstractMap, abstractMap2, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                    arrayList11 = (ArrayList) hashMap.get(Long.valueOf(message.dialog_id));
                    if (arrayList11 == null) {
                        arrayList11 = new ArrayList();
                        hashMap.put(Long.valueOf(message.dialog_id), arrayList11);
                    }
                    arrayList11.add(r6);
                    arrayList4.add(r6);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updatePrivacy) {
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateWebPage) {
                    hashMap2.put(Long.valueOf(update.webpage.id), update.webpage);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateChannelTooLong) {
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.m15d("tmessages", update + " channelId = " + update.channel_id);
                    }
                    num = (Integer) this.channelsPts.get(Integer.valueOf(update.channel_id));
                    if (num == null) {
                        Integer valueOf = Integer.valueOf(MessagesStorage.getInstance().getChannelPtsSync(update.channel_id));
                        if (valueOf.intValue() == 0) {
                            chat = (Chat) abstractMap2.get(Integer.valueOf(update.channel_id));
                            if (chat == null || chat.min) {
                                chat = getChat(Integer.valueOf(update.channel_id));
                            }
                            if (chat == null || chat.min) {
                                chat = MessagesStorage.getInstance().getChatSync(update.channel_id);
                                putChat(chat, true);
                            }
                            if (!(chat == null || chat.min)) {
                                loadUnknownChannel(chat, 0);
                            }
                            num = valueOf;
                        } else {
                            this.channelsPts.put(Integer.valueOf(update.channel_id), valueOf);
                            num = valueOf;
                        }
                    }
                    if (num.intValue() != 0) {
                        if ((update.flags & UPDATE_MASK_NAME) == 0) {
                            getChannelDifference(update.channel_id);
                        } else if (update.pts > num.intValue()) {
                            getChannelDifference(update.channel_id);
                        }
                    }
                    i = i3;
                    z3 = z2;
                } else if ((update instanceof TL_updateReadChannelInbox) || (update instanceof TL_updateReadChannelOutbox)) {
                    r6 = (((long) update.channel_id) << UPDATE_MASK_CHAT_MEMBERS) | ((long) update.max_id);
                    long j2 = (long) (-update.channel_id);
                    ConcurrentHashMap concurrentHashMap3;
                    if (update instanceof TL_updateReadChannelInbox) {
                        concurrentHashMap3 = this.dialogs_read_inbox_max;
                        sparseArray2.put(-update.channel_id, Long.valueOf(r6));
                        r5 = concurrentHashMap3;
                    } else {
                        concurrentHashMap3 = this.dialogs_read_outbox_max;
                        sparseArray3.put(-update.channel_id, Long.valueOf(r6));
                        r5 = concurrentHashMap3;
                    }
                    num = (Integer) r5.get(Long.valueOf(j2));
                    if (num == null) {
                        num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(update instanceof TL_updateReadChannelOutbox, j2));
                    }
                    r5.put(Long.valueOf(j2), Integer.valueOf(Math.max(num.intValue(), update.max_id)));
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateDeleteChannelMessages) {
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.m15d("tmessages", update + " channelId = " + update.channel_id);
                    }
                    arrayList11 = (ArrayList) sparseArray4.get(update.channel_id);
                    if (arrayList11 == null) {
                        arrayList11 = new ArrayList();
                        sparseArray4.put(update.channel_id, arrayList11);
                    }
                    arrayList11.addAll(update.messages);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateChannel) {
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.m15d("tmessages", update + " channelId = " + update.channel_id);
                    }
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateChannelMessageViews) {
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.m15d("tmessages", update + " channelId = " + update.channel_id);
                    }
                    TL_updateChannelMessageViews tL_updateChannelMessageViews = (TL_updateChannelMessageViews) update;
                    SparseIntArray sparseIntArray = (SparseIntArray) sparseArray.get(update.channel_id);
                    if (sparseIntArray == null) {
                        sparseIntArray = new SparseIntArray();
                        sparseArray.put(update.channel_id, sparseIntArray);
                    }
                    sparseIntArray.put(tL_updateChannelMessageViews.id, update.views);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateChatParticipantAdmin) {
                    MessagesStorage.getInstance().updateChatInfo(update.chat_id, update.user_id, UPDATE_MASK_AVATAR, update.is_admin ? UPDATE_MASK_NAME : 0, update.version);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateChatAdmins) {
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateStickerSets) {
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateStickerSetsOrder) {
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateNewStickerSet) {
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateDraftMessage) {
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateSavedGifs) {
                    arrayList8.add(update);
                    i = i3;
                    z3 = z2;
                } else if ((update instanceof TL_updateEditChannelMessage) || (update instanceof TL_updateEditMessage)) {
                    i6 = UserConfig.getClientUserId();
                    if (update instanceof TL_updateEditChannelMessage) {
                        message = ((TL_updateEditChannelMessage) update).message;
                        chat = (Chat) abstractMap2.get(Integer.valueOf(message.to_id.channel_id));
                        if (chat == null) {
                            chat = getChat(Integer.valueOf(message.to_id.channel_id));
                        }
                        if (chat == null) {
                            chat = MessagesStorage.getInstance().getChatSync(message.to_id.channel_id);
                            putChat(chat, true);
                        }
                        if (chat != null && chat.megagroup) {
                            message.flags |= TLRPC.MESSAGE_FLAG_MEGAGROUP;
                        }
                    } else {
                        message2 = ((TL_updateEditMessage) update).message;
                        if (message2.dialog_id == ((long) i6)) {
                            message2.unread = false;
                            message2.media_unread = false;
                            message2.out = true;
                        }
                        message = message2;
                    }
                    if (!message.out && message.from_id == UserConfig.getClientUserId()) {
                        message.out = true;
                    }
                    if (!z) {
                        i5 = message.entities.size();
                        for (i4 = 0; i4 < i5; i4 += UPDATE_MASK_NAME) {
                            messageEntity = (MessageEntity) message.entities.get(i4);
                            if (messageEntity instanceof TL_messageEntityMentionName) {
                                i7 = ((TL_messageEntityMentionName) messageEntity).user_id;
                                user = (User) abstractMap.get(Integer.valueOf(i7));
                                if (user == null || user.min) {
                                    user = getUser(Integer.valueOf(i7));
                                }
                                if (user == null || user.min) {
                                    user = MessagesStorage.getInstance().getUserSync(i7);
                                    if (user != null && user.min) {
                                        user = null;
                                    }
                                    putUser(user, true);
                                }
                                if (user == null) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (message.to_id.chat_id != 0) {
                        message.dialog_id = (long) (-message.to_id.chat_id);
                    } else if (message.to_id.channel_id != 0) {
                        message.dialog_id = (long) (-message.to_id.channel_id);
                    } else {
                        if (message.to_id.user_id == UserConfig.getClientUserId()) {
                            message.to_id.user_id = message.from_id;
                        }
                        message.dialog_id = (long) message.to_id.user_id;
                    }
                    concurrentHashMap2 = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                    num = (Integer) concurrentHashMap2.get(Long.valueOf(message.dialog_id));
                    if (num == null) {
                        num = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message.out, message.dialog_id));
                        concurrentHashMap2.put(Long.valueOf(message.dialog_id), num);
                    }
                    message.unread = num.intValue() < message.id;
                    if (message.dialog_id == ((long) i6)) {
                        message.out = true;
                        message.unread = false;
                        message.media_unread = false;
                    }
                    if (message.out && (message.message == null || message.message.length() == 0)) {
                        message.message = "-1";
                        message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    ImageLoader.saveMessageThumbs(message);
                    r6 = new MessageObject(message, abstractMap, abstractMap2, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                    arrayList11 = (ArrayList) hashMap3.get(Long.valueOf(message.dialog_id));
                    if (arrayList11 == null) {
                        arrayList11 = new ArrayList();
                        hashMap3.put(Long.valueOf(message.dialog_id), arrayList11);
                    }
                    arrayList11.add(r6);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateChannelPinnedMessage) {
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.m15d("tmessages", update + " channelId = " + update.channel_id);
                    }
                    MessagesStorage.getInstance().updateChannelPinnedMessage(update.channel_id, ((TL_updateChannelPinnedMessage) update).id);
                    i = i3;
                    z3 = z2;
                } else if (update instanceof TL_updateReadFeaturedStickers) {
                    arrayList8.add(update);
                }
                i = i3;
                z3 = z2;
            }
            i2 += UPDATE_MASK_NAME;
            i3 = i;
            z2 = z3;
        }
        MoboConstants.f1341h = true;
        if (!hashMap.isEmpty()) {
            for (Entry entry : hashMap.entrySet()) {
                if (updatePrintingUsersWithNewMessages(((Long) entry.getKey()).longValue(), (ArrayList) entry.getValue())) {
                    z2 = true;
                }
            }
        }
        if (z2) {
            updatePrintingStrings();
        }
        if (!arrayList10.isEmpty()) {
            ContactsController.getInstance().processContactsUpdates(arrayList10, abstractMap);
        }
        if (!arrayList4.isEmpty()) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass107(arrayList4));
        }
        if (!arrayList5.isEmpty()) {
            MessagesStorage.getInstance().putMessages(arrayList5, true, true, false, MediaController.m71a().m167c());
        }
        if (!hashMap3.isEmpty()) {
            for (Entry entry2 : hashMap3.entrySet()) {
                messages_Messages tL_messages_messages = new TL_messages_messages();
                arrayList11 = (ArrayList) entry2.getValue();
                for (i5 = 0; i5 < arrayList11.size(); i5 += UPDATE_MASK_NAME) {
                    tL_messages_messages.messages.add(((MessageObject) arrayList11.get(i5)).messageOwner);
                }
                MessagesStorage.getInstance().putMessages(tL_messages_messages, ((Long) entry2.getKey()).longValue(), -2, 0, false);
            }
        }
        if (sparseArray.size() != 0) {
            MessagesStorage.getInstance().putChannelViews(sparseArray, true);
        }
        AndroidUtilities.runOnUIThread(new AnonymousClass108(i3, arrayList8, hashMap2, hashMap, hashMap3, z2, arrayList10, arrayList7, sparseArray));
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new AnonymousClass109(sparseArray2, sparseArray3, hashMap4, arrayList6, sparseArray4));
        if (!hashMap2.isEmpty()) {
            MessagesStorage.getInstance().putWebPages(hashMap2);
        }
        if (!(sparseArray2.size() == 0 && sparseArray3.size() == 0 && hashMap4.isEmpty())) {
            if (sparseArray2.size() != 0) {
                MessagesStorage.getInstance().updateDialogsWithReadMessages(sparseArray2, sparseArray3, true);
            }
            MessagesStorage.getInstance().markMessagesAsRead(sparseArray2, sparseArray3, hashMap4, true);
        }
        if (!arrayList6.isEmpty()) {
            MessagesStorage.getInstance().markMessagesContentAsRead(arrayList6);
        }
        if (sparseArray4.size() != 0) {
            for (i = 0; i < sparseArray4.size(); i += UPDATE_MASK_NAME) {
                i4 = sparseArray4.keyAt(i);
                arrayList11 = (ArrayList) sparseArray4.get(i4);
                MessagesStorage.getInstance().markMessagesAsDeleted(arrayList11, true, i4, false);
                MessagesStorage.getInstance().updateDialogsWithDeletedMessages(arrayList11, true, i4);
            }
        }
        if (!arrayList9.isEmpty()) {
            for (i2 = 0; i2 < arrayList9.size(); i2 += UPDATE_MASK_NAME) {
                TL_updateEncryptedMessagesRead tL_updateEncryptedMessagesRead = (TL_updateEncryptedMessagesRead) arrayList9.get(i2);
                MessagesStorage.getInstance().createTaskForSecretChat(tL_updateEncryptedMessagesRead.chat_id, tL_updateEncryptedMessagesRead.max_date, tL_updateEncryptedMessagesRead.date, UPDATE_MASK_NAME, null);
            }
        }
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processUpdates(com.hanista.mobogram.tgnet.TLRPC.Updates r18, boolean r19) {
        /*
        r17 = this;
        r11 = 0;
        r9 = 0;
        r10 = 0;
        r12 = 0;
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updateShort;
        if (r2 == 0) goto L_0x0058;
    L_0x000a:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r0 = r18;
        r3 = r0.update;
        r2.add(r3);
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r0 = r17;
        r0.processUpdateArray(r2, r3, r4, r5);
        r3 = r12;
    L_0x001f:
        r2 = com.hanista.mobogram.messenger.SecretChatHelper.getInstance();
        r2.processPendingEncMessages();
        if (r19 != 0) goto L_0x0b3d;
    L_0x0028:
        r5 = new java.util.ArrayList;
        r0 = r17;
        r2 = r0.updatesQueueChannels;
        r2 = r2.keySet();
        r5.<init>(r2);
        r2 = 0;
        r4 = r2;
    L_0x0037:
        r2 = r5.size();
        if (r4 >= r2) goto L_0x0b38;
    L_0x003d:
        r2 = r5.get(r4);
        r2 = (java.lang.Integer) r2;
        if (r11 == 0) goto L_0x0b2c;
    L_0x0045:
        r6 = r11.contains(r2);
        if (r6 == 0) goto L_0x0b2c;
    L_0x004b:
        r2 = r2.intValue();
        r0 = r17;
        r0.getChannelDifference(r2);
    L_0x0054:
        r2 = r4 + 1;
        r4 = r2;
        goto L_0x0037;
    L_0x0058:
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updateShortChatMessage;
        if (r2 != 0) goto L_0x0064;
    L_0x005e:
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updateShortMessage;
        if (r2 == 0) goto L_0x042f;
    L_0x0064:
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updateShortChatMessage;
        if (r2 == 0) goto L_0x01c5;
    L_0x006a:
        r0 = r18;
        r2 = r0.from_id;
        r4 = r2;
    L_0x006f:
        r2 = java.lang.Integer.valueOf(r4);
        r0 = r17;
        r2 = r0.getUser(r2);
        r5 = 0;
        r8 = 0;
        r6 = 0;
        if (r2 == 0) goto L_0x0082;
    L_0x007e:
        r3 = r2.min;
        if (r3 == 0) goto L_0x0097;
    L_0x0082:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r2 = r2.getUserSync(r4);
        if (r2 == 0) goto L_0x0091;
    L_0x008c:
        r3 = r2.min;
        if (r3 == 0) goto L_0x0091;
    L_0x0090:
        r2 = 0;
    L_0x0091:
        r3 = 1;
        r0 = r17;
        r0.putUser(r2, r3);
    L_0x0097:
        r3 = r2;
        r2 = 0;
        r0 = r18;
        r7 = r0.fwd_from;
        if (r7 == 0) goto L_0x0b9f;
    L_0x009f:
        r0 = r18;
        r7 = r0.fwd_from;
        r7 = r7.from_id;
        if (r7 == 0) goto L_0x00d3;
    L_0x00a7:
        r0 = r18;
        r2 = r0.fwd_from;
        r2 = r2.from_id;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r17;
        r2 = r0.getUser(r2);
        if (r2 != 0) goto L_0x00cd;
    L_0x00b9:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r0 = r18;
        r5 = r0.fwd_from;
        r5 = r5.from_id;
        r2 = r2.getUserSync(r5);
        r5 = 1;
        r0 = r17;
        r0.putUser(r2, r5);
    L_0x00cd:
        r5 = 1;
        r16 = r5;
        r5 = r2;
        r2 = r16;
    L_0x00d3:
        r0 = r18;
        r7 = r0.fwd_from;
        r7 = r7.channel_id;
        if (r7 == 0) goto L_0x0b98;
    L_0x00db:
        r0 = r18;
        r2 = r0.fwd_from;
        r2 = r2.channel_id;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r17;
        r2 = r0.getChat(r2);
        if (r2 != 0) goto L_0x0101;
    L_0x00ed:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r0 = r18;
        r6 = r0.fwd_from;
        r6 = r6.channel_id;
        r2 = r2.getChatSync(r6);
        r6 = 1;
        r0 = r17;
        r0.putChat(r2, r6);
    L_0x0101:
        r6 = 1;
        r16 = r6;
        r6 = r5;
        r5 = r2;
        r2 = r16;
    L_0x0108:
        r7 = 0;
        r0 = r18;
        r13 = r0.via_bot_id;
        if (r13 == 0) goto L_0x0137;
    L_0x010f:
        r0 = r18;
        r7 = r0.via_bot_id;
        r7 = java.lang.Integer.valueOf(r7);
        r0 = r17;
        r7 = r0.getUser(r7);
        if (r7 != 0) goto L_0x0131;
    L_0x011f:
        r7 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r0 = r18;
        r8 = r0.via_bot_id;
        r7 = r7.getUserSync(r8);
        r8 = 1;
        r0 = r17;
        r0.putUser(r7, r8);
    L_0x0131:
        r8 = 1;
        r16 = r8;
        r8 = r7;
        r7 = r16;
    L_0x0137:
        r0 = r18;
        r13 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updateShortMessage;
        if (r13 == 0) goto L_0x01cf;
    L_0x013d:
        if (r3 == 0) goto L_0x0149;
    L_0x013f:
        if (r2 == 0) goto L_0x0145;
    L_0x0141:
        if (r6 != 0) goto L_0x0145;
    L_0x0143:
        if (r5 == 0) goto L_0x0149;
    L_0x0145:
        if (r7 == 0) goto L_0x01cc;
    L_0x0147:
        if (r8 != 0) goto L_0x01cc;
    L_0x0149:
        r2 = 1;
    L_0x014a:
        r6 = r2;
    L_0x014b:
        if (r6 != 0) goto L_0x0b95;
    L_0x014d:
        r0 = r18;
        r2 = r0.entities;
        r2 = r2.isEmpty();
        if (r2 != 0) goto L_0x0b95;
    L_0x0157:
        r2 = 0;
        r5 = r2;
    L_0x0159:
        r0 = r18;
        r2 = r0.entities;
        r2 = r2.size();
        if (r5 >= r2) goto L_0x0b95;
    L_0x0163:
        r0 = r18;
        r2 = r0.entities;
        r2 = r2.get(r5);
        r2 = (com.hanista.mobogram.tgnet.TLRPC.MessageEntity) r2;
        r7 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r7 == 0) goto L_0x020b;
    L_0x0171:
        r2 = (com.hanista.mobogram.tgnet.TLRPC.TL_messageEntityMentionName) r2;
        r2 = r2.user_id;
        r7 = java.lang.Integer.valueOf(r2);
        r0 = r17;
        r7 = r0.getUser(r7);
        if (r7 == 0) goto L_0x0185;
    L_0x0181:
        r7 = r7.min;
        if (r7 == 0) goto L_0x020b;
    L_0x0185:
        r7 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r2 = r7.getUserSync(r2);
        if (r2 == 0) goto L_0x0194;
    L_0x018f:
        r7 = r2.min;
        if (r7 == 0) goto L_0x0194;
    L_0x0193:
        r2 = 0;
    L_0x0194:
        if (r2 != 0) goto L_0x0205;
    L_0x0196:
        r2 = 1;
    L_0x0197:
        if (r3 == 0) goto L_0x0b92;
    L_0x0199:
        r5 = r3.status;
        if (r5 == 0) goto L_0x0b92;
    L_0x019d:
        r5 = r3.status;
        r5 = r5.expires;
        if (r5 > 0) goto L_0x0b92;
    L_0x01a3:
        r0 = r17;
        r5 = r0.onlinePrivacy;
        r3 = r3.id;
        r3 = java.lang.Integer.valueOf(r3);
        r6 = com.hanista.mobogram.tgnet.ConnectionsManager.getInstance();
        r6 = r6.getCurrentTime();
        r6 = java.lang.Integer.valueOf(r6);
        r5.put(r3, r6);
        r12 = 1;
        r8 = r12;
    L_0x01be:
        if (r2 == 0) goto L_0x0210;
    L_0x01c0:
        r2 = 1;
    L_0x01c1:
        r3 = r8;
        r9 = r2;
        goto L_0x001f;
    L_0x01c5:
        r0 = r18;
        r2 = r0.user_id;
        r4 = r2;
        goto L_0x006f;
    L_0x01cc:
        r2 = 0;
        goto L_0x014a;
    L_0x01cf:
        r0 = r18;
        r13 = r0.chat_id;
        r13 = java.lang.Integer.valueOf(r13);
        r0 = r17;
        r13 = r0.getChat(r13);
        if (r13 != 0) goto L_0x01f1;
    L_0x01df:
        r13 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r0 = r18;
        r14 = r0.chat_id;
        r13 = r13.getChatSync(r14);
        r14 = 1;
        r0 = r17;
        r0.putChat(r13, r14);
    L_0x01f1:
        if (r13 == 0) goto L_0x01ff;
    L_0x01f3:
        if (r3 == 0) goto L_0x01ff;
    L_0x01f5:
        if (r2 == 0) goto L_0x01fb;
    L_0x01f7:
        if (r6 != 0) goto L_0x01fb;
    L_0x01f9:
        if (r5 == 0) goto L_0x01ff;
    L_0x01fb:
        if (r7 == 0) goto L_0x0203;
    L_0x01fd:
        if (r8 != 0) goto L_0x0203;
    L_0x01ff:
        r2 = 1;
    L_0x0200:
        r6 = r2;
        goto L_0x014b;
    L_0x0203:
        r2 = 0;
        goto L_0x0200;
    L_0x0205:
        r2 = 1;
        r0 = r17;
        r0.putUser(r3, r2);
    L_0x020b:
        r2 = r5 + 1;
        r5 = r2;
        goto L_0x0159;
    L_0x0210:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.lastPtsValue;
        r0 = r18;
        r3 = r0.pts_count;
        r2 = r2 + r3;
        r0 = r18;
        r3 = r0.pts;
        if (r2 != r3) goto L_0x03a0;
    L_0x021d:
        r5 = new com.hanista.mobogram.tgnet.TLRPC$TL_message;
        r5.<init>();
        r0 = r18;
        r2 = r0.id;
        r5.id = r2;
        r6 = com.hanista.mobogram.messenger.UserConfig.getClientUserId();
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updateShortMessage;
        if (r2 == 0) goto L_0x035b;
    L_0x0232:
        r0 = r18;
        r2 = r0.out;
        if (r2 == 0) goto L_0x0357;
    L_0x0238:
        r5.from_id = r6;
    L_0x023a:
        r2 = new com.hanista.mobogram.tgnet.TLRPC$TL_peerUser;
        r2.<init>();
        r5.to_id = r2;
        r2 = r5.to_id;
        r2.user_id = r4;
        r2 = (long) r4;
        r5.dialog_id = r2;
    L_0x0248:
        r0 = r18;
        r2 = r0.fwd_from;
        r5.fwd_from = r2;
        r0 = r18;
        r2 = r0.silent;
        r5.silent = r2;
        r0 = r18;
        r2 = r0.out;
        r5.out = r2;
        r0 = r18;
        r2 = r0.mentioned;
        r5.mentioned = r2;
        r0 = r18;
        r2 = r0.media_unread;
        r5.media_unread = r2;
        r0 = r18;
        r2 = r0.entities;
        r5.entities = r2;
        r0 = r18;
        r2 = r0.message;
        r5.message = r2;
        r0 = r18;
        r2 = r0.date;
        r5.date = r2;
        r0 = r18;
        r2 = r0.via_bot_id;
        r5.via_bot_id = r2;
        r0 = r18;
        r2 = r0.flags;
        r2 = r2 | 256;
        r5.flags = r2;
        r0 = r18;
        r2 = r0.reply_to_msg_id;
        r5.reply_to_msg_id = r2;
        r2 = new com.hanista.mobogram.tgnet.TLRPC$TL_messageMediaEmpty;
        r2.<init>();
        r5.media = r2;
        r2 = r5.out;
        if (r2 == 0) goto L_0x0376;
    L_0x0297:
        r0 = r17;
        r2 = r0.dialogs_read_outbox_max;
        r3 = r2;
    L_0x029c:
        r12 = r5.dialog_id;
        r2 = java.lang.Long.valueOf(r12);
        r2 = r3.get(r2);
        r2 = (java.lang.Integer) r2;
        if (r2 != 0) goto L_0x02c3;
    L_0x02aa:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r7 = r5.out;
        r12 = r5.dialog_id;
        r2 = r2.getDialogReadMax(r7, r12);
        r2 = java.lang.Integer.valueOf(r2);
        r12 = r5.dialog_id;
        r7 = java.lang.Long.valueOf(r12);
        r3.put(r7, r2);
    L_0x02c3:
        r2 = r2.intValue();
        r3 = r5.id;
        if (r2 >= r3) goto L_0x037d;
    L_0x02cb:
        r2 = 1;
    L_0x02cc:
        r5.unread = r2;
        r2 = r5.dialog_id;
        r6 = (long) r6;
        r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r2 != 0) goto L_0x02de;
    L_0x02d5:
        r2 = 0;
        r5.unread = r2;
        r2 = 0;
        r5.media_unread = r2;
        r2 = 1;
        r5.out = r2;
    L_0x02de:
        r0 = r18;
        r2 = r0.pts;
        com.hanista.mobogram.messenger.MessagesStorage.lastPtsValue = r2;
        r6 = new com.hanista.mobogram.messenger.MessageObject;
        r2 = 0;
        r0 = r17;
        r3 = r0.createdDialogIds;
        r12 = r5.dialog_id;
        r7 = java.lang.Long.valueOf(r12);
        r3 = r3.contains(r7);
        r6.<init>(r5, r2, r3);
        r7 = new java.util.ArrayList;
        r7.<init>();
        r7.add(r6);
        r3 = new java.util.ArrayList;
        r3.<init>();
        r3.add(r5);
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updateShortMessage;
        if (r2 == 0) goto L_0x0382;
    L_0x030e:
        r0 = r18;
        r2 = r0.out;
        if (r2 != 0) goto L_0x0380;
    L_0x0314:
        r0 = r18;
        r2 = r0.user_id;
        r12 = (long) r2;
        r0 = r17;
        r2 = r0.updatePrintingUsersWithNewMessages(r12, r7);
        if (r2 == 0) goto L_0x0380;
    L_0x0321:
        r2 = 1;
    L_0x0322:
        if (r2 == 0) goto L_0x0327;
    L_0x0324:
        r17.updatePrintingStrings();
    L_0x0327:
        r5 = new com.hanista.mobogram.messenger.MessagesController$98;
        r0 = r17;
        r5.<init>(r2, r4, r7);
        com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r5);
    L_0x0331:
        r2 = r6.isOut();
        if (r2 != 0) goto L_0x0349;
    L_0x0337:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r2 = r2.getStorageQueue();
        r4 = new com.hanista.mobogram.messenger.MessagesController$100;
        r0 = r17;
        r4.<init>(r7);
        r2.postRunnable(r4);
    L_0x0349:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r4 = 0;
        r5 = 1;
        r6 = 0;
        r7 = 0;
        r2.putMessages(r3, r4, r5, r6, r7);
        r2 = r9;
        goto L_0x01c1;
    L_0x0357:
        r5.from_id = r4;
        goto L_0x023a;
    L_0x035b:
        r5.from_id = r4;
        r2 = new com.hanista.mobogram.tgnet.TLRPC$TL_peerChat;
        r2.<init>();
        r5.to_id = r2;
        r2 = r5.to_id;
        r0 = r18;
        r3 = r0.chat_id;
        r2.chat_id = r3;
        r0 = r18;
        r2 = r0.chat_id;
        r2 = -r2;
        r2 = (long) r2;
        r5.dialog_id = r2;
        goto L_0x0248;
    L_0x0376:
        r0 = r17;
        r2 = r0.dialogs_read_inbox_max;
        r3 = r2;
        goto L_0x029c;
    L_0x037d:
        r2 = 0;
        goto L_0x02cc;
    L_0x0380:
        r2 = 0;
        goto L_0x0322;
    L_0x0382:
        r0 = r18;
        r2 = r0.chat_id;
        r2 = -r2;
        r4 = (long) r2;
        r0 = r17;
        r2 = r0.updatePrintingUsersWithNewMessages(r4, r7);
        if (r2 == 0) goto L_0x0393;
    L_0x0390:
        r17.updatePrintingStrings();
    L_0x0393:
        r4 = new com.hanista.mobogram.messenger.MessagesController$99;
        r0 = r17;
        r1 = r18;
        r4.<init>(r2, r1, r7);
        com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r4);
        goto L_0x0331;
    L_0x03a0:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.lastPtsValue;
        r0 = r18;
        r3 = r0.pts;
        if (r2 == r3) goto L_0x0b8f;
    L_0x03a8:
        r2 = "tmessages";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "need get diff short message, pts: ";
        r3 = r3.append(r4);
        r4 = com.hanista.mobogram.messenger.MessagesStorage.lastPtsValue;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r0 = r18;
        r4 = r0.pts;
        r3 = r3.append(r4);
        r4 = " count = ";
        r3 = r3.append(r4);
        r0 = r18;
        r4 = r0.pts_count;
        r3 = r3.append(r4);
        r3 = r3.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
        r0 = r17;
        r2 = r0.gettingDifference;
        if (r2 != 0) goto L_0x0405;
    L_0x03e8:
        r0 = r17;
        r2 = r0.updatesStartWaitTimePts;
        r4 = 0;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 == 0) goto L_0x0405;
    L_0x03f2:
        r2 = java.lang.System.currentTimeMillis();
        r0 = r17;
        r4 = r0.updatesStartWaitTimePts;
        r2 = r2 - r4;
        r2 = java.lang.Math.abs(r2);
        r4 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 > 0) goto L_0x042c;
    L_0x0405:
        r0 = r17;
        r2 = r0.updatesStartWaitTimePts;
        r4 = 0;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 != 0) goto L_0x0417;
    L_0x040f:
        r2 = java.lang.System.currentTimeMillis();
        r0 = r17;
        r0.updatesStartWaitTimePts = r2;
    L_0x0417:
        r2 = "tmessages";
        r3 = "add to queue";
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
        r0 = r17;
        r2 = r0.updatesQueuePts;
        r0 = r18;
        r2.add(r0);
        r2 = r9;
        goto L_0x01c1;
    L_0x042c:
        r2 = 1;
        goto L_0x01c1;
    L_0x042f:
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updatesCombined;
        if (r2 != 0) goto L_0x043b;
    L_0x0435:
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updates;
        if (r2 == 0) goto L_0x0acc;
    L_0x043b:
        r3 = 0;
        r2 = 0;
        r4 = r2;
    L_0x043e:
        r0 = r18;
        r2 = r0.chats;
        r2 = r2.size();
        if (r4 >= r2) goto L_0x049b;
    L_0x0448:
        r0 = r18;
        r2 = r0.chats;
        r2 = r2.get(r4);
        r2 = (com.hanista.mobogram.tgnet.TLRPC.Chat) r2;
        r5 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_channel;
        if (r5 == 0) goto L_0x0497;
    L_0x0456:
        r5 = r2.min;
        if (r5 == 0) goto L_0x0497;
    L_0x045a:
        r5 = r2.id;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r17;
        r5 = r0.getChat(r5);
        if (r5 == 0) goto L_0x046c;
    L_0x0468:
        r6 = r5.min;
        if (r6 == 0) goto L_0x0481;
    L_0x046c:
        r6 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r0 = r18;
        r7 = r0.chat_id;
        r6 = r6.getChatSync(r7);
        if (r5 != 0) goto L_0x0480;
    L_0x047a:
        r5 = 1;
        r0 = r17;
        r0.putChat(r6, r5);
    L_0x0480:
        r5 = r6;
    L_0x0481:
        if (r5 == 0) goto L_0x0487;
    L_0x0483:
        r5 = r5.min;
        if (r5 == 0) goto L_0x0497;
    L_0x0487:
        if (r3 != 0) goto L_0x048e;
    L_0x0489:
        r3 = new java.util.HashMap;
        r3.<init>();
    L_0x048e:
        r5 = r2.id;
        r5 = java.lang.Integer.valueOf(r5);
        r3.put(r5, r2);
    L_0x0497:
        r2 = r4 + 1;
        r4 = r2;
        goto L_0x043e;
    L_0x049b:
        if (r3 == 0) goto L_0x0b8c;
    L_0x049d:
        r2 = 0;
        r4 = r2;
    L_0x049f:
        r0 = r18;
        r2 = r0.updates;
        r2 = r2.size();
        if (r4 >= r2) goto L_0x0b8c;
    L_0x04a9:
        r0 = r18;
        r2 = r0.updates;
        r2 = r2.get(r4);
        r2 = (com.hanista.mobogram.tgnet.TLRPC.Update) r2;
        r5 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updateNewChannelMessage;
        if (r5 == 0) goto L_0x0576;
    L_0x04b7:
        r2 = (com.hanista.mobogram.tgnet.TLRPC.TL_updateNewChannelMessage) r2;
        r2 = r2.message;
        r2 = r2.to_id;
        r2 = r2.channel_id;
        r5 = java.lang.Integer.valueOf(r2);
        r5 = r3.containsKey(r5);
        if (r5 == 0) goto L_0x0576;
    L_0x04c9:
        r3 = "tmessages";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "need get diff because of min channel ";
        r4 = r4.append(r5);
        r2 = r4.append(r2);
        r2 = r2.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r3, r2);
        r9 = 1;
        r6 = r9;
    L_0x04e5:
        if (r6 != 0) goto L_0x0b88;
    L_0x04e7:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r0 = r18;
        r3 = r0.users;
        r0 = r18;
        r4 = r0.chats;
        r5 = 1;
        r7 = 1;
        r2.putUsersAndChats(r3, r4, r5, r7);
        r0 = r18;
        r2 = r0.updates;
        r0 = r17;
        r3 = r0.updatesComparator;
        java.util.Collections.sort(r2, r3);
        r2 = 0;
        r7 = r2;
        r5 = r10;
        r4 = r11;
    L_0x0507:
        r0 = r18;
        r2 = r0.updates;
        r2 = r2.size();
        if (r2 <= 0) goto L_0x09ba;
    L_0x0511:
        r0 = r18;
        r2 = r0.updates;
        r2 = r2.get(r7);
        r2 = (com.hanista.mobogram.tgnet.TLRPC.Update) r2;
        r0 = r17;
        r3 = r0.getUpdateType(r2);
        if (r3 != 0) goto L_0x066f;
    L_0x0523:
        r9 = new com.hanista.mobogram.tgnet.TLRPC$TL_updates;
        r9.<init>();
        r3 = r9.updates;
        r3.add(r2);
        r3 = r2.pts;
        r9.pts = r3;
        r3 = r2.pts_count;
        r9.pts_count = r3;
        r3 = 1;
        r8 = r3;
    L_0x0537:
        r0 = r18;
        r3 = r0.updates;
        r3 = r3.size();
        if (r8 >= r3) goto L_0x057b;
    L_0x0541:
        r0 = r18;
        r3 = r0.updates;
        r3 = r3.get(r8);
        r3 = (com.hanista.mobogram.tgnet.TLRPC.Update) r3;
        r0 = r17;
        r10 = r0.getUpdateType(r3);
        if (r10 != 0) goto L_0x057b;
    L_0x0553:
        r10 = r9.pts;
        r11 = r3.pts_count;
        r10 = r10 + r11;
        r11 = r3.pts;
        if (r10 != r11) goto L_0x057b;
    L_0x055c:
        r10 = r9.updates;
        r10.add(r3);
        r10 = r3.pts;
        r9.pts = r10;
        r10 = r9.pts_count;
        r3 = r3.pts_count;
        r3 = r3 + r10;
        r9.pts_count = r3;
        r0 = r18;
        r3 = r0.updates;
        r3.remove(r8);
        r3 = 1;
        r8 = r3;
        goto L_0x0537;
    L_0x0576:
        r2 = r4 + 1;
        r4 = r2;
        goto L_0x049f;
    L_0x057b:
        r3 = com.hanista.mobogram.messenger.MessagesStorage.lastPtsValue;
        r8 = r9.pts_count;
        r3 = r3 + r8;
        r8 = r9.pts;
        if (r3 != r8) goto L_0x05d9;
    L_0x0584:
        r2 = r9.updates;
        r0 = r18;
        r3 = r0.users;
        r0 = r18;
        r8 = r0.chats;
        r10 = 0;
        r0 = r17;
        r2 = r0.processUpdateArray(r2, r3, r8, r10);
        if (r2 != 0) goto L_0x05d3;
    L_0x0597:
        r2 = "tmessages";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r6 = "need get diff inner TL_updates, seq: ";
        r3 = r3.append(r6);
        r6 = com.hanista.mobogram.messenger.MessagesStorage.lastSeqValue;
        r3 = r3.append(r6);
        r6 = " ";
        r3 = r3.append(r6);
        r0 = r18;
        r6 = r0.seq;
        r3 = r3.append(r6);
        r3 = r3.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
        r6 = 1;
        r2 = r6;
    L_0x05c4:
        r3 = r2;
        r2 = r5;
    L_0x05c6:
        r0 = r18;
        r5 = r0.updates;
        r5.remove(r7);
        r5 = 0;
        r7 = r5;
        r6 = r3;
        r5 = r2;
        goto L_0x0507;
    L_0x05d3:
        r2 = r9.pts;
        com.hanista.mobogram.messenger.MessagesStorage.lastPtsValue = r2;
        r2 = r6;
        goto L_0x05c4;
    L_0x05d9:
        r3 = com.hanista.mobogram.messenger.MessagesStorage.lastPtsValue;
        r8 = r9.pts;
        if (r3 == r8) goto L_0x0b85;
    L_0x05df:
        r3 = "tmessages";
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r2 = r8.append(r2);
        r8 = " need get diff, pts: ";
        r2 = r2.append(r8);
        r8 = com.hanista.mobogram.messenger.MessagesStorage.lastPtsValue;
        r2 = r2.append(r8);
        r8 = " ";
        r2 = r2.append(r8);
        r8 = r9.pts;
        r2 = r2.append(r8);
        r8 = " count = ";
        r2 = r2.append(r8);
        r8 = r9.pts_count;
        r2 = r2.append(r8);
        r2 = r2.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r3, r2);
        r0 = r17;
        r2 = r0.gettingDifference;
        if (r2 != 0) goto L_0x0646;
    L_0x061f:
        r0 = r17;
        r2 = r0.updatesStartWaitTimePts;
        r10 = 0;
        r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r2 == 0) goto L_0x0646;
    L_0x0629:
        r0 = r17;
        r2 = r0.updatesStartWaitTimePts;
        r10 = 0;
        r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r2 == 0) goto L_0x066b;
    L_0x0633:
        r2 = java.lang.System.currentTimeMillis();
        r0 = r17;
        r10 = r0.updatesStartWaitTimePts;
        r2 = r2 - r10;
        r2 = java.lang.Math.abs(r2);
        r10 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r2 > 0) goto L_0x066b;
    L_0x0646:
        r0 = r17;
        r2 = r0.updatesStartWaitTimePts;
        r10 = 0;
        r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r2 != 0) goto L_0x0658;
    L_0x0650:
        r2 = java.lang.System.currentTimeMillis();
        r0 = r17;
        r0.updatesStartWaitTimePts = r2;
    L_0x0658:
        r2 = "tmessages";
        r3 = "add to queue";
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
        r0 = r17;
        r2 = r0.updatesQueuePts;
        r2.add(r9);
        r2 = r6;
        goto L_0x05c4;
    L_0x066b:
        r6 = 1;
        r2 = r6;
        goto L_0x05c4;
    L_0x066f:
        r0 = r17;
        r3 = r0.getUpdateType(r2);
        r8 = 1;
        if (r3 != r8) goto L_0x0775;
    L_0x0678:
        r9 = new com.hanista.mobogram.tgnet.TLRPC$TL_updates;
        r9.<init>();
        r3 = r9.updates;
        r3.add(r2);
        r3 = r2.qts;
        r9.pts = r3;
        r3 = 1;
        r8 = r3;
    L_0x0688:
        r0 = r18;
        r3 = r0.updates;
        r3 = r3.size();
        if (r8 >= r3) goto L_0x06c0;
    L_0x0692:
        r0 = r18;
        r3 = r0.updates;
        r3 = r3.get(r8);
        r3 = (com.hanista.mobogram.tgnet.TLRPC.Update) r3;
        r0 = r17;
        r10 = r0.getUpdateType(r3);
        r11 = 1;
        if (r10 != r11) goto L_0x06c0;
    L_0x06a5:
        r10 = r9.pts;
        r10 = r10 + 1;
        r11 = r3.qts;
        if (r10 != r11) goto L_0x06c0;
    L_0x06ad:
        r10 = r9.updates;
        r10.add(r3);
        r3 = r3.qts;
        r9.pts = r3;
        r0 = r18;
        r3 = r0.updates;
        r3.remove(r8);
        r3 = 1;
        r8 = r3;
        goto L_0x0688;
    L_0x06c0:
        r3 = com.hanista.mobogram.messenger.MessagesStorage.lastQtsValue;
        if (r3 == 0) goto L_0x06d1;
    L_0x06c4:
        r3 = com.hanista.mobogram.messenger.MessagesStorage.lastQtsValue;
        r8 = r9.updates;
        r8 = r8.size();
        r3 = r3 + r8;
        r8 = r9.pts;
        if (r3 != r8) goto L_0x06ea;
    L_0x06d1:
        r2 = r9.updates;
        r0 = r18;
        r3 = r0.users;
        r0 = r18;
        r5 = r0.chats;
        r8 = 0;
        r0 = r17;
        r0.processUpdateArray(r2, r3, r5, r8);
        r2 = r9.pts;
        com.hanista.mobogram.messenger.MessagesStorage.lastQtsValue = r2;
        r5 = 1;
        r2 = r5;
        r3 = r6;
        goto L_0x05c6;
    L_0x06ea:
        r3 = com.hanista.mobogram.messenger.MessagesStorage.lastPtsValue;
        r8 = r9.pts;
        if (r3 == r8) goto L_0x09b6;
    L_0x06f0:
        r3 = "tmessages";
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r2 = r8.append(r2);
        r8 = " need get diff, qts: ";
        r2 = r2.append(r8);
        r8 = com.hanista.mobogram.messenger.MessagesStorage.lastQtsValue;
        r2 = r2.append(r8);
        r8 = " ";
        r2 = r2.append(r8);
        r8 = r9.pts;
        r2 = r2.append(r8);
        r2 = r2.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r3, r2);
        r0 = r17;
        r2 = r0.gettingDifference;
        if (r2 != 0) goto L_0x074a;
    L_0x0723:
        r0 = r17;
        r2 = r0.updatesStartWaitTimeQts;
        r10 = 0;
        r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r2 == 0) goto L_0x074a;
    L_0x072d:
        r0 = r17;
        r2 = r0.updatesStartWaitTimeQts;
        r10 = 0;
        r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r2 == 0) goto L_0x0770;
    L_0x0737:
        r2 = java.lang.System.currentTimeMillis();
        r0 = r17;
        r10 = r0.updatesStartWaitTimeQts;
        r2 = r2 - r10;
        r2 = java.lang.Math.abs(r2);
        r10 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r2 > 0) goto L_0x0770;
    L_0x074a:
        r0 = r17;
        r2 = r0.updatesStartWaitTimeQts;
        r10 = 0;
        r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r2 != 0) goto L_0x075c;
    L_0x0754:
        r2 = java.lang.System.currentTimeMillis();
        r0 = r17;
        r0.updatesStartWaitTimeQts = r2;
    L_0x075c:
        r2 = "tmessages";
        r3 = "add to queue";
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
        r0 = r17;
        r2 = r0.updatesQueueQts;
        r2.add(r9);
        r2 = r5;
        r3 = r6;
        goto L_0x05c6;
    L_0x0770:
        r6 = 1;
        r2 = r5;
        r3 = r6;
        goto L_0x05c6;
    L_0x0775:
        r0 = r17;
        r3 = r0.getUpdateType(r2);
        r8 = 2;
        if (r3 != r8) goto L_0x09ba;
    L_0x077e:
        r0 = r17;
        r11 = r0.getUpdateChannelId(r2);
        r9 = 0;
        r0 = r17;
        r3 = r0.channelsPts;
        r8 = java.lang.Integer.valueOf(r11);
        r3 = r3.get(r8);
        r3 = (java.lang.Integer) r3;
        if (r3 != 0) goto L_0x0b82;
    L_0x0795:
        r3 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r3 = r3.getChannelPtsSync(r11);
        r10 = java.lang.Integer.valueOf(r3);
        r3 = r10.intValue();
        if (r3 != 0) goto L_0x082c;
    L_0x07a7:
        r3 = 0;
        r8 = r3;
    L_0x07a9:
        r0 = r18;
        r3 = r0.chats;
        r3 = r3.size();
        if (r8 >= r3) goto L_0x0b7f;
    L_0x07b3:
        r0 = r18;
        r3 = r0.chats;
        r3 = r3.get(r8);
        r3 = (com.hanista.mobogram.tgnet.TLRPC.Chat) r3;
        r13 = r3.id;
        if (r13 != r11) goto L_0x0827;
    L_0x07c1:
        r8 = 0;
        r0 = r17;
        r0.loadUnknownChannel(r3, r8);
        r3 = 1;
    L_0x07c9:
        r8 = r10;
        r9 = r3;
    L_0x07cb:
        r13 = new com.hanista.mobogram.tgnet.TLRPC$TL_updates;
        r13.<init>();
        r3 = r13.updates;
        r3.add(r2);
        r3 = r2.pts;
        r13.pts = r3;
        r3 = r2.pts_count;
        r13.pts_count = r3;
        r3 = 1;
        r10 = r3;
    L_0x07df:
        r0 = r18;
        r3 = r0.updates;
        r3 = r3.size();
        if (r10 >= r3) goto L_0x0839;
    L_0x07e9:
        r0 = r18;
        r3 = r0.updates;
        r3 = r3.get(r10);
        r3 = (com.hanista.mobogram.tgnet.TLRPC.Update) r3;
        r0 = r17;
        r14 = r0.getUpdateType(r3);
        r15 = 2;
        if (r14 != r15) goto L_0x0839;
    L_0x07fc:
        r0 = r17;
        r14 = r0.getUpdateChannelId(r3);
        if (r11 != r14) goto L_0x0839;
    L_0x0804:
        r14 = r13.pts;
        r15 = r3.pts_count;
        r14 = r14 + r15;
        r15 = r3.pts;
        if (r14 != r15) goto L_0x0839;
    L_0x080d:
        r14 = r13.updates;
        r14.add(r3);
        r14 = r3.pts;
        r13.pts = r14;
        r14 = r13.pts_count;
        r3 = r3.pts_count;
        r3 = r3 + r14;
        r13.pts_count = r3;
        r0 = r18;
        r3 = r0.updates;
        r3.remove(r10);
        r3 = 1;
        r10 = r3;
        goto L_0x07df;
    L_0x0827:
        r3 = r8 + 1;
        r8 = r3;
        goto L_0x07a9;
    L_0x082c:
        r0 = r17;
        r3 = r0.channelsPts;
        r8 = java.lang.Integer.valueOf(r11);
        r3.put(r8, r10);
        r8 = r10;
        goto L_0x07cb;
    L_0x0839:
        if (r9 != 0) goto L_0x099c;
    L_0x083b:
        r3 = r8.intValue();
        r9 = r13.pts_count;
        r3 = r3 + r9;
        r9 = r13.pts;
        if (r3 != r9) goto L_0x08b1;
    L_0x0846:
        r2 = r13.updates;
        r0 = r18;
        r3 = r0.users;
        r0 = r18;
        r8 = r0.chats;
        r9 = 0;
        r0 = r17;
        r2 = r0.processUpdateArray(r2, r3, r8, r9);
        if (r2 != 0) goto L_0x0893;
    L_0x0859:
        r2 = "tmessages";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r8 = "need get channel diff inner TL_updates, channel_id = ";
        r3 = r3.append(r8);
        r3 = r3.append(r11);
        r3 = r3.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
        if (r4 != 0) goto L_0x087e;
    L_0x0875:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r2 = r5;
        r3 = r6;
        goto L_0x05c6;
    L_0x087e:
        r2 = java.lang.Integer.valueOf(r11);
        r2 = r4.contains(r2);
        if (r2 != 0) goto L_0x09b6;
    L_0x0888:
        r2 = java.lang.Integer.valueOf(r11);
        r4.add(r2);
        r2 = r5;
        r3 = r6;
        goto L_0x05c6;
    L_0x0893:
        r0 = r17;
        r2 = r0.channelsPts;
        r3 = java.lang.Integer.valueOf(r11);
        r8 = r13.pts;
        r8 = java.lang.Integer.valueOf(r8);
        r2.put(r3, r8);
        r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r3 = r13.pts;
        r2.saveChannelPts(r11, r3);
        r2 = r5;
        r3 = r6;
        goto L_0x05c6;
    L_0x08b1:
        r3 = r8.intValue();
        r9 = r13.pts;
        if (r3 == r9) goto L_0x09b6;
    L_0x08b9:
        r3 = "tmessages";
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r2 = r9.append(r2);
        r9 = " need get channel diff, pts: ";
        r2 = r2.append(r9);
        r2 = r2.append(r8);
        r8 = " ";
        r2 = r2.append(r8);
        r8 = r13.pts;
        r2 = r2.append(r8);
        r8 = " count = ";
        r2 = r2.append(r8);
        r8 = r13.pts_count;
        r2 = r2.append(r8);
        r8 = " channelId = ";
        r2 = r2.append(r8);
        r2 = r2.append(r11);
        r2 = r2.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r3, r2);
        r0 = r17;
        r2 = r0.updatesStartWaitTimeChannels;
        r3 = java.lang.Integer.valueOf(r11);
        r2 = r2.get(r3);
        r2 = (java.lang.Long) r2;
        r0 = r17;
        r3 = r0.gettingDifferenceChannels;
        r8 = java.lang.Integer.valueOf(r11);
        r3 = r3.get(r8);
        r3 = (java.lang.Boolean) r3;
        if (r3 != 0) goto L_0x091f;
    L_0x091a:
        r3 = 0;
        r3 = java.lang.Boolean.valueOf(r3);
    L_0x091f:
        r3 = r3.booleanValue();
        if (r3 != 0) goto L_0x093a;
    L_0x0925:
        if (r2 == 0) goto L_0x093a;
    L_0x0927:
        r8 = java.lang.System.currentTimeMillis();
        r14 = r2.longValue();
        r8 = r8 - r14;
        r8 = java.lang.Math.abs(r8);
        r14 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r3 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r3 > 0) goto L_0x0981;
    L_0x093a:
        if (r2 != 0) goto L_0x094f;
    L_0x093c:
        r0 = r17;
        r2 = r0.updatesStartWaitTimeChannels;
        r3 = java.lang.Integer.valueOf(r11);
        r8 = java.lang.System.currentTimeMillis();
        r8 = java.lang.Long.valueOf(r8);
        r2.put(r3, r8);
    L_0x094f:
        r2 = "tmessages";
        r3 = "add to queue";
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
        r0 = r17;
        r2 = r0.updatesQueueChannels;
        r3 = java.lang.Integer.valueOf(r11);
        r2 = r2.get(r3);
        r2 = (java.util.ArrayList) r2;
        if (r2 != 0) goto L_0x0978;
    L_0x0968:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r0 = r17;
        r3 = r0.updatesQueueChannels;
        r8 = java.lang.Integer.valueOf(r11);
        r3.put(r8, r2);
    L_0x0978:
        r2.add(r13);
        r2 = r4;
    L_0x097c:
        r3 = r6;
        r4 = r2;
        r2 = r5;
        goto L_0x05c6;
    L_0x0981:
        if (r4 != 0) goto L_0x0989;
    L_0x0983:
        r2 = new java.util.ArrayList;
        r2.<init>();
        goto L_0x097c;
    L_0x0989:
        r2 = java.lang.Integer.valueOf(r11);
        r2 = r4.contains(r2);
        if (r2 != 0) goto L_0x099a;
    L_0x0993:
        r2 = java.lang.Integer.valueOf(r11);
        r4.add(r2);
    L_0x099a:
        r2 = r4;
        goto L_0x097c;
    L_0x099c:
        r2 = "tmessages";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r8 = "need load unknown channel = ";
        r3 = r3.append(r8);
        r3 = r3.append(r11);
        r3 = r3.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
    L_0x09b6:
        r2 = r5;
        r3 = r6;
        goto L_0x05c6;
    L_0x09ba:
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updatesCombined;
        if (r2 == 0) goto L_0x0a07;
    L_0x09c0:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.lastSeqValue;
        r2 = r2 + 1;
        r0 = r18;
        r3 = r0.seq_start;
        if (r2 == r3) goto L_0x09d2;
    L_0x09ca:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.lastSeqValue;
        r0 = r18;
        r3 = r0.seq_start;
        if (r2 != r3) goto L_0x0a05;
    L_0x09d2:
        r2 = 1;
    L_0x09d3:
        if (r2 == 0) goto L_0x0a23;
    L_0x09d5:
        r0 = r18;
        r2 = r0.updates;
        r0 = r18;
        r3 = r0.users;
        r0 = r18;
        r7 = r0.chats;
        r8 = 0;
        r0 = r17;
        r0.processUpdateArray(r2, r3, r7, r8);
        r0 = r18;
        r2 = r0.date;
        if (r2 == 0) goto L_0x09f3;
    L_0x09ed:
        r0 = r18;
        r2 = r0.date;
        com.hanista.mobogram.messenger.MessagesStorage.lastDateValue = r2;
    L_0x09f3:
        r0 = r18;
        r2 = r0.seq;
        if (r2 == 0) goto L_0x09ff;
    L_0x09f9:
        r0 = r18;
        r2 = r0.seq;
        com.hanista.mobogram.messenger.MessagesStorage.lastSeqValue = r2;
    L_0x09ff:
        r3 = r12;
        r10 = r5;
        r9 = r6;
        r11 = r4;
        goto L_0x001f;
    L_0x0a05:
        r2 = 0;
        goto L_0x09d3;
    L_0x0a07:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.lastSeqValue;
        r2 = r2 + 1;
        r0 = r18;
        r3 = r0.seq;
        if (r2 == r3) goto L_0x0a1f;
    L_0x0a11:
        r0 = r18;
        r2 = r0.seq;
        if (r2 == 0) goto L_0x0a1f;
    L_0x0a17:
        r0 = r18;
        r2 = r0.seq;
        r3 = com.hanista.mobogram.messenger.MessagesStorage.lastSeqValue;
        if (r2 != r3) goto L_0x0a21;
    L_0x0a1f:
        r2 = 1;
        goto L_0x09d3;
    L_0x0a21:
        r2 = 0;
        goto L_0x09d3;
    L_0x0a23:
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updatesCombined;
        if (r2 == 0) goto L_0x0a9d;
    L_0x0a29:
        r2 = "tmessages";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r7 = "need get diff TL_updatesCombined, seq: ";
        r3 = r3.append(r7);
        r7 = com.hanista.mobogram.messenger.MessagesStorage.lastSeqValue;
        r3 = r3.append(r7);
        r7 = " ";
        r3 = r3.append(r7);
        r0 = r18;
        r7 = r0.seq_start;
        r3 = r3.append(r7);
        r3 = r3.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
    L_0x0a54:
        r0 = r17;
        r2 = r0.gettingDifference;
        if (r2 != 0) goto L_0x0a77;
    L_0x0a5a:
        r0 = r17;
        r2 = r0.updatesStartWaitTimeSeq;
        r8 = 0;
        r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
        if (r2 == 0) goto L_0x0a77;
    L_0x0a64:
        r2 = java.lang.System.currentTimeMillis();
        r0 = r17;
        r8 = r0.updatesStartWaitTimeSeq;
        r2 = r2 - r8;
        r2 = java.lang.Math.abs(r2);
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
        if (r2 > 0) goto L_0x0ac9;
    L_0x0a77:
        r0 = r17;
        r2 = r0.updatesStartWaitTimeSeq;
        r8 = 0;
        r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
        if (r2 != 0) goto L_0x0a89;
    L_0x0a81:
        r2 = java.lang.System.currentTimeMillis();
        r0 = r17;
        r0.updatesStartWaitTimeSeq = r2;
    L_0x0a89:
        r2 = "tmessages";
        r3 = "add TL_updates/Combined to queue";
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
        r0 = r17;
        r2 = r0.updatesQueueSeq;
        r0 = r18;
        r2.add(r0);
        goto L_0x09ff;
    L_0x0a9d:
        r2 = "tmessages";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r7 = "need get diff TL_updates, seq: ";
        r3 = r3.append(r7);
        r7 = com.hanista.mobogram.messenger.MessagesStorage.lastSeqValue;
        r3 = r3.append(r7);
        r7 = " ";
        r3 = r3.append(r7);
        r0 = r18;
        r7 = r0.seq;
        r3 = r3.append(r7);
        r3 = r3.toString();
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
        goto L_0x0a54;
    L_0x0ac9:
        r6 = 1;
        goto L_0x09ff;
    L_0x0acc:
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_updatesTooLong;
        if (r2 == 0) goto L_0x0adf;
    L_0x0ad2:
        r2 = "tmessages";
        r3 = "need get diff TL_updatesTooLong";
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r3);
        r9 = 1;
        r3 = r12;
        goto L_0x001f;
    L_0x0adf:
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.messenger.MessagesController.UserActionUpdatesSeq;
        if (r2 == 0) goto L_0x0aee;
    L_0x0ae5:
        r0 = r18;
        r2 = r0.seq;
        com.hanista.mobogram.messenger.MessagesStorage.lastSeqValue = r2;
        r3 = r12;
        goto L_0x001f;
    L_0x0aee:
        r0 = r18;
        r2 = r0 instanceof com.hanista.mobogram.messenger.MessagesController.UserActionUpdatesPts;
        if (r2 == 0) goto L_0x0b29;
    L_0x0af4:
        r0 = r18;
        r2 = r0.chat_id;
        if (r2 == 0) goto L_0x0b23;
    L_0x0afa:
        r0 = r17;
        r2 = r0.channelsPts;
        r0 = r18;
        r3 = r0.chat_id;
        r3 = java.lang.Integer.valueOf(r3);
        r0 = r18;
        r4 = r0.pts;
        r4 = java.lang.Integer.valueOf(r4);
        r2.put(r3, r4);
        r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r0 = r18;
        r3 = r0.chat_id;
        r0 = r18;
        r4 = r0.pts;
        r2.saveChannelPts(r3, r4);
        r3 = r12;
        goto L_0x001f;
    L_0x0b23:
        r0 = r18;
        r2 = r0.pts;
        com.hanista.mobogram.messenger.MessagesStorage.lastPtsValue = r2;
    L_0x0b29:
        r3 = r12;
        goto L_0x001f;
    L_0x0b2c:
        r2 = r2.intValue();
        r6 = 0;
        r0 = r17;
        r0.processChannelsUpdatesQueue(r2, r6);
        goto L_0x0054;
    L_0x0b38:
        if (r9 == 0) goto L_0x0b72;
    L_0x0b3a:
        r17.getDifference();
    L_0x0b3d:
        if (r10 == 0) goto L_0x0b56;
    L_0x0b3f:
        r2 = new com.hanista.mobogram.tgnet.TLRPC$TL_messages_receivedQueue;
        r2.<init>();
        r4 = com.hanista.mobogram.messenger.MessagesStorage.lastQtsValue;
        r2.max_qts = r4;
        r4 = com.hanista.mobogram.tgnet.ConnectionsManager.getInstance();
        r5 = new com.hanista.mobogram.messenger.MessagesController$101;
        r0 = r17;
        r5.<init>();
        r4.sendRequest(r2, r5);
    L_0x0b56:
        if (r3 == 0) goto L_0x0b62;
    L_0x0b58:
        r2 = new com.hanista.mobogram.messenger.MessagesController$102;
        r0 = r17;
        r2.<init>();
        com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r2);
    L_0x0b62:
        r2 = com.hanista.mobogram.messenger.MessagesStorage.getInstance();
        r3 = com.hanista.mobogram.messenger.MessagesStorage.lastSeqValue;
        r4 = com.hanista.mobogram.messenger.MessagesStorage.lastPtsValue;
        r5 = com.hanista.mobogram.messenger.MessagesStorage.lastDateValue;
        r6 = com.hanista.mobogram.messenger.MessagesStorage.lastQtsValue;
        r2.saveDiffParams(r3, r4, r5, r6);
        return;
    L_0x0b72:
        r2 = 0;
    L_0x0b73:
        r4 = 3;
        if (r2 >= r4) goto L_0x0b3d;
    L_0x0b76:
        r4 = 0;
        r0 = r17;
        r0.processUpdatesQueue(r2, r4);
        r2 = r2 + 1;
        goto L_0x0b73;
    L_0x0b7f:
        r3 = r9;
        goto L_0x07c9;
    L_0x0b82:
        r8 = r3;
        goto L_0x07cb;
    L_0x0b85:
        r2 = r6;
        goto L_0x05c4;
    L_0x0b88:
        r5 = r10;
        r4 = r11;
        goto L_0x09ff;
    L_0x0b8c:
        r6 = r9;
        goto L_0x04e5;
    L_0x0b8f:
        r2 = r9;
        goto L_0x01c1;
    L_0x0b92:
        r8 = r12;
        goto L_0x01be;
    L_0x0b95:
        r2 = r6;
        goto L_0x0197;
    L_0x0b98:
        r16 = r6;
        r6 = r5;
        r5 = r16;
        goto L_0x0108;
    L_0x0b9f:
        r16 = r6;
        r6 = r5;
        r5 = r16;
        goto L_0x0108;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.MessagesController.processUpdates(com.hanista.mobogram.tgnet.TLRPC$Updates, boolean):void");
    }

    public void putChat(Chat chat, boolean z) {
        if (chat != null) {
            Chat chat2 = (Chat) this.chats.get(Integer.valueOf(chat.id));
            if (chat.min) {
                if (chat2 == null) {
                    this.chats.put(Integer.valueOf(chat.id), chat);
                } else if (!z) {
                    chat2.title = chat.title;
                    chat2.photo = chat.photo;
                    chat2.broadcast = chat.broadcast;
                    chat2.verified = chat.verified;
                    chat2.megagroup = chat.megagroup;
                    chat2.democracy = chat.democracy;
                    if (chat.username != null) {
                        chat2.username = chat.username;
                        chat2.flags |= UPDATE_MASK_USER_PRINT;
                        return;
                    }
                    chat2.username = null;
                    chat2.flags &= -65;
                }
            } else if (!z) {
                if (!(chat2 == null || chat.version == chat2.version)) {
                    this.loadedFullChats.remove(Integer.valueOf(chat.id));
                }
                this.chats.put(Integer.valueOf(chat.id), chat);
            } else if (chat2 == null) {
                this.chats.put(Integer.valueOf(chat.id), chat);
            } else if (chat2.min) {
                chat.min = false;
                chat.title = chat2.title;
                chat.photo = chat2.photo;
                chat.broadcast = chat2.broadcast;
                chat.verified = chat2.verified;
                chat.megagroup = chat2.megagroup;
                chat.democracy = chat2.democracy;
                if (chat2.username != null) {
                    chat.username = chat2.username;
                    chat.flags |= UPDATE_MASK_USER_PRINT;
                } else {
                    chat.username = null;
                    chat.flags &= -65;
                }
                this.chats.put(Integer.valueOf(chat.id), chat);
            }
        }
    }

    public void putChats(ArrayList<Chat> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i = 0; i < size; i += UPDATE_MASK_NAME) {
                putChat((Chat) arrayList.get(i), z);
            }
        }
    }

    public void putEncryptedChat(EncryptedChat encryptedChat, boolean z) {
        if (encryptedChat != null) {
            if (z) {
                this.encryptedChats.putIfAbsent(Integer.valueOf(encryptedChat.id), encryptedChat);
            } else {
                this.encryptedChats.put(Integer.valueOf(encryptedChat.id), encryptedChat);
            }
        }
    }

    public void putEncryptedChats(ArrayList<EncryptedChat> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i = 0; i < size; i += UPDATE_MASK_NAME) {
                putEncryptedChat((EncryptedChat) arrayList.get(i), z);
            }
        }
    }

    public boolean putUser(User user, boolean z) {
        if (user == null) {
            return false;
        }
        boolean z2 = (!z || user.id / PointerIconCompat.TYPE_DEFAULT == 333 || user.id == 777000) ? false : true;
        User user2 = (User) this.users.get(Integer.valueOf(user.id));
        if (!(user2 == null || user2.username == null || user2.username.length() <= 0)) {
            this.usersByUsernames.remove(user2.username);
        }
        if (user.username != null && user.username.length() > 0) {
            this.usersByUsernames.put(user.username.toLowerCase(), user);
        }
        if (user.min) {
            if (user2 == null) {
                this.users.put(Integer.valueOf(user.id), user);
                return false;
            } else if (z2) {
                return false;
            } else {
                if (user.username != null) {
                    user2.username = user.username;
                    user2.flags |= UPDATE_MASK_CHAT_AVATAR;
                } else {
                    user2.username = null;
                    user2.flags &= -9;
                }
                if (user.photo != null) {
                    user2.photo = user.photo;
                    user2.flags |= UPDATE_MASK_CHAT_MEMBERS;
                    return false;
                }
                user2.photo = null;
                user2.flags &= -33;
                return false;
            }
        } else if (!z2) {
            this.users.put(Integer.valueOf(user.id), user);
            if (user.id == UserConfig.getClientUserId()) {
                UserConfig.setCurrentUser(user);
                UserConfig.saveConfig(true);
            }
            return (user2 == null || user.status == null || user2.status == null || user.status.expires == user2.status.expires) ? false : true;
        } else if (user2 == null) {
            this.users.put(Integer.valueOf(user.id), user);
            return false;
        } else if (!user2.min) {
            return false;
        } else {
            user.min = false;
            if (user2.username != null) {
                user.username = user2.username;
                user.flags |= UPDATE_MASK_CHAT_AVATAR;
            } else {
                user.username = null;
                user.flags &= -9;
            }
            if (user2.photo != null) {
                user.photo = user2.photo;
                user.flags |= UPDATE_MASK_CHAT_MEMBERS;
            } else {
                user.photo = null;
                user.flags &= -33;
            }
            this.users.put(Integer.valueOf(user.id), user);
            return false;
        }
    }

    public void putUsers(ArrayList<User> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            int i = 0;
            Object obj = null;
            while (i < size) {
                Object obj2 = putUser((User) arrayList.get(i), z) ? UPDATE_MASK_NAME : obj;
                i += UPDATE_MASK_NAME;
                obj = obj2;
            }
            if (obj != null) {
                AndroidUtilities.runOnUIThread(new C05458());
            }
        }
    }

    public void reRunUpdateTimerProc() {
        this.lastStatusUpdateTime = 0;
        this.statusSettingState = 0;
        updateTimerProc();
    }

    public void registerForPush(String str) {
        if (str != null && str.length() != 0 && !this.registeringForPush && UserConfig.getClientUserId() != 0) {
            if (!UserConfig.registeredForPush || !str.equals(UserConfig.pushString)) {
                if (UserConfig.isRobot) {
                    this.registeringForPush = false;
                    return;
                }
                this.registeringForPush = true;
                TLObject tL_account_registerDevice = new TL_account_registerDevice();
                tL_account_registerDevice.token_type = UPDATE_MASK_AVATAR;
                tL_account_registerDevice.token = str;
                ConnectionsManager.getInstance().sendRequest(tL_account_registerDevice, new AnonymousClass85(str));
            }
        }
    }

    public void reloadWebPages(long j, HashMap<String, ArrayList<MessageObject>> hashMap) {
        for (Entry entry : hashMap.entrySet()) {
            String str = (String) entry.getKey();
            ArrayList arrayList = (ArrayList) entry.getValue();
            ArrayList arrayList2 = (ArrayList) this.reloadingWebpages.get(str);
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
                this.reloadingWebpages.put(str, arrayList2);
            }
            arrayList2.addAll(arrayList);
            TLObject tL_messages_getWebPagePreview = new TL_messages_getWebPagePreview();
            tL_messages_getWebPagePreview.message = str;
            ConnectionsManager.getInstance().sendRequest(tL_messages_getWebPagePreview, new AnonymousClass52(str, j));
        }
    }

    public void reportSpam(long j, User user, Chat chat) {
        if (user != null || chat != null) {
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            edit.putInt("spam3_" + j, UPDATE_MASK_NAME);
            edit.commit();
            TLObject tL_messages_reportSpam = new TL_messages_reportSpam();
            if (chat != null) {
                tL_messages_reportSpam.peer = getInputPeer(-chat.id);
            } else if (user != null) {
                tL_messages_reportSpam.peer = getInputPeer(user.id);
            }
            ConnectionsManager.getInstance().sendRequest(tL_messages_reportSpam, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                }
            }, UPDATE_MASK_AVATAR);
        }
    }

    public void saveGif(Document document) {
        TLObject tL_messages_saveGif = new TL_messages_saveGif();
        tL_messages_saveGif.id = new TL_inputDocument();
        tL_messages_saveGif.id.id = document.id;
        tL_messages_saveGif.id.access_hash = document.access_hash;
        tL_messages_saveGif.unsave = false;
        ConnectionsManager.getInstance().sendRequest(tL_messages_saveGif, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
            }
        });
    }

    public void saveRecentSticker(Document document, boolean z) {
        TLObject tL_messages_saveRecentSticker = new TL_messages_saveRecentSticker();
        tL_messages_saveRecentSticker.id = new TL_inputDocument();
        tL_messages_saveRecentSticker.id.id = document.id;
        tL_messages_saveRecentSticker.id.access_hash = document.access_hash;
        tL_messages_saveRecentSticker.unsave = false;
        tL_messages_saveRecentSticker.attached = z;
        ConnectionsManager.getInstance().sendRequest(tL_messages_saveRecentSticker, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
            }
        });
    }

    public void sendBotStart(User user, String str) {
        if (user != null) {
            TLObject tL_messages_startBot = new TL_messages_startBot();
            tL_messages_startBot.bot = getInputUser(user);
            tL_messages_startBot.peer = getInputPeer(user.id);
            tL_messages_startBot.start_param = str;
            tL_messages_startBot.random_id = Utilities.random.nextLong();
            ConnectionsManager.getInstance().sendRequest(tL_messages_startBot, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        MessagesController.this.processUpdates((Updates) tLObject, false);
                    }
                }
            });
        }
    }

    public void sendTyping(long j, int i, int i2) {
        if (j != 0) {
            HashMap hashMap = (HashMap) this.sendingTypings.get(Integer.valueOf(i));
            if (hashMap == null || hashMap.get(Long.valueOf(j)) == null) {
                if (hashMap == null) {
                    hashMap = new HashMap();
                    this.sendingTypings.put(Integer.valueOf(i), hashMap);
                }
                int i3 = (int) j;
                int i4 = (int) (j >> UPDATE_MASK_CHAT_MEMBERS);
                TLObject tL_messages_setTyping;
                int sendRequest;
                if (i3 != 0) {
                    if (i4 != UPDATE_MASK_NAME) {
                        tL_messages_setTyping = new TL_messages_setTyping();
                        tL_messages_setTyping.peer = getInputPeer(i3);
                        if (tL_messages_setTyping.peer instanceof TL_inputPeerChannel) {
                            Chat chat = getChat(Integer.valueOf(tL_messages_setTyping.peer.channel_id));
                            if (chat == null || !chat.megagroup) {
                                return;
                            }
                        }
                        if (tL_messages_setTyping.peer != null) {
                            if (i == 0) {
                                tL_messages_setTyping.action = new TL_sendMessageTypingAction();
                            } else if (i == UPDATE_MASK_NAME) {
                                tL_messages_setTyping.action = new TL_sendMessageRecordAudioAction();
                            } else if (i == UPDATE_MASK_AVATAR) {
                                tL_messages_setTyping.action = new TL_sendMessageCancelAction();
                            } else if (i == 3) {
                                tL_messages_setTyping.action = new TL_sendMessageUploadDocumentAction();
                            } else if (i == UPDATE_MASK_STATUS) {
                                tL_messages_setTyping.action = new TL_sendMessageUploadPhotoAction();
                            } else if (i == 5) {
                                tL_messages_setTyping.action = new TL_sendMessageUploadVideoAction();
                            }
                            hashMap.put(Long.valueOf(j), Boolean.valueOf(true));
                            sendRequest = ConnectionsManager.getInstance().sendRequest(tL_messages_setTyping, new AnonymousClass49(i, j), UPDATE_MASK_AVATAR);
                            if (i2 != 0) {
                                ConnectionsManager.getInstance().bindRequestToGuid(sendRequest, i2);
                            }
                        }
                    }
                } else if (i == 0) {
                    EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(i4));
                    if (encryptedChat.auth_key != null && encryptedChat.auth_key.length > UPDATE_MASK_NAME && (encryptedChat instanceof TL_encryptedChat)) {
                        tL_messages_setTyping = new TL_messages_setEncryptedTyping();
                        tL_messages_setTyping.peer = new TL_inputEncryptedChat();
                        tL_messages_setTyping.peer.chat_id = encryptedChat.id;
                        tL_messages_setTyping.peer.access_hash = encryptedChat.access_hash;
                        tL_messages_setTyping.typing = true;
                        hashMap.put(Long.valueOf(j), Boolean.valueOf(true));
                        sendRequest = ConnectionsManager.getInstance().sendRequest(tL_messages_setTyping, new AnonymousClass50(i, j), UPDATE_MASK_AVATAR);
                        if (i2 != 0) {
                            ConnectionsManager.getInstance().bindRequestToGuid(sendRequest, i2);
                        }
                    }
                }
            }
        }
    }

    public void setLastCreatedDialogId(long j, boolean z) {
        Utilities.stageQueue.postRunnable(new C05437(z, j));
    }

    public void sortDialogs(HashMap<Integer, Chat> hashMap) {
        this.dialogsServerOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogsUnreadOnly.clear();
        this.dialogsJustGroupsOnly.clear();
        this.dialogsSuperGroupsOnly.clear();
        this.dialogsChannelOnly.clear();
        this.dialogsContactOnly.clear();
        this.dialogsFavoriteOnly.clear();
        this.dialogsHiddenOnly.clear();
        this.dialogsBotOnly.clear();
        Collections.sort(this.dialogs, this.dialogComparator);
        int i = 0;
        int i2 = -1;
        while (i < this.dialogs.size()) {
            int i3;
            TL_dialog tL_dialog = (TL_dialog) this.dialogs.get(i);
            if (tL_dialog.id == ((long) UserConfig.getClientUserId()) && !MoboConstants.aJ && MoboConstants.aF) {
                i3 = i;
            } else {
                int i4 = (int) (tL_dialog.id >> UPDATE_MASK_CHAT_MEMBERS);
                int i5 = (int) tL_dialog.id;
                if (!(i5 == 0 || i4 == UPDATE_MASK_NAME)) {
                    this.dialogsServerOnly.add(tL_dialog);
                    Chat chat;
                    if (DialogObject.isChannel(tL_dialog)) {
                        chat = getChat(Integer.valueOf(-i5));
                        if (chat != null && ((chat.megagroup && chat.editor) || chat.creator)) {
                            this.dialogsGroupsOnly.add(tL_dialog);
                        }
                        if (chat != null) {
                            if (chat.megagroup) {
                                this.dialogsSuperGroupsOnly.add(tL_dialog);
                            } else {
                                this.dialogsChannelOnly.add(tL_dialog);
                            }
                        }
                    } else if (i5 < 0) {
                        if (hashMap != null) {
                            chat = (Chat) hashMap.get(Integer.valueOf(-i5));
                            if (!(chat == null || chat.migrated_to == null)) {
                                this.dialogs.remove(i);
                                i--;
                                i3 = i2;
                            }
                        }
                        this.dialogsGroupsOnly.add(tL_dialog);
                        this.dialogsJustGroupsOnly.add(tL_dialog);
                    }
                    if (i5 > 0) {
                        User user = getUser(Integer.valueOf((int) tL_dialog.id));
                        if (user == null || !user.bot) {
                            this.dialogsContactOnly.add(tL_dialog);
                        } else {
                            this.dialogsBotOnly.add(tL_dialog);
                        }
                    }
                }
                if (FavoriteUtil.m1142b(Long.valueOf(tL_dialog.id))) {
                    this.dialogsFavoriteOnly.add(tL_dialog);
                }
                if (getEncryptedChat(Integer.valueOf(i4)) instanceof TL_encryptedChat) {
                    this.dialogsContactOnly.add(tL_dialog);
                }
                if (HiddenConfig.m1399b(Long.valueOf(tL_dialog.id))) {
                    this.dialogsHiddenOnly.add(tL_dialog);
                }
                if (tL_dialog.unread_count > 0) {
                    this.dialogsUnreadOnly.add(tL_dialog);
                }
                i3 = i2;
            }
            i += UPDATE_MASK_NAME;
            i2 = i3;
        }
        if (i2 != -1 && !MoboConstants.aJ && MoboConstants.aF) {
            this.dialogs.remove(i2);
        }
    }

    public void startShortPoll(int i, boolean z) {
        Utilities.stageQueue.postRunnable(new AnonymousClass92(z, i));
    }

    public void toggleAdminMode(int i, boolean z) {
        TLObject tL_messages_toggleChatAdmins = new TL_messages_toggleChatAdmins();
        tL_messages_toggleChatAdmins.chat_id = i;
        tL_messages_toggleChatAdmins.enabled = z;
        ConnectionsManager.getInstance().sendRequest(tL_messages_toggleChatAdmins, new AnonymousClass76(i));
    }

    public void toggleUserAdmin(int i, int i2, boolean z) {
        TLObject tL_messages_editChatAdmin = new TL_messages_editChatAdmin();
        tL_messages_editChatAdmin.chat_id = i;
        tL_messages_editChatAdmin.user_id = getInputUser(i2);
        tL_messages_editChatAdmin.is_admin = z;
        ConnectionsManager.getInstance().sendRequest(tL_messages_editChatAdmin, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
            }
        });
    }

    public void toogleChannelInvites(int i, boolean z) {
        TLObject tL_channels_toggleInvites = new TL_channels_toggleInvites();
        tL_channels_toggleInvites.channel = getInputChannel(i);
        tL_channels_toggleInvites.enabled = z;
        ConnectionsManager.getInstance().sendRequest(tL_channels_toggleInvites, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
                if (tLObject != null) {
                    MessagesController.this.processUpdates((Updates) tLObject, false);
                }
            }
        }, UPDATE_MASK_USER_PRINT);
    }

    public void toogleChannelSignatures(int i, boolean z) {
        TLObject tL_channels_toggleSignatures = new TL_channels_toggleSignatures();
        tL_channels_toggleSignatures.channel = getInputChannel(i);
        tL_channels_toggleSignatures.enabled = z;
        ConnectionsManager.getInstance().sendRequest(tL_channels_toggleSignatures, new RequestDelegate() {

            /* renamed from: com.hanista.mobogram.messenger.MessagesController.72.1 */
            class C05351 implements Runnable {
                C05351() {
                }

                public void run() {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.updateInterfaces;
                    Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL);
                    instance.postNotificationName(i, objArr);
                }
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tLObject != null) {
                    MessagesController.this.processUpdates((Updates) tLObject, false);
                    AndroidUtilities.runOnUIThread(new C05351());
                }
            }
        }, UPDATE_MASK_USER_PRINT);
    }

    public void unblockUser(int i) {
        TLObject tL_contacts_unblock = new TL_contacts_unblock();
        User user = getUser(Integer.valueOf(i));
        if (user != null) {
            this.blockedUsers.remove(Integer.valueOf(user.id));
            tL_contacts_unblock.id = getInputUser(user);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            ConnectionsManager.getInstance().sendRequest(tL_contacts_unblock, new AnonymousClass25(user));
        }
    }

    public void unregistedPush() {
        if (UserConfig.registeredForPush && UserConfig.pushString.length() == 0) {
            TLObject tL_account_unregisterDevice = new TL_account_unregisterDevice();
            tL_account_unregisterDevice.token = UserConfig.pushString;
            tL_account_unregisterDevice.token_type = UPDATE_MASK_AVATAR;
            ConnectionsManager.getInstance().sendRequest(tL_account_unregisterDevice, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                }
            });
        }
    }

    public void updateChannelAbout(int i, String str, ChatFull chatFull) {
        if (chatFull != null) {
            TLObject tL_channels_editAbout = new TL_channels_editAbout();
            tL_channels_editAbout.channel = getInputChannel(i);
            tL_channels_editAbout.about = str;
            ConnectionsManager.getInstance().sendRequest(tL_channels_editAbout, new AnonymousClass73(chatFull, str), UPDATE_MASK_USER_PRINT);
        }
    }

    public void updateChannelUserName(int i, String str) {
        TLObject tL_channels_updateUsername = new TL_channels_updateUsername();
        tL_channels_updateUsername.channel = getInputChannel(i);
        tL_channels_updateUsername.username = str;
        ConnectionsManager.getInstance().sendRequest(tL_channels_updateUsername, new AnonymousClass74(i, str), UPDATE_MASK_USER_PRINT);
    }

    public void updateConfig(TL_config tL_config) {
        AndroidUtilities.runOnUIThread(new C05063(tL_config));
    }

    protected void updateInterfaceWithMessages(long j, ArrayList<MessageObject> arrayList) {
        updateInterfaceWithMessages(j, arrayList, false);
    }

    protected void updateInterfaceWithMessages(long j, ArrayList<MessageObject> arrayList, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            Object obj;
            Object obj2 = ((int) j) == 0 ? UPDATE_MASK_NAME : null;
            MessageObject messageObject = null;
            int i = 0;
            Object obj3 = null;
            int i2 = 0;
            while (i2 < arrayList.size()) {
                MessageObject messageObject2 = (MessageObject) arrayList.get(i2);
                if (messageObject == null || ((obj2 == null && messageObject2.getId() > messageObject.getId()) || (((obj2 != null || (messageObject2.getId() < 0 && messageObject.getId() < 0)) && messageObject2.getId() < messageObject.getId()) || messageObject2.messageOwner.date > messageObject.messageOwner.date))) {
                    if (messageObject2.messageOwner.to_id.channel_id != 0) {
                        i = messageObject2.messageOwner.to_id.channel_id;
                        messageObject = messageObject2;
                    } else {
                        messageObject = messageObject2;
                    }
                }
                if (!(!messageObject2.isOut() || messageObject2.isSending() || messageObject2.isForwarded())) {
                    if (messageObject2.isNewGif()) {
                        StickersQuery.addRecentGif(messageObject2.messageOwner.media.document, messageObject2.messageOwner.date);
                    } else if (messageObject2.isSticker()) {
                        StickersQuery.addRecentSticker(0, messageObject2.messageOwner.media.document, messageObject2.messageOwner.date);
                    }
                }
                obj = (messageObject2.isOut() && messageObject2.isSent()) ? UPDATE_MASK_NAME : obj3;
                i2 += UPDATE_MASK_NAME;
                obj3 = obj;
            }
            MessagesQuery.loadReplyMessagesForMessages(arrayList, j);
            NotificationCenter instance = NotificationCenter.getInstance();
            int i3 = NotificationCenter.didReceivedNewMessages;
            Object[] objArr = new Object[UPDATE_MASK_AVATAR];
            objArr[0] = Long.valueOf(j);
            objArr[UPDATE_MASK_NAME] = arrayList;
            instance.postNotificationName(i3, objArr);
            if (messageObject != null) {
                TL_dialog tL_dialog = (TL_dialog) this.dialogs_dict.get(Long.valueOf(j));
                MessageObject messageObject3;
                if (!(messageObject.messageOwner.action instanceof TL_messageActionChatMigrateTo)) {
                    if (tL_dialog == null) {
                        if (!z) {
                            Chat chat = getChat(Integer.valueOf(i));
                            if (i != 0 && chat == null) {
                                return;
                            }
                            if (chat == null || !chat.left) {
                                TL_dialog tL_dialog2 = new TL_dialog();
                                tL_dialog2.id = j;
                                tL_dialog2.unread_count = 0;
                                tL_dialog2.top_message = messageObject.getId();
                                tL_dialog2.last_message_date = messageObject.messageOwner.date;
                                tL_dialog2.flags = ChatObject.isChannel(chat) ? UPDATE_MASK_NAME : 0;
                                this.dialogs_dict.put(Long.valueOf(j), tL_dialog2);
                                this.dialogs.add(tL_dialog2);
                                this.dialogMessage.put(Long.valueOf(j), messageObject);
                                if (messageObject.messageOwner.to_id.channel_id == 0) {
                                    this.dialogMessagesByIds.put(Integer.valueOf(messageObject.getId()), messageObject);
                                    if (messageObject.messageOwner.random_id != 0) {
                                        this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject.messageOwner.random_id), messageObject);
                                    }
                                }
                                this.nextDialogsCacheOffset += UPDATE_MASK_NAME;
                                obj = UPDATE_MASK_NAME;
                            } else {
                                return;
                            }
                        }
                        obj = null;
                    } else {
                        if ((tL_dialog.top_message > 0 && messageObject.getId() > 0 && messageObject.getId() > tL_dialog.top_message) || ((tL_dialog.top_message < 0 && messageObject.getId() < 0 && messageObject.getId() < tL_dialog.top_message) || !this.dialogMessage.containsKey(Long.valueOf(j)) || tL_dialog.top_message < 0 || tL_dialog.last_message_date <= messageObject.messageOwner.date)) {
                            messageObject3 = (MessageObject) this.dialogMessagesByIds.remove(Integer.valueOf(tL_dialog.top_message));
                            if (!(messageObject3 == null || messageObject3.messageOwner.random_id == 0)) {
                                this.dialogMessagesByRandomIds.remove(Long.valueOf(messageObject3.messageOwner.random_id));
                            }
                            tL_dialog.top_message = messageObject.getId();
                            if (z) {
                                obj = null;
                            } else {
                                tL_dialog.last_message_date = messageObject.messageOwner.date;
                                obj = UPDATE_MASK_NAME;
                            }
                            this.dialogMessage.put(Long.valueOf(j), messageObject);
                            if (messageObject.messageOwner.to_id.channel_id == 0) {
                                this.dialogMessagesByIds.put(Integer.valueOf(messageObject.getId()), messageObject);
                                if (messageObject.messageOwner.random_id != 0) {
                                    this.dialogMessagesByRandomIds.put(Long.valueOf(messageObject.messageOwner.random_id), messageObject);
                                }
                            }
                        }
                        obj = null;
                    }
                    if (obj != null) {
                        sortDialogs(null);
                    }
                    if (obj3 != null) {
                        SearchQuery.increasePeerRaiting(j);
                    }
                } else if (tL_dialog != null) {
                    this.dialogs.remove(tL_dialog);
                    this.dialogsServerOnly.remove(tL_dialog);
                    this.dialogsGroupsOnly.remove(tL_dialog);
                    this.dialogsContactOnly.remove(tL_dialog);
                    this.dialogsChannelOnly.remove(tL_dialog);
                    this.dialogsFavoriteOnly.remove(tL_dialog);
                    this.dialogsUnreadOnly.remove(tL_dialog);
                    this.dialogsJustGroupsOnly.remove(tL_dialog);
                    this.dialogsSuperGroupsOnly.remove(tL_dialog);
                    this.dialogsHiddenOnly.remove(tL_dialog);
                    this.dialogsBotOnly.remove(tL_dialog);
                    this.dialogs_dict.remove(Long.valueOf(tL_dialog.id));
                    this.dialogs_read_inbox_max.remove(Long.valueOf(tL_dialog.id));
                    this.dialogs_read_outbox_max.remove(Long.valueOf(tL_dialog.id));
                    this.nextDialogsCacheOffset--;
                    this.dialogMessage.remove(Long.valueOf(tL_dialog.id));
                    messageObject3 = (MessageObject) this.dialogMessagesByIds.remove(Integer.valueOf(tL_dialog.top_message));
                    if (!(messageObject3 == null || messageObject3.messageOwner.random_id == 0)) {
                        this.dialogMessagesByRandomIds.remove(Long.valueOf(messageObject3.messageOwner.random_id));
                    }
                    tL_dialog.top_message = 0;
                    NotificationsController.getInstance().removeNotificationsForDialog(tL_dialog.id);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                }
            }
        }
    }

    public void updateTimerProc() {
        ArrayList arrayList;
        int i;
        int intValue;
        Long l;
        int i2;
        ArrayList arrayList2;
        long currentTimeMillis = System.currentTimeMillis();
        checkDeletingTask(false);
        if (UserConfig.isClientActivated()) {
            TLObject tL_account_updateStatus;
            if (ConnectionsManager.getInstance().getPauseTime() != 0 || !ApplicationLoader.isScreenOn || ApplicationLoader.mainInterfacePaused || UserConfig.isRobot) {
                if (!(this.statusSettingState == UPDATE_MASK_AVATAR || this.offlineSent || Math.abs(System.currentTimeMillis() - ConnectionsManager.getInstance().getPauseTime()) < 2000 || UserConfig.isRobot)) {
                    this.statusSettingState = UPDATE_MASK_AVATAR;
                    if (this.statusRequest != 0) {
                        ConnectionsManager.getInstance().cancelRequest(this.statusRequest, true);
                    }
                    tL_account_updateStatus = new TL_account_updateStatus();
                    tL_account_updateStatus.offline = true;
                    this.statusRequest = ConnectionsManager.getInstance().sendRequest(tL_account_updateStatus, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tL_error == null) {
                                MessagesController.this.offlineSent = true;
                            } else if (MessagesController.this.lastStatusUpdateTime != 0) {
                                MessagesController.this.lastStatusUpdateTime = MessagesController.this.lastStatusUpdateTime + HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS;
                            }
                            MessagesController.this.statusRequest = 0;
                        }
                    });
                }
            } else if (this.statusSettingState != UPDATE_MASK_NAME && (this.lastStatusUpdateTime == 0 || Math.abs(System.currentTimeMillis() - this.lastStatusUpdateTime) >= 55000 || this.offlineSent)) {
                this.statusSettingState = UPDATE_MASK_NAME;
                if (this.statusRequest != 0) {
                    ConnectionsManager.getInstance().cancelRequest(this.statusRequest, true);
                }
                if (MoboConstants.f1338e) {
                    tL_account_updateStatus = new TL_account_updateStatus();
                    tL_account_updateStatus.offline = true;
                    this.statusRequest = ConnectionsManager.getInstance().sendRequest(tL_account_updateStatus, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tL_error == null) {
                                MessagesController.this.offlineSent = true;
                                MessagesController.this.statusSettingState = 0;
                            } else if (MessagesController.this.lastStatusUpdateTime != 0) {
                                MessagesController.this.lastStatusUpdateTime = MessagesController.this.lastStatusUpdateTime + HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS;
                            }
                            MessagesController.this.statusRequest = 0;
                        }
                    });
                } else {
                    tL_account_updateStatus = new TL_account_updateStatus();
                    tL_account_updateStatus.offline = false;
                    this.statusRequest = ConnectionsManager.getInstance().sendRequest(tL_account_updateStatus, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tL_error == null) {
                                MessagesController.this.lastStatusUpdateTime = System.currentTimeMillis();
                                MessagesController.this.offlineSent = false;
                                MessagesController.this.statusSettingState = 0;
                            } else if (MessagesController.this.lastStatusUpdateTime != 0) {
                                MessagesController.this.lastStatusUpdateTime = MessagesController.this.lastStatusUpdateTime + HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS;
                            }
                            MessagesController.this.statusRequest = 0;
                        }
                    });
                }
            }
            if (!this.updatesQueueChannels.isEmpty()) {
                arrayList = new ArrayList(this.updatesQueueChannels.keySet());
                for (i = 0; i < arrayList.size(); i += UPDATE_MASK_NAME) {
                    intValue = ((Integer) arrayList.get(i)).intValue();
                    l = (Long) this.updatesStartWaitTimeChannels.get(Integer.valueOf(intValue));
                    if (l != null && l.longValue() + 1500 < currentTimeMillis) {
                        FileLog.m16e("tmessages", "QUEUE CHANNEL " + intValue + " UPDATES WAIT TIMEOUT - CHECK QUEUE");
                        processChannelsUpdatesQueue(intValue, 0);
                    }
                }
            }
            i2 = 0;
            while (i2 < 3) {
                if (getUpdatesStartTime(i2) != 0 && getUpdatesStartTime(i2) + 1500 < currentTimeMillis) {
                    FileLog.m16e("tmessages", i2 + " QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE");
                    processUpdatesQueue(i2, 0);
                }
                i2 += UPDATE_MASK_NAME;
            }
        }
        if (!(this.channelViewsToSend.size() == 0 && this.channelViewsToReload.size() == 0) && Math.abs(System.currentTimeMillis() - this.lastViewsCheckTime) >= HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS) {
            this.lastViewsCheckTime = System.currentTimeMillis();
            intValue = 0;
            while (intValue < UPDATE_MASK_AVATAR) {
                SparseArray sparseArray = intValue == 0 ? this.channelViewsToSend : this.channelViewsToReload;
                if (sparseArray.size() != 0) {
                    int i3 = 0;
                    while (i3 < sparseArray.size()) {
                        int keyAt = sparseArray.keyAt(i3);
                        TLObject tL_messages_getMessagesViews = new TL_messages_getMessagesViews();
                        tL_messages_getMessagesViews.peer = getInputPeer(keyAt);
                        tL_messages_getMessagesViews.id = (ArrayList) sparseArray.get(keyAt);
                        tL_messages_getMessagesViews.increment = i3 == 0;
                        ConnectionsManager.getInstance().sendRequest(tL_messages_getMessagesViews, new AnonymousClass45(keyAt, tL_messages_getMessagesViews));
                        i3 += UPDATE_MASK_NAME;
                    }
                    sparseArray.clear();
                }
                intValue += UPDATE_MASK_NAME;
            }
        }
        if (!this.onlinePrivacy.isEmpty()) {
            arrayList = null;
            intValue = ConnectionsManager.getInstance().getCurrentTime();
            for (Entry entry : this.onlinePrivacy.entrySet()) {
                if (((Integer) entry.getValue()).intValue() < intValue - 30) {
                    arrayList2 = arrayList == null ? new ArrayList() : arrayList;
                    arrayList2.add(entry.getKey());
                } else {
                    arrayList2 = arrayList;
                }
                arrayList = arrayList2;
            }
            if (arrayList != null) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    this.onlinePrivacy.remove((Integer) it.next());
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i = NotificationCenter.updateInterfaces;
                        Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                        objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_STATUS);
                        instance.postNotificationName(i, objArr);
                    }
                });
            }
        }
        if (this.shortPollChannels.size() != 0) {
            for (i2 = 0; i2 < this.shortPollChannels.size(); i2 += UPDATE_MASK_NAME) {
                i = this.shortPollChannels.keyAt(i2);
                if (((long) this.shortPollChannels.get(i)) < System.currentTimeMillis() / 1000) {
                    this.shortPollChannels.delete(i);
                    if (this.needShortPollChannels.indexOfKey(i) >= 0) {
                        getChannelDifference(i);
                    }
                }
            }
        }
        if (!this.printingUsers.isEmpty() || this.lastPrintingStringCount != this.printingUsers.size()) {
            ArrayList arrayList3 = new ArrayList(this.printingUsers.keySet());
            intValue = 0;
            Object obj = null;
            while (intValue < arrayList3.size()) {
                l = (Long) arrayList3.get(intValue);
                arrayList2 = (ArrayList) this.printingUsers.get(l);
                int i4 = 0;
                Object obj2 = obj;
                while (i4 < arrayList2.size()) {
                    PrintingUser printingUser = (PrintingUser) arrayList2.get(i4);
                    if (printingUser.lastTime + 5900 < currentTimeMillis) {
                        obj2 = UPDATE_MASK_NAME;
                        arrayList2.remove(printingUser);
                        i4--;
                    }
                    i4 += UPDATE_MASK_NAME;
                }
                if (arrayList2.isEmpty()) {
                    this.printingUsers.remove(l);
                    arrayList3.remove(intValue);
                    intValue--;
                }
                intValue += UPDATE_MASK_NAME;
                obj = obj2;
            }
            updatePrintingStrings();
            if (obj != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter instance = NotificationCenter.getInstance();
                        int i = NotificationCenter.updateInterfaces;
                        Object[] objArr = new Object[MessagesController.UPDATE_MASK_NAME];
                        objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_USER_PRINT);
                        instance.postNotificationName(i, objArr);
                    }
                });
            }
        }
    }

    public void uploadAndApplyUserAvatar(PhotoSize photoSize) {
        if (photoSize != null) {
            this.uploadingAvatar = FileLoader.getInstance().getDirectory(UPDATE_MASK_STATUS) + "/" + photoSize.location.volume_id + "_" + photoSize.location.local_id + ".jpg";
            FileLoader.getInstance().uploadFile(this.uploadingAvatar, false, true);
        }
    }
}
