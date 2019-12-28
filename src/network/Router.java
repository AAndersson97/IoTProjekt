package network;

import java.io.IOException;
import java.util.*;
import static network.Constants.Node.*;

public class Router implements Comparator<Router>, Runnable {
    private Thread thread;
    // Routerns adress är dess identifikation (Router Id)
    private short[] address;
    private LocationCreator.Location location;
    private boolean active;
    private final static ArrayDeque<Runnable> queue;
    private int areaId;
    // Sant om routern gränsar mot en eller flera områden
    private boolean isABR;
    private HashMap<short[], Router> routingTable;

    public LocationCreator.Location getLocation() {
        return location;
    }

    static {
        queue = new ArrayDeque<>();
    }

    public Router() {
        location = LocationCreator.getInstance().getLocation();
        routingTable = new HashMap<>();
        active = true;
        try {
            areaId = Network.getArea(this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        /*Simulator.scheduleTaskPeriodically(new TimerTask() {
            @Override
            public void run() {
                sendHelloPackets();
            }
        }, 0, HELLO_INTERVAL);*/
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty() && active) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
                if (!active) {
                    break;
                }
                else
                    queue.removeFirst().run();
            }
        }
    }

    @Override
    public int compare(Router o1, Router o2) {
        return Math.abs((o1.location.getY() - o2.location.getX()) + (o1.location.getY() - o2.location.getY()));
    }

    public short[] getAddress() {
        return address;
    }

    public void receivePacket(Packet packet) {
        synchronized (queue) {
            queue.addLast(() -> handlePacket(packet));
            queue.notifyAll();
        }
    }

    private void handlePacket(Packet packet) {
        if (packet instanceof OSPFPacket) {
            handleOSPFPacket((OSPFPacket) packet);
        } else if (packet instanceof IPPacket){
            handleIPPacket((IPPacket) packet);
        }
    }

    private void handleOSPFPacket(OSPFPacket packet) {
        // Om paketets AreaId ej överenstämmer med routerns areaid ska packetet ej bearbetas
        if (packet.OSPFHeader.getAreaID() == areaId){
            System.out.println("Packet arrived");
        }
    }

    private void handleIPPacket(IPPacket packet) {
        IPHeader ipHeader = IPPacket.getIpHeader(packet);
        if (Arrays.equals(ipHeader.destinationAdress, address))
            System.out.println("Packet arrived from " + Arrays.toString(ipHeader.sourceAdress));
    }

    private void forwardPacket(Packet packet) {

        System.out.println("Forward packet");
    }

    private void sendHelloPackets() {
        short[][] neigbors = new short[0][0];
        OSPFHeader header = null;
        IPHeader ipHeader = null;
        try {
            header = new OSPFHeader(OSPFPacketType.Hello, 0, areaId, address);
            ipHeader = new IPHeader(0, address, MULTI_CAST, OSPF_PROTOCOL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HelloPacket helloPacket = new HelloPacket(ipHeader, header, neigbors, 0);
        Network.sendMulticast(address, helloPacket);
    }

    public void setIsABR(boolean isABR) {
        this.isABR = isABR;
    }

    public void turnOff() {
        active = false;
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    public boolean isActive() {
        return thread.isAlive();
    }

    public int getAreaId() {
        return areaId;
    }

    public void assignAreaId(int areaId) {
        this.areaId = areaId;
    }

    public void setAddress(short[] address) {
        this.address = address;
    }
}