package network;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Node implements Comparator<Node>, Runnable {
    private Thread thread;
    // Routerns adress är dess identifikation (Router Id)
    private short[] address;
    private Location location;
    private boolean active;
    private Transmission transmission;
    private final ConcurrentLinkedQueue<Packet> buffer;
    private HashMap<short[], Node> routingTable;

    public Location getLocation() {
        return location;
    }

    public Node() {
        buffer = new ConcurrentLinkedQueue<>();
        location = LocationCreator.getInstance().getLocation();
        routingTable = new HashMap<>();
        active = true;
        address = AddressGenerator.generateAddress();
        thread = new Thread(this);
        transmission = new Transmission(Transmission.SignalStrength.VERYGOOD);
        thread.start();
        Network.registerNode(this);
    }

    @Override
    public void run() {
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
    }

    private void handleIPPacket(Packet packet) {
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

    public int getTransmissionRadius() {
        return transmission.transmissionRadius();
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public boolean isActive() {
        return thread.isAlive();
    }
    public void setAddress(short[] address) {
        this.address = address;
    }
}