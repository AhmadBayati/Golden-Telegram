package org.aspectj.runtime.reflect;

import java.lang.reflect.Modifier;

class StringMaker {
    static StringMaker longStringMaker;
    static StringMaker middleStringMaker;
    static StringMaker shortStringMaker;
    int cacheOffset;
    boolean includeArgs;
    boolean includeEnclosingPoint;
    boolean includeJoinPointTypeName;
    boolean includeModifiers;
    boolean includeThrows;
    boolean shortKindName;
    boolean shortPrimaryTypeNames;
    boolean shortTypeNames;

    static {
        shortStringMaker = new StringMaker();
        shortStringMaker.shortTypeNames = true;
        shortStringMaker.includeArgs = false;
        shortStringMaker.includeThrows = false;
        shortStringMaker.includeModifiers = false;
        shortStringMaker.shortPrimaryTypeNames = true;
        shortStringMaker.includeJoinPointTypeName = false;
        shortStringMaker.includeEnclosingPoint = false;
        shortStringMaker.cacheOffset = 0;
        middleStringMaker = new StringMaker();
        middleStringMaker.shortTypeNames = true;
        middleStringMaker.includeArgs = true;
        middleStringMaker.includeThrows = false;
        middleStringMaker.includeModifiers = false;
        middleStringMaker.shortPrimaryTypeNames = false;
        shortStringMaker.cacheOffset = 1;
        longStringMaker = new StringMaker();
        longStringMaker.shortTypeNames = false;
        longStringMaker.includeArgs = true;
        longStringMaker.includeThrows = false;
        longStringMaker.includeModifiers = true;
        longStringMaker.shortPrimaryTypeNames = false;
        longStringMaker.shortKindName = false;
        longStringMaker.cacheOffset = 2;
    }

    StringMaker() {
        this.shortTypeNames = true;
        this.includeArgs = true;
        this.includeThrows = false;
        this.includeModifiers = false;
        this.shortPrimaryTypeNames = false;
        this.includeJoinPointTypeName = true;
        this.includeEnclosingPoint = true;
        this.shortKindName = true;
    }

    public void addSignature(StringBuffer stringBuffer, Class[] clsArr) {
        if (clsArr != null) {
            if (this.includeArgs) {
                stringBuffer.append("(");
                addTypeNames(stringBuffer, clsArr);
                stringBuffer.append(")");
            } else if (clsArr.length == 0) {
                stringBuffer.append("()");
            } else {
                stringBuffer.append("(..)");
            }
        }
    }

    public void addThrows(StringBuffer stringBuffer, Class[] clsArr) {
        if (this.includeThrows && clsArr != null && clsArr.length != 0) {
            stringBuffer.append(" throws ");
            addTypeNames(stringBuffer, clsArr);
        }
    }

    public void addTypeNames(StringBuffer stringBuffer, Class[] clsArr) {
        for (int i = 0; i < clsArr.length; i++) {
            if (i > 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(makeTypeName(clsArr[i]));
        }
    }

    String makeKindName(String str) {
        int lastIndexOf = str.lastIndexOf(45);
        return lastIndexOf == -1 ? str : str.substring(lastIndexOf + 1);
    }

    String makeModifiersString(int i) {
        if (!this.includeModifiers) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        String modifier = Modifier.toString(i);
        return modifier.length() == 0 ? TtmlNode.ANONYMOUS_REGION_ID : new StringBuffer().append(modifier).append(" ").toString();
    }

    public String makePrimaryTypeName(Class cls, String str) {
        return makeTypeName(cls, str, this.shortPrimaryTypeNames);
    }

    public String makeTypeName(Class cls) {
        return makeTypeName(cls, cls.getName(), this.shortTypeNames);
    }

    String makeTypeName(Class cls, String str, boolean z) {
        if (cls == null) {
            return "ANONYMOUS";
        }
        if (!cls.isArray()) {
            return z ? stripPackageName(str).replace('$', '.') : str.replace('$', '.');
        } else {
            Class componentType = cls.getComponentType();
            return new StringBuffer().append(makeTypeName(componentType, componentType.getName(), z)).append("[]").toString();
        }
    }

    String stripPackageName(String str) {
        int lastIndexOf = str.lastIndexOf(46);
        return lastIndexOf == -1 ? str : str.substring(lastIndexOf + 1);
    }
}
