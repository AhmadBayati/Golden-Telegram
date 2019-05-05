package com.hanista.mobogram.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.hanista.mobogram.ui.LaunchActivity;

public class OpenChatReceiver extends Activity {
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        if (intent.getAction() == null || !intent.getAction().startsWith("com.tmessages.openchat")) {
            finish();
            return;
        }
        Intent intent2 = new Intent(this, LaunchActivity.class);
        intent2.setAction(intent.getAction());
        intent2.putExtras(intent);
        startActivity(intent2);
        finish();
    }
}