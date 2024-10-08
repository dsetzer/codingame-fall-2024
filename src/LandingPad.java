import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LandingPad extends Building {
    private Map<Integer, Integer> astronautTypeCounts;
    private int totalAstronauts;

    public LandingPad(int id, int x, int y, Map<Integer, Integer> astronautTypes) {
        super(id, x, y);
        this.astronautTypeCounts = new HashMap<>(astronautTypes);
        this.totalAstronauts = astronautTypes.values().stream().mapToInt(Integer::intValue).sum();

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
