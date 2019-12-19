package network;

import java.util.ArrayList;


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
    public void sendMessage(String message,short[] source, short[] destination) {
        IPPacket ipPacket = createPackage(message, source, destination);

        //Area.getInstance().getNode(destination).receivePacket(ipPacket);
    }

    public void sendMessage(Packet packet, short[] source, short[] destination) {
        //Area.getInstance().getNode(destination).receivePacket(packet);
    }

    public void addPacketListener(PacketListener listener) {
        packetListener = listener;
    }

    public static IPPacket createPackage(String message, short[] source, short[] destination) {
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
        byte[] bytes = new byte[numbers.length];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte)numbers[i];

        return bytes;
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
