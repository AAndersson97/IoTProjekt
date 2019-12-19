package network;

import java.util.ArrayList;
import java.util.Arrays;

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

    @Override
    public byte[] toByteArray() {
        byte[] tcp = tcpPacket.toByteArray();
        byte[] ip = ipHeader.toByteArray();
        byte[] combined = new byte[tcp.length + ip.length];
        System.arraycopy(tcp,0,combined,0,tcp.length);
        System.arraycopy(ip,0,combined,tcp.length, ip.length);
        return combined;
    }
}
