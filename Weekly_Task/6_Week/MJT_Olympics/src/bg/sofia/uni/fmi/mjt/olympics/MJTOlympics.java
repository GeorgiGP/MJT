package bg.sofia.uni.fmi.mjt.olympics;

import bg.sofia.uni.fmi.mjt.olympics.comparator.NationMedalComparator;
import bg.sofia.uni.fmi.mjt.olympics.competition.Competition;
import bg.sofia.uni.fmi.mjt.olympics.competition.CompetitionResultFetcher;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Medal;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MJTOlympics implements Olympics {

    private static final int TOP_N_COMPETITORS = 3;

    private final CompetitionResultFetcher competitionResultFetcher;
    private final Set<Competitor> registeredCompetitors;

    private final Map<String, EnumMap<Medal, Integer>> nationsMedalTable;

    public MJTOlympics(Set<Competitor> registeredCompetitors, CompetitionResultFetcher competitionResultFetcher) {
        this.competitionResultFetcher = competitionResultFetcher;
        this.registeredCompetitors = registeredCompetitors;
        this.nationsMedalTable = new HashMap<>();
    }

    @Override
    public void updateMedalStatistics(Competition competition) {
        validateCompetition(competition);

        TreeSet<Competitor> ranking = competitionResultFetcher.getResult(competition);

        for (Competitor competitor : ranking) {
            nationsMedalTable.putIfAbsent(competitor.getNationality(), new EnumMap<>(Medal.class));
        }
        int topN = Math.min(TOP_N_COMPETITORS, ranking.size());
        for (int i = 0; i < topN; i++) {
            Competitor competitor = ranking.pollFirst();
            if (competitor == null) {
                return;
            }
            Medal medal = Medal.values()[i];
            competitor.addMedal(medal);
            updateMedalTable(competitor, medal);
        }
    }

    /**
     * @throws IllegalArgumentException if the competition is null.
     * @throws IllegalArgumentException if a competitor is not registered in the Olympics.
     */
    private void validateCompetition(Competition competition) {
        if (competition == null) {
            throw new IllegalArgumentException("Competition cannot be null");
        }

        if (!registeredCompetitors.containsAll(competition.competitors())) {
            throw new IllegalArgumentException("Not all competitors are registered for the Olympics");
        }
    }

    private void updateMedalTable(Competitor competitor, Medal medal) {
        nationsMedalTable.putIfAbsent(competitor.getNationality(), new EnumMap<>(Medal.class));
        EnumMap<Medal, Integer> nationMedals = nationsMedalTable.get(competitor.getNationality());
        nationMedals.put(medal, nationMedals.getOrDefault(medal, 0) + 1);
        nationsMedalTable.put(competitor.getNationality(), nationMedals);
    }

    @Override
    public Set<Competitor> getRegisteredCompetitors() {
        return registeredCompetitors;
    }

    @Override
    public Map<String, EnumMap<Medal, Integer>> getNationsMedalTable() {
        return nationsMedalTable;
    }

    @Override
    public TreeSet<String> getNationsRankList() {
        TreeSet<String> nationsRankList = new TreeSet<>(new NationMedalComparator(this));
        nationsRankList.addAll(nationsMedalTable.keySet());
        return nationsRankList;
    }

    @Override
    public int getTotalMedals(String nationality) {
        validateNationality(nationality);

        EnumMap<Medal, Integer> nationMedals = nationsMedalTable.get(nationality);
        if (nationMedals == null || nationMedals.isEmpty()) {
            return 0;
        }

        int totalMedals = 0;
        for (int count : nationMedals.values()) {
            totalMedals += count;
        }

        return totalMedals;
    }

    /**
     * @throws IllegalArgumentException when nationality is null
     * @throws IllegalArgumentException when nationality is not registered in the olympics
     */
    private void validateNationality(String nationality) {
        if (nationality == null) {
            throw new IllegalArgumentException("The nationality cannot be null");
        }

        if (!nationsMedalTable.containsKey(nationality)) {
            throw new IllegalArgumentException("The nationality is not in the medal table");
        }
    }
}