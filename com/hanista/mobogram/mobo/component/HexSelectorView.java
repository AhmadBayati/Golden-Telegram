package com.hanista.mobogram.mobo.component;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.hanista.mobogram.C0338R;

/* renamed from: com.hanista.mobogram.mobo.component.c */
public class HexSelectorView extends LinearLayout {
    private EditText f413a;
    private int f414b;
    private TextView f415c;
    private Button f416d;
    private Dialog f417e;
    private HexSelectorView f418f;

    /* renamed from: com.hanista.mobogram.mobo.component.c.a */
    public interface HexSelectorView {
        void m424a(int i);
    }

    /* renamed from: com.hanista.mobogram.mobo.component.c.1 */
    class HexSelectorView implements OnFocusChangeListener {
        final /* synthetic */ HexSelectorView f408a;

        HexSelectorView(HexSelectorView hexSelectorView) {
            this.f408a = hexSelectorView;
        }

        public void onFocusChange(View view, boolean z) {
            if (z && this.f408a.f417e != null) {
                this.f408a.f417e.getWindow().setSoftInputMode(5);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.c.2 */
    class HexSelectorView implements OnKeyListener {
        final /* synthetic */ HexSelectorView f409a;

        HexSelectorView(HexSelectorView hexSelectorView) {
            this.f409a = hexSelectorView;
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            Log.d("HexSelector", "onKey: keyCode" + i + " event: " + keyEvent);
            this.f409a.m481a();
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.c.3 */
    class HexSelectorView implements TextWatcher {
        final /* synthetic */ HexSelectorView f410a;

        HexSelectorView(HexSelectorView hexSelectorView) {
            this.f410a = hexSelectorView;
        }

        public void afterTextChanged(Editable editable) {
            this.f410a.m481a();
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.c.4 */
    class HexSelectorView implements OnEditorActionListener {
        final /* synthetic */ HexSelectorView f411a;

        HexSelectorView(HexSelectorView hexSelectorView) {
            this.f411a = hexSelectorView;
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 && i != 0) {
                return false;
            }
            this.f411a.m481a();
            ((InputMethodManager) this.f411a.getContext().getSystemService("input_method")).hideSoftInputFromWindow(this.f411a.f413a.getApplicationWindowToken(), 2);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.c.5 */
    class HexSelectorView implements OnClickListener {
        final /* synthetic */ HexSelectorView f412a;

        HexSelectorView(HexSelectorView hexSelectorView) {
            this.f412a = hexSelectorView;
        }

        public void onClick(View view) {
            this.f412a.m481a();
        }
    }

    public HexSelectorView(Context context) {
        super(context);
        m479b();
    }

    private String m477a(String str, char c, int i) {
        if (str.length() >= i) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int length = str.length(); length < i; length++) {
            stringBuilder.append(c);
        }
        stringBuilder.append(str);
        return stringBuilder.toString();
    }

    private void m479b() {
        View inflate = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C0338R.layout.color_hexview, null);
        addView(inflate, new LayoutParams(-1, -1));
        this.f415c = (TextView) inflate.findViewById(C0338R.id.color_hex_txtError);
        this.f413a = (EditText) inflate.findViewById(C0338R.id.color_hex_edit);
        this.f413a.setOnFocusChangeListener(new HexSelectorView(this));
        this.f413a.setOnKeyListener(new HexSelectorView(this));
        this.f413a.addTextChangedListener(new HexSelectorView(this));
        this.f413a.setOnEditorActionListener(new HexSelectorView(this));
        this.f416d = (Button) inflate.findViewById(C0338R.id.color_hex_btnSave);
        this.f416d.setOnClickListener(new HexSelectorView(this));
    }

    private void m480c() {
        Log.d("HexSelector", "String parsing succeeded. changing to " + this.f414b);
        if (this.f418f != null) {
            this.f418f.m424a(getColor());
        }
    }

    public void m481a() {
        try {
            String trim = this.f413a.getText().toString().toUpperCase().trim();
            Log.d("HexSelector", "String parsing: " + trim);
            if (trim.startsWith("0x")) {
                trim = trim.substring(2);
            }
            if (trim.startsWith("#")) {
                trim = trim.substring(1);
            }
            if (trim.length() != 8) {
                throw new Exception();
            }
            this.f414b = (int) Long.parseLong(trim, 16);
            this.f415c.setVisibility(8);
            m480c();
        } catch (Exception e) {
            Log.d("HexSelector", "String parsing died");
            e.printStackTrace();
            this.f415c.setVisibility(0);
        }
    }

    public int getColor() {
        return this.f414b;
    }

    public void setColor(int i) {
        if (i != this.f414b) {
            this.f414b = i;
            this.f413a.setText(m477a(Integer.toHexString(i).toUpperCase(), '0', 8));
            this.f415c.setVisibility(8);
        }
    }

    public void setDialog(Dialog dialog) {
        this.f417e = dialog;
    }

    public void setOnColorChangedListener(HexSelectorView hexSelectorView) {
        this.f418f = hexSelectorView;
    }
}
