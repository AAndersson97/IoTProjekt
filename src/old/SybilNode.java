package old;

import network.AttackNode;
import network.Node;
import network.Packet;

public class SybilNode extends Node {

    private AttackNode master;

    public SybilNode(AttackNode node) {
        super();
        master = node;
    }

    @Override
    public void receivePacket(Packet packet) {
        master.receivePacket(packet);
    }


}
