package network;

import utilities.Checksum;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class OSPFHeader {
    private static final int HEADER_SIZE = 24;
    private byte version;
    private int type;
    private int length;
    private int routerID;
    private int areaID;
    private int checkSum;
    private byte instanceID;
    private byte reserved;

    OSPFHeader(OSPFPacketType type, int length, int id) {
        version = (byte)3;
        this.type = type.value;
        this.length = length;
        routerID = areaID = 0;
        instanceID = (byte) id;
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
        out.write(instanceID);
        out.write(reserved);

        return out.toByteArray();

    }

}
