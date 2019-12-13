import java.util.ArrayList;

class NodeList {
    private static NodeList instance;
    private static ArrayList<Node> nodeList;

    static {
        instance = new NodeList();
        nodeList = new ArrayList();
    }

    NodeList() {
    }

    public static NodeList getInstance() {
        return instance;
    }

    public void addNode(Node node) {
        nodeList.add(node);
    }

    public void removeNode(int index) {
        assert nodeList.isEmpty(): "List is empty, node was not removed";
        if (index < 0 || index >= nodeList.size())
            throw new IllegalArgumentException("Index out of bounds. Min index: 0, max index: " + (nodeList.size()-1));
    }


}