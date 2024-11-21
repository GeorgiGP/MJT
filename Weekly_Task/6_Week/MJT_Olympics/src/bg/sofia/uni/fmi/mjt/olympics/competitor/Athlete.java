package bg.sofia.uni.fmi.mjt.olympics.competitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Athlete implements Competitor {

    private final String identifier;
    private final String name;
    private final String nationality;

    private final Map<Medal, Integer> medals;

    public Athlete(String identifier, String name, String nationality) {
        this.identifier = identifier;
        this.name = name;
        this.nationality = nationality;
        this.medals = new HashMap<>();
    }

    public void addMedal(Medal medal) {
        validateMedal(medal);
        if (!medals.containsKey(medal)) {
            medals.put(medal, 1);
        } else {
            medals.put(medal, medals.get(medal) + 1);
        }
    }

    private void validateMedal(Medal medal) {
        if (medal == null) {
            throw new IllegalArgumentException("Medal cannot be null");
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNationality() {
        return nationality;
    }

    @Override
    public Collection<Medal> getMedals() {
        List<Medal> medals = new ArrayList<>();
        for (Medal medal : this.medals.keySet()) {
            int count = this.medals.get(medal);
            for (int i = 0; i < count; i++) {
                medals.add(medal);
            }
        }
        return Collections.unmodifiableList(medals);
    }

    public Map<Medal, Integer> getMedalsMap() {
        return Collections.unmodifiableMap(medals);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Athlete athlete = (Athlete) o;
        return Objects.equals(identifier, athlete.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
