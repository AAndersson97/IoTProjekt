package network;

import java.util.Comparator;

public class Node implements Comparator<Node> {
    private String address;
    private LocationCreator.Location location;
    public void createNode() {


    }

    Node() {
        location = LocationCreator.getInstance().createLocation();
    }

    @Override
    public int compare(Node o1, Node o2) {
        return Math.abs((o1.location.getY() - o2.location.getX()) + (o1.location.getY() - o2.location.getY()));
    }
}