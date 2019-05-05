package org.aspectj.runtime.reflect;

import java.lang.ref.SoftReference;
import java.util.StringTokenizer;
import org.aspectj.lang.Signature;

abstract class SignatureImpl implements Signature {
    static Class[] EMPTY_CLASS_ARRAY = null;
    static String[] EMPTY_STRING_ARRAY = null;
    static final String INNER_SEP = ":";
    static final char SEP = '-';
    private static boolean useCache;
    Class declaringType;
    String declaringTypeName;
    ClassLoader lookupClassLoader;
    int modifiers;
    String name;
    Cache stringCache;
    private String stringRep;

    private interface Cache {
        String get(int i);

        void set(int i, String str);
    }

    private static final class CacheImpl implements Cache {
        private SoftReference toStringCacheRef;

        public CacheImpl() {
            makeCache();
        }

        private String[] array() {
            return (String[]) this.toStringCacheRef.get();
        }

        private String[] makeCache() {
            Object obj = new String[3];
            this.toStringCacheRef = new SoftReference(obj);
            return obj;
        }

        public String get(int i) {
            String[] array = array();
            return array == null ? null : array[i];
        }

        public void set(int i, String str) {
            String[] array = array();
            if (array == null) {
                array = makeCache();
            }
            array[i] = str;
        }
    }

    static {
        useCache = true;
        EMPTY_STRING_ARRAY = new String[0];
        EMPTY_CLASS_ARRAY = new Class[0];
    }

    SignatureImpl(int i, String str, Class cls) {
        this.modifiers = -1;
        this.lookupClassLoader = null;
        this.modifiers = i;
        this.name = str;
        this.declaringType = cls;
    }

    public SignatureImpl(String str) {
        this.modifiers = -1;
        this.lookupClassLoader = null;
        this.stringRep = str;
    }

    private ClassLoader getLookupClassLoader() {
        if (this.lookupClassLoader == null) {
            this.lookupClassLoader = getClass().getClassLoader();
        }
        return this.lookupClassLoader;
    }

    static boolean getUseCache() {
        return useCache;
    }

    static void setUseCache(boolean z) {
        useCache = z;
    }

    void addFullTypeNames(StringBuffer stringBuffer, Class[] clsArr) {
        for (int i = 0; i < clsArr.length; i++) {
            if (i > 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(fullTypeName(clsArr[i]));
        }
    }

    void addShortTypeNames(StringBuffer stringBuffer, Class[] clsArr) {
        for (int i = 0; i < clsArr.length; i++) {
            if (i > 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(shortTypeName(clsArr[i]));
        }
    }

    void addTypeArray(StringBuffer stringBuffer, Class[] clsArr) {
        addFullTypeNames(stringBuffer, clsArr);
    }

    protected abstract String createToString(StringMaker stringMaker);

    int extractInt(int i) {
        return Integer.parseInt(extractString(i), 16);
    }

    String extractString(int i) {
        int i2 = 0;
        int indexOf = this.stringRep.indexOf(45);
        while (true) {
            int i3 = i - 1;
            if (i <= 0) {
                break;
            }
            i2 = indexOf + 1;
            indexOf = this.stringRep.indexOf(45, i2);
            i = i3;
        }
        if (indexOf == -1) {
            indexOf = this.stringRep.length();
        }
        return this.stringRep.substring(i2, indexOf);
    }

    String[] extractStrings(int i) {
        StringTokenizer stringTokenizer = new StringTokenizer(extractString(i), INNER_SEP);
        int countTokens = stringTokenizer.countTokens();
        String[] strArr = new String[countTokens];
        for (int i2 = 0; i2 < countTokens; i2++) {
            strArr[i2] = stringTokenizer.nextToken();
        }
        return strArr;
    }

    Class extractType(int i) {
        return Factory.makeClass(extractString(i), getLookupClassLoader());
    }

    Class[] extractTypes(int i) {
        StringTokenizer stringTokenizer = new StringTokenizer(extractString(i), INNER_SEP);
        int countTokens = stringTokenizer.countTokens();
        Class[] clsArr = new Class[countTokens];
        for (int i2 = 0; i2 < countTokens; i2++) {
            clsArr[i2] = Factory.makeClass(stringTokenizer.nextToken(), getLookupClassLoader());
        }
        return clsArr;
    }

    String fullTypeName(Class cls) {
        return cls == null ? "ANONYMOUS" : cls.isArray() ? new StringBuffer().append(fullTypeName(cls.getComponentType())).append("[]").toString() : cls.getName().replace('$', '.');
    }

    public Class getDeclaringType() {
        if (this.declaringType == null) {
            this.declaringType = extractType(2);
        }
        return this.declaringType;
    }

    public String getDeclaringTypeName() {
        if (this.declaringTypeName == null) {
            this.declaringTypeName = getDeclaringType().getName();
        }
        return this.declaringTypeName;
    }

    public int getModifiers() {
        if (this.modifiers == -1) {
            this.modifiers = extractInt(0);
        }
        return this.modifiers;
    }

    public String getName() {
        if (this.name == null) {
            this.name = extractString(1);
        }
        return this.name;
    }

    public void setLookupClassLoader(ClassLoader classLoader) {
        this.lookupClassLoader = classLoader;
    }

    String shortTypeName(Class cls) {
        return cls == null ? "ANONYMOUS" : cls.isArray() ? new StringBuffer().append(shortTypeName(cls.getComponentType())).append("[]").toString() : stripPackageName(cls.getName()).replace('$', '.');
    }

    String stripPackageName(String str) {
        int lastIndexOf = str.lastIndexOf(46);
        return lastIndexOf == -1 ? str : str.substring(lastIndexOf + 1);
    }

    public final String toLongString() {
        return toString(StringMaker.longStringMaker);
    }

    public final String toShortString() {
        return toString(StringMaker.shortStringMaker);
    }

    public final String toString() {
        return toString(StringMaker.middleStringMaker);
    }

    String toString(StringMaker stringMaker) {
        String str = null;
        if (useCache) {
            if (this.stringCache == null) {
                try {
                    this.stringCache = new CacheImpl();
                } catch (Throwable th) {
                    useCache = false;
                }
            } else {
                str = this.stringCache.get(stringMaker.cacheOffset);
            }
        }
        if (str == null) {
            str = createToString(stringMaker);
        }
        if (useCache) {
            this.stringCache.set(stringMaker.cacheOffset, str);
        }
        return str;
    }
}
