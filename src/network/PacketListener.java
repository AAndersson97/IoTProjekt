package network;
@FunctionalInterface
public interface PacketListener {
    void packetAdded(Packet packet);

}
