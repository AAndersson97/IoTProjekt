package network;

import static network.Constants.Protocol.*;

public class HelloMessage extends OLSRMessage {
    // Strukturen på meddelandet skiljer sig något från specifikationen i RFC3626-dokumentet för att förenkla hanteringen av
    // hello-meddelanden. Dock är all nödvändig information inkluderad i varje meddelande.
    public final short reserved;
    public final Willingness willingness; // vanligtvis WILL_DEFAULT
    public final LinkCode linkCode; // information om länken mellan avsändarens gränssnitt och listan över grannars gränssnitt. Specificerar även grannes status.
    public final int linkMsgSize;
    public final short[] neighborIfaceAdr; // neighbor interface address
    private float hTime; // specificerar när nästa HELLO-meddelande skickas

    HelloMessage(short[] originatorAddr, int msgSeqNum, Willingness willingness, LinkCode linkCode, short[] neighbor) {
        super(MessageType.HELLO_MESSAGE, originatorAddr, 1, msgSeqNum);
        reserved = 0;
        this.linkCode = linkCode;
        this.willingness = willingness;
        neighborIfaceAdr = neighbor;
        linkMsgSize = Short.SIZE + Integer.SIZE * 2 + neighborIfaceAdr.length; // storleken på linkCode, linkMsgSize, neighborIfaceAdr och hTime;
        hTime = System.currentTimeMillis() + HELLO_INTERVAL;
    }

    HelloMessage(HelloMessage message) {
        super(message);
        reserved = 0;
        this.willingness = message.willingness;
        this.linkCode = message.linkCode;
        this.neighborIfaceAdr = message.neighborIfaceAdr;
        this.linkMsgSize = Short.SIZE + Integer.SIZE * 2 + neighborIfaceAdr.length;
    }

    public static int length() {
        return Short.SIZE + Integer.SIZE * 3 + Float.SIZE + ADDRESS_LENGTH * Short.SIZE;
    }

    @Override
    OLSRMessage copy() {
        return new HelloMessage(this);
    }
}
