package com.hanista.mobogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MusicPlayerReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.MEDIA_BUTTON")) {
            if (intent.getExtras() != null) {
                KeyEvent keyEvent = (KeyEvent) intent.getExtras().get("android.intent.extra.KEY_EVENT");
                if (keyEvent != null && keyEvent.getAction() == 0) {
                    switch (keyEvent.getKeyCode()) {
                        case 79:
                        case 85:
                            if (MediaController.m71a().m191s()) {
                                MediaController.m71a().m158a(MediaController.m71a().m182j());
                            } else {
                                MediaController.m71a().m166b(MediaController.m71a().m182j());
                            }
                        case 86:
                        case 87:
                            MediaController.m71a().m184l();
                        case 88:
                            MediaController.m71a().m185m();
                        case 126:
                            MediaController.m71a().m158a(MediaController.m71a().m182j());
                        case 127:
                            MediaController.m71a().m166b(MediaController.m71a().m182j());
                        default:
                    }
                }
            }
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PLAY)) {
            MediaController.m71a().m158a(MediaController.m71a().m182j());
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PAUSE) || intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")) {
            MediaController.m71a().m166b(MediaController.m71a().m182j());
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_NEXT)) {
            MediaController.m71a().m184l();
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_CLOSE)) {
            MediaController.m71a().m155a(true, true);
        } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PREVIOUS)) {
            MediaController.m71a().m185m();
        }
    }
}
