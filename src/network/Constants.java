package network;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static network.Constants.GUI.*;

public final class Constants {
    public static final boolean LOGGING = true;
    private Constants() {}
    public static final class GUI {
        public static final int WINDOW_WIDTH = 660;
        public static final int WINDOW_HEIGHT = 500;
        public static final int CIRCLE_RADIUS = 10;
        public static final String WINDOW_TITLE = "Sybil Simulator";
    }
    public static final class Node {
        public static final int MAX_NODES = 30;
        public static final int ADDRESS_LENGTH = 4;
        public static final int MAX_NUM_OF_AREAS = 6;
        public static final int MAX_ROUTERS_AREA = 50;
        public static final int TIME_OUT = 0;
        public static final int ROUTER_UPDATE_RATE = 1000; // I millisekunder
        // Skicka ett Hello-paket varje 5 tidsenheter (sekunder)
        public static final int DEFAULT_WIN_SIZE = 65535; // antalet bytes, win size talar om för avsändaren hur mycket data kan skickas per gång
        public static final int MAX_XCOORDINATE = GUI.WINDOW_WIDTH - 100;
        public static final int MAX_YCOORDINATE = GUI.WINDOW_HEIGHT - 100;
        public static final int BUFFER_SIZE = 1024;
        public static final int NUM_OF_SYBIL = 3;
    }
    public static final class Protocol {
        public static final int SCALING_FACTOR = 1/16; // 0.0625 sekunder
        public static final int HELLO_INTERVAL = 2; // 2 sekunder
        public static final int REFRESH_INTERVAL = 2;
        public static final int TC_INTERVAL = 5;
    }
    public static final class Network {
        public static final int PACKET_LOSS = 1;
        public static final short[] MULTI_CAST = new short[]{255,0,0,5};
        public static final short[] FIRST_ADDRESS = new short[]{110,0,0,1};
    }
}
