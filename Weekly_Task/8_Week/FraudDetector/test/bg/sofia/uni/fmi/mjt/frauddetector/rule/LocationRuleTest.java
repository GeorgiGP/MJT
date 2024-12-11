package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LocationRuleTest {
    private static List<Transaction> transactions;
    private static final String[] toRead = {
            "TX000001,AC00128,14.09,2023-04-11 16:29:14,San Diego,ATM",
            "TX000002,AC00128,376.24,2023-06-27 16:44:19,Houston,ATM",
            "TX000003,AC00128,126.29,2023-07-10 18:16:08,Mesa,Online",
            "TX000004,AC00128,1.29,2023-07-10 18:16:08,Houston,Online"
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
    void testLocationRuleViolation() {
        Rule r = new LocationsRule(3, 0.5);
        assertTrue(r.applicable(transactions));
    }

    @Test
    void testLocationRuleNotViolated() {
        Rule r = new LocationsRule(4, 0.5);
        assertFalse(r.applicable(transactions));
    }

    @Test
    void testLocationRuleNull() {
        Rule r = new LocationsRule(3, 0.5);
        assertThrows(IllegalArgumentException.class,() -> r.applicable(null),
                "Must not work with null transactions");
    }
}
