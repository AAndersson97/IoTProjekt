package network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import static network.Constants.Network.PACKET_LOSS;

/**
 * Klassen representerar en trådlöst nätverkskanal där router på nätverket tar emot paket
 */
public class WifiChannel extends Channel {
    // En router som observerar i nätverkskanalen tar emot all data som skickas över kanalen
    private HashMap<short[],Node> observers;

    {
        observers = new HashMap<>();
    }

    public void send(Node sender, OLSRPacket packet) {
        if (observers.isEmpty())
            throw new NullPointerException("There is no observers on this network");
        Simulator.scheduleTask(() -> observers.forEach((address,node) -> {
                if (simulateLoss());
                else if (!Arrays.equals(sender.getAddress(), node.getAddress()) && isWithinTransmissionArea(sender, node))
                    node.receivePacket(packet);
        }));
    }

    public boolean isWithinTransmissionArea(Node sender, Node receiver) {
        return Transmission.isInsideTransmissionArea(sender.getTransmissionRadius(), sender.getLocation(), receiver.getLocation());
    }

    private boolean simulateLoss() {
        return (new Random().nextInt(100/PACKET_LOSS)==0);
    }

    public void addObserver(Node node) {
        observers.put(node.getAddress(),node);
    }

    public HashMap<short[], Node> getObservers() {
        return observers;
    }

}
