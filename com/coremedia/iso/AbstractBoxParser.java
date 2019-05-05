package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.UserBox;
import com.googlecode.mp4parser.DataSource;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public abstract class AbstractBoxParser implements BoxParser {
    private static Logger LOG;
    ThreadLocal<ByteBuffer> header;

    /* renamed from: com.coremedia.iso.AbstractBoxParser.1 */
    class C00801 extends ThreadLocal<ByteBuffer> {
        C00801() {
        }

        protected ByteBuffer initialValue() {
            return ByteBuffer.allocate(32);
        }
    }

    static {
        LOG = Logger.getLogger(AbstractBoxParser.class.getName());
    }

    public AbstractBoxParser() {
        this.header = new C00801();
    }

    public abstract Box createBox(String str, byte[] bArr, String str2);

    public Box parseBox(DataSource dataSource, Container container) {
        long position = dataSource.position();
        ((ByteBuffer) this.header.get()).rewind().limit(8);
        int read;
        do {
            read = dataSource.read((ByteBuffer) this.header.get());
            if (read == 8) {
                ((ByteBuffer) this.header.get()).rewind();
                position = IsoTypeReader.readUInt32((ByteBuffer) this.header.get());
                if (position >= 8 || position <= 1) {
                    long j;
                    byte[] bArr;
                    String read4cc = IsoTypeReader.read4cc((ByteBuffer) this.header.get());
                    if (position == 1) {
                        ((ByteBuffer) this.header.get()).limit(16);
                        dataSource.read((ByteBuffer) this.header.get());
                        ((ByteBuffer) this.header.get()).position(8);
                        position = IsoTypeReader.readUInt64((ByteBuffer) this.header.get()) - 16;
                    } else if (position == 0) {
                        long size = dataSource.size() - dataSource.position();
                        position = size + 8;
                        position = size;
                    } else {
                        position -= 8;
                    }
                    if (UserBox.TYPE.equals(read4cc)) {
                        ((ByteBuffer) this.header.get()).limit(((ByteBuffer) this.header.get()).limit() + 16);
                        dataSource.read((ByteBuffer) this.header.get());
                        byte[] bArr2 = new byte[16];
                        for (int position2 = ((ByteBuffer) this.header.get()).position() - 16; position2 < ((ByteBuffer) this.header.get()).position(); position2++) {
                            bArr2[position2 - (((ByteBuffer) this.header.get()).position() - 16)] = ((ByteBuffer) this.header.get()).get(position2);
                        }
                        j = position - 16;
                        bArr = bArr2;
                    } else {
                        bArr = null;
                        j = position;
                    }
                    Box createBox = createBox(read4cc, bArr, container instanceof Box ? ((Box) container).getType() : TtmlNode.ANONYMOUS_REGION_ID);
                    createBox.setParent(container);
                    ((ByteBuffer) this.header.get()).rewind();
                    createBox.parse(dataSource, (ByteBuffer) this.header.get(), j, this);
                    return createBox;
                }
                LOG.severe("Plausibility check failed: size < 8 (size = " + position + "). Stop parsing!");
                return null;
            }
        } while (read >= 0);
        dataSource.position(position);
        throw new EOFException();
    }
}
