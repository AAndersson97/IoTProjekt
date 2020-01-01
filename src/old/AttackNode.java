package old;

import network.Node;
import network.SybilNode;

public class AttackNode extends Node {

    AttackNode(int numOfSybil) {
        createSybilNodes(numOfSybil);
    }

    public void createSybilNodes(int num) {
        while (num-- > 0)
            new SybilNode(this);
    }
}
