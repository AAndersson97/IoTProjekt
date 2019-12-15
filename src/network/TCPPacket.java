package network;

public class TCPPacket {

    private TCPHeader header;
    private byte[] data;

    public TCPPacket(TCPHeader header) {
        this.header = header;
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

}
