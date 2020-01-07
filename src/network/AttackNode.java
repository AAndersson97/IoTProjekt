package network;

import java.util.ArrayList;

public class AttackNode extends Node {

    private SybilNode[] sybilNodes;

    public AttackNode(int numOfSybil, short[] nodToAttack) {
        sybilNodes = new SybilNode[Constants.Node.NUM_OF_SYBIL];
        createSybilNodes(numOfSybil);
        ArrayList<Location> attackLocations = LocationCreator.getInstance().getLocationWithinRange(nodToAttack);
        setLocation(attackLocations.get(0));
    }

    public void createSybilNodes(int num) {
        while (num-- > 0)
            sybilNodes[num] = new SybilNode(this);
    }

    public SybilNode[] getSybilNodes() {
        return sybilNodes;
    }
}
