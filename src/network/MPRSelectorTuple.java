package network;

import static network.Constants.Protocol.MPR_SELECTOR_HOLD_TIME;

public class MPRSelectorTuple {
    public short[] ms_main_addr; // adress till noden som valde denna nod att vara MPR
    private long ms_time; // när tuppeln löper ut och ska tas bort

    public MPRSelectorTuple(short[] ms_main_addr) {
        this.ms_main_addr = ms_main_addr;
        this.ms_time = System.currentTimeMillis() + MPR_SELECTOR_HOLD_TIME;
    }

    public void renewTupple() {
        ms_time = System.currentTimeMillis() + MPR_SELECTOR_HOLD_TIME;
    }
    public long get_time() {
        return ms_time;
    }
}
