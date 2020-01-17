package network;

public class SybilNode extends Node {

    private AttackNode master;

    public SybilNode(AttackNode node) {
        address = AddressGenerator.generateAddress();
        master = node;
    }

    @Override
    public void receivePacket(Packet packet) {
        master.receivePacket(packet);
    }

}
