package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.BuildVars;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController.AlbumEntry;
import com.hanista.mobogram.messenger.MediaController.PhotoEntry;
import com.hanista.mobogram.messenger.MediaController.SearchImage;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.Request;
import com.hanista.mobogram.messenger.volley.RequestQueue;
import com.hanista.mobogram.messenger.volley.Response.ErrorListener;
import com.hanista.mobogram.messenger.volley.Response.Listener;
import com.hanista.mobogram.messenger.volley.VolleyError;
import com.hanista.mobogram.messenger.volley.toolbox.JsonObjectRequest;
import com.hanista.mobogram.messenger.volley.toolbox.Volley;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.DocumentAttribute;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.FoundGif;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_documentAttributeVideo;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_foundGifs;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_searchGifs;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.PhotoPickerPhotoCell;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.CheckBox;
import com.hanista.mobogram.ui.Components.PickerBottomLayout;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class PhotoPickerActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private boolean allowCaption;
    private ChatActivity chatActivity;
    private PhotoPickerActivityDelegate delegate;
    private TextView emptyView;
    private int giphyReqId;
    private boolean giphySearchEndReached;
    private int itemWidth;
    private String lastSearchString;
    private int lastSearchToken;
    private ListAdapter listAdapter;
    private GridView listView;
    private boolean loadingRecent;
    private int nextGiphySearchOffset;
    private String nextSearchBingString;
    private PickerBottomLayout pickerBottomLayout;
    private FrameLayout progressView;
    private ArrayList<SearchImage> recentImages;
    private RequestQueue requestQueue;
    private ActionBarMenuItem searchItem;
    private ArrayList<SearchImage> searchResult;
    private HashMap<String, SearchImage> searchResultKeys;
    private HashMap<String, SearchImage> searchResultUrls;
    private boolean searching;
    private AlbumEntry selectedAlbum;
    private HashMap<Integer, PhotoEntry> selectedPhotos;
    private HashMap<String, SearchImage> selectedWebPhotos;
    private boolean sendPressed;
    private boolean singlePhoto;
    private int type;

    public interface PhotoPickerActivityDelegate {
        void actionButtonPressed(boolean z);

        boolean didSelectVideo(String str);

        void selectedPhotosChanged();
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.12 */
    class AnonymousClass12 extends JsonObjectRequest {
        AnonymousClass12(int i, String str, JSONObject jSONObject, Listener listener, ErrorListener errorListener) {
            super(i, str, jSONObject, listener, errorListener);
        }

        public Map<String, String> getHeaders() {
            Map<String, String> hashMap = new HashMap();
            hashMap.put("Authorization", "Basic " + Base64.encodeToString((BuildVars.BING_SEARCH_KEY + ":" + BuildVars.BING_SEARCH_KEY).getBytes(), 2));
            return hashMap;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.1 */
    class C17621 extends ActionBarMenuOnItemClick {
        C17621() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PhotoPickerActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.2 */
    class C17632 extends ActionBarMenuItemSearchListener {
        C17632() {
        }

        public boolean canCollapseSearch() {
            PhotoPickerActivity.this.finishFragment();
            return false;
        }

        public void onSearchExpand() {
        }

        public void onSearchPressed(EditText editText) {
            if (editText.getText().toString().length() != 0) {
                PhotoPickerActivity.this.searchResult.clear();
                PhotoPickerActivity.this.searchResultKeys.clear();
                PhotoPickerActivity.this.nextSearchBingString = null;
                PhotoPickerActivity.this.giphySearchEndReached = true;
                if (PhotoPickerActivity.this.type == 0) {
                    PhotoPickerActivity.this.searchBingImages(editText.getText().toString(), 0, 53);
                } else if (PhotoPickerActivity.this.type == 1) {
                    PhotoPickerActivity.this.nextGiphySearchOffset = 0;
                    PhotoPickerActivity.this.searchGiphyImages(editText.getText().toString(), 0);
                }
                PhotoPickerActivity.this.lastSearchString = editText.getText().toString();
                if (PhotoPickerActivity.this.lastSearchString.length() == 0) {
                    PhotoPickerActivity.this.lastSearchString = null;
                    if (PhotoPickerActivity.this.type == 0) {
                        PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", C0338R.string.NoRecentPhotos));
                    } else if (PhotoPickerActivity.this.type == 1) {
                        PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", C0338R.string.NoRecentGIFs));
                    }
                } else {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
                }
                PhotoPickerActivity.this.updateSearchInterface();
            }
        }

        public void onTextChanged(EditText editText) {
            if (editText.getText().length() == 0) {
                PhotoPickerActivity.this.searchResult.clear();
                PhotoPickerActivity.this.searchResultKeys.clear();
                PhotoPickerActivity.this.lastSearchString = null;
                PhotoPickerActivity.this.nextSearchBingString = null;
                PhotoPickerActivity.this.giphySearchEndReached = true;
                PhotoPickerActivity.this.searching = false;
                PhotoPickerActivity.this.requestQueue.cancelAll((Object) "search");
                if (PhotoPickerActivity.this.type == 0) {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", C0338R.string.NoRecentPhotos));
                } else if (PhotoPickerActivity.this.type == 1) {
                    PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", C0338R.string.NoRecentGIFs));
                }
                PhotoPickerActivity.this.updateSearchInterface();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.3 */
    class C17643 implements OnItemClickListener {
        C17643() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (PhotoPickerActivity.this.selectedAlbum == null || !PhotoPickerActivity.this.selectedAlbum.isVideo) {
                ArrayList access$1500 = PhotoPickerActivity.this.selectedAlbum != null ? PhotoPickerActivity.this.selectedAlbum.photos : (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) ? PhotoPickerActivity.this.recentImages : PhotoPickerActivity.this.searchResult;
                if (i >= 0 && i < access$1500.size()) {
                    if (PhotoPickerActivity.this.searchItem != null) {
                        AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.searchItem.getSearchField());
                    }
                    PhotoViewer.getInstance().setParentActivity(PhotoPickerActivity.this.getParentActivity());
                    PhotoViewer.getInstance().openPhotoForSelect(access$1500, i, PhotoPickerActivity.this.singlePhoto ? 1 : 0, PhotoPickerActivity.this, PhotoPickerActivity.this.chatActivity);
                }
            } else if (i >= 0 && i < PhotoPickerActivity.this.selectedAlbum.photos.size() && PhotoPickerActivity.this.delegate.didSelectVideo(((PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i)).path)) {
                PhotoPickerActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.4 */
    class C17664 implements OnItemLongClickListener {

        /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.4.1 */
        class C17651 implements OnClickListener {
            C17651() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                PhotoPickerActivity.this.recentImages.clear();
                if (PhotoPickerActivity.this.listAdapter != null) {
                    PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                }
                MessagesStorage.getInstance().clearWebRecent(PhotoPickerActivity.this.type);
            }
        }

        C17664() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                return false;
            }
            Builder builder = new Builder(PhotoPickerActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setMessage(LocaleController.getString("ClearSearch", C0338R.string.ClearSearch));
            builder.setPositiveButton(LocaleController.getString("ClearButton", C0338R.string.ClearButton).toUpperCase(), new C17651());
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            PhotoPickerActivity.this.showDialog(builder.create());
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.5 */
    class C17675 implements OnTouchListener {
        C17675() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.6 */
    class C17686 implements OnScrollListener {
        C17686() {
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            if (i2 != 0 && i + i2 > i3 - 2 && !PhotoPickerActivity.this.searching) {
                if (PhotoPickerActivity.this.type == 0 && PhotoPickerActivity.this.nextSearchBingString != null) {
                    PhotoPickerActivity.this.searchBingImages(PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.searchResult.size(), 54);
                } else if (PhotoPickerActivity.this.type == 1 && !PhotoPickerActivity.this.giphySearchEndReached) {
                    PhotoPickerActivity.this.searchGiphyImages(PhotoPickerActivity.this.searchItem.getSearchField().getText().toString(), PhotoPickerActivity.this.nextGiphySearchOffset);
                }
            }
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 1) {
                AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.7 */
    class C17697 implements View.OnClickListener {
        C17697() {
        }

        public void onClick(View view) {
            PhotoPickerActivity.this.delegate.actionButtonPressed(true);
            PhotoPickerActivity.this.finishFragment();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.8 */
    class C17708 implements View.OnClickListener {
        C17708() {
        }

        public void onClick(View view) {
            PhotoPickerActivity.this.sendSelectedPhotos();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.9 */
    class C17729 implements RequestDelegate {
        final /* synthetic */ String val$query;
        final /* synthetic */ int val$token;

        /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.9.1 */
        class C17711 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C17711(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                boolean z = true;
                if (C17729.this.val$token == PhotoPickerActivity.this.lastSearchToken) {
                    if (this.val$response != null) {
                        TL_messages_foundGifs tL_messages_foundGifs = (TL_messages_foundGifs) this.val$response;
                        PhotoPickerActivity.this.nextGiphySearchOffset = tL_messages_foundGifs.next_offset;
                        boolean z2 = false;
                        for (int i = 0; i < tL_messages_foundGifs.results.size(); i++) {
                            FoundGif foundGif = (FoundGif) tL_messages_foundGifs.results.get(i);
                            if (!PhotoPickerActivity.this.searchResultKeys.containsKey(foundGif.url)) {
                                SearchImage searchImage = new SearchImage();
                                searchImage.id = foundGif.url;
                                if (foundGif.document != null) {
                                    for (int i2 = 0; i2 < foundGif.document.attributes.size(); i2++) {
                                        DocumentAttribute documentAttribute = (DocumentAttribute) foundGif.document.attributes.get(i2);
                                        if ((documentAttribute instanceof TL_documentAttributeImageSize) || (documentAttribute instanceof TL_documentAttributeVideo)) {
                                            searchImage.width = documentAttribute.f2659w;
                                            searchImage.height = documentAttribute.f2658h;
                                            break;
                                        }
                                    }
                                } else {
                                    searchImage.width = foundGif.f2661w;
                                    searchImage.height = foundGif.f2660h;
                                }
                                searchImage.size = 0;
                                searchImage.imageUrl = foundGif.content_url;
                                searchImage.thumbUrl = foundGif.thumb_url;
                                searchImage.localUrl = foundGif.url + "|" + C17729.this.val$query;
                                searchImage.document = foundGif.document;
                                if (!(foundGif.photo == null || foundGif.document == null)) {
                                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(foundGif.photo.sizes, PhotoPickerActivity.this.itemWidth, true);
                                    if (closestPhotoSizeWithSize != null) {
                                        foundGif.document.thumb = closestPhotoSizeWithSize;
                                    }
                                }
                                searchImage.type = 1;
                                PhotoPickerActivity.this.searchResult.add(searchImage);
                                PhotoPickerActivity.this.searchResultKeys.put(searchImage.id, searchImage);
                                z2 = true;
                            }
                        }
                        PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                        if (z2) {
                            z = false;
                        }
                        photoPickerActivity.giphySearchEndReached = z;
                    }
                    PhotoPickerActivity.this.searching = false;
                    PhotoPickerActivity.this.updateSearchInterface();
                }
            }
        }

        C17729(int i, String str) {
            this.val$token = i;
            this.val$query = str;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C17711(tLObject));
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.PhotoPickerActivity.ListAdapter.1 */
        class C17731 implements View.OnClickListener {
            C17731() {
            }

            public void onClick(View view) {
                int intValue = ((Integer) ((View) view.getParent()).getTag()).intValue();
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(intValue);
                    if (PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId))) {
                        PhotoPickerActivity.this.selectedPhotos.remove(Integer.valueOf(photoEntry.imageId));
                        photoEntry.imagePath = null;
                        photoEntry.thumbPath = null;
                        photoEntry.stickers.clear();
                        PhotoPickerActivity.this.updatePhotoAtIndex(intValue);
                    } else {
                        PhotoPickerActivity.this.selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
                    }
                    ((PhotoPickerPhotoCell) view.getParent()).setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), true);
                } else {
                    AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                    SearchImage searchImage = (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) ? (SearchImage) PhotoPickerActivity.this.recentImages.get(((Integer) ((View) view.getParent()).getTag()).intValue()) : (SearchImage) PhotoPickerActivity.this.searchResult.get(((Integer) ((View) view.getParent()).getTag()).intValue());
                    if (PhotoPickerActivity.this.selectedWebPhotos.containsKey(searchImage.id)) {
                        PhotoPickerActivity.this.selectedWebPhotos.remove(searchImage.id);
                        searchImage.imagePath = null;
                        searchImage.thumbPath = null;
                        PhotoPickerActivity.this.updatePhotoAtIndex(intValue);
                    } else {
                        PhotoPickerActivity.this.selectedWebPhotos.put(searchImage.id, searchImage);
                    }
                    ((PhotoPickerPhotoCell) view.getParent()).setChecked(PhotoPickerActivity.this.selectedWebPhotos.containsKey(searchImage.id), true);
                }
                PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size() + PhotoPickerActivity.this.selectedWebPhotos.size(), true);
                PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return PhotoPickerActivity.this.selectedAlbum != null;
        }

        public int getCount() {
            int i = 0;
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                if (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) {
                    return PhotoPickerActivity.this.recentImages.size();
                }
                int size;
                if (PhotoPickerActivity.this.type == 0) {
                    size = PhotoPickerActivity.this.searchResult.size();
                    if (PhotoPickerActivity.this.nextSearchBingString != null) {
                        i = 1;
                    }
                    return i + size;
                } else if (PhotoPickerActivity.this.type == 1) {
                    size = PhotoPickerActivity.this.searchResult.size();
                    if (!PhotoPickerActivity.this.giphySearchEndReached) {
                        i = 1;
                    }
                    return i + size;
                }
            }
            return PhotoPickerActivity.this.selectedAlbum.photos.size();
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (PhotoPickerActivity.this.selectedAlbum != null || ((PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null && i < PhotoPickerActivity.this.recentImages.size()) || i < PhotoPickerActivity.this.searchResult.size())) ? 0 : 1;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int i2 = 0;
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                View photoPickerPhotoCell;
                PhotoPickerPhotoCell photoPickerPhotoCell2;
                boolean isShowingImage;
                PhotoPickerPhotoCell photoPickerPhotoCell3 = (PhotoPickerPhotoCell) view;
                if (view == null) {
                    photoPickerPhotoCell = new PhotoPickerPhotoCell(this.mContext);
                    photoPickerPhotoCell3 = (PhotoPickerPhotoCell) photoPickerPhotoCell;
                    photoPickerPhotoCell3.checkFrame.setOnClickListener(new C17731());
                    photoPickerPhotoCell3.checkFrame.setVisibility(PhotoPickerActivity.this.singlePhoto ? 8 : 0);
                    photoPickerPhotoCell2 = photoPickerPhotoCell3;
                } else {
                    photoPickerPhotoCell2 = photoPickerPhotoCell3;
                    photoPickerPhotoCell = view;
                }
                photoPickerPhotoCell2.itemWidth = PhotoPickerActivity.this.itemWidth;
                BackupImageView backupImageView = ((PhotoPickerPhotoCell) photoPickerPhotoCell).photoImage;
                backupImageView.setTag(Integer.valueOf(i));
                photoPickerPhotoCell.setTag(Integer.valueOf(i));
                backupImageView.setOrientation(0, true);
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    PhotoEntry photoEntry = (PhotoEntry) PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                    if (photoEntry.thumbPath != null) {
                        backupImageView.setImage(photoEntry.thumbPath, null, this.mContext.getResources().getDrawable(C0338R.drawable.nophotos));
                    } else if (photoEntry.path != null) {
                        backupImageView.setOrientation(photoEntry.orientation, true);
                        if (photoEntry.isVideo) {
                            backupImageView.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, null, this.mContext.getResources().getDrawable(C0338R.drawable.nophotos));
                        } else {
                            backupImageView.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, this.mContext.getResources().getDrawable(C0338R.drawable.nophotos));
                        }
                    } else {
                        backupImageView.setImageResource(C0338R.drawable.nophotos);
                    }
                    photoPickerPhotoCell2.setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                    isShowingImage = PhotoViewer.getInstance().isShowingImage(photoEntry.path);
                } else {
                    SearchImage searchImage = (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) ? (SearchImage) PhotoPickerActivity.this.recentImages.get(i) : (SearchImage) PhotoPickerActivity.this.searchResult.get(i);
                    if (searchImage.thumbPath != null) {
                        backupImageView.setImage(searchImage.thumbPath, null, this.mContext.getResources().getDrawable(C0338R.drawable.nophotos));
                    } else if (searchImage.thumbUrl != null && searchImage.thumbUrl.length() > 0) {
                        backupImageView.setImage(searchImage.thumbUrl, null, this.mContext.getResources().getDrawable(C0338R.drawable.nophotos));
                    } else if (searchImage.document == null || searchImage.document.thumb == null) {
                        backupImageView.setImageResource(C0338R.drawable.nophotos);
                    } else {
                        backupImageView.setImage(searchImage.document.thumb.location, null, this.mContext.getResources().getDrawable(C0338R.drawable.nophotos));
                    }
                    photoPickerPhotoCell2.setChecked(PhotoPickerActivity.this.selectedWebPhotos.containsKey(searchImage.id), false);
                    isShowingImage = searchImage.document != null ? PhotoViewer.getInstance().isShowingImage(FileLoader.getPathToAttach(searchImage.document, true).getAbsolutePath()) : PhotoViewer.getInstance().isShowingImage(searchImage.imageUrl);
                }
                backupImageView.getImageReceiver().setVisible(!isShowingImage, true);
                CheckBox checkBox = photoPickerPhotoCell2.checkBox;
                if (PhotoPickerActivity.this.singlePhoto || isShowingImage) {
                    i2 = 8;
                }
                checkBox.setVisibility(i2);
                return photoPickerPhotoCell;
            } else if (itemViewType != 1) {
                return view;
            } else {
                if (view == null) {
                    view = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0338R.layout.media_loading_layout, viewGroup, false);
                }
                LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = PhotoPickerActivity.this.itemWidth;
                layoutParams.height = PhotoPickerActivity.this.itemWidth;
                view.setLayoutParams(layoutParams);
                return view;
            }
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isEmpty() {
            return PhotoPickerActivity.this.selectedAlbum != null ? PhotoPickerActivity.this.selectedAlbum.photos.isEmpty() : (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) ? PhotoPickerActivity.this.recentImages.isEmpty() : PhotoPickerActivity.this.searchResult.isEmpty();
        }

        public boolean isEnabled(int i) {
            return PhotoPickerActivity.this.selectedAlbum == null ? (PhotoPickerActivity.this.searchResult.isEmpty() && PhotoPickerActivity.this.lastSearchString == null) ? i < PhotoPickerActivity.this.recentImages.size() : i < PhotoPickerActivity.this.searchResult.size() : true;
        }
    }

    public PhotoPickerActivity(int i, AlbumEntry albumEntry, HashMap<Integer, PhotoEntry> hashMap, HashMap<String, SearchImage> hashMap2, ArrayList<SearchImage> arrayList, boolean z, boolean z2, ChatActivity chatActivity) {
        this.searchResult = new ArrayList();
        this.searchResultKeys = new HashMap();
        this.searchResultUrls = new HashMap();
        this.giphySearchEndReached = true;
        this.allowCaption = true;
        this.itemWidth = 100;
        this.selectedAlbum = albumEntry;
        this.selectedPhotos = hashMap;
        this.selectedWebPhotos = hashMap2;
        this.type = i;
        this.recentImages = arrayList;
        this.singlePhoto = z;
        this.chatActivity = chatActivity;
        this.allowCaption = z2;
        if (albumEntry != null && albumEntry.isVideo) {
            this.singlePhoto = true;
        }
    }

    private void fixLayout() {
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    PhotoPickerActivity.this.fixLayoutInternal();
                    if (PhotoPickerActivity.this.listView != null) {
                        PhotoPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private void fixLayoutInternal() {
        if (getParentActivity() != null) {
            int firstVisiblePosition = this.listView.getFirstVisiblePosition();
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            rotation = AndroidUtilities.isTablet() ? 3 : (rotation == 3 || rotation == 1) ? 5 : 3;
            this.listView.setNumColumns(rotation);
            if (AndroidUtilities.isTablet()) {
                this.itemWidth = (AndroidUtilities.dp(490.0f) - ((rotation + 1) * AndroidUtilities.dp(4.0f))) / rotation;
            } else {
                this.itemWidth = (AndroidUtilities.displaySize.x - ((rotation + 1) * AndroidUtilities.dp(4.0f))) / rotation;
            }
            this.listView.setColumnWidth(this.itemWidth);
            this.listAdapter.notifyDataSetChanged();
            this.listView.setSelection(firstVisiblePosition);
            if (this.selectedAlbum == null) {
                this.emptyView.setPadding(0, 0, 0, (int) (((float) (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight())) * 0.4f));
            }
        }
    }

    private PhotoPickerPhotoCell getCellForIndex(int i) {
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof PhotoPickerPhotoCell) {
                PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) childAt;
                int intValue = ((Integer) photoPickerPhotoCell.photoImage.getTag()).intValue();
                if (this.selectedAlbum == null) {
                    ArrayList arrayList = (this.searchResult.isEmpty() && this.lastSearchString == null) ? this.recentImages : this.searchResult;
                    if (intValue < 0) {
                        continue;
                    } else if (intValue >= arrayList.size()) {
                        continue;
                    }
                } else if (intValue < 0) {
                    continue;
                } else if (intValue >= this.selectedAlbum.photos.size()) {
                    continue;
                }
                if (intValue == i) {
                    return photoPickerPhotoCell;
                }
            }
        }
        return null;
    }

    private void searchBingImages(String str, int i, int i2) {
        boolean z = true;
        if (this.searching) {
            this.searching = false;
            if (this.giphyReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
                this.giphyReqId = 0;
            }
            this.requestQueue.cancelAll((Object) "search");
        }
        try {
            String str2;
            this.searching = true;
            if (this.nextSearchBingString != null) {
                str2 = this.nextSearchBingString;
            } else {
                String str3 = UserConfig.getCurrentUser().phone;
                if (!(str3.startsWith("44") || str3.startsWith("49") || str3.startsWith("43") || str3.startsWith("31") || str3.startsWith("1"))) {
                    z = false;
                }
                Locale locale = Locale.US;
                String str4 = "https://api.datamarket.azure.com/Bing/Search/v1/Image?Query='%s'&$skip=%d&$top=%d&$format=json%s";
                Object[] objArr = new Object[4];
                objArr[0] = URLEncoder.encode(str, C0700C.UTF8_NAME);
                objArr[1] = Integer.valueOf(i);
                objArr[2] = Integer.valueOf(i2);
                objArr[3] = z ? TtmlNode.ANONYMOUS_REGION_ID : "&Adult='Off'";
                str2 = String.format(locale, str4, objArr);
            }
            Request anonymousClass12 = new AnonymousClass12(0, str2, null, new Listener<JSONObject>() {
                public void onResponse(JSONObject jSONObject) {
                    PhotoPickerActivity.this.nextSearchBingString = null;
                    JSONObject jSONObject2 = jSONObject.getJSONObject("d");
                    JSONArray jSONArray = jSONObject2.getJSONArray("results");
                    try {
                        PhotoPickerActivity.this.nextSearchBingString = jSONObject2.getString("__next");
                    } catch (Throwable e) {
                        PhotoPickerActivity.this.nextSearchBingString = null;
                        FileLog.m18e("tmessages", e);
                    }
                    int i = 0;
                    while (i < jSONArray.length()) {
                        try {
                            try {
                                jSONObject2 = jSONArray.getJSONObject(i);
                                String MD5 = Utilities.MD5(jSONObject2.getString("MediaUrl"));
                                if (!PhotoPickerActivity.this.searchResultKeys.containsKey(MD5)) {
                                    SearchImage searchImage = new SearchImage();
                                    searchImage.id = MD5;
                                    searchImage.width = jSONObject2.getInt("Width");
                                    searchImage.height = jSONObject2.getInt("Height");
                                    searchImage.size = jSONObject2.getInt("FileSize");
                                    searchImage.imageUrl = jSONObject2.getString("MediaUrl");
                                    searchImage.thumbUrl = jSONObject2.getJSONObject("Thumbnail").getString("MediaUrl");
                                    PhotoPickerActivity.this.searchResult.add(searchImage);
                                    PhotoPickerActivity.this.searchResultKeys.put(MD5, searchImage);
                                }
                            } catch (Throwable e2) {
                                FileLog.m18e("tmessages", e2);
                            }
                            i++;
                        } catch (Throwable e22) {
                            FileLog.m18e("tmessages", e22);
                        }
                    }
                    PhotoPickerActivity.this.searching = false;
                    if (!(PhotoPickerActivity.this.nextSearchBingString == null || PhotoPickerActivity.this.nextSearchBingString.contains("json"))) {
                        PhotoPickerActivity.this.nextSearchBingString = PhotoPickerActivity.this.nextSearchBingString + "&$format=json";
                    }
                    PhotoPickerActivity.this.updateSearchInterface();
                }
            }, new ErrorListener() {
                public void onErrorResponse(VolleyError volleyError) {
                    FileLog.m16e("tmessages", "Error: " + volleyError.getMessage());
                    PhotoPickerActivity.this.nextSearchBingString = null;
                    PhotoPickerActivity.this.searching = false;
                    PhotoPickerActivity.this.updateSearchInterface();
                }
            });
            anonymousClass12.setShouldCache(false);
            anonymousClass12.setTag("search");
            this.requestQueue.add(anonymousClass12);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            this.nextSearchBingString = null;
            this.searching = false;
            updateSearchInterface();
        }
    }

    private void searchGiphyImages(String str, int i) {
        if (this.searching) {
            this.searching = false;
            if (this.giphyReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
                this.giphyReqId = 0;
            }
            this.requestQueue.cancelAll((Object) "search");
        }
        this.searching = true;
        TLObject tL_messages_searchGifs = new TL_messages_searchGifs();
        tL_messages_searchGifs.f2673q = str;
        tL_messages_searchGifs.offset = i;
        int i2 = this.lastSearchToken + 1;
        this.lastSearchToken = i2;
        this.giphyReqId = ConnectionsManager.getInstance().sendRequest(tL_messages_searchGifs, new C17729(i2, str));
        ConnectionsManager.getInstance().bindRequestToGuid(this.giphyReqId, this.classGuid);
    }

    private void sendSelectedPhotos() {
        if ((!this.selectedPhotos.isEmpty() || !this.selectedWebPhotos.isEmpty()) && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            this.delegate.actionButtonPressed(false);
            finishFragment();
        }
    }

    private void updateSearchInterface() {
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if ((this.searching && this.searchResult.isEmpty()) || (this.loadingRecent && this.lastSearchString == null)) {
            this.progressView.setVisibility(0);
            this.listView.setEmptyView(null);
            this.emptyView.setVisibility(8);
            return;
        }
        this.progressView.setVisibility(8);
        this.emptyView.setVisibility(0);
        this.listView.setEmptyView(this.emptyView);
    }

    public boolean allowCaption() {
        return this.allowCaption;
    }

    public boolean cancelButtonPressed() {
        this.delegate.actionButtonPressed(true);
        finishFragment();
        return true;
    }

    public View createView(Context context) {
        int i = 0;
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR);
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        if (this.selectedAlbum != null) {
            this.actionBar.setTitle(this.selectedAlbum.bucketName);
        } else if (this.type == 0) {
            this.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", C0338R.string.SearchImagesTitle));
        } else if (this.type == 1) {
            this.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", C0338R.string.SearchGifsTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C17621());
        if (this.selectedAlbum == null) {
            this.searchItem = this.actionBar.createMenu().addItem(0, (int) C0338R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C17632());
        }
        if (this.selectedAlbum == null) {
            if (this.type == 0) {
                this.searchItem.getSearchField().setHint(LocaleController.getString("SearchImagesTitle", C0338R.string.SearchImagesTitle));
            } else if (this.type == 1) {
                this.searchItem.getSearchField().setHint(LocaleController.getString("SearchGifsTitle", C0338R.string.SearchGifsTitle));
            }
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.MSG_TEXT_COLOR);
        this.listView = new GridView(context);
        this.listView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        this.listView.setClipToPadding(false);
        this.listView.setDrawSelectorOnTop(true);
        this.listView.setStretchMode(2);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setNumColumns(-1);
        this.listView.setVerticalSpacing(AndroidUtilities.dp(4.0f));
        this.listView.setHorizontalSpacing(AndroidUtilities.dp(4.0f));
        this.listView.setSelector(C0338R.drawable.list_selector);
        frameLayout.addView(this.listView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.bottomMargin = this.singlePhoto ? 0 : AndroidUtilities.dp(48.0f);
        this.listView.setLayoutParams(layoutParams);
        GridView gridView = this.listView;
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        gridView.setAdapter(listAdapter);
        AndroidUtilities.setListViewEdgeEffectColor(this.listView, Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.listView.setOnItemClickListener(new C17643());
        if (this.selectedAlbum == null) {
            this.listView.setOnItemLongClickListener(new C17664());
        }
        this.emptyView = new TextView(context);
        this.emptyView.setTypeface(FontUtil.m1176a().m1161d());
        this.emptyView.setTextColor(-8355712);
        this.emptyView.setTextSize(20.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        if (this.selectedAlbum != null) {
            this.emptyView.setText(LocaleController.getString("NoPhotos", C0338R.string.NoPhotos));
        } else if (this.type == 0) {
            this.emptyView.setText(LocaleController.getString("NoRecentPhotos", C0338R.string.NoRecentPhotos));
        } else if (this.type == 1) {
            this.emptyView.setText(LocaleController.getString("NoRecentGIFs", C0338R.string.NoRecentGIFs));
        }
        frameLayout.addView(this.emptyView);
        layoutParams = (FrameLayout.LayoutParams) this.emptyView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.bottomMargin = this.singlePhoto ? 0 : AndroidUtilities.dp(48.0f);
        this.emptyView.setLayoutParams(layoutParams);
        this.emptyView.setOnTouchListener(new C17675());
        if (this.selectedAlbum == null) {
            this.listView.setOnScrollListener(new C17686());
            this.progressView = new FrameLayout(context);
            this.progressView.setVisibility(8);
            frameLayout.addView(this.progressView);
            layoutParams = (FrameLayout.LayoutParams) this.progressView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            if (!this.singlePhoto) {
                i = AndroidUtilities.dp(48.0f);
            }
            layoutParams.bottomMargin = i;
            this.progressView.setLayoutParams(layoutParams);
            View progressBar = new ProgressBar(context);
            this.progressView.addView(progressBar);
            layoutParams = (FrameLayout.LayoutParams) progressBar.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -2;
            layoutParams.gravity = 17;
            progressBar.setLayoutParams(layoutParams);
            updateSearchInterface();
        }
        this.pickerBottomLayout = new PickerBottomLayout(context);
        frameLayout.addView(this.pickerBottomLayout);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.pickerBottomLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = AndroidUtilities.dp(48.0f);
        layoutParams2.gravity = 80;
        this.pickerBottomLayout.setLayoutParams(layoutParams2);
        this.pickerBottomLayout.cancelButton.setOnClickListener(new C17697());
        this.pickerBottomLayout.doneButton.setOnClickListener(new C17708());
        if (this.singlePhoto) {
            this.pickerBottomLayout.setVisibility(8);
        }
        this.listView.setEmptyView(this.emptyView);
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.recentImagesDidLoaded && this.selectedAlbum == null && this.type == ((Integer) objArr[0]).intValue()) {
            this.recentImages = (ArrayList) objArr[1];
            this.loadingRecent = false;
            updateSearchInterface();
        }
    }

    public PhotoPickerActivityDelegate getDelegate() {
        return this.delegate;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        int i2 = 0;
        PhotoPickerPhotoCell cellForIndex = getCellForIndex(i);
        if (cellForIndex == null) {
            return null;
        }
        int[] iArr = new int[2];
        cellForIndex.photoImage.getLocationInWindow(iArr);
        PlaceProviderObject placeProviderObject = new PlaceProviderObject();
        placeProviderObject.viewX = iArr[0];
        int i3 = iArr[1];
        if (VERSION.SDK_INT < 21) {
            i2 = AndroidUtilities.statusBarHeight;
        }
        placeProviderObject.viewY = i3 - i2;
        placeProviderObject.parentView = this.listView;
        placeProviderObject.imageReceiver = cellForIndex.photoImage.getImageReceiver();
        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
        placeProviderObject.scale = cellForIndex.photoImage.getScaleX();
        cellForIndex.checkBox.setVisibility(8);
        return placeProviderObject;
    }

    public int getSelectedCount() {
        return this.selectedPhotos.size() + this.selectedWebPhotos.size();
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        PhotoPickerPhotoCell cellForIndex = getCellForIndex(i);
        return cellForIndex != null ? cellForIndex.photoImage.getImageReceiver().getBitmap() : null;
    }

    public boolean isPhotoChecked(int i) {
        boolean z = true;
        if (this.selectedAlbum != null) {
            return i >= 0 && i < this.selectedAlbum.photos.size() && this.selectedPhotos.containsKey(Integer.valueOf(((PhotoEntry) this.selectedAlbum.photos.get(i)).imageId));
        } else {
            ArrayList arrayList = (this.searchResult.isEmpty() && this.lastSearchString == null) ? this.recentImages : this.searchResult;
            if (i < 0 || i >= arrayList.size() || !this.selectedWebPhotos.containsKey(((SearchImage) arrayList.get(i)).id)) {
                z = false;
            }
            return z;
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
        if (this.selectedAlbum == null) {
            this.requestQueue = Volley.newRequestQueue(ApplicationLoader.applicationContext);
            if (this.recentImages.isEmpty()) {
                MessagesStorage.getInstance().loadWebRecent(this.type);
                this.loadingRecent = true;
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentImagesDidLoaded);
        if (this.requestQueue != null) {
            this.requestQueue.cancelAll((Object) "search");
            this.requestQueue.stop();
        }
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.searchItem != null) {
            this.searchItem.openSearch(true);
            getParentActivity().getWindow().setSoftInputMode(32);
        }
        fixLayout();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.searchItem != null) {
            AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
        }
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
        if (this.selectedAlbum != null) {
            if (this.selectedPhotos.isEmpty()) {
                if (i >= 0 && i < this.selectedAlbum.photos.size()) {
                    PhotoEntry photoEntry = (PhotoEntry) this.selectedAlbum.photos.get(i);
                    this.selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
                } else {
                    return;
                }
            }
        } else if (this.selectedPhotos.isEmpty()) {
            ArrayList arrayList = (this.searchResult.isEmpty() && this.lastSearchString == null) ? this.recentImages : this.searchResult;
            if (i >= 0 && i < arrayList.size()) {
                SearchImage searchImage = (SearchImage) arrayList.get(i);
                this.selectedWebPhotos.put(searchImage.id, searchImage);
            } else {
                return;
            }
        }
        sendSelectedPhotos();
    }

    public void setDelegate(PhotoPickerActivityDelegate photoPickerActivityDelegate) {
        this.delegate = photoPickerActivityDelegate;
    }

    public void setPhotoChecked(int i) {
        boolean z;
        if (this.selectedAlbum == null) {
            ArrayList arrayList = (this.searchResult.isEmpty() && this.lastSearchString == null) ? this.recentImages : this.searchResult;
            if (i >= 0 && i < arrayList.size()) {
                SearchImage searchImage = (SearchImage) arrayList.get(i);
                if (this.selectedWebPhotos.containsKey(searchImage.id)) {
                    this.selectedWebPhotos.remove(searchImage.id);
                    z = false;
                } else {
                    this.selectedWebPhotos.put(searchImage.id, searchImage);
                    z = true;
                }
            } else {
                return;
            }
        } else if (i >= 0 && i < this.selectedAlbum.photos.size()) {
            boolean z2;
            PhotoEntry photoEntry = (PhotoEntry) this.selectedAlbum.photos.get(i);
            if (this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId))) {
                this.selectedPhotos.remove(Integer.valueOf(photoEntry.imageId));
                z2 = false;
            } else {
                this.selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
                z2 = true;
            }
            z = z2;
        } else {
            return;
        }
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (((Integer) childAt.getTag()).intValue() == i) {
                ((PhotoPickerPhotoCell) childAt).setChecked(z, false);
                break;
            }
        }
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
        this.delegate.selectedPhotosChanged();
    }

    public void updatePhotoAtIndex(int i) {
        PhotoPickerPhotoCell cellForIndex = getCellForIndex(i);
        if (cellForIndex == null) {
            return;
        }
        if (this.selectedAlbum != null) {
            cellForIndex.photoImage.setOrientation(0, true);
            PhotoEntry photoEntry = (PhotoEntry) this.selectedAlbum.photos.get(i);
            if (photoEntry.thumbPath != null) {
                cellForIndex.photoImage.setImage(photoEntry.thumbPath, null, cellForIndex.getContext().getResources().getDrawable(C0338R.drawable.nophotos));
                return;
            } else if (photoEntry.path != null) {
                cellForIndex.photoImage.setOrientation(photoEntry.orientation, true);
                if (photoEntry.isVideo) {
                    cellForIndex.photoImage.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, null, cellForIndex.getContext().getResources().getDrawable(C0338R.drawable.nophotos));
                    return;
                } else {
                    cellForIndex.photoImage.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, cellForIndex.getContext().getResources().getDrawable(C0338R.drawable.nophotos));
                    return;
                }
            } else {
                cellForIndex.photoImage.setImageResource(C0338R.drawable.nophotos);
                return;
            }
        }
        ArrayList arrayList = (this.searchResult.isEmpty() && this.lastSearchString == null) ? this.recentImages : this.searchResult;
        SearchImage searchImage = (SearchImage) arrayList.get(i);
        if (searchImage.document != null && searchImage.document.thumb != null) {
            cellForIndex.photoImage.setImage(searchImage.document.thumb.location, null, cellForIndex.getContext().getResources().getDrawable(C0338R.drawable.nophotos));
        } else if (searchImage.thumbPath != null) {
            cellForIndex.photoImage.setImage(searchImage.thumbPath, null, cellForIndex.getContext().getResources().getDrawable(C0338R.drawable.nophotos));
        } else if (searchImage.thumbUrl == null || searchImage.thumbUrl.length() <= 0) {
            cellForIndex.photoImage.setImageResource(C0338R.drawable.nophotos);
        } else {
            cellForIndex.photoImage.setImage(searchImage.thumbUrl, null, cellForIndex.getContext().getResources().getDrawable(C0338R.drawable.nophotos));
        }
    }

    public void willHidePhotoViewer() {
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt.getTag() != null) {
                PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) childAt;
                int intValue = ((Integer) childAt.getTag()).intValue();
                if (this.selectedAlbum == null) {
                    ArrayList arrayList = (this.searchResult.isEmpty() && this.lastSearchString == null) ? this.recentImages : this.searchResult;
                    if (intValue < 0) {
                        continue;
                    } else if (intValue >= arrayList.size()) {
                    }
                } else if (intValue < 0) {
                    continue;
                } else if (intValue >= this.selectedAlbum.photos.size()) {
                    continue;
                }
                if (intValue == i) {
                    photoPickerPhotoCell.checkBox.setVisibility(0);
                    return;
                }
            }
        }
    }
}
