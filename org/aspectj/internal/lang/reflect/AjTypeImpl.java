package org.aspectj.internal.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.aspectj.internal.lang.annotation.ajcDeclareEoW;
import org.aspectj.internal.lang.annotation.ajcDeclareParents;
import org.aspectj.internal.lang.annotation.ajcDeclarePrecedence;
import org.aspectj.internal.lang.annotation.ajcDeclareSoft;
import org.aspectj.internal.lang.annotation.ajcITD;
import org.aspectj.internal.lang.annotation.ajcPrivileged;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.annotation.DeclareWarning;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.DeclareAnnotation;
import org.aspectj.lang.reflect.DeclareErrorOrWarning;
import org.aspectj.lang.reflect.DeclareParents;
import org.aspectj.lang.reflect.DeclarePrecedence;
import org.aspectj.lang.reflect.DeclareSoft;
import org.aspectj.lang.reflect.InterTypeConstructorDeclaration;
import org.aspectj.lang.reflect.InterTypeFieldDeclaration;
import org.aspectj.lang.reflect.InterTypeMethodDeclaration;
import org.aspectj.lang.reflect.NoSuchAdviceException;
import org.aspectj.lang.reflect.NoSuchPointcutException;
import org.aspectj.lang.reflect.PerClause;
import org.aspectj.lang.reflect.PerClauseKind;
import org.aspectj.lang.reflect.Pointcut;

public class AjTypeImpl<T> implements AjType<T> {
    private static final String ajcMagic = "ajc$";
    private Advice[] advice;
    private Class<T> clazz;
    private Advice[] declaredAdvice;
    private InterTypeConstructorDeclaration[] declaredITDCons;
    private InterTypeFieldDeclaration[] declaredITDFields;
    private InterTypeMethodDeclaration[] declaredITDMethods;
    private Pointcut[] declaredPointcuts;
    private InterTypeConstructorDeclaration[] itdCons;
    private InterTypeFieldDeclaration[] itdFields;
    private InterTypeMethodDeclaration[] itdMethods;
    private Pointcut[] pointcuts;

    public AjTypeImpl(Class<T> cls) {
        this.declaredPointcuts = null;
        this.pointcuts = null;
        this.declaredAdvice = null;
        this.advice = null;
        this.declaredITDMethods = null;
        this.itdMethods = null;
        this.declaredITDFields = null;
        this.itdFields = null;
        this.itdCons = null;
        this.declaredITDCons = null;
        this.clazz = cls;
    }

