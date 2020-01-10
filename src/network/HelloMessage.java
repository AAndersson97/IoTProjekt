package network;

import java.util.ArrayList;
import java.util.HashMap;

import static network.Constants.Protocol.*;

public class HelloMessage extends OLSRMessage {
    // Strukturen på meddelandet skiljer sig något från specifikationen i RFC3626-dokumentet för att förenkla hanteringen av
    // hello-meddelanden. Dock är all nödvändig information inkluderad i varje meddelande.
    public final short reserved;
    public final Willingness willingness; // vanligtvis WILL_DEFAULT
    public final int linkMsgSize;
    public final HashMap<LinkCode, ArrayList<short[]>> neighborIfaceAdr; // neighbor interface address
    public final long hTime; // specificerar när nästa HELLO-meddelande skickas

    HelloMessage(short[] originatorAddr, int msgSeqNum, Willingness willingness, HashMap<LinkCode, ArrayList<short[]>> neighbors) {
        super(MessageType.HELLO_MESSAGE, originatorAddr, 1, msgSeqNum);
        reserved = 0;
        this.willingness = willingness;
        neighborIfaceAdr = neighbors;
        linkMsgSize = Short.SIZE + Integer.SIZE * 2 + Float.SIZE + neighborIfaceAdr.keySet().size() * Integer.SIZE * 2
                + neighborIfaceAdr.values().size() * Short.SIZE * ADDRESS_LENGTH; // storleken på linkCode, linkMsgSize, neighborIfaceAdr och hTime;
        hTime = System.currentTimeMillis() + HELLO_INTERVAL;
    }

    HelloMessage(HelloMessage message) {
        super(message);
        reserved = 0;
        this.willingness = message.willingness;
        this.neighborIfaceAdr = message.neighborIfaceAdr;
        this.linkMsgSize = message.linkMsgSize;
        this.hTime = message.hTime;
    }

    public static int length() {
        return Short.SIZE + Integer.SIZE * 3 + Float.SIZE + ADDRESS_LENGTH * Short.SIZE;
    }

    @Override
    OLSRMessage copy() {
        return new HelloMessage(this);
    }
}
