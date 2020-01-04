package network;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Node implements Comparator<Node>, Runnable {
    private Thread thread;
    private short[] address;
    private Location location;
    private int seqNum; // gränsnittets sekvensnummer
    private HashMap<short[], DuplicateTuple> duplicateSet;
    private boolean active; // true om nodens tråd är aktiv
    private boolean isMPR; // true om noden är en multipoint relay vars uppgift är att vidarebefodra kontrolltraffik
    private Transmission transmission;
    private final ConcurrentLinkedQueue<OLSRPacket> buffer; // tillfällig lagring av paket som inte än har bearbetas
    private ArrayList<short[]> routingTable;

    public Location getLocation() {
        return location;
    }

    public Node() {
        buffer = new ConcurrentLinkedQueue<>();
        location = LocationCreator.getInstance().getLocation();
        routingTable = new ArrayList<>();
        active = true;
        address = AddressGenerator.generateAddress();
        thread = new Thread(this);
        transmission = new Transmission(Transmission.SignalStrength.VERYGOOD);
        thread.start();
        duplicateSet = new HashMap<>();
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

    public void receivePacket(OLSRPacket packet) {
        synchronized (buffer) {
            buffer.add(packet);
            buffer.notifyAll();
        }
    }

    private void handlePacket(OLSRPacket packet) {
        if (OLSRPacket.canBeProcessed(packet)) {
            DuplicateTuple tuple;
            if ((tuple = duplicateSet.get(packet.originatorAddr)) != null) {
                // Om nedanstående villkor är sanna har paketet redan bearbetats förut eller att noden implementerar meddelandetypen, då måste bearbetning ske
                if (Arrays.equals(tuple.d_addr, packet.originatorAddr) && tuple.d_seq_num == packet.seqNum)
                        dropPacket(packet);
                else {
                    if (!Arrays.equals(tuple.d_iface, packet.ipHeader.destinationAdress) && !tuple.d_retransmitted) {
                        if (doImplementMsgType())
                            processAccordingToMsgType(packet);
                        else
                            forwardOLSRPacket(packet);
                    }
                    else
                        processOLSRPacket(packet);
                }
            }
        } else
            dropPacket(packet);

    }


    private void forwardOLSRPacket(OLSRPacket packet) {
        if (Constants.LOG_ACTIVE)
            System.out.println("Forward packet: " + packet.toString());
        if (doImplementMsgType())
            processAccordingToMsgType(packet);
        // Avsändarens adress måste finnas i denna nods routingtabell, alltså är 1-hoppsgranne till denna nod
        incrementSeqNum();
        if (routingTable.contains(packet.originatorAddr)) {
        }

    }

    private void processOLSRPacket(OLSRPacket packet) {
        if (Constants.LOG_ACTIVE)
            System.out.println("Process packet: " + packet.toString());
        incrementSeqNum();

    }

    private boolean doImplementMsgType() {
        // Specifikationerna för meddelandetypen bestämmer hur paketet ska vidarebefodras
        return false;
    }

    private void processAccordingToMsgType(OLSRPacket packet) {

    }

    private void dropPacket(OLSRPacket packet) {
        if (Constants.LOG_ACTIVE)
            System.out.println("Packet dropped: " + packet.toString());
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
    public void incrementSeqNum() {
       seqNum = (short) (seqNum+1 % Short.MAX_VALUE);
    }
}