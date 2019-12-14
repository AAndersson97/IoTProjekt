package network;

public class IPHeader implements Constants {

    private byte version;
    private byte headerLength;
    private short tos;
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
            ihl = (byte)(baseHeader[0]&15);
            tos = (short)(baseHeader[1]&63);
            length = (int)(((baseHeader[2]<<8)&65280)|(baseHeader[3]&255));
            id = (int)(((baseHeader[4]<<8)&65280)|(baseHeader[5]&255));
            flags = (byte)((baseHeader[6]>>5)&7);
            offset = (short)(baseHeader[6]&31);
            ttl = (short)(baseHeader[8]&63);
            protocol = (short)(baseHeader[9]&63);
            checksum = (int)(((baseHeader[10]<<8)&65280)|(baseHeader[11]&255));
            source_address[0] = baseHeader[12];
            source_address[1] = baseHeader[13];
            source_address[2] = baseHeader[14];
            source_address[3] = baseHeader[15];
            destination_address[0] = baseHeader[16];
            destination_address[1] = baseHeader[17];
            destination_address[2] = baseHeader[18];
            destination_address[3] = baseHeader[19];
        }
        else{
            throw new RuntimeException("Error in creating bash header for TCPPacket") ;
        }
    }
}
