import java.util.Random;

public class LocationCreator implements Constants {
    private static LocationCreator instance ;
    private static Location createdLocations[];

    static {
        createdLocations = new Location[NUM_OF_NODES];
        instance = new LocationCreator();
    }

    /**
     * Skapar ett Location-objekt med unika x- och y-koordinater. Koordinaterna tilldelas ett värde mellan 0 och 10.
     * @return Ett Location-objekt innehållandes x- och y-koordinater.
     */
    public Location createLocation() {
        Location location;
        do {
            location = new Location((int)Math.random()*MAX_COORDINATE,(int)Math.random()*MAX_COORDINATE);
        } while(locationExists(location));

        return location;
    }

    public boolean locationExists(Location location) {
        for (Location location2 : createdLocations) {
            if (location2.x == location.x || location2.y == location.y) {
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
