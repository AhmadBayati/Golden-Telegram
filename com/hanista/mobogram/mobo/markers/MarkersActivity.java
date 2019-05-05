package com.hanista.mobogram.mobo.markers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.PointerIconCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.markers.ToolButton.C0914a;
import com.hanista.mobogram.mobo.markers.ToolButton.SwatchButton;
import com.hanista.mobogram.mobo.markers.ToolButton.ZoomToolButton;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.ActionBar.Theme;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

public class MarkersActivity extends Activity {
    protected MediaScannerConnection f1731a;
    private boolean f1732b;
    private Slate f1733c;
    private ZoomTouchView f1734d;
    private ToolButton f1735e;
    private ToolButton f1736f;
    private ToolButton f1737g;
    private ToolButton f1738h;
    private ToolButton f1739i;
    private ToolButton f1740j;
    private View f1741k;
    private View f1742l;
    private View f1743m;
    private View f1744n;
    private View f1745o;
    private View f1746p;
    private Dialog f1747q;
    private SharedPreferences f1748r;
    private LinkedList<String> f1749s;
    private String f1750t;
    private MediaScannerConnectionClient f1751u;

    /* renamed from: com.hanista.mobogram.mobo.markers.MarkersActivity.1 */
    class C09131 implements MediaScannerConnectionClient {
        final /* synthetic */ MarkersActivity f1717a;

        C09131(MarkersActivity markersActivity) {
            this.f1717a = markersActivity;
        }

        private void m1736a() {
            synchronized (this.f1717a.f1749s) {
                if (this.f1717a.f1749s.isEmpty()) {
                    this.f1717a.f1731a.disconnect();
                    return;
                }
                this.f1717a.f1731a.scanFile((String) this.f1717a.f1749s.removeFirst(), "image/png");
            }
        }

        public void onMediaScannerConnected() {
            Log.v("Markers", "media scanner connected");
            m1736a();
        }

