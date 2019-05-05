package com.hanista.mobogram.messenger.exoplayer.extractor.mp3;

import android.util.Pair;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.GaplessInfo;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PsExtractor;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.tgnet.TLRPC;
import java.nio.charset.Charset;

final class Id3Util {
    private static final Charset[] CHARSET_BY_ENCODING;
    private static final int ID3_TAG;
    private static final int MAXIMUM_METADATA_SIZE = 3145728;

    static {
        ID3_TAG = Util.getIntegerCodeForString("ID3");
        CHARSET_BY_ENCODING = new Charset[]{Charset.forName("ISO-8859-1"), Charset.forName("UTF-16LE"), Charset.forName("UTF-16BE"), Charset.forName(C0700C.UTF8_NAME)};
    }

    private Id3Util() {
    }

    private static boolean canParseMetadata(int i, int i2, int i3, int i4) {
        return i2 != NalUnitUtil.EXTENDED_SAR && i >= 2 && i <= 4 && i4 <= MAXIMUM_METADATA_SIZE && ((i != 2 || ((i3 & 63) == 0 && (i3 & 64) == 0)) && ((i != 3 || (i3 & 31) == 0) && (i != 4 || (i3 & 15) == 0)));
    }

    private static boolean canUnescapeVersion4(ParsableByteArray parsableByteArray, boolean z) {
        parsableByteArray.setPosition(ID3_TAG);
        while (parsableByteArray.bytesLeft() >= 10) {
            if (parsableByteArray.readInt() == 0) {
                return true;
            }
            long readUnsignedInt = parsableByteArray.readUnsignedInt();
            if (!z) {
                if ((8421504 & readUnsignedInt) != 0) {
                    return false;
                }
                readUnsignedInt = (((readUnsignedInt >> 24) & 127) << 21) | (((readUnsignedInt & 127) | (((readUnsignedInt >> 8) & 127) << 7)) | (((readUnsignedInt >> 16) & 127) << 14));
            }
            if (readUnsignedInt > ((long) (parsableByteArray.bytesLeft() - 2))) {
                return false;
            }
            if ((parsableByteArray.readUnsignedShort() & 1) != 0 && parsableByteArray.bytesLeft() < 4) {
                return false;
            }
            parsableByteArray.skipBytes((int) readUnsignedInt);
        }
        return true;
    }

    private static Pair<String, String> findNextComment(int i, ParsableByteArray parsableByteArray) {
        int readUnsignedShort;
        while (true) {
            int readUnsignedInt24;
            if (i == 2) {
                if (parsableByteArray.bytesLeft() < 6) {
                    return null;
                }
                String readString = parsableByteArray.readString(3, Charset.forName("US-ASCII"));
                if (readString.equals("\u0000\u0000\u0000")) {
                    return null;
                }
                readUnsignedInt24 = parsableByteArray.readUnsignedInt24();
                if (readUnsignedInt24 == 0 || readUnsignedInt24 > parsableByteArray.bytesLeft()) {
                    return null;
                }
                if (readString.equals("COM")) {
                    break;
                }
                parsableByteArray.skipBytes(readUnsignedInt24);
            } else if (parsableByteArray.bytesLeft() < 10) {
                return null;
            } else {
                String readString2 = parsableByteArray.readString(4, Charset.forName("US-ASCII"));
                if (readString2.equals("\u0000\u0000\u0000\u0000")) {
                    return null;
                }
                readUnsignedInt24 = i == 4 ? parsableByteArray.readSynchSafeInt() : parsableByteArray.readUnsignedIntToInt();
                if (readUnsignedInt24 == 0 || readUnsignedInt24 > parsableByteArray.bytesLeft() - 2) {
                    return null;
                }
                readUnsignedShort = parsableByteArray.readUnsignedShort();
                readUnsignedShort = ((i != 4 || (readUnsignedShort & 12) == 0) && (i != 3 || (readUnsignedShort & PsExtractor.AUDIO_STREAM) == 0)) ? ID3_TAG : 1;
                if (readUnsignedShort == 0 && readString2.equals("COMM")) {
                    break;
                }
                parsableByteArray.skipBytes(readUnsignedInt24);
            }
        }
        readUnsignedShort = parsableByteArray.readUnsignedByte();
        if (readUnsignedShort < 0 || readUnsignedShort >= CHARSET_BY_ENCODING.length) {
            return null;
        }
        String[] split = parsableByteArray.readString(readUnsignedInt24 - 1, CHARSET_BY_ENCODING[readUnsignedShort]).split("\u0000");
        return split.length == 2 ? Pair.create(split[ID3_TAG], split[1]) : null;
    }

