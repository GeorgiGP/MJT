package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import bg.sofia.uni.fmi.mjt.frauddetector.utils.StatisticsFunctions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ZScoreRuleTest {
    private static List<Transaction> transactions;
    private static final String[] toRead = {
            "TX000001,AC00128,1.0,2023-09-11 16:30:01,San Diego,ATM",
            "TX000002,AC00128,2.0,2023-09-11 16:31:19,San Diego,ATM",
            "TX000003,AC00128,3.0,2023-09-11 16:31:08,San Diego,Online",
            "TX000004,AC00128,6.0,2023-07-10 18:39:08,Houston,Online"
    };

    @BeforeAll
    static void setUp() {
        transactions = new ArrayList<>();
        transactions.add(Transaction.of(toRead[0]));
        transactions.add(Transaction.of(toRead[1]));
        transactions.add(Transaction.of(toRead[2]));
        transactions.add(Transaction.of(toRead[3]));
    }

    @Test
    void testCalculation() {
        List<Double> list = List.of(1.0, 2.0, 3.0, 6.0);
        double avg = StatisticsFunctions.getAvgOfList(list);
        assertEquals(avg, 3.0);
        double standardDeviation = StatisticsFunctions.getStandardDeviation(list);
        assertEquals(standardDeviation, Math.sqrt(3.5));
        ZScoreRule r = new ZScoreRule(1, 0.5);
        double zScore = (6.0 - avg) / standardDeviation;
        assertEquals(zScore, r.getZScoreOf(6.0, list));
    }

    @Test
    void testZScoreRuleViolation() {
        Rule r = new ZScoreRule(1, 0.5);
        assertTrue(r.applicable(transactions)); //it is bigger than 1
    }

    @Test
    void testZScoreRuleValid() {
        Rule r = new ZScoreRule(2, 0.5);
        assertFalse(r.applicable(transactions)); //it is bigger less than 2
    }

    @Test
    void testZScoreRuleNull() {
        Rule r = new ZScoreRule(3, 0.5);
        assertThrows(IllegalArgumentException.class,() -> r.applicable(null),
                "Must not work with null transactions");
    }

}
