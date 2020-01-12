package network;

import utilities.Checksum;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import static network.Constants.Protocol.UDP_HEADER_SIZE;

public final class UDPHeader implements Serializable {
    public final short senderPort;
    public final short destinationPort;
    public final short length;
    public final int checksum;

    UDPHeader(short senderPort, short destinationPort, short dataLength) {
        this.senderPort = senderPort;
        this.destinationPort = destinationPort;
        this.length = (short) (UDP_HEADER_SIZE + dataLength);
        checksum = Checksum.generateChecksum(getBytes());
    }

    UDPHeader(UDPHeader header) {
        senderPort = header.senderPort;
        destinationPort = header.destinationPort;
        length = header.length;
        checksum = header.checksum;
    }

    public byte[] getBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(senderPort);
        out.write(destinationPort);
        out.write(length);
        return out.toByteArray();
    }

    @Override
    public String toString() {
        return "UDPHeader{" +
                "senderPort=" + senderPort +
                ", destinationPort=" + destinationPort +
                ", length=" + length +
                '}';
    }
}
