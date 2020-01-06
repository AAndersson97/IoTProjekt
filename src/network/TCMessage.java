package network;
import java.util.ArrayList;

import static network.Constants.Protocol.TOP_HOLD_TIME;

public class TCMessage extends OLSRMessage {
    public final int ANSN; // Advertised Neighbor Sequence Number, ett sekvensnmmer som associeras med "advertised neighbor set".
    public final short reserved;
    public final short[][] advertisedNMA; // Advertised Neighbor Main Address, innehåller adresser till grannoder

    protected TCMessage(short[] originatorAddr, int msgSeqNum, int ANSN, short[][] advertisedNMA) {
        super(MessageType.TC_MESSAGE, originatorAddr, TOP_HOLD_TIME, msgSeqNum);
        this.ANSN = ANSN;
        reserved = 0;
        this.advertisedNMA = advertisedNMA;
    }

    protected TCMessage(TCMessage message) {
        super(message);
        ANSN = message.ANSN;
        reserved = 0;
        advertisedNMA = message.advertisedNMA;
    }

    @Override
    OLSRMessage copy() {
        return new TCMessage(this);
    }
}
