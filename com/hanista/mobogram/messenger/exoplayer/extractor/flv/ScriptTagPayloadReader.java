package com.hanista.mobogram.messenger.exoplayer.extractor.flv;

import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

final class ScriptTagPayloadReader extends TagPayloadReader {
    private static final int AMF_TYPE_BOOLEAN = 1;
    private static final int AMF_TYPE_DATE = 11;
    private static final int AMF_TYPE_ECMA_ARRAY = 8;
    private static final int AMF_TYPE_END_MARKER = 9;
    private static final int AMF_TYPE_NUMBER = 0;
    private static final int AMF_TYPE_OBJECT = 3;
    private static final int AMF_TYPE_STRICT_ARRAY = 10;
    private static final int AMF_TYPE_STRING = 2;
    private static final String KEY_DURATION = "duration";
    private static final String NAME_METADATA = "onMetaData";

    public ScriptTagPayloadReader(TrackOutput trackOutput) {
        super(trackOutput);
    }

    private static Boolean readAmfBoolean(ParsableByteArray parsableByteArray) {
        boolean z = true;
        if (parsableByteArray.readUnsignedByte() != AMF_TYPE_BOOLEAN) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    private static Object readAmfData(ParsableByteArray parsableByteArray, int i) {
        switch (i) {
            case AMF_TYPE_NUMBER /*0*/:
                return readAmfDouble(parsableByteArray);
            case AMF_TYPE_BOOLEAN /*1*/:
                return readAmfBoolean(parsableByteArray);
            case AMF_TYPE_STRING /*2*/:
                return readAmfString(parsableByteArray);
            case AMF_TYPE_OBJECT /*3*/:
                return readAmfObject(parsableByteArray);
            case AMF_TYPE_ECMA_ARRAY /*8*/:
                return readAmfEcmaArray(parsableByteArray);
            case AMF_TYPE_STRICT_ARRAY /*10*/:
                return readAmfStrictArray(parsableByteArray);
            case AMF_TYPE_DATE /*11*/:
                return readAmfDate(parsableByteArray);
            default:
                return null;
        }
    }

    private static Date readAmfDate(ParsableByteArray parsableByteArray) {
        Date date = new Date((long) readAmfDouble(parsableByteArray).doubleValue());
        parsableByteArray.skipBytes(AMF_TYPE_STRING);
        return date;
    }

    private static Double readAmfDouble(ParsableByteArray parsableByteArray) {
        return Double.valueOf(Double.longBitsToDouble(parsableByteArray.readLong()));
    }

    private static HashMap<String, Object> readAmfEcmaArray(ParsableByteArray parsableByteArray) {
        int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
        HashMap<String, Object> hashMap = new HashMap(readUnsignedIntToInt);
        for (int i = AMF_TYPE_NUMBER; i < readUnsignedIntToInt; i += AMF_TYPE_BOOLEAN) {
            hashMap.put(readAmfString(parsableByteArray), readAmfData(parsableByteArray, readAmfType(parsableByteArray)));
        }
        return hashMap;
    }

    private static HashMap<String, Object> readAmfObject(ParsableByteArray parsableByteArray) {
        HashMap<String, Object> hashMap = new HashMap();
        while (true) {
            String readAmfString = readAmfString(parsableByteArray);
            int readAmfType = readAmfType(parsableByteArray);
            if (readAmfType == AMF_TYPE_END_MARKER) {
                return hashMap;
            }
            hashMap.put(readAmfString, readAmfData(parsableByteArray, readAmfType));
        }
    }

    private static ArrayList<Object> readAmfStrictArray(ParsableByteArray parsableByteArray) {
        int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
        ArrayList<Object> arrayList = new ArrayList(readUnsignedIntToInt);
        for (int i = AMF_TYPE_NUMBER; i < readUnsignedIntToInt; i += AMF_TYPE_BOOLEAN) {
            arrayList.add(readAmfData(parsableByteArray, readAmfType(parsableByteArray)));
        }
        return arrayList;
    }

    private static String readAmfString(ParsableByteArray parsableByteArray) {
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        int position = parsableByteArray.getPosition();
        parsableByteArray.skipBytes(readUnsignedShort);
        return new String(parsableByteArray.data, position, readUnsignedShort);
    }

    private static int readAmfType(ParsableByteArray parsableByteArray) {
        return parsableByteArray.readUnsignedByte();
    }

    protected boolean parseHeader(ParsableByteArray parsableByteArray) {
        return true;
    }

    protected void parsePayload(ParsableByteArray parsableByteArray, long j) {
        if (readAmfType(parsableByteArray) != AMF_TYPE_STRING) {
            throw new ParserException();
        }
        if (!NAME_METADATA.equals(readAmfString(parsableByteArray))) {
            return;
        }
        if (readAmfType(parsableByteArray) != AMF_TYPE_ECMA_ARRAY) {
            throw new ParserException();
        }
        Map readAmfEcmaArray = readAmfEcmaArray(parsableByteArray);
        if (readAmfEcmaArray.containsKey(KEY_DURATION)) {
            double doubleValue = ((Double) readAmfEcmaArray.get(KEY_DURATION)).doubleValue();
            if (doubleValue > 0.0d) {
                setDurationUs((long) (doubleValue * 1000000.0d));
            }
        }
    }

    public void seek() {
    }
}
