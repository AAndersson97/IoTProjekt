package network;

public abstract class OSPFPacket implements Packet {
    protected IPHeader ipHeader;
    protected OSPFHeader OSPFHeader;
}
