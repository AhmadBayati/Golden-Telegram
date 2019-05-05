package com.hanista.mobogram.SQLite;

import com.hanista.mobogram.tgnet.NativeByteBuffer;

public class SQLiteCursor {
    public static final int FIELD_TYPE_BYTEARRAY = 4;
    public static final int FIELD_TYPE_FLOAT = 2;
    public static final int FIELD_TYPE_INT = 1;
    public static final int FIELD_TYPE_NULL = 5;
    public static final int FIELD_TYPE_STRING = 3;
    boolean inRow;
    SQLitePreparedStatement preparedStatement;

    public SQLiteCursor(SQLitePreparedStatement sQLitePreparedStatement) {
        this.inRow = false;
        this.preparedStatement = sQLitePreparedStatement;
    }

    public byte[] byteArrayValue(int i) {
        checkRow();
        return columnByteArrayValue(this.preparedStatement.getStatementHandle(), i);
    }

    public NativeByteBuffer byteBufferValue(int i) {
        checkRow();
        int columnByteBufferValue = columnByteBufferValue(this.preparedStatement.getStatementHandle(), i);
        return columnByteBufferValue != 0 ? NativeByteBuffer.wrap(columnByteBufferValue) : null;
    }

    void checkRow() {
        if (!this.inRow) {
            throw new SQLiteException("You must call next before");
        }
    }

    native byte[] columnByteArrayValue(int i, int i2);

    native int columnByteBufferValue(int i, int i2);

    native double columnDoubleValue(int i, int i2);

    native int columnIntValue(int i, int i2);

    native int columnIsNull(int i, int i2);

    native long columnLongValue(int i, int i2);

    native String columnStringValue(int i, int i2);

    native int columnType(int i, int i2);

    public void dispose() {
        this.preparedStatement.dispose();
    }

    public double doubleValue(int i) {
        checkRow();
        return columnDoubleValue(this.preparedStatement.getStatementHandle(), i);
    }

    public int getStatementHandle() {
        return this.preparedStatement.getStatementHandle();
    }

    public int getTypeOf(int i) {
        checkRow();
        return columnType(this.preparedStatement.getStatementHandle(), i);
    }

    public int intValue(int i) {
        checkRow();
        return columnIntValue(this.preparedStatement.getStatementHandle(), i);
    }

    public boolean isNull(int i) {
        checkRow();
        return columnIsNull(this.preparedStatement.getStatementHandle(), i) == FIELD_TYPE_INT;
    }

    public long longValue(int i) {
        checkRow();
        return columnLongValue(this.preparedStatement.getStatementHandle(), i);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean next() {
        /*
        r8 = this;
        r6 = -1;
        r0 = r8.preparedStatement;
        r1 = r8.preparedStatement;
        r1 = r1.getStatementHandle();
        r0 = r0.step(r1);
        if (r0 != r6) goto L_0x0044;
    L_0x000f:
        r1 = 6;
        r7 = r1;
        r1 = r0;
        r0 = r7;
    L_0x0013:
        r2 = r0 + -1;
        if (r0 == 0) goto L_0x004e;
    L_0x0017:
        r0 = "tmessages";
        r3 = "sqlite busy, waiting...";
        com.hanista.mobogram.messenger.FileLog.m16e(r0, r3);	 Catch:{ Exception -> 0x003b }
        r4 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        java.lang.Thread.sleep(r4);	 Catch:{ Exception -> 0x003b }
        r0 = r8.preparedStatement;	 Catch:{ Exception -> 0x003b }
        r0 = r0.step();	 Catch:{ Exception -> 0x003b }
        if (r0 != 0) goto L_0x0038;
    L_0x002d:
        if (r0 != r6) goto L_0x0044;
    L_0x002f:
        r0 = new com.hanista.mobogram.SQLite.SQLiteException;
        r1 = "sqlite busy";
        r0.<init>(r1);
        throw r0;
    L_0x0038:
        r1 = r0;
        r0 = r2;
        goto L_0x0013;
    L_0x003b:
        r0 = move-exception;
        r3 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r3, r0);
        r0 = r2;
        goto L_0x0013;
    L_0x0044:
        if (r0 != 0) goto L_0x004c;
    L_0x0046:
        r0 = 1;
    L_0x0047:
        r8.inRow = r0;
        r0 = r8.inRow;
        return r0;
    L_0x004c:
        r0 = 0;
        goto L_0x0047;
    L_0x004e:
        r0 = r1;
        goto L_0x002d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.SQLite.SQLiteCursor.next():boolean");
    }

    public String stringValue(int i) {
        checkRow();
        return columnStringValue(this.preparedStatement.getStatementHandle(), i);
    }
}
