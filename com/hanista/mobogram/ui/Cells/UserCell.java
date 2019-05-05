package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.SimpleTextView;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.CheckBoxSquare;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.LaunchActivity;
import com.hanista.mobogram.ui.PhotoViewer;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import com.hanista.mobogram.ui.ProfileActivity;

public class UserCell extends FrameLayout implements PhotoViewerProvider {
    private ImageView adminImage;
    private AvatarDrawable avatarDrawable;
    public BackupImageView avatarImageView;
    private CheckBox checkBox;
    private CheckBoxSquare checkBoxBig;
    private Drawable curDrawable;
    private int currentDrawable;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currrntStatus;
    private ImageView imageView;
    private FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    public ImageView mutualImageView;
    private int nameColor;
    private SimpleTextView nameTextView;
    private int radius;
    private int statusColor;
    private int statusOnlineColor;
    private SimpleTextView statusTextView;

    /* renamed from: com.hanista.mobogram.ui.Cells.UserCell.1 */
    class C11181 implements OnClickListener {
        C11181() {
        }

        public void onClick(View view) {
            if (UserCell.this.currentObject instanceof User) {
                User user = (User) UserCell.this.currentObject;
                if (MoboConstants.aB == 2) {
                    if (user.photo != null && user.photo.photo_big != null) {
                        PhotoViewer.getInstance().openPhoto(user.photo.photo_big, UserCell.this);
                    }
                } else if (MoboConstants.aB == 1 && PhotoViewer.getInstance().getParentActivity() != null && (PhotoViewer.getInstance().getParentActivity() instanceof LaunchActivity)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", user.id);
                    ((LaunchActivity) PhotoViewer.getInstance().getParentActivity()).presentFragment(new ProfileActivity(bundle));
                }
            }
        }
    }

