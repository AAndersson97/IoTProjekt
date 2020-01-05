package network;
import static network.Constants.Protocol.Willingness;

public class NeighborTuple {
    public final short[] n_neighbor_main_addr;
    public N_status status;
    public final Willingness n_willingness;

    NeighborTuple(short[] neighbor, N_status status, Willingness willingness) {
        n_neighbor_main_addr = neighbor;
        this.status = status;
        n_willingness = willingness;
    }

    enum N_status {
        NOT_SYM, SYM;
    }
}
