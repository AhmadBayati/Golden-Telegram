package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_getWallPapers;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_wallPaper;
import com.hanista.mobogram.tgnet.TLRPC.TL_wallPaperSolid;
import com.hanista.mobogram.tgnet.TLRPC.Vector;
import com.hanista.mobogram.tgnet.TLRPC.WallPaper;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.WallpaperCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class WallpapersActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private ImageView backgroundImage;
    private String currentPicturePath;
    private View doneButton;
    private ListAdapter listAdapter;
    private String loadingFile;
    private File loadingFileObject;
    private PhotoSize loadingSize;
    private FrameLayout progressView;
    private View progressViewBackground;
    private int selectedBackground;
    private int selectedColor;
    private ArrayList<WallPaper> wallPapers;
    private HashMap<Integer, WallPaper> wallpappersByIds;

    /* renamed from: com.hanista.mobogram.ui.WallpapersActivity.1 */
    class C19611 extends ActionBarMenuOnItemClick {
        C19611() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                WallpapersActivity.this.finishFragment();
            } else if (i == WallpapersActivity.done_button) {
                boolean renameTo;
                WallPaper wallPaper = (WallPaper) WallpapersActivity.this.wallpappersByIds.get(Integer.valueOf(WallpapersActivity.this.selectedBackground));
                if (wallPaper == null || wallPaper.id == 1000001 || !(wallPaper instanceof TL_wallPaper)) {
                    renameTo = WallpapersActivity.this.selectedBackground == -1 ? FileLoader.renameTo(new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg"), new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg")) : true;
                } else {
                    int i2 = AndroidUtilities.displaySize.x;
                    int i3 = AndroidUtilities.displaySize.y;
                    if (i2 <= i3) {
                        int i4 = i3;
                        i3 = i2;
                        i2 = i4;
                    }
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(wallPaper.sizes, Math.min(i3, i2));
                    try {
                        renameTo = AndroidUtilities.copyFile(new File(FileLoader.getInstance().getDirectory(4), closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + ".jpg"), new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"));
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                        renameTo = false;
                    }
                }
                if (renameTo) {
                    Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                    edit.putInt("selectedBackground", WallpapersActivity.this.selectedBackground);
                    edit.putInt("selectedColor", WallpapersActivity.this.selectedColor);
                    edit.commit();
                    if (ThemeUtil.m2490b()) {
                        AdvanceTheme.m2281a(false);
                    }
                    ApplicationLoader.reloadWallpaper();
                }
                WallpapersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.WallpapersActivity.2 */
    class C19622 implements OnTouchListener {
        C19622() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.WallpapersActivity.3 */
    class C19653 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.WallpapersActivity.3.1 */
        class C19631 implements OnClickListener {
            C19631() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent;
                if (i == 0) {
                    try {
                        intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        File generatePicturePath = AndroidUtilities.generatePicturePath();
                        if (generatePicturePath != null) {
                            if (VERSION.SDK_INT >= 24) {
                                intent.putExtra("output", FileProvider.getUriForFile(WallpapersActivity.this.getParentActivity(), "com.hanista.mobogram.provider", generatePicturePath));
                                intent.addFlags(2);
                                intent.addFlags(WallpapersActivity.done_button);
                            } else {
                                intent.putExtra("output", Uri.fromFile(generatePicturePath));
                            }
                            WallpapersActivity.this.currentPicturePath = generatePicturePath.getAbsolutePath();
                        }
                        WallpapersActivity.this.startActivityForResult(intent, 10);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                } else if (i == WallpapersActivity.done_button) {
                    intent = new Intent("android.intent.action.PICK");
                    intent.setType("image/*");
                    WallpapersActivity.this.startActivityForResult(intent, 11);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.WallpapersActivity.3.2 */
        class C19642 implements Runnable {
            C19642() {
            }

            public void run() {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.wallpaperChanged, new Object[0]);
            }
        }

        C19653() {
        }

        public void onItemClick(View view, int i) {
            if (i == 0) {
                if (WallpapersActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(WallpapersActivity.this.getParentActivity());
                    builder.setItems(new CharSequence[]{LocaleController.getString("FromCamera", C0338R.string.FromCamera), LocaleController.getString("FromGalley", C0338R.string.FromGalley), LocaleController.getString("Cancel", C0338R.string.Cancel)}, new C19631());
                    WallpapersActivity.this.showDialog(builder.create());
                } else {
                    return;
                }
            } else if (i - 1 >= 0 && i - 1 < WallpapersActivity.this.wallPapers.size()) {
                WallpapersActivity.this.selectedBackground = ((WallPaper) WallpapersActivity.this.wallPapers.get(i - 1)).id;
                WallpapersActivity.this.listAdapter.notifyDataSetChanged();
                WallpapersActivity.this.processSelectedBackground();
            } else {
                return;
            }
            AndroidUtilities.runOnUIThread(new C19642());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.WallpapersActivity.4 */
    class C19674 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.ui.WallpapersActivity.4.1 */
        class C19661 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C19661(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                WallpapersActivity.this.wallPapers.clear();
                Vector vector = (Vector) this.val$response;
                WallpapersActivity.this.wallpappersByIds.clear();
                Iterator it = vector.objects.iterator();
                while (it.hasNext()) {
                    Object next = it.next();
                    WallpapersActivity.this.wallPapers.add((WallPaper) next);
                    WallpapersActivity.this.wallpappersByIds.put(Integer.valueOf(((WallPaper) next).id), (WallPaper) next);
                }
                if (WallpapersActivity.this.listAdapter != null) {
                    WallpapersActivity.this.listAdapter.notifyDataSetChanged();
                }
                if (WallpapersActivity.this.backgroundImage != null) {
                    WallpapersActivity.this.processSelectedBackground();
                }
                MessagesStorage.getInstance().putWallpapers(WallpapersActivity.this.wallPapers);
            }
        }

        C19674() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                AndroidUtilities.runOnUIThread(new C19661(tLObject));
            }
        }
    }

    private class ListAdapter extends Adapter {
        private Context mContext;

        private class Holder extends ViewHolder {
            public Holder(View view) {
                super(view);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return WallpapersActivity.this.wallPapers.size() + WallpapersActivity.done_button;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ((WallpaperCell) viewHolder.itemView).setWallpaper(i == 0 ? null : (WallPaper) WallpapersActivity.this.wallPapers.get(i - 1), WallpapersActivity.this.selectedBackground);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new WallpaperCell(this.mContext));
        }
    }

    public WallpapersActivity() {
        this.wallPapers = new ArrayList();
        this.wallpappersByIds = new HashMap();
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
    }

    private void loadWallpapers() {
        ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(new TL_account_getWallPapers(), new C19674()), this.classGuid);
    }

    private void processSelectedBackground() {
        WallPaper wallPaper = (WallPaper) this.wallpappersByIds.get(Integer.valueOf(this.selectedBackground));
        if (this.selectedBackground == -1 || this.selectedBackground == 1000001 || wallPaper == null || !(wallPaper instanceof TL_wallPaper)) {
            if (this.loadingFile != null) {
                FileLoader.getInstance().cancelLoadFile(this.loadingSize);
            }
            if (this.selectedBackground == 1000001) {
                this.backgroundImage.setImageResource(ThemeUtil.m2485a().m2295i());
                this.backgroundImage.setBackgroundColor(0);
                this.selectedColor = 0;
            } else if (this.selectedBackground == -1) {
                File file = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg");
                if (!file.exists()) {
                    file = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
                }
                if (file.exists()) {
                    this.backgroundImage.setImageURI(Uri.fromFile(file));
                } else {
                    this.selectedBackground = 1000001;
                    processSelectedBackground();
                }
            } else if (wallPaper == null) {
                return;
            } else {
                if (wallPaper instanceof TL_wallPaperSolid) {
                    this.backgroundImage.getDrawable();
                    this.backgroundImage.setImageBitmap(null);
                    this.selectedColor = wallPaper.bg_color | Theme.MSG_TEXT_COLOR;
                    this.backgroundImage.setBackgroundColor(this.selectedColor);
                }
            }
            this.loadingFileObject = null;
            this.loadingFile = null;
            this.loadingSize = null;
            this.doneButton.setEnabled(true);
            this.progressView.setVisibility(8);
            return;
        }
        int i = AndroidUtilities.displaySize.x;
        int i2 = AndroidUtilities.displaySize.y;
        if (i <= i2) {
            int i3 = i2;
            i2 = i;
            i = i3;
        }
        PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(wallPaper.sizes, Math.min(i2, i));
        if (closestPhotoSizeWithSize != null) {
            String str = closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + ".jpg";
            File file2 = new File(FileLoader.getInstance().getDirectory(4), str);
            if (file2.exists()) {
                if (this.loadingFile != null) {
                    FileLoader.getInstance().cancelLoadFile(this.loadingSize);
                }
                this.loadingFileObject = null;
                this.loadingFile = null;
                this.loadingSize = null;
                try {
                    this.backgroundImage.setImageURI(Uri.fromFile(file2));
                } catch (Throwable th) {
                    FileLog.m18e("tmessages", th);
                }
                this.backgroundImage.setBackgroundColor(0);
                this.selectedColor = 0;
                this.doneButton.setEnabled(true);
                this.progressView.setVisibility(8);
                return;
            }
            this.progressViewBackground.getBackground().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.calcDrawableColor(this.backgroundImage.getDrawable())[0], Mode.MULTIPLY));
            this.loadingFile = str;
            this.loadingFileObject = file2;
            this.doneButton.setEnabled(false);
            this.progressView.setVisibility(0);
            this.loadingSize = closestPhotoSizeWithSize;
            this.selectedColor = 0;
            FileLoader.getInstance().loadFile(closestPhotoSizeWithSize, null, true);
            this.backgroundImage.setBackgroundColor(0);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ChatBackground", C0338R.string.ChatBackground));
        this.actionBar.setActionBarMenuOnItemClick(new C19611());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.SRC_IN);
            this.doneButton = createMenu.addItemWithWidth((int) done_button, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.doneButton = createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        View frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.backgroundImage = new ImageView(context);
        this.backgroundImage.setScaleType(ScaleType.CENTER_CROP);
        frameLayout.addView(this.backgroundImage, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.backgroundImage.setOnTouchListener(new C19622());
        this.progressView = new FrameLayout(context);
        this.progressView.setVisibility(4);
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 0.0f, 0.0f, 52.0f));
        this.progressViewBackground = new View(context);
        this.progressViewBackground.setBackgroundResource(C0338R.drawable.system_loader);
        this.progressView.addView(this.progressViewBackground, LayoutHelper.createFrame(36, 36, 17));
        View progressBar = new ProgressBar(context);
        try {
            progressBar.setIndeterminateDrawable(context.getResources().getDrawable(C0338R.drawable.loading_animation));
        } catch (Exception e) {
        }
        progressBar.setIndeterminate(true);
        AndroidUtilities.setProgressBarAnimationDuration(progressBar, ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED);
        this.progressView.addView(progressBar, LayoutHelper.createFrame(32, 32, 17));
        progressBar = new RecyclerListView(context);
        progressBar.setClipToPadding(false);
        progressBar.setTag(Integer.valueOf(8));
        progressBar.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(0);
        progressBar.setLayoutManager(linearLayoutManager);
        progressBar.setDisallowInterceptTouchEvents(true);
        progressBar.setOverScrollMode(2);
        Adapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        progressBar.setAdapter(listAdapter);
        frameLayout.addView(progressBar, LayoutHelper.createFrame(-1, 102, 83));
        progressBar.setOnItemClickListener(new C19653());
        processSelectedBackground();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        String str;
        if (i == NotificationCenter.FileDidFailedLoad) {
            str = (String) objArr[0];
            if (this.loadingFile != null && this.loadingFile.equals(str)) {
                this.loadingFileObject = null;
                this.loadingFile = null;
                this.loadingSize = null;
                this.progressView.setVisibility(8);
                this.doneButton.setEnabled(false);
            }
        } else if (i == NotificationCenter.FileDidLoaded) {
            str = (String) objArr[0];
            if (this.loadingFile != null && this.loadingFile.equals(str)) {
                this.backgroundImage.setImageURI(Uri.fromFile(this.loadingFileObject));
                this.progressView.setVisibility(8);
                this.backgroundImage.setBackgroundColor(0);
                this.doneButton.setEnabled(true);
                this.loadingFileObject = null;
                this.loadingFile = null;
                this.loadingSize = null;
            }
        } else if (i == NotificationCenter.wallpapersDidLoaded) {
            this.wallPapers = (ArrayList) objArr[0];
            this.wallpappersByIds.clear();
            Iterator it = this.wallPapers.iterator();
            while (it.hasNext()) {
                WallPaper wallPaper = (WallPaper) it.next();
                this.wallpappersByIds.put(Integer.valueOf(wallPaper.id), wallPaper);
            }
            if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
            }
            if (!(this.wallPapers.isEmpty() || this.backgroundImage == null)) {
                processSelectedBackground();
            }
            loadWallpapers();
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        FileOutputStream fileOutputStream;
        Throwable e;
        FileOutputStream fileOutputStream2 = null;
        if (i2 != -1) {
            return;
        }
        Point realScreenSize;
        Bitmap loadBitmap;
        if (i == 10) {
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            try {
                realScreenSize = AndroidUtilities.getRealScreenSize();
                loadBitmap = ImageLoader.loadBitmap(this.currentPicturePath, null, (float) realScreenSize.x, (float) realScreenSize.y, true);
                fileOutputStream = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg"));
                try {
                    loadBitmap.compress(CompressFormat.JPEG, 87, fileOutputStream);
                    this.selectedBackground = -1;
                    this.selectedColor = 0;
                    this.backgroundImage.getDrawable();
                    this.backgroundImage.setImageBitmap(loadBitmap);
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable e2) {
                            FileLog.m18e("tmessages", e2);
                        }
                    }
                } catch (Exception e3) {
                    e2 = e3;
                    try {
                        FileLog.m18e("tmessages", e2);
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable e22) {
                                FileLog.m18e("tmessages", e22);
                            }
                        }
                        this.currentPicturePath = null;
                    } catch (Throwable th) {
                        e22 = th;
                        fileOutputStream2 = fileOutputStream;
                        if (fileOutputStream2 != null) {
                            try {
                                fileOutputStream2.close();
                            } catch (Throwable e4) {
                                FileLog.m18e("tmessages", e4);
                            }
                        }
                        throw e22;
                    }
                }
            } catch (Exception e5) {
                e22 = e5;
                fileOutputStream = null;
                FileLog.m18e("tmessages", e22);
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                this.currentPicturePath = null;
            } catch (Throwable th2) {
                e22 = th2;
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                throw e22;
            }
            this.currentPicturePath = null;
        } else if (i == 11 && intent != null && intent.getData() != null) {
            try {
                realScreenSize = AndroidUtilities.getRealScreenSize();
                loadBitmap = ImageLoader.loadBitmap(null, intent.getData(), (float) realScreenSize.x, (float) realScreenSize.y, true);
                loadBitmap.compress(CompressFormat.JPEG, 87, new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg")));
                this.selectedBackground = -1;
                this.selectedColor = 0;
                this.backgroundImage.getDrawable();
                this.backgroundImage.setImageBitmap(loadBitmap);
            } catch (Throwable e222) {
                FileLog.m18e("tmessages", e222);
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.wallpapersDidLoaded);
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        this.selectedBackground = sharedPreferences.getInt("selectedBackground", 1000001);
        this.selectedColor = sharedPreferences.getInt("selectedColor", 0);
        MessagesStorage.getInstance().getWallpapers();
        new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg").delete();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.wallpapersDidLoaded);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        processSelectedBackground();
        initThemeActionBar();
    }

    public void restoreSelfArgs(Bundle bundle) {
        this.currentPicturePath = bundle.getString("path");
    }

    public void saveSelfArgs(Bundle bundle) {
        if (this.currentPicturePath != null) {
            bundle.putString("path", this.currentPicturePath);
        }
    }
}
