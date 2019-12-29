package network.old;

import network.IPHeader;

public abstract class OSPFPacket implements Packet {
    protected IPHeader ipHeader;
    protected OSPFHeader OSPFHeader;
}
