package network;

import java.util.ArrayList;
import java.util.Random;

public class LocationCreator implements Constants {
    private static LocationCreator instance ;
    private static ArrayList<Location> createdLocations;

    static {
        createdLocations = new ArrayList<>();
        instance = new LocationCreator();
    }

    /**
     * Skapar ett Location-objekt med unika x- och y-koordinater. Koordinaterna tilldelas ett värde mellan 0 och 10.
     * @return Ett Location-objekt innehållandes x- och y-koordinater.
     */
    public Location createLocation() {
        Location location;
        do {
            location = new Location((int) (Math.random() * MAX_XCOORDINATE), (int) (Math.random() * MAX_YCOORDINATE));
        } while(locationExists(location));

        return location;
    }

    public boolean locationExists(Location location) {
        for (Location l : createdLocations) {
            if (l.x == location.x || l.y == location.y) {
                return true;
            }
        }

        return false;
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
    }

}
