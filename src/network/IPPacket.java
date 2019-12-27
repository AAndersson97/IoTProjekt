package network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Klassen representerar ett IP Paket. Metoderna är statiska då det skapar onödigt "overhead" om varje paket ska ha sin egna kopia av metoden.
 */
public class IPPacket implements Packet {
    private IPHeader ipHeader;
    private TCPPacket TCPPacket;

    public IPPacket(IPHeader ipHeader, TCPPacket packet) {
        this.ipHeader = ipHeader;
        this.TCPPacket = packet;
    }

    public IPPacket(IPPacket packet) {
        ipHeader = new IPHeader(getIpHeader(packet));
        TCPPacket = new TCPPacket(packet.TCPPacket);

    }

    public static int length(IPPacket packet) {
        return (packet.ipHeader.getTotalLength() + packet.TCPPacket.getLength());
    }

    public static IPHeader getIpHeader(IPPacket packet) {
        return packet.ipHeader;
    }

    public static TCPPacket getTCPPacket(IPPacket packet) {
        return packet.TCPPacket;
    }

    public static byte[] toByteArray(IPPacket packet) {
        byte[] packetBytes = packet.TCPPacket.toByteArray();
        byte[] ip = new byte[0];
        try {
            ip = packet.ipHeader.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] combined = new byte[packetBytes.length + ip.length];
        System.arraycopy(packetBytes,0,combined,0,packetBytes.length);
        System.arraycopy(ip,0,combined,packetBytes.length, ip.length);
        return combined;
    }

    public static Packet copy(IPPacket packet) {
        return new IPPacket(packet);
    }
}
