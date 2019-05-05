package com.hanista.mobogram.telegraph.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.telegraph.model.UpdateModel;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.SimpleTextView;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.LaunchActivity;
import com.hanista.mobogram.ui.PhotoViewer;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import com.hanista.mobogram.ui.ProfileActivity;
import java.util.Calendar;
import java.util.Date;

public class UpdateCell extends FrameLayout implements PhotoViewerProvider {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox checkBox;
    private User currentUser;
    private SimpleTextView dateTextView;
    private ImageView imageView;
    private String lastName;
    private SimpleTextView nameTextView;
    private int newValueColor;
    private SimpleTextView newValueTextView;
    private int oldValueColor;
    private SimpleTextView oldValueTextView;
    private ImageView optionsButton;
    private UpdateModel updateModel;

    /* renamed from: com.hanista.mobogram.telegraph.ui.UpdateCell.1 */
    class C09331 implements OnClickListener {
        C09331() {
        }

        public void onClick(View view) {
            if (UpdateCell.this.currentUser != null) {
                User access$000 = UpdateCell.this.currentUser;
                if (MoboConstants.aB == 2) {
                    FileLocation fileLocation = null;
                    if (UpdateCell.this.updateModel.getPhotoBig() != null) {
                        fileLocation = UpdateCell.this.updateModel.getPhotoBig();
                    } else if (!(access$000.photo == null || access$000.photo.photo_big == null)) {
                        fileLocation = access$000.photo.photo_big;
                    }
                    if (fileLocation != null) {
                        PhotoViewer.getInstance().openPhoto(fileLocation, UpdateCell.this);
                    }
                } else if (MoboConstants.aB == 1 && PhotoViewer.getInstance().getParentActivity() != null && (PhotoViewer.getInstance().getParentActivity() instanceof LaunchActivity)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", access$000.id);
                    ((LaunchActivity) PhotoViewer.getInstance().getParentActivity()).presentFragment(new ProfileActivity(bundle));
                }
            }
        }
    }

