import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

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

class LandingPad extends Building {
    private Map<Integer, Integer> astronautTypeCounts;
    private int totalAstronauts;

    public LandingPad(int id, int x, int y, int totalAstronauts, List<Integer> astronautTypes) {
        super(id, x, y);
        this.totalAstronauts = totalAstronauts;
        this.astronautTypeCounts = new HashMap<>();

        for (int type : astronautTypes) {
            astronautTypeCounts.put(type, astronautTypeCounts.getOrDefault(type, 0) + 1);
        }

        System.err.println("Debug: LandingPad " + id + " created with total astronauts: " + this.totalAstronauts +
            ", astronauts by type: " + this.astronautTypeCounts);
    }

    public int getTotalAstronauts() {
        return totalAstronauts;
    }

    public Map<Integer, Integer> getAstronautTypeCounts() {
        return astronautTypeCounts;
    }

    public Set<Integer> getAstronautTypes() {
        return Collections.unmodifiableSet(astronautTypeCounts.keySet());
    }

    public int getAstronautCount(int type) {
        return astronautTypeCounts.getOrDefault(type, 0);
    }


}

class Player {
    private static City city;
    private static TransportationAI ai;
    private static GameState gameState;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        city = new City();
        ai = new TransportationAI(city);
        gameState = new GameState();

        // Game loop
        while (true) {
            parseInput(in);
            List<String> actions = ai.makeDecisions();

            if (actions.isEmpty()) {
                // If no actions, wait
                System.out.println("WAIT");
            }else{
            // Output actions
                System.out.println(String.join(";", actions));
            }
            System.out.flush();

            // The simulation of days is handled by the game engine in Codingame
            // We don't need to simulate days here
            gameState.incrementMonth();

            // Debug output
            System.err.println(gameState.getGameStatus());
        }
    }

    private static void parseInput(Scanner in) {
        int resources = in.nextInt();
        city.setResources(resources);

        int numTravelRoutes = in.nextInt();
        for (int i = 0; i < numTravelRoutes; i++) {
            int buildingId1 = in.nextInt();
            int buildingId2 = in.nextInt();
            int capacity = in.nextInt();
            // Update city with this travel route information
            city.addOrUpdateTube(buildingId1, buildingId2, capacity);
        }

        int numPods = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }
        for (int i = 0; i < numPods; i++) {
            String podProperties = in.nextLine();
            // Parse podProperties and update city
            parsePodProperties(podProperties);
        }

        int numNewBuildings = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }
        for (int i = 0; i < numNewBuildings; i++) {
            String buildingProperties = in.nextLine();
            // Parse buildingProperties and update city
            parseBuildingProperties(buildingProperties);
        }
    }

    private static void parsePodProperties(String podProperties) {
        // Implement parsing logic for pod properties
        // Example (adjust according to actual format):
        // podId position buildingId1 buildingId2 ...
        String[] parts = podProperties.split(" ");
        int podId = Integer.parseInt(parts[0]);
        int position = Integer.parseInt(parts[1]);
        List<Integer> route = new ArrayList<>();
        for (int i = 2; i < parts.length; i++) {
            route.add(Integer.valueOf(parts[i]));
        }
        city.updatePod(podId, position, route);
    }

    private static void parseBuildingProperties(String buildingProperties) {
        String[] parts = buildingProperties.split(" ");
        int buildingId = Integer.parseInt(parts[0]);
        int type = Integer.parseInt(parts[1]);
        int x = Integer.parseInt(parts[2]);
        int y = Integer.parseInt(parts[3]);

        if (type == 0) { // Landing Pad
            int totalAstronauts = Integer.parseInt(parts[4]);
            List<Integer> astronautTypes = new ArrayList<>();
            for (int i = 5; i < parts.length; i++) {
                astronautTypes.add(Integer.valueOf(parts[i]));
            }
            System.err.println("Debug: Creating LandingPad with id " + buildingId +
                ", total astronauts: " + totalAstronauts + ", astronaut types: " + astronautTypes);
            city.addOrUpdateBuilding(new LandingPad(buildingId, x, y, totalAstronauts, astronautTypes));
        } else { // Lunar Module
            System.err.println("Debug: Creating LunarModule with id " + buildingId + ", type " + type);
            city.addOrUpdateBuilding(new LunarModule(buildingId, x, y, type));
        }
    }
}

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

