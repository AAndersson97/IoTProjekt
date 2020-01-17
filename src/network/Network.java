package network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Network {
    private static int numOfNodes;
    private static WifiChannel wifiChannel;
    private static NodeDisconnetListener listener;

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

    public static void registerNodeDisconnectListener(NodeDisconnetListener l) {
        listener = l;
    }

    /**
     *
     * @param sender Avsändaren
     * @param packet Paketet som ska skickas
     */
    public static void sendPacket(Node sender, short[] receiver,Packet packet) {
        if (sender == null || packet == null)
            throw new IllegalArgumentException("Source address neither packet must be null");
        packet.setWifiMacHeader(new WifiMacHeader(sender.getAddress(), receiver));
        WifiMacTrailer trailer = new WifiMacTrailer();
        packet.setWifiMacTrailer(trailer);
        trailer.setCheckSum(packet.toBytes());
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

    public static Node getNode(short[] addr) {
        for (Node node : getNodeList())
            if (Arrays.equals(node.getAddress(), addr))
                return node;

         return null;
    }

    public static void unregisterNode(Node node) {
        wifiChannel.removeObserver(node);
        numOfNodes--;
        if (listener != null)
            listener.nodeDisconnected(node);
    }

    public static short[] removeAttackNode() {
        AttackNode node = null;
        for (Node n : wifiChannel.getObservers()) {
            if (n instanceof AttackNode)
                node = (AttackNode) n;
        }
        if (node != null) {
            node.turnOff();
            wifiChannel.removeObserver(node);
            return node.getAddress();
        }
        return null;
    }

    public static ArrayList<Node> getNodeList() {
        return wifiChannel.getObservers();
    }

    @FunctionalInterface
    public interface NodeDisconnetListener {
        void nodeDisconnected(Node node);
    }
}
