package network;

import UI.SybilSimulator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PacketLocator {

    private static LocationListener locationListener;
    private static PacketDroppedListener packetDroppedListener;
    private static Timer timer;

    static {
        timer = new Timer();
    }
    public synchronized static void reportPacketDropped(Node node) {
        packetDroppedListener.packetDropped(node);
    }

    public synchronized static void reportPacketTransport(short[] startNode, short[] endNode, Packet packet) {
        ConcurrentHashMap<short[],Node> nodeList = Network.getNodeList();
        System.out.println("Contains start: " + nodeList.get(startNode) + " contains end: " + nodeList.contains(endNode));
        nodeList.forEach((key,value) -> {if (Arrays.equals(key, startNode) || Arrays.equals(key, endNode))
            System.out.println("Match found: " + Arrays.toString(key));});
        /*Location start = nodeList.get(startNode).getLocation(), end = nodeList.get(endNode).getLocation();
        if (start != null && end != null) {
            Location finalStart = start, finalEnd = end;
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
            */
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
