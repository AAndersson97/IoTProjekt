package network;

import network.utilities.AddressGenerator;

import java.util.*;

public class Network {
    private static int numOfNodes;
    private static WifiChannel wifiChannel;

    static {
        wifiChannel = new WifiChannel();
    }
    private Network() {
    }

    public static int getNumOfNodes() {
        return numOfNodes;
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
        for (Node node : getNodeList()) {
            node.disconnect();
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

    public static void removeNode(Node node) {
        wifiChannel.removeObserver(node);
        numOfNodes--;
        AddressGenerator.returnAddress(node.getAddress());
    }

    public static short[] removeAttackNode() {
        AttackNode node = null;
        for (Node n : getNodeList()) {
            if (n instanceof AttackNode)
                node = (AttackNode) n;
        }
        if (node != null) {
            node.disconnect();
            wifiChannel.removeObserver(node);
            return node.getAddress();
        }
        return null;
    }

    public static ArrayList<Node> getNodeList() {
        return wifiChannel.getObservers();
    }

    public static Node getRandomNode(Node sender) {
        ArrayList<Node> observers = getNodeList();
        observers.remove(sender);
        return observers.get(new Random().nextInt(observers.size()));
    }

    @FunctionalInterface
    public interface NodeDisconnetListener {
        void nodeDisconnected(Node node);
    }
}
