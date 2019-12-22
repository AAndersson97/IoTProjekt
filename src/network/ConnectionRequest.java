package network;

import java.net.InetAddress;

public interface ConnectionRequest {
    void requestConnection(InetAddress source);
}
