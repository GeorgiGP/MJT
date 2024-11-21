package bg.sofia.uni.fmi.mjt.olympics;

import bg.sofia.uni.fmi.mjt.olympics.competition.Competition;
import bg.sofia.uni.fmi.mjt.olympics.competition.CompetitionResultFetcher;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Medal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MJTOlympicsTest {

    Set<Competitor> registeredCompetitors;
    CompetitionResultFetcher competitionResultFetcher;

    public static class ComparatorCompetitor implements Comparator<Competitor> {
        @Override
        public int compare(Competitor o1, Competitor o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    @BeforeEach
    void setUp() {
        registeredCompetitors = new HashSet<>();
        competitionResultFetcher = mock(CompetitionResultFetcher.class);
    }

    @Test
    void testUpdateMedalStatisticsNullCompetition() {
        MJTOlympics mjtOlympics = new MJTOlympics(registeredCompetitors, competitionResultFetcher);
        assertThrows(IllegalArgumentException.class, () -> mjtOlympics.updateMedalStatistics(null));
    }

    @Test
    void testUpdateMedalStatisticsWithCompetitionWhereOneOfTheCompetitorsHasNotBeenRegistered() {
        Competition competition = mock(Competition.class);
        Competitor competitor1 = mock(Competitor.class);
        Competitor competitor2 = mock(Competitor.class);
        Competitor competitor3 = mock(Competitor.class);

        when(competition.competitors()).thenReturn(Set.of(competitor1, competitor2, competitor3));
        registeredCompetitors.add(competitor1);
        registeredCompetitors.add(competitor3);

        MJTOlympics mjtolympics = new MJTOlympics(registeredCompetitors, competitionResultFetcher);
        assertThrows(IllegalArgumentException.class, () -> mjtolympics.updateMedalStatistics(competition), "When updating medal statistics all of the competitors in the competition must be registered in the olympics.");
    }

    @Test
    void testUpdateMedalStatisticsRegisteredCompetitors() {
        Competition competition = mock(Competition.class);

        Competitor competitor1 = mock(Competitor.class);
        when(competitor1.getName()).thenReturn("Alex");
        when(competitor1.getNationality()).thenReturn("Bulgaria");

        Competitor competitor2 = mock(Competitor.class);
        when(competitor2.getName()).thenReturn("Michael");
        when(competitor2.getNationality()).thenReturn("USA");

        when(competition.competitors()).thenReturn(Set.of(competitor1, competitor2));
        registeredCompetitors.add(competitor1);
        registeredCompetitors.add(competitor2);

        TreeSet<Competitor> treeSet = new TreeSet<>(new ComparatorCompetitor());
        treeSet.add(competitor1);
        treeSet.add(competitor2);

        when(competitionResultFetcher.getResult(competition)).thenReturn(treeSet);

        MJTOlympics olympics = new MJTOlympics(registeredCompetitors, competitionResultFetcher);

        assertDoesNotThrow(() -> olympics.updateMedalStatistics(competition), "When the competitors are registered,no exception should be thrown should!");

        Map<String, EnumMap<Medal, Integer>> table = olympics.getNationsMedalTable();

        assertTrue(table.get("Bulgaria").get(Medal.GOLD) == 1 && table.get("USA").get(Medal.SILVER) == 1, "The winners are registered and their medals count for their countries should increase.");
    }

    @Test
    void testUpdateMedalStatisticsRegisteredCompetitor() {
        Competition competition = mock(Competition.class);

        Competitor competitor1 = mock(Competitor.class);
        when(competitor1.getName()).thenReturn("Alex");
        when(competitor1.getNationality()).thenReturn("Bulgaria");

        when(competition.competitors()).thenReturn(Set.of(competitor1));
        registeredCompetitors.add(competitor1);

        TreeSet<Competitor> treeSet = new TreeSet<>(new ComparatorCompetitor());
        treeSet.add(competitor1);

        when(competitionResultFetcher.getResult(competition)).thenReturn(treeSet);

        MJTOlympics olympics = new MJTOlympics(registeredCompetitors, competitionResultFetcher);

        assertDoesNotThrow(() -> olympics.updateMedalStatistics(competition), "When the competitors are registered,no exception should be thrown should!");
        assertEquals(1, olympics.getTotalMedals("Bulgaria"), "After adding a medal to the olympics for a country with no medals, should contain one medal.");
    }


    @Test
    void testGetTotalMedalsNationalityNull() {
        MJTOlympics mjtOlympics = new MJTOlympics(registeredCompetitors, competitionResultFetcher);
        assertThrows(IllegalArgumentException.class, () -> mjtOlympics.getTotalMedals(null));
    }

    @Test
    void testGetTotalMedalsNationalityNotRegistered() {
        String nation1 = "Bulgaria";
        MJTOlympics mjtOlympics = new MJTOlympics(registeredCompetitors, competitionResultFetcher);
        assertThrows(IllegalArgumentException.class, () -> mjtOlympics.getTotalMedals(nation1));
    }

    @Test
    void testGetNationsRankList() {
        Competition competition = mock(Competition.class);

        Competitor competitor1 = mock(Competitor.class);
        when(competitor1.getName()).thenReturn("Alex");
        when(competitor1.getNationality()).thenReturn("Bulgaria");

        Competitor competitor2 = mock(Competitor.class);
        when(competitor2.getName()).thenReturn("Mikael");
        when(competitor2.getNationality()).thenReturn("Austria");

        Competitor competitor3 = mock(Competitor.class);
        when(competitor3.getName()).thenReturn("Boyan");
        when(competitor3.getNationality()).thenReturn("Bulgaria");

        when(competition.competitors()).thenReturn(Set.of(competitor1, competitor2, competitor3));
        registeredCompetitors.add(competitor1);
        registeredCompetitors.add(competitor2);
        registeredCompetitors.add(competitor3);

        CompetitionResultFetcher competitionResultFetcher = mock(CompetitionResultFetcher.class);

        TreeSet<Competitor> treeSet = new TreeSet<>(new ComparatorCompetitor());
        treeSet.add(competitor1);
        treeSet.add(competitor2);
        treeSet.add(competitor3);
        when(competitionResultFetcher.getResult(competition)).thenReturn(treeSet);

        MJTOlympics olympics = new MJTOlympics(registeredCompetitors, competitionResultFetcher);
        assertDoesNotThrow(() -> olympics.updateMedalStatistics(competition), "When the competitors are registered,no exception should be thrown should!");

        TreeSet<String> result = olympics.getNationsRankList();

        assertEquals(result.first(), "Bulgaria");
        assertEquals(result.last(), "Austria");
    }
}
