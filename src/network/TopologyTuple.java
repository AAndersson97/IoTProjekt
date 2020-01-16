package network;

import java.util.Arrays;

import static network.Constants.Protocol.TC_HOLD_TIME;

/**
 * Innehåller information om en destination i nätverket
 */
public class TopologyTuple {
    public final short[] t_dest_addr; // destinationens huvudadress
    public final short[] t_last_addr; // adress till en nod som kan nå t_dest_addr i ett hopp, vanligtvis MPR-nod till t_dest_addr
    public final int t_seq; // sekvensnummer
    private long t_time; // specificerar tiden då tupeln går ut och måste tas bort

    public TopologyTuple(short[] t_dest_addr, short[] t_last_addr, int t_seq) {
        this.t_dest_addr = t_dest_addr;
        this.t_last_addr = t_last_addr;
        this.t_seq = t_seq;
        this.t_time = System.currentTimeMillis() + TC_HOLD_TIME;
    }

    public void renewTupple() {
        t_time = System.currentTimeMillis() + TC_HOLD_TIME;
    }

    public long get_time() {
        return t_time;
    }

    @Override
    public String toString() {
        return "TopologyTuple{" +
                "t_dest_addr=" + Arrays.toString(t_dest_addr) +
                ", t_last_addr=" + Arrays.toString(t_last_addr) +
                ", t_seq=" + t_seq +
                ", t_time=" + t_time +
                '}';
    }
}