    private static GaplessInfo parseGaplessInfo(ParsableByteArray parsableByteArray, int i, int i2) {
        unescape(parsableByteArray, i, i2);
        parsableByteArray.setPosition(ID3_TAG);
        int readSynchSafeInt;
        if (i != 3 || (i2 & 64) == 0) {
            if (i == 4 && (i2 & 64) != 0) {
                if (parsableByteArray.bytesLeft() < 4) {
                    return null;
                }
                readSynchSafeInt = parsableByteArray.readSynchSafeInt();
                if (readSynchSafeInt < 6 || readSynchSafeInt > parsableByteArray.bytesLeft() + 4) {
                    return null;
                }
                parsableByteArray.setPosition(readSynchSafeInt);
            }
        } else if (parsableByteArray.bytesLeft() < 4) {
            return null;
        } else {
            readSynchSafeInt = parsableByteArray.readUnsignedIntToInt();
            if (readSynchSafeInt > parsableByteArray.bytesLeft()) {
                return null;
            }
            if (readSynchSafeInt >= 6) {
                parsableByteArray.skipBytes(2);
                int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
                parsableByteArray.setPosition(4);
                parsableByteArray.setLimit(parsableByteArray.limit() - readUnsignedIntToInt);
                if (parsableByteArray.bytesLeft() < readSynchSafeInt) {
                    return null;
                }
            }
            parsableByteArray.skipBytes(readSynchSafeInt);
        }
        while (true) {
            Pair findNextComment = findNextComment(i, parsableByteArray);
            if (findNextComment == null) {
                return null;
            }
            if (((String) findNextComment.first).length() > 3) {
                GaplessInfo createFromComment = GaplessInfo.createFromComment(((String) findNextComment.first).substring(3), (String) findNextComment.second);
                if (createFromComment != null) {
                    return createFromComment;
                }
            }
        }
    }

    public static GaplessInfo parseId3(ExtractorInput extractorInput) {
        ParsableByteArray parsableByteArray = new ParsableByteArray(10);
        GaplessInfo gaplessInfo = null;
        int i = ID3_TAG;
        while (true) {
            extractorInput.peekFully(parsableByteArray.data, ID3_TAG, 10);
            parsableByteArray.setPosition(ID3_TAG);
            if (parsableByteArray.readUnsignedInt24() != ID3_TAG) {
                extractorInput.resetPeekPosition();
                extractorInput.advancePeekPosition(i);
                return gaplessInfo;
            }
            int readUnsignedByte = parsableByteArray.readUnsignedByte();
            int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
            int readUnsignedByte3 = parsableByteArray.readUnsignedByte();
            int readSynchSafeInt = parsableByteArray.readSynchSafeInt();
            if (gaplessInfo == null && canParseMetadata(readUnsignedByte, readUnsignedByte2, readUnsignedByte3, readSynchSafeInt)) {
                byte[] bArr = new byte[readSynchSafeInt];
                extractorInput.peekFully(bArr, ID3_TAG, readSynchSafeInt);
                gaplessInfo = parseGaplessInfo(new ParsableByteArray(bArr), readUnsignedByte, readUnsignedByte3);
            } else {
                extractorInput.advancePeekPosition(readSynchSafeInt);
            }
            i += readSynchSafeInt + 10;
        }
    }

