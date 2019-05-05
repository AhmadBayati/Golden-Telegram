package com.hanista.mobogram.mobo.p004e;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.hanista.mobogram.messenger.ApplicationLoader;

/* renamed from: com.hanista.mobogram.mobo.e.c */
public class SettingManager {
    private MobogramDBHelper f874a;

    public SettingManager() {
        this.f874a = ApplicationLoader.getOpenHelper();
    }

    private void m939a(String str, String str2) {
        SQLiteDatabase writableDatabase = this.f874a.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("key", str);
            contentValues.put("value", str2);
            if (m940c(str) == null) {
                writableDatabase.insertOrThrow("tbl_setting", null, contentValues);
            } else {
                writableDatabase.update("tbl_setting", contentValues, "key='" + str + "'", null);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    private String m940c(String str) {
        Throwable th;
        Cursor cursor = null;
        try {
            Cursor query = this.f874a.getReadableDatabase().query("tbl_setting", null, "key= '" + str + "'", null, null, null, null);
            try {
                if (query.moveToNext()) {
                    String string = query.getString(query.getColumnIndex("value"));
                    if (query == null) {
                        return string;
                    }
                    query.close();
                    return string;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                cursor = query;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public int m941a(String str) {
        String c = m940c(str);
        return c != null ? Integer.parseInt(c) : 0;
    }

    public void m942a(String str, int i) {
        m939a(str, i + TtmlNode.ANONYMOUS_REGION_ID);
    }

    public void m943a(String str, boolean z) {
        m939a(str, z + TtmlNode.ANONYMOUS_REGION_ID);
    }

    public boolean m944b(String str) {
        String c = m940c(str);
        return c != null ? Boolean.parseBoolean(c) : false;
    }
}
