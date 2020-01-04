package network;

import static network.Constants.Protocol.SCALING_FACTOR;

public class OLSRPacket {
    private IPHeader ipHeader;
    private UDPHeader udpHeader;
    private short length;
    private short seqNum;
    private short msgType;
    private short vTime; // Hur lång tid mottagande av paket en nod måste anse att informationen i meddelandet är giltig om inte nylig uppdatering till informationen har mottagits
    private short msgSize;
    private final short[] originatorAddr;
    private short timeToLive;
    private short hopCount; // Måste öka med ett för varje hopp
    private static short msgSeqNum;
    private String msg;

    public OLSRPacket(MessageType type ,short[] originator, short seqNum,String msg) {
        msgType = (short) type.value;
        originatorAddr = originator;
        msgSize = (short) msg.length();
        timeToLive = 255;
        this.msg = msg;
        this.seqNum = seqNum;
        setVtime(this);
    }

    public OLSRPacket(OLSRPacket packet) {
        this.ipHeader = new IPHeader(packet.ipHeader);
        this.udpHeader = new UDPHeader(udpHeader);
        this.length = packet.length;
        this.seqNum = packet.seqNum;
        this.msgType = packet.msgType;
        this.vTime = packet.vTime;
        this.msgSize = packet.msgSize;
        this.originatorAddr = packet.originatorAddr;
        this.timeToLive = packet.timeToLive;
        this.hopCount = packet.hopCount;
        this.msg = packet.msg;
    }

    private static void setVtime(OLSRPacket packet) {
        int a = packet.vTime & 0b11110000; // fyra högsta bitarna i Vtime-fältet
        int b = packet.vTime & 0b00001111;; // fyra lägsta bitarna i Vtime-fältet
        packet.vTime = (short) (SCALING_FACTOR * (1+a/16)*Math.pow(2,b));
    }

    public static boolean canRetransmit(OLSRPacket packet) {
        if (packet.timeToLive <= 1 || packet.msg.isBlank())
            return false;
        packet.timeToLive--;
        packet.hopCount++;
        return true;
    }

    public enum MessageType {
        HELLO_MESSAGE(1), TC_MESSAGE(2), MID_MESSAGE(3), HNA_MESSAGE(4);
        int value;
        MessageType(int value) {
            this.value = value;
        }
    }
}
