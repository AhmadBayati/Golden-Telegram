package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.DeclareSoft;
import org.aspectj.lang.reflect.PointcutExpression;

public class DeclareSoftImpl implements DeclareSoft {
    private AjType<?> declaringType;
    private AjType<?> exceptionType;
    private String missingTypeName;
    private PointcutExpression pointcut;

    public DeclareSoftImpl(AjType<?> ajType, String str, String str2) {
        this.declaringType = ajType;
        this.pointcut = new PointcutExpressionImpl(str);
        try {
            this.exceptionType = AjTypeSystem.getAjType(Class.forName(str2, false, ajType.getJavaClass().getClassLoader()));
        } catch (ClassNotFoundException e) {
            this.missingTypeName = str2;
        }
    }

    public AjType getDeclaringType() {
        return this.declaringType;
    }

    public PointcutExpression getPointcutExpression() {
        return this.pointcut;
    }

    public AjType getSoftenedExceptionType() {
        if (this.missingTypeName == null) {
            return this.exceptionType;
        }
        throw new ClassNotFoundException(this.missingTypeName);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("declare soft : ");
        if (this.missingTypeName != null) {
            stringBuffer.append(this.exceptionType.getName());
        } else {
            stringBuffer.append(this.missingTypeName);
        }
        stringBuffer.append(" : ");
        stringBuffer.append(getPointcutExpression().asString());
        return stringBuffer.toString();
    }
}
