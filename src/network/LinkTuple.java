package network;

public class LinkTuple {
    public final short[] l_local_iface_addr; // lokala nodens adress
    public final short[] l_neighbor_iface_addr; // grannens adress
    public final int l_sym_time; // tiden tills länken anses vara symmetrisk
    public final int l_asym_time; // tiden tills grannens gränssnitts anses vara "heard"
    public final long l_time; // tiden när tuplen går ut och måste tas bort
    // när både l_sym_time och l_asym_time löpt ut är länken förlorad
    // om l_sym_time ej löpt ut måste länken deklareras som symmetrisk, annars som asymmetrisk.

    public LinkTuple(short[] l_local_iface_addr, short[] l_neighbor_iface_addr, int l_sym_time, int l_asym_time) {
        this.l_local_iface_addr = l_local_iface_addr;
        this.l_neighbor_iface_addr = l_neighbor_iface_addr;
        this.l_sym_time = l_sym_time;
        this.l_asym_time = l_asym_time;
        this.l_time = System.currentTimeMillis();
    }
}
