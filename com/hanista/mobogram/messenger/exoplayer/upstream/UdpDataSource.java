package com.hanista.mobogram.messenger.exoplayer.upstream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

public final class UdpDataSource implements UriDataSource {
    public static final int DEAFULT_SOCKET_TIMEOUT_MILLIS = 8000;
    public static final int DEFAULT_MAX_PACKET_SIZE = 2000;
    private InetAddress address;
    private DataSpec dataSpec;
    private final TransferListener listener;
    private MulticastSocket multicastSocket;
    private boolean opened;
    private final DatagramPacket packet;
    private byte[] packetBuffer;
    private int packetRemaining;
    private DatagramSocket socket;
    private InetSocketAddress socketAddress;
    private final int socketTimeoutMillis;

    public static final class UdpDataSourceException extends IOException {
        public UdpDataSourceException(IOException iOException) {
            super(iOException);
        }

        public UdpDataSourceException(String str) {
            super(str);
        }
    }

    public UdpDataSource(TransferListener transferListener) {
        this(transferListener, DEFAULT_MAX_PACKET_SIZE);
    }

    public UdpDataSource(TransferListener transferListener, int i) {
        this(transferListener, i, DEAFULT_SOCKET_TIMEOUT_MILLIS);
    }

    public UdpDataSource(TransferListener transferListener, int i, int i2) {
        this.listener = transferListener;
        this.socketTimeoutMillis = i2;
        this.packetBuffer = new byte[i];
        this.packet = new DatagramPacket(this.packetBuffer, 0, i);
    }

    public void close() {
        if (this.multicastSocket != null) {
            try {
                this.multicastSocket.leaveGroup(this.address);
            } catch (IOException e) {
            }
            this.multicastSocket = null;
        }
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }
        this.address = null;
        this.socketAddress = null;
        this.packetRemaining = 0;
        if (this.opened) {
            this.opened = false;
            if (this.listener != null) {
                this.listener.onTransferEnd();
            }
        }
    }

    public String getUri() {
        return this.dataSpec == null ? null : this.dataSpec.uri.toString();
    }

    public long open(DataSpec dataSpec) {
        this.dataSpec = dataSpec;
        String host = dataSpec.uri.getHost();
        int port = dataSpec.uri.getPort();
        try {
            this.address = InetAddress.getByName(host);
            this.socketAddress = new InetSocketAddress(this.address, port);
            if (this.address.isMulticastAddress()) {
                this.multicastSocket = new MulticastSocket(this.socketAddress);
                this.multicastSocket.joinGroup(this.address);
                this.socket = this.multicastSocket;
            } else {
                this.socket = new DatagramSocket(this.socketAddress);
            }
            try {
                this.socket.setSoTimeout(this.socketTimeoutMillis);
                this.opened = true;
                if (this.listener != null) {
                    this.listener.onTransferStart();
                }
                return -1;
            } catch (IOException e) {
                throw new UdpDataSourceException(e);
            }
        } catch (IOException e2) {
            throw new UdpDataSourceException(e2);
        }
    }

    public int read(byte[] bArr, int i, int i2) {
        if (this.packetRemaining == 0) {
            try {
                this.socket.receive(this.packet);
                this.packetRemaining = this.packet.getLength();
                if (this.listener != null) {
                    this.listener.onBytesTransferred(this.packetRemaining);
                }
            } catch (IOException e) {
                throw new UdpDataSourceException(e);
            }
        }
        int length = this.packet.getLength() - this.packetRemaining;
        int min = Math.min(this.packetRemaining, i2);
        System.arraycopy(this.packetBuffer, length, bArr, i, min);
        this.packetRemaining -= min;
        return min;
    }
}
