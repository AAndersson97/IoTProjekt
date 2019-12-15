package network;


import net.sourceforge.sizeof.SizeOf;

public class OSPFPacket extends Packet {
    private short version;
    private OSPFPacketType type;
    private int length;

    OSPFPacket() {
        SizeOf.sizeOf(this);
    }

    public int length() {
        return 0;
    }

}
