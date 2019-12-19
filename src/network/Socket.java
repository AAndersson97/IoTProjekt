package network;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Socket implements Constants {
    private boolean open;
    private int[] openPorts;
    private PipedInputStream input;
    private PipedOutputStream output;
    private InetAddress address;

    public Socket(InetAddress address, int[] openPorts) {
        this.address = address;
        this.openPorts = openPorts;
    }

    public void listen() {
    }

    public void open() throws IOException{
        output = new PipedOutputStream();
        input = new PipedInputStream(output);
        open = true;
    }

    public synchronized void send(Packet packet) throws IOException {
        if (!open)
            throw new IOException("Socket is down");
        output.write(packet.toByteArray());
    }

    public byte[] read() throws IOException{
        if (!open)
            throw new IOException("Socket is down");
        return input.readAllBytes();
    }

    public boolean isOpen() {
        return open;
    }

    public void close() throws IOException {
        if (!open)
            throw new IOException("Socket is already closed");
        open = false;
        input.close();
        output.close();
    }
}
