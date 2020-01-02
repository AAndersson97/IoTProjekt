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

    public byte[] getBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(senderPort);
        out.write(destinationPort);
        out.write(length);
        return out.toByteArray();
    }

}
