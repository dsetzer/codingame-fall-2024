import java.util.*;

class Player {
    private ResourceManager resourceManager;
    private PodManager podManager;
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
        resources = in.nextInt();
        int numTravelRoutes = in.nextInt();

        // Parse travel routes (tubes and teleporters)
        for (int i = 0; i < numTravelRoutes; i++) {
            int buildingId1 = in.nextInt();
            int buildingId2 = in.nextInt();
            int capacity = in.nextInt();

            if (capacity > 0) {
                city.addOrUpdateTube(buildingId1, buildingId2, capacity);
            } else {
                // Teleporter (capacity == 0)
                city.addTeleporter(new Teleporter(city.getBuildingById(buildingId1), city.getBuildingById(buildingId2)));
            }
        }

        // Parse pods
        int numPods = in.nextInt();
        for (int i = 0; i < numPods; i++) {
            int podId = in.nextInt();
            int numStops = in.nextInt();
            List<Building> path = new ArrayList<>();
            for (int j = 0; j < numStops; j++) {
                int buildingId = in.nextInt();
                path.add(city.getBuildingById(buildingId));
            }
            city.addPod(new TransportPod(podId, path));
        }

        // Parse new buildings
        int numNewBuildings = in.nextInt();
        for (int i = 0; i < numNewBuildings; i++) {
            int buildingType = in.nextInt();
            int buildingId = in.nextInt();
            int x = in.nextInt();
            int y = in.nextInt();

            if (buildingType == 0) {
                // Landing pad
                int numAstronauts = in.nextInt();
                Map<Integer, Integer> astronautTypes = new HashMap<>();
                for (int j = 0; j < numAstronauts; j++) {
                    int type = in.nextInt();
                    astronautTypes.merge(type, 1, Integer::sum);
                }
                city.addBuilding(new LandingPad(buildingId, x, y, astronautTypes));
            } else {
                // Lunar module
                city.addBuilding(new LunarModule(buildingId, x, y, buildingType));
            }
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
