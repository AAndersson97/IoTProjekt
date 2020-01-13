package network;

import java.util.ArrayList;
import java.util.Arrays;

public class AttackNode extends Node {

    private SybilNode[] sybilNodes;

    public AttackNode(int numOfSybil) {
        sybilNodes = new SybilNode[0];
        sybilNodes = new SybilNode[Constants.Node.NUM_OF_SYBIL];
        createSybilNodes(numOfSybil);
        ArrayList<Node> blacklist = new ArrayList<>();
        blacklist.add(this);
        blacklist.addAll(Arrays.asList(sybilNodes));
        ArrayList<Location> attackLocations = LocationManager.getInstance().findLocationToAttack(blacklist);
        setLocation(attackLocations.get(0));
        for (int i = 0; i < sybilNodes.length; i++) {
            sybilNodes[i].setLocation(attackLocations.get(i+1));
            Network.registerNode(sybilNodes[i]);
        }
        Network.registerNode(this);
        for (SybilNode sybilNode : sybilNodes) {
            neighborSet.add(new NeighborTuple(sybilNode.getAddress(), NeighborTuple.N_status.SYM, Constants.Protocol.Willingness.WILL_DEFAULT));
            linkSet.add(new LinkTuple(getAddress(), sybilNode.getAddress(), Long.MAX_VALUE, Long.MAX_VALUE));
            twoHopNeighborSet.add(new TwoHopTuple(getAddress(), sybilNode.getAddress()));
            routingTable.add(new RoutingTuple(sybilNode.getAddress(), getAddress(), 2, getAddress()));
        }
        turnOn();
    }

    public void createSybilNodes(int num) {
        while (num-- > 0)
            sybilNodes[num] = new SybilNode(this);
    }

    @Override
    public void receivePacket(Packet packet) {
        dropPacket(packet);
    }

    public SybilNode[] getSybilNodes() {
        return sybilNodes;
    }
}
