package com.hanista.mobogram.mobo.p010k;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p010k.p011a.TapTarget;
import com.hanista.mobogram.mobo.p010k.p011a.TapTargetSequence.TapTargetSequence;
import com.hanista.mobogram.mobo.p020s.BlackTheme;
import com.hanista.mobogram.mobo.p020s.BlueGreyTheme;
import com.hanista.mobogram.mobo.p020s.BrownTheme;
import com.hanista.mobogram.mobo.p020s.CyanTheme;
import com.hanista.mobogram.mobo.p020s.GreenTheme;
import com.hanista.mobogram.mobo.p020s.IndigoTheme;
import com.hanista.mobogram.mobo.p020s.PinkTheme;
import com.hanista.mobogram.mobo.p020s.PurpleTheme;
import com.hanista.mobogram.mobo.p020s.RedTheme;
import com.hanista.mobogram.mobo.p020s.TelegramTheme;
import com.hanista.mobogram.mobo.p020s.Theme;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Cells.DrawerActionCell;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.k.a */
public class MaterialHelperUtil {
    private static boolean f1302a;
    private static boolean f1303b;
    private static boolean f1304c;
    private static boolean f1305d;
    private static boolean f1306e;
    private static boolean f1307f;

    /* renamed from: com.hanista.mobogram.mobo.k.a.1 */
    static class MaterialHelperUtil implements TapTargetSequence {
        MaterialHelperUtil() {
        }

        public void m1269a() {
            MaterialHelperUtil.f1302a = false;
        }

        public void m1270a(TapTarget tapTarget) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.2 */
    static class MaterialHelperUtil implements TapTargetSequence {
        MaterialHelperUtil() {
        }

        public void m1271a() {
            MaterialHelperUtil.f1303b = false;
        }

        public void m1272a(TapTarget tapTarget) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.3 */
    static class MaterialHelperUtil implements TapTargetSequence {
        MaterialHelperUtil() {
        }

        public void m1273a() {
            MaterialHelperUtil.f1304c = false;
        }

        public void m1274a(TapTarget tapTarget) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.4 */
    static class MaterialHelperUtil implements TapTargetSequence {
        MaterialHelperUtil() {
        }

        public void m1275a() {
            MaterialHelperUtil.f1305d = false;
        }

        public void m1276a(TapTarget tapTarget) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.5 */
    static class MaterialHelperUtil implements OnScrollListener {
        int f1197a;
        int f1198b;
        int f1199c;
        final /* synthetic */ Activity f1200d;
        final /* synthetic */ ListView f1201e;

        MaterialHelperUtil(Activity activity, ListView listView) {
            this.f1200d = activity;
            this.f1201e = listView;
        }

