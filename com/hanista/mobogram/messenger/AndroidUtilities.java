package com.hanista.mobogram.messenger;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;
import android.support.v4.internal.view.SupportMenu;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
import com.hanista.mobogram.ui.Components.ForegroundDetector;
import com.hanista.mobogram.ui.Components.NumberPicker;
import com.hanista.mobogram.ui.Components.NumberPicker.Formatter;
import com.hanista.mobogram.ui.Components.TypefaceSpan;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Pattern;

public class AndroidUtilities {
    public static final int FLAG_TAG_ALL = 7;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_COLOR = 4;
    public static Pattern WEB_URL;
    private static int adjustOwnerClassGuid;
    private static RectF bitmapRect;
    private static final Object callLock;
    public static float density;
    public static DisplayMetrics displayMetrics;
    public static Point displaySize;
    public static boolean incorrectDisplaySizeFix;
    public static boolean isInMultiwindow;
    private static Boolean isTablet;
    public static int leftBaseline;
    private static Field mAttachInfoField;
    private static Field mStableInsetsField;
    public static Integer photoSize;
    private static int prevOrientation;
    private static Paint roundPaint;
    private static final Object smsLock;
    public static int statusBarHeight;
    private static final Hashtable<String, Typeface> typefaceCache;
    public static boolean usingHardwareInput;
    private static boolean waitingForCall;
    private static boolean waitingForSms;

    /* renamed from: com.hanista.mobogram.messenger.AndroidUtilities.1 */
    static class C03391 implements OnClickListener {
        final /* synthetic */ BaseFragment val$fragment;

