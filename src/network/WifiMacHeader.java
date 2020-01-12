package network;

public class WifiMacHeader {

    public final byte ctrlType;
    public final byte ctrlSubType;
    public final byte ctrlToDs;
    public final byte ctrlFromDs;
    public final byte ctrlMoreFrag;
    public final byte ctrlRetry;
    public final byte ctrlMoreData;
    public final byte ctrlWep;
    public final byte duration;
    public final short[] address1; // avsändarens macadress
    public final short[] address2;  // mottagarens macadress
    public final short[] address3;
    public final short[] address4;
    public final byte seqFrag;
    public final short seqSeq;
    public final byte qosTid;
    public final byte qosEosp;
    public final byte qosAckPolicy;
    public final byte amsduPresent;
    public final byte qosStuff;

    public WifiMacHeader(short[] address1, short[] address2) {
        this.address1 = address1;
        this.address2 = address2;
        ctrlType = ctrlSubType = ctrlToDs = ctrlFromDs = ctrlMoreFrag = ctrlRetry = ctrlMoreData = ctrlWep = duration = 0;
        address3 = address4 = null;
        seqSeq = qosTid = qosEosp = qosAckPolicy = qosStuff = amsduPresent = 0;
        seqFrag = 0;
    }


    enum QosAckPolicy {
        NORMAL_ACK, NO_ACK, NO_EXPLICIT_ACK, BLOCK_ACK
    };
    enum AddressType {
        ADDR1, ADDR2, ADDR3, ADDR4
    };
}
