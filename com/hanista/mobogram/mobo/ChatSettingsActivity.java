package com.hanista.mobogram.mobo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
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
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
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
import com.hanista.mobogram.ui.Cells.TextInfoCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.util.Locale;

/* renamed from: com.hanista.mobogram.mobo.c */
public class ChatSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListView f319a;
    private ChatSettingsActivity f320b;
    private int f321c;
    private int f322d;
    private int f323e;
    private int f324f;
    private int f325g;
    private int f326h;
    private int f327i;
    private int f328j;
    private int f329k;
    private int f330l;
    private int f331m;
    private int f332n;
    private int f333o;
    private int f334p;
    private int f335q;
    private int f336r;
    private int f337s;
    private int f338t;

    /* renamed from: com.hanista.mobogram.mobo.c.1 */
    class ChatSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ ChatSettingsActivity f249a;

        ChatSettingsActivity(ChatSettingsActivity chatSettingsActivity) {
            this.f249a = chatSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f249a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.c.2 */
    class ChatSettingsActivity implements OnItemClickListener {
        final /* synthetic */ ChatSettingsActivity f258a;

        /* renamed from: com.hanista.mobogram.mobo.c.2.1 */
        class ChatSettingsActivity implements OnClickListener {
            final /* synthetic */ boolean[] f250a;
            final /* synthetic */ ChatSettingsActivity f251b;

            ChatSettingsActivity(ChatSettingsActivity chatSettingsActivity, boolean[] zArr) {
                this.f251b = chatSettingsActivity;
                this.f250a = zArr;
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                this.f250a[intValue] = !this.f250a[intValue];
                checkBoxCell.setChecked(this.f250a[intValue], true);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.c.2.2 */
        class ChatSettingsActivity implements OnClickListener {
            final /* synthetic */ boolean[] f252a;
            final /* synthetic */ ChatSettingsActivity f253b;

            ChatSettingsActivity(ChatSettingsActivity chatSettingsActivity, boolean[] zArr) {
                this.f253b = chatSettingsActivity;
                this.f252a = zArr;
            }

            public void onClick(View view) {
                try {
                    if (this.f253b.f258a.visibleDialog != null) {
                        this.f253b.f258a.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                int i = 0;
                for (int i2 = 0; i2 < 5; i2++) {
                    if (this.f252a[i2]) {
                        if (i2 == 0) {
                            i |= 1;
                        } else if (i2 == 1) {
                            i |= 2;
                        } else if (i2 == 2) {
                            i |= 4;
                        } else if (i2 == 3) {
                            i |= 8;
                        } else if (i2 == 4) {
                            i |= 16;
                        }
                    }
                }
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("show_direct_share_btn_mask", i);
                edit.commit();
                MoboConstants.m1379a();
                if (this.f253b.f258a.f319a != null) {
                    this.f253b.f258a.f319a.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.c.2.3 */
        class ChatSettingsActivity implements OnClickListener {
            final /* synthetic */ boolean[] f254a;
            final /* synthetic */ ChatSettingsActivity f255b;

            ChatSettingsActivity(ChatSettingsActivity chatSettingsActivity, boolean[] zArr) {
                this.f255b = chatSettingsActivity;
                this.f254a = zArr;
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                this.f254a[intValue] = !this.f254a[intValue];
                checkBoxCell.setChecked(this.f254a[intValue], true);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.c.2.4 */
        class ChatSettingsActivity implements OnClickListener {
            final /* synthetic */ boolean[] f256a;
            final /* synthetic */ ChatSettingsActivity f257b;

            ChatSettingsActivity(ChatSettingsActivity chatSettingsActivity, boolean[] zArr) {
                this.f257b = chatSettingsActivity;
                this.f256a = zArr;
            }

            public void onClick(View view) {
                try {
                    if (this.f257b.f258a.visibleDialog != null) {
                        this.f257b.f258a.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                int i = 0;
                for (int i2 = 0; i2 < 4; i2++) {
                    if (this.f256a[i2]) {
                        if (i2 == 0) {
                            i |= 1;
                        } else if (i2 == 1) {
                            i |= 2;
                        } else if (i2 == 2) {
                            i |= 4;
                        } else if (i2 == 3) {
                            i |= 16;
                        }
                    }
                }
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("show_direct_reply_btn_mask", i);
                edit.commit();
                MoboConstants.m1379a();
                if (this.f257b.f258a.f319a != null) {
                    this.f257b.f258a.f319a.invalidateViews();
                }
            }
        }

        ChatSettingsActivity(ChatSettingsActivity chatSettingsActivity) {
            this.f258a = chatSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
            Editor edit = sharedPreferences.edit();
            boolean z;
            if (i == this.f258a.f321c) {
                z = sharedPreferences.getBoolean("keep_original_file_name", false);
                edit.putBoolean("keep_original_file_name", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f258a.f322d) {
                z = MoboConstants.aE;
                edit.putBoolean("delete_file_on_delete_message", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f258a.f328j) {
                z = sharedPreferences.getBoolean("donot_close_last_chat", true);
                edit.putBoolean("donot_close_last_chat", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f258a.f329k) {
                z = MoboConstants.ae;
                edit.putBoolean("drawing_feature", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f258a.f330l) {
                z = MoboConstants.aT;
                edit.putBoolean("voice_changer", !z);
                if (z) {
                    edit.putInt("voice_changer_type", 0);
                }
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f258a.f331m) {
                z = MoboConstants.af;
                edit.putBoolean("show_gif_fullscreen", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f258a.f332n) {
                z = MoboConstants.aU;
                edit.putBoolean("show_gif_as_video", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f258a.f333o) {
                z = MoboConstants.aD;
                edit.putBoolean("use_internal_video_player", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f258a.f323e) {
                r2 = new boolean[5];
                r3 = new Builder(this.f258a.getParentActivity());
                r4 = MoboConstants.f1356w;
                r3.setApplyTopPadding(false);
                r5 = new LinearLayout(this.f258a.getParentActivity());
                r5.setOrientation(1);
                for (r1 = 0; r1 < 5; r1++) {
                    r0 = null;
                    if (r1 == 0) {
                        r2[r1] = (r4 & 1) != 0;
                        r0 = LocaleController.getString("Contacts", C0338R.string.Contacts);
                    } else if (r1 == 1) {
                        r2[r1] = (r4 & 2) != 0;
                        r0 = LocaleController.getString("Groups", C0338R.string.Groups);
                    } else if (r1 == 2) {
                        r2[r1] = (r4 & 4) != 0;
                        r0 = LocaleController.getString("SuperGroups", C0338R.string.SuperGroups);
                    } else if (r1 == 3) {
                        r2[r1] = (r4 & 8) != 0;
                        r0 = LocaleController.getString("Channels", C0338R.string.Channels);
                    } else if (r1 == 4) {
                        r2[r1] = (r4 & 16) != 0;
                        r0 = LocaleController.getString("Robots", C0338R.string.Robots);
                    }
                    r6 = new CheckBoxCell(this.f258a.getParentActivity());
                    r6.setTag(Integer.valueOf(r1));
                    r6.setBackgroundResource(C0338R.drawable.list_selector);
                    r5.addView(r6, LayoutHelper.createLinear(-1, 48));
                    r6.setText(r0, TtmlNode.ANONYMOUS_REGION_ID, r2[r1], true);
                    r6.setOnClickListener(new ChatSettingsActivity(this, r2));
                }
                r0 = new BottomSheetCell(this.f258a.getParentActivity(), 1);
                r0.setBackgroundResource(C0338R.drawable.list_selector);
                r0.setTextAndIcon(LocaleController.getString("Save", C0338R.string.Save).toUpperCase(), 0);
                r0.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
                r0.setOnClickListener(new ChatSettingsActivity(this, r2));
                r5.addView(r0, LayoutHelper.createLinear(-1, 48));
                r3.setCustomView(r5);
                this.f258a.showDialog(r3.create());
            } else if (i == this.f258a.f324f) {
                r2 = new boolean[4];
                r3 = new Builder(this.f258a.getParentActivity());
                r4 = MoboConstants.f1357x;
                r3.setApplyTopPadding(false);
                r5 = new LinearLayout(this.f258a.getParentActivity());
                r5.setOrientation(1);
                for (r1 = 0; r1 < 4; r1++) {
                    r0 = null;
                    if (r1 == 0) {
                        r2[r1] = (r4 & 1) != 0;
                        r0 = LocaleController.getString("Contacts", C0338R.string.Contacts);
                    } else if (r1 == 1) {
                        r2[r1] = (r4 & 2) != 0;
                        r0 = LocaleController.getString("Groups", C0338R.string.Groups);
                    } else if (r1 == 2) {
                        r2[r1] = (r4 & 4) != 0;
                        r0 = LocaleController.getString("SuperGroups", C0338R.string.SuperGroups);
                    } else if (r1 == 3) {
                        r2[r1] = (r4 & 16) != 0;
                        r0 = LocaleController.getString("Robots", C0338R.string.Robots);
                    }
                    r6 = new CheckBoxCell(this.f258a.getParentActivity());
                    r6.setTag(Integer.valueOf(r1));
                    r6.setBackgroundResource(C0338R.drawable.list_selector);
                    r5.addView(r6, LayoutHelper.createLinear(-1, 48));
                    r6.setText(r0, TtmlNode.ANONYMOUS_REGION_ID, r2[r1], true);
                    r6.setOnClickListener(new ChatSettingsActivity(this, r2));
                }
                r0 = new BottomSheetCell(this.f258a.getParentActivity(), 1);
                r0.setBackgroundResource(C0338R.drawable.list_selector);
                r0.setTextAndIcon(LocaleController.getString("Save", C0338R.string.Save).toUpperCase(), 0);
                r0.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
                r0.setOnClickListener(new ChatSettingsActivity(this, r2));
                r5.addView(r0, LayoutHelper.createLinear(-1, 48));
                r3.setCustomView(r5);
                this.f258a.showDialog(r3.create());
            } else if (i == this.f258a.f325g) {
                z = MoboConstants.f1351r;
                edit.putBoolean("show_contact_status_in_group", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f258a.f326h) {
                z = MoboConstants.f1353t;
                edit.putBoolean("confirm_before_send_voice", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
            } else if (i == this.f258a.f327i) {
                z = MoboConstants.f1320M;
                edit.putBoolean("show_exact_members_and_views", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
            } else if (i == this.f258a.f334p) {
                z = MoboConstants.f1321N;
                edit.putBoolean("use_front_speaker_on_sensor", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
            } else if (i == this.f258a.f335q) {
                z = MoboConstants.ab;
                edit.putBoolean("show_date_toast", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
            } else if (i == this.f258a.f336r) {
                z = MoboConstants.ac;
                edit.putBoolean("copy_transmitter_name", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
            } else if (i == this.f258a.f337s) {
                z = MoboConstants.f1330W;
                edit.putBoolean("hide_camera_in_attach_panel", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.c.a */
    private class ChatSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ ChatSettingsActivity f259a;
        private Context f260b;

        public ChatSettingsActivity(ChatSettingsActivity chatSettingsActivity, Context context) {
            this.f259a = chatSettingsActivity;
            this.f260b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f259a.f338t;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == this.f259a.f329k || i == this.f259a.f331m || i == this.f259a.f336r || i == this.f259a.f332n || i == this.f259a.f325g || i == this.f259a.f321c || i == this.f259a.f328j || i == this.f259a.f335q || i == this.f259a.f326h || i == this.f259a.f327i || i == this.f259a.f334p || i == this.f259a.f333o || i == this.f259a.f322d || i == this.f259a.f337s || i == this.f259a.f330l) ? 8 : (i == this.f259a.f323e || i == this.f259a.f324f) ? 6 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f260b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f260b) : view;
                } else {
                    View textSettingsCell;
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f260b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f260b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f260b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                        if (i == this.f259a.f321c) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("KeepOriginalFileName", C0338R.string.KeepOriginalFileName), LocaleController.getString("KeepOriginalFileNameDetail", C0338R.string.KeepOriginalFileNameDetail), sharedPreferences.getBoolean("keep_original_file_name", false), true);
                        } else if (i == this.f259a.f322d) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("DeleteFileOnDeleteMessage", C0338R.string.DeleteFileOnDeleteMessage), LocaleController.getString("DeleteFileOnDeleteMessageDetail", C0338R.string.DeleteFileOnDeleteMessageDetail), MoboConstants.aE, true);
                        } else if (i == this.f259a.f325g) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowContactStatusInGroup", C0338R.string.ShowContactStatusInGroup), LocaleController.getString("ShowContactStatusInGroupDetail", C0338R.string.ShowContactStatusInGroupDetail), sharedPreferences.getBoolean("show_contact_status_in_group", true), true);
                        } else if (i == this.f259a.f326h) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ConfirmationBeforSendVoice", C0338R.string.ConfirmationBeforSendVoice), LocaleController.getString("ConfirmationBeforSendVoiceDetail", C0338R.string.ConfirmationBeforSendVoiceDetail), MoboConstants.f1353t, true);
                        } else if (i == this.f259a.f328j) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("DontCloseLastChatOnJoinNewChat", C0338R.string.DontCloseLastChatOnJoinNewChat), LocaleController.getString("DontCloseLastChatOnJoinNewChatDetail", C0338R.string.DontCloseLastChatOnJoinNewChatDetail), sharedPreferences.getBoolean("donot_close_last_chat", true), true);
                        } else if (i == this.f259a.f329k) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("DrawingFeature", C0338R.string.DrawingFeature), LocaleController.getString("DrawingFeatureDetail", C0338R.string.DrawingFeatureDetail), MoboConstants.ae, true);
                        } else if (i == this.f259a.f330l) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("VoiceChanger", C0338R.string.VoiceChanger), LocaleController.getString("VoiceChangerDetail", C0338R.string.VoiceChangerDetail), MoboConstants.aT, true);
                        } else if (i == this.f259a.f331m) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowGifsInFullScreen", C0338R.string.ShowGifsInFullScreen), LocaleController.getString("ShowGifsInFullScreenDetail", C0338R.string.ShowGifsInFullScreenDetail), MoboConstants.af, true);
                        } else if (i == this.f259a.f332n) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowGifAsVideo", C0338R.string.ShowGifAsVideo), LocaleController.getString("ShowGifAsVideoDetail", C0338R.string.ShowGifAsVideoDetail), MoboConstants.aU, true);
                        } else if (i == this.f259a.f333o) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("UseInternalVideoPlayer", C0338R.string.UseInternalVideoPlayer), LocaleController.getString("UseInternalVideoPlayerDetail", C0338R.string.UseInternalVideoPlayerDetail), MoboConstants.aD, true);
                        } else if (i == this.f259a.f327i) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowExactMembersAndViews", C0338R.string.ShowExactMembersAndViews), LocaleController.getString("ShowExactMembersAndViewsDetail", C0338R.string.ShowExactMembersAndViewsDetail), MoboConstants.f1320M, true);
                        } else if (i == this.f259a.f334p) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("UseFrontSpeakerOnProximitySensorTouched", C0338R.string.UseFrontSpeakerOnProximitySensorTouched), LocaleController.getString("UseFrontSpeakerOnProximitySensorTouchedDetail", C0338R.string.UseFrontSpeakerOnProximitySensorTouchedDetail), MoboConstants.f1321N, true);
                        } else if (i == this.f259a.f335q) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowDateToast", C0338R.string.ShowDateToast), LocaleController.getString("ShowDateToastDetail", C0338R.string.ShowDateToastDetail), MoboConstants.ab, true);
                        } else if (i == this.f259a.f336r) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("CopyTransmitterName", C0338R.string.CopyTransmitterName), LocaleController.getString("CopyTransmitterNameDetail", C0338R.string.CopyTransmitterNameDetail), MoboConstants.ac, true);
                        } else if (i == this.f259a.f337s) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("HideCameraInAttachPanel", C0338R.string.HideCameraInAttachPanel), LocaleController.getString("HideCameraInAttachPanelDetail", C0338R.string.HideCameraInAttachPanelDetail), MoboConstants.f1330W, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f260b) : view;
                    } else {
                        if (itemViewType == 5) {
                            if (view != null) {
                                return view;
                            }
                            textSettingsCell = new TextInfoCell(this.f260b);
                            try {
                                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                                ((TextInfoCell) textSettingsCell).setText(String.format(Locale.US, "Mobogram for Android v%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)}));
                                return textSettingsCell;
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                                return textSettingsCell;
                            }
                        } else if (itemViewType != 6) {
                            return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f260b) : view;
                        } else {
                            textSettingsCell = view == null ? new TextDetailSettingsCell(this.f260b) : view;
                            TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                            String string;
                            int i2;
                            String str;
                            if (i == this.f259a.f323e) {
                                string = LocaleController.getString("ShowDirectShareBtn", C0338R.string.ShowDirectShareBtn);
                                i2 = MoboConstants.f1356w;
                                str = TtmlNode.ANONYMOUS_REGION_ID;
                                if ((i2 & 1) != 0) {
                                    str = str + LocaleController.getString("Contacts", C0338R.string.Contacts);
                                }
                                if ((i2 & 2) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("Groups", C0338R.string.Groups);
                                }
                                if ((i2 & 4) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("SuperGroups", C0338R.string.SuperGroups);
                                }
                                if ((i2 & 8) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("Channels", C0338R.string.Channels);
                                }
                                if ((i2 & 16) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("Robots", C0338R.string.Robots);
                                }
                                if (str.length() == 0) {
                                    str = LocaleController.getString("NothingSelected", C0338R.string.NothingSelected);
                                }
                                textDetailSettingsCell.setTextAndValue(string, str, true);
                            } else if (i == this.f259a.f324f) {
                                string = LocaleController.getString("ShowDirectReplyBtn", C0338R.string.ShowDirectReplyBtn);
                                i2 = MoboConstants.f1357x;
                                str = TtmlNode.ANONYMOUS_REGION_ID;
                                if ((i2 & 1) != 0) {
                                    str = str + LocaleController.getString("Contacts", C0338R.string.Contacts);
                                }
                                if ((i2 & 2) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("Groups", C0338R.string.Groups);
                                }
                                if ((i2 & 4) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("SuperGroups", C0338R.string.SuperGroups);
                                }
                                if ((i2 & 16) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("Robots", C0338R.string.Robots);
                                }
                                if (str.length() == 0) {
                                    str = LocaleController.getString("NothingSelected", C0338R.string.NothingSelected) + "\n" + LocaleController.getString("ShowDirectReplyBtnDetail", C0338R.string.ShowDirectReplyBtnDetail);
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
            return i == this.f259a.f328j || i == this.f259a.f325g || i == this.f259a.f331m || i == this.f259a.f336r || i == this.f259a.f321c || i == this.f259a.f323e || i == this.f259a.f335q || i == this.f259a.f329k || i == this.f259a.f332n || i == this.f259a.f326h || i == this.f259a.f327i || i == this.f259a.f334p || i == this.f259a.f330l || i == this.f259a.f333o || i == this.f259a.f322d || i == this.f259a.f324f || i == this.f259a.f337s;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ChatsSettings", C0338R.string.ChatsSettings));
        this.actionBar.setActionBarMenuOnItemClick(new ChatSettingsActivity(this));
        this.f320b = new ChatSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f319a = new ListView(context);
        initThemeBackground(this.f319a);
        this.f319a.setDivider(null);
        this.f319a.setDividerHeight(0);
        this.f319a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f319a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f319a, LayoutHelper.createFrame(-1, -1, 51));
        this.f319a.setAdapter(this.f320b);
        this.f319a.setOnItemClickListener(new ChatSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.f319a.invalidateViews();
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        this.f338t = 0;
        int i = this.f338t;
        this.f338t = i + 1;
        this.f321c = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f322d = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f328j = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f330l = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f329k = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f331m = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f332n = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f333o = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f323e = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f324f = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f325g = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f326h = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f327i = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f334p = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f335q = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f336r = i;
        i = this.f338t;
        this.f338t = i + 1;
        this.f337s = i;
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}
