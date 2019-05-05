package com.hanista.mobogram.mobo.p003d;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;

/* renamed from: com.hanista.mobogram.mobo.d.b */
public class ImageListActivity extends BaseFragment {
    private static String[] f505e;
    private static String[] f506f;
    Integer[] f507a;
    Integer[] f508b;
    private int f509c;
    private ImageListActivity f510d;

    /* renamed from: com.hanista.mobogram.mobo.d.b.1 */
    class ImageListActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ ImageListActivity f499a;

        ImageListActivity(ImageListActivity imageListActivity) {
            this.f499a = imageListActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f499a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.d.b.2 */
    class ImageListActivity implements OnItemClickListener {
        final /* synthetic */ ImageListActivity f500a;

        ImageListActivity(ImageListActivity imageListActivity) {
            this.f500a = imageListActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            String str = this.f500a.f509c == 0 ? ImageListActivity.f505e[i] : ImageListActivity.f506f[i];
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mobotheme", 0);
            String str2 = this.f500a.f509c == 0 ? "chatBubbleStyle" : "chatCheckStyle";
            if (!sharedPreferences.getString(str2, TtmlNode.ANONYMOUS_REGION_ID).equals(str)) {
                Editor edit = sharedPreferences.edit();
                edit.putString(str2, str);
                edit.apply();
                if (this.f500a.f509c == 0) {
                    Theme.setBubbles(this.f500a.getParentActivity());
                } else {
                    Theme.setChecks(this.f500a.getParentActivity());
                }
            }
            this.f500a.f510d.notifyDataSetChanged();
            this.f500a.finishFragment();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.d.b.a */
    private class ImageListActivity extends ArrayAdapter<String> {
        final /* synthetic */ ImageListActivity f501a;
        private final Context f502b;
        private final String[] f503c;
        private final Integer[] f504d;

        public ImageListActivity(ImageListActivity imageListActivity, Context context, String[] strArr, Integer[] numArr) {
            this.f501a = imageListActivity;
            super(context, C0338R.layout.imagelist, strArr);
            this.f502b = context;
            this.f503c = strArr;
            this.f504d = numArr;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = (LayoutInflater) this.f502b.getSystemService("layout_inflater");
            String string = ApplicationLoader.applicationContext.getSharedPreferences("mobotheme", 0).getString(this.f501a.f509c == 0 ? "chatBubbleStyle" : "chatCheckStyle", this.f503c[0]);
            View inflate = layoutInflater.inflate(C0338R.layout.imagelist, viewGroup, false);
            if (string.equals(this.f503c[i])) {
                inflate.setBackgroundColor(-3092272);
            } else {
                inflate.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
            }
            ImageView imageView = (ImageView) inflate.findViewById(C0338R.id.bubble_in);
            ImageView imageView2 = (ImageView) inflate.findViewById(C0338R.id.bubble_out);
            ((TextView) inflate.findViewById(C0338R.id.bubble_title)).setText(this.f503c[i]);
            imageView.setImageResource(this.f504d[i].intValue());
            imageView2.setImageResource(this.f504d[this.f503c.length + i].intValue());
            if (this.f501a.f509c == 1) {
                inflate.setPadding(50, 0, 0, 0);
                imageView.getLayoutParams().width = 70;
                imageView.setColorFilter(0, Mode.SRC_ATOP);
                imageView2.getLayoutParams().width = 70;
                imageView2.setColorFilter(0, Mode.SRC_ATOP);
            }
            return inflate;
        }
    }

    static {
        f505e = new String[]{"Telegram", "Lex", "Hangouts", "Notepad", "Ed", "Edge", "iOS", "Telegram_old"};
        f506f = new String[]{"Stock", "EdCheck", "Lex", "Gladiator", "MaxChecks", "ElipLex", "CubeLex", "MaxLines", "RLex", "MaxLinesPro", "ReadLex", "MaxHeart"};
    }

    public static String m525a(int i) {
        return f505e[i];
    }

    public static String m528b(int i) {
        return f506f[i];
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(this.f509c == 0 ? LocaleController.getString("BubbleStyle", C0338R.string.BubbleStyle) : LocaleController.getString("CheckStyle", C0338R.string.CheckStyle));
        this.actionBar.setActionBarMenuOnItemClick(new ImageListActivity(this));
        this.fragmentView = getParentActivity().getLayoutInflater().inflate(C0338R.layout.imagelistlayout, null, false);
        this.f510d = new ImageListActivity(this, context, this.f509c == 0 ? f505e : f506f, this.f509c == 0 ? this.f507a : this.f508b);
        ListView listView = (ListView) this.fragmentView.findViewById(C0338R.id.list);
        listView.setAdapter(this.f510d);
        listView.setDivider(null);
        listView.setOnItemClickListener(new ImageListActivity(this));
        return this.fragmentView;
    }

    public boolean onFragmentCreate() {
        this.f509c = this.arguments.getInt("array_id", 0);
        if (this.f509c != 0) {
            super.onFragmentCreate();
        } else {
            super.onFragmentCreate();
        }
        return true;
    }
}
