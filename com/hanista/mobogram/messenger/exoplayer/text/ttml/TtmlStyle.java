package com.hanista.mobogram.messenger.exoplayer.text.ttml;

import android.text.Layout.Alignment;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;

final class TtmlStyle {
    public static final int FONT_SIZE_UNIT_EM = 2;
    public static final int FONT_SIZE_UNIT_PERCENT = 3;
    public static final int FONT_SIZE_UNIT_PIXEL = 1;
    private static final int OFF = 0;
    private static final int ON = 1;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_BOLD_ITALIC = 3;
    public static final int STYLE_ITALIC = 2;
    public static final int STYLE_NORMAL = 0;
    public static final int UNSPECIFIED = -1;
    private int backgroundColor;
    private int bold;
    private int fontColor;
    private String fontFamily;
    private float fontSize;
    private int fontSizeUnit;
    private boolean hasBackgroundColor;
    private boolean hasFontColor;
    private String id;
    private TtmlStyle inheritableStyle;
    private int italic;
    private int linethrough;
    private Alignment textAlign;
    private int underline;

    public TtmlStyle() {
        this.linethrough = UNSPECIFIED;
        this.underline = UNSPECIFIED;
        this.bold = UNSPECIFIED;
        this.italic = UNSPECIFIED;
        this.fontSizeUnit = UNSPECIFIED;
    }

    private TtmlStyle inherit(TtmlStyle ttmlStyle, boolean z) {
        if (ttmlStyle != null) {
            if (!this.hasFontColor && ttmlStyle.hasFontColor) {
                setFontColor(ttmlStyle.fontColor);
            }
            if (this.bold == UNSPECIFIED) {
                this.bold = ttmlStyle.bold;
            }
            if (this.italic == UNSPECIFIED) {
                this.italic = ttmlStyle.italic;
            }
            if (this.fontFamily == null) {
                this.fontFamily = ttmlStyle.fontFamily;
            }
            if (this.linethrough == UNSPECIFIED) {
                this.linethrough = ttmlStyle.linethrough;
            }
            if (this.underline == UNSPECIFIED) {
                this.underline = ttmlStyle.underline;
            }
            if (this.textAlign == null) {
                this.textAlign = ttmlStyle.textAlign;
            }
            if (this.fontSizeUnit == UNSPECIFIED) {
                this.fontSizeUnit = ttmlStyle.fontSizeUnit;
                this.fontSize = ttmlStyle.fontSize;
            }
            if (z && !this.hasBackgroundColor && ttmlStyle.hasBackgroundColor) {
                setBackgroundColor(ttmlStyle.backgroundColor);
            }
        }
        return this;
    }

    public TtmlStyle chain(TtmlStyle ttmlStyle) {
        return inherit(ttmlStyle, true);
    }

    public int getBackgroundColor() {
        if (this.hasBackgroundColor) {
            return this.backgroundColor;
        }
        throw new IllegalStateException("Background color has not been defined.");
    }

    public int getFontColor() {
        if (this.hasFontColor) {
            return this.fontColor;
        }
        throw new IllegalStateException("Font color has not been defined.");
    }

    public String getFontFamily() {
        return this.fontFamily;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public int getFontSizeUnit() {
        return this.fontSizeUnit;
    }

    public String getId() {
        return this.id;
    }

    public int getStyle() {
        int i = STYLE_NORMAL;
        if (this.bold == UNSPECIFIED && this.italic == UNSPECIFIED) {
            return UNSPECIFIED;
        }
        int i2 = this.bold != UNSPECIFIED ? this.bold : STYLE_NORMAL;
        if (this.italic != UNSPECIFIED) {
            i = this.italic;
        }
        return i2 | i;
    }

    public Alignment getTextAlign() {
        return this.textAlign;
    }

    public boolean hasBackgroundColor() {
        return this.hasBackgroundColor;
    }

    public boolean hasFontColor() {
        return this.hasFontColor;
    }

    public TtmlStyle inherit(TtmlStyle ttmlStyle) {
        return inherit(ttmlStyle, false);
    }

    public boolean isLinethrough() {
        return this.linethrough == STYLE_BOLD;
    }

    public boolean isUnderline() {
        return this.underline == STYLE_BOLD;
    }

    public TtmlStyle setBackgroundColor(int i) {
        this.backgroundColor = i;
        this.hasBackgroundColor = true;
        return this;
    }

    public TtmlStyle setBold(boolean z) {
        int i = STYLE_BOLD;
        Assertions.checkState(this.inheritableStyle == null);
        if (!z) {
            i = STYLE_NORMAL;
        }
        this.bold = i;
        return this;
    }

    public TtmlStyle setFontColor(int i) {
        Assertions.checkState(this.inheritableStyle == null);
        this.fontColor = i;
        this.hasFontColor = true;
        return this;
    }

    public TtmlStyle setFontFamily(String str) {
        Assertions.checkState(this.inheritableStyle == null);
        this.fontFamily = str;
        return this;
    }

    public TtmlStyle setFontSize(float f) {
        this.fontSize = f;
        return this;
    }

    public TtmlStyle setFontSizeUnit(int i) {
        this.fontSizeUnit = i;
        return this;
    }

    public TtmlStyle setId(String str) {
        this.id = str;
        return this;
    }

    public TtmlStyle setItalic(boolean z) {
        int i = STYLE_NORMAL;
        Assertions.checkState(this.inheritableStyle == null);
        if (z) {
            i = STYLE_ITALIC;
        }
        this.italic = i;
        return this;
    }

    public TtmlStyle setLinethrough(boolean z) {
        int i = STYLE_BOLD;
        Assertions.checkState(this.inheritableStyle == null);
        if (!z) {
            i = STYLE_NORMAL;
        }
        this.linethrough = i;
        return this;
    }

    public TtmlStyle setTextAlign(Alignment alignment) {
        this.textAlign = alignment;
        return this;
    }

    public TtmlStyle setUnderline(boolean z) {
        int i = STYLE_BOLD;
        Assertions.checkState(this.inheritableStyle == null);
        if (!z) {
            i = STYLE_NORMAL;
        }
        this.underline = i;
        return this;
    }
}
