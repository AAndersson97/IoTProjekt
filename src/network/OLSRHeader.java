package network;

public class OLSRHeader {
    public final int packetLength;
    public final int packetSeqNum;

    OLSRHeader(int packetLength, int packetSeqNum) {
        this.packetLength = packetLength;
        this.packetSeqNum = packetSeqNum;
    }

    OLSRHeader(OLSRHeader header) {
        this.packetLength = header.packetLength;
        this.packetSeqNum = header.packetSeqNum;
    }
}
