package network;

import network.tuples.RoutingTuple;
import network.tuples.TopologyTuple;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PrintMethods {
    public synchronized static void printRoutingTable(List<RoutingTuple> routingTable, Node node) {
        Objects.requireNonNull(routingTable);
        System.out.println("Node address: " + Arrays.toString(node.getAddress()));
        for (RoutingTuple tuple : routingTable) {
            System.out.println(tuple.toString());
        }
    }
    public synchronized static void printPacketInfo(Packet packet, Node node) {
        Objects.requireNonNull(packet);
        if (packet instanceof TFTPPacket) {
            TFTPPacket tftpPacket = (TFTPPacket) packet;
            if (node != null)
                System.out.println("Current node: " + Arrays.toString(node.getAddress()));
            System.out.println(tftpPacket.ipHeader.toString() + "\n" + "Receiver: " + Arrays.toString(tftpPacket.wifiMacHeader.receiver) +
                    " Sender: " + Arrays.toString(tftpPacket.wifiMacHeader.sender));
        }
    }
    public synchronized static void printMPRSet(short[][] mprSet, Node node) {
        Objects.requireNonNull(mprSet);
        if (node != null)
            System.out.println("Current node: " + Arrays.toString(node.getAddress()));
        System.out.print("Mpr Set: ");
        for (short[] addr : mprSet) {
            System.out.print(Arrays.toString(addr) + " ");
        }
    }
    public synchronized static void printTopologyTuples(List<TopologyTuple> set, Node node) {
        Objects.requireNonNull(set);
        if (node != null)
            System.out.println("Current node: " + Arrays.toString(node.getAddress()));
        for (TopologyTuple tuple : set)
            System.out.println(tuple.toString());
    }
}
