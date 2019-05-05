package org.aspectj.runtime.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.EnclosingStaticPart;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.lang.reflect.CatchClauseSignature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.InitializerSignature;
import org.aspectj.lang.reflect.LockSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.lang.reflect.UnlockSignature;

public final class Factory {
    private static Object[] NO_ARGS;
    static Class class$java$lang$ClassNotFoundException;
    static Hashtable prims;
    int count;
    String filename;
    Class lexicalClass;
    ClassLoader lookupClassLoader;

    static {
        prims = new Hashtable();
        prims.put("void", Void.TYPE);
        prims.put("boolean", Boolean.TYPE);
        prims.put("byte", Byte.TYPE);
        prims.put("char", Character.TYPE);
        prims.put("short", Short.TYPE);
        prims.put("int", Integer.TYPE);
        prims.put("long", Long.TYPE);
        prims.put("float", Float.TYPE);
        prims.put("double", Double.TYPE);
        NO_ARGS = new Object[0];
    }

    public Factory(String str, Class cls) {
        this.filename = str;
        this.lexicalClass = cls;
        this.count = 0;
        this.lookupClassLoader = cls.getClassLoader();
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (Throwable e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    static Class makeClass(String str, ClassLoader classLoader) {
        if (str.equals("*")) {
            return null;
        }
        Class cls = (Class) prims.get(str);
        if (cls != null) {
            return cls;
        }
        if (classLoader != null) {
            return Class.forName(str, false, classLoader);
        }
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            if (class$java$lang$ClassNotFoundException != null) {
                return class$java$lang$ClassNotFoundException;
            }
            cls = class$("java.lang.ClassNotFoundException");
            class$java$lang$ClassNotFoundException = cls;
            return cls;
        }
    }

    public static StaticPart makeEncSJP(Member member) {
        Signature signature;
        String str;
        if (member instanceof Method) {
            Method method = (Method) member;
            MethodSignatureImpl methodSignatureImpl = new MethodSignatureImpl(method.getModifiers(), method.getName(), method.getDeclaringClass(), method.getParameterTypes(), new String[method.getParameterTypes().length], method.getExceptionTypes(), method.getReturnType());
            signature = methodSignatureImpl;
            str = JoinPoint.METHOD_EXECUTION;
        } else if (member instanceof Constructor) {
            Constructor constructor = (Constructor) member;
            ConstructorSignatureImpl constructorSignatureImpl = new ConstructorSignatureImpl(constructor.getModifiers(), constructor.getDeclaringClass(), constructor.getParameterTypes(), new String[constructor.getParameterTypes().length], constructor.getExceptionTypes());
            Object obj = constructorSignatureImpl;
            str = JoinPoint.CONSTRUCTOR_EXECUTION;
        } else {
            throw new IllegalArgumentException("member must be either a method or constructor");
        }
        return new EnclosingStaticPartImpl(-1, str, signature, null);
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object obj, Object obj2) {
        return new JoinPointImpl(staticPart, obj, obj2, NO_ARGS);
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object obj, Object obj2, Object obj3) {
        return new JoinPointImpl(staticPart, obj, obj2, new Object[]{obj3});
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object obj, Object obj2, Object obj3, Object obj4) {
        return new JoinPointImpl(staticPart, obj, obj2, new Object[]{obj3, obj4});
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object obj, Object obj2, Object[] objArr) {
        return new JoinPointImpl(staticPart, obj, obj2, objArr);
    }

    public AdviceSignature makeAdviceSig(int i, String str, Class cls, Class[] clsArr, String[] strArr, Class[] clsArr2, Class cls2) {
        Object adviceSignatureImpl = new AdviceSignatureImpl(i, str, cls, clsArr, strArr, clsArr2, cls2);
        adviceSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return adviceSignatureImpl;
    }

    public AdviceSignature makeAdviceSig(String str) {
        Object adviceSignatureImpl = new AdviceSignatureImpl(str);
        adviceSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return adviceSignatureImpl;
    }

    public AdviceSignature makeAdviceSig(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        int i;
        int parseInt = Integer.parseInt(str, 16);
        Class makeClass = makeClass(str3, this.lookupClassLoader);
        StringTokenizer stringTokenizer = new StringTokenizer(str4, ":");
        int countTokens = stringTokenizer.countTokens();
        Class[] clsArr = new Class[countTokens];
        for (i = 0; i < countTokens; i++) {
            clsArr[i] = makeClass(stringTokenizer.nextToken(), this.lookupClassLoader);
        }
        stringTokenizer = new StringTokenizer(str5, ":");
        int countTokens2 = stringTokenizer.countTokens();
        String[] strArr = new String[countTokens2];
        for (i = 0; i < countTokens2; i++) {
            strArr[i] = stringTokenizer.nextToken();
        }
        stringTokenizer = new StringTokenizer(str6, ":");
        int countTokens3 = stringTokenizer.countTokens();
        Class[] clsArr2 = new Class[countTokens3];
        for (i = 0; i < countTokens3; i++) {
            clsArr2[i] = makeClass(stringTokenizer.nextToken(), this.lookupClassLoader);
        }
        String str8 = str2;
        Object adviceSignatureImpl = new AdviceSignatureImpl(parseInt, str8, makeClass, clsArr, strArr, clsArr2, makeClass(str7, this.lookupClassLoader));
        adviceSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return adviceSignatureImpl;
    }

    public CatchClauseSignature makeCatchClauseSig(Class cls, Class cls2, String str) {
        Object catchClauseSignatureImpl = new CatchClauseSignatureImpl(cls, cls2, str);
        catchClauseSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return catchClauseSignatureImpl;
    }

    public CatchClauseSignature makeCatchClauseSig(String str) {
        Object catchClauseSignatureImpl = new CatchClauseSignatureImpl(str);
        catchClauseSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return catchClauseSignatureImpl;
    }

    public CatchClauseSignature makeCatchClauseSig(String str, String str2, String str3) {
        Object catchClauseSignatureImpl = new CatchClauseSignatureImpl(makeClass(str, this.lookupClassLoader), makeClass(new StringTokenizer(str2, ":").nextToken(), this.lookupClassLoader), new StringTokenizer(str3, ":").nextToken());
        catchClauseSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return catchClauseSignatureImpl;
    }

    public ConstructorSignature makeConstructorSig(int i, Class cls, Class[] clsArr, String[] strArr, Class[] clsArr2) {
        Object constructorSignatureImpl = new ConstructorSignatureImpl(i, cls, clsArr, strArr, clsArr2);
        constructorSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return constructorSignatureImpl;
    }

    public ConstructorSignature makeConstructorSig(String str) {
        Object constructorSignatureImpl = new ConstructorSignatureImpl(str);
        constructorSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return constructorSignatureImpl;
    }

    public ConstructorSignature makeConstructorSig(String str, String str2, String str3, String str4, String str5) {
        int i = 0;
        int parseInt = Integer.parseInt(str, 16);
        Class makeClass = makeClass(str2, this.lookupClassLoader);
        StringTokenizer stringTokenizer = new StringTokenizer(str3, ":");
        int countTokens = stringTokenizer.countTokens();
        Class[] clsArr = new Class[countTokens];
        for (int i2 = 0; i2 < countTokens; i2++) {
            clsArr[i2] = makeClass(stringTokenizer.nextToken(), this.lookupClassLoader);
        }
        StringTokenizer stringTokenizer2 = new StringTokenizer(str4, ":");
        int countTokens2 = stringTokenizer2.countTokens();
        String[] strArr = new String[countTokens2];
        for (int i3 = 0; i3 < countTokens2; i3++) {
            strArr[i3] = stringTokenizer2.nextToken();
        }
        stringTokenizer2 = new StringTokenizer(str5, ":");
        countTokens2 = stringTokenizer2.countTokens();
        Class[] clsArr2 = new Class[countTokens2];
        while (i < countTokens2) {
            clsArr2[i] = makeClass(stringTokenizer2.nextToken(), this.lookupClassLoader);
            i++;
        }
        Object constructorSignatureImpl = new ConstructorSignatureImpl(parseInt, makeClass, clsArr, strArr, clsArr2);
        constructorSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return constructorSignatureImpl;
    }

    public EnclosingStaticPart makeESJP(String str, Signature signature, int i) {
        int i2 = this.count;
        this.count = i2 + 1;
        return new EnclosingStaticPartImpl(i2, str, signature, makeSourceLoc(i, -1));
    }

    public EnclosingStaticPart makeESJP(String str, Signature signature, int i, int i2) {
        int i3 = this.count;
        this.count = i3 + 1;
        return new EnclosingStaticPartImpl(i3, str, signature, makeSourceLoc(i, i2));
    }

    public EnclosingStaticPart makeESJP(String str, Signature signature, SourceLocation sourceLocation) {
        int i = this.count;
        this.count = i + 1;
        return new EnclosingStaticPartImpl(i, str, signature, sourceLocation);
    }

    public FieldSignature makeFieldSig(int i, String str, Class cls, Class cls2) {
        Object fieldSignatureImpl = new FieldSignatureImpl(i, str, cls, cls2);
        fieldSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return fieldSignatureImpl;
    }

    public FieldSignature makeFieldSig(String str) {
        Object fieldSignatureImpl = new FieldSignatureImpl(str);
        fieldSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return fieldSignatureImpl;
    }

    public FieldSignature makeFieldSig(String str, String str2, String str3, String str4) {
        Object fieldSignatureImpl = new FieldSignatureImpl(Integer.parseInt(str, 16), str2, makeClass(str3, this.lookupClassLoader), makeClass(str4, this.lookupClassLoader));
        fieldSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return fieldSignatureImpl;
    }

    public InitializerSignature makeInitializerSig(int i, Class cls) {
        Object initializerSignatureImpl = new InitializerSignatureImpl(i, cls);
        initializerSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return initializerSignatureImpl;
    }

    public InitializerSignature makeInitializerSig(String str) {
        Object initializerSignatureImpl = new InitializerSignatureImpl(str);
        initializerSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return initializerSignatureImpl;
    }

    public InitializerSignature makeInitializerSig(String str, String str2) {
        Object initializerSignatureImpl = new InitializerSignatureImpl(Integer.parseInt(str, 16), makeClass(str2, this.lookupClassLoader));
        initializerSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return initializerSignatureImpl;
    }

    public LockSignature makeLockSig() {
        Object lockSignatureImpl = new LockSignatureImpl(makeClass("Ljava/lang/Object;", this.lookupClassLoader));
        lockSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return lockSignatureImpl;
    }

    public LockSignature makeLockSig(Class cls) {
        Object lockSignatureImpl = new LockSignatureImpl(cls);
        lockSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return lockSignatureImpl;
    }

    public LockSignature makeLockSig(String str) {
        Object lockSignatureImpl = new LockSignatureImpl(str);
        lockSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return lockSignatureImpl;
    }

    public MethodSignature makeMethodSig(int i, String str, Class cls, Class[] clsArr, String[] strArr, Class[] clsArr2, Class cls2) {
        Object methodSignatureImpl = new MethodSignatureImpl(i, str, cls, clsArr, strArr, clsArr2, cls2);
        methodSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return methodSignatureImpl;
    }

    public MethodSignature makeMethodSig(String str) {
        Object methodSignatureImpl = new MethodSignatureImpl(str);
        methodSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return methodSignatureImpl;
    }

    public MethodSignature makeMethodSig(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        int i;
        int parseInt = Integer.parseInt(str, 16);
        Class makeClass = makeClass(str3, this.lookupClassLoader);
        StringTokenizer stringTokenizer = new StringTokenizer(str4, ":");
        int countTokens = stringTokenizer.countTokens();
        Class[] clsArr = new Class[countTokens];
        for (i = 0; i < countTokens; i++) {
            clsArr[i] = makeClass(stringTokenizer.nextToken(), this.lookupClassLoader);
        }
        stringTokenizer = new StringTokenizer(str5, ":");
        int countTokens2 = stringTokenizer.countTokens();
        String[] strArr = new String[countTokens2];
        for (i = 0; i < countTokens2; i++) {
            strArr[i] = stringTokenizer.nextToken();
        }
        stringTokenizer = new StringTokenizer(str6, ":");
        int countTokens3 = stringTokenizer.countTokens();
        Class[] clsArr2 = new Class[countTokens3];
        for (i = 0; i < countTokens3; i++) {
            clsArr2[i] = makeClass(stringTokenizer.nextToken(), this.lookupClassLoader);
        }
        return new MethodSignatureImpl(parseInt, str2, makeClass, clsArr, strArr, clsArr2, makeClass(str7, this.lookupClassLoader));
    }

    public StaticPart makeSJP(String str, String str2, String str3, String str4, String str5, String str6, String str7, int i) {
        Signature makeMethodSig = makeMethodSig(str2, str3, str4, str5, str6, TtmlNode.ANONYMOUS_REGION_ID, str7);
        int i2 = this.count;
        this.count = i2 + 1;
        return new StaticPartImpl(i2, str, makeMethodSig, makeSourceLoc(i, -1));
    }

    public StaticPart makeSJP(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i) {
        Signature makeMethodSig = makeMethodSig(str2, str3, str4, str5, str6, str7, str8);
        int i2 = this.count;
        this.count = i2 + 1;
        return new StaticPartImpl(i2, str, makeMethodSig, makeSourceLoc(i, -1));
    }

    public StaticPart makeSJP(String str, Signature signature, int i) {
        int i2 = this.count;
        this.count = i2 + 1;
        return new StaticPartImpl(i2, str, signature, makeSourceLoc(i, -1));
    }

    public StaticPart makeSJP(String str, Signature signature, int i, int i2) {
        int i3 = this.count;
        this.count = i3 + 1;
        return new StaticPartImpl(i3, str, signature, makeSourceLoc(i, i2));
    }

    public StaticPart makeSJP(String str, Signature signature, SourceLocation sourceLocation) {
        int i = this.count;
        this.count = i + 1;
        return new StaticPartImpl(i, str, signature, sourceLocation);
    }

    public SourceLocation makeSourceLoc(int i, int i2) {
        return new SourceLocationImpl(this.lexicalClass, this.filename, i);
    }

    public UnlockSignature makeUnlockSig() {
        Object unlockSignatureImpl = new UnlockSignatureImpl(makeClass("Ljava/lang/Object;", this.lookupClassLoader));
        unlockSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return unlockSignatureImpl;
    }

    public UnlockSignature makeUnlockSig(Class cls) {
        Object unlockSignatureImpl = new UnlockSignatureImpl(cls);
        unlockSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return unlockSignatureImpl;
    }

    public UnlockSignature makeUnlockSig(String str) {
        Object unlockSignatureImpl = new UnlockSignatureImpl(str);
        unlockSignatureImpl.setLookupClassLoader(this.lookupClassLoader);
        return unlockSignatureImpl;
    }
}
