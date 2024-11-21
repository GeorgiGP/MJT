package bg.sofia.uni.fmi.mjt.olympics.comparator;

import bg.sofia.uni.fmi.mjt.olympics.MJTOlympics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NationMedalComparatorTest {
    MJTOlympics olympics;
    final String nation1 = "Bulgaria";
    final String nation2 = "Serbia";

    @BeforeEach
    void setUp() {
        olympics = mock(MJTOlympics.class);
    }

    @Test
    void testCompareTwoAndZeroMedals() {
        when(olympics.getTotalMedals(nation1)).thenReturn(2);
        when(olympics.getTotalMedals(nation2)).thenReturn(0);
        NationMedalComparator comparator = new NationMedalComparator(olympics);

        assertTrue(comparator.compare(nation1, nation2) < 0, "Nations with more medals should be first");
    }

    @Test
    void testCompareZeroAndTwoMedals() {
        when(olympics.getTotalMedals(nation1)).thenReturn(0);
        when(olympics.getTotalMedals(nation2)).thenReturn(2);
        NationMedalComparator comparator = new NationMedalComparator(olympics);

        assertTrue(comparator.compare(nation1, nation2) > 0, "Nations with more medals should be first");
    }

    @Test
    void testCompareWithSameCountMedalsButDifferentNationsAscending() {
        when(olympics.getTotalMedals(nation1)).thenReturn(3);
        when(olympics.getTotalMedals(nation2)).thenReturn(3);
        NationMedalComparator comparator = new NationMedalComparator(olympics);

        assertTrue(comparator.compare(nation1, nation2) < 0, "Nations with same medals should be ordered in alphabetical order");
    }

    @Test
    void testCompareWithSameCountMedalsButDifferentNationsDescending() {
        when(olympics.getTotalMedals(nation1)).thenReturn(3);
        when(olympics.getTotalMedals(nation2)).thenReturn(3);
        NationMedalComparator comparator = new NationMedalComparator(olympics);

        assertFalse(comparator.compare(nation2, nation1) < 0, "Nations with same medals should be ordered in alphabetical order");
    }
}
