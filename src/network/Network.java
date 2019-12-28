package network;

import java.io.IOException;
import java.util.ArrayList;
import static network.Constants.GUI.*;
import static network.Constants.AreaBoundaries.*;

public class Network {
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
    public static int getArea(Router router) throws Exception {
        LocationCreator.Location location = router.getLocation();
        int x = location.getX(), y = location.getY(), areaId = 0;
        if ((areaId = area1or2.apply(x,y)) == -1)
            if ((areaId = area3or0.apply(x,y)) == -1)
                if ((areaId = area4or5.apply(x,y)) == -1)
                    throw new Exception("Location out of bounds");
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
    public static void sendPacket(short[] remote, short[] source, Packet packet) {
        if (remote == null || source == null)
            throw new IllegalArgumentException("Source address neither remote address must not be null");
        wifiChannel.send(packet, source);
    }

    public static synchronized void sendMulticast(short[] source, Packet packet) {
        if (source == null)
            throw new IllegalArgumentException("Source address must not be null");
        wifiChannel.send(packet, source);
    }

    public static void shutdownNetwork() {
        for (Router router : wifiChannel.getObservers()) {
            router.turnOff();
        }
    }

    public static ArrayList<Router> getNodeList() {
        return wifiChannel.getObservers();
    }

}
