package network;

import java.io.Serializable;

public class WifiMacTrailer implements Serializable {
    private int checkSum;

    public WifiMacTrailer() {

    }

    public WifiMacTrailer(WifiMacTrailer trailer) {
        checkSum = trailer.checkSum;
    }

    public void setCheckSum(byte[] data) {
        checkSum = utilities.Checksum.generateChecksum(data);
    }
}
