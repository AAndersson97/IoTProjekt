package network;

public interface Constants {
    int MAX_NODES = 20;
    int WINDOW_WIDTH = 660;
    int WINDOW_HEIGHT = 400;
    int ADDRESS_LENGTH = 4;
    int CIRCLE_RADIUS = 20;
    int MAX_NUM_OF_AREAS = 6;
    int MAX_ROUTERS_AREA = 50;
    int IP_PROTOCOL = 4;
    short[] MULTI_CAST = new short[]{224,0,0,5};
    int TIME_OUT = 0;
    int FTP_PORT = 20;
    int OSPF_PORT = 89;
    int MULTICAST_PORT = 6789;
    int ROUTER_UPDATE_RATE = 1000; // I millisekunder
    // Skicka ett Hello-paket varje 5 tidsenheter (sekunder)
    int HELLO_INTERVAL = 5;
    int DEAD_INTERVAL = 10;
    int MAX_XCOORDINATE = WINDOW_WIDTH - 100;
    int MAX_YCOORDINATE = WINDOW_HEIGHT - 100;
}
