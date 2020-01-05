package network;

import java.io.IOException;
import java.util.ArrayList;

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
     * @throws IOException Om paketsändningen misslyckades kastas ett IOException
     */
    public static void sendPacket(Node sender, OLSRPacket packet) {
        if (sender == null || packet == null)
            throw new IllegalArgumentException("Source address neither packet must be null");
        wifiChannel.send(sender, packet);
    }

    public static void shutdownNetwork() {
        for (Node node : wifiChannel.getObservers()) {
            node.turnOff();
        }
    }

    public static void registerNode(Node node) {
        wifiChannel.addObserver(node);
        numOfNodes++;
    }

    public static ArrayList<Node> getNodeList() {
        return wifiChannel.getObservers();
    }

}
