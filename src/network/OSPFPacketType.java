package network;

public enum OSPFPacketType {
    Hello(1), DD(2), LSR(3), LSU(4), LSAck(5);
    int value;

    OSPFPacketType(int value) {
        this.value = value;
    }
}
