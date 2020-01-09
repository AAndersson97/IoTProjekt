package network;

import java.util.Arrays;

public class OLSRPacket<T extends OLSRMessage> extends Packet {
    public final OLSRHeader olsrHeader;
    public final T message; // Hello-meddelanden är oftast flera till antalet, antalet beror på antalet grannar avsändaren har

    public OLSRPacket(IPHeader ipHeader, UDPHeader udpHeader, OLSRHeader olsrHeader, T message) {
        this.ipHeader = ipHeader;
        this.udpHeader = udpHeader;
        this.olsrHeader = olsrHeader;
        this.message = message;
    }

    public OLSRPacket(OLSRPacket<T> packet) {
        this.ipHeader = new IPHeader(packet.ipHeader);
        this.udpHeader = new UDPHeader(packet.udpHeader);
        this.olsrHeader = packet.olsrHeader;
        this.message = packet.message;
    }

    public static boolean canRetransmit(OLSRPacket<OLSRMessage> packet) {
        if (packet.message == null)
            return false;
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

    public OLSRPacket<? extends OLSRMessage> copy() {
        IPHeader ipHeader = new IPHeader(this.ipHeader);
        UDPHeader udpHeader = new UDPHeader(this.udpHeader);
        OLSRHeader olsrHeader = new OLSRHeader(this.olsrHeader);
        return new OLSRPacket<>(ipHeader, udpHeader, olsrHeader, message);
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
