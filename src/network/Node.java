package network;

import network.old.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Node implements Comparator<Node>, Runnable {
    private Thread thread;
    // Routerns adress är dess identifikation (Router Id)
    private short[] address;
    private LocationCreator.Location location;
    private boolean active;
    private final ConcurrentLinkedQueue<Packet> buffer;
    private HashMap<short[], Node> routingTable;

    public LocationCreator.Location getLocation() {
        return location;
    }

    public Node() {
        buffer = new ConcurrentLinkedQueue<>();
        location = LocationCreator.getInstance().getLocation();
        routingTable = new HashMap<>();
        active = true;
        thread = new Thread(this);
        thread.start();
        Network.registerNode(this);
    }

    @Override
    public void run() {
        /*Simulator.scheduleTaskPeriodically(new TimerTask() {
            @Override
            public void run() {
                sendHelloPackets();
            }
        }, 0, HELLO_INTERVAL);*/
        while (active) {
                while (buffer.isEmpty() && active) {
                    try {
                        synchronized (buffer) {
                            buffer.wait();
                        }
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
                if (!active) {
                    break;
                } else {
                    handlePacket(buffer.poll());
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
        synchronized (buffer) {
            buffer.add(packet);
            buffer.notifyAll();
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
        if (Arrays.equals(ipHeader.destinationAdress, address))
            System.out.println("Packet arrived from " + Arrays.toString(ipHeader.sourceAdress));
    }

    private void forwardPacket(Packet packet) {

        System.out.println("Forward packet");
    }

    private void sendHelloPackets() {
    }

    public void turnOff() {
        active = false;
        synchronized (buffer) {
            buffer.notifyAll();
        }
    }

    public boolean isActive() {
        return thread.isAlive();
    }
    public void setAddress(short[] address) {
        this.address = address;
    }
}