package network;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Network implements Constants {
    private static int numOfNodes;
    private static int numOfAreas;
    private static ArrayList<Area> areas;

    static {
        numOfAreas = numOfNodes = 0;
        areas = new ArrayList<>();
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

    // Att göra:
    /**
     * Returnerar ett områdes id till en nod baserad på nodens position
     * @param router En nod i nätverket som saknar område
     */
    public static int getArea(Router router) {
        LocationCreator.Location location = router.getLocation();
        int x = location.getX(), y = location.getY(), areaId = 0;
        //System.out.println("(WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2: " + (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2);
        //System.out.println("WINDOW_WIDTH/3: " + WINDOW_WIDTH/3);
        //System.out.println("(2*WINDOW_WIDTH)/3: " + (2*WINDOW_WIDTH)/3);
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
        return areaId;
    }
}
