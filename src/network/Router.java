package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Router extends Thread implements Comparator<Router>, Constants {
    private Communication communication;
    private Socket OSPFSocket;
    private Socket FTPSocket;
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
        OSPFSocket = new Socket(address, OSPF_PORT);
        FTPSocket = new Socket(address, FTP_PORT);
        OSPFSocket.open();
        FTPSocket.open();
        start();
        
    }

    @Override
    public void run() {
        byte[] data = null;
        while (true) {
            try {
                if (OSPFSocket.bytesToRead() > 0)
                    data = OSPFSocket.read();
                else if (FTPSocket.bytesToRead() > 0)
                    data = FTPSocket.read();
                System.out.println(data == null ? "No data" : "Length: " + data.length);
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int compare(Router o1, Router o2) {
        return Math.abs((o1.location.getY() - o2.location.getX()) + (o1.location.getY() - o2.location.getY()));
    }

    public InetAddress getAddress() {
        return address;
    }

    public void receivePacket(Packet packet) {
        if (packet instanceof OSPFPacket) {
        } else if (packet instanceof IPPacket){
            IPPacket ipPacket = (IPPacket) packet;
            byte[] destination = ipPacket.getIpHeader().destinationAdress;
            //InetAddress.getByName(ipPacket.getIpHeader().destinationAdress);
            if (Arrays.compare(destination, address.getAddress()) == 0) {
                System.out.println("Package has reached its final destination");
            } else
                forwardPacket(packet);
        }
        packet.addTravelNode(address);

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

    }

    public boolean isActive() {
        return true;
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