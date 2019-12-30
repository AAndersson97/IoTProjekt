package network.old;

import network.Channel;
import network.Simulator;

import java.util.ArrayList;
import java.util.Random;

import static network.Constants.Network.PACKET_LOSS;

/**
 * Klassen representerar ett trådlöst nätverkskanal där router på nätverket tar emot paket
 */
public class WifiChannel extends Channel {
    // En router som observerar i nätverkskanalen tar emot all data som skickas över kanalen
    private ArrayList<Node> observers;
    private double packetLoss; // I procent

    {
        observers = new ArrayList<>();
    }

    public void send(Packet packet, short[] address) {
        if (observers.isEmpty())
            throw new NullPointerException("There is no observers on this network");
        Simulator.scheduleTask(() -> observers.forEach(node -> {
                if (simulateLoss());
                else if (!address.equals(node.getAddress()))
                    node.receivePacket(packet);
        }));
    }

    private boolean simulateLoss() {
        return (new Random().nextInt(100/PACKET_LOSS)==0);
    }

    public void addObserver(network.old.Node node) {
        observers.add(node);
    }

    public ArrayList<Node> getObservers() {
        return observers;
    }

}
