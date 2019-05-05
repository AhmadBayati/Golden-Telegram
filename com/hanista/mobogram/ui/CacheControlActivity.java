package com.hanista.mobogram.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.SQLite.SQLiteDatabase;
import com.hanista.mobogram.SQLite.SQLitePreparedStatement;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ClearCacheService;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.query.BotQuery;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.Builder;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.io.File;
import java.util.ArrayList;

public class CacheControlActivity extends BaseFragment {
    private long audioSize;
    private int cacheInfoRow;
    private int cacheRow;
    private long cacheSize;
    private boolean calculating;
    private volatile boolean canceled;
    private boolean[] clear;
    private int databaseInfoRow;
    private int databaseRow;
    private long databaseSize;
    private long documentsSize;
    private int keepMediaInfoRow;
    private int keepMediaRow;
    private ListAdapter listAdapter;
    private long musicSize;
    private long photoSize;
    private int rowCount;
    private long totalSize;
    private long videoSize;

    /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.1 */
    class C10711 implements Runnable {

        /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.1.1 */
        class C10701 implements Runnable {
            C10701() {
            }

            public void run() {
                CacheControlActivity.this.calculating = false;
                if (CacheControlActivity.this.listAdapter != null) {
                    CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                }
            }
        }

        C10711() {
        }

