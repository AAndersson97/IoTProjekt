package network;

import java.nio.channels.Pipe;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static network.Constants.Network.BROADCAST;
import static network.Constants.Protocol.*;

public class Node implements Comparator<Node>, Runnable {
    private Thread thread;
    private final Timer timer;
    private final short[] address;
    private Location location;
    private int seqNum; // gränsnittets sekvensnummer för att göra det möjligt för grannar att sortera mellan paket
    private int ANSN; // ett sekvensnummer som ökar med 1 varje gång mängden av grannar i "neighborSet" uppdateras.
    private final ArrayList<DuplicateTuple> duplicateSet; // Innehåller info om mottagna paket för att undvika att samma paket vidarebefodras/bearbetas flera gånger om
    private boolean active; // true om nodens tråd är aktiv
    private Willingness willingness;
    protected final ArrayList<NeighborTuple> neighborSet; // nyckeln är grannens ip-adress
    private short[][] mprSet; // lista över grannar som valts som MPR-nod
    protected final ArrayList<LinkTuple> linkSet; // nyckeln är grannens ip-adress
    protected final ArrayList<TwoHopTuple> twoHopNeighborSet; // nykeln är tvåhoppsgrannen
    protected final ArrayList<MPRSelectorTuple> mprSelectorSet; // innehåller info om grannar som vald denna nod till att bli en MPR-nod
    private final ArrayList<TopologyTuple> topologySet; // nyckeln är destinationsadressen
    private final Transmission transmission;
    private final ConcurrentLinkedQueue<Packet> buffer; // tillfällig lagring av paket som inte än har bearbetas
    protected final List<RoutingTuple> routingTable; // Nyckel: destination, värde: RoutingTuple

    private static int count = 0;

    public Location getLocation() {
        return location;
    }

