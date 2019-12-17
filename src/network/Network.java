package network;

import javafx.concurrent.ScheduledService;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Network implements Constants {
    private static int numOfNodes;
    private static int numOfAreas;
    private static ScheduledExecutorService executor;
    private static ArrayList<Area> areas;

    static {
        numOfAreas = numOfNodes = 0;
        areas = new ArrayList<>();
        // Skapar en gemensam tråd för alla noder att använda för uppgifter som ska utföras regelbundet
        executor = Executors.newScheduledThreadPool(1);
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
        firstAddress[0] = (short)(100 + ((numOfAreas+1)*10));
        firstAddress[1] = firstAddress[2] = firstAddress[3] = 0;
        areas.add(new Area(numOfAreas++,firstAddress, 24));
    }

    private void createAreas() {
        while(numOfAreas < 6) {
            createNewArea();
        }
    }

    public static Area findArea(LocationCreator.Location location) {
        int x = location.getX();
        int y = location.getY();

        if (x > 0 && x < WINDOW_WIDTH/3 && y > 0 && y < (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2)
            return areas.get(2);
        //if (x > 0 && x < (WINDOW_WIDTH/3)/2 && y > (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2 && y < (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2)
        return null;
    }

    public static ScheduledExecutorService getExecutor() {
        return executor;
    }
}