    private static boolean unescape(ParsableByteArray parsableByteArray, int i, int i2) {
        if (i != 4) {
            if ((i2 & TLRPC.USER_FLAG_UNUSED) != 0) {
                Object obj = parsableByteArray.data;
                int length = obj.length;
                int i3 = false;
                while (i3 + 1 < length) {
                    if ((obj[i3] & NalUnitUtil.EXTENDED_SAR) == NalUnitUtil.EXTENDED_SAR && obj[i3 + 1] == null) {
                        System.arraycopy(obj, i3 + 2, obj, i3 + 1, (length - i3) - 2);
                        length--;
                    }
                    i3++;
                }
                parsableByteArray.setLimit(length);
            }
        } else if (canUnescapeVersion4(parsableByteArray, false)) {
            unescapeVersion4(parsableByteArray, false);
        } else if (!canUnescapeVersion4(parsableByteArray, true)) {
            return false;
        } else {
            unescapeVersion4(parsableByteArray, true);
        }
        return true;
    }

    private static void unescapeVersion4(ParsableByteArray parsableByteArray, boolean z) {
        parsableByteArray.setPosition(ID3_TAG);
        Object obj = parsableByteArray.data;
        while (parsableByteArray.bytesLeft() >= 10 && parsableByteArray.readInt() != 0) {
            int position;
            int i;
            int readUnsignedIntToInt = z ? parsableByteArray.readUnsignedIntToInt() : parsableByteArray.readSynchSafeInt();
            int readUnsignedShort = parsableByteArray.readUnsignedShort();
            if ((readUnsignedShort & 1) != 0) {
                position = parsableByteArray.getPosition();
                System.arraycopy(obj, position + 4, obj, position, parsableByteArray.bytesLeft() - 4);
                position = readUnsignedIntToInt - 4;
                i = readUnsignedShort & -2;
                parsableByteArray.setLimit(parsableByteArray.limit() - 4);
            } else {
                i = readUnsignedShort;
                position = readUnsignedIntToInt;
            }
            if ((i & 2) != 0) {
                readUnsignedIntToInt = parsableByteArray.getPosition() + 1;
                int i2 = ID3_TAG;
                int i3 = readUnsignedIntToInt;
                while (i2 + 1 < position) {
                    if ((obj[readUnsignedIntToInt - 1] & NalUnitUtil.EXTENDED_SAR) == NalUnitUtil.EXTENDED_SAR && obj[readUnsignedIntToInt] == null) {
                        readUnsignedIntToInt++;
                        position--;
                    }
                    int i4 = i3 + 1;
                    int i5 = readUnsignedIntToInt + 1;
                    obj[i3] = obj[readUnsignedIntToInt];
                    i2++;
                    i3 = i4;
                    readUnsignedIntToInt = i5;
                }
                parsableByteArray.setLimit(parsableByteArray.limit() - (readUnsignedIntToInt - i3));
                System.arraycopy(obj, readUnsignedIntToInt, obj, i3, parsableByteArray.bytesLeft() - readUnsignedIntToInt);
                readUnsignedIntToInt = i & -3;
            } else {
                readUnsignedIntToInt = i;
            }
            if (readUnsignedIntToInt != readUnsignedShort || z) {
                i = parsableByteArray.getPosition() - 6;
                writeSyncSafeInteger(obj, i, position);
                obj[i + 4] = (byte) (readUnsignedIntToInt >> 8);
                obj[i + 5] = (byte) (readUnsignedIntToInt & NalUnitUtil.EXTENDED_SAR);
            }
            parsableByteArray.skipBytes(position);
        }
    }

    private static void writeSyncSafeInteger(byte[] bArr, int i, int i2) {
        bArr[i] = (byte) ((i2 >> 21) & 127);
        bArr[i + 1] = (byte) ((i2 >> 14) & 127);
        bArr[i + 2] = (byte) ((i2 >> 7) & 127);
        bArr[i + 3] = (byte) (i2 & 127);
    }
}
