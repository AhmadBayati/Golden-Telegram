package com.hanista.mobogram.mobo.p020s;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.p003d.ImageListActivity;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import java.io.File;

/* renamed from: com.hanista.mobogram.mobo.s.a */
public class AdvanceTheme {
    public static int f2464A;
    public static int f2465B;
    public static int f2466C;
    public static int f2467D;
    public static int f2468E;
    public static int f2469F;
    public static int f2470G;
    public static int f2471H;
    public static boolean f2472I;
    public static boolean f2473J;
    public static int f2474K;
    public static int f2475L;
    public static int f2476M;
    public static int f2477N;
    public static int f2478O;
    public static int f2479P;
    public static int f2480Q;
    public static int f2481R;
    public static int f2482S;
    public static int f2483T;
    public static int f2484U;
    public static int f2485V;
    public static int f2486W;
    public static int f2487X;
    public static int f2488Y;
    public static int f2489Z;
    public static boolean f2490a;
    public static int aA;
    public static int aB;
    public static int aC;
    public static int aD;
    public static int aE;
    public static int aF;
    public static int aG;
    public static int aH;
    public static int aI;
    public static int aJ;
    public static int aK;
    public static int aL;
    public static int aM;
    public static int aN;
    public static int aO;
    public static int aP;
    public static int aQ;
    public static int aR;
    public static int aS;
    public static int aT;
    public static int aU;
    public static int aV;
    public static int aW;
    public static int aX;
    public static int aY;
    public static int aZ;
    public static int aa;
    public static int ab;
    public static int ac;
    public static int ad;
    public static int ae;
    public static int af;
    public static int ag;
    public static int ah;
    public static int ai;
    public static int aj;
    public static int ak;
    public static int al;
    public static int am;
    public static int an;
    public static int ao;
    public static int ap;
    public static int aq;
    public static int ar;
    public static int as;
    public static int at;
    public static int au;
    public static int av;
    public static int aw;
    public static int ax;
    public static int ay;
    public static int az;
    public static int f2491b;
    public static int bA;
    public static int bB;
    public static int bC;
    public static String bD;
    public static int bE;
    public static int bF;
    public static int bG;
    public static int bH;
    public static int bI;
    public static int bJ;
    public static int bK;
    public static int bL;
    public static int bM;
    public static boolean bN;
    public static int bO;
    public static int bP;
    public static int bQ;
    public static int bR;
    public static int bS;
    public static int bT;
    public static int bU;
    public static int bV;
    public static int bW;
    public static int bX;
    public static int bY;
    public static boolean bZ;
    public static int ba;
    public static int bb;
    public static int bc;
    public static int bd;
    public static int be;
    public static int bf;
    public static int bg;
    public static int bh;
    public static int bi;
    public static int bj;
    public static int bk;
    public static int bl;
    public static int bm;
    public static int bn;
    public static int bo;
    public static int bp;
    public static int bq;
    public static int br;
    public static int bs;
    public static int bt;
    public static int bu;
    public static int bv;
    public static int bw;
    public static int bx;
    public static int by;
    public static int bz;
    public static int f2492c;
    public static boolean ca;
    public static int cb;
    public static String cc;
    public static int cd;
    public static boolean ce;
    public static int cf;
    public static int cg;
    public static int ch;
    public static int ci;
    public static int cj;
    public static int f2493d;
    public static int f2494e;
    public static int f2495f;
    public static int f2496g;
    public static int f2497h;
    public static int f2498i;
    public static int f2499j;
    public static int f2500k;
    public static int f2501l;
    public static int f2502m;
    public static int f2503n;
    public static int f2504o;
    public static int f2505p;
    public static int f2506q;
    public static int f2507r;
    public static int f2508s;
    public static int f2509t;
    public static int f2510u;
    public static int f2511v;
    public static int f2512w;
    public static int f2513x;
    public static int f2514y;
    public static int f2515z;

    public static int m2275a(int i, float f) {
        return Color.argb(Math.round(((float) Color.alpha(i)) * f), Color.red(i), Color.green(i), Color.blue(i));
    }

    public static int m2276a(int i, int i2) {
        return AdvanceTheme.m2283b(i, i2);
    }

