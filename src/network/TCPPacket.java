package network;

public class TCPPacket {

    public class TCPHeader {
        private final int HEADER_SIZE = 20;
        private int sourcePort;
        private int destinationPort;
        private long sequenceNumber;
        private long ackNum; // Numret på nästa pakets sequence number
        private int dataOffset;
        //private byte empty;
        private byte flags;
        private short windowSize;
        private int checksum;
        private int urgentPointer;
        private byte[] options;

        public TCPHeader(byte[] baseHeader) {
            if (baseHeader.length == HEADER_SIZE) {
                sourcePort = ( ( ( baseHeader[0] << 8 ) & 65280 ) | ( baseHeader[1 ] & 255 ));
            }
        }


    }

}
