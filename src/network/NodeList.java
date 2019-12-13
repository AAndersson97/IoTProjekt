class NodeList {
    private static NodeList instace;
    private ArrayList<Node> nodeList;

    static {
        instance = new NodeList();
        nodeList = new ArrayList();
    }
    NodeList() {

    }

    public static getInstance() {
        return instance;
    }

    public void addNode(Node node) {
        nodeList.add(node);
    }

    public void removeNode(int index) {

    }


}