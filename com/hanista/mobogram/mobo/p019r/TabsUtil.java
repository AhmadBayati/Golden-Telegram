package com.hanista.mobogram.mobo.p019r;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.DialogsTab;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p001b.CategoryUtil;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* renamed from: com.hanista.mobogram.mobo.r.d */
public class TabsUtil {
    public static String m2258a(int i) {
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return LocaleController.getString("All", C0338R.string.All);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return LocaleController.getString("Channels", C0338R.string.Channels);
            case VideoPlayer.STATE_READY /*4*/:
                return LocaleController.getString("Contacts", C0338R.string.Contacts);
            case VideoPlayer.STATE_ENDED /*5*/:
                return LocaleController.getString("Robots", C0338R.string.Robots);
            case Method.TRACE /*6*/:
                return LocaleController.getString("Favorites", C0338R.string.Favorites);
            case Method.PATCH /*7*/:
                return LocaleController.getString("Groups", C0338R.string.Groups);
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                return LocaleController.getString("Unread", C0338R.string.Unread);
            case C0338R.styleable.PromptView_iconTint /*9*/:
                return LocaleController.getString("SuperGroups", C0338R.string.SuperGroups);
            case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                return LocaleController.getString("UnreadUnmuted", C0338R.string.UnreadUnmuted);
            case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                return LocaleController.getString("AllGroups", C0338R.string.AllGroups);
            case Atom.FULL_HEADER_SIZE /*12*/:
                return LocaleController.getString("MyChats", C0338R.string.MyChats);
            case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                return LocaleController.getString("PhoneContacts", C0338R.string.PhoneContacts);
            default:
                return null;
        }
    }

    public static ArrayList<TL_dialog> m2259a(int i, long j, boolean z) {
        ArrayList arrayList;
        ArrayList arrayList2 = new ArrayList();
        if (i == 0) {
            arrayList = MessagesController.getInstance().dialogs;
        } else if (i == 1) {
            arrayList = MessagesController.getInstance().dialogsServerOnly;
        } else if (i == 2) {
            arrayList = MessagesController.getInstance().dialogsGroupsOnly;
        } else if (i == 3) {
            arrayList = MessagesController.getInstance().dialogsChannelOnly;
        } else if (i == 4) {
            arrayList = MessagesController.getInstance().dialogsContactOnly;
        } else if (i == 5) {
            arrayList = MessagesController.getInstance().dialogsBotOnly;
        } else if (i == 6) {
            arrayList = MessagesController.getInstance().dialogsFavoriteOnly;
        } else if (i == 7) {
            arrayList = MessagesController.getInstance().dialogsJustGroupsOnly;
        } else if (i == 9) {
            arrayList = MessagesController.getInstance().dialogsSuperGroupsOnly;
        } else if (i == 8) {
            arrayList = MessagesController.getInstance().dialogsUnreadOnly;
        } else if (i == 10) {
            r2 = MessagesController.getInstance().dialogsUnreadOnly.iterator();
            while (r2.hasNext()) {
                r0 = (TL_dialog) r2.next();
                if (!MessagesController.getInstance().isDialogMuted(r0.id)) {
                    arrayList2.add(r0);
                }
            }
            arrayList = arrayList2;
        } else if (i == 11) {
            arrayList2.addAll(MessagesController.getInstance().dialogsJustGroupsOnly);
            arrayList2.addAll(MessagesController.getInstance().dialogsSuperGroupsOnly);
            arrayList = arrayList2;
        } else if (i == 12) {
            r2 = MessagesController.getInstance().dialogsServerOnly.iterator();
            while (r2.hasNext()) {
                r0 = (TL_dialog) r2.next();
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) r0.id)));
                if (chat != null && (chat.creator || (MoboConstants.f1328U && chat.editor))) {
                    arrayList2.add(r0);
                }
            }
            arrayList = arrayList2;
        } else {
            arrayList = i == 20 ? CategoryUtil.m345a() : arrayList2;
        }
        if (j > 0) {
            arrayList = CategoryUtil.m346a(j, arrayList);
        }
        return DialogsTab.m947a(i, HiddenConfig.m1393a(arrayList, z));
    }

    public static List<TabData> m2260a(boolean z, boolean z2) {
        int i = MoboConstants.f1344k;
        Collection<TabData> arrayList = new ArrayList();
        Map a = TabsUtil.m2261a();
        if (!z2) {
            arrayList.add(new TabData(8, C0338R.drawable.ic_main_tab_unread, (i & 32) != 0, TabsUtil.m2258a(8), ((Integer) a.get(Integer.valueOf(8))).intValue(), C0338R.drawable.ic_share_tab_unread, C0338R.drawable.ic_tab_unread_gray, C0338R.drawable.ic_tab_unread_selected, C0338R.drawable.ic_tab_unread));
        }
        arrayList.add(new TabData(0, C0338R.drawable.ic_main_tab_all, (i & TLRPC.USER_FLAG_UNUSED) != 0, TabsUtil.m2258a(0), ((Integer) a.get(Integer.valueOf(0))).intValue(), C0338R.drawable.ic_share_tab_all, C0338R.drawable.ic_tab_all_gray, C0338R.drawable.ic_tab_all_selected, C0338R.drawable.ic_tab_all));
        arrayList.add(new TabData(6, C0338R.drawable.ic_main_tab_favorite, (i & 1) != 0, TabsUtil.m2258a(6), ((Integer) a.get(Integer.valueOf(6))).intValue(), C0338R.drawable.ic_share_tab_favorite, C0338R.drawable.ic_tab_favorite_gray, C0338R.drawable.ic_tab_favorite_selected, C0338R.drawable.ic_tab_favorite));
        arrayList.add(new TabData(4, C0338R.drawable.ic_main_tab_contact, (i & 2) != 0, TabsUtil.m2258a(4), ((Integer) a.get(Integer.valueOf(4))).intValue(), C0338R.drawable.ic_share_tab_contact, C0338R.drawable.ic_tab_contact_gray, C0338R.drawable.ic_tab_contact_selected, C0338R.drawable.ic_tab_contact));
        arrayList.add(new TabData(7, C0338R.drawable.ic_main_tab_group, (i & 4) != 0, TabsUtil.m2258a(7), ((Integer) a.get(Integer.valueOf(7))).intValue(), C0338R.drawable.ic_share_tab_group, C0338R.drawable.ic_tab_group_gray, C0338R.drawable.ic_tab_group_selected, C0338R.drawable.ic_tab_group));
        arrayList.add(new TabData(9, C0338R.drawable.ic_main_tab_supergroup, (i & 64) != 0, TabsUtil.m2258a(9), ((Integer) a.get(Integer.valueOf(9))).intValue(), C0338R.drawable.ic_share_tab_supergroup, C0338R.drawable.ic_tab_supergroup_gray, C0338R.drawable.ic_tab_supergroup_selected, C0338R.drawable.ic_tab_supergroup));
        arrayList.add(new TabData(3, C0338R.drawable.ic_main_tab_channel, (i & 8) != 0, TabsUtil.m2258a(3), ((Integer) a.get(Integer.valueOf(3))).intValue(), C0338R.drawable.ic_share_tab_channel, C0338R.drawable.ic_tab_channel_gray, C0338R.drawable.ic_tab_channel_selected, C0338R.drawable.ic_tab_channel));
        if (!UserConfig.isRobot) {
            arrayList.add(new TabData(5, C0338R.drawable.ic_main_tab_bot, (i & 16) != 0, TabsUtil.m2258a(5), ((Integer) a.get(Integer.valueOf(5))).intValue(), C0338R.drawable.ic_share_tab_bot, C0338R.drawable.ic_tab_bot_gray, C0338R.drawable.ic_tab_bot_selected, C0338R.drawable.ic_tab_bot));
        }
        if (!z2) {
            arrayList.add(new TabData(10, C0338R.drawable.ic_main_tab_important, (i & TLRPC.USER_FLAG_UNUSED2) != 0, TabsUtil.m2258a(10), ((Integer) a.get(Integer.valueOf(10))).intValue(), C0338R.drawable.ic_main_tab_important, C0338R.drawable.ic_tab_important_gray, C0338R.drawable.ic_tab_important_selected, C0338R.drawable.ic_tab_important));
        }
        arrayList.add(new TabData(11, C0338R.drawable.ic_main_tab_all_groups, (i & TLRPC.USER_FLAG_UNUSED3) != 0, TabsUtil.m2258a(11), ((Integer) a.get(Integer.valueOf(11))).intValue(), C0338R.drawable.ic_share_tab_group, C0338R.drawable.ic_tab_group_gray, C0338R.drawable.ic_tab_group_selected, C0338R.drawable.ic_tab_group));
        if (!UserConfig.isRobot) {
            arrayList.add(new TabData(12, C0338R.drawable.ic_main_tab_creator, (i & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0, TabsUtil.m2258a(12), ((Integer) a.get(Integer.valueOf(12))).intValue(), C0338R.drawable.ic_share_tab_creator, C0338R.drawable.ic_tab_creator_gray, C0338R.drawable.ic_tab_creator_selected, C0338R.drawable.ic_tab_creator));
        }
        if (z2 && MoboConstants.ai && !UserConfig.isRobot) {
            arrayList.add(new TabData(13, C0338R.drawable.ic_share_tab_phone_contact, true, TabsUtil.m2258a(13), ((Integer) a.get(Integer.valueOf(13))).intValue(), C0338R.drawable.ic_share_tab_phone_contact, C0338R.drawable.ic_tab_phone_contact_gray, C0338R.drawable.ic_tab_phone_contact_selected, C0338R.drawable.ic_tab_phone_contact));
        }
        List<TabData> arrayList2 = new ArrayList();
        if (z) {
            for (TabData tabData : arrayList) {
                if (tabData.m2231c()) {
                    arrayList2.add(tabData);
                }
            }
        } else {
            arrayList2.addAll(arrayList);
        }
        Collections.sort(arrayList2);
        return arrayList2;
    }

    public static Map<Integer, Integer> m2261a() {
        Map<Integer, Integer> hashMap = new HashMap();
        String[] split = MoboConstants.f1326S.split("@");
        if (split != null && split.length > 1) {
            for (String str : split) {
                if (str.length() > 0) {
                    hashMap.put(Integer.valueOf(Integer.parseInt(str.split("#")[0])), Integer.valueOf(Integer.parseInt(str.split("#")[1])));
                }
            }
        }
        if (hashMap.get(Integer.valueOf(8)) == null) {
            hashMap.put(Integer.valueOf(8), Integer.valueOf(1));
        }
        if (hashMap.get(Integer.valueOf(0)) == null) {
            hashMap.put(Integer.valueOf(0), Integer.valueOf(2));
        }
        if (hashMap.get(Integer.valueOf(6)) == null) {
            hashMap.put(Integer.valueOf(6), Integer.valueOf(3));
        }
        if (hashMap.get(Integer.valueOf(4)) == null) {
            hashMap.put(Integer.valueOf(4), Integer.valueOf(4));
        }
        if (hashMap.get(Integer.valueOf(7)) == null) {
            hashMap.put(Integer.valueOf(7), Integer.valueOf(5));
        }
        if (hashMap.get(Integer.valueOf(9)) == null) {
            hashMap.put(Integer.valueOf(9), Integer.valueOf(6));
        }
        if (hashMap.get(Integer.valueOf(3)) == null) {
            hashMap.put(Integer.valueOf(3), Integer.valueOf(7));
        }
        if (hashMap.get(Integer.valueOf(5)) == null) {
            hashMap.put(Integer.valueOf(5), Integer.valueOf(8));
        }
        if (hashMap.get(Integer.valueOf(10)) == null) {
            hashMap.put(Integer.valueOf(10), Integer.valueOf(9));
        }
        if (hashMap.get(Integer.valueOf(11)) == null) {
            hashMap.put(Integer.valueOf(11), Integer.valueOf(10));
        }
        if (hashMap.get(Integer.valueOf(12)) == null) {
            hashMap.put(Integer.valueOf(12), Integer.valueOf(11));
        }
        if (hashMap.get(Integer.valueOf(13)) == null) {
            hashMap.put(Integer.valueOf(13), Integer.valueOf(12));
        }
        return hashMap;
    }

    public static void m2262a(List<TabData> list) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        String str2 = str;
        for (TabData tabData : list) {
            str2 = str2 + "@" + tabData.m2225a() + "#" + tabData.m2234e();
        }
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
        edit.putString("tabs_orders", str2);
        edit.commit();
        MoboConstants.f1326S = str2;
    }

    public static int m2263b(int i) {
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return TLRPC.USER_FLAG_UNUSED;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return 8;
            case VideoPlayer.STATE_READY /*4*/:
                return 2;
            case VideoPlayer.STATE_ENDED /*5*/:
                return 16;
            case Method.TRACE /*6*/:
                return 1;
            case Method.PATCH /*7*/:
                return 4;
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                return 32;
            case C0338R.styleable.PromptView_iconTint /*9*/:
                return 64;
            case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                return TLRPC.USER_FLAG_UNUSED2;
            case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                return TLRPC.USER_FLAG_UNUSED3;
            case Atom.FULL_HEADER_SIZE /*12*/:
                return TLRPC.MESSAGE_FLAG_HAS_VIEWS;
            default:
                return 0;
        }
    }

    public static void m2264b() {
        List<TabData> a = TabsUtil.m2260a(false, false);
        boolean z = false;
        boolean z2 = false;
        for (TabData tabData : a) {
            if (MoboConstants.f1329V == tabData.m2225a() && tabData.m2231c()) {
                z2 = true;
            }
            boolean z3 = (MoboConstants.ak == tabData.m2225a() && tabData.m2231c()) ? true : z;
            z = z3;
        }
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
        if (!z2) {
            Editor edit = sharedPreferences.edit();
            for (TabData tabData2 : a) {
                if (tabData2.m2231c()) {
                    edit.putInt("default_tab", tabData2.m2225a());
                    edit.commit();
                    MoboConstants.f1329V = tabData2.m2225a();
                    break;
                }
            }
        }
        List<TabData> a2 = TabsUtil.m2260a(false, true);
        if (!z) {
            Editor edit2 = sharedPreferences.edit();
            for (TabData tabData22 : a2) {
                if (tabData22.m2231c()) {
                    edit2.putInt("multi_forward_default_tab", tabData22.m2225a());
                    edit2.commit();
                    MoboConstants.ak = tabData22.m2225a();
                    return;
                }
            }
        }
    }
}
