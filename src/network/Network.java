package network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Network {
    private static int numOfNodes;
    private static WifiChannel wifiChannel;

    static {
        wifiChannel = new WifiChannel();
    }
    private Network() {
    }

    public static void newNodeAdded() {
        numOfNodes++;
    }

    public static int getNumOfNodes() {
        return numOfNodes;
    }

    /**
     *
     * @param sender Avsändaren
     * @param packet Paketet som ska skickas
     */
    public static void sendPacket(Node sender, OLSRPacket packet) {
        if (sender == null || packet == null)
            throw new IllegalArgumentException("Source address neither packet must be null");
        wifiChannel.send(sender, packet);
    }

    public static void shutdownNetwork() {
        for (Node node : wifiChannel.getObservers().values()) {
            node.turnOff();
        }
    }

    public static void registerNode(Node node) {
        wifiChannel.addObserver(node);
        numOfNodes++;
    }

    public static HashMap<short[], Node> getNodeList() {
        return wifiChannel.getObservers();
    }

}
