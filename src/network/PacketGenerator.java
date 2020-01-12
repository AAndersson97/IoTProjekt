package network;
import static network.Constants.Protocol.TFTP_PORT;
import static network.Constants.Protocol.UDP_PROTOCOL_NUM;


public class PacketGenerator {
    public static TFTPPacket generateTFTPPacket(short[] sourceAddress, short[] destinationAddress,String data) {
        IPHeader ipHeader = new IPHeader(data.length(), sourceAddress, destinationAddress, UDP_PROTOCOL_NUM);
        UDPHeader udpHeader = new UDPHeader(TFTP_PORT, TFTP_PORT, (short) ipHeader.getLength());
        return new TFTPPacket(ipHeader, udpHeader, TFTPPacket.TFTPOpcode.DATA, data);
    }
}
