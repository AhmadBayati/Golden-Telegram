package com.hanista.mobogram.mobo.component;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import java.util.Iterator;
import java.util.LinkedList;
import org.json.JSONArray;

public class HistorySelectorView extends LinearLayout {
    JSONArray f353a;
    C0900a f354b;
    int f355c;

    /* renamed from: com.hanista.mobogram.mobo.component.HistorySelectorView.1 */
    class C08991 implements OnClickListener {
        final /* synthetic */ int f351a;
        final /* synthetic */ HistorySelectorView f352b;

        C08991(HistorySelectorView historySelectorView, int i) {
            this.f352b = historySelectorView;
            this.f351a = i;
        }

        public void onClick(View view) {
            this.f352b.setColor(this.f351a);
            this.f352b.m441d();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.HistorySelectorView.a */
    public interface C0900a {
        void m436a(int i);
    }

    public HistorySelectorView(Context context) {
        super(context);
        m439b();
    }

    public HistorySelectorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        m439b();
    }

    private void m439b() {
        addView(((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C0338R.layout.color_historyview, null), new LayoutParams(-1, -1));
        m443a();
        m440c();
    }

    private void m440c() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
        LinearLayout linearLayout = (LinearLayout) findViewById(C0338R.id.colorlist);
        if (this.f353a == null || this.f353a.length() <= 0) {
            findViewById(C0338R.id.nocolors).setVisibility(0);
            linearLayout.setVisibility(8);
            findViewById(C0338R.id.colorlistscroll).setVisibility(8);
            return;
        }
        try {
            for (int length = this.f353a.length() - 1; length >= 0; length--) {
                int i = this.f353a.getInt(length);
                ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(C0338R.layout.color_historyview_item, linearLayout, false);
                TextView textView = (TextView) viewGroup.findViewById(C0338R.id.colorbox);
                textView.setBackgroundColor(i);
                linearLayout.addView(viewGroup);
                textView.setOnClickListener(new C08991(this, i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void m441d() {
        if (this.f354b != null) {
            this.f354b.m436a(getColor());
        }
    }

    private int getColor() {
        return this.f355c;
    }

    private void setColor(int i) {
        this.f355c = i;
    }

    public JSONArray m442a(JSONArray jSONArray, int i, int i2) {
        LinkedList linkedList = new LinkedList();
        for (int i3 = 0; i3 < jSONArray.length(); i3++) {
            linkedList.add(Integer.valueOf(jSONArray.getInt(i3)));
        }
        linkedList.add(Integer.valueOf(i2));
        linkedList.remove(i);
        JSONArray jSONArray2 = new JSONArray();
        Iterator it = linkedList.iterator();
        while (it.hasNext()) {
            jSONArray2.put(((Integer) it.next()).intValue());
        }
        return jSONArray2;
    }

    public void m443a() {
        try {
            this.f353a = new JSONArray(getContext().getSharedPreferences("RECENT_COLORS", 0).getString("HISTORY", TtmlNode.ANONYMOUS_REGION_ID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void m444a(int i) {
        Object obj = null;
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("RECENT_COLORS", 0);
            if (this.f353a == null) {
                this.f353a = new JSONArray();
            }
            for (int i2 = 0; i2 < this.f353a.length(); i2++) {
                if (this.f353a.getInt(i2) == i) {
                    obj = 1;
                    this.f353a = m442a(this.f353a, i2, i);
                }
            }
            if (obj == null) {
                this.f353a.put(i);
            }
            if (this.f353a.length() > 30) {
                JSONArray jSONArray = new JSONArray();
                for (int length = this.f353a.length() - 30; length < this.f353a.length(); length++) {
                    jSONArray.put(this.f353a.getInt(length));
                }
                this.f353a = jSONArray;
            }
            Editor edit = sharedPreferences.edit();
            edit.putString("HISTORY", this.f353a.toString());
            edit.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnColorChangedListener(C0900a c0900a) {
        this.f354b = c0900a;
    }
}
