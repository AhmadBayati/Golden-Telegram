package com.hanista.mobogram.messenger.exoplayer.metadata.id3;

import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.metadata.MetadataParser;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class Id3Parser implements MetadataParser<List<Id3Frame>> {
    private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
    private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
    private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
    private static final int ID3_TEXT_ENCODING_UTF_8 = 3;

    private static int delimiterLength(int i) {
        return (i == 0 || i == ID3_TEXT_ENCODING_UTF_8) ? ID3_TEXT_ENCODING_UTF_16 : ID3_TEXT_ENCODING_UTF_16BE;
    }

    private static String getCharsetName(int i) {
        switch (i) {
            case ID3_TEXT_ENCODING_ISO_8859_1 /*0*/:
                return "ISO-8859-1";
            case ID3_TEXT_ENCODING_UTF_16 /*1*/:
                return "UTF-16";
            case ID3_TEXT_ENCODING_UTF_16BE /*2*/:
                return "UTF-16BE";
            case ID3_TEXT_ENCODING_UTF_8 /*3*/:
                return C0700C.UTF8_NAME;
            default:
                return "ISO-8859-1";
        }
    }

    private static int indexOfEos(byte[] bArr, int i, int i2) {
        int indexOfZeroByte = indexOfZeroByte(bArr, i);
        if (i2 == 0 || i2 == ID3_TEXT_ENCODING_UTF_8) {
            return indexOfZeroByte;
        }
        while (indexOfZeroByte < bArr.length - 1) {
            if (bArr[indexOfZeroByte + ID3_TEXT_ENCODING_UTF_16] == null) {
                return indexOfZeroByte;
            }
            indexOfZeroByte = indexOfZeroByte(bArr, indexOfZeroByte + ID3_TEXT_ENCODING_UTF_16);
        }
        return bArr.length;
    }

    private static int indexOfZeroByte(byte[] bArr, int i) {
        while (i < bArr.length) {
            if (bArr[i] == null) {
                return i;
            }
            i += ID3_TEXT_ENCODING_UTF_16;
        }
        return bArr.length;
    }

    private static ApicFrame parseApicFrame(ParsableByteArray parsableByteArray, int i) {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        String charsetName = getCharsetName(readUnsignedByte);
        byte[] bArr = new byte[(i - 1)];
        parsableByteArray.readBytes(bArr, ID3_TEXT_ENCODING_ISO_8859_1, i - 1);
        int indexOfZeroByte = indexOfZeroByte(bArr, ID3_TEXT_ENCODING_ISO_8859_1);
        String str = new String(bArr, ID3_TEXT_ENCODING_ISO_8859_1, indexOfZeroByte, "ISO-8859-1");
        int i2 = bArr[indexOfZeroByte + ID3_TEXT_ENCODING_UTF_16] & NalUnitUtil.EXTENDED_SAR;
        indexOfZeroByte += ID3_TEXT_ENCODING_UTF_16BE;
        int indexOfEos = indexOfEos(bArr, indexOfZeroByte, readUnsignedByte);
        return new ApicFrame(str, new String(bArr, indexOfZeroByte, indexOfEos - indexOfZeroByte, charsetName), i2, Arrays.copyOfRange(bArr, delimiterLength(readUnsignedByte) + indexOfEos, bArr.length));
    }

    private static BinaryFrame parseBinaryFrame(ParsableByteArray parsableByteArray, int i, String str) {
        byte[] bArr = new byte[i];
        parsableByteArray.readBytes(bArr, ID3_TEXT_ENCODING_ISO_8859_1, i);
        return new BinaryFrame(str, bArr);
    }

    private static GeobFrame parseGeobFrame(ParsableByteArray parsableByteArray, int i) {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        String charsetName = getCharsetName(readUnsignedByte);
        byte[] bArr = new byte[(i - 1)];
        parsableByteArray.readBytes(bArr, ID3_TEXT_ENCODING_ISO_8859_1, i - 1);
        int indexOfZeroByte = indexOfZeroByte(bArr, ID3_TEXT_ENCODING_ISO_8859_1);
        String str = new String(bArr, ID3_TEXT_ENCODING_ISO_8859_1, indexOfZeroByte, "ISO-8859-1");
        indexOfZeroByte += ID3_TEXT_ENCODING_UTF_16;
        int indexOfEos = indexOfEos(bArr, indexOfZeroByte, readUnsignedByte);
        String str2 = new String(bArr, indexOfZeroByte, indexOfEos - indexOfZeroByte, charsetName);
        indexOfZeroByte = delimiterLength(readUnsignedByte) + indexOfEos;
        indexOfEos = indexOfEos(bArr, indexOfZeroByte, readUnsignedByte);
        return new GeobFrame(str, str2, new String(bArr, indexOfZeroByte, indexOfEos - indexOfZeroByte, charsetName), Arrays.copyOfRange(bArr, delimiterLength(readUnsignedByte) + indexOfEos, bArr.length));
    }

    private static int parseId3Header(ParsableByteArray parsableByteArray) {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
        int readUnsignedByte3 = parsableByteArray.readUnsignedByte();
        if (readUnsignedByte == 73 && readUnsignedByte2 == 68 && readUnsignedByte3 == 51) {
            parsableByteArray.skipBytes(ID3_TEXT_ENCODING_UTF_16BE);
            readUnsignedByte2 = parsableByteArray.readUnsignedByte();
            readUnsignedByte = parsableByteArray.readSynchSafeInt();
            if ((readUnsignedByte2 & ID3_TEXT_ENCODING_UTF_16BE) != 0) {
                readUnsignedByte3 = parsableByteArray.readSynchSafeInt();
                if (readUnsignedByte3 > 4) {
                    parsableByteArray.skipBytes(readUnsignedByte3 - 4);
                }
                readUnsignedByte -= readUnsignedByte3;
            }
            return (readUnsignedByte2 & 8) != 0 ? readUnsignedByte - 10 : readUnsignedByte;
        } else {
            Object[] objArr = new Object[ID3_TEXT_ENCODING_UTF_8];
            objArr[ID3_TEXT_ENCODING_ISO_8859_1] = Integer.valueOf(readUnsignedByte);
            objArr[ID3_TEXT_ENCODING_UTF_16] = Integer.valueOf(readUnsignedByte2);
            objArr[ID3_TEXT_ENCODING_UTF_16BE] = Integer.valueOf(readUnsignedByte3);
            throw new ParserException(String.format(Locale.US, "Unexpected ID3 file identifier, expected \"ID3\", actual \"%c%c%c\".", objArr));
        }
    }

    private static PrivFrame parsePrivFrame(ParsableByteArray parsableByteArray, int i) {
        byte[] bArr = new byte[i];
        parsableByteArray.readBytes(bArr, ID3_TEXT_ENCODING_ISO_8859_1, i);
        int indexOfZeroByte = indexOfZeroByte(bArr, ID3_TEXT_ENCODING_ISO_8859_1);
        return new PrivFrame(new String(bArr, ID3_TEXT_ENCODING_ISO_8859_1, indexOfZeroByte, "ISO-8859-1"), Arrays.copyOfRange(bArr, indexOfZeroByte + ID3_TEXT_ENCODING_UTF_16, bArr.length));
    }

    private static TextInformationFrame parseTextInformationFrame(ParsableByteArray parsableByteArray, int i, String str) {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        String charsetName = getCharsetName(readUnsignedByte);
        byte[] bArr = new byte[(i - 1)];
        parsableByteArray.readBytes(bArr, ID3_TEXT_ENCODING_ISO_8859_1, i - 1);
        return new TextInformationFrame(str, new String(bArr, ID3_TEXT_ENCODING_ISO_8859_1, indexOfEos(bArr, ID3_TEXT_ENCODING_ISO_8859_1, readUnsignedByte), charsetName));
    }

    private static TxxxFrame parseTxxxFrame(ParsableByteArray parsableByteArray, int i) {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        String charsetName = getCharsetName(readUnsignedByte);
        byte[] bArr = new byte[(i - 1)];
        parsableByteArray.readBytes(bArr, ID3_TEXT_ENCODING_ISO_8859_1, i - 1);
        int indexOfEos = indexOfEos(bArr, ID3_TEXT_ENCODING_ISO_8859_1, readUnsignedByte);
        String str = new String(bArr, ID3_TEXT_ENCODING_ISO_8859_1, indexOfEos, charsetName);
        indexOfEos += delimiterLength(readUnsignedByte);
        return new TxxxFrame(str, new String(bArr, indexOfEos, indexOfEos(bArr, indexOfEos, readUnsignedByte) - indexOfEos, charsetName));
    }

    public boolean canParse(String str) {
        return str.equals(MimeTypes.APPLICATION_ID3);
    }

    public List<Id3Frame> parse(byte[] bArr, int i) {
        List arrayList = new ArrayList();
        ParsableByteArray parsableByteArray = new ParsableByteArray(bArr, i);
        int parseId3Header = parseId3Header(parsableByteArray);
        while (parseId3Header > 0) {
            int readUnsignedByte = parsableByteArray.readUnsignedByte();
            int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
            int readUnsignedByte3 = parsableByteArray.readUnsignedByte();
            int readUnsignedByte4 = parsableByteArray.readUnsignedByte();
            int readSynchSafeInt = parsableByteArray.readSynchSafeInt();
            if (readSynchSafeInt <= ID3_TEXT_ENCODING_UTF_16) {
                break;
            }
            Object parseTxxxFrame;
            parsableByteArray.skipBytes(ID3_TEXT_ENCODING_UTF_16BE);
            if (readUnsignedByte == 84 && readUnsignedByte2 == 88 && readUnsignedByte3 == 88 && readUnsignedByte4 == 88) {
                try {
                    parseTxxxFrame = parseTxxxFrame(parsableByteArray, readSynchSafeInt);
                } catch (Throwable e) {
                    throw new ParserException(e);
                }
            } else if (readUnsignedByte == 80 && readUnsignedByte2 == 82 && readUnsignedByte3 == 73 && readUnsignedByte4 == 86) {
                parseTxxxFrame = parsePrivFrame(parsableByteArray, readSynchSafeInt);
            } else if (readUnsignedByte == 71 && readUnsignedByte2 == 69 && readUnsignedByte3 == 79 && readUnsignedByte4 == 66) {
                parseTxxxFrame = parseGeobFrame(parsableByteArray, readSynchSafeInt);
            } else if (readUnsignedByte == 65 && readUnsignedByte2 == 80 && readUnsignedByte3 == 73 && readUnsignedByte4 == 67) {
                parseTxxxFrame = parseApicFrame(parsableByteArray, readSynchSafeInt);
            } else if (readUnsignedByte == 84) {
                parseTxxxFrame = parseTextInformationFrame(parsableByteArray, readSynchSafeInt, String.format(Locale.US, "%c%c%c%c", new Object[]{Integer.valueOf(readUnsignedByte), Integer.valueOf(readUnsignedByte2), Integer.valueOf(readUnsignedByte3), Integer.valueOf(readUnsignedByte4)}));
            } else {
                parseTxxxFrame = parseBinaryFrame(parsableByteArray, readSynchSafeInt, String.format(Locale.US, "%c%c%c%c", new Object[]{Integer.valueOf(readUnsignedByte), Integer.valueOf(readUnsignedByte2), Integer.valueOf(readUnsignedByte3), Integer.valueOf(readUnsignedByte4)}));
            }
            arrayList.add(parseTxxxFrame);
            parseId3Header -= readSynchSafeInt + 10;
        }
        return Collections.unmodifiableList(arrayList);
    }
}