        C03391(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            try {
                this.val$fragment.getParentActivity().startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.maps")), 500);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.AndroidUtilities.2 */
    static class C03402 implements Formatter {
        C03402() {
        }

        public String format(int i) {
            return i == 0 ? LocaleController.getString("ShortMessageLifetimeForever", C0338R.string.ShortMessageLifetimeForever) : (i < AndroidUtilities.FLAG_TAG_BR || i >= 16) ? i == 16 ? AndroidUtilities.formatTTLString(30) : i == 17 ? AndroidUtilities.formatTTLString(60) : i == 18 ? AndroidUtilities.formatTTLString(3600) : i == 19 ? AndroidUtilities.formatTTLString(86400) : i == 20 ? AndroidUtilities.formatTTLString(604800) : TtmlNode.ANONYMOUS_REGION_ID : AndroidUtilities.formatTTLString(i);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.AndroidUtilities.3 */
    static class C03413 implements OnClickListener {
        final /* synthetic */ EncryptedChat val$encryptedChat;
        final /* synthetic */ NumberPicker val$numberPicker;

        C03413(EncryptedChat encryptedChat, NumberPicker numberPicker) {
            this.val$encryptedChat = encryptedChat;
            this.val$numberPicker = numberPicker;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            int i2 = this.val$encryptedChat.ttl;
            int value = this.val$numberPicker.getValue();
            if (value >= 0 && value < 16) {
                this.val$encryptedChat.ttl = value;
            } else if (value == 16) {
                this.val$encryptedChat.ttl = 30;
            } else if (value == 17) {
                this.val$encryptedChat.ttl = 60;
            } else if (value == 18) {
                this.val$encryptedChat.ttl = 3600;
            } else if (value == 19) {
                this.val$encryptedChat.ttl = 86400;
            } else if (value == 20) {
                this.val$encryptedChat.ttl = 604800;
            }
            if (i2 != this.val$encryptedChat.ttl) {
                SecretChatHelper.getInstance().sendTTLMessage(this.val$encryptedChat, null);
                MessagesStorage.getInstance().updateEncryptedChatTTL(this.val$encryptedChat);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.AndroidUtilities.4 */
    static class C03424 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$num;
        final /* synthetic */ View val$view;
        final /* synthetic */ float val$x;

        C03424(View view, int i, float f) {
            this.val$view = view;
            this.val$num = i;
            this.val$x = f;
        }

        public void onAnimationEnd(Animator animator) {
            AndroidUtilities.shakeView(this.val$view, this.val$num == 5 ? 0.0f : -this.val$x, this.val$num + AndroidUtilities.FLAG_TAG_BR);
        }
    }

    static {
        typefaceCache = new Hashtable();
        prevOrientation = -10;
        waitingForSms = false;
        waitingForCall = false;
        smsLock = new Object();
        callLock = new Object();
        statusBarHeight = 0;
        density = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        displaySize = new Point();
        photoSize = null;
        displayMetrics = new DisplayMetrics();
        isTablet = null;
        adjustOwnerClassGuid = 0;
        WEB_URL = null;
        try {
            String str = "a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef";
            String str2 = "[a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef\\-]{0,61}[a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]){0,1}";
            str2 = "a-zA-Z\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef";
            str2 = "[a-zA-Z\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]{2,63}";
            str2 = "([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef\\-]{0,61}[a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]){0,1}\\.)+[a-zA-Z\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]{2,63}";
            WEB_URL = Pattern.compile("((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + Pattern.compile("(([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]([a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef\\-]{0,61}[a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]){0,1}\\.)+[a-zA-Z\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef]{2,63}|" + Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))") + ")") + ")(?:\\:\\d{1,5})?)(\\/(?:(?:[" + "a-zA-Z0-9\u00a0-\ud7ff\uf900-\ufdcf\ufdf0-\uffef" + "\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        leftBaseline = isTablet() ? 80 : 72;
        checkDisplaySize(ApplicationLoader.applicationContext, null);
    }

    public static void addMediaToGallery(Uri uri) {
        if (uri != null) {
            try {
                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                intent.setData(uri);
                ApplicationLoader.applicationContext.sendBroadcast(intent);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public static void addMediaToGallery(String str) {
        if (str != null) {
            addMediaToGallery(Uri.fromFile(new File(str)));
        }
    }

    public static void addToClipboard(CharSequence charSequence) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", charSequence));
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public static Builder buildTTLAlert(Context context, EncryptedChat encryptedChat) {
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("MessageLifetime", C0338R.string.MessageLifetime));
        View numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        if (encryptedChat.ttl > 0 && encryptedChat.ttl < 16) {
            numberPicker.setValue(encryptedChat.ttl);
        } else if (encryptedChat.ttl == 30) {
            numberPicker.setValue(16);
        } else if (encryptedChat.ttl == 60) {
            numberPicker.setValue(17);
        } else if (encryptedChat.ttl == 3600) {
            numberPicker.setValue(18);
        } else if (encryptedChat.ttl == 86400) {
            numberPicker.setValue(19);
        } else if (encryptedChat.ttl == 604800) {
            numberPicker.setValue(20);
        } else if (encryptedChat.ttl == 0) {
            numberPicker.setValue(0);
        }
        numberPicker.setFormatter(new C03402());
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", C0338R.string.Done), new C03413(encryptedChat, numberPicker));
        return builder;
    }

    public static byte[] calcAuthKeyHash(byte[] bArr) {
        Object obj = new byte[16];
        System.arraycopy(Utilities.computeSHA1(bArr), 0, obj, 0, 16);
        return obj;
    }

    public static int[] calcDrawableColor(Drawable drawable) {
        int i = Theme.MSG_TEXT_COLOR;
        int[] iArr = new int[FLAG_TAG_BOLD];
        try {
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null) {
                    bitmap = Bitmaps.createScaledBitmap(bitmap, FLAG_TAG_BR, FLAG_TAG_BR, true);
                    if (bitmap != null) {
                        i = bitmap.getPixel(0, 0);
                        bitmap.recycle();
                    }
                }
            } else if (drawable instanceof ColorDrawable) {
                i = ((ColorDrawable) drawable).getColor();
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        double[] rgbToHsv = rgbToHsv((i >> 16) & NalUnitUtil.EXTENDED_SAR, (i >> 8) & NalUnitUtil.EXTENDED_SAR, i & NalUnitUtil.EXTENDED_SAR);
        rgbToHsv[FLAG_TAG_BR] = Math.min(1.0d, (rgbToHsv[FLAG_TAG_BR] + 0.05d) + (0.1d * (1.0d - rgbToHsv[FLAG_TAG_BR])));
        rgbToHsv[FLAG_TAG_BOLD] = Math.max(0.0d, rgbToHsv[FLAG_TAG_BOLD] * 0.65d);
        int[] hsvToRgb = hsvToRgb(rgbToHsv[0], rgbToHsv[FLAG_TAG_BR], rgbToHsv[FLAG_TAG_BOLD]);
        iArr[0] = Color.argb(102, hsvToRgb[0], hsvToRgb[FLAG_TAG_BR], hsvToRgb[FLAG_TAG_BOLD]);
        iArr[FLAG_TAG_BR] = Color.argb(136, hsvToRgb[0], hsvToRgb[FLAG_TAG_BR], hsvToRgb[FLAG_TAG_BOLD]);
        return iArr;
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        ApplicationLoader.applicationHandler.removeCallbacks(runnable);
    }

    public static void checkDisplaySize(Context context, Configuration configuration) {
        boolean z = true;
        try {
            int ceil;
            density = context.getResources().getDisplayMetrics().density;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            if (configuration.keyboard == FLAG_TAG_BR || configuration.hardKeyboardHidden != FLAG_TAG_BR) {
                z = false;
            }
            usingHardwareInput = z;
            WindowManager windowManager = (WindowManager) context.getSystemService("window");
            if (windowManager != null) {
                Display defaultDisplay = windowManager.getDefaultDisplay();
                if (defaultDisplay != null) {
                    defaultDisplay.getMetrics(displayMetrics);
                    defaultDisplay.getSize(displaySize);
                }
            }
            if (configuration.screenWidthDp != 0) {
                ceil = (int) Math.ceil((double) (((float) configuration.screenWidthDp) * density));
                if (Math.abs(displaySize.x - ceil) > 3) {
                    displaySize.x = ceil;
                }
            }
            if (configuration.screenHeightDp != 0) {
                ceil = (int) Math.ceil((double) (((float) configuration.screenHeightDp) * density));
                if (Math.abs(displaySize.y - ceil) > 3) {
                    displaySize.y = ceil;
                }
            }
            FileLog.m16e("tmessages", "display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public static void checkForCrashes(Activity activity) {
    }

    public static void checkForUpdates(Activity activity) {
    }

    public static void clearCursorDrawable(EditText editText) {
        if (editText != null) {
            try {
                Field declaredField = TextView.class.getDeclaredField("mCursorDrawableRes");
                declaredField.setAccessible(true);
                declaredField.setInt(editText, 0);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    @SuppressLint({"NewApi"})
    public static void clearDrawableAnimation(View view) {
        if (VERSION.SDK_INT >= 21 && view != null) {
            Drawable selector;
            if (view instanceof ListView) {
                selector = ((ListView) view).getSelector();
                if (selector != null) {
                    selector.setState(StateSet.NOTHING);
                    return;
                }
                return;
            }
            selector = view.getBackground();
            if (selector != null) {
                selector.setState(StateSet.NOTHING);
                selector.jumpToCurrentState();
            }
        }
    }

    public static int compare(int i, int i2) {
        return i == i2 ? 0 : i > i2 ? FLAG_TAG_BR : -1;
    }

    public static boolean copyFile(File file, File file2) {
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        Throwable e;
        FileInputStream fileInputStream2;
        FileOutputStream fileOutputStream2 = null;
        if (!file2.exists()) {
            file2.createNewFile();
        }
        try {
            fileInputStream = new FileInputStream(file);
            try {
                fileOutputStream = new FileOutputStream(file2);
            } catch (Exception e2) {
                e = e2;
                fileInputStream2 = fileInputStream;
                try {
                    FileLog.m18e("tmessages", e);
                    if (fileInputStream2 != null) {
                        fileInputStream2.close();
                    }
                    if (fileOutputStream2 != null) {
                        return false;
                    }
                    fileOutputStream2.close();
                    return false;
                } catch (Throwable th) {
                    e = th;
                    fileInputStream = fileInputStream2;
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (fileOutputStream2 != null) {
                        fileOutputStream2.close();
                    }
                    throw e;
                }
            } catch (Throwable th2) {
                e = th2;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                throw e;
            }
            try {
                fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0, fileInputStream.getChannel().size());
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                return true;
            } catch (Exception e3) {
                e = e3;
                fileOutputStream2 = fileOutputStream;
                fileInputStream2 = fileInputStream;
                FileLog.m18e("tmessages", e);
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                if (fileOutputStream2 != null) {
                    return false;
                }
                fileOutputStream2.close();
                return false;
            } catch (Throwable th3) {
                e = th3;
                fileOutputStream2 = fileOutputStream;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                throw e;
            }
        } catch (Exception e4) {
            e = e4;
            fileInputStream2 = null;
            FileLog.m18e("tmessages", e);
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            if (fileOutputStream2 != null) {
                return false;
            }
            fileOutputStream2.close();
            return false;
        } catch (Throwable th4) {
            e = th4;
            fileInputStream = null;
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
            throw e;
        }
    }

    public static boolean copyFile(InputStream inputStream, File file) {
        OutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bArr = new byte[ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT];
        while (true) {
            int read = inputStream.read(bArr);
            if (read > 0) {
                Thread.yield();
                fileOutputStream.write(bArr, 0, read);
            } else {
                fileOutputStream.close();
                return true;
            }
        }
    }

    public static CheckBoxCell createDeleteFileCheckBox(Context context) {
        CheckBoxCell checkBoxCell = new CheckBoxCell(context);
        checkBoxCell.setBackgroundResource(C0338R.drawable.list_selector);
        checkBoxCell.setText(LocaleController.getString("DeleteRelativeMediaFromMemory", C0338R.string.DeleteRelativeMediaFromMemory), TtmlNode.ANONYMOUS_REGION_ID, false, false);
        checkBoxCell.setPadding(LocaleController.isRTL ? dp(8.0f) : 0, 0, LocaleController.isRTL ? 0 : dp(8.0f), 0);
        checkBoxCell.setChecked(MoboConstants.aE, false);
        return checkBoxCell;
    }

    private static Intent createShortcutIntent(long j, boolean z) {
        Chat chat;
        User user;
        Parcelable parcelable = null;
        Intent intent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
        int i = (int) j;
        int i2 = (int) (j >> 32);
        if (i == 0) {
            intent.putExtra("encId", i2);
            EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i2));
            if (encryptedChat == null) {
                return null;
            }
            chat = null;
            user = MessagesController.getInstance().getUser(Integer.valueOf(encryptedChat.user_id));
        } else if (i > 0) {
            intent.putExtra("userId", i);
            chat = null;
            user = MessagesController.getInstance().getUser(Integer.valueOf(i));
        } else if (i >= 0) {
            return null;
        } else {
            Chat chat2 = MessagesController.getInstance().getChat(Integer.valueOf(-i));
            intent.putExtra("chatId", -i);
            chat = chat2;
            user = null;
        }
        if (user == null && chat == null) {
            return null;
        }
        String formatName;
        TLObject tLObject;
        if (user != null) {
            formatName = ContactsController.formatName(user.first_name, user.last_name);
            if (user.photo != null) {
                tLObject = user.photo.photo_small;
            }
            tLObject = null;
        } else {
            formatName = chat.title;
            if (chat.photo != null) {
                tLObject = chat.photo.photo_small;
            }
            tLObject = null;
        }
        intent.setAction("com.tmessages.openchat" + j);
        intent.addFlags(67108864);
        Intent intent2 = new Intent();
        intent2.putExtra("android.intent.extra.shortcut.INTENT", intent);
        intent2.putExtra("android.intent.extra.shortcut.NAME", formatName);
        intent2.putExtra("duplicate", false);
        if (!z) {
            if (tLObject != null) {
                try {
                    Bitmap createBitmap;
                    Bitmap decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(tLObject, true).toString());
                    if (decodeFile != null) {
                        i = dp(58.0f);
                        createBitmap = Bitmap.createBitmap(i, i, Config.ARGB_8888);
                        createBitmap.eraseColor(0);
                        Canvas canvas = new Canvas(createBitmap);
                        Shader bitmapShader = new BitmapShader(decodeFile, TileMode.CLAMP, TileMode.CLAMP);
                        if (roundPaint == null) {
                            roundPaint = new Paint(FLAG_TAG_BR);
                            bitmapRect = new RectF();
                        }
                        float width = ((float) i) / ((float) decodeFile.getWidth());
                        canvas.save();
                        canvas.scale(width, width);
                        roundPaint.setShader(bitmapShader);
                        bitmapRect.set(0.0f, 0.0f, (float) decodeFile.getWidth(), (float) decodeFile.getHeight());
                        canvas.drawRoundRect(bitmapRect, (float) decodeFile.getWidth(), (float) decodeFile.getHeight(), roundPaint);
                        canvas.restore();
                        Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(C0338R.drawable.book_logo);
                        int dp = dp(15.0f);
                        int dp2 = (i - dp) - dp(2.0f);
                        i = (i - dp) - dp(2.0f);
                        drawable.setBounds(dp2, i, dp2 + dp, dp + i);
                        drawable.draw(canvas);
                        try {
                            canvas.setBitmap(null);
                        } catch (Exception e) {
                        }
                    } else {
                        createBitmap = decodeFile;
                    }
                    parcelable = createBitmap;
                } catch (Throwable th) {
                    FileLog.m18e("tmessages", th);
                }
            }
            if (parcelable != null) {
                intent2.putExtra("android.intent.extra.shortcut.ICON", parcelable);
            } else if (user != null) {
                if (user.bot) {
                    intent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, C0338R.drawable.book_bot));
                } else {
                    intent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, C0338R.drawable.book_user));
                }
            } else if (chat != null) {
                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                    intent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, C0338R.drawable.book_group));
                } else {
                    intent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, C0338R.drawable.book_channel));
                }
            }
        }
        return intent2;
    }

    public static byte[] decodeQuotedPrintable(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        while (i < bArr.length) {
            byte b = bArr[i];
            if (b == 61) {
                i += FLAG_TAG_BR;
                try {
                    int digit = Character.digit((char) bArr[i], 16);
                    i += FLAG_TAG_BR;
                    byteArrayOutputStream.write((char) ((digit << FLAG_TAG_COLOR) + Character.digit((char) bArr[i], 16)));
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                    return null;
                }
            }
            byteArrayOutputStream.write(b);
            i += FLAG_TAG_BR;
        }
        byte[] toByteArray = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
            return toByteArray;
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
            return toByteArray;
        }
    }

