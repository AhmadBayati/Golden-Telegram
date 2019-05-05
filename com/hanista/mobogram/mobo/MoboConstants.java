package com.hanista.mobogram.mobo;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.tgnet.TLRPC;
import java.io.File;
import java.util.Map.Entry;

/* renamed from: com.hanista.mobogram.mobo.k */
public class MoboConstants {
    public static int f1308A;
    public static boolean f1309B;
    public static boolean f1310C;
    public static boolean f1311D;
    public static boolean f1312E;
    public static boolean f1313F;
    public static int f1314G;
    public static int f1315H;
    public static int f1316I;
    public static int f1317J;
    public static boolean f1318K;
    public static boolean f1319L;
    public static boolean f1320M;
    public static boolean f1321N;
    public static boolean f1322O;
    public static boolean f1323P;
    public static boolean f1324Q;
    public static String f1325R;
    public static String f1326S;
    public static String f1327T;
    public static boolean f1328U;
    public static int f1329V;
    public static boolean f1330W;
    public static boolean f1331X;
    public static boolean f1332Y;
    public static boolean f1333Z;
    public static boolean f1334a;
    public static int aA;
    public static int aB;
    public static int aC;
    public static boolean aD;
    public static boolean aE;
    public static boolean aF;
    public static int aG;
    public static boolean aH;
    public static boolean aI;
    public static boolean aJ;
    public static boolean aK;
    public static boolean aL;
    public static boolean aM;
    public static boolean aN;
    public static boolean aO;
    public static boolean aP;
    public static int aQ;
    public static int aR;
    public static int aS;
    public static boolean aT;
    public static boolean aU;
    public static boolean aV;
    public static boolean aa;
    public static boolean ab;
    public static boolean ac;
    public static boolean ad;
    public static boolean ae;
    public static boolean af;
    public static boolean ag;
    public static boolean ah;
    public static boolean ai;
    public static boolean aj;
    public static int ak;
    public static boolean al;
    public static int am;
    public static boolean an;
    public static boolean ao;
    public static int ap;
    public static int aq;
    public static int ar;
    public static int as;
    public static boolean at;
    public static boolean au;
    public static boolean av;
    public static int aw;
    public static int ax;
    public static int ay;
    public static boolean az;
    public static boolean f1335b;
    public static boolean f1336c;
    public static boolean f1337d;
    public static boolean f1338e;
    public static boolean f1339f;
    public static boolean f1340g;
    public static boolean f1341h;
    public static boolean f1342i;
    public static boolean f1343j;
    public static int f1344k;
    public static int f1345l;
    public static int f1346m;
    public static boolean f1347n;
    public static boolean f1348o;
    public static boolean f1349p;
    public static boolean f1350q;
    public static boolean f1351r;
    public static boolean f1352s;
    public static boolean f1353t;
    public static boolean f1354u;
    public static boolean f1355v;
    public static int f1356w;
    public static int f1357x;
    public static boolean f1358y;
    public static boolean f1359z;

    static {
        f1334a = true;
        f1335b = true;
        f1336c = true;
        f1337d = true;
        f1338e = false;
        f1339f = false;
        f1340g = false;
        f1341h = false;
        f1342i = false;
        f1343j = false;
        f1347n = true;
        f1348o = true;
        f1349p = true;
        f1350q = true;
        f1351r = true;
        f1352s = false;
        f1353t = false;
        f1354u = false;
        f1355v = true;
        f1358y = false;
        f1359z = true;
        f1308A = 1;
        f1309B = true;
        f1310C = true;
        f1311D = true;
        f1312E = true;
        f1313F = true;
        f1325R = "Telegram";
        ao = true;
        aL = true;
        aM = true;
        aN = true;
        aU = true;
        aV = false;
    }

