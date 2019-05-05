package org.aspectj.runtime.internal;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import org.aspectj.lang.ProceedingJoinPoint;

public abstract class AroundClosure {
    protected int bitflags;
    protected Object[] preInitializationState;
    protected Object[] state;

    public AroundClosure() {
        this.bitflags = AccessibilityNodeInfoCompat.ACTION_DISMISS;
    }

    public AroundClosure(Object[] objArr) {
        this.bitflags = AccessibilityNodeInfoCompat.ACTION_DISMISS;
        this.state = objArr;
    }

    public int getFlags() {
        return this.bitflags;
    }

    public Object[] getPreInitializationState() {
        return this.preInitializationState;
    }

    public Object[] getState() {
        return this.state;
    }

    public ProceedingJoinPoint linkClosureAndJoinPoint() {
        ProceedingJoinPoint proceedingJoinPoint = (ProceedingJoinPoint) this.state[this.state.length - 1];
        proceedingJoinPoint.set$AroundClosure(this);
        return proceedingJoinPoint;
    }

    public ProceedingJoinPoint linkClosureAndJoinPoint(int i) {
        ProceedingJoinPoint proceedingJoinPoint = (ProceedingJoinPoint) this.state[this.state.length - 1];
        proceedingJoinPoint.set$AroundClosure(this);
        this.bitflags = i;
        return proceedingJoinPoint;
    }

    public abstract Object run(Object[] objArr);
}
