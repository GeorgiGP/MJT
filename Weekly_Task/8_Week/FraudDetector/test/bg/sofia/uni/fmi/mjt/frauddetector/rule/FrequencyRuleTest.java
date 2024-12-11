package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FrequencyRuleTest {
    private static List<Transaction> transactions;
    private static final String[] toRead = {
            "TX000001,AC00128,14.09,2023-09-11 16:30:01,San Diego,ATM",
            "TX000002,AC00128,376.24,2023-09-11 16:31:19,San Diego,ATM",
            "TX000003,AC00128,126.29,2023-09-11 16:31:08,San Diego,Online",
            "TX000004,AC00128,1.29,2023-07-10 18:39:08,Houston,Online"
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
    void testFrequencyRuleViolation() {
        Rule r = new FrequencyRule(3, Duration.of(120, ChronoUnit.SECONDS) ,0.5);
        assertTrue(r.applicable(transactions));
    }

    @Test
    void testFrequencyRuleValid() {
        Rule r = new FrequencyRule(2, Duration.of(10, ChronoUnit.SECONDS) ,0.5);
        assertFalse(r.applicable(transactions));
    }

    @Test
    void testFrequencyRuleNull() {
        Rule r = new FrequencyRule(20, Duration.of(1, ChronoUnit.SECONDS) , 0.5);
        assertThrows(IllegalArgumentException.class,() -> r.applicable(null),
                "Should throw IllegalArgumentException if transactions are null");
    }

    @Test
    void testFrequencyRuleNullTemporalAmount() {
        assertThrows(IllegalArgumentException.class,() -> new FrequencyRule(3, null, 0.5),
                "Should throw IllegalArgumentException if temporal amount is null");
    }
}
