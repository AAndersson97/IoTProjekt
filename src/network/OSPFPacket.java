package network;

public abstract class OSPFPacket extends Packet {
    protected IPHeader ipHeader;
    protected OSPFHeader OSPFHeader;
    @Override
    public abstract byte[] toByteArray();

    @Override
    public abstract OSPFPacket copy();
}
