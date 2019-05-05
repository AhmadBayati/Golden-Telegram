package com.hanista.mobogram.messenger.exoplayer.extractor.webm;

import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.Stack;

final class DefaultEbmlReader implements EbmlReader {
    private static final int ELEMENT_STATE_READ_CONTENT = 2;
    private static final int ELEMENT_STATE_READ_CONTENT_SIZE = 1;
    private static final int ELEMENT_STATE_READ_ID = 0;
    private static final int MAX_ID_BYTES = 4;
    private static final int MAX_INTEGER_ELEMENT_SIZE_BYTES = 8;
    private static final int MAX_LENGTH_BYTES = 8;
    private static final int VALID_FLOAT32_ELEMENT_SIZE_BYTES = 4;
    private static final int VALID_FLOAT64_ELEMENT_SIZE_BYTES = 8;
    private long elementContentSize;
    private int elementId;
    private int elementState;
    private final Stack<MasterElement> masterElementsStack;
    private EbmlReaderOutput output;
    private final byte[] scratch;
    private final VarintReader varintReader;

    private static final class MasterElement {
        private final long elementEndPosition;
        private final int elementId;

        private MasterElement(int i, long j) {
            this.elementId = i;
            this.elementEndPosition = j;
        }
    }

    DefaultEbmlReader() {
        this.scratch = new byte[VALID_FLOAT64_ELEMENT_SIZE_BYTES];
        this.masterElementsStack = new Stack();
        this.varintReader = new VarintReader();
    }

    private long maybeResyncToNextLevel1Element(ExtractorInput extractorInput) {
        extractorInput.resetPeekPosition();
        while (true) {
            extractorInput.peekFully(this.scratch, ELEMENT_STATE_READ_ID, VALID_FLOAT32_ELEMENT_SIZE_BYTES);
            int parseUnsignedVarintLength = VarintReader.parseUnsignedVarintLength(this.scratch[ELEMENT_STATE_READ_ID]);
            if (parseUnsignedVarintLength != -1 && parseUnsignedVarintLength <= VALID_FLOAT32_ELEMENT_SIZE_BYTES) {
                int assembleVarint = (int) VarintReader.assembleVarint(this.scratch, parseUnsignedVarintLength, false);
                if (this.output.isLevel1Element(assembleVarint)) {
                    extractorInput.skipFully(parseUnsignedVarintLength);
                    return (long) assembleVarint;
                }
            }
            extractorInput.skipFully(ELEMENT_STATE_READ_CONTENT_SIZE);
        }
    }

    private double readFloat(ExtractorInput extractorInput, int i) {
        long readInteger = readInteger(extractorInput, i);
        return i == VALID_FLOAT32_ELEMENT_SIZE_BYTES ? (double) Float.intBitsToFloat((int) readInteger) : Double.longBitsToDouble(readInteger);
    }

    private long readInteger(ExtractorInput extractorInput, int i) {
        int i2 = ELEMENT_STATE_READ_ID;
        extractorInput.readFully(this.scratch, ELEMENT_STATE_READ_ID, i);
        long j = 0;
        while (i2 < i) {
            j = (j << VALID_FLOAT64_ELEMENT_SIZE_BYTES) | ((long) (this.scratch[i2] & NalUnitUtil.EXTENDED_SAR));
            i2 += ELEMENT_STATE_READ_CONTENT_SIZE;
        }
        return j;
    }

    private String readString(ExtractorInput extractorInput, int i) {
        if (i == 0) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        byte[] bArr = new byte[i];
        extractorInput.readFully(bArr, ELEMENT_STATE_READ_ID, i);
        return new String(bArr);
    }

    public void init(EbmlReaderOutput ebmlReaderOutput) {
        this.output = ebmlReaderOutput;
    }

    public boolean read(ExtractorInput extractorInput) {
        Assertions.checkState(this.output != null);
        while (true) {
            if (this.masterElementsStack.isEmpty() || extractorInput.getPosition() < ((MasterElement) this.masterElementsStack.peek()).elementEndPosition) {
                if (this.elementState == 0) {
                    long readUnsignedVarint = this.varintReader.readUnsignedVarint(extractorInput, true, false, VALID_FLOAT32_ELEMENT_SIZE_BYTES);
                    if (readUnsignedVarint == -2) {
                        readUnsignedVarint = maybeResyncToNextLevel1Element(extractorInput);
                    }
                    if (readUnsignedVarint == -1) {
                        return false;
                    }
                    this.elementId = (int) readUnsignedVarint;
                    this.elementState = ELEMENT_STATE_READ_CONTENT_SIZE;
                }
                if (this.elementState == ELEMENT_STATE_READ_CONTENT_SIZE) {
                    this.elementContentSize = this.varintReader.readUnsignedVarint(extractorInput, false, true, VALID_FLOAT64_ELEMENT_SIZE_BYTES);
                    this.elementState = ELEMENT_STATE_READ_CONTENT;
                }
                int elementType = this.output.getElementType(this.elementId);
                switch (elementType) {
                    case ELEMENT_STATE_READ_ID /*0*/:
                        extractorInput.skipFully((int) this.elementContentSize);
                        this.elementState = ELEMENT_STATE_READ_ID;
                    case ELEMENT_STATE_READ_CONTENT_SIZE /*1*/:
                        long position = extractorInput.getPosition();
                        this.masterElementsStack.add(new MasterElement(this.elementContentSize + position, null));
                        this.output.startMasterElement(this.elementId, position, this.elementContentSize);
                        this.elementState = ELEMENT_STATE_READ_ID;
                        return true;
                    case ELEMENT_STATE_READ_CONTENT /*2*/:
                        if (this.elementContentSize > 8) {
                            throw new ParserException("Invalid integer size: " + this.elementContentSize);
                        }
                        this.output.integerElement(this.elementId, readInteger(extractorInput, (int) this.elementContentSize));
                        this.elementState = ELEMENT_STATE_READ_ID;
                        return true;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        if (this.elementContentSize > 2147483647L) {
                            throw new ParserException("String element size: " + this.elementContentSize);
                        }
                        this.output.stringElement(this.elementId, readString(extractorInput, (int) this.elementContentSize));
                        this.elementState = ELEMENT_STATE_READ_ID;
                        return true;
                    case VALID_FLOAT32_ELEMENT_SIZE_BYTES /*4*/:
                        this.output.binaryElement(this.elementId, (int) this.elementContentSize, extractorInput);
                        this.elementState = ELEMENT_STATE_READ_ID;
                        return true;
                    case VideoPlayer.STATE_ENDED /*5*/:
                        if (this.elementContentSize == 4 || this.elementContentSize == 8) {
                            this.output.floatElement(this.elementId, readFloat(extractorInput, (int) this.elementContentSize));
                            this.elementState = ELEMENT_STATE_READ_ID;
                            return true;
                        }
                        throw new ParserException("Invalid float size: " + this.elementContentSize);
                    default:
                        throw new ParserException("Invalid element type " + elementType);
                }
            }
            this.output.endMasterElement(((MasterElement) this.masterElementsStack.pop()).elementId);
            return true;
        }
    }

    public void reset() {
        this.elementState = ELEMENT_STATE_READ_ID;
        this.masterElementsStack.clear();
        this.varintReader.reset();
    }
}
