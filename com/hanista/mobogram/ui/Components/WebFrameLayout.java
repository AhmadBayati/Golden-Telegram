package com.hanista.mobogram.ui.Components;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.BottomSheetDelegate;
import com.hanista.mobogram.ui.ActionBar.Theme;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebFrameLayout extends FrameLayout {
    static final Pattern youtubeIdRegex;
    private View customView;
    private CustomViewCallback customViewCallback;
    private BottomSheet dialog;
    private String embedUrl;
    private FrameLayout fullscreenVideoContainer;
    private boolean hasDescription;
    private int height;
    private String openUrl;
    private ProgressBar progressBar;
    private WebView webView;
    private int width;
    private final String youtubeFrame;

    /* renamed from: com.hanista.mobogram.ui.Components.WebFrameLayout.1 */
    class C14991 extends WebChromeClient {
        C14991() {
        }

        public void onHideCustomView() {
            super.onHideCustomView();
            if (WebFrameLayout.this.customView != null) {
                if (WebFrameLayout.this.dialog != null) {
                    WebFrameLayout.this.dialog.getSheetContainer().setVisibility(0);
                    WebFrameLayout.this.fullscreenVideoContainer.setVisibility(4);
                    WebFrameLayout.this.fullscreenVideoContainer.removeView(WebFrameLayout.this.customView);
                }
                if (!(WebFrameLayout.this.customViewCallback == null || WebFrameLayout.this.customViewCallback.getClass().getName().contains(".chromium."))) {
                    WebFrameLayout.this.customViewCallback.onCustomViewHidden();
                }
                WebFrameLayout.this.customView = null;
            }
        }

        public void onShowCustomView(View view, int i, CustomViewCallback customViewCallback) {
            onShowCustomView(view, customViewCallback);
        }

        public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
            if (WebFrameLayout.this.customView != null) {
                customViewCallback.onCustomViewHidden();
                return;
            }
            WebFrameLayout.this.customView = view;
            if (WebFrameLayout.this.dialog != null) {
                WebFrameLayout.this.dialog.getSheetContainer().setVisibility(4);
                WebFrameLayout.this.fullscreenVideoContainer.setVisibility(0);
                WebFrameLayout.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
            }
            WebFrameLayout.this.customViewCallback = customViewCallback;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.WebFrameLayout.2 */
    class C15002 extends WebViewClient {
        C15002() {
        }

        public void onLoadResource(WebView webView, String str) {
            super.onLoadResource(webView, str);
        }

        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            WebFrameLayout.this.progressBar.setVisibility(4);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.WebFrameLayout.3 */
    class C15013 implements OnClickListener {
        C15013() {
        }

        public void onClick(View view) {
            if (WebFrameLayout.this.dialog != null) {
                WebFrameLayout.this.dialog.dismiss();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.WebFrameLayout.4 */
    class C15024 implements OnClickListener {
        C15024() {
        }

        public void onClick(View view) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", WebFrameLayout.this.openUrl));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            Toast.makeText(WebFrameLayout.this.getContext(), LocaleController.getString("LinkCopied", C0338R.string.LinkCopied), 0).show();
            if (WebFrameLayout.this.dialog != null) {
                WebFrameLayout.this.dialog.dismiss();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.WebFrameLayout.5 */
    class C15035 implements OnClickListener {
        C15035() {
        }

        public void onClick(View view) {
            Browser.openUrl(WebFrameLayout.this.getContext(), WebFrameLayout.this.openUrl);
            if (WebFrameLayout.this.dialog != null) {
                WebFrameLayout.this.dialog.dismiss();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.WebFrameLayout.6 */
    class C15046 implements OnTouchListener {
        C15046() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.WebFrameLayout.7 */
    class C15057 extends BottomSheetDelegate {
        C15057() {
        }

        public void onOpenAnimationEnd() {
            Throwable e;
            Object obj = null;
            Object obj2 = 1;
            Map hashMap = new HashMap();
            hashMap.put("Referer", "http://youtube.com");
            try {
                String toLowerCase = Uri.parse(WebFrameLayout.this.openUrl).getHost().toLowerCase();
                if ((toLowerCase != null && toLowerCase.endsWith("youtube.com")) || toLowerCase.endsWith("youtu.be")) {
                    Matcher matcher = WebFrameLayout.youtubeIdRegex.matcher(WebFrameLayout.this.openUrl);
                    if ((matcher.find() ? matcher.group(1) : null) != null) {
                        try {
                            WebFrameLayout.this.webView.loadDataWithBaseURL("http://youtube.com", String.format("<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,    .embed-container embed {        position: absolute;        top: 0;        left: 0;        width: 100%% !important;        height: 100%% !important;    }    </style></head><body>    <div class=\"embed-container\">        <div id=\"player\"></div>    </div>    <script src=\"https://www.youtube.com/iframe_api\"></script>    <script>    var player;    YT.ready(function() {         player = new YT.Player(\"player\", {                                \"width\" : \"100%%\",                                \"events\" : {                                \"onReady\" : \"onReady\",                                },                                \"videoId\" : \"%1$s\",                                \"height\" : \"100%%\",                                \"playerVars\" : {                                \"start\" : 0,                                \"rel\" : 0,                                \"showinfo\" : 0,                                \"modestbranding\" : 1,                                \"iv_load_policy\" : 3,                                \"autohide\" : 1,                                \"cc_load_policy\" : 1,                                \"playsinline\" : 1,                                \"controls\" : 1                                }                                });        player.setSize(window.innerWidth, window.innerHeight);    });    function onReady(event) {        player.playVideo();    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>", new Object[]{matcher.find() ? matcher.group(1) : null}), "text/html", C0700C.UTF8_NAME, "http://youtube.com");
                        } catch (Exception e2) {
                            e = e2;
                            FileLog.m18e("tmessages", e);
                            obj = obj2;
                            if (obj != null) {
                                try {
                                    WebFrameLayout.this.webView.loadUrl(WebFrameLayout.this.embedUrl, hashMap);
                                } catch (Throwable e3) {
                                    FileLog.m18e("tmessages", e3);
                                    return;
                                }
                            }
                        }
                        obj = obj2;
                    }
                }
            } catch (Throwable e4) {
                obj2 = null;
                e3 = e4;
                FileLog.m18e("tmessages", e3);
                obj = obj2;
                if (obj != null) {
                    WebFrameLayout.this.webView.loadUrl(WebFrameLayout.this.embedUrl, hashMap);
                }
            }
            if (obj != null) {
                WebFrameLayout.this.webView.loadUrl(WebFrameLayout.this.embedUrl, hashMap);
            }
        }
    }

    static {
        youtubeIdRegex = Pattern.compile("(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})");
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public WebFrameLayout(Context context, BottomSheet bottomSheet, String str, String str2, String str3, String str4, int i, int i2) {
        View textView;
        super(context);
        this.youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,    .embed-container embed {        position: absolute;        top: 0;        left: 0;        width: 100%% !important;        height: 100%% !important;    }    </style></head><body>    <div class=\"embed-container\">        <div id=\"player\"></div>    </div>    <script src=\"https://www.youtube.com/iframe_api\"></script>    <script>    var player;    YT.ready(function() {         player = new YT.Player(\"player\", {                                \"width\" : \"100%%\",                                \"events\" : {                                \"onReady\" : \"onReady\",                                },                                \"videoId\" : \"%1$s\",                                \"height\" : \"100%%\",                                \"playerVars\" : {                                \"start\" : 0,                                \"rel\" : 0,                                \"showinfo\" : 0,                                \"modestbranding\" : 1,                                \"iv_load_policy\" : 3,                                \"autohide\" : 1,                                \"cc_load_policy\" : 1,                                \"playsinline\" : 1,                                \"controls\" : 1                                }                                });        player.setSize(window.innerWidth, window.innerHeight);    });    function onReady(event) {        player.playVideo();    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>";
        this.embedUrl = str4;
        boolean z = str2 != null && str2.length() > 0;
        this.hasDescription = z;
        this.openUrl = str3;
        this.width = i;
        this.height = i2;
        if (this.width == 0 || this.height == 0) {
            this.width = AndroidUtilities.displaySize.x;
            this.height = AndroidUtilities.displaySize.y / 2;
        }
        this.dialog = bottomSheet;
        this.fullscreenVideoContainer = new FrameLayout(context);
        this.fullscreenVideoContainer.setBackgroundColor(Theme.MSG_TEXT_COLOR);
        if (VERSION.SDK_INT >= 21) {
            this.fullscreenVideoContainer.setFitsSystemWindows(true);
        }
        bottomSheet.setApplyTopPadding(false);
        bottomSheet.setApplyBottomPadding(false);
        this.dialog.getContainer().addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.fullscreenVideoContainer.setVisibility(4);
        this.webView = new WebView(context);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        if (VERSION.SDK_INT >= 17) {
            this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        String userAgentString = this.webView.getSettings().getUserAgentString();
        if (userAgentString != null) {
            this.webView.getSettings().setUserAgentString(userAgentString.replace("Android", TtmlNode.ANONYMOUS_REGION_ID));
        }
        if (VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        }
        this.webView.setWebChromeClient(new C14991());
        this.webView.setWebViewClient(new C15002());
        addView(this.webView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 0.0f, 0.0f, 0.0f, (float) ((this.hasDescription ? 22 : 0) + 84)));
        this.progressBar = new ProgressBar(context);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, (float) (((this.hasDescription ? 22 : 0) + 84) / 2)));
        if (this.hasDescription) {
            textView = new TextView(context);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.REPLY_PANEL_MESSAGE_TEXT_COLOR);
            textView.setText(str2);
            textView.setSingleLine(true);
            textView.setTypeface(FontUtil.m1176a().m1160c());
            textView.setEllipsize(TruncateAt.END);
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 77.0f));
        }
        textView = new TextView(context);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-7697782);
        textView.setText(str);
        textView.setSingleLine(true);
        textView.setEllipsize(TruncateAt.END);
        textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 57.0f));
        View view = new View(context);
        view.setBackgroundColor(-2368549);
        addView(view, new LayoutParams(-1, 1, 83));
        ((LayoutParams) view.getLayoutParams()).bottomMargin = AndroidUtilities.dp(48.0f);
        view = new FrameLayout(context);
        view.setBackgroundColor(-1);
        addView(view, LayoutHelper.createFrame(-1, 48, 83));
        View textView2 = new TextView(context);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(-15095832);
        textView2.setGravity(17);
        textView2.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR, false));
        textView2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView2.setText(LocaleController.getString("Close", C0338R.string.Close).toUpperCase());
        textView2.setTypeface(FontUtil.m1176a().m1160c());
        view.addView(textView2, LayoutHelper.createFrame(-2, -1, 51));
        textView2.setOnClickListener(new C15013());
        textView2 = new LinearLayout(context);
        textView2.setOrientation(0);
        view.addView(textView2, LayoutHelper.createFrame(-2, -1, 53));
        view = new TextView(context);
        view.setTextSize(1, 14.0f);
        view.setTextColor(-15095832);
        view.setGravity(17);
        view.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR, false));
        view.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        view.setText(LocaleController.getString("Copy", C0338R.string.Copy).toUpperCase());
        view.setTypeface(FontUtil.m1176a().m1160c());
        textView2.addView(view, LayoutHelper.createFrame(-2, -1, 51));
        view.setOnClickListener(new C15024());
        view = new TextView(context);
        view.setTextSize(1, 14.0f);
        view.setTextColor(-15095832);
        view.setGravity(17);
        view.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR, false));
        view.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        view.setText(LocaleController.getString("OpenInBrowser", C0338R.string.OpenInBrowser).toUpperCase());
        view.setTypeface(FontUtil.m1176a().m1160c());
        textView2.addView(view, LayoutHelper.createFrame(-2, -1, 51));
        view.setOnClickListener(new C15035());
        setOnTouchListener(new C15046());
        bottomSheet.setDelegate(new C15057());
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            removeView(this.webView);
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec((AndroidUtilities.dp((float) ((this.hasDescription ? 22 : 0) + 84)) + ((int) Math.min(((float) this.height) / ((float) (this.width / MeasureSpec.getSize(i))), (float) (AndroidUtilities.displaySize.y / 2)))) + 1, C0700C.ENCODING_PCM_32BIT));
    }
}
