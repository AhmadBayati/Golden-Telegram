package com.googlecode.mp4parser.boxes.cenc;

import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CencEncryptingSampleList extends AbstractList<Sample> {
    List<CencSampleAuxiliaryDataFormat> auxiliaryDataFormats;
    RangeStartMap<Integer, SecretKey> ceks;
    Cipher cipher;
    private final String encryptionAlgo;
    List<Sample> parent;

    private class EncryptedSampleImpl implements Sample {
        static final /* synthetic */ boolean $assertionsDisabled;
        private final SecretKey cek;
        private final CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat;
        private final Cipher cipher;
        private final Sample clearSample;

        static {
            $assertionsDisabled = !CencEncryptingSampleList.class.desiredAssertionStatus();
        }

        private EncryptedSampleImpl(Sample sample, CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat, Cipher cipher, SecretKey secretKey) {
            this.clearSample = sample;
            this.cencSampleAuxiliaryDataFormat = cencSampleAuxiliaryDataFormat;
            this.cipher = cipher;
            this.cek = secretKey;
        }

        public ByteBuffer asByteBuffer() {
            ByteBuffer byteBuffer = (ByteBuffer) this.clearSample.asByteBuffer().rewind();
            ByteBuffer allocate = ByteBuffer.allocate(byteBuffer.limit());
            CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat = this.cencSampleAuxiliaryDataFormat;
            CencEncryptingSampleList.this.initCipher(this.cencSampleAuxiliaryDataFormat.iv, this.cek);
            try {
                if (cencSampleAuxiliaryDataFormat.pairs != null) {
                    for (Pair pair : cencSampleAuxiliaryDataFormat.pairs) {
                        byte[] bArr = new byte[pair.clear()];
                        byteBuffer.get(bArr);
                        allocate.put(bArr);
                        if (pair.encrypted() > 0) {
                            byte[] bArr2 = new byte[CastUtils.l2i(pair.encrypted())];
                            byteBuffer.get(bArr2);
                            if ($assertionsDisabled || bArr2.length % 16 == 0) {
                                bArr = this.cipher.update(bArr2);
                                if ($assertionsDisabled || bArr.length == bArr2.length) {
                                    allocate.put(bArr);
                                } else {
                                    throw new AssertionError();
                                }
                            }
                            throw new AssertionError();
                        }
                    }
                } else {
                    byte[] bArr3 = new byte[byteBuffer.limit()];
                    byteBuffer.get(bArr3);
                    if ("cbc1".equals(CencEncryptingSampleList.this.encryptionAlgo)) {
                        int length = (bArr3.length / 16) * 16;
                        allocate.put(this.cipher.doFinal(bArr3, 0, length));
                        allocate.put(bArr3, length, bArr3.length - length);
                    } else if ("cenc".equals(CencEncryptingSampleList.this.encryptionAlgo)) {
                        allocate.put(this.cipher.doFinal(bArr3));
                    }
                }
                byteBuffer.rewind();
                allocate.rewind();
                return allocate;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            } catch (Throwable e2) {
                throw new RuntimeException(e2);
            }
        }

        public long getSize() {
            return this.clearSample.getSize();
        }

        public void writeTo(WritableByteChannel writableByteChannel) {
            int i = 0;
            ByteBuffer byteBuffer = (ByteBuffer) this.clearSample.asByteBuffer().rewind();
            CencEncryptingSampleList.this.initCipher(this.cencSampleAuxiliaryDataFormat.iv, this.cek);
            try {
                if (this.cencSampleAuxiliaryDataFormat.pairs == null || this.cencSampleAuxiliaryDataFormat.pairs.length <= 0) {
                    byte[] bArr = new byte[byteBuffer.limit()];
                    byteBuffer.get(bArr);
                    if ("cbc1".equals(CencEncryptingSampleList.this.encryptionAlgo)) {
                        int length = (bArr.length / 16) * 16;
                        writableByteChannel.write(ByteBuffer.wrap(this.cipher.doFinal(bArr, 0, length)));
                        writableByteChannel.write(ByteBuffer.wrap(bArr, length, bArr.length - length));
                    } else if ("cenc".equals(CencEncryptingSampleList.this.encryptionAlgo)) {
                        writableByteChannel.write(ByteBuffer.wrap(this.cipher.doFinal(bArr)));
                    }
                } else {
                    byte[] bArr2 = new byte[byteBuffer.limit()];
                    byteBuffer.get(bArr2);
                    for (Pair pair : this.cencSampleAuxiliaryDataFormat.pairs) {
                        i += pair.clear();
                        if (pair.encrypted() > 0) {
                            this.cipher.update(bArr2, i, CastUtils.l2i(pair.encrypted()), bArr2, i);
                            i = (int) (((long) i) + pair.encrypted());
                        }
                    }
                    writableByteChannel.write(ByteBuffer.wrap(bArr2));
                }
                byteBuffer.rewind();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            } catch (Throwable e2) {
                throw new RuntimeException(e2);
            } catch (Throwable e22) {
                throw new RuntimeException(e22);
            }
        }
    }

    public CencEncryptingSampleList(RangeStartMap<Integer, SecretKey> rangeStartMap, List<Sample> list, List<CencSampleAuxiliaryDataFormat> list2, String str) {
        this.ceks = new RangeStartMap();
        this.auxiliaryDataFormats = list2;
        this.ceks = rangeStartMap;
        this.encryptionAlgo = str;
        this.parent = list;
        try {
            if ("cenc".equals(str)) {
                this.cipher = Cipher.getInstance("AES/CTR/NoPadding");
            } else if ("cbc1".equals(str)) {
                this.cipher = Cipher.getInstance("AES/CBC/NoPadding");
            } else {
                throw new RuntimeException("Only cenc & cbc1 is supported as encryptionAlgo");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    public CencEncryptingSampleList(SecretKey secretKey, List<Sample> list, List<CencSampleAuxiliaryDataFormat> list2) {
        this(new RangeStartMap(Integer.valueOf(0), secretKey), list, list2, "cenc");
    }

    public Sample get(int i) {
        Sample sample = (Sample) this.parent.get(i);
        return this.ceks.get(Integer.valueOf(i)) != null ? new EncryptedSampleImpl(sample, (CencSampleAuxiliaryDataFormat) this.auxiliaryDataFormats.get(i), this.cipher, (SecretKey) this.ceks.get(Integer.valueOf(i)), null) : sample;
    }

    protected void initCipher(byte[] bArr, SecretKey secretKey) {
        try {
            Object obj = new byte[16];
            System.arraycopy(bArr, 0, obj, 0, bArr.length);
            this.cipher.init(1, secretKey, new IvParameterSpec(obj));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    public int size() {
        return this.parent.size();
    }
}
