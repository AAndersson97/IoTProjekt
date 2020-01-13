package network;

public class TFTPPacket extends Packet {
    public final TFTPOpcode opcode;
    public final String data;

    TFTPPacket(IPHeader ipHeader, UDPHeader udpHeader, OLSRHeader olsrHeader, TFTPOpcode opcode, String data) {
        super(ipHeader, udpHeader, olsrHeader);
        this.opcode = opcode;
        this.data = data;
    }

    TFTPPacket(WifiMacHeader header, WifiMacTrailer trailer, IPHeader ipHeader, UDPHeader udpHeader, OLSRHeader olsrHeader, TFTPOpcode opcode, String data) {
        super(header, trailer, ipHeader, udpHeader, olsrHeader);
        this.opcode = opcode;
        this.data = data;
    }

    TFTPPacket(TFTPPacket packet) {
        super(packet.ipHeader, packet.udpHeader, packet.olsrHeader);
        this.opcode = packet.opcode;
        this.data = packet.data;
    }

    // Read Request, Write Request, Read or write the next block of data, Acknowledgement, Error message, Option Acknowledgement
    enum TFTPOpcode {
        RRQ(1), WRQ(2), DATA(3), ACK(4), ERROR(5), OACK(6);
        int value;
        TFTPOpcode(int value) {
            this.value = value;
        }
    }

    @Override
    public Packet copy() {
        return new TFTPPacket(new WifiMacHeader(wifiMacHeader), new WifiMacTrailer(wifiMacTrailer),new IPHeader(ipHeader),  new UDPHeader(udpHeader),new OLSRHeader(olsrHeader), opcode, String.valueOf(data));
    }
}
