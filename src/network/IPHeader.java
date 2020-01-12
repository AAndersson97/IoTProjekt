package network;

import utilities.Checksum;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Klassens syfte är att skapa en implementera en IP-header som javaobjekt. Alla fält som vanligtvis förekommer i en IP-header har inkluderas i denna klass, dock sker ej explicit tilldelning av värden till varje fält
 * då samma information redan finns i OLSR-Header eller är inte nödvändiga för att simulera nätverkskommunikation över protokollet OLSR.
 */
public final class IPHeader implements Header, Serializable {
    private static byte version = (byte) 4;
    private short typeOfService;
    private int totalLength;
    private int id;
    private byte flags;
    private short offset;
    private short timeToLive;
    private int protocol;
    private int checksum;
    short[] sourceAddress;
    short[] destinationAddress;
    byte[] options;

    {
        version = 4;
        flags = 2;
        timeToLive = 64;
    }
    public IPHeader(IPHeader header) {
        sourceAddress = Arrays.copyOf(header.sourceAddress, header.sourceAddress.length);
        destinationAddress = Arrays.copyOf(header.destinationAddress, header.sourceAddress.length);
        if (header.getOptions() != null)
            options = Arrays.copyOf(header.options, header.options.length);
        totalLength = header.getTotalLength();
        protocol = header.getProtocol();
        offset = header.offset;
        id = header.id;
        checksum = header.checksum;
        timeToLive = header.timeToLive;
        flags = header.flags;
        typeOfService = header.typeOfService;
    }

    public IPHeader(int dataLength, short[] sourceAddress, short[] destinationAddress, int protocol) {
        this.protocol = protocol;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.totalLength = calculateHeaderLength(this) + dataLength;
        checksum = Checksum.generateChecksum(toByteArray());
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(version);
        out.write(typeOfService);
        out.write(id);
        out.write(totalLength);
        out.write(id);
        out.write(flags);
        out.write(offset);
        out.write(timeToLive);
        out.write(protocol);
        for (short num : sourceAddress)
            out.write(num);
        for (short num : destinationAddress)
            out.write(num);
        if (options != null)
            for (byte option : options)
                out.write(option);

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

    public int getProtocol() {
        return protocol;
    }

    public int getChecksum() {
        return checksum;
    }

    public short[] getSourceAddress() {
        return sourceAddress;
    }

    public short[] getDestinationAddress() {
        return destinationAddress;
    }

    public void decrementTTL() {
        timeToLive--;
    }

    public int getLength() {
        return totalLength;
    }

    public boolean isFragmentOn() {
        return ((flags&2) == 2);
    }
    public boolean isMoreFragmentsOn() {
        return ((flags&1) == 1);
    }

    public void setSourceAddress(short[] sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    @Override
    public Header copy() {
        return new IPHeader(this);
    }

    public static int calculateHeaderLength(IPHeader header) {
        return Short.SIZE * 4 + Integer.SIZE * 3 + Byte.SIZE + (header.sourceAddress.length * Short.SIZE) * 2 + (header.options == null ? 0 : header.options.length * Short.SIZE);
    }

    @Override
    public String toString() {
        return "IPHeader{" +
                "version=" + version +
                ", totalLength=" + totalLength +
                ", protocol=" + protocol +
                ", sourceAdress=" + Arrays.toString(sourceAddress) +
                ", destinationAdress=" + Arrays.toString(destinationAddress) +
                '}';
    }
}
