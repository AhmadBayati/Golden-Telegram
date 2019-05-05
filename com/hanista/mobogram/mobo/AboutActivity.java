package com.hanista.mobogram.mobo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.LaunchActivity;

/* renamed from: com.hanista.mobogram.mobo.a */
public class AboutActivity extends BaseFragment implements OnClickListener {
    private View f145a;

    /* renamed from: com.hanista.mobogram.mobo.a.1 */
    class AboutActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ AboutActivity f79a;

        AboutActivity(AboutActivity aboutActivity) {
            this.f79a = aboutActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f79a.finishFragment();
            }
        }
    }

    @SuppressLint({"InflateParams"})
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(context.getString(C0338R.string.About));
        this.actionBar.setActionBarMenuOnItemClick(new AboutActivity(this));
        this.f145a = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(C0338R.layout.about_activity, null);
        this.fragmentView = this.f145a;
        ((TextView) this.f145a.findViewById(C0338R.id.about_version)).setText(context.getString(C0338R.string.AppName) + "\n" + MoboUtils.m1708b(context));
        this.f145a.findViewById(C0338R.id.layout_comment).setOnClickListener(this);
        this.f145a.findViewById(C0338R.id.layout_products).setOnClickListener(this);
        this.f145a.findViewById(C0338R.id.layout_channel).setOnClickListener(this);
        this.f145a.findViewById(C0338R.id.layout_mail).setOnClickListener(this);
        this.f145a.findViewById(C0338R.id.layout_powered_by).setOnClickListener(this);
        return this.f145a;
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case C0338R.id.layout_comment /*2131558458*/:
                MoboUtils.m1715d(getParentActivity());
            case C0338R.id.layout_products /*2131558460*/:
                intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("bazaar://collection?slug=by_author&aid=6149"));
                getParentActivity().startActivity(intent);
            case C0338R.id.layout_channel /*2131558462*/:
                intent = new Intent(getParentActivity(), LaunchActivity.class);
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse("https://telegram.me/hanista_channel"));
                getParentActivity().startActivity(intent);
            case C0338R.id.layout_mail /*2131558464*/:
                MoboUtils.m1699a(getParentActivity(), getParentActivity().getString(C0338R.string.AppName) + "-" + MoboUtils.m1708b(getParentActivity()), TtmlNode.ANONYMOUS_REGION_ID, null);
            case C0338R.id.layout_powered_by /*2131558466*/:
                MoboUtils.m1722g(getParentActivity());
            default:
        }
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}
