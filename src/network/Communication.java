package network;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class Communication {


    public static boolean sendMessage(String message, short[] source, short[] destination) {
        IPPacket ipPacket = createPackage(message, source, destination);
        NodeList.getInstance().getNode(source).receivePacket(ipPacket);
        return true;
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

    public static void findNeighbours() {

    }
}
