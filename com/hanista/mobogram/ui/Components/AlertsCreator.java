package com.hanista.mobogram.ui.Components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationsController;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.ReportHelpActivity;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_reportPeer;
import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputReportReasonPornography;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputReportReasonSpam;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputReportReasonViolence;
import com.hanista.mobogram.tgnet.TLRPC.TL_peerNotifySettings;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.Builder;
import com.hanista.mobogram.ui.ReportOtherActivity;

public class AlertsCreator {

    /* renamed from: com.hanista.mobogram.ui.Components.AlertsCreator.1 */
    static class C12871 implements OnClickListener {
        final /* synthetic */ long val$dialog_id;

        C12871(long j) {
            this.val$dialog_id = j;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            long j = 1;
            int currentTime = ConnectionsManager.getInstance().getCurrentTime();
            int i2 = i == 0 ? currentTime + 3600 : i == 1 ? currentTime + 28800 : i == 2 ? currentTime + 172800 : i == 3 ? ConnectionsManager.DEFAULT_DATACENTER_ID : currentTime;
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            if (i == 3) {
                edit.putInt("notify2_" + this.val$dialog_id, 2);
            } else {
                edit.putInt("notify2_" + this.val$dialog_id, 3);
                edit.putInt("notifyuntil_" + this.val$dialog_id, i2);
                j = 1 | (((long) i2) << 32);
            }
            NotificationsController.getInstance().removeNotificationsForDialog(this.val$dialog_id);
            MessagesStorage.getInstance().setDialogFlags(this.val$dialog_id, j);
            edit.commit();
            TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.val$dialog_id));
            if (tL_dialog != null) {
                tL_dialog.notify_settings = new TL_peerNotifySettings();
                tL_dialog.notify_settings.mute_until = i2;
            }
            NotificationsController.updateServerNotificationsSettings(this.val$dialog_id);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.AlertsCreator.2 */
    static class C12892 implements OnClickListener {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ BaseFragment val$parentFragment;

        /* renamed from: com.hanista.mobogram.ui.Components.AlertsCreator.2.1 */
        class C12881 implements RequestDelegate {
            C12881() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
            }
        }

        C12892(long j, BaseFragment baseFragment) {
            this.val$dialog_id = j;
            this.val$parentFragment = baseFragment;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 3) {
                Bundle bundle = new Bundle();
                bundle.putLong("dialog_id", this.val$dialog_id);
                this.val$parentFragment.presentFragment(new ReportOtherActivity(bundle));
                return;
            }
            TLObject tL_account_reportPeer = new TL_account_reportPeer();
            tL_account_reportPeer.peer = MessagesController.getInputPeer((int) this.val$dialog_id);
            if (i == 0) {
                tL_account_reportPeer.reason = new TL_inputReportReasonSpam();
            } else if (i == 1) {
                tL_account_reportPeer.reason = new TL_inputReportReasonViolence();
            } else if (i == 2) {
                tL_account_reportPeer.reason = new TL_inputReportReasonPornography();
            }
            ConnectionsManager.getInstance().sendRequest(tL_account_reportPeer, new C12881());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.AlertsCreator.3 */
    static class C12903 implements OnClickListener {
        final /* synthetic */ BaseFragment val$fragment;

