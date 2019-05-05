package org.aspectj.lang;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Aspects {
    private static final String ASPECTOF = "aspectOf";
    private static final Class[] EMPTY_CLASS_ARRAY;
    private static final Object[] EMPTY_OBJECT_ARRAY;
    private static final String HASASPECT = "hasAspect";
    private static final Class[] PEROBJECT_CLASS_ARRAY;
    private static final Class[] PERTYPEWITHIN_CLASS_ARRAY;

    static {
        EMPTY_CLASS_ARRAY = new Class[0];
        PEROBJECT_CLASS_ARRAY = new Class[]{Object.class};
        PERTYPEWITHIN_CLASS_ARRAY = new Class[]{Class.class};
        EMPTY_OBJECT_ARRAY = new Object[0];
    }

    public static <T> T aspectOf(Class<T> cls) {
        try {
            return getSingletonOrThreadAspectOf(cls).invoke(null, EMPTY_OBJECT_ARRAY);
        } catch (Throwable e) {
            throw new NoAspectBoundException(cls.getName(), e);
        } catch (Throwable e2) {
            throw new NoAspectBoundException(cls.getName(), e2);
        }
    }

    public static <T> T aspectOf(Class<T> cls, Class<?> cls2) {
        try {
            return getPerTypeWithinAspectOf(cls).invoke(null, new Object[]{cls2});
        } catch (Throwable e) {
            throw new NoAspectBoundException(cls.getName(), e);
        } catch (Throwable e2) {
            throw new NoAspectBoundException(cls.getName(), e2);
        }
    }

    public static <T> T aspectOf(Class<T> cls, Object obj) {
        try {
            return getPerObjectAspectOf(cls).invoke(null, new Object[]{obj});
        } catch (Throwable e) {
            throw new NoAspectBoundException(cls.getName(), e);
        } catch (Throwable e2) {
            throw new NoAspectBoundException(cls.getName(), e2);
        }
    }

    private static Method checkAspectOf(Method method, Class<?> cls) {
        method.setAccessible(true);
        if (method.isAccessible() && Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
            return method;
        }
        throw new NoSuchMethodException(cls.getName() + ".aspectOf(..) is not accessible public static");
    }

    private static Method checkHasAspect(Method method, Class cls) {
        method.setAccessible(true);
        if (method.isAccessible() && Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
            return method;
        }
        throw new NoSuchMethodException(cls.getName() + ".hasAspect(..) is not accessible public static");
    }

    private static Method getPerObjectAspectOf(Class<?> cls) {
        return checkAspectOf(cls.getDeclaredMethod(ASPECTOF, PEROBJECT_CLASS_ARRAY), cls);
    }

    private static Method getPerObjectHasAspect(Class cls) {
        return checkHasAspect(cls.getDeclaredMethod(HASASPECT, PEROBJECT_CLASS_ARRAY), cls);
    }

    private static Method getPerTypeWithinAspectOf(Class<?> cls) {
        return checkAspectOf(cls.getDeclaredMethod(ASPECTOF, PERTYPEWITHIN_CLASS_ARRAY), cls);
    }

    private static Method getPerTypeWithinHasAspect(Class cls) {
        return checkHasAspect(cls.getDeclaredMethod(HASASPECT, PERTYPEWITHIN_CLASS_ARRAY), cls);
    }

    private static Method getSingletonOrThreadAspectOf(Class<?> cls) {
        return checkAspectOf(cls.getDeclaredMethod(ASPECTOF, EMPTY_CLASS_ARRAY), cls);
    }

    private static Method getSingletonOrThreadHasAspect(Class cls) {
        return checkHasAspect(cls.getDeclaredMethod(HASASPECT, EMPTY_CLASS_ARRAY), cls);
    }

    public static boolean hasAspect(Class<?> cls) {
        try {
            return ((Boolean) getSingletonOrThreadHasAspect(cls).invoke(null, EMPTY_OBJECT_ARRAY)).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasAspect(Class<?> cls, Class<?> cls2) {
        try {
            return ((Boolean) getPerTypeWithinHasAspect(cls).invoke(null, new Object[]{cls2})).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasAspect(Class<?> cls, Object obj) {
        try {
            return ((Boolean) getPerObjectHasAspect(cls).invoke(null, new Object[]{obj})).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }
}
