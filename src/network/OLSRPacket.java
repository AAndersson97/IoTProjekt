package network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class OLSRPacket<T extends OLSRMessage> {
    public final IPHeader ipHeader;
    public final UDPHeader udpHeader;
    public final OLSRHeader olsrHeader;
    public final ArrayList<T> messages;

    public OLSRPacket(IPHeader ipHeader, UDPHeader udpHeader, OLSRHeader olsrHeader, ArrayList<T> messages) {
        this.ipHeader = ipHeader;
        this.udpHeader = udpHeader;
        this.olsrHeader = olsrHeader;
        this.messages = messages;
    }

    public OLSRPacket(OLSRPacket packet) {
        this.ipHeader = new IPHeader(packet.ipHeader);
        this.udpHeader = new UDPHeader(packet.udpHeader);
        this.olsrHeader = packet.olsrHeader;
        this.messages = packet.messages;
    }

    public static boolean canRetransmit(OLSRPacket packet) {
        if (packet.messages == null || packet.messages.isEmpty())
            return false;
        Iterator<OLSRMessage> iterator = packet.messages.iterator();
        while (iterator.hasNext()) {
            OLSRMessage msg = iterator.next();
            if (msg.getTTL() <= 0)
                iterator.remove();
            msg.decrementTTL();
            msg.incrementHopCount();
        }
        return true;
    }

    /**
     * Paket utan meddelande, paket där ursprungsadressen är samma som destinationsadressen eller meddelanden
     * där TTL-fältet är 1 eller mindre ska kastas.
     * @param
     * @return
     */
    public static boolean canBeProcessed(OLSRMessage message, short[] receiver) {
        return  !(message.getTTL() <= 0 || Arrays.equals(message.originatorAddr, receiver));
    }

    public OLSRPacket copy() {
        IPHeader ipHeader = new IPHeader(this.ipHeader);
        UDPHeader udpHeader = new UDPHeader(this.udpHeader);
        OLSRHeader olsrHeader = new OLSRHeader(this.olsrHeader);
        ArrayList<OLSRMessage> messages = new ArrayList<>();
        for (OLSRMessage message : this.messages)
            messages.add(message.copy());
        return new OLSRPacket(ipHeader, udpHeader, olsrHeader, messages);
    }

    @Override
    public String toString() {
        return "OLSRPacket{" +
                "ipHeader=" + ipHeader.toString() +
                ", udpHeader=" + udpHeader.toString() +
                ", length=" + messages.size() +
                ", seqNum=" + olsrHeader.packetSeqNum + "}";
    }
}
