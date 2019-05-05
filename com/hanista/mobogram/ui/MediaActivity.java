package com.hanista.mobogram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.query.SharedMediaQuery;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.component.PowerView;
import com.hanista.mobogram.mobo.download.DownloadMessagesStorage;
import com.hanista.mobogram.mobo.download.DownloadUtil;
import com.hanista.mobogram.mobo.p000a.Archive;
import com.hanista.mobogram.mobo.p000a.ArchiveUtil;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p009j.SharedGifCell.SharedGifCell;
import com.hanista.mobogram.mobo.p010k.MaterialHelperUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_search;
import com.hanista.mobogram.tgnet.TLRPC.TL_webPageEmpty;
import com.hanista.mobogram.tgnet.TLRPC.WebPage;
import com.hanista.mobogram.tgnet.TLRPC.messages_Messages;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import com.hanista.mobogram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import com.hanista.mobogram.ui.ActionBar.BackDrawable;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Adapters.BaseSectionsAdapter;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
import com.hanista.mobogram.ui.Cells.GreySectionCell;
import com.hanista.mobogram.ui.Cells.LoadingCell;
import com.hanista.mobogram.ui.Cells.SharedDocumentCell;
import com.hanista.mobogram.ui.Cells.SharedLinkCell;
import com.hanista.mobogram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate;
import com.hanista.mobogram.ui.Cells.SharedMediaSectionCell;
import com.hanista.mobogram.ui.Cells.SharedPhotoVideoCell;
import com.hanista.mobogram.ui.Cells.SharedPhotoVideoCell.SharedPhotoVideoCellDelegate;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.NumberTextView;
import com.hanista.mobogram.ui.Components.PlayerView;
import com.hanista.mobogram.ui.Components.SectionsListView;
import com.hanista.mobogram.ui.Components.ShareAlert;
import com.hanista.mobogram.ui.Components.WebFrameLayout;
import com.hanista.mobogram.ui.DialogsActivity.DialogsActivityDelegate;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class MediaActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private static final int DOWNLOAD_TYPE_ALL = 0;
    private static final int DOWNLOAD_TYPE_DOWNLOADED = 1;
    private static final int DOWNLOAD_TYPE_NOT_DOWNLOADED = 2;
    private static final int MEDIA_TYPE_ALL = 0;
    private static final int MEDIA_TYPE_GIF = 3;
    private static final int MEDIA_TYPE_PHOTO = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static final int add_to_download_list = 190;
    private static final int delete = 4;
    private static final int files_item = 6;
    private static final int forward = 3;
    private static final int forward_noname = 111;
    private static final int links_item = 7;
    private static final int menu_download = 52;
    private static final int multi_forward = 112;
    private static final int music_item = 8;
    private static final int select_all = 191;
    private static final int shared_media_gif_item = 83;
    private static final int shared_media_item = 1;
    private static final int shared_media_photo_item = 2;
    private static final int shared_media_video_item = 5;
    private ArrayList<View> actionModeViews;
    private Archive archive;
    private SharedDocumentsAdapter audioAdapter;
    private MediaSearchAdapter audioSearchAdapter;
    private int cantDeleteMessagesCount;
    private ArrayList<SharedPhotoVideoCell> cellCache;
    private int columnsCount;
    private boolean deleteFilesOnDeleteMessage;
    private long dialog_id;
    private SharedDocumentsAdapter documentsAdapter;
    private MediaSearchAdapter documentsSearchAdapter;
    private ActionBarMenuItem downloadItem;
    private int downloadType;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private ImageView emptyImageView;
    private TextView emptyTextView;
    private LinearLayout emptyView;
    private SharedGifAdapter gifAdapter;
    protected ChatFull info;
    private SharedLinksAdapter linksAdapter;
    private MediaSearchAdapter linksSearchAdapter;
    private SectionsListView listView;
    private int mediaType;
    private long mergeDialogId;
    private SharedPhotoVideoAdapter photoAdapter;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private ActionBarPopupWindowLayout popupLayout;
    private LinearLayout progressView;
    private boolean scrolling;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private HashMap<Integer, MessageObject>[] selectedFiles;
    private NumberTextView selectedMessagesCountTextView;
    private int selectedMode;
    private SharedMediaData[] sharedMediaData;
    private SharedPhotoVideoAdapter videoAdapter;

    /* renamed from: com.hanista.mobogram.ui.MediaActivity.1 */
    class C16971 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.MediaActivity.1.1 */
        class C16941 implements OnClickListener {
            C16941() {
            }

            public void onClick(View view) {
                MediaActivity.this.deleteFilesOnDeleteMessage = !MediaActivity.this.deleteFilesOnDeleteMessage;
                ((CheckBoxCell) view).setChecked(MediaActivity.this.deleteFilesOnDeleteMessage, true);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.MediaActivity.1.2 */
        class C16952 implements DialogInterface.OnClickListener {
            C16952() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                for (int i2 = MediaActivity.shared_media_item; i2 >= 0; i2--) {
                    MessageObject messageObject;
                    List arrayList = new ArrayList(MediaActivity.this.selectedFiles[i2].keySet());
                    ArrayList arrayList2 = null;
                    EncryptedChat encryptedChat = null;
                    int i3 = MediaActivity.MEDIA_TYPE_ALL;
                    if (!arrayList.isEmpty()) {
                        messageObject = (MessageObject) MediaActivity.this.selectedFiles[i2].get(arrayList.get(MediaActivity.MEDIA_TYPE_ALL));
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            i3 = messageObject.messageOwner.to_id.channel_id;
                        }
                    }
                    if (((int) MediaActivity.this.dialog_id) == 0) {
                        encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (MediaActivity.this.dialog_id >> 32)));
                    }
                    if (encryptedChat != null) {
                        arrayList2 = new ArrayList();
                        for (Entry value : MediaActivity.this.selectedFiles[i2].entrySet()) {
                            messageObject = (MessageObject) value.getValue();
                            if (!(messageObject.messageOwner.random_id == 0 || messageObject.type == 10)) {
                                arrayList2.add(Long.valueOf(messageObject.messageOwner.random_id));
                            }
                        }
                    }
                    MessagesController.getInstance().deleteMessages(arrayList, arrayList2, encryptedChat, i3, MediaActivity.this.deleteFilesOnDeleteMessage);
                    if (MediaActivity.this.dialog_id == ((long) UserConfig.getClientUserId())) {
                        ArchiveUtil.m263a(arrayList);
                    }
                    MediaActivity.this.selectedFiles[i2].clear();
                }
                MediaActivity.this.actionBar.hideActionMode();
                MediaActivity.this.actionBar.closeSearchField();
                MediaActivity.this.cantDeleteMessagesCount = MediaActivity.MEDIA_TYPE_ALL;
            }
        }

        /* renamed from: com.hanista.mobogram.ui.MediaActivity.1.3 */
        class C16963 implements DialogsActivityDelegate {
            final /* synthetic */ int val$id;

            C16963(int i) {
                this.val$id = i;
            }

            public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
                int i = (int) j;
                if (i != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("scrollToTopOnResume", true);
                    if (i > 0) {
                        bundle.putInt("user_id", i);
                    } else if (i < 0) {
                        bundle.putInt("chat_id", -i);
                    }
                    if (MessagesController.checkCanOpenChat(bundle, dialogsActivity)) {
                        ArrayList arrayList = new ArrayList();
                        for (int i2 = MediaActivity.shared_media_item; i2 >= 0; i2--) {
                            Object arrayList2 = new ArrayList(MediaActivity.this.selectedFiles[i2].keySet());
                            Collections.sort(arrayList2);
                            Iterator it = arrayList2.iterator();
                            while (it.hasNext()) {
                                Integer num = (Integer) it.next();
                                if (num.intValue() > 0) {
                                    arrayList.add(MediaActivity.this.selectedFiles[i2].get(num));
                                }
                            }
                            MediaActivity.this.selectedFiles[i2].clear();
                        }
                        MediaActivity.this.cantDeleteMessagesCount = MediaActivity.MEDIA_TYPE_ALL;
                        MediaActivity.this.actionBar.hideActionMode();
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[MediaActivity.MEDIA_TYPE_ALL]);
                        BaseFragment chatActivity = new ChatActivity(bundle);
                        ChatActivity.forwardNoName = this.val$id == MediaActivity.forward_noname;
                        MediaActivity.this.presentFragment(chatActivity, true);
                        chatActivity.showReplyPanel(true, null, arrayList, null, false, false);
                        if (!AndroidUtilities.isTablet()) {
                            MediaActivity.this.removeSelfFromStack();
                            return;
                        }
                        return;
                    }
                    return;
                }
                dialogsActivity.finishFragment();
            }
        }

        C16971() {
        }

        public void onItemClick(int i) {
            int i2 = MediaActivity.shared_media_item;
            if (i == -1) {
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    while (i2 >= 0) {
                        MediaActivity.this.selectedFiles[i2].clear();
                        i2--;
                    }
                    MediaActivity.this.cantDeleteMessagesCount = MediaActivity.MEDIA_TYPE_ALL;
                    MediaActivity.this.actionBar.hideActionMode();
                    MediaActivity.this.listView.invalidateViews();
                    return;
                }
                MediaActivity.this.finishFragment();
            } else if (i == MediaActivity.menu_download) {
                MediaActivity.this.showSelectDownloadTypesDialog();
            } else if (i == MediaActivity.shared_media_item) {
                if (MediaActivity.this.selectedMode != 0 || MediaActivity.this.mediaType != 0) {
                    MediaActivity.this.selectedMode = MediaActivity.MEDIA_TYPE_ALL;
                    MediaActivity.this.mediaType = MediaActivity.MEDIA_TYPE_ALL;
                    MediaActivity.this.switchToCurrentSelectedMode();
                    MediaActivity.this.reloadAll();
                }
            } else if (i == MediaActivity.shared_media_photo_item) {
                if (MediaActivity.this.selectedMode != 0 || MediaActivity.this.mediaType != MediaActivity.shared_media_item) {
                    MediaActivity.this.selectedMode = MediaActivity.MEDIA_TYPE_ALL;
                    MediaActivity.this.mediaType = MediaActivity.shared_media_item;
                    MediaActivity.this.switchToCurrentSelectedMode();
                    MediaActivity.this.reloadAll();
                }
            } else if (i == MediaActivity.shared_media_gif_item) {
                if (MediaActivity.this.selectedMode != MediaActivity.shared_media_video_item) {
                    MediaActivity.this.selectedMode = MediaActivity.shared_media_video_item;
                    MediaActivity.this.mediaType = MediaActivity.forward;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i == MediaActivity.shared_media_video_item) {
                if (MediaActivity.this.selectedMode != 0 || MediaActivity.this.mediaType != MediaActivity.shared_media_photo_item) {
                    MediaActivity.this.selectedMode = MediaActivity.MEDIA_TYPE_ALL;
                    MediaActivity.this.mediaType = MediaActivity.shared_media_photo_item;
                    MediaActivity.this.switchToCurrentSelectedMode();
                    MediaActivity.this.reloadAll();
                }
            } else if (i == MediaActivity.files_item) {
                if (MediaActivity.this.selectedMode != MediaActivity.shared_media_item) {
                    MediaActivity.this.selectedMode = MediaActivity.shared_media_item;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i == MediaActivity.links_item) {
                if (MediaActivity.this.selectedMode != MediaActivity.forward) {
                    MediaActivity.this.selectedMode = MediaActivity.forward;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i == MediaActivity.music_item) {
                if (MediaActivity.this.selectedMode != MediaActivity.delete) {
                    MediaActivity.this.selectedMode = MediaActivity.delete;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i == MediaActivity.delete) {
                if (MediaActivity.this.getParentActivity() != null) {
                    int i3 = MediaActivity.shared_media_item;
                    int i4 = MediaActivity.MEDIA_TYPE_ALL;
                    while (i3 >= 0) {
                        int i5;
                        for (Entry value : MediaActivity.this.selectedFiles[i3].entrySet()) {
                            MessageObject messageObject = (MessageObject) value.getValue();
                            messageObject.checkMediaExistance();
                            if (messageObject.mediaExists) {
                                i5 = MediaActivity.shared_media_item;
                                break;
                            }
                        }
                        i5 = i4;
                        i3--;
                        i4 = i5;
                    }
                    Builder builder = new Builder(MediaActivity.this.getParentActivity());
                    MediaActivity.this.deleteFilesOnDeleteMessage = MoboConstants.aE;
                    if (i4 != 0) {
                        View frameLayout = new FrameLayout(MediaActivity.this.getParentActivity());
                        if (VERSION.SDK_INT >= 21) {
                            frameLayout.setPadding(MediaActivity.MEDIA_TYPE_ALL, AndroidUtilities.dp(8.0f), MediaActivity.MEDIA_TYPE_ALL, MediaActivity.MEDIA_TYPE_ALL);
                        }
                        View createDeleteFileCheckBox = AndroidUtilities.createDeleteFileCheckBox(MediaActivity.this.getParentActivity());
                        frameLayout.addView(createDeleteFileCheckBox, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
                        createDeleteFileCheckBox.setOnClickListener(new C16941());
                        builder.setView(frameLayout);
                    }
                    Object[] objArr = new Object[MediaActivity.shared_media_item];
                    objArr[MediaActivity.MEDIA_TYPE_ALL] = LocaleController.formatPluralString("items", MediaActivity.this.selectedFiles[MediaActivity.MEDIA_TYPE_ALL].size() + MediaActivity.this.selectedFiles[MediaActivity.shared_media_item].size());
                    builder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", C0338R.string.AreYouSureDeleteMessages, objArr));
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C16952());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    MediaActivity.this.showDialog(builder.create());
                }
            } else if (i == MediaActivity.forward || i == MediaActivity.forward_noname) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putInt("dialogsType", MediaActivity.shared_media_item);
                BaseFragment dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(new C16963(i));
                MediaActivity.this.presentFragment(dialogsActivity);
            } else if (i == MediaActivity.multi_forward) {
                MediaActivity.this.multipleForward();
            } else if (i == MediaActivity.add_to_download_list) {
                MediaActivity.this.addMessagesToDownloadList();
            } else if (i == MediaActivity.select_all) {
                MediaActivity.this.doSelectAllMessages();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.MediaActivity.2 */
    class C16982 extends ActionBarMenuItemSearchListener {
        C16982() {
        }

        public void onSearchCollapse() {
            MediaActivity.this.dropDownContainer.setVisibility(MediaActivity.MEDIA_TYPE_ALL);
            if (MediaActivity.this.selectedMode == MediaActivity.shared_media_item) {
                MediaActivity.this.documentsSearchAdapter.search(null);
            } else if (MediaActivity.this.selectedMode == MediaActivity.forward) {
                MediaActivity.this.linksSearchAdapter.search(null);
            } else if (MediaActivity.this.selectedMode == MediaActivity.delete) {
                MediaActivity.this.audioSearchAdapter.search(null);
            }
            MediaActivity.this.searching = false;
            MediaActivity.this.searchWas = false;
            MediaActivity.this.switchToCurrentSelectedMode();
        }

        public void onSearchExpand() {
            MediaActivity.this.dropDownContainer.setVisibility(MediaActivity.music_item);
            MediaActivity.this.searching = true;
        }

        public void onTextChanged(EditText editText) {
            String obj = editText.getText().toString();
            if (obj.length() != 0) {
                MediaActivity.this.searchWas = true;
                MediaActivity.this.switchToCurrentSelectedMode();
            }
            if (MediaActivity.this.selectedMode == MediaActivity.shared_media_item) {
                if (MediaActivity.this.documentsSearchAdapter != null) {
                    MediaActivity.this.documentsSearchAdapter.search(obj);
                }
            } else if (MediaActivity.this.selectedMode == MediaActivity.forward) {
                if (MediaActivity.this.linksSearchAdapter != null) {
                    MediaActivity.this.linksSearchAdapter.search(obj);
                }
            } else if (MediaActivity.this.selectedMode == MediaActivity.delete && MediaActivity.this.audioSearchAdapter != null) {
                MediaActivity.this.audioSearchAdapter.search(obj);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.MediaActivity.3 */
    class C16993 implements OnClickListener {
        C16993() {
        }

        public void onClick(View view) {
            MediaActivity.this.dropDownContainer.toggleSubMenu();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.MediaActivity.4 */
    class C17004 implements OnTouchListener {
        C17004() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.MediaActivity.5 */
    class C17015 implements OnItemClickListener {
        C17015() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if ((MediaActivity.this.selectedMode == MediaActivity.shared_media_item || MediaActivity.this.selectedMode == MediaActivity.delete) && (view instanceof SharedDocumentCell)) {
                MediaActivity.this.onItemClick(i, view, ((SharedDocumentCell) view).getMessage(), MediaActivity.MEDIA_TYPE_ALL);
            } else if (MediaActivity.this.selectedMode == MediaActivity.forward && (view instanceof SharedLinkCell)) {
                MediaActivity.this.onItemClick(i, view, ((SharedLinkCell) view).getMessage(), MediaActivity.MEDIA_TYPE_ALL);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.MediaActivity.6 */
    class C17026 implements OnScrollListener {
        C17026() {
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            int i4 = MediaActivity.shared_media_photo_item;
            if ((!MediaActivity.this.searching || !MediaActivity.this.searchWas) && i2 != 0 && i + i2 > i3 - 2 && !MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading) {
                if (MediaActivity.this.selectedMode == 0) {
                    i4 = MediaActivity.MEDIA_TYPE_ALL;
                } else if (MediaActivity.this.selectedMode == MediaActivity.shared_media_item) {
                    i4 = MediaActivity.shared_media_item;
                } else if (MediaActivity.this.selectedMode != MediaActivity.shared_media_photo_item) {
                    i4 = MediaActivity.this.selectedMode == MediaActivity.delete ? MediaActivity.delete : MediaActivity.this.selectedMode == MediaActivity.shared_media_video_item ? MediaActivity.shared_media_video_item : MediaActivity.forward;
                }
                if (!MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].endReached[MediaActivity.MEDIA_TYPE_ALL]) {
                    MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading = true;
                    SharedMediaQuery.loadMedia(MediaActivity.this.dialog_id, MediaActivity.MEDIA_TYPE_ALL, 50, MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].max_id[MediaActivity.MEDIA_TYPE_ALL], i4, true, MediaActivity.this.classGuid);
                } else if (MediaActivity.this.mergeDialogId != 0 && !MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].endReached[MediaActivity.shared_media_item]) {
                    MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading = true;
                    SharedMediaQuery.loadMedia(MediaActivity.this.mergeDialogId, MediaActivity.MEDIA_TYPE_ALL, 50, MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].max_id[MediaActivity.shared_media_item], i4, true, MediaActivity.this.classGuid);
                }
            }
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            boolean z = true;
            if (i == MediaActivity.shared_media_item && MediaActivity.this.searching && MediaActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(MediaActivity.this.getParentActivity().getCurrentFocus());
            }
            MediaActivity mediaActivity = MediaActivity.this;
            if (i == 0) {
                z = false;
            }
            mediaActivity.scrolling = z;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.MediaActivity.7 */
    class C17037 implements OnItemLongClickListener {
        C17037() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            if ((MediaActivity.this.selectedMode == MediaActivity.shared_media_item || MediaActivity.this.selectedMode == MediaActivity.delete) && (view instanceof SharedDocumentCell)) {
                return MediaActivity.this.onItemLongClick(((SharedDocumentCell) view).getMessage(), view, MediaActivity.MEDIA_TYPE_ALL);
            } else if (MediaActivity.this.selectedMode != MediaActivity.forward || !(view instanceof SharedLinkCell)) {
                return false;
            } else {
                return MediaActivity.this.onItemLongClick(((SharedLinkCell) view).getMessage(), view, MediaActivity.MEDIA_TYPE_ALL);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.MediaActivity.8 */
    class C17048 implements OnTouchListener {
        C17048() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.MediaActivity.9 */
    class C17059 implements OnPreDrawListener {
        C17059() {
        }

        public boolean onPreDraw() {
            MediaActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            MediaActivity.this.fixLayoutInternal();
            return true;
        }
    }

    public class MediaSearchAdapter extends BaseFragmentAdapter {
        private int currentType;
        protected ArrayList<MessageObject> globalSearch;
        private int lastReqId;
        private Context mContext;
        private int reqId;
        private ArrayList<MessageObject> searchResult;
        private Timer searchTimer;

        /* renamed from: com.hanista.mobogram.ui.MediaActivity.MediaSearchAdapter.1 */
        class C17071 implements RequestDelegate {
            final /* synthetic */ int val$currentReqId;
            final /* synthetic */ int val$max_id;

            /* renamed from: com.hanista.mobogram.ui.MediaActivity.MediaSearchAdapter.1.1 */
            class C17061 implements Runnable {
                final /* synthetic */ ArrayList val$messageObjects;

                C17061(ArrayList arrayList) {
                    this.val$messageObjects = arrayList;
                }

                public void run() {
                    if (C17071.this.val$currentReqId == MediaSearchAdapter.this.lastReqId) {
                        MediaSearchAdapter.this.globalSearch = this.val$messageObjects;
                        MediaSearchAdapter.this.notifyDataSetChanged();
                    }
                    MediaSearchAdapter.this.reqId = MediaActivity.MEDIA_TYPE_ALL;
                }
            }

            C17071(int i, int i2) {
                this.val$max_id = i;
                this.val$currentReqId = i2;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                ArrayList arrayList = new ArrayList();
                if (tL_error == null) {
                    messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = (messages_Messages) tLObject;
                    for (int i = MediaActivity.MEDIA_TYPE_ALL; i < com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.size(); i += MediaActivity.shared_media_item) {
                        Message message = (Message) com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.get(i);
                        if (this.val$max_id == 0 || message.id <= this.val$max_id) {
                            arrayList.add(new MessageObject(message, null, false));
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new C17061(arrayList));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.MediaActivity.MediaSearchAdapter.2 */
        class C17082 extends TimerTask {
            final /* synthetic */ String val$query;

            C17082(String str) {
                this.val$query = str;
            }

            public void run() {
                try {
                    MediaSearchAdapter.this.searchTimer.cancel();
                    MediaSearchAdapter.this.searchTimer = null;
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                MediaSearchAdapter.this.processSearch(this.val$query);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.MediaActivity.MediaSearchAdapter.3 */
        class C17103 implements Runnable {
            final /* synthetic */ String val$query;

            /* renamed from: com.hanista.mobogram.ui.MediaActivity.MediaSearchAdapter.3.1 */
            class C17091 implements Runnable {
                final /* synthetic */ ArrayList val$copy;

                C17091(ArrayList arrayList) {
                    this.val$copy = arrayList;
                }

                public void run() {
                    String toLowerCase = C17103.this.val$query.trim().toLowerCase();
                    if (toLowerCase.length() == 0) {
                        MediaSearchAdapter.this.updateSearchResults(new ArrayList());
                        return;
                    }
                    String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
                    String str = (toLowerCase.equals(translitString) || translitString.length() == 0) ? null : translitString;
                    String[] strArr = new String[((str != null ? MediaActivity.shared_media_item : MediaActivity.MEDIA_TYPE_ALL) + MediaActivity.shared_media_item)];
                    strArr[MediaActivity.MEDIA_TYPE_ALL] = toLowerCase;
                    if (str != null) {
                        strArr[MediaActivity.shared_media_item] = str;
                    }
                    ArrayList arrayList = new ArrayList();
                    for (int i = MediaActivity.MEDIA_TYPE_ALL; i < this.val$copy.size(); i += MediaActivity.shared_media_item) {
                        MessageObject messageObject = (MessageObject) this.val$copy.get(i);
                        for (int i2 = MediaActivity.MEDIA_TYPE_ALL; i2 < strArr.length; i2 += MediaActivity.shared_media_item) {
                            CharSequence charSequence = strArr[i2];
                            String documentName = messageObject.getDocumentName();
                            if (!(documentName == null || documentName.length() == 0)) {
                                if (!documentName.toLowerCase().contains(charSequence)) {
                                    if (MediaSearchAdapter.this.currentType == MediaActivity.delete) {
                                        boolean contains;
                                        Document document = messageObject.type == 0 ? messageObject.messageOwner.media.webpage.document : messageObject.messageOwner.media.document;
                                        int i3 = MediaActivity.MEDIA_TYPE_ALL;
                                        while (i3 < document.attributes.size()) {
                                            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i3);
                                            if (documentAttribute instanceof TL_documentAttributeAudio) {
                                                boolean contains2 = documentAttribute.performer != null ? documentAttribute.performer.toLowerCase().contains(charSequence) : false;
                                                contains = (contains2 || documentAttribute.title == null) ? contains2 : documentAttribute.title.toLowerCase().contains(charSequence);
                                                if (contains) {
                                                    arrayList.add(messageObject);
                                                    break;
                                                }
                                            } else {
                                                i3 += MediaActivity.shared_media_item;
                                            }
                                        }
                                        contains = false;
                                        if (contains) {
                                            arrayList.add(messageObject);
                                            break;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    arrayList.add(messageObject);
                                    break;
                                }
                            }
                        }
                    }
                    MediaSearchAdapter.this.updateSearchResults(arrayList);
                }
            }

            C17103(String str) {
                this.val$query = str;
            }

            public void run() {
                if (!MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.isEmpty()) {
                    if (MediaSearchAdapter.this.currentType == MediaActivity.shared_media_item || MediaSearchAdapter.this.currentType == MediaActivity.delete) {
                        MessageObject messageObject = (MessageObject) MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.get(MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.size() - 1);
                        MediaSearchAdapter.this.queryServerSearch(this.val$query, messageObject.getId(), messageObject.getDialogId());
                    } else if (MediaSearchAdapter.this.currentType == MediaActivity.forward) {
                        MediaSearchAdapter.this.queryServerSearch(this.val$query, MediaActivity.MEDIA_TYPE_ALL, MediaActivity.this.dialog_id);
                    }
                }
                if (MediaSearchAdapter.this.currentType == MediaActivity.shared_media_item || MediaSearchAdapter.this.currentType == MediaActivity.delete) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.addAll(MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages);
                    Utilities.searchQueue.postRunnable(new C17091(arrayList));
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.MediaActivity.MediaSearchAdapter.4 */
        class C17114 implements Runnable {
            final /* synthetic */ ArrayList val$documents;

            C17114(ArrayList arrayList) {
                this.val$documents = arrayList;
            }

            public void run() {
                MediaSearchAdapter.this.searchResult = this.val$documents;
                MediaSearchAdapter.this.notifyDataSetChanged();
            }
        }

        /* renamed from: com.hanista.mobogram.ui.MediaActivity.MediaSearchAdapter.5 */
        class C17125 implements SharedLinkCellDelegate {
            C17125() {
            }

            public boolean canPerformActions() {
                return !MediaActivity.this.actionBar.isActionModeShowed();
            }

            public void needOpenWebView(WebPage webPage) {
                MediaActivity.this.openWebView(webPage);
            }
        }

        public MediaSearchAdapter(Context context, int i) {
            this.searchResult = new ArrayList();
            this.globalSearch = new ArrayList();
            this.reqId = MediaActivity.MEDIA_TYPE_ALL;
            this.mContext = context;
            this.currentType = i;
        }

        private void processSearch(String str) {
            AndroidUtilities.runOnUIThread(new C17103(str));
        }

        private void updateSearchResults(ArrayList<MessageObject> arrayList) {
            AndroidUtilities.runOnUIThread(new C17114(arrayList));
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            int size = this.searchResult.size();
            int size2 = this.globalSearch.size();
            return size2 != 0 ? size + size2 : size;
        }

        public MessageObject getItem(int i) {
            return i < this.searchResult.size() ? (MessageObject) this.searchResult.get(i) : (MessageObject) this.globalSearch.get(i - this.searchResult.size());
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return MediaActivity.MEDIA_TYPE_ALL;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = true;
            View sharedDocumentCell;
            MessageObject item;
            boolean containsKey;
            if (this.currentType == MediaActivity.shared_media_item || this.currentType == MediaActivity.delete) {
                sharedDocumentCell = view == null ? new SharedDocumentCell(this.mContext) : view;
                SharedDocumentCell sharedDocumentCell2 = (SharedDocumentCell) sharedDocumentCell;
                item = getItem(i);
                sharedDocumentCell2.setDocument(item, i != getCount() + -1);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    containsKey = MediaActivity.this.selectedFiles[item.getDialogId() == MediaActivity.this.dialog_id ? MediaActivity.MEDIA_TYPE_ALL : MediaActivity.shared_media_item].containsKey(Integer.valueOf(item.getId()));
                    if (MediaActivity.this.scrolling) {
                        z = false;
                    }
                    sharedDocumentCell2.setChecked(containsKey, z);
                    return sharedDocumentCell;
                }
                if (MediaActivity.this.scrolling) {
                    z = false;
                }
                sharedDocumentCell2.setChecked(false, z);
                return sharedDocumentCell;
            } else if (this.currentType != MediaActivity.forward) {
                return view;
            } else {
                if (view == null) {
                    sharedDocumentCell = new SharedLinkCell(this.mContext);
                    ((SharedLinkCell) sharedDocumentCell).setDelegate(new C17125());
                } else {
                    sharedDocumentCell = view;
                }
                SharedLinkCell sharedLinkCell = (SharedLinkCell) sharedDocumentCell;
                item = getItem(i);
                sharedLinkCell.setLink(item, i != getCount() + -1);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    containsKey = MediaActivity.this.selectedFiles[item.getDialogId() == MediaActivity.this.dialog_id ? MediaActivity.MEDIA_TYPE_ALL : MediaActivity.shared_media_item].containsKey(Integer.valueOf(item.getId()));
                    if (MediaActivity.this.scrolling) {
                        z = false;
                    }
                    sharedLinkCell.setChecked(containsKey, z);
                    return sharedDocumentCell;
                }
                if (MediaActivity.this.scrolling) {
                    z = false;
                }
                sharedLinkCell.setChecked(false, z);
                return sharedDocumentCell;
            }
        }

        public int getViewTypeCount() {
            return MediaActivity.shared_media_item;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isEmpty() {
            return this.searchResult.isEmpty() && this.globalSearch.isEmpty();
        }

        public boolean isEnabled(int i) {
            return i != this.searchResult.size() + this.globalSearch.size();
        }

        public boolean isGlobalSearch(int i) {
            int size = this.searchResult.size();
            return (i < 0 || i >= size) && i > size && i <= size + this.globalSearch.size();
        }

        public void queryServerSearch(String str, int i, long j) {
            int i2 = (int) j;
            if (i2 != 0) {
                if (this.reqId != 0) {
                    ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
                    this.reqId = MediaActivity.MEDIA_TYPE_ALL;
                }
                if (str == null || str.length() == 0) {
                    this.globalSearch.clear();
                    this.lastReqId = MediaActivity.MEDIA_TYPE_ALL;
                    notifyDataSetChanged();
                    return;
                }
                TLObject tL_messages_search = new TL_messages_search();
                tL_messages_search.offset = MediaActivity.MEDIA_TYPE_ALL;
                tL_messages_search.limit = 50;
                tL_messages_search.max_id = i;
                if (this.currentType == MediaActivity.shared_media_item) {
                    tL_messages_search.filter = new TL_inputMessagesFilterDocument();
                } else if (this.currentType == MediaActivity.forward) {
                    tL_messages_search.filter = new TL_inputMessagesFilterUrl();
                } else if (this.currentType == MediaActivity.delete) {
                    tL_messages_search.filter = new TL_inputMessagesFilterMusic();
                }
                tL_messages_search.f2672q = str;
                tL_messages_search.peer = MessagesController.getInputPeer(i2);
                if (tL_messages_search.peer != null) {
                    i2 = this.lastReqId + MediaActivity.shared_media_item;
                    this.lastReqId = i2;
                    this.reqId = ConnectionsManager.getInstance().sendRequest(tL_messages_search, new C17071(i, i2), MediaActivity.shared_media_photo_item);
                    ConnectionsManager.getInstance().bindRequestToGuid(this.reqId, MediaActivity.this.classGuid);
                }
            }
        }

        public void search(String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (str == null) {
                this.searchResult.clear();
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new C17082(str), 200, 300);
        }
    }

    private class SharedDocumentsAdapter extends BaseSectionsAdapter {
        private int currentType;
        private Context mContext;

        public SharedDocumentsAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
        }

        public int getCountForSection(int i) {
            return i < MediaActivity.this.sharedMediaData[this.currentType].sections.size() ? ((ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get(MediaActivity.this.sharedMediaData[this.currentType].sections.get(i))).size() + MediaActivity.shared_media_item : MediaActivity.shared_media_item;
        }

        public Object getItem(int i, int i2) {
            return null;
        }

        public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
            if (i >= MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                return view == null ? new LoadingCell(this.mContext) : view;
            } else {
                ArrayList arrayList = (ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(i));
                View greySectionCell;
                if (i2 == 0) {
                    greySectionCell = view == null ? new GreySectionCell(this.mContext) : view;
                    ((GreySectionCell) greySectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) arrayList.get(MediaActivity.MEDIA_TYPE_ALL)).messageOwner.date).toUpperCase());
                    return greySectionCell;
                }
                greySectionCell = view == null ? new SharedDocumentCell(this.mContext) : view;
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) greySectionCell;
                MessageObject messageObject = (MessageObject) arrayList.get(i2 - 1);
                boolean z = i2 != arrayList.size() || (i == MediaActivity.this.sharedMediaData[this.currentType].sections.size() - 1 && MediaActivity.this.sharedMediaData[this.currentType].loading);
                sharedDocumentCell.setDocument(messageObject, z);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    sharedDocumentCell.setChecked(MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? MediaActivity.MEDIA_TYPE_ALL : MediaActivity.shared_media_item].containsKey(Integer.valueOf(messageObject.getId())), !MediaActivity.this.scrolling);
                    return greySectionCell;
                }
                sharedDocumentCell.setChecked(false, !MediaActivity.this.scrolling);
                return greySectionCell;
            }
        }

        public int getItemViewType(int i, int i2) {
            return i < MediaActivity.this.sharedMediaData[this.currentType].sections.size() ? i2 == 0 ? MediaActivity.MEDIA_TYPE_ALL : MediaActivity.shared_media_item : MediaActivity.shared_media_photo_item;
        }

        public int getSectionCount() {
            int i = MediaActivity.shared_media_item;
            int size = MediaActivity.this.sharedMediaData[this.currentType].sections.size();
            if (MediaActivity.this.sharedMediaData[this.currentType].sections.isEmpty() || (MediaActivity.this.sharedMediaData[this.currentType].endReached[MediaActivity.MEDIA_TYPE_ALL] && MediaActivity.this.sharedMediaData[this.currentType].endReached[MediaActivity.shared_media_item])) {
                i = MediaActivity.MEDIA_TYPE_ALL;
            }
            return i + size;
        }

        public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
            View greySectionCell = view == null ? new GreySectionCell(this.mContext) : view;
            if (i < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                ((GreySectionCell) greySectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(i))).get(MediaActivity.MEDIA_TYPE_ALL)).messageOwner.date).toUpperCase());
            }
            return greySectionCell;
        }

        public int getViewTypeCount() {
            return MediaActivity.forward;
        }

        public boolean isRowEnabled(int i, int i2) {
            return i2 != 0;
        }
    }

    private class SharedGifAdapter extends BaseSectionsAdapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.MediaActivity.SharedGifAdapter.1 */
        class C17131 implements SharedGifCell {
            C17131() {
            }

            public void didClickItem(com.hanista.mobogram.mobo.p009j.SharedGifCell sharedGifCell, int i, MessageObject messageObject, int i2) {
                MediaActivity.this.onItemClick(i, sharedGifCell, messageObject, i2);
            }

            public boolean didLongClickItem(com.hanista.mobogram.mobo.p009j.SharedGifCell sharedGifCell, int i, MessageObject messageObject, int i2) {
                return MediaActivity.this.onItemLongClick(messageObject, sharedGifCell, i2);
            }
        }

        public SharedGifAdapter(Context context) {
            this.mContext = context;
        }

        public int getCountForSection(int i) {
            return i < MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sections.size() ? ((int) Math.ceil((double) (((float) ((ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sectionArrays.get(MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sections.get(i))).size()) / ((float) MediaActivity.this.columnsCount)))) + MediaActivity.shared_media_item : MediaActivity.shared_media_item;
        }

        public Object getItem(int i, int i2) {
            return null;
        }

        public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
            if (i >= MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sections.size()) {
                return view == null ? new LoadingCell(this.mContext) : view;
            } else {
                ArrayList arrayList = (ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sectionArrays.get((String) MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sections.get(i));
                View sharedMediaSectionCell;
                if (i2 == 0) {
                    sharedMediaSectionCell = view == null ? new SharedMediaSectionCell(this.mContext) : view;
                    ((SharedMediaSectionCell) sharedMediaSectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) arrayList.get(MediaActivity.MEDIA_TYPE_ALL)).messageOwner.date).toUpperCase());
                    return sharedMediaSectionCell;
                }
                com.hanista.mobogram.mobo.p009j.SharedGifCell sharedGifCell;
                if (view == null) {
                    sharedMediaSectionCell = new com.hanista.mobogram.mobo.p009j.SharedGifCell(this.mContext);
                    com.hanista.mobogram.mobo.p009j.SharedGifCell sharedGifCell2 = (com.hanista.mobogram.mobo.p009j.SharedGifCell) sharedMediaSectionCell;
                    sharedGifCell2.setDelegate(new C17131());
                    view = sharedMediaSectionCell;
                    sharedGifCell = sharedGifCell2;
                } else {
                    sharedGifCell = (com.hanista.mobogram.mobo.p009j.SharedGifCell) view;
                }
                int access$4600 = MediaActivity.this.columnsCount / MediaActivity.shared_media_photo_item;
                sharedGifCell.setItemsCount(access$4600);
                for (int i3 = MediaActivity.MEDIA_TYPE_ALL; i3 < access$4600; i3 += MediaActivity.shared_media_item) {
                    int i4 = ((i2 - 1) * access$4600) + i3;
                    if (i4 < arrayList.size()) {
                        MessageObject messageObject = (MessageObject) arrayList.get(i4);
                        sharedGifCell.setIsFirst(i2 == MediaActivity.shared_media_item);
                        sharedGifCell.m1236a(i3, MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].messages.indexOf(messageObject), messageObject);
                        if (MediaActivity.this.actionBar.isActionModeShowed()) {
                            sharedGifCell.m1237a(i3, MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? MediaActivity.MEDIA_TYPE_ALL : MediaActivity.shared_media_item].containsKey(Integer.valueOf(messageObject.getId())), !MediaActivity.this.scrolling);
                        } else {
                            sharedGifCell.m1237a(i3, false, !MediaActivity.this.scrolling);
                        }
                    } else {
                        sharedGifCell.m1236a(i3, i4, null);
                    }
                }
                sharedGifCell.requestLayout();
                return view;
            }
        }

        public int getItemViewType(int i, int i2) {
            return i < MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sections.size() ? i2 == 0 ? MediaActivity.MEDIA_TYPE_ALL : MediaActivity.shared_media_item : MediaActivity.shared_media_photo_item;
        }

        public int getSectionCount() {
            int i = MediaActivity.shared_media_item;
            int size = MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sections.size();
            if (MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sections.isEmpty() || (MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].endReached[MediaActivity.MEDIA_TYPE_ALL] && MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].endReached[MediaActivity.shared_media_item])) {
                i = MediaActivity.MEDIA_TYPE_ALL;
            }
            return i + size;
        }

        public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
            View sharedMediaSectionCell;
            if (view == null) {
                sharedMediaSectionCell = new SharedMediaSectionCell(this.mContext);
                sharedMediaSectionCell.setBackgroundColor(-1);
            } else {
                sharedMediaSectionCell = view;
            }
            if (i < MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sections.size()) {
                ((SharedMediaSectionCell) sharedMediaSectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sectionArrays.get((String) MediaActivity.this.sharedMediaData[MediaActivity.shared_media_video_item].sections.get(i))).get(MediaActivity.MEDIA_TYPE_ALL)).messageOwner.date).toUpperCase());
            }
            return sharedMediaSectionCell;
        }

        public int getViewTypeCount() {
            return MediaActivity.forward;
        }

        public boolean isRowEnabled(int i, int i2) {
            return false;
        }
    }

    private class SharedLinksAdapter extends BaseSectionsAdapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.MediaActivity.SharedLinksAdapter.1 */
        class C17141 implements SharedLinkCellDelegate {
            C17141() {
            }

            public boolean canPerformActions() {
                return !MediaActivity.this.actionBar.isActionModeShowed();
            }

            public void needOpenWebView(WebPage webPage) {
                MediaActivity.this.openWebView(webPage);
            }
        }

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        public int getCountForSection(int i) {
            return i < MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size() ? ((ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.forward].sectionArrays.get(MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.get(i))).size() + MediaActivity.shared_media_item : MediaActivity.shared_media_item;
        }

        public Object getItem(int i, int i2) {
            return null;
        }

        public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
            if (i >= MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size()) {
                return view == null ? new LoadingCell(this.mContext) : view;
            } else {
                ArrayList arrayList = (ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.forward].sectionArrays.get((String) MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.get(i));
                View greySectionCell;
                if (i2 == 0) {
                    greySectionCell = view == null ? new GreySectionCell(this.mContext) : view;
                    ((GreySectionCell) greySectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) arrayList.get(MediaActivity.MEDIA_TYPE_ALL)).messageOwner.date).toUpperCase());
                    return greySectionCell;
                }
                if (view == null) {
                    greySectionCell = new SharedLinkCell(this.mContext);
                    ((SharedLinkCell) greySectionCell).setDelegate(new C17141());
                } else {
                    greySectionCell = view;
                }
                SharedLinkCell sharedLinkCell = (SharedLinkCell) greySectionCell;
                MessageObject messageObject = (MessageObject) arrayList.get(i2 - 1);
                boolean z = i2 != arrayList.size() || (i == MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size() - 1 && MediaActivity.this.sharedMediaData[MediaActivity.forward].loading);
                sharedLinkCell.setLink(messageObject, z);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    sharedLinkCell.setChecked(MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? MediaActivity.MEDIA_TYPE_ALL : MediaActivity.shared_media_item].containsKey(Integer.valueOf(messageObject.getId())), !MediaActivity.this.scrolling);
                    return greySectionCell;
                }
                sharedLinkCell.setChecked(false, !MediaActivity.this.scrolling);
                return greySectionCell;
            }
        }

        public int getItemViewType(int i, int i2) {
            return i < MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size() ? i2 == 0 ? MediaActivity.MEDIA_TYPE_ALL : MediaActivity.shared_media_item : MediaActivity.shared_media_photo_item;
        }

        public int getSectionCount() {
            int i = MediaActivity.shared_media_item;
            int size = MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size();
            if (MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.isEmpty() || (MediaActivity.this.sharedMediaData[MediaActivity.forward].endReached[MediaActivity.MEDIA_TYPE_ALL] && MediaActivity.this.sharedMediaData[MediaActivity.forward].endReached[MediaActivity.shared_media_item])) {
                i = MediaActivity.MEDIA_TYPE_ALL;
            }
            return i + size;
        }

        public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
            View greySectionCell = view == null ? new GreySectionCell(this.mContext) : view;
            if (i < MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.size()) {
                ((GreySectionCell) greySectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.forward].sectionArrays.get((String) MediaActivity.this.sharedMediaData[MediaActivity.forward].sections.get(i))).get(MediaActivity.MEDIA_TYPE_ALL)).messageOwner.date).toUpperCase());
            }
            return greySectionCell;
        }

        public int getViewTypeCount() {
            return MediaActivity.forward;
        }

        public boolean isRowEnabled(int i, int i2) {
            return i2 != 0;
        }
    }

    private class SharedMediaData {
        private boolean[] endReached;
        private boolean loading;
        private int[] max_id;
        private ArrayList<MessageObject> messages;
        private HashMap<Integer, MessageObject>[] messagesDict;
        private HashMap<String, ArrayList<MessageObject>> sectionArrays;
        private ArrayList<String> sections;
        private int totalCount;

        private SharedMediaData() {
            this.messages = new ArrayList();
            HashMap[] hashMapArr = new HashMap[MediaActivity.shared_media_photo_item];
            hashMapArr[MediaActivity.MEDIA_TYPE_ALL] = new HashMap();
            hashMapArr[MediaActivity.shared_media_item] = new HashMap();
            this.messagesDict = hashMapArr;
            this.sections = new ArrayList();
            this.sectionArrays = new HashMap();
            this.endReached = new boolean[]{false, true};
            this.max_id = new int[]{MediaActivity.MEDIA_TYPE_ALL, MediaActivity.MEDIA_TYPE_ALL};
        }

        public boolean addMessage(MessageObject messageObject, boolean z, boolean z2) {
            int i = messageObject.getDialogId() == MediaActivity.this.dialog_id ? MediaActivity.MEDIA_TYPE_ALL : MediaActivity.shared_media_item;
            if (this.messagesDict[i].containsKey(Integer.valueOf(messageObject.getId()))) {
                return false;
            }
            if (z2) {
                this.max_id[i] = Math.max(messageObject.getId(), this.max_id[i]);
            } else if (messageObject.getId() > 0) {
                this.max_id[i] = Math.min(messageObject.getId(), this.max_id[i]);
            }
            if (MediaActivity.this.selectedMode == 0 && MediaActivity.this.mediaType == MediaActivity.shared_media_photo_item && !messageObject.isVideo()) {
                return false;
            }
            if (MediaActivity.this.selectedMode == 0 && MediaActivity.this.mediaType == MediaActivity.shared_media_item && messageObject.isVideo()) {
                return false;
            }
            if (MediaActivity.this.selectedMode != MediaActivity.forward) {
                if (MediaActivity.this.downloadType == MediaActivity.shared_media_item) {
                    if (!FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                        return false;
                    }
                } else if (MediaActivity.this.downloadType == MediaActivity.shared_media_photo_item && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                    return false;
                }
            }
            if (MediaActivity.this.archive != null) {
                if (MediaActivity.this.archive.m204a().longValue() == -1 && MediaActivity.this.archive.m211e().contains(Integer.valueOf(messageObject.getId()))) {
                    return false;
                }
                if (MediaActivity.this.archive.m204a().longValue() > 0 && !MediaActivity.this.archive.m211e().contains(Integer.valueOf(messageObject.getId()))) {
                    return false;
                }
            }
            String formatYearMonth = LocaleController.formatYearMonth((long) messageObject.messageOwner.date);
            ArrayList arrayList = (ArrayList) this.sectionArrays.get(formatYearMonth);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.sectionArrays.put(formatYearMonth, arrayList);
                if (z) {
                    this.sections.add(MediaActivity.MEDIA_TYPE_ALL, formatYearMonth);
                } else {
                    this.sections.add(formatYearMonth);
                }
            }
            if (z) {
                arrayList.add(MediaActivity.MEDIA_TYPE_ALL, messageObject);
                this.messages.add(MediaActivity.MEDIA_TYPE_ALL, messageObject);
            } else {
                arrayList.add(messageObject);
                this.messages.add(messageObject);
            }
            this.messagesDict[i].put(Integer.valueOf(messageObject.getId()), messageObject);
            return true;
        }

        public boolean deleteMessage(int i, int i2) {
            MessageObject messageObject = (MessageObject) this.messagesDict[i2].get(Integer.valueOf(i));
            if (messageObject == null) {
                return false;
            }
            String formatYearMonth = LocaleController.formatYearMonth((long) messageObject.messageOwner.date);
            ArrayList arrayList = (ArrayList) this.sectionArrays.get(formatYearMonth);
            if (arrayList == null) {
                return false;
            }
            arrayList.remove(messageObject);
            this.messages.remove(messageObject);
            this.messagesDict[i2].remove(Integer.valueOf(messageObject.getId()));
            if (arrayList.isEmpty()) {
                this.sectionArrays.remove(formatYearMonth);
                this.sections.remove(formatYearMonth);
            }
            this.totalCount--;
            return true;
        }

        public void replaceMid(int i, int i2) {
            MessageObject messageObject = (MessageObject) this.messagesDict[MediaActivity.MEDIA_TYPE_ALL].get(Integer.valueOf(i));
            if (messageObject != null) {
                this.messagesDict[MediaActivity.MEDIA_TYPE_ALL].remove(Integer.valueOf(i));
                this.messagesDict[MediaActivity.MEDIA_TYPE_ALL].put(Integer.valueOf(i2), messageObject);
                messageObject.messageOwner.id = i2;
            }
        }
    }

    private class SharedPhotoVideoAdapter extends BaseSectionsAdapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.MediaActivity.SharedPhotoVideoAdapter.1 */
        class C17151 implements SharedPhotoVideoCellDelegate {
            C17151() {
            }

            public void didClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2) {
                MediaActivity.this.onItemClick(i, sharedPhotoVideoCell, messageObject, i2);
            }

            public boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2) {
                return MediaActivity.this.onItemLongClick(messageObject, sharedPhotoVideoCell, i2);
            }
        }

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        public int getCountForSection(int i) {
            return i < MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sections.size() ? ((int) Math.ceil((double) (((float) ((ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sectionArrays.get(MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sections.get(i))).size()) / ((float) MediaActivity.this.columnsCount)))) + MediaActivity.shared_media_item : MediaActivity.shared_media_item;
        }

        public Object getItem(int i, int i2) {
            return null;
        }

        public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
            if (i >= MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sections.size()) {
                return view == null ? new LoadingCell(this.mContext) : view;
            } else {
                ArrayList arrayList = (ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sectionArrays.get((String) MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sections.get(i));
                View sharedMediaSectionCell;
                if (i2 == 0) {
                    sharedMediaSectionCell = view == null ? new SharedMediaSectionCell(this.mContext) : view;
                    ((SharedMediaSectionCell) sharedMediaSectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) arrayList.get(MediaActivity.MEDIA_TYPE_ALL)).messageOwner.date).toUpperCase());
                    return sharedMediaSectionCell;
                }
                SharedPhotoVideoCell sharedPhotoVideoCell;
                if (view == null) {
                    if (MediaActivity.this.cellCache.isEmpty()) {
                        sharedMediaSectionCell = new SharedPhotoVideoCell(this.mContext);
                    } else {
                        View view2 = (View) MediaActivity.this.cellCache.get(MediaActivity.MEDIA_TYPE_ALL);
                        MediaActivity.this.cellCache.remove(MediaActivity.MEDIA_TYPE_ALL);
                        sharedMediaSectionCell = view2;
                    }
                    SharedPhotoVideoCell sharedPhotoVideoCell2 = (SharedPhotoVideoCell) sharedMediaSectionCell;
                    sharedPhotoVideoCell2.setDelegate(new C17151());
                    view = sharedMediaSectionCell;
                    sharedPhotoVideoCell = sharedPhotoVideoCell2;
                } else {
                    sharedPhotoVideoCell = (SharedPhotoVideoCell) view;
                }
                sharedPhotoVideoCell.setItemsCount(MediaActivity.this.columnsCount);
                for (int i3 = MediaActivity.MEDIA_TYPE_ALL; i3 < MediaActivity.this.columnsCount; i3 += MediaActivity.shared_media_item) {
                    int access$4600 = ((i2 - 1) * MediaActivity.this.columnsCount) + i3;
                    if (access$4600 < arrayList.size()) {
                        MessageObject messageObject = (MessageObject) arrayList.get(access$4600);
                        sharedPhotoVideoCell.setIsFirst(i2 == MediaActivity.shared_media_item);
                        sharedPhotoVideoCell.setItem(i3, MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].messages.indexOf(messageObject), messageObject);
                        if (MediaActivity.this.actionBar.isActionModeShowed()) {
                            sharedPhotoVideoCell.setChecked(i3, MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? MediaActivity.MEDIA_TYPE_ALL : MediaActivity.shared_media_item].containsKey(Integer.valueOf(messageObject.getId())), !MediaActivity.this.scrolling);
                        } else {
                            sharedPhotoVideoCell.setChecked(i3, false, !MediaActivity.this.scrolling);
                        }
                    } else {
                        sharedPhotoVideoCell.setItem(i3, access$4600, null);
                    }
                }
                sharedPhotoVideoCell.requestLayout();
                return view;
            }
        }

        public int getItemViewType(int i, int i2) {
            return i < MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sections.size() ? i2 == 0 ? MediaActivity.MEDIA_TYPE_ALL : MediaActivity.shared_media_item : MediaActivity.shared_media_photo_item;
        }

        public int getSectionCount() {
            int i = MediaActivity.shared_media_item;
            int size = MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sections.size();
            if (MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sections.isEmpty() || (MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].endReached[MediaActivity.MEDIA_TYPE_ALL] && MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].endReached[MediaActivity.shared_media_item])) {
                i = MediaActivity.MEDIA_TYPE_ALL;
            }
            return i + size;
        }

        public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
            View sharedMediaSectionCell;
            if (view == null) {
                sharedMediaSectionCell = new SharedMediaSectionCell(this.mContext);
                sharedMediaSectionCell.setBackgroundColor(-1);
            } else {
                sharedMediaSectionCell = view;
            }
            if (i < MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sections.size()) {
                ((SharedMediaSectionCell) sharedMediaSectionCell).setText(LocaleController.formatYearMonth((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sectionArrays.get((String) MediaActivity.this.sharedMediaData[MediaActivity.MEDIA_TYPE_ALL].sections.get(i))).get(MediaActivity.MEDIA_TYPE_ALL)).messageOwner.date).toUpperCase());
            }
            return sharedMediaSectionCell;
        }

        public int getViewTypeCount() {
            return MediaActivity.forward;
        }

        public boolean isRowEnabled(int i, int i2) {
            return false;
        }
    }

    public MediaActivity(Bundle bundle) {
        super(bundle);
        this.cellCache = new ArrayList(files_item);
        HashMap[] hashMapArr = new HashMap[shared_media_photo_item];
        hashMapArr[MEDIA_TYPE_ALL] = new HashMap();
        hashMapArr[shared_media_item] = new HashMap();
        this.selectedFiles = hashMapArr;
        this.actionModeViews = new ArrayList();
        this.info = null;
        this.columnsCount = delete;
        this.sharedMediaData = new SharedMediaData[files_item];
    }

    private void addMessagesToDownloadList() {
        ArrayList arrayList = new ArrayList();
        for (int i = shared_media_item; i >= 0; i--) {
            Object arrayList2 = new ArrayList(this.selectedFiles[i].keySet());
            Collections.sort(arrayList2);
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                Integer num = (Integer) it.next();
                if (num.intValue() > 0) {
                    arrayList.add(this.selectedFiles[i].get(num));
                }
            }
            this.selectedFiles[i].clear();
        }
        messages_Messages com_hanista_mobogram_tgnet_TLRPC_messages_Messages = new messages_Messages();
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            com_hanista_mobogram_tgnet_TLRPC_messages_Messages.messages.add(((MessageObject) it2.next()).messageOwner);
        }
        DownloadMessagesStorage.m783a().m811a(com_hanista_mobogram_tgnet_TLRPC_messages_Messages, 1, -1, MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, false);
        Toast.makeText(getParentActivity(), LocaleController.getString("FilesAddedToDownloadList", C0338R.string.FilesAddedToDownloadList), MEDIA_TYPE_ALL).show();
        this.cantDeleteMessagesCount = MEDIA_TYPE_ALL;
        this.actionBar.hideActionMode();
        notifyDataSetChanged();
    }

    private void doSelectAllMessages() {
        for (int i = shared_media_item; i >= 0; i--) {
            ArrayList arrayList = new ArrayList(this.selectedFiles[i].keySet());
            Collections.sort(arrayList);
            if (arrayList.size() > shared_media_item) {
                int i2;
                MessageObject messageObject;
                List arrayList2 = new ArrayList();
                for (i2 = MEDIA_TYPE_ALL; i2 < this.sharedMediaData[this.selectedMode].messages.size(); i2 += shared_media_item) {
                    messageObject = (MessageObject) this.sharedMediaData[this.selectedMode].messages.get(i2);
                    if (messageObject.getId() == ((Integer) arrayList.get(MEDIA_TYPE_ALL)).intValue() || messageObject.getId() == ((Integer) arrayList.get(arrayList.size() - 1)).intValue()) {
                        arrayList2.add(Integer.valueOf(i2));
                        if (arrayList2.size() == shared_media_photo_item) {
                            break;
                        }
                    }
                }
                if (arrayList2.size() == shared_media_photo_item) {
                    for (int intValue = ((Integer) arrayList2.get(MEDIA_TYPE_ALL)).intValue() + shared_media_item; intValue < ((Integer) arrayList2.get(shared_media_item)).intValue(); intValue += shared_media_item) {
                        messageObject = (MessageObject) this.sharedMediaData[this.selectedMode].messages.get(intValue);
                        if (messageObject.getId() > 0) {
                            if (!this.selectedFiles[messageObject.getDialogId() == this.dialog_id ? MEDIA_TYPE_ALL : shared_media_item].containsKey(Integer.valueOf(messageObject.getId()))) {
                                i2 = messageObject.getDialogId() == this.dialog_id ? MEDIA_TYPE_ALL : shared_media_item;
                                if (this.selectedFiles[i2].containsKey(Integer.valueOf(messageObject.getId()))) {
                                    this.selectedFiles[i2].remove(Integer.valueOf(messageObject.getId()));
                                    if (!messageObject.canDeleteMessage(null)) {
                                        this.cantDeleteMessagesCount--;
                                    }
                                } else {
                                    this.selectedFiles[i2].put(Integer.valueOf(messageObject.getId()), messageObject);
                                    if (!messageObject.canDeleteMessage(null)) {
                                        this.cantDeleteMessagesCount += shared_media_item;
                                    }
                                }
                                if (this.selectedFiles[MEDIA_TYPE_ALL].isEmpty() && this.selectedFiles[shared_media_item].isEmpty()) {
                                    this.actionBar.hideActionMode();
                                } else {
                                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles[MEDIA_TYPE_ALL].size() + this.selectedFiles[shared_media_item].size(), true);
                                }
                                this.actionBar.createActionMode().getItem(delete).setVisibility(this.cantDeleteMessagesCount == 0 ? MEDIA_TYPE_ALL : music_item);
                            }
                        }
                    }
                    notifyDataSetChanged();
                } else {
                    return;
                }
            }
        }
        ActionBarMenuItem item = this.actionBar.createActionMode().getItem(select_all);
        if (item != null) {
            item.setVisibility(music_item);
        }
    }

    private void fixLayoutInternal() {
        int i = MEDIA_TYPE_ALL;
        if (this.listView != null) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != shared_media_photo_item) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
            if (AndroidUtilities.isTablet()) {
                this.columnsCount = delete;
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), MEDIA_TYPE_ALL, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            } else if (rotation == forward || rotation == shared_media_item) {
                this.columnsCount = files_item;
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), MEDIA_TYPE_ALL, AndroidUtilities.dp(40.0f), MEDIA_TYPE_ALL);
            } else {
                this.columnsCount = delete;
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), MEDIA_TYPE_ALL, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            }
            this.photoVideoAdapter.notifyDataSetChanged();
            this.photoAdapter.notifyDataSetChanged();
            this.gifAdapter.notifyDataSetChanged();
            this.videoAdapter.notifyDataSetChanged();
            if (this.dropDownContainer != null) {
                if (!AndroidUtilities.isTablet()) {
                    LayoutParams layoutParams = (LayoutParams) this.dropDownContainer.getLayoutParams();
                    if (VERSION.SDK_INT >= 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    layoutParams.topMargin = i;
                    this.dropDownContainer.setLayoutParams(layoutParams);
                }
                if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != shared_media_photo_item) {
                    this.dropDown.setTextSize(20.0f);
                } else {
                    this.dropDown.setTextSize(18.0f);
                }
            }
        }
    }

    private void multipleForward() {
        ArrayList arrayList = new ArrayList();
        for (int i = shared_media_item; i >= 0; i--) {
            Object arrayList2 = new ArrayList(this.selectedFiles[i].keySet());
            Collections.sort(arrayList2);
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                Integer num = (Integer) it.next();
                if (num.intValue() > 0) {
                    arrayList.add(this.selectedFiles[i].get(num));
                }
            }
            this.selectedFiles[i].clear();
        }
        this.cantDeleteMessagesCount = MEDIA_TYPE_ALL;
        this.actionBar.hideActionMode();
        showDialog(new ShareAlert(getParentActivity(), arrayList));
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        if (this.photoVideoAdapter != null) {
            this.photoVideoAdapter.notifyDataSetChanged();
        }
        if (this.photoAdapter != null) {
            this.photoAdapter.notifyDataSetChanged();
        }
        if (this.gifAdapter != null) {
            this.gifAdapter.notifyDataSetChanged();
        }
        if (this.videoAdapter != null) {
            this.videoAdapter.notifyDataSetChanged();
        }
        if (this.documentsAdapter != null) {
            this.documentsAdapter.notifyDataSetChanged();
        }
        if (this.linksAdapter != null) {
            this.linksAdapter.notifyDataSetChanged();
        }
        if (this.audioAdapter != null) {
            this.audioAdapter.notifyDataSetChanged();
        }
        updateDownloadItem();
    }

    private void onItemClick(int i, View view, MessageObject messageObject, int i2) {
        long j = 0;
        if (messageObject != null) {
            if (this.actionBar.isActionModeShowed()) {
                int i3 = messageObject.getDialogId() == this.dialog_id ? MEDIA_TYPE_ALL : shared_media_item;
                if (this.selectedFiles[i3].containsKey(Integer.valueOf(messageObject.getId()))) {
                    this.selectedFiles[i3].remove(Integer.valueOf(messageObject.getId()));
                    if (!messageObject.canDeleteMessage(null)) {
                        this.cantDeleteMessagesCount--;
                    }
                } else {
                    this.selectedFiles[i3].put(Integer.valueOf(messageObject.getId()), messageObject);
                    if (!messageObject.canDeleteMessage(null)) {
                        this.cantDeleteMessagesCount += shared_media_item;
                    }
                }
                if (this.selectedFiles[MEDIA_TYPE_ALL].isEmpty() && this.selectedFiles[shared_media_item].isEmpty()) {
                    this.actionBar.hideActionMode();
                } else {
                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles[MEDIA_TYPE_ALL].size() + this.selectedFiles[shared_media_item].size(), true);
                }
                this.actionBar.createActionMode().getItem(delete).setVisibility(this.cantDeleteMessagesCount == 0 ? MEDIA_TYPE_ALL : music_item);
                this.scrolling = false;
                if (view instanceof SharedDocumentCell) {
                    ((SharedDocumentCell) view).setChecked(this.selectedFiles[i3].containsKey(Integer.valueOf(messageObject.getId())), true);
                } else if (view instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) view).setChecked(i2, this.selectedFiles[i3].containsKey(Integer.valueOf(messageObject.getId())), true);
                } else if (view instanceof com.hanista.mobogram.mobo.p009j.SharedGifCell) {
                    ((com.hanista.mobogram.mobo.p009j.SharedGifCell) view).m1237a(i2, this.selectedFiles[i3].containsKey(Integer.valueOf(messageObject.getId())), true);
                } else if (view instanceof SharedLinkCell) {
                    ((SharedLinkCell) view).setChecked(this.selectedFiles[i3].containsKey(Integer.valueOf(messageObject.getId())), true);
                }
                updateAddToDownloadListItem();
                updateSelectAllListItem();
                updateGravity();
            } else if (this.selectedMode == 0) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[this.selectedMode].messages, i, this.dialog_id, this.mergeDialogId, this);
            } else if (this.selectedMode == shared_media_item || this.selectedMode == delete) {
                if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
                    if (sharedDocumentCell.isLoaded()) {
                        if (!messageObject.isMusic() || !MediaController.m71a().m160a(this.sharedMediaData[this.selectedMode].messages, messageObject)) {
                            r0 = messageObject.messageOwner.media != null ? FileLoader.getAttachFileName(messageObject.getDocument()) : TtmlNode.ANONYMOUS_REGION_ID;
                            File file = (messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0) ? null : new File(messageObject.messageOwner.attachPath);
                            File pathToMessage = (file == null || !(file == null || file.exists())) ? FileLoader.getPathToMessage(messageObject.messageOwner) : file;
                            if (pathToMessage != null && pathToMessage.exists()) {
                                try {
                                    String mimeTypeFromExtension;
                                    Intent intent = new Intent("android.intent.action.VIEW");
                                    intent.setFlags(shared_media_item);
                                    MimeTypeMap singleton = MimeTypeMap.getSingleton();
                                    int lastIndexOf = r0.lastIndexOf(46);
                                    if (lastIndexOf != -1) {
                                        mimeTypeFromExtension = singleton.getMimeTypeFromExtension(r0.substring(lastIndexOf + shared_media_item).toLowerCase());
                                        if (mimeTypeFromExtension == null) {
                                            mimeTypeFromExtension = messageObject.getDocument().mime_type;
                                            if (mimeTypeFromExtension == null || mimeTypeFromExtension.length() == 0) {
                                                mimeTypeFromExtension = null;
                                            }
                                        }
                                    } else {
                                        mimeTypeFromExtension = null;
                                    }
                                    if (VERSION.SDK_INT >= 24) {
                                        intent.setDataAndType(FileProvider.getUriForFile(getParentActivity(), "com.hanista.mobogram.provider", pathToMessage), mimeTypeFromExtension != null ? mimeTypeFromExtension : "text/plain");
                                    } else {
                                        intent.setDataAndType(Uri.fromFile(pathToMessage), mimeTypeFromExtension != null ? mimeTypeFromExtension : "text/plain");
                                    }
                                    if (mimeTypeFromExtension != null) {
                                        try {
                                            getParentActivity().startActivityForResult(intent, 500);
                                            return;
                                        } catch (Exception e) {
                                            if (VERSION.SDK_INT >= 24) {
                                                intent.setDataAndType(FileProvider.getUriForFile(getParentActivity(), "com.hanista.mobogram.provider", pathToMessage), "text/plain");
                                            } else {
                                                intent.setDataAndType(Uri.fromFile(pathToMessage), "text/plain");
                                            }
                                            getParentActivity().startActivityForResult(intent, 500);
                                            return;
                                        }
                                    }
                                    getParentActivity().startActivityForResult(intent, 500);
                                } catch (Exception e2) {
                                    if (getParentActivity() != null) {
                                        Builder builder = new Builder(getParentActivity());
                                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                                        Object[] objArr = new Object[shared_media_item];
                                        objArr[MEDIA_TYPE_ALL] = messageObject.getDocument().mime_type;
                                        builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", C0338R.string.NoHandleAppInstalled, objArr));
                                        showDialog(builder.create());
                                    }
                                }
                            }
                        }
                    } else if (sharedDocumentCell.isLoading()) {
                        FileLoader.getInstance().cancelLoadFile(sharedDocumentCell.getMessage().getDocument());
                        sharedDocumentCell.updateFileExistIcon();
                    } else {
                        FileLoader.getInstance().loadFile(sharedDocumentCell.getMessage().getDocument(), false, false);
                        sharedDocumentCell.updateFileExistIcon();
                    }
                }
            } else if (this.selectedMode == forward) {
                try {
                    WebPage webPage = messageObject.messageOwner.media.webpage;
                    if (webPage == null || (webPage instanceof TL_webPageEmpty)) {
                        r0 = null;
                    } else if (VERSION.SDK_INT < 16 || webPage.embed_url == null || webPage.embed_url.length() == 0) {
                        r0 = webPage.url;
                    } else {
                        openWebView(webPage);
                        return;
                    }
                    if (r0 == null) {
                        r0 = ((SharedLinkCell) view).getLink(MEDIA_TYPE_ALL);
                    }
                    if (r0 != null) {
                        Browser.openUrl(getParentActivity(), r0);
                    }
                } catch (Throwable e3) {
                    FileLog.m18e("tmessages", e3);
                }
            } else if (this.selectedMode == shared_media_video_item) {
                messageObject.checkMediaExistance();
                PhotoViewer instance;
                long j2;
                if (messageObject.mediaExists) {
                    instance = PhotoViewer.getInstance();
                    j2 = messageObject.type != 0 ? this.dialog_id : 0;
                    if (messageObject.type != 0) {
                        j = this.mergeDialogId;
                    }
                    instance.openPhoto(messageObject, j2, j, this);
                    return;
                }
                ((com.hanista.mobogram.mobo.p009j.SharedGifCell) view).m1239b(i2, i, messageObject);
                instance = PhotoViewer.getInstance();
                j2 = messageObject.type != 0 ? this.dialog_id : 0;
                if (messageObject.type != 0) {
                    j = this.mergeDialogId;
                }
                instance.openPhoto(messageObject, j2, j, this);
                PhotoViewer.getInstance().openGif(messageObject, FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80), null);
            }
        }
    }

    private boolean onItemLongClick(MessageObject messageObject, View view, int i) {
        if (this.actionBar.isActionModeShowed()) {
            return false;
        }
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedFiles[messageObject.getDialogId() == this.dialog_id ? MEDIA_TYPE_ALL : shared_media_item].put(Integer.valueOf(messageObject.getId()), messageObject);
        if (!messageObject.canDeleteMessage(null)) {
            this.cantDeleteMessagesCount += shared_media_item;
        }
        this.actionBar.createActionMode().getItem(delete).setVisibility(this.cantDeleteMessagesCount == 0 ? MEDIA_TYPE_ALL : music_item);
        this.selectedMessagesCountTextView.setNumber(shared_media_item, false);
        AnimatorSet animatorSet = new AnimatorSet();
        Collection arrayList = new ArrayList();
        for (int i2 = MEDIA_TYPE_ALL; i2 < this.actionModeViews.size(); i2 += shared_media_item) {
            View view2 = (View) this.actionModeViews.get(i2);
            AndroidUtilities.clearDrawableAnimation(view2);
            arrayList.add(ObjectAnimator.ofFloat(view2, "scaleY", new float[]{0.1f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(250);
        animatorSet.start();
        this.scrolling = false;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell) {
            ((SharedPhotoVideoCell) view).setChecked(i, true, true);
        } else if (view instanceof SharedLinkCell) {
            ((SharedLinkCell) view).setChecked(true, true);
        } else if (view instanceof com.hanista.mobogram.mobo.p009j.SharedGifCell) {
            ((com.hanista.mobogram.mobo.p009j.SharedGifCell) view).m1237a(i, true, true);
        }
        this.actionBar.showActionMode();
        updateAddToDownloadListItem();
        updateSelectAllListItem();
        updateGravity();
        return true;
    }

    private void openWebView(WebPage webPage) {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setCustomView(new WebFrameLayout(getParentActivity(), builder.create(), webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height));
        builder.setUseFullWidth(true);
        showDialog(builder.create());
    }

    private void reloadAll() {
        this.sharedMediaData = new SharedMediaData[files_item];
        for (int i = MEDIA_TYPE_ALL; i < this.sharedMediaData.length; i += shared_media_item) {
            this.sharedMediaData[i] = new SharedMediaData();
            this.sharedMediaData[i].max_id[MEDIA_TYPE_ALL] = ((int) this.dialog_id) == 0 ? TLRPC.MESSAGE_FLAG_MEGAGROUP : ConnectionsManager.DEFAULT_DATACENTER_ID;
            if (!(this.mergeDialogId == 0 || this.info == null)) {
                this.sharedMediaData[i].max_id[shared_media_item] = this.info.migrated_from_max_id;
                this.sharedMediaData[i].endReached[shared_media_item] = false;
            }
        }
        this.sharedMediaData[MEDIA_TYPE_ALL].loading = true;
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(getParentActivity());
        this.photoAdapter = new SharedPhotoVideoAdapter(getParentActivity());
        this.gifAdapter = new SharedGifAdapter(getParentActivity());
        this.videoAdapter = new SharedPhotoVideoAdapter(getParentActivity());
        this.documentsAdapter = new SharedDocumentsAdapter(getParentActivity(), shared_media_item);
        this.audioAdapter = new SharedDocumentsAdapter(getParentActivity(), delete);
        this.documentsSearchAdapter = new MediaSearchAdapter(getParentActivity(), shared_media_item);
        this.audioSearchAdapter = new MediaSearchAdapter(getParentActivity(), delete);
        this.linksSearchAdapter = new MediaSearchAdapter(getParentActivity(), forward);
        this.linksAdapter = new SharedLinksAdapter(getParentActivity());
        switchToCurrentSelectedMode();
        SharedMediaQuery.loadMedia(this.dialog_id, MEDIA_TYPE_ALL, 50, MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, true, this.classGuid);
        notifyDataSetChanged();
    }

    private void showMaterialHelp() {
        try {
            MaterialHelperUtil.m1362a(getParentActivity(), this.dropDownContainer, this.downloadItem, null);
        } catch (Exception e) {
        }
    }

    private void showSelectDownloadTypesDialog() {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        CharSequence[] charSequenceArr = new CharSequence[forward];
        charSequenceArr[MEDIA_TYPE_ALL] = LocaleController.getString("All", C0338R.string.All);
        charSequenceArr[shared_media_item] = LocaleController.getString("Downloaded", C0338R.string.Downloaded);
        charSequenceArr[shared_media_photo_item] = LocaleController.getString("NotDownloaded", C0338R.string.NotDownloaded);
        builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    MediaActivity.this.downloadType = MediaActivity.MEDIA_TYPE_ALL;
                } else if (i == MediaActivity.shared_media_item) {
                    MediaActivity.this.downloadType = MediaActivity.shared_media_item;
                } else if (i == MediaActivity.shared_media_photo_item) {
                    MediaActivity.this.downloadType = MediaActivity.shared_media_photo_item;
                }
                MediaActivity.this.reloadAll();
            }
        });
        showDialog(builder.create());
    }

    private void switchToCurrentSelectedMode() {
        if (this.searching && this.searchWas) {
            if (this.listView != null) {
                if (this.selectedMode == shared_media_item) {
                    this.listView.setAdapter(this.documentsSearchAdapter);
                    this.documentsSearchAdapter.notifyDataSetChanged();
                } else if (this.selectedMode == forward) {
                    this.listView.setAdapter(this.linksSearchAdapter);
                    this.linksSearchAdapter.notifyDataSetChanged();
                } else if (this.selectedMode == delete) {
                    this.listView.setAdapter(this.audioSearchAdapter);
                    this.audioSearchAdapter.notifyDataSetChanged();
                }
            }
            if (this.emptyTextView != null) {
                this.emptyTextView.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
                this.emptyTextView.setTextSize(shared_media_item, 20.0f);
                this.emptyImageView.setVisibility(music_item);
            }
        } else {
            this.emptyTextView.setTextSize(shared_media_item, 17.0f);
            this.emptyImageView.setVisibility(MEDIA_TYPE_ALL);
            if (this.selectedMode == 0) {
                if (this.mediaType == 0) {
                    this.listView.setAdapter(this.photoVideoAdapter);
                    this.dropDown.setText(LocaleController.getString("SharedMediaTitle", C0338R.string.SharedMediaTitle));
                } else if (this.mediaType == shared_media_item) {
                    this.listView.setAdapter(this.photoAdapter);
                    this.dropDown.setText(LocaleController.getString("SharedPhotosTitle", C0338R.string.SharedPhotosTitle));
                } else if (this.mediaType == forward) {
                    this.listView.setAdapter(this.gifAdapter);
                    this.dropDown.setText(LocaleController.getString("SharedGifsTitle", C0338R.string.SharedGifsTitle));
                } else {
                    this.listView.setAdapter(this.videoAdapter);
                    this.dropDown.setText(LocaleController.getString("SharedVideosTitle", C0338R.string.SharedVideosTitle));
                }
                this.emptyImageView.setImageResource(C0338R.drawable.tip1);
                if (((int) this.dialog_id) == 0) {
                    this.emptyTextView.setText(LocaleController.getString("NoMediaSecret", C0338R.string.NoMediaSecret));
                } else {
                    this.emptyTextView.setText(LocaleController.getString("NoMedia", C0338R.string.NoMedia));
                }
                this.searchItem.setVisibility(music_item);
                if (this.sharedMediaData[this.selectedMode].loading && this.sharedMediaData[this.selectedMode].messages.isEmpty()) {
                    this.progressView.setVisibility(MEDIA_TYPE_ALL);
                    this.listView.setEmptyView(null);
                    this.emptyView.setVisibility(music_item);
                } else {
                    this.progressView.setVisibility(music_item);
                    this.listView.setEmptyView(this.emptyView);
                }
                this.listView.setVisibility(MEDIA_TYPE_ALL);
                this.listView.setPadding(MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, AndroidUtilities.dp(4.0f));
            } else if (this.selectedMode == shared_media_item || this.selectedMode == delete) {
                if (this.selectedMode == shared_media_item) {
                    this.listView.setAdapter(this.documentsAdapter);
                    this.dropDown.setText(LocaleController.getString("DocumentsTitle", C0338R.string.DocumentsTitle));
                    this.emptyImageView.setImageResource(C0338R.drawable.tip2);
                    if (((int) this.dialog_id) == 0) {
                        this.emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", C0338R.string.NoSharedFilesSecret));
                    } else {
                        this.emptyTextView.setText(LocaleController.getString("NoSharedFiles", C0338R.string.NoSharedFiles));
                    }
                } else if (this.selectedMode == delete) {
                    this.listView.setAdapter(this.audioAdapter);
                    this.dropDown.setText(LocaleController.getString("AudioTitle", C0338R.string.AudioTitle));
                    this.emptyImageView.setImageResource(C0338R.drawable.tip4);
                    if (((int) this.dialog_id) == 0) {
                        this.emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", C0338R.string.NoSharedAudioSecret));
                    } else {
                        this.emptyTextView.setText(LocaleController.getString("NoSharedAudio", C0338R.string.NoSharedAudio));
                    }
                }
                this.searchItem.setVisibility(!this.sharedMediaData[this.selectedMode].messages.isEmpty() ? MEDIA_TYPE_ALL : music_item);
                if (!(this.sharedMediaData[this.selectedMode].loading || this.sharedMediaData[this.selectedMode].endReached[MEDIA_TYPE_ALL] || !this.sharedMediaData[this.selectedMode].messages.isEmpty())) {
                    this.sharedMediaData[this.selectedMode].loading = true;
                    SharedMediaQuery.loadMedia(this.dialog_id, MEDIA_TYPE_ALL, 50, MEDIA_TYPE_ALL, this.selectedMode == shared_media_item ? shared_media_item : delete, true, this.classGuid);
                }
                this.listView.setVisibility(MEDIA_TYPE_ALL);
                if (this.sharedMediaData[this.selectedMode].loading && this.sharedMediaData[this.selectedMode].messages.isEmpty()) {
                    this.progressView.setVisibility(MEDIA_TYPE_ALL);
                    this.listView.setEmptyView(null);
                    this.emptyView.setVisibility(music_item);
                } else {
                    this.progressView.setVisibility(music_item);
                    this.listView.setEmptyView(this.emptyView);
                }
                this.listView.setPadding(MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, AndroidUtilities.dp(4.0f));
            } else if (this.selectedMode == forward) {
                this.listView.setAdapter(this.linksAdapter);
                this.dropDown.setText(LocaleController.getString("LinksTitle", C0338R.string.LinksTitle));
                this.emptyImageView.setImageResource(C0338R.drawable.tip3);
                if (((int) this.dialog_id) == 0) {
                    this.emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", C0338R.string.NoSharedLinksSecret));
                } else {
                    this.emptyTextView.setText(LocaleController.getString("NoSharedLinks", C0338R.string.NoSharedLinks));
                }
                this.searchItem.setVisibility(!this.sharedMediaData[forward].messages.isEmpty() ? MEDIA_TYPE_ALL : music_item);
                if (!(this.sharedMediaData[this.selectedMode].loading || this.sharedMediaData[this.selectedMode].endReached[MEDIA_TYPE_ALL] || !this.sharedMediaData[this.selectedMode].messages.isEmpty())) {
                    this.sharedMediaData[this.selectedMode].loading = true;
                    SharedMediaQuery.loadMedia(this.dialog_id, MEDIA_TYPE_ALL, 50, MEDIA_TYPE_ALL, forward, true, this.classGuid);
                }
                this.listView.setVisibility(MEDIA_TYPE_ALL);
                if (this.sharedMediaData[this.selectedMode].loading && this.sharedMediaData[this.selectedMode].messages.isEmpty()) {
                    this.progressView.setVisibility(MEDIA_TYPE_ALL);
                    this.listView.setEmptyView(null);
                    this.emptyView.setVisibility(music_item);
                } else {
                    this.progressView.setVisibility(music_item);
                    this.listView.setEmptyView(this.emptyView);
                }
                this.listView.setPadding(MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, AndroidUtilities.dp(4.0f));
            } else if (this.selectedMode == shared_media_video_item) {
                this.listView.setAdapter(this.gifAdapter);
                this.dropDown.setText(LocaleController.getString("SharedGifsTitle", C0338R.string.SharedGifsTitle));
                this.emptyImageView.setImageResource(C0338R.drawable.tip1);
                if (((int) this.dialog_id) == 0) {
                    this.emptyTextView.setText(LocaleController.getString("NoMediaSecret", C0338R.string.NoMediaSecret));
                } else {
                    this.emptyTextView.setText(LocaleController.getString("NoSharedGif", C0338R.string.NoSharedGif));
                }
                this.searchItem.setVisibility(music_item);
                if (!(this.sharedMediaData[this.selectedMode].loading || this.sharedMediaData[this.selectedMode].endReached[MEDIA_TYPE_ALL] || !this.sharedMediaData[this.selectedMode].messages.isEmpty())) {
                    this.sharedMediaData[this.selectedMode].loading = true;
                    SharedMediaQuery.loadMedia(this.dialog_id, MEDIA_TYPE_ALL, 50, MEDIA_TYPE_ALL, shared_media_video_item, true, this.classGuid);
                }
                if (this.sharedMediaData[this.selectedMode].loading && this.sharedMediaData[this.selectedMode].messages.isEmpty()) {
                    this.progressView.setVisibility(MEDIA_TYPE_ALL);
                    this.listView.setEmptyView(null);
                    this.emptyView.setVisibility(music_item);
                } else {
                    this.progressView.setVisibility(music_item);
                    this.listView.setEmptyView(this.emptyView);
                }
                this.listView.setVisibility(MEDIA_TYPE_ALL);
                this.listView.setPadding(MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, AndroidUtilities.dp(4.0f));
            }
        }
        updateDownloadItem();
    }

    private void updateAddToDownloadListItem() {
        ActionBarMenuItem item = this.actionBar.createActionMode().getItem(add_to_download_list);
        if (item != null) {
            item.setVisibility(MEDIA_TYPE_ALL);
            for (int i = shared_media_item; i >= 0; i--) {
                Object arrayList = new ArrayList(this.selectedFiles[i].keySet());
                Collections.sort(arrayList);
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    Integer num = (Integer) it.next();
                    if (num.intValue() > 0) {
                        MessageObject messageObject = (MessageObject) this.selectedFiles[i].get(num);
                        if (!(messageObject == null || num.intValue() <= 0 || DownloadUtil.m824a(messageObject))) {
                            item.setVisibility(music_item);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void updateDownloadItem() {
        if (this.selectedMode == forward) {
            this.downloadItem.setVisibility(music_item);
        } else {
            this.downloadItem.setVisibility(MEDIA_TYPE_ALL);
        }
    }

    private void updateGravity() {
        try {
            ActionBarMenu createActionMode = this.actionBar.createActionMode();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getParentActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int i = MEDIA_TYPE_ALL;
            int i2 = MEDIA_TYPE_ALL;
            while (i < createActionMode.getChildCount()) {
                View childAt = createActionMode.getChildAt(i);
                i += shared_media_item;
                i2 = childAt.getVisibility() == 0 ? childAt.getLayoutParams().width + i2 : i2;
            }
            LayoutParams layoutParams = (LayoutParams) createActionMode.getLayoutParams();
            layoutParams.height = -1;
            layoutParams.width = -1;
            if (((float) i2) > ((float) displayMetrics.widthPixels) / displayMetrics.density) {
                layoutParams.gravity = forward;
            } else {
                layoutParams.gravity = shared_media_video_item;
            }
            createActionMode.setLayoutParams(layoutParams);
        } catch (Exception e) {
        }
    }

    private void updateSelectAllListItem() {
        ActionBarMenuItem item = this.actionBar.createActionMode().getItem(select_all);
        if (item != null) {
            item.setVisibility(music_item);
            for (int i = shared_media_item; i >= 0; i--) {
                if (new ArrayList(this.selectedFiles[i].keySet()).size() > shared_media_item) {
                    item.setVisibility(MEDIA_TYPE_ALL);
                    return;
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
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        initThemeActionBar();
        this.actionBar.setTitle(TtmlNode.ANONYMOUS_REGION_ID);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new C16971());
        for (i = shared_media_item; i >= 0; i--) {
            this.selectedFiles[i].clear();
        }
        this.cantDeleteMessagesCount = MEDIA_TYPE_ALL;
        this.actionModeViews.clear();
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_menu_download);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            this.downloadItem = createMenu.addItemWithWidth((int) menu_download, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.downloadItem = createMenu.addItem(menu_download, C0338R.drawable.ic_menu_download, AndroidUtilities.dp(56.0f));
        }
        this.searchItem = createMenu.addItem((int) MEDIA_TYPE_ALL, (int) C0338R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C16982());
        this.searchItem.getSearchField().setHint(LocaleController.getString("Search", C0338R.string.Search));
        this.searchItem.setVisibility(music_item);
        this.dropDownContainer = new ActionBarMenuItem(context, createMenu, ThemeUtil.m2485a().m2294h());
        this.dropDownContainer.setSubMenuOpenSide(shared_media_item);
        this.dropDownContainer.addSubItem(shared_media_item, LocaleController.getString("SharedMediaTitle", C0338R.string.SharedMediaTitle), MEDIA_TYPE_ALL);
        this.dropDownContainer.addSubItem(shared_media_photo_item, LocaleController.getString("SharedPhotosTitle", C0338R.string.SharedPhotosTitle), MEDIA_TYPE_ALL);
        this.dropDownContainer.addSubItem(shared_media_video_item, LocaleController.getString("SharedVideosTitle", C0338R.string.SharedVideosTitle), MEDIA_TYPE_ALL);
        this.dropDownContainer.addSubItem(shared_media_gif_item, LocaleController.getString("SharedGifsTitle", C0338R.string.SharedGifsTitle), MEDIA_TYPE_ALL);
        this.dropDownContainer.addSubItem(files_item, LocaleController.getString("DocumentsTitle", C0338R.string.DocumentsTitle), MEDIA_TYPE_ALL);
        if (((int) this.dialog_id) != 0) {
            this.dropDownContainer.addSubItem(links_item, LocaleController.getString("LinksTitle", C0338R.string.LinksTitle), MEDIA_TYPE_ALL);
            this.dropDownContainer.addSubItem(music_item, LocaleController.getString("AudioTitle", C0338R.string.AudioTitle), MEDIA_TYPE_ALL);
        } else {
            EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            if (encryptedChat != null && AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 46) {
                this.dropDownContainer.addSubItem(music_item, LocaleController.getString("AudioTitle", C0338R.string.AudioTitle), MEDIA_TYPE_ALL);
            }
        }
        this.actionBar.addView(this.dropDownContainer, shared_media_item, LayoutHelper.createFrame(-2, Face.UNCOMPUTED_PROBABILITY, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
        this.dropDownContainer.setOnClickListener(new C16993());
        this.dropDown = new TextView(context);
        this.dropDown.setGravity(forward);
        this.dropDown.setSingleLine(true);
        this.dropDown.setLines(shared_media_item);
        this.dropDown.setMaxLines(shared_media_item);
        this.dropDown.setEllipsize(TruncateAt.END);
        this.dropDown.setTextColor(-1);
        this.dropDown.setTypeface(FontUtil.m1176a().m1160c());
        this.dropDown.setCompoundDrawablesWithIntrinsicBounds(MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, C0338R.drawable.ic_arrow_drop_down, MEDIA_TYPE_ALL);
        this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.dropDown.setPadding(MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, AndroidUtilities.dp(10.0f), MEDIA_TYPE_ALL);
        this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(createActionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.selectedMessagesCountTextView.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        this.selectedMessagesCountTextView.setMinimumWidth(AndroidUtilities.dp(22.0f));
        this.selectedMessagesCountTextView.setOnTouchListener(new C17004());
        createActionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear((int) MEDIA_TYPE_ALL, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 65, (int) MEDIA_TYPE_ALL, (int) MEDIA_TYPE_ALL, (int) MEDIA_TYPE_ALL));
        if (((int) this.dialog_id) != 0) {
            this.actionModeViews.add(createActionMode.addItem(select_all, C0338R.drawable.ic_ab_select_all, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(createActionMode.addItem(add_to_download_list, C0338R.drawable.ic_ab_download, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(createActionMode.addItem(multi_forward, C0338R.drawable.ic_ab_fwd_multiforward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(createActionMode.addItem(forward, C0338R.drawable.ic_ab_fwd_quoteforward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(createActionMode.addItem(forward_noname, C0338R.drawable.ic_ab_fwd_forward, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        }
        this.actionModeViews.add(createActionMode.addItem(delete, C0338R.drawable.ic_ab_fwd_delete, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(context);
        this.photoAdapter = new SharedPhotoVideoAdapter(context);
        this.gifAdapter = new SharedGifAdapter(context);
        this.videoAdapter = new SharedPhotoVideoAdapter(context);
        this.documentsAdapter = new SharedDocumentsAdapter(context, shared_media_item);
        this.audioAdapter = new SharedDocumentsAdapter(context, delete);
        this.documentsSearchAdapter = new MediaSearchAdapter(context, shared_media_item);
        this.audioSearchAdapter = new MediaSearchAdapter(context, delete);
        this.linksSearchAdapter = new MediaSearchAdapter(context, forward);
        this.linksAdapter = new SharedLinksAdapter(context);
        View frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.listView = new SectionsListView(context);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(MEDIA_TYPE_ALL);
        this.listView.setDrawSelectorOnTop(true);
        this.listView.setClipToPadding(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.listView.setOnItemClickListener(new C17015());
        this.listView.setOnScrollListener(new C17026());
        this.listView.setOnItemLongClickListener(new C17037());
        for (i = MEDIA_TYPE_ALL; i < files_item; i += shared_media_item) {
            this.cellCache.add(new SharedPhotoVideoCell(context));
        }
        this.emptyView = new LinearLayout(context);
        this.emptyView.setOrientation(shared_media_item);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(music_item);
        this.emptyView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.emptyView.setOnTouchListener(new C17048());
        this.emptyImageView = new ImageView(context);
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        this.emptyTextView = new TextView(context);
        this.emptyTextView.setTextColor(-7697782);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setTextSize(shared_media_item, 17.0f);
        this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), MEDIA_TYPE_ALL, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        this.emptyView.addView(this.emptyTextView, LayoutHelper.createLinear(-2, -2, 17, (int) MEDIA_TYPE_ALL, 24, (int) MEDIA_TYPE_ALL, (int) MEDIA_TYPE_ALL));
        this.progressView = new LinearLayout(context);
        this.progressView.setGravity(17);
        this.progressView.setOrientation(shared_media_item);
        this.progressView.setVisibility(music_item);
        this.progressView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.progressView.addView(new ProgressBar(context), LayoutHelper.createLinear(-2, -2));
        switchToCurrentSelectedMode();
        if (!AndroidUtilities.isTablet()) {
            frameLayout.addView(new PlayerView(context, this), LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            frameLayout.addView(new PowerView(context, this), LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        updateDownloadItem();
        showMaterialHelp();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        ArrayList arrayList;
        int i2;
        ActionBarMenuItem actionBarMenuItem;
        int i3;
        if (i == NotificationCenter.mediaDidLoaded) {
            long longValue = ((Long) objArr[MEDIA_TYPE_ALL]).longValue();
            if (((Integer) objArr[forward]).intValue() == this.classGuid) {
                int intValue = ((Integer) objArr[delete]).intValue();
                this.sharedMediaData[intValue].loading = false;
                this.sharedMediaData[intValue].totalCount = ((Integer) objArr[shared_media_item]).intValue();
                arrayList = (ArrayList) objArr[shared_media_photo_item];
                boolean z = ((int) this.dialog_id) == 0;
                int i4 = longValue == this.dialog_id ? MEDIA_TYPE_ALL : shared_media_item;
                for (i2 = MEDIA_TYPE_ALL; i2 < arrayList.size(); i2 += shared_media_item) {
                    this.sharedMediaData[intValue].addMessage((MessageObject) arrayList.get(i2), false, z);
                }
                this.sharedMediaData[intValue].endReached[i4] = ((Boolean) objArr[shared_media_video_item]).booleanValue();
                if (this.sharedMediaData[intValue].messages.size() == 0 && arrayList.size() > 0) {
                    this.sharedMediaData[this.selectedMode].loading = true;
                    SharedMediaQuery.loadMedia(this.dialog_id, MEDIA_TYPE_ALL, 50, this.sharedMediaData[this.selectedMode].max_id[MEDIA_TYPE_ALL], intValue, true, this.classGuid);
                }
                if (i4 == 0 && this.sharedMediaData[this.selectedMode].messages.isEmpty() && this.mergeDialogId != 0) {
                    this.sharedMediaData[this.selectedMode].loading = true;
                    SharedMediaQuery.loadMedia(this.mergeDialogId, MEDIA_TYPE_ALL, 50, this.sharedMediaData[this.selectedMode].max_id[shared_media_item], intValue, true, this.classGuid);
                }
                if (!this.sharedMediaData[this.selectedMode].loading) {
                    if (this.progressView != null) {
                        this.progressView.setVisibility(music_item);
                    }
                    if (this.selectedMode == intValue && this.listView != null && this.listView.getEmptyView() == null) {
                        this.listView.setEmptyView(this.emptyView);
                    }
                }
                this.scrolling = true;
                if (this.selectedMode == 0 && intValue == 0) {
                    if (this.photoVideoAdapter != null) {
                        this.photoVideoAdapter.notifyDataSetChanged();
                    }
                    if (this.photoAdapter != null) {
                        this.photoAdapter.notifyDataSetChanged();
                    }
                    if (this.videoAdapter != null) {
                        this.videoAdapter.notifyDataSetChanged();
                    }
                } else if (this.selectedMode == shared_media_item && intValue == shared_media_item) {
                    if (this.documentsAdapter != null) {
                        this.documentsAdapter.notifyDataSetChanged();
                    }
                } else if (this.selectedMode == forward && intValue == forward) {
                    if (this.linksAdapter != null) {
                        this.linksAdapter.notifyDataSetChanged();
                    }
                } else if (this.selectedMode == delete && intValue == delete) {
                    if (this.audioAdapter != null) {
                        this.audioAdapter.notifyDataSetChanged();
                    }
                } else if (this.selectedMode == shared_media_video_item && this.gifAdapter != null) {
                    this.gifAdapter.notifyDataSetChanged();
                }
                if (this.selectedMode == shared_media_item || this.selectedMode == forward || this.selectedMode == delete) {
                    actionBarMenuItem = this.searchItem;
                    i3 = (this.sharedMediaData[this.selectedMode].messages.isEmpty() || this.searching) ? music_item : MEDIA_TYPE_ALL;
                    actionBarMenuItem.setVisibility(i3);
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            int i5;
            Chat chat = ((int) this.dialog_id) < 0 ? MessagesController.getInstance().getChat(Integer.valueOf(-((int) this.dialog_id))) : null;
            r2 = ((Integer) objArr[shared_media_item]).intValue();
            if (ChatObject.isChannel(chat)) {
                if (r2 == 0 && this.mergeDialogId != 0) {
                    i5 = shared_media_item;
                } else if (r2 == chat.id) {
                    i5 = MEDIA_TYPE_ALL;
                } else {
                    return;
                }
            } else if (r2 == 0) {
                i5 = MEDIA_TYPE_ALL;
            } else {
                return;
            }
            r2 = null;
            Iterator it = ((ArrayList) objArr[MEDIA_TYPE_ALL]).iterator();
            while (it.hasNext()) {
                r0 = (Integer) it.next();
                SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                int length = sharedMediaDataArr.length;
                for (i2 = MEDIA_TYPE_ALL; i2 < length; i2 += shared_media_item) {
                    if (sharedMediaDataArr[i2].deleteMessage(r0.intValue(), i5)) {
                        r2 = shared_media_item;
                    }
                }
            }
            if (r2 != null) {
                this.scrolling = true;
                if (this.photoVideoAdapter != null) {
                    this.photoVideoAdapter.notifyDataSetChanged();
                }
                if (this.photoAdapter != null) {
                    this.photoAdapter.notifyDataSetChanged();
                }
                if (this.gifAdapter != null) {
                    this.gifAdapter.notifyDataSetChanged();
                }
                if (this.videoAdapter != null) {
                    this.videoAdapter.notifyDataSetChanged();
                }
                if (this.documentsAdapter != null) {
                    this.documentsAdapter.notifyDataSetChanged();
                }
                if (this.linksAdapter != null) {
                    this.linksAdapter.notifyDataSetChanged();
                }
                if (this.audioAdapter != null) {
                    this.audioAdapter.notifyDataSetChanged();
                }
                if (this.selectedMode == shared_media_item || this.selectedMode == forward || this.selectedMode == delete) {
                    actionBarMenuItem = this.searchItem;
                    i3 = (this.sharedMediaData[this.selectedMode].messages.isEmpty() || this.searching) ? music_item : MEDIA_TYPE_ALL;
                    actionBarMenuItem.setVisibility(i3);
                }
            }
        } else if (i == NotificationCenter.didReceivedNewMessages) {
            if (((Long) objArr[MEDIA_TYPE_ALL]).longValue() == this.dialog_id) {
                arrayList = (ArrayList) objArr[shared_media_item];
                boolean z2 = ((int) this.dialog_id) == 0;
                r2 = null;
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    MessageObject messageObject = (MessageObject) it2.next();
                    if (messageObject.messageOwner.media != null) {
                        r4 = SharedMediaQuery.getMediaType(messageObject.messageOwner);
                        if (r4 != -1) {
                            r2 = this.sharedMediaData[r4].addMessage(messageObject, true, z2) ? shared_media_item : r2;
                        } else {
                            return;
                        }
                    }
                }
                if (r2 != null) {
                    this.scrolling = true;
                    if (this.photoVideoAdapter != null) {
                        this.photoVideoAdapter.notifyDataSetChanged();
                    }
                    if (this.photoAdapter != null) {
                        this.photoAdapter.notifyDataSetChanged();
                    }
                    if (this.gifAdapter != null) {
                        this.gifAdapter.notifyDataSetChanged();
                    }
                    if (this.videoAdapter != null) {
                        this.videoAdapter.notifyDataSetChanged();
                    }
                    if (this.documentsAdapter != null) {
                        this.documentsAdapter.notifyDataSetChanged();
                    }
                    if (this.linksAdapter != null) {
                        this.linksAdapter.notifyDataSetChanged();
                    }
                    if (this.audioAdapter != null) {
                        this.audioAdapter.notifyDataSetChanged();
                    }
                    if (this.selectedMode == shared_media_item || this.selectedMode == forward || this.selectedMode == delete) {
                        actionBarMenuItem = this.searchItem;
                        i3 = (this.sharedMediaData[this.selectedMode].messages.isEmpty() || this.searching) ? music_item : MEDIA_TYPE_ALL;
                        actionBarMenuItem.setVisibility(i3);
                    }
                }
            }
        } else if (i == NotificationCenter.messageReceivedByServer) {
            r0 = (Integer) objArr[MEDIA_TYPE_ALL];
            Integer num = (Integer) objArr[shared_media_item];
            SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
            r4 = sharedMediaDataArr2.length;
            for (r2 = MEDIA_TYPE_ALL; r2 < r4; r2 += shared_media_item) {
                sharedMediaDataArr2[r2].replaceMid(r0.intValue(), num.intValue());
            }
        }
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        int i2 = MEDIA_TYPE_ALL;
        if (messageObject == null || this.listView == null || (this.selectedMode != 0 && this.selectedMode != shared_media_video_item)) {
            return null;
        }
        int childCount = this.listView.getChildCount();
        for (int i3 = MEDIA_TYPE_ALL; i3 < childCount; i3 += shared_media_item) {
            View childAt = this.listView.getChildAt(i3);
            int i4;
            MessageObject messageObject2;
            BackupImageView imageView;
            if (childAt instanceof SharedPhotoVideoCell) {
                SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) childAt;
                i4 = MEDIA_TYPE_ALL;
                while (i4 < files_item) {
                    messageObject2 = sharedPhotoVideoCell.getMessageObject(i4);
                    if (messageObject2 == null) {
                        break;
                        continue;
                    } else {
                        imageView = sharedPhotoVideoCell.getImageView(i4);
                        if (messageObject2.getId() == messageObject.getId()) {
                            int[] iArr = new int[shared_media_photo_item];
                            imageView.getLocationInWindow(iArr);
                            PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                            placeProviderObject.viewX = iArr[MEDIA_TYPE_ALL];
                            placeProviderObject.viewY = iArr[shared_media_item] - (VERSION.SDK_INT >= 21 ? MEDIA_TYPE_ALL : AndroidUtilities.statusBarHeight);
                            placeProviderObject.parentView = this.listView;
                            placeProviderObject.imageReceiver = imageView.getImageReceiver();
                            placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
                            placeProviderObject.parentView.getLocationInWindow(iArr);
                            placeProviderObject.clipTopAddition = AndroidUtilities.dp(40.0f);
                            return placeProviderObject;
                        }
                        i4 += shared_media_item;
                    }
                }
                continue;
            } else if (childAt instanceof com.hanista.mobogram.mobo.p009j.SharedGifCell) {
                com.hanista.mobogram.mobo.p009j.SharedGifCell sharedGifCell = (com.hanista.mobogram.mobo.p009j.SharedGifCell) childAt;
                i4 = MEDIA_TYPE_ALL;
                while (i4 < files_item) {
                    messageObject2 = sharedGifCell.m1238b(i4);
                    if (messageObject2 != null) {
                        imageView = sharedGifCell.m1235a(i4);
                        if (messageObject2.getId() == messageObject.getId()) {
                            int[] iArr2 = new int[shared_media_photo_item];
                            imageView.getLocationInWindow(iArr2);
                            PlaceProviderObject placeProviderObject2 = new PlaceProviderObject();
                            placeProviderObject2.viewX = iArr2[MEDIA_TYPE_ALL];
                            i3 = iArr2[shared_media_item];
                            if (VERSION.SDK_INT < 21) {
                                i2 = AndroidUtilities.statusBarHeight;
                            }
                            placeProviderObject2.viewY = i3 - i2;
                            placeProviderObject2.parentView = this.listView;
                            placeProviderObject2.imageReceiver = imageView.getImageReceiver();
                            placeProviderObject2.thumb = placeProviderObject2.imageReceiver.getBitmap();
                            placeProviderObject2.parentView.getLocationInWindow(iArr2);
                            placeProviderObject2.clipTopAddition = AndroidUtilities.dp(40.0f);
                            return placeProviderObject2;
                        }
                        i4 += shared_media_item;
                    } else {
                        break;
                        continue;
                    }
                }
                continue;
            } else {
                continue;
            }
        }
        return null;
    }

    public int getSelectedCount() {
        return MEDIA_TYPE_ALL;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        return null;
    }

    public void initThemeActionBar() {
        if (ThemeUtil.m2490b()) {
            Drawable backDrawable = new BackDrawable(false);
            ((BackDrawable) backDrawable).setColor(AdvanceTheme.bi);
            this.actionBar.setBackButtonDrawable(backDrawable);
            this.actionBar.setBackgroundColor(AdvanceTheme.f2491b);
        }
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    public boolean onBackPressed() {
        int i = shared_media_item;
        if (this.actionBar == null || !this.actionBar.isActionModeShowed()) {
            return true;
        }
        while (i >= 0) {
            this.selectedFiles[i].clear();
            i--;
        }
        this.actionBar.hideActionMode();
        this.cantDeleteMessagesCount = MEDIA_TYPE_ALL;
        notifyDataSetChanged();
        return false;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new C17059());
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
        this.dialog_id = getArguments().getLong("dialog_id", 0);
        for (int i = MEDIA_TYPE_ALL; i < this.sharedMediaData.length; i += shared_media_item) {
            this.sharedMediaData[i] = new SharedMediaData();
            this.sharedMediaData[i].max_id[MEDIA_TYPE_ALL] = ((int) this.dialog_id) == 0 ? TLRPC.MESSAGE_FLAG_MEGAGROUP : ConnectionsManager.DEFAULT_DATACENTER_ID;
            if (!(this.mergeDialogId == 0 || this.info == null)) {
                this.sharedMediaData[i].max_id[shared_media_item] = this.info.migrated_from_max_id;
                this.sharedMediaData[i].endReached[shared_media_item] = false;
            }
        }
        this.sharedMediaData[MEDIA_TYPE_ALL].loading = true;
        SharedMediaQuery.loadMedia(this.dialog_id, MEDIA_TYPE_ALL, 50, MEDIA_TYPE_ALL, MEDIA_TYPE_ALL, true, this.classGuid);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
    }

    public void onPause() {
        super.onPause();
        if (this.dropDownContainer != null) {
            this.dropDownContainer.closeSubMenu();
        }
    }

    public void onResume() {
        super.onResume();
        this.scrolling = true;
        if (this.photoVideoAdapter != null) {
            this.photoVideoAdapter.notifyDataSetChanged();
        }
        if (this.photoAdapter != null) {
            this.photoAdapter.notifyDataSetChanged();
        }
        if (this.gifAdapter != null) {
            this.gifAdapter.notifyDataSetChanged();
        }
        if (this.videoAdapter != null) {
            this.videoAdapter.notifyDataSetChanged();
        }
        if (this.documentsAdapter != null) {
            this.documentsAdapter.notifyDataSetChanged();
        }
        if (this.linksAdapter != null) {
            this.linksAdapter.notifyDataSetChanged();
        }
        fixLayoutInternal();
        initThemeActionBar();
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
    }

    public void setArchive(Archive archive) {
        this.archive = archive;
    }

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        if (this.info != null && this.info.migrated_from_chat_id != 0) {
            this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
        }
    }

    public void setMergeDialogId(long j) {
        this.mergeDialogId = j;
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