        public void run() {
            CacheControlActivity.this.cacheSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(4), 0);
            if (!CacheControlActivity.this.canceled) {
                CacheControlActivity.this.photoSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(0), 0);
                if (!CacheControlActivity.this.canceled) {
                    CacheControlActivity.this.videoSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(2), 0);
                    if (!CacheControlActivity.this.canceled) {
                        CacheControlActivity.this.documentsSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), 1);
                        if (!CacheControlActivity.this.canceled) {
                            CacheControlActivity.this.musicSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), 2);
                            if (!CacheControlActivity.this.canceled) {
                                CacheControlActivity.this.audioSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(1), 0);
                                CacheControlActivity.this.totalSize = ((((CacheControlActivity.this.cacheSize + CacheControlActivity.this.videoSize) + CacheControlActivity.this.audioSize) + CacheControlActivity.this.photoSize) + CacheControlActivity.this.documentsSize) + CacheControlActivity.this.musicSize;
                                AndroidUtilities.runOnUIThread(new C10701());
                            }
                        }
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.2 */
    class C10732 implements Runnable {
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.2.1 */
        class C10721 implements Runnable {
            final /* synthetic */ boolean val$imagesClearedFinal;

            C10721(boolean z) {
                this.val$imagesClearedFinal = z;
            }

            public void run() {
                if (this.val$imagesClearedFinal) {
                    ImageLoader.getInstance().clearMemory();
                }
                if (CacheControlActivity.this.listAdapter != null) {
                    CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                }
                try {
                    C10732.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        C10732(ProgressDialog progressDialog) {
            this.val$progressDialog = progressDialog;
        }

        public void run() {
            boolean z = false;
            for (int i = 0; i < 6; i++) {
                if (CacheControlActivity.this.clear[i]) {
                    int i2;
                    int i3;
                    if (i == 0) {
                        i2 = 0;
                        i3 = 0;
                    } else if (i == 1) {
                        i2 = 0;
                        i3 = 2;
                    } else if (i == 2) {
                        i2 = 1;
                        i3 = 3;
                    } else if (i == 3) {
                        i2 = 2;
                        i3 = 3;
                    } else if (i == 4) {
                        i2 = 0;
                        i3 = 1;
                    } else if (i == 5) {
                        i2 = 0;
                        i3 = 4;
                    } else {
                        i2 = 0;
                        i3 = -1;
                    }
                    if (i3 != -1) {
                        File checkDirectory = FileLoader.getInstance().checkDirectory(i3);
                        if (checkDirectory != null) {
                            try {
                                File[] listFiles = checkDirectory.listFiles();
                                if (listFiles != null) {
                                    int i4 = 0;
                                    while (i4 < listFiles.length) {
                                        String toLowerCase = listFiles[i4].getName().toLowerCase();
                                        if (i2 == 1 || i2 == 2) {
                                            if (toLowerCase.endsWith(".mp3") || toLowerCase.endsWith(".m4a")) {
                                                if (i2 == 1) {
                                                    i4++;
                                                }
                                            } else if (i2 == 2) {
                                                i4++;
                                            }
                                        }
                                        if (!toLowerCase.equals(".nomedia") && listFiles[i4].isFile()) {
                                            listFiles[i4].delete();
                                        }
                                        i4++;
                                    }
                                }
                            } catch (Throwable th) {
                                FileLog.m18e("tmessages", th);
                            }
                        }
                        if (i3 == 4) {
                            CacheControlActivity.this.cacheSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(4), i2);
                            z = true;
                        } else if (i3 == 1) {
                            CacheControlActivity.this.audioSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(1), i2);
                        } else if (i3 == 3) {
                            if (i2 == 1) {
                                CacheControlActivity.this.documentsSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), i2);
                            } else {
                                CacheControlActivity.this.musicSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), i2);
                            }
                        } else if (i3 == 0) {
                            CacheControlActivity.this.photoSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(0), i2);
                            z = true;
                        } else if (i3 == 2) {
                            CacheControlActivity.this.videoSize = CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(2), i2);
                        }
                    }
                }
            }
            CacheControlActivity.this.totalSize = ((((CacheControlActivity.this.cacheSize + CacheControlActivity.this.videoSize) + CacheControlActivity.this.audioSize) + CacheControlActivity.this.photoSize) + CacheControlActivity.this.documentsSize) + CacheControlActivity.this.musicSize;
            AndroidUtilities.runOnUIThread(new C10721(z));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.3 */
    class C10743 extends ActionBarMenuOnItemClick {
        C10743() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                CacheControlActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.4 */
    class C10814 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.4.1 */
        class C10751 implements OnClickListener {
            C10751() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("keep_media", i).commit();
                if (CacheControlActivity.this.listAdapter != null) {
                    CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                }
                PendingIntent service = PendingIntent.getService(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, ClearCacheService.class), 0);
                AlarmManager alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM);
                if (i == 2) {
                    alarmManager.cancel(service);
                } else {
                    alarmManager.setInexactRepeating(2, 86400000, 86400000, service);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.4.2 */
        class C10782 implements OnClickListener {

            /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.4.2.1 */
            class C10771 implements Runnable {
                final /* synthetic */ ProgressDialog val$progressDialog;

                /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.4.2.1.1 */
                class C10761 implements Runnable {
                    C10761() {
                    }

                    public void run() {
                        try {
                            C10771.this.val$progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                        if (CacheControlActivity.this.listAdapter != null) {
                            CacheControlActivity.this.databaseSize = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db").length();
                            CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                        }
                    }
                }

                C10771(ProgressDialog progressDialog) {
                    this.val$progressDialog = progressDialog;
                }

                public void run() {
                    try {
                        SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                        ArrayList arrayList = new ArrayList();
                        SQLiteCursor queryFinalized = database.queryFinalized("SELECT did FROM dialogs WHERE 1", new Object[0]);
                        StringBuilder stringBuilder = new StringBuilder();
                        while (queryFinalized.next()) {
                            long longValue = queryFinalized.longValue(0);
                            int i = (int) (longValue >> 32);
                            if (!(((int) longValue) == 0 || i == 1)) {
                                arrayList.add(Long.valueOf(longValue));
                            }
                        }
                        queryFinalized.dispose();
                        SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                        SQLitePreparedStatement executeFast2 = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                        database.beginTransaction();
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            Long l = (Long) arrayList.get(i2);
                            int i3 = 0;
                            SQLiteCursor queryFinalized2 = database.queryFinalized("SELECT COUNT(mid) FROM messages WHERE uid = " + l, new Object[0]);
                            if (queryFinalized2.next()) {
                                i3 = queryFinalized2.intValue(0);
                            }
                            queryFinalized2.dispose();
                            if (i3 > 2) {
                                SQLiteCursor queryFinalized3 = database.queryFinalized("SELECT last_mid_i, last_mid FROM dialogs WHERE did = " + l, new Object[0]);
                                i3 = -1;
                                if (queryFinalized3.next()) {
                                    long longValue2 = queryFinalized3.longValue(0);
                                    long longValue3 = queryFinalized3.longValue(1);
                                    SQLiteCursor queryFinalized4 = database.queryFinalized("SELECT data FROM messages WHERE uid = " + l + " AND mid IN (" + longValue2 + "," + longValue3 + ")", new Object[0]);
                                    while (queryFinalized4.next()) {
                                        try {
                                            AbstractSerializedData byteBufferValue = queryFinalized4.byteBufferValue(0);
                                            if (byteBufferValue != null) {
                                                Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                byteBufferValue.reuse();
                                                if (TLdeserialize != null) {
                                                    i3 = TLdeserialize.id;
                                                }
                                            }
                                        } catch (Throwable e) {
                                            FileLog.m18e("tmessages", e);
                                        }
                                    }
                                    queryFinalized4.dispose();
                                    database.executeFast("DELETE FROM messages WHERE uid = " + l + " AND mid != " + longValue2 + " AND mid != " + longValue3).stepThis().dispose();
                                    database.executeFast("DELETE FROM messages_holes WHERE uid = " + l).stepThis().dispose();
                                    database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + l).stepThis().dispose();
                                    database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + l).stepThis().dispose();
                                    database.executeFast("DELETE FROM media_v2 WHERE uid = " + l).stepThis().dispose();
                                    database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + l).stepThis().dispose();
                                    BotQuery.clearBotKeyboard(l.longValue(), null);
                                    if (i3 != -1) {
                                        MessagesStorage.createFirstHoles(l.longValue(), executeFast, executeFast2, i3);
                                    }
                                }
                                queryFinalized3.dispose();
                            }
                        }
                        executeFast.dispose();
                        executeFast2.dispose();
                        database.commitTransaction();
                        database.executeFast("VACUUM").stepThis().dispose();
                    } catch (Throwable e2) {
                        FileLog.m18e("tmessages", e2);
                    } finally {
                        AndroidUtilities.runOnUIThread(new C10761());
                    }
                }
            }

            C10782() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ProgressDialog progressDialog = new ProgressDialog(CacheControlActivity.this.getParentActivity());
                progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C10771(progressDialog));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.4.3 */
        class C10793 implements View.OnClickListener {
            C10793() {
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                CacheControlActivity.this.clear[intValue] = !CacheControlActivity.this.clear[intValue];
                checkBoxCell.setChecked(CacheControlActivity.this.clear[intValue], true);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.CacheControlActivity.4.4 */
        class C10804 implements View.OnClickListener {
            C10804() {
            }

            public void onClick(View view) {
                try {
                    if (CacheControlActivity.this.visibleDialog != null) {
                        CacheControlActivity.this.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                CacheControlActivity.this.cleanupFolders();
            }
        }

        C10814() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (CacheControlActivity.this.getParentActivity() != null) {
                if (i == CacheControlActivity.this.keepMediaRow) {
                    Builder builder = new Builder(CacheControlActivity.this.getParentActivity());
                    builder.setItems(new CharSequence[]{LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("KeepMediaForever", C0338R.string.KeepMediaForever)}, new C10751());
                    CacheControlActivity.this.showDialog(builder.create());
                } else if (i == CacheControlActivity.this.databaseRow) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(CacheControlActivity.this.getParentActivity());
                    builder2.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder2.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    builder2.setMessage(LocaleController.getString("LocalDatabaseClear", C0338R.string.LocalDatabaseClear));
                    builder2.setPositiveButton(LocaleController.getString("CacheClear", C0338R.string.CacheClear), new C10782());
                    CacheControlActivity.this.showDialog(builder2.create());
                } else if (i == CacheControlActivity.this.cacheRow && CacheControlActivity.this.totalSize > 0 && CacheControlActivity.this.getParentActivity() != null) {
                    Builder builder3 = new Builder(CacheControlActivity.this.getParentActivity());
                    builder3.setApplyTopPadding(false);
                    builder3.setApplyBottomPadding(false);
                    View linearLayout = new LinearLayout(CacheControlActivity.this.getParentActivity());
                    linearLayout.setOrientation(1);
                    for (int i2 = 0; i2 < 6; i2++) {
                        long j2 = 0;
                        String str = null;
                        if (i2 == 0) {
                            j2 = CacheControlActivity.this.photoSize;
                            str = LocaleController.getString("LocalPhotoCache", C0338R.string.LocalPhotoCache);
                        } else if (i2 == 1) {
                            j2 = CacheControlActivity.this.videoSize;
                            str = LocaleController.getString("LocalVideoCache", C0338R.string.LocalVideoCache);
                        } else if (i2 == 2) {
                            j2 = CacheControlActivity.this.documentsSize;
                            str = LocaleController.getString("LocalDocumentCache", C0338R.string.LocalDocumentCache);
                        } else if (i2 == 3) {
                            j2 = CacheControlActivity.this.musicSize;
                            str = LocaleController.getString("LocalMusicCache", C0338R.string.LocalMusicCache);
                        } else if (i2 == 4) {
                            j2 = CacheControlActivity.this.audioSize;
                            str = LocaleController.getString("LocalAudioCache", C0338R.string.LocalAudioCache);
                        } else if (i2 == 5) {
                            j2 = CacheControlActivity.this.cacheSize;
                            str = LocaleController.getString("LocalCache", C0338R.string.LocalCache);
                        }
                        if (j2 > 0) {
                            CacheControlActivity.this.clear[i2] = true;
                            View checkBoxCell = new CheckBoxCell(CacheControlActivity.this.getParentActivity());
                            checkBoxCell.setTag(Integer.valueOf(i2));
                            checkBoxCell.setBackgroundResource(C0338R.drawable.list_selector);
                            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                            checkBoxCell.setText(str, AndroidUtilities.formatFileSize(j2), true, true);
                            checkBoxCell.setOnClickListener(new C10793());
                        } else {
                            CacheControlActivity.this.clear[i2] = false;
                        }
                    }
                    View bottomSheetCell = new BottomSheetCell(CacheControlActivity.this.getParentActivity(), 1);
                    bottomSheetCell.setBackgroundResource(C0338R.drawable.list_selector);
                    bottomSheetCell.setTextAndIcon(LocaleController.getString("ClearMediaCache", C0338R.string.ClearMediaCache).toUpperCase(), 0);
                    bottomSheetCell.setTextColor(Theme.STICKERS_SHEET_REMOVE_TEXT_COLOR);
                    bottomSheetCell.setOnClickListener(new C10804());
                    linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 48));
                    builder3.setCustomView(linearLayout);
                    CacheControlActivity.this.showDialog(builder3.create());
                }
            }
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return CacheControlActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == CacheControlActivity.this.databaseRow || i == CacheControlActivity.this.cacheRow || i == CacheControlActivity.this.keepMediaRow) ? 0 : (i == CacheControlActivity.this.databaseInfoRow || i == CacheControlActivity.this.cacheInfoRow || i == CacheControlActivity.this.keepMediaInfoRow) ? 1 : 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            View textSettingsCell;
            if (itemViewType == 0) {
                if (view == null) {
                    textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(-1);
                } else {
                    textSettingsCell = view;
                }
                TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                if (i == CacheControlActivity.this.databaseRow) {
                    textSettingsCell2.setTextAndValue(LocaleController.getString("LocalDatabase", C0338R.string.LocalDatabase), AndroidUtilities.formatFileSize(CacheControlActivity.this.databaseSize), false);
                    return textSettingsCell;
                } else if (i == CacheControlActivity.this.cacheRow) {
                    if (CacheControlActivity.this.calculating) {
                        textSettingsCell2.setTextAndValue(LocaleController.getString("ClearMediaCache", C0338R.string.ClearMediaCache), LocaleController.getString("CalculatingSize", C0338R.string.CalculatingSize), false);
                        return textSettingsCell;
                    }
                    textSettingsCell2.setTextAndValue(LocaleController.getString("ClearMediaCache", C0338R.string.ClearMediaCache), CacheControlActivity.this.totalSize == 0 ? LocaleController.getString("CacheEmpty", C0338R.string.CacheEmpty) : AndroidUtilities.formatFileSize(CacheControlActivity.this.totalSize), false);
                    return textSettingsCell;
                } else if (i != CacheControlActivity.this.keepMediaRow) {
                    return textSettingsCell;
                } else {
                    int i2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("keep_media", 2);
                    String formatPluralString = i2 == 0 ? LocaleController.formatPluralString("Weeks", 1) : i2 == 1 ? LocaleController.formatPluralString("Months", 1) : LocaleController.getString("KeepMediaForever", C0338R.string.KeepMediaForever);
                    textSettingsCell2.setTextAndValue(LocaleController.getString("KeepMedia", C0338R.string.KeepMedia), formatPluralString, false);
                    return textSettingsCell;
                }
            } else if (itemViewType != 1) {
                return view;
            } else {
                textSettingsCell = view == null ? new TextInfoPrivacyCell(this.mContext) : view;
                if (i == CacheControlActivity.this.databaseInfoRow) {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("LocalDatabaseInfo", C0338R.string.LocalDatabaseInfo));
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textSettingsCell;
                } else if (i == CacheControlActivity.this.cacheInfoRow) {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(TtmlNode.ANONYMOUS_REGION_ID);
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider);
                    return textSettingsCell;
                } else if (i != CacheControlActivity.this.keepMediaInfoRow) {
                    return textSettingsCell;
                } else {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(AndroidUtilities.replaceTags(LocaleController.getString("KeepMediaInfo", C0338R.string.KeepMediaInfo)));
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider);
                    return textSettingsCell;
                }
            }
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == CacheControlActivity.this.databaseRow || ((i == CacheControlActivity.this.cacheRow && CacheControlActivity.this.totalSize > 0) || i == CacheControlActivity.this.keepMediaRow);
        }
    }

    public CacheControlActivity() {
        this.databaseSize = -1;
        this.cacheSize = -1;
        this.documentsSize = -1;
        this.audioSize = -1;
        this.musicSize = -1;
        this.photoSize = -1;
        this.videoSize = -1;
        this.totalSize = -1;
        this.clear = new boolean[6];
        this.calculating = true;
        this.canceled = false;
    }

    private void cleanupFolders() {
        ProgressDialog progressDialog = new ProgressDialog(getParentActivity());
        progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Utilities.globalQueue.postRunnable(new C10732(progressDialog));
    }

    private long getDirectorySize(File file, int i) {
        Throwable th;
        if (file == null || this.canceled) {
            return 0;
        }
        long j;
        if (file.isDirectory()) {
            try {
                File[] listFiles = file.listFiles();
                if (listFiles != null) {
                    int i2 = 0;
                    j = 0;
                    while (i2 < listFiles.length) {
                        try {
                            if (this.canceled) {
                                return 0;
                            }
                            File file2 = listFiles[i2];
                            if (i == 1 || i == 2) {
                                String toLowerCase = file2.getName().toLowerCase();
                                if (toLowerCase.endsWith(".mp3") || toLowerCase.endsWith(".m4a")) {
                                    if (i == 1) {
                                        i2++;
                                    }
                                } else if (i == 2) {
                                    i2++;
                                }
                            }
                            j = file2.isDirectory() ? j + getDirectorySize(file2, i) : j + file2.length();
                            i2++;
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                }
                j = 0;
            } catch (Throwable th3) {
                Throwable th4 = th3;
                j = 0;
                th = th4;
                FileLog.m18e("tmessages", th);
                return j;
            }
        }
        if (file.isFile()) {
            j = file.length() + 0;
        }
        j = 0;
        return j;
    }

    private void initTheme(FrameLayout frameLayout) {
        if (ThemeUtil.m2490b()) {
            frameLayout.setBackgroundColor(AdvanceTheme.f2497h);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("CacheSettings", C0338R.string.CacheSettings));
        this.actionBar.setActionBarMenuOnItemClick(new C10743());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        initTheme(frameLayout);
        View listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new C10814());
        return this.fragmentView;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.keepMediaRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.keepMediaInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.cacheRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.cacheInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.databaseRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.databaseInfoRow = i;
        this.databaseSize = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db").length();
        Utilities.globalQueue.postRunnable(new C10711());
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.canceled = true;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        initThemeActionBar();
    }
}
