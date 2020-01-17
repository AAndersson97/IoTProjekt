package network;

import java.net.InetAddress;
import java.util.*;

import static network.Constants.Network.BROADCAST;
import static network.Constants.Protocol.*;
import static network.Constants.Protocol.OLSR_HEADER_SIZE;

public class AttackNode extends Node {

    private SybilNode[] sybilNodes;
    private Node underAttack;

    public AttackNode(int numOfSybil) {
        sybilNodes = new SybilNode[0];
        sybilNodes = new SybilNode[Constants.Node.NUM_OF_SYBIL];
        createSybilNodes(numOfSybil);
        ArrayList<Node> blacklist = new ArrayList<>();
        blacklist.add(this);
        blacklist.addAll(Arrays.asList(sybilNodes));
        LocationManager.AttackData data = LocationManager.getInstance().findNodeToAttack(blacklist);
        Objects.requireNonNull(data.locations);
        underAttack = data.node;
        setLocation(data.locations.get(0));
        for (int i = 0; i < sybilNodes.length; i++)
            sybilNodes[i].setLocation(data.locations.get(i+1));
        turnOn();
    }

    @Override
    public void run() {
        timer.schedule(sendSpamMsgTask(this), 2000, 75);
        timer.schedule(sendOLSRMsgTask(), 0, 2000);
    }

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
        long timeNow = System.currentTimeMillis();
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

    private TimerTask sendSpamMsgTask(AttackNode attackNode) {
        return new TimerTask() {
            @Override
            public void run() {
                TFTPPacket packet = generatePacket(getAddress());
                Network.sendPacket(attackNode, underAttack.getAddress(), packet);
                Network.sendPacket(attackNode, underAttack.getAddress(), generatePacket(sybilNodes[0].getAddress()));
                Network.sendPacket(attackNode, underAttack.getAddress(), generatePacket(sybilNodes[1].getAddress()));
                Network.sendPacket(attackNode, underAttack.getAddress(), generatePacket(sybilNodes[2].getAddress()));
            }
        };
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

    public Node getNodeUnderAttack() {
        return underAttack;
    }

    @Override
    public void receivePacket(Packet packet) {
        if (packet instanceof TFTPPacket) {
            PacketLocator.reportPacketTransport(new PacketLocator.PacketTravel(packet.ipHeader.sourceAddress, getAddress(), packet, PacketLocator.PacketStatus.RECEIVED, PacketLocator.PacketType.TFTP));
            packet.PACKET_ID+=25;
            Network.sendPacket(this, underAttack.getAddress(), packet);
        }
        dropPacket(packet);
    }

    private void createSybilNodes(int num) {
        while (num-- > 0)
            sybilNodes[num] = new SybilNode(this);
    }

    public SybilNode[] getSybilNodes() {
        return sybilNodes;
    }

    @Override
    public void turnOff() {
        active = false;
        timer.cancel();
    }
}
