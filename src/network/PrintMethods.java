package network;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PrintMethods {
    public static void printRoutingTable(List<RoutingTuple> routingTable, Node node) {
        Objects.requireNonNull(routingTable);
        System.out.println("Node address: " + Arrays.toString(node.getAddress()));
        for (RoutingTuple tuple : routingTable) {
            System.out.println(tuple.toString());
        }
    }
    public static void printPacketInfo(Packet packet, Node node) {
        Objects.requireNonNull(packet);
        if (packet instanceof TFTPPacket) {
            TFTPPacket tftpPacket = (TFTPPacket) packet;
            if (node != null)
                System.out.println("Current node: " + Arrays.toString(node.getAddress()));
            System.out.println(tftpPacket.ipHeader.toString() + "\n" + "Receiver: " + Arrays.toString(tftpPacket.wifiMacHeader.receiver) +
                    " Sender: " + Arrays.toString(tftpPacket.wifiMacHeader.sender));
        }
    }
}
