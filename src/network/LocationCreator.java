package network;

import java.util.ArrayList;

import static network.Constants.GUI.*;

public class LocationCreator {
    private static LocationCreator instance;
    private static ArrayList<Location> locations;
    private Node latestNode;

    static {
        locations = new ArrayList<>();
        instance = new LocationCreator();
    }

    private LocationCreator() {
        createLocations();
    }

    /**
     * Skapar ett Location-objekt med unika x- och y-koordinater. Koordinaterna tilldelas ett värde mellan 0 och 10.
     * @return Ett Location-objekt innehållandes x- och y-koordinater.
     */
    private void createLocations() {
        for (int y = CIRCLE_RADIUS*5, x = 40; x <  WINDOW_WIDTH; y+=CIRCLE_RADIUS*2) {
            locations.add(new Location(x, y));
            y += CIRCLE_RADIUS;
            if (y > WINDOW_HEIGHT-(BOTTOM_BAR_HEIGHT*2)) {
                y = (CIRCLE_RADIUS*4) + (int)(Math.random()*20);
                x += CIRCLE_RADIUS*4;
            }
        }
    }

    public Location getLocation(Node node) {
        if (latestNode == null) {
            int index = (int)(Math.random() * (locations.size()-1));
            Location location = locations.get(index);
            locations.remove(index);
            latestNode = node;
            return location;
        }
       Location location = findLocationWithinRange();
       locations.remove(location);
       latestNode = node;
       return location;
    }

    public Location findLocationWithinRange() {
        ArrayList<Location> locationsWithinRange = new ArrayList<>();
        for (Location l : locations) {
            if (Transmission.isInsideTransmissionArea(latestNode.getTransmissionRadius(), latestNode.getLocation(), l)) {
                locationsWithinRange.add(l);
            }
        }
        return locationsWithinRange.get((int)(Math.random() * (locationsWithinRange.size() - 1)));
    }

    public static LocationCreator getInstance() {
        return instance;
    }

}
