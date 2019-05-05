package org.aspectj.internal.lang.reflect;

import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.lang.annotation.Annotation;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareAnnotation;
import org.aspectj.lang.reflect.DeclareAnnotation.Kind;
import org.aspectj.lang.reflect.SignaturePattern;
import org.aspectj.lang.reflect.TypePattern;

public class DeclareAnnotationImpl implements DeclareAnnotation {
    private String annText;
    private AjType<?> declaringType;
    private Kind kind;
    private SignaturePattern signaturePattern;
    private Annotation theAnnotation;
    private TypePattern typePattern;

    /* renamed from: org.aspectj.internal.lang.reflect.DeclareAnnotationImpl.1 */
    static /* synthetic */ class C19741 {
        static final /* synthetic */ int[] $SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind;

        static {
            $SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind = new int[Kind.values().length];
            try {
                $SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind[Kind.Type.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind[Kind.Method.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind[Kind.Field.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind[Kind.Constructor.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public DeclareAnnotationImpl(AjType<?> ajType, String str, String str2, Annotation annotation, String str3) {
        this.declaringType = ajType;
        if (str.equals("at_type")) {
            this.kind = Kind.Type;
        } else if (str.equals("at_field")) {
            this.kind = Kind.Field;
        } else if (str.equals("at_method")) {
            this.kind = Kind.Method;
        } else if (str.equals("at_constructor")) {
            this.kind = Kind.Constructor;
        } else {
            throw new IllegalStateException("Unknown declare annotation kind: " + str);
        }
        if (this.kind == Kind.Type) {
            this.typePattern = new TypePatternImpl(str2);
        } else {
            this.signaturePattern = new SignaturePatternImpl(str2);
        }
        this.theAnnotation = annotation;
        this.annText = str3;
    }

    public Annotation getAnnotation() {
        return this.theAnnotation;
    }

    public String getAnnotationAsText() {
        return this.annText;
    }

    public AjType<?> getDeclaringType() {
        return this.declaringType;
    }

    public Kind getKind() {
        return this.kind;
    }

    public SignaturePattern getSignaturePattern() {
        return this.signaturePattern;
    }

    public TypePattern getTypePattern() {
        return this.typePattern;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("declare @");
        switch (C19741.$SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind[getKind().ordinal()]) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                stringBuffer.append("type : ");
                stringBuffer.append(getTypePattern().asString());
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                stringBuffer.append("method : ");
                stringBuffer.append(getSignaturePattern().asString());
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                stringBuffer.append("field : ");
                stringBuffer.append(getSignaturePattern().asString());
                break;
            case VideoPlayer.STATE_READY /*4*/:
                stringBuffer.append("constructor : ");
                stringBuffer.append(getSignaturePattern().asString());
                break;
        }
        stringBuffer.append(" : ");
        stringBuffer.append(getAnnotationAsText());
        return stringBuffer.toString();
    }
}
