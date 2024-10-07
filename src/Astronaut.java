class Astronaut implements Comparable<Astronaut> {
    private int id;
    private int type;
    private Building currentBuilding;
    private Building targetModule;

    public Astronaut(int id, int type, Building startingBuilding) {
        this.id = id;
        this.type = type;
        this.currentBuilding = startingBuilding;
        this.targetModule = null;
    }

    public void setTargetModule(Building targetModule) {
        this.targetModule = targetModule;
    }

    public void moveTo(Building newBuilding) {
        this.currentBuilding = newBuilding;
    }

    @Override
    public int compareTo(Astronaut other) {
        return Integer.compare(this.id, other.id);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public Building getCurrentBuilding() {
        return currentBuilding;
    }

    public Building getTargetModule() {
        return targetModule;
    }

    public String formatString() {
        return String.format("%d %d %d", id, type, currentBuilding.id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Astronaut && ((Astronaut) obj).id == id;
    }
}