class Building {
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

class LunarModule extends Building {
    private int moduleType;

    public LunarModule(int id, int x, int y, int moduleType) {
        super(id, x, y);
        this.moduleType = moduleType;
    }

    public int getModuleType() {
        return moduleType;
    }

    public void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }

    public String formatString() {
        return String.format("%d %d %d %d", id, x, y, moduleType);
    }
}

class TransportationAI {
    private final City city;
    private final Map<Integer, Building> buildingMap;
    private final Map<Integer, List<Tube>> tubeMap;
    private final Map<Integer, TransportPod> podMap;
    private List<Teleporter> teleporters;
    private final PriorityQueue<BuildingPair> potentialConnections;

    private static final int TELEPORTER_COST = 5000;
    private static final int POD_COST = 1000;
    private static final int POD_REFUND = 750;
    private static final int MAX_TUBES_PER_BUILDING = 5;
    private static final double TUBE_COST_PER_KM = 10.0;
    private static final int MAX_POD_CAPACITY = 10;

    public TransportationAI(City city) {
        this.city = city;
        this.buildingMap = new HashMap<>();
        this.tubeMap = new HashMap<>();
        this.podMap = new HashMap<>();
        this.teleporters = new ArrayList<>();
        this.potentialConnections = new PriorityQueue<>((a, b) -> Double.compare(a.distance, b.distance));
    }

    public List<String> makeDecisions() {
        List<String> actions = new ArrayList<>();

        analyzeCityState();

        actions.addAll(decideTubeConstruction());
        actions.addAll(decideTeleporterConstruction());
        actions.addAll(decidePodConstruction());
        actions.addAll(decidePodRoutes());

        return actions;
    }

    private void analyzeCityState() {
        updateBuildingMap();
        updateTubeMap();
        updatePodMap();
        updateTeleporters();
        identifyPotentialConnections();
        analyzeNetworkEfficiency();
    }

    private void updateBuildingMap() {
        for (Building building : city.getBuildings()) {
            buildingMap.put(building.getId(), building);
        }
    }

    private void updateTubeMap() {
        tubeMap.clear();
        for (Tube tube : city.getTubes()) {
            tubeMap.computeIfAbsent(tube.getStart().getId(), k -> new ArrayList<>()).add(tube);
            tubeMap.computeIfAbsent(tube.getEnd().getId(), k -> new ArrayList<>()).add(tube);
        }
    }

    private void updatePodMap() {
        for (TransportPod pod : city.getPods()) {
            podMap.put(pod.getId(), pod);
        }
    }

    private void updateTeleporters() {
        teleporters = city.getTeleporters();
    }

    private void identifyPotentialConnections() {
        potentialConnections.clear();
        List<Building> buildings = new ArrayList<>(buildingMap.values());
        for (int i = 0; i < buildings.size(); i++) {
            for (int j = i + 1; j < buildings.size(); j++) {
                Building b1 = buildings.get(i);
                Building b2 = buildings.get(j);
                if (canConnectBuildings(b1, b2)) {
                    double distance = calculateDistance(b1, b2);
                    potentialConnections.offer(new BuildingPair(b1, b2, distance));
                }
            }
        }
    }

    private boolean canConnectBuildings(Building b1, Building b2) {
        if (tubeMap.getOrDefault(b1.getId(), Collections.emptyList()).size() >= MAX_TUBES_PER_BUILDING ||
            tubeMap.getOrDefault(b2.getId(), Collections.emptyList()).size() >= MAX_TUBES_PER_BUILDING) {
            return false;
        }
        if (city.getTubeByBuildings(b1, b2) != null) {
            return false;
        }
        return !wouldCrossExistingTube(b1, b2);
    }

