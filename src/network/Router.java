package network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Router implements Comparator<Router>, Constants, Runnable {
    private Communication communication;
    private Thread thread;
    // Routerns adress är dess identifikation (Router Id)
    private InetAddress address;
    private LocationCreator.Location location;
    private boolean active;
    private final static ArrayDeque<Runnable> queue;
    private int areaId;
    // Sant om routern gränsar mot en eller flera områden
    private boolean isABR;
    private HashMap<Short[], Router> routingTable;

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
        communication = new Communication(address);
        areaId = Network.getArea(this);
        //System.out.println(address.getHostAddress());
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        Simulator.scheduleTaskPeriodically(new TimerTask() {
            @Override
            public void run() {
                sendHelloPackets();
            }
        }, 0, HELLO_INTERVAL);
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

    public InetAddress getAddress() {
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

        }
    }

    private void handleOSPFPacket(OSPFPacket packet) {
        // Om paketets AreaId ej överenstämmer med routerns areaid ska packetet ej bearbetas
        if (packet.OSPFHeader.getAreaID() == areaId){
            /*switch (packet.OSPFHeader.getType()){
                case OSPFPacketType.Hello.getValue():
                    break;

            }*/
            System.out.println("Package arrived");
        }
    }

    private void forwardPacket(Packet packet) {

        System.out.println("Forward packet");
    }

    private void sendHelloPackets() {

        HelloPacket helloPacket = new HelloPacket();
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

    public void setAddress(byte[] address) {
        try {
            this.address = InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            System.out.println("Host unknown or wrong format of the host address");
        }
        Address.generated = this.address;
    }


}