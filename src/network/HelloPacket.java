package network;

public class HelloPacket extends OSPFPacket{
    private OSPFHeader header;
    private int networkMask;
    private byte priority;
    private short helloInterval;
    private short deadInterval;
    private int designatedRouterId;
    private int backupDRId;
    // Lista med grannars Id som routern nyligen inhämtat Hello-meddelanden
    private int[] neighborIds;

    HelloPacket(OSPFHeader header, int[] neighborIds, int DRId) {
        helloInterval = Constants.HELLO_INTERVAL;
        deadInterval = Constants.DEAD_INTERVAL;
        priority = 1;
        backupDRId = 0;
        designatedRouterId = DRId;
        this.header = header;
    }
    // Paketets alla fält förutom neighborIds och header upptar 20 bytes
    @Override
    public int length() {
        return header.length() + 20 + neighborIds.length;
    }
}
