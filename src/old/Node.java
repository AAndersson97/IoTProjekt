package old;

import network.IPHeader;
import network.Location;
import network.LocationCreator;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;

import static network.Constants.GUI.*;

public class Node implements Comparator<Node>, Runnable {
    private Thread thread;
    // Routerns adress är dess identifikation (Router Id)
    private short[] address;
    private Location location;
    private boolean active;
    private final static ArrayDeque<Runnable> queue;
    private int areaId;
    // Sant om routern gränsar mot en eller flera områden
    private boolean isABR;
    private HashMap<short[], Node> routingTable;

    public Location getLocation() {
        return location;
    }

    static {
        queue = new ArrayDeque<>();
    }

    public Node() {
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
    public int compare(Node o1, Node o2) {
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

    }

    private void handleIPPacket(IPPacket packet) {
        IPHeader ipHeader = IPPacket.getIpHeader(packet);
        //if (Arrays.equals(ipHeader.destinationAdress, address))
        //    System.out.println("Packet arrived from " + Arrays.toString(ipHeader.sourceAdress));
    }

    private void forwardPacket(Packet packet) {

        System.out.println("Forward packet");
    }

    private void sendHelloPackets() {
    }

    public void setIsABR(boolean isABR) {
        this.isABR = isABR;
    }

    private static void reallocate(Node node) {
        int areaId = node.areaId;
        if (areaId == 1 || areaId == 2) {
            node.location.setX((WINDOW_WIDTH/3) - (CIRCLE_RADIUS + 5));
            if (areaId == 1)
                node.location.setY(WINDOW_HEIGHT - (CIRCLE_RADIUS * 6));
            else
                node.location.setY((WINDOW_HEIGHT - CIRCLE_RADIUS * 2) / 4);
        } else if (areaId == 3) {
            node.location.setX(WINDOW_WIDTH/2);
            node.location.setY((WINDOW_HEIGHT - CIRCLE_RADIUS * 2) / 2);
        } else if (areaId == 4 || areaId == 5) {
            node.location.setX((WINDOW_WIDTH)/3);
            if (areaId == 4)
                node.location.setY((WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2 - CIRCLE_RADIUS);
            else
                node.location.setY(WINDOW_HEIGHT - CIRCLE_RADIUS*6);
        }
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
        if (isABR)
            reallocate(this);
    }

    public void setAddress(short[] address) {
        this.address = address;
    }
}