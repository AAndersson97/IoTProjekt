package network;

public abstract class OSPFPacket extends Packet {
    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
