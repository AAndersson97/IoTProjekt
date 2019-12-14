package network;

import java.util.Arrays;

public class IPPacket {
    private IPHeader ipHeader;
    private TCPPacket tcpPacket;

    public IPPacket(IPHeader ipHeader, TCPPacket tcpPacket) {
        this.ipHeader = ipHeader;
        this.tcpPacket = tcpPacket;
    }

    public byte[] toByteArray() {

     return null;
    }
}
