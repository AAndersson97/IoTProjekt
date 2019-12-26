package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

public class Communication {
    //private static ArrayList<IPPacket> sentPackets;
    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;
    private ServerSocket serverSocket;

    public Communication(InetAddress address) {

    }

    public void stop() {
        writer.close();
        try {
            reader.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(String message,short[] source, short[] destination) {
        IPPacket ipPacket = createPackage(message, source, destination);

        //Area.getInstance().getNode(destination).receivePacket(ipPacket);
    }

    public void sendMessage(Packet packet, short[] source, short[] destination) {
        //Area.getInstance().getNode(destination).receivePacket(packet);
    }

    public static IPPacket createPackage(String message, short[] source, short[] destination) {
        TCPHeader tcpHeader = new TCPHeader(0, 0, 0, 0, 0);
        byte[] byteMsg = message.getBytes();
        TCPPacket packet = new TCPPacket(tcpHeader,byteMsg);
        IPHeader ipHeader = null;
        try {
            ipHeader = new IPHeader(byteMsg.length,shortToByte(source), shortToByte(destination), Constants.TCP_PROTOCOL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new IPPacket(ipHeader, packet);
    }

    private static byte[] shortToByte(short[] numbers) {
        byte[] bytes = new byte[numbers.length];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte)numbers[i];

        return bytes;
    }

}
