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
        private int windowSize;
        private int checksum;
        private int urgentPointer;
        private byte[] options;

        public TCPHeader(byte[] baseHeader) {
            if (baseHeader.length == HEADER_SIZE) {
                sourcePort = ( ( ( baseHeader[0] << 8 ) & 65280 ) | ( baseHeader[1 ] & 255 ));
                destinationPort = (int)(((baseHeader[2]<<8)&65280)|(baseHeader[3]&255));

                sequenceNumber = (((baseHeader[4]<<24)&4278190080l)
                        |((baseHeader[5]<<16)&16711680l)
                        |((baseHeader[6]<<8)&65280)
                        |(baseHeader[7]&255));

                ackNum = (((baseHeader[8]<<24)&4278190080l)
                        |((baseHeader[9]<<16)&16711680l)
                        |((baseHeader[10]<<8)&65280)
                        |(baseHeader[11]&255));

                dataOffset = (byte)((baseHeader[12]>>4)&15);
                flags = (byte)(baseHeader[13]&63);
                windowSize = (((baseHeader[14]<<8)&65280)|(baseHeader[15]&255));
                checksum = (((baseHeader[16]<<8)&65280)|(baseHeader[17]&255));
                urgentPointer = (((baseHeader[18]<<8)&65280)|(baseHeader[19]&255));
            }
            else {
                throw new RuntimeException("Failed to create a TCP Header");
            }
        }

        public int length() {
            return HEADER_SIZE + (this.options == null ? 0 : this.options.length);
        }

        public TCPHeader() {

        }


    }

}