    public static int m2277a(int i, int i2, float f) {
        int c = AdvanceTheme.m2286c(i, i2);
        return Color.argb(Math.round(((float) Color.alpha(c)) * f), Color.red(c), Color.green(c), Color.blue(c));
    }

    public static int m2278a(String str, int i) {
        return ApplicationLoader.applicationContext.getSharedPreferences("mobotheme", 0).getInt(str, i);
    }

    public static void m2279a() {
        if (new File(MoboUtils.m1735r(ApplicationLoader.applicationContext), "mobotheme.xml").exists()) {
            f2490a = true;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mobotheme", 0);
            f2491b = sharedPreferences.getInt("theme_color", -15684964);
            f2492c = sharedPreferences.getInt("header_icons_color", -1);
            f2493d = sharedPreferences.getInt("dialog_color", f2491b);
            f2494e = sharedPreferences.getInt("title_color", Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            f2495f = sharedPreferences.getInt("summary_color", -7697782);
            f2496g = sharedPreferences.getInt("shadow_color", Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
            f2497h = sharedPreferences.getInt("bg_color", -1);
            f2498i = sharedPreferences.getInt("divider_color", -2500135);
            f2499j = sharedPreferences.getInt("section_color", f2491b);
            f2500k = sharedPreferences.getInt("header_color", f2491b);
            f2501l = sharedPreferences.getInt("header_title_color", -1);
            f2502m = sharedPreferences.getInt("avatar_radius", 32);
            f2503n = sharedPreferences.getInt("avatar_color", AdvanceTheme.m2276a(f2491b, 16));
            f2504o = sharedPreferences.getInt("header_status_color", AdvanceTheme.m2276a(f2491b, -64));
            f2505p = sharedPreferences.getInt("avatar_size", 42);
            f2506q = sharedPreferences.getInt("font_type", FontUtil.m1179c());
            f2507r = sharedPreferences.getInt("main_header_icons_color", -1);
            f2508s = sharedPreferences.getInt("main_header_color", f2491b);
            f2509t = sharedPreferences.getInt("main_header_gradient", 0);
            f2510u = sharedPreferences.getInt("main_header_gradient_color", f2491b);
            f2511v = sharedPreferences.getInt("main_header_tab_icon_color", f2507r);
            f2512w = sharedPreferences.getInt("main_header_tab_unselected_icon_color", AdvanceTheme.m2275a(f2511v, 0.3f));
            f2513x = sharedPreferences.getInt("main_header_title_color", -1);
            f2514y = sharedPreferences.getInt("main_row_color", -1);
            f2515z = sharedPreferences.getInt("main_row_color", 0);
            f2464A = sharedPreferences.getInt("main_floating_bg_color", f2491b);
            f2465B = sharedPreferences.getInt("main_floating_pencil_color", -1);
            f2466C = sharedPreferences.getInt("main_header_tab_counter_size", 11);
            f2467D = sharedPreferences.getInt("main_header_tab_counter_color", -1);
            f2468E = sharedPreferences.getInt("main_header_tab_counter_bg_color", -2937041);
            f2469F = sharedPreferences.getInt("main_header_tab_counter_both_bg_color", f2468E);
            f2470G = sharedPreferences.getInt("main_header_tab_counter_silent_bg_color", -4605511);
            f2471H = sharedPreferences.getInt("drawer_avatar_size", 64);
            f2472I = sharedPreferences.getBoolean("drawer_center_avatar_check", false);
            f2473J = sharedPreferences.getBoolean("drawer_hide_bg_shadow_check", false);
            f2474K = sharedPreferences.getInt("drawer_header_color", f2491b);
            f2475L = sharedPreferences.getInt("drawer_header_gradient", 0);
            f2476M = sharedPreferences.getInt("drawer_header_gradient_color", f2491b);
            f2477N = sharedPreferences.getInt("drawer_name_color", -1);
            f2478O = sharedPreferences.getInt("drawer_name_size", 15);
            f2479P = sharedPreferences.getInt("drawer_phone_color", AdvanceTheme.m2276a(f2491b, -64));
            f2480Q = sharedPreferences.getInt("drawer_phone_size", 13);
            f2481R = sharedPreferences.getInt("drawer_avatar_color", AdvanceTheme.m2276a(f2491b, 21));
            f2482S = sharedPreferences.getInt("drawer_avatar_radius", 32);
            f2483T = sharedPreferences.getInt("drawer_icon_color", Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
            f2484U = sharedPreferences.getInt("drawer_list_color", -1);
            f2485V = sharedPreferences.getInt("drawer_row_gradient", 0);
            f2486W = sharedPreferences.getInt("drawer_row_gradient_color", -1);
            f2487X = sharedPreferences.getInt("drawer_option_color", -12303292);
            f2488Y = sharedPreferences.getInt("drawer_option_size", 15);
            f2489Z = sharedPreferences.getInt("dlg_name_color", 0);
            aa = sharedPreferences.getInt("dlg_count_size", 13);
            ab = sharedPreferences.getInt("dlg_count_color", -1);
            ac = sharedPreferences.getInt("dlg_count_bg_color", 0);
            ai = sharedPreferences.getInt("dlg_count_silent_bg_color", AdvanceTheme.m2286c(ac, -4605511));
            ad = sharedPreferences.getInt("dlg_checks_color", f2491b);
            ae = sharedPreferences.getInt("dlg_time_color", Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
            af = sharedPreferences.getInt("dlg_typing_color", f2491b);
            ag = sharedPreferences.getInt("dlg_time_size", 13);
            ah = sharedPreferences.getInt("dlg_media_color", 0);
            aj = sharedPreferences.getInt("dlg_group_name_color", 0);
            ak = sharedPreferences.getInt("dlg_mute_color", -5723992);
            al = sharedPreferences.getInt("dlg_group_icon_color", 0);
            am = sharedPreferences.getInt("dlg_message_color", 0);
            an = sharedPreferences.getInt("dlg_member_color", AdvanceTheme.m2276a(f2491b, 21));
            ao = sharedPreferences.getInt("dlg_name_size", 17);
            ap = sharedPreferences.getInt("dlg_unknown_name_color", AdvanceTheme.m2286c(f2489Z, Theme.STICKERS_SHEET_TITLE_TEXT_COLOR));
            aq = sharedPreferences.getInt("dlg_group_name_size", ao);
            ar = sharedPreferences.getInt("dlg_message_size", 16);
            as = sharedPreferences.getInt("dlg_divider_color", -2302756);
            at = sharedPreferences.getInt("dlg_avatar_radius", 32);
            au = sharedPreferences.getInt("dlg_highlight_search_color", AdvanceTheme.m2276a(f2491b, -64));
            av = sharedPreferences.getInt("dlg_row_gradient", 0);
            aw = sharedPreferences.getInt("dlg_row_gradient_color", -1);
            ax = sharedPreferences.getInt("profile_name_color", 0);
            ay = sharedPreferences.getInt("profile_name_size", 18);
            az = sharedPreferences.getInt("profile_status_size", 14);
            aA = sharedPreferences.getInt("profile_row_color", -1);
            aB = sharedPreferences.getInt("profile_title_color", Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            aC = sharedPreferences.getInt("profile_title_color", 0);
            aD = sharedPreferences.getInt("profile_header_icons_color", -1);
            aE = sharedPreferences.getInt("profile_avatar_radius", 32);
            aF = sharedPreferences.getInt("profile_icons_color", Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
            aG = sharedPreferences.getInt("profile_row_avatar_radius", 32);
            aH = sharedPreferences.getInt("profile_summary_color", -7697782);
            aI = sharedPreferences.getInt("profile_online_color", AdvanceTheme.m2276a(f2491b, -64));
            aJ = sharedPreferences.getInt("profile_status_color", AdvanceTheme.m2276a(f2491b, -64));
            aK = sharedPreferences.getInt("profile_row_gradient", 0);
            aL = sharedPreferences.getInt("profile_row_gradient_color", -1);
            aM = sharedPreferences.getInt("profile_header_color", f2491b);
            aN = sharedPreferences.getInt("profile_header_gradient", 0);
            aO = sharedPreferences.getInt("profile_header_gradient_color", f2491b);
            aP = sharedPreferences.getInt("contacts_status_color", -5723992);
            aQ = sharedPreferences.getInt("contacts_online_color", AdvanceTheme.m2276a(f2491b, 21));
            aR = sharedPreferences.getInt("contacts_name_color", Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            aS = sharedPreferences.getInt("contacts_name_color", 0);
            aT = sharedPreferences.getInt("contacts_icons_color", Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
            aU = sharedPreferences.getInt("contacts_row_color", -1);
            aV = sharedPreferences.getInt("contacts_row_gradient", 0);
            aW = sharedPreferences.getInt("contacts_row_gradient_color", -1);
            aX = sharedPreferences.getInt("contacts_name_size", 17);
            aY = sharedPreferences.getInt("contacts_avatar_radius", 32);
            aZ = sharedPreferences.getInt("contacts_status_size", 14);
            ba = sharedPreferences.getInt("contacts_header_icons_color", -1);
            bb = sharedPreferences.getInt("contacts_header_title_color", -1);
            bc = sharedPreferences.getInt("contacts_header_color", f2491b);
            bd = sharedPreferences.getInt("contacts_header_gradient", 0);
            be = sharedPreferences.getInt("contacts_header_gradient_color", f2491b);
            bf = sharedPreferences.getInt("chat_attach_text_color", Theme.ATTACH_SHEET_TEXT_COLOR);
            bg = sharedPreferences.getInt("chat_header_color", f2491b);
            bh = sharedPreferences.getInt("chat_name_color", -1);
            bi = sharedPreferences.getInt("chat_header_icons_color", -1);
            bj = sharedPreferences.getInt("chat_attach_bg_color", -1);
            bk = sharedPreferences.getInt("chat_attach_bg_gradient", 0);
            bl = sharedPreferences.getInt("chat_attach_bg_gradient_color", -1);
            bm = sharedPreferences.getInt("chat_text_size", AndroidUtilities.isTablet() ? 18 : 16);
            bn = sharedPreferences.getInt("chat_date_bubble_color", 0);
            bo = sharedPreferences.getInt("chat_left_bubble_color", -1);
            br = sharedPreferences.getInt("chat_left_link_color", f2491b);
            bs = sharedPreferences.getInt("chat_left_link_color", 0);
            bp = sharedPreferences.getInt("chat_left_text_color", Theme.MSG_TEXT_COLOR);
            bq = sharedPreferences.getInt("chat_left_text_color", 0);
            bt = sharedPreferences.getInt("chat_right_text_color", Theme.MSG_TEXT_COLOR);
            bu = sharedPreferences.getInt("chat_right_text_color", 0);
            bw = sharedPreferences.getInt("chat_forward_right_color", AdvanceTheme.m2283b(f2491b, 21));
            bx = sharedPreferences.getInt("chat_forward_left_color", f2491b);
            by = sharedPreferences.getInt("chat_avatar_radius", 32);
            bz = sharedPreferences.getInt("chat_right_bubble_color", AdvanceTheme.m2282b());
            bv = sharedPreferences.getInt("chat_right_link_color", f2491b);
            bA = sharedPreferences.getInt("chat_contact_name_color", f2491b);
            bB = sharedPreferences.getInt("chat_member_color", AdvanceTheme.m2276a(f2491b, 21));
            bC = sharedPreferences.getInt("chat_checks_color", f2491b);
            bE = sharedPreferences.getInt("chat_right_time_color", AdvanceTheme.m2276a(f2491b, 21));
            bF = sharedPreferences.getInt("chat_left_time_color", 0);
            bG = sharedPreferences.getInt("chat_edit_text_icons_color", 0);
            bH = sharedPreferences.getInt("chat_edit_text_bg_color", -1);
            bI = sharedPreferences.getInt("chat_edit_text_color", 0);
            bJ = sharedPreferences.getInt("chat_send_icon_color", AdvanceTheme.m2286c(bG, f2491b));
            bK = sharedPreferences.getInt("chat_edit_text_size", 18);
            bL = sharedPreferences.getInt("chat_edit_text_bg_gradient", 0);
            bM = sharedPreferences.getInt("chat_edit_text_bg_gradient_color", -1);
            bN = sharedPreferences.getBoolean("chat_solid_bg_color_check", false);
            bO = sharedPreferences.getInt("chat_solid_bg_color", -1);
            bP = sharedPreferences.getInt("chat_gradient_bg", 0);
            bQ = sharedPreferences.getInt("chat_gradient_bg_color", -1);
            bR = sharedPreferences.getInt("chat_header_gradient", 0);
            bS = sharedPreferences.getInt("chat_header_gradient_color", f2491b);
            bT = sharedPreferences.getInt("chat_status_color", AdvanceTheme.m2276a(f2491b, -64));
            bU = sharedPreferences.getInt("chat_typing_color", bT);
            bV = sharedPreferences.getInt("chat_date_color", 0);
            bW = sharedPreferences.getInt("chat_date_size", 0);
            bX = sharedPreferences.getInt("chat_name_size", 18);
            bY = sharedPreferences.getInt("chat_status_size", 14);
            bD = sharedPreferences.getString("chat_check_style", ImageListActivity.m528b(0));
            bZ = sharedPreferences.getBoolean("chat_show_username_check", false);
            ca = sharedPreferences.getBoolean("chat_member_color_check", false);
            cb = sharedPreferences.getInt("chat_header_avatar_radius", 32);
            cc = sharedPreferences.getString("chat_bubble_style", ImageListActivity.m525a(0));
            cd = sharedPreferences.getInt("chat_command_color", f2491b);
            ce = sharedPreferences.getBoolean("chat_command_color_check", false);
            ch = sharedPreferences.getInt("emoji_view_bg_color", -657673);
            ci = sharedPreferences.getInt("emoji_view_tab_color", AdvanceTheme.m2276a(f2491b, -21));
            cj = sharedPreferences.getInt("emoji_view_tab_icon_color", -5723992);
            cf = sharedPreferences.getInt("emoji_view_bg_gradient", 0);
            cg = sharedPreferences.getInt("emoji_view_bg_gradient_color", -657673);
            return;
        }
        f2490a = false;
    }

    public static void m2280a(int i) {
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mobotheme", 0).edit();
        edit.putInt("chat_text_size", i);
        edit.commit();
        bm = i;
    }

    public static void m2281a(boolean z) {
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mobotheme", 0).edit();
        edit.putBoolean("chat_solid_bg_color_check", z);
        edit.commit();
        bN = z;
    }

    public static int m2282b() {
        return f2491b != -16738680 ? AdvanceTheme.m2276a(f2491b, -80) : -5054501;
    }

    public static int m2283b(int i, int i2) {
        int alpha = Color.alpha(i);
        int red = Color.red(i) - i2;
        int green = Color.green(i) - i2;
        int blue = Color.blue(i) - i2;
        if (i2 < 0) {
            if (red > NalUnitUtil.EXTENDED_SAR) {
                red = NalUnitUtil.EXTENDED_SAR;
            }
            if (green > NalUnitUtil.EXTENDED_SAR) {
                green = NalUnitUtil.EXTENDED_SAR;
            }
            if (blue > NalUnitUtil.EXTENDED_SAR) {
                blue = NalUnitUtil.EXTENDED_SAR;
            }
            if (red == NalUnitUtil.EXTENDED_SAR && r1 == NalUnitUtil.EXTENDED_SAR && r0 == NalUnitUtil.EXTENDED_SAR) {
                blue = i2;
                green = i2;
                red = i2;
            }
        }
        if (i2 > 0) {
            if (red < 0) {
                red = 0;
            }
            if (green < 0) {
                green = 0;
            }
            if (blue < 0) {
                blue = 0;
            }
            if (red == 0 && r1 == 0 && r0 == 0) {
                blue = i2;
                green = i2;
                return Color.argb(alpha, green, blue, i2);
            }
        }
        i2 = blue;
        blue = green;
        green = red;
        return Color.argb(alpha, green, blue, i2);
    }

    public static int m2284b(int i, int i2, float f) {
        int c = AdvanceTheme.m2286c(i, i2);
        return Color.argb(Math.round(((float) Color.alpha(c)) * f), Color.red(c), Color.green(c), Color.blue(c));
    }

    public static void m2285b(int i) {
        Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mobotheme", 0).edit();
        edit.putInt("font_type", i);
        edit.commit();
        f2506q = i;
    }

    public static int m2286c(int i, int i2) {
        return i == 0 ? i2 : i;
    }
}