    @SuppressLint({"RtlHardcoded"})
    public UpdateCell(Context context, int i) {
        int i2 = 3;
        super(context);
        this.currentUser = null;
        this.lastName = null;
        this.oldValueColor = -5723992;
        this.newValueColor = -12876608;
        this.avatarDrawable = new AvatarDrawable();
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        boolean z = LocaleController.isRTL;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (z ? 5 : 3) | 48, z ? 0.0f : (float) (i + 7), 8.0f, z ? (float) (i + 7) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((z ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (z ? 5 : 3) | 48, z ? 28.0f : (float) (i + 68), 11.5f, z ? (float) (i + 68) : 28.0f, 0.0f));
        this.oldValueTextView = new SimpleTextView(context);
        this.oldValueTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.oldValueTextView.setTextSize(14);
        this.oldValueTextView.setGravity((z ? 5 : 3) | 48);
        addView(this.oldValueTextView, LayoutHelper.createFrame(-1, 20.0f, (z ? 5 : 3) | 48, z ? 28.0f : (float) (i + 68), 62.5f, z ? (float) (i + 68) : 28.0f, 0.0f));
        this.newValueTextView = new SimpleTextView(context);
        this.newValueTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.newValueTextView.setTextSize(14);
        this.newValueTextView.setGravity((z ? 5 : 3) | 48);
        addView(this.newValueTextView, LayoutHelper.createFrame(-1, 20.0f, (z ? 5 : 3) | 48, z ? 28.0f : (float) (i + 68), 40.5f, z ? (float) (i + 68) : 28.0f, 0.0f));
        this.dateTextView = new SimpleTextView(context);
        this.dateTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.dateTextView.setTextSize(14);
        this.dateTextView.setGravity((z ? 3 : 5) | 48);
        addView(this.dateTextView, LayoutHelper.createFrame(-1, 20.0f, (z ? 3 : 5) | 48, z ? (float) (i + 5) : 28.0f, 80.5f, z ? 28.0f : (float) (i + 10), 0.0f));
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (z ? 5 : 3) | 16, z ? 0.0f : 16.0f, 0.0f, z ? 16.0f : 0.0f, 0.0f));
        this.checkBox = new CheckBox(context, C0338R.drawable.round_check2);
        this.checkBox.setVisibility(4);
        addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (z ? 5 : 3) | 48, z ? 0.0f : (float) (i + 37), 38.0f, z ? (float) (i + 37) : 0.0f, 0.0f));
        this.optionsButton = new ImageView(context);
        this.optionsButton.setFocusable(false);
        this.optionsButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR));
        this.optionsButton.setImageResource(C0338R.drawable.doc_actions_b);
        this.optionsButton.setScaleType(ScaleType.CENTER);
        View view = this.optionsButton;
        if (!LocaleController.isRTL) {
            i2 = 5;
        }
        addView(view, LayoutHelper.createFrame(40, 40, i2 | 48));
        initOnAvatarClickListener();
    }

    private void initOnAvatarClickListener() {
        if (MoboConstants.aB != 0) {
            this.avatarImageView.setOnClickListener(new C09331());
        }
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            this.nameTextView.setTextColor(AdvanceTheme.aR);
            this.nameTextView.setTextSize(AdvanceTheme.aX);
            this.newValueTextView.setTextSize(AdvanceTheme.aZ);
            this.newValueTextView.setTextColor(AdvanceTheme.aQ);
            this.oldValueTextView.setTextSize(AdvanceTheme.aZ);
            this.oldValueTextView.setTextColor(AdvanceTheme.aP);
            this.dateTextView.setTextSize(AdvanceTheme.aZ);
            this.dateTextView.setTextColor(AdvanceTheme.aP);
            this.avatarImageView.setRoundRadius(AndroidUtilities.dp((float) AdvanceTheme.aY));
        }
    }

    public boolean allowCaption() {
        return true;
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public BackupImageView getAvatarImageView() {
        return this.avatarImageView;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        if (fileLocation == null || this.currentUser == null) {
            return null;
        }
        int i2 = this.currentUser.id;
        FileLocation photoBig = (this.updateModel == null || this.updateModel.getPhotoBig() == null) ? (this.currentUser == null || this.currentUser.photo == null || this.currentUser.photo.photo_big == null) ? null : this.currentUser.photo.photo_big : this.updateModel.getPhotoBig();
        if (photoBig == null || photoBig.local_id != fileLocation.local_id || photoBig.volume_id != fileLocation.volume_id || photoBig.dc_id != fileLocation.dc_id) {
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

    public UpdateModel getUpdateModel() {
        return this.updateModel;
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(104.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
    }

    public void setData(UpdateModel updateModel) {
        User user = MessagesController.getInstance().getUser(Integer.valueOf(updateModel.getUserId()));
        if (user == null) {
            this.nameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.avatarImageView.setImageDrawable(null);
        }
        this.currentUser = user;
        this.updateModel = updateModel;
        update();
    }

    public void setOnOptionsClick(OnClickListener onClickListener) {
        this.optionsButton.setOnClickListener(onClickListener);
    }

    public void setPhotoChecked(int i) {
    }

    public void update() {
        if (this.currentUser != null) {
            TLObject tLObject = null;
            if (this.updateModel != null && this.updateModel.getPhotoSmall() != null) {
                tLObject = this.updateModel.getPhotoSmall();
            } else if (this.currentUser.photo != null) {
                tLObject = this.currentUser.photo.photo_small;
            }
            this.avatarDrawable.setInfo(this.currentUser);
            this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
            this.lastName = ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name);
            this.nameTextView.setText(this.lastName);
        } else {
            this.nameTextView.setText(LocaleController.getString("HiddenName", C0338R.string.HiddenName));
        }
        this.oldValueTextView.setTextColor(this.oldValueColor);
        this.newValueTextView.setTextColor(this.newValueColor);
        if (this.updateModel.getType() == 1) {
            this.oldValueTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            if (this.updateModel.getNewValue().equals("1")) {
                this.newValueTextView.setText(getContext().getString(C0338R.string.get_online));
            } else {
                this.newValueTextView.setText(getContext().getString(C0338R.string.get_offline));
            }
        } else if (this.updateModel.getType() == 2) {
            this.oldValueTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.newValueTextView.setText(getContext().getString(C0338R.string.new_name) + " " + this.updateModel.getNewValue().replace(";;;", " - "));
        } else if (this.updateModel.getType() == 3) {
            this.oldValueTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            if (this.updateModel.getNewValue() == null) {
                this.newValueTextView.setText(getContext().getString(C0338R.string.changed_photo));
            } else if (this.updateModel.getNewValue().equals("0")) {
                this.newValueTextView.setText(getContext().getString(C0338R.string.removed_all_photos));
            } else if (this.updateModel.getNewValue().equals("1")) {
                this.newValueTextView.setText(getContext().getString(C0338R.string.removed_photo));
            } else {
                this.newValueTextView.setText(getContext().getString(C0338R.string.added_photo));
            }
        } else if (this.updateModel.getType() == 4) {
            this.oldValueTextView.setText(getContext().getString(C0338R.string.old_phone) + " " + this.updateModel.getOldValue());
            this.newValueTextView.setText(getContext().getString(C0338R.string.new_phone) + " " + this.updateModel.getNewValue());
        } else if (this.updateModel.getType() == 5) {
            this.oldValueTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            if (this.updateModel.getNewValue().equals("1")) {
                this.newValueTextView.setText(getContext().getString(C0338R.string.added_you));
            } else {
                this.newValueTextView.setText(getContext().getString(C0338R.string.removed_you));
            }
        }
        Long valueOf = Long.valueOf(Long.parseLong(this.updateModel.getChangeDate()));
        if (valueOf.longValue() != 0) {
            Date date = new Date(valueOf.longValue());
            String a = MoboUtils.m1697a(date);
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            this.dateTextView.setText(a + " - " + MoboUtils.m1695a(instance.get(11), 2) + ":" + MoboUtils.m1695a(instance.get(12), 2));
        }
        initTheme();
    }

    public void updatePhotoAtIndex(int i) {
    }

    public void willHidePhotoViewer() {
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}