        public void onScanCompleted(String str, Uri uri) {
            Log.v("Markers", "File scanned: " + str);
            synchronized (this.f1717a.f1749s) {
                if (str.equals(this.f1717a.f1750t)) {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("image/png");
                    intent.putExtra("android.intent.extra.STREAM", uri);
                    this.f1717a.startActivity(Intent.createChooser(intent, "Send drawing to:"));
                    this.f1717a.f1750t = null;
                }
                m1736a();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.markers.MarkersActivity.2 */
    class C09152 extends C0914a {
        final /* synthetic */ MarkersActivity f1718a;

        C09152(MarkersActivity markersActivity) {
            this.f1718a = markersActivity;
        }

        public void m1744a(ToolButton toolButton) {
            if (toolButton == this.f1718a.f1736f && toolButton != this.f1718a.f1735e) {
                this.f1718a.f1735e.m1801a();
                this.f1718a.f1748r.edit().putString("tool", (String) this.f1718a.f1736f.getTag()).commit();
            } else if (toolButton == this.f1718a.f1738h && toolButton != this.f1718a.f1737g) {
                this.f1718a.f1737g.m1801a();
                this.f1718a.f1748r.edit().putInt(TtmlNode.ATTR_TTS_COLOR, ((SwatchButton) this.f1718a.f1737g).f1775e).commit();
            }
        }

        public void m1745a(ToolButton toolButton, float f, float f2) {
            this.f1718a.f1733c.setZoomMode(false);
            this.f1718a.f1734d.setEnabled(false);
            this.f1718a.f1733c.m1868c(f, f2);
            this.f1718a.f1735e = this.f1718a.f1736f;
            this.f1718a.f1736f = toolButton;
            if (this.f1718a.f1735e != this.f1718a.f1736f) {
                this.f1718a.f1735e.m1803c();
                this.f1718a.f1748r.edit().putString("tool", (String) this.f1718a.f1736f.getTag()).commit();
            }
        }

        public void m1746a(ToolButton toolButton, int i) {
            this.f1718a.m1790a(i);
            this.f1718a.f1737g = this.f1718a.f1738h;
            this.f1718a.f1738h = toolButton;
            if (this.f1718a.f1737g != this.f1718a.f1738h) {
                this.f1718a.f1737g.m1803c();
                this.f1718a.f1748r.edit().putInt(TtmlNode.ATTR_TTS_COLOR, i).commit();
            }
            if (this.f1718a.f1736f instanceof ZoomToolButton) {
                m1744a(this.f1718a.f1736f);
            }
        }

        public void m1747b(ToolButton toolButton) {
            this.f1718a.f1733c.setZoomMode(true);
            this.f1718a.f1734d.setEnabled(true);
            this.f1718a.f1735e = this.f1718a.f1736f;
            this.f1718a.f1736f = toolButton;
            if (this.f1718a.f1735e != this.f1718a.f1736f) {
                this.f1718a.f1735e.m1803c();
                this.f1718a.f1748r.edit().putString("tool", (String) this.f1718a.f1736f.getTag()).commit();
            }
        }

        public void m1748b(ToolButton toolButton, int i) {
            this.f1718a.m1797b(i);
            this.f1718a.f1739i = this.f1718a.f1740j;
            this.f1718a.f1740j = toolButton;
            if (this.f1718a.f1739i != this.f1718a.f1740j) {
                this.f1718a.f1739i.m1803c();
                this.f1718a.f1748r.edit().putString("tool_type", (String) this.f1718a.f1740j.getTag()).commit();
            }
        }

        public void m1749c(ToolButton toolButton) {
            this.f1718a.f1733c.m1865b();
        }

        public void m1750c(ToolButton toolButton, int i) {
            this.f1718a.f1733c.setDrawingBackground(i);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.markers.MarkersActivity.a */
    public interface C0916a {
        void m1751a(View view);
    }

    /* renamed from: com.hanista.mobogram.mobo.markers.MarkersActivity.3 */
    class C09173 implements C0916a {
        final /* synthetic */ C0914a f1719a;
        final /* synthetic */ MarkersActivity f1720b;

        C09173(MarkersActivity markersActivity, C0914a c0914a) {
            this.f1720b = markersActivity;
            this.f1719a = c0914a;
        }

        public void m1752a(View view) {
            SwatchButton swatchButton = (SwatchButton) view;
            if (swatchButton != null) {
                swatchButton.setCallback(this.f1719a);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.markers.MarkersActivity.4 */
    class C09184 implements C0916a {
        final /* synthetic */ int f1721a;
        final /* synthetic */ MarkersActivity f1722b;

        C09184(MarkersActivity markersActivity, int i) {
            this.f1722b = markersActivity;
            this.f1721a = i;
        }

        public void m1753a(View view) {
            SwatchButton swatchButton = (SwatchButton) view;
            if (swatchButton != null && this.f1721a == swatchButton.f1775e) {
                this.f1722b.f1738h = swatchButton;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.markers.MarkersActivity.5 */
    class C09195 extends AnimatorListenerAdapter {
        final /* synthetic */ MarkersActivity f1723a;

        C09195(MarkersActivity markersActivity) {
            this.f1723a = markersActivity;
        }

        public void onAnimationEnd(Animator animator) {
            if (this.f1723a.f1746p != null) {
                this.f1723a.f1746p.setVisibility(8);
            } else {
                this.f1723a.f1742l.setVisibility(8);
                this.f1723a.f1744n.setVisibility(8);
            }
            this.f1723a.f1743m.setVisibility(8);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.markers.MarkersActivity.6 */
    class C09206 extends AsyncTask<Void, Void, String> {
        final /* synthetic */ boolean f1724a;
        final /* synthetic */ Bitmap f1725b;
        final /* synthetic */ boolean f1726c;
        final /* synthetic */ boolean f1727d;
        final /* synthetic */ MarkersActivity f1728e;

        C09206(MarkersActivity markersActivity, boolean z, Bitmap bitmap, boolean z2, boolean z3) {
            this.f1728e = markersActivity;
            this.f1724a = z;
            this.f1725b = bitmap;
            this.f1726c = z2;
            this.f1727d = z3;
        }

        protected String m1754a(Void... voidArr) {
            try {
                File file;
                File file2 = new File(this.f1728e.m1800e(), this.f1724a ? "Drawings/.temporary" : "Drawings");
                if (!file2.exists()) {
                    if (!file2.mkdirs()) {
                        throw new IOException("cannot create dirs: " + file2);
                    } else if (this.f1724a) {
                        file = new File(file2, ".nomedia");
                        if (!file.exists()) {
                            new FileOutputStream(file).write(10);
                        }
                    }
                }
                file = new File(this.f1728e.getIntent().getStringExtra("output"));
                Log.d("Markers", "save: saving " + file);
                OutputStream fileOutputStream = new FileOutputStream(file);
                this.f1725b.compress(CompressFormat.PNG, 0, fileOutputStream);
                this.f1725b.recycle();
                fileOutputStream.close();
                return file.toString();
            } catch (IOException e) {
                Log.e("Markers", "save: error: " + e);
                return null;
            }
        }

        protected void m1755a(String str) {
            if (str != null && this.f1726c) {
                synchronized (this.f1728e.f1749s) {
                    this.f1728e.f1749s.add(str);
                    if (this.f1726c) {
                        this.f1728e.f1750t = str;
                    }
                    if (!this.f1728e.f1731a.isConnected()) {
                        this.f1728e.f1731a.connect();
                    }
                }
            }
            if (this.f1727d) {
                this.f1728e.f1733c.m1867c();
            }
            if (!this.f1726c) {
                Intent intent = new Intent();
                intent.putExtra("fromMarkers", true);
                this.f1728e.setResult(-1, intent);
                this.f1728e.finish();
            }
        }

        protected /* synthetic */ Object doInBackground(Object[] objArr) {
            return m1754a((Void[]) objArr);
        }

        protected /* synthetic */ void onPostExecute(Object obj) {
            m1755a((String) obj);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.markers.MarkersActivity.7 */
    class C09217 implements OnClickListener {
        final /* synthetic */ MarkersActivity f1729a;

        C09217(MarkersActivity markersActivity) {
            this.f1729a = markersActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.markers.MarkersActivity.8 */
    class C09228 implements OnClickListener {
        final /* synthetic */ MarkersActivity f1730a;

        C09228(MarkersActivity markersActivity) {
            this.f1730a = markersActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    public MarkersActivity() {
        this.f1732b = false;
        this.f1749s = new LinkedList();
        this.f1751u = new C09131(this);
    }

    private String m1757a(Bundle bundle) {
        if (bundle == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder("Bundle{");
        Object obj = 1;
        for (String str : bundle.keySet()) {
            if (obj == null) {
                stringBuilder.append(" ");
            }
            obj = null;
            stringBuilder.append(str + "=(");
            stringBuilder.append(bundle.get(str));
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public static void m1760a(ViewGroup viewGroup, C0916a c0916a) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                m1760a((ViewGroup) childAt, c0916a);
            } else {
                c0916a.m1751a(childAt);
            }
        }
    }

    private void m1761a(Object obj, boolean z) {
        if (obj != null) {
            if (obj instanceof View) {
                ((View) obj).setEnabled(z);
            } else if (obj instanceof MenuItem) {
                ((MenuItem) obj).setEnabled(z);
            }
        }
    }

    static final boolean m1762a() {
        return VERSION.SDK_INT >= 11;
    }

    static final boolean m1765b() {
        return false;
    }

    static final boolean m1768c() {
        return VERSION.SDK_INT >= 19;
    }

    @TargetApi(11)
    private void m1775f() {
        if (m1762a()) {
            if (this.f1746p != null) {
                this.f1746p.setLayerType(1, null);
            } else {
                this.f1744n.setLayerType(1, null);
                this.f1742l.setLayerType(1, null);
            }
            this.f1743m.setLayerType(1, null);
        }
    }

    private void m1777g() {
        this.f1748r = getSharedPreferences("markers", 0);
        String string = this.f1748r.getString("tool", null);
        if (string != null) {
            this.f1736f = (ToolButton) this.f1744n.findViewWithTag(string);
        }
        if (this.f1736f == null) {
            this.f1736f = (ToolButton) this.f1744n.findViewById(C0338R.id.pen_thick);
        }
        if (this.f1736f == null) {
            this.f1736f = (ToolButton) this.f1744n.findViewById(C0338R.id.pen_thin);
        }
        this.f1735e = this.f1736f;
        if (this.f1736f != null) {
            this.f1736f.m1801a();
        }
        ToolButton toolButton = (ToolButton) this.f1744n.findViewWithTag(this.f1748r.getString("tool_type", "type_whiteboard"));
        this.f1740j = toolButton;
        this.f1739i = toolButton;
        if (this.f1740j != null) {
            this.f1740j.m1801a();
        }
        m1760a((ViewGroup) this.f1742l, new C09184(this, this.f1748r.getInt(TtmlNode.ATTR_TTS_COLOR, Theme.MSG_TEXT_COLOR)));
        this.f1737g = this.f1738h;
        if (this.f1738h != null) {
            this.f1738h.m1801a();
        }
        m1795a(true, false);
    }

    private void m1779h() {
        this.f1747q.show();
    }

    private void m1781i() {
        this.f1747q.dismiss();
    }

    private void m1783j() {
        Builder builder = new Builder(this);
        builder.setTitle(LocaleController.getString("Help", C0338R.string.Help)).setMessage(LocaleController.getString("DrawingHelp", C0338R.string.DrawingHelp));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C09228(this));
        builder.create().show();
    }

    private void m1785k() {
        SettingManager settingManager = new SettingManager();
        if (!settingManager.m944b("drawingHelpDisplayed")) {
            settingManager.m943a("drawingHelpDisplayed", true);
            m1783j();
        }
    }

    public void m1790a(int i) {
        this.f1733c.setPenColor(i);
    }

    protected void m1791a(Intent intent) {
        m1792a(intent.getData());
    }

    protected void m1792a(Uri uri) {
        m1796a("temporary.png", true);
        this.f1732b = true;
        try {
            Bitmap bitmap = Media.getBitmap(getContentResolver(), uri);
            if (bitmap != null) {
                this.f1733c.m1862a(bitmap);
                Log.d("Markers", "successfully loaded bitmap: " + bitmap);
                return;
            }
            Log.e("Markers", "couldn't get bitmap from " + uri);
        } catch (FileNotFoundException e) {
            Log.e("Markers", "error loading image from " + uri + ": " + e);
        } catch (IOException e2) {
            Log.e("Markers", "error loading image from " + uri + ": " + e2);
        }
    }

    public void m1793a(String str) {
        m1798b(str, false);
    }

    public void m1794a(String str, boolean z, boolean z2, boolean z3, boolean z4) {
        Bitmap a = this.f1733c.m1860a(!z);
        if (a == null) {
            Log.e("Markers", "save: null bitmap");
        } else {
            new C09206(this, z, a, z3, z4).execute(new Void[0]);
        }
    }

    @TargetApi(11)
    public void m1795a(boolean z, boolean z2) {
        int i;
        if (m1765b()) {
            i = 1280;
            if (!z) {
                i = 1284;
            }
            if (m1768c()) {
                i |= TLRPC.USER_FLAG_UNUSED3;
                if (!z) {
                    i |= InputDeviceCompat.SOURCE_TOUCHSCREEN;
                }
            }
            this.f1733c.setSystemUiVisibility(i);
        }
        i = this.f1743m.getHeight();
        AnimatorSet animatorSet;
        AnimatorSet.Builder with;
        if (z) {
            if (this.f1746p != null) {
                this.f1746p.setVisibility(0);
            } else {
                this.f1742l.setVisibility(0);
                this.f1744n.setVisibility(0);
            }
            this.f1743m.setVisibility(0);
            if (m1762a() && z2) {
                animatorSet = new AnimatorSet();
                with = animatorSet.play(ObjectAnimator.ofFloat(this.f1745o, "alpha", new float[]{0.5f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})).with(ObjectAnimator.ofFloat(this.f1743m, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})).with(ObjectAnimator.ofFloat(this.f1743m, "translationY", new float[]{(float) (-i), 0.0f}));
                if (this.f1746p != null) {
                    with.with(ObjectAnimator.ofFloat(this.f1746p, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                } else {
                    with.with(ObjectAnimator.ofFloat(this.f1742l, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})).with(ObjectAnimator.ofFloat(this.f1742l, "translationY", new float[]{(float) i, 0.0f})).with(ObjectAnimator.ofFloat(this.f1744n, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})).with(ObjectAnimator.ofFloat(this.f1744n, "translationX", new float[]{(float) (-i), 0.0f}));
                }
                animatorSet.setDuration(200);
                animatorSet.start();
            } else if (m1762a()) {
                this.f1745o.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            }
        } else if (m1762a() && z2) {
            animatorSet = new AnimatorSet();
            with = animatorSet.play(ObjectAnimator.ofFloat(this.f1745o, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.5f})).with(ObjectAnimator.ofFloat(this.f1743m, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f})).with(ObjectAnimator.ofFloat(this.f1743m, "translationY", new float[]{0.0f, (float) (-i)}));
            if (this.f1746p != null) {
                with.with(ObjectAnimator.ofFloat(this.f1746p, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f}));
            } else {
                with.with(ObjectAnimator.ofFloat(this.f1742l, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f})).with(ObjectAnimator.ofFloat(this.f1742l, "translationY", new float[]{0.0f, (float) i})).with(ObjectAnimator.ofFloat(this.f1744n, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f})).with(ObjectAnimator.ofFloat(this.f1744n, "translationX", new float[]{0.0f, (float) (-i)}));
            }
            animatorSet.addListener(new C09195(this));
            animatorSet.setDuration(200);
            animatorSet.start();
        } else {
            if (this.f1746p != null) {
                this.f1746p.setVisibility(8);
            } else {
                this.f1742l.setVisibility(8);
                this.f1744n.setVisibility(8);
            }
            this.f1743m.setVisibility(8);
            if (m1762a()) {
                this.f1745o.setAlpha(0.5f);
            }
        }
        this.f1748r.edit().putBoolean("hudup", z).commit();
    }

    public boolean m1796a(String str, boolean z) {
        File file = new File(m1800e(), z ? "Drawings/.temporary" : "Drawings");
        String file2 = new File(file, str).toString();
        Log.d("Markers", "loadDrawing: " + file2);
        if (file.exists()) {
            Options options = new Options();
            options.inDither = false;
            options.inPreferredConfig = Config.ARGB_8888;
            options.inScaled = false;
            Bitmap decodeFile = BitmapFactory.decodeFile(file2, options);
            if (decodeFile != null) {
                this.f1733c.m1862a(decodeFile);
                return true;
            }
        }
        return false;
    }

    public void m1797b(int i) {
        this.f1733c.setPenType(i);
    }

    public void m1798b(String str, boolean z) {
        m1794a(str, z, false, false, false);
    }

    public void clickAbout(View view) {
        m1781i();
        About.m1812a(this);
    }

    public void clickCleaner(View view) {
        Builder builder = new Builder(this);
        builder.setTitle(LocaleController.getString("Help", C0338R.string.Help)).setMessage(LocaleController.getString("DrawingCleanerHelp", C0338R.string.DrawingCleanerHelp));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C09217(this));
        builder.create().show();
    }

    public void clickClear(View view) {
        this.f1733c.m1867c();
    }

    public void clickDebug(View view) {
        int i = 0;
        m1781i();
        boolean z = this.f1733c.getDebugFlags() == 0;
        Slate slate = this.f1733c;
        if (z) {
            i = -1;
        }
        slate.setDebugFlags(i);
        this.f1741k.setSelected(z);
    }

    public void clickHelp(View view) {
        m1783j();
    }

    public void clickLoad(View view) {
        m1781i();
        startActivityForResult(new Intent("android.intent.action.PICK", Media.INTERNAL_CONTENT_URI), PointerIconCompat.TYPE_DEFAULT);
    }

    public void clickLogo(View view) {
        m1795a(!m1799d(), true);
    }

    public void clickMarketLink(View view) {
        m1781i();
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName())));
    }

    public void clickOverflow(View view) {
        if (this.f1747q == null) {
            View inflate = ((LayoutInflater) getSystemService("layout_inflater")).inflate(C0338R.layout.overflow_menu, null);
            this.f1747q = new Dialog(this);
            Window window = this.f1747q.getWindow();
            window.requestFeature(1);
            window.setGravity(53);
            LayoutParams attributes = window.getAttributes();
            attributes.width = -2;
            attributes.height = -2;
            attributes.y = getResources().getDimensionPixelOffset(C0338R.dimen.action_bar_height);
            window.setAttributes(attributes);
            window.setWindowAnimations(16973827);
            window.clearFlags(2);
            this.f1747q.setCanceledOnTouchOutside(true);
            this.f1747q.setContentView(inflate);
            inflate = inflate.getRootView();
            inflate.setBackgroundDrawable(null);
            inflate.setPadding(0, 0, 0, 0);
        }
        m1779h();
    }

    public void clickQr(View view) {
        m1781i();
        QrCode.m1824a(this);
    }

    public void clickSave(View view) {
        view.setEnabled(false);
        m1793a(System.currentTimeMillis() + ".png");
        view.setEnabled(true);
    }

    public void clickSaveAndClear(View view) {
        if (!this.f1733c.m1863a()) {
            view.setEnabled(false);
            m1794a(System.currentTimeMillis() + ".png", false, true, false, true);
            view.setEnabled(true);
        }
    }

    public void clickShare(View view) {
        m1781i();
        m1761a((Object) view, false);
        m1794a(System.currentTimeMillis() + ".png", false, false, true, false);
        m1761a((Object) view, true);
    }

    public void clickShareMarketLink(View view) {
        m1781i();
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", getString(C0338R.string.AppName));
        intent.putExtra("android.intent.extra.TEXT", "http://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(Intent.createChooser(intent, "Share the Markers app with:"));
    }

    public void clickSiteLink(View view) {
        m1781i();
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://dsandler.org/markers?from=app")));
    }

    public void clickUndo(View view) {
        this.f1733c.m1870e();
    }

    public boolean m1799d() {
        return this.f1743m.getVisibility() == 0;
    }

    @TargetApi(8)
    public File m1800e() {
        return AndroidUtilities.getCacheDir();
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        switch (i) {
            case PointerIconCompat.TYPE_DEFAULT /*1000*/:
                if (i2 == -1) {
                    m1791a(intent);
                }
            default:
        }
    }

    public void onAttachedToWindow() {
        getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @TargetApi(11)
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        LayoutParams layoutParams = new LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.format = 1;
        window.setBackgroundDrawableResource(C0338R.drawable.markers_transparent);
        window.setAttributes(layoutParams);
        window.requestFeature(1);
        setContentView(C0338R.layout.main);
        this.f1733c = (Slate) getLastNonConfigurationInstance();
        if (this.f1733c == null) {
            this.f1733c = new Slate(this);
            if (this.f1732b) {
                this.f1732b = false;
            } else {
                m1796a("temporary.png", true);
            }
        }
        ViewGroup viewGroup = (ViewGroup) findViewById(C0338R.id.root);
        viewGroup.addView(this.f1733c, 0);
        this.f1734d = new ZoomTouchView(this);
        this.f1734d.setSlate(this.f1733c);
        this.f1734d.setEnabled(false);
        if (m1762a()) {
            this.f1734d.setAlpha(0.0f);
        }
        viewGroup.addView(this.f1734d, 0);
        this.f1731a = new MediaScannerConnection(this, this.f1751u);
        if (bundle != null) {
            onRestoreInstanceState(bundle);
        }
        this.f1743m = findViewById(C0338R.id.actionbar);
        this.f1746p = findViewById(C0338R.id.hud);
        this.f1744n = findViewById(C0338R.id.tools);
        this.f1742l = findViewById(C0338R.id.colors);
        this.f1745o = findViewById(C0338R.id.logo);
        DecorTracker a = DecorTracker.m1816a();
        a.m1818a(this.f1745o);
        a.m1818a(this.f1743m);
        if (this.f1746p != null) {
            a.m1818a(this.f1746p);
        } else {
            a.m1818a(this.f1744n);
            a.m1818a(this.f1742l);
        }
        m1775f();
        this.f1741k = findViewById(C0338R.id.debug);
        ((TextView) this.f1743m.findViewById(C0338R.id.logotype)).setTypeface(Typeface.create("sans-serif-light", 0));
        C0914a c09152 = new C09152(this);
        this.f1733c.setDrawingBackground(-1);
        m1760a((ViewGroup) this.f1742l, new C09173(this, c09152));
        ((ToolButton) findViewById(C0338R.id.tool_zoom)).setCallback(c09152);
        ((ToolButton) findViewById(C0338R.id.pen_thin)).setCallback(c09152);
        ToolButton toolButton = (ToolButton) findViewById(C0338R.id.pen_medium);
        if (toolButton != null) {
            toolButton.setCallback(c09152);
        }
        ((ToolButton) findViewById(C0338R.id.pen_thick)).setCallback(c09152);
        toolButton = (ToolButton) findViewById(C0338R.id.fat_marker);
        if (toolButton != null) {
            toolButton.setCallback(c09152);
        }
        toolButton = (ToolButton) findViewById(C0338R.id.whiteboard_marker);
        toolButton.setCallback(c09152);
        ToolButton toolButton2 = (ToolButton) findViewById(C0338R.id.felttip_marker);
        if (toolButton2 != null) {
            toolButton2.setCallback(c09152);
        }
        toolButton2 = (ToolButton) findViewById(C0338R.id.airbrush_marker);
        if (toolButton2 != null) {
            toolButton2.setCallback(c09152);
        }
        toolButton2 = (ToolButton) findViewById(C0338R.id.fountainpen_marker);
        if (toolButton2 != null) {
            toolButton2.setCallback(c09152);
        }
        this.f1740j = toolButton;
        this.f1739i = toolButton;
        m1777g();
        this.f1736f.m1801a();
        this.f1740j.m1801a();
        if (getIntent().getParcelableExtra("backgroundImage") != null) {
            m1792a((Uri) getIntent().getParcelableExtra("backgroundImage"));
        }
        m1785k();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 82) {
            return super.onKeyDown(i, keyEvent);
        }
        m1795a(!m1799d(), true);
        return true;
    }

    public void onPause() {
        super.onPause();
    }

    protected void onRestoreInstanceState(Bundle bundle) {
    }

    public void onResume() {
        super.onResume();
        setRequestedOrientation("landscape".equals(getString(C0338R.string.orientation)) ? 0 : 1);
    }

    public Object onRetainNonConfigurationInstance() {
        ((ViewGroup) this.f1733c.getParent()).removeView(this.f1733c);
        return this.f1733c;
    }

    protected void onSaveInstanceState(Bundle bundle) {
    }

    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String action = intent.getAction();
        Log.d("Markers", "starting with intent=" + intent + " action=" + action + " extras=" + m1757a(intent.getExtras()));
        if (action != null) {
            if (action.equals("android.intent.action.EDIT")) {
                this.f1733c.m1867c();
                m1791a(intent);
            } else if (action.equals("android.intent.action.SEND")) {
                this.f1733c.m1867c();
                m1792a((Uri) intent.getParcelableExtra("android.intent.extra.STREAM"));
            }
        }
    }

    protected void onStop() {
        super.onStop();
    }
}
