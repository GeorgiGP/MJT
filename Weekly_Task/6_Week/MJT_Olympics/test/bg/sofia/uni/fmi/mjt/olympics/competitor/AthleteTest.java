package bg.sofia.uni.fmi.mjt.olympics.competitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AthleteTest {
    @Test
    void testGetMedalsChangingMedals() {
        Athlete athlete = new Athlete("random", "random", "random");
        assertThrows(UnsupportedOperationException.class, () -> athlete.getMedals().clear());
    }

    @Test
    void testAddMedalNullMedal() {
        Athlete athlete = new Athlete("random", "random", "random");

        assertThrows(IllegalArgumentException.class, () -> athlete.addMedal(null), "addMedal should throw IllegalArgumentException if medal as input is null");
    }

    @Test
    void testAddMedalWithSilverMedalInEmptySetJustOneMedal() {
        Athlete athlete = new Athlete("random", "random", "random");

        athlete.addMedal(Medal.SILVER);
        assertEquals(1, athlete.getMedalsMap().size(), "If the athlete doesn't have medals, after adding silver medal, he should contain just one medal!");
    }

    @Test
    void testAddMedalWithTwoGoldMedalInEmptySetTwoGoldMedals() {
        Athlete athlete = new Athlete("random", "random", "random");

        athlete.addMedal(Medal.GOLD);
        athlete.addMedal(Medal.GOLD);
        assertEquals(2, athlete.getMedalsMap().get(Medal.GOLD), "If the athlete doesn't have medals, after adding 2 gold medals, expected to contain 2 gold medals!");
    }

    @Test
    void testEqualsWithTwoAthletesSameNamesAndNationalityAndMedalsButDifferentId() {
        Athlete athlete = new Athlete("s321", "Georgi", "Bulgaria");
        Athlete athlete2 = new Athlete("s322", "Georgi", "Bulgaria");
        assertNotEquals(athlete, athlete2, "Two athletes must be different if they have different id");
    }

    @Test
    void testEqualsWithTheSecondAthleteIsNull() {
        Athlete athlete = new Athlete("s320", "Ivona", "Bulgaria");
        assertNotEquals(athlete, null, "If second athlete is null it should be different");
    }

    @Test
    void testEqualsWithTwoAthletesSameIdButDifferentNameOrNationality() {
        Athlete athlete = new Athlete("s322", "John", "England");
        Athlete athlete2 = new Athlete("s322", "Michael", "USA");
        assertEquals(athlete, athlete2, "Two athletes with same id are the same, no matter other factors");
    }

    @Test
    void testEqualsWithOneAthlete() {
        Athlete athlete = new Athlete("s320", "Ivona", "Bulgaria");
        assertEquals(athlete, athlete, "The athlete must be the same if the instance is the same");
    }

    @Test
    void testEqualsBothWaysWithAthletesWithSameMembers() {
        Athlete athlete = new Athlete("s320", "Georgi", "Bulgaria");
        Athlete athlete2 = new Athlete("s320", "Georgi", "Bulgaria");
        assertTrue(athlete.equals(athlete2) && athlete2.equals(athlete), "The athlete must be the same in both ways a1=a2 and a2=a1");
    }


    @Test
    public void testEqualsTransitive() {
        Athlete athlete = new Athlete("s320", "Ivona", "Bulgaria");
        Athlete athlete2 = new Athlete("s320", "Ivona", "Bulgaria");
        Athlete athlete3 = new Athlete("s320", "Ivona", "Bulgaria");
        assertTrue(athlete.equals(athlete2) && athlete2.equals(athlete3) && athlete.equals(athlete3));
    }

    @Test
    void testHashCodeDifferentIds() {
        Athlete athlete = new Athlete("s320", "Georgi", "Bulgaria");
        Athlete athlete2 = new Athlete("s321", "Georgi", "Bulgaria");
        assertNotEquals(athlete.hashCode(), athlete2.hashCode(), "Two athletes with different ids should have different hashcode");
    }

    @Test
    void testHashCodeConsistency() {
        Athlete athlete = new Athlete("s320", "Ivona", "Bulgaria");
        assertEquals(athlete.hashCode(), athlete.hashCode(), "The same athlete must have the same hashcode");
    }

    @Test
    void testHashCodeEqualIds() {
        Athlete athlete = new Athlete("s320", "Georgi", "Bulgaria");
        Athlete athlete2 = new Athlete("s320", "random", "random");
        assertEquals(athlete.hashCode(), athlete2.hashCode(), "Two athletes with the same id should have the same hashcode");
    }
}