    public static int dp(float f) {
        return f == 0.0f ? 0 : (int) Math.ceil((double) (density * f));
    }

    public static float dpf2(float f) {
        return f == 0.0f ? 0.0f : density * f;
    }

    public static String formatFileSize(long j) {
        Object[] objArr;
        if (j < 1024) {
            objArr = new Object[FLAG_TAG_BR];
            objArr[0] = Long.valueOf(j);
            return String.format("%d B", objArr);
        } else if (j < 1048576) {
            objArr = new Object[FLAG_TAG_BR];
            objArr[0] = Float.valueOf(((float) j) / 1024.0f);
            return String.format("%.1f KB", objArr);
        } else if (j < 1073741824) {
            objArr = new Object[FLAG_TAG_BR];
            objArr[0] = Float.valueOf((((float) j) / 1024.0f) / 1024.0f);
            return String.format("%.1f MB", objArr);
        } else {
            objArr = new Object[FLAG_TAG_BR];
            objArr[0] = Float.valueOf(((((float) j) / 1024.0f) / 1024.0f) / 1024.0f);
            return String.format("%.1f GB", objArr);
        }
    }

    public static String formatTTLString(int i) {
        if (i < 60) {
            return LocaleController.formatPluralString("Seconds", i);
        }
        if (i < 3600) {
            return LocaleController.formatPluralString("Minutes", i / 60);
        }
        if (i < 86400) {
            return LocaleController.formatPluralString("Hours", (i / 60) / 60);
        }
        if (i < 604800) {
            return LocaleController.formatPluralString("Days", ((i / 60) / 60) / 24);
        }
        int i2 = ((i / 60) / 60) / 24;
        if (i % FLAG_TAG_ALL == 0) {
            return LocaleController.formatPluralString("Weeks", i2 / FLAG_TAG_ALL);
        }
        Object[] objArr = new Object[FLAG_TAG_BOLD];
        objArr[0] = LocaleController.formatPluralString("Weeks", i2 / FLAG_TAG_ALL);
        objArr[FLAG_TAG_BR] = LocaleController.formatPluralString("Days", i2 % FLAG_TAG_ALL);
        return String.format("%s %s", objArr);
    }

