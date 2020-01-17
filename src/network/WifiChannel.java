package network;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static network.Constants.Network.PACKET_LOSS;

/**
 * Klassen representerar en trådlöst nätverkskanal där router på nätverket tar emot paket
 */
public class WifiChannel extends Channel {
    // En router som observerar i nätverkskanalen tar emot all data som skickas över kanalen
    private final List<Node> observers;
    private int numOfTcPackets;

    {
        observers = new ArrayList<>();
    }

    public synchronized void send(Node sender, Packet packet) {
        if (observers.isEmpty())
            throw new NullPointerException("There is no observers on this network");
        Simulator.scheduleTask(() -> {
            synchronized (observers) {
                for (Node node : observers) {
                    if (!Arrays.equals(sender.getAddress(), node.getAddress()) && isWithinTransmissionArea(sender, node)) {
                        if (!simulateLoss()) {
                            node.receivePacket(packet.copy());
                        }
                    }
                }
            }
        });
    }

    public boolean isWithinTransmissionArea(Node sender, Node receiver) {
        return Transmission.isInsideTransmissionArea(sender.getTransmissionRadius(), sender.getLocation(), receiver.getLocation());
    }

    private boolean simulateLoss() {
        return (new Random().nextInt(100/PACKET_LOSS)==0);
    }

    public void addObserver(Node node) {
        synchronized (observers) {
            observers.add(node);
        }

    }

    public ArrayList<Node> getObservers() {
        synchronized (observers) {
            return new ArrayList<>(observers);
        }
    }

    public void removeObserver(Node node) {
        synchronized (observers) {
            observers.remove(node);
        }
    }
}
