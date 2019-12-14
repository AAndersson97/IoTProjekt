package network;

import java.util.ArrayList;
import java.util.HashMap;

class NodeList {
    private static NodeList instance;
    private static HashMap<Short[],Node> nodeList;

    static {
        instance = new NodeList();
        nodeList = new HashMap<>();
    }

    NodeList() {
    }

    public static NodeList getInstance() {
        return instance;
    }

    public void addNode(Node node) {
        nodeList.put(node.getAddress(),node);
    }

    public HashMap<Short[],Node> getNodeList() {
        return new HashMap<>(nodeList);
    }

    public void removeNode(Short[] address) {
        if (address == null)
            throw new IllegalArgumentException();
        nodeList.remove(address);

    }


}