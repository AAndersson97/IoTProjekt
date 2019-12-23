package network;

import utilities.Checksum;

import java.io.ByteArrayOutputStream;

public class OSPFHeader extends Header {
    private static final int HEADER_SIZE = 24;
    // Då programmet använder IPv4 används version 2 av OSPF-protokollet
    private final byte version = (byte) 0b10;
    private int type;
    private int length;
    private int routerID;
    private int areaID;
    private int checkSum;
    private short authentication;

    OSPFHeader(OSPFHeader header) {
        this.type = header.type;
        this.length = header.length;
        this.routerID = header.routerID;
        this.areaID = header.areaID;
        this.checkSum = header.checkSum;
        this.authentication = header.authentication;
    }
    OSPFHeader(OSPFPacketType type, int dataLength, int areaID, int routerID) {
        this.type = type.value;
        this.length = HEADER_SIZE + dataLength;
        this.routerID = routerID;
        // Area som paketet tillhör/ska till
        this.areaID = areaID;
        // Ingen autentisering
        authentication = 0;
        checkSum = Checksum.generateChecksum(toByteArray());
    }

    public int length() {
        return length;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(version);
        out.write(type);
        out.write(routerID);
        out.write(areaID);
        out.write(length);
        out.write(authentication);
        out.write(checkSum);
        return out.toByteArray();
    }

    @Override
    public Header copy() {
        return new OSPFHeader(this);
    }
}
