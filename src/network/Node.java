package network;

import UI.SybilSimulator;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

import static network.Constants.Network.BROADCAST;
import static network.Constants.Protocol.*;

public class Node implements Comparator<Node>, Runnable {
    private Thread thread;
    private final Timer timer;
    private short[] address;
    private Location location;
    private int seqNum; // gränsnittets sekvensnummer för att göra det möjligt för grannar att sortera mellan paket
    private int ANSN; // ett sekvensnummer som ökar med 1 varje gång mängden av grannar i "neighborSet" uppdateras.
    private final HashMap<short[], HashMap<Integer, DuplicateTuple>> duplicateSets; // Innehåller info om mottagna paket för att undvika att samma paket vidarebefodras/bearbetas flera gånger om
    private boolean active; // true om nodens tråd är aktiv
    private Willingness willingness;
    private final HashMap<short[], NeighborTuple> neighborSet; // nyckeln är grannens ip-adress
    private short[][] mprSet; // lista över grannar som valts som MPR-nod
    private final HashMap<short[], LinkTuple> linkSet; // nyckeln är grannens ip-adress
    private final ArrayList<TwoHopTuple> twoHopNeighborSet;
    private final HashMap<short[], MPRSelectorTuple> mprSelectorSet; // innehåller info om grannar som vald denna nod till att bli en MPR-nod
    private final HashMap<short[], TopologyTuple> topologySet;
    private final Transmission transmission;
    private final ConcurrentLinkedQueue<OLSRPacket<OLSRMessage>> buffer; // tillfällig lagring av paket som inte än har bearbetas
    private final HashMap<short[], RoutingTuple> routingTable; // Nyckel: destination, värde: RoutingTuple

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
        timer = new Timer();
        duplicateSets = new HashMap<>();
        neighborSet = new HashMap<>();
        twoHopNeighborSet = new ArrayList<>();
        mprSelectorSet = new HashMap<>();
        topologySet = new HashMap<>();
        linkSet = new HashMap<>();
        willingness = Willingness.WILL_DEFAULT;
        Network.registerNode(this);
        thread.start();
    }

    @Override
    public void run() {
        while (active) {
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

    public void receivePacket(OLSRPacket<OLSRMessage> packet) {
        synchronized (buffer) {
            buffer.add(packet);
            buffer.notifyAll();
        }
    }

    private <T extends OLSRMessage> void handlePacket(OLSRPacket<T> packet) {
        for (OLSRMessage message : packet.messages) {
            if (OLSRPacket.canBeProcessed(message, address)) {
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
                    }
                    // Om sändarens adress ej finns i denna nods 1-hoppskvarter ska paketet slängas
                    if (!routingTable.containsKey(packet.ipHeader.sourceAddress))
                        dropPacket(packet);
                    else if (!Arrays.equals(tuple.d_iface, address) && !tuple.d_retransmitted) {
                        prepareForwardingOLSR(packet);
                    }
                }
            } else
                dropPacket(packet);
        }
    }

    private void updateDuplicateSet(OLSRMessage message) {
        DuplicateTuple tuple = duplicateSets.get(message.originatorAddr).get(message.msgSeqNum);
        tuple.renewTupple();
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
        OLSRPacket<HelloMessage> olsrPacket = new OLSRPacket<>(ipHeader, udpHeader, olsrHeader, messages);
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
        if (mprSetContains(tuple.l_neighbor_iface_addr))
            neighborType = LinkCode.NeighborTypes.MPR_NEIGH;
        else if ((neighborTuple = neighborSet.get(tuple.l_neighbor_iface_addr)) != null) {
            if (neighborTuple.status == NeighborTuple.N_status.SYM)
                neighborType = LinkCode.NeighborTypes.SYM_NEIGH;
            else
                neighborType = LinkCode.NeighborTypes.NOT_NEIGH;
        }
    }

    private boolean mprSetContains(short[] address) {
        for (short[] mpr : mprSet)
            if (Arrays.equals(address, mpr))
                return true;

        return false;
    }

    /**
     * Metoden utför de sista kontrollerna innan paketet verkligen vidarebefodras
     * @param packet Packet som ska vidarebefodras
     */
    private <T extends OLSRMessage>void prepareForwardingOLSR(OLSRPacket<T> packet) {
        // Nedanstående villkor är sant om avsändaradressen tillhör en nod som är en MPR selector till denna nod
        if (mprSelectorSet.containsKey(packet.ipHeader.sourceAddress))
            doForwardOLSRPacket(packet);
    }

    private <T extends OLSRMessage> void doForwardOLSRPacket(OLSRPacket<T> packet) {
        if (Constants.LOG_ACTIVE)
            System.out.println("Forward packet: " + packet.toString());
        try {
            Thread.sleep(calculateJitter());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        incrementSeqNum();
    }

    private <T extends OLSRMessage> void processAccordingToMsgType(OLSRPacket<T> packet) {
        PacketLocator.reportPacketTransport(packet.ipHeader.sourceAddress, address);
        for (OLSRMessage message : packet.messages) {
            switch (message.msgType) {
                case HELLO_MESSAGE:
                    processHelloMessage((HelloMessage) message);
                    break;
                case TC_MESSAGE:
                    if (neighborSet.get(packet.ipHeader.sourceAddress) == null) {
                        dropPacket(packet);
                        break;
                    }
                    processTCMessage((TCMessage) message);
                    break;
            }
        }

    }

    private void processTCMessage(TCMessage message) {
        long timeNow = System.currentTimeMillis();
        if (message.vTime >= timeNow) {
            TopologyTuple topologyTuple;
            if ((topologyTuple = topologySet.get(message.originatorAddr)) != null
                    && topologyTuple.t_seq < message.ANSN) {
                // Tar bort tuppel som innehåller äldre information än informationen i meddelandet
                synchronized (topologySet) {
                    topologySet.remove(topologyTuple.t_last_addr);
                }
            }
            for (short[] neighbor : message.advertisedNMA) {
                if ((topologyTuple = topologySet.get(message.originatorAddr)) != null) {
                    if (Arrays.equals(topologyTuple.t_last_addr, message.originatorAddr)) {
                        topologyTuple.renewTupple();
                    }
                } else {
                    topologyTuple = new TopologyTuple(neighbor, message.originatorAddr, ANSN);
                    topologySet.put(topologyTuple.t_last_addr,topologyTuple);
                }
            }
        }
    }

    private void processHelloMessage(HelloMessage message) {
        LinkTuple linkTuple;
        long timeNow = System.currentTimeMillis();
        if ((linkTuple = linkSet.get(message.originatorAddr)) == null) {
            linkTuple = new LinkTuple(address, message.originatorAddr, timeNow - 1, timeNow + message.vTime);
            linkSet.put(message.originatorAddr, linkTuple);
            detectNeighborLoss(linkTuple);
        } else {
            // sant om avsändaren är en symmetrisk granne
            if (linkTuple.l_sym_time >= timeNow) {
                LinkCode.NeighborTypes neighborType = message.linkCode.neighborType;
                if (neighborType == LinkCode.NeighborTypes.SYM_NEIGH || neighborType == LinkCode.NeighborTypes.MPR_NEIGH) {
                    // en nod är inte sin egen 2-hoppsgranne vilket är fallet om nedanstående inte är sant
                    if (!Arrays.equals(address, message.originatorAddr)) {
                        TwoHopTuple twoHopTuple = new TwoHopTuple(message.originatorAddr, message.neighborIfaceAdr);
                        //twoHopNeighborSet.add(twoHopTuple);
                        //removeTwoHopTimer(twoHopTuple);
                        updateTwoHopSet(message);
                    }
                }
            }
        }
        timeNow = System.currentTimeMillis();
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
        if (message.linkCode.neighborType == LinkCode.NeighborTypes.MPR_NEIGH)
            recordMPRSelector(message);

        // en länk som förlorar dess symmetri ska ändå annonseras i nätverket, åtminstående varaktigheten av giltighetstiden som finns definerad i HELLO-meddelandet.
        // detta tillåter grannar att upptäcka länkbräckage.
        linkTuple.l_time = Math.max(linkTuple.l_time, linkTuple.l_asym_time);
        NeighborTuple neighborTuple;
        if ((neighborTuple = neighborSet.get(linkTuple.l_neighbor_iface_addr)) == null)
            neighborTuple = new NeighborTuple(linkTuple.l_neighbor_iface_addr, NeighborTuple.N_status.NOT_SYM, willingness);
        else
            neighborTuple.n_willingness = message.willingness;
        changeNeighborStatus(linkTuple, neighborTuple);
        removeLinkTupleTimer(linkTuple);
        mprSet = new MPRCalculator(neighborSet.values(), twoHopNeighborSet, address).populateAndReturnMPRSet();
    }

    public void updateRoutingTable() {
        routingTable.clear();
        for (NeighborTuple tuple : neighborSet.values()) {
            if (tuple.status == NeighborTuple.N_status.SYM) {
                LinkTuple linkTuple;
                if ((linkTuple = findLinkTuple(tuple)) != null && linkTuple.l_time >= System.currentTimeMillis()) {
                    RoutingTuple routingTuple;
                    if (routingTable.containsKey(tuple.n_neighbor_main_addr)) {
                        routingTuple = new RoutingTuple(linkTuple.l_neighbor_iface_addr, linkTuple.l_neighbor_iface_addr, 1, linkTuple.l_local_iface_addr);
                    } else {
                        routingTuple = new RoutingTuple(tuple.n_neighbor_main_addr, linkTuple.l_neighbor_iface_addr, 1, linkTuple.l_local_iface_addr);
                    }
                    routingTable.put(routingTuple.r_dest_addr, routingTuple);
                }
            }
        }
        for (TwoHopTuple tuple : twoHopNeighborSet) {
            if (!Arrays.equals(address, tuple.n_2hop_addr) && !neighborSet.containsKey(tuple.n_2hop_addr)) {
                RoutingTuple neighbor = findExistingRouteTuple(tuple);
                RoutingTuple routingTuple = new RoutingTuple(tuple.n_2hop_addr, neighbor.r_next_addr, 2, neighbor.r_iface_addr);
                routingTable.put(routingTuple.r_dest_addr, routingTuple);
            }
        }
        for (int h = 2, count = 0;;h++, count = 0) {
            for (TopologyTuple topologyTuple : topologySet.values()) {
                RoutingTuple tuple = null;
                if ((routingTable.get(topologyTuple.t_dest_addr) == null) ||
                        (Arrays.equals(topologyTuple.t_last_addr, tuple.r_dest_addr) && tuple.r_dist == h)) {
                    RoutingTuple existing = findExistingRouteTuple(topologyTuple);
                    if (existing != null) {
                        RoutingTuple routingTuple = new RoutingTuple(topologyTuple.t_dest_addr, existing.r_next_addr, h + 1, existing.r_iface_addr);
                        routingTable.put(routingTuple.r_dest_addr, routingTuple);
                        count++;
                    }
                }
            }
            // om nedanstående är sant finns inga fler grannar som ligger det antal hop som är specificerat i variabeln h
            if (count == 0)
                break;
        }
    }

    public RoutingTuple findExistingRouteTuple(TopologyTuple topologyTuple) {
        for (RoutingTuple routingTuple : routingTable.values())
            if (Arrays.equals(routingTuple.r_dest_addr, topologyTuple.t_last_addr))
                return routingTuple;

        return null;
    }
    public RoutingTuple findExistingRouteTuple(TwoHopTuple twoHopTuple) {
        for (RoutingTuple routingTuple : routingTable.values())
            if (Arrays.equals(routingTuple.r_dest_addr, twoHopTuple.n_2hop_addr))
                return routingTuple;

        return null;
    }

    private LinkTuple findLinkTuple(NeighborTuple neighborTuple) {
        for (LinkTuple tuple : linkSet.values()) {
            if (Arrays.equals(tuple.l_neighbor_iface_addr, neighborTuple.n_neighbor_main_addr)) {
                return tuple;
            }
        }
        return null;
    }

    private void detectNeighborLoss(LinkTuple tuple) {
        long timeNow = System.currentTimeMillis();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                if (tuple.l_asym_time > timeNow)
                    timer.schedule(this, (long) tuple.l_asym_time - timeNow);
                else
                    lossOfNeighbor(tuple.l_neighbor_iface_addr);
            }
        }, (long) tuple.l_asym_time - timeNow);
    }

    private void recordMPRSelector(HelloMessage message) {
        MPRSelectorTuple tuple;
        if ((tuple = mprSelectorSet.get(message.originatorAddr)) == null) {
            tuple = new MPRSelectorTuple(message.originatorAddr);
            mprSelectorSet.put(message.originatorAddr, tuple);
            removeMPRSelectorTimer(tuple);
        } else {
            tuple.ms_main_addr = message.originatorAddr;
            tuple.renewTupple();
        }
    }

    /**
     * När en grannod försvinner måste alla tvåhoppsgrannetuppler med grannadress lika med grannodens adress tas bort.
     * Detsamma gäller för tupplerna i mprSelectorSet.
     * @param neighborAddress Grannodens adress
     */
    private void lossOfNeighbor(short[] neighborAddress) {
        synchronized (mprSelectorSet) {
            mprSelectorSet.remove(neighborAddress);
        }
        synchronized (twoHopNeighborSet) {
            twoHopNeighborSet.removeIf((tuple) -> Arrays.equals(tuple.n_neighbor_main_addr, neighborAddress));
        }
        mprSet = new MPRCalculator(neighborSet.values(), twoHopNeighborSet, address).populateAndReturnMPRSet();
    }

    private void removeMPRSelectorTimer(MPRSelectorTuple tuple) {
        long timeNow = System.currentTimeMillis();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                if (tuple.get_time() > timeNow)
                    timer.schedule(this, (long) tuple.get_time() - timeNow);
                else {
                    synchronized (mprSelectorSet) {
                        mprSelectorSet.remove(tuple.ms_main_addr);
                    }
                }
            }
        }, (long) tuple.get_time() - timeNow);
    }

    private void removeLinkTupleTimer(LinkTuple tuple) {
        long timeNow = System.currentTimeMillis();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                // fältet l_time kan ha uppdateras mellan tiden då detta TimerTask-objektet skapades och när metoden run() anropas
                if (tuple.l_time > timeNow)
                    timer.schedule(this, (long) tuple.l_time - timeNow);
                else {
                    synchronized (linkSet) {
                        linkSet.remove(tuple.l_neighbor_iface_addr);
                    }
                    synchronized (neighborSet) {
                        neighborSet.remove(tuple.l_neighbor_iface_addr);
                    }
                }
            }
        }, (long) tuple.l_time - timeNow);
    }

    private void removeTwoHopTimer(TwoHopTuple tuple) {
        long timeNow = System.currentTimeMillis();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                // fältet l_time kan ha uppdateras mellan tiden då detta TimerTask-objektet skapades och när metoden run() anropas
                if (tuple.get_time() > timeNow)
                    timer.schedule(this, (long) tuple.get_time() - timeNow);
                else {
                    lossOfNeighbor(tuple.n_neighbor_main_addr);
                }
            }
        }, (long) tuple.get_time() - timeNow);
    }

    private void updateTwoHopSet(HelloMessage message) {
        twoHopNeighborSet.removeIf(tuple -> Arrays.equals(tuple.n_neighbor_main_addr, message.originatorAddr)
                && Arrays.equals(tuple.n_2hop_addr, message.neighborIfaceAdr));
    }

    private void changeNeighborStatus(LinkTuple tuple, NeighborTuple neighborTuple) {
        NeighborTuple.N_status n_status = NeighborTuple.N_status.NOT_SYM;
        long timeNow = System.currentTimeMillis();
        if (tuple.l_sym_time >= timeNow)
            n_status = NeighborTuple.N_status.SYM;
        neighborTuple.status = n_status;
    }

    // Specifikationerna för meddelandetypen bestämmer hur paketet ska vidarebefodras
    // HELLO-paket vidarbefodras ej, TC-meddelande vidarbefodras av MPR-noder enligt standardalgoritm
    private void forwardAccordingToMsgType(OLSRPacket packet) {
    }

    private long calculateJitter() {
        return (long)(Math.random() * MAX_JITTER_MS);
    }

    private void dropPacket(OLSRPacket packet) {
        if (Constants.LOG_ACTIVE)
            System.out.println("Packet dropped: " + packet.toString());
        Node node = Network.getNodeList()
        SybilSimulator.animateDroppedPath(node);
    }

    public void turnOff() {
        active = false;
        timer.cancel();
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