package network;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Comparator;

public class Node implements Comparator<Node>, Constants {
    private Short[] address;
    private LocationCreator.Location location;
    private IPPacket currentPacket;

    public LocationCreator.Location getLocation() {
        return location;
    }

    public Node() {
        location = LocationCreator.getInstance().getLocation();
        address = AddressGenerator.getInstance().generateAddress();
        NodeList.getInstance().addNode(this);
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

    private void forwardPacket(IPPacket packet) {

    }
}