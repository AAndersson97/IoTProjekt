package network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static network.Constants.GUI.PACKET_TRANSPORT_DELAY;

public class PacketLocator {

    private static LocationListener locationListener;


    public synchronized static void reportPacketTransport(short[] startNode, short[] endNode) {
        ArrayList<Node> nodeList = Network.getNodeList();
        Location start = null, end = null;
        for (Node node : nodeList) {
            if (Arrays.equals(startNode, node.getAddress()))
                start = node.getLocation();
            else if (Arrays.equals(endNode, node.getAddress()))
                end = node.getLocation();
        }
        if (start != null && end != null) {
            Location finalStart = start, finalEnd = end;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    locationListener.reportedTransport(finalStart, finalEnd);
                }
            }, PACKET_TRANSPORT_DELAY);
        }
        else
            if (Constants.LOG_ACTIVE) {
                System.out.println("Start and/or end location is/are null");
            }

    }

    public static void registerListener(LocationListener listener) {
        locationListener = listener;
    }

    @FunctionalInterface
    public interface LocationListener {
        void reportedTransport(Location start, Location end);
    }
}
