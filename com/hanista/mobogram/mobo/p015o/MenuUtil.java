package com.hanista.mobogram.mobo.p015o;

import android.content.SharedPreferences.Editor;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PsExtractor;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* renamed from: com.hanista.mobogram.mobo.o.d */
public class MenuUtil {
    public static int m1993a(int i) {
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return 1;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return 2;
            case VideoPlayer.STATE_PREPARING /*2*/:
                return 4;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return 8;
            case VideoPlayer.STATE_READY /*4*/:
                return 16;
            case VideoPlayer.STATE_ENDED /*5*/:
                return 32;
            case Method.TRACE /*6*/:
                return 64;
            case Method.PATCH /*7*/:
                return TLRPC.USER_FLAG_UNUSED;
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                return TLRPC.USER_FLAG_UNUSED2;
            case C0338R.styleable.PromptView_iconTint /*9*/:
                return TLRPC.USER_FLAG_UNUSED3;
            case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                return TLRPC.MESSAGE_FLAG_HAS_VIEWS;
            case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                return TLRPC.MESSAGE_FLAG_HAS_BOT_ID;
            case Atom.FULL_HEADER_SIZE /*12*/:
                return ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT;
            case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                return MessagesController.UPDATE_MASK_CHANNEL;
            case C0338R.styleable.PromptView_primaryTextFontFamily /*14*/:
                return MessagesController.UPDATE_MASK_CHAT_ADMINS;
            case C0338R.styleable.PromptView_primaryTextSize /*15*/:
                return TLRPC.MESSAGE_FLAG_EDITED;
            case TLRPC.USER_FLAG_PHONE /*16*/:
                return AccessibilityNodeInfoCompat.ACTION_CUT;
            case C0338R.styleable.PromptView_primaryTextTypeface /*17*/:
                return AccessibilityNodeInfoCompat.ACTION_SET_SELECTION;
            case C0338R.styleable.PromptView_secondaryText /*18*/:
                return AccessibilityNodeInfoCompat.ACTION_EXPAND;
            case C0338R.styleable.PromptView_secondaryTextColour /*19*/:
                return AccessibilityNodeInfoCompat.ACTION_COLLAPSE;
            case C0338R.styleable.PromptView_secondaryTextFontFamily /*20*/:
                return AccessibilityNodeInfoCompat.ACTION_DISMISS;
            case C0338R.styleable.PromptView_secondaryTextSize /*21*/:
                return AccessibilityNodeInfoCompat.ACTION_SET_TEXT;
            case C0338R.styleable.PromptView_secondaryTextStyle /*22*/:
                return AccessibilityEventCompat.TYPE_WINDOWS_CHANGED;
            case C0338R.styleable.PromptView_secondaryTextTypeface /*23*/:
                return AccessibilityEventCompat.TYPE_VIEW_CONTEXT_CLICKED;
            case C0338R.styleable.PromptView_target /*24*/:
                return AccessibilityEventCompat.TYPE_ASSIST_READING_CONTEXT;
            case C0338R.styleable.PromptView_textPadding /*25*/:
                return 33554432;
            case C0338R.styleable.PromptView_textSeparation /*26*/:
                return 67108864;
            default:
                return 0;
        }
    }

    public static List<MenuData> m1994a(boolean z) {
        int i = MoboConstants.f1345l;
        Collection<MenuData> arrayList = new ArrayList();
        Map a = MenuUtil.m1995a();
        arrayList.add(new MenuData(0, 0, (i & 1) != 0, null, ((Integer) a.get(Integer.valueOf(0))).intValue(), 0));
        arrayList.add(new MenuData(1, 0, (i & 2) != 0, null, ((Integer) a.get(Integer.valueOf(1))).intValue(), 1));
        arrayList.add(new MenuData(2, C0338R.drawable.menu_invite_telegram, (i & 4) != 0, LocaleController.getString("AddUser", C0338R.string.AddUser), ((Integer) a.get(Integer.valueOf(2))).intValue(), 3));
        arrayList.add(new MenuData(3, 0, (i & 8) != 0, null, ((Integer) a.get(Integer.valueOf(3))).intValue(), 2));
        if (!UserConfig.isRobot) {
            arrayList.add(new MenuData(4, C0338R.drawable.menu_chat_telegram, (i & 16) != 0, LocaleController.getString("NewChat", C0338R.string.NewChat), ((Integer) a.get(Integer.valueOf(4))).intValue(), 3));
            arrayList.add(new MenuData(5, 0, (i & 32) != 0, null, ((Integer) a.get(Integer.valueOf(5))).intValue(), 2));
        }
        arrayList.add(new MenuData(6, C0338R.drawable.menu_download_telegram, (i & 64) != 0, LocaleController.getString("DownloadManager", C0338R.string.DownloadManager), ((Integer) a.get(Integer.valueOf(6))).intValue(), 3));
        arrayList.add(new MenuData(7, C0338R.drawable.menu_filemanager_telegram, (i & TLRPC.USER_FLAG_UNUSED) != 0, LocaleController.getString("FileManager", C0338R.string.FileManager), ((Integer) a.get(Integer.valueOf(7))).intValue(), 3));
        arrayList.add(new MenuData(8, C0338R.drawable.menu_category_telegram, (i & TLRPC.USER_FLAG_UNUSED2) != 0, LocaleController.getString("CategoryManagement", C0338R.string.CategoryManagement), ((Integer) a.get(Integer.valueOf(8))).intValue(), 3));
        if (!UserConfig.isRobot) {
            arrayList.add(new MenuData(9, C0338R.drawable.menu_spcontacts_telegram, (i & TLRPC.USER_FLAG_UNUSED3) != 0, LocaleController.getString("SpecificContacts", C0338R.string.SpecificContacts), ((Integer) a.get(Integer.valueOf(9))).intValue(), 3));
            arrayList.add(new MenuData(10, C0338R.drawable.menu_contacts_telegram, (i & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0, LocaleController.getString("OnlineContacts", C0338R.string.OnlineContacts), ((Integer) a.get(Integer.valueOf(10))).intValue(), 3));
            arrayList.add(new MenuData(11, C0338R.drawable.menu_contacts_telegram, (i & TLRPC.MESSAGE_FLAG_HAS_BOT_ID) != 0, LocaleController.getString("ContactsChanges", C0338R.string.ContactsChanges), ((Integer) a.get(Integer.valueOf(11))).intValue(), 3));
        }
        arrayList.add(new MenuData(12, C0338R.drawable.menu_settings_telegram, (i & ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT) != 0, LocaleController.getString("MoboSettings", C0338R.string.MoboSettings), ((Integer) a.get(Integer.valueOf(12))).intValue(), 3));
        arrayList.add(new MenuData(13, 0, (i & MessagesController.UPDATE_MASK_CHANNEL) != 0, null, ((Integer) a.get(Integer.valueOf(13))).intValue(), 2));
        if (!UserConfig.isRobot) {
            arrayList.add(new MenuData(14, C0338R.drawable.menu_userfinder_telegram, (i & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0, LocaleController.getString("UsernameFinder", C0338R.string.UsernameFinder), ((Integer) a.get(Integer.valueOf(14))).intValue(), 3));
            arrayList.add(new MenuData(15, C0338R.drawable.menu_contacts_telegram, (TLRPC.MESSAGE_FLAG_EDITED & i) != 0, LocaleController.getString("Contacts", C0338R.string.Contacts), ((Integer) a.get(Integer.valueOf(15))).intValue(), 3));
        }
        arrayList.add(new MenuData(16, C0338R.drawable.menu_themes_telegram, (AccessibilityNodeInfoCompat.ACTION_CUT & i) != 0, LocaleController.getString("Themes", C0338R.string.Themes), ((Integer) a.get(Integer.valueOf(16))).intValue(), 3));
        arrayList.add(new MenuData(17, C0338R.drawable.menu_invite_telegram, (AccessibilityNodeInfoCompat.ACTION_SET_SELECTION & i) != 0, LocaleController.getString("InviteFriends", C0338R.string.InviteFriends), ((Integer) a.get(Integer.valueOf(17))).intValue(), 3));
        arrayList.add(new MenuData(18, C0338R.drawable.menu_settings_telegram, (AccessibilityNodeInfoCompat.ACTION_EXPAND & i) != 0, LocaleController.getString("Settings", C0338R.string.Settings), ((Integer) a.get(Integer.valueOf(18))).intValue(), 3));
        arrayList.add(new MenuData(19, C0338R.drawable.menu_help_telegram, (AccessibilityNodeInfoCompat.ACTION_COLLAPSE & i) != 0, LocaleController.getString("TelegramFaq", C0338R.string.TelegramFaq), ((Integer) a.get(Integer.valueOf(19))).intValue(), 3));
        arrayList.add(new MenuData(20, C0338R.drawable.menu_apps_telegram, (AccessibilityNodeInfoCompat.ACTION_DISMISS & i) != 0, LocaleController.getString("OtherApps", C0338R.string.OtherApps), ((Integer) a.get(Integer.valueOf(20))).intValue(), 3));
        arrayList.add(new MenuData(21, C0338R.drawable.menu_about_telegram, (AccessibilityNodeInfoCompat.ACTION_SET_TEXT & i) != 0, LocaleController.getString("About", C0338R.string.About), ((Integer) a.get(Integer.valueOf(21))).intValue(), 3));
        arrayList.add(new MenuData(22, C0338R.drawable.menu_dialogdm_telegram, (AccessibilityEventCompat.TYPE_WINDOWS_CHANGED & i) != 0, LocaleController.getString("ChatDownloadManager", C0338R.string.ChatDownloadManager), ((Integer) a.get(Integer.valueOf(22))).intValue(), 3));
        arrayList.add(new MenuData(23, C0338R.drawable.menu_moboplus, (AccessibilityEventCompat.TYPE_VIEW_CONTEXT_CLICKED & i) != 0, LocaleController.getString("MoboPlus", C0338R.string.MoboPlus), ((Integer) a.get(Integer.valueOf(23))).intValue(), 3));
        if (!UserConfig.isRobot) {
            arrayList.add(new MenuData(24, C0338R.drawable.menu_archive, (AccessibilityEventCompat.TYPE_ASSIST_READING_CONTEXT & i) != 0, LocaleController.getString("FavoriteMessages", C0338R.string.FavoriteMessages), ((Integer) a.get(Integer.valueOf(24))).intValue(), 3));
        }
        arrayList.add(new MenuData(25, 0, (33554432 & i) != 0, null, ((Integer) a.get(Integer.valueOf(25))).intValue(), 2));
        arrayList.add(new MenuData(26, C0338R.drawable.menu_power, (67108864 & i) != 0, LocaleController.getString("TurnOff", C0338R.string.TurnOff), ((Integer) a.get(Integer.valueOf(26))).intValue(), 3));
        List<MenuData> arrayList2 = new ArrayList();
        if (z) {
            for (MenuData menuData : arrayList) {
                if (menuData.m1975d()) {
                    arrayList2.add(menuData);
                }
            }
        } else {
            arrayList2.addAll(arrayList);
        }
        Collections.sort(arrayList2);
        return arrayList2;
    }

    public static Map<Integer, Integer> m1995a() {
        Map<Integer, Integer> hashMap = new HashMap();
        String[] split = MoboConstants.f1327T.split("@");
        if (split != null && split.length > 1) {
            for (String str : split) {
                if (str.length() > 0) {
                    hashMap.put(Integer.valueOf(Integer.parseInt(str.split("#")[0])), Integer.valueOf(Integer.parseInt(str.split("#")[1])));
                }
            }
        }
        if (hashMap.get(Integer.valueOf(0)) == null) {
            hashMap.put(Integer.valueOf(0), Integer.valueOf(10));
        }
        if (hashMap.get(Integer.valueOf(1)) == null) {
            hashMap.put(Integer.valueOf(1), Integer.valueOf(20));
        }
        if (hashMap.get(Integer.valueOf(2)) == null) {
            hashMap.put(Integer.valueOf(2), Integer.valueOf(30));
        }
        if (hashMap.get(Integer.valueOf(3)) == null) {
            hashMap.put(Integer.valueOf(3), Integer.valueOf(40));
        }
        if (hashMap.get(Integer.valueOf(4)) == null) {
            hashMap.put(Integer.valueOf(4), Integer.valueOf(50));
        }
        if (hashMap.get(Integer.valueOf(23)) == null) {
            hashMap.put(Integer.valueOf(23), Integer.valueOf(55));
        }
        if (hashMap.get(Integer.valueOf(5)) == null) {
            hashMap.put(Integer.valueOf(5), Integer.valueOf(60));
        }
        if (hashMap.get(Integer.valueOf(24)) == null) {
            hashMap.put(Integer.valueOf(24), Integer.valueOf(65));
        }
        if (hashMap.get(Integer.valueOf(6)) == null) {
            hashMap.put(Integer.valueOf(6), Integer.valueOf(70));
        }
        if (hashMap.get(Integer.valueOf(22)) == null) {
            hashMap.put(Integer.valueOf(22), Integer.valueOf(75));
        }
        if (hashMap.get(Integer.valueOf(7)) == null) {
            hashMap.put(Integer.valueOf(7), Integer.valueOf(80));
        }
        if (hashMap.get(Integer.valueOf(8)) == null) {
            hashMap.put(Integer.valueOf(8), Integer.valueOf(90));
        }
        if (hashMap.get(Integer.valueOf(9)) == null) {
            hashMap.put(Integer.valueOf(9), Integer.valueOf(100));
        }
        if (hashMap.get(Integer.valueOf(10)) == null) {
            hashMap.put(Integer.valueOf(10), Integer.valueOf(110));
        }
        if (hashMap.get(Integer.valueOf(11)) == null) {
            hashMap.put(Integer.valueOf(11), Integer.valueOf(120));
        }
        if (hashMap.get(Integer.valueOf(12)) == null) {
            hashMap.put(Integer.valueOf(12), Integer.valueOf(130));
        }
        if (hashMap.get(Integer.valueOf(13)) == null) {
            hashMap.put(Integer.valueOf(13), Integer.valueOf(140));
        }
        if (hashMap.get(Integer.valueOf(14)) == null) {
            hashMap.put(Integer.valueOf(14), Integer.valueOf(150));
        }
        if (hashMap.get(Integer.valueOf(15)) == null) {
            hashMap.put(Integer.valueOf(15), Integer.valueOf(160));
        }
        if (hashMap.get(Integer.valueOf(16)) == null) {
            hashMap.put(Integer.valueOf(16), Integer.valueOf(170));
        }
        if (hashMap.get(Integer.valueOf(17)) == null) {
            hashMap.put(Integer.valueOf(17), Integer.valueOf(180));
        }
        if (hashMap.get(Integer.valueOf(18)) == null) {
            hashMap.put(Integer.valueOf(18), Integer.valueOf(190));
        }
        if (hashMap.get(Integer.valueOf(19)) == null) {
            hashMap.put(Integer.valueOf(19), Integer.valueOf(Callback.DEFAULT_DRAG_ANIMATION_DURATION));
        }
        if (hashMap.get(Integer.valueOf(20)) == null) {
            hashMap.put(Integer.valueOf(20), Integer.valueOf(210));
        }
        if (hashMap.get(Integer.valueOf(21)) == null) {
            hashMap.put(Integer.valueOf(21), Integer.valueOf(220));
        }
        if (hashMap.get(Integer.valueOf(25)) == null) {
            hashMap.put(Integer.valueOf(25), Integer.valueOf(230));
        }
        if (hashMap.get(Integer.valueOf(26)) == null) {
            hashMap.put(Integer.valueOf(26), Integer.valueOf(PsExtractor.VIDEO_STREAM_MASK));
        }
        return hashMap;
    }

    public static void m1996a(List<MenuData> list) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        String str2 = str;
        for (MenuData menuData : list) {
            str2 = str2 + "@" + menuData.m1973b() + "#" + menuData.m1977f();
        }
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
        edit.putString("menus_orders", str2);
        edit.commit();
        MoboConstants.f1327T = str2;
    }
}
