package old;
import network.IPHeader;
import network.UDPHeader;

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
    private short msgSeqNum;
    private String msg;

    public OLSRPacket() {

    }

    private void setVtime() {
        int a = vTime & 0b11110000; // fyra högsta bitarna i Vtime-fältet
        int b = vTime & 0b00001111;; // fyra lägsta bitarna i Vtime-fältet
        vTime = (short) (SCALING_FACTOR * (1+a/16)*Math.pow(2,b));
    }
}
