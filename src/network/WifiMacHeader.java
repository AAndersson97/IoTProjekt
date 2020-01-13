package network;

import java.io.Serializable;

public class WifiMacHeader implements Serializable {

    public final byte ctrlType;
    public final byte ctrlSubType;
    public final byte ctrlToDs;
    public final byte ctrlFromDs;
    public final byte ctrlMoreFrag;
    public final byte ctrlRetry;
    public final byte ctrlMoreData;
    public final byte ctrlWep;
    public final byte duration;
    public short[] sender; // avsändarens macadress
    public short[] receiver;  // mottagarens macadress
    public final short[] address3;
    public final short[] address4;
    public final byte seqFrag;
    public final short seqSeq;
    public final byte qosTid;
    public final byte qosEosp;
    public final byte qosAckPolicy;
    public final byte amsduPresent;
    public final byte qosStuff;

    {
        ctrlType = ctrlSubType = ctrlToDs = ctrlFromDs = ctrlMoreFrag = ctrlRetry = ctrlMoreData = ctrlWep = duration = 0;
        address3 = address4 = null;
        seqSeq = qosTid = qosEosp = qosAckPolicy = qosStuff = amsduPresent = 0;
        seqFrag = 0;
    }
    public WifiMacHeader(short[] sender, short[] receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public WifiMacHeader(WifiMacHeader header) {
        this.sender = header.sender;
        this.receiver = header.receiver;
    }


    enum QosAckPolicy {
        NORMAL_ACK, NO_ACK, NO_EXPLICIT_ACK, BLOCK_ACK
    };
    enum AddressType {
        ADDR1, ADDR2, ADDR3, ADDR4
    };
}
