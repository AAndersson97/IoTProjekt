package network;

import java.util.Arrays;
import java.util.Comparator;

public class Node implements Comparator<Node>, Constants {
    private Short[] address;
    private LocationCreator.Location location;

    public LocationCreator.Location getLocation() {
        return location;
    }

    public Node() {
        location = LocationCreator.getInstance().getLocation();
        address = AddressGenerator.getInstance().generateAddress();
        NodeList.getInstance().addNode(this);
    }

    @Override
    public int compare(Node o1, Node o2) {
        return Math.abs((o1.location.getY() - o2.location.getX()) + (o1.location.getY() - o2.location.getY()));
    }

    public Short[] getAddress() {
        return address;
    }

    public String addressToString() {
        return address[0] + "." + address[1] + "." + address[2] + "." + address[3];
    }
}