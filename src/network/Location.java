package network;

public class Location {
    private final int x, y;
    private final GridCell gridCell;
    Location(int x, int y, GridCell gridCell) {
        this.x = x;
        this.y = y;
        this.gridCell = gridCell;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public GridCell getGridCell() {
        return gridCell;
    }
}
