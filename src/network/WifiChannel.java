package network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Klassen representerar ett trådlöst nätverkskanal där router på nätverket tar emot paket
 */
public class WifiChannel extends Channel {
    public ArrayList<Router> observers;

    public void send(Packet packet) throws IOException {
        Simulator.scheduleTask(() -> {
            observers.forEach(node -> {
                node.receivePacket(packet);});
        });
    }
}
