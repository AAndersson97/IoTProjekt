package network;

public class WifiMacTrailer {
    public final int checkSum;
    WifiMacTrailer(byte[] data) {
        checkSum = utilities.Checksum.generateChecksum(data);
    }
}
