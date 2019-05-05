package com.hanista.mobogram.SQLite;

import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.tgnet.NativeByteBuffer;
import java.nio.ByteBuffer;

public class SQLitePreparedStatement {
    private boolean finalizeAfterQuery;
    private boolean isFinalized;
    private int sqliteStatementHandle;

    public SQLitePreparedStatement(SQLiteDatabase sQLiteDatabase, String str, boolean z) {
        this.isFinalized = false;
        this.finalizeAfterQuery = false;
        this.finalizeAfterQuery = z;
        this.sqliteStatementHandle = prepare(sQLiteDatabase.getSQLiteHandle(), str);
    }

    native void bindByteBuffer(int i, int i2, ByteBuffer byteBuffer, int i3);

    public void bindByteBuffer(int i, NativeByteBuffer nativeByteBuffer) {
        bindByteBuffer(this.sqliteStatementHandle, i, nativeByteBuffer.buffer, nativeByteBuffer.limit());
    }

    public void bindByteBuffer(int i, ByteBuffer byteBuffer) {
        bindByteBuffer(this.sqliteStatementHandle, i, byteBuffer, byteBuffer.limit());
    }

    public void bindDouble(int i, double d) {
        bindDouble(this.sqliteStatementHandle, i, d);
    }

    native void bindDouble(int i, int i2, double d);

    native void bindInt(int i, int i2, int i3);

    public void bindInteger(int i, int i2) {
        bindInt(this.sqliteStatementHandle, i, i2);
    }

    native void bindLong(int i, int i2, long j);

    public void bindLong(int i, long j) {
        bindLong(this.sqliteStatementHandle, i, j);
    }

    public void bindNull(int i) {
        bindNull(this.sqliteStatementHandle, i);
    }

    native void bindNull(int i, int i2);

    native void bindString(int i, int i2, String str);

    public void bindString(int i, String str) {
        bindString(this.sqliteStatementHandle, i, str);
    }

    void checkFinalized() {
        if (this.isFinalized) {
            throw new SQLiteException("Prepared query finalized");
        }
    }

    public void dispose() {
        if (this.finalizeAfterQuery) {
            finalizeQuery();
        }
    }

    native void finalize(int i);

    public void finalizeQuery() {
        if (!this.isFinalized) {
            try {
                this.isFinalized = true;
                finalize(this.sqliteStatementHandle);
            } catch (Throwable e) {
                FileLog.m17e("tmessages", e.getMessage(), e);
            }
        }
    }

    public int getStatementHandle() {
        return this.sqliteStatementHandle;
    }

    native int prepare(int i, String str);

    public SQLiteCursor query(Object[] objArr) {
        if (objArr == null) {
            throw new IllegalArgumentException();
        }
        checkFinalized();
        reset(this.sqliteStatementHandle);
        int i = 1;
        for (Object obj : objArr) {
            if (obj == null) {
                bindNull(this.sqliteStatementHandle, i);
            } else if (obj instanceof Integer) {
                bindInt(this.sqliteStatementHandle, i, ((Integer) obj).intValue());
            } else if (obj instanceof Double) {
                bindDouble(this.sqliteStatementHandle, i, ((Double) obj).doubleValue());
            } else if (obj instanceof String) {
                bindString(this.sqliteStatementHandle, i, (String) obj);
            } else {
                throw new IllegalArgumentException();
            }
            i++;
        }
        return new SQLiteCursor(this);
    }

    public void requery() {
        checkFinalized();
        reset(this.sqliteStatementHandle);
    }

    native void reset(int i);

    public int step() {
        return step(this.sqliteStatementHandle);
    }

    native int step(int i);

    public SQLitePreparedStatement stepThis() {
        step(this.sqliteStatementHandle);
        return this;
    }
}
