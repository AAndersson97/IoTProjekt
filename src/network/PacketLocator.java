package network;

import UI.SybilSimulator;

import java.util.*;

public class PacketLocator {

    private static LocationListener locationListener;
    private static PacketDroppedListener packetDroppedListener;
    private static Timer timer;

    static {
        timer = new Timer();
    }
    public synchronized static void reportPacketDropped(Node node) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                packetDroppedListener.packetDropped(node);
            }
        } , SybilSimulator.packetTransportDelay);
    }

    public synchronized static void reportPacketTransport(short[] startNode, short[] endNode, Packet packet) {
        ArrayList<Node> nodeList = Network.getNodeList();
        Node start = null, end = null;
        for (Node node : nodeList) {
            if (Arrays.equals(node.getAddress(), startNode))
                start = node;
            else if (Arrays.equals(node.getAddress(), endNode))
                end = node;
        }
        if (start != null && end != null) {
            Location finalStart = start.getLocation(), finalEnd = end.getLocation();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    locationListener.reportedTransport(finalStart, finalEnd, packet);
                }
            }, SybilSimulator.packetTransportDelay);
        }
        else
            if (Constants.LOG_ACTIVE) {
                System.out.println("Start and/or end location is/are null");
            }

    }

    public static void registerLocationListener(LocationListener listener) {
        locationListener = listener;
    }

    public static void registerPacketDroppedListener(PacketDroppedListener listener) {
        packetDroppedListener = listener;
    }

    public static void shutDown() {
        timer.cancel();
    }

    @FunctionalInterface
    public interface LocationListener {
        void reportedTransport(Location start, Location end, Packet packet);
    }

    @FunctionalInterface
    public interface PacketDroppedListener {
        void packetDropped(Node node);
    }

}
