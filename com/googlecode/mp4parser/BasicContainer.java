package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.LazyList;
import com.googlecode.mp4parser.util.Logger;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class BasicContainer implements Container, Closeable, Iterator<Box> {
    private static final Box EOF;
    private static Logger LOG;
    protected BoxParser boxParser;
    private List<Box> boxes;
    protected DataSource dataSource;
    long endPosition;
    Box lookahead;
    long parsePosition;
    long startPosition;

    /* renamed from: com.googlecode.mp4parser.BasicContainer.1 */
    class C03181 extends AbstractBox {
        C03181(String str) {
            super(str);
        }

        protected void _parseDetails(ByteBuffer byteBuffer) {
        }

        protected void getContent(ByteBuffer byteBuffer) {
        }

        protected long getContentSize() {
            return 0;
        }
    }

    static {
        EOF = new C03181("eof ");
        LOG = Logger.getLogger(BasicContainer.class);
    }

    public BasicContainer() {
        this.lookahead = null;
        this.parsePosition = 0;
        this.startPosition = 0;
        this.endPosition = 0;
        this.boxes = new ArrayList();
    }

    public void addBox(Box box) {
        if (box != null) {
            this.boxes = new ArrayList(getBoxes());
            box.setParent(this);
            this.boxes.add(box);
        }
    }

    public void close() {
        this.dataSource.close();
    }

    public List<Box> getBoxes() {
        return (this.dataSource == null || this.lookahead == EOF) ? this.boxes : new LazyList(this.boxes, this);
    }

    public <T extends Box> List<T> getBoxes(Class<T> cls) {
        Box box = null;
        List boxes = getBoxes();
        int i = 0;
        List list = null;
        while (i < boxes.size()) {
            List list2;
            Box box2 = (Box) boxes.get(i);
            if (cls.isInstance(box2)) {
                if (box == null) {
                    list2 = list;
                    i++;
                    list = list2;
                    box = box2;
                } else {
                    if (list == null) {
                        list = new ArrayList(2);
                        list.add(box);
                    }
                    list.add(box2);
                }
            }
            box2 = box;
            list2 = list;
            i++;
            list = list2;
            box = box2;
        }
        return list != null ? list : box != null ? Collections.singletonList(box) : Collections.emptyList();
    }

    public <T extends Box> List<T> getBoxes(Class<T> cls, boolean z) {
        List<T> arrayList = new ArrayList(2);
        List boxes = getBoxes();
        for (int i = 0; i < boxes.size(); i++) {
            Box box = (Box) boxes.get(i);
            if (cls.isInstance(box)) {
                arrayList.add(box);
            }
            if (z && (box instanceof Container)) {
                arrayList.addAll(((Container) box).getBoxes(cls, z));
            }
        }
        return arrayList;
    }

    public ByteBuffer getByteBuffer(long j, long j2) {
        if (this.dataSource != null) {
            ByteBuffer map;
            synchronized (this.dataSource) {
                map = this.dataSource.map(this.startPosition + j, j2);
            }
            return map;
        }
        ByteBuffer allocate = ByteBuffer.allocate(CastUtils.l2i(j2));
        long j3 = j + j2;
        long j4 = 0;
        for (Box box : this.boxes) {
            long size = box.getSize() + j4;
            if (size > j && j4 < j3) {
                OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                WritableByteChannel newChannel = Channels.newChannel(byteArrayOutputStream);
                box.getBox(newChannel);
                newChannel.close();
                if (j4 >= j && size <= j3) {
                    allocate.put(byteArrayOutputStream.toByteArray());
                    j4 = size;
                } else if (j4 < j && size > j3) {
                    allocate.put(byteArrayOutputStream.toByteArray(), CastUtils.l2i(j - j4), CastUtils.l2i((box.getSize() - (j - j4)) - (size - j3)));
                    j4 = size;
                } else if (j4 < j && size <= j3) {
                    allocate.put(byteArrayOutputStream.toByteArray(), CastUtils.l2i(j - j4), CastUtils.l2i(box.getSize() - (j - j4)));
                    j4 = size;
                } else if (j4 >= j && size > j3) {
                    allocate.put(byteArrayOutputStream.toByteArray(), 0, CastUtils.l2i(box.getSize() - (size - j3)));
                }
            }
            j4 = size;
        }
        return (ByteBuffer) allocate.rewind();
    }

    protected long getContainerSize() {
        long j = 0;
        for (int i = 0; i < getBoxes().size(); i++) {
            j += ((Box) this.boxes.get(i)).getSize();
        }
        return j;
    }

    public boolean hasNext() {
        if (this.lookahead == EOF) {
            return false;
        }
        if (this.lookahead != null) {
            return true;
        }
        try {
            this.lookahead = next();
            return true;
        } catch (NoSuchElementException e) {
            this.lookahead = EOF;
            return false;
        }
    }

    public void initContainer(DataSource dataSource, long j, BoxParser boxParser) {
        this.dataSource = dataSource;
        long position = dataSource.position();
        this.startPosition = position;
        this.parsePosition = position;
        dataSource.position(dataSource.position() + j);
        this.endPosition = dataSource.position();
        this.boxParser = boxParser;
    }

    public Box next() {
        Box box;
        if (this.lookahead != null && this.lookahead != EOF) {
            box = this.lookahead;
            this.lookahead = null;
            return box;
        } else if (this.dataSource == null || this.parsePosition >= this.endPosition) {
            this.lookahead = EOF;
            throw new NoSuchElementException();
        } else {
            try {
                synchronized (this.dataSource) {
                    this.dataSource.position(this.parsePosition);
                    box = this.boxParser.parseBox(this.dataSource, this);
                    this.parsePosition = this.dataSource.position();
                }
                return box;
            } catch (EOFException e) {
                throw new NoSuchElementException();
            } catch (IOException e2) {
                throw new NoSuchElementException();
            }
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void setBoxes(List<Box> list) {
        this.boxes = new ArrayList(list);
        this.lookahead = EOF;
        this.dataSource = null;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClass().getSimpleName()).append("[");
        for (int i = 0; i < this.boxes.size(); i++) {
            if (i > 0) {
                stringBuilder.append(";");
            }
            stringBuilder.append(((Box) this.boxes.get(i)).toString());
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public final void writeContainer(WritableByteChannel writableByteChannel) {
        for (Box box : getBoxes()) {
            box.getBox(writableByteChannel);
        }
    }
}
