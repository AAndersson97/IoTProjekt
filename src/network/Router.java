package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketImpl;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Router implements Comparator<Router>, Constants, Runnable {
    private Communication communication;
    private Thread thread;
    // Routerns adress är dess identifikation (Router Id)
    private InetAddress address;
    private LocationCreator.Location location;
    private boolean active;
    private int areaId;
    // Sant om routern gränsar mot en eller flera områden
    private boolean isABR;
    private HashMap<Short[], Router> routingTable;

    public LocationCreator.Location getLocation() {
        return location;
    }

    public Router() {
        location = LocationCreator.getInstance().getLocation();
        routingTable = new HashMap<>();
        active = true;
        communication = new Communication(address);
        areaId = Network.getArea(this);
        System.out.println(address.getHostAddress());
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

    }

    @Override
    public int compare(Router o1, Router o2) {
        return Math.abs((o1.location.getY() - o2.location.getX()) + (o1.location.getY() - o2.location.getY()));
    }

    public InetAddress getAddress() {
        return address;
    }

    public void receivePacket(Packet packet, InetAddress destination) {
        if (packet instanceof OSPFPacket) {
        } else if (packet instanceof IPPacket){
            IPPacket ipPacket = (IPPacket) packet;
            if (destination.equals(address)) {
                byte[] dest= ipPacket.getIpHeader().destinationAdress;
                //InetAddress.getByName(ipPacket.getIpHeader().destinationAdress);
                if (Arrays.compare(dest, address.getAddress()) == 0) {
                    System.out.println("Package has reached its final destination");
                } else
                    forwardPacket(packet);
            } else if (Arrays.compare(destination.getAddress(),MULTI_CAST) == 0) {

            }
        }
        // packet.addTravelNode(address);

    }

    private void forwardPacket(Packet packet) {

        System.out.println("Forward packet");
    }

    private void sendHelloPackets() {
        Runnable runnable = () -> {
        };
    }

    public void setIsABR(boolean isABR) {
        this.isABR = isABR;
    }

    public void turnOff() {
        thread.interrupt();
    }

    public boolean isActive() {
        return thread.isAlive();
    }

    public void assignAreaId(int areaId) {
        this.areaId = areaId;
    }

    public void setAddress(byte[] address) {
        try {
            this.address = InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            System.out.println("Host unknown or wrong format of the host address");
        }
        Address.generated = this.address;
    }


}