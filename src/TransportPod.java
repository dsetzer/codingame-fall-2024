import java.util.*;

class TransportPod {
    public static final int COST = 1000;
    public static final int DECONSTRUCTION_REFUND = 750;
    public static final int CAPACITY = 10;

    private int id;
    private List<Building> route;
    private int currentPosition;
    private List<Astronaut> passengers;

    public TransportPod(int id, List<Building> route) {
        this.id = id;
        this.route = route;
        this.currentPosition = 0;
        this.passengers = new ArrayList<>();
    }

    public void move() {
        currentPosition = (currentPosition + 1) % route.size();
    }

    public void resetToStart() {
        currentPosition = 0;
        passengers.clear();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public List<Building> getRoute() {
        return route;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public List<Astronaut> getPassengers() {
        return passengers;
    }

    public Building getCurrentBuilding() {
        return route.get(currentPosition);
    }

    public void updatePosition(int position) {
        this.currentPosition = position;
    }

    public void updateRoute(List<Building> route) {
        this.route = route;
    }

    public String formatString() {
        StringBuilder sb = new StringBuilder();
        sb.append("POD ").append(id).append(" ").append(route.size());
        for (Building b : route) {
            sb.append(" ").append(b.id);
        }
        return sb.toString();
    }
}
