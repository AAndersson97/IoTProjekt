package network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class TCPPacket implements Packet{
    private TCPHeader header;
    private byte[] data;

    public TCPPacket(TCPPacket packet) {
        this.header = new TCPHeader(packet.header);
        this.data = Arrays.copyOf(packet.data, packet.data.length);
    }

    public TCPPacket(TCPHeader header, byte[] data) {
        this.header = header;
        this.data = data;
    }

    public int getLength() {
        return header.length() + (data == null ? 0 : data.length);
    }

    public void setHeader(TCPHeader tcpHeader) {
        header = tcpHeader;
    }

    public TCPHeader getHeader() {
        return header;
    }

    public boolean isAckPacket() {
        return header.isFlagOn(TCPHeader.TCPFlags.ACK);
    }

    public boolean isSynPacket() {
        return header.isFlagOn(TCPHeader.TCPFlags.SYN);
    }

    public boolean isFinPacket() {
        return header.isFlagOn(TCPHeader.TCPFlags.FIN);
    }

    public boolean containData() {
        return data != null;
    }

    public byte[] getData() {
        return data;
    }

    public int length() {
        return header.length() + data.length;
    }

    public byte[] toByteArray(){
        byte[] headerBytes = header.toByteArray() ;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
            out.write(headerBytes) ;
            if( this.data != null ) {
                out.write( data ) ;
            }
            return out.toByteArray() ;
        }
        catch(IOException ex){
            System.out.println( ex.toString() ) ;
        }
        return null ;
    }

    public Packet copy() {
        return new TCPPacket(this);
    }
}
