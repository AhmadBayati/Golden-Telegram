package com.hanista.mobogram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.SerializedData;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Components.ContextProgressView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.ShareAlert;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.net.URLEncoder;

public class WebviewActivity extends BaseFragment {
    private static final int open_in = 2;
    private static final int share = 1;
    private String currentBot;
    private String currentGame;
    private MessageObject currentMessageObject;
    private String currentUrl;
    private String linkToCopy;
    private ActionBarMenuItem progressItem;
    private ContextProgressView progressView;
    private String short_param;
    private WebView webView;

    /* renamed from: com.hanista.mobogram.ui.WebviewActivity.1 */
    class C19681 extends ActionBarMenuOnItemClick {
        C19681() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                WebviewActivity.this.finishFragment();
            } else if (i == WebviewActivity.share) {
                WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                WebviewActivity.this.showDialog(new ShareAlert(WebviewActivity.this.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy));
            } else if (i == WebviewActivity.open_in) {
                WebviewActivity.openGameInBrowser(WebviewActivity.this.currentUrl, WebviewActivity.this.currentMessageObject, WebviewActivity.this.getParentActivity(), WebviewActivity.this.short_param, WebviewActivity.this.currentBot);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.WebviewActivity.2 */
    class C19702 extends WebViewClient {

        /* renamed from: com.hanista.mobogram.ui.WebviewActivity.2.1 */
        class C19691 extends AnimatorListenerAdapterProxy {
            C19691() {
            }

            public void onAnimationEnd(Animator animator) {
                WebviewActivity.this.progressView.setVisibility(4);
            }
        }

        C19702() {
        }

        public void onLoadResource(WebView webView, String str) {
            super.onLoadResource(webView, str);
        }

        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            WebviewActivity.this.progressItem.getImageView().setVisibility(0);
            WebviewActivity.this.progressItem.setEnabled(true);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleX", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleY", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
            animatorSet.addListener(new C19691());
            animatorSet.setDuration(150);
            animatorSet.start();
        }
    }

    private class TelegramWebviewProxy {

        /* renamed from: com.hanista.mobogram.ui.WebviewActivity.TelegramWebviewProxy.1 */
        class C19711 implements Runnable {
            final /* synthetic */ String val$eventName;

            C19711(String str) {
                this.val$eventName = str;
            }

            public void run() {
                if (WebviewActivity.this.getParentActivity() != null) {
                    FileLog.m16e("tmessages", this.val$eventName);
                    String str = this.val$eventName;
                    boolean z = true;
                    switch (str.hashCode()) {
                        case -1788360622:
                            if (str.equals("share_game")) {
                                z = false;
                                break;
                            }
                            break;
                        case 406539826:
                            if (str.equals("share_score")) {
                                z = true;
                                break;
                            }
                            break;
                    }
                    switch (z) {
                        case VideoPlayer.TRACK_DEFAULT /*0*/:
                            WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                            break;
                        case WebviewActivity.share /*1*/:
                            WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = true;
                            break;
                    }
                    WebviewActivity.this.showDialog(new ShareAlert(WebviewActivity.this.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy));
                }
            }
        }

        private TelegramWebviewProxy() {
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            AndroidUtilities.runOnUIThread(new C19711(str));
        }
    }

    public WebviewActivity(String str, String str2, String str3, String str4, MessageObject messageObject) {
        this.currentUrl = str;
        this.currentBot = str2;
        this.currentGame = str3;
        this.currentMessageObject = messageObject;
        this.short_param = str4;
        this.linkToCopy = "https://telegram.me/" + this.currentBot + (TextUtils.isEmpty(str4) ? TtmlNode.ANONYMOUS_REGION_ID : "?game=" + str4);
    }

    public static void openGameInBrowser(String str, MessageObject messageObject, Activity activity, String str2, String str3) {
        try {
            int i;
            String str4;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
            String string = sharedPreferences.getString(TtmlNode.ANONYMOUS_REGION_ID + messageObject.getId(), null);
            CharSequence stringBuilder = new StringBuilder(string != null ? string : TtmlNode.ANONYMOUS_REGION_ID);
            StringBuilder stringBuilder2 = new StringBuilder("tgShareScoreUrl=" + URLEncoder.encode("tgb://share_game_score?hash=", C0700C.UTF8_NAME));
            if (string == null) {
                char[] toCharArray = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                for (i = 0; i < 20; i += share) {
                    stringBuilder.append(toCharArray[Utilities.random.nextInt(toCharArray.length)]);
                }
            }
            stringBuilder2.append(stringBuilder);
            i = str.indexOf(35);
            if (i < 0) {
                str4 = str + "#" + stringBuilder2;
            } else {
                String substring = str.substring(i + share);
                str4 = (substring.indexOf(61) >= 0 || substring.indexOf(63) >= 0) ? str + "&" + stringBuilder2 : substring.length() > 0 ? str + "?" + stringBuilder2 : str + stringBuilder2;
            }
            Editor edit = sharedPreferences.edit();
            edit.putInt(stringBuilder + "_date", (int) (System.currentTimeMillis() / 1000));
            AbstractSerializedData serializedData = new SerializedData(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(serializedData);
            edit.putString(stringBuilder + "_m", Utilities.bytesToHex(serializedData.toByteArray()));
            edit.putString(stringBuilder + "_link", "https://telegram.me/" + str3 + (TextUtils.isEmpty(str2) ? TtmlNode.ANONYMOUS_REGION_ID : "?game=" + str2));
            edit.commit();
            Browser.openUrl((Context) activity, str4, false);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public static boolean supportWebview() {
        return ("samsung".equals(Build.MANUFACTURER) && "GT-I9500".equals(Build.MODEL)) ? false : true;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public View createView(Context context) {
        this.swipeBackEnabled = false;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(this.currentGame);
        this.actionBar.setSubtitle("@" + this.currentBot);
        this.actionBar.setActionBarMenuOnItemClick(new C19681());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        this.progressItem = createMenu.addItemWithWidth((int) share, (int) C0338R.drawable.share, AndroidUtilities.dp(54.0f));
        this.progressView = new ContextProgressView(context, share);
        this.progressItem.addView(this.progressView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.progressItem.getImageView().setVisibility(4);
        createMenu.addItem(0, (int) C0338R.drawable.ic_ab_other).addSubItem(open_in, LocaleController.getString("OpenInExternalApp", C0338R.string.OpenInExternalApp), 0);
        this.webView = new WebView(context);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        if (VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
            this.webView.addJavascriptInterface(new TelegramWebviewProxy(), "TelegramWebviewProxy");
        }
        this.webView.setWebViewClient(new C19702());
        frameLayout.addView(this.webView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        return this.fragmentView;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        try {
            ViewParent parent = this.webView.getParent();
            if (parent != null) {
                ((FrameLayout) parent).removeView(this.webView);
            }
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
            this.webView = null;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    protected void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2 && this.webView != null) {
            this.webView.loadUrl(this.currentUrl);
        }
    }
}
