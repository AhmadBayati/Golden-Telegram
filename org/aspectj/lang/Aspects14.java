package org.aspectj.lang;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Aspects14 {
    private static final String ASPECTOF = "aspectOf";
    private static final Class[] EMPTY_CLASS_ARRAY;
    private static final Object[] EMPTY_OBJECT_ARRAY;
    private static final String HASASPECT = "hasAspect";
    private static final Class[] PEROBJECT_CLASS_ARRAY;
    private static final Class[] PERTYPEWITHIN_CLASS_ARRAY;
    static Class class$java$lang$Class;
    static Class class$java$lang$Object;

    static {
        Class class$;
        EMPTY_CLASS_ARRAY = new Class[0];
        Class[] clsArr = new Class[1];
        if (class$java$lang$Object == null) {
            class$ = class$("java.lang.Object");
            class$java$lang$Object = class$;
        } else {
            class$ = class$java$lang$Object;
        }
        clsArr[0] = class$;
        PEROBJECT_CLASS_ARRAY = clsArr;
        clsArr = new Class[1];
        if (class$java$lang$Class == null) {
            class$ = class$("java.lang.Class");
            class$java$lang$Class = class$;
        } else {
            class$ = class$java$lang$Class;
        }
        clsArr[0] = class$;
        PERTYPEWITHIN_CLASS_ARRAY = clsArr;
        EMPTY_OBJECT_ARRAY = new Object[0];
    }

    public static Object aspectOf(Class cls) {
        try {
            return getSingletonOrThreadAspectOf(cls).invoke(null, EMPTY_OBJECT_ARRAY);
        } catch (Throwable e) {
            throw new NoAspectBoundException(cls.getName(), e);
        } catch (Throwable e2) {
            throw new NoAspectBoundException(cls.getName(), e2);
        }
    }

    public static Object aspectOf(Class cls, Class cls2) {
        try {
            return getPerTypeWithinAspectOf(cls).invoke(null, new Object[]{cls2});
        } catch (Throwable e) {
            throw new NoAspectBoundException(cls.getName(), e);
        } catch (Throwable e2) {
            throw new NoAspectBoundException(cls.getName(), e2);
        }
    }

    public static Object aspectOf(Class cls, Object obj) {
        try {
            return getPerObjectAspectOf(cls).invoke(null, new Object[]{obj});
        } catch (Throwable e) {
            throw new NoAspectBoundException(cls.getName(), e);
        } catch (Throwable e2) {
            throw new NoAspectBoundException(cls.getName(), e2);
        }
    }

    private static Method checkAspectOf(Method method, Class cls) {
        method.setAccessible(true);
        if (method.isAccessible() && Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
            return method;
        }
        throw new NoSuchMethodException(new StringBuffer().append(cls.getName()).append(".aspectOf(..) is not accessible public static").toString());
    }

    private static Method checkHasAspect(Method method, Class cls) {
        method.setAccessible(true);
        if (method.isAccessible() && Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
            return method;
        }
        throw new NoSuchMethodException(new StringBuffer().append(cls.getName()).append(".hasAspect(..) is not accessible public static").toString());
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (Throwable e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    private static Method getPerObjectAspectOf(Class cls) {
        return checkAspectOf(cls.getDeclaredMethod(ASPECTOF, PEROBJECT_CLASS_ARRAY), cls);
    }

    private static Method getPerObjectHasAspect(Class cls) {
        return checkHasAspect(cls.getDeclaredMethod(HASASPECT, PEROBJECT_CLASS_ARRAY), cls);
    }

    private static Method getPerTypeWithinAspectOf(Class cls) {
        return checkAspectOf(cls.getDeclaredMethod(ASPECTOF, PERTYPEWITHIN_CLASS_ARRAY), cls);
    }

    private static Method getPerTypeWithinHasAspect(Class cls) {
        return checkHasAspect(cls.getDeclaredMethod(HASASPECT, PERTYPEWITHIN_CLASS_ARRAY), cls);
    }

    private static Method getSingletonOrThreadAspectOf(Class cls) {
        return checkAspectOf(cls.getDeclaredMethod(ASPECTOF, EMPTY_CLASS_ARRAY), cls);
    }

    private static Method getSingletonOrThreadHasAspect(Class cls) {
        return checkHasAspect(cls.getDeclaredMethod(HASASPECT, EMPTY_CLASS_ARRAY), cls);
    }

    public static boolean hasAspect(Class cls) {
        try {
            return ((Boolean) getSingletonOrThreadHasAspect(cls).invoke(null, EMPTY_OBJECT_ARRAY)).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasAspect(Class cls, Class cls2) {
        try {
            return ((Boolean) getPerTypeWithinHasAspect(cls).invoke(null, new Object[]{cls2})).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasAspect(Class cls, Object obj) {
        try {
            return ((Boolean) getPerObjectHasAspect(cls).invoke(null, new Object[]{obj})).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }
}
