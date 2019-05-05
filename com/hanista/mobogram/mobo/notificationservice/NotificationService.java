package com.hanista.mobogram.mobo.notificationservice;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;
import com.google.gson.Gson;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.LaunchActivity;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class NotificationService extends IntentService {
    public NotificationService() {
        super("NotificationService");
    }

    private int m1951a(Integer num) {
        if (num == null) {
            return m1955b();
        }
        switch (num.intValue()) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return m1955b();
            case VideoPlayer.STATE_PREPARING /*2*/:
                return C0338R.drawable.ic_notif_hanista;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return C0338R.drawable.ic_notif_android;
            case VideoPlayer.STATE_READY /*4*/:
                return C0338R.drawable.ic_notif_download;
            case VideoPlayer.STATE_ENDED /*5*/:
                return C0338R.drawable.ic_notif_notification;
            case Method.TRACE /*6*/:
                return C0338R.drawable.ic_notif_tools;
            case Method.PATCH /*7*/:
                return C0338R.drawable.ic_notif_books;
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                return C0338R.drawable.ic_notif_business;
            case C0338R.styleable.PromptView_iconTint /*9*/:
                return C0338R.drawable.ic_notif_communication;
            case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                return C0338R.drawable.ic_notif_education;
            case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                return C0338R.drawable.ic_notif_entertainment;
            case Atom.FULL_HEADER_SIZE /*12*/:
                return C0338R.drawable.ic_notif_finance;
            case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                return C0338R.drawable.ic_notif_healthy;
            case C0338R.styleable.PromptView_primaryTextFontFamily /*14*/:
                return C0338R.drawable.ic_notif_medical;
            case C0338R.styleable.PromptView_primaryTextSize /*15*/:
                return C0338R.drawable.ic_notif_music;
            case TLRPC.USER_FLAG_PHONE /*16*/:
                return C0338R.drawable.ic_notif_news;
            case C0338R.styleable.PromptView_primaryTextTypeface /*17*/:
                return C0338R.drawable.ic_notif_productivity;
            case C0338R.styleable.PromptView_secondaryText /*18*/:
                return C0338R.drawable.ic_notif_religious;
            case C0338R.styleable.PromptView_secondaryTextColour /*19*/:
                return C0338R.drawable.ic_notif_sport;
            default:
                return m1955b();
        }
    }

    public static Bitmap m1952a(String str) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void m1953a(ServerNotification serverNotification) {
        if (VERSION.SDK_INT <= 15 || serverNotification.getBigPictureUrl() == null) {
            m1956b(serverNotification);
        } else {
            m1958c(serverNotification);
        }
    }

    private boolean m1954a(Long l) {
        Throwable e;
        Throwable th;
        boolean z = false;
        FileInputStream fileInputStream = null;
        FileInputStream fileInputStream2;
        try {
            File file = new File(MoboUtils.m1707b(), "notification.data");
            if (file.exists()) {
                Properties properties = new Properties();
                fileInputStream2 = new FileInputStream(file);
                try {
                    properties.load(fileInputStream2);
                    if (properties.getProperty("is_notified_" + l) != null) {
                        z = true;
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (Throwable e2) {
                                Log.e("NotificationService", e2.getMessage(), e2);
                            }
                        }
                    } else if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (Throwable e22) {
                            Log.e("NotificationService", e22.getMessage(), e22);
                        }
                    }
                } catch (Exception e3) {
                    e22 = e3;
                    try {
                        Log.e("NotificationService", e22.getMessage(), e22);
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (Throwable e222) {
                                Log.e("NotificationService", e222.getMessage(), e222);
                            }
                        }
                        return z;
                    } catch (Throwable th2) {
                        th = th2;
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (Throwable e2222) {
                                Log.e("NotificationService", e2222.getMessage(), e2222);
                            }
                        }
                        throw th;
                    }
                }
            } else if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Throwable e22222) {
                    Log.e("NotificationService", e22222.getMessage(), e22222);
                }
            }
        } catch (Exception e4) {
            e22222 = e4;
            fileInputStream2 = fileInputStream;
            Log.e("NotificationService", e22222.getMessage(), e22222);
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            return z;
        } catch (Throwable th3) {
            th = th3;
            fileInputStream2 = fileInputStream;
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            throw th;
        }
        return z;
    }

    private int m1955b() {
        return C0338R.drawable.ic_mobo_notif;
    }

    private void m1956b(ServerNotification serverNotification) {
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        Builder autoCancel = new Builder(this).setSmallIcon(m1951a(serverNotification.getIconType())).setTicker(serverNotification.getTitle()).setContentTitle(serverNotification.getTitle()).setDefaults(1).setOngoing(serverNotification.isOngoing().booleanValue()).setContentText(serverNotification.getText()).setAutoCancel(true);
        if (serverNotification.getImageUrl() != null) {
            autoCancel.setLargeIcon(m1952a(serverNotification.getImageUrl()));
        }
        if (serverNotification.getBigPictureUrl() != null) {
            autoCancel.setStyle(new BigPictureStyle().bigPicture(m1952a(serverNotification.getBigPictureUrl())).setBigContentTitle(serverNotification.getTitle()).setSummaryText(serverNotification.getText()));
        }
        Intent intent = new Intent();
        if (serverNotification.getType() == null) {
            intent.setClass(this, LaunchActivity.class);
        } else if (serverNotification.getType().intValue() != 1 && serverNotification.getType().intValue() == 2) {
            intent.setAction(serverNotification.getAction());
            intent.setData(Uri.parse(serverNotification.getUrl()));
        }
        if (serverNotification.getTargetPackage() != null) {
            intent.setPackage(serverNotification.getTargetPackage());
        }
        autoCancel.setContentIntent(PendingIntent.getActivity(this, 0, intent, 268435456));
        notificationManager.notify(MoboUtils.m1691a(), autoCancel.build());
    }

    private void m1957b(Long l) {
        Throwable e;
        InputStream inputStream;
        OutputStream outputStream;
        FileOutputStream fileOutputStream;
        InputStream inputStream2;
        FileInputStream fileInputStream = null;
        try {
            File file = new File(MoboUtils.m1707b(), "notification.data");
            if (!file.exists()) {
                file.createNewFile();
            }
            Properties properties = new Properties();
            InputStream fileInputStream2 = new FileInputStream(file);
            try {
                properties.load(fileInputStream2);
                properties.setProperty("is_notified_" + l, "true");
                OutputStream fileOutputStream2 = new FileOutputStream(file);
                try {
                    properties.store(fileOutputStream2, null);
                    fileOutputStream2.flush();
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (Throwable e2) {
                            Log.e("NotificationService", e2.getMessage(), e2);
                            return;
                        }
                    }
                    if (fileInputStream2 != null) {
                        fileInputStream2.close();
                    }
                } catch (Exception e3) {
                    e2 = e3;
                    inputStream = fileInputStream2;
                    outputStream = fileOutputStream2;
                    try {
                        Log.e("NotificationService", e2.getMessage(), e2);
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable e22) {
                                Log.e("NotificationService", e22.getMessage(), e22);
                                return;
                            }
                        }
                        if (fileInputStream == null) {
                            fileInputStream.close();
                        }
                    } catch (Throwable th) {
                        e22 = th;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable e4) {
                                Log.e("NotificationService", e4.getMessage(), e4);
                                throw e22;
                            }
                        }
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        throw e22;
                    }
                } catch (Throwable th2) {
                    e22 = th2;
                    inputStream = fileInputStream2;
                    outputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    throw e22;
                }
            } catch (Exception e5) {
                e22 = e5;
                inputStream2 = fileInputStream2;
                fileOutputStream = null;
                inputStream = inputStream2;
                Log.e("NotificationService", e22.getMessage(), e22);
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (fileInputStream == null) {
                    fileInputStream.close();
                }
            } catch (Throwable th3) {
                e22 = th3;
                inputStream2 = fileInputStream2;
                fileOutputStream = null;
                inputStream = inputStream2;
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                throw e22;
            }
        } catch (Exception e6) {
            e22 = e6;
            fileOutputStream = null;
            Log.e("NotificationService", e22.getMessage(), e22);
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (fileInputStream == null) {
                fileInputStream.close();
            }
        } catch (Throwable th4) {
            e22 = th4;
            fileOutputStream = null;
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            throw e22;
        }
    }

    @SuppressLint({"NewApi"})
    private void m1958c(ServerNotification serverNotification) {
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        Notification.Builder autoCancel = new Notification.Builder(this).setSmallIcon(m1951a(serverNotification.getIconType())).setTicker(serverNotification.getTitle()).setContentTitle(serverNotification.getTitle()).setDefaults(1).setOngoing(serverNotification.isOngoing().booleanValue()).setContentText(serverNotification.getText()).setAutoCancel(true);
        if (serverNotification.getImageUrl() != null) {
            autoCancel.setLargeIcon(m1952a(serverNotification.getImageUrl()));
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), C0338R.layout.customnotification);
        remoteViews.setTextViewText(C0338R.id.tv_title, serverNotification.getTitle());
        remoteViews.setTextViewText(C0338R.id.tv_text, serverNotification.getText());
        if (serverNotification.getText2() != null) {
            remoteViews.setTextViewText(C0338R.id.tv_text_2, serverNotification.getText2());
        } else {
            remoteViews.setViewVisibility(C0338R.id.tv_text_2, 8);
        }
        if (serverNotification.getText3() != null) {
            remoteViews.setTextViewText(C0338R.id.tv_text_3, serverNotification.getText3());
        } else {
            remoteViews.setViewVisibility(C0338R.id.tv_text_3, 8);
        }
        if (serverNotification.getBtnText() != null) {
            remoteViews.setTextViewText(C0338R.id.btn_download, serverNotification.getBtnText());
        } else {
            remoteViews.setViewVisibility(C0338R.id.btn_download, 8);
        }
        if (serverNotification.getBigPictureUrl() != null) {
            remoteViews.setImageViewBitmap(C0338R.id.img_background, m1952a(serverNotification.getBigPictureUrl()));
        }
        if (serverNotification.getPictureUrl() != null) {
            remoteViews.setImageViewBitmap(C0338R.id.img_left, m1952a(serverNotification.getPictureUrl()));
        }
        Intent intent = new Intent();
        if (serverNotification.getType() == null) {
            intent.setClass(this, LaunchActivity.class);
        } else if (serverNotification.getType().intValue() != 1 && serverNotification.getType().intValue() == 2) {
            intent.setAction(serverNotification.getAction());
            intent.setData(Uri.parse(serverNotification.getUrl()));
        }
        if (serverNotification.getTargetPackage() != null) {
            intent.setPackage(serverNotification.getTargetPackage());
        }
        autoCancel.setContentIntent(PendingIntent.getActivity(this, 0, intent, 268435456));
        Notification build = autoCancel.build();
        build.bigContentView = remoteViews;
        notificationManager.notify(MoboUtils.m1691a(), build);
    }

    public String m1959a(List<Long> list) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        String str2 = str;
        for (Long longValue : list) {
            str2 = str2 + "-" + longValue.longValue();
        }
        return str2.replaceFirst("-", TtmlNode.ANONYMOUS_REGION_ID);
    }

    public void m1960a() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), 0);
        if (System.currentTimeMillis() - Long.valueOf(sharedPreferences.getLong("notificationLastNotify", 0)).longValue() > 14400000) {
            sharedPreferences.edit().putLong("notificationLastNotify", System.currentTimeMillis()).commit();
            List b = m1961b(sharedPreferences.getString("notificationIds", null));
            String str = "notification.jsp";
            long clientUserId = (long) UserConfig.getClientUserId();
            if (clientUserId == 0) {
                clientUserId = (long) VERSION.SDK_INT;
            }
            RestClient restClient = new RestClient("http://hanista.com:PORT/app-general/service/mobogram/".replace("PORT", (new Random(clientUserId + System.currentTimeMillis()).nextInt(5) + 8081) + TtmlNode.ANONYMOUS_REGION_ID));
            Object notificationRequest = new NotificationRequest(b, MoboUtils.m1720f(this), Integer.valueOf(MoboUtils.m1692a((Context) this)), Integer.valueOf(VERSION.SDK_INT));
            Gson gson = new Gson();
            for (ServerNotification serverNotification : Arrays.asList((ServerNotification[]) gson.fromJson(restClient.m1966a(str, gson.toJson(notificationRequest)), ServerNotification[].class))) {
                if (!b.contains(serverNotification.getId())) {
                    if (!m1954a(serverNotification.getId())) {
                        m1957b(serverNotification.getId());
                        m1953a(serverNotification);
                    }
                    b.add(serverNotification.getId());
                }
            }
            sharedPreferences.edit().putString("notificationIds", m1959a(b)).commit();
        }
    }

    public List<Long> m1961b(String str) {
        List arrayList = new ArrayList();
        if (!MoboUtils.m1710b(str)) {
            for (String parseLong : str.split("-")) {
                arrayList.add(Long.valueOf(Long.parseLong(parseLong)));
            }
        }
        return arrayList;
    }

    protected void onHandleIntent(Intent intent) {
        try {
            if (MoboUtils.m1718e(this)) {
                m1960a();
            }
        } catch (Exception e) {
        }
    }
}
