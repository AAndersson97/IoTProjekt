package network;
import static network.Constants.Protocol.DUP_HOLD_TIME;
/**
 * Klassen innehåller information om paket en nod har mottagit. Informationen förhindrar att samma paket bearbetas
 * och/eller skickas vidare i nätverket flera gånger.
 */
public class DuplicateTuple {
    public final short[] d_addr; // Sändarens IP-adresss
    public final short d_seq_num; // meddelandet sekvensnummer
    public boolean d_retransmitted; // true om meddelanden redan har vidarebefordras
    public short[] d_iface; // denna nods IP-adress, variabeltypen ändras till en lista om noden har flera gränssnitt (listan innehåller då adresser på gränssnitten som mottagit paketet)
    public long d_time; // tidstämpel då tupplen blir ogiltig och måste tas bort

    DuplicateTuple(short[] d_addr, short d_seq_num) {
        this.d_addr = d_addr;
        this.d_seq_num = d_seq_num;
        d_time = System.currentTimeMillis()/ 1000 + DUP_HOLD_TIME;
    }
}
