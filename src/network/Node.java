package network;

import java.util.Comparator;

public class Node implements Comparator<Node> {
    private Short[] address;
    private LocationCreator.Location location;

    public LocationCreator.Location getLocation() {
        return location;
    }

    public void createNode() {


    }

    public Node() {
        location = LocationCreator.getInstance().createLocation();
        address = AddressGenerator.getInstance().generateAddress();
    }

    @Override
    public int compare(Node o1, Node o2) {
        return Math.abs((o1.location.getY() - o2.location.getX()) + (o1.location.getY() - o2.location.getY()));
    }

    public Short[] getAddress() {
        return address;
    }
}