package network;

import java.util.LinkedList;

public final class Constants {
    public static final boolean LOG_ACTIVE = true;
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
        public static final int ADDRESS_LENGTH = 4;
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
        public static final int SCALING_FACTOR = 1/16; // 0.0625 sekunder
        public static final int HELLO_INTERVAL = 2; // 2 sekunder
        public static final int REFRESH_INTERVAL = 2;
        public static final int TC_INTERVAL = 5;
        public static final int NEIGHB_HOLD_TIME = 6;
        public static final int TOP_HOLD_TIME = 15;
        public static final int DUP_HOLD_TIME = 30;
        public static final int MID_HOLD_TIME = 15;
        public static final int HNA_HOLD_TIME = 15;
        public static final int UDP_HEADER_SIZE = 8;
        public static final int OLSR_MIN_LENGTH = 16;
        public static final int OLSR_PORT = 698;

        public enum LinkTypes {
            UNSPEC_LINK(0), ASYM_LINK(1), SYM_LINK(2), LOST_LINK(3);
            int value;
            LinkTypes(int value) {
                this.value = value;
            }
        }
        public enum NeighborTypes {
            NOT_NEIGH(0), SYM_NEIGH(1), MPR_NEIGH(2);
            int value;
            NeighborTypes(int value) {
                this.value = value;
            }
        }

        /** Betecknar hur villig en nod är att vidarebefodra traffik för andra noder.
         *  WILL_NEBER indikerar en nod som inte vill hantera traffik för andra noder, oftast p.g.a resursbrist.
         *  WILL_ALWAYS indikerar att en nod alltid ska väljas för att vidarebefodra traffik, ofast p.g.a resursöverflöd.
         *  En nod kan dynamiskt ändra villighet när förutsättningarna att vidarebefodra traffik ändras.
         */
        public enum Willingness {
            WILL_NEVER(0), WILL_LOW(1), WILL_DEFAULT(3), WILL_HIGH(6);
            int value;
            Willingness(int value) {
                this.value = value;
            }
        }

    }
    public static final class Network {
        public static final int PACKET_LOSS = 1;
        public static final short[] MULTI_CAST = new short[]{255,0,0,5};
        public static final short[] FIRST_ADDRESS = new short[]{110,0,0,1};
    }
    public static final class Simulation {
    }
}
