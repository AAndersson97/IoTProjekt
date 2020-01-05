package network;

import java.util.ArrayList;

import static network.Constants.Protocol.NEIGHB_HOLD_TIME;
import static network.Constants.Protocol.Willingness;
import static network.Constants.Protocol.SCALING_FACTOR;

public class HelloMessage extends OLSRMessage {
    public final short reserved;
    public final Willingness willingness; // vanligtvis WILL_DEFAULT
    public final LinkCode linkCode; // information om länken mellan avsändarens gränssnitt och listan över grannars gränssnitt. Specificerar även grannes status.
    public final int linkMsgSize;
    public final short[] neighborIfaceAdr; // neighbor interface address
    private float hTime; // specificerar hur ofta HELLO-meddelanden skickas ut av noden, även tiden tills nästa meddelande skickas ut

    HelloMessage(short[] originatorAddr, int msgSeqNum, Willingness willingness, LinkCode linkCode, short[] neighbor) {
        super(MessageType.HELLO_MESSAGE, originatorAddr, 1, msgSeqNum);
        reserved = 0;
        this.linkCode = linkCode;
        this.willingness = willingness;
        neighborIfaceAdr = neighbor;
        linkMsgSize = Short.SIZE + Integer.SIZE * 2 + neighborIfaceAdr.length; // storleken på linkCode, linkMsgSize, neighborIfaceAdr och hTime;
        calculateHtime(this);
    }

    HelloMessage(HelloMessage message) {
        super(message);
        reserved = 0;
        this.willingness = message.willingness;
        this.linkCode = message.linkCode;
        this.neighborIfaceAdr = message.neighborIfaceAdr;
        this.linkMsgSize = Short.SIZE + Integer.SIZE * 2 + neighborIfaceAdr.length;
        calculateHtime(message);
    }

    private static void calculateHtime(HelloMessage hello) {
        int a = NEIGHB_HOLD_TIME & 0b11110000; // fyra högsta bitarna av Htime-fältet
        int b = NEIGHB_HOLD_TIME & 0b00001111; // fyra lägsta bitarna av Htime-fältet
        hello.hTime = (float) (SCALING_FACTOR * (1+a/16.0)*Math.pow(2,b));
    }




    @Override
    OLSRMessage copy() {
        return new HelloMessage(this);
    }
}
