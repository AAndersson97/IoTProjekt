package network;

import UI.SybilSimulator;

import java.util.*;
import java.util.function.Function;

public class PacketLocator {

    private static LocationListener locationListener;
    private static PacketDroppedListener packetDroppedListener;
    private static HashMap<Integer, ArrayList<PacketTravel>> packetStops; // paket som ej än anlänt till slutdestination ska finnas i listan för att göra det möjligt att matcha fördröjningar i gränssnitt korrekt
    private static Timer timer;

    static {
        timer = new Timer();
        packetStops = new HashMap<>();
    }
    public static void reportPacketDropped(Node node, PacketType type, int packetId) {
        int additionalDelay = 1;
        if (type == PacketType.TFTP && packetStops.containsKey(packetId))
            additionalDelay = packetStops.get(packetId).size() + 1;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                packetDroppedListener.packetDropped(node);
            }
        } , SybilSimulator.packetTransportDelay * additionalDelay);
    }

    public static void reportPacketTransport(PacketTravel travel) {
        if (travel.packetType == PacketType.HELLO && !SybilSimulator.showHelloPackets)
            return;
        if (travel.packetStatus == PacketStatus.RECEIVED) {
            if (packetStops.containsKey(travel.packet.PACKET_ID))
                reportPackets(travel.packet.PACKET_ID);
            else
                reportPacket(travel.start, travel.destination, travel.packet, SybilSimulator.packetTransportDelay);
        } else if (travel.packetStatus == PacketStatus.FORWARDED) {
            packetStops.computeIfAbsent(travel.packet.PACKET_ID, packet1 -> new ArrayList<>());
            packetStops.get(travel.packet.PACKET_ID).add(travel);
        }
    }

    private static void reportPackets(int key) {
        int hops = 0;
        for (PacketTravel travel : packetStops.get(key)) {
            reportPacket(travel.start, travel.destination, travel.packet, SybilSimulator.packetTransportDelay*(hops));
            hops++;
        }
        packetStops.remove(key);
    }

    public static void reportPacket(short[] startAddr, short[] endAddr, Packet packet, int delay) {
        ArrayList<Node> nodeList = Network.getNodeList();
        Node start = null, end = null;
        for (Node node : nodeList) {
            if (Arrays.equals(node.getAddress(), startAddr))
                start = node;
            else if (Arrays.equals(node.getAddress(), endAddr))
                end = node;
            if (start != null && end != null)
                break;
        }
        if (start != null && end != null) {
            Location finalStart = start.getLocation(), finalEnd = end.getLocation();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    locationListener.reportedTransport(finalStart, finalEnd, packet);
                }
            }, delay);
        } else if (Constants.LOG_ACTIVE) {
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

    enum PacketStatus {
        RECEIVED, FORWARDED, DROPPED
    }

    enum PacketType {
        HELLO, TC, TFTP
    }

    static class PacketTravel {
        final Packet packet;
        final PacketType packetType;
        final PacketStatus packetStatus;
        final short[] start; // startnod för paketet
        final short[] destination; // slutadress för paketet, nodens som skickar har enbart kunskap om det egna objektet och ej destinationsnodens objekt (instans av klassen Node)

        public PacketTravel(short[] start, short[] destination, Packet packet, PacketStatus status, PacketType type) {
            this.packet = packet;
            this.packetStatus = status;
            this.start = start;
            this.destination = destination;
            this.packetType = type;
        }
    }
}
