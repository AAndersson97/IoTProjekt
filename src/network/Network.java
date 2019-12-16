package network;

import java.util.HashMap;

public class Network {
    private static Network instance;
    private static HashMap<Short[],Node> nodeList;

    static {
        instance = new Network();
        nodeList = new HashMap<>();
    }

    Network() {
    }

    public static Network getInstance() {
        return instance;
    }

    public void addNode(Node node) {
        nodeList.put(node.getAddress(),node);
    }

    public HashMap<Short[],Node> getNodeList() {
        return new HashMap<>(nodeList);
    }

    public Node getNode(short[] address) {
        return nodeList.get(address);
    }

    public void removeNode(Short[] address) {
        if (address == null)
            throw new IllegalArgumentException();
        nodeList.remove(address);
    }

    public int numOfNodes() {
        return nodeList.size();
    }


}