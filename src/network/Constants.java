package network;

import java.util.LinkedList;

public final class Constants {
    public static final boolean LOG_ACTIVE = false;
    private Constants() {}
    public static final class GUI {
        public static final int WINDOW_WIDTH = 660;
        public static final int WINDOW_HEIGHT = 500;
        public static final int CIRCLE_RADIUS = 10;
        public static final int BOTTOM_BAR_HEIGHT = 50;
        public static final int PACKET_GUI_X = 1092;
        public static final int PACKET_GUI_Y = 160;
        public static final String WINDOW_TITLE = "Sybil Simulator";
    }
    public static final class Node {
        public static final int MAX_NODES = 30;
        public static final int MAX_NUM_OF_AREAS = 6;
        public static final int MAX_ROUTERS_AREA = 50;
        public static final int TIME_OUT = 0;
        public static final int ROUTER_UPDATE_RATE = 1000; // I millisekunder
        // Skicka ett Hello-paket varje 5 tidsenheter (sekunder)
        public static final int DEFAULT_WIN_SIZE = 65535; // antalet bytes, win size talar om för avsändaren hur mycket data kan skickas per gång
        public static final int MAX_XCOORDINATE = GUI.WINDOW_WIDTH - 100;
        public static final int MAX_YCOORDINATE = GUI.WINDOW_HEIGHT - 100;
        public static final int NUM_OF_SYBIL = 3;
    }
    public static final class Protocol {
        public static final float SCALING_FACTOR = (float) (1/16.0); // 0.0625 sekunder
        public static final int HELLO_INTERVAL = 2000; // 2000 millisekunder
        public static final int ADDRESS_LENGTH = 4; // 4 bytes per IP-adress
        public static final int REFRESH_INTERVAL = 2000;
        public static final int TC_INTERVAL = 5000;
        public static final int NEIGHB_HOLD_TIME = 6000;
        public static final int TOP_HOLD_TIME = 15000;
        public static final int DUP_HOLD_TIME = 30000;
        public static final int TCP_HOLD_TIME = 3000;
        public static final int TWO_HOP_HOLD_TIME = 6000;
        public static final int LINK_HOLD_TIME = 4000;
        public static final int MPR_SELECTOR_HOLD_TIME = 4000;
        public static final int MID_HOLD_TIME = 15000;
        public static final int UDP_HEADER_SIZE = Short.SIZE * 3 + Integer.SIZE;
        public static final int UDP_PROTOCOL_NUM = 17;
        public static final int OLSR_MIN_LENGTH = 16;
        public static final short OLSR_PORT = 698;
        public static final short OLSR_HEADER_SIZE = Integer.SIZE * 2;
        public static final int NUM_ACTIVE_MSG_TYPES = 3;
        public static final int DEFAULT_TTL = 255;
        public static final double MAX_JITTER = HELLO_INTERVAL/4.0; // jitter används för att variera intervallet när meddelanden skickas ut med syfte att undvika synkronisering
        public static final long MAX_JITTER_MS = (long) ((HELLO_INTERVAL/4.0) * 1000);

        /** Betecknar hur villig en nod är att vidarebefodra traffik för andra noder.
         *  WILL_NEBER indikerar en nod som inte vill hantera traffik för andra noder, oftast p.g.a resursbrist.
         *  WILL_ALWAYS indikerar att en nod alltid ska väljas för att vidarebefodra traffik, ofast p.g.a resursöverflöd.
         *  En nod kan dynamiskt ändra villighet när förutsättningarna att vidarebefodra traffik ändras.
         */
        public enum Willingness {
            WILL_NEVER(0), WILL_LOW(1), WILL_DEFAULT(3), WILL_HIGH(6), WILL_ALWAYS(7);
            int value;
            Willingness(int value) {
                this.value = value;
            }
        }

    }
    public static final class Network {
        public static final int PACKET_LOSS = 1;
        public static final short[] BROADCAST = new short[]{255,255,255,255};
        public static final short[] FIRST_ADDRESS = new short[]{110,0,0,1};
    }
    public static final class Simulation {
    }
}
