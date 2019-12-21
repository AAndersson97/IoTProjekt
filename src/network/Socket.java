package network;

import java.io.*;
import java.net.*;

/**
 * Klassen hanterar Sockets på både klient- och serversidan. Den hanterar enbart ip-adresser och ej värdnamn.
 */
public class Socket extends SocketImpl implements Constants, AutoCloseable {
    private boolean open, connected, bound;
    private InetAddress localAddress;
    private Object lock;
    private int fdUseCount;

    public Socket(InetAddress address, int port) throws IOException {
        this();
        if (port < 0 || port > 0xFFFF)
            throw new IllegalArgumentException("socket: Port out of range: " + port);
        bind(address, port);
    }

    public Socket() {
        connected = bound = false;
        lock = new Object();
        fdUseCount = 0;
    }

    @Override
    protected void create(boolean stream) throws IOException {

    }

    @Override
    protected void connect(String host, int port) throws IOException {
        connect(InetAddress.getByName(host), port);
    }

    @Override
    protected void connect(InetAddress address, int port) throws IOException {
        connect(new InetSocketAddress(address.getHostAddress(), port), 0);
    }

    @Override
    protected void connect(SocketAddress address, int timeout) throws IOException {
        if (address == null)
            throw new IllegalArgumentException("connect: The address must not be null");
        if (timeout < 0)
            throw new IllegalArgumentException("connect: The timeout can't be negative");
        if (!open)
            throw new SocketException("Socket is closed");
        if (connected)
            throw new SocketException("Socket is already connected");
        InetSocketAddress socketAddress = ((InetSocketAddress)address);
        InetAddress addr = socketAddress.getAddress();
        checkAddress(addr);
        this.port = socketAddress.getPort();
        this.address = addr;

        connected = true;
    }

    private void checkAddress(InetAddress address) {
        if (address == null)
            throw new IllegalArgumentException("Address must not be null");
        if (!(address instanceof Inet4Address))
            throw new IllegalArgumentException("Only IPv4 addresses are supported");
    }

    @Override
    protected void bind(InetAddress host, int port) throws IOException {
        if (!open)
            throw new SocketException("Socket is closed");
        if (bound)
            throw new SocketException("Socket is already bound");
        checkAddress(host);
        this.localAddress = host;
        this.localport = port;
        bound = true;
    }

    /**
     * Använder lås (ett godtycligt objekt) och synkronisering för att undvika att olika trådar stör varandra
     * @return
     */
    private FileDescriptor acquireFD() {
        synchronized (lock) {
            fdUseCount++;
            return fd;
        }
    }

    private void releaseFD() {
        synchronized (lock) {
            fdUseCount--;
            if (fdUseCount < 0 && fd != null) {
                try {
                    close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void listen(int backlog) throws IOException {

    }

    @Override
    protected void accept(SocketImpl s) throws IOException {

    }

    @Override
    protected InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    protected int available() throws IOException {
        return 0;
    }

    @Override
    protected void sendUrgentData(int data) throws IOException {

    }

    @Override
    public void setOption(int optID, Object value) throws SocketException {

    }

    @Override
    public Object getOption(int optID) throws SocketException {
        return null;
    }

    @Override
    public void close() throws IOException {
        fd = null;

    }
}
