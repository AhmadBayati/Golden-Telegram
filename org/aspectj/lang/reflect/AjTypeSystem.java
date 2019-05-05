package org.aspectj.lang.reflect;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.aspectj.internal.lang.reflect.AjTypeImpl;

public class AjTypeSystem {
    private static Map<Class, WeakReference<AjType>> ajTypes;

    static {
        ajTypes = Collections.synchronizedMap(new WeakHashMap());
    }

    public static <T> AjType<T> getAjType(Class<T> cls) {
        WeakReference weakReference = (WeakReference) ajTypes.get(cls);
        if (weakReference != null) {
            AjType<T> ajType = (AjType) weakReference.get();
            if (ajType != null) {
                return ajType;
            }
            AjType ajTypeImpl = new AjTypeImpl(cls);
            ajTypes.put(cls, new WeakReference(ajTypeImpl));
            return ajTypeImpl;
        }
        ajTypeImpl = new AjTypeImpl(cls);
        ajTypes.put(cls, new WeakReference(ajTypeImpl));
        return ajTypeImpl;
    }
}
