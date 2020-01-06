package network;

public class MPRSelectorTupple {
    public short[] ms_main_addr; // adress till noden som valde denna nod att vara MPR
    public float ms_time; // när tuppeln löper ut och ska tas bort

    public MPRSelectorTupple(short[] ms_main_addr, float vTime) {
        this.ms_main_addr = ms_main_addr;
        this.ms_time = System.currentTimeMillis() + (vTime * 1000);
    }
}
