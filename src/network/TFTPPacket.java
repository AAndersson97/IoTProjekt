package network;

public class TFTPPacket extends Packet {
    public final TFTPOpcode opcode;
    public final String data;

    TFTPPacket(TFTPOpcode opcode, String data) {
        this.opcode = opcode;
        this.data = data;
    }
    TFTPPacket(TFTPPacket packet) {
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

}
