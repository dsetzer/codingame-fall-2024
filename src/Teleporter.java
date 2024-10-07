class Teleporter {
    public static final int COST = 5000;

    private final Building entrance;
    private final Building exit;

    public Teleporter(Building entrance, Building exit) {
        this.entrance = entrance;
        this.exit = exit;
        entrance.setHasTeleporter(true);
        exit.setHasTeleporter(true);
    }

    // Getters and setters
    public Building getEntrance() {
        return entrance;
    }

    public Building getExit() {
        return exit;
    }

    @Override
    public String toString() {
        return "TELEPORT " + entrance.getId() + " " + exit.getId();
    }
}
