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
        packetDroppedListener.packetDropped(node);
    }

    public synchronized static void reportPacketTransport(short[] startNode, short[] endNode) {
        HashMap<short[],Node> nodeList = Network.getNodeList();
        Location start = nodeList.get(startNode).getLocation(), end = nodeList.get(endNode).getLocation();
        if (start != null && end != null) {
            Location finalStart = start, finalEnd = end;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    locationListener.reportedTransport(finalStart, finalEnd);
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
        void reportedTransport(Location start, Location end);
    }

    @FunctionalInterface
    public interface PacketDroppedListener {
        void packetDropped(Node node);
    }

}
