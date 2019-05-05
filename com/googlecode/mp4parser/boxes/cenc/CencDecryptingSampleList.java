package com.googlecode.mp4parser.boxes.cenc;

import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CencDecryptingSampleList extends AbstractList<Sample> {
    String encryptionAlgo;
    RangeStartMap<Integer, SecretKey> keys;
    List<Sample> parent;
    List<CencSampleAuxiliaryDataFormat> sencInfo;

    public CencDecryptingSampleList(RangeStartMap<Integer, SecretKey> rangeStartMap, List<Sample> list, List<CencSampleAuxiliaryDataFormat> list2, String str) {
        this.keys = new RangeStartMap();
        this.sencInfo = list2;
        this.keys = rangeStartMap;
        this.parent = list;
        this.encryptionAlgo = str;
    }

    public CencDecryptingSampleList(SecretKey secretKey, List<Sample> list, List<CencSampleAuxiliaryDataFormat> list2) {
        this(new RangeStartMap(Integer.valueOf(0), secretKey), list, list2, "cenc");
    }

    public Sample get(int i) {
        if (this.keys.get(Integer.valueOf(i)) == null) {
            return (Sample) this.parent.get(i);
        }
        Sample sample = (Sample) this.parent.get(i);
        ByteBuffer asByteBuffer = sample.asByteBuffer();
        asByteBuffer.rewind();
        ByteBuffer allocate = ByteBuffer.allocate(asByteBuffer.limit());
        CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat = (CencSampleAuxiliaryDataFormat) this.sencInfo.get(i);
        Cipher cipher = getCipher((SecretKey) this.keys.get(Integer.valueOf(i)), cencSampleAuxiliaryDataFormat.iv);
        try {
            int length;
            if (cencSampleAuxiliaryDataFormat.pairs == null || cencSampleAuxiliaryDataFormat.pairs.length <= 0) {
                byte[] bArr = new byte[asByteBuffer.limit()];
                asByteBuffer.get(bArr);
                if ("cbc1".equals(this.encryptionAlgo)) {
                    length = (bArr.length / 16) * 16;
                    allocate.put(cipher.doFinal(bArr, 0, length));
                    allocate.put(bArr, length, bArr.length - length);
                } else if ("cenc".equals(this.encryptionAlgo)) {
                    allocate.put(cipher.doFinal(bArr));
                }
            } else {
                for (Pair pair : cencSampleAuxiliaryDataFormat.pairs) {
                    int clear = pair.clear();
                    int l2i = CastUtils.l2i(pair.encrypted());
                    byte[] bArr2 = new byte[clear];
                    asByteBuffer.get(bArr2);
                    allocate.put(bArr2);
                    if (l2i > 0) {
                        byte[] bArr3 = new byte[l2i];
                        asByteBuffer.get(bArr3);
                        allocate.put(cipher.update(bArr3));
                    }
                }
                if (asByteBuffer.remaining() > 0) {
                    System.err.println("Decrypted sample but still data remaining: " + sample.getSize());
                }
                allocate.put(cipher.doFinal());
            }
            asByteBuffer.rewind();
            allocate.rewind();
            return new SampleImpl(allocate);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    Cipher getCipher(SecretKey secretKey, byte[] bArr) {
        Object obj = new byte[16];
        System.arraycopy(bArr, 0, obj, 0, bArr.length);
        try {
            Cipher instance;
            if ("cenc".equals(this.encryptionAlgo)) {
                instance = Cipher.getInstance("AES/CTR/NoPadding");
                instance.init(2, secretKey, new IvParameterSpec(obj));
                return instance;
            } else if ("cbc1".equals(this.encryptionAlgo)) {
                instance = Cipher.getInstance("AES/CBC/NoPadding");
                instance.init(2, secretKey, new IvParameterSpec(obj));
                return instance;
            } else {
                throw new RuntimeException("Only cenc & cbc1 is supported as encryptionAlgo");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        } catch (Throwable e22) {
            throw new RuntimeException(e22);
        } catch (Throwable e222) {
            throw new RuntimeException(e222);
        }
    }

    public int size() {
        return this.parent.size();
    }
}
