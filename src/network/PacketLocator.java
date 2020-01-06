package network;

public class PacketLocator {

    private static LocationListener locationListener;


    public synchronized static void reportPacketTransport(Location start, Location end) {
        locationListener.reportedTransport(start, end);
    }

    public static void registerListener(LocationListener listener) {
        locationListener = listener;
    }

    @FunctionalInterface
    public interface LocationListener {
        void reportedTransport(Location start, Location end);
    }
}
