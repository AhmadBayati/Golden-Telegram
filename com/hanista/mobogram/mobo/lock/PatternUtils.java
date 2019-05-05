package com.hanista.mobogram.mobo.lock;

import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.lock.PatternView.Cell;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.lock.d */
class PatternUtils {
    public static List<Cell> m1574a(String str) {
        List<Cell> arrayList = new ArrayList();
        try {
            for (byte b : str.getBytes(C0700C.UTF8_NAME)) {
                arrayList.add(Cell.m1473a(b / 3, b % 3));
            }
        } catch (UnsupportedEncodingException e) {
        }
        return arrayList;
    }
}
