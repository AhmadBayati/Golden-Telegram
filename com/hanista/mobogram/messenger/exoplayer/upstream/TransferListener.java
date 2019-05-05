package com.hanista.mobogram.messenger.exoplayer.upstream;

public interface TransferListener {
    void onBytesTransferred(int i);

    void onTransferEnd();

    void onTransferStart();
}
