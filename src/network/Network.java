package network;

import java.util.ArrayList;

public class Network implements Constants {
    private static int numOfNodes;
    private static int numOfAreas;
    private static ArrayList<Area> areas;

    static {
        numOfAreas = numOfNodes = 0;
        areas = new ArrayList<>();
    }
    private Network() {

    }

    public static void newNodeAdded() {
        numOfNodes++;
    }

    public static int getNumOfNodes() {
        return numOfNodes;
    }

    public static Area createNewArea() {
        if (numOfAreas >= MAX_NUM_OF_AREAS) {
            return findArea();
        }
        short[] firstAddress = new short[4];
        firstAddress[0] = (short)(100 + ((numOfAreas+1)*10));
        firstAddress[1] = firstAddress[2] = firstAddress[3] = 0;
        Area newArea = new Area(numOfAreas++,firstAddress, 24);
        areas.add(newArea);
        return newArea;
    }

    /**
     * Hitta område med lägst antal noder
     */
    public static Area findArea() {
        if (areas.isEmpty())
            return null;
        Area area = areas.get(0);
        for (int i = 1; i < areas.size(); i++)
            if (areas.get(i).getNumOfNodes() < area.getNumOfNodes())
                area = areas.get(i);
        return area;
    }
}
