package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

public class Router implements Comparator<Router>, Constants {
    private Communication communication;
    private ServerSocket server;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread thread;
    // Routerns adress är dess identifikation (Router Id)
    private InetAddress address;
    private LocationCreator.Location location;
    private boolean active;
    private int areaId;
    private ScheduledFuture pendingTask;
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
        communication = Communication.getInstance();
        areaId = Network.getArea(this);
        configureSocket();
        System.out.println(address.getHostAddress());
        //sendHelloPackets();
    }

    private void configureSocket() {
        try {
            //server = new ServerSocket(address);
            socket = server.accept();
            out = new PrintWriter(socket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        thread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    String readLine;
                    System.out.println((readLine = in.readLine()) == null ? "Null" : readLine);
                    Thread.sleep(3000);
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                    break;
                }
            }
        });
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
                Communication.addPacket(ipPacket);
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
        pendingTask.cancel(true);
    }

    public boolean isActive() {
        return pendingTask.isCancelled();
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
    }
}