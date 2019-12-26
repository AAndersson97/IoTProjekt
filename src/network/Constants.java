package network;

import java.net.InetAddress;

public interface Constants {
    int MAX_NODES = 20;
    int WINDOW_WIDTH = 660;
    int WINDOW_HEIGHT = 400;
    int ADDRESS_LENGTH = 4;
    int CIRCLE_RADIUS = 20;
    int MAX_NUM_OF_AREAS = 6;
    int MAX_ROUTERS_AREA = 50;
    String WINDOW_TITLE = "Sybil Simulator";
    short TCP_PROTOCOL = 6;
    short OSPF_PROTOCOL = 89;
    int IP_PROTOCOL = 4;
    byte[] MULTI_CAST = new byte[]{(byte) 0b11111111,0,0,5};
    int TIME_OUT = 0;
    int ROUTER_UPDATE_RATE = 1000; // I millisekunder
    // Skicka ett Hello-paket varje 5 tidsenheter (sekunder)
    int HELLO_INTERVAL = 10000; // 10 sekunder, 10 000 millisekunder
    int DEAD_INTERVAL = 10;
    int MAX_XCOORDINATE = WINDOW_WIDTH - 100;
    int MAX_YCOORDINATE = WINDOW_HEIGHT - 100;
}
