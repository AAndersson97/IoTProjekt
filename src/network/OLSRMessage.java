package network;

import static network.Constants.Protocol.*;

public abstract class OLSRMessage {
    public final MessageType msgType;
    public final int msgSize;
    public final short[] originatorAddr;
    public long vTime; // Hur lång tid mottagande av paket en nod måste anse att informationen i meddelandet är giltig om inte nylig uppdatering till informationen har mottagits
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
        this.vTime = System.currentTimeMillis() + MESSAGE_VTIME;
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
