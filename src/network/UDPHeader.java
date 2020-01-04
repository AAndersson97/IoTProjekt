package network;

import utilities.Checksum;

import java.io.ByteArrayOutputStream;
import static network.Constants.Protocol.UDP_HEADER_SIZE;

public class UDPHeader {
    private short senderPort;
    private short destinationPort;
    private short length;
    private int checksum;

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
