package network;

import com.savarese.rocksaw.net.RawSocket;

import java.io.IOException;
import java.net.InetAddress;

public class OSPFServer implements Constants {
    private RawSocket socket;
    private int a;
    private boolean multicast;
    public OSPFServer(InetAddress address) {
        multicast = false;
        configureSocket(address);
    }

    private void configureSocket(InetAddress address) {
        socket = new RawSocket();

        try {
            socket.open(RawSocket.PF_INET, 89);
            socket.setUseSelectTimeout(true);
            socket.setReceiveTimeout(TIME_OUT);
            socket.setSendTimeout(TIME_OUT);
            if (multicast) {
                socket.setIPHeaderInclude(false);
            } else {
                socket.bind(address);
            }
            System.out.println("Socket Opened");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
