package org.aspectj.internal.lang.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AjTypeSystem;

public class StringToType {

    /* renamed from: org.aspectj.internal.lang.reflect.StringToType.1 */
    static class C19761 implements ParameterizedType {
        final /* synthetic */ Class val$baseClass;
        final /* synthetic */ Type[] val$typeParams;

        C19761(Type[] typeArr, Class cls) {
            this.val$typeParams = typeArr;
            this.val$baseClass = cls;
        }

        public Type[] getActualTypeArguments() {
            return this.val$typeParams;
        }

        public Type getOwnerType() {
            return this.val$baseClass.getEnclosingClass();
        }

        public Type getRawType() {
            return this.val$baseClass;
        }
    }

    public static Type[] commaSeparatedListToTypeArray(String str, Class cls) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        Type[] typeArr = new Type[stringTokenizer.countTokens()];
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            int i2 = i + 1;
            typeArr[i] = stringToType(stringTokenizer.nextToken().trim(), cls);
            i = i2;
        }
        return typeArr;
    }

    private static Type makeParameterizedType(String str, Class cls) {
        int indexOf = str.indexOf(60);
        return new C19761(commaSeparatedListToTypeArray(str.substring(indexOf + 1, str.lastIndexOf(62)), cls), Class.forName(str.substring(0, indexOf), false, cls.getClassLoader()));
    }

    public static Type stringToType(String str, Class cls) {
        try {
            return str.indexOf("<") == -1 ? AjTypeSystem.getAjType(Class.forName(str, false, cls.getClassLoader())) : makeParameterizedType(str, cls);
        } catch (ClassNotFoundException e) {
            TypeVariable[] typeParameters = cls.getTypeParameters();
            for (int i = 0; i < typeParameters.length; i++) {
                if (typeParameters[i].getName().equals(str)) {
                    return typeParameters[i];
                }
            }
            throw new ClassNotFoundException(str);
        }
    }
}
