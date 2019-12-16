package network;

import java.util.ArrayList;

public abstract class Packet {
    private ArrayList<short[]> travelNodes;

    Packet() {
        travelNodes = new ArrayList<>();
    }
    public void addTravelNode(short[] address) {
        travelNodes.add(address);
    }

    public abstract int length();
}
