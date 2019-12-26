package network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class IPPacket extends Packet {
    private IPHeader ipHeader;
    private TCPPacket TCPPacket;

    public IPPacket(IPHeader ipHeader, TCPPacket packet) {
        this.ipHeader = ipHeader;
        this.TCPPacket = packet;
    }

    public IPPacket(IPPacket packet) {
        ipHeader = new IPHeader(packet.getIpHeader());
        TCPPacket = new TCPPacket(packet.TCPPacket);

    }

    public int length() {
        return (ipHeader.getTotalLength() + TCPPacket.getLength());
    }

    public IPHeader getIpHeader() {
        return ipHeader;
    }

    public void setIpHeader(IPHeader ipHeader) {
        this.ipHeader = ipHeader;
    }

    public TCPPacket getTCPPacket() {
        return TCPPacket;
    }

    public void setPacket(TCPPacket packet) {
        this.TCPPacket = packet;
    }

    @Override
    public byte[] toByteArray() {
        byte[] packetBytes = TCPPacket.toByteArray();
        byte[] ip = new byte[0];
        try {
            ip = ipHeader.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] combined = new byte[packetBytes.length + ip.length];
        System.arraycopy(packetBytes,0,combined,0,packetBytes.length);
        System.arraycopy(ip,0,combined,packetBytes.length, ip.length);
        return combined;
    }

    @Override
    public Packet copy() {
        return new IPPacket(this);
    }


}
