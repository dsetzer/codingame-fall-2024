class Tube {
    private Building start;
    private Building end;
    private int capacity;
    private int baseCost;
    private int upgradeCost;
    private double distance;

    public Tube(Building start, Building end, int capacity) {
        this.start = start;
        this.end = end;
        this.capacity = capacity;
        this.baseCost = calculateCost();
        this.upgradeCost = baseCost;
        this.distance = calculateDistance();
    }

    private double calculateDistance() {
        return Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2));
    }

    private int calculateCost() {;
        return (int) (distance * 10); // 1 resource per 0.1km
    }

    public void upgrade() {
        capacity++;
        upgradeCost += baseCost * capacity;
    }

    // Getters and setters
    public Building getStart() {
        return start;
    }

    public Building getEnd() {
        return end;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getBaseCost() {
        return baseCost;
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public double getDistance() {
        return distance;
    }

    public void setStart(Building start) {
        this.start = start;
    }

    public void setEnd(Building end) {
        this.end = end;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