        private void m1277a() {
            if (this.f1198b > 0 && this.f1199c == 0) {
                MaterialHelperUtil.m1366a(this.f1200d, this.f1201e);
            }
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            this.f1197a = i;
            this.f1198b = i2;
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            this.f1199c = i;
            m1277a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.6 */
    static class MaterialHelperUtil implements TapTargetSequence {
        MaterialHelperUtil() {
        }

        public void m1278a() {
            MaterialHelperUtil.f1306e = false;
        }

        public void m1279a(TapTarget tapTarget) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.7 */
    static class MaterialHelperUtil implements TapTargetSequence {
        MaterialHelperUtil() {
        }

        public void m1280a() {
            MaterialHelperUtil.f1307f = false;
        }

        public void m1281a(TapTarget tapTarget) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.8 */
    static class MaterialHelperUtil implements TapTargetSequence {
        MaterialHelperUtil() {
        }

        public void m1282a() {
        }

        public void m1283a(TapTarget tapTarget) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.9 */
    static class MaterialHelperUtil implements TapTargetSequence {
        MaterialHelperUtil() {
        }

        public void m1284a() {
        }

        public void m1285a(TapTarget tapTarget) {
        }
    }

    private static SharedPreferences m1355a() {
        return ApplicationLoader.applicationContext.getSharedPreferences("mobohelp", 0);
    }

    private static TapTarget m1356a(Rect rect, String str, String str2, Theme theme) {
        return MaterialHelperUtil.m1358a(TapTarget.m1295a(rect, (CharSequence) str, (CharSequence) str2), theme);
    }

    private static TapTarget m1357a(View view, String str, String str2, Theme theme) {
        return MaterialHelperUtil.m1358a(TapTarget.m1296a(view, (CharSequence) str, (CharSequence) str2), theme);
    }

    private static TapTarget m1358a(TapTarget tapTarget, Theme theme) {
        tapTarget.m1298a(theme.m2289c()).m1306d(C0338R.color.white).m1307e(18).m1308f(16).m1302b((int) C0338R.color.white).m1309g(C0338R.color.black).m1300a(true).m1303b(true).m1305c(true).m1304c((int) C0338R.color.white);
        if (FontUtil.m1176a().m1160c() != null) {
            tapTarget.m1299a(FontUtil.m1176a().m1160c());
        }
        return tapTarget;
    }

    public static Boolean m1359a(String str) {
        boolean z = true;
        if (MoboUtils.m1727j(ApplicationLoader.applicationContext) || MoboUtils.m1728k(ApplicationLoader.applicationContext)) {
            return Boolean.valueOf(true);
        }
        if (!MaterialHelperUtil.m1355a().getBoolean(str, false) || MaterialHelperUtil.m1355a().getBoolean("disable_help", false)) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    public static void m1360a(Activity activity, View view) {
        if (activity != null) {
            List arrayList = new ArrayList();
            if (!(MaterialHelperUtil.m1359a("profileSearchMembersHelp").booleanValue() || view == null)) {
                MaterialHelperUtil.m1373b("profileSearchMembersHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view, LocaleController.getString("HelpProfileSearchMembersTitle", C0338R.string.HelpProfileSearchMembersTitle), LocaleController.getString("HelpProfileSearchMembersDetail", C0338R.string.HelpProfileSearchMembersDetail), new GreenTheme()));
            }
            MaterialHelperUtil.m1368a(activity, arrayList);
        }
    }

    public static void m1361a(Activity activity, View view, View view2) {
        if (activity != null && !f1304c) {
            f1304c = true;
            List arrayList = new ArrayList();
            if (!(MaterialHelperUtil.m1359a("messagesActionsHelp").booleanValue() || view == null)) {
                MaterialHelperUtil.m1373b("messagesActionsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view, LocaleController.getString("HelpMessagesActionsTitle", C0338R.string.HelpMessagesActionsTitle), LocaleController.getString("HelpMessagesActionsDetail", C0338R.string.HelpMessagesActionsDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("messagesActionsSelectAllHelp").booleanValue() || view2 == null)) {
                MaterialHelperUtil.m1373b("messagesActionsSelectAllHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view2, LocaleController.getString("HelpMessagesActionsSelectAllTitle", C0338R.string.HelpMessagesActionsSelectAllTitle), LocaleController.getString("HelpMessagesActionsSelectAllDetail", C0338R.string.HelpMessagesActionsSelectAllDetail), new GreenTheme()));
            }
            if (arrayList.size() > 0) {
                new com.hanista.mobogram.mobo.p010k.p011a.TapTargetSequence(activity).m1317a(arrayList).m1318a(true).m1316a(new MaterialHelperUtil()).m1319a();
            } else {
                f1304c = false;
            }
        }
    }

    public static void m1362a(Activity activity, View view, View view2, View view3) {
        if (activity != null) {
            List arrayList = new ArrayList();
            if (!(MaterialHelperUtil.m1359a("mediaTypeHelp").booleanValue() || view == null)) {
                MaterialHelperUtil.m1373b("mediaTypeHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view, LocaleController.getString("HelpMediaTypeTitle", C0338R.string.HelpMediaTypeTitle), LocaleController.getString("HelpMediaTypeDetail", C0338R.string.HelpMediaTypeDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("mediaDownloadTypeHelp").booleanValue() || view2 == null)) {
                MaterialHelperUtil.m1373b("mediaDownloadTypeHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view2, LocaleController.getString("HelpMediaDownloadTypeTitle", C0338R.string.HelpMediaDownloadTypeTitle), LocaleController.getString("HelpMediaDownloadTypeDetail", C0338R.string.HelpMediaDownloadTypeDetail), new GreenTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("mediaChatHelp").booleanValue() || view3 == null)) {
                MaterialHelperUtil.m1373b("mediaChatHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view3, LocaleController.getString("HelpMediaChatTitle", C0338R.string.HelpMediaChatTitle), LocaleController.getString("HelpMediaChatDetail", C0338R.string.HelpMediaChatDetail), new TelegramTheme()));
            }
            MaterialHelperUtil.m1368a(activity, arrayList);
        }
    }

    public static void m1363a(Activity activity, View view, View view2, View view3, View view4) {
        if (activity != null && !f1302a) {
            f1302a = true;
            List arrayList = new ArrayList();
            if (!(MaterialHelperUtil.m1359a("tabHelp").booleanValue() || view == null)) {
                MaterialHelperUtil.m1373b("tabHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view, LocaleController.getString("HelpTabTitle", C0338R.string.HelpTabTitle), LocaleController.getString("HelpTabDetail", C0338R.string.HelpTabDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("ghostIconHelp").booleanValue() || view2 == null)) {
                MaterialHelperUtil.m1373b("ghostIconHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view2, LocaleController.getString("HelpGhostTitle", C0338R.string.HelpGhostTitle), LocaleController.getString("HelpGhostDetail", C0338R.string.HelpGhostDetail), new GreenTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("dialogCellHelp").booleanValue() || view3 == null)) {
                MaterialHelperUtil.m1373b("dialogCellHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view3, LocaleController.getString("HelpDialogCellTitle", C0338R.string.HelpDialogCellTitle), LocaleController.getString("HelpDialogCellDetail", C0338R.string.HelpDialogCellDetail), new CyanTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("menuHelp").booleanValue() || view4 == null)) {
                MaterialHelperUtil.m1373b("menuHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view4, LocaleController.getString("HelpMenuTitle", C0338R.string.HelpMenuTitle), LocaleController.getString("HelpMenuDetail", C0338R.string.HelpMenuDetail), new BrownTheme()));
            }
            if (arrayList.size() > 0) {
                new com.hanista.mobogram.mobo.p010k.p011a.TapTargetSequence(activity).m1317a(arrayList).m1318a(true).m1316a(new MaterialHelperUtil()).m1319a();
            } else {
                f1302a = false;
            }
        }
    }

    public static void m1364a(Activity activity, View view, View view2, View view3, View view4, Rect rect, Rect rect2, Rect rect3) {
        if (activity != null && !f1303b) {
            f1303b = true;
            List arrayList = new ArrayList();
            if (!(MaterialHelperUtil.m1359a("chatMenuHelp").booleanValue() || view == null)) {
                MaterialHelperUtil.m1373b("chatMenuHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view, LocaleController.getString("HelpChatMenuTitle", C0338R.string.HelpChatMenuTitle), LocaleController.getString("HelpChatMenuDetail", C0338R.string.HelpChatMenuDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("markerHelp").booleanValue() || view2 == null)) {
                MaterialHelperUtil.m1373b("markerHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view2, LocaleController.getString("HelpMarkerTitle", C0338R.string.HelpMarkerTitle), LocaleController.getString("HelpMarkerDetail", C0338R.string.HelpMarkerDetail), new GreenTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("voiceChangerHelp").booleanValue() || view3 == null)) {
                MaterialHelperUtil.m1373b("voiceChangerHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view3, LocaleController.getString("HelpVoiceChangerTitle", C0338R.string.HelpVoiceChangerTitle), LocaleController.getString("HelpVoiceChangerDetail", C0338R.string.HelpVoiceChangerDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("recentChatBarHelp").booleanValue() || view4 == null)) {
                MaterialHelperUtil.m1373b("recentChatBarHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view4, LocaleController.getString("HelpRecentChatBarTitle", C0338R.string.HelpRecentChatBarTitle), LocaleController.getString("HelpRecentChatBarDetail", C0338R.string.HelpRecentChatBarDetail), new CyanTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("archiveBtnHelp").booleanValue() || rect == null)) {
                MaterialHelperUtil.m1373b("archiveBtnHelp");
                arrayList.add(MaterialHelperUtil.m1356a(rect, LocaleController.getString("HelpArchiveBtnTitle", C0338R.string.HelpArchiveBtnTitle), LocaleController.getString("HelpArchiveBtnDetail", C0338R.string.HelpArchiveBtnDetail), new BrownTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("replyBtnHelp").booleanValue() || rect2 == null)) {
                MaterialHelperUtil.m1373b("replyBtnHelp");
                arrayList.add(MaterialHelperUtil.m1356a(rect2, LocaleController.getString("HelpReplyBtnTitle", C0338R.string.HelpReplyBtnTitle), LocaleController.getString("HelpReplyBtnDetail", C0338R.string.HelpReplyBtnDetail), new TelegramTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("showUseMessagesHelp").booleanValue() || rect3 == null)) {
                MaterialHelperUtil.m1373b("showUseMessagesHelp");
                arrayList.add(MaterialHelperUtil.m1356a(rect3, LocaleController.getString("HelpShowThisUserMessagesTitle", C0338R.string.HelpShowThisUserMessagesTitle), LocaleController.getString("HelpShowThisUserMessagesDetail", C0338R.string.HelpShowThisUserMessagesDetail), new RedTheme()));
            }
            if (arrayList.size() > 0) {
                new com.hanista.mobogram.mobo.p010k.p011a.TapTargetSequence(activity).m1317a(arrayList).m1318a(true).m1316a(new MaterialHelperUtil()).m1319a();
            } else {
                f1303b = false;
            }
        }
    }

    public static void m1365a(Activity activity, View view, View view2, View view3, View view4, View view5) {
        if (activity != null && !f1305d) {
            f1305d = true;
            List arrayList = new ArrayList();
            if (!(MaterialHelperUtil.m1359a("contactsDeleteHelp").booleanValue() || view == null)) {
                MaterialHelperUtil.m1373b("contactsDeleteHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view, LocaleController.getString("HelpContactsDeleteTitle", C0338R.string.HelpContactsDeleteTitle), LocaleController.getString("HelpContactsDeleteDetail", C0338R.string.HelpContactsDeleteDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("contactsAddHelp").booleanValue() || view2 == null)) {
                MaterialHelperUtil.m1373b("contactsAddHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view2, LocaleController.getString("HelpContactsAddTitle", C0338R.string.HelpContactsAddTitle), LocaleController.getString("HelpContactsAddDetail", C0338R.string.HelpContactsAddDetail), new GreenTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("contactsAvatarHelp").booleanValue() || view3 == null)) {
                MaterialHelperUtil.m1373b("contactsAvatarHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view3, LocaleController.getString("HelpContactsAvatarTitle", C0338R.string.HelpContactsAvatarTitle), LocaleController.getString("HelpContactsAvatarDetail", C0338R.string.HelpContactsAvatarDetail), new PurpleTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("contactsMutualHelp").booleanValue() || view4 == null)) {
                MaterialHelperUtil.m1373b("contactsMutualHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view4, LocaleController.getString("HelpContactsMutualTitle", C0338R.string.HelpContactsMutualTitle), LocaleController.getString("HelpContactsMutualDetail", C0338R.string.HelpContactsMutualDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("contactsContextMenuHelp").booleanValue() || view5 == null)) {
                MaterialHelperUtil.m1373b("contactsContextMenuHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view5, LocaleController.getString("HelpContactsContextMenuTitle", C0338R.string.HelpContactsContextMenuTitle), LocaleController.getString("HelpContactsContextMenuDetail", C0338R.string.HelpContactsContextMenuDetail), new BlueGreyTheme()));
            }
            if (arrayList.size() > 0) {
                new com.hanista.mobogram.mobo.p010k.p011a.TapTargetSequence(activity).m1317a(arrayList).m1318a(true).m1316a(new MaterialHelperUtil()).m1319a();
            } else {
                f1305d = false;
            }
        }
    }

    public static void m1366a(Activity activity, ListView listView) {
        if (activity != null && !f1306e) {
            f1306e = true;
            DrawerActionCell drawerActionCell = null;
            DrawerActionCell drawerActionCell2 = null;
            DrawerActionCell drawerActionCell3 = null;
            DrawerActionCell drawerActionCell4 = null;
            DrawerActionCell drawerActionCell5 = null;
            DrawerActionCell drawerActionCell6 = null;
            DrawerActionCell drawerActionCell7 = null;
            DrawerActionCell drawerActionCell8 = null;
            DrawerActionCell drawerActionCell9 = null;
            DrawerActionCell drawerActionCell10 = null;
            DrawerActionCell drawerActionCell11 = null;
            DrawerActionCell drawerActionCell12 = null;
            DrawerActionCell drawerActionCell13 = null;
            DrawerActionCell drawerActionCell14 = null;
            DrawerActionCell drawerActionCell15 = null;
            int lastVisiblePosition = listView.getLastVisiblePosition();
            int i = 0;
            while (i < lastVisiblePosition) {
                DrawerActionCell drawerActionCell16;
                View childAt = listView.getChildAt(i);
                if (!(childAt == null || !(childAt instanceof DrawerActionCell) || childAt.getTag() == null)) {
                    DrawerActionCell drawerActionCell17;
                    switch (Integer.parseInt(childAt.getTag() + TtmlNode.ANONYMOUS_REGION_ID)) {
                        case VideoPlayer.STATE_PREPARING /*2*/:
                            drawerActionCell17 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell17;
                            continue;
                        case VideoPlayer.STATE_READY /*4*/:
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell17;
                            continue;
                        case Method.TRACE /*6*/:
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell17;
                            continue;
                        case Method.PATCH /*7*/:
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell17;
                            continue;
                        case TLRPC.USER_FLAG_USERNAME /*8*/:
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell17;
                            continue;
                        case C0338R.styleable.PromptView_iconTint /*9*/:
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell17;
                            continue;
                        case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell17;
                            continue;
                        case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell17;
                            continue;
                        case Atom.FULL_HEADER_SIZE /*12*/:
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell17;
                            continue;
                        case C0338R.styleable.PromptView_primaryTextFontFamily /*14*/:
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell17;
                            continue;
                        case TLRPC.USER_FLAG_PHONE /*16*/:
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell17;
                            continue;
                        case C0338R.styleable.PromptView_secondaryTextStyle /*22*/:
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell17;
                            continue;
                        case C0338R.styleable.PromptView_secondaryTextTypeface /*23*/:
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell17;
                            continue;
                        case C0338R.styleable.PromptView_target /*24*/:
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            drawerActionCell17 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = (DrawerActionCell) childAt;
                            drawerActionCell16 = drawerActionCell15;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell17;
                            continue;
                        case C0338R.styleable.PromptView_textSeparation /*26*/:
                            drawerActionCell16 = (DrawerActionCell) childAt;
                            drawerActionCell15 = drawerActionCell14;
                            drawerActionCell14 = drawerActionCell13;
                            drawerActionCell13 = drawerActionCell12;
                            drawerActionCell12 = drawerActionCell11;
                            drawerActionCell11 = drawerActionCell10;
                            drawerActionCell10 = drawerActionCell9;
                            drawerActionCell9 = drawerActionCell8;
                            drawerActionCell8 = drawerActionCell7;
                            drawerActionCell7 = drawerActionCell6;
                            drawerActionCell6 = drawerActionCell5;
                            drawerActionCell5 = drawerActionCell4;
                            drawerActionCell4 = drawerActionCell3;
                            drawerActionCell3 = drawerActionCell2;
                            drawerActionCell2 = drawerActionCell;
                            continue;
                    }
                }
                drawerActionCell16 = drawerActionCell15;
                drawerActionCell15 = drawerActionCell14;
                drawerActionCell14 = drawerActionCell13;
                drawerActionCell13 = drawerActionCell12;
                drawerActionCell12 = drawerActionCell11;
                drawerActionCell11 = drawerActionCell10;
                drawerActionCell10 = drawerActionCell9;
                drawerActionCell9 = drawerActionCell8;
                drawerActionCell8 = drawerActionCell7;
                drawerActionCell7 = drawerActionCell6;
                drawerActionCell6 = drawerActionCell5;
                drawerActionCell5 = drawerActionCell4;
                drawerActionCell4 = drawerActionCell3;
                drawerActionCell3 = drawerActionCell2;
                drawerActionCell2 = drawerActionCell;
                i++;
                drawerActionCell = drawerActionCell2;
                drawerActionCell2 = drawerActionCell3;
                drawerActionCell3 = drawerActionCell4;
                drawerActionCell4 = drawerActionCell5;
                drawerActionCell5 = drawerActionCell6;
                drawerActionCell6 = drawerActionCell7;
                drawerActionCell7 = drawerActionCell8;
                drawerActionCell8 = drawerActionCell9;
                drawerActionCell9 = drawerActionCell10;
                drawerActionCell10 = drawerActionCell11;
                drawerActionCell11 = drawerActionCell12;
                drawerActionCell12 = drawerActionCell13;
                drawerActionCell13 = drawerActionCell14;
                drawerActionCell14 = drawerActionCell15;
                drawerActionCell15 = drawerActionCell16;
            }
            listView.setOnScrollListener(new MaterialHelperUtil(activity, listView));
            List arrayList = new ArrayList();
            if (!(MaterialHelperUtil.m1359a("drawerNewAccountHelp").booleanValue() || drawerActionCell == null)) {
                MaterialHelperUtil.m1373b("drawerNewAccountHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell.textView, LocaleController.getString("HelpDrawerChangeUserTitle", C0338R.string.HelpDrawerChangeUserTitle), LocaleController.getString("HelpDrawerChangeUserDetail", C0338R.string.HelpDrawerChangeUserDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerNewChatHelp").booleanValue() || drawerActionCell2 == null)) {
                MaterialHelperUtil.m1373b("drawerNewChatHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell2.textView, LocaleController.getString("HelpDrawerNewChatTitle", C0338R.string.HelpDrawerNewChatTitle), LocaleController.getString("HelpDrawerNewChatDetail", C0338R.string.HelpDrawerNewChatDetail), new GreenTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerMoboplusHelp").booleanValue() || drawerActionCell3 == null)) {
                MaterialHelperUtil.m1373b("drawerMoboplusHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell3.textView, LocaleController.getString("HelpDrawerMoboplusTitle", C0338R.string.HelpDrawerMoboplusTitle), LocaleController.getString("HelpDrawerMoboplusDetail", C0338R.string.HelpDrawerMoboplusDetail), new BrownTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerArchiveHelp").booleanValue() || drawerActionCell4 == null)) {
                MaterialHelperUtil.m1373b("drawerArchiveHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell4.textView, LocaleController.getString("HelpDrawerArchiveTitle", C0338R.string.HelpDrawerArchiveTitle), LocaleController.getString("HelpDrawerArchiveDetail", C0338R.string.HelpDrawerArchiveDetail), new TelegramTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerDownloadManagerHelp").booleanValue() || drawerActionCell5 == null)) {
                MaterialHelperUtil.m1373b("drawerDownloadManagerHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell5.textView, LocaleController.getString("HelpDrawerDownloadManagerTitle", C0338R.string.HelpDrawerDownloadManagerTitle), LocaleController.getString("HelpDrawerDownloadManagerDetail", C0338R.string.HelpDrawerDownloadManagerDetail), new BlueGreyTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerDialogDmHelp").booleanValue() || drawerActionCell6 == null)) {
                MaterialHelperUtil.m1373b("drawerDialogDmHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell6.textView, LocaleController.getString("HelpDrawerDialogDmTitle", C0338R.string.HelpDrawerDialogDmTitle), LocaleController.getString("HelpDrawerDialogDmDetail", C0338R.string.HelpDrawerDialogDmDetail), new PurpleTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerFileManagerHelp").booleanValue() || drawerActionCell7 == null)) {
                MaterialHelperUtil.m1373b("drawerFileManagerHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell7.textView, LocaleController.getString("HelpDrawerFileManagerTitle", C0338R.string.HelpDrawerFileManagerTitle), LocaleController.getString("HelpDrawerFileManagerDetail", C0338R.string.HelpDrawerFileManagerDetail), new BrownTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerCategoryHelp").booleanValue() || drawerActionCell8 == null)) {
                MaterialHelperUtil.m1373b("drawerCategoryHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell8.textView, LocaleController.getString("HelpDrawerCategoryTitle", C0338R.string.HelpDrawerCategoryTitle), LocaleController.getString("HelpDrawerCategoryDetail", C0338R.string.HelpDrawerCategoryDetail), new GreenTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerSpContactsHelp").booleanValue() || drawerActionCell9 == null)) {
                MaterialHelperUtil.m1373b("drawerSpContactsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell9.textView, LocaleController.getString("HelpDrawerSpecificContactTitle", C0338R.string.HelpDrawerSpecificContactTitle), LocaleController.getString("HelpDrawerSpecificContactDetail", C0338R.string.HelpDrawerSpecificContactDetail), new PurpleTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerOnlineContactsHelp").booleanValue() || drawerActionCell10 == null)) {
                MaterialHelperUtil.m1373b("drawerOnlineContactsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell10.textView, LocaleController.getString("HelpDrawerOnlineContactsTitle", C0338R.string.HelpDrawerOnlineContactsTitle), LocaleController.getString("HelpDrawerOnlineContactsDetail", C0338R.string.HelpDrawerOnlineContactsDetail), new PinkTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerContactChangesHelp").booleanValue() || drawerActionCell11 == null)) {
                MaterialHelperUtil.m1373b("drawerContactChangesHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell11.textView, LocaleController.getString("HelpDrawerContactChangesTitle", C0338R.string.HelpDrawerContactChangesTitle), LocaleController.getString("HelpDrawerContactChangesDetail", C0338R.string.HelpDrawerContactChangesDetail), new CyanTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerMoboSettingsHelp").booleanValue() || drawerActionCell12 == null)) {
                MaterialHelperUtil.m1373b("drawerMoboSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell12.textView, LocaleController.getString("HelpDrawerMoboSettingsTitle", C0338R.string.HelpDrawerMoboSettingsTitle), LocaleController.getString("HelpDrawerMoboSettingsDetail", C0338R.string.HelpDrawerMoboSettingsDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerUsernameFinderHelp").booleanValue() || drawerActionCell13 == null)) {
                MaterialHelperUtil.m1373b("drawerUsernameFinderHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell13.textView, LocaleController.getString("HelpDrawerUsernameFinderTitle", C0338R.string.HelpDrawerUsernameFinderTitle), LocaleController.getString("HelpDrawerUsernameFinderDetail", C0338R.string.HelpDrawerUsernameFinderDetail), new GreenTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerThemeHelp").booleanValue() || drawerActionCell14 == null)) {
                MaterialHelperUtil.m1373b("drawerThemeHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell14.textView, LocaleController.getString("HelpDrawerThemeTitle", C0338R.string.HelpDrawerThemeTitle), LocaleController.getString("HelpDrawerThemeDetail", C0338R.string.HelpDrawerThemeDetail), new BlueGreyTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("drawerTurnOffHelp").booleanValue() || drawerActionCell15 == null)) {
                MaterialHelperUtil.m1373b("drawerTurnOffHelp");
                arrayList.add(MaterialHelperUtil.m1357a(drawerActionCell15.textView, LocaleController.getString("HelpDrawerTurnOffTitle", C0338R.string.HelpDrawerTurnOffTitle), LocaleController.getString("HelpDrawerTurnOffDetail", C0338R.string.HelpDrawerTurnOffDetail), new BlackTheme()));
            }
            if (arrayList.size() > 0) {
                new com.hanista.mobogram.mobo.p010k.p011a.TapTargetSequence(activity).m1317a(arrayList).m1318a(true).m1316a(new MaterialHelperUtil()).m1319a();
            } else {
                f1306e = false;
            }
        }
    }

    public static void m1367a(Activity activity, ListView listView, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16) {
        if (activity != null && !f1307f) {
            f1307f = true;
            View view = null;
            View view2 = null;
            View view3 = null;
            View view4 = null;
            View view5 = null;
            View view6 = null;
            View view7 = null;
            View view8 = null;
            View view9 = null;
            View view10 = null;
            View view11 = null;
            View view12 = null;
            View view13 = null;
            View view14 = null;
            View view15 = null;
            int lastVisiblePosition = listView.getLastVisiblePosition();
            int i17 = 0;
            View view16 = null;
            while (i17 < lastVisiblePosition) {
                View childAt = listView.getChildAt(i17);
                if (!(childAt == null || childAt.getTag() == null)) {
                    int parseInt = Integer.parseInt(childAt.getTag() + TtmlNode.ANONYMOUS_REGION_ID);
                    View view17;
                    if (parseInt == i) {
                        view17 = view15;
                        view15 = view14;
                        view14 = view13;
                        view13 = view12;
                        view12 = view11;
                        view11 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = childAt;
                        childAt = view17;
                    } else if (parseInt == i2) {
                        view2 = view;
                        view17 = view14;
                        view14 = view13;
                        view13 = view12;
                        view12 = view11;
                        view11 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = childAt;
                        childAt = view15;
                        view15 = view17;
                    } else if (parseInt == i3) {
                        view3 = view2;
                        view2 = view;
                        view17 = view13;
                        view13 = view12;
                        view12 = view11;
                        view11 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = childAt;
                        childAt = view15;
                        view15 = view14;
                        view14 = view17;
                    } else if (parseInt == i4) {
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                        view17 = view12;
                        view12 = view11;
                        view11 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = childAt;
                        childAt = view15;
                        view15 = view14;
                        view14 = view13;
                        view13 = view17;
                    } else if (parseInt == i5) {
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                        view17 = view11;
                        view11 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = childAt;
                        childAt = view15;
                        view15 = view14;
                        view14 = view13;
                        view13 = view12;
                        view12 = view17;
                    } else if (parseInt == i6) {
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                        view17 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = childAt;
                        childAt = view15;
                        view15 = view14;
                        view14 = view13;
                        view13 = view12;
                        view12 = view11;
                        view11 = view17;
                    } else if (parseInt == i7) {
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                        view17 = view9;
                        view9 = view8;
                        view8 = childAt;
                        childAt = view15;
                        view15 = view14;
                        view14 = view13;
                        view13 = view12;
                        view12 = view11;
                        view11 = view10;
                        view10 = view17;
                    } else if (parseInt == i8) {
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                        view17 = childAt;
                        childAt = view15;
                        view15 = view14;
                        view14 = view13;
                        view13 = view12;
                        view12 = view11;
                        view11 = view10;
                        view10 = view9;
                        view9 = view17;
                    } else if (parseInt == i9) {
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                        view17 = view14;
                        view14 = view13;
                        view13 = view12;
                        view12 = view11;
                        view11 = view10;
                        view10 = childAt;
                        childAt = view15;
                        view15 = view17;
                    } else if (parseInt == i10) {
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                        view17 = view12;
                        view12 = view11;
                        view11 = childAt;
                        childAt = view15;
                        view15 = view14;
                        view14 = view13;
                        view13 = view17;
                    } else if (parseInt == i11) {
                        view11 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                        view17 = view15;
                        view15 = view14;
                        view14 = view13;
                        view13 = view12;
                        view12 = childAt;
                        childAt = view17;
                    } else if (parseInt == i12) {
                        view12 = view11;
                        view11 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                        view17 = childAt;
                        childAt = view15;
                        view15 = view14;
                        view14 = view13;
                        view13 = view17;
                    } else if (parseInt == i13) {
                        view13 = view12;
                        view12 = view11;
                        view11 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                        view17 = view15;
                        view15 = view14;
                        view14 = childAt;
                        childAt = view17;
                    } else if (parseInt == i14) {
                        view14 = view13;
                        view13 = view12;
                        view12 = view11;
                        view11 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                        view17 = childAt;
                        childAt = view15;
                        view15 = view17;
                    } else if (parseInt == i15) {
                        view15 = view14;
                        view14 = view13;
                        view13 = view12;
                        view12 = view11;
                        view11 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                    } else if (parseInt == i16) {
                        view16 = childAt;
                        childAt = view15;
                        view15 = view14;
                        view14 = view13;
                        view13 = view12;
                        view12 = view11;
                        view11 = view10;
                        view10 = view9;
                        view9 = view8;
                        view8 = view7;
                        view7 = view6;
                        view6 = view5;
                        view5 = view4;
                        view4 = view3;
                        view3 = view2;
                        view2 = view;
                    }
                    i17++;
                    view = view2;
                    view2 = view3;
                    view3 = view4;
                    view4 = view5;
                    view5 = view6;
                    view6 = view7;
                    view7 = view8;
                    view8 = view9;
                    view9 = view10;
                    view10 = view11;
                    view11 = view12;
                    view12 = view13;
                    view13 = view14;
                    view14 = view15;
                    view15 = childAt;
                }
                childAt = view15;
                view15 = view14;
                view14 = view13;
                view13 = view12;
                view12 = view11;
                view11 = view10;
                view10 = view9;
                view9 = view8;
                view8 = view7;
                view7 = view6;
                view6 = view5;
                view5 = view4;
                view4 = view3;
                view3 = view2;
                view2 = view;
                i17++;
                view = view2;
                view2 = view3;
                view3 = view4;
                view4 = view5;
                view5 = view6;
                view6 = view7;
                view7 = view8;
                view8 = view9;
                view9 = view10;
                view10 = view11;
                view11 = view12;
                view12 = view13;
                view13 = view14;
                view14 = view15;
                view15 = childAt;
            }
            List arrayList = new ArrayList();
            if (!(MaterialHelperUtil.m1359a("tabSettingsHelp").booleanValue() || view == null)) {
                MaterialHelperUtil.m1373b("tabSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view, LocaleController.getString("HelpTabSettingsTitle", C0338R.string.HelpTabSettingsTitle), LocaleController.getString("HelpTabSettingsDetail", C0338R.string.HelpTabSettingsDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("viewSettingsHelp").booleanValue() || view2 == null)) {
                MaterialHelperUtil.m1373b("viewSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view2, LocaleController.getString("HelpViewSettingsTitle", C0338R.string.HelpViewSettingsTitle), LocaleController.getString("HelpViewSettingsDetail", C0338R.string.HelpViewSettingsDetail), new GreenTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("chatSettingsHelp").booleanValue() || view3 == null)) {
                MaterialHelperUtil.m1373b("chatSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view3, LocaleController.getString("HelpChatSettingsTitle", C0338R.string.HelpChatSettingsTitle), LocaleController.getString("HelpChatSettingsDetail", C0338R.string.HelpChatSettingsDetail), new BrownTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("emojiSettingsHelp").booleanValue() || view4 == null)) {
                MaterialHelperUtil.m1373b("emojiSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view4, LocaleController.getString("HelpEmojiSettingsTitle", C0338R.string.HelpEmojiSettingsTitle), LocaleController.getString("HelpEmojiSettingsDetail", C0338R.string.HelpEmojiSettingsDetail), new TelegramTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("chatBarSettingsHelp").booleanValue() || view5 == null)) {
                MaterialHelperUtil.m1373b("chatBarSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view5, LocaleController.getString("HelpChatBarSettingsTitle", C0338R.string.HelpChatBarSettingsTitle), LocaleController.getString("HelpChatBarSettingsDetail", C0338R.string.HelpChatBarSettingsDetail), new BlueGreyTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("forwardSettingsHelp").booleanValue() || view6 == null)) {
                MaterialHelperUtil.m1373b("forwardSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view6, LocaleController.getString("HelpForwardSettingsTitle", C0338R.string.HelpForwardSettingsTitle), LocaleController.getString("HelpForwardSettingsDetail", C0338R.string.HelpForwardSettingsDetail), new PurpleTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("categorySettingsHelp").booleanValue() || view7 == null)) {
                MaterialHelperUtil.m1373b("categorySettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view7, LocaleController.getString("HelpCategorySettingsTitle", C0338R.string.HelpCategorySettingsTitle), LocaleController.getString("HelpCategorySettingsDetail", C0338R.string.HelpCategorySettingsDetail), new BrownTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("archiveSettingsHelp").booleanValue() || view8 == null)) {
                MaterialHelperUtil.m1373b("archiveSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view8, LocaleController.getString("HelpArchiveSettingsTitle", C0338R.string.HelpArchiveSettingsTitle), LocaleController.getString("HelpArchiveSettingsDetail", C0338R.string.HelpArchiveSettingsDetail), new GreenTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("notifcaitonBarSettingsHelp").booleanValue() || view9 == null)) {
                MaterialHelperUtil.m1373b("notifcaitonBarSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view9, LocaleController.getString("HelpNotificationBarSettingsTitle", C0338R.string.HelpNotificationBarSettingsTitle), LocaleController.getString("HelpNotificationBarSettingsDetail", C0338R.string.HelpNotificationBarSettingsDetail), new PurpleTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("menuSettingsHelp").booleanValue() || view10 == null)) {
                MaterialHelperUtil.m1373b("menuSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view10, LocaleController.getString("HelpMenuSettingsTitle", C0338R.string.HelpMenuSettingsTitle), LocaleController.getString("HelpMenuSettingsDetail", C0338R.string.HelpMenuSettingsDetail), new PinkTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("hideChatSettingsHelp").booleanValue() || view11 == null)) {
                MaterialHelperUtil.m1373b("hideChatSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view11, LocaleController.getString("HelpHideChatSettingsTitle", C0338R.string.HelpHideChatSettingsTitle), LocaleController.getString("HelpHideChatSettingsDetail", C0338R.string.HelpHideChatSettingsDetail), new CyanTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("ghostSettingsHelp").booleanValue() || view12 == null)) {
                MaterialHelperUtil.m1373b("ghostSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view12, LocaleController.getString("HelpGhostSettingsTitle", C0338R.string.HelpGhostSettingsTitle), LocaleController.getString("HelpGhostSettingsDetail", C0338R.string.HelpGhostSettingsDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("lockSettingsHelp").booleanValue() || view13 == null)) {
                MaterialHelperUtil.m1373b("lockSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view13, LocaleController.getString("HelpLockSettingsTitle", C0338R.string.HelpLockSettingsTitle), LocaleController.getString("HelpLockSettingsDetail", C0338R.string.HelpLockSettingsDetail), new GreenTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("storageSettingsHelp").booleanValue() || view14 == null)) {
                MaterialHelperUtil.m1373b("storageSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view14, LocaleController.getString("HelpStorageSettingsTitle", C0338R.string.HelpStorageSettingsTitle), LocaleController.getString("HelpStorageSettingsDetail", C0338R.string.HelpStorageSettingsDetail), new BlueGreyTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("storageFolderSettingsHelp").booleanValue() || view15 == null)) {
                MaterialHelperUtil.m1373b("storageFolderSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view15, LocaleController.getString("HelpStorageFolderSettingsTitle", C0338R.string.HelpStorageFolderSettingsTitle), LocaleController.getString("HelpStorageFolderSettingsDetail", C0338R.string.HelpStorageFolderSettingsDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("clearCacheSettingsHelp").booleanValue() || view16 == null)) {
                MaterialHelperUtil.m1373b("clearCacheSettingsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view16, LocaleController.getString("HelpClearCacheTitle", C0338R.string.HelpClearCacheTitle), LocaleController.getString("HelpClearCacheDetail", C0338R.string.HelpClearCacheDetail), new PurpleTheme()));
            }
            if (arrayList.size() > 0) {
                new com.hanista.mobogram.mobo.p010k.p011a.TapTargetSequence(activity).m1317a(arrayList).m1318a(true).m1316a(new MaterialHelperUtil()).m1319a();
            } else {
                f1307f = false;
            }
        }
    }

    private static void m1368a(Activity activity, List<TapTarget> list) {
        if (list != null && list.size() > 0) {
            new com.hanista.mobogram.mobo.p010k.p011a.TapTargetSequence(activity).m1317a((List) list).m1318a(true).m1316a(new MaterialHelperUtil()).m1319a();
        }
    }

    public static void m1369a(Dialog dialog, View view, View view2, View view3, View view4, View view5) {
        if (dialog != null) {
            List arrayList = new ArrayList();
            if (!(MaterialHelperUtil.m1359a("shareSelectAllHelp").booleanValue() || view2 == null)) {
                MaterialHelperUtil.m1373b("shareSelectAllHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view2, LocaleController.getString("HelpShareAlertSelectAllTitle", C0338R.string.HelpShareAlertSelectAllTitle), LocaleController.getString("HelpShareAlertSelectAllDetail", C0338R.string.HelpShareAlertSelectAllDetail), new GreenTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("shareQuoteHelp").booleanValue() || view == null)) {
                MaterialHelperUtil.m1373b("shareQuoteHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view, LocaleController.getString("HelpShareAlertQuoteTitle", C0338R.string.HelpShareAlertQuoteTitle), LocaleController.getString("HelpShareAlertQuoteDetail", C0338R.string.HelpShareAlertQuoteDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("shareCaptionHelp").booleanValue() || view3 == null)) {
                MaterialHelperUtil.m1373b("shareCaptionHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view3, LocaleController.getString("HelpShareAlertCaptionTitle", C0338R.string.HelpShareAlertCaptionTitle), LocaleController.getString("HelpShareAlertCaptionDetail", C0338R.string.HelpShareAlertCaptionDetail), new CyanTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("shareContactsHelp").booleanValue() || view4 == null)) {
                MaterialHelperUtil.m1373b("shareContactsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view4, LocaleController.getString("HelpShareAlertContactsTitle", C0338R.string.HelpShareAlertContactsTitle), LocaleController.getString("HelpShareAlertContactsDetail", C0338R.string.HelpShareAlertContactsDetail), new PurpleTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("shareTabsHelp").booleanValue() || view5 == null)) {
                MaterialHelperUtil.m1373b("shareTabsHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view5, LocaleController.getString("HelpShareAlertTabsTitle", C0338R.string.HelpShareAlertTabsTitle), LocaleController.getString("HelpShareAlertTabsDetail", C0338R.string.HelpShareAlertTabsDetail), new TelegramTheme()));
            }
            MaterialHelperUtil.m1370a(dialog, arrayList);
        }
    }

    private static void m1370a(Dialog dialog, List<TapTarget> list) {
        if (list != null && list.size() > 0) {
            new com.hanista.mobogram.mobo.p010k.p011a.TapTargetSequence(dialog).m1317a((List) list).m1318a(true).m1316a(new MaterialHelperUtil()).m1319a();
        }
    }

    public static void m1372b(Activity activity, View view, View view2) {
        if (activity != null) {
            List arrayList = new ArrayList();
            if (!(MaterialHelperUtil.m1359a("dmMenuHelp").booleanValue() || view == null)) {
                MaterialHelperUtil.m1373b("dmMenuHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view, LocaleController.getString("HelpDlManagerMenuTitle", C0338R.string.HelpDlManagerMenuTitle), LocaleController.getString("HelpDlManagerMenuDetail", C0338R.string.HelpDlManagerMenuDetail), new IndigoTheme()));
            }
            if (!(MaterialHelperUtil.m1359a("dmStartHelp").booleanValue() || view2 == null)) {
                MaterialHelperUtil.m1373b("dmStartHelp");
                arrayList.add(MaterialHelperUtil.m1357a(view2, LocaleController.getString("HelpDlManagerServiceTitle", C0338R.string.HelpDlManagerServiceTitle), LocaleController.getString("HelpDlManagerServiceDetail", C0338R.string.HelpDlManagerServiceDetail), new TelegramTheme()));
            }
            MaterialHelperUtil.m1368a(activity, arrayList);
        }
    }

    private static void m1373b(String str) {
        MaterialHelperUtil.m1355a().edit().putBoolean(str, true).commit();
    }
}
