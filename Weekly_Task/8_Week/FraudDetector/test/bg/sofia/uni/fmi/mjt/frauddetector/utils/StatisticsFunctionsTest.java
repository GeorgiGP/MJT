package bg.sofia.uni.fmi.mjt.frauddetector.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsFunctionsTest {
    @Test
    void testAvg() {
        List<Double> list = List.of(1.0, 2.0, 3.0, 4.0, 10.0);
        assertEquals(4.0, StatisticsFunctions.getAvgOfList(list));
    }
}
