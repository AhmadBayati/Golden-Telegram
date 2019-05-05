package com.hanista.mobogram.ui;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.AudioEntry;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.tgnet.TLRPC.Document;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.Peer;
import com.hanista.mobogram.tgnet.TLRPC.TL_document;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeAudio;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeFilename;
import com.hanista.mobogram.tgnet.TLRPC.TL_message;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_photoSizeEmpty;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.AudioCell;
import com.hanista.mobogram.ui.Cells.AudioCell.AudioCellDelegate;
import com.hanista.mobogram.ui.Components.EmptyTextProgressView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.PickerBottomLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class AudioSelectActivity extends BaseFragment implements NotificationCenterDelegate {
    private ArrayList<AudioEntry> audioEntries;
    private PickerBottomLayout bottomLayout;
    private AudioSelectActivityDelegate delegate;
    private ListAdapter listViewAdapter;
    private boolean loadingAudio;
    private MessageObject playingAudio;
    private EmptyTextProgressView progressView;
    private HashMap<Long, AudioEntry> selectedAudios;

    /* renamed from: com.hanista.mobogram.ui.AudioSelectActivity.1 */
    class C10581 extends ActionBarMenuOnItemClick {
        C10581() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                AudioSelectActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.AudioSelectActivity.2 */
    class C10592 implements OnItemClickListener {
        C10592() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            AudioCell audioCell = (AudioCell) view;
            AudioEntry audioEntry = audioCell.getAudioEntry();
            if (AudioSelectActivity.this.selectedAudios.containsKey(Long.valueOf(audioEntry.id))) {
                AudioSelectActivity.this.selectedAudios.remove(Long.valueOf(audioEntry.id));
                audioCell.setChecked(false);
            } else {
                AudioSelectActivity.this.selectedAudios.put(Long.valueOf(audioEntry.id), audioEntry);
                audioCell.setChecked(true);
            }
            AudioSelectActivity.this.updateBottomLayoutCount();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.AudioSelectActivity.3 */
    class C10603 implements OnClickListener {
        C10603() {
        }

        public void onClick(View view) {
            AudioSelectActivity.this.finishFragment();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.AudioSelectActivity.4 */
    class C10614 implements OnClickListener {
        C10614() {
        }

        public void onClick(View view) {
            if (AudioSelectActivity.this.delegate != null) {
                ArrayList arrayList = new ArrayList();
                for (Entry value : AudioSelectActivity.this.selectedAudios.entrySet()) {
                    arrayList.add(((AudioEntry) value.getValue()).messageObject);
                }
                AudioSelectActivity.this.delegate.didSelectAudio(arrayList);
            }
            AudioSelectActivity.this.finishFragment();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.AudioSelectActivity.5 */
    class C10635 implements Runnable {

        /* renamed from: com.hanista.mobogram.ui.AudioSelectActivity.5.1 */
        class C10621 implements Runnable {
            final /* synthetic */ ArrayList val$newAudioEntries;

            C10621(ArrayList arrayList) {
                this.val$newAudioEntries = arrayList;
            }

            public void run() {
                AudioSelectActivity.this.audioEntries = this.val$newAudioEntries;
                AudioSelectActivity.this.progressView.showTextView();
                AudioSelectActivity.this.listViewAdapter.notifyDataSetChanged();
            }
        }

        C10635() {
        }

        public void run() {
            Cursor query;
            Throwable e;
            String[] strArr = new String[]{"_id", "artist", "title", "_data", "duration", "album"};
            ArrayList arrayList = new ArrayList();
            try {
                query = ApplicationLoader.applicationContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, strArr, "is_music != 0", null, "title");
                int i = -2000000000;
                while (query.moveToNext()) {
                    try {
                        AudioEntry audioEntry = new AudioEntry();
                        audioEntry.id = (long) query.getInt(0);
                        audioEntry.author = query.getString(1);
                        audioEntry.title = query.getString(2);
                        audioEntry.path = query.getString(3);
                        audioEntry.duration = (int) (query.getLong(4) / 1000);
                        audioEntry.genre = query.getString(5);
                        File file = new File(audioEntry.path);
                        Message tL_message = new TL_message();
                        tL_message.out = true;
                        tL_message.id = i;
                        tL_message.to_id = new TL_peerUser();
                        Peer peer = tL_message.to_id;
                        int clientUserId = UserConfig.getClientUserId();
                        tL_message.from_id = clientUserId;
                        peer.user_id = clientUserId;
                        tL_message.date = (int) (System.currentTimeMillis() / 1000);
                        tL_message.message = "-1";
                        tL_message.attachPath = audioEntry.path;
                        tL_message.media = new TL_messageMediaDocument();
                        tL_message.media.document = new TL_document();
                        tL_message.flags |= 768;
                        String fileExtension = FileLoader.getFileExtension(file);
                        tL_message.media.document.id = 0;
                        tL_message.media.document.access_hash = 0;
                        tL_message.media.document.date = tL_message.date;
                        Document document = tL_message.media.document;
                        StringBuilder append = new StringBuilder().append("audio/");
                        if (fileExtension.length() <= 0) {
                            fileExtension = "mp3";
                        }
                        document.mime_type = append.append(fileExtension).toString();
                        tL_message.media.document.size = (int) file.length();
                        tL_message.media.document.thumb = new TL_photoSizeEmpty();
                        tL_message.media.document.thumb.type = "s";
                        tL_message.media.document.dc_id = 0;
                        TL_documentAttributeAudio tL_documentAttributeAudio = new TL_documentAttributeAudio();
                        tL_documentAttributeAudio.duration = audioEntry.duration;
                        tL_documentAttributeAudio.title = audioEntry.title;
                        tL_documentAttributeAudio.performer = audioEntry.author;
                        tL_documentAttributeAudio.flags |= 3;
                        tL_message.media.document.attributes.add(tL_documentAttributeAudio);
                        TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
                        tL_documentAttributeFilename.file_name = file.getName();
                        tL_message.media.document.attributes.add(tL_documentAttributeFilename);
                        audioEntry.messageObject = new MessageObject(tL_message, null, false);
                        arrayList.add(audioEntry);
                        i--;
                    } catch (Exception e2) {
                        e = e2;
                    }
                }
                if (query != null) {
                    query.close();
                }
            } catch (Exception e3) {
                e = e3;
                query = null;
                try {
                    FileLog.m18e("tmessages", e);
                    if (query != null) {
                        query.close();
                    }
                    AndroidUtilities.runOnUIThread(new C10621(arrayList));
                } catch (Throwable th) {
                    e = th;
                    if (query != null) {
                        query.close();
                    }
                    throw e;
                }
            } catch (Throwable th2) {
                e = th2;
                query = null;
                if (query != null) {
                    query.close();
                }
                throw e;
            }
            AndroidUtilities.runOnUIThread(new C10621(arrayList));
        }
    }

    public interface AudioSelectActivityDelegate {
        void didSelectAudio(ArrayList<MessageObject> arrayList);
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.AudioSelectActivity.ListAdapter.1 */
        class C10641 implements AudioCellDelegate {
            C10641() {
            }

            public void startedPlayingAudio(MessageObject messageObject) {
                AudioSelectActivity.this.playingAudio = messageObject;
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public int getCount() {
            return AudioSelectActivity.this.audioEntries.size();
        }

        public Object getItem(int i) {
            return AudioSelectActivity.this.audioEntries.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            getItemViewType(i);
            if (view == null) {
                View audioCell = new AudioCell(this.mContext);
                ((AudioCell) audioCell).setDelegate(new C10641());
                view2 = audioCell;
            } else {
                view2 = view;
            }
            ((AudioCell) view2).setAudio((AudioEntry) AudioSelectActivity.this.audioEntries.get(i), i != AudioSelectActivity.this.audioEntries.size() + -1, AudioSelectActivity.this.selectedAudios.containsKey(Long.valueOf(((AudioEntry) AudioSelectActivity.this.audioEntries.get(i)).id)));
            return view2;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return AudioSelectActivity.this.audioEntries.isEmpty();
        }

        public boolean isEnabled(int i) {
            return true;
        }
    }

    public AudioSelectActivity() {
        this.audioEntries = new ArrayList();
        this.selectedAudios = new HashMap();
    }

    private void loadAudio() {
        this.loadingAudio = true;
        if (this.progressView != null) {
            this.progressView.showProgress();
        }
        Utilities.globalQueue.postRunnable(new C10635());
    }

    private void updateBottomLayoutCount() {
        this.bottomLayout.updateSelectedCount(this.selectedAudios.size(), true);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AttachMusic", C0338R.string.AttachMusic));
        this.actionBar.setActionBarMenuOnItemClick(new C10581());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.progressView = new EmptyTextProgressView(context);
        this.progressView.setText(LocaleController.getString("NoAudio", C0338R.string.NoAudio));
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        View listView = new ListView(context);
        listView.setEmptyView(this.progressView);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        listView.setOnItemClickListener(new C10592());
        this.bottomLayout = new PickerBottomLayout(context, false);
        frameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.bottomLayout.cancelButton.setOnClickListener(new C10603());
        this.bottomLayout.doneButton.setOnClickListener(new C10614());
        listView = new View(context);
        listView.setBackgroundResource(C0338R.drawable.header_shadow_reverse);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        if (this.loadingAudio) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        updateBottomLayoutCount();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.audioDidReset && this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        loadAudio();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        if (this.playingAudio != null && MediaController.m71a().m172d(this.playingAudio)) {
            MediaController.m71a().m155a(true, true);
        }
    }

    public void setDelegate(AudioSelectActivityDelegate audioSelectActivityDelegate) {
        this.delegate = audioSelectActivityDelegate;
    }
}