    private boolean wouldCrossExistingTube(Building b1, Building b2) {
        for (Tube tube : city.getTubes()) {
            if (segmentsIntersect(b1, b2, tube.getStart(), tube.getEnd())) {
                return true;
            }
        }
        return false;
    }

    private boolean segmentsIntersect(Building a, Building b, Building c, Building d) {
        int o1 = orientation(a, b, c);
        int o2 = orientation(a, b, d);
        int o3 = orientation(c, d, a);
        int o4 = orientation(c, d, b);

        if (o1 != o2 && o3 != o4) return true;

        if (o1 == 0 && onSegment(a, c, b)) return true;
        if (o2 == 0 && onSegment(a, d, b)) return true;
        if (o3 == 0 && onSegment(c, a, d)) return true;
        if (o4 == 0 && onSegment(c, b, d)) return true;

        return false;
    }

    private int orientation(Building p, Building q, Building r) {
        int val = (q.getY() - p.getY()) * (r.getX() - q.getX()) -
                  (q.getX() - p.getX()) * (r.getY() - q.getY());
        if (val == 0) return 0;
        return (val > 0) ? 1 : 2;
    }

    private boolean onSegment(Building p, Building q, Building r) {
        return q.getX() <= Math.max(p.getX(), r.getX()) && q.getX() >= Math.min(p.getX(), r.getX()) &&
            q.getY() <= Math.max(p.getY(), r.getY()) && q.getY() >= Math.min(p.getY(), r.getY());
    }

    private void analyzeNetworkEfficiency() {
        Map<Integer, Integer> moduleTypeCount = new HashMap<>();
        Map<Integer, Integer> astronautTypeCount = new HashMap<>();
        int totalAstronauts = 0;

        for (Building building : buildingMap.values()) {
            if (building instanceof LunarModule module) {
                moduleTypeCount.put(module.getModuleType(), moduleTypeCount.getOrDefault(module.getModuleType(), 0) + 1);
            } else if (building instanceof LandingPad landingPad) {
                totalAstronauts += landingPad.getTotalAstronauts();
                for (Map.Entry<Integer, Integer> entry : landingPad.getAstronautTypeCounts().entrySet()) {
                    int astronautType = entry.getKey();
                    int count = entry.getValue();
                    astronautTypeCount.put(astronautType, astronautTypeCount.getOrDefault(astronautType, 0) + count);
                }
            }
        }

        System.err.println("Debug: Total astronauts across all landing pads: " + totalAstronauts);
        System.err.println("Debug: Astronaut counts by type: " + astronautTypeCount);
        System.err.println("Debug: Module counts by type: " + moduleTypeCount);

        // Analyze balance between astronaut types and module types
        for (Map.Entry<Integer, Integer> entry : astronautTypeCount.entrySet()) {
            int astronautType = entry.getKey();
            int astronautCount = entry.getValue();
            int moduleCount = moduleTypeCount.getOrDefault(astronautType, 0);
            if (moduleCount == 0) {
                System.err.println("Warning: No modules for astronaut type " + astronautType + " (Count: " + astronautCount + ")");
            } else if (astronautCount > moduleCount * MAX_POD_CAPACITY) {
                System.err.println("Warning: Potential bottleneck for astronaut type " + astronautType +
                    " (Astronauts: " + astronautCount + ", Modules: " + moduleCount + ")");
            }
        }
    }

    private List<String> decideTubeConstruction() {
        List<String> actions = new ArrayList<>();
        int availableResources = city.getResources();

        while (!potentialConnections.isEmpty() && availableResources > 0) {
            BuildingPair pair = potentialConnections.poll();
            int tubeCost = calculateTubeCost(pair.distance);

            if (tubeCost <= availableResources) {
                actions.add(String.format("TUBE %d %d", pair.b1.getId(), pair.b2.getId()));
                availableResources -= tubeCost;
            }
        }

        // Consider upgrading existing tubes
        for (Tube tube : city.getTubes()) {
            if (tube.getCapacity() < 3 && isHighTrafficTube(tube)) {
                int upgradeCost = tube.getUpgradeCost();
                if (upgradeCost <= availableResources) {
                    actions.add(String.format("UPGRADE %d %d", tube.getStart().getId(), tube.getEnd().getId()));
                    availableResources -= upgradeCost;
                }
            }
        }

        return actions;
    }

