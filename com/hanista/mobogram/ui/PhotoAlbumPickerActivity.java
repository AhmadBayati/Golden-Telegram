package com.hanista.mobogram.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.AlbumEntry;
import com.hanista.mobogram.messenger.MediaController.PhotoEntry;
import com.hanista.mobogram.messenger.MediaController.SearchImage;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC.InputDocument;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.PhotoPickerAlbumsCell;
import com.hanista.mobogram.ui.Cells.PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate;
import com.hanista.mobogram.ui.Cells.PhotoPickerSearchCell;
import com.hanista.mobogram.ui.Cells.PhotoPickerSearchCell.PhotoPickerSearchCellDelegate;
import com.hanista.mobogram.ui.Components.PickerBottomLayout;
import com.hanista.mobogram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class PhotoAlbumPickerActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int item_photos = 2;
    private static final int item_video = 3;
    private ArrayList<AlbumEntry> albumsSorted;
    private boolean allowCaption;
    private boolean allowGifs;
    private ChatActivity chatActivity;
    private int columnsCount;
    private PhotoAlbumPickerActivityDelegate delegate;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private TextView emptyView;
    private ListAdapter listAdapter;
    private ListView listView;
    private boolean loading;
    private PickerBottomLayout pickerBottomLayout;
    private FrameLayout progressView;
    private ArrayList<SearchImage> recentGifImages;
    private HashMap<String, SearchImage> recentImagesGifKeys;
    private HashMap<String, SearchImage> recentImagesWebKeys;
    private ArrayList<SearchImage> recentWebImages;
    private int selectedMode;
    private HashMap<Integer, PhotoEntry> selectedPhotos;
    private HashMap<String, SearchImage> selectedWebPhotos;
    private boolean sendPressed;
    private boolean singlePhoto;
    private ArrayList<AlbumEntry> videoAlbumsSorted;

    public interface PhotoAlbumPickerActivityDelegate {
        void didSelectPhotos(ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<ArrayList<InputDocument>> arrayList3, ArrayList<SearchImage> arrayList4);

        boolean didSelectVideo(String str);

        void startPhotoSelectActivity();
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoAlbumPickerActivity.1 */
    class C17511 extends ActionBarMenuOnItemClick {
        C17511() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PhotoAlbumPickerActivity.this.finishFragment();
            } else if (i == 1) {
                if (PhotoAlbumPickerActivity.this.delegate != null) {
                    PhotoAlbumPickerActivity.this.finishFragment(false);
                    PhotoAlbumPickerActivity.this.delegate.startPhotoSelectActivity();
                }
            } else if (i == PhotoAlbumPickerActivity.item_photos) {
                if (PhotoAlbumPickerActivity.this.selectedMode != 0) {
                    PhotoAlbumPickerActivity.this.selectedMode = 0;
                    PhotoAlbumPickerActivity.this.dropDown.setText(LocaleController.getString("PickerPhotos", C0338R.string.PickerPhotos));
                    PhotoAlbumPickerActivity.this.emptyView.setText(LocaleController.getString("NoPhotos", C0338R.string.NoPhotos));
                    PhotoAlbumPickerActivity.this.listAdapter.notifyDataSetChanged();
                }
            } else if (i == PhotoAlbumPickerActivity.item_video && PhotoAlbumPickerActivity.this.selectedMode != 1) {
                PhotoAlbumPickerActivity.this.selectedMode = 1;
                PhotoAlbumPickerActivity.this.dropDown.setText(LocaleController.getString("PickerVideo", C0338R.string.PickerVideo));
                PhotoAlbumPickerActivity.this.emptyView.setText(LocaleController.getString("NoVideo", C0338R.string.NoVideo));
                PhotoAlbumPickerActivity.this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoAlbumPickerActivity.2 */
    class C17522 implements OnClickListener {
        C17522() {
        }

        public void onClick(View view) {
            PhotoAlbumPickerActivity.this.dropDownContainer.toggleSubMenu();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoAlbumPickerActivity.3 */
    class C17533 implements OnTouchListener {
        C17533() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoAlbumPickerActivity.4 */
    class C17544 implements OnClickListener {
        C17544() {
        }

        public void onClick(View view) {
            PhotoAlbumPickerActivity.this.finishFragment();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoAlbumPickerActivity.5 */
    class C17555 implements OnClickListener {
        C17555() {
        }

        public void onClick(View view) {
            PhotoAlbumPickerActivity.this.sendSelectedPhotos();
            PhotoAlbumPickerActivity.this.finishFragment();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoAlbumPickerActivity.6 */
    class C17566 implements OnPreDrawListener {
        C17566() {
        }

        public boolean onPreDraw() {
            PhotoAlbumPickerActivity.this.fixLayoutInternal();
            if (PhotoAlbumPickerActivity.this.listView != null) {
                PhotoAlbumPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            }
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoAlbumPickerActivity.7 */
    class C17577 implements PhotoPickerActivityDelegate {
        C17577() {
        }

        public void actionButtonPressed(boolean z) {
            PhotoAlbumPickerActivity.this.removeSelfFromStack();
            if (!z) {
                PhotoAlbumPickerActivity.this.sendSelectedPhotos();
            }
        }

        public boolean didSelectVideo(String str) {
            PhotoAlbumPickerActivity.this.removeSelfFromStack();
            return PhotoAlbumPickerActivity.this.delegate.didSelectVideo(str);
        }

        public void selectedPhotosChanged() {
            if (PhotoAlbumPickerActivity.this.pickerBottomLayout != null) {
                PhotoAlbumPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoAlbumPickerActivity.this.selectedPhotos.size() + PhotoAlbumPickerActivity.this.selectedWebPhotos.size(), true);
            }
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.PhotoAlbumPickerActivity.ListAdapter.1 */
        class C17581 implements PhotoPickerAlbumsCellDelegate {
            C17581() {
            }

            public void didSelectAlbum(AlbumEntry albumEntry) {
                PhotoAlbumPickerActivity.this.openPhotoPicker(albumEntry, 0);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.PhotoAlbumPickerActivity.ListAdapter.2 */
        class C17592 implements PhotoPickerSearchCellDelegate {
            C17592() {
            }

            public void didPressedSearchButton(int i) {
                PhotoAlbumPickerActivity.this.openPhotoPicker(null, i);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public int getCount() {
            int i = 0;
            if (!PhotoAlbumPickerActivity.this.singlePhoto && PhotoAlbumPickerActivity.this.selectedMode != 0) {
                return PhotoAlbumPickerActivity.this.videoAlbumsSorted != null ? (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.videoAlbumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount))) : 0;
            } else {
                if (PhotoAlbumPickerActivity.this.singlePhoto) {
                    return PhotoAlbumPickerActivity.this.albumsSorted != null ? (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount))) : 0;
                } else {
                    if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
                        i = (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount)));
                    }
                    return i + 1;
                }
            }
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (PhotoAlbumPickerActivity.this.singlePhoto || PhotoAlbumPickerActivity.this.selectedMode == 1) ? 0 : i != 0 ? 0 : 1;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            View photoPickerAlbumsCell;
            if (itemViewType == 0) {
                PhotoPickerAlbumsCell photoPickerAlbumsCell2;
                if (view == null) {
                    photoPickerAlbumsCell = new PhotoPickerAlbumsCell(this.mContext);
                    PhotoPickerAlbumsCell photoPickerAlbumsCell3 = (PhotoPickerAlbumsCell) photoPickerAlbumsCell;
                    photoPickerAlbumsCell3.setDelegate(new C17581());
                    view = photoPickerAlbumsCell;
                    photoPickerAlbumsCell2 = photoPickerAlbumsCell3;
                } else {
                    photoPickerAlbumsCell2 = (PhotoPickerAlbumsCell) view;
                }
                photoPickerAlbumsCell2.setAlbumsCount(PhotoAlbumPickerActivity.this.columnsCount);
                int i2 = 0;
                while (i2 < PhotoAlbumPickerActivity.this.columnsCount) {
                    itemViewType = (PhotoAlbumPickerActivity.this.singlePhoto || PhotoAlbumPickerActivity.this.selectedMode == 1) ? (PhotoAlbumPickerActivity.this.columnsCount * i) + i2 : ((i - 1) * PhotoAlbumPickerActivity.this.columnsCount) + i2;
                    if (PhotoAlbumPickerActivity.this.singlePhoto || PhotoAlbumPickerActivity.this.selectedMode == 0) {
                        if (itemViewType < PhotoAlbumPickerActivity.this.albumsSorted.size()) {
                            photoPickerAlbumsCell2.setAlbum(i2, (AlbumEntry) PhotoAlbumPickerActivity.this.albumsSorted.get(itemViewType));
                        } else {
                            photoPickerAlbumsCell2.setAlbum(i2, null);
                        }
                    } else if (itemViewType < PhotoAlbumPickerActivity.this.videoAlbumsSorted.size()) {
                        photoPickerAlbumsCell2.setAlbum(i2, (AlbumEntry) PhotoAlbumPickerActivity.this.videoAlbumsSorted.get(itemViewType));
                    } else {
                        photoPickerAlbumsCell2.setAlbum(i2, null);
                    }
                    i2++;
                }
                photoPickerAlbumsCell2.requestLayout();
                return view;
            } else if (itemViewType != 1 || view != null) {
                return view;
            } else {
                photoPickerAlbumsCell = new PhotoPickerSearchCell(this.mContext, PhotoAlbumPickerActivity.this.allowGifs);
                ((PhotoPickerSearchCell) photoPickerAlbumsCell).setDelegate(new C17592());
                return photoPickerAlbumsCell;
            }
        }

        public int getViewTypeCount() {
            return (PhotoAlbumPickerActivity.this.singlePhoto || PhotoAlbumPickerActivity.this.selectedMode == 1) ? 1 : PhotoAlbumPickerActivity.item_photos;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isEmpty() {
            return getCount() == 0;
        }

        public boolean isEnabled(int i) {
            return true;
        }
    }

    public PhotoAlbumPickerActivity(boolean z, boolean z2, boolean z3, ChatActivity chatActivity) {
        this.albumsSorted = null;
        this.videoAlbumsSorted = null;
        this.selectedPhotos = new HashMap();
        this.selectedWebPhotos = new HashMap();
        this.recentImagesWebKeys = new HashMap();
        this.recentImagesGifKeys = new HashMap();
        this.recentWebImages = new ArrayList();
        this.recentGifImages = new ArrayList();
        this.loading = false;
        this.columnsCount = item_photos;
        this.chatActivity = chatActivity;
        this.singlePhoto = z;
        this.allowGifs = z2;
        this.allowCaption = z3;
    }

    private void fixLayout() {
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new C17566());
        }
    }

    private void fixLayoutInternal() {
        if (getParentActivity() != null) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.columnsCount = item_photos;
            if (!AndroidUtilities.isTablet() && (rotation == item_video || rotation == 1)) {
                this.columnsCount = 4;
            }
            this.listAdapter.notifyDataSetChanged();
            if (this.dropDownContainer != null) {
                if (!AndroidUtilities.isTablet()) {
                    LayoutParams layoutParams = (LayoutParams) this.dropDownContainer.getLayoutParams();
                    layoutParams.topMargin = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                    this.dropDownContainer.setLayoutParams(layoutParams);
                }
                if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != item_photos) {
                    this.dropDown.setTextSize(20.0f);
                } else {
                    this.dropDown.setTextSize(18.0f);
                }
            }
        }
    }

    private void openPhotoPicker(AlbumEntry albumEntry, int i) {
        ArrayList arrayList = null;
        if (albumEntry == null) {
            if (i == 0) {
                arrayList = this.recentWebImages;
            } else if (i == 1) {
                arrayList = this.recentGifImages;
            }
        }
        BaseFragment photoPickerActivity = new PhotoPickerActivity(i, albumEntry, this.selectedPhotos, this.selectedWebPhotos, arrayList, this.singlePhoto, this.allowCaption, this.chatActivity);
        photoPickerActivity.setDelegate(new C17577());
        presentFragment(photoPickerActivity);
    }

    private void sendSelectedPhotos() {
        if ((!this.selectedPhotos.isEmpty() || !this.selectedWebPhotos.isEmpty()) && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            for (Entry value : this.selectedPhotos.entrySet()) {
                PhotoEntry photoEntry = (PhotoEntry) value.getValue();
                if (photoEntry.imagePath != null) {
                    arrayList.add(photoEntry.imagePath);
                    arrayList2.add(photoEntry.caption != null ? photoEntry.caption.toString() : null);
                    arrayList3.add(!photoEntry.stickers.isEmpty() ? new ArrayList(photoEntry.stickers) : null);
                } else if (photoEntry.path != null) {
                    arrayList.add(photoEntry.path);
                    arrayList2.add(photoEntry.caption != null ? photoEntry.caption.toString() : null);
                    arrayList3.add(!photoEntry.stickers.isEmpty() ? new ArrayList(photoEntry.stickers) : null);
                }
            }
            ArrayList arrayList4 = new ArrayList();
            Object obj = null;
            Object obj2 = null;
            for (Entry value2 : this.selectedWebPhotos.entrySet()) {
                Object obj3;
                Object obj4;
                SearchImage searchImage = (SearchImage) value2.getValue();
                if (searchImage.imagePath != null) {
                    arrayList.add(searchImage.imagePath);
                    arrayList2.add(searchImage.caption != null ? searchImage.caption.toString() : null);
                    arrayList3.add(!searchImage.stickers.isEmpty() ? new ArrayList(searchImage.stickers) : null);
                } else {
                    arrayList4.add(searchImage);
                }
                searchImage.date = (int) (System.currentTimeMillis() / 1000);
                SearchImage searchImage2;
                if (searchImage.type == 0) {
                    searchImage2 = (SearchImage) this.recentImagesWebKeys.get(searchImage.id);
                    if (searchImage2 != null) {
                        this.recentWebImages.remove(searchImage2);
                        this.recentWebImages.add(0, searchImage2);
                    } else {
                        this.recentWebImages.add(0, searchImage);
                    }
                    obj3 = 1;
                    obj4 = obj2;
                } else {
                    if (searchImage.type == 1) {
                        obj2 = 1;
                        searchImage2 = (SearchImage) this.recentImagesGifKeys.get(searchImage.id);
                        if (searchImage2 != null) {
                            this.recentGifImages.remove(searchImage2);
                            this.recentGifImages.add(0, searchImage2);
                            obj3 = obj;
                            int i = 1;
                        } else {
                            this.recentGifImages.add(0, searchImage);
                        }
                    }
                    obj3 = obj;
                    obj4 = obj2;
                }
                obj = obj3;
                obj2 = obj4;
            }
            if (obj != null) {
                MessagesStorage.getInstance().putWebRecent(this.recentWebImages);
            }
            if (obj2 != null) {
                MessagesStorage.getInstance().putWebRecent(this.recentGifImages);
            }
            this.delegate.didSelectPhotos(arrayList, arrayList2, arrayList3, arrayList4);
        }
    }

    public View createView(Context context) {
        LayoutParams layoutParams;
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR);
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new C17511());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.addItem(1, (int) C0338R.drawable.ic_ab_other);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.MSG_TEXT_COLOR);
        if (this.singlePhoto) {
            this.actionBar.setTitle(LocaleController.getString("Gallery", C0338R.string.Gallery));
        } else {
            this.selectedMode = 0;
            this.dropDownContainer = new ActionBarMenuItem(context, createMenu, 0);
            this.dropDownContainer.setSubMenuOpenSide(1);
            this.dropDownContainer.addSubItem(item_photos, LocaleController.getString("PickerPhotos", C0338R.string.PickerPhotos), 0);
            this.dropDownContainer.addSubItem(item_video, LocaleController.getString("PickerVideo", C0338R.string.PickerVideo), 0);
            this.actionBar.addView(this.dropDownContainer);
            layoutParams = (LayoutParams) this.dropDownContainer.getLayoutParams();
            layoutParams.height = -1;
            layoutParams.width = -2;
            layoutParams.rightMargin = AndroidUtilities.dp(40.0f);
            layoutParams.leftMargin = AndroidUtilities.isTablet() ? AndroidUtilities.dp(64.0f) : AndroidUtilities.dp(56.0f);
            layoutParams.gravity = 51;
            this.dropDownContainer.setLayoutParams(layoutParams);
            this.dropDownContainer.setOnClickListener(new C17522());
            this.dropDown = new TextView(context);
            this.dropDown.setGravity(item_video);
            this.dropDown.setSingleLine(true);
            this.dropDown.setLines(1);
            this.dropDown.setMaxLines(1);
            this.dropDown.setEllipsize(TruncateAt.END);
            this.dropDown.setTextColor(-1);
            this.dropDown.setTypeface(FontUtil.m1176a().m1160c());
            this.dropDown.setCompoundDrawablesWithIntrinsicBounds(0, 0, C0338R.drawable.ic_arrow_drop_down, 0);
            this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
            this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
            this.dropDown.setText(LocaleController.getString("PickerPhotos", C0338R.string.PickerPhotos));
            this.dropDownContainer.addView(this.dropDown);
            layoutParams = (LayoutParams) this.dropDown.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -2;
            layoutParams.leftMargin = AndroidUtilities.dp(16.0f);
            layoutParams.gravity = 16;
            this.dropDown.setLayoutParams(layoutParams);
        }
        this.listView = new ListView(context);
        this.listView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setSelector(new ColorDrawable(0));
        this.listView.setDividerHeight(0);
        this.listView.setDivider(null);
        this.listView.setDrawingCacheEnabled(false);
        this.listView.setScrollingCacheEnabled(false);
        frameLayout.addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
        this.listView.setLayoutParams(layoutParams);
        ListView listView = this.listView;
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        AndroidUtilities.setListViewEdgeEffectColor(this.listView, Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.emptyView = new TextView(context);
        this.emptyView.setTextColor(-8355712);
        this.emptyView.setTextSize(20.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.emptyView.setText(LocaleController.getString("NoPhotos", C0338R.string.NoPhotos));
        frameLayout.addView(this.emptyView);
        layoutParams = (LayoutParams) this.emptyView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
        this.emptyView.setLayoutParams(layoutParams);
        this.emptyView.setOnTouchListener(new C17533());
        this.progressView = new FrameLayout(context);
        this.progressView.setVisibility(8);
        frameLayout.addView(this.progressView);
        layoutParams = (LayoutParams) this.progressView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
        this.progressView.setLayoutParams(layoutParams);
        this.progressView.addView(new ProgressBar(context));
        layoutParams = (LayoutParams) this.progressView.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.gravity = 17;
        this.progressView.setLayoutParams(layoutParams);
        this.pickerBottomLayout = new PickerBottomLayout(context);
        frameLayout.addView(this.pickerBottomLayout);
        LayoutParams layoutParams2 = (LayoutParams) this.pickerBottomLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = AndroidUtilities.dp(48.0f);
        layoutParams2.gravity = 80;
        this.pickerBottomLayout.setLayoutParams(layoutParams2);
        this.pickerBottomLayout.cancelButton.setOnClickListener(new C17544());
        this.pickerBottomLayout.doneButton.setOnClickListener(new C17555());
        if (!this.loading || (this.albumsSorted != null && (this.albumsSorted == null || !this.albumsSorted.isEmpty()))) {
            this.progressView.setVisibility(8);
            this.listView.setEmptyView(this.emptyView);
        } else {
            this.progressView.setVisibility(0);
            this.listView.setEmptyView(null);
        }
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.albumsDidLoaded) {
            if (this.classGuid == ((Integer) objArr[0]).intValue()) {
                this.albumsSorted = (ArrayList) objArr[1];
                this.videoAlbumsSorted = (ArrayList) objArr[item_video];
                if (this.progressView != null) {
                    this.progressView.setVisibility(8);
                }
                if (this.listView != null && this.listView.getEmptyView() == null) {
                    this.listView.setEmptyView(this.emptyView);
                }
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
                this.loading = false;
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.recentImagesDidLoaded) {
            int intValue = ((Integer) objArr[0]).intValue();
            Iterator it;
            SearchImage searchImage;
            if (intValue == 0) {
                this.recentWebImages = (ArrayList) objArr[1];
                this.recentImagesWebKeys.clear();
                it = this.recentWebImages.iterator();
                while (it.hasNext()) {
                    searchImage = (SearchImage) it.next();
                    this.recentImagesWebKeys.put(searchImage.id, searchImage);
                }
            } else if (intValue == 1) {
                this.recentGifImages = (ArrayList) objArr[1];
                this.recentImagesGifKeys.clear();
                it = this.recentGifImages.iterator();
                while (it.hasNext()) {
                    searchImage = (SearchImage) it.next();
                    this.recentImagesGifKeys.put(searchImage.id, searchImage);
                }
            }
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public boolean onFragmentCreate() {
        this.loading = true;
        MediaController.m111e(this.classGuid);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.albumsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.albumsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentImagesDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        super.onFragmentDestroy();
    }

    public void onPause() {
        super.onPause();
        if (this.dropDownContainer != null) {
            this.dropDownContainer.closeSubMenu();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        fixLayout();
    }

    public void setDelegate(PhotoAlbumPickerActivityDelegate photoAlbumPickerActivityDelegate) {
        this.delegate = photoAlbumPickerActivityDelegate;
    }
}
