package com.hanista.mobogram.mobo.dialogdm;

import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.SampleSource;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.VideoPlayer;

/* renamed from: com.hanista.mobogram.mobo.dialogdm.f */
public class DialogDmUtil {
    public static String m615a(int i) {
        return i == -1 ? LocaleController.getString("Last10Messages", C0338R.string.Last10Messages) : i == -2 ? LocaleController.getString("Last20Messages", C0338R.string.Last20Messages) : i == -3 ? LocaleController.getString("Last50Messages", C0338R.string.Last50Messages) : i == -4 ? LocaleController.getString("Last100Messages", C0338R.string.Last100Messages) : i == -5 ? LocaleController.getString("Last200Messages", C0338R.string.Last200Messages) : i == -6 ? LocaleController.getString("Last500Messages", C0338R.string.Last500Messages) : i == 0 ? LocaleController.getString("AllMessages", C0338R.string.AllMessages) : TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static String m616b(int i) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        if ((i & 1) != 0) {
            str = str + LocaleController.getString("AttachPhoto", C0338R.string.AttachPhoto);
        }
        if ((i & 2) != 0) {
            if (str.length() != 0) {
                str = str + ", ";
            }
            str = str + LocaleController.getString("AttachAudio", C0338R.string.AttachAudio);
        }
        if ((i & 4) != 0) {
            if (str.length() != 0) {
                str = str + ", ";
            }
            str = str + LocaleController.getString("AttachVideo", C0338R.string.AttachVideo);
        }
        if ((i & 8) != 0) {
            if (str.length() != 0) {
                str = str + ", ";
            }
            str = str + LocaleController.getString("AttachDocument", C0338R.string.AttachDocument);
        }
        if ((i & 16) != 0) {
            if (str.length() != 0) {
                str = str + ", ";
            }
            str = str + LocaleController.getString("AttachMusic", C0338R.string.AttachMusic);
        }
        if ((i & 32) != 0) {
            if (str.length() != 0) {
                str = str + ", ";
            }
            str = str + LocaleController.getString("AttachGif", C0338R.string.AttachGif);
        }
        return str.length() == 0 ? LocaleController.getString("NoMediaAutoDownload", C0338R.string.NoMediaAutoDownload) : str;
    }

    public static int m617c(int i) {
        switch (i) {
            case -6:
                return 500;
            case -5:
                return Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            case SampleSource.FORMAT_READ /*-4*/:
                return 100;
            case SampleSource.SAMPLE_READ /*-3*/:
                return 50;
            case LayoutHelper.WRAP_CONTENT /*-2*/:
                return 20;
            case VideoPlayer.TRACK_DISABLED /*-1*/:
                return 10;
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return ConnectionsManager.DEFAULT_DATACENTER_ID;
            default:
                return 0;
        }
    }
}