    public static void m1379a() {
        MoboConstants.m1383d();
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
        f1334a = sharedPreferences.getBoolean("show_tab", true);
        f1344k = sharedPreferences.getInt("visible_tabs", NalUnitUtil.EXTENDED_SAR);
        f1345l = sharedPreferences.getInt("visible_menus", 134217727);
        boolean z = f1334a && sharedPreferences.getBoolean("swipe_on_tabs", true) && f1344k != 0;
        f1335b = z;
        f1336c = sharedPreferences.getBoolean("separate_mutual_contacts", true);
        f1337d = sharedPreferences.getBoolean("shamsi_for_all_locales", true);
        f1338e = sharedPreferences.getBoolean("ghost_mode", false);
        f1340g = sharedPreferences.getBoolean("not_send_read_state", false);
        f1339f = sharedPreferences.getBoolean("hide_typing_state", false);
        f1342i = sharedPreferences.getBoolean("multi_forward", false);
        f1343j = sharedPreferences.getBoolean("forward_no_name_without_caption", false);
        f1346m = sharedPreferences.getInt("auto_dl_favorites_mask", 0);
        f1347n = sharedPreferences.getBoolean("show_last_seen_icon", true);
        f1348o = sharedPreferences.getBoolean("show_ghost_icon", true);
        f1349p = sharedPreferences.getBoolean("show_ghost_state_icon", true);
        f1350q = sharedPreferences.getBoolean("show_contact_status", true);
        f1352s = sharedPreferences.getBoolean("confirm_before_send_sticker", false);
        f1353t = sharedPreferences.getBoolean("confirm_before_send_voice", false);
        f1351r = sharedPreferences.getBoolean("show_contact_status_in_group", true);
        f1354u = sharedPreferences.getBoolean("keep_original_file_name", false);
        f1355v = sharedPreferences.getBoolean("donot_close_last_chat", true);
        f1356w = sharedPreferences.getInt("show_direct_share_btn_mask", 8);
        f1357x = sharedPreferences.getInt("show_direct_reply_btn_mask", 0);
        f1359z = sharedPreferences.getBoolean("show_categories_at_startup", false);
        f1358y = sharedPreferences.getBoolean("show_category_icon", true);
        f1308A = sharedPreferences.getInt("category_list_order", 1);
        f1309B = sharedPreferences.getBoolean("favorite_stickers", true);
        f1310C = sharedPreferences.getBoolean("favorite_emojis", true);
        String str = "auto_sync_contacts";
        z = (MoboUtils.m1727j(ApplicationLoader.applicationContext) || MoboUtils.m1728k(ApplicationLoader.applicationContext)) ? false : true;
        f1311D = sharedPreferences.getBoolean(str, z);
        f1312E = sharedPreferences.getBoolean("show_hidden_chats_in_share", true);
        f1320M = sharedPreferences.getBoolean("show_exact_members_and_views", true);
        f1321N = sharedPreferences.getBoolean("use_front_speaker_on_sensor", true);
        f1313F = sharedPreferences.getBoolean("schedule_download_alarm", false);
        f1314G = sharedPreferences.getInt("schedule_download_alarm_start_hour", 2);
        f1315H = sharedPreferences.getInt("schedule_download_alarm_start_minute", 0);
        f1316I = sharedPreferences.getInt("schedule_download_alarm_end_hour", 8);
        f1317J = sharedPreferences.getInt("schedule_download_alarm_end_minute", 0);
        f1318K = sharedPreferences.getBoolean("turn_on_wifi_on_start", false);
        f1319L = sharedPreferences.getBoolean("turn_off_wifi_on_end", false);
        ao = sharedPreferences.getBoolean("schedule_dialog_dm_alarm", false);
        ap = sharedPreferences.getInt("schedule_dialog_dm_alarm_start_hour", 2);
        aq = sharedPreferences.getInt("schedule_dialog_dm_alarm_start_minute", 0);
        ar = sharedPreferences.getInt("schedule_dialog_dm_alarm_end_hour", 8);
        as = sharedPreferences.getInt("schedule_dialog_dm_alarm_end_minute", 0);
        at = sharedPreferences.getBoolean("dialog_dm_turn_on_wifi_on_start", false);
        au = sharedPreferences.getBoolean("dialog_dm_turn_off_wifi_on_end", false);
        f1322O = sharedPreferences.getBoolean("specific_contact_service_enabled", false);
        f1323P = sharedPreferences.getBoolean("specific_contact_notification", true);
        f1324Q = sharedPreferences.getBoolean("tablet_mode", true);
        f1325R = sharedPreferences.getString("storage_folder_name", "Telegram");
        f1326S = sharedPreferences.getString("tabs_orders", TtmlNode.ANONYMOUS_REGION_ID);
        f1327T = sharedPreferences.getString("menus_orders", TtmlNode.ANONYMOUS_REGION_ID);
        f1328U = sharedPreferences.getBoolean("show_admin_chats_in_creator", false);
        ab = sharedPreferences.getBoolean("show_date_toast", true);
        ac = sharedPreferences.getBoolean("copy_transmitter_name", false);
        ad = sharedPreferences.getBoolean("small_stickers", false);
        ae = sharedPreferences.getBoolean("drawing_feature", true);
        af = sharedPreferences.getBoolean("show_gif_fullscreen", true);
        f1329V = sharedPreferences.getInt("default_tab", 0);
        f1331X = sharedPreferences.getBoolean("show_tabs_in_bottom", false);
        f1332Y = sharedPreferences.getBoolean("show_tabs_unread_count", true);
        f1333Z = sharedPreferences.getBoolean("count_muted_messages", true);
        aa = sharedPreferences.getBoolean("count_chats_instead_of_messages", false);
        ah = sharedPreferences.getBoolean("multi_forward_show_tabs", true);
        aj = sharedPreferences.getBoolean("multi_forward_show_as_list", false);
        ai = sharedPreferences.getBoolean("multi_forward_show_phone_contact_tab", true);
        ak = sharedPreferences.getInt("multi_forward_default_tab", 0);
        al = sharedPreferences.getBoolean("multi_forward_last_selected_tab", false);
        ag = sharedPreferences.getBoolean("hide_phone", false);
        am = sharedPreferences.getInt("hidden_chats_entering_method", 100);
        an = sharedPreferences.getBoolean("hidden_chats_show_notifications", true);
        av = sharedPreferences.getBoolean("chat_bar_show", true);
        aw = sharedPreferences.getInt("chat_bar_chat_state", 7);
        ax = sharedPreferences.getInt("chat_bar_chat_types", 31);
        ay = sharedPreferences.getInt("chat_bar_count", 100);
        az = sharedPreferences.getBoolean("chat_bar_open_as_default", false);
        aA = sharedPreferences.getInt("chat_bar_height", 80);
        aB = sharedPreferences.getInt("touch_contact_avatar", 2);
        aC = sharedPreferences.getInt("touch_group_avatar", 1);
        aD = sharedPreferences.getBoolean("use_internal_video_player", true);
        aE = sharedPreferences.getBoolean("delete_file_on_delete_message", true);
        z = sharedPreferences.getBoolean("archive_feature", true) && !UserConfig.isRobot;
        aF = z;
        aG = sharedPreferences.getInt("archive_button_mask", UserConfig.isRobot ? 0 : 8);
        z = sharedPreferences.getBoolean("archive_button_for_my_msg", false) && aF;
        aH = z;
        z = sharedPreferences.getBoolean("archive_categorizing", true) && aF;
        aI = z;
        aJ = sharedPreferences.getBoolean("archive_show_my_chat", true);
        aK = sharedPreferences.getBoolean("archive_forward_without_quoting", false);
        aL = sharedPreferences.getBoolean("notif_show_reply_btn", true);
        aM = sharedPreferences.getBoolean("notif_show_mark_as_read_btn", true);
        aN = sharedPreferences.getBoolean("notif_mark_as_read_in_popup", true);
        aO = sharedPreferences.getBoolean("turn_off", false);
        f1330W = sharedPreferences.getBoolean("hide_camera_in_attach_panel", false);
        aP = sharedPreferences.getBoolean("use_old_emojis", false);
        aT = sharedPreferences.getBoolean("voice_changer", true);
        aQ = sharedPreferences.getInt("voice_changer_type", 0);
        aR = sharedPreferences.getInt("record_speed", 11025);
        aS = sharedPreferences.getInt("transpose_semitone", 5);
        aU = sharedPreferences.getBoolean("show_gif_as_video", true);
        aV = sharedPreferences.getBoolean("block_ads", false);
        SettingManager settingManager = new SettingManager();
        if (!settingManager.m944b("unreadTabAdded")) {
            settingManager.m943a("unreadTabAdded", true);
            Editor edit = sharedPreferences.edit();
            edit.putInt("visible_tabs", f1344k | 32);
            edit.commit();
            MoboConstants.m1379a();
        }
        if (!settingManager.m944b("dialogDmTabAdded")) {
            settingManager.m943a("dialogDmTabAdded", true);
            edit = sharedPreferences.edit();
            edit.putInt("visible_menus", f1345l | AccessibilityEventCompat.TYPE_WINDOWS_CHANGED);
            edit.commit();
            MoboConstants.m1379a();
        }
        if (!settingManager.m944b("moboPlusMenuAdded")) {
            settingManager.m943a("moboPlusMenuAdded", true);
            edit = sharedPreferences.edit();
            edit.putInt("visible_menus", f1345l | AccessibilityEventCompat.TYPE_VIEW_CONTEXT_CLICKED);
            edit.commit();
            MoboConstants.m1379a();
        }
        if (!settingManager.m944b("archiveMenuAdded")) {
            settingManager.m943a("archiveMenuAdded", true);
            edit = sharedPreferences.edit();
            edit.putInt("visible_menus", f1345l | AccessibilityEventCompat.TYPE_ASSIST_READING_CONTEXT);
            edit.commit();
            MoboConstants.m1379a();
        }
        if (!settingManager.m944b("powerMenuAdded")) {
            settingManager.m943a("powerMenuAdded", true);
            edit = sharedPreferences.edit();
            edit.putInt("visible_menus", (f1345l | 33554432) | 67108864);
            edit.commit();
            MoboConstants.m1379a();
        }
        if (!settingManager.m944b("superGroupsTabAdded")) {
            settingManager.m943a("superGroupsTabAdded", true);
            edit = sharedPreferences.edit();
            edit.putInt("visible_tabs", f1344k | 64);
            edit.commit();
            MoboConstants.m1379a();
        }
        if (!settingManager.m944b("allTabAdded")) {
            settingManager.m943a("allTabAdded", true);
            Editor edit2 = sharedPreferences.edit();
            edit2.putInt("visible_tabs", f1344k | TLRPC.USER_FLAG_UNUSED);
            edit2.commit();
            MoboConstants.m1379a();
        }
    }

