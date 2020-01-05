package network;

/**
 * Beskriver symmetrisk länk mellan en nods grannar och dess symmetriska 2-hopskvarter.
 */
public class TwoHopTuple {
    public final short[] n_neighbor_main_addr; // huvudadressen till en granne
    public final short[] n_2hop_addr; // huvudadressen till en 2-hopsgranne med en symmetrisk länk till n_neighbor_main_addre
    public final long n_time; // specificerar tiden då tupeln går ut och ska tas bort

    public TwoHopTuple(short[] n_neighbor_main_addr, short[] n_2hop_addr) {
        this.n_neighbor_main_addr = n_neighbor_main_addr;
        this.n_2hop_addr = n_2hop_addr;
        this.n_time = System.currentTimeMillis();
    }
}
