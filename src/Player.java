import java.util.*;

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
