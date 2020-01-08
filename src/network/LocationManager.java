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
    private Node latestNode;

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

    public Location getLocation(Node node) {
        if (latestNode == null) {
            int col = (int)(Math.random() * (numOfCols));
            int row = (int)(Math.random() * (numOfRows));
            GridCell key = new GridCell(col, row);
            Location location = locations.get(key);
            locations.remove(key);
            latestNode = node;
            return location;
        }
       Location location = findLocationWithinRange();
       locations.remove(location.getGridCell());
       latestNode = node;
       return location;
    }

    public Location findLocationWithinRange() {
        ArrayList<Location> locationsWithinRange = new ArrayList<>();
        for (Location l : locations.values()) {
            if (Transmission.isInsideTransmissionArea(latestNode.getTransmissionRadius(), latestNode.getLocation(), l)) {
                locationsWithinRange.add(l);
            }
        }
        return locationsWithinRange.get((int) (Math.random() * (locationsWithinRange.size()-1)));
    }


    /**
     * Hitta kandidatnoder att attackera. En kandidatnod har minst fyra lediga fysiska platser runt sig.
     * @param blacklist Lista av noder som ej ska attackeras, i första hand attacknoden och sybilnoder.
     * @return
     */
    public ArrayList<Location> findLocationToAttack(List<Node> blacklist) {
        ArrayList<Node> candidates = new ArrayList<>(Network.getNodeList().values());
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
                System.out.println(Arrays.toString(candidate.getAddress()));
                break;
            }
            else
                list.clear();
        }
        return list;
    }

    /*public ArrayList<Location> getLocationWithinRange(short[] addr) {
        Node node = Network.getNodeList().get(addr);
        if (node == null)
            throw new NullPointerException("There exists no node with the specified address");
        ArrayList<Location> locationsWithinRange = new ArrayList<>();
        for (Location l : locations) {
            if (Transmission.isInsideTransmissionArea(node.getTransmissionRadius(), node.getLocation(), l)) {
                locationsWithinRange.add(l);
            }
        }
        locations.removeAll(locationsWithinRange);
        return locationsWithinRange;
    }*/

    public static LocationManager getInstance() {
        return instance;
    }



}