    public Node() {
        buffer = new ConcurrentLinkedQueue<>();
        routingTable = Collections.synchronizedList(new ArrayList<RoutingTuple>());
        active = true;
        address = AddressGenerator.generateAddress();
        thread = new Thread(this);
        transmission = new Transmission(Transmission.SignalStrength.VERYGOOD);
        timer = new Timer();
        duplicateSet = new ArrayList<>();
        neighborSet = new ArrayList<>();
        twoHopNeighborSet = new ArrayList<>();
        mprSelectorSet = new ArrayList<>();
        topologySet = new ArrayList<>();
        linkSet = new ArrayList<>();
        willingness = Willingness.WILL_DEFAULT;
        mprSet = new short[0][];
        if (this.getClass() == Node.class) {
            location = LocationManager.getInstance().getLocation(this);
            // Innan tråden kan startas i SybilNode- och i AttackNode-objekten måste objektet skapas först
            thread.start();
        }
        if (this.getClass() != SybilNode.class) {
            Network.registerNode(this);
        }
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
            PacketLocator.reportPacketTransport(packet.ipHeader.sourceAddress, address, packet);
            processAccordingToMsgType(olsrPacket);
            if (olsrPacket.message instanceof TCMessage && !mprSelectorSet.isEmpty())
                prepareForwarding(packet);
        } else {
            PacketLocator.reportPacketTransport(packet.wifiMacHeader.sender, address, packet);
            if (!Arrays.equals(packet.wifiMacHeader.receiver, address))
                dropPacket(packet);
            else if (packet.ipHeader.getTimeToLive() > 0) {
                DuplicateTuple duplicateTuple = findDuplicateTuple(packet.ipHeader.sourceAddress);
                if (duplicateTuple != null && duplicateTuple.d_seq_num == packet.olsrHeader.packetSeqNum) {
                    duplicateTuple.renewTupple();
                    dropPacket(packet);
                }
                else {
                    if (Arrays.equals(packet.ipHeader.destinationAddress, address) && packet.udpHeader.destinationPort == TFTP_PORT) {
                        processTFTPPacket((TFTPPacket) packet);
                    } else {
                        prepareForwarding(packet);
                    }
                }

            }

        }
    }

    private void processTFTPPacket(TFTPPacket packet) {
        System.out.println("Message received!");
    }

    /*private void updateDuplicateSet(OLSRMessage message) {
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
    }*/

    private void sendTCPacket() {
        int count = 0;
        short[][] advertisedNMA = new short[mprSelectorSet.size()][];
        for (MPRSelectorTuple selectorTuple : mprSelectorSet)
            advertisedNMA[count++] = selectorTuple.ms_main_addr;
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
            for (LinkTuple tuple : linkSet) {
                if (tuple.l_time >= timeNow) {
                    LinkCode linkCode = createLinkCode(tuple);
                    if (linkCode == null)
                        continue;
                    neighbors.computeIfAbsent(linkCode, k -> new ArrayList<>());
                    neighbors.get(linkCode).add(tuple.l_neighbor_iface_addr);
                }
            }
            for (NeighborTuple tuple : neighborSet) {
                if (findLinkTuple(tuple.n_neighbor_main_addr) == null) {
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
        if (Arrays.equals(address, new short[]{110,0,0,2})) {
            System.out.println("Routing table size: " + routingTable.size() + " num of nodes on network: " + Network.getNumOfNodes() + " two hops: " + twoHopNeighborSet.size());
        }
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
        else if ((neighborTuple = findNeighborTuple(tuple.l_neighbor_iface_addr)) != null) {
            if (neighborTuple.status == NeighborTuple.N_status.SYM)
                neighborType = LinkCode.NeighborTypes.SYM_NEIGH;
            else
                neighborType = LinkCode.NeighborTypes.NOT_NEIGH;
        } else
            return null;

        return new LinkCode(linkType, neighborType);
    }

    private NeighborTuple findNeighborTuple(short[] neighbor_addr) {
        for (NeighborTuple neighborTuple : neighborSet) {
            if (Arrays.equals(neighborTuple.n_neighbor_main_addr, neighbor_addr))
                return neighborTuple;
        }
        return null;
    }

    private short[] findNextHop(short[] destination) {
        ArrayList<RoutingTuple> tuples = new ArrayList<>();
        int minHopp = 0;
        NeighborTuple shortestPath;
        return null;
    }

    public LinkTuple findLinkTuple(short[] neighbor) {
        for (LinkTuple linkTuple : linkSet)
            if (Arrays.equals(linkTuple.l_neighbor_iface_addr, neighbor))
                return linkTuple;

        return null;
    }

    private TopologyTuple findTopologyTuple(short[] originatorAddr) {
        for (TopologyTuple topologyTuple : topologySet) {
            if (Arrays.equals(topologyTuple.t_dest_addr, originatorAddr)) {
                return topologyTuple;
            }
        }
        return null;
    }

    private boolean mprSetContains(short[] address) {
        for (short[] mpr : mprSet)
            if (Arrays.equals(address, mpr))
                return true;

        return false;
    }

    private MPRSelectorTuple findMPRSelector(short[] addr) {
        for (MPRSelectorTuple mprSelectorTuple : mprSelectorSet) {
            if (Arrays.equals(mprSelectorTuple.ms_main_addr, addr)) {
                return mprSelectorTuple;
            }
        }
        return null;
    }

    /**
     * Metoden utför de sista kontrollerna innan paketet verkligen vidarebefodras
     * @param packet Packet som ska vidarebefodras
     */
    private void prepareForwarding(Packet packet) {
        // enbart grannars paket ska vidarebefordras
        if (findNeighborTuple(packet.ipHeader.sourceAddress) != null || Arrays.equals(packet.ipHeader.sourceAddress, address)) {
            // Nedanstående villkor är sant om avsändaradressen tillhör en nod som är en MPR selector till denna nod
            //if (mprSelectorSet.containsKey(packet.ipHeader.sourceAddress))
            doForward(packet);
            DuplicateTuple duplicateTuple = null;
            if ((duplicateTuple = findDuplicateTuple(packet.ipHeader.sourceAddress)) == null) {
                duplicateTuple = new DuplicateTuple(packet.ipHeader.sourceAddress, address, packet.olsrHeader.packetSeqNum);
                duplicateSet.add(duplicateTuple);
                removeDuplicateTupleTimer(duplicateTuple);
            }
        }

    }

    private void removeDuplicateTupleTimer(DuplicateTuple duplicateTuple) {
        synchronized (timer) {
            timer.schedule(createDuplicateTupleRemoveTask(duplicateTuple), duplicateTuple.get_time() - System.currentTimeMillis());
        }
    }

    private void doForward(Packet packet) {
        if (Constants.LOG_ACTIVE)
            System.out.println("Forward packet: " + packet.toString());
        try {
            Thread.sleep(calculateJitter());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        incrementSeqNum();
        RoutingTuple nextHop = findRoutingTuple(packet.ipHeader.destinationAddress);
        packet.ipHeader.decrementTTL();
        if (nextHop == null);
            //Network.sendPacket(this, BROADCAST, packet);
        else {
            Network.sendPacket(this, nextHop.r_next_addr, packet);
        }
    }

    public RoutingTuple findRoutingTuple(short[] destination) {
        for (RoutingTuple tuple : routingTable) {
            if (Arrays.equals(tuple.r_dest_addr, destination))
                return tuple;
        }
        return null;
    }

    private void processAccordingToMsgType(OLSRPacket packet) {
            switch (packet.message.msgType) {
                case HELLO_MESSAGE:
                    processHelloMessage((HelloMessage) packet.message);
                    break;
                case TC_MESSAGE:
                    if (isADuplicate(packet.message) || findNeighborTuple(packet.ipHeader.sourceAddress) == null) {
                        dropPacket(packet);
                        break;
                    }
                    processTCMessage((TCMessage) packet.message);
                    break;
            }
    }

    /**
     * Metoden kontrollera i duplicateSets för att se om samma meddelandet redan skickats förut.
     * @return true om meddelandet är en dubblett
     */
    private boolean isADuplicate(OLSRMessage message) {
        DuplicateTuple duplicateTuple;
        boolean isADuplicate = false;
        // Kontrollerar om ursprungsadressen finns i samlingen över Duplicate Set
        if ((duplicateTuple = findDuplicateTuple(message.originatorAddr)) != null) {
            // om nedanstående villlkor är sant ska paketet ej bearbetas men möjligtvis ska den skickas vidare
            isADuplicate = duplicateTuple.d_seq_num == message.msgSeqNum;
        } if (isADuplicate){}
            //updateDuplicateSet(message);
        return isADuplicate;
    }

    private DuplicateTuple findDuplicateTuple(short[] originatorAddr) {
        for (DuplicateTuple duplicateTuple : duplicateSet) {
            if (Arrays.equals(duplicateTuple.d_addr, originatorAddr))
                return duplicateTuple;
        }
        return null;
    }

    private TwoHopTuple findTwoHopTuple(short[] neighborAddress) {
        for (TwoHopTuple twoHopTuple : twoHopNeighborSet) {
            if (Arrays.equals(twoHopTuple.n_2hop_addr, neighborAddress)) {
                return twoHopTuple;
            }
        }
        return null;
    }

    private void processTCMessage(TCMessage message) {
        long timeNow = System.currentTimeMillis();
        int topologySize = topologySet.size();
        if (findLinkTuple(message.originatorAddr) != null && message.vTime >= timeNow) {
            TopologyTuple topologyTuple;
            synchronized (topologySet) {
                if ((topologyTuple = findTopologyTuple(message.originatorAddr)) != null) {
                    if (topologyTuple.t_seq < message.ANSN) {
                        // Tar bort tuppel som innehåller äldre information än informationen i meddelandet
                        topologySet.remove(topologyTuple);
                    } else {
                        return;
                    }
                }
                for (short[] neighbor : message.advertisedNMA) {
                    if ((topologyTuple = findTopologyTuple(message.originatorAddr)) != null) {
                        topologyTuple.renewTupple();
                    } else {
                        topologyTuple = new TopologyTuple(neighbor, message.originatorAddr, ANSN);
                        topologySet.add(topologyTuple);
                        removeTCTuppleTimer(topologyTuple);
                    }
                }
            }
        }
        // Om ändringar gjorts i topology set ska routingtabellen uppdateras
        if (topologySet.size() != topologySize)
            updateRoutingTable();
    }

    private void removeTCTuppleTimer(TopologyTuple topologyTuple) {
        synchronized (timer) {
            timer.schedule(createTCTuppleRemoveTask(topologyTuple), topologyTuple.get_time() - System.currentTimeMillis());
        }
    }

    private void processHelloMessage(HelloMessage message) {
        LinkTuple linkTuple;
        long timeNow = System.currentTimeMillis();
        boolean linkUpdated = false, linkAdded = false;
        // Bearbeta inte paket vars giltighetstid har gått ut
        if (message.vTime < timeNow)
            return;
        synchronized (this) {
            if ((linkTuple = findLinkTuple(message.originatorAddr)) == null) {
                linkTuple = new LinkTuple(address, message.originatorAddr, timeNow - 1, message.vTime);
                linkSet.add(linkTuple);
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
                    }
                    populateTwoHopNeighborSet(neighborAddress, linkCode, message);
                }
            }
            linkTuple.l_time = Math.max(linkTuple.l_time, linkTuple.l_asym_time);
            if (linkUpdated)
                linkTupleUpdated(linkTuple, message.willingness);
            else if (linkAdded)
                linkTupleAdded(linkTuple, message.willingness);
            if (linkUpdated || linkAdded)
                updateRoutingTable();
        }

        // en länk som förlorar dess symmetri ska ändå annonseras i nätverket, åtminstående varaktigheten av giltighetstiden som finns definerad i HELLO-meddelandet.
        // detta tillåter grannar att upptäcka länkbräckage.
        //linkTuple.l_time = Math.max(linkTuple.l_time, linkTuple.l_asym_time);
        count++;
        mprSet = new MPRCalculator(neighborSet, twoHopNeighborSet, address).populateAndReturnMPRSet();
    }

    private void linkTupleUpdated(LinkTuple linkTuple, Willingness willingness) {
        NeighborTuple neighborTuple = findNeighborTuple(linkTuple.l_neighbor_iface_addr);
        if (neighborTuple == null) {
            linkTupleAdded(linkTuple, willingness);
            neighborTuple = findNeighborTuple(linkTuple.l_neighbor_iface_addr);
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
        neighborSet.add(neighborTuple);
        removeLinkTimer(linkTuple);
    }

    private void populateTwoHopNeighborSet(short[] neighborAddress, LinkCode linkCode, HelloMessage message) {
        for (LinkTuple linkTuple : linkSet) {
            if (Arrays.equals(linkTuple.l_neighbor_iface_addr, message.originatorAddr)
                    && linkTuple.l_sym_time > System.currentTimeMillis()) {
                if (linkCode.neighborType == LinkCode.NeighborTypes.SYM_NEIGH
                        || linkCode.neighborType == LinkCode.NeighborTypes.MPR_NEIGH) {
                    // En nod är inte sin egen tvåhoppsgranne
                    if (!Arrays.equals(neighborAddress, address)) {
                        TwoHopTuple twoHopTuple;
                        if ((twoHopTuple = findTwoHopTuple(neighborAddress)) == null) {
                            twoHopTuple = new TwoHopTuple(message.originatorAddr, neighborAddress);
                            twoHopNeighborSet.add(twoHopTuple);
                            removeTwoHopTimer(twoHopTuple);
                        } else {
                            twoHopTuple.renewTupple();
                        }
                        //updateRoutingTable();
                    }
                } else if (linkCode.neighborType == LinkCode.NeighborTypes.NOT_NEIGH) {
                    twoHopNeighborSet.removeIf(tuple -> Arrays.equals(tuple.n_neighbor_main_addr, message.originatorAddr) && Arrays.equals(tuple.n_2hop_addr, neighborAddress));
                }
            }
        }
    }

    public void updateRoutingTable() {
        routingTable.clear();
        for (NeighborTuple tuple : neighborSet) {
            if (tuple.status == NeighborTuple.N_status.SYM) {
                LinkTuple linkTuple;
                if ((linkTuple = findLinkTuple(tuple.n_neighbor_main_addr)) != null
                        && linkTuple.l_time >= System.currentTimeMillis()) {
                    RoutingTuple routingTuple;
                    if (findRoutingTuple(tuple.n_neighbor_main_addr) != null) {
                        routingTuple = new RoutingTuple(linkTuple.l_neighbor_iface_addr, linkTuple.l_neighbor_iface_addr, 1, linkTuple.l_local_iface_addr);
                    } else {
                        routingTuple = new RoutingTuple(tuple.n_neighbor_main_addr, linkTuple.l_neighbor_iface_addr, 1, linkTuple.l_local_iface_addr);
                    }
                    routingTable.add(routingTuple);
                }
            }
        }
        for (TwoHopTuple tuple : twoHopNeighborSet) {
            if (!Arrays.equals(address, tuple.n_2hop_addr) && findNeighborTuple(tuple.n_neighbor_main_addr) != null) {
                RoutingTuple neighbor = findRoutingTuple(tuple.n_neighbor_main_addr);
                if (neighbor != null && findRoutingTuple(tuple.n_2hop_addr) == null) {
                    RoutingTuple routingTuple = new RoutingTuple(tuple.n_2hop_addr, neighbor.r_next_addr, 2, neighbor.r_iface_addr);
                    routingTable.add(routingTuple);
                }
            }
        }
        for (int h = 2;; h++) {
            boolean added = false;
            for (TopologyTuple topologyTuple : topologySet) {
                RoutingTuple tuple = findRoutingTuple(topologyTuple.t_dest_addr);
                RoutingTuple existing = findRoutingTuple(topologyTuple.t_last_addr);
                if (tuple == null && existing != null && existing.r_dist == h) {
                    RoutingTuple routingTuple = new RoutingTuple(topologyTuple.t_dest_addr, existing.r_next_addr, h + 1, existing.r_iface_addr);
                    routingTable.add(routingTuple);
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
            if ((tuple = findMPRSelector(message.originatorAddr)) == null) {
                tuple = new MPRSelectorTuple(message.originatorAddr);
                mprSelectorSet.add(tuple);
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
            mprSelectorSet.removeIf((mprSelectorTuple -> Arrays.equals(mprSelectorTuple.ms_main_addr, neighborAddress)));
        }
        synchronized (twoHopNeighborSet) {
            twoHopNeighborSet.removeIf(twoHopTuple ->  Arrays.equals(twoHopTuple.n_2hop_addr, neighborAddress));
        }
        //linkTupleUpdated(linkSet.get(neighborAddress), Willingness.WILL_DEFAULT);
        mprSet = new MPRCalculator(neighborSet, twoHopNeighborSet, address).populateAndReturnMPRSet();
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

    protected void dropPacket(Packet packet) {
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
                        linkSet.remove(tuple);
                    }
                    synchronized (neighborSet) {
                        neighborSet.remove(findNeighborTuple(tuple.l_neighbor_iface_addr));
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
                        topologySet.remove(tuple);
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
                        mprSelectorSet.remove(tuple);
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

    private TimerTask createDuplicateTupleRemoveTask(DuplicateTuple duplicateTuple) {
        return new TimerTask() {
            @Override
            public void run() {
                if (duplicateTuple.get_time() > System.currentTimeMillis())
                    timer.schedule(createDuplicateTupleRemoveTask(duplicateTuple), duplicateTuple.get_time() - System.currentTimeMillis());
                else {
                    synchronized (duplicateSet) {
                        duplicateSet.remove(duplicateTuple);
                    }
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