    private void addAnnotationStyleDeclareParents(List<DeclareParents> list) {
        for (Field field : this.clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(org.aspectj.lang.annotation.DeclareParents.class) && field.getType().isInterface()) {
                list.add(new DeclareParentsImpl(((org.aspectj.lang.annotation.DeclareParents) field.getAnnotation(org.aspectj.lang.annotation.DeclareParents.class)).value(), field.getType().getName(), false, this));
            }
        }
    }

    private void addAnnotationStyleITDFields(List<InterTypeFieldDeclaration> list, boolean z) {
    }

    private void addAnnotationStyleITDMethods(List<InterTypeMethodDeclaration> list, boolean z) {
        if (isAspect()) {
            for (Field field : this.clazz.getDeclaredFields()) {
                if (field.getType().isInterface() && field.isAnnotationPresent(org.aspectj.lang.annotation.DeclareParents.class)) {
                    Class cls = org.aspectj.lang.annotation.DeclareParents.class;
                    if (((org.aspectj.lang.annotation.DeclareParents) field.getAnnotation(cls)).defaultImpl() != cls) {
                        for (Method method : field.getType().getDeclaredMethods()) {
                            if (Modifier.isPublic(method.getModifiers()) || !z) {
                                list.add(new InterTypeMethodDeclarationImpl(this, AjTypeSystem.getAjType(field.getType()), method, 1));
                            }
                        }
                    }
                }
            }
        }
    }

    private Advice asAdvice(Method method) {
        if (method.getAnnotations().length == 0) {
            return null;
        }
        Before before = (Before) method.getAnnotation(Before.class);
        if (before != null) {
            return new AdviceImpl(method, before.value(), AdviceKind.BEFORE);
        }
        After after = (After) method.getAnnotation(After.class);
        if (after != null) {
            return new AdviceImpl(method, after.value(), AdviceKind.AFTER);
        }
        AfterReturning afterReturning = (AfterReturning) method.getAnnotation(AfterReturning.class);
        String pointcut;
        if (afterReturning != null) {
            pointcut = afterReturning.pointcut();
            if (pointcut.equals(TtmlNode.ANONYMOUS_REGION_ID)) {
                pointcut = afterReturning.value();
            }
            return new AdviceImpl(method, pointcut, AdviceKind.AFTER_RETURNING, afterReturning.returning());
        }
        AfterThrowing afterThrowing = (AfterThrowing) method.getAnnotation(AfterThrowing.class);
        if (afterThrowing != null) {
            pointcut = afterThrowing.pointcut();
            if (pointcut == null) {
                pointcut = afterThrowing.value();
            }
            return new AdviceImpl(method, pointcut, AdviceKind.AFTER_THROWING, afterThrowing.throwing());
        }
        Around around = (Around) method.getAnnotation(Around.class);
        return around != null ? new AdviceImpl(method, around.value(), AdviceKind.AROUND) : null;
    }

    private Pointcut asPointcut(Method method) {
        org.aspectj.lang.annotation.Pointcut pointcut = (org.aspectj.lang.annotation.Pointcut) method.getAnnotation(org.aspectj.lang.annotation.Pointcut.class);
        if (pointcut == null) {
            return null;
        }
        String name = method.getName();
        if (name.startsWith(ajcMagic)) {
            name = name.substring(name.indexOf("$$") + 2, name.length());
            int indexOf = name.indexOf("$");
            if (indexOf != -1) {
                name = name.substring(0, indexOf);
            }
        }
        return new PointcutImpl(name, pointcut.value(), method, AjTypeSystem.getAjType(method.getDeclaringClass()), pointcut.argNames());
    }

    private Advice[] getAdvice(Set set) {
        if (this.advice == null) {
            initAdvice();
        }
        List arrayList = new ArrayList();
        for (Advice advice : this.advice) {
            if (set.contains(advice.getKind())) {
                arrayList.add(advice);
            }
        }
        Advice[] adviceArr = new Advice[arrayList.size()];
        arrayList.toArray(adviceArr);
        return adviceArr;
    }

    private Advice[] getDeclaredAdvice(Set set) {
        if (this.declaredAdvice == null) {
            initDeclaredAdvice();
        }
        List arrayList = new ArrayList();
        for (Advice advice : this.declaredAdvice) {
            if (set.contains(advice.getKind())) {
                arrayList.add(advice);
            }
        }
        Advice[] adviceArr = new Advice[arrayList.size()];
        arrayList.toArray(adviceArr);
        return adviceArr;
    }

    private void initAdvice() {
        Method[] methods = this.clazz.getMethods();
        List arrayList = new ArrayList();
        for (Method asAdvice : methods) {
            Advice asAdvice2 = asAdvice(asAdvice);
            if (asAdvice2 != null) {
                arrayList.add(asAdvice2);
            }
        }
        this.advice = new Advice[arrayList.size()];
        arrayList.toArray(this.advice);
    }

    private void initDeclaredAdvice() {
        Method[] declaredMethods = this.clazz.getDeclaredMethods();
        List arrayList = new ArrayList();
        for (Method asAdvice : declaredMethods) {
            Advice asAdvice2 = asAdvice(asAdvice);
            if (asAdvice2 != null) {
                arrayList.add(asAdvice2);
            }
        }
        this.declaredAdvice = new Advice[arrayList.size()];
        arrayList.toArray(this.declaredAdvice);
    }

    private boolean isReallyAMethod(Method method) {
        return method.getName().startsWith(ajcMagic) ? false : method.getAnnotations().length == 0 ? true : (method.isAnnotationPresent(org.aspectj.lang.annotation.Pointcut.class) || method.isAnnotationPresent(Before.class) || method.isAnnotationPresent(After.class) || method.isAnnotationPresent(AfterReturning.class) || method.isAnnotationPresent(AfterThrowing.class) || method.isAnnotationPresent(Around.class)) ? false : true;
    }

    private AjType<?>[] toAjTypeArray(Class<?>[] clsArr) {
        AjType<?>[] ajTypeArr = new AjType[clsArr.length];
        for (int i = 0; i < ajTypeArr.length; i++) {
            ajTypeArr[i] = AjTypeSystem.getAjType(clsArr[i]);
        }
        return ajTypeArr;
    }

    private Class<?>[] toClassArray(AjType<?>[] ajTypeArr) {
        Class<?>[] clsArr = new Class[ajTypeArr.length];
        for (int i = 0; i < clsArr.length; i++) {
            clsArr[i] = ajTypeArr[i].getJavaClass();
        }
        return clsArr;
    }

    public boolean equals(Object obj) {
        return !(obj instanceof AjTypeImpl) ? false : ((AjTypeImpl) obj).clazz.equals(this.clazz);
    }

    public Advice getAdvice(String str) {
        if (str.equals(TtmlNode.ANONYMOUS_REGION_ID)) {
            throw new IllegalArgumentException("use getAdvice(AdviceType...) instead for un-named advice");
        }
        if (this.advice == null) {
            initAdvice();
        }
        for (Advice advice : this.advice) {
            if (advice.getName().equals(str)) {
                return advice;
            }
        }
        throw new NoSuchAdviceException(str);
    }

    public Advice[] getAdvice(AdviceKind... adviceKindArr) {
        Set allOf;
        if (adviceKindArr.length == 0) {
            allOf = EnumSet.allOf(AdviceKind.class);
        } else {
            allOf = EnumSet.noneOf(AdviceKind.class);
            allOf.addAll(Arrays.asList(adviceKindArr));
        }
        return getAdvice(allOf);
    }

    public AjType<?>[] getAjTypes() {
        return toAjTypeArray(this.clazz.getClasses());
    }

    public <A extends Annotation> A getAnnotation(Class<A> cls) {
        return this.clazz.getAnnotation(cls);
    }

    public Annotation[] getAnnotations() {
        return this.clazz.getAnnotations();
    }

    public Constructor getConstructor(AjType<?>... ajTypeArr) {
        return this.clazz.getConstructor(toClassArray(ajTypeArr));
    }

    public Constructor[] getConstructors() {
        return this.clazz.getConstructors();
    }

    public DeclareAnnotation[] getDeclareAnnotations() {
        List arrayList = new ArrayList();
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ajcDeclareAnnotation.class)) {
                ajcDeclareAnnotation org_aspectj_internal_lang_annotation_ajcDeclareAnnotation = (ajcDeclareAnnotation) method.getAnnotation(ajcDeclareAnnotation.class);
                for (Annotation annotation : method.getAnnotations()) {
                    if (annotation.annotationType() != ajcDeclareAnnotation.class) {
                        break;
                    }
                }
                Annotation annotation2 = null;
                arrayList.add(new DeclareAnnotationImpl(this, org_aspectj_internal_lang_annotation_ajcDeclareAnnotation.kind(), org_aspectj_internal_lang_annotation_ajcDeclareAnnotation.pattern(), annotation2, org_aspectj_internal_lang_annotation_ajcDeclareAnnotation.annotation()));
            }
        }
        if (getSupertype().isAspect()) {
            arrayList.addAll(Arrays.asList(getSupertype().getDeclareAnnotations()));
        }
        DeclareAnnotation[] declareAnnotationArr = new DeclareAnnotation[arrayList.size()];
        arrayList.toArray(declareAnnotationArr);
        return declareAnnotationArr;
    }

    public DeclareErrorOrWarning[] getDeclareErrorOrWarnings() {
        List arrayList = new ArrayList();
        for (Field field : this.clazz.getDeclaredFields()) {
            try {
                if (field.isAnnotationPresent(DeclareWarning.class)) {
                    DeclareWarning declareWarning = (DeclareWarning) field.getAnnotation(DeclareWarning.class);
                    if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                        arrayList.add(new DeclareErrorOrWarningImpl(declareWarning.value(), (String) field.get(null), false, this));
                    }
                } else if (field.isAnnotationPresent(DeclareError.class)) {
                    DeclareError declareError = (DeclareError) field.getAnnotation(DeclareError.class);
                    if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                        arrayList.add(new DeclareErrorOrWarningImpl(declareError.value(), (String) field.get(null), true, this));
                    }
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e2) {
            }
        }
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ajcDeclareEoW.class)) {
                ajcDeclareEoW org_aspectj_internal_lang_annotation_ajcDeclareEoW = (ajcDeclareEoW) method.getAnnotation(ajcDeclareEoW.class);
                arrayList.add(new DeclareErrorOrWarningImpl(org_aspectj_internal_lang_annotation_ajcDeclareEoW.pointcut(), org_aspectj_internal_lang_annotation_ajcDeclareEoW.message(), org_aspectj_internal_lang_annotation_ajcDeclareEoW.isError(), this));
            }
        }
        DeclareErrorOrWarning[] declareErrorOrWarningArr = new DeclareErrorOrWarning[arrayList.size()];
        arrayList.toArray(declareErrorOrWarningArr);
        return declareErrorOrWarningArr;
    }

    public DeclareParents[] getDeclareParents() {
        List arrayList = new ArrayList();
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ajcDeclareParents.class)) {
                ajcDeclareParents org_aspectj_internal_lang_annotation_ajcDeclareParents = (ajcDeclareParents) method.getAnnotation(ajcDeclareParents.class);
                arrayList.add(new DeclareParentsImpl(org_aspectj_internal_lang_annotation_ajcDeclareParents.targetTypePattern(), org_aspectj_internal_lang_annotation_ajcDeclareParents.parentTypes(), org_aspectj_internal_lang_annotation_ajcDeclareParents.isExtends(), this));
            }
        }
        addAnnotationStyleDeclareParents(arrayList);
        if (getSupertype().isAspect()) {
            arrayList.addAll(Arrays.asList(getSupertype().getDeclareParents()));
        }
        DeclareParents[] declareParentsArr = new DeclareParents[arrayList.size()];
        arrayList.toArray(declareParentsArr);
        return declareParentsArr;
    }

    public DeclarePrecedence[] getDeclarePrecedence() {
        List arrayList = new ArrayList();
        if (this.clazz.isAnnotationPresent(org.aspectj.lang.annotation.DeclarePrecedence.class)) {
            arrayList.add(new DeclarePrecedenceImpl(((org.aspectj.lang.annotation.DeclarePrecedence) this.clazz.getAnnotation(org.aspectj.lang.annotation.DeclarePrecedence.class)).value(), this));
        }
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ajcDeclarePrecedence.class)) {
                arrayList.add(new DeclarePrecedenceImpl(((ajcDeclarePrecedence) method.getAnnotation(ajcDeclarePrecedence.class)).value(), this));
            }
        }
        if (getSupertype().isAspect()) {
            arrayList.addAll(Arrays.asList(getSupertype().getDeclarePrecedence()));
        }
        DeclarePrecedence[] declarePrecedenceArr = new DeclarePrecedence[arrayList.size()];
        arrayList.toArray(declarePrecedenceArr);
        return declarePrecedenceArr;
    }

    public DeclareSoft[] getDeclareSofts() {
        List arrayList = new ArrayList();
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ajcDeclareSoft.class)) {
                ajcDeclareSoft org_aspectj_internal_lang_annotation_ajcDeclareSoft = (ajcDeclareSoft) method.getAnnotation(ajcDeclareSoft.class);
                arrayList.add(new DeclareSoftImpl(this, org_aspectj_internal_lang_annotation_ajcDeclareSoft.pointcut(), org_aspectj_internal_lang_annotation_ajcDeclareSoft.exceptionType()));
            }
        }
        if (getSupertype().isAspect()) {
            arrayList.addAll(Arrays.asList(getSupertype().getDeclareSofts()));
        }
        DeclareSoft[] declareSoftArr = new DeclareSoft[arrayList.size()];
        arrayList.toArray(declareSoftArr);
        return declareSoftArr;
    }

    public Advice getDeclaredAdvice(String str) {
        if (str.equals(TtmlNode.ANONYMOUS_REGION_ID)) {
            throw new IllegalArgumentException("use getAdvice(AdviceType...) instead for un-named advice");
        }
        if (this.declaredAdvice == null) {
            initDeclaredAdvice();
        }
        for (Advice advice : this.declaredAdvice) {
            if (advice.getName().equals(str)) {
                return advice;
            }
        }
        throw new NoSuchAdviceException(str);
    }

    public Advice[] getDeclaredAdvice(AdviceKind... adviceKindArr) {
        Set allOf;
        if (adviceKindArr.length == 0) {
            allOf = EnumSet.allOf(AdviceKind.class);
        } else {
            allOf = EnumSet.noneOf(AdviceKind.class);
            allOf.addAll(Arrays.asList(adviceKindArr));
        }
        return getDeclaredAdvice(allOf);
    }

    public AjType<?>[] getDeclaredAjTypes() {
        return toAjTypeArray(this.clazz.getDeclaredClasses());
    }

    public Annotation[] getDeclaredAnnotations() {
        return this.clazz.getDeclaredAnnotations();
    }

    public Constructor getDeclaredConstructor(AjType<?>... ajTypeArr) {
        return this.clazz.getDeclaredConstructor(toClassArray(ajTypeArr));
    }

    public Constructor[] getDeclaredConstructors() {
        return this.clazz.getDeclaredConstructors();
    }

    public Field getDeclaredField(String str) {
        Field declaredField = this.clazz.getDeclaredField(str);
        if (!declaredField.getName().startsWith(ajcMagic)) {
            return declaredField;
        }
        throw new NoSuchFieldException(str);
    }

    public Field[] getDeclaredFields() {
        Field[] declaredFields = this.clazz.getDeclaredFields();
        List arrayList = new ArrayList();
        for (Field field : declaredFields) {
            if (!(field.getName().startsWith(ajcMagic) || field.isAnnotationPresent(DeclareWarning.class) || field.isAnnotationPresent(DeclareError.class))) {
                arrayList.add(field);
            }
        }
        Field[] fieldArr = new Field[arrayList.size()];
        arrayList.toArray(fieldArr);
        return fieldArr;
    }

    public InterTypeConstructorDeclaration getDeclaredITDConstructor(AjType<?> ajType, AjType<?>... ajTypeArr) {
        for (InterTypeConstructorDeclaration interTypeConstructorDeclaration : getDeclaredITDConstructors()) {
            try {
                if (interTypeConstructorDeclaration.getTargetType().equals(ajType)) {
                    AjType[] parameterTypes = interTypeConstructorDeclaration.getParameterTypes();
                    if (parameterTypes.length == ajTypeArr.length) {
                        int i = 0;
                        while (i < parameterTypes.length) {
                            if (parameterTypes[i].equals(ajTypeArr[i])) {
                                i++;
                            }
                        }
                        return interTypeConstructorDeclaration;
                    }
                    continue;
                } else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException();
    }

    public InterTypeConstructorDeclaration[] getDeclaredITDConstructors() {
        if (this.declaredITDCons == null) {
            List arrayList = new ArrayList();
            for (Method method : this.clazz.getDeclaredMethods()) {
                if (method.getName().contains("ajc$postInterConstructor") && method.isAnnotationPresent(ajcITD.class)) {
                    ajcITD org_aspectj_internal_lang_annotation_ajcITD = (ajcITD) method.getAnnotation(ajcITD.class);
                    arrayList.add(new InterTypeConstructorDeclarationImpl(this, org_aspectj_internal_lang_annotation_ajcITD.targetType(), org_aspectj_internal_lang_annotation_ajcITD.modifiers(), method));
                }
            }
            this.declaredITDCons = new InterTypeConstructorDeclaration[arrayList.size()];
            arrayList.toArray(this.declaredITDCons);
        }
        return this.declaredITDCons;
    }

    public InterTypeFieldDeclaration getDeclaredITDField(String str, AjType<?> ajType) {
        for (InterTypeFieldDeclaration interTypeFieldDeclaration : getDeclaredITDFields()) {
            if (interTypeFieldDeclaration.getName().equals(str)) {
                try {
                    if (interTypeFieldDeclaration.getTargetType().equals(ajType)) {
                        return interTypeFieldDeclaration;
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        }
        throw new NoSuchFieldException(str);
    }

    public InterTypeFieldDeclaration[] getDeclaredITDFields() {
        List arrayList = new ArrayList();
        if (this.declaredITDFields == null) {
            for (Method method : this.clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ajcITD.class) && method.getName().contains("ajc$interFieldInit")) {
                    ajcITD org_aspectj_internal_lang_annotation_ajcITD = (ajcITD) method.getAnnotation(ajcITD.class);
                    try {
                        Method declaredMethod = this.clazz.getDeclaredMethod(method.getName().replace("FieldInit", "FieldGetDispatch"), method.getParameterTypes());
                        arrayList.add(new InterTypeFieldDeclarationImpl(this, org_aspectj_internal_lang_annotation_ajcITD.targetType(), org_aspectj_internal_lang_annotation_ajcITD.modifiers(), org_aspectj_internal_lang_annotation_ajcITD.name(), AjTypeSystem.getAjType(declaredMethod.getReturnType()), declaredMethod.getGenericReturnType()));
                    } catch (NoSuchMethodException e) {
                        throw new IllegalStateException("Can't find field get dispatch method for " + method.getName());
                    }
                }
            }
            addAnnotationStyleITDFields(arrayList, false);
            this.declaredITDFields = new InterTypeFieldDeclaration[arrayList.size()];
            arrayList.toArray(this.declaredITDFields);
        }
        return this.declaredITDFields;
    }

    public InterTypeMethodDeclaration getDeclaredITDMethod(String str, AjType<?> ajType, AjType<?>... ajTypeArr) {
        for (InterTypeMethodDeclaration interTypeMethodDeclaration : getDeclaredITDMethods()) {
            try {
                if (interTypeMethodDeclaration.getName().equals(str) && interTypeMethodDeclaration.getTargetType().equals(ajType)) {
                    AjType[] parameterTypes = interTypeMethodDeclaration.getParameterTypes();
                    if (parameterTypes.length == ajTypeArr.length) {
                        int i = 0;
                        while (i < parameterTypes.length) {
                            if (parameterTypes[i].equals(ajTypeArr[i])) {
                                i++;
                            }
                        }
                        return interTypeMethodDeclaration;
                    }
                    continue;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException(str);
    }

    public InterTypeMethodDeclaration[] getDeclaredITDMethods() {
        if (this.declaredITDMethods == null) {
            List arrayList = new ArrayList();
            for (Method method : this.clazz.getDeclaredMethods()) {
                if (method.getName().contains("ajc$interMethodDispatch1$") && method.isAnnotationPresent(ajcITD.class)) {
                    ajcITD org_aspectj_internal_lang_annotation_ajcITD = (ajcITD) method.getAnnotation(ajcITD.class);
                    arrayList.add(new InterTypeMethodDeclarationImpl(this, org_aspectj_internal_lang_annotation_ajcITD.targetType(), org_aspectj_internal_lang_annotation_ajcITD.modifiers(), org_aspectj_internal_lang_annotation_ajcITD.name(), method));
                }
            }
            addAnnotationStyleITDMethods(arrayList, false);
            this.declaredITDMethods = new InterTypeMethodDeclaration[arrayList.size()];
            arrayList.toArray(this.declaredITDMethods);
        }
        return this.declaredITDMethods;
    }

    public Method getDeclaredMethod(String str, AjType<?>... ajTypeArr) {
        Method declaredMethod = this.clazz.getDeclaredMethod(str, toClassArray(ajTypeArr));
        if (isReallyAMethod(declaredMethod)) {
            return declaredMethod;
        }
        throw new NoSuchMethodException(str);
    }

    public Method[] getDeclaredMethods() {
        Method[] declaredMethods = this.clazz.getDeclaredMethods();
        List arrayList = new ArrayList();
        for (Method method : declaredMethods) {
            if (isReallyAMethod(method)) {
                arrayList.add(method);
            }
        }
        Method[] methodArr = new Method[arrayList.size()];
        arrayList.toArray(methodArr);
        return methodArr;
    }

    public Pointcut getDeclaredPointcut(String str) {
        for (Pointcut pointcut : getDeclaredPointcuts()) {
            if (pointcut.getName().equals(str)) {
                return pointcut;
            }
        }
        throw new NoSuchPointcutException(str);
    }

    public Pointcut[] getDeclaredPointcuts() {
        if (this.declaredPointcuts != null) {
            return this.declaredPointcuts;
        }
        List arrayList = new ArrayList();
        for (Method asPointcut : this.clazz.getDeclaredMethods()) {
            Pointcut asPointcut2 = asPointcut(asPointcut);
            if (asPointcut2 != null) {
                arrayList.add(asPointcut2);
            }
        }
        Pointcut[] pointcutArr = new Pointcut[arrayList.size()];
        arrayList.toArray(pointcutArr);
        this.declaredPointcuts = pointcutArr;
        return pointcutArr;
    }

    public AjType<?> getDeclaringType() {
        Class declaringClass = this.clazz.getDeclaringClass();
        return declaringClass != null ? new AjTypeImpl(declaringClass) : null;
    }

    public Constructor getEnclosingConstructor() {
        return this.clazz.getEnclosingConstructor();
    }

    public Method getEnclosingMethod() {
        return this.clazz.getEnclosingMethod();
    }

    public AjType<?> getEnclosingType() {
        Class enclosingClass = this.clazz.getEnclosingClass();
        return enclosingClass != null ? new AjTypeImpl(enclosingClass) : null;
    }

    public T[] getEnumConstants() {
        return this.clazz.getEnumConstants();
    }

    public Field getField(String str) {
        Field field = this.clazz.getField(str);
        if (!field.getName().startsWith(ajcMagic)) {
            return field;
        }
        throw new NoSuchFieldException(str);
    }

    public Field[] getFields() {
        Field[] fields = this.clazz.getFields();
        List arrayList = new ArrayList();
        for (Field field : fields) {
            if (!(field.getName().startsWith(ajcMagic) || field.isAnnotationPresent(DeclareWarning.class) || field.isAnnotationPresent(DeclareError.class))) {
                arrayList.add(field);
            }
        }
        Field[] fieldArr = new Field[arrayList.size()];
        arrayList.toArray(fieldArr);
        return fieldArr;
    }

    public Type getGenericSupertype() {
        return this.clazz.getGenericSuperclass();
    }

    public InterTypeConstructorDeclaration getITDConstructor(AjType<?> ajType, AjType<?>... ajTypeArr) {
        for (InterTypeConstructorDeclaration interTypeConstructorDeclaration : getITDConstructors()) {
            try {
                if (interTypeConstructorDeclaration.getTargetType().equals(ajType)) {
                    AjType[] parameterTypes = interTypeConstructorDeclaration.getParameterTypes();
                    if (parameterTypes.length == ajTypeArr.length) {
                        int i = 0;
                        while (i < parameterTypes.length) {
                            if (parameterTypes[i].equals(ajTypeArr[i])) {
                                i++;
                            }
                        }
                        return interTypeConstructorDeclaration;
                    }
                    continue;
                } else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException();
    }

    public InterTypeConstructorDeclaration[] getITDConstructors() {
        if (this.itdCons == null) {
            List arrayList = new ArrayList();
            for (Method method : this.clazz.getMethods()) {
                if (method.getName().contains("ajc$postInterConstructor") && method.isAnnotationPresent(ajcITD.class)) {
                    ajcITD org_aspectj_internal_lang_annotation_ajcITD = (ajcITD) method.getAnnotation(ajcITD.class);
                    if (Modifier.isPublic(org_aspectj_internal_lang_annotation_ajcITD.modifiers())) {
                        arrayList.add(new InterTypeConstructorDeclarationImpl(this, org_aspectj_internal_lang_annotation_ajcITD.targetType(), org_aspectj_internal_lang_annotation_ajcITD.modifiers(), method));
                    }
                }
            }
            this.itdCons = new InterTypeConstructorDeclaration[arrayList.size()];
            arrayList.toArray(this.itdCons);
        }
        return this.itdCons;
    }

    public InterTypeFieldDeclaration getITDField(String str, AjType<?> ajType) {
        for (InterTypeFieldDeclaration interTypeFieldDeclaration : getITDFields()) {
            if (interTypeFieldDeclaration.getName().equals(str)) {
                try {
                    if (interTypeFieldDeclaration.getTargetType().equals(ajType)) {
                        return interTypeFieldDeclaration;
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        }
        throw new NoSuchFieldException(str);
    }

    public InterTypeFieldDeclaration[] getITDFields() {
        List arrayList = new ArrayList();
        if (this.itdFields == null) {
            for (Method method : this.clazz.getMethods()) {
                if (method.isAnnotationPresent(ajcITD.class)) {
                    ajcITD org_aspectj_internal_lang_annotation_ajcITD = (ajcITD) method.getAnnotation(ajcITD.class);
                    if (method.getName().contains("ajc$interFieldInit") && Modifier.isPublic(org_aspectj_internal_lang_annotation_ajcITD.modifiers())) {
                        try {
                            Method declaredMethod = method.getDeclaringClass().getDeclaredMethod(method.getName().replace("FieldInit", "FieldGetDispatch"), method.getParameterTypes());
                            arrayList.add(new InterTypeFieldDeclarationImpl(this, org_aspectj_internal_lang_annotation_ajcITD.targetType(), org_aspectj_internal_lang_annotation_ajcITD.modifiers(), org_aspectj_internal_lang_annotation_ajcITD.name(), AjTypeSystem.getAjType(declaredMethod.getReturnType()), declaredMethod.getGenericReturnType()));
                        } catch (NoSuchMethodException e) {
                            throw new IllegalStateException("Can't find field get dispatch method for " + method.getName());
                        }
                    }
                }
            }
            addAnnotationStyleITDFields(arrayList, true);
            this.itdFields = new InterTypeFieldDeclaration[arrayList.size()];
            arrayList.toArray(this.itdFields);
        }
        return this.itdFields;
    }

    public InterTypeMethodDeclaration getITDMethod(String str, AjType<?> ajType, AjType<?>... ajTypeArr) {
        for (InterTypeMethodDeclaration interTypeMethodDeclaration : getITDMethods()) {
            try {
                if (interTypeMethodDeclaration.getName().equals(str) && interTypeMethodDeclaration.getTargetType().equals(ajType)) {
                    AjType[] parameterTypes = interTypeMethodDeclaration.getParameterTypes();
                    if (parameterTypes.length == ajTypeArr.length) {
                        int i = 0;
                        while (i < parameterTypes.length) {
                            if (parameterTypes[i].equals(ajTypeArr[i])) {
                                i++;
                            }
                        }
                        return interTypeMethodDeclaration;
                    }
                    continue;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException(str);
    }

    public InterTypeMethodDeclaration[] getITDMethods() {
        if (this.itdMethods == null) {
            List arrayList = new ArrayList();
            for (Method method : this.clazz.getDeclaredMethods()) {
                if (method.getName().contains("ajc$interMethod$") && method.isAnnotationPresent(ajcITD.class)) {
                    ajcITD org_aspectj_internal_lang_annotation_ajcITD = (ajcITD) method.getAnnotation(ajcITD.class);
                    if (Modifier.isPublic(org_aspectj_internal_lang_annotation_ajcITD.modifiers())) {
                        arrayList.add(new InterTypeMethodDeclarationImpl(this, org_aspectj_internal_lang_annotation_ajcITD.targetType(), org_aspectj_internal_lang_annotation_ajcITD.modifiers(), org_aspectj_internal_lang_annotation_ajcITD.name(), method));
                    }
                }
            }
            addAnnotationStyleITDMethods(arrayList, true);
            this.itdMethods = new InterTypeMethodDeclaration[arrayList.size()];
            arrayList.toArray(this.itdMethods);
        }
        return this.itdMethods;
    }

    public AjType<?>[] getInterfaces() {
        return toAjTypeArray(this.clazz.getInterfaces());
    }

    public Class<T> getJavaClass() {
        return this.clazz;
    }

    public Method getMethod(String str, AjType<?>... ajTypeArr) {
        Method method = this.clazz.getMethod(str, toClassArray(ajTypeArr));
        if (isReallyAMethod(method)) {
            return method;
        }
        throw new NoSuchMethodException(str);
    }

    public Method[] getMethods() {
        Method[] methods = this.clazz.getMethods();
        List arrayList = new ArrayList();
        for (Method method : methods) {
            if (isReallyAMethod(method)) {
                arrayList.add(method);
            }
        }
        Method[] methodArr = new Method[arrayList.size()];
        arrayList.toArray(methodArr);
        return methodArr;
    }

    public int getModifiers() {
        return this.clazz.getModifiers();
    }

    public String getName() {
        return this.clazz.getName();
    }

    public Package getPackage() {
        return this.clazz.getPackage();
    }

    public PerClause getPerClause() {
        if (!isAspect()) {
            return null;
        }
        String value = ((Aspect) this.clazz.getAnnotation(Aspect.class)).value();
        if (value.equals(TtmlNode.ANONYMOUS_REGION_ID)) {
            return getSupertype().isAspect() ? getSupertype().getPerClause() : new PerClauseImpl(PerClauseKind.SINGLETON);
        } else {
            if (value.startsWith("perthis(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERTHIS, value.substring("perthis(".length(), value.length() - 1));
            }
            if (value.startsWith("pertarget(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERTARGET, value.substring("pertarget(".length(), value.length() - 1));
            }
            if (value.startsWith("percflow(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERCFLOW, value.substring("percflow(".length(), value.length() - 1));
            }
            if (value.startsWith("percflowbelow(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERCFLOWBELOW, value.substring("percflowbelow(".length(), value.length() - 1));
            }
            if (value.startsWith("pertypewithin")) {
                return new TypePatternBasedPerClauseImpl(PerClauseKind.PERTYPEWITHIN, value.substring("pertypewithin(".length(), value.length() - 1));
            }
            throw new IllegalStateException("Per-clause not recognized: " + value);
        }
    }

    public Pointcut getPointcut(String str) {
        for (Pointcut pointcut : getPointcuts()) {
            if (pointcut.getName().equals(str)) {
                return pointcut;
            }
        }
        throw new NoSuchPointcutException(str);
    }

    public Pointcut[] getPointcuts() {
        if (this.pointcuts != null) {
            return this.pointcuts;
        }
        List arrayList = new ArrayList();
        for (Method asPointcut : this.clazz.getMethods()) {
            Pointcut asPointcut2 = asPointcut(asPointcut);
            if (asPointcut2 != null) {
                arrayList.add(asPointcut2);
            }
        }
        Pointcut[] pointcutArr = new Pointcut[arrayList.size()];
        arrayList.toArray(pointcutArr);
        this.pointcuts = pointcutArr;
        return pointcutArr;
    }

    public AjType<? super T> getSupertype() {
        Class superclass = this.clazz.getSuperclass();
        return superclass == null ? null : new AjTypeImpl(superclass);
    }

    public TypeVariable<Class<T>>[] getTypeParameters() {
        return this.clazz.getTypeParameters();
    }

    public int hashCode() {
        return this.clazz.hashCode();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> cls) {
        return this.clazz.isAnnotationPresent(cls);
    }

    public boolean isArray() {
        return this.clazz.isArray();
    }

    public boolean isAspect() {
        return this.clazz.getAnnotation(Aspect.class) != null;
    }

    public boolean isEnum() {
        return this.clazz.isEnum();
    }

    public boolean isInstance(Object obj) {
        return this.clazz.isInstance(obj);
    }

    public boolean isInterface() {
        return this.clazz.isInterface();
    }

    public boolean isLocalClass() {
        return this.clazz.isLocalClass() && !isAspect();
    }

    public boolean isMemberAspect() {
        return this.clazz.isMemberClass() && isAspect();
    }

    public boolean isMemberClass() {
        return this.clazz.isMemberClass() && !isAspect();
    }

    public boolean isPrimitive() {
        return this.clazz.isPrimitive();
    }

    public boolean isPrivileged() {
        return isAspect() && this.clazz.isAnnotationPresent(ajcPrivileged.class);
    }

    public String toString() {
        return getName();
    }
}
