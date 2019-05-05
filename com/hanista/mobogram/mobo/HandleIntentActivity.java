package com.hanista.mobogram.mobo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class HandleIntentActivity extends Activity {
    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean m203a(android.content.Intent r6) {
        /*
        r5 = this;
        r0 = 1;
        r1 = 0;
        r2 = r6.getFlags();
        r3 = com.hanista.mobogram.messenger.UserConfig.isClientActivated();
        if (r3 == 0) goto L_0x0081;
    L_0x000c:
        r3 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r2 = r2 & r3;
        if (r2 != 0) goto L_0x0081;
    L_0x0011:
        if (r6 == 0) goto L_0x0081;
    L_0x0013:
        r2 = r6.getAction();
        if (r2 == 0) goto L_0x0081;
    L_0x0019:
        r2 = "android.intent.action.VIEW";
        r3 = r6.getAction();
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0081;
    L_0x0026:
        r2 = r6.getData();
        if (r2 == 0) goto L_0x0081;
    L_0x002c:
        r3 = r2.getScheme();
        if (r3 == 0) goto L_0x0081;
    L_0x0032:
        r4 = "tg";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0081;
    L_0x003b:
        r2 = r2.toString();
        r3 = "tg:resolve";
        r3 = r2.startsWith(r3);
        if (r3 != 0) goto L_0x0051;
    L_0x0048:
        r3 = "tg://resolve";
        r3 = r2.startsWith(r3);
        if (r3 == 0) goto L_0x006f;
    L_0x0051:
        if (r0 == 0) goto L_0x0057;
    L_0x0053:
        r0 = com.hanista.mobogram.mobo.MoboConstants.aV;
        if (r0 != 0) goto L_0x0083;
    L_0x0057:
        r0 = com.hanista.mobogram.ui.LaunchActivity.class;
        r6.setClass(r5, r0);
        r0 = com.hanista.mobogram.mobo.MoboUtils.m1706b(r5);	 Catch:{ Exception -> 0x0095 }
        if (r0 == 0) goto L_0x0068;
    L_0x0062:
        r2 = "com.hanista.mobogram.referrer";
        r6.putExtra(r2, r0);	 Catch:{ Exception -> 0x0095 }
    L_0x0068:
        r5.startActivity(r6);
    L_0x006b:
        r5.finish();
        return r1;
    L_0x006f:
        r3 = "tg:join";
        r3 = r2.startsWith(r3);
        if (r3 != 0) goto L_0x0051;
    L_0x0078:
        r3 = "tg://join";
        r2 = r2.startsWith(r3);
        if (r2 != 0) goto L_0x0051;
    L_0x0081:
        r0 = r1;
        goto L_0x0051;
    L_0x0083:
        r0 = "AdIsBlocked";
        r2 = 2131166464; // 0x7f070500 float:1.7947174E38 double:1.0529361354E-314;
        r0 = com.hanista.mobogram.messenger.LocaleController.getString(r0, r2);
        r0 = android.widget.Toast.makeText(r5, r0, r1);
        r0.show();
        goto L_0x006b;
    L_0x0095:
        r0 = move-exception;
        goto L_0x0068;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.mobo.HandleIntentActivity.a(android.content.Intent):boolean");
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        m203a(getIntent());
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        m203a(intent);
    }
}
