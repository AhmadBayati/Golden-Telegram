package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.exoplayer.ExoPlayer.Factory;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.ContactsAdapter;
import com.hanista.mobogram.ui.Adapters.SearchAdapter;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.ChipSpan;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.LetterSectionsListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GroupCreateActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private static final int select_all_button = 2;
    private ArrayList<ChipSpan> allSpans;
    private int beforeChangeIndex;
    private CharSequence changeString;
    private int chatType;
    private GroupCreateActivityDelegate delegate;
    private TextView emptyTextView;
    private boolean ignoreChange;
    private boolean isAlwaysShare;
    private boolean isDelete;
    private boolean isGroup;
    private boolean isNeverShare;
    private LetterSectionsListView listView;
    private ContactsAdapter listViewAdapter;
    private int maxCount;
    ProgressDialog progressDialog;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private HashMap<Integer, ChipSpan> selectedContacts;
    private EditText userSelectEditText;

    public interface GroupCreateActivityDelegate {
        void didSelectUsers(ArrayList<Integer> arrayList);
    }

    /* renamed from: com.hanista.mobogram.ui.GroupCreateActivity.1 */
    class C15631 extends ActionBarMenuOnItemClick {
        C15631() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                GroupCreateActivity.this.finishFragment();
            } else if (i == GroupCreateActivity.done_button) {
                if (!GroupCreateActivity.this.selectedContacts.isEmpty()) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.addAll(GroupCreateActivity.this.selectedContacts.keySet());
                    if (GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare || GroupCreateActivity.this.isDelete) {
                        if (GroupCreateActivity.this.delegate != null) {
                            GroupCreateActivity.this.delegate.didSelectUsers(arrayList);
                        }
                        GroupCreateActivity.this.finishFragment();
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putIntegerArrayList("result", arrayList);
                    bundle.putInt("chatType", GroupCreateActivity.this.chatType);
                    GroupCreateActivity.this.presentFragment(new GroupCreateFinalActivity(bundle));
                }
            } else if (i == GroupCreateActivity.select_all_button) {
                GroupCreateActivity.this.selectAll();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.GroupCreateActivity.2 */
    class C15642 implements TextWatcher {
        C15642() {
        }

        public void afterTextChanged(Editable editable) {
            if (!GroupCreateActivity.this.ignoreChange) {
                String str;
                boolean z;
                int selectionEnd = GroupCreateActivity.this.userSelectEditText.getSelectionEnd();
                if (editable.toString().length() < GroupCreateActivity.this.changeString.toString().length()) {
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    try {
                        str = GroupCreateActivity.this.changeString.toString().substring(selectionEnd, GroupCreateActivity.this.beforeChangeIndex);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                    if (str.length() > 0) {
                        z = GroupCreateActivity.this.searching && GroupCreateActivity.this.searchWas;
                        Spannable text = GroupCreateActivity.this.userSelectEditText.getText();
                        for (int i = 0; i < GroupCreateActivity.this.allSpans.size(); i += GroupCreateActivity.done_button) {
                            ChipSpan chipSpan = (ChipSpan) GroupCreateActivity.this.allSpans.get(i);
                            if (text.getSpanStart(chipSpan) == -1) {
                                GroupCreateActivity.this.allSpans.remove(chipSpan);
                                GroupCreateActivity.this.selectedContacts.remove(Integer.valueOf(chipSpan.uid));
                            }
                        }
                        if (!(GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare || GroupCreateActivity.this.isDelete)) {
                            ActionBar access$1500 = GroupCreateActivity.this.actionBar;
                            Object[] objArr = new Object[GroupCreateActivity.select_all_button];
                            objArr[0] = Integer.valueOf(GroupCreateActivity.this.selectedContacts.size());
                            objArr[GroupCreateActivity.done_button] = Integer.valueOf(GroupCreateActivity.this.maxCount);
                            access$1500.setSubtitle(LocaleController.formatString("MembersCount", C0338R.string.MembersCount, objArr));
                        }
                        GroupCreateActivity.this.listView.invalidateViews();
                    } else {
                        z = true;
                    }
                } else {
                    z = true;
                }
                if (z) {
                    str = GroupCreateActivity.this.userSelectEditText.getText().toString().replace("<", TtmlNode.ANONYMOUS_REGION_ID);
                    if (str.length() != 0) {
                        GroupCreateActivity.this.searching = true;
                        GroupCreateActivity.this.searchWas = true;
                        if (GroupCreateActivity.this.listView != null) {
                            GroupCreateActivity.this.listView.setAdapter(GroupCreateActivity.this.searchListViewAdapter);
                            GroupCreateActivity.this.searchListViewAdapter.notifyDataSetChanged();
                            GroupCreateActivity.this.listView.setFastScrollAlwaysVisible(false);
                            GroupCreateActivity.this.listView.setFastScrollEnabled(false);
                            GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
                        }
                        if (GroupCreateActivity.this.emptyTextView != null) {
                            GroupCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
                        }
                        GroupCreateActivity.this.searchListViewAdapter.searchDialogs(str);
                        return;
                    }
                    GroupCreateActivity.this.searchListViewAdapter.searchDialogs(null);
                    GroupCreateActivity.this.searching = false;
                    GroupCreateActivity.this.searchWas = false;
                    GroupCreateActivity.this.listView.setAdapter(GroupCreateActivity.this.listViewAdapter);
                    GroupCreateActivity.this.listViewAdapter.notifyDataSetChanged();
                    GroupCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
                    GroupCreateActivity.this.listView.setFastScrollEnabled(true);
                    GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
                    GroupCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
                }
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!GroupCreateActivity.this.ignoreChange) {
                GroupCreateActivity.this.beforeChangeIndex = GroupCreateActivity.this.userSelectEditText.getSelectionStart();
                GroupCreateActivity.this.changeString = new SpannableString(charSequence);
            }
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.GroupCreateActivity.3 */
    class C15653 implements OnTouchListener {
        C15653() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.GroupCreateActivity.4 */
    class C15664 implements OnItemClickListener {
        C15664() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            User user;
            if (GroupCreateActivity.this.searching && GroupCreateActivity.this.searchWas) {
                user = (User) GroupCreateActivity.this.searchListViewAdapter.getItem(i);
            } else {
                int sectionForPosition = GroupCreateActivity.this.listViewAdapter.getSectionForPosition(i);
                int positionInSectionForPosition = GroupCreateActivity.this.listViewAdapter.getPositionInSectionForPosition(i);
                if (positionInSectionForPosition >= 0 && sectionForPosition >= 0) {
                    user = (User) GroupCreateActivity.this.listViewAdapter.getItem(sectionForPosition, positionInSectionForPosition);
                } else {
                    return;
                }
            }
            if (user != null) {
                Object spannableStringBuilder;
                boolean z;
                if (GroupCreateActivity.this.selectedContacts.containsKey(Integer.valueOf(user.id))) {
                    try {
                        ChipSpan chipSpan = (ChipSpan) GroupCreateActivity.this.selectedContacts.get(Integer.valueOf(user.id));
                        GroupCreateActivity.this.selectedContacts.remove(Integer.valueOf(user.id));
                        spannableStringBuilder = new SpannableStringBuilder(GroupCreateActivity.this.userSelectEditText.getText());
                        spannableStringBuilder.delete(spannableStringBuilder.getSpanStart(chipSpan), spannableStringBuilder.getSpanEnd(chipSpan));
                        GroupCreateActivity.this.allSpans.remove(chipSpan);
                        GroupCreateActivity.this.ignoreChange = true;
                        GroupCreateActivity.this.userSelectEditText.setText(spannableStringBuilder);
                        GroupCreateActivity.this.userSelectEditText.setSelection(spannableStringBuilder.length());
                        GroupCreateActivity.this.ignoreChange = false;
                        z = false;
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                        z = false;
                    }
                } else if (GroupCreateActivity.this.maxCount != 0 && GroupCreateActivity.this.selectedContacts.size() == GroupCreateActivity.this.maxCount) {
                    return;
                } else {
                    if (GroupCreateActivity.this.chatType == 0 && GroupCreateActivity.this.selectedContacts.size() == MessagesController.getInstance().maxGroupCount) {
                        Builder builder = new Builder(GroupCreateActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setMessage(LocaleController.getString("SoftUserLimitAlert", C0338R.string.SoftUserLimitAlert));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                        GroupCreateActivity.this.showDialog(builder.create());
                        return;
                    }
                    GroupCreateActivity.this.ignoreChange = true;
                    GroupCreateActivity.this.createAndPutChipForUser(user).uid = user.id;
                    GroupCreateActivity.this.ignoreChange = false;
                    z = true;
                }
                if (!(GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare || GroupCreateActivity.this.isDelete)) {
                    ActionBar access$2100 = GroupCreateActivity.this.actionBar;
                    Object[] objArr = new Object[GroupCreateActivity.select_all_button];
                    objArr[0] = Integer.valueOf(GroupCreateActivity.this.selectedContacts.size());
                    objArr[GroupCreateActivity.done_button] = Integer.valueOf(GroupCreateActivity.this.maxCount);
                    access$2100.setSubtitle(LocaleController.formatString("MembersCount", C0338R.string.MembersCount, objArr));
                }
                if (GroupCreateActivity.this.searching || GroupCreateActivity.this.searchWas) {
                    GroupCreateActivity.this.ignoreChange = true;
                    spannableStringBuilder = new SpannableStringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
                    Iterator it = GroupCreateActivity.this.allSpans.iterator();
                    while (it.hasNext()) {
                        ImageSpan imageSpan = (ImageSpan) it.next();
                        spannableStringBuilder.append("<<");
                        spannableStringBuilder.setSpan(imageSpan, spannableStringBuilder.length() - 2, spannableStringBuilder.length(), 33);
                    }
                    GroupCreateActivity.this.userSelectEditText.setText(spannableStringBuilder);
                    GroupCreateActivity.this.userSelectEditText.setSelection(spannableStringBuilder.length());
                    GroupCreateActivity.this.ignoreChange = false;
                    GroupCreateActivity.this.searchListViewAdapter.searchDialogs(null);
                    GroupCreateActivity.this.searching = false;
                    GroupCreateActivity.this.searchWas = false;
                    GroupCreateActivity.this.listView.setAdapter(GroupCreateActivity.this.listViewAdapter);
                    GroupCreateActivity.this.listViewAdapter.notifyDataSetChanged();
                    GroupCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
                    GroupCreateActivity.this.listView.setFastScrollEnabled(true);
                    GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
                    GroupCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
                } else if (view instanceof UserCell) {
                    ((UserCell) view).setChecked(z, true);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.GroupCreateActivity.5 */
    class C15675 implements OnScrollListener {
        C15675() {
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            if (absListView.isFastScrollEnabled()) {
                AndroidUtilities.clearDrawableAnimation(absListView);
            }
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            boolean z = true;
            if (i == GroupCreateActivity.done_button) {
                AndroidUtilities.hideKeyboard(GroupCreateActivity.this.userSelectEditText);
            }
            if (GroupCreateActivity.this.listViewAdapter != null) {
                ContactsAdapter access$1900 = GroupCreateActivity.this.listViewAdapter;
                if (i == 0) {
                    z = false;
                }
                access$1900.setIsScrolling(z);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.GroupCreateActivity.6 */
    class C15686 implements Runnable {
        C15686() {
        }

        public void run() {
            GroupCreateActivity.this.selectedContacts.clear();
            GroupCreateActivity.this.allSpans.clear();
            GroupCreateActivity.this.ignoreChange = true;
            int i;
            User user;
            if (GroupCreateActivity.this.searching && GroupCreateActivity.this.searchWas) {
                for (i = 0; i < GroupCreateActivity.this.searchListViewAdapter.getCount(); i += GroupCreateActivity.done_button) {
                    user = (User) GroupCreateActivity.this.searchListViewAdapter.getItem(i);
                    if (user != null) {
                        GroupCreateActivity.this.createAndPutChipForUser(user).uid = user.id;
                    }
                }
            } else {
                for (i = 0; i < GroupCreateActivity.this.listViewAdapter.getSectionCount(); i += GroupCreateActivity.done_button) {
                    for (int i2 = 0; i2 < GroupCreateActivity.this.listViewAdapter.getCountForSection(i); i2 += GroupCreateActivity.done_button) {
                        user = (User) GroupCreateActivity.this.listViewAdapter.getItem(i, i2);
                        if (user != null) {
                            GroupCreateActivity.this.createAndPutChipForUser(user).uid = user.id;
                        }
                    }
                }
            }
            GroupCreateActivity.this.ignoreChange = false;
            if (!(GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare || GroupCreateActivity.this.isDelete)) {
                ActionBar access$2200 = GroupCreateActivity.this.actionBar;
                Object[] objArr = new Object[GroupCreateActivity.select_all_button];
                objArr[0] = Integer.valueOf(GroupCreateActivity.this.selectedContacts.size());
                objArr[GroupCreateActivity.done_button] = Integer.valueOf(GroupCreateActivity.this.maxCount);
                access$2200.setSubtitle(LocaleController.formatString("MembersCount", C0338R.string.MembersCount, objArr));
            }
            if (GroupCreateActivity.this.searching || GroupCreateActivity.this.searchWas) {
                GroupCreateActivity.this.ignoreChange = true;
                Object spannableStringBuilder = new SpannableStringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
                Iterator it = GroupCreateActivity.this.allSpans.iterator();
                while (it.hasNext()) {
                    ImageSpan imageSpan = (ImageSpan) it.next();
                    spannableStringBuilder.append("<<");
                    spannableStringBuilder.setSpan(imageSpan, spannableStringBuilder.length() - 2, spannableStringBuilder.length(), 33);
                }
                GroupCreateActivity.this.userSelectEditText.setText(spannableStringBuilder);
                GroupCreateActivity.this.userSelectEditText.setSelection(spannableStringBuilder.length());
                GroupCreateActivity.this.ignoreChange = false;
                GroupCreateActivity.this.searchListViewAdapter.searchDialogs(null);
                GroupCreateActivity.this.searching = false;
                GroupCreateActivity.this.searchWas = false;
                GroupCreateActivity.this.listView.setAdapter(GroupCreateActivity.this.listViewAdapter);
                GroupCreateActivity.this.listViewAdapter.notifyDataSetChanged();
                if (VERSION.SDK_INT >= 11) {
                    GroupCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
                }
                GroupCreateActivity.this.listView.setFastScrollEnabled(true);
                GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
                GroupCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
            }
            GroupCreateActivity.this.progressDialog.dismiss();
        }
    }

    public GroupCreateActivity() {
        this.maxCount = Factory.DEFAULT_MIN_REBUFFER_MS;
        this.chatType = 0;
        this.selectedContacts = new HashMap();
        this.allSpans = new ArrayList();
    }

    public GroupCreateActivity(Bundle bundle) {
        super(bundle);
        this.maxCount = Factory.DEFAULT_MIN_REBUFFER_MS;
        this.chatType = 0;
        this.selectedContacts = new HashMap();
        this.allSpans = new ArrayList();
        this.chatType = bundle.getInt("chatType", 0);
        this.isAlwaysShare = bundle.getBoolean("isAlwaysShare", false);
        this.isNeverShare = bundle.getBoolean("isNeverShare", false);
        this.isGroup = bundle.getBoolean("isGroup", false);
        this.isDelete = bundle.getBoolean("isDelete", false);
        this.maxCount = this.chatType == 0 ? MessagesController.getInstance().maxMegagroupCount : MessagesController.getInstance().maxBroadcastCount;
    }

    private ChipSpan createAndPutChipForUser(User user) {
        View inflate = ((LayoutInflater) ApplicationLoader.applicationContext.getSystemService("layout_inflater")).inflate(C0338R.layout.group_create_bubble, null);
        TextView textView = (TextView) inflate.findViewById(C0338R.id.bubble_text_view);
        String userName = UserObject.getUserName(user);
        if (!(userName.length() != 0 || user.phone == null || user.phone.length() == 0)) {
            userName = PhoneFormat.getInstance().format("+" + user.phone);
        }
        textView.setText(userName + ", ");
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
        inflate.measure(makeMeasureSpec, makeMeasureSpec);
        inflate.layout(0, 0, inflate.getMeasuredWidth(), inflate.getMeasuredHeight());
        Bitmap createBitmap = Bitmap.createBitmap(inflate.getWidth(), inflate.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.translate((float) (-inflate.getScrollX()), (float) (-inflate.getScrollY()));
        inflate.draw(canvas);
        inflate.setDrawingCacheEnabled(true);
        inflate.getDrawingCache().copy(Config.ARGB_8888, true);
        inflate.destroyDrawingCache();
        Drawable bitmapDrawable = new BitmapDrawable(createBitmap);
        bitmapDrawable.setBounds(0, 0, createBitmap.getWidth(), createBitmap.getHeight());
        Object spannableStringBuilder = new SpannableStringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
        ChipSpan chipSpan = new ChipSpan(bitmapDrawable, done_button);
        this.allSpans.add(chipSpan);
        this.selectedContacts.put(Integer.valueOf(user.id), chipSpan);
        Iterator it = this.allSpans.iterator();
        while (it.hasNext()) {
            ImageSpan imageSpan = (ImageSpan) it.next();
            spannableStringBuilder.append("<<");
            spannableStringBuilder.setSpan(imageSpan, spannableStringBuilder.length() - 2, spannableStringBuilder.length(), 33);
        }
        this.userSelectEditText.setText(spannableStringBuilder);
        this.userSelectEditText.setSelection(spannableStringBuilder.length());
        return chipSpan;
    }

    private void initTheme() {
        if (ThemeUtil.m2490b() && this.userSelectEditText != null) {
            if (this.userSelectEditText.getBackground() != null) {
                this.userSelectEditText.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
            }
            this.userSelectEditText.setHintTextColor(AdvanceTheme.f2495f);
            this.userSelectEditText.setTextColor(AdvanceTheme.f2494e);
        }
    }

    private void selectAll() {
        this.progressDialog = new ProgressDialog(getParentActivity());
        this.progressDialog.setMessage(LocaleController.getString("PleaseWait", C0338R.string.PleaseWait));
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
        new Handler().postDelayed(new C15686(), 500);
    }

    private void updateVisibleRows(int i) {
        if (this.listView != null) {
            int childCount = this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2 += done_button) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                }
            }
        }
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", C0338R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", C0338R.string.AlwaysShareWithTitle));
            }
        } else if (this.isNeverShare) {
            if (this.isGroup) {
                this.actionBar.setTitle(LocaleController.getString("NeverAllow", C0338R.string.NeverAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", C0338R.string.NeverShareWithTitle));
            }
        } else if (this.isDelete) {
            this.actionBar.setTitle(LocaleController.getString("MoboDeleteContacts", C0338R.string.MoboDeleteContacts));
        } else {
            this.actionBar.setTitle(this.chatType == 0 ? LocaleController.getString("NewGroup", C0338R.string.NewGroup) : LocaleController.getString("NewBroadcastList", C0338R.string.NewBroadcastList));
            ActionBar actionBar = this.actionBar;
            Object[] objArr = new Object[select_all_button];
            objArr[0] = Integer.valueOf(this.selectedContacts.size());
            objArr[done_button] = Integer.valueOf(this.maxCount);
            actionBar.setSubtitle(LocaleController.formatString("MembersCount", C0338R.string.MembersCount, objArr));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C15631());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_select_all);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            createMenu.addItemWithWidth((int) select_all_button, drawable, AndroidUtilities.dp(56.0f));
            drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            createMenu.addItemWithWidth((int) done_button, drawable, AndroidUtilities.dp(56.0f));
        } else {
            createMenu.addItemWithWidth((int) select_all_button, (int) C0338R.drawable.ic_select_all, AndroidUtilities.dp(56.0f));
            createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        this.searchListViewAdapter = new SearchAdapter(context, null, false, false, false, false);
        this.searchListViewAdapter.setCheckedMap(this.selectedContacts);
        this.searchListViewAdapter.setUseUserCell(true);
        this.listViewAdapter = new ContactsAdapter(context, done_button, false, null, false);
        this.listViewAdapter.setCheckedMap(this.selectedContacts);
        this.fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(done_button);
        View frameLayout = new FrameLayout(context);
        if (ThemeUtil.m2490b()) {
            frameLayout.setBackgroundColor(AdvanceTheme.f2497h);
        }
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.userSelectEditText = new EditText(context);
        this.userSelectEditText.setTextSize(done_button, 16.0f);
        this.userSelectEditText.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.userSelectEditText.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.userSelectEditText.setInputType(655536);
        this.userSelectEditText.setMinimumHeight(AndroidUtilities.dp(54.0f));
        this.userSelectEditText.setSingleLine(false);
        this.userSelectEditText.setLines(select_all_button);
        this.userSelectEditText.setMaxLines(select_all_button);
        this.userSelectEditText.setVerticalScrollBarEnabled(true);
        this.userSelectEditText.setHorizontalScrollBarEnabled(false);
        this.userSelectEditText.setPadding(0, 0, 0, 0);
        this.userSelectEditText.setImeOptions(268435462);
        this.userSelectEditText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        AndroidUtilities.clearCursorDrawable(this.userSelectEditText);
        frameLayout.addView(this.userSelectEditText, LayoutHelper.createFrame(-1, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
        if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.userSelectEditText.setHint(LocaleController.getString("AlwaysAllowPlaceholder", C0338R.string.AlwaysAllowPlaceholder));
            } else {
                this.userSelectEditText.setHint(LocaleController.getString("AlwaysShareWithPlaceholder", C0338R.string.AlwaysShareWithPlaceholder));
            }
        } else if (this.isNeverShare) {
            if (this.isGroup) {
                this.userSelectEditText.setHint(LocaleController.getString("NeverAllowPlaceholder", C0338R.string.NeverAllowPlaceholder));
            } else {
                this.userSelectEditText.setHint(LocaleController.getString("NeverShareWithPlaceholder", C0338R.string.NeverShareWithPlaceholder));
            }
        } else if (this.isDelete) {
            this.userSelectEditText.setHint(LocaleController.getString("DeleteContactPlaceholder", C0338R.string.DeleteContactPlaceholder));
        } else {
            this.userSelectEditText.setHint(LocaleController.getString("SendMessageTo", C0338R.string.SendMessageTo));
        }
        this.userSelectEditText.setTextIsSelectable(false);
        this.userSelectEditText.addTextChangedListener(new C15642());
        View linearLayout2 = new LinearLayout(context);
        linearLayout2.setVisibility(4);
        linearLayout2.setOrientation(done_button);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -1));
        linearLayout2.setOnTouchListener(new C15653());
        this.emptyTextView = new TextView(context);
        this.emptyTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.emptyTextView.setTextColor(-8355712);
        this.emptyTextView.setTextSize(20.0f);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
        linearLayout2.addView(this.emptyTextView, LayoutHelper.createLinear(-1, -1, 0.5f));
        linearLayout2.addView(new FrameLayout(context), LayoutHelper.createLinear(-1, -1, 0.5f));
        this.listView = new LetterSectionsListView(context);
        this.listView.setEmptyView(linearLayout2);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setFastScrollEnabled(true);
        this.listView.setScrollBarStyle(33554432);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setFastScrollAlwaysVisible(true);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? done_button : select_all_button);
        linearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
        this.listView.setOnItemClickListener(new C15664());
        this.listView.setOnScrollListener(new C15675());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.contactsDidLoaded) {
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if ((intValue & select_all_button) != 0 || (intValue & done_button) != 0 || (intValue & 4) != 0) {
                updateVisibleRows(intValue);
            }
        } else if (i == NotificationCenter.chatDidCreated) {
            removeSelfFromStack();
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
        initTheme();
    }

    public void setDelegate(GroupCreateActivityDelegate groupCreateActivityDelegate) {
        this.delegate = groupCreateActivityDelegate;
    }
}
