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

    }


}