package network;

import network.utilities.PacketIdGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class Packet {
    public int PACKET_ID;
    public WifiMacHeader wifiMacHeader;
    public IPHeader ipHeader;
    public UDPHeader udpHeader;
    public OLSRHeader olsrHeader;
    public WifiMacTrailer wifiMacTrailer;

    public Packet(IPHeader ipHeader, UDPHeader udpHeader, OLSRHeader header) {
        this.olsrHeader = header;
        this.ipHeader = ipHeader;
        this.udpHeader = udpHeader;
        this.PACKET_ID = PacketIdGenerator.getPacketId();
    }
    public Packet(IPHeader ipHeader, UDPHeader udpHeader, OLSRHeader header, int packetId) {
        this.olsrHeader = header;
        this.ipHeader = ipHeader;
        this.udpHeader = udpHeader;
        this.PACKET_ID = packetId;
    }

    public Packet(WifiMacHeader header, WifiMacTrailer trailer, IPHeader ipHeader, UDPHeader udpHeader, OLSRHeader olsrHeader) {
        this(ipHeader, udpHeader, olsrHeader);
        this.wifiMacHeader = header;
        this.wifiMacTrailer = trailer;
    }

    public Packet(WifiMacHeader header, WifiMacTrailer trailer, IPHeader ipHeader, UDPHeader udpHeader, OLSRHeader olsrHeader, int id) {
        this(ipHeader, udpHeader, olsrHeader, id);
        this.wifiMacHeader = header;
        this.wifiMacTrailer = trailer;
    }


    public byte[] toBytes() {
        if (wifiMacHeader == null || ipHeader == null ||udpHeader == null|| wifiMacTrailer == null)
            throw new NullPointerException("The headers and trailers must not be null");
        byte[] bytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(wifiMacHeader);
            oos.writeObject(ipHeader);
            oos.writeObject(udpHeader);
            oos.writeObject(wifiMacTrailer);
            oos.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return bytes;
    }

    public void setWifiMacHeader(WifiMacHeader wifiMacHeader) {
        this.wifiMacHeader = wifiMacHeader;
    }

    public void setWifiMacTrailer(WifiMacTrailer wifiMacTrailer) {
        this.wifiMacTrailer = wifiMacTrailer;
    }

    public abstract Packet copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Packet packet = (Packet) o;

        return PACKET_ID == packet.PACKET_ID;
    }

    @Override
    public int hashCode() {
        return PACKET_ID;
    }
}