    private boolean isHighTrafficTube(Tube tube) {
        // Implement logic to determine if a tube has high traffic
        // This could be based on the number of pods using the tube, or the number of astronauts passing through
        return podMap.values().stream()
            .filter(pod -> pod.getRoute().contains(tube.getStart()) && pod.getRoute().contains(tube.getEnd()))
            .count() >= tube.getCapacity();
    }

    private List<String> decideTeleporterConstruction() {
        List<String> actions = new ArrayList<>();
        int availableResources = city.getResources();

        // Identify potential teleporter locations
        List<BuildingPair> potentialTeleporters = identifyPotentialTeleporterLocations();

        for (BuildingPair pair : potentialTeleporters) {
            if (availableResources >= TELEPORTER_COST) {
                actions.add(String.format("TELEPORT %d %d", pair.b1.getId(), pair.b2.getId()));
                availableResources -= TELEPORTER_COST;
            } else {
                break;
            }
        }

        return actions;
    }

    private List<BuildingPair> identifyPotentialTeleporterLocations() {
        List<BuildingPair> potentialTeleporters = new ArrayList<>();
        Map<Integer, Set<Building>> moduleTypeToBuildings = new HashMap<>();

        // Group buildings by module type
        for (Building building : buildingMap.values()) {
            if (building instanceof LunarModule module) {
                moduleTypeToBuildings.computeIfAbsent(module.getModuleType(), k -> new HashSet<>()).add(module);
            }
        }

        // For each module type, find the two most distant buildings
        for (Set<Building> buildings : moduleTypeToBuildings.values()) {
            if (buildings.size() >= 2) {
                Building[] mostDistant = findMostDistantBuildings(buildings);
                potentialTeleporters.add(new BuildingPair(mostDistant[0], mostDistant[1],
                    calculateDistance(mostDistant[0], mostDistant[1])));
            }
        }

        // Sort potential teleporters by distance in descending order
        potentialTeleporters.sort((a, b) -> Double.compare(b.distance, a.distance));

        return potentialTeleporters;
    }

    private Building[] findMostDistantBuildings(Set<Building> buildings) {
        Building[] mostDistant = new Building[2];
        double maxDistance = 0;

        for (Building b1 : buildings) {
            for (Building b2 : buildings) {
                if (b1 != b2) {
                    double distance = calculateDistance(b1, b2);
                    if (distance > maxDistance) {
                        maxDistance = distance;
                        mostDistant[0] = b1;
                        mostDistant[1] = b2;
                    }
                }
            }
        }

        return mostDistant;
    }

    private List<String> decidePodConstruction() {
        List<String> actions = new ArrayList<>();
        int availableResources = city.getResources();

        // Analyze current pod distribution and identify needs
        Map<BuildingPair, Integer> routeNeedMap = analyzeRouteNeeds();

        // Construct new pods for high-need routes
        for (Map.Entry<BuildingPair, Integer> entry : routeNeedMap.entrySet()) {
            BuildingPair route = entry.getKey();
            int need = entry.getValue();

            while (need > 0 && availableResources >= POD_COST) {
                int podId = generateUniquePodId();
                actions.add(String.format("POD %d %d %d", podId, route.b1.getId(), route.b2.getId()));
                availableResources -= POD_COST;
                need--;
            }
        }

        // Consider deconstructing underutilized pods
        List<TransportPod> underutilizedPods = identifyUnderutilizedPods();
        for (TransportPod pod : underutilizedPods) {
            if (availableResources < POD_COST) {  // Only deconstruct if we're low on resources
                actions.add(String.format("DESTROY %d", pod.getId()));
                availableResources += POD_REFUND;
            }
        }

        return actions;
    }

