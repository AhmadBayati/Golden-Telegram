package org.aspectj.internal.lang.reflect;

import com.hanista.mobogram.ui.Components.VideoPlayer;
import org.aspectj.lang.reflect.PerClauseKind;
import org.aspectj.lang.reflect.PointcutBasedPerClause;
import org.aspectj.lang.reflect.PointcutExpression;

public class PointcutBasedPerClauseImpl extends PerClauseImpl implements PointcutBasedPerClause {
    private final PointcutExpression pointcutExpression;

    /* renamed from: org.aspectj.internal.lang.reflect.PointcutBasedPerClauseImpl.1 */
    static /* synthetic */ class C19751 {
        static final /* synthetic */ int[] $SwitchMap$org$aspectj$lang$reflect$PerClauseKind;

        static {
            $SwitchMap$org$aspectj$lang$reflect$PerClauseKind = new int[PerClauseKind.values().length];
            try {
                $SwitchMap$org$aspectj$lang$reflect$PerClauseKind[PerClauseKind.PERCFLOW.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$PerClauseKind[PerClauseKind.PERCFLOWBELOW.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$PerClauseKind[PerClauseKind.PERTARGET.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$PerClauseKind[PerClauseKind.PERTHIS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public PointcutBasedPerClauseImpl(PerClauseKind perClauseKind, String str) {
        super(perClauseKind);
        this.pointcutExpression = new PointcutExpressionImpl(str);
    }

    public PointcutExpression getPointcutExpression() {
        return this.pointcutExpression;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        switch (C19751.$SwitchMap$org$aspectj$lang$reflect$PerClauseKind[getKind().ordinal()]) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                stringBuffer.append("percflow(");
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                stringBuffer.append("percflowbelow(");
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                stringBuffer.append("pertarget(");
                break;
            case VideoPlayer.STATE_READY /*4*/:
                stringBuffer.append("perthis(");
                break;
        }
        stringBuffer.append(this.pointcutExpression.asString());
        stringBuffer.append(")");
        return stringBuffer.toString();
    }
}
