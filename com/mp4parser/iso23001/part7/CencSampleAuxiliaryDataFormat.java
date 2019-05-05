package com.mp4parser.iso23001.part7;

import com.coremedia.iso.Hex;
import java.math.BigInteger;
import java.util.Arrays;

public class CencSampleAuxiliaryDataFormat {
    public byte[] iv;
    public Pair[] pairs;

    public interface Pair {
        int clear();

        long encrypted();
    }

    private abstract class AbstractPair implements Pair {
        private AbstractPair() {
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Pair pair = (Pair) obj;
            return clear() != pair.clear() ? false : encrypted() == pair.encrypted();
        }

        public String toString() {
            return "P(" + clear() + "|" + encrypted() + ")";
        }
    }

    private class ByteBytePair extends AbstractPair {
        private byte clear;
        private byte encrypted;

        public ByteBytePair(int i, long j) {
            super(null);
            this.clear = (byte) i;
            this.encrypted = (byte) ((int) j);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class ByteIntPair extends AbstractPair {
        private byte clear;
        private int encrypted;

        public ByteIntPair(int i, long j) {
            super(null);
            this.clear = (byte) i;
            this.encrypted = (int) j;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class ByteLongPair extends AbstractPair {
        private byte clear;
        private long encrypted;

        public ByteLongPair(int i, long j) {
            super(null);
            this.clear = (byte) i;
            this.encrypted = j;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return this.encrypted;
        }
    }

    private class ByteShortPair extends AbstractPair {
        private byte clear;
        private short encrypted;

        public ByteShortPair(int i, long j) {
            super(null);
            this.clear = (byte) i;
            this.encrypted = (short) ((int) j);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class IntBytePair extends AbstractPair {
        private int clear;
        private byte encrypted;

        public IntBytePair(int i, long j) {
            super(null);
            this.clear = i;
            this.encrypted = (byte) ((int) j);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class IntIntPair extends AbstractPair {
        private int clear;
        private int encrypted;

        public IntIntPair(int i, long j) {
            super(null);
            this.clear = i;
            this.encrypted = (int) j;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class IntLongPair extends AbstractPair {
        private int clear;
        private long encrypted;

        public IntLongPair(int i, long j) {
            super(null);
            this.clear = i;
            this.encrypted = j;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return this.encrypted;
        }
    }

    private class IntShortPair extends AbstractPair {
        private int clear;
        private short encrypted;

        public IntShortPair(int i, long j) {
            super(null);
            this.clear = i;
            this.encrypted = (short) ((int) j);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class ShortBytePair extends AbstractPair {
        private short clear;
        private byte encrypted;

        public ShortBytePair(int i, long j) {
            super(null);
            this.clear = (short) i;
            this.encrypted = (byte) ((int) j);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class ShortIntPair extends AbstractPair {
        private short clear;
        private int encrypted;

        public ShortIntPair(int i, long j) {
            super(null);
            this.clear = (short) i;
            this.encrypted = (int) j;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class ShortLongPair extends AbstractPair {
        private short clear;
        private long encrypted;

        public ShortLongPair(int i, long j) {
            super(null);
            this.clear = (short) i;
            this.encrypted = j;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return this.encrypted;
        }
    }

    private class ShortShortPair extends AbstractPair {
        private short clear;
        private short encrypted;

        public ShortShortPair(int i, long j) {
            super(null);
            this.clear = (short) i;
            this.encrypted = (short) ((int) j);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    public CencSampleAuxiliaryDataFormat() {
        this.iv = new byte[0];
        this.pairs = null;
    }

    public Pair createPair(int i, long j) {
        return i <= 127 ? j <= 127 ? new ByteBytePair(i, j) : j <= 32767 ? new ByteShortPair(i, j) : j <= 2147483647L ? new ByteIntPair(i, j) : new ByteLongPair(i, j) : i <= 32767 ? j <= 127 ? new ShortBytePair(i, j) : j <= 32767 ? new ShortShortPair(i, j) : j <= 2147483647L ? new ShortIntPair(i, j) : new ShortLongPair(i, j) : j <= 127 ? new IntBytePair(i, j) : j <= 32767 ? new IntShortPair(i, j) : j <= 2147483647L ? new IntIntPair(i, j) : new IntLongPair(i, j);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat = (CencSampleAuxiliaryDataFormat) obj;
        if (!new BigInteger(this.iv).equals(new BigInteger(cencSampleAuxiliaryDataFormat.iv))) {
            return false;
        }
        if (this.pairs != null) {
            if (Arrays.equals(this.pairs, cencSampleAuxiliaryDataFormat.pairs)) {
                return true;
            }
        } else if (cencSampleAuxiliaryDataFormat.pairs == null) {
            return true;
        }
        return false;
    }

    public int getSize() {
        int length = this.iv.length;
        return (this.pairs == null || this.pairs.length <= 0) ? length : (length + 2) + (this.pairs.length * 6);
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (this.iv != null ? Arrays.hashCode(this.iv) : 0) * 31;
        if (this.pairs != null) {
            i = Arrays.hashCode(this.pairs);
        }
        return hashCode + i;
    }

    public String toString() {
        return "Entry{iv=" + Hex.encodeHex(this.iv) + ", pairs=" + Arrays.toString(this.pairs) + '}';
    }
}
