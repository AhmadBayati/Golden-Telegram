package com.hanista.mobogram.mobo;

import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.mobo.p008i.Font;
import com.hanista.mobogram.mobo.p008i.FontCell;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.LaunchActivity;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.g */
public class FontSelectActivity extends BaseFragment {
    private BaseFragmentAdapter f1091a;
    private ListView f1092b;
    private TextView f1093c;

    /* renamed from: com.hanista.mobogram.mobo.g.1 */
    class FontSelectActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ FontSelectActivity f922a;

        FontSelectActivity(FontSelectActivity fontSelectActivity) {
            this.f922a = fontSelectActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f922a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.2 */
    class FontSelectActivity implements OnTouchListener {
        final /* synthetic */ FontSelectActivity f923a;

        FontSelectActivity(FontSelectActivity fontSelectActivity) {
            this.f923a = fontSelectActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.3 */
    class FontSelectActivity implements OnItemClickListener {
        final /* synthetic */ FontSelectActivity f926a;

        /* renamed from: com.hanista.mobogram.mobo.g.3.1 */
        class FontSelectActivity implements OnClickListener {
            final /* synthetic */ FontSelectActivity f924a;

            FontSelectActivity(FontSelectActivity fontSelectActivity) {
                this.f924a = fontSelectActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                this.f924a.f926a.m1137a();
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.g.3.2 */
        class FontSelectActivity implements OnCancelListener {
            final /* synthetic */ FontSelectActivity f925a;

            FontSelectActivity(FontSelectActivity fontSelectActivity) {
                this.f925a = fontSelectActivity;
            }

            public void onCancel(DialogInterface dialogInterface) {
                this.f925a.f926a.m1137a();
            }
        }

        FontSelectActivity(FontSelectActivity fontSelectActivity) {
            this.f926a = fontSelectActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            FontUtil.m1177a(((Font) FontUtil.m1178b().get(i)).m1158a());
            Builder builder = new Builder(this.f926a.getParentActivity());
            builder.setTitle(LocaleController.getString("Font", C0338R.string.Font)).setMessage(LocaleController.getString("RestartForApplyChanges", C0338R.string.RestartForApplyChanges));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new FontSelectActivity(this));
            builder.setOnCancelListener(new FontSelectActivity(this));
            builder.create().show();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.g.a */
    private class FontSelectActivity extends BaseFragmentAdapter {
        final /* synthetic */ FontSelectActivity f927a;
        private Context f928b;

        public FontSelectActivity(FontSelectActivity fontSelectActivity, Context context) {
            this.f927a = fontSelectActivity;
            this.f928b = context;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public int getCount() {
            return FontUtil.m1178b().size();
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View fontCell = view == null ? new FontCell(this.f928b) : view;
            List b = FontUtil.m1178b();
            Font font = (Font) b.get(i);
            ((FontCell) fontCell).m1175a(font.m1159b(), font, i != b.size() + -1);
            return fontCell;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return LocaleController.getInstance().sortedLanguages == null || LocaleController.getInstance().sortedLanguages.size() == 0;
        }

        public boolean isEnabled(int i) {
            return true;
        }
    }

    private void m1137a() {
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 1234567, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Font", C0338R.string.Font));
        this.actionBar.setActionBarMenuOnItemClick(new FontSelectActivity(this));
        this.f1091a = new FontSelectActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        View linearLayout = new LinearLayout(context);
        linearLayout.setVisibility(4);
        linearLayout.setOrientation(1);
        ((FrameLayout) this.fragmentView).addView(linearLayout);
        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOnTouchListener(new FontSelectActivity(this));
        this.f1093c = new TextView(context);
        this.f1093c.setTextColor(-8355712);
        this.f1093c.setTextSize(20.0f);
        this.f1093c.setGravity(17);
        this.f1093c.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
        linearLayout.addView(this.f1093c);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.f1093c.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        this.f1093c.setLayoutParams(layoutParams2);
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams2);
        this.f1092b = new ListView(context);
        initThemeBackground(this.f1092b);
        this.f1092b.setEmptyView(linearLayout);
        this.f1092b.setVerticalScrollBarEnabled(false);
        this.f1092b.setDivider(null);
        this.f1092b.setDividerHeight(0);
        this.f1092b.setAdapter(this.f1091a);
        ((FrameLayout) this.fragmentView).addView(this.f1092b);
        layoutParams = (LayoutParams) this.f1092b.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.f1092b.setLayoutParams(layoutParams);
        this.f1092b.setOnItemClickListener(new FontSelectActivity(this));
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.f1091a != null) {
            this.f1091a.notifyDataSetChanged();
        }
        initThemeActionBar();
    }
}
