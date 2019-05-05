package com.google.gson.stream;

import com.hanista.mobogram.tgnet.TLRPC;

final class StringPool {
    private final String[] pool;

    StringPool() {
        this.pool = new String[TLRPC.USER_FLAG_UNUSED3];
    }

    public String get(char[] cArr, int i, int i2) {
        int i3;
        int i4 = 0;
        int i5 = 0;
        for (i3 = i; i3 < i + i2; i3++) {
            i5 = (i5 * 31) + cArr[i3];
        }
        i3 = ((i5 >>> 20) ^ (i5 >>> 12)) ^ i5;
        i5 = (this.pool.length - 1) & (i3 ^ ((i3 >>> 7) ^ (i3 >>> 4)));
        String str = this.pool[i5];
        if (str == null || str.length() != i2) {
            str = new String(cArr, i, i2);
            this.pool[i5] = str;
            return str;
        }
        while (i4 < i2) {
            if (str.charAt(i4) != cArr[i + i4]) {
                str = new String(cArr, i, i2);
                this.pool[i5] = str;
                return str;
            }
            i4++;
        }
        return str;
    }
}
