package org.aspectj.internal.lang.reflect;

import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.aspectj.lang.annotation.AdviceName;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.PointcutExpression;

public class AdviceImpl implements Advice {
    private static final String AJC_INTERNAL = "org.aspectj.runtime.internal";
    private final Method adviceMethod;
    private AjType[] exceptionTypes;
    private Type[] genericParameterTypes;
    private boolean hasExtraParam;
    private final AdviceKind kind;
    private AjType[] parameterTypes;
    private PointcutExpression pointcutExpression;

    /* renamed from: org.aspectj.internal.lang.reflect.AdviceImpl.1 */
    static /* synthetic */ class C19731 {
        static final /* synthetic */ int[] $SwitchMap$org$aspectj$lang$reflect$AdviceKind;

        static {
            $SwitchMap$org$aspectj$lang$reflect$AdviceKind = new int[AdviceKind.values().length];
            try {
                $SwitchMap$org$aspectj$lang$reflect$AdviceKind[AdviceKind.AFTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$AdviceKind[AdviceKind.AFTER_RETURNING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$AdviceKind[AdviceKind.AFTER_THROWING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$AdviceKind[AdviceKind.AROUND.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$AdviceKind[AdviceKind.BEFORE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    protected AdviceImpl(Method method, String str, AdviceKind adviceKind) {
        this.hasExtraParam = false;
        this.kind = adviceKind;
        this.adviceMethod = method;
        this.pointcutExpression = new PointcutExpressionImpl(str);
    }

    protected AdviceImpl(Method method, String str, AdviceKind adviceKind, String str2) {
        this(method, str, adviceKind);
        this.hasExtraParam = true;
    }

    public AjType getDeclaringType() {
        return AjTypeSystem.getAjType(this.adviceMethod.getDeclaringClass());
    }

    public AjType<?>[] getExceptionTypes() {
        if (this.exceptionTypes == null) {
            Class[] exceptionTypes = this.adviceMethod.getExceptionTypes();
            this.exceptionTypes = new AjType[exceptionTypes.length];
            for (int i = 0; i < exceptionTypes.length; i++) {
                this.exceptionTypes[i] = AjTypeSystem.getAjType(exceptionTypes[i]);
            }
        }
        return this.exceptionTypes;
    }

    public Type[] getGenericParameterTypes() {
        if (this.genericParameterTypes == null) {
            Type[] genericParameterTypes = this.adviceMethod.getGenericParameterTypes();
            int length = genericParameterTypes.length;
            int i = 0;
            int i2 = 0;
            while (i < length) {
                Type type = genericParameterTypes[i];
                int i3 = ((type instanceof Class) && ((Class) type).getPackage().getName().equals(AJC_INTERNAL)) ? i2 + 1 : i2;
                i++;
                i2 = i3;
            }
            this.genericParameterTypes = new Type[(genericParameterTypes.length - i2)];
            for (i2 = 0; i2 < this.genericParameterTypes.length; i2++) {
                if (genericParameterTypes[i2] instanceof Class) {
                    this.genericParameterTypes[i2] = AjTypeSystem.getAjType((Class) genericParameterTypes[i2]);
                } else {
                    this.genericParameterTypes[i2] = genericParameterTypes[i2];
                }
            }
        }
        return this.genericParameterTypes;
    }

    public AdviceKind getKind() {
        return this.kind;
    }

    public String getName() {
        String name = this.adviceMethod.getName();
        if (!name.startsWith("ajc$")) {
            return name;
        }
        AdviceName adviceName = (AdviceName) this.adviceMethod.getAnnotation(AdviceName.class);
        return adviceName != null ? adviceName.value() : TtmlNode.ANONYMOUS_REGION_ID;
    }

    public AjType<?>[] getParameterTypes() {
        if (this.parameterTypes == null) {
            Class[] parameterTypes = this.adviceMethod.getParameterTypes();
            int i = 0;
            for (Class cls : parameterTypes) {
                if (cls.getPackage().getName().equals(AJC_INTERNAL)) {
                    i++;
                }
            }
            this.parameterTypes = new AjType[(parameterTypes.length - i)];
            for (i = 0; i < this.parameterTypes.length; i++) {
                this.parameterTypes[i] = AjTypeSystem.getAjType(parameterTypes[i]);
            }
        }
        return this.parameterTypes;
    }

    public PointcutExpression getPointcutExpression() {
        return this.pointcutExpression;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        if (getName().length() > 0) {
            stringBuffer.append("@AdviceName(\"");
            stringBuffer.append(getName());
            stringBuffer.append("\") ");
        }
        if (getKind() == AdviceKind.AROUND) {
            stringBuffer.append(this.adviceMethod.getGenericReturnType().toString());
            stringBuffer.append(" ");
        }
        switch (C19731.$SwitchMap$org$aspectj$lang$reflect$AdviceKind[getKind().ordinal()]) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                stringBuffer.append("after(");
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                stringBuffer.append("after(");
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                stringBuffer.append("after(");
                break;
            case VideoPlayer.STATE_READY /*4*/:
                stringBuffer.append("around(");
                break;
            case VideoPlayer.STATE_ENDED /*5*/:
                stringBuffer.append("before(");
                break;
        }
        AjType[] parameterTypes = getParameterTypes();
        int length = parameterTypes.length;
        if (this.hasExtraParam) {
            length--;
        }
        for (int i = 0; i < length; i++) {
            stringBuffer.append(parameterTypes[i].getName());
            if (i + 1 < length) {
                stringBuffer.append(",");
            }
        }
        stringBuffer.append(") ");
        switch (C19731.$SwitchMap$org$aspectj$lang$reflect$AdviceKind[getKind().ordinal()]) {
            case VideoPlayer.STATE_PREPARING /*2*/:
                stringBuffer.append("returning");
                if (this.hasExtraParam) {
                    stringBuffer.append("(");
                    stringBuffer.append(parameterTypes[length - 1].getName());
                    stringBuffer.append(") ");
                    break;
                }
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                break;
        }
        stringBuffer.append("throwing");
        if (this.hasExtraParam) {
            stringBuffer.append("(");
            stringBuffer.append(parameterTypes[length - 1].getName());
            stringBuffer.append(") ");
        }
        AjType[] exceptionTypes = getExceptionTypes();
        if (exceptionTypes.length > 0) {
            stringBuffer.append("throws ");
            for (length = 0; length < exceptionTypes.length; length++) {
                stringBuffer.append(exceptionTypes[length].getName());
                if (length + 1 < exceptionTypes.length) {
                    stringBuffer.append(",");
                }
            }
            stringBuffer.append(" ");
        }
        stringBuffer.append(": ");
        stringBuffer.append(getPointcutExpression().asString());
        return stringBuffer.toString();
    }
}
