package com.hanista.mobogram.ui.Components;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.MediaController.PhotoEntry;
import com.hanista.mobogram.messenger.MediaController.SearchImage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.InputDocument;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.LaunchActivity;
import com.hanista.mobogram.ui.PhotoAlbumPickerActivity;
import com.hanista.mobogram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import com.hanista.mobogram.ui.PhotoCropActivity;
import com.hanista.mobogram.ui.PhotoCropActivity.PhotoEditActivityDelegate;
import com.hanista.mobogram.ui.PhotoViewer;
import com.hanista.mobogram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import java.io.File;
import java.util.ArrayList;

public class AvatarUpdater implements NotificationCenterDelegate, PhotoEditActivityDelegate {
    private PhotoSize bigPhoto;
    private boolean clearAfterUpdate;
    public String currentPicturePath;
    public AvatarUpdaterDelegate delegate;
    public BaseFragment parentFragment;
    File picturePath;
    public boolean returnOnly;
    private PhotoSize smallPhoto;
    public String uploadingAvatar;

    public interface AvatarUpdaterDelegate {
        void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2);
    }

    /* renamed from: com.hanista.mobogram.ui.Components.AvatarUpdater.1 */
    class C12951 implements PhotoAlbumPickerActivityDelegate {
        C12951() {
        }

        public void didSelectPhotos(ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<ArrayList<InputDocument>> arrayList3, ArrayList<SearchImage> arrayList4) {
            if (!arrayList.isEmpty()) {
                AvatarUpdater.this.processBitmap(ImageLoader.loadBitmap((String) arrayList.get(0), null, 800.0f, 800.0f, true));
            }
        }

        public boolean didSelectVideo(String str) {
            return true;
        }

        public void startPhotoSelectActivity() {
            try {
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                AvatarUpdater.this.parentFragment.startActivityForResult(intent, 14);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.AvatarUpdater.2 */
    class C12962 extends EmptyPhotoViewerProvider {
        final /* synthetic */ ArrayList val$arrayList;

        C12962(ArrayList arrayList) {
            this.val$arrayList = arrayList;
        }

        public boolean allowCaption() {
            return false;
        }

        public void sendButtonPressed(int i) {
            PhotoEntry photoEntry = (PhotoEntry) this.val$arrayList.get(0);
            String str = photoEntry.imagePath != null ? photoEntry.imagePath : photoEntry.path != null ? photoEntry.path : null;
            AvatarUpdater.this.processBitmap(ImageLoader.loadBitmap(str, null, 800.0f, 800.0f, true));
        }
    }

    public AvatarUpdater() {
        this.uploadingAvatar = null;
        this.picturePath = null;
        this.parentFragment = null;
        this.clearAfterUpdate = false;
        this.returnOnly = false;
    }

    private void processBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.smallPhoto = ImageLoader.scaleAndSaveImage(bitmap, 100.0f, 100.0f, 80, false);
            this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
            bitmap.recycle();
            if (this.bigPhoto != null && this.smallPhoto != null) {
                if (!this.returnOnly) {
                    UserConfig.saveConfig(false);
                    this.uploadingAvatar = FileLoader.getInstance().getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg";
                    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
                    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
                    FileLoader.getInstance().uploadFile(this.uploadingAvatar, false, true);
                } else if (this.delegate != null) {
                    this.delegate.didUploadedPhoto(null, this.smallPhoto, this.bigPhoto);
                }
            }
        }
    }

    private void startCrop(String str, Uri uri) {
        try {
            LaunchActivity launchActivity = (LaunchActivity) this.parentFragment.getParentActivity();
            if (launchActivity != null) {
                Bundle bundle = new Bundle();
                if (str != null) {
                    bundle.putString("photoPath", str);
                } else if (uri != null) {
                    bundle.putParcelable("photoUri", uri);
                }
                BaseFragment photoCropActivity = new PhotoCropActivity(bundle);
                photoCropActivity.setDelegate(this);
                launchActivity.presentFragment(photoCropActivity);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            processBitmap(ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true));
        }
    }

    public void clear() {
        if (this.uploadingAvatar != null) {
            this.clearAfterUpdate = true;
            return;
        }
        this.parentFragment = null;
        this.delegate = null;
    }

    public void didFinishEdit(Bitmap bitmap) {
        processBitmap(bitmap);
    }

    public void didReceivedNotification(int i, Object... objArr) {
        String str;
        if (i == NotificationCenter.FileDidUpload) {
            str = (String) objArr[0];
            if (this.uploadingAvatar != null && str.equals(this.uploadingAvatar)) {
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailUpload);
                if (this.delegate != null) {
                    this.delegate.didUploadedPhoto((InputFile) objArr[1], this.smallPhoto, this.bigPhoto);
                }
                this.uploadingAvatar = null;
                if (this.clearAfterUpdate) {
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        } else if (i == NotificationCenter.FileDidFailUpload) {
            str = (String) objArr[0];
            if (this.uploadingAvatar != null && str.equals(this.uploadingAvatar)) {
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailUpload);
                this.uploadingAvatar = null;
                if (this.clearAfterUpdate) {
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 != -1) {
            return;
        }
        if (i == 13) {
            int i3;
            PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
            try {
                int i4;
                switch (new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", 1)) {
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        i4 = 180;
                        break;
                    case Method.TRACE /*6*/:
                        i4 = 90;
                        break;
                    case TLRPC.USER_FLAG_USERNAME /*8*/:
                        i4 = 270;
                        break;
                    default:
                        i4 = 0;
                        break;
                }
                i3 = i4;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                i3 = 0;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(new PhotoEntry(0, 0, 0, this.currentPicturePath, i3, false));
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, new C12962(arrayList), null);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        } else if (i == 14 && intent != null && intent.getData() != null) {
            startCrop(null, intent.getData());
        }
    }

    public void openCamera() {
        try {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            File generatePicturePath = AndroidUtilities.generatePicturePath();
            if (generatePicturePath != null) {
                if (VERSION.SDK_INT >= 24) {
                    intent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "com.hanista.mobogram.provider", generatePicturePath));
                    intent.addFlags(2);
                    intent.addFlags(1);
                } else {
                    intent.putExtra("output", Uri.fromFile(generatePicturePath));
                }
                this.currentPicturePath = generatePicturePath.getAbsolutePath();
            }
            this.parentFragment.startActivityForResult(intent, 13);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void openGallery() {
        if (VERSION.SDK_INT < 23 || this.parentFragment == null || this.parentFragment.getParentActivity() == null || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            BaseFragment photoAlbumPickerActivity = new PhotoAlbumPickerActivity(true, false, false, null);
            photoAlbumPickerActivity.setDelegate(new C12951());
            this.parentFragment.presentFragment(photoAlbumPickerActivity);
            return;
        }
        this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
    }
}
