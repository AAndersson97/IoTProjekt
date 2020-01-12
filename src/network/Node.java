package network;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private final HashMap<short[], TwoHopTuple> twoHopNeighborSet; // nykeln är tvåhoppsgrannen
    private final HashMap<short[], MPRSelectorTuple> mprSelectorSet; // innehåller info om grannar som vald denna nod till att bli en MPR-nod
    private final HashMap<short[], TopologyTuple> topologySet; // nyckeln är destinationsadressen
    private final Transmission transmission;
    private final ConcurrentLinkedQueue<Packet> buffer; // tillfällig lagring av paket som inte än har bearbetas
    private final HashMap<short[], RoutingTuple> routingTable; // Nyckel: destination, värde: RoutingTuple

    private static int count = 0;

    public Location getLocation() {
        return location;
    }

    public Node() {
        buffer = new ConcurrentLinkedQueue<>();
        routingTable = new HashMap<>();
        active = true;
        address = AddressGenerator.generateAddress();
        thread = new Thread(this);
        transmission = new Transmission(Transmission.SignalStrength.VERYGOOD);
        timer = new Timer();
        duplicateSets = new HashMap<>();
        neighborSet = new HashMap<>();
        twoHopNeighborSet = new HashMap<>();
        mprSelectorSet = new HashMap<>();
        topologySet = new HashMap<>();
        linkSet = new HashMap<>();
        willingness = Willingness.WILL_DEFAULT;
        if (this.getClass() == Node.class) {
            location = LocationManager.getInstance().getLocation(this);
            // Innan tråden kan startas i SybilNode- och i AttackNode-objekten måste objektet skapas först
            thread.start();
        }
        Network.registerNode(this);
    }

    @Override
    public void run() {
        timer.schedule(sendOLSRMsgTask(), 0, HELLO_INTERVAL);
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

    private TimerTask sendOLSRMsgTask() {
        return new TimerTask() {
            @Override
            public void run() {
                sendHelloPacket();
                if (!mprSelectorSet.isEmpty())
                    sendTCPacket();
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

    public void receivePacket(Packet packet) {
        synchronized (buffer) {
            buffer.add(packet);
            buffer.notifyAll();
        }
    }

    /**
     * Metodens syfte är att ta reda på vilket sorts paket som skickats till noden. Efteråt dirigeras arbetet för bearbetning eller vidaresändning till annan metod, alternativt slängs paketet.
     * @param packet Paketet som noden mottagit och ska bearbetas, vidaresändas eller kastas.
     */
    private void handlePacket(Packet packet) {
        if (packet instanceof OLSRPacket) {
            OLSRPacket olsrPacket = (OLSRPacket) packet;
            processAccordingToMsgType(olsrPacket);
        } else {
            // Om sändarens adress ej finns i denna nods 1-hoppskvarter ska paketet slängas
            if (!routingTable.containsKey(packet.ipHeader.sourceAddress))
                dropPacket(packet);
        }
    }

    private void updateDuplicateSet(OLSRMessage message) {
        HashMap<Integer, DuplicateTuple> duplicateSet = duplicateSets.get(message.originatorAddr);
        if (duplicateSet != null && duplicateSet.containsKey(message.msgSeqNum)) {
            DuplicateTuple tuple = duplicateSet.get(message.msgSeqNum);
            tuple.renewTupple();
            tuple.d_iface = address;
        } else {
            if (duplicateSet == null) {
                duplicateSet = new HashMap<>();
                duplicateSets.put(message.originatorAddr, duplicateSet);
            }
            DuplicateTuple tuple = new DuplicateTuple(message.originatorAddr, address, message.msgSeqNum);
            duplicateSet.put(message.msgSeqNum, tuple);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (duplicateSets) {
                        duplicateSets.get(message.originatorAddr).remove(message.msgSeqNum);
                    }
                }
            }, DUP_HOLD_TIME);
        }
    }

    private void sendTCPacket() {
        int count = 0;
        short[][] advertisedNMA = new short[linkSet.size()][];
        for (LinkTuple linkTuple : linkSet.values())
            advertisedNMA[count++] = linkTuple.l_neighbor_iface_addr;
        TCMessage tcMessage = new TCMessage(address, seqNum, ANSN++, advertisedNMA);
        IPHeader ipHeader = new IPHeader(advertisedNMA.length * (ADDRESS_LENGTH * Short.SIZE), address, BROADCAST, UDP_PROTOCOL_NUM);
        UDPHeader udpHeader = new UDPHeader(OLSR_PORT, OLSR_PORT,(short) (ipHeader.getTotalLength() + OLSR_HEADER_SIZE));
        OLSRHeader olsrHeader = new OLSRHeader((short) (ipHeader.getTotalLength() + OLSR_HEADER_SIZE), this.seqNum);
        OLSRPacket olsrPacket = new OLSRPacket(ipHeader, udpHeader, olsrHeader, tcMessage);
        incrementSeqNum();
        Network.sendPacket(this, BROADCAST, olsrPacket);
    }

    private void sendHelloPacket() {
        HashMap<LinkCode, ArrayList<short[]>> neighbors = new HashMap<>(); // grannar som har en länk till denna nod, info som används för att senare hitta grannar utan länk till denna nod
        long timeNow = System.currentTimeMillis();
        synchronized (this) {
            for (LinkTuple tuple : linkSet.values()) {
                if (tuple.l_time >= timeNow) {
                    LinkCode linkCode = createLinkCode(tuple);
                    if (linkCode == null)
                        continue;
                    neighbors.computeIfAbsent(linkCode, k -> new ArrayList<>());
                    neighbors.get(linkCode).add(tuple.l_neighbor_iface_addr);
                }
            }
            for (NeighborTuple tuple : neighborSet.values()) {
                if (!linkSet.containsKey(tuple.n_neighbor_main_addr)) {
                    LinkCode.NeighborTypes neighborType = tuple.status == NeighborTuple.N_status.SYM ? LinkCode.NeighborTypes.SYM_NEIGH : LinkCode.NeighborTypes.NOT_NEIGH;
                    LinkCode linkCode = new LinkCode(LinkCode.LinkTypes.UNSPEC_LINK, neighborType);
                    neighbors.computeIfAbsent(linkCode, k -> new ArrayList<>());
                    neighbors.get(linkCode).add(tuple.n_neighbor_main_addr);
                }
            }
        }
        HelloMessage messages = new HelloMessage(address, seqNum, willingness, neighbors);
        IPHeader ipHeader = new IPHeader(HelloMessage.length(), address, BROADCAST, UDP_PROTOCOL_NUM);
        UDPHeader udpHeader = new UDPHeader(OLSR_PORT, OLSR_PORT,(short) (ipHeader.getTotalLength() + OLSR_HEADER_SIZE));
        OLSRHeader olsrHeader = new OLSRHeader((short) (ipHeader.getTotalLength() + OLSR_HEADER_SIZE), this.seqNum);
        OLSRPacket<HelloMessage> olsrPacket = new OLSRPacket<>(ipHeader, udpHeader, olsrHeader, messages);
        incrementSeqNum();
        Network.sendPacket(this, BROADCAST, olsrPacket);
    }

    private LinkCode createLinkCode(LinkTuple tuple) {
        long timeNow = System.currentTimeMillis();
        LinkCode.LinkTypes linkType;
        LinkCode.NeighborTypes neighborType;
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
        } else
            return null;

        return new LinkCode(linkType, neighborType);
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
    private void prepareForwardingOLSR(OLSRPacket packet) {
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

    private void processAccordingToMsgType(OLSRPacket packet) {
        PacketLocator.reportPacketTransport(packet.ipHeader.sourceAddress, address, packet);
        boolean dropped = false;
            switch (packet.message.msgType) {
                case HELLO_MESSAGE:
                    dropped = !processHelloMessage((HelloMessage) packet.message);
                    break;
                case TC_MESSAGE:
                    if (isADuplicate(packet.message) || neighborSet.get(packet.ipHeader.sourceAddress) == null) {
                        dropped = true;
                        dropPacket(packet);
                        break;
                    }
                    processTCMessage((TCMessage) packet.message);
                    System.out.println("TC Message from: " + Arrays.toString(packet.message.originatorAddr));
                    break;
            }
        if (!dropped)
            updateRoutingTable();
    }

    /**
     * Metoden kontrollera i duplicateSets för att se om samma meddelandet redan skickats förut.
     * @return true om meddelandet är en dubblett
     */
    private boolean isADuplicate(OLSRMessage message) {
        HashMap<Integer, DuplicateTuple> duplicateSet;
        boolean isADuplicate = false;
        // Kontrollerar om ursprungsadressen finns i samlingen över Duplicate Set
        if ((duplicateSet = duplicateSets.get(message.originatorAddr)) != null) {
            // om nedanstående villlkor är sant ska paketet ej bearbetas men möjligtvis ska den skickas vidare
            isADuplicate = duplicateSet.containsKey(message.msgSeqNum);
        } if (isADuplicate)
            updateDuplicateSet(message);
        return isADuplicate;
    }

    private boolean alreadyForwarded(OLSRMessage message) {
        DuplicateTuple tuple = duplicateSets.get(message.originatorAddr).get(message.msgSeqNum);
        return Arrays.equals(tuple.d_addr, address);
    }

    private void processTCMessage(TCMessage message) {
        long timeNow = System.currentTimeMillis();
        if (linkSet.containsKey(message.originatorAddr) && message.vTime >= timeNow) {
            TopologyTuple topologyTuple;
            synchronized (topologySet) {
                if ((topologyTuple = topologySet.get(message.originatorAddr)) != null) {
                    if (topologyTuple.t_seq < message.ANSN) {
                        // Tar bort tuppel som innehåller äldre information än informationen i meddelandet
                        topologySet.remove(topologyTuple.t_last_addr);
                    } else {
                        return;
                    }
                }
                for (short[] neighbor : message.advertisedNMA) {
                    if ((topologyTuple = topologySet.get(message.originatorAddr)) != null) {
                        topologyTuple.renewTupple();
                    } else {
                        topologyTuple = new TopologyTuple(neighbor, message.originatorAddr, ANSN);
                        topologySet.put(topologyTuple.t_last_addr, topologyTuple);
                        removeTCTuppleTimer(topologyTuple);
                    }
                }
            }
        }
    }

    private void removeTCTuppleTimer(TopologyTuple topologyTuple) {
        synchronized (timer) {
            timer.schedule(createTCTuppleRemoveTask(topologyTuple), 0, topologyTuple.get_time() - System.currentTimeMillis());
        }
    }

    private boolean processHelloMessage(HelloMessage message) {
        LinkTuple linkTuple;
        long timeNow = System.currentTimeMillis();
        boolean linkUpdated = false, linkAdded = false;
        // Bearbeta inte paket vars giltighetstid har gått ut
        if (message.vTime < timeNow)
            return false;
        synchronized (this) {
            if ((linkTuple = linkSet.get(message.originatorAddr)) == null) {
                linkTuple = new LinkTuple(address, message.originatorAddr, timeNow - 1, message.vTime);
                linkSet.put(message.originatorAddr, linkTuple);
                linkAdded = true;
                detectNeighborLoss(linkTuple);
            }
            linkTuple.l_neighbor_iface_addr = message.originatorAddr;
            linkTuple.l_asym_time = message.vTime;
            for (LinkCode linkCode : message.neighborIfaceAdr.keySet()) {
                timeNow = System.currentTimeMillis();
                for (short[] neighborAddress : message.neighborIfaceAdr.get(linkCode)) {
                    if (Arrays.equals(neighborAddress, address)) {
                        if (linkCode.linkType == LinkCode.LinkTypes.LOST_LINK) {
                            linkTuple.l_sym_time = timeNow - 1;
                            linkTuple.renewTuple();
                            linkUpdated = true;
                        } else if (linkCode.linkType == LinkCode.LinkTypes.SYM_LINK || linkCode.linkType == LinkCode.LinkTypes.ASYM_LINK) {
                            linkTuple.l_sym_time = message.vTime;
                            linkTuple.l_time = linkTuple.l_sym_time;
                            linkUpdated = true;
                        }
                        if (linkCode.neighborType == LinkCode.NeighborTypes.MPR_NEIGH)
                            recordMPRSelector(message);
                    } else {
                        populateTwoHopNeighborSet(neighborAddress, linkCode, message);
                    }
                }
            }
            linkTuple.l_time = Math.max(linkTuple.l_time, linkTuple.l_asym_time);
            if (linkUpdated)
                linkTupleUpdated(linkTuple, message.willingness);
            else if (linkAdded)
                linkTupleAdded(linkTuple, message.willingness);
        }

        // en länk som förlorar dess symmetri ska ändå annonseras i nätverket, åtminstående varaktigheten av giltighetstiden som finns definerad i HELLO-meddelandet.
        // detta tillåter grannar att upptäcka länkbräckage.
        //linkTuple.l_time = Math.max(linkTuple.l_time, linkTuple.l_asym_time);
        mprSet = new MPRCalculator(neighborSet.values(), twoHopNeighborSet.values(), address).populateAndReturnMPRSet();
        return true;
    }

    private void linkTupleUpdated(LinkTuple linkTuple, Willingness willingness) {
        NeighborTuple neighborTuple = neighborSet.get(linkTuple.l_neighbor_iface_addr);
        if (neighborTuple == null) {
            linkTupleAdded(linkTuple, willingness);
            neighborTuple = neighborSet.get(linkTuple.l_neighbor_iface_addr);
        }
        if (neighborTuple == null)
            throw new IllegalStateException("There must exist a neighbor tuple associated with a at least one link tuple");
        if (Arrays.equals(linkTuple.l_neighbor_iface_addr, neighborTuple.n_neighbor_main_addr)) {
            neighborTuple.status = NeighborTuple.N_status.SYM;
        } else {
            neighborTuple.status = NeighborTuple.N_status.NOT_SYM;
        }

    }

    private void linkTupleAdded(LinkTuple linkTuple, Willingness willingness) {
        NeighborTuple neighborTuple = new NeighborTuple(linkTuple.l_neighbor_iface_addr, willingness);
        if (linkTuple.l_sym_time >= System.currentTimeMillis())
            neighborTuple.status = NeighborTuple.N_status.SYM;
        else
            neighborTuple.status = NeighborTuple.N_status.NOT_SYM;
        neighborSet.put(neighborTuple.n_neighbor_main_addr, neighborTuple);
        removeLinkTimer(linkTuple);
    }

    private void populateTwoHopNeighborSet(short[] neighborAddress, LinkCode linkCode, HelloMessage message) {
        for (LinkTuple linkTuple : linkSet.values()) {
            if (Arrays.equals(linkTuple.l_neighbor_iface_addr, message.originatorAddr)
                    && linkTuple.l_sym_time > System.currentTimeMillis()) {
                if (linkCode.neighborType == LinkCode.NeighborTypes.SYM_NEIGH
                        || linkCode.neighborType == LinkCode.NeighborTypes.MPR_NEIGH) {
                    // En nod är inte sin egen tvåhoppsgranne
                    if (!Arrays.equals(neighborAddress, address)) {
                        TwoHopTuple twoHopTuple;
                        if ((twoHopTuple = twoHopNeighborSet.get(neighborAddress)) == null) {
                            twoHopTuple = new TwoHopTuple(message.originatorAddr, neighborAddress);
                            twoHopNeighborSet.put(twoHopTuple.n_2hop_addr,twoHopTuple);
                            removeTwoHopTimer(twoHopTuple);
                        } else {
                            twoHopTuple.renewTupple();
                        }
                    }
                } else if (linkCode.neighborType == LinkCode.NeighborTypes.NOT_NEIGH) {
                    twoHopNeighborSet.values().removeIf(tuple -> Arrays.equals(tuple.n_neighbor_main_addr, neighborAddress) && Arrays.equals(tuple.n_2hop_addr, neighborAddress));
                }
            }
        }
    }

    public void updateRoutingTable() {
        routingTable.clear();
        for (NeighborTuple tuple : neighborSet.values()) {
            if (tuple.status == NeighborTuple.N_status.SYM) {
                LinkTuple linkTuple;
                if ((linkTuple = linkSet.get(tuple.n_neighbor_main_addr)) != null
                        && linkTuple.l_time >= System.currentTimeMillis()) {
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
        for (TwoHopTuple tuple : twoHopNeighborSet.values()) {
            if (!Arrays.equals(address, tuple.n_2hop_addr) && !neighborSet.containsKey(tuple.n_neighbor_main_addr)) {
                RoutingTuple neighbor = routingTable.get(tuple.n_2hop_addr);
                if (neighbor != null) {
                    RoutingTuple routingTuple = new RoutingTuple(tuple.n_2hop_addr, neighbor.r_next_addr, 2, neighbor.r_iface_addr);
                    routingTable.put(routingTuple.r_dest_addr, routingTuple);
                }
            }
        }
        for (int h = 2;;h++) {
            boolean added = false;
            for (TopologyTuple topologyTuple : topologySet.values()) {
                RoutingTuple tuple = routingTable.get(topologyTuple.t_dest_addr);
                RoutingTuple existing = routingTable.get(topologyTuple.t_last_addr);
                if (tuple == null && existing != null && existing.r_dist == h) {
                    RoutingTuple routingTuple = new RoutingTuple(topologyTuple.t_dest_addr, existing.r_next_addr, h + 1, existing.r_iface_addr);
                    routingTable.put(routingTuple.r_dest_addr, routingTuple);
                    added = true;
                }
            }
            // om nedanstående är sant finns inga fler grannar som ligger det antal hop som är specificerat i variabeln h
            if (!added)
                break;
        }
    }

    private void detectNeighborLoss(LinkTuple tuple) {
        synchronized (timer) {
            timer.schedule(createNeighborLossDetectionTask(tuple), tuple.l_asym_time);
        }
    }

    private void recordMPRSelector(HelloMessage message) {
        MPRSelectorTuple tuple;
        synchronized (mprSelectorSet) {
            if ((tuple = mprSelectorSet.get(message.originatorAddr)) == null) {
                tuple = new MPRSelectorTuple(message.originatorAddr);
                mprSelectorSet.put(message.originatorAddr, tuple);
                removeMPRSelectorTimer(tuple);
            } else {
                tuple.renewTupple();
            }
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
            twoHopNeighborSet.remove(neighborAddress);
        }
        //linkTupleUpdated(linkSet.get(neighborAddress), Willingness.WILL_DEFAULT);
        mprSet = new MPRCalculator(neighborSet.values(), twoHopNeighborSet.values(), address).populateAndReturnMPRSet();
        updateRoutingTable();
    }

    private void removeMPRSelectorTimer(MPRSelectorTuple tuple) {
        synchronized (timer) {
            timer.schedule(createMPRSelectorRemoveTask(tuple), tuple.get_time() - System.currentTimeMillis());
        }
    }

    private void removeTwoHopTimer(TwoHopTuple tuple) {
        synchronized (timer) {
            timer.schedule(createTwoHopTimer(tuple), tuple.get_time() - System.currentTimeMillis());
        }
    }

    private void removeLinkTimer(LinkTuple linkTuple) {
        synchronized (timer) {
            timer.schedule(createLinkTupleRemoveTask(linkTuple), linkTuple.l_time - System.currentTimeMillis());
        }
    }

    // Specifikationerna för meddelandetypen bestämmer hur paketet ska vidarebefodras
    // HELLO-paket vidarbefodras ej, TC-meddelande vidarbefodras av MPR-noder enligt standardalgoritm
    private void forwardAccordingToMsgType(OLSRPacket packet) {
    }

    private long calculateJitter() {
        return (long)(Math.random() * MAX_JITTER_MS);
    }

    private void dropPacket(Packet packet) {
        if (Constants.LOG_ACTIVE)
            System.out.println("Packet dropped: " + packet.toString());
        PacketLocator.reportPacketDropped(this);
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

    public void setLocation(Location location) {
        this.location = location;
    }
    public void turnOn() {
        this.active = true;
        if (!thread.isAlive())
            thread.start();
    }

    public TimerTask createLinkTupleRemoveTask(LinkTuple tuple) {
        return new TimerTask() {
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                // fältet l_time kan ha uppdateras mellan tiden då detta TimerTask-objektet skapades och när metoden run() anropas
                if (tuple.l_time > timeNow)
                    timer.schedule(createLinkTupleRemoveTask(tuple), tuple.l_time - timeNow);
                else {
                    synchronized (linkSet) {
                        linkSet.remove(tuple.l_neighbor_iface_addr);
                    }
                    synchronized (neighborSet) {
                        neighborSet.remove(tuple.l_neighbor_iface_addr);
                    }
                }
            }
        };
    }

    private TimerTask createTCTuppleRemoveTask(TopologyTuple tuple) {
        return new TimerTask() {
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                if (tuple.get_time() > timeNow)
                    timer.schedule(createTCTuppleRemoveTask(tuple), tuple.get_time() - timeNow);
                else {
                    synchronized (topologySet) {
                        topologySet.remove(tuple.t_dest_addr);
                    }
                }
            }
        };
    }


    private TimerTask createMPRSelectorRemoveTask(MPRSelectorTuple tuple) {
        return new TimerTask() {
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                if (tuple.get_time() > timeNow)
                    timer.schedule(createMPRSelectorRemoveTask(tuple), tuple.get_time() - timeNow);
                else {
                    synchronized (mprSelectorSet) {
                        mprSelectorSet.remove(tuple.ms_main_addr);
                    }
                }
            }
        };
    }

    private TimerTask createTwoHopTimer(TwoHopTuple tuple) {
        return new TimerTask() {
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                // fältet l_time kan ha uppdateras mellan tiden då detta TimerTask-objektet skapades och när metoden run() anropas
                if (tuple.get_time() > timeNow)
                    timer.schedule(createTwoHopTimer(tuple), tuple.get_time() - timeNow);
                else {
                    lossOfNeighbor(tuple.n_2hop_addr);
                }
            }
        };
    }

    private TimerTask createNeighborLossDetectionTask(LinkTuple tuple) {
        return new TimerTask() {
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                if (tuple.l_asym_time > timeNow)
                    timer.schedule(createNeighborLossDetectionTask(tuple), tuple.l_asym_time - timeNow);
                else
                    lossOfNeighbor(tuple.l_neighbor_iface_addr);
            }
        };
    }
}