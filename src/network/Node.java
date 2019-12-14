package network;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Comparator;

public class Node implements Comparator<Node>, Constants {
    private Byte[] address;
    private LocationCreator.Location location;
    private IPPacket currentPacket;

    public LocationCreator.Location getLocation() {
        return location;
    }

    public Node() {
        location = LocationCreator.getInstance().getLocation();
        //address = shortToByte(AddressGenerator.getInstance().generateAddress());
        NodeList.getInstance().addNode(this);
    }

    @Override
    public int compare(Node o1, Node o2) {
        return Math.abs((o1.location.getY() - o2.location.getX()) + (o1.location.getY() - o2.location.getY()));
    }

    public Byte[] getAddress() {
        return address;
    }

    private static byte[] shortToByte(short[] numbers) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(numbers.length);
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.put(numbers);
        return byteBuffer.array();
    }

    public String addressToString() {
        return address[0] + "." + address[1] + "." + address[2] + "." + address[3];
    }

    public void receivePacket(IPPacket packet) {

       // if (Arrays.compare(packet.getIpHeader().destinationAdress,address) == 0);
    }
}