package network;

import static network.Constants.Protocol.TCP_HOLD_TIME;

/**
 * Innehåller information om en destination i nätverket
 */
public class TopologyTuple {
    public final short[] t_dest_addr; // destinationens huvudadress
    public final short[] t_last_addr; // adress till en nod som kan nå t_dest_addr i ett hopp, vanligtvis MPR-nod till t_dest_addr
    public final int t_seq; // sekvensnummer
    public float t_time; // specificerar tiden då tupeln går ut och måste tas bort

    public TopologyTuple(short[] t_dest_addr, short[] t_last_addr, int t_seq) {
        this.t_dest_addr = t_dest_addr;
        this.t_last_addr = t_last_addr;
        this.t_seq = t_seq;
        this.t_time = TCP_HOLD_TIME;
    }
}
