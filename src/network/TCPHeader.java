package network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TCPHeader {
    private static final int HEADER_SIZE = 20;
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

    private TCPHeader() {
    }

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

    /**
     * Omvandlar klassens variabler to byte-format för att kunna returnera en bytearray med all info denna klassen innehåller
     */
    private byte[] getHeader() {
        byte[] header = new byte[HEADER_SIZE];
        if (options != null)
            header = addOptions(new byte[HEADER_SIZE + options.length]);
        header[0] = (byte)((sourcePort>>8)&255);
        header[1] = (byte)(sourcePort&255);
        header[2] = (byte)((destinationPort>>8)&255);
        header[3] = (byte)(destinationPort&255);
        header[4] = (byte)((sequenceNumber>>24)&255);
        header[5] = (byte)((sequenceNumber>>16)&255);
        header[6] = (byte)((sequenceNumber>>8)&255);
        header[7] = (byte)(sequenceNumber&255);
        header[8] = (byte)((ackNum>>24)&255);
        header[9] = (byte)((ackNum>>16)&255);
        header[10] = (byte)((ackNum>>8)&255);
        header[11] = (byte)(ackNum&255);
        header[12] = (byte)((dataOffset&15)<<4);
        header[13] = (byte)(flags&63);
        header[14] = (byte)((windowSize>>8)&255);
        header[15] = (byte)(windowSize&255);
        header[16] = (byte)((checksum>>8)&255);
        header[17] = (byte)(checksum&255);
        header[18] = (byte)((urgentPointer>>8)&255);
        header[19] = (byte)(urgentPointer&255);
        return header;
    }

    /**
     * Lägger till de icke-nödvändiga "options" till TCPHeader
     * @param header Bytearray innehållandes information enligt formatet för en TCPHeader
     * @return Bytearray där de sista elementen innehåller informatiom om TCPHeaders options
     */
    private byte[] addOptions(byte[] header) {
        for (int i = HEADER_SIZE-1, j = 0; i < header.length; i++, j++)
            header[i] = options[j];
        return header;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public static class TCPHeaderBuilder {
        TCPHeader tcpHeader = new TCPHeader();
        public TCPHeaderBuilder() {
        }

        public TCPHeaderBuilder sourcePort(int port) {
            tcpHeader.sourcePort = port;
            return this;
        }

        public TCPHeaderBuilder destinationPort(int port) {
            tcpHeader.destinationPort = port;
            return this;
        }

        public TCPHeaderBuilder sequenceNumber(long seqNum) {
            tcpHeader.sequenceNumber = seqNum;
            return this;
        }

        public TCPHeaderBuilder flags(byte flags) {
            tcpHeader.flags = flags;
            return this;
        }

        public TCPHeaderBuilder windowSize(int winSize) {
            tcpHeader.windowSize = winSize;
            return this;
        }

        public TCPHeader build() {
            tcpHeader.dataOffset = HEADER_SIZE / 4;
            tcpHeader.checksum = tcpHeader.urgentPointer = 0;
            return tcpHeader;
        }
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public long getAckNum() {
        return ackNum;
    }

    public void setAckNum(long ackNum) {
        this.ackNum = ackNum;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(int dataOffset) {
        this.dataOffset = dataOffset;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public int getUrgentPointer() {
        return urgentPointer;
    }

    public void setUrgentPointer(int urgentPointer) {
        this.urgentPointer = urgentPointer;
    }

    public boolean isFlagOn(TCPFlags flag) {

    }

    public enum TCPFlags {
         ABC(5), B(7);


         TCPFlags(int num) {
             this.
         }
    }
}