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
     * @param remote Mottagarens IP-address
     * @param packet Paketet som ska skickas
     * @throws IOException Om paketsändningen misslyckades kastas ett IOException
     */
    public static void sendPacket(short[] remote, Node sender, OLSRPacket packet) {
        if (remote == null || sender == null)
            throw new IllegalArgumentException("Source address neither remote address must not be null");
        wifiChannel.send(packet, remote, sender);
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
