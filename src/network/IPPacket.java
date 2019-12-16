package network;

import java.util.ArrayList;

public class IPPacket extends Packet {
    private IPHeader ipHeader;
    private TCPPacket tcpPacket;

    public IPPacket(IPHeader ipHeader, TCPPacket tcpPacket) {
        this.ipHeader = ipHeader;
        this.tcpPacket = tcpPacket;
    }

    public int length() {
        return (ipHeader.getTotalLength() + tcpPacket.getLength());
    }

    public IPHeader getIpHeader() {
        return ipHeader;
    }

    public void setIpHeader(IPHeader ipHeader) {
        this.ipHeader = ipHeader;
    }

    public TCPPacket getTcpPacket() {
        return tcpPacket;
    }

    public void setTcpPacket(TCPPacket tcpPacket) {
        this.tcpPacket = tcpPacket;
    }
}
