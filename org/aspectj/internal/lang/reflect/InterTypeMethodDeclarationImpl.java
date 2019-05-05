package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeMethodDeclaration;

public class InterTypeMethodDeclarationImpl extends InterTypeDeclarationImpl implements InterTypeMethodDeclaration {
    private Method baseMethod;
    private AjType<?>[] exceptionTypes;
    private Type[] genericParameterTypes;
    private Type genericReturnType;
    private String name;
    private int parameterAdjustmentFactor;
    private AjType<?>[] parameterTypes;
    private AjType<?> returnType;

    public InterTypeMethodDeclarationImpl(AjType<?> ajType, String str, int i, String str2, Method method) {
        super((AjType) ajType, str, i);
        this.parameterAdjustmentFactor = 1;
        this.name = str2;
        this.baseMethod = method;
    }

    public InterTypeMethodDeclarationImpl(AjType<?> ajType, AjType<?> ajType2, Method method, int i) {
        super((AjType) ajType, (AjType) ajType2, i);
        this.parameterAdjustmentFactor = 1;
        this.parameterAdjustmentFactor = 0;
        this.name = method.getName();
        this.baseMethod = method;
    }

    public AjType<?>[] getExceptionTypes() {
        Class[] exceptionTypes = this.baseMethod.getExceptionTypes();
        AjType<?>[] ajTypeArr = new AjType[exceptionTypes.length];
        for (int i = 0; i < exceptionTypes.length; i++) {
            ajTypeArr[i] = AjTypeSystem.getAjType(exceptionTypes[i]);
        }
        return ajTypeArr;
    }

    public Type[] getGenericParameterTypes() {
        Type[] genericParameterTypes = this.baseMethod.getGenericParameterTypes();
        Type[] typeArr = new AjType[(genericParameterTypes.length - this.parameterAdjustmentFactor)];
        for (int i = this.parameterAdjustmentFactor; i < genericParameterTypes.length; i++) {
            if (genericParameterTypes[i] instanceof Class) {
                typeArr[i - this.parameterAdjustmentFactor] = AjTypeSystem.getAjType((Class) genericParameterTypes[i]);
            } else {
                typeArr[i - this.parameterAdjustmentFactor] = genericParameterTypes[i];
            }
        }
        return typeArr;
    }

    public Type getGenericReturnType() {
        Type genericReturnType = this.baseMethod.getGenericReturnType();
        return genericReturnType instanceof Class ? AjTypeSystem.getAjType((Class) genericReturnType) : genericReturnType;
    }

    public String getName() {
        return this.name;
    }

    public AjType<?>[] getParameterTypes() {
        Class[] parameterTypes = this.baseMethod.getParameterTypes();
        AjType<?>[] ajTypeArr = new AjType[(parameterTypes.length - this.parameterAdjustmentFactor)];
        for (int i = this.parameterAdjustmentFactor; i < parameterTypes.length; i++) {
            ajTypeArr[i - this.parameterAdjustmentFactor] = AjTypeSystem.getAjType(parameterTypes[i]);
        }
        return ajTypeArr;
    }

    public AjType<?> getReturnType() {
        return AjTypeSystem.getAjType(this.baseMethod.getReturnType());
    }

    public TypeVariable<Method>[] getTypeParameters() {
        return this.baseMethod.getTypeParameters();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Modifier.toString(getModifiers()));
        stringBuffer.append(" ");
        stringBuffer.append(getReturnType().toString());
        stringBuffer.append(" ");
        stringBuffer.append(this.targetTypeName);
        stringBuffer.append(".");
        stringBuffer.append(getName());
        stringBuffer.append("(");
        AjType[] parameterTypes = getParameterTypes();
        for (int i = 0; i < parameterTypes.length - 1; i++) {
            stringBuffer.append(parameterTypes[i].toString());
            stringBuffer.append(", ");
        }
        if (parameterTypes.length > 0) {
            stringBuffer.append(parameterTypes[parameterTypes.length - 1].toString());
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }
}
