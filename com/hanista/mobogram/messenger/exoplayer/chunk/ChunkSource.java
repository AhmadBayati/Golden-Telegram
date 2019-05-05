package com.hanista.mobogram.messenger.exoplayer.chunk;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import java.util.List;

public interface ChunkSource {
    void continueBuffering(long j);

    void disable(List<? extends MediaChunk> list);

    void enable(int i);

    void getChunkOperation(List<? extends MediaChunk> list, long j, ChunkOperationHolder chunkOperationHolder);

    MediaFormat getFormat(int i);

    int getTrackCount();

    void maybeThrowError();

    void onChunkLoadCompleted(Chunk chunk);

    void onChunkLoadError(Chunk chunk, Exception exception);

    boolean prepare();
}
