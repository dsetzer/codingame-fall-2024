abstract class Building {
    protected int id;
    protected int x;
    protected int y;
    protected boolean hasTeleporter;

    public Building(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.hasTeleporter = false;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasTeleporter() {
        return hasTeleporter;
    }

    public void setHasTeleporter(boolean hasTeleporter) {
        this.hasTeleporter = hasTeleporter;
    }
}
