package network;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static network.Constants.Network.BROADCAST;
import static network.Constants.Protocol.*;

public class Node implements Comparator<Node>, Runnable {
    private Thread thread;
    private short[] address;
    private Location location;
    private int seqNum; // gränsnittets sekvensnummer
    private final HashMap<short[], HashMap<Integer, DuplicateTuple>> duplicateSets; // Innehåller info om mottagna paket för att undvika att samma paket vidarebefodras/bearbetas flera gånger om
    private boolean active; // true om nodens tråd är aktiv
    private boolean isMPR; // true om noden är en multipoint relay vars uppgift är att vidarebefodra kontrolltraffik
    private Willingness willingness;
    private HashMap<short[], NeighborTuple> neighborSet; // nyckeln är grannens ip-adress
    private ArrayList<short[]> mprSet; // lista över grannar som valts som MPR-nod
    private final HashMap<short[], LinkTuple> linkSet; // nyckeln är grannens ip-adress
    private ArrayList<TwoHopTuple> twoHopNeighborSet;
    private HashMap<short[], Double> mprSelectorSet; // innehåller info om grannar som vald denna nod till att bli en MPR-nod
    private ArrayList<TopologyTuple> topologySet;
    private Transmission transmission;
    private final ArrayList<Timer> timers;
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
        timers = new ArrayList<>();
        neighborSet = new HashMap<>();
        twoHopNeighborSet = new ArrayList<>();
        mprSelectorSet = new HashMap<>();
        topologySet = new ArrayList<>();
        linkSet = new HashMap<>();
        mprSet = new ArrayList<>();
        willingness = Willingness.WILL_DEFAULT;
        Network.registerNode(this);
    }

    @Override
    public void run() {
        while (active) {
            Timer timer = new Timer();
            timers.add(timer);
            timer.schedule(sendHelloMsgTask(), 0, HELLO_INTERVAL);
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

    private TimerTask sendHelloMsgTask() {
        return new TimerTask() {
            @Override
            public void run() {
                sendHelloPacket();
            }
        };
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

    private <T extends OLSRMessage> void handlePacket(OLSRPacket<T> packet) {
        for (OLSRMessage message : packet.messages) {
            if (OLSRPacket.canBeProcessed(message, address)) {
                if (doImplementMsgType())
                    processAccordingToMsgType(packet);
                HashMap<Integer, DuplicateTuple> duplicateSet;
                // Kontrollerar om ursprungsadressen finns i samlingen över Duplicate Set
                if ((duplicateSet = duplicateSets.get(message.originatorAddr)) != null) {
                    DuplicateTuple tuple;
                    // om nedanstående villlkor är sant ska paketet ej bearbetas men möjligtvis ska den skickas vidare
                    if ((tuple = duplicateSet.get(seqNum)) != null) {
                        // om nedanstående villkor är sant ska paketet förberedas för vidaresändning
                        if (!Arrays.equals(tuple.d_iface, address))
                            prepareForwardingOLSR(packet);
                            // Paketet ska varken bearbetas eller skickas vidare
                        else {
                            //updateDuplicateSet(packet);
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
                    } else
                        processOLSRPacket(packet);

                } else if (doImplementMsgType())
                    processAccordingToMsgType(packet);
            } else
                dropPacket(packet);
        }
    }

    private void updateDuplicateSet(OLSRMessage message) {
        DuplicateTuple tuple = duplicateSets.get(message.originatorAddr).get(message.msgSeqNum);
        tuple.d_time = System.currentTimeMillis()/1000 + DUP_HOLD_TIME;
        tuple.d_iface = address;
        tuple.d_retransmitted = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (duplicateSets) {
                    duplicateSets.get(message.originatorAddr).remove(message.msgSeqNum);
                }
            }
        }, DUP_HOLD_TIME);
    }

    private void sendHelloPacket() {
        LinkCode.LinkTypes linkType = null;
        LinkCode.NeighborTypes neighborType = null;
        ArrayList<HelloMessage> messages = new ArrayList<>();
        int seqNum = 0;
        ArrayList<short[]> linkNeighbors = new ArrayList<>(); // grannar som har en länk till denna nod, info som används för att senare hitta grannar utan länk till denna nod
        for (LinkTuple tuple : linkSet.values()) {
            setLinkandNeighborType(linkType, neighborType, tuple);
            LinkCode linkCode = new LinkCode(linkType, neighborType);
            linkNeighbors.add(tuple.l_neighbor_iface_addr);
            messages.add(new HelloMessage(address, seqNum++, willingness, linkCode, tuple.l_neighbor_iface_addr));
        }
        if (linkNeighbors.size() != neighborSet.values().size()) {
            for (NeighborTuple tuple : neighborSet.values()) {
                if (!linkSet.containsKey(tuple.n_neighbor_main_addr)) {
                    neighborType = tuple.status == NeighborTuple.N_status.SYM ? LinkCode.NeighborTypes.SYM_NEIGH : LinkCode.NeighborTypes.NOT_NEIGH;
                    LinkCode linkCode = new LinkCode(LinkCode.LinkTypes.UNSPEC_LINK, neighborType);
                    messages.add(new HelloMessage(address, seqNum++, willingness, linkCode, tuple.n_neighbor_main_addr));
                }
            }
        }
        IPHeader ipHeader = new IPHeader(messages.size() * HelloMessage.length(), address, BROADCAST, UDP_PROTOCOL_NUM);
        UDPHeader udpHeader = new UDPHeader(OLSR_PORT, OLSR_PORT,(short) (ipHeader.getTotalLength() + OLSR_HEADER_SIZE));
        OLSRHeader olsrHeader = new OLSRHeader((short) (ipHeader.getTotalLength() + OLSR_HEADER_SIZE), this.seqNum);
        OLSRPacket olsrPacket = new OLSRPacket(ipHeader, udpHeader, olsrHeader, messages);
        incrementSeqNum();
        Network.sendPacket(this, olsrPacket);
    }

    private void setLinkandNeighborType(LinkCode.LinkTypes linkType, LinkCode.NeighborTypes neighborType, LinkTuple tuple) {
        long timeNow = System.currentTimeMillis();
        NeighborTuple neighborTuple;
        if (tuple.l_sym_time >= timeNow)
            linkType = LinkCode.LinkTypes.SYM_LINK;
        else if (tuple.l_asym_time >= timeNow)
            linkType = LinkCode.LinkTypes.ASYM_LINK;
        else
            linkType = LinkCode.LinkTypes.LOST_LINK;
        if (mprSet.contains(tuple.l_neighbor_iface_addr))
            neighborType = LinkCode.NeighborTypes.MPR_NEIGH;
        else if ((neighborTuple = neighborSet.get(tuple.l_neighbor_iface_addr)) != null) {
            if (neighborTuple.status == NeighborTuple.N_status.SYM)
                neighborType = LinkCode.NeighborTypes.SYM_NEIGH;
            else
                neighborType = LinkCode.NeighborTypes.NOT_NEIGH;
        }
    }

    /**
     * Metoden utför de sista kontrollerna innan paketet verkligen vidarebefodras
     * @param packet Packet som ska vidarebefodras
     */
    private void prepareForwardingOLSR(OLSRPacket packet) {
        if (doImplementMsgType())
            forwardAccordingToMsgType(packet);
        // Nedanstående villkor är sant om avsändaradressen tillhör en nod som är en MPR selector till denna nod
        if (mprSelectorSet.containsKey(packet.ipHeader.sourceAddress))
            doForwardOLSRPacket(packet);
    }

    private void doForwardOLSRPacket(OLSRPacket packet) {
        if (Constants.LOG_ACTIVE)
            System.out.println("Forward packet: " + packet.toString());
        try {
            Thread.sleep(calculateJitter());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        incrementSeqNum();


    }

    private void processOLSRPacket(OLSRPacket packet) {
        if (Constants.LOG_ACTIVE)
            System.out.println("Process packet: " + packet.toString());
        incrementSeqNum();

    }

    private boolean doImplementMsgType() {
        return true;
    }

    private <T extends OLSRMessage> void processAccordingToMsgType(OLSRPacket<T> packet) {
        for (OLSRMessage message : packet.messages) {
            switch (message.msgType) {
                case HELLO_MESSAGE:
                    processHelloMessage((HelloMessage) message);
                    break;
            }
        }

    }

    private void processHelloMessage(HelloMessage message) {
        LinkTuple linkTuple;
        long timeNow = System.currentTimeMillis();
        if ((linkTuple = linkSet.get(message.originatorAddr)) == null) {
            linkTuple = new LinkTuple(address, message.originatorAddr, timeNow - 1, timeNow + message.vTime, message.vTime);
            linkSet.put(message.originatorAddr, linkTuple);
        }
        linkTuple.l_asym_time = timeNow + message.vTime;
        // true om denna nods adress finns med i meddelandet
        if (Arrays.equals(message.neighborIfaceAdr, address)) {
            if (message.linkCode.linkType == LinkCode.LinkTypes.LOST_LINK)
                linkTuple.l_sym_time = timeNow - 1; // tiden har löpt ut
            else if (message.linkCode.linkType == LinkCode.LinkTypes.ASYM_LINK || message.linkCode.linkType == LinkCode.LinkTypes.SYM_LINK) {
                linkTuple.l_sym_time = timeNow + message.vTime;
                linkTuple.l_time = linkTuple.l_sym_time + NEIGHB_HOLD_TIME;
            }
        }
        // en länk som förlorar dess symmetri ska ändå annonseras i nätverket, åtminstående varaktigheten av giltighetstiden som finns definerad i HELLO-meddelandet.
        // detta tillåter grannar att upptäcka länkbräckage.
        linkTuple.l_time = Math.max(linkTuple.l_time, linkTuple.l_asym_time);
        NeighborTuple neighborTuple;
        if ((neighborTuple = neighborSet.get(linkTuple.l_neighbor_iface_addr)) == null)
            neighborTuple = new NeighborTuple(linkTuple.l_neighbor_iface_addr, NeighborTuple.N_status.NOT_SYM, willingness);
        changeNeighborStatus(linkTuple, neighborTuple);
        addRemoveLinkTupleTimer(linkTuple);
    }

    private void addRemoveLinkTupleTimer(LinkTuple tuple) {
        Timer timer = new Timer();
        long timeNow = System.currentTimeMillis();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (linkSet) {
                    linkSet.remove(tuple.l_neighbor_iface_addr);
                }
                synchronized (neighborSet) {
                    neighborSet.remove(tuple.l_neighbor_iface_addr);
                }
            }
        }, (long) tuple.l_time - timeNow);
        timers.add(timer);
    }

    private void changeNeighborStatus(LinkTuple tuple, NeighborTuple neighborTuple) {
        NeighborTuple.N_status n_status = NeighborTuple.N_status.NOT_SYM;
        long timeNow = System.currentTimeMillis();
        if (tuple.l_sym_time >= timeNow)
            n_status = NeighborTuple.N_status.SYM;
        neighborTuple.status = n_status;
    }

    // Specifikationerna för meddelandetypen bestämmer hur paketet ska vidarebefodras
    private void forwardAccordingToMsgType(OLSRPacket packet) {
    }

    private long calculateJitter() {
        return (long)(Math.random() * MAX_JITTER_MS);
    }

    private void dropPacket(OLSRPacket packet) {
        if (Constants.LOG_ACTIVE)
            System.out.println("Packet dropped: " + packet.toString());
    }

    public void turnOff() {
        active = false;
        synchronized (timers) {
            for (Timer timer : timers)
                timer.cancel();
        }
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