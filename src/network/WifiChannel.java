package network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Klassen representerar ett trådlöst nätverkskanal där router på nätverket tar emot paket
 */
public class WifiChannel extends Channel {
    // En router som observerar i nätverkskanalen tar emot all data som skickas över kanalen
    private ArrayList<Router> observers;

    {
        observers = new ArrayList<>();
    }

    public void send(Packet packet, InetAddress address) {
        if (observers.isEmpty())
            throw new NullPointerException("There is no observers on this network");
        Simulator.scheduleTask(() -> observers.forEach(node -> {
                if (!address.equals(node.getAddress()))
                    node.receivePacket(packet);
        }));
    }

    public void addObserver(Router router) {
        observers.add(router);
    }

    public ArrayList<Router> getObservers() {
        return observers;
    }

}
