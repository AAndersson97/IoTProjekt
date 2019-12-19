package network;

import java.io.*;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class Socket implements Constants {
    private boolean open;
    private int[] openPorts;
    private PipedInputStream input;
    private PipedOutputStream output;
    private InetAddress address;
    private DataListener listener;

    public Socket(InetAddress address, int[] openPorts) {
        this.address = address;
        this.openPorts = openPorts;
    }

    public void open() throws IOException {
        output = new PipedOutputStream();
        input = new PipedInputStream(output);
        open = true;
    }

    public synchronized void send(Packet packet, int port) throws IOException {
        if (!open)
            throw new IOException("Socket is down");
        output.write(packet.toByteArray());
        listener.dataRetrieved(port);
    }

    public boolean isPortOpen(int port) {
        for (int i = 0; i < openPorts.length; i++)
            if (openPorts[i] == port)
                return true;
        return false;
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

    @FunctionalInterface
    interface DataListener {
        void dataRetrieved(int port);
    }
}
