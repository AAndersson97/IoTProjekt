package network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class Packet {
    public WifiMacHeader wifiMacHeader;
    public IPHeader ipHeader;
    public UDPHeader udpHeader;
    public WifiMacTrailer wifiMacTrailer;

    public byte[] toBytes() {
        if (wifiMacHeader == null || ipHeader == null ||udpHeader == null|| wifiMacTrailer == null)
            throw new NullPointerException("The headers and trailers must not be null");
        byte[] bytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(wifiMacHeader);
            oos.writeObject(ipHeader);
            oos.writeObject(udpHeader);;
            oos.writeObject(wifiMacTrailer);
            oos.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return bytes;
    }

    public void setWifiMacHeader(WifiMacHeader wifiMacHeader) {
        this.wifiMacHeader = wifiMacHeader;
    }

    public void setWifiMacTrailer(WifiMacTrailer wifiMacTrailer) {
        this.wifiMacTrailer = wifiMacTrailer;
    }

    public abstract Packet copy();
}
