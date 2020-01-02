package network;
import static network.Constants.Protocol.SCALING_FACTOR;

public class OLSRPacket {
    private IPHeader ipHeader;
    private UDPHeader udpHeader;
    private short length;
    private short seqNum;
    private byte msgType;
    private short vTime; // Hur lång tid mottagande av paket en nod måste anse att informationen i meddelandet är giltig om inte nylig uppdatering till informationen har mottagits
    private short msgSize;
    private int orginatorAddr;
    private byte timeToLive;
    private byte hopCount; // Måste öka med ett för varje hopp
    private static short msgSeqNum;
    private String msg;

    public OLSRPacket() {
        msgSeqNum++;
    }

    public OLSRPacket(OLSRPacket packet) {
        this.ipHeader = new IPHeader(packet.ipHeader);
        //this.udpHeader = new UDPHeader(udpHeader);
        this.length = packet.length;
        this.seqNum = packet.seqNum;
        this.msgType = packet.msgType;
        this.vTime = packet.vTime;
        this.msgSize = packet.msgSize;
        this.orginatorAddr = packet.orginatorAddr;
        this.timeToLive = packet.timeToLive;
        this.hopCount = packet.hopCount;
        this.msg = packet.msg;
    }

    private void setVtime() {
        int a = vTime & 0b11110000; // fyra högsta bitarna i Vtime-fältet
        int b = vTime & 0b00001111;; // fyra lägsta bitarna i Vtime-fältet
        vTime = (short) (SCALING_FACTOR * (1+a/16)*Math.pow(2,b));
    }
}
