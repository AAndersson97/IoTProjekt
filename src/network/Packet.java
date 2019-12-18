package network;

import java.net.InetAddress;
import java.util.ArrayList;

public abstract class Packet {
    private ArrayList<InetAddress> travelNodes;

    Packet() {
        travelNodes = new ArrayList<>();
    }
    public void addTravelNode(InetAddress address) {
        travelNodes.add(address);
    }

    public abstract int length();
}
