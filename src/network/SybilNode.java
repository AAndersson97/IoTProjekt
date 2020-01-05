package network;

public class SybilNode extends Node {

    private AttackNode master;

    public SybilNode(AttackNode node) {
        super();
        master = node;
    }


}
