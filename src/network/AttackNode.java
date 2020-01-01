package network;

public class AttackNode extends Node {

    private SybilNode[] sybilNodes;

    public AttackNode(int numOfSybil) {
        sybilNodes = new SybilNode[Constants.Node.NUM_OF_SYBIL];
        createSybilNodes(numOfSybil);
    }

    public void createSybilNodes(int num) {
        while (num-- > 0)
            sybilNodes[num] = new SybilNode(this);
    }

    public SybilNode[] getSybilNodes() {
        return sybilNodes;
    }
}
