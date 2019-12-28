package network;

import java.util.ArrayList;
import java.util.Random;
import static network.Constants.GUI.*;

public class LocationCreator {
    private static LocationCreator instance ;
    private static ArrayList<Location> locations;

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

    public static LocationCreator getInstance() {
        return instance;
    }

    public class Location {
        private int x, y;
        Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

}
