package old;

import network.IPHeader;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class HelloPacket extends OSPFPacket {
    private int networkMask;
    private byte priority;
    private short helloInterval;
    private short deadInterval;
    private int designatedRouterId;
    private int backupDRId;
    // Lista med grannars Id som routern nyligen inhämtat Hello-meddelanden
    private short[][] neighborIds;

    HelloPacket(HelloPacket packet) {
        networkMask = packet.networkMask;
        priority = packet.priority;
        helloInterval = packet.helloInterval;
        deadInterval = packet.deadInterval;
        designatedRouterId = packet.designatedRouterId;
        backupDRId = packet.backupDRId;
        neighborIds = Arrays.copyOf(packet.neighborIds, packet.neighborIds.length);
    }

    public HelloPacket(IPHeader ipHeader, OSPFHeader header, short[][] neighborIds, int DRId) {
        this.ipHeader = ipHeader;
        priority = 1;
        backupDRId = 0;
        designatedRouterId = DRId;
        this.neighborIds = neighborIds;
        this.OSPFHeader = header;
    }
    // Paketets alla fält förutom neighborIds och header upptar 20 bytes
    public static int length(HelloPacket packet) {
        return packet.OSPFHeader.length() + 20 + (packet.neighborIds.length * packet.neighborIds[0].length);
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(networkMask);
        out.write(priority);
        out.write(helloInterval);
        out.write(deadInterval);
        out.write(designatedRouterId);
        out.write(backupDRId);
        for (short[] address : neighborIds)
            for (short num : address)
                out.write(num);
        return out.toByteArray();
    }

    public OSPFPacket copy() {
        return new HelloPacket(this);
    }
}