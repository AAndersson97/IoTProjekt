package network;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Communication {
    private static Communication instance;
    private static ArrayList<IPPacket> sentPackets;
    private static PacketListener packetListener;

    static {
        sentPackets = new ArrayList<>();
        instance = new Communication();
    }

    private Communication() {

    }
    public boolean sendMessage(String message, short[] source, short[] destination) {
        IPPacket ipPacket = createPackage(message, source, destination);
        Network.getInstance().getNode(source).receivePacket(ipPacket);
        return true;
    }

    public void addPacketListener(PacketListener listener) {
        packetListener = listener;
    }

    private static IPPacket createPackage(String message, short[] source, short[] destination) {
        TCPHeader tcpHeader = new TCPHeader.TCPHeaderBuilder()
                .flags((byte) 0)
                .sequenceNumber(0)
                .windowSize(1)
                .build();
        byte[] byteMsg = message.getBytes();
        TCPPacket packet = new TCPPacket(tcpHeader,byteMsg);
        IPHeader ipHeader = new IPHeader(byteMsg.length,shortToByte(source), shortToByte(destination));
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

    public static Communication getInstance() {
        return instance;
    }
}
