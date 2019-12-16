package network;

import utilities.Checksum;

public class IPHeader {
    private static final int HEADER_SIZE = 20;
    private static final int TCP_PROTOCOL = 6;
    private byte version;
    private byte headerLength;
    private short typeOfService;
    private int totalLength;
    private int id;
    private byte flags;
    private short offset;
    private short timeToLive;
    private short protocol;
    private int checksum;
    byte[] sourceAdress = new byte[4];
    byte[] destinationAdress = new byte[4];
    byte[] options;

    public IPHeader(byte[] baseHeader) {
        if (baseHeader.length == HEADER_SIZE) {
            version = (byte)((baseHeader[0]>>4)&15);
            headerLength = (byte)(baseHeader[0]&15);
            typeOfService = (short)(baseHeader[1]&63);
            totalLength = (((baseHeader[2]<<8)&65280)|(baseHeader[3]&255));
            id = (((baseHeader[4]<<8)&65280)|(baseHeader[5]&255));
            flags = (byte)((baseHeader[6]>>5)&7);
            offset = (short)(baseHeader[6]&31);
            timeToLive = (short)(baseHeader[8]&63);
            protocol = (short)(baseHeader[9]&63);
            checksum = (((baseHeader[10]<<8)&65280)|(baseHeader[11]&255));
            sourceAdress[0] = baseHeader[12];
            sourceAdress[1] = baseHeader[13];
            sourceAdress[2] = baseHeader[14];
            sourceAdress[3] = baseHeader[15];
            destinationAdress[0] = baseHeader[16];
            destinationAdress[1] = baseHeader[17];
            destinationAdress[2] = baseHeader[18];
            destinationAdress[3] = baseHeader[19];
        }
        else{
            throw new RuntimeException("Failed to create a IP Header") ;
        }
    }

    public IPHeader(int length, byte[] sourceAdress, byte[] destinationAdress) {
        this.version = 4;
        this.headerLength = 5;
        this.totalLength = headerLength + length;
        this.typeOfService = 0;
        this.id = offset = 0;
        this.flags = 2;
        this.timeToLive = 64;
        this.protocol = TCP_PROTOCOL;
        //this.checksum = Checksum.generateChecksum();
        this.sourceAdress = sourceAdress;
        this.destinationAdress = destinationAdress;
    }

    public byte[] toByteArray() {
        return null;
    }

    public byte[] getHeader() {
        byte[] header = new byte[HEADER_SIZE];
        header[0] = (byte)(((version&15)<<4)|(headerLength&15));
        header[1] = (byte)(typeOfService&255);
        header[2] = (byte)((totalLength>>8)&255);
        header[3] = (byte)(totalLength&255);
        header[4] = (byte)((id>>8)&255);
        header[5] = (byte)(id&255);
        header[6] = (byte)(((flags&7)<<5)|((offset>>8)&31));
        header[7] = (byte)(offset&255);
        header[8] = (byte)(timeToLive&255);
        header[9] = (byte)(protocol&255);
        header[10] = (byte)((checksum>>8)&255);
        header[11] = (byte)(checksum&255);
        header[12] = sourceAdress[0];
        header[13] = sourceAdress[1];
        header[14] = sourceAdress[2];
        header[15] = sourceAdress[3];
        header[16] = destinationAdress[0];
        header[17] = destinationAdress[1];
        header[18] = destinationAdress[2];
        header[19] = destinationAdress[3];
        return header;
    }

    public byte[] getOptions() {
        return options;
    }

    public void setOptions(byte[] options) {
        this.options = options;
    }

    public byte getVersion() {
        return version;
    }

    public byte getHeaderLength() {
        return headerLength;
    }

    public short getTypeOfService() {
        return typeOfService;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public int getId() {
        return id;
    }

    public byte getFlags() {
        return flags;
    }

    public short getOffset() {
        return offset;
    }

    public short getTimeToLive() {
        return timeToLive;
    }

    public short getProtocol() {
        return protocol;
    }

    public int getChecksum() {
        return checksum;
    }

    public byte[] getSourceAdress() {
        return sourceAdress;
    }

    public byte[] getDestinationAdress() {
        return destinationAdress;
    }

    public boolean isFragmentOn() {
        return ((flags&2) == 2);
    }
    public boolean isMoreFragmentsOn() {
        return ((flags&1) == 1);
    }
}
