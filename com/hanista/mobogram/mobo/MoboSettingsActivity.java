package com.hanista.mobogram.mobo;

import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.lock.LockActivity;
import com.hanista.mobogram.mobo.notif.NotificationBarSettingsActivity;
import com.hanista.mobogram.mobo.p000a.ArchiveSettingsActivity;
import com.hanista.mobogram.mobo.p001b.CategorySettingsActivity;
import com.hanista.mobogram.mobo.p002c.ChatsBarSettingsActivity;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p010k.MaterialHelperUtil;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p015o.MenuSettingsActivity;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_photos_photo;
import com.hanista.mobogram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_userProfilePhoto;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailMultiLineCell;
import com.hanista.mobogram.ui.Cells.TextDetailSettingsCell;
import com.hanista.mobogram.ui.Cells.TextInfoCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.AvatarUpdater;
import com.hanista.mobogram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.DocumentSelectActivity;
import com.hanista.mobogram.ui.DocumentSelectActivity.DocumentSelectActivityDelegate;
import com.hanista.mobogram.ui.LaunchActivity;
import com.hanista.mobogram.ui.PasscodeActivity;
import com.hanista.mobogram.ui.PhotoViewer;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/* renamed from: com.hanista.mobogram.mobo.l */
public class MoboSettingsActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private int f1404A;
    private int f1405B;
    private int f1406C;
    private int f1407D;
    private int f1408E;
    private int f1409F;
    private int f1410G;
    private int f1411H;
    private int f1412I;
    private int f1413J;
    private int f1414K;
    private int f1415L;
    private int f1416M;
    private int f1417N;
    private int f1418O;
    private int f1419P;
    private int f1420Q;
    private ListView f1421a;
    private MoboSettingsActivity f1422b;
    private BackupImageView f1423c;
    private TextView f1424d;
    private TextView f1425e;
    private AvatarUpdater f1426f;
    private View f1427g;
    private View f1428h;
    private int f1429i;
    private int f1430j;
    private int f1431k;
    private int f1432l;
    private int f1433m;
    private int f1434n;
    private int f1435o;
    private int f1436p;
    private int f1437q;
    private int f1438r;
    private int f1439s;
    private int f1440t;
    private int f1441u;
    private int f1442v;
    private int f1443w;
    private int f1444x;
    private int f1445y;
    private int f1446z;

    /* renamed from: com.hanista.mobogram.mobo.l.1 */
    class MoboSettingsActivity implements AvatarUpdaterDelegate {
        final /* synthetic */ MoboSettingsActivity f1370a;

        /* renamed from: com.hanista.mobogram.mobo.l.1.1 */
        class MoboSettingsActivity implements RequestDelegate {
            final /* synthetic */ MoboSettingsActivity f1361a;

            /* renamed from: com.hanista.mobogram.mobo.l.1.1.1 */
            class MoboSettingsActivity implements Runnable {
                final /* synthetic */ MoboSettingsActivity f1360a;

                MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
                    this.f1360a = moboSettingsActivity;
                }

                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                    UserConfig.saveConfig(true);
                }
            }

            MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
                this.f1361a = moboSettingsActivity;
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                    if (user == null) {
                        user = UserConfig.getCurrentUser();
                        if (user != null) {
                            MessagesController.getInstance().putUser(user, false);
                        } else {
                            return;
                        }
                    }
                    UserConfig.setCurrentUser(user);
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
                    AndroidUtilities.runOnUIThread(new MoboSettingsActivity(this));
                }
            }
        }

        MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
            this.f1370a = moboSettingsActivity;
        }

        public void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
            TLObject tL_photos_uploadProfilePhoto = new TL_photos_uploadProfilePhoto();
            tL_photos_uploadProfilePhoto.file = inputFile;
            ConnectionsManager.getInstance().sendRequest(tL_photos_uploadProfilePhoto, new MoboSettingsActivity(this));
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.l.a */
    interface MoboSettingsActivity {
        void m1385a(String str);
    }

    /* renamed from: com.hanista.mobogram.mobo.l.2 */
    class MoboSettingsActivity implements MoboSettingsActivity {
        final /* synthetic */ MoboSettingsActivity f1371a;

        MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
            this.f1371a = moboSettingsActivity;
        }

        public void m1386a(String str) {
            File file = new File(new File(MoboConstants.m1381b(), MoboConstants.f1325R), "Backups");
            if (!file.exists()) {
                file.mkdirs();
            }
            File file2 = new File(file, str + ".mgs");
            file2.delete();
            if (this.f1371a.m1433a(file2)) {
                Toast.makeText(this.f1371a.getParentActivity(), LocaleController.formatString("BackupFileSavedTo", C0338R.string.BackupFileSavedTo, file2.getAbsolutePath()), 1).show();
                return;
            }
            Toast.makeText(this.f1371a.getParentActivity(), LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred), 1).show();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.l.3 */
    class MoboSettingsActivity implements MoboSettingsActivity {
        final /* synthetic */ MoboSettingsActivity f1372a;

        MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
            this.f1372a = moboSettingsActivity;
        }

        public void m1387a(String str) {
            File databasePath = ApplicationLoader.applicationContext.getDatabasePath(MoboUtils.m1712c(ApplicationLoader.applicationContext) + ".db");
            File file = new File(new File(MoboConstants.m1381b(), MoboConstants.f1325R), "Backups");
            if (!file.exists()) {
                file.mkdirs();
            }
            if (MoboUtils.m1704a(databasePath, new File(file, str + ".mgd"))) {
                Toast.makeText(this.f1372a.getParentActivity(), LocaleController.formatString("BackupFileSavedTo", C0338R.string.BackupFileSavedTo, r1.getAbsolutePath()), 1).show();
                return;
            }
            Toast.makeText(this.f1372a.getParentActivity(), LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred), 1).show();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.l.4 */
    class MoboSettingsActivity implements MoboSettingsActivity {
        final /* synthetic */ MoboSettingsActivity f1373a;

        MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
            this.f1373a = moboSettingsActivity;
        }

        public void m1388a(String str) {
            File databasePath = ApplicationLoader.applicationContext.getDatabasePath(MoboUtils.m1712c(ApplicationLoader.applicationContext) + ".db");
            File file = new File(new File(MoboConstants.m1381b(), MoboConstants.f1325R), "Backups");
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(new File(file, str + ".mga"))));
                file = File.createTempFile("mobo", ".mgs", ApplicationLoader.applicationContext.getCacheDir());
                if (this.f1373a.m1433a(file)) {
                    this.f1373a.m1431a(zipOutputStream, file);
                    this.f1373a.m1431a(zipOutputStream, databasePath);
                    zipOutputStream.close();
                    Toast.makeText(this.f1373a.getParentActivity(), LocaleController.formatString("BackupFileSavedTo", C0338R.string.BackupFileSavedTo, r1.getAbsolutePath()), 1).show();
                    return;
                }
                Toast.makeText(this.f1373a.getParentActivity(), LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred), 1).show();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.l.5 */
    class MoboSettingsActivity implements OnClickListener {
        final /* synthetic */ EditText f1374a;
        final /* synthetic */ MoboSettingsActivity f1375b;
        final /* synthetic */ MoboSettingsActivity f1376c;

        MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity, EditText editText, MoboSettingsActivity moboSettingsActivity2) {
            this.f1376c = moboSettingsActivity;
            this.f1374a = editText;
            this.f1375b = moboSettingsActivity2;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            String obj = this.f1374a.getText().toString();
            if (obj.length() > 0) {
                this.f1375b.m1385a(obj);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.l.6 */
    class MoboSettingsActivity implements DocumentSelectActivityDelegate {
        final /* synthetic */ MoboSettingsActivity f1383a;

        /* renamed from: com.hanista.mobogram.mobo.l.6.1 */
        class MoboSettingsActivity implements OnClickListener {
            final /* synthetic */ String f1380a;
            final /* synthetic */ File f1381b;
            final /* synthetic */ MoboSettingsActivity f1382c;

            /* renamed from: com.hanista.mobogram.mobo.l.6.1.1 */
            class MoboSettingsActivity implements Runnable {
                final /* synthetic */ MoboSettingsActivity f1379a;

                /* renamed from: com.hanista.mobogram.mobo.l.6.1.1.1 */
                class MoboSettingsActivity implements OnClickListener {
                    final /* synthetic */ MoboSettingsActivity f1377a;

                    MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
                        this.f1377a = moboSettingsActivity;
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        this.f1377a.f1379a.f1382c.f1383a.m1443e();
                    }
                }

                /* renamed from: com.hanista.mobogram.mobo.l.6.1.1.2 */
                class MoboSettingsActivity implements OnCancelListener {
                    final /* synthetic */ MoboSettingsActivity f1378a;

                    MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
                        this.f1378a = moboSettingsActivity;
                    }

                    public void onCancel(DialogInterface dialogInterface) {
                        this.f1378a.f1379a.f1382c.f1383a.m1443e();
                    }
                }

                MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
                    this.f1379a = moboSettingsActivity;
                }

                public void run() {
                    boolean z = false;
                    if (this.f1379a.f1380a.equals("mgs")) {
                        z = this.f1379a.f1382c.f1383a.m1437b(this.f1379a.f1381b);
                    } else if (this.f1379a.f1380a.equals("mgd")) {
                        try {
                            SQLiteDatabase.openDatabase(this.f1379a.f1381b.getAbsolutePath(), null, 0).close();
                            MoboUtils.m1704a(this.f1379a.f1381b, ApplicationLoader.applicationContext.getDatabasePath(MoboUtils.m1712c(ApplicationLoader.applicationContext) + ".db"));
                            z = true;
                        } catch (Exception e) {
                        }
                    } else if (this.f1379a.f1380a.equals("mga")) {
                        try {
                            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(this.f1379a.f1381b));
                            boolean z2 = false;
                            while (true) {
                                ZipEntry nextEntry = zipInputStream.getNextEntry();
                                if (nextEntry == null) {
                                    break;
                                } else if (nextEntry.getName().endsWith(".mgs")) {
                                    r2 = File.createTempFile("mobo", "mgs", ApplicationLoader.applicationContext.getCacheDir());
                                    r4 = new FileOutputStream(r2);
                                    r5 = new byte[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
                                    while (true) {
                                        r6 = zipInputStream.read(r5, 0, TLRPC.MESSAGE_FLAG_HAS_VIEWS);
                                        if (r6 == -1) {
                                            break;
                                        }
                                        r4.write(r5, 0, r6);
                                    }
                                    zipInputStream.closeEntry();
                                    r4.close();
                                    z2 = this.f1379a.f1382c.f1383a.m1437b(r2);
                                } else if (nextEntry.getName().endsWith(".db")) {
                                    r2 = File.createTempFile("mobo", ".db", ApplicationLoader.applicationContext.getCacheDir());
                                    r4 = new FileOutputStream(r2);
                                    r5 = new byte[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
                                    while (true) {
                                        r6 = zipInputStream.read(r5, 0, TLRPC.MESSAGE_FLAG_HAS_VIEWS);
                                        if (r6 == -1) {
                                            break;
                                        }
                                        r4.write(r5, 0, r6);
                                    }
                                    zipInputStream.closeEntry();
                                    r4.close();
                                    SQLiteDatabase.openDatabase(r2.getAbsolutePath(), null, 0).close();
                                    MoboUtils.m1704a(r2, ApplicationLoader.applicationContext.getDatabasePath(MoboUtils.m1712c(ApplicationLoader.applicationContext) + ".db"));
                                    z2 = true;
                                }
                            }
                            z = z2;
                        } catch (Exception e2) {
                        }
                    } else {
                        Toast.makeText(this.f1379a.f1382c.f1383a.getParentActivity(), LocaleController.getString("SelectedFileIsNotBackupFile", C0338R.string.SelectedFileIsNotBackupFile), 1).show();
                        return;
                    }
                    if (z) {
                        Builder builder = new Builder(this.f1379a.f1382c.f1383a.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("BackupFileRestoredSuccessfully", C0338R.string.BackupFileRestoredSuccessfully));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new MoboSettingsActivity(this));
                        builder.setOnCancelListener(new MoboSettingsActivity(this));
                        builder.create().show();
                        return;
                    }
                    Toast.makeText(this.f1379a.f1382c.f1383a.getParentActivity(), LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred), 1).show();
                }
            }

            MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity, String str, File file) {
                this.f1382c = moboSettingsActivity;
                this.f1380a = str;
                this.f1381b = file;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                AndroidUtilities.runOnUIThread(new MoboSettingsActivity(this));
            }
        }

        MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
            this.f1383a = moboSettingsActivity;
        }

        public void didSelectFiles(DocumentSelectActivity documentSelectActivity, ArrayList<String> arrayList) {
            File file = new File((String) arrayList.get(0));
            Builder builder = new Builder(this.f1383a.getParentActivity());
            String substring = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
            builder.setTitle(LocaleController.getString("Restore", C0338R.string.Restore));
            if (substring.equals("mgs")) {
                builder.setMessage(LocaleController.getString("AreYouSureRestoreMoboSettings", C0338R.string.AreYouSureRestoreMoboSettings));
            } else if (substring.equals("mgd")) {
                builder.setMessage(LocaleController.getString("AreYouSureRestoreMoboData", C0338R.string.AreYouSureRestoreMoboData));
            } else if (substring.equals("mga")) {
                builder.setMessage(LocaleController.getString("AreYouSureRestoreAll", C0338R.string.AreYouSureRestoreAll));
            } else {
                builder.setMessage(LocaleController.getString("SelectedFileIsNotBackupFile", C0338R.string.SelectedFileIsNotBackupFile));
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new MoboSettingsActivity(this, substring, file));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            this.f1383a.showDialog(builder.create());
        }

        public void startDocumentSelectActivity() {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.l.7 */
    class MoboSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ MoboSettingsActivity f1384a;

        MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
            this.f1384a = moboSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f1384a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.l.8 */
    class MoboSettingsActivity extends FrameLayout {
        final /* synthetic */ MoboSettingsActivity f1385a;

        MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity, Context context) {
            this.f1385a = moboSettingsActivity;
            super(context);
        }

        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view != this.f1385a.f1421a) {
                return super.drawChild(canvas, view, j);
            }
            boolean drawChild = super.drawChild(canvas, view, j);
            if (this.f1385a.parentLayout != null) {
                int childCount = getChildCount();
                int i = 0;
                while (i < childCount) {
                    View childAt = getChildAt(i);
                    if (childAt != view && (childAt instanceof ActionBar) && childAt.getVisibility() == 0) {
                        if (((ActionBar) childAt).getCastShadows()) {
                            i = childAt.getMeasuredHeight();
                            this.f1385a.parentLayout.drawHeaderShadow(canvas, i);
                        }
                        i = 0;
                        this.f1385a.parentLayout.drawHeaderShadow(canvas, i);
                    } else {
                        i++;
                    }
                }
                i = 0;
                this.f1385a.parentLayout.drawHeaderShadow(canvas, i);
            }
            return drawChild;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.l.9 */
    class MoboSettingsActivity implements OnItemClickListener {
        final /* synthetic */ MoboSettingsActivity f1389a;

        /* renamed from: com.hanista.mobogram.mobo.l.9.1 */
        class MoboSettingsActivity implements OnClickListener {
            final /* synthetic */ EditText f1386a;
            final /* synthetic */ Editor f1387b;
            final /* synthetic */ MoboSettingsActivity f1388c;

            MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity, EditText editText, Editor editor) {
                this.f1388c = moboSettingsActivity;
                this.f1386a = editText;
                this.f1387b = editor;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                String obj = this.f1386a.getText().toString();
                if (obj.length() > 0) {
                    this.f1387b.putString("storage_folder_name", obj);
                    this.f1387b.commit();
                    MoboConstants.m1379a();
                    ImageLoader.getInstance().checkMediaPaths();
                }
            }
        }

        MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
            this.f1389a = moboSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            if (i == this.f1389a.f1445y) {
                if (HiddenConfig.f1399b.length() > 0) {
                    this.f1389a.presentFragment(new HiddenActivity(2));
                } else {
                    this.f1389a.presentFragment(new HiddenActivity(0));
                }
            }
            if (i == this.f1389a.f1433m) {
                this.f1389a.presentFragment(new TabSettingsActivity());
            } else if (i == this.f1389a.f1434n) {
                this.f1389a.presentFragment(new ViewSettingsActivity());
            } else if (i == this.f1389a.f1435o) {
                this.f1389a.presentFragment(new ChatSettingsActivity());
            } else if (i == this.f1389a.f1436p) {
                this.f1389a.presentFragment(new EmojiSettingsActivity());
            } else if (i == this.f1389a.f1437q) {
                this.f1389a.presentFragment(new ChatsBarSettingsActivity());
            } else if (i == this.f1389a.f1438r) {
                this.f1389a.presentFragment(new ForwardSettingsActivity());
            } else if (i == this.f1389a.f1439s) {
                this.f1389a.presentFragment(new CategorySettingsActivity());
            } else if (i == this.f1389a.f1440t) {
                this.f1389a.presentFragment(new ArchiveSettingsActivity());
            } else if (i == this.f1389a.f1441u) {
                this.f1389a.presentFragment(new NotificationBarSettingsActivity());
            } else if (i == this.f1389a.f1442v) {
                this.f1389a.presentFragment(new MenuSettingsActivity());
            } else if (i == this.f1389a.f1443w) {
                this.f1389a.presentFragment(new GhostModeSettingsActivity());
            } else if (i == this.f1389a.f1444x) {
                if (UserConfig.passcodeHash.length() <= 0) {
                    this.f1389a.presentFragment(new PasscodeActivity(0));
                } else if (UserConfig.passcodeType == 2) {
                    this.f1389a.presentFragment(new LockActivity(2));
                } else {
                    this.f1389a.presentFragment(new PasscodeActivity(2));
                }
            } else if (i == this.f1389a.f1407D) {
                r4 = MoboConstants.f1311D;
                edit.putBoolean("auto_sync_contacts", !r4);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    r9 = (TextDetailCheckCell) view;
                    if (r4) {
                        z = false;
                    }
                    r9.setChecked(z);
                }
                MoboConstants.m1379a();
                if (!r4) {
                    ContactsController.getInstance().checkContacts();
                    this.f1389a.m1441d();
                }
            } else if (i == this.f1389a.f1411H) {
                r4 = MoboConstants.aV;
                edit.putBoolean("block_ads", !r4);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    r9 = (TextDetailCheckCell) view;
                    if (r4) {
                        z = false;
                    }
                    r9.setChecked(z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f1389a.f1408E) {
                if (this.f1389a.getParentActivity() != null) {
                    this.f1389a.presentFragment(new StorageSelectActivity());
                } else {
                    return;
                }
            } else if (i == this.f1389a.f1409F) {
                if (this.f1389a.getParentActivity() != null) {
                    Builder builder = new Builder(this.f1389a.getParentActivity());
                    builder.setTitle(LocaleController.getString("StorageFolder", C0338R.string.StorageFolder));
                    View editText = new EditText(this.f1389a.getParentActivity());
                    if (VERSION.SDK_INT < 11) {
                        editText.setBackgroundResource(17301529);
                    }
                    editText.setTextSize(18.0f);
                    editText.setText(MoboConstants.f1325R);
                    editText.setImeOptions(6);
                    editText.setSingleLine(true);
                    builder.setView(editText);
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new MoboSettingsActivity(this, editText, edit));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    this.f1389a.showDialog(builder.create());
                    if (editText != null) {
                        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) editText.getLayoutParams();
                        if (marginLayoutParams != null) {
                            if (marginLayoutParams instanceof LayoutParams) {
                                ((LayoutParams) marginLayoutParams).gravity = 1;
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
                } else {
                    return;
                }
            } else if (i == this.f1389a.f1410G) {
                if (this.f1389a.getParentActivity() != null) {
                    new MoboSettingsActivity(this.f1389a).execute(new Void[0]);
                } else {
                    return;
                }
            } else if (i == this.f1389a.f1412I) {
                this.f1389a.presentFragment(new ReportHelpActivity());
            } else if (i == this.f1389a.f1415L) {
                this.f1389a.m1445f();
            } else if (i == this.f1389a.f1416M) {
                this.f1389a.m1447g();
            } else if (i == this.f1389a.f1417N) {
                this.f1389a.m1449h();
            } else if (i == this.f1389a.f1418O) {
                this.f1389a.m1451i();
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.l.b */
    class MoboSettingsActivity extends AsyncTask<Void, Void, Boolean> {
        int f1390a;
        long f1391b;
        ProgressDialog f1392c;
        final /* synthetic */ MoboSettingsActivity f1393d;

        MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity) {
            this.f1393d = moboSettingsActivity;
        }

        protected Boolean m1389a(Void... voidArr) {
            try {
                File[] listFiles = AndroidUtilities.getCacheDir().listFiles();
                this.f1390a = listFiles.length;
                for (File file : listFiles) {
                    this.f1391b += file.length();
                    file.delete();
                }
                Thread.sleep(2000);
                return Boolean.valueOf(true);
            } catch (Exception e) {
                return Boolean.valueOf(false);
            }
        }

        protected void m1390a(Boolean bool) {
            super.onPostExecute(bool);
            try {
                this.f1392c.dismiss();
            } catch (Exception e) {
            }
            if (!bool.booleanValue() || this.f1393d.getParentActivity() == null) {
                Toast.makeText(this.f1393d.getParentActivity(), LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred), 1).show();
                return;
            }
            long j = this.f1391b / 1024000;
            Toast.makeText(this.f1393d.getParentActivity(), LocaleController.formatString("CacheFolderCleanedSuccessfully", C0338R.string.CacheFolderCleanedSuccessfully, this.f1390a + TtmlNode.ANONYMOUS_REGION_ID, j + "MB"), 1).show();
        }

        protected /* synthetic */ Object doInBackground(Object[] objArr) {
            return m1389a((Void[]) objArr);
        }

        protected /* synthetic */ void onPostExecute(Object obj) {
            m1390a((Boolean) obj);
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.f1392c = new ProgressDialog(this.f1393d.getParentActivity());
            this.f1392c.setMessage(LocaleController.getString("PleaseWait", C0338R.string.PleaseWait));
            this.f1392c.setCanceledOnTouchOutside(false);
            this.f1392c.setCancelable(false);
            this.f1392c.show();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.l.c */
    private class MoboSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ MoboSettingsActivity f1394a;
        private Context f1395b;

        public MoboSettingsActivity(MoboSettingsActivity moboSettingsActivity, Context context) {
            this.f1394a = moboSettingsActivity;
            this.f1395b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f1394a.f1420Q;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == this.f1394a.f1430j ? 0 : (i == this.f1394a.f1410G || i == this.f1394a.f1445y || i == this.f1394a.f1433m || i == this.f1394a.f1408E || i == this.f1394a.f1409F) ? 2 : i == this.f1394a.f1419P ? 5 : (i == this.f1394a.f1432l || i == this.f1394a.f1404A || i == this.f1394a.f1406C || i == this.f1394a.f1414K) ? 4 : (i == this.f1394a.f1431k || i == this.f1394a.f1446z || i == this.f1394a.f1405B || i == this.f1394a.f1413J) ? 7 : (i == this.f1394a.f1407D || i == this.f1394a.f1411H) ? 8 : (i == this.f1394a.f1415L || i == this.f1394a.f1416M || i == this.f1394a.f1417N || i == this.f1394a.f1418O) ? 9 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View emptyCell;
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                emptyCell = view == null ? new EmptyCell(this.f1395b) : view;
                if (i == this.f1394a.f1430j) {
                    ((EmptyCell) emptyCell).setHeight(AndroidUtilities.dp(88.0f));
                } else {
                    ((EmptyCell) emptyCell).setHeight(AndroidUtilities.dp(16.0f));
                }
            } else {
                if (itemViewType == 1) {
                    if (view == null) {
                        emptyCell = new TextInfoPrivacyCell(this.f1395b);
                    }
                } else if (itemViewType == 2) {
                    emptyCell = view == null ? new TextSettingsCell(this.f1395b) : view;
                    TextSettingsCell textSettingsCell = (TextSettingsCell) emptyCell;
                    if (i == this.f1394a.f1408E) {
                        textSettingsCell.setText(LocaleController.getString("DefaultStorage", C0338R.string.DefaultStorage), true);
                    } else if (i == this.f1394a.f1409F) {
                        textSettingsCell.setText(LocaleController.getString("StorageFolder", C0338R.string.StorageFolder), true);
                    } else if (i == this.f1394a.f1410G) {
                        textSettingsCell.setText(LocaleController.getString("CleanCacheFolder", C0338R.string.CleanCacheFolder), true);
                    } else if (i == this.f1394a.f1445y) {
                        textSettingsCell.setText(LocaleController.getString("HideChatsSetting", C0338R.string.HideChatsSetting), true);
                    } else if (i == this.f1394a.f1433m) {
                        textSettingsCell.setText(LocaleController.getString("TabSettings", C0338R.string.TabSettings), true);
                    } else if (i == this.f1394a.f1434n) {
                        textSettingsCell.setText(LocaleController.getString("ViewSetting", C0338R.string.ViewSetting), true);
                    } else if (i == this.f1394a.f1435o) {
                        textSettingsCell.setText(LocaleController.getString("ChatsSettings", C0338R.string.ChatsSettings), true);
                    } else if (i == this.f1394a.f1436p) {
                        textSettingsCell.setText(LocaleController.getString("EmojiSettings", C0338R.string.EmojiSettings), true);
                    } else if (i == this.f1394a.f1437q) {
                        textSettingsCell.setText(LocaleController.getString("RecentChatsBarSettings", C0338R.string.RecentChatsBarSettings), true);
                    } else if (i == this.f1394a.f1438r) {
                        textSettingsCell.setText(LocaleController.getString("ForwardSettings", C0338R.string.ForwardSettings), true);
                    } else if (i == this.f1394a.f1439s) {
                        textSettingsCell.setText(LocaleController.getString("CategorySettings", C0338R.string.CategorySettings), true);
                    } else if (i == this.f1394a.f1440t) {
                        textSettingsCell.setText(LocaleController.getString("ArchiveSettings", C0338R.string.ArchiveSettings), true);
                    } else if (i == this.f1394a.f1441u) {
                        textSettingsCell.setText(LocaleController.getString("NotificationBarSettings", C0338R.string.NotificationBarSettings), true);
                    } else if (i == this.f1394a.f1442v) {
                        textSettingsCell.setText(LocaleController.getString("MenuSettings", C0338R.string.MenuSettings), true);
                    } else if (i == this.f1394a.f1443w) {
                        textSettingsCell.setText(LocaleController.getString("GhostModeSettings", C0338R.string.GhostModeSettings), true);
                    } else if (i == this.f1394a.f1444x) {
                        textSettingsCell.setText(LocaleController.getString("PasscodeOrPatternLock", C0338R.string.PasscodeOrPatternLock), true);
                    } else if (i == this.f1394a.f1412I) {
                        textSettingsCell.setText(LocaleController.getString("ReportProblemHelp", C0338R.string.ReportProblemHelp), true);
                    }
                } else if (itemViewType == 3) {
                    emptyCell = view == null ? new TextCheckCell(this.f1395b) : view;
                    TextCheckCell textCheckCell = (TextCheckCell) emptyCell;
                } else if (itemViewType == 8) {
                    emptyCell = view == null ? new TextDetailCheckCell(this.f1395b) : view;
                    TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) emptyCell;
                    if (i == this.f1394a.f1407D) {
                        textDetailCheckCell.setTextAndCheck(LocaleController.getString("AutoSyncContacts", C0338R.string.AutoSyncContacts), LocaleController.getString("AutoSyncContactsDetail", C0338R.string.AutoSyncContactsDetail), MoboConstants.f1311D, true);
                    } else if (i == this.f1394a.f1411H) {
                        textDetailCheckCell.setTextAndCheck(LocaleController.getString("BlockAds", C0338R.string.BlockAds), LocaleController.getString("BlockAdsDetail", C0338R.string.BlockAdsDetail), MoboConstants.aV, true);
                    }
                } else if (itemViewType == 4) {
                    emptyCell = view == null ? new HeaderCell(this.f1395b) : view;
                    if (i == this.f1394a.f1432l) {
                        ((HeaderCell) emptyCell).setText(LocaleController.getString("Settings", C0338R.string.Settings));
                    } else if (i == this.f1394a.f1404A) {
                        ((HeaderCell) emptyCell).setText(LocaleController.getString("Privacy", C0338R.string.Privacy));
                    } else if (i == this.f1394a.f1406C) {
                        ((HeaderCell) emptyCell).setText(LocaleController.getString("OtherSettings", C0338R.string.OtherSettings));
                    } else if (i == this.f1394a.f1414K) {
                        ((HeaderCell) emptyCell).setText(LocaleController.getString("BackupRestore", C0338R.string.BackupRestore));
                    }
                } else if (itemViewType == 5) {
                    if (view == null) {
                        emptyCell = new TextInfoCell(this.f1395b);
                        try {
                            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                            ((TextInfoCell) emptyCell).setText(String.format(Locale.US, "Mobogram for Android v%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)}));
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                } else if (itemViewType == 6) {
                    emptyCell = view == null ? new TextDetailSettingsCell(this.f1395b) : view;
                    TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) emptyCell;
                } else if (itemViewType == 7) {
                    if (view == null) {
                        emptyCell = new ShadowSectionCell(this.f1395b);
                    }
                } else if (itemViewType == 9) {
                    emptyCell = view == null ? new TextDetailMultiLineCell(this.f1395b) : view;
                    TextDetailMultiLineCell textDetailMultiLineCell = (TextDetailMultiLineCell) emptyCell;
                    if (i == this.f1394a.f1415L) {
                        textDetailMultiLineCell.setTextAndValue(LocaleController.getString("BackupMoboSettings", C0338R.string.BackupMoboSettings), LocaleController.getString("BackupMoboSettings", C0338R.string.BackupMoboSettingsDetail), true);
                    } else if (i == this.f1394a.f1416M) {
                        textDetailMultiLineCell.setTextAndValue(LocaleController.getString("BackupMoboData", C0338R.string.BackupMoboData), LocaleController.getString("BackupMoboDataDetail", C0338R.string.BackupMoboDataDetail), true);
                    } else if (i == this.f1394a.f1417N) {
                        textDetailMultiLineCell.setTextAndValue(LocaleController.getString("BackupAll", C0338R.string.BackupAll), LocaleController.getString("BackupAllDetail", C0338R.string.BackupAllDetail), true);
                    } else if (i == this.f1394a.f1418O) {
                        textDetailMultiLineCell.setTextAndValue(LocaleController.getString("Restore", C0338R.string.Restore), LocaleController.getString("RestoreDetail", C0338R.string.RestoreDetail), true);
                    }
                }
                emptyCell = view;
            }
            emptyCell.setTag(Integer.valueOf(i));
            return emptyCell;
        }

        public int getViewTypeCount() {
            return 10;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == this.f1394a.f1433m || i == this.f1394a.f1434n || i == this.f1394a.f1439s || i == this.f1394a.f1437q || i == this.f1394a.f1440t || i == this.f1394a.f1410G || i == this.f1394a.f1435o || i == this.f1394a.f1436p || i == this.f1394a.f1408E || i == this.f1394a.f1409F || i == this.f1394a.f1443w || i == this.f1394a.f1445y || i == this.f1394a.f1412I || i == this.f1394a.f1407D || i == this.f1394a.f1438r || i == this.f1394a.f1442v || i == this.f1394a.f1411H || i == this.f1394a.f1441u || i == this.f1394a.f1444x || i == this.f1394a.f1415L || i == this.f1394a.f1416M || i == this.f1394a.f1417N || i == this.f1394a.f1418O;
        }
    }

    public MoboSettingsActivity() {
        this.f1426f = new AvatarUpdater();
    }

    private void m1428a() {
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        if (this.f1421a != null) {
            LayoutParams layoutParams = (LayoutParams) this.f1421a.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.f1421a.setLayoutParams(layoutParams);
                this.f1427g.setTranslationY((float) currentActionBarHeight);
            }
        }
        if (this.f1423c != null) {
            float dp = ((float) this.f1429i) / ((float) AndroidUtilities.dp(88.0f));
            this.f1427g.setScaleY(dp);
            this.f1428h.setTranslationY((float) (currentActionBarHeight + this.f1429i));
            if (dp > DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD) {
            }
            if (ThemeUtil.m2490b()) {
                int i = AdvanceTheme.f2505p;
                this.f1423c.setScaleX((((float) i) + (18.0f * dp)) / (((float) i) * DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                this.f1423c.setScaleY((((float) i) + (18.0f * dp)) / (((float) i) * DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            } else {
                this.f1423c.setScaleX(((18.0f * dp) + 42.0f) / 42.0f);
                this.f1423c.setScaleY(((18.0f * dp) + 42.0f) / 42.0f);
            }
            float currentActionBarHeight2 = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT + dp))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * dp);
            this.f1423c.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * dp);
            this.f1423c.setTranslationY((float) Math.ceil((double) currentActionBarHeight2));
            this.f1424d.setTranslationX((AndroidUtilities.density * -21.0f) * dp);
            this.f1424d.setTranslationY((((float) Math.floor((double) currentActionBarHeight2)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((7.0f * AndroidUtilities.density) * dp))));
            this.f1425e.setTranslationX((AndroidUtilities.density * -21.0f) * dp);
            this.f1425e.setTranslationY((((float) Math.floor((double) currentActionBarHeight2)) + ((float) AndroidUtilities.dp(22.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * dp));
            this.f1424d.setScaleX((0.12f * dp) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.f1424d.setScaleY((0.12f * dp) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
    }

    private void m1430a(String str, MoboSettingsActivity moboSettingsActivity) {
        Builder builder = new Builder(getParentActivity());
        View linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(40.0f));
        View editText = new EditText(getParentActivity());
        editText.setTextSize(18.0f);
        editText.setImeOptions(6);
        editText.setSingleLine(true);
        editText.setHint(LocaleController.getString("BackupFileName", C0338R.string.BackupFileName));
        editText.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        linearLayout.setOrientation(1);
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setTitle(str);
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new MoboSettingsActivity(this, editText, moboSettingsActivity));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void m1431a(ZipOutputStream zipOutputStream, File file) {
        byte[] bArr = new byte[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file), TLRPC.MESSAGE_FLAG_HAS_VIEWS);
        zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
        while (true) {
            int read = bufferedInputStream.read(bArr, 0, TLRPC.MESSAGE_FLAG_HAS_VIEWS);
            if (read != -1) {
                zipOutputStream.write(bArr, 0, read);
            } else {
                bufferedInputStream.close();
                return;
            }
        }
    }

    private boolean m1433a(File file) {
        IOException e;
        FileNotFoundException e2;
        Throwable th;
        boolean z = false;
        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            try {
                objectOutputStream.writeObject(ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).getAll());
                z = true;
                if (objectOutputStream != null) {
                    try {
                        objectOutputStream.flush();
                        objectOutputStream.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e4) {
                e2 = e4;
                try {
                    e2.printStackTrace();
                    if (objectOutputStream != null) {
                        try {
                            objectOutputStream.flush();
                            objectOutputStream.close();
                        } catch (IOException e32) {
                            e32.printStackTrace();
                        }
                    }
                    return z;
                } catch (Throwable th2) {
                    th = th2;
                    if (objectOutputStream != null) {
                        try {
                            objectOutputStream.flush();
                            objectOutputStream.close();
                        } catch (IOException e322) {
                            e322.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e5) {
                e322 = e5;
                e322.printStackTrace();
                if (objectOutputStream != null) {
                    try {
                        objectOutputStream.flush();
                        objectOutputStream.close();
                    } catch (IOException e3222) {
                        e3222.printStackTrace();
                    }
                }
                return z;
            }
        } catch (FileNotFoundException e6) {
            e2 = e6;
            objectOutputStream = null;
            e2.printStackTrace();
            if (objectOutputStream != null) {
                objectOutputStream.flush();
                objectOutputStream.close();
            }
            return z;
        } catch (IOException e7) {
            e3222 = e7;
            objectOutputStream = null;
            e3222.printStackTrace();
            if (objectOutputStream != null) {
                objectOutputStream.flush();
                objectOutputStream.close();
            }
            return z;
        } catch (Throwable th3) {
            th = th3;
            objectOutputStream = null;
            if (objectOutputStream != null) {
                objectOutputStream.flush();
                objectOutputStream.close();
            }
            throw th;
        }
        return z;
    }

    private void m1435b() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                final /* synthetic */ MoboSettingsActivity f1367a;

                {
                    this.f1367a = r1;
                }

                public boolean onPreDraw() {
                    if (this.f1367a.fragmentView != null) {
                        this.f1367a.m1428a();
                        this.f1367a.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private boolean m1437b(File file) {
        ObjectInputStream objectInputStream;
        FileNotFoundException e;
        IOException e2;
        ClassNotFoundException e3;
        Throwable th;
        ObjectInputStream objectInputStream2 = null;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(file));
            try {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.clear();
                for (Entry entry : ((Map) objectInputStream.readObject()).entrySet()) {
                    Object value = entry.getValue();
                    String str = (String) entry.getKey();
                    if (value instanceof Boolean) {
                        edit.putBoolean(str, ((Boolean) value).booleanValue());
                    } else if (value instanceof Float) {
                        edit.putFloat(str, ((Float) value).floatValue());
                    } else if (value instanceof Integer) {
                        edit.putInt(str, ((Integer) value).intValue());
                    } else if (value instanceof Long) {
                        edit.putLong(str, ((Long) value).longValue());
                    } else if (value instanceof String) {
                        edit.putString(str, (String) value);
                    }
                }
                edit.commit();
                if (objectInputStream == null) {
                    return true;
                }
                try {
                    objectInputStream.close();
                    return true;
                } catch (IOException e4) {
                    e4.printStackTrace();
                    return true;
                }
            } catch (FileNotFoundException e5) {
                e = e5;
                objectInputStream2 = objectInputStream;
            } catch (IOException e6) {
                e2 = e6;
            } catch (ClassNotFoundException e7) {
                e3 = e7;
            }
        } catch (FileNotFoundException e8) {
            e = e8;
            try {
                e.printStackTrace();
                if (objectInputStream2 != null) {
                    try {
                        objectInputStream2.close();
                    } catch (IOException e22) {
                        e22.printStackTrace();
                        return false;
                    }
                }
                return false;
            } catch (Throwable th2) {
                th = th2;
                objectInputStream = objectInputStream2;
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException e42) {
                        e42.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (IOException e9) {
            e22 = e9;
            objectInputStream = null;
            try {
                e22.printStackTrace();
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException e222) {
                        e222.printStackTrace();
                        return false;
                    }
                }
                return false;
            } catch (Throwable th3) {
                th = th3;
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                throw th;
            }
        } catch (ClassNotFoundException e10) {
            e3 = e10;
            objectInputStream = null;
            e3.printStackTrace();
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e2222) {
                    e2222.printStackTrace();
                    return false;
                }
            }
            return false;
        } catch (Throwable th4) {
            th = th4;
            objectInputStream = null;
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            throw th;
        }
    }

    private void m1439c() {
        TLObject tLObject;
        FileLocation fileLocation = null;
        boolean z = true;
        User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        if (user.photo != null) {
            tLObject = user.photo.photo_small;
            fileLocation = user.photo.photo_big;
        } else {
            tLObject = null;
        }
        Drawable avatarDrawable = new AvatarDrawable(user, true);
        avatarDrawable.setColor(-10708787);
        if (this.f1423c != null) {
            this.f1423c.setImage(tLObject, "50_50", avatarDrawable);
            this.f1423c.getImageReceiver().setVisible(!PhotoViewer.getInstance().isShowingImage(fileLocation), false);
            this.f1424d.setText(UserObject.getUserName(user));
            if (MoboConstants.f1338e) {
                this.f1425e.setText(LocaleController.getString("Hidden", C0338R.string.Hidden));
            } else {
                this.f1425e.setText(LocaleController.getString("Online", C0338R.string.Online));
            }
            ImageReceiver imageReceiver = this.f1423c.getImageReceiver();
            if (PhotoViewer.getInstance().isShowingImage(fileLocation)) {
                z = false;
            }
            imageReceiver.setVisible(z, false);
        }
    }

    private void m1441d() {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("RestartForApplyChanges", C0338R.string.RestartForApplyChanges));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
            final /* synthetic */ MoboSettingsActivity f1368a;

            {
                this.f1368a = r1;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                this.f1368a.m1443e();
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {
            final /* synthetic */ MoboSettingsActivity f1369a;

            {
                this.f1369a = r1;
            }

            public void onCancel(DialogInterface dialogInterface) {
                this.f1369a.m1443e();
            }
        });
        builder.create().show();
    }

    private void m1443e() {
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 1234567, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }

    private void m1445f() {
        m1430a(LocaleController.getString("BackupMoboSettings", C0338R.string.BackupMoboSettings), new MoboSettingsActivity(this));
    }

    private void m1447g() {
        m1430a(LocaleController.getString("BackupMoboData", C0338R.string.BackupMoboData), new MoboSettingsActivity(this));
    }

    private void m1449h() {
        m1430a(LocaleController.getString("BackupAll", C0338R.string.BackupAll), new MoboSettingsActivity(this));
    }

    private void m1451i() {
        BaseFragment documentSelectActivity = new DocumentSelectActivity();
        documentSelectActivity.setDelegate(new MoboSettingsActivity(this));
        presentFragment(documentSelectActivity);
    }

    private void m1453j() {
        try {
            MaterialHelperUtil.m1367a(getParentActivity(), this.f1421a, this.f1433m, this.f1434n, this.f1435o, this.f1436p, this.f1437q, this.f1438r, this.f1439s, this.f1440t, this.f1441u, this.f1442v, this.f1445y, this.f1443w, this.f1444x, this.f1408E, this.f1409F, this.f1410G);
        } catch (Exception e) {
        }
    }

    private void m1455k() {
        if (ThemeUtil.m2490b()) {
            initThemeActionBar();
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_other).setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
        }
    }

    public boolean allowCaption() {
        return true;
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(5));
        this.actionBar.setItemsBackgroundColor(ThemeUtil.m2485a().m2291e());
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAddToContainer(false);
        this.f1429i = 88;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new MoboSettingsActivity(this));
        this.f1422b = new MoboSettingsActivity(this, context);
        this.fragmentView = new MoboSettingsActivity(this, context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f1421a = new ListView(context);
        int i = AdvanceTheme.f2497h;
        int i2 = AdvanceTheme.f2500k;
        this.f1421a.setDivider(null);
        this.f1421a.setDividerHeight(0);
        this.f1421a.setVerticalScrollBarEnabled(false);
        if (ThemeUtil.m2490b()) {
            this.f1421a.setBackgroundColor(i);
            AndroidUtilities.setListViewEdgeEffectColor(this.f1421a, i2);
        }
        AndroidUtilities.setListViewEdgeEffectColor(this.f1421a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f1421a, LayoutHelper.createFrame(-1, -1, 51));
        this.f1421a.setAdapter(this.f1422b);
        this.f1421a.setOnItemClickListener(new MoboSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        this.f1427g = new View(context);
        this.f1427g.setPivotY(0.0f);
        this.f1427g.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(5));
        if (ThemeUtil.m2490b()) {
            this.f1427g.setBackgroundColor(i2);
        }
        frameLayout.addView(this.f1427g, LayoutHelper.createFrame(-1, 88.0f));
        this.f1428h = new View(context);
        this.f1428h.setBackgroundResource(C0338R.drawable.header_shadow);
        frameLayout.addView(this.f1428h, LayoutHelper.createFrame(-1, 3.0f));
        this.f1423c = new BackupImageView(context);
        this.f1423c.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.f1423c.setPivotX(0.0f);
        this.f1423c.setPivotY(0.0f);
        if (ThemeUtil.m2490b()) {
            this.f1423c.setRoundRadius(AdvanceTheme.f2502m);
            i = AdvanceTheme.f2505p;
            frameLayout.addView(this.f1423c, LayoutHelper.createFrame(i, (float) i, 3, 64.0f, 0.0f, 0.0f, 0.0f));
        } else {
            frameLayout.addView(this.f1423c, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        }
        this.f1423c.setOnClickListener(new View.OnClickListener() {
            final /* synthetic */ MoboSettingsActivity f1362a;

            {
                this.f1362a = r1;
            }

            public void onClick(View view) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                if (user.photo != null && user.photo.photo_big != null) {
                    PhotoViewer.getInstance().setParentActivity(this.f1362a.getParentActivity());
                    PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.f1362a);
                }
            }
        });
        this.f1424d = new TextView(context);
        this.f1424d.setTextColor(-1);
        if (ThemeUtil.m2490b()) {
            this.f1424d.setTextColor(AdvanceTheme.f2501l);
        }
        this.f1424d.setTextSize(1, 18.0f);
        this.f1424d.setLines(1);
        this.f1424d.setMaxLines(1);
        this.f1424d.setSingleLine(true);
        this.f1424d.setEllipsize(TruncateAt.END);
        this.f1424d.setGravity(3);
        this.f1424d.setTypeface(FontUtil.m1176a().m1160c());
        this.f1424d.setPivotX(0.0f);
        this.f1424d.setPivotY(0.0f);
        frameLayout.addView(this.f1424d, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 48.0f, 0.0f));
        this.f1425e = new TextView(context);
        this.f1425e.setTypeface(FontUtil.m1176a().m1161d());
        this.f1425e.setTextColor(AvatarDrawable.getProfileTextColorForId(5));
        if (ThemeUtil.m2490b()) {
            this.f1425e.setTextColor(AdvanceTheme.f2504o);
        }
        this.f1425e.setTextSize(1, 14.0f);
        this.f1425e.setLines(1);
        this.f1425e.setMaxLines(1);
        this.f1425e.setSingleLine(true);
        this.f1425e.setEllipsize(TruncateAt.END);
        this.f1425e.setGravity(3);
        frameLayout.addView(this.f1425e, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 48.0f, 0.0f));
        m1428a();
        this.f1421a.setOnScrollListener(new OnScrollListener() {
            int f1363a;
            int f1364b;
            int f1365c;
            final /* synthetic */ MoboSettingsActivity f1366d;

            {
                this.f1366d = r1;
            }

            private void m1384a() {
                if (this.f1364b > 0 && this.f1365c == 0) {
                    this.f1366d.m1453j();
                }
            }

            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                int i4 = 0;
                this.f1363a = i;
                this.f1364b = i2;
                if (i3 != 0) {
                    View childAt = absListView.getChildAt(0);
                    if (childAt != null) {
                        if (i == 0) {
                            int dp = AndroidUtilities.dp(88.0f);
                            if (childAt.getTop() < 0) {
                                i4 = childAt.getTop();
                            }
                            i4 += dp;
                        }
                        if (this.f1366d.f1429i != i4) {
                            this.f1366d.f1429i = i4;
                            this.f1366d.m1428a();
                        }
                    }
                }
            }

            public void onScrollStateChanged(AbsListView absListView, int i) {
                this.f1365c = i;
                m1384a();
            }
        });
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if ((intValue & 2) != 0 || (intValue & 1) != 0) {
                m1439c();
            }
        }
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        if (fileLocation == null) {
            return null;
        }
        User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        if (user == null || user.photo == null || user.photo.photo_big == null) {
            return null;
        }
        FileLocation fileLocation2 = user.photo.photo_big;
        if (fileLocation2.local_id != fileLocation.local_id || fileLocation2.volume_id != fileLocation.volume_id || fileLocation2.dc_id != fileLocation.dc_id) {
            return null;
        }
        int[] iArr = new int[2];
        this.f1423c.getLocationInWindow(iArr);
        PlaceProviderObject placeProviderObject = new PlaceProviderObject();
        placeProviderObject.viewX = iArr[0];
        placeProviderObject.viewY = iArr[1] - AndroidUtilities.statusBarHeight;
        placeProviderObject.parentView = this.f1423c;
        placeProviderObject.imageReceiver = this.f1423c.getImageReceiver();
        placeProviderObject.dialogId = UserConfig.getClientUserId();
        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
        placeProviderObject.size = -1;
        placeProviderObject.radius = this.f1423c.getImageReceiver().getRoundRadius();
        placeProviderObject.scale = this.f1423c.getScaleX();
        return placeProviderObject;
    }

    public int getSelectedCount() {
        return 0;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        return null;
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.f1426f.onActivityResult(i, i2, intent);
    }

    protected void onBecomeFullyVisible() {
        m1453j();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        m1435b();
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.f1426f.parentFragment = this;
        this.f1426f.delegate = new MoboSettingsActivity(this);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        this.f1420Q = 0;
        int i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1430j = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1432l = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1433m = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1434n = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1435o = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1436p = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1437q = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1438r = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1439s = i;
        if (UserConfig.isRobot) {
            this.f1440t = -1;
        } else {
            i = this.f1420Q;
            this.f1420Q = i + 1;
            this.f1440t = i;
        }
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1441u = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1442v = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1446z = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1404A = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1445y = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1443w = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1444x = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1405B = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1406C = i;
        if (UserConfig.isRobot) {
            this.f1407D = -1;
        } else {
            i = this.f1420Q;
            this.f1420Q = i + 1;
            this.f1407D = i;
        }
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1408E = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1409F = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1410G = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1411H = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1412I = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1413J = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1414K = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1415L = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1416M = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1417N = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1418O = i;
        i = this.f1420Q;
        this.f1420Q = i + 1;
        this.f1419P = i;
        MessagesController.getInstance().loadFullUser(UserConfig.getCurrentUser(), this.classGuid, true);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.f1423c != null) {
            this.f1423c.setImageDrawable(null);
        }
        MessagesController.getInstance().cancelLoadFullUser(UserConfig.getClientUserId());
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        this.f1426f.clear();
    }

    public void onResume() {
        super.onResume();
        if (this.f1422b != null) {
            this.f1422b.notifyDataSetChanged();
        }
        m1439c();
        m1455k();
        m1435b();
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.f1426f != null) {
            this.f1426f.currentPicturePath = bundle.getString("path");
        }
    }

    public void saveSelfArgs(Bundle bundle) {
        if (this.f1426f != null && this.f1426f.currentPicturePath != null) {
            bundle.putString("path", this.f1426f.currentPicturePath);
        }
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
    }

    public void setPhotoChecked(int i) {
    }

    public void updatePhotoAtIndex(int i) {
    }

    public void willHidePhotoViewer() {
        this.f1423c.getImageReceiver().setVisible(true, true);
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}
