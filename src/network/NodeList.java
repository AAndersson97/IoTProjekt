import java.util.ArrayList;

class NodeList {
    private static NodeList instace;
    private static ArrayList<Node> nodeList;

    static {
        instace = new NodeList();
        nodeList = new ArrayList();
    }

    NodeList() {
    }

    public static NodeList getInstance() {
        return instace;
    }

    public void addNode(Node node) {
        nodeList.add(node);
    }

    public void removeNode(int index) {

    }


}