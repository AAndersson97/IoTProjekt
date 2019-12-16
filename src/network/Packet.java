package network;

import java.util.ArrayList;

public abstract class Packet {
    private ArrayList<Short[]> travelNodes;

    Packet() {
        travelNodes = new ArrayList<>();
    }
    public void addTravelNode(Short[] address) {
        travelNodes.add(address);
    }

    public abstract int length();
}
