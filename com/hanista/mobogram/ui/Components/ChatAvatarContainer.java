package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.mobo.p000a.Archive;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatFull;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.SimpleTextView;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.ProfileActivity;

public class ChatAvatarContainer extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private int onlineCount;
    private ChatActivity parentFragment;
    private RecordStatusDrawable recordStatusDrawable;
    private SendingFileExDrawable sendingFileDrawable;
    private SimpleTextView subtitleTextView;
    private ImageView timeItem;
    private TimerDrawable timerDrawable;
    private SimpleTextView titleTextView;
    private TypingDotsDrawable typingDotsDrawable;

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAvatarContainer.1 */
    class C13331 implements OnClickListener {
        C13331() {
        }

        public void onClick(View view) {
            ChatAvatarContainer.this.parentFragment.showDialog(AndroidUtilities.buildTTLAlert(ChatAvatarContainer.this.getContext(), ChatAvatarContainer.this.parentFragment.getCurrentEncryptedChat()).create());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ChatAvatarContainer.2 */
    class C13342 implements OnClickListener {
        C13342() {
        }

        public void onClick(View view) {
            User currentUser = ChatAvatarContainer.this.parentFragment.getCurrentUser();
            Chat currentChat = ChatAvatarContainer.this.parentFragment.getCurrentChat();
            if (currentUser != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", currentUser.id);
                if (ChatAvatarContainer.this.timeItem != null) {
                    bundle.putLong("dialog_id", ChatAvatarContainer.this.parentFragment.getDialogId());
                }
                BaseFragment profileActivity = new ProfileActivity(bundle);
                profileActivity.setPlayProfileAnimation(true);
                ChatAvatarContainer.this.parentFragment.presentFragment(profileActivity);
            } else if (currentChat != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putInt("chat_id", currentChat.id);
                BaseFragment profileActivity2 = new ProfileActivity(bundle2);
                profileActivity2.setChatInfo(ChatAvatarContainer.this.parentFragment.getCurrentChatInfo());
                profileActivity2.setPlayProfileAnimation(true);
                ChatAvatarContainer.this.parentFragment.presentFragment(profileActivity2);
            }
        }
    }

    public ChatAvatarContainer(Context context, ChatActivity chatActivity, boolean z) {
        boolean z2 = true;
        super(context);
        this.avatarDrawable = new AvatarDrawable();
        this.onlineCount = -1;
        this.parentFragment = chatActivity;
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        addView(this.avatarImageView);
        this.titleTextView = new SimpleTextView(context);
        this.titleTextView.setTextColor(-1);
        this.titleTextView.setTextSize(18);
        this.titleTextView.setGravity(3);
        this.titleTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
        this.titleTextView.setRightDrawableTopPadding(-AndroidUtilities.dp(1.3f));
        addView(this.titleTextView);
        this.subtitleTextView = new SimpleTextView(context);
        this.subtitleTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.subtitleTextView.setTextColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
        this.subtitleTextView.setTextSize(14);
        this.subtitleTextView.setGravity(3);
        addView(this.subtitleTextView);
        if (z) {
            this.timeItem = new ImageView(context);
            this.timeItem.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
            this.timeItem.setScaleType(ScaleType.CENTER);
            ImageView imageView = this.timeItem;
            Drawable timerDrawable = new TimerDrawable(context);
            this.timerDrawable = timerDrawable;
            imageView.setImageDrawable(timerDrawable);
            addView(this.timeItem);
            this.timeItem.setOnClickListener(new C13331());
        }
        setOnClickListener(new C13342());
        Chat currentChat = this.parentFragment.getCurrentChat();
        this.typingDotsDrawable = new TypingDotsDrawable();
        this.typingDotsDrawable.setIsChat(currentChat != null);
        this.recordStatusDrawable = new RecordStatusDrawable();
        this.recordStatusDrawable.setIsChat(currentChat != null);
        this.sendingFileDrawable = new SendingFileExDrawable();
        SendingFileExDrawable sendingFileExDrawable = this.sendingFileDrawable;
        if (currentChat == null) {
            z2 = false;
        }
        sendingFileExDrawable.setIsChat(z2);
    }

    private void setTypingAnimation(boolean z) {
        if (z) {
            try {
                Integer num = (Integer) MessagesController.getInstance().printingStringsTypes.get(Long.valueOf(this.parentFragment.getDialogId()));
                if (num.intValue() == 0) {
                    this.subtitleTextView.setLeftDrawable(this.typingDotsDrawable);
                    this.typingDotsDrawable.start();
                    this.recordStatusDrawable.stop();
                    this.sendingFileDrawable.stop();
                    return;
                } else if (num.intValue() == 1) {
                    this.subtitleTextView.setLeftDrawable(this.recordStatusDrawable);
                    this.recordStatusDrawable.start();
                    this.typingDotsDrawable.stop();
                    this.sendingFileDrawable.stop();
                    return;
                } else if (num.intValue() == 2) {
                    this.subtitleTextView.setLeftDrawable(this.sendingFileDrawable);
                    this.sendingFileDrawable.start();
                    this.typingDotsDrawable.stop();
                    this.recordStatusDrawable.stop();
                    return;
                } else {
                    return;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                return;
            }
        }
        this.subtitleTextView.setLeftDrawable(null);
        this.typingDotsDrawable.stop();
        this.recordStatusDrawable.stop();
        this.sendingFileDrawable.stop();
    }

    public void checkAndUpdateAvatar() {
        TLObject tLObject = null;
        User currentUser = this.parentFragment.getCurrentUser();
        Chat currentChat = this.parentFragment.getCurrentChat();
        if (currentUser != null) {
            if (currentUser.photo != null) {
                tLObject = currentUser.photo.photo_small;
            }
            this.avatarDrawable.setInfo(currentUser);
        } else if (currentChat != null) {
            if (currentChat.photo != null) {
                tLObject = currentChat.photo.photo_small;
            }
            this.avatarDrawable.setInfo(currentChat);
        }
        if (this.avatarImageView != null) {
            this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
        }
        if (ThemeUtil.m2490b()) {
            int dp = AndroidUtilities.dp((float) AdvanceTheme.cb);
            if (this.avatarImageView != null) {
                this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
                this.avatarImageView.setRoundRadius(dp);
            }
            if (this.avatarDrawable != null) {
                this.avatarDrawable.setRadius(dp);
            }
        }
    }

    public void hideTimeItem() {
        if (this.timeItem != null) {
            this.timeItem.setVisibility(8);
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int currentActionBarHeight = (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(42.0f)) / 2);
        if (this.parentFragment == null || this.parentFragment.getArchive() == null) {
            this.avatarImageView.layout(AndroidUtilities.dp(8.0f), currentActionBarHeight, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(42.0f) + currentActionBarHeight);
            this.titleTextView.layout(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(1.3f) + currentActionBarHeight, AndroidUtilities.dp(62.0f) + this.titleTextView.getMeasuredWidth(), (this.titleTextView.getTextHeight() + currentActionBarHeight) + AndroidUtilities.dp(1.3f));
            if (this.timeItem != null) {
                this.timeItem.layout(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(15.0f) + currentActionBarHeight, AndroidUtilities.dp(58.0f), AndroidUtilities.dp(49.0f) + currentActionBarHeight);
            }
            this.subtitleTextView.layout(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(24.0f) + currentActionBarHeight, AndroidUtilities.dp(62.0f) + this.subtitleTextView.getMeasuredWidth(), (currentActionBarHeight + this.subtitleTextView.getTextHeight()) + AndroidUtilities.dp(24.0f));
            return;
        }
        this.avatarImageView.layout(AndroidUtilities.dp(8.0f), currentActionBarHeight, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(42.0f) + currentActionBarHeight);
        this.titleTextView.layout(AndroidUtilities.dp(8.0f), currentActionBarHeight, AndroidUtilities.dp(8.0f) + this.titleTextView.getMeasuredWidth(), (this.titleTextView.getTextHeight() + currentActionBarHeight) + AndroidUtilities.dp(1.3f));
        if (this.timeItem != null) {
            this.timeItem.layout(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(15.0f) + currentActionBarHeight, AndroidUtilities.dp(58.0f), AndroidUtilities.dp(49.0f) + currentActionBarHeight);
        }
        this.subtitleTextView.layout(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(24.0f) + currentActionBarHeight, AndroidUtilities.dp(8.0f) + this.subtitleTextView.getMeasuredWidth(), (currentActionBarHeight + this.subtitleTextView.getTextHeight()) + AndroidUtilities.dp(24.0f));
    }

    protected void onMeasure(int i, int i2) {
        int size = MeasureSpec.getSize(i);
        int dp = size - AndroidUtilities.dp(70.0f);
        if (!(this.parentFragment == null || this.parentFragment.getArchive() == null)) {
            dp = size - AndroidUtilities.dp(16.0f);
        }
        this.avatarImageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), C0700C.ENCODING_PCM_32BIT));
        this.titleTextView.measure(MeasureSpec.makeMeasureSpec(dp, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), TLRPC.MESSAGE_FLAG_MEGAGROUP));
        this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(dp, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), TLRPC.MESSAGE_FLAG_MEGAGROUP));
        if (this.timeItem != null) {
            this.timeItem.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0f), C0700C.ENCODING_PCM_32BIT));
        }
        setMeasuredDimension(size, MeasureSpec.getSize(i2));
    }

    public void setSubtitleColor(int i) {
        this.subtitleTextView.setTextColor(i);
    }

    public void setSubtitleSize(int i) {
        this.subtitleTextView.setTextSize(i);
    }

    public void setTime(int i) {
        if (this.timerDrawable != null) {
            this.timerDrawable.setTime(i);
        }
    }

    public void setTitle(CharSequence charSequence) {
        this.titleTextView.setText(charSequence);
    }

    public void setTitleColor(int i) {
        this.titleTextView.setTextColor(i);
    }

    public void setTitleIcons(int i, int i2) {
        this.titleTextView.setLeftDrawable(i);
        this.titleTextView.setRightDrawable(i2);
    }

    public void setTitleSize(int i) {
        this.titleTextView.setTextSize(i);
    }

    public void showTimeItem() {
        if (this.timeItem != null) {
            this.timeItem.setVisibility(0);
        }
    }

    public void updateOnlineCount() {
        this.onlineCount = 0;
        ChatFull currentChatInfo = this.parentFragment.getCurrentChatInfo();
        if (currentChatInfo != null) {
            int currentTime = ConnectionsManager.getInstance().getCurrentTime();
            if ((currentChatInfo instanceof TL_chatFull) || ((currentChatInfo instanceof TL_channelFull) && currentChatInfo.participants_count <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && currentChatInfo.participants != null)) {
                for (int i = 0; i < currentChatInfo.participants.participants.size(); i++) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) currentChatInfo.participants.participants.get(i)).user_id));
                    if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getClientUserId()) || user.status.expires <= AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS))) {
                        this.onlineCount++;
                    }
                }
            }
        }
    }

    public void updateSubtitle() {
        User currentUser = this.parentFragment.getCurrentUser();
        Chat currentChat = this.parentFragment.getCurrentChat();
        CharSequence charSequence = (CharSequence) MessagesController.getInstance().printingStrings.get(Long.valueOf(this.parentFragment.getDialogId()));
        if (charSequence != null) {
            charSequence = TextUtils.replace(charSequence, new String[]{"..."}, new String[]{TtmlNode.ANONYMOUS_REGION_ID});
        }
        if (charSequence == null || charSequence.length() == 0 || (ChatObject.isChannel(currentChat) && !currentChat.megagroup)) {
            setTypingAnimation(false);
            if (currentChat != null) {
                ChatFull currentChatInfo = this.parentFragment.getCurrentChatInfo();
                if (ChatObject.isChannel(currentChat)) {
                    if (currentChatInfo == null || currentChatInfo.participants_count == 0) {
                        if (currentChat.megagroup) {
                            this.subtitleTextView.setText(LocaleController.getString("Loading", C0338R.string.Loading).toLowerCase());
                        } else if ((currentChat.flags & 64) != 0) {
                            this.subtitleTextView.setText(LocaleController.getString("ChannelPublic", C0338R.string.ChannelPublic).toLowerCase());
                        } else {
                            this.subtitleTextView.setText(LocaleController.getString("ChannelPrivate", C0338R.string.ChannelPrivate).toLowerCase());
                        }
                    } else if (!currentChat.megagroup || currentChatInfo.participants_count > Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                        int[] iArr = new int[1];
                        this.subtitleTextView.setText(LocaleController.formatPluralString("Members", iArr[0]).replace(String.format("%d", new Object[]{Integer.valueOf(iArr[0])}), LocaleController.formatShortNumber(currentChatInfo.participants_count, iArr)));
                    } else if (this.onlineCount <= 1 || currentChatInfo.participants_count == 0) {
                        this.subtitleTextView.setText(LocaleController.formatPluralString("Members", currentChatInfo.participants_count));
                    } else {
                        this.subtitleTextView.setText(String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", currentChatInfo.participants_count), LocaleController.formatPluralString("Online", this.onlineCount)}));
                    }
                } else if (ChatObject.isKickedFromChat(currentChat)) {
                    this.subtitleTextView.setText(LocaleController.getString("YouWereKicked", C0338R.string.YouWereKicked));
                } else if (ChatObject.isLeftFromChat(currentChat)) {
                    this.subtitleTextView.setText(LocaleController.getString("YouLeft", C0338R.string.YouLeft));
                } else {
                    int i = currentChat.participants_count;
                    if (!(currentChatInfo == null || currentChatInfo.participants == null)) {
                        i = currentChatInfo.participants.participants.size();
                    }
                    if (this.onlineCount <= 1 || i == 0) {
                        this.subtitleTextView.setText(LocaleController.formatPluralString("Members", i));
                    } else {
                        this.subtitleTextView.setText(String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", i), LocaleController.formatPluralString("Online", this.onlineCount)}));
                    }
                }
            } else if (currentUser != null) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(currentUser.id));
                charSequence = (user.id == 333000 || user.id == 777000) ? LocaleController.getString("ServiceNotifications", C0338R.string.ServiceNotifications) : user.bot ? LocaleController.getString("Bot", C0338R.string.Bot) : LocaleController.formatUserStatus(user);
                this.subtitleTextView.setText(charSequence);
            }
        } else {
            this.subtitleTextView.setText(charSequence);
            setTypingAnimation(true);
        }
        if (this.parentFragment != null && this.parentFragment.getArchive() != null) {
            Archive archive = this.parentFragment.getArchive();
            if (archive.m204a().longValue() == -1) {
                this.subtitleTextView.setText(LocaleController.getString("NotCategorized", C0338R.string.NotCategorized));
            } else if (archive.m204a().longValue() == 0) {
                this.subtitleTextView.setText(LocaleController.getString("All", C0338R.string.All));
            } else {
                this.subtitleTextView.setText(archive.m208b());
            }
            this.titleTextView.setText(LocaleController.getString("FavoriteMessages", C0338R.string.FavoriteMessages));
            this.avatarImageView.setVisibility(8);
            setOnClickListener(null);
        }
    }
}
