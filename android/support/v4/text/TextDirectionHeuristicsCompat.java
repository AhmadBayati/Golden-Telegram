package android.support.v4.text;

import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.tgnet.TLRPC;
import java.nio.CharBuffer;
import java.util.Locale;

public final class TextDirectionHeuristicsCompat {
    public static final TextDirectionHeuristicCompat ANYRTL_LTR;
    public static final TextDirectionHeuristicCompat FIRSTSTRONG_LTR;
    public static final TextDirectionHeuristicCompat FIRSTSTRONG_RTL;
    public static final TextDirectionHeuristicCompat LOCALE;
    public static final TextDirectionHeuristicCompat LTR;
    public static final TextDirectionHeuristicCompat RTL;
    private static final int STATE_FALSE = 1;
    private static final int STATE_TRUE = 0;
    private static final int STATE_UNKNOWN = 2;

    private interface TextDirectionAlgorithm {
        int checkRtl(CharSequence charSequence, int i, int i2);
    }

    private static class AnyStrong implements TextDirectionAlgorithm {
        public static final AnyStrong INSTANCE_LTR;
        public static final AnyStrong INSTANCE_RTL;
        private final boolean mLookForRtl;

        static {
            INSTANCE_RTL = new AnyStrong(true);
            INSTANCE_LTR = new AnyStrong(false);
        }

        private AnyStrong(boolean z) {
            this.mLookForRtl = z;
        }

        public int checkRtl(CharSequence charSequence, int i, int i2) {
            int i3 = i + i2;
            int i4 = TextDirectionHeuristicsCompat.STATE_TRUE;
            while (i < i3) {
                switch (TextDirectionHeuristicsCompat.isRtlText(Character.getDirectionality(charSequence.charAt(i)))) {
                    case TextDirectionHeuristicsCompat.STATE_TRUE /*0*/:
                        if (!this.mLookForRtl) {
                            i4 = TextDirectionHeuristicsCompat.STATE_FALSE;
                            break;
                        }
                        return TextDirectionHeuristicsCompat.STATE_TRUE;
                    case TextDirectionHeuristicsCompat.STATE_FALSE /*1*/:
                        if (this.mLookForRtl) {
                            i4 = TextDirectionHeuristicsCompat.STATE_FALSE;
                            break;
                        }
                        return TextDirectionHeuristicsCompat.STATE_FALSE;
                    default:
                        break;
                }
                i += TextDirectionHeuristicsCompat.STATE_FALSE;
            }
            return i4 != 0 ? !this.mLookForRtl ? TextDirectionHeuristicsCompat.STATE_TRUE : TextDirectionHeuristicsCompat.STATE_FALSE : TextDirectionHeuristicsCompat.STATE_UNKNOWN;
        }
    }

    private static class FirstStrong implements TextDirectionAlgorithm {
        public static final FirstStrong INSTANCE;

        static {
            INSTANCE = new FirstStrong();
        }

        private FirstStrong() {
        }

        public int checkRtl(CharSequence charSequence, int i, int i2) {
            int i3 = i + i2;
            int i4 = TextDirectionHeuristicsCompat.STATE_UNKNOWN;
            while (i < i3 && i4 == TextDirectionHeuristicsCompat.STATE_UNKNOWN) {
                i4 = TextDirectionHeuristicsCompat.isRtlTextOrFormat(Character.getDirectionality(charSequence.charAt(i)));
                i += TextDirectionHeuristicsCompat.STATE_FALSE;
            }
            return i4;
        }
    }

    private static abstract class TextDirectionHeuristicImpl implements TextDirectionHeuristicCompat {
        private final TextDirectionAlgorithm mAlgorithm;

        public TextDirectionHeuristicImpl(TextDirectionAlgorithm textDirectionAlgorithm) {
            this.mAlgorithm = textDirectionAlgorithm;
        }

        private boolean doCheck(CharSequence charSequence, int i, int i2) {
            switch (this.mAlgorithm.checkRtl(charSequence, i, i2)) {
                case TextDirectionHeuristicsCompat.STATE_TRUE /*0*/:
                    return true;
                case TextDirectionHeuristicsCompat.STATE_FALSE /*1*/:
                    return false;
                default:
                    return defaultIsRtl();
            }
        }

        protected abstract boolean defaultIsRtl();

        public boolean isRtl(CharSequence charSequence, int i, int i2) {
            if (charSequence != null && i >= 0 && i2 >= 0 && charSequence.length() - i2 >= i) {
                return this.mAlgorithm == null ? defaultIsRtl() : doCheck(charSequence, i, i2);
            } else {
                throw new IllegalArgumentException();
            }
        }

        public boolean isRtl(char[] cArr, int i, int i2) {
            return isRtl(CharBuffer.wrap(cArr), i, i2);
        }
    }

    private static class TextDirectionHeuristicInternal extends TextDirectionHeuristicImpl {
        private final boolean mDefaultIsRtl;

        TextDirectionHeuristicInternal(TextDirectionAlgorithm textDirectionAlgorithm, boolean z) {
            super(textDirectionAlgorithm);
            this.mDefaultIsRtl = z;
        }

        protected boolean defaultIsRtl() {
            return this.mDefaultIsRtl;
        }
    }

    private static class TextDirectionHeuristicLocale extends TextDirectionHeuristicImpl {
        public static final TextDirectionHeuristicLocale INSTANCE;

        static {
            INSTANCE = new TextDirectionHeuristicLocale();
        }

        public TextDirectionHeuristicLocale() {
            super(null);
        }

        protected boolean defaultIsRtl() {
            return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == TextDirectionHeuristicsCompat.STATE_FALSE;
        }
    }

    static {
        LTR = new TextDirectionHeuristicInternal(null, false);
        RTL = new TextDirectionHeuristicInternal(null, true);
        FIRSTSTRONG_LTR = new TextDirectionHeuristicInternal(FirstStrong.INSTANCE, false);
        FIRSTSTRONG_RTL = new TextDirectionHeuristicInternal(FirstStrong.INSTANCE, true);
        ANYRTL_LTR = new TextDirectionHeuristicInternal(AnyStrong.INSTANCE_RTL, false);
        LOCALE = TextDirectionHeuristicLocale.INSTANCE;
    }

    private TextDirectionHeuristicsCompat() {
    }

    static int isRtlText(int i) {
        switch (i) {
            case STATE_TRUE /*0*/:
                return STATE_FALSE;
            case STATE_FALSE /*1*/:
            case STATE_UNKNOWN /*2*/:
                return STATE_TRUE;
            default:
                return STATE_UNKNOWN;
        }
    }

    static int isRtlTextOrFormat(int i) {
        switch (i) {
            case STATE_TRUE /*0*/:
            case C0338R.styleable.PromptView_primaryTextFontFamily /*14*/:
            case C0338R.styleable.PromptView_primaryTextSize /*15*/:
                return STATE_FALSE;
            case STATE_FALSE /*1*/:
            case STATE_UNKNOWN /*2*/:
            case TLRPC.USER_FLAG_PHONE /*16*/:
            case C0338R.styleable.PromptView_primaryTextTypeface /*17*/:
                return STATE_TRUE;
            default:
                return STATE_UNKNOWN;
        }
    }
}
