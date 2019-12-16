package network;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Node implements Comparator<Node>, Constants {
    private Communication communication;
    private short[] address;
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
        Network.getInstance().addNode(this);
        active = true;
        communication = Communication.getInstance();
    }

    @Override
    public int compare(Node o1, Node o2) {
        return Math.abs((o1.location.getY() - o2.location.getX()) + (o1.location.getY() - o2.location.getY()));
    }

    public short[] getAddress() {
        return address;
    }

    private static byte[] shortToByte(short[] numbers) {
        byte[] bytes = new byte[numbers.length];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte)numbers[i];

        return bytes;
    }

    public String addressToString() {
        return address[0] + "." + address[1] + "." + address[2] + "." + address[3];
    }

    public void receivePacket(Packet packet) {
        if (packet instanceof OSPFPacket) {

        } else if (packet instanceof IPPacket){
            IPPacket ipPacket = (IPPacket) packet;
            byte[] byteAddress = shortToByte(address);
            byte[] destination = ipPacket.getIpHeader().destinationAdress;
            if (Arrays.compare(destination, byteAddress) == 0) {
                System.out.println("Package has reached its final destination");
                Communication.addPacket(ipPacket);
            } else
                forwardPacket(packet);
        }
        packet.addTravelNode(address);

    }

    private void forwardPacket(Packet packet) {

    }

    private void sendHelloPackets() {
        Thread thread = new Thread(() -> {
            try {
                //communication.sendMessage("hello", address,new short[]{140,1,1,0});
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void turnOff() {
        active = false;
        Network.getInstance().removeNode(address);
    }

    public boolean isActive() {
        return active;
    }
}