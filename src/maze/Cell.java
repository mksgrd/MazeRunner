package maze;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Cell implements Serializable {
    private final int row;
    private final int col;
    private static final int[] d = {-1, 0, 1, 0};

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return row == cell.row &&
                col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public List<Cell> getNeighbors(int rowLowerBound, int colLowerBound, int rowUpperBound, int colUpperBound, int maxDistance) {
        List<Cell> neighbors = new LinkedList<>();
        for (int i = 0; i < d.length; ++i) {
            int nRow = Math.min(Math.max(rowLowerBound, row + d[i] * maxDistance), rowUpperBound);
            int nCol = Math.min(Math.max(colLowerBound, col + d[d.length - i - 1] * maxDistance), colUpperBound);
            if (!(row == nRow && col == nCol)) {
                neighbors.add(new Cell(nRow, nCol));
            }
        }
        return neighbors;
    }
}
