package network;

import javafx.scene.Node;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

public class HelloPacket extends OSPFPacket {
    private int networkMask;
    private byte priority;
    private short helloInterval;
    private short deadInterval;
    private int designatedRouterId;
    private int backupDRId;
    // Lista med grannars Id som routern nyligen inhämtat Hello-meddelanden
    private int[] neighborIds;

    HelloPacket(HelloPacket packet) {
        networkMask = packet.networkMask;
        priority = packet.priority;
        helloInterval = packet.helloInterval;
        deadInterval = packet.deadInterval;
        designatedRouterId = packet.designatedRouterId;
        backupDRId = packet.backupDRId;
        neighborIds = Arrays.copyOf(packet.neighborIds, packet.neighborIds.length);
    }

    public HelloPacket(IPHeader ipHeader, OSPFHeader header, int[] neighborIds, int DRId) {
        this.ipHeader = ipHeader;
        helloInterval = Constants.HELLO_INTERVAL;
        deadInterval = Constants.DEAD_INTERVAL;
        priority = 1;
        backupDRId = 0;
        designatedRouterId = DRId;
        this.neighborIds = neighborIds;
        this.OSPFHeader = header;
    }
    // Paketets alla fält förutom neighborIds och header upptar 20 bytes
    @Override
    public int length() {
        return OSPFHeader.length() + 20 + neighborIds.length;
    }

    @Override
    public byte[] toByteArray() {
        return null;
    }

    @Override
    public OSPFPacket copy() {
        return new HelloPacket(this);
    }
}