    private Map<BuildingPair, Integer> analyzeRouteNeeds() {
        Map<BuildingPair, Integer> routeNeedMap = new HashMap<>();

        for (Building start : buildingMap.values()) {
            if (start instanceof LandingPad landingPad) {
                Map<Integer, List<Building>> targetModules = getTargetModules(landingPad.getAstronautTypes());

                for (Map.Entry<Integer, List<Building>> entry : targetModules.entrySet()) {
                    int astronautType = entry.getKey();
                    List<Building> modules = entry.getValue();

                    int astronautCount = landingPad.getAstronautCount(astronautType);

                    for (Building end : modules) {
                        BuildingPair route = new BuildingPair(start, end, calculateDistance(start, end));
                        int currentPods = countPodsOnRoute(route);
                        int neededPods = (int) Math.ceil((double) astronautCount / MAX_POD_CAPACITY) - currentPods;
                        if (neededPods > 0) {
                            routeNeedMap.put(route, neededPods);
                        }
                    }
                }
            }
        }

        return routeNeedMap;
    }

    private Map<Integer, List<Building>> getTargetModules(Set<Integer> astronautTypes) {
        Map<Integer, List<Building>> targetModules = new HashMap<>();
        for (int astronautType : astronautTypes) {
            targetModules.put(astronautType, buildingMap.values().stream() .filter(b -> b instanceof LunarModule && ((LunarModule) b).getModuleType() == astronautType) .collect(Collectors.toList()));
        }
        return targetModules;
    }

    private int countPodsOnRoute(BuildingPair route) {
        return (int) podMap.values().stream().filter(pod -> pod.getRoute().contains(route.b1) && pod.getRoute().contains(route.b2)).count();
    }

    private List<TransportPod> identifyUnderutilizedPods() {
        return podMap.values().stream() .filter(this::isPodUnderutilized) .collect(Collectors.toList());
    }

    private boolean isPodUnderutilized(TransportPod pod) {
        // A pod is considered underutilized if it's been operating at less than 50% capacity for the last 5 days
        // This is a simplified version; in a real-world scenario, we'd need to track pod utilization over time
        return pod.getPassengers().size() < MAX_POD_CAPACITY / 2;
    }

    private int generateUniquePodId() {
        AtomicInteger id = new AtomicInteger(1);
        while (podMap.values().stream().anyMatch(pod -> pod.getId() == id.get())) {
            id.incrementAndGet();
        }
        return id.get();
    }

    private List<String> decidePodRoutes() {
        List<String> actions = new ArrayList<>();

        for (TransportPod pod : podMap.values()) {
            List<Building> optimizedRoute = optimizePodRoute(pod);
            if (!optimizedRoute.equals(pod.getRoute())) {
                actions.add(formatPodRouteAction(pod.getId(), optimizedRoute));
            }
        }

        return actions;
    }

    private List<Building> optimizePodRoute(TransportPod pod) {
        List<Building> currentRoute = pod.getRoute();
        Building start = currentRoute.get(0);
        Building end = currentRoute.get(currentRoute.size() - 1);

        // If it's a circular route, we'll optimize it as is
        // Otherwise, we'll find the best route between start and end
        if (start.equals(end)) {
            return optimizeCircularRoute(currentRoute);
        } else {
            return findOptimalRoute(start, end);
        }
    }

