package old;

import network.Location;

import java.util.ArrayList;

import static network.Constants.GUI.*;

public class LocationCreator {
    private static network.LocationCreator instance ;
    private static ArrayList<Location> locations;

    static {
        locations = new ArrayList<>();
        instance = new network.LocationCreator();
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
            if (y > WINDOW_HEIGHT-(CIRCLE_RADIUS*6)) {
                y = (CIRCLE_RADIUS*4) + (int)(Math.random()*20);
                x += CIRCLE_RADIUS*4;
            }
        }
    }

    public Location getLocation() {
        int index = (int)(Math.random() * (locations.size()-1));
        Location location = locations.get(index);
        locations.remove(index);
        return location;
    }

    public static network.LocationCreator getInstance() {
        return instance;
    }

}
