package network;

import network.utilities.AddressGenerator;

public class SybilNode extends Node {

    private AttackNode master;

    public SybilNode(AttackNode node) {
        address = AddressGenerator.generateAddress();
        master = node;
    }

    @Override
    public MisbehaviourVoting.Vote requestVote() {
        return MisbehaviourVoting.Vote.AGREE;
    }

    @Override
    public void receivePacket(Packet packet) {
        master.receivePacket(packet);
    }

}
