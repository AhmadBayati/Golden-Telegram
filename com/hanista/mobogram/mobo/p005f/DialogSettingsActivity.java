package com.hanista.mobogram.mobo.p005f;

import android.app.Dialog;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.Builder;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailSettingsCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.LayoutHelper;

/* renamed from: com.hanista.mobogram.mobo.f.b */
public class DialogSettingsActivity extends BaseFragment {
    private ListView f904a;
    private DialogSettingsActivity f905b;
    private int f906c;
    private int f907d;
    private int f908e;
    private int f909f;
    private int f910g;
    private DialogSettings f911h;
    private long f912i;

    /* renamed from: com.hanista.mobogram.mobo.f.b.1 */
    class DialogSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ DialogSettingsActivity f896a;

        DialogSettingsActivity(DialogSettingsActivity dialogSettingsActivity) {
            this.f896a = dialogSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f896a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.f.b.2 */
    class DialogSettingsActivity implements OnItemClickListener {
        final /* synthetic */ DialogSettingsActivity f901a;

        /* renamed from: com.hanista.mobogram.mobo.f.b.2.1 */
        class DialogSettingsActivity implements OnClickListener {
            final /* synthetic */ boolean[] f897a;
            final /* synthetic */ DialogSettingsActivity f898b;

            DialogSettingsActivity(DialogSettingsActivity dialogSettingsActivity, boolean[] zArr) {
                this.f898b = dialogSettingsActivity;
                this.f897a = zArr;
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                this.f897a[intValue] = !this.f897a[intValue];
                checkBoxCell.setChecked(this.f897a[intValue], true);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.f.b.2.2 */
        class DialogSettingsActivity implements OnClickListener {
            final /* synthetic */ boolean[] f899a;
            final /* synthetic */ DialogSettingsActivity f900b;

            DialogSettingsActivity(DialogSettingsActivity dialogSettingsActivity, boolean[] zArr) {
                this.f900b = dialogSettingsActivity;
                this.f899a = zArr;
            }

            public void onClick(View view) {
                int i = 0;
                try {
                    if (this.f900b.f901a.visibleDialog != null) {
                        this.f900b.f901a.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                int i2 = 0;
                while (i < 6) {
                    if (this.f899a[i]) {
                        if (i == 0) {
                            i2 |= 1;
                        } else if (i == 1) {
                            i2 |= 2;
                        } else if (i == 2) {
                            i2 |= 4;
                        } else if (i == 3) {
                            i2 |= 8;
                        } else if (i == 4) {
                            i2 |= 16;
                        } else if (i == 5) {
                            i2 |= 32;
                        }
                    }
                    i++;
                }
                this.f900b.f901a.f911h.m973a(i2);
                DialogSettingsUtil.m999a(this.f900b.f901a.f911h);
                if (this.f900b.f901a.f904a != null) {
                    this.f900b.f901a.f904a.invalidateViews();
                }
            }
        }

        DialogSettingsActivity(DialogSettingsActivity dialogSettingsActivity) {
            this.f901a = dialogSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (i == this.f901a.f909f) {
                if (this.f901a.getParentActivity() != null) {
                    DialogSettingsUtil.m1000a(this.f901a.f911h.m972a());
                    this.f901a.m987a();
                    if (this.f901a.f904a != null) {
                        this.f901a.f904a.invalidateViews();
                    }
                }
            } else if (i == this.f901a.f907d) {
                this.f901a.f911h.m975a(!this.f901a.f911h.m982e());
                DialogSettingsUtil.m999a(this.f901a.f911h);
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(this.f901a.f911h.m982e());
                }
                this.f901a.m987a();
            } else if (i == this.f901a.f908e) {
                this.f901a.f911h.m979b(!this.f901a.f911h.m983f());
                DialogSettingsUtil.m999a(this.f901a.f911h);
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(this.f901a.f911h.m983f());
                }
                this.f901a.m987a();
            } else if (i == this.f901a.f906c) {
                boolean[] zArr = new boolean[6];
                Builder builder = new Builder(this.f901a.getParentActivity());
                int c = this.f901a.f911h.m980c();
                builder.setApplyTopPadding(false);
                builder.setApplyBottomPadding(false);
                View linearLayout = new LinearLayout(this.f901a.getParentActivity());
                linearLayout.setOrientation(1);
                for (int i2 = 0; i2 < 6; i2++) {
                    String str = null;
                    if (i2 == 0) {
                        zArr[i2] = (c & 1) != 0;
                        str = LocaleController.getString("AttachPhoto", C0338R.string.AttachPhoto);
                    } else if (i2 == 1) {
                        zArr[i2] = (c & 2) != 0;
                        str = LocaleController.getString("AttachAudio", C0338R.string.AttachAudio);
                    } else if (i2 == 2) {
                        zArr[i2] = (c & 4) != 0;
                        str = LocaleController.getString("AttachVideo", C0338R.string.AttachVideo);
                    } else if (i2 == 3) {
                        zArr[i2] = (c & 8) != 0;
                        str = LocaleController.getString("AttachDocument", C0338R.string.AttachDocument);
                    } else if (i2 == 4) {
                        zArr[i2] = (c & 16) != 0;
                        str = LocaleController.getString("AttachMusic", C0338R.string.AttachMusic);
                    } else if (i2 == 5) {
                        if (VERSION.SDK_INT < 11) {
                        } else {
                            zArr[i2] = (c & 32) != 0;
                            str = LocaleController.getString("AttachGif", C0338R.string.AttachGif);
                        }
                    }
                    View checkBoxCell = new CheckBoxCell(this.f901a.getParentActivity());
                    checkBoxCell.setTag(Integer.valueOf(i2));
                    checkBoxCell.setBackgroundResource(C0338R.drawable.list_selector);
                    linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                    checkBoxCell.setText(str, TtmlNode.ANONYMOUS_REGION_ID, zArr[i2], true);
                    checkBoxCell.setOnClickListener(new DialogSettingsActivity(this, zArr));
                }
                View bottomSheetCell = new BottomSheetCell(this.f901a.getParentActivity(), 1);
                bottomSheetCell.setBackgroundResource(C0338R.drawable.list_selector);
                bottomSheetCell.setTextAndIcon(LocaleController.getString("Save", C0338R.string.Save).toUpperCase(), 0);
                bottomSheetCell.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
                bottomSheetCell.setOnClickListener(new DialogSettingsActivity(this, zArr));
                linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 48));
                builder.setCustomView(linearLayout);
                this.f901a.showDialog(builder.create());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.f.b.a */
    private class DialogSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ DialogSettingsActivity f902a;
        private Context f903b;

        public DialogSettingsActivity(DialogSettingsActivity dialogSettingsActivity, Context context) {
            this.f902a = dialogSettingsActivity;
            this.f903b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f902a.f910g;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == this.f902a.f909f ? 2 : i == this.f902a.f906c ? 6 : (i == this.f902a.f907d || i == this.f902a.f908e) ? 8 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f903b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f903b) : view;
                } else {
                    View textSettingsCell;
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f903b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        if (i == this.f902a.f909f) {
                            textSettingsCell2.setText(LocaleController.getString("ReturnToDefaultSettings", C0338R.string.ReturnToDefaultSettings), true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f903b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f903b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        if (i == this.f902a.f907d) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("HideTypingStateInChat", C0338R.string.HideTypingStateInChat), LocaleController.getString("HideTypingStateInChatDetail", C0338R.string.HideTypingStateInChatDetail), this.f902a.f911h.m982e(), true);
                        } else if (i == this.f902a.f908e) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("NotSendReadState", C0338R.string.NotSendReadState), LocaleController.getString("NotSendReadStateDetail", C0338R.string.NotSendReadStateDetail), this.f902a.f911h.m983f(), true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f903b) : view;
                    } else {
                        if (itemViewType != 6) {
                            return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f903b) : view;
                        } else {
                            textSettingsCell = view == null ? new TextDetailSettingsCell(this.f903b) : view;
                            TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                            if (i == this.f902a.f906c) {
                                int c = this.f902a.f911h.m980c();
                                String string = LocaleController.getString("AutomaticMediaDownload", C0338R.string.AutomaticMediaDownload);
                                String str = TtmlNode.ANONYMOUS_REGION_ID;
                                if ((c & 1) != 0) {
                                    str = str + LocaleController.getString("AttachPhoto", C0338R.string.AttachPhoto);
                                }
                                if ((c & 2) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("AttachAudio", C0338R.string.AttachAudio);
                                }
                                if ((c & 4) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("AttachVideo", C0338R.string.AttachVideo);
                                }
                                if ((c & 8) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("AttachDocument", C0338R.string.AttachDocument);
                                }
                                if ((c & 16) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("AttachMusic", C0338R.string.AttachMusic);
                                }
                                if ((c & 32) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("AttachGif", C0338R.string.AttachGif);
                                }
                                if (str.length() == 0) {
                                    str = LocaleController.getString("NoMediaAutoDownload", C0338R.string.NoMediaAutoDownload);
                                }
                                textDetailSettingsCell.setTextAndValue(string, str, true);
                            }
                            return textSettingsCell;
                        }
                    }
                }
            }
        }

        public int getViewTypeCount() {
            return 9;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == this.f902a.f907d || i == this.f902a.f908e || i == this.f902a.f906c || i == this.f902a.f909f;
        }
    }

    public DialogSettingsActivity(Bundle bundle) {
        super(bundle);
    }

    private void m987a() {
        this.f911h = DialogSettingsUtil.m997a(this.f912i);
        if (this.f911h == null) {
            this.f911h = new DialogSettings();
            this.f911h.m978b(Long.valueOf(this.f912i));
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ChatSettings", C0338R.string.ChatSettings));
        this.actionBar.setActionBarMenuOnItemClick(new DialogSettingsActivity(this));
        this.f905b = new DialogSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f904a = new ListView(context);
        initThemeBackground(this.f904a);
        this.f904a.setDivider(null);
        this.f904a.setDividerHeight(0);
        this.f904a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f904a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f904a, LayoutHelper.createFrame(-1, -1, 51));
        this.f904a.setAdapter(this.f905b);
        this.f904a.setOnItemClickListener(new DialogSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.f912i = this.arguments.getLong("dialogId", 0);
        if (this.f912i == 0) {
            return false;
        }
        m987a();
        this.f910g = 0;
        int i = this.f910g;
        this.f910g = i + 1;
        this.f906c = i;
        if (this.f912i < 0) {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) this.f912i)));
            if (chat == null || !ChatObject.isChannel(chat) || chat.megagroup) {
                i = this.f910g;
                this.f910g = i + 1;
                this.f907d = i;
                i = this.f910g;
                this.f910g = i + 1;
                this.f908e = i;
            } else {
                this.f907d = -1;
                this.f908e = -1;
            }
        } else {
            i = this.f910g;
            this.f910g = i + 1;
            this.f907d = i;
            i = this.f910g;
            this.f910g = i + 1;
            this.f908e = i;
        }
        i = this.f910g;
        this.f910g = i + 1;
        this.f909f = i;
        return true;
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}
