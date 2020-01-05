package network;

import java.util.Arrays;

import static network.Constants.Protocol.SCALING_FACTOR;
import static network.Constants.Protocol.OLSR_HEADER_SIZE;
import static network.Constants.Protocol.DEFAULT_TTL;

public class OLSRPacket {
    public final IPHeader ipHeader;
    public final UDPHeader udpHeader;
    public final int length;
    public final int seqNum;
    public final int msgType;
    public final int msgSize;
    public final short[] originatorAddr;
    public final String msg;
    private int vTime; // Hur lång tid mottagande av paket en nod måste anse att informationen i meddelandet är giltig om inte nylig uppdatering till informationen har mottagits
    private int timeToLive;
    private int hopCount; // Måste öka med ett för varje hopp
    private static int msgSeqNum;

    public OLSRPacket(IPHeader ipHeader, UDPHeader udpHeader, MessageType type, short[] originator, short seqNum, String msg) {
        this.ipHeader = ipHeader;
        this.udpHeader = udpHeader;
        msgType = type.value;
        originatorAddr = originator;
        msgSize = msg.length();
        timeToLive = DEFAULT_TTL;
        length = OLSR_HEADER_SIZE + msg.length();
        this.msg = msg;
        this.seqNum = seqNum;
        setVtime(this);
    }

    public OLSRPacket(OLSRPacket packet) {
        this.ipHeader = new IPHeader(packet.ipHeader);
        this.udpHeader = new UDPHeader(packet.udpHeader);
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
        packet.vTime = (int) (SCALING_FACTOR * (1+a/16) * Math.pow(2,b));
    }

    public static boolean canRetransmit(OLSRPacket packet) {
        if (packet.timeToLive <= 1 || packet.msg.isBlank())
            return false;
        packet.timeToLive--;
        packet.hopCount++;
        return true;
    }

    /**
     * Paket utan meddelande, paket där ursprungsadressen är samma som destinationsadressen eller paket
     * där TTL-fältet är 1 eller mindre ska kastas.
     * @param packet
     * @return
     */
    public static boolean canBeProcessed(OLSRPacket packet) {
        return !(packet.msg.isBlank() || packet.timeToLive <= 0 || Arrays.equals(packet.originatorAddr, packet.ipHeader.destinationAddress));
    }

    public enum MessageType {
        HELLO_MESSAGE(1), TC_MESSAGE(2), MID_MESSAGE(3), HNA_MESSAGE(4);
        int value;
        MessageType(int value) {
            this.value = value;
        }
    }

    @Override
    public String toString() {
        return "OLSRPacket{" +
                "ipHeader=" + ipHeader.toString() +
                ", udpHeader=" + udpHeader.toString() +
                ", length=" + length +
                ", seqNum=" + seqNum +
                ", msgType=" + msgType +
                ", msgSize=" + msgSize +
                ", originatorAddr=" + Arrays.toString(originatorAddr) +
                ", msg='" + msg + '\'' +
                ", vTime=" + vTime +
                ", timeToLive=" + timeToLive +
                ", hopCount=" + hopCount +
                '}';
    }
}
