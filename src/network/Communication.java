package network;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class Communication {
    private static ArrayList<IPPacket> sentPackets;
    private static PacketListener packetListener;

    static {
        sentPackets = new ArrayList<IPPacket>();
    }
    public static boolean sendMessage(String message, short[] source, short[] destination) {
        IPPacket ipPacket = createPackage(message, source, destination);
        NodeList.getInstance().getNode(source).receivePacket(ipPacket);
        return true;
    }

    public static void addPacketListener(PacketListener listener) {
        packetListener = listener;
    }

    private static IPPacket createPackage(String message, short[] source, short[] destination) {
        TCPHeader tcpHeader = new TCPHeader.TCPHeaderBuilder()
                .destinationPort(0)
                .flags((byte) 0)
                .sourcePort(0)
                .sequenceNumber(0)
                .windowSize(1)
                .build();
        TCPPacket packet = new TCPPacket(tcpHeader,message.getBytes());
        IPHeader ipHeader = new IPHeader(shortToByte(source), shortToByte(destination));
        return new IPPacket(ipHeader, packet);

    }

    private static byte[] shortToByte(short[] numbers) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(numbers.length);
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.put(numbers);
        return byteBuffer.array();
    }

    public static void addPacket(IPPacket packet) {
        sentPackets.add(packet);
        if (packetListener != null)
            packetListener.packetAdded(packet);

    }
}
