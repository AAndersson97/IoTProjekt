package network;

import network.old.Area;
import network.old.Packet;

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
     * @param source Avsändarens IP-address
     * @param remote Mottagarens IP-address
     * @param packet Paketet som ska skickas
     * @throws IOException Om paketsändningen misslyckades kastas ett IOException
     */
    public static void sendPacket(short[] remote, short[] source, Packet packet) {
        if (remote == null || source == null)
            throw new IllegalArgumentException("Source address neither remote address must not be null");
        wifiChannel.send(packet, source);
    }

    public static synchronized void sendMulticast(short[] source, Packet packet) {
        if (source == null)
            throw new IllegalArgumentException("Source address must not be null");
        wifiChannel.send(packet, source);
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
