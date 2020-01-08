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
        turnOn();
    }

    public void createSybilNodes(int num) {
        while (num-- > 0)
            sybilNodes[num] = new SybilNode(this);
    }

    public SybilNode[] getSybilNodes() {
        return sybilNodes;
    }
}
