package network;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import static network.Constants.Protocol.DUP_HOLD_TIME;

public class Node implements Comparator<Node>, Runnable {
    private Thread thread;
    private short[] address;
    private Location location;
    private int seqNum; // gränsnittets sekvensnummer
    private HashMap<short[], HashMap<Integer, DuplicateTuple>> duplicateSets; // Innehåller info om mottagna paket för att undvika att samma paket vidarebefodras/bearbetas flera gånger om
    private boolean active; // true om nodens tråd är aktiv
    private HashMap<short[], Node> MPRSelectors; // Samling innehållandes noder som valt denna nod att vara en MPR-nod
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
        duplicateSets = new HashMap<>();
        MPRSelectors = new HashMap<>();
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
            HashMap<Integer, DuplicateTuple> duplicateSet;
            // Kontrollerar om ursprungsadressen finns i samlingen över Duplicate Set
            if ((duplicateSet = duplicateSets.get(packet.originatorAddr)) != null) {
                DuplicateTuple tuple;
                // om nedanstående villlkor är sant ska paketet ej bearbetas men möjligtvis ska den skickas vidare
                if ((tuple = duplicateSet.get(seqNum)) != null) {
                    // om nedanstående villkor är sant ska paketet förberedas för vidaresändning
                    if (!Arrays.equals(tuple.d_iface, address))
                        prepareForwardingOLSR(packet);
                    // Paketet ska varken bearbetas eller skickas vidare
                    else {
                        updateDuplicateSet(packet);
                        dropPacket(packet);
                    }
                } else {
                    processOLSRPacket(packet);
                }

                // Om sändarens adress ej finns i denna nods 1-hoppskvarter ska paketet slängas
                if (!routingTable.contains(packet.ipHeader.sourceAddress))
                    dropPacket(packet);
                else if (!Arrays.equals(tuple.d_iface, address) && !tuple.d_retransmitted) {
                    prepareForwardingOLSR(packet);
                }
                else
                    processOLSRPacket(packet);

            }
            else if (doImplementMsgType())
                processAccordingToMsgType(packet);
        }
        else
            dropPacket(packet);
    }

    private void updateDuplicateSet(OLSRPacket packet) {
        DuplicateTuple tuple = duplicateSets.get(packet.originatorAddr).get(packet.seqNum);
        tuple.d_time = System.currentTimeMillis()/1000 + DUP_HOLD_TIME;
        tuple.d_iface = address;
        tuple.d_retransmitted = true;
    }


    /**
     * Metoden utför de sista kontrollerna innan paketet verkligen vidarebefodras
     * @param packet Packet som ska vidarebefodras
     */
    private void prepareForwardingOLSR(OLSRPacket packet) {
        if (doImplementMsgType())
            forwardAccordingToMsgType(packet);
        // Nedanstående villkor är sant om avsändaradressen tillhör en nod som är en MPR selector till denna nod
        if (MPRSelectors.containsKey(packet.ipHeader.sourceAddress))
            doForwardOLSRPacket(packet);
    }

    private void doForwardOLSRPacket(OLSRPacket packet) {
        if (Constants.LOG_ACTIVE)
            System.out.println("Forward packet: " + packet.toString());
        incrementSeqNum();


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

    private void forwardAccordingToMsgType(OLSRPacket packet) {

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