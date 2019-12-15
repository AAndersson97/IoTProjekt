package network;
import java.lang.instrument.Instrumentation;

public class OSPFPacket extends Packet {
    private short version;
    private OSPFPacketType type;
    private int length;

    OSPFPacket() {

    }

}
