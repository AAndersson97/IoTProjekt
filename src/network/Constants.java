package network;

public final class Constants {
    public static final boolean LOG_ACTIVE = false;
    private Constants() {}
    public static final class GUI {
        public static final boolean SHOW_PACKET_DROPPED = false;
        public static final int WINDOW_WIDTH = 660;
        public static final int WINDOW_HEIGHT = 500;
        public static final int CIRCLE_RADIUS = 10;
        public static final int BOTTOM_BAR_HEIGHT = 50;
        public static final int PACKET_GUI_X = 1092;
        public static final int PACKET_GUI_Y = 160;
        public static final String WINDOW_TITLE = "Sybil Simulator";
    }
    public static final class Node {
        public static final int MAX_NODES = 20;
        public static final int BUFFER_CAPACITY = 1000;
        public static final int NUM_OF_SYBIL = 3;
    }
    public static final class Protocol {
        public static final int HELLO_INTERVAL = 4000; // 4000 millisekunder
        public static final int ADDRESS_LENGTH = 4; // 4 bytes per IP-adress
        public static final int TC_INTERVAL = 4000;
        public static final int TOP_HOLD_TIME = 15000;
        public static final int TC_HOLD_TIME = TC_INTERVAL * 3;
        public static final int TWO_HOP_HOLD_TIME = HELLO_INTERVAL * 3;
        public static final int LINK_HOLD_TIME = HELLO_INTERVAL * 3; // en tuppel innehållandes information om viss länk ska vara giltig tillräcklig länge för att grannoden ska hinna skicka nytt hellopaket plus viss tidsmarginal
        public static final int NEIGHB_HOLD_TIME = LINK_HOLD_TIME;
        public static final int MPR_SELECTOR_HOLD_TIME = HELLO_INTERVAL * 3;
        public static final int UDP_HEADER_SIZE = Short.SIZE * 3 + Integer.SIZE;
        public static final int UDP_PROTOCOL_NUM = 17;
        public static final short OLSR_PORT = 698;
        public static final short TFTP_PORT = 69;
        public static final short OLSR_HEADER_SIZE = Integer.SIZE * 2;
        public static final long MAX_JITTER_MS = (HELLO_INTERVAL/4); // jitter används för att variera intervallet när meddelanden skickas ut med syfte att undvika synkronisering

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
        public static final int PACKET_LOSS = 1;   // procent av antalet paket som tappas på vägen på grund av nätverksstörningar
        public static final short[] BROADCAST = new short[]{255,255,255,255};
        public static final short[] FIRST_ADDRESS = new short[]{110,0,0,1};
    }
}
