package network;

import java.net.InetAddress;
import java.util.ArrayList;

public abstract class Packet {
    Packet() {

    }
    // En kopieringskonstruktor
    Packet(Packet packet) {
    }

    public abstract int length();

    public abstract byte[] toByteArray();

    public abstract Packet copy();
}
