package network;

public class OSPFPacket extends Packet {
    private short version;
    private OSPFPacketType type;
    private int length;

    OSPFPacket() {
    }

    public int length() {
        return 0;
    }

}
