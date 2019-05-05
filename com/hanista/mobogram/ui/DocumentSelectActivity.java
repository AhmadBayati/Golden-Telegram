package com.hanista.mobogram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BackDrawable;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.SharedDocumentCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.NumberTextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

public class DocumentSelectActivity extends BaseFragment {
    private static final int done = 3;
    private ArrayList<View> actionModeViews;
    private File currentDir;
    private DocumentSelectActivityDelegate delegate;
    private TextView emptyView;
    private ArrayList<HistoryEntry> history;
    private ArrayList<ListItem> items;
    private ListAdapter listAdapter;
    private ListView listView;
    private BroadcastReceiver receiver;
    private boolean receiverRegistered;
    private boolean scrolling;
    private HashMap<String, ListItem> selectedFiles;
    private NumberTextView selectedMessagesCountTextView;
    private long sizeLimit;

    public interface DocumentSelectActivityDelegate {
        void didSelectFiles(DocumentSelectActivity documentSelectActivity, ArrayList<String> arrayList);

        void startDocumentSelectActivity();
    }

    /* renamed from: com.hanista.mobogram.ui.DocumentSelectActivity.1 */
    class C15491 extends BroadcastReceiver {

        /* renamed from: com.hanista.mobogram.ui.DocumentSelectActivity.1.1 */
        class C15481 implements Runnable {
            C15481() {
            }

