package network;

final public class GridCell {
    final int col, row;
    public GridCell(int col, int row) {
        this.col = col;
        this.row = row;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GridCell gridCell = (GridCell) o;

        if (col != gridCell.col) return false;
        return row == gridCell.row;
    }
    @Override
    public int hashCode() {
        int result = col;
        result = 31 * result + row;
        return result;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
