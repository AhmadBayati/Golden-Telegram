package org.aspectj.runtime.reflect;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.tgnet.TLRPC;
import org.aspectj.lang.JoinPoint.EnclosingStaticPart;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

class JoinPointImpl implements ProceedingJoinPoint {
    Object _this;
    private AroundClosure arc;
    Object[] args;
    StaticPart staticPart;
    Object target;

    static class StaticPartImpl implements StaticPart {
        private int id;
        String kind;
        Signature signature;
        SourceLocation sourceLocation;

        public StaticPartImpl(int i, String str, Signature signature, SourceLocation sourceLocation) {
            this.kind = str;
            this.signature = signature;
            this.sourceLocation = sourceLocation;
            this.id = i;
        }

        public int getId() {
            return this.id;
        }

        public String getKind() {
            return this.kind;
        }

        public Signature getSignature() {
            return this.signature;
        }

        public SourceLocation getSourceLocation() {
            return this.sourceLocation;
        }

        public final String toLongString() {
            return toString(StringMaker.longStringMaker);
        }

        public final String toShortString() {
            return toString(StringMaker.shortStringMaker);
        }

        public final String toString() {
            return toString(StringMaker.middleStringMaker);
        }

        String toString(StringMaker stringMaker) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(stringMaker.makeKindName(getKind()));
            stringBuffer.append("(");
            stringBuffer.append(((SignatureImpl) getSignature()).toString(stringMaker));
            stringBuffer.append(")");
            return stringBuffer.toString();
        }
    }

    static class EnclosingStaticPartImpl extends StaticPartImpl implements EnclosingStaticPart {
        public EnclosingStaticPartImpl(int i, String str, Signature signature, SourceLocation sourceLocation) {
            super(i, str, signature, sourceLocation);
        }
    }

    public JoinPointImpl(StaticPart staticPart, Object obj, Object obj2, Object[] objArr) {
        this.staticPart = staticPart;
        this._this = obj;
        this.target = obj2;
        this.args = objArr;
    }

    public Object[] getArgs() {
        if (this.args == null) {
            this.args = new Object[0];
        }
        Object obj = new Object[this.args.length];
        System.arraycopy(this.args, 0, obj, 0, this.args.length);
        return obj;
    }

    public String getKind() {
        return this.staticPart.getKind();
    }

    public Signature getSignature() {
        return this.staticPart.getSignature();
    }

    public SourceLocation getSourceLocation() {
        return this.staticPart.getSourceLocation();
    }

    public StaticPart getStaticPart() {
        return this.staticPart;
    }

    public Object getTarget() {
        return this.target;
    }

    public Object getThis() {
        return this._this;
    }

    public Object proceed() {
        return this.arc == null ? null : this.arc.run(this.arc.getState());
    }

    public Object proceed(Object[] objArr) {
        int i = 1;
        if (this.arc == null) {
            return null;
        }
        int flags = this.arc.getFlags();
        if ((AccessibilityNodeInfoCompat.ACTION_DISMISS & flags) != 0) {
        }
        int i2 = (AccessibilityNodeInfoCompat.ACTION_CUT & flags) != 0 ? 1 : 0;
        int i3 = (flags & ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT) != 0 ? 1 : 0;
        int i4 = (flags & TLRPC.USER_FLAG_UNUSED2) != 0 ? 1 : 0;
        int i5 = (flags & 16) != 0 ? 1 : 0;
        flags = (flags & 1) != 0 ? 1 : 0;
        Object[] state = this.arc.getState();
        int i6 = 0 + (i3 != 0 ? 1 : 0);
        int i7 = (i5 == 0 || i2 != 0) ? 0 : 1;
        i6 += i7;
        if (i3 == 0 || i4 == 0) {
            i7 = 0;
        } else {
            state[0] = objArr[0];
            i7 = 1;
        }
        if (i5 == 0 || flags == 0) {
            flags = i7;
        } else if (i2 != 0) {
            flags = (i4 != 0 ? 1 : 0) + 1;
            if (i4 == 0) {
                i = 0;
            }
            state[0] = objArr[i];
        } else {
            flags = (i3 != 0 ? 1 : 0) + 1;
            i7 = i3 != 0 ? 1 : 0;
            if (i3 == 0) {
                i = 0;
            }
            state[i7] = objArr[i];
        }
        for (i = flags; i < objArr.length; i++) {
            state[(i - flags) + i6] = objArr[i];
        }
        return this.arc.run(state);
    }

    public void set$AroundClosure(AroundClosure aroundClosure) {
        this.arc = aroundClosure;
    }

    public final String toLongString() {
        return this.staticPart.toLongString();
    }

    public final String toShortString() {
        return this.staticPart.toShortString();
    }

    public final String toString() {
        return this.staticPart.toString();
    }
}
