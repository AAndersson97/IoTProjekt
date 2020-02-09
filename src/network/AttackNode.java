package network;

import java.util.*;

import static network.Constants.Network.BROADCAST;
import static network.Constants.Protocol.*;
import static network.Constants.Protocol.OLSR_HEADER_SIZE;

public class AttackNode extends Node {
    private SybilNode[] sybilNodes;
    private Node underAttack;

    public AttackNode(int numOfSybil, AttackType attackType) {
        sybilNodes = new SybilNode[0];
        sybilNodes = new SybilNode[Constants.Node.NUM_OF_SYBIL];
        createSybilNodes(numOfSybil);
        initializeAttack();
        if (attackType == AttackType.ROUTING)
            timer.schedule(sendOLSRMsgTask(), 0, 2000);
        LogWriter.getInstance().writeToFile(getAddressString(), "attacknode.txt");
    }

    private void initializeAttack() {
        ArrayList<Node> blacklist = new ArrayList<>();
        blacklist.add(this);
        blacklist.addAll(Arrays.asList(sybilNodes));
        LocationManager.AttackData data = LocationManager.getInstance().findNodeToAttack(blacklist);
        Objects.requireNonNull(data.locations);
        underAttack = data.node;
        setLocation(data.locations.get(0));
        for (int i = 0; i < sybilNodes.length; i++)
            sybilNodes[i].setLocation(data.locations.get(i+1));
    }

    /**
     * Vid en routingattack skickar attacknoden ut hello-paket oftare än vanliga noder, vilket ökar sannolikheten att de
     * vanliga noderna ska uppdatera sina routingtabeller utifrån information från attacknodens hellopaket och på sådant sätt dirigera paket via attacknoden.
     * @return
     */
    private TimerTask sendOLSRMsgTask() {
        return new TimerTask() {
            @Override
            public void run() {
                sendHelloPacket();
            }
        };
    }

    @Override
    protected void sendHelloPacket() {
        HashMap<LinkCode, ArrayList<short[]>> neighbors = new HashMap<>(); // grannar som har en länk till denna nod, info som används för att senare hitta grannar utan länk till denna nod
        LinkCode linkCode = new LinkCode(LinkCode.LinkTypes.SYM_LINK, LinkCode.NeighborTypes.SYM_NEIGH);
        neighbors.put(linkCode, new ArrayList<>());
        for (SybilNode sybilNode : sybilNodes)
            neighbors.get(linkCode).add(sybilNode.getAddress());
        neighbors.get(linkCode).add(underAttack.getAddress());
        HelloMessage message = new HelloMessage(getAddress(), seqNum, Willingness.WILL_DEFAULT, neighbors);
        IPHeader ipHeader = new IPHeader(HelloMessage.length(), getAddress(), BROADCAST, UDP_PROTOCOL_NUM);
        UDPHeader udpHeader = new UDPHeader(OLSR_PORT, OLSR_PORT,(short) (ipHeader.getTotalLength() + OLSR_HEADER_SIZE));
        OLSRHeader olsrHeader = new OLSRHeader((short) (ipHeader.getTotalLength() + OLSR_HEADER_SIZE), this.seqNum);
        OLSRPacket<HelloMessage> olsrPacket = new OLSRPacket<>(ipHeader, udpHeader, olsrHeader, message);
        incrementSeqNum();
        Network.sendPacket(this, BROADCAST, olsrPacket);
    }

    private TFTPPacket generatePacket(short[] sender) {
        IPHeader ipHeader = new IPHeader(0, underAttack.getAddress(), getAddress(), UDP_PROTOCOL_NUM);
        UDPHeader udpHeader = new UDPHeader(TFTP_PORT, TFTP_PORT, (short) ipHeader.getLength());
        OLSRHeader olsrHeader = new OLSRHeader(ipHeader.getLength() + udpHeader.length,seqNum++);
        TFTPPacket packet = new TFTPPacket(ipHeader, udpHeader, olsrHeader, TFTPPacket.TFTPOpcode.DATA, "");
        packet.setWifiMacHeader(new WifiMacHeader(underAttack.getAddress(), getAddress()));
        WifiMacTrailer wifiMacTrailer = new WifiMacTrailer();
        packet.setWifiMacTrailer(wifiMacTrailer);
        wifiMacTrailer.setCheckSum(packet.toBytes());
        return packet;
    }

    public void shutdown() {
        active = false;
        timer.cancel();
        Network.removeNode(this);
        for (SybilNode sybilNode : sybilNodes)
            Network.removeNode(sybilNode);
        synchronized (buffer) {
            buffer.notifyAll();
        }
    }

    public Node getNodeUnderAttack() {
        return underAttack;
    }

    public SybilNode[] getSybilNodes() {
        return sybilNodes;
    }

    public int[] getSybilNodeIds() {
        int[] ids = new int[sybilNodes.length];
        for (int i = 0; i < ids.length; i++)
            ids[i] = sybilNodes[i].getAddress()[3];
        return ids;
    }

    @Override
    public void receivePacket(Packet packet) {
        if (packet instanceof TFTPPacket) {
            PacketLocator.reportPacketTransport(new PacketLocator.PacketTravel(packet.wifiMacHeader.sender, address, packet, PacketLocator.PacketStatus.RECEIVED, PacketLocator.PacketType.TFTP));
        }
        dropPacket(packet);
    }

    private void createSybilNodes(int num) {
        while (num-- > 0)
            sybilNodes[num] = new SybilNode(this);
    }

    @Override
    public MisbehaviourVoting.Vote requestVote() {
        return MisbehaviourVoting.Vote.AGREE;
    }

    @Override
    public void disconnect() {
        active = false;
        timer.purge();
        sybilNodes = null;
    }
}
