package network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;

public class Network implements Constants {
    private static int numOfNodes;
    private static int numOfAreas;
    private static WifiChannel wifiChannel;
    private static ArrayList<Area> areas;

    static {
        numOfAreas = numOfNodes = 0;
        areas = new ArrayList<>();
        wifiChannel = new WifiChannel();
        createAreas();
    }
    private Network() {
    }

    public static void newNodeAdded() {
        numOfNodes++;
    }

    public static int getNumOfNodes() {
        return numOfNodes;
    }

    private static void createNewArea() {
        short[] firstAddress = new short[4];
        firstAddress[0] = (short)(numOfAreas == 0? 0 : 100 + ((numOfAreas+1)*10));
        firstAddress[1] = firstAddress[2] = firstAddress[3] = 0;
        areas.add(new Area(numOfAreas++,firstAddress, 24));
    }

    private static void createAreas() {
        while(numOfAreas < 6) {
            createNewArea();
        }
    }

    /**
     * Returnerar ett områdes id till en nod baserad på nodens position
     * @param router En nod i nätverket som saknar område
     */
    public static int getArea(Router router) {
        LocationCreator.Location location = router.getLocation();
        int x = location.getX(), y = location.getY(), areaId = 0;
        if (x >= 0 && x < WINDOW_WIDTH/3 && y >= 0 && y < (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2)
            areaId = 2;
        else if (x >= 0 && x < WINDOW_WIDTH/3 && y >= (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2)
            areaId = 1;
        else if (x >= WINDOW_WIDTH/3 && x < (2*WINDOW_WIDTH)/3 && y >= 0 && y < (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2)
            areaId = 3;
        //else if (x >= WINDOW_WIDTH/3 && x < (2*WINDOW_WIDTH)/3 && y >= (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2)
        else if (x >= (2*WINDOW_WIDTH)/3 && y >= 0 && y <= (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2)
            areaId = 4;
        else if (x >= (2*WINDOW_WIDTH)/3 && y > (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2)
            areaId = 5;
        areas.get(areaId).addNode(router);
        wifiChannel.addObserver(router);
        return areaId;
    }

    public static Area getArea(int id) {
        if (id < 0 || id >= areas.size())
            throw new IllegalArgumentException("Area id is out of bounds, min: 0 max: " + (areas.size()-1));
        return areas.get(id);
    }

    /**
     *
     * @param source Avsändarens IP-address
     * @param remote Mottagarens IP-address
     * @param packet Paketet som ska skickas
     * @throws IOException Om paketsändningen misslyckades kastas ett IOException
     */
    public static synchronized void sendPacket(InetAddress remote, InetAddress source, Packet packet) throws IOException {
        if (remote == null ||source == null)
            throw new IllegalArgumentException("Source address neither remote address must not be null");
        wifiChannel.send(packet);
    }

}
