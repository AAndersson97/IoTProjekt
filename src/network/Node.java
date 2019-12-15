package network;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Node implements Comparator<Node>, Constants {
    private Short[] address;
    private LocationCreator.Location location;
    private boolean active;
    private HashMap<Short[], Node> routingTable;

    public LocationCreator.Location getLocation() {
        return location;
    }

    public Node() {
        location = LocationCreator.getInstance().getLocation();
        address = AddressGenerator.getInstance().generateAddress();
        routingTable = new HashMap<>();
        NodeList.getInstance().addNode(this);
        active = true;

    }

    @Override
    public int compare(Node o1, Node o2) {
        return Math.abs((o1.location.getY() - o2.location.getX()) + (o1.location.getY() - o2.location.getY()));
    }

    public Short[] getAddress() {
        return address;
    }

    private static byte[] shortToByte(Short[] numbers) {
        byte[] bytes = new byte[numbers.length];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = numbers[i].byteValue();

        return bytes;
    }

    public String addressToString() {
        return address[0] + "." + address[1] + "." + address[2] + "." + address[3];
    }

    public void receivePacket(IPPacket packet) {
        byte[] byteAddress = shortToByte(address);
        byte[] destination = packet.getIpHeader().destinationAdress;
        if (Arrays.compare(destination,byteAddress) == 0) {
            System.out.println("Package has reached its final destination");
            Communication.addPacket(packet);
        } else
            forwardPacket(packet);
        packet.addTravelNode(address);

    }

    private void forwardPacket(Packet packet) {

    }

    public void turnOff() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }
}