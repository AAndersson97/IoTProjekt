package network;
@FunctionalInterface
public interface PacketListener {
    void packetAdded(IPPacket packet);

}
