package network;

import utilities.Checksum;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class IPHeader extends Header {
    private static final int HEADER_SIZE = 20;
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
    short[] sourceAdress;
    short[] destinationAdress;
    byte[] options;

    {
        version = 4;
        headerLength = 5;
        flags = 2;
        timeToLive = 64;
    }
    public IPHeader(IPHeader header) {
        sourceAdress = Arrays.copyOf(header.sourceAdress, header.sourceAdress.length);
        destinationAdress = Arrays.copyOf(header.destinationAdress, header.sourceAdress.length);
        if (header.getOptions() != null)
            options = Arrays.copyOf(header.options, header.options.length);
        totalLength = header.getTotalLength();
        protocol = header.getProtocol();
        offset = header.offset;
        id = header.id;
        checksum = header.checksum;
    }

    public IPHeader(int length, short[] sourceAdress, short[] destinationAdress, short protocol) throws IOException {
        this.totalLength = headerLength + length;
        this.protocol = protocol;
        this.sourceAdress = sourceAdress;
        this.destinationAdress = destinationAdress;
        checksum = Checksum.generateChecksum(toByteArray());
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(version);
        out.write(headerLength);
        out.write(typeOfService);
        out.write(id);
        out.write(totalLength);
        out.write(id);
        out.write(flags);
        out.write(offset);
        out.write(timeToLive);
        out.write(protocol);
        out.write(checksum);
        for (short num : sourceAdress)
            out.write(num);
        for (short num : destinationAdress)
            out.write(num);
        if (options != null)
            out.write(options);

        return out.toByteArray();
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

    public short[] getSourceAdress() {
        return sourceAdress;
    }

    public short[] getDestinationAdress() {
        return destinationAdress;
    }

    public int getLength() {
        return totalLength - headerLength;
    }

    public boolean isFragmentOn() {
        return ((flags&2) == 2);
    }
    public boolean isMoreFragmentsOn() {
        return ((flags&1) == 1);
    }

    @Override
    public Header copy() {
        return new IPHeader(this);
    }
}
