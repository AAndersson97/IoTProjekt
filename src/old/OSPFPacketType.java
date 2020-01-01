package old;

public enum OSPFPacketType {
    Hello(1), DD(2), LSR(3), LSU(4), LSAck(5);
    private final int value;

    OSPFPacketType(final int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