    private static void m1380a(SharedPreferences sharedPreferences, SharedPreferences sharedPreferences2) {
        Editor edit = sharedPreferences2.edit();
        for (Entry entry : sharedPreferences.getAll().entrySet()) {
            Object value = entry.getValue();
            String str = (String) entry.getKey();
            if (value instanceof Boolean) {
                edit.putBoolean(str, ((Boolean) value).booleanValue());
            } else if (value instanceof Float) {
                edit.putFloat(str, ((Float) value).floatValue());
            } else if (value instanceof Integer) {
                edit.putInt(str, ((Integer) value).intValue());
            } else if (value instanceof Long) {
                edit.putLong(str, ((Long) value).longValue());
            } else if (value instanceof String) {
                edit.putString(str, (String) value);
            }
        }
        edit.commit();
    }

    public static File m1381b() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String string = sharedPreferences.getString("selected_storage", absolutePath);
        if (string.equals(absolutePath)) {
            return Environment.getExternalStorageDirectory();
        }
        File file = new File(string);
        return !MoboUtils.m1703a(file) ? Environment.getExternalStorageDirectory() : file;
    }

    public static boolean m1382c() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String string = sharedPreferences.getString("selected_storage", absolutePath);
        return string.equals(absolutePath) ? "mounted".equals(Environment.getExternalStorageState()) : MoboUtils.m1703a(new File(string)) ? true : "mounted".equals(Environment.getExternalStorageState());
    }

    private static void m1383d() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
        if (!sharedPreferences.getBoolean("prefs_converted", false)) {
            MoboConstants.m1380a(ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0), sharedPreferences);
            MoboConstants.m1380a(ApplicationLoader.applicationContext.getSharedPreferences("SpecificContactNotifications", 0), sharedPreferences);
            Editor edit = sharedPreferences.edit();
            SharedPreferences sharedPreferences2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            if (sharedPreferences2.getInt("main_theme_color", -100) != -100) {
                edit.putInt("main_theme_color", sharedPreferences2.getInt("main_theme_color", -100));
            }
            if (sharedPreferences2.getBoolean("theme_back_white", false)) {
                edit.putBoolean("theme_back_white", sharedPreferences2.getBoolean("theme_back_white", false));
            }
            edit.putBoolean("prefs_converted", true);
            edit.commit();
        }
    }
}
