package bg.sofia.uni.fmi.mjt.olympics.competition;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CompetitionTest {
    @Test
    void testCompetitionWithNullName() {
        Set<Competitor> s = new HashSet<>();
        s.add(mock(Competitor.class));

        assertThrows(IllegalArgumentException.class, () -> new Competition(null, "race", s));
    }

    @Test
    void testCompetitionWithBlankName() {
        Set<Competitor> s = new HashSet<>();
        s.add(mock(Competitor.class));

        assertThrows(IllegalArgumentException.class, () -> new Competition("  ", "race", s));
    }

    @Test
    void testCompetitionWithNullDiscipline() {
        Set<Competitor> s = new HashSet<>();
        s.add(mock(Competitor.class));

        assertThrows(IllegalArgumentException.class, () -> new Competition("name", null, s));
    }

    @Test
    void testCompetitionWithBlankDiscipline() {
        Set<Competitor> s = new HashSet<>();
        s.add(mock(Competitor.class));

        assertThrows(IllegalArgumentException.class, () -> new Competition("name", "  ", s));
    }

    @Test
    void testCompetitionWithNullCompetitors() {
        assertThrows(IllegalArgumentException.class, () -> new Competition("name", "sports", null));
    }

    @Test
    void testCompetitionWithEmptyCompetitors() {
        assertThrows(IllegalArgumentException.class, () -> new Competition("name", "sports", new HashSet<>()));
    }

    @Test
    void testCompetitionOk() {
        Set<Competitor> s = new HashSet<>();
        s.add(mock(Competitor.class));

        assertDoesNotThrow(() -> new Competition("name", "sports", s));
    }

    @Test
    void testGetCompetitorsChanging() {
        Set<Competitor> s = new HashSet<>();
        s.add(mock(Competitor.class));
        Competition competition = new Competition("name", "sports", s);

        assertThrows(UnsupportedOperationException.class, () -> competition.competitors().clear());
    }

    @Test
    void testEqualsWithSameRef() {
        Competitor competitor1 = mock(Competitor.class);
        Set<Competitor> s = new HashSet<>();
        s.add(competitor1);
        Competition competition = new Competition("theLeague", "football", s);
        assertEquals(competition, competition, "One competition reference must be equal to itself");
    }

    @Test
    void testEqualsWithTwoSameCompetitions() {
        Competitor competitor1 = mock(Competitor.class);
        Set<Competitor> s = new HashSet<>();
        s.add(competitor1);
        Competition competition = new Competition("theLeague", "football", s);
        Competition competition2 = new Competition("theLeague", "football", s);
        assertEquals(competition, competition2, "Two competition with same name and discipline must be equal!");
    }

    @Test
    void testEqualsWithTheSecondCompetitionIsNull() {
        Competitor competitor1 = mock(Competitor.class);
        Set<Competitor> s = new HashSet<>();
        s.add(competitor1);
        Competition competition = new Competition("theLeague", "football", s);
        assertNotEquals(competition, null, "Competitions are not equal if the competition is null!");
    }

    @Test
    void testEqualsWithWithDifferentCompetitionNames() {
        Competitor competitor1 = mock(Competitor.class);
        Set<Competitor> s = new HashSet<>();
        s.add(competitor1);
        Competition competition = new Competition("theLeague", "football", s);
        Competition competition2 = new Competition("ballLeague", "football", s);
        assertNotEquals(competition, competition2, "Two competition with different name must not be equal!");
    }

    @Test
    void testEqualsWithWithDifferentCompetitionDisciplines() {
        Competitor competitor1 = mock(Competitor.class);
        Set<Competitor> s = new HashSet<>();
        s.add(competitor1);
        Competition competition = new Competition("theLeague", "basketball", s);
        Competition competition2 = new Competition("theLeague", "football", s);
        assertNotEquals(competition, competition2, "Two competition with different disciplines must not be equal!");
    }

    @Test
    void testEqualsCompetitionsBothWays() {
        Competitor competitor1 = mock(Competitor.class);
        Set<Competitor> s = new HashSet<>();
        s.add(competitor1);
        Competition competition = new Competition("theLeague", "football", s);
        Competition competition2 = new Competition("theLeague", "football", s);
        assertTrue(competition.equals(competition2) && competition2.equals(competition), "Two same competition must be equal both ways!");
    }

    @Test
    void testEqualsCompetitionsTransitive() {
        Competitor competitor1 = mock(Competitor.class);
        Set<Competitor> s = new HashSet<>();
        s.add(competitor1);
        Competition competition = new Competition("theLeague", "basketball", s);
        Competition competition2 = new Competition("theLeague", "basketball", s);
        Competition competition3 = new Competition("theLeague", "basketball", s);
        assertTrue(competition.equals(competition2) && competition2.equals(competition3) && competition.equals(competition3), "Equals must be transitive!");
    }

    @Test
    void testHashCodeDifferentNames() {
        Competitor competitor1 = mock(Competitor.class);
        Set<Competitor> s = new HashSet<>();
        s.add(competitor1);
        Competition competition = new Competition("ballLeague", "basketball", s);
        Competition competition2 = new Competition("theLeague", "basketball", s);
        assertNotEquals(competition.hashCode(), competition2.hashCode(), "Two competitions with different names should have different hashcode");
    }

    @Test
    void testHashCodeDifferentDisciplines() {
        Competitor competitor1 = mock(Competitor.class);
        Set<Competitor> s = new HashSet<>();
        s.add(competitor1);
        Competition competition = new Competition("theLeague", "football", s);
        Competition competition2 = new Competition("theLeague", "basketball", s);
        assertNotEquals(competition.hashCode(), competition2.hashCode(), "Two competitions from different disciplines should have different hashcode");
    }

    @Test
    void testHashCodeConsistency() {
        Competitor competitor1 = mock(Competitor.class);
        Set<Competitor> s = new HashSet<>();
        s.add(competitor1);
        Competition competition = new Competition("theLeague", "football", s);
        assertEquals(competition.hashCode(), competition.hashCode(), "The same competition refs must have the same hashcode");
    }

    @Test
    void testHashCodeEqualNamesAndDisciplines() {
        Competitor competitor1 = mock(Competitor.class);
        Set<Competitor> s = new HashSet<>();
        s.add(competitor1);
        Competition competition = new Competition("theLeague", "football", s);
        Competition competition2 = new Competition("theLeague", "football", s);
        assertEquals(competition.hashCode(), competition2.hashCode(), "The same competition must have the same hashcode");
    }
}
