public class Package {

    private class TCPHeader {
        private int senderPort;
        private int destinationPort;
        private long sequenceNumber;
        private long acknum; // Numret på nästa pakets sequence number
        private int dataoffset;
        //private byte empty;
        private byte flags;
        private short windowSize;
        private int checksum;
        private int urgentPointer;


    }
}
