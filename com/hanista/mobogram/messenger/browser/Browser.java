package com.hanista.mobogram.messenger.browser;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.ShareBroadcastReceiver;
import com.hanista.mobogram.messenger.support.customtabs.CustomTabsCallback;
import com.hanista.mobogram.messenger.support.customtabs.CustomTabsClient;
import com.hanista.mobogram.messenger.support.customtabs.CustomTabsIntent.Builder;
import com.hanista.mobogram.messenger.support.customtabs.CustomTabsServiceConnection;
import com.hanista.mobogram.messenger.support.customtabs.CustomTabsSession;
import com.hanista.mobogram.messenger.support.customtabsclient.shared.CustomTabsHelper;
import com.hanista.mobogram.messenger.support.customtabsclient.shared.ServiceConnection;
import com.hanista.mobogram.messenger.support.customtabsclient.shared.ServiceConnectionCallback;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.LaunchActivity;
import java.lang.ref.WeakReference;

public class Browser {
    private static WeakReference<Activity> currentCustomTabsActivity;
    private static CustomTabsClient customTabsClient;
    private static WeakReference<CustomTabsSession> customTabsCurrentSession;
    private static String customTabsPackageToBind;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static CustomTabsSession customTabsSession;

    /* renamed from: com.hanista.mobogram.messenger.browser.Browser.1 */
    static class C06881 implements ServiceConnectionCallback {
        C06881() {
        }

        public void onServiceConnected(CustomTabsClient customTabsClient) {
            Browser.customTabsClient = customTabsClient;
            if (MediaController.m71a().m142D() && Browser.customTabsClient != null) {
                try {
                    Browser.customTabsClient.warmup(0);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        public void onServiceDisconnected() {
            Browser.customTabsClient = null;
        }
    }

    private static class NavigationCallback extends CustomTabsCallback {
        private NavigationCallback() {
        }

        public void onNavigationEvent(int i, Bundle bundle) {
            FileLog.m16e("tmessages", "code = " + i + " extras " + bundle);
        }
    }

    public static void bindCustomTabsService(Activity activity) {
        Activity activity2 = null;
        if (VERSION.SDK_INT >= 15) {
            if (currentCustomTabsActivity != null) {
                activity2 = (Activity) currentCustomTabsActivity.get();
            }
            if (!(activity2 == null || activity2 == activity)) {
                unbindCustomTabsService(activity2);
            }
            if (customTabsClient == null) {
                currentCustomTabsActivity = new WeakReference(activity);
                try {
                    if (TextUtils.isEmpty(customTabsPackageToBind)) {
                        customTabsPackageToBind = CustomTabsHelper.getPackageNameToUse(activity);
                        if (customTabsPackageToBind == null) {
                            return;
                        }
                    }
                    customTabsServiceConnection = new ServiceConnection(new C06881());
                    if (!CustomTabsClient.bindCustomTabsService(activity, customTabsPackageToBind, customTabsServiceConnection)) {
                        customTabsServiceConnection = null;
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }
    }

    private static CustomTabsSession getCurrentSession() {
        return customTabsCurrentSession == null ? null : (CustomTabsSession) customTabsCurrentSession.get();
    }

    private static CustomTabsSession getSession() {
        if (customTabsClient == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            customTabsSession = customTabsClient.newSession(new NavigationCallback());
            setCurrentSession(customTabsSession);
        }
        return customTabsSession;
    }

    public static boolean isInternalUri(Uri uri) {
        String host = uri.getHost();
        Object toLowerCase = host != null ? host.toLowerCase() : TtmlNode.ANONYMOUS_REGION_ID;
        return "tg".equals(uri.getScheme()) || "telegram.me".equals(toLowerCase) || "telegram.dog".equals(toLowerCase);
    }

    public static boolean isInternalUrl(String str) {
        return isInternalUri(Uri.parse(str));
    }

    public static void openUrl(Context context, Uri uri) {
        openUrl(context, uri, true);
    }

    public static void openUrl(Context context, Uri uri, boolean z) {
        if (context != null && uri != null) {
            try {
                String toLowerCase = uri.getScheme() != null ? uri.getScheme().toLowerCase() : TtmlNode.ANONYMOUS_REGION_ID;
                boolean isInternalUri = isInternalUri(uri);
                if (VERSION.SDK_INT < 15 || !z || !MediaController.m71a().m142D() || isInternalUri || toLowerCase.equals("tel")) {
                    Intent intent = new Intent("android.intent.action.VIEW", uri);
                    if (isInternalUri) {
                        intent.setComponent(new ComponentName(context.getPackageName(), LaunchActivity.class.getName()));
                    }
                    intent.putExtra("com.android.browser.application_id", context.getPackageName());
                    context.startActivity(intent);
                    return;
                }
                Intent intent2 = new Intent(ApplicationLoader.applicationContext, ShareBroadcastReceiver.class);
                intent2.setAction("android.intent.action.SEND");
                Builder builder = new Builder(getSession());
                if (ThemeUtil.m2490b()) {
                    int i = AdvanceTheme.bg;
                    if (i == -1) {
                        i = AdvanceTheme.m2283b(i, 32);
                    }
                    builder.setToolbarColor(i);
                } else {
                    builder.setToolbarColor(Theme.ACTION_BAR_COLOR);
                }
                builder.setShowTitle(true);
                builder.setActionButton(BitmapFactory.decodeResource(context.getResources(), C0338R.drawable.abc_ic_menu_share_mtrl_alpha), LocaleController.getString("ShareFile", C0338R.string.ShareFile), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, intent2, 0), false);
                builder.build().launchUrl((Activity) context, uri);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public static void openUrl(Context context, String str) {
        if (str != null) {
            openUrl(context, Uri.parse(str), true);
        }
    }

    public static void openUrl(Context context, String str, boolean z) {
        if (context != null && str != null) {
            openUrl(context, Uri.parse(str), z);
        }
    }

    private static void setCurrentSession(CustomTabsSession customTabsSession) {
        customTabsCurrentSession = new WeakReference(customTabsSession);
    }

    public static void unbindCustomTabsService(Activity activity) {
        if (VERSION.SDK_INT >= 15 && customTabsServiceConnection != null) {
            if ((currentCustomTabsActivity == null ? null : (Activity) currentCustomTabsActivity.get()) == activity) {
                currentCustomTabsActivity.clear();
            }
            try {
                activity.unbindService(customTabsServiceConnection);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            customTabsClient = null;
            customTabsSession = null;
        }
    }
}
