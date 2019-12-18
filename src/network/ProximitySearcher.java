package network;

import java.util.ArrayList;

public class ProximitySearcher {
    private static ProximitySearcher instance;

    static {
        instance = new ProximitySearcher();
    }

    ProximitySearcher() {
    }
    public static ProximitySearcher getInstance() {
        return instance;
    }

    public ArrayList<Router> findClosestNeighbours(Router n) {
        ArrayList<Router>neighbours = new ArrayList<>();
        int nx = n.getLocation().getX();
        int ny = n.getLocation().getY();
        /*for(Node node : Area.getNodeList().values()){
            int nox = node.getLocation().getX();
            int noy = node.getLocation().getY();
            if(nox != nx && noy!=ny){
                if(Math.abs((nox-nx))<=100  && Math.abs(noy-ny)<=100){
                    neighbours.add(node);
                }
            }

        }*/
        //ArrayList<Node> nodes = (ArrayList<Node>) NodeList.getInstance().getNodeList().values();
        //Collections.sort(nodes);
        //nodes.sort((n1,n2)-> Math.abs((n1.getLocation().getY() - n2.getLocation().getX()) + (n1.getLocation().getY() - n2.getLocation().getY())));
        return neighbours;
    }
}
