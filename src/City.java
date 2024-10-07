import java.util.*;
import java.util.stream.Collectors;

class City {
    private int resources;
    private List<Building> buildings;
    private List<Tube> tubes;
    private List<Teleporter> teleporters;
    private List<TransportPod> pods;
    private List<Astronaut> astronauts;

    public City() {
        this.resources = 0;
        this.buildings = new ArrayList<>();
        this.tubes = new ArrayList<>();
        this.teleporters = new ArrayList<>();
        this.pods = new ArrayList<>();
        this.astronauts = new ArrayList<>();
    }

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public void addTube(Tube tube) {
        tubes.add(tube);
        resources -= tube.getBaseCost();
    }

    public void addTeleporter(Teleporter teleporter) {
        teleporters.add(teleporter);
        resources -= Teleporter.COST;
    }

    public void addPod(TransportPod pod) {
        pods.add(pod);
        resources -= TransportPod.COST;
    }

    public void addAstronaut(Astronaut astronaut) {
        astronauts.add(astronaut);
    }

    public void removePod(TransportPod pod) {
        pods.remove(pod);
        resources += TransportPod.DECONSTRUCTION_REFUND;
    }

    public void simulateDay() {
        // Move transport pods
        for (TransportPod pod : pods) {
            pod.move();
            handlePodArrival(pod);
        }

        // Handle teleportations
        handleTeleportations();

        // Move astronauts through tubes
        moveAstronautsThroughTubes();

        // Calculate productivity
        calculateProductivity();
    }

    public void addOrUpdateTube(int buildingId1, int buildingId2, int capacity) {
        Building building1 = getBuildingById(buildingId1);
        Building building2 = getBuildingById(buildingId2);
        if (building1 == null || building2 == null) {
            System.err.println("Warning: Tried to add tube between non-existent buildings");
            return;
        }

        Tube existingTube = getTubeByBuildings(building1, building2);
        if (existingTube != null) {
            existingTube.setCapacity(capacity);
        } else {
            tubes.add(new Tube(building1, building2, capacity));
        }
    }

    public void updatePod(int podId, int position, List<Integer> route) {
        TransportPod pod = getPodById(podId);
        if (pod == null) {
            List<Building> routeBuildings = new ArrayList<>();
            for (Integer buildingId : route) {
                Building building = getBuildingById(buildingId);
                if (building != null) {
                    routeBuildings.add(building);
                }
            }
            pod = new TransportPod(podId, routeBuildings);
            pods.add(pod);
        } else {
            pod.updatePosition(position);
            List<Building> routeBuildings = route.stream()
                .map(this::getBuildingById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            pod.updateRoute(routeBuildings);
        }
    }

    public void addOrUpdateBuilding(Building building) {
        Building existingBuilding = getBuildingById(building.getId());
        if (existingBuilding != null) {
            buildings.remove(existingBuilding);
        }
        buildings.add(building);
    }

    private void handlePodArrival(TransportPod pod) {
        Building currentBuilding = pod.getCurrentBuilding();
        List<Astronaut> disembarking = new ArrayList<>();

        for (Astronaut astronaut : pod.getPassengers()) {
            if (astronaut.getTargetModule() == currentBuilding) {
                disembarking.add(astronaut);
                astronaut.moveTo(currentBuilding);
            }
        }

        pod.getPassengers().removeAll(disembarking);

        // Load new passengers
        List<Astronaut> waitingAstronauts = getWaitingAstronauts(currentBuilding);
        int availableSeats = TransportPod.CAPACITY - pod.getPassengers().size();

        for (int i = 0; i < Math.min(availableSeats, waitingAstronauts.size()); i++) {
            Astronaut astronaut = waitingAstronauts.get(i);
            pod.getPassengers().add(astronaut);
            astronaut.moveTo(null); // Astronaut is now in transit
        }
    }

    private void handleTeleportations() {
        for (Teleporter teleporter : teleporters) {
            List<Astronaut> teleporting = getAstronautsAtBuilding(teleporter.getEntrance());
            for (Astronaut astronaut : teleporting) {
                astronaut.moveTo(teleporter.getExit());
            }
        }
    }

    private void moveAstronautsThroughTubes() {
        for (Tube tube : tubes) {
            List<Astronaut> moving = getAstronautsAtBuilding(tube.getStart());
            int movedCount = 0;
            for (Astronaut astronaut : moving) {
                if (movedCount < tube.getCapacity()) {
                    astronaut.moveTo(tube.getEnd());
                    movedCount++;
                } else {
                    break;
                }
            }
        }
    }

    private void calculateProductivity() {
        int dailyScore = 0;
        for (Building building : buildings) {
            if (building instanceof LunarModule) {
                LunarModule module = (LunarModule) building;
                int workingAstronauts = countWorkingAstronauts(module);
                dailyScore += workingAstronauts; // Each working astronaut contributes 1 point
            }
        }
        resources += dailyScore;
    }

    private List<Astronaut> getWaitingAstronauts(Building building) {
        // Implementation to get waiting astronauts at a building
        return new ArrayList<>(); // Placeholder
    }

    private List<Astronaut> getAstronautsAtBuilding(Building building) {
        // Implementation to get astronauts at a specific building
        return new ArrayList<>(); // Placeholder
    }

    private int countWorkingAstronauts(LunarModule module) {
        int workingAstronauts = 0;
        int moduleType = module.getModuleType();

        for (Astronaut astronaut : astronauts) {
            if (astronaut.getCurrentBuilding() == module && astronaut.getType() == moduleType) {
                workingAstronauts++;
            }
        }

        return workingAstronauts;
    }

    public void endOfMonthCleanup() {
        // Clear astronauts, reset pods, apply interest to resources
        astronauts.clear();
        for (TransportPod pod : pods) {
            pod.resetToStart();
        }
        resources += resources / 10; // 10% interest
    }

    public int getScore() {
        // Calculate and return the current score
        return 0; // Placeholder
    }

    // Getters and setters for all fields

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Astronaut> getAstronauts() {
        return astronauts;
    }

    public List<TransportPod> getPods() {
        return pods;
    }

    public List<Tube> getTubes() {
        return tubes;
    }

    public List<Teleporter> getTeleporters() {
        return teleporters;
    }

    public int getResources() {
        return resources;
    }

    public void setResources(int resources) {
        this.resources = resources;
    }

    public Building getBuildingById(int id) {
        return buildings.stream() .filter(b -> b.getId() == id) .findFirst() .orElse(null);
    }

    public TransportPod getPodById(int id) {
        return pods.stream() .filter(p -> p.getId() == id) .findFirst() .orElse(null);
    }

    public Tube getTubeByBuildings(Building building1, Building building2) {
        return tubes.stream() .filter(t -> (t.getStart() == building1 && t.getEnd() == building2) || (t.getStart() == building2 && t.getEnd() == building1)) .findFirst() .orElse(null);
    }
}
