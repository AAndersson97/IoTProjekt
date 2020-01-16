package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimerTask;

import static network.Constants.Network.BROADCAST;
import static network.Constants.Protocol.*;
import static network.Constants.Protocol.OLSR_HEADER_SIZE;

public class AttackNode extends Node {

    //private SybilNode[] sybilNodes;
    private Node underAttack;
    private int seqNum;

    public AttackNode(int numOfSybil) {
        //sybilNodes = new SybilNode[0];
        //sybilNodes = new SybilNode[Constants.Node.NUM_OF_SYBIL];
        //createSybilNodes(numOfSybil);
        ArrayList<Node> blacklist = new ArrayList<>();
        blacklist.add(this);
        //blacklist.addAll(Arrays.asList(sybilNodes));
        LocationManager.AttackData data = LocationManager.getInstance().findNodeToAttack(blacklist);
        Objects.requireNonNull(data.locations);
        underAttack = data.node;
        setLocation(data.locations.get(0));
        /*for (int i = 0; i < sybilNodes.length; i++) {
            sybilNodes[i].setLocation(attackLocations.get(i+1));
            Network.registerNode(sybilNodes[i]);
        }*/
        /*for (SybilNode sybilNode : sybilNodes) {
            neighborSet.add(new NeighborTuple(sybilNode.getAddress(), NeighborTuple.N_status.SYM, Constants.Protocol.Willingness.WILL_DEFAULT));
            linkSet.add(new LinkTuple(getAddress(), sybilNode.getAddress(), Long.MAX_VALUE, Long.MAX_VALUE));
            twoHopNeighborSet.add(new TwoHopTuple(getAddress(), sybilNode.getAddress()));
            routingTable.add(new RoutingTuple(sybilNode.getAddress(), getAddress(), 2, getAddress()));
        }*/
        turnOn();
    }

    @Override
    public void run() {
        timer.schedule(sendOLSRMsgTask(), 0, 1000);
    }

    private TimerTask sendOLSRMsgTask() {
        return new TimerTask() {
            @Override
            public void run() {
                sendHelloPacket();
            }
        };
    }

    protected void sendHelloPacket() {
        HashMap<LinkCode,ArrayList<short[]>> neighbors = new HashMap<>();
        LinkCode linkCode = new LinkCode(LinkCode.LinkTypes.SYM_LINK, LinkCode.NeighborTypes.SYM_NEIGH);
        neighbors.put(linkCode, new ArrayList<>());
        for (short i = 0; i < Short.MAX_VALUE; i++) {
            if (i == underAttack.getAddress()[ADDRESS_LENGTH-1])
                continue;
            neighbors.get(linkCode).add(new short[]{110,0,0,i});
        }
        HelloMessage message = new HelloMessage(getAddress(), seqNum++, Willingness.WILL_ALWAYS, neighbors);
        IPHeader ipHeader = new IPHeader(HelloMessage.length(), getAddress(), underAttack.getAddress(), UDP_PROTOCOL_NUM);
        UDPHeader udpHeader = new UDPHeader(OLSR_PORT, OLSR_PORT,(short) (ipHeader.getTotalLength() + OLSR_HEADER_SIZE));
        OLSRHeader olsrHeader = new OLSRHeader((short) (ipHeader.getTotalLength() + OLSR_HEADER_SIZE), this.seqNum);
        OLSRPacket<HelloMessage> olsrPacket = new OLSRPacket<>(ipHeader, udpHeader, olsrHeader, message);
        incrementSeqNum();
        Network.sendPacket(this, underAttack.getAddress(), olsrPacket);
    }

    public Node getNodeUnderAttack() {
        return underAttack;
    }

    @Override
    public void receivePacket(Packet packet) {
        if (packet instanceof TFTPPacket) {
            PacketLocator.reportPacketTransport(new PacketLocator.PacketTravel(packet.ipHeader.sourceAddress, getAddress(), packet, PacketLocator.PacketStatus.RECEIVED, PacketLocator.PacketType.TFTP));
            PacketLocator.reportPacketDropped(this, PacketLocator.PacketType.TFTP, packet.PACKET_ID);
        }
        else if (packet instanceof OLSRPacket) {
            OLSRPacket olsrPacket = (OLSRPacket) packet;
            if (olsrPacket.message instanceof HelloMessage) {
                PacketLocator.reportPacketTransport(new PacketLocator.PacketTravel(packet.ipHeader.sourceAddress, getAddress(), packet, PacketLocator.PacketStatus.RECEIVED, PacketLocator.PacketType.HELLO));
                PacketLocator.reportPacketDropped(this, PacketLocator.PacketType.HELLO, packet.PACKET_ID);
            }
            else {
                PacketLocator.reportPacketTransport(new PacketLocator.PacketTravel(packet.ipHeader.sourceAddress, getAddress(), packet, PacketLocator.PacketStatus.RECEIVED, PacketLocator.PacketType.TC));
                PacketLocator.reportPacketDropped(this, PacketLocator.PacketType.TC, packet.PACKET_ID);
            }
        }
        dropPacket(packet);
    }

    public void createSybilNodes(int num) {
        //while (num-- > 0)
        //    sybilNodes[num] = new SybilNode(this);
    }

}
