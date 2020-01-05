package network;

import java.util.Arrays;

/**
 * Beskriver symmetrisk länk mellan en nods grannar och dess symmetriska 2-hopskvarter.
 */
public class TwoHopTuple {
    public final short[] n_neighbor_main_addr; // huvudadressen till en granne
    public final short[] n_2hop_addr; // huvudadressen till en 2-hopsgranne med en symmetrisk länk till n_neighbor_main_addre
    public final float n_time; // specificerar tiden då tupeln går ut och ska tas bort

    public TwoHopTuple(short[] n_neighbor_main_addr, short[] n_2hop_addr, float vTime) {
        this.n_neighbor_main_addr = n_neighbor_main_addr;
        this.n_2hop_addr = n_2hop_addr;
        this.n_time = System.currentTimeMillis() + vTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TwoHopTuple that = (TwoHopTuple) o;

        if (!Arrays.equals(n_neighbor_main_addr, that.n_neighbor_main_addr)) return false;
        return Arrays.equals(n_2hop_addr, that.n_2hop_addr);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(n_neighbor_main_addr);
        result = 31 * result + Arrays.hashCode(n_2hop_addr);
        result = 31 * result + (n_time != +0.0f ? Float.floatToIntBits(n_time) : 0);
        return result;
    }
}
