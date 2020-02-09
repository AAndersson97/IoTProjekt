package network.tuples;

import java.util.Arrays;

public class RoutingTuple {
    public final short[] r_dest_addr; // destinationsnodens adress
    public final short[] r_next_addr; // adressen till noden som utgör första hoppet till destinationsnoden
    public final int r_dist; // antalet hopp från den lokala noden till destinationsnoden
    public final short[] r_iface_addr; // lokala nodens adress (specifikt för ett av nodens gränssnitt)

    public RoutingTuple(short[] r_dest_addr, short[] r_next_addr, int r_dist, short[] r_iface_addr) {
        this.r_dest_addr = r_dest_addr;
        this.r_next_addr = r_next_addr;
        this.r_dist = r_dist;
        this.r_iface_addr = r_iface_addr;
    }

    @Override
    public String toString() {
        return "RoutingTuple{" +
                "r_dest_addr=" + Arrays.toString(r_dest_addr) +
                ", r_next_addr=" + Arrays.toString(r_next_addr) +
                ", r_dist=" + r_dist +
                ", r_iface_addr=" + Arrays.toString(r_iface_addr) +
                '}';
    }
}