        C12903(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            try {
                this.val$fragment.presentFragment(new ReportHelpActivity());
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public static Dialog createMuteAlert(Context context, long j) {
        if (context == null) {
            return null;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("Notifications", C0338R.string.Notifications));
        CharSequence[] charSequenceArr = new CharSequence[4];
        charSequenceArr[0] = LocaleController.formatString("MuteFor", C0338R.string.MuteFor, LocaleController.formatPluralString("Hours", 1));
        charSequenceArr[1] = LocaleController.formatString("MuteFor", C0338R.string.MuteFor, LocaleController.formatPluralString("Hours", 8));
        charSequenceArr[2] = LocaleController.formatString("MuteFor", C0338R.string.MuteFor, LocaleController.formatPluralString("Days", 2));
        charSequenceArr[3] = LocaleController.getString("MuteDisable", C0338R.string.MuteDisable);
        builder.setItems(charSequenceArr, new C12871(j));
        return builder.create();
    }

    public static Dialog createReportAlert(Context context, long j, BaseFragment baseFragment) {
        if (context == null || baseFragment == null) {
            return null;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("ReportChat", C0338R.string.ReportChat));
        builder.setItems(new CharSequence[]{LocaleController.getString("ReportChatSpam", C0338R.string.ReportChatSpam), LocaleController.getString("ReportChatViolence", C0338R.string.ReportChatViolence), LocaleController.getString("ReportChatPornography", C0338R.string.ReportChatPornography), LocaleController.getString("ReportChatOther", C0338R.string.ReportChatOther)}, new C12892(j, baseFragment));
        return builder.create();
    }

    public static void showAddUserAlert(String str, BaseFragment baseFragment, boolean z) {
        if (str != null && baseFragment != null && baseFragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            boolean z2 = true;
            switch (str.hashCode()) {
                case -1763467626:
                    if (str.equals("USERS_TOO_FEW")) {
                        z2 = true;
                        break;
                    }
                    break;
                case -538116776:
                    if (str.equals("USER_BLOCKED")) {
                        z2 = true;
                        break;
                    }
                    break;
                case -512775857:
                    if (str.equals("USER_RESTRICTED")) {
                        z2 = true;
                        break;
                    }
                    break;
                case -454039871:
                    if (str.equals("PEER_FLOOD")) {
                        z2 = false;
                        break;
                    }
                    break;
                case -420079733:
                    if (str.equals("BOTS_TOO_MUCH")) {
                        z2 = true;
                        break;
                    }
                    break;
                case 517420851:
                    if (str.equals("USER_BOT")) {
                        z2 = true;
                        break;
                    }
                    break;
                case 1167301807:
                    if (str.equals("USERS_TOO_MUCH")) {
                        z2 = true;
                        break;
                    }
                    break;
                case 1227003815:
                    if (str.equals("USER_ID_INVALID")) {
                        z2 = true;
                        break;
                    }
                    break;
                case 1253103379:
                    if (str.equals("ADMINS_TOO_MUCH")) {
                        z2 = true;
                        break;
                    }
                    break;
                case 1623167701:
                    if (str.equals("USER_NOT_MUTUAL_CONTACT")) {
                        z2 = true;
                        break;
                    }
                    break;
                case 1916725894:
                    if (str.equals("USER_PRIVACY_RESTRICTED")) {
                        z2 = true;
                        break;
                    }
                    break;
            }
            switch (z2) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    builder.setMessage(LocaleController.getString("NobodyLikesSpam2", C0338R.string.NobodyLikesSpam2));
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", C0338R.string.MoreInfo), new C12903(baseFragment));
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                case VideoPlayer.STATE_PREPARING /*2*/:
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdd", C0338R.string.GroupUserCantAdd));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdd", C0338R.string.ChannelUserCantAdd));
                        break;
                    }
                case VideoPlayer.STATE_READY /*4*/:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserAddLimit", C0338R.string.GroupUserAddLimit));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserAddLimit", C0338R.string.ChannelUserAddLimit));
                        break;
                    }
                case VideoPlayer.STATE_ENDED /*5*/:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserLeftError", C0338R.string.GroupUserLeftError));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserLeftError", C0338R.string.ChannelUserLeftError));
                        break;
                    }
                case Method.TRACE /*6*/:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdmin", C0338R.string.GroupUserCantAdmin));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdmin", C0338R.string.ChannelUserCantAdmin));
                        break;
                    }
                case Method.PATCH /*7*/:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserCantBot", C0338R.string.GroupUserCantBot));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantBot", C0338R.string.ChannelUserCantBot));
                        break;
                    }
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("InviteToGroupError", C0338R.string.InviteToGroupError));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("InviteToChannelError", C0338R.string.InviteToChannelError));
                        break;
                    }
                case C0338R.styleable.PromptView_iconTint /*9*/:
                    builder.setMessage(LocaleController.getString("CreateGroupError", C0338R.string.CreateGroupError));
                    break;
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    builder.setMessage(LocaleController.getString("UserRestricted", C0338R.string.UserRestricted));
                    break;
                default:
                    builder.setMessage(str);
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            baseFragment.showDialog(builder.create(), true);
        }
    }

    public static void showFloodWaitAlert(String str, BaseFragment baseFragment) {
        if (str != null && str.startsWith("FLOOD_WAIT") && baseFragment != null && baseFragment.getParentActivity() != null) {
            int intValue = Utilities.parseInt(str).intValue();
            String formatPluralString = intValue < 60 ? LocaleController.formatPluralString("Seconds", intValue) : LocaleController.formatPluralString("Minutes", intValue / 60);
            AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setMessage(LocaleController.formatString("FloodWaitTime", C0338R.string.FloodWaitTime, formatPluralString));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            baseFragment.showDialog(builder.create(), true);
        }
    }
}
