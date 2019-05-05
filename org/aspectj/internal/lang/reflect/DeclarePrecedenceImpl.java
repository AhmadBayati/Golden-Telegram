package org.aspectj.internal.lang.reflect;

import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclarePrecedence;
import org.aspectj.lang.reflect.TypePattern;

public class DeclarePrecedenceImpl implements DeclarePrecedence {
    private AjType<?> declaringType;
    private TypePattern[] precedenceList;
    private String precedenceString;

    public DeclarePrecedenceImpl(String str, AjType ajType) {
        this.declaringType = ajType;
        this.precedenceString = str;
        if (str.startsWith("(")) {
            str = str.substring(1, str.length() - 1);
        }
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        this.precedenceList = new TypePattern[stringTokenizer.countTokens()];
        for (int i = 0; i < this.precedenceList.length; i++) {
            this.precedenceList[i] = new TypePatternImpl(stringTokenizer.nextToken().trim());
        }
    }

    public AjType getDeclaringType() {
        return this.declaringType;
    }

    public TypePattern[] getPrecedenceOrder() {
        return this.precedenceList;
    }

    public String toString() {
        return "declare precedence : " + this.precedenceString;
    }
}
