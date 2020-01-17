package network;

public class LinkTuple {
    public final short[] l_local_iface_addr; // lokala nodens adress
    public short[] l_neighbor_iface_addr; // grannens adress
    public long l_sym_time; // tiden tills länken anses vara symmetrisk
    public long l_asym_time; // tiden tills grannens gränssnitts anses vara "heard"
    public long l_time; // tiden då tuppeln blir ogiltig
    // när både l_sym_time och l_asym_time löpt ut är länken förlorad
    // om l_sym_time ej löpt ut måste länken deklareras som symmetrisk, annars som asymmetrisk.

    public LinkTuple(short[] l_local_iface_addr, short[] l_neighbor_iface_addr, long l_sym_time, long l_asym_time) {
        this.l_local_iface_addr = l_local_iface_addr;
        this.l_neighbor_iface_addr = l_neighbor_iface_addr;
        this.l_sym_time = l_sym_time;
        this.l_asym_time = l_asym_time;
        this.l_time = l_asym_time;
    }

    public void renewTuple() {
        l_time = l_asym_time;
    }

}