            public void run() {
                try {
                    if (DocumentSelectActivity.this.currentDir == null) {
                        DocumentSelectActivity.this.listRoots();
                    } else {
                        DocumentSelectActivity.this.listFiles(DocumentSelectActivity.this.currentDir);
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        C15491() {
        }

        public void onReceive(Context context, Intent intent) {
            Runnable c15481 = new C15481();
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                DocumentSelectActivity.this.listView.postDelayed(c15481, 1000);
            } else {
                c15481.run();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DocumentSelectActivity.2 */
    class C15502 extends ActionBarMenuOnItemClick {
        C15502() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                    DocumentSelectActivity.this.selectedFiles.clear();
                    DocumentSelectActivity.this.actionBar.hideActionMode();
                    DocumentSelectActivity.this.listView.invalidateViews();
                    return;
                }
                DocumentSelectActivity.this.finishFragment();
            } else if (i == DocumentSelectActivity.done && DocumentSelectActivity.this.delegate != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(DocumentSelectActivity.this.selectedFiles.keySet());
                DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, arrayList);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DocumentSelectActivity.3 */
    class C15513 implements OnTouchListener {
        C15513() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DocumentSelectActivity.4 */
    class C15524 implements OnTouchListener {
        C15524() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DocumentSelectActivity.5 */
    class C15535 implements OnScrollListener {
        C15535() {
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            DocumentSelectActivity.this.scrolling = i != 0;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DocumentSelectActivity.6 */
    class C15546 implements OnItemLongClickListener {
        C15546() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (DocumentSelectActivity.this.actionBar.isActionModeShowed() || i < 0 || i >= DocumentSelectActivity.this.items.size()) {
                return false;
            }
            ListItem listItem = (ListItem) DocumentSelectActivity.this.items.get(i);
            File file = listItem.file;
            if (!(file == null || file.isDirectory())) {
                if (!file.canRead()) {
                    DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", C0338R.string.AccessError));
                    return false;
                } else if (DocumentSelectActivity.this.sizeLimit != 0 && file.length() > DocumentSelectActivity.this.sizeLimit) {
                    DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", C0338R.string.FileUploadLimit, AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit)));
                    return false;
                } else if (file.length() == 0) {
                    return false;
                } else {
                    DocumentSelectActivity.this.selectedFiles.put(file.toString(), listItem);
                    DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(1, false);
                    AnimatorSet animatorSet = new AnimatorSet();
                    Collection arrayList = new ArrayList();
                    for (int i2 = 0; i2 < DocumentSelectActivity.this.actionModeViews.size(); i2++) {
                        View view2 = (View) DocumentSelectActivity.this.actionModeViews.get(i2);
                        AndroidUtilities.clearDrawableAnimation(view2);
                        arrayList.add(ObjectAnimator.ofFloat(view2, "scaleY", new float[]{0.1f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                    }
                    animatorSet.playTogether(arrayList);
                    animatorSet.setDuration(250);
                    animatorSet.start();
                    DocumentSelectActivity.this.scrolling = false;
                    if (view instanceof SharedDocumentCell) {
                        ((SharedDocumentCell) view).setChecked(true, true);
                    }
                    DocumentSelectActivity.this.actionBar.showActionMode();
                }
            }
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DocumentSelectActivity.7 */
    class C15557 implements OnItemClickListener {
        C15557() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (i >= 0 && i < DocumentSelectActivity.this.items.size()) {
                ListItem listItem = (ListItem) DocumentSelectActivity.this.items.get(i);
                File file = listItem.file;
                if (file == null) {
                    if (listItem.icon == C0338R.drawable.ic_storage_gallery) {
                        if (DocumentSelectActivity.this.delegate != null) {
                            DocumentSelectActivity.this.delegate.startDocumentSelectActivity();
                        }
                        DocumentSelectActivity.this.finishFragment(false);
                        return;
                    }
                    HistoryEntry historyEntry = (HistoryEntry) DocumentSelectActivity.this.history.remove(DocumentSelectActivity.this.history.size() - 1);
                    DocumentSelectActivity.this.actionBar.setTitle(historyEntry.title);
                    if (historyEntry.dir != null) {
                        DocumentSelectActivity.this.listFiles(historyEntry.dir);
                    } else {
                        DocumentSelectActivity.this.listRoots();
                    }
                    DocumentSelectActivity.this.listView.setSelectionFromTop(historyEntry.scrollItem, historyEntry.scrollOffset);
                } else if (file.isDirectory()) {
                    HistoryEntry historyEntry2 = new HistoryEntry(null);
                    historyEntry2.scrollItem = DocumentSelectActivity.this.listView.getFirstVisiblePosition();
                    historyEntry2.scrollOffset = DocumentSelectActivity.this.listView.getChildAt(0).getTop();
                    historyEntry2.dir = DocumentSelectActivity.this.currentDir;
                    historyEntry2.title = DocumentSelectActivity.this.actionBar.getTitle();
                    DocumentSelectActivity.this.history.add(historyEntry2);
                    if (DocumentSelectActivity.this.listFiles(file)) {
                        DocumentSelectActivity.this.actionBar.setTitle(listItem.title);
                        DocumentSelectActivity.this.listView.setSelection(0);
                        return;
                    }
                    DocumentSelectActivity.this.history.remove(historyEntry2);
                } else {
                    if (!file.canRead()) {
                        DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", C0338R.string.AccessError));
                        file = new File("/mnt/sdcard");
                    }
                    if (DocumentSelectActivity.this.sizeLimit != 0 && file.length() > DocumentSelectActivity.this.sizeLimit) {
                        DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", C0338R.string.FileUploadLimit, AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit)));
                    } else if (file.length() == 0) {
                    } else {
                        if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                            if (DocumentSelectActivity.this.selectedFiles.containsKey(file.toString())) {
                                DocumentSelectActivity.this.selectedFiles.remove(file.toString());
                            } else {
                                DocumentSelectActivity.this.selectedFiles.put(file.toString(), listItem);
                            }
                            if (DocumentSelectActivity.this.selectedFiles.isEmpty()) {
                                DocumentSelectActivity.this.actionBar.hideActionMode();
                            } else {
                                DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(DocumentSelectActivity.this.selectedFiles.size(), true);
                            }
                            DocumentSelectActivity.this.scrolling = false;
                            if (view instanceof SharedDocumentCell) {
                                ((SharedDocumentCell) view).setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(listItem.file.toString()), true);
                            }
                        } else if (DocumentSelectActivity.this.delegate != null) {
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(file.getAbsolutePath());
                            DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, arrayList);
                        }
                    }
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DocumentSelectActivity.8 */
    class C15568 implements OnPreDrawListener {
        C15568() {
        }

        public boolean onPreDraw() {
            DocumentSelectActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            DocumentSelectActivity.this.fixLayoutInternal();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.DocumentSelectActivity.9 */
    class C15579 implements Comparator<File> {
        C15579() {
        }

        public int compare(File file, File file2) {
            return file.isDirectory() != file2.isDirectory() ? file.isDirectory() ? -1 : 1 : file.getName().compareToIgnoreCase(file2.getName());
        }
    }

    private class HistoryEntry {
        File dir;
        int scrollItem;
        int scrollOffset;
        String title;

        private HistoryEntry() {
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getCount() {
            return DocumentSelectActivity.this.items.size();
        }

        public Object getItem(int i) {
            return DocumentSelectActivity.this.items.get(i);
        }

        public long getItemId(int i) {
            return 0;
        }

        public int getItemViewType(int i) {
            return ((ListItem) DocumentSelectActivity.this.items.get(i)).subtitle.length() > 0 ? 0 : 1;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = true;
            View sharedDocumentCell = view == null ? new SharedDocumentCell(this.mContext) : view;
            SharedDocumentCell sharedDocumentCell2 = (SharedDocumentCell) sharedDocumentCell;
            ListItem listItem = (ListItem) DocumentSelectActivity.this.items.get(i);
            if (listItem.icon != 0) {
                ((SharedDocumentCell) sharedDocumentCell).setTextAndValueAndTypeAndThumb(listItem.title, listItem.subtitle, null, null, listItem.icon);
            } else {
                ((SharedDocumentCell) sharedDocumentCell).setTextAndValueAndTypeAndThumb(listItem.title, listItem.subtitle, listItem.ext.toUpperCase().substring(0, Math.min(listItem.ext.length(), 4)), listItem.thumb, 0);
            }
            if (listItem.file == null || !DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                if (DocumentSelectActivity.this.scrolling) {
                    z = false;
                }
                sharedDocumentCell2.setChecked(false, z);
            } else {
                sharedDocumentCell2.setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(listItem.file.toString()), !DocumentSelectActivity.this.scrolling);
            }
            return sharedDocumentCell;
        }

        public int getViewTypeCount() {
            return 2;
        }
    }

    private class ListItem {
        String ext;
        File file;
        int icon;
        String subtitle;
        String thumb;
        String title;

        private ListItem() {
            this.subtitle = TtmlNode.ANONYMOUS_REGION_ID;
            this.ext = TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public DocumentSelectActivity() {
        this.items = new ArrayList();
        this.receiverRegistered = false;
        this.history = new ArrayList();
        this.sizeLimit = 1610612736;
        this.selectedFiles = new HashMap();
        this.actionModeViews = new ArrayList();
        this.receiver = new C15491();
    }

    private void fixLayoutInternal() {
        if (this.selectedMessagesCountTextView != null) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
        }
    }

    private String getRootSubtitle(String str) {
        try {
            StatFs statFs = new StatFs(str);
            long blockCount = ((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize());
            long blockSize = ((long) statFs.getBlockSize()) * ((long) statFs.getAvailableBlocks());
            if (blockCount == 0) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            return LocaleController.formatString("FreeOfTotal", C0338R.string.FreeOfTotal, AndroidUtilities.formatFileSize(blockSize), AndroidUtilities.formatFileSize(blockCount));
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return str;
        }
    }

    private void initThemeBackButton() {
        if (ThemeUtil.m2490b()) {
            Drawable backDrawable = new BackDrawable(false);
            ((BackDrawable) backDrawable).setColor(AdvanceTheme.bi);
            this.actionBar.setBackgroundColor(AdvanceTheme.f2491b);
            this.actionBar.setBackButtonDrawable(backDrawable);
        }
    }

    private boolean listFiles(File file) {
        if (file.canRead()) {
            this.emptyView.setText(LocaleController.getString("NoFiles", C0338R.string.NoFiles));
            try {
                File[] listFiles = file.listFiles();
                if (listFiles == null) {
                    showErrorBox(LocaleController.getString("UnknownError", C0338R.string.UnknownError));
                    return false;
                }
                this.currentDir = file;
                this.items.clear();
                Arrays.sort(listFiles, new C15579());
                for (File file2 : listFiles) {
                    if (file2.getName().indexOf(46) != 0) {
                        ListItem listItem = new ListItem();
                        listItem.title = file2.getName();
                        listItem.file = file2;
                        if (file2.isDirectory()) {
                            listItem.icon = C0338R.drawable.ic_directory;
                            listItem.subtitle = LocaleController.getString("Folder", C0338R.string.Folder);
                        } else {
                            String name = file2.getName();
                            String[] split = name.split("\\.");
                            listItem.ext = split.length > 1 ? split[split.length - 1] : "?";
                            listItem.subtitle = AndroidUtilities.formatFileSize(file2.length());
                            String toLowerCase = name.toLowerCase();
                            if (toLowerCase.endsWith(".jpg") || toLowerCase.endsWith(".png") || toLowerCase.endsWith(".gif") || toLowerCase.endsWith(".jpeg")) {
                                listItem.thumb = file2.getAbsolutePath();
                            }
                        }
                        this.items.add(listItem);
                    }
                }
                ListItem listItem2 = new ListItem();
                listItem2.title = "..";
                if (this.history.size() > 0) {
                    HistoryEntry historyEntry = (HistoryEntry) this.history.get(this.history.size() - 1);
                    if (historyEntry.dir == null) {
                        listItem2.subtitle = LocaleController.getString("Folder", C0338R.string.Folder);
                    } else {
                        listItem2.subtitle = historyEntry.dir.toString();
                    }
                } else {
                    listItem2.subtitle = LocaleController.getString("Folder", C0338R.string.Folder);
                }
                listItem2.icon = C0338R.drawable.ic_directory;
                listItem2.file = null;
                this.items.add(0, listItem2);
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                this.listAdapter.notifyDataSetChanged();
                return true;
            } catch (Exception e) {
                showErrorBox(e.getLocalizedMessage());
                return false;
            }
        } else if ((!file.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString()) && !file.getAbsolutePath().startsWith("/sdcard") && !file.getAbsolutePath().startsWith("/mnt/sdcard")) || Environment.getExternalStorageState().equals("mounted") || Environment.getExternalStorageState().equals("mounted_ro")) {
            showErrorBox(LocaleController.getString("AccessError", C0338R.string.AccessError));
            return false;
        } else {
            this.currentDir = file;
            this.items.clear();
            if ("shared".equals(Environment.getExternalStorageState())) {
                this.emptyView.setText(LocaleController.getString("UsbActive", C0338R.string.UsbActive));
            } else {
                this.emptyView.setText(LocaleController.getString("NotMounted", C0338R.string.NotMounted));
            }
            AndroidUtilities.clearDrawableAnimation(this.listView);
            this.scrolling = true;
            this.listAdapter.notifyDataSetChanged();
            return true;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.SuppressLint({"NewApi"})
    private void listRoots() {
        /*
        r8 = this;
        r6 = 2131166207; // 0x7f0703ff float:1.7946653E38 double:1.0529360085E-314;
        r5 = 2130837836; // 0x7f02014c float:1.7280637E38 double:1.0527737716E-314;
        r7 = 2130837824; // 0x7f020140 float:1.7280613E38 double:1.0527737657E-314;
        r2 = 0;
        r8.currentDir = r2;
        r0 = r8.items;
        r0.clear();
        r4 = new java.util.HashSet;
        r4.<init>();
        r0 = android.os.Environment.getExternalStorageDirectory();
        r0 = r0.getPath();
        android.os.Environment.isExternalStorageRemovable();
        r1 = android.os.Environment.getExternalStorageState();
        r3 = "mounted";
        r3 = r1.equals(r3);
        if (r3 != 0) goto L_0x0037;
    L_0x002e:
        r3 = "mounted_ro";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0061;
    L_0x0037:
        r1 = new com.hanista.mobogram.ui.DocumentSelectActivity$ListItem;
        r1.<init>(r2);
        r3 = android.os.Environment.isExternalStorageRemovable();
        if (r3 == 0) goto L_0x01eb;
    L_0x0042:
        r3 = "SdCard";
        r3 = com.hanista.mobogram.messenger.LocaleController.getString(r3, r6);
        r1.title = r3;
        r1.icon = r5;
    L_0x004d:
        r3 = r8.getRootSubtitle(r0);
        r1.subtitle = r3;
        r3 = android.os.Environment.getExternalStorageDirectory();
        r1.file = r3;
        r3 = r8.items;
        r3.add(r1);
        r4.add(r0);
    L_0x0061:
        r1 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x0240, all -> 0x023d }
        r0 = new java.io.FileReader;	 Catch:{ Exception -> 0x0240, all -> 0x023d }
        r3 = "/proc/mounts";
        r0.<init>(r3);	 Catch:{ Exception -> 0x0240, all -> 0x023d }
        r1.<init>(r0);	 Catch:{ Exception -> 0x0240, all -> 0x023d }
    L_0x006e:
        r0 = r1.readLine();	 Catch:{ Exception -> 0x0154 }
        if (r0 == 0) goto L_0x0213;
    L_0x0074:
        r3 = "vfat";
        r3 = r0.contains(r3);	 Catch:{ Exception -> 0x0154 }
        if (r3 != 0) goto L_0x0086;
    L_0x007d:
        r3 = "/mnt";
        r3 = r0.contains(r3);	 Catch:{ Exception -> 0x0154 }
        if (r3 == 0) goto L_0x006e;
    L_0x0086:
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m16e(r3, r0);	 Catch:{ Exception -> 0x0154 }
        r3 = new java.util.StringTokenizer;	 Catch:{ Exception -> 0x0154 }
        r5 = " ";
        r3.<init>(r0, r5);	 Catch:{ Exception -> 0x0154 }
        r3.nextToken();	 Catch:{ Exception -> 0x0154 }
        r3 = r3.nextToken();	 Catch:{ Exception -> 0x0154 }
        r5 = r4.contains(r3);	 Catch:{ Exception -> 0x0154 }
        if (r5 != 0) goto L_0x006e;
    L_0x00a1:
        r5 = "/dev/block/vold";
        r5 = r0.contains(r5);	 Catch:{ Exception -> 0x0154 }
        if (r5 == 0) goto L_0x006e;
    L_0x00aa:
        r5 = "/mnt/secure";
        r5 = r0.contains(r5);	 Catch:{ Exception -> 0x0154 }
        if (r5 != 0) goto L_0x006e;
    L_0x00b3:
        r5 = "/mnt/asec";
        r5 = r0.contains(r5);	 Catch:{ Exception -> 0x0154 }
        if (r5 != 0) goto L_0x006e;
    L_0x00bc:
        r5 = "/mnt/obb";
        r5 = r0.contains(r5);	 Catch:{ Exception -> 0x0154 }
        if (r5 != 0) goto L_0x006e;
    L_0x00c5:
        r5 = "/dev/mapper";
        r5 = r0.contains(r5);	 Catch:{ Exception -> 0x0154 }
        if (r5 != 0) goto L_0x006e;
    L_0x00ce:
        r5 = "tmpfs";
        r0 = r0.contains(r5);	 Catch:{ Exception -> 0x0154 }
        if (r0 != 0) goto L_0x006e;
    L_0x00d7:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x0154 }
        r0.<init>(r3);	 Catch:{ Exception -> 0x0154 }
        r0 = r0.isDirectory();	 Catch:{ Exception -> 0x0154 }
        if (r0 != 0) goto L_0x0244;
    L_0x00e2:
        r0 = 47;
        r0 = r3.lastIndexOf(r0);	 Catch:{ Exception -> 0x0154 }
        r5 = -1;
        if (r0 == r5) goto L_0x0244;
    L_0x00eb:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0154 }
        r5.<init>();	 Catch:{ Exception -> 0x0154 }
        r6 = "/storage/";
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0154 }
        r0 = r0 + 1;
        r0 = r3.substring(r0);	 Catch:{ Exception -> 0x0154 }
        r0 = r5.append(r0);	 Catch:{ Exception -> 0x0154 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0154 }
        r5 = new java.io.File;	 Catch:{ Exception -> 0x0154 }
        r5.<init>(r0);	 Catch:{ Exception -> 0x0154 }
        r5 = r5.isDirectory();	 Catch:{ Exception -> 0x0154 }
        if (r5 == 0) goto L_0x0244;
    L_0x0110:
        r4.add(r0);	 Catch:{ Exception -> 0x0154 }
        r3 = new com.hanista.mobogram.ui.DocumentSelectActivity$ListItem;	 Catch:{ Exception -> 0x014b }
        r5 = 0;
        r3.<init>(r5);	 Catch:{ Exception -> 0x014b }
        r5 = r0.toLowerCase();	 Catch:{ Exception -> 0x014b }
        r6 = "sd";
        r5 = r5.contains(r6);	 Catch:{ Exception -> 0x014b }
        if (r5 == 0) goto L_0x01fe;
    L_0x0126:
        r5 = "SdCard";
        r6 = 2131166207; // 0x7f0703ff float:1.7946653E38 double:1.0529360085E-314;
        r5 = com.hanista.mobogram.messenger.LocaleController.getString(r5, r6);	 Catch:{ Exception -> 0x014b }
        r3.title = r5;	 Catch:{ Exception -> 0x014b }
    L_0x0132:
        r5 = 2130837836; // 0x7f02014c float:1.7280637E38 double:1.0527737716E-314;
        r3.icon = r5;	 Catch:{ Exception -> 0x014b }
        r5 = r8.getRootSubtitle(r0);	 Catch:{ Exception -> 0x014b }
        r3.subtitle = r5;	 Catch:{ Exception -> 0x014b }
        r5 = new java.io.File;	 Catch:{ Exception -> 0x014b }
        r5.<init>(r0);	 Catch:{ Exception -> 0x014b }
        r3.file = r5;	 Catch:{ Exception -> 0x014b }
        r0 = r8.items;	 Catch:{ Exception -> 0x014b }
        r0.add(r3);	 Catch:{ Exception -> 0x014b }
        goto L_0x006e;
    L_0x014b:
        r0 = move-exception;
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r0);	 Catch:{ Exception -> 0x0154 }
        goto L_0x006e;
    L_0x0154:
        r0 = move-exception;
    L_0x0155:
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r0);	 Catch:{ all -> 0x020c }
        if (r1 == 0) goto L_0x0160;
    L_0x015d:
        r1.close();	 Catch:{ Exception -> 0x0223 }
    L_0x0160:
        r0 = new com.hanista.mobogram.ui.DocumentSelectActivity$ListItem;
        r0.<init>(r2);
        r1 = "/";
        r0.title = r1;
        r1 = "SystemRoot";
        r3 = 2131166326; // 0x7f070476 float:1.7946894E38 double:1.0529360673E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r3);
        r0.subtitle = r1;
        r0.icon = r7;
        r1 = new java.io.File;
        r3 = "/";
        r1.<init>(r3);
        r0.file = r1;
        r1 = r8.items;
        r1.add(r0);
        r0 = new java.io.File;	 Catch:{ Exception -> 0x0234 }
        r1 = com.hanista.mobogram.mobo.MoboConstants.m1381b();	 Catch:{ Exception -> 0x0234 }
        r3 = com.hanista.mobogram.mobo.MoboConstants.f1325R;	 Catch:{ Exception -> 0x0234 }
        r0.<init>(r1, r3);	 Catch:{ Exception -> 0x0234 }
        r1 = r0.exists();	 Catch:{ Exception -> 0x0234 }
        if (r1 == 0) goto L_0x01b4;
    L_0x0198:
        r1 = new com.hanista.mobogram.ui.DocumentSelectActivity$ListItem;	 Catch:{ Exception -> 0x0234 }
        r3 = 0;
        r1.<init>(r3);	 Catch:{ Exception -> 0x0234 }
        r3 = com.hanista.mobogram.mobo.MoboConstants.f1325R;	 Catch:{ Exception -> 0x0234 }
        r1.title = r3;	 Catch:{ Exception -> 0x0234 }
        r3 = r0.toString();	 Catch:{ Exception -> 0x0234 }
        r1.subtitle = r3;	 Catch:{ Exception -> 0x0234 }
        r3 = 2130837824; // 0x7f020140 float:1.7280613E38 double:1.0527737657E-314;
        r1.icon = r3;	 Catch:{ Exception -> 0x0234 }
        r1.file = r0;	 Catch:{ Exception -> 0x0234 }
        r0 = r8.items;	 Catch:{ Exception -> 0x0234 }
        r0.add(r1);	 Catch:{ Exception -> 0x0234 }
    L_0x01b4:
        r0 = new com.hanista.mobogram.ui.DocumentSelectActivity$ListItem;
        r0.<init>(r2);
        r1 = "Gallery";
        r3 = 2131165714; // 0x7f070212 float:1.7945653E38 double:1.052935765E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r3);
        r0.title = r1;
        r1 = "GalleryInfo";
        r3 = 2131165715; // 0x7f070213 float:1.7945655E38 double:1.0529357654E-314;
        r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r3);
        r0.subtitle = r1;
        r1 = 2130837998; // 0x7f0201ee float:1.7280966E38 double:1.0527738517E-314;
        r0.icon = r1;
        r0.file = r2;
        r1 = r8.items;
        r1.add(r0);
        r0 = r8.listView;
        com.hanista.mobogram.messenger.AndroidUtilities.clearDrawableAnimation(r0);
        r0 = 1;
        r8.scrolling = r0;
        r0 = r8.listAdapter;
        r0.notifyDataSetChanged();
        return;
    L_0x01eb:
        r3 = "InternalStorage";
        r5 = 2131165755; // 0x7f07023b float:1.7945736E38 double:1.052935785E-314;
        r3 = com.hanista.mobogram.messenger.LocaleController.getString(r3, r5);
        r1.title = r3;
        r3 = 2130837997; // 0x7f0201ed float:1.7280964E38 double:1.052773851E-314;
        r1.icon = r3;
        goto L_0x004d;
    L_0x01fe:
        r5 = "ExternalStorage";
        r6 = 2131165631; // 0x7f0701bf float:1.7945485E38 double:1.052935724E-314;
        r5 = com.hanista.mobogram.messenger.LocaleController.getString(r5, r6);	 Catch:{ Exception -> 0x014b }
        r3.title = r5;	 Catch:{ Exception -> 0x014b }
        goto L_0x0132;
    L_0x020c:
        r0 = move-exception;
    L_0x020d:
        if (r1 == 0) goto L_0x0212;
    L_0x020f:
        r1.close();	 Catch:{ Exception -> 0x022c }
    L_0x0212:
        throw r0;
    L_0x0213:
        if (r1 == 0) goto L_0x0160;
    L_0x0215:
        r1.close();	 Catch:{ Exception -> 0x021a }
        goto L_0x0160;
    L_0x021a:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
        goto L_0x0160;
    L_0x0223:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
        goto L_0x0160;
    L_0x022c:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x0212;
    L_0x0234:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
        goto L_0x01b4;
    L_0x023d:
        r0 = move-exception;
        r1 = r2;
        goto L_0x020d;
    L_0x0240:
        r0 = move-exception;
        r1 = r2;
        goto L_0x0155;
    L_0x0244:
        r0 = r3;
        goto L_0x0110;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.DocumentSelectActivity.listRoots():void");
    }

    private void showErrorBox(String str) {
        if (getParentActivity() != null) {
            new Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(str).setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null).show();
        }
    }

    public View createView(Context context) {
        if (!this.receiverRegistered) {
            this.receiverRegistered = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
            intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
            intentFilter.addAction("android.intent.action.MEDIA_EJECT");
            intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
            intentFilter.addAction("android.intent.action.MEDIA_NOFS");
            intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
            intentFilter.addAction("android.intent.action.MEDIA_SHARED");
            intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
            intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            intentFilter.addDataScheme("file");
            ApplicationLoader.applicationContext.registerReceiver(this.receiver, intentFilter);
        }
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        initThemeBackButton();
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SelectFile", C0338R.string.SelectFile));
        this.actionBar.setActionBarMenuOnItemClick(new C15502());
        this.selectedFiles.clear();
        this.actionModeViews.clear();
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(createActionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.selectedMessagesCountTextView.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        this.selectedMessagesCountTextView.setOnTouchListener(new C15513());
        createActionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 65, 0, 0, 0));
        this.actionModeViews.add(createActionMode.addItem(done, C0338R.drawable.ic_ab_done_gray, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.fragmentView = getParentActivity().getLayoutInflater().inflate(C0338R.layout.document_select_layout, null, false);
        this.listAdapter = new ListAdapter(context);
        this.emptyView = (TextView) this.fragmentView.findViewById(C0338R.id.searchEmptyView);
        this.emptyView.setOnTouchListener(new C15524());
        this.listView = (ListView) this.fragmentView.findViewById(C0338R.id.listView);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnScrollListener(new C15535());
        this.listView.setOnItemLongClickListener(new C15546());
        this.listView.setOnItemClickListener(new C15557());
        listRoots();
        return this.fragmentView;
    }

    public boolean onBackPressed() {
        if (this.history.size() <= 0) {
            return super.onBackPressed();
        }
        HistoryEntry historyEntry = (HistoryEntry) this.history.remove(this.history.size() - 1);
        this.actionBar.setTitle(historyEntry.title);
        if (historyEntry.dir != null) {
            listFiles(historyEntry.dir);
        } else {
            listRoots();
        }
        this.listView.setSelectionFromTop(historyEntry.scrollItem, historyEntry.scrollOffset);
        return false;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new C15568());
        }
    }

    public void onFragmentDestroy() {
        try {
            if (this.receiverRegistered) {
                ApplicationLoader.applicationContext.unregisterReceiver(this.receiver);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        fixLayoutInternal();
    }

    public void setDelegate(DocumentSelectActivityDelegate documentSelectActivityDelegate) {
        this.delegate = documentSelectActivityDelegate;
    }
}
