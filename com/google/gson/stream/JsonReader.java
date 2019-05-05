package com.google.gson.stream;

import android.support.v4.view.MotionEventCompat;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.util.shamsicalendar.ShamsiCalendar;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class JsonReader implements Closeable {
    private static final String FALSE = "false";
    private static final char[] NON_EXECUTE_PREFIX;
    private static final String TRUE = "true";
    private final char[] buffer;
    private int bufferStartColumn;
    private int bufferStartLine;
    private final Reader in;
    private boolean lenient;
    private int limit;
    private String name;
    private int pos;
    private boolean skipping;
    private JsonScope[] stack;
    private int stackSize;
    private final StringPool stringPool;
    private JsonToken token;
    private String value;
    private int valueLength;
    private int valuePos;

    /* renamed from: com.google.gson.stream.JsonReader.1 */
    static class C03151 extends JsonReaderInternalAccess {
        C03151() {
        }

        public void promoteNameToValue(JsonReader jsonReader) {
            if (jsonReader instanceof JsonTreeReader) {
                ((JsonTreeReader) jsonReader).promoteNameToValue();
                return;
            }
            jsonReader.peek();
            if (jsonReader.token != JsonToken.NAME) {
                throw new IllegalStateException("Expected a name but was " + jsonReader.peek() + " " + " at line " + jsonReader.getLineNumber() + " column " + jsonReader.getColumnNumber());
            }
            jsonReader.value = jsonReader.name;
            jsonReader.name = null;
            jsonReader.token = JsonToken.STRING;
        }
    }

    /* renamed from: com.google.gson.stream.JsonReader.2 */
    static /* synthetic */ class C03162 {
        static final /* synthetic */ int[] $SwitchMap$com$google$gson$stream$JsonScope;

        static {
            $SwitchMap$com$google$gson$stream$JsonScope = new int[JsonScope.values().length];
            try {
                $SwitchMap$com$google$gson$stream$JsonScope[JsonScope.EMPTY_DOCUMENT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonScope[JsonScope.EMPTY_ARRAY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonScope[JsonScope.NONEMPTY_ARRAY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonScope[JsonScope.EMPTY_OBJECT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonScope[JsonScope.DANGLING_NAME.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonScope[JsonScope.NONEMPTY_OBJECT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonScope[JsonScope.NONEMPTY_DOCUMENT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonScope[JsonScope.CLOSED.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    static {
        NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
        JsonReaderInternalAccess.INSTANCE = new C03151();
    }

    public JsonReader(Reader reader) {
        this.stringPool = new StringPool();
        this.lenient = false;
        this.buffer = new char[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
        this.pos = 0;
        this.limit = 0;
        this.bufferStartLine = 1;
        this.bufferStartColumn = 1;
        this.stack = new JsonScope[32];
        this.stackSize = 0;
        push(JsonScope.EMPTY_DOCUMENT);
        this.skipping = false;
        if (reader == null) {
            throw new NullPointerException("in == null");
        }
        this.in = reader;
    }

    private JsonToken advance() {
        peek();
        JsonToken jsonToken = this.token;
        this.token = null;
        this.value = null;
        this.name = null;
        return jsonToken;
    }

    private void checkLenient() {
        if (!this.lenient) {
            throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void consumeNonExecutePrefix() {
        nextNonWhitespace(true);
        this.pos--;
        if (this.pos + NON_EXECUTE_PREFIX.length <= this.limit || fillBuffer(NON_EXECUTE_PREFIX.length)) {
            int i = 0;
            while (i < NON_EXECUTE_PREFIX.length) {
                if (this.buffer[this.pos + i] == NON_EXECUTE_PREFIX[i]) {
                    i++;
                } else {
                    return;
                }
            }
            this.pos += NON_EXECUTE_PREFIX.length;
        }
    }

    private JsonToken decodeLiteral() {
        if (this.valuePos == -1) {
            return JsonToken.STRING;
        }
        if (this.valueLength == 4 && (('n' == this.buffer[this.valuePos] || 'N' == this.buffer[this.valuePos]) && (('u' == this.buffer[this.valuePos + 1] || 'U' == this.buffer[this.valuePos + 1]) && (('l' == this.buffer[this.valuePos + 2] || 'L' == this.buffer[this.valuePos + 2]) && ('l' == this.buffer[this.valuePos + 3] || 'L' == this.buffer[this.valuePos + 3]))))) {
            this.value = "null";
            return JsonToken.NULL;
        } else if (this.valueLength == 4 && (('t' == this.buffer[this.valuePos] || 'T' == this.buffer[this.valuePos]) && (('r' == this.buffer[this.valuePos + 1] || 'R' == this.buffer[this.valuePos + 1]) && (('u' == this.buffer[this.valuePos + 2] || 'U' == this.buffer[this.valuePos + 2]) && ('e' == this.buffer[this.valuePos + 3] || 'E' == this.buffer[this.valuePos + 3]))))) {
            this.value = TRUE;
            return JsonToken.BOOLEAN;
        } else if (this.valueLength == 5 && (('f' == this.buffer[this.valuePos] || 'F' == this.buffer[this.valuePos]) && (('a' == this.buffer[this.valuePos + 1] || 'A' == this.buffer[this.valuePos + 1]) && (('l' == this.buffer[this.valuePos + 2] || 'L' == this.buffer[this.valuePos + 2]) && (('s' == this.buffer[this.valuePos + 3] || 'S' == this.buffer[this.valuePos + 3]) && ('e' == this.buffer[this.valuePos + 4] || 'E' == this.buffer[this.valuePos + 4])))))) {
            this.value = FALSE;
            return JsonToken.BOOLEAN;
        } else {
            this.value = this.stringPool.get(this.buffer, this.valuePos, this.valueLength);
            return decodeNumber(this.buffer, this.valuePos, this.valueLength);
        }
    }

    private JsonToken decodeNumber(char[] cArr, int i, int i2) {
        int i3;
        char c = cArr[i];
        if (c == '-') {
            i3 = i + 1;
            c = cArr[i3];
        } else {
            i3 = i;
        }
        if (c == '0') {
            i3++;
            c = cArr[i3];
        } else if (c < '1' || c > '9') {
            return JsonToken.STRING;
        } else {
            i3++;
            c = cArr[i3];
            while (c >= '0' && c <= '9') {
                i3++;
                c = cArr[i3];
            }
        }
        if (c == '.') {
            i3++;
            c = cArr[i3];
            while (c >= '0' && c <= '9') {
                i3++;
                c = cArr[i3];
            }
        }
        char c2 = c;
        int i4 = i3;
        char c3 = c2;
        if (c3 == 'e' || c3 == 'E') {
            i3 = i4 + 1;
            c = cArr[i3];
            if (c == '+' || c == '-') {
                i3++;
                c = cArr[i3];
            }
            if (c < '0' || c > '9') {
                return JsonToken.STRING;
            }
            i3++;
            i4 = i3;
            c3 = cArr[i3];
            while (c3 >= '0' && c3 <= '9') {
                i3 = i4 + 1;
                i4 = i3;
                c3 = cArr[i3];
            }
        }
        return i4 == i + i2 ? JsonToken.NUMBER : JsonToken.STRING;
    }

    private void expect(JsonToken jsonToken) {
        peek();
        if (this.token != jsonToken) {
            throw new IllegalStateException("Expected " + jsonToken + " but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
        }
        advance();
    }

    private boolean fillBuffer(int i) {
        Object obj = this.buffer;
        int i2 = this.bufferStartLine;
        int i3 = this.bufferStartColumn;
        int i4 = this.pos;
        for (int i5 = 0; i5 < i4; i5++) {
            if (obj[i5] == '\n') {
                i2++;
                i3 = 1;
            } else {
                i3++;
            }
        }
        this.bufferStartLine = i2;
        this.bufferStartColumn = i3;
        if (this.limit != this.pos) {
            this.limit -= this.pos;
            System.arraycopy(obj, this.pos, obj, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.pos = 0;
        do {
            i3 = this.in.read(obj, this.limit, obj.length - this.limit);
            if (i3 == -1) {
                return false;
            }
            this.limit = i3 + this.limit;
            if (this.bufferStartLine == 1 && this.bufferStartColumn == 1 && this.limit > 0 && obj[0] == '\ufeff') {
                this.pos++;
                this.bufferStartColumn--;
            }
        } while (this.limit < i);
        return true;
    }

    private int getColumnNumber() {
        int i = this.bufferStartColumn;
        for (int i2 = 0; i2 < this.pos; i2++) {
            i = this.buffer[i2] == '\n' ? 1 : i + 1;
        }
        return i;
    }

    private int getLineNumber() {
        int i = this.bufferStartLine;
        for (int i2 = 0; i2 < this.pos; i2++) {
            if (this.buffer[i2] == '\n') {
                i++;
            }
        }
        return i;
    }

    private JsonToken nextInArray(boolean z) {
        JsonToken jsonToken;
        if (z) {
            this.stack[this.stackSize - 1] = JsonScope.NONEMPTY_ARRAY;
        } else {
            switch (nextNonWhitespace(true)) {
                case NalUnitTypes.NAL_TYPE_RSV_NVCL44 /*44*/:
                    break;
                case 59:
                    checkLenient();
                    break;
                case 93:
                    this.stackSize--;
                    jsonToken = JsonToken.END_ARRAY;
                    this.token = jsonToken;
                    return jsonToken;
                default:
                    throw syntaxError("Unterminated array");
            }
        }
        switch (nextNonWhitespace(true)) {
            case NalUnitTypes.NAL_TYPE_RSV_NVCL44 /*44*/:
            case 59:
                break;
            case 93:
                if (z) {
                    this.stackSize--;
                    jsonToken = JsonToken.END_ARRAY;
                    this.token = jsonToken;
                    return jsonToken;
                }
                break;
            default:
                this.pos--;
                return nextValue();
        }
        checkLenient();
        this.pos--;
        this.value = "null";
        jsonToken = JsonToken.NULL;
        this.token = jsonToken;
        return jsonToken;
    }

    private JsonToken nextInObject(boolean z) {
        JsonToken jsonToken;
        if (z) {
            switch (nextNonWhitespace(true)) {
                case 125:
                    this.stackSize--;
                    jsonToken = JsonToken.END_OBJECT;
                    this.token = jsonToken;
                    return jsonToken;
                default:
                    this.pos--;
                    break;
            }
        }
        switch (nextNonWhitespace(true)) {
            case NalUnitTypes.NAL_TYPE_RSV_NVCL44 /*44*/:
            case 59:
                break;
            case 125:
                this.stackSize--;
                jsonToken = JsonToken.END_OBJECT;
                this.token = jsonToken;
                return jsonToken;
            default:
                throw syntaxError("Unterminated object");
        }
        int nextNonWhitespace = nextNonWhitespace(true);
        switch (nextNonWhitespace) {
            case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                break;
            case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
                checkLenient();
                break;
            default:
                checkLenient();
                this.pos--;
                this.name = nextLiteral(false);
                if (this.name.length() == 0) {
                    throw syntaxError("Expected name");
                }
                break;
        }
        this.name = nextString((char) nextNonWhitespace);
        this.stack[this.stackSize - 1] = JsonScope.DANGLING_NAME;
        jsonToken = JsonToken.NAME;
        this.token = jsonToken;
        return jsonToken;
    }

    private String nextLiteral(boolean z) {
        String str = null;
        this.valuePos = -1;
        this.valueLength = 0;
        int i = 0;
        StringBuilder stringBuilder = null;
        while (true) {
            if (this.pos + i < this.limit) {
                switch (this.buffer[this.pos + i]) {
                    case C0338R.styleable.PromptView_iconTint /*9*/:
                    case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                    case Atom.FULL_HEADER_SIZE /*12*/:
                    case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                    case TLRPC.USER_FLAG_PHOTO /*32*/:
                    case NalUnitTypes.NAL_TYPE_RSV_NVCL44 /*44*/:
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        break;
                    case NalUnitTypes.NAL_TYPE_AUD_NUT /*35*/:
                    case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
                    case ';':
                    case '=':
                    case '\\':
                        checkLenient();
                        break;
                    default:
                        i++;
                        continue;
                }
            } else if (i >= this.buffer.length) {
                if (stringBuilder == null) {
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append(this.buffer, this.pos, i);
                this.valueLength += i;
                this.pos = i + this.pos;
                i = !fillBuffer(1) ? 0 : 0;
            } else if (!fillBuffer(i + 1)) {
                this.buffer[this.limit] = '\u0000';
            }
            if (z && stringBuilder == null) {
                this.valuePos = this.pos;
            } else if (this.skipping) {
                str = "skipped!";
            } else if (stringBuilder == null) {
                str = this.stringPool.get(this.buffer, this.pos, i);
            } else {
                stringBuilder.append(this.buffer, this.pos, i);
                str = stringBuilder.toString();
            }
            this.valueLength += i;
            this.pos += i;
            return str;
        }
    }

    private int nextNonWhitespace(boolean z) {
        char[] cArr = this.buffer;
        int i = this.pos;
        int i2 = this.limit;
        while (true) {
            if (i == i2) {
                this.pos = i;
                if (fillBuffer(1)) {
                    i = this.pos;
                    i2 = this.limit;
                } else if (!z) {
                    return -1;
                } else {
                    throw new EOFException("End of input at line " + getLineNumber() + " column " + getColumnNumber());
                }
            }
            int i3 = i + 1;
            char c = cArr[i];
            switch (c) {
                case C0338R.styleable.PromptView_iconTint /*9*/:
                case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                case ShamsiCalendar.CURRENT_CENTURY /*13*/:
                case TLRPC.USER_FLAG_PHOTO /*32*/:
                    i = i3;
                    break;
                case NalUnitTypes.NAL_TYPE_AUD_NUT /*35*/:
                    this.pos = i3;
                    checkLenient();
                    skipToEndOfLine();
                    i = this.pos;
                    i2 = this.limit;
                    break;
                case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
                    this.pos = i3;
                    if (i3 == i2) {
                        this.pos--;
                        boolean fillBuffer = fillBuffer(2);
                        this.pos++;
                        if (!fillBuffer) {
                            return c;
                        }
                    }
                    checkLenient();
                    switch (cArr[this.pos]) {
                        case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                            this.pos++;
                            if (skipTo("*/")) {
                                i = this.pos + 2;
                                i2 = this.limit;
                                break;
                            }
                            throw syntaxError("Unterminated comment");
                        case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
                            this.pos++;
                            skipToEndOfLine();
                            i = this.pos;
                            i2 = this.limit;
                            break;
                        default:
                            return c;
                    }
                default:
                    this.pos = i3;
                    return c;
            }
        }
    }

    private String nextString(char c) {
        char[] cArr = this.buffer;
        StringBuilder stringBuilder = null;
        do {
            int i = this.pos;
            int i2 = this.limit;
            int i3 = i;
            while (i3 < i2) {
                int i4 = i3 + 1;
                char c2 = cArr[i3];
                if (c2 == c) {
                    this.pos = i4;
                    if (this.skipping) {
                        return "skipped!";
                    }
                    if (stringBuilder == null) {
                        return this.stringPool.get(cArr, i, (i4 - i) - 1);
                    }
                    stringBuilder.append(cArr, i, (i4 - i) - 1);
                    return stringBuilder.toString();
                }
                StringBuilder stringBuilder2;
                int i5;
                int i6;
                if (c2 == '\\') {
                    this.pos = i4;
                    if (stringBuilder == null) {
                        stringBuilder = new StringBuilder();
                    }
                    stringBuilder.append(cArr, i, (i4 - i) - 1);
                    stringBuilder.append(readEscapeCharacter());
                    i = this.pos;
                    stringBuilder2 = stringBuilder;
                    i5 = i;
                    i6 = i;
                    i = this.limit;
                    i2 = i6;
                } else {
                    i6 = i;
                    i = i2;
                    i2 = i4;
                    stringBuilder2 = stringBuilder;
                    i5 = i6;
                }
                i3 = i2;
                i2 = i;
                i = i5;
                stringBuilder = stringBuilder2;
            }
            if (stringBuilder == null) {
                stringBuilder = new StringBuilder();
            }
            stringBuilder.append(cArr, i, i3 - i);
            this.pos = i3;
        } while (fillBuffer(1));
        throw syntaxError("Unterminated string");
    }

    private JsonToken nextValue() {
        JsonToken jsonToken;
        int nextNonWhitespace = nextNonWhitespace(true);
        switch (nextNonWhitespace) {
            case NalUnitTypes.NAL_TYPE_PPS_NUT /*34*/:
                break;
            case NalUnitTypes.NAL_TYPE_PREFIX_SEI_NUT /*39*/:
                checkLenient();
                break;
            case 91:
                push(JsonScope.EMPTY_ARRAY);
                jsonToken = JsonToken.BEGIN_ARRAY;
                this.token = jsonToken;
                return jsonToken;
            case 123:
                push(JsonScope.EMPTY_OBJECT);
                jsonToken = JsonToken.BEGIN_OBJECT;
                this.token = jsonToken;
                return jsonToken;
            default:
                this.pos--;
                return readLiteral();
        }
        this.value = nextString((char) nextNonWhitespace);
        jsonToken = JsonToken.STRING;
        this.token = jsonToken;
        return jsonToken;
    }

    private JsonToken objectValue() {
        switch (nextNonWhitespace(true)) {
            case 58:
                break;
            case 61:
                checkLenient();
                if ((this.pos < this.limit || fillBuffer(1)) && this.buffer[this.pos] == '>') {
                    this.pos++;
                    break;
                }
            default:
                throw syntaxError("Expected ':'");
        }
        this.stack[this.stackSize - 1] = JsonScope.NONEMPTY_OBJECT;
        return nextValue();
    }

    private void push(JsonScope jsonScope) {
        if (this.stackSize == this.stack.length) {
            Object obj = new JsonScope[(this.stackSize * 2)];
            System.arraycopy(this.stack, 0, obj, 0, this.stackSize);
            this.stack = obj;
        }
        JsonScope[] jsonScopeArr = this.stack;
        int i = this.stackSize;
        this.stackSize = i + 1;
        jsonScopeArr[i] = jsonScope;
    }

    private char readEscapeCharacter() {
        if (this.pos != this.limit || fillBuffer(1)) {
            char[] cArr = this.buffer;
            int i = this.pos;
            this.pos = i + 1;
            char c = cArr[i];
            switch (c) {
                case 'b':
                    return '\b';
                case 'f':
                    return '\f';
                case 'n':
                    return '\n';
                case 'r':
                    return '\r';
                case 't':
                    return '\t';
                case 'u':
                    if (this.pos + 4 <= this.limit || fillBuffer(4)) {
                        int i2 = this.pos;
                        int i3 = i2 + 4;
                        int i4 = i2;
                        c = '\u0000';
                        for (i = i4; i < i3; i++) {
                            char c2 = this.buffer[i];
                            c = (char) (c << 4);
                            if (c2 >= '0' && c2 <= '9') {
                                c = (char) (c + (c2 - 48));
                            } else if (c2 >= 'a' && c2 <= 'f') {
                                c = (char) (c + ((c2 - 97) + 10));
                            } else if (c2 < 'A' || c2 > 'F') {
                                throw new NumberFormatException("\\u" + this.stringPool.get(this.buffer, this.pos, 4));
                            } else {
                                c = (char) (c + ((c2 - 65) + 10));
                            }
                        }
                        this.pos += 4;
                        return c;
                    }
                    throw syntaxError("Unterminated escape sequence");
                default:
                    return c;
            }
        }
        throw syntaxError("Unterminated escape sequence");
    }

    private JsonToken readLiteral() {
        this.value = nextLiteral(true);
        if (this.valueLength == 0) {
            throw syntaxError("Expected literal value");
        }
        this.token = decodeLiteral();
        if (this.token == JsonToken.STRING) {
            checkLenient();
        }
        return this.token;
    }

    private boolean skipTo(String str) {
        while (true) {
            if (this.pos + str.length() > this.limit && !fillBuffer(str.length())) {
                return false;
            }
            int i = 0;
            while (i < str.length()) {
                if (this.buffer[this.pos + i] != str.charAt(i)) {
                    this.pos++;
                } else {
                    i++;
                }
            }
            return true;
        }
    }

    private void skipToEndOfLine() {
        char c;
        do {
            if (this.pos < this.limit || fillBuffer(1)) {
                char[] cArr = this.buffer;
                int i = this.pos;
                this.pos = i + 1;
                c = cArr[i];
                if (c == '\r') {
                    return;
                }
            } else {
                return;
            }
        } while (c != '\n');
    }

    private IOException syntaxError(String str) {
        throw new MalformedJsonException(str + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public void beginArray() {
        expect(JsonToken.BEGIN_ARRAY);
    }

    public void beginObject() {
        expect(JsonToken.BEGIN_OBJECT);
    }

    public void close() {
        this.value = null;
        this.token = null;
        this.stack[0] = JsonScope.CLOSED;
        this.stackSize = 1;
        this.in.close();
    }

    public void endArray() {
        expect(JsonToken.END_ARRAY);
    }

    public void endObject() {
        expect(JsonToken.END_OBJECT);
    }

    public boolean hasNext() {
        peek();
        return (this.token == JsonToken.END_OBJECT || this.token == JsonToken.END_ARRAY) ? false : true;
    }

    public final boolean isLenient() {
        return this.lenient;
    }

    public boolean nextBoolean() {
        peek();
        if (this.token != JsonToken.BOOLEAN) {
            throw new IllegalStateException("Expected a boolean but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
        }
        boolean z = this.value == TRUE;
        advance();
        return z;
    }

    public double nextDouble() {
        peek();
        if (this.token == JsonToken.STRING || this.token == JsonToken.NUMBER) {
            double parseDouble = Double.parseDouble(this.value);
            if (parseDouble >= 1.0d && this.value.startsWith("0")) {
                throw new MalformedJsonException("JSON forbids octal prefixes: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
            } else if (this.lenient || !(Double.isNaN(parseDouble) || Double.isInfinite(parseDouble))) {
                advance();
                return parseDouble;
            } else {
                throw new MalformedJsonException("JSON forbids NaN and infinities: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
            }
        }
        throw new IllegalStateException("Expected a double but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public int nextInt() {
        peek();
        if (this.token == JsonToken.STRING || this.token == JsonToken.NUMBER) {
            int parseInt;
            try {
                parseInt = Integer.parseInt(this.value);
            } catch (NumberFormatException e) {
                double parseDouble = Double.parseDouble(this.value);
                parseInt = (int) parseDouble;
                if (((double) parseInt) != parseDouble) {
                    throw new NumberFormatException("Expected an int but was " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
                }
            }
            if (((long) parseInt) < 1 || !this.value.startsWith("0")) {
                advance();
                return parseInt;
            }
            throw new MalformedJsonException("JSON forbids octal prefixes: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
        }
        throw new IllegalStateException("Expected an int but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public long nextLong() {
        long parseLong;
        peek();
        if (this.token == JsonToken.STRING || this.token == JsonToken.NUMBER) {
            try {
                parseLong = Long.parseLong(this.value);
            } catch (NumberFormatException e) {
                double parseDouble = Double.parseDouble(this.value);
                parseLong = (long) parseDouble;
                if (((double) parseLong) != parseDouble) {
                    throw new NumberFormatException("Expected a long but was " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
                }
            }
            if (parseLong < 1 || !this.value.startsWith("0")) {
                advance();
                return parseLong;
            }
            throw new MalformedJsonException("JSON forbids octal prefixes: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
        }
        throw new IllegalStateException("Expected a long but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public String nextName() {
        peek();
        if (this.token != JsonToken.NAME) {
            throw new IllegalStateException("Expected a name but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
        }
        String str = this.name;
        advance();
        return str;
    }

    public void nextNull() {
        peek();
        if (this.token != JsonToken.NULL) {
            throw new IllegalStateException("Expected null but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
        }
        advance();
    }

    public String nextString() {
        peek();
        if (this.token == JsonToken.STRING || this.token == JsonToken.NUMBER) {
            String str = this.value;
            advance();
            return str;
        }
        throw new IllegalStateException("Expected a string but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public JsonToken peek() {
        if (this.token != null) {
            return this.token;
        }
        switch (C03162.$SwitchMap$com$google$gson$stream$JsonScope[this.stack[this.stackSize - 1].ordinal()]) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                if (this.lenient) {
                    consumeNonExecutePrefix();
                }
                this.stack[this.stackSize - 1] = JsonScope.NONEMPTY_DOCUMENT;
                JsonToken nextValue = nextValue();
                if (this.lenient || this.token == JsonToken.BEGIN_ARRAY || this.token == JsonToken.BEGIN_OBJECT) {
                    return nextValue;
                }
                throw new IOException("Expected JSON document to start with '[' or '{' but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
            case VideoPlayer.STATE_PREPARING /*2*/:
                return nextInArray(true);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return nextInArray(false);
            case VideoPlayer.STATE_READY /*4*/:
                return nextInObject(true);
            case VideoPlayer.STATE_ENDED /*5*/:
                return objectValue();
            case Method.TRACE /*6*/:
                return nextInObject(false);
            case Method.PATCH /*7*/:
                if (nextNonWhitespace(false) == -1) {
                    return JsonToken.END_DOCUMENT;
                }
                this.pos--;
                if (this.lenient) {
                    return nextValue();
                }
                throw syntaxError("Expected EOF");
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                throw new IllegalStateException("JsonReader is closed");
            default:
                throw new AssertionError();
        }
    }

    public final void setLenient(boolean z) {
        this.lenient = z;
    }

    public void skipValue() {
        this.skipping = true;
        int i = 0;
        while (true) {
            try {
                JsonToken advance = advance();
                if (advance == JsonToken.BEGIN_ARRAY || advance == JsonToken.BEGIN_OBJECT) {
                    i++;
                    continue;
                } else if (advance == JsonToken.END_ARRAY || advance == JsonToken.END_OBJECT) {
                    i--;
                    continue;
                }
                if (i == 0) {
                    break;
                }
            } finally {
                this.skipping = false;
            }
        }
    }

    public String toString() {
        return getClass().getSimpleName() + " at line " + getLineNumber() + " column " + getColumnNumber();
    }
}
