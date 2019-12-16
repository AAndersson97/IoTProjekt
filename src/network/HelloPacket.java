package network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HelloPacket extends OSPFPacket{
    private OSPFHeader header;
    private int interfaceID;
    private byte priority;
    private short interval;
    private short routerDeadInterval;
    private int designatedRouterId;
    private int backupRouterId;
    private int neighborId;
    private byte[] options;

    HelloPacket(short interval, short deadInterval, OSPFHeader header) {
        interfaceID = designatedRouterId = backupRouterId = neighborId = 0;
        priority = 1;
        this.interval = interval;
        routerDeadInterval = deadInterval;
        this.header = header;
    }
    @Override
    public int length() {
        return 0;
    }
}
