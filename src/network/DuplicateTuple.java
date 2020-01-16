package network;
import static network.Constants.Protocol.DUP_HOLD_TIME;
import static network.Constants.Protocol.MPR_SELECTOR_HOLD_TIME;

/**
 * Klassen innehåller information om paket en nod har mottagit. Informationen förhindrar att samma paket bearbetas
 * och/eller skickas vidare i nätverket flera gånger.
 */
public class DuplicateTuple {
    public final short[] d_addr; // Sändarens IP-adresss
    public final int d_seq_num; // meddelandets sekvensnummer
    public boolean d_retransmitted; // true om meddelanden redan har vidarebefordras
    public short[] d_iface; // denna nods IP-adress, variabeltypen ändras till en lista om noden har flera gränssnitt (listan innehåller då adresser på gränssnitten som mottagit paketet)
    private long d_time; // tiden då tuppeln blir ogiltig

    DuplicateTuple(short[] d_addr, short[] d_iface, int d_seq_num, boolean retransmitted) {
        this.d_addr = d_addr;
        this.d_seq_num = d_seq_num;
        this.d_iface = d_iface;
        this.d_retransmitted = retransmitted;
        d_time = System.currentTimeMillis() + DUP_HOLD_TIME;
    }

    public void renewTupple() {
        d_time = System.currentTimeMillis() + DUP_HOLD_TIME;
    }
    public long get_time() {
        return d_time;
    }
}
