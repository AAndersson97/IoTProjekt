package network;
import static network.Constants.Protocol.TFTP_PORT;
import static network.Constants.Protocol.UDP_PROTOCOL_NUM;


public class PacketGenerator {
    private static int seqNum;
    public static TFTPPacket generateTFTPPacket(short[] sourceAddress, short[] destinationAddress,String data) {
        IPHeader ipHeader = new IPHeader(data.length(), sourceAddress, destinationAddress, UDP_PROTOCOL_NUM);
        UDPHeader udpHeader = new UDPHeader(TFTP_PORT, TFTP_PORT, (short) ipHeader.getLength());
        OLSRHeader olsrHeader = new OLSRHeader(ipHeader.getLength() + udpHeader.length,seqNum++);
        TFTPPacket packet = new TFTPPacket(ipHeader, udpHeader, olsrHeader, TFTPPacket.TFTPOpcode.DATA, data);
        packet.setWifiMacHeader(new WifiMacHeader(sourceAddress, sourceAddress));
        WifiMacTrailer wifiMacTrailer = new WifiMacTrailer();
        packet.setWifiMacTrailer(wifiMacTrailer);
        wifiMacTrailer.setCheckSum(packet.toBytes());
        return packet;
    }
}