    public UserCell(Context context, int i, int i2, boolean z) {
        float f;
        float f2;
        super(context);
        this.statusColor = -5723992;
        this.statusOnlineColor = -12876608;
        this.curDrawable = null;
        this.nameColor = Theme.MSG_TEXT_COLOR;
        this.radius = 32;
        this.avatarDrawable = new AvatarDrawable();
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 7), 8.0f, LocaleController.isRTL ? (float) (i + 7) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view = this.nameTextView;
        int i3 = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = (float) ((i2 == 2 ? 18 : 0) + 28);
        } else {
            f = (float) (i + 68);
        }
        if (LocaleController.isRTL) {
            f2 = (float) (i + 68);
        } else {
            f2 = (float) ((i2 == 2 ? 18 : 0) + 28);
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i3, f, 11.5f, f2, 0.0f));
        this.mutualImageView = new ImageView(context);
        this.mutualImageView.setScaleType(ScaleType.CENTER);
        this.mutualImageView.setVisibility(8);
        this.mutualImageView.setImageResource(ThemeUtil.m2485a().m2299m());
        addView(this.mutualImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 7.0f : 0.0f, 8.0f, LocaleController.isRTL ? 0.0f : 7.0f, 0.0f));
        this.statusTextView = new SimpleTextView(context);
        this.statusTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.statusTextView.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (i + 68), 34.5f, LocaleController.isRTL ? (float) (i + 68) : 28.0f, 0.0f));
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i2 == 2) {
            this.checkBoxBig = new CheckBoxSquare(context);
            addView(this.checkBoxBig, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : 5) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i2 == 1) {
            this.checkBox = new CheckBox(context, C0338R.drawable.round_check2);
            this.checkBox.setVisibility(4);
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 37), 38.0f, LocaleController.isRTL ? (float) (i + 37) : 0.0f, 0.0f));
        }
        setBackgroundResource(ThemeUtil.m2485a().m2293g());
        if (z) {
            this.adminImage = new ImageView(context);
            addView(this.adminImage, LayoutHelper.createFrame(16, 16.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 24.0f : 0.0f, 13.5f, LocaleController.isRTL ? 0.0f : 24.0f, 0.0f));
        }
        initOnAvatarClickListener();
    }

    private void initOnAvatarClickListener() {
        if (MoboConstants.aB != 0) {
            this.avatarImageView.setOnClickListener(new C11181());
        }
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            String obj = getTag() != null ? getTag().toString() : TtmlNode.ANONYMOUS_REGION_ID;
            Drawable drawable = getContext().getResources().getDrawable(C0338R.drawable.ic_mutual_white);
            drawable.setColorFilter(AdvanceTheme.aT, Mode.SRC_IN);
            this.mutualImageView.setImageDrawable(drawable);
            if (obj.contains("Contacts")) {
                setStatusColors(AdvanceTheme.aP, AdvanceTheme.aQ);
                this.nameColor = AdvanceTheme.aR;
                this.nameTextView.setTextColor(this.nameColor);
                this.nameTextView.setTextSize(AdvanceTheme.aX);
                setStatusSize(AdvanceTheme.aZ);
                setAvatarRadius(AdvanceTheme.aY);
            } else if (obj.contains("Profile")) {
                setStatusColors(AdvanceTheme.aH, AdvanceTheme.aI);
                this.nameColor = AdvanceTheme.aB;
                this.nameTextView.setTextColor(this.nameColor);
                this.nameTextView.setTextSize(17);
                setStatusSize(14);
                setAvatarRadius(AdvanceTheme.aG);
                int i = AdvanceTheme.aF;
                if (this.currentDrawable != 0) {
                    getResources().getDrawable(this.currentDrawable).setColorFilter(i, Mode.SRC_IN);
                }
                if (this.adminImage != null) {
                    this.adminImage.setColorFilter(i, Mode.SRC_IN);
                }
            } else if (obj.contains("Pref")) {
                setStatusColors(AdvanceTheme.f2495f, AdvanceTheme.m2276a(AdvanceTheme.f2491b, -64));
                this.nameColor = AdvanceTheme.f2494e;
                this.nameTextView.setTextColor(this.nameColor);
            }
        }
    }

    public boolean allowCaption() {
        return false;
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        if (fileLocation == null || this.currentObject == null) {
            return null;
        }
        FileLocation fileLocation2;
        int i2;
        if (this.currentObject instanceof User) {
            User user = (User) this.currentObject;
            int i3 = user.id;
            FileLocation fileLocation3 = (user == null || user.photo == null || user.photo.photo_big == null) ? null : user.photo.photo_big;
            int i4 = i3;
            fileLocation2 = fileLocation3;
            i2 = i4;
        } else {
            Chat chat = (Chat) this.currentObject;
            if (chat == null || chat.photo == null || chat.photo.photo_big == null) {
                i2 = 0;
                fileLocation2 = null;
            } else {
                fileLocation2 = chat.photo.photo_big;
                i2 = 0;
            }
        }
        if (fileLocation2 == null || fileLocation2.local_id != fileLocation.local_id || fileLocation2.volume_id != fileLocation.volume_id || fileLocation2.dc_id != fileLocation.dc_id) {
            return null;
        }
        int[] iArr = new int[2];
        this.avatarImageView.getLocationInWindow(iArr);
        PlaceProviderObject placeProviderObject = new PlaceProviderObject();
        placeProviderObject.viewX = iArr[0];
        placeProviderObject.viewY = iArr[1] - AndroidUtilities.statusBarHeight;
        placeProviderObject.parentView = this.avatarImageView;
        placeProviderObject.imageReceiver = this.avatarImageView.getImageReceiver();
        placeProviderObject.dialogId = i2;
        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
        placeProviderObject.size = -1;
        placeProviderObject.radius = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
        return placeProviderObject;
    }

    public int getSelectedCount() {
        return 0;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        return null;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
    }

    public void setAvatarRadius(int i) {
        this.radius = i;
    }

    public void setCheckDisabled(boolean z) {
        if (this.checkBoxBig != null) {
            this.checkBoxBig.setDisabled(z);
        }
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox != null) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
        } else if (this.checkBoxBig != null) {
            if (this.checkBoxBig.getVisibility() != 0) {
                this.checkBoxBig.setVisibility(0);
            }
            this.checkBoxBig.setChecked(z, z2);
        }
    }

    public void setData(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, int i) {
        if (tLObject == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.statusTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currrntStatus = charSequence2;
        this.currentName = charSequence;
        this.currentObject = tLObject;
        this.currentDrawable = i;
        update(0);
    }

    public void setImageDrawable(Drawable drawable) {
        this.curDrawable = drawable;
    }

    public void setIsAdmin(int i) {
        if (this.adminImage != null) {
            this.adminImage.setVisibility(i != 0 ? 0 : 8);
            SimpleTextView simpleTextView = this.nameTextView;
            int dp = (!LocaleController.isRTL || i == 0) ? 0 : AndroidUtilities.dp(16.0f);
            int dp2 = (LocaleController.isRTL || i == 0) ? 0 : AndroidUtilities.dp(16.0f);
            simpleTextView.setPadding(dp, 0, dp2, 0);
            if (i == 1) {
                this.adminImage.setImageResource(C0338R.drawable.admin_star);
            } else if (i == 2) {
                this.adminImage.setImageResource(C0338R.drawable.admin_star2);
            }
        }
    }

    public void setNameColor(int i) {
        this.nameColor = i;
    }

    public void setPhotoChecked(int i) {
    }

    public void setStatusColor(int i) {
        this.statusColor = i;
    }

    public void setStatusColors(int i, int i2) {
        this.statusColor = i;
        this.statusOnlineColor = i2;
    }

    public void setStatusSize(int i) {
        this.statusTextView.setTextSize(i);
    }

    public void update(int i) {
        int i2 = 1;
        if (this.currentObject != null) {
            TLObject tLObject;
            User user;
            Chat chat;
            String str;
            if (this.currentObject instanceof User) {
                User user2 = (User) this.currentObject;
                if (user2.photo != null) {
                    tLObject = user2.photo.photo_small;
                    user = user2;
                    chat = null;
                } else {
                    user = user2;
                    tLObject = null;
                    chat = null;
                }
            } else {
                chat = (Chat) this.currentObject;
                if (chat.photo != null) {
                    tLObject = chat.photo.photo_small;
                    user = null;
                } else {
                    user = null;
                    tLObject = null;
                }
            }
            initTheme();
            if (i != 0) {
                int i3 = ((i & 2) == 0 || ((this.lastAvatar == null || tLObject != null) && (this.lastAvatar != null || tLObject == null || this.lastAvatar == null || tLObject == null || (this.lastAvatar.volume_id == tLObject.volume_id && this.lastAvatar.local_id == tLObject.local_id)))) ? 0 : 1;
                if (!(user == null || i3 != 0 || (i & 4) == 0)) {
                    if ((user.status != null ? user.status.expires : 0) != this.lastStatus) {
                        i3 = 1;
                    }
                }
                if (i3 != 0 || this.currentName != null || this.lastName == null || (i & 1) == 0) {
                    i2 = i3;
                    str = null;
                } else {
                    str = user != null ? UserObject.getUserName(user) : chat.title;
                    if (str.equals(this.lastName)) {
                        i2 = i3;
                    }
                }
                if (i2 == 0) {
                    return;
                }
            }
            str = null;
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                if (user.status != null) {
                    this.lastStatus = user.status.expires;
                } else {
                    this.lastStatus = 0;
                }
            } else {
                this.avatarDrawable.setInfo(chat);
            }
            if (this.currentName != null) {
                this.lastName = null;
                this.nameTextView.setText(this.currentName);
            } else {
                if (user != null) {
                    this.lastName = str == null ? UserObject.getUserName(user) : str;
                } else {
                    if (str == null) {
                        str = chat.title;
                    }
                    this.lastName = str;
                }
                this.nameTextView.setText(this.lastName);
            }
            if (this.currrntStatus != null) {
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(this.currrntStatus);
            } else if (user != null) {
                if (user.bot) {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (user.bot_chat_history || (this.adminImage != null && this.adminImage.getVisibility() == 0)) {
                        this.statusTextView.setText(LocaleController.getString("BotStatusRead", C0338R.string.BotStatusRead));
                    } else {
                        this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", C0338R.string.BotStatusCantRead));
                    }
                } else if (user.id == UserConfig.getClientUserId() || ((user.status != null && user.status.expires > ConnectionsManager.getInstance().getCurrentTime()) || MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(user.id)))) {
                    this.statusTextView.setTextColor(this.statusOnlineColor);
                    this.statusTextView.setText(LocaleController.getString("Online", C0338R.string.Online));
                } else {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(LocaleController.formatUserStatus(user));
                }
            }
            if ((this.imageView.getVisibility() == 0 && this.currentDrawable == 0) || (this.imageView.getVisibility() == 8 && this.currentDrawable != 0)) {
                this.imageView.setVisibility(this.currentDrawable == 0 ? 8 : 0);
                this.imageView.setImageResource(this.currentDrawable);
            }
            if (ThemeUtil.m2490b()) {
                if (this.curDrawable != null) {
                    this.imageView.setImageDrawable(this.curDrawable);
                }
                this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp((float) this.radius));
                this.avatarDrawable.setRadius(AndroidUtilities.dp((float) this.radius));
            }
            this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
            if (user != null && user.mutual_contact && MoboConstants.f1336c) {
                this.mutualImageView.setVisibility(0);
            } else {
                this.mutualImageView.setVisibility(8);
            }
            if (user != null && user.id == UserConfig.getClientUserId() && MoboConstants.f1338e) {
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(LocaleController.getString("Hidden", C0338R.string.Hidden));
            }
        }
    }

    public void updatePhotoAtIndex(int i) {
    }

    public void willHidePhotoViewer() {
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}
