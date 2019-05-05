package org.aspectj.runtime.internal;

import java.util.Stack;
import java.util.Vector;
import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.runtime.CFlow;
import org.aspectj.runtime.internal.cflowstack.ThreadStack;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactory;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactoryImpl;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactoryImpl11;

public class CFlowStack {
    private static ThreadStackFactory tsFactory;
    private ThreadStack stackProxy;

    static {
        selectFactoryForVMVersion();
    }

    public CFlowStack() {
        this.stackProxy = tsFactory.getNewThreadStack();
    }

    private static String getSystemPropertyWithoutSecurityException(String str, String str2) {
        try {
            str2 = System.getProperty(str, str2);
        } catch (SecurityException e) {
        }
        return str2;
    }

    private static ThreadStackFactory getThreadLocalStackFactory() {
        return new ThreadStackFactoryImpl();
    }

    private static ThreadStackFactory getThreadLocalStackFactoryFor11() {
        return new ThreadStackFactoryImpl11();
    }

    private Stack getThreadStack() {
        return this.stackProxy.getThreadStack();
    }

    public static String getThreadStackFactoryClassName() {
        return tsFactory.getClass().getName();
    }

    private static void selectFactoryForVMVersion() {
        Object obj = 1;
        Object obj2 = null;
        String systemPropertyWithoutSecurityException = getSystemPropertyWithoutSecurityException("aspectj.runtime.cflowstack.usethreadlocal", "unspecified");
        if (!systemPropertyWithoutSecurityException.equals("unspecified")) {
            if (systemPropertyWithoutSecurityException.equals("yes") || systemPropertyWithoutSecurityException.equals("true")) {
                obj2 = 1;
            }
            obj = obj2;
        } else if (System.getProperty("java.class.version", "0.0").compareTo("46.0") < 0) {
            obj = null;
        }
        if (obj != null) {
            tsFactory = getThreadLocalStackFactory();
        } else {
            tsFactory = getThreadLocalStackFactoryFor11();
        }
    }

    public Object get(int i) {
        CFlow peekCFlow = peekCFlow();
        return peekCFlow == null ? null : peekCFlow.get(i);
    }

    public boolean isValid() {
        return !getThreadStack().isEmpty();
    }

    public Object peek() {
        Vector threadStack = getThreadStack();
        if (!threadStack.isEmpty()) {
            return threadStack.peek();
        }
        throw new NoAspectBoundException();
    }

    public CFlow peekCFlow() {
        Vector threadStack = getThreadStack();
        return threadStack.isEmpty() ? null : (CFlow) threadStack.peek();
    }

    public Object peekInstance() {
        CFlow peekCFlow = peekCFlow();
        if (peekCFlow != null) {
            return peekCFlow.getAspect();
        }
        throw new NoAspectBoundException();
    }

    public CFlow peekTopCFlow() {
        Vector threadStack = getThreadStack();
        return threadStack.isEmpty() ? null : (CFlow) threadStack.elementAt(0);
    }

    public void pop() {
        Vector threadStack = getThreadStack();
        threadStack.pop();
        if (threadStack.isEmpty()) {
            this.stackProxy.removeThreadStack();
        }
    }

    public void push(Object obj) {
        getThreadStack().push(obj);
    }

    public void push(Object[] objArr) {
        getThreadStack().push(new CFlowPlusState(objArr));
    }

    public void pushInstance(Object obj) {
        getThreadStack().push(new CFlow(obj));
    }
}
