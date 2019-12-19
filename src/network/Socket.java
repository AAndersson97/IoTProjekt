package network;

import java.io.*;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class Socket implements Constants {
    private boolean open;
    private int port;
    private PipedInputStream input;
    private InetAddress address;

    public Socket(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void open() {
        input = new PipedInputStream();
        open = true;
    }

    public synchronized void connect(PipedOutputStream output) throws IOException {
        if (!open)
            throw new IOException("Socket is down");
        input.connect(output);
    }

    public synchronized void disconnect() throws IOException {
        input.close();
    }

    public byte[] read() throws IOException{
        if (!open)
            throw new IOException("Socket is down");
        return input.readAllBytes();
    }

    public int bytesToRead() throws IOException {
        return input.available();
    }

    public boolean isOpen() {
        return open;
    }

    public void close() throws IOException {
        if (!open)
            throw new IOException("Socket is already closed");
        open = false;
        input.close();
    }

    public int getPort() {
        return port;
    }
}
