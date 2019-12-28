package network;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static network.Constants.GUI.*;

public final class Constants {
    private Constants() {}
    public static final class GUI {
        public static final int WINDOW_WIDTH = 660;
        public static final int WINDOW_HEIGHT = 400;
        public static final int CIRCLE_RADIUS = 20;
        public static final String WINDOW_TITLE = "Sybil Simulator";
    }
    public static final class Node {
        public static final int MAX_NODES = 20;
        public static final int ADDRESS_LENGTH = 4;
        public static final int MAX_NUM_OF_AREAS = 6;
        public static final int MAX_ROUTERS_AREA = 50;
        public static final short TCP_PROTOCOL = 6;
        public static final short OSPF_PROTOCOL = 89;
        public static final int IP_PROTOCOL = 4;
        public static final short[] MULTI_CAST = new short[]{255,0,0,5};
        public static final int TIME_OUT = 0;
        public static final int ROUTER_UPDATE_RATE = 1000; // I millisekunder
        // Skicka ett Hello-paket varje 5 tidsenheter (sekunder)
        public static final int HELLO_INTERVAL = 10000; // 10 sekunder, 10 000 millisekunder
        public static final int DEAD_INTERVAL = 10;
        public static final int DEFAULT_WIN_SIZE = 65535; // antalet bytes, win size talar om för avsändaren hur mycket data kan skickas per gång
        public static final int MAX_XCOORDINATE = GUI.WINDOW_WIDTH - 100;
        public static final int MAX_YCOORDINATE = GUI.WINDOW_HEIGHT - 100;
    }
    public static final class AreaBoundaries {
        public static final BiFunction<Integer, Integer, Integer> area1or2 = (x, y) -> x >= 0 && x < WINDOW_WIDTH/3 ? (y >= (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2 ? 1 : 2 ) : -1;
        public static final BiFunction<Integer, Integer, Integer> area4or5 = (x,y) -> x >= (2*WINDOW_WIDTH)/3 ? (y > (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2? 5: 4) : -1;
        public static final BiFunction<Integer, Integer, Integer> area3or0 = (x,y) -> (x >= WINDOW_WIDTH/3 && x < (2*WINDOW_WIDTH)/3)? (y < (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2? 3 : 0) : -1;
    }
}