    private List<Building> optimizeCircularRoute(List<Building> currentRoute) {
        // Implement Traveling Salesman Problem (TSP) approximation
        // For simplicity, we'll use a 2-opt algorithm here
        List<Building> optimizedRoute = new ArrayList<>(currentRoute);
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < optimizedRoute.size() - 2; i++) {
                for (int j = i + 2; j < optimizedRoute.size() - 1; j++) {
                    if (tryTwoOptSwap(optimizedRoute, i, j)) {
                        improved = true;
                    }
                }
            }
        }
        return optimizedRoute;
    }

    private boolean tryTwoOptSwap(List<Building> route, int i, int j) {
        Building a = route.get(i);
        Building b = route.get(i + 1);
        Building c = route.get(j);
        Building d = route.get(j + 1);

        double currentDistance = calculateDistance(a, b) + calculateDistance(c, d);
        double newDistance = calculateDistance(a, c) + calculateDistance(b, d);

        if (newDistance < currentDistance) {
            // Perform the 2-opt swap
            Collections.reverse(route.subList(i + 1, j + 1));
            return true;
        }
        return false;
    }

    private List<Building> findOptimalRoute(Building start, Building end) {
        // Implement Dijkstra's algorithm for pathfinding
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
        Map<Building, Double> distances = new HashMap<>();
        Map<Building, Building> previousBuilding = new HashMap<>();

        for (Building building : buildingMap.values()) {
            distances.put(building, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        queue.offer(new Node(start, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.building.equals(end)) {
                return reconstructPath(previousBuilding, end);
            }

            if (current.distance > distances.get(current.building)) {
                continue;
            }

            for (Building neighbor : getNeighbors(current.building)) {
                double newDist = distances.get(current.building) + calculateDistance(current.building, neighbor);
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousBuilding.put(neighbor, current.building);
                    queue.offer(new Node(neighbor, newDist));
                }
            }
        }

        // If no path is found, return the direct route
        return Arrays.asList(start, end);
    }

    private List<Building> reconstructPath(Map<Building, Building> cameFrom, Building current) {
        List<Building> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

    private List<Building> getNeighbors(Building building) {
        Set<Building> neighbors = new HashSet<>();
        for (Tube tube : tubeMap.getOrDefault(building.getId(), Collections.emptyList())) {
            neighbors.add(tube.getStart().equals(building) ? tube.getEnd() : tube.getStart());
        }
        for (Teleporter teleporter : teleporters) {
            if (teleporter.getEntrance().equals(building)) {
                neighbors.add(teleporter.getExit());
            }
        }
        return new ArrayList<>(neighbors);
    }

    private String formatPodRouteAction(int podId, List<Building> route) {
        StringBuilder sb = new StringBuilder();
        sb.append("POD ").append(podId);
        for (Building building : route) {
            sb.append(" ").append(building.getId());
        }
        return sb.toString();
    }

    private double calculateDistance(Building b1, Building b2) {
        return Math.hypot(b1.getX() - b2.getX(), b1.getY() - b2.getY());
    }

    private int calculateTubeCost(double distance) {
        return (int) Math.floor(distance * TUBE_COST_PER_KM);
    }

    class BuildingPair {
        Building b1;
        Building b2;
        double distance;

        BuildingPair(Building b1, Building b2, double distance) {
            this.b1 = b1;
            this.b2 = b2;
            this.distance = distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BuildingPair that = (BuildingPair) o;
            return (Objects.equals(b1, that.b1) && Objects.equals(b2, that.b2)) ||
                    (Objects.equals(b1, that.b2) && Objects.equals(b2, that.b1));
        }

        @Override
        public int hashCode() {
            return Objects.hash(Math.min(b1.getId(), b2.getId()), Math.max(b1.getId(), b2.getId()));
        }
    }

    class Node {
        Building building;
        double distance;

        Node(Building building, double distance) {
            this.building = building;
            this.distance = distance;
        }
    }
}

class GameState {
    private int totalScore;
    private int currentMonth;
    private static final int MAX_MONTHS = 12;

    public GameState() {
        this.totalScore = 0;
        this.currentMonth = 1;
    }

    public void updateScore(int monthlyScore) {
        this.totalScore += monthlyScore;
    }

    public void incrementMonth() {
        if (currentMonth < MAX_MONTHS) {
            currentMonth++;
        }
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public boolean isGameOver() {
        return currentMonth > MAX_MONTHS;
    }

    public void reset() {
        this.totalScore = 0;
        this.currentMonth = 1;
    }

    public String getGameStatus() {
        return String.format("Month: %d/%d, Total Score: %d", currentMonth, MAX_MONTHS, totalScore);
    }

    public double getAverageScorePerMonth() {
        return (double) totalScore / currentMonth;
    }

    public int getRemainingMonths() {
        return MAX_MONTHS - currentMonth;
    }

    @Override
    public String toString() {
        return getGameStatus();
    }
}

