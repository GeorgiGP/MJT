package bg.sofia.uni.fmi.mjt.olympics.competition;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @throws IllegalArgumentException when the competition name is null or blank
 * @throws IllegalArgumentException when the competition discipline is null or blank
 * @throws IllegalArgumentException when the competition's competitors is null or empty
 */
public record Competition(String name, String discipline, Set<Competitor> competitors) {

    public Competition {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (discipline == null || discipline.isBlank()) {
            throw new IllegalArgumentException("Discipline cannot be null or blank");
        }
        if (competitors == null || competitors.isEmpty()) {
            throw new IllegalArgumentException("Competitors cannot be null or empty");
        }
    }

    public Set<Competitor> competitors() {
        return Collections.unmodifiableSet(competitors);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competition that = (Competition) o;
        return Objects.equals(name, that.name) && Objects.equals(discipline, that.discipline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, discipline);
    }
}