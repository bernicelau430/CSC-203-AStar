public final class Viewport {
    private int row;
    private int col;
    private int numRowsViewport;
    private int numColsViewport;

    public Viewport(int numRows, int numCols) {
        this.numRowsViewport = numRows;
        this.numColsViewport = numCols;
    }

    public Point worldToViewport(int col, int row) {
        return new Point(col - this.col, row - this.row);
    }

    public Point viewportToWorld(int col, int row) {
        return new Point(col + this.col, row + this.row);
    }

    public boolean contains(Point p) {
        return p.getY() >= row && p.getY() < row + numRowsViewport && p.getX() >= col && p.getX() < col + numColsViewport;
    }

    public void shift(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getRow() {return this.row;}

    public int getCol() {return this.col;}
    public int getNumRowsViewport() {return this.numRowsViewport;}
    public int getNumColsViewport() {return this.numColsViewport;}

}
