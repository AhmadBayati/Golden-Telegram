package com.hanista.mobogram.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.SerializedData;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.ui.Components.ShareAlert;

public class ShareActivity extends Activity {
    private Dialog visibleDialog;

    /* renamed from: com.hanista.mobogram.ui.ShareActivity.1 */
    class C19121 implements OnDismissListener {
        C19121() {
        }

        public void onDismiss(DialogInterface dialogInterface) {
            if (!ShareActivity.this.isFinishing()) {
                ShareActivity.this.finish();
            }
            ShareActivity.this.visibleDialog = null;
        }
    }

    protected void onCreate(Bundle bundle) {
        ApplicationLoader.postInitApplication();
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        requestWindowFeature(1);
        setTheme(C0338R.style.Theme_TMessages_Transparent);
        super.onCreate(bundle);
        setContentView(new View(this), new LayoutParams(-1, -1));
        Intent intent = getIntent();
        if (intent == null || !"android.intent.action.VIEW".equals(intent.getAction()) || intent.getData() == null) {
            finish();
            return;
        }
        Uri data = intent.getData();
        String scheme = data.getScheme();
        String uri = data.toString();
        String queryParameter = data.getQueryParameter("hash");
        if ("tgb".equals(scheme) && uri.toLowerCase().startsWith("tgb://share_game_score") && !TextUtils.isEmpty(queryParameter)) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
            Object string = sharedPreferences.getString(queryParameter + "_m", null);
            if (TextUtils.isEmpty(string)) {
                finish();
                return;
            }
            AbstractSerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
            Message TLdeserialize = Message.TLdeserialize(serializedData, serializedData.readInt32(false), false);
            if (TLdeserialize == null) {
                finish();
                return;
            }
            String string2 = sharedPreferences.getString(queryParameter + "_link", null);
            MessageObject messageObject = new MessageObject(TLdeserialize, null, false);
            messageObject.messageOwner.with_my_score = true;
            try {
                this.visibleDialog = new ShareAlert(this, messageObject, null, false, string2);
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new C19121());
                this.visibleDialog.show();
                return;
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                finish();
                return;
            }
        }
        finish();
    }

    public void onPause() {
        super.onPause();
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }
}
