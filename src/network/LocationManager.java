package network;

import java.util.*;

import static network.Constants.GUI.*;

public class LocationManager {
    private static LocationManager instance;
    private static HashMap<GridCell,Location> locations;
    // Programmet är uppdelat i rutnät
    private static int numOfCols, numOfRows;
    private static final int cellHeight = CIRCLE_RADIUS*5;
    private static final int cellWidth = 40;

    static {
        locations = new HashMap<>();
        instance = new LocationManager();
    }

    private LocationManager() {
        createLocations();
    }

    /**
     * Skapar ett Location-objekt med unika x- och y-koordinater. Koordinaterna tilldelas ett värde mellan 0 och 10.
     * @return Ett Location-objekt innehållandes x- och y-koordinater.
     */
    private void createLocations() {
        int col = 0, row = 0;
        for (int y = cellHeight, x = cellWidth; x <  WINDOW_WIDTH; y+=CIRCLE_RADIUS*2) {
            GridCell gridCell = new GridCell(col, row++);
            locations.put(gridCell, new Location(x, y, gridCell));
            y += CIRCLE_RADIUS;
            if (y > WINDOW_HEIGHT-(BOTTOM_BAR_HEIGHT*2)) {
                y = (CIRCLE_RADIUS*4) + (int)(Math.random()*20);
                x += CIRCLE_RADIUS*4;
                col++;
                numOfRows = row;
                row = 0;
            }
        }
        numOfCols = col;
    }

    public Location getLocation() {
        if (Network.getNumOfNodes() == 0) {
            int col = numOfCols / 2;
            int row = numOfRows / 2;
            GridCell key = new GridCell(col, row);
            Location location = locations.get(key);
            locations.remove(key);
            return location;
        }
       Location location = findLocationWithinRange();
       locations.remove(location.getGridCell());
       return location;
    }

    private Location findLocationWithinRange() {
        int index = (int)(Math.random() * (Network.getNumOfNodes()-1));
        Node neighbor = Network.getNodeList().get(index);
        ArrayList<Location> locationsWithinRange = new ArrayList<>();
        for (Location l : locations.values()) {
            if (Transmission.isInsideTransmissionArea(neighbor.getTransmissionRadius(), neighbor.getLocation(), l)) {
                locationsWithinRange.add(l);
            }
        }
        return locationsWithinRange.get((int) (Math.random() * (locationsWithinRange.size()-1)));
    }

    public void returnLocation(Location location) {
        locations.put(location.getGridCell(), location);
    }

    /**
     * Hitta kandidatnoder att attackera. En kandidatnod har minst fyra lediga fysiska platser runt sig.
     * @param blacklist Lista av noder som ej ska attackeras, i första hand attacknoden och sybilnoder.
     * @return
     */
    public AttackData findNodeToAttack(List<Node> blacklist) {
        ArrayList<Node> candidates = Network.getNodeList();
        candidates.removeIf(blacklist::contains);
        ArrayList<Location> list = new ArrayList<>();
        for (Node candidate : candidates) {
            GridCell gridCell = candidate.getLocation().getGridCell();
            int colMin = gridCell.col-1, rowMin = gridCell.row-1;
            for (int col = colMin, row = rowMin; col < colMin+3;row++) {
                if (col == gridCell.col && row == gridCell.row)
                    continue;
                Location location = locations.get(new GridCell(col, row));
                if (location != null)
                    list.add(location);
                if (row == rowMin + 2) {
                    row = rowMin;
                    col++;
                }
            }
            if (list.size() >= 4) {
                return new AttackData(candidate, list);
            }
            else
                list.clear();
        }
        return null;
    }

    public static LocationManager getInstance() {
        return instance;
    }

    public class AttackData {
        Node node;
        ArrayList<Location> locations;
        public AttackData(Node node, ArrayList<Location> locations) {
            this.node = node;
            this.locations = locations;
        }
    }
}
