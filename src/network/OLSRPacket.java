package network;

import java.util.Arrays;

public class OLSRPacket<T extends OLSRMessage> extends Packet {
    public final T message; // Hello-meddelanden är oftast flera till antalet, antalet beror på antalet grannar avsändaren har

    public OLSRPacket(IPHeader ipHeader, UDPHeader udpHeader , OLSRHeader olsrHeader, T message, int id) {
        super(ipHeader, udpHeader, olsrHeader, id);
        this.olsrHeader = olsrHeader;
        this.message = message;
    }

    public OLSRPacket(IPHeader ipHeader, UDPHeader udpHeader , OLSRHeader olsrHeader, T message) {
        super(ipHeader, udpHeader, olsrHeader);
        this.olsrHeader = olsrHeader;
        this.message = message;
    }

    public OLSRPacket(OLSRPacket<T> packet) {
        super(packet.ipHeader, packet.udpHeader, packet.olsrHeader);
        this.olsrHeader = packet.olsrHeader;
        this.message = packet.message;
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

    public OLSRPacket<? extends OLSRMessage> copy() {
        IPHeader ipHeader = new IPHeader(this.ipHeader);
        UDPHeader udpHeader = new UDPHeader(this.udpHeader);
        OLSRHeader olsrHeader = new OLSRHeader(this.olsrHeader);
        return new OLSRPacket<>(ipHeader, udpHeader, olsrHeader, message, this.PACKET_ID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OLSRPacket<?> that = (OLSRPacket<?>) o;

        return message.equals(that.message);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + message.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "OLSRPacket{" +
                "ipHeader=" + ipHeader.toString() +
                ", udpHeader=" + udpHeader.toString() +
                ", length=" + OLSRMessage.length(message) +
                ", seqNum=" + olsrHeader.packetSeqNum + "}";
    }
}
