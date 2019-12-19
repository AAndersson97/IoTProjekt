package network;

import java.io.IOException;
import java.net.InetAddress;

public class OSPFServer implements Constants {
    private boolean multicast;
    public OSPFServer(InetAddress address) {
        multicast = false;
        configureSocket(address);
    }

    private void configureSocket(InetAddress address) {

    }

}
