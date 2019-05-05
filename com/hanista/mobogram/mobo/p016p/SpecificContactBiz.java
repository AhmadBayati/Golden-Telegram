package com.hanista.mobogram.mobo.p016p;

import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import java.util.HashMap;
import java.util.Map;

/* renamed from: com.hanista.mobogram.mobo.p.d */
public class SpecificContactBiz {
    public static final Map<Integer, Integer> f2058a;

    static {
        f2058a = new HashMap();
    }

    public static void m2017a() {
        f2058a.clear();
        for (SpecificContact specificContact : new DataBaseAccess().m909q()) {
            f2058a.put(Integer.valueOf(specificContact.m1999b()), Integer.valueOf(specificContact.m2001c()));
        }
    }
}
