package network;

import static network.Constants.Protocol.NEIGHB_HOLD_TIME;
import static network.Constants.Protocol.SCALING_FACTOR;

public abstract class OLSRMessage {
    public final MessageType msgType;
    public final int msgSize;
    public final short[] originatorAddr;
    public float vTime; // Hur lång tid mottagande av paket en nod måste anse att informationen i meddelandet är giltig om inte nylig uppdatering till informationen har mottagits
    public final int msgSeqNum;
    private int timeToLive;
    private int hopCount; // Måste öka med ett för varje hopp

    protected OLSRMessage(MessageType msgType, short[] originatorAddr, int timeToLive, int msgSeqNum) {
        this.msgType = msgType;
        this.msgSize = Integer.SIZE * 5 + Float.SIZE + originatorAddr.length;
        this.originatorAddr = originatorAddr;
        this.timeToLive = timeToLive;
        this.hopCount = 0;
        this.msgSeqNum = msgSeqNum;
        setVtime(this);
    }

    protected OLSRMessage(OLSRMessage message) {
        this.msgType = message.msgType;
        this.msgSeqNum = message.msgSize;
        this.originatorAddr = message.originatorAddr;
        this.vTime = message.vTime;
        this.timeToLive = message.timeToLive;
        this.hopCount = message.hopCount;
        this.msgSize = message.msgSize;
    };

    private static void setVtime(OLSRMessage msg) {
        int a = NEIGHB_HOLD_TIME & 0b11110000; // fyra högsta bitarna i Vtime-fältet
        int b = NEIGHB_HOLD_TIME & 0b00001111; // fyra lägsta bitarna i Vtime-fältet
        msg.vTime = (int) (SCALING_FACTOR * (1+a/16.0) * Math.pow(2,b));
    }

    public void decrementTTL() {
        timeToLive--;
    }

    public void incrementHopCount() {
        hopCount++;
    }

    public int getTTL() {
        return timeToLive;
    }

    public enum MessageType {
        HELLO_MESSAGE(1), TC_MESSAGE(2), MID_MESSAGE(3);
        int value;
        MessageType(int value) {
            this.value = value;
        }
    }

    abstract OLSRMessage copy();

    protected static int length(OLSRMessage message) {
        return message.msgSize;
    };
}