    public static File generatePictureInCachePath() {
        try {
            return new File(getCacheDir(), "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".jpg");
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return null;
        }
    }

    public static File generatePicturePath() {
        try {
            return new File(getAlbumDir(), "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".jpg");
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return null;
        }
    }

    public static CharSequence generateSearchName(String str, String str2, String str3) {
        if (str == null && str2 == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (str == null || str.length() == 0) {
            str = str2;
        } else if (!(str2 == null || str2.length() == 0)) {
            str = str + " " + str2;
        }
        String trim = str.trim();
        String str4 = " " + trim.toLowerCase();
        Object[] objArr = new Object[FLAG_TAG_BR];
        objArr[0] = Integer.valueOf(AdvanceTheme.au & -1);
        String format = String.format("#%08X", objArr);
        int i = 0;
        while (true) {
            int indexOf = str4.indexOf(" " + str3, i);
            if (indexOf == -1) {
                break;
            }
            int i2 = indexOf - (indexOf == 0 ? 0 : FLAG_TAG_BR);
            int length = ((indexOf == 0 ? 0 : FLAG_TAG_BR) + str3.length()) + i2;
            if (i != 0 && i != i2 + FLAG_TAG_BR) {
                spannableStringBuilder.append(trim.substring(i, i2));
            } else if (i == 0 && i2 != 0) {
                spannableStringBuilder.append(trim.substring(0, i2));
            }
            String substring = trim.substring(i2, length);
            if (substring.startsWith(" ")) {
                spannableStringBuilder.append(" ");
            }
            substring = substring.trim();
            if (ThemeUtil.m2490b()) {
                spannableStringBuilder.append(replaceTags("<c" + format + ">" + substring + "</c>"));
            } else {
                spannableStringBuilder.append(replaceTags("<c#ff4d83b3>" + substring + "</c>"));
            }
            i = length;
        }
        if (!(i == -1 || i == trim.length())) {
            spannableStringBuilder.append(trim.substring(i, trim.length()));
        }
        return spannableStringBuilder;
    }

    public static File generateVideoPath() {
        try {
            return new File(getAlbumDir(), "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4");
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return null;
        }
    }

    private static File getAlbumDir() {
        if (VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            return FileLoader.getInstance().getDirectory(FLAG_TAG_COLOR);
        }
        File file;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
            if (!(file.mkdirs() || file.exists())) {
                FileLog.m15d("tmessages", "failed to create directory");
                return null;
            }
        }
        FileLog.m15d("tmessages", "External storage is not mounted READ/WRITE.");
        file = null;
        return file;
    }

    public static File getCacheDir() {
        File externalCacheDir;
        String str = null;
        try {
            str = Environment.getExternalStorageState();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        if (str == null || str.startsWith("mounted")) {
            try {
                externalCacheDir = ApplicationLoader.applicationContext.getExternalCacheDir();
                if (externalCacheDir != null) {
                    return externalCacheDir;
                }
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
        }
        try {
            externalCacheDir = ApplicationLoader.applicationContext.getCacheDir();
            if (externalCacheDir != null) {
                return externalCacheDir;
            }
        } catch (Throwable e22) {
            FileLog.m18e("tmessages", e22);
        }
        return new File(TtmlNode.ANONYMOUS_REGION_ID);
    }

    public static String getDataColumn(Context context, Uri uri, String str, String[] strArr) {
        Cursor query;
        Throwable e;
        String str2 = "_data";
        String[] strArr2 = new String[FLAG_TAG_BR];
        strArr2[0] = "_data";
        try {
            query = context.getContentResolver().query(uri, strArr2, str, strArr, null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        str2 = query.getString(query.getColumnIndexOrThrow("_data"));
                        if (str2.startsWith("content://") || !(str2.startsWith("/") || str2.startsWith("file://"))) {
                            if (query != null) {
                                query.close();
                            }
                            return null;
                        } else if (query == null) {
                            return str2;
                        } else {
                            query.close();
                            return str2;
                        }
                    }
                } catch (Exception e2) {
                    e = e2;
                    try {
                        FileLog.m18e("tmessages", e);
                        if (query != null) {
                            query.close();
                        }
                        return null;
                    } catch (Throwable th) {
                        e = th;
                        if (query != null) {
                            query.close();
                        }
                        throw e;
                    }
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e3) {
            e = e3;
            query = null;
            FileLog.m18e("tmessages", e);
            if (query != null) {
                query.close();
            }
            return null;
        } catch (Throwable th2) {
            e = th2;
            query = null;
            if (query != null) {
                query.close();
            }
            throw e;
        }
        return null;
    }

    public static int getMinTabletSide() {
        int i;
        if (isSmallTablet()) {
            int min = Math.min(displaySize.x, displaySize.y);
            int max = Math.max(displaySize.x, displaySize.y);
            i = (max * 35) / 100;
            if (i < dp(320.0f)) {
                i = dp(320.0f);
            }
            return Math.min(min, max - i);
        }
        min = Math.min(displaySize.x, displaySize.y);
        i = (min * 35) / 100;
        if (i < dp(320.0f)) {
            i = dp(320.0f);
        }
        return min - i;
    }

    public static int getMyLayerVersion(int i) {
        return SupportMenu.USER_MASK & i;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.SuppressLint({"NewApi"})
    public static java.lang.String getPath(android.net.Uri r7) {
        /*
        r3 = 1;
        r1 = 0;
        r0 = 0;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0103 }
        r4 = 19;
        if (r2 < r4) goto L_0x0051;
    L_0x0009:
        r2 = r3;
    L_0x000a:
        if (r2 == 0) goto L_0x00d9;
    L_0x000c:
        r2 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0103 }
        r2 = android.provider.DocumentsContract.isDocumentUri(r2, r7);	 Catch:{ Exception -> 0x0103 }
        if (r2 == 0) goto L_0x00d9;
    L_0x0014:
        r2 = isExternalStorageDocument(r7);	 Catch:{ Exception -> 0x0103 }
        if (r2 == 0) goto L_0x0053;
    L_0x001a:
        r1 = android.provider.DocumentsContract.getDocumentId(r7);	 Catch:{ Exception -> 0x0103 }
        r2 = ":";
        r1 = r1.split(r2);	 Catch:{ Exception -> 0x0103 }
        r2 = 0;
        r2 = r1[r2];	 Catch:{ Exception -> 0x0103 }
        r3 = "primary";
        r2 = r3.equalsIgnoreCase(r2);	 Catch:{ Exception -> 0x0103 }
        if (r2 == 0) goto L_0x0050;
    L_0x0031:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0103 }
        r2.<init>();	 Catch:{ Exception -> 0x0103 }
        r3 = android.os.Environment.getExternalStorageDirectory();	 Catch:{ Exception -> 0x0103 }
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x0103 }
        r3 = "/";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x0103 }
        r3 = 1;
        r1 = r1[r3];	 Catch:{ Exception -> 0x0103 }
        r1 = r2.append(r1);	 Catch:{ Exception -> 0x0103 }
        r0 = r1.toString();	 Catch:{ Exception -> 0x0103 }
    L_0x0050:
        return r0;
    L_0x0051:
        r2 = r1;
        goto L_0x000a;
    L_0x0053:
        r2 = isDownloadsDocument(r7);	 Catch:{ Exception -> 0x0103 }
        if (r2 == 0) goto L_0x0079;
    L_0x0059:
        r1 = android.provider.DocumentsContract.getDocumentId(r7);	 Catch:{ Exception -> 0x0103 }
        r2 = "content://downloads/public_downloads";
        r2 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0103 }
        r1 = java.lang.Long.valueOf(r1);	 Catch:{ Exception -> 0x0103 }
        r4 = r1.longValue();	 Catch:{ Exception -> 0x0103 }
        r1 = android.content.ContentUris.withAppendedId(r2, r4);	 Catch:{ Exception -> 0x0103 }
        r2 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0103 }
        r3 = 0;
        r4 = 0;
        r0 = getDataColumn(r2, r1, r3, r4);	 Catch:{ Exception -> 0x0103 }
        goto L_0x0050;
    L_0x0079:
        r2 = isMediaDocument(r7);	 Catch:{ Exception -> 0x0103 }
        if (r2 == 0) goto L_0x0050;
    L_0x007f:
        r2 = android.provider.DocumentsContract.getDocumentId(r7);	 Catch:{ Exception -> 0x0103 }
        r4 = ":";
        r4 = r2.split(r4);	 Catch:{ Exception -> 0x0103 }
        r2 = 0;
        r5 = r4[r2];	 Catch:{ Exception -> 0x0103 }
        r2 = -1;
        r6 = r5.hashCode();	 Catch:{ Exception -> 0x0103 }
        switch(r6) {
            case 93166550: goto L_0x00c5;
            case 100313435: goto L_0x00b0;
            case 112202875: goto L_0x00ba;
            default: goto L_0x0095;
        };	 Catch:{ Exception -> 0x0103 }
    L_0x0095:
        r1 = r2;
    L_0x0096:
        switch(r1) {
            case 0: goto L_0x00d0;
            case 1: goto L_0x00d3;
            case 2: goto L_0x00d6;
            default: goto L_0x0099;
        };	 Catch:{ Exception -> 0x0103 }
    L_0x0099:
        r1 = r0;
    L_0x009a:
        r2 = "_id=?";
        r2 = 1;
        r2 = new java.lang.String[r2];	 Catch:{ Exception -> 0x0103 }
        r3 = 0;
        r5 = 1;
        r4 = r4[r5];	 Catch:{ Exception -> 0x0103 }
        r2[r3] = r4;	 Catch:{ Exception -> 0x0103 }
        r3 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0103 }
        r4 = "_id=?";
        r0 = getDataColumn(r3, r1, r4, r2);	 Catch:{ Exception -> 0x0103 }
        goto L_0x0050;
    L_0x00b0:
        r3 = "image";
        r3 = r5.equals(r3);	 Catch:{ Exception -> 0x0103 }
        if (r3 == 0) goto L_0x0095;
    L_0x00b9:
        goto L_0x0096;
    L_0x00ba:
        r1 = "video";
        r1 = r5.equals(r1);	 Catch:{ Exception -> 0x0103 }
        if (r1 == 0) goto L_0x0095;
    L_0x00c3:
        r1 = r3;
        goto L_0x0096;
    L_0x00c5:
        r1 = "audio";
        r1 = r5.equals(r1);	 Catch:{ Exception -> 0x0103 }
        if (r1 == 0) goto L_0x0095;
    L_0x00ce:
        r1 = 2;
        goto L_0x0096;
    L_0x00d0:
        r1 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x0103 }
        goto L_0x009a;
    L_0x00d3:
        r1 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x0103 }
        goto L_0x009a;
    L_0x00d6:
        r1 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x0103 }
        goto L_0x009a;
    L_0x00d9:
        r1 = "content";
        r2 = r7.getScheme();	 Catch:{ Exception -> 0x0103 }
        r1 = r1.equalsIgnoreCase(r2);	 Catch:{ Exception -> 0x0103 }
        if (r1 == 0) goto L_0x00f0;
    L_0x00e6:
        r1 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0103 }
        r2 = 0;
        r3 = 0;
        r0 = getDataColumn(r1, r7, r2, r3);	 Catch:{ Exception -> 0x0103 }
        goto L_0x0050;
    L_0x00f0:
        r1 = "file";
        r2 = r7.getScheme();	 Catch:{ Exception -> 0x0103 }
        r1 = r1.equalsIgnoreCase(r2);	 Catch:{ Exception -> 0x0103 }
        if (r1 == 0) goto L_0x0050;
    L_0x00fd:
        r0 = r7.getPath();	 Catch:{ Exception -> 0x0103 }
        goto L_0x0050;
    L_0x0103:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x0050;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.AndroidUtilities.getPath(android.net.Uri):java.lang.String");
    }

    public static int getPeerLayerVersion(int i) {
        return (i >> 16) & SupportMenu.USER_MASK;
    }

    public static int getPhotoSize() {
        if (photoSize == null) {
            if (VERSION.SDK_INT >= 16) {
                photoSize = Integer.valueOf(1280);
            } else {
                photoSize = Integer.valueOf(800);
            }
        }
        return photoSize.intValue();
    }

    public static float getPixelsInCM(float f, boolean z) {
        return (z ? displayMetrics.xdpi : displayMetrics.ydpi) * (f / 2.54f);
    }

    public static Point getRealScreenSize() {
        Point point = new Point();
        try {
            WindowManager windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            if (VERSION.SDK_INT >= 17) {
                windowManager.getDefaultDisplay().getRealSize(point);
            } else {
                try {
                    point.set(((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue(), ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue());
                } catch (Throwable e) {
                    point.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    FileLog.m18e("tmessages", e);
                }
            }
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
        return point;
    }

    public static CharSequence getTrimmedString(CharSequence charSequence) {
        if (!(charSequence == null || charSequence.length() == 0)) {
            while (charSequence.length() > 0 && (charSequence.charAt(0) == '\n' || charSequence.charAt(0) == ' ')) {
                charSequence = charSequence.subSequence(FLAG_TAG_BR, charSequence.length());
            }
            while (charSequence.length() > 0 && (charSequence.charAt(charSequence.length() - 1) == '\n' || charSequence.charAt(charSequence.length() - 1) == ' ')) {
                charSequence = charSequence.subSequence(0, charSequence.length() - 1);
            }
        }
        return charSequence;
    }

    public static Typeface getTypeface(String str) {
        Typeface typeface;
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(str)) {
                try {
                    typefaceCache.put(str, Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), str));
                } catch (Exception e) {
                    FileLog.m16e("Typefaces", "Could not get typeface '" + str + "' because " + e.getMessage());
                    typeface = null;
                }
            }
            typeface = (Typeface) typefaceCache.get(str);
        }
        return typeface;
    }

    public static Typeface getTypefaceFromFile(String str) {
        Typeface typeface;
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(str)) {
                try {
                    typefaceCache.put(str, Typeface.createFromFile(str));
                } catch (Exception e) {
                    FileLog.m16e("Typefaces", "Could not get typeface '" + str + "' because " + e.getMessage());
                    typeface = null;
                }
            }
            typeface = (Typeface) typefaceCache.get(str);
        }
        return typeface;
    }

    public static int getViewInset(View view) {
        if (view == null || VERSION.SDK_INT < 21 || view.getHeight() == displaySize.y || view.getHeight() == displaySize.y - statusBarHeight) {
            return 0;
        }
        try {
            if (mAttachInfoField == null) {
                mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
                mAttachInfoField.setAccessible(true);
            }
            Object obj = mAttachInfoField.get(view);
            if (obj != null) {
                if (mStableInsetsField == null) {
                    mStableInsetsField = obj.getClass().getDeclaredField("mStableInsets");
                    mStableInsetsField.setAccessible(true);
                }
                return ((Rect) mStableInsetsField.get(obj)).bottom;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return 0;
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            try {
                InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    private static int[] hsvToRgb(double d, double d2, double d3) {
        double floor = (double) ((int) Math.floor(6.0d * d));
        double d4 = (6.0d * d) - floor;
        double d5 = (1.0d - d2) * d3;
        double d6 = (1.0d - (d4 * d2)) * d3;
        d4 = (1.0d - ((1.0d - d4) * d2)) * d3;
        double d7;
        switch (((int) floor) % 6) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                d6 = d3;
                d3 = d5;
                d5 = d4;
                break;
            case FLAG_TAG_BR /*1*/:
                d7 = d5;
                d5 = d3;
                d3 = d7;
                break;
            case FLAG_TAG_BOLD /*2*/:
                d6 = d5;
                d5 = d3;
                d3 = d4;
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                d7 = d6;
                d6 = d5;
                d5 = d7;
                break;
            case FLAG_TAG_COLOR /*4*/:
                d6 = d4;
                break;
            case VideoPlayer.STATE_ENDED /*5*/:
                d7 = d6;
                d6 = d3;
                d3 = d7;
                break;
            default:
                d3 = 0.0d;
                d5 = 0.0d;
                d6 = 0.0d;
                break;
        }
        return new int[]{(int) (d6 * 255.0d), (int) (d5 * 255.0d), (int) (255.0d * d3)};
    }

    public static void installShortcut(long j) {
        try {
            Intent createShortcutIntent = createShortcutIntent(j, false);
            createShortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            ApplicationLoader.applicationContext.sendBroadcast(createShortcutIntent);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isGoogleMapsInstalled(BaseFragment baseFragment) {
        try {
            ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (NameNotFoundException e) {
            if (baseFragment.getParentActivity() == null) {
                return false;
            }
            Builder builder = new Builder(baseFragment.getParentActivity());
            builder.setMessage("Install Google Maps?");
            builder.setCancelable(true);
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C03391(baseFragment));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            baseFragment.showDialog(builder.create());
            return false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isInternalUri(android.net.Uri r4) {
        /*
        r2 = 0;
        r0 = r4.getPath();
        if (r0 != 0) goto L_0x0009;
    L_0x0007:
        return r2;
    L_0x0008:
        r0 = r1;
    L_0x0009:
        r1 = com.hanista.mobogram.messenger.Utilities.readlink(r0);
        if (r1 == 0) goto L_0x0015;
    L_0x000f:
        r3 = r1.equals(r0);
        if (r3 == 0) goto L_0x0008;
    L_0x0015:
        if (r0 == 0) goto L_0x0023;
    L_0x0017:
        r1 = new java.io.File;	 Catch:{ Exception -> 0x0053 }
        r1.<init>(r0);	 Catch:{ Exception -> 0x0053 }
        r1 = r1.getCanonicalPath();	 Catch:{ Exception -> 0x0053 }
        if (r1 == 0) goto L_0x0023;
    L_0x0022:
        r0 = r1;
    L_0x0023:
        if (r0 == 0) goto L_0x005e;
    L_0x0025:
        r0 = r0.toLowerCase();
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r3 = "/data/data/";
        r1 = r1.append(r3);
        r3 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;
        r3 = r3.getPackageName();
        r1 = r1.append(r3);
        r3 = "/files";
        r1 = r1.append(r3);
        r1 = r1.toString();
        r0 = r0.contains(r1);
        if (r0 == 0) goto L_0x005e;
    L_0x0050:
        r0 = 1;
    L_0x0051:
        r2 = r0;
        goto L_0x0007;
    L_0x0053:
        r1 = move-exception;
        r1 = "/./";
        r3 = "/";
        r0.replace(r1, r3);
        goto L_0x0023;
    L_0x005e:
        r0 = r2;
        goto L_0x0051;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.AndroidUtilities.isInternalUri(android.net.Uri):boolean");
    }

    public static boolean isKeyboardShowed(View view) {
        if (view == null) {
            return false;
        }
        try {
            return ((InputMethodManager) view.getContext().getSystemService("input_method")).isActive(view);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return false;
        }
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isSmallTablet() {
        return ((float) Math.min(displaySize.x, displaySize.y)) / density <= 700.0f;
    }

    public static boolean isTablet() {
        if (isTablet == null) {
            boolean z = MoboConstants.f1324Q && ApplicationLoader.applicationContext.getResources().getBoolean(C0338R.bool.isTablet);
            isTablet = Boolean.valueOf(z);
        }
        return isTablet.booleanValue();
    }

    public static boolean isWaitingForCall() {
        boolean z;
        synchronized (callLock) {
            z = waitingForCall;
        }
        return z;
    }

    public static boolean isWaitingForSms() {
        boolean z;
        synchronized (smsLock) {
            z = waitingForSms;
        }
        return z;
    }

    public static void lockOrientation(Activity activity) {
        if (activity != null && prevOrientation == -10) {
            try {
                prevOrientation = activity.getRequestedOrientation();
                WindowManager windowManager = (WindowManager) activity.getSystemService("window");
                if (windowManager != null && windowManager.getDefaultDisplay() != null) {
                    int rotation = windowManager.getDefaultDisplay().getRotation();
                    int i = activity.getResources().getConfiguration().orientation;
                    if (rotation == 3) {
                        if (i == FLAG_TAG_BR) {
                            activity.setRequestedOrientation(FLAG_TAG_BR);
                        } else {
                            activity.setRequestedOrientation(8);
                        }
                    } else if (rotation == FLAG_TAG_BR) {
                        if (i == FLAG_TAG_BR) {
                            activity.setRequestedOrientation(9);
                        } else {
                            activity.setRequestedOrientation(0);
                        }
                    } else if (rotation == 0) {
                        if (i == FLAG_TAG_BOLD) {
                            activity.setRequestedOrientation(0);
                        } else {
                            activity.setRequestedOrientation(FLAG_TAG_BR);
                        }
                    } else if (i == FLAG_TAG_BOLD) {
                        activity.setRequestedOrientation(8);
                    } else {
                        activity.setRequestedOrientation(9);
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public static long makeBroadcastId(int i) {
        return 4294967296L | (((long) i) & 4294967295L);
    }

    public static boolean needShowPasscode(boolean z) {
        boolean isWasInBackground = ForegroundDetector.getInstance().isWasInBackground(z);
        if (z) {
            ForegroundDetector.getInstance().resetBackgroundVar();
        }
        return UserConfig.passcodeHash.length() > 0 && isWasInBackground && (UserConfig.appLocked || !(UserConfig.autoLockIn == 0 || UserConfig.lastPauseTime == 0 || UserConfig.appLocked || UserConfig.lastPauseTime + UserConfig.autoLockIn > ConnectionsManager.getInstance().getCurrentTime()));
    }

    public static void openForView(MessageObject messageObject, Activity activity) {
        String fileName = messageObject.getFileName();
        File file = (messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0) ? null : new File(messageObject.messageOwner.attachPath);
        File pathToMessage = (file == null || !file.exists()) ? FileLoader.getPathToMessage(messageObject.messageOwner) : file;
        if (pathToMessage != null && pathToMessage.exists()) {
            String mimeTypeFromExtension;
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setFlags(FLAG_TAG_BR);
            MimeTypeMap singleton = MimeTypeMap.getSingleton();
            int lastIndexOf = fileName.lastIndexOf(46);
            if (lastIndexOf != -1) {
                mimeTypeFromExtension = singleton.getMimeTypeFromExtension(fileName.substring(lastIndexOf + FLAG_TAG_BR).toLowerCase());
                if (mimeTypeFromExtension == null) {
                    if (messageObject.type == 9 || messageObject.type == 0) {
                        mimeTypeFromExtension = messageObject.getDocument().mime_type;
                    }
                    if (mimeTypeFromExtension == null || mimeTypeFromExtension.length() == 0) {
                        mimeTypeFromExtension = null;
                    }
                }
            } else {
                mimeTypeFromExtension = null;
            }
            if (VERSION.SDK_INT >= 24) {
                intent.setDataAndType(FileProvider.getUriForFile(activity, "com.hanista.mobogram.provider", pathToMessage), mimeTypeFromExtension != null ? mimeTypeFromExtension : "text/plain");
            } else {
                intent.setDataAndType(Uri.fromFile(pathToMessage), mimeTypeFromExtension != null ? mimeTypeFromExtension : "text/plain");
            }
            if (mimeTypeFromExtension != null) {
                try {
                    activity.startActivityForResult(intent, 500);
                    return;
                } catch (Exception e) {
                    if (VERSION.SDK_INT >= 24) {
                        intent.setDataAndType(FileProvider.getUriForFile(activity, "com.hanista.mobogram.provider", pathToMessage), "text/plain");
                    } else {
                        intent.setDataAndType(Uri.fromFile(pathToMessage), "text/plain");
                    }
                    activity.startActivityForResult(intent, 500);
                    return;
                }
            }
            activity.startActivityForResult(intent, 500);
        }
    }

    public static void removeAdjustResize(Activity activity, int i) {
        if (activity != null && !isTablet() && adjustOwnerClassGuid == i) {
            activity.getWindow().setSoftInputMode(32);
        }
    }

    public static SpannableStringBuilder replaceTags(String str) {
        return replaceTags(str, FLAG_TAG_ALL);
    }

    public static SpannableStringBuilder replaceTags(String str, int i) {
        int i2 = 0;
        try {
            int indexOf;
            int indexOf2;
            CharSequence stringBuilder = new StringBuilder(str);
            if ((i & FLAG_TAG_BR) != 0) {
                while (true) {
                    indexOf = stringBuilder.indexOf("<br>");
                    if (indexOf != -1) {
                        stringBuilder.replace(indexOf, indexOf + FLAG_TAG_COLOR, "\n");
                    } else {
                        while (true) {
                            break;
                            stringBuilder.replace(indexOf, indexOf + 5, "\n");
                        }
                    }
                }
                indexOf = stringBuilder.indexOf("<br/>");
                if (indexOf == -1) {
                    break;
                }
                stringBuilder.replace(indexOf, indexOf + 5, "\n");
            }
            ArrayList arrayList = new ArrayList();
            if ((i & FLAG_TAG_BOLD) != 0) {
                while (true) {
                    indexOf2 = stringBuilder.indexOf("<b>");
                    if (indexOf2 == -1) {
                        break;
                    }
                    stringBuilder.replace(indexOf2, indexOf2 + 3, TtmlNode.ANONYMOUS_REGION_ID);
                    indexOf = stringBuilder.indexOf("</b>");
                    if (indexOf == -1) {
                        indexOf = stringBuilder.indexOf("<b>");
                    }
                    stringBuilder.replace(indexOf, indexOf + FLAG_TAG_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
                    arrayList.add(Integer.valueOf(indexOf2));
                    arrayList.add(Integer.valueOf(indexOf));
                }
            }
            ArrayList arrayList2 = new ArrayList();
            if ((i & FLAG_TAG_COLOR) != 0) {
                while (true) {
                    indexOf = stringBuilder.indexOf("<c#");
                    if (indexOf == -1) {
                        break;
                    }
                    stringBuilder.replace(indexOf, indexOf + FLAG_TAG_BOLD, TtmlNode.ANONYMOUS_REGION_ID);
                    indexOf2 = stringBuilder.indexOf(">", indexOf);
                    int parseColor = Color.parseColor(stringBuilder.substring(indexOf, indexOf2));
                    stringBuilder.replace(indexOf, indexOf2 + FLAG_TAG_BR, TtmlNode.ANONYMOUS_REGION_ID);
                    indexOf2 = stringBuilder.indexOf("</c>");
                    stringBuilder.replace(indexOf2, indexOf2 + FLAG_TAG_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
                    arrayList2.add(Integer.valueOf(indexOf));
                    arrayList2.add(Integer.valueOf(indexOf2));
                    arrayList2.add(Integer.valueOf(parseColor));
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (int i3 = 0; i3 < arrayList.size() / FLAG_TAG_BOLD; i3 += FLAG_TAG_BR) {
                spannableStringBuilder.setSpan(new TypefaceSpan(FontUtil.m1176a().m1160c()), ((Integer) arrayList.get(i3 * FLAG_TAG_BOLD)).intValue(), ((Integer) arrayList.get((i3 * FLAG_TAG_BOLD) + FLAG_TAG_BR)).intValue(), 33);
            }
            while (i2 < arrayList2.size() / 3) {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(((Integer) arrayList2.get((i2 * 3) + FLAG_TAG_BOLD)).intValue()), ((Integer) arrayList2.get(i2 * 3)).intValue(), ((Integer) arrayList2.get((i2 * 3) + FLAG_TAG_BR)).intValue(), 33);
                i2 += FLAG_TAG_BR;
            }
            return spannableStringBuilder;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return new SpannableStringBuilder(str);
        }
    }

    public static void requestAdjustResize(Activity activity, int i) {
        if (activity != null && !isTablet()) {
            activity.getWindow().setSoftInputMode(16);
            adjustOwnerClassGuid = i;
        }
    }

    private static double[] rgbToHsv(int i, int i2, int i3) {
        double d;
        double d2 = ((double) i) / 255.0d;
        double d3 = ((double) i2) / 255.0d;
        double d4 = ((double) i3) / 255.0d;
        double d5 = (d2 <= d3 || d2 <= d4) ? d3 > d4 ? d3 : d4 : d2;
        double d6 = (d2 >= d3 || d2 >= d4) ? d3 < d4 ? d3 : d4 : d2;
        double d7 = d5 - d6;
        double d8 = d5 == 0.0d ? 0.0d : d7 / d5;
        if (d5 == d6) {
            d = 0.0d;
        } else {
            if (d2 <= d3 || d2 <= d4) {
                d = d3 > d4 ? ((d4 - d2) / d7) + 2.0d : ((d2 - d3) / d7) + 4.0d;
            } else {
                d = ((double) (d3 < d4 ? 6 : 0)) + ((d3 - d4) / d7);
            }
            d /= 6.0d;
        }
        return new double[]{d, d8, d5};
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long j) {
        if (j == 0) {
            ApplicationLoader.applicationHandler.post(runnable);
        } else {
            ApplicationLoader.applicationHandler.postDelayed(runnable, j);
        }
    }

    public static void setListViewEdgeEffectColor(AbsListView absListView, int i) {
        if (VERSION.SDK_INT >= 21) {
            try {
                Field declaredField = AbsListView.class.getDeclaredField("mEdgeGlowTop");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect = (EdgeEffect) declaredField.get(absListView);
                if (edgeEffect != null) {
                    edgeEffect.setColor(i);
                }
                declaredField = AbsListView.class.getDeclaredField("mEdgeGlowBottom");
                declaredField.setAccessible(true);
                edgeEffect = (EdgeEffect) declaredField.get(absListView);
                if (edgeEffect != null) {
                    edgeEffect.setColor(i);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public static int setMyLayerVersion(int i, int i2) {
        return (SupportMenu.CATEGORY_MASK & i) | i2;
    }

    public static int setPeerLayerVersion(int i, int i2) {
        return (SupportMenu.USER_MASK & i) | (i2 << 16);
    }

    public static void setProgressBarAnimationDuration(ProgressBar progressBar, int i) {
        if (progressBar != null) {
            try {
                Field declaredField = ProgressBar.class.getDeclaredField("mDuration");
                declaredField.setAccessible(true);
                declaredField.setInt(progressBar, i);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public static void setRectToRect(Matrix matrix, RectF rectF, RectF rectF2, int i, ScaleToFit scaleToFit) {
        float height;
        float width;
        if (i == 90 || i == 270) {
            height = rectF2.height() / rectF.width();
            width = rectF2.width() / rectF.height();
        } else {
            height = rectF2.width() / rectF.width();
            width = rectF2.height() / rectF.height();
        }
        if (scaleToFit != ScaleToFit.FILL) {
            if (height > width) {
                height = width;
            } else {
                width = height;
            }
        }
        float f = (-rectF.left) * height;
        float f2 = (-rectF.top) * width;
        matrix.setTranslate(rectF2.left, rectF2.top);
        if (i == 90) {
            matrix.preRotate(90.0f);
            matrix.preTranslate(0.0f, -rectF2.width());
        } else if (i == 180) {
            matrix.preRotate(BitmapDescriptorFactory.HUE_CYAN);
            matrix.preTranslate(-rectF2.width(), -rectF2.height());
        } else if (i == 270) {
            matrix.preRotate(BitmapDescriptorFactory.HUE_VIOLET);
            matrix.preTranslate(-rectF2.height(), 0.0f);
        }
        matrix.preScale(height, width);
        matrix.preTranslate(f, f2);
    }

    public static void setWaitingForCall(boolean z) {
        synchronized (callLock) {
            waitingForCall = z;
        }
    }

    public static void setWaitingForSms(boolean z) {
        synchronized (smsLock) {
            waitingForSms = z;
        }
    }

    public static void shakeView(View view, float f, int i) {
        if (i == 6) {
            view.setTranslationX(0.0f);
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[FLAG_TAG_BR];
        float[] fArr = new float[FLAG_TAG_BR];
        fArr[0] = (float) dp(f);
        animatorArr[0] = ObjectAnimator.ofFloat(view, "translationX", fArr);
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(50);
        animatorSet.addListener(new C03424(view, i, f));
        animatorSet.start();
    }

    public static void showKeyboard(View view) {
        if (view != null) {
            try {
                ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, FLAG_TAG_BR);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public static void uninstallShortcut(long j) {
        try {
            Intent createShortcutIntent = createShortcutIntent(j, true);
            createShortcutIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            ApplicationLoader.applicationContext.sendBroadcast(createShortcutIntent);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public static void unlockOrientation(Activity activity) {
        if (activity != null) {
            try {
                if (prevOrientation != -10) {
                    activity.setRequestedOrientation(prevOrientation);
                    prevOrientation = -10;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public static void unregisterUpdates() {
    }
}
