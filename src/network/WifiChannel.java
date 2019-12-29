package network;

import network.old.Packet;

import java.util.ArrayList;

/**
 * Klassen representerar ett trådlöst nätverkskanal där router på nätverket tar emot paket
 */
public class WifiChannel extends Channel {
    // En router som observerar i nätverkskanalen tar emot all data som skickas över kanalen
    private ArrayList<Node> observers;

    {
        observers = new ArrayList<>();
    }

    public void send(Packet packet, short[] address) {
        if (observers.isEmpty())
            throw new NullPointerException("There is no observers on this network");
        Simulator.scheduleTask(() -> observers.forEach(node -> {
                if (!address.equals(node.getAddress()))
                    node.receivePacket(packet);
        }));
    }

    public void addObserver(Node node) {
        observers.add(node);
    }

    public ArrayList<Node> getObservers() {
        return observers;
    }

}
